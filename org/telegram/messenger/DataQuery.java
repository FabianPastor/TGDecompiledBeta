package org.telegram.messenger;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutInfo.Builder;
import android.content.pm.ShortcutManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Build.VERSION;
import android.os.Parcelable;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.widget.Toast;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.messenger.support.SparseLongArray;
import org.telegram.tgnet.AbstractSerializedData;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.BotInfo;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatPhoto;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.DraftMessage;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.InputStickerSet;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageEntity;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.StickerSet;
import org.telegram.tgnet.TLRPC.StickerSetCovered;
import org.telegram.tgnet.TLRPC.TL_channels_getMessages;
import org.telegram.tgnet.TLRPC.TL_contacts_getTopPeers;
import org.telegram.tgnet.TLRPC.TL_contacts_resetTopPeerRating;
import org.telegram.tgnet.TLRPC.TL_contacts_topPeers;
import org.telegram.tgnet.TLRPC.TL_documentAttributeSticker;
import org.telegram.tgnet.TLRPC.TL_documentEmpty;
import org.telegram.tgnet.TLRPC.TL_draftMessage;
import org.telegram.tgnet.TLRPC.TL_draftMessageEmpty;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputDocument;
import org.telegram.tgnet.TLRPC.TL_inputMessageEntityMentionName;
import org.telegram.tgnet.TLRPC.TL_inputMessagesFilterDocument;
import org.telegram.tgnet.TLRPC.TL_inputMessagesFilterEmpty;
import org.telegram.tgnet.TLRPC.TL_inputMessagesFilterMusic;
import org.telegram.tgnet.TLRPC.TL_inputMessagesFilterPhotoVideo;
import org.telegram.tgnet.TLRPC.TL_inputMessagesFilterUrl;
import org.telegram.tgnet.TLRPC.TL_inputMessagesFilterVoice;
import org.telegram.tgnet.TLRPC.TL_inputStickerSetID;
import org.telegram.tgnet.TLRPC.TL_message;
import org.telegram.tgnet.TLRPC.TL_messageActionGameScore;
import org.telegram.tgnet.TLRPC.TL_messageActionHistoryClear;
import org.telegram.tgnet.TLRPC.TL_messageActionPaymentSent;
import org.telegram.tgnet.TLRPC.TL_messageActionPinMessage;
import org.telegram.tgnet.TLRPC.TL_messageEmpty;
import org.telegram.tgnet.TLRPC.TL_messageEntityBold;
import org.telegram.tgnet.TLRPC.TL_messageEntityCode;
import org.telegram.tgnet.TLRPC.TL_messageEntityEmail;
import org.telegram.tgnet.TLRPC.TL_messageEntityItalic;
import org.telegram.tgnet.TLRPC.TL_messageEntityPre;
import org.telegram.tgnet.TLRPC.TL_messageEntityTextUrl;
import org.telegram.tgnet.TLRPC.TL_messageEntityUrl;
import org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_message_secret;
import org.telegram.tgnet.TLRPC.TL_messages_allStickers;
import org.telegram.tgnet.TLRPC.TL_messages_archivedStickers;
import org.telegram.tgnet.TLRPC.TL_messages_channelMessages;
import org.telegram.tgnet.TLRPC.TL_messages_faveSticker;
import org.telegram.tgnet.TLRPC.TL_messages_favedStickers;
import org.telegram.tgnet.TLRPC.TL_messages_featuredStickers;
import org.telegram.tgnet.TLRPC.TL_messages_getAllDrafts;
import org.telegram.tgnet.TLRPC.TL_messages_getAllStickers;
import org.telegram.tgnet.TLRPC.TL_messages_getArchivedStickers;
import org.telegram.tgnet.TLRPC.TL_messages_getFavedStickers;
import org.telegram.tgnet.TLRPC.TL_messages_getFeaturedStickers;
import org.telegram.tgnet.TLRPC.TL_messages_getMaskStickers;
import org.telegram.tgnet.TLRPC.TL_messages_getMessages;
import org.telegram.tgnet.TLRPC.TL_messages_getRecentStickers;
import org.telegram.tgnet.TLRPC.TL_messages_getSavedGifs;
import org.telegram.tgnet.TLRPC.TL_messages_getStickerSet;
import org.telegram.tgnet.TLRPC.TL_messages_installStickerSet;
import org.telegram.tgnet.TLRPC.TL_messages_messages;
import org.telegram.tgnet.TLRPC.TL_messages_messagesSlice;
import org.telegram.tgnet.TLRPC.TL_messages_readFeaturedStickers;
import org.telegram.tgnet.TLRPC.TL_messages_recentStickers;
import org.telegram.tgnet.TLRPC.TL_messages_saveDraft;
import org.telegram.tgnet.TLRPC.TL_messages_saveGif;
import org.telegram.tgnet.TLRPC.TL_messages_savedGifs;
import org.telegram.tgnet.TLRPC.TL_messages_search;
import org.telegram.tgnet.TLRPC.TL_messages_stickerSet;
import org.telegram.tgnet.TLRPC.TL_messages_stickerSetInstallResultArchive;
import org.telegram.tgnet.TLRPC.TL_messages_uninstallStickerSet;
import org.telegram.tgnet.TLRPC.TL_peerChat;
import org.telegram.tgnet.TLRPC.TL_peerUser;
import org.telegram.tgnet.TLRPC.TL_stickerPack;
import org.telegram.tgnet.TLRPC.TL_topPeer;
import org.telegram.tgnet.TLRPC.TL_topPeerCategoryBotsInline;
import org.telegram.tgnet.TLRPC.TL_topPeerCategoryCorrespondents;
import org.telegram.tgnet.TLRPC.TL_topPeerCategoryPeers;
import org.telegram.tgnet.TLRPC.Updates;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserProfilePhoto;
import org.telegram.tgnet.TLRPC.messages_Messages;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.StickersArchiveAlert;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.Components.URLSpanUserMention;
import org.telegram.ui.LaunchActivity;

public class DataQuery
{
  private static volatile DataQuery[] Instance = new DataQuery[3];
  public static final int MEDIA_AUDIO = 2;
  public static final int MEDIA_FILE = 1;
  public static final int MEDIA_MUSIC = 4;
  public static final int MEDIA_PHOTOVIDEO = 0;
  public static final int MEDIA_TYPES_COUNT = 5;
  public static final int MEDIA_URL = 3;
  public static final int TYPE_FAVE = 2;
  public static final int TYPE_FEATURED = 3;
  public static final int TYPE_IMAGE = 0;
  public static final int TYPE_MASK = 1;
  private static RectF bitmapRect;
  private static Comparator<TLRPC.MessageEntity> entityComparator = new Comparator()
  {
    public int compare(TLRPC.MessageEntity paramAnonymousMessageEntity1, TLRPC.MessageEntity paramAnonymousMessageEntity2)
    {
      int i;
      if (paramAnonymousMessageEntity1.offset > paramAnonymousMessageEntity2.offset) {
        i = 1;
      }
      for (;;)
      {
        return i;
        if (paramAnonymousMessageEntity1.offset < paramAnonymousMessageEntity2.offset) {
          i = -1;
        } else {
          i = 0;
        }
      }
    }
  };
  private static Paint erasePaint;
  private static Paint roundPaint;
  private static Path roundPath;
  private HashMap<String, ArrayList<TLRPC.Document>> allStickers = new HashMap();
  private HashMap<String, ArrayList<TLRPC.Document>> allStickersFeatured = new HashMap();
  private int[] archivedStickersCount = new int[2];
  private SparseArray<TLRPC.BotInfo> botInfos = new SparseArray();
  private LongSparseArray<TLRPC.Message> botKeyboards = new LongSparseArray();
  private SparseLongArray botKeyboardsByMids = new SparseLongArray();
  private int currentAccount;
  private LongSparseArray<TLRPC.Message> draftMessages = new LongSparseArray();
  private LongSparseArray<TLRPC.DraftMessage> drafts = new LongSparseArray();
  private ArrayList<TLRPC.StickerSetCovered> featuredStickerSets = new ArrayList();
  private LongSparseArray<TLRPC.StickerSetCovered> featuredStickerSetsById = new LongSparseArray();
  private boolean featuredStickersLoaded;
  private LongSparseArray<TLRPC.TL_messages_stickerSet> groupStickerSets = new LongSparseArray();
  public ArrayList<TLRPC.TL_topPeer> hints = new ArrayList();
  private boolean inTransaction;
  public ArrayList<TLRPC.TL_topPeer> inlineBots = new ArrayList();
  private LongSparseArray<TLRPC.TL_messages_stickerSet> installedStickerSetsById = new LongSparseArray();
  private long lastMergeDialogId;
  private int lastReqId;
  private int lastReturnedNum;
  private String lastSearchQuery;
  private int[] loadDate = new int[4];
  private int loadFeaturedDate;
  private int loadFeaturedHash;
  private int[] loadHash = new int[4];
  boolean loaded;
  boolean loading;
  private boolean loadingDrafts;
  private boolean loadingFeaturedStickers;
  private boolean loadingRecentGifs;
  private boolean[] loadingRecentStickers = new boolean[3];
  private boolean[] loadingStickers = new boolean[4];
  private int mergeReqId;
  private int[] messagesSearchCount = { 0, 0 };
  private boolean[] messagesSearchEndReached = { 0, 0 };
  private SharedPreferences preferences;
  private ArrayList<Long> readingStickerSets = new ArrayList();
  private ArrayList<TLRPC.Document> recentGifs = new ArrayList();
  private boolean recentGifsLoaded;
  private ArrayList<TLRPC.Document>[] recentStickers = { new ArrayList(), new ArrayList(), new ArrayList() };
  private boolean[] recentStickersLoaded = new boolean[3];
  private int reqId;
  private ArrayList<MessageObject> searchResultMessages = new ArrayList();
  private SparseArray<MessageObject>[] searchResultMessagesMap = { new SparseArray(), new SparseArray() };
  private ArrayList<TLRPC.TL_messages_stickerSet>[] stickerSets = { new ArrayList(), new ArrayList(), new ArrayList(0), new ArrayList() };
  private LongSparseArray<TLRPC.TL_messages_stickerSet> stickerSetsById = new LongSparseArray();
  private HashMap<String, TLRPC.TL_messages_stickerSet> stickerSetsByName = new HashMap();
  private LongSparseArray<String> stickersByEmoji = new LongSparseArray();
  private boolean[] stickersLoaded = new boolean[4];
  private ArrayList<Long> unreadStickerSets = new ArrayList();
  
  public DataQuery(int paramInt)
  {
    this.currentAccount = paramInt;
    Iterator localIterator;
    if (this.currentAccount == 0)
    {
      this.preferences = ApplicationLoader.applicationContext.getSharedPreferences("drafts", 0);
      localIterator = this.preferences.getAll().entrySet().iterator();
    }
    for (;;)
    {
      if (!localIterator.hasNext()) {
        return;
      }
      Object localObject1 = (Map.Entry)localIterator.next();
      long l;
      try
      {
        Object localObject2 = (String)((Map.Entry)localObject1).getKey();
        l = Utilities.parseLong((String)localObject2).longValue();
        byte[] arrayOfByte = Utilities.hexToBytes((String)((Map.Entry)localObject1).getValue());
        localObject1 = new org/telegram/tgnet/SerializedData;
        ((SerializedData)localObject1).<init>(arrayOfByte);
        if (!((String)localObject2).startsWith("r_")) {
          break label629;
        }
        localObject2 = TLRPC.Message.TLdeserialize((AbstractSerializedData)localObject1, ((SerializedData)localObject1).readInt32(true), true);
        ((TLRPC.Message)localObject2).readAttachPath((AbstractSerializedData)localObject1, UserConfig.getInstance(this.currentAccount).clientUserId);
        if (localObject2 == null) {
          continue;
        }
        this.draftMessages.put(l, localObject2);
      }
      catch (Exception localException) {}
      continue;
      this.preferences = ApplicationLoader.applicationContext.getSharedPreferences("drafts" + this.currentAccount, 0);
      break;
      label629:
      TLRPC.DraftMessage localDraftMessage = TLRPC.DraftMessage.TLdeserialize(localException, localException.readInt32(true), true);
      if (localDraftMessage != null) {
        this.drafts.put(l, localDraftMessage);
      }
    }
  }
  
  private MessageObject broadcastPinnedMessage(final TLRPC.Message paramMessage, final ArrayList<TLRPC.User> paramArrayList, final ArrayList<TLRPC.Chat> paramArrayList1, final boolean paramBoolean1, boolean paramBoolean2)
  {
    final SparseArray localSparseArray = new SparseArray();
    for (int i = 0; i < paramArrayList.size(); i++)
    {
      localObject = (TLRPC.User)paramArrayList.get(i);
      localSparseArray.put(((TLRPC.User)localObject).id, localObject);
    }
    final Object localObject = new SparseArray();
    for (i = 0; i < paramArrayList1.size(); i++)
    {
      TLRPC.Chat localChat = (TLRPC.Chat)paramArrayList1.get(i);
      ((SparseArray)localObject).put(localChat.id, localChat);
    }
    if (paramBoolean2) {}
    for (paramMessage = new MessageObject(this.currentAccount, paramMessage, localSparseArray, (SparseArray)localObject, false);; paramMessage = null)
    {
      return paramMessage;
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          MessagesController.getInstance(DataQuery.this.currentAccount).putUsers(paramArrayList, paramBoolean1);
          MessagesController.getInstance(DataQuery.this.currentAccount).putChats(paramArrayList1, paramBoolean1);
          NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.didLoadedPinnedMessage, new Object[] { new MessageObject(DataQuery.this.currentAccount, paramMessage, localSparseArray, localObject, false) });
        }
      });
    }
  }
  
  private void broadcastReplyMessages(final ArrayList<TLRPC.Message> paramArrayList, final SparseArray<ArrayList<MessageObject>> paramSparseArray, final ArrayList<TLRPC.User> paramArrayList1, final ArrayList<TLRPC.Chat> paramArrayList2, final long paramLong, final boolean paramBoolean)
  {
    final SparseArray localSparseArray = new SparseArray();
    for (int i = 0; i < paramArrayList1.size(); i++)
    {
      localObject = (TLRPC.User)paramArrayList1.get(i);
      localSparseArray.put(((TLRPC.User)localObject).id, localObject);
    }
    final Object localObject = new SparseArray();
    for (i = 0; i < paramArrayList2.size(); i++)
    {
      TLRPC.Chat localChat = (TLRPC.Chat)paramArrayList2.get(i);
      ((SparseArray)localObject).put(localChat.id, localChat);
    }
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        MessagesController.getInstance(DataQuery.this.currentAccount).putUsers(paramArrayList1, paramBoolean);
        MessagesController.getInstance(DataQuery.this.currentAccount).putChats(paramArrayList2, paramBoolean);
        int i = 0;
        for (int j = 0; j < paramArrayList.size(); j++)
        {
          Object localObject1 = (TLRPC.Message)paramArrayList.get(j);
          ArrayList localArrayList = (ArrayList)paramSparseArray.get(((TLRPC.Message)localObject1).id);
          if (localArrayList != null)
          {
            localObject1 = new MessageObject(DataQuery.this.currentAccount, (TLRPC.Message)localObject1, localSparseArray, localObject, false);
            i = 0;
            if (i < localArrayList.size())
            {
              Object localObject2 = (MessageObject)localArrayList.get(i);
              ((MessageObject)localObject2).replyMessageObject = ((MessageObject)localObject1);
              if ((((MessageObject)localObject2).messageOwner.action instanceof TLRPC.TL_messageActionPinMessage)) {
                ((MessageObject)localObject2).generatePinMessageText(null, null);
              }
              for (;;)
              {
                if (((MessageObject)localObject2).isMegagroup())
                {
                  localObject2 = ((MessageObject)localObject2).replyMessageObject.messageOwner;
                  ((TLRPC.Message)localObject2).flags |= 0x80000000;
                }
                i++;
                break;
                if ((((MessageObject)localObject2).messageOwner.action instanceof TLRPC.TL_messageActionGameScore)) {
                  ((MessageObject)localObject2).generateGameMessageText(null);
                } else if ((((MessageObject)localObject2).messageOwner.action instanceof TLRPC.TL_messageActionPaymentSent)) {
                  ((MessageObject)localObject2).generatePaymentSentMessageText(null);
                }
              }
            }
            i = 1;
          }
        }
        if (i != 0) {
          NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.didLoadedReplyMessages, new Object[] { Long.valueOf(paramLong) });
        }
      }
    });
  }
  
  private static int calcDocumentsHash(ArrayList<TLRPC.Document> paramArrayList)
  {
    if (paramArrayList == null) {}
    long l;
    for (int i = 0;; i = (int)l)
    {
      return i;
      l = 0L;
      i = 0;
      if (i < Math.min(200, paramArrayList.size()))
      {
        TLRPC.Document localDocument = (TLRPC.Document)paramArrayList.get(i);
        if (localDocument == null) {}
        for (;;)
        {
          i++;
          break;
          int j = (int)(localDocument.id >> 32);
          int k = (int)localDocument.id;
          l = ((l * 20261L + 2147483648L + j) % 2147483648L * 20261L + 2147483648L + k) % 2147483648L;
        }
      }
    }
  }
  
  private int calcFeaturedStickersHash(ArrayList<TLRPC.StickerSetCovered> paramArrayList)
  {
    long l1 = 0L;
    int i = 0;
    if (i < paramArrayList.size())
    {
      TLRPC.StickerSet localStickerSet = ((TLRPC.StickerSetCovered)paramArrayList.get(i)).set;
      if (localStickerSet.archived) {}
      for (;;)
      {
        i++;
        break;
        int j = (int)(localStickerSet.id >> 32);
        int k = (int)localStickerSet.id;
        long l2 = ((l1 * 20261L + 2147483648L + j) % 2147483648L * 20261L + 2147483648L + k) % 2147483648L;
        l1 = l2;
        if (this.unreadStickerSets.contains(Long.valueOf(localStickerSet.id))) {
          l1 = (l2 * 20261L + 2147483648L + 1L) % 2147483648L;
        }
      }
    }
    return (int)l1;
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
        i++;
        break;
        l = (20261L * l + 2147483648L + localStickerSet.hash) % 2147483648L;
      }
    }
    return (int)l;
  }
  
  public static boolean canAddMessageToMedia(TLRPC.Message paramMessage)
  {
    boolean bool1 = false;
    boolean bool2;
    if (((paramMessage instanceof TLRPC.TL_message_secret)) && (((paramMessage.media instanceof TLRPC.TL_messageMediaPhoto)) || (MessageObject.isVideoMessage(paramMessage)) || (MessageObject.isGifMessage(paramMessage))) && (paramMessage.media.ttl_seconds != 0) && (paramMessage.media.ttl_seconds <= 60)) {
      bool2 = bool1;
    }
    do
    {
      for (;;)
      {
        return bool2;
        if ((!(paramMessage instanceof TLRPC.TL_message_secret)) && ((paramMessage instanceof TLRPC.TL_message)) && (((paramMessage.media instanceof TLRPC.TL_messageMediaPhoto)) || ((paramMessage.media instanceof TLRPC.TL_messageMediaDocument))))
        {
          bool2 = bool1;
          if (paramMessage.media.ttl_seconds != 0) {}
        }
        else
        {
          if ((!(paramMessage.media instanceof TLRPC.TL_messageMediaPhoto)) && ((!(paramMessage.media instanceof TLRPC.TL_messageMediaDocument)) || (MessageObject.isGifDocument(paramMessage.media.document)))) {
            break;
          }
          bool2 = true;
        }
      }
      bool2 = bool1;
    } while (paramMessage.entities.isEmpty());
    for (int i = 0;; i++)
    {
      bool2 = bool1;
      if (i >= paramMessage.entities.size()) {
        break;
      }
      TLRPC.MessageEntity localMessageEntity = (TLRPC.MessageEntity)paramMessage.entities.get(i);
      if (((localMessageEntity instanceof TLRPC.TL_messageEntityUrl)) || ((localMessageEntity instanceof TLRPC.TL_messageEntityTextUrl)) || ((localMessageEntity instanceof TLRPC.TL_messageEntityEmail)))
      {
        bool2 = true;
        break;
      }
    }
  }
  
  private static boolean checkInclusion(int paramInt, ArrayList<TLRPC.MessageEntity> paramArrayList)
  {
    boolean bool1 = false;
    boolean bool2 = bool1;
    if (paramArrayList != null)
    {
      if (paramArrayList.isEmpty()) {
        bool2 = bool1;
      }
    }
    else {
      return bool2;
    }
    int i = paramArrayList.size();
    for (int j = 0;; j++)
    {
      bool2 = bool1;
      if (j >= i) {
        break;
      }
      TLRPC.MessageEntity localMessageEntity = (TLRPC.MessageEntity)paramArrayList.get(j);
      if ((localMessageEntity.offset <= paramInt) && (localMessageEntity.offset + localMessageEntity.length > paramInt))
      {
        bool2 = true;
        break;
      }
    }
  }
  
  private static boolean checkIntersection(int paramInt1, int paramInt2, ArrayList<TLRPC.MessageEntity> paramArrayList)
  {
    boolean bool1 = false;
    boolean bool2 = bool1;
    if (paramArrayList != null)
    {
      if (paramArrayList.isEmpty()) {
        bool2 = bool1;
      }
    }
    else {
      return bool2;
    }
    int i = paramArrayList.size();
    for (int j = 0;; j++)
    {
      bool2 = bool1;
      if (j >= i) {
        break;
      }
      TLRPC.MessageEntity localMessageEntity = (TLRPC.MessageEntity)paramArrayList.get(j);
      if ((localMessageEntity.offset > paramInt1) && (localMessageEntity.offset + localMessageEntity.length <= paramInt2))
      {
        bool2 = true;
        break;
      }
    }
  }
  
  private Intent createIntrnalShortcutIntent(long paramLong)
  {
    Intent localIntent = new Intent(ApplicationLoader.applicationContext, OpenChatReceiver.class);
    int i = (int)paramLong;
    int j = (int)(paramLong >> 32);
    if (i == 0)
    {
      localIntent.putExtra("encId", j);
      if (MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf(j)) != null) {
        break label77;
      }
      localIntent = null;
    }
    for (;;)
    {
      return localIntent;
      if (i > 0) {
        localIntent.putExtra("userId", i);
      }
      for (;;)
      {
        label77:
        localIntent.putExtra("currentAccount", this.currentAccount);
        localIntent.setAction("com.tmessages.openchat" + paramLong);
        localIntent.addFlags(67108864);
        break;
        if (i >= 0) {
          break label144;
        }
        localIntent.putExtra("chatId", -i);
      }
      label144:
      localIntent = null;
    }
  }
  
  private void deletePeer(final int paramInt1, final int paramInt2)
  {
    MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable()
    {
      public void run()
      {
        try
        {
          MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().executeFast(String.format(Locale.US, "DELETE FROM chat_hints WHERE did = %d AND type = %d", new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2) })).stepThis().dispose();
          return;
        }
        catch (Exception localException)
        {
          for (;;)
          {
            FileLog.e(localException);
          }
        }
      }
    });
  }
  
  public static DataQuery getInstance(int paramInt)
  {
    Object localObject1 = Instance[paramInt];
    Object localObject2 = localObject1;
    if (localObject1 == null) {}
    try
    {
      localObject1 = Instance[paramInt];
      localObject2 = localObject1;
      if (localObject1 == null)
      {
        localObject1 = Instance;
        localObject2 = new org/telegram/messenger/DataQuery;
        ((DataQuery)localObject2).<init>(paramInt);
        localObject1[paramInt] = localObject2;
      }
      return (DataQuery)localObject2;
    }
    finally {}
  }
  
  private int getMask()
  {
    int i = 0;
    if ((this.lastReturnedNum < this.searchResultMessages.size() - 1) || (this.messagesSearchEndReached[0] == 0) || (this.messagesSearchEndReached[1] == 0)) {
      i = 0x0 | 0x1;
    }
    int j = i;
    if (this.lastReturnedNum > 0) {
      j = i | 0x2;
    }
    return j;
  }
  
  private void getMediaCountDatabase(final long paramLong, int paramInt1, final int paramInt2)
  {
    MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable()
    {
      public void run()
      {
        int i = -1;
        try
        {
          SQLiteCursor localSQLiteCursor = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT count FROM media_counts_v2 WHERE uid = %d AND type = %d LIMIT 1", new Object[] { Long.valueOf(paramLong), Integer.valueOf(paramInt2) }), new Object[0]);
          if (localSQLiteCursor.next()) {
            i = localSQLiteCursor.intValue(0);
          }
          localSQLiteCursor.dispose();
          int j = (int)paramLong;
          int k = i;
          if (i == -1)
          {
            k = i;
            if (j == 0)
            {
              localSQLiteCursor = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT COUNT(mid) FROM media_v2 WHERE uid = %d AND type = %d LIMIT 1", new Object[] { Long.valueOf(paramLong), Integer.valueOf(paramInt2) }), new Object[0]);
              if (localSQLiteCursor.next()) {
                i = localSQLiteCursor.intValue(0);
              }
              localSQLiteCursor.dispose();
              k = i;
              if (i != -1)
              {
                DataQuery.this.putMediaCountDatabase(paramLong, paramInt2, i);
                k = i;
              }
            }
          }
          DataQuery.this.processLoadedMediaCount(k, paramLong, paramInt2, this.val$classGuid, true);
          return;
        }
        catch (Exception localException)
        {
          for (;;)
          {
            FileLog.e(localException);
          }
        }
      }
    });
  }
  
  public static int getMediaType(TLRPC.Message paramMessage)
  {
    int i = -1;
    int j;
    if (paramMessage == null) {
      j = i;
    }
    do
    {
      for (;;)
      {
        return j;
        if ((paramMessage.media instanceof TLRPC.TL_messageMediaPhoto))
        {
          j = 0;
        }
        else
        {
          if (!(paramMessage.media instanceof TLRPC.TL_messageMediaDocument)) {
            break;
          }
          if ((MessageObject.isVoiceMessage(paramMessage)) || (MessageObject.isRoundVideoMessage(paramMessage)))
          {
            j = 2;
          }
          else if (MessageObject.isVideoMessage(paramMessage))
          {
            j = 0;
          }
          else
          {
            j = i;
            if (!MessageObject.isStickerMessage(paramMessage)) {
              if (MessageObject.isMusicMessage(paramMessage)) {
                j = 4;
              } else {
                j = 1;
              }
            }
          }
        }
      }
      j = i;
    } while (paramMessage.entities.isEmpty());
    for (int k = 0;; k++)
    {
      j = i;
      if (k >= paramMessage.entities.size()) {
        break;
      }
      TLRPC.MessageEntity localMessageEntity = (TLRPC.MessageEntity)paramMessage.entities.get(k);
      if (((localMessageEntity instanceof TLRPC.TL_messageEntityUrl)) || ((localMessageEntity instanceof TLRPC.TL_messageEntityTextUrl)) || ((localMessageEntity instanceof TLRPC.TL_messageEntityEmail)))
      {
        j = 3;
        break;
      }
    }
  }
  
  public static long getStickerSetId(TLRPC.Document paramDocument)
  {
    int i = 0;
    TLRPC.DocumentAttribute localDocumentAttribute;
    if (i < paramDocument.attributes.size())
    {
      localDocumentAttribute = (TLRPC.DocumentAttribute)paramDocument.attributes.get(i);
      if ((localDocumentAttribute instanceof TLRPC.TL_documentAttributeSticker)) {
        if (!(localDocumentAttribute.stickerset instanceof TLRPC.TL_inputStickerSetID)) {
          break label58;
        }
      }
    }
    label58:
    for (long l = localDocumentAttribute.stickerset.id;; l = -1L)
    {
      return l;
      i++;
      break;
    }
  }
  
  private void loadGroupStickerSet(final TLRPC.StickerSet paramStickerSet, boolean paramBoolean)
  {
    if (paramBoolean) {
      MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable()
      {
        public void run()
        {
          for (;;)
          {
            try
            {
              Object localObject1 = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase();
              localObject2 = new java/lang/StringBuilder;
              ((StringBuilder)localObject2).<init>();
              localObject1 = ((SQLiteDatabase)localObject1).queryFinalized("SELECT document FROM web_recent_v3 WHERE id = 's_" + paramStickerSet.id + "'", new Object[0]);
              if ((!((SQLiteCursor)localObject1).next()) || (((SQLiteCursor)localObject1).isNull(0))) {
                continue;
              }
              NativeByteBuffer localNativeByteBuffer = ((SQLiteCursor)localObject1).byteBufferValue(0);
              if (localNativeByteBuffer != null)
              {
                localObject2 = TLRPC.TL_messages_stickerSet.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
                localNativeByteBuffer.reuse();
                ((SQLiteCursor)localObject1).dispose();
                if ((localObject2 == null) || (((TLRPC.TL_messages_stickerSet)localObject2).set == null) || (((TLRPC.TL_messages_stickerSet)localObject2).set.hash != paramStickerSet.hash)) {
                  DataQuery.this.loadGroupStickerSet(paramStickerSet, false);
                }
                if ((localObject2 != null) && (((TLRPC.TL_messages_stickerSet)localObject2).set != null))
                {
                  localObject1 = new org/telegram/messenger/DataQuery$6$1;
                  ((1)localObject1).<init>(this, (TLRPC.TL_messages_stickerSet)localObject2);
                  AndroidUtilities.runOnUIThread((Runnable)localObject1);
                }
                return;
              }
            }
            catch (Throwable localThrowable)
            {
              Object localObject2;
              FileLog.e(localThrowable);
              continue;
            }
            localObject2 = null;
            continue;
            localObject2 = null;
          }
        }
      });
    }
    for (;;)
    {
      return;
      TLRPC.TL_messages_getStickerSet localTL_messages_getStickerSet = new TLRPC.TL_messages_getStickerSet();
      localTL_messages_getStickerSet.stickerset = new TLRPC.TL_inputStickerSetID();
      localTL_messages_getStickerSet.stickerset.id = paramStickerSet.id;
      localTL_messages_getStickerSet.stickerset.access_hash = paramStickerSet.access_hash;
      ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_messages_getStickerSet, new RequestDelegate()
      {
        public void run(final TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
        {
          if (paramAnonymousTLObject != null)
          {
            paramAnonymousTLObject = (TLRPC.TL_messages_stickerSet)paramAnonymousTLObject;
            MessagesStorage.getInstance(DataQuery.this.currentAccount).getStorageQueue().postRunnable(new Runnable()
            {
              public void run()
              {
                try
                {
                  SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().executeFast("REPLACE INTO web_recent_v3 VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                  localSQLitePreparedStatement.requery();
                  Object localObject = new java/lang/StringBuilder;
                  ((StringBuilder)localObject).<init>();
                  localSQLitePreparedStatement.bindString(1, "s_" + paramAnonymousTLObject.set.id);
                  localSQLitePreparedStatement.bindInteger(2, 6);
                  localSQLitePreparedStatement.bindString(3, "");
                  localSQLitePreparedStatement.bindString(4, "");
                  localSQLitePreparedStatement.bindString(5, "");
                  localSQLitePreparedStatement.bindInteger(6, 0);
                  localSQLitePreparedStatement.bindInteger(7, 0);
                  localSQLitePreparedStatement.bindInteger(8, 0);
                  localSQLitePreparedStatement.bindInteger(9, 0);
                  localObject = new org/telegram/tgnet/NativeByteBuffer;
                  ((NativeByteBuffer)localObject).<init>(paramAnonymousTLObject.getObjectSize());
                  paramAnonymousTLObject.serializeToStream((AbstractSerializedData)localObject);
                  localSQLitePreparedStatement.bindByteBuffer(10, (NativeByteBuffer)localObject);
                  localSQLitePreparedStatement.step();
                  ((NativeByteBuffer)localObject).reuse();
                  localSQLitePreparedStatement.dispose();
                  return;
                }
                catch (Exception localException)
                {
                  for (;;)
                  {
                    FileLog.e(localException);
                  }
                }
              }
            });
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                DataQuery.this.groupStickerSets.put(paramAnonymousTLObject.set.id, paramAnonymousTLObject);
                NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.groupStickersDidLoaded, new Object[] { Long.valueOf(paramAnonymousTLObject.set.id) });
              }
            });
          }
        }
      });
    }
  }
  
  private void loadMediaDatabase(final long paramLong, final int paramInt1, int paramInt2, final int paramInt3, final int paramInt4, final boolean paramBoolean)
  {
    MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable()
    {
      public void run()
      {
        TLRPC.TL_messages_messages localTL_messages_messages = new TLRPC.TL_messages_messages();
        ArrayList localArrayList1;
        ArrayList localArrayList2;
        int i;
        boolean bool;
        int j;
        long l1;
        long l2;
        Object localObject3;
        try
        {
          localArrayList1 = new java/util/ArrayList;
          localArrayList1.<init>();
          localArrayList2 = new java/util/ArrayList;
          localArrayList2.<init>();
          i = paramInt1 + 1;
          localObject1 = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase();
          bool = false;
          if ((int)paramLong == 0) {
            break label1028;
          }
          j = 0;
          l1 = paramBoolean;
          if (paramInt3) {
            j = -(int)paramLong;
          }
          l2 = l1;
          if (l1 != 0L)
          {
            l2 = l1;
            if (j != 0) {
              l2 = l1 | j << 32;
            }
          }
          localObject3 = ((SQLiteDatabase)localObject1).queryFinalized(String.format(Locale.US, "SELECT start FROM media_holes_v2 WHERE uid = %d AND type = %d AND start IN (0, 1)", new Object[] { Long.valueOf(paramLong), Integer.valueOf(paramInt4) }), new Object[0]);
          if (!((SQLiteCursor)localObject3).next()) {
            break label579;
          }
          if (((SQLiteCursor)localObject3).intValue(0) != 1) {
            break label573;
          }
          bool = true;
        }
        catch (Exception localException)
        {
          for (;;)
          {
            Object localObject1;
            long l3;
            localTL_messages_messages.messages.clear();
            localTL_messages_messages.chats.clear();
            localTL_messages_messages.users.clear();
            FileLog.e(localException);
            return;
            label573:
            bool = false;
            continue;
            label579:
            ((SQLiteCursor)localObject3).dispose();
            localObject4 = localException.queryFinalized(String.format(Locale.US, "SELECT min(mid) FROM media_v2 WHERE uid = %d AND type = %d AND mid > 0", new Object[] { Long.valueOf(paramLong), Integer.valueOf(paramInt4) }), new Object[0]);
            if (((SQLiteCursor)localObject4).next())
            {
              k = ((SQLiteCursor)localObject4).intValue(0);
              if (k != 0)
              {
                localObject3 = localException.executeFast("REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)");
                ((SQLitePreparedStatement)localObject3).requery();
                ((SQLitePreparedStatement)localObject3).bindLong(1, paramLong);
                ((SQLitePreparedStatement)localObject3).bindInteger(2, paramInt4);
                ((SQLitePreparedStatement)localObject3).bindInteger(3, 0);
                ((SQLitePreparedStatement)localObject3).bindInteger(4, k);
                ((SQLitePreparedStatement)localObject3).step();
                ((SQLitePreparedStatement)localObject3).dispose();
              }
            }
            ((SQLiteCursor)localObject4).dispose();
          }
        }
        finally
        {
          DataQuery.this.processLoadedMedia(localTL_messages_messages, paramLong, paramInt1, paramBoolean, paramInt4, true, this.val$classGuid, paramInt3, false);
        }
        ((SQLiteCursor)localObject3).dispose();
        if (l2 != 0L)
        {
          l1 = 0L;
          localObject3 = ((SQLiteDatabase)localObject1).queryFinalized(String.format(Locale.US, "SELECT end FROM media_holes_v2 WHERE uid = %d AND type = %d AND end <= %d ORDER BY end DESC LIMIT 1", new Object[] { Long.valueOf(paramLong), Integer.valueOf(paramInt4), Integer.valueOf(paramBoolean) }), new Object[0]);
          if (((SQLiteCursor)localObject3).next())
          {
            l3 = ((SQLiteCursor)localObject3).intValue(0);
            l1 = l3;
            if (j != 0) {
              l1 = l3 | j << 32;
            }
          }
          ((SQLiteCursor)localObject3).dispose();
          if (l1 > 1L) {
            localObject1 = ((SQLiteDatabase)localObject1).queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v2 WHERE uid = %d AND mid > 0 AND mid < %d AND mid >= %d AND type = %d ORDER BY date DESC, mid DESC LIMIT %d", new Object[] { Long.valueOf(paramLong), Long.valueOf(l2), Long.valueOf(l1), Integer.valueOf(paramInt4), Integer.valueOf(i) }), new Object[0]);
          }
        }
        SQLiteCursor localSQLiteCursor;
        while (((SQLiteCursor)localObject1).next())
        {
          localObject3 = ((SQLiteCursor)localObject1).byteBufferValue(0);
          if (localObject3 != null)
          {
            Object localObject4 = TLRPC.Message.TLdeserialize((AbstractSerializedData)localObject3, ((NativeByteBuffer)localObject3).readInt32(false), false);
            ((TLRPC.Message)localObject4).readAttachPath((AbstractSerializedData)localObject3, UserConfig.getInstance(DataQuery.this.currentAccount).clientUserId);
            ((NativeByteBuffer)localObject3).reuse();
            ((TLRPC.Message)localObject4).id = ((SQLiteCursor)localObject1).intValue(1);
            ((TLRPC.Message)localObject4).dialog_id = paramLong;
            if ((int)paramLong == 0) {
              ((TLRPC.Message)localObject4).random_id = ((SQLiteCursor)localObject1).longValue(2);
            }
            localTL_messages_messages.messages.add(localObject4);
            if (((TLRPC.Message)localObject4).from_id > 0)
            {
              if (!localArrayList1.contains(Integer.valueOf(((TLRPC.Message)localObject4).from_id)))
              {
                localArrayList1.add(Integer.valueOf(((TLRPC.Message)localObject4).from_id));
                continue;
                int k;
                localSQLiteCursor = ((SQLiteDatabase)localObject2).queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v2 WHERE uid = %d AND mid > 0 AND mid < %d AND type = %d ORDER BY date DESC, mid DESC LIMIT %d", new Object[] { Long.valueOf(paramLong), Long.valueOf(l2), Integer.valueOf(paramInt4), Integer.valueOf(i) }), new Object[0]);
                continue;
                l1 = 0L;
                localObject3 = localSQLiteCursor.queryFinalized(String.format(Locale.US, "SELECT max(end) FROM media_holes_v2 WHERE uid = %d AND type = %d", new Object[] { Long.valueOf(paramLong), Integer.valueOf(paramInt4) }), new Object[0]);
                if (((SQLiteCursor)localObject3).next())
                {
                  l2 = ((SQLiteCursor)localObject3).intValue(0);
                  l1 = l2;
                  if (j != 0) {
                    l1 = l2 | j << 32;
                  }
                }
                ((SQLiteCursor)localObject3).dispose();
                if (l1 > 1L)
                {
                  localSQLiteCursor = localSQLiteCursor.queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v2 WHERE uid = %d AND mid >= %d AND type = %d ORDER BY date DESC, mid DESC LIMIT %d", new Object[] { Long.valueOf(paramLong), Long.valueOf(l1), Integer.valueOf(paramInt4), Integer.valueOf(i) }), new Object[0]);
                }
                else
                {
                  localSQLiteCursor = localSQLiteCursor.queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v2 WHERE uid = %d AND mid > 0 AND type = %d ORDER BY date DESC, mid DESC LIMIT %d", new Object[] { Long.valueOf(paramLong), Integer.valueOf(paramInt4), Integer.valueOf(i) }), new Object[0]);
                  continue;
                  label1028:
                  bool = true;
                  if (paramBoolean != 0) {
                    localSQLiteCursor = localSQLiteCursor.queryFinalized(String.format(Locale.US, "SELECT m.data, m.mid, r.random_id FROM media_v2 as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid > %d AND type = %d ORDER BY m.mid ASC LIMIT %d", new Object[] { Long.valueOf(paramLong), Integer.valueOf(paramBoolean), Integer.valueOf(paramInt4), Integer.valueOf(i) }), new Object[0]);
                  } else {
                    localSQLiteCursor = localSQLiteCursor.queryFinalized(String.format(Locale.US, "SELECT m.data, m.mid, r.random_id FROM media_v2 as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND type = %d ORDER BY m.mid ASC LIMIT %d", new Object[] { Long.valueOf(paramLong), Integer.valueOf(paramInt4), Integer.valueOf(i) }), new Object[0]);
                  }
                }
              }
            }
            else if (!localArrayList2.contains(Integer.valueOf(-((TLRPC.Message)localObject4).from_id))) {
              localArrayList2.add(Integer.valueOf(-((TLRPC.Message)localObject4).from_id));
            }
          }
        }
        localSQLiteCursor.dispose();
        if (!localArrayList1.isEmpty()) {
          MessagesStorage.getInstance(DataQuery.this.currentAccount).getUsersInternal(TextUtils.join(",", localArrayList1), localTL_messages_messages.users);
        }
        if (!localArrayList2.isEmpty()) {
          MessagesStorage.getInstance(DataQuery.this.currentAccount).getChatsInternal(TextUtils.join(",", localArrayList2), localTL_messages_messages.chats);
        }
        if (localTL_messages_messages.messages.size() > paramInt1)
        {
          bool = false;
          localTL_messages_messages.messages.remove(localTL_messages_messages.messages.size() - 1);
        }
        for (;;)
        {
          DataQuery.this.processLoadedMedia(localTL_messages_messages, paramLong, paramInt1, paramBoolean, paramInt4, true, this.val$classGuid, paramInt3, bool);
          break;
        }
      }
    });
  }
  
  private MessageObject loadPinnedMessageInternal(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    long l1 = paramInt2;
    l2 = paramInt1;
    localObject1 = null;
    for (;;)
    {
      try
      {
        localObject2 = new java/util/ArrayList;
        ((ArrayList)localObject2).<init>();
        localArrayList1 = new java/util/ArrayList;
        localArrayList1.<init>();
        localArrayList2 = new java/util/ArrayList;
        localArrayList2.<init>();
        localArrayList3 = new java/util/ArrayList;
        localArrayList3.<init>();
        localSQLiteCursor = MessagesStorage.getInstance(this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, mid, date FROM messages WHERE mid = %d", new Object[] { Long.valueOf(l1 | l2 << 32) }), new Object[0]);
        localObject3 = localObject1;
        NativeByteBuffer localNativeByteBuffer;
        if (localSQLiteCursor.next())
        {
          localNativeByteBuffer = localSQLiteCursor.byteBufferValue(0);
          localObject3 = localObject1;
          if (localNativeByteBuffer != null)
          {
            localObject3 = TLRPC.Message.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
            ((TLRPC.Message)localObject3).readAttachPath(localNativeByteBuffer, UserConfig.getInstance(this.currentAccount).clientUserId);
            localNativeByteBuffer.reuse();
            if (!(((TLRPC.Message)localObject3).action instanceof TLRPC.TL_messageActionHistoryClear)) {
              continue;
            }
            localObject3 = null;
          }
        }
        localSQLiteCursor.dispose();
        localObject1 = localObject3;
        if (localObject3 != null) {
          continue;
        }
        localSQLiteCursor = MessagesStorage.getInstance(this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT data FROM chat_pinned WHERE uid = %d", new Object[] { Integer.valueOf(paramInt1) }), new Object[0]);
        localObject1 = localObject3;
        if (localSQLiteCursor.next())
        {
          localNativeByteBuffer = localSQLiteCursor.byteBufferValue(0);
          localObject1 = localObject3;
          if (localNativeByteBuffer != null)
          {
            localObject1 = TLRPC.Message.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
            ((TLRPC.Message)localObject1).readAttachPath(localNativeByteBuffer, UserConfig.getInstance(this.currentAccount).clientUserId);
            localNativeByteBuffer.reuse();
            if ((((TLRPC.Message)localObject1).id == paramInt2) && (!(((TLRPC.Message)localObject1).action instanceof TLRPC.TL_messageActionHistoryClear))) {
              continue;
            }
            localObject1 = null;
          }
        }
      }
      catch (Exception localException)
      {
        Object localObject2;
        ArrayList localArrayList1;
        ArrayList localArrayList2;
        ArrayList localArrayList3;
        SQLiteCursor localSQLiteCursor;
        Object localObject3;
        FileLog.e(localException);
        continue;
        l2 = -paramInt1;
        ((TLRPC.Message)localObject1).dialog_id = l2;
        MessagesStorage.addUsersAndChatsFromMessage((TLRPC.Message)localObject1, localArrayList2, localArrayList3);
        continue;
        if (!paramBoolean) {
          continue;
        }
        MessageObject localMessageObject = broadcastPinnedMessage((TLRPC.Message)localObject1, (ArrayList)localObject2, localArrayList1, true, paramBoolean);
        continue;
        if (localArrayList2.isEmpty()) {
          continue;
        }
        MessagesStorage.getInstance(this.currentAccount).getUsersInternal(TextUtils.join(",", localArrayList2), (ArrayList)localObject2);
        if (localArrayList3.isEmpty()) {
          continue;
        }
        MessagesStorage.getInstance(this.currentAccount).getChatsInternal(TextUtils.join(",", localArrayList3), localArrayList1);
        broadcastPinnedMessage((TLRPC.Message)localObject1, (ArrayList)localObject2, localArrayList1, true, false);
        continue;
      }
      localSQLiteCursor.dispose();
      if (localObject1 != null) {
        continue;
      }
      localObject1 = new org/telegram/tgnet/TLRPC$TL_channels_getMessages;
      ((TLRPC.TL_channels_getMessages)localObject1).<init>();
      ((TLRPC.TL_channels_getMessages)localObject1).channel = MessagesController.getInstance(this.currentAccount).getInputChannel(paramInt1);
      ((TLRPC.TL_channels_getMessages)localObject1).id.add(Integer.valueOf(paramInt2));
      localObject2 = ConnectionsManager.getInstance(this.currentAccount);
      localObject3 = new org/telegram/messenger/DataQuery$53;
      ((53)localObject3).<init>(this, paramInt1);
      ((ConnectionsManager)localObject2).sendRequest((TLObject)localObject1, (RequestDelegate)localObject3);
      localObject3 = null;
      return (MessageObject)localObject3;
      ((TLRPC.Message)localObject3).id = localSQLiteCursor.intValue(1);
      ((TLRPC.Message)localObject3).date = localSQLiteCursor.intValue(2);
      ((TLRPC.Message)localObject3).dialog_id = (-paramInt1);
      MessagesStorage.addUsersAndChatsFromMessage((TLRPC.Message)localObject3, localArrayList2, localArrayList3);
    }
  }
  
  private void processLoadStickersResponse(final int paramInt, final TLRPC.TL_messages_allStickers paramTL_messages_allStickers)
  {
    final ArrayList localArrayList = new ArrayList();
    if (paramTL_messages_allStickers.sets.isEmpty())
    {
      processLoadedStickers(paramInt, localArrayList, false, (int)(System.currentTimeMillis() / 1000L), paramTL_messages_allStickers.hash);
      return;
    }
    final LongSparseArray localLongSparseArray = new LongSparseArray();
    final int i = 0;
    label50:
    final TLRPC.StickerSet localStickerSet;
    Object localObject;
    if (i < paramTL_messages_allStickers.sets.size())
    {
      localStickerSet = (TLRPC.StickerSet)paramTL_messages_allStickers.sets.get(i);
      localObject = (TLRPC.TL_messages_stickerSet)this.stickerSetsById.get(localStickerSet.id);
      if ((localObject == null) || (((TLRPC.TL_messages_stickerSet)localObject).set.hash != localStickerSet.hash)) {
        break label215;
      }
      ((TLRPC.TL_messages_stickerSet)localObject).set.archived = localStickerSet.archived;
      ((TLRPC.TL_messages_stickerSet)localObject).set.installed = localStickerSet.installed;
      ((TLRPC.TL_messages_stickerSet)localObject).set.official = localStickerSet.official;
      localLongSparseArray.put(((TLRPC.TL_messages_stickerSet)localObject).set.id, localObject);
      localArrayList.add(localObject);
      if (localLongSparseArray.size() == paramTL_messages_allStickers.sets.size()) {
        processLoadedStickers(paramInt, localArrayList, false, (int)(System.currentTimeMillis() / 1000L), paramTL_messages_allStickers.hash);
      }
    }
    for (;;)
    {
      i++;
      break label50;
      break;
      label215:
      localArrayList.add(null);
      localObject = new TLRPC.TL_messages_getStickerSet();
      ((TLRPC.TL_messages_getStickerSet)localObject).stickerset = new TLRPC.TL_inputStickerSetID();
      ((TLRPC.TL_messages_getStickerSet)localObject).stickerset.id = localStickerSet.id;
      ((TLRPC.TL_messages_getStickerSet)localObject).stickerset.access_hash = localStickerSet.access_hash;
      ConnectionsManager.getInstance(this.currentAccount).sendRequest((TLObject)localObject, new RequestDelegate()
      {
        public void run(final TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
        {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              TLRPC.TL_messages_stickerSet localTL_messages_stickerSet = (TLRPC.TL_messages_stickerSet)paramAnonymousTLObject;
              DataQuery.23.this.val$newStickerArray.set(DataQuery.23.this.val$index, localTL_messages_stickerSet);
              DataQuery.23.this.val$newStickerSets.put(DataQuery.23.this.val$stickerSet.id, localTL_messages_stickerSet);
              if (DataQuery.23.this.val$newStickerSets.size() == DataQuery.23.this.val$res.sets.size())
              {
                for (int i = 0; i < DataQuery.23.this.val$newStickerArray.size(); i++) {
                  if (DataQuery.23.this.val$newStickerArray.get(i) == null) {
                    DataQuery.23.this.val$newStickerArray.remove(i);
                  }
                }
                DataQuery.this.processLoadedStickers(DataQuery.23.this.val$type, DataQuery.23.this.val$newStickerArray, false, (int)(System.currentTimeMillis() / 1000L), DataQuery.23.this.val$res.hash);
              }
            }
          });
        }
      });
    }
  }
  
  private void processLoadedFeaturedStickers(final ArrayList<TLRPC.StickerSetCovered> paramArrayList, final ArrayList<Long> paramArrayList1, final boolean paramBoolean, final int paramInt1, final int paramInt2)
  {
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        DataQuery.access$1202(DataQuery.this, false);
        DataQuery.access$1302(DataQuery.this, true);
      }
    });
    Utilities.stageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        long l = 1000L;
        Object localObject1;
        if (((paramBoolean) && ((paramArrayList == null) || (Math.abs(System.currentTimeMillis() / 1000L - paramInt1) >= 3600L))) || ((!paramBoolean) && (paramArrayList == null) && (paramInt2 == 0)))
        {
          localObject1 = new Runnable()
          {
            public void run()
            {
              if ((DataQuery.17.this.val$res != null) && (DataQuery.17.this.val$hash != 0)) {
                DataQuery.access$1402(DataQuery.this, DataQuery.17.this.val$hash);
              }
              DataQuery.this.loadFeaturedStickers(false, false);
            }
          };
          if ((paramArrayList == null) && (!paramBoolean))
          {
            AndroidUtilities.runOnUIThread((Runnable)localObject1, l);
            if (paramArrayList != null) {
              break label103;
            }
          }
        }
        for (;;)
        {
          return;
          l = 0L;
          break;
          label103:
          if (paramArrayList != null)
          {
            try
            {
              ArrayList localArrayList = new java/util/ArrayList;
              localArrayList.<init>();
              localObject1 = new android/util/LongSparseArray;
              ((LongSparseArray)localObject1).<init>();
              for (int i = 0; i < paramArrayList.size(); i++)
              {
                localObject2 = (TLRPC.StickerSetCovered)paramArrayList.get(i);
                localArrayList.add(localObject2);
                ((LongSparseArray)localObject1).put(((TLRPC.StickerSetCovered)localObject2).set.id, localObject2);
              }
              if (!paramBoolean) {
                DataQuery.this.putFeaturedStickersToCache(localArrayList, paramArrayList1, paramInt1, paramInt2);
              }
              Object localObject2 = new org/telegram/messenger/DataQuery$17$2;
              ((2)localObject2).<init>(this, (LongSparseArray)localObject1, localArrayList);
              AndroidUtilities.runOnUIThread((Runnable)localObject2);
            }
            catch (Throwable localThrowable)
            {
              FileLog.e(localThrowable);
            }
          }
          else if (!paramBoolean)
          {
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                DataQuery.access$1902(DataQuery.this, DataQuery.17.this.val$date);
              }
            });
            DataQuery.this.putFeaturedStickersToCache(null, null, paramInt1, 0);
          }
        }
      }
    });
  }
  
  private void processLoadedMedia(final TLRPC.messages_Messages parammessages_Messages, final long paramLong, int paramInt1, int paramInt2, final int paramInt3, final boolean paramBoolean1, final int paramInt4, boolean paramBoolean2, final boolean paramBoolean3)
  {
    int i = (int)paramLong;
    if ((paramBoolean1) && (parammessages_Messages.messages.isEmpty()) && (i != 0)) {
      loadMedia(paramLong, paramInt1, paramInt2, paramInt3, false, paramInt4);
    }
    for (;;)
    {
      return;
      if (!paramBoolean1)
      {
        ImageLoader.saveMessagesThumbs(parammessages_Messages.messages);
        MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(parammessages_Messages.users, parammessages_Messages.chats, true, true);
        putMediaDatabase(paramLong, paramInt3, parammessages_Messages.messages, paramInt2, paramBoolean3);
      }
      SparseArray localSparseArray = new SparseArray();
      for (paramInt1 = 0; paramInt1 < parammessages_Messages.users.size(); paramInt1++)
      {
        localObject = (TLRPC.User)parammessages_Messages.users.get(paramInt1);
        localSparseArray.put(((TLRPC.User)localObject).id, localObject);
      }
      Object localObject = new ArrayList();
      for (paramInt1 = 0; paramInt1 < parammessages_Messages.messages.size(); paramInt1++)
      {
        TLRPC.Message localMessage = (TLRPC.Message)parammessages_Messages.messages.get(paramInt1);
        ((ArrayList)localObject).add(new MessageObject(this.currentAccount, localMessage, localSparseArray, true));
      }
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          int i = parammessages_Messages.count;
          MessagesController.getInstance(DataQuery.this.currentAccount).putUsers(parammessages_Messages.users, paramBoolean1);
          MessagesController.getInstance(DataQuery.this.currentAccount).putChats(parammessages_Messages.chats, paramBoolean1);
          NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.mediaDidLoaded, new Object[] { Long.valueOf(paramLong), Integer.valueOf(i), paramInt4, Integer.valueOf(paramInt3), Integer.valueOf(paramBoolean3), Boolean.valueOf(this.val$topReached) });
        }
      });
    }
  }
  
  private void processLoadedMediaCount(final int paramInt1, final long paramLong, final int paramInt2, final int paramInt3, boolean paramBoolean)
  {
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        int i = 0;
        int j = (int)paramLong;
        if ((paramInt1) && (paramInt2 == -1) && (j != 0))
        {
          DataQuery.this.getMediaCount(paramLong, paramInt3, this.val$classGuid, false);
          return;
        }
        if (!paramInt1) {
          DataQuery.this.putMediaCountDatabase(paramLong, paramInt3, paramInt2);
        }
        NotificationCenter localNotificationCenter = NotificationCenter.getInstance(DataQuery.this.currentAccount);
        j = NotificationCenter.mediaCountDidLoaded;
        long l = paramLong;
        if ((paramInt1) && (paramInt2 == -1)) {}
        for (;;)
        {
          localNotificationCenter.postNotificationName(j, new Object[] { Long.valueOf(l), Integer.valueOf(i), Boolean.valueOf(paramInt1), Integer.valueOf(paramInt3) });
          break;
          i = paramInt2;
        }
      }
    });
  }
  
  private void processLoadedRecentDocuments(final int paramInt1, final ArrayList<TLRPC.Document> paramArrayList, final boolean paramBoolean, final int paramInt2)
  {
    if (paramArrayList != null) {
      MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable()
      {
        public void run()
        {
          try
          {
            SQLiteDatabase localSQLiteDatabase = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase();
            int i;
            Object localObject1;
            int j;
            int k;
            if (paramBoolean)
            {
              i = MessagesController.getInstance(DataQuery.this.currentAccount).maxRecentGifsCount;
              localSQLiteDatabase.beginTransaction();
              localObject1 = localSQLiteDatabase.executeFast("REPLACE INTO web_recent_v3 VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
              j = paramArrayList.size();
              if (!paramBoolean) {
                break label217;
              }
              k = 2;
            }
            int m;
            for (;;)
            {
              m = 0;
              if ((m < j) && (m != i)) {
                break label250;
              }
              ((SQLitePreparedStatement)localObject1).dispose();
              localSQLiteDatabase.commitTransaction();
              if (paramArrayList.size() < i) {
                break label436;
              }
              localSQLiteDatabase.beginTransaction();
              while (i < paramArrayList.size())
              {
                localObject1 = new java/lang/StringBuilder;
                ((StringBuilder)localObject1).<init>();
                localSQLiteDatabase.executeFast("DELETE FROM web_recent_v3 WHERE id = '" + ((TLRPC.Document)paramArrayList.get(i)).id + "' AND type = " + k).stepThis().dispose();
                i++;
              }
              if (paramInt1 == 2)
              {
                i = MessagesController.getInstance(DataQuery.this.currentAccount).maxFaveStickersCount;
                break;
              }
              i = MessagesController.getInstance(DataQuery.this.currentAccount).maxRecentStickersCount;
              break;
              label217:
              if (paramInt1 == 0) {
                k = 3;
              } else if (paramInt1 == 1) {
                k = 4;
              } else {
                k = 5;
              }
            }
            label250:
            TLRPC.Document localDocument = (TLRPC.Document)paramArrayList.get(m);
            ((SQLitePreparedStatement)localObject1).requery();
            Object localObject2 = new java/lang/StringBuilder;
            ((StringBuilder)localObject2).<init>();
            ((SQLitePreparedStatement)localObject1).bindString(1, "" + localDocument.id);
            ((SQLitePreparedStatement)localObject1).bindInteger(2, k);
            ((SQLitePreparedStatement)localObject1).bindString(3, "");
            ((SQLitePreparedStatement)localObject1).bindString(4, "");
            ((SQLitePreparedStatement)localObject1).bindString(5, "");
            ((SQLitePreparedStatement)localObject1).bindInteger(6, 0);
            ((SQLitePreparedStatement)localObject1).bindInteger(7, 0);
            ((SQLitePreparedStatement)localObject1).bindInteger(8, 0);
            if (paramInt2 != 0) {}
            for (int n = paramInt2;; n = j - m)
            {
              ((SQLitePreparedStatement)localObject1).bindInteger(9, n);
              localObject2 = new org/telegram/tgnet/NativeByteBuffer;
              ((NativeByteBuffer)localObject2).<init>(localDocument.getObjectSize());
              localDocument.serializeToStream((AbstractSerializedData)localObject2);
              ((SQLitePreparedStatement)localObject1).bindByteBuffer(10, (NativeByteBuffer)localObject2);
              ((SQLitePreparedStatement)localObject1).step();
              if (localObject2 != null) {
                ((NativeByteBuffer)localObject2).reuse();
              }
              m++;
              break;
            }
            localSQLiteDatabase.commitTransaction();
            label436:
            return;
          }
          catch (Exception localException)
          {
            for (;;)
            {
              FileLog.e(localException);
            }
          }
        }
      });
    }
    if (paramInt2 == 0) {
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          SharedPreferences.Editor localEditor = MessagesController.getEmojiSettings(DataQuery.this.currentAccount).edit();
          if (paramBoolean)
          {
            DataQuery.access$402(DataQuery.this, false);
            DataQuery.access$502(DataQuery.this, true);
            localEditor.putLong("lastGifLoadTime", System.currentTimeMillis()).commit();
            if (paramArrayList != null)
            {
              if (!paramBoolean) {
                break label226;
              }
              DataQuery.access$302(DataQuery.this, paramArrayList);
            }
          }
          for (;;)
          {
            NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.recentDocumentsDidLoaded, new Object[] { Boolean.valueOf(paramBoolean), Integer.valueOf(paramInt1) });
            return;
            DataQuery.this.loadingRecentStickers[paramInt1] = 0;
            DataQuery.this.recentStickersLoaded[paramInt1] = 1;
            if (paramInt1 == 0)
            {
              localEditor.putLong("lastStickersLoadTime", System.currentTimeMillis()).commit();
              break;
            }
            if (paramInt1 == 1)
            {
              localEditor.putLong("lastStickersLoadTimeMask", System.currentTimeMillis()).commit();
              break;
            }
            localEditor.putLong("lastStickersLoadTimeFavs", System.currentTimeMillis()).commit();
            break;
            label226:
            DataQuery.this.recentStickers[paramInt1] = paramArrayList;
          }
        }
      });
    }
  }
  
  private void processLoadedStickers(final int paramInt1, final ArrayList<TLRPC.TL_messages_stickerSet> paramArrayList, final boolean paramBoolean, final int paramInt2, final int paramInt3)
  {
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        DataQuery.this.loadingStickers[paramInt1] = 0;
        DataQuery.this.stickersLoaded[paramInt1] = 1;
      }
    });
    Utilities.stageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        Object localObject1;
        long l;
        if (((paramBoolean) && ((paramArrayList == null) || (Math.abs(System.currentTimeMillis() / 1000L - paramInt2) >= 3600L))) || ((!paramBoolean) && (paramArrayList == null) && (paramInt3 == 0)))
        {
          localObject1 = new Runnable()
          {
            public void run()
            {
              if ((DataQuery.28.this.val$res != null) && (DataQuery.28.this.val$hash != 0)) {
                DataQuery.this.loadHash[DataQuery.28.this.val$type] = DataQuery.28.this.val$hash;
              }
              DataQuery.this.loadStickers(DataQuery.28.this.val$type, false, false);
            }
          };
          if ((paramArrayList == null) && (!paramBoolean))
          {
            l = 1000L;
            AndroidUtilities.runOnUIThread((Runnable)localObject1, l);
            if (paramArrayList != null) {
              break label103;
            }
          }
        }
        for (;;)
        {
          return;
          l = 0L;
          break;
          label103:
          if (paramArrayList != null)
          {
            ArrayList localArrayList;
            LongSparseArray localLongSparseArray1;
            HashMap localHashMap1;
            LongSparseArray localLongSparseArray2;
            LongSparseArray localLongSparseArray3;
            HashMap localHashMap2;
            TLRPC.TL_messages_stickerSet localTL_messages_stickerSet;
            do
            {
              try
              {
                localArrayList = new java/util/ArrayList;
                localArrayList.<init>();
                localLongSparseArray1 = new android/util/LongSparseArray;
                localLongSparseArray1.<init>();
                localHashMap1 = new java/util/HashMap;
                localHashMap1.<init>();
                localLongSparseArray2 = new android/util/LongSparseArray;
                localLongSparseArray2.<init>();
                localLongSparseArray3 = new android/util/LongSparseArray;
                localLongSparseArray3.<init>();
                localHashMap2 = new java/util/HashMap;
                localHashMap2.<init>();
                for (int i = 0;; i++)
                {
                  if (i >= paramArrayList.size()) {
                    break label530;
                  }
                  localTL_messages_stickerSet = (TLRPC.TL_messages_stickerSet)paramArrayList.get(i);
                  if (localTL_messages_stickerSet != null) {
                    break;
                  }
                }
                localArrayList.add(localTL_messages_stickerSet);
                localLongSparseArray1.put(localTL_messages_stickerSet.set.id, localTL_messages_stickerSet);
                localHashMap1.put(localTL_messages_stickerSet.set.short_name, localTL_messages_stickerSet);
                j = 0;
                if (j < localTL_messages_stickerSet.documents.size())
                {
                  localObject1 = (TLRPC.Document)localTL_messages_stickerSet.documents.get(j);
                  if ((localObject1 == null) || ((localObject1 instanceof TLRPC.TL_documentEmpty))) {}
                  for (;;)
                  {
                    j++;
                    break;
                    localLongSparseArray3.put(((TLRPC.Document)localObject1).id, localObject1);
                  }
                }
              }
              catch (Throwable localThrowable)
              {
                FileLog.e(localThrowable);
              }
            } while (localTL_messages_stickerSet.set.archived);
            int j = 0;
            label331:
            TLRPC.TL_stickerPack localTL_stickerPack;
            if (j < localTL_messages_stickerSet.packs.size())
            {
              localTL_stickerPack = (TLRPC.TL_stickerPack)localTL_messages_stickerSet.packs.get(j);
              if ((localTL_stickerPack != null) && (localTL_stickerPack.emoticon != null)) {
                break label378;
              }
            }
            for (;;)
            {
              j++;
              break label331;
              break;
              label378:
              localTL_stickerPack.emoticon = localTL_stickerPack.emoticon.replace("️", "");
              Object localObject3 = (ArrayList)localHashMap2.get(localTL_stickerPack.emoticon);
              localObject2 = localObject3;
              if (localObject3 == null)
              {
                localObject2 = new java/util/ArrayList;
                ((ArrayList)localObject2).<init>();
                localHashMap2.put(localTL_stickerPack.emoticon, localObject2);
              }
              for (int k = 0; k < localTL_stickerPack.documents.size(); k++)
              {
                localObject3 = (Long)localTL_stickerPack.documents.get(k);
                if (localLongSparseArray2.indexOfKey(((Long)localObject3).longValue()) < 0) {
                  localLongSparseArray2.put(((Long)localObject3).longValue(), localTL_stickerPack.emoticon);
                }
                localObject3 = (TLRPC.Document)localLongSparseArray3.get(((Long)localObject3).longValue());
                if (localObject3 != null) {
                  ((ArrayList)localObject2).add(localObject3);
                }
              }
            }
            label530:
            if (!paramBoolean) {
              DataQuery.this.putStickersToCache(paramInt1, localArrayList, paramInt2, paramInt3);
            }
            Object localObject2 = new org/telegram/messenger/DataQuery$28$2;
            ((2)localObject2).<init>(this, localLongSparseArray1, localHashMap1, localArrayList, localHashMap2, localLongSparseArray2);
            AndroidUtilities.runOnUIThread((Runnable)localObject2);
          }
          else if (!paramBoolean)
          {
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                DataQuery.this.loadDate[DataQuery.28.this.val$type] = DataQuery.28.this.val$date;
              }
            });
            DataQuery.this.putStickersToCache(paramInt1, null, paramInt2, 0);
          }
        }
      }
    });
  }
  
  private void putFeaturedStickersToCache(final ArrayList<TLRPC.StickerSetCovered> paramArrayList, final ArrayList<Long> paramArrayList1, final int paramInt1, final int paramInt2)
  {
    if (paramArrayList != null) {}
    for (paramArrayList = new ArrayList(paramArrayList);; paramArrayList = null)
    {
      MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable()
      {
        public void run()
        {
          for (;;)
          {
            try
            {
              if (paramArrayList != null)
              {
                localSQLitePreparedStatement = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().executeFast("REPLACE INTO stickers_featured VALUES(?, ?, ?, ?, ?)");
                localSQLitePreparedStatement.requery();
                int i = 4;
                int j = 0;
                if (j < paramArrayList.size())
                {
                  i += ((TLRPC.StickerSetCovered)paramArrayList.get(j)).getObjectSize();
                  j++;
                  continue;
                }
                NativeByteBuffer localNativeByteBuffer1 = new org/telegram/tgnet/NativeByteBuffer;
                localNativeByteBuffer1.<init>(i);
                NativeByteBuffer localNativeByteBuffer2 = new org/telegram/tgnet/NativeByteBuffer;
                localNativeByteBuffer2.<init>(paramArrayList1.size() * 8 + 4);
                localNativeByteBuffer1.writeInt32(paramArrayList.size());
                i = 0;
                if (i < paramArrayList.size())
                {
                  ((TLRPC.StickerSetCovered)paramArrayList.get(i)).serializeToStream(localNativeByteBuffer1);
                  i++;
                  continue;
                }
                localNativeByteBuffer2.writeInt32(paramArrayList1.size());
                i = 0;
                if (i < paramArrayList1.size())
                {
                  localNativeByteBuffer2.writeInt64(((Long)paramArrayList1.get(i)).longValue());
                  i++;
                  continue;
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
            }
            catch (Exception localException)
            {
              SQLitePreparedStatement localSQLitePreparedStatement;
              FileLog.e(localException);
              continue;
            }
            localSQLitePreparedStatement = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().executeFast("UPDATE stickers_featured SET date = ?");
            localSQLitePreparedStatement.requery();
            localSQLitePreparedStatement.bindInteger(1, paramInt1);
            localSQLitePreparedStatement.step();
            localSQLitePreparedStatement.dispose();
          }
        }
      });
      return;
    }
  }
  
  private void putMediaCountDatabase(final long paramLong, int paramInt1, final int paramInt2)
  {
    MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable()
    {
      public void run()
      {
        try
        {
          SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().executeFast("REPLACE INTO media_counts_v2 VALUES(?, ?, ?)");
          localSQLitePreparedStatement.requery();
          localSQLitePreparedStatement.bindLong(1, paramLong);
          localSQLitePreparedStatement.bindInteger(2, paramInt2);
          localSQLitePreparedStatement.bindInteger(3, this.val$count);
          localSQLitePreparedStatement.step();
          localSQLitePreparedStatement.dispose();
          return;
        }
        catch (Exception localException)
        {
          for (;;)
          {
            FileLog.e(localException);
          }
        }
      }
    });
  }
  
  private void putMediaDatabase(final long paramLong, final int paramInt1, final ArrayList<TLRPC.Message> paramArrayList, int paramInt2, final boolean paramBoolean)
  {
    MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable()
    {
      public void run()
      {
        int i = 1;
        try
        {
          if ((paramArrayList.isEmpty()) || (paramBoolean))
          {
            MessagesStorage.getInstance(DataQuery.this.currentAccount).doneHolesInMedia(paramLong, paramInt1, this.val$type);
            if (paramArrayList.isEmpty()) {
              return;
            }
          }
        }
        catch (Exception localException)
        {
          SQLitePreparedStatement localSQLitePreparedStatement;
          for (;;)
          {
            Iterator localIterator;
            FileLog.e(localException);
          }
          localSQLitePreparedStatement.dispose();
          if (!paramBoolean) {
            break label275;
          }
        }
        MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().beginTransaction();
        localSQLitePreparedStatement = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().executeFast("REPLACE INTO media_v2 VALUES(?, ?, ?, ?, ?)");
        localIterator = paramArrayList.iterator();
        while (localIterator.hasNext())
        {
          TLRPC.Message localMessage = (TLRPC.Message)localIterator.next();
          if (DataQuery.canAddMessageToMedia(localMessage))
          {
            long l1 = localMessage.id;
            long l2 = l1;
            if (localMessage.to_id.channel_id != 0) {
              l2 = l1 | localMessage.to_id.channel_id << 32;
            }
            localSQLitePreparedStatement.requery();
            NativeByteBuffer localNativeByteBuffer = new org/telegram/tgnet/NativeByteBuffer;
            localNativeByteBuffer.<init>(localMessage.getObjectSize());
            localMessage.serializeToStream(localNativeByteBuffer);
            localSQLitePreparedStatement.bindLong(1, l2);
            localSQLitePreparedStatement.bindLong(2, paramLong);
            localSQLitePreparedStatement.bindInteger(3, localMessage.date);
            localSQLitePreparedStatement.bindInteger(4, this.val$type);
            localSQLitePreparedStatement.bindByteBuffer(5, localNativeByteBuffer);
            localSQLitePreparedStatement.step();
            localNativeByteBuffer.reuse();
          }
        }
        if (paramInt1 != 0)
        {
          label275:
          if (!paramBoolean) {
            break label334;
          }
          label282:
          if (paramInt1 == 0) {
            break label360;
          }
          MessagesStorage.getInstance(DataQuery.this.currentAccount).closeHolesInMedia(paramLong, i, paramInt1, this.val$type);
        }
        for (;;)
        {
          MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().commitTransaction();
          break;
          label334:
          i = ((TLRPC.Message)paramArrayList.get(paramArrayList.size() - 1)).id;
          break label282;
          label360:
          MessagesStorage.getInstance(DataQuery.this.currentAccount).closeHolesInMedia(paramLong, i, Integer.MAX_VALUE, this.val$type);
        }
      }
    });
  }
  
  private void putStickersToCache(final int paramInt1, final ArrayList<TLRPC.TL_messages_stickerSet> paramArrayList, final int paramInt2, final int paramInt3)
  {
    if (paramArrayList != null) {}
    for (paramArrayList = new ArrayList(paramArrayList);; paramArrayList = null)
    {
      MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable()
      {
        public void run()
        {
          for (;;)
          {
            try
            {
              if (paramArrayList != null)
              {
                localSQLitePreparedStatement = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().executeFast("REPLACE INTO stickers_v2 VALUES(?, ?, ?, ?)");
                localSQLitePreparedStatement.requery();
                int i = 4;
                int j = 0;
                if (j < paramArrayList.size())
                {
                  i += ((TLRPC.TL_messages_stickerSet)paramArrayList.get(j)).getObjectSize();
                  j++;
                  continue;
                }
                NativeByteBuffer localNativeByteBuffer = new org/telegram/tgnet/NativeByteBuffer;
                localNativeByteBuffer.<init>(i);
                localNativeByteBuffer.writeInt32(paramArrayList.size());
                i = 0;
                if (i < paramArrayList.size())
                {
                  ((TLRPC.TL_messages_stickerSet)paramArrayList.get(i)).serializeToStream(localNativeByteBuffer);
                  i++;
                  continue;
                }
                localSQLitePreparedStatement.bindInteger(1, paramInt1 + 1);
                localSQLitePreparedStatement.bindByteBuffer(2, localNativeByteBuffer);
                localSQLitePreparedStatement.bindInteger(3, paramInt2);
                localSQLitePreparedStatement.bindInteger(4, paramInt3);
                localSQLitePreparedStatement.step();
                localNativeByteBuffer.reuse();
                localSQLitePreparedStatement.dispose();
                return;
              }
            }
            catch (Exception localException)
            {
              SQLitePreparedStatement localSQLitePreparedStatement;
              FileLog.e(localException);
              continue;
            }
            localSQLitePreparedStatement = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().executeFast("UPDATE stickers_v2 SET date = ?");
            localSQLitePreparedStatement.requery();
            localSQLitePreparedStatement.bindInteger(1, paramInt2);
            localSQLitePreparedStatement.step();
            localSQLitePreparedStatement.dispose();
          }
        }
      });
      return;
    }
  }
  
  private static void removeEmptyMessages(ArrayList<TLRPC.Message> paramArrayList)
  {
    int j;
    for (int i = 0; i < paramArrayList.size(); i = j + 1)
    {
      TLRPC.Message localMessage = (TLRPC.Message)paramArrayList.get(i);
      if ((localMessage != null) && (!(localMessage instanceof TLRPC.TL_messageEmpty)))
      {
        j = i;
        if (!(localMessage.action instanceof TLRPC.TL_messageActionHistoryClear)) {}
      }
      else
      {
        paramArrayList.remove(i);
        j = i - 1;
      }
    }
  }
  
  private static void removeOffsetAfter(int paramInt1, int paramInt2, ArrayList<TLRPC.MessageEntity> paramArrayList)
  {
    int i = paramArrayList.size();
    for (int j = 0; j < i; j++)
    {
      TLRPC.MessageEntity localMessageEntity = (TLRPC.MessageEntity)paramArrayList.get(j);
      if (localMessageEntity.offset > paramInt1) {
        localMessageEntity.offset -= paramInt2;
      }
    }
  }
  
  private void saveDraftReplyMessage(final long paramLong, TLRPC.Message paramMessage)
  {
    if (paramMessage == null) {}
    for (;;)
    {
      return;
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          Object localObject = (TLRPC.DraftMessage)DataQuery.this.drafts.get(paramLong);
          if ((localObject != null) && (((TLRPC.DraftMessage)localObject).reply_to_msg_id == this.val$message.id))
          {
            DataQuery.this.draftMessages.put(paramLong, this.val$message);
            localObject = new SerializedData(this.val$message.getObjectSize());
            this.val$message.serializeToStream((AbstractSerializedData)localObject);
            DataQuery.this.preferences.edit().putString("r_" + paramLong, Utilities.bytesToHex(((SerializedData)localObject).toByteArray())).commit();
            NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.newDraftReceived, new Object[] { Long.valueOf(paramLong) });
          }
        }
      });
    }
  }
  
  private void savePeer(final int paramInt1, final int paramInt2, final double paramDouble)
  {
    MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable()
    {
      public void run()
      {
        try
        {
          SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().executeFast("REPLACE INTO chat_hints VALUES(?, ?, ?, ?)");
          localSQLitePreparedStatement.requery();
          localSQLitePreparedStatement.bindInteger(1, paramInt1);
          localSQLitePreparedStatement.bindInteger(2, paramInt2);
          localSQLitePreparedStatement.bindDouble(3, paramDouble);
          localSQLitePreparedStatement.bindInteger(4, (int)System.currentTimeMillis() / 1000);
          localSQLitePreparedStatement.step();
          localSQLitePreparedStatement.dispose();
          return;
        }
        catch (Exception localException)
        {
          for (;;)
          {
            FileLog.e(localException);
          }
        }
      }
    });
  }
  
  private void savePinnedMessage(final TLRPC.Message paramMessage)
  {
    MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable()
    {
      public void run()
      {
        try
        {
          MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().beginTransaction();
          SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().executeFast("REPLACE INTO chat_pinned VALUES(?, ?, ?)");
          NativeByteBuffer localNativeByteBuffer = new org/telegram/tgnet/NativeByteBuffer;
          localNativeByteBuffer.<init>(paramMessage.getObjectSize());
          paramMessage.serializeToStream(localNativeByteBuffer);
          localSQLitePreparedStatement.requery();
          localSQLitePreparedStatement.bindInteger(1, paramMessage.to_id.channel_id);
          localSQLitePreparedStatement.bindInteger(2, paramMessage.id);
          localSQLitePreparedStatement.bindByteBuffer(3, localNativeByteBuffer);
          localSQLitePreparedStatement.step();
          localNativeByteBuffer.reuse();
          localSQLitePreparedStatement.dispose();
          MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().commitTransaction();
          return;
        }
        catch (Exception localException)
        {
          for (;;)
          {
            FileLog.e(localException);
          }
        }
      }
    });
  }
  
  private void saveReplyMessages(final SparseArray<ArrayList<MessageObject>> paramSparseArray, final ArrayList<TLRPC.Message> paramArrayList)
  {
    MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable()
    {
      public void run()
      {
        try
        {
          MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().beginTransaction();
          SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().executeFast("UPDATE messages SET replydata = ? WHERE mid = ?");
          for (int i = 0; i < paramArrayList.size(); i++)
          {
            Object localObject = (TLRPC.Message)paramArrayList.get(i);
            ArrayList localArrayList = (ArrayList)paramSparseArray.get(((TLRPC.Message)localObject).id);
            if (localArrayList != null)
            {
              NativeByteBuffer localNativeByteBuffer = new org/telegram/tgnet/NativeByteBuffer;
              localNativeByteBuffer.<init>(((TLRPC.Message)localObject).getObjectSize());
              ((TLRPC.Message)localObject).serializeToStream(localNativeByteBuffer);
              for (int j = 0; j < localArrayList.size(); j++)
              {
                localObject = (MessageObject)localArrayList.get(j);
                localSQLitePreparedStatement.requery();
                long l1 = ((MessageObject)localObject).getId();
                long l2 = l1;
                if (((MessageObject)localObject).messageOwner.to_id.channel_id != 0) {
                  l2 = l1 | ((MessageObject)localObject).messageOwner.to_id.channel_id << 32;
                }
                localSQLitePreparedStatement.bindByteBuffer(1, localNativeByteBuffer);
                localSQLitePreparedStatement.bindLong(2, l2);
                localSQLitePreparedStatement.step();
              }
              localNativeByteBuffer.reuse();
            }
          }
          localSQLitePreparedStatement.dispose();
          MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().commitTransaction();
          return;
        }
        catch (Exception localException)
        {
          for (;;)
          {
            FileLog.e(localException);
          }
        }
      }
    });
  }
  
  private void searchMessagesInChat(String paramString, final long paramLong1, final long paramLong2, final int paramInt1, final int paramInt2, boolean paramBoolean, final TLRPC.User paramUser)
  {
    int i = 0;
    int j = 0;
    final long l1 = paramLong1;
    int k;
    if (!paramBoolean)
    {
      k = 1;
      if (this.reqId != 0)
      {
        ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.reqId, true);
        this.reqId = 0;
      }
      if (this.mergeReqId != 0)
      {
        ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.mergeReqId, true);
        this.mergeReqId = 0;
      }
      if (paramString != null) {
        break label714;
      }
      if (!this.searchResultMessages.isEmpty()) {
        break label92;
      }
    }
    label92:
    label263:
    Object localObject;
    label319:
    final TLRPC.TL_messages_search localTL_messages_search;
    label523:
    label553:
    label714:
    label845:
    label864:
    do
    {
      int m;
      long l2;
      do
      {
        for (;;)
        {
          return;
          k = 0;
          break;
          if (paramInt2 != 1) {
            break label553;
          }
          this.lastReturnedNum += 1;
          if (this.lastReturnedNum < this.searchResultMessages.size())
          {
            paramString = (MessageObject)this.searchResultMessages.get(this.lastReturnedNum);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatSearchResultsAvailable, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramString.getId()), Integer.valueOf(getMask()), Long.valueOf(paramString.getDialogId()), Integer.valueOf(this.lastReturnedNum), Integer.valueOf(this.messagesSearchCount[0] + this.messagesSearchCount[1]) });
          }
          else
          {
            if ((this.messagesSearchEndReached[0] == 0) || (paramLong2 != 0L) || (this.messagesSearchEndReached[1] == 0)) {
              break label263;
            }
            this.lastReturnedNum -= 1;
          }
        }
        m = 0;
        localObject = this.lastSearchQuery;
        paramString = (MessageObject)this.searchResultMessages.get(this.searchResultMessages.size() - 1);
        if ((paramString.getDialogId() != paramLong1) || (this.messagesSearchEndReached[0] != 0)) {
          break label523;
        }
        i = paramString.getId();
        l2 = paramLong1;
        l1 = l2;
        if (this.messagesSearchEndReached[0] != 0)
        {
          l1 = l2;
          if (this.messagesSearchEndReached[1] == 0)
          {
            l1 = l2;
            if (paramLong2 != 0L) {
              l1 = paramLong2;
            }
          }
        }
        if ((l1 != paramLong1) || (m == 0)) {
          break label864;
        }
        if (paramLong2 == 0L) {
          break label845;
        }
        paramString = MessagesController.getInstance(this.currentAccount).getInputPeer((int)paramLong2);
      } while (paramString == null);
      localTL_messages_search = new TLRPC.TL_messages_search();
      localTL_messages_search.peer = paramString;
      this.lastMergeDialogId = paramLong2;
      localTL_messages_search.limit = 1;
      if (localObject != null) {}
      for (paramString = (String)localObject;; paramString = "")
      {
        localTL_messages_search.q = paramString;
        if (paramUser != null)
        {
          localTL_messages_search.from_id = MessagesController.getInstance(this.currentAccount).getInputUser(paramUser);
          localTL_messages_search.flags |= 0x1;
        }
        localTL_messages_search.filter = new TLRPC.TL_inputMessagesFilterEmpty();
        this.mergeReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_messages_search, new RequestDelegate()
        {
          public void run(final TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
          {
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                TLRPC.messages_Messages localmessages_Messages;
                int[] arrayOfInt;
                if (DataQuery.this.lastMergeDialogId == DataQuery.31.this.val$mergeDialogId)
                {
                  DataQuery.access$3802(DataQuery.this, 0);
                  if (paramAnonymousTLObject != null)
                  {
                    localmessages_Messages = (TLRPC.messages_Messages)paramAnonymousTLObject;
                    DataQuery.this.messagesSearchEndReached[1] = localmessages_Messages.messages.isEmpty();
                    arrayOfInt = DataQuery.this.messagesSearchCount;
                    if (!(localmessages_Messages instanceof TLRPC.TL_messages_messagesSlice)) {
                      break label151;
                    }
                  }
                }
                label151:
                for (int i = localmessages_Messages.count;; i = localmessages_Messages.messages.size())
                {
                  arrayOfInt[1] = i;
                  DataQuery.this.searchMessagesInChat(DataQuery.31.this.val$req.q, DataQuery.31.this.val$dialog_id, DataQuery.31.this.val$mergeDialogId, DataQuery.31.this.val$guid, DataQuery.31.this.val$direction, true, DataQuery.31.this.val$user);
                  return;
                }
              }
            });
          }
        }, 2);
        break;
        if (paramString.getDialogId() == paramLong2) {
          i = paramString.getId();
        }
        l2 = paramLong2;
        this.messagesSearchEndReached[1] = false;
        break label319;
        if (paramInt2 != 2) {
          break;
        }
        this.lastReturnedNum -= 1;
        if (this.lastReturnedNum < 0)
        {
          this.lastReturnedNum = 0;
          break;
        }
        if (this.lastReturnedNum >= this.searchResultMessages.size()) {
          this.lastReturnedNum = (this.searchResultMessages.size() - 1);
        }
        paramString = (MessageObject)this.searchResultMessages.get(this.lastReturnedNum);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatSearchResultsAvailable, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramString.getId()), Integer.valueOf(getMask()), Long.valueOf(paramString.getDialogId()), Integer.valueOf(this.lastReturnedNum), Integer.valueOf(this.messagesSearchCount[0] + this.messagesSearchCount[1]) });
        break;
        m = k;
        i = j;
        l2 = l1;
        localObject = paramString;
        if (k == 0) {
          break label319;
        }
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatSearchResultsLoading, new Object[] { Integer.valueOf(paramInt1) });
        localObject = this.messagesSearchEndReached;
        this.messagesSearchEndReached[1] = false;
        localObject[0] = 0;
        localObject = this.messagesSearchCount;
        this.messagesSearchCount[1] = 0;
        localObject[0] = 0;
        this.searchResultMessages.clear();
        this.searchResultMessagesMap[0].clear();
        this.searchResultMessagesMap[1].clear();
        m = k;
        i = j;
        l2 = l1;
        localObject = paramString;
        break label319;
      }
      this.lastMergeDialogId = 0L;
      this.messagesSearchEndReached[1] = true;
      this.messagesSearchCount[1] = 0;
      localTL_messages_search = new TLRPC.TL_messages_search();
      localTL_messages_search.peer = MessagesController.getInstance(this.currentAccount).getInputPeer((int)l1);
    } while (localTL_messages_search.peer == null);
    localTL_messages_search.limit = 21;
    if (localObject != null) {}
    for (paramString = (String)localObject;; paramString = "")
    {
      localTL_messages_search.q = paramString;
      localTL_messages_search.offset_id = i;
      if (paramUser != null)
      {
        localTL_messages_search.from_id = MessagesController.getInstance(this.currentAccount).getInputUser(paramUser);
        localTL_messages_search.flags |= 0x1;
      }
      localTL_messages_search.filter = new TLRPC.TL_inputMessagesFilterEmpty();
      paramInt2 = this.lastReqId + 1;
      this.lastReqId = paramInt2;
      this.lastSearchQuery = ((String)localObject);
      this.reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_messages_search, new RequestDelegate()
      {
        public void run(final TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
        {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              Object localObject1;
              int i;
              int j;
              int k;
              int m;
              if (DataQuery.32.this.val$currentReqId == DataQuery.this.lastReqId)
              {
                DataQuery.access$4302(DataQuery.this, 0);
                if (paramAnonymousTLObject != null)
                {
                  localObject1 = (TLRPC.messages_Messages)paramAnonymousTLObject;
                  for (i = 0; i < ((TLRPC.messages_Messages)localObject1).messages.size(); i = j + 1)
                  {
                    localObject2 = (TLRPC.Message)((TLRPC.messages_Messages)localObject1).messages.get(i);
                    if (!(localObject2 instanceof TLRPC.TL_messageEmpty))
                    {
                      j = i;
                      if (!(((TLRPC.Message)localObject2).action instanceof TLRPC.TL_messageActionHistoryClear)) {}
                    }
                    else
                    {
                      ((TLRPC.messages_Messages)localObject1).messages.remove(i);
                      j = i - 1;
                    }
                  }
                  MessagesStorage.getInstance(DataQuery.this.currentAccount).putUsersAndChats(((TLRPC.messages_Messages)localObject1).users, ((TLRPC.messages_Messages)localObject1).chats, true, true);
                  MessagesController.getInstance(DataQuery.this.currentAccount).putUsers(((TLRPC.messages_Messages)localObject1).users, false);
                  MessagesController.getInstance(DataQuery.this.currentAccount).putChats(((TLRPC.messages_Messages)localObject1).chats, false);
                  if ((DataQuery.32.this.val$req.offset_id == 0) && (DataQuery.32.this.val$queryWithDialogFinal == DataQuery.32.this.val$dialog_id))
                  {
                    DataQuery.access$4402(DataQuery.this, 0);
                    DataQuery.this.searchResultMessages.clear();
                    DataQuery.this.searchResultMessagesMap[0].clear();
                    DataQuery.this.searchResultMessagesMap[1].clear();
                    DataQuery.this.messagesSearchCount[0] = 0;
                  }
                  i = 0;
                  j = 0;
                  if (j < Math.min(((TLRPC.messages_Messages)localObject1).messages.size(), 20))
                  {
                    localObject2 = (TLRPC.Message)((TLRPC.messages_Messages)localObject1).messages.get(j);
                    k = 1;
                    MessageObject localMessageObject = new MessageObject(DataQuery.this.currentAccount, (TLRPC.Message)localObject2, false);
                    DataQuery.this.searchResultMessages.add(localMessageObject);
                    localObject2 = DataQuery.this.searchResultMessagesMap;
                    if (DataQuery.32.this.val$queryWithDialogFinal == DataQuery.32.this.val$dialog_id) {}
                    for (i = 0;; i = 1)
                    {
                      localObject2[i].put(localMessageObject.getId(), localMessageObject);
                      j++;
                      i = k;
                      break;
                    }
                  }
                  Object localObject2 = DataQuery.this.messagesSearchEndReached;
                  if (DataQuery.32.this.val$queryWithDialogFinal != DataQuery.32.this.val$dialog_id) {
                    break label733;
                  }
                  j = 0;
                  if (((TLRPC.messages_Messages)localObject1).messages.size() == 21) {
                    break label739;
                  }
                  m = 1;
                  label461:
                  localObject2[j] = m;
                  localObject2 = DataQuery.this.messagesSearchCount;
                  if (DataQuery.32.this.val$queryWithDialogFinal != DataQuery.32.this.val$dialog_id) {
                    break label745;
                  }
                  j = 0;
                  label499:
                  if ((!(localObject1 instanceof TLRPC.TL_messages_messagesSlice)) && (!(localObject1 instanceof TLRPC.TL_messages_channelMessages))) {
                    break label751;
                  }
                  k = ((TLRPC.messages_Messages)localObject1).count;
                  label519:
                  localObject2[j] = k;
                  if (!DataQuery.this.searchResultMessages.isEmpty()) {
                    break label763;
                  }
                  NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.chatSearchResultsAvailable, new Object[] { Integer.valueOf(DataQuery.32.this.val$guid), Integer.valueOf(0), Integer.valueOf(DataQuery.this.getMask()), Long.valueOf(0L), Integer.valueOf(0), Integer.valueOf(0) });
                }
              }
              for (;;)
              {
                if ((DataQuery.32.this.val$queryWithDialogFinal == DataQuery.32.this.val$dialog_id) && (DataQuery.this.messagesSearchEndReached[0] != 0) && (DataQuery.32.this.val$mergeDialogId != 0L) && (DataQuery.this.messagesSearchEndReached[1] == 0)) {
                  DataQuery.this.searchMessagesInChat(DataQuery.this.lastSearchQuery, DataQuery.32.this.val$dialog_id, DataQuery.32.this.val$mergeDialogId, DataQuery.32.this.val$guid, 0, true, DataQuery.32.this.val$user);
                }
                return;
                label733:
                j = 1;
                break;
                label739:
                m = 0;
                break label461;
                label745:
                j = 1;
                break label499;
                label751:
                k = ((TLRPC.messages_Messages)localObject1).messages.size();
                break label519;
                label763:
                if (i != 0)
                {
                  if (DataQuery.this.lastReturnedNum >= DataQuery.this.searchResultMessages.size()) {
                    DataQuery.access$4402(DataQuery.this, DataQuery.this.searchResultMessages.size() - 1);
                  }
                  localObject1 = (MessageObject)DataQuery.this.searchResultMessages.get(DataQuery.this.lastReturnedNum);
                  NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.chatSearchResultsAvailable, new Object[] { Integer.valueOf(DataQuery.32.this.val$guid), Integer.valueOf(((MessageObject)localObject1).getId()), Integer.valueOf(DataQuery.this.getMask()), Long.valueOf(((MessageObject)localObject1).getDialogId()), Integer.valueOf(DataQuery.this.lastReturnedNum), Integer.valueOf(DataQuery.this.messagesSearchCount[0] + DataQuery.this.messagesSearchCount[1]) });
                }
              }
            }
          });
        }
      }, 2);
      break;
    }
  }
  
  public static void sortEntities(ArrayList<TLRPC.MessageEntity> paramArrayList)
  {
    Collections.sort(paramArrayList, entityComparator);
  }
  
  public void addNewStickerSet(TLRPC.TL_messages_stickerSet paramTL_messages_stickerSet)
  {
    if ((this.stickerSetsById.indexOfKey(paramTL_messages_stickerSet.set.id) >= 0) || (this.stickerSetsByName.containsKey(paramTL_messages_stickerSet.set.short_name))) {}
    for (;;)
    {
      return;
      if (paramTL_messages_stickerSet.set.masks) {}
      LongSparseArray localLongSparseArray;
      Object localObject1;
      for (int i = 1;; i = 0)
      {
        this.stickerSets[i].add(0, paramTL_messages_stickerSet);
        this.stickerSetsById.put(paramTL_messages_stickerSet.set.id, paramTL_messages_stickerSet);
        this.installedStickerSetsById.put(paramTL_messages_stickerSet.set.id, paramTL_messages_stickerSet);
        this.stickerSetsByName.put(paramTL_messages_stickerSet.set.short_name, paramTL_messages_stickerSet);
        localLongSparseArray = new LongSparseArray();
        for (j = 0; j < paramTL_messages_stickerSet.documents.size(); j++)
        {
          localObject1 = (TLRPC.Document)paramTL_messages_stickerSet.documents.get(j);
          localLongSparseArray.put(((TLRPC.Document)localObject1).id, localObject1);
        }
      }
      for (int j = 0; j < paramTL_messages_stickerSet.packs.size(); j++)
      {
        TLRPC.TL_stickerPack localTL_stickerPack = (TLRPC.TL_stickerPack)paramTL_messages_stickerSet.packs.get(j);
        localTL_stickerPack.emoticon = localTL_stickerPack.emoticon.replace("️", "");
        Object localObject2 = (ArrayList)this.allStickers.get(localTL_stickerPack.emoticon);
        localObject1 = localObject2;
        if (localObject2 == null)
        {
          localObject1 = new ArrayList();
          this.allStickers.put(localTL_stickerPack.emoticon, localObject1);
        }
        for (int k = 0; k < localTL_stickerPack.documents.size(); k++)
        {
          localObject2 = (Long)localTL_stickerPack.documents.get(k);
          if (this.stickersByEmoji.indexOfKey(((Long)localObject2).longValue()) < 0) {
            this.stickersByEmoji.put(((Long)localObject2).longValue(), localTL_stickerPack.emoticon);
          }
          localObject2 = (TLRPC.Document)localLongSparseArray.get(((Long)localObject2).longValue());
          if (localObject2 != null) {
            ((ArrayList)localObject1).add(localObject2);
          }
        }
      }
      this.loadHash[i] = calcStickersHash(this.stickerSets[i]);
      NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.stickersDidLoaded, new Object[] { Integer.valueOf(i) });
      loadStickers(i, false, true);
    }
  }
  
  public void addRecentGif(TLRPC.Document paramDocument, int paramInt)
  {
    int i = 0;
    for (int j = 0; j < this.recentGifs.size(); j++)
    {
      localObject = (TLRPC.Document)this.recentGifs.get(j);
      if (((TLRPC.Document)localObject).id == paramDocument.id)
      {
        this.recentGifs.remove(j);
        this.recentGifs.add(0, localObject);
        i = 1;
      }
    }
    if (i == 0) {
      this.recentGifs.add(0, paramDocument);
    }
    if (this.recentGifs.size() > MessagesController.getInstance(this.currentAccount).maxRecentGifsCount)
    {
      localObject = (TLRPC.Document)this.recentGifs.remove(this.recentGifs.size() - 1);
      MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable()
      {
        public void run()
        {
          try
          {
            SQLiteDatabase localSQLiteDatabase = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase();
            StringBuilder localStringBuilder = new java/lang/StringBuilder;
            localStringBuilder.<init>();
            localSQLiteDatabase.executeFast("DELETE FROM web_recent_v3 WHERE id = '" + localObject.id + "' AND type = 2").stepThis().dispose();
            return;
          }
          catch (Exception localException)
          {
            for (;;)
            {
              FileLog.e(localException);
            }
          }
        }
      });
    }
    final Object localObject = new ArrayList();
    ((ArrayList)localObject).add(paramDocument);
    processLoadedRecentDocuments(0, (ArrayList)localObject, true, paramInt);
  }
  
  public void addRecentSticker(final int paramInt1, TLRPC.Document paramDocument, int paramInt2, boolean paramBoolean)
  {
    int i = 0;
    for (int j = 0; j < this.recentStickers[paramInt1].size(); j++)
    {
      localObject = (TLRPC.Document)this.recentStickers[paramInt1].get(j);
      if (((TLRPC.Document)localObject).id == paramDocument.id)
      {
        this.recentStickers[paramInt1].remove(j);
        if (!paramBoolean) {
          this.recentStickers[paramInt1].add(0, localObject);
        }
        i = 1;
      }
    }
    if ((i == 0) && (!paramBoolean)) {
      this.recentStickers[paramInt1].add(0, paramDocument);
    }
    if (paramInt1 == 2) {
      if (paramBoolean)
      {
        Toast.makeText(ApplicationLoader.applicationContext, LocaleController.getString("RemovedFromFavorites", NUM), 0).show();
        localObject = new TLRPC.TL_messages_faveSticker();
        ((TLRPC.TL_messages_faveSticker)localObject).id = new TLRPC.TL_inputDocument();
        ((TLRPC.TL_messages_faveSticker)localObject).id.id = paramDocument.id;
        ((TLRPC.TL_messages_faveSticker)localObject).id.access_hash = paramDocument.access_hash;
        ((TLRPC.TL_messages_faveSticker)localObject).unfave = paramBoolean;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest((TLObject)localObject, new RequestDelegate()
        {
          public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error) {}
        });
        j = MessagesController.getInstance(this.currentAccount).maxFaveStickersCount;
        label222:
        if ((this.recentStickers[paramInt1].size() > j) || (paramBoolean)) {
          if (!paramBoolean) {
            break label377;
          }
        }
      }
    }
    label377:
    for (final Object localObject = paramDocument;; localObject = (TLRPC.Document)this.recentStickers[paramInt1].remove(this.recentStickers[paramInt1].size() - 1))
    {
      MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable()
      {
        public void run()
        {
          int i;
          if (paramInt1 == 0) {
            i = 3;
          }
          try
          {
            for (;;)
            {
              SQLiteDatabase localSQLiteDatabase = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase();
              StringBuilder localStringBuilder = new java/lang/StringBuilder;
              localStringBuilder.<init>();
              localSQLiteDatabase.executeFast("DELETE FROM web_recent_v3 WHERE id = '" + localObject.id + "' AND type = " + i).stepThis().dispose();
              return;
              if (paramInt1 == 1) {
                i = 4;
              } else {
                i = 5;
              }
            }
          }
          catch (Exception localException)
          {
            for (;;)
            {
              FileLog.e(localException);
            }
          }
        }
      });
      if (!paramBoolean)
      {
        localObject = new ArrayList();
        ((ArrayList)localObject).add(paramDocument);
        processLoadedRecentDocuments(paramInt1, (ArrayList)localObject, false, paramInt2);
      }
      if (paramInt1 == 2) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.recentDocumentsDidLoaded, new Object[] { Boolean.valueOf(false), Integer.valueOf(paramInt1) });
      }
      return;
      Toast.makeText(ApplicationLoader.applicationContext, LocaleController.getString("AddedToFavorites", NUM), 0).show();
      break;
      j = MessagesController.getInstance(this.currentAccount).maxRecentStickersCount;
      break label222;
    }
  }
  
  public void beginTransaction()
  {
    this.inTransaction = true;
  }
  
  public void buildShortcuts()
  {
    if (Build.VERSION.SDK_INT < 25) {
      return;
    }
    final ArrayList localArrayList = new ArrayList();
    for (int i = 0;; i++) {
      if (i < this.hints.size())
      {
        localArrayList.add(this.hints.get(i));
        if (localArrayList.size() != 3) {}
      }
      else
      {
        Utilities.globalQueue.postRunnable(new Runnable()
        {
          @SuppressLint({"NewApi"})
          public void run()
          {
            ShortcutManager localShortcutManager;
            Object localObject1;
            ArrayList localArrayList1;
            long l1;
            ArrayList localArrayList2;
            label400:
            do
            {
              try
              {
                localShortcutManager = (ShortcutManager)ApplicationLoader.applicationContext.getSystemService(ShortcutManager.class);
                localObject1 = localShortcutManager.getDynamicShortcuts();
                localArrayList1 = new java/util/ArrayList;
                localArrayList1.<init>();
                localObject2 = new java/util/ArrayList;
                ((ArrayList)localObject2).<init>();
                localObject3 = new java/util/ArrayList;
                ((ArrayList)localObject3).<init>();
                if ((localObject1 != null) && (!((List)localObject1).isEmpty()))
                {
                  ((ArrayList)localObject2).add("compose");
                  i = 0;
                  if (i < localArrayList.size())
                  {
                    localObject5 = (TLRPC.TL_topPeer)localArrayList.get(i);
                    if (((TLRPC.TL_topPeer)localObject5).peer.user_id != 0) {
                      l1 = ((TLRPC.TL_topPeer)localObject5).peer.user_id;
                    }
                    for (;;)
                    {
                      localObject5 = new java/lang/StringBuilder;
                      ((StringBuilder)localObject5).<init>();
                      ((ArrayList)localObject2).add("did" + l1);
                      i++;
                      break;
                      long l2 = -((TLRPC.TL_topPeer)localObject5).peer.chat_id;
                      l1 = l2;
                      if (l2 == 0L) {
                        l1 = -((TLRPC.TL_topPeer)localObject5).peer.channel_id;
                      }
                    }
                  }
                  for (i = 0; i < ((List)localObject1).size(); i++)
                  {
                    localObject5 = ((ShortcutInfo)((List)localObject1).get(i)).getId();
                    if (!((ArrayList)localObject2).remove(localObject5)) {
                      ((ArrayList)localObject3).add(localObject5);
                    }
                    localArrayList1.add(localObject5);
                  }
                  if ((((ArrayList)localObject2).isEmpty()) && (((ArrayList)localObject3).isEmpty())) {
                    return;
                  }
                }
              }
              catch (Throwable localThrowable2)
              {
                for (;;)
                {
                  Object localObject3;
                  int i;
                  continue;
                  j = ((TLRPC.TL_topPeer)localObject2).peer.chat_id;
                  k = j;
                  if (j == 0) {
                    k = ((TLRPC.TL_topPeer)localObject2).peer.channel_id;
                  }
                  localObject5 = MessagesController.getInstance(DataQuery.this.currentAccount).getChat(Integer.valueOf(k));
                  localIntent.putExtra("chatId", k);
                  l1 = -k;
                }
                localObject1 = null;
                if (localThrowable2 == null) {
                  break label1176;
                }
              }
              localObject2 = new android/content/Intent;
              ((Intent)localObject2).<init>(ApplicationLoader.applicationContext, LaunchActivity.class);
              ((Intent)localObject2).setAction("new_dialog");
              localArrayList2 = new java/util/ArrayList;
              localArrayList2.<init>();
              localObject1 = new android/content/pm/ShortcutInfo$Builder;
              ((ShortcutInfo.Builder)localObject1).<init>(ApplicationLoader.applicationContext, "compose");
              localArrayList2.add(((ShortcutInfo.Builder)localObject1).setShortLabel(LocaleController.getString("NewConversationShortcut", NUM)).setLongLabel(LocaleController.getString("NewConversationShortcut", NUM)).setIcon(Icon.createWithResource(ApplicationLoader.applicationContext, NUM)).setIntent((Intent)localObject2).build());
              if (!localArrayList1.contains("compose")) {
                break label527;
              }
              localShortcutManager.updateShortcuts(localArrayList2);
              localArrayList2.clear();
              if (!((ArrayList)localObject3).isEmpty()) {
                localShortcutManager.removeDynamicShortcuts((List)localObject3);
              }
              i = 0;
            } while (i >= localArrayList.size());
            Intent localIntent = new android/content/Intent;
            localIntent.<init>(ApplicationLoader.applicationContext, OpenChatReceiver.class);
            Object localObject2 = (TLRPC.TL_topPeer)localArrayList.get(i);
            localObject3 = null;
            Object localObject5 = null;
            if (((TLRPC.TL_topPeer)localObject2).peer.user_id != 0)
            {
              localIntent.putExtra("userId", ((TLRPC.TL_topPeer)localObject2).peer.user_id);
              localObject3 = MessagesController.getInstance(DataQuery.this.currentAccount).getUser(Integer.valueOf(((TLRPC.TL_topPeer)localObject2).peer.user_id));
              l1 = ((TLRPC.TL_topPeer)localObject2).peer.user_id;
              if ((localObject3 != null) || (localObject5 != null)) {
                break label610;
              }
            }
            for (;;)
            {
              i++;
              break label400;
              label527:
              localShortcutManager.addDynamicShortcuts(localArrayList2);
              break;
              int j;
              int k;
              label610:
              localObject5 = ContactsController.formatName(localThrowable2.first_name, localThrowable2.last_name);
              localObject2 = localObject5;
              if (localThrowable2.photo != null)
              {
                localObject1 = localThrowable2.photo.photo_small;
                localObject2 = localObject5;
              }
              label657:
              localIntent.putExtra("currentAccount", DataQuery.this.currentAccount);
              Object localObject4 = new java/lang/StringBuilder;
              ((StringBuilder)localObject4).<init>();
              localIntent.setAction("com.tmessages.openchat" + l1);
              localIntent.addFlags(67108864);
              localObject5 = null;
              localObject4 = null;
              if (localObject1 != null) {
                localObject4 = localObject5;
              }
              try
              {
                localObject1 = BitmapFactory.decodeFile(FileLoader.getPathToAttach((TLObject)localObject1, true).toString());
                localObject4 = localObject1;
                if (localObject1 != null)
                {
                  localObject4 = localObject1;
                  k = AndroidUtilities.dp(48.0F);
                  localObject4 = localObject1;
                  localObject5 = Bitmap.createBitmap(k, k, Bitmap.Config.ARGB_8888);
                  localObject4 = localObject1;
                  localCanvas = new android/graphics/Canvas;
                  localObject4 = localObject1;
                  localCanvas.<init>((Bitmap)localObject5);
                  localObject4 = localObject1;
                  if (DataQuery.roundPaint == null)
                  {
                    localObject4 = localObject1;
                    Object localObject6 = new android/graphics/Paint;
                    localObject4 = localObject1;
                    ((Paint)localObject6).<init>(3);
                    localObject4 = localObject1;
                    DataQuery.access$5202((Paint)localObject6);
                    localObject4 = localObject1;
                    localObject6 = new android/graphics/RectF;
                    localObject4 = localObject1;
                    ((RectF)localObject6).<init>();
                    localObject4 = localObject1;
                    DataQuery.access$5302((RectF)localObject6);
                    localObject4 = localObject1;
                    localObject6 = new android/graphics/Paint;
                    localObject4 = localObject1;
                    ((Paint)localObject6).<init>(1);
                    localObject4 = localObject1;
                    DataQuery.access$5402((Paint)localObject6);
                    localObject4 = localObject1;
                    localObject6 = DataQuery.erasePaint;
                    localObject4 = localObject1;
                    PorterDuffXfermode localPorterDuffXfermode = new android/graphics/PorterDuffXfermode;
                    localObject4 = localObject1;
                    localPorterDuffXfermode.<init>(PorterDuff.Mode.CLEAR);
                    localObject4 = localObject1;
                    ((Paint)localObject6).setXfermode(localPorterDuffXfermode);
                    localObject4 = localObject1;
                    localObject6 = new android/graphics/Path;
                    localObject4 = localObject1;
                    ((Path)localObject6).<init>();
                    localObject4 = localObject1;
                    DataQuery.access$5502((Path)localObject6);
                    localObject4 = localObject1;
                    DataQuery.roundPath.addCircle(k / 2, k / 2, k / 2 - AndroidUtilities.dp(2.0F), Path.Direction.CW);
                    localObject4 = localObject1;
                    DataQuery.roundPath.toggleInverseFillType();
                  }
                  localObject4 = localObject1;
                  DataQuery.bitmapRect.set(AndroidUtilities.dp(2.0F), AndroidUtilities.dp(2.0F), AndroidUtilities.dp(46.0F), AndroidUtilities.dp(46.0F));
                  localObject4 = localObject1;
                  localCanvas.drawBitmap((Bitmap)localObject1, null, DataQuery.bitmapRect, DataQuery.roundPaint);
                  localObject4 = localObject1;
                  localCanvas.drawPath(DataQuery.roundPath, DataQuery.erasePaint);
                  localObject4 = localObject1;
                }
              }
              catch (Throwable localThrowable1)
              {
                try
                {
                  Canvas localCanvas;
                  localCanvas.setBitmap(null);
                  localObject4 = localObject5;
                  label1054:
                  localObject1 = new java/lang/StringBuilder;
                  ((StringBuilder)localObject1).<init>();
                  localObject5 = "did" + l1;
                  localObject1 = localObject2;
                  if (TextUtils.isEmpty((CharSequence)localObject2)) {
                    localObject1 = " ";
                  }
                  localObject2 = new android/content/pm/ShortcutInfo$Builder;
                  ((ShortcutInfo.Builder)localObject2).<init>(ApplicationLoader.applicationContext, (String)localObject5);
                  localObject2 = ((ShortcutInfo.Builder)localObject2).setShortLabel((CharSequence)localObject1).setLongLabel((CharSequence)localObject1).setIntent(localIntent);
                  if (localObject4 != null)
                  {
                    ((ShortcutInfo.Builder)localObject2).setIcon(Icon.createWithBitmap((Bitmap)localObject4));
                    label1141:
                    localArrayList2.add(((ShortcutInfo.Builder)localObject2).build());
                    if (!localArrayList1.contains(localObject5)) {
                      break label1237;
                    }
                    localShortcutManager.updateShortcuts(localArrayList2);
                  }
                  for (;;)
                  {
                    localArrayList2.clear();
                    break;
                    label1176:
                    localObject4 = ((TLRPC.Chat)localObject5).title;
                    localObject2 = localObject4;
                    if (((TLRPC.Chat)localObject5).photo == null) {
                      break label657;
                    }
                    localObject1 = ((TLRPC.Chat)localObject5).photo.photo_small;
                    localObject2 = localObject4;
                    break label657;
                    localThrowable1 = localThrowable1;
                    FileLog.e(localThrowable1);
                    break label1054;
                    ((ShortcutInfo.Builder)localObject2).setIcon(Icon.createWithResource(ApplicationLoader.applicationContext, NUM));
                    break label1141;
                    label1237:
                    localShortcutManager.addDynamicShortcuts(localArrayList2);
                  }
                }
                catch (Exception localException)
                {
                  for (;;) {}
                }
              }
            }
          }
        });
        break;
      }
    }
  }
  
  public void calcNewHash(int paramInt)
  {
    this.loadHash[paramInt] = calcStickersHash(this.stickerSets[paramInt]);
  }
  
  public boolean canAddStickerToFavorites()
  {
    boolean bool = false;
    if ((this.stickersLoaded[0] == 0) || (this.stickerSets[0].size() >= 5) || (!this.recentStickers[2].isEmpty())) {
      bool = true;
    }
    return bool;
  }
  
  public void checkFeaturedStickers()
  {
    if ((!this.loadingFeaturedStickers) && ((!this.featuredStickersLoaded) || (Math.abs(System.currentTimeMillis() / 1000L - this.loadFeaturedDate) >= 3600L))) {
      loadFeaturedStickers(true, false);
    }
  }
  
  public void checkStickers(int paramInt)
  {
    if ((this.loadingStickers[paramInt] == 0) && ((this.stickersLoaded[paramInt] == 0) || (Math.abs(System.currentTimeMillis() / 1000L - this.loadDate[paramInt]) >= 3600L))) {
      loadStickers(paramInt, true, false);
    }
  }
  
  public void cleanDraft(long paramLong, boolean paramBoolean)
  {
    TLRPC.DraftMessage localDraftMessage = (TLRPC.DraftMessage)this.drafts.get(paramLong);
    if (localDraftMessage == null) {}
    for (;;)
    {
      return;
      if (!paramBoolean)
      {
        this.drafts.remove(paramLong);
        this.draftMessages.remove(paramLong);
        this.preferences.edit().remove("" + paramLong).remove("r_" + paramLong).commit();
        MessagesController.getInstance(this.currentAccount).sortDialogs(null);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
      }
      else if (localDraftMessage.reply_to_msg_id != 0)
      {
        localDraftMessage.reply_to_msg_id = 0;
        localDraftMessage.flags &= 0xFFFFFFFE;
        saveDraft(paramLong, localDraftMessage.message, localDraftMessage.entities, null, localDraftMessage.no_webpage, true);
      }
    }
  }
  
  public void cleanup()
  {
    for (int i = 0; i < 3; i++)
    {
      this.recentStickers[i].clear();
      this.loadingRecentStickers[i] = false;
      this.recentStickersLoaded[i] = false;
    }
    for (i = 0; i < 4; i++)
    {
      this.loadHash[i] = 0;
      this.loadDate[i] = 0;
      this.stickerSets[i].clear();
      this.loadingStickers[i] = false;
      this.stickersLoaded[i] = false;
    }
    this.featuredStickerSets.clear();
    this.loadFeaturedDate = 0;
    this.loadFeaturedHash = 0;
    this.allStickers.clear();
    this.allStickersFeatured.clear();
    this.stickersByEmoji.clear();
    this.featuredStickerSetsById.clear();
    this.featuredStickerSets.clear();
    this.unreadStickerSets.clear();
    this.recentGifs.clear();
    this.stickerSetsById.clear();
    this.installedStickerSetsById.clear();
    this.stickerSetsByName.clear();
    this.loadingFeaturedStickers = false;
    this.featuredStickersLoaded = false;
    this.loadingRecentGifs = false;
    this.recentGifsLoaded = false;
    this.loading = false;
    this.loaded = false;
    this.hints.clear();
    this.inlineBots.clear();
    NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.reloadHints, new Object[0]);
    NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
    this.drafts.clear();
    this.draftMessages.clear();
    this.preferences.edit().clear().commit();
    this.botInfos.clear();
    this.botKeyboards.clear();
    this.botKeyboardsByMids.clear();
  }
  
  public void clearBotKeyboard(final long paramLong, final ArrayList<Integer> paramArrayList)
  {
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        if (paramArrayList != null) {
          for (int i = 0; i < paramArrayList.size(); i++)
          {
            long l = DataQuery.this.botKeyboardsByMids.get(((Integer)paramArrayList.get(i)).intValue());
            if (l != 0L)
            {
              DataQuery.this.botKeyboards.remove(l);
              DataQuery.this.botKeyboardsByMids.delete(((Integer)paramArrayList.get(i)).intValue());
              NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.botKeyboardDidLoaded, new Object[] { null, Long.valueOf(l) });
            }
          }
        }
        DataQuery.this.botKeyboards.remove(paramLong);
        NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.botKeyboardDidLoaded, new Object[] { null, Long.valueOf(paramLong) });
      }
    });
  }
  
  public void endTransaction()
  {
    this.inTransaction = false;
  }
  
  public HashMap<String, ArrayList<TLRPC.Document>> getAllStickers()
  {
    return this.allStickers;
  }
  
  public HashMap<String, ArrayList<TLRPC.Document>> getAllStickersFeatured()
  {
    return this.allStickersFeatured;
  }
  
  public int getArchivedStickersCount(int paramInt)
  {
    return this.archivedStickersCount[paramInt];
  }
  
  public TLRPC.DraftMessage getDraft(long paramLong)
  {
    return (TLRPC.DraftMessage)this.drafts.get(paramLong);
  }
  
  public TLRPC.Message getDraftMessage(long paramLong)
  {
    return (TLRPC.Message)this.draftMessages.get(paramLong);
  }
  
  public String getEmojiForSticker(long paramLong)
  {
    String str = (String)this.stickersByEmoji.get(paramLong);
    if (str != null) {}
    for (;;)
    {
      return str;
      str = "";
    }
  }
  
  public ArrayList<TLRPC.MessageEntity> getEntities(CharSequence[] paramArrayOfCharSequence)
  {
    if ((paramArrayOfCharSequence == null) || (paramArrayOfCharSequence[0] == null))
    {
      localObject1 = null;
      return (ArrayList<TLRPC.MessageEntity>)localObject1;
    }
    Object localObject2 = null;
    int i = -1;
    int j = 0;
    int k = 0;
    Object localObject3 = paramArrayOfCharSequence[0];
    label39:
    int m;
    if (k == 0)
    {
      localObject1 = "`";
      m = TextUtils.indexOf((CharSequence)localObject3, (CharSequence)localObject1, j);
      if (m == -1) {
        break label736;
      }
      if (i != -1) {
        break label154;
      }
      if ((paramArrayOfCharSequence[0].length() - m <= 2) || (paramArrayOfCharSequence[0].charAt(m + 1) != '`') || (paramArrayOfCharSequence[0].charAt(m + 2) != '`')) {
        break label142;
      }
      k = 1;
      label113:
      i = m;
      if (k == 0) {
        break label148;
      }
    }
    label142:
    label148:
    for (j = 3;; j = 1)
    {
      j = m + j;
      break;
      localObject1 = "```";
      break label39;
      k = 0;
      break label113;
    }
    label154:
    Object localObject1 = localObject2;
    if (localObject2 == null) {
      localObject1 = new ArrayList();
    }
    if (k != 0) {}
    for (j = 3;; j = 1) {
      for (j = m + j; (j < paramArrayOfCharSequence[0].length()) && (paramArrayOfCharSequence[0].charAt(j) == '`'); j++) {
        m++;
      }
    }
    label234:
    int n;
    label265:
    label282:
    label294:
    Object localObject4;
    label348:
    label370:
    Object localObject5;
    if (k != 0)
    {
      j = 3;
      n = m + j;
      if (k == 0) {
        break label625;
      }
      if (i <= 0) {
        break label577;
      }
      j = paramArrayOfCharSequence[0].charAt(i - 1);
      if ((j != 32) && (j != 10)) {
        break label583;
      }
      j = 1;
      localObject2 = paramArrayOfCharSequence[0];
      if (j == 0) {
        break label589;
      }
      k = 1;
      localObject2 = TextUtils.substring((CharSequence)localObject2, 0, i - k);
      localObject4 = TextUtils.substring(paramArrayOfCharSequence[0], i + 3, m);
      if (m + 3 >= paramArrayOfCharSequence[0].length()) {
        break label595;
      }
      k = paramArrayOfCharSequence[0].charAt(m + 3);
      localObject3 = paramArrayOfCharSequence[0];
      if ((k != 32) && (k != 10)) {
        break label601;
      }
      k = 1;
      localObject5 = TextUtils.substring((CharSequence)localObject3, k + (m + 3), paramArrayOfCharSequence[0].length());
      if (((CharSequence)localObject2).length() == 0) {
        break label607;
      }
      localObject2 = TextUtils.concat(new CharSequence[] { localObject2, "\n" });
      k = j;
      label423:
      localObject3 = localObject5;
      if (((CharSequence)localObject5).length() != 0) {
        localObject3 = TextUtils.concat(new CharSequence[] { "\n", localObject5 });
      }
      j = n;
      if (!TextUtils.isEmpty((CharSequence)localObject4))
      {
        paramArrayOfCharSequence[0] = TextUtils.concat(new CharSequence[] { localObject2, localObject4, localObject3 });
        localObject2 = new TLRPC.TL_messageEntityPre();
        if (k == 0) {
          break label613;
        }
        j = 0;
        label509:
        ((TLRPC.TL_messageEntityPre)localObject2).offset = (j + i);
        if (k == 0) {
          break label619;
        }
        j = 0;
        label526:
        ((TLRPC.TL_messageEntityPre)localObject2).length = (j + (m - i - 3));
        ((TLRPC.TL_messageEntityPre)localObject2).language = "";
        ((ArrayList)localObject1).add(localObject2);
        j = n - 6;
      }
    }
    for (;;)
    {
      i = -1;
      k = 0;
      localObject2 = localObject1;
      break;
      j = 1;
      break label234;
      label577:
      j = 0;
      break label265;
      label583:
      j = 0;
      break label282;
      label589:
      k = 0;
      break label294;
      label595:
      k = 0;
      break label348;
      label601:
      k = 0;
      break label370;
      label607:
      k = 1;
      break label423;
      label613:
      j = 1;
      break label509;
      label619:
      j = 1;
      break label526;
      label625:
      j = n;
      if (i + 1 != m)
      {
        paramArrayOfCharSequence[0] = TextUtils.concat(new CharSequence[] { TextUtils.substring(paramArrayOfCharSequence[0], 0, i), TextUtils.substring(paramArrayOfCharSequence[0], i + 1, m), TextUtils.substring(paramArrayOfCharSequence[0], m + 1, paramArrayOfCharSequence[0].length()) });
        localObject2 = new TLRPC.TL_messageEntityCode();
        ((TLRPC.TL_messageEntityCode)localObject2).offset = i;
        ((TLRPC.TL_messageEntityCode)localObject2).length = (m - i - 1);
        ((ArrayList)localObject1).add(localObject2);
        j = n - 2;
      }
    }
    label736:
    localObject1 = localObject2;
    if (i != -1)
    {
      localObject1 = localObject2;
      if (k != 0)
      {
        paramArrayOfCharSequence[0] = TextUtils.concat(new CharSequence[] { TextUtils.substring(paramArrayOfCharSequence[0], 0, i), TextUtils.substring(paramArrayOfCharSequence[0], i + 2, paramArrayOfCharSequence[0].length()) });
        localObject1 = localObject2;
        if (localObject2 == null) {
          localObject1 = new ArrayList();
        }
        localObject2 = new TLRPC.TL_messageEntityCode();
        ((TLRPC.TL_messageEntityCode)localObject2).offset = i;
        ((TLRPC.TL_messageEntityCode)localObject2).length = 1;
        ((ArrayList)localObject1).add(localObject2);
      }
    }
    localObject3 = localObject1;
    if ((paramArrayOfCharSequence[0] instanceof Spannable))
    {
      localObject5 = (Spannable)paramArrayOfCharSequence[0];
      localObject3 = (TypefaceSpan[])((Spannable)localObject5).getSpans(0, paramArrayOfCharSequence[0].length(), TypefaceSpan.class);
      localObject2 = localObject1;
      if (localObject3 != null)
      {
        localObject2 = localObject1;
        if (localObject3.length > 0)
        {
          j = 0;
          for (;;)
          {
            localObject2 = localObject1;
            if (j >= localObject3.length) {
              break label1042;
            }
            localObject4 = localObject3[j];
            k = ((Spannable)localObject5).getSpanStart(localObject4);
            i = ((Spannable)localObject5).getSpanEnd(localObject4);
            localObject2 = localObject1;
            if (!checkInclusion(k, (ArrayList)localObject1))
            {
              localObject2 = localObject1;
              if (!checkInclusion(i, (ArrayList)localObject1))
              {
                if (!checkIntersection(k, i, (ArrayList)localObject1)) {
                  break;
                }
                localObject2 = localObject1;
              }
            }
            j++;
            localObject1 = localObject2;
          }
          localObject2 = localObject1;
          if (localObject1 == null) {
            localObject2 = new ArrayList();
          }
          if (((TypefaceSpan)localObject4).isBold()) {}
          for (localObject1 = new TLRPC.TL_messageEntityBold();; localObject1 = new TLRPC.TL_messageEntityItalic())
          {
            ((TLRPC.MessageEntity)localObject1).offset = k;
            ((TLRPC.MessageEntity)localObject1).length = (i - k);
            ((ArrayList)localObject2).add(localObject1);
            break;
          }
        }
      }
      label1042:
      localObject4 = (URLSpanUserMention[])((Spannable)localObject5).getSpans(0, paramArrayOfCharSequence[0].length(), URLSpanUserMention.class);
      localObject3 = localObject2;
      if (localObject4 != null)
      {
        localObject3 = localObject2;
        if (localObject4.length > 0)
        {
          localObject1 = localObject2;
          if (localObject2 == null) {
            localObject1 = new ArrayList();
          }
          for (j = 0;; j++)
          {
            localObject3 = localObject1;
            if (j >= localObject4.length) {
              break;
            }
            localObject2 = new TLRPC.TL_inputMessageEntityMentionName();
            ((TLRPC.TL_inputMessageEntityMentionName)localObject2).user_id = MessagesController.getInstance(this.currentAccount).getInputUser(Utilities.parseInt(localObject4[j].getURL()).intValue());
            if (((TLRPC.TL_inputMessageEntityMentionName)localObject2).user_id != null)
            {
              ((TLRPC.TL_inputMessageEntityMentionName)localObject2).offset = ((Spannable)localObject5).getSpanStart(localObject4[j]);
              ((TLRPC.TL_inputMessageEntityMentionName)localObject2).length = (Math.min(((Spannable)localObject5).getSpanEnd(localObject4[j]), paramArrayOfCharSequence[0].length()) - ((TLRPC.TL_inputMessageEntityMentionName)localObject2).offset);
              if (paramArrayOfCharSequence[0].charAt(((TLRPC.TL_inputMessageEntityMentionName)localObject2).offset + ((TLRPC.TL_inputMessageEntityMentionName)localObject2).length - 1) == ' ') {
                ((TLRPC.TL_inputMessageEntityMentionName)localObject2).length -= 1;
              }
              ((ArrayList)localObject1).add(localObject2);
            }
          }
        }
      }
    }
    label1284:
    label1369:
    label1482:
    label1643:
    label1655:
    for (k = 0;; k++)
    {
      localObject1 = localObject3;
      if (k >= 2) {
        break;
      }
      i = 0;
      j = -1;
      if (k == 0)
      {
        localObject2 = "**";
        if (k != 0) {
          break label1369;
        }
        m = 42;
      }
      int i1;
      for (;;)
      {
        i = TextUtils.indexOf(paramArrayOfCharSequence[0], (CharSequence)localObject2, i);
        if (i == -1) {
          break label1655;
        }
        if (j == -1)
        {
          if (i == 0) {}
          for (i1 = 32;; i1 = paramArrayOfCharSequence[0].charAt(i - 1))
          {
            n = j;
            if (!checkInclusion(i, (ArrayList)localObject3)) {
              if (i1 != 32)
              {
                n = j;
                if (i1 != 10) {}
              }
              else
              {
                n = i;
              }
            }
            i += 2;
            j = n;
            break label1284;
            localObject2 = "__";
            break;
            m = 95;
            break label1284;
          }
        }
        i1 = i + 2;
        n = i;
        for (i = i1; (i < paramArrayOfCharSequence[0].length()) && (paramArrayOfCharSequence[0].charAt(i) == m); i++) {
          n++;
        }
        i1 = n + 2;
        if ((!checkInclusion(n, (ArrayList)localObject3)) && (!checkIntersection(j, n, (ArrayList)localObject3))) {
          break label1482;
        }
        j = -1;
        i = i1;
      }
      localObject1 = localObject3;
      i = i1;
      if (j + 2 != n)
      {
        localObject1 = localObject3;
        if (localObject3 == null) {
          localObject1 = new ArrayList();
        }
        paramArrayOfCharSequence[0] = TextUtils.concat(new CharSequence[] { TextUtils.substring(paramArrayOfCharSequence[0], 0, j), TextUtils.substring(paramArrayOfCharSequence[0], j + 2, n), TextUtils.substring(paramArrayOfCharSequence[0], n + 2, paramArrayOfCharSequence[0].length()) });
        if (k != 0) {
          break label1643;
        }
      }
      for (localObject3 = new TLRPC.TL_messageEntityBold();; localObject3 = new TLRPC.TL_messageEntityItalic())
      {
        ((TLRPC.MessageEntity)localObject3).offset = j;
        ((TLRPC.MessageEntity)localObject3).length = (n - j - 2);
        removeOffsetAfter(((TLRPC.MessageEntity)localObject3).offset + ((TLRPC.MessageEntity)localObject3).length, 4, (ArrayList)localObject1);
        ((ArrayList)localObject1).add(localObject3);
        i = i1 - 4;
        j = -1;
        localObject3 = localObject1;
        break;
      }
    }
  }
  
  public ArrayList<TLRPC.StickerSetCovered> getFeaturedStickerSets()
  {
    return this.featuredStickerSets;
  }
  
  public int getFeaturesStickersHashWithoutUnread()
  {
    long l = 0L;
    int i = 0;
    if (i < this.featuredStickerSets.size())
    {
      TLRPC.StickerSet localStickerSet = ((TLRPC.StickerSetCovered)this.featuredStickerSets.get(i)).set;
      if (localStickerSet.archived) {}
      for (;;)
      {
        i++;
        break;
        int j = (int)(localStickerSet.id >> 32);
        int k = (int)localStickerSet.id;
        l = ((l * 20261L + 2147483648L + j) % 2147483648L * 20261L + 2147483648L + k) % 2147483648L;
      }
    }
    return (int)l;
  }
  
  public TLRPC.TL_messages_stickerSet getGroupStickerSetById(TLRPC.StickerSet paramStickerSet)
  {
    TLRPC.TL_messages_stickerSet localTL_messages_stickerSet1 = (TLRPC.TL_messages_stickerSet)this.stickerSetsById.get(paramStickerSet.id);
    TLRPC.TL_messages_stickerSet localTL_messages_stickerSet2 = localTL_messages_stickerSet1;
    if (localTL_messages_stickerSet1 == null)
    {
      localTL_messages_stickerSet1 = (TLRPC.TL_messages_stickerSet)this.groupStickerSets.get(paramStickerSet.id);
      if ((localTL_messages_stickerSet1 != null) && (localTL_messages_stickerSet1.set != null)) {
        break label57;
      }
      loadGroupStickerSet(paramStickerSet, true);
      localTL_messages_stickerSet2 = localTL_messages_stickerSet1;
    }
    for (;;)
    {
      return localTL_messages_stickerSet2;
      label57:
      localTL_messages_stickerSet2 = localTL_messages_stickerSet1;
      if (localTL_messages_stickerSet1.set.hash != paramStickerSet.hash)
      {
        loadGroupStickerSet(paramStickerSet, false);
        localTL_messages_stickerSet2 = localTL_messages_stickerSet1;
      }
    }
  }
  
  public String getLastSearchQuery()
  {
    return this.lastSearchQuery;
  }
  
  public void getMediaCount(final long paramLong, int paramInt1, final int paramInt2, boolean paramBoolean)
  {
    int i = (int)paramLong;
    if ((paramBoolean) || (i == 0))
    {
      getMediaCountDatabase(paramLong, paramInt1, paramInt2);
      return;
    }
    TLRPC.TL_messages_search localTL_messages_search = new TLRPC.TL_messages_search();
    localTL_messages_search.limit = 1;
    localTL_messages_search.offset_id = 0;
    if (paramInt1 == 0) {
      localTL_messages_search.filter = new TLRPC.TL_inputMessagesFilterPhotoVideo();
    }
    for (;;)
    {
      localTL_messages_search.q = "";
      localTL_messages_search.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(i);
      if (localTL_messages_search.peer == null) {
        break;
      }
      paramInt1 = ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_messages_search, new RequestDelegate()
      {
        public void run(final TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
        {
          if (paramAnonymousTL_error == null)
          {
            paramAnonymousTLObject = (TLRPC.messages_Messages)paramAnonymousTLObject;
            MessagesStorage.getInstance(DataQuery.this.currentAccount).putUsersAndChats(paramAnonymousTLObject.users, paramAnonymousTLObject.chats, true, true);
            if (!(paramAnonymousTLObject instanceof TLRPC.TL_messages_messages)) {
              break label81;
            }
          }
          label81:
          for (int i = paramAnonymousTLObject.messages.size();; i = paramAnonymousTLObject.count)
          {
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                MessagesController.getInstance(DataQuery.this.currentAccount).putUsers(paramAnonymousTLObject.users, false);
                MessagesController.getInstance(DataQuery.this.currentAccount).putChats(paramAnonymousTLObject.chats, false);
              }
            });
            DataQuery.this.processLoadedMediaCount(i, paramLong, paramInt2, this.val$classGuid, false);
            return;
          }
        }
      });
      ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(paramInt1, paramInt2);
      break;
      if (paramInt1 == 1) {
        localTL_messages_search.filter = new TLRPC.TL_inputMessagesFilterDocument();
      } else if (paramInt1 == 2) {
        localTL_messages_search.filter = new TLRPC.TL_inputMessagesFilterVoice();
      } else if (paramInt1 == 3) {
        localTL_messages_search.filter = new TLRPC.TL_inputMessagesFilterUrl();
      } else if (paramInt1 == 4) {
        localTL_messages_search.filter = new TLRPC.TL_inputMessagesFilterMusic();
      }
    }
  }
  
  public ArrayList<TLRPC.Document> getRecentGifs()
  {
    return new ArrayList(this.recentGifs);
  }
  
  public ArrayList<TLRPC.Document> getRecentStickers(int paramInt)
  {
    ArrayList localArrayList = this.recentStickers[paramInt];
    return new ArrayList(localArrayList.subList(0, Math.min(localArrayList.size(), 20)));
  }
  
  public ArrayList<TLRPC.Document> getRecentStickersNoCopy(int paramInt)
  {
    return this.recentStickers[paramInt];
  }
  
  public TLRPC.TL_messages_stickerSet getStickerSetById(long paramLong)
  {
    return (TLRPC.TL_messages_stickerSet)this.stickerSetsById.get(paramLong);
  }
  
  public TLRPC.TL_messages_stickerSet getStickerSetByName(String paramString)
  {
    return (TLRPC.TL_messages_stickerSet)this.stickerSetsByName.get(paramString);
  }
  
  public String getStickerSetName(long paramLong)
  {
    Object localObject = (TLRPC.TL_messages_stickerSet)this.stickerSetsById.get(paramLong);
    if (localObject != null) {
      localObject = ((TLRPC.TL_messages_stickerSet)localObject).set.short_name;
    }
    for (;;)
    {
      return (String)localObject;
      localObject = (TLRPC.StickerSetCovered)this.featuredStickerSetsById.get(paramLong);
      if (localObject != null) {
        localObject = ((TLRPC.StickerSetCovered)localObject).set.short_name;
      } else {
        localObject = null;
      }
    }
  }
  
  public ArrayList<TLRPC.TL_messages_stickerSet> getStickerSets(int paramInt)
  {
    if (paramInt == 3) {}
    for (ArrayList localArrayList = this.stickerSets[2];; localArrayList = this.stickerSets[paramInt]) {
      return localArrayList;
    }
  }
  
  public ArrayList<Long> getUnreadStickerSets()
  {
    return this.unreadStickerSets;
  }
  
  public void increaseInlineRaiting(int paramInt)
  {
    int i;
    Object localObject1;
    if (UserConfig.getInstance(this.currentAccount).botRatingLoadTime != 0)
    {
      i = Math.max(1, (int)(System.currentTimeMillis() / 1000L) - UserConfig.getInstance(this.currentAccount).botRatingLoadTime);
      localObject1 = null;
    }
    for (int j = 0;; j++)
    {
      Object localObject2 = localObject1;
      if (j < this.inlineBots.size())
      {
        localObject2 = (TLRPC.TL_topPeer)this.inlineBots.get(j);
        if (((TLRPC.TL_topPeer)localObject2).peer.user_id != paramInt) {}
      }
      else
      {
        localObject1 = localObject2;
        if (localObject2 == null)
        {
          localObject1 = new TLRPC.TL_topPeer();
          ((TLRPC.TL_topPeer)localObject1).peer = new TLRPC.TL_peerUser();
          ((TLRPC.TL_topPeer)localObject1).peer.user_id = paramInt;
          this.inlineBots.add(localObject1);
        }
        ((TLRPC.TL_topPeer)localObject1).rating += Math.exp(i / MessagesController.getInstance(this.currentAccount).ratingDecay);
        Collections.sort(this.inlineBots, new Comparator()
        {
          public int compare(TLRPC.TL_topPeer paramAnonymousTL_topPeer1, TLRPC.TL_topPeer paramAnonymousTL_topPeer2)
          {
            int i;
            if (paramAnonymousTL_topPeer1.rating > paramAnonymousTL_topPeer2.rating) {
              i = -1;
            }
            for (;;)
            {
              return i;
              if (paramAnonymousTL_topPeer1.rating < paramAnonymousTL_topPeer2.rating) {
                i = 1;
              } else {
                i = 0;
              }
            }
          }
        });
        if (this.inlineBots.size() > 20) {
          this.inlineBots.remove(this.inlineBots.size() - 1);
        }
        savePeer(paramInt, 1, ((TLRPC.TL_topPeer)localObject1).rating);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
        return;
        i = 60;
        break;
      }
    }
  }
  
  public void increasePeerRaiting(final long paramLong)
  {
    int i = (int)paramLong;
    if (i <= 0) {}
    label71:
    for (;;)
    {
      return;
      if (i > 0) {}
      for (TLRPC.User localUser = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(i));; localUser = null)
      {
        if ((localUser == null) || (localUser.bot)) {
          break label71;
        }
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable()
        {
          public void run()
          {
            d1 = 0.0D;
            int i = 0;
            int j = 0;
            try
            {
              SQLiteCursor localSQLiteCursor = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT MAX(mid), MAX(date) FROM messages WHERE uid = %d AND out = 1", new Object[] { Long.valueOf(paramLong) }), new Object[0]);
              if (localSQLiteCursor.next())
              {
                j = localSQLiteCursor.intValue(0);
                i = localSQLiteCursor.intValue(1);
              }
              localSQLiteCursor.dispose();
              d2 = d1;
              if (j > 0)
              {
                d2 = d1;
                if (UserConfig.getInstance(DataQuery.this.currentAccount).ratingLoadTime != 0)
                {
                  j = UserConfig.getInstance(DataQuery.this.currentAccount).ratingLoadTime;
                  d2 = i - j;
                }
              }
            }
            catch (Exception localException)
            {
              for (;;)
              {
                FileLog.e(localException);
                final double d2 = d1;
              }
            }
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                Object localObject1 = null;
                int i = 0;
                Object localObject2 = localObject1;
                if (i < DataQuery.this.hints.size())
                {
                  localObject2 = (TLRPC.TL_topPeer)DataQuery.this.hints.get(i);
                  if (((DataQuery.48.this.val$lower_id >= 0) || ((((TLRPC.TL_topPeer)localObject2).peer.chat_id != -DataQuery.48.this.val$lower_id) && (((TLRPC.TL_topPeer)localObject2).peer.channel_id != -DataQuery.48.this.val$lower_id))) && ((DataQuery.48.this.val$lower_id <= 0) || (((TLRPC.TL_topPeer)localObject2).peer.user_id != DataQuery.48.this.val$lower_id))) {}
                }
                else
                {
                  localObject1 = localObject2;
                  if (localObject2 == null)
                  {
                    localObject1 = new TLRPC.TL_topPeer();
                    if (DataQuery.48.this.val$lower_id <= 0) {
                      break label286;
                    }
                    ((TLRPC.TL_topPeer)localObject1).peer = new TLRPC.TL_peerUser();
                    ((TLRPC.TL_topPeer)localObject1).peer.user_id = DataQuery.48.this.val$lower_id;
                  }
                }
                for (;;)
                {
                  DataQuery.this.hints.add(localObject1);
                  ((TLRPC.TL_topPeer)localObject1).rating += Math.exp(d2 / MessagesController.getInstance(DataQuery.this.currentAccount).ratingDecay);
                  Collections.sort(DataQuery.this.hints, new Comparator()
                  {
                    public int compare(TLRPC.TL_topPeer paramAnonymous3TL_topPeer1, TLRPC.TL_topPeer paramAnonymous3TL_topPeer2)
                    {
                      int i;
                      if (paramAnonymous3TL_topPeer1.rating > paramAnonymous3TL_topPeer2.rating) {
                        i = -1;
                      }
                      for (;;)
                      {
                        return i;
                        if (paramAnonymous3TL_topPeer1.rating < paramAnonymous3TL_topPeer2.rating) {
                          i = 1;
                        } else {
                          i = 0;
                        }
                      }
                    }
                  });
                  DataQuery.this.savePeer((int)DataQuery.48.this.val$did, 0, ((TLRPC.TL_topPeer)localObject1).rating);
                  NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.reloadHints, new Object[0]);
                  return;
                  i++;
                  break;
                  label286:
                  ((TLRPC.TL_topPeer)localObject1).peer = new TLRPC.TL_peerChat();
                  ((TLRPC.TL_topPeer)localObject1).peer.chat_id = (-DataQuery.48.this.val$lower_id);
                }
              }
            });
          }
        });
        break;
      }
    }
  }
  
  public void installShortcut(long paramLong)
  {
    label71:
    do
    {
      try
      {
        localIntent1 = createIntrnalShortcutIntent(paramLong);
        i = (int)paramLong;
        j = (int)(paramLong >> 32);
        localUser = null;
        localChat = null;
        if (i != 0) {
          break label513;
        }
        localObject1 = MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf(j));
        if (localObject1 == null) {
          return;
        }
      }
      catch (Exception localThrowable)
      {
        try
        {
          localObject1 = BitmapFactory.decodeFile(FileLoader.getPathToAttach((TLObject)localObject2, true).toString());
          if (j != 0) {
            break label170;
          }
          localObject3 = localObject1;
          if (localObject1 == null) {
            break label399;
          }
          localObject3 = localObject1;
          k = AndroidUtilities.dp(58.0F);
          localObject3 = localObject1;
          localObject2 = Bitmap.createBitmap(k, k, Bitmap.Config.ARGB_8888);
          localObject3 = localObject1;
          ((Bitmap)localObject2).eraseColor(0);
          localObject3 = localObject1;
          localObject4 = new android/graphics/Canvas;
          localObject3 = localObject1;
          ((Canvas)localObject4).<init>((Bitmap)localObject2);
          if (j == 0) {
            break label660;
          }
          localObject3 = localObject1;
          localObject5 = new org/telegram/ui/Components/AvatarDrawable;
          localObject3 = localObject1;
          ((AvatarDrawable)localObject5).<init>(localUser);
          localObject3 = localObject1;
          ((AvatarDrawable)localObject5).setSavedMessages(1);
          localObject3 = localObject1;
          ((AvatarDrawable)localObject5).setBounds(0, 0, k, k);
          localObject3 = localObject1;
          ((AvatarDrawable)localObject5).draw((Canvas)localObject4);
          localObject3 = localObject1;
          localObject5 = ApplicationLoader.applicationContext.getResources().getDrawable(NUM);
          localObject3 = localObject1;
          j = AndroidUtilities.dp(15.0F);
          localObject3 = localObject1;
          i = k - j - AndroidUtilities.dp(2.0F);
          localObject3 = localObject1;
          k = k - j - AndroidUtilities.dp(2.0F);
          localObject3 = localObject1;
          ((Drawable)localObject5).setBounds(i, k, i + j, k + j);
          localObject3 = localObject1;
          ((Drawable)localObject5).draw((Canvas)localObject4);
          localObject3 = localObject1;
        }
        catch (Throwable localThrowable)
        {
          try
          {
            Intent localIntent1;
            int i;
            int j;
            TLRPC.User localUser;
            TLRPC.Chat localChat;
            String str2;
            int k;
            Object localObject5;
            ((Canvas)localObject4).setBitmap(null);
            Object localObject3 = localObject2;
            if (Build.VERSION.SDK_INT < 26) {
              break label982;
            }
            Object localObject1 = new android/content/pm/ShortcutInfo$Builder;
            Object localObject4 = ApplicationLoader.applicationContext;
            Object localObject2 = new java/lang/StringBuilder;
            ((StringBuilder)localObject2).<init>();
            ((ShortcutInfo.Builder)localObject1).<init>((Context)localObject4, "sdid_" + paramLong);
            localObject1 = ((ShortcutInfo.Builder)localObject1).setShortLabel(str2).setIntent(localIntent1);
            if (localObject3 == null) {
              break label876;
            }
            ((ShortcutInfo.Builder)localObject1).setIcon(Icon.createWithBitmap((Bitmap)localObject3));
            for (;;)
            {
              ((ShortcutManager)ApplicationLoader.applicationContext.getSystemService(ShortcutManager.class)).requestPinShortcut(((ShortcutInfo.Builder)localObject1).build(), null);
              break;
              localException1 = localException1;
              FileLog.e(localException1);
              break;
              if (i > 0)
              {
                localUser = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(i));
                break label71;
              }
              if (i >= 0) {
                break;
              }
              localChat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-i));
              break label71;
              String str1 = ContactsController.formatName(localUser.first_name, localUser.last_name);
              str2 = str1;
              j = i;
              if (localUser.photo == null) {
                break label114;
              }
              localObject2 = localUser.photo.photo_small;
              str2 = str1;
              j = i;
              break label114;
              str1 = localChat.title;
              str2 = str1;
              j = i;
              if (localChat.photo == null) {
                break label114;
              }
              localObject2 = localChat.photo.photo_small;
              str2 = str1;
              j = i;
              break label114;
              localObject3 = str1;
              localObject5 = new android/graphics/BitmapShader;
              localObject3 = str1;
              ((BitmapShader)localObject5).<init>(str1, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
              localObject3 = str1;
              if (roundPaint == null)
              {
                localObject3 = str1;
                Object localObject6 = new android/graphics/Paint;
                localObject3 = str1;
                ((Paint)localObject6).<init>(1);
                localObject3 = str1;
                roundPaint = (Paint)localObject6;
                localObject3 = str1;
                localObject6 = new android/graphics/RectF;
                localObject3 = str1;
                ((RectF)localObject6).<init>();
                localObject3 = str1;
                bitmapRect = (RectF)localObject6;
              }
              localObject3 = str1;
              float f = k / str1.getWidth();
              localObject3 = str1;
              ((Canvas)localObject4).save();
              localObject3 = str1;
              ((Canvas)localObject4).scale(f, f);
              localObject3 = str1;
              roundPaint.setShader((Shader)localObject5);
              localObject3 = str1;
              bitmapRect.set(0.0F, 0.0F, str1.getWidth(), str1.getHeight());
              localObject3 = str1;
              ((Canvas)localObject4).drawRoundRect(bitmapRect, str1.getWidth(), str1.getHeight(), roundPaint);
              localObject3 = str1;
              ((Canvas)localObject4).restore();
              break label289;
              localThrowable = localThrowable;
              FileLog.e(localThrowable);
              break label399;
              if (localUser != null)
              {
                if (localUser.bot) {
                  localThrowable.setIcon(Icon.createWithResource(ApplicationLoader.applicationContext, NUM));
                } else {
                  localThrowable.setIcon(Icon.createWithResource(ApplicationLoader.applicationContext, NUM));
                }
              }
              else if (localChat != null) {
                if ((ChatObject.isChannel(localChat)) && (!localChat.megagroup)) {
                  localThrowable.setIcon(Icon.createWithResource(ApplicationLoader.applicationContext, NUM));
                } else {
                  localThrowable.setIcon(Icon.createWithResource(ApplicationLoader.applicationContext, NUM));
                }
              }
            }
            Intent localIntent2 = new android/content/Intent;
            localIntent2.<init>();
            if (localObject3 == null) {
              break label1059;
            }
            localIntent2.putExtra("android.intent.extra.shortcut.ICON", (Parcelable)localObject3);
            for (;;)
            {
              localIntent2.putExtra("android.intent.extra.shortcut.INTENT", localIntent1);
              localIntent2.putExtra("android.intent.extra.shortcut.NAME", str2);
              localIntent2.putExtra("duplicate", false);
              localIntent2.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
              ApplicationLoader.applicationContext.sendBroadcast(localIntent2);
              break;
              if (localUser != null)
              {
                if (localUser.bot) {
                  localIntent2.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", Intent.ShortcutIconResource.fromContext(ApplicationLoader.applicationContext, NUM));
                } else {
                  localIntent2.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", Intent.ShortcutIconResource.fromContext(ApplicationLoader.applicationContext, NUM));
                }
              }
              else if (localChat != null) {
                if ((ChatObject.isChannel(localChat)) && (!localChat.megagroup)) {
                  localIntent2.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", Intent.ShortcutIconResource.fromContext(ApplicationLoader.applicationContext, NUM));
                } else {
                  localIntent2.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", Intent.ShortcutIconResource.fromContext(ApplicationLoader.applicationContext, NUM));
                }
              }
            }
          }
          catch (Exception localException2)
          {
            for (;;) {}
          }
        }
      }
      localUser = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(((TLRPC.EncryptedChat)localObject1).user_id));
    } while ((localUser == null) && (localChat == null));
    localObject2 = null;
    i = 0;
    if (localUser != null) {
      if (UserObject.isUserSelf(localUser))
      {
        str2 = LocaleController.getString("SavedMessages", NUM);
        j = 1;
        label114:
        localObject3 = null;
        localObject4 = null;
        localObject1 = null;
        if ((j != 0) || (localObject2 != null)) {
          if (j == 0) {
            localObject3 = localObject4;
          }
        }
      }
    }
  }
  
  public boolean isLoadingStickers(int paramInt)
  {
    return this.loadingStickers[paramInt];
  }
  
  public boolean isMessageFound(int paramInt, boolean paramBoolean)
  {
    boolean bool = true;
    SparseArray[] arrayOfSparseArray = this.searchResultMessagesMap;
    int i;
    if (paramBoolean)
    {
      i = 1;
      if (arrayOfSparseArray[i].indexOfKey(paramInt) < 0) {
        break label37;
      }
    }
    label37:
    for (paramBoolean = bool;; paramBoolean = false)
    {
      return paramBoolean;
      i = 0;
      break;
    }
  }
  
  public boolean isStickerInFavorites(TLRPC.Document paramDocument)
  {
    int i = 0;
    if (i < this.recentStickers[2].size())
    {
      TLRPC.Document localDocument = (TLRPC.Document)this.recentStickers[2].get(i);
      if ((localDocument.id != paramDocument.id) || (localDocument.dc_id != paramDocument.dc_id)) {}
    }
    for (boolean bool = true;; bool = false)
    {
      return bool;
      i++;
      break;
    }
  }
  
  public boolean isStickerPackInstalled(long paramLong)
  {
    if (this.installedStickerSetsById.indexOfKey(paramLong) >= 0) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean isStickerPackInstalled(String paramString)
  {
    return this.stickerSetsByName.containsKey(paramString);
  }
  
  public boolean isStickerPackUnread(long paramLong)
  {
    return this.unreadStickerSets.contains(Long.valueOf(paramLong));
  }
  
  public void loadArchivedStickersCount(final int paramInt, boolean paramBoolean)
  {
    boolean bool = true;
    if (paramBoolean)
    {
      int i = MessagesController.getNotificationsSettings(this.currentAccount).getInt("archivedStickersCount" + paramInt, -1);
      if (i == -1) {
        loadArchivedStickersCount(paramInt, false);
      }
      for (;;)
      {
        return;
        this.archivedStickersCount[paramInt] = i;
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.archivedStickersCountDidLoaded, new Object[] { Integer.valueOf(paramInt) });
      }
    }
    TLRPC.TL_messages_getArchivedStickers localTL_messages_getArchivedStickers = new TLRPC.TL_messages_getArchivedStickers();
    localTL_messages_getArchivedStickers.limit = 0;
    if (paramInt == 1) {}
    for (paramBoolean = bool;; paramBoolean = false)
    {
      localTL_messages_getArchivedStickers.masks = paramBoolean;
      ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_messages_getArchivedStickers, new RequestDelegate()
      {
        public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
        {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              if (paramAnonymousTL_error == null)
              {
                TLRPC.TL_messages_archivedStickers localTL_messages_archivedStickers = (TLRPC.TL_messages_archivedStickers)paramAnonymousTLObject;
                DataQuery.this.archivedStickersCount[DataQuery.22.this.val$type] = localTL_messages_archivedStickers.count;
                MessagesController.getNotificationsSettings(DataQuery.this.currentAccount).edit().putInt("archivedStickersCount" + DataQuery.22.this.val$type, localTL_messages_archivedStickers.count).commit();
                NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.archivedStickersCountDidLoaded, new Object[] { Integer.valueOf(DataQuery.22.this.val$type) });
              }
            }
          });
        }
      });
      break;
    }
  }
  
  public void loadBotInfo(final int paramInt1, boolean paramBoolean, final int paramInt2)
  {
    if (paramBoolean)
    {
      TLRPC.BotInfo localBotInfo = (TLRPC.BotInfo)this.botInfos.get(paramInt1);
      if (localBotInfo != null) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.botInfoDidLoaded, new Object[] { localBotInfo, Integer.valueOf(paramInt2) });
      }
    }
    for (;;)
    {
      return;
      MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable()
      {
        public void run()
        {
          Runnable local1 = null;
          try
          {
            SQLiteCursor localSQLiteCursor = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT info FROM bot_info WHERE uid = %d", new Object[] { Integer.valueOf(paramInt1) }), new Object[0]);
            Object localObject = local1;
            if (localSQLiteCursor.next())
            {
              localObject = local1;
              if (!localSQLiteCursor.isNull(0))
              {
                NativeByteBuffer localNativeByteBuffer = localSQLiteCursor.byteBufferValue(0);
                localObject = local1;
                if (localNativeByteBuffer != null)
                {
                  localObject = TLRPC.BotInfo.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
                  localNativeByteBuffer.reuse();
                }
              }
            }
            localSQLiteCursor.dispose();
            if (localObject != null)
            {
              local1 = new org/telegram/messenger/DataQuery$66$1;
              local1.<init>(this, (TLRPC.BotInfo)localObject);
              AndroidUtilities.runOnUIThread(local1);
            }
            return;
          }
          catch (Exception localException)
          {
            for (;;)
            {
              FileLog.e(localException);
            }
          }
        }
      });
    }
  }
  
  public void loadBotKeyboard(final long paramLong)
  {
    TLRPC.Message localMessage = (TLRPC.Message)this.botKeyboards.get(paramLong);
    if (localMessage != null) {
      NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.botKeyboardDidLoaded, new Object[] { localMessage, Long.valueOf(paramLong) });
    }
    for (;;)
    {
      return;
      MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable()
      {
        public void run()
        {
          Runnable local1 = null;
          try
          {
            SQLiteCursor localSQLiteCursor = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT info FROM bot_keyboard WHERE uid = %d", new Object[] { Long.valueOf(paramLong) }), new Object[0]);
            Object localObject = local1;
            if (localSQLiteCursor.next())
            {
              localObject = local1;
              if (!localSQLiteCursor.isNull(0))
              {
                NativeByteBuffer localNativeByteBuffer = localSQLiteCursor.byteBufferValue(0);
                localObject = local1;
                if (localNativeByteBuffer != null)
                {
                  localObject = TLRPC.Message.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
                  localNativeByteBuffer.reuse();
                }
              }
            }
            localSQLiteCursor.dispose();
            if (localObject != null)
            {
              local1 = new org/telegram/messenger/DataQuery$65$1;
              local1.<init>(this, (TLRPC.Message)localObject);
              AndroidUtilities.runOnUIThread(local1);
            }
            return;
          }
          catch (Exception localException)
          {
            for (;;)
            {
              FileLog.e(localException);
            }
          }
        }
      });
    }
  }
  
  public void loadDrafts()
  {
    if ((UserConfig.getInstance(this.currentAccount).draftsLoaded) || (this.loadingDrafts)) {}
    for (;;)
    {
      return;
      this.loadingDrafts = true;
      TLRPC.TL_messages_getAllDrafts localTL_messages_getAllDrafts = new TLRPC.TL_messages_getAllDrafts();
      ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_messages_getAllDrafts, new RequestDelegate()
      {
        public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
        {
          if (paramAnonymousTL_error != null) {}
          for (;;)
          {
            return;
            MessagesController.getInstance(DataQuery.this.currentAccount).processUpdates((TLRPC.Updates)paramAnonymousTLObject, false);
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                UserConfig.getInstance(DataQuery.this.currentAccount).draftsLoaded = true;
                DataQuery.access$6302(DataQuery.this, false);
                UserConfig.getInstance(DataQuery.this.currentAccount).saveConfig(false);
              }
            });
          }
        }
      });
    }
  }
  
  public void loadFeaturedStickers(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (this.loadingFeaturedStickers) {}
    for (;;)
    {
      return;
      this.loadingFeaturedStickers = true;
      if (!paramBoolean1) {
        break;
      }
      MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable()
      {
        /* Error */
        public void run()
        {
          // Byte code:
          //   0: aconst_null
          //   1: astore_1
          //   2: aconst_null
          //   3: astore_2
          //   4: aconst_null
          //   5: astore_3
          //   6: new 26	java/util/ArrayList
          //   9: dup
          //   10: invokespecial 27	java/util/ArrayList:<init>	()V
          //   13: astore 4
          //   15: iconst_0
          //   16: istore 5
          //   18: iconst_0
          //   19: istore 6
          //   21: iconst_0
          //   22: istore 7
          //   24: iconst_0
          //   25: istore 8
          //   27: iconst_0
          //   28: istore 9
          //   30: aconst_null
          //   31: astore 10
          //   33: aconst_null
          //   34: astore 11
          //   36: aload_2
          //   37: astore 12
          //   39: iload 5
          //   41: istore 13
          //   43: aload_0
          //   44: getfield 17	org/telegram/messenger/DataQuery$14:this$0	Lorg/telegram/messenger/DataQuery;
          //   47: invokestatic 31	org/telegram/messenger/DataQuery:access$000	(Lorg/telegram/messenger/DataQuery;)I
          //   50: invokestatic 37	org/telegram/messenger/MessagesStorage:getInstance	(I)Lorg/telegram/messenger/MessagesStorage;
          //   53: invokevirtual 41	org/telegram/messenger/MessagesStorage:getDatabase	()Lorg/telegram/SQLite/SQLiteDatabase;
          //   56: ldc 43
          //   58: iconst_0
          //   59: anewarray 4	java/lang/Object
          //   62: invokevirtual 49	org/telegram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/telegram/SQLite/SQLiteCursor;
          //   65: astore 14
          //   67: aload_2
          //   68: astore 12
          //   70: iload 5
          //   72: istore 13
          //   74: aload 14
          //   76: astore 11
          //   78: aload 14
          //   80: astore 10
          //   82: aload 14
          //   84: invokevirtual 55	org/telegram/SQLite/SQLiteCursor:next	()Z
          //   87: ifeq +277 -> 364
          //   90: aload_2
          //   91: astore 12
          //   93: iload 5
          //   95: istore 13
          //   97: aload 14
          //   99: astore 11
          //   101: aload 14
          //   103: astore 10
          //   105: aload 14
          //   107: iconst_0
          //   108: invokevirtual 59	org/telegram/SQLite/SQLiteCursor:byteBufferValue	(I)Lorg/telegram/tgnet/NativeByteBuffer;
          //   111: astore 15
          //   113: aload_3
          //   114: astore_1
          //   115: aload 15
          //   117: ifnull +87 -> 204
          //   120: aload_2
          //   121: astore 12
          //   123: iload 5
          //   125: istore 13
          //   127: aload 14
          //   129: astore 11
          //   131: aload 14
          //   133: astore 10
          //   135: new 26	java/util/ArrayList
          //   138: astore_1
          //   139: aload_2
          //   140: astore 12
          //   142: iload 5
          //   144: istore 13
          //   146: aload 14
          //   148: astore 11
          //   150: aload 14
          //   152: astore 10
          //   154: aload_1
          //   155: invokespecial 27	java/util/ArrayList:<init>	()V
          //   158: aload 15
          //   160: iconst_0
          //   161: invokevirtual 65	org/telegram/tgnet/NativeByteBuffer:readInt32	(Z)I
          //   164: istore 9
          //   166: iconst_0
          //   167: istore 13
          //   169: iload 13
          //   171: iload 9
          //   173: if_icmpge +26 -> 199
          //   176: aload_1
          //   177: aload 15
          //   179: aload 15
          //   181: iconst_0
          //   182: invokevirtual 65	org/telegram/tgnet/NativeByteBuffer:readInt32	(Z)I
          //   185: iconst_0
          //   186: invokestatic 71	org/telegram/tgnet/TLRPC$StickerSetCovered:TLdeserialize	(Lorg/telegram/tgnet/AbstractSerializedData;IZ)Lorg/telegram/tgnet/TLRPC$StickerSetCovered;
          //   189: invokevirtual 75	java/util/ArrayList:add	(Ljava/lang/Object;)Z
          //   192: pop
          //   193: iinc 13 1
          //   196: goto -27 -> 169
          //   199: aload 15
          //   201: invokevirtual 78	org/telegram/tgnet/NativeByteBuffer:reuse	()V
          //   204: aload_1
          //   205: astore 12
          //   207: iload 5
          //   209: istore 13
          //   211: aload 14
          //   213: astore 11
          //   215: aload 14
          //   217: astore 10
          //   219: aload 14
          //   221: iconst_1
          //   222: invokevirtual 59	org/telegram/SQLite/SQLiteCursor:byteBufferValue	(I)Lorg/telegram/tgnet/NativeByteBuffer;
          //   225: astore_2
          //   226: aload_2
          //   227: ifnull +89 -> 316
          //   230: aload_1
          //   231: astore 12
          //   233: iload 5
          //   235: istore 13
          //   237: aload 14
          //   239: astore 11
          //   241: aload 14
          //   243: astore 10
          //   245: aload_2
          //   246: iconst_0
          //   247: invokevirtual 65	org/telegram/tgnet/NativeByteBuffer:readInt32	(Z)I
          //   250: istore 7
          //   252: iconst_0
          //   253: istore 9
          //   255: iload 9
          //   257: iload 7
          //   259: if_icmpge +38 -> 297
          //   262: aload_1
          //   263: astore 12
          //   265: iload 5
          //   267: istore 13
          //   269: aload 14
          //   271: astore 11
          //   273: aload 14
          //   275: astore 10
          //   277: aload 4
          //   279: aload_2
          //   280: iconst_0
          //   281: invokevirtual 82	org/telegram/tgnet/NativeByteBuffer:readInt64	(Z)J
          //   284: invokestatic 88	java/lang/Long:valueOf	(J)Ljava/lang/Long;
          //   287: invokevirtual 75	java/util/ArrayList:add	(Ljava/lang/Object;)Z
          //   290: pop
          //   291: iinc 9 1
          //   294: goto -39 -> 255
          //   297: aload_1
          //   298: astore 12
          //   300: iload 5
          //   302: istore 13
          //   304: aload 14
          //   306: astore 11
          //   308: aload 14
          //   310: astore 10
          //   312: aload_2
          //   313: invokevirtual 78	org/telegram/tgnet/NativeByteBuffer:reuse	()V
          //   316: aload_1
          //   317: astore 12
          //   319: iload 5
          //   321: istore 13
          //   323: aload 14
          //   325: astore 11
          //   327: aload 14
          //   329: astore 10
          //   331: aload 14
          //   333: iconst_2
          //   334: invokevirtual 92	org/telegram/SQLite/SQLiteCursor:intValue	(I)I
          //   337: istore 7
          //   339: aload_1
          //   340: astore 12
          //   342: iload 7
          //   344: istore 13
          //   346: aload 14
          //   348: astore 11
          //   350: aload 14
          //   352: astore 10
          //   354: aload_0
          //   355: getfield 17	org/telegram/messenger/DataQuery$14:this$0	Lorg/telegram/messenger/DataQuery;
          //   358: aload_1
          //   359: invokestatic 96	org/telegram/messenger/DataQuery:access$1000	(Lorg/telegram/messenger/DataQuery;Ljava/util/ArrayList;)I
          //   362: istore 9
          //   364: aload_1
          //   365: astore 10
          //   367: iload 7
          //   369: istore 6
          //   371: iload 9
          //   373: istore 5
          //   375: aload 14
          //   377: ifnull +19 -> 396
          //   380: aload 14
          //   382: invokevirtual 99	org/telegram/SQLite/SQLiteCursor:dispose	()V
          //   385: iload 9
          //   387: istore 5
          //   389: iload 7
          //   391: istore 6
          //   393: aload_1
          //   394: astore 10
          //   396: aload_0
          //   397: getfield 17	org/telegram/messenger/DataQuery$14:this$0	Lorg/telegram/messenger/DataQuery;
          //   400: aload 10
          //   402: aload 4
          //   404: iconst_1
          //   405: iload 6
          //   407: iload 5
          //   409: invokestatic 103	org/telegram/messenger/DataQuery:access$1100	(Lorg/telegram/messenger/DataQuery;Ljava/util/ArrayList;Ljava/util/ArrayList;ZII)V
          //   412: return
          //   413: astore_1
          //   414: aload 11
          //   416: astore 14
          //   418: aload 14
          //   420: astore 10
          //   422: aload_1
          //   423: invokestatic 109	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
          //   426: aload 12
          //   428: astore 10
          //   430: iload 13
          //   432: istore 6
          //   434: iload 8
          //   436: istore 5
          //   438: aload 14
          //   440: ifnull -44 -> 396
          //   443: aload 14
          //   445: invokevirtual 99	org/telegram/SQLite/SQLiteCursor:dispose	()V
          //   448: aload 12
          //   450: astore 10
          //   452: iload 13
          //   454: istore 6
          //   456: iload 8
          //   458: istore 5
          //   460: goto -64 -> 396
          //   463: astore 14
          //   465: aload 14
          //   467: astore_1
          //   468: aload 10
          //   470: ifnull +8 -> 478
          //   473: aload 10
          //   475: invokevirtual 99	org/telegram/SQLite/SQLiteCursor:dispose	()V
          //   478: aload_1
          //   479: athrow
          //   480: astore_1
          //   481: aload 14
          //   483: astore 10
          //   485: goto -17 -> 468
          //   488: astore 10
          //   490: aload_1
          //   491: astore 12
          //   493: iload 6
          //   495: istore 13
          //   497: aload 10
          //   499: astore_1
          //   500: goto -82 -> 418
          // Local variable table:
          //   start	length	slot	name	signature
          //   0	503	0	this	14
          //   1	393	1	localObject1	Object
          //   413	10	1	localThrowable1	Throwable
          //   467	12	1	localObject2	Object
          //   480	11	1	localObject3	Object
          //   499	1	1	localObject4	Object
          //   3	310	2	localNativeByteBuffer1	NativeByteBuffer
          //   5	109	3	localObject5	Object
          //   13	390	4	localArrayList	ArrayList
          //   16	443	5	i	int
          //   19	475	6	j	int
          //   22	368	7	k	int
          //   25	432	8	m	int
          //   28	358	9	n	int
          //   31	453	10	localObject6	Object
          //   488	10	10	localThrowable2	Throwable
          //   34	381	11	localObject7	Object
          //   37	455	12	localObject8	Object
          //   41	455	13	i1	int
          //   65	379	14	localObject9	Object
          //   463	19	14	localObject10	Object
          //   111	89	15	localNativeByteBuffer2	NativeByteBuffer
          // Exception table:
          //   from	to	target	type
          //   43	67	413	java/lang/Throwable
          //   82	90	413	java/lang/Throwable
          //   105	113	413	java/lang/Throwable
          //   135	139	413	java/lang/Throwable
          //   154	158	413	java/lang/Throwable
          //   219	226	413	java/lang/Throwable
          //   245	252	413	java/lang/Throwable
          //   277	291	413	java/lang/Throwable
          //   312	316	413	java/lang/Throwable
          //   331	339	413	java/lang/Throwable
          //   354	364	413	java/lang/Throwable
          //   43	67	463	finally
          //   82	90	463	finally
          //   105	113	463	finally
          //   135	139	463	finally
          //   154	158	463	finally
          //   219	226	463	finally
          //   245	252	463	finally
          //   277	291	463	finally
          //   312	316	463	finally
          //   331	339	463	finally
          //   354	364	463	finally
          //   422	426	463	finally
          //   158	166	480	finally
          //   176	193	480	finally
          //   199	204	480	finally
          //   158	166	488	java/lang/Throwable
          //   176	193	488	java/lang/Throwable
          //   199	204	488	java/lang/Throwable
        }
      });
    }
    final TLRPC.TL_messages_getFeaturedStickers localTL_messages_getFeaturedStickers = new TLRPC.TL_messages_getFeaturedStickers();
    if (paramBoolean2) {}
    for (int i = 0;; i = this.loadFeaturedHash)
    {
      localTL_messages_getFeaturedStickers.hash = i;
      ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_messages_getFeaturedStickers, new RequestDelegate()
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
                DataQuery.this.processLoadedFeaturedStickers(localTL_messages_featuredStickers.sets, localTL_messages_featuredStickers.unread, false, (int)(System.currentTimeMillis() / 1000L), localTL_messages_featuredStickers.hash);
              }
              for (;;)
              {
                return;
                DataQuery.this.processLoadedFeaturedStickers(null, null, false, (int)(System.currentTimeMillis() / 1000L), DataQuery.15.this.val$req.hash);
              }
            }
          });
        }
      });
      break;
    }
  }
  
  public void loadHints(boolean paramBoolean)
  {
    if (this.loading) {}
    for (;;)
    {
      return;
      if (paramBoolean)
      {
        if (!this.loaded)
        {
          this.loading = true;
          MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable()
          {
            public void run()
            {
              ArrayList localArrayList1 = new ArrayList();
              ArrayList localArrayList2 = new ArrayList();
              ArrayList localArrayList3 = new ArrayList();
              ArrayList localArrayList4 = new ArrayList();
              int i = UserConfig.getInstance(DataQuery.this.currentAccount).getClientUserId();
              ArrayList localArrayList5;
              Object localObject1;
              SQLiteCursor localSQLiteCursor;
              int j;
              int k;
              TLRPC.TL_topPeer localTL_topPeer;
              Object localObject2;
              try
              {
                localArrayList5 = new java/util/ArrayList;
                localArrayList5.<init>();
                localObject1 = new java/util/ArrayList;
                ((ArrayList)localObject1).<init>();
                localSQLiteCursor = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().queryFinalized("SELECT did, type, rating FROM chat_hints WHERE 1 ORDER BY rating DESC", new Object[0]);
                for (;;)
                {
                  if (localSQLiteCursor.next())
                  {
                    j = localSQLiteCursor.intValue(0);
                    if (j != i)
                    {
                      k = localSQLiteCursor.intValue(1);
                      localTL_topPeer = new org/telegram/tgnet/TLRPC$TL_topPeer;
                      localTL_topPeer.<init>();
                      localTL_topPeer.rating = localSQLiteCursor.doubleValue(2);
                      if (j > 0)
                      {
                        localObject2 = new org/telegram/tgnet/TLRPC$TL_peerUser;
                        ((TLRPC.TL_peerUser)localObject2).<init>();
                        localTL_topPeer.peer = ((TLRPC.Peer)localObject2);
                        localTL_topPeer.peer.user_id = j;
                        localArrayList5.add(Integer.valueOf(j));
                        label187:
                        if (k != 0) {
                          break label251;
                        }
                        localArrayList1.add(localTL_topPeer);
                        continue;
                      }
                    }
                  }
                }
              }
              catch (Exception localException)
              {
                FileLog.e(localException);
              }
              for (;;)
              {
                localObject2 = new org/telegram/tgnet/TLRPC$TL_peerChat;
                ((TLRPC.TL_peerChat)localObject2).<init>();
                localTL_topPeer.peer = ((TLRPC.Peer)localObject2);
                localTL_topPeer.peer.chat_id = (-j);
                ((ArrayList)localObject1).add(Integer.valueOf(-j));
                break label187;
                label251:
                if (k != 1) {
                  break;
                }
                localArrayList2.add(localTL_topPeer);
                break;
                localSQLiteCursor.dispose();
                if (!localArrayList5.isEmpty()) {
                  MessagesStorage.getInstance(DataQuery.this.currentAccount).getUsersInternal(TextUtils.join(",", localArrayList5), localException);
                }
                if (!((ArrayList)localObject1).isEmpty()) {
                  MessagesStorage.getInstance(DataQuery.this.currentAccount).getChatsInternal(TextUtils.join(",", (Iterable)localObject1), localArrayList4);
                }
                localObject1 = new org/telegram/messenger/DataQuery$43$1;
                ((1)localObject1).<init>(this, localException, localArrayList4, localArrayList1, localArrayList2);
                AndroidUtilities.runOnUIThread((Runnable)localObject1);
              }
            }
          });
          this.loaded = true;
        }
      }
      else
      {
        this.loading = true;
        TLRPC.TL_contacts_getTopPeers localTL_contacts_getTopPeers = new TLRPC.TL_contacts_getTopPeers();
        localTL_contacts_getTopPeers.hash = 0;
        localTL_contacts_getTopPeers.bots_pm = false;
        localTL_contacts_getTopPeers.correspondents = true;
        localTL_contacts_getTopPeers.groups = false;
        localTL_contacts_getTopPeers.channels = false;
        localTL_contacts_getTopPeers.bots_inline = true;
        localTL_contacts_getTopPeers.offset = 0;
        localTL_contacts_getTopPeers.limit = 20;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_contacts_getTopPeers, new RequestDelegate()
        {
          public void run(final TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
          {
            if ((paramAnonymousTLObject instanceof TLRPC.TL_contacts_topPeers)) {
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  final TLRPC.TL_contacts_topPeers localTL_contacts_topPeers = (TLRPC.TL_contacts_topPeers)paramAnonymousTLObject;
                  MessagesController.getInstance(DataQuery.this.currentAccount).putUsers(localTL_contacts_topPeers.users, false);
                  MessagesController.getInstance(DataQuery.this.currentAccount).putChats(localTL_contacts_topPeers.chats, false);
                  int i = 0;
                  while (i < localTL_contacts_topPeers.categories.size())
                  {
                    TLRPC.TL_topPeerCategoryPeers localTL_topPeerCategoryPeers = (TLRPC.TL_topPeerCategoryPeers)localTL_contacts_topPeers.categories.get(i);
                    if ((localTL_topPeerCategoryPeers.category instanceof TLRPC.TL_topPeerCategoryBotsInline))
                    {
                      DataQuery.this.inlineBots = localTL_topPeerCategoryPeers.peers;
                      UserConfig.getInstance(DataQuery.this.currentAccount).botRatingLoadTime = ((int)(System.currentTimeMillis() / 1000L));
                      i++;
                    }
                    else
                    {
                      DataQuery.this.hints = localTL_topPeerCategoryPeers.peers;
                      int j = UserConfig.getInstance(DataQuery.this.currentAccount).getClientUserId();
                      for (int k = 0;; k++) {
                        if (k < DataQuery.this.hints.size())
                        {
                          if (((TLRPC.TL_topPeer)DataQuery.this.hints.get(k)).peer.user_id == j) {
                            DataQuery.this.hints.remove(k);
                          }
                        }
                        else
                        {
                          UserConfig.getInstance(DataQuery.this.currentAccount).ratingLoadTime = ((int)(System.currentTimeMillis() / 1000L));
                          break;
                        }
                      }
                    }
                  }
                  UserConfig.getInstance(DataQuery.this.currentAccount).saveConfig(false);
                  DataQuery.this.buildShortcuts();
                  NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.reloadHints, new Object[0]);
                  NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
                  MessagesStorage.getInstance(DataQuery.this.currentAccount).getStorageQueue().postRunnable(new Runnable()
                  {
                    public void run()
                    {
                      try
                      {
                        MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().executeFast("DELETE FROM chat_hints WHERE 1").stepThis().dispose();
                        MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().beginTransaction();
                        MessagesStorage.getInstance(DataQuery.this.currentAccount).putUsersAndChats(localTL_contacts_topPeers.users, localTL_contacts_topPeers.chats, false, false);
                        SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().executeFast("REPLACE INTO chat_hints VALUES(?, ?, ?, ?)");
                        for (int i = 0; i < localTL_contacts_topPeers.categories.size(); i++)
                        {
                          TLRPC.TL_topPeerCategoryPeers localTL_topPeerCategoryPeers = (TLRPC.TL_topPeerCategoryPeers)localTL_contacts_topPeers.categories.get(i);
                          int j;
                          int k;
                          label159:
                          int m;
                          if ((localTL_topPeerCategoryPeers.category instanceof TLRPC.TL_topPeerCategoryBotsInline))
                          {
                            j = 1;
                            k = 0;
                            if (k >= localTL_topPeerCategoryPeers.peers.size()) {
                              continue;
                            }
                            localObject = (TLRPC.TL_topPeer)localTL_topPeerCategoryPeers.peers.get(k);
                            if (!(((TLRPC.TL_topPeer)localObject).peer instanceof TLRPC.TL_peerUser)) {
                              break label257;
                            }
                            m = ((TLRPC.TL_topPeer)localObject).peer.user_id;
                          }
                          for (;;)
                          {
                            localSQLitePreparedStatement.requery();
                            localSQLitePreparedStatement.bindInteger(1, m);
                            localSQLitePreparedStatement.bindInteger(2, j);
                            localSQLitePreparedStatement.bindDouble(3, ((TLRPC.TL_topPeer)localObject).rating);
                            localSQLitePreparedStatement.bindInteger(4, 0);
                            localSQLitePreparedStatement.step();
                            k++;
                            break label159;
                            j = 0;
                            break;
                            label257:
                            if ((((TLRPC.TL_topPeer)localObject).peer instanceof TLRPC.TL_peerChat)) {
                              m = -((TLRPC.TL_topPeer)localObject).peer.chat_id;
                            } else {
                              m = -((TLRPC.TL_topPeer)localObject).peer.channel_id;
                            }
                          }
                        }
                        localSQLitePreparedStatement.dispose();
                        MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().commitTransaction();
                        Object localObject = new org/telegram/messenger/DataQuery$44$1$1$1;
                        ((1)localObject).<init>(this);
                        AndroidUtilities.runOnUIThread((Runnable)localObject);
                        return;
                      }
                      catch (Exception localException)
                      {
                        for (;;)
                        {
                          FileLog.e(localException);
                        }
                      }
                    }
                  });
                }
              });
            }
          }
        });
      }
    }
  }
  
  public void loadMedia(final long paramLong, final int paramInt1, int paramInt2, final int paramInt3, boolean paramBoolean, final int paramInt4)
  {
    if (((int)paramLong < 0) && (ChatObject.isChannel(-(int)paramLong, this.currentAccount))) {}
    int i;
    for (final boolean bool = true;; bool = false)
    {
      i = (int)paramLong;
      if ((!paramBoolean) && (i != 0)) {
        break;
      }
      loadMediaDatabase(paramLong, paramInt1, paramInt2, paramInt3, paramInt4, bool);
      return;
    }
    TLRPC.TL_messages_search localTL_messages_search = new TLRPC.TL_messages_search();
    localTL_messages_search.limit = (paramInt1 + 1);
    localTL_messages_search.offset_id = paramInt2;
    if (paramInt3 == 0) {
      localTL_messages_search.filter = new TLRPC.TL_inputMessagesFilterPhotoVideo();
    }
    for (;;)
    {
      localTL_messages_search.q = "";
      localTL_messages_search.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(i);
      if (localTL_messages_search.peer == null) {
        break;
      }
      paramInt1 = ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_messages_search, new RequestDelegate()
      {
        public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
        {
          boolean bool;
          if (paramAnonymousTL_error == null)
          {
            paramAnonymousTLObject = (TLRPC.messages_Messages)paramAnonymousTLObject;
            if (paramAnonymousTLObject.messages.size() <= paramInt1) {
              break label77;
            }
            bool = false;
            paramAnonymousTLObject.messages.remove(paramAnonymousTLObject.messages.size() - 1);
          }
          for (;;)
          {
            DataQuery.this.processLoadedMedia(paramAnonymousTLObject, paramLong, paramInt1, paramInt3, paramInt4, false, bool, this.val$isChannel, bool);
            return;
            label77:
            bool = true;
          }
        }
      });
      ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(paramInt1, paramInt4);
      break;
      if (paramInt3 == 1) {
        localTL_messages_search.filter = new TLRPC.TL_inputMessagesFilterDocument();
      } else if (paramInt3 == 2) {
        localTL_messages_search.filter = new TLRPC.TL_inputMessagesFilterVoice();
      } else if (paramInt3 == 3) {
        localTL_messages_search.filter = new TLRPC.TL_inputMessagesFilterUrl();
      } else if (paramInt3 == 4) {
        localTL_messages_search.filter = new TLRPC.TL_inputMessagesFilterMusic();
      }
    }
  }
  
  public void loadMusic(final long paramLong1, long paramLong2)
  {
    MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable()
    {
      public void run()
      {
        final ArrayList localArrayList = new ArrayList();
        try
        {
          if ((int)paramLong1 != 0)
          {
            SQLiteCursor localSQLiteCursor1 = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v2 WHERE uid = %d AND mid < %d AND type = %d ORDER BY date DESC, mid DESC LIMIT 1000", new Object[] { Long.valueOf(paramLong1), Long.valueOf(this.val$max_id), Integer.valueOf(4) }), new Object[0]);
            while (localSQLiteCursor1.next())
            {
              Object localObject = localSQLiteCursor1.byteBufferValue(0);
              if (localObject != null)
              {
                TLRPC.Message localMessage = TLRPC.Message.TLdeserialize((AbstractSerializedData)localObject, ((NativeByteBuffer)localObject).readInt32(false), false);
                localMessage.readAttachPath((AbstractSerializedData)localObject, UserConfig.getInstance(DataQuery.this.currentAccount).clientUserId);
                ((NativeByteBuffer)localObject).reuse();
                if (MessageObject.isMusicMessage(localMessage))
                {
                  localMessage.id = localSQLiteCursor1.intValue(1);
                  localMessage.dialog_id = paramLong1;
                  localObject = new org/telegram/messenger/MessageObject;
                  ((MessageObject)localObject).<init>(DataQuery.this.currentAccount, localMessage, false);
                  localArrayList.add(0, localObject);
                  continue;
                  AndroidUtilities.runOnUIThread(new Runnable()
                  {
                    public void run()
                    {
                      NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.musicDidLoaded, new Object[] { Long.valueOf(DataQuery.41.this.val$uid), localArrayList });
                    }
                  });
                }
              }
            }
          }
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
        }
        for (;;)
        {
          return;
          SQLiteCursor localSQLiteCursor2 = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v2 WHERE uid = %d AND mid > %d AND type = %d ORDER BY date DESC, mid DESC LIMIT 1000", new Object[] { Long.valueOf(paramLong1), Long.valueOf(this.val$max_id), Integer.valueOf(4) }), new Object[0]);
          break;
          localSQLiteCursor2.dispose();
        }
      }
    });
  }
  
  public MessageObject loadPinnedMessage(final int paramInt1, final int paramInt2, boolean paramBoolean)
  {
    if (paramBoolean) {
      MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable()
      {
        public void run()
        {
          DataQuery.this.loadPinnedMessageInternal(paramInt1, paramInt2, false);
        }
      });
    }
    for (MessageObject localMessageObject = null;; localMessageObject = loadPinnedMessageInternal(paramInt1, paramInt2, true)) {
      return localMessageObject;
    }
  }
  
  public void loadRecents(final int paramInt, final boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    if (paramBoolean1) {
      if (!this.loadingRecentGifs) {}
    }
    for (;;)
    {
      return;
      this.loadingRecentGifs = true;
      if (this.recentGifsLoaded) {
        paramBoolean2 = false;
      }
      for (;;)
      {
        if (!paramBoolean2) {
          break label86;
        }
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable()
        {
          public void run()
          {
            int i;
            Object localObject2;
            SQLiteCursor localSQLiteCursor;
            try
            {
              if (paramBoolean1)
              {
                i = 2;
                Object localObject1 = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase();
                localObject2 = new java/lang/StringBuilder;
                ((StringBuilder)localObject2).<init>();
                localSQLiteCursor = ((SQLiteDatabase)localObject1).queryFinalized("SELECT document FROM web_recent_v3 WHERE type = " + i + " ORDER BY date DESC", new Object[0]);
                localObject1 = new java/util/ArrayList;
                ((ArrayList)localObject1).<init>();
                while (localSQLiteCursor.next()) {
                  if (!localSQLiteCursor.isNull(0))
                  {
                    localObject2 = localSQLiteCursor.byteBufferValue(0);
                    if (localObject2 != null)
                    {
                      TLRPC.Document localDocument = TLRPC.Document.TLdeserialize((AbstractSerializedData)localObject2, ((NativeByteBuffer)localObject2).readInt32(false), false);
                      if (localDocument != null) {
                        ((ArrayList)localObject1).add(localDocument);
                      }
                      ((NativeByteBuffer)localObject2).reuse();
                      continue;
                      return;
                    }
                  }
                }
              }
            }
            catch (Throwable localThrowable)
            {
              FileLog.e(localThrowable);
            }
            for (;;)
            {
              if (paramInt == 0)
              {
                i = 3;
                break;
              }
              if (paramInt == 1)
              {
                i = 4;
                break;
              }
              i = 5;
              break;
              localSQLiteCursor.dispose();
              localObject2 = new org/telegram/messenger/DataQuery$8$1;
              ((1)localObject2).<init>(this, localThrowable);
              AndroidUtilities.runOnUIThread((Runnable)localObject2);
            }
          }
        });
        break;
        if (this.loadingRecentStickers[paramInt] != 0) {
          break;
        }
        this.loadingRecentStickers[paramInt] = true;
        if (this.recentStickersLoaded[paramInt] != 0) {
          paramBoolean2 = false;
        }
      }
      label86:
      localObject = MessagesController.getEmojiSettings(this.currentAccount);
      if (!paramBoolean3)
      {
        long l;
        if (paramBoolean1) {
          l = ((SharedPreferences)localObject).getLong("lastGifLoadTime", 0L);
        }
        for (;;)
        {
          if (Math.abs(System.currentTimeMillis() - l) < 3600000L)
          {
            if (paramBoolean1)
            {
              this.loadingRecentGifs = false;
              break;
              if (paramInt == 0)
              {
                l = ((SharedPreferences)localObject).getLong("lastStickersLoadTime", 0L);
                continue;
              }
              if (paramInt == 1)
              {
                l = ((SharedPreferences)localObject).getLong("lastStickersLoadTimeMask", 0L);
                continue;
              }
              l = ((SharedPreferences)localObject).getLong("lastStickersLoadTimeFavs", 0L);
              continue;
            }
            this.loadingRecentStickers[paramInt] = false;
            break;
          }
        }
      }
      if (paramBoolean1)
      {
        localObject = new TLRPC.TL_messages_getSavedGifs();
        ((TLRPC.TL_messages_getSavedGifs)localObject).hash = calcDocumentsHash(this.recentGifs);
        ConnectionsManager.getInstance(this.currentAccount).sendRequest((TLObject)localObject, new RequestDelegate()
        {
          public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
          {
            paramAnonymousTL_error = null;
            if ((paramAnonymousTLObject instanceof TLRPC.TL_messages_savedGifs)) {
              paramAnonymousTL_error = ((TLRPC.TL_messages_savedGifs)paramAnonymousTLObject).gifs;
            }
            DataQuery.this.processLoadedRecentDocuments(paramInt, paramAnonymousTL_error, paramBoolean1, 0);
          }
        });
      }
      else
      {
        if (paramInt != 2) {
          break;
        }
        localObject = new TLRPC.TL_messages_getFavedStickers();
        ((TLRPC.TL_messages_getFavedStickers)localObject).hash = calcDocumentsHash(this.recentStickers[paramInt]);
        ConnectionsManager.getInstance(this.currentAccount).sendRequest((TLObject)localObject, new RequestDelegate()
        {
          public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
          {
            paramAnonymousTL_error = null;
            if (paramInt == 2) {
              if ((paramAnonymousTLObject instanceof TLRPC.TL_messages_favedStickers)) {
                paramAnonymousTL_error = ((TLRPC.TL_messages_favedStickers)paramAnonymousTLObject).stickers;
              }
            }
            for (;;)
            {
              DataQuery.this.processLoadedRecentDocuments(paramInt, paramAnonymousTL_error, paramBoolean1, 0);
              return;
              if ((paramAnonymousTLObject instanceof TLRPC.TL_messages_recentStickers)) {
                paramAnonymousTL_error = ((TLRPC.TL_messages_recentStickers)paramAnonymousTLObject).stickers;
              }
            }
          }
        });
      }
    }
    Object localObject = new TLRPC.TL_messages_getRecentStickers();
    ((TLRPC.TL_messages_getRecentStickers)localObject).hash = calcDocumentsHash(this.recentStickers[paramInt]);
    if (paramInt == 1) {}
    for (paramBoolean2 = true;; paramBoolean2 = false)
    {
      ((TLRPC.TL_messages_getRecentStickers)localObject).attached = paramBoolean2;
      break;
    }
  }
  
  public void loadReplyMessagesForMessages(ArrayList<MessageObject> paramArrayList, final long paramLong)
  {
    final Object localObject1;
    final Object localObject2;
    final int i;
    final Object localObject3;
    long l1;
    ArrayList localArrayList1;
    ArrayList localArrayList2;
    if ((int)paramLong == 0)
    {
      localObject1 = new ArrayList();
      localObject2 = new LongSparseArray();
      for (i = 0; i < paramArrayList.size(); i++)
      {
        localObject3 = (MessageObject)paramArrayList.get(i);
        if ((((MessageObject)localObject3).isReply()) && (((MessageObject)localObject3).replyMessageObject == null))
        {
          l1 = ((MessageObject)localObject3).messageOwner.reply_to_random_id;
          localArrayList1 = (ArrayList)((LongSparseArray)localObject2).get(l1);
          localArrayList2 = localArrayList1;
          if (localArrayList1 == null)
          {
            localArrayList2 = new ArrayList();
            ((LongSparseArray)localObject2).put(l1, localArrayList2);
          }
          localArrayList2.add(localObject3);
          if (!((ArrayList)localObject1).contains(Long.valueOf(l1))) {
            ((ArrayList)localObject1).add(Long.valueOf(l1));
          }
        }
      }
      if (!((ArrayList)localObject1).isEmpty()) {}
    }
    for (;;)
    {
      return;
      MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable()
      {
        public void run()
        {
          try
          {
            Object localObject1 = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT m.data, m.mid, m.date, r.random_id FROM randoms as r INNER JOIN messages as m ON r.mid = m.mid WHERE r.random_id IN(%s)", new Object[] { TextUtils.join(",", localObject1) }), new Object[0]);
            int i;
            while (((SQLiteCursor)localObject1).next())
            {
              Object localObject2 = ((SQLiteCursor)localObject1).byteBufferValue(0);
              if (localObject2 != null)
              {
                Object localObject3 = TLRPC.Message.TLdeserialize((AbstractSerializedData)localObject2, ((NativeByteBuffer)localObject2).readInt32(false), false);
                ((TLRPC.Message)localObject3).readAttachPath((AbstractSerializedData)localObject2, UserConfig.getInstance(DataQuery.this.currentAccount).clientUserId);
                ((NativeByteBuffer)localObject2).reuse();
                ((TLRPC.Message)localObject3).id = ((SQLiteCursor)localObject1).intValue(1);
                ((TLRPC.Message)localObject3).date = ((SQLiteCursor)localObject1).intValue(2);
                ((TLRPC.Message)localObject3).dialog_id = paramLong;
                long l = ((SQLiteCursor)localObject1).longValue(3);
                localObject2 = (ArrayList)this.val$replyMessageRandomOwners.get(l);
                this.val$replyMessageRandomOwners.remove(l);
                if (localObject2 != null)
                {
                  MessageObject localMessageObject = new org/telegram/messenger/MessageObject;
                  localMessageObject.<init>(DataQuery.this.currentAccount, (TLRPC.Message)localObject3, false);
                  for (i = 0; i < ((ArrayList)localObject2).size(); i++)
                  {
                    localObject3 = (MessageObject)((ArrayList)localObject2).get(i);
                    ((MessageObject)localObject3).replyMessageObject = localMessageObject;
                    ((MessageObject)localObject3).messageOwner.reply_to_msg_id = localMessageObject.getId();
                    if (((MessageObject)localObject3).isMegagroup())
                    {
                      localObject3 = ((MessageObject)localObject3).replyMessageObject.messageOwner;
                      ((TLRPC.Message)localObject3).flags |= 0x80000000;
                    }
                  }
                }
              }
            }
            ((SQLiteCursor)localObject1).dispose();
            if (this.val$replyMessageRandomOwners.size() != 0) {
              for (i = 0; i < this.val$replyMessageRandomOwners.size(); i++)
              {
                localObject1 = (ArrayList)this.val$replyMessageRandomOwners.valueAt(i);
                for (int j = 0; j < ((ArrayList)localObject1).size(); j++) {
                  ((MessageObject)((ArrayList)localObject1).get(j)).messageOwner.reply_to_random_id = 0L;
                }
              }
            }
            localObject1 = new org/telegram/messenger/DataQuery$56$1;
            ((1)localObject1).<init>(this);
            AndroidUtilities.runOnUIThread((Runnable)localObject1);
            return;
          }
          catch (Exception localException)
          {
            for (;;)
            {
              FileLog.e(localException);
            }
          }
        }
      });
      continue;
      ArrayList localArrayList3 = new ArrayList();
      localObject3 = new SparseArray();
      localObject2 = new StringBuilder();
      i = 0;
      int j = 0;
      while (j < paramArrayList.size())
      {
        localObject1 = (MessageObject)paramArrayList.get(j);
        int k = i;
        if (((MessageObject)localObject1).getId() > 0)
        {
          k = i;
          if (((MessageObject)localObject1).isReply())
          {
            k = i;
            if (((MessageObject)localObject1).replyMessageObject == null)
            {
              int m = ((MessageObject)localObject1).messageOwner.reply_to_msg_id;
              long l2 = m;
              l1 = l2;
              if (((MessageObject)localObject1).messageOwner.to_id.channel_id != 0)
              {
                l1 = l2 | ((MessageObject)localObject1).messageOwner.to_id.channel_id << 32;
                i = ((MessageObject)localObject1).messageOwner.to_id.channel_id;
              }
              if (((StringBuilder)localObject2).length() > 0) {
                ((StringBuilder)localObject2).append(',');
              }
              ((StringBuilder)localObject2).append(l1);
              localArrayList1 = (ArrayList)((SparseArray)localObject3).get(m);
              localArrayList2 = localArrayList1;
              if (localArrayList1 == null)
              {
                localArrayList2 = new ArrayList();
                ((SparseArray)localObject3).put(m, localArrayList2);
              }
              localArrayList2.add(localObject1);
              k = i;
              if (!localArrayList3.contains(Integer.valueOf(m)))
              {
                localArrayList3.add(Integer.valueOf(m));
                k = i;
              }
            }
          }
        }
        j++;
        i = k;
      }
      if (!localArrayList3.isEmpty()) {
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable()
        {
          public void run()
          {
            ArrayList localArrayList2;
            ArrayList localArrayList3;
            ArrayList localArrayList4;
            Object localObject2;
            SQLiteCursor localSQLiteCursor;
            Object localObject3;
            try
            {
              ArrayList localArrayList1 = new java/util/ArrayList;
              localArrayList1.<init>();
              localArrayList2 = new java/util/ArrayList;
              localArrayList2.<init>();
              localArrayList3 = new java/util/ArrayList;
              localArrayList3.<init>();
              localArrayList4 = new java/util/ArrayList;
              localArrayList4.<init>();
              localObject2 = new java/util/ArrayList;
              ((ArrayList)localObject2).<init>();
              localSQLiteCursor = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, mid, date FROM messages WHERE mid IN(%s)", new Object[] { localObject2.toString() }), new Object[0]);
              while (localSQLiteCursor.next())
              {
                NativeByteBuffer localNativeByteBuffer = localSQLiteCursor.byteBufferValue(0);
                if (localNativeByteBuffer != null)
                {
                  localObject3 = TLRPC.Message.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
                  ((TLRPC.Message)localObject3).readAttachPath(localNativeByteBuffer, UserConfig.getInstance(DataQuery.this.currentAccount).clientUserId);
                  localNativeByteBuffer.reuse();
                  ((TLRPC.Message)localObject3).id = localSQLiteCursor.intValue(1);
                  ((TLRPC.Message)localObject3).date = localSQLiteCursor.intValue(2);
                  ((TLRPC.Message)localObject3).dialog_id = paramLong;
                  MessagesStorage.addUsersAndChatsFromMessage((TLRPC.Message)localObject3, localArrayList4, (ArrayList)localObject2);
                  localArrayList1.add(localObject3);
                  localObject3.remove(Integer.valueOf(((TLRPC.Message)localObject3).id));
                  continue;
                  return;
                }
              }
            }
            catch (Exception localException)
            {
              FileLog.e(localException);
            }
            for (;;)
            {
              localSQLiteCursor.dispose();
              if (!localArrayList4.isEmpty()) {
                MessagesStorage.getInstance(DataQuery.this.currentAccount).getUsersInternal(TextUtils.join(",", localArrayList4), localArrayList2);
              }
              if (!((ArrayList)localObject2).isEmpty()) {
                MessagesStorage.getInstance(DataQuery.this.currentAccount).getChatsInternal(TextUtils.join(",", (Iterable)localObject2), localArrayList3);
              }
              DataQuery.this.broadcastReplyMessages(localException, i, localArrayList2, localArrayList3, paramLong, true);
              if (!localObject3.isEmpty())
              {
                Object localObject1;
                if (this.val$channelIdFinal != 0)
                {
                  localObject1 = new org/telegram/tgnet/TLRPC$TL_channels_getMessages;
                  ((TLRPC.TL_channels_getMessages)localObject1).<init>();
                  ((TLRPC.TL_channels_getMessages)localObject1).channel = MessagesController.getInstance(DataQuery.this.currentAccount).getInputChannel(this.val$channelIdFinal);
                  ((TLRPC.TL_channels_getMessages)localObject1).id = localObject3;
                  localObject2 = ConnectionsManager.getInstance(DataQuery.this.currentAccount);
                  localObject3 = new org/telegram/messenger/DataQuery$57$1;
                  ((1)localObject3).<init>(this);
                  ((ConnectionsManager)localObject2).sendRequest((TLObject)localObject1, (RequestDelegate)localObject3);
                }
                else
                {
                  localObject2 = new org/telegram/tgnet/TLRPC$TL_messages_getMessages;
                  ((TLRPC.TL_messages_getMessages)localObject2).<init>();
                  ((TLRPC.TL_messages_getMessages)localObject2).id = localObject3;
                  localObject1 = ConnectionsManager.getInstance(DataQuery.this.currentAccount);
                  localObject3 = new org/telegram/messenger/DataQuery$57$2;
                  ((2)localObject3).<init>(this);
                  ((ConnectionsManager)localObject1).sendRequest((TLObject)localObject2, (RequestDelegate)localObject3);
                }
              }
            }
          }
        });
      }
    }
  }
  
  public void loadStickers(final int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    int i = 0;
    final int j = 0;
    if (this.loadingStickers[paramInt] != 0) {}
    for (;;)
    {
      return;
      if (paramInt == 3)
      {
        if ((this.featuredStickerSets.isEmpty()) || (!MessagesController.getInstance(this.currentAccount).preloadFeaturedStickers)) {}
      }
      else
      {
        for (;;)
        {
          this.loadingStickers[paramInt] = true;
          if (!paramBoolean1) {
            break label89;
          }
          MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable()
          {
            /* Error */
            public void run()
            {
              // Byte code:
              //   0: aconst_null
              //   1: astore_1
              //   2: aconst_null
              //   3: astore_2
              //   4: aconst_null
              //   5: astore_3
              //   6: iconst_0
              //   7: istore 4
              //   9: iconst_0
              //   10: istore 5
              //   12: iconst_0
              //   13: istore 6
              //   15: iconst_0
              //   16: istore 7
              //   18: iconst_0
              //   19: istore 8
              //   21: aconst_null
              //   22: astore 9
              //   24: aconst_null
              //   25: astore 10
              //   27: aload_2
              //   28: astore 11
              //   30: iload 4
              //   32: istore 12
              //   34: aload 10
              //   36: astore 13
              //   38: aload 9
              //   40: astore 14
              //   42: aload_0
              //   43: getfield 19	org/telegram/messenger/DataQuery$24:this$0	Lorg/telegram/messenger/DataQuery;
              //   46: invokestatic 32	org/telegram/messenger/DataQuery:access$000	(Lorg/telegram/messenger/DataQuery;)I
              //   49: invokestatic 38	org/telegram/messenger/MessagesStorage:getInstance	(I)Lorg/telegram/messenger/MessagesStorage;
              //   52: invokevirtual 42	org/telegram/messenger/MessagesStorage:getDatabase	()Lorg/telegram/SQLite/SQLiteDatabase;
              //   55: astore 15
              //   57: aload_2
              //   58: astore 11
              //   60: iload 4
              //   62: istore 12
              //   64: aload 10
              //   66: astore 13
              //   68: aload 9
              //   70: astore 14
              //   72: new 44	java/lang/StringBuilder
              //   75: astore 16
              //   77: aload_2
              //   78: astore 11
              //   80: iload 4
              //   82: istore 12
              //   84: aload 10
              //   86: astore 13
              //   88: aload 9
              //   90: astore 14
              //   92: aload 16
              //   94: invokespecial 45	java/lang/StringBuilder:<init>	()V
              //   97: aload_2
              //   98: astore 11
              //   100: iload 4
              //   102: istore 12
              //   104: aload 10
              //   106: astore 13
              //   108: aload 9
              //   110: astore 14
              //   112: aload 15
              //   114: aload 16
              //   116: ldc 47
              //   118: invokevirtual 51	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
              //   121: aload_0
              //   122: getfield 21	org/telegram/messenger/DataQuery$24:val$type	I
              //   125: iconst_1
              //   126: iadd
              //   127: invokevirtual 54	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
              //   130: invokevirtual 58	java/lang/StringBuilder:toString	()Ljava/lang/String;
              //   133: iconst_0
              //   134: anewarray 4	java/lang/Object
              //   137: invokevirtual 64	org/telegram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/telegram/SQLite/SQLiteCursor;
              //   140: astore 10
              //   142: aload_2
              //   143: astore 11
              //   145: iload 4
              //   147: istore 12
              //   149: aload 10
              //   151: astore 13
              //   153: aload 10
              //   155: astore 14
              //   157: aload 10
              //   159: invokevirtual 70	org/telegram/SQLite/SQLiteCursor:next	()Z
              //   162: ifeq +161 -> 323
              //   165: aload_2
              //   166: astore 11
              //   168: iload 4
              //   170: istore 12
              //   172: aload 10
              //   174: astore 13
              //   176: aload 10
              //   178: astore 14
              //   180: aload 10
              //   182: iconst_0
              //   183: invokevirtual 74	org/telegram/SQLite/SQLiteCursor:byteBufferValue	(I)Lorg/telegram/tgnet/NativeByteBuffer;
              //   186: astore 9
              //   188: aload_3
              //   189: astore_1
              //   190: aload 9
              //   192: ifnull +87 -> 279
              //   195: aload_2
              //   196: astore 11
              //   198: iload 4
              //   200: istore 12
              //   202: aload 10
              //   204: astore 13
              //   206: aload 10
              //   208: astore 14
              //   210: new 76	java/util/ArrayList
              //   213: astore_1
              //   214: aload_2
              //   215: astore 11
              //   217: iload 4
              //   219: istore 12
              //   221: aload 10
              //   223: astore 13
              //   225: aload 10
              //   227: astore 14
              //   229: aload_1
              //   230: invokespecial 77	java/util/ArrayList:<init>	()V
              //   233: aload 9
              //   235: iconst_0
              //   236: invokevirtual 83	org/telegram/tgnet/NativeByteBuffer:readInt32	(Z)I
              //   239: istore 8
              //   241: iconst_0
              //   242: istore 12
              //   244: iload 12
              //   246: iload 8
              //   248: if_icmpge +26 -> 274
              //   251: aload_1
              //   252: aload 9
              //   254: aload 9
              //   256: iconst_0
              //   257: invokevirtual 83	org/telegram/tgnet/NativeByteBuffer:readInt32	(Z)I
              //   260: iconst_0
              //   261: invokestatic 89	org/telegram/tgnet/TLRPC$TL_messages_stickerSet:TLdeserialize	(Lorg/telegram/tgnet/AbstractSerializedData;IZ)Lorg/telegram/tgnet/TLRPC$TL_messages_stickerSet;
              //   264: invokevirtual 93	java/util/ArrayList:add	(Ljava/lang/Object;)Z
              //   267: pop
              //   268: iinc 12 1
              //   271: goto -27 -> 244
              //   274: aload 9
              //   276: invokevirtual 96	org/telegram/tgnet/NativeByteBuffer:reuse	()V
              //   279: aload_1
              //   280: astore 11
              //   282: iload 4
              //   284: istore 12
              //   286: aload 10
              //   288: astore 13
              //   290: aload 10
              //   292: astore 14
              //   294: aload 10
              //   296: iconst_1
              //   297: invokevirtual 100	org/telegram/SQLite/SQLiteCursor:intValue	(I)I
              //   300: istore 6
              //   302: aload_1
              //   303: astore 11
              //   305: iload 6
              //   307: istore 12
              //   309: aload 10
              //   311: astore 13
              //   313: aload 10
              //   315: astore 14
              //   317: aload_1
              //   318: invokestatic 104	org/telegram/messenger/DataQuery:access$2300	(Ljava/util/ArrayList;)I
              //   321: istore 8
              //   323: aload_1
              //   324: astore 14
              //   326: iload 6
              //   328: istore 4
              //   330: iload 8
              //   332: istore 5
              //   334: aload 10
              //   336: ifnull +19 -> 355
              //   339: aload 10
              //   341: invokevirtual 107	org/telegram/SQLite/SQLiteCursor:dispose	()V
              //   344: iload 8
              //   346: istore 5
              //   348: iload 6
              //   350: istore 4
              //   352: aload_1
              //   353: astore 14
              //   355: aload_0
              //   356: getfield 19	org/telegram/messenger/DataQuery$24:this$0	Lorg/telegram/messenger/DataQuery;
              //   359: aload_0
              //   360: getfield 21	org/telegram/messenger/DataQuery$24:val$type	I
              //   363: aload 14
              //   365: iconst_1
              //   366: iload 4
              //   368: iload 5
              //   370: invokestatic 111	org/telegram/messenger/DataQuery:access$2200	(Lorg/telegram/messenger/DataQuery;ILjava/util/ArrayList;ZII)V
              //   373: return
              //   374: astore_1
              //   375: aload 13
              //   377: astore 10
              //   379: aload 10
              //   381: astore 14
              //   383: aload_1
              //   384: invokestatic 117	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
              //   387: aload 11
              //   389: astore 14
              //   391: iload 12
              //   393: istore 4
              //   395: iload 7
              //   397: istore 5
              //   399: aload 10
              //   401: ifnull -46 -> 355
              //   404: aload 10
              //   406: invokevirtual 107	org/telegram/SQLite/SQLiteCursor:dispose	()V
              //   409: aload 11
              //   411: astore 14
              //   413: iload 12
              //   415: istore 4
              //   417: iload 7
              //   419: istore 5
              //   421: goto -66 -> 355
              //   424: astore 10
              //   426: aload 10
              //   428: astore_1
              //   429: aload 14
              //   431: ifnull +8 -> 439
              //   434: aload 14
              //   436: invokevirtual 107	org/telegram/SQLite/SQLiteCursor:dispose	()V
              //   439: aload_1
              //   440: athrow
              //   441: astore_1
              //   442: aload 10
              //   444: astore 14
              //   446: goto -17 -> 429
              //   449: astore 14
              //   451: aload_1
              //   452: astore 11
              //   454: iload 5
              //   456: istore 12
              //   458: aload 14
              //   460: astore_1
              //   461: goto -82 -> 379
              // Local variable table:
              //   start	length	slot	name	signature
              //   0	464	0	this	24
              //   1	352	1	localObject1	Object
              //   374	10	1	localThrowable1	Throwable
              //   428	12	1	localObject2	Object
              //   441	11	1	localObject3	Object
              //   460	1	1	localObject4	Object
              //   3	212	2	localObject5	Object
              //   5	184	3	localObject6	Object
              //   7	409	4	i	int
              //   10	445	5	j	int
              //   13	336	6	k	int
              //   16	402	7	m	int
              //   19	326	8	n	int
              //   22	253	9	localNativeByteBuffer	NativeByteBuffer
              //   25	380	10	localObject7	Object
              //   424	19	10	localObject8	Object
              //   28	425	11	localObject9	Object
              //   32	425	12	i1	int
              //   36	340	13	localObject10	Object
              //   40	405	14	localObject11	Object
              //   449	10	14	localThrowable2	Throwable
              //   55	58	15	localSQLiteDatabase	SQLiteDatabase
              //   75	40	16	localStringBuilder	StringBuilder
              // Exception table:
              //   from	to	target	type
              //   42	57	374	java/lang/Throwable
              //   72	77	374	java/lang/Throwable
              //   92	97	374	java/lang/Throwable
              //   112	142	374	java/lang/Throwable
              //   157	165	374	java/lang/Throwable
              //   180	188	374	java/lang/Throwable
              //   210	214	374	java/lang/Throwable
              //   229	233	374	java/lang/Throwable
              //   294	302	374	java/lang/Throwable
              //   317	323	374	java/lang/Throwable
              //   42	57	424	finally
              //   72	77	424	finally
              //   92	97	424	finally
              //   112	142	424	finally
              //   157	165	424	finally
              //   180	188	424	finally
              //   210	214	424	finally
              //   229	233	424	finally
              //   294	302	424	finally
              //   317	323	424	finally
              //   383	387	424	finally
              //   233	241	441	finally
              //   251	268	441	finally
              //   274	279	441	finally
              //   233	241	449	java/lang/Throwable
              //   251	268	449	java/lang/Throwable
              //   274	279	449	java/lang/Throwable
            }
          });
          break;
          loadArchivedStickersCount(paramInt, paramBoolean1);
        }
        label89:
        if (paramInt != 3) {
          break;
        }
        localObject1 = new TLRPC.TL_messages_allStickers();
        ((TLRPC.TL_messages_allStickers)localObject1).hash = this.loadFeaturedHash;
        j = 0;
        i = this.featuredStickerSets.size();
        while (j < i)
        {
          ((TLRPC.TL_messages_allStickers)localObject1).sets.add(((TLRPC.StickerSetCovered)this.featuredStickerSets.get(j)).set);
          j++;
        }
        processLoadStickersResponse(paramInt, (TLRPC.TL_messages_allStickers)localObject1);
      }
    }
    if (paramInt == 0)
    {
      localObject1 = new TLRPC.TL_messages_getAllStickers();
      localObject2 = (TLRPC.TL_messages_getAllStickers)localObject1;
      if (paramBoolean2) {}
      for (;;)
      {
        ((TLRPC.TL_messages_getAllStickers)localObject2).hash = j;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest((TLObject)localObject1, new RequestDelegate()
        {
          public void run(final TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
          {
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                if ((paramAnonymousTLObject instanceof TLRPC.TL_messages_allStickers)) {
                  DataQuery.this.processLoadStickersResponse(DataQuery.25.this.val$type, (TLRPC.TL_messages_allStickers)paramAnonymousTLObject);
                }
                for (;;)
                {
                  return;
                  DataQuery.this.processLoadedStickers(DataQuery.25.this.val$type, null, false, (int)(System.currentTimeMillis() / 1000L), DataQuery.25.this.val$hash);
                }
              }
            });
          }
        });
        break;
        j = this.loadHash[paramInt];
      }
    }
    Object localObject1 = new TLRPC.TL_messages_getMaskStickers();
    Object localObject2 = (TLRPC.TL_messages_getMaskStickers)localObject1;
    if (paramBoolean2) {}
    for (j = i;; j = this.loadHash[paramInt])
    {
      ((TLRPC.TL_messages_getMaskStickers)localObject2).hash = j;
      break;
    }
  }
  
  public void markFaturedStickersAsRead(boolean paramBoolean)
  {
    if (this.unreadStickerSets.isEmpty()) {}
    for (;;)
    {
      return;
      this.unreadStickerSets.clear();
      this.loadFeaturedHash = calcFeaturedStickersHash(this.featuredStickerSets);
      NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.featuredStickersDidLoaded, new Object[0]);
      putFeaturedStickersToCache(this.featuredStickerSets, this.unreadStickerSets, this.loadFeaturedDate, this.loadFeaturedHash);
      if (paramBoolean)
      {
        TLRPC.TL_messages_readFeaturedStickers localTL_messages_readFeaturedStickers = new TLRPC.TL_messages_readFeaturedStickers();
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_messages_readFeaturedStickers, new RequestDelegate()
        {
          public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error) {}
        });
      }
    }
  }
  
  public void markFaturedStickersByIdAsRead(final long paramLong)
  {
    if ((!this.unreadStickerSets.contains(Long.valueOf(paramLong))) || (this.readingStickerSets.contains(Long.valueOf(paramLong)))) {}
    for (;;)
    {
      return;
      this.readingStickerSets.add(Long.valueOf(paramLong));
      TLRPC.TL_messages_readFeaturedStickers localTL_messages_readFeaturedStickers = new TLRPC.TL_messages_readFeaturedStickers();
      localTL_messages_readFeaturedStickers.id.add(Long.valueOf(paramLong));
      ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_messages_readFeaturedStickers, new RequestDelegate()
      {
        public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error) {}
      });
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          DataQuery.this.unreadStickerSets.remove(Long.valueOf(paramLong));
          DataQuery.this.readingStickerSets.remove(Long.valueOf(paramLong));
          DataQuery.access$1402(DataQuery.this, DataQuery.this.calcFeaturedStickersHash(DataQuery.this.featuredStickerSets));
          NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.featuredStickersDidLoaded, new Object[0]);
          DataQuery.this.putFeaturedStickersToCache(DataQuery.this.featuredStickerSets, DataQuery.this.unreadStickerSets, DataQuery.this.loadFeaturedDate, DataQuery.this.loadFeaturedHash);
        }
      }, 1000L);
    }
  }
  
  public void putBotInfo(final TLRPC.BotInfo paramBotInfo)
  {
    if (paramBotInfo == null) {}
    for (;;)
    {
      return;
      this.botInfos.put(paramBotInfo.user_id, paramBotInfo);
      MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable()
      {
        public void run()
        {
          try
          {
            SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().executeFast("REPLACE INTO bot_info(uid, info) VALUES(?, ?)");
            localSQLitePreparedStatement.requery();
            NativeByteBuffer localNativeByteBuffer = new org/telegram/tgnet/NativeByteBuffer;
            localNativeByteBuffer.<init>(paramBotInfo.getObjectSize());
            paramBotInfo.serializeToStream(localNativeByteBuffer);
            localSQLitePreparedStatement.bindInteger(1, paramBotInfo.user_id);
            localSQLitePreparedStatement.bindByteBuffer(2, localNativeByteBuffer);
            localSQLitePreparedStatement.step();
            localNativeByteBuffer.reuse();
            localSQLitePreparedStatement.dispose();
            return;
          }
          catch (Exception localException)
          {
            for (;;)
            {
              FileLog.e(localException);
            }
          }
        }
      });
    }
  }
  
  public void putBotKeyboard(long paramLong, TLRPC.Message paramMessage)
  {
    if (paramMessage == null) {}
    for (;;)
    {
      return;
      int i = 0;
      try
      {
        Object localObject = MessagesStorage.getInstance(this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT mid FROM bot_keyboard WHERE uid = %d", new Object[] { Long.valueOf(paramLong) }), new Object[0]);
        if (((SQLiteCursor)localObject).next()) {
          i = ((SQLiteCursor)localObject).intValue(0);
        }
        ((SQLiteCursor)localObject).dispose();
        if (i < paramMessage.id)
        {
          localObject = MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("REPLACE INTO bot_keyboard VALUES(?, ?, ?)");
          ((SQLitePreparedStatement)localObject).requery();
          NativeByteBuffer localNativeByteBuffer = new org/telegram/tgnet/NativeByteBuffer;
          localNativeByteBuffer.<init>(paramMessage.getObjectSize());
          paramMessage.serializeToStream(localNativeByteBuffer);
          ((SQLitePreparedStatement)localObject).bindLong(1, paramLong);
          ((SQLitePreparedStatement)localObject).bindInteger(2, paramMessage.id);
          ((SQLitePreparedStatement)localObject).bindByteBuffer(3, localNativeByteBuffer);
          ((SQLitePreparedStatement)localObject).step();
          localNativeByteBuffer.reuse();
          ((SQLitePreparedStatement)localObject).dispose();
          localObject = new org/telegram/messenger/DataQuery$67;
          ((67)localObject).<init>(this, paramLong, paramMessage);
          AndroidUtilities.runOnUIThread((Runnable)localObject);
        }
      }
      catch (Exception paramMessage)
      {
        FileLog.e(paramMessage);
      }
    }
  }
  
  public void putGroupStickerSet(TLRPC.TL_messages_stickerSet paramTL_messages_stickerSet)
  {
    this.groupStickerSets.put(paramTL_messages_stickerSet.set.id, paramTL_messages_stickerSet);
  }
  
  public void removeInline(int paramInt)
  {
    for (int i = 0;; i++) {
      if (i < this.inlineBots.size())
      {
        if (((TLRPC.TL_topPeer)this.inlineBots.get(i)).peer.user_id == paramInt)
        {
          this.inlineBots.remove(i);
          TLRPC.TL_contacts_resetTopPeerRating localTL_contacts_resetTopPeerRating = new TLRPC.TL_contacts_resetTopPeerRating();
          localTL_contacts_resetTopPeerRating.category = new TLRPC.TL_topPeerCategoryBotsInline();
          localTL_contacts_resetTopPeerRating.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(paramInt);
          ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_contacts_resetTopPeerRating, new RequestDelegate()
          {
            public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error) {}
          });
          deletePeer(paramInt, 1);
          NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
        }
      }
      else {
        return;
      }
    }
  }
  
  public void removePeer(int paramInt)
  {
    for (int i = 0;; i++) {
      if (i < this.hints.size())
      {
        if (((TLRPC.TL_topPeer)this.hints.get(i)).peer.user_id == paramInt)
        {
          this.hints.remove(i);
          NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.reloadHints, new Object[0]);
          TLRPC.TL_contacts_resetTopPeerRating localTL_contacts_resetTopPeerRating = new TLRPC.TL_contacts_resetTopPeerRating();
          localTL_contacts_resetTopPeerRating.category = new TLRPC.TL_topPeerCategoryCorrespondents();
          localTL_contacts_resetTopPeerRating.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(paramInt);
          deletePeer(paramInt, 0);
          ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_contacts_resetTopPeerRating, new RequestDelegate()
          {
            public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error) {}
          });
        }
      }
      else {
        return;
      }
    }
  }
  
  public void removeRecentGif(final TLRPC.Document paramDocument)
  {
    this.recentGifs.remove(paramDocument);
    TLRPC.TL_messages_saveGif localTL_messages_saveGif = new TLRPC.TL_messages_saveGif();
    localTL_messages_saveGif.id = new TLRPC.TL_inputDocument();
    localTL_messages_saveGif.id.id = paramDocument.id;
    localTL_messages_saveGif.id.access_hash = paramDocument.access_hash;
    localTL_messages_saveGif.unsave = true;
    ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_messages_saveGif, new RequestDelegate()
    {
      public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error) {}
    });
    MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable()
    {
      public void run()
      {
        try
        {
          SQLiteDatabase localSQLiteDatabase = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase();
          StringBuilder localStringBuilder = new java/lang/StringBuilder;
          localStringBuilder.<init>();
          localSQLiteDatabase.executeFast("DELETE FROM web_recent_v3 WHERE id = '" + paramDocument.id + "' AND type = 2").stepThis().dispose();
          return;
        }
        catch (Exception localException)
        {
          for (;;)
          {
            FileLog.e(localException);
          }
        }
      }
    });
  }
  
  public void removeStickersSet(final Context paramContext, final TLRPC.StickerSet paramStickerSet, final int paramInt, final BaseFragment paramBaseFragment, final boolean paramBoolean)
  {
    final int i;
    TLRPC.TL_inputStickerSetID localTL_inputStickerSetID;
    boolean bool;
    label49:
    int j;
    if (paramStickerSet.masks)
    {
      i = 1;
      localTL_inputStickerSetID = new TLRPC.TL_inputStickerSetID();
      localTL_inputStickerSetID.access_hash = paramStickerSet.access_hash;
      localTL_inputStickerSetID.id = paramStickerSet.id;
      if (paramInt == 0) {
        break label329;
      }
      if (paramInt != 1) {
        break label265;
      }
      bool = true;
      paramStickerSet.archived = bool;
      j = 0;
      label58:
      if (j < this.stickerSets[i].size())
      {
        paramContext = (TLRPC.TL_messages_stickerSet)this.stickerSets[i].get(j);
        if (paramContext.set.id != paramStickerSet.id) {
          break label317;
        }
        this.stickerSets[i].remove(j);
        if (paramInt != 2) {
          break label271;
        }
        this.stickerSets[i].add(0, paramContext);
      }
      label134:
      this.loadHash[i] = calcStickersHash(this.stickerSets[i]);
      putStickersToCache(i, this.stickerSets[i], this.loadDate[i], this.loadHash[i]);
      NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.stickersDidLoaded, new Object[] { Integer.valueOf(i) });
      paramContext = new TLRPC.TL_messages_installStickerSet();
      paramContext.stickerset = localTL_inputStickerSetID;
      if (paramInt != 1) {
        break label323;
      }
      bool = true;
      label225:
      paramContext.archived = bool;
      ConnectionsManager.getInstance(this.currentAccount).sendRequest(paramContext, new RequestDelegate()
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
                NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.needReloadArchivedStickers, new Object[] { Integer.valueOf(DataQuery.29.this.val$type) });
                if ((DataQuery.29.this.val$hide != 1) && (DataQuery.29.this.val$baseFragment != null) && (DataQuery.29.this.val$baseFragment.getParentActivity() != null))
                {
                  localActivity = DataQuery.29.this.val$baseFragment.getParentActivity();
                  if (!DataQuery.29.this.val$showSettings) {
                    break label145;
                  }
                }
              }
              label145:
              for (Object localObject = DataQuery.29.this.val$baseFragment;; localObject = null)
              {
                localObject = new StickersArchiveAlert(localActivity, (BaseFragment)localObject, ((TLRPC.TL_messages_stickerSetInstallResultArchive)paramAnonymousTLObject).sets);
                DataQuery.29.this.val$baseFragment.showDialog(((StickersArchiveAlert)localObject).create());
                return;
              }
            }
          });
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              DataQuery.this.loadStickers(DataQuery.29.this.val$type, false, false);
            }
          }, 1000L);
        }
      });
    }
    for (;;)
    {
      return;
      i = 0;
      break;
      label265:
      bool = false;
      break label49;
      label271:
      this.stickerSetsById.remove(paramContext.set.id);
      this.installedStickerSetsById.remove(paramContext.set.id);
      this.stickerSetsByName.remove(paramContext.set.short_name);
      break label134;
      label317:
      j++;
      break label58;
      label323:
      bool = false;
      break label225;
      label329:
      paramBaseFragment = new TLRPC.TL_messages_uninstallStickerSet();
      paramBaseFragment.stickerset = localTL_inputStickerSetID;
      ConnectionsManager.getInstance(this.currentAccount).sendRequest(paramBaseFragment, new RequestDelegate()
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
                  if (DataQuery.30.this.val$stickerSet.masks) {
                    Toast.makeText(DataQuery.30.this.val$context, LocaleController.getString("MasksRemoved", NUM), 0).show();
                  }
                  for (;;)
                  {
                    DataQuery.this.loadStickers(DataQuery.30.this.val$type, false, true);
                    return;
                    Toast.makeText(DataQuery.30.this.val$context, LocaleController.getString("StickersRemoved", NUM), 0).show();
                  }
                }
              }
              catch (Exception localException)
              {
                for (;;)
                {
                  FileLog.e(localException);
                  continue;
                  Toast.makeText(DataQuery.30.this.val$context, LocaleController.getString("ErrorOccurred", NUM), 0).show();
                }
              }
            }
          });
        }
      });
    }
  }
  
  public void reorderStickers(int paramInt, final ArrayList<Long> paramArrayList)
  {
    Collections.sort(this.stickerSets[paramInt], new Comparator()
    {
      public int compare(TLRPC.TL_messages_stickerSet paramAnonymousTL_messages_stickerSet1, TLRPC.TL_messages_stickerSet paramAnonymousTL_messages_stickerSet2)
      {
        int i = paramArrayList.indexOf(Long.valueOf(paramAnonymousTL_messages_stickerSet1.set.id));
        int j = paramArrayList.indexOf(Long.valueOf(paramAnonymousTL_messages_stickerSet2.set.id));
        if (i > j) {
          i = 1;
        }
        for (;;)
        {
          return i;
          if (i < j) {
            i = -1;
          } else {
            i = 0;
          }
        }
      }
    });
    this.loadHash[paramInt] = calcStickersHash(this.stickerSets[paramInt]);
    NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.stickersDidLoaded, new Object[] { Integer.valueOf(paramInt) });
    loadStickers(paramInt, false, true);
  }
  
  public void saveDraft(long paramLong, CharSequence paramCharSequence, ArrayList<TLRPC.MessageEntity> paramArrayList, TLRPC.Message paramMessage, boolean paramBoolean)
  {
    saveDraft(paramLong, paramCharSequence, paramArrayList, paramMessage, paramBoolean, false);
  }
  
  public void saveDraft(long paramLong, CharSequence paramCharSequence, ArrayList<TLRPC.MessageEntity> paramArrayList, TLRPC.Message paramMessage, boolean paramBoolean1, boolean paramBoolean2)
  {
    Object localObject;
    if ((!TextUtils.isEmpty(paramCharSequence)) || (paramMessage != null))
    {
      localObject = new TLRPC.TL_draftMessage();
      ((TLRPC.DraftMessage)localObject).date = ((int)(System.currentTimeMillis() / 1000L));
      if (paramCharSequence != null) {
        break label211;
      }
      paramCharSequence = "";
      label42:
      ((TLRPC.DraftMessage)localObject).message = paramCharSequence;
      ((TLRPC.DraftMessage)localObject).no_webpage = paramBoolean1;
      if (paramMessage != null)
      {
        ((TLRPC.DraftMessage)localObject).reply_to_msg_id = paramMessage.id;
        ((TLRPC.DraftMessage)localObject).flags |= 0x1;
      }
      if ((paramArrayList != null) && (!paramArrayList.isEmpty()))
      {
        ((TLRPC.DraftMessage)localObject).entities = paramArrayList;
        ((TLRPC.DraftMessage)localObject).flags |= 0x8;
      }
      paramCharSequence = (TLRPC.DraftMessage)this.drafts.get(paramLong);
      if ((paramBoolean2) || (((paramCharSequence == null) || (!paramCharSequence.message.equals(((TLRPC.DraftMessage)localObject).message)) || (paramCharSequence.reply_to_msg_id != ((TLRPC.DraftMessage)localObject).reply_to_msg_id) || (paramCharSequence.no_webpage != ((TLRPC.DraftMessage)localObject).no_webpage)) && ((paramCharSequence != null) || (!TextUtils.isEmpty(((TLRPC.DraftMessage)localObject).message)) || (((TLRPC.DraftMessage)localObject).reply_to_msg_id != 0)))) {
        break label221;
      }
    }
    for (;;)
    {
      return;
      localObject = new TLRPC.TL_draftMessageEmpty();
      break;
      label211:
      paramCharSequence = paramCharSequence.toString();
      break label42;
      label221:
      saveDraft(paramLong, (TLRPC.DraftMessage)localObject, paramMessage, false);
      int i = (int)paramLong;
      if (i != 0)
      {
        paramCharSequence = new TLRPC.TL_messages_saveDraft();
        paramCharSequence.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(i);
        if (paramCharSequence.peer != null)
        {
          paramCharSequence.message = ((TLRPC.DraftMessage)localObject).message;
          paramCharSequence.no_webpage = ((TLRPC.DraftMessage)localObject).no_webpage;
          paramCharSequence.reply_to_msg_id = ((TLRPC.DraftMessage)localObject).reply_to_msg_id;
          paramCharSequence.entities = ((TLRPC.DraftMessage)localObject).entities;
          paramCharSequence.flags = ((TLRPC.DraftMessage)localObject).flags;
          ConnectionsManager.getInstance(this.currentAccount).sendRequest(paramCharSequence, new RequestDelegate()
          {
            public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error) {}
          });
        }
      }
      else
      {
        MessagesController.getInstance(this.currentAccount).sortDialogs(null);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
      }
    }
  }
  
  public void saveDraft(final long paramLong, TLRPC.DraftMessage paramDraftMessage, TLRPC.Message paramMessage, boolean paramBoolean)
  {
    Object localObject = this.preferences.edit();
    label144:
    label201:
    final long l;
    if ((paramDraftMessage == null) || ((paramDraftMessage instanceof TLRPC.TL_draftMessageEmpty)))
    {
      this.drafts.remove(paramLong);
      this.draftMessages.remove(paramLong);
      this.preferences.edit().remove("" + paramLong).remove("r_" + paramLong).commit();
      if (paramMessage != null) {
        break label381;
      }
      this.draftMessages.remove(paramLong);
      ((SharedPreferences.Editor)localObject).remove("r_" + paramLong);
      ((SharedPreferences.Editor)localObject).commit();
      if (paramBoolean) {
        if ((paramDraftMessage.reply_to_msg_id != 0) && (paramMessage == null))
        {
          i = (int)paramLong;
          paramMessage = null;
          localObject = null;
          if (i <= 0) {
            break label451;
          }
          paramMessage = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(i));
          if ((paramMessage != null) || (localObject != null))
          {
            l = paramDraftMessage.reply_to_msg_id;
            if (!ChatObject.isChannel((TLRPC.Chat)localObject)) {
              break label472;
            }
            l |= ((TLRPC.Chat)localObject).id << 32;
          }
        }
      }
    }
    label381:
    label451:
    label472:
    for (int i = ((TLRPC.Chat)localObject).id;; i = 0)
    {
      MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable()
      {
        public void run()
        {
          Object localObject1 = null;
          for (;;)
          {
            try
            {
              localObject2 = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT data FROM messages WHERE mid = %d", new Object[] { Long.valueOf(l) }), new Object[0]);
              localObject3 = localObject1;
              if (((SQLiteCursor)localObject2).next())
              {
                NativeByteBuffer localNativeByteBuffer = ((SQLiteCursor)localObject2).byteBufferValue(0);
                localObject3 = localObject1;
                if (localNativeByteBuffer != null)
                {
                  localObject3 = TLRPC.Message.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
                  ((TLRPC.Message)localObject3).readAttachPath(localNativeByteBuffer, UserConfig.getInstance(DataQuery.this.currentAccount).clientUserId);
                  localNativeByteBuffer.reuse();
                }
              }
              ((SQLiteCursor)localObject2).dispose();
              if (localObject3 != null) {
                continue;
              }
              if (paramLong != 0)
              {
                localObject2 = new org/telegram/tgnet/TLRPC$TL_channels_getMessages;
                ((TLRPC.TL_channels_getMessages)localObject2).<init>();
                ((TLRPC.TL_channels_getMessages)localObject2).channel = MessagesController.getInstance(DataQuery.this.currentAccount).getInputChannel(paramLong);
                ((TLRPC.TL_channels_getMessages)localObject2).id.add(Integer.valueOf((int)l));
                localObject3 = ConnectionsManager.getInstance(DataQuery.this.currentAccount);
                localObject1 = new org/telegram/messenger/DataQuery$62$1;
                ((1)localObject1).<init>(this);
                ((ConnectionsManager)localObject3).sendRequest((TLObject)localObject2, (RequestDelegate)localObject1);
                return;
              }
            }
            catch (Exception localException)
            {
              Object localObject2;
              Object localObject3;
              FileLog.e(localException);
              continue;
              DataQuery.this.saveDraftReplyMessage(this.val$did, localException);
              continue;
            }
            localObject2 = new org/telegram/tgnet/TLRPC$TL_messages_getMessages;
            ((TLRPC.TL_messages_getMessages)localObject2).<init>();
            ((TLRPC.TL_messages_getMessages)localObject2).id.add(Integer.valueOf((int)l));
            localObject1 = ConnectionsManager.getInstance(DataQuery.this.currentAccount);
            localObject3 = new org/telegram/messenger/DataQuery$62$2;
            ((2)localObject3).<init>(this);
            ((ConnectionsManager)localObject1).sendRequest((TLObject)localObject2, (RequestDelegate)localObject3);
          }
        }
      });
      NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.newDraftReceived, new Object[] { Long.valueOf(paramLong) });
      return;
      this.drafts.put(paramLong, paramDraftMessage);
      try
      {
        SerializedData localSerializedData1 = new org/telegram/tgnet/SerializedData;
        localSerializedData1.<init>(paramDraftMessage.getObjectSize());
        paramDraftMessage.serializeToStream(localSerializedData1);
        StringBuilder localStringBuilder = new java/lang/StringBuilder;
        localStringBuilder.<init>();
        ((SharedPreferences.Editor)localObject).putString("" + paramLong, Utilities.bytesToHex(localSerializedData1.toByteArray()));
      }
      catch (Exception localException)
      {
        FileLog.e(localException);
      }
      break;
      this.draftMessages.put(paramLong, paramMessage);
      SerializedData localSerializedData2 = new SerializedData(paramMessage.getObjectSize());
      paramMessage.serializeToStream(localSerializedData2);
      ((SharedPreferences.Editor)localObject).putString("r_" + paramLong, Utilities.bytesToHex(localSerializedData2.toByteArray()));
      break label144;
      localObject = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-i));
      break label201;
    }
  }
  
  public void searchMessagesInChat(String paramString, long paramLong1, long paramLong2, int paramInt1, int paramInt2, TLRPC.User paramUser)
  {
    searchMessagesInChat(paramString, paramLong1, paramLong2, paramInt1, paramInt2, false, paramUser);
  }
  
  public void uninstallShortcut(long paramLong)
  {
    try
    {
      Object localObject2;
      if (Build.VERSION.SDK_INT >= 26)
      {
        ShortcutManager localShortcutManager = (ShortcutManager)ApplicationLoader.applicationContext.getSystemService(ShortcutManager.class);
        localObject1 = new java/util/ArrayList;
        ((ArrayList)localObject1).<init>();
        localObject2 = new java/lang/StringBuilder;
        ((StringBuilder)localObject2).<init>();
        ((ArrayList)localObject1).add("sdid_" + paramLong);
        localShortcutManager.removeDynamicShortcuts((List)localObject1);
      }
      for (;;)
      {
        return;
        i = (int)paramLong;
        int j = (int)(paramLong >> 32);
        localObject2 = null;
        localObject1 = null;
        if (i != 0) {
          break;
        }
        localObject2 = MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf(j));
        if (localObject2 != null)
        {
          localObject2 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(((TLRPC.EncryptedChat)localObject2).user_id));
          if ((localObject2 != null) || (localObject1 != null))
          {
            if (localObject2 == null) {
              break label289;
            }
            localObject2 = ContactsController.formatName(((TLRPC.User)localObject2).first_name, ((TLRPC.User)localObject2).last_name);
            localObject1 = new android/content/Intent;
            ((Intent)localObject1).<init>();
            ((Intent)localObject1).putExtra("android.intent.extra.shortcut.INTENT", createIntrnalShortcutIntent(paramLong));
            ((Intent)localObject1).putExtra("android.intent.extra.shortcut.NAME", (String)localObject2);
            ((Intent)localObject1).putExtra("duplicate", false);
            ((Intent)localObject1).setAction("com.android.launcher.action.UNINSTALL_SHORTCUT");
            ApplicationLoader.applicationContext.sendBroadcast((Intent)localObject1);
          }
        }
      }
    }
    catch (Exception localException)
    {
      for (;;)
      {
        Object localObject1;
        int i;
        FileLog.e(localException);
        continue;
        Object localObject3;
        if (i > 0)
        {
          localObject3 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(i));
        }
        else if (i < 0)
        {
          localObject1 = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-i));
          continue;
          label289:
          localObject3 = ((TLRPC.Chat)localObject1).title;
        }
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/DataQuery.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */