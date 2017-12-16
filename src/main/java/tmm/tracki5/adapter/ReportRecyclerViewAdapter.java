package tmm.tracki5.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import tmm.tracki5.R;
import tmm.tracki5.model.ReportViewHolders;
import tmm.tracki5.model.ReportItems;
/**
 * Created by Arun on 03/03/16.
 */
public class ReportRecyclerViewAdapter  extends RecyclerView.Adapter<ReportViewHolders> {

    private List<ReportItems> itemList;
    private Context context;

    public ReportRecyclerViewAdapter(Context context, List<ReportItems> itemList) {
        this.itemList = itemList;
        this.context = context;
    }
    @Override
    public ReportViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.solvent_list, null);
        ReportViewHolders rcv = new ReportViewHolders(layoutView);
        return rcv;
    }
    @Override
    public void onBindViewHolder(ReportViewHolders holder, int position) {
        holder.countryName.setText(itemList.get(position).getName());
        holder.countryDesc.setText(itemList.get(position).getDesc());
        holder.countryPhoto.setImageResource(itemList.get(position).getPhoto());
    }
    @Override
    public int getItemCount() {
        return this.itemList.size();
    }
}
