package com.google.android.gms.common.api.internal;

import android.util.Log;
import android.util.SparseArray;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.internal.Preconditions;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicReference;

public class zzi
  extends zzk
{
  private final SparseArray<zza> zzed = new SparseArray();
  
  private zzi(LifecycleFragment paramLifecycleFragment)
  {
    super(paramLifecycleFragment);
    this.mLifecycleFragment.addCallback("AutoManageHelper", this);
  }
  
  public static zzi zza(LifecycleActivity paramLifecycleActivity)
  {
    LifecycleFragment localLifecycleFragment = getFragment(paramLifecycleActivity);
    paramLifecycleActivity = (zzi)localLifecycleFragment.getCallbackOrNull("AutoManageHelper", zzi.class);
    if (paramLifecycleActivity != null) {}
    for (;;)
    {
      return paramLifecycleActivity;
      paramLifecycleActivity = new zzi(localLifecycleFragment);
    }
  }
  
  private final zza zzd(int paramInt)
  {
    if (this.zzed.size() <= paramInt) {}
    for (zza localzza = null;; localzza = (zza)this.zzed.get(this.zzed.keyAt(paramInt))) {
      return localzza;
    }
  }
  
  public final void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    for (int i = 0; i < this.zzed.size(); i++)
    {
      zza localzza = zzd(i);
      if (localzza != null)
      {
        paramPrintWriter.append(paramString).append("GoogleApiClient #").print(localzza.zzee);
        paramPrintWriter.println(":");
        localzza.zzef.dump(String.valueOf(paramString).concat("  "), paramFileDescriptor, paramPrintWriter, paramArrayOfString);
      }
    }
  }
  
  public final void onStart()
  {
    super.onStart();
    boolean bool = this.mStarted;
    Object localObject = String.valueOf(this.zzed);
    Log.d("AutoManageHelper", String.valueOf(localObject).length() + 14 + "onStart " + bool + " " + (String)localObject);
    if (this.zzer.get() == null) {
      for (int i = 0; i < this.zzed.size(); i++)
      {
        localObject = zzd(i);
        if (localObject != null) {
          ((zza)localObject).zzef.connect();
        }
      }
    }
  }
  
  public void onStop()
  {
    super.onStop();
    for (int i = 0; i < this.zzed.size(); i++)
    {
      zza localzza = zzd(i);
      if (localzza != null) {
        localzza.zzef.disconnect();
      }
    }
  }
  
  public final void zza(int paramInt, GoogleApiClient paramGoogleApiClient, GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
  {
    Preconditions.checkNotNull(paramGoogleApiClient, "GoogleApiClient instance cannot be null");
    if (this.zzed.indexOfKey(paramInt) < 0) {}
    for (boolean bool = true;; bool = false)
    {
      Preconditions.checkState(bool, 54 + "Already managing a GoogleApiClient with id " + paramInt);
      zzl localzzl = (zzl)this.zzer.get();
      bool = this.mStarted;
      String str = String.valueOf(localzzl);
      Log.d("AutoManageHelper", String.valueOf(str).length() + 49 + "starting AutoManage for client " + paramInt + " " + bool + " " + str);
      paramOnConnectionFailedListener = new zza(paramInt, paramGoogleApiClient, paramOnConnectionFailedListener);
      this.zzed.put(paramInt, paramOnConnectionFailedListener);
      if ((this.mStarted) && (localzzl == null))
      {
        paramOnConnectionFailedListener = String.valueOf(paramGoogleApiClient);
        Log.d("AutoManageHelper", String.valueOf(paramOnConnectionFailedListener).length() + 11 + "connecting " + paramOnConnectionFailedListener);
        paramGoogleApiClient.connect();
      }
      return;
    }
  }
  
  protected final void zza(ConnectionResult paramConnectionResult, int paramInt)
  {
    Log.w("AutoManageHelper", "Unresolved error while connecting client. Stopping auto-manage.");
    if (paramInt < 0) {
      Log.wtf("AutoManageHelper", "AutoManageLifecycleHelper received onErrorResolutionFailed callback but no failing client ID is set", new Exception());
    }
    for (;;)
    {
      return;
      Object localObject = (zza)this.zzed.get(paramInt);
      if (localObject != null)
      {
        zzc(paramInt);
        localObject = ((zza)localObject).zzeg;
        if (localObject != null) {
          ((GoogleApiClient.OnConnectionFailedListener)localObject).onConnectionFailed(paramConnectionResult);
        }
      }
    }
  }
  
  public final void zzc(int paramInt)
  {
    zza localzza = (zza)this.zzed.get(paramInt);
    this.zzed.remove(paramInt);
    if (localzza != null)
    {
      localzza.zzef.unregisterConnectionFailedListener(localzza);
      localzza.zzef.disconnect();
    }
  }
  
  protected final void zzr()
  {
    for (int i = 0; i < this.zzed.size(); i++)
    {
      zza localzza = zzd(i);
      if (localzza != null) {
        localzza.zzef.connect();
      }
    }
  }
  
  private final class zza
    implements GoogleApiClient.OnConnectionFailedListener
  {
    public final int zzee;
    public final GoogleApiClient zzef;
    public final GoogleApiClient.OnConnectionFailedListener zzeg;
    
    public zza(int paramInt, GoogleApiClient paramGoogleApiClient, GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
    {
      this.zzee = paramInt;
      this.zzef = paramGoogleApiClient;
      this.zzeg = paramOnConnectionFailedListener;
      paramGoogleApiClient.registerConnectionFailedListener(this);
    }
    
    public final void onConnectionFailed(ConnectionResult paramConnectionResult)
    {
      String str = String.valueOf(paramConnectionResult);
      Log.d("AutoManageHelper", String.valueOf(str).length() + 27 + "beginFailureResolution for " + str);
      zzi.this.zzb(paramConnectionResult, this.zzee);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/zzi.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */