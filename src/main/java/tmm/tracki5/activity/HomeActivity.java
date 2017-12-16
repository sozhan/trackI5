package tmm.tracki5.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CalendarContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import tmm.tracki5.R;
import tmm.tracki5.model.TrackAccess;
import tmm.tracki5.model.TaskListModel;
import tmm.tracki5.utils.CalendarUtil;
import tmm.tracki5.adapter.TaskListAdapter;

/**
 * Created by Arun on 19/02/16.
 */
public class HomeActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {

    private static String TAG = HomeActivity.class.getSimpleName();

    public ArrayList<String> taskArray;
    public static ArrayList<String> taskCheck;

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    TrackAccess help;
    TextView fName, lName, NoEvents, Clear;
    Button reports;
    ImageButton taskAdd;
    ListView tasklist;
    TaskListModel[] taskListModels;
    AlertDialog.Builder alertLogout;
    LinearLayout attendance, task;
    RelativeLayout marks, leave;
    CheckBox check;

    public Handler handler;

    String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    //String date = "2016 - 02 - 20";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        attendance = (LinearLayout) findViewById(R.id.attendance);
        // task = (LinearLayout) findViewById(R.id.task_layout);
        marks = (RelativeLayout) findViewById(R.id.marks);
        leave = (RelativeLayout) findViewById(R.id.apply_leave);

        help = TrackAccess.getInstance(getApplicationContext());

        fName = (TextView) findViewById(R.id.first_name);
        lName = (TextView) findViewById(R.id.last_name);
        Clear = (TextView) findViewById(R.id.clear);
        NoEvents = (TextView) findViewById(R.id.noEvents);
        NoEvents.setVisibility(View.INVISIBLE);

        reports = (Button) findViewById(R.id.reports);
        taskAdd = (ImageButton) findViewById(R.id.taskAdd);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        CalendarUtil.readCalendarEvent(this);

        taskArray = new ArrayList<String>();
        taskCheck = new ArrayList<String>();
        for (int i = 0; i < CalendarUtil.startDates.size(); i++) {
            if (CalendarUtil.startDates.get(i).equals(date)) {
                taskArray.add(CalendarUtil.nameOfEvent.get(i));
                //taskCheck.add("0");
                //desc.add("From "+CalenderUtil.startDates.get(i)+" to "+CalenderUtil.endDates.get(i));
            }
        }

        attendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeActivity.this, Attendance.class);
                startActivity(i);
            }
        });

        marks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeActivity.this, MarksUpload.class);
                startActivity(i);
            }
        });

        leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCalenderEvent("Leave");
            }
        });

        taskAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent i = new Intent(HomeActivity.this, CalenderAct.class);
//                startActivity(i);
            }
        });

        reports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeActivity.this, Reports.class);
                startActivity(i);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Announcements Coming soon...", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                addCalenderEvent("Announcement");
            }
        });

        alertLogout = new AlertDialog.Builder(this);

        tasklist = (ListView) findViewById(R.id.task_listview);
        //check = (CheckBox) findViewById(R.id.checkBox);
        tasklist.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });
        tasklist.setClickable(true);
        setTaskAdapter();

        Clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearTaskList(view);
            }
        });
//        ViewGroup.LayoutParams listViewParams = (ViewGroup.LayoutParams)tasklist.getLayoutParams();
//        listViewParams.height = 120;
//        tasklist.requestLayout();

        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

        help.open();
        Cursor cr = help.getSessionPH(1);
        if(cr != null && cr.moveToFirst()){
            String fname = cr.getString(cr.getColumnIndex(TrackAccess.USER_FIRST_NAME));
            String lname = cr.getString(cr.getColumnIndex(TrackAccess.USER_LAST_NAME));
            fName.setText(fname);
            lName.setText(lname);
        }
        help.close();
        displayView(0);
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

//        int totalHeight = 0;
//        for (int i = 0; i < listAdapter.getCount(); i++) {
//            View listItem = listAdapter.getView(i, null, listView);
//            listItem.measure(0, 0);
//            totalHeight += listItem.getMeasuredHeight();
//        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = 300;
        listView.setLayoutParams(params);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            logout();
            return true;
        }

        if(id == R.id.action_search){
            Toast.makeText(getApplicationContext(), "Search action is selected!", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayAct(position);
    }

    private void displayAct(int position) {
        switch (position) {
            case 0:
//                Intent myIntent = new Intent( HomeActivity.this, HomeWeb.class);
//                myIntent.putExtra("key","http://tracki5.azurewebsites.net/");
//                startActivityForResult(myIntent, 0);
                break;
            case 1:
//                Intent my1Intent = new Intent( HomeActivity.this, HomeWeb.class);
//                my1Intent.putExtra("key","http://tracki5.azurewebsites.net/profile.html");
//                startActivityForResult(my1Intent, 0);
                break;
//            case 2:
////                fragment = new MessagesFragment();
////                title = getString(R.string.title_messages);
//                break;
            case 3:
                logout();
            default:
                break;
        }

    }

    private void displayView(int position) {
        Fragment fragment = null;
        String title = getString(R.string.app_name);
        switch (position) {
//            case 0:
//                fragment = new HomeFragment();
//                title = getString(R.string.title_home);
//                break;
//            case 1:
////                fragment = new FriendsFragment();
////                title = getString(R.string.title_friends);
//                break;
//            case 2:
////                fragment = new MessagesFragment();
////                title = getString(R.string.title_messages);
//                break;
//            case 3:
////                fragment = new MessagesFragment();
////                title = getString(R.string.title_messages);
//                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();

            // set the toolbar title
            getSupportActionBar().setTitle(title);
        }
    }

    private void logout(){
        alertLogout.setMessage("Are you sure want to Logout?");
        alertLogout.setCancelable(true);

        alertLogout.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        help.open();
                        help.updateUser(1, null, null, null, null, null, null);
                        help.close();
                        finish();
                    }
                });

        alertLogout.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = alertLogout.create();
        alert.show();
    }

    private void taskTextView(){
        tasklist.setVisibility(View.INVISIBLE);
        NoEvents.setVisibility(View.VISIBLE);
    }

    private void addCalenderEvent(String label){

        if (Build.VERSION.SDK_INT >= 14) {
            Calendar cal1 = Calendar.getInstance();
            Intent intent = new Intent(Intent.ACTION_EDIT)
                    .setData(CalendarContract.Events.CONTENT_URI)
                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, cal1.getTimeInMillis())
                    .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, cal1.getTimeInMillis()+60*60*1000)
                    .putExtra(CalendarContract.Events.TITLE, label+": ");
            startActivity(intent);
        }

        else {
            Calendar cal = Calendar.getInstance();
            Intent intent = new Intent(Intent.ACTION_EDIT);
            intent.setType("vnd.android.cursor.item/event");
            intent.putExtra("beginTime", cal.getTimeInMillis());
            intent.putExtra("allDay", true);
            intent.putExtra("rrule", "FREQ=YEARLY");
            intent.putExtra("endTime", cal.getTimeInMillis()+60*60*1000);
            intent.putExtra("title", label);
            startActivity(intent);
        }
    }

    private void clearTaskList(View v){

        //ArrayAdapter adapter = ((CustomTasklistAdapter) tasklist.getAdapter());

        if (taskCheck.size() > 0) {
            for (int i = 0; i < taskCheck.size(); i++) {
                taskArray.remove(taskCheck.get(i));
//                      items.remove(items.get(adapter.getItemViewType(ids.get(i))));
//                      items.remove(adapter.getItemViewType(ids.get(i)));
                setTaskAdapter();
                // System.out.println();
            }
            taskCheck.clear();
            //adapter.notifyDataSetChanged();
            ((TaskListAdapter) tasklist.getAdapter()).notifyDataSetChanged();
        } else {
            Snackbar.make(v, "Select some Events to clear!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }

    }
    private void setTaskAdapter(){
        if(taskArray.size()!=0){
            taskListModels = new TaskListModel[taskArray.size()];
            for (int i=0; i < taskArray.size(); i++){
                taskListModels[i] = new TaskListModel(taskArray.get(i),0);
            }
            TaskListAdapter adapter = new TaskListAdapter(this, taskListModels);
            tasklist.setAdapter(adapter);
            setListViewHeightBasedOnChildren(tasklist);
        }else{
            taskTextView();
        }
    }
}
