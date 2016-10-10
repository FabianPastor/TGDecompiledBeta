package com.google.android.gms.measurement.internal;

import android.os.Binder;
import android.support.annotation.BinderThread;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzac;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class zzy
  extends zzm.zza
{
  private final zzx anq;
  private Boolean art;
  @Nullable
  private String aru;
  
  public zzy(zzx paramzzx)
  {
    this(paramzzx, null);
  }
  
  public zzy(zzx paramzzx, @Nullable String paramString)
  {
    zzac.zzy(paramzzx);
    this.anq = paramzzx;
    this.aru = paramString;
  }
  
  @BinderThread
  private void zzf(AppMetadata paramAppMetadata)
  {
    zzac.zzy(paramAppMetadata);
    zzmt(paramAppMetadata.packageName);
    this.anq.zzbvc().zzne(paramAppMetadata.anQ);
  }
  
  @BinderThread
  private void zzmt(String paramString)
    throws SecurityException
  {
    if (TextUtils.isEmpty(paramString))
    {
      this.anq.zzbvg().zzbwc().log("Measurement Service called without app package");
      throw new SecurityException("Measurement Service called without app package");
    }
    try
    {
      zzmu(paramString);
      return;
    }
    catch (SecurityException localSecurityException)
    {
      this.anq.zzbvg().zzbwc().zzj("Measurement Service called with invalid calling package", paramString);
      throw localSecurityException;
    }
  }
  
  /* Error */
  @BinderThread
  public List<UserAttributeParcel> zza(final AppMetadata paramAppMetadata, boolean paramBoolean)
  {
    // Byte code:
    //   0: aload_0
    //   1: aload_1
    //   2: invokespecial 121	com/google/android/gms/measurement/internal/zzy:zzf	(Lcom/google/android/gms/measurement/internal/AppMetadata;)V
    //   5: aload_0
    //   6: getfield 44	com/google/android/gms/measurement/internal/zzy:anq	Lcom/google/android/gms/measurement/internal/zzx;
    //   9: invokevirtual 125	com/google/android/gms/measurement/internal/zzx:zzbvf	()Lcom/google/android/gms/measurement/internal/zzw;
    //   12: new 18	com/google/android/gms/measurement/internal/zzy$7
    //   15: dup
    //   16: aload_0
    //   17: aload_1
    //   18: invokespecial 128	com/google/android/gms/measurement/internal/zzy$7:<init>	(Lcom/google/android/gms/measurement/internal/zzy;Lcom/google/android/gms/measurement/internal/AppMetadata;)V
    //   21: invokevirtual 134	com/google/android/gms/measurement/internal/zzw:zzd	(Ljava/util/concurrent/Callable;)Ljava/util/concurrent/Future;
    //   24: astore_1
    //   25: aload_1
    //   26: invokeinterface 140 1 0
    //   31: checkcast 142	java/util/List
    //   34: astore_3
    //   35: new 144	java/util/ArrayList
    //   38: dup
    //   39: aload_3
    //   40: invokeinterface 148 1 0
    //   45: invokespecial 151	java/util/ArrayList:<init>	(I)V
    //   48: astore_1
    //   49: aload_3
    //   50: invokeinterface 155 1 0
    //   55: astore_3
    //   56: aload_3
    //   57: invokeinterface 161 1 0
    //   62: ifeq +67 -> 129
    //   65: aload_3
    //   66: invokeinterface 164 1 0
    //   71: checkcast 166	com/google/android/gms/measurement/internal/zzak
    //   74: astore 4
    //   76: iload_2
    //   77: ifne +14 -> 91
    //   80: aload 4
    //   82: getfield 169	com/google/android/gms/measurement/internal/zzak:mName	Ljava/lang/String;
    //   85: invokestatic 172	com/google/android/gms/measurement/internal/zzal:zznh	(Ljava/lang/String;)Z
    //   88: ifne -32 -> 56
    //   91: aload_1
    //   92: new 174	com/google/android/gms/measurement/internal/UserAttributeParcel
    //   95: dup
    //   96: aload 4
    //   98: invokespecial 177	com/google/android/gms/measurement/internal/UserAttributeParcel:<init>	(Lcom/google/android/gms/measurement/internal/zzak;)V
    //   101: invokeinterface 181 2 0
    //   106: pop
    //   107: goto -51 -> 56
    //   110: astore_1
    //   111: aload_0
    //   112: getfield 44	com/google/android/gms/measurement/internal/zzy:anq	Lcom/google/android/gms/measurement/internal/zzx;
    //   115: invokevirtual 89	com/google/android/gms/measurement/internal/zzx:zzbvg	()Lcom/google/android/gms/measurement/internal/zzp;
    //   118: invokevirtual 95	com/google/android/gms/measurement/internal/zzp:zzbwc	()Lcom/google/android/gms/measurement/internal/zzp$zza;
    //   121: ldc -73
    //   123: aload_1
    //   124: invokevirtual 113	com/google/android/gms/measurement/internal/zzp$zza:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   127: aconst_null
    //   128: areturn
    //   129: aload_1
    //   130: areturn
    //   131: astore_1
    //   132: goto -21 -> 111
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	135	0	this	zzy
    //   0	135	1	paramAppMetadata	AppMetadata
    //   0	135	2	paramBoolean	boolean
    //   34	32	3	localObject	Object
    //   74	23	4	localzzak	zzak
    // Exception table:
    //   from	to	target	type
    //   25	56	110	java/lang/InterruptedException
    //   56	76	110	java/lang/InterruptedException
    //   80	91	110	java/lang/InterruptedException
    //   91	107	110	java/lang/InterruptedException
    //   25	56	131	java/util/concurrent/ExecutionException
    //   56	76	131	java/util/concurrent/ExecutionException
    //   80	91	131	java/util/concurrent/ExecutionException
    //   91	107	131	java/util/concurrent/ExecutionException
  }
  
  @BinderThread
  public void zza(final AppMetadata paramAppMetadata)
  {
    zzf(paramAppMetadata);
    this.anq.zzbvf().zzm(new Runnable()
    {
      public void run()
      {
        zzy.zza(zzy.this).zzbxp();
        zzy.this.zzms(paramAppMetadata.anU);
        zzy.zza(zzy.this).zzd(paramAppMetadata);
      }
    });
  }
  
  @BinderThread
  public void zza(final EventParcel paramEventParcel, final AppMetadata paramAppMetadata)
  {
    zzac.zzy(paramEventParcel);
    zzf(paramAppMetadata);
    this.anq.zzbvf().zzm(new Runnable()
    {
      public void run()
      {
        zzy.zza(zzy.this).zzbxp();
        zzy.this.zzms(paramAppMetadata.anU);
        zzy.zza(zzy.this).zzb(paramEventParcel, paramAppMetadata);
      }
    });
  }
  
  @BinderThread
  public void zza(final EventParcel paramEventParcel, final String paramString1, final String paramString2)
  {
    zzac.zzy(paramEventParcel);
    zzac.zzhz(paramString1);
    zzmt(paramString1);
    this.anq.zzbvf().zzm(new Runnable()
    {
      public void run()
      {
        zzy.zza(zzy.this).zzbxp();
        zzy.this.zzms(paramString2);
        zzy.zza(zzy.this).zzb(paramEventParcel, paramString1);
      }
    });
  }
  
  @BinderThread
  public void zza(final UserAttributeParcel paramUserAttributeParcel, final AppMetadata paramAppMetadata)
  {
    zzac.zzy(paramUserAttributeParcel);
    zzf(paramAppMetadata);
    if (paramUserAttributeParcel.getValue() == null)
    {
      this.anq.zzbvf().zzm(new Runnable()
      {
        public void run()
        {
          zzy.zza(zzy.this).zzbxp();
          zzy.this.zzms(paramAppMetadata.anU);
          zzy.zza(zzy.this).zzc(paramUserAttributeParcel, paramAppMetadata);
        }
      });
      return;
    }
    this.anq.zzbvf().zzm(new Runnable()
    {
      public void run()
      {
        zzy.zza(zzy.this).zzbxp();
        zzy.this.zzms(paramAppMetadata.anU);
        zzy.zza(zzy.this).zzb(paramUserAttributeParcel, paramAppMetadata);
      }
    });
  }
  
  @BinderThread
  public byte[] zza(final EventParcel paramEventParcel, final String paramString)
  {
    zzac.zzhz(paramString);
    zzac.zzy(paramEventParcel);
    zzmt(paramString);
    this.anq.zzbvg().zzbwi().zzj("Log and bundle. event", paramEventParcel.name);
    long l1 = this.anq.zzaan().nanoTime() / 1000000L;
    paramString = this.anq.zzbvf().zze(new Callable()
    {
      public byte[] zzbxr()
        throws Exception
      {
        zzy.zza(zzy.this).zzbxp();
        return zzy.zza(zzy.this).zza(paramEventParcel, paramString);
      }
    });
    try
    {
      byte[] arrayOfByte = (byte[])paramString.get();
      paramString = arrayOfByte;
      if (arrayOfByte == null)
      {
        this.anq.zzbvg().zzbwc().log("Log and bundle returned null");
        paramString = new byte[0];
      }
      long l2 = this.anq.zzaan().nanoTime() / 1000000L;
      this.anq.zzbvg().zzbwi().zzd("Log and bundle processed. event, size, time_ms", paramEventParcel.name, Integer.valueOf(paramString.length), Long.valueOf(l2 - l1));
      return paramString;
    }
    catch (InterruptedException paramString)
    {
      this.anq.zzbvg().zzbwc().zze("Failed to log and bundle. event, error", paramEventParcel.name, paramString);
      return null;
    }
    catch (ExecutionException paramString)
    {
      for (;;) {}
    }
  }
  
  @BinderThread
  public void zzb(final AppMetadata paramAppMetadata)
  {
    zzf(paramAppMetadata);
    this.anq.zzbvf().zzm(new Runnable()
    {
      public void run()
      {
        zzy.zza(zzy.this).zzbxp();
        zzy.this.zzms(paramAppMetadata.anU);
        zzy.zza(zzy.this).zzc(paramAppMetadata);
      }
    });
  }
  
  @WorkerThread
  void zzms(String paramString)
  {
    if (!TextUtils.isEmpty(paramString))
    {
      paramString = paramString.split(":", 2);
      if (paramString.length != 2) {}
    }
    long l;
    try
    {
      l = Long.valueOf(paramString[0]).longValue();
      if (l > 0L)
      {
        this.anq.zzbvh().apP.zzi(paramString[1], l);
        return;
      }
    }
    catch (NumberFormatException localNumberFormatException)
    {
      this.anq.zzbvg().zzbwe().zzj("Combining sample with a non-number weight", paramString[0]);
      return;
    }
    this.anq.zzbvg().zzbwe().zzj("Combining sample with a non-positive weight", Long.valueOf(l));
  }
  
  protected void zzmu(String paramString)
    throws SecurityException
  {
    if ((this.aru == null) && (com.google.android.gms.common.zze.zzb(this.anq.getContext(), Binder.getCallingUid(), paramString))) {
      this.aru = paramString;
    }
    if (paramString.equals(this.aru)) {
      return;
    }
    if (this.art == null) {
      if (((!"com.google.android.gms".equals(this.aru)) && (!com.google.android.gms.common.util.zzy.zzf(this.anq.getContext(), Binder.getCallingUid()))) || (this.anq.zzbxg())) {
        break label129;
      }
    }
    label129:
    for (boolean bool = true;; bool = false)
    {
      this.art = Boolean.valueOf(bool);
      if (this.art.booleanValue()) {
        break;
      }
      throw new SecurityException(String.format("Unknown calling package name '%s'.", new Object[] { paramString }));
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/measurement/internal/zzy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */