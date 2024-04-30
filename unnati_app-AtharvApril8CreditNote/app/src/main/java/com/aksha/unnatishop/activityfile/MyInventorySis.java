package com.aksha.unnatishop.activityfile;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.aksha.unnatishop.CustomProgressBar;
import com.aksha.unnatishop.R;
import com.aksha.unnatishop.Usbpriting.async.AsyncUsbEscPosPrint;
import com.aksha.unnatishop.Usbpriting.connection.usb.UsbConnection;
import com.aksha.unnatishop.Usbpriting.connection.usb.UsbPrintersConnections;
import com.aksha.unnatishop.Web.ConnectionDetector;
import com.aksha.unnatishop.Web.HttpUrlConnection;
import com.aksha.unnatishop.Web.MCrypt;
import com.aksha.unnatishop.Web.WebUtils;
import com.aksha.unnatishop.adapter.List_Adapter;
import com.aksha.unnatishop.adapter.List_Adapter_NoData;
import com.aksha.unnatishop.localclass.Preference;
import com.aksha.unnatishop.localclass.Product;
import com.aksha.unnatishop.localclass.ProductDetails;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class MyInventorySis extends AppCompatActivity {
    ConnectionDetector cd;
    ArrayList<HashMap<String, String>> list_items;
    MCrypt mCrypt;
    AutoCompleteTextView Autotv_Serch;
    /*  RecyclerView recyclr_MyInventory;*/
//    Button btn_search, btn_print;
    View loadMoreView;
    ListView list_myInventory;
    boolean loadingMore = false;
    int start = 0;
    int limit = 15;
    String totall = "0", ttype;
    int lastInScreen = 0;
    GetProducts ft;
    List<ProductDetails> printlist;
    TextView txt_Total;
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    SharedPreferences settings;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_inventory_sis);

        Toolbar toolbar1 = (Toolbar) findViewById(R.id.toolbar);
        toolbar1.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar1);

        Window w = getWindow();
        w.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar1.setNavigationOnClickListener(view -> finish());

        mCrypt = new MCrypt();
        cd = new ConnectionDetector(this);
        WebUtils.hideOrangeview(this);
        ft = new GetProducts();
        handleEvent();

        settings = this.getSharedPreferences(Preference.MyPREFERENCES, Context.MODE_PRIVATE);
        String italic = null;
        try {
            italic = mCrypt.Decrypt(settings.getString("storetype", ""));
            ttype = italic;
        } catch (Exception e) {
            e.printStackTrace();
        }


//        btn_search = (Button) findViewById(R.id.btn_search);
//        btn_print = (Button) findViewById(R.id.btn_print);
        txt_Total = (TextView) findViewById(R.id.txt_Total);
        Autotv_Serch = (AutoCompleteTextView) findViewById(R.id.autoTV_MYInventory);
        list_myInventory = (ListView) findViewById(R.id.list_myInventory);
        List<ProductDetails> data = new ArrayList<>();
        List_Adapter adapter = new List_Adapter(this, data);
        list_myInventory.setAdapter(adapter);
        ConstraintLayout edittext = findViewById(R.id.linearLayout7);
        Animation animation2 = AnimationUtils.loadAnimation(this, R.anim.slide_away_appear);
        animation2.setStartOffset(800);
        edittext.startAnimation(animation2);
        Animation animation3 = AnimationUtils.loadAnimation(this, R.anim.slide_away_appear);
        animation3.setStartOffset(400);
//        btn_print.startAnimation(animation3);
        Animation animation4 = AnimationUtils.loadAnimation(this, R.anim.slide_away_appear);
        animation4.setStartOffset(600);
//        btn_search.startAnimation(animation4);

//        btn_print.setEnabled(!Preference.getPrinterStatus(this).equalsIgnoreCase("0"));

        loadMoreView = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.load_more, null, false);

        list_myInventory.addFooterView(loadMoreView);
        TextView price = (TextView) findViewById(R.id.price);
        TextView amount = (TextView) findViewById(R.id.amount);
        ConstraintLayout constraintLayout5 = (ConstraintLayout) findViewById(R.id.constraintLayout5);
        if (ttype.equals("SIS-Store")) {
            price.setVisibility(View.GONE);
            amount.setVisibility(View.GONE);
            constraintLayout5.setVisibility(View.GONE);

        } else {
            price.setVisibility(View.VISIBLE);
            constraintLayout5.setVisibility(View.VISIBLE);
            amount.setVisibility(View.VISIBLE);
        }

        list_myInventory.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                lastInScreen = (firstVisibleItem + visibleItemCount);
                if ((lastInScreen == totalItemCount) && !(loadingMore)) {

                    if (cd.isConnectingToInternet()) {

                        if (ft.getStatus() == AsyncTask.Status.PENDING) {
                            ft.execute("");

                        }
                        if (ft.getStatus() == AsyncTask.Status.FINISHED) {
                            try {
                                ft = new GetProducts();
                                ft.execute("");
                            } catch (Exception e) {
                                e.getMessage();
                            }

                        }
                    } else {
                        Toast.makeText(MyInventorySis.this, "Pleae check internet connection", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });


        //            @Override
//            public void onClick(View view) {
//
//                if (cd.isConnectingToInternet()) {
//                    try {
//
//                        if (!Autotv_Serch.getText().toString().equalsIgnoreCase("")) {
//                            list_myInventory.setAdapter(null);
//                            List<ProductDetails> data = new ArrayList<>();
//                            List_Adapter adapter = new List_Adapter(MyInventorySis.this, data);
//                            list_myInventory.setAdapter(adapter);
//                            ft = new GetProducts();
//                            ft.execute(Autotv_Serch.getText().toString());
//
//                        } else {
//                            Toast.makeText(MyInventorySis.this, "Pleae enter product name.", Toast.LENGTH_SHORT).show();
//                        }
//
//                    } catch (Exception e) {
//                        e.getMessage();
//                    }
//                } else {
//                    Toast.makeText(MyInventorySis.this, "Pleae check internet connection", Toast.LENGTH_SHORT).show();
//                }
//            }
        Autotv_Serch.setOnEditorActionListener((textView, actionId, keyEvent) -> {

            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (cd.isConnectingToInternet()) {
                    try {

                        if (!Autotv_Serch.getText().toString().equalsIgnoreCase("")) {
                            list_myInventory.setAdapter(null);
                            List<ProductDetails> data1 = new ArrayList<>();
                            List_Adapter adapter1 = new List_Adapter(MyInventorySis.this, data1);
                            list_myInventory.setAdapter(adapter1);
                            ft = new GetProducts();
                            ft.execute(Autotv_Serch.getText().toString());

                        } else {
                            Toast.makeText(MyInventorySis.this, "Pleae enter product name.", Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        e.getMessage();
                    }
                } else {
                    Toast.makeText(MyInventorySis.this, "Pleae check internet connection", Toast.LENGTH_SHORT).show();
                }
            }
            return false;
        });

//        btn_print.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                new SweetAlertDialog(MyInventorySis.this, SweetAlertDialog.WARNING_TYPE)
//                        .setTitleText("Print Inventory ")
//                        .setContentText("Do you want to print ?")
//                        .setConfirmText("Yes")
//                        .setCancelText("No")
//                        .showCancelButton(true)
//                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                            @Override
//                            public void onClick(SweetAlertDialog sDialog) {
//
//
//                                // WebUtils.printBillssss(mCrypt, getActivity(), printlist, totall);
//                                printssUsb();
//
//                                sDialog.dismissWithAnimation();
//
//
//                            }
//                        })
//                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                            @Override
//                            public void onClick(SweetAlertDialog sDialog) {
//                                sDialog.dismissWithAnimation();
//                            }
//                        })
//                        .show();
//            }
//        });


    }

    private void handleEvent() {

        EditText etSearch = findViewById(R.id.autoTV_MYInventory);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (start <= 0 && s.length() <= 0) {
                    mCrypt = new MCrypt();
                    cd = new ConnectionDetector(MyInventorySis.this);
                    WebUtils.hideOrangeview(MyInventorySis.this);
                    ft = new GetProducts();
                    ft.execute("");

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }

    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbManager usbManager = (UsbManager) MyInventorySis.this.getSystemService(Context.USB_SERVICE);
                    UsbDevice usbDevice = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (usbManager != null && usbDevice != null) {

                            try {
                                new AsyncUsbEscPosPrint(context).execute(WebUtils.getinventryprint(new UsbConnection(usbManager, usbDevice), mCrypt, MyInventorySis.this, printlist, totall));
                                context.unregisterReceiver(usbReceiver);

                            } catch (Exception e) {
                                e.getMessage();
                            }


                        }
                    }
                }
            }
        }
    };

    public void printssUsb() {


        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        while (deviceIterator.hasNext()) {
            UsbDevice device = deviceIterator.next();
            //your code
        }


        UsbConnection usbConnection = UsbPrintersConnections.selectFirstConnected(MyInventorySis.this);
        UsbManager usbManager = (UsbManager) MyInventorySis.this.getSystemService(Context.USB_SERVICE);

        if (usbConnection == null || usbManager == null) {
            new AlertDialog.Builder(MyInventorySis.this).setTitle("USB Connection").setMessage("No USB printer found Please Connect to Printer").show();
            return;
        }

        PendingIntent permissionIntent = PendingIntent.getBroadcast(MyInventorySis.this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        MyInventorySis.this.registerReceiver(usbReceiver, filter);
        usbManager.requestPermission(usbConnection.getDevice(), permissionIntent);

    }


    ////
    ////
    class GetProducts extends AsyncTask<String, Void, List<ProductDetails>> {

        CustomProgressBar dialog;

        String listcount = "0";

        @Override
        protected void onPreExecute() {

            dialog = new CustomProgressBar(MyInventorySis.this);
            dialog.startlodingdiloge();

        }


        @Override
        public List<ProductDetails> doInBackground(String... paramVarArgs) {
            List<ProductDetails> productDetails = null;//=new ArrayList<>();
            String URLLOGIN = "";
            try {
                String store_id = Preference.get_storeId(MyInventorySis.this);
                String api_id = WebUtils.check_Pref(MyInventorySis.this);
                if (paramVarArgs[0].equalsIgnoreCase("")) {
                    URLLOGIN = WebUtils.URL + "?route=mpos/product/invproducts";
                } else {
                    URLLOGIN = WebUtils.URL + "?route=mpos/product/sproductsinv&q=" + mCrypt.Encrypt(paramVarArgs[0]);
                }
                int starttt = lastInScreen - 1;
                String start = String.valueOf(starttt);
                HashMap<String, String> post = new HashMap<String, String>();
                post.put("store_id", store_id);
                post.put("start", mCrypt.Encrypt(start));
                post.put("limit", mCrypt.Encrypt("20"));
                HttpUrlConnection cls = new HttpUrlConnection();
                String response = cls.sendPOST(MyInventorySis.this, URLLOGIN, post, api_id);
                String output = response;
                JSONObject jsonobj = new JSONObject(output);

                Gson gson = new GsonBuilder().create();
                Product prdct = gson.fromJson(output, Product.class);
                productDetails = prdct.products;
                totall = String.valueOf(mCrypt.Decrypt(prdct.total));
                listcount = String.valueOf(mCrypt.Decrypt(prdct.listcount));
            } catch (Exception e) {
                e.getMessage();
            }

            return productDetails;
        }

        @Override
        protected void onPostExecute(final List<ProductDetails> list_obj) {
            super.onPostExecute(list_obj);
            //
            try {
                if (list_obj != null) {
                    if (list_obj.size() > 0) {


                        List<ProductDetails> obj = null;
                        printlist = null;
                        printlist = list_obj;
                        try {
                            Object valadp = list_myInventory.getAdapter();
                            if (valadp instanceof HeaderViewListAdapter) {

                                obj = ((List_Adapter) ((HeaderViewListAdapter) list_myInventory.getAdapter()).getWrappedAdapter()).getItemList();//setItem(list_obj);

                            } else if (valadp instanceof List_Adapter) {
                                obj = ((List_Adapter) (list_myInventory.getAdapter())).getItemList();

                            }
                        } catch (Exception e) {
                            e.getMessage();
                        }
                        if (obj != null) {
                            String PRINTERMAC = "PRINTERMAC";
                            int chunk_list_size = obj.size();
                            if (chunk_list_size == Integer.parseInt(listcount)) {


                            }
                        } else {
                            Toast.makeText(MyInventorySis.this, "No data found please search again", Toast.LENGTH_SHORT).show();
                        }


//print


                        txt_Total.setText("₹ " + totall);
                        try {
                            Object valadp = list_myInventory.getAdapter();
                            if (valadp instanceof HeaderViewListAdapter) {

                                ((List_Adapter) ((HeaderViewListAdapter) list_myInventory.getAdapter()).getWrappedAdapter()).setItem(list_obj);
                                ((List_Adapter) ((HeaderViewListAdapter) list_myInventory.getAdapter()).getWrappedAdapter()).notifyDataSetChanged();
                            } else if (valadp instanceof List_Adapter) {

                                ((List_Adapter) (list_myInventory.getAdapter())).setItem(list_obj);
                                ((List_Adapter) (list_myInventory.getAdapter())).notifyDataSetChanged();
                            }

                        } catch (Exception e) {
                            e.getMessage();
                        }

                    } else {
                        try {

                            if (list_myInventory.getAdapter() instanceof HeaderViewListAdapter) {
                                if (((HeaderViewListAdapter) list_myInventory.getAdapter()).getWrappedAdapter().getCount() == 0) {

                                    List_Adapter_NoData adapter = new List_Adapter_NoData(MyInventorySis.this);
                                    list_myInventory.setAdapter(adapter);

                                }
                            } else {
                                List_Adapter_NoData adapter = new List_Adapter_NoData(MyInventorySis.this);
                                list_myInventory.setAdapter(adapter);
                            }
                        } catch (Exception e) {
                            e.getMessage();
                        }
                        list_myInventory.removeFooterView(loadMoreView);
                    }


                    if (list_obj.size() < 20) {
                        loadingMore = true;
                        list_myInventory.removeFooterView(loadMoreView);
                    } else {
                        loadingMore = false;
                    }
                } else {
                    List_Adapter_NoData adapter = new List_Adapter_NoData(MyInventorySis.this);
                    list_myInventory.setAdapter(adapter);

                    loadingMore = true;
                    list_myInventory.removeFooterView(loadMoreView);

                }

            } catch (Exception er) {
                er.getMessage();
            }
            dialog.dismiss();


        }


    }
}



