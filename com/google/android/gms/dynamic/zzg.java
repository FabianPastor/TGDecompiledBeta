package com.google.android.gms.dynamic;

import android.content.Context;
import android.os.IBinder;
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.common.zze;

public abstract class zzg<T> {
    private final String Qe;
    private T Qf;

    public static class zza extends Exception {
        public zza(String str) {
            super(str);
        }

        public zza(String str, Throwable th) {
            super(str, th);
        }
    }

    protected zzg(String str) {
        this.Qe = str;
    }

    protected abstract T zzc(IBinder iBinder);

    protected final T zzcr(Context context) throws zza {
        if (this.Qf == null) {
            zzaa.zzy(context);
            Context remoteContext = zze.getRemoteContext(context);
            if (remoteContext == null) {
                throw new zza("Could not get remote context.");
            }
            try {
                this.Qf = zzc((IBinder) remoteContext.getClassLoader().loadClass(this.Qe).newInstance());
            } catch (Throwable e) {
                throw new zza("Could not load creator class.", e);
            } catch (Throwable e2) {
                throw new zza("Could not instantiate creator.", e2);
            } catch (Throwable e22) {
                throw new zza("Could not access creator.", e22);
            }
        }
        return this.Qf;
    }
}
