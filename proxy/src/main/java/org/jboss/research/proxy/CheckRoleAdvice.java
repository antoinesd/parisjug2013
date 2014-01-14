package org.jboss.research.proxy;

import java.lang.reflect.AnnotatedElement;

/**
* @author Antoine Sabot-Durand
*/
public class CheckRoleAdvice implements Advice {
    private static void checkRole(AnnotatedElement annotatedElement, EnsureRole ensureRole) {
        System.out.println("verify that role is " + ensureRole.value() + " when calling " + annotatedElement);
    }

    @Override
    public Object chain(AnnotatedElement annotatedElement, Object receiver, Object[] args, AdviceContext context) {
        EnsureRole ensureRole = annotatedElement.getAnnotation(EnsureRole.class);
        if (ensureRole != null) {
            checkRole(annotatedElement, ensureRole);
        }
        return context.call(annotatedElement, receiver, args);
    }
}
