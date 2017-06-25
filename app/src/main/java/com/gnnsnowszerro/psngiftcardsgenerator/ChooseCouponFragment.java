package com.gnnsnowszerro.psngiftcardsgenerator;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Alexandr on 16/06/2017.
 */

public class ChooseCouponFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_choose_coupon, container, false);

        ListView listView = (ListView) rootView.findViewById(R.id.coupon_list);

        final ArrayList<Coupon> coupons = new ArrayList<>();
        coupons.add(new Coupon(28000, "50%", R.drawable.ic_discount));
        coupons.add(new Coupon(11500, "20%", R.drawable.ic_discount));
        coupons.add(new Coupon(6000, "10%", R.drawable.ic_discount));
        coupons.add(new Coupon(75000, "200$\n PS Gift Card", R.drawable.ic_gift_card));
        coupons.add(new Coupon(38000, "100$\n PS Gift Card", R.drawable.ic_gift_card));
        coupons.add(new Coupon(19500, "50$\n PS Gift Card", R.drawable.ic_gift_card));
        coupons.add(new Coupon(9900, "25$\n PS Gift Card", R.drawable.ic_gift_card));
        coupons.add(new Coupon(4000, "10$\n PS Gift Card", R.drawable.ic_gift_card));

        final CouponListAdapter couponAdapter = new CouponListAdapter(coupons, getContext());
        listView.setAdapter(couponAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Coupon coupon = (Coupon) couponAdapter.getItem(position);

                Intent intent = new Intent(getContext(), GenerateActivity.class);

                intent.putExtra("cost", coupon.getCost());
                intent.putExtra("text", coupon.getText());
                intent.putExtra("image_id", coupon.getImage_id());

                startActivity(intent);

            }
        });

        listView.setNestedScrollingEnabled(true);

        return rootView;

    }
}
