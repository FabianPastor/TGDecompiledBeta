package com.google.android.gms.common.internal;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

public abstract class GmsClientSupervisor
{
  private static final Object zztm = new Object();
  private static GmsClientSupervisor zztn;
  
  public static GmsClientSupervisor getInstance(Context paramContext)
  {
    synchronized (zztm)
    {
      if (zztn == null)
      {
        zzh localzzh = new com/google/android/gms/common/internal/zzh;
        localzzh.<init>(paramContext.getApplicationContext());
        zztn = localzzh;
      }
      return zztn;
    }
  }
  
  protected abstract boolean bindService(ConnectionStatusConfig paramConnectionStatusConfig, ServiceConnection paramServiceConnection, String paramString);
  
  public boolean bindService(String paramString1, String paramString2, int paramInt, ServiceConnection paramServiceConnection, String paramString3)
  {
    return bindService(new ConnectionStatusConfig(paramString1, paramString2, paramInt), paramServiceConnection, paramString3);
  }
  
  protected abstract void unbindService(ConnectionStatusConfig paramConnectionStatusConfig, ServiceConnection paramServiceConnection, String paramString);
  
  public void unbindService(String paramString1, String paramString2, int paramInt, ServiceConnection paramServiceConnection, String paramString3)
  {
    unbindService(new ConnectionStatusConfig(paramString1, paramString2, paramInt), paramServiceConnection, paramString3);
  }
  
  protected static final class ConnectionStatusConfig
  {
    private final ComponentName mComponentName;
    private final String zzto;
    private final String zztp;
    private final int zztq;
    
    public ConnectionStatusConfig(String paramString1, String paramString2, int paramInt)
    {
      this.zzto = Preconditions.checkNotEmpty(paramString1);
      this.zztp = Preconditions.checkNotEmpty(paramString2);
      this.mComponentName = null;
      this.zztq = paramInt;
    }
    
    public final boolean equals(Object paramObject)
    {
      boolean bool = true;
      if (this == paramObject) {}
      for (;;)
      {
        return bool;
        if (!(paramObject instanceof ConnectionStatusConfig))
        {
          bool = false;
        }
        else
        {
          paramObject = (ConnectionStatusConfig)paramObject;
          if ((!Objects.equal(this.zzto, ((ConnectionStatusConfig)paramObject).zzto)) || (!Objects.equal(this.zztp, ((ConnectionStatusConfig)paramObject).zztp)) || (!Objects.equal(this.mComponentName, ((ConnectionStatusConfig)paramObject).mComponentName)) || (this.zztq != ((ConnectionStatusConfig)paramObject).zztq)) {
            bool = false;
          }
        }
      }
    }
    
    public final int getBindFlags()
    {
      return this.zztq;
    }
    
    public final ComponentName getComponentName()
    {
      return this.mComponentName;
    }
    
    public final String getPackage()
    {
      return this.zztp;
    }
    
    public final Intent getStartServiceIntent(Context paramContext)
    {
      if (this.zzto != null) {}
      for (paramContext = new Intent(this.zzto).setPackage(this.zztp);; paramContext = new Intent().setComponent(this.mComponentName)) {
        return paramContext;
      }
    }
    
    public final int hashCode()
    {
      return Objects.hashCode(new Object[] { this.zzto, this.zztp, this.mComponentName, Integer.valueOf(this.zztq) });
    }
    
    public final String toString()
    {
      if (this.zzto == null) {}
      for (String str = this.mComponentName.flattenToString();; str = this.zzto) {
        return str;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/GmsClientSupervisor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */