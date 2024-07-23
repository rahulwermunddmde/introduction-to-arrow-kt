package arrow_demo.repository

import arrow.core.Either
import arrow.core.Option
import arrow.core.left
import arrow.core.none
import arrow.core.raise.either
import arrow.core.raise.ensureNotNull
import arrow.core.right
import arrow.core.toOption
import arrow_demo.domain.DataProduct
import arrow_demo.domain.DataProductAlgebra
import arrow_demo.domain.DataProductError
import arrow_demo.domain.DataProductId
import arrow_demo.domain.DataProductName
import arrow_demo.domain.DataProductNotFound
import arrow_demo.domain.GenericError
import arrow_demo.domain.Port
import arrow_demo.domain.PortId
import arrow_demo.domain.PortNotFound

class DataProductRepository : DataProductAlgebra {

    override fun findById(id: DataProductId): DataProduct {
        val maybeDataProduct: DataProduct? = DATABASE[id]
        if (maybeDataProduct != null) {
            return maybeDataProduct
        } else {
            throw NoSuchElementException("Data product with id $id not found")
        }
    }

    override fun findByIdSafe(id: DataProductId): DataProduct? =
        try {
            DATABASE[id]
        } catch (e: Exception) {
            // For any unforeseen database access errors
            null
        }

    override fun findByIdOption(id: DataProductId): Option<DataProduct> =
        try {
            DATABASE[id].toOption()
        } catch (e: Exception) {
            none()
        }

    override fun findByEither(id: DataProductId): Either<DataProductError, DataProduct> =
        try {
            DATABASE[id]?.right() ?: DataProductNotFound(id.toString()).left()
        } catch (e: Exception) {
            GenericError(e.message ?: "Unknown error").left()
        }

    override fun findAll(): List<DataProduct> {
        return DATABASE.values.toList()
    }

    override fun findPortByDataProductName(
        dataProductName: DataProductName,
        portId: PortId
    ): Either<DataProductError, Port> =
        either {
            val allDataProducts = runCatching { findAll() }.fold(
                onSuccess = { it },
                onFailure = { raise(GenericError(it.message?: "Unknown error")) }
            )
            val dataProduct =
                ensureNotNull(allDataProducts.find { it.name == dataProductName }) { DataProductNotFound(dataProductName.value) }
            val port =
                ensureNotNull(dataProduct.ports.find { it.id == portId }) { PortNotFound(portId.value.toString()) }
            port
        }
}