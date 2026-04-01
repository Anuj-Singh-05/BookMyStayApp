import java.io.*;
import java.util.*;

/**
 * ============================================================
 * MASTER CLASS: BookMyStayApp 
 * ============================================================
 * FINAL COMPLETION: UC1 to UC12
 * Features: Multi-threading, Stack Rollback, Add-on Maps, 
 * Queue Allocation, and File-based Persistence.
 */

// UC9: Custom Exception
class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) { super(message); }
}

// UC2, UC4 & UC12: Room Models (Must be Serializable for UC12)
abstract class Room implements Serializable {
    protected String category;
    protected double price;
    public Room(String category, double price) { this.category = category; this.price = price; }
}
class SingleRoom extends Room { public SingleRoom() { super("Standard", 1500.0); } }

// UC3, UC11 & UC12: Thread-Safe & Persistable Inventory
class RoomInventory implements Serializable {
    private static final long serialVersionUID = 1L;
    private Map<String, Integer> roomAvailability = new HashMap<>();

    public RoomInventory() {
        roomAvailability.put("Single", 5);
        roomAvailability.put("Double", 3);
        roomAvailability.put("Suite", 2);
    }

    public synchronized boolean reserveRoom(String type) {
        int count = roomAvailability.getOrDefault(type, 0);
        if (count > 0) {
            roomAvailability.put(type, count - 1);
            return true;
        }
        return false;
    }

    public synchronized void releaseRoom(String type) {
        roomAvailability.put(type, roomAvailability.getOrDefault(type, 0) + 1);
    }

    public synchronized int getCount(String type) {
        return roomAvailability.getOrDefault(type, 0);
    }
}

// UC5, UC7, UC8 & UC12: Persistable Reservation
class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;
    private String guestName;
    private String roomType;
    private String resId;
    private List<String> addOns = new ArrayList<>(); // UC7

    public Reservation(String guestName, String roomType, String resId) {
        this.guestName = guestName;
        this.roomType = roomType;
        this.resId = resId;
    }
    public void addService(String s) { addOns.add(s); }
    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
    public String getResId() { return resId; }
    @Override
    public String toString() { return guestName + " (" + resId + ") [" + roomType + "] Add-ons: " + addOns; }
}

// UC8, UC10 & UC12: Persistable Booking Manager
class BookingManager implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<Reservation> history = new ArrayList<>();
    // UC10: Stack for LIFO Rollback Tracking
    private transient Stack<String> rollbackStack = new Stack<>(); 

    public synchronized void recordBooking(Reservation res) { 
        history.add(res); 
    }

    public synchronized void cancelBooking(String resId, String type, RoomInventory inv) {
        rollbackStack.push(resId); // Field is now used here
        inv.releaseRoom(type);
        System.out.println("UC10 Rollback: Room released for ID " + resId);
    }

    // Updated UC10: Displaying logs without the "Unchecked cast" warning
    public void displayRollbackLogs() {
        System.out.println("\n--- Recently Released Room IDs (Stack LIFO) ---");
        if (rollbackStack.isEmpty()) {
            System.out.println("No recent rollbacks.");
        } else {
            // Modern approach: Create a new stack by passing the old one 
            // This is type-safe and avoids the .clone() cast warning
            Stack<String> tempStack = new Stack<>();
            tempStack.addAll(rollbackStack);
            
            while (!tempStack.isEmpty()) {
                System.out.println("Returned to Pool: " + tempStack.pop());
            }
        }
    }

    public List<Reservation> getHistory() { return history; }
}

// UC12: Persistence Service (The File Handler)
class PersistenceService {
    private static final String FILE_NAME = "hotel_data.ser";

    public static void saveState(RoomInventory inv, BookingManager manager) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(inv);
            oos.writeObject(manager);
            System.out.println("UC12: System state successfully persisted to " + FILE_NAME);
        } catch (IOException e) {
            System.out.println("UC12 Error: Could not save data. " + e.getMessage());
        }
    }

    public static Object[] loadState() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            RoomInventory inv = (RoomInventory) ois.readObject();
            BookingManager manager = (BookingManager) ois.readObject();
            System.out.println("UC12: System state recovered from persistent storage.");
            return new Object[]{inv, manager};
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("UC12: No previous state found. Starting fresh.");
            return null;
        }
    }
}

// UC11: Concurrent Thread
class BookingTask extends Thread {
    private Reservation res;
    private RoomInventory inv;
    private BookingManager manager;

    public BookingTask(Reservation res, RoomInventory inv, BookingManager manager) {
        this.res = res; this.inv = inv; this.manager = manager;
    }

    @Override
    public void run() {
        if (inv.reserveRoom(res.getRoomType())) {
            manager.recordBooking(res);
            System.out.println("[THREAD] Confirmed: " + res.getGuestName());
        }
    }
}

// --- MAIN APPLICATION ---
public class BookMyStayApp {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("========================================");
        System.out.println("   Book My Stay - FINAL COMPLETE BUILD  ");
        System.out.println("========================================\n");

        // UC12: Attempt Recovery
        RoomInventory inventory;
        BookingManager manager;
        Object[] recovered = PersistenceService.loadState();

        if (recovered != null) {
            inventory = (RoomInventory) recovered[0];
            manager = (BookingManager) recovered[1];
        } else {
            inventory = new RoomInventory();
            manager = new BookingManager();
        }

        // Show current state after loading
        System.out.println("Current Single Rooms available: " + inventory.getCount("Single"));
        System.out.println("Previous Bookings: " + manager.getHistory().size());

        // UC11: Simulate some concurrent activity
        System.out.println("\n--- Processing New Concurrent Bookings ---");
        List<Thread> threads = new ArrayList<>();
        for (int i = 1; i <= 2; i++) {
            Reservation r = new Reservation("NewGuest-" + i, "Single", "ID-" + System.currentTimeMillis());
            r.addService("Wifi"); // UC7
            Thread t = new BookingTask(r, inventory, manager);
            threads.add(t);
            t.start();
        }
        for (Thread t : threads) t.join();

        // UC8: Display History
        System.out.println("\n--- UC8: Full Booking History ---");
        manager.getHistory().forEach(System.out::println);

        // UC12: Shutdown and Persist
        System.out.println("\n--- UC12: Shutting Down & Saving ---");
        PersistenceService.saveState(inventory, manager);
        System.out.println("Final Single Room Count: " + inventory.getCount("Single"));
    }
}