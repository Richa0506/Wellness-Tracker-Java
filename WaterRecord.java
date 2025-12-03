package fitnesstracker;

import java.time.LocalDateTime;

public class WaterRecord {
    private int amount;
    private LocalDateTime timestamp;

    public WaterRecord(int amount, LocalDateTime timestamp) {
        this.amount = amount;
        this.timestamp = timestamp;
    }
    public int getAmount() { return amount; }
    public LocalDateTime getTimestamp() { return timestamp; }
}