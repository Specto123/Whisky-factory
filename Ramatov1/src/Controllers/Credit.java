package Controllers;

import java.sql.Date;

public class Credit {
    private int ID;
    private double paid;
    private double percent;
    private double penalties;
    private double total;
    private int expired;
    private double rest;
    private Date date;

    public int getID() {
        return ID;
    }

    public double getPaid() {
        return paid;
    }

    public double getPercent() {
        return percent;
    }

    public double getPenalties() {
        return penalties;
    }

    public double getTotal() {
        return total;
    }

    public int getExpired() {
        return expired;
    }

    public double getRest() {
        return rest;
    }

    public Date getDate() {
        return date;
    }

    public Credit(int ID, double paid, double percent, double penalties, double total, int expired, double rest, Date date) {
        this.ID = ID;
        this.paid = paid;
        this.percent = percent;
        this.penalties = penalties;
        this.total = total;
        this.expired = expired;
        this.rest = rest;
        this.date = date;
    }
}
