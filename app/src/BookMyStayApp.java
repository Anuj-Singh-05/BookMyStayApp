import java.util.HashMap;
import java.util.Map;

/**
 * ============================================================
 * MAIN CLASS: BookMyStayApp 
 * ============================================================
 * Incremental Build: UC1 + UC2 + UC3 + UC4 (Room Search)
 * @version 4.1
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
}

// --- UC4: Room Search Service (Read-Only) ---
class RoomSearchService {
    public void searchAvailableRooms(RoomInventory inventory, Room single, Room doubleRoom, Room suite) {
        Map<String, Integer> availability = inventory.getRoomAvailability();

        // Check and display only if availability > 0
        if (availability.getOrDefault("Single Room", 0) > 0) {
            System.out.println("Single Room:");
            single.displayRoomDetails();
            System.out.println("Available: " + availability.get("Single Room") + "\n");
        }

        if (availability.getOrDefault("Double Room", 0) > 0) {
            System.out.println("Double Room:");
            doubleRoom.displayRoomDetails();
            System.out.println("Available: " + availability.get("Double Room") + "\n");
        }

        if (availability.getOrDefault("Suite Room", 0) > 0) {
            System.out.println("Suite Room:");
            suite.displayRoomDetails();
            System.out.println("Available: " + availability.get("Suite Room") + "\n");
        }
    }
}

// --- Main Application ---
public class BookMyStayApp {
    public static void main(String[] args) {
        // UC1: Welcome Header
        System.out.println("========================================");
        System.out.println("   Welcome to Book My Stay App");
        System.out.println("   Version: 4.1");
        System.out.println("========================================\n");

        // Initialize Objects
        RoomInventory inventory = new RoomInventory();
        RoomSearchService searchService = new RoomSearchService();
        
        Room single = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suite = new SuiteRoom();

        // UC4: Room Search Execution
        System.out.println("Room Search\n");
        searchService.searchAvailableRooms(inventory, single, doubleRoom, suite);
    }
}