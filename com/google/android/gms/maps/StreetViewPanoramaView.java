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
import com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate;
import com.google.android.gms.maps.internal.IStreetViewPanoramaViewDelegate;
import com.google.android.gms.maps.internal.StreetViewLifecycleDelegate;
import com.google.android.gms.maps.internal.zzah;
import com.google.android.gms.maps.internal.zzai;
import com.google.android.gms.maps.model.RuntimeRemoteException;
import java.util.ArrayList;
import java.util.List;

public class StreetViewPanoramaView extends FrameLayout {
    private final zzb zzboQ;

    static class zzb extends com.google.android.gms.dynamic.zza<zza> {
        private final Context mContext;
        private final List<OnStreetViewPanoramaReadyCallback> zzboI = new ArrayList();
        private final StreetViewPanoramaOptions zzboT;
        protected zze<zza> zzbor;
        private final ViewGroup zzbox;

        zzb(ViewGroup viewGroup, Context context, StreetViewPanoramaOptions streetViewPanoramaOptions) {
            this.zzbox = viewGroup;
            this.mContext = context;
            this.zzboT = streetViewPanoramaOptions;
        }

        public void getStreetViewPanoramaAsync(OnStreetViewPanoramaReadyCallback onStreetViewPanoramaReadyCallback) {
            if (zzBN() != null) {
                ((zza) zzBN()).getStreetViewPanoramaAsync(onStreetViewPanoramaReadyCallback);
            } else {
                this.zzboI.add(onStreetViewPanoramaReadyCallback);
            }
        }

        public void zzJz() {
            if (this.zzbor != null && zzBN() == null) {
                try {
                    this.zzbor.zza(new zza(this.zzbox, zzai.zzbI(this.mContext).zza(zzd.zzA(this.mContext), this.zzboT)));
                    for (OnStreetViewPanoramaReadyCallback streetViewPanoramaAsync : this.zzboI) {
                        ((zza) zzBN()).getStreetViewPanoramaAsync(streetViewPanoramaAsync);
                    }
                    this.zzboI.clear();
                } catch (RemoteException e) {
                    throw new RuntimeRemoteException(e);
                } catch (GooglePlayServicesNotAvailableException e2) {
                }
            }
        }

        protected void zza(zze<zza> com_google_android_gms_dynamic_zze_com_google_android_gms_maps_StreetViewPanoramaView_zza) {
            this.zzbor = com_google_android_gms_dynamic_zze_com_google_android_gms_maps_StreetViewPanoramaView_zza;
            zzJz();
        }
    }

    static class zza implements StreetViewLifecycleDelegate {
        private final IStreetViewPanoramaViewDelegate zzboR;
        private View zzboS;
        private final ViewGroup zzbou;

        public zza(ViewGroup viewGroup, IStreetViewPanoramaViewDelegate iStreetViewPanoramaViewDelegate) {
            this.zzboR = (IStreetViewPanoramaViewDelegate) zzac.zzw(iStreetViewPanoramaViewDelegate);
            this.zzbou = (ViewGroup) zzac.zzw(viewGroup);
        }

        public void getStreetViewPanoramaAsync(final OnStreetViewPanoramaReadyCallback onStreetViewPanoramaReadyCallback) {
            try {
                this.zzboR.getStreetViewPanoramaAsync(new com.google.android.gms.maps.internal.zzaf.zza(this) {
                    public void zza(IStreetViewPanoramaDelegate iStreetViewPanoramaDelegate) throws RemoteException {
                        onStreetViewPanoramaReadyCallback.onStreetViewPanoramaReady(new StreetViewPanorama(iStreetViewPanoramaDelegate));
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
                this.zzboR.onCreate(bundle2);
                zzah.zzd(bundle2, bundle);
                this.zzboS = (View) zzd.zzF(this.zzboR.getView());
                this.zzbou.removeAllViews();
                this.zzbou.addView(this.zzboS);
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
            throw new UnsupportedOperationException("onCreateView not allowed on StreetViewPanoramaViewDelegate");
        }

        public void onDestroy() {
            try {
                this.zzboR.onDestroy();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public void onDestroyView() {
            throw new UnsupportedOperationException("onDestroyView not allowed on StreetViewPanoramaViewDelegate");
        }

        public void onInflate(Activity activity, Bundle bundle, Bundle bundle2) {
            throw new UnsupportedOperationException("onInflate not allowed on StreetViewPanoramaViewDelegate");
        }

        public void onLowMemory() {
            try {
                this.zzboR.onLowMemory();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public void onPause() {
            try {
                this.zzboR.onPause();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public void onResume() {
            try {
                this.zzboR.onResume();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public void onSaveInstanceState(Bundle bundle) {
            try {
                Bundle bundle2 = new Bundle();
                zzah.zzd(bundle, bundle2);
                this.zzboR.onSaveInstanceState(bundle2);
                zzah.zzd(bundle2, bundle);
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public void onStart() {
        }

        public void onStop() {
        }
    }

    public StreetViewPanoramaView(Context context) {
        super(context);
        this.zzboQ = new zzb(this, context, null);
    }

    public StreetViewPanoramaView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.zzboQ = new zzb(this, context, null);
    }

    public StreetViewPanoramaView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.zzboQ = new zzb(this, context, null);
    }

    public StreetViewPanoramaView(Context context, StreetViewPanoramaOptions streetViewPanoramaOptions) {
        super(context);
        this.zzboQ = new zzb(this, context, streetViewPanoramaOptions);
    }

    public void getStreetViewPanoramaAsync(OnStreetViewPanoramaReadyCallback onStreetViewPanoramaReadyCallback) {
        zzac.zzdj("getStreetViewPanoramaAsync() must be called on the main thread");
        this.zzboQ.getStreetViewPanoramaAsync(onStreetViewPanoramaReadyCallback);
    }

    public final void onCreate(Bundle bundle) {
        this.zzboQ.onCreate(bundle);
        if (this.zzboQ.zzBN() == null) {
            com.google.android.gms.dynamic.zza.zzb((FrameLayout) this);
        }
    }

    public final void onDestroy() {
        this.zzboQ.onDestroy();
    }

    public final void onLowMemory() {
        this.zzboQ.onLowMemory();
    }

    public final void onPause() {
        this.zzboQ.onPause();
    }

    public final void onResume() {
        this.zzboQ.onResume();
    }

    public final void onSaveInstanceState(Bundle bundle) {
        this.zzboQ.onSaveInstanceState(bundle);
    }
}
