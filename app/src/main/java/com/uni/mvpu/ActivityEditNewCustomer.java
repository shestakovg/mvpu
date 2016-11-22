package com.uni.mvpu;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import Adapters.NewCustomerAdapter;
import Entitys.NewCustomer;
import Entitys.OrderExtra;
import core.DayOfWeekController;
import core.DeliveryAreaController;
import core.OutletCategoryController;
import core.PriceTypeController;
import core.appManager;
import core.priceTypeManager;

public class ActivityEditNewCustomer extends AppCompatActivity {
    protected final Context currentContext = this;
    private   NewCustomer customer;
    private DeliveryAreaController deliveryAreaController;
    private PriceTypeController priceTypeController;
    private OutletCategoryController outletCategoryController;
    private DayOfWeekController dayOfWeekController;

    private EditText EditDeliveryAddress;
    private EditText EditCustomerName;
    private EditText EditManager1Name;
    private EditText EditManager2Name;
    private EditText EditManager1Phone;
    private EditText EditManager2Phone;
    private EditText EditAdditionaInfo;

    private Spinner spinDeliveryArea;

    private Spinner spinOutletCategory;
    private Spinner spinPriceType;
    private Spinner spinVisitDay;
    private Spinner spinDeliveryDay;

    @Override
    public void onBackPressed() {
        if (customer.isHasChanged() && !customer.isSend()) {
            AlertDialog.Builder ad = new AlertDialog.Builder(this);
            ad.setTitle("Вопрос");
            ad.setMessage("Сохранить изменения по клиенту " + customer.getCustomerName() + " ?");
            ad.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int arg1) {
                    if (save()) finish();
                }
            });
            ad.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int arg1) {
                    finish();
                }
            });
            ad.show();
        }
        else     super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_new_customer);
        setTitle("Регистрация нового клиента");
        Intent i = getIntent();
        int id = i.getIntExtra("newCustomer",-1);
        if (id<0)
            customer = new NewCustomer(this, appManager.getOurInstance().appSetupInstance.getRouteId().toString());
        else
            customer = NewCustomerAdapter.GetDataFromDB(this, id).get(0);

        ((Button) this.findViewById(R.id.newCustsOK)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSave(v);
            }
        });

        ((Button) this.findViewById(R.id.newCustsOK)).setEnabled(!customer.isSend());

        deliveryAreaController = new DeliveryAreaController(this);

        EditDeliveryAddress = (EditText)  this.findViewById(R.id.newCustEditDeliveryAddress);
        EditDeliveryAddress.setText(customer.getDeliveryAddress());
        EditDeliveryAddress.addTextChangedListener(
                new TextWatcher()
                {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.length()>0) {
                            customer.setHasChanged(true);
                            customer.setDeliveryAddress(s.toString());
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                }
        );
        EditCustomerName = (EditText)  this.findViewById(R.id.newCustEditCustomerName);
        EditCustomerName.setText(customer.getCustomerName());
        EditCustomerName.addTextChangedListener(
                new TextWatcher()
                {

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.length()>0)
                        {
                            customer.setHasChanged(true);
                            customer.setCustomerName(s.toString());
                        }

                        //Toast.makeText(currentContext, customer.getCustomerName(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                }
        );

        EditManager1Name = (EditText)  this.findViewById(R.id.newCustEditManager1Name);
        EditManager1Name.setText(customer.getManager1Name());
        EditManager1Name.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.length()>0) {
                            customer.setHasChanged(true);
                            customer.setManager1Name(s.toString());
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                }
        );

        EditManager2Name = (EditText)  this.findViewById(R.id.newCustEditManager2Name);
        EditManager2Name.setText(customer.getManager2Name());
        EditManager2Name.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.length()>0) {
                            customer.setHasChanged(true);
                            customer.setManager2Name(s.toString());
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                }
        );


        EditManager1Phone = (EditText)  this.findViewById(R.id.newCustEditManager1Phone);
        EditManager1Phone.setText(customer.getManager1Phone());
        EditManager1Phone.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.length()>0) {
                            customer.setHasChanged(true);
                            customer.setManager1Phone(s.toString());
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                }
        );

        EditManager2Phone = (EditText)  this.findViewById(R.id.newCustEditManager2Phone);
        EditManager2Phone.setText(customer.getManager2Phone());
        EditManager2Phone.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.length()>0) {
                            customer.setHasChanged(true);
                            customer.setManager2Phone(s.toString());
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                }
        );

        EditAdditionaInfo = (EditText)  this.findViewById(R.id.newCustEditAdditionalInfo);
        EditAdditionaInfo.setText(customer.getAdditionalInfo());
        EditAdditionaInfo.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.length()>0) {
                            customer.setHasChanged(true);
                            customer.setAdditionalInfo(s.toString());
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                }
        );

        spinDeliveryArea = (Spinner) this.findViewById(R.id.newCustSpinnerDeliveryArea);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item, deliveryAreaController.getDeliveryAreaNames());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinDeliveryArea.setAdapter(adapter);

        int spinnerPosition = 0;
        if (customer.isNew())
            spinnerPosition = deliveryAreaController.GetDefaultMemeberId();
        else
            spinnerPosition = adapter.getPosition(customer.getTerritoryName());

        spinDeliveryArea.setSelection(spinnerPosition);
        spinDeliveryArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onSpinnerDeliveryAreaItemSelected(parent, view,position,id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        priceTypeController =  new PriceTypeController(this);
        spinPriceType = (Spinner) this.findViewById(R.id.newCustSpinnerPriceType);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item, priceTypeController.getNames());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinPriceType.setAdapter(adapter);

        spinnerPosition = 0;
        if (customer.isNew())
            spinnerPosition = priceTypeController.GetDefaultMemeberId();
        else
            spinnerPosition = adapter.getPosition(customer.getPriceTypeName());

        spinPriceType.setSelection(spinnerPosition);
        spinPriceType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onSpinnerPriceTypeItemSelected(parent, view,position,id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        outletCategoryController =  new OutletCategoryController(this);
        spinOutletCategory = (Spinner) this.findViewById(R.id.newCustSpinnerOutletCategory);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item, outletCategoryController.getNames());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinOutletCategory.setAdapter(adapter);

        spinnerPosition = 0;
        if (customer.isNew())
            spinnerPosition = outletCategoryController.GetDefaultMemeberId();
        else
            spinnerPosition = adapter.getPosition(customer.getOutletCategotyName());

        spinOutletCategory.setSelection(spinnerPosition);
        spinOutletCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onSpinnerOutletCategoryItemSelected(parent, view,position,id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        dayOfWeekController =  new DayOfWeekController(this);
        spinVisitDay = (Spinner) this.findViewById(R.id.newCustSpinnerVisitDay);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item, dayOfWeekController.getNames());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinVisitDay.setAdapter(adapter);

        spinnerPosition = 0;
        if (customer.isNew())
            spinnerPosition = dayOfWeekController.GetDefaultMemeberId();
        else
            spinnerPosition = adapter.getPosition(customer.getVisitDayName());

        spinVisitDay.setSelection(spinnerPosition);
        spinVisitDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onSpinnerVisitDayItemSelected(parent, view,position,id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinDeliveryDay = (Spinner) this.findViewById(R.id.newCustSpinnerDeliveyDay);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item, dayOfWeekController.getNames());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinDeliveryDay.setAdapter(adapter);

        spinnerPosition = 0;
        if (customer.isNew())
            spinnerPosition = dayOfWeekController.GetDefaultMemeberId();
        else
            spinnerPosition = adapter.getPosition(customer.getDeliveryDayName());

        spinDeliveryDay.setSelection(spinnerPosition);
        spinDeliveryDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onSpinnerDeliveryDayItemSelected(parent, view,position,id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void onSpinnerPriceTypeItemSelected(AdapterView<?> parent, View view,int position, long id) {
        if (!priceTypeController.getItem(position).getPriceType().equals(customer.getPriceTypeId())) {
            customer.setHasChanged(true);
            customer.setPriceTypeId(priceTypeController.getItem(position).getPriceType());
            customer.setPriceTypeName(priceTypeController.getItem(position).getPriceName());
        }
    }

    private void onSpinnerOutletCategoryItemSelected(AdapterView<?> parent, View view,int position, long id) {
        if (outletCategoryController.getItem(position).getCategoryOrder()!= customer.getOutletCategotyId()){
            customer.setHasChanged(true);
            customer.setOutletCategotyId(outletCategoryController.getItem(position).getCategoryOrder());
            customer.setOutletCategotyName(outletCategoryController.getItem(position).getCategoryName());
        }
    }

    private void onSpinnerDeliveryAreaItemSelected(AdapterView<?> parent, View view,int position, long id) {
        if (!deliveryAreaController.getItem(position).getIdRef().equals(customer.getTerritoryId())) {
                customer.setHasChanged(true);
                customer.setTerritoryId(deliveryAreaController.getItem(position).getIdRef());
                customer.setTerritoryName(deliveryAreaController.getItem(position).getDescription());}
    }

    private void onSpinnerVisitDayItemSelected(AdapterView<?> parent, View view,int position, long id) {
        if (dayOfWeekController.getItem(position).getDayOrder()!= customer.getVisitDayId()) {
            customer.setHasChanged(true);
            customer.setVisitDayId(dayOfWeekController.getItem(position).getDayOrder());
            customer.setVisitDayName(dayOfWeekController.getItem(position).getDayName());
        }
    }

    private void onSpinnerDeliveryDayItemSelected(AdapterView<?> parent, View view,int position, long id) {
        if (dayOfWeekController.getItem(position).getDayOrder()!=customer.getDeliveryDayId()) {
            customer.setHasChanged(true);
            customer.setDeliveryDayId(dayOfWeekController.getItem(position).getDayOrder());
            customer.setDeliveryDayName(dayOfWeekController.getItem(position).getDayName());
        }
    }

    /**
     * @return
     */
    @NonNull
    private Boolean save()
    {
        if (customer.isValid()) {
            customer.Save();
            return true;
        }
        else
        {
            Toast.makeText(this, customer.getValidationMessage(), Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private void onClickSave(View v)
    {
       if (save()) this.finish();
    }
}
