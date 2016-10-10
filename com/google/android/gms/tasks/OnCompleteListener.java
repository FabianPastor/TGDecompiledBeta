package com.google.android.gms.tasks;

import android.support.annotation.NonNull;

public abstract interface OnCompleteListener<TResult>
{
  public abstract void onComplete(@NonNull Task<TResult> paramTask);
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/tasks/OnCompleteListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */