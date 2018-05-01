package com.google.firebase.iid;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import javax.annotation.concurrent.GuardedBy;

public final class zzk
{
  @GuardedBy("MessengerIpcClient.class")
  private static zzk zzbqv;
  private final ScheduledExecutorService zzbqw;
  @GuardedBy("this")
  private zzm zzbqx = new zzm(this, null);
  @GuardedBy("this")
  private int zzbqy = 1;
  private final Context zzqs;
  
  private zzk(Context paramContext, ScheduledExecutorService paramScheduledExecutorService)
  {
    this.zzbqw = paramScheduledExecutorService;
    this.zzqs = paramContext.getApplicationContext();
  }
  
  private final <T> Task<T> zza(zzt<T> paramzzt)
  {
    try
    {
      Object localObject;
      if (Log.isLoggable("MessengerIpcClient", 3))
      {
        localObject = String.valueOf(paramzzt);
        int i = String.valueOf(localObject).length();
        StringBuilder localStringBuilder = new java/lang/StringBuilder;
        localStringBuilder.<init>(i + 9);
        Log.d("MessengerIpcClient", "Queueing " + (String)localObject);
      }
      if (!this.zzbqx.zzb(paramzzt))
      {
        localObject = new com/google/firebase/iid/zzm;
        ((zzm)localObject).<init>(this, null);
        this.zzbqx = ((zzm)localObject);
        this.zzbqx.zzb(paramzzt);
      }
      paramzzt = paramzzt.zzbri.getTask();
      return paramzzt;
    }
    finally {}
  }
  
  private final int zzsp()
  {
    try
    {
      int i = this.zzbqy;
      this.zzbqy = (i + 1);
      return i;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public static zzk zzv(Context paramContext)
  {
    try
    {
      if (zzbqv == null)
      {
        zzk localzzk = new com/google/firebase/iid/zzk;
        localzzk.<init>(paramContext, Executors.newSingleThreadScheduledExecutor());
        zzbqv = localzzk;
      }
      paramContext = zzbqv;
      return paramContext;
    }
    finally {}
  }
  
  public final Task<Void> zza(int paramInt, Bundle paramBundle)
  {
    return zza(new zzs(zzsp(), 2, paramBundle));
  }
  
  public final Task<Bundle> zzb(int paramInt, Bundle paramBundle)
  {
    return zza(new zzv(zzsp(), 1, paramBundle));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/firebase/iid/zzk.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */