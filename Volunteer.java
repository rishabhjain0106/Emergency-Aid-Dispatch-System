import java.util.*;

public class Volunteer extends User {
    public int id;
    public String password;      // for login
    public int location;
    public int capacity;
    public int currentLoad;
    public List<Integer> assignedRequests;

    private static int counter = 1;

    public Volunteer(String name, String password, String phone, int location, int capacity) {
        super(name, phone);
        this.password = password;
        this.id = counter++;
        this.location = location;
        this.capacity = capacity;
        this.currentLoad = 0;
        this.assignedRequests = new ArrayList<>();
    }

    // Legacy constructor
    public Volunteer(String name, String phone, int location, int capacity) {
        this(name, phone, phone, location, capacity);
    }

    public boolean canAccept() {
        return currentLoad < capacity;
    }

    public void assign(int reqId) {
        assignedRequests.add(reqId);
        currentLoad++;
    }

    public void cancel(int reqId) {
        if (assignedRequests.remove(Integer.valueOf(reqId))) {
            currentLoad = Math.max(0, currentLoad - 1);
        }
    }

    public static void resetCounter(int val) {
        counter = val;
    }
}