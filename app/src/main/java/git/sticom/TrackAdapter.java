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

public class TrackAdapter extends BaseAdapter {
	
	private Activity activity;
	private ArrayList<HashMap<String, String>> data;
	private static LayoutInflater inflater = null;
	public ImageLoader imageLoader;
	
	public TrackAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
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
		vi = inflater.inflate(R.layout.list_track, null);
		
		TextView id_produk = (TextView) vi.findViewById(R.id.kode);
		TextView proses = (TextView) vi.findViewById(R.id.proses); 
		TextView waktu = (TextView) vi.findViewById(R.id.waktu);
		ImageView thumb_image = (ImageView) vi.findViewById(R.id.gambar);
		
		HashMap<String, String> daftar_produk = new HashMap<String, String>();
		daftar_produk = data.get(position);
		
		id_produk.setText(daftar_produk.get(Tracking.TAG_ID));
		proses.setText(daftar_produk.get(Tracking.TAG_PROSES));
		waktu.setText(daftar_produk.get(Tracking.TAG_WAKTU));
		return vi;
	}

}
