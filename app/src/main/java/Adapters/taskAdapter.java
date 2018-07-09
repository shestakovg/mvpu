package Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.uni.mvpu.R;

import java.io.IOException;
import java.util.ArrayList;

import Dialogs.DlgEditTask;
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
        ((TextView) view.findViewById(R.id.taskNumber)).setText("¹ "+task.getNumber());
        ((TextView) view.findViewById(R.id.taskResult)).setText(task.getResultDescription());
        TextView textViewDescription = (TextView) view.findViewById(R.id.taskDescription);
        textViewDescription.setText(task.getDescription());
        if (!task.isResolved()) {
            textViewDescription.setTextColor(Color.BLACK);
            textViewDescription.setTypeface(textViewDescription.getTypeface(), Typeface.BOLD);
        } else {
            textViewDescription.setTextColor(Color.LTGRAY);
            textViewDescription.setTypeface(textViewDescription.getTypeface(), Typeface.ITALIC);
        }



        ImageView editImage = (ImageView) view.findViewById(R.id.lvImage);
        editImage.setTag(position);
        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTask(v);
            }
        });
        view.setTag(position);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTask(v);
            }
        });
        return view;
    }

    private void editTask(View v) {
        int postion =  (Integer) v.getTag();
        Task task = tasks.get(postion);
        //Toast.makeText(this.context, task.getDescription(), Toast.LENGTH_SHORT).show();
        DlgEditTask dlg = new DlgEditTask(this.context, task);
        dlg.show();
    }
}
