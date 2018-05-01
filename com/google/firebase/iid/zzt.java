package com.google.firebase.iid;

import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.tasks.TaskCompletionSource;

abstract class zzt<T>
{
  final int what;
  final int zzbrh;
  final TaskCompletionSource<T> zzbri = new TaskCompletionSource();
  final Bundle zzbrj;
  
  zzt(int paramInt1, int paramInt2, Bundle paramBundle)
  {
    this.zzbrh = paramInt1;
    this.what = paramInt2;
    this.zzbrj = paramBundle;
  }
  
  final void finish(T paramT)
  {
    if (Log.isLoggable("MessengerIpcClient", 3))
    {
      String str1 = String.valueOf(this);
      String str2 = String.valueOf(paramT);
      Log.d("MessengerIpcClient", String.valueOf(str1).length() + 16 + String.valueOf(str2).length() + "Finishing " + str1 + " with " + str2);
    }
    this.zzbri.setResult(paramT);
  }
  
  public String toString()
  {
    int i = this.what;
    int j = this.zzbrh;
    boolean bool = zzst();
    return 55 + "Request { what=" + i + " id=" + j + " oneWay=" + bool + "}";
  }
  
  final void zza(zzu paramzzu)
  {
    if (Log.isLoggable("MessengerIpcClient", 3))
    {
      String str1 = String.valueOf(this);
      String str2 = String.valueOf(paramzzu);
      Log.d("MessengerIpcClient", String.valueOf(str1).length() + 14 + String.valueOf(str2).length() + "Failing " + str1 + " with " + str2);
    }
    this.zzbri.setException(paramzzu);
  }
  
  abstract void zzh(Bundle paramBundle);
  
  abstract boolean zzst();
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/firebase/iid/zzt.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */