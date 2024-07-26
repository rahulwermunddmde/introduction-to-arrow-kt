package arrow_demo.playground

import arrow_demo.domain.DataProduct
import arrow_demo.infrastructure.DataProductEntity
import arrow_demo.util.getJsonObjectMapper
import arrow_demo.util.getYamlObjectMapper
import com.sksamuel.tribune.core.Parser
import com.sksamuel.tribune.core.Parsers
import com.sksamuel.tribune.core.ints.int
import com.sksamuel.tribune.core.map
import com.sksamuel.tribune.core.strings.notNullOrBlank

object ParserBasics {

    @JvmInline
    value class MyInt(val value: Int)

    val myIntParser: Parser<String?, MyInt, String> = Parsers.nullableString
        .notNullOrBlank { "Value must be provided" }
        .int { "Value must be an integer" }
        .map { MyInt(it) }

    val validDpEntityYaml: String = """
        id: 123
        name: "Data Product"
        ports:
          - id: 1
            name: "Port 1"
            location: "Location 1"
            availability: 98.2
          - id: 2
            name: "Port 2"
            location: "Location 2"
            availability: 86.7
    """.trimIndent()

    val validDpEntity: DataProductEntity =
        getYamlObjectMapper().readValue(validDpEntityYaml, DataProductEntity::class.java)

    val invalidDpEntityYaml: String = """
        id: 123
        ports:
          - id: 1
            name: "Port 1"
            availability: -34.6
          - id: "Bla"
            name: "Port 2"
            location: "Location 2"
            availability: "Available"
    """.trimIndent()

    val invalidDpEntity: DataProductEntity =
        getYamlObjectMapper().readValue(invalidDpEntityYaml, DataProductEntity::class.java)

    val validDpEntityJson: String = """
        {
          "id": 123,
          "name": "Data Product",
          "ports": [
            {
              "id": 1,
              "name": "Port 1",
              "location": "Location 1",
              "availability": 98.2
            },
            {
              "id": 2,
              "name": "Port 2",
              "location": "Location 2",
              "availability": 86.7
            }
          ]
        }
    """.trimIndent()

    val validDpEntityFromJson: DataProductEntity =
        getJsonObjectMapper().readValue(validDpEntityJson, DataProductEntity::class.java)

    @JvmStatic
    fun main(args: Array<String>) {
        println(myIntParser.parse("123"))
        println(myIntParser.parse("abc"))
        println(DataProduct.parser.parse(validDpEntity))
        println(DataProduct.parser.parse(invalidDpEntity))
        println(DataProduct.parser.parse(validDpEntityFromJson))
    }
}