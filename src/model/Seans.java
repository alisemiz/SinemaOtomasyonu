package model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Seanslar tablosundaki bir kaydı temsil eden model sınıfı.
 * GÜNCELLENDİ: Artık salon_id ve salon_kapasite bilgilerini de tutuyor.
 */
public class Seans {

    private final IntegerProperty seansId;
    private final StringProperty filmAd;
    private final StringProperty salonAd;
    private final StringProperty tarihSaat;

    // YENİ EKLENEN ALANLAR
    private final IntegerProperty salonId;
    private final IntegerProperty salonKapasite;
    private final IntegerProperty filmId;

    public Seans(int seansId, int filmId, String filmAd, int salonId, String salonAd, int salonKapasite, String tarihSaat) {
        this.seansId = new SimpleIntegerProperty(seansId);
        this.filmId = new SimpleIntegerProperty(filmId); // Eklendi
        this.filmAd = new SimpleStringProperty(filmAd);
        this.salonId = new SimpleIntegerProperty(salonId); // Eklendi
        this.salonAd = new SimpleStringProperty(salonAd);
        this.salonKapasite = new SimpleIntegerProperty(salonKapasite); // Eklendi
        this.tarihSaat = new SimpleStringProperty(tarihSaat);
    }

    // --- Standart Getter Metotları ---
    public int getSeansId() { return seansId.get(); }
    public String getFilmAd() { return filmAd.get(); }
    public String getSalonAd() { return salonAd.get(); }
    public String getTarihSaat() { return tarihSaat.get(); }
    public int getSalonId() { return salonId.get(); } // Eklendi
    public int getSalonKapasite() { return salonKapasite.get(); } // Eklendi
    public int getFilmId() { return filmId.get(); } // Eklendi


    // --- Property Getter Metotları (JavaFX TableView için) ---
    public IntegerProperty seansIdProperty() { return seansId; }
    public StringProperty filmAdProperty() { return filmAd; }
    public StringProperty salonAdProperty() { return salonAd; }
    public StringProperty tarihSaatProperty() { return tarihSaat; }
}