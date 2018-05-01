package com.google.android.gms.common.internal;

import android.accounts.Account;
import android.view.View;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.internal.zzcxe;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class zzr
{
  private final String zzebs;
  private final Account zzebz;
  private final Set<Scope> zzfmo;
  private final int zzfmq;
  private final View zzfmr;
  private final String zzfms;
  private final Set<Scope> zzfzg;
  private final Map<Api<?>, zzt> zzfzh;
  private final zzcxe zzfzi;
  private Integer zzfzj;
  
  public zzr(Account paramAccount, Set<Scope> paramSet, Map<Api<?>, zzt> paramMap, int paramInt, View paramView, String paramString1, String paramString2, zzcxe paramzzcxe)
  {
    this.zzebz = paramAccount;
    if (paramSet == null) {}
    for (paramAccount = Collections.EMPTY_SET;; paramAccount = Collections.unmodifiableSet(paramSet))
    {
      this.zzfmo = paramAccount;
      paramAccount = paramMap;
      if (paramMap == null) {
        paramAccount = Collections.EMPTY_MAP;
      }
      this.zzfzh = paramAccount;
      this.zzfmr = paramView;
      this.zzfmq = paramInt;
      this.zzebs = paramString1;
      this.zzfms = paramString2;
      this.zzfzi = paramzzcxe;
      paramAccount = new HashSet(this.zzfmo);
      paramSet = this.zzfzh.values().iterator();
      while (paramSet.hasNext()) {
        paramAccount.addAll(((zzt)paramSet.next()).zzehs);
      }
    }
    this.zzfzg = Collections.unmodifiableSet(paramAccount);
  }
  
  public final Account getAccount()
  {
    return this.zzebz;
  }
  
  @Deprecated
  public final String getAccountName()
  {
    if (this.zzebz != null) {
      return this.zzebz.name;
    }
    return null;
  }
  
  public final Account zzakt()
  {
    if (this.zzebz != null) {
      return this.zzebz;
    }
    return new Account("<<default account>>", "com.google");
  }
  
  public final Set<Scope> zzakv()
  {
    return this.zzfmo;
  }
  
  public final Set<Scope> zzakw()
  {
    return this.zzfzg;
  }
  
  public final Map<Api<?>, zzt> zzakx()
  {
    return this.zzfzh;
  }
  
  public final String zzaky()
  {
    return this.zzebs;
  }
  
  public final String zzakz()
  {
    return this.zzfms;
  }
  
  public final zzcxe zzalb()
  {
    return this.zzfzi;
  }
  
  public final Integer zzalc()
  {
    return this.zzfzj;
  }
  
  public final void zzc(Integer paramInteger)
  {
    this.zzfzj = paramInteger;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/zzr.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */