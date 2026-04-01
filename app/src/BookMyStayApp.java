import java.util.*;

/**
 * ============================================================
 * MASTER CLASS: BookMyStayApp 
 * ============================================================
 * FULL INTEGRATION: UC1 to UC11
 * Includes: Inheritance, Inventory, Categories, Queues, 
 * Add-Ons, History, Validation, Stack Rollback, and Thread Safety.
 */

// --- UC9: Custom Exception ---
class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) { super(message); }
}

// --- UC2 & UC4: Room Models & Categories ---
abstract class Room {
    protected String category; // UC4
    protected double price;
    public Room(String category, double price) { this.category = category; this.price = price; }
    public String getCategory() { return category; }
}
class SingleRoom extends Room { public SingleRoom() { super("Standard", 1500.0); } }
class DoubleRoom extends Room { public DoubleRoom() { super("Deluxe", 2500.0); } }
class SuiteRoom extends Room { public SuiteRoom() { super("Luxury", 5000.0); } }

// --- UC3 & UC11: Thread-Safe Inventory ---
class RoomInventory {
    private Map<String, Integer> roomAvailability = new HashMap<>();

    public RoomInventory() {
        roomAvailability.put("Single", 5);
        roomAvailability.put("Double", 3);
        roomAvailability.put("Suite", 2);
    }

    // UC11: Synchronized to prevent double-booking in multi-threaded environment
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

// --- UC7: Add-On Service Registry (Map) ---
class ServiceRegistry {
    private Map<String, Double> services = new HashMap<>();
    public ServiceRegistry() {
        services.put("Wifi", 200.0);
        services.put("Breakfast", 500.0);
    }
    public Map<String, Double> getServices() { return services; }
}

// --- UC5, UC7 & UC8: Reservation Model ---
class Reservation {
    private String guestName;
    private String roomType;
    private String resId;
    private List<String> selectedAddOns = new ArrayList<>(); // UC7 List

    public Reservation(String guestName, String roomType, String resId) {
        this.guestName = guestName;
        this.roomType = roomType;
        this.resId = resId;
    }

    public void addService(String service) { this.selectedAddOns.add(service); }
    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
    public String getResId() { return resId; }
    public List<String> getAddOns() { return selectedAddOns; }
}

// --- UC8 & UC10: History and Rollback (Stack & List) ---
class BookingManager {
    private List<Reservation> history = new ArrayList<>(); // UC8
    private Stack<String> rollbackStack = new Stack<>(); // UC10

    public synchronized void recordBooking(Reservation res) {
        history.add(res);
    }

    public synchronized void cancelBooking(String resId, String type, RoomInventory inv) {
        rollbackStack.push(resId);
        inv.releaseRoom(type);
        System.out.println("UC10 Rollback: Room released for ID " + resId);
    }

    public List<Reservation> getHistory() { return history; }
}

// --- UC11: Concurrent Booking Thread ---
class BookingTask extends Thread {
    private Reservation res;
    private RoomInventory inv;
    private BookingManager manager;

    public BookingTask(Reservation res, RoomInventory inv, BookingManager manager) {
        this.res = res;
        this.inv = inv;
        this.manager = manager;
    }

    @Override
    public void run() {
        // UC11: Critical Section
        if (inv.reserveRoom(res.getRoomType())) {
            manager.recordBooking(res);
            System.out.println("[CONFIRMED] " + res.getGuestName() + " secured a " + res.getRoomType());
        } else {
            System.out.println("[FAILED] No rooms left for " + res.getGuestName());
        }
    }
}

// --- UC9: Validator ---
class ReservationValidator {
    public void validate(String name, String type, RoomInventory inv) throws InvalidBookingException {
        if (name == null || name.isEmpty()) throw new InvalidBookingException("Invalid Name");
        if (!inv.getCount(type).equals(null) && inv.getCount(type) < 0) throw new InvalidBookingException("Invalid Type");
    }
}

// --- MAIN APPLICATION ---
public class BookMyStayApp {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== BOOK MY STAY: FULL UC1-UC11 INTEGRATION ===\n");

        RoomInventory inventory = new RoomInventory();
        BookingManager manager = new BookingManager();
        ServiceRegistry serviceMap = new ServiceRegistry();

        // UC5/UC11: Prepare multiple concurrent requests
        List<Reservation> requests = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            Reservation r = new Reservation("Guest-" + i, "Single", "RES-00" + i);
            r.addService("Wifi"); // UC7
            requests.add(r);
        }

        // UC11: Simulate Concurrent Access
        List<Thread> threads = new ArrayList<>();
        for (Reservation r : requests) {
            Thread t = new BookingTask(r, inventory, manager);
            threads.add(t);
            t.start();
        }

        for (Thread t : threads) t.join();

        // UC8: Show History
        System.out.println("\n--- UC8: Booking History ---");
        manager.getHistory().forEach(r -> System.out.println(r.getGuestName() + " booked " + r.getRoomType() + " with " + r.getAddOns()));

        // UC10: Test a Cancellation
        System.out.println("\n--- UC10: Testing Cancellation ---");
        manager.cancelBooking("RES-001", "Single", inventory);
        
        System.out.println("\nFinal Inventory: " + inventory.getCount("Single"));
    }
}