package org.jaggard.library

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchIllegalStateException
import org.junit.jupiter.api.Test

class LibraryTest {

    @Test
    fun addBooksSuccess() {
        val lib = Library()
        val book = Book("ISBN 0-09-186777-0", "Tony Hawks", "Round Ireland with a fridge")
        val book2 = Book("ISBN 0-7475-8108-8", "J.K. Rowling", "Harry Potter and the Half-Blood Prince")
        lib.addBook(book)
        lib.addBook(book2)

        assertThat(lib.size())
            .isEqualTo(2)
    }

    @Test
    fun addBookFailure() {
        val lib = Library()
        val isbn = "ISBN 0-09-186777-0"
        val book = Book(isbn, "Tony Hawks", "Round Ireland with a fridge")
        val book2 = Book(isbn /*wrong*/, "J.K. Rowling", "Harry Potter and the Half-Blood Prince")
        lib.addBook(book)
        val exception = catchIllegalStateException { lib.addBook(book2) }
        assertThat(exception)
            .isNotNull()
            .hasMessage("Book with ISBN 'ISBN 0-09-186777-0' is already in this library.")
    }
}
