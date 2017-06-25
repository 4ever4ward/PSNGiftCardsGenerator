package com.gnnsnowszerro.psngiftcardsgenerator;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appnext.appnextsdk.API.AppnextAPI;
import com.appnext.appnextsdk.API.AppnextAd;
import com.appnext.appnextsdk.API.AppnextAdRequest;
import com.appnext.base.Appnext;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GenerateActivity extends AppCompatActivity {
    AppnextAPI appnextAPI;
    AppnextAd ad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate);

        TextView couponText = (TextView) findViewById(R.id.coupon_text);
        final TextView couponCost = (TextView) findViewById(R.id.coupon_cost);
        final TextView balanceText = (TextView) findViewById(R.id.balance);
        ImageView couponImage = (ImageView) findViewById(R.id.coupon_image);
        final EditText emailAdress = (EditText) findViewById(R.id.email_edit_text);

        LinearLayout headerRedeemButton = (LinearLayout) findViewById(R.id.header_redeem_button);
        Button redeemButton = (Button) findViewById(R.id.redeem_button);
        Button generateButton = (Button) findViewById(R.id.generate_button);


        headerRedeemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        redeemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailAdress.getText().toString();

                if (validateEmail(email)) {
                    createAndShowDialog(getString(R.string.redeam_error_title), getString(R.string.redeam_error_text));
                }
            }
        });

        // Initialize coupon data from intent
        couponText.setText(String.format(Locale.getDefault(), "%s", getIntent().getStringExtra("text")).replace("\n", ""));
        couponCost.setText(String.format(Locale.getDefault(), "%d", getIntent().getIntExtra("cost", 0)).replace("\n", ""));
        int image_id = getIntent().getIntExtra("image_id", -1);

        if (image_id == R.drawable.ic_gift_card) {
            couponText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            couponImage.setImageResource(R.drawable.ic_gift_card_dark);

        } else if (image_id == R.drawable.ic_discount) {
            couponText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
            couponImage.setImageResource(R.drawable.ic_discount_dark);

        }

        // Initialize balance
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(GenerateActivity.this);
        balanceText.setText(String.format(Locale.getDefault(), "%d", prefs.getInt("balance", 0)));

        // OnClickListener for GENERATE button
        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailAdress.getText().toString();

                if (prefs.getInt("balance", 0) >= Integer.parseInt(couponCost.getText().toString())) {
                    if (!email.equals("")) {
                        if (validateEmail(email)) {
                            createAndShowDialog(getString(R.string.generate_excellent_title), getString(R.string.generate_excellent_text));
                        } else {
                            createAndShowDialog(getString(R.string.generate_invalid_email_title), getString(R.string.generate_invalid_email_text));
                        }
                    } else {
                        createAndShowDialog(getString(R.string.generate_no_email_title), getString(R.string.generate_no_email_text));
                    }
                } else {
                    createAndShowDialog(getString(R.string.generate_no_money_title), getString(R.string.generate_no_money_text));
                }
            }
        });

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
        // This is an optional usage. To load more ads either don't use the fucntion or call it with a different value: setCount(x)
        appnextAPI.loadAds(new AppnextAdRequest().setCount(10));

// ------------------------------------------------------------------------------------------------------------------------------------- //

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



    private void createAndShowDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(GenerateActivity.this)
                .setMessage(message)
                .setTitle(title)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        builder.create().show();
    }


//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        appnextAPI.finish();
//    }

    public boolean validateEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }

}
