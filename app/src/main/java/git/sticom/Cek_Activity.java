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
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import git.sticom.util.Server;

public class Cek_Activity extends AppCompatActivity{
private static String TAG = Cek_Activity.class.getSimpleName();
	
	private Toolbar mToolbar;
	
	JSONArray string_json = null;
	SessionManager session;
	String idorder;
	private ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();
	ImageButton cari;
	TextView nama, alamat, telpon, kelurahan, total, status, btnDetail;
	EditText id_order;
	ListView list;
	TrackAdapter adapter;
	
	ArrayList<HashMap<String, String>> DaftarProduk = new ArrayList<HashMap<String, String>>();
	
	public static final String TAG_ID = "id";
	public static final String TAG_WAKTU = "waktu";
	public static final String TAG_PROSES = "proses";
	private static final String TAG_SUKSES = "sukses";
	private static final String url_cek_order = Server.URL+"cekorder.php";
	private static final String url_tracking = Server.URL+"tracking.php";
	public static final String DATA_ID = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cek_order);

	        getSupportActionBar().setDisplayShowHomeEnabled(true);
	        
	        cari = (ImageButton) findViewById(R.id.bSearch);
	        id_order = (EditText) findViewById(R.id.id_order1);
	        list = (ListView) findViewById(R.id.listView1);
	        btnDetail = (TextView) findViewById(R.id.detail_track);
	        
	        
	        cari.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
					new CekOrder().execute();
					
				}
			});
	        
	        btnDetail.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String id = id_order.getText().toString();
					Intent in = new Intent(getApplicationContext(),Tracking.class);
					Bundle b= new Bundle();
					b.putString("id", id);
					in.putExtras(b);
					startActivity(in);
				}
			});
	}
	
	public void SetListViewAdapter(ArrayList<HashMap<String, String>> produk) {
		adapter = new TrackAdapter(this, produk);
		list.setAdapter(adapter);
	}
	
	class CekOrder extends AsyncTask<String, String, String>{
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(Cek_Activity.this);
			pDialog.setMessage("Mencari Data ... !");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			try{
				String idorder = id_order.getText().toString();
				List<NameValuePair> params1 = new ArrayList<NameValuePair>();
				params1.add(new BasicNameValuePair("idorder",idorder));
				
				JSONObject json = jsonParser.makeHttpRequest(
						url_cek_order, "GET", params1);
				string_json = json.getJSONArray("detail");
				
				runOnUiThread(new Runnable() {	
					public void run() {
						nama = (TextView) findViewById(R.id.nama);
				        alamat = (TextView) findViewById(R.id.alamat);
				        telpon = (TextView) findViewById(R.id.telepon);
				        kelurahan = (TextView) findViewById(R.id.kelurahan);
				        status = (TextView) findViewById(R.id.status);

				try {
 
					// ambil objek member pertama dari JSON Array
					JSONObject ar = string_json.getJSONObject(0);
					String nama1 = ar.getString("nama");
					String alamat1 = ar.getString("alamat");
					String telpon1 = ar.getString("telpon");
					String kelurahan1 = ar.getString("kelurahan");
					String status1 = ar.getString("status");
					
			
			   nama.setText(nama1);
		       alamat.setText(alamat1);
		       telpon.setText(telpon1);
		       kelurahan.setText(kelurahan1);
		       status.setText(status1);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					}
			}
		});
			}catch (JSONException e) {
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
	
	class DetailTrack extends AsyncTask<String, String, String>{
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(Cek_Activity.this);
			pDialog.setMessage("Mencari History Proses ... !");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			String idorder = id_order.getText().toString();
			List<NameValuePair> params1 = new ArrayList<NameValuePair>();
			params1.add(new BasicNameValuePair("idorder",idorder));
			
			JSONObject json = jsonParser.makeHttpRequest(url_tracking, "GET",params1);
			
			try {


				string_json = json.getJSONArray("produk");

				for (int i = 0; i < string_json.length(); i++) {
					JSONObject c = string_json.getJSONObject(i);

					String id_produk = c.getString(TAG_ID);
					String proses = c.getString(TAG_PROSES);
					String waktu = c.getString(TAG_WAKTU);

					HashMap<String, String> map = new HashMap<String, String>();

					map.put(TAG_ID, id_produk);
					map.put(TAG_PROSES, proses);
					map.put(TAG_WAKTU, waktu);

					DaftarProduk.add(map);
				}

		} catch (JSONException e) {
			e.printStackTrace();
		}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			pDialog.dismiss();

			runOnUiThread(new Runnable() {
				public void run() {

					SetListViewAdapter(DaftarProduk);
				}
			});
		}
		}
		
	}


