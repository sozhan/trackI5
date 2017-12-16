package tmm.tracki5.activity;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import fr.ganfra.materialspinner.MaterialSpinner;
import tmm.tracki5.AppController;
import tmm.tracki5.R;
import tmm.tracki5.adapter.AttendanceListAdapter;
import tmm.tracki5.apiController.ApiUrls;
import tmm.tracki5.model.AttendanceData;
import tmm.tracki5.model.TrackAccess;
import tmm.tracki5.utils.NetworkConnection;

/**
 * Created by Arun on 03/04/16.
 */
public class MarksUpload extends AppCompatActivity {

    private Toolbar aToolbar;

    NetworkConnection net;
    private TrackAccess track;
    ApiUrls ApiUrl;
    JSONObject obj;
    JSONObject innerObj[], studentObj[];
    ArrayList<AttendanceData> studentList;
    AttendanceListAdapter attendanceAdapter;

    String[] apiSubList, apiClassList, apiSecList, apiExamList;
    String[] apiGradeList = {"Marks"};
    String[] apiTotalMarkList = {"50","100","150","200"};
    String[] error;

    final static String successMessage = "Success";
    final static String failureMessage = "Failure";
    int initialIndex, totalStudent;

    private static String TAG = MarksUpload.class.getSimpleName();

    Button ExamView, Absent, Previous, Next, Upload;
    TextView Roll, Name;
    private ProgressBar progressWindow;
    private MaterialSpinner ClassList, SectionList, SubjectList, ExamList, GradeTypeList, TotalMarks;

    private TextInputLayout inputLayoutMark;
    EditText mark;

    // Progress dialog
    private ProgressDialog pDialog;

    ArrayAdapter<String> adapter;
    Cursor cr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.marks_upload);

        aToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(aToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        net = new NetworkConnection(getApplicationContext());
        this.track = TrackAccess.getInstance(getApplicationContext());

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        ClassList = (MaterialSpinner) findViewById(R.id.class_spin);
        SectionList = (MaterialSpinner) findViewById(R.id.section_spin);
        SubjectList = (MaterialSpinner) findViewById(R.id.subject_spin);
        ExamList = (MaterialSpinner) findViewById(R.id.exam_spin);
        GradeTypeList = (MaterialSpinner) findViewById(R.id.grade_spin);
        TotalMarks = (MaterialSpinner) findViewById(R.id.marks_spin);
        ExamView = (Button) findViewById(R.id.exam_view);
        progressWindow = (ProgressBar) findViewById(R.id.progressBar1);
        progressWindow.setVisibility(View.INVISIBLE);
        mark = (EditText) findViewById(R.id.input_mark);
        inputLayoutMark = (TextInputLayout) findViewById(R.id.input_layout_mark);
        Previous = (Button) findViewById(R.id.previous);
        Next = (Button) findViewById(R.id.next);
        Upload = (Button) findViewById(R.id.uploadMarks);
        Absent = (Button) findViewById(R.id.absent);
        Roll = (TextView) findViewById(R.id.mark_roll);
        Name = (TextView) findViewById(R.id.mark_name);

        ClassList.setEnabled(false);
        SectionList.setEnabled(false);
        ExamList.setEnabled(false);
        GradeTypeList.setEnabled(false);
        TotalMarks.setEnabled(false);
        mark.setEnabled(false);
        Previous.setEnabled(false);
        Previous.setVisibility(View.INVISIBLE);
        Next.setEnabled(false);
        Upload.setEnabled(false);
        Absent.setEnabled(false);

        mark.addTextChangedListener(new MyTextWatcher(mark));

        track.open();
        cr = track.getSessionPH(1);
        // track.close();
        if (cr != null && cr.moveToFirst()) {
            subjectList(cr);
        }

        ClassList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {

                String item = ClassList.getSelectedItem().toString();
                if (arg2 != -1) {
                    Toast.makeText(getBaseContext(),
                            "You have selected item : " + item,
                            Toast.LENGTH_SHORT).show();
                    for (int i = 0; i < apiClassList.length; i++) {
                        if (item == apiClassList[i]) {
                            try {
                                JSONArray section = innerObj[i].getJSONArray("Sections");
                                JSONArray exam = innerObj[i].getJSONArray("Exams");
                                apiSecList = new String[section.length()];
                                apiExamList = new String[exam.length()];
                                for (int j = 0; j < section.length(); j++) {
                                    apiSecList[j] = section.getString(j);
                                }
                                for (int k = 0; k < exam.length(); k++) {
                                    apiExamList[k] = exam.getString(k);
                                }
                                settingSectionList(apiSecList);
                                settingExamList(apiExamList);
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
                    if (ExamList.isEnabled()) {
                        ExamList.setEnabled(false);
                    }
                    if (GradeTypeList.isEnabled()) {
                        GradeTypeList.setEnabled(false);
                    }
                    if (TotalMarks.isEnabled()) {
                        TotalMarks.setEnabled(false);
                    }
                }
                ExamView.setText("Get Student List");
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        SectionList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {

                String item = SectionList.getSelectedItem().toString();
                if (arg2 != -1) {
                    Toast.makeText(getBaseContext(),
                            "You have selected item : " + item,
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getBaseContext(),
                            "Please select any Section!!!" + item,
                            Toast.LENGTH_SHORT).show();
                }
                ExamView.setText("Get Student List");
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
                if (arg2 != -1) {
                    Toast.makeText(getBaseContext(),
                            "You have selected item : " + item,
                            Toast.LENGTH_SHORT).show();
                    cr = track.getSessionPH(1);
                    if (cr != null && cr.moveToFirst()) {
                        classExamList(cr);
                    }
                } else {
                    Toast.makeText(getBaseContext(),
                            "Please select any Subject!!!" + item,
                            Toast.LENGTH_SHORT).show();
                    SubjectList.setError("Subject is Mandatory");
                    if (ClassList.isEnabled()) {
                        ClassList.setEnabled(false);
                    }
                    if (SectionList.isEnabled()) {
                        SectionList.setEnabled(false);
                    }
                    if (ExamList.isEnabled()) {
                        ExamList.setEnabled(false);
                    }
                    if (GradeTypeList.isEnabled()) {
                        GradeTypeList.setEnabled(false);
                    }
                    if (TotalMarks.isEnabled()) {
                        TotalMarks.setEnabled(false);
                    }
                }
                ExamView.setText("Get Student List");
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        ExamList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // String index = arg0.getItemAtPosition(arg2);
                //int index1 = --index;
                String item = ExamList.getSelectedItem().toString();
                if (arg2 != -1) {
                    Toast.makeText(getBaseContext(),
                            "You have selected item : " + item,
                            Toast.LENGTH_SHORT).show();
                    settingGradeTotalMarksList();
                } else {
                    Toast.makeText(getBaseContext(),
                            "Please select any Section!!!" + item,
                            Toast.LENGTH_SHORT).show();
                    if (GradeTypeList.isEnabled()) {
                        GradeTypeList.setEnabled(false);
                    }
                    if (TotalMarks.isEnabled()) {
                        TotalMarks.setEnabled(false);
                    }
                }
                ExamView.setText("Get Student List");
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        GradeTypeList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // String index = arg0.getItemAtPosition(arg2);
                //int index1 = --index;
                String item = GradeTypeList.getSelectedItem().toString();
                if (arg2 != -1) {
                    Toast.makeText(getBaseContext(),
                            "You have selected item : " + item,
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getBaseContext(),
                            "Please select any Grade type!!!" + item,
                            Toast.LENGTH_SHORT).show();
                }
                ExamView.setText("Get Student List");
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        TotalMarks.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // String index = arg0.getItemAtPosition(arg2);
                //int index1 = --index;
                String item = TotalMarks.getSelectedItem().toString();
                if (arg2 != -1) {
                    Toast.makeText(getBaseContext(),
                            "You have selected item : " + item,
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getBaseContext(),
                            "Please select any Total mark!!!" + item,
                            Toast.LENGTH_SHORT).show();
                }
                ExamView.setText("Get Student List");
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        ExamView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SubjectList.isEnabled() && ClassList.isEnabled()
                        && SectionList.isEnabled() && ExamList.isEnabled() && GradeTypeList.isEnabled() && TotalMarks.isEnabled()
                        && !ExamList.getSelectedItem().equals("Select Exam")
                        && !SectionList.getSelectedItem().equals("Select Section")
                        && !GradeTypeList.getSelectedItem().equals("Select Grade")
                        && !TotalMarks.getSelectedItem().equals("Total Marks")) {
                    progressWindow.setVisibility(View.VISIBLE);
                    cr = track.getSessionPH(1);
                    if (cr != null && cr.moveToFirst()) {
                        studentMarkUploadList(cr);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please select All!!!", Toast.LENGTH_LONG).show();
                }
            }
        });

        Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateMark()) {
                    Toast.makeText(getBaseContext(),
                            "Please give correct Mark Value!!!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    studentList.get(initialIndex).setMark(mark.getText().toString());
                    mark.setText("");
                    if (Previous.getVisibility() == View.INVISIBLE || Previous.getVisibility() == View.GONE) {
                        Previous.setVisibility(View.VISIBLE);
                    }
                    if(!Previous.isEnabled()){
                        Previous.setEnabled(true);
                    }
                    if (initialIndex + 1 == totalStudent - 1) {
                        Next.setText("Submit");
                    }
                    loadNextStudent();
                }
            }
        });

        Previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (initialIndex - 1 == 0) {
                        Previous.setVisibility(View.INVISIBLE);
                    }
                    loadPreviousStudent();
            }
        });

        Absent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mark.setText("Absent");
            }
        });

        Upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadStudentMarks();
            }
        });
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
    public void classExamList(Cursor cr){

        String phoneNumber = cr.getString(cr.getColumnIndex(TrackAccess.USER_PHONE_NUMBER));
        String sessionAPI = cr.getString(cr.getColumnIndex(TrackAccess.USER_SESSION_API));
        if (!cr.isClosed()) {
            cr.close();
        }
        if(net.isNetworkAvailable()){
            showpDialog("Fetching Class & Exam List...");
            getClassExamList(phoneNumber, sessionAPI);
        }else{
            Toast.makeText(getApplicationContext(), "Network Not Connected!", Toast.LENGTH_LONG).show();
            return;
        }
    }

    public void studentMarkUploadList(Cursor cr){

        String phoneNumber = cr.getString(cr.getColumnIndex(TrackAccess.USER_PHONE_NUMBER));
        String sessionAPI = cr.getString(cr.getColumnIndex(TrackAccess.USER_SESSION_API));
        if (!cr.isClosed()) {
            cr.close();
        }
        if(net.isNetworkAvailable()){
            showpDialog("Fetching Class Students List...");
            getstudentMarkUploadList(phoneNumber, sessionAPI);
        }else{
            Toast.makeText(getApplicationContext(), "Network Not Connected!", Toast.LENGTH_LONG).show();
            return;
        }
    }

    public void uploadStudentMarks(){
        obj = new JSONObject();
        JSONArray selectedNames = new JSONArray();
        for(int i = 0; i < studentList.size();i++){
            try {
                JSONObject selectedName = new JSONObject();
                selectedName.put("RollNo", studentList.get(i).getRegNo());
                selectedName.put("Mark", studentList.get(i).getMark());
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
                obj.put("Subject", SubjectList.getSelectedItem());
                obj.put("Exam", ExamList.getSelectedItem());
                obj.put("Marks", selectedNames);
                showpDialog("Uploading Mark List...");
                sendMarkList(obj);
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Upload Object "+e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        if (!cr.isClosed()) {
            cr.close();
        }
    }
    public void getSubList(String phone, String session) {
        String tag_json_obj = "json_obj_req";
        obj = new JSONObject();
        try{
            obj.put("mobileNumber", phone);

            obj.put("sessionApiKey", session);

        }catch (Exception e){

        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                ApiUrl.GET_SUBJECT_LIST, null, new Response.Listener<JSONObject>() {
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
                                apiSubList[i] = data.getString(i);
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

    public void getClassExamList(String phone, String session) {
        String tag_json_obj = "json_obj_req";
        obj = new JSONObject();
        try{
            obj.put("mobileNumber", phone);

            obj.put("sessionApiKey", session);

            obj.put("Subject",SubjectList.getSelectedItem());

        }catch (Exception e){

        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                ApiUrl.GET_CLASS_EXAMS, null, new Response.Listener<JSONObject>() {
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

    public void getstudentMarkUploadList(String phone, String session) {
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
                            studentObj = new JSONObject[data.length()];
                            for (int i = 0; i < data.length(); i++) {
                                studentObj[i] = data.getJSONObject(i);
                            }
                            error[0] = "Success";
                            error[1] = "Success";
                            settingStudentList(studentObj, error);
                        } else if (status.equals(failureMessage)) {
                            error[0] = "ERROR";
                            error[1] = reason;
                            settingStudentList(studentObj, error);
                        }
                    } else {
                        error[0] = "ERROR";
                        error[1] = "Null Response";
                        settingStudentList(studentObj, error);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    error[0] = "ERROR";
                    error[1] = e.getMessage();
                    settingStudentList(studentObj, error);
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

    public void sendMarkList(final JSONObject sendObj) {
        String tag_json_obj = "json_obj_req";

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                ApiUrl.UPLOAD_MARKS, sendObj, new Response.Listener<JSONObject>() {
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
                            String data = response.getString("Data");
                            Toast.makeText(getApplicationContext(), "Data uploaded successfully: "+data, Toast.LENGTH_LONG).show();
                        } else if (status.equals(failureMessage)) {
                            hidepDialog();
                            error[0] = "ERROR";
                            error[1] = response.getString("Data");
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

    public void settingSubjectList(String[] list){
        if(list[0].equals("ERROR")){
            Toast.makeText(getApplicationContext(), list[0]+": "+list[1], Toast.LENGTH_LONG).show();
        }else {
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
            // adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            SubjectList.setAdapter(adapter);
            SubjectList.setEnabled(true);
        }
    }

    public void settingClassList(String[] list){
        if(list[0].equals("ERROR")){
            Toast.makeText(getApplicationContext(), list[0]+": "+list[1], Toast.LENGTH_LONG).show();
        }else {
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
            // adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            ClassList.setAdapter(adapter);
            ClassList.setEnabled(true);
        }
    }

    public void settingSectionList(String[] list){
        if(list[0].equals("ERROR")){
            Toast.makeText(getApplicationContext(), list[0]+": "+list[1], Toast.LENGTH_LONG).show();
        }else {
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
            // adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            SectionList.setAdapter(adapter);
            SectionList.setEnabled(true);
        }
    }

    public void settingExamList(String[] list){
        if(list[0].equals("ERROR")){
            Toast.makeText(getApplicationContext(), list[0]+": "+list[1], Toast.LENGTH_LONG).show();
        }else {
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
            // adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            ExamList.setAdapter(adapter);
            ExamList.setEnabled(true);
        }
    }

    public void settingGradeTotalMarksList(){
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, apiGradeList);
            // adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            GradeTypeList.setAdapter(adapter);
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, apiTotalMarkList);
            TotalMarks.setAdapter(adapter);
            GradeTypeList.setEnabled(true);
            TotalMarks.setEnabled(true);
    }

    public void settingStudentList(JSONObject[] obj, String[] list){
        if(list[0].equals("ERROR")){
            Toast.makeText(getApplicationContext(), list[0]+": "+list[1], Toast.LENGTH_LONG).show();
        }else {
            ExamView.setText(ExamList.getSelectedItem()+" / "+TotalMarks.getSelectedItem());
            studentList = new ArrayList<AttendanceData>();
            for (int loop = 0; loop < obj.length; loop++) {
                try {
                    String fname = obj[loop].getString("FirstName");
                    String lname = obj[loop].getString("LastName");
                    String rollNo = obj[loop].getString("RollNo");
                    AttendanceData data = new AttendanceData(fname + " " + lname, rollNo, "");
                    studentList.add(data);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
            if(studentList != null) {
                progressWindow.setVisibility(View.INVISIBLE);
                initialIndex = 0;
                totalStudent = studentList.size();
                Roll.setText(studentList.get(initialIndex).getRegNo());
                Name.setText(studentList.get(initialIndex).getStudentName());
                mark.setEnabled(true);
                Next.setEnabled(true);
                Absent.setEnabled(true);
            }
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
                case R.id.input_mark:
                    if(!validateMark() && mark.isEnabled()){
                        inputLayoutMark.setError("Less than "+TotalMarks.getSelectedItem());
                    }else if(mark.isEnabled()){
                        if(inputLayoutMark.isErrorEnabled()){
                            inputLayoutMark.setErrorEnabled(false);
                        }
                    }
                    break;
            }
        }
    }

    public boolean validateMark(){
        if(mark.getText().toString().isEmpty()){
            return false;
        }else if(mark.getText().toString().equals("Absent")){
            return true;
        }else {
            String temTotal = TotalMarks.getSelectedItem().toString();
            String temMark = mark.getText().toString();
            int tempMark = Integer.parseInt(temMark.trim());
            int tempTotal = Integer.parseInt(temTotal.trim());
            if (tempMark <= tempTotal) {
                return true;
            } else {
                return false;
            }
        }
    }

    private void loadPreviousStudent() {
        if (initialIndex - 1 >= 0) {
            initialIndex = initialIndex - 1;
            setStudentDataOnTextViews(initialIndex);
            if (initialIndex == 0) {
                Toast.makeText(getApplicationContext(), "Currently Giving Assessment For First student",
                        Toast.LENGTH_LONG).show();
            }
            if (initialIndex == totalStudent - 1) {
                Next.setText("Next");
            }
        }
        Upload.setEnabled(false);
        Upload.setBackgroundColor(getResources().getColor(R.color.material_grey_500));
        Next.setVisibility(View.VISIBLE);
    }

    private void loadNextStudent() {
        if (initialIndex + 1 < totalStudent) {
            initialIndex = initialIndex + 1;
            setStudentDataOnTextViews(initialIndex);

        } else if(initialIndex + 1 == totalStudent){
            setStudentDataOnTextViews(initialIndex);
            Upload.setEnabled(true);
            Upload.setBackgroundColor(getResources().getColor(R.color.material_green_700));
            Next.setVisibility(View.INVISIBLE);
        }
    }

    private void setStudentDataOnTextViews(int index) {
        Name.setText(studentList.get(index).getStudentName());
        Roll.setText(studentList.get(index).getRegNo());
        if (!studentList.get(index).getMark().isEmpty()) {
            mark.setText(studentList.get(index).getMark());
        } else {
            mark.setText("");
        }
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