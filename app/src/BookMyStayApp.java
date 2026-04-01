import java.util.*;

/**
 * ============================================================
 * MAIN CLASS: BookMyStayApp 
 * ============================================================
 * Incremental Build: UC1 - UC6 (Final Allocation & Set Uniqueness)
 * @version 6.1
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

    public void displayRoomDetails() {
        System.out.println("Beds: " + numberOfBeds);
        System.out.println("Size: " + squareFeet + " sqft");
        System.out.println("Price per night: " + pricePerNight);
    }
}

class SingleRoom extends Room { public SingleRoom() { super(1, 250, 1500.0); } }
class DoubleRoom extends Room { public DoubleRoom() { super(2, 400, 2500.0); } }
class SuiteRoom extends Room { public SuiteRoom() { super(3, 750, 5000.0); } }

// --- UC3: Centralized Room Inventory ---
class RoomInventory {
    private Map<String, Integer> roomAvailability;

    public RoomInventory() {
        roomAvailability = new HashMap<>();
        initializeInventory();
    }

    private void initializeInventory() {
        roomAvailability.put("Single Room", 5);
        roomAvailability.put("Double Room", 3);
        roomAvailability.put("Suite Room", 2);
    }

    public Map<String, Integer> getRoomAvailability() {
        return roomAvailability;
    }

    public void updateAvailability(String roomType, int newCount) {
        roomAvailability.put(roomType, newCount);
    }
}

// --- UC5: Reservation Model & Booking Queue ---
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

// --- UC6: Room Allocation Service ---
class RoomAllocationService {
    // Maps Room Type to a Set of assigned IDs to prevent double-booking
    private Map<String, Set<String>> allocatedRooms = new HashMap<>();

    public RoomAllocationService() {
        allocatedRooms.put("Single Room", new HashSet<>());
        allocatedRooms.put("Double Room", new HashSet<>());
        allocatedRooms.put("Suite Room", new HashSet<>());
    }

    public void processAllocations(BookingRequestQueue queue, RoomInventory inventory) {
        Queue<Reservation> requests = queue.getRequestQueue();

        System.out.println("Processing Room Allocations...\n");

        while (!requests.isEmpty()) {
            Reservation request = requests.poll(); // FIFO: Dequeue the first person
            String type = request.getRoomType();
            int currentCount = inventory.getRoomAvailability().get(type);

            if (currentCount > 0) {
                // Generate a unique Room ID (e.g., SR-105)
                String roomId = generateRoomId(type, currentCount);
                
                // Record the allocation in the Set
                allocatedRooms.get(type).add(roomId);
                
                // Update Inventory
                inventory.updateAvailability(type, currentCount - 1);

                System.out.println("Confirmed: " + request.getGuestName() + 
                                   " assigned to " + type + " (ID: " + roomId + ")");
            } else {
                System.out.println("Failed: No availability for " + request.getGuestName() + " (" + type + ")");
            }
        }
    }

    private String generateRoomId(String type, int count) {
        String prefix = type.substring(0, 1).toUpperCase();
        return prefix + "R-" + (100 + count);
    }
}

// --- Main Application ---
public class BookMyStayApp {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   Welcome to Book My Stay App v6.1");
        System.out.println("========================================\n");

        // 1. Setup Services
        RoomInventory inventory = new RoomInventory();
        BookingRequestQueue queue = new BookingRequestQueue();
        RoomAllocationService allocationService = new RoomAllocationService();

        // 2. Simulate Requests (UC5)
        queue.addRequest(new Reservation("Alice", "Single Room"));
        queue.addRequest(new Reservation("Bob", "Double Room"));
        queue.addRequest(new Reservation("Charlie", "Suite Room"));
        queue.addRequest(new Reservation("David", "Single Room"));

        System.out.println();

        // 3. Process Allocations (UC6)
        allocationService.processAllocations(queue, inventory);

        // 4. Final Inventory Check
        System.out.println("\nFinal Inventory Status:");
        System.out.println(inventory.getRoomAvailability());
    }
}