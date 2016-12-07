package com.google.android.gms.security;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.zzc;
import com.google.android.gms.common.zze;
import java.lang.reflect.Method;

public class ProviderInstaller {
    public static final String PROVIDER_NAME = "GmsCore_OpenSSL";
    private static final zzc zzbCa = zzc.zzuz();
    private static Method zzbCb = null;
    private static final Object zztU = new Object();

    class AnonymousClass1 extends AsyncTask<Void, Void, Integer> {
        final /* synthetic */ ProviderInstallListener zzbCc;
        final /* synthetic */ Context zztd;

        AnonymousClass1(Context context, ProviderInstallListener providerInstallListener) {
            this.zztd = context;
            this.zzbCc = providerInstallListener;
        }

        protected /* synthetic */ Object doInBackground(Object[] objArr) {
            return zzc((Void[]) objArr);
        }

        protected /* synthetic */ void onPostExecute(Object obj) {
            zzg((Integer) obj);
        }

        protected Integer zzc(Void... voidArr) {
            try {
                ProviderInstaller.installIfNeeded(this.zztd);
                return Integer.valueOf(0);
            } catch (GooglePlayServicesRepairableException e) {
                return Integer.valueOf(e.getConnectionStatusCode());
            } catch (GooglePlayServicesNotAvailableException e2) {
                return Integer.valueOf(e2.errorCode);
            }
        }

        protected void zzg(Integer num) {
            if (num.intValue() == 0) {
                this.zzbCc.onProviderInstalled();
                return;
            }
            this.zzbCc.onProviderInstallFailed(num.intValue(), ProviderInstaller.zzbCa.zzb(this.zztd, num.intValue(), "pi"));
        }
    }

    public interface ProviderInstallListener {
        void onProviderInstallFailed(int i, Intent intent);

        void onProviderInstalled();
    }

    public static void installIfNeeded(Context context) throws GooglePlayServicesRepairableException, GooglePlayServicesNotAvailableException {
        zzac.zzb((Object) context, (Object) "Context must not be null");
        zzbCa.zzam(context);
        Context remoteContext = zze.getRemoteContext(context);
        if (remoteContext == null) {
            Log.e("ProviderInstaller", "Failed to get remote context");
            throw new GooglePlayServicesNotAvailableException(8);
        }
        synchronized (zztU) {
            try {
                if (zzbCb == null) {
                    zzbz(remoteContext);
                }
                zzbCb.invoke(null, new Object[]{remoteContext});
            } catch (Exception e) {
                String str = "ProviderInstaller";
                String str2 = "Failed to install provider: ";
                String valueOf = String.valueOf(e.getMessage());
                Log.e(str, valueOf.length() != 0 ? str2.concat(valueOf) : new String(str2));
                throw new GooglePlayServicesNotAvailableException(8);
            }
        }
    }

    public static void installIfNeededAsync(Context context, ProviderInstallListener providerInstallListener) {
        zzac.zzb((Object) context, (Object) "Context must not be null");
        zzac.zzb((Object) providerInstallListener, (Object) "Listener must not be null");
        zzac.zzdn("Must be called on the UI thread");
        new AnonymousClass1(context, providerInstallListener).execute(new Void[0]);
    }

    private static void zzbz(Context context) throws ClassNotFoundException, NoSuchMethodException {
        zzbCb = context.getClassLoader().loadClass("com.google.android.gms.common.security.ProviderInstallerImpl").getMethod("insertProvider", new Class[]{Context.class});
    }
}
