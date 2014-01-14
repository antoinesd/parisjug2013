package org.jboss.research.proxy;

public class UserServiceImpl implements UserService {
    @Override
    public void addUser(String userName, String userMailAddress, boolean admin) {
        // add a new user to the database
        // send a mail to invite the new user
        throw new RuntimeException("There should be a lot of noise in the stacktrace");
    }
}
