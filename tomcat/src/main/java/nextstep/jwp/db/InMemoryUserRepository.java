package nextstep.jwp.db;

import nextstep.jwp.model.User;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUserRepository {

    private static final Map<String, User> database = new ConcurrentHashMap<>();

    static {
        final User user = new User(1L, "gugu", "password", "hkkang@woowahan.com");
        database.put(user.getAccount(), user);
    }

    public static void save(User user) {
        database.put(user.getAccount(), user);
    }

    public static Optional<User> findByAccountAndPassword(String account, String password) {
        final Optional<User> optionalUser = Optional.ofNullable(database.get(account));

        if (optionalUser.isPresent()) {
            final User user = optionalUser.get();
            if (user.checkPassword(password)) {
                return optionalUser;
            }
        }

        return Optional.empty();
    }

    private InMemoryUserRepository() {}
}
