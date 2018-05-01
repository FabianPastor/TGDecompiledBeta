package com.google.android.gms.internal;

import android.os.Binder;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzbq;
import com.google.android.gms.common.util.zzd;
import com.google.android.gms.common.util.zzx;
import com.google.android.gms.common.zzp;
import com.google.android.gms.common.zzq;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public final class zzcir
  extends zzchf
{
  private final zzcim zziwf;
  private Boolean zzjgl;
  private String zzjgm;
  
  public zzcir(zzcim paramzzcim)
  {
    this(paramzzcim, null);
  }
  
  private zzcir(zzcim paramzzcim, String paramString)
  {
    zzbq.checkNotNull(paramzzcim);
    this.zziwf = paramzzcim;
    this.zzjgm = null;
  }
  
  private final void zzb(zzcgi paramzzcgi, boolean paramBoolean)
  {
    zzbq.checkNotNull(paramzzcgi);
    zzf(paramzzcgi.packageName, false);
    this.zziwf.zzawu().zzkg(paramzzcgi.zzixs);
  }
  
  private final void zzf(String paramString, boolean paramBoolean)
  {
    boolean bool = false;
    if (TextUtils.isEmpty(paramString))
    {
      this.zziwf.zzawy().zzazd().log("Measurement Service called without app package");
      throw new SecurityException("Measurement Service called without app package");
    }
    if (paramBoolean) {}
    for (;;)
    {
      try
      {
        if (this.zzjgl == null)
        {
          if (("com.google.android.gms".equals(this.zzjgm)) || (zzx.zzf(this.zziwf.getContext(), Binder.getCallingUid()))) {
            break label201;
          }
          paramBoolean = bool;
          if (zzq.zzci(this.zziwf.getContext()).zzbq(Binder.getCallingUid())) {
            break label201;
          }
          this.zzjgl = Boolean.valueOf(paramBoolean);
        }
        if (!this.zzjgl.booleanValue())
        {
          if ((this.zzjgm == null) && (zzp.zzb(this.zziwf.getContext(), Binder.getCallingUid(), paramString))) {
            this.zzjgm = paramString;
          }
          if (!paramString.equals(this.zzjgm)) {
            throw new SecurityException(String.format("Unknown calling package name '%s'.", new Object[] { paramString }));
          }
        }
      }
      catch (SecurityException localSecurityException)
      {
        this.zziwf.zzawy().zzazd().zzj("Measurement Service called with invalid calling package. appId", zzchm.zzjk(paramString));
        throw localSecurityException;
      }
      return;
      label201:
      paramBoolean = true;
    }
  }
  
  /* Error */
  public final List<zzcln> zza(zzcgi paramzzcgi, boolean paramBoolean)
  {
    // Byte code:
    //   0: aload_0
    //   1: aload_1
    //   2: iconst_0
    //   3: invokespecial 160	com/google/android/gms/internal/zzcir:zzb	(Lcom/google/android/gms/internal/zzcgi;Z)V
    //   6: aload_0
    //   7: getfield 27	com/google/android/gms/internal/zzcir:zziwf	Lcom/google/android/gms/internal/zzcim;
    //   10: invokevirtual 164	com/google/android/gms/internal/zzcim:zzawx	()Lcom/google/android/gms/internal/zzcih;
    //   13: new 166	com/google/android/gms/internal/zzcjh
    //   16: dup
    //   17: aload_0
    //   18: aload_1
    //   19: invokespecial 169	com/google/android/gms/internal/zzcjh:<init>	(Lcom/google/android/gms/internal/zzcir;Lcom/google/android/gms/internal/zzcgi;)V
    //   22: invokevirtual 175	com/google/android/gms/internal/zzcih:zzc	(Ljava/util/concurrent/Callable;)Ljava/util/concurrent/Future;
    //   25: astore_3
    //   26: aload_3
    //   27: invokeinterface 181 1 0
    //   32: checkcast 183	java/util/List
    //   35: astore 4
    //   37: new 185	java/util/ArrayList
    //   40: dup
    //   41: aload 4
    //   43: invokeinterface 188 1 0
    //   48: invokespecial 191	java/util/ArrayList:<init>	(I)V
    //   51: astore_3
    //   52: aload 4
    //   54: invokeinterface 195 1 0
    //   59: astore 4
    //   61: aload 4
    //   63: invokeinterface 200 1 0
    //   68: ifeq +75 -> 143
    //   71: aload 4
    //   73: invokeinterface 203 1 0
    //   78: checkcast 205	com/google/android/gms/internal/zzclp
    //   81: astore 5
    //   83: iload_2
    //   84: ifne +14 -> 98
    //   87: aload 5
    //   89: getfield 208	com/google/android/gms/internal/zzclp:mName	Ljava/lang/String;
    //   92: invokestatic 211	com/google/android/gms/internal/zzclq:zzki	(Ljava/lang/String;)Z
    //   95: ifne -34 -> 61
    //   98: aload_3
    //   99: new 213	com/google/android/gms/internal/zzcln
    //   102: dup
    //   103: aload 5
    //   105: invokespecial 216	com/google/android/gms/internal/zzcln:<init>	(Lcom/google/android/gms/internal/zzclp;)V
    //   108: invokeinterface 219 2 0
    //   113: pop
    //   114: goto -53 -> 61
    //   117: astore_3
    //   118: aload_0
    //   119: getfield 27	com/google/android/gms/internal/zzcir:zziwf	Lcom/google/android/gms/internal/zzcim;
    //   122: invokevirtual 69	com/google/android/gms/internal/zzcim:zzawy	()Lcom/google/android/gms/internal/zzchm;
    //   125: invokevirtual 75	com/google/android/gms/internal/zzchm:zzazd	()Lcom/google/android/gms/internal/zzcho;
    //   128: ldc -35
    //   130: aload_1
    //   131: getfield 38	com/google/android/gms/internal/zzcgi:packageName	Ljava/lang/String;
    //   134: invokestatic 149	com/google/android/gms/internal/zzchm:zzjk	(Ljava/lang/String;)Ljava/lang/Object;
    //   137: aload_3
    //   138: invokevirtual 225	com/google/android/gms/internal/zzcho:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   141: aconst_null
    //   142: areturn
    //   143: aload_3
    //   144: areturn
    //   145: astore_3
    //   146: goto -28 -> 118
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	149	0	this	zzcir
    //   0	149	1	paramzzcgi	zzcgi
    //   0	149	2	paramBoolean	boolean
    //   25	74	3	localObject1	Object
    //   117	27	3	localInterruptedException	InterruptedException
    //   145	1	3	localExecutionException	ExecutionException
    //   35	37	4	localObject2	Object
    //   81	23	5	localzzclp	zzclp
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
  
  public final List<zzcgl> zza(String paramString1, String paramString2, zzcgi paramzzcgi)
  {
    zzb(paramzzcgi, false);
    paramString1 = this.zziwf.zzawx().zzc(new zzciz(this, paramzzcgi, paramString1, paramString2));
    try
    {
      paramString1 = (List)paramString1.get();
      return paramString1;
    }
    catch (InterruptedException paramString1)
    {
      this.zziwf.zzawy().zzazd().zzj("Failed to get conditional user properties", paramString1);
      return Collections.emptyList();
    }
    catch (ExecutionException paramString1)
    {
      for (;;) {}
    }
  }
  
  /* Error */
  public final List<zzcln> zza(String paramString1, String paramString2, String paramString3, boolean paramBoolean)
  {
    // Byte code:
    //   0: aload_0
    //   1: aload_1
    //   2: iconst_1
    //   3: invokespecial 42	com/google/android/gms/internal/zzcir:zzf	(Ljava/lang/String;Z)V
    //   6: aload_0
    //   7: getfield 27	com/google/android/gms/internal/zzcir:zziwf	Lcom/google/android/gms/internal/zzcim;
    //   10: invokevirtual 164	com/google/android/gms/internal/zzcim:zzawx	()Lcom/google/android/gms/internal/zzcih;
    //   13: new 245	com/google/android/gms/internal/zzciy
    //   16: dup
    //   17: aload_0
    //   18: aload_1
    //   19: aload_2
    //   20: aload_3
    //   21: invokespecial 248	com/google/android/gms/internal/zzciy:<init>	(Lcom/google/android/gms/internal/zzcir;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
    //   24: invokevirtual 175	com/google/android/gms/internal/zzcih:zzc	(Ljava/util/concurrent/Callable;)Ljava/util/concurrent/Future;
    //   27: astore_2
    //   28: aload_2
    //   29: invokeinterface 181 1 0
    //   34: checkcast 183	java/util/List
    //   37: astore_3
    //   38: new 185	java/util/ArrayList
    //   41: dup
    //   42: aload_3
    //   43: invokeinterface 188 1 0
    //   48: invokespecial 191	java/util/ArrayList:<init>	(I)V
    //   51: astore_2
    //   52: aload_3
    //   53: invokeinterface 195 1 0
    //   58: astore_3
    //   59: aload_3
    //   60: invokeinterface 200 1 0
    //   65: ifeq +74 -> 139
    //   68: aload_3
    //   69: invokeinterface 203 1 0
    //   74: checkcast 205	com/google/android/gms/internal/zzclp
    //   77: astore 5
    //   79: iload 4
    //   81: ifne +14 -> 95
    //   84: aload 5
    //   86: getfield 208	com/google/android/gms/internal/zzclp:mName	Ljava/lang/String;
    //   89: invokestatic 211	com/google/android/gms/internal/zzclq:zzki	(Ljava/lang/String;)Z
    //   92: ifne -33 -> 59
    //   95: aload_2
    //   96: new 213	com/google/android/gms/internal/zzcln
    //   99: dup
    //   100: aload 5
    //   102: invokespecial 216	com/google/android/gms/internal/zzcln:<init>	(Lcom/google/android/gms/internal/zzclp;)V
    //   105: invokeinterface 219 2 0
    //   110: pop
    //   111: goto -52 -> 59
    //   114: astore_2
    //   115: aload_0
    //   116: getfield 27	com/google/android/gms/internal/zzcir:zziwf	Lcom/google/android/gms/internal/zzcim;
    //   119: invokevirtual 69	com/google/android/gms/internal/zzcim:zzawy	()Lcom/google/android/gms/internal/zzchm;
    //   122: invokevirtual 75	com/google/android/gms/internal/zzchm:zzazd	()Lcom/google/android/gms/internal/zzcho;
    //   125: ldc -35
    //   127: aload_1
    //   128: invokestatic 149	com/google/android/gms/internal/zzchm:zzjk	(Ljava/lang/String;)Ljava/lang/Object;
    //   131: aload_2
    //   132: invokevirtual 225	com/google/android/gms/internal/zzcho:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   135: invokestatic 241	java/util/Collections:emptyList	()Ljava/util/List;
    //   138: areturn
    //   139: aload_2
    //   140: areturn
    //   141: astore_2
    //   142: goto -27 -> 115
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	145	0	this	zzcir
    //   0	145	1	paramString1	String
    //   0	145	2	paramString2	String
    //   0	145	3	paramString3	String
    //   0	145	4	paramBoolean	boolean
    //   77	24	5	localzzclp	zzclp
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
  public final List<zzcln> zza(String paramString1, String paramString2, boolean paramBoolean, zzcgi paramzzcgi)
  {
    // Byte code:
    //   0: aload_0
    //   1: aload 4
    //   3: iconst_0
    //   4: invokespecial 160	com/google/android/gms/internal/zzcir:zzb	(Lcom/google/android/gms/internal/zzcgi;Z)V
    //   7: aload_0
    //   8: getfield 27	com/google/android/gms/internal/zzcir:zziwf	Lcom/google/android/gms/internal/zzcim;
    //   11: invokevirtual 164	com/google/android/gms/internal/zzcim:zzawx	()Lcom/google/android/gms/internal/zzcih;
    //   14: new 252	com/google/android/gms/internal/zzcix
    //   17: dup
    //   18: aload_0
    //   19: aload 4
    //   21: aload_1
    //   22: aload_2
    //   23: invokespecial 253	com/google/android/gms/internal/zzcix:<init>	(Lcom/google/android/gms/internal/zzcir;Lcom/google/android/gms/internal/zzcgi;Ljava/lang/String;Ljava/lang/String;)V
    //   26: invokevirtual 175	com/google/android/gms/internal/zzcih:zzc	(Ljava/util/concurrent/Callable;)Ljava/util/concurrent/Future;
    //   29: astore_1
    //   30: aload_1
    //   31: invokeinterface 181 1 0
    //   36: checkcast 183	java/util/List
    //   39: astore_2
    //   40: new 185	java/util/ArrayList
    //   43: dup
    //   44: aload_2
    //   45: invokeinterface 188 1 0
    //   50: invokespecial 191	java/util/ArrayList:<init>	(I)V
    //   53: astore_1
    //   54: aload_2
    //   55: invokeinterface 195 1 0
    //   60: astore_2
    //   61: aload_2
    //   62: invokeinterface 200 1 0
    //   67: ifeq +77 -> 144
    //   70: aload_2
    //   71: invokeinterface 203 1 0
    //   76: checkcast 205	com/google/android/gms/internal/zzclp
    //   79: astore 5
    //   81: iload_3
    //   82: ifne +14 -> 96
    //   85: aload 5
    //   87: getfield 208	com/google/android/gms/internal/zzclp:mName	Ljava/lang/String;
    //   90: invokestatic 211	com/google/android/gms/internal/zzclq:zzki	(Ljava/lang/String;)Z
    //   93: ifne -32 -> 61
    //   96: aload_1
    //   97: new 213	com/google/android/gms/internal/zzcln
    //   100: dup
    //   101: aload 5
    //   103: invokespecial 216	com/google/android/gms/internal/zzcln:<init>	(Lcom/google/android/gms/internal/zzclp;)V
    //   106: invokeinterface 219 2 0
    //   111: pop
    //   112: goto -51 -> 61
    //   115: astore_1
    //   116: aload_0
    //   117: getfield 27	com/google/android/gms/internal/zzcir:zziwf	Lcom/google/android/gms/internal/zzcim;
    //   120: invokevirtual 69	com/google/android/gms/internal/zzcim:zzawy	()Lcom/google/android/gms/internal/zzchm;
    //   123: invokevirtual 75	com/google/android/gms/internal/zzchm:zzazd	()Lcom/google/android/gms/internal/zzcho;
    //   126: ldc -35
    //   128: aload 4
    //   130: getfield 38	com/google/android/gms/internal/zzcgi:packageName	Ljava/lang/String;
    //   133: invokestatic 149	com/google/android/gms/internal/zzchm:zzjk	(Ljava/lang/String;)Ljava/lang/Object;
    //   136: aload_1
    //   137: invokevirtual 225	com/google/android/gms/internal/zzcho:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   140: invokestatic 241	java/util/Collections:emptyList	()Ljava/util/List;
    //   143: areturn
    //   144: aload_1
    //   145: areturn
    //   146: astore_1
    //   147: goto -31 -> 116
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	150	0	this	zzcir
    //   0	150	1	paramString1	String
    //   0	150	2	paramString2	String
    //   0	150	3	paramBoolean	boolean
    //   0	150	4	paramzzcgi	zzcgi
    //   79	23	5	localzzclp	zzclp
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
  
  public final void zza(long paramLong, String paramString1, String paramString2, String paramString3)
  {
    this.zziwf.zzawx().zzg(new zzcjj(this, paramString2, paramString3, paramString1, paramLong));
  }
  
  public final void zza(zzcgi paramzzcgi)
  {
    zzb(paramzzcgi, false);
    paramzzcgi = new zzcji(this, paramzzcgi);
    if (this.zziwf.zzawx().zzazs())
    {
      paramzzcgi.run();
      return;
    }
    this.zziwf.zzawx().zzg(paramzzcgi);
  }
  
  public final void zza(zzcgl paramzzcgl, zzcgi paramzzcgi)
  {
    zzbq.checkNotNull(paramzzcgl);
    zzbq.checkNotNull(paramzzcgl.zziyg);
    zzb(paramzzcgi, false);
    zzcgl localzzcgl = new zzcgl(paramzzcgl);
    localzzcgl.packageName = paramzzcgi.packageName;
    if (paramzzcgl.zziyg.getValue() == null)
    {
      this.zziwf.zzawx().zzg(new zzcit(this, localzzcgl, paramzzcgi));
      return;
    }
    this.zziwf.zzawx().zzg(new zzciu(this, localzzcgl, paramzzcgi));
  }
  
  public final void zza(zzcha paramzzcha, zzcgi paramzzcgi)
  {
    zzbq.checkNotNull(paramzzcha);
    zzb(paramzzcgi, false);
    this.zziwf.zzawx().zzg(new zzcjc(this, paramzzcha, paramzzcgi));
  }
  
  public final void zza(zzcha paramzzcha, String paramString1, String paramString2)
  {
    zzbq.checkNotNull(paramzzcha);
    zzbq.zzgm(paramString1);
    zzf(paramString1, true);
    this.zziwf.zzawx().zzg(new zzcjd(this, paramzzcha, paramString1));
  }
  
  public final void zza(zzcln paramzzcln, zzcgi paramzzcgi)
  {
    zzbq.checkNotNull(paramzzcln);
    zzb(paramzzcgi, false);
    if (paramzzcln.getValue() == null)
    {
      this.zziwf.zzawx().zzg(new zzcjf(this, paramzzcln, paramzzcgi));
      return;
    }
    this.zziwf.zzawx().zzg(new zzcjg(this, paramzzcln, paramzzcgi));
  }
  
  public final byte[] zza(zzcha paramzzcha, String paramString)
  {
    zzbq.zzgm(paramString);
    zzbq.checkNotNull(paramzzcha);
    zzf(paramString, true);
    this.zziwf.zzawy().zzazi().zzj("Log and bundle. event", this.zziwf.zzawt().zzjh(paramzzcha.name));
    long l1 = this.zziwf.zzws().nanoTime() / 1000000L;
    Object localObject = this.zziwf.zzawx().zzd(new zzcje(this, paramzzcha, paramString));
    try
    {
      byte[] arrayOfByte = (byte[])((Future)localObject).get();
      localObject = arrayOfByte;
      if (arrayOfByte == null)
      {
        this.zziwf.zzawy().zzazd().zzj("Log and bundle returned null. appId", zzchm.zzjk(paramString));
        localObject = new byte[0];
      }
      long l2 = this.zziwf.zzws().nanoTime() / 1000000L;
      this.zziwf.zzawy().zzazi().zzd("Log and bundle processed. event, size, time_ms", this.zziwf.zzawt().zzjh(paramzzcha.name), Integer.valueOf(localObject.length), Long.valueOf(l2 - l1));
      return (byte[])localObject;
    }
    catch (InterruptedException localInterruptedException)
    {
      this.zziwf.zzawy().zzazd().zzd("Failed to log and bundle. appId, event, error", zzchm.zzjk(paramString), this.zziwf.zzawt().zzjh(paramzzcha.name), localInterruptedException);
      return null;
    }
    catch (ExecutionException localExecutionException)
    {
      for (;;) {}
    }
  }
  
  public final void zzb(zzcgi paramzzcgi)
  {
    zzb(paramzzcgi, false);
    this.zziwf.zzawx().zzg(new zzcis(this, paramzzcgi));
  }
  
  public final void zzb(zzcgl paramzzcgl)
  {
    zzbq.checkNotNull(paramzzcgl);
    zzbq.checkNotNull(paramzzcgl.zziyg);
    zzf(paramzzcgl.packageName, true);
    zzcgl localzzcgl = new zzcgl(paramzzcgl);
    if (paramzzcgl.zziyg.getValue() == null)
    {
      this.zziwf.zzawx().zzg(new zzciv(this, localzzcgl));
      return;
    }
    this.zziwf.zzawx().zzg(new zzciw(this, localzzcgl));
  }
  
  public final String zzc(zzcgi paramzzcgi)
  {
    zzb(paramzzcgi, false);
    return this.zziwf.zzjx(paramzzcgi.packageName);
  }
  
  public final void zzd(zzcgi paramzzcgi)
  {
    zzf(paramzzcgi.packageName, false);
    this.zziwf.zzawx().zzg(new zzcjb(this, paramzzcgi));
  }
  
  public final List<zzcgl> zzj(String paramString1, String paramString2, String paramString3)
  {
    zzf(paramString1, true);
    paramString1 = this.zziwf.zzawx().zzc(new zzcja(this, paramString1, paramString2, paramString3));
    try
    {
      paramString1 = (List)paramString1.get();
      return paramString1;
    }
    catch (InterruptedException paramString1)
    {
      this.zziwf.zzawy().zzazd().zzj("Failed to get conditional user properties", paramString1);
      return Collections.emptyList();
    }
    catch (ExecutionException paramString1)
    {
      for (;;) {}
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcir.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */