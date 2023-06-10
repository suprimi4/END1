package Controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class AuthenticationMediator implements AuthenticatorMediator {@Override
public void openStudentWindow() {
    try {
        FXMLLoader fxmlLoader = new FXMLLoader(com.example.kur.HelloApplication.class.getResource("student-win.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        Stage stage = new Stage();
        stage.setTitle("");
        stage.setScene(scene);
        stage.show();
    } catch (IOException e) {
        e.printStackTrace();
    }
}

    @Override
    public void openTeacherWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(com.example.kur.HelloApplication.class.getResource("teacher-win.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 320, 240);
            Stage stage = new Stage();
            stage.setTitle("Hello!");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
