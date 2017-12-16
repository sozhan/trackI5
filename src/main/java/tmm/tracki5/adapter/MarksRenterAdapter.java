package tmm.tracki5.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;
import android.text.TextWatcher;
import android.text.Editable;

import tmm.tracki5.R;
import tmm.tracki5.activity.MarksReport;
import tmm.tracki5.model.AttendanceData;

/**
 * Created by Arun on 20/04/16.
 */
public class MarksRenterAdapter extends ArrayAdapter {

    AttendanceData[] modelItems = null;
    Context context;
    //ViewHolder holder;

    public MarksRenterAdapter(Context context, AttendanceData[] resource) {
        super(context, R.layout.marks_renter_listitemview,resource);
        // TODO Auto-generated constructor stub
        this.context = context;
        this.modelItems = resource;
        //this.holder = null;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        //final int pos = position;
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.marks_renter_listitemview, parent, false);
            holder.markEnter = (EditText) convertView.findViewById(R.id.input_mark_renter);
            holder.name = (TextView) convertView.findViewById(R.id.rollText);
            holder.markEdit = (Button) convertView.findViewById(R.id.marks_renter_edit);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.name.setText("Roll No: "+modelItems[position].getRegNo()+", Mark:");
        holder.markEnter.setText(modelItems[position].getMark());
        holder.markEnter.setEnabled(false);
        holder.markEdit.setText("Edit");
        holder.markEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.markEdit.getText().toString() == "Edit"){
                    holder.markEnter.setEnabled(true);
                    holder.markEdit.setText("Save");
                }else if(holder.markEdit.getText().toString() == "Save"){
                    holder.markEnter.setEnabled(false);
                    String markValue = holder.markEnter.getText().toString();
                    if (markValue != null) {
                        MarksReport.markRenterListModel[position].setMark(markValue);
                    }
                }
//                holder.markEnter.addTextChangedListener(new TextWatcher() {
//
//                    public void afterTextChanged(Editable s) {
//                        String markValue = holder.markEnter.getText().toString();
//                        if (markValue != null) {
//                            MarksReport.markRenterListModel[position].setMark(markValue);
//                        }
//                    }
//
//                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                    }
//
//                    public void onTextChanged(CharSequence s, int start, int before, int count) {
//                        holder.markEnter.setText(s);
//                    }
//                });
            }
        });
        return convertView;
    }

    public class ViewHolder {
        EditText markEnter;
        Button markEdit;
        TextView name;
    }
}

