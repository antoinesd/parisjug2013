package org.jboss.research.invokedynamic;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.AnnotatedElement;

public interface Advice {
    public MethodHandle chain(AnnotatedElement annotatedElement, MethodHandle previous);
}
