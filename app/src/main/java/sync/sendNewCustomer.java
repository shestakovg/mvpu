package sync;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import Adapters.NewCustomerAdapter;
import Entitys.NewCustomer;
import core.appManager;
import core.wputils;

/**
 * Created by shest on 11/20/2016.
 */

public class sendNewCustomer  extends AsyncTask<Void, Void,Boolean> {
    NewCustomer customer;
    NewCustomerAdapter adapter;

    public sendNewCustomer(NewCustomer customer, NewCustomerAdapter adapter) {
        this.customer = customer;
        this.adapter = adapter;
    }


    @Override
    protected Boolean doInBackground(Void... params) {
        Boolean success = false;
        try {
            URL myurl = new URL(appManager.getOurInstance().appSetupInstance.getServiceUrl() + "/dictionary/savenewcustomer");
            HttpURLConnection connection = (HttpURLConnection) myurl.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");


            JSONObject newObj = new JSONObject();
            newObj.put("newCustomer", convertCustonmerToJSON());

            OutputStreamWriter streamWriter = new OutputStreamWriter(connection.getOutputStream());
            streamWriter.write(newObj.toString());
            //streamWriter.write(convertCustonmerToJSON().toString());
            streamWriter.flush();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                success = true;
            }
            if (connection != null){
                connection.disconnect();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return success;
    }

    private JSONObject convertCustonmerToJSON()
    {
        JSONObject json   = new JSONObject();
        try{
            json.put("routeId", customer.getRouteId());
            json.put("registrationDate", wputils.getDateTimeString(customer.getRegistrationDate()));
            json.put("territory", customer.getTerritoryId());
            json.put("CustomerName",wputils.decodeCyrilicString(customer.getCustomerName()));
            json.put("DeliveryAddress",wputils.decodeCyrilicString(customer.getDeliveryAddress()));
            json.put("OutletCategoty", customer.getOutletCategotyId());
            json.put("PriceType", customer.getPriceTypeId());
            json.put("VisitDay", customer.getVisitDayId());
            json.put("DeliveryDay", customer.getDeliveryDayId());
            json.put("Manager1Name",wputils.decodeCyrilicString(customer.getManager1Name()));
            json.put("Manager2Name",wputils.decodeCyrilicString(customer.getManager2Name()));
            json.put("Manager1Phone",wputils.decodeCyrilicString(customer.getManager1Phone()));
            json.put("Manager2Phone",wputils.decodeCyrilicString(customer.getManager2Phone()));
            json.put("AdditionalInfo",wputils.decodeCyrilicString(customer.getAdditionalInfo()));
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return json;
    }

    @Override
    protected void onPostExecute(Boolean aVoid) {
        super.onPostExecute(aVoid);
        if (aVoid) {
            customer.setSend(true);
            customer.markAsSend();
            adapter.notifyDataSetChanged();
        }
        else
        {
            customer.showSendError();
            adapter.notifyDataSetChanged();
        }
    }
}
