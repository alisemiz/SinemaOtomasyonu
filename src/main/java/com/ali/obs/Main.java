import db.DataBaseHelper; // Veritabanı için gerekli
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox; // Login FXML'i VBox ile başlıyor
import javafx.stage.Stage;

import java.io.IOException;

// ----- SSL Doğrulamasını Devre Dışı Bırakma Kodları -----
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
// -----------------------------------------------------

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {

        // 1. SSL Güvenliğini (Afişler için) devre dışı bırak
        disableSslVerification();

        // 2. Veritabanı tablolarını kontrol et/oluştur
        DataBaseHelper.createTables();

        // 3. Login Ekranını (login-view.fxml) Yükle
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/login-view.fxml"));
        VBox root = fxmlLoader.load(); // Login FXML'imiz VBox ile başlıyor

        // 4. Sahneyi ve Ana Pencereyi (Stage) Ayarla
        Scene scene = new Scene(root); // Boyutları FXML'den alacak
        primaryStage.setTitle("Giriş Yap - Sinema Otomasyonu");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false); // Login ekranı boyutlandırılamasın

        // 5. Pencereyi göster
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Java'nın katı SSL sertifika kontrolünü devre dışı bırakan metot.
     */
    private static void disableSslVerification() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() { return null; }
                        public void checkClientTrusted(X509Certificate[] certs, String authType) { }
                        public void checkServerTrusted(X509Certificate[] certs, String authType) { }
                    }
            };
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
            System.out.println("UYARI: Tüm SSL sertifikalarına güvenme modu AKTİF.");
        } catch (Exception e) {
            System.out.println("SSL sertifika güvenliği devre dışı bırakılamadı: " + e.getMessage());
        }
    }
}