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
import com.google.android.gms.dynamic.zzj;
import com.google.android.gms.dynamic.zzn;
import com.google.android.gms.dynamic.zzo;
import com.google.android.gms.internal.fz;
import com.google.android.gms.internal.gd;
import com.google.android.gms.internal.gy;
import com.google.android.gms.wallet.MaskedWallet;
import com.google.android.gms.wallet.MaskedWalletRequest;

@TargetApi(12)
public final class WalletFragment extends Fragment {
    private boolean mCreated = false;
    private final Fragment zzaSB = this;
    private WalletFragmentOptions zzbQb;
    private WalletFragmentInitParams zzbQc;
    private MaskedWalletRequest zzbQd;
    private MaskedWallet zzbQe;
    private Boolean zzbQf;
    private zzb zzbQk;
    private final zzj zzbQl = zzj.zza(this);
    private final zzc zzbQm = new zzc();
    private zza zzbQn = new zza(this);

    public interface OnStateChangedListener {
        void onStateChanged(WalletFragment walletFragment, int i, int i2, Bundle bundle);
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
        private /* synthetic */ WalletFragment zzbQq;

        private zzc(WalletFragment walletFragment) {
            this.zzbQq = walletFragment;
        }

        public final void onClick(View view) {
            Context activity = this.zzbQq.zzaSB.getActivity();
            GooglePlayServicesUtil.showErrorDialogFragment(GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity), activity, -1);
        }

        protected final void zza(FrameLayout frameLayout) {
            int i = -1;
            int i2 = -2;
            View button = new Button(this.zzbQq.zzaSB.getActivity());
            button.setText(R.string.wallet_buy_button_place_holder);
            if (this.zzbQq.zzbQb != null) {
                WalletFragmentStyle fragmentStyle = this.zzbQq.zzbQb.getFragmentStyle();
                if (fragmentStyle != null) {
                    DisplayMetrics displayMetrics = this.zzbQq.zzaSB.getResources().getDisplayMetrics();
                    i = fragmentStyle.zza("buyButtonWidth", displayMetrics, -1);
                    i2 = fragmentStyle.zza("buyButtonHeight", displayMetrics, -2);
                }
            }
            button.setLayoutParams(new LayoutParams(i, i2));
            button.setOnClickListener(this);
            frameLayout.addView(button);
        }

        protected final void zza(zzo<zzb> com_google_android_gms_dynamic_zzo_com_google_android_gms_wallet_fragment_WalletFragment_zzb) {
            Activity activity = this.zzbQq.zzaSB.getActivity();
            if (this.zzbQq.zzbQk == null && this.zzbQq.mCreated && activity != null) {
                try {
                    this.zzbQq.zzbQk = new zzb(gy.zza(activity, this.zzbQq.zzbQl, this.zzbQq.zzbQb, this.zzbQq.zzbQn));
                    this.zzbQq.zzbQb = null;
                    com_google_android_gms_dynamic_zzo_com_google_android_gms_wallet_fragment_WalletFragment_zzb.zza(this.zzbQq.zzbQk);
                    if (this.zzbQq.zzbQc != null) {
                        this.zzbQq.zzbQk.initialize(this.zzbQq.zzbQc);
                        this.zzbQq.zzbQc = null;
                    }
                    if (this.zzbQq.zzbQd != null) {
                        this.zzbQq.zzbQk.updateMaskedWalletRequest(this.zzbQq.zzbQd);
                        this.zzbQq.zzbQd = null;
                    }
                    if (this.zzbQq.zzbQe != null) {
                        this.zzbQq.zzbQk.updateMaskedWallet(this.zzbQq.zzbQe);
                        this.zzbQq.zzbQe = null;
                    }
                    if (this.zzbQq.zzbQf != null) {
                        this.zzbQq.zzbQk.setEnabled(this.zzbQq.zzbQf.booleanValue());
                        this.zzbQq.zzbQf = null;
                    }
                } catch (GooglePlayServicesNotAvailableException e) {
                }
            }
        }
    }

    static class zza extends gd {
        private OnStateChangedListener zzbQo;
        private final WalletFragment zzbQp;

        zza(WalletFragment walletFragment) {
            this.zzbQp = walletFragment;
        }

        public final void zza(int i, int i2, Bundle bundle) {
            if (this.zzbQo != null) {
                this.zzbQo.onStateChanged(this.zzbQp, i, i2, bundle);
            }
        }

        public final void zza(OnStateChangedListener onStateChangedListener) {
            this.zzbQo = onStateChangedListener;
        }
    }

    public static WalletFragment newInstance(WalletFragmentOptions walletFragmentOptions) {
        WalletFragment walletFragment = new WalletFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("extraWalletFragmentOptions", walletFragmentOptions);
        walletFragment.zzaSB.setArguments(bundle);
        return walletFragment;
    }

    public final int getState() {
        return this.zzbQk != null ? this.zzbQk.getState() : 0;
    }

    public final void initialize(WalletFragmentInitParams walletFragmentInitParams) {
        if (this.zzbQk != null) {
            this.zzbQk.initialize(walletFragmentInitParams);
            this.zzbQc = null;
        } else if (this.zzbQc == null) {
            this.zzbQc = walletFragmentInitParams;
            if (this.zzbQd != null) {
                Log.w("WalletFragment", "updateMaskedWalletRequest() was called before initialize()");
            }
            if (this.zzbQe != null) {
                Log.w("WalletFragment", "updateMaskedWallet() was called before initialize()");
            }
        } else {
            Log.w("WalletFragment", "initialize(WalletFragmentInitParams) was called more than once. Ignoring.");
        }
    }

    public final void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (this.zzbQk != null) {
            this.zzbQk.onActivityResult(i, i2, intent);
        }
    }

    public final void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (bundle != null) {
            bundle.setClassLoader(WalletFragmentOptions.class.getClassLoader());
            WalletFragmentInitParams walletFragmentInitParams = (WalletFragmentInitParams) bundle.getParcelable("walletFragmentInitParams");
            if (walletFragmentInitParams != null) {
                if (this.zzbQc != null) {
                    Log.w("WalletFragment", "initialize(WalletFragmentInitParams) was called more than once.Ignoring.");
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
        } else if (this.zzaSB.getArguments() != null) {
            WalletFragmentOptions walletFragmentOptions = (WalletFragmentOptions) this.zzaSB.getArguments().getParcelable("extraWalletFragmentOptions");
            if (walletFragmentOptions != null) {
                walletFragmentOptions.zzby(this.zzaSB.getActivity());
                this.zzbQb = walletFragmentOptions;
            }
        }
        this.mCreated = true;
        this.zzbQm.onCreate(bundle);
    }

    public final View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return this.zzbQm.onCreateView(layoutInflater, viewGroup, bundle);
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
        this.zzbQm.onInflate(activity, bundle2, bundle);
    }

    public final void onPause() {
        super.onPause();
        this.zzbQm.onPause();
    }

    public final void onResume() {
        super.onResume();
        this.zzbQm.onResume();
        FragmentManager fragmentManager = this.zzaSB.getActivity().getFragmentManager();
        Fragment findFragmentByTag = fragmentManager.findFragmentByTag(GooglePlayServicesUtil.GMS_ERROR_DIALOG);
        if (findFragmentByTag != null) {
            fragmentManager.beginTransaction().remove(findFragmentByTag).commit();
            GooglePlayServicesUtil.showErrorDialogFragment(GooglePlayServicesUtil.isGooglePlayServicesAvailable(this.zzaSB.getActivity()), this.zzaSB.getActivity(), -1);
        }
    }

    public final void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.setClassLoader(WalletFragmentOptions.class.getClassLoader());
        this.zzbQm.onSaveInstanceState(bundle);
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
        this.zzbQm.onStart();
    }

    public final void onStop() {
        super.onStop();
        this.zzbQm.onStop();
    }

    public final void setEnabled(boolean z) {
        if (this.zzbQk != null) {
            this.zzbQk.setEnabled(z);
            this.zzbQf = null;
            return;
        }
        this.zzbQf = Boolean.valueOf(z);
    }

    public final void setOnStateChangedListener(OnStateChangedListener onStateChangedListener) {
        this.zzbQn.zza(onStateChangedListener);
    }

    public final void updateMaskedWallet(MaskedWallet maskedWallet) {
        if (this.zzbQk != null) {
            this.zzbQk.updateMaskedWallet(maskedWallet);
            this.zzbQe = null;
            return;
        }
        this.zzbQe = maskedWallet;
    }

    public final void updateMaskedWalletRequest(MaskedWalletRequest maskedWalletRequest) {
        if (this.zzbQk != null) {
            this.zzbQk.updateMaskedWalletRequest(maskedWalletRequest);
            this.zzbQd = null;
            return;
        }
        this.zzbQd = maskedWalletRequest;
    }
}
