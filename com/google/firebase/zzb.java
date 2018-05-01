package com.google.firebase;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.internal.zzbem;

public final class zzb
  implements zzbem
{
  public final Exception zzq(Status paramStatus)
  {
    if (paramStatus.getStatusCode() == 8) {
      return new FirebaseException(paramStatus.zzpq());
    }
    return new FirebaseApiNotAvailableException(paramStatus.zzpq());
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/firebase/zzb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */