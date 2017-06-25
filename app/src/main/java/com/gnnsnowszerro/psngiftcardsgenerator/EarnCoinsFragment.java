package com.gnnsnowszerro.psngiftcardsgenerator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adcolony.sdk.AdColony;
import com.adcolony.sdk.AdColonyInterstitial;
import com.adcolony.sdk.AdColonyInterstitialListener;
import com.adcolony.sdk.AdColonyZone;
import com.nativex.monetization.MonetizationManager;
import com.nativex.monetization.business.reward.Reward;
import com.nativex.monetization.communication.RedeemRewardData;
import com.nativex.monetization.enums.AdEvent;
import com.nativex.monetization.listeners.OnAdEventV2;
import com.nativex.monetization.listeners.RewardListener;
import com.nativex.monetization.listeners.SessionListener;
import com.nativex.monetization.mraid.AdInfo;
import com.offertoro.sdk.OTOfferWallSettings;
import com.offertoro.sdk.interfaces.OfferWallListener;
import com.offertoro.sdk.sdk.OffersInit;

import net.adxmi.android.AdManager;
import net.adxmi.android.os.EarnPointsOrderInfo;
import net.adxmi.android.os.EarnPointsOrderList;
import net.adxmi.android.os.OffersManager;
import net.adxmi.android.os.PointsChangeNotify;
import net.adxmi.android.os.PointsEarnNotify;
import net.adxmi.android.os.PointsManager;

import java.util.Locale;

/**
 * Created by Alexandr on 16/06/2017.
 */

public class EarnCoinsFragment extends Fragment implements OfferWallListener, PointsChangeNotify, PointsEarnNotify {

    public static final String TAG = EarnCoinsFragment.class.getSimpleName();

    private final static int REQUEST_READ_PHONE_STATE = 1;

    public static final String OFFER_TORRO_SECRET_KEY = "e04ac7cb5cba11fbed15a53cab99e952";
    public static final String OFFER_TORRO_APP_ID = "2632";
    public static final String OFFER_TORRO_USER_ID = "4746";

    public static final String NATIVEX_APP_ID = "120243";
    public static final String NATIVEX_PLACEMENT_NAME = "Main Menu Screen";

    public static final String ADXMI_APP_ID = "01bc87913a968e80";
    public static final String ADXMI_APP_SECRET = "19edf67159c7e389";

    public static final String ADCOLONY_APP_ID = "appa567471ee29646b5b5";
    public static final String ADCOLONY_TIME_ZONE = "vzac61b40e83e8436c9e";

    private SharedPreferences prefs;
    private SharedPreferences.Editor prefsEditor;

    private TextView balanceView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_earn_coins, container, false);

        balanceView = ((TextView) getActivity().findViewById(R.id.balance));

        FrameLayout quickOffers = (FrameLayout) rootView.findViewById(R.id.quick_offers);
        FrameLayout proTasks = (FrameLayout) rootView.findViewById(R.id.pro_tasks);
        FrameLayout dailyOffers = (FrameLayout) rootView.findViewById(R.id.daily_offers);
        FrameLayout watchVideo = (FrameLayout) rootView.findViewById(R.id.watch_video);
        FrameLayout rateUs = (FrameLayout) rootView.findViewById(R.id.rate_us);

        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        prefsEditor = prefs.edit();
        prefsEditor.apply();



//      ADXMI Configuring ------------------------------------------------------------------------//
        AdManager.getInstance(getContext()).init(ADXMI_APP_ID, ADXMI_APP_SECRET);
        initOfferWall();
        PointsManager.getInstance(getContext()).registerNotify(new PointsChangeNotify() {
            @Override
            public void onPointBalanceChange(int pointBalance) {
                addReward(pointBalance);
                balanceView.setText(String.format(Locale.getDefault(), "%d", getTotalBalanceAmount()));
            }
        });
        checkReadPhoneStatePermission();

// -----------------------------------------------------------------------------------------------//


//      AdColony Configuring ---------------------------------------------------------------------//
        AdColony.configure(getActivity(), ADCOLONY_APP_ID, ADCOLONY_TIME_ZONE);

        final AdColonyInterstitialListener listener = new AdColonyInterstitialListener() {
            @Override
            public void onRequestFilled(AdColonyInterstitial ad) {
                ad.show();
            }

            @Override
            public void onClosed(AdColonyInterstitial ad) {
                addReward(10);
                Toast.makeText(getContext(), "Added 10 Tokens", Toast.LENGTH_SHORT).show();
                balanceView.setText(String.format(Locale.getDefault(), "%d", getTotalBalanceAmount()));
            }

            @Override
            public void onRequestNotFilled(AdColonyZone zone) {
                Toast.makeText(getContext(), "Sorry, We don't have video for you.", Toast.LENGTH_SHORT).show();
            }
        };
// ---------------------------------------------------------------------------------------------- //


//      Nativex Configuring ----------------------------------------------------------------------//
        MonetizationManager.setRewardListener(rewardListener);
// ---------------------------------------------------------------------------------------------- //

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.quick_offers: {
                        OTOfferWallSettings.getInstance().
                                configInit(OFFER_TORRO_APP_ID, OFFER_TORRO_SECRET_KEY, OFFER_TORRO_USER_ID);

                        OffersInit.getInstance().setOfferWallListener(EarnCoinsFragment.this);
                        OffersInit.getInstance().showOfferWall(getActivity());
                        break;
                    }
                    case R.id.pro_tasks: {
                        MonetizationManager.createSession(getActivity(), NATIVEX_APP_ID, sessionListener);
                        MonetizationManager.showReadyAd(getActivity(),
                                NATIVEX_PLACEMENT_NAME, onAdEventListener);

                        MonetizationManager.showAd(getActivity(), NATIVEX_PLACEMENT_NAME);
                        break;
                    }
                    case R.id.daily_offers: {
                        if (hasGetReadPhoneStatePermission()) {
                            OffersManager.getInstance(getContext()).showOffersWall();
                        } else {
                            requestReadPhoneStatePermission();
                        }
                        break;
                    }
                    case R.id.watch_video: {
                        AdColony.requestInterstitial(ADCOLONY_TIME_ZONE, listener);
                        break;
                    }
                    case R.id.rate_us: {
                        final String appPackageName = getContext().getPackageName();
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                        break;
                    }
                }
            }
        };


        quickOffers.setOnClickListener(clickListener);
        proTasks.setOnClickListener(clickListener);
        dailyOffers.setOnClickListener(clickListener);
        watchVideo.setOnClickListener(clickListener);
        rateUs.setOnClickListener(clickListener);

        return rootView;
    }


    // ----- OTOfferWall Listeners ------------------------------------------------------------------ //
    @Override
    public void onOTOfferWallInitSuccess() {
        Log.e(TAG, "onOTOfferWallInitSuccess: ");
    }

    @Override
    public void onOTOfferWallInitFail(String s) {

    }

    @Override
    public void onOTOfferWallOpened() {
        Log.e(TAG, "onOTOfferWallOpened: ");
    }

    @Override
    public void onOTOfferWallCredited(double credits, double totalCredits) {
        Log.v(TAG, "onOTOfferWallCredited: credits = " + credits + " totalCredits = " + totalCredits);

        addReward((int) credits);
        balanceView.setText(String.format(Locale.getDefault(), "%d", getTotalBalanceAmount()));
    }

    @Override
    public void onOTOfferWallClosed() {
        Log.e(TAG, "onOTOfferWallClosed: ");

    }
// ---------------------------------------------------------------------------------------------- //


    //  ----- ADXMI --------------------------------------------------------------------------------- //
    private void checkReadPhoneStatePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (!hasGetReadPhoneStatePermission()) {
                requestReadPhoneStatePermission();
            }
        }
    }

    private boolean hasGetReadPhoneStatePermission() {
        return ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestReadPhoneStatePermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
    }

    private void initOfferWall() {
        // If using rewarded OfferWall advertisement, remember to invoke the initialization of rewarded offerwall advertisement:
        OffersManager.getInstance(getContext()).onAppLaunch();
        // (Optional) Register listener for OfferWall currency points, to get notification of currency points changing
        PointsManager.getInstance(getContext()).registerNotify(this);
        // (Optional) Register listener for earning OfferWall currency points
        PointsManager.getInstance(getContext()).registerPointsEarnNotify(this);
    }


    @Override
    public void onPointBalanceChange(int i) {

    }

    @Override
    public void onPointEarn(Context context, EarnPointsOrderList list) {
        for (int i = 0; i < list.size(); ++i) {
            EarnPointsOrderInfo info = list.get(i);
            Log.i("Adxmi", info.getMessage());
        }
    }

//  --------------------------------------------------------------------------------------------- //


    @Override
    public void onDestroy() {
        super.onDestroy();
        OffersManager.getInstance(getContext()).onAppExit();
    }


    //  ----- NATIVEX ------------------------------------------------------------------------------- //
    private RewardListener rewardListener = new RewardListener() {
        @Override
        public void onRedeem(RedeemRewardData data) {
            for (Reward reward : data.getRewards()) {
                Log.d("Sample", "Reward: rewardName:" + reward.getRewardName()
                        + " rewardId:" + reward.getRewardId()
                        + " amount:" + Double.toString(reward.getAmount()));
                // add the reward amount to the total
                addReward((int) reward.getAmount());
                balanceView.setText(String.format(Locale.getDefault(), "%d", getTotalBalanceAmount()));
            }
        }
    };

    private OnAdEventV2 onAdEventListener = new OnAdEventV2() {
        @Override
        public void onEvent(AdEvent event, AdInfo adInfo, String message) {
            System.out.println("Placement: " + adInfo.getPlacement());
            switch (event) {
                case ALREADY_FETCHED:
                    Log.d(TAG, "onEvent: ALREADY_FETCHED");
                    break;
                case ALREADY_SHOWN:
                    Log.d(TAG, "onEvent: ALREADY_SHOWN");
                    break;
                case BEFORE_DISPLAY:
                    Log.d(TAG, "onEvent: BEFORE_DISPLAY");
                    break;
                case DISMISSED:
                    Log.d(TAG, "onEvent: DISMISSED");
                    break;
                case DISPLAYED:
                    Log.d(TAG, "onEvent: DISPLAYED");
                    break;
                case DOWNLOADING:
                    Log.d(TAG, "onEvent: DOWNLOADING");
                    break;
                case ERROR:
                    Log.d(TAG, "onEvent: ERROR");
                    break;
                case EXPIRED:
                    Log.d(TAG, "onEvent: EXPIRED");
                    break;
                case FETCHED:
                    Log.d(TAG, "onEvent: FETCHED");
                    break;
                case NO_AD:
                    Toast.makeText(getContext(), "Sorry, we don't have ad for you", Toast.LENGTH_SHORT).show();
                    break;
                case USER_NAVIGATES_OUT_OF_APP:
                    Log.d(TAG, "onEvent: USER_NAVIGATES_OUT_OF_APP");
                    break;
                default:
                    break;
            }
        }
    };

    private SessionListener sessionListener = new SessionListener() {
        @Override
        public void createSessionCompleted(boolean success, boolean isOfferWallEnabled, String sessionId) {
            if (success) {
                MonetizationManager.fetchAd(getActivity(), NATIVEX_PLACEMENT_NAME, onAdEventListener);
            }
        }
    };
//    --------------------------------------------------------------------------------------------//


    private void addReward(int amount) {
        int totalAmount = prefs.getInt("balance", 0) + amount;
        prefsEditor.putInt("balance", totalAmount);
        prefsEditor.apply();
    }

    private int getTotalBalanceAmount() {
        return prefs.getInt("balance", 0);
    }


}
