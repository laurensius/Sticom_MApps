package git.sticom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.impl.io.IdentityOutputStream;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

import git.sticom.util.Server;


public class Tracking extends AppCompatActivity{
private static String TAG = Tracking.class.getSimpleName();
	
	private Toolbar mToolbar;
	
	private ProgressDialog pDialog;
	ArrayList<HashMap<String, String>> DaftarProduk = new ArrayList<HashMap<String, String>>();
	
	public static final String TAG_ID = "id";
	public static final String TAG_WAKTU = "waktu";
	public static final String TAG_PROSES = "proses";
	private static final String url_tracking = Server.URL+"tracking.php";
	
	JSONArray string_json = null;
	JSONParser jsonParser = new JSONParser();

	ListView list;
	TrackAdapter adapter;
	SessionManager session;
	
	@Override
		protected void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
			setContentView(R.layout.tracking);
		
	        getSupportActionBar().setDisplayShowHomeEnabled(true);
	        
	        
	        
			DaftarProduk = new ArrayList<HashMap<String, String>>();
			list = (ListView) findViewById(R.id.listView1);
			
			new DetailTrack().execute();
	}
	
	public void SetListViewAdapter(ArrayList<HashMap<String, String>> produk) {
		adapter = new TrackAdapter(this, produk);
		list.setAdapter(adapter);
	}
	
	class DetailTrack extends AsyncTask<String, String, String>{
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(Tracking.this);
			pDialog.setMessage("Mencari History Proses ... !");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			Bundle b = getIntent().getExtras();
			final String id = (String) b.getCharSequence("id");
			List<NameValuePair> params1 = new ArrayList<NameValuePair>();
			params1.add(new BasicNameValuePair("idorder",id));
			
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
		protected void onPostExecute(String file_url) {
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
