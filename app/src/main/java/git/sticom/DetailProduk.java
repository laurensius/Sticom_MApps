package git.sticom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import git.sticom.util.Server;

public class DetailProduk extends AppCompatActivity {
private static String TAG = DetailProduk.class.getSimpleName();
	
	private Toolbar mToolbar;
	
JSONArray string_json = null;
	
	SessionManager session;

	String idproduk;
	EditText textQty;

	private ProgressDialog pDialog;

	JSONParser jsonParser = new JSONParser();

	public static final String TAG_ID = "id";
	public static final String TAG_PRODUK = "produk";
	public static final String TAG_DES = "deskripsi";
	public static final String TAG_GAMBAR = "gambar";
	private static final String TAG_SUKSES = "sukses";
	private static final String url_detail_produk = Server.URL+"detailproduk.php";
	private static final String url_tambah_keranjang = Server.URL+"tambahkeranjang.php";

	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.single_list_item);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
		
		session = new SessionManager(getApplicationContext());

		Intent i = getIntent();

		idproduk = i.getStringExtra(TAG_ID);
		textQty = (EditText) findViewById(R.id.textQty);
		textQty.setText("1");

		new AmbilDetailProduk().execute();
		
		// inisialisasi  button
		Button btnTambahKeranjang = (Button) findViewById(R.id.btnBeli);

		// klik even tombol SimpanKeranjang
		btnTambahKeranjang.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				// buat method pada background thread
				new simpanKeranjang().execute();
			}
		});

	}
	
	class simpanKeranjang extends AsyncTask<String, String, String>{

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(DetailProduk.this);
			pDialog.setMessage("Keranjang Belanja..silahkan tunggu");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}
		
		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			session.checkSession();  
			HashMap<String, String> user = session.getUserDetails();
			final String sesid = user.get(SessionManager.KEY_NAME);
			String id_produk = idproduk;
			String qty =  textQty.getText().toString();
			String session = sesid;
			// Membangun Parameters
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("id_produk", id_produk));
						params.add(new BasicNameValuePair("session", session));
						params.add(new BasicNameValuePair("qty", qty));

						// mengirim data yang diupdate lewat request http
						// Dengan method POST
						JSONObject json = jsonParser.makeHttpRequest(url_tambah_keranjang,"POST", params);

						// cek json sukses tag (apakah 1 atau 0)
						try {
							int sukses = json.getInt(TAG_SUKSES);
							
							if (sukses == 1) {
								// sukses mengupdate data
								Intent i = new Intent(DetailProduk.this, Keranjang.class);
				                startActivity(i);
								// kirim result code 100 untuk notifikasi kalau simpan dilaksanakan
								setResult(100, i);
								finish();
							} else {
								// gagal update data
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
			return null;
		}
		
		@Override
		protected void onPostExecute(String file_url) {
			// TODO Auto-generated method stub
			pDialog.dismiss();
		}
		
	}
	
	class AmbilDetailProduk extends AsyncTask<String, String, String>{
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(DetailProduk.this);
			pDialog.setMessage("Mohon Tunggu ... !");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {

				List<NameValuePair> params1 = new ArrayList<NameValuePair>();
				params1.add(new BasicNameValuePair("idproduk",idproduk));

				JSONObject json = jsonParser.makeHttpRequest(
						url_detail_produk, "GET", params1);
				string_json = json.getJSONArray("produk");

					runOnUiThread(new Runnable() {
						public void run() {

							ImageView thumb_image = (ImageView) findViewById(R.id.imageView1);
							TextView produk = (TextView) findViewById(R.id.produk);
					        TextView deskripsi = (TextView) findViewById(R.id.deskripsi);
					        TextView harga = (TextView) findViewById(R.id.harga);
					        

					try {
						
				        
						// ambil objek member pertama dari JSON Array
						JSONObject ar = string_json.getJSONObject(0);
						String produk_d = ar.getString("nama");
						String deskripsi_d = ar.getString("deskripsi");
						String harga_d = ar.getString("harga");
						
				
				   produk.setText(produk_d);
			       deskripsi.setText(deskripsi_d);
			       harga.setText("Harga : Rp."+ harga_d + ",-");
				   ImageLoader imageLoader = new ImageLoader(getApplicationContext());
			       imageLoader.DisplayImage(ar.getString(TAG_GAMBAR),thumb_image);		        
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						}
				}
			});
					
			} catch (JSONException e) {
				e.printStackTrace();
		}
			return null;
		}
		
		@Override
		protected void onPostExecute(String file_url) {
			// TODO Auto-generated method stub
			pDialog.dismiss();
		}
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.detail_menu, menu);
		return true;
	}
	public boolean onOptionsItemSelected(MenuItem item)
    {
         
        switch (item.getItemId())
        {
        case R.id.home:
            // Single menu item is selected do something
            // Ex: launching new activity/screen or show alert message
        	finish();
			Intent i = new Intent(getApplicationContext(), Order_Activity.class);
			startActivity(i);
            return true;
 
        case R.id.exit:
        	keluar();
        	return true;
 
        default:
            return super.onOptionsItemSelected(item);
        }
    }    
	public void keluar(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		 builder.setMessage("Apakah Anda Ingin" + " keluar?")
		 .setCancelable(false)
		 .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
		 public void onClick(DialogInterface dialog, int id) {
		 finish();
		 }
		 })
		 .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
		 public void onClick(DialogInterface dialog, int id) {
		 dialog.cancel();
		 }
		 }).show();
		}

}
