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
import com.google.android.gms.dynamic.zzn;
import com.google.android.gms.dynamic.zzo;
import com.google.android.gms.dynamic.zzr;
import com.google.android.gms.internal.fz;
import com.google.android.gms.internal.gd;
import com.google.android.gms.internal.gy;
import com.google.android.gms.wallet.MaskedWallet;
import com.google.android.gms.wallet.MaskedWalletRequest;

public final class SupportWalletFragment extends Fragment {
    private boolean mCreated = false;
    private final Fragment zzaSE = this;
    private zzb zzbPX;
    private final zzr zzbPY = zzr.zza(this);
    private final zzc zzbPZ = new zzc();
    private zza zzbQa = new zza(this);
    private WalletFragmentOptions zzbQb;
    private WalletFragmentInitParams zzbQc;
    private MaskedWalletRequest zzbQd;
    private MaskedWallet zzbQe;
    private Boolean zzbQf;

    public interface OnStateChangedListener {
        void onStateChanged(SupportWalletFragment supportWalletFragment, int i, int i2, Bundle bundle);
    }

    static class zzb implements LifecycleDelegate {
        private final fz zzbQi;

        private zzb(fz fzVar) {
            this.zzbQi = fzVar;
        }

        private final int getState() {
            try {
                return this.zzbQi.getState();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        private final void initialize(WalletFragmentInitParams walletFragmentInitParams) {
            try {
                this.zzbQi.initialize(walletFragmentInitParams);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        private final void onActivityResult(int i, int i2, Intent intent) {
            try {
                this.zzbQi.onActivityResult(i, i2, intent);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        private final void setEnabled(boolean z) {
            try {
                this.zzbQi.setEnabled(z);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        private final void updateMaskedWallet(MaskedWallet maskedWallet) {
            try {
                this.zzbQi.updateMaskedWallet(maskedWallet);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        private final void updateMaskedWalletRequest(MaskedWalletRequest maskedWalletRequest) {
            try {
                this.zzbQi.updateMaskedWalletRequest(maskedWalletRequest);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        public final void onCreate(Bundle bundle) {
            try {
                this.zzbQi.onCreate(bundle);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        public final View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
            try {
                return (View) zzn.zzE(this.zzbQi.onCreateView(zzn.zzw(layoutInflater), zzn.zzw(viewGroup), bundle));
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        public final void onDestroy() {
        }

        public final void onDestroyView() {
        }

        public final void onInflate(Activity activity, Bundle bundle, Bundle bundle2) {
            try {
                this.zzbQi.zza(zzn.zzw(activity), (WalletFragmentOptions) bundle.getParcelable("extraWalletFragmentOptions"), bundle2);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        public final void onLowMemory() {
        }

        public final void onPause() {
            try {
                this.zzbQi.onPause();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        public final void onResume() {
            try {
                this.zzbQi.onResume();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        public final void onSaveInstanceState(Bundle bundle) {
            try {
                this.zzbQi.onSaveInstanceState(bundle);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        public final void onStart() {
            try {
                this.zzbQi.onStart();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        public final void onStop() {
            try {
                this.zzbQi.onStop();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }

    class zzc extends com.google.android.gms.dynamic.zza<zzb> implements OnClickListener {
        private /* synthetic */ SupportWalletFragment zzbQj;

        private zzc(SupportWalletFragment supportWalletFragment) {
            this.zzbQj = supportWalletFragment;
        }

        public final void onClick(View view) {
            FragmentActivity activity = this.zzbQj.zzaSE.getActivity();
            GooglePlayServicesUtil.showErrorDialogFragment(GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity), activity, -1);
        }

        protected final void zza(FrameLayout frameLayout) {
            int i = -1;
            int i2 = -2;
            View button = new Button(this.zzbQj.zzaSE.getActivity());
            button.setText(R.string.wallet_buy_button_place_holder);
            if (this.zzbQj.zzbQb != null) {
                WalletFragmentStyle fragmentStyle = this.zzbQj.zzbQb.getFragmentStyle();
                if (fragmentStyle != null) {
                    DisplayMetrics displayMetrics = this.zzbQj.zzaSE.getResources().getDisplayMetrics();
                    i = fragmentStyle.zza("buyButtonWidth", displayMetrics, -1);
                    i2 = fragmentStyle.zza("buyButtonHeight", displayMetrics, -2);
                }
            }
            button.setLayoutParams(new LayoutParams(i, i2));
            button.setOnClickListener(this);
            frameLayout.addView(button);
        }

        protected final void zza(zzo<zzb> com_google_android_gms_dynamic_zzo_com_google_android_gms_wallet_fragment_SupportWalletFragment_zzb) {
            FragmentActivity activity = this.zzbQj.zzaSE.getActivity();
            if (this.zzbQj.zzbPX == null && this.zzbQj.mCreated && activity != null) {
                try {
                    this.zzbQj.zzbPX = new zzb(gy.zza(activity, this.zzbQj.zzbPY, this.zzbQj.zzbQb, this.zzbQj.zzbQa));
                    this.zzbQj.zzbQb = null;
                    com_google_android_gms_dynamic_zzo_com_google_android_gms_wallet_fragment_SupportWalletFragment_zzb.zza(this.zzbQj.zzbPX);
                    if (this.zzbQj.zzbQc != null) {
                        this.zzbQj.zzbPX.initialize(this.zzbQj.zzbQc);
                        this.zzbQj.zzbQc = null;
                    }
                    if (this.zzbQj.zzbQd != null) {
                        this.zzbQj.zzbPX.updateMaskedWalletRequest(this.zzbQj.zzbQd);
                        this.zzbQj.zzbQd = null;
                    }
                    if (this.zzbQj.zzbQe != null) {
                        this.zzbQj.zzbPX.updateMaskedWallet(this.zzbQj.zzbQe);
                        this.zzbQj.zzbQe = null;
                    }
                    if (this.zzbQj.zzbQf != null) {
                        this.zzbQj.zzbPX.setEnabled(this.zzbQj.zzbQf.booleanValue());
                        this.zzbQj.zzbQf = null;
                    }
                } catch (GooglePlayServicesNotAvailableException e) {
                }
            }
        }
    }

    static class zza extends gd {
        private OnStateChangedListener zzbQg;
        private final SupportWalletFragment zzbQh;

        zza(SupportWalletFragment supportWalletFragment) {
            this.zzbQh = supportWalletFragment;
        }

        public final void zza(int i, int i2, Bundle bundle) {
            if (this.zzbQg != null) {
                this.zzbQg.onStateChanged(this.zzbQh, i, i2, bundle);
            }
        }

        public final void zza(OnStateChangedListener onStateChangedListener) {
            this.zzbQg = onStateChangedListener;
        }
    }

    public static SupportWalletFragment newInstance(WalletFragmentOptions walletFragmentOptions) {
        SupportWalletFragment supportWalletFragment = new SupportWalletFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("extraWalletFragmentOptions", walletFragmentOptions);
        supportWalletFragment.zzaSE.setArguments(bundle);
        return supportWalletFragment;
    }

    public final int getState() {
        return this.zzbPX != null ? this.zzbPX.getState() : 0;
    }

    public final void initialize(WalletFragmentInitParams walletFragmentInitParams) {
        if (this.zzbPX != null) {
            this.zzbPX.initialize(walletFragmentInitParams);
            this.zzbQc = null;
        } else if (this.zzbQc == null) {
            this.zzbQc = walletFragmentInitParams;
            if (this.zzbQd != null) {
                Log.w("SupportWalletFragment", "updateMaskedWalletRequest() was called before initialize()");
            }
            if (this.zzbQe != null) {
                Log.w("SupportWalletFragment", "updateMaskedWallet() was called before initialize()");
            }
        } else {
            Log.w("SupportWalletFragment", "initialize(WalletFragmentInitParams) was called more than once. Ignoring.");
        }
    }

    public final void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (this.zzbPX != null) {
            this.zzbPX.onActivityResult(i, i2, intent);
        }
    }

    public final void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (bundle != null) {
            bundle.setClassLoader(WalletFragmentOptions.class.getClassLoader());
            WalletFragmentInitParams walletFragmentInitParams = (WalletFragmentInitParams) bundle.getParcelable("walletFragmentInitParams");
            if (walletFragmentInitParams != null) {
                if (this.zzbQc != null) {
                    Log.w("SupportWalletFragment", "initialize(WalletFragmentInitParams) was called more than once.Ignoring.");
                }
                this.zzbQc = walletFragmentInitParams;
            }
            if (this.zzbQd == null) {
                this.zzbQd = (MaskedWalletRequest) bundle.getParcelable("maskedWalletRequest");
            }
            if (this.zzbQe == null) {
                this.zzbQe = (MaskedWallet) bundle.getParcelable("maskedWallet");
            }
            if (bundle.containsKey("walletFragmentOptions")) {
                this.zzbQb = (WalletFragmentOptions) bundle.getParcelable("walletFragmentOptions");
            }
            if (bundle.containsKey("enabled")) {
                this.zzbQf = Boolean.valueOf(bundle.getBoolean("enabled"));
            }
        } else if (this.zzaSE.getArguments() != null) {
            WalletFragmentOptions walletFragmentOptions = (WalletFragmentOptions) this.zzaSE.getArguments().getParcelable("extraWalletFragmentOptions");
            if (walletFragmentOptions != null) {
                walletFragmentOptions.zzby(this.zzaSE.getActivity());
                this.zzbQb = walletFragmentOptions;
            }
        }
        this.mCreated = true;
        this.zzbPZ.onCreate(bundle);
    }

    public final View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return this.zzbPZ.onCreateView(layoutInflater, viewGroup, bundle);
    }

    public final void onDestroy() {
        super.onDestroy();
        this.mCreated = false;
    }

    public final void onInflate(Activity activity, AttributeSet attributeSet, Bundle bundle) {
        super.onInflate(activity, attributeSet, bundle);
        if (this.zzbQb == null) {
            this.zzbQb = WalletFragmentOptions.zza((Context) activity, attributeSet);
        }
        Bundle bundle2 = new Bundle();
        bundle2.putParcelable("attrKeyWalletFragmentOptions", this.zzbQb);
        this.zzbPZ.onInflate(activity, bundle2, bundle);
    }

    public final void onPause() {
        super.onPause();
        this.zzbPZ.onPause();
    }

    public final void onResume() {
        super.onResume();
        this.zzbPZ.onResume();
        FragmentManager supportFragmentManager = this.zzaSE.getActivity().getSupportFragmentManager();
        Fragment findFragmentByTag = supportFragmentManager.findFragmentByTag(GooglePlayServicesUtil.GMS_ERROR_DIALOG);
        if (findFragmentByTag != null) {
            supportFragmentManager.beginTransaction().remove(findFragmentByTag).commit();
            GooglePlayServicesUtil.showErrorDialogFragment(GooglePlayServicesUtil.isGooglePlayServicesAvailable(this.zzaSE.getActivity()), this.zzaSE.getActivity(), -1);
        }
    }

    public final void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.setClassLoader(WalletFragmentOptions.class.getClassLoader());
        this.zzbPZ.onSaveInstanceState(bundle);
        if (this.zzbQc != null) {
            bundle.putParcelable("walletFragmentInitParams", this.zzbQc);
            this.zzbQc = null;
        }
        if (this.zzbQd != null) {
            bundle.putParcelable("maskedWalletRequest", this.zzbQd);
            this.zzbQd = null;
        }
        if (this.zzbQe != null) {
            bundle.putParcelable("maskedWallet", this.zzbQe);
            this.zzbQe = null;
        }
        if (this.zzbQb != null) {
            bundle.putParcelable("walletFragmentOptions", this.zzbQb);
            this.zzbQb = null;
        }
        if (this.zzbQf != null) {
            bundle.putBoolean("enabled", this.zzbQf.booleanValue());
            this.zzbQf = null;
        }
    }

    public final void onStart() {
        super.onStart();
        this.zzbPZ.onStart();
    }

    public final void onStop() {
        super.onStop();
        this.zzbPZ.onStop();
    }

    public final void setEnabled(boolean z) {
        if (this.zzbPX != null) {
            this.zzbPX.setEnabled(z);
            this.zzbQf = null;
            return;
        }
        this.zzbQf = Boolean.valueOf(z);
    }

    public final void setOnStateChangedListener(OnStateChangedListener onStateChangedListener) {
        this.zzbQa.zza(onStateChangedListener);
    }

    public final void updateMaskedWallet(MaskedWallet maskedWallet) {
        if (this.zzbPX != null) {
            this.zzbPX.updateMaskedWallet(maskedWallet);
            this.zzbQe = null;
            return;
        }
        this.zzbQe = maskedWallet;
    }

    public final void updateMaskedWalletRequest(MaskedWalletRequest maskedWalletRequest) {
        if (this.zzbPX != null) {
            this.zzbPX.updateMaskedWalletRequest(maskedWalletRequest);
            this.zzbQd = null;
            return;
        }
        this.zzbQd = maskedWalletRequest;
    }
}
