package tmm.tracki5.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import tmm.tracki5.R;
import tmm.tracki5.model.TaskListModel;
import tmm.tracki5.activity.HomeActivity;

/**
 * Created by Arun on 19/02/16.
 */
public class TaskListAdapter extends ArrayAdapter {

    TaskListModel[] modelItems = null;
    Context context;

    public TaskListAdapter(Context context, TaskListModel[] resource) {
        super(context,R.layout.task_listitemview,resource);
        // TODO Auto-generated constructor stub
        this.context = context;
        this.modelItems = resource;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final int pos = position;
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.task_listitemview, parent, false);
            holder.cb = (CheckBox) convertView.findViewById(R.id.checkBox);
            holder.name = (TextView) convertView.findViewById(R.id.label);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.name.setText(modelItems[position].getName());
//        if(modelItems[position].getValue() == 1)
//            holder.cb.setChecked(true);
//        else
//            holder.cb.setChecked(false);
        holder.cb.setOnCheckedChangeListener(null);
        holder.cb.setChecked(HomeActivity.taskCheck.contains(modelItems[position]));
        holder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    HomeActivity.taskCheck.add(modelItems[position].getName());
//                    System.out.println("IDS A: " + selectedItems.toString());
                } else {
                    if (HomeActivity.taskCheck.contains(modelItems[position])) {
                        int i = HomeActivity.taskCheck.indexOf(modelItems[position]);
                        HomeActivity.taskCheck.remove(i);
                        //  System.out.println("IDS R: " + selectedItems.toString());
                    }
                }
            }
        });
        return convertView;








//    LayoutInflater inflater = ((Activity)context).getLayoutInflater();
//        convertView = inflater.inflate(R.layout.task_listitemview, parent, false);
////        TextView name = (TextView) convertView.findViewById(R.id.label);
////        CheckBox cb = (CheckBox) convertView.findViewById(R.id.checkBox);
//        holder.name.setText(modelItems[position].getName());
//        if(modelItems[position].getValue() == 1)
//        cb.setChecked(true);
//        else
//        cb.setChecked(false);
//        //deletion process
//
//        return convertView;
    }

    public class ViewHolder {
        CheckBox cb;
        TextView name;
    }
}

