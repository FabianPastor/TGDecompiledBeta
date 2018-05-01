package com.google.android.gms.common.api;

import android.accounts.Account;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.internal.ApiExceptionMapper;
import com.google.android.gms.common.api.internal.BaseImplementation.ApiMethodImpl;
import com.google.android.gms.common.api.internal.BasePendingResult;
import com.google.android.gms.common.api.internal.GoogleApiManager;
import com.google.android.gms.common.api.internal.GoogleApiManager.zza;
import com.google.android.gms.common.api.internal.StatusExceptionMapper;
import com.google.android.gms.common.api.internal.zzbo;
import com.google.android.gms.common.api.internal.zzby;
import com.google.android.gms.common.api.internal.zzh;
import com.google.android.gms.common.internal.ClientSettings;
import com.google.android.gms.common.internal.ClientSettings.Builder;
import com.google.android.gms.common.internal.Preconditions;
import java.util.Collection;
import java.util.Collections;

public class GoogleApi<O extends Api.ApiOptions>
{
  private final Api<O> mApi;
  private final Context mContext;
  private final int mId;
  private final O zzcl;
  private final zzh<O> zzcm;
  private final Looper zzcn;
  private final GoogleApiClient zzco;
  private final StatusExceptionMapper zzcp;
  protected final GoogleApiManager zzcq;
  
  protected GoogleApi(Context paramContext, Api<O> paramApi, Looper paramLooper)
  {
    Preconditions.checkNotNull(paramContext, "Null context is not permitted.");
    Preconditions.checkNotNull(paramApi, "Api must not be null.");
    Preconditions.checkNotNull(paramLooper, "Looper must not be null.");
    this.mContext = paramContext.getApplicationContext();
    this.mApi = paramApi;
    this.zzcl = null;
    this.zzcn = paramLooper;
    this.zzcm = zzh.zza(paramApi);
    this.zzco = new zzbo(this);
    this.zzcq = GoogleApiManager.zzb(this.mContext);
    this.mId = this.zzcq.zzbg();
    this.zzcp = new ApiExceptionMapper();
  }
  
  public GoogleApi(Context paramContext, Api<O> paramApi, O paramO, Settings paramSettings)
  {
    Preconditions.checkNotNull(paramContext, "Null context is not permitted.");
    Preconditions.checkNotNull(paramApi, "Api must not be null.");
    Preconditions.checkNotNull(paramSettings, "Settings must not be null; use Settings.DEFAULT_SETTINGS instead.");
    this.mContext = paramContext.getApplicationContext();
    this.mApi = paramApi;
    this.zzcl = paramO;
    this.zzcn = paramSettings.zzcs;
    this.zzcm = zzh.zza(this.mApi, this.zzcl);
    this.zzco = new zzbo(this);
    this.zzcq = GoogleApiManager.zzb(this.mContext);
    this.mId = this.zzcq.zzbg();
    this.zzcp = paramSettings.zzcr;
    this.zzcq.zza(this);
  }
  
  @Deprecated
  public GoogleApi(Context paramContext, Api<O> paramApi, O paramO, StatusExceptionMapper paramStatusExceptionMapper)
  {
    this(paramContext, paramApi, paramO, new GoogleApi.Settings.Builder().setMapper(paramStatusExceptionMapper).build());
  }
  
  private final <A extends Api.AnyClient, T extends BaseImplementation.ApiMethodImpl<? extends Result, A>> T zza(int paramInt, T paramT)
  {
    paramT.zzx();
    this.zzcq.zza(this, paramInt, paramT);
    return paramT;
  }
  
  public GoogleApiClient asGoogleApiClient()
  {
    return this.zzco;
  }
  
  protected ClientSettings.Builder createClientSettingsBuilder()
  {
    ClientSettings.Builder localBuilder = new ClientSettings.Builder();
    if ((this.zzcl instanceof Api.ApiOptions.HasGoogleSignInAccountOptions))
    {
      localObject = ((Api.ApiOptions.HasGoogleSignInAccountOptions)this.zzcl).getGoogleSignInAccount();
      if (localObject != null)
      {
        localObject = ((GoogleSignInAccount)localObject).getAccount();
        localBuilder = localBuilder.setAccount((Account)localObject);
        if (!(this.zzcl instanceof Api.ApiOptions.HasGoogleSignInAccountOptions)) {
          break label138;
        }
        localObject = ((Api.ApiOptions.HasGoogleSignInAccountOptions)this.zzcl).getGoogleSignInAccount();
        if (localObject == null) {
          break label138;
        }
      }
    }
    label138:
    for (Object localObject = ((GoogleSignInAccount)localObject).getRequestedScopes();; localObject = Collections.emptySet())
    {
      return localBuilder.addAllRequiredScopes((Collection)localObject).setRealClientClassName(this.mContext.getClass().getName()).setRealClientPackageName(this.mContext.getPackageName());
      if ((this.zzcl instanceof Api.ApiOptions.HasAccountOptions))
      {
        localObject = ((Api.ApiOptions.HasAccountOptions)this.zzcl).getAccount();
        break;
      }
      localObject = null;
      break;
    }
  }
  
  public <A extends Api.AnyClient, T extends BaseImplementation.ApiMethodImpl<? extends Result, A>> T doRead(T paramT)
  {
    return zza(0, paramT);
  }
  
  public <A extends Api.AnyClient, T extends BaseImplementation.ApiMethodImpl<? extends Result, A>> T doWrite(T paramT)
  {
    return zza(1, paramT);
  }
  
  public final Api<O> getApi()
  {
    return this.mApi;
  }
  
  public final int getInstanceId()
  {
    return this.mId;
  }
  
  public Looper getLooper()
  {
    return this.zzcn;
  }
  
  public Api.Client zza(Looper paramLooper, GoogleApiManager.zza<O> paramzza)
  {
    ClientSettings localClientSettings = createClientSettingsBuilder().build();
    return this.mApi.zzk().buildClient(this.mContext, paramLooper, localClientSettings, this.zzcl, paramzza, paramzza);
  }
  
  public zzby zza(Context paramContext, Handler paramHandler)
  {
    return new zzby(paramContext, paramHandler, createClientSettingsBuilder().build());
  }
  
  public final zzh<O> zzm()
  {
    return this.zzcm;
  }
  
  public static class Settings
  {
    public static final Settings DEFAULT_SETTINGS = new Builder().build();
    public final StatusExceptionMapper zzcr;
    public final Looper zzcs;
    
    private Settings(StatusExceptionMapper paramStatusExceptionMapper, Account paramAccount, Looper paramLooper)
    {
      this.zzcr = paramStatusExceptionMapper;
      this.zzcs = paramLooper;
    }
    
    public static class Builder
    {
      private Looper zzcn;
      private StatusExceptionMapper zzcp;
      
      public GoogleApi.Settings build()
      {
        if (this.zzcp == null) {
          this.zzcp = new ApiExceptionMapper();
        }
        if (this.zzcn == null) {
          this.zzcn = Looper.getMainLooper();
        }
        return new GoogleApi.Settings(this.zzcp, null, this.zzcn, null);
      }
      
      public Builder setMapper(StatusExceptionMapper paramStatusExceptionMapper)
      {
        Preconditions.checkNotNull(paramStatusExceptionMapper, "StatusExceptionMapper must not be null.");
        this.zzcp = paramStatusExceptionMapper;
        return this;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/GoogleApi.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */