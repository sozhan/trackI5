package tmm.tracki5.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import tmm.tracki5.model.AttendanceData;
import tmm.tracki5.R;

/**
 * Created by Arun on 02/04/16.
 */
public class AttendanceListAdapter extends BaseAdapter {

    Context context;
    ArrayList<AttendanceData> listData;
    LayoutInflater inflater;
    boolean isDownloadScreen;
    public AttendanceListAdapter(Context context, ArrayList<AttendanceData> listData,boolean isDownloadScreen) {
        this.context = context;
        this.listData = listData;
        this.isDownloadScreen = isDownloadScreen;
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null){
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertView == null){
            convertView = inflater.inflate(R.layout.attendance_row_item,null);
        }

        TextView studentInfo = (TextView)convertView.findViewById(R.id.studentInfo);
        CheckBox checkBox = (CheckBox)convertView.findViewById(R.id.checkBox);

        AttendanceData fileData = (AttendanceData)getItem(position);
        studentInfo.setText(fileData.getAttendanceInfo());
        checkBox.setChecked(fileData.getIsPresent());

        return convertView;
    }

}
