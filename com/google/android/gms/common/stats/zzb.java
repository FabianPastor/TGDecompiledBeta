package com.google.android.gms.common.stats;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.Debug;
import android.os.Process;
import android.os.SystemClock;
import android.util.Log;
import com.google.android.gms.common.util.zzt;
import com.google.android.gms.internal.zzrs;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class zzb
{
  private static final Object Cz = new Object();
  private static zzb DX;
  private static Integer Ed;
  private final List<String> DY;
  private final List<String> DZ;
  private final List<String> Ea;
  private final List<String> Eb;
  private zze Ec;
  private zze Ee;
  
  private zzb()
  {
    if (getLogLevel() == zzd.LOG_LEVEL_OFF)
    {
      this.DY = Collections.EMPTY_LIST;
      this.DZ = Collections.EMPTY_LIST;
      this.Ea = Collections.EMPTY_LIST;
      this.Eb = Collections.EMPTY_LIST;
      return;
    }
    Object localObject = (String)zzc.zza.Ei.get();
    if (localObject == null)
    {
      localObject = Collections.EMPTY_LIST;
      this.DY = ((List)localObject);
      localObject = (String)zzc.zza.Ej.get();
      if (localObject != null) {
        break label200;
      }
      localObject = Collections.EMPTY_LIST;
      label83:
      this.DZ = ((List)localObject);
      localObject = (String)zzc.zza.Ek.get();
      if (localObject != null) {
        break label213;
      }
      localObject = Collections.EMPTY_LIST;
      label106:
      this.Ea = ((List)localObject);
      localObject = (String)zzc.zza.El.get();
      if (localObject != null) {
        break label226;
      }
    }
    label200:
    label213:
    label226:
    for (localObject = Collections.EMPTY_LIST;; localObject = Arrays.asList(((String)localObject).split(",")))
    {
      this.Eb = ((List)localObject);
      this.Ec = new zze(1024, ((Long)zzc.zza.Em.get()).longValue());
      this.Ee = new zze(1024, ((Long)zzc.zza.Em.get()).longValue());
      return;
      localObject = Arrays.asList(((String)localObject).split(","));
      break;
      localObject = Arrays.asList(((String)localObject).split(","));
      break label83;
      localObject = Arrays.asList(((String)localObject).split(","));
      break label106;
    }
  }
  
  private static int getLogLevel()
  {
    if (Ed == null) {}
    for (;;)
    {
      try
      {
        if (!com.google.android.gms.common.util.zzd.zzact()) {
          continue;
        }
        i = ((Integer)zzc.zza.Eh.get()).intValue();
        Ed = Integer.valueOf(i);
      }
      catch (SecurityException localSecurityException)
      {
        int i;
        Ed = Integer.valueOf(zzd.LOG_LEVEL_OFF);
        continue;
      }
      return Ed.intValue();
      i = zzd.LOG_LEVEL_OFF;
    }
  }
  
  private static String zza(StackTraceElement[] paramArrayOfStackTraceElement, int paramInt)
  {
    if (paramInt + 4 >= paramArrayOfStackTraceElement.length) {
      return "<bottom of call stack>";
    }
    paramArrayOfStackTraceElement = paramArrayOfStackTraceElement[(paramInt + 4)];
    String str1 = String.valueOf(paramArrayOfStackTraceElement.getClassName());
    String str2 = String.valueOf(paramArrayOfStackTraceElement.getMethodName());
    paramInt = paramArrayOfStackTraceElement.getLineNumber();
    return String.valueOf(str1).length() + 13 + String.valueOf(str2).length() + str1 + "." + str2 + ":" + paramInt;
  }
  
  private void zza(Context paramContext, String paramString1, int paramInt, String paramString2, String paramString3, String paramString4, String paramString5)
  {
    long l2 = System.currentTimeMillis();
    Object localObject2 = null;
    Object localObject1 = localObject2;
    if ((getLogLevel() & zzd.Er) != 0)
    {
      localObject1 = localObject2;
      if (paramInt != 13) {
        localObject1 = zzm(3, 5);
      }
    }
    long l1 = 0L;
    if ((getLogLevel() & zzd.Et) != 0) {
      l1 = Debug.getNativeHeapAllocatedSize();
    }
    if ((paramInt == 1) || (paramInt == 4) || (paramInt == 14)) {}
    for (paramString1 = new ConnectionEvent(l2, paramInt, null, null, null, null, (String)localObject1, paramString1, SystemClock.elapsedRealtime(), l1);; paramString1 = new ConnectionEvent(l2, paramInt, paramString2, paramString3, paramString4, paramString5, (String)localObject1, paramString1, SystemClock.elapsedRealtime(), l1))
    {
      paramContext.startService(new Intent().setComponent(zzd.En).putExtra("com.google.android.gms.common.stats.EXTRA_LOG_EVENT", paramString1));
      return;
    }
  }
  
  private void zza(Context paramContext, String paramString1, String paramString2, Intent paramIntent, int paramInt)
  {
    Object localObject2 = null;
    if ((!zzawv()) || (this.Ec == null)) {}
    do
    {
      return;
      if ((paramInt != 4) && (paramInt != 1)) {
        break;
      }
    } while (!this.Ec.zzig(paramString1));
    Object localObject1 = null;
    String str = null;
    paramIntent = (Intent)localObject2;
    for (;;)
    {
      zza(paramContext, paramString1, paramInt, paramIntent, paramString2, str, (String)localObject1);
      return;
      localObject1 = zzd(paramContext, paramIntent);
      if (localObject1 == null)
      {
        Log.w("ConnectionTracker", String.format("Client %s made an invalid request %s", new Object[] { paramString2, paramIntent.toUri(0) }));
        return;
      }
      str = ((ServiceInfo)localObject1).processName;
      localObject1 = ((ServiceInfo)localObject1).name;
      paramIntent = zzt.zzaxx();
      if (!zzb(paramIntent, paramString2, str, (String)localObject1)) {
        break;
      }
      this.Ec.zzif(paramString1);
    }
  }
  
  public static zzb zzawu()
  {
    synchronized (Cz)
    {
      if (DX == null) {
        DX = new zzb();
      }
      return DX;
    }
  }
  
  private boolean zzawv()
  {
    return false;
  }
  
  private String zzb(ServiceConnection paramServiceConnection)
  {
    return String.valueOf(Process.myPid() << 32 | System.identityHashCode(paramServiceConnection));
  }
  
  private boolean zzb(String paramString1, String paramString2, String paramString3, String paramString4)
  {
    int i = getLogLevel();
    return (!this.DY.contains(paramString1)) && (!this.DZ.contains(paramString2)) && (!this.Ea.contains(paramString3)) && (!this.Eb.contains(paramString4)) && ((!paramString3.equals(paramString1)) || ((i & zzd.Es) == 0));
  }
  
  private boolean zzc(Context paramContext, Intent paramIntent)
  {
    paramIntent = paramIntent.getComponent();
    if (paramIntent == null) {
      return false;
    }
    return com.google.android.gms.common.util.zzd.zzx(paramContext, paramIntent.getPackageName());
  }
  
  private static ServiceInfo zzd(Context paramContext, Intent paramIntent)
  {
    paramContext = paramContext.getPackageManager().queryIntentServices(paramIntent, 128);
    if ((paramContext == null) || (paramContext.size() == 0))
    {
      Log.w("ConnectionTracker", String.format("There are no handler of this intent: %s\n Stack trace: %s", new Object[] { paramIntent.toUri(0), zzm(3, 20) }));
      return null;
    }
    if (paramContext.size() > 1)
    {
      Log.w("ConnectionTracker", String.format("Multiple handlers found for this intent: %s\n Stack trace: %s", new Object[] { paramIntent.toUri(0), zzm(3, 20) }));
      paramContext = paramContext.iterator();
      while (paramContext.hasNext()) {
        Log.w("ConnectionTracker", ((ResolveInfo)paramContext.next()).serviceInfo.name);
      }
      return null;
    }
    return ((ResolveInfo)paramContext.get(0)).serviceInfo;
  }
  
  private static String zzm(int paramInt1, int paramInt2)
  {
    StackTraceElement[] arrayOfStackTraceElement = Thread.currentThread().getStackTrace();
    StringBuffer localStringBuffer = new StringBuffer();
    int i = paramInt1;
    while (i < paramInt2 + paramInt1)
    {
      localStringBuffer.append(zza(arrayOfStackTraceElement, i)).append(" ");
      i += 1;
    }
    return localStringBuffer.toString();
  }
  
  @SuppressLint({"UntrackedBindService"})
  public void zza(Context paramContext, ServiceConnection paramServiceConnection)
  {
    paramContext.unbindService(paramServiceConnection);
    zza(paramContext, zzb(paramServiceConnection), null, null, 1);
  }
  
  public void zza(Context paramContext, ServiceConnection paramServiceConnection, String paramString, Intent paramIntent)
  {
    zza(paramContext, zzb(paramServiceConnection), paramString, paramIntent, 3);
  }
  
  public boolean zza(Context paramContext, Intent paramIntent, ServiceConnection paramServiceConnection, int paramInt)
  {
    return zza(paramContext, paramContext.getClass().getName(), paramIntent, paramServiceConnection, paramInt);
  }
  
  @SuppressLint({"UntrackedBindService"})
  public boolean zza(Context paramContext, String paramString, Intent paramIntent, ServiceConnection paramServiceConnection, int paramInt)
  {
    if (zzc(paramContext, paramIntent))
    {
      Log.w("ConnectionTracker", "Attempted to bind to a service in a STOPPED package.");
      return false;
    }
    boolean bool = paramContext.bindService(paramIntent, paramServiceConnection, paramInt);
    if (bool) {
      zza(paramContext, zzb(paramServiceConnection), paramString, paramIntent, 2);
    }
    return bool;
  }
  
  public void zzb(Context paramContext, ServiceConnection paramServiceConnection)
  {
    zza(paramContext, zzb(paramServiceConnection), null, null, 4);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/stats/zzb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */