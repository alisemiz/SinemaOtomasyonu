package controller;

import db.BiletDAO; // BiletDAO'yu import et
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.BiletDetay; // Raporlama modelini import et

public class BiletRaporController {

    // --- FXML Tablo Elemanları ---
    @FXML
    private TableView<BiletDetay> biletTable;
    @FXML
    private TableColumn<BiletDetay, Integer> colBiletId;
    @FXML
    private TableColumn<BiletDetay, String> colFilmAdi;
    @FXML
    private TableColumn<BiletDetay, String> colSalonAdi;
    @FXML
    private TableColumn<BiletDetay, String> colSeansTarihSaat;
    @FXML
    private TableColumn<BiletDetay, String> colKoltukNo;
    @FXML
    private TableColumn<BiletDetay, String> colMusteriAdi;

    // --- DAO Sınıfı ---
    private BiletDAO biletDAO;

    /**
     * FXML yüklendikten sonra otomatik çalışır.
     */
    @FXML
    public void initialize() {
        biletDAO = new BiletDAO();

        // 1. Tablo Sütunlarını Ayarla
        // Buradaki string'ler BiletDetay modelindeki property isimleriyle eşleşmelidir.
        colBiletId.setCellValueFactory(new PropertyValueFactory<>("biletId"));
        colFilmAdi.setCellValueFactory(new PropertyValueFactory<>("filmAdi"));
        colSalonAdi.setCellValueFactory(new PropertyValueFactory<>("salonAdi"));
        colSeansTarihSaat.setCellValueFactory(new PropertyValueFactory<>("seansTarihSaat"));
        colKoltukNo.setCellValueFactory(new PropertyValueFactory<>("koltukNo"));
        colMusteriAdi.setCellValueFactory(new PropertyValueFactory<>("musteriAdi"));

        // 2. Tabloyu veritabanından gelen verilerle doldur
        loadBiletData();
    }

    /**
     * Veritabanından tüm bilet detaylarını çeker ve tabloyu yeniler.
     */
    private void loadBiletData() {
        ObservableList<BiletDetay> biletler = biletDAO.getAllBiletDetaylari();
        biletTable.setItems(biletler);
    }
}