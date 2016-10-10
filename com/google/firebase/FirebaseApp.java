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
import com.google.android.gms.common.internal.zzab.zza;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.internal.zzz;
import com.google.android.gms.common.util.zza;
import com.google.android.gms.common.util.zzc;
import com.google.android.gms.common.util.zzs;
import com.google.android.gms.common.util.zzt;
import com.google.android.gms.internal.zzamz;
import com.google.android.gms.internal.zzana;
import com.google.android.gms.internal.zzanb;
import com.google.android.gms.internal.zzanc;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.GetTokenResult;
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

public class FirebaseApp
{
  public static final String DEFAULT_APP_NAME = "[DEFAULT]";
  private static final List<String> aSH = Arrays.asList(new String[] { "com.google.firebase.auth.FirebaseAuth", "com.google.firebase.iid.FirebaseInstanceId" });
  private static final List<String> aSI = Collections.singletonList("com.google.firebase.crash.FirebaseCrash");
  private static final List<String> aSJ = Arrays.asList(new String[] { "com.google.android.gms.measurement.AppMeasurement" });
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
  
  protected FirebaseApp(Context paramContext, String paramString, FirebaseOptions paramFirebaseOptions)
  {
    this.zzask = ((Context)zzac.zzy(paramContext));
    this.mName = zzac.zzhz(paramString);
    this.aSM = ((FirebaseOptions)zzac.zzy(paramFirebaseOptions));
  }
  
  public static List<FirebaseApp> getApps(Context paramContext)
  {
    zzana localzzana = zzana.zzew(paramContext);
    ArrayList localArrayList;
    synchronized (zzaok)
    {
      localArrayList = new ArrayList(afS.values());
      Object localObject2 = zzana.N().O();
      ((Set)localObject2).removeAll(afS.keySet());
      localObject2 = ((Set)localObject2).iterator();
      if (((Iterator)localObject2).hasNext())
      {
        String str = (String)((Iterator)localObject2).next();
        localArrayList.add(initializeApp(paramContext, localzzana.zzua(str), str));
      }
    }
    return localArrayList;
  }
  
  @Nullable
  public static FirebaseApp getInstance()
  {
    synchronized (zzaok)
    {
      Object localObject2 = (FirebaseApp)afS.get("[DEFAULT]");
      if (localObject2 == null)
      {
        localObject2 = String.valueOf(zzt.zzaxy());
        throw new IllegalStateException(String.valueOf(localObject2).length() + 116 + "Default FirebaseApp is not initialized in this process " + (String)localObject2 + ". Make sure to call FirebaseApp.initializeApp(Context) first.");
      }
    }
    return localFirebaseApp;
  }
  
  public static FirebaseApp getInstance(@NonNull String paramString)
  {
    for (;;)
    {
      synchronized (zzaok)
      {
        localObject1 = (FirebaseApp)afS.get(zzrr(paramString));
        if (localObject1 != null) {
          return (FirebaseApp)localObject1;
        }
        localObject1 = zzcnw();
        if (((List)localObject1).isEmpty())
        {
          localObject1 = "";
          throw new IllegalStateException(String.format("FirebaseApp with name %s doesn't exist. %s", new Object[] { paramString, localObject1 }));
        }
      }
      Object localObject1 = String.valueOf(zzz.zzhy(", ").zza((Iterable)localObject1));
      if (((String)localObject1).length() != 0) {
        localObject1 = "Available app names: ".concat((String)localObject1);
      } else {
        localObject1 = new String("Available app names: ");
      }
    }
  }
  
  public static FirebaseApp initializeApp(Context paramContext)
  {
    FirebaseOptions localFirebaseOptions;
    synchronized (zzaok)
    {
      if (afS.containsKey("[DEFAULT]"))
      {
        paramContext = getInstance();
        return paramContext;
      }
      localFirebaseOptions = FirebaseOptions.fromResource(paramContext);
      if (localFirebaseOptions == null) {
        return null;
      }
    }
    paramContext = initializeApp(paramContext, localFirebaseOptions);
    return paramContext;
  }
  
  public static FirebaseApp initializeApp(Context paramContext, FirebaseOptions paramFirebaseOptions)
  {
    return initializeApp(paramContext, paramFirebaseOptions, "[DEFAULT]");
  }
  
  public static FirebaseApp initializeApp(Context paramContext, FirebaseOptions paramFirebaseOptions, String paramString)
  {
    zzana localzzana = zzana.zzew(paramContext);
    zzep(paramContext);
    paramString = zzrr(paramString);
    if ((paramContext instanceof Application)) {}
    synchronized (zzaok)
    {
      while (!afS.containsKey(paramString))
      {
        bool = true;
        zzac.zza(bool, String.valueOf(paramString).length() + 33 + "FirebaseApp name " + paramString + " already exists!");
        zzac.zzb(paramContext, "Application context cannot be null.");
        paramContext = new FirebaseApp(paramContext, paramString, paramFirebaseOptions);
        afS.put(paramString, paramContext);
        localzzana.zzg(paramContext);
        paramContext.zza(FirebaseApp.class, paramContext, aSH);
        if (paramContext.zzcnu())
        {
          paramContext.zza(FirebaseApp.class, paramContext, aSI);
          paramContext.zza(Context.class, paramContext.getApplicationContext(), aSJ);
        }
        return paramContext;
        paramContext = paramContext.getApplicationContext();
      }
      boolean bool = false;
    }
  }
  
  private <T> void zza(Class<T> paramClass, T paramT, Iterable<String> paramIterable)
  {
    boolean bool;
    if (zzs.zzaxw())
    {
      bool = this.zzask.isDeviceProtectedStorage();
      if (bool) {
        zzc.zzer(this.zzask);
      }
    }
    for (;;)
    {
      Iterator localIterator = paramIterable.iterator();
      for (;;)
      {
        if (localIterator.hasNext())
        {
          paramIterable = (String)localIterator.next();
          if (bool) {}
          try
          {
            if (aSK.contains(paramIterable))
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
            if (aSL.contains(paramIterable)) {
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
      return;
      bool = false;
    }
  }
  
  private void zzcnt()
  {
    if (!this.aSO.get()) {}
    for (boolean bool = true;; bool = false)
    {
      zzac.zza(bool, "FirebaseApp was deleted");
      return;
    }
  }
  
  private static List<String> zzcnw()
  {
    zza localzza = new zza();
    synchronized (zzaok)
    {
      localObject2 = afS.values().iterator();
      if (((Iterator)localObject2).hasNext()) {
        localzza.add(((FirebaseApp)((Iterator)localObject2).next()).getName());
      }
    }
    Object localObject2 = zzana.N();
    if (localObject2 != null) {
      localCollection.addAll(((zzana)localObject2).O());
    }
    ??? = new ArrayList(localCollection);
    Collections.sort((List)???);
    return (List<String>)???;
  }
  
  private void zzcnx()
  {
    zza(FirebaseApp.class, this, aSH);
    if (zzcnu())
    {
      zza(FirebaseApp.class, this, aSI);
      zza(Context.class, this.zzask, aSJ);
    }
  }
  
  public static void zzcp(boolean paramBoolean)
  {
    synchronized (zzaok)
    {
      Iterator localIterator = new ArrayList(afS.values()).iterator();
      while (localIterator.hasNext())
      {
        FirebaseApp localFirebaseApp = (FirebaseApp)localIterator.next();
        if (localFirebaseApp.aSN.get()) {
          localFirebaseApp.zzcq(paramBoolean);
        }
      }
    }
  }
  
  private void zzcq(boolean paramBoolean)
  {
    Log.d("FirebaseApp", "Notifying background state change listeners.");
    Iterator localIterator = this.aSQ.iterator();
    while (localIterator.hasNext()) {
      ((zzb)localIterator.next()).zzcp(paramBoolean);
    }
  }
  
  @TargetApi(14)
  private static void zzep(Context paramContext)
  {
    if ((zzs.zzaxn()) && ((paramContext.getApplicationContext() instanceof Application))) {
      zzamz.zza((Application)paramContext.getApplicationContext());
    }
  }
  
  private static String zzrr(@NonNull String paramString)
  {
    return paramString.trim();
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof FirebaseApp)) {
      return false;
    }
    return this.mName.equals(((FirebaseApp)paramObject).getName());
  }
  
  @NonNull
  public Context getApplicationContext()
  {
    zzcnt();
    return this.zzask;
  }
  
  @NonNull
  public String getName()
  {
    zzcnt();
    return this.mName;
  }
  
  @NonNull
  public FirebaseOptions getOptions()
  {
    zzcnt();
    return this.aSM;
  }
  
  public Task<GetTokenResult> getToken(boolean paramBoolean)
  {
    zzcnt();
    if (this.aSS == null) {
      return Tasks.forException(new FirebaseApiNotAvailableException("firebase-auth is not linked, please fall back to unauthenticated mode."));
    }
    return this.aSS.zzcr(paramBoolean);
  }
  
  public int hashCode()
  {
    return this.mName.hashCode();
  }
  
  public String toString()
  {
    return zzab.zzx(this).zzg("name", this.mName).zzg("options", this.aSM).toString();
  }
  
  public void zza(@NonNull zzanb paramzzanb)
  {
    this.aSS = ((zzanb)zzac.zzy(paramzzanb));
  }
  
  @UiThread
  public void zza(@NonNull zzanc paramzzanc)
  {
    Log.d("FirebaseApp", "Notifying auth state listeners.");
    Iterator localIterator = this.aSP.iterator();
    int i = 0;
    while (localIterator.hasNext())
    {
      ((zza)localIterator.next()).zzb(paramzzanc);
      i += 1;
    }
    Log.d("FirebaseApp", String.format("Notified %d auth state listeners.", new Object[] { Integer.valueOf(i) }));
  }
  
  public void zza(@NonNull zza paramzza)
  {
    zzcnt();
    zzac.zzy(paramzza);
    this.aSP.add(paramzza);
  }
  
  public void zza(zzb paramzzb)
  {
    zzcnt();
    if ((this.aSN.get()) && (zzamz.L().M())) {
      paramzzb.zzcp(true);
    }
    this.aSQ.add(paramzzb);
  }
  
  public boolean zzcnu()
  {
    return "[DEFAULT]".equals(getName());
  }
  
  public String zzcnv()
  {
    String str1 = String.valueOf(zzc.zzr(getName().getBytes()));
    String str2 = String.valueOf(zzc.zzr(getOptions().getApplicationId().getBytes()));
    return String.valueOf(str1).length() + 1 + String.valueOf(str2).length() + str1 + "+" + str2;
  }
  
  public static abstract interface zza
  {
    public abstract void zzb(@NonNull zzanc paramzzanc);
  }
  
  public static abstract interface zzb
  {
    public abstract void zzcp(boolean paramBoolean);
  }
  
  @TargetApi(24)
  private static class zzc
    extends BroadcastReceiver
  {
    private static AtomicReference<zzc> aST = new AtomicReference();
    private final Context zzask;
    
    public zzc(Context paramContext)
    {
      this.zzask = paramContext;
    }
    
    private static void zzeq(Context paramContext)
    {
      if (aST.get() == null)
      {
        zzc localzzc = new zzc(paramContext);
        if (aST.compareAndSet(null, localzzc))
        {
          IntentFilter localIntentFilter = new IntentFilter("android.intent.action.USER_UNLOCKED");
          localIntentFilter.addDataScheme("package");
          paramContext.registerReceiver(localzzc, localIntentFilter);
        }
      }
    }
    
    public void onReceive(Context arg1, Intent paramIntent)
    {
      synchronized ()
      {
        paramIntent = FirebaseApp.afS.values().iterator();
        if (paramIntent.hasNext()) {
          FirebaseApp.zza((FirebaseApp)paramIntent.next());
        }
      }
      unregister();
    }
    
    public void unregister()
    {
      this.zzask.unregisterReceiver(this);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/firebase/FirebaseApp.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */