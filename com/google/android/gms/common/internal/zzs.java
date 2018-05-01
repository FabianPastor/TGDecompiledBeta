package com.google.android.gms.common.internal;

import android.accounts.Account;
import android.support.v4.util.ArraySet;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.internal.zzcxe;
import java.util.Collection;

public final class zzs
{
  private String zzebs;
  private Account zzebz;
  private int zzfmq = 0;
  private String zzfms;
  private zzcxe zzfzi = zzcxe.zzkbs;
  private ArraySet<Scope> zzfzk;
  
  public final zzr zzald()
  {
    return new zzr(this.zzebz, this.zzfzk, null, 0, null, this.zzebs, this.zzfms, this.zzfzi);
  }
  
  public final zzs zze(Account paramAccount)
  {
    this.zzebz = paramAccount;
    return this;
  }
  
  public final zzs zze(Collection<Scope> paramCollection)
  {
    if (this.zzfzk == null) {
      this.zzfzk = new ArraySet();
    }
    this.zzfzk.addAll(paramCollection);
    return this;
  }
  
  public final zzs zzgf(String paramString)
  {
    this.zzebs = paramString;
    return this;
  }
  
  public final zzs zzgg(String paramString)
  {
    this.zzfms = paramString;
    return this;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/zzs.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */