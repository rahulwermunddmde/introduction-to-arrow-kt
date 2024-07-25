package arrow_demo.infrastructure

data class DataProductEntity(
    val id: String?,
    val name: String?,
    val ports: List<PortEntity>?,
)