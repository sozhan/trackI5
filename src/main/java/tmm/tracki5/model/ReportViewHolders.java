package tmm.tracki5.model;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import tmm.tracki5.R;
import tmm.tracki5.activity.AttenanceReportRegular;
import tmm.tracki5.activity.MarksReport;
import tmm.tracki5.activity.Academic;

/**
 * Created by Arun on 03/03/16.
 */
public class ReportViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final Context context;
    public TextView countryName;
    public TextView countryDesc;
    public ImageView countryPhoto;
    int superUser = 0, marksReport = 1, academicReport = 2;
    int position;


    public ReportViewHolders(View itemView) {
        super(itemView);
        context = itemView.getContext();
        itemView.setClickable(true);
        itemView.setOnClickListener(this);
        countryName = (TextView) itemView.findViewById(R.id.country_name);
        countryDesc = (TextView) itemView.findViewById(R.id.country_desc);
        countryPhoto = (ImageView) itemView.findViewById(R.id.country_photo);
    }

    @Override
    public void onClick(View view) {
        position = getPosition();
        final Intent intent;
        if (position == superUser){
            intent =  new Intent(context, AttenanceReportRegular.class);
            context.startActivity(intent);
        } else if(position == marksReport){
            intent =  new Intent(context, MarksReport.class);
            context.startActivity(intent);
        } else if(position == academicReport){
            intent =  new Intent(context, Academic.class);
            context.startActivity(intent);
        }

        //Toast.makeText(view.getContext(), "Clicked Position = " + getPosition(), Toast.LENGTH_SHORT).show();
    }
}

