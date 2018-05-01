package com.google.android.gms.common.api.internal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Keep;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public class LifecycleCallback
{
  protected final LifecycleFragment mLifecycleFragment;
  
  protected LifecycleCallback(LifecycleFragment paramLifecycleFragment)
  {
    this.mLifecycleFragment = paramLifecycleFragment;
  }
  
  @Keep
  private static LifecycleFragment getChimeraLifecycleFragmentImpl(LifecycleActivity paramLifecycleActivity)
  {
    throw new IllegalStateException("Method not available in SDK.");
  }
  
  protected static LifecycleFragment getFragment(LifecycleActivity paramLifecycleActivity)
  {
    if (paramLifecycleActivity.zzbv()) {}
    for (paramLifecycleActivity = zzcc.zza(paramLifecycleActivity.zzby());; paramLifecycleActivity = zzbr.zzc(paramLifecycleActivity.zzbx()))
    {
      return paramLifecycleActivity;
      if (!paramLifecycleActivity.zzbw()) {
        break;
      }
    }
    throw new IllegalArgumentException("Can't get fragment for unexpected activity.");
  }
  
  public void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString) {}
  
  public final Activity getActivity()
  {
    return this.mLifecycleFragment.getLifecycleActivity();
  }
  
  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent) {}
  
  public void onCreate(Bundle paramBundle) {}
  
  public void onDestroy() {}
  
  public void onResume() {}
  
  public void onSaveInstanceState(Bundle paramBundle) {}
  
  public void onStart() {}
  
  public void onStop() {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/LifecycleCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */