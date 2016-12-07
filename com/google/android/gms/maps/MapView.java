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
    private final zzb alI;

    static class zzb extends com.google.android.gms.dynamic.zza<zza> {
        protected zzf<zza> alG;
        private final List<OnMapReadyCallback> alH = new ArrayList();
        private final ViewGroup alN;
        private final GoogleMapOptions alO;
        private final Context mContext;

        zzb(ViewGroup viewGroup, Context context, GoogleMapOptions googleMapOptions) {
            this.alN = viewGroup;
            this.mContext = context;
            this.alO = googleMapOptions;
        }

        public void getMapAsync(OnMapReadyCallback onMapReadyCallback) {
            if (zzbdt() != null) {
                ((zza) zzbdt()).getMapAsync(onMapReadyCallback);
            } else {
                this.alH.add(onMapReadyCallback);
            }
        }

        public void onEnterAmbient(Bundle bundle) {
            if (zzbdt() != null) {
                ((zza) zzbdt()).onEnterAmbient(bundle);
            }
        }

        public void onExitAmbient() {
            if (zzbdt() != null) {
                ((zza) zzbdt()).onExitAmbient();
            }
        }

        protected void zza(zzf<zza> com_google_android_gms_dynamic_zzf_com_google_android_gms_maps_MapView_zza) {
            this.alG = com_google_android_gms_dynamic_zzf_com_google_android_gms_maps_MapView_zza;
            zzbru();
        }

        public void zzbru() {
            if (this.alG != null && zzbdt() == null) {
                try {
                    MapsInitializer.initialize(this.mContext);
                    IMapViewDelegate zza = zzai.zzdp(this.mContext).zza(zze.zzac(this.mContext), this.alO);
                    if (zza != null) {
                        this.alG.zza(new zza(this.alN, zza));
                        for (OnMapReadyCallback mapAsync : this.alH) {
                            ((zza) zzbdt()).getMapAsync(mapAsync);
                        }
                        this.alH.clear();
                    }
                } catch (RemoteException e) {
                    throw new RuntimeRemoteException(e);
                } catch (GooglePlayServicesNotAvailableException e2) {
                }
            }
        }
    }

    static class zza implements MapLifecycleDelegate {
        private final ViewGroup alJ;
        private final IMapViewDelegate alK;
        private View alL;

        public zza(ViewGroup viewGroup, IMapViewDelegate iMapViewDelegate) {
            this.alK = (IMapViewDelegate) zzac.zzy(iMapViewDelegate);
            this.alJ = (ViewGroup) zzac.zzy(viewGroup);
        }

        public void getMapAsync(final OnMapReadyCallback onMapReadyCallback) {
            try {
                this.alK.getMapAsync(new com.google.android.gms.maps.internal.zzt.zza(this) {
                    final /* synthetic */ zza alM;

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
                this.alK.onCreate(bundle);
                this.alL = (View) zze.zzae(this.alK.getView());
                this.alJ.removeAllViews();
                this.alJ.addView(this.alL);
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
            throw new UnsupportedOperationException("onCreateView not allowed on MapViewDelegate");
        }

        public void onDestroy() {
            try {
                this.alK.onDestroy();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public void onDestroyView() {
            throw new UnsupportedOperationException("onDestroyView not allowed on MapViewDelegate");
        }

        public void onEnterAmbient(Bundle bundle) {
            try {
                this.alK.onEnterAmbient(bundle);
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public void onExitAmbient() {
            try {
                this.alK.onExitAmbient();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public void onInflate(Activity activity, Bundle bundle, Bundle bundle2) {
            throw new UnsupportedOperationException("onInflate not allowed on MapViewDelegate");
        }

        public void onLowMemory() {
            try {
                this.alK.onLowMemory();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public void onPause() {
            try {
                this.alK.onPause();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public void onResume() {
            try {
                this.alK.onResume();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public void onSaveInstanceState(Bundle bundle) {
            try {
                this.alK.onSaveInstanceState(bundle);
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public void onStart() {
            try {
                this.alK.onStart();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public void onStop() {
            try {
                this.alK.onStop();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
    }

    public MapView(Context context) {
        super(context);
        this.alI = new zzb(this, context, null);
        zzbrv();
    }

    public MapView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.alI = new zzb(this, context, GoogleMapOptions.createFromAttributes(context, attributeSet));
        zzbrv();
    }

    public MapView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.alI = new zzb(this, context, GoogleMapOptions.createFromAttributes(context, attributeSet));
        zzbrv();
    }

    public MapView(Context context, GoogleMapOptions googleMapOptions) {
        super(context);
        this.alI = new zzb(this, context, googleMapOptions);
        zzbrv();
    }

    private void zzbrv() {
        setClickable(true);
    }

    public void getMapAsync(OnMapReadyCallback onMapReadyCallback) {
        zzac.zzhq("getMapAsync() must be called on the main thread");
        this.alI.getMapAsync(onMapReadyCallback);
    }

    public final void onCreate(Bundle bundle) {
        this.alI.onCreate(bundle);
        if (this.alI.zzbdt() == null) {
            com.google.android.gms.dynamic.zza.zzb((FrameLayout) this);
        }
    }

    public final void onDestroy() {
        this.alI.onDestroy();
    }

    public final void onEnterAmbient(Bundle bundle) {
        zzac.zzhq("onEnterAmbient() must be called on the main thread");
        this.alI.onEnterAmbient(bundle);
    }

    public final void onExitAmbient() {
        zzac.zzhq("onExitAmbient() must be called on the main thread");
        this.alI.onExitAmbient();
    }

    public final void onLowMemory() {
        this.alI.onLowMemory();
    }

    public final void onPause() {
        this.alI.onPause();
    }

    public final void onResume() {
        this.alI.onResume();
    }

    public final void onSaveInstanceState(Bundle bundle) {
        this.alI.onSaveInstanceState(bundle);
    }

    public final void onStart() {
        this.alI.onStart();
    }

    public final void onStop() {
        this.alI.onStop();
    }
}
