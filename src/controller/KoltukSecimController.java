package controller;

import db.BiletDAO;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.Seans;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class KoltukSecimController {

    // --- FXML Elemanları ---
    @FXML
    private Label lblFilmAdi;
    @FXML
    private Label lblSalonVeTarih;
    @FXML
    private GridPane koltukGrid; // Koltukların ekleneceği grid
    @FXML
    private Label lblSecilenKoltuklar;
    @FXML
    private TextField txtMusteriAd;
    @FXML
    private Button btnSatinAl;

    // --- Diğer Değişkenler ---
    private Seans seciliSeans;
    private BiletDAO biletDAO;
    private List<ToggleButton> secilenKoltukButonlari = new ArrayList<>();

    @FXML
    public void initialize() {
        biletDAO = new BiletDAO();
        lblSecilenKoltuklar.setText("Lütfen koltuk seçin.");
    }

    /**
     * MainController'dan 'Seans' bilgisini almak için kullanılır.
     */
    public void initData(Seans seans) {
        this.seciliSeans = seans;

        lblFilmAdi.setText(seciliSeans.getFilmAd());
        lblSalonVeTarih.setText(seciliSeans.getSalonAd() + " (Kapasite: " + seciliSeans.getSalonKapasite() + ")  |  " + seciliSeans.getTarihSaat());

        // Koltukları oluştur
        generateKoltukGrid();
    }

    /**
     * Dinamik olarak koltuk grid'ini (ToggleButton'lar) oluşturan ana metot.
     * GÜNCELLENDİ: Artık salon kapasitesine göre dinamik grid oluşturuyor.
     */
    private void generateKoltukGrid() {
        // 1. Bu seansa ait DOLU koltukları veritabanından çek
        ObservableList<String> doluKoltuklar = biletDAO.getDoluKoltuklarBySeansId(seciliSeans.getSeansId());

        // 2. Salon kapasitesine ve varsayılan sütun sayısına (10) göre
        //   satır ve sütun sayılarını hesapla.
        int kapasite = seciliSeans.getSalonKapasite();
        int cols = 10; // Bir sırada maksimum 10 koltuk olduğunu varsayıyoruz

        // Gerekli satır sayısını hesapla (örn: 85 kapasite / 10 = 8.5 -> 9 satır)
        int rows = (int) Math.ceil((double) kapasite / cols);

        // 3. Harf sırası (A, B, C...) için
        char rowChar = 'A';
        int koltukSayaci = 1; // Toplam kapasiteyi geçmemek için sayaç

        for (int i = 0; i < rows; i++) { // Satırlar
            for (int j = 0; j < cols; j++) { // Sütunlar

                // 4. Koltuk numarasını oluştur
                String koltukNo = rowChar + "" + (j + 1); // "A1", "A2", "B1"...
                ToggleButton koltukButton = new ToggleButton(koltukNo);
                koltukButton.setPrefWidth(50);
                koltukButton.setPrefHeight(50);

                // 5. Bu koltuğun, salonun gerçek kapasitesi içinde olup olmadığını kontrol et
                if (koltukSayaci > kapasite) {
                    // Bu koltuk var olmayan bir koltuktur (örn: 80 kapasiteli salonda J10)
                    koltukButton.setDisable(true);
                    koltukButton.setVisible(false); // Tamamen görünmez yap
                } else {
                    // Bu koltuk salon kapasitesi içinde, var olan bir koltuktur

                    // 6. Koltuğun dolu olup olmadığını kontrol et
                    if (doluKoltuklar.contains(koltukNo)) {
                        // KOLTUK DOLU
                        koltukButton.setDisable(true); // Pasif yap
                        koltukButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white;"); // Kırmızı
                    } else {
                        // KOLTUK BOŞ
                        koltukButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;"); // Yeşil

                        koltukButton.setOnAction(event -> {
                            if (koltukButton.isSelected()) {
                                secilenKoltukButonlari.add(koltukButton);
                                koltukButton.setStyle("-fx-background-color: #FFC107; -fx-text-fill: black;"); // Sarı
                            } else {
                                secilenKoltukButonlari.remove(koltukButton);
                                koltukButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;"); // Tekrar Yeşil
                            }
                            updateSecilenKoltuklarLabel();
                        });
                    }
                }

                // Hazırlanan butonu Grid'e (konteynere) ekle
                koltukGrid.add(koltukButton, j, i); // (Sütun j, Satır i)
                koltukSayaci++; // Sayacı artır
            }
            rowChar++; // Bir sonraki harfe geç (B, C, D...)
        }
    }

    /**
     * Alttaki 'lblSecilenKoltuklar' etiketini günceller.
     */
    private void updateSecilenKoltuklarLabel() {
        // ... (Bu metotta değişiklik yok)
        if (secilenKoltukButonlari.isEmpty()) {
            lblSecilenKoltuklar.setText("Lütfen koltuk seçin.");
        } else {
            String secilenler = secilenKoltukButonlari.stream()
                    .map(ToggleButton::getText)
                    .collect(Collectors.joining(", "));
            lblSecilenKoltuklar.setText(secilenler);
        }
    }

    /**
     * "Satın Al" butonuna tıklandığında çalışır.
     */
    @FXML
    private void handleSatinAl() {
        // ... (Bu metotta değişiklik yok)
        if (secilenKoltukButonlari.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Hata", "Lütfen en az bir koltuk seçin.");
            return;
        }

        List<String> secilenKoltukNolari = secilenKoltukButonlari.stream()
                .map(ToggleButton::getText)
                .collect(Collectors.toList());

        String musteriAd = txtMusteriAd.getText();

        try {
            biletDAO.addBiletler(seciliSeans.getSeansId(), secilenKoltukNolari, musteriAd);

            showAlert(Alert.AlertType.INFORMATION, "Başarılı",
                    secilenKoltukNolari.size() + " adet bilet (" + lblSecilenKoltuklar.getText() + ") " +
                            " başarıyla satın alındı.");

            closeStage();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Veritabanı Hatası", "Biletler kaydedilirken bir hata oluştu: " + e.getMessage());
        }
    }

    // --- Yardımcı Metotlar ---

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        // ... (Bu metotta değişiklik yok)
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeStage() {
        // ... (Bu metotta değişiklik yok)
        Stage stage = (Stage) btnSatinAl.getScene().getWindow();
        stage.close();
    }
}