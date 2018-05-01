package org.telegram.messenger;

import android.text.TextUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.Builder;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.CapabilityClient;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.Channel;
import com.google.android.gms.wearable.Channel.GetInputStreamResult;
import com.google.android.gms.wearable.Channel.GetOutputStreamResult;
import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONObject;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatPhoto;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserProfilePhoto;

public class WearDataLayerListenerService
  extends WearableListenerService
{
  private static boolean watchConnected;
  private int currentAccount = UserConfig.selectedAccount;
  
  public static boolean isWatchConnected()
  {
    return watchConnected;
  }
  
  public static void sendMessageToWatch(String paramString1, final byte[] paramArrayOfByte, String paramString2)
  {
    Wearable.getCapabilityClient(ApplicationLoader.applicationContext).getCapability(paramString2, 1).addOnCompleteListener(new OnCompleteListener()
    {
      public void onComplete(Task<CapabilityInfo> paramAnonymousTask)
      {
        Object localObject = (CapabilityInfo)paramAnonymousTask.getResult();
        if (localObject != null)
        {
          paramAnonymousTask = Wearable.getMessageClient(ApplicationLoader.applicationContext);
          localObject = ((CapabilityInfo)localObject).getNodes().iterator();
          while (((Iterator)localObject).hasNext()) {
            paramAnonymousTask.sendMessage(((Node)((Iterator)localObject).next()).getId(), this.val$path, paramArrayOfByte);
          }
        }
      }
    });
  }
  
  public static void updateWatchConnectionState()
  {
    Wearable.getCapabilityClient(ApplicationLoader.applicationContext).getCapability("remote_notifications", 1).addOnCompleteListener(new OnCompleteListener()
    {
      public void onComplete(Task<CapabilityInfo> paramAnonymousTask)
      {
        WearDataLayerListenerService.access$102(false);
        for (;;)
        {
          try
          {
            paramAnonymousTask = (CapabilityInfo)paramAnonymousTask.getResult();
            if (paramAnonymousTask == null) {
              return;
            }
          }
          catch (Exception paramAnonymousTask)
          {
            continue;
          }
          paramAnonymousTask = paramAnonymousTask.getNodes().iterator();
          if (paramAnonymousTask.hasNext()) {
            if (((Node)paramAnonymousTask.next()).isNearby()) {
              WearDataLayerListenerService.access$102(true);
            }
          }
        }
      }
    });
  }
  
  public void onCapabilityChanged(CapabilityInfo paramCapabilityInfo)
  {
    if ("remote_notifications".equals(paramCapabilityInfo.getName()))
    {
      watchConnected = false;
      paramCapabilityInfo = paramCapabilityInfo.getNodes().iterator();
      while (paramCapabilityInfo.hasNext()) {
        if (((Node)paramCapabilityInfo.next()).isNearby()) {
          watchConnected = true;
        }
      }
    }
  }
  
  public void onChannelOpened(Channel paramChannel)
  {
    GoogleApiClient localGoogleApiClient = new GoogleApiClient.Builder(this).addApi(Wearable.API).build();
    if (!localGoogleApiClient.blockingConnect().isSuccess()) {
      if (BuildVars.LOGS_ENABLED) {
        FileLog.e("failed to connect google api client");
      }
    }
    for (;;)
    {
      return;
      Object localObject1 = paramChannel.getPath();
      if (BuildVars.LOGS_ENABLED) {
        FileLog.d("wear channel path: " + (String)localObject1);
      }
      try
      {
        if ("/getCurrentUser".equals(localObject1))
        {
          localObject1 = new java/io/DataOutputStream;
          localObject5 = new java/io/BufferedOutputStream;
          ((BufferedOutputStream)localObject5).<init>(((Channel.GetOutputStreamResult)paramChannel.getOutputStream(localGoogleApiClient).await()).getOutputStream());
          ((DataOutputStream)localObject1).<init>((OutputStream)localObject5);
          if (UserConfig.getInstance(this.currentAccount).isClientActivated())
          {
            localObject6 = UserConfig.getInstance(this.currentAccount).getCurrentUser();
            ((DataOutputStream)localObject1).writeInt(((TLRPC.User)localObject6).id);
            ((DataOutputStream)localObject1).writeUTF(((TLRPC.User)localObject6).first_name);
            ((DataOutputStream)localObject1).writeUTF(((TLRPC.User)localObject6).last_name);
            ((DataOutputStream)localObject1).writeUTF(((TLRPC.User)localObject6).phone);
            if (((TLRPC.User)localObject6).photo != null)
            {
              localObject5 = FileLoader.getPathToAttach(((TLRPC.User)localObject6).photo.photo_small, true);
              localCyclicBarrier = new java/util/concurrent/CyclicBarrier;
              localCyclicBarrier.<init>(2);
              if (!((File)localObject5).exists())
              {
                localObject7 = new org/telegram/messenger/WearDataLayerListenerService$1;
                ((1)localObject7).<init>(this, (File)localObject5, localCyclicBarrier);
                localObject8 = new org/telegram/messenger/WearDataLayerListenerService$2;
                ((2)localObject8).<init>(this, (NotificationCenter.NotificationCenterDelegate)localObject7, (TLRPC.User)localObject6);
                AndroidUtilities.runOnUIThread((Runnable)localObject8);
              }
            }
          }
        }
      }
      catch (Exception localException1)
      {
        try
        {
          Object localObject5;
          Object localObject6;
          CyclicBarrier localCyclicBarrier;
          Object localObject7;
          localCyclicBarrier.await(10L, TimeUnit.SECONDS);
          Object localObject8 = new org/telegram/messenger/WearDataLayerListenerService$3;
          ((3)localObject8).<init>(this, (NotificationCenter.NotificationCenterDelegate)localObject7);
          AndroidUtilities.runOnUIThread((Runnable)localObject8);
          if ((((File)localObject5).exists()) && (((File)localObject5).length() <= 52428800L))
          {
            localObject8 = new byte[(int)((File)localObject5).length()];
            localObject7 = new java/io/FileInputStream;
            ((FileInputStream)localObject7).<init>((File)localObject5);
            localObject5 = new java/io/DataInputStream;
            ((DataInputStream)localObject5).<init>((InputStream)localObject7);
            ((DataInputStream)localObject5).readFully((byte[])localObject8);
            ((FileInputStream)localObject7).close();
            ((DataOutputStream)localObject1).writeInt(localObject8.length);
            ((DataOutputStream)localObject1).write((byte[])localObject8);
            label368:
            ((DataOutputStream)localObject1).flush();
            ((DataOutputStream)localObject1).close();
          }
          for (;;)
          {
            paramChannel.close(localGoogleApiClient).await();
            localGoogleApiClient.disconnect();
            if (!BuildVars.LOGS_ENABLED) {
              break;
            }
            FileLog.d("WearableDataLayer channel thread exiting");
            break;
            ((DataOutputStream)localObject1).writeInt(0);
            break label368;
            localException1 = localException1;
            if (!BuildVars.LOGS_ENABLED) {
              continue;
            }
            FileLog.e("error processing wear request", localException1);
            continue;
            localException1.writeInt(0);
            break label368;
            localException1.writeInt(0);
            break label368;
            Object localObject2;
            if ("/waitForAuthCode".equals(localException1))
            {
              ConnectionsManager.getInstance(this.currentAccount).setAppPaused(false, false);
              localObject2 = new String[1];
              localObject2[0] = null;
              localObject7 = new java/util/concurrent/CyclicBarrier;
              ((CyclicBarrier)localObject7).<init>(2);
              localObject5 = new org/telegram/messenger/WearDataLayerListenerService$4;
              ((4)localObject5).<init>(this, (String[])localObject2, (CyclicBarrier)localObject7);
              localObject8 = new org/telegram/messenger/WearDataLayerListenerService$5;
              ((5)localObject8).<init>(this, (NotificationCenter.NotificationCenterDelegate)localObject5);
              AndroidUtilities.runOnUIThread((Runnable)localObject8);
            }
            try
            {
              ((CyclicBarrier)localObject7).await(15L, TimeUnit.SECONDS);
              localObject7 = new org/telegram/messenger/WearDataLayerListenerService$6;
              ((6)localObject7).<init>(this, (NotificationCenter.NotificationCenterDelegate)localObject5);
              AndroidUtilities.runOnUIThread((Runnable)localObject7);
              localObject5 = new java/io/DataOutputStream;
              ((DataOutputStream)localObject5).<init>(((Channel.GetOutputStreamResult)paramChannel.getOutputStream(localGoogleApiClient).await()).getOutputStream());
              if (localObject2[0] != null) {
                ((DataOutputStream)localObject5).writeUTF(localObject2[0]);
              }
              for (;;)
              {
                ((DataOutputStream)localObject5).flush();
                ((DataOutputStream)localObject5).close();
                ConnectionsManager.getInstance(this.currentAccount).setAppPaused(true, false);
                break;
                ((DataOutputStream)localObject5).writeUTF("");
              }
              if (!"/getChatPhoto".equals(localObject2)) {
                continue;
              }
              localObject7 = new java/io/DataInputStream;
              ((DataInputStream)localObject7).<init>(((Channel.GetInputStreamResult)paramChannel.getInputStream(localGoogleApiClient).await()).getInputStream());
              localObject8 = new java/io/DataOutputStream;
              ((DataOutputStream)localObject8).<init>(((Channel.GetOutputStreamResult)paramChannel.getOutputStream(localGoogleApiClient).await()).getOutputStream());
              for (;;)
              {
                try
                {
                  localObject2 = ((DataInputStream)localObject7).readUTF();
                  localObject5 = new org/json/JSONObject;
                  ((JSONObject)localObject5).<init>((String)localObject2);
                  i = ((JSONObject)localObject5).getInt("chat_id");
                  int j = ((JSONObject)localObject5).getInt("account_id");
                  int k = -1;
                  m = 0;
                  n = k;
                  if (m < UserConfig.getActivatedAccountsCount())
                  {
                    if (UserConfig.getInstance(m).getClientUserId() == j) {
                      n = m;
                    }
                  }
                  else
                  {
                    if (n == -1) {
                      break label1029;
                    }
                    localObject5 = null;
                    if (i <= 0) {
                      continue;
                    }
                    localObject6 = MessagesController.getInstance(n).getUser(Integer.valueOf(i));
                    localObject2 = localObject5;
                    if (localObject6 != null)
                    {
                      localObject2 = localObject5;
                      if (((TLRPC.User)localObject6).photo != null) {
                        localObject2 = ((TLRPC.User)localObject6).photo.photo_small;
                      }
                    }
                    if (localObject2 == null) {
                      break label1020;
                    }
                    localObject5 = FileLoader.getPathToAttach((TLObject)localObject2, true);
                    if ((!((File)localObject5).exists()) || (((File)localObject5).length() >= 102400L)) {
                      continue;
                    }
                    ((DataOutputStream)localObject8).writeInt((int)((File)localObject5).length());
                    localObject2 = new java/io/FileInputStream;
                    ((FileInputStream)localObject2).<init>((File)localObject5);
                    localObject5 = new byte['⠀'];
                    m = ((FileInputStream)localObject2).read((byte[])localObject5);
                    if (m <= 0) {
                      continue;
                    }
                    ((DataOutputStream)localObject8).write((byte[])localObject5, 0, m);
                    continue;
                  }
                }
                catch (Exception localException2)
                {
                  int i;
                  int m;
                  int n;
                  ((DataInputStream)localObject7).close();
                  ((DataOutputStream)localObject8).close();
                  break;
                  m++;
                  continue;
                  localObject6 = MessagesController.getInstance(n).getChat(Integer.valueOf(-i));
                  Object localObject3 = localObject5;
                  if (localObject6 == null) {
                    continue;
                  }
                  localObject3 = localObject5;
                  if (((TLRPC.Chat)localObject6).photo == null) {
                    continue;
                  }
                  localObject3 = ((TLRPC.Chat)localObject6).photo.photo_small;
                  continue;
                  ((FileInputStream)localObject3).close();
                  ((DataOutputStream)localObject8).flush();
                  ((DataInputStream)localObject7).close();
                  ((DataOutputStream)localObject8).close();
                  break;
                  ((DataOutputStream)localObject8).writeInt(0);
                  continue;
                }
                finally
                {
                  ((DataInputStream)localObject7).close();
                  ((DataOutputStream)localObject8).close();
                }
                label1020:
                ((DataOutputStream)localObject8).writeInt(0);
                continue;
                label1029:
                ((DataOutputStream)localObject8).writeInt(0);
              }
            }
            catch (Exception localException3)
            {
              for (;;) {}
            }
          }
        }
        catch (Exception localException4)
        {
          for (;;) {}
        }
      }
    }
  }
  
  public void onCreate()
  {
    super.onCreate();
    if (BuildVars.LOGS_ENABLED) {
      FileLog.d("WearableDataLayer service created");
    }
  }
  
  public void onDestroy()
  {
    super.onDestroy();
    if (BuildVars.LOGS_ENABLED) {
      FileLog.d("WearableDataLayer service destroyed");
    }
  }
  
  public void onMessageReceived(final MessageEvent paramMessageEvent)
  {
    if ("/reply".equals(paramMessageEvent.getPath())) {
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          for (;;)
          {
            try
            {
              ApplicationLoader.postInitApplication();
              str = new java/lang/String;
              str.<init>(paramMessageEvent.getData(), "UTF-8");
              localJSONObject = new org/json/JSONObject;
              localJSONObject.<init>(str);
              str = localJSONObject.getString("text");
              if ((str == null) || (str.length() == 0)) {
                return;
              }
            }
            catch (Exception localException)
            {
              String str;
              JSONObject localJSONObject;
              long l;
              int i;
              int j;
              int k;
              int m;
              int n;
              if (!BuildVars.LOGS_ENABLED) {
                continue;
              }
              FileLog.e(localException);
              continue;
              m++;
              continue;
            }
            l = localJSONObject.getLong("chat_id");
            i = localJSONObject.getInt("max_id");
            j = -1;
            k = localJSONObject.getInt("account_id");
            m = 0;
            n = j;
            if (m < UserConfig.getActivatedAccountsCount())
            {
              if (UserConfig.getInstance(m).getClientUserId() != k) {
                continue;
              }
              n = m;
            }
            if ((l != 0L) && (i != 0) && (n != -1))
            {
              SendMessagesHelper.getInstance(n).sendMessage(str.toString(), l, null, null, true, null, null, null);
              MessagesController.getInstance(n).markDialogAsRead(l, i, i, 0, false, 0, true);
            }
          }
        }
      });
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/WearDataLayerListenerService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */