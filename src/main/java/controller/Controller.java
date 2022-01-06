package controller;

import model.Course;
import model.Student;
import model.Teacher;
import repository.CourseJdbcRepository;
import repository.StudentJdbcRepository;
import repository.TeacherJdbcRepository;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Controller {
    private final CourseJdbcRepository coursesRepository;
    private final StudentJdbcRepository studentsRepository;
    private final TeacherJdbcRepository teachersRepository;

    public Controller() {
        this.coursesRepository = new CourseJdbcRepository();
        this.studentsRepository = new StudentJdbcRepository();
        this.teachersRepository = new TeacherJdbcRepository();
    }



    public List<Course> getAllCourses(){
        try {
            return coursesRepository.getAll();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }



    public List<Student> getAllStudents(){
        try {
            return studentsRepository.getAll();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }


    public List<Teacher> getAllTeachers(){
        try {
            return teachersRepository.getAll();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     *
     *    Returns all students enrolled for a certain course.
     *
     *    <br>
     *    <br>
     *
     *    PRE:
     *    <br>
     *          ID of course is given.
     *    <br>
     *    <br>
     *
     *    EXECUTION:
     *    <br>
     *          Filters students for students, that are enrolled for the course
     *    <br>
     *    <br>
     *
     *    POST:
     *    <br>
     *          Returns all students, that are enrolled for a course.
     *
     *    @param theCourse is an Object of given type
     *    @return List of students enrolled for a certain course
     * */
    public List<Student> getStudentsEnrolledForCourse(Integer theCourse){

        try {
            List<Student> allStudents = studentsRepository.getAll();
            return Objects.requireNonNull(allStudents).stream().filter(student -> student.getEnrolledCourses().contains(theCourse)).collect(Collectors.toList());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     *
     *    Registers a student to a course.
     *
     *    <br>
     *    <br>
     *
     *    PRE:
     *    <br>
     *          Student and Course exist.
     *    <br>
     *    <br>
     *
     *    EXECUTION:
     *    <br>
     *          Appends student ID to course enrollment and course id to student courses.
     *    <br>
     *    <br>
     *
     *    POST:
     *    <br>
     *          Course is assigned to student and student is assigned to course.
     *
     *    @param student is an Object of type Student
     *    @param course is an Object of type Course
     *    @return true if student is registered to course, else false
     * */
    public boolean registerStudentToCourse(Student student, Course course) throws Exception{

        try {
            if(!this.studentsRepository.getAll().contains(student) || !this.coursesRepository.getAll().contains(course)){
                return false;
            }
        } catch (FileNotFoundException e) {
            throw new Exception("Student or Course does not exist");
        }

        if(course.getStudentsEnrolled().size()==course.getMaxEnrollment()){
            throw new Exception("Course is full");
        }

        else if(student.getTotalCredits() + course.getCredits() > 30){
            throw new Exception("Student has too many credits");
        }

        else{
            Course updatedCourse = new Course();
            updatedCourse.setCourseID(course.getCourseID());
            updatedCourse.setCredits(course.getCourseID());
            updatedCourse.setMaxEnrollment(course.getMaxEnrollment());
            updatedCourse.setTeacher(course.getTeacher());
            updatedCourse.setName(course.getName());


            List<Integer> newStudentList = new ArrayList<>(course.getStudentsEnrolled());
            newStudentList.add((int) student.getStudentId());
            updatedCourse.setStudentsEnrolled(newStudentList);


            this.coursesRepository.update(course, updatedCourse);


            Student updatedStudent = new Student();

            updatedStudent.setLastName(student.getLastName());
            updatedStudent.setFirstName(student.getFirstName());
            updatedStudent.setStudentId(student.getStudentId());
            updatedStudent.setTotalCredits(student.getTotalCredits());

            List<Integer> newCoursesList = new ArrayList<>(student.getEnrolledCourses());
            newCoursesList.add(course.getCourseID());
            updatedStudent.setEnrolledCourses(newCoursesList);

            this.studentsRepository.update(student, updatedStudent);


            return true;
        }

    }


    /**
     *
     *    Modifies a certain course number of credits.
     *
     *    <br>
     *    <br>
     *
     *    PRE:
     *    <br>
     *          New number of credits is given and course exists.
     *    <br>
     *    <br>
     *
     *    EXECUTION:
     *    <br>
     *          Updates credit number in source, then alters total number of credits a student has.
     *    <br>
     *    <br>
     *
     *    POST:
     *    <br>
     *          Course is updated and all students are updated to the new number of credits.
     *
     *    @param course is a course Object
     *    @param newCourse is an Integer

     * */
    public void modifyCourseCredits(Course course, Integer newCourse){

        Course updatedCourse = new Course();
        updatedCourse.setCourseID(course.getCourseID());
        updatedCourse.setCredits(newCourse);
        updatedCourse.setMaxEnrollment(course.getMaxEnrollment());
        updatedCourse.setTeacher(course.getTeacher());
        updatedCourse.setStudentsEnrolled(course.getStudentsEnrolled());
        updatedCourse.setName(course.getName());

        try {
            this.coursesRepository.update(course, updatedCourse);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int courseCreditDifference = course.getCredits() - newCourse;

        try {
            for(Student student: this.studentsRepository.getAll()){
                if(student.getEnrolledCourses().contains(course.getCourseID())){
                    Student updatedStudent = new Student();

                    updatedStudent.setLastName(student.getLastName());
                    updatedStudent.setFirstName(student.getFirstName());
                    updatedStudent.setStudentId(student.getStudentId());

                    updatedStudent.setEnrolledCourses(student.getEnrolledCourses());

                    updatedStudent.setTotalCredits(student.getTotalCredits() - courseCreditDifference);

                    try {
                        this.studentsRepository.update(student, updatedStudent);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     *
     *    Deletes a course from the database.
     *
     *    <br>
     *    <br>
     *
     *    PRE:
     *    <br>
     *          Course exists.
     *    <br>
     *    <br>
     *
     *    EXECUTION:
     *    <br>
     *          Course is deleted, student and teacher are removed from course if they were assigned to that course.
     *    <br>
     *    <br>
     *
     *    POST:
     *    <br>
     *          Course is deleted, student and teacher are removed from that course.
     *
     *    @param course is a Course
     * */
    public void deleteCourse(Course course){

        try {
            this.coursesRepository.delete(course);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            for(Teacher teacher: this.teachersRepository.getAll()){
                if(teacher.getCourses().contains(course.getCourseID())){
                    Teacher updatedTeacher = new Teacher();

                    updatedTeacher.setTeacherID(teacher.getTeacherID());
                    updatedTeacher.setFirstName(teacher.getFirstName());
                    updatedTeacher.setLastName(teacher.getLastName());

                    List<Integer> updatedTeacherCoursesList = new ArrayList<>();
                    for(Integer course1: teacher.getCourses()){
                        if(course1 != course.getCourseID()){
                            updatedTeacherCoursesList.add(course1);
                        }
                    }

                    updatedTeacher.setCourses(updatedTeacherCoursesList);

                    try {
                        this.teachersRepository.update(teacher, updatedTeacher);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            for(Student student: this.studentsRepository.getAll()){
                if(student.getEnrolledCourses().contains(course.getCourseID())){
                    Student updatedStudent = new Student();

                    updatedStudent.setLastName(student.getLastName());
                    updatedStudent.setFirstName(student.getFirstName());
                    updatedStudent.setStudentId(student.getStudentId());

                    List<Integer> updatedStudentCoursesList = new ArrayList<>();
                    for (Integer course1 : student.getEnrolledCourses()){
                        if (course1 != course.getCourseID()){
                            updatedStudentCoursesList.add(course1);
                        }

                    }
                    updatedStudent.setEnrolledCourses(updatedStudentCoursesList);

                    updatedStudent.setTotalCredits(student.getTotalCredits() - course.getCredits());

                    try {
                        this.studentsRepository.update(student, updatedStudent);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }


    /**
     *
     *    Returns all students sorted by name.
     *
     *    <br>
     *    <br>
     *
     *    PRE:
     *    <br>
     *          None
     *
     *    <br>
     *    <br>
     *
     *    EXECUTION:
     *    <br>
     *          Sorts all students by name.
     *
     *    <br>
     *    <br>
     *
     *    POST:
     *    <br>
     *          Returns all students sorted by name
     *
     *
     *    @return returns all students sorted alphabetically by name
     * */
    public List<Student> studentsSortedByName(){

        List<Student> sortedList = new ArrayList<>();

        try {
            sortedList = this.studentsRepository.getAll();
            Collections.sort(sortedList);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return sortedList;
    }



    /**
     *
     *    Sorts all courses by credits.
     *
     *    <br>
     *    <br>
     *
     *    PRE:
     *    <br>
                None
     *
     *    <br>
     *    <br>
     *
     *    EXECUTION:
     *    <br>
     *          Sorts all courses by credits.
     *
     *    <br>
     *    <br>
     *
     *    POST:
     *    <br>
     *          Returns a list of all courses sorted by credits.
     *
     *
     *    @return returns a list of all courses sorted by credits
     * */
    public List<Course> coursesSortedByCredits(){

        List<Course> sortedList = new ArrayList<>();

        try {
            sortedList = this.coursesRepository.getAll();
            Collections.sort(sortedList);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return sortedList;
    }


    /**
     *
     *    Filters just the students with min x credits.
     *
     *    <br>
     *    <br>
     *
     *    PRE:
     *    <br>
     *          None
     *
     *    <br>
     *    <br>
     *
     *    EXECUTION:
     *    <br>
     *          Filters the students, that have a minimum number of x credits.
     *    <br>
     *    <br>
     *
     *    POST:
     *    <br>
     *          Returns a list of students with min x credits.
     *
     *    @param credits is an Integer
     *    @return a list of students with min x credits
     * */
    public List<Student> filterStudentsWithMinXCredits(Integer credits){

        try {
            List<Student> allStudents = studentsRepository.getAll();
            return Objects.requireNonNull(allStudents).stream().filter(student -> student.getTotalCredits() > credits).collect(Collectors.toList());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     *
     *    Filters for courses with free places.
     *
     *    <br>
     *    <br>
     *
     *    PRE:
     *    <br>
     *          None
     *
     *    <br>
     *    <br>
     *
     *    EXECUTION:
     *    <br>
     *          Filters for courses with free places.
     *    <br>
     *    <br>
     *
     *    POST:
     *    <br>
     *          Returns a list of courses with free places.
     *
     *    @return returns a list of courses, that have spare places
     * */
    public List<Course> filterCoursesWithFreePlaces(){

        try {
            List<Course> allCourses = coursesRepository.getAll();
            return Objects.requireNonNull(allCourses).stream().filter(course -> course.getStudentsEnrolled().size() < course.getMaxEnrollment()).collect(Collectors.toList());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void createTeacher(Teacher teacher){
        try {
            this.teachersRepository.create(teacher);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createCourse(Course course){
        try {
            this.coursesRepository.create(course);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createStudent(Student student){
        try {
            this.studentsRepository.create(student);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
