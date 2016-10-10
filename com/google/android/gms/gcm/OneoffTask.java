package com.google.android.gms.gcm;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable.Creator;

public class OneoffTask
  extends Task
{
  public static final Parcelable.Creator<OneoffTask> CREATOR = new Parcelable.Creator()
  {
    public OneoffTask zzmw(Parcel paramAnonymousParcel)
    {
      return new OneoffTask(paramAnonymousParcel, null);
    }
    
    public OneoffTask[] zztn(int paramAnonymousInt)
    {
      return new OneoffTask[paramAnonymousInt];
    }
  };
  private final long aeZ;
  private final long afa;
  
  @Deprecated
  private OneoffTask(Parcel paramParcel)
  {
    super(paramParcel);
    this.aeZ = paramParcel.readLong();
    this.afa = paramParcel.readLong();
  }
  
  private OneoffTask(Builder paramBuilder)
  {
    super(paramBuilder);
    this.aeZ = Builder.zza(paramBuilder);
    this.afa = Builder.zzb(paramBuilder);
  }
  
  public long getWindowEnd()
  {
    return this.afa;
  }
  
  public long getWindowStart()
  {
    return this.aeZ;
  }
  
  public void toBundle(Bundle paramBundle)
  {
    super.toBundle(paramBundle);
    paramBundle.putLong("window_start", this.aeZ);
    paramBundle.putLong("window_end", this.afa);
  }
  
  public String toString()
  {
    String str = String.valueOf(super.toString());
    long l1 = getWindowStart();
    long l2 = getWindowEnd();
    return String.valueOf(str).length() + 64 + str + " windowStart=" + l1 + " windowEnd=" + l2;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    super.writeToParcel(paramParcel, paramInt);
    paramParcel.writeLong(this.aeZ);
    paramParcel.writeLong(this.afa);
  }
  
  public static class Builder
    extends Task.Builder
  {
    private long afb = -1L;
    private long afc = -1L;
    
    public Builder()
    {
      this.isPersisted = false;
    }
    
    public OneoffTask build()
    {
      checkConditions();
      return new OneoffTask(this, null);
    }
    
    protected void checkConditions()
    {
      super.checkConditions();
      if ((this.afb == -1L) || (this.afc == -1L)) {
        throw new IllegalArgumentException("Must specify an execution window using setExecutionWindow.");
      }
      if (this.afb >= this.afc) {
        throw new IllegalArgumentException("Window start must be shorter than window end.");
      }
    }
    
    public Builder setExecutionWindow(long paramLong1, long paramLong2)
    {
      this.afb = paramLong1;
      this.afc = paramLong2;
      return this;
    }
    
    public Builder setExtras(Bundle paramBundle)
    {
      this.extras = paramBundle;
      return this;
    }
    
    public Builder setPersisted(boolean paramBoolean)
    {
      this.isPersisted = paramBoolean;
      return this;
    }
    
    public Builder setRequiredNetwork(int paramInt)
    {
      this.requiredNetworkState = paramInt;
      return this;
    }
    
    public Builder setRequiresCharging(boolean paramBoolean)
    {
      this.requiresCharging = paramBoolean;
      return this;
    }
    
    public Builder setService(Class<? extends GcmTaskService> paramClass)
    {
      this.gcmTaskService = paramClass.getName();
      return this;
    }
    
    public Builder setTag(String paramString)
    {
      this.tag = paramString;
      return this;
    }
    
    public Builder setUpdateCurrent(boolean paramBoolean)
    {
      this.updateCurrent = paramBoolean;
      return this;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/gcm/OneoffTask.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */