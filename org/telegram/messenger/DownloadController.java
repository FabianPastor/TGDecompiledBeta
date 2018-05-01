package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.util.LongSparseArray;
import android.util.SparseArray;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.PhotoSize;

public class DownloadController
  implements NotificationCenter.NotificationCenterDelegate
{
  public static final int AUTODOWNLOAD_MASK_AUDIO = 2;
  public static final int AUTODOWNLOAD_MASK_DOCUMENT = 8;
  public static final int AUTODOWNLOAD_MASK_GIF = 32;
  public static final int AUTODOWNLOAD_MASK_MUSIC = 16;
  public static final int AUTODOWNLOAD_MASK_PHOTO = 1;
  public static final int AUTODOWNLOAD_MASK_VIDEO = 4;
  public static final int AUTODOWNLOAD_MASK_VIDEOMESSAGE = 64;
  private static volatile DownloadController[] Instance = new DownloadController[3];
  private HashMap<String, FileDownloadProgressListener> addLaterArray = new HashMap();
  private ArrayList<DownloadObject> audioDownloadQueue = new ArrayList();
  private int currentAccount;
  private ArrayList<FileDownloadProgressListener> deleteLaterArray = new ArrayList();
  private ArrayList<DownloadObject> documentDownloadQueue = new ArrayList();
  private HashMap<String, DownloadObject> downloadQueueKeys = new HashMap();
  private ArrayList<DownloadObject> gifDownloadQueue = new ArrayList();
  public boolean globalAutodownloadEnabled;
  private int lastCheckMask = 0;
  private int lastTag = 0;
  private boolean listenerInProgress = false;
  private HashMap<String, ArrayList<MessageObject>> loadingFileMessagesObservers = new HashMap();
  private HashMap<String, ArrayList<WeakReference<FileDownloadProgressListener>>> loadingFileObservers = new HashMap();
  public int[] mobileDataDownloadMask = new int[4];
  public int[] mobileMaxFileSize = new int[7];
  private ArrayList<DownloadObject> musicDownloadQueue = new ArrayList();
  private SparseArray<String> observersByTag = new SparseArray();
  private ArrayList<DownloadObject> photoDownloadQueue = new ArrayList();
  public int[] roamingDownloadMask = new int[4];
  public int[] roamingMaxFileSize = new int[7];
  private LongSparseArray<Long> typingTimes = new LongSparseArray();
  private ArrayList<DownloadObject> videoDownloadQueue = new ArrayList();
  private ArrayList<DownloadObject> videoMessageDownloadQueue = new ArrayList();
  public int[] wifiDownloadMask = new int[4];
  public int[] wifiMaxFileSize = new int[7];
  
  public DownloadController(int paramInt)
  {
    this.currentAccount = paramInt;
    Object localObject1 = MessagesController.getMainSettings(this.currentAccount);
    paramInt = 0;
    if (paramInt < 4)
    {
      Object localObject2 = new StringBuilder().append("mobileDataDownloadMask");
      if (paramInt == 0)
      {
        localObject3 = "";
        label259:
        localObject3 = localObject3;
        if ((paramInt != 0) && (!((SharedPreferences)localObject1).contains((String)localObject3))) {
          break label429;
        }
        this.mobileDataDownloadMask[paramInt] = ((SharedPreferences)localObject1).getInt((String)localObject3, 115);
        localObject2 = this.wifiDownloadMask;
        Object localObject4 = new StringBuilder().append("wifiDownloadMask");
        if (paramInt != 0) {
          break label411;
        }
        localObject3 = "";
        label328:
        localObject2[paramInt] = ((SharedPreferences)localObject1).getInt(localObject3, 115);
        localObject4 = this.roamingDownloadMask;
        localObject2 = new StringBuilder().append("roamingDownloadMask");
        if (paramInt != 0) {
          break label420;
        }
        localObject3 = "";
        label376:
        localObject4[paramInt] = ((SharedPreferences)localObject1).getInt(localObject3, 0);
      }
      for (;;)
      {
        paramInt++;
        break;
        localObject3 = Integer.valueOf(paramInt);
        break label259;
        label411:
        localObject3 = Integer.valueOf(paramInt);
        break label328;
        label420:
        localObject3 = Integer.valueOf(paramInt);
        break label376;
        label429:
        this.mobileDataDownloadMask[paramInt] = this.mobileDataDownloadMask[0];
        this.wifiDownloadMask[paramInt] = this.wifiDownloadMask[0];
        this.roamingDownloadMask[paramInt] = this.roamingDownloadMask[0];
      }
    }
    int i = 0;
    if (i < 7)
    {
      if (i == 1) {
        paramInt = 2097152;
      }
      for (;;)
      {
        this.mobileMaxFileSize[i] = ((SharedPreferences)localObject1).getInt("mobileMaxDownloadSize" + i, paramInt);
        this.wifiMaxFileSize[i] = ((SharedPreferences)localObject1).getInt("wifiMaxDownloadSize" + i, paramInt);
        this.roamingMaxFileSize[i] = ((SharedPreferences)localObject1).getInt("roamingMaxDownloadSize" + i, paramInt);
        i++;
        break;
        if (i == 6) {
          paramInt = 5242880;
        } else {
          paramInt = 10485760;
        }
      }
    }
    this.globalAutodownloadEnabled = ((SharedPreferences)localObject1).getBoolean("globalAutodownloadEnabled", true);
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        NotificationCenter.getInstance(DownloadController.this.currentAccount).addObserver(DownloadController.this, NotificationCenter.FileDidFailedLoad);
        NotificationCenter.getInstance(DownloadController.this.currentAccount).addObserver(DownloadController.this, NotificationCenter.FileDidLoaded);
        NotificationCenter.getInstance(DownloadController.this.currentAccount).addObserver(DownloadController.this, NotificationCenter.FileLoadProgressChanged);
        NotificationCenter.getInstance(DownloadController.this.currentAccount).addObserver(DownloadController.this, NotificationCenter.FileUploadProgressChanged);
        NotificationCenter.getInstance(DownloadController.this.currentAccount).addObserver(DownloadController.this, NotificationCenter.httpFileDidLoaded);
        NotificationCenter.getInstance(DownloadController.this.currentAccount).addObserver(DownloadController.this, NotificationCenter.httpFileDidFailedLoad);
      }
    });
    Object localObject3 = new BroadcastReceiver()
    {
      public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
      {
        DownloadController.this.checkAutodownloadSettings();
      }
    };
    localObject1 = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
    ApplicationLoader.applicationContext.registerReceiver((BroadcastReceiver)localObject3, (IntentFilter)localObject1);
    if (UserConfig.getInstance(this.currentAccount).isClientActivated()) {
      checkAutodownloadSettings();
    }
  }
  
  private void checkDownloadFinished(String paramString, int paramInt)
  {
    DownloadObject localDownloadObject = (DownloadObject)this.downloadQueueKeys.get(paramString);
    if (localDownloadObject != null)
    {
      this.downloadQueueKeys.remove(paramString);
      if ((paramInt == 0) || (paramInt == 2)) {
        MessagesStorage.getInstance(this.currentAccount).removeFromDownloadQueue(localDownloadObject.id, localDownloadObject.type, false);
      }
      if (localDownloadObject.type != 1) {
        break label86;
      }
      this.photoDownloadQueue.remove(localDownloadObject);
      if (this.photoDownloadQueue.isEmpty()) {
        newDownloadObjectsAvailable(1);
      }
    }
    for (;;)
    {
      return;
      label86:
      if (localDownloadObject.type == 2)
      {
        this.audioDownloadQueue.remove(localDownloadObject);
        if (this.audioDownloadQueue.isEmpty()) {
          newDownloadObjectsAvailable(2);
        }
      }
      else if (localDownloadObject.type == 64)
      {
        this.videoMessageDownloadQueue.remove(localDownloadObject);
        if (this.videoMessageDownloadQueue.isEmpty()) {
          newDownloadObjectsAvailable(64);
        }
      }
      else if (localDownloadObject.type == 4)
      {
        this.videoDownloadQueue.remove(localDownloadObject);
        if (this.videoDownloadQueue.isEmpty()) {
          newDownloadObjectsAvailable(4);
        }
      }
      else if (localDownloadObject.type == 8)
      {
        this.documentDownloadQueue.remove(localDownloadObject);
        if (this.documentDownloadQueue.isEmpty()) {
          newDownloadObjectsAvailable(8);
        }
      }
      else if (localDownloadObject.type == 16)
      {
        this.musicDownloadQueue.remove(localDownloadObject);
        if (this.musicDownloadQueue.isEmpty()) {
          newDownloadObjectsAvailable(16);
        }
      }
      else if (localDownloadObject.type == 32)
      {
        this.gifDownloadQueue.remove(localDownloadObject);
        if (this.gifDownloadQueue.isEmpty()) {
          newDownloadObjectsAvailable(32);
        }
      }
    }
  }
  
  public static DownloadController getInstance(int paramInt)
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
        localObject2 = new org/telegram/messenger/DownloadController;
        ((DownloadController)localObject2).<init>(paramInt);
        localObject1[paramInt] = localObject2;
      }
      return (DownloadController)localObject2;
    }
    finally {}
  }
  
  public static int maskToIndex(int paramInt)
  {
    int i = 0;
    if (paramInt == 1) {}
    for (;;)
    {
      return i;
      if (paramInt == 2) {
        i = 1;
      } else if (paramInt == 4) {
        i = 2;
      } else if (paramInt == 8) {
        i = 3;
      } else if (paramInt == 16) {
        i = 4;
      } else if (paramInt == 32) {
        i = 5;
      } else if (paramInt == 64) {
        i = 6;
      }
    }
  }
  
  private void processLaterArrays()
  {
    Iterator localIterator = this.addLaterArray.entrySet().iterator();
    while (localIterator.hasNext())
    {
      localObject = (Map.Entry)localIterator.next();
      addLoadingFileObserver((String)((Map.Entry)localObject).getKey(), (FileDownloadProgressListener)((Map.Entry)localObject).getValue());
    }
    this.addLaterArray.clear();
    Object localObject = this.deleteLaterArray.iterator();
    while (((Iterator)localObject).hasNext()) {
      removeLoadingFileObserver((FileDownloadProgressListener)((Iterator)localObject).next());
    }
    this.deleteLaterArray.clear();
  }
  
  public void addLoadingFileObserver(String paramString, FileDownloadProgressListener paramFileDownloadProgressListener)
  {
    addLoadingFileObserver(paramString, null, paramFileDownloadProgressListener);
  }
  
  public void addLoadingFileObserver(String paramString, MessageObject paramMessageObject, FileDownloadProgressListener paramFileDownloadProgressListener)
  {
    if (this.listenerInProgress) {
      this.addLaterArray.put(paramString, paramFileDownloadProgressListener);
    }
    for (;;)
    {
      return;
      removeLoadingFileObserver(paramFileDownloadProgressListener);
      ArrayList localArrayList1 = (ArrayList)this.loadingFileObservers.get(paramString);
      ArrayList localArrayList2 = localArrayList1;
      if (localArrayList1 == null)
      {
        localArrayList2 = new ArrayList();
        this.loadingFileObservers.put(paramString, localArrayList2);
      }
      localArrayList2.add(new WeakReference(paramFileDownloadProgressListener));
      if (paramMessageObject != null)
      {
        localArrayList1 = (ArrayList)this.loadingFileMessagesObservers.get(paramString);
        localArrayList2 = localArrayList1;
        if (localArrayList1 == null)
        {
          localArrayList2 = new ArrayList();
          this.loadingFileMessagesObservers.put(paramString, localArrayList2);
        }
        localArrayList2.add(paramMessageObject);
      }
      this.observersByTag.put(paramFileDownloadProgressListener.getObserverTag(), paramString);
    }
  }
  
  public boolean canDownloadMedia(MessageObject paramMessageObject)
  {
    return canDownloadMedia(paramMessageObject.messageOwner);
  }
  
  public boolean canDownloadMedia(TLRPC.Message paramMessage)
  {
    boolean bool1 = false;
    boolean bool2;
    if (!this.globalAutodownloadEnabled)
    {
      bool2 = bool1;
      return bool2;
    }
    int i;
    label23:
    TLRPC.Peer localPeer;
    int j;
    label69:
    int k;
    if (MessageObject.isPhoto(paramMessage))
    {
      i = 1;
      localPeer = paramMessage.to_id;
      if (localPeer == null) {
        break label242;
      }
      if (localPeer.user_id == 0) {
        break label209;
      }
      if (!ContactsController.getInstance(this.currentAccount).contactsDict.containsKey(Integer.valueOf(localPeer.user_id))) {
        break label203;
      }
      j = 0;
      if (!ConnectionsManager.isConnectedToWiFi()) {
        break label248;
      }
      j = this.wifiDownloadMask[j];
      k = this.wifiMaxFileSize[maskToIndex(i)];
    }
    for (;;)
    {
      if (i != 1)
      {
        bool2 = bool1;
        if (MessageObject.getMessageSize(paramMessage) > k) {
          break;
        }
      }
      bool2 = bool1;
      if ((j & i) == 0) {
        break;
      }
      bool2 = true;
      break;
      if (MessageObject.isVoiceMessage(paramMessage))
      {
        i = 2;
        break label23;
      }
      if (MessageObject.isRoundVideoMessage(paramMessage))
      {
        i = 64;
        break label23;
      }
      if (MessageObject.isVideoMessage(paramMessage))
      {
        i = 4;
        break label23;
      }
      if (MessageObject.isMusicMessage(paramMessage))
      {
        i = 16;
        break label23;
      }
      if (MessageObject.isGifMessage(paramMessage))
      {
        i = 32;
        break label23;
      }
      i = 8;
      break label23;
      label203:
      j = 1;
      break label69;
      label209:
      if (localPeer.chat_id != 0)
      {
        j = 2;
        break label69;
      }
      if (MessageObject.isMegagroup(paramMessage))
      {
        j = 2;
        break label69;
      }
      j = 3;
      break label69;
      label242:
      j = 1;
      break label69;
      label248:
      if (ConnectionsManager.isRoaming())
      {
        j = this.roamingDownloadMask[j];
        k = this.roamingMaxFileSize[maskToIndex(i)];
      }
      else
      {
        j = this.mobileDataDownloadMask[j];
        k = this.mobileMaxFileSize[maskToIndex(i)];
      }
    }
  }
  
  public void checkAutodownloadSettings()
  {
    int i = getCurrentDownloadMask();
    if (i == this.lastCheckMask) {}
    for (;;)
    {
      return;
      this.lastCheckMask = i;
      if ((i & 0x1) != 0)
      {
        if (this.photoDownloadQueue.isEmpty()) {
          newDownloadObjectsAvailable(1);
        }
        label40:
        if ((i & 0x2) == 0) {
          break label255;
        }
        if (this.audioDownloadQueue.isEmpty()) {
          newDownloadObjectsAvailable(2);
        }
        label61:
        if ((i & 0x40) == 0) {
          break label313;
        }
        if (this.videoMessageDownloadQueue.isEmpty()) {
          newDownloadObjectsAvailable(64);
        }
        label84:
        if ((i & 0x8) == 0) {
          break label371;
        }
        if (this.documentDownloadQueue.isEmpty()) {
          newDownloadObjectsAvailable(8);
        }
        label107:
        if ((i & 0x4) == 0) {
          break label429;
        }
        if (this.videoDownloadQueue.isEmpty()) {
          newDownloadObjectsAvailable(4);
        }
        label128:
        if ((i & 0x10) == 0) {
          break label487;
        }
        if (this.musicDownloadQueue.isEmpty()) {
          newDownloadObjectsAvailable(16);
        }
        label151:
        if ((i & 0x20) == 0) {
          break label545;
        }
        if (this.gifDownloadQueue.isEmpty()) {
          newDownloadObjectsAvailable(32);
        }
      }
      int j;
      for (;;)
      {
        j = getAutodownloadMaskAll();
        if (j != 0) {
          break label603;
        }
        MessagesStorage.getInstance(this.currentAccount).clearDownloadQueue(0);
        break;
        Object localObject;
        for (j = 0; j < this.photoDownloadQueue.size(); j++)
        {
          localObject = (DownloadObject)this.photoDownloadQueue.get(j);
          FileLoader.getInstance(this.currentAccount).cancelLoadFile((TLRPC.PhotoSize)((DownloadObject)localObject).object);
        }
        this.photoDownloadQueue.clear();
        break label40;
        label255:
        for (j = 0; j < this.audioDownloadQueue.size(); j++)
        {
          localObject = (DownloadObject)this.audioDownloadQueue.get(j);
          FileLoader.getInstance(this.currentAccount).cancelLoadFile((TLRPC.Document)((DownloadObject)localObject).object);
        }
        this.audioDownloadQueue.clear();
        break label61;
        label313:
        for (j = 0; j < this.videoMessageDownloadQueue.size(); j++)
        {
          localObject = (DownloadObject)this.videoMessageDownloadQueue.get(j);
          FileLoader.getInstance(this.currentAccount).cancelLoadFile((TLRPC.Document)((DownloadObject)localObject).object);
        }
        this.videoMessageDownloadQueue.clear();
        break label84;
        label371:
        for (j = 0; j < this.documentDownloadQueue.size(); j++)
        {
          localObject = (TLRPC.Document)((DownloadObject)this.documentDownloadQueue.get(j)).object;
          FileLoader.getInstance(this.currentAccount).cancelLoadFile((TLRPC.Document)localObject);
        }
        this.documentDownloadQueue.clear();
        break label107;
        label429:
        for (j = 0; j < this.videoDownloadQueue.size(); j++)
        {
          localObject = (DownloadObject)this.videoDownloadQueue.get(j);
          FileLoader.getInstance(this.currentAccount).cancelLoadFile((TLRPC.Document)((DownloadObject)localObject).object);
        }
        this.videoDownloadQueue.clear();
        break label128;
        label487:
        for (j = 0; j < this.musicDownloadQueue.size(); j++)
        {
          localObject = (TLRPC.Document)((DownloadObject)this.musicDownloadQueue.get(j)).object;
          FileLoader.getInstance(this.currentAccount).cancelLoadFile((TLRPC.Document)localObject);
        }
        this.musicDownloadQueue.clear();
        break label151;
        label545:
        for (j = 0; j < this.gifDownloadQueue.size(); j++)
        {
          localObject = (TLRPC.Document)((DownloadObject)this.gifDownloadQueue.get(j)).object;
          FileLoader.getInstance(this.currentAccount).cancelLoadFile((TLRPC.Document)localObject);
        }
        this.gifDownloadQueue.clear();
      }
      label603:
      if ((j & 0x1) == 0) {
        MessagesStorage.getInstance(this.currentAccount).clearDownloadQueue(1);
      }
      if ((j & 0x2) == 0) {
        MessagesStorage.getInstance(this.currentAccount).clearDownloadQueue(2);
      }
      if ((j & 0x40) == 0) {
        MessagesStorage.getInstance(this.currentAccount).clearDownloadQueue(64);
      }
      if ((j & 0x4) == 0) {
        MessagesStorage.getInstance(this.currentAccount).clearDownloadQueue(4);
      }
      if ((j & 0x8) == 0) {
        MessagesStorage.getInstance(this.currentAccount).clearDownloadQueue(8);
      }
      if ((j & 0x10) == 0) {
        MessagesStorage.getInstance(this.currentAccount).clearDownloadQueue(16);
      }
      if ((j & 0x20) == 0) {
        MessagesStorage.getInstance(this.currentAccount).clearDownloadQueue(32);
      }
    }
  }
  
  public void cleanup()
  {
    this.photoDownloadQueue.clear();
    this.audioDownloadQueue.clear();
    this.videoMessageDownloadQueue.clear();
    this.documentDownloadQueue.clear();
    this.videoDownloadQueue.clear();
    this.musicDownloadQueue.clear();
    this.gifDownloadQueue.clear();
    this.downloadQueueKeys.clear();
    this.typingTimes.clear();
  }
  
  public void didReceivedNotification(int paramInt1, int paramInt2, Object... paramVarArgs)
  {
    Object localObject1;
    if ((paramInt1 == NotificationCenter.FileDidFailedLoad) || (paramInt1 == NotificationCenter.httpFileDidFailedLoad))
    {
      this.listenerInProgress = true;
      localObject1 = (String)paramVarArgs[0];
      localObject2 = (ArrayList)this.loadingFileObservers.get(localObject1);
      if (localObject2 != null)
      {
        paramInt1 = 0;
        paramInt2 = ((ArrayList)localObject2).size();
        while (paramInt1 < paramInt2)
        {
          localObject3 = (WeakReference)((ArrayList)localObject2).get(paramInt1);
          if (((WeakReference)localObject3).get() != null)
          {
            ((FileDownloadProgressListener)((WeakReference)localObject3).get()).onFailedDownload((String)localObject1);
            this.observersByTag.remove(((FileDownloadProgressListener)((WeakReference)localObject3).get()).getObserverTag());
          }
          paramInt1++;
        }
        this.loadingFileObservers.remove(localObject1);
      }
      this.listenerInProgress = false;
      processLaterArrays();
      checkDownloadFinished((String)localObject1, ((Integer)paramVarArgs[1]).intValue());
    }
    do
    {
      for (;;)
      {
        return;
        if ((paramInt1 == NotificationCenter.FileDidLoaded) || (paramInt1 == NotificationCenter.httpFileDidLoaded))
        {
          this.listenerInProgress = true;
          paramVarArgs = (String)paramVarArgs[0];
          localObject3 = (ArrayList)this.loadingFileMessagesObservers.get(paramVarArgs);
          if (localObject3 != null)
          {
            paramInt1 = 0;
            paramInt2 = ((ArrayList)localObject3).size();
            while (paramInt1 < paramInt2)
            {
              ((MessageObject)((ArrayList)localObject3).get(paramInt1)).mediaExists = true;
              paramInt1++;
            }
            this.loadingFileMessagesObservers.remove(paramVarArgs);
          }
          localObject1 = (ArrayList)this.loadingFileObservers.get(paramVarArgs);
          if (localObject1 != null)
          {
            paramInt1 = 0;
            paramInt2 = ((ArrayList)localObject1).size();
            while (paramInt1 < paramInt2)
            {
              localObject3 = (WeakReference)((ArrayList)localObject1).get(paramInt1);
              if (((WeakReference)localObject3).get() != null)
              {
                ((FileDownloadProgressListener)((WeakReference)localObject3).get()).onSuccessDownload(paramVarArgs);
                this.observersByTag.remove(((FileDownloadProgressListener)((WeakReference)localObject3).get()).getObserverTag());
              }
              paramInt1++;
            }
            this.loadingFileObservers.remove(paramVarArgs);
          }
          this.listenerInProgress = false;
          processLaterArrays();
          checkDownloadFinished(paramVarArgs, 0);
        }
        else
        {
          if (paramInt1 != NotificationCenter.FileLoadProgressChanged) {
            break;
          }
          this.listenerInProgress = true;
          localObject1 = (String)paramVarArgs[0];
          localObject3 = (ArrayList)this.loadingFileObservers.get(localObject1);
          if (localObject3 != null)
          {
            localObject2 = (Float)paramVarArgs[1];
            paramInt1 = 0;
            paramInt2 = ((ArrayList)localObject3).size();
            while (paramInt1 < paramInt2)
            {
              paramVarArgs = (WeakReference)((ArrayList)localObject3).get(paramInt1);
              if (paramVarArgs.get() != null) {
                ((FileDownloadProgressListener)paramVarArgs.get()).onProgressDownload((String)localObject1, ((Float)localObject2).floatValue());
              }
              paramInt1++;
            }
          }
          this.listenerInProgress = false;
          processLaterArrays();
        }
      }
    } while (paramInt1 != NotificationCenter.FileUploadProgressChanged);
    this.listenerInProgress = true;
    Object localObject3 = (String)paramVarArgs[0];
    Object localObject2 = (ArrayList)this.loadingFileObservers.get(localObject3);
    if (localObject2 != null)
    {
      localObject1 = (Float)paramVarArgs[1];
      paramVarArgs = (Boolean)paramVarArgs[2];
      paramInt1 = 0;
      paramInt2 = ((ArrayList)localObject2).size();
      while (paramInt1 < paramInt2)
      {
        WeakReference localWeakReference = (WeakReference)((ArrayList)localObject2).get(paramInt1);
        if (localWeakReference.get() != null) {
          ((FileDownloadProgressListener)localWeakReference.get()).onProgressUpload((String)localObject3, ((Float)localObject1).floatValue(), paramVarArgs.booleanValue());
        }
        paramInt1++;
      }
    }
    this.listenerInProgress = false;
    processLaterArrays();
    long l;
    label782:
    label807:
    do
    {
      try
      {
        paramVarArgs = SendMessagesHelper.getInstance(this.currentAccount).getDelayedMessages((String)localObject3);
        if (paramVarArgs == null) {
          break;
        }
        paramInt1 = 0;
        if (paramInt1 >= paramVarArgs.size()) {
          break;
        }
        localObject1 = (SendMessagesHelper.DelayedMessage)paramVarArgs.get(paramInt1);
        if (((SendMessagesHelper.DelayedMessage)localObject1).encryptedChat == null)
        {
          l = ((SendMessagesHelper.DelayedMessage)localObject1).peer;
          if (((SendMessagesHelper.DelayedMessage)localObject1).type != 4) {
            break label807;
          }
          localObject2 = (Long)this.typingTimes.get(l);
          if ((localObject2 == null) || (((Long)localObject2).longValue() + 4000L < System.currentTimeMillis()))
          {
            localObject2 = ((SendMessagesHelper.DelayedMessage)localObject1).extraHashMap;
            localObject1 = new java/lang/StringBuilder;
            ((StringBuilder)localObject1).<init>();
            localObject1 = (MessageObject)((HashMap)localObject2).get((String)localObject3 + "_i");
            if ((localObject1 == null) || (!((MessageObject)localObject1).isVideo())) {
              break label782;
            }
            MessagesController.getInstance(this.currentAccount).sendTyping(l, 5, 0);
          }
        }
        for (;;)
        {
          this.typingTimes.put(l, Long.valueOf(System.currentTimeMillis()));
          paramInt1++;
          break;
          MessagesController.getInstance(this.currentAccount).sendTyping(l, 4, 0);
        }
      }
      catch (Exception paramVarArgs)
      {
        FileLog.e(paramVarArgs);
      }
      localObject2 = (Long)this.typingTimes.get(l);
      ((SendMessagesHelper.DelayedMessage)localObject1).obj.getDocument();
    } while ((localObject2 != null) && (((Long)localObject2).longValue() + 4000L >= System.currentTimeMillis()));
    if (((SendMessagesHelper.DelayedMessage)localObject1).obj.isRoundVideo()) {
      MessagesController.getInstance(this.currentAccount).sendTyping(l, 8, 0);
    }
    for (;;)
    {
      this.typingTimes.put(l, Long.valueOf(System.currentTimeMillis()));
      break;
      if (((SendMessagesHelper.DelayedMessage)localObject1).obj.isVideo()) {
        MessagesController.getInstance(this.currentAccount).sendTyping(l, 5, 0);
      } else if (((SendMessagesHelper.DelayedMessage)localObject1).obj.isVoice()) {
        MessagesController.getInstance(this.currentAccount).sendTyping(l, 9, 0);
      } else if (((SendMessagesHelper.DelayedMessage)localObject1).obj.getDocument() != null) {
        MessagesController.getInstance(this.currentAccount).sendTyping(l, 3, 0);
      } else if (((SendMessagesHelper.DelayedMessage)localObject1).location != null) {
        MessagesController.getInstance(this.currentAccount).sendTyping(l, 4, 0);
      }
    }
  }
  
  public int generateObserverTag()
  {
    int i = this.lastTag;
    this.lastTag = (i + 1);
    return i;
  }
  
  protected int getAutodownloadMask()
  {
    int i;
    if (!this.globalAutodownloadEnabled)
    {
      i = 0;
      return i;
    }
    int j = 0;
    int[] arrayOfInt;
    if (ConnectionsManager.isConnectedToWiFi()) {
      arrayOfInt = this.wifiDownloadMask;
    }
    for (;;)
    {
      for (int k = 0;; k++)
      {
        i = j;
        if (k >= 4) {
          break;
        }
        i = 0;
        if ((arrayOfInt[k] & 0x1) != 0) {
          i = 0x0 | 0x1;
        }
        int m = i;
        if ((arrayOfInt[k] & 0x2) != 0) {
          m = i | 0x2;
        }
        i = m;
        if ((arrayOfInt[k] & 0x40) != 0) {
          i = m | 0x40;
        }
        m = i;
        if ((arrayOfInt[k] & 0x4) != 0) {
          m = i | 0x4;
        }
        int n = m;
        if ((arrayOfInt[k] & 0x8) != 0) {
          n = m | 0x8;
        }
        i = n;
        if ((arrayOfInt[k] & 0x10) != 0) {
          i = n | 0x10;
        }
        m = i;
        if ((arrayOfInt[k] & 0x20) != 0) {
          m = i | 0x20;
        }
        j |= m << k * 8;
      }
      if (ConnectionsManager.isRoaming()) {
        arrayOfInt = this.roamingDownloadMask;
      } else {
        arrayOfInt = this.mobileDataDownloadMask;
      }
    }
  }
  
  protected int getAutodownloadMaskAll()
  {
    int i;
    if (!this.globalAutodownloadEnabled)
    {
      i = 0;
      return i;
    }
    int j = 0;
    int k = 0;
    for (;;)
    {
      i = j;
      if (k >= 4) {
        break;
      }
      if (((this.mobileDataDownloadMask[k] & 0x1) == 0) && ((this.wifiDownloadMask[k] & 0x1) == 0))
      {
        i = j;
        if ((this.roamingDownloadMask[k] & 0x1) == 0) {}
      }
      else
      {
        i = j | 0x1;
      }
      int m;
      if (((this.mobileDataDownloadMask[k] & 0x2) == 0) && ((this.wifiDownloadMask[k] & 0x2) == 0))
      {
        m = i;
        if ((this.roamingDownloadMask[k] & 0x2) == 0) {}
      }
      else
      {
        m = i | 0x2;
      }
      if (((this.mobileDataDownloadMask[k] & 0x40) == 0) && ((this.wifiDownloadMask[k] & 0x40) == 0))
      {
        j = m;
        if ((this.roamingDownloadMask[k] & 0x40) == 0) {}
      }
      else
      {
        j = m | 0x40;
      }
      if (((this.mobileDataDownloadMask[k] & 0x4) == 0) && ((this.wifiDownloadMask[k] & 0x4) == 0))
      {
        i = j;
        if ((this.roamingDownloadMask[k] & 0x4) == 0) {}
      }
      else
      {
        i = j | 0x4;
      }
      if (((this.mobileDataDownloadMask[k] & 0x8) == 0) && ((this.wifiDownloadMask[k] & 0x8) == 0))
      {
        m = i;
        if ((this.roamingDownloadMask[k] & 0x8) == 0) {}
      }
      else
      {
        m = i | 0x8;
      }
      if (((this.mobileDataDownloadMask[k] & 0x10) == 0) && ((this.wifiDownloadMask[k] & 0x10) == 0))
      {
        j = m;
        if ((this.roamingDownloadMask[k] & 0x10) == 0) {}
      }
      else
      {
        j = m | 0x10;
      }
      if (((this.mobileDataDownloadMask[k] & 0x20) == 0) && ((this.wifiDownloadMask[k] & 0x20) == 0))
      {
        i = j;
        if ((this.roamingDownloadMask[k] & 0x20) == 0) {}
      }
      else
      {
        i = j | 0x20;
      }
      k++;
      j = i;
    }
  }
  
  protected int getCurrentDownloadMask()
  {
    int i;
    if (!this.globalAutodownloadEnabled)
    {
      i = 0;
      return i;
    }
    if (ConnectionsManager.isConnectedToWiFi())
    {
      j = 0;
      for (k = 0;; k++)
      {
        i = j;
        if (k >= 4) {
          break;
        }
        j |= this.wifiDownloadMask[k];
      }
    }
    if (ConnectionsManager.isRoaming())
    {
      j = 0;
      for (k = 0;; k++)
      {
        i = j;
        if (k >= 4) {
          break;
        }
        j |= this.roamingDownloadMask[k];
      }
    }
    int j = 0;
    for (int k = 0;; k++)
    {
      i = j;
      if (k >= 4) {
        break;
      }
      j |= this.mobileDataDownloadMask[k];
    }
  }
  
  protected void newDownloadObjectsAvailable(int paramInt)
  {
    int i = getCurrentDownloadMask();
    if (((i & 0x1) != 0) && ((paramInt & 0x1) != 0) && (this.photoDownloadQueue.isEmpty())) {
      MessagesStorage.getInstance(this.currentAccount).getDownloadQueue(1);
    }
    if (((i & 0x2) != 0) && ((paramInt & 0x2) != 0) && (this.audioDownloadQueue.isEmpty())) {
      MessagesStorage.getInstance(this.currentAccount).getDownloadQueue(2);
    }
    if (((i & 0x40) != 0) && ((paramInt & 0x40) != 0) && (this.videoMessageDownloadQueue.isEmpty())) {
      MessagesStorage.getInstance(this.currentAccount).getDownloadQueue(64);
    }
    if (((i & 0x4) != 0) && ((paramInt & 0x4) != 0) && (this.videoDownloadQueue.isEmpty())) {
      MessagesStorage.getInstance(this.currentAccount).getDownloadQueue(4);
    }
    if (((i & 0x8) != 0) && ((paramInt & 0x8) != 0) && (this.documentDownloadQueue.isEmpty())) {
      MessagesStorage.getInstance(this.currentAccount).getDownloadQueue(8);
    }
    if (((i & 0x10) != 0) && ((paramInt & 0x10) != 0) && (this.musicDownloadQueue.isEmpty())) {
      MessagesStorage.getInstance(this.currentAccount).getDownloadQueue(16);
    }
    if (((i & 0x20) != 0) && ((paramInt & 0x20) != 0) && (this.gifDownloadQueue.isEmpty())) {
      MessagesStorage.getInstance(this.currentAccount).getDownloadQueue(32);
    }
  }
  
  protected void processDownloadObjects(int paramInt, ArrayList<DownloadObject> paramArrayList)
  {
    if (paramArrayList.isEmpty()) {
      return;
    }
    ArrayList localArrayList = null;
    label20:
    label22:
    DownloadObject localDownloadObject;
    String str;
    if (paramInt == 1)
    {
      localArrayList = this.photoDownloadQueue;
      paramInt = 0;
      if (paramInt < paramArrayList.size())
      {
        localDownloadObject = (DownloadObject)paramArrayList.get(paramInt);
        if (!(localDownloadObject.object instanceof TLRPC.Document)) {
          break label164;
        }
        str = FileLoader.getAttachFileName((TLRPC.Document)localDownloadObject.object);
        label64:
        if (!this.downloadQueueKeys.containsKey(str)) {
          break label177;
        }
      }
    }
    label164:
    label177:
    label221:
    label336:
    for (;;)
    {
      paramInt++;
      break label22;
      break;
      if (paramInt == 2)
      {
        localArrayList = this.audioDownloadQueue;
        break label20;
      }
      if (paramInt == 64)
      {
        localArrayList = this.videoMessageDownloadQueue;
        break label20;
      }
      if (paramInt == 4)
      {
        localArrayList = this.videoDownloadQueue;
        break label20;
      }
      if (paramInt == 8)
      {
        localArrayList = this.documentDownloadQueue;
        break label20;
      }
      if (paramInt == 16)
      {
        localArrayList = this.musicDownloadQueue;
        break label20;
      }
      if (paramInt != 32) {
        break label20;
      }
      localArrayList = this.gifDownloadQueue;
      break label20;
      str = FileLoader.getAttachFileName(localDownloadObject.object);
      break label64;
      int i = 1;
      FileLoader localFileLoader;
      Object localObject;
      int j;
      if ((localDownloadObject.object instanceof TLRPC.PhotoSize))
      {
        localFileLoader = FileLoader.getInstance(this.currentAccount);
        localObject = (TLRPC.PhotoSize)localDownloadObject.object;
        if (localDownloadObject.secret)
        {
          j = 2;
          localFileLoader.loadFile((TLRPC.PhotoSize)localObject, null, j);
          j = i;
        }
      }
      for (;;)
      {
        if (j == 0) {
          break label336;
        }
        localArrayList.add(localDownloadObject);
        this.downloadQueueKeys.put(str, localDownloadObject);
        break;
        j = 0;
        break label221;
        if ((localDownloadObject.object instanceof TLRPC.Document))
        {
          localObject = (TLRPC.Document)localDownloadObject.object;
          localFileLoader = FileLoader.getInstance(this.currentAccount);
          if (localDownloadObject.secret) {}
          for (j = 2;; j = 0)
          {
            localFileLoader.loadFile((TLRPC.Document)localObject, false, j);
            j = i;
            break;
          }
        }
        j = 0;
      }
    }
  }
  
  public void removeLoadingFileObserver(FileDownloadProgressListener paramFileDownloadProgressListener)
  {
    if (this.listenerInProgress) {
      this.deleteLaterArray.add(paramFileDownloadProgressListener);
    }
    for (;;)
    {
      return;
      String str = (String)this.observersByTag.get(paramFileDownloadProgressListener.getObserverTag());
      if (str != null)
      {
        ArrayList localArrayList = (ArrayList)this.loadingFileObservers.get(str);
        if (localArrayList != null)
        {
          int j;
          for (int i = 0; i < localArrayList.size(); i = j + 1)
          {
            WeakReference localWeakReference = (WeakReference)localArrayList.get(i);
            if (localWeakReference.get() != null)
            {
              j = i;
              if (localWeakReference.get() != paramFileDownloadProgressListener) {}
            }
            else
            {
              localArrayList.remove(i);
              j = i - 1;
            }
          }
          if (localArrayList.isEmpty()) {
            this.loadingFileObservers.remove(str);
          }
        }
        this.observersByTag.remove(paramFileDownloadProgressListener.getObserverTag());
      }
    }
  }
  
  public static abstract interface FileDownloadProgressListener
  {
    public abstract int getObserverTag();
    
    public abstract void onFailedDownload(String paramString);
    
    public abstract void onProgressDownload(String paramString, float paramFloat);
    
    public abstract void onProgressUpload(String paramString, float paramFloat, boolean paramBoolean);
    
    public abstract void onSuccessDownload(String paramString);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/DownloadController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */