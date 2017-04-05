package com.google.android.gms.maps.model;

import android.os.RemoteException;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.maps.model.internal.zze;

public final class IndoorLevel {
    private final zze zzbpv;

    public IndoorLevel(zze com_google_android_gms_maps_model_internal_zze) {
        this.zzbpv = (zze) zzac.zzw(com_google_android_gms_maps_model_internal_zze);
    }

    public void activate() {
        try {
            this.zzbpv.activate();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof IndoorLevel)) {
            return false;
        }
        try {
            return this.zzbpv.zza(((IndoorLevel) obj).zzbpv);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public String getName() {
        try {
            return this.zzbpv.getName();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public String getShortName() {
        try {
            return this.zzbpv.getShortName();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public int hashCode() {
        try {
            return this.zzbpv.hashCodeRemote();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }
}
