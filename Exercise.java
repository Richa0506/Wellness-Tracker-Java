package fitnesstracker;

public class Exercise {
    private String name;
    private double met; // MET value

    public Exercise(String name, double met) {
        this.name = name;
        this.met = met;
    }

    public String getName() {
        return name;
    }

    public double getMet() {
        return met;
    }

    @Override
    public String toString() {
        return name + " (MET=" + met + ")";
    }
}
