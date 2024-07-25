package arrow_demo.domain

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow_demo.infrastructure.PortEntity
import com.sksamuel.tribune.core.Parser
import com.sksamuel.tribune.core.Parsers
import com.sksamuel.tribune.core.compose
import com.sksamuel.tribune.core.doubles.double
import com.sksamuel.tribune.core.filter
import com.sksamuel.tribune.core.longs.long
import com.sksamuel.tribune.core.map


data class Port(val id: PortId, val name: PortName, val location: PortLocation, val availability: PortAvailability) {
    companion object {
        val parser: Parser<PortEntity, Port, DataProductError> =
            Parser.compose(
                PortId.parser.contramap { it.id },
                PortName.parser.contramap { it.name },
                PortLocation.parser.contramap { it.location },
                PortAvailability.parser.contramap { it.availability },
                ::Port
            )
    }
}

@JvmInline
value class PortId(val value: Long) {
    companion object {
        val parser: Parser<String?, PortId, DataProductError> =
            Parsers
                .nonBlankString { PortFieldNotFound("id") }
                .long { PortIdInvalid(it) }
                .map { PortId(it) }
    }
}

@JvmInline
value class PortName(val value: String) {
    companion object {
        val parser: Parser<String?, PortName, DataProductError> =
            Parsers
                .nonBlankString { PortFieldNotFound("name") }
                .map { PortName(it) }
    }
}

@JvmInline
value class PortLocation(val value: String) {
    companion object {
        val parser: Parser<String?, PortLocation, DataProductError> =
            Parsers
                .nonBlankString { PortFieldNotFound("location") }
                .map { PortLocation(it) }
    }
}

@JvmInline
value class PortAvailability(val value: Double) {
    operator fun compareTo(other: PortAvailability): Int = value.compareTo(other.value)

    companion object {
        val parser: Parser<String?, PortAvailability, DataProductError> =
            Parsers
                .nonBlankString { PortFieldNotFound("availability") }
                .double { PortAvailabilityInvalid(it) }
                .filter({ it >= 0.0 }, { NegativeAvailability })
                .map { PortAvailability(it) }
    }
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