package arrow_demo

import arrow.core.raise.either
import arrow.core.raise.nullable
import arrow_demo.domain.DataProduct
import arrow_demo.domain.DataProductAlgebra
import arrow_demo.domain.DataProductId
import arrow_demo.domain.DataProductName
import arrow_demo.domain.Port
import arrow_demo.domain.PortAvailability
import arrow_demo.domain.PortAvailabilityEither
import arrow_demo.domain.PortId
import arrow_demo.domain.PortLocation
import arrow_demo.domain.PortName
import arrow_demo.repository.DATABASE
import arrow_demo.repository.DataProductRepository
import arrow_demo.service.DataProductService

fun main() {
    val dataProductRepository: DataProductAlgebra = DataProductRepository()
    val dataProductService = DataProductService(dataProductRepository)
    val dataProductId: Long = 42
//    val portLocation1 = dataProductService.retrieveNumValidPorts(DataProductId(dataProductId))
//    println(portLocation1)

    val portLocation2 = dataProductService.retrieveNumValidPortsSafe(DataProductId(dataProductId))
    println(portLocation2)

    val id1: Long = 1
    val id2: Long = 32
    val combinedLocations = dataProductService.combineLocationsSafely(DataProductId(id1), DataProductId(id2))
    println(combinedLocations)

    val combinedLocations2 = dataProductService.combineLocationsSafelyArrow(DataProductId(id1), DataProductId(id2))
    println(combinedLocations2)

    val combinedLocations3 =
        nullable { dataProductService.combineLocationsUsingRaiseContextNullable(DataProductId(id1), DataProductId(id2)) }
    println(combinedLocations3)

    val combinedLocations4 =
        either { dataProductService.combineLocationsUsingRaiseContextEither(DataProductId(id1), DataProductId(id2)) }
    println(combinedLocations4)

    val maxNumValidPortsGap = dataProductService.getNumValidPortsGapWithMaxOption(DataProductId(id2))
    println("The max availability gap is ${maxNumValidPortsGap.getOrNull()}")

    val maxNumValidPortsGap2 = dataProductService.getNumValidPortsGapWithMaxTry(DataProductId(id1))
    println("The max availability gap is ${maxNumValidPortsGap2.getOrNull()}")

    val maxNumValidPortsGap3 = dataProductService.getNumValidPortsGapWithMaxEither(DataProductId(id2))
    println("The max availability gap is $maxNumValidPortsGap3")

    val availabilityOrError = PortAvailabilityEither(-2.0)
    println(availabilityOrError)

    val ids = (1..10L).map { DataProductId(it) }.toList()
    val maxNumValidPortsGapForIds = dataProductService.getNumValidPortsGapWithMaxEitherNel(ids)
    println(maxNumValidPortsGapForIds)

    val validDataProduct = DATABASE[DataProductId(1)]!!
    val invalidDataProduct1 = DataProduct(DataProductId(1), DataProductName(" "), emptyList())
    val invalidDataProduct2 = DataProduct(
        DataProductId(1),
        DataProductName(" "),
        listOf(
            Port(PortId(1), PortName(" "), PortLocation("Hive: db.table"), PortAvailability(-1.0)),
            Port(PortId(2), PortName("Port 2"), PortLocation(" "), PortAvailability(98.0))
        )
    )

    println(dataProductService.validateDataProduct(validDataProduct))
    println(dataProductService.validateDataProduct(invalidDataProduct1))
    println(dataProductService.validateDataProduct(invalidDataProduct2))
}