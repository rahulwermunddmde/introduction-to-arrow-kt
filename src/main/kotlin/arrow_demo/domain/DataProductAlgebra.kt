package arrow_demo.domain

import arrow.core.Either
import arrow.core.Option

interface DataProductAlgebra {
    fun findById(id: DataProductId): DataProduct
    fun findByIdSafe(id: DataProductId): DataProduct?
    fun findByIdOption(id: DataProductId): Option<DataProduct>
    fun findByEither(id: DataProductId): Either<DataProductError, DataProduct>

    fun findAll(): List<DataProduct>

    fun findPortByDataProductName(dataProductName: DataProductName, portId: PortId): Either<DataProductError, Port>
}