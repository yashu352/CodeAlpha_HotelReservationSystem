
import java.util.*;
import java.io.*;

class Room {
    int roomNo;
    String category;
    double price;
    boolean available;

    Room(int roomNo, String category, double price) {
        this.roomNo = roomNo;
        this.category = category;
        this.price = price;
        this.available = true;
    }

    void display() {
        System.out.println(roomNo + "  " + category +
                "  Rs." + price +
                "  " + (available ? "Available" : "Booked"));
    }
}

class Booking {
    int bookingId;
    String customerName;
    int roomNo;
    int days;
    double amount;
    boolean paid;

    Booking(int bookingId, String customerName,
            int roomNo, int days, double amount) {
        this.bookingId = bookingId;
        this.customerName = customerName;
        this.roomNo = roomNo;
        this.days = days;
        this.amount = amount;
        this.paid = false;
    }

    void display() {
        System.out.println("\nBooking ID: " + bookingId);
        System.out.println("Customer: " + customerName);
        System.out.println("Room Number: " + roomNo);
        System.out.println("Days: " + days);
        System.out.println("Total Amount: Rs." + amount);
        System.out.println("Payment: " + (paid ? "Paid" : "Pending"));
    }
}

public class CodeAlpha_HotelReservationSystem {

    static ArrayList<Room> rooms = new ArrayList<>();
    static ArrayList<Booking> bookings = new ArrayList<>();

    static Scanner sc = new Scanner(System.in);

    static int bookingCounter = 1;

    static void saveBookings() {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter("bookings.txt"));

            for (Booking b : bookings) {
                pw.println(b.bookingId + "," +
                        b.customerName + "," +
                        b.roomNo + "," +
                        b.days + "," +
                        b.amount + "," +
                        b.paid);
            }

            pw.close();

        } catch (IOException e) {
            System.out.println("Error saving bookings.");
        }
    }

    static void loadBookings() {
        try {
            File file = new File("bookings.txt");

            if (!file.exists()) {
                return;
            }

            Scanner fileReader = new Scanner(file);

            while (fileReader.hasNextLine()) {
                String[] data = fileReader.nextLine().split(",");

                int id = Integer.parseInt(data[0]);
                String name = data[1];
                int roomNo = Integer.parseInt(data[2]);
                int days = Integer.parseInt(data[3]);
                double amount = Double.parseDouble(data[4]);
                boolean paid = Boolean.parseBoolean(data[5]);

                Booking b = new Booking(id, name, roomNo, days, amount);
                b.paid = paid;

                bookings.add(b);

                for (Room r : rooms) {
                    if (r.roomNo == roomNo) {
                        r.available = false;
                    }
                }

                bookingCounter = Math.max(bookingCounter, id + 1);
            }

            fileReader.close();

        } catch (Exception e) {
            System.out.println("Error loading bookings.");
        }
    }

    static void displayRooms() {
        System.out.println("\n--- HOTEL ROOMS ---");

        for (Room r : rooms) {
            r.display();
        }
    }

    static void searchRooms() {
        System.out.print("Enter category (Standard/Deluxe/Suite): ");
        String category = sc.next();

        boolean found = false;

        for (Room r : rooms) {
            if (r.category.equalsIgnoreCase(category) && r.available) {
                r.display();
                found = true;
            }
        }

        if (!found) {
            System.out.println("No available rooms found.");
        }
    }

    static void bookRoom() {
        displayRooms();

        System.out.print("\nEnter room number: ");
        int roomNo = sc.nextInt();

        Room selected = null;

        for (Room r : rooms) {
            if (r.roomNo == roomNo && r.available) {
                selected = r;
                break;
            }
        }

        if (selected == null) {
            System.out.println("Room not available!");
            return;
        }

        System.out.print("Enter customer name: ");
        String name = sc.next();

        System.out.print("Enter number of days: ");
        int days = sc.nextInt();

        if (days <= 0) {
            System.out.println("Invalid number of days.");
            return;
        }

        double amount = selected.price * days;

        Booking b = new Booking(
                bookingCounter++, name, roomNo, days, amount);

        bookings.add(b);
        selected.available = false;

        saveBookings();

        System.out.println("\nRoom booked successfully!");
        b.display();
    }

    static void cancelBooking() {
        System.out.print("Enter booking ID: ");
        int id = sc.nextInt();

        for (Booking b : bookings) {
            if (b.bookingId == id) {

                for (Room r : rooms) {
                    if (r.roomNo == b.roomNo) {
                        r.available = true;
                    }
                }

                bookings.remove(b);
                saveBookings();

                System.out.println("Booking cancelled successfully!");
                return;
            }
        }

        System.out.println("Booking not found.");
    }

    static void makePayment() {
        System.out.print("Enter booking ID: ");
        int id = sc.nextInt();

        for (Booking b : bookings) {
            if (b.bookingId == id) {

                if (b.paid) {
                    System.out.println("Payment already completed.");
                } else {
                    System.out.println("Amount to pay: Rs." + b.amount);
                    System.out.print("Confirm payment? (1=Yes, 0=No): ");

                    int confirm = sc.nextInt();

                    if (confirm == 1) {
                        b.paid = true;
                        saveBookings();
                        System.out.println("Payment successful!");
                    } else {
                        System.out.println("Payment cancelled.");
                    }
                }

                return;
            }
        }

        System.out.println("Booking not found.");
    }

    static void viewBookings() {
        System.out.println("\n--- BOOKING DETAILS ---");

        if (bookings.isEmpty()) {
            System.out.println("No bookings available.");
            return;
        }

        for (Booking b : bookings) {
            b.display();
        }
    }

    public static void main(String[] args) {

        rooms.add(new Room(101, "Standard", 1500));
        rooms.add(new Room(102, "Standard", 1500));
        rooms.add(new Room(201, "Deluxe", 2500));
        rooms.add(new Room(202, "Deluxe", 2500));
        rooms.add(new Room(301, "Suite", 4000));
        rooms.add(new Room(302, "Suite", 4000));

        loadBookings();

        int choice;

        do {
            System.out.println("\n===== HOTEL RESERVATION SYSTEM =====");
            System.out.println("1. Display Rooms");
            System.out.println("2. Search Rooms");
            System.out.println("3. Book Room");
            System.out.println("4. Cancel Booking");
            System.out.println("5. Make Payment");
            System.out.println("6. View Bookings");
            System.out.println("7. Exit");

            System.out.print("Enter your choice: ");
            choice = sc.nextInt();

            switch (choice) {

                case 1:
                    displayRooms();
                    break;

                case 2:
                    searchRooms();
                    break;

                case 3:
                    bookRoom();
                    break;

                case 4:
                    cancelBooking();
                    break;

                case 5:
                    makePayment();
                    break;

                case 6:
                    viewBookings();
                    break;

                case 7:
                    saveBookings();
                    System.out.println("Thank you for visiting!");
                    break;

                default:
                    System.out.println("Invalid choice!");
            }

        } while (choice != 7);

        sc.close();
    }
}
