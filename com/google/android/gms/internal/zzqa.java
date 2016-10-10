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

public class zzqa
  extends zzqd
{
  private final SparseArray<zza> wq = new SparseArray();
  
  private zzqa(zzrb paramzzrb)
  {
    super(paramzzrb);
    this.yY.zza("AutoManageHelper", this);
  }
  
  public static zzqa zza(zzqz paramzzqz)
  {
    paramzzqz = zzc(paramzzqz);
    zzqa localzzqa = (zzqa)paramzzqz.zza("AutoManageHelper", zzqa.class);
    if (localzzqa != null) {
      return localzzqa;
    }
    return new zzqa(paramzzqz);
  }
  
  public void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    int i = 0;
    while (i < this.wq.size())
    {
      ((zza)this.wq.valueAt(i)).dump(paramString, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
      i += 1;
    }
  }
  
  public void onStart()
  {
    super.onStart();
    boolean bool = this.mStarted;
    String str = String.valueOf(this.wq);
    Log.d("AutoManageHelper", String.valueOf(str).length() + 14 + "onStart " + bool + " " + str);
    if (!this.wy)
    {
      int i = 0;
      while (i < this.wq.size())
      {
        ((zza)this.wq.valueAt(i)).ws.connect();
        i += 1;
      }
    }
  }
  
  public void onStop()
  {
    super.onStop();
    int i = 0;
    while (i < this.wq.size())
    {
      ((zza)this.wq.valueAt(i)).ws.disconnect();
      i += 1;
    }
  }
  
  public void zza(int paramInt, GoogleApiClient paramGoogleApiClient, GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
  {
    zzac.zzb(paramGoogleApiClient, "GoogleApiClient instance cannot be null");
    if (this.wq.indexOfKey(paramInt) < 0) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      zzac.zza(bool1, 54 + "Already managing a GoogleApiClient with id " + paramInt);
      bool1 = this.mStarted;
      boolean bool2 = this.wy;
      Log.d("AutoManageHelper", 54 + "starting AutoManage for client " + paramInt + " " + bool1 + " " + bool2);
      paramOnConnectionFailedListener = new zza(paramInt, paramGoogleApiClient, paramOnConnectionFailedListener);
      this.wq.put(paramInt, paramOnConnectionFailedListener);
      if ((this.mStarted) && (!this.wy))
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
        localObject = (zza)this.wq.get(paramInt);
      } while (localObject == null);
      zzfq(paramInt);
      localObject = ((zza)localObject).wt;
    } while (localObject == null);
    ((GoogleApiClient.OnConnectionFailedListener)localObject).onConnectionFailed(paramConnectionResult);
  }
  
  protected void zzaqk()
  {
    int i = 0;
    while (i < this.wq.size())
    {
      ((zza)this.wq.valueAt(i)).ws.connect();
      i += 1;
    }
  }
  
  public void zzfq(int paramInt)
  {
    zza localzza = (zza)this.wq.get(paramInt);
    this.wq.remove(paramInt);
    if (localzza != null) {
      localzza.zzaql();
    }
  }
  
  private class zza
    implements GoogleApiClient.OnConnectionFailedListener
  {
    public final int wr;
    public final GoogleApiClient ws;
    public final GoogleApiClient.OnConnectionFailedListener wt;
    
    public zza(int paramInt, GoogleApiClient paramGoogleApiClient, GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
    {
      this.wr = paramInt;
      this.ws = paramGoogleApiClient;
      this.wt = paramOnConnectionFailedListener;
      paramGoogleApiClient.registerConnectionFailedListener(this);
    }
    
    public void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
    {
      paramPrintWriter.append(paramString).append("GoogleApiClient #").print(this.wr);
      paramPrintWriter.println(":");
      this.ws.dump(String.valueOf(paramString).concat("  "), paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    }
    
    public void onConnectionFailed(@NonNull ConnectionResult paramConnectionResult)
    {
      String str = String.valueOf(paramConnectionResult);
      Log.d("AutoManageHelper", String.valueOf(str).length() + 27 + "beginFailureResolution for " + str);
      zzqa.this.zzb(paramConnectionResult, this.wr);
    }
    
    public void zzaql()
    {
      this.ws.unregisterConnectionFailedListener(this);
      this.ws.disconnect();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzqa.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */