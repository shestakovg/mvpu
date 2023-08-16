package Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.uni.mvpu.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import Entitys.Order;
import Entitys.OutletObject;
import core.OrderListMode;
import core.appManager;
import core.wputils;

/**
 * Created by shestakov.g on 02.06.2015.
 */
public class orderListAdapter extends BaseAdapter {
    Context context;
    LayoutInflater lInflater;
    ArrayList<Order> orders;
    private OrderListMode orderMode;
    private String outletId="";

    public orderListAdapter(Context context, ArrayList<Order> orders, String outletId) {
        this.context = context;
        this.orders = orders;
        lInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (outletId.isEmpty()) {
            orderMode = OrderListMode.orderByDay;
        }
        else
        {
            orderMode = OrderListMode.ordersByOutlets;
            this.outletId = outletId;
        }
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
        Order order = getOrder(position);
        OutletObject olObj = OutletObject.getInstance(UUID.fromString(order.outletId) ,  context);
        //((TextView) view.findViewById(R.id.txtViewListOrderName)).setText(order.orderDescription);
        //((TextView) view.findViewById(R.id.txtViewListOrderName)).setText("");
        ((TextView) view.findViewById(R.id.txtViewListOrderSum)).setText("—ÛÏÏ‡: "+wputils.withTwoDecimalPlaces(order.orderSum));
        //((TextView) view.findViewById(R.id.tvListOrderOutletName)).setText(olObj.outletName+"  "+olObj.outletAddress);
        String secondRowText = order.orderDescription + (order._1CDocNumber1.isEmpty() ? "" : ". BAS: "+order._1CDocNumber1) +". "+order.Comment;
        ((TextView) view.findViewById(R.id.tvListOrderOutletName)).setText(secondRowText);
        ImageView orderSended = (ImageView) view.findViewById(R.id.ivImage);
        //orderSended.setImageResource(R.drawable.document_16);
        if (order.underSumLimit)
        {
            orderSended.setImageResource(R.drawable.unchecked);
        }
        else {
            if (order.sended)
                orderSended.setImageResource(R.drawable.read);
            else
                orderSended.setImageResource(R.drawable.unread);
        }
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
        context.startActivity(appManager.getOurInstance().getOrderActivityIntent(((Order) getItem(position)), context,((Order) getItem(position)).outletId));

        //Toast.makeText(context,((Order) getItem(position)).orderDescription, Toast.LENGTH_SHORT).show();
    }
}
