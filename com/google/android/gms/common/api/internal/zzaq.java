package com.google.android.gms.common.api.internal;

import com.google.android.gms.signin.internal.BaseSignInCallbacks;
import com.google.android.gms.signin.internal.SignInResponse;
import java.lang.ref.WeakReference;

final class zzaq
  extends BaseSignInCallbacks
{
  private final WeakReference<zzaj> zzhw;
  
  zzaq(zzaj paramzzaj)
  {
    this.zzhw = new WeakReference(paramzzaj);
  }
  
  public final void onSignInComplete(SignInResponse paramSignInResponse)
  {
    zzaj localzzaj = (zzaj)this.zzhw.get();
    if (localzzaj == null) {}
    for (;;)
    {
      return;
      zzaj.zzd(localzzaj).zza(new zzar(this, localzzaj, localzzaj, paramSignInResponse));
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/zzaq.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */