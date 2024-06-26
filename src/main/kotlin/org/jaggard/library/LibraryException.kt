package org.jaggard.library

sealed class LibraryException : Exception() {
    class NotInLibraryException : LibraryException()
    class NoSuchBookException : LibraryException()
    class AlreadyReturnedException : LibraryException()
}

