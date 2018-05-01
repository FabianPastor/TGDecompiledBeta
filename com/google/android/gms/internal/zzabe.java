package com.google.android.gms.internal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.MainThread;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public class zzabe
{
  protected final zzabf zzaCR;
  
  protected zzabe(zzabf paramzzabf)
  {
    this.zzaCR = paramzzabf;
  }
  
  protected static zzabf zzc(zzabd paramzzabd)
  {
    if (paramzzabd.zzwS()) {
      return zzabu.zza(paramzzabd.zzwU());
    }
    return zzabg.zzt(paramzzabd.zzwT());
  }
  
  public static zzabf zzs(Activity paramActivity)
  {
    return zzc(new zzabd(paramActivity));
  }
  
  @MainThread
  public void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString) {}
  
  public Activity getActivity()
  {
    return this.zzaCR.zzwV();
  }
  
  @MainThread
  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent) {}
  
  @MainThread
  public void onCreate(Bundle paramBundle) {}
  
  @MainThread
  public void onDestroy() {}
  
  @MainThread
  public void onSaveInstanceState(Bundle paramBundle) {}
  
  @MainThread
  public void onStart() {}
  
  @MainThread
  public void onStop() {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzabe.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */