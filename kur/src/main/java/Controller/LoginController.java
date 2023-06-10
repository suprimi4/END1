package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LoginController {
   

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField usernameField;

    private AuthenticatorMediator mediator;

    public LoginController() {
        mediator = new AuthenticationMediator() {
        };
    }

        public void activeLoginButton(ActionEvent actionEvent)  {

                Map<String, Runnable> roleActions = new HashMap<>();
                roleActions.put("teacher", mediator::openTeacherWindow);
                roleActions.put("student", mediator::openStudentWindow);

                String username = usernameField.getText();
                String password = passwordField.getText();

                if (ValidCredentials(username, password)) {
                    String userRole = username; // Метод, который определяет роль пользователя по имени
                    Runnable action = roleActions.get(userRole);
                    if (action != null) {
                        action.run();
                    } else {
                        showErrorAlert("Unknown user role: " + userRole);
                    }
                } else {
                    showErrorAlert("Invalid credentials");
                }

        }




    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean ValidCredentials(String username, String password) {
        // Проверка учетных данных в базе данных или другом источнике

        if (username.equals("teacher") && password.equals("teacher")) {
            return true;  // Пример учетных данных для преподавателя
        } else if (username.equals("student") && password.equals("student")) {
            return true;  // Пример учетных данных для студента
        } else {
            return false;
        }
    }

    private boolean Teacher(String username) {
        if (username.equals("teacher")) {
            return true;
        } else {
            return false;
        }
    }
}
