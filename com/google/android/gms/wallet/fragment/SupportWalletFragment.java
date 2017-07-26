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
import com.google.android.gms.internal.ga;
import com.google.android.gms.internal.ge;
import com.google.android.gms.internal.gz;
import com.google.android.gms.wallet.MaskedWallet;
import com.google.android.gms.wallet.MaskedWalletRequest;

public final class SupportWalletFragment extends Fragment {
    private boolean mCreated = false;
    private final Fragment zzaSE = this;
    private zzb zzbPZ;
    private final zzr zzbQa = zzr.zza(this);
    private final zzc zzbQb = new zzc();
    private zza zzbQc = new zza(this);
    private WalletFragmentOptions zzbQd;
    private WalletFragmentInitParams zzbQe;
    private MaskedWalletRequest zzbQf;
    private MaskedWallet zzbQg;
    private Boolean zzbQh;

    public interface OnStateChangedListener {
        void onStateChanged(SupportWalletFragment supportWalletFragment, int i, int i2, Bundle bundle);
    }

    static class zzb implements LifecycleDelegate {
        private final ga zzbQk;

        private zzb(ga gaVar) {
            this.zzbQk = gaVar;
        }

        private final int getState() {
            try {
                return this.zzbQk.getState();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        private final void initialize(WalletFragmentInitParams walletFragmentInitParams) {
            try {
                this.zzbQk.initialize(walletFragmentInitParams);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        private final void onActivityResult(int i, int i2, Intent intent) {
            try {
                this.zzbQk.onActivityResult(i, i2, intent);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        private final void setEnabled(boolean z) {
            try {
                this.zzbQk.setEnabled(z);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        private final void updateMaskedWallet(MaskedWallet maskedWallet) {
            try {
                this.zzbQk.updateMaskedWallet(maskedWallet);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        private final void updateMaskedWalletRequest(MaskedWalletRequest maskedWalletRequest) {
            try {
                this.zzbQk.updateMaskedWalletRequest(maskedWalletRequest);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        public final void onCreate(Bundle bundle) {
            try {
                this.zzbQk.onCreate(bundle);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        public final View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
            try {
                return (View) zzn.zzE(this.zzbQk.onCreateView(zzn.zzw(layoutInflater), zzn.zzw(viewGroup), bundle));
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
                this.zzbQk.zza(zzn.zzw(activity), (WalletFragmentOptions) bundle.getParcelable("extraWalletFragmentOptions"), bundle2);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        public final void onLowMemory() {
        }

        public final void onPause() {
            try {
                this.zzbQk.onPause();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        public final void onResume() {
            try {
                this.zzbQk.onResume();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        public final void onSaveInstanceState(Bundle bundle) {
            try {
                this.zzbQk.onSaveInstanceState(bundle);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        public final void onStart() {
            try {
                this.zzbQk.onStart();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        public final void onStop() {
            try {
                this.zzbQk.onStop();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }

    class zzc extends com.google.android.gms.dynamic.zza<zzb> implements OnClickListener {
        private /* synthetic */ SupportWalletFragment zzbQl;

        private zzc(SupportWalletFragment supportWalletFragment) {
            this.zzbQl = supportWalletFragment;
        }

        public final void onClick(View view) {
            FragmentActivity activity = this.zzbQl.zzaSE.getActivity();
            GooglePlayServicesUtil.showErrorDialogFragment(GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity), activity, -1);
        }

        protected final void zza(FrameLayout frameLayout) {
            int i = -1;
            int i2 = -2;
            View button = new Button(this.zzbQl.zzaSE.getActivity());
            button.setText(R.string.wallet_buy_button_place_holder);
            if (this.zzbQl.zzbQd != null) {
                WalletFragmentStyle fragmentStyle = this.zzbQl.zzbQd.getFragmentStyle();
                if (fragmentStyle != null) {
                    DisplayMetrics displayMetrics = this.zzbQl.zzaSE.getResources().getDisplayMetrics();
                    i = fragmentStyle.zza("buyButtonWidth", displayMetrics, -1);
                    i2 = fragmentStyle.zza("buyButtonHeight", displayMetrics, -2);
                }
            }
            button.setLayoutParams(new LayoutParams(i, i2));
            button.setOnClickListener(this);
            frameLayout.addView(button);
        }

        protected final void zza(zzo<zzb> com_google_android_gms_dynamic_zzo_com_google_android_gms_wallet_fragment_SupportWalletFragment_zzb) {
            FragmentActivity activity = this.zzbQl.zzaSE.getActivity();
            if (this.zzbQl.zzbPZ == null && this.zzbQl.mCreated && activity != null) {
                try {
                    this.zzbQl.zzbPZ = new zzb(gz.zza(activity, this.zzbQl.zzbQa, this.zzbQl.zzbQd, this.zzbQl.zzbQc));
                    this.zzbQl.zzbQd = null;
                    com_google_android_gms_dynamic_zzo_com_google_android_gms_wallet_fragment_SupportWalletFragment_zzb.zza(this.zzbQl.zzbPZ);
                    if (this.zzbQl.zzbQe != null) {
                        this.zzbQl.zzbPZ.initialize(this.zzbQl.zzbQe);
                        this.zzbQl.zzbQe = null;
                    }
                    if (this.zzbQl.zzbQf != null) {
                        this.zzbQl.zzbPZ.updateMaskedWalletRequest(this.zzbQl.zzbQf);
                        this.zzbQl.zzbQf = null;
                    }
                    if (this.zzbQl.zzbQg != null) {
                        this.zzbQl.zzbPZ.updateMaskedWallet(this.zzbQl.zzbQg);
                        this.zzbQl.zzbQg = null;
                    }
                    if (this.zzbQl.zzbQh != null) {
                        this.zzbQl.zzbPZ.setEnabled(this.zzbQl.zzbQh.booleanValue());
                        this.zzbQl.zzbQh = null;
                    }
                } catch (GooglePlayServicesNotAvailableException e) {
                }
            }
        }
    }

    static class zza extends ge {
        private OnStateChangedListener zzbQi;
        private final SupportWalletFragment zzbQj;

        zza(SupportWalletFragment supportWalletFragment) {
            this.zzbQj = supportWalletFragment;
        }

        public final void zza(int i, int i2, Bundle bundle) {
            if (this.zzbQi != null) {
                this.zzbQi.onStateChanged(this.zzbQj, i, i2, bundle);
            }
        }

        public final void zza(OnStateChangedListener onStateChangedListener) {
            this.zzbQi = onStateChangedListener;
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
        return this.zzbPZ != null ? this.zzbPZ.getState() : 0;
    }

    public final void initialize(WalletFragmentInitParams walletFragmentInitParams) {
        if (this.zzbPZ != null) {
            this.zzbPZ.initialize(walletFragmentInitParams);
            this.zzbQe = null;
        } else if (this.zzbQe == null) {
            this.zzbQe = walletFragmentInitParams;
            if (this.zzbQf != null) {
                Log.w("SupportWalletFragment", "updateMaskedWalletRequest() was called before initialize()");
            }
            if (this.zzbQg != null) {
                Log.w("SupportWalletFragment", "updateMaskedWallet() was called before initialize()");
            }
        } else {
            Log.w("SupportWalletFragment", "initialize(WalletFragmentInitParams) was called more than once. Ignoring.");
        }
    }

    public final void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (this.zzbPZ != null) {
            this.zzbPZ.onActivityResult(i, i2, intent);
        }
    }

    public final void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (bundle != null) {
            bundle.setClassLoader(WalletFragmentOptions.class.getClassLoader());
            WalletFragmentInitParams walletFragmentInitParams = (WalletFragmentInitParams) bundle.getParcelable("walletFragmentInitParams");
            if (walletFragmentInitParams != null) {
                if (this.zzbQe != null) {
                    Log.w("SupportWalletFragment", "initialize(WalletFragmentInitParams) was called more than once.Ignoring.");
                }
                this.zzbQe = walletFragmentInitParams;
            }
            if (this.zzbQf == null) {
                this.zzbQf = (MaskedWalletRequest) bundle.getParcelable("maskedWalletRequest");
            }
            if (this.zzbQg == null) {
                this.zzbQg = (MaskedWallet) bundle.getParcelable("maskedWallet");
            }
            if (bundle.containsKey("walletFragmentOptions")) {
                this.zzbQd = (WalletFragmentOptions) bundle.getParcelable("walletFragmentOptions");
            }
            if (bundle.containsKey("enabled")) {
                this.zzbQh = Boolean.valueOf(bundle.getBoolean("enabled"));
            }
        } else if (this.zzaSE.getArguments() != null) {
            WalletFragmentOptions walletFragmentOptions = (WalletFragmentOptions) this.zzaSE.getArguments().getParcelable("extraWalletFragmentOptions");
            if (walletFragmentOptions != null) {
                walletFragmentOptions.zzby(this.zzaSE.getActivity());
                this.zzbQd = walletFragmentOptions;
            }
        }
        this.mCreated = true;
        this.zzbQb.onCreate(bundle);
    }

    public final View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return this.zzbQb.onCreateView(layoutInflater, viewGroup, bundle);
    }

    public final void onDestroy() {
        super.onDestroy();
        this.mCreated = false;
    }

    public final void onInflate(Activity activity, AttributeSet attributeSet, Bundle bundle) {
        super.onInflate(activity, attributeSet, bundle);
        if (this.zzbQd == null) {
            this.zzbQd = WalletFragmentOptions.zza((Context) activity, attributeSet);
        }
        Bundle bundle2 = new Bundle();
        bundle2.putParcelable("attrKeyWalletFragmentOptions", this.zzbQd);
        this.zzbQb.onInflate(activity, bundle2, bundle);
    }

    public final void onPause() {
        super.onPause();
        this.zzbQb.onPause();
    }

    public final void onResume() {
        super.onResume();
        this.zzbQb.onResume();
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
        this.zzbQb.onSaveInstanceState(bundle);
        if (this.zzbQe != null) {
            bundle.putParcelable("walletFragmentInitParams", this.zzbQe);
            this.zzbQe = null;
        }
        if (this.zzbQf != null) {
            bundle.putParcelable("maskedWalletRequest", this.zzbQf);
            this.zzbQf = null;
        }
        if (this.zzbQg != null) {
            bundle.putParcelable("maskedWallet", this.zzbQg);
            this.zzbQg = null;
        }
        if (this.zzbQd != null) {
            bundle.putParcelable("walletFragmentOptions", this.zzbQd);
            this.zzbQd = null;
        }
        if (this.zzbQh != null) {
            bundle.putBoolean("enabled", this.zzbQh.booleanValue());
            this.zzbQh = null;
        }
    }

    public final void onStart() {
        super.onStart();
        this.zzbQb.onStart();
    }

    public final void onStop() {
        super.onStop();
        this.zzbQb.onStop();
    }

    public final void setEnabled(boolean z) {
        if (this.zzbPZ != null) {
            this.zzbPZ.setEnabled(z);
            this.zzbQh = null;
            return;
        }
        this.zzbQh = Boolean.valueOf(z);
    }

    public final void setOnStateChangedListener(OnStateChangedListener onStateChangedListener) {
        this.zzbQc.zza(onStateChangedListener);
    }

    public final void updateMaskedWallet(MaskedWallet maskedWallet) {
        if (this.zzbPZ != null) {
            this.zzbPZ.updateMaskedWallet(maskedWallet);
            this.zzbQg = null;
            return;
        }
        this.zzbQg = maskedWallet;
    }

    public final void updateMaskedWalletRequest(MaskedWalletRequest maskedWalletRequest) {
        if (this.zzbPZ != null) {
            this.zzbPZ.updateMaskedWalletRequest(maskedWalletRequest);
            this.zzbQf = null;
            return;
        }
        this.zzbQf = maskedWalletRequest;
    }
}
