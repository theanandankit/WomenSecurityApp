package com.project.womensecurityapp.Report;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.womensecurityapp.R;

import java.util.List;

public class AdapterReport extends RecyclerView.Adapter<AdapterReport.ViewHolder> {

    private static final String TAG = "AdapterReport";

    Context context;
    List<ModelReport> modelReportList;

    public AdapterReport(Context context, List<ModelReport> modelReportList) {
        this.context = context;
        this.modelReportList = modelReportList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_timeline, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Log.d(TAG, "onBindViewHolder: called");

        //get data
        String time = modelReportList.get(position).getTime();
        String place = modelReportList.get(position).getPlace();
        String latitude = modelReportList.get(position).getLatitude();
        String longitude = modelReportList.get(position).getLongitude();

        //set data
        holder.timeTV.setText(time);
        holder.placeTV.setText(place);
        holder.coordinateTV.setText(String.format("%s, %s", latitude, longitude));

    }

    @Override
    public int getItemCount() {
        return modelReportList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView timeTV;
        TextView placeTV;
        TextView coordinateTV;
        LinearLayout report_layout;
        FrameLayout itemLine;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            timeTV = itemView.findViewById(R.id.report_time);
            placeTV = itemView.findViewById(R.id.report_place);
            coordinateTV = itemView.findViewById(R.id.report_coordinate);
            report_layout = itemView.findViewById(R.id.report_layout);
            itemLine = itemView.findViewById(R.id.report_itemLine);

        }
    }
    
}
