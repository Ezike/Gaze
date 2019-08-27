package dev.sasikanth.nasa.apod.data

sealed class NetworkState {

    object Loading : NetworkState()

    // Since we are using persistence as source of truth, we don't need to pass data here
    // We are mainly using it for network state item in grid view
    object Success : NetworkState()

    // To handle invalid syntax failure, 400
    object BadRequestError : NetworkState()

    object NotFoundError : NetworkState()

    object ServerError : NetworkState()

    // God only knows what happened ¯\_(ツ)_/¯
    data class UnknownError(val errorCode: Int) : NetworkState()

    // To handle exceptions for network states, separating failure cases from exceptions
    data class Exception(val message: String? = null) : NetworkState()
}
