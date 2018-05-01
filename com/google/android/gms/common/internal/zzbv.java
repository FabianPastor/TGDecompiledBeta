package com.google.android.gms.common.internal;

import android.content.Context;
import android.view.View;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.dynamic.zzn;
import com.google.android.gms.dynamic.zzp;
import com.google.android.gms.dynamic.zzq;

public final class zzbv
  extends zzp<zzbb>
{
  private static final zzbv zzaIv = new zzbv();
  
  private zzbv()
  {
    super("com.google.android.gms.common.ui.SignInButtonCreatorImpl");
  }
  
  public static View zzc(Context paramContext, int paramInt1, int paramInt2)
    throws zzq
  {
    return zzaIv.zzd(paramContext, paramInt1, paramInt2);
  }
  
  private final View zzd(Context paramContext, int paramInt1, int paramInt2)
    throws zzq
  {
    try
    {
      zzbt localzzbt = new zzbt(paramInt1, paramInt2, null);
      IObjectWrapper localIObjectWrapper = zzn.zzw(paramContext);
      paramContext = (View)zzn.zzE(((zzbb)zzaS(paramContext)).zza(localIObjectWrapper, localzzbt));
      return paramContext;
    }
    catch (Exception paramContext)
    {
      throw new zzq(64 + "Could not get button with size " + paramInt1 + " and color " + paramInt2, paramContext);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/zzbv.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */