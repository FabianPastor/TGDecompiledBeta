package org.telegram.messenger;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.SparseArray;
import java.io.File;

public class ClearCacheService
  extends IntentService
{
  public ClearCacheService()
  {
    super("ClearCacheService");
  }
  
  protected void onHandleIntent(Intent paramIntent)
  {
    ApplicationLoader.postInitApplication();
    final int i = MessagesController.getGlobalMainSettings().getInt("keep_media", 2);
    if (i == 2) {}
    for (;;)
    {
      return;
      Utilities.globalQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          int i;
          long l1;
          long l2;
          SparseArray localSparseArray;
          if (i == 0)
          {
            i = 7;
            l1 = System.currentTimeMillis() / 1000L;
            l2 = 86400 * i;
            localSparseArray = ImageLoader.getInstance().createMediaPaths();
            i = 0;
            label35:
            if (i >= localSparseArray.size()) {
              return;
            }
            if (localSparseArray.keyAt(i) != 4) {
              break label79;
            }
          }
          for (;;)
          {
            i++;
            break label35;
            if (i == 1)
            {
              i = 30;
              break;
            }
            i = 3;
            break;
            try
            {
              label79:
              Utilities.clearDir(((File)localSparseArray.valueAt(i)).getAbsolutePath(), 0, l1 - l2);
            }
            catch (Throwable localThrowable)
            {
              FileLog.e(localThrowable);
            }
          }
        }
      });
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/ClearCacheService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */