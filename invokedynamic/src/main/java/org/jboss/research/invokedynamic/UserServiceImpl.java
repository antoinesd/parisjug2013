package org.jboss.research.invokedynamic;

//@Intercepted
public class UserServiceImpl implements UserService {
    private static int COUNTER;

    @Inject
    private Mailer mailer;

    @Override
    public void addUser(String userName, String userMailAddress, boolean admin) {
        // add a new user to the database
        // send a mail to invite the new user
        //mailer.sendAMail(userMailAddress, "hello " + userName+", ...");
        throw new RuntimeException("Let's look at the clean stack trace");
    }
}
