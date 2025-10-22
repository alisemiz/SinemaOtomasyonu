package model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Filmler tablosundaki bir kaydı temsil eden model sınıfı.
 * GÜNCELLENDİ: posterUrl alanı eklendi.
 */
public class Film {

    private final IntegerProperty filmId;
    private final StringProperty ad;
    private final StringProperty yonetmen;
    private final IntegerProperty sureDk;
    private final StringProperty posterUrl; // YENİ EKLENDİ

    /**
     * Film nesnesi oluşturucu (Constructor)
     * posterUrl eklendi
     */
    public Film(int filmId, String ad, String yonetmen, int sureDk, String posterUrl) {
        this.filmId = new SimpleIntegerProperty(filmId);
        this.ad = new SimpleStringProperty(ad);
        this.yonetmen = new SimpleStringProperty(yonetmen);
        this.sureDk = new SimpleIntegerProperty(sureDk);
        this.posterUrl = new SimpleStringProperty(posterUrl); // YENİ EKLENDİ
    }

    // --- Standart Getter Metotları ---
    public int getFilmId() { return filmId.get(); }
    public String getAd() { return ad.get(); }
    public String getYonetmen() { return yonetmen.get(); }
    public int getSureDk() { return sureDk.get(); }
    public String getPosterUrl() { return posterUrl.get(); } // YENİ EKLENDİ

    // --- Property Getter Metotları (JavaFX TableView için) ---
    public IntegerProperty filmIdProperty() { return filmId; }
    public StringProperty adProperty() { return ad; }
    public StringProperty yonetmenProperty() { return yonetmen; }
    public IntegerProperty sureDkProperty() { return sureDk; }
    public StringProperty posterUrlProperty() { return posterUrl; } // YENİ EKLENDİ

    /**
     * ComboBox (Açılır Liste) içinde filmlerin adını göstermek için
     */
    @Override
    public String toString() {
        return this.getAd();
    }
}