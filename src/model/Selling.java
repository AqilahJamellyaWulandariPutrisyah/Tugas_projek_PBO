package model;

public class Selling {
    private int id, userId, concertId, ticketsForSale;
    private double pricePerTicket;

    public Selling(int id, int userId, int concertId, int ticketsForSale, double pricePerTicket) {
        this.id = id;
        this.userId = userId;
        this.concertId = concertId;
        this.ticketsForSale = ticketsForSale;
        this.pricePerTicket = pricePerTicket;
    }
    // getters...
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public int getConcertId() { return concertId; }
    public int getTicketsForSale() { return ticketsForSale; }
    public double getPricePerTicket() { return pricePerTicket; }
}