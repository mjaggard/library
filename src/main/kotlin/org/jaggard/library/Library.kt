package org.jaggard.library

import java.util.*
import java.util.concurrent.ConcurrentHashMap

/*
 * This class is responsible for the datastore. Normally this would be in a database of some kind.
 * Each map represents a database index.
 * The byIsbn is the primary index which includes pointers to the real data.
 * Other maps represent indexes in the database which just point to the main index.
 * This, along with the functions in this class ensure that we never end up with an inconsistent state and,
 * if anything did go wrong, there's only one instance of each book stored in this class.
 */
class Library(
    private val byIsbn: MutableMap<String, Book> = ConcurrentHashMap(),
    // The below could later be a keyword index search to allow "rowling" rather than exactly "J.K. Rowling"
    private val byAuthor: MutableMap<String, MutableSet<String>> = TreeMap(String.CASE_INSENSITIVE_ORDER) //Ignore case.
) {

    fun addBook(book: Book) {
        val previous = byIsbn.putIfAbsent(book.isbn, book)
        if (previous != null) {
            // ConcurrentHashMap cannot contain null as a genuine value
            throw IllegalStateException("Book with ISBN '${book.isbn}' is already in this library.")
        }
        addToIndex(byAuthor, book, book.author)
    }

    private fun addToIndex(index: MutableMap<String, MutableSet<String>>, book: Book, indexedValue: String) {
        // If the index doesn't contain the indexedValue then we add a new set containing this book's ISBN.
        val existingBooksByIndex = index.putIfAbsent(indexedValue, mutableSetOf(book.isbn))
        // If it did contain the indexedValue then we add the book's ISBN to the set.
        existingBooksByIndex?.add(book.isbn)
    }

    fun size() = byIsbn.size

    fun findByAuthor(author: String): List<Book> =
        byAuthor[author]
            .orEmpty()
            .mapNotNull { byIsbn[it] } //NotNull should not be needed but better than ever getting a NullPointerException
            .toList()
}
