import org.json.simple.*;
import org.json.simple.parser.*;
import java.io.*;
import java.util.*;

public class DataStore {

    // Custom pretty print method for JSON
    private static String prettyPrintJSON(JSONArray arr) {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");

        for (int i = 0; i < arr.size(); i++) {
            JSONObject obj = (JSONObject) arr.get(i);
            sb.append(prettyPrintJSONObject(obj, 1));
            if (i < arr.size() - 1) {
                sb.append(",\n");
            } else {
                sb.append("\n");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    private static String prettyPrintJSONObject(JSONObject obj, int indentLevel) {
        StringBuilder sb = new StringBuilder();
        String indent = "  ".repeat(indentLevel);
        String indentInner = "  ".repeat(indentLevel + 1);

        sb.append("{\n");

        // Sort keys for consistent ordering
        List<String> keys = new ArrayList<>(obj.keySet());
        Collections.sort(keys);

        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            Object value = obj.get(key);

            sb.append(indentInner).append("\"").append(key).append("\": ");

            if (value instanceof JSONObject) {
                sb.append(prettyPrintJSONObject((JSONObject) value, indentLevel + 1));
            } else if (value instanceof String) {
                sb.append("\"").append(value).append("\"");
            } else {
                sb.append(value);
            }

            if (i < keys.size() - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }

        sb.append(indent).append("}");
        return sb.toString();
    }

    private static void savePrettyJSON(JSONArray arr, String path) throws Exception {
        try (FileWriter fw = new FileWriter(path)) {
            fw.write(prettyPrintJSON(arr));
            fw.flush();
        }
    }

    // ==================== ADMINS ====================
    @SuppressWarnings("unchecked")
    public static void saveAdmins(String path, List<Admin> admins) throws Exception {
        JSONArray arr = new JSONArray();
        for (Admin a : admins) {
            JSONObject obj = new JSONObject();
            obj.put("name", a.name);
            obj.put("password", a.password);
            arr.add(obj);
        }
        savePrettyJSON(arr, path);
    }

    public static List<Admin> loadAdmins(String path) throws Exception {
        List<Admin> list = new ArrayList<>();
        File f = new File(path);
        if (!f.exists()) return list;

        JSONArray arr = (JSONArray) new JSONParser().parse(new FileReader(path));
        for (Object o : arr) {
            JSONObject obj = (JSONObject) o;
            list.add(new Admin((String) obj.get("name"), (String) obj.get("password")));
        }
        return list;
    }

    // ==================== VOLUNTEERS ====================
    @SuppressWarnings("unchecked")
    public static void saveVolunteers(String path, List<Volunteer> volunteers) throws Exception {
        JSONArray arr = new JSONArray();
        for (Volunteer v : volunteers) {
            JSONObject obj = new JSONObject();
            obj.put("name", v.name);
            obj.put("password", v.password);
            obj.put("phone", v.phone);
            obj.put("location", v.location);
            obj.put("capacity", v.capacity);
            obj.put("currentLoad", v.currentLoad);

            JSONArray assigned = new JSONArray();
            for (int id : v.assignedRequests) {
                assigned.add(id);
            }
            obj.put("assignedRequests", assigned);
            arr.add(obj);
        }
        savePrettyJSON(arr, path);
    }

    public static List<Volunteer> loadVolunteers(String path) throws Exception {
        List<Volunteer> list = new ArrayList<>();
        File f = new File(path);
        if (!f.exists()) return list;

        JSONArray arr = (JSONArray) new JSONParser().parse(new FileReader(path));
        int maxId = 0;

        for (Object o : arr) {
            JSONObject obj = (JSONObject) o;
            Volunteer v = new Volunteer(
                    (String) obj.get("name"),
                    (String) obj.get("password"),
                    (String) obj.get("phone"),
                    ((Long) obj.get("location")).intValue(),
                    ((Long) obj.get("capacity")).intValue()
            );

            if (obj.containsKey("currentLoad")) {
                v.currentLoad = ((Long) obj.get("currentLoad")).intValue();
            }

            if (obj.containsKey("assignedRequests")) {
                JSONArray assigned = (JSONArray) obj.get("assignedRequests");
                v.assignedRequests.clear();
                for (Object idObj : assigned) {
                    int rid = ((Long) idObj).intValue();
                    v.assignedRequests.add(rid);
                }
                v.currentLoad = v.assignedRequests.size();
            }

            list.add(v);
            maxId = Math.max(maxId, v.id);
        }
        Volunteer.resetCounter(maxId + 1);
        return list;
    }

    // ==================== AID REQUESTS ====================
    @SuppressWarnings("unchecked")
    public static void saveRequests(String path, List<AidRequest> requests) throws Exception {
        JSONArray arr = new JSONArray();
        for (AidRequest r : requests) {
            JSONObject obj = new JSONObject();
            obj.put("id", r.id);
            obj.put("name", r.name);
            obj.put("password", r.password);
            obj.put("phone", r.phone);
            obj.put("aidType", r.aidType);
            obj.put("location", r.location);
            obj.put("requestTimeMillis", r.requestTimeMillis);
            obj.put("assigned", r.assigned);
            obj.put("volunteerId", r.volunteerId);
            obj.put("volunteerPhone", r.volunteerPhone);
            obj.put("completed", r.completed);
            obj.put("completedTimeMillis", r.completedTimeMillis);
            arr.add(obj);
        }
        savePrettyJSON(arr, path);
    }

    public static List<AidRequest> loadRequests(String path) throws Exception {
        List<AidRequest> list = new ArrayList<>();
        File f = new File(path);
        if (!f.exists()) return list;

        JSONArray arr = (JSONArray) new JSONParser().parse(new FileReader(path));
        int maxId = 0;

        for (Object o : arr) {
            JSONObject obj = (JSONObject) o;
            AidRequest r = new AidRequest(
                    (String) obj.get("name"),
                    (String) obj.get("password"),
                    (String) obj.get("aidType"),
                    ((Long) obj.get("location")).intValue(),
                    (String) obj.get("phone")
            );

            r.id = ((Long) obj.get("id")).intValue();
            r.requestTimeMillis = (Long) obj.get("requestTimeMillis");
            r.assigned = (Boolean) obj.get("assigned");
            r.volunteerId = ((Long) obj.get("volunteerId")).intValue();
            r.volunteerPhone = (String) obj.get("volunteerPhone");

            // Load new fields with backward compatibility
            if (obj.containsKey("completed")) {
                r.completed = (Boolean) obj.get("completed");
            } else {
                r.completed = false;  // Default for old data
            }

            if (obj.containsKey("completedTimeMillis")) {
                r.completedTimeMillis = (Long) obj.get("completedTimeMillis");
            } else {
                r.completedTimeMillis = 0L;  // Default for old data
            }

            list.add(r);
            maxId = Math.max(maxId, r.id);
        }
        AidRequest.resetCounter(maxId + 1);
        return list;
    }
}