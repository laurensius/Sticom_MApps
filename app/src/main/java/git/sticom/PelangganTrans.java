package git.sticom;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import git.sticom.util.Server;

public class PelangganTrans extends AppCompatActivity implements OnItemSelectedListener{
private static String TAG = PelangganTrans.class.getSimpleName();
	
	private Toolbar mToolbar;
	
	private EditText nama;
	private EditText alamat;
	private EditText telpon;
	//private TextView textSpin1;
	//private TextView textSpin2;
	private Button  btnSimpan;
	private Spinner spinnerPropinsi;
	private Spinner spinnerKabupaten;
	private ArrayList<Kecamatan> kecList;
	private ArrayList<Kelurahan> kelList;
	ProgressDialog pDialog;
	ProgressDialog pDialog2;
	ProgressDialog pDialog3;
	ProgressDialog pDialog4;
	TextView txtKecamatan;
	TextView txtKelurahan;
	JSONParser jParser = new JSONParser();
	private static final String TAG_SUKSES = "sukses";
	private static final String TAG_IDORDER = "idorder";
	private static final String TAG_NAMA = "nama";
	private static final String TAG_ALAMAT = "alamat";
	private static final String TAG_TELPON = "telpon";
	private static final String TAG_SUBTOTAL = "subtotal";
	private static final String TAG_ONGKIR = "ongkir";
	private static final String TAG_GRANDTOTAL = "grandtotal";
	JSONArray string_json_kecamatan,string_json_kel,string_cetak  = null;
	SessionManager session;
	private String url_kecamatan = Server.URL+"kecamatan.php";
	private String url_kelurahan = Server.URL+"kelurahan.php";
	private String url_simpan_trans = Server.URL+"simpantransaksi.php";
	private String url_cetak_trans = Server.URL+"cetaktransaksi.php";
	
	Calendar c1 = Calendar.getInstance();
    SimpleDateFormat sdf1 = new SimpleDateFormat("d/M/yyyy h:m:s a");
       //SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm:ss");
       String strdate1 = sdf1.format(c1.getTime());
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pelanggan);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
		
		session = new SessionManager(getApplicationContext());
		nama = (EditText)findViewById(R.id.nama);
		alamat = (EditText)findViewById(R.id.alamat);
		telpon = (EditText)findViewById(R.id.telpon);
		spinnerPropinsi = (Spinner) findViewById(R.id.spinProp);
		spinnerKabupaten = (Spinner) findViewById(R.id.spinKab);
		txtKecamatan = (TextView) findViewById(R.id.TextSpin1);
		txtKelurahan = (TextView) findViewById(R.id.TextSpin2);

		kecList = new ArrayList<Kecamatan>();
		kelList = new ArrayList<Kelurahan>();

		// spinner item select listener
		spinnerPropinsi.setOnItemSelectedListener(this);
		spinnerKabupaten.setOnItemSelectedListener(this);
		
		btnSimpan = (Button)findViewById(R.id.proses);
		
		
		
		new GetKec().execute();
		
		// Add new simpan click event
				btnSimpan.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						new SimpanTransaksi().execute();
						new CetakTransaksi().execute();
					}
				});
	
	}
	
	@Override
	public void onBackPressed() {
		Intent i = new Intent(PelangganTrans.this, SplashActivity.class);
        startActivity(i);
		finish();
	return;
	}
	
	/**
	 * Adding spinner data
	 * */
	private void populatePropinsi() {

		List<String> labelprop = new ArrayList<String>();

		for (int i = 0; i < kecList.size(); i++) {
			labelprop.add(kecList.get(i).getName());
		}

		// Creating adapter for spinner
		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, labelprop);

		// Drop down layout style - list view with radio button
		spinnerAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// attaching data adapter to spinner
		spinnerPropinsi.setAdapter(spinnerAdapter);
	}
	
	/**
	 * Adding spinner data
	 * */
	private void populateKabupaten() {
		List<String> labelkab = new ArrayList<String>();

		for (int i = 0; i < kelList.size(); i++) {
			labelkab.add(kelList.get(i).getName());
		}

		// Creating adapter for spinner
		ArrayAdapter<String> spinnerAdapter2 = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, labelkab);

		// Drop down layout style - list view with radio button
		spinnerAdapter2
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// attaching data adapter to spinner
		spinnerKabupaten.setAdapter(spinnerAdapter2);
	}
	
	private class GetKec extends AsyncTask<Void, Void, Void>{
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(PelangganTrans.this);
			pDialog.setMessage("Mengambil Data Kecamatan ..");
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			List<NameValuePair> params = new ArrayList<NameValuePair>();

			JSONObject json = jParser.makeHttpRequest(url_kecamatan, "GET",params);

			if (json != null) {
				try {
					string_json_kecamatan = json.getJSONArray("kecamatan");
			
					for (int i = 0; i < string_json_kecamatan.length(); i++) {
						JSONObject c = string_json_kecamatan.getJSONObject(i);
							Kecamatan kec = new Kecamatan(c.getInt("id"),
									c.getString("kecamatan"));
							kecList.add(kec);
						}


				} catch (JSONException e) {
					e.printStackTrace();
				}

			} else {
				Log.e("JSON Data", "Didn't receive any data from server!");
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (pDialog.isShowing())
				pDialog.dismiss();
			populatePropinsi();
		}
		
	}
	
	private class GetKel extends AsyncTask<Void, Void, Void>{
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog2 = new ProgressDialog(PelangganTrans.this);
			pDialog2.setMessage("Mengambil Data Kelurahan ..");
			pDialog2.setCancelable(false);
			pDialog2.show();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			String id_kecamatan = txtKecamatan.getText().toString();
			List<NameValuePair> params1 = new ArrayList<NameValuePair>();
			params1.add(new BasicNameValuePair("id_kecamatan",id_kecamatan));

			JSONObject json = jParser.makeHttpRequest(url_kelurahan, "GET",params1);

			if (json != null) {
				try {
					string_json_kel = json.getJSONArray("kelurahan");
			
					for (int i = 0; i < string_json_kel.length(); i++) {
						JSONObject c = string_json_kel.getJSONObject(i);
							Kelurahan kel = new Kelurahan(c.getInt("id"),
									c.getString("kelurahan"));
							kelList.add(kel);
						}


				} catch (JSONException e) {
					e.printStackTrace();
				}
				
			} else {
				Log.e("JSON Data", "Didn't receive any data from server!");
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (pDialog2.isShowing())
				pDialog2.dismiss();
			populateKabupaten();
		}
		
	}
	
	private class SimpanTransaksi extends AsyncTask<Void, Void, Void>{
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog3 = new ProgressDialog(PelangganTrans.this);
			pDialog3.setMessage("Simpan Transaksi ..");
			pDialog3.setCancelable(false);
			pDialog3.show();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			session.checkSession();  
			HashMap<String, String> user = session.getUserDetails();
			final String idsesi = user.get(SessionManager.KEY_NAME);
			 int sukses;
			 Intent in = getIntent(); 
				
			// ambil berat item dari intent sebelumnya
			String berat = in.getStringExtra("beratbuku");
			
			String namapel = nama.getText().toString();
			String alamatpel = alamat.getText().toString();
			String telponpel = telpon.getText().toString();
			String id_kota = txtKelurahan.getText().toString();
			
			try {
				
			List<NameValuePair> params1 = new ArrayList<NameValuePair>();
			params1.add(new BasicNameValuePair("namapel",namapel));
			params1.add(new BasicNameValuePair("alamatpel",alamatpel));
			params1.add(new BasicNameValuePair("telponpel",telponpel));
			params1.add(new BasicNameValuePair("id_kota",id_kota));
			params1.add(new BasicNameValuePair("idsesi",idsesi));
			params1.add(new BasicNameValuePair("berat",berat));

			JSONObject json2 = jParser.makeHttpRequest(url_simpan_trans,"POST", params1);
			Log.d("Respon ", json2.toString());
			// cek json sukses tag (apakah 1 atau 0)
			
				sukses = json2.getInt(TAG_SUKSES);
				
				if (sukses == 1) {
					// sukses menyimpan transaksi				
					Log.d("Transaksi tersimpan!", json2.toString()); 
					//Intent i = new Intent(PelangganTrans.this, SplashActivity.class);
	                //startActivity(i);
					//finish();
				} else {
					// gagal update data
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			pDialog3.dismiss();
		}
		
	}
	
	private class CetakTransaksi extends AsyncTask<Void, Void, Void>{
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog4 = new ProgressDialog(PelangganTrans.this);
			pDialog4.setMessage("Mencetak PDF ..");
			pDialog4.setCancelable(false);
			pDialog4.show();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			session.checkSession();  
			HashMap<String, String> user = session.getUserDetails();
			final String idsesi = user.get(SessionManager.KEY_NAME);

				List<NameValuePair> params1 = new ArrayList<NameValuePair>();
				params1.add(new BasicNameValuePair("idsesi",idsesi));
				

				final JSONObject json3 = jParser.makeHttpRequest(url_cetak_trans,"POST", params1);
				Log.d("Respon ", json3.toString());
				
				if (json3 != null) {
				runOnUiThread(new Runnable() { 
				public void run() {
					try {
						String idorder = json3.getString(TAG_IDORDER);
						String namapel = json3.getString(TAG_NAMA);
						String alamatpel = json3.getString(TAG_ALAMAT);
						String telponpel = json3.getString(TAG_TELPON);
						String subtotal = json3.getString(TAG_SUBTOTAL);
						String ongkir = json3.getString(TAG_ONGKIR);
						String grandtotal = json3.getString(TAG_GRANDTOTAL);
						
						try {
						Document doc = new Document();
						final String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/sticom";
						File dir = new File(path);
						if(!dir.exists())
							dir.mkdirs();

						Log.d("PDFCreator", "PDF Path: " + path);
						
						 String fpath =  "Order#"+idorder + ".pdf";
				         File file = new File(dir, fpath);
				         
				       //File file = new File(dir, "order.pdf");
							FileOutputStream fOut = new FileOutputStream(file);

							PdfWriter.getInstance(doc, fOut);

							//open the document
							doc.open();

							
							/* Create Paragraph and Set Font */
							Paragraph p1 = new Paragraph("BUKTI TRANSAKSI STICOM ONLINE");
							/* Create Set Font and its Size */
							Font paraFont= new Font(Font.HELVETICA);
							paraFont.setSize(17);
							p1.setAlignment(Paragraph.ALIGN_CENTER);
							p1.setFont(paraFont);

							//add paragraph to document    
							doc.add(p1);
							
							/* Create Paragraph and Set Font */
							Paragraph p2 = new Paragraph("Komputer | Service | Accessories");
							/* Create Set Font and its Size */
							Font paraFont2= new Font(Font.HELVETICA);
							paraFont2.setSize(15);
							p2.setAlignment(Paragraph.ALIGN_CENTER);
							p2.setFont(paraFont2);

							//add paragraph to document    
							doc.add(p2);
							
							/* Create Paragraph and Set Font */
							Paragraph p4 = new Paragraph(" Nomor Order =  "+idorder);
							/* Create Set Font and its Size */
							Font paraFont4= new Font(Font.HELVETICA);
							paraFont4.setSize(14);
							p4.setAlignment(Paragraph.ALIGN_CENTER);
							p4.setFont(paraFont4);

							//add paragraph to document    
							doc.add(p4);
							
							/* Create Paragraph and Set Font */
							Paragraph p5 = new Paragraph(" Nama Pemesan =    "+namapel);
							/* Create Set Font and its Size */
							Font paraFont5= new Font(Font.HELVETICA);
							paraFont5.setSize(14);
							p5.setAlignment(Paragraph.ALIGN_CENTER);
							p5.setFont(paraFont5);

							//add paragraph to document    
							doc.add(p5);
							
							/* Create Paragraph and Set Font */
							Paragraph p6 = new Paragraph(" Alamat =    "+alamatpel);
							/* Create Set Font and its Size */
							Font paraFont6= new Font(Font.HELVETICA);
							paraFont6.setSize(14);
							p6.setAlignment(Paragraph.ALIGN_CENTER);
							p6.setFont(paraFont6);

							//add paragraph to document    
							doc.add(p6);
							
													
							/* Create Paragraph and Set Font */
							Paragraph p7 = new Paragraph(" No HP =    "+telponpel);
							/* Create Set Font and its Size */
							Font paraFont7= new Font(Font.HELVETICA);
							paraFont7.setSize(14);
							p7.setAlignment(Paragraph.ALIGN_CENTER);
							p7.setFont(paraFont7);

							//add paragraph to document    
							doc.add(p7);
							
							/* Create Paragraph and Set Font */
							Paragraph p8 = new Paragraph(" ");
							p8.setAlignment(Paragraph.ALIGN_CENTER);
							doc.add(p8);
							
							float[] columnWidths = {0.5f, 5f, 1.2f, 1.5f, 2f};
			                 PdfPTable table = new PdfPTable(columnWidths);
			                 PdfPCell c1 = new PdfPCell(new Phrase("No"));
			                 c1.setHorizontalAlignment(Element.ALIGN_LEFT);
			                 table.addCell(c1);

			                 c1 = new PdfPCell(new Phrase("Nama Produk"));
			                 c1.setHorizontalAlignment(Element.ALIGN_CENTER);
			                 table.addCell(c1);

			                 c1 = new PdfPCell(new Phrase("Harga"));
			                 c1.setHorizontalAlignment(Element.ALIGN_CENTER);
			                 table.addCell(c1);
			                 
			                 c1 = new PdfPCell(new Phrase("Qty"));
			                 c1.setHorizontalAlignment(Element.ALIGN_CENTER);
			                 table.addCell(c1);
			                 				                 
			                 c1 = new PdfPCell(new Phrase("Total"));
			                 c1.setHorizontalAlignment(Element.ALIGN_CENTER);
			                 table.addCell(c1);
						
						string_cetak = json3.getJSONArray("trans");	
						int a=1;
						for (int i = 0; i < string_cetak.length(); i++) {
							JSONObject c = string_cetak.getJSONObject(i);
								String namabuku = c.getString("namabuku");
								String hargabuku = c.getString("harga");
								String quantity = c.getString("qty");
								String totalharga = c.getString("totharga");
								String count = String.valueOf(a);
							
							 table.addCell(count);
			                 table.addCell(namabuku);
			                 table.addCell(hargabuku);
			                 table.addCell(quantity);
			                 table.addCell(totalharga);
			                 a++;
							}
						
						  table.addCell("");
			              table.addCell("");
			              table.addCell("");
			              table.addCell("Subtotal");
			              table.addCell(subtotal);
			              
			              table.addCell("");
			              table.addCell("");
			              table.addCell("");
			              table.addCell("Total Ongkir");
			              table.addCell(ongkir);
			              
			              table.addCell("");
			              table.addCell("");
			              table.addCell("");
			              table.addCell("Grand Total");
			              table.addCell(grandtotal);
			              
			              doc.add(table); 
			              
			              /* Create Paragraph and Set Font */
			  			Paragraph p9 = new Paragraph("---------------------------------------------------------------------------------------------------------");
			  			/* Create Set Font and its Size */
			  			paraFont2.setSize(15);
			  			p9.setAlignment(Paragraph.ALIGN_CENTER);

			  			//add paragraph to document    
			  			doc.add(p9);
			  			
			  			
			              /* Create Paragraph and Set Font */
			  			Paragraph p10 = new Paragraph("*Ini adalah bukti pemesanan Anda");
			  			/* Create Set Font and its Size */
			  			paraFont2.setSize(12);
			  			p10.setAlignment(Paragraph.ALIGN_RIGHT);

			  			//add paragraph to document    
			  			doc.add(p10);
			  			
			  			Paragraph p11 = new Paragraph("*Silahkan transfer ke no.rek. xxxxx Bank INI atau bayar di tempat.");
			  			/* Create Set Font and its Size */
			  			paraFont2.setSize(12);
			  			p11.setAlignment(Paragraph.ALIGN_RIGHT);

			  			//add paragraph to document    
			  			doc.add(p11);
			  			
			              /* Create Paragraph and Set Font */
			  			Paragraph p12 = new Paragraph("*Silakan Hub. 089686720493, bila ada pengaduan.");
			  			/* Create Set Font and its Size */
			  			paraFont2.setSize(12);
			  			p12.setAlignment(Paragraph.ALIGN_RIGHT);

			  			//add paragraph to document    
			  			doc.add(p12);
			  			
			            doc.close();

			            String cpath = "Order#"+ idorder + ".pdf";
				        
						//Buka file pdf otomatis dengan pdf reader
				        File files= new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/sticom/" + cpath);

				        if (files.exists())
						 {
						Uri paths = Uri.fromFile(files);
						 Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setDataAndType(paths, "application/pdf");
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						try {

						  startActivity(intent);
						  finish();
						  session.destroySession();
						  Toast.makeText(getApplicationContext(), "Bukti Pemesanan tersimpan pada SDCARD Anda dalam folder dedi laundry...", Toast.LENGTH_LONG).show();
						  			
						}
						catch (ActivityNotFoundException e) {
						finish();
						session.destroySession();
						Toast.makeText(PelangganTrans.this, "Tidak Ada Aplikasi untuk membuka File PDF", Toast.LENGTH_SHORT).show();
						Toast.makeText(getApplicationContext(), "Bukti Pemesanan tersimpan pada SDCARD Anda dalam folder dedi laundry...", Toast.LENGTH_LONG).show();
						}
						}


						} catch (DocumentException de) {
							Log.e("PDFCreator", "DocumentException:" + de);
						} catch (IOException e) {
							Log.e("PDFCreator", "ioException:" + e);
						} 

					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				});
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			pDialog4.dismiss();
		}
		
	}
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		// TODO Auto-generated method stub
		switch(parent.getId()){
		
	    case R.id.spinProp: 
      	int idp=kecList.get(pos).getId();
      	String idp_text=String.valueOf(idp);
      	txtKecamatan.setText(idp_text);
      	kelList.clear();
		new GetKel().execute();

        break;
        
	    case R.id.spinKab:
	    int idk=kelList.get(pos).getId();
	   String idk_text=String.valueOf(idk);
	   txtKelurahan.setText(idk_text);
      }
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		Log.i("Message", "Nothing is selected");
	}

}
