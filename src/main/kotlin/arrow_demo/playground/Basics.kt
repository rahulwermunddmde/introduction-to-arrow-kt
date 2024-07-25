package arrow_demo.playground

import arrow.core.Either
import arrow.core.Option
import arrow.core.flatMap
import arrow.core.left
import arrow.core.none
import arrow.core.right
import arrow.core.some
import arrow.core.toOption
import arrow_demo.util.filter
import arrow_demo.util.filterNot
import arrow_demo.util.filterNotOrElse
import arrow_demo.util.filterOrElse

object Basics {
    fun getValue(): String? = null

    val nullableValue: String? = getValue()
    val asResult: Result<String> = runCatching { nullableValue!! }
    val asOption: Option<String> = nullableValue.toOption()
    val asEither: Either<String, String> = nullableValue.run { this?.right() ?: "Value is null".left() }
    val asEitherThrowable: Either<Throwable, String> = Either.catch { nullableValue!! }

    @JvmStatic
    fun main(args: Array<String>) {
        println("------------- NULLABLE --------------")
        println(nullableValue?.length?.toString())
        println(
            nullableValue
                ?.firstOrNull()
                ?.takeIf { it.uppercase() == "A" }
                ?.toString()
        )
        println(nullableValue?.let { it.length / 2 })
        println(nullableValue?.takeIf { it.length > 10 })
        println(nullableValue?.takeUnless { it.length > 10 })

        println("------------- RESULT --------------")
        println(asResult.map { it.length.toString() })
        println(asResult.filter { it.length > 10 })
        println(asResult.filterNot { it.length > 10 })
        println(
            asResult
                .flatMap { runCatching { it.firstOrNull()!! } }
                .filter { it.uppercase() == "A" }
                .map { it.toString() }
        )

        println("------------- OPTION --------------")
        println(asOption.map { it.length.toString() })
        println(asOption.filter { it.length > 10 })
        println(asOption.filterNot { it.length > 10 })
        println(
            asOption
                .flatMap { it.firstOrNull().toOption() }
                .filter { it.uppercase() == "A" }
                .map { it.toString() }
        )
        println(nullableValue.some())
        println(none<String>())

        println("------------- EITHER --------------")
        println(asEither.map { it.length.toString() })
        println(asEither.filterOrElse({ it.length > 10 }, { "Not long enough" }))
        println(asEither.filterNotOrElse({ it.length > 10 }, { "Too long" }))
        println(
            asEither
                .flatMap { it.firstOrNull().toOption().toEither { "Empty string" } }
                .filterOrElse({ it.uppercase() == "A" }, { "Not starting with A" })
                .map { it.toString() }
        )
        println(asEither.swap())

        println("-----------------")
        println(nullableValue)
        println(asResult)
        println(asOption)
        println(asEither)
        println(asEitherThrowable)
    }
}