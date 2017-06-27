package com.google.firebase;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.common.internal.zzbe;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.util.zzr;
import com.google.android.gms.internal.zs;
import com.google.android.gms.internal.zt;
import com.google.android.gms.internal.zu;
import com.google.android.gms.internal.zv;
import com.google.android.gms.internal.zzbav;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.GetTokenResult;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class FirebaseApp {
    public static final String DEFAULT_APP_NAME = "[DEFAULT]";
    private static final List<String> zzbUT = Arrays.asList(new String[]{"com.google.firebase.auth.FirebaseAuth", "com.google.firebase.iid.FirebaseInstanceId"});
    private static final List<String> zzbUU = Collections.singletonList("com.google.firebase.crash.FirebaseCrash");
    private static final List<String> zzbUV = Arrays.asList(new String[]{"com.google.android.gms.measurement.AppMeasurement"});
    private static final List<String> zzbUW = Arrays.asList(new String[0]);
    private static final Set<String> zzbUX = Collections.emptySet();
    static final Map<String, FirebaseApp> zzbgQ = new ArrayMap();
    private static final Object zzuH = new Object();
    private final Context mApplicationContext;
    private final String mName;
    private final FirebaseOptions zzbUY;
    private final AtomicBoolean zzbUZ = new AtomicBoolean(false);
    private final AtomicBoolean zzbVa = new AtomicBoolean();
    private final List<zza> zzbVb = new CopyOnWriteArrayList();
    private final List<zzc> zzbVc = new CopyOnWriteArrayList();
    private final List<Object> zzbVd = new CopyOnWriteArrayList();
    private zu zzbVe;
    private zzb zzbVf;

    public interface zza {
        void zzb(@NonNull zv zvVar);
    }

    public interface zzb {
    }

    public interface zzc {
        void zzac(boolean z);
    }

    @TargetApi(24)
    static class zzd extends BroadcastReceiver {
        private static AtomicReference<zzd> zzbVg = new AtomicReference();
        private final Context mApplicationContext;

        private zzd(Context context) {
            this.mApplicationContext = context;
        }

        private static void zzbB(Context context) {
            if (zzbVg.get() == null) {
                BroadcastReceiver com_google_firebase_FirebaseApp_zzd = new zzd(context);
                if (zzbVg.compareAndSet(null, com_google_firebase_FirebaseApp_zzd)) {
                    context.registerReceiver(com_google_firebase_FirebaseApp_zzd, new IntentFilter("android.intent.action.USER_UNLOCKED"));
                }
            }
        }

        public final void onReceive(Context context, Intent intent) {
            synchronized (FirebaseApp.zzuH) {
                for (FirebaseApp zza : FirebaseApp.zzbgQ.values()) {
                    zza.zzEs();
                }
            }
            this.mApplicationContext.unregisterReceiver(this);
        }
    }

    private FirebaseApp(Context context, String str, FirebaseOptions firebaseOptions) {
        this.mApplicationContext = (Context) zzbo.zzu(context);
        this.mName = zzbo.zzcF(str);
        this.zzbUY = (FirebaseOptions) zzbo.zzu(firebaseOptions);
        this.zzbVf = new zs();
    }

    public static List<FirebaseApp> getApps(Context context) {
        List<FirebaseApp> arrayList;
        zt.zzbL(context);
        synchronized (zzuH) {
            arrayList = new ArrayList(zzbgQ.values());
            zt.zzJW();
            Set<String> zzJX = zt.zzJX();
            zzJX.removeAll(zzbgQ.keySet());
            for (String str : zzJX) {
                zt.zzhq(str);
                arrayList.add(initializeApp(context, null, str));
            }
        }
        return arrayList;
    }

    @Nullable
    public static FirebaseApp getInstance() {
        FirebaseApp firebaseApp;
        synchronized (zzuH) {
            firebaseApp = (FirebaseApp) zzbgQ.get(DEFAULT_APP_NAME);
            if (firebaseApp == null) {
                String valueOf = String.valueOf(zzr.zzsf());
                throw new IllegalStateException(new StringBuilder(String.valueOf(valueOf).length() + 116).append("Default FirebaseApp is not initialized in this process ").append(valueOf).append(". Make sure to call FirebaseApp.initializeApp(Context) first.").toString());
            }
        }
        return firebaseApp;
    }

    public static FirebaseApp getInstance(@NonNull String str) {
        FirebaseApp firebaseApp;
        synchronized (zzuH) {
            firebaseApp = (FirebaseApp) zzbgQ.get(str.trim());
            if (firebaseApp != null) {
            } else {
                String str2;
                Iterable zzEr = zzEr();
                if (zzEr.isEmpty()) {
                    str2 = "";
                } else {
                    String str3 = "Available app names: ";
                    str2 = String.valueOf(TextUtils.join(", ", zzEr));
                    str2 = str2.length() != 0 ? str3.concat(str2) : new String(str3);
                }
                throw new IllegalStateException(String.format("FirebaseApp with name %s doesn't exist. %s", new Object[]{str, str2}));
            }
        }
        return firebaseApp;
    }

    @Nullable
    public static FirebaseApp initializeApp(Context context) {
        FirebaseApp instance;
        synchronized (zzuH) {
            if (zzbgQ.containsKey(DEFAULT_APP_NAME)) {
                instance = getInstance();
            } else {
                FirebaseOptions fromResource = FirebaseOptions.fromResource(context);
                if (fromResource == null) {
                    instance = null;
                } else {
                    instance = initializeApp(context, fromResource);
                }
            }
        }
        return instance;
    }

    public static FirebaseApp initializeApp(Context context, FirebaseOptions firebaseOptions) {
        return initializeApp(context, firebaseOptions, DEFAULT_APP_NAME);
    }

    public static FirebaseApp initializeApp(Context context, FirebaseOptions firebaseOptions, String str) {
        FirebaseApp firebaseApp;
        zt.zzbL(context);
        if (context.getApplicationContext() instanceof Application) {
            zzbav.zza((Application) context.getApplicationContext());
            zzbav.zzpv().zza(new zza());
        }
        String trim = str.trim();
        if (context.getApplicationContext() != null) {
            Object applicationContext = context.getApplicationContext();
        }
        synchronized (zzuH) {
            zzbo.zza(!zzbgQ.containsKey(trim), new StringBuilder(String.valueOf(trim).length() + 33).append("FirebaseApp name ").append(trim).append(" already exists!").toString());
            zzbo.zzb(applicationContext, (Object) "Application context cannot be null.");
            firebaseApp = new FirebaseApp(applicationContext, trim, firebaseOptions);
            zzbgQ.put(trim, firebaseApp);
        }
        zt.zze(firebaseApp);
        firebaseApp.zza(FirebaseApp.class, firebaseApp, zzbUT);
        if (firebaseApp.zzEp()) {
            firebaseApp.zza(FirebaseApp.class, firebaseApp, zzbUU);
            firebaseApp.zza(Context.class, firebaseApp.getApplicationContext(), zzbUV);
        }
        return firebaseApp;
    }

    private final void zzEo() {
        zzbo.zza(!this.zzbVa.get(), (Object) "FirebaseApp was deleted");
    }

    private static List<String> zzEr() {
        Collection com_google_android_gms_common_util_zza = new com.google.android.gms.common.util.zza();
        synchronized (zzuH) {
            for (FirebaseApp name : zzbgQ.values()) {
                com_google_android_gms_common_util_zza.add(name.getName());
            }
            if (zt.zzJW() != null) {
                com_google_android_gms_common_util_zza.addAll(zt.zzJX());
            }
        }
        List<String> arrayList = new ArrayList(com_google_android_gms_common_util_zza);
        Collections.sort(arrayList);
        return arrayList;
    }

    private final void zzEs() {
        zza(FirebaseApp.class, this, zzbUT);
        if (zzEp()) {
            zza(FirebaseApp.class, this, zzbUU);
            zza(Context.class, this.mApplicationContext, zzbUV);
        }
    }

    private final <T> void zza(Class<T> cls, T t, Iterable<String> iterable) {
        boolean isDeviceProtectedStorage = ContextCompat.isDeviceProtectedStorage(this.mApplicationContext);
        if (isDeviceProtectedStorage) {
            zzd.zzbB(this.mApplicationContext);
        }
        for (String str : iterable) {
            String str2;
            if (isDeviceProtectedStorage) {
                try {
                    if (!zzbUW.contains(str2)) {
                    }
                } catch (ClassNotFoundException e) {
                    if (zzbUX.contains(str2)) {
                        throw new IllegalStateException(String.valueOf(str2).concat(" is missing, but is required. Check if it has been removed by Proguard."));
                    }
                    Log.d("FirebaseApp", String.valueOf(str2).concat(" is not linked. Skipping initialization."));
                } catch (NoSuchMethodException e2) {
                    throw new IllegalStateException(String.valueOf(str2).concat("#getInstance has been removed by Proguard. Add keep rule to prevent it."));
                } catch (Throwable e3) {
                    Log.wtf("FirebaseApp", "Firebase API initialization failure.", e3);
                } catch (Throwable e4) {
                    String str3 = "FirebaseApp";
                    String str4 = "Failed to initialize ";
                    str2 = String.valueOf(str2);
                    Log.wtf(str3, str2.length() != 0 ? str4.concat(str2) : new String(str4), e4);
                }
            }
            Method method = Class.forName(str2).getMethod("getInstance", new Class[]{cls});
            int modifiers = method.getModifiers();
            if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers)) {
                method.invoke(null, new Object[]{t});
            }
        }
    }

    public static void zzac(boolean z) {
        synchronized (zzuH) {
            ArrayList arrayList = new ArrayList(zzbgQ.values());
            int size = arrayList.size();
            int i = 0;
            while (i < size) {
                Object obj = arrayList.get(i);
                i++;
                FirebaseApp firebaseApp = (FirebaseApp) obj;
                if (firebaseApp.zzbUZ.get()) {
                    firebaseApp.zzav(z);
                }
            }
        }
    }

    private final void zzav(boolean z) {
        Log.d("FirebaseApp", "Notifying background state change listeners.");
        for (zzc zzac : this.zzbVc) {
            zzac.zzac(z);
        }
    }

    public boolean equals(Object obj) {
        return !(obj instanceof FirebaseApp) ? false : this.mName.equals(((FirebaseApp) obj).getName());
    }

    @NonNull
    public Context getApplicationContext() {
        zzEo();
        return this.mApplicationContext;
    }

    @NonNull
    public String getName() {
        zzEo();
        return this.mName;
    }

    @NonNull
    public FirebaseOptions getOptions() {
        zzEo();
        return this.zzbUY;
    }

    public final Task<GetTokenResult> getToken(boolean z) {
        zzEo();
        return this.zzbVe == null ? Tasks.forException(new FirebaseApiNotAvailableException("firebase-auth is not linked, please fall back to unauthenticated mode.")) : this.zzbVe.zzaw(z);
    }

    public int hashCode() {
        return this.mName.hashCode();
    }

    public void setAutomaticResourceManagementEnabled(boolean z) {
        zzEo();
        if (this.zzbUZ.compareAndSet(!z, z)) {
            boolean zzpw = zzbav.zzpv().zzpw();
            if (z && zzpw) {
                zzav(true);
            } else if (!z && zzpw) {
                zzav(false);
            }
        }
    }

    public String toString() {
        return zzbe.zzt(this).zzg("name", this.mName).zzg("options", this.zzbUY).toString();
    }

    public final boolean zzEp() {
        return DEFAULT_APP_NAME.equals(getName());
    }

    public final String zzEq() {
        String valueOf = String.valueOf(com.google.android.gms.common.util.zzc.zzh(getName().getBytes()));
        String valueOf2 = String.valueOf(com.google.android.gms.common.util.zzc.zzh(getOptions().getApplicationId().getBytes()));
        return new StringBuilder((String.valueOf(valueOf).length() + 1) + String.valueOf(valueOf2).length()).append(valueOf).append("+").append(valueOf2).toString();
    }

    public final void zza(@NonNull zu zuVar) {
        this.zzbVe = (zu) zzbo.zzu(zuVar);
    }

    @UiThread
    public final void zza(@NonNull zv zvVar) {
        Log.d("FirebaseApp", "Notifying auth state listeners.");
        int i = 0;
        for (zza zzb : this.zzbVb) {
            zzb.zzb(zvVar);
            i++;
        }
        Log.d("FirebaseApp", String.format("Notified %d auth state listeners.", new Object[]{Integer.valueOf(i)}));
    }

    public final void zza(@NonNull zza com_google_firebase_FirebaseApp_zza) {
        zzEo();
        zzbo.zzu(com_google_firebase_FirebaseApp_zza);
        this.zzbVb.add(com_google_firebase_FirebaseApp_zza);
        this.zzbVb.size();
    }

    public final void zza(zzc com_google_firebase_FirebaseApp_zzc) {
        zzEo();
        if (this.zzbUZ.get() && zzbav.zzpv().zzpw()) {
            com_google_firebase_FirebaseApp_zzc.zzac(true);
        }
        this.zzbVc.add(com_google_firebase_FirebaseApp_zzc);
    }
}
