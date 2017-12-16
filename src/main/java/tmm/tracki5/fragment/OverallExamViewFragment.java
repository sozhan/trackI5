package tmm.tracki5.fragment;


import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import fr.ganfra.materialspinner.MaterialSpinner;
import tmm.tracki5.AppController;
import tmm.tracki5.R;
import tmm.tracki5.activity.Academic;
import tmm.tracki5.activity.AttenanceReportRegular;
import tmm.tracki5.adapter.NothingSelectedSpinnerAdapter;
import tmm.tracki5.apiController.ApiUrls;
import tmm.tracki5.model.TrackAccess;
import tmm.tracki5.utils.NetworkConnection;

/**
 * A simple {@link Fragment} subclass.
 */
public class OverallExamViewFragment extends Fragment {

    NetworkConnection net;
    private TrackAccess track;
    ApiUrls ApiUrl;
    JSONObject obj;
    JSONObject innerObj[];
    String[] apiClassList, apiSecList, apiRollList, apiNameList;
    String[] error;

    String date = new SimpleDateFormat("dd-MMM-yyyy").format(new Date());

    final static String successMessage = "Success";
    final static String failureMessage = "Failure";

    private static String TAG = Academic.class.getSimpleName();

    private ProgressBar StudentAverageProgress;
    private MaterialSpinner ClassList;
    private MaterialSpinner SectionList;
    private MaterialSpinner RollList;
    ArrayAdapter<String> adapter;
    Cursor cr;
    private int mProgressStatus=0;
    private ProgressDialog pDialog;
    private Handler mHandler = new Handler();
    Button student;
    TextView totalPercentage, grade;

        public OverallExamViewFragment() {
            // Required empty public constructor
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            net = new NetworkConnection(getContext());
            this.track = TrackAccess.getInstance(getContext());

            pDialog = new ProgressDialog(this.getContext());
            pDialog.setCancelable(false);

            View v = inflater.inflate(R.layout.fragment_overallexamview, container, false);

            StudentAverageProgress = (ProgressBar) v.findViewById(R.id.StudentsAcademicAverageBar);

            student = (Button) v.findViewById(R.id.StudentAcademicAverage);
            totalPercentage = (TextView) v.findViewById(R.id.TotalParentPercentage);
            grade = (TextView) v.findViewById(R.id.gradeText);

            SetAverage(StudentAverageProgress,0);

            ClassList = (MaterialSpinner)v.findViewById(R.id.class_List);
            SectionList = (MaterialSpinner)v.findViewById(R.id.section);
            RollList = (MaterialSpinner)v.findViewById(R.id.roll);
            SectionList.setEnabled(false);
            RollList.setEnabled(false);

            track.open();
            cr = track.getSessionPH(1);
            // track.close();
            if (cr != null && cr.moveToFirst()) {
                classList(cr);
            }

            ClassList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3) {
                    String item = ClassList.getSelectedItem().toString();
                    if (arg2 != -1) {
                        Toast.makeText(getContext(),
                                "You have selected item : " + item,
                                Toast.LENGTH_SHORT).show();
                        for (int i = 0; i < apiClassList.length; i++) {
                            if (item == apiClassList[i]) {
                                try {
                                    JSONArray section = innerObj[i].getJSONArray("Sections");
                                    apiSecList = new String[section.length()];
                                    for (int j = 0; j < section.length(); j++) {
                                        apiSecList[j] = section.getString(j);
                                    }
                                    settingSectionList(apiSecList);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    error[0] = "ERROR";
                                    error[1] = e.getMessage();
                                    settingSectionList(error);
                                }
                            }
                        }
                    } else {
                        Toast.makeText(getContext(),
                                "Please select any Class!!!" + item,
                                Toast.LENGTH_SHORT).show();
                        if (SectionList.isEnabled()) {
                            SectionList.setEnabled(false);
                        }
                        if (RollList.isEnabled()) {
                            RollList.setEnabled(false);
                        }
                        student.setText("Student");
                        totalPercentage.setText("");
                        grade.setText("");
                        SetAverage(StudentAverageProgress, 0);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });

            SectionList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3) {
                    // String index = arg0.getItemAtPosition(arg2);
                    //int index1 = --index;
                    String item = SectionList.getSelectedItem().toString();
                    if (arg2 != -1) {
                        Toast.makeText(getContext(),
                                "You have selected item : " + item,
                                Toast.LENGTH_SHORT).show();
                        cr = track.getSessionPH(1);
                        if (cr != null && cr.moveToFirst()) {
                            rollList(cr);
                        }
                    } else {
                        Toast.makeText(getContext(),
                                "Please select any Section!!!" + item,
                                Toast.LENGTH_SHORT).show();
                        if (RollList.isEnabled()) {
                            RollList.setEnabled(false);
                        }
                        student.setText("Student");
                        totalPercentage.setText("");
                        grade.setText("");
                        SetAverage(StudentAverageProgress, 0);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });

            RollList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3) {
                    // String index = arg0.getItemAtPosition(arg2);
                    //int index1 = --index;
                    String item = RollList.getSelectedItem().toString();
                    if (arg2 != -1) {
                        Toast.makeText(getContext(),
                                "You have selected item : " + item,
                                Toast.LENGTH_SHORT).show();
                        student.setText(apiNameList[arg2]);
                        totalPercentage.setText("87");
                        grade.setText("A+");
                        SetAverage(StudentAverageProgress, 87);
                    } else {
                        Toast.makeText(getContext(),
                                "Please select any Roll No!!!" + item,
                                Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });

            return v;
        }

    public void classList(Cursor cr){

        String phoneNumber = cr.getString(cr.getColumnIndex(TrackAccess.USER_PHONE_NUMBER));
        String sessionAPI = cr.getString(cr.getColumnIndex(TrackAccess.USER_SESSION_API));
        if (!cr.isClosed()) {
            cr.close();
        }
        if(net.isNetworkAvailable()){
            showpDialog("Fetching Class List...");
            getClassList(phoneNumber, sessionAPI);
        }else{
            Toast.makeText(getContext(), "Network Not Connected!", Toast.LENGTH_LONG).show();
            return;
        }
    }

    public void rollList(Cursor cr){

        String phoneNumber = cr.getString(cr.getColumnIndex(TrackAccess.USER_PHONE_NUMBER));
        String sessionAPI = cr.getString(cr.getColumnIndex(TrackAccess.USER_SESSION_API));
        if (!cr.isClosed()) {
            cr.close();
        }
        if(net.isNetworkAvailable()){
            showpDialog("Fetching Class List...");
            getRollList(phoneNumber, sessionAPI);
        }else{
            Toast.makeText(getContext(), "Network Not Connected!", Toast.LENGTH_LONG).show();
            return;
        }
    }

    public void getClassList(String phone, String session) {
        String tag_json_obj = "json_obj_req";
        obj = new JSONObject();
        try{
            obj.put("mobileNumber", phone);
            obj.put("sessionApiKey", session);
        }catch (Exception e){

        }
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                ApiUrl.GET_CLASS_SECTION_LIST, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                error = new String[2];
                try {
                    // Parsing json object response
                    // response will be a json object
                    if (response != null && !response.equals("null")) {
                        String status = response.getString("Status");
                        String reason = response.getString("Reason");
                        if (status.equals(successMessage)) {
                            JSONArray data = response.getJSONArray("Data");
                            innerObj = new JSONObject[data.length()];
                            apiClassList = new String[data.length()];
                            for (int i = 0; i < data.length(); i++) {
                                innerObj[i] = data.getJSONObject(i);
                                apiClassList[i] = innerObj[i].getString("Grade");
                                //JSONArray section = innerObj.getJSONArray("Sections");
                            }
                            settingClassList(apiClassList);
                        } else if (status.equals(failureMessage)) {
                            error[0] = "ERROR";
                            error[1] = reason;
                            settingClassList(error);
                        }
                    } else {
                        error[0] = "ERROR";
                        error[1] = "Null Response";
                        settingClassList(error);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    error[0] = "ERROR";
                    error[1] = e.getMessage();
                    settingClassList(error);
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
                hidepDialog();
                Toast.makeText(getContext(), "Check your Internet Connectivity! "+error.getMessage(), Toast.LENGTH_LONG).show();
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

    public void getRollList(String phone, String session) {
        String tag_json_obj = "json_obj_req";
        String date1 = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
        obj = new JSONObject();
        try{
            obj.put("mobileNumber", phone);
            obj.put("sessionApiKey", session);
            obj.put("Grade", ClassList.getSelectedItem());
            obj.put("Section", SectionList.getSelectedItem());
        }catch (Exception e){

        }
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                ApiUrl.GET_STUDENTS_LIST_CLASS_FILTER, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                error = new String[2];
                try {
                    // Parsing json object response
                    // response will be a json object
                    if (response != null && !response.equals("null")) {
                        String status = response.getString("Status");
                        String reason = response.getString("Reason");
                        if (status.equals(successMessage)) {
                            JSONArray data = response.getJSONArray("Data");
                            apiRollList = new String[data.length()];
                            apiNameList = new String[data.length()];
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject roll = data.getJSONObject(i);
                                apiRollList[i] = roll.getString("RollNo");
                                apiNameList[i] = roll.getString("FirstName")+" "+roll.getString("LastName");
                            }
                            settingRollNameValues(apiRollList);
                        } else if (status.equals(failureMessage)) {
                            error[0] = "ERROR";
                            error[1] = reason;
                            settingClassList(error);
                        }
                    } else {
                        error[0] = "ERROR";
                        error[1] = "Null Response";
                        settingClassList(error);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    error[0] = "ERROR";
                    error[1] = e.getMessage();
                    settingClassList(error);
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
                hidepDialog();
                Toast.makeText(getContext(), "Check your Internet Connectivity! "+error.getMessage(), Toast.LENGTH_LONG).show();
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

    public void settingClassList(String[] list){
        if(list[0].equals("ERROR")){
            Toast.makeText(getContext(), list[0]+": "+list[1], Toast.LENGTH_LONG).show();
        }else {
            adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, list);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            ClassList.setAdapter(adapter);
            ClassList.setEnabled(true);
        }
    }

    public void settingSectionList(String[] list){
        if(list[0].equals("ERROR")){
            Toast.makeText(getContext(), list[0]+": "+list[1], Toast.LENGTH_LONG).show();
        }else {
            adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, list);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            SectionList.setAdapter(adapter);
            SectionList.setEnabled(true);
        }
    }

    public void settingRollNameValues(String[] list){
        if(list[0].equals("ERROR")){
            Toast.makeText(getContext(), list[0]+": "+list[1], Toast.LENGTH_LONG).show();
        }else {
            adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, list);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            RollList.setAdapter(adapter);
            RollList.setEnabled(true);
        }
    }

        public void SetAverage(final ProgressBar P, final int average) {
            final Thread background = new Thread() {
                public void run() {
                    try {
                        while(mProgressStatus <= average)
                        {
                            // Thread will sleep for 5 seconds
                            Thread.sleep(1000/50);
                            mProgressStatus+=1;
                            //update the progress bar
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    P.setProgress(mProgressStatus);
                                }
                            });
                        }
                    } catch (Exception e) {

                    }
                }
            };
            background.start();
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

}
