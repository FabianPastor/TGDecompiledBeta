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
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

public final class zzm
  implements Handler.Callback
{
  private final zza Cs;
  private final ArrayList<GoogleApiClient.ConnectionCallbacks> Ct = new ArrayList();
  final ArrayList<GoogleApiClient.ConnectionCallbacks> Cu = new ArrayList();
  private final ArrayList<GoogleApiClient.OnConnectionFailedListener> Cv = new ArrayList();
  private volatile boolean Cw = false;
  private final AtomicInteger Cx = new AtomicInteger(0);
  private boolean Cy = false;
  private final Handler mHandler;
  private final Object zzakd = new Object();
  
  public zzm(Looper paramLooper, zza paramzza)
  {
    this.Cs = paramzza;
    this.mHandler = new Handler(paramLooper, this);
  }
  
  public boolean handleMessage(Message arg1)
  {
    if (???.what == 1)
    {
      GoogleApiClient.ConnectionCallbacks localConnectionCallbacks = (GoogleApiClient.ConnectionCallbacks)???.obj;
      synchronized (this.zzakd)
      {
        if ((this.Cw) && (this.Cs.isConnected()) && (this.Ct.contains(localConnectionCallbacks))) {
          localConnectionCallbacks.onConnected(this.Cs.zzaoe());
        }
        return true;
      }
    }
    int i = ???.what;
    Log.wtf("GmsClientEvents", 45 + "Don't know how to handle message: " + i, new Exception());
    return false;
  }
  
  public boolean isConnectionCallbacksRegistered(GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks)
  {
    zzac.zzy(paramConnectionCallbacks);
    synchronized (this.zzakd)
    {
      boolean bool = this.Ct.contains(paramConnectionCallbacks);
      return bool;
    }
  }
  
  public boolean isConnectionFailedListenerRegistered(GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
  {
    zzac.zzy(paramOnConnectionFailedListener);
    synchronized (this.zzakd)
    {
      boolean bool = this.Cv.contains(paramOnConnectionFailedListener);
      return bool;
    }
  }
  
  public void registerConnectionCallbacks(GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks)
  {
    zzac.zzy(paramConnectionCallbacks);
    synchronized (this.zzakd)
    {
      if (this.Ct.contains(paramConnectionCallbacks))
      {
        String str = String.valueOf(paramConnectionCallbacks);
        Log.w("GmsClientEvents", String.valueOf(str).length() + 62 + "registerConnectionCallbacks(): listener " + str + " is already registered");
        if (this.Cs.isConnected()) {
          this.mHandler.sendMessage(this.mHandler.obtainMessage(1, paramConnectionCallbacks));
        }
        return;
      }
      this.Ct.add(paramConnectionCallbacks);
    }
  }
  
  public void registerConnectionFailedListener(GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
  {
    zzac.zzy(paramOnConnectionFailedListener);
    synchronized (this.zzakd)
    {
      if (this.Cv.contains(paramOnConnectionFailedListener))
      {
        paramOnConnectionFailedListener = String.valueOf(paramOnConnectionFailedListener);
        Log.w("GmsClientEvents", String.valueOf(paramOnConnectionFailedListener).length() + 67 + "registerConnectionFailedListener(): listener " + paramOnConnectionFailedListener + " is already registered");
        return;
      }
      this.Cv.add(paramOnConnectionFailedListener);
    }
  }
  
  public void unregisterConnectionCallbacks(GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks)
  {
    zzac.zzy(paramConnectionCallbacks);
    synchronized (this.zzakd)
    {
      if (!this.Ct.remove(paramConnectionCallbacks))
      {
        paramConnectionCallbacks = String.valueOf(paramConnectionCallbacks);
        Log.w("GmsClientEvents", String.valueOf(paramConnectionCallbacks).length() + 52 + "unregisterConnectionCallbacks(): listener " + paramConnectionCallbacks + " not found");
      }
      while (!this.Cy) {
        return;
      }
      this.Cu.add(paramConnectionCallbacks);
    }
  }
  
  public void unregisterConnectionFailedListener(GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
  {
    zzac.zzy(paramOnConnectionFailedListener);
    synchronized (this.zzakd)
    {
      if (!this.Cv.remove(paramOnConnectionFailedListener))
      {
        paramOnConnectionFailedListener = String.valueOf(paramOnConnectionFailedListener);
        Log.w("GmsClientEvents", String.valueOf(paramOnConnectionFailedListener).length() + 57 + "unregisterConnectionFailedListener(): listener " + paramOnConnectionFailedListener + " not found");
      }
      return;
    }
  }
  
  public void zzaut()
  {
    this.Cw = false;
    this.Cx.incrementAndGet();
  }
  
  public void zzauu()
  {
    this.Cw = true;
  }
  
  public void zzgo(int paramInt)
  {
    boolean bool = false;
    if (Looper.myLooper() == this.mHandler.getLooper()) {
      bool = true;
    }
    zzac.zza(bool, "onUnintentionalDisconnection must only be called on the Handler thread");
    this.mHandler.removeMessages(1);
    synchronized (this.zzakd)
    {
      this.Cy = true;
      Object localObject2 = new ArrayList(this.Ct);
      int i = this.Cx.get();
      localObject2 = ((ArrayList)localObject2).iterator();
      GoogleApiClient.ConnectionCallbacks localConnectionCallbacks;
      do
      {
        if (((Iterator)localObject2).hasNext())
        {
          localConnectionCallbacks = (GoogleApiClient.ConnectionCallbacks)((Iterator)localObject2).next();
          if ((this.Cw) && (this.Cx.get() == i)) {}
        }
        else
        {
          this.Cu.clear();
          this.Cy = false;
          return;
        }
      } while (!this.Ct.contains(localConnectionCallbacks));
      localConnectionCallbacks.onConnectionSuspended(paramInt);
    }
  }
  
  public void zzn(ConnectionResult paramConnectionResult)
  {
    if (Looper.myLooper() == this.mHandler.getLooper()) {}
    for (boolean bool = true;; bool = false)
    {
      zzac.zza(bool, "onConnectionFailure must only be called on the Handler thread");
      this.mHandler.removeMessages(1);
      synchronized (this.zzakd)
      {
        Object localObject2 = new ArrayList(this.Cv);
        int i = this.Cx.get();
        localObject2 = ((ArrayList)localObject2).iterator();
        while (((Iterator)localObject2).hasNext())
        {
          GoogleApiClient.OnConnectionFailedListener localOnConnectionFailedListener = (GoogleApiClient.OnConnectionFailedListener)((Iterator)localObject2).next();
          if ((!this.Cw) || (this.Cx.get() != i)) {
            return;
          }
          if (this.Cv.contains(localOnConnectionFailedListener)) {
            localOnConnectionFailedListener.onConnectionFailed(paramConnectionResult);
          }
        }
      }
      return;
    }
  }
  
  public void zzp(Bundle paramBundle)
  {
    boolean bool2 = true;
    boolean bool1;
    if (Looper.myLooper() == this.mHandler.getLooper())
    {
      bool1 = true;
      zzac.zza(bool1, "onConnectionSuccess must only be called on the Handler thread");
    }
    for (;;)
    {
      synchronized (this.zzakd)
      {
        if (this.Cy) {
          break label206;
        }
        bool1 = true;
        zzac.zzbr(bool1);
        this.mHandler.removeMessages(1);
        this.Cy = true;
        if (this.Cu.size() != 0) {
          break label211;
        }
        bool1 = bool2;
        zzac.zzbr(bool1);
        Object localObject2 = new ArrayList(this.Ct);
        int i = this.Cx.get();
        localObject2 = ((ArrayList)localObject2).iterator();
        GoogleApiClient.ConnectionCallbacks localConnectionCallbacks;
        if (((Iterator)localObject2).hasNext())
        {
          localConnectionCallbacks = (GoogleApiClient.ConnectionCallbacks)((Iterator)localObject2).next();
          if ((this.Cw) && (this.Cs.isConnected()) && (this.Cx.get() == i)) {}
        }
        else
        {
          this.Cu.clear();
          this.Cy = false;
          return;
        }
        if (this.Cu.contains(localConnectionCallbacks)) {
          continue;
        }
        localConnectionCallbacks.onConnected(paramBundle);
      }
      bool1 = false;
      break;
      label206:
      bool1 = false;
      continue;
      label211:
      bool1 = false;
    }
  }
  
  public static abstract interface zza
  {
    public abstract boolean isConnected();
    
    public abstract Bundle zzaoe();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/zzm.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */