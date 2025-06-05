package com.example.donemodevi;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.donemodevi.databinding.ActivityMainBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private DatabaseHelper dbHelper;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        context = this;
        dbHelper = new DatabaseHelper(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // 1. MSKU klasör yapısı
        File rootDir = new File(getExternalFilesDir(null), "MSKU/TF/BSM");
        if (!rootDir.exists()) {
            if (rootDir.mkdirs()) {
                Toast.makeText(this, "Klasör oluşturuldu: " + rootDir.getPath(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Klasör oluşturulamadı!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Klasör zaten var: " + rootDir.getPath(), Toast.LENGTH_SHORT).show();
        }

        try {
            File myFile = new File(rootDir, "bilgiler.txt");
            FileWriter writer = new FileWriter(myFile);
            writer.write("Merhaba, bu benim ilk dosya yazım!");
            writer.close();
            Toast.makeText(this, "Dosya yazıldı: " + myFile.getPath(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Dosya yazılamadı: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        // 2. Merhaba mesajı
        binding.myButton.setOnClickListener(v -> {
            String name = binding.myEditText.getText().toString();
            binding.myTextView.setText("Merhaba, " + name + "!");
        });

        // 3. Fotoğraf çekme
        binding.btnTakePhoto.setOnClickListener(v -> {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        });

        // 4. İnternete bağlan
        binding.btnConnectToWeb.setOnClickListener(v -> new ConnectToInternetTask(context).execute());

        // 5. SMS gönderme
        binding.btnSendSms.setOnClickListener(v -> {
            String phone = binding.editTextPhone.getText().toString();
            String message = binding.editTextMessage.getText().toString();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 101);
            } else {
                SmsManager.getDefault().sendTextMessage(phone, null, message, null, null);
                Toast.makeText(this, "SMS Gönderildi", Toast.LENGTH_SHORT).show();
            }
        });

        // 6. Animasyon
        binding.btnStartAnimation.setOnClickListener(v -> {
            Animation rotate = AnimationUtils.loadAnimation(MainActivity.this, R.anim.rotate);
            binding.rotatingImage.startAnimation(rotate);
        });

        // 7. Konum alma
        binding.btnGetLocation.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            } else {
                getLastLocation();
            }
        });

        // 8. SQLite veriyi kaydet
        binding.btnSaveToDb.setOnClickListener(v -> {
            String title = binding.editTextTitle.getText().toString();
            String desc = binding.editTextDescription.getText().toString();
            boolean result = dbHelper.veriEkle(title, desc);
            if (result) {
                Toast.makeText(this, "Veri Kaydedildi", Toast.LENGTH_SHORT).show();
                Cursor cursor = dbHelper.veriGetir();
                StringBuilder sb = new StringBuilder();
                while (cursor.moveToNext()) {
                    sb.append("Başlık: ").append(cursor.getString(1)).append("\n");
                    sb.append("Açıklama: ").append(cursor.getString(2)).append("\n\n");
                }
                binding.txtSavedData.setText(sb.toString());
            }
        });
    }

    // Konum alma işlemi
    private void getLastLocation() {
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                try {
                    Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    if (!addresses.isEmpty()) {
                        String address = addresses.get(0).getAddressLine(0);
                        binding.txtLocation.setText("Adres: " + address);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    binding.txtLocation.setText("Hata: " + e.getMessage());
                }
            }
        });
    }

    // Fotoğraf sonucu
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
            binding.imageView.setImageBitmap(imageBitmap);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // İnternete bağlanma kontrolü
    private static class ConnectToInternetTask extends AsyncTask<Void, Void, Boolean> {
        private final Context context;

        public ConnectToInternetTask(Context context) {
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                HttpURLConnection urlc = (HttpURLConnection) new URL("https://www.google.com").openConnection();
                urlc.setRequestProperty("User-Agent", "Android");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                return urlc.getResponseCode() == 200;
            } catch (IOException e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean isConnected) {
            Toast.makeText(context, isConnected ? "İnternet bağlantısı başarılı!" : "İnternet bağlantısı yok!", Toast.LENGTH_SHORT).show();
        }
    }

    // İzin sonuçları
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLastLocation();
        } else if (requestCode == 101 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "SMS izni verildi. Tekrar deneyin.", Toast.LENGTH_SHORT).show();
        }
    }
}
