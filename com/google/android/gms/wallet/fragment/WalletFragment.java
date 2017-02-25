package com.google.android.gms.wallet.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.google.android.gms.internal.zzbks;
import com.google.android.gms.internal.zzbkz;
import com.google.android.gms.wallet.MaskedWallet;
import com.google.android.gms.wallet.MaskedWalletRequest;

@TargetApi(12)
public final class WalletFragment extends Fragment {
    private boolean mCreated = false;
    private final Fragment zzaRK = this;
    private WalletFragmentOptions zzbRV;
    private WalletFragmentInitParams zzbRW;
    private MaskedWalletRequest zzbRX;
    private MaskedWallet zzbRY;
    private Boolean zzbRZ;
    private zzb zzbSe;
    private final com.google.android.gms.dynamic.zzb zzbSf = com.google.android.gms.dynamic.zzb.zza(this);
    private final zzc zzbSg = new zzc();
    private zza zzbSh = new zza(this);

    public interface OnStateChangedListener {
        void onStateChanged(WalletFragment walletFragment, int i, int i2, Bundle bundle);
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
        final /* synthetic */ WalletFragment zzbSk;

        private zzc(WalletFragment walletFragment) {
            this.zzbSk = walletFragment;
        }

        public void onClick(View view) {
            Context activity = this.zzbSk.zzaRK.getActivity();
            GooglePlayServicesUtil.showErrorDialogFragment(GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity), activity, -1);
        }

        protected void zza(FrameLayout frameLayout) {
            int i = -1;
            int i2 = -2;
            View button = new Button(this.zzbSk.zzaRK.getActivity());
            button.setText(R.string.wallet_buy_button_place_holder);
            if (this.zzbSk.zzbRV != null) {
                WalletFragmentStyle fragmentStyle = this.zzbSk.zzbRV.getFragmentStyle();
                if (fragmentStyle != null) {
                    DisplayMetrics displayMetrics = this.zzbSk.zzaRK.getResources().getDisplayMetrics();
                    i = fragmentStyle.zza("buyButtonWidth", displayMetrics, -1);
                    i2 = fragmentStyle.zza("buyButtonHeight", displayMetrics, -2);
                }
            }
            button.setLayoutParams(new LayoutParams(i, i2));
            button.setOnClickListener(this);
            frameLayout.addView(button);
        }

        protected void zza(zze<zzb> com_google_android_gms_dynamic_zze_com_google_android_gms_wallet_fragment_WalletFragment_zzb) {
            Activity activity = this.zzbSk.zzaRK.getActivity();
            if (this.zzbSk.zzbSe == null && this.zzbSk.mCreated && activity != null) {
                try {
                    this.zzbSk.zzbSe = new zzb(zzbkz.zza(activity, this.zzbSk.zzbSf, this.zzbSk.zzbRV, this.zzbSk.zzbSh));
                    this.zzbSk.zzbRV = null;
                    com_google_android_gms_dynamic_zze_com_google_android_gms_wallet_fragment_WalletFragment_zzb.zza(this.zzbSk.zzbSe);
                    if (this.zzbSk.zzbRW != null) {
                        this.zzbSk.zzbSe.initialize(this.zzbSk.zzbRW);
                        this.zzbSk.zzbRW = null;
                    }
                    if (this.zzbSk.zzbRX != null) {
                        this.zzbSk.zzbSe.updateMaskedWalletRequest(this.zzbSk.zzbRX);
                        this.zzbSk.zzbRX = null;
                    }
                    if (this.zzbSk.zzbRY != null) {
                        this.zzbSk.zzbSe.updateMaskedWallet(this.zzbSk.zzbRY);
                        this.zzbSk.zzbRY = null;
                    }
                    if (this.zzbSk.zzbRZ != null) {
                        this.zzbSk.zzbSe.setEnabled(this.zzbSk.zzbRZ.booleanValue());
                        this.zzbSk.zzbRZ = null;
                    }
                } catch (GooglePlayServicesNotAvailableException e) {
                }
            }
        }
    }

    static class zza extends com.google.android.gms.internal.zzbkt.zza {
        private OnStateChangedListener zzbSi;
        private final WalletFragment zzbSj;

        zza(WalletFragment walletFragment) {
            this.zzbSj = walletFragment;
        }

        public void zza(int i, int i2, Bundle bundle) {
            if (this.zzbSi != null) {
                this.zzbSi.onStateChanged(this.zzbSj, i, i2, bundle);
            }
        }

        public void zza(OnStateChangedListener onStateChangedListener) {
            this.zzbSi = onStateChangedListener;
        }
    }

    public static WalletFragment newInstance(WalletFragmentOptions walletFragmentOptions) {
        WalletFragment walletFragment = new WalletFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("extraWalletFragmentOptions", walletFragmentOptions);
        walletFragment.zzaRK.setArguments(bundle);
        return walletFragment;
    }

    public int getState() {
        return this.zzbSe != null ? this.zzbSe.getState() : 0;
    }

    public void initialize(WalletFragmentInitParams walletFragmentInitParams) {
        if (this.zzbSe != null) {
            this.zzbSe.initialize(walletFragmentInitParams);
            this.zzbRW = null;
        } else if (this.zzbRW == null) {
            this.zzbRW = walletFragmentInitParams;
            if (this.zzbRX != null) {
                Log.w("WalletFragment", "updateMaskedWalletRequest() was called before initialize()");
            }
            if (this.zzbRY != null) {
                Log.w("WalletFragment", "updateMaskedWallet() was called before initialize()");
            }
        } else {
            Log.w("WalletFragment", "initialize(WalletFragmentInitParams) was called more than once. Ignoring.");
        }
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (this.zzbSe != null) {
            this.zzbSe.onActivityResult(i, i2, intent);
        }
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (bundle != null) {
            bundle.setClassLoader(WalletFragmentOptions.class.getClassLoader());
            WalletFragmentInitParams walletFragmentInitParams = (WalletFragmentInitParams) bundle.getParcelable("walletFragmentInitParams");
            if (walletFragmentInitParams != null) {
                if (this.zzbRW != null) {
                    Log.w("WalletFragment", "initialize(WalletFragmentInitParams) was called more than once.Ignoring.");
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
        } else if (this.zzaRK.getArguments() != null) {
            WalletFragmentOptions walletFragmentOptions = (WalletFragmentOptions) this.zzaRK.getArguments().getParcelable("extraWalletFragmentOptions");
            if (walletFragmentOptions != null) {
                walletFragmentOptions.zzci(this.zzaRK.getActivity());
                this.zzbRV = walletFragmentOptions;
            }
        }
        this.mCreated = true;
        this.zzbSg.onCreate(bundle);
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return this.zzbSg.onCreateView(layoutInflater, viewGroup, bundle);
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
        this.zzbSg.onInflate(activity, bundle2, bundle);
    }

    public void onPause() {
        super.onPause();
        this.zzbSg.onPause();
    }

    public void onResume() {
        super.onResume();
        this.zzbSg.onResume();
        FragmentManager fragmentManager = this.zzaRK.getActivity().getFragmentManager();
        Fragment findFragmentByTag = fragmentManager.findFragmentByTag(GooglePlayServicesUtil.GMS_ERROR_DIALOG);
        if (findFragmentByTag != null) {
            fragmentManager.beginTransaction().remove(findFragmentByTag).commit();
            GooglePlayServicesUtil.showErrorDialogFragment(GooglePlayServicesUtil.isGooglePlayServicesAvailable(this.zzaRK.getActivity()), this.zzaRK.getActivity(), -1);
        }
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.setClassLoader(WalletFragmentOptions.class.getClassLoader());
        this.zzbSg.onSaveInstanceState(bundle);
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
        this.zzbSg.onStart();
    }

    public void onStop() {
        super.onStop();
        this.zzbSg.onStop();
    }

    public void setEnabled(boolean z) {
        if (this.zzbSe != null) {
            this.zzbSe.setEnabled(z);
            this.zzbRZ = null;
            return;
        }
        this.zzbRZ = Boolean.valueOf(z);
    }

    public void setOnStateChangedListener(OnStateChangedListener onStateChangedListener) {
        this.zzbSh.zza(onStateChangedListener);
    }

    public void updateMaskedWallet(MaskedWallet maskedWallet) {
        if (this.zzbSe != null) {
            this.zzbSe.updateMaskedWallet(maskedWallet);
            this.zzbRY = null;
            return;
        }
        this.zzbRY = maskedWallet;
    }

    public void updateMaskedWalletRequest(MaskedWalletRequest maskedWalletRequest) {
        if (this.zzbSe != null) {
            this.zzbSe.updateMaskedWalletRequest(maskedWalletRequest);
            this.zzbRX = null;
            return;
        }
        this.zzbRX = maskedWalletRequest;
    }
}
