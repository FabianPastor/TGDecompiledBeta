package com.google.android.gms.internal;

import android.os.Bundle;
import com.google.android.gms.common.api.BooleanResult;
import com.google.android.gms.common.api.Status;

final class gx
  extends gw
{
  private final zzbaz<BooleanResult> zzaIz;
  
  public gx(zzbaz<BooleanResult> paramzzbaz)
  {
    super(null);
    this.zzaIz = paramzzbaz;
  }
  
  public final void zza(Status paramStatus, boolean paramBoolean, Bundle paramBundle)
  {
    this.zzaIz.setResult(new BooleanResult(paramStatus, paramBoolean));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/gx.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */