package git.sticom;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

public class Beranda extends AppCompatActivity {

    CarouselView cvBeranda;
    ListView lvTerlaris;
    Button btnLogin, btnDaftar;
    private Dialog dialBox;
    int[] sampleImages = {R.mipmap.image_1, R.mipmap.image_2, R.mipmap.image_3};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beranda);
        dialBox = createDialogBox();
        cvBeranda = findViewById(R.id.cv_slider);
        cvBeranda.setPageCount(sampleImages.length);
        cvBeranda.setImageListener(imageListener);
        lvTerlaris = (ListView) findViewById(R.id.lv_terlaris);
        btnLogin = (Button)findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Beranda.this,Login.class);
                startActivity(i);
                finish();
            }
        });
        btnDaftar = (Button)findViewById(R.id.btn_daftar);
        btnDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Beranda.this,RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            imageView.setImageResource(sampleImages[position]);
        }
    };

    @Override
    public void onBackPressed(){
        dialBox.show();
    }

    private Dialog createDialogBox(){
        dialBox = new AlertDialog.Builder(this)
                .setTitle("Konfirmasi Keluar")
                .setMessage("Apakah Anda yakin akan keluar dari aplikasi ?")
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        finish();
                    }
                })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialBox.dismiss();
                    }
                })
                .create();
        return dialBox;
    }
}
