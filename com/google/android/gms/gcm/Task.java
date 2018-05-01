package com.google.android.gms.gcm;

import android.os.Bundle;
import android.os.Parcel;
import android.support.annotation.CallSuper;
import android.util.Log;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.zzbo;
import java.util.Iterator;
import java.util.Set;

public class Task
  implements ReflectedParcelable
{
  public static final int EXTRAS_LIMIT_BYTES = 10240;
  public static final int NETWORK_STATE_ANY = 2;
  public static final int NETWORK_STATE_CONNECTED = 0;
  public static final int NETWORK_STATE_UNMETERED = 1;
  protected static final long UNINITIALIZED = -1L;
  private final Bundle mExtras;
  private final String mTag;
  private final String zzbgg;
  private final boolean zzbgh;
  private final boolean zzbgi;
  private final int zzbgj;
  private final boolean zzbgk;
  private final boolean zzbgl;
  private final zzi zzbgm;
  
  @Deprecated
  Task(Parcel paramParcel)
  {
    Log.e("Task", "Constructing a Task object using a parcel.");
    this.zzbgg = paramParcel.readString();
    this.mTag = paramParcel.readString();
    if (paramParcel.readInt() == 1)
    {
      bool1 = true;
      this.zzbgh = bool1;
      if (paramParcel.readInt() != 1) {
        break label93;
      }
    }
    label93:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      this.zzbgi = bool1;
      this.zzbgj = 2;
      this.zzbgk = false;
      this.zzbgl = false;
      this.zzbgm = zzi.zzbgb;
      this.mExtras = null;
      return;
      bool1 = false;
      break;
    }
  }
  
  Task(Builder paramBuilder)
  {
    this.zzbgg = paramBuilder.gcmTaskService;
    this.mTag = paramBuilder.tag;
    this.zzbgh = paramBuilder.updateCurrent;
    this.zzbgi = paramBuilder.isPersisted;
    this.zzbgj = paramBuilder.requiredNetworkState;
    this.zzbgk = paramBuilder.requiresCharging;
    this.zzbgl = false;
    this.mExtras = paramBuilder.extras;
    if (paramBuilder.zzbgn != null) {}
    for (paramBuilder = paramBuilder.zzbgn;; paramBuilder = zzi.zzbgb)
    {
      this.zzbgm = paramBuilder;
      return;
    }
  }
  
  public static void zzy(Bundle paramBundle)
  {
    if (paramBundle != null)
    {
      Object localObject1 = Parcel.obtain();
      paramBundle.writeToParcel((Parcel)localObject1, 0);
      int i = ((Parcel)localObject1).dataSize();
      if (i > 10240)
      {
        ((Parcel)localObject1).recycle();
        paramBundle = String.valueOf("Extras exceeding maximum size(10240 bytes): ");
        throw new IllegalArgumentException(String.valueOf(paramBundle).length() + 11 + paramBundle + i);
      }
      ((Parcel)localObject1).recycle();
      localObject1 = paramBundle.keySet().iterator();
      while (((Iterator)localObject1).hasNext())
      {
        Object localObject2 = paramBundle.get((String)((Iterator)localObject1).next());
        if (((localObject2 instanceof Integer)) || ((localObject2 instanceof Long)) || ((localObject2 instanceof Double)) || ((localObject2 instanceof String)) || ((localObject2 instanceof Boolean))) {}
        for (i = 1;; i = 0)
        {
          if (i != 0) {
            break label170;
          }
          if (!(localObject2 instanceof Bundle)) {
            break label172;
          }
          zzy((Bundle)localObject2);
          break;
        }
        label170:
        continue;
        label172:
        throw new IllegalArgumentException("Only the following extra parameter types are supported: Integer, Long, Double, String, Boolean, and nested Bundles with the same restrictions.");
      }
    }
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public Bundle getExtras()
  {
    return this.mExtras;
  }
  
  public int getRequiredNetwork()
  {
    return this.zzbgj;
  }
  
  public boolean getRequiresCharging()
  {
    return this.zzbgk;
  }
  
  public String getServiceName()
  {
    return this.zzbgg;
  }
  
  public String getTag()
  {
    return this.mTag;
  }
  
  public boolean isPersisted()
  {
    return this.zzbgi;
  }
  
  public boolean isUpdateCurrent()
  {
    return this.zzbgh;
  }
  
  public void toBundle(Bundle paramBundle)
  {
    paramBundle.putString("tag", this.mTag);
    paramBundle.putBoolean("update_current", this.zzbgh);
    paramBundle.putBoolean("persisted", this.zzbgi);
    paramBundle.putString("service", this.zzbgg);
    paramBundle.putInt("requiredNetwork", this.zzbgj);
    paramBundle.putBoolean("requiresCharging", this.zzbgk);
    paramBundle.putBoolean("requiresIdle", false);
    paramBundle.putBundle("retryStrategy", this.zzbgm.zzx(new Bundle()));
    paramBundle.putBundle("extras", this.mExtras);
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = 1;
    paramParcel.writeString(this.zzbgg);
    paramParcel.writeString(this.mTag);
    if (this.zzbgh)
    {
      paramInt = 1;
      paramParcel.writeInt(paramInt);
      if (!this.zzbgi) {
        break label52;
      }
    }
    label52:
    for (paramInt = i;; paramInt = 0)
    {
      paramParcel.writeInt(paramInt);
      return;
      paramInt = 0;
      break;
    }
  }
  
  public static abstract class Builder
  {
    protected Bundle extras;
    protected String gcmTaskService;
    protected boolean isPersisted;
    protected int requiredNetworkState;
    protected boolean requiresCharging;
    protected String tag;
    protected boolean updateCurrent;
    protected zzi zzbgn = zzi.zzbgb;
    
    public abstract Task build();
    
    @CallSuper
    protected void checkConditions()
    {
      if (this.gcmTaskService != null) {}
      zzi localzzi;
      int i;
      for (boolean bool = true;; bool = false)
      {
        zzbo.zzb(bool, "Must provide an endpoint for this task by calling setService(ComponentName).");
        GcmNetworkManager.zzdn(this.tag);
        localzzi = this.zzbgn;
        if (localzzi == null) {
          break label195;
        }
        i = localzzi.zzvE();
        if ((i == 1) || (i == 0)) {
          break;
        }
        throw new IllegalArgumentException(45 + "Must provide a valid RetryPolicy: " + i);
      }
      int j = localzzi.zzvF();
      int k = localzzi.zzvG();
      if ((i == 0) && (j < 0)) {
        throw new IllegalArgumentException(52 + "InitialBackoffSeconds can't be negative: " + j);
      }
      if ((i == 1) && (j < 10)) {
        throw new IllegalArgumentException("RETRY_POLICY_LINEAR must have an initial backoff at least 10 seconds.");
      }
      if (k < j)
      {
        i = localzzi.zzvG();
        throw new IllegalArgumentException(77 + "MaximumBackoffSeconds must be greater than InitialBackoffSeconds: " + i);
      }
      label195:
      if (this.isPersisted) {
        Task.zzy(this.extras);
      }
    }
    
    public abstract Builder setExtras(Bundle paramBundle);
    
    public abstract Builder setPersisted(boolean paramBoolean);
    
    public abstract Builder setRequiredNetwork(int paramInt);
    
    public abstract Builder setRequiresCharging(boolean paramBoolean);
    
    public abstract Builder setService(Class<? extends GcmTaskService> paramClass);
    
    public abstract Builder setTag(String paramString);
    
    public abstract Builder setUpdateCurrent(boolean paramBoolean);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/gcm/Task.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */