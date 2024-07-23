package arrow_demo.domain

sealed interface DataProductError {
    fun message(): String
}

data class DataProductNameInvalid(val name: DataProductName) : DataProductError {
    override fun message(): String = "Data product name ${name.value} is invalid"
}

data object DataProductEmptyPorts : DataProductError {
    override fun message(): String = "Data product must have at least one port"
}

data class PortNamesInvalid(val names: List<PortName>) : DataProductError {
    override fun message(): String = "One or more port names in [${names.joinToString { it.value }}] are invalid"
}

data class PortAvailabilitiesInvalid(val availabilities: List<PortAvailability>) : DataProductError {
    override fun message(): String =
        "One or more port availabilities in [${availabilities.joinToString { it.value.toString() }}] are invalid"
}

data class PortLocationsInvalid(val locations: List<PortLocation>) : DataProductError {
    override fun message(): String =
        "One or more port locations in [${locations.joinToString { it.value }}] are invalid"
}

data class DataProductNotFound(val field: String) : DataProductError {
    override fun message(): String = "Data product with field $field not found"
}

data class PortNotFound(val field: String) : DataProductError {
    override fun message(): String = "Port with field $field not found"
}

data object NegativeAvailability : DataProductError {
    override fun message(): String = "Port availability cannot be negative"
}

data class GenericError(val cause: String) : DataProductError {
    override fun message(): String = "An error occurred: $cause"
}