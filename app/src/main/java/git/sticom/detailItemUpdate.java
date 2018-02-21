package git.sticom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import git.sticom.util.Server;

@SuppressWarnings("deprecation")
public class detailItemUpdate extends AppCompatActivity{
private static String TAG = detailItemUpdate.class.getSimpleName();
	
	private Toolbar mToolbar;
	
JSONArray string_json = null;
	
	SessionManager session;

	private ProgressDialog pDialog;

	JSONParser jsonParser = new JSONParser();
	
    TextView id_produk;
	String idproduks;
	int i;
	private TextView textQty;
	List<String> list = new ArrayList<String>();

	public static final String TAG_IDPRODUK = "id_produk";
	public static final String TAG_PRODUK = "produk";
	public static final String TAG_HARGA = "harga";
	public static final String TAG_QTY = "qty";
	public static final String TAG_GAMBAR = "gambar";
	public static final String TAG_SUKSES = "sukses";
	
	private static final String url_ambil_update = Server.URL+"detailitemupdate.php";
	private static String url_update_item = Server.URL+"updateitem.php";
	private static String url_hapus_item = Server.URL+"hapusitem.php";

	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_update);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
		
		session = new SessionManager(getApplicationContext());

		Intent i = getIntent();

		idproduks = i.getStringExtra(TAG_IDPRODUK);
		
		id_produk = (TextView) findViewById(R.id.idproduk);
		textQty = (TextView) findViewById(R.id.textQty);
		
		
		new AmbilItem().execute();
		
		// inisialisasi  button
		Button btnUpdate = (Button) findViewById(R.id.btnUpdate);
		Button btnDelete = (Button) findViewById(R.id.btnDelete);

		// klik even tombol update item produk
		btnUpdate.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				new UpdateItem().execute();
			}
		});
		

		// klik even tombol hapus item produk
		btnDelete.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				// buat method pada background thread
				new HapusItem().execute();
			}
		});
		
	}
	
	class UpdateItem extends AsyncTask<String, String, String>{
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(detailItemUpdate.this);
			pDialog.setMessage("Update Item...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			session.checkSession();
			HashMap<String, String> user = session.getUserDetails();
			String idsesi = user.get(SessionManager.KEY_NAME);
			String idproduk = id_produk.getText().toString();
			 String qty =  textQty.getText().toString();
			// Membangun Parameters
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("idproduk", idproduk));
						params.add(new BasicNameValuePair("idsesi", idsesi));
						params.add(new BasicNameValuePair("qty", qty));

						// mengirim data yang diupdate lewat request http
						// Dengan method POST
						JSONObject json = jsonParser.makeHttpRequest(url_update_item,"POST", params);

						// cek json sukses tag (apakah 1 atau 0)
						try {
							int sukses = json.getInt(TAG_SUKSES);
							
							if (sukses == 1) {
								// sukses mengupdate data
								Intent i = new Intent(detailItemUpdate.this, Keranjang.class);
				                startActivity(i);
								// kirim result code 100 untuk notifikasi kalau simpan dilaksanakan
								finish();
							} else {
								Log.d("log: ", json.toString());
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
	
	class AmbilItem extends AsyncTask<String, String, String>{
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(detailItemUpdate.this);
			pDialog.setMessage("Mohon Tunggu ... !");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			session.checkSession();
			HashMap<String, String> user = session.getUserDetails();
			String idsesi = user.get(SessionManager.KEY_NAME);
			String idproduk = idproduks;

					try {

						List<NameValuePair> params1 = new ArrayList<NameValuePair>();
						params1.add(new BasicNameValuePair("idproduk",idproduk));
						params1.add(new BasicNameValuePair("idsesi",idsesi));

						JSONObject json = jsonParser.makeHttpRequest(
								url_ambil_update, "POST", params1);
						string_json = json.getJSONArray("item");
						Log.d("log: ", json.toString());

							runOnUiThread(new Runnable() {
								public void run() {

									ImageView thumb_image = (ImageView) findViewById(R.id.imageView);
									TextView idproduk = (TextView) findViewById(R.id.idproduk);
									TextView produk = (TextView) findViewById(R.id.produk);
							        TextView harga = (TextView) findViewById(R.id.harga);
							        TextView quantity = (TextView) findViewById(R.id.quantity);
									TextView jumlah = (TextView) findViewById(R.id.textQty); 

							try {

								// ambil objek member pertama dari JSON Array
								JSONObject ar = string_json.getJSONObject(0);
								String produk_d = ar.getString("nama");
								String idproduk_d = ar.getString("id_produk");
								String harga_d = ar.getString("harga");
								String Quantity = ar.getString("qty");
								
						   idproduk.setText(idproduk_d);
						   produk.setText(produk_d);
					       harga.setText(harga_d);
					       harga.setText("Harga : Rp."+ harga_d + ",-");
					       quantity.setText("Quantity");
					       quantity = (TextView) findViewById(R.id.quantity);
					       jumlah.setText(Quantity);
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
	
	class HapusItem extends AsyncTask<String, String, String>{
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(detailItemUpdate.this);
			pDialog.setMessage("Menghapus Item ... !");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			session.checkSession();
			HashMap<String, String> user = session.getUserDetails();
			String idsesi = user.get(SessionManager.KEY_NAME);
			String idproduk = id_produk.getText().toString();
			// Membangun Parameters
						List<NameValuePair> params1 = new ArrayList<NameValuePair>();
						params1.add(new BasicNameValuePair("idproduk", idproduk));
						params1.add(new BasicNameValuePair("idsesi", idsesi));

						// mengirim data yang diupdate lewat request http
						// Dengan method POST
						JSONObject json = jsonParser.makeHttpRequest(url_hapus_item,"POST", params1);

						// cek json sukses tag (apakah 1 atau 0)
						try {
							int sukses = json.getInt(TAG_SUKSES);
							
							if (sukses == 0) {
								// sukses mengupdate data
								Intent k = new Intent(detailItemUpdate.this, Keranjang.class);
				                startActivity(k);
								// kirim result code 100 untuk notifikasi kalau simpan dilaksanakan
								finish();
							} else {
								Log.d("log: ", json.toString());
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
	
}
