package com.google.android.gms.internal;

import android.support.annotation.NonNull;
import android.util.Log;
import android.util.SparseArray;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.internal.zzaa;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public class zzqm
  extends zzqp
{
  private final SparseArray<zza> yq = new SparseArray();
  
  private zzqm(zzrp paramzzrp)
  {
    super(paramzzrp);
    this.Bf.zza("AutoManageHelper", this);
  }
  
  public static zzqm zza(zzrn paramzzrn)
  {
    paramzzrn = zzc(paramzzrn);
    zzqm localzzqm = (zzqm)paramzzrn.zza("AutoManageHelper", zzqm.class);
    if (localzzqm != null) {
      return localzzqm;
    }
    return new zzqm(paramzzrn);
  }
  
  public void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    int i = 0;
    while (i < this.yq.size())
    {
      ((zza)this.yq.valueAt(i)).dump(paramString, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
      i += 1;
    }
  }
  
  public void onStart()
  {
    super.onStart();
    boolean bool = this.mStarted;
    String str = String.valueOf(this.yq);
    Log.d("AutoManageHelper", String.valueOf(str).length() + 14 + "onStart " + bool + " " + str);
    if (!this.yz)
    {
      int i = 0;
      while (i < this.yq.size())
      {
        ((zza)this.yq.valueAt(i)).ys.connect();
        i += 1;
      }
    }
  }
  
  public void onStop()
  {
    super.onStop();
    int i = 0;
    while (i < this.yq.size())
    {
      ((zza)this.yq.valueAt(i)).ys.disconnect();
      i += 1;
    }
  }
  
  public void zza(int paramInt, GoogleApiClient paramGoogleApiClient, GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
  {
    zzaa.zzb(paramGoogleApiClient, "GoogleApiClient instance cannot be null");
    if (this.yq.indexOfKey(paramInt) < 0) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      zzaa.zza(bool1, 54 + "Already managing a GoogleApiClient with id " + paramInt);
      bool1 = this.mStarted;
      boolean bool2 = this.yz;
      Log.d("AutoManageHelper", 54 + "starting AutoManage for client " + paramInt + " " + bool1 + " " + bool2);
      paramOnConnectionFailedListener = new zza(paramInt, paramGoogleApiClient, paramOnConnectionFailedListener);
      this.yq.put(paramInt, paramOnConnectionFailedListener);
      if ((this.mStarted) && (!this.yz))
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
        localObject = (zza)this.yq.get(paramInt);
      } while (localObject == null);
      zzfs(paramInt);
      localObject = ((zza)localObject).yt;
    } while (localObject == null);
    ((GoogleApiClient.OnConnectionFailedListener)localObject).onConnectionFailed(paramConnectionResult);
  }
  
  protected void zzarm()
  {
    int i = 0;
    while (i < this.yq.size())
    {
      ((zza)this.yq.valueAt(i)).ys.connect();
      i += 1;
    }
  }
  
  public void zzfs(int paramInt)
  {
    zza localzza = (zza)this.yq.get(paramInt);
    this.yq.remove(paramInt);
    if (localzza != null) {
      localzza.zzarn();
    }
  }
  
  private class zza
    implements GoogleApiClient.OnConnectionFailedListener
  {
    public final int yr;
    public final GoogleApiClient ys;
    public final GoogleApiClient.OnConnectionFailedListener yt;
    
    public zza(int paramInt, GoogleApiClient paramGoogleApiClient, GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
    {
      this.yr = paramInt;
      this.ys = paramGoogleApiClient;
      this.yt = paramOnConnectionFailedListener;
      paramGoogleApiClient.registerConnectionFailedListener(this);
    }
    
    public void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
    {
      paramPrintWriter.append(paramString).append("GoogleApiClient #").print(this.yr);
      paramPrintWriter.println(":");
      this.ys.dump(String.valueOf(paramString).concat("  "), paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    }
    
    public void onConnectionFailed(@NonNull ConnectionResult paramConnectionResult)
    {
      String str = String.valueOf(paramConnectionResult);
      Log.d("AutoManageHelper", String.valueOf(str).length() + 27 + "beginFailureResolution for " + str);
      zzqm.this.zzb(paramConnectionResult, this.yr);
    }
    
    public void zzarn()
    {
      this.ys.unregisterConnectionFailedListener(this);
      this.ys.disconnect();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzqm.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */