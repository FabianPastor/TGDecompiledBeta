package com.google.android.gms.gcm;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable.Creator;

public class PeriodicTask
  extends Task
{
  public static final Parcelable.Creator<PeriodicTask> CREATOR = new Parcelable.Creator()
  {
    public PeriodicTask zzmy(Parcel paramAnonymousParcel)
    {
      return new PeriodicTask(paramAnonymousParcel, null);
    }
    
    public PeriodicTask[] zztp(int paramAnonymousInt)
    {
      return new PeriodicTask[paramAnonymousInt];
    }
  };
  protected long mFlexInSeconds = -1L;
  protected long mIntervalInSeconds = -1L;
  
  @Deprecated
  private PeriodicTask(Parcel paramParcel)
  {
    super(paramParcel);
    this.mIntervalInSeconds = paramParcel.readLong();
    this.mFlexInSeconds = Math.min(paramParcel.readLong(), this.mIntervalInSeconds);
  }
  
  private PeriodicTask(Builder paramBuilder)
  {
    super(paramBuilder);
    this.mIntervalInSeconds = Builder.zza(paramBuilder);
    this.mFlexInSeconds = Math.min(Builder.zzb(paramBuilder), this.mIntervalInSeconds);
  }
  
  public long getFlex()
  {
    return this.mFlexInSeconds;
  }
  
  public long getPeriod()
  {
    return this.mIntervalInSeconds;
  }
  
  public void toBundle(Bundle paramBundle)
  {
    super.toBundle(paramBundle);
    paramBundle.putLong("period", this.mIntervalInSeconds);
    paramBundle.putLong("period_flex", this.mFlexInSeconds);
  }
  
  public String toString()
  {
    String str = String.valueOf(super.toString());
    long l1 = getPeriod();
    long l2 = getFlex();
    return String.valueOf(str).length() + 54 + str + " period=" + l1 + " flex=" + l2;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    super.writeToParcel(paramParcel, paramInt);
    paramParcel.writeLong(this.mIntervalInSeconds);
    paramParcel.writeLong(this.mFlexInSeconds);
  }
  
  public static class Builder
    extends Task.Builder
  {
    private long afd = -1L;
    private long afe = -1L;
    
    public Builder()
    {
      this.isPersisted = true;
    }
    
    public PeriodicTask build()
    {
      checkConditions();
      return new PeriodicTask(this, null);
    }
    
    protected void checkConditions()
    {
      super.checkConditions();
      if (this.afd == -1L) {
        throw new IllegalArgumentException("Must call setPeriod(long) to establish an execution interval for this periodic task.");
      }
      if (this.afd <= 0L)
      {
        long l = this.afd;
        throw new IllegalArgumentException(66 + "Period set cannot be less than or equal to 0: " + l);
      }
      if (this.afe == -1L) {
        this.afe = (((float)this.afd * 0.1F));
      }
      while (this.afe <= this.afd) {
        return;
      }
      this.afe = this.afd;
    }
    
    public Builder setExtras(Bundle paramBundle)
    {
      this.extras = paramBundle;
      return this;
    }
    
    public Builder setFlex(long paramLong)
    {
      this.afe = paramLong;
      return this;
    }
    
    public Builder setPeriod(long paramLong)
    {
      this.afd = paramLong;
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


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/gcm/PeriodicTask.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */