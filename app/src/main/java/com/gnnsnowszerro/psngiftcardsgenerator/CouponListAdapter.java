package com.gnnsnowszerro.psngiftcardsgenerator;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Alexandr on 16/06/2017.
 */

public class CouponListAdapter extends BaseAdapter {

    Context context;
    ArrayList<Coupon> coupons;
    LayoutInflater inflater;
    ViewHolder viewHolder;

    public CouponListAdapter(ArrayList<Coupon> coupons, Context context) {
        this.coupons = coupons;
        this.context = context;
    }

    public class ViewHolder {
        TextView couponCostView;
        TextView couponTextView;
        ImageView couponImageView;

        public ViewHolder(View view) {
            couponCostView = (TextView) view.findViewById(R.id.coupon_cost);
            couponTextView = (TextView) view.findViewById(R.id.coupon_text);
            couponImageView = (ImageView) view.findViewById(R.id.coupon_image);
        }

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rootView = convertView;

        Coupon coupon = coupons.get(position);

        if (rootView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            rootView = inflater.inflate(R.layout.coupon_layout, null);

            viewHolder = new ViewHolder(rootView);

            viewHolder.couponCostView.setText(String.format(Locale.getDefault(), "%d Coins", coupon.getCost()));
            viewHolder.couponTextView.setText(String.format(Locale.getDefault(), "%s", coupon.getText()));
            viewHolder.couponImageView.setImageResource(coupon.getImage_id());

            if (coupon.getImage_id() == R.drawable.ic_discount) {
                viewHolder.couponTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            } else {
                viewHolder.couponTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            }

        }

        return rootView;
    }


    @Override
    public int getCount() {
        return coupons.size();
    }

    @Override
    public Object getItem(int position) {
        return coupons.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


}
