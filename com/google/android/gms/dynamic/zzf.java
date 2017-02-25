package com.google.android.gms.dynamic;

import android.content.Context;
import android.os.IBinder;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.zzg;

public abstract class zzf<T> {
    private final String zzaRL;
    private T zzaRM;

    public static class zza extends Exception {
        public zza(String str) {
            super(str);
        }

        public zza(String str, Throwable th) {
            super(str, th);
        }
    }

    protected zzf(String str) {
        this.zzaRL = str;
    }

    protected final T zzbl(Context context) throws zza {
        if (this.zzaRM == null) {
            zzac.zzw(context);
            Context remoteContext = zzg.getRemoteContext(context);
            if (remoteContext == null) {
                throw new zza("Could not get remote context.");
            }
            try {
                this.zzaRM = zzc((IBinder) remoteContext.getClassLoader().loadClass(this.zzaRL).newInstance());
            } catch (Throwable e) {
                throw new zza("Could not load creator class.", e);
            } catch (Throwable e2) {
                throw new zza("Could not instantiate creator.", e2);
            } catch (Throwable e22) {
                throw new zza("Could not access creator.", e22);
            }
        }
        return this.zzaRM;
    }

    protected abstract T zzc(IBinder iBinder);
}
