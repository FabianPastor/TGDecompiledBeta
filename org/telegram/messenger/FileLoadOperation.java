package org.telegram.messenger;

import android.util.SparseArray;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
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
import org.telegram.tgnet.TLRPC.TL_fileHash;
import org.telegram.tgnet.TLRPC.TL_fileLocation;
import org.telegram.tgnet.TLRPC.TL_inputDocumentFileLocation;
import org.telegram.tgnet.TLRPC.TL_inputEncryptedFileLocation;
import org.telegram.tgnet.TLRPC.TL_inputFileLocation;
import org.telegram.tgnet.TLRPC.TL_inputWebFileLocation;
import org.telegram.tgnet.TLRPC.TL_upload_cdnFile;
import org.telegram.tgnet.TLRPC.TL_upload_cdnFileReuploadNeeded;
import org.telegram.tgnet.TLRPC.TL_upload_file;
import org.telegram.tgnet.TLRPC.TL_upload_fileCdnRedirect;
import org.telegram.tgnet.TLRPC.TL_upload_getCdnFile;
import org.telegram.tgnet.TLRPC.TL_upload_getCdnFileHashes;
import org.telegram.tgnet.TLRPC.TL_upload_getFile;
import org.telegram.tgnet.TLRPC.TL_upload_getWebFile;
import org.telegram.tgnet.TLRPC.TL_upload_reuploadCdnFile;
import org.telegram.tgnet.TLRPC.TL_upload_webFile;
import org.telegram.tgnet.TLRPC.TL_webDocument;
import org.telegram.tgnet.TLRPC.Vector;

public class FileLoadOperation
{
  private static final int bigFileSizeFrom = 1048576;
  private static final int cdnChunkCheckSize = 131072;
  private static final int downloadChunkSize = 32768;
  private static final int downloadChunkSizeBig = 131072;
  private static final int maxCdnParts = 12288;
  private static final int maxDownloadRequests = 4;
  private static final int maxDownloadRequestsBig = 4;
  private static final int stateDownloading = 1;
  private static final int stateFailed = 2;
  private static final int stateFinished = 3;
  private static final int stateIdle = 0;
  private boolean allowDisordererFileSave;
  private int bytesCountPadding;
  private File cacheFileFinal;
  private File cacheFileParts;
  private File cacheFileTemp;
  private File cacheIvTemp;
  private byte[] cdnCheckBytes;
  private int cdnDatacenterId;
  private SparseArray<TLRPC.TL_fileHash> cdnHashes;
  private byte[] cdnIv;
  private byte[] cdnKey;
  private byte[] cdnToken;
  private int currentAccount;
  private int currentDownloadChunkSize;
  private int currentMaxDownloadRequests;
  private int currentType;
  private int datacenterId;
  private ArrayList<RequestInfo> delayedRequestInfos;
  private FileLoadOperationDelegate delegate;
  private volatile int downloadedBytes;
  private boolean encryptFile;
  private byte[] encryptIv;
  private byte[] encryptKey;
  private String ext;
  private RandomAccessFile fileOutputStream;
  private RandomAccessFile filePartsStream;
  private RandomAccessFile fileReadStream;
  private RandomAccessFile fiv;
  private int initialDatacenterId;
  private boolean isCdn;
  private boolean isForceRequest;
  private byte[] iv;
  private byte[] key;
  private TLRPC.InputFileLocation location;
  private ArrayList<Range> notCheckedCdnRanges;
  private ArrayList<Range> notLoadedBytesRanges;
  private volatile ArrayList<Range> notLoadedBytesRangesCopy;
  private ArrayList<Range> notRequestedBytesRanges;
  private volatile boolean paused;
  private int renameRetryCount;
  private ArrayList<RequestInfo> requestInfos;
  private int requestedBytesCount;
  private boolean requestingCdnOffsets;
  private int requestsCount;
  private boolean reuploadingCdn;
  private boolean started;
  private volatile int state = 0;
  private File storePath;
  private ArrayList<FileStreamLoadOperation> streamListeners;
  private int streamStartOffset;
  private File tempPath;
  private int totalBytesCount;
  private TLRPC.TL_inputWebFileLocation webLocation;
  
  public FileLoadOperation(TLRPC.Document paramDocument)
  {
    for (;;)
    {
      try
      {
        if (!(paramDocument instanceof TLRPC.TL_documentEncrypted)) {
          continue;
        }
        localObject = new org/telegram/tgnet/TLRPC$TL_inputEncryptedFileLocation;
        ((TLRPC.TL_inputEncryptedFileLocation)localObject).<init>();
        this.location = ((TLRPC.InputFileLocation)localObject);
        this.location.id = paramDocument.id;
        this.location.access_hash = paramDocument.access_hash;
        j = paramDocument.dc_id;
        this.datacenterId = j;
        this.initialDatacenterId = j;
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
            continue;
          }
        }
        this.ext = "";
      }
      catch (Exception paramDocument)
      {
        Object localObject;
        int j;
        FileLog.e(paramDocument);
        onFail(true, 0);
        continue;
        this.ext = this.ext.substring(j);
        continue;
        if (!"video/mp4".equals(paramDocument.mime_type)) {
          continue;
        }
        this.currentType = 33554432;
        continue;
        this.currentType = 67108864;
        continue;
        if (!paramDocument.equals("video/mp4")) {
          continue;
        }
        i = 0;
        continue;
        if (!paramDocument.equals("audio/ogg")) {
          continue;
        }
        i = 1;
        continue;
        this.ext = ".mp4";
        continue;
        this.ext = ".ogg";
        continue;
        this.ext = "";
        continue;
      }
      if (!"audio/ogg".equals(paramDocument.mime_type)) {
        continue;
      }
      this.currentType = 50331648;
      if (this.ext.length() <= 1)
      {
        if (paramDocument.mime_type == null) {
          continue;
        }
        paramDocument = paramDocument.mime_type;
      }
      switch (paramDocument.hashCode())
      {
      default: 
        switch (i)
        {
        default: 
          this.ext = "";
          return;
          if ((paramDocument instanceof TLRPC.TL_document))
          {
            localObject = new org/telegram/tgnet/TLRPC$TL_inputDocumentFileLocation;
            ((TLRPC.TL_inputDocumentFileLocation)localObject).<init>();
            this.location = ((TLRPC.InputFileLocation)localObject);
            this.location.id = paramDocument.id;
            this.location.access_hash = paramDocument.access_hash;
            j = paramDocument.dc_id;
            this.datacenterId = j;
            this.initialDatacenterId = j;
            this.allowDisordererFileSave = true;
          }
          break;
        }
        break;
      }
    }
  }
  
  public FileLoadOperation(TLRPC.FileLocation paramFileLocation, String paramString, int paramInt)
  {
    int i;
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
      i = paramFileLocation.dc_id;
      this.datacenterId = i;
      this.initialDatacenterId = i;
      this.currentType = 16777216;
      this.totalBytesCount = paramInt;
      if (paramString == null) {
        break label222;
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
      i = paramFileLocation.dc_id;
      this.datacenterId = i;
      this.initialDatacenterId = i;
      this.allowDisordererFileSave = true;
      break;
      label222:
      paramString = "jpg";
    }
  }
  
  public FileLoadOperation(TLRPC.TL_webDocument paramTL_webDocument)
  {
    this.webLocation = new TLRPC.TL_inputWebFileLocation();
    this.webLocation.url = paramTL_webDocument.url;
    this.webLocation.access_hash = paramTL_webDocument.access_hash;
    this.totalBytesCount = paramTL_webDocument.size;
    int i = paramTL_webDocument.dc_id;
    this.datacenterId = i;
    this.initialDatacenterId = i;
    String str = FileLoader.getExtensionByMime(paramTL_webDocument.mime_type);
    if (paramTL_webDocument.mime_type.startsWith("image/")) {
      this.currentType = 16777216;
    }
    for (;;)
    {
      this.allowDisordererFileSave = true;
      this.ext = ImageLoader.getHttpUrlExtension(paramTL_webDocument.url, str);
      return;
      if (paramTL_webDocument.mime_type.equals("audio/ogg")) {
        this.currentType = 50331648;
      } else if (paramTL_webDocument.mime_type.startsWith("video/")) {
        this.currentType = 33554432;
      } else {
        this.currentType = 67108864;
      }
    }
  }
  
  private void addPart(ArrayList<Range> paramArrayList, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    if ((paramArrayList == null) || (paramInt2 < paramInt1)) {}
    for (;;)
    {
      return;
      int i = 0;
      int j = paramArrayList.size();
      int k = 0;
      for (;;)
      {
        int m = i;
        Range localRange;
        if (k < j)
        {
          localRange = (Range)paramArrayList.get(k);
          if (paramInt1 > localRange.start) {
            break label172;
          }
          if (paramInt2 >= localRange.end)
          {
            paramArrayList.remove(k);
            m = 1;
          }
        }
        else
        {
          if (!paramBoolean) {
            break;
          }
          if (m == 0) {
            break label288;
          }
        }
        try
        {
          this.filePartsStream.seek(0L);
          paramInt2 = paramArrayList.size();
          this.filePartsStream.writeInt(paramInt2);
          paramInt1 = 0;
          for (;;)
          {
            if (paramInt1 < paramInt2)
            {
              localRange = (Range)paramArrayList.get(paramInt1);
              this.filePartsStream.writeInt(localRange.start);
              this.filePartsStream.writeInt(localRange.end);
              paramInt1++;
              continue;
              if (paramInt2 > localRange.start)
              {
                Range.access$102(localRange, paramInt2);
                m = 1;
                break;
                label172:
                if (paramInt2 < localRange.end)
                {
                  paramArrayList.add(0, new Range(localRange.start, paramInt1, null));
                  m = 1;
                  Range.access$102(localRange, paramInt2);
                  break;
                }
                if (paramInt1 < localRange.end)
                {
                  Range.access$002(localRange, paramInt1);
                  m = 1;
                  break;
                }
              }
              k++;
            }
          }
        }
        catch (Exception paramArrayList)
        {
          FileLog.e(paramArrayList);
        }
      }
      if (this.streamListeners != null)
      {
        paramInt2 = this.streamListeners.size();
        for (paramInt1 = 0; paramInt1 < paramInt2; paramInt1++) {
          ((FileStreamLoadOperation)this.streamListeners.get(paramInt1)).newDataAvailable();
        }
        continue;
        label288:
        if (BuildVars.LOGS_ENABLED) {
          FileLog.e(this.cacheFileFinal + " downloaded duplicate file part " + paramInt1 + " - " + paramInt2);
        }
      }
    }
  }
  
  /* Error */
  private void cleanup()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 512	org/telegram/messenger/FileLoadOperation:fileOutputStream	Ljava/io/RandomAccessFile;
    //   4: astore_1
    //   5: aload_1
    //   6: ifnull +25 -> 31
    //   9: aload_0
    //   10: getfield 512	org/telegram/messenger/FileLoadOperation:fileOutputStream	Ljava/io/RandomAccessFile;
    //   13: invokevirtual 516	java/io/RandomAccessFile:getChannel	()Ljava/nio/channels/FileChannel;
    //   16: invokevirtual 521	java/nio/channels/FileChannel:close	()V
    //   19: aload_0
    //   20: getfield 512	org/telegram/messenger/FileLoadOperation:fileOutputStream	Ljava/io/RandomAccessFile;
    //   23: invokevirtual 522	java/io/RandomAccessFile:close	()V
    //   26: aload_0
    //   27: aconst_null
    //   28: putfield 512	org/telegram/messenger/FileLoadOperation:fileOutputStream	Ljava/io/RandomAccessFile;
    //   31: aload_0
    //   32: getfield 524	org/telegram/messenger/FileLoadOperation:fileReadStream	Ljava/io/RandomAccessFile;
    //   35: astore_1
    //   36: aload_1
    //   37: ifnull +25 -> 62
    //   40: aload_0
    //   41: getfield 524	org/telegram/messenger/FileLoadOperation:fileReadStream	Ljava/io/RandomAccessFile;
    //   44: invokevirtual 516	java/io/RandomAccessFile:getChannel	()Ljava/nio/channels/FileChannel;
    //   47: invokevirtual 521	java/nio/channels/FileChannel:close	()V
    //   50: aload_0
    //   51: getfield 524	org/telegram/messenger/FileLoadOperation:fileReadStream	Ljava/io/RandomAccessFile;
    //   54: invokevirtual 522	java/io/RandomAccessFile:close	()V
    //   57: aload_0
    //   58: aconst_null
    //   59: putfield 524	org/telegram/messenger/FileLoadOperation:fileReadStream	Ljava/io/RandomAccessFile;
    //   62: aload_0
    //   63: getfield 449	org/telegram/messenger/FileLoadOperation:filePartsStream	Ljava/io/RandomAccessFile;
    //   66: astore_1
    //   67: aload_1
    //   68: ifnull +25 -> 93
    //   71: aload_0
    //   72: getfield 449	org/telegram/messenger/FileLoadOperation:filePartsStream	Ljava/io/RandomAccessFile;
    //   75: invokevirtual 516	java/io/RandomAccessFile:getChannel	()Ljava/nio/channels/FileChannel;
    //   78: invokevirtual 521	java/nio/channels/FileChannel:close	()V
    //   81: aload_0
    //   82: getfield 449	org/telegram/messenger/FileLoadOperation:filePartsStream	Ljava/io/RandomAccessFile;
    //   85: invokevirtual 522	java/io/RandomAccessFile:close	()V
    //   88: aload_0
    //   89: aconst_null
    //   90: putfield 449	org/telegram/messenger/FileLoadOperation:filePartsStream	Ljava/io/RandomAccessFile;
    //   93: aload_0
    //   94: getfield 526	org/telegram/messenger/FileLoadOperation:fiv	Ljava/io/RandomAccessFile;
    //   97: ifnull +15 -> 112
    //   100: aload_0
    //   101: getfield 526	org/telegram/messenger/FileLoadOperation:fiv	Ljava/io/RandomAccessFile;
    //   104: invokevirtual 522	java/io/RandomAccessFile:close	()V
    //   107: aload_0
    //   108: aconst_null
    //   109: putfield 526	org/telegram/messenger/FileLoadOperation:fiv	Ljava/io/RandomAccessFile;
    //   112: aload_0
    //   113: getfield 357	org/telegram/messenger/FileLoadOperation:delayedRequestInfos	Ljava/util/ArrayList;
    //   116: ifnull +169 -> 285
    //   119: iconst_0
    //   120: istore_2
    //   121: iload_2
    //   122: aload_0
    //   123: getfield 357	org/telegram/messenger/FileLoadOperation:delayedRequestInfos	Ljava/util/ArrayList;
    //   126: invokevirtual 433	java/util/ArrayList:size	()I
    //   129: if_icmpge +149 -> 278
    //   132: aload_0
    //   133: getfield 357	org/telegram/messenger/FileLoadOperation:delayedRequestInfos	Ljava/util/ArrayList;
    //   136: iload_2
    //   137: invokevirtual 437	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   140: checkcast 38	org/telegram/messenger/FileLoadOperation$RequestInfo
    //   143: astore_1
    //   144: aload_1
    //   145: invokestatic 530	org/telegram/messenger/FileLoadOperation$RequestInfo:access$2000	(Lorg/telegram/messenger/FileLoadOperation$RequestInfo;)Lorg/telegram/tgnet/TLRPC$TL_upload_file;
    //   148: ifnull +80 -> 228
    //   151: aload_1
    //   152: invokestatic 530	org/telegram/messenger/FileLoadOperation$RequestInfo:access$2000	(Lorg/telegram/messenger/FileLoadOperation$RequestInfo;)Lorg/telegram/tgnet/TLRPC$TL_upload_file;
    //   155: iconst_0
    //   156: putfield 535	org/telegram/tgnet/TLRPC$TL_upload_file:disableFree	Z
    //   159: aload_1
    //   160: invokestatic 530	org/telegram/messenger/FileLoadOperation$RequestInfo:access$2000	(Lorg/telegram/messenger/FileLoadOperation$RequestInfo;)Lorg/telegram/tgnet/TLRPC$TL_upload_file;
    //   163: invokevirtual 538	org/telegram/tgnet/TLRPC$TL_upload_file:freeResources	()V
    //   166: iinc 2 1
    //   169: goto -48 -> 121
    //   172: astore_1
    //   173: aload_1
    //   174: invokestatic 230	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   177: goto -158 -> 19
    //   180: astore_1
    //   181: aload_1
    //   182: invokestatic 230	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   185: goto -154 -> 31
    //   188: astore_1
    //   189: aload_1
    //   190: invokestatic 230	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   193: goto -143 -> 50
    //   196: astore_1
    //   197: aload_1
    //   198: invokestatic 230	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   201: goto -139 -> 62
    //   204: astore_1
    //   205: aload_1
    //   206: invokestatic 230	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   209: goto -128 -> 81
    //   212: astore_1
    //   213: aload_1
    //   214: invokestatic 230	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   217: goto -124 -> 93
    //   220: astore_1
    //   221: aload_1
    //   222: invokestatic 230	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   225: goto -113 -> 112
    //   228: aload_1
    //   229: invokestatic 542	org/telegram/messenger/FileLoadOperation$RequestInfo:access$2100	(Lorg/telegram/messenger/FileLoadOperation$RequestInfo;)Lorg/telegram/tgnet/TLRPC$TL_upload_webFile;
    //   232: ifnull +21 -> 253
    //   235: aload_1
    //   236: invokestatic 542	org/telegram/messenger/FileLoadOperation$RequestInfo:access$2100	(Lorg/telegram/messenger/FileLoadOperation$RequestInfo;)Lorg/telegram/tgnet/TLRPC$TL_upload_webFile;
    //   239: iconst_0
    //   240: putfield 545	org/telegram/tgnet/TLRPC$TL_upload_webFile:disableFree	Z
    //   243: aload_1
    //   244: invokestatic 542	org/telegram/messenger/FileLoadOperation$RequestInfo:access$2100	(Lorg/telegram/messenger/FileLoadOperation$RequestInfo;)Lorg/telegram/tgnet/TLRPC$TL_upload_webFile;
    //   247: invokevirtual 546	org/telegram/tgnet/TLRPC$TL_upload_webFile:freeResources	()V
    //   250: goto -84 -> 166
    //   253: aload_1
    //   254: invokestatic 550	org/telegram/messenger/FileLoadOperation$RequestInfo:access$2200	(Lorg/telegram/messenger/FileLoadOperation$RequestInfo;)Lorg/telegram/tgnet/TLRPC$TL_upload_cdnFile;
    //   257: ifnull -91 -> 166
    //   260: aload_1
    //   261: invokestatic 550	org/telegram/messenger/FileLoadOperation$RequestInfo:access$2200	(Lorg/telegram/messenger/FileLoadOperation$RequestInfo;)Lorg/telegram/tgnet/TLRPC$TL_upload_cdnFile;
    //   264: iconst_0
    //   265: putfield 553	org/telegram/tgnet/TLRPC$TL_upload_cdnFile:disableFree	Z
    //   268: aload_1
    //   269: invokestatic 550	org/telegram/messenger/FileLoadOperation$RequestInfo:access$2200	(Lorg/telegram/messenger/FileLoadOperation$RequestInfo;)Lorg/telegram/tgnet/TLRPC$TL_upload_cdnFile;
    //   272: invokevirtual 554	org/telegram/tgnet/TLRPC$TL_upload_cdnFile:freeResources	()V
    //   275: goto -109 -> 166
    //   278: aload_0
    //   279: getfield 357	org/telegram/messenger/FileLoadOperation:delayedRequestInfos	Ljava/util/ArrayList;
    //   282: invokevirtual 557	java/util/ArrayList:clear	()V
    //   285: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	286	0	this	FileLoadOperation
    //   4	156	1	localObject	Object
    //   172	2	1	localException1	Exception
    //   180	2	1	localException2	Exception
    //   188	2	1	localException3	Exception
    //   196	2	1	localException4	Exception
    //   204	2	1	localException5	Exception
    //   212	2	1	localException6	Exception
    //   220	49	1	localException7	Exception
    //   120	47	2	i	int
    // Exception table:
    //   from	to	target	type
    //   9	19	172	java/lang/Exception
    //   0	5	180	java/lang/Exception
    //   19	31	180	java/lang/Exception
    //   173	177	180	java/lang/Exception
    //   40	50	188	java/lang/Exception
    //   31	36	196	java/lang/Exception
    //   50	62	196	java/lang/Exception
    //   189	193	196	java/lang/Exception
    //   71	81	204	java/lang/Exception
    //   62	67	212	java/lang/Exception
    //   81	93	212	java/lang/Exception
    //   205	209	212	java/lang/Exception
    //   93	112	220	java/lang/Exception
  }
  
  private void clearOperaion(RequestInfo paramRequestInfo)
  {
    int i = Integer.MAX_VALUE;
    int j = 0;
    if (j < this.requestInfos.size())
    {
      RequestInfo localRequestInfo = (RequestInfo)this.requestInfos.get(j);
      i = Math.min(localRequestInfo.offset, i);
      removePart(this.notRequestedBytesRanges, localRequestInfo.offset, localRequestInfo.offset + this.currentDownloadChunkSize);
      if (paramRequestInfo == localRequestInfo) {}
      for (;;)
      {
        j++;
        break;
        if (localRequestInfo.requestToken != 0) {
          ConnectionsManager.getInstance(this.currentAccount).cancelRequest(localRequestInfo.requestToken, true);
        }
      }
    }
    this.requestInfos.clear();
    j = 0;
    if (j < this.delayedRequestInfos.size())
    {
      paramRequestInfo = (RequestInfo)this.delayedRequestInfos.get(j);
      removePart(this.notRequestedBytesRanges, paramRequestInfo.offset, paramRequestInfo.offset + this.currentDownloadChunkSize);
      if (paramRequestInfo.response != null)
      {
        paramRequestInfo.response.disableFree = false;
        paramRequestInfo.response.freeResources();
      }
      for (;;)
      {
        i = Math.min(paramRequestInfo.offset, i);
        j++;
        break;
        if (paramRequestInfo.responseWeb != null)
        {
          paramRequestInfo.responseWeb.disableFree = false;
          paramRequestInfo.responseWeb.freeResources();
        }
        else if (paramRequestInfo.responseCdn != null)
        {
          paramRequestInfo.responseCdn.disableFree = false;
          paramRequestInfo.responseCdn.freeResources();
        }
      }
    }
    this.delayedRequestInfos.clear();
    this.requestsCount = 0;
    if (this.notLoadedBytesRanges == null)
    {
      this.downloadedBytes = i;
      this.requestedBytesCount = i;
    }
  }
  
  private void copytNotLoadedRanges()
  {
    if (this.notLoadedBytesRanges == null) {}
    for (;;)
    {
      return;
      this.notLoadedBytesRangesCopy = new ArrayList(this.notLoadedBytesRanges);
    }
  }
  
  private void delayRequestInfo(RequestInfo paramRequestInfo)
  {
    this.delayedRequestInfos.add(paramRequestInfo);
    if (paramRequestInfo.response != null) {
      paramRequestInfo.response.disableFree = true;
    }
    for (;;)
    {
      return;
      if (paramRequestInfo.responseWeb != null) {
        paramRequestInfo.responseWeb.disableFree = true;
      } else if (paramRequestInfo.responseCdn != null) {
        paramRequestInfo.responseCdn.disableFree = true;
      }
    }
  }
  
  private int getDownloadedLengthFromOffsetInternal(ArrayList<Range> paramArrayList, int paramInt1, int paramInt2)
  {
    if ((paramArrayList == null) || (this.state == 3) || (paramArrayList.isEmpty())) {
      if (this.downloadedBytes != 0) {}
    }
    for (;;)
    {
      return paramInt2;
      paramInt2 = Math.min(paramInt2, Math.max(this.downloadedBytes - paramInt1, 0));
      continue;
      int i = paramArrayList.size();
      Object localObject1 = null;
      int j = paramInt2;
      int k = 0;
      while (k < i)
      {
        Range localRange = (Range)paramArrayList.get(k);
        Object localObject2 = localObject1;
        if (paramInt1 <= localRange.start) {
          if (localObject1 != null)
          {
            localObject2 = localObject1;
            if (localRange.start >= ((Range)localObject1).start) {}
          }
          else
          {
            localObject2 = localRange;
          }
        }
        int m = j;
        if (localRange.start <= paramInt1)
        {
          m = j;
          if (localRange.end > paramInt1) {
            m = 0;
          }
        }
        k++;
        j = m;
        localObject1 = localObject2;
      }
      if (j == 0) {
        paramInt2 = 0;
      } else if (localObject1 != null) {
        paramInt2 = Math.min(paramInt2, ((Range)localObject1).start - paramInt1);
      } else {
        paramInt2 = Math.min(paramInt2, Math.max(this.totalBytesCount - paramInt1, 0));
      }
    }
  }
  
  private void onFail(boolean paramBoolean, final int paramInt)
  {
    cleanup();
    this.state = 2;
    if (paramBoolean) {
      Utilities.stageQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          FileLoadOperation.this.delegate.didFailedLoadingFile(FileLoadOperation.this, paramInt);
        }
      });
    }
    for (;;)
    {
      return;
      this.delegate.didFailedLoadingFile(this, paramInt);
    }
  }
  
  private void onFinishLoadingFile(final boolean paramBoolean)
    throws Exception
  {
    if (this.state != 1) {}
    for (;;)
    {
      return;
      this.state = 3;
      cleanup();
      if (this.cacheIvTemp != null)
      {
        this.cacheIvTemp.delete();
        this.cacheIvTemp = null;
      }
      if (this.cacheFileParts != null)
      {
        this.cacheFileParts.delete();
        this.cacheFileParts = null;
      }
      if ((this.cacheFileTemp != null) && (!this.cacheFileTemp.renameTo(this.cacheFileFinal)))
      {
        if (BuildVars.LOGS_ENABLED) {
          FileLog.e("unable to rename temp = " + this.cacheFileTemp + " to final = " + this.cacheFileFinal + " retry = " + this.renameRetryCount);
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
                FileLoadOperation.this.onFinishLoadingFile(paramBoolean);
                return;
              }
              catch (Exception localException)
              {
                for (;;)
                {
                  FileLoadOperation.this.onFail(false, 0);
                }
              }
            }
          }, 200L);
        }
        else
        {
          this.cacheFileFinal = this.cacheFileTemp;
        }
      }
      else
      {
        if (BuildVars.LOGS_ENABLED) {
          FileLog.d("finished downloading file to " + this.cacheFileFinal);
        }
        this.delegate.didFinishLoadingFile(this, this.cacheFileFinal);
        if (paramBoolean) {
          if (this.currentType == 50331648) {
            StatsController.getInstance(this.currentAccount).incrementReceivedItemsCount(ConnectionsManager.getCurrentNetworkType(), 3, 1);
          } else if (this.currentType == 33554432) {
            StatsController.getInstance(this.currentAccount).incrementReceivedItemsCount(ConnectionsManager.getCurrentNetworkType(), 2, 1);
          } else if (this.currentType == 16777216) {
            StatsController.getInstance(this.currentAccount).incrementReceivedItemsCount(ConnectionsManager.getCurrentNetworkType(), 4, 1);
          } else if (this.currentType == 67108864) {
            StatsController.getInstance(this.currentAccount).incrementReceivedItemsCount(ConnectionsManager.getCurrentNetworkType(), 5, 1);
          }
        }
      }
    }
  }
  
  private boolean processRequestResult(RequestInfo paramRequestInfo, TLRPC.TL_error paramTL_error)
  {
    boolean bool;
    if (this.state != 1) {
      bool = false;
    }
    label130:
    int i;
    int j;
    label203:
    label340:
    int k;
    int m;
    for (;;)
    {
      return bool;
      this.requestInfos.remove(paramRequestInfo);
      if (paramTL_error == null) {
        try
        {
          if ((this.notLoadedBytesRanges == null) && (this.downloadedBytes != paramRequestInfo.offset))
          {
            delayRequestInfo(paramRequestInfo);
            bool = false;
          }
          else
          {
            if (paramRequestInfo.response != null) {
              paramTL_error = paramRequestInfo.response.bytes;
            }
            for (;;)
            {
              if ((paramTL_error != null) && (paramTL_error.limit() != 0)) {
                break label130;
              }
              onFinishLoadingFile(true);
              bool = false;
              break;
              if (paramRequestInfo.responseWeb != null) {
                paramTL_error = paramRequestInfo.responseWeb.bytes;
              } else if (paramRequestInfo.responseCdn != null) {
                paramTL_error = paramRequestInfo.responseCdn.bytes;
              } else {
                paramTL_error = null;
              }
            }
            i = paramTL_error.limit();
            if (this.isCdn)
            {
              j = paramRequestInfo.offset / 131072 * 131072;
              if (this.cdnHashes != null) {}
              for (TLRPC.TL_fileHash localTL_fileHash = (TLRPC.TL_fileHash)this.cdnHashes.get(j);; localTL_fileHash = null)
              {
                if (localTL_fileHash != null) {
                  break label203;
                }
                delayRequestInfo(paramRequestInfo);
                requestFileOffsets(j);
                bool = true;
                break;
              }
            }
            if (paramRequestInfo.responseCdn != null)
            {
              j = paramRequestInfo.offset / 16;
              this.cdnIv[15] = ((byte)(byte)(j & 0xFF));
              this.cdnIv[14] = ((byte)(byte)(j >> 8 & 0xFF));
              this.cdnIv[13] = ((byte)(byte)(j >> 16 & 0xFF));
              this.cdnIv[12] = ((byte)(byte)(j >> 24 & 0xFF));
              Utilities.aesCtrDecryption(paramTL_error.buffer, this.cdnKey, this.cdnIv, 0, paramTL_error.limit());
            }
            this.downloadedBytes += i;
            if (this.totalBytesCount > 0) {
              if (this.downloadedBytes >= this.totalBytesCount)
              {
                j = 1;
                if (this.key != null)
                {
                  Utilities.aesIgeEncryption(paramTL_error.buffer, this.key, this.iv, false, true, 0, paramTL_error.limit());
                  if ((j != 0) && (this.bytesCountPadding != 0)) {
                    paramTL_error.limit(paramTL_error.limit() - this.bytesCountPadding);
                  }
                }
                if (this.encryptFile)
                {
                  k = paramRequestInfo.offset / 16;
                  this.encryptIv[15] = ((byte)(byte)(k & 0xFF));
                  this.encryptIv[14] = ((byte)(byte)(k >> 8 & 0xFF));
                  this.encryptIv[13] = ((byte)(byte)(k >> 16 & 0xFF));
                  this.encryptIv[12] = ((byte)(byte)(k >> 24 & 0xFF));
                  Utilities.aesCtrDecryption(paramTL_error.buffer, this.encryptKey, this.encryptIv, 0, paramTL_error.limit());
                }
                if (this.notLoadedBytesRanges != null) {
                  this.fileOutputStream.seek(paramRequestInfo.offset);
                }
                this.fileOutputStream.getChannel().write(paramTL_error.buffer);
                addPart(this.notLoadedBytesRanges, paramRequestInfo.offset, paramRequestInfo.offset + i, true);
                if (!this.isCdn) {
                  break label1114;
                }
                m = paramRequestInfo.offset / 131072;
                int n = this.notCheckedCdnRanges.size();
                int i1 = 1;
                k = 0;
                label584:
                i = i1;
                if (k < n)
                {
                  paramRequestInfo = (Range)this.notCheckedCdnRanges.get(k);
                  if ((paramRequestInfo.start > m) || (m > paramRequestInfo.end)) {
                    break label998;
                  }
                  i = 0;
                }
                if (i != 0) {
                  break label1114;
                }
                k = m * 131072;
                i = getDownloadedLengthFromOffsetInternal(this.notLoadedBytesRanges, k, 131072);
                if ((i == 0) || ((i != 131072) && ((this.totalBytesCount <= 0) || (i != this.totalBytesCount - k)) && ((this.totalBytesCount > 0) || (j == 0)))) {
                  break label1114;
                }
                paramTL_error = (TLRPC.TL_fileHash)this.cdnHashes.get(k);
                if (this.fileReadStream == null)
                {
                  this.cdnCheckBytes = new byte[131072];
                  paramRequestInfo = new java/io/RandomAccessFile;
                  paramRequestInfo.<init>(this.cacheFileTemp, "r");
                  this.fileReadStream = paramRequestInfo;
                }
                this.fileReadStream.seek(k);
                this.fileReadStream.readFully(this.cdnCheckBytes, 0, i);
                if (Arrays.equals(Utilities.computeSHA256(this.cdnCheckBytes, 0, i), paramTL_error.hash)) {
                  break label1090;
                }
                if (BuildVars.LOGS_ENABLED)
                {
                  if (this.location == null) {
                    break label1004;
                  }
                  paramRequestInfo = new java/lang/StringBuilder;
                  paramRequestInfo.<init>();
                  FileLog.e("invalid cdn hash " + this.location + " id = " + this.location.id + " local_id = " + this.location.local_id + " access_hash = " + this.location.access_hash + " volume_id = " + this.location.volume_id + " secret = " + this.location.secret);
                }
              }
            }
            for (;;)
            {
              onFail(false, 0);
              this.cacheFileTemp.delete();
              bool = false;
              break;
              j = 0;
              break label340;
              if ((i != this.currentDownloadChunkSize) || (((this.totalBytesCount == this.downloadedBytes) || (this.downloadedBytes % this.currentDownloadChunkSize != 0)) && ((this.totalBytesCount <= 0) || (this.totalBytesCount <= this.downloadedBytes)))) {}
              for (j = 1;; j = 0) {
                break;
              }
              label998:
              k++;
              break label584;
              label1004:
              if (this.webLocation != null)
              {
                paramRequestInfo = new java/lang/StringBuilder;
                paramRequestInfo.<init>();
                FileLog.e("invalid cdn hash  " + this.webLocation + " id = " + this.webLocation.url + " access_hash = " + this.webLocation.access_hash);
              }
            }
            bool = false;
          }
        }
        catch (Exception paramRequestInfo)
        {
          onFail(false, 0);
          FileLog.e(paramRequestInfo);
        }
      }
    }
    for (;;)
    {
      break;
      label1090:
      this.cdnHashes.remove(k);
      addPart(this.notCheckedCdnRanges, m, m + 1, false);
      label1114:
      if (this.fiv != null)
      {
        this.fiv.seek(0L);
        this.fiv.write(this.iv);
      }
      if ((this.totalBytesCount > 0) && (this.state == 1))
      {
        copytNotLoadedRanges();
        this.delegate.didChangedLoadProgress(this, Math.min(1.0F, this.downloadedBytes / this.totalBytesCount));
      }
      for (i = 0;; i++)
      {
        if (i < this.delayedRequestInfos.size())
        {
          paramRequestInfo = (RequestInfo)this.delayedRequestInfos.get(i);
          if ((this.notLoadedBytesRanges == null) && (this.downloadedBytes != paramRequestInfo.offset)) {
            continue;
          }
          this.delayedRequestInfos.remove(i);
          if (!processRequestResult(paramRequestInfo, null))
          {
            if (paramRequestInfo.response == null) {
              break label1284;
            }
            paramRequestInfo.response.disableFree = false;
            paramRequestInfo.response.freeResources();
          }
        }
        for (;;)
        {
          if (j == 0) {
            break label1340;
          }
          onFinishLoadingFile(true);
          break;
          label1284:
          if (paramRequestInfo.responseWeb != null)
          {
            paramRequestInfo.responseWeb.disableFree = false;
            paramRequestInfo.responseWeb.freeResources();
          }
          else if (paramRequestInfo.responseCdn != null)
          {
            paramRequestInfo.responseCdn.disableFree = false;
            paramRequestInfo.responseCdn.freeResources();
          }
        }
      }
      label1340:
      startDownloadRequest();
      continue;
      if (paramTL_error.text.contains("FILE_MIGRATE_"))
      {
        paramRequestInfo = new Scanner(paramTL_error.text.replace("FILE_MIGRATE_", ""));
        paramRequestInfo.useDelimiter("");
        try
        {
          j = paramRequestInfo.nextInt();
          paramRequestInfo = Integer.valueOf(j);
        }
        catch (Exception paramRequestInfo)
        {
          for (;;)
          {
            paramRequestInfo = null;
          }
          this.datacenterId = paramRequestInfo.intValue();
          this.downloadedBytes = 0;
          this.requestedBytesCount = 0;
          startDownloadRequest();
        }
        if (paramRequestInfo == null) {
          onFail(false, 0);
        }
      }
      else if (paramTL_error.text.contains("OFFSET_INVALID"))
      {
        if (this.downloadedBytes % this.currentDownloadChunkSize == 0) {
          try
          {
            onFinishLoadingFile(true);
          }
          catch (Exception paramRequestInfo)
          {
            FileLog.e(paramRequestInfo);
            onFail(false, 0);
          }
        } else {
          onFail(false, 0);
        }
      }
      else
      {
        if (!paramTL_error.text.contains("RETRY_LIMIT")) {
          break label1521;
        }
        onFail(false, 2);
      }
    }
    label1521:
    if (BuildVars.LOGS_ENABLED)
    {
      if (this.location == null) {
        break label1648;
      }
      FileLog.e("" + this.location + " id = " + this.location.id + " local_id = " + this.location.local_id + " access_hash = " + this.location.access_hash + " volume_id = " + this.location.volume_id + " secret = " + this.location.secret);
    }
    for (;;)
    {
      onFail(false, 0);
      break;
      label1648:
      if (this.webLocation != null) {
        FileLog.e("" + this.webLocation + " id = " + this.webLocation.url + " access_hash = " + this.webLocation.access_hash);
      }
    }
  }
  
  private void removePart(ArrayList<Range> paramArrayList, int paramInt1, int paramInt2)
  {
    if ((paramArrayList == null) || (paramInt2 < paramInt1)) {
      return;
    }
    int i = paramArrayList.size();
    int j = 0;
    label86:
    label106:
    label108:
    for (int k = 0;; k++)
    {
      int m = j;
      Range localRange;
      if (k < i)
      {
        localRange = (Range)paramArrayList.get(k);
        if (paramInt1 != localRange.end) {
          break label86;
        }
        Range.access$002(localRange, paramInt2);
      }
      for (m = 1;; m = 1)
      {
        if (m != 0) {
          break label106;
        }
        paramArrayList.add(new Range(paramInt1, paramInt2, null));
        break;
        if (paramInt2 != localRange.start) {
          break label108;
        }
        Range.access$102(localRange, paramInt1);
      }
      break;
    }
  }
  
  private void requestFileOffsets(int paramInt)
  {
    if (this.requestingCdnOffsets) {}
    for (;;)
    {
      return;
      this.requestingCdnOffsets = true;
      TLRPC.TL_upload_getCdnFileHashes localTL_upload_getCdnFileHashes = new TLRPC.TL_upload_getCdnFileHashes();
      localTL_upload_getCdnFileHashes.file_token = this.cdnToken;
      localTL_upload_getCdnFileHashes.offset = paramInt;
      ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_upload_getCdnFileHashes, new RequestDelegate()
      {
        public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
        {
          if (paramAnonymousTL_error != null) {
            FileLoadOperation.this.onFail(false, 0);
          }
          label267:
          for (;;)
          {
            return;
            FileLoadOperation.access$2302(FileLoadOperation.this, false);
            paramAnonymousTLObject = (TLRPC.Vector)paramAnonymousTLObject;
            if (!paramAnonymousTLObject.objects.isEmpty())
            {
              if (FileLoadOperation.this.cdnHashes == null) {
                FileLoadOperation.access$2402(FileLoadOperation.this, new SparseArray());
              }
              for (i = 0; i < paramAnonymousTLObject.objects.size(); i++)
              {
                paramAnonymousTL_error = (TLRPC.TL_fileHash)paramAnonymousTLObject.objects.get(i);
                FileLoadOperation.this.cdnHashes.put(paramAnonymousTL_error.offset, paramAnonymousTL_error);
              }
            }
            for (int i = 0;; i++)
            {
              if (i >= FileLoadOperation.this.delayedRequestInfos.size()) {
                break label267;
              }
              paramAnonymousTLObject = (FileLoadOperation.RequestInfo)FileLoadOperation.this.delayedRequestInfos.get(i);
              if ((FileLoadOperation.this.notLoadedBytesRanges != null) || (FileLoadOperation.this.downloadedBytes == FileLoadOperation.RequestInfo.access$2600(paramAnonymousTLObject)))
              {
                FileLoadOperation.this.delayedRequestInfos.remove(i);
                if (FileLoadOperation.this.processRequestResult(paramAnonymousTLObject, null)) {
                  break;
                }
                if (FileLoadOperation.RequestInfo.access$2000(paramAnonymousTLObject) != null)
                {
                  FileLoadOperation.RequestInfo.access$2000(paramAnonymousTLObject).disableFree = false;
                  FileLoadOperation.RequestInfo.access$2000(paramAnonymousTLObject).freeResources();
                  break;
                }
                if (FileLoadOperation.RequestInfo.access$2100(paramAnonymousTLObject) != null)
                {
                  FileLoadOperation.RequestInfo.access$2100(paramAnonymousTLObject).disableFree = false;
                  FileLoadOperation.RequestInfo.access$2100(paramAnonymousTLObject).freeResources();
                  break;
                }
                if (FileLoadOperation.RequestInfo.access$2200(paramAnonymousTLObject) == null) {
                  break;
                }
                FileLoadOperation.RequestInfo.access$2200(paramAnonymousTLObject).disableFree = false;
                FileLoadOperation.RequestInfo.access$2200(paramAnonymousTLObject).freeResources();
                break;
              }
            }
          }
        }
      }, null, null, 0, this.datacenterId, 1, true);
    }
  }
  
  private void startDownloadRequest()
  {
    if ((this.paused) || (this.state != 1) || (this.requestInfos.size() + this.delayedRequestInfos.size() >= this.currentMaxDownloadRequests)) {}
    int i;
    int j;
    label66:
    int m;
    int i1;
    label99:
    int i2;
    int i3;
    final Object localObject;
    label185:
    do
    {
      return;
      i = 1;
      if (this.totalBytesCount > 0) {
        i = Math.max(0, this.currentMaxDownloadRequests - this.requestInfos.size());
      }
      j = 0;
      if (j >= i) {
        break;
      }
      if (this.notRequestedBytesRanges == null) {
        break label532;
      }
      int k = this.notRequestedBytesRanges.size();
      m = Integer.MAX_VALUE;
      n = Integer.MAX_VALUE;
      i1 = 0;
      i2 = m;
      i3 = n;
      if (i1 < k)
      {
        localObject = (Range)this.notRequestedBytesRanges.get(i1);
        i3 = n;
        if (this.streamStartOffset == 0) {
          break label495;
        }
        if ((((Range)localObject).start > this.streamStartOffset) || (((Range)localObject).end <= this.streamStartOffset)) {
          break label458;
        }
        i3 = this.streamStartOffset;
        i2 = Integer.MAX_VALUE;
      }
      if (i3 == Integer.MAX_VALUE) {
        break label517;
      }
      n = i3;
      if (this.notRequestedBytesRanges != null) {
        addPart(this.notRequestedBytesRanges, n, this.currentDownloadChunkSize + n, false);
      }
    } while ((this.totalBytesCount > 0) && (n >= this.totalBytesCount));
    boolean bool;
    label264:
    label276:
    label287:
    label340:
    final RequestInfo localRequestInfo;
    ConnectionsManager localConnectionsManager;
    RequestDelegate local12;
    if ((this.totalBytesCount <= 0) || (j == i - 1) || ((this.totalBytesCount > 0) && (this.currentDownloadChunkSize + n >= this.totalBytesCount)))
    {
      bool = true;
      if (this.requestsCount % 2 != 0) {
        break label547;
      }
      m = 2;
      if (!this.isForceRequest) {
        break label555;
      }
      i3 = 32;
      i3 |= 0x2;
      if (!this.isCdn) {
        break label561;
      }
      localObject = new TLRPC.TL_upload_getCdnFile();
      ((TLRPC.TL_upload_getCdnFile)localObject).file_token = this.cdnToken;
      ((TLRPC.TL_upload_getCdnFile)localObject).offset = n;
      ((TLRPC.TL_upload_getCdnFile)localObject).limit = this.currentDownloadChunkSize;
      i3 |= 0x1;
      this.requestedBytesCount += this.currentDownloadChunkSize;
      localRequestInfo = new RequestInfo(null);
      this.requestInfos.add(localRequestInfo);
      RequestInfo.access$2602(localRequestInfo, n);
      localConnectionsManager = ConnectionsManager.getInstance(this.currentAccount);
      local12 = new RequestDelegate()
      {
        public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
        {
          if (!FileLoadOperation.this.requestInfos.contains(localRequestInfo)) {}
          for (;;)
          {
            return;
            if ((paramAnonymousTL_error != null) && ((localObject instanceof TLRPC.TL_upload_getCdnFile)) && (paramAnonymousTL_error.text.equals("FILE_TOKEN_INVALID")))
            {
              FileLoadOperation.access$3002(FileLoadOperation.this, false);
              FileLoadOperation.this.clearOperaion(localRequestInfo);
              FileLoadOperation.this.startDownloadRequest();
            }
            else if ((paramAnonymousTLObject instanceof TLRPC.TL_upload_fileCdnRedirect))
            {
              paramAnonymousTLObject = (TLRPC.TL_upload_fileCdnRedirect)paramAnonymousTLObject;
              if (!paramAnonymousTLObject.file_hashes.isEmpty())
              {
                if (FileLoadOperation.this.cdnHashes == null) {
                  FileLoadOperation.access$2402(FileLoadOperation.this, new SparseArray());
                }
                for (int i = 0; i < paramAnonymousTLObject.file_hashes.size(); i++)
                {
                  paramAnonymousTL_error = (TLRPC.TL_fileHash)paramAnonymousTLObject.file_hashes.get(i);
                  FileLoadOperation.this.cdnHashes.put(paramAnonymousTL_error.offset, paramAnonymousTL_error);
                }
              }
              if ((paramAnonymousTLObject.encryption_iv == null) || (paramAnonymousTLObject.encryption_key == null) || (paramAnonymousTLObject.encryption_iv.length != 16) || (paramAnonymousTLObject.encryption_key.length != 32))
              {
                paramAnonymousTLObject = new TLRPC.TL_error();
                paramAnonymousTLObject.text = "bad redirect response";
                paramAnonymousTLObject.code = 400;
                FileLoadOperation.this.processRequestResult(localRequestInfo, paramAnonymousTLObject);
              }
              else
              {
                FileLoadOperation.access$3002(FileLoadOperation.this, true);
                if (FileLoadOperation.this.notCheckedCdnRanges == null)
                {
                  FileLoadOperation.access$3202(FileLoadOperation.this, new ArrayList());
                  FileLoadOperation.this.notCheckedCdnRanges.add(new FileLoadOperation.Range(0, 12288, null));
                }
                FileLoadOperation.access$3302(FileLoadOperation.this, paramAnonymousTLObject.dc_id);
                FileLoadOperation.access$3402(FileLoadOperation.this, paramAnonymousTLObject.encryption_iv);
                FileLoadOperation.access$3502(FileLoadOperation.this, paramAnonymousTLObject.encryption_key);
                FileLoadOperation.access$3602(FileLoadOperation.this, paramAnonymousTLObject.file_token);
                FileLoadOperation.this.clearOperaion(localRequestInfo);
                FileLoadOperation.this.startDownloadRequest();
              }
            }
            else
            {
              if (!(paramAnonymousTLObject instanceof TLRPC.TL_upload_cdnFileReuploadNeeded)) {
                break;
              }
              if (!FileLoadOperation.this.reuploadingCdn)
              {
                FileLoadOperation.this.clearOperaion(localRequestInfo);
                FileLoadOperation.access$3702(FileLoadOperation.this, true);
                paramAnonymousTLObject = (TLRPC.TL_upload_cdnFileReuploadNeeded)paramAnonymousTLObject;
                paramAnonymousTL_error = new TLRPC.TL_upload_reuploadCdnFile();
                paramAnonymousTL_error.file_token = FileLoadOperation.this.cdnToken;
                paramAnonymousTL_error.request_token = paramAnonymousTLObject.request_token;
                ConnectionsManager.getInstance(FileLoadOperation.this.currentAccount).sendRequest(paramAnonymousTL_error, new RequestDelegate()
                {
                  public void run(TLObject paramAnonymous2TLObject, TLRPC.TL_error paramAnonymous2TL_error)
                  {
                    FileLoadOperation.access$3702(FileLoadOperation.this, false);
                    if (paramAnonymous2TL_error == null)
                    {
                      paramAnonymous2TL_error = (TLRPC.Vector)paramAnonymous2TLObject;
                      if (!paramAnonymous2TL_error.objects.isEmpty())
                      {
                        if (FileLoadOperation.this.cdnHashes == null) {
                          FileLoadOperation.access$2402(FileLoadOperation.this, new SparseArray());
                        }
                        for (int i = 0; i < paramAnonymous2TL_error.objects.size(); i++)
                        {
                          paramAnonymous2TLObject = (TLRPC.TL_fileHash)paramAnonymous2TL_error.objects.get(i);
                          FileLoadOperation.this.cdnHashes.put(paramAnonymous2TLObject.offset, paramAnonymous2TLObject);
                        }
                      }
                      FileLoadOperation.this.startDownloadRequest();
                    }
                    for (;;)
                    {
                      return;
                      if ((paramAnonymous2TL_error.text.equals("FILE_TOKEN_INVALID")) || (paramAnonymous2TL_error.text.equals("REQUEST_TOKEN_INVALID")))
                      {
                        FileLoadOperation.access$3002(FileLoadOperation.this, false);
                        FileLoadOperation.this.clearOperaion(FileLoadOperation.12.this.val$requestInfo);
                        FileLoadOperation.this.startDownloadRequest();
                      }
                      else
                      {
                        FileLoadOperation.this.onFail(false, 0);
                      }
                    }
                  }
                }, null, null, 0, FileLoadOperation.this.datacenterId, 1, true);
              }
            }
          }
          if ((paramAnonymousTLObject instanceof TLRPC.TL_upload_file))
          {
            FileLoadOperation.RequestInfo.access$2002(localRequestInfo, (TLRPC.TL_upload_file)paramAnonymousTLObject);
            label490:
            if (paramAnonymousTLObject != null)
            {
              if (FileLoadOperation.this.currentType != 50331648) {
                break label625;
              }
              StatsController.getInstance(FileLoadOperation.this.currentAccount).incrementReceivedBytesCount(paramAnonymousTLObject.networkType, 3, paramAnonymousTLObject.getObjectSize() + 4);
            }
          }
          for (;;)
          {
            FileLoadOperation.this.processRequestResult(localRequestInfo, paramAnonymousTL_error);
            break;
            if ((paramAnonymousTLObject instanceof TLRPC.TL_upload_webFile))
            {
              FileLoadOperation.RequestInfo.access$2102(localRequestInfo, (TLRPC.TL_upload_webFile)paramAnonymousTLObject);
              if ((FileLoadOperation.this.totalBytesCount != 0) || (FileLoadOperation.RequestInfo.access$2100(localRequestInfo).size == 0)) {
                break label490;
              }
              FileLoadOperation.access$1302(FileLoadOperation.this, FileLoadOperation.RequestInfo.access$2100(localRequestInfo).size);
              break label490;
            }
            FileLoadOperation.RequestInfo.access$2202(localRequestInfo, (TLRPC.TL_upload_cdnFile)paramAnonymousTLObject);
            break label490;
            label625:
            if (FileLoadOperation.this.currentType == 33554432) {
              StatsController.getInstance(FileLoadOperation.this.currentAccount).incrementReceivedBytesCount(paramAnonymousTLObject.networkType, 2, paramAnonymousTLObject.getObjectSize() + 4);
            } else if (FileLoadOperation.this.currentType == 16777216) {
              StatsController.getInstance(FileLoadOperation.this.currentAccount).incrementReceivedBytesCount(paramAnonymousTLObject.networkType, 4, paramAnonymousTLObject.getObjectSize() + 4);
            } else if (FileLoadOperation.this.currentType == 67108864) {
              StatsController.getInstance(FileLoadOperation.this.currentAccount).incrementReceivedBytesCount(paramAnonymousTLObject.networkType, 5, paramAnonymousTLObject.getObjectSize() + 4);
            }
          }
        }
      };
      if (!this.isCdn) {
        break label642;
      }
    }
    label458:
    label495:
    label517:
    label532:
    label547:
    label555:
    label561:
    label642:
    for (int n = this.cdnDatacenterId;; n = this.datacenterId)
    {
      RequestInfo.access$1802(localRequestInfo, localConnectionsManager.sendRequest((TLObject)localObject, local12, null, null, i3, n, m, bool));
      this.requestsCount += 1;
      j++;
      break label66;
      break;
      i3 = n;
      if (this.streamStartOffset < ((Range)localObject).start)
      {
        i3 = n;
        if (((Range)localObject).start < n) {
          i3 = ((Range)localObject).start;
        }
      }
      m = Math.min(m, ((Range)localObject).start);
      i1++;
      n = i3;
      break label99;
      if (i2 == Integer.MAX_VALUE) {
        break;
      }
      n = i2;
      break label185;
      n = this.requestedBytesCount;
      break label185;
      bool = false;
      break label264;
      m = 65538;
      break label276;
      i3 = 0;
      break label287;
      if (this.webLocation != null)
      {
        localObject = new TLRPC.TL_upload_getWebFile();
        ((TLRPC.TL_upload_getWebFile)localObject).location = this.webLocation;
        ((TLRPC.TL_upload_getWebFile)localObject).offset = n;
        ((TLRPC.TL_upload_getWebFile)localObject).limit = this.currentDownloadChunkSize;
        break label340;
      }
      localObject = new TLRPC.TL_upload_getFile();
      ((TLRPC.TL_upload_getFile)localObject).location = this.location;
      ((TLRPC.TL_upload_getFile)localObject).offset = n;
      ((TLRPC.TL_upload_getFile)localObject).limit = this.currentDownloadChunkSize;
      break label340;
    }
  }
  
  public void cancel()
  {
    Utilities.stageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        if ((FileLoadOperation.this.state == 3) || (FileLoadOperation.this.state == 2)) {}
        for (;;)
        {
          return;
          if (FileLoadOperation.this.requestInfos != null) {
            for (int i = 0; i < FileLoadOperation.this.requestInfos.size(); i++)
            {
              FileLoadOperation.RequestInfo localRequestInfo = (FileLoadOperation.RequestInfo)FileLoadOperation.this.requestInfos.get(i);
              if (FileLoadOperation.RequestInfo.access$1800(localRequestInfo) != 0) {
                ConnectionsManager.getInstance(FileLoadOperation.this.currentAccount).cancelRequest(FileLoadOperation.RequestInfo.access$1800(localRequestInfo), true);
              }
            }
          }
          FileLoadOperation.this.onFail(false, 1);
        }
      }
    });
  }
  
  protected File getCurrentFile()
  {
    final CountDownLatch localCountDownLatch = new CountDownLatch(1);
    final File[] arrayOfFile = new File[1];
    Utilities.stageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        if (FileLoadOperation.this.state == 3) {
          arrayOfFile[0] = FileLoadOperation.this.cacheFileFinal;
        }
        for (;;)
        {
          localCountDownLatch.countDown();
          return;
          arrayOfFile[0] = FileLoadOperation.this.cacheFileTemp;
        }
      }
    });
    try
    {
      localCountDownLatch.await();
      return arrayOfFile[0];
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e(localException);
      }
    }
  }
  
  public int getCurrentType()
  {
    return this.currentType;
  }
  
  public int getDatacenterId()
  {
    return this.initialDatacenterId;
  }
  
  protected float getDownloadedLengthFromOffset(float paramFloat)
  {
    ArrayList localArrayList = this.notLoadedBytesRangesCopy;
    if ((this.totalBytesCount == 0) || (localArrayList == null)) {}
    for (paramFloat = 0.0F;; paramFloat = getDownloadedLengthFromOffsetInternal(localArrayList, (int)(this.totalBytesCount * paramFloat), this.totalBytesCount) / this.totalBytesCount + paramFloat) {
      return paramFloat;
    }
  }
  
  protected int getDownloadedLengthFromOffset(final int paramInt1, final int paramInt2)
  {
    final CountDownLatch localCountDownLatch = new CountDownLatch(1);
    final int[] arrayOfInt = new int[1];
    Utilities.stageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        arrayOfInt[0] = FileLoadOperation.this.getDownloadedLengthFromOffsetInternal(FileLoadOperation.this.notLoadedBytesRanges, paramInt1, paramInt2);
        localCountDownLatch.countDown();
      }
    });
    try
    {
      localCountDownLatch.await();
      return arrayOfInt[0];
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e(localException);
      }
    }
  }
  
  public String getFileName()
  {
    if (this.location != null) {}
    for (String str = this.location.volume_id + "_" + this.location.local_id + "." + this.ext;; str = Utilities.MD5(this.webLocation.url) + "." + this.ext) {
      return str;
    }
  }
  
  public boolean isForceRequest()
  {
    return this.isForceRequest;
  }
  
  public boolean isPaused()
  {
    return this.paused;
  }
  
  public void pause()
  {
    if (this.state != 1) {}
    for (;;)
    {
      return;
      Utilities.stageQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          FileLoadOperation.access$902(FileLoadOperation.this, true);
        }
      });
    }
  }
  
  protected void removeStreamListener(final FileStreamLoadOperation paramFileStreamLoadOperation)
  {
    Utilities.stageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        if (FileLoadOperation.this.streamListeners == null) {}
        for (;;)
        {
          return;
          FileLoadOperation.this.streamListeners.remove(paramFileStreamLoadOperation);
        }
      }
    });
  }
  
  public void setDelegate(FileLoadOperationDelegate paramFileLoadOperationDelegate)
  {
    this.delegate = paramFileLoadOperationDelegate;
  }
  
  public void setEncryptFile(boolean paramBoolean)
  {
    this.encryptFile = paramBoolean;
    if (this.encryptFile) {
      this.allowDisordererFileSave = false;
    }
  }
  
  public void setForceRequest(boolean paramBoolean)
  {
    this.isForceRequest = paramBoolean;
  }
  
  public void setPaths(int paramInt, File paramFile1, File paramFile2)
  {
    this.storePath = paramFile1;
    this.tempPath = paramFile2;
    this.currentAccount = paramInt;
  }
  
  public boolean start()
  {
    return start(null, 0);
  }
  
  public boolean start(final FileStreamLoadOperation paramFileStreamLoadOperation, final int paramInt)
  {
    int i;
    label33:
    final boolean bool1;
    label48:
    boolean bool2;
    if (this.currentDownloadChunkSize == 0)
    {
      if (this.totalBytesCount >= 1048576)
      {
        i = 131072;
        this.currentDownloadChunkSize = i;
        if (this.totalBytesCount < 1048576) {
          break label99;
        }
        this.currentMaxDownloadRequests = 4;
      }
    }
    else
    {
      if (this.state == 0) {
        break label102;
      }
      bool1 = true;
      bool2 = this.paused;
      this.paused = false;
      if (paramFileStreamLoadOperation == null) {
        break label108;
      }
      Utilities.stageQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          if (FileLoadOperation.this.streamListeners == null) {
            FileLoadOperation.access$802(FileLoadOperation.this, new ArrayList());
          }
          FileLoadOperation.access$1002(FileLoadOperation.this, paramInt / FileLoadOperation.this.currentDownloadChunkSize * FileLoadOperation.this.currentDownloadChunkSize);
          FileLoadOperation.this.streamListeners.add(paramFileStreamLoadOperation);
          if (bool1) {
            FileLoadOperation.this.startDownloadRequest();
          }
        }
      });
      label81:
      if (!bool1) {
        break label135;
      }
      bool1 = bool2;
    }
    for (;;)
    {
      return bool1;
      i = 32768;
      break;
      label99:
      break label33;
      label102:
      bool1 = false;
      break label48;
      label108:
      if ((!bool2) || (!bool1)) {
        break label81;
      }
      Utilities.stageQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          FileLoadOperation.this.startDownloadRequest();
        }
      });
      break label81;
      label135:
      if ((this.location == null) && (this.webLocation == null))
      {
        onFail(true, 0);
        bool1 = false;
      }
      else
      {
        this.streamStartOffset = (paramInt / this.currentDownloadChunkSize * this.currentDownloadChunkSize);
        if ((this.allowDisordererFileSave) && (this.totalBytesCount > 0) && (this.totalBytesCount > this.currentDownloadChunkSize))
        {
          this.notLoadedBytesRanges = new ArrayList();
          this.notRequestedBytesRanges = new ArrayList();
        }
        Object localObject1 = null;
        Object localObject2 = null;
        Object localObject3 = null;
        paramFileStreamLoadOperation = null;
        String str1;
        String str2;
        Object localObject4;
        Object localObject5;
        label372:
        int j;
        int k;
        if (this.webLocation != null)
        {
          str1 = Utilities.MD5(this.webLocation.url);
          if (this.encryptFile)
          {
            str2 = str1 + ".temp.enc";
            localObject2 = str1 + "." + this.ext + ".enc";
            localObject3 = localObject2;
            localObject4 = localObject1;
            localObject5 = str2;
            if (this.key != null)
            {
              paramFileStreamLoadOperation = str1 + ".iv.enc";
              localObject5 = str2;
              localObject4 = localObject1;
              localObject3 = localObject2;
            }
            this.requestInfos = new ArrayList(this.currentMaxDownloadRequests);
            this.delayedRequestInfos = new ArrayList(this.currentMaxDownloadRequests - 1);
            this.state = 1;
            this.cacheFileFinal = new File(this.storePath, (String)localObject3);
            bool2 = this.cacheFileFinal.exists();
            bool1 = bool2;
            if (bool2)
            {
              bool1 = bool2;
              if (this.totalBytesCount != 0)
              {
                bool1 = bool2;
                if (this.totalBytesCount != this.cacheFileFinal.length())
                {
                  this.cacheFileFinal.delete();
                  bool1 = false;
                }
              }
            }
            if (bool1) {
              break label2538;
            }
            this.cacheFileTemp = new File(this.tempPath, (String)localObject5);
            paramInt = 0;
            j = 0;
            k = 0;
            if (this.encryptFile)
            {
              localObject5 = new File(FileLoader.getInternalCacheDir(), (String)localObject3 + ".key");
              i = j;
            }
          }
        }
        try
        {
          localObject3 = new java/io/RandomAccessFile;
          i = j;
          ((RandomAccessFile)localObject3).<init>((File)localObject5, "rws");
          i = j;
          l = ((File)localObject5).length();
          i = j;
          this.encryptKey = new byte[32];
          i = j;
          this.encryptIv = new byte[16];
          if ((l > 0L) && (l % 48L == 0L))
          {
            i = j;
            ((RandomAccessFile)localObject3).read(this.encryptKey, 0, 32);
            i = j;
            ((RandomAccessFile)localObject3).read(this.encryptIv, 0, 16);
            paramInt = k;
          }
          try
          {
            for (;;)
            {
              ((RandomAccessFile)localObject3).getChannel().close();
              i = paramInt;
              ((RandomAccessFile)localObject3).close();
              if (localObject4 == null) {
                break label1944;
              }
              this.cacheFileParts = new File(this.tempPath, (String)localObject4);
              try
              {
                localObject3 = new java/io/RandomAccessFile;
                ((RandomAccessFile)localObject3).<init>(this.cacheFileParts, "rws");
                this.filePartsStream = ((RandomAccessFile)localObject3);
                l = this.filePartsStream.length();
                if (l % 8L != 4L) {
                  break label1944;
                }
                k = this.filePartsStream.readInt();
                if (k > (l - 4L) / 2L) {
                  break label1944;
                }
                for (i = 0; i < k; i++)
                {
                  int m = this.filePartsStream.readInt();
                  j = this.filePartsStream.readInt();
                  localObject3 = this.notLoadedBytesRanges;
                  localObject5 = new org/telegram/messenger/FileLoadOperation$Range;
                  ((Range)localObject5).<init>(m, j, null);
                  ((ArrayList)localObject3).add(localObject5);
                  localObject3 = this.notRequestedBytesRanges;
                  localObject5 = new org/telegram/messenger/FileLoadOperation$Range;
                  ((Range)localObject5).<init>(m, j, null);
                  ((ArrayList)localObject3).add(localObject5);
                }
                localObject2 = str1 + ".temp";
                str2 = str1 + "." + this.ext;
                localObject3 = str2;
                localObject4 = localObject1;
                localObject5 = localObject2;
                if (this.key == null) {
                  break label372;
                }
                paramFileStreamLoadOperation = str1 + ".iv";
                localObject3 = str2;
                localObject4 = localObject1;
                localObject5 = localObject2;
              }
              catch (Exception localException2)
              {
                FileLog.e(localException2);
              }
              if ((this.location.volume_id != 0L) && (this.location.local_id != 0))
              {
                if ((this.datacenterId == Integer.MIN_VALUE) || (this.location.volume_id == -2147483648L) || (this.datacenterId == 0))
                {
                  onFail(true, 0);
                  bool1 = false;
                  break;
                }
                if (this.encryptFile)
                {
                  localObject2 = this.location.volume_id + "_" + this.location.local_id + ".temp.enc";
                  str2 = this.location.volume_id + "_" + this.location.local_id + "." + this.ext + ".enc";
                  localObject3 = str2;
                  localObject4 = localObject1;
                  localObject5 = localObject2;
                  if (this.key == null) {
                    break label372;
                  }
                  paramFileStreamLoadOperation = this.location.volume_id + "_" + this.location.local_id + ".iv.enc";
                  localObject3 = str2;
                  localObject4 = localObject1;
                  localObject5 = localObject2;
                  break label372;
                }
                str2 = this.location.volume_id + "_" + this.location.local_id + ".temp";
                str1 = this.location.volume_id + "_" + this.location.local_id + "." + this.ext;
                if (this.key != null) {
                  localObject2 = this.location.volume_id + "_" + this.location.local_id + ".iv";
                }
                localObject3 = str1;
                paramFileStreamLoadOperation = (FileStreamLoadOperation)localObject2;
                localObject4 = localObject1;
                localObject5 = str2;
                if (this.notLoadedBytesRanges == null) {
                  break label372;
                }
                localObject4 = this.location.volume_id + "_" + this.location.local_id + ".pt";
                localObject3 = str1;
                paramFileStreamLoadOperation = (FileStreamLoadOperation)localObject2;
                localObject5 = str2;
                break label372;
              }
              if ((this.datacenterId == 0) || (this.location.id == 0L))
              {
                onFail(true, 0);
                bool1 = false;
                break;
              }
              if (this.encryptFile)
              {
                str2 = this.datacenterId + "_" + this.location.id + ".temp.enc";
                localObject2 = this.datacenterId + "_" + this.location.id + this.ext + ".enc";
                localObject3 = localObject2;
                localObject4 = localObject1;
                localObject5 = str2;
                if (this.key == null) {
                  break label372;
                }
                paramFileStreamLoadOperation = this.datacenterId + "_" + this.location.id + ".iv.enc";
                localObject3 = localObject2;
                localObject4 = localObject1;
                localObject5 = str2;
                break label372;
              }
              str1 = this.datacenterId + "_" + this.location.id + ".temp";
              str2 = this.datacenterId + "_" + this.location.id + this.ext;
              localObject2 = localObject3;
              if (this.key != null) {
                localObject2 = this.datacenterId + "_" + this.location.id + ".iv";
              }
              localObject3 = str2;
              paramFileStreamLoadOperation = (FileStreamLoadOperation)localObject2;
              localObject4 = localObject1;
              localObject5 = str1;
              if (this.notLoadedBytesRanges == null) {
                break label372;
              }
              localObject4 = this.datacenterId + "_" + this.location.id + ".pt";
              localObject3 = str2;
              paramFileStreamLoadOperation = (FileStreamLoadOperation)localObject2;
              localObject5 = str1;
              break label372;
              i = j;
              Utilities.random.nextBytes(this.encryptKey);
              i = j;
              Utilities.random.nextBytes(this.encryptIv);
              i = j;
              ((RandomAccessFile)localObject3).write(this.encryptKey);
              i = j;
              ((RandomAccessFile)localObject3).write(this.encryptIv);
              paramInt = 1;
            }
          }
          catch (Exception localException3)
          {
            for (;;)
            {
              i = paramInt;
              FileLog.e(localException3);
            }
          }
        }
        catch (Exception localException1)
        {
          long l;
          for (;;)
          {
            FileLog.e(localException1);
            paramInt = i;
          }
          label1944:
          if (this.cacheFileTemp.exists()) {
            if (paramInt != 0) {
              this.cacheFileTemp.delete();
            }
          }
          while (this.notLoadedBytesRanges != null)
          {
            this.downloadedBytes = this.totalBytesCount;
            k = this.notLoadedBytesRanges.size();
            for (i = 0; i < k; i++)
            {
              Range localRange = (Range)this.notLoadedBytesRanges.get(i);
              this.downloadedBytes -= localRange.end - localRange.start;
            }
            l = this.cacheFileTemp.length();
            if ((paramFileStreamLoadOperation != null) && (l % this.currentDownloadChunkSize != 0L)) {
              this.downloadedBytes = 0;
            }
            for (this.requestedBytesCount = 0;; this.requestedBytesCount = i)
            {
              if ((this.notLoadedBytesRanges == null) || (!this.notLoadedBytesRanges.isEmpty())) {
                break label2171;
              }
              this.notLoadedBytesRanges.add(new Range(this.downloadedBytes, this.totalBytesCount, null));
              this.notRequestedBytesRanges.add(new Range(this.downloadedBytes, this.totalBytesCount, null));
              break;
              i = (int)this.cacheFileTemp.length() / this.currentDownloadChunkSize * this.currentDownloadChunkSize;
              this.downloadedBytes = i;
            }
            label2171:
            continue;
            if ((this.notLoadedBytesRanges != null) && (this.notLoadedBytesRanges.isEmpty()))
            {
              this.notLoadedBytesRanges.add(new Range(0, this.totalBytesCount, null));
              this.notRequestedBytesRanges.add(new Range(0, this.totalBytesCount, null));
            }
          }
          if (BuildVars.LOGS_ENABLED) {
            FileLog.d("start loading file to temp = " + this.cacheFileTemp + " final = " + this.cacheFileFinal);
          }
          if (paramFileStreamLoadOperation != null) {
            this.cacheIvTemp = new File(this.tempPath, paramFileStreamLoadOperation);
          }
          try
          {
            paramFileStreamLoadOperation = new java/io/RandomAccessFile;
            paramFileStreamLoadOperation.<init>(this.cacheIvTemp, "rws");
            this.fiv = paramFileStreamLoadOperation;
            if ((this.downloadedBytes != 0) && (paramInt == 0))
            {
              l = this.cacheIvTemp.length();
              if ((l > 0L) && (l % 32L == 0L)) {
                this.fiv.read(this.iv, 0, 32);
              }
            }
            else if ((this.downloadedBytes != 0) && (this.totalBytesCount > 0))
            {
              copytNotLoadedRanges();
              this.delegate.didChangedLoadProgress(this, Math.min(1.0F, this.downloadedBytes / this.totalBytesCount));
            }
          }
          catch (Exception paramFileStreamLoadOperation)
          {
            try
            {
              for (;;)
              {
                paramFileStreamLoadOperation = new java/io/RandomAccessFile;
                paramFileStreamLoadOperation.<init>(this.cacheFileTemp, "rws");
                this.fileOutputStream = paramFileStreamLoadOperation;
                if (this.downloadedBytes != 0) {
                  this.fileOutputStream.seek(this.downloadedBytes);
                }
                if (this.fileOutputStream != null) {
                  break label2513;
                }
                onFail(true, 0);
                bool1 = false;
                break;
                this.downloadedBytes = 0;
                this.requestedBytesCount = 0;
              }
              paramFileStreamLoadOperation = paramFileStreamLoadOperation;
              FileLog.e(paramFileStreamLoadOperation);
              this.downloadedBytes = 0;
              this.requestedBytesCount = 0;
            }
            catch (Exception paramFileStreamLoadOperation)
            {
              for (;;)
              {
                FileLog.e(paramFileStreamLoadOperation);
              }
              label2513:
              this.started = true;
              Utilities.stageQueue.postRunnable(new Runnable()
              {
                public void run()
                {
                  if ((FileLoadOperation.this.totalBytesCount != 0) && (FileLoadOperation.this.downloadedBytes == FileLoadOperation.this.totalBytesCount)) {}
                  for (;;)
                  {
                    try
                    {
                      FileLoadOperation.this.onFinishLoadingFile(false);
                      return;
                    }
                    catch (Exception localException)
                    {
                      FileLoadOperation.this.onFail(true, 0);
                      continue;
                    }
                    FileLoadOperation.this.startDownloadRequest();
                  }
                }
              });
            }
          }
        }
      }
    }
    for (;;)
    {
      bool1 = true;
      break;
      label2538:
      this.started = true;
      try
      {
        onFinishLoadingFile(false);
      }
      catch (Exception paramFileStreamLoadOperation)
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
  
  public static class Range
  {
    private int end;
    private int start;
    
    private Range(int paramInt1, int paramInt2)
    {
      this.start = paramInt1;
      this.end = paramInt2;
    }
  }
  
  private static class RequestInfo
  {
    private int offset;
    private int requestToken;
    private TLRPC.TL_upload_file response;
    private TLRPC.TL_upload_cdnFile responseCdn;
    private TLRPC.TL_upload_webFile responseWeb;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/FileLoadOperation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */