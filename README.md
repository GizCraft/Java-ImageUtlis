# ImageUtlis — Documentation

**ImageUtlis** adalah utilitas Android berbasis Java untuk menampilkan dan memanipulasi gambar secara programatis (tanpa XML). Library ini menyediakan fitur caching, transformasi, filter warna, serta pengaturan tampilan yang fleksibel dalam satu alur pemanggilan (fluent API).

---

## 📦 Import

```java
import com.gizdev.myapk.utlis.ImageUtlis;


```

## 🔧 Inisialisasi

Sebelum digunakan, disarankan untuk menginisialisasi cache pada onCreate() di Application atau Activity:
```java
ImageUtlis.init(context);
```
Jika tidak dipanggil, sistem akan melakukan inisialisasi otomatis saat instance pertama dibuat.

## 🖼️ Penggunaan Dasar
```java
new ImageUtlis(context)
    .load("https://example.com/image.jpg")
    .addTo(parentViewGroup);
```

## 📥 Sumber Gambar

# Metode	Deskripsi

load(String url)	Memuat gambar dari URL (HTTP/HTTPS)
res(int resId)	Memuat dari resource drawable
draw(Drawable)	Memuat dari objek Drawable
bmp(Bitmap)	Memuat dari Bitmap
file(File)	Memuat dari file lokal
asset(String)	Memuat dari folder assets
uri(Uri)	Memuat dari content URI


Contoh:

.load("https://picsum.photos/id/1/400/300")
.res(R.drawable.my_image)
.asset("images/photo.jpg")


---

🎨 Manipulasi Gambar

Bentuk & Border

Metode	Deskripsi

circle(boolean)	Membuat gambar lingkaran
radius(float)	Sudut melengkung (px)
stroke(int, int)	Border (width, color)


.circle(true)
.radius(30f)
.stroke(5, Color.WHITE)


---

Transformasi

Metode	Deskripsi

rotate(float)	Rotasi (derajat)
scale(float, float)	Skala horizontal & vertikal



---

Filter & Efek

Metode	Deskripsi

brightness(float)	Kecerahan (-255 s.d 255)
contrast(float)	Kontras (0.1 – 3.0)
saturation(float)	Saturasi warna
sharpen(float)	Ketajaman (eksperimental)
tint(int)	Overlay warna transparan
blur(float)	Efek blur
gradOverlay(...)	Overlay gradien


.brightness(30)
.contrast(1.2f)
.saturation(0.8f)
.tint(0x5500FF00)
.blur(8f)
.gradOverlay(
    new int[]{0x00000000, 0xAA000000},
    GradientDrawable.Orientation.TOP_BOTTOM
)


---

⏳ Placeholder & Error

Metode	Deskripsi

placeholder(int)	Gambar saat loading
errorImg(int)	Gambar saat gagal load


.placeholder(R.drawable.loading)
.errorImg(R.drawable.broken)


---

💾 Cache

.exp(3600) // cache 1 jam

Metode	Deskripsi

exp(long)	Durasi cache dalam detik



---

🎯 Event & Listener

.click(v -> Toast.makeText(ctx, "Klik", Toast.LENGTH_SHORT).show())
.listener(new ImageUtlis.OnImageLoadListener() {
    @Override public void onSuccess() {}
    @Override public void onError(Throwable t) {}
})


---

📐 Layout & Tampilan

Metode	Deskripsi

params(LayoutParams)	Set ukuran layout
pad(...)	Padding
margin(...)	Margin
scaleType(...)	ScaleType ImageView


.params(new LinearLayout.LayoutParams(-1, 400))
.margin(20,20,20,20)
.pad(10,10,10,10)
.scaleType(ImageView.ScaleType.CENTER_CROP)


---

✅ Finalisasi

Tambahkan ke layout:

.addTo(parentViewGroup);

Atau ambil instance ImageView:

ImageView img = imageUtlis.get();


---

📝 Contoh Lengkap

LinearLayout root = new LinearLayout(this);
root.setOrientation(LinearLayout.VERTICAL);

new ImageUtlis(this)
    .load("https://picsum.photos/id/10/600/400")
    .placeholder(android.R.drawable.ic_menu_gallery)
    .errorImg(android.R.drawable.ic_delete)
    .circle(true)
    .stroke(6, Color.WHITE)
    .brightness(20)
    .contrast(1.1f)
    .saturation(1.2f)
    .tint(0x33000000)
    .rotate(5f)
    .scale(1.05f, 1.05f)
    .click(v -> Toast.makeText(this, "Gambar", Toast.LENGTH_SHORT).show())
    .exp(7200)
    .params(new LinearLayout.LayoutParams(400, 400))
    .margin(20,20,20,20)
    .addTo(root);

setContentView(root);


---

⚠️ Ketentuan Penggunaan

1. Izin Internet



<uses-permission android:name="android.permission.INTERNET"/>

2. HTTP (Non-HTTPS)



android:usesCleartextTraffic="true"

3. Threading



Semua proses load berjalan di background thread.


4. Memory Cache



Menggunakan LruCache (maksimal ±50 bitmap).


5. Disk Cache



Disimpan di cacheDir dalam format PNG.


6. Cache Expired



File otomatis dihapus jika melewati masa berlaku.


7. Manajemen Bitmap



Recycle otomatis untuk mencegah memory leak.


8. Gambar Resolusi Besar



Disarankan menggunakan downsampling manual.


9. Sharpen



Masih tahap pengembangan.


10. Chain Method



Mendukung chaining dengan utilitas lain.



---

🛑 Penanganan Error

Semua error ditangkap internal.

Tidak menyebabkan crash.

Log tersedia di Logcat dengan tag: ImgUtl.



---

🔄 Kompatibilitas

Minimum API: 16 (Android 4.1)

Dependensi: AndroidX AppCompat

Tanpa library eksternal



---

📌 Catatan

Dirancang untuk proyek berbasis Java tanpa XML, dengan pendekatan programmatic UI sepenuhnya.


---
