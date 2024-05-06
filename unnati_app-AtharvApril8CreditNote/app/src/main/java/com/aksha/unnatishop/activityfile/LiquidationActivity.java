package com.aksha.unnatishop.activityfile;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aksha.unnatishop.CustomProgressBar;
import com.aksha.unnatishop.R;
import com.aksha.unnatishop.Retrofitclass.APIService;
import com.aksha.unnatishop.Retrofitclass.ApiClient;
import com.aksha.unnatishop.Web.ConnectionDetector;
import com.aksha.unnatishop.Web.MCrypt;
import com.aksha.unnatishop.Web.WebUtils;
import com.aksha.unnatishop.adapter.RecyclerViewSpacesItemDecoration;
import com.aksha.unnatishop.adapter.Recycler_Adapter_NoData;
import com.aksha.unnatishop.localclass.Preference;
import com.aksha.unnatishop.model.GetProductInventory;
import com.aksha.unnatishop.model.InventoryProduct;
import com.aksha.unnatishop.ppay.adapter.MyProductInventory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LiquidationActivity extends AppCompatActivity {
    int lastInScreen = 0;
    MCrypt mCrypt;
    ConnectionDetector cd;

    SearchView epos_searchview;

    List<InventoryProduct> poList;


    String productName = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liquidation);
        mCrypt = new MCrypt();
        getProductList();

        cd = new ConnectionDetector(this);
        epos_searchview = findViewById(R.id.autoTV_MYInventory);
        epos_searchview.setActivated(true);
        epos_searchview.setQueryHint("Search Product");
        epos_searchview.onActionViewExpanded();
        epos_searchview.setIconified(false);
        epos_searchview.clearFocus();
        SearchManager searchManager = (SearchManager) this.getSystemService(Context.SEARCH_SERVICE);
        epos_searchview.setSearchableInfo(searchManager.getSearchableInfo(this.getComponentName()));
        epos_searchview.setMaxWidth(Integer.MAX_VALUE);


        epos_searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override

            public boolean onQueryTextSubmit(String query) {


                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                RecyclerView recyclerView = findViewById(R.id.recycler_tab1);
                List<InventoryProduct> list_obj1 = new ArrayList<>();
                List<InventoryProduct> filtered_list = new ArrayList<>();
                if (poList != null)
                    list_obj1 = poList;
                if (s.length() > 0) {
                    for (int i = 0; i < list_obj1.size(); i++) {

                        try {
                            if (mCrypt.Decrypt(list_obj1.get(i).getName()).toLowerCase().contains(s.toLowerCase())) {
                                filtered_list.add(list_obj1.get(i));

                            } else {
                                continue;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }


                    final GridLayoutManager layoutManager = new GridLayoutManager(LiquidationActivity.this, 2);
                    recyclerView.setLayoutManager(layoutManager);
                    MyProductInventory adapter = new MyProductInventory(LiquidationActivity.this, filtered_list, (list_productDetails, position) -> showAcknowledgementDialog(LiquidationActivity.this, position, list_productDetails));
                    recyclerView.setAdapter(adapter);


                } else {
                    final GridLayoutManager layoutManager = new GridLayoutManager(LiquidationActivity.this, 2);
                    recyclerView.setLayoutManager(layoutManager);
                    MyProductInventory adapter = new MyProductInventory(LiquidationActivity.this, list_obj1, (list_productDetails, position) -> showAcknowledgementDialog(LiquidationActivity.this, position, list_productDetails));
                    recyclerView.setAdapter(adapter);


                }


                return false;
            }
        });


        Toolbar toolbar1 = findViewById(R.id.toolbar);
        toolbar1.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar1);


        handleEvent();

        Window w = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            w.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


    }

    private void handleEvent() {

//        Autotv_Serch.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (start <= 0 && s.length() <= 0) {
//                    mCrypt = new MCrypt();
//                    cd = new ConnectionDetector(LiquidationActivity.this);
//                    WebUtils.hideOrangeview(LiquidationActivity.this);
//                    if (cd.isConnectingToInternet()) {
//                        getProductList();
//                    } else {
//                        Toast.makeText(LiquidationActivity.this, "PLease check your Internet connection", Toast.LENGTH_SHORT).show();
//                    }
//
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });
//
//
//        Autotv_Serch.setOnEditorActionListener((textView, actionId, keyEvent) -> {
//
//            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                if (cd.isConnectingToInternet()) {
//                    if (!Autotv_Serch.getText().toString().equalsIgnoreCase("")) {
//
//                        searchProductList(Autotv_Serch.getText().toString());
//
//                    } else {
//                        Toast.makeText(LiquidationActivity.this, "Please enter product name.", Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    Toast.makeText(LiquidationActivity.this, "Please check internet connection", Toast.LENGTH_SHORT).show();
//                }
//
//            }
//            return false;
//        });

    }

//    private void searchProductList(String s) {
//        CustomProgressBar dialog;
//        dialog = new CustomProgressBar(this);
//        dialog.startlodingdiloge();
//        Retrofit retrofit = new Retrofit.Builder().baseUrl(WebUtils.sURL).addConverterFactory(GsonConverterFactory.create()).build();
//        APIService apiInterface = retrofit.create(APIService.class);
//
//        try {
//            Call<GetProductInventory> call = apiInterface.searchInventoryProducts(Preference.get_storeId(LiquidationActivity.this), WebUtils.check_Pref(LiquidationActivity.this), s);
//
//            call.enqueue(new Callback<>() {
//                @Override
//                public void onResponse(@NonNull Call<GetProductInventory> call, @NonNull Response<GetProductInventory> response) {
//                    dialog.dismiss();
//                    if (response.isSuccessful() && response.body() != null) {
//
//                        dialog.dismiss();
//                        GetProductInventory poListResponse = response.body();
//                        assert poListResponse != null;
//                        poList = poListResponse.getProducts();
//                        Log.e("LiquidationActivityTag", "onSuccess: " + poListResponse.getProducts().size());
//                        RecyclerView recyclerView = findViewById(R.id.recycler_tab1);
//                        if (poList.size() > 0) {
//
//                            final GridLayoutManager layoutManager = new GridLayoutManager(LiquidationActivity.this, 2);
//                            recyclerView.setLayoutManager(layoutManager);
//                            int spacingInPixels = 10;
//                            recyclerView.addItemDecoration(new RecyclerViewSpacesItemDecoration(spacingInPixels));
//                            MyProductInventory adapter = new MyProductInventory(LiquidationActivity.this, poList, (list_productDetails, position) -> showAcknowledgementDialog(LiquidationActivity.this, position, list_productDetails));
//                            recyclerView.setAdapter(adapter);
//
//
//                        } else {
//                            int img = R.drawable.empty_list;
//                            Recycler_Adapter_NoData adap = new Recycler_Adapter_NoData(LiquidationActivity.this, img, "No List to show");
//                            recyclerView.setLayoutManager(new LinearLayoutManager(LiquidationActivity.this, LinearLayoutManager.VERTICAL, false));
//                            recyclerView.setAdapter(adap);
//                            dialog.dismiss();
//
//                        }
//
//
//                    }
//                }
//
//                @Override
//                public void onFailure(@NonNull Call<GetProductInventory> call, @NonNull Throwable t) {
//                    try {
//                        Log.e("LiquidationActivityTag", "onFailure: " + call.execute().message());
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
//                    dialog.dismiss();
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//            dialog.dismiss();
//        }
//
//
//    }

    private void getProductList() {
        CustomProgressBar dialog;
        dialog = new CustomProgressBar(this);
        dialog.startlodingdiloge();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(WebUtils.sURL).addConverterFactory(GsonConverterFactory.create()).build();
        APIService apiInterface = retrofit.create(APIService.class);

        try {
            Call<GetProductInventory> call = apiInterface.getInventoryProducts(Preference.get_storeId(LiquidationActivity.this),
                    WebUtils.check_Pref(LiquidationActivity.this));

            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<GetProductInventory> call, @NonNull Response<GetProductInventory> response) {

                    dialog.dismiss();
                    if (response.isSuccessful() && response.body() != null) {

                        dialog.dismiss();
                        GetProductInventory poListResponse = response.body();
                        assert poListResponse != null;
                        poList = poListResponse.getProducts();
                        RecyclerView recyclerView = findViewById(R.id.recycler_tab1);
                        if (poList.size() > 0) {

                            final GridLayoutManager layoutManager = new GridLayoutManager(LiquidationActivity.this, 2);
                            recyclerView.setLayoutManager(layoutManager);
                            int spacingInPixels = 10;
                            recyclerView.addItemDecoration(new RecyclerViewSpacesItemDecoration(spacingInPixels));
                            MyProductInventory adapter = new MyProductInventory(LiquidationActivity.this, poList, new MyProductInventory.OpenDialog() {
                                @Override
                                public void openDialog(List<InventoryProduct> list_productDetails, int position) {
                                    showAcknowledgementDialog(LiquidationActivity.this, position, list_productDetails);
                                }
                            });
                            recyclerView.setAdapter(adapter);


                        } else {
                            int img = R.drawable.empty_list;
                            Recycler_Adapter_NoData adap = new Recycler_Adapter_NoData(LiquidationActivity.this, img, "No List to show");
                            recyclerView.setLayoutManager(new LinearLayoutManager(LiquidationActivity.this, LinearLayoutManager.VERTICAL, false));
                            recyclerView.setAdapter(adap);
                            dialog.dismiss();

                        }


                    }
                }

                @Override
                public void onFailure(@NonNull Call<GetProductInventory> call, @NonNull Throwable t) {
                    dialog.dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            dialog.dismiss();
        }


    }

    public void showAcknowledgementDialog(Context activity, int position, List<InventoryProduct> list_productDetails) {
        final Dialog dialog = new Dialog(activity, R.style.BottomSheetDialog);
        Window window = dialog.getWindow();
        assert window != null;
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.bottom_sheet_my_inventory);
        TextView tvName = dialog.findViewById(R.id.text_item_name);
        TextView tvPrice = dialog.findViewById(R.id.text_item_quant);
        TextView tvQuant = dialog.findViewById(R.id.tvQuantPc);
        ImageView closeBtn = dialog.findViewById(R.id.closeBtn);
        TextView saveBtn = dialog.findViewById(R.id.textView41);
        EditText editText = dialog.findViewById(R.id.editText);
//        ImageView productImg = dialog.findViewById(R.id.imageView9);
        closeBtn.setOnClickListener(view -> dialog.dismiss());
        TextView tvWarning = dialog.findViewById(R.id.tvWarning);


        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String charSequenceString = charSequence.toString();
                if (charSequence.length() > 0) {
                    try {
                        if (Integer.valueOf(charSequenceString) > Integer.parseInt(mCrypt.Decrypt(list_productDetails.get(position).getQuantity()))) {
                            tvWarning.setVisibility(View.VISIBLE);
                            editText.setText("");
                        } else {
                            tvWarning.setVisibility(View.GONE);

                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        try {
            tvName.setText(mCrypt.Decrypt(list_productDetails.get(position).getName()));
            tvPrice.setText(mCrypt.Decrypt(list_productDetails.get(position).getQuantity()));
            tvQuant.setText("Quantity : " + mCrypt.Decrypt(list_productDetails.get(position).getQuantity()) + " Pcs");
//            Glide.with(context).load(mCrypt.Decrypt(list_productDetails.get(i).getThumb())).fitCenter().into(productImg);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Map<String, String> map = new HashMap<>();
        map.put("product_id", "product_id");
        map.put("quantity", "quantity");
        map.put("batch_no", "batch_no");
        map.put("warehouse", "warehouse");
        map.put("store_id", "store_id");
        saveBtn.setOnClickListener(view -> {
            setLiquidationApi(map);
            dialog.dismiss();
        });

        dialog.show();
//        closeBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.dismiss();
//            }
//        });


        dialog.show();

    }

    private void setLiquidationApi(Map<String, String> map) {
        APIService service = ApiClient.getClient().create(APIService.class);
        Call<Object> call = service.confirmLiquidation(map);
        CustomProgressBar dialog;
        dialog = new CustomProgressBar(LiquidationActivity.this);
        dialog.startlodingdiloge();
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    dialog.dismiss();

                    JSONObject jsonObject = new JSONObject((Map) response.body());
                    String message = null;
                    try {
                        message = jsonObject.getString("message");
                        Toast.makeText(LiquidationActivity.this, message, Toast.LENGTH_SHORT).show();
//
                    } catch (JSONException e) {
                        throw new RuntimeException(e);

                    }

//


                } else {
                    new SweetAlertDialog(LiquidationActivity.this, SweetAlertDialog.WARNING_TYPE).setTitleText("Error").setContentText("Something Went Wrong").setConfirmText("Ok").showCancelButton(true).setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismiss();
                        }
                    }).show();
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                System.out.println("Error: " + t.getMessage());
                dialog.dismiss();

            }
        });

    }


}