import java.util.*;

/**
 * ============================================================
 * MAIN CLASS: BookMyStayApp 
 * ============================================================
 * Unified Build: UC1 through UC9
 * Includes: Inventory (UC3), Categories (UC4), Queues (UC5), 
 * Allocation (UC6), Add-Ons (UC7), History (UC8), & Validation (UC9)
 */

// --- UC9: Custom Exception ---
class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) { super(message); }
}

// --- UC2 & UC4: Room Models & Categories ---
abstract class Room {
    protected String category; // UC4
    protected double pricePerNight;
    public Room(String category, double price) { 
        this.category = category; 
        this.pricePerNight = price; 
    }
    public String getCategory() { return category; }
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

// --- UC5 & UC8: Reservation & History ---
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

class BookingHistory {
    private List<Reservation> history = new ArrayList<>();
    public void add(Reservation res) { history.add(res); }
    public List<Reservation> getAll() { return history; }
}

// --- UC7: Add-On Services ---
class AddOnService {
    private String name;
    private double cost;
    public AddOnService(String name, double cost) { this.name = name; this.cost = cost; }
    public String getName() { return name; }
    public double getCost() { return cost; }
}

// --- UC9: Validator ---
class ReservationValidator {
    public void validate(String name, String type, RoomInventory inv) throws InvalidBookingException {
        if (name == null || name.trim().isEmpty()) throw new InvalidBookingException("Name is required.");
        if (!inv.getRoomAvailability().containsKey(type)) throw new InvalidBookingException("Invalid room type selected.");
        if (inv.getRoomAvailability().get(type) <= 0) throw new InvalidBookingException("Room type out of stock.");
    }
}

// --- MAIN APPLICATION ---
public class BookMyStayApp {
    public static void main(String[] args) {
        System.out.println("--- Hotel System: UC1-UC9 Integrated ---");
        
        Scanner scanner = new Scanner(System.in);
        RoomInventory inventory = new RoomInventory();
        ReservationValidator validator = new ReservationValidator();
        BookingHistory history = new BookingHistory();
        
        try {
            System.out.print("Enter Guest Name: ");
            String name = scanner.nextLine();
            System.out.print("Enter Room Type (Single/Double/Suite): ");
            String type = scanner.nextLine();

            // UC9: Validation
            validator.validate(name, type, inventory);

            // UC6: Allocation (Simplified for single input)
            int current = inventory.getRoomAvailability().get(type);
            inventory.updateAvailability(type, current - 1);
            Reservation res = new Reservation(name, type);
            history.add(res);

            // UC7: Add-Ons (Optional logic)
            System.out.println("Booking successful for " + name + "!");
            System.out.println("Added to Booking History. Inventory updated.");

        } catch (InvalidBookingException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}