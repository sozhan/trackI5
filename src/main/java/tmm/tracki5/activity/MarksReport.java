package tmm.tracki5.activity;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AdapterView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import fr.ganfra.materialspinner.MaterialSpinner;
import tmm.tracki5.AppController;
import tmm.tracki5.R;
import tmm.tracki5.adapter.MarksRenterAdapter;
import tmm.tracki5.apiController.ApiUrls;
import tmm.tracki5.model.AttendanceData;
import tmm.tracki5.model.TrackAccess;
import tmm.tracki5.utils.NetworkConnection;
import tmm.tracki5.model.MarksReportData.MarksReportData;
import tmm.tracki5.model.Comparators.AverageComparator;
import tmm.tracki5.model.Comparators.MarkComparator;
import tmm.tracki5.model.MarksReportData.ClassSectionAverage;
import tmm.tracki5.model.MarksReportData.MarksReportData;
import tmm.tracki5.model.MarksReportData.StudentAverage;
import tmm.tracki5.model.MarksReportData.StudentMark;

/**
 * Created by Arun on 04/03/16.
 */
public class MarksReport extends AppCompatActivity {

    NetworkConnection net;
    private TrackAccess track;
    //APISubjectList subList;
    public static AttendanceData[] markRenterListModel;

    ApiUrls ApiUrl;
    JSONObject obj;
    JSONObject innerObj[];

    String[] apiSubList, apiClassList, apiSecList, apiExamList, apiMarkList;
    ArrayList<MarksReportData> apiMarksList2;
    String[] error;

    final static String successMessage = "Success";
    final static String failureMessage = "Failure";

    private static String TAG = MarksReport.class.getSimpleName();

    private Toolbar aToolbar;

    private ProgressBar progressWindow;
    Cursor cr, cr1;
    private MaterialSpinner ClassList;
    private MaterialSpinner SectionList;
    private MaterialSpinner SubjectList;
    private MaterialSpinner ExamList;
    private MaterialSpinner SortBy;

    Button Scan;
    Button markRenterSubmit;
    ListView scoreList, scoreRenter;

    // Progress dialog
    private ProgressDialog pDialog;

    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.marks_report);

        aToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(aToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        net = new NetworkConnection(getApplicationContext());
        this.track = TrackAccess.getInstance(getApplicationContext());
        //subList = new APISubjectList(getApplicationContext());

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        progressWindow = (ProgressBar)findViewById(R.id.progressBar1);
        progressWindow.setVisibility(View.INVISIBLE);

        scoreList = (ListView)findViewById(R.id.markList);
        scoreRenter = (ListView)findViewById(R.id.mark_reenter);
        scoreRenter.setVisibility(View.INVISIBLE);

        track.open();
        cr = track.getSessionPH(1);
        // track.close();
        if (cr != null && cr.moveToFirst()) {
            subjectList(cr);
        }

        ClassList = (MaterialSpinner) findViewById(R.id.class_spinner);
        ClassList.setEnabled(false);
        ClassList.setOnItemSelectedListener(new OnItemSelectedListener() {
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
                }
                Scan.setText("Get Mark List");
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        SectionList = (MaterialSpinner) findViewById(R.id.section_spinner);
        SectionList.setEnabled(false);
        SectionList.setOnItemSelectedListener(new OnItemSelectedListener() {
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
                } else {
                    Toast.makeText(getBaseContext(),
                            "Please select any Section!!!" + item,
                            Toast.LENGTH_SHORT).show();
                }
                Scan.setText("Get Mark List");
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        SubjectList = (MaterialSpinner) findViewById(R.id.subject_spinner);
        SubjectList.setEnabled(false);
        SubjectList.setOnItemSelectedListener(new OnItemSelectedListener() {
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
                    cr1 = track.getSessionPH(1);
                    if (cr1 != null && cr1.moveToFirst()) {
                        classExamList(cr1);
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
                }
                Scan.setText("Get Mark List");
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        ExamList = (MaterialSpinner) findViewById(R.id.exam_spinner);
        ExamList.setEnabled(false);
        ExamList.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // String index = arg0.getItemAtPosition(arg2);
                //int index1 = --index;
                String item = SubjectList.getSelectedItem().toString();
                Scan.setText("Get Mark List");
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        Scan = (Button) findViewById(R.id.scan_marks);
        Scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SubjectList.isEnabled() && ClassList.isEnabled()
                        && SectionList.isEnabled() && ExamList.isEnabled()
                        && !ExamList.getSelectedItem().equals("Select Exam")
                        && !SectionList.getSelectedItem().equals("Select Section")) {
                    progressWindow.setVisibility(View.VISIBLE);
                    cr1 = track.getSessionPH(1);
                    if (cr1 != null && cr1.moveToFirst()) {
                        markList(cr1);
                    }
                    if (!SortBy.isEnabled()) {
                        SortBy.setEnabled(true);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please select All!!!", Toast.LENGTH_LONG).show();
                }
            }
        });

        markRenterSubmit = (Button) findViewById(R.id.marks_renter_submit);
        markRenterSubmit.setVisibility(View.INVISIBLE);
        markRenterSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                obj = new JSONObject();
                JSONArray selectedNames = new JSONArray();
                for(int i = 0; i < markRenterListModel.length;i++){
                    try {
                        JSONObject selectedName = new JSONObject();
                        selectedName.put("RollNo", markRenterListModel[i].getRegNo());
                        selectedName.put("Mark", markRenterListModel[i].getMark());
                        selectedNames.put(selectedName);
                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "SelectedName "+e.getMessage()+markRenterListModel[i].getRegNo()
                                + markRenterListModel[i].getIsPresent().toString(), Toast.LENGTH_LONG).show();
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
                        renterMarkList(obj);
                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Upload Object "+e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                if (!cr.isClosed()) {
                    cr.close();
                }
            }
        });

        SortBy = (MaterialSpinner) findViewById(R.id.sort_by);
        String[] sortList = {"High","Low"};
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sortList);
        SortBy.setAdapter(adapter);
        SortBy.setEnabled(false);
        SortBy.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // String index = arg0.getItemAtPosition(arg2);
                //int index1 = --index;
                String item = SortBy.getSelectedItem().toString();


                if (item.contains("High")) {
                    Comparator cmp = new MarkComparator();
                    if (apiMarkList[0].contains("Mark")) {
                        cmp = new MarkComparator();
                    } else if (apiMarkList[0].contains("Average")) {
                        cmp = new AverageComparator();
                    }
                    Collections.sort(apiMarksList2, cmp);
                    ArrayList<String> list = getMarksListForDisplay(apiMarksList2);
                    Collections.reverse(list);
                    String[] listArray = list.toArray(new String[0]);
                    settingMarkList(listArray);
                } else if (item.contains("Low")) {
                    Comparator cmp = new MarkComparator();
                    if (apiMarkList[0].contains("Mark")) {
                        cmp = new MarkComparator();
                    } else if (apiMarkList[0].contains("Average")) {
                        cmp = new AverageComparator();
                    }
                    Collections.sort(apiMarksList2, cmp);
                    ArrayList<String> list = getMarksListForDisplay(apiMarksList2);
                    String[] listArray = list.toArray(new String[0]);
                    settingMarkList(listArray);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
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
//            Array apiList = subList.getSubjectList(phoneNumber, sessionAPI);
//            apiSubList = new String[apiList.length()];
//            settingSubjectList(apiSubList);
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
            showpDialog("Fetching Class List...");
//            Array apiList = subList.getSubjectList(phoneNumber, sessionAPI);
//            apiSubList = new String[apiList.length()];
//            settingSubjectList(apiSubList);
            getClassExamList(phoneNumber, sessionAPI);
        }else{
            Toast.makeText(getApplicationContext(), "Network Not Connected!", Toast.LENGTH_LONG).show();
            return;
        }
    }

    public void markList(Cursor cr){

        String phoneNumber = cr.getString(cr.getColumnIndex(TrackAccess.USER_PHONE_NUMBER));
        String sessionAPI = cr.getString(cr.getColumnIndex(TrackAccess.USER_SESSION_API));
        if (!cr.isClosed()) {
            cr.close();
        }
        if(net.isNetworkAvailable()){
            showpDialog("Fetching Exam List...");
//            Array apiList = subList.getSubjectList(phoneNumber, sessionAPI);
//            apiSubList = new String[apiList.length()];
//            settingSubjectList(apiSubList);
            getMarkList(phoneNumber, sessionAPI);
        }else{
            Toast.makeText(getApplicationContext(), "Network Not Connected!", Toast.LENGTH_LONG).show();
            return;
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

    public void getMarkList(String phone, String session) {
        String tag_json_obj = "json_obj_req";
        obj = new JSONObject();
        try{
            obj.put("mobileNumber", phone);

            obj.put("sessionApiKey", session);

            obj.put("Grade", ClassList.getSelectedItem());

            obj.put("Section", SectionList.getSelectedItem());

            obj.put("Exam", ExamList.getSelectedItem());

            obj.put("Subject", SubjectList.getSelectedItem());

        }catch (Exception e){

        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                ApiUrl.GET_MARKS, null, new Response.Listener<JSONObject>() {
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
                            int switchList = 0;
                            List<String> list = new ArrayList<String>();
                            apiMarksList2 = new ArrayList<MarksReportData>();
                            if(!ClassList.getSelectedItem().equals("all")
                                    && !SectionList.getSelectedItem().equals("all")
                                    && !ExamList.getSelectedItem().equals("all")) {
                                switchList = 1;
                                JSONObject data = response.getJSONObject("Data");
                                String avg = data.getString("ClassAverage");
                                Scan.setText("Class Average: " + avg);
                                JSONArray marks = data.getJSONArray("Marks");
                                markRenterListModel = new AttendanceData[marks.length()];
                                for (int i = 0; i < marks.length(); i++) {
                                    JSONObject mar = marks.getJSONObject(i);
                                    String rollno = mar.getString("RollNo");
                                    int mark = mar.getInt("Mark");
                                    list.add("Roll No-" + rollno + " : " + mark);
                                    StudentMark cm = new StudentMark();
                                    cm.RollNo = rollno;
                                    cm.Mark = mark;
                                    apiMarksList2.add(cm);
                                    if(rollno != null)
                                    markRenterListModel[i] = new AttendanceData(rollno, mark);
                                }
                            } else if(ClassList.getSelectedItem().equals("all")
                                    && SectionList.getSelectedItem().equals("all")
                                    && ExamList.getSelectedItem().equals("all")){
                                JSONArray data = response.getJSONArray("Data");
                                Scan.setText("Subject: " + SubjectList.getSelectedItem());
                                for(int i = 0; i < data.length(); i++){
                                    JSONObject mar = data.getJSONObject(i);
                                    String grade = mar.getString("Grade");
                                    String sec = mar.getString("Section");
                                    double avg = mar.getDouble("Average");
                                    list.add("Class: " + grade + " " + sec +", Average: "+ avg);
                                    ClassSectionAverage ad = new ClassSectionAverage();
                                    ad.Average = avg;
                                    ad.Grade = grade;
                                    ad.Section = sec;
                                    apiMarksList2.add(ad);
                                }
                            } else if(ClassList.getSelectedItem().equals("all")
                                    && SectionList.getSelectedItem().equals("all")){
                                JSONArray data = response.getJSONArray("Data");
                                Scan.setText("Exam: " + ExamList.getSelectedItem());
                                for(int i = 0; i < data.length(); i++){
                                    JSONObject mar = data.getJSONObject(i);
                                    String grade = mar.getString("Grade");
                                    JSONArray marSec = mar.getJSONArray("Marks");
                                    for(int j = 0; j < marSec.length(); j++){
                                        JSONObject secAvg = marSec.getJSONObject(j);
                                        String sec = secAvg.getString("Section");
                                        double avg = secAvg.getDouble("Average");
                                        list.add("Class: " + grade + " " + sec +", Average: "+ avg);
                                        ClassSectionAverage ad = new ClassSectionAverage();
                                        ad.Grade = grade;
                                        ad.Section = sec;
                                        ad.Average = avg;
                                        apiMarksList2.add(ad);
                                    }
                                }
                            } else if(SectionList.getSelectedItem().equals("all")
                                    && ExamList.getSelectedItem().equals("all")){
                                JSONArray data = response.getJSONArray("Data");
                                Scan.setText("Class: " + ClassList.getSelectedItem());
                                for(int i = 0; i < data.length(); i++){
                                    JSONObject mar = data.getJSONObject(i);
                                    String sec = mar.getString("Section");
                                    double avg = mar.getDouble("Average");
                                    list.add("Section: " + sec +", Average: "+ avg);
                                    ClassSectionAverage ad = new ClassSectionAverage();
                                    ad.Grade = ClassList.getSelectedItem().toString();
                                    ad.Section = sec;
                                    ad.Average = avg;
                                    apiMarksList2.add(ad);
                                }
                            } else if(SectionList.getSelectedItem().equals("all")){
                                JSONArray data = response.getJSONArray("Data");
                                Scan.setText("Class: " + ClassList.getSelectedItem()+", Exam: "+ExamList.getSelectedItem());
                                for(int i = 0; i < data.length(); i++){
                                    JSONObject mar = data.getJSONObject(i);
                                    String sec = mar.getString("Section");
                                    double avg = mar.getDouble("Average");
                                    list.add("Section: " + sec +", Average: "+ avg);
                                    ClassSectionAverage ad = new ClassSectionAverage();
                                    ad.Grade = ClassList.getSelectedItem().toString();
                                    ad.Section = sec;
                                    ad.Average = avg;
                                    apiMarksList2.add(ad);
                                }
                            } else if(ExamList.getSelectedItem().equals("all")){
                                JSONArray data = response.getJSONArray("Data");
                                Scan.setText("Class: " + ClassList.getSelectedItem()+" "+SectionList.getSelectedItem());
                                for(int i = 0; i < data.length(); i++){
                                    JSONObject mar = data.getJSONObject(i);
                                    String roll = mar.getString("RollNo");
                                    double avg = mar.getDouble("Average");
                                    list.add("Roll No: " + roll +", Average: "+ avg);
                                    StudentAverage sa = new StudentAverage();
                                    sa.RollNo = roll;
                                    sa.Average = avg;
                                    apiMarksList2.add(sa);
                                }
                            }

                            if(switchList == 1){
                                settingMarkRenterList();
                            } else{
                                apiMarkList = new String[list.size()];
                                list.toArray(apiMarkList);
                                settingMarkList(apiMarkList);
                            }

                        } else if (status.equals(failureMessage)) {
                            error[0] = "ERROR";
                            error[1] = reason;
                            settingMarkList(error);
                        }
                    } else {
                        error[0] = "ERROR";
                        error[1] = "Null Response";
                        settingMarkList(error);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    error[0] = "ERROR";
                    error[1] = e.getMessage();
                    settingMarkList(error);
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

    public void renterMarkList(final JSONObject sendObj) {
        String tag_json_obj = "json_obj_req";
        markRenterSubmit.setEnabled(false);
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
        markRenterSubmit.setEnabled(true);
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

    public void settingMarkList(String[] list){
        switchList();
        if(list[0].equals("ERROR")){
            Toast.makeText(getApplicationContext(), list[0]+": "+list[0], Toast.LENGTH_LONG).show();
        }else {
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
            // adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            scoreList.setAdapter(adapter);
            progressWindow.setVisibility(View.INVISIBLE);
        }
    }

    public void settingMarkRenterList(){
        switchListRenter();
        MarksRenterAdapter adapter = new MarksRenterAdapter(this, markRenterListModel);
        scoreRenter.setAdapter(adapter);
        progressWindow.setVisibility(View.INVISIBLE);
    }

    public void switchList(){
        if(scoreRenter.getVisibility()==View.VISIBLE){
            scoreRenter.setVisibility(View.INVISIBLE);
            scoreList.setVisibility(View.VISIBLE);
        }
        if (!SortBy.isEnabled()) {
            SortBy.setEnabled(true);
        }
        if(markRenterSubmit.getVisibility()==View.VISIBLE){
            markRenterSubmit.setVisibility(View.INVISIBLE);
        }
    }

    public void switchListRenter(){
        if(scoreRenter.getVisibility()==View.INVISIBLE){
            scoreRenter.setVisibility(View.VISIBLE);
            scoreList.setVisibility(View.INVISIBLE);
        }
        if (SortBy.isEnabled()) {
            SortBy.setEnabled(false);
        }
        if(markRenterSubmit.getVisibility()==View.INVISIBLE){
            markRenterSubmit.setVisibility(View.VISIBLE);
        }
    }

    private ArrayList<String> getMarksListForDisplay(ArrayList<MarksReportData> marksData)
    {
        //Placeholder
        ArrayList<String> list = new ArrayList<String>();
        ArrayList<String> fields = new ArrayList<>();

        //Get the type.
        Class marksTypeClass = marksData.get(0).getClass();
        String marksType = marksTypeClass.getName();
        if(marksType.contains("StudentMark"))
        {
            fields.add("RollNo");
            fields.add("Mark");
        }
        else if(marksType.contains("StudentAverage"))
        {
            fields.add("RollNo");
            fields.add("Average");
        }
        else if(marksType.contains("ClassSectionAverage"))
        {
            fields.add("Grade");
            fields.add("Section");
            fields.add("Average");
        }

        StringBuilder item;
        for(MarksReportData d: marksData)
        {
            item = new StringBuilder();
            for(String f: fields)
            {
                Object fValue = "";
                try
                {
                    fValue = marksTypeClass.getField(f).get(d);
                }
                catch (NoSuchFieldException ne) { }
                catch (IllegalAccessException ie) { }
                item.append(", " + f + ": " + fValue);
            }
            item.delete(0, 1);
            list.add(item.toString());
        }
        return list;
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