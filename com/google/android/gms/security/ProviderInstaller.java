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
    private static final zzc azV = zzc.zzapd();
    private static Method azW = null;
    private static final Object zzaok = new Object();

    class AnonymousClass1 extends AsyncTask<Void, Void, Integer> {
        final /* synthetic */ ProviderInstallListener azX;
        final /* synthetic */ Context zzamt;

        AnonymousClass1(Context context, ProviderInstallListener providerInstallListener) {
            this.zzamt = context;
            this.azX = providerInstallListener;
        }

        protected /* synthetic */ Object doInBackground(Object[] objArr) {
            return zzb((Void[]) objArr);
        }

        protected /* synthetic */ void onPostExecute(Object obj) {
            zzg((Integer) obj);
        }

        protected Integer zzb(Void... voidArr) {
            try {
                ProviderInstaller.installIfNeeded(this.zzamt);
                return Integer.valueOf(0);
            } catch (GooglePlayServicesRepairableException e) {
                return Integer.valueOf(e.getConnectionStatusCode());
            } catch (GooglePlayServicesNotAvailableException e2) {
                return Integer.valueOf(e2.errorCode);
            }
        }

        protected void zzg(Integer num) {
            if (num.intValue() == 0) {
                this.azX.onProviderInstalled();
                return;
            }
            this.azX.onProviderInstallFailed(num.intValue(), ProviderInstaller.azV.zza(this.zzamt, num.intValue(), "pi"));
        }
    }

    public interface ProviderInstallListener {
        void onProviderInstallFailed(int i, Intent intent);

        void onProviderInstalled();
    }

    public static void installIfNeeded(Context context) throws GooglePlayServicesRepairableException, GooglePlayServicesNotAvailableException {
        zzac.zzb((Object) context, (Object) "Context must not be null");
        azV.zzbp(context);
        Context remoteContext = zze.getRemoteContext(context);
        if (remoteContext == null) {
            Log.e("ProviderInstaller", "Failed to get remote context");
            throw new GooglePlayServicesNotAvailableException(8);
        }
        synchronized (zzaok) {
            try {
                if (azW == null) {
                    zzdy(remoteContext);
                }
                azW.invoke(null, new Object[]{remoteContext});
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
        zzac.zzhq("Must be called on the UI thread");
        new AnonymousClass1(context, providerInstallListener).execute(new Void[0]);
    }

    private static void zzdy(Context context) throws ClassNotFoundException, NoSuchMethodException {
        azW = context.getClassLoader().loadClass("com.google.android.gms.common.security.ProviderInstallerImpl").getMethod("insertProvider", new Class[]{Context.class});
    }
}
