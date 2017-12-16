package tmm.tracki5.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import tmm.tracki5.R;
import tmm.tracki5.model.ReportItems;
import tmm.tracki5.adapter.ReportRecyclerViewAdapter;

/**
 * Created by Arun on 03/03/16.
 */
public class Reports extends AppCompatActivity {

    private Toolbar rToolbar;

    private StaggeredGridLayoutManager gaggeredGridLayoutReport;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reports);

        rToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(rToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //StaggeredGridView
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        gaggeredGridLayoutReport = new StaggeredGridLayoutManager(4, 1);
        recyclerView.setLayoutManager(gaggeredGridLayoutReport);

        List<ReportItems> reportList = getListItemData();

        ReportRecyclerViewAdapter rcAdapter = new ReportRecyclerViewAdapter(Reports.this, reportList);
        recyclerView.setAdapter(rcAdapter);

    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.report_menu, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        if (id == R.id.action_search) {
//            Toast.makeText(getApplicationContext(), "Search action is selected!", Toast.LENGTH_SHORT).show();
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    private List<ReportItems> getListItemData(){
        List<ReportItems> listViewItems = new ArrayList<ReportItems>();
        listViewItems.add(new ReportItems("Attendance Reports", "Attendance Reports", R.drawable.report_back));
        listViewItems.add(new ReportItems("Marks Report", "Regular User", R.drawable.report_back));
        listViewItems.add(new ReportItems("Academic Report", "Parent User", R.drawable.report_back));

        return listViewItems;
    }
}