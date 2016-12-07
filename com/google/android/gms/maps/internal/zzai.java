package com.google.android.gms.maps.internal;

import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.dynamic.zze;
import com.google.android.gms.maps.internal.zzc.zza;
import com.google.android.gms.maps.model.RuntimeRemoteException;

public class zzai {
    private static Context amt;
    private static zzc amu;

    private static Context getRemoteContext(Context context) {
        if (amt == null) {
            if (zzbse()) {
                amt = context.getApplicationContext();
            } else {
                amt = GooglePlayServicesUtil.getRemoteContext(context);
            }
        }
        return amt;
    }

    private static <T> T zza(ClassLoader classLoader, String str) {
        try {
            return zzf(((ClassLoader) zzac.zzy(classLoader)).loadClass(str));
        } catch (ClassNotFoundException e) {
            String str2 = "Unable to find dynamic class ";
            String valueOf = String.valueOf(str);
            throw new IllegalStateException(valueOf.length() != 0 ? str2.concat(valueOf) : new String(str2));
        }
    }

    public static boolean zzbse() {
        return false;
    }

    private static Class<?> zzbsf() {
        try {
            return Class.forName("com.google.android.gms.maps.internal.CreatorImpl");
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static zzc zzdp(Context context) throws GooglePlayServicesNotAvailableException {
        zzac.zzy(context);
        if (amu != null) {
            return amu;
        }
        zzdq(context);
        amu = zzdr(context);
        try {
            amu.zzh(zze.zzac(getRemoteContext(context).getResources()), GooglePlayServicesUtil.GOOGLE_PLAY_SERVICES_VERSION_CODE);
            return amu;
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    private static void zzdq(Context context) throws GooglePlayServicesNotAvailableException {
        int isGooglePlayServicesAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        switch (isGooglePlayServicesAvailable) {
            case 0:
                return;
            default:
                throw new GooglePlayServicesNotAvailableException(isGooglePlayServicesAvailable);
        }
    }

    private static zzc zzdr(Context context) {
        if (zzbse()) {
            Log.i(zzai.class.getSimpleName(), "Making Creator statically");
            return (zzc) zzf(zzbsf());
        }
        Log.i(zzai.class.getSimpleName(), "Making Creator dynamically");
        return zza.zzhn((IBinder) zza(getRemoteContext(context).getClassLoader(), "com.google.android.gms.maps.internal.CreatorImpl"));
    }

    private static <T> T zzf(Class<?> cls) {
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
