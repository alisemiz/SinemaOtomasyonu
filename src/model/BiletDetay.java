package model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Bilet raporlama ekranında gösterilecek detaylı bilgileri tutan model sınıfı.
 * Bu sınıf, Biletler, Seanslar, Filmler ve Salonlar tablolarının
 * JOIN işlemi sonucu elde edilen verileri temsil eder.
 */
public class BiletDetay {

    private final IntegerProperty biletId;
    private final StringProperty filmAdi;
    private final StringProperty salonAdi;
    private final StringProperty seansTarihSaat;
    private final StringProperty koltukNo;
    private final StringProperty musteriAdi;

    public BiletDetay(int biletId, String filmAdi, String salonAdi, String seansTarihSaat, String koltukNo, String musteriAdi) {
        this.biletId = new SimpleIntegerProperty(biletId);
        this.filmAdi = new SimpleStringProperty(filmAdi);
        this.salonAdi = new SimpleStringProperty(salonAdi);
        this.seansTarihSaat = new SimpleStringProperty(seansTarihSaat);
        this.koltukNo = new SimpleStringProperty(koltukNo);
        this.musteriAdi = new SimpleStringProperty(musteriAdi);
    }

    // --- Standart Getter Metotları ---
    public int getBiletId() { return biletId.get(); }
    public String getFilmAdi() { return filmAdi.get(); }
    public String getSalonAdi() { return salonAdi.get(); }
    public String getSeansTarihSaat() { return seansTarihSaat.get(); }
    public String getKoltukNo() { return koltukNo.get(); }
    public String getMusteriAdi() { return musteriAdi.get(); }

    // --- Property Getter Metotları (JavaFX TableView için) ---
    public IntegerProperty biletIdProperty() { return biletId; }
    public StringProperty filmAdiProperty() { return filmAdi; }
    public StringProperty salonAdiProperty() { return salonAdi; }
    public StringProperty seansTarihSaatProperty() { return seansTarihSaat; }
    public StringProperty koltukNoProperty() { return koltukNo; }
    public StringProperty musteriAdiProperty() { return musteriAdi; }
}