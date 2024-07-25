package arrow_demo.domain

import arrow_demo.infrastructure.DataProductEntity
import com.sksamuel.tribune.core.Parser
import com.sksamuel.tribune.core.Parsers
import com.sksamuel.tribune.core.collections.list
import com.sksamuel.tribune.core.compose
import com.sksamuel.tribune.core.longs.long
import com.sksamuel.tribune.core.map

data class DataProduct(
    val id: DataProductId,
    val name: DataProductName,
    val ports: List<Port>,
) {
    fun numValidPorts(): Int = ports.count { it.availability.value > 75.0 }

    companion object {
        val parser: Parser<DataProductEntity, DataProduct, DataProductError> =
            Parser.compose(
                DataProductId.parser.contramap { it.id },
                DataProductName.parser.contramap { it.name },
                Parser.list(Port.parser).contramap { it.ports ?: emptyList() },
                ::DataProduct
            )
    }
}

@JvmInline
value class DataProductId(val value: Long) {
    companion object {
        val parser: Parser<String?, DataProductId, DataProductError> =
            Parsers
                .nonBlankString { DataProductFieldNotFound("id") }
                .long { DataProductIdInvalid(it) }
                .map { DataProductId(it) }
    }
}

@JvmInline
value class DataProductName(val value: String) {
    companion object {
        val parser: Parser<String?, DataProductName, DataProductError> =
            Parsers
                .nonBlankString { DataProductFieldNotFound("name") }
                .map { DataProductName(it) }
    }
}