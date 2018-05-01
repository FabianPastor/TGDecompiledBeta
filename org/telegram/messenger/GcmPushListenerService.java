package org.telegram.messenger;

import android.text.TextUtils;
import android.util.Base64;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageAction;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.TL_message;
import org.telegram.tgnet.TLRPC.TL_messageActionPinMessage;
import org.telegram.tgnet.TLRPC.TL_messageMediaEmpty;
import org.telegram.tgnet.TLRPC.TL_peerChannel;
import org.telegram.tgnet.TLRPC.TL_peerChat;
import org.telegram.tgnet.TLRPC.TL_peerUser;
import org.telegram.tgnet.TLRPC.TL_updateReadChannelInbox;
import org.telegram.tgnet.TLRPC.TL_updateReadHistoryInbox;
import org.telegram.tgnet.TLRPC.TL_updateServiceNotification;
import org.telegram.tgnet.TLRPC.TL_updates;

public class GcmPushListenerService
  extends FirebaseMessagingService
{
  public static final int NOTIFICATION_ID = 1;
  
  private void onDecryptError()
  {
    for (int i = 0; i < 3; i++) {
      if (UserConfig.getInstance(i).isClientActivated())
      {
        ConnectionsManager.onInternalPushReceived(i);
        ConnectionsManager.getInstance(i).resumeNetworkMaybe();
      }
    }
  }
  
  public void onMessageReceived(RemoteMessage paramRemoteMessage)
  {
    String str = paramRemoteMessage.getFrom();
    final Map localMap = paramRemoteMessage.getData();
    final long l = paramRemoteMessage.getSentTime();
    if (BuildVars.LOGS_ENABLED) {
      FileLog.d("GCM received data: " + localMap + " from: " + str);
    }
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        ApplicationLoader.postInitApplication();
        Utilities.stageQueue.postRunnable(new Runnable()
        {
          public void run()
          {
            Object localObject1 = null;
            Object localObject2 = null;
            try
            {
              Object localObject3 = GcmPushListenerService.1.this.val$data.get("p");
              if (!(localObject3 instanceof String)) {
                GcmPushListenerService.this.onDecryptError();
              }
              for (;;)
              {
                label36:
                return;
                localObject5 = Base64.decode((String)localObject3, 8);
                localObject3 = new org/telegram/tgnet/NativeByteBuffer;
                ((NativeByteBuffer)localObject3).<init>(localObject5.length);
                ((NativeByteBuffer)localObject3).writeBytes((byte[])localObject5);
                ((NativeByteBuffer)localObject3).position(0);
                if (SharedConfig.pushAuthKeyId == null)
                {
                  SharedConfig.pushAuthKeyId = new byte[8];
                  localObject6 = Utilities.computeSHA1(SharedConfig.pushAuthKey);
                  System.arraycopy(localObject6, localObject6.length - 8, SharedConfig.pushAuthKeyId, 0, 8);
                }
                localObject6 = new byte[8];
                ((NativeByteBuffer)localObject3).readBytes((byte[])localObject6, true);
                if (!Arrays.equals(SharedConfig.pushAuthKeyId, (byte[])localObject6))
                {
                  GcmPushListenerService.this.onDecryptError();
                }
                else
                {
                  localObject7 = new byte[16];
                  ((NativeByteBuffer)localObject3).readBytes((byte[])localObject7, true);
                  localObject6 = MessageKeyData.generateMessageKeyData(SharedConfig.pushAuthKey, (byte[])localObject7, true, 2);
                  Utilities.aesIgeEncryption(((NativeByteBuffer)localObject3).buffer, ((MessageKeyData)localObject6).aesKey, ((MessageKeyData)localObject6).aesIv, false, false, 24, localObject5.length - 24);
                  if (!Utilities.arraysEquals((byte[])localObject7, 0, Utilities.computeSHA256(SharedConfig.pushAuthKey, 96, 32, ((NativeByteBuffer)localObject3).buffer, 24, ((NativeByteBuffer)localObject3).buffer.limit()), 8))
                  {
                    GcmPushListenerService.this.onDecryptError();
                  }
                  else
                  {
                    localObject5 = new byte[((NativeByteBuffer)localObject3).readInt32(true)];
                    ((NativeByteBuffer)localObject3).readBytes((byte[])localObject5, true);
                    localObject3 = new java/lang/String;
                    ((String)localObject3).<init>((byte[])localObject5, "UTF-8");
                    localObject6 = new org/json/JSONObject;
                    ((JSONObject)localObject6).<init>((String)localObject3);
                    localObject7 = ((JSONObject)localObject6).getJSONObject("custom");
                    if (((JSONObject)localObject6).has("user_id"))
                    {
                      localObject3 = ((JSONObject)localObject6).get("user_id");
                      label310:
                      if (localObject3 != null) {
                        break label837;
                      }
                      i = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
                      label325:
                      j = UserConfig.selectedAccount;
                      k = 0;
                      label333:
                      m = j;
                      if (k < 3)
                      {
                        m = UserConfig.getInstance(k).getClientUserId();
                        if (m != i) {
                          break label892;
                        }
                        m = k;
                      }
                      k = m;
                      localObject1 = localObject2;
                    }
                    try
                    {
                      if (UserConfig.getInstance(k).isClientActivated())
                      {
                        localObject1 = localObject2;
                        if (((JSONObject)localObject6).has("loc_key"))
                        {
                          localObject1 = localObject2;
                          localObject5 = ((JSONObject)localObject6).getString("loc_key");
                          label404:
                          localObject1 = localObject5;
                          GcmPushListenerService.1.this.val$data.get("google.sent_time");
                          i = -1;
                          localObject1 = localObject5;
                          switch (((String)localObject5).hashCode())
                          {
                          }
                        }
                        for (;;)
                        {
                          switch (i)
                          {
                          default: 
                            l = 0L;
                            localObject1 = localObject5;
                            if (!((JSONObject)localObject7).has("channel_id")) {
                              break label1259;
                            }
                            localObject1 = localObject5;
                            j = ((JSONObject)localObject7).getInt("channel_id");
                            l = -j;
                            label518:
                            localObject1 = localObject5;
                            if (!((JSONObject)localObject7).has("from_id")) {
                              break label1265;
                            }
                            localObject1 = localObject5;
                            n = ((JSONObject)localObject7).getInt("from_id");
                            l = n;
                            label548:
                            localObject1 = localObject5;
                            if (!((JSONObject)localObject7).has("chat_id")) {
                              break label1271;
                            }
                            localObject1 = localObject5;
                            i1 = ((JSONObject)localObject7).getInt("chat_id");
                            l = -i1;
                            label579:
                            if (l == 0L) {
                              break label36;
                            }
                            localObject1 = localObject5;
                            if (!((JSONObject)localObject6).has("badge")) {
                              break label1277;
                            }
                            localObject1 = localObject5;
                            i = ((JSONObject)localObject6).getInt("badge");
                            label611:
                            if (i == 0) {
                              break label9215;
                            }
                            localObject1 = localObject5;
                            i2 = ((JSONObject)localObject7).getInt("msg_id");
                            localObject1 = localObject5;
                            localObject2 = (Integer)MessagesController.getInstance(k).dialogs_read_inbox_max.get(Long.valueOf(l));
                            localObject3 = localObject2;
                            if (localObject2 == null)
                            {
                              localObject1 = localObject5;
                              localObject3 = Integer.valueOf(MessagesStorage.getInstance(k).getDialogReadMax(false, l));
                              localObject1 = localObject5;
                              MessagesController.getInstance(m).dialogs_read_inbox_max.put(Long.valueOf(l), localObject3);
                            }
                            localObject1 = localObject5;
                            if (i2 <= ((Integer)localObject3).intValue()) {
                              break label36;
                            }
                            localObject1 = localObject5;
                            if (!((JSONObject)localObject7).has("chat_from_id")) {
                              break label1283;
                            }
                            localObject1 = localObject5;
                            i3 = ((JSONObject)localObject7).getInt("chat_from_id");
                            label733:
                            localObject1 = localObject5;
                            if (!((JSONObject)localObject7).has("mention")) {
                              break label1289;
                            }
                            localObject1 = localObject5;
                            if (((JSONObject)localObject7).getInt("mention") == 0) {
                              break label1289;
                            }
                            bool1 = true;
                            label762:
                            localObject1 = localObject5;
                            if (!((JSONObject)localObject6).has("loc_args")) {
                              break label1295;
                            }
                            localObject1 = localObject5;
                            localObject2 = ((JSONObject)localObject6).getJSONArray("loc_args");
                            localObject1 = localObject5;
                            localObject3 = new String[((JSONArray)localObject2).length()];
                            for (i = 0;; i++)
                            {
                              localObject1 = localObject5;
                              localObject8 = localObject3;
                              if (i >= localObject3.length) {
                                break;
                              }
                              localObject1 = localObject5;
                              localObject3[i] = ((JSONArray)localObject2).getString(i);
                            }
                            localObject3 = null;
                            break label310;
                            label837:
                            if ((localObject3 instanceof Integer))
                            {
                              i = ((Integer)localObject3).intValue();
                              break label325;
                            }
                            if ((localObject3 instanceof String))
                            {
                              i = Utilities.parseInt((String)localObject3).intValue();
                              break label325;
                            }
                            i = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
                            break label325;
                            label892:
                            k++;
                            break label333;
                            localObject5 = "";
                            break label404;
                            localObject1 = localObject5;
                            if (((String)localObject5).equals("DC_UPDATE"))
                            {
                              i = 0;
                              continue;
                              localObject1 = localObject5;
                              if (((String)localObject5).equals("MESSAGE_ANNOUNCEMENT")) {
                                i = 1;
                              }
                            }
                            break;
                          }
                        }
                        localObject1 = localObject5;
                        i = ((JSONObject)localObject7).getInt("dc");
                        localObject1 = localObject5;
                        localObject3 = ((JSONObject)localObject7).getString("addr").split(":");
                        localObject1 = localObject5;
                        if (localObject3.length == 2)
                        {
                          localObject2 = localObject3[0];
                          localObject1 = localObject5;
                          m = Integer.parseInt(localObject3[1]);
                          localObject1 = localObject5;
                          ConnectionsManager.getInstance(k).applyDatacenterAddress(i, (String)localObject2, m);
                          localObject1 = localObject5;
                          ConnectionsManager.getInstance(k).resumeNetworkMaybe();
                        }
                      }
                    }
                    catch (Throwable localThrowable1) {}
                  }
                }
              }
            }
            catch (Throwable localThrowable2)
            {
              for (;;)
              {
                Object localObject5;
                Object localObject6;
                Object localObject7;
                int i;
                int j;
                int m;
                long l;
                int n;
                int i1;
                int i2;
                int i3;
                boolean bool1;
                Object localObject8;
                label1259:
                label1265:
                label1271:
                label1277:
                label1283:
                label1289:
                label1295:
                label1345:
                label1358:
                label2132:
                label2568:
                label2602:
                label2734:
                label9104:
                label9109:
                label9215:
                label9362:
                int k = -1;
              }
            }
            if (k != -1)
            {
              ConnectionsManager.onInternalPushReceived(k);
              ConnectionsManager.getInstance(k).resumeNetworkMaybe();
            }
            for (;;)
            {
              if (BuildVars.LOGS_ENABLED) {
                FileLog.e("error in loc_key = " + (String)localObject1);
              }
              FileLog.e(localThrowable1);
              break;
              localObject1 = localObject5;
              Object localObject4 = new org/telegram/tgnet/TLRPC$TL_updateServiceNotification;
              localObject1 = localObject5;
              ((TLRPC.TL_updateServiceNotification)localObject4).<init>();
              localObject1 = localObject5;
              ((TLRPC.TL_updateServiceNotification)localObject4).popup = false;
              localObject1 = localObject5;
              ((TLRPC.TL_updateServiceNotification)localObject4).flags = 2;
              localObject1 = localObject5;
              ((TLRPC.TL_updateServiceNotification)localObject4).inbox_date = ((int)(GcmPushListenerService.1.this.val$time / 1000L));
              localObject1 = localObject5;
              ((TLRPC.TL_updateServiceNotification)localObject4).message = ((JSONObject)localObject6).getString("message");
              localObject1 = localObject5;
              ((TLRPC.TL_updateServiceNotification)localObject4).type = "announcement";
              localObject1 = localObject5;
              localObject2 = new org/telegram/tgnet/TLRPC$TL_messageMediaEmpty;
              localObject1 = localObject5;
              ((TLRPC.TL_messageMediaEmpty)localObject2).<init>();
              localObject1 = localObject5;
              ((TLRPC.TL_updateServiceNotification)localObject4).media = ((TLRPC.MessageMedia)localObject2);
              localObject1 = localObject5;
              localObject2 = new org/telegram/tgnet/TLRPC$TL_updates;
              localObject1 = localObject5;
              ((TLRPC.TL_updates)localObject2).<init>();
              localObject1 = localObject5;
              ((TLRPC.TL_updates)localObject2).updates.add(localObject4);
              localObject1 = localObject5;
              localObject4 = Utilities.stageQueue;
              localObject1 = localObject5;
              localObject6 = new org/telegram/messenger/GcmPushListenerService$1$1$1;
              localObject1 = localObject5;
              ((1)localObject6).<init>(this, m, (TLRPC.TL_updates)localObject2);
              localObject1 = localObject5;
              ((DispatchQueue)localObject4).postRunnable((Runnable)localObject6);
              localObject1 = localObject5;
              ConnectionsManager.getInstance(k).resumeNetworkMaybe();
              break;
              j = 0;
              break label518;
              n = 0;
              break label548;
              i1 = 0;
              break label579;
              i = 0;
              break label611;
              i3 = 0;
              break label733;
              bool1 = false;
              break label762;
              localObject8 = null;
              Object localObject9 = null;
              Object localObject10 = null;
              localObject4 = localObject8[0];
              localObject2 = null;
              boolean bool2 = false;
              int i4 = 0;
              i = 0;
              boolean bool3 = false;
              localObject1 = localObject5;
              int i5;
              boolean bool4;
              if (((String)localObject5).startsWith("CHAT_")) {
                if (j != 0)
                {
                  m = 1;
                  localObject7 = localObject8[1];
                  i5 = i;
                  localObject6 = localObject4;
                  localObject1 = localObject5;
                  if (BuildVars.LOGS_ENABLED)
                  {
                    localObject1 = localObject5;
                    localObject4 = new java/lang/StringBuilder;
                    localObject1 = localObject5;
                    ((StringBuilder)localObject4).<init>();
                    localObject1 = localObject5;
                    FileLog.d("GCM received message notification " + (String)localObject5 + " for dialogId = " + l + " mid = " + i2);
                  }
                  i = -1;
                  localObject1 = localObject5;
                  switch (((String)localObject5).hashCode())
                  {
                  default: 
                    localObject4 = localObject9;
                    bool4 = bool2;
                    localObject2 = localObject10;
                    switch (i)
                    {
                    default: 
                      localObject1 = localObject5;
                      localObject4 = localObject9;
                      bool4 = bool2;
                      localObject2 = localObject10;
                      if (BuildVars.LOGS_ENABLED)
                      {
                        localObject1 = localObject5;
                        localObject4 = new java/lang/StringBuilder;
                        localObject1 = localObject5;
                        ((StringBuilder)localObject4).<init>();
                        localObject1 = localObject5;
                        FileLog.w("unhandled loc_key = " + (String)localObject5);
                        localObject2 = localObject10;
                        bool4 = bool2;
                        localObject4 = localObject9;
                      }
                    case 77: 
                    case 78: 
                    case 79: 
                    case 80: 
                    case 81: 
                    case 82: 
                    case 83: 
                    case 84: 
                    case 85: 
                      if (localObject4 != null)
                      {
                        localObject1 = localObject5;
                        localObject8 = new org/telegram/tgnet/TLRPC$TL_message;
                        localObject1 = localObject5;
                        ((TLRPC.TL_message)localObject8).<init>();
                        localObject1 = localObject5;
                        ((TLRPC.TL_message)localObject8).id = i2;
                        if (localObject2 == null) {
                          break label9104;
                        }
                        localObject1 = localObject5;
                        ((TLRPC.TL_message)localObject8).message = ((String)localObject2);
                        localObject1 = localObject5;
                        ((TLRPC.TL_message)localObject8).date = ((int)(GcmPushListenerService.1.this.val$time / 1000L));
                        if (i5 != 0)
                        {
                          localObject1 = localObject5;
                          localObject2 = new org/telegram/tgnet/TLRPC$TL_messageActionPinMessage;
                          localObject1 = localObject5;
                          ((TLRPC.TL_messageActionPinMessage)localObject2).<init>();
                          localObject1 = localObject5;
                          ((TLRPC.TL_message)localObject8).action = ((TLRPC.MessageAction)localObject2);
                        }
                        if (m != 0)
                        {
                          localObject1 = localObject5;
                          ((TLRPC.TL_message)localObject8).flags |= 0x80000000;
                        }
                        if (j == 0) {
                          break label9109;
                        }
                        localObject1 = localObject5;
                        localObject2 = new org/telegram/tgnet/TLRPC$TL_peerChannel;
                        localObject1 = localObject5;
                        ((TLRPC.TL_peerChannel)localObject2).<init>();
                        localObject1 = localObject5;
                        ((TLRPC.TL_message)localObject8).to_id = ((TLRPC.Peer)localObject2);
                        localObject1 = localObject5;
                        ((TLRPC.TL_message)localObject8).to_id.channel_id = j;
                        localObject1 = localObject5;
                        ((TLRPC.TL_message)localObject8).dialog_id = (-j);
                        localObject1 = localObject5;
                        ((TLRPC.TL_message)localObject8).from_id = i3;
                        localObject1 = localObject5;
                        ((TLRPC.TL_message)localObject8).mentioned = bool1;
                        localObject1 = localObject5;
                        localObject2 = new org/telegram/messenger/MessageObject;
                        localObject1 = localObject5;
                        ((MessageObject)localObject2).<init>(k, (TLRPC.Message)localObject8, (String)localObject4, (String)localObject7, (String)localObject6, bool4, bool3);
                        localObject1 = localObject5;
                        localObject4 = new java/util/ArrayList;
                        localObject1 = localObject5;
                        ((ArrayList)localObject4).<init>();
                        localObject1 = localObject5;
                        ((ArrayList)localObject4).add(localObject2);
                        localObject1 = localObject5;
                        NotificationsController.getInstance(k).processNewMessages((ArrayList)localObject4, true, true);
                      }
                      break;
                    }
                  }
                }
              }
              for (;;)
              {
                localObject1 = localObject5;
                ConnectionsManager.onInternalPushReceived(k);
                localObject1 = localObject5;
                ConnectionsManager.getInstance(k).resumeNetworkMaybe();
                break;
                m = 0;
                break label1345;
                localObject1 = localObject5;
                if (((String)localObject5).startsWith("PINNED_"))
                {
                  if (i3 != 0) {}
                  for (m = 1;; m = 0)
                  {
                    i5 = 1;
                    localObject7 = localObject4;
                    localObject6 = localObject2;
                    break;
                  }
                }
                localObject1 = localObject5;
                localObject7 = localObject4;
                localObject6 = localObject2;
                i5 = i;
                m = i4;
                if (!((String)localObject5).startsWith("CHANNEL_")) {
                  break label1358;
                }
                bool3 = true;
                localObject7 = localObject4;
                localObject6 = localObject2;
                i5 = i;
                m = i4;
                break label1358;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("MESSAGE_TEXT")) {
                  break label2132;
                }
                i = 0;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("MESSAGE_NOTEXT")) {
                  break label2132;
                }
                i = 1;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("MESSAGE_PHOTO")) {
                  break label2132;
                }
                i = 2;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("MESSAGE_PHOTO_SECRET")) {
                  break label2132;
                }
                i = 3;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("MESSAGE_VIDEO")) {
                  break label2132;
                }
                i = 4;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("MESSAGE_VIDEO_SECRET")) {
                  break label2132;
                }
                i = 5;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("MESSAGE_SCREENSHOT")) {
                  break label2132;
                }
                i = 6;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("MESSAGE_ROUND")) {
                  break label2132;
                }
                i = 7;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("MESSAGE_DOC")) {
                  break label2132;
                }
                i = 8;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("MESSAGE_STICKER")) {
                  break label2132;
                }
                i = 9;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("MESSAGE_AUDIO")) {
                  break label2132;
                }
                i = 10;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("MESSAGE_CONTACT")) {
                  break label2132;
                }
                i = 11;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("MESSAGE_GEO")) {
                  break label2132;
                }
                i = 12;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("MESSAGE_GEOLIVE")) {
                  break label2132;
                }
                i = 13;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("MESSAGE_GIF")) {
                  break label2132;
                }
                i = 14;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("MESSAGE_GAME")) {
                  break label2132;
                }
                i = 15;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("MESSAGE_INVOICE")) {
                  break label2132;
                }
                i = 16;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("MESSAGE_FWDS")) {
                  break label2132;
                }
                i = 17;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("MESSAGE_PHOTOS")) {
                  break label2132;
                }
                i = 18;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("MESSAGES")) {
                  break label2132;
                }
                i = 19;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("CHANNEL_MESSAGE_TEXT")) {
                  break label2132;
                }
                i = 20;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("CHANNEL_MESSAGE_NOTEXT")) {
                  break label2132;
                }
                i = 21;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("CHANNEL_MESSAGE_PHOTO")) {
                  break label2132;
                }
                i = 22;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("CHANNEL_MESSAGE_VIDEO")) {
                  break label2132;
                }
                i = 23;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("CHANNEL_MESSAGE_ROUND")) {
                  break label2132;
                }
                i = 24;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("CHANNEL_MESSAGE_DOC")) {
                  break label2132;
                }
                i = 25;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("CHANNEL_MESSAGE_STICKER")) {
                  break label2132;
                }
                i = 26;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("CHANNEL_MESSAGE_AUDIO")) {
                  break label2132;
                }
                i = 27;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("CHANNEL_MESSAGE_CONTACT")) {
                  break label2132;
                }
                i = 28;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("CHANNEL_MESSAGE_GEO")) {
                  break label2132;
                }
                i = 29;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("CHANNEL_MESSAGE_GEOLIVE")) {
                  break label2132;
                }
                i = 30;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("CHANNEL_MESSAGE_GIF")) {
                  break label2132;
                }
                i = 31;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("CHANNEL_MESSAGE_GAME")) {
                  break label2132;
                }
                i = 32;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("CHANNEL_MESSAGE_FWDS")) {
                  break label2132;
                }
                i = 33;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("CHANNEL_MESSAGE_PHOTOS")) {
                  break label2132;
                }
                i = 34;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("CHANNEL_MESSAGES")) {
                  break label2132;
                }
                i = 35;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("CHAT_MESSAGE_TEXT")) {
                  break label2132;
                }
                i = 36;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("CHAT_MESSAGE_NOTEXT")) {
                  break label2132;
                }
                i = 37;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("CHAT_MESSAGE_PHOTO")) {
                  break label2132;
                }
                i = 38;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("CHAT_MESSAGE_VIDEO")) {
                  break label2132;
                }
                i = 39;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("CHAT_MESSAGE_ROUND")) {
                  break label2132;
                }
                i = 40;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("CHAT_MESSAGE_DOC")) {
                  break label2132;
                }
                i = 41;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("CHAT_MESSAGE_STICKER")) {
                  break label2132;
                }
                i = 42;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("CHAT_MESSAGE_AUDIO")) {
                  break label2132;
                }
                i = 43;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("CHAT_MESSAGE_CONTACT")) {
                  break label2132;
                }
                i = 44;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("CHAT_MESSAGE_GEO")) {
                  break label2132;
                }
                i = 45;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("CHAT_MESSAGE_GEOLIVE")) {
                  break label2132;
                }
                i = 46;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("CHAT_MESSAGE_GIF")) {
                  break label2132;
                }
                i = 47;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("CHAT_MESSAGE_GAME")) {
                  break label2132;
                }
                i = 48;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("CHAT_MESSAGE_INVOICE")) {
                  break label2132;
                }
                i = 49;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("CHAT_CREATED")) {
                  break label2132;
                }
                i = 50;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("CHAT_TITLE_EDITED")) {
                  break label2132;
                }
                i = 51;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("CHAT_PHOTO_EDITED")) {
                  break label2132;
                }
                i = 52;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("CHAT_ADD_MEMBER")) {
                  break label2132;
                }
                i = 53;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("CHAT_ADD_YOU")) {
                  break label2132;
                }
                i = 54;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("CHAT_DELETE_MEMBER")) {
                  break label2132;
                }
                i = 55;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("CHAT_DELETE_YOU")) {
                  break label2132;
                }
                i = 56;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("CHAT_LEFT")) {
                  break label2132;
                }
                i = 57;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("CHAT_RETURNED")) {
                  break label2132;
                }
                i = 58;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("CHAT_JOINED")) {
                  break label2132;
                }
                i = 59;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("CHAT_MESSAGE_FWDS")) {
                  break label2132;
                }
                i = 60;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("CHAT_MESSAGE_PHOTOS")) {
                  break label2132;
                }
                i = 61;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("CHAT_MESSAGES")) {
                  break label2132;
                }
                i = 62;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("PINNED_TEXT")) {
                  break label2132;
                }
                i = 63;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("PINNED_NOTEXT")) {
                  break label2132;
                }
                i = 64;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("PINNED_PHOTO")) {
                  break label2132;
                }
                i = 65;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("PINNED_VIDEO")) {
                  break label2132;
                }
                i = 66;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("PINNED_ROUND")) {
                  break label2132;
                }
                i = 67;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("PINNED_DOC")) {
                  break label2132;
                }
                i = 68;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("PINNED_STICKER")) {
                  break label2132;
                }
                i = 69;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("PINNED_AUDIO")) {
                  break label2132;
                }
                i = 70;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("PINNED_CONTACT")) {
                  break label2132;
                }
                i = 71;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("PINNED_GEO")) {
                  break label2132;
                }
                i = 72;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("PINNED_GEOLIVE")) {
                  break label2132;
                }
                i = 73;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("PINNED_GAME")) {
                  break label2132;
                }
                i = 74;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("PINNED_INVOICE")) {
                  break label2132;
                }
                i = 75;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("PINNED_GIF")) {
                  break label2132;
                }
                i = 76;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("CONTACT_JOINED")) {
                  break label2132;
                }
                i = 77;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("AUTH_UNKNOWN")) {
                  break label2132;
                }
                i = 78;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("AUTH_REGION")) {
                  break label2132;
                }
                i = 79;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("ENCRYPTION_REQUEST")) {
                  break label2132;
                }
                i = 80;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("ENCRYPTION_ACCEPT")) {
                  break label2132;
                }
                i = 81;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("ENCRYPTED_MESSAGE")) {
                  break label2132;
                }
                i = 82;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("LOCKED_MESSAGE")) {
                  break label2132;
                }
                i = 83;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("PHONE_CALL_REQUEST")) {
                  break label2132;
                }
                i = 84;
                break label2132;
                localObject1 = localObject5;
                if (!((String)localObject5).equals("PHONE_CALL_MISSED")) {
                  break label2132;
                }
                i = 85;
                break label2132;
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("NotificationMessageText", NUM, new Object[] { localObject8[0], localObject8[1] });
                localObject2 = localObject8[1];
                bool4 = bool2;
                break label2568;
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("NotificationMessageNoText", NUM, new Object[] { localObject8[0] });
                localObject2 = "";
                bool4 = bool2;
                break label2568;
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("NotificationMessagePhoto", NUM, new Object[] { localObject8[0] });
                localObject1 = localObject5;
                localObject2 = LocaleController.getString("AttachPhoto", NUM);
                bool4 = bool2;
                break label2568;
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("NotificationMessageSDPhoto", NUM, new Object[] { localObject8[0] });
                localObject1 = localObject5;
                localObject2 = LocaleController.getString("AttachPhoto", NUM);
                bool4 = bool2;
                break label2568;
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("NotificationMessageVideo", NUM, new Object[] { localObject8[0] });
                localObject1 = localObject5;
                localObject2 = LocaleController.getString("AttachVideo", NUM);
                bool4 = bool2;
                break label2568;
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("NotificationMessageSDVideo", NUM, new Object[] { localObject8[0] });
                localObject1 = localObject5;
                localObject2 = LocaleController.getString("AttachVideo", NUM);
                bool4 = bool2;
                break label2568;
                localObject1 = localObject5;
                localObject4 = LocaleController.getString("ActionTakeScreenshoot", NUM).replace("un1", localObject8[0]);
                bool4 = bool2;
                localObject2 = localObject10;
                break label2568;
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("NotificationMessageRound", NUM, new Object[] { localObject8[0] });
                localObject1 = localObject5;
                localObject2 = LocaleController.getString("AttachRound", NUM);
                bool4 = bool2;
                break label2568;
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("NotificationMessageDocument", NUM, new Object[] { localObject8[0] });
                localObject1 = localObject5;
                localObject2 = LocaleController.getString("AttachDocument", NUM);
                bool4 = bool2;
                break label2568;
                localObject1 = localObject5;
                if (localObject8.length > 1)
                {
                  localObject1 = localObject5;
                  if (!TextUtils.isEmpty(localObject8[1])) {
                    localObject1 = localObject5;
                  }
                }
                for (localObject4 = LocaleController.formatString("NotificationMessageStickerEmoji", NUM, new Object[] { localObject8[0], localObject8[1] });; localObject4 = LocaleController.formatString("NotificationMessageSticker", NUM, new Object[] { localObject8[0] }))
                {
                  localObject1 = localObject5;
                  localObject2 = LocaleController.getString("AttachSticker", NUM);
                  bool4 = bool2;
                  break;
                  localObject1 = localObject5;
                }
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("NotificationMessageAudio", NUM, new Object[] { localObject8[0] });
                localObject1 = localObject5;
                localObject2 = LocaleController.getString("AttachAudio", NUM);
                bool4 = bool2;
                break label2568;
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("NotificationMessageContact", NUM, new Object[] { localObject8[0] });
                localObject1 = localObject5;
                localObject2 = LocaleController.getString("AttachContact", NUM);
                bool4 = bool2;
                break label2568;
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("NotificationMessageMap", NUM, new Object[] { localObject8[0] });
                localObject1 = localObject5;
                localObject2 = LocaleController.getString("AttachLocation", NUM);
                bool4 = bool2;
                break label2568;
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("NotificationMessageLiveLocation", NUM, new Object[] { localObject8[0] });
                localObject1 = localObject5;
                localObject2 = LocaleController.getString("AttachLiveLocation", NUM);
                bool4 = bool2;
                break label2568;
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("NotificationMessageGif", NUM, new Object[] { localObject8[0] });
                localObject1 = localObject5;
                localObject2 = LocaleController.getString("AttachGif", NUM);
                bool4 = bool2;
                break label2568;
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("NotificationMessageGame", NUM, new Object[] { localObject8[0] });
                localObject1 = localObject5;
                localObject2 = LocaleController.getString("AttachGame", NUM);
                bool4 = bool2;
                break label2568;
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("NotificationMessageInvoice", NUM, new Object[] { localObject8[0], localObject8[1] });
                localObject1 = localObject5;
                localObject2 = LocaleController.getString("PaymentInvoice", NUM);
                bool4 = bool2;
                break label2568;
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("NotificationMessageForwardFew", NUM, new Object[] { localObject8[0], LocaleController.formatPluralString("messages", Utilities.parseInt(localObject8[1]).intValue()) });
                bool4 = true;
                localObject2 = localObject10;
                break label2568;
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("NotificationMessageFew", NUM, new Object[] { localObject8[0], LocaleController.formatPluralString("Photos", Utilities.parseInt(localObject8[1]).intValue()) });
                bool4 = true;
                localObject2 = localObject10;
                break label2568;
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("NotificationMessageFew", NUM, new Object[] { localObject8[0], LocaleController.formatPluralString("messages", Utilities.parseInt(localObject8[1]).intValue()) });
                bool4 = true;
                localObject2 = localObject10;
                break label2568;
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("NotificationMessageText", NUM, new Object[] { localObject8[0], localObject8[1] });
                localObject2 = localObject8[1];
                bool4 = bool2;
                break label2568;
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("ChannelMessageNoText", NUM, new Object[] { localObject8[0] });
                localObject2 = "";
                bool4 = bool2;
                break label2568;
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("ChannelMessagePhoto", NUM, new Object[] { localObject8[0] });
                localObject1 = localObject5;
                localObject2 = LocaleController.getString("AttachPhoto", NUM);
                bool4 = bool2;
                break label2568;
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("ChannelMessageVideo", NUM, new Object[] { localObject8[0] });
                localObject1 = localObject5;
                localObject2 = LocaleController.getString("AttachVideo", NUM);
                bool4 = bool2;
                break label2568;
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("ChannelMessageRound", NUM, new Object[] { localObject8[0] });
                localObject1 = localObject5;
                localObject2 = LocaleController.getString("AttachRound", NUM);
                bool4 = bool2;
                break label2568;
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("ChannelMessageDocument", NUM, new Object[] { localObject8[0] });
                localObject1 = localObject5;
                localObject2 = LocaleController.getString("AttachDocument", NUM);
                bool4 = bool2;
                break label2568;
                localObject1 = localObject5;
                if (localObject8.length > 1)
                {
                  localObject1 = localObject5;
                  if (!TextUtils.isEmpty(localObject8[1])) {
                    localObject1 = localObject5;
                  }
                }
                for (localObject4 = LocaleController.formatString("ChannelMessageStickerEmoji", NUM, new Object[] { localObject8[0], localObject8[1] });; localObject4 = LocaleController.formatString("ChannelMessageSticker", NUM, new Object[] { localObject8[0] }))
                {
                  localObject1 = localObject5;
                  localObject2 = LocaleController.getString("AttachSticker", NUM);
                  bool4 = bool2;
                  break;
                  localObject1 = localObject5;
                }
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("ChannelMessageAudio", NUM, new Object[] { localObject8[0] });
                localObject1 = localObject5;
                localObject2 = LocaleController.getString("AttachAudio", NUM);
                bool4 = bool2;
                break label2568;
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("ChannelMessageContact", NUM, new Object[] { localObject8[0] });
                localObject1 = localObject5;
                localObject2 = LocaleController.getString("AttachContact", NUM);
                bool4 = bool2;
                break label2568;
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("ChannelMessageMap", NUM, new Object[] { localObject8[0] });
                localObject1 = localObject5;
                localObject2 = LocaleController.getString("AttachLocation", NUM);
                bool4 = bool2;
                break label2568;
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("ChannelMessageLiveLocation", NUM, new Object[] { localObject8[0] });
                localObject1 = localObject5;
                localObject2 = LocaleController.getString("AttachLiveLocation", NUM);
                bool4 = bool2;
                break label2568;
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("ChannelMessageGIF", NUM, new Object[] { localObject8[0] });
                localObject1 = localObject5;
                localObject2 = LocaleController.getString("AttachGif", NUM);
                bool4 = bool2;
                break label2568;
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("NotificationMessageGame", NUM, new Object[] { localObject8[0] });
                localObject1 = localObject5;
                localObject2 = LocaleController.getString("AttachGame", NUM);
                bool4 = bool2;
                break label2568;
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("ChannelMessageFew", NUM, new Object[] { localObject8[0], LocaleController.formatPluralString("ForwardedMessageCount", Utilities.parseInt(localObject8[1]).intValue()).toLowerCase() });
                bool4 = true;
                localObject2 = localObject10;
                break label2568;
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("ChannelMessageFew", NUM, new Object[] { localObject8[0], LocaleController.formatPluralString("Photos", Utilities.parseInt(localObject8[1]).intValue()) });
                bool4 = true;
                localObject2 = localObject10;
                break label2568;
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("ChannelMessageFew", NUM, new Object[] { localObject8[0], LocaleController.formatPluralString("messages", Utilities.parseInt(localObject8[1]).intValue()) });
                bool4 = true;
                localObject2 = localObject10;
                break label2568;
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("NotificationMessageGroupText", NUM, new Object[] { localObject8[0], localObject8[1], localObject8[2] });
                localObject2 = localObject8[1];
                bool4 = bool2;
                break label2568;
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("NotificationMessageGroupNoText", NUM, new Object[] { localObject8[0], localObject8[1] });
                localObject2 = "";
                bool4 = bool2;
                break label2568;
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("NotificationMessageGroupPhoto", NUM, new Object[] { localObject8[0], localObject8[1] });
                localObject1 = localObject5;
                localObject2 = LocaleController.getString("AttachPhoto", NUM);
                bool4 = bool2;
                break label2568;
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("NotificationMessageGroupVideo", NUM, new Object[] { localObject8[0], localObject8[1] });
                localObject1 = localObject5;
                localObject2 = LocaleController.getString("AttachVideo", NUM);
                bool4 = bool2;
                break label2568;
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("NotificationMessageGroupRound", NUM, new Object[] { localObject8[0], localObject8[1] });
                localObject1 = localObject5;
                localObject2 = LocaleController.getString("AttachRound", NUM);
                bool4 = bool2;
                break label2568;
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("NotificationMessageGroupDocument", NUM, new Object[] { localObject8[0], localObject8[1] });
                localObject1 = localObject5;
                localObject2 = LocaleController.getString("AttachDocument", NUM);
                bool4 = bool2;
                break label2568;
                localObject1 = localObject5;
                if (localObject8.length > 2)
                {
                  localObject1 = localObject5;
                  if (!TextUtils.isEmpty(localObject8[2])) {
                    localObject1 = localObject5;
                  }
                }
                for (localObject4 = LocaleController.formatString("NotificationMessageGroupStickerEmoji", NUM, new Object[] { localObject8[0], localObject8[1], localObject8[2] });; localObject4 = LocaleController.formatString("NotificationMessageGroupSticker", NUM, new Object[] { localObject8[0], localObject8[1] }))
                {
                  localObject1 = localObject5;
                  localObject2 = LocaleController.getString("AttachSticker", NUM);
                  bool4 = bool2;
                  break;
                  localObject1 = localObject5;
                }
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("NotificationMessageGroupAudio", NUM, new Object[] { localObject8[0], localObject8[1] });
                localObject1 = localObject5;
                localObject2 = LocaleController.getString("AttachAudio", NUM);
                bool4 = bool2;
                break label2568;
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("NotificationMessageGroupContact", NUM, new Object[] { localObject8[0], localObject8[1] });
                localObject1 = localObject5;
                localObject2 = LocaleController.getString("AttachContact", NUM);
                bool4 = bool2;
                break label2568;
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("NotificationMessageGroupMap", NUM, new Object[] { localObject8[0], localObject8[1] });
                localObject1 = localObject5;
                localObject2 = LocaleController.getString("AttachLocation", NUM);
                bool4 = bool2;
                break label2568;
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("NotificationMessageGroupLiveLocation", NUM, new Object[] { localObject8[0], localObject8[1] });
                localObject1 = localObject5;
                localObject2 = LocaleController.getString("AttachLiveLocation", NUM);
                bool4 = bool2;
                break label2568;
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("NotificationMessageGroupGif", NUM, new Object[] { localObject8[0], localObject8[1] });
                localObject1 = localObject5;
                localObject2 = LocaleController.getString("AttachGif", NUM);
                bool4 = bool2;
                break label2568;
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("NotificationMessageGroupGame", NUM, new Object[] { localObject8[0], localObject8[1], localObject8[2] });
                localObject1 = localObject5;
                localObject2 = LocaleController.getString("AttachGame", NUM);
                bool4 = bool2;
                break label2568;
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("NotificationMessageGroupInvoice", NUM, new Object[] { localObject8[0], localObject8[1], localObject8[2] });
                localObject1 = localObject5;
                localObject2 = LocaleController.getString("PaymentInvoice", NUM);
                bool4 = bool2;
                break label2568;
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("NotificationInvitedToGroup", NUM, new Object[] { localObject8[0], localObject8[1] });
                bool4 = bool2;
                localObject2 = localObject10;
                break label2568;
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("NotificationEditedGroupName", NUM, new Object[] { localObject8[0], localObject8[1] });
                bool4 = bool2;
                localObject2 = localObject10;
                break label2568;
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("NotificationEditedGroupPhoto", NUM, new Object[] { localObject8[0], localObject8[1] });
                bool4 = bool2;
                localObject2 = localObject10;
                break label2568;
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("NotificationGroupAddMember", NUM, new Object[] { localObject8[0], localObject8[1], localObject8[2] });
                bool4 = bool2;
                localObject2 = localObject10;
                break label2568;
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("NotificationInvitedToGroup", NUM, new Object[] { localObject8[0], localObject8[1] });
                bool4 = bool2;
                localObject2 = localObject10;
                break label2568;
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("NotificationGroupKickMember", NUM, new Object[] { localObject8[0], localObject8[1] });
                bool4 = bool2;
                localObject2 = localObject10;
                break label2568;
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("NotificationGroupKickYou", NUM, new Object[] { localObject8[0], localObject8[1] });
                bool4 = bool2;
                localObject2 = localObject10;
                break label2568;
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("NotificationGroupLeftMember", NUM, new Object[] { localObject8[0], localObject8[1] });
                bool4 = bool2;
                localObject2 = localObject10;
                break label2568;
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("NotificationGroupAddSelf", NUM, new Object[] { localObject8[0], localObject8[1] });
                bool4 = bool2;
                localObject2 = localObject10;
                break label2568;
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("NotificationGroupAddSelfMega", NUM, new Object[] { localObject8[0], localObject8[1] });
                bool4 = bool2;
                localObject2 = localObject10;
                break label2568;
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("NotificationGroupForwardedFew", NUM, new Object[] { localObject8[0], localObject8[1], LocaleController.formatPluralString("messages", Utilities.parseInt(localObject8[2]).intValue()) });
                bool4 = true;
                localObject2 = localObject10;
                break label2568;
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("NotificationGroupFew", NUM, new Object[] { localObject8[0], localObject8[1], LocaleController.formatPluralString("Photos", Utilities.parseInt(localObject8[2]).intValue()) });
                bool4 = true;
                localObject2 = localObject10;
                break label2568;
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("NotificationGroupFew", NUM, new Object[] { localObject8[0], localObject8[1], LocaleController.formatPluralString("messages", Utilities.parseInt(localObject8[2]).intValue()) });
                bool4 = true;
                localObject2 = localObject10;
                break label2568;
                if (i3 != 0)
                {
                  localObject1 = localObject5;
                  localObject4 = LocaleController.formatString("NotificationActionPinnedText", NUM, new Object[] { localObject8[0], localObject8[1], localObject8[2] });
                  bool4 = bool2;
                  localObject2 = localObject10;
                  break label2568;
                }
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, new Object[] { localObject8[0], localObject8[1] });
                bool4 = bool2;
                localObject2 = localObject10;
                break label2568;
                if (i3 != 0)
                {
                  localObject1 = localObject5;
                  localObject4 = LocaleController.formatString("NotificationActionPinnedNoText", NUM, new Object[] { localObject8[0], localObject8[1] });
                  bool4 = bool2;
                  localObject2 = localObject10;
                  break label2568;
                }
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("NotificationActionPinnedNoTextChannel", NUM, new Object[] { localObject8[0] });
                bool4 = bool2;
                localObject2 = localObject10;
                break label2568;
                if (i3 != 0)
                {
                  localObject1 = localObject5;
                  localObject4 = LocaleController.formatString("NotificationActionPinnedPhoto", NUM, new Object[] { localObject8[0], localObject8[1] });
                  bool4 = bool2;
                  localObject2 = localObject10;
                  break label2568;
                }
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("NotificationActionPinnedPhotoChannel", NUM, new Object[] { localObject8[0] });
                bool4 = bool2;
                localObject2 = localObject10;
                break label2568;
                if (i3 != 0)
                {
                  localObject1 = localObject5;
                  localObject4 = LocaleController.formatString("NotificationActionPinnedVideo", NUM, new Object[] { localObject8[0], localObject8[1] });
                  bool4 = bool2;
                  localObject2 = localObject10;
                  break label2568;
                }
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("NotificationActionPinnedVideoChannel", NUM, new Object[] { localObject8[0] });
                bool4 = bool2;
                localObject2 = localObject10;
                break label2568;
                if (i3 != 0)
                {
                  localObject1 = localObject5;
                  localObject4 = LocaleController.formatString("NotificationActionPinnedRound", NUM, new Object[] { localObject8[0], localObject8[1] });
                  bool4 = bool2;
                  localObject2 = localObject10;
                  break label2568;
                }
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("NotificationActionPinnedRoundChannel", NUM, new Object[] { localObject8[0] });
                bool4 = bool2;
                localObject2 = localObject10;
                break label2568;
                if (i3 != 0)
                {
                  localObject1 = localObject5;
                  localObject4 = LocaleController.formatString("NotificationActionPinnedFile", NUM, new Object[] { localObject8[0], localObject8[1] });
                  bool4 = bool2;
                  localObject2 = localObject10;
                  break label2568;
                }
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("NotificationActionPinnedFileChannel", NUM, new Object[] { localObject8[0] });
                bool4 = bool2;
                localObject2 = localObject10;
                break label2568;
                if (i3 != 0)
                {
                  localObject1 = localObject5;
                  if (localObject8.length > 2)
                  {
                    localObject1 = localObject5;
                    if (!TextUtils.isEmpty(localObject8[2]))
                    {
                      localObject1 = localObject5;
                      localObject4 = LocaleController.formatString("NotificationActionPinnedStickerEmoji", NUM, new Object[] { localObject8[0], localObject8[1], localObject8[2] });
                      bool4 = bool2;
                      localObject2 = localObject10;
                      break label2568;
                    }
                  }
                  localObject1 = localObject5;
                  localObject4 = LocaleController.formatString("NotificationActionPinnedSticker", NUM, new Object[] { localObject8[0], localObject8[1] });
                  bool4 = bool2;
                  localObject2 = localObject10;
                  break label2568;
                }
                localObject1 = localObject5;
                if (localObject8.length > 1)
                {
                  localObject1 = localObject5;
                  if (!TextUtils.isEmpty(localObject8[1]))
                  {
                    localObject1 = localObject5;
                    localObject4 = LocaleController.formatString("NotificationActionPinnedStickerEmojiChannel", NUM, new Object[] { localObject8[0], localObject8[1] });
                    bool4 = bool2;
                    localObject2 = localObject10;
                    break label2568;
                  }
                }
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("NotificationActionPinnedStickerChannel", NUM, new Object[] { localObject8[0] });
                bool4 = bool2;
                localObject2 = localObject10;
                break label2568;
                if (i3 != 0)
                {
                  localObject1 = localObject5;
                  localObject4 = LocaleController.formatString("NotificationActionPinnedVoice", NUM, new Object[] { localObject8[0], localObject8[1] });
                  bool4 = bool2;
                  localObject2 = localObject10;
                  break label2568;
                }
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("NotificationActionPinnedVoiceChannel", NUM, new Object[] { localObject8[0] });
                bool4 = bool2;
                localObject2 = localObject10;
                break label2568;
                if (i3 != 0)
                {
                  localObject1 = localObject5;
                  localObject4 = LocaleController.formatString("NotificationActionPinnedContact", NUM, new Object[] { localObject8[0], localObject8[1] });
                  bool4 = bool2;
                  localObject2 = localObject10;
                  break label2568;
                }
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("NotificationActionPinnedContactChannel", NUM, new Object[] { localObject8[0] });
                bool4 = bool2;
                localObject2 = localObject10;
                break label2568;
                if (i3 != 0)
                {
                  localObject1 = localObject5;
                  localObject4 = LocaleController.formatString("NotificationActionPinnedGeo", NUM, new Object[] { localObject8[0], localObject8[1] });
                  bool4 = bool2;
                  localObject2 = localObject10;
                  break label2568;
                }
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("NotificationActionPinnedGeoChannel", NUM, new Object[] { localObject8[0] });
                bool4 = bool2;
                localObject2 = localObject10;
                break label2568;
                if (i3 != 0)
                {
                  localObject1 = localObject5;
                  localObject4 = LocaleController.formatString("NotificationActionPinnedGeoLive", NUM, new Object[] { localObject8[0], localObject8[1] });
                  bool4 = bool2;
                  localObject2 = localObject10;
                  break label2568;
                }
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("NotificationActionPinnedGeoLiveChannel", NUM, new Object[] { localObject8[0] });
                bool4 = bool2;
                localObject2 = localObject10;
                break label2568;
                if (i3 != 0)
                {
                  localObject1 = localObject5;
                  localObject4 = LocaleController.formatString("NotificationActionPinnedGame", NUM, new Object[] { localObject8[0], localObject8[1] });
                  bool4 = bool2;
                  localObject2 = localObject10;
                  break label2568;
                }
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("NotificationActionPinnedGameChannel", NUM, new Object[] { localObject8[0] });
                bool4 = bool2;
                localObject2 = localObject10;
                break label2568;
                if (i3 != 0)
                {
                  localObject1 = localObject5;
                  localObject4 = LocaleController.formatString("NotificationActionPinnedInvoice", NUM, new Object[] { localObject8[0], localObject8[1] });
                  bool4 = bool2;
                  localObject2 = localObject10;
                  break label2568;
                }
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("NotificationActionPinnedInvoiceChannel", NUM, new Object[] { localObject8[0] });
                bool4 = bool2;
                localObject2 = localObject10;
                break label2568;
                if (i3 != 0)
                {
                  localObject1 = localObject5;
                  localObject4 = LocaleController.formatString("NotificationActionPinnedGif", NUM, new Object[] { localObject8[0], localObject8[1] });
                  bool4 = bool2;
                  localObject2 = localObject10;
                  break label2568;
                }
                localObject1 = localObject5;
                localObject4 = LocaleController.formatString("NotificationActionPinnedGifChannel", NUM, new Object[] { localObject8[0] });
                bool4 = bool2;
                localObject2 = localObject10;
                break label2568;
                localObject2 = localObject4;
                break label2602;
                if (i1 != 0)
                {
                  localObject1 = localObject5;
                  localObject2 = new org/telegram/tgnet/TLRPC$TL_peerChat;
                  localObject1 = localObject5;
                  ((TLRPC.TL_peerChat)localObject2).<init>();
                  localObject1 = localObject5;
                  ((TLRPC.TL_message)localObject8).to_id = ((TLRPC.Peer)localObject2);
                  localObject1 = localObject5;
                  ((TLRPC.TL_message)localObject8).to_id.chat_id = i1;
                  localObject1 = localObject5;
                  ((TLRPC.TL_message)localObject8).dialog_id = (-i1);
                  break label2734;
                }
                localObject1 = localObject5;
                localObject2 = new org/telegram/tgnet/TLRPC$TL_peerUser;
                localObject1 = localObject5;
                ((TLRPC.TL_peerUser)localObject2).<init>();
                localObject1 = localObject5;
                ((TLRPC.TL_message)localObject8).to_id = ((TLRPC.Peer)localObject2);
                localObject1 = localObject5;
                ((TLRPC.TL_message)localObject8).to_id.user_id = n;
                localObject1 = localObject5;
                ((TLRPC.TL_message)localObject8).dialog_id = n;
                break label2734;
                localObject1 = localObject5;
                i = ((JSONObject)localObject7).getInt("max_id");
                localObject1 = localObject5;
                localObject4 = new java/util/ArrayList;
                localObject1 = localObject5;
                ((ArrayList)localObject4).<init>();
                localObject1 = localObject5;
                if (BuildVars.LOGS_ENABLED)
                {
                  localObject1 = localObject5;
                  localObject2 = new java/lang/StringBuilder;
                  localObject1 = localObject5;
                  ((StringBuilder)localObject2).<init>();
                  localObject1 = localObject5;
                  FileLog.d("GCM received read notification max_id = " + i + " for dialogId = " + l);
                }
                if (j == 0) {
                  break label9362;
                }
                localObject1 = localObject5;
                localObject2 = new org/telegram/tgnet/TLRPC$TL_updateReadChannelInbox;
                localObject1 = localObject5;
                ((TLRPC.TL_updateReadChannelInbox)localObject2).<init>();
                localObject1 = localObject5;
                ((TLRPC.TL_updateReadChannelInbox)localObject2).channel_id = j;
                localObject1 = localObject5;
                ((TLRPC.TL_updateReadChannelInbox)localObject2).max_id = i;
                localObject1 = localObject5;
                ((ArrayList)localObject4).add(localObject2);
                localObject1 = localObject5;
                MessagesController.getInstance(m).processUpdateArray((ArrayList)localObject4, null, null, false);
              }
              localObject1 = localObject5;
              localObject2 = new org/telegram/tgnet/TLRPC$TL_updateReadHistoryInbox;
              localObject1 = localObject5;
              ((TLRPC.TL_updateReadHistoryInbox)localObject2).<init>();
              if (n != 0)
              {
                localObject1 = localObject5;
                localObject6 = new org/telegram/tgnet/TLRPC$TL_peerUser;
                localObject1 = localObject5;
                ((TLRPC.TL_peerUser)localObject6).<init>();
                localObject1 = localObject5;
                ((TLRPC.TL_updateReadHistoryInbox)localObject2).peer = ((TLRPC.Peer)localObject6);
                localObject1 = localObject5;
                ((TLRPC.TL_updateReadHistoryInbox)localObject2).peer.user_id = n;
              }
              for (;;)
              {
                localObject1 = localObject5;
                ((TLRPC.TL_updateReadHistoryInbox)localObject2).max_id = i;
                localObject1 = localObject5;
                ((ArrayList)localObject4).add(localObject2);
                break;
                localObject1 = localObject5;
                localObject6 = new org/telegram/tgnet/TLRPC$TL_peerChat;
                localObject1 = localObject5;
                ((TLRPC.TL_peerChat)localObject6).<init>();
                localObject1 = localObject5;
                ((TLRPC.TL_updateReadHistoryInbox)localObject2).peer = ((TLRPC.Peer)localObject6);
                localObject1 = localObject5;
                ((TLRPC.TL_updateReadHistoryInbox)localObject2).peer.chat_id = i1;
              }
              GcmPushListenerService.this.onDecryptError();
            }
          }
        });
      }
    });
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/GcmPushListenerService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */