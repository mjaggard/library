package org.jaggard.library

import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentSkipListMap
import java.util.concurrent.ConcurrentSkipListSet

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
    private val byAuthor: MutableMap<String, MutableSet<String>> = ConcurrentSkipListMap(String.CASE_INSENSITIVE_ORDER),
    private val byTitle: MutableMap<String, MutableSet<String>> = ConcurrentSkipListMap(String.CASE_INSENSITIVE_ORDER),
    private val checkedOut: MutableSet<String> = ConcurrentSkipListSet()
) {

    // We don't actually need a response here.
    // If we did, a Result would be more suitable than throwing an exception if the book already exists.
    fun addBook(book: Book) {
        val previous = byIsbn.putIfAbsent(book.isbn, book)
        if (previous != null) {
            // ConcurrentHashMap cannot contain null as a genuine value
            throw IllegalStateException("Book with ISBN '${book.isbn}' is already in this library.")
        }
        addToIndex(byAuthor, book, book.author)
        addToIndex(byTitle, book, book.title)
    }

    private fun addToIndex(index: MutableMap<String, MutableSet<String>>, book: Book, indexedValue: String) {
        // If the index doesn't contain the indexedValue then we add a new set containing this book's ISBN.
        val existingBooksByIndex = index.putIfAbsent(indexedValue, mutableSetOf(book.isbn))
        // If it did contain the indexedValue then we add the book's ISBN to the set.
        existingBooksByIndex?.add(book.isbn)
    }

    fun size() = byIsbn.size

    private fun findByIndex(index: Map<String, Set<String>>, indexedValue: String) =
        index[indexedValue]
            .orEmpty()
            .mapNotNull { byIsbn[it] } //NotNull should not be needed but better than ever getting a NullPointerException
            .toList()

    fun findByAuthor(author: String): List<Book> = findByIndex(byAuthor, author)

    fun findByTitle(title: String): List<Book> = findByIndex(byTitle, title)

    fun findByIsbn(isbn: String): Optional<Book> = Optional.ofNullable(byIsbn[isbn])

    fun tryBorrow(book: Book) = tryBorrow(book.isbn)

    fun tryBorrow(isbn: String): Result<Borrowed> {
        val book = byIsbn[isbn] ?: return Result.failure(LibraryException.NoSuchBookException())

        if (checkedOut.add(isbn))
            return Result.success(object : Borrowed {
                override val book = book
                private var alreadyReturned = false

                override fun returnBook() {
                    if (alreadyReturned)
                        throw LibraryException.AlreadyReturnedException()
                    if (!checkedOut.remove(book.isbn))
                        throw IllegalStateException("Should not be possible")
                    alreadyReturned = true
                }
            })

        return Result.failure(LibraryException.NotInLibraryException())
    }
}
