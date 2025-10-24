# Sinema Otomasyon Sistemi

Bu proje, JavaFX ve SQLite kullanılarak geliştirilmiş bir masaüstü sinema otomasyon sistemidir. Hem sinema yöneticilerinin film, salon ve seansları yönetmesine hem de kullanıcıların seansları görüp koltuk seçerek bilet almasına olanak tanır.

## ✨ Temel Özellikler

* **Yönetici ve Kullanıcı Rolleri:**
    * Basit bir giriş ekranı ile "admin" ve "user" rolleri ayrılmıştır.
    * Adminler tüm yönetim fonksiyonlarına erişebilirken, kullanıcılar sadece film/seans görüntüleme ve bilet alma işlemlerini yapabilir.
* **Film Yönetimi:**
    * Yeni film ekleme (Ad, Yönetmen, Süre, Afiş URL'si).
    * Mevcut filmleri düzenleme ve silme.
    * Filmlere ait afişlerin URL üzerinden yüklenip gösterilmesi.
* **Salon Yönetimi:**
    * Yeni sinema salonu ekleme (Ad, Kapasite).
    * Mevcut salonları düzenleme ve silme.
* **Seans Yönetimi (Admin):**
    * Filmleri, salonları ve tarih/saati seçerek yeni seanslar oluşturma.
    * Mevcut seansları listeleme ve silme.
* **Seans Listeleme (Kullanıcı):**
    * Ana ekranda bir filme tıklandığında, o filme ait seansların (Salon, Tarih/Saat) listelenmesi.
* **Dinamik Koltuk Seçimi:**
    * Bir seans seçilip "Bilet Al"a basıldığında, salonun kapasitesine göre dinamik olarak koltuk düzeninin oluşturulması.
    * O seans için daha önce satılmış (dolu) koltukların kırmızı ve pasif olarak gösterilmesi.
    * Kullanıcının boş koltukları seçebilmesi (seçilenler sarı olur).
* **Bilet Satış:**
    * Seçilen koltukların ve (isteğe bağlı) müşteri adının veritabanına kaydedilmesi.
* **Bilet Raporlama (Admin):**
    * Satılan tüm biletlerin detaylı (Film, Salon, Seans, Koltuk, Müşteri) bir listesini gösteren rapor ekranı.

## 🛠️ Kullanılan Teknolojiler

* **Java:** Ana programlama dili.
* **JavaFX:** Modern masaüstü kullanıcı arayüzü (GUI) oluşturmak için kullanıldı (FXML ile birlikte).
* **SQLite:** Sunucusuz, dosya tabanlı bir veritabanı yönetim sistemi. Verilerin yerel olarak saklanması için kullanıldı.
* **JDBC (SQLite JDBC Driver):** Java'nın SQLite veritabanı ile iletişim kurmasını sağlayan sürücü.

## 🚀 Kurulum ve Çalıştırma

1.  Projeyi klonlayın veya indirin.
2.  Bir Java IDE (IntelliJ IDEA önerilir) ile projeyi açın.
3.  JavaFX SDK'sının ve SQLite JDBC sürücüsünün projeye kütüphane olarak eklendiğinden emin olun.
    * JavaFX SDK'sı için gerekli VM seçeneklerinin (`--module-path`, `--add-modules`) Run Configuration'da ayarlanması gerekebilir.
4.  `Main.java` sınıfını çalıştırın.
5.  Uygulama ilk çalıştığında proje dizininde `cinema.db` adında bir veritabanı dosyası otomatik olarak oluşturulacaktır.
6.  Giriş yapmak için şu bilgileri kullanabilirsiniz:
    * **Admin:** Kullanıcı Adı: `admin`, Şifre: `admin`
    * **Kullanıcı:** Kullanıcı Adı: `user`, Şifre: `user`

## 📸 Ekran Görüntüleri



## 🔮 Gelecek Geliştirmeler

* CSS ile arayüz stilinin iyileştirilmesi.
* Daha detaylı raporlama ve filtreleme seçenekleri.
* Gerçek kullanıcı kayıt ve şifre yönetimi.
* Bilet yazdırma veya PDF oluşturma özelliği.
* Arama ve filtreleme fonksiyonları.
