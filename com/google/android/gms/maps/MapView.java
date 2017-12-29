package com.google.android.gms.maps;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import android.os.StrictMode.ThreadPolicy.Builder;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.internal.zzbq;
import com.google.android.gms.dynamic.zzn;
import com.google.android.gms.dynamic.zzo;
import com.google.android.gms.maps.internal.IMapViewDelegate;
import com.google.android.gms.maps.internal.MapLifecycleDelegate;
import com.google.android.gms.maps.internal.zzby;
import com.google.android.gms.maps.internal.zzbz;
import com.google.android.gms.maps.model.RuntimeRemoteException;
import java.util.ArrayList;
import java.util.List;

public class MapView extends FrameLayout {
    private final zzb zzisw;

    static class zzb extends com.google.android.gms.dynamic.zza<zza> {
        private zzo<zza> zzisu;
        private final List<OnMapReadyCallback> zzisv = new ArrayList();
        private final ViewGroup zzita;
        private final Context zzitb;
        private final GoogleMapOptions zzitc;

        zzb(ViewGroup viewGroup, Context context, GoogleMapOptions googleMapOptions) {
            this.zzita = viewGroup;
            this.zzitb = context;
            this.zzitc = googleMapOptions;
        }

        public final void getMapAsync(OnMapReadyCallback onMapReadyCallback) {
            if (zzapw() != null) {
                ((zza) zzapw()).getMapAsync(onMapReadyCallback);
            } else {
                this.zzisv.add(onMapReadyCallback);
            }
        }

        protected final void zza(zzo<zza> com_google_android_gms_dynamic_zzo_com_google_android_gms_maps_MapView_zza) {
            this.zzisu = com_google_android_gms_dynamic_zzo_com_google_android_gms_maps_MapView_zza;
            if (this.zzisu != null && zzapw() == null) {
                try {
                    MapsInitializer.initialize(this.zzitb);
                    IMapViewDelegate zza = zzbz.zzdt(this.zzitb).zza(zzn.zzz(this.zzitb), this.zzitc);
                    if (zza != null) {
                        this.zzisu.zza(new zza(this.zzita, zza));
                        for (OnMapReadyCallback mapAsync : this.zzisv) {
                            ((zza) zzapw()).getMapAsync(mapAsync);
                        }
                        this.zzisv.clear();
                    }
                } catch (RemoteException e) {
                    throw new RuntimeRemoteException(e);
                } catch (GooglePlayServicesNotAvailableException e2) {
                }
            }
        }
    }

    static class zza implements MapLifecycleDelegate {
        private final ViewGroup zzisx;
        private final IMapViewDelegate zzisy;
        private View zzisz;

        public zza(ViewGroup viewGroup, IMapViewDelegate iMapViewDelegate) {
            this.zzisy = (IMapViewDelegate) zzbq.checkNotNull(iMapViewDelegate);
            this.zzisx = (ViewGroup) zzbq.checkNotNull(viewGroup);
        }

        public final void getMapAsync(OnMapReadyCallback onMapReadyCallback) {
            try {
                this.zzisy.getMapAsync(new zzac(this, onMapReadyCallback));
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public final void onCreate(Bundle bundle) {
            try {
                Bundle bundle2 = new Bundle();
                zzby.zzd(bundle, bundle2);
                this.zzisy.onCreate(bundle2);
                zzby.zzd(bundle2, bundle);
                this.zzisz = (View) zzn.zzx(this.zzisy.getView());
                this.zzisx.removeAllViews();
                this.zzisx.addView(this.zzisz);
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public final View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
            throw new UnsupportedOperationException("onCreateView not allowed on MapViewDelegate");
        }

        public final void onDestroy() {
            try {
                this.zzisy.onDestroy();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public final void onEnterAmbient(Bundle bundle) {
            try {
                Bundle bundle2 = new Bundle();
                zzby.zzd(bundle, bundle2);
                this.zzisy.onEnterAmbient(bundle2);
                zzby.zzd(bundle2, bundle);
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public final void onExitAmbient() {
            try {
                this.zzisy.onExitAmbient();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public final void onInflate(Activity activity, Bundle bundle, Bundle bundle2) {
            throw new UnsupportedOperationException("onInflate not allowed on MapViewDelegate");
        }

        public final void onLowMemory() {
            try {
                this.zzisy.onLowMemory();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public final void onPause() {
            try {
                this.zzisy.onPause();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public final void onResume() {
            try {
                this.zzisy.onResume();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public final void onSaveInstanceState(Bundle bundle) {
            try {
                Bundle bundle2 = new Bundle();
                zzby.zzd(bundle, bundle2);
                this.zzisy.onSaveInstanceState(bundle2);
                zzby.zzd(bundle2, bundle);
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public final void onStart() {
            try {
                this.zzisy.onStart();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public final void onStop() {
            try {
                this.zzisy.onStop();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
    }

    public MapView(Context context) {
        super(context);
        this.zzisw = new zzb(this, context, null);
        setClickable(true);
    }

    public MapView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.zzisw = new zzb(this, context, GoogleMapOptions.createFromAttributes(context, attributeSet));
        setClickable(true);
    }

    public MapView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.zzisw = new zzb(this, context, GoogleMapOptions.createFromAttributes(context, attributeSet));
        setClickable(true);
    }

    public MapView(Context context, GoogleMapOptions googleMapOptions) {
        super(context);
        this.zzisw = new zzb(this, context, googleMapOptions);
        setClickable(true);
    }

    public void getMapAsync(OnMapReadyCallback onMapReadyCallback) {
        zzbq.zzge("getMapAsync() must be called on the main thread");
        this.zzisw.getMapAsync(onMapReadyCallback);
    }

    public final void onCreate(Bundle bundle) {
        ThreadPolicy threadPolicy = StrictMode.getThreadPolicy();
        StrictMode.setThreadPolicy(new Builder(threadPolicy).permitAll().build());
        try {
            this.zzisw.onCreate(bundle);
            if (this.zzisw.zzapw() == null) {
                com.google.android.gms.dynamic.zza.zzb((FrameLayout) this);
            }
            StrictMode.setThreadPolicy(threadPolicy);
        } catch (Throwable th) {
            StrictMode.setThreadPolicy(threadPolicy);
        }
    }

    public final void onDestroy() {
        this.zzisw.onDestroy();
    }

    public final void onEnterAmbient(Bundle bundle) {
        zzbq.zzge("onEnterAmbient() must be called on the main thread");
        com.google.android.gms.dynamic.zza com_google_android_gms_dynamic_zza = this.zzisw;
        if (com_google_android_gms_dynamic_zza.zzapw() != null) {
            ((zza) com_google_android_gms_dynamic_zza.zzapw()).onEnterAmbient(bundle);
        }
    }

    public final void onExitAmbient() {
        zzbq.zzge("onExitAmbient() must be called on the main thread");
        com.google.android.gms.dynamic.zza com_google_android_gms_dynamic_zza = this.zzisw;
        if (com_google_android_gms_dynamic_zza.zzapw() != null) {
            ((zza) com_google_android_gms_dynamic_zza.zzapw()).onExitAmbient();
        }
    }

    public final void onLowMemory() {
        this.zzisw.onLowMemory();
    }

    public final void onPause() {
        this.zzisw.onPause();
    }

    public final void onResume() {
        this.zzisw.onResume();
    }

    public final void onSaveInstanceState(Bundle bundle) {
        this.zzisw.onSaveInstanceState(bundle);
    }

    public final void onStart() {
        this.zzisw.onStart();
    }

    public final void onStop() {
        this.zzisw.onStop();
    }
}
