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
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.util.zzs;
import com.google.android.gms.common.util.zzt;
import com.google.android.gms.internal.zzbqj;
import com.google.android.gms.internal.zzbqk;
import com.google.android.gms.internal.zzbql;
import com.google.android.gms.internal.zzbqm;
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
    private static final List<String> zzbUA = Arrays.asList(new String[]{"com.google.android.gms.measurement.AppMeasurement"});
    private static final List<String> zzbUB = Arrays.asList(new String[0]);
    private static final Set<String> zzbUC = Collections.emptySet();
    private static final List<String> zzbUy = Arrays.asList(new String[]{"com.google.firebase.auth.FirebaseAuth", "com.google.firebase.iid.FirebaseInstanceId"});
    private static final List<String> zzbUz = Collections.singletonList("com.google.firebase.crash.FirebaseCrash");
    static final Map<String, FirebaseApp> zzbha = new ArrayMap();
    private static final Object zztU = new Object();
    private final String mName;
    private final FirebaseOptions zzbUD;
    private final AtomicBoolean zzbUE = new AtomicBoolean(false);
    private final AtomicBoolean zzbUF = new AtomicBoolean();
    private final List<zza> zzbUG = new CopyOnWriteArrayList();
    private final List<zzb> zzbUH = new CopyOnWriteArrayList();
    private final List<Object> zzbUI = new CopyOnWriteArrayList();
    private zzbql zzbUJ;
    private final Context zzvZ;

    public interface zza {
        void zzb(@NonNull zzbqm com_google_android_gms_internal_zzbqm);
    }

    public interface zzb {
        void zzaQ(boolean z);
    }

    @TargetApi(24)
    private static class zzc extends BroadcastReceiver {
        private static AtomicReference<zzc> zzbUK = new AtomicReference();
        private final Context zzvZ;

        public zzc(Context context) {
            this.zzvZ = context;
        }

        private static void zzbR(Context context) {
            if (zzbUK.get() == null) {
                BroadcastReceiver com_google_firebase_FirebaseApp_zzc = new zzc(context);
                if (zzbUK.compareAndSet(null, com_google_firebase_FirebaseApp_zzc)) {
                    context.registerReceiver(com_google_firebase_FirebaseApp_zzc, new IntentFilter("android.intent.action.USER_UNLOCKED"));
                }
            }
        }

        public void onReceive(Context context, Intent intent) {
            synchronized (FirebaseApp.zztU) {
                for (FirebaseApp zza : FirebaseApp.zzbha.values()) {
                    zza.zzTw();
                }
            }
            unregister();
        }

        public void unregister() {
            this.zzvZ.unregisterReceiver(this);
        }
    }

    protected FirebaseApp(Context context, String str, FirebaseOptions firebaseOptions) {
        this.zzvZ = (Context) zzac.zzw(context);
        this.mName = zzac.zzdv(str);
        this.zzbUD = (FirebaseOptions) zzac.zzw(firebaseOptions);
    }

    public static List<FirebaseApp> getApps(Context context) {
        List<FirebaseApp> arrayList;
        zzbqk zzbZ = zzbqk.zzbZ(context);
        synchronized (zztU) {
            arrayList = new ArrayList(zzbha.values());
            Set<String> zzaaq = zzbqk.zzaap().zzaaq();
            zzaaq.removeAll(zzbha.keySet());
            for (String str : zzaaq) {
                zzbZ.zzjD(str);
                arrayList.add(initializeApp(context, null, str));
            }
        }
        return arrayList;
    }

    @Nullable
    public static FirebaseApp getInstance() {
        FirebaseApp firebaseApp;
        synchronized (zztU) {
            firebaseApp = (FirebaseApp) zzbha.get(DEFAULT_APP_NAME);
            if (firebaseApp == null) {
                String valueOf = String.valueOf(zzt.zzyK());
                throw new IllegalStateException(new StringBuilder(String.valueOf(valueOf).length() + 116).append("Default FirebaseApp is not initialized in this process ").append(valueOf).append(". Make sure to call FirebaseApp.initializeApp(Context) first.").toString());
            }
        }
        return firebaseApp;
    }

    public static FirebaseApp getInstance(@NonNull String str) {
        FirebaseApp firebaseApp;
        synchronized (zztU) {
            firebaseApp = (FirebaseApp) zzbha.get(zzit(str));
            if (firebaseApp != null) {
            } else {
                String str2;
                Iterable zzTv = zzTv();
                if (zzTv.isEmpty()) {
                    str2 = "";
                } else {
                    String str3 = "Available app names: ";
                    str2 = String.valueOf(TextUtils.join(", ", zzTv));
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
        synchronized (zztU) {
            if (zzbha.containsKey(DEFAULT_APP_NAME)) {
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
        zzbqk zzbZ = zzbqk.zzbZ(context);
        zzbQ(context);
        String zzit = zzit(str);
        if (context.getApplicationContext() != null) {
            Object applicationContext = context.getApplicationContext();
        }
        synchronized (zztU) {
            zzac.zza(!zzbha.containsKey(zzit), new StringBuilder(String.valueOf(zzit).length() + 33).append("FirebaseApp name ").append(zzit).append(" already exists!").toString());
            zzac.zzb(applicationContext, (Object) "Application context cannot be null.");
            firebaseApp = new FirebaseApp(applicationContext, zzit, firebaseOptions);
            zzbha.put(zzit, firebaseApp);
        }
        zzbZ.zzg(firebaseApp);
        firebaseApp.zza(FirebaseApp.class, firebaseApp, zzbUy);
        if (firebaseApp.zzTt()) {
            firebaseApp.zza(FirebaseApp.class, firebaseApp, zzbUz);
            firebaseApp.zza(Context.class, firebaseApp.getApplicationContext(), zzbUA);
        }
        return firebaseApp;
    }

    private void zzTs() {
        zzac.zza(!this.zzbUF.get(), (Object) "FirebaseApp was deleted");
    }

    private static List<String> zzTv() {
        Collection com_google_android_gms_common_util_zza = new com.google.android.gms.common.util.zza();
        synchronized (zztU) {
            for (FirebaseApp name : zzbha.values()) {
                com_google_android_gms_common_util_zza.add(name.getName());
            }
            zzbqk zzaap = zzbqk.zzaap();
            if (zzaap != null) {
                com_google_android_gms_common_util_zza.addAll(zzaap.zzaaq());
            }
        }
        List<String> arrayList = new ArrayList(com_google_android_gms_common_util_zza);
        Collections.sort(arrayList);
        return arrayList;
    }

    private void zzTw() {
        zza(FirebaseApp.class, this, zzbUy);
        if (zzTt()) {
            zza(FirebaseApp.class, this, zzbUz);
            zza(Context.class, this.zzvZ, zzbUA);
        }
    }

    private <T> void zza(Class<T> cls, T t, Iterable<String> iterable) {
        boolean isDeviceProtectedStorage = ContextCompat.isDeviceProtectedStorage(this.zzvZ);
        if (isDeviceProtectedStorage) {
            zzc.zzbR(this.zzvZ);
        }
        for (String str : iterable) {
            String str2;
            if (isDeviceProtectedStorage) {
                try {
                    if (!zzbUB.contains(str2)) {
                    }
                } catch (ClassNotFoundException e) {
                    if (zzbUC.contains(str2)) {
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

    public static void zzaQ(boolean z) {
        synchronized (zztU) {
            Iterator it = new ArrayList(zzbha.values()).iterator();
            while (it.hasNext()) {
                FirebaseApp firebaseApp = (FirebaseApp) it.next();
                if (firebaseApp.zzbUE.get()) {
                    firebaseApp.zzaR(z);
                }
            }
        }
    }

    private void zzaR(boolean z) {
        Log.d("FirebaseApp", "Notifying background state change listeners.");
        for (zzb zzaQ : this.zzbUH) {
            zzaQ.zzaQ(z);
        }
    }

    @TargetApi(14)
    private static void zzbQ(Context context) {
        if (zzs.zzyA() && (context.getApplicationContext() instanceof Application)) {
            zzbqj.zza((Application) context.getApplicationContext());
        }
    }

    private static String zzit(@NonNull String str) {
        return str.trim();
    }

    public boolean equals(Object obj) {
        return !(obj instanceof FirebaseApp) ? false : this.mName.equals(((FirebaseApp) obj).getName());
    }

    @NonNull
    public Context getApplicationContext() {
        zzTs();
        return this.zzvZ;
    }

    @NonNull
    public String getName() {
        zzTs();
        return this.mName;
    }

    @NonNull
    public FirebaseOptions getOptions() {
        zzTs();
        return this.zzbUD;
    }

    public Task<GetTokenResult> getToken(boolean z) {
        zzTs();
        return this.zzbUJ == null ? Tasks.forException(new FirebaseApiNotAvailableException("firebase-auth is not linked, please fall back to unauthenticated mode.")) : this.zzbUJ.zzaS(z);
    }

    public int hashCode() {
        return this.mName.hashCode();
    }

    public void setAutomaticResourceManagementEnabled(boolean z) {
        zzTs();
        if (this.zzbUE.compareAndSet(!z, z)) {
            boolean zzaao = zzbqj.zzaan().zzaao();
            if (z && zzaao) {
                zzaR(true);
            } else if (!z && zzaao) {
                zzaR(false);
            }
        }
    }

    public String toString() {
        return zzaa.zzv(this).zzg("name", this.mName).zzg("options", this.zzbUD).toString();
    }

    public boolean zzTt() {
        return DEFAULT_APP_NAME.equals(getName());
    }

    public String zzTu() {
        String valueOf = String.valueOf(com.google.android.gms.common.util.zzc.zzs(getName().getBytes()));
        String valueOf2 = String.valueOf(com.google.android.gms.common.util.zzc.zzs(getOptions().getApplicationId().getBytes()));
        return new StringBuilder((String.valueOf(valueOf).length() + 1) + String.valueOf(valueOf2).length()).append(valueOf).append("+").append(valueOf2).toString();
    }

    public void zza(@NonNull zzbql com_google_android_gms_internal_zzbql) {
        this.zzbUJ = (zzbql) zzac.zzw(com_google_android_gms_internal_zzbql);
    }

    @UiThread
    public void zza(@NonNull zzbqm com_google_android_gms_internal_zzbqm) {
        Log.d("FirebaseApp", "Notifying auth state listeners.");
        int i = 0;
        for (zza zzb : this.zzbUG) {
            zzb.zzb(com_google_android_gms_internal_zzbqm);
            i++;
        }
        Log.d("FirebaseApp", String.format("Notified %d auth state listeners.", new Object[]{Integer.valueOf(i)}));
    }

    public void zza(@NonNull zza com_google_firebase_FirebaseApp_zza) {
        zzTs();
        zzac.zzw(com_google_firebase_FirebaseApp_zza);
        this.zzbUG.add(com_google_firebase_FirebaseApp_zza);
    }

    public void zza(zzb com_google_firebase_FirebaseApp_zzb) {
        zzTs();
        if (this.zzbUE.get() && zzbqj.zzaan().zzaao()) {
            com_google_firebase_FirebaseApp_zzb.zzaQ(true);
        }
        this.zzbUH.add(com_google_firebase_FirebaseApp_zzb);
    }
}
