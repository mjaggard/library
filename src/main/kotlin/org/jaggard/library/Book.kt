package org.jaggard.library

/**
 * I decided to make this immutable to start with, if that becomes a problem later, then I'll change it.
 */
data class Book(
    val isbn: String, // Could be validated using https://regexlib.com/REDetails.aspx?regexp_id=1747
    val author: String, // Assuming that every book has a single author for now
    val title: String,
    val reference: Boolean, // Is this book a reference book that cannot be borrowed?
)
