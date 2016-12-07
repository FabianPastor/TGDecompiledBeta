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
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.dynamic.zze;
import com.google.android.gms.dynamic.zzf;
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
    private final zzb aoJ = new zzb(this);

    static class zzb extends com.google.android.gms.dynamic.zza<zza> {
        private final Fragment Qd;
        protected zzf<zza> aoN;
        private final List<OnMapReadyCallback> aoO = new ArrayList();
        private Activity mActivity;

        zzb(Fragment fragment) {
            this.Qd = fragment;
        }

        private void setActivity(Activity activity) {
            this.mActivity = activity;
            zzbsp();
        }

        public void getMapAsync(OnMapReadyCallback onMapReadyCallback) {
            if (zzbdo() != null) {
                ((zza) zzbdo()).getMapAsync(onMapReadyCallback);
            } else {
                this.aoO.add(onMapReadyCallback);
            }
        }

        public void onEnterAmbient(Bundle bundle) {
            if (zzbdo() != null) {
                ((zza) zzbdo()).onEnterAmbient(bundle);
            }
        }

        public void onExitAmbient() {
            if (zzbdo() != null) {
                ((zza) zzbdo()).onExitAmbient();
            }
        }

        protected void zza(zzf<zza> com_google_android_gms_dynamic_zzf_com_google_android_gms_maps_MapFragment_zza) {
            this.aoN = com_google_android_gms_dynamic_zzf_com_google_android_gms_maps_MapFragment_zza;
            zzbsp();
        }

        public void zzbsp() {
            if (this.mActivity != null && this.aoN != null && zzbdo() == null) {
                try {
                    MapsInitializer.initialize(this.mActivity);
                    IMapFragmentDelegate zzah = zzai.zzdm(this.mActivity).zzah(zze.zzac(this.mActivity));
                    if (zzah != null) {
                        this.aoN.zza(new zza(this.Qd, zzah));
                        for (OnMapReadyCallback mapAsync : this.aoO) {
                            ((zza) zzbdo()).getMapAsync(mapAsync);
                        }
                        this.aoO.clear();
                    }
                } catch (RemoteException e) {
                    throw new RuntimeRemoteException(e);
                } catch (GooglePlayServicesNotAvailableException e2) {
                }
            }
        }
    }

    static class zza implements MapLifecycleDelegate {
        private final Fragment Qd;
        private final IMapFragmentDelegate aoK;

        public zza(Fragment fragment, IMapFragmentDelegate iMapFragmentDelegate) {
            this.aoK = (IMapFragmentDelegate) zzaa.zzy(iMapFragmentDelegate);
            this.Qd = (Fragment) zzaa.zzy(fragment);
        }

        public void getMapAsync(final OnMapReadyCallback onMapReadyCallback) {
            try {
                this.aoK.getMapAsync(new com.google.android.gms.maps.internal.zzt.zza(this) {
                    final /* synthetic */ zza aoM;

                    public void zza(IGoogleMapDelegate iGoogleMapDelegate) throws RemoteException {
                        onMapReadyCallback.onMapReady(new GoogleMap(iGoogleMapDelegate));
                    }
                });
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public void onCreate(Bundle bundle) {
            if (bundle == null) {
                try {
                    bundle = new Bundle();
                } catch (RemoteException e) {
                    throw new RuntimeRemoteException(e);
                }
            }
            Bundle arguments = this.Qd.getArguments();
            if (arguments != null && arguments.containsKey("MapOptions")) {
                zzah.zza(bundle, "MapOptions", arguments.getParcelable("MapOptions"));
            }
            this.aoK.onCreate(bundle);
        }

        public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
            try {
                return (View) zze.zzae(this.aoK.onCreateView(zze.zzac(layoutInflater), zze.zzac(viewGroup), bundle));
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public void onDestroy() {
            try {
                this.aoK.onDestroy();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public void onDestroyView() {
            try {
                this.aoK.onDestroyView();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public void onEnterAmbient(Bundle bundle) {
            try {
                this.aoK.onEnterAmbient(bundle);
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public void onExitAmbient() {
            try {
                this.aoK.onExitAmbient();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public void onInflate(Activity activity, Bundle bundle, Bundle bundle2) {
            try {
                this.aoK.onInflate(zze.zzac(activity), (GoogleMapOptions) bundle.getParcelable("MapOptions"), bundle2);
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public void onLowMemory() {
            try {
                this.aoK.onLowMemory();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public void onPause() {
            try {
                this.aoK.onPause();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public void onResume() {
            try {
                this.aoK.onResume();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public void onSaveInstanceState(Bundle bundle) {
            try {
                this.aoK.onSaveInstanceState(bundle);
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public void onStart() {
            try {
                this.aoK.onStart();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public void onStop() {
            try {
                this.aoK.onStop();
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
        zzaa.zzhs("getMapAsync must be called on the main thread.");
        this.aoJ.getMapAsync(onMapReadyCallback);
    }

    public void onActivityCreated(Bundle bundle) {
        if (bundle != null) {
            bundle.setClassLoader(MapFragment.class.getClassLoader());
        }
        super.onActivityCreated(bundle);
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.aoJ.setActivity(activity);
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.aoJ.onCreate(bundle);
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View onCreateView = this.aoJ.onCreateView(layoutInflater, viewGroup, bundle);
        onCreateView.setClickable(true);
        return onCreateView;
    }

    public void onDestroy() {
        this.aoJ.onDestroy();
        super.onDestroy();
    }

    public void onDestroyView() {
        this.aoJ.onDestroyView();
        super.onDestroyView();
    }

    public final void onEnterAmbient(Bundle bundle) {
        zzaa.zzhs("onEnterAmbient must be called on the main thread.");
        this.aoJ.onEnterAmbient(bundle);
    }

    public final void onExitAmbient() {
        zzaa.zzhs("onExitAmbient must be called on the main thread.");
        this.aoJ.onExitAmbient();
    }

    @SuppressLint({"NewApi"})
    public void onInflate(Activity activity, AttributeSet attributeSet, Bundle bundle) {
        super.onInflate(activity, attributeSet, bundle);
        this.aoJ.setActivity(activity);
        Parcelable createFromAttributes = GoogleMapOptions.createFromAttributes(activity, attributeSet);
        Bundle bundle2 = new Bundle();
        bundle2.putParcelable("MapOptions", createFromAttributes);
        this.aoJ.onInflate(activity, bundle2, bundle);
    }

    public void onLowMemory() {
        this.aoJ.onLowMemory();
        super.onLowMemory();
    }

    public void onPause() {
        this.aoJ.onPause();
        super.onPause();
    }

    public void onResume() {
        super.onResume();
        this.aoJ.onResume();
    }

    public void onSaveInstanceState(Bundle bundle) {
        if (bundle != null) {
            bundle.setClassLoader(MapFragment.class.getClassLoader());
        }
        super.onSaveInstanceState(bundle);
        this.aoJ.onSaveInstanceState(bundle);
    }

    public void onStart() {
        super.onStart();
        this.aoJ.onStart();
    }

    public void onStop() {
        this.aoJ.onStop();
        super.onStop();
    }

    public void setArguments(Bundle bundle) {
        super.setArguments(bundle);
    }
}
