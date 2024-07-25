package arrow_demo.util

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.flatMap
import arrow.core.raise.result

inline fun <T> Result<T>.filter(p: (T) -> Boolean): Result<T> =
    result {
        val t = bind()
        run {
            if (p(t)) {
                Result.success(t)
            } else {
                Result.failure(NoSuchElementException("Predicate does not hold for $t"))
            }
        }.bind()
    }

inline fun <T> Result<T>.filterNot(p: (T) -> Boolean): Result<T> =
    result {
        val t = bind()
        run {
            if (!p(t)) {
                Result.success(t)
            } else {
                Result.failure(NoSuchElementException("Predicate does not hold for $t"))
            }
        }.bind()
    }

inline fun <A, B> Either<A, B>.filterOrElse(predicate: (B) -> Boolean, default: () -> A): Either<A, B> =
    flatMap { if (predicate(it)) Right(it) else Left(default()) }

inline fun <A, B> Either<A, B>.filterNotOrElse(predicate: (B) -> Boolean, default: () -> A): Either<A, B> =
    flatMap { if (!predicate(it)) Right(it) else Left(default()) }