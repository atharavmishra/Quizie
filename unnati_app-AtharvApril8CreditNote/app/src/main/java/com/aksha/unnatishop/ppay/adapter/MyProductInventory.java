package com.aksha.unnatishop.ppay.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.aksha.unnatishop.R;
import com.aksha.unnatishop.Web.MCrypt;
import com.aksha.unnatishop.model.InventoryProduct;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/*
import aksha.unnati.R;*/

public class MyProductInventory extends RecyclerView.Adapter<MyProductInventory.ViewHolder> {
    //  private ArrayList<String> name;
    //  private ArrayList<String> price;
    //  private ArrayList<String> quantity;
    private Context context;
    String menu_open;
    String pr_qty;
    Boolean flag = false;
    SharedPreferences sharedPreferences;
    List<InventoryProduct> list_productDetails;
    static ArrayList<HashMap<String, String>> product_list;
    // LinearLayout llll;
    Activity activity;
    /* ImageView img_minus;*/ int mvisibile;
    // int position;
    String category_id;
    int in_tot_price;
    ViewPager viewPager;
    FragmentManager fm;
    MCrypt mCrypt;
    String packsized;

    public MyProductInventory(Context context, List<InventoryProduct> list_productDetails) {
        //this.name = list_name;
        this.context = context;
        //.price=list_price;
        viewPager = new ViewPager(context);
        this.list_productDetails = list_productDetails;
        this.category_id = category_id;
        //  this.quantity=quantity;
        mCrypt = new MCrypt();


        product_list = new ArrayList<HashMap<String, String>>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        final View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.my_product_inventory, viewGroup, false);
        /**/
        // viewPager=new ViewPager(view.getContext());
        return new ViewHolder(view);
    }

    public void setItemSearch(HashMap<String, String> data) {
        product_list.add(data);
        //notifyDataSetChanged();
    }

    public ArrayList<HashMap<String, String>> getItemSearch() {
        return product_list;
    }

    public List<InventoryProduct> getItemList() {
        return list_productDetails;
    }

    public void SetImageSelected(String ProductId) {

        for (int icount = 0; icount < list_productDetails.size(); icount++) {
            InventoryProduct item = list_productDetails.get(icount);

            if (item.id.equalsIgnoreCase(ProductId)) {
                list_productDetails.set(icount, item);
                break;
            }
        }
        notifyDataSetChanged();
    }

    InventoryProduct prddtl;

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {

        viewHolder.cardView.setOnClickListener(view -> {
            showAcknowledgementDialog(context, i);
        });

        try {
            viewHolder.tv_name.setText(mCrypt.Decrypt(list_productDetails.get(i).getName()));
            viewHolder.tv_price.setText(mCrypt.Decrypt(list_productDetails.get(i).getPirce()));
            viewHolder.text_item_quant.setText(String.format("Qty:%sPcs", mCrypt.Decrypt(list_productDetails.get(i).getQuantity())));
//            Glide.with(context).load(mCrypt.Decrypt(list_productDetails.get(i).getThumb())).fitCenter().into(viewHolder.product_image);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public int getIndextoRemove(ArrayList<HashMap<String, String>> list_item, String product_id) {
        int retIndex = -1;
        for (int icount = 0; icount < list_item.size(); icount++) {
            HashMap<String, String> item = list_item.get(icount);
            if (item.get("product_id").equalsIgnoreCase(product_id)) {
                retIndex = icount;
                break;
            }
        }
        return retIndex;
    }

    public void showAcknowledgementDialog(Context activity, int i) {
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
//        ImageView productImg = dialog.findViewById(R.id.imageView9);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        try {
            tvName.setText(mCrypt.Decrypt(list_productDetails.get(i).getName()));
            tvPrice.setText(mCrypt.Decrypt(list_productDetails.get(i).getQuantity()));
            tvQuant.setText("Quantity : " + mCrypt.Decrypt(list_productDetails.get(i).getQuantity()) + " Pcs");
//            Glide.with(context).load(mCrypt.Decrypt(list_productDetails.get(i).getThumb())).fitCenter().into(productImg);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        dialog.show();
//        closeBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.dismiss();
//            }
//        });


        dialog.show();

    }


    public Double getListTotal(ArrayList<HashMap<String, String>> list_item) {
        Double price = 0.0;
        for (int icount = 0; icount < list_item.size(); icount++) {
            HashMap<String, String> item = list_item.get(icount);
            Double Unit_Price = Double.parseDouble(item.get("product_price").substring(3, (item.get("product_price").length())).replace(",", ""));
            Unit_Price = Unit_Price * Double.parseDouble(item.get("product_quantity"));
            price = price + Unit_Price;
        }
        return price;
    }

    public Double getTotalTax(ArrayList<HashMap<String, String>> list_item) {
        Double price = 0.0;
        for (int icount = 0; icount < list_item.size(); icount++) {
            HashMap<String, String> item = list_item.get(icount);
            String value = item.get("product_tax").replace(",", "");//substring(4,(item.get("product_tax").length()))
            Double Unit_Price = Double.parseDouble(value) * Double.parseDouble(item.get("product_quantity"));
            price = price + Unit_Price;
        }

        return price;
    }

    public Double getTotalSubsidy(ArrayList<HashMap<String, String>> list_item) {
        //tax
        Double totalsubsidy = 0.0;
        for (int icount = 0; icount < list_item.size(); icount++) {
            HashMap<String, String> item = list_item.get(icount);
            Double Unit_Price = Double.parseDouble(item.get("product_price").substring(3, (item.get("product_price").length())).replace(",", ""));
            String value = item.get("product_tax").replace(",", "");//substring(4,(item.get("product_tax").length()))
            Double tax_Price = Double.parseDouble(value) * Double.parseDouble(item.get("product_quantity"));
            Unit_Price = (Unit_Price)/** Double.parseDouble(item.get("product_quantity"))*/ + tax_Price;
            Double Subsidy_Price = Double.parseDouble(item.get("product_subsidy").trim());
            double discount = (Subsidy_Price / (double) 100) * Unit_Price;
            totalsubsidy += discount;
        }
        return totalsubsidy;
    }

    @Override
    public int getItemCount() {
        return list_productDetails.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_name;
        private TextView tv_price, text_item_quant;
        ImageView img_minus;
        CardView cardView;
        ImageView product_image;
        LinearLayout ll;
        // ArrayList<HashMap<String, String>> list_items;
        Typeface customTypeOne = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/" + context.getResources().getString(R.string.font_normal));

        public ViewHolder(View view) {
            super(view);
            product_image = (ImageView) view.findViewById(R.id.productimage);
            tv_name = (TextView) view.findViewById(R.id.text_item_name);
            tv_price = (TextView) view.findViewById(R.id.text_item_price);
            text_item_quant = (TextView) view.findViewById(R.id.text_item_quant);
            cardView = (CardView) view.findViewById(R.id.card_view);
            tv_name.setTypeface(customTypeOne);
            tv_price.setTypeface(customTypeOne);
            tv_price.setTypeface(customTypeOne);
            text_item_quant.setTypeface(customTypeOne);
            ll = (LinearLayout) view.findViewById(R.id.linear);
            img_minus = (ImageView) view.findViewById(R.id.img_minus);
        }
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }


}


///for refreshing tabs






