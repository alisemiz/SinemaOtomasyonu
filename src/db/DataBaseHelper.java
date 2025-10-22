package db; // Az önce oluşturduğumuz paketin adı

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DataBaseHelper {

    // Projemizin ana klasöründe 'cinema.db' adında bir veritabanı dosyası oluşturacak
    private static final String DB_URL = "jdbc:sqlite:cinema.db";

    /**
     * Veritabanına bağlantı kurar.
     * @return Connection nesnesi
     */
    public static Connection connect() {
        Connection conn = null;
        try {
            // SQLite JDBC sürücüsünü kullanarak bağlantı aç
            conn = DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            System.out.println("SQLite bağlantı hatası: " + e.getMessage());
        }
        return conn;
    }

    /**
     * Uygulama ilk çalıştığında veritabanı tablolarını oluşturur.
     * 'IF NOT EXISTS' sayesinde, tablolar zaten varsa tekrar oluşturulmaz.
     */
    public static void createTables() {
        // Filmler tablosu
        String sqlFilmler = "CREATE TABLE IF NOT EXISTS Filmler ("
                + " film_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " ad TEXT NOT NULL,"
                + " yonetmen TEXT,"
                + " sure_dk INTEGER,"
                + " poster_url TEXT" // İleride film afişleri için kullanabiliriz
                + ");";

        // Salonlar tablosu
        String sqlSalonlar = "CREATE TABLE IF NOT EXISTS Salonlar ("
                + " salon_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " ad TEXT NOT NULL,"
                + " kapasite INTEGER"
                + ");";

        // Seanslar tablosu
        String sqlSeanslar = "CREATE TABLE IF NOT EXISTS Seanslar ("
                + " seans_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " film_id INTEGER NOT NULL,"
                + " salon_id INTEGER NOT NULL,"
                + " tarih_saat TEXT NOT NULL," // 'YYYY-MM-DD HH:MM:SS' formatında saklayacağız
                + " FOREIGN KEY (film_id) REFERENCES Filmler (film_id) ON DELETE CASCADE,"
                + " FOREIGN KEY (salon_id) REFERENCES Salonlar (salon_id) ON DELETE CASCADE"
                + ");";

        // Biletler tablosu
        String sqlBiletler = "CREATE TABLE IF NOT EXISTS Biletler ("
                + " bilet_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " seans_id INTEGER NOT NULL,"
                + " koltuk_no TEXT NOT NULL," // "A5", "B12" gibi
                + " musteri_ad TEXT,"
                + " FOREIGN KEY (seans_id) REFERENCES Seanslar (seans_id) ON DELETE CASCADE"
                + ");";

        // 'try-with-resources' bloğu, bağlantıyı ve statement'ı otomatik kapatır
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {

            // Tabloları oluştur
            stmt.execute(sqlFilmler);
            stmt.execute(sqlSalonlar);
            stmt.execute(sqlSeanslar);
            stmt.execute(sqlBiletler);

            System.out.println("Veritabanı tabloları başarıyla oluşturuldu veya zaten mevcuttu.");

        } catch (SQLException e) {
            System.out.println("Tablo oluşturma hatası: " + e.getMessage());
        }
    }
}