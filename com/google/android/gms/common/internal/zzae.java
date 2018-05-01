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

public final class zzae
  implements Handler.Callback
{
  private final Handler mHandler;
  private final Object mLock = new Object();
  private final zzaf zzgab;
  private final ArrayList<GoogleApiClient.ConnectionCallbacks> zzgac = new ArrayList();
  private ArrayList<GoogleApiClient.ConnectionCallbacks> zzgad = new ArrayList();
  private final ArrayList<GoogleApiClient.OnConnectionFailedListener> zzgae = new ArrayList();
  private volatile boolean zzgaf = false;
  private final AtomicInteger zzgag = new AtomicInteger(0);
  private boolean zzgah = false;
  
  public zzae(Looper paramLooper, zzaf paramzzaf)
  {
    this.zzgab = paramzzaf;
    this.mHandler = new Handler(paramLooper, this);
  }
  
  public final boolean handleMessage(Message arg1)
  {
    if (???.what == 1)
    {
      GoogleApiClient.ConnectionCallbacks localConnectionCallbacks = (GoogleApiClient.ConnectionCallbacks)???.obj;
      synchronized (this.mLock)
      {
        if ((this.zzgaf) && (this.zzgab.isConnected()) && (this.zzgac.contains(localConnectionCallbacks))) {
          localConnectionCallbacks.onConnected(this.zzgab.zzafi());
        }
        return true;
      }
    }
    int i = ???.what;
    Log.wtf("GmsClientEvents", 45 + "Don't know how to handle message: " + i, new Exception());
    return false;
  }
  
  public final void registerConnectionCallbacks(GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks)
  {
    zzbq.checkNotNull(paramConnectionCallbacks);
    synchronized (this.mLock)
    {
      if (this.zzgac.contains(paramConnectionCallbacks))
      {
        String str = String.valueOf(paramConnectionCallbacks);
        Log.w("GmsClientEvents", String.valueOf(str).length() + 62 + "registerConnectionCallbacks(): listener " + str + " is already registered");
        if (this.zzgab.isConnected()) {
          this.mHandler.sendMessage(this.mHandler.obtainMessage(1, paramConnectionCallbacks));
        }
        return;
      }
      this.zzgac.add(paramConnectionCallbacks);
    }
  }
  
  public final void registerConnectionFailedListener(GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
  {
    zzbq.checkNotNull(paramOnConnectionFailedListener);
    synchronized (this.mLock)
    {
      if (this.zzgae.contains(paramOnConnectionFailedListener))
      {
        paramOnConnectionFailedListener = String.valueOf(paramOnConnectionFailedListener);
        Log.w("GmsClientEvents", String.valueOf(paramOnConnectionFailedListener).length() + 67 + "registerConnectionFailedListener(): listener " + paramOnConnectionFailedListener + " is already registered");
        return;
      }
      this.zzgae.add(paramOnConnectionFailedListener);
    }
  }
  
  public final void unregisterConnectionFailedListener(GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
  {
    zzbq.checkNotNull(paramOnConnectionFailedListener);
    synchronized (this.mLock)
    {
      if (!this.zzgae.remove(paramOnConnectionFailedListener))
      {
        paramOnConnectionFailedListener = String.valueOf(paramOnConnectionFailedListener);
        Log.w("GmsClientEvents", String.valueOf(paramOnConnectionFailedListener).length() + 57 + "unregisterConnectionFailedListener(): listener " + paramOnConnectionFailedListener + " not found");
      }
      return;
    }
  }
  
  public final void zzali()
  {
    this.zzgaf = false;
    this.zzgag.incrementAndGet();
  }
  
  public final void zzalj()
  {
    this.zzgaf = true;
  }
  
  public final void zzcg(int paramInt)
  {
    int i = 0;
    if (Looper.myLooper() == this.mHandler.getLooper()) {}
    for (boolean bool = true;; bool = false)
    {
      zzbq.zza(bool, "onUnintentionalDisconnection must only be called on the Handler thread");
      this.mHandler.removeMessages(1);
      synchronized (this.mLock)
      {
        this.zzgah = true;
        ArrayList localArrayList = new ArrayList(this.zzgac);
        int k = this.zzgag.get();
        localArrayList = (ArrayList)localArrayList;
        int m = localArrayList.size();
        Object localObject3;
        int j;
        do
        {
          if (i >= m) {
            break;
          }
          localObject3 = localArrayList.get(i);
          j = i + 1;
          localObject3 = (GoogleApiClient.ConnectionCallbacks)localObject3;
          if ((!this.zzgaf) || (this.zzgag.get() != k)) {
            break;
          }
          i = j;
        } while (!this.zzgac.contains(localObject3));
        ((GoogleApiClient.ConnectionCallbacks)localObject3).onConnectionSuspended(paramInt);
        i = j;
      }
    }
    this.zzgad.clear();
    this.zzgah = false;
  }
  
  public final void zzk(Bundle paramBundle)
  {
    boolean bool2 = true;
    int i = 0;
    boolean bool1;
    if (Looper.myLooper() == this.mHandler.getLooper())
    {
      bool1 = true;
      zzbq.zza(bool1, "onConnectionSuccess must only be called on the Handler thread");
    }
    for (;;)
    {
      synchronized (this.mLock)
      {
        if (this.zzgah) {
          break label215;
        }
        bool1 = true;
        zzbq.checkState(bool1);
        this.mHandler.removeMessages(1);
        this.zzgah = true;
        if (this.zzgad.size() != 0) {
          break label221;
        }
        bool1 = bool2;
        zzbq.checkState(bool1);
        ArrayList localArrayList = new ArrayList(this.zzgac);
        int k = this.zzgag.get();
        localArrayList = (ArrayList)localArrayList;
        int m = localArrayList.size();
        if (i >= m) {
          break label227;
        }
        Object localObject2 = localArrayList.get(i);
        int j = i + 1;
        localObject2 = (GoogleApiClient.ConnectionCallbacks)localObject2;
        if ((!this.zzgaf) || (!this.zzgab.isConnected()) || (this.zzgag.get() != k)) {
          break label227;
        }
        i = j;
        if (this.zzgad.contains(localObject2)) {
          continue;
        }
        ((GoogleApiClient.ConnectionCallbacks)localObject2).onConnected(paramBundle);
        i = j;
      }
      bool1 = false;
      break;
      label215:
      bool1 = false;
      continue;
      label221:
      bool1 = false;
    }
    label227:
    this.zzgad.clear();
    this.zzgah = false;
  }
  
  public final void zzk(ConnectionResult paramConnectionResult)
  {
    int i = 0;
    if (Looper.myLooper() == this.mHandler.getLooper()) {}
    for (boolean bool = true;; bool = false)
    {
      zzbq.zza(bool, "onConnectionFailure must only be called on the Handler thread");
      this.mHandler.removeMessages(1);
      synchronized (this.mLock)
      {
        ArrayList localArrayList = new ArrayList(this.zzgae);
        int k = this.zzgag.get();
        localArrayList = (ArrayList)localArrayList;
        int m = localArrayList.size();
        while (i < m)
        {
          Object localObject2 = localArrayList.get(i);
          int j = i + 1;
          localObject2 = (GoogleApiClient.OnConnectionFailedListener)localObject2;
          if ((!this.zzgaf) || (this.zzgag.get() != k)) {
            return;
          }
          i = j;
          if (this.zzgae.contains(localObject2))
          {
            ((GoogleApiClient.OnConnectionFailedListener)localObject2).onConnectionFailed(paramConnectionResult);
            i = j;
          }
        }
      }
      return;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/zzae.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */