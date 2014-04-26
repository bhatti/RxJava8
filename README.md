#RxJava8 - Reactive Extensions using Java 8

##Overview

Implementation of core features of reactive extension using Java 8. It is inspired by Microsoft's Rx library at https://rx.codeplex.com/, but it doesn't support all of their APIs.



##Building
 - Download and install <a href="http://www.gradle.org/downloads">Gradle</a>.
 - Download and install <a href="http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html">Java 8</a>.
 - Checkout code using 

```
git clone git@github.com:bhatti/RxJava8.git
```

 - Compile and build jar file using

```
./gradlew jar
```
 
 - For now, you will have to copy and add jar file manually in your application.


##Version
 - 0.1 : experimental
 
##License
 - MIT

##How To Guide

### Creating Observable from Array of objects
```java 
   Observable.from("Erica", "Matt", "John", "Mike").subscribe(System.out::println, 
      Throwable::printStackTrace, () -> System.out.println("done"));
```


### Creating Observable from Collection
```java 
   List<String> names = Arrays.asList("Erica", "Matt", "John", "Mike", 
      "Scott", "Alex", "Jeff", "Brad"); 
   Observable.from(names).subscribe(System.out::println, 
      Throwable::printStackTrace, () -> System.out.println("done"));
```


### Creating Observable from Stream
```java 
   Stream<String> names = Stream.of("Erica", "Matt", "John", "Mike", 
      "Scott", "Alex", "Jeff", "Brad"); 
   // note third argument for onComplete is optional
   Observable.from(names).subscribe(name -> System.out.println(name), 
      error -> error.printStackTrace());
```


### Creating Observable from Iterator
```java 
   Stream<String> names = Stream.of("Erica", "Matt", "John", "Mike", 
      "Scott", "Alex", "Jeff", "Brad"); 
   Observable.from(names.iterator()).subscribe(name -> System.out.println(name), 
      error -> error.printStackTrace());
```


### Creating Observable from Spliterator
```java 
   List<String> names = Arrays.asList("Erica", "Matt", "John", "Mike", 
      "Scott", "Alex", "Jeff", "Brad"); 
   Observable.from(names.spliterator()).subscribe(System.out::println, 
      Throwable::printStackTrace);
```


### Creating Observable from a single object
```java 

   Observable.just("value").subscribe(v -> System.out.println(v), 
      error -> error.printStackTrace());
   // if a single object is collection, it would be treated as a single entity, e.g.
   Observable.just(Arrays.asList(1, 2, 3)).subscribe( num -> System.out.println(num), 
      error -> error.printStackTrace());
```

### Creating Observable for an error
```java 
   Observable.throwing(new Error("test error")).subscribe(System.out::println, 
      error -> System.err.println(error));
   // this will print error 
```


### Creating Observable from a consumer function
```java 
   Observable.create(observer -> {
      for (String name : names) {
         observer.onNext(name);
      }
      observer.onCompleted();
   }).subscribe(System.out::println, Throwable::printStackTrace);
```

### Creating Observable from range
```java 
   // Creates range of numbers starting at from until it reaches to exclusively
   Observable.range(4, 8).subscribe(num -> System.out.println(num), 
      error -> error.printStackTrace());
   // will print 4, 5, 6, 7
```


### Creating empty Observable - it would call onCompleted right away
```java 
   Observable.empty().subscribe(System.out::println, 
      Throwable::printStackTrace, () -> System.out.println("Completed"));
```



### Creating never Observable - it would not call any of call back methods
```java 
   Observable.never().subscribe(System.out::println, Throwable::printStackTrace);
```



### Changing Scheduler
By default Observable notifies observer asynchronously using thread-pool scheduler but you can change default scheduler as follows:

#### Using thread-pool scheduler
```java 
   Observable.from("Erica", "Matt", "John").subscribeOn(Scheduler.getThreadPoolScheduler()).
      subscribe(System.out::println, Throwable::printStackTrace);
```

#### Using new-thread scheduler - it will create new thread 
```java 
   Observable.from("Erica", "Matt", "John").subscribeOn(Scheduler.getNewThreadScheduler()).
      subscribe(System.out::println, Throwable::printStackTrace);
```

#### Using timer thread with interval - it will notify at each interval
```java 
   Observable.from("Erica", "Matt", "John").subscribeOn(Scheduler.getTimerSchedulerWithMilliInterval(1000)).
      subscribe(System.out::println, Throwable::printStackTrace);
   // this will print each name every second
```

#### Using immediate scheduler 
This scheduler will call callback functions right away on the same thread. You can use this scheduler for a smaller amount of data that you want to consume synchronously. However, you cannot unsubscribe as it runs on the same thread.
```java 
   Observable.from("Erica", "Matt", "John").
      subscribeOn(Scheduler.getThreadPoolSchedulergetImmediateScheduler()).
      subscribe(System.out::println, Throwable::printStackTrace);
   // this will print each name every second
```


### Transforming 
Observables keep sequence of items as streams and they support map/flatMap operation as supported by standard Stream class, e.g.
### Map
```java 
   Observable.from("Erica", "Matt", "John").map(name -> name.hashCode()).
      subscribe(System.out::println, Throwable::printStackTrace);
```


### FlatMap
FlatMap merges list of lists into a single list when doing transformation, e.g.
```java 
   Stream<List<Integer>> integerListStream = Stream.of( Arrays.asList(1, 2), 
      Arrays.asList(3, 4), Arrays.asList(5));
   Observable.from(integerListStream).flatMap(integerList -> integerList.stream()).
      subscribe(System.out::println, Throwable::printStackTrace);
```

### Filtering
Observables supports basic filtering support as provided by Java Streams, e.g.
### Filter
```java 
   Observable.from("Erica", "Matt", "John", "Mike", "Scott", 
      "Alex", "Jeff", "Brad").filter(name -> name.startsWith("M")).
      subscribe(System.out::println, Throwable::printStackTrace);
   // This will only print Matt and Mike
```


### Skip - skips given number of elements
```java 
   Stream<String> names = Stream.of("Erica", "Matt", "John", "Mike", 
      "Scott", "Alex", "Jeff", "Brad"); 
   Observable.from(names).skip().subscribe(System.out::println, 
      Throwable::printStackTrace);
   // This will skip Erica and John
```

### Limit
```java 
   Stream<String> names = Stream.of("Erica", "Matt", "John", "Mike", 
      "Scott", "Alex", "Jeff", "Brad"); 
   Observable.from(names).limit(2).subscribe(System.out::println, 
      Throwable::printStackTrace);
   // This will only print first two names
```


### Distinct - removes duplicates
```java 
   Stream<String> names = Stream.of("Erica", "Matt", "John", "Erica");
   Observable.from(names).distinct.subscribe(System.out::println, 
      Throwable::printStackTrace);
   // This will print Erica only once
```



### Merge - concates two observable data
```java 
   Observable<Integer> observable1 = Observable.from(Stream.of(1, 2, 3));
   Observable<Integer> observable2 = Observable.from(Stream.of(4, 5, 6));
   observable1.merge(observable2).subscribe(System.out::println, 
      Throwable::printStackTrace);
   // This will print 1, 2, 3, 4, 5, 6
```



## Contact
  Email bhatti AT plexobject DOT com for any questions or suggestions.

