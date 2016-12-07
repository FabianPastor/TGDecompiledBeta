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
import android.util.Log;
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.common.internal.zzx;
import com.google.android.gms.common.internal.zzz;
import com.google.android.gms.common.util.zzs;
import com.google.android.gms.common.util.zzt;
import com.google.android.gms.internal.zzanq;
import com.google.android.gms.internal.zzanr;
import com.google.android.gms.internal.zzans;
import com.google.android.gms.internal.zzant;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.GetTokenResult;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class FirebaseApp {
    public static final String DEFAULT_APP_NAME = "[DEFAULT]";
    private static final List<String> aVT = Arrays.asList(new String[]{"com.google.firebase.auth.FirebaseAuth", "com.google.firebase.iid.FirebaseInstanceId"});
    private static final List<String> aVU = Collections.singletonList("com.google.firebase.crash.FirebaseCrash");
    private static final List<String> aVV = Arrays.asList(new String[]{"com.google.android.gms.measurement.AppMeasurement"});
    private static final List<String> aVW = Arrays.asList(new String[0]);
    private static final Set<String> aVX = Collections.emptySet();
    static final Map<String, FirebaseApp> aic = new ArrayMap();
    private static final Object zzaox = new Object();
    private final FirebaseOptions aVY;
    private final AtomicBoolean aVZ = new AtomicBoolean(false);
    private final AtomicBoolean aWa = new AtomicBoolean();
    private final List<zza> aWb = new CopyOnWriteArrayList();
    private final List<zzb> aWc = new CopyOnWriteArrayList();
    private final List<Object> aWd = new CopyOnWriteArrayList();
    private zzans aWe;
    private final String mName;
    private final Context zzatc;

    public interface zza {
        void zzb(@NonNull zzant com_google_android_gms_internal_zzant);
    }

    public interface zzb {
        void zzcr(boolean z);
    }

    @TargetApi(24)
    private static class zzc extends BroadcastReceiver {
        private static AtomicReference<zzc> aWf = new AtomicReference();
        private final Context zzatc;

        public zzc(Context context) {
            this.zzatc = context;
        }

        private static void zzen(Context context) {
            if (aWf.get() == null) {
                BroadcastReceiver com_google_firebase_FirebaseApp_zzc = new zzc(context);
                if (aWf.compareAndSet(null, com_google_firebase_FirebaseApp_zzc)) {
                    IntentFilter intentFilter = new IntentFilter("android.intent.action.USER_UNLOCKED");
                    intentFilter.addDataScheme("package");
                    context.registerReceiver(com_google_firebase_FirebaseApp_zzc, intentFilter);
                }
            }
        }

        public void onReceive(Context context, Intent intent) {
            synchronized (FirebaseApp.zzaox) {
                for (FirebaseApp zza : FirebaseApp.aic.values()) {
                    zza.zzcnx();
                }
            }
            unregister();
        }

        public void unregister() {
            this.zzatc.unregisterReceiver(this);
        }
    }

    protected FirebaseApp(Context context, String str, FirebaseOptions firebaseOptions) {
        this.zzatc = (Context) zzaa.zzy(context);
        this.mName = zzaa.zzib(str);
        this.aVY = (FirebaseOptions) zzaa.zzy(firebaseOptions);
    }

    public static List<FirebaseApp> getApps(Context context) {
        List<FirebaseApp> arrayList;
        zzanr zzeu = zzanr.zzeu(context);
        synchronized (zzaox) {
            arrayList = new ArrayList(aic.values());
            Set<String> Q = zzanr.P().Q();
            Q.removeAll(aic.keySet());
            for (String str : Q) {
                zzeu.zztz(str);
                arrayList.add(initializeApp(context, null, str));
            }
        }
        return arrayList;
    }

    @Nullable
    public static FirebaseApp getInstance() {
        FirebaseApp firebaseApp;
        synchronized (zzaox) {
            firebaseApp = (FirebaseApp) aic.get(DEFAULT_APP_NAME);
            if (firebaseApp == null) {
                String valueOf = String.valueOf(zzt.zzayz());
                throw new IllegalStateException(new StringBuilder(String.valueOf(valueOf).length() + 116).append("Default FirebaseApp is not initialized in this process ").append(valueOf).append(". Make sure to call FirebaseApp.initializeApp(Context) first.").toString());
            }
        }
        return firebaseApp;
    }

    public static FirebaseApp getInstance(@NonNull String str) {
        FirebaseApp firebaseApp;
        synchronized (zzaox) {
            firebaseApp = (FirebaseApp) aic.get(zzrq(str));
            if (firebaseApp != null) {
            } else {
                String str2;
                Iterable zzcnw = zzcnw();
                if (zzcnw.isEmpty()) {
                    str2 = "";
                } else {
                    String str3 = "Available app names: ";
                    str2 = String.valueOf(zzx.zzia(", ").zzb(zzcnw));
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
        synchronized (zzaox) {
            if (aic.containsKey(DEFAULT_APP_NAME)) {
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
        zzanr zzeu = zzanr.zzeu(context);
        zzem(context);
        String zzrq = zzrq(str);
        if (!(context instanceof Application)) {
            Object applicationContext = context.getApplicationContext();
        }
        synchronized (zzaox) {
            zzaa.zza(!aic.containsKey(zzrq), new StringBuilder(String.valueOf(zzrq).length() + 33).append("FirebaseApp name ").append(zzrq).append(" already exists!").toString());
            zzaa.zzb(applicationContext, (Object) "Application context cannot be null.");
            firebaseApp = new FirebaseApp(applicationContext, zzrq, firebaseOptions);
            aic.put(zzrq, firebaseApp);
        }
        zzeu.zzg(firebaseApp);
        firebaseApp.zza(FirebaseApp.class, firebaseApp, aVT);
        if (firebaseApp.zzcnu()) {
            firebaseApp.zza(FirebaseApp.class, firebaseApp, aVU);
            firebaseApp.zza(Context.class, firebaseApp.getApplicationContext(), aVV);
        }
        return firebaseApp;
    }

    private <T> void zza(Class<T> cls, T t, Iterable<String> iterable) {
        boolean isDeviceProtectedStorage = ContextCompat.isDeviceProtectedStorage(this.zzatc);
        if (isDeviceProtectedStorage) {
            zzc.zzen(this.zzatc);
        }
        for (String str : iterable) {
            String str2;
            if (isDeviceProtectedStorage) {
                try {
                    if (!aVW.contains(str2)) {
                    }
                } catch (ClassNotFoundException e) {
                    if (aVX.contains(str2)) {
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

    private void zzcnt() {
        zzaa.zza(!this.aWa.get(), (Object) "FirebaseApp was deleted");
    }

    private static List<String> zzcnw() {
        Collection com_google_android_gms_common_util_zza = new com.google.android.gms.common.util.zza();
        synchronized (zzaox) {
            for (FirebaseApp name : aic.values()) {
                com_google_android_gms_common_util_zza.add(name.getName());
            }
            zzanr P = zzanr.P();
            if (P != null) {
                com_google_android_gms_common_util_zza.addAll(P.Q());
            }
        }
        List<String> arrayList = new ArrayList(com_google_android_gms_common_util_zza);
        Collections.sort(arrayList);
        return arrayList;
    }

    private void zzcnx() {
        zza(FirebaseApp.class, this, aVT);
        if (zzcnu()) {
            zza(FirebaseApp.class, this, aVU);
            zza(Context.class, this.zzatc, aVV);
        }
    }

    public static void zzcr(boolean z) {
        synchronized (zzaox) {
            Iterator it = new ArrayList(aic.values()).iterator();
            while (it.hasNext()) {
                FirebaseApp firebaseApp = (FirebaseApp) it.next();
                if (firebaseApp.aVZ.get()) {
                    firebaseApp.zzcs(z);
                }
            }
        }
    }

    private void zzcs(boolean z) {
        Log.d("FirebaseApp", "Notifying background state change listeners.");
        for (zzb zzcr : this.aWc) {
            zzcr.zzcr(z);
        }
    }

    @TargetApi(14)
    private static void zzem(Context context) {
        if (zzs.zzayq() && (context.getApplicationContext() instanceof Application)) {
            zzanq.zza((Application) context.getApplicationContext());
        }
    }

    private static String zzrq(@NonNull String str) {
        return str.trim();
    }

    public boolean equals(Object obj) {
        return !(obj instanceof FirebaseApp) ? false : this.mName.equals(((FirebaseApp) obj).getName());
    }

    @NonNull
    public Context getApplicationContext() {
        zzcnt();
        return this.zzatc;
    }

    @NonNull
    public String getName() {
        zzcnt();
        return this.mName;
    }

    @NonNull
    public FirebaseOptions getOptions() {
        zzcnt();
        return this.aVY;
    }

    public Task<GetTokenResult> getToken(boolean z) {
        zzcnt();
        return this.aWe == null ? Tasks.forException(new FirebaseApiNotAvailableException("firebase-auth is not linked, please fall back to unauthenticated mode.")) : this.aWe.zzct(z);
    }

    public int hashCode() {
        return this.mName.hashCode();
    }

    public void setAutomaticResourceManagementEnabled(boolean z) {
        zzcnt();
        if (this.aVZ.compareAndSet(!z, z)) {
            boolean O = zzanq.N().O();
            if (z && O) {
                zzcs(true);
            } else if (!z && O) {
                zzcs(false);
            }
        }
    }

    public String toString() {
        return zzz.zzx(this).zzg("name", this.mName).zzg("options", this.aVY).toString();
    }

    public void zza(@NonNull zzans com_google_android_gms_internal_zzans) {
        this.aWe = (zzans) zzaa.zzy(com_google_android_gms_internal_zzans);
    }

    @UiThread
    public void zza(@NonNull zzant com_google_android_gms_internal_zzant) {
        Log.d("FirebaseApp", "Notifying auth state listeners.");
        int i = 0;
        for (zza zzb : this.aWb) {
            zzb.zzb(com_google_android_gms_internal_zzant);
            i++;
        }
        Log.d("FirebaseApp", String.format("Notified %d auth state listeners.", new Object[]{Integer.valueOf(i)}));
    }

    public void zza(@NonNull zza com_google_firebase_FirebaseApp_zza) {
        zzcnt();
        zzaa.zzy(com_google_firebase_FirebaseApp_zza);
        this.aWb.add(com_google_firebase_FirebaseApp_zza);
    }

    public void zza(zzb com_google_firebase_FirebaseApp_zzb) {
        zzcnt();
        if (this.aVZ.get() && zzanq.N().O()) {
            com_google_firebase_FirebaseApp_zzb.zzcr(true);
        }
        this.aWc.add(com_google_firebase_FirebaseApp_zzb);
    }

    public boolean zzcnu() {
        return DEFAULT_APP_NAME.equals(getName());
    }

    public String zzcnv() {
        String valueOf = String.valueOf(com.google.android.gms.common.util.zzc.zzs(getName().getBytes()));
        String valueOf2 = String.valueOf(com.google.android.gms.common.util.zzc.zzs(getOptions().getApplicationId().getBytes()));
        return new StringBuilder((String.valueOf(valueOf).length() + 1) + String.valueOf(valueOf2).length()).append(valueOf).append("+").append(valueOf2).toString();
    }
}
