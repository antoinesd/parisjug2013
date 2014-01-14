Proxy vs Invokedynamic
=======

This code was first created from Rémi Forax. It's a proof of concept for using InvokeDynamics vs classical Java proxy in 'magical'
frameworks.

What is showed
-------------

This maven project has 2 modules : proxy and invokedynamic. Both modules do the same : call a service method on witch a security annotation was added (defined for the purpose of the test).
In this service we do throw an exception to show the stack trace.

Proxy version gives a verbose and proxy stack trace while InvokeDynamic version shows a clean stack trace with only defined
classes in it.

Pre-requsisites to run this poc
-------------

You'll need a JDK 1.7 (tested with update 45). The code is not running under Java 8 right now due to API change in java.lang.invoke between java 7 and 8.
You'll also need maven 3.x

Compiling the code
----------

As we have to rewrite bytecode after compilation in invokedynamic module, it's important that you compile the code from Maven (and not your IDE). a
simple `mvn clean package` will do it.

Run the code
----------

You can run the code from maven as well or from you IDE (be careful that your IDE doesn't recompile the code or if it does be sure that `process-class` maven phase is also launch to weave the code)

Go to `proxy` module and enter

`mvn -Dtest=ProxyTest`

The result stack trace will be :

```java
java.lang.RuntimeException: There should be a lot of noise in the stacktrace
    org.jboss.research.proxy.UserServiceImpl.addUser(UserServiceImpl.java:8)
    sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
    sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:57)
    sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
    java.lang.reflect.Method.invoke(Method.java:606)
    org.jboss.research.proxy.EffectiveCallAdvice.chain(EffectiveCallAdvice.java:15)
    org.jboss.research.proxy.Container$AdviceContextImpl.call(Container.java:26)
    org.jboss.research.proxy.CheckRoleAdvice.chain(CheckRoleAdvice.java:19)
    org.jboss.research.proxy.Container$AdviceContextImpl.call(Container.java:26)
    org.jboss.research.proxy.Container$1.invoke(Container.java:45)
    com.sun.proxy.$Proxy4.addUser(Unknown Source)
    org.jboss.research.proxy.ProxyTest.proxy(ProxyTest.java:16)
```

Go to the `invokedynamic` module and launch

`mvn -Dtest=InvokeDynamicProxyTest`

Stack trace will be less verbose :

```java
java.lang.RuntimeException: Let's look at the clean stack trace
    org.jboss.research.invokedynamic.UserServiceImpl.addUser(UserServiceImpl.java:15)
    org.jboss.research.invokedynamic.InvokeDynamicTest.invokeDynamic(InvokeDynamicTest.java:44)
```

Going further
---------

From this code I hope to produce a JBoss Weld version using partly InvokeDynamic to simplify the framework,
provides better performance (to be tested) and most specially to have better stack traces for users.
