package db;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Seans;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SeansDAO {

    /**
     * Veritabanındaki tüm seansları, film ve salon adlarıyla birlikte getirir.
     * GÜNCELLENDİ: Artık salon_id ve kapasite bilgilerini de sorguya ekliyor.
     */
    public ObservableList<Seans> getAllSeanslar() {
        ObservableList<Seans> seanslar = FXCollections.observableArrayList();

        // Sorguya sl.salon_id, sl.kapasite ve f.film_id eklendi
        String sql = "SELECT s.seans_id, f.film_id, f.ad AS film_adi, sl.salon_id, sl.ad AS salon_adi, sl.kapasite, s.tarih_saat "
                + "FROM Seanslar s "
                + "JOIN Filmler f ON s.film_id = f.film_id "
                + "JOIN Salonlar sl ON s.salon_id = sl.salon_id "
                + "ORDER BY s.tarih_saat DESC";

        try (Connection conn = DataBaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                // Yeni Seans constructor'ına göre güncellendi
                Seans seans = new Seans(
                        rs.getInt("seans_id"),
                        rs.getInt("film_id"),
                        rs.getString("film_adi"),
                        rs.getInt("salon_id"),
                        rs.getString("salon_adi"),
                        rs.getInt("kapasite"),
                        rs.getString("tarih_saat")
                );
                seanslar.add(seans);
            }
        } catch (SQLException e) {
            System.out.println("Seansları getirme hatası: " + e.getMessage());
        }
        return seanslar;
    }

    /**
     * Belirli bir filme ait tüm seansları getirir.
     * YENİ EKLENEN METOT
     * @param filmId Seansları aranacak filmin ID'si
     * @return O filme ait seansların listesi
     */
    public ObservableList<Seans> getSeanslarByFilmId(int filmId) {
        ObservableList<Seans> seanslar = FXCollections.observableArrayList();

        // Sorgu, getAllSeanslar'a benziyor ancak film_id'ye göre filtreleniyor (WHERE f.film_id = ?)
        String sql = "SELECT s.seans_id, f.film_id, f.ad AS film_adi, sl.salon_id, sl.ad AS salon_adi, sl.kapasite, s.tarih_saat "
                + "FROM Seanslar s "
                + "JOIN Filmler f ON s.film_id = f.film_id "
                + "JOIN Salonlar sl ON s.salon_id = sl.salon_id "
                + "WHERE f.film_id = ? " // Burası önemli
                + "ORDER BY s.tarih_saat ASC"; // Müşteri için eskiden yeniye sıralama

        try (Connection conn = DataBaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, filmId); // Parametreyi ata
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Seans seans = new Seans(
                        rs.getInt("seans_id"),
                        rs.getInt("film_id"),
                        rs.getString("film_adi"),
                        rs.getInt("salon_id"),
                        rs.getString("salon_adi"),
                        rs.getInt("kapasite"),
                        rs.getString("tarih_saat")
                );
                seanslar.add(seans);
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println("Filme göre seans getirme hatası: " + e.getMessage());
        }
        return seanslar;
    }

    /**
     * Yeni bir seans ekler.
     */
    public void addSeans(int filmId, int salonId, String tarihSaat) {
        // ... (Bu metot değişmedi)
        String sql = "INSERT INTO Seanslar(film_id, salon_id, tarih_saat) VALUES(?,?,?)";

        try (Connection conn = DataBaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, filmId);
            pstmt.setInt(2, salonId);
            pstmt.setString(3, tarihSaat);
            pstmt.executeUpdate();
            System.out.println("Yeni seans eklendi.");

        } catch (SQLException e) {
            System.out.println("Seans ekleme hatası: " + e.getMessage());
        }
    }

    /**
     * Bir seansı ID'sine göre siler.
     */
    public void deleteSeans(int seansId) {
        // ... (Bu metot değişmedi)
        String sql = "DELETE FROM Seanslar WHERE seans_id = ?";

        try (Connection conn = DataBaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, seansId);
            pstmt.executeUpdate();
            System.out.println("Seans silindi, ID: " + seansId);

        } catch (SQLException e) {
            System.out.println("Seans silme hatası: " + e.getMessage());
        }
    }
}