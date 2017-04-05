package com.google.android.gms.maps;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.dynamic.zzd;
import com.google.android.gms.dynamic.zze;
import com.google.android.gms.maps.internal.IGoogleMapDelegate;
import com.google.android.gms.maps.internal.IMapFragmentDelegate;
import com.google.android.gms.maps.internal.MapLifecycleDelegate;
import com.google.android.gms.maps.internal.zzah;
import com.google.android.gms.maps.internal.zzai;
import com.google.android.gms.maps.model.RuntimeRemoteException;
import java.util.ArrayList;
import java.util.List;

@TargetApi(11)
public class MapFragment extends Fragment {
    private final zzb zzboo = new zzb(this);

    static class zzb extends com.google.android.gms.dynamic.zza<zza> {
        private Activity mActivity;
        private final Fragment zzaRK;
        protected zze<zza> zzbor;
        private final List<OnMapReadyCallback> zzbos = new ArrayList();

        zzb(Fragment fragment) {
            this.zzaRK = fragment;
        }

        private void setActivity(Activity activity) {
            this.mActivity = activity;
            zzJz();
        }

        public void getMapAsync(OnMapReadyCallback onMapReadyCallback) {
            if (zzBN() != null) {
                ((zza) zzBN()).getMapAsync(onMapReadyCallback);
            } else {
                this.zzbos.add(onMapReadyCallback);
            }
        }

        public void onEnterAmbient(Bundle bundle) {
            if (zzBN() != null) {
                ((zza) zzBN()).onEnterAmbient(bundle);
            }
        }

        public void onExitAmbient() {
            if (zzBN() != null) {
                ((zza) zzBN()).onExitAmbient();
            }
        }

        public void zzJz() {
            if (this.mActivity != null && this.zzbor != null && zzBN() == null) {
                try {
                    MapsInitializer.initialize(this.mActivity);
                    IMapFragmentDelegate zzI = zzai.zzbI(this.mActivity).zzI(zzd.zzA(this.mActivity));
                    if (zzI != null) {
                        this.zzbor.zza(new zza(this.zzaRK, zzI));
                        for (OnMapReadyCallback mapAsync : this.zzbos) {
                            ((zza) zzBN()).getMapAsync(mapAsync);
                        }
                        this.zzbos.clear();
                    }
                } catch (RemoteException e) {
                    throw new RuntimeRemoteException(e);
                } catch (GooglePlayServicesNotAvailableException e2) {
                }
            }
        }

        protected void zza(zze<zza> com_google_android_gms_dynamic_zze_com_google_android_gms_maps_MapFragment_zza) {
            this.zzbor = com_google_android_gms_dynamic_zze_com_google_android_gms_maps_MapFragment_zza;
            zzJz();
        }
    }

    static class zza implements MapLifecycleDelegate {
        private final Fragment zzaRK;
        private final IMapFragmentDelegate zzbop;

        public zza(Fragment fragment, IMapFragmentDelegate iMapFragmentDelegate) {
            this.zzbop = (IMapFragmentDelegate) zzac.zzw(iMapFragmentDelegate);
            this.zzaRK = (Fragment) zzac.zzw(fragment);
        }

        public void getMapAsync(final OnMapReadyCallback onMapReadyCallback) {
            try {
                this.zzbop.getMapAsync(new com.google.android.gms.maps.internal.zzt.zza(this) {
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
                Bundle arguments = this.zzaRK.getArguments();
                if (arguments != null && arguments.containsKey("MapOptions")) {
                    zzah.zza(bundle2, "MapOptions", arguments.getParcelable("MapOptions"));
                }
                this.zzbop.onCreate(bundle2);
                zzah.zzd(bundle2, bundle);
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
            try {
                Bundle bundle2 = new Bundle();
                zzah.zzd(bundle, bundle2);
                IObjectWrapper onCreateView = this.zzbop.onCreateView(zzd.zzA(layoutInflater), zzd.zzA(viewGroup), bundle2);
                zzah.zzd(bundle2, bundle);
                return (View) zzd.zzF(onCreateView);
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public void onDestroy() {
            try {
                this.zzbop.onDestroy();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public void onDestroyView() {
            try {
                this.zzbop.onDestroyView();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public void onEnterAmbient(Bundle bundle) {
            try {
                Bundle bundle2 = new Bundle();
                zzah.zzd(bundle, bundle2);
                this.zzbop.onEnterAmbient(bundle2);
                zzah.zzd(bundle2, bundle);
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public void onExitAmbient() {
            try {
                this.zzbop.onExitAmbient();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public void onInflate(Activity activity, Bundle bundle, Bundle bundle2) {
            GoogleMapOptions googleMapOptions = (GoogleMapOptions) bundle.getParcelable("MapOptions");
            try {
                Bundle bundle3 = new Bundle();
                zzah.zzd(bundle2, bundle3);
                this.zzbop.onInflate(zzd.zzA(activity), googleMapOptions, bundle3);
                zzah.zzd(bundle3, bundle2);
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public void onLowMemory() {
            try {
                this.zzbop.onLowMemory();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public void onPause() {
            try {
                this.zzbop.onPause();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public void onResume() {
            try {
                this.zzbop.onResume();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public void onSaveInstanceState(Bundle bundle) {
            try {
                Bundle bundle2 = new Bundle();
                zzah.zzd(bundle, bundle2);
                this.zzbop.onSaveInstanceState(bundle2);
                zzah.zzd(bundle2, bundle);
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public void onStart() {
            try {
                this.zzbop.onStart();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public void onStop() {
            try {
                this.zzbop.onStop();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
    }

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    public static MapFragment newInstance(GoogleMapOptions googleMapOptions) {
        MapFragment mapFragment = new MapFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("MapOptions", googleMapOptions);
        mapFragment.setArguments(bundle);
        return mapFragment;
    }

    public void getMapAsync(OnMapReadyCallback onMapReadyCallback) {
        zzac.zzdj("getMapAsync must be called on the main thread.");
        this.zzboo.getMapAsync(onMapReadyCallback);
    }

    public void onActivityCreated(Bundle bundle) {
        if (bundle != null) {
            bundle.setClassLoader(MapFragment.class.getClassLoader());
        }
        super.onActivityCreated(bundle);
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.zzboo.setActivity(activity);
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.zzboo.onCreate(bundle);
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View onCreateView = this.zzboo.onCreateView(layoutInflater, viewGroup, bundle);
        onCreateView.setClickable(true);
        return onCreateView;
    }

    public void onDestroy() {
        this.zzboo.onDestroy();
        super.onDestroy();
    }

    public void onDestroyView() {
        this.zzboo.onDestroyView();
        super.onDestroyView();
    }

    public final void onEnterAmbient(Bundle bundle) {
        zzac.zzdj("onEnterAmbient must be called on the main thread.");
        this.zzboo.onEnterAmbient(bundle);
    }

    public final void onExitAmbient() {
        zzac.zzdj("onExitAmbient must be called on the main thread.");
        this.zzboo.onExitAmbient();
    }

    @SuppressLint({"NewApi"})
    public void onInflate(Activity activity, AttributeSet attributeSet, Bundle bundle) {
        super.onInflate(activity, attributeSet, bundle);
        this.zzboo.setActivity(activity);
        Parcelable createFromAttributes = GoogleMapOptions.createFromAttributes(activity, attributeSet);
        Bundle bundle2 = new Bundle();
        bundle2.putParcelable("MapOptions", createFromAttributes);
        this.zzboo.onInflate(activity, bundle2, bundle);
    }

    public void onLowMemory() {
        this.zzboo.onLowMemory();
        super.onLowMemory();
    }

    public void onPause() {
        this.zzboo.onPause();
        super.onPause();
    }

    public void onResume() {
        super.onResume();
        this.zzboo.onResume();
    }

    public void onSaveInstanceState(Bundle bundle) {
        if (bundle != null) {
            bundle.setClassLoader(MapFragment.class.getClassLoader());
        }
        super.onSaveInstanceState(bundle);
        this.zzboo.onSaveInstanceState(bundle);
    }

    public void onStart() {
        super.onStart();
        this.zzboo.onStart();
    }

    public void onStop() {
        this.zzboo.onStop();
        super.onStop();
    }

    public void setArguments(Bundle bundle) {
        super.setArguments(bundle);
    }
}
