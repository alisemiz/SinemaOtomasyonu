package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.io.IOException;

public class LoginController {

    @FXML
    private TextField txtUsername;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private Button btnLogin;
    @FXML
    private Label lblStatus;

    /**
     * "Giriş Yap" butonuna tıklandığında çalışır.
     */
    @FXML
    private void handleLogin() {
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        String userType = null;

        if (username.equals("admin") && password.equals("admin")) {
            userType = "admin";
            System.out.println("Admin girişi başarılı.");
        } else if (username.equals("user") && password.equals("user")) {
            userType = "user";
            System.out.println("Kullanıcı girişi başarılı.");
        } else {
            lblStatus.setText("Geçersiz kullanıcı adı veya şifre.");
            return;
        }

        if (userType != null) {
            try {
                Stage currentStage = (Stage) btnLogin.getScene().getWindow();
                currentStage.close();

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main-view.fxml"));
                BorderPane root = loader.load();

                Stage mainStage = new Stage();
                mainStage.setTitle("Sinema Otomasyon Sistemi");

                // --- PENCERE BOYUTU BURADA AYARLANDI ---
                // Sahneyi belirli bir boyutla oluştur (Genişlik: 1000, Yükseklik: 800)
                Scene scene = new Scene(root, 1000, 800);
                // ----------------------------------------

                mainStage.setScene(scene);

                MainController mainController = loader.getController();
                mainController.setUserType(userType);

                mainStage.show();

            } catch (IOException e) {
                e.printStackTrace();
                lblStatus.setText("Ana ekran yüklenirken hata oluştu!");
            }
        }
    }
}