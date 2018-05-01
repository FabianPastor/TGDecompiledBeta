package com.google.android.gms.internal;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.SparseArray;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.internal.zzbo;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicReference;

public class zzbau
  extends zzbba
{
  private final SparseArray<zza> zzaBB = new SparseArray();
  
  private zzbau(zzbdt paramzzbdt)
  {
    super(paramzzbdt);
    this.zzaEG.zza("AutoManageHelper", this);
  }
  
  public static zzbau zza(zzbdr paramzzbdr)
  {
    paramzzbdr = zzb(paramzzbdr);
    zzbau localzzbau = (zzbau)paramzzbdr.zza("AutoManageHelper", zzbau.class);
    if (localzzbau != null) {
      return localzzbau;
    }
    return new zzbau(paramzzbdr);
  }
  
  @Nullable
  private final zza zzam(int paramInt)
  {
    if (this.zzaBB.size() <= paramInt) {
      return null;
    }
    return (zza)this.zzaBB.get(this.zzaBB.keyAt(paramInt));
  }
  
  public final void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    int i = 0;
    while (i < this.zzaBB.size())
    {
      zza localzza = zzam(i);
      if (localzza != null)
      {
        paramPrintWriter.append(paramString).append("GoogleApiClient #").print(localzza.zzaBC);
        paramPrintWriter.println(":");
        localzza.zzaBD.dump(String.valueOf(paramString).concat("  "), paramFileDescriptor, paramPrintWriter, paramArrayOfString);
      }
      i += 1;
    }
  }
  
  public final void onStart()
  {
    super.onStart();
    boolean bool = this.mStarted;
    Object localObject = String.valueOf(this.zzaBB);
    Log.d("AutoManageHelper", String.valueOf(localObject).length() + 14 + "onStart " + bool + " " + (String)localObject);
    if (this.zzaBN.get() == null)
    {
      int i = 0;
      while (i < this.zzaBB.size())
      {
        localObject = zzam(i);
        if (localObject != null) {
          ((zza)localObject).zzaBD.connect();
        }
        i += 1;
      }
    }
  }
  
  public final void onStop()
  {
    super.onStop();
    int i = 0;
    while (i < this.zzaBB.size())
    {
      zza localzza = zzam(i);
      if (localzza != null) {
        localzza.zzaBD.disconnect();
      }
      i += 1;
    }
  }
  
  public final void zza(int paramInt, GoogleApiClient paramGoogleApiClient, GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
  {
    zzbo.zzb(paramGoogleApiClient, "GoogleApiClient instance cannot be null");
    if (this.zzaBB.indexOfKey(paramInt) < 0) {}
    for (boolean bool = true;; bool = false)
    {
      zzbo.zza(bool, 54 + "Already managing a GoogleApiClient with id " + paramInt);
      zzbbb localzzbbb = (zzbbb)this.zzaBN.get();
      bool = this.mStarted;
      String str = String.valueOf(localzzbbb);
      Log.d("AutoManageHelper", String.valueOf(str).length() + 49 + "starting AutoManage for client " + paramInt + " " + bool + " " + str);
      paramOnConnectionFailedListener = new zza(paramInt, paramGoogleApiClient, paramOnConnectionFailedListener);
      this.zzaBB.put(paramInt, paramOnConnectionFailedListener);
      if ((this.mStarted) && (localzzbbb == null))
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
    Object localObject;
    do
    {
      do
      {
        return;
        localObject = (zza)this.zzaBB.get(paramInt);
      } while (localObject == null);
      zzal(paramInt);
      localObject = ((zza)localObject).zzaBE;
    } while (localObject == null);
    ((GoogleApiClient.OnConnectionFailedListener)localObject).onConnectionFailed(paramConnectionResult);
  }
  
  public final void zzal(int paramInt)
  {
    zza localzza = (zza)this.zzaBB.get(paramInt);
    this.zzaBB.remove(paramInt);
    if (localzza != null)
    {
      localzza.zzaBD.unregisterConnectionFailedListener(localzza);
      localzza.zzaBD.disconnect();
    }
  }
  
  protected final void zzps()
  {
    int i = 0;
    while (i < this.zzaBB.size())
    {
      zza localzza = zzam(i);
      if (localzza != null) {
        localzza.zzaBD.connect();
      }
      i += 1;
    }
  }
  
  final class zza
    implements GoogleApiClient.OnConnectionFailedListener
  {
    public final int zzaBC;
    public final GoogleApiClient zzaBD;
    public final GoogleApiClient.OnConnectionFailedListener zzaBE;
    
    public zza(int paramInt, GoogleApiClient paramGoogleApiClient, GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
    {
      this.zzaBC = paramInt;
      this.zzaBD = paramGoogleApiClient;
      this.zzaBE = paramOnConnectionFailedListener;
      paramGoogleApiClient.registerConnectionFailedListener(this);
    }
    
    public final void onConnectionFailed(@NonNull ConnectionResult paramConnectionResult)
    {
      String str = String.valueOf(paramConnectionResult);
      Log.d("AutoManageHelper", String.valueOf(str).length() + 27 + "beginFailureResolution for " + str);
      zzbau.this.zzb(paramConnectionResult, this.zzaBC);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbau.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */