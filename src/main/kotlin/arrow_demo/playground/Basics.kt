package arrow_demo.playground

import arrow.core.Either
import arrow.core.Option
import arrow.core.firstOrNone
import arrow.core.flatMap
import arrow.core.getOrElse
import arrow.core.left
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
    val asResult: Result<String> = runCatching { getValue()!! }
    val asOption: Option<String> = getValue().toOption()
    val asEither: Either<String, String> =
        getValue().run { this?.right() ?: "Value is null".left() }
    val resultAsEither: Either<Throwable, String> = Either.catch { getValue()!! }

    val regex: Regex = "^[A-D].*$".toRegex()

    @JvmStatic
    fun main(args: Array<String>) {
        println("----------------- OLD SCHOOL TRY-CATCH -----------------")
        println(
            try {
                getValue()!!
            } catch (e: Exception) {
                e.message
            }
        )

        println(
            try {
                val value = getValue()!!
                if (regex.matches(value)) {
                    val firstChar = value.dropWhile { it <= 'Z' }.firstOrNull()
                    if (firstChar != null) {
                        val range = firstChar..'z'
                        range.joinToString { it.toString() }
                    } else {
                        "Empty string"
                    }
                } else {
                    "Not matching regex"
                }
            } catch (e: Exception) {
                e.message
            }
        )

        println("------------- NULLABLE --------------")
        println("Chaining: " + nullableValue?.length?.toString())
        println("Mapping: " + nullableValue?.let { it.length / 2 })
        println("Filter: " + nullableValue?.takeIf { it.length > 10 })
        println("Filter not: " + nullableValue?.takeUnless { it.length > 10 })
        println("More complex chaining: " +
                nullableValue
                    ?.takeIf { regex.matches(it) }
                    ?.dropWhile { it <= 'Z' }
                    ?.firstOrNull()
                    ?.rangeTo('z')
                    ?.joinToString { it.toString() }
        )

        println("------------- RESULT --------------")
        println("Mapping/Chaining: " + asResult.map { it.length.toString() })
        println("Filter: " + asResult.filter { it.length > 10 })
        println("Filter not: " + asResult.filterNot { it.length > 10 })
        println("More complex chaining: " +
            asResult
                .filter { regex.matches(it) }
                .map { it.dropWhile { c -> c <= 'Z' } }
                .mapCatching { it.firstOrNull()!! }
                .map { it..'z' }
                .map { it.joinToString { c -> c.toString() } }
        )

        println("------------- OPTION --------------")
        println("Mapping/Chaining: " + asOption.map { it.length.toString() })
        println("Filter: " + asOption.filter { it.length > 10 })
        println("Filter not: " + asOption.filterNot { it.length > 10 })
        println("More complex chaining: " +
            asOption
                .filter { regex.matches(it) }
                .map { it.dropWhile { c -> c <= 'Z' } }
                .flatMap { it.firstOrNull().toOption() }
                .map { it..'z' }
                .map { it.joinToString { c -> c.toString() } }
        )

        val list = listOf(null, 1, 2, 3)
        fun <T> List<T>.firstOrElse(default: T): T = firstOrNull() ?: default
        println(list.firstOrElse(0))

        fun <T> List<T>.firstOrElse2(default: T): T = firstOrNone().getOrElse { default }
        println(list.firstOrElse2(0))

        println(nullableValue.some())
        println(nullableValue.some().filterNot { it == null })

        println("------------- EITHER --------------")
        println("Mapping/Chaining: " + asEither.map { it.length.toString() })
        println("Filter: " + asEither.filterOrElse({ it.length > 10 }, { "Not long enough" }))
        println("Filter not: " + asEither.filterNotOrElse({ it.length > 10 }, { "Too long" }))
        println("More complex chaining: " +
            asEither
                .filterOrElse({ regex.matches(it) }, { "Not matching regex" })
                .map { it.dropWhile { c -> c <= 'Z' } }
                .flatMap { it.firstOrNull()?.right() ?: "Empty string".left() }
                .map { it..'z' }
                .map { it.joinToString { c -> c.toString() } }
        )
        println("Swapping: " + asEither.swap())

        println("-----------------")
        println(nullableValue)
        println(asResult)
        println(asOption)
        println(asEither)
        println(resultAsEither)
    }
}