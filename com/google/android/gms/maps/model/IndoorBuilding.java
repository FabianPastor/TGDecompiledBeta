package com.google.android.gms.maps.model;

import android.os.IBinder;
import android.os.RemoteException;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.maps.model.internal.zzd;
import com.google.android.gms.maps.model.internal.zze.zza;
import java.util.ArrayList;
import java.util.List;

public final class IndoorBuilding {
    private final zzd zzbpu;

    public IndoorBuilding(zzd com_google_android_gms_maps_model_internal_zzd) {
        this.zzbpu = (zzd) zzac.zzw(com_google_android_gms_maps_model_internal_zzd);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof IndoorBuilding)) {
            return false;
        }
        try {
            return this.zzbpu.zzb(((IndoorBuilding) obj).zzbpu);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public int getActiveLevelIndex() {
        try {
            return this.zzbpu.getActiveLevelIndex();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public int getDefaultLevelIndex() {
        try {
            return this.zzbpu.getActiveLevelIndex();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public List<IndoorLevel> getLevels() {
        try {
            List<IBinder> levels = this.zzbpu.getLevels();
            List<IndoorLevel> arrayList = new ArrayList(levels.size());
            for (IBinder zzem : levels) {
                arrayList.add(new IndoorLevel(zza.zzem(zzem)));
            }
            return arrayList;
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public int hashCode() {
        try {
            return this.zzbpu.hashCodeRemote();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public boolean isUnderground() {
        try {
            return this.zzbpu.isUnderground();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }
}
