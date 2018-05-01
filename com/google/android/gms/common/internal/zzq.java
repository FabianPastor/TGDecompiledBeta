package com.google.android.gms.common.internal;

import android.accounts.Account;
import android.content.Context;
import android.view.View;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient.Builder;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.internal.zzctl;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class zzq
{
  private final Set<Scope> zzaAT;
  private final int zzaAV;
  private final View zzaAW;
  private final String zzaAX;
  private final Set<Scope> zzaHk;
  private final Map<Api<?>, zzr> zzaHl;
  private final zzctl zzaHm;
  private Integer zzaHn;
  private final Account zzajb;
  private final String zzake;
  
  public zzq(Account paramAccount, Set<Scope> paramSet, Map<Api<?>, zzr> paramMap, int paramInt, View paramView, String paramString1, String paramString2, zzctl paramzzctl)
  {
    this.zzajb = paramAccount;
    if (paramSet == null) {}
    for (paramAccount = Collections.EMPTY_SET;; paramAccount = Collections.unmodifiableSet(paramSet))
    {
      this.zzaAT = paramAccount;
      paramAccount = paramMap;
      if (paramMap == null) {
        paramAccount = Collections.EMPTY_MAP;
      }
      this.zzaHl = paramAccount;
      this.zzaAW = paramView;
      this.zzaAV = paramInt;
      this.zzake = paramString1;
      this.zzaAX = paramString2;
      this.zzaHm = paramzzctl;
      paramAccount = new HashSet(this.zzaAT);
      paramSet = this.zzaHl.values().iterator();
      while (paramSet.hasNext()) {
        paramAccount.addAll(((zzr)paramSet.next()).zzame);
      }
    }
    this.zzaHk = Collections.unmodifiableSet(paramAccount);
  }
  
  public static zzq zzaA(Context paramContext)
  {
    return new GoogleApiClient.Builder(paramContext).zzpn();
  }
  
  public final Account getAccount()
  {
    return this.zzajb;
  }
  
  @Deprecated
  public final String getAccountName()
  {
    if (this.zzajb != null) {
      return this.zzajb.name;
    }
    return null;
  }
  
  public final Set<Scope> zzc(Api<?> paramApi)
  {
    paramApi = (zzr)this.zzaHl.get(paramApi);
    if ((paramApi == null) || (paramApi.zzame.isEmpty())) {
      return this.zzaAT;
    }
    HashSet localHashSet = new HashSet(this.zzaAT);
    localHashSet.addAll(paramApi.zzame);
    return localHashSet;
  }
  
  public final void zzc(Integer paramInteger)
  {
    this.zzaHn = paramInteger;
  }
  
  public final Account zzrl()
  {
    if (this.zzajb != null) {
      return this.zzajb;
    }
    return new Account("<<default account>>", "com.google");
  }
  
  public final int zzrm()
  {
    return this.zzaAV;
  }
  
  public final Set<Scope> zzrn()
  {
    return this.zzaAT;
  }
  
  public final Set<Scope> zzro()
  {
    return this.zzaHk;
  }
  
  public final Map<Api<?>, zzr> zzrp()
  {
    return this.zzaHl;
  }
  
  public final String zzrq()
  {
    return this.zzake;
  }
  
  public final String zzrr()
  {
    return this.zzaAX;
  }
  
  public final View zzrs()
  {
    return this.zzaAW;
  }
  
  public final zzctl zzrt()
  {
    return this.zzaHm;
  }
  
  public final Integer zzru()
  {
    return this.zzaHn;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/zzq.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */