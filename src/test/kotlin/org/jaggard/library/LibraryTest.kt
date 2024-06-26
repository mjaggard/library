package org.jaggard.library

import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test

class LibraryTest {
    private val roundIreland = Book("ISBN 0-09-186777-0", "Tony Hawks", "Round Ireland with a fridge")
    private val harryPotter = Book("ISBN 0-7475-8108-8", "J.K. Rowling", "Harry Potter and the Half-Blood Prince")
    private val harryPotter2 = Book("ISBN 978-1-4088-5566-9", "J.K. Rowling", "Harry Potter and the Chamber of Secrets")

    @Test
    fun addBooksSuccess() {
        val lib = Library()

        lib.addBook(roundIreland)
        lib.addBook(harryPotter)

        assertThat(lib.size())
            .isEqualTo(2)
    }

    @Test
    fun addBookFailure() {
        val lib = Library()

        val book2DuplicateIsbn = Book(roundIreland.isbn, "J.K. Rowling", "Harry Potter and the Half-Blood Prince")

        lib.addBook(roundIreland)
        val exception = catchIllegalStateException { lib.addBook(book2DuplicateIsbn) }
        assertThat(exception)
            .isNotNull()
            .hasMessage("Book with ISBN 'ISBN 0-09-186777-0' is already in this library.")
    }

    @Test
    fun findNoBookByAuthor() {
        val lib = Library()
        lib.addBook(roundIreland)
        lib.addBook(harryPotter)

        assertThat(lib.findByAuthor("Charles Dickens"))
            .isEmpty()
    }

    @Test
    fun findOneBookByAuthor() {
        val lib = Library()
        lib.addBook(roundIreland)
        lib.addBook(harryPotter)

        assertThat(lib.findByAuthor("Tony Hawks"))
            .containsExactly(roundIreland)
    }

    @Test
    fun findOneBookByIncorrectlyCasedAuthor() {
        val lib = Library()
        lib.addBook(roundIreland)
        lib.addBook(harryPotter)

        assertThat(lib.findByAuthor("tonY HawKs"))
            .containsExactly(roundIreland)
    }

    @Test
    fun findMultipleBookByAuthor() {
        val lib = Library()
        lib.addBook(roundIreland)
        lib.addBook(harryPotter)

        lib.addBook(harryPotter2)

        assertThat(lib.findByAuthor("J.K. Rowling"))
            .containsExactlyInAnyOrder(harryPotter, harryPotter2)
    }

    @Test
    fun findOneBookByTitle() {
        val lib = Library()
        lib.addBook(roundIreland)
        lib.addBook(harryPotter)

        assertThat(lib.findByTitle("Round Ireland with a fridge"))
            .containsExactly(roundIreland)
    }

    @Test
    fun findOneBookByIsbn() {
        val lib = Library()
        lib.addBook(roundIreland)
        lib.addBook(harryPotter)

        assertThat(lib.findByIsbn("ISBN 0-09-186777-0"))
            .isPresent
            .contains(roundIreland)
    }

    @Test
    fun findNoBookByIsbn() {
        val lib = Library()
        lib.addBook(roundIreland)
        lib.addBook(harryPotter)

        assertThat(lib.findByIsbn("ISBN 978-1-4088-5566-9"))
            .isEmpty
    }


    @Test
    fun borrowBookByIsbn() {
        val lib = Library()
        lib.addBook(roundIreland)
        lib.addBook(harryPotter)

        val result = lib.tryBorrow(roundIreland.isbn)
        assertThat(result.isSuccess)
            .isTrue
        assertThat(result.getOrNull())
            .isNotNull
            .extracting { it!!.book }
            .isEqualTo(roundIreland)

        assertThat(lib.numberCheckedOut())
            .isOne
    }

    @Test
    fun borrowBookAndReturn() {
        val lib = Library()
        lib.addBook(roundIreland)
        lib.addBook(harryPotter)

        val result = lib.tryBorrow(roundIreland)
        assertThat(result.isSuccess)
            .isTrue
        assertThat(result.getOrNull())
            .isNotNull
            .extracting { it!!.book }
            .isEqualTo(roundIreland)

        result.getOrThrow().returnBook()

        assertThat(lib.numberCheckedOut())
            .isZero
    }

    @Test
    fun borrowBookAlreadyBorrowed() {
        val lib = Library()
        lib.addBook(roundIreland)
        lib.addBook(harryPotter)

        val result = lib.tryBorrow(roundIreland)
        assertThat(result.isSuccess).isTrue
        val result2 = lib.tryBorrow(roundIreland)
        assertThat(result2.isFailure)
            .isTrue
        assertThat(result2.exceptionOrNull())
            .isNotNull
            .isOfAnyClassIn(LibraryException.NotInLibraryException::class.java)

        assertThat(lib.numberCheckedOut())
            .isOne
    }

    @Test
    fun borrowBookAlreadyBorrowedAndReturned() {
        val lib = Library()
        lib.addBook(roundIreland)
        lib.addBook(harryPotter)

        val result = lib.tryBorrow(roundIreland)
        assertThat(result.isSuccess).isTrue

        result.getOrThrow().returnBook()

        val result2 = lib.tryBorrow(roundIreland)
        assertThat(result2.isSuccess).isTrue

        assertThat(lib.numberCheckedOut())
            .isOne
    }

    @Test
    fun borrowNonExistentBook() {
        val lib = Library()
        lib.addBook(roundIreland)
        lib.addBook(harryPotter)

        val result = lib.tryBorrow(harryPotter2)
        assertThat(result.isFailure).isTrue
        assertThat(result.exceptionOrNull())
            .isNotNull
            .isOfAnyClassIn(LibraryException.NoSuchBookException::class.java)

        assertThat(lib.numberCheckedOut())
            .isZero
    }

    @Test
    fun borrowBookAndReturnTwice() {
        val lib = Library()
        lib.addBook(roundIreland)
        lib.addBook(harryPotter)

        val result = lib.tryBorrow(roundIreland)
        assertThat(result.isSuccess)
            .isTrue
        assertThat(result.getOrNull())
            .isNotNull
            .extracting { it!!.book }
            .isEqualTo(roundIreland)

        result.getOrThrow().returnBook()
        assertThat(catchThrowable { result.getOrThrow().returnBook() })
            .isNotNull
            .isOfAnyClassIn(LibraryException.AlreadyReturnedException::class.java)

        assertThat(lib.numberCheckedOut())
            .isZero
    }

    @Test
    fun borrowMultipleBooks() {
        val lib = Library()
        lib.addBook(roundIreland)
        lib.addBook(harryPotter)

        assertThat(lib.numberCheckedOut())
            .isZero

        val result = lib.tryBorrow(roundIreland)
        assertThat(result.isSuccess)
            .isTrue
        assertThat(lib.numberCheckedOut())
            .isOne

        val result2 = lib.tryBorrow(harryPotter)
        assertThat(result2.isSuccess)
            .isTrue
        assertThat(lib.numberCheckedOut())
            .isEqualTo(2)

        result.getOrThrow().returnBook()
        assertThat(lib.numberCheckedOut())
            .isOne

        result2.getOrThrow().returnBook()
        assertThat(lib.numberCheckedOut())
            .isZero
    }
}
