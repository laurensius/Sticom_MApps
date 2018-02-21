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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import git.sticom.util.Server;


public class Order_Activity extends AppCompatActivity{	
private static String TAG = Order_Activity.class.getSimpleName();
	
	private Toolbar mToolbar;
	
	private ProgressDialog pDialog;

	JSONParser jParser = new JSONParser();

	ArrayList<HashMap<String, String>> DaftarProduk = new ArrayList<HashMap<String, String>>();

	private static String url_produk = Server.URL+"produk.php";


	public static final String TAG_ID = "id";
	public static final String TAG_PRODUK = "produk";
	public static final String TAG_HARGA = "harga";
	public static final String TAG_GAMBAR = "gambar";


	JSONArray string_json = null;

	ListView list;
	LazyAdapter adapter;
	SessionManager session;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
		
		session = new SessionManager(getApplicationContext());
        
        session.checkSession();
             

		DaftarProduk = new ArrayList<HashMap<String, String>>();

		new AmbilData().execute();

		list = (ListView) findViewById(R.id.listView1);
		
		 list.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
	                HashMap<String, String> map = DaftarProduk.get(position);
								
	                //Starting new intent
	                Intent in = new Intent(getApplicationContext(), DetailProduk.class);
	                in.putExtra(TAG_ID, map.get(TAG_ID));
	                in.putExtra(TAG_GAMBAR, map.get(TAG_GAMBAR));
	                startActivity(in); 
				}
			});
	        
	}
	
	public void SetListViewAdapter(ArrayList<HashMap<String, String>> produk) {
		adapter = new LazyAdapter(this, produk);
		list.setAdapter(adapter);
	}
	
	class AmbilData extends AsyncTask<String, String, String>{
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(Order_Activity.this);
			pDialog.setMessage("Mohon tunggu...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			List<NameValuePair> params = new ArrayList<NameValuePair>();

			JSONObject json = jParser.makeHttpRequest(url_produk, "GET",params);

			try {


					string_json = json.getJSONArray("produk");

					for (int i = 0; i < string_json.length(); i++) {
						JSONObject c = string_json.getJSONObject(i);

						String id_produk = c.getString(TAG_ID);
						String produk = c.getString(TAG_PRODUK);
						String harga = "Harga : " + c.getString(TAG_HARGA);
						String link_image = c.getString(TAG_GAMBAR);

						HashMap<String, String> map = new HashMap<String, String>();

						map.put(TAG_ID, id_produk);
						map.put(TAG_PRODUK, produk);
						map.put(TAG_HARGA, harga);
						map.put(TAG_GAMBAR, link_image);

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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.katalog, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item)
    {
         
        switch (item.getItemId())
        {
        case R.id.cart:
        	Intent i = new Intent(Order_Activity.this, Keranjang.class);
            startActivity(i);
			finish();;
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
		 Intent i = new Intent(Order_Activity.this, Order_Activity.class);
		 i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
		 finish();
		 System.exit(0);
		 }
		 })
		 .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
		 public void onClick(DialogInterface dialog, int id) {
		 dialog.cancel();
		 }
		 }).show();
		}

}
