package com.google.android.gms.maps;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.dynamic.zzn;
import com.google.android.gms.dynamic.zzo;
import com.google.android.gms.maps.internal.IStreetViewPanoramaViewDelegate;
import com.google.android.gms.maps.internal.StreetViewLifecycleDelegate;
import com.google.android.gms.maps.internal.zzbw;
import com.google.android.gms.maps.internal.zzbx;
import com.google.android.gms.maps.model.RuntimeRemoteException;
import java.util.ArrayList;
import java.util.List;

public class StreetViewPanoramaView extends FrameLayout {
    private final zzb zzbmS;

    static class zzb extends com.google.android.gms.dynamic.zza<zza> {
        private final List<OnStreetViewPanoramaReadyCallback> zzbmK = new ArrayList();
        private final StreetViewPanoramaOptions zzbmV;
        private zzo<zza> zzbms;
        private final ViewGroup zzbmy;
        private final Context zzbmz;

        zzb(ViewGroup viewGroup, Context context, StreetViewPanoramaOptions streetViewPanoramaOptions) {
            this.zzbmy = viewGroup;
            this.zzbmz = context;
            this.zzbmV = streetViewPanoramaOptions;
        }

        public final void getStreetViewPanoramaAsync(OnStreetViewPanoramaReadyCallback onStreetViewPanoramaReadyCallback) {
            if (zztx() != null) {
                ((zza) zztx()).getStreetViewPanoramaAsync(onStreetViewPanoramaReadyCallback);
            } else {
                this.zzbmK.add(onStreetViewPanoramaReadyCallback);
            }
        }

        protected final void zza(zzo<zza> com_google_android_gms_dynamic_zzo_com_google_android_gms_maps_StreetViewPanoramaView_zza) {
            this.zzbms = com_google_android_gms_dynamic_zzo_com_google_android_gms_maps_StreetViewPanoramaView_zza;
            if (this.zzbms != null && zztx() == null) {
                try {
                    this.zzbms.zza(new zza(this.zzbmy, zzbx.zzbh(this.zzbmz).zza(zzn.zzw(this.zzbmz), this.zzbmV)));
                    for (OnStreetViewPanoramaReadyCallback streetViewPanoramaAsync : this.zzbmK) {
                        ((zza) zztx()).getStreetViewPanoramaAsync(streetViewPanoramaAsync);
                    }
                    this.zzbmK.clear();
                } catch (RemoteException e) {
                    throw new RuntimeRemoteException(e);
                } catch (GooglePlayServicesNotAvailableException e2) {
                }
            }
        }
    }

    static class zza implements StreetViewLifecycleDelegate {
        private final IStreetViewPanoramaViewDelegate zzbmT;
        private View zzbmU;
        private final ViewGroup zzbmv;

        public zza(ViewGroup viewGroup, IStreetViewPanoramaViewDelegate iStreetViewPanoramaViewDelegate) {
            this.zzbmT = (IStreetViewPanoramaViewDelegate) zzbo.zzu(iStreetViewPanoramaViewDelegate);
            this.zzbmv = (ViewGroup) zzbo.zzu(viewGroup);
        }

        public final void getStreetViewPanoramaAsync(OnStreetViewPanoramaReadyCallback onStreetViewPanoramaReadyCallback) {
            try {
                this.zzbmT.getStreetViewPanoramaAsync(new zzai(this, onStreetViewPanoramaReadyCallback));
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public final void onCreate(Bundle bundle) {
            try {
                Bundle bundle2 = new Bundle();
                zzbw.zzd(bundle, bundle2);
                this.zzbmT.onCreate(bundle2);
                zzbw.zzd(bundle2, bundle);
                this.zzbmU = (View) zzn.zzE(this.zzbmT.getView());
                this.zzbmv.removeAllViews();
                this.zzbmv.addView(this.zzbmU);
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public final View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
            throw new UnsupportedOperationException("onCreateView not allowed on StreetViewPanoramaViewDelegate");
        }

        public final void onDestroy() {
            try {
                this.zzbmT.onDestroy();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public final void onDestroyView() {
            throw new UnsupportedOperationException("onDestroyView not allowed on StreetViewPanoramaViewDelegate");
        }

        public final void onInflate(Activity activity, Bundle bundle, Bundle bundle2) {
            throw new UnsupportedOperationException("onInflate not allowed on StreetViewPanoramaViewDelegate");
        }

        public final void onLowMemory() {
            try {
                this.zzbmT.onLowMemory();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public final void onPause() {
            try {
                this.zzbmT.onPause();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public final void onResume() {
            try {
                this.zzbmT.onResume();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public final void onSaveInstanceState(Bundle bundle) {
            try {
                Bundle bundle2 = new Bundle();
                zzbw.zzd(bundle, bundle2);
                this.zzbmT.onSaveInstanceState(bundle2);
                zzbw.zzd(bundle2, bundle);
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public final void onStart() {
        }

        public final void onStop() {
        }
    }

    public StreetViewPanoramaView(Context context) {
        super(context);
        this.zzbmS = new zzb(this, context, null);
    }

    public StreetViewPanoramaView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.zzbmS = new zzb(this, context, null);
    }

    public StreetViewPanoramaView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.zzbmS = new zzb(this, context, null);
    }

    public StreetViewPanoramaView(Context context, StreetViewPanoramaOptions streetViewPanoramaOptions) {
        super(context);
        this.zzbmS = new zzb(this, context, streetViewPanoramaOptions);
    }

    public void getStreetViewPanoramaAsync(OnStreetViewPanoramaReadyCallback onStreetViewPanoramaReadyCallback) {
        zzbo.zzcz("getStreetViewPanoramaAsync() must be called on the main thread");
        this.zzbmS.getStreetViewPanoramaAsync(onStreetViewPanoramaReadyCallback);
    }

    public final void onCreate(Bundle bundle) {
        this.zzbmS.onCreate(bundle);
        if (this.zzbmS.zztx() == null) {
            com.google.android.gms.dynamic.zza.zzb((FrameLayout) this);
        }
    }

    public final void onDestroy() {
        this.zzbmS.onDestroy();
    }

    public final void onLowMemory() {
        this.zzbmS.onLowMemory();
    }

    public final void onPause() {
        this.zzbmS.onPause();
    }

    public final void onResume() {
        this.zzbmS.onResume();
    }

    public final void onSaveInstanceState(Bundle bundle) {
        this.zzbmS.onSaveInstanceState(bundle);
    }
}
