package com.google.android.gms.common.api.internal;

import android.app.Activity;
import android.content.Intent;

public abstract interface LifecycleFragment
{
  public abstract void addCallback(String paramString, LifecycleCallback paramLifecycleCallback);
  
  public abstract <T extends LifecycleCallback> T getCallbackOrNull(String paramString, Class<T> paramClass);
  
  public abstract Activity getLifecycleActivity();
  
  public abstract void startActivityForResult(Intent paramIntent, int paramInt);
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/LifecycleFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */