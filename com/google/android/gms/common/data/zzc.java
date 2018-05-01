package com.google.android.gms.common.data;

import com.google.android.gms.common.internal.zzbg;
import com.google.android.gms.common.internal.zzbq;
import java.util.Arrays;

public class zzc
{
  protected final DataHolder zzfqt;
  protected int zzfvx;
  private int zzfvy;
  
  public zzc(DataHolder paramDataHolder, int paramInt)
  {
    this.zzfqt = ((DataHolder)zzbq.checkNotNull(paramDataHolder));
    zzbx(paramInt);
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if ((paramObject instanceof zzc))
    {
      paramObject = (zzc)paramObject;
      bool1 = bool2;
      if (zzbg.equal(Integer.valueOf(((zzc)paramObject).zzfvx), Integer.valueOf(this.zzfvx)))
      {
        bool1 = bool2;
        if (zzbg.equal(Integer.valueOf(((zzc)paramObject).zzfvy), Integer.valueOf(this.zzfvy)))
        {
          bool1 = bool2;
          if (((zzc)paramObject).zzfqt == this.zzfqt) {
            bool1 = true;
          }
        }
      }
    }
    return bool1;
  }
  
  protected final byte[] getByteArray(String paramString)
  {
    return this.zzfqt.zzg(paramString, this.zzfvx, this.zzfvy);
  }
  
  protected final int getInteger(String paramString)
  {
    return this.zzfqt.zzc(paramString, this.zzfvx, this.zzfvy);
  }
  
  protected final String getString(String paramString)
  {
    return this.zzfqt.zzd(paramString, this.zzfvx, this.zzfvy);
  }
  
  public int hashCode()
  {
    return Arrays.hashCode(new Object[] { Integer.valueOf(this.zzfvx), Integer.valueOf(this.zzfvy), this.zzfqt });
  }
  
  protected final void zzbx(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt < this.zzfqt.zzfwg)) {}
    for (boolean bool = true;; bool = false)
    {
      zzbq.checkState(bool);
      this.zzfvx = paramInt;
      this.zzfvy = this.zzfqt.zzbz(this.zzfvx);
      return;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/data/zzc.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */