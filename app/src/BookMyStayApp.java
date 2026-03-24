import java.util.HashMap;
import java.util.Map;
import java.util.LinkedList;
import java.util.Queue;

/**
 * ============================================================
 * MAIN CLASS: BookMyStayApp 
 * ============================================================
 * Incremental Build: UC1 - UC5 (Booking Request Queue)
 * @version 5.1
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

// --- UC5: Reservation & Booking Queue ---
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
    private Queue<Reservation> requestQueue;

    public BookingRequestQueue() {
        // LinkedList implements the Queue interface in Java
        this.requestQueue = new LinkedList<>();
    }

    public void addRequest(Reservation reservation) {
        requestQueue.add(reservation);
        System.out.println("Booking request received for " + reservation.getGuestName() + 
                           " (" + reservation.getRoomType() + ")");
    }

    public Queue<Reservation> getRequestQueue() {
        return requestQueue;
    }
}

// --- Main Application ---
public class BookMyStayApp {
    public static void main(String[] args) {
        // Header
        System.out.println("========================================");
        System.out.println("   Welcome to Book My Stay App");
        System.out.println("   Version: 5.1");
        System.out.println("========================================\n");

        // Initialization
        RoomInventory inventory = new RoomInventory();
        BookingRequestQueue bookingQueue = new BookingRequestQueue();

        // UC5: Simulating Incoming Booking Requests
        System.out.println("Booking Request Intake\n");

        bookingQueue.addRequest(new Reservation("Alice", "Single Room"));
        bookingQueue.addRequest(new Reservation("Bob", "Double Room"));
        bookingQueue.addRequest(new Reservation("Charlie", "Suite Room"));

        System.out.println("\nAll requests stored in FIFO order.");
        System.out.println("Current Queue Size: " + bookingQueue.getRequestQueue().size());

        // Display the queue contents to verify order
        System.out.println("\nPending Requests in Queue:");
        for (Reservation res : bookingQueue.getRequestQueue()) {
            System.out.println("- " + res.getGuestName() + " wants a " + res.getRoomType());
        }
    }
}