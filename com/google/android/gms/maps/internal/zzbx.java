package com.google.android.gms.maps.internal;

import android.content.Context;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.dynamic.zzn;
import com.google.android.gms.maps.model.RuntimeRemoteException;

public class zzbx {
    private static Context zzbmZ;
    private static zze zzbna;

    private static <T> T zza(ClassLoader classLoader, String str) {
        try {
            return zzd(((ClassLoader) zzbo.zzu(classLoader)).loadClass(str));
        } catch (ClassNotFoundException e) {
            String str2 = "Unable to find dynamic class ";
            String valueOf = String.valueOf(str);
            throw new IllegalStateException(valueOf.length() != 0 ? str2.concat(valueOf) : new String(str2));
        }
    }

    public static zze zzbh(Context context) throws GooglePlayServicesNotAvailableException {
        zzbo.zzu(context);
        if (zzbna != null) {
            return zzbna;
        }
        int isGooglePlayServicesAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        switch (isGooglePlayServicesAvailable) {
            case 0:
                zze com_google_android_gms_maps_internal_zze;
                Log.i(zzbx.class.getSimpleName(), "Making Creator dynamically");
                IBinder iBinder = (IBinder) zza(zzbi(context).getClassLoader(), "com.google.android.gms.maps.internal.CreatorImpl");
                if (iBinder == null) {
                    com_google_android_gms_maps_internal_zze = null;
                } else {
                    IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.maps.internal.ICreator");
                    if (queryLocalInterface instanceof zze) {
                        com_google_android_gms_maps_internal_zze = (zze) queryLocalInterface;
                    } else {
                        Object com_google_android_gms_maps_internal_zzf = new zzf(iBinder);
                    }
                }
                zzbna = com_google_android_gms_maps_internal_zze;
                try {
                    zzbna.zzi(zzn.zzw(zzbi(context).getResources()), GooglePlayServicesUtil.GOOGLE_PLAY_SERVICES_VERSION_CODE);
                    return zzbna;
                } catch (RemoteException e) {
                    throw new RuntimeRemoteException(e);
                }
            default:
                throw new GooglePlayServicesNotAvailableException(isGooglePlayServicesAvailable);
        }
    }

    private static Context zzbi(Context context) {
        if (zzbmZ != null) {
            return zzbmZ;
        }
        Context remoteContext = GooglePlayServicesUtil.getRemoteContext(context);
        zzbmZ = remoteContext;
        return remoteContext;
    }

    private static <T> T zzd(Class<?> cls) {
        String str;
        String valueOf;
        try {
            return cls.newInstance();
        } catch (InstantiationException e) {
            str = "Unable to instantiate the dynamic class ";
            valueOf = String.valueOf(cls.getName());
            throw new IllegalStateException(valueOf.length() != 0 ? str.concat(valueOf) : new String(str));
        } catch (IllegalAccessException e2) {
            str = "Unable to call the default constructor of ";
            valueOf = String.valueOf(cls.getName());
            throw new IllegalStateException(valueOf.length() != 0 ? str.concat(valueOf) : new String(str));
        }
    }
}
