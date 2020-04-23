[![JitPack](https://img.shields.io/jitpack/v/github/adrielcafe/broker.svg?style=for-the-badge)](https://jitpack.io/#adrielcafe/broker) 
[![Android API](https://img.shields.io/badge/api-16%2B-brightgreen.svg?style=for-the-badge)](https://android-arsenal.com/api?level=16) 
[![Github Actions](https://img.shields.io/github/workflow/status/adrielcafe/broker/main/master?style=for-the-badge)](https://github.com/adrielcafe/broker/actions)
[![Codacy](https://img.shields.io/codacy/grade/ae430c15c7834ac088b23d27e4890dc0.svg?style=for-the-badge)](https://www.codacy.com/app/adriel_cafe/broker) 
[![Codecov](https://img.shields.io/codecov/c/github/adrielcafe/broker/master.svg?style=for-the-badge)](https://codecov.io/gh/adrielcafe/broker) 
[![kotlin](https://img.shields.io/github/languages/top/adrielcafe/broker.svg?style=for-the-badge)](https://kotlinlang.org/) 
[![ktlint](https://img.shields.io/badge/code%20style-%E2%9D%A4-FF4081.svg?style=for-the-badge)](https://ktlint.github.io/) 
[![License MIT](https://img.shields.io/github/license/adrielcafe/broker.svg?style=for-the-badge&color=yellow)](https://opensource.org/licenses/MIT)  

# Broker
Broker is a [Publish-Subscribe](https://en.wikipedia.org/wiki/Publish%E2%80%93subscribe_pattern) (a.k.a Pub/Sub, EventBus) library for Android and JVM built with [Coroutines](https://github.com/Kotlin/kotlinx.coroutines).

<p align="center">
    <img src="https://github.com/adrielcafe/broker/raw/master/broker-flow.png?raw=true" style="max-width:80%;">
</p>

**Features**
* Helps to decouple your code: publishers are loosely coupled to subscribers, and don't even need to know of their existence;
* Works great with Activity, Fragment, Service, Custom View, ViewModel...;
* Provides a [global instance](#global-pubsub) by default and lets you create [your own instances](#local-pubsub);
* Also provides extension functions to help avoid boilerplate code;
* [Lifecycle-aware](#lifecycle-aware): subscribe and unsubscribe to events automatically;
* Thread-safe: you can publish/subscribe from any thread;
* Fast: all work is done outside the main thread and the events are delivered through a Coroutines Flow.

## Usage
Take a look at the [sample app](https://github.com/adrielcafe/broker/tree/master/sample/src/main/java/cafe/adriel/broker/sample) for working examples.

### Events
```kotlin
object EventA
data class EventB(val message: String)
```
```kotlin
sealed class MyEvent {
    object EventA : MyEvent()
    data class EventB(val message: String) : MyEvent()
}
```

### Global Pub/Sub
```kotlin
class MyActivity : AppCompatActivity() {

    override fun onStart() {
        super.onStart()
        GlobalBroker.subscribe<MyEvent>(this, lifecycleScope) { event ->
            // Handle event
        }
    }

    override fun onStop() {
        GlobalBroker.unsubscribe(this, lifecycleScope)
        super.onStop()
    }
}
```
```kotlin
class MyViewModel : ViewModel() {

    fun doSomething() {
        GlobalBroker.publish(MyEvent("Hello!"))
    }
}
```

#### GlobalBroker.Publisher & GlobalBroker.Subscriber
```kotlin
class MyActivity : AppCompatActivity(), GlobalBroker.Subscriber {

    override fun onStart() {
        super.onStart()
        subscribe<MyEvent>(lifecycleScope) { event ->
            // Handle event
        }
    }

    override fun onStop() {
        unsubscribe(lifecycleScope)
        super.onStop()
    }
}
```
```kotlin
class MyViewModel : ViewModel(), GlobalBroker.Publisher {

    fun doSomething() {
        publish(SampleEvent("Hello!"))
    }
}
```

### Local Pub/Sub
```kotlin
val myModule = module {

    scope<MyActivity> {
        scoped { Broker() }

        viewModel { MyViewModel(broker = get()) }
    }
}
```
```kotlin
class MyActivity : AppCompatActivity() {

    private val broker by instance<Broker>()

    override fun onStart() {
        super.onStart()
        broker.subscribe<MyEvent>(this, lifecycleScope) { event ->
            // Handle event
        }
    }

    override fun onStop() {
        broker.unsubscribe(this, lifecycleScope)
        super.onStop()
    }
}
```
```kotlin
class MyViewModel(broker: Broker) : ViewModel() {

    fun doSomething() {
        broker.publish(MyEvent("Hello!"))
    }
}
```

#### BrokerPublisher & BrokerSubscriber
```kotlin
val myModule = module {

    scope<MyActivity> {
        val broker = Broker()

        scoped<BrokerPublisher> { broker }

        scoped<BrokerSubscriber> { broker }

        viewModel { MyViewModel(broker = get<BrokerPublisher>()) }
    }
}
```
```kotlin
class MyActivity : AppCompatActivity() {

    private val broker by instance<BrokerSubscriber>()
}
```
```kotlin
class MyViewModel(broker: BrokerPublisher) : ViewModel()
```

### Lifecycle-aware
```kotlin
class MyActivity : AppCompatActivity(), GlobalBroker.Subscriber {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        subscribe<SampleEvent>(owner = this) { event ->
            // Handle event
        }
    }
}
```

### Error handling
```kotlin
subscribe<BrokerExceptionEvent>(lifecycleScope) { event ->
    // Handle error
}
```

## Import to your project
1. Add the JitPack repository in your root build.gradle at the end of repositories:
```gradle
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

2. Next, add the desired dependencies to your module:
```gradle
dependencies {
    // Core
    implementation "com.github.adrielcafe.broker:broker-core:$currentVersion"

    // Lifecycle support
    implementation "com.github.adrielcafe.broker:broker-lifecycle:$currentVersion"
}
```
Current version: [![JitPack](https://img.shields.io/jitpack/v/github/adrielcafe/broker.svg?style=flat-square)](https://jitpack.io/#adrielcafe/broker)

### Platform compatibility

|         | `broker-core` | `broker-lifecycle` |
|---------|---------------|--------------------|
| Android | ✓             | ✓                  |
| JVM     | ✓             |                    |
