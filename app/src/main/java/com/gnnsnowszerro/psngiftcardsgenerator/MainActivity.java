package com.gnnsnowszerro.psngiftcardsgenerator;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.appnext.appnextsdk.API.AppnextAPI;
import com.appnext.appnextsdk.API.AppnextAd;
import com.appnext.appnextsdk.API.AppnextAdRequest;
import com.appnext.base.Appnext;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    public TextView balanceTextView;

    AppnextAPI appnextAPI;
    AppnextAd ad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        balanceTextView = (TextView) findViewById(R.id.balance);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        if (!prefs.contains("balance")) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("balance", 0);
            editor.apply();
        }

        balanceTextView.setText(String.format(Locale.getDefault(), "%d", prefs.getInt("balance", 0)));

//      Appnext banner configuration -------------------------------------------------------------------------------------------------//
        Appnext.init(this);

        appnextAPI = new AppnextAPI(this, "71e1876b-4594-4065-8d8a-9739c2e519de");
        appnextAPI.setAdListener(new AppnextAPI.AppnextAdListener() {
            @Override
            public void onAdsLoaded(ArrayList<AppnextAd> arrayList) {
                ad = arrayList.get(0);

                findViewById(R.id.banner_view).setVisibility(View.VISIBLE);
                new DownloadImageTask((ImageView) findViewById(R.id.icon)).execute(ad.getImageURL());
                ((TextView) findViewById(R.id.title)).setText(ad.getAdTitle());
                ((TextView) findViewById(R.id.rating)).setText(ad.getStoreRating());
                ((Button) findViewById(R.id.install)).setText(ad.getButtonText());

                findViewById(R.id.click).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        appnextAPI.adClicked(ad);
                    }
                });
                findViewById(R.id.privacy).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        appnextAPI.privacyClicked(ad);
                    }
                });

                appnextAPI.adImpression(ad);
            }

            @Override
            public void onError(String s) {

            }
        });
        // In this example we're loading only one ad for the banner using the setCount(1) function in the ad request
        // This is an optional usage. To load more ads either don't use the function or call it with a different value: setCount(x)
        appnextAPI.loadAds(new AppnextAdRequest().setCount(10));
// -----------------------------------------------------------------------------------------------------------------------------------------//


    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }



//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        appnextAPI.finish();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        appnextAPI.finish();
//    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0: {
                    return new ChooseCouponFragment();
                }
                case 1: {
                    return new EarnCoinsFragment();
                }
                case 2: {
                    return new InstructionsFragment();
                }
            }

            return new ChooseCouponFragment();
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "CHOOSE\nCOUPON";
                case 1:
                    return "EARN COINS";
                case 2:
                    return "INSTRUCTIONS";
            }
            return null;
        }
    }

}
