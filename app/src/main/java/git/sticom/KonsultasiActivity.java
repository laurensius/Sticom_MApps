package git.sticom;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

public class KonsultasiActivity extends AppCompatActivity {

    String myMail;

    MaterialEditText txtNama, txtEmail, txtKeluhan;
    Button btnSend;

    int success;
    private ProgressDialog pDialog;
    private Context context;


    private static final String TAG = KonsultasiActivity.class.getSimpleName();

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    private static final String url_send = Server.URL + "konsultasi.php";
    String tag_json_obj = "json_obj_req";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_konsultasi);
        context = KonsultasiActivity.this;

        txtNama = (MaterialEditText)findViewById(R.id.edtNama);
        txtEmail = (MaterialEditText)findViewById(R.id.edtEmail);
        txtKeluhan = (MaterialEditText)findViewById(R.id.edtKeluhan);

        btnSend = (Button)findViewById(R.id.btnSend);

        myMail = "rosidinade4@gmail.com";

        pDialog = new ProgressDialog(context);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Send();
                new SendMail().execute("");
                new SendKonsul().execute("");
            }
        });

    }

    private class SendKonsul extends AsyncTask<String, Integer, Void>{

        String mailTo = txtEmail.getText().toString();
        String nama = txtNama.getText().toString();
        String pertanyaan = txtKeluhan.getText().toString();

        @Override
        protected Void doInBackground(String... params) {
            Mail m = new Mail("rosidinade4@gmail.com", "realita28");

            String[] toArr = {myMail};
            m.setTo(toArr);
            m.setFrom("rosidinade4@gmail.com");
            m.setSubject("Pertanyaan Konsultasi");
            m.setBody("Dari Saudara "+nama+", \nEmail : "+mailTo+"\n\nPertanyaan :\n"+pertanyaan);

            try {
                if(m.send()) {
                    Toast.makeText(context, "Email was sent successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Email was not sen", Toast.LENGTH_SHORT).show();
                }
            } catch(Exception e) {
                Log.e("Mail", "Could not send email", e);
            }
            return null;
        }
    }

    private class SendMail extends AsyncTask<String, Integer, Void>{

        String mailTo = txtEmail.getText().toString();
        String nama = txtNama.getText().toString();

        @Override
        protected Void doInBackground(String... params) {
            Mail m = new Mail("rosidinade4@gmail.com", "realita28");

            String[] toArr = {mailTo};
            m.setTo(toArr);
            m.setFrom("rosidinade4@gmail.com");
            m.setSubject("Konfirmasi Konsultasi");
            m.setBody("Hai Saudara "+nama+", \nTerima Kasih telah mengajukan pertanyaan kepada kami mengenai permasalahan komputer yang anda alami. " +
                    "Agar pertanyaan anda bisa diproses harap balas Email ini dengan kata 'TANYA STICOM'.\n" +
                    "Setelah kami menerima email balasan dari Anda, Kami akan mencari solusi yang cepat dan tepat untuk masalah yang anda alami.\n\n" +
                    "Salam STICOM");

            try {
                if(m.send()) {
                    Toast.makeText(context, "Email was sent successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Email was not sen", Toast.LENGTH_SHORT).show();
                }
            } catch(Exception e) {
                Log.e("Mail", "Could not send email", e);
            }
            return null;
        }
    }

    private void Send() {
        pDialog.setMessage("Mengirim...");
        pDialog.setCancelable(false);
        showDialog();

        final String Nama = txtNama.getText().toString();
        final String Email = txtEmail.getText().toString();
        final String Keluhan = txtKeluhan.getText().toString();

        StringRequest strReq = new StringRequest(Request.Method.POST, url_send, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    // Cek error node pada json
                    if (success == 1) {
                        Log.d("Add Keluhan", jObj.toString());

                        hideDialog();
                        Toast.makeText(KonsultasiActivity.this, jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                        KonsultasiActivity.this.finish();

                    } else {
                        hideDialog();
                        Toast.makeText(KonsultasiActivity.this, jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
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
                Toast.makeText(KonsultasiActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters ke post url
                Map<String, String> params = new HashMap<String, String>();
                // jika id kosong maka simpan, jika id ada nilainya maka update
                params.put("nama", Nama);
                params.put("email", Email);
                params.put("keluhan", Keluhan);
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
}
