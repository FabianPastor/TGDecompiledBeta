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
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.dynamic.zzd;
import com.google.android.gms.dynamic.zze;
import com.google.android.gms.maps.internal.IGoogleMapDelegate;
import com.google.android.gms.maps.internal.IMapViewDelegate;
import com.google.android.gms.maps.internal.MapLifecycleDelegate;
import com.google.android.gms.maps.internal.zzah;
import com.google.android.gms.maps.internal.zzai;
import com.google.android.gms.maps.model.RuntimeRemoteException;
import java.util.ArrayList;
import java.util.List;

public class MapView extends FrameLayout {
    private final zzb zzbox;

    static class zzb extends com.google.android.gms.dynamic.zza<zza> {
        private final Context mContext;
        private final ViewGroup zzboB;
        private final GoogleMapOptions zzboC;
        protected zze<zza> zzbov;
        private final List<OnMapReadyCallback> zzbow = new ArrayList();

        zzb(ViewGroup viewGroup, Context context, GoogleMapOptions googleMapOptions) {
            this.zzboB = viewGroup;
            this.mContext = context;
            this.zzboC = googleMapOptions;
        }

        public void getMapAsync(OnMapReadyCallback onMapReadyCallback) {
            if (zzBM() != null) {
                ((zza) zzBM()).getMapAsync(onMapReadyCallback);
            } else {
                this.zzbow.add(onMapReadyCallback);
            }
        }

        public void onEnterAmbient(Bundle bundle) {
            if (zzBM() != null) {
                ((zza) zzBM()).onEnterAmbient(bundle);
            }
        }

        public void onExitAmbient() {
            if (zzBM() != null) {
                ((zza) zzBM()).onExitAmbient();
            }
        }

        public void zzJy() {
            if (this.zzbov != null && zzBM() == null) {
                try {
                    MapsInitializer.initialize(this.mContext);
                    IMapViewDelegate zza = zzai.zzbI(this.mContext).zza(zzd.zzA(this.mContext), this.zzboC);
                    if (zza != null) {
                        this.zzbov.zza(new zza(this.zzboB, zza));
                        for (OnMapReadyCallback mapAsync : this.zzbow) {
                            ((zza) zzBM()).getMapAsync(mapAsync);
                        }
                        this.zzbow.clear();
                    }
                } catch (RemoteException e) {
                    throw new RuntimeRemoteException(e);
                } catch (GooglePlayServicesNotAvailableException e2) {
                }
            }
        }

        protected void zza(zze<zza> com_google_android_gms_dynamic_zze_com_google_android_gms_maps_MapView_zza) {
            this.zzbov = com_google_android_gms_dynamic_zze_com_google_android_gms_maps_MapView_zza;
            zzJy();
        }
    }

    static class zza implements MapLifecycleDelegate {
        private View zzboA;
        private final ViewGroup zzboy;
        private final IMapViewDelegate zzboz;

        public zza(ViewGroup viewGroup, IMapViewDelegate iMapViewDelegate) {
            this.zzboz = (IMapViewDelegate) zzac.zzw(iMapViewDelegate);
            this.zzboy = (ViewGroup) zzac.zzw(viewGroup);
        }

        public void getMapAsync(final OnMapReadyCallback onMapReadyCallback) {
            try {
                this.zzboz.getMapAsync(new com.google.android.gms.maps.internal.zzt.zza(this) {
                    public void zza(IGoogleMapDelegate iGoogleMapDelegate) throws RemoteException {
                        onMapReadyCallback.onMapReady(new GoogleMap(iGoogleMapDelegate));
                    }
                });
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public void onCreate(Bundle bundle) {
            try {
                Bundle bundle2 = new Bundle();
                zzah.zzd(bundle, bundle2);
                this.zzboz.onCreate(bundle2);
                zzah.zzd(bundle2, bundle);
                this.zzboA = (View) zzd.zzF(this.zzboz.getView());
                this.zzboy.removeAllViews();
                this.zzboy.addView(this.zzboA);
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
            throw new UnsupportedOperationException("onCreateView not allowed on MapViewDelegate");
        }

        public void onDestroy() {
            try {
                this.zzboz.onDestroy();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public void onDestroyView() {
            throw new UnsupportedOperationException("onDestroyView not allowed on MapViewDelegate");
        }

        public void onEnterAmbient(Bundle bundle) {
            try {
                Bundle bundle2 = new Bundle();
                zzah.zzd(bundle, bundle2);
                this.zzboz.onEnterAmbient(bundle2);
                zzah.zzd(bundle2, bundle);
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public void onExitAmbient() {
            try {
                this.zzboz.onExitAmbient();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public void onInflate(Activity activity, Bundle bundle, Bundle bundle2) {
            throw new UnsupportedOperationException("onInflate not allowed on MapViewDelegate");
        }

        public void onLowMemory() {
            try {
                this.zzboz.onLowMemory();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public void onPause() {
            try {
                this.zzboz.onPause();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public void onResume() {
            try {
                this.zzboz.onResume();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public void onSaveInstanceState(Bundle bundle) {
            try {
                Bundle bundle2 = new Bundle();
                zzah.zzd(bundle, bundle2);
                this.zzboz.onSaveInstanceState(bundle2);
                zzah.zzd(bundle2, bundle);
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public void onStart() {
            try {
                this.zzboz.onStart();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public void onStop() {
            try {
                this.zzboz.onStop();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
    }

    public MapView(Context context) {
        super(context);
        this.zzbox = new zzb(this, context, null);
        zzJz();
    }

    public MapView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.zzbox = new zzb(this, context, GoogleMapOptions.createFromAttributes(context, attributeSet));
        zzJz();
    }

    public MapView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.zzbox = new zzb(this, context, GoogleMapOptions.createFromAttributes(context, attributeSet));
        zzJz();
    }

    public MapView(Context context, GoogleMapOptions googleMapOptions) {
        super(context);
        this.zzbox = new zzb(this, context, googleMapOptions);
        zzJz();
    }

    private void zzJz() {
        setClickable(true);
    }

    public void getMapAsync(OnMapReadyCallback onMapReadyCallback) {
        zzac.zzdj("getMapAsync() must be called on the main thread");
        this.zzbox.getMapAsync(onMapReadyCallback);
    }

    public final void onCreate(Bundle bundle) {
        this.zzbox.onCreate(bundle);
        if (this.zzbox.zzBM() == null) {
            com.google.android.gms.dynamic.zza.zzb((FrameLayout) this);
        }
    }

    public final void onDestroy() {
        this.zzbox.onDestroy();
    }

    public final void onEnterAmbient(Bundle bundle) {
        zzac.zzdj("onEnterAmbient() must be called on the main thread");
        this.zzbox.onEnterAmbient(bundle);
    }

    public final void onExitAmbient() {
        zzac.zzdj("onExitAmbient() must be called on the main thread");
        this.zzbox.onExitAmbient();
    }

    public final void onLowMemory() {
        this.zzbox.onLowMemory();
    }

    public final void onPause() {
        this.zzbox.onPause();
    }

    public final void onResume() {
        this.zzbox.onResume();
    }

    public final void onSaveInstanceState(Bundle bundle) {
        this.zzbox.onSaveInstanceState(bundle);
    }

    public final void onStart() {
        this.zzbox.onStart();
    }

    public final void onStop() {
        this.zzbox.onStop();
    }
}
