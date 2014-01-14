package org.jboss.research.proxy;

import org.junit.Assert;
import org.junit.Test;

public class ProxyTest {

    @Test
    public void proxy() {
        Container container = new Container();
        container.addAdvice(new CheckRoleAdvice());

        UserService userService = container.getService(UserService.class, UserServiceImpl.class);

        try {
            userService.addUser("Darth Vador", "1 Force Street, Death Star", true);
        } catch (RuntimeException e) {
            Assert.assertEquals(12, stackTraceSize(e));
        }
    }

    private int stackTraceSize(RuntimeException e) {
        int i = 0;
        System.err.println(e);
        for (StackTraceElement stackTraceElement : e.getStackTrace()) {
            System.err.println("  " + stackTraceElement);
            i++;
            if (stackTraceElement.toString().startsWith(getClass().getName())) {
                return i;
            }
        }
        throw new RuntimeException();
    }

}
