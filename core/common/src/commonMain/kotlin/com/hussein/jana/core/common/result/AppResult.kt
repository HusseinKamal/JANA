package com.hussein.jana.core.common.result

sealed interface AppResult<out T> {
    data class Success<T>(val data: T) : AppResult<T>
    data class Error(val error: AppError) : AppResult<Nothing>
    data object Loading : AppResult<Nothing>
}

sealed interface AppError {
    data object NoInternet : AppError
    data object Timeout : AppError
    data class Server(val code: Int, val message: String?) : AppError
    data class Unknown(val throwable: Throwable?) : AppError
}

fun <T, R> AppResult<T>.map(transform: (T) -> R): AppResult<R> = when (this) {
    is AppResult.Success -> AppResult.Success(transform(data))
    is AppResult.Error   -> this
    AppResult.Loading    -> AppResult.Loading
}
