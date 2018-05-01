package com.google.firebase.iid;

import android.os.Bundle;

final class zzs
  extends zzt<Void>
{
  zzs(int paramInt1, int paramInt2, Bundle paramBundle)
  {
    super(paramInt1, 2, paramBundle);
  }
  
  final void zzh(Bundle paramBundle)
  {
    if (paramBundle.getBoolean("ack", false)) {
      finish(null);
    }
    for (;;)
    {
      return;
      zza(new zzu(4, "Invalid response to one way request"));
    }
  }
  
  final boolean zzst()
  {
    return true;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/firebase/iid/zzs.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */