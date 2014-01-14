Proxy vs Invokedynamic
=======

This code was first created from Rémi Forax. It's a poc for using InvokeDynamics vs classical Java proxy in 'magical'
frameworks.

What is showed
-------------

This maven project has 2 modules : proxy and invokedynamic. Both modules do the same : call a service method an hundred
times on witch a security annotation was added (definied for the purpose of the test).
After these calls we throw an exception to show the stack trace.

Proxy version gives a verbose and proxy stack trace while InokeDynamic version shows a clean stack trace with only definied
classes in it.

Pre-requsisites to run this poc
-------------

You'll need a JDK 1.7 (tested with update 45). The code is not running under Java 8 right now due to API change in java.lang
.invoke between java 7 and 8.
You'll also need maven 3.x

Compiling the code
----------

As we have to rewrite bytecode after compilation in invokedynamic module, it's important that you compile the code from Mven
(and not your IDE). a
simple `mvn clean package` will do it.

Run the code
----------

You can run the code from maven as well or from you IDE (be careful that your IDE doesn't recompile the code or if it does be
 sure that `process-class`maven phase is also launch to weave the code)

Go to proxy module and enter

`mvn exec:java -Dexec.mainClass="org.jboss.research.proxy.Main"`

The result stack trace will be :


    java.lang.Throwable
        at org.jboss.research.proxy.UserServiceImpl.addUser(UserServiceImpl.java:12)
        at sun.reflect.GeneratedMethodAccessor8.invoke(Unknown Source)
        at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
        at java.lang.reflect.Method.invoke(Method.java:606)
        at org.jboss.research.proxy.EffectiveCallAdvice.chain(EffectiveCallAdvice.java:15)
        at org.jboss.research.proxy.Container$AdviceContextImpl.call(Container.java:26)
        at org.jboss.research.proxy.CheckRoleAdvice.chain(CheckRoleAdvice.java:19)
        at org.jboss.research.proxy.Container$AdviceContextImpl.call(Container.java:26)
        at org.jboss.research.proxy.Container$1.invoke(Container.java:45)
        at com.sun.proxy.$Proxy18.addUser(Unknown Source)
        at org.jboss.research.proxy.Main.main(Main.java:12)

Go to the Invokedynamic module and launch

`mvn exec:java -Dexec.mainClass="org.jboss.research.invokedynamic.Main"`

Stack trace will be less verbose :

    java.lang.Throwable
        at org.jboss.research.invokedynamic.UserServiceImpl.addUser(UserServiceImpl.java:18)
        at org.jboss.research.invokedynamic.Main.main(Main.java:45)


Going further
---------

From this code I hope to produce a JBoss Weld version using partly InvokeDynamic to simplify the framewrok,
provides better performance (to be tested) and better stack traces or users.
