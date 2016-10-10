package com.google.android.gms.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.ApiOptions;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.zzd;
import com.google.android.gms.common.internal.zzai;
import com.google.android.gms.common.internal.zze.zzf;
import com.google.android.gms.tasks.TaskCompletionSource;
import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class zzqt
  implements Handler.Callback
{
  private static zzqt yu;
  private static final Object zzaok = new Object();
  private final Context mContext;
  private final Handler mHandler;
  private final GoogleApiAvailability vP;
  private long xS = 120000L;
  private long xT = 5000L;
  private final Set<zzpz<?>> yA = new com.google.android.gms.common.util.zza();
  private final ReferenceQueue<zzd<?>> yB = new ReferenceQueue();
  private final SparseArray<zza> yC = new SparseArray();
  private zzb yD;
  private long yt = 10000L;
  private int yv = -1;
  private final AtomicInteger yw = new AtomicInteger(1);
  private final SparseArray<zzc<?>> yx = new SparseArray();
  private final Map<zzpz<?>, zzc<?>> yy = new ConcurrentHashMap(5, 0.75F, 1);
  private zzqi yz = null;
  
  private zzqt(Context paramContext)
  {
    this(paramContext, GoogleApiAvailability.getInstance());
  }
  
  private zzqt(Context paramContext, GoogleApiAvailability paramGoogleApiAvailability)
  {
    this.mContext = paramContext;
    paramContext = new HandlerThread("GoogleApiHandler", 9);
    paramContext.start();
    this.mHandler = new Handler(paramContext.getLooper(), this);
    this.vP = paramGoogleApiAvailability;
  }
  
  private int zza(zzd<?> paramzzd)
  {
    int i = this.yw.getAndIncrement();
    this.mHandler.sendMessage(this.mHandler.obtainMessage(6, i, 0, paramzzd));
    return i;
  }
  
  public static Pair<zzqt, Integer> zza(Context paramContext, zzd<?> paramzzd)
  {
    synchronized (zzaok)
    {
      if (yu == null) {
        yu = new zzqt(paramContext.getApplicationContext());
      }
      int i = yu.zza(paramzzd);
      paramContext = Pair.create(yu, Integer.valueOf(i));
      return paramContext;
    }
  }
  
  @WorkerThread
  private void zza(int paramInt, zzrd.zzb<?> paramzzb, TaskCompletionSource<Void> paramTaskCompletionSource)
  {
    ((zzc)this.yx.get(paramInt)).zzb(paramInt, paramzzb, paramTaskCompletionSource);
  }
  
  @WorkerThread
  private void zza(int paramInt, zzri paramzzri, TaskCompletionSource<Void> paramTaskCompletionSource)
  {
    ((zzc)this.yx.get(paramInt)).zzb(paramInt, paramzzri, paramTaskCompletionSource);
  }
  
  @WorkerThread
  private void zza(zzd<?> paramzzd, int paramInt)
  {
    Object localObject = paramzzd.zzapx();
    if (!this.yy.containsKey(localObject)) {
      this.yy.put(localObject, new zzc(paramzzd));
    }
    localObject = (zzc)this.yy.get(localObject);
    ((zzc)localObject).zzfw(paramInt);
    this.yx.put(paramInt, localObject);
    zzc.zza((zzc)localObject);
    this.yC.put(paramInt, new zza(paramzzd, paramInt, this.yB));
    if ((this.yD == null) || (!zzb.zza(this.yD).get()))
    {
      this.yD = new zzb(this.yB, this.yC);
      this.yD.start();
    }
  }
  
  @WorkerThread
  private void zza(zzpy paramzzpy)
  {
    ((zzc)this.yx.get(paramzzpy.wf)).zzb(paramzzpy);
  }
  
  public static zzqt zzasa()
  {
    synchronized (zzaok)
    {
      zzqt localzzqt = yu;
      return localzzqt;
    }
  }
  
  @WorkerThread
  private void zzasb()
  {
    Iterator localIterator = this.yy.values().iterator();
    while (localIterator.hasNext())
    {
      zzc localzzc = (zzc)localIterator.next();
      localzzc.zzasf();
      zzc.zza(localzzc);
    }
  }
  
  @WorkerThread
  private void zze(int paramInt, boolean paramBoolean)
  {
    zzc localzzc = (zzc)this.yx.get(paramInt);
    if (localzzc != null)
    {
      if (!paramBoolean) {
        this.yx.delete(paramInt);
      }
      localzzc.zzf(paramInt, paramBoolean);
      return;
    }
    Log.wtf("GoogleApiManager", 52 + "onRelease received for unknown instance: " + paramInt, new Exception());
  }
  
  @WorkerThread
  private void zzfv(int paramInt)
  {
    zzc localzzc = (zzc)this.yx.get(paramInt);
    if (localzzc != null)
    {
      this.yx.delete(paramInt);
      localzzc.zzfx(paramInt);
      return;
    }
    Log.wtf("GoogleApiManager", 64 + "onCleanupLeakInternal received for unknown instance: " + paramInt, new Exception());
  }
  
  @WorkerThread
  public boolean handleMessage(Message paramMessage)
  {
    boolean bool = false;
    int i;
    switch (paramMessage.what)
    {
    default: 
      i = paramMessage.what;
      Log.w("GoogleApiManager", 31 + "Unknown message id: " + i);
      return false;
    case 1: 
      zza((zzqb)paramMessage.obj);
    }
    for (;;)
    {
      return true;
      zza((zzd)paramMessage.obj, paramMessage.arg1);
      continue;
      zzasb();
      continue;
      zzfv(paramMessage.arg1);
      continue;
      i = paramMessage.arg1;
      if (paramMessage.arg2 == 1) {
        bool = true;
      }
      zze(i, bool);
      continue;
      zza((zzpy)paramMessage.obj);
      continue;
      if (this.yx.get(paramMessage.arg1) != null)
      {
        zzc.zza((zzc)this.yx.get(paramMessage.arg1), new Status(17, "Error resolution was canceled by the user."));
        continue;
        if (this.yy.containsKey(paramMessage.obj))
        {
          zzc.zzb((zzc)this.yy.get(paramMessage.obj));
          continue;
          Pair localPair = (Pair)paramMessage.obj;
          zza(paramMessage.arg1, (zzri)localPair.first, (TaskCompletionSource)localPair.second);
          continue;
          if (this.yy.containsKey(paramMessage.obj))
          {
            zzc.zzc((zzc)this.yy.get(paramMessage.obj));
            continue;
            if (this.yy.containsKey(paramMessage.obj))
            {
              zzc.zzd((zzc)this.yy.get(paramMessage.obj));
              continue;
              localPair = (Pair)paramMessage.obj;
              zza(paramMessage.arg1, (zzrd.zzb)localPair.first, (TaskCompletionSource)localPair.second);
            }
          }
        }
      }
    }
  }
  
  public void zza(ConnectionResult paramConnectionResult, int paramInt)
  {
    if (!zzc(paramConnectionResult, paramInt)) {
      this.mHandler.sendMessage(this.mHandler.obtainMessage(5, paramInt, 0));
    }
  }
  
  public <O extends Api.ApiOptions> void zza(zzd<O> paramzzd, int paramInt, zzqc.zza<? extends Result, Api.zzb> paramzza)
  {
    paramzzd = new zzpy.zzb(paramzzd.getInstanceId(), paramInt, paramzza);
    this.mHandler.sendMessage(this.mHandler.obtainMessage(4, paramzzd));
  }
  
  public <O extends Api.ApiOptions, TResult> void zza(zzd<O> paramzzd, int paramInt, zzro<Api.zzb, TResult> paramzzro, TaskCompletionSource<TResult> paramTaskCompletionSource)
  {
    paramzzd = new zzpy.zzd(paramzzd.getInstanceId(), paramInt, paramzzro, paramTaskCompletionSource);
    this.mHandler.sendMessage(this.mHandler.obtainMessage(4, paramzzd));
  }
  
  @WorkerThread
  public void zza(zzqb paramzzqb)
  {
    Iterator localIterator = paramzzqb.zzaqm().iterator();
    for (;;)
    {
      zzpz localzzpz;
      zzc localzzc;
      if (localIterator.hasNext())
      {
        localzzpz = (zzpz)localIterator.next();
        localzzc = (zzc)this.yy.get(localzzpz);
        if (localzzc == null) {
          paramzzqb.cancel();
        }
      }
      else
      {
        return;
      }
      if (localzzc.isConnected()) {
        paramzzqb.zza(localzzpz, ConnectionResult.uJ);
      } else if (localzzc.zzasg() != null) {
        paramzzqb.zza(localzzpz, localzzc.zzasg());
      } else {
        localzzc.zzb(paramzzqb);
      }
    }
  }
  
  public void zza(zzqi paramzzqi)
  {
    Object localObject = zzaok;
    if (paramzzqi == null) {}
    try
    {
      this.yz = null;
      this.yA.clear();
      return;
    }
    finally {}
  }
  
  public void zzaqk()
  {
    this.mHandler.sendMessage(this.mHandler.obtainMessage(3));
  }
  
  boolean zzc(ConnectionResult paramConnectionResult, int paramInt)
  {
    if ((paramConnectionResult.hasResolution()) || (this.vP.isUserResolvableError(paramConnectionResult.getErrorCode())))
    {
      this.vP.zza(this.mContext, paramConnectionResult, paramInt);
      return true;
    }
    return false;
  }
  
  public void zzd(int paramInt, boolean paramBoolean)
  {
    Handler localHandler1 = this.mHandler;
    Handler localHandler2 = this.mHandler;
    if (paramBoolean) {}
    for (int i = 1;; i = 2)
    {
      localHandler1.sendMessage(localHandler2.obtainMessage(8, paramInt, i));
      return;
    }
  }
  
  private final class zza
    extends PhantomReference<zzd<?>>
  {
    private final int wf;
    
    public zza(int paramInt, ReferenceQueue<zzd<?>> paramReferenceQueue)
    {
      super(localReferenceQueue);
      this.wf = paramReferenceQueue;
    }
    
    public void zzasd()
    {
      zzqt.zza(zzqt.this).sendMessage(zzqt.zza(zzqt.this).obtainMessage(2, this.wf, 2));
    }
  }
  
  private static final class zzb
    extends Thread
  {
    private final ReferenceQueue<zzd<?>> yB;
    private final SparseArray<zzqt.zza> yC;
    private final AtomicBoolean yF = new AtomicBoolean();
    
    public zzb(ReferenceQueue<zzd<?>> paramReferenceQueue, SparseArray<zzqt.zza> paramSparseArray)
    {
      super();
      this.yB = paramReferenceQueue;
      this.yC = paramSparseArray;
    }
    
    /* Error */
    public void run()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 29	com/google/android/gms/internal/zzqt$zzb:yF	Ljava/util/concurrent/atomic/AtomicBoolean;
      //   4: iconst_1
      //   5: invokevirtual 45	java/util/concurrent/atomic/AtomicBoolean:set	(Z)V
      //   8: bipush 10
      //   10: invokestatic 51	android/os/Process:setThreadPriority	(I)V
      //   13: aload_0
      //   14: getfield 29	com/google/android/gms/internal/zzqt$zzb:yF	Ljava/util/concurrent/atomic/AtomicBoolean;
      //   17: invokevirtual 55	java/util/concurrent/atomic/AtomicBoolean:get	()Z
      //   20: ifeq +42 -> 62
      //   23: aload_0
      //   24: getfield 31	com/google/android/gms/internal/zzqt$zzb:yB	Ljava/lang/ref/ReferenceQueue;
      //   27: invokevirtual 61	java/lang/ref/ReferenceQueue:remove	()Ljava/lang/ref/Reference;
      //   30: checkcast 63	com/google/android/gms/internal/zzqt$zza
      //   33: astore_1
      //   34: aload_0
      //   35: getfield 33	com/google/android/gms/internal/zzqt$zzb:yC	Landroid/util/SparseArray;
      //   38: aload_1
      //   39: invokestatic 66	com/google/android/gms/internal/zzqt$zza:zza	(Lcom/google/android/gms/internal/zzqt$zza;)I
      //   42: invokevirtual 70	android/util/SparseArray:remove	(I)V
      //   45: aload_1
      //   46: invokevirtual 73	com/google/android/gms/internal/zzqt$zza:zzasd	()V
      //   49: goto -36 -> 13
      //   52: astore_1
      //   53: aload_0
      //   54: getfield 29	com/google/android/gms/internal/zzqt$zzb:yF	Ljava/util/concurrent/atomic/AtomicBoolean;
      //   57: iconst_0
      //   58: invokevirtual 45	java/util/concurrent/atomic/AtomicBoolean:set	(Z)V
      //   61: return
      //   62: aload_0
      //   63: getfield 29	com/google/android/gms/internal/zzqt$zzb:yF	Ljava/util/concurrent/atomic/AtomicBoolean;
      //   66: iconst_0
      //   67: invokevirtual 45	java/util/concurrent/atomic/AtomicBoolean:set	(Z)V
      //   70: return
      //   71: astore_1
      //   72: aload_0
      //   73: getfield 29	com/google/android/gms/internal/zzqt$zzb:yF	Ljava/util/concurrent/atomic/AtomicBoolean;
      //   76: iconst_0
      //   77: invokevirtual 45	java/util/concurrent/atomic/AtomicBoolean:set	(Z)V
      //   80: aload_1
      //   81: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	82	0	this	zzb
      //   33	13	1	localzza	zzqt.zza
      //   52	1	1	localInterruptedException	InterruptedException
      //   71	10	1	localObject	Object
      // Exception table:
      //   from	to	target	type
      //   13	49	52	java/lang/InterruptedException
      //   13	49	71	finally
    }
  }
  
  private class zzc<O extends Api.ApiOptions>
    implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, zzqg
  {
    private final Api.zze vC;
    private final zzpz<O> vx;
    private final SparseArray<Map<zzrd.zzb<?>, zzri>> wg = new SparseArray();
    private boolean xR;
    private final Queue<zzpy> yG = new LinkedList();
    private final Api.zzb yH;
    private final SparseArray<zzrq> yI = new SparseArray();
    private final Set<zzqb> yJ = new HashSet();
    private ConnectionResult yK = null;
    
    @WorkerThread
    public zzc()
    {
      Object localObject;
      this.vC = ((zzd)localObject).zza(zzqt.zza(zzqt.this).getLooper(), this, this);
      if ((this.vC instanceof zzai)) {}
      for (this.yH = ((zzai)this.vC).zzavk();; this.yH = this.vC)
      {
        this.vx = ((zzd)localObject).zzapx();
        return;
      }
    }
    
    @WorkerThread
    private void connect()
    {
      if ((this.vC.isConnected()) || (this.vC.isConnecting())) {
        return;
      }
      if ((this.vC.zzapr()) && (zzqt.zzk(zzqt.this) != 0))
      {
        zzqt.zza(zzqt.this, zzqt.zzi(zzqt.this).isGooglePlayServicesAvailable(zzqt.zzh(zzqt.this)));
        if (zzqt.zzk(zzqt.this) != 0)
        {
          onConnectionFailed(new ConnectionResult(zzqt.zzk(zzqt.this), null));
          return;
        }
      }
      this.vC.zza(new zzqt.zzd(zzqt.this, this.vC, this.vx));
    }
    
    @WorkerThread
    private void resume()
    {
      if (this.xR) {
        connect();
      }
    }
    
    @WorkerThread
    private void zzab(Status paramStatus)
    {
      Iterator localIterator = this.yG.iterator();
      while (localIterator.hasNext()) {
        ((zzpy)localIterator.next()).zzx(paramStatus);
      }
      this.yG.clear();
    }
    
    @WorkerThread
    private void zzarr()
    {
      if (this.xR)
      {
        zzash();
        if (zzqt.zzi(zzqt.this).isGooglePlayServicesAvailable(zzqt.zzh(zzqt.this)) != 18) {
          break label60;
        }
      }
      label60:
      for (Status localStatus = new Status(8, "Connection timed out while waiting for Google Play services update to complete.");; localStatus = new Status(8, "API failed to connect while resuming due to an unknown error."))
      {
        zzab(localStatus);
        this.vC.disconnect();
        return;
      }
    }
    
    @WorkerThread
    private void zzash()
    {
      if (this.xR)
      {
        zzqt.zza(zzqt.this).removeMessages(10, this.vx);
        zzqt.zza(zzqt.this).removeMessages(9, this.vx);
        this.xR = false;
      }
    }
    
    private void zzasi()
    {
      zzqt.zza(zzqt.this).removeMessages(11, this.vx);
      zzqt.zza(zzqt.this).sendMessageDelayed(zzqt.zza(zzqt.this).obtainMessage(11, this.vx), zzqt.zzj(zzqt.this));
    }
    
    private void zzasj()
    {
      int i;
      if ((this.vC.isConnected()) && (this.wg.size() == 0)) {
        i = 0;
      }
      while (i < this.yI.size())
      {
        if (((zzrq)this.yI.get(this.yI.keyAt(i))).zzasx())
        {
          zzasi();
          return;
        }
        i += 1;
      }
      this.vC.disconnect();
    }
    
    @WorkerThread
    private void zzc(zzpy paramzzpy)
    {
      paramzzpy.zza(this.yI);
      try
      {
        paramzzpy.zzb(this.yH);
        return;
      }
      catch (DeadObjectException paramzzpy)
      {
        this.vC.disconnect();
        onConnectionSuspended(1);
      }
    }
    
    @WorkerThread
    private void zzj(ConnectionResult paramConnectionResult)
    {
      Iterator localIterator = this.yJ.iterator();
      while (localIterator.hasNext()) {
        ((zzqb)localIterator.next()).zza(this.vx, paramConnectionResult);
      }
      this.yJ.clear();
    }
    
    boolean isConnected()
    {
      return this.vC.isConnected();
    }
    
    @WorkerThread
    public void onConnected(@Nullable Bundle paramBundle)
    {
      zzasf();
      zzj(ConnectionResult.uJ);
      zzash();
      int i = 0;
      while (i < this.wg.size())
      {
        paramBundle = ((Map)this.wg.get(this.wg.keyAt(i))).values().iterator();
        while (paramBundle.hasNext())
        {
          zzri localzzri = (zzri)paramBundle.next();
          try
          {
            localzzri.wj.zza(this.yH, new TaskCompletionSource());
          }
          catch (DeadObjectException localDeadObjectException)
          {
            this.vC.disconnect();
            onConnectionSuspended(1);
          }
        }
        i += 1;
      }
      zzase();
      zzasi();
    }
    
    @WorkerThread
    public void onConnectionFailed(@NonNull ConnectionResult paramConnectionResult)
    {
      zzasf();
      zzqt.zza(zzqt.this, -1);
      zzj(paramConnectionResult);
      int i = this.yI.keyAt(0);
      if (this.yG.isEmpty()) {
        this.yK = paramConnectionResult;
      }
      do
      {
        return;
        synchronized (zzqt.zzasc())
        {
          if ((zzqt.zzd(zzqt.this) != null) && (zzqt.zze(zzqt.this).contains(this.vx)))
          {
            zzqt.zzd(zzqt.this).zzb(paramConnectionResult, i);
            return;
          }
        }
      } while (zzqt.this.zzc(paramConnectionResult, i));
      if (paramConnectionResult.getErrorCode() == 18) {
        this.xR = true;
      }
      if (this.xR)
      {
        zzqt.zza(zzqt.this).sendMessageDelayed(Message.obtain(zzqt.zza(zzqt.this), 9, this.vx), zzqt.zzb(zzqt.this));
        return;
      }
      paramConnectionResult = String.valueOf(this.vx.zzaqj());
      zzab(new Status(17, String.valueOf(paramConnectionResult).length() + 38 + "API: " + paramConnectionResult + " is not available on this device."));
    }
    
    @WorkerThread
    public void onConnectionSuspended(int paramInt)
    {
      zzasf();
      this.xR = true;
      zzqt.zza(zzqt.this).sendMessageDelayed(Message.obtain(zzqt.zza(zzqt.this), 9, this.vx), zzqt.zzb(zzqt.this));
      zzqt.zza(zzqt.this).sendMessageDelayed(Message.obtain(zzqt.zza(zzqt.this), 10, this.vx), zzqt.zzc(zzqt.this));
      zzqt.zza(zzqt.this, -1);
    }
    
    public void zza(ConnectionResult paramConnectionResult, Api<?> paramApi, int paramInt)
    {
      onConnectionFailed(paramConnectionResult);
    }
    
    @WorkerThread
    public void zzase()
    {
      while ((this.vC.isConnected()) && (!this.yG.isEmpty())) {
        zzc((zzpy)this.yG.remove());
      }
    }
    
    @WorkerThread
    public void zzasf()
    {
      this.yK = null;
    }
    
    ConnectionResult zzasg()
    {
      return this.yK;
    }
    
    @WorkerThread
    public void zzb(int paramInt, @NonNull zzrd.zzb<?> paramzzb, @NonNull TaskCompletionSource<Void> paramTaskCompletionSource)
    {
      Map localMap = (Map)this.wg.get(paramInt);
      if ((localMap != null) && (localMap.get(paramzzb) != null))
      {
        zzb(new zzpy.zze(paramInt, ((zzri)localMap.get(paramzzb)).wk, paramTaskCompletionSource, this.wg));
        return;
      }
      paramTaskCompletionSource.setException(new com.google.android.gms.common.api.zza(Status.wa));
      Log.wtf("GoogleApiManager", "Received call to unregister a listener without a matching registration call.", new Exception());
    }
    
    @WorkerThread
    public void zzb(int paramInt, @NonNull zzri paramzzri, @NonNull TaskCompletionSource<Void> paramTaskCompletionSource)
    {
      zzb(new zzpy.zzc(paramInt, paramzzri, paramTaskCompletionSource, this.wg));
    }
    
    @WorkerThread
    public void zzb(zzpy paramzzpy)
    {
      if (this.vC.isConnected())
      {
        zzc(paramzzpy);
        zzasi();
        return;
      }
      this.yG.add(paramzzpy);
      if ((this.yK != null) && (this.yK.hasResolution()))
      {
        onConnectionFailed(this.yK);
        return;
      }
      connect();
    }
    
    @WorkerThread
    public void zzb(zzqb paramzzqb)
    {
      this.yJ.add(paramzzqb);
    }
    
    @WorkerThread
    public void zzf(int paramInt, boolean paramBoolean)
    {
      ??? = this.yG.iterator();
      while (((Iterator)???).hasNext())
      {
        zzpy localzzpy = (zzpy)((Iterator)???).next();
        if ((localzzpy.wf == paramInt) && (localzzpy.lN != 1) && (localzzpy.cancel())) {
          ((Iterator)???).remove();
        }
      }
      ((zzrq)this.yI.get(paramInt)).release();
      this.wg.delete(paramInt);
      if (!paramBoolean)
      {
        this.yI.remove(paramInt);
        zzqt.zzf(zzqt.this).remove(paramInt);
        if ((this.yI.size() == 0) && (this.yG.isEmpty()))
        {
          zzash();
          this.vC.disconnect();
          zzqt.zzg(zzqt.this).remove(this.vx);
          synchronized (zzqt.zzasc())
          {
            zzqt.zze(zzqt.this).remove(this.vx);
            return;
          }
        }
      }
    }
    
    @WorkerThread
    public void zzfw(int paramInt)
    {
      this.yI.put(paramInt, new zzrq(this.vC));
    }
    
    @WorkerThread
    public void zzfx(final int paramInt)
    {
      ((zzrq)this.yI.get(paramInt)).zza(new zzrq.zzc()
      {
        public void zzask()
        {
          if (zzqt.zzc.zze(zzqt.zzc.this).isEmpty()) {
            zzqt.zzc.this.zzf(paramInt, false);
          }
        }
      });
    }
  }
  
  private class zzd
    implements zze.zzf
  {
    private final Api.zze vC;
    private final zzpz<?> vx;
    
    public zzd(zzpz<?> paramzzpz)
    {
      this.vC = paramzzpz;
      zzpz localzzpz;
      this.vx = localzzpz;
    }
    
    @WorkerThread
    public void zzh(@NonNull ConnectionResult paramConnectionResult)
    {
      if (paramConnectionResult.isSuccess())
      {
        this.vC.zza(null, Collections.emptySet());
        return;
      }
      ((zzqt.zzc)zzqt.zzg(zzqt.this).get(this.vx)).onConnectionFailed(paramConnectionResult);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzqt.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */