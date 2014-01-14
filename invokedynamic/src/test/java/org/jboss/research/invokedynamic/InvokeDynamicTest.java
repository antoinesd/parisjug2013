package org.jboss.research.invokedynamic;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.AnnotatedElement;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class InvokeDynamicTest {
    @SuppressWarnings("unused")
    private static void checkRole(AnnotatedElement annotatedElement, EnsureRole ensureRole) {
        // check role here
        System.out.println("verify that role is " + ensureRole.value() + " when calling " + annotatedElement);
    }

    private static MethodHandle getCheckRoleMethodHandle() throws NoSuchMethodException, IllegalAccessException {
        return MethodHandles.lookup().findStatic(InvokeDynamicTest.class,
          "checkRole",
          MethodType.methodType(void.class, AnnotatedElement.class, EnsureRole.class));
    }

    @Before
    public void setup() {
        Container.addAdvice(new Advice() {
            @Override
            public MethodHandle chain(AnnotatedElement annotatedElement, MethodHandle mh) throws NoSuchMethodException, IllegalAccessException {
                EnsureRole ensureRole = annotatedElement.getAnnotation(EnsureRole.class);
                if (ensureRole == null) {
                    return mh;
                }
                MethodHandle combiner = MethodHandles.insertArguments(getCheckRoleMethodHandle(), 0, annotatedElement, ensureRole);
                return MethodHandles.foldArguments(mh, combiner);
            }
        });
    }

    @Test
    public void invokeDynamicStackTrace() {
        UserService userService = new UserServiceImpl();
        try {
            userService.addUser("Darth Vador", "1 Force Street, Death Star", true);
        } catch (RuntimeException e) {
            Assert.assertEquals(2, stackTraceSize(e));
        }
    }

    private int stackTraceSize(RuntimeException e) {
        int i = 0;
        System.err.println(e);
        for (StackTraceElement stackTraceElement : e.getStackTrace()) {
            System.err.println("  " + stackTraceElement);
            i ++;
            if (stackTraceElement.toString().startsWith(getClass().getName())) {
                return i;
            }
        }
        throw new RuntimeException();
    }
}
