package Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.uni.mvpu.R;

import java.io.IOException;
import java.util.ArrayList;

import Entitys.OutletObject;
import Entitys.Task;
import Helpers.taskHelper;

public class taskAdapter extends BaseAdapter {
    Context context;
    OutletObject outlet;
    ArrayList<Task> tasks;
    LayoutInflater lInflater;
    taskAdapter currentAdapter;

    public taskAdapter(Context context, OutletObject outlet) throws IOException {
        this.context = context;
        this.outlet = outlet;
        taskHelper helper = new taskHelper(context, false);
        tasks = helper.getOutletTask(outlet.outletId.toString());
        this.lInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.currentAdapter = this;
        helper.close();
    }

    @Override
    public int getCount() {
        return tasks.size();
    }

    @Override
    public Object getItem(int position) {
        return tasks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.lv_item_task, parent, false);
        }
        Task task = tasks.get(position);
        TextView textViewDescription = (TextView) view.findViewById(R.id.taskDescription);
        textViewDescription.setText(task.getDescription());
        if (!task.isResolved()) {
            textViewDescription.setTextColor(Color.BLACK);
            textViewDescription.setTypeface(textViewDescription.getTypeface(), Typeface.BOLD);
        } else {
            textViewDescription.setTextColor(Color.LTGRAY);
            textViewDescription.setTypeface(textViewDescription.getTypeface(), Typeface.ITALIC);
        }

        return view;
    }
}
