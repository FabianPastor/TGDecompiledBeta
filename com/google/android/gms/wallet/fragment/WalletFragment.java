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
import com.google.android.gms.internal.zzdkw;
import com.google.android.gms.internal.zzdla;
import com.google.android.gms.internal.zzdlv;
import com.google.android.gms.wallet.MaskedWallet;
import com.google.android.gms.wallet.MaskedWalletRequest;

@TargetApi(12)
public final class WalletFragment extends Fragment {
    private boolean mCreated = false;
    private final Fragment zzgwm = this;
    private WalletFragmentOptions zzlep;
    private WalletFragmentInitParams zzleq;
    private MaskedWalletRequest zzler;
    private MaskedWallet zzles;
    private Boolean zzlet;
    private zzb zzley;
    private final zzj zzlez = zzj.zza(this);
    private final zzc zzlfa = new zzc();
    private zza zzlfb = new zza(this);

    public interface OnStateChangedListener {
        void onStateChanged(WalletFragment walletFragment, int i, int i2, Bundle bundle);
    }

    static class zzb implements LifecycleDelegate {
        private final zzdkw zzlew;

        private zzb(zzdkw com_google_android_gms_internal_zzdkw) {
            this.zzlew = com_google_android_gms_internal_zzdkw;
        }

        private final void initialize(WalletFragmentInitParams walletFragmentInitParams) {
            try {
                this.zzlew.initialize(walletFragmentInitParams);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        private final void onActivityResult(int i, int i2, Intent intent) {
            try {
                this.zzlew.onActivityResult(i, i2, intent);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        private final void setEnabled(boolean z) {
            try {
                this.zzlew.setEnabled(z);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        private final void updateMaskedWallet(MaskedWallet maskedWallet) {
            try {
                this.zzlew.updateMaskedWallet(maskedWallet);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        private final void updateMaskedWalletRequest(MaskedWalletRequest maskedWalletRequest) {
            try {
                this.zzlew.updateMaskedWalletRequest(maskedWalletRequest);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        public final void onCreate(Bundle bundle) {
            try {
                this.zzlew.onCreate(bundle);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        public final View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
            try {
                return (View) zzn.zzx(this.zzlew.onCreateView(zzn.zzz(layoutInflater), zzn.zzz(viewGroup), bundle));
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        public final void onDestroy() {
        }

        public final void onInflate(Activity activity, Bundle bundle, Bundle bundle2) {
            try {
                this.zzlew.zza(zzn.zzz(activity), (WalletFragmentOptions) bundle.getParcelable("extraWalletFragmentOptions"), bundle2);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        public final void onLowMemory() {
        }

        public final void onPause() {
            try {
                this.zzlew.onPause();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        public final void onResume() {
            try {
                this.zzlew.onResume();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        public final void onSaveInstanceState(Bundle bundle) {
            try {
                this.zzlew.onSaveInstanceState(bundle);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        public final void onStart() {
            try {
                this.zzlew.onStart();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        public final void onStop() {
            try {
                this.zzlew.onStop();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }

    class zzc extends com.google.android.gms.dynamic.zza<zzb> implements OnClickListener {
        private /* synthetic */ WalletFragment zzlfe;

        private zzc(WalletFragment walletFragment) {
            this.zzlfe = walletFragment;
        }

        public final void onClick(View view) {
            Context activity = this.zzlfe.zzgwm.getActivity();
            GooglePlayServicesUtil.showErrorDialogFragment(GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity), activity, -1);
        }

        protected final void zza(FrameLayout frameLayout) {
            int i = -1;
            int i2 = -2;
            View button = new Button(this.zzlfe.zzgwm.getActivity());
            button.setText(R.string.wallet_buy_button_place_holder);
            if (this.zzlfe.zzlep != null) {
                WalletFragmentStyle fragmentStyle = this.zzlfe.zzlep.getFragmentStyle();
                if (fragmentStyle != null) {
                    DisplayMetrics displayMetrics = this.zzlfe.zzgwm.getResources().getDisplayMetrics();
                    i = fragmentStyle.zza("buyButtonWidth", displayMetrics, -1);
                    i2 = fragmentStyle.zza("buyButtonHeight", displayMetrics, -2);
                }
            }
            button.setLayoutParams(new LayoutParams(i, i2));
            button.setOnClickListener(this);
            frameLayout.addView(button);
        }

        protected final void zza(zzo<zzb> com_google_android_gms_dynamic_zzo_com_google_android_gms_wallet_fragment_WalletFragment_zzb) {
            Activity activity = this.zzlfe.zzgwm.getActivity();
            if (this.zzlfe.zzley == null && this.zzlfe.mCreated && activity != null) {
                try {
                    this.zzlfe.zzley = new zzb(zzdlv.zza(activity, this.zzlfe.zzlez, this.zzlfe.zzlep, this.zzlfe.zzlfb));
                    this.zzlfe.zzlep = null;
                    com_google_android_gms_dynamic_zzo_com_google_android_gms_wallet_fragment_WalletFragment_zzb.zza(this.zzlfe.zzley);
                    if (this.zzlfe.zzleq != null) {
                        this.zzlfe.zzley.initialize(this.zzlfe.zzleq);
                        this.zzlfe.zzleq = null;
                    }
                    if (this.zzlfe.zzler != null) {
                        this.zzlfe.zzley.updateMaskedWalletRequest(this.zzlfe.zzler);
                        this.zzlfe.zzler = null;
                    }
                    if (this.zzlfe.zzles != null) {
                        this.zzlfe.zzley.updateMaskedWallet(this.zzlfe.zzles);
                        this.zzlfe.zzles = null;
                    }
                    if (this.zzlfe.zzlet != null) {
                        this.zzlfe.zzley.setEnabled(this.zzlfe.zzlet.booleanValue());
                        this.zzlfe.zzlet = null;
                    }
                } catch (GooglePlayServicesNotAvailableException e) {
                }
            }
        }
    }

    static class zza extends zzdla {
        private OnStateChangedListener zzlfc;
        private final WalletFragment zzlfd;

        zza(WalletFragment walletFragment) {
            this.zzlfd = walletFragment;
        }

        public final void zza(int i, int i2, Bundle bundle) {
            if (this.zzlfc != null) {
                this.zzlfc.onStateChanged(this.zzlfd, i, i2, bundle);
            }
        }
    }

    public static WalletFragment newInstance(WalletFragmentOptions walletFragmentOptions) {
        WalletFragment walletFragment = new WalletFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("extraWalletFragmentOptions", walletFragmentOptions);
        walletFragment.zzgwm.setArguments(bundle);
        return walletFragment;
    }

    public final void initialize(WalletFragmentInitParams walletFragmentInitParams) {
        if (this.zzley != null) {
            this.zzley.initialize(walletFragmentInitParams);
            this.zzleq = null;
        } else if (this.zzleq == null) {
            this.zzleq = walletFragmentInitParams;
            if (this.zzler != null) {
                Log.w("WalletFragment", "updateMaskedWalletRequest() was called before initialize()");
            }
            if (this.zzles != null) {
                Log.w("WalletFragment", "updateMaskedWallet() was called before initialize()");
            }
        } else {
            Log.w("WalletFragment", "initialize(WalletFragmentInitParams) was called more than once. Ignoring.");
        }
    }

    public final void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (this.zzley != null) {
            this.zzley.onActivityResult(i, i2, intent);
        }
    }

    public final void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (bundle != null) {
            bundle.setClassLoader(WalletFragmentOptions.class.getClassLoader());
            WalletFragmentInitParams walletFragmentInitParams = (WalletFragmentInitParams) bundle.getParcelable("walletFragmentInitParams");
            if (walletFragmentInitParams != null) {
                if (this.zzleq != null) {
                    Log.w("WalletFragment", "initialize(WalletFragmentInitParams) was called more than once.Ignoring.");
                }
                this.zzleq = walletFragmentInitParams;
            }
            if (this.zzler == null) {
                this.zzler = (MaskedWalletRequest) bundle.getParcelable("maskedWalletRequest");
            }
            if (this.zzles == null) {
                this.zzles = (MaskedWallet) bundle.getParcelable("maskedWallet");
            }
            if (bundle.containsKey("walletFragmentOptions")) {
                this.zzlep = (WalletFragmentOptions) bundle.getParcelable("walletFragmentOptions");
            }
            if (bundle.containsKey("enabled")) {
                this.zzlet = Boolean.valueOf(bundle.getBoolean("enabled"));
            }
        } else if (this.zzgwm.getArguments() != null) {
            WalletFragmentOptions walletFragmentOptions = (WalletFragmentOptions) this.zzgwm.getArguments().getParcelable("extraWalletFragmentOptions");
            if (walletFragmentOptions != null) {
                walletFragmentOptions.zzeo(this.zzgwm.getActivity());
                this.zzlep = walletFragmentOptions;
            }
        }
        this.mCreated = true;
        this.zzlfa.onCreate(bundle);
    }

    public final View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return this.zzlfa.onCreateView(layoutInflater, viewGroup, bundle);
    }

    public final void onDestroy() {
        super.onDestroy();
        this.mCreated = false;
    }

    public final void onInflate(Activity activity, AttributeSet attributeSet, Bundle bundle) {
        super.onInflate(activity, attributeSet, bundle);
        if (this.zzlep == null) {
            this.zzlep = WalletFragmentOptions.zza((Context) activity, attributeSet);
        }
        Bundle bundle2 = new Bundle();
        bundle2.putParcelable("attrKeyWalletFragmentOptions", this.zzlep);
        this.zzlfa.onInflate(activity, bundle2, bundle);
    }

    public final void onPause() {
        super.onPause();
        this.zzlfa.onPause();
    }

    public final void onResume() {
        super.onResume();
        this.zzlfa.onResume();
        FragmentManager fragmentManager = this.zzgwm.getActivity().getFragmentManager();
        Fragment findFragmentByTag = fragmentManager.findFragmentByTag("GooglePlayServicesErrorDialog");
        if (findFragmentByTag != null) {
            fragmentManager.beginTransaction().remove(findFragmentByTag).commit();
            GooglePlayServicesUtil.showErrorDialogFragment(GooglePlayServicesUtil.isGooglePlayServicesAvailable(this.zzgwm.getActivity()), this.zzgwm.getActivity(), -1);
        }
    }

    public final void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.setClassLoader(WalletFragmentOptions.class.getClassLoader());
        this.zzlfa.onSaveInstanceState(bundle);
        if (this.zzleq != null) {
            bundle.putParcelable("walletFragmentInitParams", this.zzleq);
            this.zzleq = null;
        }
        if (this.zzler != null) {
            bundle.putParcelable("maskedWalletRequest", this.zzler);
            this.zzler = null;
        }
        if (this.zzles != null) {
            bundle.putParcelable("maskedWallet", this.zzles);
            this.zzles = null;
        }
        if (this.zzlep != null) {
            bundle.putParcelable("walletFragmentOptions", this.zzlep);
            this.zzlep = null;
        }
        if (this.zzlet != null) {
            bundle.putBoolean("enabled", this.zzlet.booleanValue());
            this.zzlet = null;
        }
    }

    public final void onStart() {
        super.onStart();
        this.zzlfa.onStart();
    }

    public final void onStop() {
        super.onStop();
        this.zzlfa.onStop();
    }
}
