import java.util.*;

/**
 * ============================================================
 * MAIN CLASS: BookMyStayApp 
 * ============================================================
 * Incremental Build: UC1 - UC8 (Booking History & Reporting)
 * @version 8.0
 */

// --- UC2: Room Models ---
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

// --- UC3: Inventory ---
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

// --- UC5: Reservation Model ---
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

// --- UC8: Booking History (The "Storage") ---
class BookingHistory {
    private List<Reservation> confirmedReservations = new ArrayList<>();

    public void addReservation(Reservation reservation) {
        confirmedReservations.add(reservation);
    }

    public List<Reservation> getConfirmedReservations() {
        return confirmedReservations;
    }
}

// --- UC8: Booking Report Service (The "Reporter") ---
class BookingReportService {
    public void generateReport(BookingHistory history) {
        System.out.println("\n--- Booking History Report ---");
        List<Reservation> bookings = history.getConfirmedReservations();
        
        if (bookings.isEmpty()) {
            System.out.println("No confirmed bookings found.");
        } else {
            for (Reservation res : bookings) {
                System.out.println("Guest: " + res.getGuestName() + ", Room Type: " + res.getRoomType());
            }
        }
        System.out.println("------------------------------");
    }
}

// --- UC6: Allocation Service ---
class RoomAllocationService {
    public void processAllocations(Queue<Reservation> queue, RoomInventory inventory, BookingHistory history) {
        System.out.println("--- Processing Allocations ---");
        while (!queue.isEmpty()) {
            Reservation req = queue.poll();
            String type = req.getRoomType();
            int available = inventory.getRoomAvailability().getOrDefault(type, 0);

            if (available > 0) {
                inventory.updateAvailability(type, available - 1);
                // UC8: Save the confirmed booking to history
                history.addReservation(req);
                System.out.println("Confirmed: " + req.getGuestName() + " assigned to " + type);
            } else {
                System.out.println("Failed: No rooms available for " + req.getGuestName());
            }
        }
    }
}

// --- MAIN APPLICATION ---
public class BookMyStayApp {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   Book My Stay App - Use Case 8");
        System.out.println("========================================\n");

        // Initialize Services
        RoomInventory inventory = new RoomInventory();
        BookingHistory history = new BookingHistory();
        BookingReportService reportService = new BookingReportService();
        RoomAllocationService allocationService = new RoomAllocationService();
        
        // Simulating the Booking Queue (UC5 logic used directly here)
        Queue<Reservation> requestQueue = new LinkedList<>();
        requestQueue.add(new Reservation("Abhi", "Single Room"));
        requestQueue.add(new Reservation("Subha", "Double Room"));
        requestQueue.add(new Reservation("Vanmathi", "Suite Room"));

        // 1. Process Allocations and automatically save to History
        allocationService.processAllocations(requestQueue, inventory, history);

        // 2. Admin requests the Report (UC8)
        reportService.generateReport(history);
    }
}