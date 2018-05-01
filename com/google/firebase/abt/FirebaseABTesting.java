package com.google.firebase.abt;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.internal.firebase_abt.zzi;
import com.google.android.gms.internal.firebase_abt.zzj;
import com.google.android.gms.internal.firebase_abt.zzo;
import com.google.android.gms.measurement.AppMeasurement;
import com.google.android.gms.measurement.AppMeasurement.ConditionalUserProperty;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class FirebaseABTesting
{
  private AppMeasurement zza;
  private String zzb;
  private int zzc;
  private long zzd;
  private SharedPreferences zze;
  private String zzf;
  private Integer zzg;
  
  public FirebaseABTesting(Context paramContext, String paramString, int paramInt)
    throws NoClassDefFoundError
  {
    this.zza = AppMeasurement.getInstance(paramContext);
    this.zzb = paramString;
    this.zzc = paramInt;
    this.zzg = null;
    this.zze = paramContext.getSharedPreferences("com.google.firebase.abt", 0);
    this.zzf = String.format("%s_lastKnownExperimentStartTime", new Object[] { paramString });
    this.zzd = this.zze.getLong(this.zzf, 0L);
  }
  
  private static zzo zza(byte[] paramArrayOfByte)
  {
    try
    {
      zzo localzzo = new com/google/android/gms/internal/firebase_abt/zzo;
      localzzo.<init>();
      paramArrayOfByte = (zzo)zzj.zza(localzzo, paramArrayOfByte, 0, paramArrayOfByte.length);
      return paramArrayOfByte;
    }
    catch (zzi paramArrayOfByte)
    {
      for (;;)
      {
        Log.e("FirebaseABTesting", "Payload was not defined or could not be deserialized.", paramArrayOfByte);
        paramArrayOfByte = null;
      }
    }
  }
  
  private final void zza()
  {
    if (this.zze.getLong(this.zzf, 0L) == this.zzd) {}
    for (;;)
    {
      return;
      SharedPreferences.Editor localEditor = this.zze.edit();
      localEditor.putLong(this.zzf, this.zzd);
      localEditor.apply();
    }
  }
  
  private final void zza(String paramString)
  {
    this.zza.clearConditionalUserProperty(paramString, null, null);
  }
  
  private final void zza(Collection<AppMeasurement.ConditionalUserProperty> paramCollection)
  {
    paramCollection = paramCollection.iterator();
    while (paramCollection.hasNext()) {
      zza(((AppMeasurement.ConditionalUserProperty)paramCollection.next()).mName);
    }
  }
  
  private final boolean zza(zzo paramzzo)
  {
    boolean bool1 = false;
    int i = paramzzo.zzc;
    int j = this.zzc;
    if (i != 0)
    {
      if (i == 1) {
        break label85;
      }
      bool2 = bool1;
      if (Log.isLoggable("FirebaseABTesting", 3)) {
        Log.d("FirebaseABTesting", String.format("Experiment won't be set due to the overflow policy: [%s, %s]", new Object[] { paramzzo.zzaq, paramzzo.zzar }));
      }
    }
    label85:
    for (boolean bool2 = bool1;; bool2 = true)
    {
      return bool2;
      if (j != 0)
      {
        i = j;
        break;
      }
      i = 1;
      break;
    }
  }
  
  private final AppMeasurement.ConditionalUserProperty zzb(zzo paramzzo)
  {
    AppMeasurement.ConditionalUserProperty localConditionalUserProperty = new AppMeasurement.ConditionalUserProperty();
    localConditionalUserProperty.mOrigin = this.zzb;
    localConditionalUserProperty.mCreationTimestamp = paramzzo.zzas;
    localConditionalUserProperty.mName = paramzzo.zzaq;
    localConditionalUserProperty.mValue = paramzzo.zzar;
    if (TextUtils.isEmpty(paramzzo.zzat)) {}
    for (String str = null;; str = paramzzo.zzat)
    {
      localConditionalUserProperty.mTriggerEventName = str;
      localConditionalUserProperty.mTriggerTimeout = paramzzo.zzau;
      localConditionalUserProperty.mTimeToLive = paramzzo.zzav;
      return localConditionalUserProperty;
    }
  }
  
  private final List<AppMeasurement.ConditionalUserProperty> zzb()
  {
    return this.zza.getConditionalUserProperties(this.zzb, "");
  }
  
  private final int zzc()
  {
    if (this.zzg == null) {
      this.zzg = Integer.valueOf(this.zza.getMaxUserProperties(this.zzb));
    }
    return this.zzg.intValue();
  }
  
  public void removeAllExperiments()
  {
    zza(zzb());
  }
  
  public void replaceAllExperiments(List<byte[]> paramList)
  {
    if (paramList == null) {
      Log.e("FirebaseABTesting", "Cannot replace experiments because experimentPayloads is null.");
    }
    Object localObject1;
    for (;;)
    {
      return;
      if (paramList.isEmpty())
      {
        removeAllExperiments();
      }
      else
      {
        localObject1 = new ArrayList();
        localObject2 = paramList.iterator();
        while (((Iterator)localObject2).hasNext())
        {
          paramList = zza((byte[])((Iterator)localObject2).next());
          if (paramList != null) {
            ((ArrayList)localObject1).add(paramList);
          }
        }
        if (!((List)localObject1).isEmpty()) {
          break;
        }
        Log.e("FirebaseABTesting", "All payloads are either not defined or cannot not be deserialized.");
      }
    }
    Object localObject2 = new HashSet();
    paramList = (ArrayList)localObject1;
    int i = paramList.size();
    int j = 0;
    while (j < i)
    {
      localObject3 = paramList.get(j);
      j++;
      ((Set)localObject2).add(((zzo)localObject3).zzaq);
    }
    Object localObject4 = zzb();
    paramList = new HashSet();
    Object localObject3 = ((List)localObject4).iterator();
    while (((Iterator)localObject3).hasNext()) {
      paramList.add(((AppMeasurement.ConditionalUserProperty)((Iterator)localObject3).next()).mName);
    }
    localObject3 = new ArrayList();
    localObject4 = ((List)localObject4).iterator();
    while (((Iterator)localObject4).hasNext())
    {
      AppMeasurement.ConditionalUserProperty localConditionalUserProperty = (AppMeasurement.ConditionalUserProperty)((Iterator)localObject4).next();
      if (!((Set)localObject2).contains(localConditionalUserProperty.mName)) {
        ((ArrayList)localObject3).add(localConditionalUserProperty);
      }
    }
    zza((Collection)localObject3);
    localObject2 = new ArrayList();
    localObject4 = (ArrayList)localObject1;
    int k = ((ArrayList)localObject4).size();
    j = 0;
    if (j < k)
    {
      localObject3 = (zzo)((ArrayList)localObject4).get(j);
      if (!paramList.contains(((zzo)localObject3).zzaq))
      {
        if (((zzo)localObject3).zzas > this.zzd) {
          break label437;
        }
        if (Log.isLoggable("FirebaseABTesting", 3)) {
          Log.d("FirebaseABTesting", String.format("The experiment [%s, %s, %d] is not new since its startTime is before lastKnownStartTime: %d", new Object[] { ((zzo)localObject3).zzaq, ((zzo)localObject3).zzar, Long.valueOf(((zzo)localObject3).zzas), Long.valueOf(this.zzd) }));
        }
      }
      label437:
      for (i = 0;; i = 1)
      {
        if (i != 0) {
          ((ArrayList)localObject2).add(localObject3);
        }
        j++;
        break;
      }
    }
    paramList = new ArrayDeque(zzb());
    k = zzc();
    localObject2 = (ArrayList)localObject2;
    i = ((ArrayList)localObject2).size();
    j = 0;
    for (;;)
    {
      if (j < i)
      {
        localObject3 = ((ArrayList)localObject2).get(j);
        j++;
        localObject3 = (zzo)localObject3;
        if (paramList.size() >= k)
        {
          if (zza((zzo)localObject3)) {
            while (paramList.size() >= k) {
              zza(((AppMeasurement.ConditionalUserProperty)paramList.pollFirst()).mName);
            }
          }
        }
        else
        {
          localObject3 = zzb((zzo)localObject3);
          this.zza.setConditionalUserProperty((AppMeasurement.ConditionalUserProperty)localObject3);
          paramList.offer(localObject3);
        }
      }
      else
      {
        paramList = (ArrayList)localObject1;
        i = paramList.size();
        j = 0;
        while (j < i)
        {
          localObject1 = paramList.get(j);
          j++;
          localObject1 = (zzo)localObject1;
          this.zzd = Math.max(this.zzd, ((zzo)localObject1).zzas);
        }
        zza();
        break;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/firebase/abt/FirebaseABTesting.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */