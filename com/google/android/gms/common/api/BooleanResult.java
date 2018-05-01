package com.google.android.gms.common.api;

import com.google.android.gms.common.internal.Preconditions;

public class BooleanResult
  implements Result
{
  private final Status mStatus;
  private final boolean zzck;
  
  public BooleanResult(Status paramStatus, boolean paramBoolean)
  {
    this.mStatus = ((Status)Preconditions.checkNotNull(paramStatus, "Status must not be null"));
    this.zzck = paramBoolean;
  }
  
  public final boolean equals(Object paramObject)
  {
    boolean bool = true;
    if (paramObject == this) {}
    for (;;)
    {
      return bool;
      if (!(paramObject instanceof BooleanResult))
      {
        bool = false;
      }
      else
      {
        paramObject = (BooleanResult)paramObject;
        if ((!this.mStatus.equals(((BooleanResult)paramObject).mStatus)) || (this.zzck != ((BooleanResult)paramObject).zzck)) {
          bool = false;
        }
      }
    }
  }
  
  public Status getStatus()
  {
    return this.mStatus;
  }
  
  public boolean getValue()
  {
    return this.zzck;
  }
  
  public final int hashCode()
  {
    int i = this.mStatus.hashCode();
    if (this.zzck) {}
    for (int j = 1;; j = 0) {
      return j + (i + 527) * 31;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/BooleanResult.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */