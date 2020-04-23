package cafe.adriel.broker

data class BrokerExceptionEvent(
    val subscriber: Any,
    val event: Any,
    val error: Throwable
)
