package db;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Salon; // Salon modelimizi import ediyoruz

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SalonDAO {

    /**
     * Veritabanındaki tüm salonları getirir.
     * @return Salonların ObservableList'i (ComboBox ve TableView için)
     */
    public ObservableList<Salon> getAllSalonlar() {
        ObservableList<Salon> salonlar = FXCollections.observableArrayList();
        String sql = "SELECT * FROM Salonlar";

        try (Connection conn = DataBaseHelper.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Salon salon = new Salon(
                        rs.getInt("salon_id"),
                        rs.getString("ad"),
                        rs.getInt("kapasite")
                );
                salonlar.add(salon);
            }
        } catch (SQLException e) {
            System.out.println("Salonları getirme hatası: " + e.getMessage());
        }
        return salonlar;
    }

    /**
     * Veritabanına yeni bir salon ekler.
     * @param ad Salon adı (örn: "Salon 1")
     * @param kapasite Salon kapasitesi (örn: 100)
     */
    public void addSalon(String ad, int kapasite) {
        String sql = "INSERT INTO Salonlar(ad, kapasite) VALUES(?, ?)";

        try (Connection conn = DataBaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, ad);
            pstmt.setInt(2, kapasite);
            pstmt.executeUpdate();
            System.out.println("Yeni salon eklendi: " + ad);

        } catch (SQLException e) {
            System.out.println("Salon ekleme hatası: " + e.getMessage());
        }
    }

    /**
     * Bir salonu ID'sine göre siler.
     * @param salonId Silinecek salonun ID'si
     */
    public void deleteSalon(int salonId) {
        String sql = "DELETE FROM Salonlar WHERE salon_id = ?";

        try (Connection conn = DataBaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, salonId);
            pstmt.executeUpdate();
            System.out.println("Salon silindi, ID: " + salonId);

        } catch (SQLException e) {
            System.out.println("Salon silme hatası: " + e.getMessage());
        }
    }

    /**
     * Mevcut bir salonun bilgilerini günceller.
     * YENİ EKLENEN METOT
     * @param salon Güncellenmiş bilgileri içeren Salon nesnesi
     */
    public void updateSalon(Salon salon) {
        String sql = "UPDATE Salonlar SET ad = ?, kapasite = ? WHERE salon_id = ?";

        try (Connection conn = DataBaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Salon modelimiz JavaFX Property'leri kullandığı için .get() ile alıyoruz
            pstmt.setString(1, salon.getAd());
            pstmt.setInt(2, salon.getKapasite());
            pstmt.setInt(3, salon.getSalonId());
            pstmt.executeUpdate();
            System.out.println("Salon güncellendi: " + salon.getAd());

        } catch (SQLException e) {
            System.out.println("Salon güncelleme hatası: " + e.getMessage());
        }
    }
}