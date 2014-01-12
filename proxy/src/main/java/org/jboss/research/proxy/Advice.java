package org.jboss.research.proxy;

import java.lang.reflect.AnnotatedElement;

public interface Advice {
    public Object chain(AnnotatedElement annotatedElement, Object receiver, Object[] args, AdviceContext context);
}
