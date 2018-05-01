package com.google.android.gms.common.internal;

import android.accounts.Account;
import android.support.v4.util.ArraySet;
import android.view.View;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.signin.SignInOptions;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public final class ClientSettings
{
  private final Set<Scope> zzcv;
  private final int zzcx;
  private final View zzcy;
  private final String zzcz;
  private final String zzda;
  private final Set<Scope> zzrz;
  private final Account zzs;
  private final Map<Api<?>, OptionalApiSettings> zzsa;
  private final SignInOptions zzsb;
  private Integer zzsc;
  
  public ClientSettings(Account paramAccount, Set<Scope> paramSet, Map<Api<?>, OptionalApiSettings> paramMap, int paramInt, View paramView, String paramString1, String paramString2, SignInOptions paramSignInOptions)
  {
    this.zzs = paramAccount;
    if (paramSet == null) {}
    for (paramAccount = Collections.EMPTY_SET;; paramAccount = Collections.unmodifiableSet(paramSet))
    {
      this.zzcv = paramAccount;
      paramAccount = paramMap;
      if (paramMap == null) {
        paramAccount = Collections.EMPTY_MAP;
      }
      this.zzsa = paramAccount;
      this.zzcy = paramView;
      this.zzcx = paramInt;
      this.zzcz = paramString1;
      this.zzda = paramString2;
      this.zzsb = paramSignInOptions;
      paramSet = new HashSet(this.zzcv);
      paramAccount = this.zzsa.values().iterator();
      while (paramAccount.hasNext()) {
        paramSet.addAll(((OptionalApiSettings)paramAccount.next()).mScopes);
      }
    }
    this.zzrz = Collections.unmodifiableSet(paramSet);
  }
  
  @Nullable
  public final Account getAccount()
  {
    return this.zzs;
  }
  
  @Deprecated
  @Nullable
  public final String getAccountName()
  {
    if (this.zzs != null) {}
    for (String str = this.zzs.name;; str = null) {
      return str;
    }
  }
  
  public final Account getAccountOrDefault()
  {
    if (this.zzs != null) {}
    for (Account localAccount = this.zzs;; localAccount = new Account("<<default account>>", "com.google")) {
      return localAccount;
    }
  }
  
  public final Set<Scope> getAllRequestedScopes()
  {
    return this.zzrz;
  }
  
  @Nullable
  public final Integer getClientSessionId()
  {
    return this.zzsc;
  }
  
  public final Map<Api<?>, OptionalApiSettings> getOptionalApiSettings()
  {
    return this.zzsa;
  }
  
  @Nullable
  public final String getRealClientClassName()
  {
    return this.zzda;
  }
  
  @Nullable
  public final String getRealClientPackageName()
  {
    return this.zzcz;
  }
  
  public final Set<Scope> getRequiredScopes()
  {
    return this.zzcv;
  }
  
  @Nullable
  public final SignInOptions getSignInOptions()
  {
    return this.zzsb;
  }
  
  public final void setClientSessionId(Integer paramInteger)
  {
    this.zzsc = paramInteger;
  }
  
  public static final class Builder
  {
    private int zzcx = 0;
    private View zzcy;
    private String zzcz;
    private String zzda;
    private Account zzs;
    private Map<Api<?>, ClientSettings.OptionalApiSettings> zzsa;
    private SignInOptions zzsb = SignInOptions.DEFAULT;
    private ArraySet<Scope> zzsd;
    
    public final Builder addAllRequiredScopes(Collection<Scope> paramCollection)
    {
      if (this.zzsd == null) {
        this.zzsd = new ArraySet();
      }
      this.zzsd.addAll(paramCollection);
      return this;
    }
    
    public final ClientSettings build()
    {
      return new ClientSettings(this.zzs, this.zzsd, this.zzsa, this.zzcx, this.zzcy, this.zzcz, this.zzda, this.zzsb);
    }
    
    public final Builder setAccount(Account paramAccount)
    {
      this.zzs = paramAccount;
      return this;
    }
    
    public final Builder setRealClientClassName(String paramString)
    {
      this.zzda = paramString;
      return this;
    }
    
    public final Builder setRealClientPackageName(String paramString)
    {
      this.zzcz = paramString;
      return this;
    }
  }
  
  public static final class OptionalApiSettings
  {
    public final Set<Scope> mScopes;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/ClientSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */