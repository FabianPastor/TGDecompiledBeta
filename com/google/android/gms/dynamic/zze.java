package com.google.android.gms.dynamic;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

final class zze
  implements View.OnClickListener
{
  zze(Context paramContext, Intent paramIntent) {}
  
  public final void onClick(View paramView)
  {
    try
    {
      this.val$context.startActivity(this.zzabl);
      return;
    }
    catch (ActivityNotFoundException paramView)
    {
      for (;;)
      {
        Log.e("DeferredLifecycleHelper", "Failed to start resolution intent", paramView);
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/dynamic/zze.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */