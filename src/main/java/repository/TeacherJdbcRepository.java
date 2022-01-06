package repository;

import model.Course;
import model.Teacher;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TeacherJdbcRepository implements JSONRepoCRUD<Teacher>{
    public TeacherJdbcRepository() {
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
    public Teacher create(Teacher obj) throws IOException {
        String url = "jdbc:jtds:sqlserver://DESKTOP-J23O4V7:49671/StudentRegistrationSystem;instance=SQLEXPRESS";
        String user = "cosmin";
        String password = "Coco4286";

        try {

            String insertTeachersData = "INSERT INTO Teacher " + "VALUES (" + "'" + obj.getFirstName() + "'" + ", " +
                                                                              "'" + obj.getLastName() + "'" + ", " +
                                                                                    obj.getTeacherID() + ")";

            Connection connection = DriverManager.getConnection(url, user, password);
            Statement writeTeachersData = connection.createStatement();
            writeTeachersData.executeUpdate(insertTeachersData);
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
    public List<Teacher> getAll() throws FileNotFoundException {
        String url = "jdbc:jtds:sqlserver://DESKTOP-J23O4V7:49671/StudentRegistrationSystem;instance=SQLEXPRESS";
        String user = "cosmin";
        String password = "Coco4286";

        try {
            Connection connection = DriverManager.getConnection(url, user, password);
            String teacherData = "SELECT * FROM Teacher";
            Statement getTeacherData = connection.createStatement();
            ResultSet teacherResultSet = getTeacherData.executeQuery(teacherData);
            List<Teacher> allTeachers = new ArrayList<>();

            while(teacherResultSet.next()){

                //fetch teacher courses
                String selectTeacherCourse = "SELECT courseID FROM Courses WHERE teacher = " +
                                              teacherResultSet.getString("teacherID");

                Statement getTeacherCourses = connection.createStatement();
                ResultSet teacherCoursesResultSet = getTeacherCourses.executeQuery(selectTeacherCourse);
                List<Integer> teacherCourses = new ArrayList<>();

                while(teacherCoursesResultSet.next()){
                    teacherCourses.add(teacherCoursesResultSet.getInt("courseId"));
                }

                //teacher object creation
                Teacher resultingTeacherUnit = new Teacher(teacherResultSet.getInt("teacherID"),
                                                           teacherResultSet.getString("firstName"),
                                                           teacherResultSet.getString("lastName"),
                                                           teacherCourses);

                allTeachers.add(resultingTeacherUnit);

            }

            return allTeachers;
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
    public Teacher update(Teacher oldVersion, Teacher newVersion) throws IOException {
        String url = "jdbc:jtds:sqlserver://DESKTOP-J23O4V7:49671/StudentRegistrationSystem;instance=SQLEXPRESS";
        String user = "cosmin";
        String password = "Coco4286";

        try {
            Connection connection = DriverManager.getConnection(url, user, password);

            String updateTeacher = "UPDATE Teacher Set firstName = " + "'" + newVersion.getFirstName() + "'" + ", " +
                                                      "lastName = " + "'" + newVersion.getLastName() + "'" +
                                                  "WHERE teacherID = " + newVersion.getTeacherID();

            Statement writeStudentData = connection.createStatement();
            writeStudentData.executeUpdate(updateTeacher);

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
    public void delete(Teacher obj) throws IOException {
        String url = "jdbc:jtds:sqlserver://DESKTOP-J23O4V7:49671/StudentRegistrationSystem;instance=SQLEXPRESS";
        String user = "cosmin";
        String password = "Coco4286";

        try {
            Connection connection = DriverManager.getConnection(url, user, password);

            //get teacher courses
            String selectTeacherCourses = "SELECT courseID FROM Courses WHERE teacher = " + obj.getTeacherID();

            Statement getTeacherCourses = connection.createStatement();
            ResultSet teacherCoursesResultSet = getTeacherCourses.executeQuery(selectTeacherCourses);

            List<Integer> teacherCourses = new ArrayList<>();

            while(teacherCoursesResultSet.next()){
                teacherCourses.add(teacherCoursesResultSet.getInt("courseID"));
            }

            //deletes the courses the teacher is assigned to
            List<Course> courses = new ArrayList<>();
            for (Integer courseId: teacherCourses) {
                Course deleteThisCourse = new Course();
                deleteThisCourse.setCourseID(courseId);
                courses.add(deleteThisCourse);
            }

            CourseJdbcRepository courseRepository = new CourseJdbcRepository();
            for(Course deleteThisCourse: courses) {
                courseRepository.delete(deleteThisCourse);
            }

            //deletes the teacher himself
            String deleteTeacher = "DELETE FROM Teacher WHERE teacherID = " + obj.getTeacherID();
            Statement deleteTeacherData = connection.createStatement();
            deleteTeacherData.executeUpdate(deleteTeacher);

            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
