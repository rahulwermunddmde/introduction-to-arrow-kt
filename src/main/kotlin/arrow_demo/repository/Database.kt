package arrow_demo.repository

import arrow_demo.domain.DataProduct
import arrow_demo.domain.DataProductId
import arrow_demo.domain.DataProductName
import arrow_demo.domain.Port
import arrow_demo.domain.PortAvailability
import arrow_demo.domain.PortId
import arrow_demo.domain.PortLocation
import arrow_demo.domain.PortName

val DATABASE: Map<DataProductId, DataProduct> =
    mapOf(
        DataProductId(1) to DataProduct(
            DataProductId(1),
            DataProductName("Baby products"),
            listOf(
                Port(
                    PortId(1),
                    PortName("Purchase history"),
                    PortLocation("Hive: baby_products.purchase_history"),
                    PortAvailability(85.0)
                )
            )
        ),
        DataProductId(2) to DataProduct(
            DataProductId(2),
            DataProductName("Organic food"),
            listOf(
                Port(
                    PortId(2),
                    PortName("Quality control"),
                    PortLocation("HDFS: /data/organic_food/quality_control"),
                    PortAvailability(99.0)
                )
            )
        ),
        DataProductId(3) to DataProduct(
            DataProductId(3),
            DataProductName("Customer reviews"),
            listOf(
                Port(
                    PortId(3),
                    PortName("Average score"),
                    PortLocation("Snowflake: customer_reviews.average_score"),
                    PortAvailability(95.0)
                )
            ),
        ),
        DataProductId(4) to DataProduct(
            DataProductId(4),
            DataProductName("Logistics overview"),
            listOf(
                Port(
                    PortId(4),
                    PortName("Transportation plans"),
                    PortLocation("Kafka: logistics_overview.transportation_plans"),
                    PortAvailability(90.0)
                ),
                Port(
                    PortId(5),
                    PortName("Warehouse capacity"),
                    PortLocation("Hive: logistics_overview.warehouse_capacity"),
                    PortAvailability(80.0)
                )
            )
        ),
    )