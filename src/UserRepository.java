import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Singleton to store users during application runtime
 */
public class UserRepository {

    private static UserRepository instance;

    private static Map<String, User> userRepository;

    private UserRepository() {
        userRepository = new HashMap<>();
    }

    public static User getUser(String s) {
        if (userRepository.containsKey(s)) {
            return userRepository.get(s);
        }
        return null;
    }

    public static void addUser(User user) {
        if (!userRepository.containsKey(user.getName())) {
            userRepository.put(user.getName(), user);
        }
    }

    public static Map<String, User> getUserRepository() {
        return userRepository;
    }

    public static void addUser(String userName) {
        User user = new User(userName);
        userRepository.put(userName, user);
    }

    public static UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }

}