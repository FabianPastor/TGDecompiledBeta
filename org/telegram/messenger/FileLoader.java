package org.telegram.messenger;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
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
import org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC.TL_messageService;
import org.telegram.tgnet.TLRPC.TL_photoCachedSize;
import org.telegram.tgnet.TLRPC.WebPage;

public class FileLoader
{
  private static volatile FileLoader Instance = null;
  public static final int MEDIA_DIR_AUDIO = 1;
  public static final int MEDIA_DIR_CACHE = 4;
  public static final int MEDIA_DIR_DOCUMENT = 3;
  public static final int MEDIA_DIR_IMAGE = 0;
  public static final int MEDIA_DIR_VIDEO = 2;
  private LinkedList<FileLoadOperation> audioLoadOperationQueue = new LinkedList();
  private int currentAudioLoadOperationsCount = 0;
  private int currentLoadOperationsCount = 0;
  private int currentPhotoLoadOperationsCount = 0;
  private int currentUploadOperationsCount = 0;
  private int currentUploadSmallOperationsCount = 0;
  private FileLoaderDelegate delegate = null;
  private volatile DispatchQueue fileLoaderQueue = new DispatchQueue("fileUploadQueue");
  private ConcurrentHashMap<String, FileLoadOperation> loadOperationPaths = new ConcurrentHashMap();
  private LinkedList<FileLoadOperation> loadOperationQueue = new LinkedList();
  private HashMap<Integer, File> mediaDirs = null;
  private LinkedList<FileLoadOperation> photoLoadOperationQueue = new LinkedList();
  private ConcurrentHashMap<String, FileUploadOperation> uploadOperationPaths = new ConcurrentHashMap();
  private ConcurrentHashMap<String, FileUploadOperation> uploadOperationPathsEnc = new ConcurrentHashMap();
  private LinkedList<FileUploadOperation> uploadOperationQueue = new LinkedList();
  private HashMap<String, Long> uploadSizes = new HashMap();
  private LinkedList<FileUploadOperation> uploadSmallOperationQueue = new LinkedList();
  
  private void cancelLoadFile(final TLRPC.Document paramDocument, final TLRPC.FileLocation paramFileLocation, final String paramString)
  {
    if ((paramFileLocation == null) && (paramDocument == null)) {
      return;
    }
    this.fileLoaderQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        Object localObject = null;
        if (paramFileLocation != null) {}
        do
        {
          localObject = FileLoader.getAttachFileName(paramFileLocation, paramString);
          while (localObject == null)
          {
            return;
            if (paramDocument != null) {
              localObject = FileLoader.getAttachFileName(paramDocument);
            }
          }
          localObject = (FileLoadOperation)FileLoader.this.loadOperationPaths.remove(localObject);
        } while (localObject == null);
        if (MessageObject.isVoiceDocument(paramDocument)) {
          if (!FileLoader.this.audioLoadOperationQueue.remove(localObject)) {
            FileLoader.access$1110(FileLoader.this);
          }
        }
        for (;;)
        {
          ((FileLoadOperation)localObject).cancel();
          return;
          if (paramFileLocation != null)
          {
            if (!FileLoader.this.photoLoadOperationQueue.remove(localObject)) {
              FileLoader.access$1310(FileLoader.this);
            }
          }
          else if (!FileLoader.this.loadOperationQueue.remove(localObject)) {
            FileLoader.access$1510(FileLoader.this);
          }
        }
      }
    });
  }
  
  private void checkDownloadQueue(final TLRPC.Document paramDocument, final TLRPC.FileLocation paramFileLocation, final String paramString)
  {
    this.fileLoaderQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        FileLoadOperation localFileLoadOperation = (FileLoadOperation)FileLoader.this.loadOperationPaths.remove(paramString);
        int i;
        if (MessageObject.isVoiceDocument(paramDocument))
        {
          if (localFileLoadOperation != null)
          {
            if (localFileLoadOperation.wasStarted()) {
              FileLoader.access$1110(FileLoader.this);
            }
          }
          else
          {
            if (FileLoader.this.audioLoadOperationQueue.isEmpty()) {
              return;
            }
            if (!((FileLoadOperation)FileLoader.this.audioLoadOperationQueue.get(0)).isForceRequest()) {
              break label144;
            }
          }
          label144:
          for (i = 3;; i = 1)
          {
            if (FileLoader.this.currentAudioLoadOperationsCount >= i) {
              return;
            }
            localFileLoadOperation = (FileLoadOperation)FileLoader.this.audioLoadOperationQueue.poll();
            if ((localFileLoadOperation == null) || (!localFileLoadOperation.start())) {
              break;
            }
            FileLoader.access$1108(FileLoader.this);
            break;
            FileLoader.this.audioLoadOperationQueue.remove(localFileLoadOperation);
            break;
          }
        }
        else if (paramFileLocation != null)
        {
          if (localFileLoadOperation != null)
          {
            if (localFileLoadOperation.wasStarted()) {
              FileLoader.access$1310(FileLoader.this);
            }
          }
          else
          {
            if (FileLoader.this.photoLoadOperationQueue.isEmpty()) {
              return;
            }
            if (!((FileLoadOperation)FileLoader.this.photoLoadOperationQueue.get(0)).isForceRequest()) {
              break label272;
            }
          }
          label272:
          for (i = 3;; i = 1)
          {
            if (FileLoader.this.currentPhotoLoadOperationsCount >= i) {
              return;
            }
            localFileLoadOperation = (FileLoadOperation)FileLoader.this.photoLoadOperationQueue.poll();
            if ((localFileLoadOperation == null) || (!localFileLoadOperation.start())) {
              break;
            }
            FileLoader.access$1308(FileLoader.this);
            break;
            FileLoader.this.photoLoadOperationQueue.remove(localFileLoadOperation);
            break;
          }
        }
        else
        {
          if (localFileLoadOperation != null)
          {
            if (localFileLoadOperation.wasStarted()) {
              FileLoader.access$1510(FileLoader.this);
            }
          }
          else
          {
            if (FileLoader.this.loadOperationQueue.isEmpty()) {
              return;
            }
            if (!((FileLoadOperation)FileLoader.this.loadOperationQueue.get(0)).isForceRequest()) {
              break label393;
            }
          }
          label393:
          for (i = 3;; i = 1)
          {
            if (FileLoader.this.currentLoadOperationsCount >= i) {
              return;
            }
            localFileLoadOperation = (FileLoadOperation)FileLoader.this.loadOperationQueue.poll();
            if ((localFileLoadOperation == null) || (!localFileLoadOperation.start())) {
              break;
            }
            FileLoader.access$1508(FileLoader.this);
            break;
            FileLoader.this.loadOperationQueue.remove(localFileLoadOperation);
            break;
          }
        }
      }
    });
  }
  
  public static String getAttachFileName(TLObject paramTLObject)
  {
    return getAttachFileName(paramTLObject, null);
  }
  
  public static String getAttachFileName(TLObject paramTLObject, String paramString)
  {
    int i = -1;
    if ((paramTLObject instanceof TLRPC.Document))
    {
      TLRPC.Document localDocument = (TLRPC.Document)paramTLObject;
      paramString = null;
      int j;
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
            break label228;
          }
          paramTLObject = localDocument.mime_type;
          switch (paramTLObject.hashCode())
          {
          default: 
            switch (i)
            {
            default: 
              label100:
              paramTLObject = "";
            }
            break;
          }
        }
      }
      for (;;)
      {
        if (localDocument.version == 0)
        {
          if (paramTLObject.length() > 1)
          {
            return localDocument.dc_id + "_" + localDocument.id + paramTLObject;
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
            continue;
            paramTLObject = ".ogg";
            continue;
            label228:
            paramTLObject = "";
            continue;
          }
          return localDocument.dc_id + "_" + localDocument.id;
        }
      }
      if (paramTLObject.length() > 1) {
        return localDocument.dc_id + "_" + localDocument.id + "_" + localDocument.version + paramTLObject;
      }
      return localDocument.dc_id + "_" + localDocument.id + "_" + localDocument.version;
    }
    if ((paramTLObject instanceof TLRPC.PhotoSize))
    {
      paramTLObject = (TLRPC.PhotoSize)paramTLObject;
      if ((paramTLObject.location == null) || ((paramTLObject.location instanceof TLRPC.TL_fileLocationUnavailable))) {
        return "";
      }
      paramTLObject = new StringBuilder().append(paramTLObject.location.volume_id).append("_").append(paramTLObject.location.local_id).append(".");
      if (paramString != null) {}
      for (;;)
      {
        return paramString;
        paramString = "jpg";
      }
    }
    if ((paramTLObject instanceof TLRPC.FileLocation))
    {
      if ((paramTLObject instanceof TLRPC.TL_fileLocationUnavailable)) {
        return "";
      }
      paramTLObject = (TLRPC.FileLocation)paramTLObject;
      paramTLObject = new StringBuilder().append(paramTLObject.volume_id).append("_").append(paramTLObject.local_id).append(".");
      if (paramString != null) {}
      for (;;)
      {
        return paramString;
        paramString = "jpg";
      }
    }
    return "";
  }
  
  public static TLRPC.PhotoSize getClosestPhotoSizeWithSize(ArrayList<TLRPC.PhotoSize> paramArrayList, int paramInt)
  {
    return getClosestPhotoSizeWithSize(paramArrayList, paramInt, false);
  }
  
  public static TLRPC.PhotoSize getClosestPhotoSizeWithSize(ArrayList<TLRPC.PhotoSize> paramArrayList, int paramInt, boolean paramBoolean)
  {
    Object localObject2;
    if ((paramArrayList == null) || (paramArrayList.isEmpty()))
    {
      localObject2 = null;
      return (TLRPC.PhotoSize)localObject2;
    }
    int m = 0;
    Object localObject1 = null;
    int k = 0;
    TLRPC.PhotoSize localPhotoSize;
    int i;
    for (;;)
    {
      localObject2 = localObject1;
      if (k >= paramArrayList.size()) {
        break;
      }
      localPhotoSize = (TLRPC.PhotoSize)paramArrayList.get(k);
      if (localPhotoSize != null) {
        break label78;
      }
      i = m;
      localObject2 = localObject1;
      k += 1;
      localObject1 = localObject2;
      m = i;
    }
    label78:
    if (paramBoolean)
    {
      if (localPhotoSize.h >= localPhotoSize.w) {}
      for (j = localPhotoSize.w;; j = localPhotoSize.h)
      {
        if ((localObject1 != null) && ((paramInt <= 100) || (((TLRPC.PhotoSize)localObject1).location == null) || (((TLRPC.PhotoSize)localObject1).location.dc_id != Integer.MIN_VALUE)) && (!(localPhotoSize instanceof TLRPC.TL_photoCachedSize)))
        {
          localObject2 = localObject1;
          i = m;
          if (paramInt <= m) {
            break;
          }
          localObject2 = localObject1;
          i = m;
          if (m >= j) {
            break;
          }
        }
        localObject2 = localPhotoSize;
        i = j;
        break;
      }
    }
    if (localPhotoSize.w >= localPhotoSize.h) {}
    for (int j = localPhotoSize.w;; j = localPhotoSize.h)
    {
      if ((localObject1 != null) && ((paramInt <= 100) || (((TLRPC.PhotoSize)localObject1).location == null) || (((TLRPC.PhotoSize)localObject1).location.dc_id != Integer.MIN_VALUE)) && (!(localPhotoSize instanceof TLRPC.TL_photoCachedSize)))
      {
        localObject2 = localObject1;
        i = m;
        if (j > paramInt) {
          break;
        }
        localObject2 = localObject1;
        i = m;
        if (m >= j) {
          break;
        }
      }
      localObject2 = localPhotoSize;
      i = j;
      break;
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
    if (paramDocument != null)
    {
      if (paramDocument.file_name != null) {
        return paramDocument.file_name;
      }
      int i = 0;
      while (i < paramDocument.attributes.size())
      {
        TLRPC.DocumentAttribute localDocumentAttribute = (TLRPC.DocumentAttribute)paramDocument.attributes.get(i);
        if ((localDocumentAttribute instanceof TLRPC.TL_documentAttributeFilename)) {
          return localDocumentAttribute.file_name;
        }
        i += 1;
      }
    }
    return "";
  }
  
  public static String getFileExtension(File paramFile)
  {
    paramFile = paramFile.getName();
    try
    {
      paramFile = paramFile.substring(paramFile.lastIndexOf('.') + 1);
      return paramFile;
    }
    catch (Exception paramFile) {}
    return "";
  }
  
  public static FileLoader getInstance()
  {
    Object localObject1 = Instance;
    if (localObject1 == null)
    {
      for (;;)
      {
        try
        {
          FileLoader localFileLoader2 = Instance;
          localObject1 = localFileLoader2;
          if (localFileLoader2 == null) {
            localObject1 = new FileLoader();
          }
        }
        finally
        {
          continue;
        }
        try
        {
          Instance = (FileLoader)localObject1;
          return (FileLoader)localObject1;
        }
        finally {}
      }
      throw ((Throwable)localObject1);
    }
    return localFileLoader1;
  }
  
  public static String getMessageFileName(TLRPC.Message paramMessage)
  {
    if (paramMessage == null) {
      return "";
    }
    if ((paramMessage instanceof TLRPC.TL_messageService))
    {
      if (paramMessage.action.photo != null)
      {
        paramMessage = paramMessage.action.photo.sizes;
        if (paramMessage.size() > 0)
        {
          paramMessage = getClosestPhotoSizeWithSize(paramMessage, AndroidUtilities.getPhotoSize());
          if (paramMessage != null) {
            return getAttachFileName(paramMessage);
          }
        }
      }
    }
    else
    {
      if ((paramMessage.media instanceof TLRPC.TL_messageMediaDocument)) {
        return getAttachFileName(paramMessage.media.document);
      }
      if ((paramMessage.media instanceof TLRPC.TL_messageMediaPhoto))
      {
        paramMessage = paramMessage.media.photo.sizes;
        if (paramMessage.size() > 0)
        {
          paramMessage = getClosestPhotoSizeWithSize(paramMessage, AndroidUtilities.getPhotoSize());
          if (paramMessage != null) {
            return getAttachFileName(paramMessage);
          }
        }
      }
      else if ((paramMessage.media instanceof TLRPC.TL_messageMediaWebPage))
      {
        if (paramMessage.media.webpage.photo != null)
        {
          paramMessage = paramMessage.media.webpage.photo.sizes;
          if (paramMessage.size() > 0)
          {
            paramMessage = getClosestPhotoSizeWithSize(paramMessage, AndroidUtilities.getPhotoSize());
            if (paramMessage != null) {
              return getAttachFileName(paramMessage);
            }
          }
        }
        else if (paramMessage.media.webpage.document != null)
        {
          return getAttachFileName(paramMessage.media.webpage.document);
        }
      }
    }
    return "";
  }
  
  public static File getPathToAttach(TLObject paramTLObject)
  {
    return getPathToAttach(paramTLObject, null, false);
  }
  
  public static File getPathToAttach(TLObject paramTLObject, String paramString, boolean paramBoolean)
  {
    Object localObject = null;
    if (paramBoolean) {
      localObject = getInstance().getDirectory(4);
    }
    while (localObject == null)
    {
      return new File("");
      if ((paramTLObject instanceof TLRPC.Document))
      {
        localObject = (TLRPC.Document)paramTLObject;
        if (((TLRPC.Document)localObject).key != null) {
          localObject = getInstance().getDirectory(4);
        } else if (MessageObject.isVoiceDocument((TLRPC.Document)localObject)) {
          localObject = getInstance().getDirectory(1);
        } else if (MessageObject.isVideoDocument((TLRPC.Document)localObject)) {
          localObject = getInstance().getDirectory(2);
        } else {
          localObject = getInstance().getDirectory(3);
        }
      }
      else if ((paramTLObject instanceof TLRPC.PhotoSize))
      {
        localObject = (TLRPC.PhotoSize)paramTLObject;
        if ((((TLRPC.PhotoSize)localObject).location == null) || (((TLRPC.PhotoSize)localObject).location.key != null) || ((((TLRPC.PhotoSize)localObject).location.volume_id == -2147483648L) && (((TLRPC.PhotoSize)localObject).location.local_id < 0)) || (((TLRPC.PhotoSize)localObject).size < 0)) {
          localObject = getInstance().getDirectory(4);
        } else {
          localObject = getInstance().getDirectory(0);
        }
      }
      else if ((paramTLObject instanceof TLRPC.FileLocation))
      {
        localObject = (TLRPC.FileLocation)paramTLObject;
        if ((((TLRPC.FileLocation)localObject).key != null) || ((((TLRPC.FileLocation)localObject).volume_id == -2147483648L) && (((TLRPC.FileLocation)localObject).local_id < 0))) {
          localObject = getInstance().getDirectory(4);
        } else {
          localObject = getInstance().getDirectory(0);
        }
      }
    }
    return new File((File)localObject, getAttachFileName(paramTLObject, paramString));
  }
  
  public static File getPathToAttach(TLObject paramTLObject, boolean paramBoolean)
  {
    return getPathToAttach(paramTLObject, null, paramBoolean);
  }
  
  public static File getPathToMessage(TLRPC.Message paramMessage)
  {
    if (paramMessage == null) {
      return new File("");
    }
    if ((paramMessage instanceof TLRPC.TL_messageService))
    {
      if (paramMessage.action.photo != null)
      {
        paramMessage = paramMessage.action.photo.sizes;
        if (paramMessage.size() > 0)
        {
          paramMessage = getClosestPhotoSizeWithSize(paramMessage, AndroidUtilities.getPhotoSize());
          if (paramMessage != null) {
            return getPathToAttach(paramMessage);
          }
        }
      }
    }
    else
    {
      if ((paramMessage.media instanceof TLRPC.TL_messageMediaDocument)) {
        return getPathToAttach(paramMessage.media.document);
      }
      if ((paramMessage.media instanceof TLRPC.TL_messageMediaPhoto))
      {
        paramMessage = paramMessage.media.photo.sizes;
        if (paramMessage.size() > 0)
        {
          paramMessage = getClosestPhotoSizeWithSize(paramMessage, AndroidUtilities.getPhotoSize());
          if (paramMessage != null) {
            return getPathToAttach(paramMessage);
          }
        }
      }
      else if ((paramMessage.media instanceof TLRPC.TL_messageMediaWebPage))
      {
        if (paramMessage.media.webpage.document != null) {
          return getPathToAttach(paramMessage.media.webpage.document);
        }
        if (paramMessage.media.webpage.photo != null)
        {
          paramMessage = paramMessage.media.webpage.photo.sizes;
          if (paramMessage.size() > 0)
          {
            paramMessage = getClosestPhotoSizeWithSize(paramMessage, AndroidUtilities.getPhotoSize());
            if (paramMessage != null) {
              return getPathToAttach(paramMessage);
            }
          }
        }
      }
    }
    return new File("");
  }
  
  private void loadFile(final TLRPC.Document paramDocument, final TLRPC.FileLocation paramFileLocation, final String paramString, final int paramInt, final boolean paramBoolean1, final boolean paramBoolean2)
  {
    this.fileLoaderQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        final Object localObject = null;
        if (paramFileLocation != null) {}
        label21:
        label34:
        FileLoadOperation localFileLoadOperation;
        label161:
        label162:
        label163:
        label209:
        label361:
        label471:
        do
        {
          int j;
          do
          {
            localObject = FileLoader.getAttachFileName(paramFileLocation, paramString);
            break label34;
            break label34;
            break label34;
            for (;;)
            {
              if ((localObject == null) || (((String)localObject).contains("-2147483648"))) {}
              for (;;)
              {
                return;
                if (paramDocument == null) {
                  break label162;
                }
                localObject = FileLoader.getAttachFileName(paramDocument);
                break label21;
                localFileLoadOperation = (FileLoadOperation)FileLoader.this.loadOperationPaths.get(localObject);
                if (localFileLoadOperation == null) {
                  break label163;
                }
                if (!paramBoolean1) {
                  break;
                }
                localFileLoadOperation.setForceRequest(true);
                if (MessageObject.isVoiceDocument(paramDocument)) {
                  localObject = FileLoader.this.audioLoadOperationQueue;
                }
                for (;;)
                {
                  if (localObject == null) {
                    break label161;
                  }
                  i = ((LinkedList)localObject).indexOf(localFileLoadOperation);
                  if (i <= 0) {
                    break;
                  }
                  ((LinkedList)localObject).remove(i);
                  ((LinkedList)localObject).add(0, localFileLoadOperation);
                  return;
                  if (paramFileLocation != null) {
                    localObject = FileLoader.this.photoLoadOperationQueue;
                  } else {
                    localObject = FileLoader.this.loadOperationQueue;
                  }
                }
              }
            }
            File localFile2 = FileLoader.this.getDirectory(4);
            File localFile1 = localFile2;
            final int i = 4;
            if (paramFileLocation != null)
            {
              localFileLoadOperation = new FileLoadOperation(paramFileLocation, paramString, paramInt);
              i = 0;
              if (!paramBoolean2) {
                localFile1 = FileLoader.this.getDirectory(i);
              }
              localFileLoadOperation.setPaths(localFile1, localFile2);
              localFileLoadOperation.setDelegate(new FileLoadOperation.FileLoadOperationDelegate()
              {
                public void didChangedLoadProgress(FileLoadOperation paramAnonymous2FileLoadOperation, float paramAnonymous2Float)
                {
                  if (FileLoader.this.delegate != null) {
                    FileLoader.this.delegate.fileLoadProgressChanged(localObject, paramAnonymous2Float);
                  }
                }
                
                public void didFailedLoadingFile(FileLoadOperation paramAnonymous2FileLoadOperation, int paramAnonymous2Int)
                {
                  FileLoader.this.checkDownloadQueue(FileLoader.6.this.val$document, FileLoader.6.this.val$location, localObject);
                  if (FileLoader.this.delegate != null) {
                    FileLoader.this.delegate.fileDidFailedLoad(localObject, paramAnonymous2Int);
                  }
                }
                
                public void didFinishLoadingFile(FileLoadOperation paramAnonymous2FileLoadOperation, File paramAnonymous2File)
                {
                  if (FileLoader.this.delegate != null) {
                    FileLoader.this.delegate.fileDidLoaded(localObject, paramAnonymous2File, i);
                  }
                  FileLoader.this.checkDownloadQueue(FileLoader.6.this.val$document, FileLoader.6.this.val$location, localObject);
                }
              });
              FileLoader.this.loadOperationPaths.put(localObject, localFileLoadOperation);
              if (!paramBoolean1) {
                break label361;
              }
              j = 3;
            }
            for (;;)
            {
              if (i == 1)
              {
                if (FileLoader.this.currentAudioLoadOperationsCount < j)
                {
                  if (!localFileLoadOperation.start()) {
                    break;
                  }
                  FileLoader.access$1108(FileLoader.this);
                  return;
                  if (paramDocument == null) {
                    break label209;
                  }
                  localFileLoadOperation = new FileLoadOperation(paramDocument);
                  if (MessageObject.isVoiceDocument(paramDocument))
                  {
                    i = 1;
                    break label209;
                  }
                  if (MessageObject.isVideoDocument(paramDocument))
                  {
                    i = 2;
                    break label209;
                  }
                  i = 3;
                  break label209;
                  j = 1;
                  continue;
                }
                if (paramBoolean1)
                {
                  FileLoader.this.audioLoadOperationQueue.add(0, localFileLoadOperation);
                  return;
                }
                FileLoader.this.audioLoadOperationQueue.add(localFileLoadOperation);
                return;
              }
            }
            if (paramFileLocation == null) {
              break label471;
            }
            if (FileLoader.this.currentPhotoLoadOperationsCount >= j) {
              break;
            }
          } while (!localFileLoadOperation.start());
          FileLoader.access$1308(FileLoader.this);
          return;
          if (paramBoolean1)
          {
            FileLoader.this.photoLoadOperationQueue.add(0, localFileLoadOperation);
            return;
          }
          FileLoader.this.photoLoadOperationQueue.add(localFileLoadOperation);
          return;
          if (FileLoader.this.currentLoadOperationsCount >= j) {
            break;
          }
        } while (!localFileLoadOperation.start());
        FileLoader.access$1508(FileLoader.this);
        return;
        if (paramBoolean1)
        {
          FileLoader.this.loadOperationQueue.add(0, localFileLoadOperation);
          return;
        }
        FileLoader.this.loadOperationQueue.add(localFileLoadOperation);
      }
    });
  }
  
  public void cancelLoadFile(TLRPC.Document paramDocument)
  {
    cancelLoadFile(paramDocument, null, null);
  }
  
  public void cancelLoadFile(TLRPC.FileLocation paramFileLocation, String paramString)
  {
    cancelLoadFile(null, paramFileLocation, paramString);
  }
  
  public void cancelLoadFile(TLRPC.PhotoSize paramPhotoSize)
  {
    cancelLoadFile(null, paramPhotoSize.location, null);
  }
  
  public void cancelUploadFile(final String paramString, final boolean paramBoolean)
  {
    this.fileLoaderQueue.postRunnable(new Runnable()
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
  
  public File checkDirectory(int paramInt)
  {
    return (File)this.mediaDirs.get(Integer.valueOf(paramInt));
  }
  
  public void checkUploadNewDataAvailable(final String paramString, final boolean paramBoolean, final long paramLong)
  {
    this.fileLoaderQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        FileUploadOperation localFileUploadOperation;
        if (paramBoolean)
        {
          localFileUploadOperation = (FileUploadOperation)FileLoader.this.uploadOperationPathsEnc.get(paramString);
          if (localFileUploadOperation == null) {
            break label59;
          }
          localFileUploadOperation.checkNewDataAvailable(paramLong);
        }
        label59:
        while (paramLong == 0L)
        {
          return;
          localFileUploadOperation = (FileUploadOperation)FileLoader.this.uploadOperationPaths.get(paramString);
          break;
        }
        FileLoader.this.uploadSizes.put(paramString, Long.valueOf(paramLong));
      }
    });
  }
  
  public void deleteFiles(final ArrayList<File> paramArrayList, final int paramInt)
  {
    if ((paramArrayList == null) || (paramArrayList.isEmpty())) {
      return;
    }
    this.fileLoaderQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        int i = 0;
        for (;;)
        {
          if (i < paramArrayList.size())
          {
            File localFile = (File)paramArrayList.get(i);
            if (localFile.exists()) {}
            try
            {
              if (!localFile.delete()) {
                localFile.deleteOnExit();
              }
            }
            catch (Exception localException2)
            {
              try
              {
                for (;;)
                {
                  localFile = new File(localFile.getParentFile(), "q_" + localFile.getName());
                  if ((localFile.exists()) && (!localFile.delete())) {
                    localFile.deleteOnExit();
                  }
                  i += 1;
                  break;
                  localException2 = localException2;
                  FileLog.e("tmessages", localException2);
                }
              }
              catch (Exception localException1)
              {
                for (;;)
                {
                  FileLog.e("tmessages", localException1);
                }
              }
            }
          }
        }
        if (paramInt == 2) {
          ImageLoader.getInstance().clearMemory();
        }
      }
    });
  }
  
  public File getDirectory(int paramInt)
  {
    File localFile2 = (File)this.mediaDirs.get(Integer.valueOf(paramInt));
    File localFile1 = localFile2;
    if (localFile2 == null)
    {
      localFile1 = localFile2;
      if (paramInt != 4) {
        localFile1 = (File)this.mediaDirs.get(Integer.valueOf(4));
      }
    }
    try
    {
      if (!localFile1.isDirectory()) {
        localFile1.mkdirs();
      }
      return localFile1;
    }
    catch (Exception localException) {}
    return localFile1;
  }
  
  public boolean isLoadingFile(final String paramString)
  {
    final Semaphore localSemaphore = new Semaphore(0);
    final Boolean[] arrayOfBoolean = new Boolean[1];
    this.fileLoaderQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        arrayOfBoolean[0] = Boolean.valueOf(FileLoader.this.loadOperationPaths.containsKey(paramString));
        localSemaphore.release();
      }
    });
    try
    {
      localSemaphore.acquire();
      return arrayOfBoolean[0].booleanValue();
    }
    catch (Exception paramString)
    {
      for (;;)
      {
        FileLog.e("tmessages", paramString);
      }
    }
  }
  
  public void loadFile(TLRPC.Document paramDocument, boolean paramBoolean1, boolean paramBoolean2)
  {
    if ((paramBoolean2) || ((paramDocument != null) && (paramDocument.key != null))) {}
    for (paramBoolean2 = true;; paramBoolean2 = false)
    {
      loadFile(paramDocument, null, null, 0, paramBoolean1, paramBoolean2);
      return;
    }
  }
  
  public void loadFile(TLRPC.FileLocation paramFileLocation, String paramString, int paramInt, boolean paramBoolean)
  {
    if ((paramBoolean) || (paramInt == 0) || ((paramFileLocation != null) && (paramFileLocation.key != null))) {}
    for (paramBoolean = true;; paramBoolean = false)
    {
      loadFile(null, paramFileLocation, paramString, paramInt, true, paramBoolean);
      return;
    }
  }
  
  public void loadFile(TLRPC.PhotoSize paramPhotoSize, String paramString, boolean paramBoolean)
  {
    TLRPC.FileLocation localFileLocation = paramPhotoSize.location;
    int i = paramPhotoSize.size;
    if ((paramBoolean) || ((paramPhotoSize != null) && (paramPhotoSize.size == 0)) || (paramPhotoSize.location.key != null)) {}
    for (paramBoolean = true;; paramBoolean = false)
    {
      loadFile(null, localFileLocation, paramString, i, false, paramBoolean);
      return;
    }
  }
  
  public void setDelegate(FileLoaderDelegate paramFileLoaderDelegate)
  {
    this.delegate = paramFileLoaderDelegate;
  }
  
  public void setMediaDirs(HashMap<Integer, File> paramHashMap)
  {
    this.mediaDirs = paramHashMap;
  }
  
  public void uploadFile(String paramString, boolean paramBoolean1, boolean paramBoolean2)
  {
    uploadFile(paramString, paramBoolean1, paramBoolean2, 0);
  }
  
  public void uploadFile(final String paramString, final boolean paramBoolean1, final boolean paramBoolean2, final int paramInt)
  {
    if (paramString == null) {
      return;
    }
    this.fileLoaderQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        if (paramBoolean1)
        {
          if (!FileLoader.this.uploadOperationPathsEnc.containsKey(paramString)) {}
        }
        else {
          while (FileLoader.this.uploadOperationPaths.containsKey(paramString)) {
            return;
          }
        }
        int j = paramInt;
        int i = j;
        if (j != 0)
        {
          i = j;
          if ((Long)FileLoader.this.uploadSizes.get(paramString) != null)
          {
            i = 0;
            FileLoader.this.uploadSizes.remove(paramString);
          }
        }
        FileUploadOperation localFileUploadOperation = new FileUploadOperation(paramString, paramBoolean1, i);
        if (paramBoolean1) {
          FileLoader.this.uploadOperationPathsEnc.put(paramString, localFileUploadOperation);
        }
        for (;;)
        {
          localFileUploadOperation.delegate = new FileUploadOperation.FileUploadOperationDelegate()
          {
            public void didChangedUploadProgress(FileUploadOperation paramAnonymous2FileUploadOperation, float paramAnonymous2Float)
            {
              if (FileLoader.this.delegate != null) {
                FileLoader.this.delegate.fileUploadProgressChanged(FileLoader.3.this.val$location, paramAnonymous2Float, FileLoader.3.this.val$encrypted);
              }
            }
            
            public void didFailedUploadingFile(FileUploadOperation paramAnonymous2FileUploadOperation)
            {
              FileLoader.this.fileLoaderQueue.postRunnable(new Runnable()
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
                    FileLoader.access$510(FileLoader.this);
                    if (FileLoader.this.currentUploadSmallOperationsCount < 1)
                    {
                      localFileUploadOperation = (FileUploadOperation)FileLoader.this.uploadSmallOperationQueue.poll();
                      if (localFileUploadOperation != null)
                      {
                        FileLoader.access$508(FileLoader.this);
                        localFileUploadOperation.start();
                      }
                    }
                  }
                  label211:
                  do
                  {
                    do
                    {
                      return;
                      FileLoader.this.uploadOperationPaths.remove(FileLoader.3.this.val$location);
                      break;
                      FileLoader.access$610(FileLoader.this);
                    } while (FileLoader.this.currentUploadOperationsCount >= 1);
                    localFileUploadOperation = (FileUploadOperation)FileLoader.this.uploadOperationQueue.poll();
                  } while (localFileUploadOperation == null);
                  FileLoader.access$608(FileLoader.this);
                  localFileUploadOperation.start();
                }
              });
            }
            
            public void didFinishUploadingFile(final FileUploadOperation paramAnonymous2FileUploadOperation, final TLRPC.InputFile paramAnonymous2InputFile, final TLRPC.InputEncryptedFile paramAnonymous2InputEncryptedFile, final byte[] paramAnonymous2ArrayOfByte1, final byte[] paramAnonymous2ArrayOfByte2)
            {
              FileLoader.this.fileLoaderQueue.postRunnable(new Runnable()
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
                    FileLoader.access$510(FileLoader.this);
                    if (FileLoader.this.currentUploadSmallOperationsCount < 1)
                    {
                      localFileUploadOperation = (FileUploadOperation)FileLoader.this.uploadSmallOperationQueue.poll();
                      if (localFileUploadOperation != null)
                      {
                        FileLoader.access$508(FileLoader.this);
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
                    FileLoader.access$610(FileLoader.this);
                    if (FileLoader.this.currentUploadOperationsCount < 1)
                    {
                      localFileUploadOperation = (FileUploadOperation)FileLoader.this.uploadOperationQueue.poll();
                      if (localFileUploadOperation != null)
                      {
                        FileLoader.access$608(FileLoader.this);
                        localFileUploadOperation.start();
                      }
                    }
                  }
                }
              });
            }
          };
          if (!paramBoolean2) {
            break label207;
          }
          if (FileLoader.this.currentUploadSmallOperationsCount >= 1) {
            break;
          }
          FileLoader.access$508(FileLoader.this);
          localFileUploadOperation.start();
          return;
          FileLoader.this.uploadOperationPaths.put(paramString, localFileUploadOperation);
        }
        FileLoader.this.uploadSmallOperationQueue.add(localFileUploadOperation);
        return;
        label207:
        if (FileLoader.this.currentUploadOperationsCount < 1)
        {
          FileLoader.access$608(FileLoader.this);
          localFileUploadOperation.start();
          return;
        }
        FileLoader.this.uploadOperationQueue.add(localFileUploadOperation);
      }
    });
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