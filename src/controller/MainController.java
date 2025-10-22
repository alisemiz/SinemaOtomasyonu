package controller;

import db.FilmDAO;
import db.SeansDAO;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox; // Rapor ekranı için
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Film;
import model.Seans;
import controller.KoltukSecimController;
import controller.FilmEkleController;

import javafx.concurrent.Task;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import java.io.IOException;
import java.util.Optional;

public class MainController {

    // --- FXML Alanları ---
    @FXML private TableView<Film> filmTable;
    @FXML private TableColumn<Film, String> colFilmAd;
    @FXML private TableColumn<Film, String> colYonetmen;
    @FXML private TableColumn<Film, Integer> colSure;
    @FXML private ImageView imgPoster;

    // Yönetim Butonları (ve HBox)
    @FXML private HBox yönetimButonlariHBox;
    @FXML private Button btnFilmEkle;
    @FXML private Button btnFilmDuzenle;
    @FXML private Button btnFilmSil;
    @FXML private Button btnBiletRapor; // YENİ EKLENDİ
    @FXML private Button btnSalonYonet;
    @FXML private Button btnSeansYonet;

    // Seans Tablosu
    @FXML private TableView<Seans> seansTable;
    @FXML private TableColumn<Seans, String> colSeansSalon;
    @FXML private TableColumn<Seans, String> colSeansTarih;

    // Müşteri Butonu
    @FXML private Button btnBiletAl;

    // --- DAO Sınıfları ---
    private FilmDAO filmDAO;
    private SeansDAO seansDAO;

    // --- Kullanıcı Tipi ---
    private String userType;

    public void setUserType(String userType) {
        this.userType = userType;
        setupUIForUserType();
    }

    @FXML
    public void initialize() {
        // ... (DAO ve Tablo Sütunları aynı)
        filmDAO = new FilmDAO();
        seansDAO = new SeansDAO();
        colFilmAd.setCellValueFactory(new PropertyValueFactory<>("ad"));
        colYonetmen.setCellValueFactory(new PropertyValueFactory<>("yonetmen"));
        colSure.setCellValueFactory(new PropertyValueFactory<>("sureDk"));
        colSeansSalon.setCellValueFactory(new PropertyValueFactory<>("salonAd"));
        colSeansTarih.setCellValueFactory(new PropertyValueFactory<>("tarihSaat"));

        // Film tablosu seçim dinleyicisi
        filmTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, seciliFilm) -> {
                    if (seciliFilm != null) {
                        loadSeanslarForFilm(seciliFilm.getFilmId());
                        loadFilmAfisi(seciliFilm.getPosterUrl());
                    } else {
                        seansTable.getItems().clear();
                        imgPoster.setImage(null);
                    }
                }
        );
        loadFilmData();
    }

    /**
     * Kullanıcı tipine göre arayüzü ayarlar.
     * GÜNCELLENDİ: Bilet Rapor butonunu da gizler/gösterir.
     */
    private void setupUIForUserType() {
        boolean isAdmin = userType != null && userType.equals("admin");

        if (yönetimButonlariHBox != null) {
            yönetimButonlariHBox.setVisible(isAdmin);
            yönetimButonlariHBox.setManaged(isAdmin);
        } else {
            System.err.println("FXML'de 'yönetimButonlariHBox' id'li HBox bulunamadı!");
        }

        // Bilet Al butonu her zaman görünür (ya da sadece 'user' için?)
        // btnBiletAl.setVisible(!isAdmin);
        // btnBiletAl.setManaged(!isAdmin);
    }

    // ... (loadFilmData, loadSeanslarForFilm, loadFilmAfisi aynı) ...
    public void loadFilmData() {
        ObservableList<Film> filmler = filmDAO.getAllFilmler();
        filmTable.setItems(filmler);
    }
    private void loadSeanslarForFilm(int filmId) {
        ObservableList<Seans> seanslar = seansDAO.getSeanslarByFilmId(filmId);
        seansTable.setItems(seanslar);
    }
    private void loadFilmAfisi(String urlString) {
        if (urlString == null || urlString.isEmpty() || !urlString.startsWith("http")) {
            imgPoster.setImage(null); return;
        }
        Task<Image> loadTask = new Task<>() {
            @Override
            protected Image call() throws Exception {
                HttpURLConnection conn = null;
                try {
                    URL originalUrl = new URL(urlString);
                    String path = originalUrl.getPath();
                    String encodedPath = URLEncoder.encode(path, StandardCharsets.UTF_8.toString()).replace("+", "%20").replace("%2F", "/");
                    URL imageUrl = new URL(originalUrl.getProtocol(), originalUrl.getHost(), originalUrl.getPort(), encodedPath);
                    conn = (HttpURLConnection) imageUrl.openConnection();
                    conn.setInstanceFollowRedirects(true);
                    HttpURLConnection.setFollowRedirects(true);
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                    conn.setReadTimeout(10000);
                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        try (InputStream is = conn.getInputStream()) {
                            return new Image(is, 280, 400, true, true);
                        }
                    } else { System.err.println("!!! Afiş MANUEL yüklenemedi (Task İÇİ) - HTTP Hata Kodu: " + responseCode + " URL: " + imageUrl); return null;}
                } catch (Exception e) { System.err.println("!!! Afiş MANUEL yüklenemedi (Task İÇİ): " + urlString); e.printStackTrace(); return null;
                } finally { if (conn != null) { conn.disconnect(); } }
            }
        };
        loadTask.setOnSucceeded(event -> imgPoster.setImage(loadTask.getValue()));
        loadTask.setOnFailed(event -> {
            imgPoster.setImage(null); System.err.println("!!! Afiş yükleme Task'i BAŞARISIZ OLDU: " + urlString);
            Throwable error = loadTask.getException(); if (error != null) { error.printStackTrace(); }
        });
        new Thread(loadTask).start();
    }

    // ... (handleBiletAl, handleFilmEkle, handleFilmDuzenle, handleFilmSil,
    //      handleSalonYonet, handleSeansYonet aynı) ...
    @FXML
    private void handleBiletAl() { /* ... */
        Seans seciliSeans = seansTable.getSelectionModel().getSelectedItem();
        if (seciliSeans == null) { showAlert(Alert.AlertType.WARNING, "Bilet Al Hatası", "Lütfen bilet almak için bir seans seçin."); return; }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/koltuk-secim-view.fxml"));
            BorderPane page = loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Koltuk Seçimi");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(btnBiletAl.getScene().getWindow());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            KoltukSecimController controller = loader.getController();
            controller.initData(seciliSeans);
            dialogStage.showAndWait();
        } catch (IOException e) { e.printStackTrace(); showAlert(Alert.AlertType.ERROR, "Hata", "Koltuk seçim ekranı yüklenemedi: " + e.getMessage()); }
    }
    @FXML
    private void handleFilmEkle() { /* ... */
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/film-ekle-dialog.fxml"));
            VBox page = loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Yeni Film Ekle");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(btnFilmEkle.getScene().getWindow());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            FilmEkleController controller = loader.getController();
            controller.setMainController(this);
            dialogStage.showAndWait();
        } catch (IOException e) { e.printStackTrace(); }
    }
    @FXML
    private void handleFilmDuzenle() { /* ... */
        Film seciliFilm = filmTable.getSelectionModel().getSelectedItem();
        if (seciliFilm == null) { showAlert(Alert.AlertType.WARNING, "Düzenleme Hatası", "Lütfen düzenlemek için bir film seçin."); return; }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/film-ekle-dialog.fxml"));
            VBox page = loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Filmi Düzenle");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(btnFilmDuzenle.getScene().getWindow());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            FilmEkleController controller = loader.getController();
            controller.setMainController(this);
            controller.initData(seciliFilm);
            dialogStage.showAndWait();
        } catch (IOException e) { e.printStackTrace(); }
    }
    @FXML
    private void handleFilmSil() { /* ... */
        Film seciliFilm = filmTable.getSelectionModel().getSelectedItem();
        if (seciliFilm == null) { showAlert(Alert.AlertType.WARNING, "Silme Hatası", "Lütfen silmek için bir film seçin.");
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Silme Onayı");
            alert.setHeaderText("Filmi Sil: " + seciliFilm.getAd());
            alert.setContentText("Bu filme bağlı tüm seanslar ve biletler de silinecektir.");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                filmDAO.deleteFilm(seciliFilm.getFilmId());
                loadFilmData();
                seansTable.getItems().clear();
                imgPoster.setImage(null);
            }
        }
    }
    @FXML
    private void handleSalonYonet() { /* ... */
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/salon-yonetim-view.fxml"));
            VBox page = loader.load();
            Stage salonStage = new Stage();
            salonStage.setTitle("Salon Yönetimi");
            salonStage.initModality(Modality.WINDOW_MODAL);
            salonStage.initOwner(btnSalonYonet.getScene().getWindow());
            Scene scene = new Scene(page);
            salonStage.setScene(scene);
            salonStage.showAndWait();
        } catch (IOException e) { e.printStackTrace(); }
    }
    @FXML
    private void handleSeansYonet() { /* ... */
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/seans-yonetim-view.fxml"));
            VBox page = loader.load();
            Stage seansStage = new Stage();
            seansStage.setTitle("Seans Yönetimi");
            seansStage.initModality(Modality.WINDOW_MODAL);
            seansStage.initOwner(btnSeansYonet.getScene().getWindow());
            Scene scene = new Scene(page);
            seansStage.setScene(scene);
            seansStage.showAndWait();
            loadFilmData();
            seansTable.getItems().clear();
            imgPoster.setImage(null);
        } catch (IOException e) { e.printStackTrace(); }
    }

    /**
     * "Bilet Raporları" butonuna tıklandığında çalışır.
     * YENİ EKLENEN METOT
     */
    @FXML
    private void handleBiletRapor() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/bilet-rapor-view.fxml"));
            // Rapor FXML'i VBox ile başlıyor
            VBox page = loader.load();

            Stage raporStage = new Stage();
            raporStage.setTitle("Satılan Bilet Raporu");
            // Rapor penceresi diğer pencereleri engellemesin (Modality.NONE)
            raporStage.initModality(Modality.NONE);
            raporStage.initOwner(btnBiletRapor.getScene().getWindow()); // Ana pencere sahibi

            Scene scene = new Scene(page);
            raporStage.setScene(scene);

            // BiletRaporController'a herhangi bir veri göndermemiz gerekmiyor.

            // showAndWait yerine show() kullanıyoruz ki ana pencereyle aynı anda açık kalabilsin
            raporStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Hata", "Bilet rapor ekranı yüklenemedi: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}