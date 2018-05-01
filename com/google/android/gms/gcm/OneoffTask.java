package com.google.android.gms.gcm;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable.Creator;

public class OneoffTask
  extends Task
{
  public static final Parcelable.Creator<OneoffTask> CREATOR = new zzf();
  private final long zzbfV;
  private final long zzbfW;
  
  @Deprecated
  private OneoffTask(Parcel paramParcel)
  {
    super(paramParcel);
    this.zzbfV = paramParcel.readLong();
    this.zzbfW = paramParcel.readLong();
  }
  
  private OneoffTask(Builder paramBuilder)
  {
    super(paramBuilder);
    this.zzbfV = Builder.zza(paramBuilder);
    this.zzbfW = Builder.zzb(paramBuilder);
  }
  
  public long getWindowEnd()
  {
    return this.zzbfW;
  }
  
  public long getWindowStart()
  {
    return this.zzbfV;
  }
  
  public void toBundle(Bundle paramBundle)
  {
    super.toBundle(paramBundle);
    paramBundle.putLong("window_start", this.zzbfV);
    paramBundle.putLong("window_end", this.zzbfW);
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
    paramParcel.writeLong(this.zzbfV);
    paramParcel.writeLong(this.zzbfW);
  }
  
  public static class Builder
    extends Task.Builder
  {
    private long zzbfX = -1L;
    private long zzbfY = -1L;
    
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
      if ((this.zzbfX == -1L) || (this.zzbfY == -1L)) {
        throw new IllegalArgumentException("Must specify an execution window using setExecutionWindow.");
      }
      if (this.zzbfX >= this.zzbfY) {
        throw new IllegalArgumentException("Window start must be shorter than window end.");
      }
    }
    
    public Builder setExecutionWindow(long paramLong1, long paramLong2)
    {
      this.zzbfX = paramLong1;
      this.zzbfY = paramLong2;
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