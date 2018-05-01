package com.google.android.gms.measurement.internal;

import android.content.Context;
import android.os.Binder;
import android.support.annotation.BinderThread;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.common.zzf;
import com.google.android.gms.measurement.AppMeasurement.zzf;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class zzy
  extends zzm.zza
{
  private final zzx aqw;
  private Boolean auE;
  @Nullable
  private String auF;
  
  public zzy(zzx paramzzx)
  {
    this(paramzzx, null);
  }
  
  public zzy(zzx paramzzx, @Nullable String paramString)
  {
    zzaa.zzy(paramzzx);
    this.aqw = paramzzx;
    this.auF = paramString;
  }
  
  @BinderThread
  private void zzb(AppMetadata paramAppMetadata, boolean paramBoolean)
  {
    zzaa.zzy(paramAppMetadata);
    zzn(paramAppMetadata.packageName, paramBoolean);
    this.aqw.zzbvx().zznb(paramAppMetadata.aqZ);
  }
  
  @BinderThread
  private void zzn(String paramString, boolean paramBoolean)
    throws SecurityException
  {
    if (TextUtils.isEmpty(paramString))
    {
      this.aqw.zzbwb().zzbwy().log("Measurement Service called without app package");
      throw new SecurityException("Measurement Service called without app package");
    }
    try
    {
      zzo(paramString, paramBoolean);
      return;
    }
    catch (SecurityException localSecurityException)
    {
      this.aqw.zzbwb().zzbwy().zzj("Measurement Service called with invalid calling package", paramString);
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
    //   2: iconst_0
    //   3: invokespecial 124	com/google/android/gms/measurement/internal/zzy:zzb	(Lcom/google/android/gms/measurement/internal/AppMetadata;Z)V
    //   6: aload_0
    //   7: getfield 46	com/google/android/gms/measurement/internal/zzy:aqw	Lcom/google/android/gms/measurement/internal/zzx;
    //   10: invokevirtual 128	com/google/android/gms/measurement/internal/zzx:zzbwa	()Lcom/google/android/gms/measurement/internal/zzw;
    //   13: new 18	com/google/android/gms/measurement/internal/zzy$7
    //   16: dup
    //   17: aload_0
    //   18: aload_1
    //   19: invokespecial 131	com/google/android/gms/measurement/internal/zzy$7:<init>	(Lcom/google/android/gms/measurement/internal/zzy;Lcom/google/android/gms/measurement/internal/AppMetadata;)V
    //   22: invokevirtual 137	com/google/android/gms/measurement/internal/zzw:zzd	(Ljava/util/concurrent/Callable;)Ljava/util/concurrent/Future;
    //   25: astore_1
    //   26: aload_1
    //   27: invokeinterface 143 1 0
    //   32: checkcast 145	java/util/List
    //   35: astore_3
    //   36: new 147	java/util/ArrayList
    //   39: dup
    //   40: aload_3
    //   41: invokeinterface 151 1 0
    //   46: invokespecial 154	java/util/ArrayList:<init>	(I)V
    //   49: astore_1
    //   50: aload_3
    //   51: invokeinterface 158 1 0
    //   56: astore_3
    //   57: aload_3
    //   58: invokeinterface 164 1 0
    //   63: ifeq +67 -> 130
    //   66: aload_3
    //   67: invokeinterface 167 1 0
    //   72: checkcast 169	com/google/android/gms/measurement/internal/zzak
    //   75: astore 4
    //   77: iload_2
    //   78: ifne +14 -> 92
    //   81: aload 4
    //   83: getfield 172	com/google/android/gms/measurement/internal/zzak:mName	Ljava/lang/String;
    //   86: invokestatic 175	com/google/android/gms/measurement/internal/zzal:zzne	(Ljava/lang/String;)Z
    //   89: ifne -32 -> 57
    //   92: aload_1
    //   93: new 177	com/google/android/gms/measurement/internal/UserAttributeParcel
    //   96: dup
    //   97: aload 4
    //   99: invokespecial 180	com/google/android/gms/measurement/internal/UserAttributeParcel:<init>	(Lcom/google/android/gms/measurement/internal/zzak;)V
    //   102: invokeinterface 184 2 0
    //   107: pop
    //   108: goto -51 -> 57
    //   111: astore_1
    //   112: aload_0
    //   113: getfield 46	com/google/android/gms/measurement/internal/zzy:aqw	Lcom/google/android/gms/measurement/internal/zzx;
    //   116: invokevirtual 91	com/google/android/gms/measurement/internal/zzx:zzbwb	()Lcom/google/android/gms/measurement/internal/zzq;
    //   119: invokevirtual 97	com/google/android/gms/measurement/internal/zzq:zzbwy	()Lcom/google/android/gms/measurement/internal/zzq$zza;
    //   122: ldc -70
    //   124: aload_1
    //   125: invokevirtual 116	com/google/android/gms/measurement/internal/zzq$zza:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   128: aconst_null
    //   129: areturn
    //   130: aload_1
    //   131: areturn
    //   132: astore_1
    //   133: goto -21 -> 112
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	136	0	this	zzy
    //   0	136	1	paramAppMetadata	AppMetadata
    //   0	136	2	paramBoolean	boolean
    //   35	32	3	localObject	Object
    //   75	23	4	localzzak	zzak
    // Exception table:
    //   from	to	target	type
    //   26	57	111	java/lang/InterruptedException
    //   57	77	111	java/lang/InterruptedException
    //   81	92	111	java/lang/InterruptedException
    //   92	108	111	java/lang/InterruptedException
    //   26	57	132	java/util/concurrent/ExecutionException
    //   57	77	132	java/util/concurrent/ExecutionException
    //   81	92	132	java/util/concurrent/ExecutionException
    //   92	108	132	java/util/concurrent/ExecutionException
  }
  
  @BinderThread
  public void zza(final long paramLong, final String paramString1, final String paramString2, final String paramString3)
  {
    this.aqw.zzbwa().zzm(new Runnable()
    {
      public void run()
      {
        if (paramString2 == null)
        {
          zzy.zza(zzy.this).zzbvu().zza(paramString3, null);
          return;
        }
        AppMeasurement.zzf localzzf = new AppMeasurement.zzf();
        localzzf.aqz = paramString1;
        localzzf.aqA = paramString2;
        localzzf.aqB = paramLong;
        zzy.zza(zzy.this).zzbvu().zza(paramString3, localzzf);
      }
    });
  }
  
  @BinderThread
  public void zza(final AppMetadata paramAppMetadata)
  {
    zzb(paramAppMetadata, false);
    this.aqw.zzbwa().zzm(new Runnable()
    {
      public void run()
      {
        zzy.zza(zzy.this).zzbyj();
        zzy.this.zzmr(paramAppMetadata.ard);
        zzy.zza(zzy.this).zzd(paramAppMetadata);
      }
    });
  }
  
  @BinderThread
  public void zza(final EventParcel paramEventParcel, final AppMetadata paramAppMetadata)
  {
    zzaa.zzy(paramEventParcel);
    zzb(paramAppMetadata, false);
    this.aqw.zzbwa().zzm(new Runnable()
    {
      public void run()
      {
        zzy.zza(zzy.this).zzbyj();
        zzy.this.zzmr(paramAppMetadata.ard);
        zzy.zza(zzy.this).zzb(paramEventParcel, paramAppMetadata);
      }
    });
  }
  
  @BinderThread
  public void zza(final EventParcel paramEventParcel, final String paramString1, final String paramString2)
  {
    zzaa.zzy(paramEventParcel);
    zzaa.zzib(paramString1);
    zzn(paramString1, true);
    this.aqw.zzbwa().zzm(new Runnable()
    {
      public void run()
      {
        zzy.zza(zzy.this).zzbyj();
        zzy.this.zzmr(paramString2);
        zzy.zza(zzy.this).zzb(paramEventParcel, paramString1);
      }
    });
  }
  
  @BinderThread
  public void zza(final UserAttributeParcel paramUserAttributeParcel, final AppMetadata paramAppMetadata)
  {
    zzaa.zzy(paramUserAttributeParcel);
    zzb(paramAppMetadata, false);
    if (paramUserAttributeParcel.getValue() == null)
    {
      this.aqw.zzbwa().zzm(new Runnable()
      {
        public void run()
        {
          zzy.zza(zzy.this).zzbyj();
          zzy.this.zzmr(paramAppMetadata.ard);
          zzy.zza(zzy.this).zzc(paramUserAttributeParcel, paramAppMetadata);
        }
      });
      return;
    }
    this.aqw.zzbwa().zzm(new Runnable()
    {
      public void run()
      {
        zzy.zza(zzy.this).zzbyj();
        zzy.this.zzmr(paramAppMetadata.ard);
        zzy.zza(zzy.this).zzb(paramUserAttributeParcel, paramAppMetadata);
      }
    });
  }
  
  @BinderThread
  public byte[] zza(final EventParcel paramEventParcel, final String paramString)
  {
    zzaa.zzib(paramString);
    zzaa.zzy(paramEventParcel);
    zzn(paramString, true);
    this.aqw.zzbwb().zzbxd().zzj("Log and bundle. event", paramEventParcel.name);
    long l1 = this.aqw.zzabz().nanoTime() / 1000000L;
    paramString = this.aqw.zzbwa().zze(new Callable()
    {
      public byte[] zzbyl()
        throws Exception
      {
        zzy.zza(zzy.this).zzbyj();
        return zzy.zza(zzy.this).zza(paramEventParcel, paramString);
      }
    });
    try
    {
      byte[] arrayOfByte = (byte[])paramString.get();
      paramString = arrayOfByte;
      if (arrayOfByte == null)
      {
        this.aqw.zzbwb().zzbwy().log("Log and bundle returned null");
        paramString = new byte[0];
      }
      long l2 = this.aqw.zzabz().nanoTime() / 1000000L;
      this.aqw.zzbwb().zzbxd().zzd("Log and bundle processed. event, size, time_ms", paramEventParcel.name, Integer.valueOf(paramString.length), Long.valueOf(l2 - l1));
      return paramString;
    }
    catch (InterruptedException paramString)
    {
      this.aqw.zzbwb().zzbwy().zze("Failed to log and bundle. event, error", paramEventParcel.name, paramString);
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
    zzb(paramAppMetadata, false);
    this.aqw.zzbwa().zzm(new Runnable()
    {
      public void run()
      {
        zzy.zza(zzy.this).zzbyj();
        zzy.this.zzmr(paramAppMetadata.ard);
        zzy.zza(zzy.this).zzc(paramAppMetadata);
      }
    });
  }
  
  @WorkerThread
  void zzmr(String paramString)
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
        this.aqw.zzbwc().asY.zzg(paramString[1], l);
        return;
      }
    }
    catch (NumberFormatException localNumberFormatException)
    {
      this.aqw.zzbwb().zzbxa().zzj("Combining sample with a non-number weight", paramString[0]);
      return;
    }
    this.aqw.zzbwb().zzbxa().zzj("Combining sample with a non-positive weight", Long.valueOf(l));
  }
  
  protected void zzo(String paramString, boolean paramBoolean)
    throws SecurityException
  {
    if (paramBoolean) {
      if (this.auE == null)
      {
        if (("com.google.android.gms".equals(this.auF)) || (com.google.android.gms.common.util.zzx.zzf(this.aqw.getContext(), Binder.getCallingUid())) || (zzf.zzbv(this.aqw.getContext()).zza(this.aqw.getContext().getPackageManager(), Binder.getCallingUid())))
        {
          zzx localzzx = this.aqw;
          paramBoolean = true;
          this.auE = Boolean.valueOf(paramBoolean);
        }
      }
      else {
        if (!this.auE.booleanValue()) {
          break label100;
        }
      }
    }
    label100:
    do
    {
      return;
      paramBoolean = false;
      break;
      if ((this.auF == null) && (com.google.android.gms.common.zze.zzc(this.aqw.getContext(), Binder.getCallingUid(), paramString))) {
        this.auF = paramString;
      }
    } while (paramString.equals(this.auF));
    throw new SecurityException(String.format("Unknown calling package name '%s'.", new Object[] { paramString }));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/measurement/internal/zzy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */