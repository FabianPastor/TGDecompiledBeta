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
import com.google.android.gms.common.util.zzt;
import com.google.android.gms.common.util.zzu;
import com.google.android.gms.internal.zzaac;
import com.google.android.gms.internal.zzbth;
import com.google.android.gms.internal.zzbti;
import com.google.android.gms.internal.zzbtj;
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
    private static final List<String> zzbWD = Arrays.asList(new String[]{"com.google.firebase.auth.FirebaseAuth", "com.google.firebase.iid.FirebaseInstanceId"});
    private static final List<String> zzbWE = Collections.singletonList("com.google.firebase.crash.FirebaseCrash");
    private static final List<String> zzbWF = Arrays.asList(new String[]{"com.google.android.gms.measurement.AppMeasurement"});
    private static final List<String> zzbWG = Arrays.asList(new String[0]);
    private static final Set<String> zzbWH = Collections.emptySet();
    static final Map<String, FirebaseApp> zzbhH = new ArrayMap();
    private static final Object zztX = new Object();
    private final String mName;
    private final FirebaseOptions zzbWI;
    private final AtomicBoolean zzbWJ = new AtomicBoolean(false);
    private final AtomicBoolean zzbWK = new AtomicBoolean();
    private final List<zza> zzbWL = new CopyOnWriteArrayList();
    private final List<zzb> zzbWM = new CopyOnWriteArrayList();
    private final List<Object> zzbWN = new CopyOnWriteArrayList();
    private zzbti zzbWO;
    private final Context zzwi;

    public interface zza {
        void zzb(@NonNull zzbtj com_google_android_gms_internal_zzbtj);
    }

    public interface zzb {
        void zzas(boolean z);
    }

    @TargetApi(24)
    private static class zzc extends BroadcastReceiver {
        private static AtomicReference<zzc> zzbWP = new AtomicReference();
        private final Context zzwi;

        public zzc(Context context) {
            this.zzwi = context;
        }

        private static void zzcm(Context context) {
            if (zzbWP.get() == null) {
                BroadcastReceiver com_google_firebase_FirebaseApp_zzc = new zzc(context);
                if (zzbWP.compareAndSet(null, com_google_firebase_FirebaseApp_zzc)) {
                    context.registerReceiver(com_google_firebase_FirebaseApp_zzc, new IntentFilter("android.intent.action.USER_UNLOCKED"));
                }
            }
        }

        public void onReceive(Context context, Intent intent) {
            synchronized (FirebaseApp.zztX) {
                for (FirebaseApp zza : FirebaseApp.zzbhH.values()) {
                    zza.zzVa();
                }
            }
            unregister();
        }

        public void unregister() {
            this.zzwi.unregisterReceiver(this);
        }
    }

    protected FirebaseApp(Context context, String str, FirebaseOptions firebaseOptions) {
        this.zzwi = (Context) zzac.zzw(context);
        this.mName = zzac.zzdr(str);
        this.zzbWI = (FirebaseOptions) zzac.zzw(firebaseOptions);
    }

    public static List<FirebaseApp> getApps(Context context) {
        List<FirebaseApp> arrayList;
        zzbth zzcx = zzbth.zzcx(context);
        synchronized (zztX) {
            arrayList = new ArrayList(zzbhH.values());
            Set<String> zzacb = zzbth.zzaca().zzacb();
            zzacb.removeAll(zzbhH.keySet());
            for (String str : zzacb) {
                zzcx.zzjC(str);
                arrayList.add(initializeApp(context, null, str));
            }
        }
        return arrayList;
    }

    @Nullable
    public static FirebaseApp getInstance() {
        FirebaseApp firebaseApp;
        synchronized (zztX) {
            firebaseApp = (FirebaseApp) zzbhH.get(DEFAULT_APP_NAME);
            if (firebaseApp == null) {
                String valueOf = String.valueOf(zzu.zzzr());
                throw new IllegalStateException(new StringBuilder(String.valueOf(valueOf).length() + 116).append("Default FirebaseApp is not initialized in this process ").append(valueOf).append(". Make sure to call FirebaseApp.initializeApp(Context) first.").toString());
            }
        }
        return firebaseApp;
    }

    public static FirebaseApp getInstance(@NonNull String str) {
        FirebaseApp firebaseApp;
        synchronized (zztX) {
            firebaseApp = (FirebaseApp) zzbhH.get(zzis(str));
            if (firebaseApp != null) {
            } else {
                String str2;
                Iterable zzUZ = zzUZ();
                if (zzUZ.isEmpty()) {
                    str2 = "";
                } else {
                    String str3 = "Available app names: ";
                    str2 = String.valueOf(TextUtils.join(", ", zzUZ));
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
        synchronized (zztX) {
            if (zzbhH.containsKey(DEFAULT_APP_NAME)) {
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
        zzbth zzcx = zzbth.zzcx(context);
        zzcl(context);
        String zzis = zzis(str);
        if (context.getApplicationContext() != null) {
            Object applicationContext = context.getApplicationContext();
        }
        synchronized (zztX) {
            zzac.zza(!zzbhH.containsKey(zzis), new StringBuilder(String.valueOf(zzis).length() + 33).append("FirebaseApp name ").append(zzis).append(" already exists!").toString());
            zzac.zzb(applicationContext, (Object) "Application context cannot be null.");
            firebaseApp = new FirebaseApp(applicationContext, zzis, firebaseOptions);
            zzbhH.put(zzis, firebaseApp);
        }
        zzcx.zzg(firebaseApp);
        firebaseApp.zza(FirebaseApp.class, firebaseApp, zzbWD);
        if (firebaseApp.zzUX()) {
            firebaseApp.zza(FirebaseApp.class, firebaseApp, zzbWE);
            firebaseApp.zza(Context.class, firebaseApp.getApplicationContext(), zzbWF);
        }
        return firebaseApp;
    }

    private void zzUW() {
        zzac.zza(!this.zzbWK.get(), (Object) "FirebaseApp was deleted");
    }

    private static List<String> zzUZ() {
        Collection com_google_android_gms_common_util_zza = new com.google.android.gms.common.util.zza();
        synchronized (zztX) {
            for (FirebaseApp name : zzbhH.values()) {
                com_google_android_gms_common_util_zza.add(name.getName());
            }
            zzbth zzaca = zzbth.zzaca();
            if (zzaca != null) {
                com_google_android_gms_common_util_zza.addAll(zzaca.zzacb());
            }
        }
        List<String> arrayList = new ArrayList(com_google_android_gms_common_util_zza);
        Collections.sort(arrayList);
        return arrayList;
    }

    private void zzVa() {
        zza(FirebaseApp.class, this, zzbWD);
        if (zzUX()) {
            zza(FirebaseApp.class, this, zzbWE);
            zza(Context.class, this.zzwi, zzbWF);
        }
    }

    private <T> void zza(Class<T> cls, T t, Iterable<String> iterable) {
        String valueOf;
        boolean isDeviceProtectedStorage = ContextCompat.isDeviceProtectedStorage(this.zzwi);
        if (isDeviceProtectedStorage) {
            zzc.zzcm(this.zzwi);
        }
        for (String valueOf2 : iterable) {
            if (isDeviceProtectedStorage) {
                try {
                    if (!zzbWG.contains(valueOf2)) {
                    }
                } catch (ClassNotFoundException e) {
                    if (zzbWH.contains(valueOf2)) {
                        throw new IllegalStateException(String.valueOf(valueOf2).concat(" is missing, but is required. Check if it has been removed by Proguard."));
                    }
                    Log.d("FirebaseApp", String.valueOf(valueOf2).concat(" is not linked. Skipping initialization."));
                } catch (NoSuchMethodException e2) {
                    throw new IllegalStateException(String.valueOf(valueOf2).concat("#getInstance has been removed by Proguard. Add keep rule to prevent it."));
                } catch (Throwable e3) {
                    Log.wtf("FirebaseApp", "Firebase API initialization failure.", e3);
                } catch (Throwable e4) {
                    String str = "FirebaseApp";
                    String str2 = "Failed to initialize ";
                    valueOf2 = String.valueOf(valueOf2);
                    Log.wtf(str, valueOf2.length() != 0 ? str2.concat(valueOf2) : new String(str2), e4);
                }
            }
            Method method = Class.forName(valueOf2).getMethod("getInstance", new Class[]{cls});
            int modifiers = method.getModifiers();
            if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers)) {
                method.invoke(null, new Object[]{t});
            }
        }
    }

    private void zzaV(boolean z) {
        Log.d("FirebaseApp", "Notifying background state change listeners.");
        for (zzb zzas : this.zzbWM) {
            zzas.zzas(z);
        }
    }

    public static void zzas(boolean z) {
        synchronized (zztX) {
            Iterator it = new ArrayList(zzbhH.values()).iterator();
            while (it.hasNext()) {
                FirebaseApp firebaseApp = (FirebaseApp) it.next();
                if (firebaseApp.zzbWJ.get()) {
                    firebaseApp.zzaV(z);
                }
            }
        }
    }

    @TargetApi(14)
    private static void zzcl(Context context) {
        zzt.zzzg();
        if (context.getApplicationContext() instanceof Application) {
            zzaac.zza((Application) context.getApplicationContext());
            zzaac.zzvB().zza(new com.google.android.gms.internal.zzaac.zza() {
                public void zzas(boolean z) {
                    FirebaseApp.zzas(z);
                }
            });
        }
    }

    private static String zzis(@NonNull String str) {
        return str.trim();
    }

    public boolean equals(Object obj) {
        return !(obj instanceof FirebaseApp) ? false : this.mName.equals(((FirebaseApp) obj).getName());
    }

    @NonNull
    public Context getApplicationContext() {
        zzUW();
        return this.zzwi;
    }

    @NonNull
    public String getName() {
        zzUW();
        return this.mName;
    }

    @NonNull
    public FirebaseOptions getOptions() {
        zzUW();
        return this.zzbWI;
    }

    public Task<GetTokenResult> getToken(boolean z) {
        zzUW();
        return this.zzbWO == null ? Tasks.forException(new FirebaseApiNotAvailableException("firebase-auth is not linked, please fall back to unauthenticated mode.")) : this.zzbWO.zzaW(z);
    }

    public int hashCode() {
        return this.mName.hashCode();
    }

    public void setAutomaticResourceManagementEnabled(boolean z) {
        zzUW();
        if (this.zzbWJ.compareAndSet(!z, z)) {
            boolean zzvC = zzaac.zzvB().zzvC();
            if (z && zzvC) {
                zzaV(true);
            } else if (!z && zzvC) {
                zzaV(false);
            }
        }
    }

    public String toString() {
        return zzaa.zzv(this).zzg("name", this.mName).zzg("options", this.zzbWI).toString();
    }

    public boolean zzUX() {
        return DEFAULT_APP_NAME.equals(getName());
    }

    public String zzUY() {
        String valueOf = String.valueOf(com.google.android.gms.common.util.zzc.zzs(getName().getBytes()));
        String valueOf2 = String.valueOf(com.google.android.gms.common.util.zzc.zzs(getOptions().getApplicationId().getBytes()));
        return new StringBuilder((String.valueOf(valueOf).length() + 1) + String.valueOf(valueOf2).length()).append(valueOf).append("+").append(valueOf2).toString();
    }

    public void zza(@NonNull zzbti com_google_android_gms_internal_zzbti) {
        this.zzbWO = (zzbti) zzac.zzw(com_google_android_gms_internal_zzbti);
    }

    @UiThread
    public void zza(@NonNull zzbtj com_google_android_gms_internal_zzbtj) {
        Log.d("FirebaseApp", "Notifying auth state listeners.");
        int i = 0;
        for (zza zzb : this.zzbWL) {
            zzb.zzb(com_google_android_gms_internal_zzbtj);
            i++;
        }
        Log.d("FirebaseApp", String.format("Notified %d auth state listeners.", new Object[]{Integer.valueOf(i)}));
    }

    public void zza(@NonNull zza com_google_firebase_FirebaseApp_zza) {
        zzUW();
        zzac.zzw(com_google_firebase_FirebaseApp_zza);
        this.zzbWL.add(com_google_firebase_FirebaseApp_zza);
    }

    public void zza(zzb com_google_firebase_FirebaseApp_zzb) {
        zzUW();
        if (this.zzbWJ.get() && zzaac.zzvB().zzvC()) {
            com_google_firebase_FirebaseApp_zzb.zzas(true);
        }
        this.zzbWM.add(com_google_firebase_FirebaseApp_zzb);
    }
}
