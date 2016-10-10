package com.google.android.gms.gcm;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.CallSuper;
import android.util.Log;
import com.google.android.gms.common.internal.zzac;
import java.util.Iterator;
import java.util.Set;

public abstract class Task
  implements Parcelable
{
  public static final int EXTRAS_LIMIT_BYTES = 10240;
  public static final int NETWORK_STATE_ANY = 2;
  public static final int NETWORK_STATE_CONNECTED = 0;
  public static final int NETWORK_STATE_UNMETERED = 1;
  protected static final long UNINITIALIZED = -1L;
  private final String afk;
  private final boolean afl;
  private final boolean afm;
  private final int afn;
  private final boolean afo;
  private final zzc afp;
  private final Bundle mExtras;
  private final String mTag;
  
  @Deprecated
  Task(Parcel paramParcel)
  {
    Log.e("Task", "Constructing a Task object using a parcel.");
    this.afk = paramParcel.readString();
    this.mTag = paramParcel.readString();
    if (paramParcel.readInt() == 1)
    {
      bool1 = true;
      this.afl = bool1;
      if (paramParcel.readInt() != 1) {
        break label88;
      }
    }
    label88:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      this.afm = bool1;
      this.afn = 2;
      this.afo = false;
      this.afp = zzc.aff;
      this.mExtras = null;
      return;
      bool1 = false;
      break;
    }
  }
  
  Task(Builder paramBuilder)
  {
    this.afk = paramBuilder.gcmTaskService;
    this.mTag = paramBuilder.tag;
    this.afl = paramBuilder.updateCurrent;
    this.afm = paramBuilder.isPersisted;
    this.afn = paramBuilder.requiredNetworkState;
    this.afo = paramBuilder.requiresCharging;
    this.mExtras = paramBuilder.extras;
    if (paramBuilder.afq != null) {}
    for (paramBuilder = paramBuilder.afq;; paramBuilder = zzc.aff)
    {
      this.afp = paramBuilder;
      return;
    }
  }
  
  public static void zza(zzc paramzzc)
  {
    if (paramzzc != null)
    {
      int i = paramzzc.zzboc();
      if ((i != 1) && (i != 0)) {
        throw new IllegalArgumentException(45 + "Must provide a valid RetryPolicy: " + i);
      }
      int j = paramzzc.zzbod();
      int k = paramzzc.zzboe();
      if ((i == 0) && (j < 0)) {
        throw new IllegalArgumentException(52 + "InitialBackoffSeconds can't be negative: " + j);
      }
      if ((i == 1) && (j < 10)) {
        throw new IllegalArgumentException("RETRY_POLICY_LINEAR must have an initial backoff at least 10 seconds.");
      }
      if (k < j)
      {
        i = paramzzc.zzboe();
        throw new IllegalArgumentException(77 + "MaximumBackoffSeconds must be greater than InitialBackoffSeconds: " + i);
      }
    }
  }
  
  private static boolean zzah(Object paramObject)
  {
    return ((paramObject instanceof Integer)) || ((paramObject instanceof Long)) || ((paramObject instanceof Double)) || ((paramObject instanceof String)) || ((paramObject instanceof Boolean));
  }
  
  public static void zzak(Bundle paramBundle)
  {
    if (paramBundle != null)
    {
      Object localObject = Parcel.obtain();
      paramBundle.writeToParcel((Parcel)localObject, 0);
      int i = ((Parcel)localObject).dataSize();
      if (i > 10240)
      {
        ((Parcel)localObject).recycle();
        paramBundle = String.valueOf("Extras exceeding maximum size(10240 bytes): ");
        throw new IllegalArgumentException(String.valueOf(paramBundle).length() + 11 + paramBundle + i);
      }
      ((Parcel)localObject).recycle();
      localObject = paramBundle.keySet().iterator();
      while (((Iterator)localObject).hasNext()) {
        if (!zzah(paramBundle.get((String)((Iterator)localObject).next()))) {
          throw new IllegalArgumentException("Only the following extra parameter types are supported: Integer, Long, Double, String, and Boolean. ");
        }
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
    return this.afn;
  }
  
  public boolean getRequiresCharging()
  {
    return this.afo;
  }
  
  public String getServiceName()
  {
    return this.afk;
  }
  
  public String getTag()
  {
    return this.mTag;
  }
  
  public boolean isPersisted()
  {
    return this.afm;
  }
  
  public boolean isUpdateCurrent()
  {
    return this.afl;
  }
  
  public void toBundle(Bundle paramBundle)
  {
    paramBundle.putString("tag", this.mTag);
    paramBundle.putBoolean("update_current", this.afl);
    paramBundle.putBoolean("persisted", this.afm);
    paramBundle.putString("service", this.afk);
    paramBundle.putInt("requiredNetwork", this.afn);
    paramBundle.putBoolean("requiresCharging", this.afo);
    paramBundle.putBundle("retryStrategy", this.afp.zzaj(new Bundle()));
    paramBundle.putBundle("extras", this.mExtras);
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = 1;
    paramParcel.writeString(this.afk);
    paramParcel.writeString(this.mTag);
    if (this.afl)
    {
      paramInt = 1;
      paramParcel.writeInt(paramInt);
      if (!this.afm) {
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
    protected zzc afq = zzc.aff;
    protected Bundle extras;
    protected String gcmTaskService;
    protected boolean isPersisted;
    protected int requiredNetworkState;
    protected boolean requiresCharging;
    protected String tag;
    protected boolean updateCurrent;
    
    public abstract Task build();
    
    @CallSuper
    protected void checkConditions()
    {
      if (this.gcmTaskService != null) {}
      for (boolean bool = true;; bool = false)
      {
        zzac.zzb(bool, "Must provide an endpoint for this task by calling setService(ComponentName).");
        GcmNetworkManager.zzki(this.tag);
        Task.zza(this.afq);
        if (this.isPersisted) {
          Task.zzak(this.extras);
        }
        return;
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