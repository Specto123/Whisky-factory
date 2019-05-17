package Controllers;

public class RawMaterial {
    private int id;
    private String name;
    private double sum;
    private double count;
    private double count2;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    public void setCount(double count) {
        this.count = count;
    }

    public void setCount2(double count2) {
        this.count2 = count2;
    }

    public double getSum() {
        return sum;
    }

    public double getCount() {
        return count;
    }

    public double getCount2() {
        return count2;
    }

    public RawMaterial(int id, String name, double sum, double count, double count2) {
        this.id = id;
        this.name = name;
        this.sum = sum;
        this.count = count;
        this.count2 = count2;
    }
}
