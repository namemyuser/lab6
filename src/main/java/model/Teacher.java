package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Is a model class for the InMemoryRepository template
 * */

public class Teacher extends Person {
    public List<Integer> courses;
    public Integer teacherID;

    public Teacher(Integer teacherID, String firstName, String lastName, List<Integer> courses) {
        super(firstName, lastName);
        this.courses = courses;
        this.teacherID = teacherID;
    }

    public Teacher() {
        super("", "");
        this.courses = new ArrayList<Integer>();
        this.teacherID = 0;
    }

    public int getTeacherID() {
        return teacherID;
    }

    public void setTeacherID(Integer teacherID) {
        this.teacherID = teacherID;
    }

    public List<Integer> getCourses() {
        return courses;
    }

    public void setCourses(List<Integer> courses) {
        this.courses = courses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Teacher)) return false;
        Teacher teacher = (Teacher) o;
        return Objects.equals(getCourses(), teacher.getCourses()) && Objects.equals(getTeacherID(), teacher.getTeacherID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCourses(), getTeacherID());
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", courses=" + courses +
                ", teacherID=" + teacherID +
                '}';
    }
}
