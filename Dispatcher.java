import java.util.*;

public class Dispatcher {

    private Graph city;
    public List<Volunteer> volunteers = new ArrayList<>();
    public List<AidRequest> requests = new ArrayList<>();

    public Dispatcher(Graph city) {
        this.city = city;
    }

    public void addVolunteer(Volunteer v) {
        volunteers.add(v);
    }

    public void addRequest(AidRequest r) {
        requests.add(r);
    }

    public Volunteer findVolunteerById(int id) {
        for (Volunteer v : volunteers) {
            if (v.id == id) return v;
        }
        return null;
    }

    public AidRequest findRequestById(int id) {
        for (AidRequest r : requests) {
            if (r.id == id) return r;
        }
        return null;
    }

    public void showLoad() {
        System.out.println("\n-- Volunteer Load Balancing --");
        System.out.printf("  %-12s %-8s %-12s %-12s %s%n",
                "Volunteer", "Load", "Assigned IDs", "Location", "Available");
        System.out.println("  " + "-".repeat(70));
        for (Volunteer v : volunteers) {
            double percent = (v.capacity > 0) ? (v.currentLoad * 100.0 / v.capacity) : 0;
            System.out.printf("  %-12s %d/%d (%.0f%%) %-12s %-12s %s%n",
                    v.name, v.currentLoad, v.capacity, percent,
                    v.assignedRequests.toString(),
                    city.nodeNames[v.location],
                    v.canAccept() ? "Yes" : "No");
        }
    }

    public void dispatch() {
        List<AidRequest> pending = new ArrayList<>();
        for (AidRequest r : requests) {
            if (!r.assigned && !r.completed) pending.add(r);  // Only unassigned AND not completed
        }

        if (pending.isEmpty()) {
            System.out.println("  No pending requests to dispatch.");
            return;
        }

        pending.sort((a, b) -> Long.compare(b.getWaitingSeconds(), a.getWaitingSeconds()));

        System.out.printf("  Dispatching %d pending requests...%n", pending.size());

        for (AidRequest r : pending) {
            int[] dist = city.dijkstra(r.location);

            Volunteer best = null;
            double bestScore = Double.MAX_VALUE;

            for (Volunteer v : volunteers) {
                if (!v.canAccept()) continue;

                int distance = dist[v.location];
                if (distance == Integer.MAX_VALUE) distance = 9999;

                long waitSec = Math.min(r.getWaitingSeconds(), 3600);
                double waitBonus = waitSec * 0.1;

                double loadFactor = (double) v.currentLoad / v.capacity;
                double loadPenalty = loadFactor * 20;

                double score = distance + loadPenalty - waitBonus;

                if (best == null || score < bestScore ||
                        (Math.abs(score - bestScore) < 0.01 && v.currentLoad < best.currentLoad)) {
                    best = v;
                    bestScore = score;
                }
            }

            if (best != null) {
                r.assigned = true;
                r.completed = false;
                r.volunteerId = best.id;
                r.volunteerPhone = best.phone;
                best.assign(r.id);

                int distance = city.dijkstra(r.location)[best.location];
                if (distance == Integer.MAX_VALUE) distance = 9999;

                System.out.printf("  ✓ Assigned Request #%d (%s - %s) to %s (distance: %d, load: %d/%d)%n",
                        r.id, r.name, r.aidType, best.name, distance,
                        best.currentLoad, best.capacity);
            } else {
                System.out.printf("  ✗ No available volunteer for Request #%d (%s - %s)%n",
                        r.id, r.name, r.aidType);
            }
        }
    }

    // NEW: Complete a task (release volunteer, mark as completed)
    public boolean completeTask(int requestId) {
        AidRequest req = findRequestById(requestId);
        if (req == null) {
            System.out.println("  Request not found.");
            return false;
        }

        if (!req.assigned) {
            System.out.println("  Request is not currently assigned to any volunteer.");
            return false;
        }

        if (req.completed) {
            System.out.println("  Request is already completed.");
            return false;
        }

        // Free the volunteer
        Volunteer vol = findVolunteerById(req.volunteerId);
        if (vol != null) {
            vol.cancel(req.id);
            System.out.printf("  Volunteer %s freed (now has %d/%d tasks)%n",
                    vol.name, vol.currentLoad, vol.capacity);
        }

        // Mark as completed (not assigned anymore, but completed)
        req.assigned = false;
        req.completed = true;
        req.completedTimeMillis = System.currentTimeMillis();

        System.out.printf("  ✓ Request #%d (%s) marked as COMPLETED. Delivery time: %d minutes%n",
                req.id, req.name, req.getCompletionTimeMinutes());

        return true;
    }

    // Get completed tasks (for reporting)
    public List<AidRequest> getCompletedRequests() {
        List<AidRequest> completed = new ArrayList<>();
        for (AidRequest r : requests) {
            if (r.completed) completed.add(r);
        }
        return completed;
    }

    // Get active assignments
    public List<AidRequest> getActiveAssignments() {
        List<AidRequest> active = new ArrayList<>();
        for (AidRequest r : requests) {
            if (r.assigned && !r.completed) active.add(r);
        }
        return active;
    }

    public List<AidRequest> blockRoad(int u, int v) {
        List<AidRequest> affected = new ArrayList<>();

        if (!city.blockEdge(u, v)) return affected;

        String edgeKey = Math.min(u, v) + "-" + Math.max(u, v);

        for (AidRequest r : requests) {
            if (!r.assigned || r.completed) continue;  // Skip completed tasks

            Volunteer vol = findVolunteerById(r.volunteerId);
            if (vol == null) continue;

            Set<String> path = city.getPathEdges(vol.location, r.location);
            if (path.contains(edgeKey)) {
                vol.cancel(r.id);
                r.assigned = false;
                // Don't mark as completed, just unassign
                r.volunteerId = -1;
                r.volunteerPhone = "";
                affected.add(r);
                System.out.printf("  Request #%d unassigned due to blocked road%n", r.id);
            }
        }
        return affected;
    }

    public boolean unblockRoad(int u, int v) {
        return city.unblockEdge(u, v);
    }

    public boolean cancelByVolunteer(String volunteerName, int requestId) {
        Volunteer vol = null;
        for (Volunteer v : volunteers) {
            if (v.name.equalsIgnoreCase(volunteerName)) {
                vol = v;
                break;
            }
        }
        if (vol == null) return false;

        if (!vol.assignedRequests.contains(requestId)) return false;

        AidRequest req = findRequestById(requestId);
        if (req == null) return false;

        vol.cancel(requestId);
        req.assigned = false;
        req.completed = false;  // Not completed, just cancelled
        req.volunteerId = -1;
        req.volunteerPhone = "";

        System.out.printf("  Volunteer %s cancelled request #%d%n", volunteerName, requestId);
        return true;
    }

    public boolean adminRelease(int requestId) {
        // This now calls completeTask instead of just releasing
        return completeTask(requestId);
    }

    public void repairAssignments() {
        System.out.println("\n--- Repairing Assignment Consistency ---");

        for (Volunteer v : volunteers) {
            v.assignedRequests.clear();
            v.currentLoad = 0;
        }

        for (AidRequest r : requests) {
            if (r.assigned && !r.completed && r.volunteerId != -1) {
                Volunteer v = findVolunteerById(r.volunteerId);
                if (v != null) {
                    if (!v.assignedRequests.contains(r.id)) {
                        v.assignedRequests.add(r.id);
                        v.currentLoad = v.assignedRequests.size();
                        System.out.printf("  Repaired: Request #%d -> Volunteer %s%n", r.id, v.name);
                    }
                } else {
                    r.assigned = false;
                    r.volunteerId = -1;
                    r.volunteerPhone = "";
                    System.out.printf("  Repaired: Request #%d had invalid volunteer, marked as unassigned%n", r.id);
                }
            }
        }
        System.out.println("  Assignment repair complete.");
    }
}