package org.jboss.research.invokedynamic;

@Interceptable
public interface UserService {
    @EnsureRole("manager")
    public void addUser(String userName, String userMailAddress, boolean admin);
}
