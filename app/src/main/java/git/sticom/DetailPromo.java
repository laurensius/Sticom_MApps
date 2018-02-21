package git.sticom;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import git.sticom.app.AppController;
import git.sticom.util.Server;

public class DetailPromo extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    TextView txtId, txtJudul, txtIsi;
    SwipeRefreshLayout swipe;

    private static final String url_detail 	= Server.URL + "detail_promo.php";

    private static final String TAG = DetailPromo.class.getSimpleName();

    int success;

    private ProgressDialog pDialog;
    private Context context;

    public static final String TAG_ID         = "id";
    public static final String TAG_JUDUL       = "judul";
    public static final String TAG_ISI     = "isi";

    String id = "";

    String tag_json_obj = "json_obj_req";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_promo);

        context = DetailPromo.this;

        txtId = (TextView)findViewById(R.id.txtId);
        txtJudul = (TextView)findViewById(R.id.txtJudulPromo);
        txtIsi = (TextView)findViewById(R.id.txtIsiPromo);

        swipe   = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        pDialog = new ProgressDialog(context);

        Bundle b = getIntent().getExtras();
        id = (String) b.getCharSequence("id");

        swipe.setOnRefreshListener(this);
        swipe.post(new Runnable() {
                       @Override
                       public void run() {
                           if (!id.isEmpty()) {
                               callDetail(id);
                           }
                       }
                   }
        );

    }

    @Override
    public void onRefresh() {
        callDetail(id);
    }

    private void callDetail(final String id) {
        swipe.setRefreshing(true);
        StringRequest strReq = new StringRequest(Request.Method.POST, url_detail, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response " + response.toString());
                swipe.setRefreshing(false);

                try {
                    JSONObject obj = new JSONObject(response);

                    String Id           = obj.getString(TAG_ID);
                    String Judul        = obj.getString(TAG_JUDUL);
                    String Isi          = obj.getString(TAG_ISI);

                    txtId.setText(Id);
                    txtJudul.setText(Judul);
                    txtIsi.setText(Isi);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Detail Error: " + error.getMessage());
                Toast.makeText(DetailPromo.this,
                        error.getMessage(), Toast.LENGTH_LONG).show();
                swipe.setRefreshing(false);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to post url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", id);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
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
