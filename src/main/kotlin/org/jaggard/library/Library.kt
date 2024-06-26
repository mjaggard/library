package org.jaggard.library

import java.util.concurrent.ConcurrentHashMap

/**
 * This class is responsible for the datastore. Normally this would be in a database of some kind.
 * Each map represents a database index.
 * The byIsbn is the primary index which includes pointers to the real data.
 * Other maps represent indexes in the database which just point to the main index.
 * This, along with the functions in this class ensure that we never end up with an inconsistent state and,
 * if anything did go wrong, there's only one instance of each book stored in this class.
 */
class Library(private val byIsbn: MutableMap<String, Book> = ConcurrentHashMap()) {

    fun addBook(book: Book) {
        val previous = byIsbn.putIfAbsent(book.isbn, book)
        if (previous != null) {
            throw IllegalStateException("Book with ISBN '${book.isbn}' is already in this library.")
        }
    }

    fun size() = byIsbn.size
}
