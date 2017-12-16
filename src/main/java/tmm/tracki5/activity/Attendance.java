package tmm.tracki5.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.clans.fab.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.ganfra.materialspinner.MaterialSpinner;
import tmm.tracki5.AppController;
import tmm.tracki5.R;
import tmm.tracki5.apiController.ApiUrls;
import tmm.tracki5.model.TrackAccess;
import tmm.tracki5.utils.NetworkConnection;
import tmm.tracki5.model.AttendanceData;
import tmm.tracki5.adapter.AttendanceListAdapter;

/**
 * Created by Arun on 25/02/16.
 */
public class Attendance extends AppCompatActivity {

    private Toolbar aToolbar;

    NetworkConnection net;
    private TrackAccess track;
    ApiUrls ApiUrl;
    JSONObject obj;
    JSONObject innerObj[], attenObj[];
    ArrayList<AttendanceData> studentList;
    AttendanceListAdapter attendanceAdapter;

    String[] apiSubList, apiClassList, apiSecList;
    String[] error;

    String date = new SimpleDateFormat("yyyy/MM/dd").format(new Date());

    final static String successMessage = "Success";
    final static String failureMessage = "Failure";

    private static String TAG = Attendance.class.getSimpleName();

    Button ScanAttendance;
    ListView attendanceListView;
    private ProgressBar progressWindow;
    private MaterialSpinner ClassList;
    private MaterialSpinner SectionList;
    private MaterialSpinner SubjectList;
    EditText searchField;
    FloatingActionButton floatButton;

    // Progress dialog
    private ProgressDialog pDialog;

    ArrayAdapter<String> adapter;
    Cursor cr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendance);

        aToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(aToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        net = new NetworkConnection(getApplicationContext());
        this.track = TrackAccess.getInstance(getApplicationContext());
        //subList = new APISubjectList(getApplicationContext());

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        ClassList = (MaterialSpinner) findViewById(R.id.Class_List);
        SectionList = (MaterialSpinner) findViewById(R.id.Section);
        SubjectList = (MaterialSpinner) findViewById(R.id.Subject);
        ScanAttendance = (Button) findViewById(R.id.ScanForAttendance);
        progressWindow = (ProgressBar) findViewById(R.id.progressBar1);
        progressWindow.setVisibility(View.INVISIBLE);
        attendanceListView = (ListView) findViewById(R.id.AttendanceResultListView);
        searchField = (EditText) findViewById(R.id.searchField);

        SectionList.setEnabled(false);
        SubjectList.setEnabled(false);

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
                // String index = arg0.getItemAtPosition(arg2);
                //int index1 = --index;
                String item = ClassList.getSelectedItem().toString();
                if (arg2 != -1) {
                    Toast.makeText(getBaseContext(),
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
                    Toast.makeText(getBaseContext(),
                            "Please select any Class!!!" + item,
                            Toast.LENGTH_SHORT).show();
                    if (SectionList.isEnabled()) {
                        SectionList.setEnabled(false);
                    }
                    if (SubjectList.isEnabled()) {
                        SubjectList.setEnabled(false);
                    }
                }
                ScanAttendance.setText("Get Student List");
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
                    Toast.makeText(getBaseContext(),
                            "You have selected item : " + item,
                            Toast.LENGTH_SHORT).show();
                    cr = track.getSessionPH(1);
                    if (cr != null && cr.moveToFirst()) {
                        subjectList(cr);
                    }
                } else {
                    Toast.makeText(getBaseContext(),
                            "Please select any Section!!!" + item,
                            Toast.LENGTH_SHORT).show();
                }
                ScanAttendance.setText("Get Student List");
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        SubjectList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // String index = arg0.getItemAtPosition(arg2);
                //int index1 = --index;
                String item = SubjectList.getSelectedItem().toString();
                ScanAttendance.setText("Get Mark List");
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        ScanAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SubjectList.isEnabled() && ClassList.isEnabled()
                        && SectionList.isEnabled() && !SectionList.getSelectedItem().equals("Select Section")) {
                    progressWindow.setVisibility(View.VISIBLE);
                    cr = track.getSessionPH(1);
                    if (cr != null && cr.moveToFirst()) {
                        attenList(cr);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please select Class & Section!!!", Toast.LENGTH_LONG).show();
                }
            }
        });

        createFloatingButton();
    }

    public void createFloatingButton() {
        floatButton = (FloatingActionButton) findViewById(R.id.float_menu);
        floatButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu first item clicked
                showAlert("Are you sure want to upload Attendance! ", Attendance.this);
            }
        });
        floatButton.setEnabled(false);
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
            Toast.makeText(getApplicationContext(), "Network Not Connected!", Toast.LENGTH_LONG).show();
            return;
        }
    }

    public void subjectList(Cursor cr){
        String phoneNumber = cr.getString(cr.getColumnIndex(TrackAccess.USER_PHONE_NUMBER));
        String sessionAPI = cr.getString(cr.getColumnIndex(TrackAccess.USER_SESSION_API));
        if (!cr.isClosed()) {
            cr.close();
        }
        if(net.isNetworkAvailable()){
            showpDialog("Fetching Subject List...");
            getSubList(phoneNumber, sessionAPI);
        }else{
            Toast.makeText(getApplicationContext(), "Network Not Connected!", Toast.LENGTH_LONG).show();
            return;
        }
    }

    public void attenList(Cursor cr){

        String phoneNumber = cr.getString(cr.getColumnIndex(TrackAccess.USER_PHONE_NUMBER));
        String sessionAPI = cr.getString(cr.getColumnIndex(TrackAccess.USER_SESSION_API));
        if (!cr.isClosed()) {
            cr.close();
        }
        if(net.isNetworkAvailable()){
            showpDialog("Fetching Student List...");
            getAttenList(phoneNumber, sessionAPI);
        }else{
            Toast.makeText(getApplicationContext(), "Network Not Connected!", Toast.LENGTH_LONG).show();
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
                Toast.makeText(getApplicationContext(), "Check your Internet Connectivity! "+error.getMessage(), Toast.LENGTH_LONG).show();
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

    public void getSubList(String phone, String session) {
        String tag_json_obj = "json_obj_req";
        obj = new JSONObject();
        try{
            obj.put("mobileNumber", phone);
            obj.put("sessionApiKey", session);
            obj.put("grade", ClassList.getSelectedItem());
            obj.put("section", SectionList.getSelectedItem());
        }catch (Exception e){

        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                ApiUrl.GET_SUBJECT_LIST_CLASS_FILTER, null, new Response.Listener<JSONObject>() {
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
                            apiSubList = new String[data.length()];
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject subName = data.getJSONObject(i);
                                apiSubList[i] = subName.getString("Name");
                            }
                            settingSubjectList(apiSubList);
                        } else if (status.equals(failureMessage)) {
                            error[0] = "ERROR";
                            error[1] = reason;
                            settingSubjectList(error);
                        }
                    } else {
                        error[0] = "ERROR";
                        error[1] = "Null Response";
                        settingSubjectList(error);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    error[0] = "ERROR";
                    error[1] = e.getMessage();
                    settingSubjectList(error);
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
                Toast.makeText(getApplicationContext(), "Check your Internet Connectivity! "+error.getMessage(), Toast.LENGTH_LONG).show();
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

    public void getAttenList(String phone, String session) {
        String tag_json_obj = "json_obj_req";
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
                            attenObj = new JSONObject[data.length()];
                            for (int i = 0; i < data.length(); i++) {
                                attenObj[i] = data.getJSONObject(i);
                            }
                            error[0] = "Success";
                            error[1] = "Success";
                            settingAttenList(attenObj, error);
                        } else if (status.equals(failureMessage)) {
                            error[0] = "ERROR";
                            error[1] = reason;
                            settingAttenList(attenObj, error);
                        }
                    } else {
                        error[0] = "ERROR";
                        error[1] = "Null Response";
                        settingAttenList(attenObj, error);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    error[0] = "ERROR";
                    error[1] = e.getMessage();
                    settingAttenList(attenObj, error);
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
                Toast.makeText(getApplicationContext(), "Check your Internet Connectivity! "+error.getMessage(), Toast.LENGTH_LONG).show();
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
            Toast.makeText(getApplicationContext(), list[0]+": "+list[1], Toast.LENGTH_LONG).show();
        }else {
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            ClassList.setAdapter(adapter);
            ClassList.setEnabled(true);
        }
    }

    public void settingSectionList(String[] list){
        if(list[0].equals("ERROR")){
            Toast.makeText(getApplicationContext(), list[0]+": "+list[1], Toast.LENGTH_LONG).show();
        }else {
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            SectionList.setAdapter(adapter);
            SectionList.setEnabled(true);
        }
    }

    public void settingSubjectList(String[] list){
        if(list[0].equals("ERROR")){
            Toast.makeText(getApplicationContext(), list[0]+": "+list[1], Toast.LENGTH_LONG).show();
        }else {
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            SubjectList.setAdapter(adapter);
            SubjectList.setEnabled(true);
        }
    }

    public void settingAttenList(JSONObject[] obj, String[] list){
        if(list[0].equals("ERROR")){
            Toast.makeText(getApplicationContext(), list[0]+": "+list[1], Toast.LENGTH_LONG).show();
        }else {
            studentList = new ArrayList<AttendanceData>();
            for (int loop = 0; loop < obj.length; loop++) {
                try {
                    String fname = obj[loop].getString("FirstName");
                    String lname = obj[loop].getString("LastName");
                    String rollNo = obj[loop].getString("RollNo");
                    AttendanceData data = new AttendanceData(fname + " " + lname, rollNo, false);
                    studentList.add(data);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
            if(studentList != null) {
                attendanceAdapter = new AttendanceListAdapter(getApplicationContext(), studentList, false);
                attendanceListView.setAdapter(attendanceAdapter);
                attendanceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        studentList.get(position).setIsPresent(!studentList.get(position).getIsPresent());
                        attendanceAdapter.notifyDataSetChanged();
                    }
                });
                progressWindow.setVisibility(View.INVISIBLE);
                floatButton.setEnabled(true);
            }

            searchField.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    ArrayList<AttendanceData> searchedData = new ArrayList<AttendanceData>();

                    // Call back the Adapter with current character to Filter
                    System.out.println("SEARCHING FOR : " + s.toString());

                    if (s.toString().length() > 0) {
                        searchedData = filterListData(s.toString());
                    } else {
                        searchedData = studentList;
                    }

                    //perform search in local
                    System.out.println("Now LIST DATA SIZE :" + searchedData.size());
                    attendanceListView.setAdapter(new AttendanceListAdapter(getApplicationContext(), searchedData, false));
                    attendanceAdapter.notifyDataSetChanged();
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }
    }

    public void uploadAttendance(){
        obj = new JSONObject();
        JSONArray selectedNames = new JSONArray();
        for(int i = 0; i < studentList.size();i++){
            try {
                JSONObject selectedName = new JSONObject();
                selectedName.put("RollNo", studentList.get(i).getRegNo());
                selectedName.put("IsPresent", !studentList.get(i).getIsPresent());
                selectedNames.put(selectedName);
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "SelectedName "+e.getMessage()+studentList.get(i).getRegNo()
                       + studentList.get(i).getIsPresent().toString(), Toast.LENGTH_LONG).show();
            }
        }
        cr = track.getSessionPH(1);
        if (cr != null && cr.moveToFirst()) {
            String phoneNumber = cr.getString(cr.getColumnIndex(TrackAccess.USER_PHONE_NUMBER));
            String sessionAPI = cr.getString(cr.getColumnIndex(TrackAccess.USER_SESSION_API));
            try {
                obj.put("MobileNumber", phoneNumber);
                obj.put("SessionApiKey", sessionAPI);
                obj.put("Grade", ClassList.getSelectedItem());
                obj.put("Section", SectionList.getSelectedItem());
                obj.put("AttendanceDate", date);
                if(SubjectList.isEnabled() && SubjectList.getSelectedItem() != "Select Subject"){
                    obj.put("Subject", SubjectList.getSelectedItem());
                }
                obj.put("Data", selectedNames);
                showpDialog("Uploading Attendance List...");
                sendAttenList(obj);
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Upload Object "+e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        if (!cr.isClosed()) {
            cr.close();
        }
    }

    public void sendAttenList(final JSONObject sendObj) {
        String tag_json_obj = "json_obj_req";

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                ApiUrl.UPLOAD_ATTENDANCE, null, new Response.Listener<JSONObject>() {
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
                            hidepDialog();
                            boolean data = response.getBoolean("Data");
                            if(data){
                                Toast.makeText(getApplicationContext(), "Data uploaded successfully: "+data, Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(getApplicationContext(), "Data uploaded successfully: "+data, Toast.LENGTH_LONG).show();
                            }

                        } else if (status.equals(failureMessage)) {
                            hidepDialog();
                            error[0] = "ERROR";
                            error[1] = reason;
                            Toast.makeText(getApplicationContext(), error[0]+": "+error[1], Toast.LENGTH_LONG).show();
                        }
                    } else {
                        hidepDialog();
                        error[0] = "ERROR";
                        error[1] = "Null Response";
                        Toast.makeText(getApplicationContext(), error[0]+": "+error[1], Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    hidepDialog();
                    e.printStackTrace();
                    error[0] = "ERROR";
                    error[1] = e.getMessage();
                    Toast.makeText(getApplicationContext(), error[0]+": "+error[1], Toast.LENGTH_LONG).show();;
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
                Toast.makeText(getApplicationContext(), "Check your Internet Connectivity! "+error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public byte[] getBody() {
                //        Map<String, String> params = getParams();
                return sendObj.toString().getBytes();
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    public ArrayList<AttendanceData> filterListData(String matchText) {
        ArrayList<AttendanceData> filteredRecords = new ArrayList<AttendanceData>();
        for (int i = 0; i < studentList.size(); i++) {
            AttendanceData studentData = studentList.get(i);
            System.out.println(studentData.getAttendanceInfo() + "=compare=" + matchText);
            System.out.println(studentData.getAttendanceInfo().toString().toLowerCase().contains(matchText.toLowerCase()));
            if (studentData.getAttendanceInfo().toString().toLowerCase().contains(matchText.toLowerCase())) {
                filteredRecords.add(studentData);
            }
        }
        System.out.println("Filtered SIZE : " + filteredRecords.size());
        return filteredRecords;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.report_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            this.finish();
            return true;
        }
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showAlert(String alertMessage, Context context) {
        new AlertDialog.Builder(context)
                .setTitle("Attenance Upload")
                .setMessage(alertMessage)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        uploadAttendance();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();

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
