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
import com.google.android.gms.maps.internal.IMapViewDelegate;
import com.google.android.gms.maps.internal.MapLifecycleDelegate;
import com.google.android.gms.maps.internal.zzbw;
import com.google.android.gms.maps.internal.zzbx;
import com.google.android.gms.maps.model.RuntimeRemoteException;
import java.util.ArrayList;
import java.util.List;

public class MapView extends FrameLayout {
    private final zzb zzbmu;

    static class zzb extends com.google.android.gms.dynamic.zza<zza> {
        private final GoogleMapOptions zzbmA;
        private zzo<zza> zzbms;
        private final List<OnMapReadyCallback> zzbmt = new ArrayList();
        private final ViewGroup zzbmy;
        private final Context zzbmz;

        zzb(ViewGroup viewGroup, Context context, GoogleMapOptions googleMapOptions) {
            this.zzbmy = viewGroup;
            this.zzbmz = context;
            this.zzbmA = googleMapOptions;
        }

        public final void getMapAsync(OnMapReadyCallback onMapReadyCallback) {
            if (zztx() != null) {
                ((zza) zztx()).getMapAsync(onMapReadyCallback);
            } else {
                this.zzbmt.add(onMapReadyCallback);
            }
        }

        protected final void zza(zzo<zza> com_google_android_gms_dynamic_zzo_com_google_android_gms_maps_MapView_zza) {
            this.zzbms = com_google_android_gms_dynamic_zzo_com_google_android_gms_maps_MapView_zza;
            if (this.zzbms != null && zztx() == null) {
                try {
                    MapsInitializer.initialize(this.zzbmz);
                    IMapViewDelegate zza = zzbx.zzbh(this.zzbmz).zza(zzn.zzw(this.zzbmz), this.zzbmA);
                    if (zza != null) {
                        this.zzbms.zza(new zza(this.zzbmy, zza));
                        for (OnMapReadyCallback mapAsync : this.zzbmt) {
                            ((zza) zztx()).getMapAsync(mapAsync);
                        }
                        this.zzbmt.clear();
                    }
                } catch (RemoteException e) {
                    throw new RuntimeRemoteException(e);
                } catch (GooglePlayServicesNotAvailableException e2) {
                }
            }
        }
    }

    static class zza implements MapLifecycleDelegate {
        private final ViewGroup zzbmv;
        private final IMapViewDelegate zzbmw;
        private View zzbmx;

        public zza(ViewGroup viewGroup, IMapViewDelegate iMapViewDelegate) {
            this.zzbmw = (IMapViewDelegate) zzbo.zzu(iMapViewDelegate);
            this.zzbmv = (ViewGroup) zzbo.zzu(viewGroup);
        }

        public final void getMapAsync(OnMapReadyCallback onMapReadyCallback) {
            try {
                this.zzbmw.getMapAsync(new zzab(this, onMapReadyCallback));
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public final void onCreate(Bundle bundle) {
            try {
                Bundle bundle2 = new Bundle();
                zzbw.zzd(bundle, bundle2);
                this.zzbmw.onCreate(bundle2);
                zzbw.zzd(bundle2, bundle);
                this.zzbmx = (View) zzn.zzE(this.zzbmw.getView());
                this.zzbmv.removeAllViews();
                this.zzbmv.addView(this.zzbmx);
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public final View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
            throw new UnsupportedOperationException("onCreateView not allowed on MapViewDelegate");
        }

        public final void onDestroy() {
            try {
                this.zzbmw.onDestroy();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public final void onDestroyView() {
            throw new UnsupportedOperationException("onDestroyView not allowed on MapViewDelegate");
        }

        public final void onEnterAmbient(Bundle bundle) {
            try {
                Bundle bundle2 = new Bundle();
                zzbw.zzd(bundle, bundle2);
                this.zzbmw.onEnterAmbient(bundle2);
                zzbw.zzd(bundle2, bundle);
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public final void onExitAmbient() {
            try {
                this.zzbmw.onExitAmbient();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public final void onInflate(Activity activity, Bundle bundle, Bundle bundle2) {
            throw new UnsupportedOperationException("onInflate not allowed on MapViewDelegate");
        }

        public final void onLowMemory() {
            try {
                this.zzbmw.onLowMemory();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public final void onPause() {
            try {
                this.zzbmw.onPause();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public final void onResume() {
            try {
                this.zzbmw.onResume();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public final void onSaveInstanceState(Bundle bundle) {
            try {
                Bundle bundle2 = new Bundle();
                zzbw.zzd(bundle, bundle2);
                this.zzbmw.onSaveInstanceState(bundle2);
                zzbw.zzd(bundle2, bundle);
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public final void onStart() {
            try {
                this.zzbmw.onStart();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public final void onStop() {
            try {
                this.zzbmw.onStop();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
    }

    public MapView(Context context) {
        super(context);
        this.zzbmu = new zzb(this, context, null);
        setClickable(true);
    }

    public MapView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.zzbmu = new zzb(this, context, GoogleMapOptions.createFromAttributes(context, attributeSet));
        setClickable(true);
    }

    public MapView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.zzbmu = new zzb(this, context, GoogleMapOptions.createFromAttributes(context, attributeSet));
        setClickable(true);
    }

    public MapView(Context context, GoogleMapOptions googleMapOptions) {
        super(context);
        this.zzbmu = new zzb(this, context, googleMapOptions);
        setClickable(true);
    }

    public void getMapAsync(OnMapReadyCallback onMapReadyCallback) {
        zzbo.zzcz("getMapAsync() must be called on the main thread");
        this.zzbmu.getMapAsync(onMapReadyCallback);
    }

    public final void onCreate(Bundle bundle) {
        this.zzbmu.onCreate(bundle);
        if (this.zzbmu.zztx() == null) {
            com.google.android.gms.dynamic.zza.zzb((FrameLayout) this);
        }
    }

    public final void onDestroy() {
        this.zzbmu.onDestroy();
    }

    public final void onEnterAmbient(Bundle bundle) {
        zzbo.zzcz("onEnterAmbient() must be called on the main thread");
        zzb com_google_android_gms_maps_MapView_zzb = this.zzbmu;
        if (com_google_android_gms_maps_MapView_zzb.zztx() != null) {
            ((zza) com_google_android_gms_maps_MapView_zzb.zztx()).onEnterAmbient(bundle);
        }
    }

    public final void onExitAmbient() {
        zzbo.zzcz("onExitAmbient() must be called on the main thread");
        zzb com_google_android_gms_maps_MapView_zzb = this.zzbmu;
        if (com_google_android_gms_maps_MapView_zzb.zztx() != null) {
            ((zza) com_google_android_gms_maps_MapView_zzb.zztx()).onExitAmbient();
        }
    }

    public final void onLowMemory() {
        this.zzbmu.onLowMemory();
    }

    public final void onPause() {
        this.zzbmu.onPause();
    }

    public final void onResume() {
        this.zzbmu.onResume();
    }

    public final void onSaveInstanceState(Bundle bundle) {
        this.zzbmu.onSaveInstanceState(bundle);
    }

    public final void onStart() {
        this.zzbmu.onStart();
    }

    public final void onStop() {
        this.zzbmu.onStop();
    }
}
