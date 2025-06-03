package model;

public class Concert {
    private int id;
    private String name, date, location;
    private double price;
    private int ticketsAvailable;

    // Constructors, getters, setters
    public Concert(int id, String name, String date, String location, double price, int ticketsAvailable) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.location = location;
        this.price = price;
        this.ticketsAvailable = ticketsAvailable;
    }
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDate() { return date; }
    public String getLocation() { return location; }
    public double getPrice() { return price; }
    public int getTicketsAvailable() { return ticketsAvailable; }
}