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
import com.google.android.gms.common.api.internal.BackgroundDetector;
import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.common.internal.Objects.ToStringHelper;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.util.PlatformVersion;
import com.google.android.gms.common.util.ProcessUtils;
import com.google.firebase.components.Component;
import com.google.firebase.components.zzc;
import com.google.firebase.components.zzg;
import com.google.firebase.internal.zzb;
import java.lang.reflect.InvocationTargetException;
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
import javax.annotation.concurrent.GuardedBy;

public class FirebaseApp
{
  private static final Object sLock = new Object();
  private static final List<String> zza = Arrays.asList(new String[] { "com.google.firebase.auth.FirebaseAuth", "com.google.firebase.iid.FirebaseInstanceId" });
  private static final List<String> zzb = Collections.singletonList("com.google.firebase.crash.FirebaseCrash");
  private static final List<String> zzc = Arrays.asList(new String[] { "com.google.android.gms.measurement.AppMeasurement" });
  private static final List<String> zzd = Arrays.asList(new String[0]);
  private static final Set<String> zze = Collections.emptySet();
  @GuardedBy("sLock")
  static final Map<String, FirebaseApp> zzf = new ArrayMap();
  private final String mName;
  private final Context zzg;
  private final FirebaseOptions zzh;
  private final zzg zzi;
  private final AtomicBoolean zzj = new AtomicBoolean(false);
  private final AtomicBoolean zzk = new AtomicBoolean();
  private final List<Object> zzl = new CopyOnWriteArrayList();
  private final List<BackgroundStateChangeListener> zzm = new CopyOnWriteArrayList();
  private final List<Object> zzn = new CopyOnWriteArrayList();
  private IdTokenListenersCountChangedListener zzp;
  
  private FirebaseApp(Context paramContext, String paramString, FirebaseOptions paramFirebaseOptions)
  {
    this.zzg = ((Context)Preconditions.checkNotNull(paramContext));
    this.mName = Preconditions.checkNotEmpty(paramString);
    this.zzh = ((FirebaseOptions)Preconditions.checkNotNull(paramFirebaseOptions));
    this.zzp = new com.google.firebase.internal.zza();
    this.zzi = new zzg(new zzc(this.zzg).zzj(), new Component[] { Component.of(Context.class, this.zzg), Component.of(FirebaseApp.class, this), Component.of(FirebaseOptions.class, this.zzh) });
  }
  
  public static FirebaseApp getInstance()
  {
    synchronized (sLock)
    {
      Object localObject2 = (FirebaseApp)zzf.get("[DEFAULT]");
      if (localObject2 == null)
      {
        IllegalStateException localIllegalStateException = new java/lang/IllegalStateException;
        String str = ProcessUtils.getMyProcessName();
        int i = String.valueOf(str).length();
        localObject2 = new java/lang/StringBuilder;
        ((StringBuilder)localObject2).<init>(i + 116);
        localIllegalStateException.<init>("Default FirebaseApp is not initialized in this process " + str + ". Make sure to call FirebaseApp.initializeApp(Context) first.");
        throw localIllegalStateException;
      }
    }
    return localFirebaseApp;
  }
  
  public static FirebaseApp initializeApp(Context paramContext)
  {
    for (;;)
    {
      FirebaseOptions localFirebaseOptions;
      synchronized (sLock)
      {
        if (zzf.containsKey("[DEFAULT]"))
        {
          paramContext = getInstance();
          return paramContext;
        }
        localFirebaseOptions = FirebaseOptions.fromResource(paramContext);
        if (localFirebaseOptions == null) {
          paramContext = null;
        }
      }
      paramContext = initializeApp(paramContext, localFirebaseOptions);
    }
  }
  
  public static FirebaseApp initializeApp(Context paramContext, FirebaseOptions paramFirebaseOptions)
  {
    return initializeApp(paramContext, paramFirebaseOptions, "[DEFAULT]");
  }
  
  public static FirebaseApp initializeApp(Context paramContext, FirebaseOptions paramFirebaseOptions, String paramString)
  {
    zzb.zze(paramContext);
    if ((PlatformVersion.isAtLeastIceCreamSandwich()) && ((paramContext.getApplicationContext() instanceof Application)))
    {
      BackgroundDetector.initialize((Application)paramContext.getApplicationContext());
      BackgroundDetector.getInstance().addListener(new zza());
    }
    paramString = paramString.trim();
    if (paramContext.getApplicationContext() == null) {}
    synchronized (sLock)
    {
      while (!zzf.containsKey(paramString))
      {
        bool = true;
        int i = String.valueOf(paramString).length();
        Object localObject2 = new java/lang/StringBuilder;
        ((StringBuilder)localObject2).<init>(i + 33);
        Preconditions.checkState(bool, "FirebaseApp name " + paramString + " already exists!");
        Preconditions.checkNotNull(paramContext, "Application context cannot be null.");
        localObject2 = new com/google/firebase/FirebaseApp;
        ((FirebaseApp)localObject2).<init>(paramContext, paramString, paramFirebaseOptions);
        zzf.put(paramString, localObject2);
        zzb.zzb((FirebaseApp)localObject2);
        ((FirebaseApp)localObject2).zzc();
        return (FirebaseApp)localObject2;
        paramContext = paramContext.getApplicationContext();
      }
      boolean bool = false;
    }
  }
  
  public static void onBackgroundStateChanged(boolean paramBoolean)
  {
    synchronized (sLock)
    {
      ArrayList localArrayList = new java/util/ArrayList;
      localArrayList.<init>(zzf.values());
      localArrayList = (ArrayList)localArrayList;
      int i = localArrayList.size();
      int j = 0;
      while (j < i)
      {
        Object localObject3 = localArrayList.get(j);
        int k = j + 1;
        localObject3 = (FirebaseApp)localObject3;
        j = k;
        if (((FirebaseApp)localObject3).zzj.get())
        {
          ((FirebaseApp)localObject3).zza(paramBoolean);
          j = k;
        }
      }
    }
  }
  
  private final void zza()
  {
    if (!this.zzk.get()) {}
    for (boolean bool = true;; bool = false)
    {
      Preconditions.checkState(bool, "FirebaseApp was deleted");
      return;
    }
  }
  
  private static <T> void zza(Class<T> paramClass, T paramT, Iterable<String> paramIterable, boolean paramBoolean)
  {
    Iterator localIterator = paramIterable.iterator();
    for (;;)
    {
      if (localIterator.hasNext())
      {
        paramIterable = (String)localIterator.next();
        if (paramBoolean) {}
        try
        {
          if (zzd.contains(paramIterable))
          {
            Method localMethod = Class.forName(paramIterable).getMethod("getInstance", new Class[] { paramClass });
            int i = localMethod.getModifiers();
            if ((Modifier.isPublic(i)) && (Modifier.isStatic(i))) {
              localMethod.invoke(null, new Object[] { paramT });
            }
          }
        }
        catch (ClassNotFoundException localClassNotFoundException)
        {
          if (zze.contains(paramIterable)) {
            throw new IllegalStateException(String.valueOf(paramIterable).concat(" is missing, but is required. Check if it has been removed by Proguard."));
          }
          Log.d("FirebaseApp", String.valueOf(paramIterable).concat(" is not linked. Skipping initialization."));
        }
        catch (NoSuchMethodException paramClass)
        {
          throw new IllegalStateException(String.valueOf(paramIterable).concat("#getInstance has been removed by Proguard. Add keep rule to prevent it."));
        }
        catch (InvocationTargetException paramIterable)
        {
          Log.wtf("FirebaseApp", "Firebase API initialization failure.", paramIterable);
        }
        catch (IllegalAccessException localIllegalAccessException)
        {
          paramIterable = String.valueOf(paramIterable);
          if (paramIterable.length() != 0) {}
          for (paramIterable = "Failed to initialize ".concat(paramIterable);; paramIterable = new String("Failed to initialize "))
          {
            Log.wtf("FirebaseApp", paramIterable, localIllegalAccessException);
            break;
          }
        }
      }
    }
  }
  
  private final void zza(boolean paramBoolean)
  {
    Log.d("FirebaseApp", "Notifying background state change listeners.");
    Iterator localIterator = this.zzm.iterator();
    while (localIterator.hasNext()) {
      ((BackgroundStateChangeListener)localIterator.next()).onBackgroundStateChanged(paramBoolean);
    }
  }
  
  private final void zzc()
  {
    boolean bool = ContextCompat.isDeviceProtectedStorage(this.zzg);
    if (bool) {
      zza.zzb(this.zzg);
    }
    for (;;)
    {
      zza(FirebaseApp.class, this, zza, bool);
      if (isDefaultApp())
      {
        zza(FirebaseApp.class, this, zzb, bool);
        zza(Context.class, this.zzg, zzc, bool);
      }
      return;
      this.zzi.zzb(isDefaultApp());
    }
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof FirebaseApp)) {}
    for (boolean bool = false;; bool = this.mName.equals(((FirebaseApp)paramObject).getName())) {
      return bool;
    }
  }
  
  public Context getApplicationContext()
  {
    zza();
    return this.zzg;
  }
  
  public String getName()
  {
    zza();
    return this.mName;
  }
  
  public FirebaseOptions getOptions()
  {
    zza();
    return this.zzh;
  }
  
  public int hashCode()
  {
    return this.mName.hashCode();
  }
  
  public boolean isDefaultApp()
  {
    return "[DEFAULT]".equals(getName());
  }
  
  public String toString()
  {
    return Objects.toStringHelper(this).add("name", this.mName).add("options", this.zzh).toString();
  }
  
  public static abstract interface BackgroundStateChangeListener
  {
    public abstract void onBackgroundStateChanged(boolean paramBoolean);
  }
  
  public static abstract interface IdTokenListenersCountChangedListener {}
  
  @TargetApi(24)
  private static final class zza
    extends BroadcastReceiver
  {
    private static AtomicReference<zza> zzq = new AtomicReference();
    private final Context zzg;
    
    private zza(Context paramContext)
    {
      this.zzg = paramContext;
    }
    
    private static void zza(Context paramContext)
    {
      if (zzq.get() == null)
      {
        zza localzza = new zza(paramContext);
        if (zzq.compareAndSet(null, localzza)) {
          paramContext.registerReceiver(localzza, new IntentFilter("android.intent.action.USER_UNLOCKED"));
        }
      }
    }
    
    public final void onReceive(Context arg1, Intent paramIntent)
    {
      synchronized ()
      {
        paramIntent = FirebaseApp.zzf.values().iterator();
        if (paramIntent.hasNext()) {
          FirebaseApp.zza((FirebaseApp)paramIntent.next());
        }
      }
      this.zzg.unregisterReceiver(this);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/firebase/FirebaseApp.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */