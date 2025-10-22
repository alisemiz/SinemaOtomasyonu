package model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Salonlar tablosundaki bir kaydı temsil eden model sınıfı.
 */
public class Salon {

    private final IntegerProperty salonId;
    private final StringProperty ad;
    private final IntegerProperty kapasite;

    public Salon(int salonId, String ad, int kapasite) {
        this.salonId = new SimpleIntegerProperty(salonId);
        this.ad = new SimpleStringProperty(ad);
        this.kapasite = new SimpleIntegerProperty(kapasite);
    }

    // --- Standart Getter Metotları ---
    public int getSalonId() { return salonId.get(); }
    public String getAd() { return ad.get(); }
    public int getKapasite() { return kapasite.get(); }

    // --- Property Getter Metotları (JavaFX için) ---
    public IntegerProperty salonIdProperty() { return salonId; }
    public StringProperty adProperty() { return ad; }
    public IntegerProperty kapasiteProperty() { return kapasite; }

    /**
     * ÖNEMLİ: ComboBox (Açılır Liste) içinde salonların ID'sini değil,
     * adını göstermek için 'toString' metodunu ezip (override)
     * salonun adını döndürmesini sağlıyoruz.
     */
    @Override
    public String toString() {
        return this.getAd(); // ComboBox'ta "Salon 1", "Salon 2" gibi görünecek
    }
}