import java.util.*;

/**
 * ============================================================
 * MAIN CLASS: BookMyStayApp 
 * ============================================================
 * Incremental Build: UC1 - UC7 (Add-On Service Selection)
 * @version 7.1
 */

// --- UC2: Room Domain Models ---
abstract class Room {
    protected int numberOfBeds;
    protected int squareFeet;
    protected double pricePerNight;

    public Room(int numberOfBeds, int squareFeet, double pricePerNight) {
        this.numberOfBeds = numberOfBeds;
        this.squareFeet = squareFeet;
        this.pricePerNight = pricePerNight;
    }
}

class SingleRoom extends Room { public SingleRoom() { super(1, 250, 1500.0); } }
class DoubleRoom extends Room { public DoubleRoom() { super(2, 400, 2500.0); } }
class SuiteRoom extends Room { public SuiteRoom() { super(3, 750, 5000.0); } }

// --- UC3: Centralized Room Inventory ---
class RoomInventory {
    private Map<String, Integer> roomAvailability = new HashMap<>();

    public RoomInventory() {
        roomAvailability.put("Single Room", 5);
        roomAvailability.put("Double Room", 3);
        roomAvailability.put("Suite Room", 2);
    }

    public Map<String, Integer> getRoomAvailability() { return roomAvailability; }
    public void updateAvailability(String type, int count) { roomAvailability.put(type, count); }
}

// --- UC5: Reservation & Queue ---
class Reservation {
    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }
    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
}

class BookingRequestQueue {
    private Queue<Reservation> requestQueue = new LinkedList<>();
    public void addRequest(Reservation res) { requestQueue.add(res); }
    public Queue<Reservation> getRequestQueue() { return requestQueue; }
}

// --- UC6: Allocation Service ---
class RoomAllocationService {
    public List<String> processAllocations(BookingRequestQueue queue, RoomInventory inventory) {
        List<String> confirmedRoomIds = new ArrayList<>();
        Queue<Reservation> requests = queue.getRequestQueue();

        System.out.println("--- Processing Allocations ---");
        while (!requests.isEmpty()) {
            Reservation req = requests.poll();
            String type = req.getRoomType();
            int available = inventory.getRoomAvailability().getOrDefault(type, 0);

            if (available > 0) {
                String roomId = type.substring(0, 1).toUpperCase() + "R-" + (100 + available);
                inventory.updateAvailability(type, available - 1);
                confirmedRoomIds.add(roomId);
                System.out.println("Confirmed: " + req.getGuestName() + " -> " + roomId);
            }
        }
        return confirmedRoomIds;
    }
}

// --- UC7: Add-On Service Selection ---
class AddOnService {
    private String serviceName;
    private double price;

    public AddOnService(String serviceName, double price) {
        this.serviceName = serviceName;
        this.price = price;
    }
    public String getServiceName() { return serviceName; }
    public double getPrice() { return price; }
}

class AddOnServiceManager {
    // Maps RoomID -> List of selected services (One-to-Many)
    private Map<String, List<AddOnService>> selectedServices = new HashMap<>();

    public void addServiceToReservation(String roomId, AddOnService service) {
        // computeIfAbsent creates a new list if one doesn't exist for this Room ID
        selectedServices.computeIfAbsent(roomId, k -> new ArrayList<>()).add(service);
        System.out.println("Service '" + service.getServiceName() + "' added to Room " + roomId);
    }

    public void displaySummary(String roomId) {
        List<AddOnService> services = selectedServices.get(roomId);
        if (services != null) {
            double total = 0;
            System.out.println("\nAdd-on Summary for " + roomId + ":");
            for (AddOnService s : services) {
                System.out.println("- " + s.getServiceName() + ": " + s.getPrice());
                total += s.getPrice();
            }
            System.out.println("Total Extra Cost: " + total);
        }
    }
}

// --- MAIN APPLICATION ---
public class BookMyStayApp {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   Book My Stay App - Use Case 7");
        System.out.println("========================================\n");

        // Initialize Services
        RoomInventory inventory = new RoomInventory();
        BookingRequestQueue queue = new BookingRequestQueue();
        RoomAllocationService allocationService = new RoomAllocationService();
        AddOnServiceManager addOnManager = new AddOnServiceManager();

        // 1. Add Requests (UC5)
        queue.addRequest(new Reservation("Alice", "Single Room"));
        queue.addRequest(new Reservation("Bob", "Double Room"));

        // 2. Process Allocations (UC6)
        List<String> roomIds = allocationService.processAllocations(queue, inventory);

        // 3. Select Add-Ons (UC7)
        if (!roomIds.isEmpty()) {
            String firstRoom = roomIds.get(0); // This is Alice's room
            
            AddOnService breakfast = new AddOnService("Breakfast Buffet", 200.0);
            AddOnService wifi = new AddOnService("High-Speed WiFi", 50.0);

            System.out.println("\nSelecting Services for Alice:");
            addOnManager.addServiceToReservation(firstRoom, breakfast);
            addOnManager.addServiceToReservation(firstRoom, wifi);

            // Show Summary
            addOnManager.displaySummary(firstRoom);
        }
    }
}