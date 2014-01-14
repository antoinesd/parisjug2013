package org.jboss.research.proxy;

public class Main {

    public static void main(String[] arguments) {
        Container container = new Container();

        container.addAdvice(new CheckRoleAdvice());

        UserService userService = container.getService(UserService.class, UserServiceImpl.class);
        for (int i = 0; i < 100; i++) {
            userService.addUser("Darth Vador", "1 Force Street, Death Star", true);
        }
    }

}
