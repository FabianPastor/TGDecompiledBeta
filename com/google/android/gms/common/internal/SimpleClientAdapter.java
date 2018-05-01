package com.google.android.gms.common.internal;

import android.os.IBinder;
import android.os.IInterface;
import com.google.android.gms.common.api.Api.SimpleClient;

public class SimpleClientAdapter<T extends IInterface>
  extends GmsClient<T>
{
  private final Api.SimpleClient<T> zzva;
  
  protected T createServiceInterface(IBinder paramIBinder)
  {
    return this.zzva.createServiceInterface(paramIBinder);
  }
  
  public Api.SimpleClient<T> getClient()
  {
    return this.zzva;
  }
  
  public int getMinApkVersion()
  {
    return super.getMinApkVersion();
  }
  
  protected String getServiceDescriptor()
  {
    return this.zzva.getServiceDescriptor();
  }
  
  protected String getStartServiceAction()
  {
    return this.zzva.getStartServiceAction();
  }
  
  protected void onSetConnectState(int paramInt, T paramT)
  {
    this.zzva.setState(paramInt, paramT);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/SimpleClientAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */