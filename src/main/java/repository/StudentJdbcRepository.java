package repository;

import model.Student;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentJdbcRepository implements JSONRepoCRUD<Student>{
    public StudentJdbcRepository() {
    }

    /**
     * Adds a new object to the database.
     *
     * <br>
     * <br>
     * <p>
     * PRE:
     * <br>
     * Receives an object of given type
     * <br>
     * <br>
     * <p>
     * EXECUTION:
     * <br>
     * Gets a list of all object, appends the new object to the list, and rewrites the whole list
     *
     * <br>
     * <br>
     * <p>
     * POST:
     * <br>
     * Returns the inserted object
     *
     * @param obj is an Object of given type
     * @return returns the newly inserted object
     */
    @Override
    public Student create(Student obj) throws IOException {
        String url = "jdbc:jtds:sqlserver://DESKTOP-J23O4V7:49671/StudentRegistrationSystem;instance=SQLEXPRESS";
        String user = "cosmin";
        String password = "Coco4286";

        try {
            Connection connection = DriverManager.getConnection(url, user, password);

            String insertStudentData =  "INSERT INTO Student " + "VALUES(" + "'" + obj.getFirstName() + "'" + ", " +
                                                                             "'" + obj.getLastName() + "'" + ", " +
                                                                                   obj.getStudentId() + ", " +
                                                                                   obj.getTotalCredits() + ")";

            Statement writeStudentData = connection.createStatement();
            writeStudentData.executeUpdate(insertStudentData);

            if (obj.getEnrolledCourses() != null || obj.getEnrolledCourses().size() != 0){
                for(Integer course : obj.getEnrolledCourses()){
                    String insertStudentCourse = "INSERT INTO StudentCourses VALUES(" + obj.getStudentId() + ", " + course + ")";
                    Statement writeStudentCourseData = connection.createStatement();
                    writeStudentCourseData.executeUpdate(insertStudentCourse);
                }
            }

            connection.close();

            return obj;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Returns all objects from the database of the given type
     *
     * <br>
     * <br>
     * <p>
     * PRE:
     * <br>
     * None
     * <br>
     * <br>
     * <p>
     * EXECUTION:
     * <br>
     * Gets a list of all objects of type T and returns them.
     * <br>
     * <br>
     * <p>
     * POST:
     * <br>
     * Returns a list containing all objects of type T.
     *
     * @return returns a list containing all objects of type T
     */
    @Override
    public List<Student> getAll() throws FileNotFoundException {
        String url = "jdbc:jtds:sqlserver://DESKTOP-J23O4V7:49671/StudentRegistrationSystem;instance=SQLEXPRESS";
        String user = "cosmin";
        String password = "Coco4286";

        try {
            Connection connection = DriverManager.getConnection(url, user, password);

            String studentData = "SELECT * FROM STUDENT";
            Statement getStudentData = connection.createStatement();
            ResultSet studentResultSet = getStudentData.executeQuery(studentData);
            List<Student> allStudents = new ArrayList<>();


            while(studentResultSet.next()){
                //get student courses
                String selectStudentCourses = "SELECT courseId FROM StudentCourses WHERE studentId = " + studentResultSet.getInt("studentId");

                Statement getStudentCourses = connection.createStatement();
                ResultSet studentCoursesResultSet = getStudentCourses.executeQuery(selectStudentCourses);

                List<Integer> studentCourses = new ArrayList<>();

                while(studentCoursesResultSet.next()){
                    studentCourses.add(studentCoursesResultSet.getInt("courseId"));
                }

                //object creation
                Student resultingStudentUnit = new Student(studentResultSet.getString("firstName"),
                                                           studentResultSet.getString("lastName"),
                                                           studentResultSet.getInt("studentId"),
                                                           studentResultSet.getInt("totalCredits"),
                                                           studentCourses);

                allStudents.add(resultingStudentUnit);

            }

            connection.close();

            return allStudents;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Updates a given object from the repo by ID.
     *
     * <br>
     * <br>
     * <p>
     * PRE:
     * <br>
     * <p>
     * <p>
     * Both parameters are of the same given object type(either course, student or teacher)
     *
     *
     * <br>
     * <br>
     * <p>
     * EXECUTION:
     * <br>
     * If the object exists, it is deleted and replaced by the new correct copy by reinsertion, else error is
     * thrown.
     *
     * <br>
     * <br>
     * <p>
     * <p>
     * <p>
     * POST:
     * <br>
     * Object is updated.
     *
     * @param oldVersion is an Object of given type
     * @param newVersion is an Object of given type
     * @return null, if the given ID does not exist, else the new, updated version is returned
     */
    @Override
    public Student update(Student oldVersion, Student newVersion) throws IOException {
        String url = "jdbc:jtds:sqlserver://DESKTOP-J23O4V7:49671/StudentRegistrationSystem;instance=SQLEXPRESS";
        String user = "cosmin";
        String password = "Coco4286";

        try {
            Connection connection = DriverManager.getConnection(url, user, password);

            List<Integer> addStudentCourse = new ArrayList<>();
            List<Integer> deleteStudentCourse = new ArrayList<>();

            //left join on students between old and new enrollment List
            for(Integer course:oldVersion.getEnrolledCourses()){
                if(!newVersion.getEnrolledCourses().contains(course)){
                    deleteStudentCourse.add(course);
                }
            }

            //left join on student between new and old enrollment List
            for(Integer course: newVersion.getEnrolledCourses()){
                if(!oldVersion.getEnrolledCourses().contains(course)){
                    addStudentCourse.add(course);
                }
            }

            if(addStudentCourse.size() != 0){
                for(Integer course: addStudentCourse){
                    String insertStudentCourse = "INSERT INTO StudentCourses VALUES(" + newVersion.getStudentId() + ", " + course + ")";
                    Statement writeStudentCourseData = connection.createStatement();
                    writeStudentCourseData.executeUpdate(insertStudentCourse);
                }
            }

            if(deleteStudentCourse.size() != 0){
                for(Integer course: deleteStudentCourse){
                    String deleteCourseFromStudent = "DELETE FROM StudentCourses WHERE studentID = " + newVersion.getStudentId() + " AND " + "courseID = "+ course;
                    Statement deleteCourseFromStudentData = connection.createStatement();
                    deleteCourseFromStudentData.executeUpdate(deleteCourseFromStudent);
                }
            }

            String updateStudent = "UPDATE STUDENT " +
                                   "SET firstName = " + "'" + newVersion.getFirstName() + "'" + ", " +
                                       "lastName = " + "'" + newVersion.getLastName() + "'" + ", " +
                                       "totalCredits = " + "'" + newVersion.getTotalCredits() + "' " +
                                   " WHERE studentId = " + newVersion.getStudentId();
            Statement updateCourseData = connection.createStatement();
            updateCourseData.executeUpdate(updateStudent);

            connection.close();
            return newVersion;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Deletes an object of type T from the database.
     *
     * <br>
     * <br>
     * <p>
     * PRE:
     * <br>
     * Receives the object, that has to be deleted.
     * <br>
     * <br>
     * <p>
     * EXECUTION:
     * <br>
     * Gets the list of all objects of type T, deletes the object passed as parameter and rewrites the new list.
     * <br>
     * <br>
     * <p>
     * POST:
     * <br>
     * The object passed as parameter is deleted from the Database.
     *
     * @param obj is an Object of given type
     */
    @Override
    public void delete(Student obj) throws IOException {
        String url = "jdbc:jtds:sqlserver://DESKTOP-J23O4V7:49671/StudentRegistrationSystem;instance=SQLEXPRESS";
        String user = "cosmin";
        String password = "Coco4286";

        try {
            Connection connection = DriverManager.getConnection(url, user, password);

            String deleteStudentFromCourse = "DELETE FROM StudentCourses WHERE  studentID = " + obj.getStudentId();
            Statement deleteStudentFromCourseData = connection.createStatement();
            deleteStudentFromCourseData.executeUpdate(deleteStudentFromCourse);

            String deleteStudent = "DELETE FROM Student WHERE studentID = " + obj.getStudentId();
            Statement deleteStudentData = connection.createStatement();
            deleteStudentData.executeUpdate(deleteStudent);

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
