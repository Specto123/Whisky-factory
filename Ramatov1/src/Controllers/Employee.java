package Controllers;

public class Employee {
    private int id;
    private String name;
    private double oklad;
    private double premiya;
    private double itog;
    private double procent;
    private double kolvo_prodaj;
    private double kolvo_pokupok;
    private double kolvo_proizvodstva;
    private double obwiy;

    public Employee(int id, String name, double oklad, double premiya, double itog, double procent, double kolvo_prodaj, double kolvo_pokupok, double kolvo_proizvodstva, double obwiy) {
        this.id = id;
        this.name = name;
        this.oklad = oklad;
        this.premiya = premiya;
        this.itog = itog;
        this.procent = procent;
        this.kolvo_prodaj = kolvo_prodaj;
        this.kolvo_pokupok = kolvo_pokupok;
        this.kolvo_proizvodstva = kolvo_proizvodstva;
        this.obwiy = obwiy;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getOklad() {
        return oklad;
    }

    public double getPremiya() {
        return premiya;
    }

    public double getItog() {
        return itog;
    }

    public double getProcent() {
        return procent;
    }

    public double getKolvo_prodaj() {
        return kolvo_prodaj;
    }

    public double getKolvo_pokupok() {
        return kolvo_pokupok;
    }

    public double getKolvo_proizvodstva() {
        return kolvo_proizvodstva;
    }

    public double getObwiy() {
        return obwiy;
    }
}
