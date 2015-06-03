package Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.uni.mvpu.R;

import java.util.ArrayList;

import Entitys.Order;
import core.appManager;

/**
 * Created by shestakov.g on 02.06.2015.
 */
public class orderListAdapter extends BaseAdapter {
    Context context;
    LayoutInflater lInflater;
    ArrayList<Order> orders;

    public orderListAdapter(Context context, ArrayList<Order> orders) {
        this.context = context;
        this.orders = orders;
        lInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return orders.size();
    }

    @Override
    public Object getItem(int position) {
        return orders.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private Order getOrder(int position)
    {
        return (Order) getItem(position);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.lv_item_oder, parent, false);
        }
        Order order = getOrder( position);
        ((TextView) view.findViewById(R.id.txtViewListOrderName)).setText(order.orderDescription);
        ((TextView) view.findViewById(R.id.txtViewListOrderSum)).setText("�����: "+Double.toString(order.orderSum));
        Button btn = (Button) view.findViewById(R.id.btnOrderListItem);
        btn.setTag(position);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnOrderListItemClick(v);
            }
        });
        //((TextView) view.findViewById(R.id.txtViewListOrderName)).setText(order.orderDescription);
        return view;
    }

    public void btnOrderListItemClick(View view)
    {
        int position = (Integer) view.getTag();
        context.startActivity(appManager.getOurInstance().getOrderActivityIntent(((Order) getItem(position)), context));
        //Toast.makeText(context,((Order) getItem(position)).orderDescription, Toast.LENGTH_SHORT).show();
    }
}
