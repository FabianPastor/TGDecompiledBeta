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

public class zzaaa
  extends zzaae
{
  private final SparseArray<zza> zzazN = new SparseArray();
  
  private zzaaa(zzabf paramzzabf)
  {
    super(paramzzabf);
    this.zzaCR.zza("AutoManageHelper", this);
  }
  
  public static zzaaa zza(zzabd paramzzabd)
  {
    paramzzabd = zzc(paramzzabd);
    zzaaa localzzaaa = (zzaaa)paramzzabd.zza("AutoManageHelper", zzaaa.class);
    if (localzzaaa != null) {
      return localzzaaa;
    }
    return new zzaaa(paramzzabd);
  }
  
  public void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    int i = 0;
    while (i < this.zzazN.size())
    {
      ((zza)this.zzazN.valueAt(i)).dump(paramString, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
      i += 1;
    }
  }
  
  public void onStart()
  {
    super.onStart();
    boolean bool = this.mStarted;
    String str = String.valueOf(this.zzazN);
    Log.d("AutoManageHelper", String.valueOf(str).length() + 14 + "onStart " + bool + " " + str);
    if (!this.zzazZ)
    {
      int i = 0;
      while (i < this.zzazN.size())
      {
        ((zza)this.zzazN.valueAt(i)).zzazP.connect();
        i += 1;
      }
    }
  }
  
  public void onStop()
  {
    super.onStop();
    int i = 0;
    while (i < this.zzazN.size())
    {
      ((zza)this.zzazN.valueAt(i)).zzazP.disconnect();
      i += 1;
    }
  }
  
  public void zza(int paramInt, GoogleApiClient paramGoogleApiClient, GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
  {
    zzac.zzb(paramGoogleApiClient, "GoogleApiClient instance cannot be null");
    if (this.zzazN.indexOfKey(paramInt) < 0) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      zzac.zza(bool1, 54 + "Already managing a GoogleApiClient with id " + paramInt);
      bool1 = this.mStarted;
      boolean bool2 = this.zzazZ;
      Log.d("AutoManageHelper", 54 + "starting AutoManage for client " + paramInt + " " + bool1 + " " + bool2);
      paramOnConnectionFailedListener = new zza(paramInt, paramGoogleApiClient, paramOnConnectionFailedListener);
      this.zzazN.put(paramInt, paramOnConnectionFailedListener);
      if ((this.mStarted) && (!this.zzazZ))
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
        localObject = (zza)this.zzazN.get(paramInt);
      } while (localObject == null);
      zzcA(paramInt);
      localObject = ((zza)localObject).zzazQ;
    } while (localObject == null);
    ((GoogleApiClient.OnConnectionFailedListener)localObject).onConnectionFailed(paramConnectionResult);
  }
  
  public void zzcA(int paramInt)
  {
    zza localzza = (zza)this.zzazN.get(paramInt);
    this.zzazN.remove(paramInt);
    if (localzza != null) {
      localzza.zzvy();
    }
  }
  
  protected void zzvx()
  {
    int i = 0;
    while (i < this.zzazN.size())
    {
      ((zza)this.zzazN.valueAt(i)).zzazP.connect();
      i += 1;
    }
  }
  
  private class zza
    implements GoogleApiClient.OnConnectionFailedListener
  {
    public final int zzazO;
    public final GoogleApiClient zzazP;
    public final GoogleApiClient.OnConnectionFailedListener zzazQ;
    
    public zza(int paramInt, GoogleApiClient paramGoogleApiClient, GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
    {
      this.zzazO = paramInt;
      this.zzazP = paramGoogleApiClient;
      this.zzazQ = paramOnConnectionFailedListener;
      paramGoogleApiClient.registerConnectionFailedListener(this);
    }
    
    public void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
    {
      paramPrintWriter.append(paramString).append("GoogleApiClient #").print(this.zzazO);
      paramPrintWriter.println(":");
      this.zzazP.dump(String.valueOf(paramString).concat("  "), paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    }
    
    public void onConnectionFailed(@NonNull ConnectionResult paramConnectionResult)
    {
      String str = String.valueOf(paramConnectionResult);
      Log.d("AutoManageHelper", String.valueOf(str).length() + 27 + "beginFailureResolution for " + str);
      zzaaa.this.zzb(paramConnectionResult, this.zzazO);
    }
    
    public void zzvy()
    {
      this.zzazP.unregisterConnectionFailedListener(this);
      this.zzazP.disconnect();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzaaa.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */