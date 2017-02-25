package com.google.android.gms.wallet.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import com.google.android.gms.R;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.dynamic.LifecycleDelegate;
import com.google.android.gms.dynamic.zzd;
import com.google.android.gms.dynamic.zze;
import com.google.android.gms.dynamic.zzg;
import com.google.android.gms.internal.zzbks;
import com.google.android.gms.internal.zzbkz;
import com.google.android.gms.wallet.MaskedWallet;
import com.google.android.gms.wallet.MaskedWalletRequest;

public final class SupportWalletFragment extends Fragment {
    private boolean mCreated = false;
    private final Fragment zzaRN = this;
    private zzb zzbRR;
    private final zzg zzbRS = zzg.zza(this);
    private final zzc zzbRT = new zzc();
    private zza zzbRU = new zza(this);
    private WalletFragmentOptions zzbRV;
    private WalletFragmentInitParams zzbRW;
    private MaskedWalletRequest zzbRX;
    private MaskedWallet zzbRY;
    private Boolean zzbRZ;

    public interface OnStateChangedListener {
        void onStateChanged(SupportWalletFragment supportWalletFragment, int i, int i2, Bundle bundle);
    }

    private static class zzb implements LifecycleDelegate {
        private final zzbks zzbSc;

        private zzb(zzbks com_google_android_gms_internal_zzbks) {
            this.zzbSc = com_google_android_gms_internal_zzbks;
        }

        private int getState() {
            try {
                return this.zzbSc.getState();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        private void initialize(WalletFragmentInitParams walletFragmentInitParams) {
            try {
                this.zzbSc.initialize(walletFragmentInitParams);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        private void onActivityResult(int i, int i2, Intent intent) {
            try {
                this.zzbSc.onActivityResult(i, i2, intent);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        private void setEnabled(boolean z) {
            try {
                this.zzbSc.setEnabled(z);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        private void updateMaskedWallet(MaskedWallet maskedWallet) {
            try {
                this.zzbSc.updateMaskedWallet(maskedWallet);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        private void updateMaskedWalletRequest(MaskedWalletRequest maskedWalletRequest) {
            try {
                this.zzbSc.updateMaskedWalletRequest(maskedWalletRequest);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        public void onCreate(Bundle bundle) {
            try {
                this.zzbSc.onCreate(bundle);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
            try {
                return (View) zzd.zzF(this.zzbSc.onCreateView(zzd.zzA(layoutInflater), zzd.zzA(viewGroup), bundle));
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        public void onDestroy() {
        }

        public void onDestroyView() {
        }

        public void onInflate(Activity activity, Bundle bundle, Bundle bundle2) {
            try {
                this.zzbSc.zza(zzd.zzA(activity), (WalletFragmentOptions) bundle.getParcelable("extraWalletFragmentOptions"), bundle2);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        public void onLowMemory() {
        }

        public void onPause() {
            try {
                this.zzbSc.onPause();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        public void onResume() {
            try {
                this.zzbSc.onResume();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        public void onSaveInstanceState(Bundle bundle) {
            try {
                this.zzbSc.onSaveInstanceState(bundle);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        public void onStart() {
            try {
                this.zzbSc.onStart();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        public void onStop() {
            try {
                this.zzbSc.onStop();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }

    private class zzc extends com.google.android.gms.dynamic.zza<zzb> implements OnClickListener {
        final /* synthetic */ SupportWalletFragment zzbSd;

        private zzc(SupportWalletFragment supportWalletFragment) {
            this.zzbSd = supportWalletFragment;
        }

        public void onClick(View view) {
            FragmentActivity activity = this.zzbSd.zzaRN.getActivity();
            GooglePlayServicesUtil.showErrorDialogFragment(GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity), activity, -1);
        }

        protected void zza(FrameLayout frameLayout) {
            int i = -1;
            int i2 = -2;
            View button = new Button(this.zzbSd.zzaRN.getActivity());
            button.setText(R.string.wallet_buy_button_place_holder);
            if (this.zzbSd.zzbRV != null) {
                WalletFragmentStyle fragmentStyle = this.zzbSd.zzbRV.getFragmentStyle();
                if (fragmentStyle != null) {
                    DisplayMetrics displayMetrics = this.zzbSd.zzaRN.getResources().getDisplayMetrics();
                    i = fragmentStyle.zza("buyButtonWidth", displayMetrics, -1);
                    i2 = fragmentStyle.zza("buyButtonHeight", displayMetrics, -2);
                }
            }
            button.setLayoutParams(new LayoutParams(i, i2));
            button.setOnClickListener(this);
            frameLayout.addView(button);
        }

        protected void zza(zze<zzb> com_google_android_gms_dynamic_zze_com_google_android_gms_wallet_fragment_SupportWalletFragment_zzb) {
            FragmentActivity activity = this.zzbSd.zzaRN.getActivity();
            if (this.zzbSd.zzbRR == null && this.zzbSd.mCreated && activity != null) {
                try {
                    this.zzbSd.zzbRR = new zzb(zzbkz.zza(activity, this.zzbSd.zzbRS, this.zzbSd.zzbRV, this.zzbSd.zzbRU));
                    this.zzbSd.zzbRV = null;
                    com_google_android_gms_dynamic_zze_com_google_android_gms_wallet_fragment_SupportWalletFragment_zzb.zza(this.zzbSd.zzbRR);
                    if (this.zzbSd.zzbRW != null) {
                        this.zzbSd.zzbRR.initialize(this.zzbSd.zzbRW);
                        this.zzbSd.zzbRW = null;
                    }
                    if (this.zzbSd.zzbRX != null) {
                        this.zzbSd.zzbRR.updateMaskedWalletRequest(this.zzbSd.zzbRX);
                        this.zzbSd.zzbRX = null;
                    }
                    if (this.zzbSd.zzbRY != null) {
                        this.zzbSd.zzbRR.updateMaskedWallet(this.zzbSd.zzbRY);
                        this.zzbSd.zzbRY = null;
                    }
                    if (this.zzbSd.zzbRZ != null) {
                        this.zzbSd.zzbRR.setEnabled(this.zzbSd.zzbRZ.booleanValue());
                        this.zzbSd.zzbRZ = null;
                    }
                } catch (GooglePlayServicesNotAvailableException e) {
                }
            }
        }
    }

    static class zza extends com.google.android.gms.internal.zzbkt.zza {
        private OnStateChangedListener zzbSa;
        private final SupportWalletFragment zzbSb;

        zza(SupportWalletFragment supportWalletFragment) {
            this.zzbSb = supportWalletFragment;
        }

        public void zza(int i, int i2, Bundle bundle) {
            if (this.zzbSa != null) {
                this.zzbSa.onStateChanged(this.zzbSb, i, i2, bundle);
            }
        }

        public void zza(OnStateChangedListener onStateChangedListener) {
            this.zzbSa = onStateChangedListener;
        }
    }

    public static SupportWalletFragment newInstance(WalletFragmentOptions walletFragmentOptions) {
        SupportWalletFragment supportWalletFragment = new SupportWalletFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("extraWalletFragmentOptions", walletFragmentOptions);
        supportWalletFragment.zzaRN.setArguments(bundle);
        return supportWalletFragment;
    }

    public int getState() {
        return this.zzbRR != null ? this.zzbRR.getState() : 0;
    }

    public void initialize(WalletFragmentInitParams walletFragmentInitParams) {
        if (this.zzbRR != null) {
            this.zzbRR.initialize(walletFragmentInitParams);
            this.zzbRW = null;
        } else if (this.zzbRW == null) {
            this.zzbRW = walletFragmentInitParams;
            if (this.zzbRX != null) {
                Log.w("SupportWalletFragment", "updateMaskedWalletRequest() was called before initialize()");
            }
            if (this.zzbRY != null) {
                Log.w("SupportWalletFragment", "updateMaskedWallet() was called before initialize()");
            }
        } else {
            Log.w("SupportWalletFragment", "initialize(WalletFragmentInitParams) was called more than once. Ignoring.");
        }
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (this.zzbRR != null) {
            this.zzbRR.onActivityResult(i, i2, intent);
        }
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (bundle != null) {
            bundle.setClassLoader(WalletFragmentOptions.class.getClassLoader());
            WalletFragmentInitParams walletFragmentInitParams = (WalletFragmentInitParams) bundle.getParcelable("walletFragmentInitParams");
            if (walletFragmentInitParams != null) {
                if (this.zzbRW != null) {
                    Log.w("SupportWalletFragment", "initialize(WalletFragmentInitParams) was called more than once.Ignoring.");
                }
                this.zzbRW = walletFragmentInitParams;
            }
            if (this.zzbRX == null) {
                this.zzbRX = (MaskedWalletRequest) bundle.getParcelable("maskedWalletRequest");
            }
            if (this.zzbRY == null) {
                this.zzbRY = (MaskedWallet) bundle.getParcelable("maskedWallet");
            }
            if (bundle.containsKey("walletFragmentOptions")) {
                this.zzbRV = (WalletFragmentOptions) bundle.getParcelable("walletFragmentOptions");
            }
            if (bundle.containsKey("enabled")) {
                this.zzbRZ = Boolean.valueOf(bundle.getBoolean("enabled"));
            }
        } else if (this.zzaRN.getArguments() != null) {
            WalletFragmentOptions walletFragmentOptions = (WalletFragmentOptions) this.zzaRN.getArguments().getParcelable("extraWalletFragmentOptions");
            if (walletFragmentOptions != null) {
                walletFragmentOptions.zzci(this.zzaRN.getActivity());
                this.zzbRV = walletFragmentOptions;
            }
        }
        this.mCreated = true;
        this.zzbRT.onCreate(bundle);
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return this.zzbRT.onCreateView(layoutInflater, viewGroup, bundle);
    }

    public void onDestroy() {
        super.onDestroy();
        this.mCreated = false;
    }

    public void onInflate(Activity activity, AttributeSet attributeSet, Bundle bundle) {
        super.onInflate(activity, attributeSet, bundle);
        if (this.zzbRV == null) {
            this.zzbRV = WalletFragmentOptions.zzc((Context) activity, attributeSet);
        }
        Bundle bundle2 = new Bundle();
        bundle2.putParcelable("attrKeyWalletFragmentOptions", this.zzbRV);
        this.zzbRT.onInflate(activity, bundle2, bundle);
    }

    public void onPause() {
        super.onPause();
        this.zzbRT.onPause();
    }

    public void onResume() {
        super.onResume();
        this.zzbRT.onResume();
        FragmentManager supportFragmentManager = this.zzaRN.getActivity().getSupportFragmentManager();
        Fragment findFragmentByTag = supportFragmentManager.findFragmentByTag(GooglePlayServicesUtil.GMS_ERROR_DIALOG);
        if (findFragmentByTag != null) {
            supportFragmentManager.beginTransaction().remove(findFragmentByTag).commit();
            GooglePlayServicesUtil.showErrorDialogFragment(GooglePlayServicesUtil.isGooglePlayServicesAvailable(this.zzaRN.getActivity()), this.zzaRN.getActivity(), -1);
        }
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.setClassLoader(WalletFragmentOptions.class.getClassLoader());
        this.zzbRT.onSaveInstanceState(bundle);
        if (this.zzbRW != null) {
            bundle.putParcelable("walletFragmentInitParams", this.zzbRW);
            this.zzbRW = null;
        }
        if (this.zzbRX != null) {
            bundle.putParcelable("maskedWalletRequest", this.zzbRX);
            this.zzbRX = null;
        }
        if (this.zzbRY != null) {
            bundle.putParcelable("maskedWallet", this.zzbRY);
            this.zzbRY = null;
        }
        if (this.zzbRV != null) {
            bundle.putParcelable("walletFragmentOptions", this.zzbRV);
            this.zzbRV = null;
        }
        if (this.zzbRZ != null) {
            bundle.putBoolean("enabled", this.zzbRZ.booleanValue());
            this.zzbRZ = null;
        }
    }

    public void onStart() {
        super.onStart();
        this.zzbRT.onStart();
    }

    public void onStop() {
        super.onStop();
        this.zzbRT.onStop();
    }

    public void setEnabled(boolean z) {
        if (this.zzbRR != null) {
            this.zzbRR.setEnabled(z);
            this.zzbRZ = null;
            return;
        }
        this.zzbRZ = Boolean.valueOf(z);
    }

    public void setOnStateChangedListener(OnStateChangedListener onStateChangedListener) {
        this.zzbRU.zza(onStateChangedListener);
    }

    public void updateMaskedWallet(MaskedWallet maskedWallet) {
        if (this.zzbRR != null) {
            this.zzbRR.updateMaskedWallet(maskedWallet);
            this.zzbRY = null;
            return;
        }
        this.zzbRY = maskedWallet;
    }

    public void updateMaskedWalletRequest(MaskedWalletRequest maskedWalletRequest) {
        if (this.zzbRR != null) {
            this.zzbRR.updateMaskedWalletRequest(maskedWalletRequest);
            this.zzbRX = null;
            return;
        }
        this.zzbRX = maskedWalletRequest;
    }
}
