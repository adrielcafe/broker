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
    <img width="80%" src="https://github.com/adrielcafe/broker/raw/master/broker-flow.png?raw=true">
</p>

```kotlin
class MyActivity : AppCompatActivity(), GlobalBroker.Subscriber {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        subscribe<MyEvent>(this) { event ->
            // Handle event
        }
    }
}

class MyViewModel : ViewModel(), GlobalBroker.Publisher {

    fun doSomething() {
        publish(MyEvent(payload))
    }
}
```

**Features**
* Helps to decouple your code: publishers are loosely coupled to subscribers, and don't even need to know of their existence;
* Works great with Activity, Fragment, Service, Custom View, ViewModel...;
* Provides a [global instance](#global-pubsub) by default and lets you create [your own instances](#local-pubsub);
* Also provides extension functions to help avoid boilerplate code;
* [Lifecycle-aware](#lifecycle-aware): subscribe and unsubscribe to events automatically;
* Thread-safe: you can publish/subscribe from any thread;
* Fast: all work is done outside the main thread and the events are delivered through a Coroutines Flow.
* Small: ~30kb

## Usage
Take a look at the [sample app](https://github.com/adrielcafe/broker/tree/master/sample/src/main/java/cafe/adriel/broker/sample) for working examples.

### Creating events
Events (a.k.a Topic, Message) can be represented as `object` (without payload) and `data class` (with payload).
```kotlin
object EventA
data class EventB(val message: String)
```

You can also group your events inside a `sealed class`, this way you can organize events by module, feature, scope, or similar.
```kotlin
sealed class MyEvent {
    object EventA : MyEvent()
    data class EventB(val message: String) : MyEvent()
}
```

### Global Pub/Sub
Broker provides a global instance by default with some useful extension functions.

Call `GlobalBroker.subscribe<YourEvent>()` to subscribe to an event and `GlobalBroker.unsubscribe()` to unsubscribe to all events. 

To subscribe, you should pass as parameters:
* The subscriber (usually the current class but can be a `String`, `Int`, `object`...)
* A `CoroutineScope` (tip: use the built-in [lifecycleScope and viewModelScope](https://developer.android.com/topic/libraries/architecture/coroutines))
* An *optional* `CoroutineContext` to run your lambda (default is `Dispatchers.Main`)
* A lambda used to handle the incoming events

Call `subscribe()` in `onStart()` (for Activity and Fragment) and `onAttachedToWindow()` (for Custom View), and call `unsubscribe()` in `onStop()` (for Activity and Fragment) and `onDetachedFromWindow()` (for Custom View).
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

To publish events just call `GlobalBroker.publish()` passing the event as parameter. It can be called from any thread.
```kotlin
class MyViewModel : ViewModel() {

    fun doSomething() {
        GlobalBroker.publish(MyEvent)
    }
}
```

#### GlobalBroker.Publisher & GlobalBroker.Subscriber
You can avoid some boilerplate code by implementing the `GlobalBroker.Publisher` and `GlobalBroker.Subscriber` interfaces. This also helps to identify the role of your class: is it a Publisher or Subscriber?
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

class MyViewModel : ViewModel(), GlobalBroker.Publisher {

    fun doSomething() {
        publish(MyEvent)
    }
}
```

### Local Pub/Sub
In some situations a global instance is not a good option, because of that you can also create your own Broker instance.

In the example below, we use [Koin](https://github.com/InsertKoinIO/koin) to inject a Broker instance in the `MyActivity` scope.
```kotlin
val myModule = module {

    scope<MyActivity> {
        scoped { Broker() }

        viewModel { MyViewModel(broker = get()) }
    }
}
```

And now we can inject a local Broker instance:
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

class MyViewModel(broker: Broker) : ViewModel() {

    fun doSomething() {
        broker.publish(MyEvent)
    }
}
```

#### BrokerPublisher & BrokerSubscriber
Broker class implements two interfaces: `BrokerPublisher` and `BrokerSubscriber`. You can use this to inject only the necessary behavior into your class.

Let's back to the previous example. Instead of provide a Broker instance directly we can provide two injections, one for publishers and another for subscribers.
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

Now we can inject only what our class needs:
```kotlin
class MyActivity : AppCompatActivity() {

    private val broker by instance<BrokerSubscriber>()
}


class MyViewModel(broker: BrokerPublisher) : ViewModel()
```

### Lifecycle-aware
Broker's subscribers can be [lifecycle-aware](https://developer.android.com/topic/libraries/architecture/lifecycle)! Works for global and local instances.

Instead of subscribe in `onStart()` and unsubscribe in `onStop()` just subscribe in `onCreate()` and pass the `lifecycleOnwer` as parameter. Your events will now be automatically subscribed and unsubscribed.
```kotlin
class MyActivity : AppCompatActivity(), GlobalBroker.Subscriber {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        subscribe<MyEvent>(owner = this) { event ->
            // Handle event
        }
    }
}
```

### Error handling
If the subscriber's lambda throws an error, Broker will catch it and publish the event `BrokerExceptionEvent`. Just subscribe to it if you want to handle the exceptions. 
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
