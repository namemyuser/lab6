package repository;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface JSONRepoCRUD<T> {
    /**
     *
     *    Adds a new object to the database.
     *
     *    <br>
     *    <br>
     *
     *    PRE:
     *    <br>
     *          Receives an object of given type
     *    <br>
     *    <br>
     *
     *    EXECUTION:
     *    <br>
     *          Gets a list of all object, appends the new object to the list, and rewrites the whole list
     *
     *    <br>
     *    <br>
     *
     *    POST:
     *    <br>
     *          Returns the inserted object
     *
     *
     *    @param obj is an Object of given type
     *    @return returns the newly inserted object
     * */
    T create(T obj) throws IOException;


    /**
     *
     *    Returns all objects from the database of the given type
     *
     *    <br>
     *    <br>
     *
     *    PRE:
     *    <br>
     *          None
     *    <br>
     *    <br>
     *
     *    EXECUTION:
     *    <br>
     *          Gets a list of all objects of type T and returns them.
     *    <br>
     *    <br>
     *
     *    POST:
     *    <br>
     *          Returns a list containing all objects of type T.
     *
     *
     *    @return returns a list containing all objects of type T
     * */
    List<T> getAll() throws FileNotFoundException;


    /**
     *     Updates a given object from the repo by ID.
     *
     *     <br>
     *     <br>
     *
     *     PRE:
     *     <br>
     *
     *
     *         Both parameters are of the same given object type(either course, student or teacher)
     *
     *
     *     <br>
     *     <br>
     *
     *     EXECUTION:
     *     <br>
     *          If the object exists, it is deleted and replaced by the new correct copy by reinsertion, else error is
     *          thrown.
     *
     *     <br>
     *     <br>
     *
     *

     *     POST:
     *     <br>
     *          Object is updated.
     *
     * @param oldVersion is an Object of given type
     * @param newVersion is an Object of given type
     * @return null, if the given ID does not exist, else the new, updated version is returned
     * */
    T update(T oldVersion, T newVersion) throws IOException;


    /**
     *
     *    Deletes an object of type T from the database.
     *
     *    <br>
     *    <br>
     *
     *    PRE:
     *    <br>
     *          Receives the object, that has to be deleted.
     *    <br>
     *    <br>
     *
     *    EXECUTION:
     *    <br>
     *          Gets the list of all objects of type T, deletes the object passed as parameter and rewrites the new list.
     *    <br>
     *    <br>
     *
     *    POST:
     *    <br>
     *          The object passed as parameter is deleted from the Database.
     *
     *    @param obj is an Object of given type
     * */
    void delete(T obj) throws IOException;

}
