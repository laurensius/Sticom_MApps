package git.sticom;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import java.util.HashMap;

public class IndexActivity extends AppCompatActivity{
	private static String TAG = IndexActivity.class.getSimpleName();
	
	private Toolbar mToolbar;
	SessionManager sessionManager;
	
	private ImageButton order,cek, loginn, promo, konsul;
	private TextView welcome;

	String Nama="";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_index);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

		order = (ImageButton) findViewById(R.id.bOrder);
		cek = (ImageButton) findViewById(R.id.bCek);
		promo = (ImageButton) findViewById(R.id.bPromo);
		konsul = (ImageButton) findViewById(R.id.bKonsul);
		welcome = (TextView)findViewById(R.id.tvId);

		sessionManager = new SessionManager(getApplicationContext());
		HashMap<String, String> user = sessionManager.getUserDetails();
		Nama = user.get(SessionManager.kunci_nama);

		welcome.setText("Selamat Datang Saudara "+Nama);
		
		order.setOnClickListener(new View.OnClickListener()
		{
		
		@Override
		public void onClick(View arg0) {
				Intent daftar = new Intent(IndexActivity.this, Order_Activity.class);
				startActivity(daftar);	
			}
		});		
		cek.setOnClickListener(new View.OnClickListener()
		{
		
		@Override
		public void onClick(View arg0) {
				Intent daftar = new Intent(IndexActivity.this, Cek_Activity.class);
				startActivity(daftar);				
			}
		});
		promo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent promo = new Intent(IndexActivity.this, PromoActivity.class);
				startActivity(promo);
			}
		});
		konsul.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent konsul = new Intent(IndexActivity.this, KonsultasiActivity.class);
				startActivity(konsul);
			}
		});
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        menu.add(0, 0, 0, "About Apps");
        menu.add(0, 1, 0, "Keluar");
        return true;
    }
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (item.getItemId() == 0) {
            DialogAboutApps();
        }else if(item.getItemId() == 1){
			exit();
        }else{
            //
        }
        return super.onOptionsItemSelected(item);
    }
	
	private void DialogAboutApps() {
        LayoutInflater inflater;
        View dialogView;
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.about_app, null);
        new AlertDialog.Builder(this)
                .setTitle("About Apps")
                .setView(dialogView)
                .setNeutralButton("Kembali",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0,
                                                int arg1) {
                            }
                        }).show();
    }

	
	 public void exit() {
	        AlertDialog.Builder builder = new AlertDialog.Builder(IndexActivity.this);
	        builder.setMessage("Anda yakin ingin Keluar ?");
	        builder.setCancelable(false);
	        builder.setTitle("Peringatan");
	        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {

	            @Override
	            public void onClick(DialogInterface dialog, int which) {
	                // TODO Auto-generated method stub
	                IndexActivity.this.finish();
	            }
	        });
	        builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
	            @Override
	            public void onClick(DialogInterface dialog, int which) {
	                // TODO Auto-generated method stub
	                dialog.cancel();
	            }
	        });
	        AlertDialog alert = builder.create();
	        alert.show();
	    }

}