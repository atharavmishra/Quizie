package com.aksha.unnatishop.activityfile;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aksha.unnatishop.CustomProgressBar;
import com.aksha.unnatishop.R;
import com.aksha.unnatishop.Retrofitclass.APIService;
import com.aksha.unnatishop.Web.MCrypt;
import com.aksha.unnatishop.Web.WebUtils;
import com.aksha.unnatishop.adapter.RecyclerViewSpacesItemDecoration;
import com.aksha.unnatishop.adapter.Recycler_Adapter_NoData;
import com.aksha.unnatishop.localclass.Preference;
import com.aksha.unnatishop.model.GetProductInventory;
import com.aksha.unnatishop.model.InventoryProduct;
import com.aksha.unnatishop.ppay.adapter.MyProductInventory;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LiquidationActivity extends AppCompatActivity {
    int lastInScreen = 0;
    MCrypt mCrypt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liquidation);
        mCrypt = new MCrypt();
        getProductList();

        Toolbar toolbar1 = findViewById(R.id.toolbar);
        toolbar1.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar1);

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
                        List<InventoryProduct> poList = poListResponse.getProducts();
                        RecyclerView recyclerView = findViewById(R.id.recycler_tab1);
                        if (poList.size() > 0) {

                            final GridLayoutManager layoutManager = new GridLayoutManager(LiquidationActivity.this, 2);
                            recyclerView.setLayoutManager(layoutManager);
                            int spacingInPixels = 10;
                            recyclerView.addItemDecoration(new RecyclerViewSpacesItemDecoration(spacingInPixels));
                            MyProductInventory adapter = new MyProductInventory(LiquidationActivity.this, poList);
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
}