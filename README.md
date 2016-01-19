# Auto Java
Java is a great language but as nothing is perfect it also has a few limitations. On the other hand it offers some ways to overcome its own shortcomings. With source code and byte code generation in place we can implement features which are only available in other languages. This library adds traits, AOP and auto-generated bound properties to Java.

## Multiple inheritance with traits
The first problem which we will address stems from the fact that Java does not support multiple inheritance. The recommended way to achieve a similar result is to combine interfaces with delegation. But delegation when it is used often can be tedious. Traits are here to help us ease this problem.

Lets say you have a class which contains some state and behavior and supposed to be a part of some other classes. In contrast to a plain interface with default methods it also contains state. In languages like Scala we would simply inherit from a trait but in Java we are limited to interfaces. The _Observable_ class serves as a good example of such situation.

``` java
@Trait
public class ConcreteObservable implements Observable {

    private final List<Observer> observers = Lists.newArrayList();

    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(Observable observable) {
        for (Observer observer : observers) {
            observer.onChange(observable);
        }
    }
}
```

As you maybe noticed we use a special annotation called `Trait` to denote a _trait_. The support for _traits_ is implemented as a two stage process. First, an annotation processor is used to generate an interface which can be implemented by the classes that want to include this trait. Second, the byte code generation facility comes into play to inject implementations of the methods that are inherited from the generated interface. It simply injects an instance of a class (which represents a trait) and delegates all method calls to it. Actually we will end up with the same result which we could achieve doing this by hands.

``` java
public class ExampleObservable implements ConcreteObservableTrait {

    public void doSomething() {
        notifyObservers(this);
    }

    public void doSomethingElse() {
        notifyObservers(this);
    }
}
```

When annotation processor generates an interface of the trait it includes all interfaces implemented by the original class. Here is the interface that it produces.

``` java
@Trait(
    impl = ConcreteObservable.class
)
public interface ConcreteObservableTrait extends Observable {
  default void addObserver(Observer observer) {
    throw new UnsupportedOperationException("Stab!!!");}

  default void removeObserver(Observer observer) {
    throw new UnsupportedOperationException("Stab!!!");}

  default void notifyObservers(Observable observable) {
    throw new UnsupportedOperationException("Stab!!!");}
}
```

So the new interface can be safely used wherever interfaces implemented by the original class are used.

``` java
public interface Observer {
    void onChange(Observable observable);
}
```

## Injecting behavior with AOP
_Traits_ can also be used to inject behavior into classes. To achieve this goal the trait class has to implement a specific interface called `AroundAspect`.

``` java
public interface AroundAspect {
    Object around(CallableMethod method, Object target, Object[] args) throws Throwable;
}
```

In the following example we inject a transactional _aspect_ into a target class. All methods that have the `Transactional` annotation will be surrounded by a transaction management code.

``` java
@Trait
public class TransactionalService implements AroundAspect {

    private TransactionManager transactionManager;

    public TransactionManager getTransactionManager() {
        return transactionManager;
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override
    public Object around(CallableMethod callableMethod, Object o, Object[] args) throws Throwable {
        if (isTransactional(callableMethod)) {
            checkNotNull(transactionManager).beginTransaction();
            try {
                Object result = callableMethod.call(o, args);
                transactionManager.commitTransaction();
                return result;
            } catch(Throwable e) {
                transactionManager.rollbackTransaction();
                throw e;
            }
        }
        return callableMethod.call(o, args);
    }

    private boolean isTransactional(CallableMethod callableMethod) {
        for (Annotation ann : callableMethod.getAnnotations()) {
            if (ann instanceof Transactional) {
                return true;
            }
        }
        return false;
    }
}
```

What's also nice in this approach is that when we use _traits_ to implement AOP we can have _introductions_ (injected methods) for free. In the example above we introduced a setter for inserting instances of `TransactionManager`.

``` java
public interface TransactionManager {
    void beginTransaction();
    void commitTransaction();
    void rollbackTransaction();
}
```

Due to the way the method interception works the only available _advice_ type is _around_.

## Auto-generated bound properties
Java has a notion of bound properties. A good example where it may be useful is UI binding. Bound properties are generally easy to implement but often lead to very verbose and repetitive code. So what we can do instead is some code generation magic. While it is also possible to achieve with _traits_ the solution will be slightly inefficient due to involved _reflection_. We can do better by generating the code that we would otherwise write by hands.

In the following example in order to make a class observable we tell it to implement the `HasBoundProperties` interface and then we annotate all setters that should be modified with the `PropertySetter` annotation.

``` java
public class ExampleProperties implements HasBoundProperties {

    private int width;
    private int height;

    public ExampleProperties(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    @PropertySetter
    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    @PropertySetter
    public void setHeight(int height) {
        this.height = height;
    }
}
```

## Using the library
The library consists of 3 artifacts: _core_, _processor_ and _generator_. The _core_ artifact contains all compile time dependencies. _Processor_ includes APT code and only needed when you plan to use _traits_. _Generator_ is always required. Below you will find an example of a Gradle build script which uses all features. For the maven integration please check [this](https://github.com/icon-Systemhaus-GmbH/javassist-maven-plugin) project.

``` groovy
buildscript {
    repositories {
        mavenLocal()
        jcenter()
    }
    dependencies {
        classpath 'com.darylteo.gradle:javassist-plugin:0.4.1'
        classpath 'com.tsaplin.autojava:generator:0.0.5'
    }
}

plugins {
    id 'java'
    id 'net.ltgt.apt' version '0.5'
    id 'idea'
}

repositories {
    mavenLocal()
    jcenter()
}

sourceCompatibility = 1.8
targetCompatibility = 1.8

task transform1(type: com.darylteo.gradle.javassist.tasks.TransformationTask) {
    classpath += configurations.compile
    from sourceSets.main.output.classesDir
    into sourceSets.main.output.classesDir
    transformation = new com.tsaplin.autojava.trait.TraitTransformer()
}

task transform2(type: com.darylteo.gradle.javassist.tasks.TransformationTask) {
    classpath += configurations.compile
    from sourceSets.main.output.classesDir
    into sourceSets.main.output.classesDir
    transformation = new com.tsaplin.autojava.boundproperty.BoundPropertyTransformer()
}

project.tasks.classes.dependsOn(project.tasks.transform1, project.tasks.transform2)

task wrapper(type: Wrapper) {
    gradleVersion = '2.10'
}

dependencies {
    compile 'com.tsaplin.autojava:core:0.0.5'
    compile 'com.google.guava:guava:18.0'
    apt 'com.tsaplin.autojava:processor:0.0.5'
    testCompile 'org.mockito:mockito-all:1.10.19'
    testCompile 'junit:junit:4.12'
}
```

All suggestions and contributions are always welcome.
