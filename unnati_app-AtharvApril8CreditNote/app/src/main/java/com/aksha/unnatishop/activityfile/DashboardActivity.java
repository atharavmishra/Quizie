package com.aksha.unnatishop.activityfile;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.aksha.unnatishop.CustomProgressBar;
import com.aksha.unnatishop.R;
import com.aksha.unnatishop.Retrofitclass.APIService;
import com.aksha.unnatishop.Web.WebUtils;
import com.aksha.unnatishop.adapter.AutoImageSliderAdapter;
import com.aksha.unnatishop.adapter.AutoImageSliderAdapterSeasonImages;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/** @noinspection ALL*/
public class DashboardActivity extends AppCompatActivity {

    ArrayList<String> imageUrls;
    ArrayList<String> seasonImagesUrls;
    SliderView sliderView;
    SliderView sliderViewSeasonImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        getCustomer();
        sliderView = findViewById(R.id.sliderAdapterDashboard);
        sliderViewSeasonImages = findViewById(R.id.sliderAdapterDashboard2);


    }

    public void getCustomer() {
        CustomProgressBar dialog;
        dialog = new CustomProgressBar(this);
        dialog.startlodingdiloge();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(WebUtils.sURL).addConverterFactory(GsonConverterFactory.create()).build();
        APIService apiInterface = retrofit.create(APIService.class);

        try {
            Call<Object> call = apiInterface.getBannerImages();

            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {

                    dialog.dismiss();
                    try {
                        if (response.isSuccessful() && response.body() != null) {

                            JSONObject jsonObject = new JSONObject((Map) response.body());
                            JSONArray bannersArray = jsonObject.getJSONArray("banners");
                            JSONArray seasonImagesArray = jsonObject.getJSONArray("season_images");

                            // List to store banner image URLs
                            imageUrls = new ArrayList<>();
                            seasonImagesUrls = new ArrayList<>();

                            for (int i = 0; i < seasonImagesArray.length(); i++) {
                                JSONObject bannerObject = seasonImagesArray.getJSONObject(i);
                                String imageUrl = bannerObject.getString("image");
                                // Add the image URL to the ArrayList
                                seasonImagesUrls.add(imageUrl);
                            }

                            // Iterate through the banners array and extract image URLs
                            for (int i = 0; i < bannersArray.length(); i++) {
                                JSONObject bannerObject = bannersArray.getJSONObject(i);
                                String imageUrl = bannerObject.getString("image");
                                // Add the image URL to the ArrayList
                                imageUrls.add(imageUrl);
                            }
                            Log.d("ImageURLsResponse", "onResponse: " + imageUrls);
                            sliderView.setSliderAdapter(new AutoImageSliderAdapter(DashboardActivity.this, imageUrls));
                            sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
                            sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
                            sliderView.startAutoCycle();

                            Log.d("ImageURLsResponse", "onResponse: " + seasonImagesUrls);
                            sliderViewSeasonImages.setSliderAdapter(new AutoImageSliderAdapterSeasonImages(DashboardActivity.this, seasonImagesUrls));
                            sliderViewSeasonImages.setIndicatorAnimation(IndicatorAnimationType.WORM);
                            sliderViewSeasonImages.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
                            sliderViewSeasonImages.startAutoCycle();

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        // Handle JSON parsing error here
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                    dialog.dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            dialog.dismiss();
        }


    }

}