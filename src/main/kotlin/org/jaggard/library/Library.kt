package org.jaggard.library

/**
 * This class is responsible for the datastore. Normally this would be in a database of some kind.
 * Each map represents a database index.
 * The byIsbn is the primary index which includes pointers to the real data.
 * Other maps represent indexes in the database which just point to the main index.
 * This, along with the functions in this class ensure that we never end up with an inconsistent state and,
 * if anything did go wrong, there's only one instance of each book stored in this class.
 */
class Library(private val byIsbn: Map<String, Book> = mutableMapOf()) {

}
