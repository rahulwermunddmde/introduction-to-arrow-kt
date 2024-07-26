package arrow_demo.domain

sealed interface DataProductError {
    fun message(): String
}

data class DataProductFieldNotFound(val field: String): DataProductError {
    override fun message(): String = "Required data product field $field not found"
}

data class DataProductIdInvalid(val id: String) : DataProductError {
    override fun message(): String = "Data product ID $id is invalid"
}

data class DataProductNameInvalid(val name: String) : DataProductError {
    override fun message(): String = "Data product name $name is invalid"
}

data object DataProductEmptyPorts : DataProductError {
    override fun message(): String = "Data product must have at least one port"
}

data class DuplicatePorts(val ids: Set<PortId>) : DataProductError {
    override fun message(): String = "Duplicate output port IDs encountered: $ids"
}

data class PortFieldNotFound(val field: String): DataProductError {
    override fun message(): String = "Required port field $field not found"
}

data class PortIdInvalid(val id: String) : DataProductError {
    override fun message(): String = "Port ID $id is invalid"
}

data class PortNameInvalid(val name: String) : DataProductError {
    override fun message(): String = "Port name $name is invalid"
}

data class PortLocationInvalid(val location: String) : DataProductError {
    override fun message(): String = "Port location $location is invalid"
}

data class PortAvailabilityInvalid(val availability: String) : DataProductError {
    override fun message(): String = "Port availability $availability is invalid"
}

data class DataProductNotFound(val field: String) : DataProductError {
    override fun message(): String = "Data product with field $field not found"
}

data class PortNotFound(val field: String) : DataProductError {
    override fun message(): String = "Port with field $field not found"
}

data object ImpossibleAvailability : DataProductError {
    override fun message(): String = "Port availability has to be in the range [0, 100]"
}

data class GenericError(val cause: String) : DataProductError {
    override fun message(): String = "An error occurred: $cause"
}