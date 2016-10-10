package com.google.android.gms.common.internal;

import android.content.Context;
import android.os.IBinder;
import android.view.View;
import com.google.android.gms.dynamic.zzd;
import com.google.android.gms.dynamic.zze;
import com.google.android.gms.dynamic.zzg;
import com.google.android.gms.dynamic.zzg.zza;

public final class zzag
  extends zzg<zzy>
{
  private static final zzag Da = new zzag();
  
  private zzag()
  {
    super("com.google.android.gms.common.ui.SignInButtonCreatorImpl");
  }
  
  public static View zzb(Context paramContext, int paramInt1, int paramInt2)
    throws zzg.zza
  {
    return Da.zzc(paramContext, paramInt1, paramInt2);
  }
  
  private View zzc(Context paramContext, int paramInt1, int paramInt2)
    throws zzg.zza
  {
    try
    {
      SignInButtonConfig localSignInButtonConfig = new SignInButtonConfig(paramInt1, paramInt2, null);
      zzd localzzd = zze.zzac(paramContext);
      paramContext = (View)zze.zzae(((zzy)zzcu(paramContext)).zza(localzzd, localSignInButtonConfig));
      return paramContext;
    }
    catch (Exception paramContext)
    {
      throw new zzg.zza(64 + "Could not get button with size " + paramInt1 + " and color " + paramInt2, paramContext);
    }
  }
  
  public zzy zzdz(IBinder paramIBinder)
  {
    return zzy.zza.zzdy(paramIBinder);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/zzag.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */