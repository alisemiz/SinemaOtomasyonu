package controller;

import db.SalonDAO;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Salon; // Salon modelini import ettiğimizden emin olalım

import java.util.Optional;

public class SalonYonetimController {

    // --- FXML Form Elemanları ---
    @FXML
    private Label lblFormBaslik; // YENİ EKLENDİ
    @FXML
    private TextField txtSalonAd;
    @FXML
    private TextField txtKapasite;
    @FXML
    private Button btnKaydetGuncelle; // Değişti (btnSalonEkle idi)
    @FXML
    private Button btnFormuTemizle; // YENİ EKLENDİ

    // --- FXML Tablo Elemanları ---
    @FXML
    private TableView<Salon> salonTable;
    @FXML
    private TableColumn<Salon, String> colSalonAd;
    @FXML
    private TableColumn<Salon, Integer> colKapasite;
    @FXML
    private Button btnSalonDuzenle; // YENİ EKLENDİ
    @FXML
    private Button btnSalonSil;

    // --- DAO Sınıfı ---
    private SalonDAO salonDAO;

    // Şu anda düzenlenmekte olan salonu tutar.
    // null ise, "yeni kayıt" modundayız demektir.
    private Salon suankiSeciliSalon = null;

    /**
     * FXML yüklendikten sonra otomatik çalışır.
     */
    @FXML
    public void initialize() {
        salonDAO = new SalonDAO();

        // Salon Tablosu Sütunlarını Ayarla
        // "ad" ve "kapasite" stringleri Salon.java modelindeki
        // ...Property() metotlarından gelir.
        colSalonAd.setCellValueFactory(new PropertyValueFactory<>("ad"));
        colKapasite.setCellValueFactory(new PropertyValueFactory<>("kapasite"));

        // Tabloyu veritabanından gelen verilerle doldur
        loadSalonData();
    }

    /**
     * Veritabanından salonları çeker ve tabloyu yeniler.
     */
    private void loadSalonData() {
        ObservableList<Salon> salonlar = salonDAO.getAllSalonlar();
        salonTable.setItems(salonlar);
    }

    /**
     * "Salonu Kaydet" (veya "Güncelle") butonuna tıklandığında çalışır.
     * Metodun adı 'handleSalonEkle' idi, 'handleKaydetGuncelle' olarak değişti.
     */
    @FXML
    private void handleKaydetGuncelle() {
        String ad = txtSalonAd.getText();
        String kapasiteStr = txtKapasite.getText();

        // 1. Validasyon
        if (ad.isEmpty() || kapasiteStr.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Hata", "Tüm alanlar zorunludur.");
            return;
        }

        int kapasite;
        try {
            kapasite = Integer.parseInt(kapasiteStr);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Hata", "Kapasite alanı sayısal bir değer olmalıdır.");
            return;
        }

        try {
            if (suankiSeciliSalon == null) {
                // --- YENİ KAYIT MODU ---
                salonDAO.addSalon(ad, kapasite);
                showAlert(Alert.AlertType.INFORMATION, "Başarılı", "Yeni salon başarıyla eklendi.");
            } else {
                // --- DÜZENLEME MODU ---
                // Seçili salon nesnesinin (JavaFX Property) bilgilerini güncelle.
                // Bu, TableView'in ANINDA güncellenmesini sağlar.
                suankiSeciliSalon.adProperty().set(ad);
                suankiSeciliSalon.kapasiteProperty().set(kapasite);

                // DAO aracılığıyla veritabanını güncelle
                salonDAO.updateSalon(suankiSeciliSalon);
                showAlert(Alert.AlertType.INFORMATION, "Başarılı", "Salon başarıyla güncellendi.");
            }

            // 3. Tabloyu Yenile (Property güncellemesi bazen yetmeyebilir, garantileyelim)
            loadSalonData();
            // 4. Formu Temizle ve "Yeni Kayıt" moduna dön
            handleFormuTemizle();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Veritabanı Hatası", "İşlem sırasında bir hata oluştu: " + e.getMessage());
        }
    }

    /**
     * "Seçili Salonu Düzenle" butonuna tıklandığında çalışır.
     * YENİ EKLENEN METOT
     */
    @FXML
    private void handleSalonDuzenle() {
        Salon secili = salonTable.getSelectionModel().getSelectedItem();

        if (secili == null) {
            showAlert(Alert.AlertType.WARNING, "Hata", "Lütfen düzenlemek için bir salon seçin.");
            return;
        }

        // Formu "Düzenleme Modu"na geçir
        suankiSeciliSalon = secili; // Hangi salonu düzenlediğimizi sakla
        txtSalonAd.setText(secili.getAd());
        txtKapasite.setText(String.valueOf(secili.getKapasite())); // int'i String'e çevir

        lblFormBaslik.setText("Salonu Düzenle: " + secili.getAd());
        btnKaydetGuncelle.setText("Güncellemeyi Kaydet");
    }

    /**
     * "Formu Temizle (İptal)" butonuna tıklandığında çalışır.
     * YENİ EKLENEN METOT
     */
    @FXML
    private void handleFormuTemizle() {
        suankiSeciliSalon = null; // Düzenleme modundan çık

        txtSalonAd.clear();
        txtKapasite.clear();

        lblFormBaslik.setText("Yeni Salon Ekle");
        btnKaydetGuncelle.setText("Salonu Kaydet");

        salonTable.getSelectionModel().clearSelection(); // Tablodaki seçimi kaldır
    }

    /**
     * "Seçili Salonu Sil" butonuna tıklandığında çalışır.
     * GÜNCELLENDİ: Düzenleme modundayken silme işlemi engellendi.
     */
    @FXML
    private void handleSalonSil() {
        // Silme işlemi yaparken formun düzenleme modunda olmadığından emin ol
        if (suankiSeciliSalon != null) {
            showAlert(Alert.AlertType.WARNING, "Hata", "Lütfen önce mevcut düzenleme işlemini iptal edin (Formu Temizle).");
            return;
        }

        Salon seciliSalon = salonTable.getSelectionModel().getSelectedItem();

        if (seciliSalon == null) {
            showAlert(Alert.AlertType.WARNING, "Silme Hatası", "Lütfen silmek için bir salon seçin.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Silme Onayı");
        alert.setHeaderText("Salonu Sil: " + seciliSalon.getAd());
        alert.setContentText("Bu salonu silmek istediğinizden emin misiniz? \n(Bu salona ait tüm seanslar ve biletler de silinecektir.)");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            salonDAO.deleteSalon(seciliSalon.getSalonId());
            loadSalonData();
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