package git.sticom.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import git.sticom.R;
import git.sticom.data.PromoData;

/**
 * Created by Ade on 08/02/2018.
 */

public class PromoAdapter extends BaseAdapter{
    private Activity activity;
    private LayoutInflater inflater;
    private List<PromoData> items;

    public PromoAdapter(Activity activity, List<PromoData> items) {
        this.activity = activity;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int location) {
        return items.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_promo, null);

        TextView Id = (TextView) convertView.findViewById(R.id.txtIdPromo);
        TextView Judul = (TextView) convertView.findViewById(R.id.txtJudulPromo);

        PromoData keluhan = items.get(position);

        Id.setText(keluhan.getPromo_id());
        Judul.setText(keluhan.getPromo_judul());

        return convertView;
    }
}
