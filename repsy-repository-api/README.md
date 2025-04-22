# Repsy Repository API

Repsy Repository API, paket yönetim sistemleri için paket depolama ve indirme işlevselliği sağlayan bir RESTful API uygulamasıdır. Bu uygulama, paketleri ve meta verilerini iki farklı depolama yöntemiyle (dosya sistemi veya MinIO) saklama esnekliği sunar.

## 📋 Özellikler

- ✅ Paket ve meta veri dosyalarını (.rep ve .json) yükleme ve indirme
- ✅ İki farklı depolama stratejisi: Dosya Sistemi veya MinIO (S3 uyumlu nesne depolama)
- ✅ Docker Compose ile tek komutla kurulum ve çalıştırma
- ✅ PostgreSQL veritabanı ile meta veri yönetimi
- ✅ Strateji tasarım deseni ile genişletilebilir mimari

## 🛠️ Gereksinimler

- Java JDK 17 veya üstü
- Maven (ya da proje içindeki mvnw kullanılabilir)
- Docker ve Docker Compose
- En az 2GB boş disk alanı

## 🚀 Kurulum ve Çalıştırma

### 1️⃣ Projeyi Klonlayın

```bash
git clone https://github.com/username/repsy-repository-api.git
cd repsy-repository-api
```

### 2️⃣ Maven ile Derleyin

```bash
# Windows
./mvnw.cmd clean package -DskipTests

# Linux/macOS
./mvnw clean package -DskipTests
```

### 3️⃣ Docker Compose ile Çalıştırın

```bash
docker-compose up -d
```

Bu komut, üç ayrı servisi başlatır:
- PostgreSQL: Meta verileri saklamak için veritabanı (port: 5432)
- MinIO: S3 uyumlu nesne depolama (port: 9000, 9001)
- Spring Boot API: REST API servisi (port: 8080)

## ⚙️ Yapılandırma

### Depolama Stratejisi Seçimi

Uygulamanız iki farklı depolama yöntemi kullanabilir:

1. **Dosya Sistemi (file-system)**: Tüm paketler yerel diskte `/storage` dizininde saklanır
2. **Nesne Depolama (object-storage)**: Paketler MinIO sunucusunda saklanır

Docker Compose ile çalıştırırken depolama stratejisini seçmek için:

```yaml
# docker-compose.yml içinde
environment:
  # ...
  STORAGE_STRATEGY: file-system  # ya da object-storage
```

Değişiklik yaptıktan sonra servisleri yeniden başlatın:
```bash
docker-compose down
docker-compose up -d
```

### Önemli Yapılandırma Parametreleri

| Parametre | Açıklama | Varsayılan Değer |
|-----------|----------|------------------|
| `STORAGE_STRATEGY` | Depolama stratejisi (file-system veya object-storage) | file-system |
| `storage.file-system.root-dir` | Dosya sistemi depolama dizini | ./storage |
| `minio.endpoint` | MinIO sunucu adresi | http://localhost:9000 |
| `minio.bucket-name` | MinIO bucket adı | repsy-packages |

## 📡 API Kullanımı

### 📤 Paket Yükleme

**Endpoint:** `POST /{packageName}/{version}`  
**Content-Type:** `multipart/form-data`

**Parametreler:**
- `packageName`: Paketin adı (URL yolunda)
- `version`: Paketin sürümü (URL yolunda)
- `file`: Yüklenecek dosya (form-data olarak)

**Örnek Kullanım:**

```bash
# meta.json yükleme
curl.exe -X POST -F "file=@meta.json" http://localhost:8080/mypackage/1.0.0

# package.rep yükleme
curl.exe -X POST -F "file=@package.rep" http://localhost:8080/mypackage/1.0.0
```

### 📥 Paket İndirme

**Endpoint:** `GET /{packageName}/{version}/{fileName}`

**Parametreler:**
- `packageName`: Paketin adı (URL yolunda)
- `version`: Paketin sürümü (URL yolunda)
- `fileName`: İndirilecek dosyanın adı (package.rep veya meta.json)

**Örnek Kullanım:**

```bash
# meta.json indirme
curl.exe -o indirilen_meta.json http://localhost:8080/mypackage/1.0.0/meta.json

# package.rep indirme
curl.exe -o indirilen_paket.rep http://localhost:8080/mypackage/1.0.0/package.rep
```

Tarayıcı üzerinden de indirme yapabilirsiniz:
```
http://localhost:8080/mypackage/1.0.0/meta.json
```

## 📁 Proje Yapısı

```
src/main/java/com/repsy/repsy_repository_api/
├── controller/                  # REST API controller sınıfları
│   └── PackageController.java   # Paket yükleme ve indirme endpoint'leri
├── model/                       # Veri modeli ve entity sınıfları
│   ├── Package.java             # Veritabanı entity sınıfı
│   └── MetaDataDTO.java         # Meta veri transfer nesnesi
├── repository/                  # Veritabanı erişim katmanı
│   └── PackageRepository.java   # Spring Data JPA repository
├── service/                     # İş mantığı ve strateji sınıfları
│   ├── StorageService.java      # Depolama servisi
│   ├── StorageStrategy.java     # Strateji arayüzü
│   ├── FileSystemStorageStrategy.java  # Dosya sistemi stratejisi
│   └── ObjectStorageStrategy.java      # MinIO stratejisi
└── RepsyRepositoryApiApplication.java  # Ana uygulama sınıfı
```

## 🔍 Servislere Erişim

### MinIO Konsol Erişimi

- **URL:** http://localhost:9001
- **Kullanıcı adı:** minioadmin
- **Şifre:** minioadmin

Bu konsol üzerinden yüklenen paketleri görüntüleyebilir, yönetebilir ve manuel olarak indirebilirsiniz.

### PostgreSQL Veritabanı Erişimi

```bash
# Veritabanı konteyneri içinde SQL sorguları çalıştırma
docker exec -it repsy_postgres psql -U repsy_user -d repsy_db

# Paket tablosunu görüntüleme
docker exec -it repsy_postgres psql -U repsy_user -d repsy_db -c "SELECT * FROM package;"
```

## 🔧 Sorun Giderme

### Windows'ta curl Kullanımı

Windows PowerShell'de `curl` komutu `Invoke-WebRequest`'in bir takma adıdır ve Linux/macOS'taki curl ile farklıdır. Windows'ta gerçek curl komutunu kullanmak için:

```powershell
# curl.exe kullanımı (Windows 10/11'de mevcuttur)
curl.exe -X POST -F "file=@meta.json" http://localhost:8080/mypackage/1.0.0

# veya PowerShell'in kendi komutunu kullanma
Invoke-WebRequest -Uri "http://localhost:8080/mypackage/1.0.0/meta.json" -OutFile "indirilen.json"
```



### Volume Dizinleri

- MinIO verileri: `minio_data` Docker volume'unda saklanır
- PostgreSQL verileri: `postgres_data` Docker volume'unda saklanır
- Dosya sistemi depolaması: `./storage` dizininde saklanır (yerel makinenizde)

Tüm verileri sıfırlamak için:
```bash
docker-compose down -v
```

## 🧩 Proje Hakkında

Bu proje, modern yazılım mimarisi prensiplerini ve tasarım desenlerini uygulayarak paket yönetim sistemi için güçlü bir backend API oluşturur. Strateji tasarım deseni sayesinde, farklı depolama yöntemleri arasında geçiş yapabilir ve sistemi kolayca genişletebilirsiniz.

### Teknolojiler

- **Spring Boot**: Web API çerçevesi
- **Spring Data JPA**: Veritabanı erişim katmanı
- **PostgreSQL**: İlişkisel veritabanı
- **MinIO**: S3 uyumlu nesne depolama
- **Docker & Docker Compose**: Konteynerizasyon ve orkestrasyon
- **Maven**: Bağımlılık yönetimi ve derleme

---

Geliştirme ve katkılar için [Issues](https://github.com/username/repsy-repository-api/issues) sayfasını ziyaret edebilirsiniz. 