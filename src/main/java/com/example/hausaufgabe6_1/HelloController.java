package com.example.hausaufgabe6_1;

import controller.Controller;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import model.Course;
import model.Student;
import model.Teacher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HelloController {
    @FXML
    public static Teacher teacher = new Teacher();

    @FXML
    public static Student student = new Student();


    @FXML
    private TextField tfGetLogIn;

    @FXML
    private Label welcomeText;

    @FXML
    private TextField tfGetCourseID;

    @FXML
    private Label printCreditsLabel;

    @FXML
    private Label enrollStatus;

    @FXML
    private TextFlow text_flow;

    @FXML
    void refreshData(){
        ObservableList<Node> list = text_flow.getChildren();
        Controller controller = new Controller();

        for(Course course:controller.getAllCourses()){
            Text text = new Text(course.toString());
            list.add(text);
        }

        Scene scene = new Scene((Parent) list);
        //Adding scene to the stage
        Stage newStage = new Stage();
        newStage.setScene(scene);

        //Displaying the contents of the stage
        newStage.show();
    }


    @FXML
    void enrollButton() {
        Controller controller = new Controller();

        Course theCourse = controller.getAllCourses().stream().filter(course -> course.getCourseID() == Integer.parseInt(tfGetCourseID.getText())).findAny().orElse(null);

        if(theCourse == null){
            enrollStatus.setText("Course does not exist");
        }
        else{
            if(student.getEnrolledCourses().contains(theCourse.getCourseID())){
                enrollStatus.setText("Student is already assigned.");
            }
            else{
                if(student.getTotalCredits() + theCourse.getCredits() > 30){
                    enrollStatus.setText("Credit number exceeded.");
                }
                else{
                    if(theCourse.getMaxEnrollment() < theCourse.getStudentsEnrolled().size() + 1){
                        enrollStatus.setText("No available places.");
                    }

                    else{
                        try {
                            System.out.println(student.getStudentId());
                            System.out.println(theCourse.getCourseID());
                            controller.registerStudentToCourse(student, theCourse);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        enrollStatus.setText("Student enrolled successfully");
                    }
                }
            }
        }

    }



    @FXML
    void enrollToCourseButton() {
        Controller controller = new Controller();
        List<Course> courses = controller.getAllCourses();
        text_flow = new TextFlow();

        for(Course course: courses){
            Text text_1 = new Text( course.toString() + ":\n");
            text_flow.getChildren().add(text_1);
        }
        Scene scene = new Scene(text_flow, 600, 400);
        Stage stage = new Stage();
        stage.setTitle("Choose a course from this list!");
        stage.setScene(scene);
        stage.show();


        FXMLLoader newFxmlLoader = new FXMLLoader(HelloApplication.class.getResource("EnrollStudent.fxml"));
        Scene newScene;
        try {
            newScene = new Scene(newFxmlLoader.load());
            Stage newStage = new Stage();
            newStage.setTitle("Enroll Student");
            newStage.setScene(newScene);
            newStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @FXML
    void seeCreditsButton() {
        printCreditsLabel.setText(student.getFirstName() + " " + student.getLastName() + ", you have the following amount of credits: " + student.getTotalCredits());
    }

    @FXML
    void studentsEnrolledForCourseButton() {
        System.out.println(teacher);
        System.out.println("Students enrolled for course");


        TextFlow text_flow = new TextFlow();
        Scene scene = new Scene(text_flow, 400, 400);
        Stage stage = new Stage();
        stage.setTitle("Students Assigned To Course");
        Controller controller = new Controller();
        List<Course> courses = new ArrayList<>();
        for(Integer course : teacher.getCourses()){
            courses.add(controller.getAllCourses().stream().filter(course1 -> course1.getCourseID() == course).findAny().orElse(null));
        }

        for(Course course: courses){
            Text text_1 = new Text( course.getName() + ":\n");
            text_flow.getChildren().add(text_1);
            List<Student> studentsToCourse = new ArrayList<>(controller.getStudentsEnrolledForCourse(course.getCourseID()));
            for(Student student1:studentsToCourse){
                Text text_2 = new Text( "    " + student1.toString() + ":\n");
                text_flow.getChildren().add(text_2);
            }
        }


        stage.setScene(scene);
        stage.show();

    }

    @FXML
    void btnLogInClick() {

        Controller controller = new Controller();

        List<String> allStudents = new ArrayList<>();
        List<String> allTeachers = new ArrayList<>();

        for(Student student:controller.getAllStudents()){
            allStudents.add(student.getLastName());
        }

        for(Teacher teacher:controller.getAllTeachers()){
            allTeachers.add(teacher.getLastName());
        }



        if(allTeachers.contains(tfGetLogIn.getText())){

            teacher = controller.getAllTeachers().stream().filter(tcher -> tfGetLogIn.getText().equals(tcher.getLastName())).findAny().orElse(null);
            welcomeText.setText("Logged in as teacher.");
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("TeacherMenu.fxml"));
            Scene scene;

            try {
                scene = new Scene(fxmlLoader.load());
                Stage stage = new Stage();
                stage.setTitle("Student Menu");
                stage.setScene(scene);
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }



        }
        else{
            if(allStudents.contains(tfGetLogIn.getText())){
                for(Student student1: controller.getAllStudents()){
                    if(student1.getLastName().equals(tfGetLogIn.getText())){
                        student = student1;
                    }
                }
                welcomeText.setText("Logged in as student.");
                FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("StudentMenu.fxml"));
                Scene scene;

                try {
                    scene = new Scene(fxmlLoader.load());
                    Stage stage = new Stage();
                    stage.setTitle("Teacher Menu");
                    stage.setScene(scene);
                    stage.show();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else{
                welcomeText.setText("No such Log In. Try again.");
            }
        }
    }

}
