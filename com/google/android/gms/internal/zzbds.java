package com.google.android.gms.internal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.MainThread;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public class zzbds
{
  protected final zzbdt zzaEG;
  
  protected zzbds(zzbdt paramzzbdt)
  {
    this.zzaEG = paramzzbdt;
  }
  
  protected static zzbdt zzb(zzbdr paramzzbdr)
  {
    if (paramzzbdr.zzqC()) {
      return zzbeo.zza(paramzzbdr.zzqE());
    }
    return zzbdu.zzo(paramzzbdr.zzqD());
  }
  
  public static zzbdt zzn(Activity paramActivity)
  {
    return zzb(new zzbdr(paramActivity));
  }
  
  @MainThread
  public void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString) {}
  
  public final Activity getActivity()
  {
    return this.zzaEG.zzqF();
  }
  
  @MainThread
  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent) {}
  
  @MainThread
  public void onCreate(Bundle paramBundle) {}
  
  @MainThread
  public void onDestroy() {}
  
  @MainThread
  public void onResume() {}
  
  @MainThread
  public void onSaveInstanceState(Bundle paramBundle) {}
  
  @MainThread
  public void onStart() {}
  
  @MainThread
  public void onStop() {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbds.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */