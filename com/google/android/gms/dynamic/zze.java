package com.google.android.gms.dynamic;

import android.os.IBinder;
import com.google.android.gms.dynamic.zzd.zza;
import java.lang.reflect.Field;

public final class zze<T> extends zza {
    private final T mWrappedObject;

    private zze(T t) {
        this.mWrappedObject = t;
    }

    public static <T> zzd zzac(T t) {
        return new zze(t);
    }

    public static <T> T zzae(zzd com_google_android_gms_dynamic_zzd) {
        if (com_google_android_gms_dynamic_zzd instanceof zze) {
            return ((zze) com_google_android_gms_dynamic_zzd).mWrappedObject;
        }
        IBinder asBinder = com_google_android_gms_dynamic_zzd.asBinder();
        Field[] declaredFields = asBinder.getClass().getDeclaredFields();
        if (declaredFields.length == 1) {
            Field field = declaredFields[0];
            if (field.isAccessible()) {
                throw new IllegalArgumentException("IObjectWrapper declared field not private!");
            }
            field.setAccessible(true);
            try {
                return field.get(asBinder);
            } catch (Throwable e) {
                throw new IllegalArgumentException("Binder object is null.", e);
            } catch (Throwable e2) {
                throw new IllegalArgumentException("Could not access the field in remoteBinder.", e2);
            }
        }
        throw new IllegalArgumentException("Unexpected number of IObjectWrapper declared fields: " + declaredFields.length);
    }
}
