package org.jboss.research.proxy;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class Container {


    AdviceContextImpl adviceContext = new AdviceContextImpl(new EffectiveCallAdvice(), null);


    static class AdviceContextImpl implements AdviceContext {
        private final Advice advice;

        private final AdviceContext next;

        AdviceContextImpl(Advice advice, AdviceContext next) {
            this.advice = advice;
            this.next = next;
        }

        @Override
        public Object call(AnnotatedElement annotatedElement, Object receiver, Object[] args) {
            return advice.chain(annotatedElement, receiver, args, next);
        }
    }

    public <S> S getService(Class<S> serviceInterface, Class<? extends S> serviceImplementation) {
        final S impl;
        try {
            impl = serviceImplementation.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }


        return serviceInterface.cast(
                Proxy.newProxyInstance(serviceInterface.getClassLoader(),
                        new Class<?>[]{serviceInterface},
                        new InvocationHandler() {
                            @Override
                            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                                return adviceContext.call(method, impl, args);
                            }
                        }));
    }

    public void addAdvice(Advice advice) {
        adviceContext = new AdviceContextImpl(advice, adviceContext);
    }

}
