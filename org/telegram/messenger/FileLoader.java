package org.telegram.messenger;

import android.content.Context;
import android.text.TextUtils;
import android.util.SparseArray;
import android.util.SparseIntArray;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.exoplayer2.upstream.DataSource;
import org.telegram.messenger.exoplayer2.upstream.TransferListener;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputEncryptedFile;
import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageAction;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.Photo;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_documentAttributeFilename;
import org.telegram.tgnet.TLRPC.TL_fileLocationUnavailable;
import org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC.TL_messageMediaInvoice;
import org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC.TL_messageService;
import org.telegram.tgnet.TLRPC.TL_photoCachedSize;
import org.telegram.tgnet.TLRPC.TL_webDocument;
import org.telegram.tgnet.TLRPC.WebDocument;
import org.telegram.tgnet.TLRPC.WebPage;

public class FileLoader
{
  private static volatile FileLoader[] Instance = new FileLoader[3];
  public static final int MEDIA_DIR_AUDIO = 1;
  public static final int MEDIA_DIR_CACHE = 4;
  public static final int MEDIA_DIR_DOCUMENT = 3;
  public static final int MEDIA_DIR_IMAGE = 0;
  public static final int MEDIA_DIR_VIDEO = 2;
  private static volatile DispatchQueue fileLoaderQueue = new DispatchQueue("fileUploadQueue");
  private static SparseArray<File> mediaDirs = null;
  private ArrayList<FileLoadOperation> activeFileLoadOperation = new ArrayList();
  private SparseArray<LinkedList<FileLoadOperation>> audioLoadOperationQueues = new SparseArray();
  private int currentAccount;
  private SparseIntArray currentAudioLoadOperationsCount = new SparseIntArray();
  private SparseIntArray currentLoadOperationsCount = new SparseIntArray();
  private SparseIntArray currentPhotoLoadOperationsCount = new SparseIntArray();
  private int currentUploadOperationsCount = 0;
  private int currentUploadSmallOperationsCount = 0;
  private FileLoaderDelegate delegate = null;
  private ConcurrentHashMap<String, FileLoadOperation> loadOperationPaths = new ConcurrentHashMap();
  private ConcurrentHashMap<String, Boolean> loadOperationPathsUI = new ConcurrentHashMap(10, 1.0F, 2);
  private SparseArray<LinkedList<FileLoadOperation>> loadOperationQueues = new SparseArray();
  private SparseArray<LinkedList<FileLoadOperation>> photoLoadOperationQueues = new SparseArray();
  private ConcurrentHashMap<String, FileUploadOperation> uploadOperationPaths = new ConcurrentHashMap();
  private ConcurrentHashMap<String, FileUploadOperation> uploadOperationPathsEnc = new ConcurrentHashMap();
  private LinkedList<FileUploadOperation> uploadOperationQueue = new LinkedList();
  private HashMap<String, Long> uploadSizes = new HashMap();
  private LinkedList<FileUploadOperation> uploadSmallOperationQueue = new LinkedList();
  
  public FileLoader(int paramInt)
  {
    this.currentAccount = paramInt;
  }
  
  private void cancelLoadFile(final TLRPC.Document paramDocument, final TLRPC.TL_webDocument paramTL_webDocument, final TLRPC.FileLocation paramFileLocation, final String paramString)
  {
    if ((paramFileLocation == null) && (paramDocument == null) && (paramTL_webDocument == null)) {}
    label92:
    for (;;)
    {
      return;
      if (paramFileLocation != null) {
        paramString = getAttachFileName(paramFileLocation, paramString);
      }
      for (;;)
      {
        if (paramString == null) {
          break label92;
        }
        this.loadOperationPathsUI.remove(paramString);
        fileLoaderQueue.postRunnable(new Runnable()
        {
          public void run()
          {
            FileLoadOperation localFileLoadOperation = (FileLoadOperation)FileLoader.this.loadOperationPaths.remove(paramString);
            int i;
            if (localFileLoadOperation != null)
            {
              i = localFileLoadOperation.getDatacenterId();
              if ((!MessageObject.isVoiceDocument(paramDocument)) && (!MessageObject.isVoiceWebDocument(paramTL_webDocument))) {
                break label91;
              }
              if (!FileLoader.this.getAudioLoadOperationQueue(i).remove(localFileLoadOperation)) {
                FileLoader.this.currentAudioLoadOperationsCount.put(i, FileLoader.this.currentAudioLoadOperationsCount.get(i) - 1);
              }
            }
            for (;;)
            {
              localFileLoadOperation.cancel();
              return;
              label91:
              if ((paramFileLocation != null) || (MessageObject.isImageWebDocument(paramTL_webDocument)))
              {
                if (!FileLoader.this.getPhotoLoadOperationQueue(i).remove(localFileLoadOperation)) {
                  FileLoader.this.currentPhotoLoadOperationsCount.put(i, FileLoader.this.currentPhotoLoadOperationsCount.get(i) - 1);
                }
              }
              else
              {
                if (!FileLoader.this.getLoadOperationQueue(i).remove(localFileLoadOperation)) {
                  FileLoader.this.currentLoadOperationsCount.put(i, FileLoader.this.currentLoadOperationsCount.get(i) - 1);
                }
                FileLoader.this.activeFileLoadOperation.remove(localFileLoadOperation);
              }
            }
          }
        });
        break;
        if (paramDocument != null) {
          paramString = getAttachFileName(paramDocument);
        } else if (paramTL_webDocument != null) {
          paramString = getAttachFileName(paramTL_webDocument);
        } else {
          paramString = null;
        }
      }
    }
  }
  
  public static File checkDirectory(int paramInt)
  {
    return (File)mediaDirs.get(paramInt);
  }
  
  private void checkDownloadQueue(final int paramInt, final TLRPC.Document paramDocument, final TLRPC.TL_webDocument paramTL_webDocument, final TLRPC.FileLocation paramFileLocation, final String paramString)
  {
    fileLoaderQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        Object localObject1 = FileLoader.this.getAudioLoadOperationQueue(paramInt);
        Object localObject2 = FileLoader.this.getPhotoLoadOperationQueue(paramInt);
        LinkedList localLinkedList = FileLoader.this.getLoadOperationQueue(paramInt);
        FileLoadOperation localFileLoadOperation = (FileLoadOperation)FileLoader.this.loadOperationPaths.remove(paramString);
        int i;
        int j;
        if ((MessageObject.isVoiceDocument(paramDocument)) || (MessageObject.isVoiceWebDocument(paramTL_webDocument)))
        {
          i = FileLoader.this.currentAudioLoadOperationsCount.get(paramInt);
          j = i;
          if (localFileLoadOperation != null)
          {
            if (localFileLoadOperation.wasStarted())
            {
              j = i - 1;
              FileLoader.this.currentAudioLoadOperationsCount.put(paramInt, j);
            }
          }
          else
          {
            if (((LinkedList)localObject1).isEmpty()) {
              return;
            }
            if (!((FileLoadOperation)((LinkedList)localObject1).get(0)).isForceRequest()) {
              break label216;
            }
            i = 3;
          }
        }
        for (;;)
        {
          if (j < i)
          {
            localObject2 = (FileLoadOperation)((LinkedList)localObject1).poll();
            if ((localObject2 == null) || (!((FileLoadOperation)localObject2).start())) {
              break;
            }
            j++;
            FileLoader.this.currentAudioLoadOperationsCount.put(paramInt, j);
            break;
            ((LinkedList)localObject1).remove(localFileLoadOperation);
            j = i;
            break;
            label216:
            i = 1;
            continue;
            if ((paramFileLocation != null) || (MessageObject.isImageWebDocument(paramTL_webDocument)))
            {
              i = FileLoader.this.currentPhotoLoadOperationsCount.get(paramInt);
              j = i;
              if (localFileLoadOperation != null)
              {
                if (localFileLoadOperation.wasStarted())
                {
                  j = i - 1;
                  FileLoader.this.currentPhotoLoadOperationsCount.put(paramInt, j);
                }
              }
              else
              {
                if (((LinkedList)localObject2).isEmpty()) {
                  return;
                }
                if (!((FileLoadOperation)((LinkedList)localObject2).get(0)).isForceRequest()) {
                  break label380;
                }
                i = 3;
              }
            }
            for (;;)
            {
              if (j < i)
              {
                localObject1 = (FileLoadOperation)((LinkedList)localObject2).poll();
                if ((localObject1 == null) || (!((FileLoadOperation)localObject1).start())) {
                  break;
                }
                j++;
                FileLoader.this.currentPhotoLoadOperationsCount.put(paramInt, j);
                break;
                ((LinkedList)localObject2).remove(localFileLoadOperation);
                j = i;
                break;
                label380:
                i = 1;
                continue;
                i = FileLoader.this.currentLoadOperationsCount.get(paramInt);
                j = i;
                if (localFileLoadOperation != null)
                {
                  if (localFileLoadOperation.wasStarted())
                  {
                    j = i - 1;
                    FileLoader.this.currentLoadOperationsCount.put(paramInt, j);
                    FileLoader.this.activeFileLoadOperation.remove(localFileLoadOperation);
                  }
                }
                else
                {
                  label454:
                  if (localLinkedList.isEmpty()) {
                    return;
                  }
                  if (!((FileLoadOperation)localLinkedList.get(0)).isForceRequest()) {
                    break label577;
                  }
                }
                label577:
                for (i = 3;; i = 1)
                {
                  if (j >= i) {
                    return;
                  }
                  localObject2 = (FileLoadOperation)localLinkedList.poll();
                  if ((localObject2 == null) || (!((FileLoadOperation)localObject2).start())) {
                    break label454;
                  }
                  i = j + 1;
                  FileLoader.this.currentLoadOperationsCount.put(paramInt, i);
                  j = i;
                  if (FileLoader.this.activeFileLoadOperation.contains(localObject2)) {
                    break label454;
                  }
                  FileLoader.this.activeFileLoadOperation.add(localObject2);
                  j = i;
                  break label454;
                  localLinkedList.remove(localFileLoadOperation);
                  j = i;
                  break;
                }
              }
            }
          }
        }
      }
    });
  }
  
  public static String fixFileName(String paramString)
  {
    String str = paramString;
    if (paramString != null) {
      str = paramString.replaceAll("[\001-\037<>:\"/\\\\|?*]+", "").trim();
    }
    return str;
  }
  
  public static String getAttachFileName(TLObject paramTLObject)
  {
    return getAttachFileName(paramTLObject, null);
  }
  
  public static String getAttachFileName(TLObject paramTLObject, String paramString)
  {
    int i = -1;
    TLRPC.Document localDocument;
    int j;
    if ((paramTLObject instanceof TLRPC.Document))
    {
      localDocument = (TLRPC.Document)paramTLObject;
      paramString = null;
      if (0 == 0)
      {
        paramTLObject = getDocumentFileName(localDocument);
        if (paramTLObject != null)
        {
          j = paramTLObject.lastIndexOf('.');
          if (j != -1) {}
        }
        else
        {
          paramString = "";
        }
      }
      else
      {
        paramTLObject = paramString;
        if (paramString.length() <= 1)
        {
          if (localDocument.mime_type == null) {
            break label233;
          }
          paramTLObject = localDocument.mime_type;
        }
        switch (paramTLObject.hashCode())
        {
        default: 
          switch (i)
          {
          default: 
            label100:
            paramTLObject = "";
            label127:
            if (localDocument.version == 0) {
              if (paramTLObject.length() > 1) {
                paramTLObject = localDocument.dc_id + "_" + localDocument.id + paramTLObject;
              }
            }
            break;
          }
          break;
        }
      }
    }
    for (;;)
    {
      return paramTLObject;
      paramString = paramTLObject.substring(j);
      break;
      if (!paramTLObject.equals("video/mp4")) {
        break label100;
      }
      i = 0;
      break label100;
      if (!paramTLObject.equals("audio/ogg")) {
        break label100;
      }
      i = 1;
      break label100;
      paramTLObject = ".mp4";
      break label127;
      paramTLObject = ".ogg";
      break label127;
      label233:
      paramTLObject = "";
      break label127;
      paramTLObject = localDocument.dc_id + "_" + localDocument.id;
      continue;
      if (paramTLObject.length() > 1)
      {
        paramTLObject = localDocument.dc_id + "_" + localDocument.id + "_" + localDocument.version + paramTLObject;
      }
      else
      {
        paramTLObject = localDocument.dc_id + "_" + localDocument.id + "_" + localDocument.version;
        continue;
        if ((paramTLObject instanceof TLRPC.TL_webDocument))
        {
          paramTLObject = (TLRPC.TL_webDocument)paramTLObject;
          paramTLObject = Utilities.MD5(paramTLObject.url) + "." + ImageLoader.getHttpUrlExtension(paramTLObject.url, getExtensionByMime(paramTLObject.mime_type));
        }
        else if ((paramTLObject instanceof TLRPC.PhotoSize))
        {
          paramTLObject = (TLRPC.PhotoSize)paramTLObject;
          if ((paramTLObject.location == null) || ((paramTLObject.location instanceof TLRPC.TL_fileLocationUnavailable)))
          {
            paramTLObject = "";
          }
          else
          {
            paramTLObject = new StringBuilder().append(paramTLObject.location.volume_id).append("_").append(paramTLObject.location.local_id).append(".");
            if (paramString != null) {}
            for (;;)
            {
              paramTLObject = paramString;
              break;
              paramString = "jpg";
            }
          }
        }
        else if ((paramTLObject instanceof TLRPC.FileLocation))
        {
          if ((paramTLObject instanceof TLRPC.TL_fileLocationUnavailable))
          {
            paramTLObject = "";
          }
          else
          {
            paramTLObject = (TLRPC.FileLocation)paramTLObject;
            paramTLObject = new StringBuilder().append(paramTLObject.volume_id).append("_").append(paramTLObject.local_id).append(".");
            if (paramString != null) {}
            for (;;)
            {
              paramTLObject = paramString;
              break;
              paramString = "jpg";
            }
          }
        }
        else
        {
          paramTLObject = "";
        }
      }
    }
  }
  
  private LinkedList<FileLoadOperation> getAudioLoadOperationQueue(int paramInt)
  {
    LinkedList localLinkedList1 = (LinkedList)this.audioLoadOperationQueues.get(paramInt);
    LinkedList localLinkedList2 = localLinkedList1;
    if (localLinkedList1 == null)
    {
      localLinkedList2 = new LinkedList();
      this.audioLoadOperationQueues.put(paramInt, localLinkedList2);
    }
    return localLinkedList2;
  }
  
  public static TLRPC.PhotoSize getClosestPhotoSizeWithSize(ArrayList<TLRPC.PhotoSize> paramArrayList, int paramInt)
  {
    return getClosestPhotoSizeWithSize(paramArrayList, paramInt, false);
  }
  
  public static TLRPC.PhotoSize getClosestPhotoSizeWithSize(ArrayList<TLRPC.PhotoSize> paramArrayList, int paramInt, boolean paramBoolean)
  {
    Object localObject1;
    if ((paramArrayList == null) || (paramArrayList.isEmpty()))
    {
      localObject1 = null;
      return (TLRPC.PhotoSize)localObject1;
    }
    int i = 0;
    Object localObject2 = null;
    int j = 0;
    TLRPC.PhotoSize localPhotoSize;
    int k;
    for (;;)
    {
      localObject1 = localObject2;
      if (j >= paramArrayList.size()) {
        break;
      }
      localPhotoSize = (TLRPC.PhotoSize)paramArrayList.get(j);
      if (localPhotoSize != null) {
        break label72;
      }
      k = i;
      localObject1 = localObject2;
      j++;
      localObject2 = localObject1;
      i = k;
    }
    label72:
    if (paramBoolean)
    {
      if (localPhotoSize.h >= localPhotoSize.w) {}
      for (m = localPhotoSize.w;; m = localPhotoSize.h)
      {
        if ((localObject2 != null) && ((paramInt <= 100) || (((TLRPC.PhotoSize)localObject2).location == null) || (((TLRPC.PhotoSize)localObject2).location.dc_id != Integer.MIN_VALUE)) && (!(localPhotoSize instanceof TLRPC.TL_photoCachedSize)))
        {
          localObject1 = localObject2;
          k = i;
          if (paramInt <= i) {
            break;
          }
          localObject1 = localObject2;
          k = i;
          if (i >= m) {
            break;
          }
        }
        localObject1 = localPhotoSize;
        k = m;
        break;
      }
    }
    if (localPhotoSize.w >= localPhotoSize.h) {}
    for (int m = localPhotoSize.w;; m = localPhotoSize.h)
    {
      if ((localObject2 != null) && ((paramInt <= 100) || (((TLRPC.PhotoSize)localObject2).location == null) || (((TLRPC.PhotoSize)localObject2).location.dc_id != Integer.MIN_VALUE)) && (!(localPhotoSize instanceof TLRPC.TL_photoCachedSize)))
      {
        localObject1 = localObject2;
        k = i;
        if (m > paramInt) {
          break;
        }
        localObject1 = localObject2;
        k = i;
        if (i >= m) {
          break;
        }
      }
      localObject1 = localPhotoSize;
      k = m;
      break;
    }
  }
  
  public static File getDirectory(int paramInt)
  {
    File localFile1 = (File)mediaDirs.get(paramInt);
    File localFile2 = localFile1;
    if (localFile1 == null)
    {
      localFile2 = localFile1;
      if (paramInt != 4) {
        localFile2 = (File)mediaDirs.get(4);
      }
    }
    try
    {
      if (!localFile2.isDirectory()) {
        localFile2.mkdirs();
      }
      return localFile2;
    }
    catch (Exception localException)
    {
      for (;;) {}
    }
  }
  
  public static String getDocumentExtension(TLRPC.Document paramDocument)
  {
    Object localObject = getDocumentFileName(paramDocument);
    int i = ((String)localObject).lastIndexOf('.');
    String str = null;
    if (i != -1) {
      str = ((String)localObject).substring(i + 1);
    }
    if (str != null)
    {
      localObject = str;
      if (str.length() != 0) {}
    }
    else
    {
      localObject = paramDocument.mime_type;
    }
    paramDocument = (TLRPC.Document)localObject;
    if (localObject == null) {
      paramDocument = "";
    }
    return paramDocument.toUpperCase();
  }
  
  public static String getDocumentFileName(TLRPC.Document paramDocument)
  {
    String str = null;
    Object localObject = null;
    if (paramDocument != null)
    {
      if (paramDocument.file_name != null) {
        localObject = paramDocument.file_name;
      }
    }
    else
    {
      paramDocument = fixFileName((String)localObject);
      if (paramDocument == null) {
        break label76;
      }
    }
    for (;;)
    {
      return paramDocument;
      for (int i = 0;; i++)
      {
        localObject = str;
        if (i >= paramDocument.attributes.size()) {
          break;
        }
        localObject = (TLRPC.DocumentAttribute)paramDocument.attributes.get(i);
        if ((localObject instanceof TLRPC.TL_documentAttributeFilename)) {
          str = ((TLRPC.DocumentAttribute)localObject).file_name;
        }
      }
      label76:
      paramDocument = "";
    }
  }
  
  public static String getExtensionByMime(String paramString)
  {
    int i = paramString.lastIndexOf('/');
    if (i != -1) {}
    for (paramString = paramString.substring(i + 1);; paramString = "") {
      return paramString;
    }
  }
  
  public static String getFileExtension(File paramFile)
  {
    paramFile = paramFile.getName();
    try
    {
      paramFile = paramFile.substring(paramFile.lastIndexOf('.') + 1);
      return paramFile;
    }
    catch (Exception paramFile)
    {
      for (;;)
      {
        paramFile = "";
      }
    }
  }
  
  public static FileLoader getInstance(int paramInt)
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
        localObject2 = new org/telegram/messenger/FileLoader;
        ((FileLoader)localObject2).<init>(paramInt);
        localObject1[paramInt] = localObject2;
      }
      return (FileLoader)localObject2;
    }
    finally {}
  }
  
  public static File getInternalCacheDir()
  {
    return ApplicationLoader.applicationContext.getCacheDir();
  }
  
  private LinkedList<FileLoadOperation> getLoadOperationQueue(int paramInt)
  {
    LinkedList localLinkedList1 = (LinkedList)this.loadOperationQueues.get(paramInt);
    LinkedList localLinkedList2 = localLinkedList1;
    if (localLinkedList1 == null)
    {
      localLinkedList2 = new LinkedList();
      this.loadOperationQueues.put(paramInt, localLinkedList2);
    }
    return localLinkedList2;
  }
  
  public static String getMessageFileName(TLRPC.Message paramMessage)
  {
    if (paramMessage == null) {
      paramMessage = "";
    }
    for (;;)
    {
      return paramMessage;
      if ((paramMessage instanceof TLRPC.TL_messageService))
      {
        if (paramMessage.action.photo != null)
        {
          paramMessage = paramMessage.action.photo.sizes;
          if (paramMessage.size() > 0)
          {
            paramMessage = getClosestPhotoSizeWithSize(paramMessage, AndroidUtilities.getPhotoSize());
            if (paramMessage != null) {
              paramMessage = getAttachFileName(paramMessage);
            }
          }
        }
      }
      else
      {
        if ((paramMessage.media instanceof TLRPC.TL_messageMediaDocument))
        {
          paramMessage = getAttachFileName(paramMessage.media.document);
          continue;
        }
        if ((paramMessage.media instanceof TLRPC.TL_messageMediaPhoto))
        {
          paramMessage = paramMessage.media.photo.sizes;
          if (paramMessage.size() > 0)
          {
            paramMessage = getClosestPhotoSizeWithSize(paramMessage, AndroidUtilities.getPhotoSize());
            if (paramMessage != null) {
              paramMessage = getAttachFileName(paramMessage);
            }
          }
        }
        else if ((paramMessage.media instanceof TLRPC.TL_messageMediaWebPage))
        {
          if (paramMessage.media.webpage.document != null)
          {
            paramMessage = getAttachFileName(paramMessage.media.webpage.document);
            continue;
          }
          if (paramMessage.media.webpage.photo != null)
          {
            paramMessage = paramMessage.media.webpage.photo.sizes;
            if (paramMessage.size() > 0)
            {
              paramMessage = getClosestPhotoSizeWithSize(paramMessage, AndroidUtilities.getPhotoSize());
              if (paramMessage != null) {
                paramMessage = getAttachFileName(paramMessage);
              }
            }
          }
          else if ((paramMessage.media instanceof TLRPC.TL_messageMediaInvoice))
          {
            paramMessage = getAttachFileName(((TLRPC.TL_messageMediaInvoice)paramMessage.media).photo);
          }
        }
        else if ((paramMessage.media instanceof TLRPC.TL_messageMediaInvoice))
        {
          paramMessage = ((TLRPC.TL_messageMediaInvoice)paramMessage.media).photo;
          if (paramMessage != null)
          {
            paramMessage = Utilities.MD5(paramMessage.url) + "." + ImageLoader.getHttpUrlExtension(paramMessage.url, getExtensionByMime(paramMessage.mime_type));
            continue;
          }
        }
      }
      paramMessage = "";
    }
  }
  
  public static File getPathToAttach(TLObject paramTLObject)
  {
    return getPathToAttach(paramTLObject, null, false);
  }
  
  public static File getPathToAttach(TLObject paramTLObject, String paramString, boolean paramBoolean)
  {
    Object localObject = null;
    if (paramBoolean)
    {
      localObject = getDirectory(4);
      if (localObject != null) {
        break label304;
      }
    }
    label304:
    for (paramTLObject = new File("");; paramTLObject = new File((File)localObject, getAttachFileName(paramTLObject, paramString)))
    {
      return paramTLObject;
      if ((paramTLObject instanceof TLRPC.Document))
      {
        localObject = (TLRPC.Document)paramTLObject;
        if (((TLRPC.Document)localObject).key != null)
        {
          localObject = getDirectory(4);
          break;
        }
        if (MessageObject.isVoiceDocument((TLRPC.Document)localObject))
        {
          localObject = getDirectory(1);
          break;
        }
        if (MessageObject.isVideoDocument((TLRPC.Document)localObject))
        {
          localObject = getDirectory(2);
          break;
        }
        localObject = getDirectory(3);
        break;
      }
      if ((paramTLObject instanceof TLRPC.PhotoSize))
      {
        localObject = (TLRPC.PhotoSize)paramTLObject;
        if ((((TLRPC.PhotoSize)localObject).location == null) || (((TLRPC.PhotoSize)localObject).location.key != null) || ((((TLRPC.PhotoSize)localObject).location.volume_id == -2147483648L) && (((TLRPC.PhotoSize)localObject).location.local_id < 0)) || (((TLRPC.PhotoSize)localObject).size < 0))
        {
          localObject = getDirectory(4);
          break;
        }
        localObject = getDirectory(0);
        break;
      }
      if ((paramTLObject instanceof TLRPC.FileLocation))
      {
        localObject = (TLRPC.FileLocation)paramTLObject;
        if ((((TLRPC.FileLocation)localObject).key != null) || ((((TLRPC.FileLocation)localObject).volume_id == -2147483648L) && (((TLRPC.FileLocation)localObject).local_id < 0)))
        {
          localObject = getDirectory(4);
          break;
        }
        localObject = getDirectory(0);
        break;
      }
      if (!(paramTLObject instanceof TLRPC.TL_webDocument)) {
        break;
      }
      localObject = (TLRPC.TL_webDocument)paramTLObject;
      if (((TLRPC.TL_webDocument)localObject).mime_type.startsWith("image/"))
      {
        localObject = getDirectory(0);
        break;
      }
      if (((TLRPC.TL_webDocument)localObject).mime_type.startsWith("audio/"))
      {
        localObject = getDirectory(1);
        break;
      }
      if (((TLRPC.TL_webDocument)localObject).mime_type.startsWith("video/"))
      {
        localObject = getDirectory(2);
        break;
      }
      localObject = getDirectory(3);
      break;
    }
  }
  
  public static File getPathToAttach(TLObject paramTLObject, boolean paramBoolean)
  {
    return getPathToAttach(paramTLObject, null, paramBoolean);
  }
  
  public static File getPathToMessage(TLRPC.Message paramMessage)
  {
    boolean bool1 = false;
    boolean bool2 = true;
    if (paramMessage == null) {
      paramMessage = new File("");
    }
    for (;;)
    {
      return paramMessage;
      if ((paramMessage instanceof TLRPC.TL_messageService))
      {
        if (paramMessage.action.photo != null)
        {
          paramMessage = paramMessage.action.photo.sizes;
          if (paramMessage.size() > 0)
          {
            paramMessage = getClosestPhotoSizeWithSize(paramMessage, AndroidUtilities.getPhotoSize());
            if (paramMessage != null) {
              paramMessage = getPathToAttach(paramMessage);
            }
          }
        }
      }
      else
      {
        Object localObject;
        if ((paramMessage.media instanceof TLRPC.TL_messageMediaDocument))
        {
          localObject = paramMessage.media.document;
          if (paramMessage.media.ttl_seconds != 0) {
            bool1 = true;
          }
          paramMessage = getPathToAttach((TLObject)localObject, bool1);
          continue;
        }
        if ((paramMessage.media instanceof TLRPC.TL_messageMediaPhoto))
        {
          localObject = paramMessage.media.photo.sizes;
          if (((ArrayList)localObject).size() > 0)
          {
            localObject = getClosestPhotoSizeWithSize((ArrayList)localObject, AndroidUtilities.getPhotoSize());
            if (localObject != null)
            {
              if (paramMessage.media.ttl_seconds != 0) {}
              for (bool1 = bool2;; bool1 = false)
              {
                paramMessage = getPathToAttach((TLObject)localObject, bool1);
                break;
              }
            }
          }
        }
        else if ((paramMessage.media instanceof TLRPC.TL_messageMediaWebPage))
        {
          if (paramMessage.media.webpage.document != null)
          {
            paramMessage = getPathToAttach(paramMessage.media.webpage.document);
            continue;
          }
          if (paramMessage.media.webpage.photo != null)
          {
            paramMessage = paramMessage.media.webpage.photo.sizes;
            if (paramMessage.size() > 0)
            {
              paramMessage = getClosestPhotoSizeWithSize(paramMessage, AndroidUtilities.getPhotoSize());
              if (paramMessage != null) {
                paramMessage = getPathToAttach(paramMessage);
              }
            }
          }
        }
        else if ((paramMessage.media instanceof TLRPC.TL_messageMediaInvoice))
        {
          paramMessage = getPathToAttach(((TLRPC.TL_messageMediaInvoice)paramMessage.media).photo, true);
          continue;
        }
      }
      paramMessage = new File("");
    }
  }
  
  private LinkedList<FileLoadOperation> getPhotoLoadOperationQueue(int paramInt)
  {
    LinkedList localLinkedList1 = (LinkedList)this.photoLoadOperationQueues.get(paramInt);
    LinkedList localLinkedList2 = localLinkedList1;
    if (localLinkedList1 == null)
    {
      localLinkedList2 = new LinkedList();
      this.photoLoadOperationQueues.put(paramInt, localLinkedList2);
    }
    return localLinkedList2;
  }
  
  public static FileStreamLoadOperation getStreamLoadOperation(TransferListener<? super DataSource> paramTransferListener)
  {
    return new FileStreamLoadOperation(paramTransferListener);
  }
  
  private void loadFile(final TLRPC.Document paramDocument, final TLRPC.TL_webDocument paramTL_webDocument, final TLRPC.FileLocation paramFileLocation, final String paramString, final int paramInt1, final boolean paramBoolean, final int paramInt2)
  {
    String str;
    if (paramFileLocation != null) {
      str = getAttachFileName(paramFileLocation, paramString);
    }
    for (;;)
    {
      if ((!TextUtils.isEmpty(str)) && (!str.contains("-2147483648"))) {
        this.loadOperationPathsUI.put(str, Boolean.valueOf(true));
      }
      fileLoaderQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          FileLoader.this.loadFileInternal(paramDocument, paramTL_webDocument, paramFileLocation, paramString, paramInt1, paramBoolean, null, 0, paramInt2);
        }
      });
      return;
      if (paramDocument != null) {
        str = getAttachFileName(paramDocument);
      } else if (paramTL_webDocument != null) {
        str = getAttachFileName(paramTL_webDocument);
      } else {
        str = null;
      }
    }
  }
  
  private FileLoadOperation loadFileInternal(final TLRPC.Document paramDocument, final TLRPC.TL_webDocument paramTL_webDocument, final TLRPC.FileLocation paramFileLocation, String paramString, final int paramInt1, boolean paramBoolean, FileStreamLoadOperation paramFileStreamLoadOperation, int paramInt2, int paramInt3)
  {
    final Object localObject1 = null;
    if (paramFileLocation != null)
    {
      localObject1 = getAttachFileName(paramFileLocation, paramString);
      if ((localObject1 != null) && (!((String)localObject1).contains("-2147483648"))) {
        break label63;
      }
      localObject1 = null;
    }
    for (;;)
    {
      return (FileLoadOperation)localObject1;
      if (paramDocument != null)
      {
        localObject1 = getAttachFileName(paramDocument);
        break;
      }
      if (paramTL_webDocument == null) {
        break;
      }
      localObject1 = getAttachFileName(paramTL_webDocument);
      break;
      label63:
      if ((!TextUtils.isEmpty((CharSequence)localObject1)) && (!((String)localObject1).contains("-2147483648"))) {
        this.loadOperationPathsUI.put(localObject1, Boolean.valueOf(true));
      }
      Object localObject2 = (FileLoadOperation)this.loadOperationPaths.get(localObject1);
      Object localObject3;
      Object localObject4;
      if (localObject2 != null)
      {
        if (paramInt2 == 0)
        {
          localObject1 = localObject2;
          if (!paramBoolean) {}
        }
        else
        {
          paramInt3 = ((FileLoadOperation)localObject2).getDatacenterId();
          paramString = getAudioLoadOperationQueue(paramInt3);
          localObject3 = getPhotoLoadOperationQueue(paramInt3);
          localObject4 = getLoadOperationQueue(paramInt3);
          ((FileLoadOperation)localObject2).setForceRequest(true);
          if ((MessageObject.isVoiceDocument(paramDocument)) || (MessageObject.isVoiceWebDocument(paramTL_webDocument))) {
            paramDocument = paramString;
          }
          for (;;)
          {
            localObject1 = localObject2;
            if (paramDocument == null) {
              break;
            }
            paramInt1 = paramDocument.indexOf(localObject2);
            if (paramInt1 <= 0) {
              break label439;
            }
            paramDocument.remove(paramInt1);
            if (paramInt2 == 0) {
              break label425;
            }
            if (paramDocument != paramString) {
              break label288;
            }
            localObject1 = localObject2;
            if (!((FileLoadOperation)localObject2).start(paramFileStreamLoadOperation, paramInt2)) {
              break;
            }
            this.currentAudioLoadOperationsCount.put(paramInt3, this.currentAudioLoadOperationsCount.get(paramInt3) + 1);
            localObject1 = localObject2;
            break;
            if ((paramFileLocation != null) || (MessageObject.isImageWebDocument(paramTL_webDocument))) {
              paramDocument = (TLRPC.Document)localObject3;
            } else {
              paramDocument = (TLRPC.Document)localObject4;
            }
          }
          label288:
          if (paramDocument == localObject3)
          {
            localObject1 = localObject2;
            if (((FileLoadOperation)localObject2).start(paramFileStreamLoadOperation, paramInt2))
            {
              this.currentPhotoLoadOperationsCount.put(paramInt3, this.currentPhotoLoadOperationsCount.get(paramInt3) + 1);
              localObject1 = localObject2;
            }
          }
          else
          {
            if (((FileLoadOperation)localObject2).start(paramFileStreamLoadOperation, paramInt2)) {
              this.currentLoadOperationsCount.put(paramInt3, this.currentLoadOperationsCount.get(paramInt3) + 1);
            }
            localObject1 = localObject2;
            if (((FileLoadOperation)localObject2).wasStarted())
            {
              localObject1 = localObject2;
              if (!this.activeFileLoadOperation.contains(localObject2))
              {
                if (paramFileStreamLoadOperation != null) {
                  pauseCurrentFileLoadOperations((FileLoadOperation)localObject2);
                }
                this.activeFileLoadOperation.add(localObject2);
                localObject1 = localObject2;
                continue;
                label425:
                paramDocument.add(0, localObject2);
                localObject1 = localObject2;
                continue;
                label439:
                if (paramFileStreamLoadOperation != null) {
                  pauseCurrentFileLoadOperations((FileLoadOperation)localObject2);
                }
                ((FileLoadOperation)localObject2).start(paramFileStreamLoadOperation, paramInt2);
                localObject1 = localObject2;
                if (paramDocument == localObject4)
                {
                  localObject1 = localObject2;
                  if (!this.activeFileLoadOperation.contains(localObject2))
                  {
                    this.activeFileLoadOperation.add(localObject2);
                    localObject1 = localObject2;
                  }
                }
              }
            }
          }
        }
      }
      else
      {
        localObject4 = getDirectory(4);
        localObject3 = localObject4;
        int i = 4;
        if (paramFileLocation != null)
        {
          paramString = new FileLoadOperation(paramFileLocation, paramString, paramInt1);
          paramInt1 = 0;
          label537:
          if (paramInt3 != 0) {
            break label810;
          }
          localObject2 = getDirectory(paramInt1);
          label549:
          paramString.setPaths(this.currentAccount, (File)localObject2, (File)localObject4);
          paramString.setDelegate(new FileLoadOperation.FileLoadOperationDelegate()
          {
            public void didChangedLoadProgress(FileLoadOperation paramAnonymousFileLoadOperation, float paramAnonymousFloat)
            {
              if (FileLoader.this.delegate != null) {
                FileLoader.this.delegate.fileLoadProgressChanged(localObject1, paramAnonymousFloat);
              }
            }
            
            public void didFailedLoadingFile(FileLoadOperation paramAnonymousFileLoadOperation, int paramAnonymousInt)
            {
              FileLoader.this.loadOperationPathsUI.remove(localObject1);
              FileLoader.this.checkDownloadQueue(paramAnonymousFileLoadOperation.getDatacenterId(), paramDocument, paramTL_webDocument, paramFileLocation, localObject1);
              if (FileLoader.this.delegate != null) {
                FileLoader.this.delegate.fileDidFailedLoad(localObject1, paramAnonymousInt);
              }
            }
            
            public void didFinishLoadingFile(FileLoadOperation paramAnonymousFileLoadOperation, File paramAnonymousFile)
            {
              FileLoader.this.loadOperationPathsUI.remove(localObject1);
              if (FileLoader.this.delegate != null) {
                FileLoader.this.delegate.fileDidLoaded(localObject1, paramAnonymousFile, paramInt1);
              }
              FileLoader.this.checkDownloadQueue(paramAnonymousFileLoadOperation.getDatacenterId(), paramDocument, paramTL_webDocument, paramFileLocation, localObject1);
            }
          });
          i = paramString.getDatacenterId();
          localObject2 = getAudioLoadOperationQueue(i);
          paramDocument = getPhotoLoadOperationQueue(i);
          localObject3 = getLoadOperationQueue(i);
          this.loadOperationPaths.put(localObject1, paramString);
          if (!paramBoolean) {
            break label833;
          }
          paramInt3 = 3;
        }
        for (;;)
        {
          if (paramInt1 == 1)
          {
            paramInt1 = this.currentAudioLoadOperationsCount.get(i);
            if ((paramInt2 != 0) || (paramInt1 < paramInt3))
            {
              localObject1 = paramString;
              if (!paramString.start(paramFileStreamLoadOperation, paramInt2)) {
                break;
              }
              this.currentAudioLoadOperationsCount.put(i, paramInt1 + 1);
              localObject1 = paramString;
              break;
              if (paramDocument != null)
              {
                paramString = new FileLoadOperation(paramDocument);
                if (MessageObject.isVoiceDocument(paramDocument))
                {
                  paramInt1 = 1;
                  break label537;
                }
                if (MessageObject.isVideoDocument(paramDocument))
                {
                  paramInt1 = 2;
                  break label537;
                }
                paramInt1 = 3;
                break label537;
              }
              paramString = (String)localObject2;
              paramInt1 = i;
              if (paramTL_webDocument == null) {
                break label537;
              }
              paramString = new FileLoadOperation(paramTL_webDocument);
              if (MessageObject.isVoiceWebDocument(paramTL_webDocument))
              {
                paramInt1 = 1;
                break label537;
              }
              if (MessageObject.isVideoWebDocument(paramTL_webDocument))
              {
                paramInt1 = 2;
                break label537;
              }
              if (MessageObject.isImageWebDocument(paramTL_webDocument))
              {
                paramInt1 = 0;
                break label537;
              }
              paramInt1 = 3;
              break label537;
              label810:
              localObject2 = localObject3;
              if (paramInt3 != 2) {
                break label549;
              }
              paramString.setEncryptFile(true);
              localObject2 = localObject3;
              break label549;
              label833:
              paramInt3 = 1;
              continue;
            }
            if (paramBoolean)
            {
              ((LinkedList)localObject2).add(0, paramString);
              localObject1 = paramString;
              break;
            }
            ((LinkedList)localObject2).add(paramString);
            localObject1 = paramString;
            break;
          }
        }
        if ((paramFileLocation != null) || (MessageObject.isImageWebDocument(paramTL_webDocument)))
        {
          paramInt1 = this.currentPhotoLoadOperationsCount.get(i);
          if ((paramInt2 != 0) || (paramInt1 < paramInt3))
          {
            localObject1 = paramString;
            if (paramString.start(paramFileStreamLoadOperation, paramInt2))
            {
              this.currentPhotoLoadOperationsCount.put(i, paramInt1 + 1);
              localObject1 = paramString;
            }
          }
          else if (paramBoolean)
          {
            paramDocument.add(0, paramString);
            localObject1 = paramString;
          }
          else
          {
            paramDocument.add(paramString);
            localObject1 = paramString;
          }
        }
        else
        {
          paramInt1 = this.currentLoadOperationsCount.get(i);
          if ((paramInt2 != 0) || (paramInt1 < paramInt3))
          {
            if (paramString.start(paramFileStreamLoadOperation, paramInt2))
            {
              this.currentLoadOperationsCount.put(i, paramInt1 + 1);
              this.activeFileLoadOperation.add(paramString);
            }
            localObject1 = paramString;
            if (paramString.wasStarted())
            {
              localObject1 = paramString;
              if (paramFileStreamLoadOperation != null)
              {
                pauseCurrentFileLoadOperations(paramString);
                localObject1 = paramString;
              }
            }
          }
          else if (paramBoolean)
          {
            ((LinkedList)localObject3).add(0, paramString);
            localObject1 = paramString;
          }
          else
          {
            ((LinkedList)localObject3).add(paramString);
            localObject1 = paramString;
          }
        }
      }
    }
  }
  
  private void pauseCurrentFileLoadOperations(FileLoadOperation paramFileLoadOperation)
  {
    int i = 0;
    if (i < this.activeFileLoadOperation.size())
    {
      FileLoadOperation localFileLoadOperation = (FileLoadOperation)this.activeFileLoadOperation.get(i);
      if (localFileLoadOperation == paramFileLoadOperation) {}
      for (;;)
      {
        i++;
        break;
        this.activeFileLoadOperation.remove(localFileLoadOperation);
        int j = i - 1;
        localFileLoadOperation.pause();
        int k = localFileLoadOperation.getDatacenterId();
        getLoadOperationQueue(k).add(0, localFileLoadOperation);
        i = j;
        if (localFileLoadOperation.wasStarted())
        {
          this.currentLoadOperationsCount.put(k, this.currentLoadOperationsCount.get(k) - 1);
          i = j;
        }
      }
    }
  }
  
  public static void setMediaDirs(SparseArray<File> paramSparseArray)
  {
    mediaDirs = paramSparseArray;
  }
  
  public void cancelLoadFile(TLRPC.Document paramDocument)
  {
    cancelLoadFile(paramDocument, null, null, null);
  }
  
  public void cancelLoadFile(TLRPC.FileLocation paramFileLocation, String paramString)
  {
    cancelLoadFile(null, null, paramFileLocation, paramString);
  }
  
  public void cancelLoadFile(TLRPC.PhotoSize paramPhotoSize)
  {
    cancelLoadFile(null, null, paramPhotoSize.location, null);
  }
  
  public void cancelLoadFile(TLRPC.TL_webDocument paramTL_webDocument)
  {
    cancelLoadFile(null, paramTL_webDocument, null, null);
  }
  
  public void cancelUploadFile(final String paramString, final boolean paramBoolean)
  {
    fileLoaderQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        if (!paramBoolean) {}
        for (FileUploadOperation localFileUploadOperation = (FileUploadOperation)FileLoader.this.uploadOperationPaths.get(paramString);; localFileUploadOperation = (FileUploadOperation)FileLoader.this.uploadOperationPathsEnc.get(paramString))
        {
          FileLoader.this.uploadSizes.remove(paramString);
          if (localFileUploadOperation != null)
          {
            FileLoader.this.uploadOperationPathsEnc.remove(paramString);
            FileLoader.this.uploadOperationQueue.remove(localFileUploadOperation);
            FileLoader.this.uploadSmallOperationQueue.remove(localFileUploadOperation);
            localFileUploadOperation.cancel();
          }
          return;
        }
      }
    });
  }
  
  public void checkUploadNewDataAvailable(final String paramString, final boolean paramBoolean, final long paramLong1, long paramLong2)
  {
    fileLoaderQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        FileUploadOperation localFileUploadOperation;
        if (paramBoolean)
        {
          localFileUploadOperation = (FileUploadOperation)FileLoader.this.uploadOperationPathsEnc.get(paramString);
          if (localFileUploadOperation == null) {
            break label63;
          }
          localFileUploadOperation.checkNewDataAvailable(paramLong1, this.val$finalSize);
        }
        for (;;)
        {
          return;
          localFileUploadOperation = (FileUploadOperation)FileLoader.this.uploadOperationPaths.get(paramString);
          break;
          label63:
          if (this.val$finalSize != 0L) {
            FileLoader.this.uploadSizes.put(paramString, Long.valueOf(this.val$finalSize));
          }
        }
      }
    });
  }
  
  public void deleteFiles(final ArrayList<File> paramArrayList, final int paramInt)
  {
    if ((paramArrayList == null) || (paramArrayList.isEmpty())) {}
    for (;;)
    {
      return;
      fileLoaderQueue.postRunnable(new Runnable()
      {
        /* Error */
        public void run()
        {
          // Byte code:
          //   0: iconst_0
          //   1: istore_1
          //   2: iload_1
          //   3: aload_0
          //   4: getfield 23	org/telegram/messenger/FileLoader$9:val$files	Ljava/util/ArrayList;
          //   7: invokevirtual 38	java/util/ArrayList:size	()I
          //   10: if_icmpge +237 -> 247
          //   13: aload_0
          //   14: getfield 23	org/telegram/messenger/FileLoader$9:val$files	Ljava/util/ArrayList;
          //   17: iload_1
          //   18: invokevirtual 42	java/util/ArrayList:get	(I)Ljava/lang/Object;
          //   21: checkcast 44	java/io/File
          //   24: astore_2
          //   25: new 44	java/io/File
          //   28: dup
          //   29: new 46	java/lang/StringBuilder
          //   32: dup
          //   33: invokespecial 47	java/lang/StringBuilder:<init>	()V
          //   36: aload_2
          //   37: invokevirtual 51	java/io/File:getAbsolutePath	()Ljava/lang/String;
          //   40: invokevirtual 55	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   43: ldc 57
          //   45: invokevirtual 55	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   48: invokevirtual 60	java/lang/StringBuilder:toString	()Ljava/lang/String;
          //   51: invokespecial 63	java/io/File:<init>	(Ljava/lang/String;)V
          //   54: astore_3
          //   55: aload_3
          //   56: invokevirtual 67	java/io/File:exists	()Z
          //   59: ifeq +151 -> 210
          //   62: aload_3
          //   63: invokevirtual 70	java/io/File:delete	()Z
          //   66: ifne +7 -> 73
          //   69: aload_3
          //   70: invokevirtual 73	java/io/File:deleteOnExit	()V
          //   73: new 44	java/io/File
          //   76: astore 4
          //   78: invokestatic 77	org/telegram/messenger/FileLoader:getInternalCacheDir	()Ljava/io/File;
          //   81: astore 5
          //   83: new 46	java/lang/StringBuilder
          //   86: astore_3
          //   87: aload_3
          //   88: invokespecial 47	java/lang/StringBuilder:<init>	()V
          //   91: aload 4
          //   93: aload 5
          //   95: aload_3
          //   96: aload_2
          //   97: invokevirtual 80	java/io/File:getName	()Ljava/lang/String;
          //   100: invokevirtual 55	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   103: ldc 82
          //   105: invokevirtual 55	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   108: invokevirtual 60	java/lang/StringBuilder:toString	()Ljava/lang/String;
          //   111: invokespecial 85	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
          //   114: aload 4
          //   116: invokevirtual 70	java/io/File:delete	()Z
          //   119: ifne +8 -> 127
          //   122: aload 4
          //   124: invokevirtual 73	java/io/File:deleteOnExit	()V
          //   127: new 44	java/io/File
          //   130: astore_3
          //   131: aload_2
          //   132: invokevirtual 88	java/io/File:getParentFile	()Ljava/io/File;
          //   135: astore 5
          //   137: new 46	java/lang/StringBuilder
          //   140: astore 4
          //   142: aload 4
          //   144: invokespecial 47	java/lang/StringBuilder:<init>	()V
          //   147: aload_3
          //   148: aload 5
          //   150: aload 4
          //   152: ldc 90
          //   154: invokevirtual 55	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   157: aload_2
          //   158: invokevirtual 80	java/io/File:getName	()Ljava/lang/String;
          //   161: invokevirtual 55	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   164: invokevirtual 60	java/lang/StringBuilder:toString	()Ljava/lang/String;
          //   167: invokespecial 85	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
          //   170: aload_3
          //   171: invokevirtual 67	java/io/File:exists	()Z
          //   174: ifeq +14 -> 188
          //   177: aload_3
          //   178: invokevirtual 70	java/io/File:delete	()Z
          //   181: ifne +7 -> 188
          //   184: aload_3
          //   185: invokevirtual 73	java/io/File:deleteOnExit	()V
          //   188: iinc 1 1
          //   191: goto -189 -> 2
          //   194: astore_3
          //   195: aload_3
          //   196: invokestatic 96	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
          //   199: goto -126 -> 73
          //   202: astore_3
          //   203: aload_3
          //   204: invokestatic 96	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
          //   207: goto -80 -> 127
          //   210: aload_2
          //   211: invokevirtual 67	java/io/File:exists	()Z
          //   214: ifeq -87 -> 127
          //   217: aload_2
          //   218: invokevirtual 70	java/io/File:delete	()Z
          //   221: ifne -94 -> 127
          //   224: aload_2
          //   225: invokevirtual 73	java/io/File:deleteOnExit	()V
          //   228: goto -101 -> 127
          //   231: astore_3
          //   232: aload_3
          //   233: invokestatic 96	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
          //   236: goto -109 -> 127
          //   239: astore_2
          //   240: aload_2
          //   241: invokestatic 96	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
          //   244: goto -56 -> 188
          //   247: aload_0
          //   248: getfield 25	org/telegram/messenger/FileLoader$9:val$type	I
          //   251: iconst_2
          //   252: if_icmpne +9 -> 261
          //   255: invokestatic 102	org/telegram/messenger/ImageLoader:getInstance	()Lorg/telegram/messenger/ImageLoader;
          //   258: invokevirtual 105	org/telegram/messenger/ImageLoader:clearMemory	()V
          //   261: return
          // Local variable table:
          //   start	length	slot	name	signature
          //   0	262	0	this	9
          //   1	188	1	i	int
          //   24	201	2	localFile1	File
          //   239	2	2	localException1	Exception
          //   54	131	3	localObject1	Object
          //   194	2	3	localException2	Exception
          //   202	2	3	localException3	Exception
          //   231	2	3	localException4	Exception
          //   76	75	4	localObject2	Object
          //   81	68	5	localFile2	File
          // Exception table:
          //   from	to	target	type
          //   62	73	194	java/lang/Exception
          //   73	127	202	java/lang/Exception
          //   217	228	231	java/lang/Exception
          //   127	188	239	java/lang/Exception
        }
      });
    }
  }
  
  public float getBufferedProgressFromPosition(float paramFloat, String paramString)
  {
    float f = 0.0F;
    if (TextUtils.isEmpty(paramString)) {}
    for (;;)
    {
      return f;
      paramString = (FileLoadOperation)this.loadOperationPaths.get(paramString);
      if (paramString != null) {
        f = paramString.getDownloadedLengthFromOffset(paramFloat);
      }
    }
  }
  
  public boolean isLoadingFile(String paramString)
  {
    return this.loadOperationPathsUI.containsKey(paramString);
  }
  
  public void loadFile(TLRPC.Document paramDocument, boolean paramBoolean, int paramInt)
  {
    if (paramDocument == null) {}
    for (;;)
    {
      return;
      int i = paramInt;
      if (paramInt == 0)
      {
        i = paramInt;
        if (paramDocument != null)
        {
          i = paramInt;
          if (paramDocument.key != null) {
            i = 1;
          }
        }
      }
      loadFile(paramDocument, null, null, null, 0, paramBoolean, i);
    }
  }
  
  public void loadFile(TLRPC.FileLocation paramFileLocation, String paramString, int paramInt1, int paramInt2)
  {
    if (paramFileLocation == null) {}
    for (;;)
    {
      return;
      int i = paramInt2;
      if (paramInt2 == 0) {
        if (paramInt1 != 0)
        {
          i = paramInt2;
          if (paramFileLocation != null)
          {
            i = paramInt2;
            if (paramFileLocation.key == null) {}
          }
        }
        else
        {
          i = 1;
        }
      }
      loadFile(null, null, paramFileLocation, paramString, paramInt1, true, i);
    }
  }
  
  public void loadFile(TLRPC.PhotoSize paramPhotoSize, String paramString, int paramInt)
  {
    if (paramPhotoSize == null) {}
    for (;;)
    {
      return;
      int i = paramInt;
      if (paramInt == 0)
      {
        i = paramInt;
        if (paramPhotoSize != null) {
          if (paramPhotoSize.size != 0)
          {
            i = paramInt;
            if (paramPhotoSize.location.key == null) {}
          }
          else
          {
            i = 1;
          }
        }
      }
      loadFile(null, null, paramPhotoSize.location, paramString, paramPhotoSize.size, false, i);
    }
  }
  
  public void loadFile(TLRPC.TL_webDocument paramTL_webDocument, boolean paramBoolean, int paramInt)
  {
    loadFile(null, paramTL_webDocument, null, null, 0, paramBoolean, paramInt);
  }
  
  protected FileLoadOperation loadStreamFile(final FileStreamLoadOperation paramFileStreamLoadOperation, final TLRPC.Document paramDocument, final int paramInt)
  {
    final CountDownLatch localCountDownLatch = new CountDownLatch(1);
    final FileLoadOperation[] arrayOfFileLoadOperation = new FileLoadOperation[1];
    fileLoaderQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        arrayOfFileLoadOperation[0] = FileLoader.this.loadFileInternal(paramDocument, null, null, null, 0, true, paramFileStreamLoadOperation, paramInt, 0);
        localCountDownLatch.countDown();
      }
    });
    try
    {
      localCountDownLatch.await();
      return arrayOfFileLoadOperation[0];
    }
    catch (Exception paramFileStreamLoadOperation)
    {
      for (;;)
      {
        FileLog.e(paramFileStreamLoadOperation);
      }
    }
  }
  
  public void setDelegate(FileLoaderDelegate paramFileLoaderDelegate)
  {
    this.delegate = paramFileLoaderDelegate;
  }
  
  public void uploadFile(String paramString, boolean paramBoolean1, boolean paramBoolean2, int paramInt)
  {
    uploadFile(paramString, paramBoolean1, paramBoolean2, 0, paramInt);
  }
  
  public void uploadFile(final String paramString, final boolean paramBoolean1, final boolean paramBoolean2, final int paramInt1, final int paramInt2)
  {
    if (paramString == null) {}
    for (;;)
    {
      return;
      fileLoaderQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          if (paramBoolean1) {
            if (!FileLoader.this.uploadOperationPathsEnc.containsKey(paramString)) {
              break label42;
            }
          }
          for (;;)
          {
            return;
            if (!FileLoader.this.uploadOperationPaths.containsKey(paramString))
            {
              label42:
              int i = paramInt1;
              int j = i;
              if (i != 0)
              {
                j = i;
                if ((Long)FileLoader.this.uploadSizes.get(paramString) != null)
                {
                  j = 0;
                  FileLoader.this.uploadSizes.remove(paramString);
                }
              }
              FileUploadOperation localFileUploadOperation = new FileUploadOperation(FileLoader.this.currentAccount, paramString, paramBoolean1, j, paramInt2);
              if (paramBoolean1) {
                FileLoader.this.uploadOperationPathsEnc.put(paramString, localFileUploadOperation);
              }
              for (;;)
              {
                localFileUploadOperation.setDelegate(new FileUploadOperation.FileUploadOperationDelegate()
                {
                  public void didChangedUploadProgress(FileUploadOperation paramAnonymous2FileUploadOperation, float paramAnonymous2Float)
                  {
                    if (FileLoader.this.delegate != null) {
                      FileLoader.this.delegate.fileUploadProgressChanged(FileLoader.3.this.val$location, paramAnonymous2Float, FileLoader.3.this.val$encrypted);
                    }
                  }
                  
                  public void didFailedUploadingFile(FileUploadOperation paramAnonymous2FileUploadOperation)
                  {
                    FileLoader.fileLoaderQueue.postRunnable(new Runnable()
                    {
                      public void run()
                      {
                        FileUploadOperation localFileUploadOperation;
                        if (FileLoader.3.this.val$encrypted)
                        {
                          FileLoader.this.uploadOperationPathsEnc.remove(FileLoader.3.this.val$location);
                          if (FileLoader.this.delegate != null) {
                            FileLoader.this.delegate.fileDidFailedUpload(FileLoader.3.this.val$location, FileLoader.3.this.val$encrypted);
                          }
                          if (!FileLoader.3.this.val$small) {
                            break label211;
                          }
                          FileLoader.access$610(FileLoader.this);
                          if (FileLoader.this.currentUploadSmallOperationsCount < 1)
                          {
                            localFileUploadOperation = (FileUploadOperation)FileLoader.this.uploadSmallOperationQueue.poll();
                            if (localFileUploadOperation != null)
                            {
                              FileLoader.access$608(FileLoader.this);
                              localFileUploadOperation.start();
                            }
                          }
                        }
                        for (;;)
                        {
                          return;
                          FileLoader.this.uploadOperationPaths.remove(FileLoader.3.this.val$location);
                          break;
                          label211:
                          FileLoader.access$710(FileLoader.this);
                          if (FileLoader.this.currentUploadOperationsCount < 1)
                          {
                            localFileUploadOperation = (FileUploadOperation)FileLoader.this.uploadOperationQueue.poll();
                            if (localFileUploadOperation != null)
                            {
                              FileLoader.access$708(FileLoader.this);
                              localFileUploadOperation.start();
                            }
                          }
                        }
                      }
                    });
                  }
                  
                  public void didFinishUploadingFile(final FileUploadOperation paramAnonymous2FileUploadOperation, final TLRPC.InputFile paramAnonymous2InputFile, final TLRPC.InputEncryptedFile paramAnonymous2InputEncryptedFile, final byte[] paramAnonymous2ArrayOfByte1, final byte[] paramAnonymous2ArrayOfByte2)
                  {
                    FileLoader.fileLoaderQueue.postRunnable(new Runnable()
                    {
                      public void run()
                      {
                        FileUploadOperation localFileUploadOperation;
                        if (FileLoader.3.this.val$encrypted)
                        {
                          FileLoader.this.uploadOperationPathsEnc.remove(FileLoader.3.this.val$location);
                          if (!FileLoader.3.this.val$small) {
                            break label224;
                          }
                          FileLoader.access$610(FileLoader.this);
                          if (FileLoader.this.currentUploadSmallOperationsCount < 1)
                          {
                            localFileUploadOperation = (FileUploadOperation)FileLoader.this.uploadSmallOperationQueue.poll();
                            if (localFileUploadOperation != null)
                            {
                              FileLoader.access$608(FileLoader.this);
                              localFileUploadOperation.start();
                            }
                          }
                        }
                        for (;;)
                        {
                          if (FileLoader.this.delegate != null) {
                            FileLoader.this.delegate.fileDidUploaded(FileLoader.3.this.val$location, paramAnonymous2InputFile, paramAnonymous2InputEncryptedFile, paramAnonymous2ArrayOfByte1, paramAnonymous2ArrayOfByte2, paramAnonymous2FileUploadOperation.getTotalFileSize());
                          }
                          return;
                          FileLoader.this.uploadOperationPaths.remove(FileLoader.3.this.val$location);
                          break;
                          label224:
                          FileLoader.access$710(FileLoader.this);
                          if (FileLoader.this.currentUploadOperationsCount < 1)
                          {
                            localFileUploadOperation = (FileUploadOperation)FileLoader.this.uploadOperationQueue.poll();
                            if (localFileUploadOperation != null)
                            {
                              FileLoader.access$708(FileLoader.this);
                              localFileUploadOperation.start();
                            }
                          }
                        }
                      }
                    });
                  }
                });
                if (!paramBoolean2) {
                  break label222;
                }
                if (FileLoader.this.currentUploadSmallOperationsCount >= 1) {
                  break label207;
                }
                FileLoader.access$608(FileLoader.this);
                localFileUploadOperation.start();
                break;
                FileLoader.this.uploadOperationPaths.put(paramString, localFileUploadOperation);
              }
              label207:
              FileLoader.this.uploadSmallOperationQueue.add(localFileUploadOperation);
              continue;
              label222:
              if (FileLoader.this.currentUploadOperationsCount < 1)
              {
                FileLoader.access$708(FileLoader.this);
                localFileUploadOperation.start();
              }
              else
              {
                FileLoader.this.uploadOperationQueue.add(localFileUploadOperation);
              }
            }
          }
        }
      });
    }
  }
  
  public static abstract interface FileLoaderDelegate
  {
    public abstract void fileDidFailedLoad(String paramString, int paramInt);
    
    public abstract void fileDidFailedUpload(String paramString, boolean paramBoolean);
    
    public abstract void fileDidLoaded(String paramString, File paramFile, int paramInt);
    
    public abstract void fileDidUploaded(String paramString, TLRPC.InputFile paramInputFile, TLRPC.InputEncryptedFile paramInputEncryptedFile, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, long paramLong);
    
    public abstract void fileLoadProgressChanged(String paramString, float paramFloat);
    
    public abstract void fileUploadProgressChanged(String paramString, float paramFloat, boolean paramBoolean);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/FileLoader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */