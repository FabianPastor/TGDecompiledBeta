package org.telegram.messenger.query;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.InputStickerSet;
import org.telegram.tgnet.TLRPC.StickerSet;
import org.telegram.tgnet.TLRPC.StickerSetCovered;
import org.telegram.tgnet.TLRPC.TL_documentAttributeSticker;
import org.telegram.tgnet.TLRPC.TL_documentEmpty;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputDocument;
import org.telegram.tgnet.TLRPC.TL_inputStickerSetID;
import org.telegram.tgnet.TLRPC.TL_messages_allStickers;
import org.telegram.tgnet.TLRPC.TL_messages_featuredStickers;
import org.telegram.tgnet.TLRPC.TL_messages_getAllStickers;
import org.telegram.tgnet.TLRPC.TL_messages_getFeaturedStickers;
import org.telegram.tgnet.TLRPC.TL_messages_getMaskStickers;
import org.telegram.tgnet.TLRPC.TL_messages_getRecentStickers;
import org.telegram.tgnet.TLRPC.TL_messages_getSavedGifs;
import org.telegram.tgnet.TLRPC.TL_messages_getStickerSet;
import org.telegram.tgnet.TLRPC.TL_messages_installStickerSet;
import org.telegram.tgnet.TLRPC.TL_messages_readFeaturedStickers;
import org.telegram.tgnet.TLRPC.TL_messages_recentStickers;
import org.telegram.tgnet.TLRPC.TL_messages_saveGif;
import org.telegram.tgnet.TLRPC.TL_messages_savedGifs;
import org.telegram.tgnet.TLRPC.TL_messages_stickerSet;
import org.telegram.tgnet.TLRPC.TL_messages_stickerSetInstallResultArchive;
import org.telegram.tgnet.TLRPC.TL_messages_uninstallStickerSet;
import org.telegram.tgnet.TLRPC.TL_stickerPack;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.StickersArchiveAlert;

public class StickersQuery
{
  public static final int TYPE_IMAGE = 0;
  public static final int TYPE_MASK = 1;
  private static HashMap<String, ArrayList<TLRPC.Document>> allStickers;
  private static ArrayList<TLRPC.StickerSetCovered> featuredStickerSets;
  private static HashMap<Long, TLRPC.StickerSetCovered> featuredStickerSetsById;
  private static boolean featuredStickersLoaded;
  private static int[] loadDate;
  private static int loadFeaturedDate;
  private static int loadFeaturedHash;
  private static int[] loadHash;
  private static boolean loadingFeaturedStickers;
  private static boolean loadingRecentGifs;
  private static boolean[] loadingRecentStickers;
  private static boolean[] loadingStickers;
  private static ArrayList<Long> readingStickerSets = new ArrayList();
  private static ArrayList<TLRPC.Document> recentGifs;
  private static boolean recentGifsLoaded;
  private static ArrayList<TLRPC.Document>[] recentStickers;
  private static boolean[] recentStickersLoaded;
  private static ArrayList<TLRPC.TL_messages_stickerSet>[] stickerSets = { new ArrayList(), new ArrayList() };
  private static HashMap<Long, TLRPC.TL_messages_stickerSet> stickerSetsById = new HashMap();
  private static HashMap<String, TLRPC.TL_messages_stickerSet> stickerSetsByName = new HashMap();
  private static HashMap<Long, String> stickersByEmoji;
  private static boolean[] stickersLoaded;
  private static ArrayList<Long> unreadStickerSets;
  
  static
  {
    loadingStickers = new boolean[2];
    stickersLoaded = new boolean[2];
    loadHash = new int[2];
    loadDate = new int[2];
    stickersByEmoji = new HashMap();
    allStickers = new HashMap();
    recentStickers = new ArrayList[] { new ArrayList(), new ArrayList() };
    loadingRecentStickers = new boolean[2];
    recentStickersLoaded = new boolean[2];
    recentGifs = new ArrayList();
    featuredStickerSets = new ArrayList();
    featuredStickerSetsById = new HashMap();
    unreadStickerSets = new ArrayList();
  }
  
  public static void addNewStickerSet(TLRPC.TL_messages_stickerSet paramTL_messages_stickerSet)
  {
    if ((stickerSetsById.containsKey(Long.valueOf(paramTL_messages_stickerSet.set.id))) || (stickerSetsByName.containsKey(paramTL_messages_stickerSet.set.short_name))) {
      return;
    }
    if (paramTL_messages_stickerSet.set.masks) {}
    HashMap localHashMap;
    Object localObject1;
    for (int i = 1;; i = 0)
    {
      stickerSets[i].add(0, paramTL_messages_stickerSet);
      stickerSetsById.put(Long.valueOf(paramTL_messages_stickerSet.set.id), paramTL_messages_stickerSet);
      stickerSetsByName.put(paramTL_messages_stickerSet.set.short_name, paramTL_messages_stickerSet);
      localHashMap = new HashMap();
      j = 0;
      while (j < paramTL_messages_stickerSet.documents.size())
      {
        localObject1 = (TLRPC.Document)paramTL_messages_stickerSet.documents.get(j);
        localHashMap.put(Long.valueOf(((TLRPC.Document)localObject1).id), localObject1);
        j += 1;
      }
    }
    int j = 0;
    while (j < paramTL_messages_stickerSet.packs.size())
    {
      TLRPC.TL_stickerPack localTL_stickerPack = (TLRPC.TL_stickerPack)paramTL_messages_stickerSet.packs.get(j);
      localTL_stickerPack.emoticon = localTL_stickerPack.emoticon.replace("️", "");
      Object localObject2 = (ArrayList)allStickers.get(localTL_stickerPack.emoticon);
      localObject1 = localObject2;
      if (localObject2 == null)
      {
        localObject1 = new ArrayList();
        allStickers.put(localTL_stickerPack.emoticon, localObject1);
      }
      int k = 0;
      while (k < localTL_stickerPack.documents.size())
      {
        localObject2 = (Long)localTL_stickerPack.documents.get(k);
        if (!stickersByEmoji.containsKey(localObject2)) {
          stickersByEmoji.put(localObject2, localTL_stickerPack.emoticon);
        }
        localObject2 = (TLRPC.Document)localHashMap.get(localObject2);
        if (localObject2 != null) {
          ((ArrayList)localObject1).add(localObject2);
        }
        k += 1;
      }
      j += 1;
    }
    loadHash[i] = calcStickersHash(stickerSets[i]);
    NotificationCenter.getInstance().postNotificationName(NotificationCenter.stickersDidLoaded, new Object[] { Integer.valueOf(i) });
    loadStickers(i, false, true);
  }
  
  public static void addRecentGif(TLRPC.Document paramDocument, int paramInt)
  {
    int j = 0;
    int i = 0;
    while (i < recentGifs.size())
    {
      localObject = (TLRPC.Document)recentGifs.get(i);
      if (((TLRPC.Document)localObject).id == paramDocument.id)
      {
        recentGifs.remove(i);
        recentGifs.add(0, localObject);
        j = 1;
      }
      i += 1;
    }
    if (j == 0) {
      recentGifs.add(0, paramDocument);
    }
    if (recentGifs.size() > MessagesController.getInstance().maxRecentGifsCount)
    {
      localObject = (TLRPC.Document)recentGifs.remove(recentGifs.size() - 1);
      MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
      {
        public void run()
        {
          try
          {
            MessagesStorage.getInstance().getDatabase().executeFast("DELETE FROM web_recent_v3 WHERE id = '" + this.val$old.id + "'").stepThis().dispose();
            return;
          }
          catch (Exception localException)
          {
            FileLog.e("tmessages", localException);
          }
        }
      });
    }
    Object localObject = new ArrayList();
    ((ArrayList)localObject).add(paramDocument);
    processLoadedRecentDocuments(0, (ArrayList)localObject, true, paramInt);
  }
  
  public static void addRecentSticker(int paramInt1, TLRPC.Document paramDocument, int paramInt2)
  {
    int j = 0;
    int i = 0;
    while (i < recentStickers[paramInt1].size())
    {
      localObject = (TLRPC.Document)recentStickers[paramInt1].get(i);
      if (((TLRPC.Document)localObject).id == paramDocument.id)
      {
        recentStickers[paramInt1].remove(i);
        recentStickers[paramInt1].add(0, localObject);
        j = 1;
      }
      i += 1;
    }
    if (j == 0) {
      recentStickers[paramInt1].add(0, paramDocument);
    }
    if (recentStickers[paramInt1].size() > MessagesController.getInstance().maxRecentStickersCount)
    {
      localObject = (TLRPC.Document)recentStickers[paramInt1].remove(recentStickers[paramInt1].size() - 1);
      MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
      {
        public void run()
        {
          try
          {
            MessagesStorage.getInstance().getDatabase().executeFast("DELETE FROM web_recent_v3 WHERE id = '" + this.val$old.id + "'").stepThis().dispose();
            return;
          }
          catch (Exception localException)
          {
            FileLog.e("tmessages", localException);
          }
        }
      });
    }
    Object localObject = new ArrayList();
    ((ArrayList)localObject).add(paramDocument);
    processLoadedRecentDocuments(paramInt1, (ArrayList)localObject, false, paramInt2);
  }
  
  private static int calcDocumentsHash(ArrayList<TLRPC.Document> paramArrayList)
  {
    if (paramArrayList == null) {
      return 0;
    }
    long l = 0L;
    int i = 0;
    if (i < Math.min(200, paramArrayList.size()))
    {
      TLRPC.Document localDocument = (TLRPC.Document)paramArrayList.get(i);
      if (localDocument == null) {}
      for (;;)
      {
        i += 1;
        break;
        int j = (int)(localDocument.id >> 32);
        int k = (int)localDocument.id;
        l = ((l * 20261L + 2147483648L + j) % 2147483648L * 20261L + 2147483648L + k) % 2147483648L;
      }
    }
    return (int)l;
  }
  
  private static int calcFeaturedStickersHash(ArrayList<TLRPC.StickerSetCovered> paramArrayList)
  {
    long l1 = 0L;
    int i = 0;
    if (i < paramArrayList.size())
    {
      TLRPC.StickerSet localStickerSet = ((TLRPC.StickerSetCovered)paramArrayList.get(i)).set;
      if (localStickerSet.archived) {}
      for (;;)
      {
        i += 1;
        break;
        int j = (int)(localStickerSet.id >> 32);
        int k = (int)localStickerSet.id;
        long l2 = ((l1 * 20261L + 2147483648L + j) % 2147483648L * 20261L + 2147483648L + k) % 2147483648L;
        l1 = l2;
        if (unreadStickerSets.contains(Long.valueOf(localStickerSet.id))) {
          l1 = (l2 * 20261L + 2147483648L + 1L) % 2147483648L;
        }
      }
    }
    return (int)l1;
  }
  
  public static void calcNewHash(int paramInt)
  {
    loadHash[paramInt] = calcStickersHash(stickerSets[paramInt]);
  }
  
  private static int calcStickersHash(ArrayList<TLRPC.TL_messages_stickerSet> paramArrayList)
  {
    long l = 0L;
    int i = 0;
    if (i < paramArrayList.size())
    {
      TLRPC.StickerSet localStickerSet = ((TLRPC.TL_messages_stickerSet)paramArrayList.get(i)).set;
      if (localStickerSet.archived) {}
      for (;;)
      {
        i += 1;
        break;
        l = (20261L * l + 2147483648L + localStickerSet.hash) % 2147483648L;
      }
    }
    return (int)l;
  }
  
  public static void checkFeaturedStickers()
  {
    if ((!loadingFeaturedStickers) && ((!featuredStickersLoaded) || (Math.abs(System.currentTimeMillis() / 1000L - loadFeaturedDate) >= 3600L))) {
      loadFeaturesStickers(true, false);
    }
  }
  
  public static void checkStickers(int paramInt)
  {
    if ((loadingStickers[paramInt] == 0) && ((stickersLoaded[paramInt] == 0) || (Math.abs(System.currentTimeMillis() / 1000L - loadDate[paramInt]) >= 3600L))) {
      loadStickers(paramInt, true, false);
    }
  }
  
  public static void cleanup()
  {
    int i = 0;
    while (i < 2)
    {
      loadHash[i] = 0;
      loadDate[i] = 0;
      stickerSets[i].clear();
      recentStickers[i].clear();
      loadingStickers[i] = false;
      stickersLoaded[i] = false;
      loadingRecentStickers[i] = false;
      recentStickersLoaded[i] = false;
      i += 1;
    }
    loadFeaturedDate = 0;
    loadFeaturedHash = 0;
    allStickers.clear();
    stickersByEmoji.clear();
    featuredStickerSetsById.clear();
    featuredStickerSets.clear();
    unreadStickerSets.clear();
    recentGifs.clear();
    stickerSetsById.clear();
    stickerSetsByName.clear();
    loadingFeaturedStickers = false;
    featuredStickersLoaded = false;
    loadingRecentGifs = false;
    recentGifsLoaded = false;
  }
  
  public static HashMap<String, ArrayList<TLRPC.Document>> getAllStickers()
  {
    return allStickers;
  }
  
  public static String getEmojiForSticker(long paramLong)
  {
    String str = (String)stickersByEmoji.get(Long.valueOf(paramLong));
    if (str != null) {
      return str;
    }
    return "";
  }
  
  public static ArrayList<TLRPC.StickerSetCovered> getFeaturedStickerSets()
  {
    return featuredStickerSets;
  }
  
  public static ArrayList<TLRPC.Document> getRecentGifs()
  {
    return new ArrayList(recentGifs);
  }
  
  public static ArrayList<TLRPC.Document> getRecentStickers(int paramInt)
  {
    return new ArrayList(recentStickers[paramInt]);
  }
  
  public static ArrayList<TLRPC.Document> getRecentStickersNoCopy(int paramInt)
  {
    return recentStickers[paramInt];
  }
  
  public static TLRPC.TL_messages_stickerSet getStickerSetById(Long paramLong)
  {
    return (TLRPC.TL_messages_stickerSet)stickerSetsById.get(paramLong);
  }
  
  public static TLRPC.TL_messages_stickerSet getStickerSetByName(String paramString)
  {
    return (TLRPC.TL_messages_stickerSet)stickerSetsByName.get(paramString);
  }
  
  public static long getStickerSetId(TLRPC.Document paramDocument)
  {
    int i = 0;
    while (i < paramDocument.attributes.size())
    {
      TLRPC.DocumentAttribute localDocumentAttribute = (TLRPC.DocumentAttribute)paramDocument.attributes.get(i);
      if ((localDocumentAttribute instanceof TLRPC.TL_documentAttributeSticker))
      {
        if (!(localDocumentAttribute.stickerset instanceof TLRPC.TL_inputStickerSetID)) {
          break;
        }
        return localDocumentAttribute.stickerset.id;
      }
      i += 1;
    }
    return -1L;
  }
  
  public static String getStickerSetName(long paramLong)
  {
    TLRPC.TL_messages_stickerSet localTL_messages_stickerSet = (TLRPC.TL_messages_stickerSet)stickerSetsById.get(Long.valueOf(paramLong));
    if (localTL_messages_stickerSet != null) {
      return localTL_messages_stickerSet.set.short_name;
    }
    return null;
  }
  
  public static ArrayList<TLRPC.TL_messages_stickerSet> getStickerSets(int paramInt)
  {
    return stickerSets[paramInt];
  }
  
  public static ArrayList<Long> getUnreadStickerSets()
  {
    return unreadStickerSets;
  }
  
  public static boolean isLoadingStickers(int paramInt)
  {
    return loadingStickers[paramInt];
  }
  
  public static boolean isStickerPackInstalled(long paramLong)
  {
    return stickerSetsById.containsKey(Long.valueOf(paramLong));
  }
  
  public static boolean isStickerPackInstalled(String paramString)
  {
    return stickerSetsByName.containsKey(paramString);
  }
  
  public static boolean isStickerPackUnread(long paramLong)
  {
    return unreadStickerSets.contains(Long.valueOf(paramLong));
  }
  
  public static void loadFeaturesStickers(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (loadingFeaturedStickers) {
      return;
    }
    loadingFeaturedStickers = true;
    if (paramBoolean1)
    {
      MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
      {
        /* Error */
        public void run()
        {
          // Byte code:
          //   0: aconst_null
          //   1: astore 9
          //   3: aconst_null
          //   4: astore 12
          //   6: aconst_null
          //   7: astore 13
          //   9: new 21	java/util/ArrayList
          //   12: dup
          //   13: invokespecial 22	java/util/ArrayList:<init>	()V
          //   16: astore 14
          //   18: iconst_0
          //   19: istore 4
          //   21: iconst_0
          //   22: istore 5
          //   24: iconst_0
          //   25: istore_2
          //   26: iconst_0
          //   27: istore 6
          //   29: iconst_0
          //   30: istore_3
          //   31: aconst_null
          //   32: astore 8
          //   34: aconst_null
          //   35: astore 10
          //   37: iload 4
          //   39: istore_1
          //   40: aload 12
          //   42: astore 11
          //   44: invokestatic 28	org/telegram/messenger/MessagesStorage:getInstance	()Lorg/telegram/messenger/MessagesStorage;
          //   47: invokevirtual 32	org/telegram/messenger/MessagesStorage:getDatabase	()Lorg/telegram/SQLite/SQLiteDatabase;
          //   50: ldc 34
          //   52: iconst_0
          //   53: anewarray 4	java/lang/Object
          //   56: invokevirtual 40	org/telegram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/telegram/SQLite/SQLiteCursor;
          //   59: astore 7
          //   61: aload 7
          //   63: astore 10
          //   65: iload 4
          //   67: istore_1
          //   68: aload 12
          //   70: astore 11
          //   72: aload 7
          //   74: astore 8
          //   76: aload 7
          //   78: invokevirtual 46	org/telegram/SQLite/SQLiteCursor:next	()Z
          //   81: ifeq +259 -> 340
          //   84: aload 7
          //   86: astore 10
          //   88: iload 4
          //   90: istore_1
          //   91: aload 12
          //   93: astore 11
          //   95: aload 7
          //   97: astore 8
          //   99: aload 7
          //   101: iconst_0
          //   102: invokevirtual 50	org/telegram/SQLite/SQLiteCursor:byteBufferValue	(I)Lorg/telegram/tgnet/NativeByteBuffer;
          //   105: astore 15
          //   107: aload 13
          //   109: astore 9
          //   111: aload 15
          //   113: ifnull +71 -> 184
          //   116: aload 7
          //   118: astore 10
          //   120: iload 4
          //   122: istore_1
          //   123: aload 12
          //   125: astore 11
          //   127: aload 7
          //   129: astore 8
          //   131: new 21	java/util/ArrayList
          //   134: dup
          //   135: invokespecial 22	java/util/ArrayList:<init>	()V
          //   138: astore 9
          //   140: aload 15
          //   142: iconst_0
          //   143: invokevirtual 56	org/telegram/tgnet/NativeByteBuffer:readInt32	(Z)I
          //   146: istore_2
          //   147: iconst_0
          //   148: istore_1
          //   149: iload_1
          //   150: iload_2
          //   151: if_icmpge +28 -> 179
          //   154: aload 9
          //   156: aload 15
          //   158: aload 15
          //   160: iconst_0
          //   161: invokevirtual 56	org/telegram/tgnet/NativeByteBuffer:readInt32	(Z)I
          //   164: iconst_0
          //   165: invokestatic 62	org/telegram/tgnet/TLRPC$StickerSetCovered:TLdeserialize	(Lorg/telegram/tgnet/AbstractSerializedData;IZ)Lorg/telegram/tgnet/TLRPC$StickerSetCovered;
          //   168: invokevirtual 66	java/util/ArrayList:add	(Ljava/lang/Object;)Z
          //   171: pop
          //   172: iload_1
          //   173: iconst_1
          //   174: iadd
          //   175: istore_1
          //   176: goto -27 -> 149
          //   179: aload 15
          //   181: invokevirtual 69	org/telegram/tgnet/NativeByteBuffer:reuse	()V
          //   184: aload 7
          //   186: astore 10
          //   188: iload 4
          //   190: istore_1
          //   191: aload 9
          //   193: astore 11
          //   195: aload 7
          //   197: astore 8
          //   199: aload 7
          //   201: iconst_1
          //   202: invokevirtual 50	org/telegram/SQLite/SQLiteCursor:byteBufferValue	(I)Lorg/telegram/tgnet/NativeByteBuffer;
          //   205: astore 12
          //   207: aload 12
          //   209: ifnull +89 -> 298
          //   212: aload 7
          //   214: astore 10
          //   216: iload 4
          //   218: istore_1
          //   219: aload 9
          //   221: astore 11
          //   223: aload 7
          //   225: astore 8
          //   227: aload 12
          //   229: iconst_0
          //   230: invokevirtual 56	org/telegram/tgnet/NativeByteBuffer:readInt32	(Z)I
          //   233: istore_3
          //   234: iconst_0
          //   235: istore_2
          //   236: iload_2
          //   237: iload_3
          //   238: if_icmpge +40 -> 278
          //   241: aload 7
          //   243: astore 10
          //   245: iload 4
          //   247: istore_1
          //   248: aload 9
          //   250: astore 11
          //   252: aload 7
          //   254: astore 8
          //   256: aload 14
          //   258: aload 12
          //   260: iconst_0
          //   261: invokevirtual 73	org/telegram/tgnet/NativeByteBuffer:readInt64	(Z)J
          //   264: invokestatic 79	java/lang/Long:valueOf	(J)Ljava/lang/Long;
          //   267: invokevirtual 66	java/util/ArrayList:add	(Ljava/lang/Object;)Z
          //   270: pop
          //   271: iload_2
          //   272: iconst_1
          //   273: iadd
          //   274: istore_2
          //   275: goto -39 -> 236
          //   278: aload 7
          //   280: astore 10
          //   282: iload 4
          //   284: istore_1
          //   285: aload 9
          //   287: astore 11
          //   289: aload 7
          //   291: astore 8
          //   293: aload 12
          //   295: invokevirtual 69	org/telegram/tgnet/NativeByteBuffer:reuse	()V
          //   298: aload 7
          //   300: astore 10
          //   302: iload 4
          //   304: istore_1
          //   305: aload 9
          //   307: astore 11
          //   309: aload 7
          //   311: astore 8
          //   313: aload 7
          //   315: iconst_2
          //   316: invokevirtual 83	org/telegram/SQLite/SQLiteCursor:intValue	(I)I
          //   319: istore_2
          //   320: aload 7
          //   322: astore 10
          //   324: iload_2
          //   325: istore_1
          //   326: aload 9
          //   328: astore 11
          //   330: aload 7
          //   332: astore 8
          //   334: aload 9
          //   336: invokestatic 87	org/telegram/messenger/query/StickersQuery:access$700	(Ljava/util/ArrayList;)I
          //   339: istore_3
          //   340: iload_2
          //   341: istore 4
          //   343: iload_3
          //   344: istore 5
          //   346: aload 9
          //   348: astore 8
          //   350: aload 7
          //   352: ifnull +18 -> 370
          //   355: aload 7
          //   357: invokevirtual 90	org/telegram/SQLite/SQLiteCursor:dispose	()V
          //   360: aload 9
          //   362: astore 8
          //   364: iload_3
          //   365: istore 5
          //   367: iload_2
          //   368: istore 4
          //   370: aload 8
          //   372: aload 14
          //   374: iconst_1
          //   375: iload 4
          //   377: iload 5
          //   379: invokestatic 94	org/telegram/messenger/query/StickersQuery:access$800	(Ljava/util/ArrayList;Ljava/util/ArrayList;ZII)V
          //   382: return
          //   383: astore 9
          //   385: aload 10
          //   387: astore 7
          //   389: aload 7
          //   391: astore 8
          //   393: ldc 96
          //   395: aload 9
          //   397: invokestatic 102	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
          //   400: iload_1
          //   401: istore 4
          //   403: iload 6
          //   405: istore 5
          //   407: aload 11
          //   409: astore 8
          //   411: aload 7
          //   413: ifnull -43 -> 370
          //   416: aload 7
          //   418: invokevirtual 90	org/telegram/SQLite/SQLiteCursor:dispose	()V
          //   421: iload_1
          //   422: istore 4
          //   424: iload 6
          //   426: istore 5
          //   428: aload 11
          //   430: astore 8
          //   432: goto -62 -> 370
          //   435: astore 9
          //   437: aload 8
          //   439: astore 7
          //   441: aload 7
          //   443: ifnull +8 -> 451
          //   446: aload 7
          //   448: invokevirtual 90	org/telegram/SQLite/SQLiteCursor:dispose	()V
          //   451: aload 9
          //   453: athrow
          //   454: astore 9
          //   456: goto -15 -> 441
          //   459: astore 8
          //   461: aload 9
          //   463: astore 11
          //   465: iload 5
          //   467: istore_1
          //   468: aload 8
          //   470: astore 9
          //   472: goto -83 -> 389
          // Local variable table:
          //   start	length	slot	name	signature
          //   0	475	0	this	11
          //   39	429	1	i	int
          //   25	343	2	j	int
          //   30	335	3	k	int
          //   19	404	4	m	int
          //   22	444	5	n	int
          //   27	398	6	i1	int
          //   59	388	7	localObject1	Object
          //   32	406	8	localObject2	Object
          //   459	10	8	localThrowable1	Throwable
          //   1	360	9	localObject3	Object
          //   383	13	9	localThrowable2	Throwable
          //   435	17	9	localObject4	Object
          //   454	8	9	localObject5	Object
          //   470	1	9	localObject6	Object
          //   35	351	10	localObject7	Object
          //   42	422	11	localObject8	Object
          //   4	290	12	localNativeByteBuffer1	NativeByteBuffer
          //   7	101	13	localObject9	Object
          //   16	357	14	localArrayList	ArrayList
          //   105	75	15	localNativeByteBuffer2	NativeByteBuffer
          // Exception table:
          //   from	to	target	type
          //   44	61	383	java/lang/Throwable
          //   76	84	383	java/lang/Throwable
          //   99	107	383	java/lang/Throwable
          //   131	140	383	java/lang/Throwable
          //   199	207	383	java/lang/Throwable
          //   227	234	383	java/lang/Throwable
          //   256	271	383	java/lang/Throwable
          //   293	298	383	java/lang/Throwable
          //   313	320	383	java/lang/Throwable
          //   334	340	383	java/lang/Throwable
          //   44	61	435	finally
          //   76	84	435	finally
          //   99	107	435	finally
          //   131	140	435	finally
          //   199	207	435	finally
          //   227	234	435	finally
          //   256	271	435	finally
          //   293	298	435	finally
          //   313	320	435	finally
          //   334	340	435	finally
          //   393	400	435	finally
          //   140	147	454	finally
          //   154	172	454	finally
          //   179	184	454	finally
          //   140	147	459	java/lang/Throwable
          //   154	172	459	java/lang/Throwable
          //   179	184	459	java/lang/Throwable
        }
      });
      return;
    }
    TLRPC.TL_messages_getFeaturedStickers localTL_messages_getFeaturedStickers = new TLRPC.TL_messages_getFeaturedStickers();
    if (paramBoolean2) {}
    for (int i = 0;; i = loadFeaturedHash)
    {
      localTL_messages_getFeaturedStickers.hash = i;
      ConnectionsManager.getInstance().sendRequest(localTL_messages_getFeaturedStickers, new RequestDelegate()
      {
        public void run(final TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
        {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              if ((paramAnonymousTLObject instanceof TLRPC.TL_messages_featuredStickers))
              {
                TLRPC.TL_messages_featuredStickers localTL_messages_featuredStickers = (TLRPC.TL_messages_featuredStickers)paramAnonymousTLObject;
                StickersQuery.processLoadedFeaturedStickers(localTL_messages_featuredStickers.sets, localTL_messages_featuredStickers.unread, false, (int)(System.currentTimeMillis() / 1000L), localTL_messages_featuredStickers.hash);
                return;
              }
              StickersQuery.processLoadedFeaturedStickers(null, null, false, (int)(System.currentTimeMillis() / 1000L), StickersQuery.12.this.val$req.hash);
            }
          });
        }
      });
      return;
    }
  }
  
  public static void loadRecents(final int paramInt, final boolean paramBoolean1, boolean paramBoolean2)
  {
    boolean bool = true;
    if (paramBoolean1) {
      if (!loadingRecentGifs) {}
    }
    for (;;)
    {
      return;
      loadingRecentGifs = true;
      if (recentGifsLoaded) {
        paramBoolean2 = false;
      }
      for (;;)
      {
        if (!paramBoolean2) {
          break label75;
        }
        MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
        {
          public void run()
          {
            for (;;)
            {
              final Object localObject2;
              try
              {
                Object localObject1 = MessagesStorage.getInstance().getDatabase();
                localObject2 = new StringBuilder().append("SELECT document FROM web_recent_v3 WHERE type = ");
                if (this.val$gif)
                {
                  i = 2;
                  localObject1 = ((SQLiteDatabase)localObject1).queryFinalized(i + " ORDER BY date DESC", new Object[0]);
                  localObject2 = new ArrayList();
                  if (!((SQLiteCursor)localObject1).next()) {
                    break label140;
                  }
                  if (((SQLiteCursor)localObject1).isNull(0)) {
                    continue;
                  }
                  NativeByteBuffer localNativeByteBuffer = ((SQLiteCursor)localObject1).byteBufferValue(0);
                  if (localNativeByteBuffer == null) {
                    continue;
                  }
                  TLRPC.Document localDocument = TLRPC.Document.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
                  if (localDocument != null) {
                    ((ArrayList)localObject2).add(localDocument);
                  }
                  localNativeByteBuffer.reuse();
                  continue;
                }
                if (paramInt != 0) {
                  break label157;
                }
              }
              catch (Throwable localThrowable)
              {
                FileLog.e("tmessages", localThrowable);
                return;
              }
              int i = 3;
              continue;
              label140:
              localThrowable.dispose();
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  if (StickersQuery.5.this.val$gif)
                  {
                    StickersQuery.access$002(localObject2);
                    StickersQuery.access$102(false);
                    StickersQuery.access$202(true);
                  }
                  for (;;)
                  {
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.recentDocumentsDidLoaded, new Object[] { Boolean.valueOf(StickersQuery.5.this.val$gif), Integer.valueOf(StickersQuery.5.this.val$type) });
                    StickersQuery.loadRecents(StickersQuery.5.this.val$type, StickersQuery.5.this.val$gif, false);
                    return;
                    StickersQuery.recentStickers[StickersQuery.5.this.val$type] = localObject2;
                    StickersQuery.loadingRecentStickers[StickersQuery.5.this.val$type] = 0;
                    StickersQuery.recentStickersLoaded[StickersQuery.5.this.val$type] = 1;
                  }
                }
              });
              return;
              label157:
              i = 4;
            }
          }
        });
        return;
        if (loadingRecentStickers[paramInt] != 0) {
          break;
        }
        loadingRecentStickers[paramInt] = true;
        if (recentStickersLoaded[paramInt] != 0) {
          paramBoolean2 = false;
        }
      }
      label75:
      localObject = ApplicationLoader.applicationContext.getSharedPreferences("emoji", 0);
      if (paramBoolean1) {}
      for (long l = ((SharedPreferences)localObject).getLong("lastGifLoadTime", 0L); Math.abs(System.currentTimeMillis() - l) >= 3600000L; l = ((SharedPreferences)localObject).getLong("lastStickersLoadTime", 0L))
      {
        if (!paramBoolean1) {
          break label179;
        }
        localObject = new TLRPC.TL_messages_getSavedGifs();
        ((TLRPC.TL_messages_getSavedGifs)localObject).hash = calcDocumentsHash(recentGifs);
        ConnectionsManager.getInstance().sendRequest((TLObject)localObject, new RequestDelegate()
        {
          public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
          {
            paramAnonymousTL_error = null;
            if ((paramAnonymousTLObject instanceof TLRPC.TL_messages_savedGifs)) {
              paramAnonymousTL_error = ((TLRPC.TL_messages_savedGifs)paramAnonymousTLObject).gifs;
            }
            StickersQuery.processLoadedRecentDocuments(this.val$type, paramAnonymousTL_error, paramBoolean1, 0);
          }
        });
        return;
      }
    }
    label179:
    Object localObject = new TLRPC.TL_messages_getRecentStickers();
    ((TLRPC.TL_messages_getRecentStickers)localObject).hash = calcDocumentsHash(recentStickers[paramInt]);
    if (paramInt == 1) {}
    for (paramBoolean2 = bool;; paramBoolean2 = false)
    {
      ((TLRPC.TL_messages_getRecentStickers)localObject).attached = paramBoolean2;
      ConnectionsManager.getInstance().sendRequest((TLObject)localObject, new RequestDelegate()
      {
        public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
        {
          paramAnonymousTL_error = null;
          if ((paramAnonymousTLObject instanceof TLRPC.TL_messages_recentStickers)) {
            paramAnonymousTL_error = ((TLRPC.TL_messages_recentStickers)paramAnonymousTLObject).stickers;
          }
          StickersQuery.processLoadedRecentDocuments(this.val$type, paramAnonymousTL_error, paramBoolean1, 0);
        }
      });
      return;
    }
  }
  
  public static void loadStickers(int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    int j = 0;
    final int i = 0;
    if (loadingStickers[paramInt] != 0) {
      return;
    }
    loadingStickers[paramInt] = true;
    if (paramBoolean1)
    {
      MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
      {
        /* Error */
        public void run()
        {
          // Byte code:
          //   0: aconst_null
          //   1: astore 9
          //   3: aconst_null
          //   4: astore 12
          //   6: aconst_null
          //   7: astore 13
          //   9: iconst_0
          //   10: istore 4
          //   12: iconst_0
          //   13: istore 5
          //   15: iconst_0
          //   16: istore_2
          //   17: iconst_0
          //   18: istore 6
          //   20: iconst_0
          //   21: istore_3
          //   22: aconst_null
          //   23: astore 8
          //   25: aconst_null
          //   26: astore 10
          //   28: iload 4
          //   30: istore_1
          //   31: aload 12
          //   33: astore 11
          //   35: invokestatic 30	org/telegram/messenger/MessagesStorage:getInstance	()Lorg/telegram/messenger/MessagesStorage;
          //   38: invokevirtual 34	org/telegram/messenger/MessagesStorage:getDatabase	()Lorg/telegram/SQLite/SQLiteDatabase;
          //   41: new 36	java/lang/StringBuilder
          //   44: dup
          //   45: invokespecial 37	java/lang/StringBuilder:<init>	()V
          //   48: ldc 39
          //   50: invokevirtual 43	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   53: aload_0
          //   54: getfield 17	org/telegram/messenger/query/StickersQuery$19:val$type	I
          //   57: iconst_1
          //   58: iadd
          //   59: invokevirtual 46	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
          //   62: invokevirtual 50	java/lang/StringBuilder:toString	()Ljava/lang/String;
          //   65: iconst_0
          //   66: anewarray 4	java/lang/Object
          //   69: invokevirtual 56	org/telegram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/telegram/SQLite/SQLiteCursor;
          //   72: astore 7
          //   74: aload 7
          //   76: astore 10
          //   78: iload 4
          //   80: istore_1
          //   81: aload 12
          //   83: astore 11
          //   85: aload 7
          //   87: astore 8
          //   89: aload 7
          //   91: invokevirtual 62	org/telegram/SQLite/SQLiteCursor:next	()Z
          //   94: ifeq +145 -> 239
          //   97: aload 7
          //   99: astore 10
          //   101: iload 4
          //   103: istore_1
          //   104: aload 12
          //   106: astore 11
          //   108: aload 7
          //   110: astore 8
          //   112: aload 7
          //   114: iconst_0
          //   115: invokevirtual 66	org/telegram/SQLite/SQLiteCursor:byteBufferValue	(I)Lorg/telegram/tgnet/NativeByteBuffer;
          //   118: astore 14
          //   120: aload 13
          //   122: astore 9
          //   124: aload 14
          //   126: ifnull +71 -> 197
          //   129: aload 7
          //   131: astore 10
          //   133: iload 4
          //   135: istore_1
          //   136: aload 12
          //   138: astore 11
          //   140: aload 7
          //   142: astore 8
          //   144: new 68	java/util/ArrayList
          //   147: dup
          //   148: invokespecial 69	java/util/ArrayList:<init>	()V
          //   151: astore 9
          //   153: aload 14
          //   155: iconst_0
          //   156: invokevirtual 75	org/telegram/tgnet/NativeByteBuffer:readInt32	(Z)I
          //   159: istore_2
          //   160: iconst_0
          //   161: istore_1
          //   162: iload_1
          //   163: iload_2
          //   164: if_icmpge +28 -> 192
          //   167: aload 9
          //   169: aload 14
          //   171: aload 14
          //   173: iconst_0
          //   174: invokevirtual 75	org/telegram/tgnet/NativeByteBuffer:readInt32	(Z)I
          //   177: iconst_0
          //   178: invokestatic 81	org/telegram/tgnet/TLRPC$TL_messages_stickerSet:TLdeserialize	(Lorg/telegram/tgnet/AbstractSerializedData;IZ)Lorg/telegram/tgnet/TLRPC$TL_messages_stickerSet;
          //   181: invokevirtual 85	java/util/ArrayList:add	(Ljava/lang/Object;)Z
          //   184: pop
          //   185: iload_1
          //   186: iconst_1
          //   187: iadd
          //   188: istore_1
          //   189: goto -27 -> 162
          //   192: aload 14
          //   194: invokevirtual 88	org/telegram/tgnet/NativeByteBuffer:reuse	()V
          //   197: aload 7
          //   199: astore 10
          //   201: iload 4
          //   203: istore_1
          //   204: aload 9
          //   206: astore 11
          //   208: aload 7
          //   210: astore 8
          //   212: aload 7
          //   214: iconst_1
          //   215: invokevirtual 92	org/telegram/SQLite/SQLiteCursor:intValue	(I)I
          //   218: istore_2
          //   219: aload 7
          //   221: astore 10
          //   223: iload_2
          //   224: istore_1
          //   225: aload 9
          //   227: astore 11
          //   229: aload 7
          //   231: astore 8
          //   233: aload 9
          //   235: invokestatic 96	org/telegram/messenger/query/StickersQuery:access$1800	(Ljava/util/ArrayList;)I
          //   238: istore_3
          //   239: iload_2
          //   240: istore 4
          //   242: iload_3
          //   243: istore 5
          //   245: aload 9
          //   247: astore 8
          //   249: aload 7
          //   251: ifnull +18 -> 269
          //   254: aload 7
          //   256: invokevirtual 99	org/telegram/SQLite/SQLiteCursor:dispose	()V
          //   259: aload 9
          //   261: astore 8
          //   263: iload_3
          //   264: istore 5
          //   266: iload_2
          //   267: istore 4
          //   269: aload_0
          //   270: getfield 17	org/telegram/messenger/query/StickersQuery$19:val$type	I
          //   273: aload 8
          //   275: iconst_1
          //   276: iload 4
          //   278: iload 5
          //   280: invokestatic 103	org/telegram/messenger/query/StickersQuery:access$1900	(ILjava/util/ArrayList;ZII)V
          //   283: return
          //   284: astore 9
          //   286: aload 10
          //   288: astore 7
          //   290: aload 7
          //   292: astore 8
          //   294: ldc 105
          //   296: aload 9
          //   298: invokestatic 111	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
          //   301: iload_1
          //   302: istore 4
          //   304: iload 6
          //   306: istore 5
          //   308: aload 11
          //   310: astore 8
          //   312: aload 7
          //   314: ifnull -45 -> 269
          //   317: aload 7
          //   319: invokevirtual 99	org/telegram/SQLite/SQLiteCursor:dispose	()V
          //   322: iload_1
          //   323: istore 4
          //   325: iload 6
          //   327: istore 5
          //   329: aload 11
          //   331: astore 8
          //   333: goto -64 -> 269
          //   336: astore 9
          //   338: aload 8
          //   340: astore 7
          //   342: aload 7
          //   344: ifnull +8 -> 352
          //   347: aload 7
          //   349: invokevirtual 99	org/telegram/SQLite/SQLiteCursor:dispose	()V
          //   352: aload 9
          //   354: athrow
          //   355: astore 9
          //   357: goto -15 -> 342
          //   360: astore 8
          //   362: aload 9
          //   364: astore 11
          //   366: iload 5
          //   368: istore_1
          //   369: aload 8
          //   371: astore 9
          //   373: goto -83 -> 290
          // Local variable table:
          //   start	length	slot	name	signature
          //   0	376	0	this	19
          //   30	339	1	i	int
          //   16	251	2	j	int
          //   21	243	3	k	int
          //   10	314	4	m	int
          //   13	354	5	n	int
          //   18	308	6	i1	int
          //   72	276	7	localObject1	Object
          //   23	316	8	localObject2	Object
          //   360	10	8	localThrowable1	Throwable
          //   1	259	9	localObject3	Object
          //   284	13	9	localThrowable2	Throwable
          //   336	17	9	localObject4	Object
          //   355	8	9	localObject5	Object
          //   371	1	9	localObject6	Object
          //   26	261	10	localObject7	Object
          //   33	332	11	localObject8	Object
          //   4	133	12	localObject9	Object
          //   7	114	13	localObject10	Object
          //   118	75	14	localNativeByteBuffer	NativeByteBuffer
          // Exception table:
          //   from	to	target	type
          //   35	74	284	java/lang/Throwable
          //   89	97	284	java/lang/Throwable
          //   112	120	284	java/lang/Throwable
          //   144	153	284	java/lang/Throwable
          //   212	219	284	java/lang/Throwable
          //   233	239	284	java/lang/Throwable
          //   35	74	336	finally
          //   89	97	336	finally
          //   112	120	336	finally
          //   144	153	336	finally
          //   212	219	336	finally
          //   233	239	336	finally
          //   294	301	336	finally
          //   153	160	355	finally
          //   167	185	355	finally
          //   192	197	355	finally
          //   153	160	360	java/lang/Throwable
          //   167	185	360	java/lang/Throwable
          //   192	197	360	java/lang/Throwable
        }
      });
      return;
    }
    if (paramInt == 0)
    {
      localObject1 = new TLRPC.TL_messages_getAllStickers();
      localObject2 = (TLRPC.TL_messages_getAllStickers)localObject1;
      if (paramBoolean2) {}
      for (;;)
      {
        ((TLRPC.TL_messages_getAllStickers)localObject2).hash = i;
        ConnectionsManager.getInstance().sendRequest((TLObject)localObject1, new RequestDelegate()
        {
          public void run(final TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
          {
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                if ((paramAnonymousTLObject instanceof TLRPC.TL_messages_allStickers))
                {
                  final TLRPC.TL_messages_allStickers localTL_messages_allStickers = (TLRPC.TL_messages_allStickers)paramAnonymousTLObject;
                  final ArrayList localArrayList = new ArrayList();
                  if (localTL_messages_allStickers.sets.isEmpty())
                  {
                    StickersQuery.processLoadedStickers(StickersQuery.20.this.val$type, localArrayList, false, (int)(System.currentTimeMillis() / 1000L), localTL_messages_allStickers.hash);
                    return;
                  }
                  final HashMap localHashMap = new HashMap();
                  final int i = 0;
                  label72:
                  final TLRPC.StickerSet localStickerSet;
                  Object localObject;
                  if (i < localTL_messages_allStickers.sets.size())
                  {
                    localStickerSet = (TLRPC.StickerSet)localTL_messages_allStickers.sets.get(i);
                    localObject = (TLRPC.TL_messages_stickerSet)StickersQuery.stickerSetsById.get(Long.valueOf(localStickerSet.id));
                    if ((localObject == null) || (((TLRPC.TL_messages_stickerSet)localObject).set.hash != localStickerSet.hash)) {
                      break label247;
                    }
                    ((TLRPC.TL_messages_stickerSet)localObject).set.archived = localStickerSet.archived;
                    ((TLRPC.TL_messages_stickerSet)localObject).set.installed = localStickerSet.installed;
                    ((TLRPC.TL_messages_stickerSet)localObject).set.official = localStickerSet.official;
                    localHashMap.put(Long.valueOf(((TLRPC.TL_messages_stickerSet)localObject).set.id), localObject);
                    localArrayList.add(localObject);
                    if (localHashMap.size() == localTL_messages_allStickers.sets.size()) {
                      StickersQuery.processLoadedStickers(StickersQuery.20.this.val$type, localArrayList, false, (int)(System.currentTimeMillis() / 1000L), localTL_messages_allStickers.hash);
                    }
                  }
                  for (;;)
                  {
                    i += 1;
                    break label72;
                    break;
                    label247:
                    localArrayList.add(null);
                    localObject = new TLRPC.TL_messages_getStickerSet();
                    ((TLRPC.TL_messages_getStickerSet)localObject).stickerset = new TLRPC.TL_inputStickerSetID();
                    ((TLRPC.TL_messages_getStickerSet)localObject).stickerset.id = localStickerSet.id;
                    ((TLRPC.TL_messages_getStickerSet)localObject).stickerset.access_hash = localStickerSet.access_hash;
                    ConnectionsManager.getInstance().sendRequest((TLObject)localObject, new RequestDelegate()
                    {
                      public void run(final TLObject paramAnonymous3TLObject, TLRPC.TL_error paramAnonymous3TL_error)
                      {
                        AndroidUtilities.runOnUIThread(new Runnable()
                        {
                          public void run()
                          {
                            TLRPC.TL_messages_stickerSet localTL_messages_stickerSet = (TLRPC.TL_messages_stickerSet)paramAnonymous3TLObject;
                            StickersQuery.20.1.1.this.val$newStickerArray.set(StickersQuery.20.1.1.this.val$index, localTL_messages_stickerSet);
                            StickersQuery.20.1.1.this.val$newStickerSets.put(Long.valueOf(StickersQuery.20.1.1.this.val$stickerSet.id), localTL_messages_stickerSet);
                            if (StickersQuery.20.1.1.this.val$newStickerSets.size() == StickersQuery.20.1.1.this.val$res.sets.size())
                            {
                              int i = 0;
                              while (i < StickersQuery.20.1.1.this.val$newStickerArray.size())
                              {
                                if (StickersQuery.20.1.1.this.val$newStickerArray.get(i) == null) {
                                  StickersQuery.20.1.1.this.val$newStickerArray.remove(i);
                                }
                                i += 1;
                              }
                              StickersQuery.processLoadedStickers(StickersQuery.20.this.val$type, StickersQuery.20.1.1.this.val$newStickerArray, false, (int)(System.currentTimeMillis() / 1000L), StickersQuery.20.1.1.this.val$res.hash);
                            }
                          }
                        });
                      }
                    });
                  }
                }
                StickersQuery.processLoadedStickers(StickersQuery.20.this.val$type, null, false, (int)(System.currentTimeMillis() / 1000L), StickersQuery.20.this.val$hash);
              }
            });
          }
        });
        return;
        i = loadHash[paramInt];
      }
    }
    Object localObject1 = new TLRPC.TL_messages_getMaskStickers();
    Object localObject2 = (TLRPC.TL_messages_getMaskStickers)localObject1;
    if (paramBoolean2) {}
    for (i = j;; i = loadHash[paramInt])
    {
      ((TLRPC.TL_messages_getMaskStickers)localObject2).hash = i;
      break;
    }
  }
  
  public static void markFaturedStickersAsRead(boolean paramBoolean)
  {
    if (unreadStickerSets.isEmpty()) {}
    do
    {
      return;
      unreadStickerSets.clear();
      loadFeaturedHash = calcFeaturedStickersHash(featuredStickerSets);
      NotificationCenter.getInstance().postNotificationName(NotificationCenter.featuredStickersDidLoaded, new Object[0]);
      putFeaturedStickersToCache(featuredStickerSets, unreadStickerSets, loadFeaturedDate, loadFeaturedHash);
    } while (!paramBoolean);
    TLRPC.TL_messages_readFeaturedStickers localTL_messages_readFeaturedStickers = new TLRPC.TL_messages_readFeaturedStickers();
    ConnectionsManager.getInstance().sendRequest(localTL_messages_readFeaturedStickers, new RequestDelegate()
    {
      public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error) {}
    });
  }
  
  public static void markFaturedStickersByIdAsRead(long paramLong)
  {
    if ((!unreadStickerSets.contains(Long.valueOf(paramLong))) || (readingStickerSets.contains(Long.valueOf(paramLong)))) {
      return;
    }
    readingStickerSets.add(Long.valueOf(paramLong));
    TLRPC.TL_messages_readFeaturedStickers localTL_messages_readFeaturedStickers = new TLRPC.TL_messages_readFeaturedStickers();
    localTL_messages_readFeaturedStickers.id.add(Long.valueOf(paramLong));
    ConnectionsManager.getInstance().sendRequest(localTL_messages_readFeaturedStickers, new RequestDelegate()
    {
      public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error) {}
    });
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        StickersQuery.unreadStickerSets.remove(Long.valueOf(this.val$id));
        StickersQuery.readingStickerSets.remove(Long.valueOf(this.val$id));
        StickersQuery.access$1102(StickersQuery.calcFeaturedStickersHash(StickersQuery.featuredStickerSets));
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.featuredStickersDidLoaded, new Object[0]);
        StickersQuery.putFeaturedStickersToCache(StickersQuery.featuredStickerSets, StickersQuery.unreadStickerSets, StickersQuery.loadFeaturedDate, StickersQuery.loadFeaturedHash);
      }
    }, 1000L);
  }
  
  private static void processLoadedFeaturedStickers(final ArrayList<TLRPC.StickerSetCovered> paramArrayList, final ArrayList<Long> paramArrayList1, boolean paramBoolean, final int paramInt1, final int paramInt2)
  {
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        StickersQuery.access$902(false);
        StickersQuery.access$1002(true);
      }
    });
    Utilities.stageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        long l = 1000L;
        final Object localObject;
        if (((this.val$cache) && ((paramArrayList == null) || (Math.abs(System.currentTimeMillis() / 1000L - paramInt1) >= 3600L))) || ((!this.val$cache) && (paramArrayList == null) && (paramInt2 == 0)))
        {
          localObject = new Runnable()
          {
            public void run()
            {
              if ((StickersQuery.14.this.val$res != null) && (StickersQuery.14.this.val$hash != 0)) {
                StickersQuery.access$1102(StickersQuery.14.this.val$hash);
              }
              StickersQuery.loadFeaturesStickers(false, false);
            }
          };
          if ((paramArrayList == null) && (!this.val$cache))
          {
            AndroidUtilities.runOnUIThread((Runnable)localObject, l);
            if (paramArrayList != null) {
              break label105;
            }
          }
        }
        label105:
        do
        {
          return;
          l = 0L;
          break;
          if (paramArrayList != null) {
            try
            {
              localObject = new ArrayList();
              final HashMap localHashMap = new HashMap();
              int i = 0;
              while (i < paramArrayList.size())
              {
                TLRPC.StickerSetCovered localStickerSetCovered = (TLRPC.StickerSetCovered)paramArrayList.get(i);
                ((ArrayList)localObject).add(localStickerSetCovered);
                localHashMap.put(Long.valueOf(localStickerSetCovered.set.id), localStickerSetCovered);
                i += 1;
              }
              if (!this.val$cache) {
                StickersQuery.putFeaturedStickersToCache((ArrayList)localObject, paramArrayList1, paramInt1, paramInt2);
              }
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  StickersQuery.access$1302(StickersQuery.14.this.val$unreadStickers);
                  StickersQuery.access$1402(localHashMap);
                  StickersQuery.access$1502(localObject);
                  StickersQuery.access$1102(StickersQuery.14.this.val$hash);
                  StickersQuery.access$1602(StickersQuery.14.this.val$date);
                  NotificationCenter.getInstance().postNotificationName(NotificationCenter.featuredStickersDidLoaded, new Object[0]);
                }
              });
              return;
            }
            catch (Throwable localThrowable)
            {
              FileLog.e("tmessages", localThrowable);
              return;
            }
          }
        } while (this.val$cache);
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            StickersQuery.access$1602(StickersQuery.14.this.val$date);
          }
        });
        StickersQuery.putFeaturedStickersToCache(null, null, paramInt1, 0);
      }
    });
  }
  
  private static void processLoadedRecentDocuments(final int paramInt1, final ArrayList<TLRPC.Document> paramArrayList, boolean paramBoolean, final int paramInt2)
  {
    if (paramArrayList != null) {
      MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
      {
        public void run()
        {
          for (;;)
          {
            int i;
            int m;
            int k;
            try
            {
              SQLiteDatabase localSQLiteDatabase = MessagesStorage.getInstance().getDatabase();
              SQLitePreparedStatement localSQLitePreparedStatement;
              if (this.val$gif)
              {
                i = MessagesController.getInstance().maxRecentGifsCount;
                localSQLiteDatabase.beginTransaction();
                localSQLitePreparedStatement = localSQLiteDatabase.executeFast("REPLACE INTO web_recent_v3 VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                m = paramArrayList.size();
                k = 0;
                break label360;
                localSQLitePreparedStatement.dispose();
                localSQLiteDatabase.commitTransaction();
                if (paramArrayList.size() < i) {
                  continue;
                }
                localSQLiteDatabase.beginTransaction();
                if (i < paramArrayList.size())
                {
                  localSQLiteDatabase.executeFast("DELETE FROM web_recent_v3 WHERE id = '" + ((TLRPC.Document)paramArrayList.get(i)).id + "'").stepThis().dispose();
                  i += 1;
                  continue;
                }
              }
              else
              {
                i = MessagesController.getInstance().maxRecentStickersCount;
                continue;
                label152:
                TLRPC.Document localDocument = (TLRPC.Document)paramArrayList.get(k);
                localSQLitePreparedStatement.requery();
                localSQLitePreparedStatement.bindString(1, "" + localDocument.id);
                if (this.val$gif)
                {
                  j = 2;
                  localSQLitePreparedStatement.bindInteger(2, j);
                  localSQLitePreparedStatement.bindString(3, "");
                  localSQLitePreparedStatement.bindString(4, "");
                  localSQLitePreparedStatement.bindString(5, "");
                  localSQLitePreparedStatement.bindInteger(6, 0);
                  localSQLitePreparedStatement.bindInteger(7, 0);
                  localSQLitePreparedStatement.bindInteger(8, 0);
                  if (paramInt2 == 0) {
                    break label386;
                  }
                  j = paramInt2;
                  localSQLitePreparedStatement.bindInteger(9, j);
                  NativeByteBuffer localNativeByteBuffer = new NativeByteBuffer(localDocument.getObjectSize());
                  localDocument.serializeToStream(localNativeByteBuffer);
                  localSQLitePreparedStatement.bindByteBuffer(10, localNativeByteBuffer);
                  localSQLitePreparedStatement.step();
                  if (localNativeByteBuffer == null) {
                    break label374;
                  }
                  localNativeByteBuffer.reuse();
                  break label374;
                }
                if (paramInt1 != 0) {
                  break label381;
                }
                j = 3;
                continue;
              }
              localSQLiteDatabase.commitTransaction();
              return;
            }
            catch (Exception localException)
            {
              FileLog.e("tmessages", localException);
              return;
            }
            label360:
            while (k < m)
            {
              if (k != i) {
                break label152;
              }
              break;
              label374:
              k += 1;
            }
            label381:
            int j = 4;
            continue;
            label386:
            j = m - k;
          }
        }
      });
    }
    if (paramInt2 == 0) {
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          SharedPreferences.Editor localEditor = ApplicationLoader.applicationContext.getSharedPreferences("emoji", 0).edit();
          if (this.val$gif)
          {
            StickersQuery.access$102(false);
            StickersQuery.access$202(true);
            localEditor.putLong("lastGifLoadTime", System.currentTimeMillis()).commit();
            if (paramArrayList != null)
            {
              if (!this.val$gif) {
                break label143;
              }
              StickersQuery.access$002(paramArrayList);
            }
          }
          for (;;)
          {
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.recentDocumentsDidLoaded, new Object[] { Boolean.valueOf(this.val$gif), Integer.valueOf(paramInt1) });
            return;
            StickersQuery.loadingRecentStickers[paramInt1] = 0;
            StickersQuery.recentStickersLoaded[paramInt1] = 1;
            localEditor.putLong("lastStickersLoadTime", System.currentTimeMillis()).commit();
            break;
            label143:
            StickersQuery.recentStickers[paramInt1] = paramArrayList;
          }
        }
      });
    }
  }
  
  private static void processLoadedStickers(final int paramInt1, final ArrayList<TLRPC.TL_messages_stickerSet> paramArrayList, boolean paramBoolean, final int paramInt2, final int paramInt3)
  {
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        StickersQuery.loadingStickers[this.val$type] = 0;
        StickersQuery.stickersLoaded[this.val$type] = 1;
      }
    });
    Utilities.stageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        Object localObject1;
        if (((this.val$cache) && ((paramArrayList == null) || (Math.abs(System.currentTimeMillis() / 1000L - paramInt2) >= 3600L))) || ((!this.val$cache) && (paramArrayList == null) && (paramInt3 == 0)))
        {
          localObject1 = new Runnable()
          {
            public void run()
            {
              if ((StickersQuery.23.this.val$res != null) && (StickersQuery.23.this.val$hash != 0)) {
                StickersQuery.loadHash[StickersQuery.23.this.val$type] = StickersQuery.23.this.val$hash;
              }
              StickersQuery.loadStickers(StickersQuery.23.this.val$type, false, false);
            }
          };
          if ((paramArrayList == null) && (!this.val$cache)) {}
          for (long l = 1000L;; l = 0L)
          {
            AndroidUtilities.runOnUIThread((Runnable)localObject1, l);
            if (paramArrayList != null) {
              break;
            }
            return;
          }
        }
        final ArrayList localArrayList;
        final HashMap localHashMap1;
        final HashMap localHashMap2;
        final HashMap localHashMap3;
        HashMap localHashMap4;
        final HashMap localHashMap5;
        int i;
        label171:
        int j;
        label248:
        label332:
        TLRPC.TL_stickerPack localTL_stickerPack;
        Object localObject3;
        Object localObject2;
        if (paramArrayList != null)
        {
          TLRPC.TL_messages_stickerSet localTL_messages_stickerSet;
          try
          {
            localArrayList = new ArrayList();
            localHashMap1 = new HashMap();
            localHashMap2 = new HashMap();
            localHashMap3 = new HashMap();
            localHashMap4 = new HashMap();
            localHashMap5 = new HashMap();
            i = 0;
            if (i >= paramArrayList.size()) {
              break label517;
            }
            localTL_messages_stickerSet = (TLRPC.TL_messages_stickerSet)paramArrayList.get(i);
            if (localTL_messages_stickerSet == null) {
              break label595;
            }
            localArrayList.add(localTL_messages_stickerSet);
            localHashMap1.put(Long.valueOf(localTL_messages_stickerSet.set.id), localTL_messages_stickerSet);
            localHashMap2.put(localTL_messages_stickerSet.set.short_name, localTL_messages_stickerSet);
            j = 0;
            if (j < localTL_messages_stickerSet.documents.size())
            {
              localObject1 = (TLRPC.Document)localTL_messages_stickerSet.documents.get(j);
              if ((localObject1 == null) || ((localObject1 instanceof TLRPC.TL_documentEmpty))) {
                break label602;
              }
              localHashMap4.put(Long.valueOf(((TLRPC.Document)localObject1).id), localObject1);
            }
          }
          catch (Throwable localThrowable)
          {
            FileLog.e("tmessages", localThrowable);
            return;
          }
          if (localTL_messages_stickerSet.set.archived) {
            break label595;
          }
          j = 0;
          if (j >= localTL_messages_stickerSet.packs.size()) {
            break label595;
          }
          localTL_stickerPack = (TLRPC.TL_stickerPack)localTL_messages_stickerSet.packs.get(j);
          if ((localTL_stickerPack == null) || (localTL_stickerPack.emoticon == null)) {
            break label609;
          }
          localTL_stickerPack.emoticon = localTL_stickerPack.emoticon.replace("️", "");
          localObject3 = (ArrayList)localHashMap5.get(localTL_stickerPack.emoticon);
          localObject2 = localObject3;
          if (localObject3 != null) {
            break label616;
          }
          localObject2 = new ArrayList();
          localHashMap5.put(localTL_stickerPack.emoticon, localObject2);
          break label616;
        }
        for (;;)
        {
          if (k < localTL_stickerPack.documents.size())
          {
            localObject3 = (Long)localTL_stickerPack.documents.get(k);
            if (!localHashMap3.containsKey(localObject3)) {
              localHashMap3.put(localObject3, localTL_stickerPack.emoticon);
            }
            localObject3 = (TLRPC.Document)localHashMap4.get(localObject3);
            if (localObject3 == null) {
              break label621;
            }
            ((ArrayList)localObject2).add(localObject3);
            break label621;
            label517:
            if (!this.val$cache) {
              StickersQuery.putStickersToCache(paramInt1, localArrayList, paramInt2, paramInt3);
            }
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                int i = 0;
                while (i < StickersQuery.stickerSets[StickersQuery.23.this.val$type].size())
                {
                  TLRPC.StickerSet localStickerSet = ((TLRPC.TL_messages_stickerSet)StickersQuery.stickerSets[StickersQuery.23.this.val$type].get(i)).set;
                  StickersQuery.stickerSetsById.remove(Long.valueOf(localStickerSet.id));
                  StickersQuery.stickerSetsByName.remove(localStickerSet.short_name);
                  i += 1;
                }
                StickersQuery.stickerSetsById.putAll(localHashMap1);
                StickersQuery.stickerSetsByName.putAll(localHashMap2);
                StickersQuery.stickerSets[StickersQuery.23.this.val$type] = localArrayList;
                StickersQuery.loadHash[StickersQuery.23.this.val$type] = StickersQuery.23.this.val$hash;
                StickersQuery.loadDate[StickersQuery.23.this.val$type] = StickersQuery.23.this.val$date;
                if (StickersQuery.23.this.val$type == 0)
                {
                  StickersQuery.access$2802(localHashMap5);
                  StickersQuery.access$2902(localHashMap3);
                }
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.stickersDidLoaded, new Object[] { Integer.valueOf(StickersQuery.23.this.val$type) });
              }
            });
            return;
            if (this.val$cache) {
              break;
            }
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                StickersQuery.loadDate[StickersQuery.23.this.val$type] = StickersQuery.23.this.val$date;
              }
            });
            StickersQuery.putStickersToCache(paramInt1, null, paramInt2, 0);
            return;
            label595:
            i += 1;
            break label171;
            label602:
            j += 1;
            break label248;
          }
          label609:
          j += 1;
          break label332;
          label616:
          int k = 0;
          continue;
          label621:
          k += 1;
        }
      }
    });
  }
  
  private static void putFeaturedStickersToCache(ArrayList<TLRPC.StickerSetCovered> paramArrayList, final ArrayList<Long> paramArrayList1, final int paramInt1, final int paramInt2)
  {
    if (paramArrayList != null) {}
    for (paramArrayList = new ArrayList(paramArrayList);; paramArrayList = null)
    {
      MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
      {
        public void run()
        {
          try
          {
            if (this.val$stickersFinal != null)
            {
              localSQLitePreparedStatement = MessagesStorage.getInstance().getDatabase().executeFast("REPLACE INTO stickers_featured VALUES(?, ?, ?, ?, ?)");
              localSQLitePreparedStatement.requery();
              int j = 4;
              int i = 0;
              while (i < this.val$stickersFinal.size())
              {
                j += ((TLRPC.StickerSetCovered)this.val$stickersFinal.get(i)).getObjectSize();
                i += 1;
              }
              NativeByteBuffer localNativeByteBuffer1 = new NativeByteBuffer(j);
              NativeByteBuffer localNativeByteBuffer2 = new NativeByteBuffer(paramArrayList1.size() * 8 + 4);
              localNativeByteBuffer1.writeInt32(this.val$stickersFinal.size());
              i = 0;
              while (i < this.val$stickersFinal.size())
              {
                ((TLRPC.StickerSetCovered)this.val$stickersFinal.get(i)).serializeToStream(localNativeByteBuffer1);
                i += 1;
              }
              localNativeByteBuffer2.writeInt32(paramArrayList1.size());
              i = 0;
              while (i < paramArrayList1.size())
              {
                localNativeByteBuffer2.writeInt64(((Long)paramArrayList1.get(i)).longValue());
                i += 1;
              }
              localSQLitePreparedStatement.bindInteger(1, 1);
              localSQLitePreparedStatement.bindByteBuffer(2, localNativeByteBuffer1);
              localSQLitePreparedStatement.bindByteBuffer(3, localNativeByteBuffer2);
              localSQLitePreparedStatement.bindInteger(4, paramInt1);
              localSQLitePreparedStatement.bindInteger(5, paramInt2);
              localSQLitePreparedStatement.step();
              localNativeByteBuffer1.reuse();
              localNativeByteBuffer2.reuse();
              localSQLitePreparedStatement.dispose();
              return;
            }
            SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.getInstance().getDatabase().executeFast("UPDATE stickers_featured SET date = ?");
            localSQLitePreparedStatement.requery();
            localSQLitePreparedStatement.bindInteger(1, paramInt1);
            localSQLitePreparedStatement.step();
            localSQLitePreparedStatement.dispose();
            return;
          }
          catch (Exception localException)
          {
            FileLog.e("tmessages", localException);
          }
        }
      });
      return;
    }
  }
  
  private static void putStickersToCache(final int paramInt1, ArrayList<TLRPC.TL_messages_stickerSet> paramArrayList, final int paramInt2, final int paramInt3)
  {
    if (paramArrayList != null) {}
    for (paramArrayList = new ArrayList(paramArrayList);; paramArrayList = null)
    {
      MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
      {
        public void run()
        {
          for (;;)
          {
            try
            {
              SQLitePreparedStatement localSQLitePreparedStatement;
              if (this.val$stickersFinal != null)
              {
                localSQLitePreparedStatement = MessagesStorage.getInstance().getDatabase().executeFast("REPLACE INTO stickers_v2 VALUES(?, ?, ?, ?)");
                localSQLitePreparedStatement.requery();
                int j = 4;
                i = 0;
                if (i < this.val$stickersFinal.size())
                {
                  j += ((TLRPC.TL_messages_stickerSet)this.val$stickersFinal.get(i)).getObjectSize();
                  i += 1;
                  continue;
                }
                NativeByteBuffer localNativeByteBuffer = new NativeByteBuffer(j);
                localNativeByteBuffer.writeInt32(this.val$stickersFinal.size());
                i = 0;
                if (i < this.val$stickersFinal.size())
                {
                  ((TLRPC.TL_messages_stickerSet)this.val$stickersFinal.get(i)).serializeToStream(localNativeByteBuffer);
                  i += 1;
                  continue;
                }
                if (paramInt1 == 0)
                {
                  i = 1;
                  localSQLitePreparedStatement.bindInteger(1, i);
                  localSQLitePreparedStatement.bindByteBuffer(2, localNativeByteBuffer);
                  localSQLitePreparedStatement.bindInteger(3, paramInt2);
                  localSQLitePreparedStatement.bindInteger(4, paramInt3);
                  localSQLitePreparedStatement.step();
                  localNativeByteBuffer.reuse();
                  localSQLitePreparedStatement.dispose();
                }
              }
              else
              {
                localSQLitePreparedStatement = MessagesStorage.getInstance().getDatabase().executeFast("UPDATE stickers_v2 SET date = ?");
                localSQLitePreparedStatement.requery();
                localSQLitePreparedStatement.bindInteger(1, paramInt2);
                localSQLitePreparedStatement.step();
                localSQLitePreparedStatement.dispose();
                return;
              }
            }
            catch (Exception localException)
            {
              FileLog.e("tmessages", localException);
              return;
            }
            int i = 2;
          }
        }
      });
      return;
    }
  }
  
  public static void removeRecentGif(TLRPC.Document paramDocument)
  {
    recentGifs.remove(paramDocument);
    TLRPC.TL_messages_saveGif localTL_messages_saveGif = new TLRPC.TL_messages_saveGif();
    localTL_messages_saveGif.id = new TLRPC.TL_inputDocument();
    localTL_messages_saveGif.id.id = paramDocument.id;
    localTL_messages_saveGif.id.access_hash = paramDocument.access_hash;
    localTL_messages_saveGif.unsave = true;
    ConnectionsManager.getInstance().sendRequest(localTL_messages_saveGif, new RequestDelegate()
    {
      public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error) {}
    });
    MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
    {
      public void run()
      {
        try
        {
          MessagesStorage.getInstance().getDatabase().executeFast("DELETE FROM web_recent_v3 WHERE id = '" + this.val$document.id + "'").stepThis().dispose();
          return;
        }
        catch (Exception localException)
        {
          FileLog.e("tmessages", localException);
        }
      }
    });
  }
  
  public static void removeStickersSet(final Context paramContext, TLRPC.StickerSet paramStickerSet, final int paramInt, final BaseFragment paramBaseFragment, final boolean paramBoolean)
  {
    final int i;
    TLRPC.TL_inputStickerSetID localTL_inputStickerSetID;
    label49:
    int j;
    if (paramStickerSet.masks)
    {
      i = 1;
      localTL_inputStickerSetID = new TLRPC.TL_inputStickerSetID();
      localTL_inputStickerSetID.access_hash = paramStickerSet.access_hash;
      localTL_inputStickerSetID.id = paramStickerSet.id;
      if (paramInt == 0) {
        break label300;
      }
      if (paramInt != 1) {
        break label245;
      }
      bool = true;
      paramStickerSet.archived = bool;
      j = 0;
      label58:
      if (j < stickerSets[i].size())
      {
        paramContext = (TLRPC.TL_messages_stickerSet)stickerSets[i].get(j);
        if (paramContext.set.id != paramStickerSet.id) {
          break label285;
        }
        stickerSets[i].remove(j);
        if (paramInt != 2) {
          break label251;
        }
        stickerSets[i].add(0, paramContext);
      }
      label130:
      loadHash[i] = calcStickersHash(stickerSets[i]);
      putStickersToCache(i, stickerSets[i], loadDate[i], loadHash[i]);
      NotificationCenter.getInstance().postNotificationName(NotificationCenter.stickersDidLoaded, new Object[] { Integer.valueOf(i) });
      paramContext = new TLRPC.TL_messages_installStickerSet();
      paramContext.stickerset = localTL_inputStickerSetID;
      if (paramInt != 1) {
        break label294;
      }
    }
    label245:
    label251:
    label285:
    label294:
    for (boolean bool = true;; bool = false)
    {
      paramContext.archived = bool;
      ConnectionsManager.getInstance().sendRequest(paramContext, new RequestDelegate()
      {
        public void run(final TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
        {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              Activity localActivity;
              if ((paramAnonymousTLObject instanceof TLRPC.TL_messages_stickerSetInstallResultArchive))
              {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.needReloadArchivedStickers, new Object[] { Integer.valueOf(StickersQuery.24.this.val$type) });
                if ((StickersQuery.24.this.val$hide != 1) && (StickersQuery.24.this.val$baseFragment != null) && (StickersQuery.24.this.val$baseFragment.getParentActivity() != null))
                {
                  localActivity = StickersQuery.24.this.val$baseFragment.getParentActivity();
                  if (!StickersQuery.24.this.val$showSettings) {
                    break label135;
                  }
                }
              }
              label135:
              for (Object localObject = StickersQuery.24.this.val$baseFragment;; localObject = null)
              {
                localObject = new StickersArchiveAlert(localActivity, (BaseFragment)localObject, ((TLRPC.TL_messages_stickerSetInstallResultArchive)paramAnonymousTLObject).sets);
                StickersQuery.24.this.val$baseFragment.showDialog(((StickersArchiveAlert)localObject).create());
                return;
              }
            }
          });
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              StickersQuery.loadStickers(StickersQuery.24.this.val$type, false, false);
            }
          }, 1000L);
        }
      });
      return;
      i = 0;
      break;
      bool = false;
      break label49;
      stickerSetsById.remove(Long.valueOf(paramContext.set.id));
      stickerSetsByName.remove(paramContext.set.short_name);
      break label130;
      j += 1;
      break label58;
    }
    label300:
    paramBaseFragment = new TLRPC.TL_messages_uninstallStickerSet();
    paramBaseFragment.stickerset = localTL_inputStickerSetID;
    ConnectionsManager.getInstance().sendRequest(paramBaseFragment, new RequestDelegate()
    {
      public void run(TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
      {
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            try
            {
              if (paramAnonymousTL_error == null)
              {
                if (StickersQuery.25.this.val$stickerSet.masks) {
                  Toast.makeText(StickersQuery.25.this.val$context, LocaleController.getString("MasksRemoved", 2131165851), 0).show();
                }
                for (;;)
                {
                  StickersQuery.loadStickers(StickersQuery.25.this.val$type, false, true);
                  return;
                  Toast.makeText(StickersQuery.25.this.val$context, LocaleController.getString("StickersRemoved", 2131166314), 0).show();
                }
              }
            }
            catch (Exception localException)
            {
              for (;;)
              {
                FileLog.e("tmessages", localException);
                continue;
                Toast.makeText(StickersQuery.25.this.val$context, LocaleController.getString("ErrorOccurred", 2131165626), 0).show();
              }
            }
          }
        });
      }
    });
  }
  
  public static void reorderStickers(int paramInt, ArrayList<Long> paramArrayList)
  {
    Collections.sort(stickerSets[paramInt], new Comparator()
    {
      public int compare(TLRPC.TL_messages_stickerSet paramAnonymousTL_messages_stickerSet1, TLRPC.TL_messages_stickerSet paramAnonymousTL_messages_stickerSet2)
      {
        int i = this.val$order.indexOf(Long.valueOf(paramAnonymousTL_messages_stickerSet1.set.id));
        int j = this.val$order.indexOf(Long.valueOf(paramAnonymousTL_messages_stickerSet2.set.id));
        if (i > j) {
          return 1;
        }
        if (i < j) {
          return -1;
        }
        return 0;
      }
    });
    loadHash[paramInt] = calcStickersHash(stickerSets[paramInt]);
    NotificationCenter.getInstance().postNotificationName(NotificationCenter.stickersDidLoaded, new Object[] { Integer.valueOf(paramInt) });
    loadStickers(paramInt, false, true);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/query/StickersQuery.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */