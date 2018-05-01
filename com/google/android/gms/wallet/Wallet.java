package com.google.android.gms.wallet;

import android.accounts.Account;
import android.os.RemoteException;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.AbstractClientBuilder;
import com.google.android.gms.common.api.Api.ApiOptions.HasAccountOptions;
import com.google.android.gms.common.api.Api.ClientKey;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.internal.BaseImplementation.ApiMethodImpl;
import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.internal.wallet.zzad;
import com.google.android.gms.internal.wallet.zzal;
import com.google.android.gms.internal.wallet.zzam;
import com.google.android.gms.internal.wallet.zze;
import com.google.android.gms.internal.wallet.zzw;
import com.google.android.gms.wallet.wobs.WalletObjects;
import java.util.Locale;

public final class Wallet
{
  public static final Api<WalletOptions> API = new Api("Wallet.API", CLIENT_BUILDER, CLIENT_KEY);
  private static final Api.AbstractClientBuilder<zzad, WalletOptions> CLIENT_BUILDER;
  private static final Api.ClientKey<zzad> CLIENT_KEY = new Api.ClientKey();
  @Deprecated
  public static final Payments Payments = new zzw();
  private static final WalletObjects zzep = new zzam();
  private static final zze zzeq = new zzal();
  
  static
  {
    CLIENT_BUILDER = new zzap();
  }
  
  public static final class WalletOptions
    implements Api.ApiOptions.HasAccountOptions
  {
    private final Account account;
    public final int environment;
    public final int theme;
    final boolean zzer;
    
    private WalletOptions()
    {
      this(new Builder());
    }
    
    private WalletOptions(Builder paramBuilder)
    {
      this.environment = Builder.zza(paramBuilder);
      this.theme = Builder.zzb(paramBuilder);
      this.zzer = Builder.zzc(paramBuilder);
      this.account = null;
    }
    
    public final boolean equals(Object paramObject)
    {
      boolean bool1 = false;
      boolean bool2 = bool1;
      if ((paramObject instanceof WalletOptions))
      {
        paramObject = (WalletOptions)paramObject;
        bool2 = bool1;
        if (Objects.equal(Integer.valueOf(this.environment), Integer.valueOf(((WalletOptions)paramObject).environment)))
        {
          bool2 = bool1;
          if (Objects.equal(Integer.valueOf(this.theme), Integer.valueOf(((WalletOptions)paramObject).theme)))
          {
            bool2 = bool1;
            if (Objects.equal(null, null))
            {
              bool2 = bool1;
              if (Objects.equal(Boolean.valueOf(this.zzer), Boolean.valueOf(((WalletOptions)paramObject).zzer))) {
                bool2 = true;
              }
            }
          }
        }
      }
      return bool2;
    }
    
    public final Account getAccount()
    {
      return null;
    }
    
    public final int hashCode()
    {
      return Objects.hashCode(new Object[] { Integer.valueOf(this.environment), Integer.valueOf(this.theme), null, Boolean.valueOf(this.zzer) });
    }
    
    public static final class Builder
    {
      private int environment = 3;
      private int theme = 0;
      private boolean zzer = true;
      
      public final Wallet.WalletOptions build()
      {
        return new Wallet.WalletOptions(this, null);
      }
      
      public final Builder setEnvironment(int paramInt)
      {
        if ((paramInt == 0) || (paramInt == 0) || (paramInt == 2) || (paramInt == 1) || (paramInt == 3))
        {
          this.environment = paramInt;
          return this;
        }
        throw new IllegalArgumentException(String.format(Locale.US, "Invalid environment value %d", new Object[] { Integer.valueOf(paramInt) }));
      }
      
      public final Builder setTheme(int paramInt)
      {
        if ((paramInt == 0) || (paramInt == 1))
        {
          this.theme = paramInt;
          return this;
        }
        throw new IllegalArgumentException(String.format(Locale.US, "Invalid theme value %d", new Object[] { Integer.valueOf(paramInt) }));
      }
    }
  }
  
  public static abstract class zza<R extends Result>
    extends BaseImplementation.ApiMethodImpl<R, zzad>
  {
    public zza(GoogleApiClient paramGoogleApiClient)
    {
      super(paramGoogleApiClient);
    }
    
    protected abstract void zza(zzad paramzzad)
      throws RemoteException;
  }
  
  public static abstract class zzb
    extends Wallet.zza<Status>
  {
    public zzb(GoogleApiClient paramGoogleApiClient)
    {
      super();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wallet/Wallet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */