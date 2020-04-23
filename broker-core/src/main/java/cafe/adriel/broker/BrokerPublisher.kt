package cafe.adriel.broker

interface BrokerPublisher {

    fun publish(event: Any)
}
