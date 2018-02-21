package git.sticom;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import git.sticom.adapter.PromoAdapter;
import git.sticom.app.AppController;
import git.sticom.data.PromoData;
import git.sticom.util.Server;

public class PromoActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    ListView list;
    SwipeRefreshLayout swipe;
    List<PromoData> itemList = new ArrayList<PromoData>();

    private static final String TAG = MainActivity.class.getSimpleName();

    private static String url_list 	 = Server.URL + "promo.php?offset=";

    private int offSet = 0;

    int no;

    PromoAdapter adapter;

    public static final String TAG_NO       = "no";
    public static final String TAG_ID       = "id";
    public static final String TAG_JUDUL    = "judul";

    Handler handler;
    Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promo);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        swipe = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        list = (ListView) findViewById(R.id.list_promo);
        itemList.clear();
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(PromoActivity.this, DetailPromo.class);
                intent.putExtra(TAG_ID, itemList.get(position).getPromo_id());
                startActivity(intent);
            }
        });

        adapter = new PromoAdapter(PromoActivity.this, itemList);
        list.setAdapter(adapter);

        swipe.setOnRefreshListener(this);

        swipe.post(new Runnable() {
                       @Override
                       public void run() {
                           swipe.setRefreshing(true);
                           itemList.clear();
                           adapter.notifyDataSetChanged();
                           callPromo(0);
                       }
                   }
        );

        list.setOnScrollListener(new AbsListView.OnScrollListener() {

            private int currentVisibleItemCount;
            private int currentScrollState;
            private int currentFirstVisibleItem;
            private int totalItem;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                this.currentScrollState = scrollState;
                this.isScrollCompleted();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                this.currentFirstVisibleItem = firstVisibleItem;
                this.currentVisibleItemCount = visibleItemCount;
                this.totalItem = totalItemCount;
            }

            private void isScrollCompleted() {
                if (totalItem - currentFirstVisibleItem == currentVisibleItemCount
                        && this.currentScrollState == SCROLL_STATE_IDLE) {

                    swipe.setRefreshing(true);
                    handler = new Handler();

                    runnable = new Runnable() {
                        public void run() {
                            callPromo(offSet);
                        }
                    };

                    handler.postDelayed(runnable, 3000);
                }
            }

        });
    }

    @Override
    public void onRefresh() {
        itemList.clear();
        adapter.notifyDataSetChanged();
        callPromo(0);
    }

    private void callPromo(int page) {
        swipe.setRefreshing(true);
        // Creating volley request obj
        JsonArrayRequest arrReq = new JsonArrayRequest(url_list + page,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());

                        if (response.length() > 0) {
                            // Parsing json
                            for (int i = 0; i < response.length(); i++) {
                                try {

                                    JSONObject obj = response.getJSONObject(i);
                                    PromoData data = new PromoData();

                                    no = obj.getInt(TAG_NO);

                                    data.setPromo_id(obj.getString(TAG_ID));
                                    data.setPromo_judul(obj.getString(TAG_JUDUL));

                                    //if (obj.getString(TAG_GAMBAR) != "") {
                                    //  data.setPasar_gambar(obj.getString(TAG_GAMBAR));
                                    //}

                                    // adding news to news array
                                    itemList.add(data);

                                    if (no > offSet)
                                        offSet = no;

                                    Log.d(TAG, "offSet " + offSet);

                                } catch (JSONException e) {
                                    Log.e(TAG, "JSON Parsing error: " + e.getMessage());
                                }

                                // notifying list adapter about data changes
                                // so that it renders the list view with updated data
                                adapter.notifyDataSetChanged();
                            }
                        }
                        swipe.setRefreshing(false);
                    }

                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                swipe.setRefreshing(false);
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(arrReq);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
