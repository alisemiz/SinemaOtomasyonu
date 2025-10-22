package db;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Film;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Film veritabanı işlemleri (CRUD)
 * GÜNCELLENDİ: Tüm metotlar 'poster_url' alanını içerecek şekilde güncellendi.
 */
public class FilmDAO {

    /**
     * Veritabanındaki tüm filmleri getirir.
     * GÜNCELLENDİ: 'poster_url' sorguya ve constructor'a eklendi.
     */
    public ObservableList<Film> getAllFilmler() {
        ObservableList<Film> filmler = FXCollections.observableArrayList();
        // 'poster_url' sorguya eklendi
        String sql = "SELECT film_id, ad, yonetmen, sure_dk, poster_url FROM Filmler";

        try (Connection conn = DataBaseHelper.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                // Film constructor'ı yeni alana göre güncellendi
                Film film = new Film(
                        rs.getInt("film_id"),
                        rs.getString("ad"),
                        rs.getString("yonetmen"),
                        rs.getInt("sure_dk"),
                        rs.getString("poster_url") // YENİ EKLENDİ
                );
                filmler.add(film);
            }
        } catch (SQLException e) {
            System.out.println("Filmleri getirme hatası: " + e.getMessage());
        }
        return filmler;
    }

    /**
     * Yeni bir filmi veritabanına ekler.
     * GÜNCELLENDİ: 'poster_url' parametresi ve sorgusu eklendi.
     */
    public void addFilm(String ad, String yonetmen, int sureDk, String posterUrl) { // posterUrl eklendi
        String sql = "INSERT INTO Filmler(ad, yonetmen, sure_dk, poster_url) VALUES(?,?,?,?)"; // 4. '?' eklendi

        try (Connection conn = DataBaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, ad);
            pstmt.setString(2, yonetmen);
            pstmt.setInt(3, sureDk);
            pstmt.setString(4, posterUrl); // YENİ EKLENDİ

            pstmt.executeUpdate();
            System.out.println("Yeni film eklendi: " + ad);

        } catch (SQLException e) {
            System.out.println("Film ekleme hatası: " + e.getMessage());
        }
    }

    /**
     * Bir filmi ID'sine göre siler.
     * (Bu metotta değişiklik gerekmiyor)
     */
    public void deleteFilm(int filmId) {
        String sql = "DELETE FROM Filmler WHERE film_id = ?";

        try (Connection conn = DataBaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, filmId);
            pstmt.executeUpdate();
            System.out.println("Film silindi, ID: " + filmId);

        } catch (SQLException e) {
            System.out.println("Film silme hatası: " + e.getMessage());
        }
    }

    /**
     * Mevcut bir filmin bilgilerini günceller.
     * GÜNCELLENDİ: 'poster_url' sorguya eklendi.
     */
    public void updateFilm(Film film) {
        String sql = "UPDATE Filmler SET ad = ?, yonetmen = ?, sure_dk = ?, poster_url = ? WHERE film_id = ?"; // poster_url=? eklendi

        try (Connection conn = DataBaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, film.getAd());
            pstmt.setString(2, film.getYonetmen());
            pstmt.setInt(3, film.getSureDk());
            pstmt.setString(4, film.getPosterUrl()); // YENİ EKLENDİ
            pstmt.setInt(5, film.getFilmId()); // Sıra 5 oldu
            pstmt.executeUpdate();
            System.out.println("Film güncellendi: " + film.getAd());

        } catch (SQLException e) {
            System.out.println("Film güncelleme hatası: " + e.getMessage());
        }
    }
}