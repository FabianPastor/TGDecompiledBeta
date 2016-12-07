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
import com.google.android.gms.dynamic.zze;
import com.google.android.gms.dynamic.zzf;
import com.google.android.gms.maps.internal.IGoogleMapDelegate;
import com.google.android.gms.maps.internal.IMapViewDelegate;
import com.google.android.gms.maps.internal.MapLifecycleDelegate;
import com.google.android.gms.maps.internal.zzai;
import com.google.android.gms.maps.model.RuntimeRemoteException;
import java.util.ArrayList;
import java.util.List;

public class MapView extends FrameLayout {
    private final zzb zzbnV;

    static class zzb extends com.google.android.gms.dynamic.zza<zza> {
        private final Context mContext;
        protected zzf<zza> zzbnT;
        private final List<OnMapReadyCallback> zzbnU = new ArrayList();
        private final ViewGroup zzbnZ;
        private final GoogleMapOptions zzboa;

        zzb(ViewGroup viewGroup, Context context, GoogleMapOptions googleMapOptions) {
            this.zzbnZ = viewGroup;
            this.mContext = context;
            this.zzboa = googleMapOptions;
        }

        public void getMapAsync(OnMapReadyCallback onMapReadyCallback) {
            if (zzAY() != null) {
                ((zza) zzAY()).getMapAsync(onMapReadyCallback);
            } else {
                this.zzbnU.add(onMapReadyCallback);
            }
        }

        public void onEnterAmbient(Bundle bundle) {
            if (zzAY() != null) {
                ((zza) zzAY()).onEnterAmbient(bundle);
            }
        }

        public void onExitAmbient() {
            if (zzAY() != null) {
                ((zza) zzAY()).onExitAmbient();
            }
        }

        public void zzIL() {
            if (this.zzbnT != null && zzAY() == null) {
                try {
                    MapsInitializer.initialize(this.mContext);
                    IMapViewDelegate zza = zzai.zzbq(this.mContext).zza(zze.zzA(this.mContext), this.zzboa);
                    if (zza != null) {
                        this.zzbnT.zza(new zza(this.zzbnZ, zza));
                        for (OnMapReadyCallback mapAsync : this.zzbnU) {
                            ((zza) zzAY()).getMapAsync(mapAsync);
                        }
                        this.zzbnU.clear();
                    }
                } catch (RemoteException e) {
                    throw new RuntimeRemoteException(e);
                } catch (GooglePlayServicesNotAvailableException e2) {
                }
            }
        }

        protected void zza(zzf<zza> com_google_android_gms_dynamic_zzf_com_google_android_gms_maps_MapView_zza) {
            this.zzbnT = com_google_android_gms_dynamic_zzf_com_google_android_gms_maps_MapView_zza;
            zzIL();
        }
    }

    static class zza implements MapLifecycleDelegate {
        private final ViewGroup zzbnW;
        private final IMapViewDelegate zzbnX;
        private View zzbnY;

        public zza(ViewGroup viewGroup, IMapViewDelegate iMapViewDelegate) {
            this.zzbnX = (IMapViewDelegate) zzac.zzw(iMapViewDelegate);
            this.zzbnW = (ViewGroup) zzac.zzw(viewGroup);
        }

        public void getMapAsync(final OnMapReadyCallback onMapReadyCallback) {
            try {
                this.zzbnX.getMapAsync(new com.google.android.gms.maps.internal.zzt.zza(this) {
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
                this.zzbnX.onCreate(bundle);
                this.zzbnY = (View) zze.zzE(this.zzbnX.getView());
                this.zzbnW.removeAllViews();
                this.zzbnW.addView(this.zzbnY);
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
            throw new UnsupportedOperationException("onCreateView not allowed on MapViewDelegate");
        }

        public void onDestroy() {
            try {
                this.zzbnX.onDestroy();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public void onDestroyView() {
            throw new UnsupportedOperationException("onDestroyView not allowed on MapViewDelegate");
        }

        public void onEnterAmbient(Bundle bundle) {
            try {
                this.zzbnX.onEnterAmbient(bundle);
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public void onExitAmbient() {
            try {
                this.zzbnX.onExitAmbient();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public void onInflate(Activity activity, Bundle bundle, Bundle bundle2) {
            throw new UnsupportedOperationException("onInflate not allowed on MapViewDelegate");
        }

        public void onLowMemory() {
            try {
                this.zzbnX.onLowMemory();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public void onPause() {
            try {
                this.zzbnX.onPause();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public void onResume() {
            try {
                this.zzbnX.onResume();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public void onSaveInstanceState(Bundle bundle) {
            try {
                this.zzbnX.onSaveInstanceState(bundle);
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public void onStart() {
            try {
                this.zzbnX.onStart();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public void onStop() {
            try {
                this.zzbnX.onStop();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
    }

    public MapView(Context context) {
        super(context);
        this.zzbnV = new zzb(this, context, null);
        zzIM();
    }

    public MapView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.zzbnV = new zzb(this, context, GoogleMapOptions.createFromAttributes(context, attributeSet));
        zzIM();
    }

    public MapView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.zzbnV = new zzb(this, context, GoogleMapOptions.createFromAttributes(context, attributeSet));
        zzIM();
    }

    public MapView(Context context, GoogleMapOptions googleMapOptions) {
        super(context);
        this.zzbnV = new zzb(this, context, googleMapOptions);
        zzIM();
    }

    private void zzIM() {
        setClickable(true);
    }

    public void getMapAsync(OnMapReadyCallback onMapReadyCallback) {
        zzac.zzdn("getMapAsync() must be called on the main thread");
        this.zzbnV.getMapAsync(onMapReadyCallback);
    }

    public final void onCreate(Bundle bundle) {
        this.zzbnV.onCreate(bundle);
        if (this.zzbnV.zzAY() == null) {
            com.google.android.gms.dynamic.zza.zzb((FrameLayout) this);
        }
    }

    public final void onDestroy() {
        this.zzbnV.onDestroy();
    }

    public final void onEnterAmbient(Bundle bundle) {
        zzac.zzdn("onEnterAmbient() must be called on the main thread");
        this.zzbnV.onEnterAmbient(bundle);
    }

    public final void onExitAmbient() {
        zzac.zzdn("onExitAmbient() must be called on the main thread");
        this.zzbnV.onExitAmbient();
    }

    public final void onLowMemory() {
        this.zzbnV.onLowMemory();
    }

    public final void onPause() {
        this.zzbnV.onPause();
    }

    public final void onResume() {
        this.zzbnV.onResume();
    }

    public final void onSaveInstanceState(Bundle bundle) {
        this.zzbnV.onSaveInstanceState(bundle);
    }

    public final void onStart() {
        this.zzbnV.onStart();
    }

    public final void onStop() {
        this.zzbnV.onStop();
    }
}
