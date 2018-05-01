package org.telegram.messenger.query;

import android.text.Spannable;
import android.text.TextUtils;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.AbstractSerializedData;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageEntity;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.TL_channels_getMessages;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputMessageEntityMentionName;
import org.telegram.tgnet.TLRPC.TL_messageActionGameScore;
import org.telegram.tgnet.TLRPC.TL_messageActionHistoryClear;
import org.telegram.tgnet.TLRPC.TL_messageActionPaymentSent;
import org.telegram.tgnet.TLRPC.TL_messageActionPinMessage;
import org.telegram.tgnet.TLRPC.TL_messageEmpty;
import org.telegram.tgnet.TLRPC.TL_messageEntityBold;
import org.telegram.tgnet.TLRPC.TL_messageEntityCode;
import org.telegram.tgnet.TLRPC.TL_messageEntityItalic;
import org.telegram.tgnet.TLRPC.TL_messageEntityPre;
import org.telegram.tgnet.TLRPC.TL_messages_getMessages;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.messages_Messages;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.Components.URLSpanUserMention;

public class MessagesQuery
{
  private static Comparator<TLRPC.MessageEntity> entityComparator = new Comparator()
  {
    public int compare(TLRPC.MessageEntity paramAnonymousMessageEntity1, TLRPC.MessageEntity paramAnonymousMessageEntity2)
    {
      if (paramAnonymousMessageEntity1.offset > paramAnonymousMessageEntity2.offset) {
        return 1;
      }
      if (paramAnonymousMessageEntity1.offset < paramAnonymousMessageEntity2.offset) {
        return -1;
      }
      return 0;
    }
  };
  
  private static MessageObject broadcastPinnedMessage(final TLRPC.Message paramMessage, ArrayList<TLRPC.User> paramArrayList, final ArrayList<TLRPC.Chat> paramArrayList1, final boolean paramBoolean1, boolean paramBoolean2)
  {
    final HashMap localHashMap = new HashMap();
    int i = 0;
    while (i < paramArrayList.size())
    {
      localObject = (TLRPC.User)paramArrayList.get(i);
      localHashMap.put(Integer.valueOf(((TLRPC.User)localObject).id), localObject);
      i += 1;
    }
    final Object localObject = new HashMap();
    i = 0;
    while (i < paramArrayList1.size())
    {
      TLRPC.Chat localChat = (TLRPC.Chat)paramArrayList1.get(i);
      ((HashMap)localObject).put(Integer.valueOf(localChat.id), localChat);
      i += 1;
    }
    if (paramBoolean2) {
      return new MessageObject(paramMessage, localHashMap, (AbstractMap)localObject, false);
    }
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        MessagesController.getInstance().putUsers(this.val$users, paramBoolean1);
        MessagesController.getInstance().putChats(paramArrayList1, paramBoolean1);
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.didLoadedPinnedMessage, new Object[] { new MessageObject(paramMessage, localHashMap, localObject, false) });
      }
    });
    return null;
  }
  
  private static void broadcastReplyMessages(final ArrayList<TLRPC.Message> paramArrayList, final HashMap<Integer, ArrayList<MessageObject>> paramHashMap, ArrayList<TLRPC.User> paramArrayList1, final ArrayList<TLRPC.Chat> paramArrayList2, final long paramLong, final boolean paramBoolean)
  {
    final HashMap localHashMap = new HashMap();
    int i = 0;
    while (i < paramArrayList1.size())
    {
      localObject = (TLRPC.User)paramArrayList1.get(i);
      localHashMap.put(Integer.valueOf(((TLRPC.User)localObject).id), localObject);
      i += 1;
    }
    final Object localObject = new HashMap();
    i = 0;
    while (i < paramArrayList2.size())
    {
      TLRPC.Chat localChat = (TLRPC.Chat)paramArrayList2.get(i);
      ((HashMap)localObject).put(Integer.valueOf(localChat.id), localChat);
      i += 1;
    }
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        MessagesController.getInstance().putUsers(this.val$users, paramBoolean);
        MessagesController.getInstance().putChats(paramArrayList2, paramBoolean);
        int j = 0;
        int i = 0;
        while (i < paramArrayList.size())
        {
          Object localObject1 = (TLRPC.Message)paramArrayList.get(i);
          ArrayList localArrayList = (ArrayList)paramHashMap.get(Integer.valueOf(((TLRPC.Message)localObject1).id));
          if (localArrayList != null)
          {
            localObject1 = new MessageObject((TLRPC.Message)localObject1, localHashMap, localObject, false);
            j = 0;
            if (j < localArrayList.size())
            {
              Object localObject2 = (MessageObject)localArrayList.get(j);
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
                j += 1;
                break;
                if ((((MessageObject)localObject2).messageOwner.action instanceof TLRPC.TL_messageActionGameScore)) {
                  ((MessageObject)localObject2).generateGameMessageText(null);
                } else if ((((MessageObject)localObject2).messageOwner.action instanceof TLRPC.TL_messageActionPaymentSent)) {
                  ((MessageObject)localObject2).generatePaymentSentMessageText(null);
                }
              }
            }
            j = 1;
          }
          i += 1;
        }
        if (j != 0) {
          NotificationCenter.getInstance().postNotificationName(NotificationCenter.didLoadedReplyMessages, new Object[] { Long.valueOf(paramLong) });
        }
      }
    });
  }
  
  private static boolean checkInclusion(int paramInt, ArrayList<TLRPC.MessageEntity> paramArrayList)
  {
    if ((paramArrayList == null) || (paramArrayList.isEmpty())) {}
    for (;;)
    {
      return false;
      int j = paramArrayList.size();
      int i = 0;
      while (i < j)
      {
        TLRPC.MessageEntity localMessageEntity = (TLRPC.MessageEntity)paramArrayList.get(i);
        if ((localMessageEntity.offset <= paramInt) && (localMessageEntity.offset + localMessageEntity.length > paramInt)) {
          return true;
        }
        i += 1;
      }
    }
  }
  
  private static boolean checkIntersection(int paramInt1, int paramInt2, ArrayList<TLRPC.MessageEntity> paramArrayList)
  {
    if ((paramArrayList == null) || (paramArrayList.isEmpty())) {}
    for (;;)
    {
      return false;
      int j = paramArrayList.size();
      int i = 0;
      while (i < j)
      {
        TLRPC.MessageEntity localMessageEntity = (TLRPC.MessageEntity)paramArrayList.get(i);
        if ((localMessageEntity.offset > paramInt1) && (localMessageEntity.offset + localMessageEntity.length <= paramInt2)) {
          return true;
        }
        i += 1;
      }
    }
  }
  
  public static ArrayList<TLRPC.MessageEntity> getEntities(CharSequence[] paramArrayOfCharSequence)
  {
    if ((paramArrayOfCharSequence == null) || (paramArrayOfCharSequence[0] == null))
    {
      localObject1 = null;
      return (ArrayList<TLRPC.MessageEntity>)localObject1;
    }
    Object localObject2 = null;
    int j = -1;
    int i = 0;
    int k = 0;
    Object localObject3 = paramArrayOfCharSequence[0];
    label38:
    int m;
    if (k == 0)
    {
      localObject1 = "`";
      m = TextUtils.indexOf((CharSequence)localObject3, (CharSequence)localObject1, i);
      if (m == -1) {
        break label694;
      }
      if (j != -1) {
        break label144;
      }
      if ((paramArrayOfCharSequence[0].length() - m <= 2) || (paramArrayOfCharSequence[0].charAt(m + 1) != '`') || (paramArrayOfCharSequence[0].charAt(m + 2) != '`')) {
        break label134;
      }
      k = 1;
      label110:
      j = m;
      if (k == 0) {
        break label139;
      }
    }
    label134:
    label139:
    for (i = 3;; i = 1)
    {
      i = m + i;
      break;
      localObject1 = "```";
      break label38;
      k = 0;
      break label110;
    }
    label144:
    Object localObject1 = localObject2;
    if (localObject2 == null) {
      localObject1 = new ArrayList();
    }
    if (k != 0) {}
    for (i = 3;; i = 1)
    {
      i = m + i;
      while ((i < paramArrayOfCharSequence[0].length()) && (paramArrayOfCharSequence[0].charAt(i) == '`'))
      {
        m += 1;
        i += 1;
      }
    }
    label223:
    int n;
    label249:
    label263:
    label274:
    Object localObject5;
    label326:
    label345:
    Object localObject4;
    if (k != 0)
    {
      i = 3;
      n = m + i;
      if (k == 0) {
        break label585;
      }
      if (j <= 0) {
        break label545;
      }
      i = paramArrayOfCharSequence[0].charAt(j - 1);
      if ((i != 32) && (i != 10)) {
        break label550;
      }
      i = 1;
      localObject2 = paramArrayOfCharSequence[0];
      if (i == 0) {
        break label555;
      }
      k = 1;
      localObject2 = TextUtils.substring((CharSequence)localObject2, 0, j - k);
      localObject5 = TextUtils.substring(paramArrayOfCharSequence[0], j + 3, m);
      if (m + 3 >= paramArrayOfCharSequence[0].length()) {
        break label560;
      }
      k = paramArrayOfCharSequence[0].charAt(m + 3);
      localObject3 = paramArrayOfCharSequence[0];
      if ((k != 32) && (k != 10)) {
        break label565;
      }
      k = 1;
      localObject4 = TextUtils.substring((CharSequence)localObject3, k + (m + 3), paramArrayOfCharSequence[0].length());
      if (((CharSequence)localObject2).length() == 0) {
        break label570;
      }
      localObject2 = TextUtils.concat(new CharSequence[] { localObject2, "\n" });
      k = i;
      label397:
      localObject3 = localObject4;
      if (((CharSequence)localObject4).length() != 0) {
        localObject3 = TextUtils.concat(new CharSequence[] { "\n", localObject4 });
      }
      i = n;
      if (!TextUtils.isEmpty((CharSequence)localObject5))
      {
        paramArrayOfCharSequence[0] = TextUtils.concat(new CharSequence[] { localObject2, localObject5, localObject3 });
        localObject2 = new TLRPC.TL_messageEntityPre();
        if (k == 0) {
          break label575;
        }
        i = 0;
        label481:
        ((TLRPC.TL_messageEntityPre)localObject2).offset = (i + j);
        if (k == 0) {
          break label580;
        }
        i = 0;
        label495:
        ((TLRPC.TL_messageEntityPre)localObject2).length = (i + (m - j - 3));
        ((TLRPC.TL_messageEntityPre)localObject2).language = "";
        ((ArrayList)localObject1).add(localObject2);
        i = n - 6;
      }
    }
    for (;;)
    {
      j = -1;
      k = 0;
      localObject2 = localObject1;
      break;
      i = 1;
      break label223;
      label545:
      i = 0;
      break label249;
      label550:
      i = 0;
      break label263;
      label555:
      k = 0;
      break label274;
      label560:
      k = 0;
      break label326;
      label565:
      k = 0;
      break label345;
      label570:
      k = 1;
      break label397;
      label575:
      i = 1;
      break label481;
      label580:
      i = 1;
      break label495;
      label585:
      i = n;
      if (j + 1 != m)
      {
        paramArrayOfCharSequence[0] = TextUtils.concat(new CharSequence[] { TextUtils.substring(paramArrayOfCharSequence[0], 0, j), TextUtils.substring(paramArrayOfCharSequence[0], j + 1, m), TextUtils.substring(paramArrayOfCharSequence[0], m + 1, paramArrayOfCharSequence[0].length()) });
        localObject2 = new TLRPC.TL_messageEntityCode();
        ((TLRPC.TL_messageEntityCode)localObject2).offset = j;
        ((TLRPC.TL_messageEntityCode)localObject2).length = (m - j - 1);
        ((ArrayList)localObject1).add(localObject2);
        i = n - 2;
      }
    }
    label694:
    localObject1 = localObject2;
    if (j != -1)
    {
      localObject1 = localObject2;
      if (k != 0)
      {
        paramArrayOfCharSequence[0] = TextUtils.concat(new CharSequence[] { TextUtils.substring(paramArrayOfCharSequence[0], 0, j), TextUtils.substring(paramArrayOfCharSequence[0], j + 2, paramArrayOfCharSequence[0].length()) });
        localObject1 = localObject2;
        if (localObject2 == null) {
          localObject1 = new ArrayList();
        }
        localObject2 = new TLRPC.TL_messageEntityCode();
        ((TLRPC.TL_messageEntityCode)localObject2).offset = j;
        ((TLRPC.TL_messageEntityCode)localObject2).length = 1;
        ((ArrayList)localObject1).add(localObject2);
      }
    }
    localObject3 = localObject1;
    if ((paramArrayOfCharSequence[0] instanceof Spannable))
    {
      localObject4 = (Spannable)paramArrayOfCharSequence[0];
      localObject3 = (TypefaceSpan[])((Spannable)localObject4).getSpans(0, paramArrayOfCharSequence[0].length(), TypefaceSpan.class);
      localObject2 = localObject1;
      if (localObject3 != null)
      {
        localObject2 = localObject1;
        if (localObject3.length > 0)
        {
          i = 0;
          for (;;)
          {
            localObject2 = localObject1;
            if (i >= localObject3.length) {
              break label1024;
            }
            localObject5 = localObject3[i];
            j = ((Spannable)localObject4).getSpanStart(localObject5);
            k = ((Spannable)localObject4).getSpanEnd(localObject5);
            localObject2 = localObject1;
            if (!checkInclusion(j, (ArrayList)localObject1))
            {
              localObject2 = localObject1;
              if (!checkInclusion(k, (ArrayList)localObject1))
              {
                if (!checkIntersection(j, k, (ArrayList)localObject1)) {
                  break;
                }
                localObject2 = localObject1;
              }
            }
            i += 1;
            localObject1 = localObject2;
          }
          localObject2 = localObject1;
          if (localObject1 == null) {
            localObject2 = new ArrayList();
          }
          if (((TypefaceSpan)localObject5).isBold()) {}
          for (localObject1 = new TLRPC.TL_messageEntityBold();; localObject1 = new TLRPC.TL_messageEntityItalic())
          {
            ((TLRPC.MessageEntity)localObject1).offset = j;
            ((TLRPC.MessageEntity)localObject1).length = (k - j);
            ((ArrayList)localObject2).add(localObject1);
            break;
          }
        }
      }
      label1024:
      localObject5 = (URLSpanUserMention[])((Spannable)localObject4).getSpans(0, paramArrayOfCharSequence[0].length(), URLSpanUserMention.class);
      localObject3 = localObject2;
      if (localObject5 != null)
      {
        localObject3 = localObject2;
        if (localObject5.length > 0)
        {
          localObject1 = localObject2;
          if (localObject2 == null) {
            localObject1 = new ArrayList();
          }
          i = 0;
          for (;;)
          {
            localObject3 = localObject1;
            if (i >= localObject5.length) {
              break;
            }
            localObject2 = new TLRPC.TL_inputMessageEntityMentionName();
            ((TLRPC.TL_inputMessageEntityMentionName)localObject2).user_id = MessagesController.getInputUser(Utilities.parseInt(localObject5[i].getURL()).intValue());
            if (((TLRPC.TL_inputMessageEntityMentionName)localObject2).user_id != null)
            {
              ((TLRPC.TL_inputMessageEntityMentionName)localObject2).offset = ((Spannable)localObject4).getSpanStart(localObject5[i]);
              ((TLRPC.TL_inputMessageEntityMentionName)localObject2).length = (Math.min(((Spannable)localObject4).getSpanEnd(localObject5[i]), paramArrayOfCharSequence[0].length()) - ((TLRPC.TL_inputMessageEntityMentionName)localObject2).offset);
              if (paramArrayOfCharSequence[0].charAt(((TLRPC.TL_inputMessageEntityMentionName)localObject2).offset + ((TLRPC.TL_inputMessageEntityMentionName)localObject2).length - 1) == ' ') {
                ((TLRPC.TL_inputMessageEntityMentionName)localObject2).length -= 1;
              }
              ((ArrayList)localObject1).add(localObject2);
            }
            i += 1;
          }
        }
      }
    }
    k = 0;
    for (;;)
    {
      localObject1 = localObject3;
      if (k >= 2) {
        break;
      }
      j = 0;
      i = -1;
      if (k == 0)
      {
        localObject2 = "**";
        if (k != 0) {
          break label1347;
        }
        m = 42;
      }
      label1269:
      int i1;
      for (;;)
      {
        j = TextUtils.indexOf(paramArrayOfCharSequence[0], (CharSequence)localObject2, j);
        if (j == -1) {
          break label1625;
        }
        if (i == -1)
        {
          if (j == 0) {}
          for (i1 = 32;; i1 = paramArrayOfCharSequence[0].charAt(j - 1))
          {
            n = i;
            if (!checkInclusion(j, (ArrayList)localObject3)) {
              if (i1 != 32)
              {
                n = i;
                if (i1 != 10) {}
              }
              else
              {
                n = j;
              }
            }
            j += 2;
            i = n;
            break label1269;
            localObject2 = "__";
            break;
            label1347:
            m = 95;
            break label1269;
          }
        }
        i1 = j + 2;
        n = j;
        j = i1;
        while ((j < paramArrayOfCharSequence[0].length()) && (paramArrayOfCharSequence[0].charAt(j) == m))
        {
          n += 1;
          j += 1;
        }
        i1 = n + 2;
        if ((!checkInclusion(n, (ArrayList)localObject3)) && (!checkIntersection(i, n, (ArrayList)localObject3))) {
          break label1455;
        }
        i = -1;
        j = i1;
      }
      label1455:
      localObject1 = localObject3;
      j = i1;
      if (i + 2 != n)
      {
        localObject1 = localObject3;
        if (localObject3 == null) {
          localObject1 = new ArrayList();
        }
        paramArrayOfCharSequence[0] = TextUtils.concat(new CharSequence[] { TextUtils.substring(paramArrayOfCharSequence[0], 0, i), TextUtils.substring(paramArrayOfCharSequence[0], i + 2, n), TextUtils.substring(paramArrayOfCharSequence[0], n + 2, paramArrayOfCharSequence[0].length()) });
        if (k != 0) {
          break label1613;
        }
      }
      label1613:
      for (localObject3 = new TLRPC.TL_messageEntityBold();; localObject3 = new TLRPC.TL_messageEntityItalic())
      {
        ((TLRPC.MessageEntity)localObject3).offset = i;
        ((TLRPC.MessageEntity)localObject3).length = (n - i - 2);
        removeOffsetAfter(((TLRPC.MessageEntity)localObject3).offset + ((TLRPC.MessageEntity)localObject3).length, 4, (ArrayList)localObject1);
        ((ArrayList)localObject1).add(localObject3);
        j = i1 - 4;
        i = -1;
        localObject3 = localObject1;
        break;
      }
      label1625:
      k += 1;
    }
  }
  
  public static MessageObject loadPinnedMessage(int paramInt1, final int paramInt2, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
      {
        public void run()
        {
          MessagesQuery.loadPinnedMessageInternal(this.val$channelId, paramInt2, false);
        }
      });
      return null;
    }
    return loadPinnedMessageInternal(paramInt1, paramInt2, true);
  }
  
  private static MessageObject loadPinnedMessageInternal(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    long l1 = paramInt2;
    long l2 = paramInt1;
    Object localObject2 = null;
    for (;;)
    {
      try
      {
        localArrayList1 = new ArrayList();
        localArrayList2 = new ArrayList();
        localArrayList3 = new ArrayList();
        localArrayList4 = new ArrayList();
        localSQLiteCursor = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, mid, date FROM messages WHERE mid = %d", new Object[] { Long.valueOf(l1 | l2 << 32) }), new Object[0]);
        localObject1 = localObject2;
        NativeByteBuffer localNativeByteBuffer;
        if (localSQLiteCursor.next())
        {
          localNativeByteBuffer = localSQLiteCursor.byteBufferValue(0);
          localObject1 = localObject2;
          if (localNativeByteBuffer != null)
          {
            localObject1 = TLRPC.Message.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
            localNativeByteBuffer.reuse();
            if (!(((TLRPC.Message)localObject1).action instanceof TLRPC.TL_messageActionHistoryClear)) {
              continue;
            }
            localObject1 = null;
          }
        }
        localSQLiteCursor.dispose();
        localObject2 = localObject1;
        if (localObject1 != null) {
          continue;
        }
        localSQLiteCursor = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data FROM chat_pinned WHERE uid = %d", new Object[] { Integer.valueOf(paramInt1) }), new Object[0]);
        localObject2 = localObject1;
        if (localSQLiteCursor.next())
        {
          localNativeByteBuffer = localSQLiteCursor.byteBufferValue(0);
          localObject2 = localObject1;
          if (localNativeByteBuffer != null)
          {
            localObject2 = TLRPC.Message.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
            localNativeByteBuffer.reuse();
            if (((TLRPC.Message)localObject2).id != paramInt2) {
              continue;
            }
            if (!(((TLRPC.Message)localObject2).action instanceof TLRPC.TL_messageActionHistoryClear)) {
              continue;
            }
          }
        }
      }
      catch (Exception localException)
      {
        ArrayList localArrayList1;
        ArrayList localArrayList2;
        ArrayList localArrayList3;
        ArrayList localArrayList4;
        SQLiteCursor localSQLiteCursor;
        Object localObject1;
        FileLog.e(localException);
        break label489;
        l1 = -paramInt1;
        ((TLRPC.Message)localObject2).dialog_id = l1;
        MessagesStorage.addUsersAndChatsFromMessage((TLRPC.Message)localObject2, localArrayList3, localArrayList4);
        continue;
        if (!paramBoolean) {
          continue;
        }
        return broadcastPinnedMessage((TLRPC.Message)localObject2, localArrayList1, localArrayList2, true, paramBoolean);
        if (localArrayList3.isEmpty()) {
          continue;
        }
        MessagesStorage.getInstance().getUsersInternal(TextUtils.join(",", localArrayList3), localArrayList1);
        if (localArrayList4.isEmpty()) {
          continue;
        }
        MessagesStorage.getInstance().getChatsInternal(TextUtils.join(",", localArrayList4), localArrayList2);
        broadcastPinnedMessage((TLRPC.Message)localObject2, localArrayList1, localArrayList2, true, false);
        break label489;
        localObject2 = null;
        continue;
      }
      localSQLiteCursor.dispose();
      if (localObject2 != null) {
        continue;
      }
      localObject1 = new TLRPC.TL_channels_getMessages();
      ((TLRPC.TL_channels_getMessages)localObject1).channel = MessagesController.getInputChannel(paramInt1);
      ((TLRPC.TL_channels_getMessages)localObject1).id.add(Integer.valueOf(paramInt2));
      ConnectionsManager.getInstance().sendRequest((TLObject)localObject1, new RequestDelegate()
      {
        public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
        {
          int j = 0;
          int i = j;
          if (paramAnonymousTL_error == null)
          {
            paramAnonymousTLObject = (TLRPC.messages_Messages)paramAnonymousTLObject;
            MessagesQuery.removeEmptyMessages(paramAnonymousTLObject.messages);
            i = j;
            if (!paramAnonymousTLObject.messages.isEmpty())
            {
              ImageLoader.saveMessagesThumbs(paramAnonymousTLObject.messages);
              MessagesQuery.broadcastPinnedMessage((TLRPC.Message)paramAnonymousTLObject.messages.get(0), paramAnonymousTLObject.users, paramAnonymousTLObject.chats, false, false);
              MessagesStorage.getInstance().putUsersAndChats(paramAnonymousTLObject.users, paramAnonymousTLObject.chats, true, true);
              MessagesQuery.savePinnedMessage((TLRPC.Message)paramAnonymousTLObject.messages.get(0));
              i = 1;
            }
          }
          if (i == 0) {
            MessagesStorage.getInstance().updateChannelPinnedMessage(this.val$channelId, 0);
          }
        }
      });
      break label489;
      ((TLRPC.Message)localObject1).id = localSQLiteCursor.intValue(1);
      ((TLRPC.Message)localObject1).date = localSQLiteCursor.intValue(2);
      ((TLRPC.Message)localObject1).dialog_id = (-paramInt1);
      MessagesStorage.addUsersAndChatsFromMessage((TLRPC.Message)localObject1, localArrayList3, localArrayList4);
    }
    label489:
    return null;
  }
  
  public static void loadReplyMessagesForMessages(ArrayList<MessageObject> paramArrayList, final long paramLong)
  {
    ArrayList localArrayList3;
    final HashMap localHashMap;
    StringBuilder localStringBuilder;
    final int i;
    MessageObject localMessageObject;
    Object localObject;
    ArrayList localArrayList2;
    ArrayList localArrayList1;
    if ((int)paramLong == 0)
    {
      localArrayList3 = new ArrayList();
      localHashMap = new HashMap();
      localStringBuilder = new StringBuilder();
      i = 0;
      while (i < paramArrayList.size())
      {
        localMessageObject = (MessageObject)paramArrayList.get(i);
        if ((localMessageObject.isReply()) && (localMessageObject.replyMessageObject == null))
        {
          localObject = Long.valueOf(localMessageObject.messageOwner.reply_to_random_id);
          if (localStringBuilder.length() > 0) {
            localStringBuilder.append(',');
          }
          localStringBuilder.append(localObject);
          localArrayList2 = (ArrayList)localHashMap.get(localObject);
          localArrayList1 = localArrayList2;
          if (localArrayList2 == null)
          {
            localArrayList1 = new ArrayList();
            localHashMap.put(localObject, localArrayList1);
          }
          localArrayList1.add(localMessageObject);
          if (!localArrayList3.contains(localObject)) {
            localArrayList3.add(localObject);
          }
        }
        i += 1;
      }
      if (!localArrayList3.isEmpty()) {}
    }
    do
    {
      return;
      MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
      {
        public void run()
        {
          for (;;)
          {
            int i;
            try
            {
              Object localObject1 = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT m.data, m.mid, m.date, r.random_id FROM randoms as r INNER JOIN messages as m ON r.mid = m.mid WHERE r.random_id IN(%s)", new Object[] { TextUtils.join(",", this.val$replyMessages) }), new Object[0]);
              Object localObject2;
              if (((SQLiteCursor)localObject1).next())
              {
                localObject2 = ((SQLiteCursor)localObject1).byteBufferValue(0);
                if (localObject2 == null) {
                  continue;
                }
                Object localObject3 = TLRPC.Message.TLdeserialize((AbstractSerializedData)localObject2, ((NativeByteBuffer)localObject2).readInt32(false), false);
                ((NativeByteBuffer)localObject2).reuse();
                ((TLRPC.Message)localObject3).id = ((SQLiteCursor)localObject1).intValue(1);
                ((TLRPC.Message)localObject3).date = ((SQLiteCursor)localObject1).intValue(2);
                ((TLRPC.Message)localObject3).dialog_id = paramLong;
                localObject2 = (ArrayList)this.val$replyMessageRandomOwners.remove(Long.valueOf(((SQLiteCursor)localObject1).longValue(3)));
                if (localObject2 == null) {
                  continue;
                }
                localObject3 = new MessageObject((TLRPC.Message)localObject3, null, null, false);
                i = 0;
                if (i >= ((ArrayList)localObject2).size()) {
                  continue;
                }
                Object localObject4 = (MessageObject)((ArrayList)localObject2).get(i);
                ((MessageObject)localObject4).replyMessageObject = ((MessageObject)localObject3);
                ((MessageObject)localObject4).messageOwner.reply_to_msg_id = ((MessageObject)localObject3).getId();
                if (((MessageObject)localObject4).isMegagroup())
                {
                  localObject4 = ((MessageObject)localObject4).replyMessageObject.messageOwner;
                  ((TLRPC.Message)localObject4).flags |= 0x80000000;
                }
              }
              else
              {
                ((SQLiteCursor)localObject1).dispose();
                if (!this.val$replyMessageRandomOwners.isEmpty())
                {
                  localObject1 = this.val$replyMessageRandomOwners.entrySet().iterator();
                  if (((Iterator)localObject1).hasNext())
                  {
                    localObject2 = (ArrayList)((Map.Entry)((Iterator)localObject1).next()).getValue();
                    i = 0;
                    if (i < ((ArrayList)localObject2).size())
                    {
                      ((MessageObject)((ArrayList)localObject2).get(i)).messageOwner.reply_to_random_id = 0L;
                      i += 1;
                      continue;
                    }
                    continue;
                  }
                }
                AndroidUtilities.runOnUIThread(new Runnable()
                {
                  public void run()
                  {
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.didLoadedReplyMessages, new Object[] { Long.valueOf(MessagesQuery.6.this.val$dialogId) });
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
            i += 1;
          }
        }
      });
      return;
      localArrayList3 = new ArrayList();
      localHashMap = new HashMap();
      localStringBuilder = new StringBuilder();
      i = 0;
      int j = 0;
      while (j < paramArrayList.size())
      {
        localMessageObject = (MessageObject)paramArrayList.get(j);
        int k = i;
        if (localMessageObject.getId() > 0)
        {
          k = i;
          if (localMessageObject.isReply())
          {
            k = i;
            if (localMessageObject.replyMessageObject == null)
            {
              localObject = Integer.valueOf(localMessageObject.messageOwner.reply_to_msg_id);
              long l2 = ((Integer)localObject).intValue();
              long l1 = l2;
              if (localMessageObject.messageOwner.to_id.channel_id != 0)
              {
                l1 = l2 | localMessageObject.messageOwner.to_id.channel_id << 32;
                i = localMessageObject.messageOwner.to_id.channel_id;
              }
              if (localStringBuilder.length() > 0) {
                localStringBuilder.append(',');
              }
              localStringBuilder.append(l1);
              localArrayList2 = (ArrayList)localHashMap.get(localObject);
              localArrayList1 = localArrayList2;
              if (localArrayList2 == null)
              {
                localArrayList1 = new ArrayList();
                localHashMap.put(localObject, localArrayList1);
              }
              localArrayList1.add(localMessageObject);
              k = i;
              if (!localArrayList3.contains(localObject))
              {
                localArrayList3.add(localObject);
                k = i;
              }
            }
          }
        }
        j += 1;
        i = k;
      }
    } while (localArrayList3.isEmpty());
    MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
    {
      public void run()
      {
        do
        {
          ArrayList localArrayList2;
          ArrayList localArrayList3;
          ArrayList localArrayList4;
          ArrayList localArrayList5;
          try
          {
            ArrayList localArrayList1 = new ArrayList();
            localArrayList2 = new ArrayList();
            localArrayList3 = new ArrayList();
            localArrayList4 = new ArrayList();
            localArrayList5 = new ArrayList();
            SQLiteCursor localSQLiteCursor = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, mid, date FROM messages WHERE mid IN(%s)", new Object[] { this.val$stringBuilder.toString() }), new Object[0]);
            while (localSQLiteCursor.next())
            {
              NativeByteBuffer localNativeByteBuffer = localSQLiteCursor.byteBufferValue(0);
              if (localNativeByteBuffer != null)
              {
                TLRPC.Message localMessage = TLRPC.Message.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
                localNativeByteBuffer.reuse();
                localMessage.id = localSQLiteCursor.intValue(1);
                localMessage.date = localSQLiteCursor.intValue(2);
                localMessage.dialog_id = paramLong;
                MessagesStorage.addUsersAndChatsFromMessage(localMessage, localArrayList4, localArrayList5);
                localArrayList1.add(localMessage);
                localHashMap.remove(Integer.valueOf(localMessage.id));
              }
            }
            localSQLiteCursor.dispose();
          }
          catch (Exception localException)
          {
            FileLog.e(localException);
            return;
          }
          if (!localArrayList4.isEmpty()) {
            MessagesStorage.getInstance().getUsersInternal(TextUtils.join(",", localArrayList4), localArrayList2);
          }
          if (!localArrayList5.isEmpty()) {
            MessagesStorage.getInstance().getChatsInternal(TextUtils.join(",", localArrayList5), localArrayList3);
          }
          MessagesQuery.broadcastReplyMessages(localException, i, localArrayList2, localArrayList3, paramLong, true);
        } while (localHashMap.isEmpty());
        if (this.val$channelIdFinal != 0)
        {
          localObject = new TLRPC.TL_channels_getMessages();
          ((TLRPC.TL_channels_getMessages)localObject).channel = MessagesController.getInputChannel(this.val$channelIdFinal);
          ((TLRPC.TL_channels_getMessages)localObject).id = localHashMap;
          ConnectionsManager.getInstance().sendRequest((TLObject)localObject, new RequestDelegate()
          {
            public void run(TLObject paramAnonymous2TLObject, TLRPC.TL_error paramAnonymous2TL_error)
            {
              if (paramAnonymous2TL_error == null)
              {
                paramAnonymous2TLObject = (TLRPC.messages_Messages)paramAnonymous2TLObject;
                MessagesQuery.removeEmptyMessages(paramAnonymous2TLObject.messages);
                ImageLoader.saveMessagesThumbs(paramAnonymous2TLObject.messages);
                MessagesQuery.broadcastReplyMessages(paramAnonymous2TLObject.messages, MessagesQuery.7.this.val$replyMessageOwners, paramAnonymous2TLObject.users, paramAnonymous2TLObject.chats, MessagesQuery.7.this.val$dialogId, false);
                MessagesStorage.getInstance().putUsersAndChats(paramAnonymous2TLObject.users, paramAnonymous2TLObject.chats, true, true);
                MessagesQuery.saveReplyMessages(MessagesQuery.7.this.val$replyMessageOwners, paramAnonymous2TLObject.messages);
              }
            }
          });
          return;
        }
        Object localObject = new TLRPC.TL_messages_getMessages();
        ((TLRPC.TL_messages_getMessages)localObject).id = localHashMap;
        ConnectionsManager.getInstance().sendRequest((TLObject)localObject, new RequestDelegate()
        {
          public void run(TLObject paramAnonymous2TLObject, TLRPC.TL_error paramAnonymous2TL_error)
          {
            if (paramAnonymous2TL_error == null)
            {
              paramAnonymous2TLObject = (TLRPC.messages_Messages)paramAnonymous2TLObject;
              MessagesQuery.removeEmptyMessages(paramAnonymous2TLObject.messages);
              ImageLoader.saveMessagesThumbs(paramAnonymous2TLObject.messages);
              MessagesQuery.broadcastReplyMessages(paramAnonymous2TLObject.messages, MessagesQuery.7.this.val$replyMessageOwners, paramAnonymous2TLObject.users, paramAnonymous2TLObject.chats, MessagesQuery.7.this.val$dialogId, false);
              MessagesStorage.getInstance().putUsersAndChats(paramAnonymous2TLObject.users, paramAnonymous2TLObject.chats, true, true);
              MessagesQuery.saveReplyMessages(MessagesQuery.7.this.val$replyMessageOwners, paramAnonymous2TLObject.messages);
            }
          }
        });
      }
    });
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
    int j = paramArrayList.size();
    int i = 0;
    while (i < j)
    {
      TLRPC.MessageEntity localMessageEntity = (TLRPC.MessageEntity)paramArrayList.get(i);
      if (localMessageEntity.offset > paramInt1) {
        localMessageEntity.offset -= paramInt2;
      }
      i += 1;
    }
  }
  
  private static void savePinnedMessage(TLRPC.Message paramMessage)
  {
    MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
    {
      public void run()
      {
        try
        {
          MessagesStorage.getInstance().getDatabase().beginTransaction();
          SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.getInstance().getDatabase().executeFast("REPLACE INTO chat_pinned VALUES(?, ?, ?)");
          NativeByteBuffer localNativeByteBuffer = new NativeByteBuffer(this.val$result.getObjectSize());
          this.val$result.serializeToStream(localNativeByteBuffer);
          localSQLitePreparedStatement.requery();
          localSQLitePreparedStatement.bindInteger(1, this.val$result.to_id.channel_id);
          localSQLitePreparedStatement.bindInteger(2, this.val$result.id);
          localSQLitePreparedStatement.bindByteBuffer(3, localNativeByteBuffer);
          localSQLitePreparedStatement.step();
          localNativeByteBuffer.reuse();
          localSQLitePreparedStatement.dispose();
          MessagesStorage.getInstance().getDatabase().commitTransaction();
          return;
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
        }
      }
    });
  }
  
  private static void saveReplyMessages(final HashMap<Integer, ArrayList<MessageObject>> paramHashMap, ArrayList<TLRPC.Message> paramArrayList)
  {
    MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
    {
      public void run()
      {
        for (;;)
        {
          int i;
          try
          {
            MessagesStorage.getInstance().getDatabase().beginTransaction();
            SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.getInstance().getDatabase().executeFast("UPDATE messages SET replydata = ? WHERE mid = ?");
            i = 0;
            if (i < this.val$result.size())
            {
              Object localObject = (TLRPC.Message)this.val$result.get(i);
              ArrayList localArrayList = (ArrayList)paramHashMap.get(Integer.valueOf(((TLRPC.Message)localObject).id));
              if (localArrayList != null)
              {
                NativeByteBuffer localNativeByteBuffer = new NativeByteBuffer(((TLRPC.Message)localObject).getObjectSize());
                ((TLRPC.Message)localObject).serializeToStream(localNativeByteBuffer);
                int j = 0;
                if (j < localArrayList.size())
                {
                  localObject = (MessageObject)localArrayList.get(j);
                  localSQLitePreparedStatement.requery();
                  long l2 = ((MessageObject)localObject).getId();
                  long l1 = l2;
                  if (((MessageObject)localObject).messageOwner.to_id.channel_id != 0) {
                    l1 = l2 | ((MessageObject)localObject).messageOwner.to_id.channel_id << 32;
                  }
                  localSQLitePreparedStatement.bindByteBuffer(1, localNativeByteBuffer);
                  localSQLitePreparedStatement.bindLong(2, l1);
                  localSQLitePreparedStatement.step();
                  j += 1;
                  continue;
                }
                localNativeByteBuffer.reuse();
              }
            }
            else
            {
              localSQLitePreparedStatement.dispose();
              MessagesStorage.getInstance().getDatabase().commitTransaction();
              return;
            }
          }
          catch (Exception localException)
          {
            FileLog.e(localException);
            return;
          }
          i += 1;
        }
      }
    });
  }
  
  public static void sortEntities(ArrayList<TLRPC.MessageEntity> paramArrayList)
  {
    Collections.sort(paramArrayList, entityComparator);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/query/MessagesQuery.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */