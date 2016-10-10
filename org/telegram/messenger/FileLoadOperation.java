package org.telegram.messenger;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Scanner;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputFileLocation;
import org.telegram.tgnet.TLRPC.TL_document;
import org.telegram.tgnet.TLRPC.TL_documentEncrypted;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_fileEncryptedLocation;
import org.telegram.tgnet.TLRPC.TL_fileLocation;
import org.telegram.tgnet.TLRPC.TL_inputDocumentFileLocation;
import org.telegram.tgnet.TLRPC.TL_inputEncryptedFileLocation;
import org.telegram.tgnet.TLRPC.TL_inputFileLocation;
import org.telegram.tgnet.TLRPC.TL_upload_file;
import org.telegram.tgnet.TLRPC.TL_upload_getFile;

public class FileLoadOperation
{
  private static final int bigFileSizeFrom = 1048576;
  private static final int downloadChunkSize = 32768;
  private static final int downloadChunkSizeBig = 131072;
  private static final int maxDownloadRequests = 4;
  private static final int maxDownloadRequestsBig = 2;
  private static final int stateDownloading = 1;
  private static final int stateFailed = 2;
  private static final int stateFinished = 3;
  private static final int stateIdle = 0;
  private int bytesCountPadding;
  private File cacheFileFinal;
  private File cacheFileTemp;
  private File cacheIvTemp;
  private int currentDownloadChunkSize;
  private int currentMaxDownloadRequests;
  private int datacenter_id;
  private ArrayList<RequestInfo> delayedRequestInfos;
  private FileLoadOperationDelegate delegate;
  private int downloadedBytes;
  private String ext;
  private RandomAccessFile fileOutputStream;
  private RandomAccessFile fiv;
  private boolean isForceRequest;
  private byte[] iv;
  private byte[] key;
  private TLRPC.InputFileLocation location;
  private int nextDownloadOffset;
  private int renameRetryCount;
  private ArrayList<RequestInfo> requestInfos;
  private int requestsCount;
  private boolean started;
  private volatile int state = 0;
  private File storePath;
  private File tempPath;
  private int totalBytesCount;
  
  public FileLoadOperation(TLRPC.Document paramDocument)
  {
    for (;;)
    {
      try
      {
        int j;
        if ((paramDocument instanceof TLRPC.TL_documentEncrypted))
        {
          this.location = new TLRPC.TL_inputEncryptedFileLocation();
          this.location.id = paramDocument.id;
          this.location.access_hash = paramDocument.access_hash;
          this.datacenter_id = paramDocument.dc_id;
          this.iv = new byte[32];
          System.arraycopy(paramDocument.iv, 0, this.iv, 0, this.iv.length);
          this.key = paramDocument.key;
          this.totalBytesCount = paramDocument.size;
          if ((this.key != null) && (this.totalBytesCount % 16 != 0))
          {
            this.bytesCountPadding = (16 - this.totalBytesCount % 16);
            this.totalBytesCount += this.bytesCountPadding;
          }
          this.ext = FileLoader.getDocumentFileName(paramDocument);
          if (this.ext != null)
          {
            j = this.ext.lastIndexOf('.');
            if (j != -1) {
              break label308;
            }
          }
          this.ext = "";
          if (this.ext.length() > 1) {
            break;
          }
          if (paramDocument.mime_type == null) {
            break label365;
          }
          paramDocument = paramDocument.mime_type;
        }
        switch (paramDocument.hashCode())
        {
        case 1331848029: 
          this.ext = "";
          return;
          if (!(paramDocument instanceof TLRPC.TL_document)) {
            continue;
          }
          this.location = new TLRPC.TL_inputDocumentFileLocation();
          this.location.id = paramDocument.id;
          this.location.access_hash = paramDocument.access_hash;
          this.datacenter_id = paramDocument.dc_id;
          continue;
          this.ext = this.ext.substring(j);
        }
      }
      catch (Exception paramDocument)
      {
        FileLog.e("tmessages", paramDocument);
        onFail(true, 0);
        return;
      }
      label308:
      continue;
      if (paramDocument.equals("video/mp4"))
      {
        i = 0;
        break label372;
        if (paramDocument.equals("audio/ogg"))
        {
          i = 1;
          break label372;
          this.ext = ".mp4";
          return;
          this.ext = ".ogg";
          return;
          label365:
          this.ext = "";
          return;
        }
      }
      label372:
      switch (i)
      {
      }
    }
  }
  
  public FileLoadOperation(TLRPC.FileLocation paramFileLocation, String paramString, int paramInt)
  {
    if ((paramFileLocation instanceof TLRPC.TL_fileEncryptedLocation))
    {
      this.location = new TLRPC.TL_inputEncryptedFileLocation();
      this.location.id = paramFileLocation.volume_id;
      this.location.volume_id = paramFileLocation.volume_id;
      this.location.access_hash = paramFileLocation.secret;
      this.location.local_id = paramFileLocation.local_id;
      this.iv = new byte[32];
      System.arraycopy(paramFileLocation.iv, 0, this.iv, 0, this.iv.length);
      this.key = paramFileLocation.key;
      this.datacenter_id = paramFileLocation.dc_id;
      this.totalBytesCount = paramInt;
      if (paramString == null) {
        break label190;
      }
    }
    for (;;)
    {
      this.ext = paramString;
      return;
      if (!(paramFileLocation instanceof TLRPC.TL_fileLocation)) {
        break;
      }
      this.location = new TLRPC.TL_inputFileLocation();
      this.location.volume_id = paramFileLocation.volume_id;
      this.location.secret = paramFileLocation.secret;
      this.location.local_id = paramFileLocation.local_id;
      this.datacenter_id = paramFileLocation.dc_id;
      break;
      label190:
      paramString = "jpg";
    }
  }
  
  /* Error */
  private void cleanup()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 248	org/telegram/messenger/FileLoadOperation:fileOutputStream	Ljava/io/RandomAccessFile;
    //   4: astore_2
    //   5: aload_2
    //   6: ifnull +25 -> 31
    //   9: aload_0
    //   10: getfield 248	org/telegram/messenger/FileLoadOperation:fileOutputStream	Ljava/io/RandomAccessFile;
    //   13: invokevirtual 254	java/io/RandomAccessFile:getChannel	()Ljava/nio/channels/FileChannel;
    //   16: invokevirtual 259	java/nio/channels/FileChannel:close	()V
    //   19: aload_0
    //   20: getfield 248	org/telegram/messenger/FileLoadOperation:fileOutputStream	Ljava/io/RandomAccessFile;
    //   23: invokevirtual 260	java/io/RandomAccessFile:close	()V
    //   26: aload_0
    //   27: aconst_null
    //   28: putfield 248	org/telegram/messenger/FileLoadOperation:fileOutputStream	Ljava/io/RandomAccessFile;
    //   31: aload_0
    //   32: getfield 262	org/telegram/messenger/FileLoadOperation:fiv	Ljava/io/RandomAccessFile;
    //   35: ifnull +15 -> 50
    //   38: aload_0
    //   39: getfield 262	org/telegram/messenger/FileLoadOperation:fiv	Ljava/io/RandomAccessFile;
    //   42: invokevirtual 260	java/io/RandomAccessFile:close	()V
    //   45: aload_0
    //   46: aconst_null
    //   47: putfield 262	org/telegram/messenger/FileLoadOperation:fiv	Ljava/io/RandomAccessFile;
    //   50: aload_0
    //   51: getfield 264	org/telegram/messenger/FileLoadOperation:delayedRequestInfos	Ljava/util/ArrayList;
    //   54: ifnull +94 -> 148
    //   57: iconst_0
    //   58: istore_1
    //   59: iload_1
    //   60: aload_0
    //   61: getfield 264	org/telegram/messenger/FileLoadOperation:delayedRequestInfos	Ljava/util/ArrayList;
    //   64: invokevirtual 268	java/util/ArrayList:size	()I
    //   67: if_icmpge +74 -> 141
    //   70: aload_0
    //   71: getfield 264	org/telegram/messenger/FileLoadOperation:delayedRequestInfos	Ljava/util/ArrayList;
    //   74: iload_1
    //   75: invokevirtual 272	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   78: checkcast 19	org/telegram/messenger/FileLoadOperation$RequestInfo
    //   81: astore_2
    //   82: aload_2
    //   83: invokestatic 276	org/telegram/messenger/FileLoadOperation$RequestInfo:access$800	(Lorg/telegram/messenger/FileLoadOperation$RequestInfo;)Lorg/telegram/tgnet/TLRPC$TL_upload_file;
    //   86: ifnull +18 -> 104
    //   89: aload_2
    //   90: invokestatic 276	org/telegram/messenger/FileLoadOperation$RequestInfo:access$800	(Lorg/telegram/messenger/FileLoadOperation$RequestInfo;)Lorg/telegram/tgnet/TLRPC$TL_upload_file;
    //   93: iconst_0
    //   94: putfield 281	org/telegram/tgnet/TLRPC$TL_upload_file:disableFree	Z
    //   97: aload_2
    //   98: invokestatic 276	org/telegram/messenger/FileLoadOperation$RequestInfo:access$800	(Lorg/telegram/messenger/FileLoadOperation$RequestInfo;)Lorg/telegram/tgnet/TLRPC$TL_upload_file;
    //   101: invokevirtual 284	org/telegram/tgnet/TLRPC$TL_upload_file:freeResources	()V
    //   104: iload_1
    //   105: iconst_1
    //   106: iadd
    //   107: istore_1
    //   108: goto -49 -> 59
    //   111: astore_2
    //   112: ldc -97
    //   114: aload_2
    //   115: invokestatic 165	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   118: goto -99 -> 19
    //   121: astore_2
    //   122: ldc -97
    //   124: aload_2
    //   125: invokestatic 165	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   128: goto -97 -> 31
    //   131: astore_2
    //   132: ldc -97
    //   134: aload_2
    //   135: invokestatic 165	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   138: goto -88 -> 50
    //   141: aload_0
    //   142: getfield 264	org/telegram/messenger/FileLoadOperation:delayedRequestInfos	Ljava/util/ArrayList;
    //   145: invokevirtual 287	java/util/ArrayList:clear	()V
    //   148: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	149	0	this	FileLoadOperation
    //   58	50	1	i	int
    //   4	94	2	localObject	Object
    //   111	4	2	localException1	Exception
    //   121	4	2	localException2	Exception
    //   131	4	2	localException3	Exception
    // Exception table:
    //   from	to	target	type
    //   9	19	111	java/lang/Exception
    //   0	5	121	java/lang/Exception
    //   19	31	121	java/lang/Exception
    //   112	118	121	java/lang/Exception
    //   31	50	131	java/lang/Exception
  }
  
  private void onFail(boolean paramBoolean, final int paramInt)
  {
    cleanup();
    this.state = 2;
    if (paramBoolean)
    {
      Utilities.stageQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          FileLoadOperation.this.delegate.didFailedLoadingFile(FileLoadOperation.this, paramInt);
        }
      });
      return;
    }
    this.delegate.didFailedLoadingFile(this, paramInt);
  }
  
  private void onFinishLoadingFile()
    throws Exception
  {
    if (this.state != 1) {
      return;
    }
    this.state = 3;
    cleanup();
    if (this.cacheIvTemp != null)
    {
      this.cacheIvTemp.delete();
      this.cacheIvTemp = null;
    }
    if ((this.cacheFileTemp != null) && (!this.cacheFileTemp.renameTo(this.cacheFileFinal)))
    {
      if (BuildVars.DEBUG_VERSION) {
        FileLog.e("tmessages", "unable to rename temp = " + this.cacheFileTemp + " to final = " + this.cacheFileFinal + " retry = " + this.renameRetryCount);
      }
      this.renameRetryCount += 1;
      if (this.renameRetryCount < 3)
      {
        this.state = 1;
        Utilities.stageQueue.postRunnable(new Runnable()
        {
          public void run()
          {
            try
            {
              FileLoadOperation.this.onFinishLoadingFile();
              return;
            }
            catch (Exception localException)
            {
              FileLoadOperation.this.onFail(false, 0);
            }
          }
        }, 200L);
        return;
      }
      this.cacheFileFinal = this.cacheFileTemp;
    }
    if (BuildVars.DEBUG_VERSION) {
      FileLog.e("tmessages", "finished downloading file to " + this.cacheFileFinal);
    }
    this.delegate.didFinishLoadingFile(this, this.cacheFileFinal);
  }
  
  private void processRequestResult(RequestInfo paramRequestInfo, TLRPC.TL_error paramTL_error)
  {
    this.requestInfos.remove(paramRequestInfo);
    int i;
    if (paramTL_error == null)
    {
      try
      {
        if (this.downloadedBytes != paramRequestInfo.offset)
        {
          if (this.state != 1) {
            break label696;
          }
          this.delayedRequestInfos.add(paramRequestInfo);
          paramRequestInfo.response.disableFree = true;
          return;
        }
        if ((paramRequestInfo.response.bytes == null) || (paramRequestInfo.response.bytes.limit() == 0))
        {
          onFinishLoadingFile();
          return;
        }
      }
      catch (Exception paramRequestInfo)
      {
        onFail(false, 0);
        FileLog.e("tmessages", paramRequestInfo);
        return;
      }
      i = paramRequestInfo.response.bytes.limit();
      this.downloadedBytes += i;
      if (i != this.currentDownloadChunkSize) {
        break label697;
      }
      if ((this.totalBytesCount != this.downloadedBytes) && (this.downloadedBytes % this.currentDownloadChunkSize == 0)) {
        break label708;
      }
      if (this.totalBytesCount <= 0) {
        break label697;
      }
      if (this.totalBytesCount > this.downloadedBytes) {
        break label708;
      }
      break label697;
      if (this.key != null)
      {
        Utilities.aesIgeEncryption(paramRequestInfo.response.bytes.buffer, this.key, this.iv, false, true, 0, paramRequestInfo.response.bytes.limit());
        if ((i != 0) && (this.bytesCountPadding != 0)) {
          paramRequestInfo.response.bytes.limit(paramRequestInfo.response.bytes.limit() - this.bytesCountPadding);
        }
      }
      if (this.fileOutputStream != null) {
        this.fileOutputStream.getChannel().write(paramRequestInfo.response.bytes.buffer);
      }
      if (this.fiv != null)
      {
        this.fiv.seek(0L);
        this.fiv.write(this.iv);
      }
      if ((this.totalBytesCount <= 0) || (this.state != 1)) {
        break label702;
      }
      this.delegate.didChangedLoadProgress(this, Math.min(1.0F, this.downloadedBytes / this.totalBytesCount));
      break label702;
    }
    for (;;)
    {
      int j;
      if (j < this.delayedRequestInfos.size())
      {
        paramRequestInfo = (RequestInfo)this.delayedRequestInfos.get(j);
        if (this.downloadedBytes == paramRequestInfo.offset)
        {
          this.delayedRequestInfos.remove(j);
          processRequestResult(paramRequestInfo, null);
          paramRequestInfo.response.disableFree = false;
          paramRequestInfo.response.freeResources();
        }
      }
      else
      {
        if (i != 0)
        {
          onFinishLoadingFile();
          return;
        }
        startDownloadRequest();
        return;
        if (paramTL_error.text.contains("FILE_MIGRATE_"))
        {
          paramRequestInfo = new Scanner(paramTL_error.text.replace("FILE_MIGRATE_", ""));
          paramRequestInfo.useDelimiter("");
          try
          {
            i = paramRequestInfo.nextInt();
            paramRequestInfo = Integer.valueOf(i);
          }
          catch (Exception paramRequestInfo)
          {
            for (;;)
            {
              paramRequestInfo = null;
            }
            this.datacenter_id = paramRequestInfo.intValue();
            this.nextDownloadOffset = 0;
            startDownloadRequest();
            return;
          }
          if (paramRequestInfo == null)
          {
            onFail(false, 0);
            return;
          }
        }
        if (paramTL_error.text.contains("OFFSET_INVALID"))
        {
          if (this.downloadedBytes % this.currentDownloadChunkSize == 0) {
            try
            {
              onFinishLoadingFile();
              return;
            }
            catch (Exception paramRequestInfo)
            {
              FileLog.e("tmessages", paramRequestInfo);
              onFail(false, 0);
              return;
            }
          }
          onFail(false, 0);
          return;
        }
        if (paramTL_error.text.contains("RETRY_LIMIT"))
        {
          onFail(false, 2);
          return;
        }
        if (this.location != null) {
          FileLog.e("tmessages", "" + this.location + " id = " + this.location.id + " local_id = " + this.location.local_id + " access_hash = " + this.location.access_hash + " volume_id = " + this.location.volume_id + " secret = " + this.location.secret);
        }
        onFail(false, 0);
        label696:
        return;
        label697:
        i = 1;
        break;
        label702:
        j = 0;
        continue;
        label708:
        i = 0;
        break;
      }
      j += 1;
    }
  }
  
  private void startDownloadRequest()
  {
    if ((this.state != 1) || ((this.totalBytesCount > 0) && (this.nextDownloadOffset >= this.totalBytesCount)) || (this.requestInfos.size() + this.delayedRequestInfos.size() >= this.currentMaxDownloadRequests)) {
      return;
    }
    int i = 1;
    if (this.totalBytesCount > 0) {
      i = Math.max(0, this.currentMaxDownloadRequests - this.requestInfos.size());
    }
    int j = 0;
    label77:
    boolean bool;
    label140:
    TLRPC.TL_upload_getFile localTL_upload_getFile;
    final RequestInfo localRequestInfo;
    ConnectionsManager localConnectionsManager;
    RequestDelegate local5;
    int k;
    label247:
    int n;
    if ((j < i) && ((this.totalBytesCount <= 0) || (this.nextDownloadOffset < this.totalBytesCount)))
    {
      if ((this.totalBytesCount > 0) && (j != i - 1) && ((this.totalBytesCount <= 0) || (this.nextDownloadOffset + this.currentDownloadChunkSize < this.totalBytesCount))) {
        break label307;
      }
      bool = true;
      localTL_upload_getFile = new TLRPC.TL_upload_getFile();
      localTL_upload_getFile.location = this.location;
      localTL_upload_getFile.offset = this.nextDownloadOffset;
      localTL_upload_getFile.limit = this.currentDownloadChunkSize;
      this.nextDownloadOffset += this.currentDownloadChunkSize;
      localRequestInfo = new RequestInfo(null);
      this.requestInfos.add(localRequestInfo);
      RequestInfo.access$902(localRequestInfo, localTL_upload_getFile.offset);
      localConnectionsManager = ConnectionsManager.getInstance();
      local5 = new RequestDelegate()
      {
        public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
        {
          FileLoadOperation.RequestInfo.access$802(localRequestInfo, (TLRPC.TL_upload_file)paramAnonymousTLObject);
          FileLoadOperation.this.processRequestResult(localRequestInfo, paramAnonymousTL_error);
        }
      };
      if (!this.isForceRequest) {
        break label313;
      }
      k = 32;
      n = this.datacenter_id;
      if (this.requestsCount % 2 != 0) {
        break label318;
      }
    }
    label307:
    label313:
    label318:
    for (int m = 2;; m = 65538)
    {
      RequestInfo.access$702(localRequestInfo, localConnectionsManager.sendRequest(localTL_upload_getFile, local5, null, k | 0x2, n, m, bool));
      this.requestsCount += 1;
      j += 1;
      break label77;
      break;
      bool = false;
      break label140;
      k = 0;
      break label247;
    }
  }
  
  public void cancel()
  {
    Utilities.stageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        if ((FileLoadOperation.this.state == 3) || (FileLoadOperation.this.state == 2)) {
          return;
        }
        if (FileLoadOperation.this.requestInfos != null)
        {
          int i = 0;
          while (i < FileLoadOperation.this.requestInfos.size())
          {
            FileLoadOperation.RequestInfo localRequestInfo = (FileLoadOperation.RequestInfo)FileLoadOperation.this.requestInfos.get(i);
            if (FileLoadOperation.RequestInfo.access$700(localRequestInfo) != 0) {
              ConnectionsManager.getInstance().cancelRequest(FileLoadOperation.RequestInfo.access$700(localRequestInfo), true);
            }
            i += 1;
          }
        }
        FileLoadOperation.this.onFail(false, 1);
      }
    });
  }
  
  public String getFileName()
  {
    return this.location.volume_id + "_" + this.location.local_id + "." + this.ext;
  }
  
  public boolean isForceRequest()
  {
    return this.isForceRequest;
  }
  
  public void setDelegate(FileLoadOperationDelegate paramFileLoadOperationDelegate)
  {
    this.delegate = paramFileLoadOperationDelegate;
  }
  
  public void setForceRequest(boolean paramBoolean)
  {
    this.isForceRequest = paramBoolean;
  }
  
  public void setPaths(File paramFile1, File paramFile2)
  {
    this.storePath = paramFile1;
    this.tempPath = paramFile2;
  }
  
  public boolean start()
  {
    if (this.state != 0) {
      return false;
    }
    if (this.location == null)
    {
      onFail(true, 0);
      return false;
    }
    String str1 = null;
    String str4;
    String str5;
    String str2;
    String str3;
    if ((this.location.volume_id != 0L) && (this.location.local_id != 0))
    {
      if ((this.datacenter_id == Integer.MIN_VALUE) || (this.location.volume_id == -2147483648L) || (this.datacenter_id == 0))
      {
        onFail(true, 0);
        return false;
      }
      str4 = this.location.volume_id + "_" + this.location.local_id + ".temp";
      str5 = this.location.volume_id + "_" + this.location.local_id + "." + this.ext;
      str2 = str5;
      str3 = str4;
      if (this.key != null)
      {
        str1 = this.location.volume_id + "_" + this.location.local_id + ".iv";
        str3 = str4;
        str2 = str5;
      }
    }
    for (;;)
    {
      int i;
      if (this.totalBytesCount >= 1048576)
      {
        i = 131072;
        label262:
        this.currentDownloadChunkSize = i;
        if (this.totalBytesCount < 1048576) {
          break label813;
        }
        i = 2;
        label278:
        this.currentMaxDownloadRequests = i;
        this.requestInfos = new ArrayList(this.currentMaxDownloadRequests);
        this.delayedRequestInfos = new ArrayList(this.currentMaxDownloadRequests - 1);
        this.state = 1;
        this.cacheFileFinal = new File(this.storePath, str2);
        if ((this.cacheFileFinal.exists()) && (this.totalBytesCount != 0) && (this.totalBytesCount != this.cacheFileFinal.length())) {
          this.cacheFileFinal.delete();
        }
        if (this.cacheFileFinal.exists()) {
          break label876;
        }
        this.cacheFileTemp = new File(this.tempPath, str3);
        if (this.cacheFileTemp.exists())
        {
          this.downloadedBytes = ((int)this.cacheFileTemp.length());
          i = this.downloadedBytes / this.currentDownloadChunkSize * this.currentDownloadChunkSize;
          this.downloadedBytes = i;
          this.nextDownloadOffset = i;
        }
        if (BuildVars.DEBUG_VERSION) {
          FileLog.d("tmessages", "start loading file to temp = " + this.cacheFileTemp + " final = " + this.cacheFileFinal);
        }
        if (str1 != null) {
          this.cacheIvTemp = new File(this.tempPath, str1);
        }
      }
      try
      {
        this.fiv = new RandomAccessFile(this.cacheIvTemp, "rws");
        long l = this.cacheIvTemp.length();
        if ((l > 0L) && (l % 32L == 0L)) {
          this.fiv.read(this.iv, 0, 32);
        }
      }
      catch (Exception localException1)
      {
        try
        {
          for (;;)
          {
            this.fileOutputStream = new RandomAccessFile(this.cacheFileTemp, "rws");
            if (this.downloadedBytes != 0) {
              this.fileOutputStream.seek(this.downloadedBytes);
            }
            if (this.fileOutputStream != null) {
              break label855;
            }
            onFail(true, 0);
            return false;
            if ((this.datacenter_id == 0) || (this.location.id == 0L))
            {
              onFail(true, 0);
              return false;
            }
            str4 = this.datacenter_id + "_" + this.location.id + ".temp";
            str5 = this.datacenter_id + "_" + this.location.id + this.ext;
            str2 = str5;
            str3 = str4;
            if (this.key == null) {
              break;
            }
            str1 = this.datacenter_id + "_" + this.location.id + ".iv";
            str2 = str5;
            str3 = str4;
            break;
            i = 32768;
            break label262;
            label813:
            i = 4;
            break label278;
            this.downloadedBytes = 0;
            continue;
            localException1 = localException1;
            FileLog.e("tmessages", localException1);
            this.downloadedBytes = 0;
          }
        }
        catch (Exception localException2)
        {
          for (;;)
          {
            FileLog.e("tmessages", localException2);
          }
          label855:
          this.started = true;
          Utilities.stageQueue.postRunnable(new Runnable()
          {
            public void run()
            {
              if ((FileLoadOperation.this.totalBytesCount != 0) && (FileLoadOperation.this.downloadedBytes == FileLoadOperation.this.totalBytesCount)) {
                try
                {
                  FileLoadOperation.this.onFinishLoadingFile();
                  return;
                }
                catch (Exception localException)
                {
                  FileLoadOperation.this.onFail(true, 0);
                  return;
                }
              }
              FileLoadOperation.this.startDownloadRequest();
            }
          });
        }
      }
    }
    for (;;)
    {
      return true;
      label876:
      this.started = true;
      try
      {
        onFinishLoadingFile();
      }
      catch (Exception localException3)
      {
        onFail(true, 0);
      }
    }
  }
  
  public boolean wasStarted()
  {
    return this.started;
  }
  
  public static abstract interface FileLoadOperationDelegate
  {
    public abstract void didChangedLoadProgress(FileLoadOperation paramFileLoadOperation, float paramFloat);
    
    public abstract void didFailedLoadingFile(FileLoadOperation paramFileLoadOperation, int paramInt);
    
    public abstract void didFinishLoadingFile(FileLoadOperation paramFileLoadOperation, File paramFile);
  }
  
  private static class RequestInfo
  {
    private int offset;
    private int requestToken;
    private TLRPC.TL_upload_file response;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/FileLoadOperation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */