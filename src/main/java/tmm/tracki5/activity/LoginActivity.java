package tmm.tracki5.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import tmm.tracki5.R;
import tmm.tracki5.AppController;
import tmm.tracki5.utils.NetworkConnection;
import tmm.tracki5.apiController.ApiUrls;
import tmm.tracki5.model.TrackAccess;

public class LoginActivity extends AppCompatActivity {

    private EditText inputPhone, inputPassword;
    private TextView forgetPassword, errorUser, errorPassword;
    private Button btnSignUp;

    NetworkConnection net;
    ApiUrls ApiUrl;
    private TrackAccess track;

    private String urlJsonObj = ApiUrl.LOGIN;
    private String urlJsonObjAuto = ApiUrl.AUTHORIZE;

    private String UserId, password, UserName = null, Phone = null;

    final static String successMessage = "Success";
    final static String failureMessage = "Failure";

    private static String TAG = LoginActivity.class.getSimpleName();

    // Progress dialog
    private ProgressDialog pDialog;

    private String jsonResponse;

    JSONObject obj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        net = new NetworkConnection(getApplicationContext());
        this.track = TrackAccess.getInstance(getApplicationContext());

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        track.open();
        Cursor cr = track.getSessionPH(1);
        // track.close();
        if(cr != null && cr.moveToFirst()){
            autoLogin(cr);
        }

        inputPhone = (EditText) findViewById(R.id.input_phone);
        inputPassword = (EditText) findViewById(R.id.input_password);
        forgetPassword = (TextView) findViewById(R.id.ForgetPassword);
        errorUser = (TextView) findViewById(R.id.errorUserid);
        errorPassword = (TextView) findViewById(R.id.errorPass);
        btnSignUp = (Button) findViewById(R.id.btn_signup);

        errorUser.setVisibility(View.INVISIBLE);
        errorPassword.setVisibility(View.INVISIBLE);

        inputPhone.addTextChangedListener(new MyTextWatcher(inputPhone));
        inputPassword.addTextChangedListener(new MyTextWatcher(inputPassword));

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(view);
                submitForm(view);
            }
        });

        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(view);
                forget(view);
            }
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    /**
     * Validating form
     */
    private void autoLogin(Cursor cr){

        showpDialog("Auto Login...");
        String tag_json_obj = "json_obj_req";
        String phoneNumber = cr.getString(cr.getColumnIndex(TrackAccess.USER_PHONE_NUMBER));
        String sessionAPI = cr.getString(cr.getColumnIndex(TrackAccess.USER_SESSION_API));
        if (!cr.isClosed()) {
            cr.close();
        }
        if(net.isNetworkAvailable()){
            obj = new JSONObject();
            try{
                obj.put("MobileNumber", phoneNumber);

                obj.put("SessionApiKey", sessionAPI);

            }catch (Exception e){
                Toast.makeText(getApplicationContext(),
                        "Error: " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    urlJsonObjAuto, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, response.toString());

                    try {
                        // Parsing json object response
                        // response will be a json object
                        if(response != null && !response.equals("null"))
                        {
                            String status = response.getString("Status");
                            String reason = response.getString("Reason");
                            if(status.equals(successMessage)) {
                                JSONObject data = response.getJSONObject("Data");
                                UserName = data.getString("SessionApiKey");
                                Phone = data.getString("UserMobileNumber");
                                //   hidepDialog();
                          /*  Snackbar.make(view, "Welcome "+UserName+"!!!", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show(); */
                                if (UserName != null || Phone != null) {
                                    track.close();
                                    Intent i = new Intent(LoginActivity.this, SplashScreen.class);
                                    startActivity(i);
                                    finish();
                                }
                            }else if(status.equals(failureMessage)) {
                                hidepDialog();
                                Toast.makeText(getApplicationContext(),status +": "+reason+"! Please Log in again...", Toast.LENGTH_LONG).show();
                                return;
                            }

                        }else {
                            UserName = "Invalid User";
                            hidepDialog();
                       /*     Snackbar.make(view, UserName+"!!!", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show(); */
                        }


                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(),
                                "Error: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                    hidepDialog();
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                    error.printStackTrace();
//                Toast.makeText(getApplicationContext(),
//                        error.getMessage(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), "Your Session expired! Please Log in again...", Toast.LENGTH_LONG).show();

                    // hide the progress dialog
                    hidepDialog();
                /*      Snackbar.make(view, "Invalid User", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show(); */
                }
            }) {
                @Override

                public byte[] getBody() {
                    //        Map<String, String> params = getParams();
                    return obj.toString().getBytes();

                }
            };

            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

        }else{
            hidepDialog();
            Toast.makeText(getApplicationContext(), "Network Not Connected!", Toast.LENGTH_LONG).show();
            return;
        }
    }

    private void submitForm(View v) {
        if (!validateEmail()) {
            return;
        }
        if (!validatePassword()) {
            return;
        }
        if (!net.isNetworkAvailable()){
            Toast.makeText(getApplicationContext(), "Network Not Connected!", Toast.LENGTH_SHORT).show();
            return;
        }else{
            UserId = inputPhone.getText().toString();
            password = inputPassword.getText().toString();
            UserName = null;
            makeJsonObjectPost(v);
        }
    }

    private void forget(View v){
        if (validateEmail()) {
            Snackbar.make(v, "Password has been reset and sent to the respective E-mail ID.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return;
        }else{
            Snackbar.make(v, "Enter valid Used ID", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    private boolean validateEmail() {
        String phone = inputPhone.getText().toString().trim();

        if (phone.isEmpty() || phone.length()<10 || phone.length()>13 || !isValidMobile(phone)) {
            // inputLayoutPhone.setError(getString(R.string.err_msg_phone));
            errorUser.setVisibility(View.VISIBLE);
            errorUser.setText(getString(R.string.err_msg_phone));
            requestFocus(inputPhone);
            return false;
        } else {
            //inputLayoutPhone.setErrorEnabled(false);
            errorUser.setVisibility(View.INVISIBLE);
        }

        return true;
    }

    private boolean validatePassword() {
        if (inputPassword.getText().toString().trim().isEmpty()) {
            errorPassword.setVisibility(View.VISIBLE);
            errorPassword.setText(getString(R.string.err_msg_password));
            requestFocus(inputPassword);
            return false;
        } else {
            errorPassword.setVisibility(View.INVISIBLE);
        }

        return true;
    }

    private boolean isValidMobile(String phone)
    {
        return android.util.Patterns.PHONE.matcher(phone).matches();
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_phone:
                    validateEmail();
                    break;
                case R.id.input_password:
                    validatePassword();
                    break;
            }
        }
    }

    private void makeJsonObjectPost(final View view){

        showpDialog("Please wait...");

        String tag_json_obj = "json_obj_req";

        byte[] data = null;
        try {
            data = password.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        String base64pass = Base64.encodeToString(data, Base64.DEFAULT);

        obj = new JSONObject();
        try{
            obj.put("mobileNumber", UserId);

            obj.put("currentPassword", base64pass);

        }catch (Exception e){
            Toast.makeText(getApplicationContext(),
                    "Error: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                urlJsonObj, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());


                try {
                    // Parsing json object response
                    // response will be a json object
                    if(response != null && !response.equals("null"))
                    {
                        String status = response.getString("Status");
                        String reason = response.getString("Reason");
                        if(status.equals(successMessage)) {
                            JSONObject data = response.getJSONObject("Data");
                            UserName = data.getString("SessionApiKey");
                            Phone = data.getString("UserMobileNumber");
                            String fname = data.getString("FirstName");
                            String lname = data.getString("LastName");
                            hidepDialog();
                            Snackbar.make(view, "Welcome " + UserName + " and " + Phone + "!!! " + status, Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            track.open();
                            boolean bool = track.updateUser(1, UserName, Phone, fname, lname, null, null);
                            track.close();
                            if (bool) {
                                Intent i = new Intent(LoginActivity.this, SplashScreen.class);
                                startActivity(i);
                                finish();
                            }
                        }else if(status.equals(failureMessage)){
                            hidepDialog();
                            Snackbar.make(view,status+": "+reason, Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }

                    }else {
                        UserName = "Check Your Internet Connectivity!";
                        hidepDialog();
                        Snackbar.make(view, UserName+"!!!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }


                }
                catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
                hidepDialog();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                error.printStackTrace();
//                Toast.makeText(getApplicationContext(),
//                        error.getMessage(), Toast.LENGTH_SHORT).show();
                // hide the progress dialog
                hidepDialog();
                Snackbar.make(view, "Check Your Internet Connectivity!!!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }) {
            @Override

            public byte[] getBody() {
                //        Map<String, String> params = getParams();
                return obj.toString().getBytes();

            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    private void showpDialog(String message) {
        if (!pDialog.isShowing())
            pDialog.setMessage(message);
        pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    public void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);    }
}
