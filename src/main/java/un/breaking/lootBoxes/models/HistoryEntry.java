//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package un.breaking.lootBoxes.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HistoryEntry {
    private final LocalDateTime timestamp;
    private final String crateId;
    private final String rewardName;
    private final int amount;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");

    public HistoryEntry(LocalDateTime timestamp, String crateId, String rewardName, int amount) {
        this.timestamp = timestamp;
        this.crateId = crateId;
        this.rewardName = rewardName;
        this.amount = amount;
    }

    public HistoryEntry(String crateId, String rewardName, int amount) {
        this(LocalDateTime.now(), crateId, rewardName, amount);
    }

    public LocalDateTime getTimestamp() {
        return this.timestamp;
    }

    public String getFormattedDate() {
        return this.timestamp.format(formatter);
    }

    public String getCrateId() {
        return this.crateId;
    }

    public String getRewardName() {
        return this.rewardName;
    }

    public int getAmount() {
        return this.amount;
    }

    public String serialize() {
        String var10000 = this.timestamp.toString();
        return var10000 + ";" + this.crateId + ";" + this.rewardName + ";" + this.amount;
    }

    public static HistoryEntry deserialize(String data) {
        String[] parts = data.split(";");
        if (parts.length != 4) {
            return null;
        } else {
            try {
                LocalDateTime timestamp = LocalDateTime.parse(parts[0]);
                String crateId = parts[1];
                String rewardName = parts[2];
                int amount = Integer.parseInt(parts[3]);
                return new HistoryEntry(timestamp, crateId, rewardName, amount);
            } catch (Exception var6) {
                return null;
            }
        }
    }
}
