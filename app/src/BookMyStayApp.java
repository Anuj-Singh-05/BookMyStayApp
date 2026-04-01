import java.util.*;

/**
 * ============================================================
 * MAIN CLASS: BookMyStayApp 
 * ============================================================
 * Final Unified Build: UC1 through UC10
 * Includes: UC7 Add-On Services (Map & List)
 */

// --- UC9: Custom Exception ---
class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) { super(message); }
}

// --- UC2 & UC4: Room Models & Categories ---
abstract class Room {
    protected String category;
    protected double price;
    public Room(String category, double price) { this.category = category; this.price = price; }
}
class SingleRoom extends Room { public SingleRoom() { super("Standard", 1500.0); } }
class DoubleRoom extends Room { public DoubleRoom() { super("Deluxe", 2500.0); } }
class SuiteRoom extends Room { public SuiteRoom() { super("Luxury", 5000.0); } }

// --- UC3: Inventory ---
class RoomInventory {
    private Map<String, Integer> roomAvailability = new HashMap<>();
    public RoomInventory() {
        roomAvailability.put("Single", 5);
        roomAvailability.put("Double", 3);
        roomAvailability.put("Suite", 2);
    }
    public Map<String, Integer> getRoomAvailability() { return roomAvailability; }
    public void updateAvailability(String type, int count) { roomAvailability.put(type, count); }
}

// --- UC7: Add-On Service Registry ---
class ServiceRegistry {
    // Map to store available services and their prices
    private Map<String, Double> availableServices = new HashMap<>();

    public ServiceRegistry() {
        availableServices.put("Wifi", 200.0);
        availableServices.put("Breakfast", 500.0);
        availableServices.put("Gym", 300.0);
    }

    public Map<String, Double> getAvailableServices() { return availableServices; }
}

// --- UC5 & UC8: Reservation Model ---
class Reservation {
    private String guestName;
    private String roomType;
    private String resId;
    // UC7: List to store selected services for this specific guest
    private List<String> selectedServices = new ArrayList<>();

    public Reservation(String guestName, String roomType, String resId) {
        this.guestName = guestName;
        this.roomType = roomType;
        this.resId = resId;
    }

    public void addService(String serviceName) { this.selectedServices.add(serviceName); }
    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
    public String getResId() { return resId; }
    public List<String> getSelectedServices() { return selectedServices; }
}

// --- UC10: Cancellation Service (Stack-based Rollback) ---
class CancellationService {
    private Stack<String> releasedRoomIds = new Stack<>();
    private Map<String, String> reservationToTypeMap = new HashMap<>();

    public void registerBooking(String resId, String type) {
        reservationToTypeMap.put(resId, type);
    }

    public void cancelBooking(String resId, RoomInventory inventory) throws InvalidBookingException {
        if (!reservationToTypeMap.containsKey(resId)) {
            throw new InvalidBookingException("ID not found.");
        }
        String type = reservationToTypeMap.get(resId);
        inventory.updateAvailability(type, inventory.getRoomAvailability().get(type) + 1);
        releasedRoomIds.push(resId);
        reservationToTypeMap.remove(resId);
        System.out.println("UC10: Booking " + resId + " cancelled. Inventory rolled back.");
    }
}

// --- UC6: Allocation Service ---
class RoomAllocationService {
    public void processQueue(Queue<Reservation> queue, RoomInventory inventory, CancellationService cancelService) {
        System.out.println("\n--- UC6: Processing Allocations ---");
        while (!queue.isEmpty()) {
            Reservation res = queue.poll();
            String type = res.getRoomType();
            int available = inventory.getRoomAvailability().get(type);

            if (available > 0) {
                inventory.updateAvailability(type, available - 1);
                cancelService.registerBooking(res.getResId(), type);
                System.out.println("Confirmed: " + res.getGuestName() + " -> " + type);
                
                // UC7: Display selected services during confirmation
                if (!res.getSelectedServices().isEmpty()) {
                    System.out.println("   Add-ons: " + res.getSelectedServices());
                }
            } else {
                System.out.println("Failed: No " + type + " available for " + res.getGuestName());
            }
        }
    }
}

// --- UC9: Validator ---
class ReservationValidator {
    public void validate(String name, String type, RoomInventory inv) throws InvalidBookingException {
        if (name == null || name.trim().isEmpty()) throw new InvalidBookingException("Name is empty.");
        if (!inv.getRoomAvailability().containsKey(type)) throw new InvalidBookingException("Invalid Room Type.");
    }
}

// --- MAIN APPLICATION ---
public class BookMyStayApp {
    public static void main(String[] args) {
        System.out.println("=== Book My Stay App: UC1 - UC10 Complete ===\n");

        RoomInventory inventory = new RoomInventory();
        ServiceRegistry serviceRegistry = new ServiceRegistry();
        RoomAllocationService allocationService = new RoomAllocationService();
        CancellationService cancelService = new CancellationService();
        ReservationValidator validator = new ReservationValidator();
        
        Queue<Reservation> bookingQueue = new LinkedList<>();

        try {
            // UC9: Validation
            String guest = "Abhisheak";
            String type = "Single";
            validator.validate(guest, type, inventory);
            
            // UC7: Add-On Selection
            Reservation res1 = new Reservation(guest, type, "RES-101");
            System.out.println("UC7: Available Services: " + serviceRegistry.getAvailableServices().keySet());
            res1.addService("Wifi"); // Adding from the available map
            res1.addService("Breakfast");
            
            bookingQueue.add(res1);

            // UC6: Process Allocation
            allocationService.processQueue(bookingQueue, inventory, cancelService);

            // UC10: Rollback/Cancellation
            System.out.println("\n--- Testing UC10 Cancellation ---");
            cancelService.cancelBooking("RES-101", inventory);

        } catch (InvalidBookingException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}