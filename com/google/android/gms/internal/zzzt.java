package com.google.android.gms.internal;

import android.support.annotation.NonNull;
import android.util.Log;
import android.util.SparseArray;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.internal.zzac;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public class zzzt
  extends zzzw
{
  private final SparseArray<zza> zzayx = new SparseArray();
  
  private zzzt(zzaax paramzzaax)
  {
    super(paramzzaax);
    this.zzaBs.zza("AutoManageHelper", this);
  }
  
  public static zzzt zza(zzaav paramzzaav)
  {
    paramzzaav = zzc(paramzzaav);
    zzzt localzzzt = (zzzt)paramzzaav.zza("AutoManageHelper", zzzt.class);
    if (localzzzt != null) {
      return localzzzt;
    }
    return new zzzt(paramzzaav);
  }
  
  public void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    int i = 0;
    while (i < this.zzayx.size())
    {
      ((zza)this.zzayx.valueAt(i)).dump(paramString, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
      i += 1;
    }
  }
  
  public void onStart()
  {
    super.onStart();
    boolean bool = this.mStarted;
    String str = String.valueOf(this.zzayx);
    Log.d("AutoManageHelper", String.valueOf(str).length() + 14 + "onStart " + bool + " " + str);
    if (!this.zzayG)
    {
      int i = 0;
      while (i < this.zzayx.size())
      {
        ((zza)this.zzayx.valueAt(i)).zzayz.connect();
        i += 1;
      }
    }
  }
  
  public void onStop()
  {
    super.onStop();
    int i = 0;
    while (i < this.zzayx.size())
    {
      ((zza)this.zzayx.valueAt(i)).zzayz.disconnect();
      i += 1;
    }
  }
  
  public void zza(int paramInt, GoogleApiClient paramGoogleApiClient, GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
  {
    zzac.zzb(paramGoogleApiClient, "GoogleApiClient instance cannot be null");
    if (this.zzayx.indexOfKey(paramInt) < 0) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      zzac.zza(bool1, 54 + "Already managing a GoogleApiClient with id " + paramInt);
      bool1 = this.mStarted;
      boolean bool2 = this.zzayG;
      Log.d("AutoManageHelper", 54 + "starting AutoManage for client " + paramInt + " " + bool1 + " " + bool2);
      paramOnConnectionFailedListener = new zza(paramInt, paramGoogleApiClient, paramOnConnectionFailedListener);
      this.zzayx.put(paramInt, paramOnConnectionFailedListener);
      if ((this.mStarted) && (!this.zzayG))
      {
        paramOnConnectionFailedListener = String.valueOf(paramGoogleApiClient);
        Log.d("AutoManageHelper", String.valueOf(paramOnConnectionFailedListener).length() + 11 + "connecting " + paramOnConnectionFailedListener);
        paramGoogleApiClient.connect();
      }
      return;
    }
  }
  
  protected void zza(ConnectionResult paramConnectionResult, int paramInt)
  {
    Log.w("AutoManageHelper", "Unresolved error while connecting client. Stopping auto-manage.");
    if (paramInt < 0) {
      Log.wtf("AutoManageHelper", "AutoManageLifecycleHelper received onErrorResolutionFailed callback but no failing client ID is set", new Exception());
    }
    Object localObject;
    do
    {
      do
      {
        return;
        localObject = (zza)this.zzayx.get(paramInt);
      } while (localObject == null);
      zzcu(paramInt);
      localObject = ((zza)localObject).zzayA;
    } while (localObject == null);
    ((GoogleApiClient.OnConnectionFailedListener)localObject).onConnectionFailed(paramConnectionResult);
  }
  
  public void zzcu(int paramInt)
  {
    zza localzza = (zza)this.zzayx.get(paramInt);
    this.zzayx.remove(paramInt);
    if (localzza != null) {
      localzza.zzuX();
    }
  }
  
  protected void zzuW()
  {
    int i = 0;
    while (i < this.zzayx.size())
    {
      ((zza)this.zzayx.valueAt(i)).zzayz.connect();
      i += 1;
    }
  }
  
  private class zza
    implements GoogleApiClient.OnConnectionFailedListener
  {
    public final GoogleApiClient.OnConnectionFailedListener zzayA;
    public final int zzayy;
    public final GoogleApiClient zzayz;
    
    public zza(int paramInt, GoogleApiClient paramGoogleApiClient, GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
    {
      this.zzayy = paramInt;
      this.zzayz = paramGoogleApiClient;
      this.zzayA = paramOnConnectionFailedListener;
      paramGoogleApiClient.registerConnectionFailedListener(this);
    }
    
    public void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
    {
      paramPrintWriter.append(paramString).append("GoogleApiClient #").print(this.zzayy);
      paramPrintWriter.println(":");
      this.zzayz.dump(String.valueOf(paramString).concat("  "), paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    }
    
    public void onConnectionFailed(@NonNull ConnectionResult paramConnectionResult)
    {
      String str = String.valueOf(paramConnectionResult);
      Log.d("AutoManageHelper", String.valueOf(str).length() + 27 + "beginFailureResolution for " + str);
      zzzt.this.zzb(paramConnectionResult, this.zzayy);
    }
    
    public void zzuX()
    {
      this.zzayz.unregisterConnectionFailedListener(this);
      this.zzayz.disconnect();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzzt.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */