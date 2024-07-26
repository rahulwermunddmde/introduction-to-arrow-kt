package arrow_demo.util

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.NonEmptyList
import arrow.core.flatMap

inline fun <T> Result<T>.filter(p: (T) -> Boolean): Result<T> {
    val t = getOrNull()
    return if (t != null && p(t)) this else Result.failure(NoSuchElementException("Predicate does not hold for $t"))
}

inline fun <T> Result<T>.filterNot(p: (T) -> Boolean): Result<T> =
    filter { !p(it) }

inline fun <A, B> Either<A, B>.filterOrElse(predicate: (B) -> Boolean, default: () -> A): Either<A, B> =
    flatMap { if (predicate(it)) Right(it) else Left(default()) }

inline fun <A, B> Either<A, B>.filterNotOrElse(predicate: (B) -> Boolean, default: () -> A): Either<A, B> =
    filterOrElse({ !predicate(it) }, default)

fun <T> combineNonEmptyLists(
    first: NonEmptyList<T>,
    second: NonEmptyList<T>,
): NonEmptyList<T> = first + second

inline fun <T, K> Collection<T>.findDuplicatesBy(crossinline selector: (T) -> K): Map<K, Int> =
    groupingBy(selector).eachCount().filterValues { it > 1 }