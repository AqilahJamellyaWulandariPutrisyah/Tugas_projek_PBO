package model;

public class Buying {
    private int id, buyerId, sellerId, concertId, ticketsBought;
    private double pricePerTicket;

    public Buying(int id, int buyerId, int sellerId, int concertId, int ticketsBought, double pricePerTicket) {
        this.id = id;
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.concertId = concertId;
        this.ticketsBought = ticketsBought;
        this.pricePerTicket = pricePerTicket;
    }
    // getters...
    public int getId() { return id; }
    public int getBuyerId() { return buyerId; }
    public int getSellerId() { return sellerId; }
    public int getConcertId() { return concertId; }
    public int getTicketsBought() { return ticketsBought; }
    public double getPricePerTicket() { return pricePerTicket; }
}