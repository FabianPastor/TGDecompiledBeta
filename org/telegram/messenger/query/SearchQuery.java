package org.telegram.messenger.query;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutInfo.Builder;
import android.content.pm.ShortcutManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Icon;
import android.os.Build.VERSION;
import android.text.TextUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.OpenChatReceiver;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatPhoto;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.TL_contacts_getTopPeers;
import org.telegram.tgnet.TLRPC.TL_contacts_resetTopPeerRating;
import org.telegram.tgnet.TLRPC.TL_contacts_topPeers;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_peerChat;
import org.telegram.tgnet.TLRPC.TL_peerUser;
import org.telegram.tgnet.TLRPC.TL_topPeer;
import org.telegram.tgnet.TLRPC.TL_topPeerCategoryBotsInline;
import org.telegram.tgnet.TLRPC.TL_topPeerCategoryCorrespondents;
import org.telegram.tgnet.TLRPC.TL_topPeerCategoryPeers;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserProfilePhoto;
import org.telegram.ui.LaunchActivity;

public class SearchQuery
{
  private static RectF bitmapRect;
  public static ArrayList<TLRPC.TL_topPeer> hints = new ArrayList();
  public static ArrayList<TLRPC.TL_topPeer> inlineBots = new ArrayList();
  private static boolean loaded;
  private static boolean loading;
  private static Paint roundPaint;
  
  public static void buildShortcuts()
  {
    if (Build.VERSION.SDK_INT < 25) {
      return;
    }
    ArrayList localArrayList = new ArrayList();
    int i = 0;
    for (;;)
    {
      if (i < hints.size())
      {
        localArrayList.add(hints.get(i));
        if (localArrayList.size() != 3) {}
      }
      else
      {
        Utilities.globalQueue.postRunnable(new Runnable()
        {
          @SuppressLint({"NewApi"})
          public void run()
          {
            try
            {
              ShortcutManager localShortcutManager = (ShortcutManager)ApplicationLoader.applicationContext.getSystemService(ShortcutManager.class);
              Object localObject2 = localShortcutManager.getDynamicShortcuts();
              ArrayList localArrayList1 = new ArrayList();
              Object localObject3 = new ArrayList();
              Object localObject1 = new ArrayList();
              int i;
              Object localObject4;
              long l1;
              if ((localObject2 != null) && (!((List)localObject2).isEmpty()))
              {
                ((ArrayList)localObject3).add("compose");
                i = 0;
                if (i >= this.val$hintsFinal.size()) {
                  break label1113;
                }
                localObject4 = (TLRPC.TL_topPeer)this.val$hintsFinal.get(i);
                if (((TLRPC.TL_topPeer)localObject4).peer.user_id != 0) {
                  l1 = ((TLRPC.TL_topPeer)localObject4).peer.user_id;
                }
                for (;;)
                {
                  ((ArrayList)localObject3).add("did" + l1);
                  i += 1;
                  break;
                  long l2 = -((TLRPC.TL_topPeer)localObject4).peer.chat_id;
                  l1 = l2;
                  if (l2 == 0L) {
                    l1 = -((TLRPC.TL_topPeer)localObject4).peer.channel_id;
                  }
                }
                while (i < ((List)localObject2).size())
                {
                  localObject4 = ((ShortcutInfo)((List)localObject2).get(i)).getId();
                  if (!((ArrayList)localObject3).remove(localObject4)) {
                    ((ArrayList)localObject1).add(localObject4);
                  }
                  localArrayList1.add(localObject4);
                  i += 1;
                }
                if ((((ArrayList)localObject3).isEmpty()) && (((ArrayList)localObject1).isEmpty())) {
                  return;
                }
              }
              localObject2 = new Intent(ApplicationLoader.applicationContext, LaunchActivity.class);
              ((Intent)localObject2).setAction("new_dialog");
              ArrayList localArrayList2 = new ArrayList();
              localArrayList2.add(new ShortcutInfo.Builder(ApplicationLoader.applicationContext, "compose").setShortLabel(LocaleController.getString("NewConversationShortcut", NUM)).setLongLabel(LocaleController.getString("NewConversationShortcut", NUM)).setIcon(Icon.createWithResource(ApplicationLoader.applicationContext, NUM)).setIntent((Intent)localObject2).build());
              if (localArrayList1.contains("compose"))
              {
                localShortcutManager.updateShortcuts(localArrayList2);
                label372:
                localArrayList2.clear();
                if (((ArrayList)localObject1).isEmpty()) {
                  break label1119;
                }
                localShortcutManager.removeDynamicShortcuts((List)localObject1);
              }
              for (;;)
              {
                label395:
                Intent localIntent;
                int j;
                if (i < this.val$hintsFinal.size())
                {
                  localIntent = new Intent(ApplicationLoader.applicationContext, OpenChatReceiver.class);
                  localObject2 = (TLRPC.TL_topPeer)this.val$hintsFinal.get(i);
                  localObject4 = null;
                  localObject1 = null;
                  if (((TLRPC.TL_topPeer)localObject2).peer.user_id != 0)
                  {
                    localIntent.putExtra("userId", ((TLRPC.TL_topPeer)localObject2).peer.user_id);
                    localObject4 = MessagesController.getInstance().getUser(Integer.valueOf(((TLRPC.TL_topPeer)localObject2).peer.user_id));
                    l1 = ((TLRPC.TL_topPeer)localObject2).peer.user_id;
                    break label1124;
                    localShortcutManager.addDynamicShortcuts(localArrayList2);
                    break label372;
                  }
                  int k = ((TLRPC.TL_topPeer)localObject2).peer.chat_id;
                  j = k;
                  if (k == 0) {
                    j = ((TLRPC.TL_topPeer)localObject2).peer.channel_id;
                  }
                  localObject1 = MessagesController.getInstance().getChat(Integer.valueOf(j));
                  localIntent.putExtra("chatId", j);
                  l1 = -j;
                }
                label912:
                label996:
                label1097:
                label1113:
                label1119:
                label1124:
                while ((localObject4 != null) || (localException != null))
                {
                  localObject3 = null;
                  if (localObject4 != null)
                  {
                    localObject1 = ContactsController.formatName(((TLRPC.User)localObject4).first_name, ((TLRPC.User)localObject4).last_name);
                    localObject2 = localObject1;
                    if (((TLRPC.User)localObject4).photo != null)
                    {
                      localObject3 = ((TLRPC.User)localObject4).photo.photo_small;
                      localObject2 = localObject1;
                    }
                  }
                  for (;;)
                  {
                    localIntent.setAction("com.tmessages.openchat" + l1);
                    localIntent.addFlags(67108864);
                    localObject4 = null;
                    localObject1 = null;
                    if (localObject3 != null) {
                      localObject1 = localObject4;
                    }
                    try
                    {
                      localObject3 = BitmapFactory.decodeFile(FileLoader.getPathToAttach((TLObject)localObject3, true).toString());
                      localObject1 = localObject3;
                      if (localObject3 != null)
                      {
                        localObject1 = localObject3;
                        j = AndroidUtilities.dp(48.0F);
                        localObject1 = localObject3;
                        localObject4 = Bitmap.createBitmap(j, j, Bitmap.Config.ARGB_8888);
                        localObject1 = localObject3;
                        ((Bitmap)localObject4).eraseColor(0);
                        localObject1 = localObject3;
                        localCanvas = new Canvas((Bitmap)localObject4);
                        localObject1 = localObject3;
                        BitmapShader localBitmapShader = new BitmapShader((Bitmap)localObject3, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                        localObject1 = localObject3;
                        if (SearchQuery.roundPaint == null)
                        {
                          localObject1 = localObject3;
                          SearchQuery.access$002(new Paint(1));
                          localObject1 = localObject3;
                          SearchQuery.access$102(new RectF());
                        }
                        localObject1 = localObject3;
                        float f = j / ((Bitmap)localObject3).getWidth();
                        localObject1 = localObject3;
                        localCanvas.scale(f, f);
                        localObject1 = localObject3;
                        SearchQuery.roundPaint.setShader(localBitmapShader);
                        localObject1 = localObject3;
                        SearchQuery.bitmapRect.set(AndroidUtilities.dp(2.0F), AndroidUtilities.dp(2.0F), AndroidUtilities.dp(46.0F), AndroidUtilities.dp(46.0F));
                        localObject1 = localObject3;
                        localCanvas.drawRoundRect(SearchQuery.bitmapRect, ((Bitmap)localObject3).getWidth(), ((Bitmap)localObject3).getHeight(), SearchQuery.roundPaint);
                        localObject1 = localObject3;
                      }
                    }
                    catch (Throwable localThrowable2)
                    {
                      try
                      {
                        Canvas localCanvas;
                        localCanvas.setBitmap(null);
                        localObject1 = localObject4;
                        localObject4 = "did" + l1;
                        localObject3 = localObject2;
                        if (TextUtils.isEmpty((CharSequence)localObject2)) {
                          localObject3 = " ";
                        }
                        localObject2 = new ShortcutInfo.Builder(ApplicationLoader.applicationContext, (String)localObject4).setShortLabel((CharSequence)localObject3).setLongLabel((CharSequence)localObject3).setIntent(localIntent);
                        if (localObject1 != null)
                        {
                          ((ShortcutInfo.Builder)localObject2).setIcon(Icon.createWithBitmap((Bitmap)localObject1));
                          localArrayList2.add(((ShortcutInfo.Builder)localObject2).build());
                          if (!localArrayList1.contains(localObject4)) {
                            break label1097;
                          }
                          localShortcutManager.updateShortcuts(localArrayList2);
                        }
                        for (;;)
                        {
                          localArrayList2.clear();
                          break label1134;
                          localObject4 = ((TLRPC.Chat)localObject1).title;
                          localObject2 = localObject4;
                          if (((TLRPC.Chat)localObject1).photo == null) {
                            break;
                          }
                          localObject3 = ((TLRPC.Chat)localObject1).photo.photo_small;
                          localObject2 = localObject4;
                          break;
                          localThrowable2 = localThrowable2;
                          FileLog.e(localThrowable2);
                          break label912;
                          ((ShortcutInfo.Builder)localObject2).setIcon(Icon.createWithResource(ApplicationLoader.applicationContext, NUM));
                          break label996;
                          localShortcutManager.addDynamicShortcuts(localArrayList2);
                        }
                      }
                      catch (Exception localException)
                      {
                        for (;;) {}
                      }
                    }
                  }
                  i = 0;
                  break;
                  return;
                  i = 0;
                  break label395;
                }
                label1134:
                i += 1;
              }
              return;
            }
            catch (Throwable localThrowable1) {}
          }
        });
        return;
      }
      i += 1;
    }
  }
  
  public static void cleanup()
  {
    loading = false;
    loaded = false;
    hints.clear();
    inlineBots.clear();
    NotificationCenter.getInstance().postNotificationName(NotificationCenter.reloadHints, new Object[0]);
    NotificationCenter.getInstance().postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
  }
  
  private static void deletePeer(int paramInt1, final int paramInt2)
  {
    MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
    {
      public void run()
      {
        try
        {
          MessagesStorage.getInstance().getDatabase().executeFast(String.format(Locale.US, "DELETE FROM chat_hints WHERE did = %d AND type = %d", new Object[] { Integer.valueOf(this.val$did), Integer.valueOf(paramInt2) })).stepThis().dispose();
          return;
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
        }
      }
    });
  }
  
  public static void increaseInlineRaiting(int paramInt)
  {
    int i;
    Object localObject2;
    int j;
    if (UserConfig.botRatingLoadTime != 0)
    {
      i = Math.max(1, (int)(System.currentTimeMillis() / 1000L) - UserConfig.botRatingLoadTime);
      localObject2 = null;
      j = 0;
    }
    for (;;)
    {
      Object localObject1 = localObject2;
      if (j < inlineBots.size())
      {
        localObject1 = (TLRPC.TL_topPeer)inlineBots.get(j);
        if (((TLRPC.TL_topPeer)localObject1).peer.user_id != paramInt) {}
      }
      else
      {
        localObject2 = localObject1;
        if (localObject1 == null)
        {
          localObject2 = new TLRPC.TL_topPeer();
          ((TLRPC.TL_topPeer)localObject2).peer = new TLRPC.TL_peerUser();
          ((TLRPC.TL_topPeer)localObject2).peer.user_id = paramInt;
          inlineBots.add(localObject2);
        }
        ((TLRPC.TL_topPeer)localObject2).rating += Math.exp(i / MessagesController.getInstance().ratingDecay);
        Collections.sort(inlineBots, new Comparator()
        {
          public int compare(TLRPC.TL_topPeer paramAnonymousTL_topPeer1, TLRPC.TL_topPeer paramAnonymousTL_topPeer2)
          {
            if (paramAnonymousTL_topPeer1.rating > paramAnonymousTL_topPeer2.rating) {
              return -1;
            }
            if (paramAnonymousTL_topPeer1.rating < paramAnonymousTL_topPeer2.rating) {
              return 1;
            }
            return 0;
          }
        });
        if (inlineBots.size() > 20) {
          inlineBots.remove(inlineBots.size() - 1);
        }
        savePeer(paramInt, 1, ((TLRPC.TL_topPeer)localObject2).rating);
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
        return;
        i = 60;
        break;
      }
      j += 1;
    }
  }
  
  public static void increasePeerRaiting(long paramLong)
  {
    int i = (int)paramLong;
    if (i <= 0) {}
    for (;;)
    {
      return;
      if (i > 0) {}
      for (TLRPC.User localUser = MessagesController.getInstance().getUser(Integer.valueOf(i)); (localUser != null) && (!localUser.bot); localUser = null)
      {
        MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
        {
          public void run()
          {
            d2 = 0.0D;
            int i = 0;
            int j = 0;
            try
            {
              SQLiteCursor localSQLiteCursor = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT MAX(mid), MAX(date) FROM messages WHERE uid = %d AND out = 1", new Object[] { Long.valueOf(this.val$did) }), new Object[0]);
              if (localSQLiteCursor.next())
              {
                j = localSQLiteCursor.intValue(0);
                i = localSQLiteCursor.intValue(1);
              }
              localSQLiteCursor.dispose();
              d1 = d2;
              if (j > 0)
              {
                d1 = d2;
                if (UserConfig.ratingLoadTime != 0)
                {
                  j = UserConfig.ratingLoadTime;
                  d1 = i - j;
                }
              }
            }
            catch (Exception localException)
            {
              for (;;)
              {
                FileLog.e(localException);
                final double d1 = d2;
              }
            }
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                Object localObject2 = null;
                int i = 0;
                Object localObject1 = localObject2;
                if (i < SearchQuery.hints.size())
                {
                  localObject1 = (TLRPC.TL_topPeer)SearchQuery.hints.get(i);
                  if (((SearchQuery.7.this.val$lower_id >= 0) || ((((TLRPC.TL_topPeer)localObject1).peer.chat_id != -SearchQuery.7.this.val$lower_id) && (((TLRPC.TL_topPeer)localObject1).peer.channel_id != -SearchQuery.7.this.val$lower_id))) && ((SearchQuery.7.this.val$lower_id <= 0) || (((TLRPC.TL_topPeer)localObject1).peer.user_id != SearchQuery.7.this.val$lower_id))) {}
                }
                else
                {
                  localObject2 = localObject1;
                  if (localObject1 == null)
                  {
                    localObject2 = new TLRPC.TL_topPeer();
                    if (SearchQuery.7.this.val$lower_id <= 0) {
                      break label232;
                    }
                    ((TLRPC.TL_topPeer)localObject2).peer = new TLRPC.TL_peerUser();
                    ((TLRPC.TL_topPeer)localObject2).peer.user_id = SearchQuery.7.this.val$lower_id;
                  }
                }
                for (;;)
                {
                  SearchQuery.hints.add(localObject2);
                  ((TLRPC.TL_topPeer)localObject2).rating += Math.exp(d1 / MessagesController.getInstance().ratingDecay);
                  Collections.sort(SearchQuery.hints, new Comparator()
                  {
                    public int compare(TLRPC.TL_topPeer paramAnonymous3TL_topPeer1, TLRPC.TL_topPeer paramAnonymous3TL_topPeer2)
                    {
                      if (paramAnonymous3TL_topPeer1.rating > paramAnonymous3TL_topPeer2.rating) {
                        return -1;
                      }
                      if (paramAnonymous3TL_topPeer1.rating < paramAnonymous3TL_topPeer2.rating) {
                        return 1;
                      }
                      return 0;
                    }
                  });
                  SearchQuery.savePeer((int)SearchQuery.7.this.val$did, 0, ((TLRPC.TL_topPeer)localObject2).rating);
                  NotificationCenter.getInstance().postNotificationName(NotificationCenter.reloadHints, new Object[0]);
                  return;
                  i += 1;
                  break;
                  label232:
                  ((TLRPC.TL_topPeer)localObject2).peer = new TLRPC.TL_peerChat();
                  ((TLRPC.TL_topPeer)localObject2).peer.chat_id = (-SearchQuery.7.this.val$lower_id);
                }
              }
            });
          }
        });
        return;
      }
    }
  }
  
  public static void loadHints(boolean paramBoolean)
  {
    if (loading) {}
    do
    {
      return;
      if (!paramBoolean) {
        break;
      }
    } while (loaded);
    loading = true;
    MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
    {
      public void run()
      {
        ArrayList localArrayList1 = new ArrayList();
        final ArrayList localArrayList2 = new ArrayList();
        final ArrayList localArrayList3 = new ArrayList();
        final ArrayList localArrayList4 = new ArrayList();
        int i = UserConfig.getClientUserId();
        ArrayList localArrayList5;
        ArrayList localArrayList6;
        SQLiteCursor localSQLiteCursor;
        for (;;)
        {
          int j;
          int k;
          TLRPC.TL_topPeer localTL_topPeer;
          try
          {
            localArrayList5 = new ArrayList();
            localArrayList6 = new ArrayList();
            localSQLiteCursor = MessagesStorage.getInstance().getDatabase().queryFinalized("SELECT did, type, rating FROM chat_hints WHERE 1 ORDER BY rating DESC", new Object[0]);
            if (!localSQLiteCursor.next()) {
              break;
            }
            j = localSQLiteCursor.intValue(0);
            if (j == i) {
              continue;
            }
            k = localSQLiteCursor.intValue(1);
            localTL_topPeer = new TLRPC.TL_topPeer();
            localTL_topPeer.rating = localSQLiteCursor.doubleValue(2);
            if (j > 0)
            {
              localTL_topPeer.peer = new TLRPC.TL_peerUser();
              localTL_topPeer.peer.user_id = j;
              localArrayList5.add(Integer.valueOf(j));
              if (k != 0) {
                break label216;
              }
              localArrayList1.add(localTL_topPeer);
              continue;
            }
            localTL_topPeer.peer = new TLRPC.TL_peerChat();
          }
          catch (Exception localException)
          {
            FileLog.e(localException);
            return;
          }
          localTL_topPeer.peer.chat_id = (-j);
          localArrayList6.add(Integer.valueOf(-j));
          continue;
          label216:
          if (k == 1) {
            localArrayList2.add(localTL_topPeer);
          }
        }
        localSQLiteCursor.dispose();
        if (!localArrayList5.isEmpty()) {
          MessagesStorage.getInstance().getUsersInternal(TextUtils.join(",", localArrayList5), localArrayList3);
        }
        if (!localArrayList6.isEmpty()) {
          MessagesStorage.getInstance().getChatsInternal(TextUtils.join(",", localArrayList6), localArrayList4);
        }
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            MessagesController.getInstance().putUsers(localArrayList3, true);
            MessagesController.getInstance().putChats(localArrayList4, true);
            SearchQuery.access$202(false);
            SearchQuery.access$302(true);
            SearchQuery.hints = localException;
            SearchQuery.inlineBots = localArrayList2;
            SearchQuery.buildShortcuts();
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.reloadHints, new Object[0]);
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
            if (Math.abs(UserConfig.lastHintsSyncTime - (int)(System.currentTimeMillis() / 1000L)) >= 86400) {
              SearchQuery.loadHints(false);
            }
          }
        });
      }
    });
    loaded = true;
    return;
    loading = true;
    TLRPC.TL_contacts_getTopPeers localTL_contacts_getTopPeers = new TLRPC.TL_contacts_getTopPeers();
    localTL_contacts_getTopPeers.hash = 0;
    localTL_contacts_getTopPeers.bots_pm = false;
    localTL_contacts_getTopPeers.correspondents = true;
    localTL_contacts_getTopPeers.groups = false;
    localTL_contacts_getTopPeers.channels = false;
    localTL_contacts_getTopPeers.bots_inline = true;
    localTL_contacts_getTopPeers.offset = 0;
    localTL_contacts_getTopPeers.limit = 20;
    ConnectionsManager.getInstance().sendRequest(localTL_contacts_getTopPeers, new RequestDelegate()
    {
      public void run(final TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
      {
        if ((paramAnonymousTLObject instanceof TLRPC.TL_contacts_topPeers)) {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              final TLRPC.TL_contacts_topPeers localTL_contacts_topPeers = (TLRPC.TL_contacts_topPeers)paramAnonymousTLObject;
              MessagesController.getInstance().putUsers(localTL_contacts_topPeers.users, false);
              MessagesController.getInstance().putChats(localTL_contacts_topPeers.chats, false);
              int i = 0;
              while (i < localTL_contacts_topPeers.categories.size())
              {
                TLRPC.TL_topPeerCategoryPeers localTL_topPeerCategoryPeers = (TLRPC.TL_topPeerCategoryPeers)localTL_contacts_topPeers.categories.get(i);
                if ((localTL_topPeerCategoryPeers.category instanceof TLRPC.TL_topPeerCategoryBotsInline))
                {
                  SearchQuery.inlineBots = localTL_topPeerCategoryPeers.peers;
                  UserConfig.botRatingLoadTime = (int)(System.currentTimeMillis() / 1000L);
                  i += 1;
                }
                else
                {
                  SearchQuery.hints = localTL_topPeerCategoryPeers.peers;
                  int k = UserConfig.getClientUserId();
                  int j = 0;
                  for (;;)
                  {
                    if (j < SearchQuery.hints.size())
                    {
                      if (((TLRPC.TL_topPeer)SearchQuery.hints.get(j)).peer.user_id == k) {
                        SearchQuery.hints.remove(j);
                      }
                    }
                    else
                    {
                      UserConfig.ratingLoadTime = (int)(System.currentTimeMillis() / 1000L);
                      break;
                    }
                    j += 1;
                  }
                }
              }
              UserConfig.saveConfig(false);
              SearchQuery.buildShortcuts();
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.reloadHints, new Object[0]);
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
              MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
              {
                public void run()
                {
                  for (;;)
                  {
                    int j;
                    int k;
                    int m;
                    try
                    {
                      MessagesStorage.getInstance().getDatabase().executeFast("DELETE FROM chat_hints WHERE 1").stepThis().dispose();
                      MessagesStorage.getInstance().getDatabase().beginTransaction();
                      MessagesStorage.getInstance().putUsersAndChats(localTL_contacts_topPeers.users, localTL_contacts_topPeers.chats, false, false);
                      SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.getInstance().getDatabase().executeFast("REPLACE INTO chat_hints VALUES(?, ?, ?, ?)");
                      j = 0;
                      if (j < localTL_contacts_topPeers.categories.size())
                      {
                        TLRPC.TL_topPeerCategoryPeers localTL_topPeerCategoryPeers = (TLRPC.TL_topPeerCategoryPeers)localTL_contacts_topPeers.categories.get(j);
                        if (!(localTL_topPeerCategoryPeers.category instanceof TLRPC.TL_topPeerCategoryBotsInline)) {
                          break label286;
                        }
                        k = 1;
                        break label280;
                        if (m >= localTL_topPeerCategoryPeers.peers.size()) {
                          break label291;
                        }
                        TLRPC.TL_topPeer localTL_topPeer = (TLRPC.TL_topPeer)localTL_topPeerCategoryPeers.peers.get(m);
                        if ((localTL_topPeer.peer instanceof TLRPC.TL_peerUser))
                        {
                          i = localTL_topPeer.peer.user_id;
                          localSQLitePreparedStatement.requery();
                          localSQLitePreparedStatement.bindInteger(1, i);
                          localSQLitePreparedStatement.bindInteger(2, k);
                          localSQLitePreparedStatement.bindDouble(3, localTL_topPeer.rating);
                          localSQLitePreparedStatement.bindInteger(4, 0);
                          localSQLitePreparedStatement.step();
                          m += 1;
                          continue;
                        }
                        if ((localTL_topPeer.peer instanceof TLRPC.TL_peerChat))
                        {
                          i = -localTL_topPeer.peer.chat_id;
                          continue;
                        }
                        int i = -localTL_topPeer.peer.channel_id;
                        continue;
                      }
                      else
                      {
                        localSQLitePreparedStatement.dispose();
                        MessagesStorage.getInstance().getDatabase().commitTransaction();
                        AndroidUtilities.runOnUIThread(new Runnable()
                        {
                          public void run()
                          {
                            UserConfig.lastHintsSyncTime = (int)(System.currentTimeMillis() / 1000L);
                            UserConfig.saveConfig(false);
                          }
                        });
                        return;
                      }
                    }
                    catch (Exception localException)
                    {
                      FileLog.e(localException);
                      return;
                    }
                    for (;;)
                    {
                      label280:
                      m = 0;
                      break;
                      label286:
                      k = 0;
                    }
                    label291:
                    j += 1;
                  }
                }
              });
            }
          });
        }
      }
    });
  }
  
  public static void removeInline(int paramInt)
  {
    int i = 0;
    for (;;)
    {
      if (i < inlineBots.size())
      {
        if (((TLRPC.TL_topPeer)inlineBots.get(i)).peer.user_id == paramInt)
        {
          inlineBots.remove(i);
          TLRPC.TL_contacts_resetTopPeerRating localTL_contacts_resetTopPeerRating = new TLRPC.TL_contacts_resetTopPeerRating();
          localTL_contacts_resetTopPeerRating.category = new TLRPC.TL_topPeerCategoryBotsInline();
          localTL_contacts_resetTopPeerRating.peer = MessagesController.getInputPeer(paramInt);
          ConnectionsManager.getInstance().sendRequest(localTL_contacts_resetTopPeerRating, new RequestDelegate()
          {
            public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error) {}
          });
          deletePeer(paramInt, 1);
          NotificationCenter.getInstance().postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
        }
      }
      else {
        return;
      }
      i += 1;
    }
  }
  
  public static void removePeer(int paramInt)
  {
    int i = 0;
    for (;;)
    {
      if (i < hints.size())
      {
        if (((TLRPC.TL_topPeer)hints.get(i)).peer.user_id == paramInt)
        {
          hints.remove(i);
          NotificationCenter.getInstance().postNotificationName(NotificationCenter.reloadHints, new Object[0]);
          TLRPC.TL_contacts_resetTopPeerRating localTL_contacts_resetTopPeerRating = new TLRPC.TL_contacts_resetTopPeerRating();
          localTL_contacts_resetTopPeerRating.category = new TLRPC.TL_topPeerCategoryCorrespondents();
          localTL_contacts_resetTopPeerRating.peer = MessagesController.getInputPeer(paramInt);
          deletePeer(paramInt, 0);
          ConnectionsManager.getInstance().sendRequest(localTL_contacts_resetTopPeerRating, new RequestDelegate()
          {
            public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error) {}
          });
        }
      }
      else {
        return;
      }
      i += 1;
    }
  }
  
  private static void savePeer(int paramInt1, final int paramInt2, final double paramDouble)
  {
    MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
    {
      public void run()
      {
        try
        {
          SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.getInstance().getDatabase().executeFast("REPLACE INTO chat_hints VALUES(?, ?, ?, ?)");
          localSQLitePreparedStatement.requery();
          localSQLitePreparedStatement.bindInteger(1, this.val$did);
          localSQLitePreparedStatement.bindInteger(2, paramInt2);
          localSQLitePreparedStatement.bindDouble(3, paramDouble);
          localSQLitePreparedStatement.bindInteger(4, (int)System.currentTimeMillis() / 1000);
          localSQLitePreparedStatement.step();
          localSQLitePreparedStatement.dispose();
          return;
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
        }
      }
    });
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/query/SearchQuery.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */