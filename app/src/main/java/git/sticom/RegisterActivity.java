package git.sticom;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import git.sticom.app.AppController;
import git.sticom.util.Server;

public class RegisterActivity extends AppCompatActivity {

    MaterialEditText edtUser, edtPass, edtNama;
    Button Register;

    int success;
    private ProgressDialog pDialog;
    private Context context;


    private static final String TAG = RegisterActivity.class.getSimpleName();

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    private static final String url_register = Server.URL + "registrasi.php";
    String tag_json_obj = "json_obj_req";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtUser = (MaterialEditText)findViewById(R.id.edtUsername);
        edtPass = (MaterialEditText)findViewById(R.id.edtPassword);
        edtNama = (MaterialEditText)findViewById(R.id.edtNama);
        Register = (Button)findViewById(R.id.btnRegister);

        context = RegisterActivity.this;
        pDialog = new ProgressDialog(context);
        
        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Registrasi();
            }
        });
    }

    private void Registrasi() {
        pDialog.setMessage("Membuat Akun...");
        pDialog.setCancelable(false);
        showDialog();

        final String User = edtUser.getText().toString();
        final String Pass = edtPass.getText().toString();
        final String Nama = edtNama.getText().toString();

        StringRequest strReq = new StringRequest(Request.Method.POST, url_register, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    // Cek error node pada json
                    if (success == 1) {
                        Log.d("Add Akun", jObj.toString());

                        hideDialog();
                        Toast.makeText(RegisterActivity.this, jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                        finish();

                    } else {
                        hideDialog();
                        Toast.makeText(RegisterActivity.this, jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(RegisterActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters ke post url
                Map<String, String> params = new HashMap<String, String>();
                // jika id kosong maka simpan, jika id ada nilainya maka update
                params.put("username", User);
                params.put("password", Pass);
                params.put("nama", Nama);
                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }


    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }


    @Override
    public void onBackPressed(){
        Intent i = new Intent(RegisterActivity.this,Beranda.class);
        startActivity(i);
        finish();
    }
}
