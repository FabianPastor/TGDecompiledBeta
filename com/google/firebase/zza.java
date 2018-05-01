package com.google.firebase;

import com.google.android.gms.common.api.internal.BackgroundDetector.BackgroundStateChangeListener;

final class zza
  implements BackgroundDetector.BackgroundStateChangeListener
{
  public final void onBackgroundStateChanged(boolean paramBoolean)
  {
    FirebaseApp.onBackgroundStateChanged(paramBoolean);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/firebase/zza.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */