package arrow_demo.domain

data class DataProduct(
    val id: DataProductId,
    val name: DataProductName,
    val ports: List<Port>,
) {
    fun numValidPorts(): Int = ports.size
}

@JvmInline
value class DataProductId(val value: Long)

@JvmInline
value class DataProductName(val value: String)