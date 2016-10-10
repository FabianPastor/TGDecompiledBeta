package org.telegram.messenger;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.service.chooser.ChooserTarget;
import android.service.chooser.ChooserTargetService;
import android.text.TextUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Semaphore;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatPhoto;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserProfilePhoto;
import org.telegram.ui.LaunchActivity;

@TargetApi(23)
public class TgChooserTargetService
  extends ChooserTargetService
{
  private RectF bitmapRect;
  private Paint roundPaint;
  
  private Icon createRoundBitmap(File paramFile)
  {
    try
    {
      paramFile = BitmapFactory.decodeFile(paramFile.toString());
      if (paramFile != null)
      {
        Bitmap localBitmap = Bitmap.createBitmap(paramFile.getWidth(), paramFile.getHeight(), Bitmap.Config.ARGB_8888);
        localBitmap.eraseColor(0);
        Canvas localCanvas = new Canvas(localBitmap);
        BitmapShader localBitmapShader = new BitmapShader(paramFile, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        if (this.roundPaint == null)
        {
          this.roundPaint = new Paint(1);
          this.bitmapRect = new RectF();
        }
        this.roundPaint.setShader(localBitmapShader);
        this.bitmapRect.set(0.0F, 0.0F, paramFile.getWidth(), paramFile.getHeight());
        localCanvas.drawRoundRect(this.bitmapRect, paramFile.getWidth(), paramFile.getHeight(), this.roundPaint);
        paramFile = Icon.createWithBitmap(localBitmap);
        return paramFile;
      }
    }
    catch (Throwable paramFile)
    {
      FileLog.e("tmessages", paramFile);
    }
    return null;
  }
  
  public List<ChooserTarget> onGetChooserTargets(final ComponentName paramComponentName, final IntentFilter paramIntentFilter)
  {
    paramComponentName = new ArrayList();
    if (!UserConfig.isClientActivated()) {}
    while (!ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).getBoolean("direct_share", true)) {
      return paramComponentName;
    }
    ImageLoader.getInstance();
    paramIntentFilter = new Semaphore(0);
    final ComponentName localComponentName = new ComponentName(getPackageName(), LaunchActivity.class.getCanonicalName());
    MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
    {
      public void run()
      {
        ArrayList localArrayList1 = new ArrayList();
        ArrayList localArrayList2 = new ArrayList();
        ArrayList localArrayList3 = new ArrayList();
        for (;;)
        {
          try
          {
            localObject1 = new ArrayList();
            ((ArrayList)localObject1).add(Integer.valueOf(UserConfig.getClientUserId()));
            localObject3 = new ArrayList();
            localObject4 = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT did FROM dialogs ORDER BY date DESC LIMIT %d,%d", new Object[] { Integer.valueOf(0), Integer.valueOf(30) }), new Object[0]);
            if (((SQLiteCursor)localObject4).next())
            {
              long l = ((SQLiteCursor)localObject4).longValue(0);
              i = (int)l;
              j = (int)(l >> 32);
              if ((i == 0) || (j == 1)) {
                continue;
              }
              if (i <= 0) {
                continue;
              }
              if (!((ArrayList)localObject1).contains(Integer.valueOf(i))) {
                ((ArrayList)localObject1).add(Integer.valueOf(i));
              }
              localArrayList1.add(Integer.valueOf(i));
              if (localArrayList1.size() != 8) {
                continue;
              }
            }
            ((SQLiteCursor)localObject4).dispose();
            if (!((ArrayList)localObject3).isEmpty()) {
              MessagesStorage.getInstance().getChatsInternal(TextUtils.join(",", (Iterable)localObject3), localArrayList2);
            }
            if (!((ArrayList)localObject1).isEmpty()) {
              MessagesStorage.getInstance().getUsersInternal(TextUtils.join(",", (Iterable)localObject1), localArrayList3);
            }
          }
          catch (Exception localException)
          {
            Object localObject1;
            int i;
            Bundle localBundle;
            Object localObject6;
            TLRPC.User localUser;
            Object localObject5;
            int k;
            FileLog.e("tmessages", localException);
            continue;
            j += 1;
            continue;
            int j = 0;
            Object localObject3 = localObject5;
            Object localObject2 = localObject6;
            if (j >= localArrayList2.size()) {
              continue;
            }
            Object localObject4 = (TLRPC.Chat)localArrayList2.get(j);
            if (((TLRPC.Chat)localObject4).id != -k) {
              continue;
            }
            localObject3 = localObject5;
            localObject2 = localObject6;
            if (ChatObject.isNotInChat((TLRPC.Chat)localObject4)) {
              continue;
            }
            if (!ChatObject.isChannel((TLRPC.Chat)localObject4)) {
              continue;
            }
            localObject3 = localObject5;
            localObject2 = localObject6;
            if (!((TLRPC.Chat)localObject4).megagroup) {
              continue;
            }
            localBundle.putLong("dialogId", k);
            localObject2 = localUser;
            if (((TLRPC.Chat)localObject4).photo == null) {
              continue;
            }
            localObject2 = localUser;
            if (((TLRPC.Chat)localObject4).photo.photo_small == null) {
              continue;
            }
            localObject2 = TgChooserTargetService.this.createRoundBitmap(FileLoader.getPathToAttach(((TLRPC.Chat)localObject4).photo.photo_small, true));
            localObject3 = ((TLRPC.Chat)localObject4).title;
            continue;
            j += 1;
            continue;
            paramIntentFilter.release();
          }
          i = 0;
          if (i >= localArrayList1.size()) {
            continue;
          }
          localBundle = new Bundle();
          localObject6 = null;
          localUser = null;
          localObject4 = null;
          localObject5 = null;
          k = ((Integer)localArrayList1.get(i)).intValue();
          if (k <= 0) {
            continue;
          }
          j = 0;
          localObject3 = localObject5;
          localObject1 = localObject6;
          if (j < localArrayList3.size())
          {
            localUser = (TLRPC.User)localArrayList3.get(j);
            if (localUser.id != k) {
              continue;
            }
            localObject3 = localObject5;
            localObject1 = localObject6;
            if (!localUser.bot)
            {
              localBundle.putLong("dialogId", k);
              localObject1 = localObject4;
              if (localUser.photo != null)
              {
                localObject1 = localObject4;
                if (localUser.photo.photo_small != null) {
                  localObject1 = TgChooserTargetService.this.createRoundBitmap(FileLoader.getPathToAttach(localUser.photo.photo_small, true));
                }
              }
              localObject3 = ContactsController.formatName(localUser.first_name, localUser.last_name);
            }
          }
          if (localObject3 != null)
          {
            localObject4 = localObject1;
            if (localObject1 == null) {
              localObject4 = Icon.createWithResource(ApplicationLoader.applicationContext, 2130837809);
            }
            paramComponentName.add(new ChooserTarget((CharSequence)localObject3, (Icon)localObject4, 1.0F, localComponentName, localBundle));
          }
          i += 1;
          continue;
          j = -i;
          if (!((ArrayList)localObject3).contains(Integer.valueOf(j))) {
            ((ArrayList)localObject3).add(Integer.valueOf(-i));
          }
        }
      }
    });
    try
    {
      paramIntentFilter.acquire();
      return paramComponentName;
    }
    catch (Exception paramIntentFilter)
    {
      FileLog.e("tmessages", paramIntentFilter);
    }
    return paramComponentName;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/TgChooserTargetService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */