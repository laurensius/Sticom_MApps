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
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import git.sticom.util.Server;

@SuppressWarnings({ "deprecation", "unused" })
public class Keranjang extends AppCompatActivity{
private static String TAG = Keranjang.class.getSimpleName();
	
	private Toolbar mToolbar;
	
	JSONParser jParser = new JSONParser();
	private ProgressDialog pDialog;
	ArrayList<HashMap<String, String>> DaftarKeranjang = new ArrayList<HashMap<String, String>>();
	JSONArray keranjang = null;
	
	private static String url_keranjang_belanja = Server.URL+"keranjang.php";
	
	public static final String TAG_KERANJANG = "keranjang";
	public static final String TAG_SUKSES = "sukses";
	public static final String TAG_PRODUK = "nama";
	public static final String TAG_JUMLAH = "qty";
	public static final String TAG_HARGA = "harga";
	public static final String TAG_IDPRODUK = "id_produk";
	public static final String TAG_TOTHARGA = "totharga";
	public static final String TAG_SUBTOTAL = "subtotal";
	public static final String TAG_GAMBAR = "gambar";
	public static final String TAG_COUNT = "count";
	public static final String TAG_BERAT = "berat";


	JSONArray string_json = null;
    final Context context = this;
    TextView txtBerat;
	ListView list;
	KeranjangAdapter adapter;
	SessionManager session;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.keranjang);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
		
		session = new SessionManager(getApplicationContext());

		DaftarKeranjang = new ArrayList<HashMap<String, String>>();
		// inisialisasi  button
		Button btnLanjut = (Button) findViewById(R.id.btnLanjut);
		Button btnSelesai = (Button) findViewById(R.id.btnSelesai);
		txtBerat = (TextView) findViewById(R.id.berat);
		
		new AmbilKeranjang().execute();

		list = (ListView) findViewById(R.id.kat_list);
		
		 list.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
					HashMap<String, String> map = DaftarKeranjang.get(position);
	               
					Intent in = new Intent(getApplicationContext(), detailItemUpdate.class);
					in.putExtra(TAG_IDPRODUK, map.get(TAG_IDPRODUK));
					in.putExtra(TAG_GAMBAR, map.get(TAG_GAMBAR));
					startActivity(in);
				}
			});
		 
		// klik even tombol Kembali ke katalog
			btnLanjut.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View view) {
					Intent i = new Intent(Keranjang.this, Order_Activity.class);
	                startActivity(i);
					finish();
				}
			});
			
			// klik even tombol checkout
			btnSelesai.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View view) {
					String beratbuku = txtBerat.getText().toString();
					Intent i = new Intent(Keranjang.this, PelangganTrans.class);
					// kirim total berat ke activity berikutnya
					i.putExtra("beratbuku", beratbuku);
					startActivity(i);
					finish();
				}
			});

		}

	
	public void SetListViewAdapter(ArrayList<HashMap<String, String>> keranjang) {
		adapter = new KeranjangAdapter(this, keranjang);
		list.setAdapter(adapter);

	}
	
	class AmbilKeranjang extends AsyncTask<String, String, String>{
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(Keranjang.this);
			pDialog.setMessage("Mohon tunggu...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			HashMap<String, String> user = session.getUserDetails();
			final String idsesi = user.get(SessionManager.KEY_NAME);
			
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("idsesi",idsesi));
			JSONObject json = jParser.makeHttpRequest(url_keranjang_belanja, "POST", params);
			 Log.d("Loading", json.toString());

			try {

			// mengecek untuk TAG SUKSES
				int sukses = json.getInt(TAG_SUKSES);
				
				if (sukses == 1) {				
				 keranjang = json.getJSONArray(TAG_KERANJANG);
				
				for (int i = 0; i < keranjang.length(); i++) {
				JSONObject c = keranjang.getJSONObject(i);

					String produk = c.getString(TAG_PRODUK);
					String qty = "Qty : " + c.getString(TAG_JUMLAH)+ "X" + c.getString(TAG_HARGA);
					String link_image = c.getString(TAG_GAMBAR);
					String id_produk = c.getString(TAG_IDPRODUK);
					String totharga = "Total Harga = "+c.getString(TAG_TOTHARGA);

					HashMap<String, String> map = new HashMap<String, String>();

					map.put(TAG_PRODUK, produk);
					map.put(TAG_JUMLAH, qty);
					map.put(TAG_TOTHARGA, totharga);
					map.put(TAG_IDPRODUK, id_produk);
					map.put(TAG_GAMBAR, link_image);
					

					DaftarKeranjang.add(map);
				}
				final int json_i = json.getInt(TAG_SUBTOTAL);
				final int json_item = json.getInt(TAG_COUNT);
				final int json_berat = json.getInt(TAG_BERAT);

				// update UI dari Background Thread
				runOnUiThread(new Runnable() {
					public void run() {	
				

				// TextView Subtotal
				TextView total = (TextView) findViewById(R.id.subtotal);
				try {
					String subtotal = String.valueOf(json_i);
					String count = String.valueOf(json_item);
					String berat = String.valueOf(json_berat);
					total.setText("Subtotal=Rp."+subtotal+",-"+"| Jumlah=" +count+" Item ");
					txtBerat.setText(berat);

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					}
				 }
				});


				}else {
            	//Toast.makeText(Keranjang.this,"Item di Keranjang Kosong",Toast.LENGTH_SHORT).show();
				Intent i = new Intent(Keranjang.this, Order_Activity.class);
                startActivity(i);
				finish();
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

			runOnUiThread(new Runnable() {
				public void run() {

					SetListViewAdapter(DaftarKeranjang);


				}
			});
		}
		
	}

}
