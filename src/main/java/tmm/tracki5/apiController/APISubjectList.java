package tmm.tracki5.apiController;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.concurrent.ExecutionException;

import tmm.tracki5.AppController;
import tmm.tracki5.activity.MarksReport;

/**
 * Created by Arun on 24/03/16.
 */
public class APISubjectList {

    private Context _context;

    public APISubjectList(Context context){
        this._context = context;
    }

    ApiUrls ApiUrl;
    JSONObject obj;

    private String urlJson = ApiUrl.GET_SUBJECT_LIST;

    String[] apiSubList, error;

    final static String successMessage = "Success";
    final static String failureMessage = "Failure";

    private static String TAG = MarksReport.class.getSimpleName();

    // as a field of the class where i wan't to do the synchronous `volley` call
    Object mLock = new Object();


    // need to have the error and success listeners notifyin
    final boolean[] finished = {false};

    public String[] getSubjectList(String phone, String session){

        String tag_json_obj = "json_obj_req";
        obj = new JSONObject();
        try{
            obj.put("MobileNumber", phone);

            obj.put("SessionApiKey", session);

        }catch (Exception e){
            error = new String[2];
            error[0]="ERROR";
            error[1] = e.getMessage();
            //return error;
        }

        //Using Request Future to block Async task
      /*  RequestFuture<JSONObject> future = RequestFuture.newFuture();

        JsonObjectRequest jsonObjReq1 = new JsonObjectRequest(Method.POST, urlJson, obj, future, future);

        AppController.getInstance().addToRequestQueue(jsonObjReq1, tag_json_obj);

        try {
            JSONObject response = future.get();
            try {
                // Parsing json object response
                // response will be a json object
                if(response != null && !response.equals("null"))
                {
                    String status = response.getString("Status");
                    String reason = response.getString("Reason");
                    if(status.equals(successMessage)) {
                        JSONArray data = response.getJSONArray("Data");
                        for(int i=0; i < data.length(); i++){
                            apiSubList[i] = data.getString(i);
                        }
                        // return error;
                    }else if(status.equals(failureMessage)) {
                        error[0]="ERROR";
                        error[1] = reason;
                        apiSubList = error;
                    }
                }else {
                    error[0]="ERROR";
                    error[1] = "Null Response";
                    apiSubList = error;
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
                error[0]="ERROR";
                error[1] = e.getMessage();
                apiSubList = error;
            }
        } catch (InterruptedException e) {
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                    error.printStackTrace();
//                Toast.makeText(getApplicationContext(),
//                        error.getMessage(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(_context, "Interruptted Response Error", Toast.LENGTH_LONG).show();
                }
            };
            error[0]="ERROR";
            error[1] = "InterruptedException Response";
            apiSubList = error;
            Toast.makeText(_context, "Interruptted Response Error!!!", Toast.LENGTH_LONG).show();
        } catch (ExecutionException e) {
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                    error.printStackTrace();
//                Toast.makeText(getApplicationContext(),
//                        error.getMessage(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(_context, "Execution Response Error", Toast.LENGTH_LONG).show();
                }
            };
            error[0]="ERROR";
            error[1] = "ExecutionException Response";
            apiSubList = error;
            Toast.makeText(_context, "Execution Response Error!!!", Toast.LENGTH_LONG).show();
        } */

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                urlJson, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                    Log.d(TAG, response.toString());
                    try {
                        // Parsing json object response
                        // response will be a json object
                        if (response != null && !response.equals("null")) {
                            String status = response.getString("Status");
                            String reason = response.getString("Reason");
                            if (status.equals(successMessage)) {
                                JSONArray data = response.getJSONArray("Data");
                                apiSubList = new String[data.length()];
                                for (int i = 0; i < data.length(); i++) {
                                    apiSubList[i] = data.getString(i);
                                }
                                // return error;
                            } else if (status.equals(failureMessage)) {
                                apiSubList = new String[2];
                                apiSubList[0] = "ERROR";
                                apiSubList[1] = reason;
                            }
                        } else {
                            apiSubList = new String[2];
                            apiSubList[0] = "ERROR";
                            apiSubList[1] = "Null Response";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        apiSubList = new String[2];
                        apiSubList[0] = "ERROR";
                        apiSubList[1] = e.getMessage();
                    }
                    finished[0] = true;
                    mLock.notify();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                      VolleyLog.d(TAG, "Error: " + error.getMessage());
                    error.printStackTrace();
//                Toast.makeText(getApplicationContext(),
//                        error.getMessage(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(_context, "Response Error", Toast.LENGTH_LONG).show();
                    apiSubList = new String[2];
                    apiSubList[0] = "ERROR";
                    apiSubList[1] = error.getMessage();
                    finished[0] = true;
                    mLock.notify();

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

//        while(apiSubList == null){
//            //AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
//        }
        return apiSubList;
    }


}
