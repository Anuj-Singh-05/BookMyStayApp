/**
 * ============================================================
 * MAIN CLASS: BookMyStayApp
 * ============================================================
 * Objective: Hotel Booking Management System
 * Incremental Build: UC1 (Welcome) + UC2 (Room Initialization)
 * * @author [Your Name/Reg No]
 * @version 2.1
 */

// ============================================================
// UC2: SUPPORTING CLASSES (Inheritance & Abstraction)
// Note: These are NOT public so they can stay in this one file.
// ============================================================

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

class SingleRoom extends Room {
    public SingleRoom() { super(1, 250, 1500.0); }
}

class DoubleRoom extends Room {
    public DoubleRoom() { super(2, 400, 2500.0); }
}

class SuiteRoom extends Room {
    public SuiteRoom() { super(3, 750, 5000.0); }
}

// ============================================================
// MAIN APPLICATION CLASS
// ============================================================
public class BookMyStayApp {

    public static void main(String[] args) {

        // ------------------------------------------------------------
        // USE CASE 1: Application Entry & Welcome Message
        // ------------------------------------------------------------
        System.out.println("========================================");
        System.out.println("   Welcome to Book My Stay App");
        System.out.println("   Hotel Booking Management System");
        System.out.println("   Version: 2.1");
        System.out.println("========================================");
        System.out.println("System initialized successfully.\n");


        // ------------------------------------------------------------
        // USE CASE 2: Basic Room Types & Static Availability
        // ------------------------------------------------------------
        System.out.println("Hotel Room Initialization\n");

        // 1. Initialize Room Objects (Polymorphism)
        Room single = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suite = new SuiteRoom();

        // 2. Static Availability Variables
        int singleAvail = 5;
        int doubleAvail = 3;
        int suiteAvail = 2;

        // 3. Display Room Details and Availability
        System.out.println("Single Room:");
        single.displayRoomDetails();
        System.out.println("Available: " + singleAvail + "\n");

        System.out.println("Double Room:");
        doubleRoom.displayRoomDetails();
        System.out.println("Available: " + doubleAvail + "\n");

        System.out.println("Suite Room:");
        suite.displayRoomDetails();
        System.out.println("Available: " + suiteAvail);
    }
}