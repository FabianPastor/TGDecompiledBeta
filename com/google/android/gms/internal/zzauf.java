package com.google.android.gms.internal;

import android.content.Context;
import android.os.Binder;
import android.support.annotation.BinderThread;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.util.zze;
import com.google.android.gms.common.util.zzy;
import com.google.android.gms.common.zzg;
import com.google.android.gms.common.zzh;
import com.google.android.gms.measurement.AppMeasurement.zzf;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class zzauf
  extends zzatt.zza
{
  private final zzaue zzbqb;
  private Boolean zzbuM;
  @Nullable
  private String zzbuN;
  
  public zzauf(zzaue paramzzaue)
  {
    this(paramzzaue, null);
  }
  
  public zzauf(zzaue paramzzaue, @Nullable String paramString)
  {
    zzac.zzw(paramzzaue);
    this.zzbqb = paramzzaue;
    this.zzbuN = paramString;
  }
  
  @BinderThread
  private void zzb(zzatd paramzzatd, boolean paramBoolean)
  {
    zzac.zzw(paramzzatd);
    zzm(paramzzatd.packageName, paramBoolean);
    this.zzbqb.zzKh().zzga(paramzzatd.zzbqK);
  }
  
  @BinderThread
  private void zzm(String paramString, boolean paramBoolean)
  {
    if (TextUtils.isEmpty(paramString))
    {
      this.zzbqb.zzKl().zzLZ().log("Measurement Service called without app package");
      throw new SecurityException("Measurement Service called without app package");
    }
    try
    {
      zzn(paramString, paramBoolean);
      return;
    }
    catch (SecurityException localSecurityException)
    {
      this.zzbqb.zzKl().zzLZ().zzj("Measurement Service called with invalid calling package. appId", zzatx.zzfE(paramString));
      throw localSecurityException;
    }
  }
  
  /* Error */
  @BinderThread
  public List<zzauq> zza(final zzatd paramzzatd, boolean paramBoolean)
  {
    // Byte code:
    //   0: aload_0
    //   1: aload_1
    //   2: iconst_0
    //   3: invokespecial 143	com/google/android/gms/internal/zzauf:zzb	(Lcom/google/android/gms/internal/zzatd;Z)V
    //   6: aload_0
    //   7: getfield 62	com/google/android/gms/internal/zzauf:zzbqb	Lcom/google/android/gms/internal/zzaue;
    //   10: invokevirtual 147	com/google/android/gms/internal/zzaue:zzKk	()Lcom/google/android/gms/internal/zzaud;
    //   13: new 18	com/google/android/gms/internal/zzauf$15
    //   16: dup
    //   17: aload_0
    //   18: aload_1
    //   19: invokespecial 150	com/google/android/gms/internal/zzauf$15:<init>	(Lcom/google/android/gms/internal/zzauf;Lcom/google/android/gms/internal/zzatd;)V
    //   22: invokevirtual 156	com/google/android/gms/internal/zzaud:zzd	(Ljava/util/concurrent/Callable;)Ljava/util/concurrent/Future;
    //   25: astore_3
    //   26: aload_3
    //   27: invokeinterface 162 1 0
    //   32: checkcast 164	java/util/List
    //   35: astore 4
    //   37: new 166	java/util/ArrayList
    //   40: dup
    //   41: aload 4
    //   43: invokeinterface 170 1 0
    //   48: invokespecial 173	java/util/ArrayList:<init>	(I)V
    //   51: astore_3
    //   52: aload 4
    //   54: invokeinterface 177 1 0
    //   59: astore 4
    //   61: aload 4
    //   63: invokeinterface 183 1 0
    //   68: ifeq +75 -> 143
    //   71: aload 4
    //   73: invokeinterface 186 1 0
    //   78: checkcast 188	com/google/android/gms/internal/zzaus
    //   81: astore 5
    //   83: iload_2
    //   84: ifne +14 -> 98
    //   87: aload 5
    //   89: getfield 191	com/google/android/gms/internal/zzaus:mName	Ljava/lang/String;
    //   92: invokestatic 194	com/google/android/gms/internal/zzaut:zzgd	(Ljava/lang/String;)Z
    //   95: ifne -34 -> 61
    //   98: aload_3
    //   99: new 196	com/google/android/gms/internal/zzauq
    //   102: dup
    //   103: aload 5
    //   105: invokespecial 199	com/google/android/gms/internal/zzauq:<init>	(Lcom/google/android/gms/internal/zzaus;)V
    //   108: invokeinterface 203 2 0
    //   113: pop
    //   114: goto -53 -> 61
    //   117: astore_3
    //   118: aload_0
    //   119: getfield 62	com/google/android/gms/internal/zzauf:zzbqb	Lcom/google/android/gms/internal/zzaue;
    //   122: invokevirtual 107	com/google/android/gms/internal/zzaue:zzKl	()Lcom/google/android/gms/internal/zzatx;
    //   125: invokevirtual 113	com/google/android/gms/internal/zzatx:zzLZ	()Lcom/google/android/gms/internal/zzatx$zza;
    //   128: ldc -51
    //   130: aload_1
    //   131: getfield 75	com/google/android/gms/internal/zzatd:packageName	Ljava/lang/String;
    //   134: invokestatic 132	com/google/android/gms/internal/zzatx:zzfE	(Ljava/lang/String;)Ljava/lang/Object;
    //   137: aload_3
    //   138: invokevirtual 209	com/google/android/gms/internal/zzatx$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   141: aconst_null
    //   142: areturn
    //   143: aload_3
    //   144: areturn
    //   145: astore_3
    //   146: goto -28 -> 118
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	149	0	this	zzauf
    //   0	149	1	paramzzatd	zzatd
    //   0	149	2	paramBoolean	boolean
    //   25	74	3	localObject1	Object
    //   117	27	3	localInterruptedException	InterruptedException
    //   145	1	3	localExecutionException	ExecutionException
    //   35	37	4	localObject2	Object
    //   81	23	5	localzzaus	zzaus
    // Exception table:
    //   from	to	target	type
    //   26	61	117	java/lang/InterruptedException
    //   61	83	117	java/lang/InterruptedException
    //   87	98	117	java/lang/InterruptedException
    //   98	114	117	java/lang/InterruptedException
    //   26	61	145	java/util/concurrent/ExecutionException
    //   61	83	145	java/util/concurrent/ExecutionException
    //   87	98	145	java/util/concurrent/ExecutionException
    //   98	114	145	java/util/concurrent/ExecutionException
  }
  
  @BinderThread
  public List<zzatg> zza(final String paramString1, final String paramString2, final zzatd paramzzatd)
  {
    zzb(paramzzatd, false);
    paramString1 = this.zzbqb.zzKk().zzd(new Callable()
    {
      public List<zzatg> zzMP()
        throws Exception
      {
        zzauf.zza(zzauf.this).zzMN();
        return zzauf.zza(zzauf.this).zzKg().zzl(paramzzatd.packageName, paramString1, paramString2);
      }
    });
    try
    {
      paramString1 = (List)paramString1.get();
      return paramString1;
    }
    catch (InterruptedException paramString1)
    {
      this.zzbqb.zzKl().zzLZ().zzj("Failed to get conditional user properties", paramString1);
      return Collections.emptyList();
    }
    catch (ExecutionException paramString1)
    {
      for (;;) {}
    }
  }
  
  /* Error */
  @BinderThread
  public List<zzauq> zza(final String paramString1, final String paramString2, final String paramString3, boolean paramBoolean)
  {
    // Byte code:
    //   0: aload_0
    //   1: aload_1
    //   2: iconst_1
    //   3: invokespecial 79	com/google/android/gms/internal/zzauf:zzm	(Ljava/lang/String;Z)V
    //   6: aload_0
    //   7: getfield 62	com/google/android/gms/internal/zzauf:zzbqb	Lcom/google/android/gms/internal/zzaue;
    //   10: invokevirtual 147	com/google/android/gms/internal/zzaue:zzKk	()Lcom/google/android/gms/internal/zzaud;
    //   13: new 34	com/google/android/gms/internal/zzauf$7
    //   16: dup
    //   17: aload_0
    //   18: aload_1
    //   19: aload_2
    //   20: aload_3
    //   21: invokespecial 228	com/google/android/gms/internal/zzauf$7:<init>	(Lcom/google/android/gms/internal/zzauf;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
    //   24: invokevirtual 156	com/google/android/gms/internal/zzaud:zzd	(Ljava/util/concurrent/Callable;)Ljava/util/concurrent/Future;
    //   27: astore_2
    //   28: aload_2
    //   29: invokeinterface 162 1 0
    //   34: checkcast 164	java/util/List
    //   37: astore_3
    //   38: new 166	java/util/ArrayList
    //   41: dup
    //   42: aload_3
    //   43: invokeinterface 170 1 0
    //   48: invokespecial 173	java/util/ArrayList:<init>	(I)V
    //   51: astore_2
    //   52: aload_3
    //   53: invokeinterface 177 1 0
    //   58: astore_3
    //   59: aload_3
    //   60: invokeinterface 183 1 0
    //   65: ifeq +74 -> 139
    //   68: aload_3
    //   69: invokeinterface 186 1 0
    //   74: checkcast 188	com/google/android/gms/internal/zzaus
    //   77: astore 5
    //   79: iload 4
    //   81: ifne +14 -> 95
    //   84: aload 5
    //   86: getfield 191	com/google/android/gms/internal/zzaus:mName	Ljava/lang/String;
    //   89: invokestatic 194	com/google/android/gms/internal/zzaut:zzgd	(Ljava/lang/String;)Z
    //   92: ifne -33 -> 59
    //   95: aload_2
    //   96: new 196	com/google/android/gms/internal/zzauq
    //   99: dup
    //   100: aload 5
    //   102: invokespecial 199	com/google/android/gms/internal/zzauq:<init>	(Lcom/google/android/gms/internal/zzaus;)V
    //   105: invokeinterface 203 2 0
    //   110: pop
    //   111: goto -52 -> 59
    //   114: astore_2
    //   115: aload_0
    //   116: getfield 62	com/google/android/gms/internal/zzauf:zzbqb	Lcom/google/android/gms/internal/zzaue;
    //   119: invokevirtual 107	com/google/android/gms/internal/zzaue:zzKl	()Lcom/google/android/gms/internal/zzatx;
    //   122: invokevirtual 113	com/google/android/gms/internal/zzatx:zzLZ	()Lcom/google/android/gms/internal/zzatx$zza;
    //   125: ldc -51
    //   127: aload_1
    //   128: invokestatic 132	com/google/android/gms/internal/zzatx:zzfE	(Ljava/lang/String;)Ljava/lang/Object;
    //   131: aload_2
    //   132: invokevirtual 209	com/google/android/gms/internal/zzatx$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   135: invokestatic 223	java/util/Collections:emptyList	()Ljava/util/List;
    //   138: areturn
    //   139: aload_2
    //   140: areturn
    //   141: astore_2
    //   142: goto -27 -> 115
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	145	0	this	zzauf
    //   0	145	1	paramString1	String
    //   0	145	2	paramString2	String
    //   0	145	3	paramString3	String
    //   0	145	4	paramBoolean	boolean
    //   77	24	5	localzzaus	zzaus
    // Exception table:
    //   from	to	target	type
    //   28	59	114	java/lang/InterruptedException
    //   59	79	114	java/lang/InterruptedException
    //   84	95	114	java/lang/InterruptedException
    //   95	111	114	java/lang/InterruptedException
    //   28	59	141	java/util/concurrent/ExecutionException
    //   59	79	141	java/util/concurrent/ExecutionException
    //   84	95	141	java/util/concurrent/ExecutionException
    //   95	111	141	java/util/concurrent/ExecutionException
  }
  
  /* Error */
  @BinderThread
  public List<zzauq> zza(final String paramString1, final String paramString2, boolean paramBoolean, final zzatd paramzzatd)
  {
    // Byte code:
    //   0: aload_0
    //   1: aload 4
    //   3: iconst_0
    //   4: invokespecial 143	com/google/android/gms/internal/zzauf:zzb	(Lcom/google/android/gms/internal/zzatd;Z)V
    //   7: aload_0
    //   8: getfield 62	com/google/android/gms/internal/zzauf:zzbqb	Lcom/google/android/gms/internal/zzaue;
    //   11: invokevirtual 147	com/google/android/gms/internal/zzaue:zzKk	()Lcom/google/android/gms/internal/zzaud;
    //   14: new 32	com/google/android/gms/internal/zzauf$6
    //   17: dup
    //   18: aload_0
    //   19: aload 4
    //   21: aload_1
    //   22: aload_2
    //   23: invokespecial 231	com/google/android/gms/internal/zzauf$6:<init>	(Lcom/google/android/gms/internal/zzauf;Lcom/google/android/gms/internal/zzatd;Ljava/lang/String;Ljava/lang/String;)V
    //   26: invokevirtual 156	com/google/android/gms/internal/zzaud:zzd	(Ljava/util/concurrent/Callable;)Ljava/util/concurrent/Future;
    //   29: astore_1
    //   30: aload_1
    //   31: invokeinterface 162 1 0
    //   36: checkcast 164	java/util/List
    //   39: astore_2
    //   40: new 166	java/util/ArrayList
    //   43: dup
    //   44: aload_2
    //   45: invokeinterface 170 1 0
    //   50: invokespecial 173	java/util/ArrayList:<init>	(I)V
    //   53: astore_1
    //   54: aload_2
    //   55: invokeinterface 177 1 0
    //   60: astore_2
    //   61: aload_2
    //   62: invokeinterface 183 1 0
    //   67: ifeq +77 -> 144
    //   70: aload_2
    //   71: invokeinterface 186 1 0
    //   76: checkcast 188	com/google/android/gms/internal/zzaus
    //   79: astore 5
    //   81: iload_3
    //   82: ifne +14 -> 96
    //   85: aload 5
    //   87: getfield 191	com/google/android/gms/internal/zzaus:mName	Ljava/lang/String;
    //   90: invokestatic 194	com/google/android/gms/internal/zzaut:zzgd	(Ljava/lang/String;)Z
    //   93: ifne -32 -> 61
    //   96: aload_1
    //   97: new 196	com/google/android/gms/internal/zzauq
    //   100: dup
    //   101: aload 5
    //   103: invokespecial 199	com/google/android/gms/internal/zzauq:<init>	(Lcom/google/android/gms/internal/zzaus;)V
    //   106: invokeinterface 203 2 0
    //   111: pop
    //   112: goto -51 -> 61
    //   115: astore_1
    //   116: aload_0
    //   117: getfield 62	com/google/android/gms/internal/zzauf:zzbqb	Lcom/google/android/gms/internal/zzaue;
    //   120: invokevirtual 107	com/google/android/gms/internal/zzaue:zzKl	()Lcom/google/android/gms/internal/zzatx;
    //   123: invokevirtual 113	com/google/android/gms/internal/zzatx:zzLZ	()Lcom/google/android/gms/internal/zzatx$zza;
    //   126: ldc -51
    //   128: aload 4
    //   130: getfield 75	com/google/android/gms/internal/zzatd:packageName	Ljava/lang/String;
    //   133: invokestatic 132	com/google/android/gms/internal/zzatx:zzfE	(Ljava/lang/String;)Ljava/lang/Object;
    //   136: aload_1
    //   137: invokevirtual 209	com/google/android/gms/internal/zzatx$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   140: invokestatic 223	java/util/Collections:emptyList	()Ljava/util/List;
    //   143: areturn
    //   144: aload_1
    //   145: areturn
    //   146: astore_1
    //   147: goto -31 -> 116
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	150	0	this	zzauf
    //   0	150	1	paramString1	String
    //   0	150	2	paramString2	String
    //   0	150	3	paramBoolean	boolean
    //   0	150	4	paramzzatd	zzatd
    //   79	23	5	localzzaus	zzaus
    // Exception table:
    //   from	to	target	type
    //   30	61	115	java/lang/InterruptedException
    //   61	81	115	java/lang/InterruptedException
    //   85	96	115	java/lang/InterruptedException
    //   96	112	115	java/lang/InterruptedException
    //   30	61	146	java/util/concurrent/ExecutionException
    //   61	81	146	java/util/concurrent/ExecutionException
    //   85	96	146	java/util/concurrent/ExecutionException
    //   96	112	146	java/util/concurrent/ExecutionException
  }
  
  @BinderThread
  public void zza(final long paramLong, final String paramString1, final String paramString2, final String paramString3)
  {
    this.zzbqb.zzKk().zzm(new Runnable()
    {
      public void run()
      {
        if (paramString2 == null)
        {
          zzauf.zza(zzauf.this).zzKe().zza(paramString3, null);
          return;
        }
        AppMeasurement.zzf localzzf = new AppMeasurement.zzf();
        localzzf.zzbqe = paramString1;
        localzzf.zzbqf = paramString2;
        localzzf.zzbqg = paramLong;
        zzauf.zza(zzauf.this).zzKe().zza(paramString3, localzzf);
      }
    });
  }
  
  @BinderThread
  public void zza(final zzatd paramzzatd)
  {
    zzb(paramzzatd, false);
    this.zzbqb.zzKk().zzm(new Runnable()
    {
      public void run()
      {
        zzauf.zza(zzauf.this).zzMN();
        zzauf.zza(zzauf.this).zze(paramzzatd);
      }
    });
  }
  
  @BinderThread
  public void zza(zzatg paramzzatg, final zzatd paramzzatd)
  {
    zzac.zzw(paramzzatg);
    zzac.zzw(paramzzatg.zzbqX);
    zzb(paramzzatd, false);
    final zzatg localzzatg = new zzatg(paramzzatg);
    localzzatg.packageName = paramzzatd.packageName;
    if (paramzzatg.zzbqX.getValue() == null)
    {
      this.zzbqb.zzKk().zzm(new Runnable()
      {
        public void run()
        {
          zzauf.zza(zzauf.this).zzMN();
          zzauf.zza(zzauf.this).zzc(localzzatg, paramzzatd);
        }
      });
      return;
    }
    this.zzbqb.zzKk().zzm(new Runnable()
    {
      public void run()
      {
        zzauf.zza(zzauf.this).zzMN();
        zzauf.zza(zzauf.this).zzb(localzzatg, paramzzatd);
      }
    });
  }
  
  @BinderThread
  public void zza(final zzatq paramzzatq, final zzatd paramzzatd)
  {
    zzac.zzw(paramzzatq);
    zzb(paramzzatd, false);
    this.zzbqb.zzKk().zzm(new Runnable()
    {
      public void run()
      {
        zzauf.zza(zzauf.this).zzMN();
        zzauf.zza(zzauf.this).zzb(paramzzatq, paramzzatd);
      }
    });
  }
  
  @BinderThread
  public void zza(final zzatq paramzzatq, final String paramString1, String paramString2)
  {
    zzac.zzw(paramzzatq);
    zzac.zzdr(paramString1);
    zzm(paramString1, true);
    this.zzbqb.zzKk().zzm(new Runnable()
    {
      public void run()
      {
        zzauf.zza(zzauf.this).zzMN();
        zzauf.zza(zzauf.this).zzb(paramzzatq, paramString1);
      }
    });
  }
  
  @BinderThread
  public void zza(final zzauq paramzzauq, final zzatd paramzzatd)
  {
    zzac.zzw(paramzzauq);
    zzb(paramzzatd, false);
    if (paramzzauq.getValue() == null)
    {
      this.zzbqb.zzKk().zzm(new Runnable()
      {
        public void run()
        {
          zzauf.zza(zzauf.this).zzMN();
          zzauf.zza(zzauf.this).zzc(paramzzauq, paramzzatd);
        }
      });
      return;
    }
    this.zzbqb.zzKk().zzm(new Runnable()
    {
      public void run()
      {
        zzauf.zza(zzauf.this).zzMN();
        zzauf.zza(zzauf.this).zzb(paramzzauq, paramzzatd);
      }
    });
  }
  
  @BinderThread
  public byte[] zza(final zzatq paramzzatq, final String paramString)
  {
    zzac.zzdr(paramString);
    zzac.zzw(paramzzatq);
    zzm(paramString, true);
    this.zzbqb.zzKl().zzMe().zzj("Log and bundle. event", paramzzatq.name);
    long l1 = this.zzbqb.zznR().nanoTime() / 1000000L;
    Object localObject = this.zzbqb.zzKk().zze(new Callable()
    {
      public byte[] zzMQ()
        throws Exception
      {
        zzauf.zza(zzauf.this).zzMN();
        return zzauf.zza(zzauf.this).zza(paramzzatq, paramString);
      }
    });
    try
    {
      byte[] arrayOfByte = (byte[])((Future)localObject).get();
      localObject = arrayOfByte;
      if (arrayOfByte == null)
      {
        this.zzbqb.zzKl().zzLZ().zzj("Log and bundle returned null. appId", zzatx.zzfE(paramString));
        localObject = new byte[0];
      }
      long l2 = this.zzbqb.zznR().nanoTime() / 1000000L;
      this.zzbqb.zzKl().zzMe().zzd("Log and bundle processed. event, size, time_ms", paramzzatq.name, Integer.valueOf(localObject.length), Long.valueOf(l2 - l1));
      return (byte[])localObject;
    }
    catch (InterruptedException localInterruptedException)
    {
      this.zzbqb.zzKl().zzLZ().zzd("Failed to log and bundle. appId, event, error", zzatx.zzfE(paramString), paramzzatq.name, localInterruptedException);
      return null;
    }
    catch (ExecutionException localExecutionException)
    {
      for (;;) {}
    }
  }
  
  @BinderThread
  public void zzb(final zzatd paramzzatd)
  {
    zzb(paramzzatd, false);
    this.zzbqb.zzKk().zzm(new Runnable()
    {
      public void run()
      {
        zzauf.zza(zzauf.this).zzMN();
        zzauf.zza(zzauf.this).zzd(paramzzatd);
      }
    });
  }
  
  @BinderThread
  public void zzb(zzatg paramzzatg)
  {
    zzac.zzw(paramzzatg);
    zzac.zzw(paramzzatg.zzbqX);
    zzm(paramzzatg.packageName, true);
    final zzatg localzzatg = new zzatg(paramzzatg);
    if (paramzzatg.zzbqX.getValue() == null)
    {
      this.zzbqb.zzKk().zzm(new Runnable()
      {
        public void run()
        {
          zzauf.zza(zzauf.this).zzMN();
          zzauf.zza(zzauf.this).zze(localzzatg);
        }
      });
      return;
    }
    this.zzbqb.zzKk().zzm(new Runnable()
    {
      public void run()
      {
        zzauf.zza(zzauf.this).zzMN();
        zzauf.zza(zzauf.this).zzd(localzzatg);
      }
    });
  }
  
  @BinderThread
  public String zzc(zzatd paramzzatd)
  {
    zzb(paramzzatd, false);
    return this.zzbqb.zzfP(paramzzatd.packageName);
  }
  
  @BinderThread
  public List<zzatg> zzn(final String paramString1, final String paramString2, final String paramString3)
  {
    zzm(paramString1, true);
    paramString1 = this.zzbqb.zzKk().zzd(new Callable()
    {
      public List<zzatg> zzMP()
        throws Exception
      {
        zzauf.zza(zzauf.this).zzMN();
        return zzauf.zza(zzauf.this).zzKg().zzl(paramString1, paramString2, paramString3);
      }
    });
    try
    {
      paramString1 = (List)paramString1.get();
      return paramString1;
    }
    catch (InterruptedException paramString1)
    {
      this.zzbqb.zzKl().zzLZ().zzj("Failed to get conditional user properties", paramString1);
      return Collections.emptyList();
    }
    catch (ExecutionException paramString1)
    {
      for (;;) {}
    }
  }
  
  protected void zzn(String paramString, boolean paramBoolean)
    throws SecurityException
  {
    if (paramBoolean) {
      if (this.zzbuM == null)
      {
        if (("com.google.android.gms".equals(this.zzbuN)) || (zzy.zzf(this.zzbqb.getContext(), Binder.getCallingUid())) || (zzh.zzaN(this.zzbqb.getContext()).zza(this.zzbqb.getContext().getPackageManager(), Binder.getCallingUid())))
        {
          paramBoolean = true;
          this.zzbuM = Boolean.valueOf(paramBoolean);
        }
      }
      else {
        if (!this.zzbuM.booleanValue()) {
          break label95;
        }
      }
    }
    label95:
    do
    {
      return;
      paramBoolean = false;
      break;
      if ((this.zzbuN == null) && (zzg.zzc(this.zzbqb.getContext(), Binder.getCallingUid(), paramString))) {
        this.zzbuN = paramString;
      }
    } while (paramString.equals(this.zzbuN));
    throw new SecurityException(String.format("Unknown calling package name '%s'.", new Object[] { paramString }));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzauf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */