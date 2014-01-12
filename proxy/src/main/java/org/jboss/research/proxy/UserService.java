package org.jboss.research.proxy;

public interface UserService {
    @EnsureRole("manager")
    public void addUser(String userName, String userMailAddress, boolean admin);
}
