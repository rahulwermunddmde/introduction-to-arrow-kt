package arrow_demo.playground

import arrow.core.Either
import arrow.core.Option
import arrow.core.flatMap
import arrow.core.getOrElse
import arrow.core.left
import arrow.core.right
import arrow.core.toOption
import arrow_demo.util.filter
import arrow_demo.util.filterNot
import arrow_demo.util.filterNotOrElse
import arrow_demo.util.filterOrElse

object Basics {
    fun getValue(): String? = "Hello, world!"

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

        println("------------- EITHER --------------")
        println(asEither.map { it.length.toString() })
        println(asEither.filterOrElse({ it.length > 10 }, { "Not long enough" }))
        println(asEither.filterNotOrElse({ it.length > 10 }, { "Too long" }))
        println(
            asEither
                .flatMap { it.firstOrNull().toOption().toEither { "Empty string" } }
                .flatMap {
                    if (it.uppercase() == "A") it.right() else "Not starting with A".left()
                }
                .map { it.toString() }
                .getOrElse { "Null string" }
        )

        println("-----------------")
        println(nullableValue)
        println(asResult)
        println(asOption)
        println(asEither)
        println(asEitherThrowable)
    }
}