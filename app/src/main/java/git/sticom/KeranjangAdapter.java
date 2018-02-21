package git.sticom;

import java.util.ArrayList;
import java.util.HashMap;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class KeranjangAdapter extends BaseAdapter{
	
	private Activity activity;
	private ArrayList<HashMap<String, String>> data;
	private static LayoutInflater inflater = null;
	public ImageLoader imageLoader;
	
	public KeranjangAdapter(Activity a, ArrayList<HashMap<String, String>> d){
		activity = a;
		data = d;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = new ImageLoader(activity.getApplicationContext());
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View vi = convertView;
		if (convertView == null)
		vi = inflater.inflate(R.layout.katalog_list_row, null);

		TextView id_produk = (TextView) vi.findViewById(R.id.id_produk);
		TextView produk = (TextView) vi.findViewById(R.id.produk); 
		TextView qty = (TextView) vi.findViewById(R.id.qty);
		TextView totharga = (TextView) vi.findViewById(R.id.totharga);
		ImageView thumb_image = (ImageView) vi.findViewById(R.id.gambar); 
		
		HashMap<String, String> daftar_keranjang = new HashMap<String, String>();
		daftar_keranjang = data.get(position);
	
		id_produk.setText(daftar_keranjang.get(Keranjang.TAG_IDPRODUK));
		produk.setText(daftar_keranjang.get(Keranjang.TAG_PRODUK));
		qty.setText(daftar_keranjang.get(Keranjang.TAG_JUMLAH));
		totharga.setText(daftar_keranjang.get(Keranjang.TAG_TOTHARGA));
		imageLoader.DisplayImage(daftar_keranjang.get(Order_Activity.TAG_GAMBAR),thumb_image);
		return vi;
	}

}
