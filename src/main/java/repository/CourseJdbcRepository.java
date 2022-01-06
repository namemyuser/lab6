package repository;

import model.Course;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseJdbcRepository implements JSONRepoCRUD<Course>{

    public CourseJdbcRepository() {
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
    public Course create(Course obj) throws IOException {
        String url = "jdbc:jtds:sqlserver://DESKTOP-J23O4V7:49671/StudentRegistrationSystem;instance=SQLEXPRESS";
        String user = "cosmin";
        String password = "Coco4286";

        try {
            Connection connection = DriverManager.getConnection(url, user, password);
            String insertCoursesData = "INSERT INTO Courses " + "VALUES(" + obj.getCourseID() + ", " +
                                                                        "'" + obj.getName() + "'" + ", " +
                                                                            obj.getTeacher() + ", " +
                                                                            obj.getMaxEnrollment() + ", " +
                                                                            obj.getCredits() + ")";

            Statement writeCoursesData = connection.createStatement();
            writeCoursesData.executeUpdate(insertCoursesData);
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
    public List<Course> getAll() throws FileNotFoundException {
        String url = "jdbc:jtds:sqlserver://DESKTOP-J23O4V7:49671/StudentRegistrationSystem;instance=SQLEXPRESS";
        String user = "cosmin";
        String password = "Coco4286";
    try{
        Connection connection = DriverManager.getConnection(url, user, password);

        String coursesData = "SELECT * FROM Courses";
        Statement getStudentData = connection.createStatement();
        ResultSet coursesResultSet = getStudentData.executeQuery(coursesData);
        List<Course> allStudents = new ArrayList<>();

        while(coursesResultSet.next()){
            //get course student
            String selectCourseStudents = "SELECT studentId FROM StudentCourses WHERE courseId = " + coursesResultSet.getInt("courseId");

            Statement getCourseStudents = connection.createStatement();
            ResultSet courseStudentsResultSet = getCourseStudents.executeQuery(selectCourseStudents);

            List<Integer> courseStudents = new ArrayList<>();

            while(courseStudentsResultSet.next()){
                courseStudents.add(courseStudentsResultSet.getInt("studentId"));
            }

            //create object course
            Course resultingCourseUnit = new Course(coursesResultSet.getInt("courseID"),
                    coursesResultSet.getString("name"),
                    coursesResultSet.getInt("teacher"),
                    coursesResultSet.getInt("maxEnrollment"),
                    courseStudents,
                    coursesResultSet.getInt("credits"));

            allStudents.add(resultingCourseUnit);
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
    public Course update(Course oldVersion, Course newVersion) throws IOException {
        String url = "jdbc:jtds:sqlserver://DESKTOP-J23O4V7:49671/StudentRegistrationSystem;instance=SQLEXPRESS";
        String user = "cosmin";
        String password = "Coco4286";

        try {
            Connection connection = DriverManager.getConnection(url, user, password);

            List<Integer> addStudentCourse = new ArrayList<>();
            List<Integer> deleteStudentCourse = new ArrayList<>();

            //left join on students between old and new enrollment List
            for(Integer student:oldVersion.getStudentsEnrolled()){
                if(!newVersion.getStudentsEnrolled().contains(student)){
                    deleteStudentCourse.add(student);
                }
            }

            //left join on student between new and old enrollment List
            for(Integer student: newVersion.getStudentsEnrolled()){
                if(!oldVersion.getStudentsEnrolled().contains(student)){
                    addStudentCourse.add(student);
                }
            }

            //adds new courses to connections
            if(addStudentCourse.size() != 0){
                for(Integer student: addStudentCourse){
                    String insertStudentCourse = "INSERT INTO StudentCourses VALUES(" + student + ", " + newVersion.getCourseID() + ")";
                    Statement writeStudentCourseData = connection.createStatement();
                    writeStudentCourseData.executeUpdate(insertStudentCourse);
                }
            }

            //deletes old course from connections
            if(deleteStudentCourse.size() != 0){
                for(Integer student: deleteStudentCourse){
                    String deleteCourseFromStudent = "DELETE FROM StudentCourses WHERE studentID = " + student + " AND " + "courseID = "+ newVersion.getCourseID();
                    Statement deleteCourseFromStudentData = connection.createStatement();
                    deleteCourseFromStudentData.executeUpdate(deleteCourseFromStudent);
                }
            }

            String updateCourse = "UPDATE Courses " +
                                  "SET name = " + "'" + newVersion.getName() + "'" + ", " +
                                      "teacher = " + newVersion.getTeacher() + ", " +
                                      "maxEnrollment = " + newVersion.getMaxEnrollment() + ", " +
                                      "credits = " + newVersion.getCredits() +
                                  " WHERE courseID = " + oldVersion.getCourseID();
            Statement updateCourseData = connection.createStatement();
            updateCourseData.executeUpdate(updateCourse);

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
    public void delete(Course obj) throws IOException {
        String url = "jdbc:jtds:sqlserver://DESKTOP-J23O4V7:49671/StudentRegistrationSystem;instance=SQLEXPRESS";
        String user = "cosmin";
        String password = "Coco4286";

        try {
            Connection connection = DriverManager.getConnection(url, user, password);

            String deleteCourseFromStudent = "DELETE FROM StudentCourses WHERE  courseID = " + obj.getCourseID();
            Statement deleteCourseFromStudentData = connection.createStatement();
            deleteCourseFromStudentData.executeUpdate(deleteCourseFromStudent);

            String deleteCourse = "DELETE FROM Courses WHERE courseID = " + obj.getCourseID();
            Statement deleteCourseData = connection.createStatement();
            deleteCourseData.executeUpdate(deleteCourse);

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
