package com.google.android.gms.wearable;

import android.content.Context;
import android.os.Looper;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.AbstractClientBuilder;
import com.google.android.gms.common.api.Api.ApiOptions.Optional;
import com.google.android.gms.common.api.Api.ClientKey;
import com.google.android.gms.common.api.GoogleApi.Settings;
import com.google.android.gms.wearable.internal.zzaa;
import com.google.android.gms.wearable.internal.zzaj;
import com.google.android.gms.wearable.internal.zzbv;
import com.google.android.gms.wearable.internal.zzbw;
import com.google.android.gms.wearable.internal.zzeu;
import com.google.android.gms.wearable.internal.zzez;
import com.google.android.gms.wearable.internal.zzfg;
import com.google.android.gms.wearable.internal.zzgi;
import com.google.android.gms.wearable.internal.zzh;
import com.google.android.gms.wearable.internal.zzhg;
import com.google.android.gms.wearable.internal.zzhq;
import com.google.android.gms.wearable.internal.zzk;
import com.google.android.gms.wearable.internal.zzo;

public class Wearable
{
  @Deprecated
  public static final Api<WearableOptions> API = new Api("Wearable.API", CLIENT_BUILDER, CLIENT_KEY);
  private static final Api.AbstractClientBuilder<zzhg, WearableOptions> CLIENT_BUILDER;
  private static final Api.ClientKey<zzhg> CLIENT_KEY;
  @Deprecated
  public static final CapabilityApi CapabilityApi;
  @Deprecated
  public static final ChannelApi ChannelApi;
  @Deprecated
  public static final DataApi DataApi = new zzbw();
  @Deprecated
  public static final MessageApi MessageApi;
  @Deprecated
  public static final NodeApi NodeApi;
  @Deprecated
  private static final zzi zzaa;
  @Deprecated
  private static final zzu zzab;
  @Deprecated
  private static final zzc zzx;
  @Deprecated
  private static final zza zzy;
  @Deprecated
  private static final zzf zzz;
  
  static
  {
    CapabilityApi = new zzo();
    MessageApi = new zzeu();
    NodeApi = new zzfg();
    ChannelApi = new zzaj();
    zzx = new zzk();
    zzy = new zzh();
    zzz = new zzbv();
    zzaa = new zzgi();
    zzab = new zzhq();
    CLIENT_KEY = new Api.ClientKey();
    CLIENT_BUILDER = new zzj();
  }
  
  public static CapabilityClient getCapabilityClient(Context paramContext)
  {
    return new zzaa(paramContext, GoogleApi.Settings.DEFAULT_SETTINGS);
  }
  
  public static MessageClient getMessageClient(Context paramContext)
  {
    return new zzez(paramContext, GoogleApi.Settings.DEFAULT_SETTINGS);
  }
  
  public static final class WearableOptions
    implements Api.ApiOptions.Optional
  {
    private final Looper zzac;
    
    private WearableOptions(Builder paramBuilder)
    {
      this.zzac = Builder.zza(paramBuilder);
    }
    
    public static class Builder
    {
      private Looper zzac;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/Wearable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */