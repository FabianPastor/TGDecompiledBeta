package org.telegram.messenger;

import android.annotation.TargetApi;
import android.content.ComponentName;
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
import java.util.concurrent.CountDownLatch;
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
      Bitmap localBitmap = BitmapFactory.decodeFile(paramFile.toString());
      if (localBitmap == null) {
        break label161;
      }
      paramFile = Bitmap.createBitmap(localBitmap.getWidth(), localBitmap.getHeight(), Bitmap.Config.ARGB_8888);
      paramFile.eraseColor(0);
      Canvas localCanvas = new android/graphics/Canvas;
      localCanvas.<init>(paramFile);
      BitmapShader localBitmapShader = new android/graphics/BitmapShader;
      localBitmapShader.<init>(localBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
      if (this.roundPaint == null)
      {
        Object localObject = new android/graphics/Paint;
        ((Paint)localObject).<init>(1);
        this.roundPaint = ((Paint)localObject);
        localObject = new android/graphics/RectF;
        ((RectF)localObject).<init>();
        this.bitmapRect = ((RectF)localObject);
      }
      this.roundPaint.setShader(localBitmapShader);
      this.bitmapRect.set(0.0F, 0.0F, localBitmap.getWidth(), localBitmap.getHeight());
      localCanvas.drawRoundRect(this.bitmapRect, localBitmap.getWidth(), localBitmap.getHeight(), this.roundPaint);
      paramFile = Icon.createWithBitmap(paramFile);
    }
    catch (Throwable paramFile)
    {
      for (;;)
      {
        FileLog.e(paramFile);
        label161:
        paramFile = null;
      }
    }
    return paramFile;
  }
  
  public List<ChooserTarget> onGetChooserTargets(final ComponentName paramComponentName, final IntentFilter paramIntentFilter)
  {
    final int i = UserConfig.selectedAccount;
    paramComponentName = new ArrayList();
    if (!UserConfig.getInstance(i).isClientActivated()) {}
    for (;;)
    {
      return paramComponentName;
      if (MessagesController.getGlobalMainSettings().getBoolean("direct_share", true))
      {
        ImageLoader.getInstance();
        paramIntentFilter = new CountDownLatch(1);
        final ComponentName localComponentName = new ComponentName(getPackageName(), LaunchActivity.class.getCanonicalName());
        MessagesStorage.getInstance(i).getStorageQueue().postRunnable(new Runnable()
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
                localObject1 = new java/util/ArrayList;
                ((ArrayList)localObject1).<init>();
                ((ArrayList)localObject1).add(Integer.valueOf(UserConfig.getInstance(i).getClientUserId()));
                localObject2 = new java/util/ArrayList;
                ((ArrayList)localObject2).<init>();
                localObject4 = MessagesStorage.getInstance(i).getDatabase().queryFinalized(String.format(Locale.US, "SELECT did FROM dialogs ORDER BY date DESC LIMIT %d,%d", new Object[] { Integer.valueOf(0), Integer.valueOf(30) }), new Object[0]);
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
                if (!((ArrayList)localObject2).isEmpty()) {
                  MessagesStorage.getInstance(i).getChatsInternal(TextUtils.join(",", (Iterable)localObject2), localArrayList2);
                }
                if (!((ArrayList)localObject1).isEmpty()) {
                  MessagesStorage.getInstance(i).getUsersInternal(TextUtils.join(",", (Iterable)localObject1), localArrayList3);
                }
              }
              catch (Exception localException)
              {
                Object localObject2;
                int i;
                Bundle localBundle;
                Object localObject5;
                TLRPC.User localUser;
                Object localObject6;
                int k;
                FileLog.e(localException);
                continue;
                j++;
                continue;
                int j = 0;
                Object localObject1 = localObject6;
                Object localObject3 = localObject5;
                if (j >= localArrayList2.size()) {
                  continue;
                }
                Object localObject4 = (TLRPC.Chat)localArrayList2.get(j);
                if (((TLRPC.Chat)localObject4).id != -k) {
                  continue;
                }
                localObject1 = localObject6;
                localObject3 = localObject5;
                if (ChatObject.isNotInChat((TLRPC.Chat)localObject4)) {
                  continue;
                }
                if (!ChatObject.isChannel((TLRPC.Chat)localObject4)) {
                  continue;
                }
                localObject1 = localObject6;
                localObject3 = localObject5;
                if (!((TLRPC.Chat)localObject4).megagroup) {
                  continue;
                }
                localBundle.putLong("dialogId", k);
                localObject3 = localUser;
                if (((TLRPC.Chat)localObject4).photo == null) {
                  continue;
                }
                localObject3 = localUser;
                if (((TLRPC.Chat)localObject4).photo.photo_small == null) {
                  continue;
                }
                localObject3 = TgChooserTargetService.this.createRoundBitmap(FileLoader.getPathToAttach(((TLRPC.Chat)localObject4).photo.photo_small, true));
                localObject1 = ((TLRPC.Chat)localObject4).title;
                continue;
                j++;
                continue;
                paramIntentFilter.countDown();
              }
              i = 0;
              if (i >= localArrayList1.size()) {
                continue;
              }
              localBundle = new Bundle();
              localObject5 = null;
              localUser = null;
              localObject4 = null;
              localObject6 = null;
              k = ((Integer)localArrayList1.get(i)).intValue();
              if (k <= 0) {
                continue;
              }
              j = 0;
              localObject1 = localObject6;
              localObject2 = localObject5;
              if (j < localArrayList3.size())
              {
                localUser = (TLRPC.User)localArrayList3.get(j);
                if (localUser.id != k) {
                  continue;
                }
                localObject1 = localObject6;
                localObject2 = localObject5;
                if (!localUser.bot)
                {
                  localBundle.putLong("dialogId", k);
                  localObject2 = localObject4;
                  if (localUser.photo != null)
                  {
                    localObject2 = localObject4;
                    if (localUser.photo.photo_small != null) {
                      localObject2 = TgChooserTargetService.this.createRoundBitmap(FileLoader.getPathToAttach(localUser.photo.photo_small, true));
                    }
                  }
                  localObject1 = ContactsController.formatName(localUser.first_name, localUser.last_name);
                }
              }
              if (localObject1 != null)
              {
                localObject4 = localObject2;
                if (localObject2 == null) {
                  localObject4 = Icon.createWithResource(ApplicationLoader.applicationContext, NUM);
                }
                paramComponentName.add(new ChooserTarget((CharSequence)localObject1, (Icon)localObject4, 1.0F, localComponentName, localBundle));
              }
              i++;
              continue;
              j = -i;
              if (!((ArrayList)localObject2).contains(Integer.valueOf(j))) {
                ((ArrayList)localObject2).add(Integer.valueOf(-i));
              }
            }
          }
        });
        try
        {
          paramIntentFilter.await();
        }
        catch (Exception paramIntentFilter)
        {
          FileLog.e(paramIntentFilter);
        }
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/TgChooserTargetService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */