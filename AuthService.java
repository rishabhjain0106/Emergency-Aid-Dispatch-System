import java.util.*;

public class AuthService {

    public static Volunteer loginVolunteer(List<Volunteer> list, String name, String password) {
        for (Volunteer v : list) {
            if (v.name.equalsIgnoreCase(name) && v.password.equals(password)) {
                return v;
            }
        }
        return null;
    }

    public static AidRequest loginVictim(List<AidRequest> list, String name, String password) {
        for (AidRequest r : list) {
            if (r.name.equalsIgnoreCase(name) && r.password.equals(password)) {
                return r;
            }
        }
        return null;
    }

    public static boolean loginAdmin(String name, String password, List<Admin> admins) {
        for (Admin a : admins) {
            if (a.name.equals(name) && a.password.equals(password)) {
                return true;
            }
        }
        return false;
    }

    // Overload for single admin check (simplified)
    public static boolean loginAdmin(String name, String password) {
        // Default admin credentials
        return name.equals("admin") && password.equals("admin123");
    }
}