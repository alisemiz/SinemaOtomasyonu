package db;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.BiletDetay; // YENİ IMPORT: Raporlama modeli

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class BiletDAO {

    /**
     * Belirli bir seanstaki tüm satılmış koltuk numaralarını getirir.
     */
    public ObservableList<String> getDoluKoltuklarBySeansId(int seansId) {
        ObservableList<String> doluKoltuklar = FXCollections.observableArrayList();
        String sql = "SELECT koltuk_no FROM Biletler WHERE seans_id = ?";

        try (Connection conn = DataBaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, seansId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                doluKoltuklar.add(rs.getString("koltuk_no"));
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println("Dolu koltukları getirme hatası: " + e.getMessage());
        }
        return doluKoltuklar;
    }

    /**
     * Veritabanına yeni biletler (koltuklar) kaydeder.
     */
    public void addBiletler(int seansId, List<String> secilenKoltuklar, String musteriAd) {
        String sql = "INSERT INTO Biletler(seans_id, koltuk_no, musteri_ad) VALUES(?,?,?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DataBaseHelper.connect();
            conn.setAutoCommit(false);
            pstmt = conn.prepareStatement(sql);

            for (String koltukNo : secilenKoltuklar) {
                pstmt.setInt(1, seansId);
                pstmt.setString(2, koltukNo);
                pstmt.setString(3, musteriAd);
                pstmt.addBatch();
            }

            pstmt.executeBatch();
            conn.commit();
            System.out.println(secilenKoltuklar.size() + " adet bilet başarıyla eklendi.");

        } catch (SQLException e) {
            System.out.println("Bilet ekleme (toplu) hatası: " + e.getMessage());
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException e2) {
                System.out.println("Rollback hatası: " + e2.getMessage());
            }
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                System.out.println("Bağlantı kapatma hatası: " + e.getMessage());
            }
        }
    }

    /**
     * Satılan tüm biletlerin detaylı bilgilerini getirir (Raporlama için).
     * YENİ EKLENEN METOT
     * Biletler tablosunu Seanslar, Filmler ve Salonlar tablolarıyla birleştirir.
     * @return BiletDetay nesnelerinden oluşan ObservableList
     */
    public ObservableList<BiletDetay> getAllBiletDetaylari() {
        ObservableList<BiletDetay> biletDetaylari = FXCollections.observableArrayList();

        // Çoklu JOIN sorgusu: Bilet -> Seans -> Film & Salon
        String sql = "SELECT " +
                "b.bilet_id, f.ad AS film_adi, sl.ad AS salon_adi, s.tarih_saat, b.koltuk_no, b.musteri_ad " +
                "FROM Biletler b " +
                "JOIN Seanslar s ON b.seans_id = s.seans_id " +
                "JOIN Filmler f ON s.film_id = f.film_id " +
                "JOIN Salonlar sl ON s.salon_id = sl.salon_id " +
                "ORDER BY s.tarih_saat DESC, b.bilet_id ASC"; // Önce seansa, sonra bilet ID'sine göre sırala

        try (Connection conn = DataBaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                BiletDetay detay = new BiletDetay(
                        rs.getInt("bilet_id"),
                        rs.getString("film_adi"),
                        rs.getString("salon_adi"),
                        rs.getString("tarih_saat"),
                        rs.getString("koltuk_no"),
                        rs.getString("musteri_ad")
                );
                biletDetaylari.add(detay);
            }
        } catch (SQLException e) {
            System.out.println("Bilet detaylarını getirme hatası: " + e.getMessage());
            e.printStackTrace(); // Hatanın detayını görmek için
        }
        return biletDetaylari;
    }
}