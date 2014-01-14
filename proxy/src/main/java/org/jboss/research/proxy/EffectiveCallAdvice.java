package org.jboss.research.proxy;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;

/**
* @author Antoine Sabot-Durand
*/
class EffectiveCallAdvice implements Advice {
    @Override
    public Object chain(AnnotatedElement annotatedElement, Object receiver, Object[] args, AdviceContext context) {
        try {
            return ((Method) annotatedElement).invoke(receiver, args);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            if (cause instanceof Error) {
                throw (Error) cause;
            }
            throw new UndeclaredThrowableException(cause);
        }
    }
}
