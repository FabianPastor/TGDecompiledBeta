package com.google.android.gms.common.internal;

import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public final class GmsClientEventManager
  implements Handler.Callback
{
  private final Handler mHandler;
  private final Object mLock = new Object();
  private final GmsClientEventState zztf;
  private final ArrayList<GoogleApiClient.ConnectionCallbacks> zztg = new ArrayList();
  private final ArrayList<GoogleApiClient.ConnectionCallbacks> zzth = new ArrayList();
  private final ArrayList<GoogleApiClient.OnConnectionFailedListener> zzti = new ArrayList();
  private volatile boolean zztj = false;
  private final AtomicInteger zztk = new AtomicInteger(0);
  private boolean zztl = false;
  
  public GmsClientEventManager(Looper paramLooper, GmsClientEventState paramGmsClientEventState)
  {
    this.zztf = paramGmsClientEventState;
    this.mHandler = new Handler(paramLooper, this);
  }
  
  public final void disableCallbacks()
  {
    this.zztj = false;
    this.zztk.incrementAndGet();
  }
  
  public final void enableCallbacks()
  {
    this.zztj = true;
  }
  
  public final boolean handleMessage(Message arg1)
  {
    GoogleApiClient.ConnectionCallbacks localConnectionCallbacks;
    if (???.what == 1) {
      localConnectionCallbacks = (GoogleApiClient.ConnectionCallbacks)???.obj;
    }
    for (;;)
    {
      synchronized (this.mLock)
      {
        if ((this.zztj) && (this.zztf.isConnected()) && (this.zztg.contains(localConnectionCallbacks))) {
          localConnectionCallbacks.onConnected(this.zztf.getConnectionHint());
        }
        bool = true;
        return bool;
      }
      int i = ???.what;
      Log.wtf("GmsClientEvents", 45 + "Don't know how to handle message: " + i, new Exception());
      boolean bool = false;
    }
  }
  
  public final void onConnectionFailure(ConnectionResult paramConnectionResult)
  {
    int i = 0;
    boolean bool;
    if (Looper.myLooper() == this.mHandler.getLooper())
    {
      bool = true;
      Preconditions.checkState(bool, "onConnectionFailure must only be called on the Handler thread");
      this.mHandler.removeMessages(1);
    }
    for (;;)
    {
      synchronized (this.mLock)
      {
        ArrayList localArrayList = new java/util/ArrayList;
        localArrayList.<init>(this.zzti);
        int j = this.zztk.get();
        localArrayList = (ArrayList)localArrayList;
        int k = localArrayList.size();
        if (i < k)
        {
          Object localObject2 = localArrayList.get(i);
          int m = i + 1;
          localObject2 = (GoogleApiClient.OnConnectionFailedListener)localObject2;
          if ((!this.zztj) || (this.zztk.get() != j))
          {
            return;
            bool = false;
            break;
          }
          i = m;
          if (!this.zzti.contains(localObject2)) {
            continue;
          }
          ((GoogleApiClient.OnConnectionFailedListener)localObject2).onConnectionFailed(paramConnectionResult);
          i = m;
        }
      }
    }
  }
  
  public final void onConnectionSuccess(Bundle paramBundle)
  {
    boolean bool1 = true;
    int i = 0;
    boolean bool2;
    if (Looper.myLooper() == this.mHandler.getLooper())
    {
      bool2 = true;
      Preconditions.checkState(bool2, "onConnectionSuccess must only be called on the Handler thread");
    }
    for (;;)
    {
      synchronized (this.mLock)
      {
        if (this.zztl) {
          break label217;
        }
        bool2 = true;
        Preconditions.checkState(bool2);
        this.mHandler.removeMessages(1);
        this.zztl = true;
        if (this.zzth.size() != 0) {
          break label223;
        }
        bool2 = bool1;
        Preconditions.checkState(bool2);
        ArrayList localArrayList = new java/util/ArrayList;
        localArrayList.<init>(this.zztg);
        int j = this.zztk.get();
        localArrayList = (ArrayList)localArrayList;
        int k = localArrayList.size();
        if (i >= k) {
          break label229;
        }
        Object localObject2 = localArrayList.get(i);
        int m = i + 1;
        localObject2 = (GoogleApiClient.ConnectionCallbacks)localObject2;
        if ((!this.zztj) || (!this.zztf.isConnected()) || (this.zztk.get() != j)) {
          break label229;
        }
        i = m;
        if (this.zzth.contains(localObject2)) {
          continue;
        }
        ((GoogleApiClient.ConnectionCallbacks)localObject2).onConnected(paramBundle);
        i = m;
      }
      bool2 = false;
      break;
      label217:
      bool2 = false;
      continue;
      label223:
      bool2 = false;
    }
    label229:
    this.zzth.clear();
    this.zztl = false;
  }
  
  public final void onUnintentionalDisconnection(int paramInt)
  {
    int i = 0;
    if (Looper.myLooper() == this.mHandler.getLooper()) {}
    for (boolean bool = true;; bool = false)
    {
      Preconditions.checkState(bool, "onUnintentionalDisconnection must only be called on the Handler thread");
      this.mHandler.removeMessages(1);
      synchronized (this.mLock)
      {
        this.zztl = true;
        ArrayList localArrayList = new java/util/ArrayList;
        localArrayList.<init>(this.zztg);
        int j = this.zztk.get();
        localArrayList = (ArrayList)localArrayList;
        int k = localArrayList.size();
        Object localObject3;
        int m;
        do
        {
          if (i >= k) {
            break;
          }
          localObject3 = localArrayList.get(i);
          m = i + 1;
          localObject3 = (GoogleApiClient.ConnectionCallbacks)localObject3;
          if ((!this.zztj) || (this.zztk.get() != j)) {
            break;
          }
          i = m;
        } while (!this.zztg.contains(localObject3));
        ((GoogleApiClient.ConnectionCallbacks)localObject3).onConnectionSuspended(paramInt);
        i = m;
      }
    }
    this.zzth.clear();
    this.zztl = false;
  }
  
  public final void registerConnectionCallbacks(GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks)
  {
    Preconditions.checkNotNull(paramConnectionCallbacks);
    synchronized (this.mLock)
    {
      if (this.zztg.contains(paramConnectionCallbacks))
      {
        String str = String.valueOf(paramConnectionCallbacks);
        int i = String.valueOf(str).length();
        StringBuilder localStringBuilder = new java/lang/StringBuilder;
        localStringBuilder.<init>(i + 62);
        Log.w("GmsClientEvents", "registerConnectionCallbacks(): listener " + str + " is already registered");
        if (this.zztf.isConnected()) {
          this.mHandler.sendMessage(this.mHandler.obtainMessage(1, paramConnectionCallbacks));
        }
        return;
      }
      this.zztg.add(paramConnectionCallbacks);
    }
  }
  
  public final void registerConnectionFailedListener(GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
  {
    Preconditions.checkNotNull(paramOnConnectionFailedListener);
    synchronized (this.mLock)
    {
      if (this.zzti.contains(paramOnConnectionFailedListener))
      {
        paramOnConnectionFailedListener = String.valueOf(paramOnConnectionFailedListener);
        int i = String.valueOf(paramOnConnectionFailedListener).length();
        StringBuilder localStringBuilder = new java/lang/StringBuilder;
        localStringBuilder.<init>(i + 67);
        Log.w("GmsClientEvents", "registerConnectionFailedListener(): listener " + paramOnConnectionFailedListener + " is already registered");
        return;
      }
      this.zzti.add(paramOnConnectionFailedListener);
    }
  }
  
  public final void unregisterConnectionFailedListener(GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
  {
    Preconditions.checkNotNull(paramOnConnectionFailedListener);
    synchronized (this.mLock)
    {
      if (!this.zzti.remove(paramOnConnectionFailedListener))
      {
        paramOnConnectionFailedListener = String.valueOf(paramOnConnectionFailedListener);
        int i = String.valueOf(paramOnConnectionFailedListener).length();
        StringBuilder localStringBuilder = new java/lang/StringBuilder;
        localStringBuilder.<init>(i + 57);
        Log.w("GmsClientEvents", "unregisterConnectionFailedListener(): listener " + paramOnConnectionFailedListener + " not found");
      }
      return;
    }
  }
  
  public static abstract interface GmsClientEventState
  {
    public abstract Bundle getConnectionHint();
    
    public abstract boolean isConnected();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/GmsClientEventManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */