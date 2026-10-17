public class AidRequest {
    private static int counter = 1;

    public int id;
    public String name;
    public String password;
    public String phone;
    public String aidType;
    public int location;

    public long requestTimeMillis;
    public long completedTimeMillis;  // New field for completion time

    public boolean assigned;
    public boolean completed;  // New field for completed status
    public int volunteerId;
    public String volunteerPhone;

    public AidRequest(String name, String password, String aidType, int location, String phone) {
        this.id = counter++;
        this.name = name;
        this.password = password;
        this.aidType = aidType;
        this.location = location;
        this.phone = phone;
        this.requestTimeMillis = System.currentTimeMillis();
        this.completedTimeMillis = 0;
        this.assigned = false;
        this.completed = false;
        this.volunteerId = -1;
        this.volunteerPhone = "";
    }

    public AidRequest(String name, String phone, String aidType, int location) {
        this(name, phone, aidType, location, phone);
    }

    public long getWaitingSeconds() {
        return (System.currentTimeMillis() - requestTimeMillis) / 1000;
    }

    public long getWaitingMinutes() {
        return getWaitingSeconds() / 60;
    }

    public long getCompletionTimeMinutes() {
        if (completedTimeMillis == 0) return 0;
        return (completedTimeMillis - requestTimeMillis) / 60000;
    }

    public String getStatus() {
        if (completed) return "COMPLETED";
        if (assigned) return "ASSIGNED";
        return "WAITING";
    }

    public static void resetCounter(int val) {
        counter = val;
    }
}