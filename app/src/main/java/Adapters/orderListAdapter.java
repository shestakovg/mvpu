package Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.uni.mvpu.ActivityOrderList;
import com.uni.mvpu.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import Entitys.Order;
import Entitys.OrderExtra;
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
        TextView sumTv = ((TextView) view.findViewById(R.id.txtViewListOrderSum));
        sumTv.setText("Сумма: "+wputils.withTwoDecimalPlaces(order.orderSum));
        if (order.deleted) {
            sumTv.setPaintFlags(sumTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            sumTv.setPaintFlags(sumTv.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        }

        //((TextView) view.findViewById(R.id.tvListOrderOutletName)).setText(olObj.outletName+"  "+olObj.outletAddress);
        String secondRowText = wputils.formatDate(order.orderDate) +" "+ order.orderDescription + (order._1CDocNumber1.isEmpty() ? "" : ". BAS: "+order._1CDocNumber1) +". "+order.Comment;
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

        Button btnDelete = (Button) view.findViewById(R.id.btnDeleteOrderListItem);
        btnDelete.setTag(position);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MarkOrderAsDeleted(v);
            }
        });

        Button btnMarkToSend = (Button) view.findViewById(R.id.btnMarkToSendOrderListItem);
        btnMarkToSend.setTag(position);
        btnMarkToSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MarkOrderToSend(v);
            }
        });
        //((TextView) view.findViewById(R.id.txtViewListOrderName)).setText(order.orderDescription);

        if (order.onlyOneClient) {
            ((TextView) view.findViewById(R.id.tvAdditionalInfo)).setVisibility(View.INVISIBLE);
            ((TextView) view.findViewById(R.id.tvAdditionalInfo)).setHeight(0);
        } else {
            ((TextView) view.findViewById(R.id.tvAdditionalInfo)).setVisibility(View.VISIBLE);
            ((TextView) view.findViewById(R.id.tvAdditionalInfo)).setText(order.outletName);
        }
        return view;
    }

    public void btnOrderListItemClick(View view)
    {
        int position = (Integer) view.getTag();
        context.startActivity(appManager.getOurInstance().getOrderActivityIntent(((Order) getItem(position)), context,((Order) getItem(position)).outletId));

        //Toast.makeText(context,((Order) getItem(position)).orderDescription, Toast.LENGTH_SHORT).show();
    }

    public void MarkOrderAsDeleted(View view)
    {
        int position = (Integer) view.getTag();
        final Order orderPos = ((Order) getItem(position));
        final int id = orderPos._id;
        AlertDialog.Builder ad = new AlertDialog.Builder(context);
        ad.setTitle("Удаление заказа");
        if (orderPos.sended) {
            ad.setMessage("Заказ отправлен. Удаление невозможно!");
            ad.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int arg1) {

                }
            });
            ad.show();
        } else {
            ad.setMessage("Удалить заказ?");
            ad.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int arg1) {
                    OrderExtra.setOrderAsDeleted(id, context);
                    ((ActivityOrderList) context).updateOrderList();
                }
            });
            ad.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int arg1) {

                }
            });
            ad.show();
        }
    }

    public void MarkOrderToSend(View view)
    {
        int position = (Integer) view.getTag();
        final Order orderPos = ((Order) getItem(position));
        final int id = orderPos._id;
        AlertDialog.Builder ad = new AlertDialog.Builder(context);
        //ad.setTitle(context.getString(R.string.orderControlMessage));
        if (orderPos.deleted) {
            ad.setMessage("Заказ удален. Изменение невозможно!");
            ad.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int arg1) {

                }
            });
            ad.show();
        } else {
            ad.setMessage(orderPos.allowToSend ? "Не отправлять заказ?" : "Отметить заказ к отправке?");
            ad.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int arg1) {
                    if (orderPos.allowToSend) {
                        OrderExtra.setOrderToInactive(id, context);
                    } else {
                        OrderExtra.setOrderToActive(id, context);
                    }

                    ((ActivityOrderList) context).updateOrderList();
                }
            });
            ad.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int arg1) {

                }
            });
            ad.show();
        }
    }
}
