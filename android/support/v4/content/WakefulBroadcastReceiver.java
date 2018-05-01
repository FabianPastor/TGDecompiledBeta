package android.support.v4.content;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.util.SparseArray;

@Deprecated
public abstract class WakefulBroadcastReceiver
  extends BroadcastReceiver
{
  private static int mNextId = 1;
  private static final SparseArray<PowerManager.WakeLock> sActiveWakeLocks = new SparseArray();
  
  public static boolean completeWakefulIntent(Intent arg0)
  {
    boolean bool = false;
    int i = ???.getIntExtra("android.support.content.wakelockid", 0);
    if (i == 0) {}
    for (;;)
    {
      return bool;
      synchronized (sActiveWakeLocks)
      {
        Object localObject1 = (PowerManager.WakeLock)sActiveWakeLocks.get(i);
        if (localObject1 != null)
        {
          ((PowerManager.WakeLock)localObject1).release();
          sActiveWakeLocks.remove(i);
          bool = true;
          continue;
        }
        localObject1 = new java/lang/StringBuilder;
        ((StringBuilder)localObject1).<init>();
        Log.w("WakefulBroadcastReceiv.", "No active wake lock id #" + i);
        bool = true;
      }
    }
  }
  
  public static ComponentName startWakefulService(Context paramContext, Intent paramIntent)
  {
    synchronized (sActiveWakeLocks)
    {
      int i = mNextId;
      mNextId += 1;
      if (mNextId <= 0) {
        mNextId = 1;
      }
      paramIntent.putExtra("android.support.content.wakelockid", i);
      paramIntent = paramContext.startService(paramIntent);
      if (paramIntent == null)
      {
        paramContext = null;
        return paramContext;
      }
      paramContext = (PowerManager)paramContext.getSystemService("power");
      StringBuilder localStringBuilder = new java/lang/StringBuilder;
      localStringBuilder.<init>();
      paramContext = paramContext.newWakeLock(1, "wake:" + paramIntent.flattenToShortString());
      paramContext.setReferenceCounted(false);
      paramContext.acquire(60000L);
      sActiveWakeLocks.put(i, paramContext);
      paramContext = paramIntent;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v4/content/WakefulBroadcastReceiver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */