package com.google.firebase;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import com.google.android.gms.common.api.internal.zzk;
import com.google.android.gms.common.internal.zzbg;
import com.google.android.gms.common.internal.zzbq;
import com.google.android.gms.common.util.zzs;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class FirebaseApp {
    private static final Object sLock = new Object();
    static final Map<String, FirebaseApp> zzifg = new ArrayMap();
    private static final List<String> zzman = Arrays.asList(new String[]{"com.google.firebase.auth.FirebaseAuth", "com.google.firebase.iid.FirebaseInstanceId"});
    private static final List<String> zzmao = Collections.singletonList("com.google.firebase.crash.FirebaseCrash");
    private static final List<String> zzmap = Arrays.asList(new String[]{"com.google.android.gms.measurement.AppMeasurement"});
    private static final List<String> zzmaq = Arrays.asList(new String[0]);
    private static final Set<String> zzmar = Collections.emptySet();
    private final Context mApplicationContext;
    private final String mName;
    private final FirebaseOptions zzmas;
    private final AtomicBoolean zzmat = new AtomicBoolean(false);
    private final AtomicBoolean zzmau = new AtomicBoolean();
    private final List<Object> zzmav = new CopyOnWriteArrayList();
    private final List<zza> zzmaw = new CopyOnWriteArrayList();
    private final List<Object> zzmax = new CopyOnWriteArrayList();
    private zzb zzmaz;

    public interface zza {
        void zzbf(boolean z);
    }

    public interface zzb {
    }

    @TargetApi(24)
    static class zzc extends BroadcastReceiver {
        private static AtomicReference<zzc> zzmba = new AtomicReference();
        private final Context mApplicationContext;

        private zzc(Context context) {
            this.mApplicationContext = context;
        }

        private static void zzer(Context context) {
            if (zzmba.get() == null) {
                BroadcastReceiver com_google_firebase_FirebaseApp_zzc = new zzc(context);
                if (zzmba.compareAndSet(null, com_google_firebase_FirebaseApp_zzc)) {
                    context.registerReceiver(com_google_firebase_FirebaseApp_zzc, new IntentFilter("android.intent.action.USER_UNLOCKED"));
                }
            }
        }

        public final void onReceive(Context context, Intent intent) {
            synchronized (FirebaseApp.sLock) {
                for (FirebaseApp zza : FirebaseApp.zzifg.values()) {
                    zza.zzbqr();
                }
            }
            this.mApplicationContext.unregisterReceiver(this);
        }
    }

    private FirebaseApp(Context context, String str, FirebaseOptions firebaseOptions) {
        this.mApplicationContext = (Context) zzbq.checkNotNull(context);
        this.mName = zzbq.zzgm(str);
        this.zzmas = (FirebaseOptions) zzbq.checkNotNull(firebaseOptions);
        this.zzmaz = new com.google.firebase.internal.zza();
    }

    public static FirebaseApp getInstance() {
        FirebaseApp firebaseApp;
        synchronized (sLock) {
            firebaseApp = (FirebaseApp) zzifg.get("[DEFAULT]");
            if (firebaseApp == null) {
                String zzamo = zzs.zzamo();
                throw new IllegalStateException(new StringBuilder(String.valueOf(zzamo).length() + 116).append("Default FirebaseApp is not initialized in this process ").append(zzamo).append(". Make sure to call FirebaseApp.initializeApp(Context) first.").toString());
            }
        }
        return firebaseApp;
    }

    public static FirebaseApp initializeApp(Context context) {
        FirebaseApp instance;
        synchronized (sLock) {
            if (zzifg.containsKey("[DEFAULT]")) {
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
        return initializeApp(context, firebaseOptions, "[DEFAULT]");
    }

    public static FirebaseApp initializeApp(Context context, FirebaseOptions firebaseOptions, String str) {
        FirebaseApp firebaseApp;
        com.google.firebase.internal.zzb.zzew(context);
        if (context.getApplicationContext() instanceof Application) {
            zzk.zza((Application) context.getApplicationContext());
            zzk.zzahb().zza(new zza());
        }
        String trim = str.trim();
        if (context.getApplicationContext() != null) {
            context = context.getApplicationContext();
        }
        synchronized (sLock) {
            zzbq.zza(!zzifg.containsKey(trim), new StringBuilder(String.valueOf(trim).length() + 33).append("FirebaseApp name ").append(trim).append(" already exists!").toString());
            zzbq.checkNotNull(context, "Application context cannot be null.");
            firebaseApp = new FirebaseApp(context, trim, firebaseOptions);
            zzifg.put(trim, firebaseApp);
        }
        com.google.firebase.internal.zzb.zzg(firebaseApp);
        firebaseApp.zza(FirebaseApp.class, firebaseApp, zzman);
        if (firebaseApp.zzbqo()) {
            firebaseApp.zza(FirebaseApp.class, firebaseApp, zzmao);
            firebaseApp.zza(Context.class, firebaseApp.getApplicationContext(), zzmap);
        }
        return firebaseApp;
    }

    private final <T> void zza(Class<T> cls, T t, Iterable<String> iterable) {
        String valueOf;
        boolean isDeviceProtectedStorage = ContextCompat.isDeviceProtectedStorage(this.mApplicationContext);
        if (isDeviceProtectedStorage) {
            zzc.zzer(this.mApplicationContext);
        }
        for (String valueOf2 : iterable) {
            if (isDeviceProtectedStorage) {
                try {
                    if (!zzmaq.contains(valueOf2)) {
                    }
                } catch (ClassNotFoundException e) {
                    if (zzmar.contains(valueOf2)) {
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

    public static void zzbf(boolean z) {
        synchronized (sLock) {
            ArrayList arrayList = new ArrayList(zzifg.values());
            int size = arrayList.size();
            int i = 0;
            while (i < size) {
                Object obj = arrayList.get(i);
                i++;
                FirebaseApp firebaseApp = (FirebaseApp) obj;
                if (firebaseApp.zzmat.get()) {
                    firebaseApp.zzcd(z);
                }
            }
        }
    }

    private final void zzbqn() {
        zzbq.zza(!this.zzmau.get(), "FirebaseApp was deleted");
    }

    private final void zzbqr() {
        zza(FirebaseApp.class, this, zzman);
        if (zzbqo()) {
            zza(FirebaseApp.class, this, zzmao);
            zza(Context.class, this.mApplicationContext, zzmap);
        }
    }

    private final void zzcd(boolean z) {
        Log.d("FirebaseApp", "Notifying background state change listeners.");
        for (zza zzbf : this.zzmaw) {
            zzbf.zzbf(z);
        }
    }

    public boolean equals(Object obj) {
        return !(obj instanceof FirebaseApp) ? false : this.mName.equals(((FirebaseApp) obj).getName());
    }

    public Context getApplicationContext() {
        zzbqn();
        return this.mApplicationContext;
    }

    public String getName() {
        zzbqn();
        return this.mName;
    }

    public FirebaseOptions getOptions() {
        zzbqn();
        return this.zzmas;
    }

    public int hashCode() {
        return this.mName.hashCode();
    }

    public String toString() {
        return zzbg.zzx(this).zzg("name", this.mName).zzg("options", this.zzmas).toString();
    }

    public final boolean zzbqo() {
        return "[DEFAULT]".equals(getName());
    }
}
