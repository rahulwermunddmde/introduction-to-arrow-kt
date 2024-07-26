package arrow_demo.service

import arrow.core.Either
import arrow.core.EitherNel
import arrow.core.Option
import arrow.core.left
import arrow.core.raise.NullableRaise
import arrow.core.raise.Raise
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.raise.ensureNotNull
import arrow.core.raise.mapOrAccumulate
import arrow.core.raise.nullable
import arrow.core.raise.option
import arrow.core.raise.result
import arrow.core.raise.zipOrAccumulate
import arrow.core.right
import arrow.core.toOption
import arrow_demo.domain.DataProduct
import arrow_demo.domain.DataProductAlgebra
import arrow_demo.domain.DataProductEmptyPorts
import arrow_demo.domain.DataProductError
import arrow_demo.domain.DataProductId
import arrow_demo.domain.DataProductNameInvalid
import arrow_demo.domain.DataProductNotFound
import arrow_demo.domain.DuplicatePorts
import arrow_demo.domain.GenericError
import arrow_demo.domain.Port
import arrow_demo.domain.PortAvailabilityInvalid
import arrow_demo.domain.PortLocation
import arrow_demo.domain.PortLocationInvalid
import arrow_demo.domain.PortNameInvalid
import arrow_demo.util.findDuplicatesBy

class DataProductService(private val dataProducts: DataProductAlgebra) {

    fun retrieveNumValidPorts(id: DataProductId): Int {
        val dataProduct = dataProducts.findById(id) // Not referentially transparent
        return try {
            dataProduct.numValidPorts()
        } catch (e: Exception) {
            0
        }
    }

    fun retrieveNumValidPortsSafe(id: DataProductId): Int {
        val dataProduct = dataProducts.findByIdSafe(id)
        return dataProduct?.numValidPorts() ?: 0
    }

    fun retrieveNumValidPortsOption(id: DataProductId): String {
        val maybeDataProduct = dataProducts.findByIdOption(id)

//        when (maybeDataProduct) {
//            is Some -> maybeDataProduct.value.numPorts() // Smart casting of Option to Some
//            is None -> 0
//        }

        return maybeDataProduct.fold(
            ifEmpty = { "No ports not found" },
            ifSome = { "Num ports: ${it.numValidPorts()}" }
        )
    }

    fun combineLocationsSafely(id1: DataProductId, id2: DataProductId): Set<PortLocation>? {
        val dataProduct1: DataProduct? = dataProducts.findByIdSafe(id1)
        val dataProduct2: DataProduct? = dataProducts.findByIdSafe(id2)
        return dataProduct1?.let { product1 ->
            dataProduct2?.let { product2 ->
                (product1.ports.map { it.location } + product2.ports.map { it.location }).toSet()
            }
        }
    }

    fun combineLocationsSafelyArrow(id1: DataProductId, id2: DataProductId): Set<PortLocation>? =
        nullable {
            val product1: DataProduct = dataProducts.findByIdSafe(id1).bind()
            val product2: DataProduct =
                ensureNotNull(dataProducts.findByIdSafe(id2)) // Either way is fine
            (product1.ports.map { it.location } + product2.ports.map { it.location }).toSet()
        }

    context(NullableRaise)
    fun combineLocationsUsingRaiseContextNullable(
        id1: DataProductId,
        id2: DataProductId
    ): Set<PortLocation> {
        val product1: DataProduct = dataProducts.findByIdSafe(id1).bind()
        val product2: DataProduct = ensureNotNull(dataProducts.findByIdSafe(id2))
        return (product1.ports.map { it.location } + product2.ports.map { it.location }).toSet()
    }

    context(Raise<DataProductError>)
    fun combineLocationsUsingRaiseContextEither(
        id1: DataProductId,
        id2: DataProductId
    ): Set<PortLocation> {
        val product1: DataProduct = dataProducts.findByEither(id1).bind()
        val product2: DataProduct =
            ensureNotNull(dataProducts.findByIdSafe(id2)) { DataProductNotFound(id2.toString()) }
        return (product1.ports.map { it.location } + product2.ports.map { it.location }).toSet()
    }

    fun getNumValidPortsGapWithMaxOption(id: DataProductId): Option<Int> {
        val maybeDataProduct = dataProducts.findByIdOption(id)
        val maybeMaxNumValidPorts =
            dataProducts.findAll().maxByOrNull { it.numValidPorts() }.toOption()
                .map { it.numValidPorts() }

        return maybeDataProduct.flatMap { product ->
            maybeMaxNumValidPorts.map { maxNumValidPorts ->
                maxNumValidPorts - product.numValidPorts()
            }
        }
    }

    fun getNumValidPortsGapWithMax2(id: DataProductId): Option<Int> =
        option {
            val dataProduct = dataProducts.findByIdOption(id).bind()
            val maxNumValidPortsDataProduct =
                dataProducts.findAll().maxByOrNull { it.numValidPorts() }.toOption().bind()
            // Alternative: ensureNotNull(dataProducts.findAll().maxBy { it.numValidPorts() })

            maxNumValidPortsDataProduct.numValidPorts() - dataProduct.numValidPorts()
        }

    fun getNumValidPortsGapWithMaxTry(id: DataProductId): Result<Int> =
        result {
            val dataProduct = runCatching { dataProducts.findById(id) }.bind()
            val maxNumValidPortsDataProduct =
                runCatching { dataProducts.findAll().maxBy { it.numValidPorts() } }.bind()

            maxNumValidPortsDataProduct.numValidPorts() - dataProduct.numValidPorts()
        }

    fun getNumValidPortsGapWithMaxEither(id: DataProductId): Either<DataProductError, Int> =
        either {
            val dataProduct = dataProducts.findByEither(id).bind()
            val maxNumValidPorts = dataProducts.findAll().maxNumValidPorts().bind()
            maxNumValidPorts - dataProduct.numValidPorts()
        }

    // EitherNel<DataProductError, Unit> == Either<NonEmptyList<DataProductError>, Unit>
    fun validateDataProduct(dataProduct: DataProduct): EitherNel<DataProductError, Unit> =
        either {
            zipOrAccumulate(
                { ensure(dataProduct.name.value.isNotBlank()) { DataProductNameInvalid(dataProduct.name.value) } },
                { ensure(dataProduct.ports.isNotEmpty()) { DataProductEmptyPorts } },
                {
                    val duplicatePortIds = dataProduct.ports.findDuplicatesBy { it.id }
                    ensure(duplicatePortIds.isEmpty()) { DuplicatePorts(duplicatePortIds.keys) }
                }
            ) { _, _, _ ->
                val processedPorts = dataProduct.ports.map(::validatePort)
                mapOrAccumulate(processedPorts) { it.bind() }
            }
        }

    fun validatePort(port: Port): EitherNel<DataProductError, Unit> =
        either {
            zipOrAccumulate(
                { ensure(port.name.value.isNotBlank()) { PortNameInvalid(port.name.value ) } },
                { ensure(port.location.value.isNotBlank()) { PortLocationInvalid(port.location.value) } },
                { ensure(port.availability.value in 0.0..100.0) { PortAvailabilityInvalid(port.availability.value.toString()) } },
            ) { _, _, _ -> }
        }

    private fun List<DataProduct>.maxNumValidPorts(): Either<GenericError, Int> =
        if (isEmpty()) {
            GenericError("No data products found").left()
        } else {
            maxBy { it.numValidPorts() }.numValidPorts().right()
        }

}