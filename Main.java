//import java.io.File;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        GUI.main(args);
    }
}

//import java.util.*;
//        import java.io.*;
//
//public class Main {
//
//    static Scanner sc = new Scanner(System.in);
//
//    static Graph graph;
//    static Dispatcher dispatcher;
//    static List<Admin> admins = new ArrayList<>();
//
//    static final String VOLUNTEER_FILE = "/home/devansh/IdeaProjects/DAA pbl/src/volunteers.json";
//    static final String VICTIM_FILE = "/home/devansh/IdeaProjects/DAA pbl/src/victim.json";
//    static final String ADMIN_FILE = "/home/devansh/IdeaProjects/DAA pbl/src/admin.json";
//    static final String GRAPH_FILE = "/home/devansh/IdeaProjects/DAA pbl/src/graph.txt";
//
//    public static void main(String[] args) throws Exception {
//
//        File graphFile = new File(GRAPH_FILE);
//        if (!graphFile.exists()) {
//            System.err.println("Error: " + GRAPH_FILE + " not found!");
//            return;
//        }
//        graph = Graph.loadFromFile(GRAPH_FILE);
//        dispatcher = new Dispatcher(graph);
//
//        File volFile = new File(VOLUNTEER_FILE);
//        if (volFile.exists()) {
//            List<Volunteer> vols = DataStore.loadVolunteers(VOLUNTEER_FILE);
//            for (Volunteer v : vols) dispatcher.addVolunteer(v);
//        }
//
//        File reqFile = new File(VICTIM_FILE);
//        if (reqFile.exists()) {
//            List<AidRequest> reqs = DataStore.loadRequests(VICTIM_FILE);
//            for (AidRequest r : reqs) dispatcher.addRequest(r);
//        }
//
//        File adminFile = new File(ADMIN_FILE);
//        if (adminFile.exists()) {
//            admins = DataStore.loadAdmins(ADMIN_FILE);
//        }
//        if (admins.isEmpty()) {
//            admins.add(new Admin("admin", "admin123"));
//            DataStore.saveAdmins(ADMIN_FILE, admins);
//        }
//
//        dispatcher.repairAssignments();
//
//        System.out.println("╔══════════════════════════════════════╗");
//        System.out.println("║  Emergency Aid Dispatch System (EADS)║");
//        System.out.println("╚══════════════════════════════════════╝");
//
//        while (true) {
//            System.out.println("\n--- Main Menu ---");
//            System.out.println("1. Victim");
//            System.out.println("2. Volunteer");
//            System.out.println("3. Admin");
//            System.out.println("0. Exit");
//            System.out.print("Choice: ");
//
//            String line = sc.nextLine().trim();
//            if (line.equals("0")) {
//                saveAll();
//                System.out.println("Data saved. Goodbye.");
//                break;
//            }
//
//            int ch;
//            try {
//                ch = Integer.parseInt(line);
//            } catch (NumberFormatException e) {
//                continue;
//            }
//
//            switch (ch) {
//                case 1 -> victimPortal();
//                case 2 -> volunteerPortal();
//                case 3 -> adminPortal();
//            }
//        }
//    }
//
//    static void saveAll() throws Exception {
//        DataStore.saveVolunteers(VOLUNTEER_FILE, dispatcher.volunteers);
//        DataStore.saveRequests(VICTIM_FILE, dispatcher.requests);
//        DataStore.saveAdmins(ADMIN_FILE, admins);
//        System.out.println("✓ Data saved successfully.");
//    }
//
//    // ==================== VICTIM PORTAL ====================
//    static void victimPortal() throws Exception {
//        System.out.println("\n=== Victim Portal ===");
//        System.out.println("1. Register (new victim)");
//        System.out.println("2. Login (existing victim)");
//        System.out.println("0. Back");
//        System.out.print("Choice: ");
//        String line = sc.nextLine().trim();
//
//        if (line.equals("0")) return;
//        int ch;
//        try {
//            ch = Integer.parseInt(line);
//        } catch (NumberFormatException e) {
//            return;
//        }
//
//        if (ch == 1) {
//            registerVictim();
//        } else if (ch == 2) {
//            AidRequest r = loginVictim();
//            if (r != null) victimMenu(r);
//        }
//    }
//
//    static void registerVictim() throws Exception {
//        System.out.println("\n-- Register as Victim --");
//        System.out.print("Full Name   : ");
//        String name = sc.nextLine().trim();
//        System.out.print("Password    : ");
//        String password = sc.nextLine().trim();
//        System.out.print("Phone       : ");
//        String phone = sc.nextLine().trim();
//        System.out.print("Location (node number 0-" + (graph.nodes - 1) + "): ");
//        int loc = readInt();
//        System.out.print("Aid Type (food/medical/water/shelter): ");
//        String type = sc.nextLine().trim();
//
//        for (AidRequest existing : dispatcher.requests) {
//            if (existing.name.equalsIgnoreCase(name)) {
//                System.out.println("A request with this name already exists. Please login instead.");
//                return;
//            }
//        }
//
//        AidRequest r = new AidRequest(name, password, type, loc, phone);
//        dispatcher.addRequest(r);
//        saveAll();
//        System.out.println("✓ Request #" + r.id + " registered successfully!");
//        System.out.println("You will be notified once a volunteer is assigned.");
//    }
//
//    static AidRequest loginVictim() {
//        System.out.print("Name     : ");
//        String name = sc.nextLine().trim();
//        System.out.print("Password : ");
//        String password = sc.nextLine().trim();
//        AidRequest r = AuthService.loginVictim(dispatcher.requests, name, password);
//        if (r == null)
//            System.out.println("✗ Invalid credentials.");
//        return r;
//    }
//
//    static void victimMenu(AidRequest r) throws Exception {
//        while (true) {
//            System.out.println("\n--- Victim Menu [" + r.name + "] ---");
//            System.out.println("1. View my request status");
//            System.out.println("2. View nearby volunteers");
//            System.out.println("0. Logout");
//            System.out.print("Choice: ");
//            String line = sc.nextLine().trim();
//            if (line.equals("0"))
//                break;
//
//            int ch;
//            try {
//                ch = Integer.parseInt(line);
//            } catch (NumberFormatException e) {
//                continue;
//            }
//
//            if (ch == 1) {
//                viewVictimStatus(r);
//            } else if (ch == 2) {
//                viewNearbyVolunteers(r.location);
//            }
//        }
//    }
//
//    static void viewVictimStatus(AidRequest r) {
//        System.out.println("\n-- Your Request --");
//        System.out.printf("  ID        : %d%n", r.id);
//        System.out.printf("  Aid Type  : %s%n", r.aidType);
//        System.out.printf("  Location  : %s%n", graph.nodeNames[r.location]);
//        System.out.printf("  Waiting   : %d min%n", r.getWaitingMinutes());
//
//        String status = r.getStatus();
//        System.out.printf("  Status    : %s%n", status);
//
//        if (status.equals("ASSIGNED")) {
//            Volunteer v = dispatcher.findVolunteerById(r.volunteerId);
//            System.out.printf("  Volunteer : %s%n", v != null ? v.name : "Unknown");
//            System.out.printf("  Vol Phone : %s%n", r.volunteerPhone);
//            System.out.println("  Note      : Volunteer is on the way!");
//        } else if (status.equals("COMPLETED")) {
//            Volunteer v = dispatcher.findVolunteerById(r.volunteerId);
//            System.out.printf("  Helped By : %s%n", v != null ? v.name : "Unknown");
//            System.out.printf("  Completed : %d minutes after request%n", r.getCompletionTimeMinutes());
//            System.out.println("  ✓ Thank you! Your request has been fulfilled.");
//        } else {
//            System.out.println("  ⏳ Still waiting for a volunteer to be assigned.");
//            System.out.println("  Tip: Admin can run dispatch to assign volunteers.");
//        }
//    }
//
//    static void viewNearbyVolunteers(int location) {
//        List<Integer> nearby = graph.bfsNearby(location, 2);
//        System.out.println("\n-- Volunteers within 2 hops --");
//        boolean found = false;
//        for (Volunteer v : dispatcher.volunteers) {
//            if (nearby.contains(v.location) || v.location == location) {
//                int[] dist = graph.dijkstra(location);
//                int d = dist[v.location] == Integer.MAX_VALUE ? -1 : dist[v.location];
//                System.out.printf("  %-12s  Loc: %-10s  Available: %-5s  Dist: %d  Load: %d/%d%n",
//                        v.name, graph.nodeNames[v.location],
//                        v.canAccept() ? "Yes" : "No", d, v.currentLoad, v.capacity);
//                found = true;
//            }
//        }
//        if (!found)
//            System.out.println("  No volunteers found nearby.");
//    }
//
//    // ==================== VOLUNTEER PORTAL ====================
//    static void volunteerPortal() throws Exception {
//        System.out.println("\n=== Volunteer Portal ===");
//        System.out.println("1. Register (new volunteer)");
//        System.out.println("2. Login (existing volunteer)");
//        System.out.println("0. Back");
//        System.out.print("Choice: ");
//        String line = sc.nextLine().trim();
//        if (line.equals("0"))
//            return;
//
//        int ch;
//        try {
//            ch = Integer.parseInt(line);
//        } catch (NumberFormatException e) {
//            return;
//        }
//
//        if (ch == 1) {
//            registerVolunteer();
//        } else if (ch == 2) {
//            System.out.print("Name     : ");
//            String name = sc.nextLine().trim();
//            System.out.print("Password : ");
//            String password = sc.nextLine().trim();
//            Volunteer v = AuthService.loginVolunteer(dispatcher.volunteers, name, password);
//            if (v == null) {
//                System.out.println("✗ Invalid credentials.");
//                return;
//            }
//            volunteerMenu(v);
//        }
//    }
//
//    static void registerVolunteer() throws Exception {
//        System.out.println("\n-- Register as Volunteer --");
//        System.out.print("Full Name   : ");
//        String name = sc.nextLine().trim();
//        System.out.print("Password    : ");
//        String password = sc.nextLine().trim();
//        System.out.print("Phone       : ");
//        String phone = sc.nextLine().trim();
//        System.out.print("Location (node 0-" + (graph.nodes - 1) + "): ");
//        int loc = readInt();
//        System.out.print("Capacity (max tasks): ");
//        int cap = readInt();
//
//        for (Volunteer existing : dispatcher.volunteers) {
//            if (existing.name.equalsIgnoreCase(name)) {
//                System.out.println("Volunteer with this name already exists.");
//                return;
//            }
//        }
//
//        Volunteer v = new Volunteer(name, password, phone, loc, cap);
//        dispatcher.addVolunteer(v);
//        saveAll();
//        System.out.println("✓ Volunteer " + name + " registered successfully! ID=" + v.id);
//    }
//
//    static void volunteerMenu(Volunteer v) throws Exception {
//        while (true) {
//            System.out.println("\n--- Volunteer Menu [" + v.name + "] ---");
//            System.out.println("1. View my load");
//            System.out.println("2. View my assigned requests");
//            System.out.println("3. Cancel a task");
//            System.out.println("0. Logout");
//            System.out.print("Choice: ");
//            String line = sc.nextLine().trim();
//            if (line.equals("0"))
//                break;
//
//            int ch;
//            try {
//                ch = Integer.parseInt(line);
//            } catch (NumberFormatException e) {
//                continue;
//            }
//
//            if (ch == 1) {
//                System.out.printf("  Load: %d / %d%n", v.currentLoad, v.capacity);
//                System.out.printf("  Active assignments: %s%n",
//                        v.assignedRequests.isEmpty() ? "none" : v.assignedRequests.toString());
//            } else if (ch == 2) {
//                viewVolunteerAssignments(v);
//            } else if (ch == 3) {
//                cancelVolunteerTask(v);
//                saveAll();
//            }
//        }
//    }
//
//    static void viewVolunteerAssignments(Volunteer v) {
//        if (v.assignedRequests.isEmpty()) {
//            System.out.println("  No active assignments.");
//            return;
//        }
//        System.out.println("  -- Your Active Assignments --");
//        for (int rid : v.assignedRequests) {
//            for (AidRequest r : dispatcher.requests) {
//                if (r.id == rid && !r.completed) {
//                    System.out.printf("  #%-4d  %-12s  %-10s  Waiting: %d min  Loc: %s%n",
//                            r.id, r.name, r.aidType, r.getWaitingMinutes(),
//                            graph.nodeNames[r.location]);
//                }
//            }
//        }
//    }
//
//    static void cancelVolunteerTask(Volunteer v) throws Exception {
//        viewVolunteerAssignments(v);
//        System.out.print("Enter Request ID to cancel: ");
//        int id = readInt();
//        boolean ok = dispatcher.cancelByVolunteer(v.name, id);
//        System.out.println(ok ? "  ✓ Task cancelled." : "  ✗ Request not found in your assignments.");
//        if (ok) saveAll();
//    }
//
//    // ==================== ADMIN PORTAL ====================
//    static void adminPortal() throws Exception {
//        System.out.print("Admin Name    : ");
//        String name = sc.nextLine().trim();
//        System.out.print("Admin Password: ");
//        String pass = sc.nextLine().trim();
//        if (!AuthService.loginAdmin(name, pass, admins)) {
//            System.out.println("✗ Invalid admin credentials.");
//            return;
//        }
//        System.out.println("✓ Welcome, Admin.");
//
//        while (true) {
//            System.out.println("\n--- Admin Menu ---");
//            System.out.println("1. View city map");
//            System.out.println("2. Dispatch (assign volunteers to pending requests)");
//            System.out.println("3. Show volunteer load balancing");
//            System.out.println("4. Show all requests");
//            System.out.println("5. Block a road");
//            System.out.println("6. Unblock a road");
//            System.out.println("7. Complete a task (release volunteer, mark as done)");
//            System.out.println("8. Show completed tasks");
//            System.out.println("9. System info");
//            System.out.println("10. Repair assignments (fix inconsistencies)");
//            System.out.println("0. Logout");
//            System.out.print("Choice: ");
//            String line = sc.nextLine().trim();
//            if (line.equals("0"))
//                break;
//
//            int ch;
//            try {
//                ch = Integer.parseInt(line);
//            } catch (NumberFormatException e) {
//                continue;
//            }
//
//            switch (ch) {
//                case 1 -> graph.printMap();
//                case 2 -> {
//                    System.out.println("\n-- Dispatching --");
//                    dispatcher.dispatch();
//                    saveAll();
//                }
//                case 3 -> dispatcher.showLoad();
//                case 4 -> showAllRequests();
//                case 5 -> adminBlockRoad();
//                case 6 -> adminUnblockRoad();
//                case 7 -> adminCompleteTask();
//                case 8 -> showCompletedTasks();
//                case 9 -> showSystemInfo();
//                case 10 -> {
//                    dispatcher.repairAssignments();
//                    saveAll();
//                }
//                default -> System.out.println("Invalid option.");
//            }
//        }
//    }
//
//    static void showAllRequests() {
//        System.out.printf("%n  %-4s %-14s %-10s %-8s %-12s %-12s%n",
//                "ID", "Victim", "AidType", "Wait(m)", "Status", "Volunteer");
//        System.out.println("  " + "-".repeat(75));
//        for (AidRequest r : dispatcher.requests) {
//            String volName = "-";
//            if (r.assigned && !r.completed) {
//                Volunteer v = dispatcher.findVolunteerById(r.volunteerId);
//                volName = v != null ? v.name + " (active)" : "?";
//            } else if (r.completed) {
//                Volunteer v = dispatcher.findVolunteerById(r.volunteerId);
//                volName = v != null ? v.name + " (done)" : "?";
//            }
//
//            System.out.printf("  %-4d %-14s %-10s %-8d %-12s %-12s%n",
//                    r.id, r.name, r.aidType, r.getWaitingMinutes(),
//                    r.getStatus(), volName);
//        }
//    }
//
//    static void showCompletedTasks() {
//        List<AidRequest> completed = dispatcher.getCompletedRequests();
//        if (completed.isEmpty()) {
//            System.out.println("\n  No completed tasks yet.");
//            return;
//        }
//
//        System.out.println("\n-- Completed Tasks (History) --");
//        System.out.printf("  %-4s %-14s %-10s %-12s %-12s%n",
//                "ID", "Victim", "AidType", "Completion Time", "Volunteer");
//        System.out.println("  " + "-".repeat(65));
//
//        for (AidRequest r : completed) {
//            Volunteer v = dispatcher.findVolunteerById(r.volunteerId);
//            String volName = v != null ? v.name : "Unknown";
//            System.out.printf("  %-4d %-14s %-10s %-12s %-12s%n",
//                    r.id, r.name, r.aidType,
//                    r.getCompletionTimeMinutes() + " min",
//                    volName);
//        }
//    }
//
//    static void adminCompleteTask() throws Exception {
//        System.out.println("\n-- Complete a Task (Mark as Delivered) --");
//        System.out.println("This will free the volunteer and mark the request as completed.");
//        System.out.println("The victim will see this as 'COMPLETED' in their status.\n");
//
//        // Show only active assignments
//        List<AidRequest> active = dispatcher.getActiveAssignments();
//        if (active.isEmpty()) {
//            System.out.println("  No active assignments to complete.");
//            return;
//        }
//
//        System.out.println("  Active Assignments:");
//        for (AidRequest r : active) {
//            Volunteer v = dispatcher.findVolunteerById(r.volunteerId);
//            System.out.printf("    #%d - %s (%s) -> Volunteer: %s%n",
//                    r.id, r.name, r.aidType, v != null ? v.name : "Unknown");
//        }
//
//        System.out.print("\nEnter Request ID to mark as COMPLETED: ");
//        int id = readInt();
//        boolean ok = dispatcher.completeTask(id);
//
//        if (ok) {
//            saveAll();
//            System.out.println("\n  ✓ Task completed successfully!");
//            System.out.println("  → Volunteer is now free for new assignments");
//            System.out.println("  → Victim can see this as 'COMPLETED' in their status");
//        } else {
//            System.out.println("  ✗ Failed to complete task.");
//        }
//    }
//
//    static void adminBlockRoad() throws Exception {
//        printNodeList();
//        System.out.print("Node A: ");
//        int u = readInt();
//        System.out.print("Node B: ");
//        int v = readInt();
//        List<AidRequest> affected = dispatcher.blockRoad(u, v);
//        System.out.printf("  Road %s -- %s blocked.%n", graph.nodeNames[u], graph.nodeNames[v]);
//        if (affected.isEmpty()) {
//            System.out.println("  No assignments were affected.");
//        } else {
//            System.out.println("  Following requests were unassigned (route disrupted):");
//            for (AidRequest r : affected) {
//                System.out.printf("    #%d %s (%s)%n", r.id, r.name, r.aidType);
//            }
//            System.out.println("  Run Dispatch again to reassign them.");
//        }
//        saveAll();
//    }
//
//    static void adminUnblockRoad() throws Exception {
//        printNodeList();
//        System.out.print("Node A: ");
//        int u = readInt();
//        System.out.print("Node B: ");
//        int v = readInt();
//        boolean ok = dispatcher.unblockRoad(u, v);
//        System.out.println(ok ? "  Road unblocked." : "  No blocked road found between those nodes.");
//        saveAll();
//    }
//
//    static void showSystemInfo() {
//        long completed = dispatcher.getCompletedRequests().size();
//        long active = dispatcher.getActiveAssignments().size();
//        long waiting = dispatcher.requests.stream().filter(r -> !r.assigned && !r.completed).count();
//
//        System.out.println("\n-- System Info --");
//        System.out.printf("  Total requests   : %d%n", dispatcher.requests.size());
//        System.out.printf("    ✓ Completed    : %d%n", completed);
//        System.out.printf("    → Active       : %d%n", active);
//        System.out.printf("    ⏳ Waiting      : %d%n", waiting);
//        System.out.printf("  Total volunteers : %d%n", dispatcher.volunteers.size());
//        System.out.printf("  Available now    : %d%n",
//                dispatcher.volunteers.stream().filter(Volunteer::canAccept).count());
//        System.out.printf("  Graph nodes      : %d%n", graph.nodes);
//    }
//
//    static void printNodeList() {
//        System.out.println("  Nodes:");
//        for (int i = 0; i < graph.nodes; i++) {
//            System.out.printf("    %d: %s%n", i, graph.nodeNames[i]);
//        }
//    }
//
//    static int readInt() {
//        while (true) {
//            try {
//                return Integer.parseInt(sc.nextLine().trim());
//            } catch (NumberFormatException e) {
//                System.out.print("  Please enter a number: ");
//            }
//        }
//    }
//}