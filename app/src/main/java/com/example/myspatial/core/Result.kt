package com.example.myspatial.core

sealed class Result<T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error<T>(val exception: Throwable, val message: String? = null) : Result<T>()
    class Loading<T> : Result<T>()

    fun isSuccess(): Boolean = this is Success
    fun isError(): Boolean = this is Error
    fun isLoading(): Boolean = this is Loading

    fun <R> map(transform: (T) -> R): Result<R> {
        return when (this) {
            is Success -> Success(transform(data))
            is Error -> Error(exception, message)
            is Loading -> Loading()
        }
    }

    fun <R> flatMap(transform: (T) -> Result<R>): Result<R> {
        return when (this) {
            is Success -> transform(data)
            is Error -> Error(exception, message)
            is Loading -> Loading()
        }
    }

    fun getOrNull(): T? = (this as? Success)?.data
    fun getOrDefault(default: T): T = (this as? Success)?.data ?: default
    fun exceptionOrNull(): Throwable? = (this as? Error)?.exception
}
