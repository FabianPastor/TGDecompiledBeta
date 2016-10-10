package com.google.android.gms.common.data;

import android.database.CharArrayBuffer;
import android.net.Uri;
import com.google.android.gms.common.internal.zzab;
import com.google.android.gms.common.internal.zzac;

public abstract class zzc
{
  protected final DataHolder xi;
  protected int zK;
  private int zL;
  
  public zzc(DataHolder paramDataHolder, int paramInt)
  {
    this.xi = ((DataHolder)zzac.zzy(paramDataHolder));
    zzfz(paramInt);
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if ((paramObject instanceof zzc))
    {
      paramObject = (zzc)paramObject;
      bool1 = bool2;
      if (zzab.equal(Integer.valueOf(((zzc)paramObject).zK), Integer.valueOf(this.zK)))
      {
        bool1 = bool2;
        if (zzab.equal(Integer.valueOf(((zzc)paramObject).zL), Integer.valueOf(this.zL)))
        {
          bool1 = bool2;
          if (((zzc)paramObject).xi == this.xi) {
            bool1 = true;
          }
        }
      }
    }
    return bool1;
  }
  
  protected boolean getBoolean(String paramString)
  {
    return this.xi.zze(paramString, this.zK, this.zL);
  }
  
  protected byte[] getByteArray(String paramString)
  {
    return this.xi.zzg(paramString, this.zK, this.zL);
  }
  
  protected float getFloat(String paramString)
  {
    return this.xi.zzf(paramString, this.zK, this.zL);
  }
  
  protected int getInteger(String paramString)
  {
    return this.xi.zzc(paramString, this.zK, this.zL);
  }
  
  protected long getLong(String paramString)
  {
    return this.xi.zzb(paramString, this.zK, this.zL);
  }
  
  protected String getString(String paramString)
  {
    return this.xi.zzd(paramString, this.zK, this.zL);
  }
  
  public int hashCode()
  {
    return zzab.hashCode(new Object[] { Integer.valueOf(this.zK), Integer.valueOf(this.zL), this.xi });
  }
  
  public boolean isDataValid()
  {
    return !this.xi.isClosed();
  }
  
  protected void zza(String paramString, CharArrayBuffer paramCharArrayBuffer)
  {
    this.xi.zza(paramString, this.zK, this.zL, paramCharArrayBuffer);
  }
  
  protected int zzatc()
  {
    return this.zK;
  }
  
  protected void zzfz(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt < this.xi.getCount())) {}
    for (boolean bool = true;; bool = false)
    {
      zzac.zzbr(bool);
      this.zK = paramInt;
      this.zL = this.xi.zzgb(this.zK);
      return;
    }
  }
  
  public boolean zzhm(String paramString)
  {
    return this.xi.zzhm(paramString);
  }
  
  protected Uri zzhn(String paramString)
  {
    return this.xi.zzh(paramString, this.zK, this.zL);
  }
  
  protected boolean zzho(String paramString)
  {
    return this.xi.zzi(paramString, this.zK, this.zL);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/data/zzc.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */