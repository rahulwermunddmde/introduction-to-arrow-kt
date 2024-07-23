package arrow_demo.domain

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure


data class Port(val id: PortId, val name: PortName, val location: PortLocation, val availability: PortAvailability)

@JvmInline
value class PortId(val value: Long)

@JvmInline
value class PortName(val value: String)

@JvmInline
value class PortLocation(val value: String)

@JvmInline
value class PortAvailability(val value: Double) {
    operator fun compareTo(other: PortAvailability): Int = value.compareTo(other.value)
}

@JvmInline
value class PortAvailabilityEither private constructor(val value: Double) {
    companion object {
        operator fun invoke(value: Double): Either<DataProductError, PortAvailabilityEither> =
            either {
                ensure(value >= 0.0) { NegativeAvailability }
                PortAvailabilityEither(value)
            }
    }
}