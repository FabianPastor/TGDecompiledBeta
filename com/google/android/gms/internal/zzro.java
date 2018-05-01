package com.google.android.gms.internal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.MainThread;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public class zzro
{
  protected final zzrp Bf;
  
  protected zzro(zzrp paramzzrp)
  {
    this.Bf = paramzzrp;
  }
  
  protected static zzrp zzc(zzrn paramzzrn)
  {
    if (paramzzrn.zzatv()) {
      return zzsd.zza(paramzzrn.zzatx());
    }
    return zzrq.zzt(paramzzrn.zzatw());
  }
  
  public static zzrp zzs(Activity paramActivity)
  {
    return zzc(new zzrn(paramActivity));
  }
  
  @MainThread
  public void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString) {}
  
  public Activity getActivity()
  {
    return this.Bf.zzaty();
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


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzro.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */