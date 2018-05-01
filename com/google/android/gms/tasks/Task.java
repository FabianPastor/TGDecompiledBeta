package com.google.android.gms.tasks;

import java.util.concurrent.Executor;

public abstract class Task<TResult>
{
  public Task<TResult> addOnCanceledListener(Executor paramExecutor, OnCanceledListener paramOnCanceledListener)
  {
    throw new UnsupportedOperationException("addOnCanceledListener is not implemented");
  }
  
  public Task<TResult> addOnCompleteListener(OnCompleteListener<TResult> paramOnCompleteListener)
  {
    throw new UnsupportedOperationException("addOnCompleteListener is not implemented");
  }
  
  public Task<TResult> addOnCompleteListener(Executor paramExecutor, OnCompleteListener<TResult> paramOnCompleteListener)
  {
    throw new UnsupportedOperationException("addOnCompleteListener is not implemented");
  }
  
  public abstract Task<TResult> addOnFailureListener(Executor paramExecutor, OnFailureListener paramOnFailureListener);
  
  public abstract Task<TResult> addOnSuccessListener(Executor paramExecutor, OnSuccessListener<? super TResult> paramOnSuccessListener);
  
  public abstract Exception getException();
  
  public abstract TResult getResult();
  
  public abstract boolean isCanceled();
  
  public abstract boolean isComplete();
  
  public abstract boolean isSuccessful();
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/tasks/Task.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */