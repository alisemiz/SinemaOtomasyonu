package controller;

import db.FilmDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Film;

public class FilmEkleController {

    // FXML'deki elemanları bağlıyoruz
    @FXML
    private Label lblFormBaslik;
    @FXML
    private TextField txtAd;
    @FXML
    private TextField txtYonetmen;
    @FXML
    private TextField txtSure;
    @FXML
    private TextField txtPosterUrl; // YENİ EKLENDİ

    @FXML
    private Button btnKaydet;
    @FXML
    private Button btnIptal;

    private FilmDAO filmDAO;
    private MainController mainController;
    private Film suankiSeciliFilm = null;

    /**
     * MainController referansını ayarlar.
     */
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    /**
     * "Düzenleme" modu için formu doldurur.
     * GÜNCELLENDİ: posterUrl eklendi.
     */
    public void initData(Film film) {
        this.suankiSeciliFilm = film;

        // Form alanlarını doldur
        txtAd.setText(film.getAd());
        txtYonetmen.setText(film.getYonetmen());
        txtSure.setText(String.valueOf(film.getSureDk()));
        txtPosterUrl.setText(film.getPosterUrl()); // YENİ EKLENDİ

        // Diyalog penceresinin başlığını ve buton metnini güncelle
        lblFormBaslik.setText("Filmi Düzenle: " + film.getAd());
        btnKaydet.setText("Güncellemeyi Kaydet");
    }

    @FXML
    public void initialize() {
        filmDAO = new FilmDAO();
    }

    /**
     * "Kaydet" veya "Güncellemeyi Kaydet" butonuna tıklandığında çalışır.
     * GÜNCELLENDİ: posterUrl eklendi.
     */
    @FXML
    private void handleKaydetGuncelle() {
        // Formdan verileri al
        String ad = txtAd.getText();
        String yonetmen = txtYonetmen.getText();
        String sureStr = txtSure.getText();
        String posterUrl = txtPosterUrl.getText(); // YENİ EKLENDİ

        // 1. Alanların boş olup olmadığını kontrol et (posterUrl zorunlu değil)
        if (ad.isEmpty() || yonetmen.isEmpty() || sureStr.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Hata", "Film Adı, Yönetmen ve Süre alanları zorunludur.");
            return;
        }

        // 2. Süre alanının sayı olup olmadığını kontrol et
        int sureDk;
        try {
            sureDk = Integer.parseInt(sureStr);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Hata", "Süre alanı sayısal bir değer olmalıdır.");
            return;
        }

        try {
            if (suankiSeciliFilm == null) {
                // --- YENİ KAYIT MODU ---
                // filmDAO.addFilm metoduna posterUrl eklendi
                filmDAO.addFilm(ad, yonetmen, sureDk, posterUrl);
            } else {
                // --- DÜZENLEME MODU ---
                // Seçili film nesnesinin (JavaFX Property) bilgilerini güncelle
                suankiSeciliFilm.adProperty().set(ad);
                suankiSeciliFilm.yonetmenProperty().set(yonetmen);
                suankiSeciliFilm.sureDkProperty().set(sureDk);
                suankiSeciliFilm.posterUrlProperty().set(posterUrl); // YENİ EKLENDİ

                // DAO aracılığıyla veritabanını güncelle
                filmDAO.updateFilm(suankiSeciliFilm);
            }

            // 4. Ana tablonun yenilenmesini sağla
            if (mainController != null) {
                mainController.loadFilmData(); // MainController'daki listeyi yenile
            }

            // 5. Pencereyi kapat
            closeStage();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Veritabanı Hatası", "İşlem sırasında bir hata oluştu: " + e.getMessage());
        }
    }

    /**
     * "İptal" butonuna tıklandığında çalışır.
     */
    @FXML
    private void handleIptal() {
        closeStage();
    }

    /**
     * Kullanıcıya uyarı (Alert) göstermek için yardımcı metot
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Bu diyalog penceresini kapatır.
     */
    private void closeStage() {
        Stage stage = (Stage) btnKaydet.getScene().getWindow();
        stage.close();
    }
}