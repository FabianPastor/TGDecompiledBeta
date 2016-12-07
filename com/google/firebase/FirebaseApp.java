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
import android.support.v4.util.ArrayMap;
import android.util.Log;
import com.google.android.gms.common.internal.zzab;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.internal.zzz;
import com.google.android.gms.common.util.zzs;
import com.google.android.gms.common.util.zzt;
import com.google.android.gms.internal.zzamz;
import com.google.android.gms.internal.zzana;
import com.google.android.gms.internal.zzanb;
import com.google.android.gms.internal.zzanc;
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
    private static final List<String> aSH = Arrays.asList(new String[]{"com.google.firebase.auth.FirebaseAuth", "com.google.firebase.iid.FirebaseInstanceId"});
    private static final List<String> aSI = Collections.singletonList("com.google.firebase.crash.FirebaseCrash");
    private static final List<String> aSJ = Arrays.asList(new String[]{"com.google.android.gms.measurement.AppMeasurement"});
    private static final List<String> aSK = Arrays.asList(new String[0]);
    private static final Set<String> aSL = Collections.emptySet();
    static final Map<String, FirebaseApp> afS = new ArrayMap();
    private static final Object zzaok = new Object();
    private final FirebaseOptions aSM;
    private final AtomicBoolean aSN = new AtomicBoolean(false);
    private final AtomicBoolean aSO = new AtomicBoolean();
    private final List<zza> aSP = new CopyOnWriteArrayList();
    private final List<zzb> aSQ = new CopyOnWriteArrayList();
    private final List<Object> aSR = new CopyOnWriteArrayList();
    private zzanb aSS;
    private final String mName;
    private final Context zzask;

    public interface zza {
        void zzb(@NonNull zzanc com_google_android_gms_internal_zzanc);
    }

    public interface zzb {
        void zzcp(boolean z);
    }

    @TargetApi(24)
    private static class zzc extends BroadcastReceiver {
        private static AtomicReference<zzc> aST = new AtomicReference();
        private final Context zzask;

        public zzc(Context context) {
            this.zzask = context;
        }

        private static void zzeq(Context context) {
            if (aST.get() == null) {
                BroadcastReceiver com_google_firebase_FirebaseApp_zzc = new zzc(context);
                if (aST.compareAndSet(null, com_google_firebase_FirebaseApp_zzc)) {
                    IntentFilter intentFilter = new IntentFilter("android.intent.action.USER_UNLOCKED");
                    intentFilter.addDataScheme("package");
                    context.registerReceiver(com_google_firebase_FirebaseApp_zzc, intentFilter);
                }
            }
        }

        public void onReceive(Context context, Intent intent) {
            synchronized (FirebaseApp.zzaok) {
                for (FirebaseApp zza : FirebaseApp.afS.values()) {
                    zza.zzcnx();
                }
            }
            unregister();
        }

        public void unregister() {
            this.zzask.unregisterReceiver(this);
        }
    }

    protected FirebaseApp(Context context, String str, FirebaseOptions firebaseOptions) {
        this.zzask = (Context) zzac.zzy(context);
        this.mName = zzac.zzhz(str);
        this.aSM = (FirebaseOptions) zzac.zzy(firebaseOptions);
    }

    public static List<FirebaseApp> getApps(Context context) {
        List<FirebaseApp> arrayList;
        zzana zzew = zzana.zzew(context);
        synchronized (zzaok) {
            arrayList = new ArrayList(afS.values());
            Set<String> O = zzana.N().O();
            O.removeAll(afS.keySet());
            for (String str : O) {
                arrayList.add(initializeApp(context, zzew.zzua(str), str));
            }
        }
        return arrayList;
    }

    @Nullable
    public static FirebaseApp getInstance() {
        FirebaseApp firebaseApp;
        synchronized (zzaok) {
            firebaseApp = (FirebaseApp) afS.get(DEFAULT_APP_NAME);
            if (firebaseApp == null) {
                String valueOf = String.valueOf(zzt.zzaxy());
                throw new IllegalStateException(new StringBuilder(String.valueOf(valueOf).length() + 116).append("Default FirebaseApp is not initialized in this process ").append(valueOf).append(". Make sure to call FirebaseApp.initializeApp(Context) first.").toString());
            }
        }
        return firebaseApp;
    }

    public static FirebaseApp getInstance(@NonNull String str) {
        FirebaseApp firebaseApp;
        synchronized (zzaok) {
            firebaseApp = (FirebaseApp) afS.get(zzrr(str));
            if (firebaseApp != null) {
            } else {
                String str2;
                Iterable zzcnw = zzcnw();
                if (zzcnw.isEmpty()) {
                    str2 = "";
                } else {
                    String str3 = "Available app names: ";
                    str2 = String.valueOf(zzz.zzhy(", ").zza(zzcnw));
                    str2 = str2.length() != 0 ? str3.concat(str2) : new String(str3);
                }
                throw new IllegalStateException(String.format("FirebaseApp with name %s doesn't exist. %s", new Object[]{str, str2}));
            }
        }
        return firebaseApp;
    }

    public static FirebaseApp initializeApp(Context context) {
        FirebaseApp instance;
        synchronized (zzaok) {
            if (afS.containsKey(DEFAULT_APP_NAME)) {
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
        zzana zzew = zzana.zzew(context);
        zzep(context);
        String zzrr = zzrr(str);
        if (!(context instanceof Application)) {
            Object applicationContext = context.getApplicationContext();
        }
        synchronized (zzaok) {
            zzac.zza(!afS.containsKey(zzrr), new StringBuilder(String.valueOf(zzrr).length() + 33).append("FirebaseApp name ").append(zzrr).append(" already exists!").toString());
            zzac.zzb(applicationContext, (Object) "Application context cannot be null.");
            firebaseApp = new FirebaseApp(applicationContext, zzrr, firebaseOptions);
            afS.put(zzrr, firebaseApp);
        }
        zzew.zzg(firebaseApp);
        firebaseApp.zza(FirebaseApp.class, firebaseApp, aSH);
        if (firebaseApp.zzcnu()) {
            firebaseApp.zza(FirebaseApp.class, firebaseApp, aSI);
            firebaseApp.zza(Context.class, firebaseApp.getApplicationContext(), aSJ);
        }
        return firebaseApp;
    }

    private <T> void zza(Class<T> cls, T t, Iterable<String> iterable) {
        boolean z;
        if (zzs.zzaxw()) {
            boolean isDeviceProtectedStorage = this.zzask.isDeviceProtectedStorage();
            if (isDeviceProtectedStorage) {
                zzc.zzeq(this.zzask);
            }
            z = isDeviceProtectedStorage;
        } else {
            z = false;
        }
        for (String str : iterable) {
            String str2;
            if (z) {
                try {
                    if (!aSK.contains(str2)) {
                    }
                } catch (ClassNotFoundException e) {
                    if (aSL.contains(str2)) {
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
        zzac.zza(!this.aSO.get(), (Object) "FirebaseApp was deleted");
    }

    private static List<String> zzcnw() {
        Collection com_google_android_gms_common_util_zza = new com.google.android.gms.common.util.zza();
        synchronized (zzaok) {
            for (FirebaseApp name : afS.values()) {
                com_google_android_gms_common_util_zza.add(name.getName());
            }
            zzana N = zzana.N();
            if (N != null) {
                com_google_android_gms_common_util_zza.addAll(N.O());
            }
        }
        List<String> arrayList = new ArrayList(com_google_android_gms_common_util_zza);
        Collections.sort(arrayList);
        return arrayList;
    }

    private void zzcnx() {
        zza(FirebaseApp.class, this, aSH);
        if (zzcnu()) {
            zza(FirebaseApp.class, this, aSI);
            zza(Context.class, this.zzask, aSJ);
        }
    }

    public static void zzcp(boolean z) {
        synchronized (zzaok) {
            Iterator it = new ArrayList(afS.values()).iterator();
            while (it.hasNext()) {
                FirebaseApp firebaseApp = (FirebaseApp) it.next();
                if (firebaseApp.aSN.get()) {
                    firebaseApp.zzcq(z);
                }
            }
        }
    }

    private void zzcq(boolean z) {
        Log.d("FirebaseApp", "Notifying background state change listeners.");
        for (zzb zzcp : this.aSQ) {
            zzcp.zzcp(z);
        }
    }

    @TargetApi(14)
    private static void zzep(Context context) {
        if (zzs.zzaxn() && (context.getApplicationContext() instanceof Application)) {
            zzamz.zza((Application) context.getApplicationContext());
        }
    }

    private static String zzrr(@NonNull String str) {
        return str.trim();
    }

    public boolean equals(Object obj) {
        return !(obj instanceof FirebaseApp) ? false : this.mName.equals(((FirebaseApp) obj).getName());
    }

    @NonNull
    public Context getApplicationContext() {
        zzcnt();
        return this.zzask;
    }

    @NonNull
    public String getName() {
        zzcnt();
        return this.mName;
    }

    @NonNull
    public FirebaseOptions getOptions() {
        zzcnt();
        return this.aSM;
    }

    public Task<GetTokenResult> getToken(boolean z) {
        zzcnt();
        return this.aSS == null ? Tasks.forException(new FirebaseApiNotAvailableException("firebase-auth is not linked, please fall back to unauthenticated mode.")) : this.aSS.zzcr(z);
    }

    public int hashCode() {
        return this.mName.hashCode();
    }

    public String toString() {
        return zzab.zzx(this).zzg("name", this.mName).zzg("options", this.aSM).toString();
    }

    public void zza(@NonNull zzanb com_google_android_gms_internal_zzanb) {
        this.aSS = (zzanb) zzac.zzy(com_google_android_gms_internal_zzanb);
    }

    @UiThread
    public void zza(@NonNull zzanc com_google_android_gms_internal_zzanc) {
        Log.d("FirebaseApp", "Notifying auth state listeners.");
        int i = 0;
        for (zza zzb : this.aSP) {
            zzb.zzb(com_google_android_gms_internal_zzanc);
            i++;
        }
        Log.d("FirebaseApp", String.format("Notified %d auth state listeners.", new Object[]{Integer.valueOf(i)}));
    }

    public void zza(@NonNull zza com_google_firebase_FirebaseApp_zza) {
        zzcnt();
        zzac.zzy(com_google_firebase_FirebaseApp_zza);
        this.aSP.add(com_google_firebase_FirebaseApp_zza);
    }

    public void zza(zzb com_google_firebase_FirebaseApp_zzb) {
        zzcnt();
        if (this.aSN.get() && zzamz.L().M()) {
            com_google_firebase_FirebaseApp_zzb.zzcp(true);
        }
        this.aSQ.add(com_google_firebase_FirebaseApp_zzb);
    }

    public boolean zzcnu() {
        return DEFAULT_APP_NAME.equals(getName());
    }

    public String zzcnv() {
        String valueOf = String.valueOf(com.google.android.gms.common.util.zzc.zzr(getName().getBytes()));
        String valueOf2 = String.valueOf(com.google.android.gms.common.util.zzc.zzr(getOptions().getApplicationId().getBytes()));
        return new StringBuilder((String.valueOf(valueOf).length() + 1) + String.valueOf(valueOf2).length()).append(valueOf).append("+").append(valueOf2).toString();
    }
}
