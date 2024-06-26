package org.jaggard.library

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchIllegalStateException
import org.junit.jupiter.api.Test

class LibraryTest {
    private val roundIreland = Book("ISBN 0-09-186777-0", "Tony Hawks", "Round Ireland with a fridge")
    private val harryPotter = Book("ISBN 0-7475-8108-8", "J.K. Rowling", "Harry Potter and the Half-Blood Prince")

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
        val harryPotter2 = Book("ISBN 978-1-4088-5566-9", "J.K. Rowling", "Harry Potter and the Chamber of Secrets")
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
}
