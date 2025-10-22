package controller;

import db.FilmDAO;
import db.SalonDAO;
import db.SeansDAO;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Film;
import model.Salon;
import model.Seans;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class SeansYonetimController {

    // --- FXML Form Elemanları ---
    @FXML
    private ComboBox<Film> cmbFilmler;
    @FXML
    private ComboBox<Salon> cmbSalonlar;
    @FXML
    private DatePicker dpTarih;
    @FXML
    private TextField txtSaat;
    @FXML
    private Button btnSeansEkle;

    // --- FXML Tablo Elemanları ---
    @FXML
    private TableView<Seans> seansTable;
    @FXML
    private TableColumn<Seans, String> colSeansFilm;
    @FXML
    private TableColumn<Seans, String> colSeansSalon;
    @FXML
    private TableColumn<Seans, String> colSeansTarih;
    @FXML
    private Button btnSeansSil;

    // --- DAO Sınıfları ---
    private FilmDAO filmDAO;
    private SalonDAO salonDAO;
    private SeansDAO seansDAO;

    /**
     * FXML yüklendikten sonra otomatik çalışır.
     */
    @FXML
    public void initialize() {
        // DAO'ları başlat
        filmDAO = new FilmDAO();
        salonDAO = new SalonDAO();
        seansDAO = new SeansDAO();

        // 1. ComboBox'ları (Açılır Listeleri) Doldur
        cmbFilmler.setItems(filmDAO.getAllFilmler());
        cmbSalonlar.setItems(salonDAO.getAllSalonlar());

        // 2. Seans Tablosu Sütunlarını Ayarla
        // "filmAd", "salonAd", "tarihSaat" stringleri Seans.java modelindeki
        // ...Property() metotlarından gelir.
        colSeansFilm.setCellValueFactory(new PropertyValueFactory<>("filmAd"));
        colSeansSalon.setCellValueFactory(new PropertyValueFactory<>("salonAd"));
        colSeansTarih.setCellValueFactory(new PropertyValueFactory<>("tarihSaat"));

        // 3. Tabloyu veritabanından gelen verilerle doldur
        loadSeansData();
    }

    /**
     * Veritabanından seansları çeker ve tabloyu yeniler.
     */
    private void loadSeansData() {
        ObservableList<Seans> seanslar = seansDAO.getAllSeanslar();
        seansTable.setItems(seanslar);
    }

    /**
     * "Yeni Seansı Kaydet" butonuna tıklandığında çalışır.
     */
    @FXML
    private void handleSeansEkle() {
        // Formdan verileri al
        Film seciliFilm = cmbFilmler.getValue();
        Salon seciliSalon = cmbSalonlar.getValue();
        LocalDate tarih = dpTarih.getValue();
        String saat = txtSaat.getText();

        // 1. Validasyon (Doğrulama)
        if (seciliFilm == null || seciliSalon == null || tarih == null || saat.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Hata", "Tüm alanlar zorunludur.");
            return;
        }

        // Saat formatını kontrol et (Basit regex: XX:XX)
        if (!saat.matches("^([01]?[0-9]|2[0-3]):[0-5][0-9]$")) {
            showAlert(Alert.AlertType.ERROR, "Hata", "Saat formatı geçersiz. Lütfen SS:DD formatında girin (Örn: 14:30).");
            return;
        }

        // 2. Veritabanına kaydetmek için tarih/saat formatını birleştir
        // (SQLite'ta saklayacağımız format: 'YYYY-MM-DD HH:MM:SS')
        String tarihSaatStr = tarih.format(DateTimeFormatter.ISO_LOCAL_DATE) + " " + saat + ":00";

        // 3. Veritabanına Ekle
        seansDAO.addSeans(seciliFilm.getFilmId(), seciliSalon.getSalonId(), tarihSaatStr);

        // 4. Tabloyu Yenile
        loadSeansData();

        // 5. Formu Temizle (isteğe bağlı)
        cmbFilmler.setValue(null);
        cmbSalonlar.setValue(null);
        dpTarih.setValue(null);
        txtSaat.clear();
    }

    /**
     * "Seçili Seansı Sil" butonuna tıklandığında çalışır.
     */
    @FXML
    private void handleSeansSil() {
        Seans seciliSeans = seansTable.getSelectionModel().getSelectedItem();

        if (seciliSeans == null) {
            showAlert(Alert.AlertType.WARNING, "Silme Hatası", "Lütfen silmek için bir seans seçin.");
            return;
        }

        // Kullanıcıdan onay iste
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Silme Onayı");
        alert.setHeaderText("Seansı Sil: " + seciliSeans.getFilmAd() + " (" + seciliSeans.getTarihSaat() + ")");
        alert.setContentText("Bu seansı silmek istediğinizden emin misiniz? \n(Bu seansa ait tüm biletler de silinecektir.)");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            seansDAO.deleteSeans(seciliSeans.getSeansId());
            loadSeansData(); // Tabloyu yenile
        }
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
}