package database.daos;

import com.avaje.ebean.Ebean;
import database.models.User;

/**
 * Created by Michon on 3/15/2016.
 */
public class UserDAO {
    public void save(User user) {

    }

    public User findByUsername(String username) {
        //return Ebean.find(User.class).where().eq("username", username).findUnique();
        return new User(username);
    }
}
