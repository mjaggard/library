package org.jaggard.library

sealed class LibraryException : Exception {
    constructor() : super()
    constructor(message: String) : super(message)

    class NotInLibraryException : LibraryException()
    class NoSuchBookException : LibraryException()
    class AlreadyReturnedException : LibraryException()
    class CannotBeBorrowedException(message: String)  : LibraryException(message)
}

