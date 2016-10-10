package org.telegram.messenger;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build.VERSION;
import android.system.Os;
import android.system.StructStat;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

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
    final int i = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).getInt("keep_media", 2);
    if (i == 2) {
      return;
    }
    Utilities.globalQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        long l1 = System.currentTimeMillis();
        if (i == 0) {}
        for (int i = 7;; i = 30)
        {
          long l2 = i * 86400000;
          Iterator localIterator = ImageLoader.getInstance().createMediaPaths().entrySet().iterator();
          Object localObject1;
          do
          {
            if (!localIterator.hasNext()) {
              break;
            }
            localObject1 = (Map.Entry)localIterator.next();
          } while (((Integer)((Map.Entry)localObject1).getKey()).intValue() == 4);
          for (;;)
          {
            StructStat localStructStat;
            try
            {
              localObject1 = ((File)((Map.Entry)localObject1).getValue()).listFiles();
              if (localObject1 == null) {
                break;
              }
              i = 0;
              if (i >= localObject1.length) {
                break;
              }
              Object localObject2 = localObject1[i];
              if ((!((File)localObject2).isFile()) || (((File)localObject2).getName().equals(".nomedia"))) {
                break label256;
              }
              int j = Build.VERSION.SDK_INT;
              if (j < 21) {
                break label233;
              }
              try
              {
                localStructStat = Os.stat(((File)localObject2).getPath());
                if (localStructStat.st_atime != 0L)
                {
                  if (localStructStat.st_atime + l2 >= l1) {
                    break label256;
                  }
                  ((File)localObject2).delete();
                }
              }
              catch (Exception localException)
              {
                FileLog.e("tmessages", localException);
              }
            }
            catch (Throwable localThrowable)
            {
              FileLog.e("tmessages", localThrowable);
            }
            if (localStructStat.st_mtime + l2 < l1)
            {
              localException.delete();
              break label256;
              label233:
              if (localException.lastModified() + l2 < l1)
              {
                localException.delete();
                break label256;
                return;
              }
            }
            label256:
            i += 1;
          }
        }
      }
    });
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/ClearCacheService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */