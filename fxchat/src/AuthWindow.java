import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;


public class AuthWindow {

    public static void display(Network network){



        Stage win = new Stage();
        win.initModality(Modality.APPLICATION_MODAL);
        win.setTitle("Authorization");

        HBox login = new HBox(30);
        Label lbLogin = new Label("Login:");
        TextField tfLogin = new TextField();
        login.getChildren().addAll(lbLogin,tfLogin);
        login.setAlignment(Pos.CENTER);

        HBox pass = new HBox(10);
        Label lbPass = new Label("Password:");
        PasswordField pfPass = new PasswordField();
        pass.getChildren().addAll(lbPass,pfPass);
        pass.setAlignment(Pos.CENTER);

        HBox buttons = new HBox(50);
        Button logIn = new Button("Log in");
        logIn.setOnAction(e -> {
              try {
                 network.authorise(tfLogin.getText(), pfPass.getText());

              } catch (AuthException e1) {
                  Alert alert = new Alert(Alert.AlertType.ERROR);
                  alert.setTitle("Ошибка!!!");
                  alert.setHeaderText(null);
                  alert.setContentText("Ошибка авторизации!!!");
                  alert.showAndWait();
                  return;

              }
              catch (IOException e1) {
                  e1.printStackTrace();
              }
              chat.window.setTitle("Chating...From... " + tfLogin.getText()+"!");
                win.close();
    });
        pfPass.addEventFilter(KeyEvent.KEY_PRESSED,(KeyEvent e) -> {
            if (e.getCode() == KeyCode.ENTER){
                try {
                    network.authorise(tfLogin.getText(), pfPass.getText());

                } catch (AuthException e1) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Ошибка!!!");
                    alert.setHeaderText(null);
                    alert.setContentText("Ошибка авторизации!!!");
                    alert.showAndWait();
                    return;

                }
                catch (IOException e1) {
                    e1.printStackTrace();
                }
                chat.window.setTitle("Chating...From... " + tfLogin.getText()+"!");
                win.close();
            }
        });
        Button cancel = new Button("Cancel");
        cancel.setOnAction(event ->  System.exit(0) );
        buttons.getChildren().addAll(logIn,cancel);
        buttons.setAlignment(Pos.CENTER);

        VBox layout = new VBox(5);
        layout.getChildren().addAll(login,pass,buttons);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout,300,300);
        win.setScene(scene);
        win.show();

    }
}
