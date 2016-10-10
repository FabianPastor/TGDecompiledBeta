package com.google.android.gms.common.internal;

import android.accounts.Account;
import android.content.Context;
import android.view.View;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient.Builder;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.internal.zzxa;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class zzh
{
  private final Set<Scope> BX;
  private final Map<Api<?>, zza> BY;
  private final zzxa BZ;
  private Integer Ca;
  private final Account ec;
  private final String fo;
  private final Set<Scope> vF;
  private final int vH;
  private final View vI;
  private final String vJ;
  
  public zzh(Account paramAccount, Set<Scope> paramSet, Map<Api<?>, zza> paramMap, int paramInt, View paramView, String paramString1, String paramString2, zzxa paramzzxa)
  {
    this.ec = paramAccount;
    if (paramSet == null) {}
    for (paramAccount = Collections.EMPTY_SET;; paramAccount = Collections.unmodifiableSet(paramSet))
    {
      this.vF = paramAccount;
      paramAccount = paramMap;
      if (paramMap == null) {
        paramAccount = Collections.EMPTY_MAP;
      }
      this.BY = paramAccount;
      this.vI = paramView;
      this.vH = paramInt;
      this.fo = paramString1;
      this.vJ = paramString2;
      this.BZ = paramzzxa;
      paramAccount = new HashSet(this.vF);
      paramSet = this.BY.values().iterator();
      while (paramSet.hasNext()) {
        paramAccount.addAll(((zza)paramSet.next()).hm);
      }
    }
    this.BX = Collections.unmodifiableSet(paramAccount);
  }
  
  public static zzh zzcd(Context paramContext)
  {
    return new GoogleApiClient.Builder(paramContext).zzaqd();
  }
  
  public Account getAccount()
  {
    return this.ec;
  }
  
  @Deprecated
  public String getAccountName()
  {
    if (this.ec != null) {
      return this.ec.name;
    }
    return null;
  }
  
  public Account zzatv()
  {
    if (this.ec != null) {
      return this.ec;
    }
    return new Account("<<default account>>", "com.google");
  }
  
  public int zzauf()
  {
    return this.vH;
  }
  
  public Set<Scope> zzaug()
  {
    return this.vF;
  }
  
  public Set<Scope> zzauh()
  {
    return this.BX;
  }
  
  public Map<Api<?>, zza> zzaui()
  {
    return this.BY;
  }
  
  public String zzauj()
  {
    return this.fo;
  }
  
  public String zzauk()
  {
    return this.vJ;
  }
  
  public View zzaul()
  {
    return this.vI;
  }
  
  public zzxa zzaum()
  {
    return this.BZ;
  }
  
  public Integer zzaun()
  {
    return this.Ca;
  }
  
  public Set<Scope> zzb(Api<?> paramApi)
  {
    paramApi = (zza)this.BY.get(paramApi);
    if ((paramApi == null) || (paramApi.hm.isEmpty())) {
      return this.vF;
    }
    HashSet localHashSet = new HashSet(this.vF);
    localHashSet.addAll(paramApi.hm);
    return localHashSet;
  }
  
  public void zzc(Integer paramInteger)
  {
    this.Ca = paramInteger;
  }
  
  public static final class zza
  {
    public final boolean Cb;
    public final Set<Scope> hm;
    
    public zza(Set<Scope> paramSet, boolean paramBoolean)
    {
      zzac.zzy(paramSet);
      this.hm = Collections.unmodifiableSet(paramSet);
      this.Cb = paramBoolean;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/zzh.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */