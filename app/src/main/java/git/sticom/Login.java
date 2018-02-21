package git.sticom;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import git.sticom.app.AppController;
import git.sticom.app.Config;
import git.sticom.util.NotificationUtils;
import git.sticom.util.Server;

/**
 * Created by Ade on 06/02/2018.
 */

public class Login extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    SessionManager sessionManager;
    ConnectivityManager conMgr;
    private CoordinatorLayout coordinatorLayout;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    int success;

    public static final String KEY_EMAIL = "email";
    public static final String TAG_PASSWORD = "password";
    public static final String TAG_NAMA = "nama";
    public static final String TAG_ID = "id";
    private static final String TAG_IDKUS = "idKus";
    public static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    private static String url_login 	 = Server.URL + "login.php";

    String Session;

    private EditText txtUsername;
    private EditText txtPassword;
    private TextView txtRegister;
    private Context context;
    private Button btnLogin;
    private ProgressDialog pDialog;
    String tag_json_obj = "json_obj_req";

    public Login() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = Login.this;
        Session = UUID.randomUUID().toString().replaceAll("-", "");

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorLayout);
        //Cek Koneksi Internet
        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            if (conMgr.getActiveNetworkInfo() != null
                    && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()) {
            } else {
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "Tidak Ada Koneksi Internet", Snackbar.LENGTH_LONG)
                        .setAction("Setting", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent MobileData = new Intent(
                                        android.provider.Settings.ACTION_DATA_ROAMING_SETTINGS);
                                startActivity(MobileData);
                            }
                        });

                snackbar.show();
            }

        }

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");

                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();
                }
            }
        };

        sessionManager = new SessionManager(getApplicationContext());
        //Initializing views
        pDialog = new ProgressDialog(context);
        txtUsername = (EditText) findViewById(R.id.edtUsername);
        txtPassword = (EditText) findViewById(R.id.edtPassword);
        txtRegister = (TextView) findViewById(R.id.txtRegister);

        btnLogin    = (Button) findViewById(R.id.button_login);

        //Adding click listener
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = txtUsername.getText().toString();
                String password = txtPassword.getText().toString();
                // mengecek kolom yang kosong
                if (username.trim().length() > 0 && password.trim().length() > 0) {
                    if (conMgr.getActiveNetworkInfo() != null
                            && conMgr.getActiveNetworkInfo().isAvailable()
                            && conMgr.getActiveNetworkInfo().isConnected()) {
                        login(username, password);
                    } else {
                        Snackbar snackbar = Snackbar
                                .make(coordinatorLayout, "Tidak Ada Koneksi Internet", Snackbar.LENGTH_LONG)
                                .setAction("Setting", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent MobileData = new Intent(
                                                android.provider.Settings.ACTION_DATA_ROAMING_SETTINGS);
                                        startActivity(MobileData);
                                    }
                                });

                        snackbar.show();
                    }
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext() ,"Kolom tidak boleh kosong", Toast.LENGTH_LONG).show();
                }
            }
        });

        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Register = new Intent(Login.this,RegisterActivity.class);
                startActivity(Register);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    public void onBackPressed(){
        Intent i = new Intent(Login.this,Beranda.class);
        startActivity(i);
        finish();
    }

    private void login(final String username, final String password) {
        pDialog.setMessage("Mohon Tunggu...");
        pDialog.setCancelable(false);
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST, url_login, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Login Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    // Check for error node in json
                    if (success == 1) {
                        String id = jObj.getString(TAG_ID);
                        String nama = jObj.getString(TAG_NAMA);

                        Log.e("Successfully Login!", jObj.toString());

                        Toast.makeText(getApplicationContext(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                        // menyimpan login ke session
                        sessionManager.createLoginSession(Session, id, nama);

                        Intent intent = new Intent(Login.this, IndexActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(getApplicationContext(),
                                jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();

                hideDialog();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("password", password);

                return params;
            }

        };

        // Adding request to request queue
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
