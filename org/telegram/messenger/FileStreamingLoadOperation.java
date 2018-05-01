package org.telegram.messenger;

import android.net.Uri;
import java.io.EOFException;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.Semaphore;
import org.telegram.messenger.exoplayer2.upstream.DataSource;
import org.telegram.messenger.exoplayer2.upstream.DataSpec;
import org.telegram.messenger.exoplayer2.upstream.TransferListener;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.InputFileLocation;
import org.telegram.tgnet.TLRPC.TL_cdnFileHash;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputDocumentFileLocation;
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
import org.telegram.tgnet.TLRPC.Vector;

public class FileStreamingLoadOperation
  implements DataSource
{
  private static final int bigFileSizeFrom = 1048576;
  private static final int cdnChunkCheckSize = 131072;
  private static final int downloadChunkSize = 32768;
  private static final int downloadChunkSizeBig = 131072;
  private static final int maxDownloadRequests = 4;
  private static final int maxDownloadRequestsBig = 2;
  private static final int stateDownloading = 1;
  private static final int stateFailed = 2;
  private static final int stateFinished = 3;
  private static final int stateIdle = 0;
  private int bytesCountPadding;
  private long bytesRemaining;
  private File cacheFileFinal;
  private File cacheFileTemp;
  private File cacheIvTemp;
  private byte[] cdnCheckBytes;
  private int cdnDatacenterId;
  private HashMap<Integer, TLRPC.TL_cdnFileHash> cdnHashes;
  private byte[] cdnIv;
  private byte[] cdnKey;
  private byte[] cdnToken;
  private int currentAccount;
  private int currentDownloadChunkSize;
  private int currentMaxDownloadRequests;
  private int currentType;
  private int datacenter_id;
  private ArrayList<RequestInfo> delayedRequestInfos;
  private int downloadedBytes;
  private ArrayList<RequestInfo> downloadedInfos;
  private String ext;
  private RandomAccessFile fileOutputStream;
  private RandomAccessFile fileReadStream;
  private int firstPacketOffset;
  private RandomAccessFile fiv;
  private boolean isCdn;
  private byte[] iv;
  private byte[] key;
  private int lastCheckedCdnPart;
  private final TransferListener<? super FileStreamingLoadOperation> listener;
  private TLRPC.InputFileLocation location;
  private int nextDownloadOffset;
  private boolean opened;
  private int renameRetryCount;
  private ArrayList<RequestInfo> requestInfos;
  private boolean requestingCdnOffsets;
  private int requestsCount;
  private boolean reuploadingCdn;
  private Semaphore semaphore;
  private volatile int state = 0;
  private File storePath;
  private File tempPath;
  private int totalBytesCount;
  private Uri uri;
  private TLRPC.TL_inputWebFileLocation webLocation;
  
  public FileStreamingLoadOperation()
  {
    this(null);
  }
  
  public FileStreamingLoadOperation(TransferListener<? super FileStreamingLoadOperation> paramTransferListener)
  {
    this.listener = paramTransferListener;
  }
  
  /* Error */
  private void cleanup()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 209	org/telegram/messenger/FileStreamingLoadOperation:fileOutputStream	Ljava/io/RandomAccessFile;
    //   4: astore_2
    //   5: aload_2
    //   6: ifnull +25 -> 31
    //   9: aload_0
    //   10: getfield 209	org/telegram/messenger/FileStreamingLoadOperation:fileOutputStream	Ljava/io/RandomAccessFile;
    //   13: invokevirtual 215	java/io/RandomAccessFile:getChannel	()Ljava/nio/channels/FileChannel;
    //   16: invokevirtual 220	java/nio/channels/FileChannel:close	()V
    //   19: aload_0
    //   20: getfield 209	org/telegram/messenger/FileStreamingLoadOperation:fileOutputStream	Ljava/io/RandomAccessFile;
    //   23: invokevirtual 221	java/io/RandomAccessFile:close	()V
    //   26: aload_0
    //   27: aconst_null
    //   28: putfield 209	org/telegram/messenger/FileStreamingLoadOperation:fileOutputStream	Ljava/io/RandomAccessFile;
    //   31: aload_0
    //   32: getfield 223	org/telegram/messenger/FileStreamingLoadOperation:fileReadStream	Ljava/io/RandomAccessFile;
    //   35: astore_2
    //   36: aload_2
    //   37: ifnull +25 -> 62
    //   40: aload_0
    //   41: getfield 223	org/telegram/messenger/FileStreamingLoadOperation:fileReadStream	Ljava/io/RandomAccessFile;
    //   44: invokevirtual 215	java/io/RandomAccessFile:getChannel	()Ljava/nio/channels/FileChannel;
    //   47: invokevirtual 220	java/nio/channels/FileChannel:close	()V
    //   50: aload_0
    //   51: getfield 223	org/telegram/messenger/FileStreamingLoadOperation:fileReadStream	Ljava/io/RandomAccessFile;
    //   54: invokevirtual 221	java/io/RandomAccessFile:close	()V
    //   57: aload_0
    //   58: aconst_null
    //   59: putfield 223	org/telegram/messenger/FileStreamingLoadOperation:fileReadStream	Ljava/io/RandomAccessFile;
    //   62: aload_0
    //   63: getfield 225	org/telegram/messenger/FileStreamingLoadOperation:fiv	Ljava/io/RandomAccessFile;
    //   66: ifnull +15 -> 81
    //   69: aload_0
    //   70: getfield 225	org/telegram/messenger/FileStreamingLoadOperation:fiv	Ljava/io/RandomAccessFile;
    //   73: invokevirtual 221	java/io/RandomAccessFile:close	()V
    //   76: aload_0
    //   77: aconst_null
    //   78: putfield 225	org/telegram/messenger/FileStreamingLoadOperation:fiv	Ljava/io/RandomAccessFile;
    //   81: aload_0
    //   82: getfield 118	org/telegram/messenger/FileStreamingLoadOperation:delayedRequestInfos	Ljava/util/ArrayList;
    //   85: ifnull +154 -> 239
    //   88: iconst_0
    //   89: istore_1
    //   90: iload_1
    //   91: aload_0
    //   92: getfield 118	org/telegram/messenger/FileStreamingLoadOperation:delayedRequestInfos	Ljava/util/ArrayList;
    //   95: invokevirtual 231	java/util/ArrayList:size	()I
    //   98: if_icmpge +134 -> 232
    //   101: aload_0
    //   102: getfield 118	org/telegram/messenger/FileStreamingLoadOperation:delayedRequestInfos	Ljava/util/ArrayList;
    //   105: iload_1
    //   106: invokevirtual 235	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   109: checkcast 20	org/telegram/messenger/FileStreamingLoadOperation$RequestInfo
    //   112: astore_2
    //   113: aload_2
    //   114: invokestatic 239	org/telegram/messenger/FileStreamingLoadOperation$RequestInfo:access$200	(Lorg/telegram/messenger/FileStreamingLoadOperation$RequestInfo;)Lorg/telegram/tgnet/TLRPC$TL_upload_file;
    //   117: ifnull +65 -> 182
    //   120: aload_2
    //   121: invokestatic 239	org/telegram/messenger/FileStreamingLoadOperation$RequestInfo:access$200	(Lorg/telegram/messenger/FileStreamingLoadOperation$RequestInfo;)Lorg/telegram/tgnet/TLRPC$TL_upload_file;
    //   124: iconst_0
    //   125: putfield 244	org/telegram/tgnet/TLRPC$TL_upload_file:disableFree	Z
    //   128: aload_2
    //   129: invokestatic 239	org/telegram/messenger/FileStreamingLoadOperation$RequestInfo:access$200	(Lorg/telegram/messenger/FileStreamingLoadOperation$RequestInfo;)Lorg/telegram/tgnet/TLRPC$TL_upload_file;
    //   132: invokevirtual 247	org/telegram/tgnet/TLRPC$TL_upload_file:freeResources	()V
    //   135: iload_1
    //   136: iconst_1
    //   137: iadd
    //   138: istore_1
    //   139: goto -49 -> 90
    //   142: astore_2
    //   143: aload_2
    //   144: invokestatic 253	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   147: goto -128 -> 19
    //   150: astore_2
    //   151: aload_2
    //   152: invokestatic 253	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   155: goto -124 -> 31
    //   158: astore_2
    //   159: aload_2
    //   160: invokestatic 253	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   163: goto -113 -> 50
    //   166: astore_2
    //   167: aload_2
    //   168: invokestatic 253	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   171: goto -109 -> 62
    //   174: astore_2
    //   175: aload_2
    //   176: invokestatic 253	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   179: goto -98 -> 81
    //   182: aload_2
    //   183: invokestatic 257	org/telegram/messenger/FileStreamingLoadOperation$RequestInfo:access$300	(Lorg/telegram/messenger/FileStreamingLoadOperation$RequestInfo;)Lorg/telegram/tgnet/TLRPC$TL_upload_webFile;
    //   186: ifnull +21 -> 207
    //   189: aload_2
    //   190: invokestatic 257	org/telegram/messenger/FileStreamingLoadOperation$RequestInfo:access$300	(Lorg/telegram/messenger/FileStreamingLoadOperation$RequestInfo;)Lorg/telegram/tgnet/TLRPC$TL_upload_webFile;
    //   193: iconst_0
    //   194: putfield 260	org/telegram/tgnet/TLRPC$TL_upload_webFile:disableFree	Z
    //   197: aload_2
    //   198: invokestatic 257	org/telegram/messenger/FileStreamingLoadOperation$RequestInfo:access$300	(Lorg/telegram/messenger/FileStreamingLoadOperation$RequestInfo;)Lorg/telegram/tgnet/TLRPC$TL_upload_webFile;
    //   201: invokevirtual 261	org/telegram/tgnet/TLRPC$TL_upload_webFile:freeResources	()V
    //   204: goto -69 -> 135
    //   207: aload_2
    //   208: invokestatic 265	org/telegram/messenger/FileStreamingLoadOperation$RequestInfo:access$400	(Lorg/telegram/messenger/FileStreamingLoadOperation$RequestInfo;)Lorg/telegram/tgnet/TLRPC$TL_upload_cdnFile;
    //   211: ifnull -76 -> 135
    //   214: aload_2
    //   215: invokestatic 265	org/telegram/messenger/FileStreamingLoadOperation$RequestInfo:access$400	(Lorg/telegram/messenger/FileStreamingLoadOperation$RequestInfo;)Lorg/telegram/tgnet/TLRPC$TL_upload_cdnFile;
    //   218: iconst_0
    //   219: putfield 268	org/telegram/tgnet/TLRPC$TL_upload_cdnFile:disableFree	Z
    //   222: aload_2
    //   223: invokestatic 265	org/telegram/messenger/FileStreamingLoadOperation$RequestInfo:access$400	(Lorg/telegram/messenger/FileStreamingLoadOperation$RequestInfo;)Lorg/telegram/tgnet/TLRPC$TL_upload_cdnFile;
    //   226: invokevirtual 269	org/telegram/tgnet/TLRPC$TL_upload_cdnFile:freeResources	()V
    //   229: goto -94 -> 135
    //   232: aload_0
    //   233: getfield 118	org/telegram/messenger/FileStreamingLoadOperation:delayedRequestInfos	Ljava/util/ArrayList;
    //   236: invokevirtual 272	java/util/ArrayList:clear	()V
    //   239: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	240	0	this	FileStreamingLoadOperation
    //   89	50	1	i	int
    //   4	125	2	localObject	Object
    //   142	2	2	localException1	Exception
    //   150	2	2	localException2	Exception
    //   158	2	2	localException3	Exception
    //   166	2	2	localException4	Exception
    //   174	49	2	localException5	Exception
    // Exception table:
    //   from	to	target	type
    //   9	19	142	java/lang/Exception
    //   0	5	150	java/lang/Exception
    //   19	31	150	java/lang/Exception
    //   143	147	150	java/lang/Exception
    //   40	50	158	java/lang/Exception
    //   31	36	166	java/lang/Exception
    //   50	62	166	java/lang/Exception
    //   159	163	166	java/lang/Exception
    //   62	81	174	java/lang/Exception
  }
  
  private void clearOperaion(RequestInfo paramRequestInfo)
  {
    int i = Integer.MAX_VALUE;
    int j = 0;
    if (j < this.requestInfos.size())
    {
      RequestInfo localRequestInfo = (RequestInfo)this.requestInfos.get(j);
      i = Math.min(localRequestInfo.offset, i);
      if (paramRequestInfo == localRequestInfo) {}
      for (;;)
      {
        j += 1;
        break;
        if (localRequestInfo.requestToken != 0) {
          ConnectionsManager.getInstance(this.currentAccount).cancelRequest(localRequestInfo.requestToken, true);
        }
      }
    }
    this.requestInfos.clear();
    int k = 0;
    j = i;
    i = k;
    if (i < this.delayedRequestInfos.size())
    {
      paramRequestInfo = (RequestInfo)this.delayedRequestInfos.get(i);
      if (paramRequestInfo.response != null)
      {
        paramRequestInfo.response.disableFree = false;
        paramRequestInfo.response.freeResources();
      }
      for (;;)
      {
        j = Math.min(paramRequestInfo.offset, j);
        i += 1;
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
    this.nextDownloadOffset = j;
  }
  
  private void delayRequestInfo(RequestInfo paramRequestInfo)
  {
    if (paramRequestInfo.response != null) {
      paramRequestInfo.response.disableFree = true;
    }
    do
    {
      return;
      if (paramRequestInfo.responseWeb != null)
      {
        paramRequestInfo.responseWeb.disableFree = true;
        return;
      }
    } while (paramRequestInfo.responseCdn == null);
    paramRequestInfo.responseCdn.disableFree = true;
  }
  
  private void onFail(boolean paramBoolean, int paramInt)
  {
    cleanup();
    this.state = 2;
    if (paramBoolean)
    {
      Utilities.stageQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          FileStreamingLoadOperation.this.semaphore.release();
        }
      });
      return;
    }
    this.semaphore.release();
  }
  
  private void onFinishLoadingFile(final boolean paramBoolean)
    throws Exception
  {
    if (this.state != 1) {}
    do
    {
      do
      {
        return;
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
                  FileStreamingLoadOperation.this.onFinishLoadingFile(paramBoolean);
                  return;
                }
                catch (Exception localException)
                {
                  FileStreamingLoadOperation.this.onFail(false, 0);
                }
              }
            }, 200L);
            return;
          }
          this.cacheFileFinal = this.cacheFileTemp;
        }
        if (BuildVars.DEBUG_VERSION) {
          FileLog.e("finished downloading file to " + this.cacheFileFinal);
        }
      } while (!paramBoolean);
      if (this.currentType == 50331648)
      {
        StatsController.getInstance(this.currentAccount).incrementReceivedItemsCount(ConnectionsManager.getCurrentNetworkType(), 3, 1);
        return;
      }
      if (this.currentType == 33554432)
      {
        StatsController.getInstance(this.currentAccount).incrementReceivedItemsCount(ConnectionsManager.getCurrentNetworkType(), 2, 1);
        return;
      }
      if (this.currentType == 16777216)
      {
        StatsController.getInstance(this.currentAccount).incrementReceivedItemsCount(ConnectionsManager.getCurrentNetworkType(), 4, 1);
        return;
      }
    } while (this.currentType != 67108864);
    StatsController.getInstance(this.currentAccount).incrementReceivedItemsCount(ConnectionsManager.getCurrentNetworkType(), 5, 1);
  }
  
  private boolean processRequestResult(RequestInfo paramRequestInfo, TLRPC.TL_error paramTL_error)
  {
    if (this.state != 1) {
      return false;
    }
    this.requestInfos.remove(paramRequestInfo);
    int j;
    int i;
    if (paramTL_error == null)
    {
      int k;
      try
      {
        delayRequestInfo(paramRequestInfo);
        localNativeByteBuffer = paramRequestInfo.bytes;
        if ((localNativeByteBuffer == null) || (localNativeByteBuffer.limit() == 0))
        {
          onFinishLoadingFile(true);
          return false;
        }
        j = localNativeByteBuffer.limit();
        if (this.isCdn)
        {
          k = (this.downloadedBytes + j) / 131072;
          if (this.lastCheckedCdnPart == k) {
            break label1368;
          }
          i = 1;
          i = (k - i) * 131072;
          if (this.cdnHashes == null) {
            break label1373;
          }
          paramTL_error = (TLRPC.TL_cdnFileHash)this.cdnHashes.get(Integer.valueOf(i));
          label121:
          if (paramTL_error == null)
          {
            delayRequestInfo(paramRequestInfo);
            requestFileOffsets(i);
            return true;
          }
        }
        if (paramRequestInfo.responseCdn != null)
        {
          i = paramRequestInfo.offset / 16;
          this.cdnIv[15] = ((byte)(i & 0xFF));
          this.cdnIv[14] = ((byte)(i >> 8 & 0xFF));
          this.cdnIv[13] = ((byte)(i >> 16 & 0xFF));
          this.cdnIv[12] = ((byte)(i >> 24 & 0xFF));
          Utilities.aesCtrDecryption(localNativeByteBuffer.buffer, this.cdnKey, this.cdnIv, 0, localNativeByteBuffer.limit());
        }
        this.downloadedBytes += j;
        if (j != this.currentDownloadChunkSize) {
          break label1378;
        }
        if ((this.totalBytesCount != this.downloadedBytes) && (this.downloadedBytes % this.currentDownloadChunkSize == 0)) {
          break label1383;
        }
        if (this.totalBytesCount <= 0) {
          break label1378;
        }
        if (this.totalBytesCount > this.downloadedBytes) {
          break label1383;
        }
      }
      catch (Exception paramRequestInfo)
      {
        NativeByteBuffer localNativeByteBuffer;
        label299:
        onFail(false, 0);
        FileLog.e(paramRequestInfo);
      }
      if (this.key != null)
      {
        Utilities.aesIgeEncryption(localNativeByteBuffer.buffer, this.key, this.iv, false, true, 0, localNativeByteBuffer.limit());
        if ((i != 0) && (this.bytesCountPadding != 0)) {
          localNativeByteBuffer.limit(localNativeByteBuffer.limit() - this.bytesCountPadding);
        }
      }
      this.fileOutputStream.getChannel().write(localNativeByteBuffer.buffer);
      RequestInfo.access$002(paramRequestInfo, paramRequestInfo.bytes.limit() - this.firstPacketOffset);
      paramRequestInfo.bytes.position(this.firstPacketOffset);
      this.downloadedInfos.add(paramRequestInfo);
      if (this.isCdn)
      {
        k = this.downloadedBytes / 131072;
        if ((k != this.lastCheckedCdnPart) || (i != 0))
        {
          this.fileOutputStream.getFD().sync();
          if (this.lastCheckedCdnPart == k) {
            break label1388;
          }
          j = 1;
          label460:
          j = (k - j) * 131072;
          paramRequestInfo = (TLRPC.TL_cdnFileHash)this.cdnHashes.get(Integer.valueOf(j));
          if (this.fileReadStream == null)
          {
            this.cdnCheckBytes = new byte[131072];
            this.fileReadStream = new RandomAccessFile(this.cacheFileTemp, "r");
            if (j != 0) {
              this.fileReadStream.seek(j);
            }
          }
          if (this.lastCheckedCdnPart != k)
          {
            j = 131072;
            this.fileReadStream.readFully(this.cdnCheckBytes, 0, j);
            if (Arrays.equals(Utilities.computeSHA256(this.cdnCheckBytes, 0, j), paramRequestInfo.hash)) {
              break label806;
            }
            if (this.location == null) {
              break label725;
            }
            FileLog.e("invalid cdn hash " + this.location + " id = " + this.location.id + " local_id = " + this.location.local_id + " access_hash = " + this.location.access_hash + " volume_id = " + this.location.volume_id + " secret = " + this.location.secret);
          }
          for (;;)
          {
            onFail(false, 0);
            this.cacheFileTemp.delete();
            return false;
            j = this.downloadedBytes - 131072 * k;
            break;
            label725:
            if (this.webLocation != null) {
              FileLog.e("invalid cdn hash  " + this.webLocation + " id = " + this.webLocation.url + " access_hash = " + this.webLocation.access_hash);
            }
          }
          label804:
          return false;
          label806:
          this.lastCheckedCdnPart = k;
        }
      }
      if (this.fiv != null)
      {
        this.fiv.seek(0L);
        this.fiv.write(this.iv);
      }
      if ((this.totalBytesCount <= 0) || (this.state != 1)) {
        break label1394;
      }
      break label1394;
    }
    for (;;)
    {
      if (j < this.delayedRequestInfos.size())
      {
        paramRequestInfo = (RequestInfo)this.delayedRequestInfos.get(j);
        if (this.downloadedBytes != paramRequestInfo.offset) {
          break label1009;
        }
        this.delayedRequestInfos.remove(j);
        if (!processRequestResult(paramRequestInfo, null))
        {
          if (paramRequestInfo.response == null) {
            break label959;
          }
          paramRequestInfo.response.disableFree = false;
          paramRequestInfo.response.freeResources();
        }
      }
      for (;;)
      {
        if (i != 0) {
          onFinishLoadingFile(true);
        }
        if (this.semaphore == null) {
          break;
        }
        this.semaphore.release();
        break;
        label959:
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
      label1009:
      j += 1;
      continue;
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
        }
        if (paramRequestInfo == null)
        {
          onFail(false, 0);
          break label804;
        }
        break label804;
      }
      if (paramTL_error.text.contains("OFFSET_INVALID"))
      {
        if (this.downloadedBytes % this.currentDownloadChunkSize == 0)
        {
          try
          {
            onFinishLoadingFile(true);
          }
          catch (Exception paramRequestInfo)
          {
            FileLog.e(paramRequestInfo);
            onFail(false, 0);
          }
          break label804;
        }
        onFail(false, 0);
        break label804;
      }
      if (paramTL_error.text.contains("RETRY_LIMIT"))
      {
        onFail(false, 2);
        break label804;
      }
      if (this.location != null) {
        FileLog.e("" + this.location + " id = " + this.location.id + " local_id = " + this.location.local_id + " access_hash = " + this.location.access_hash + " volume_id = " + this.location.volume_id + " secret = " + this.location.secret);
      }
      for (;;)
      {
        onFail(false, 0);
        break;
        if (this.webLocation != null) {
          FileLog.e("" + this.webLocation + " id = " + this.webLocation.url + " access_hash = " + this.webLocation.access_hash);
        }
      }
      label1368:
      i = 0;
      break;
      label1373:
      paramTL_error = null;
      break label121;
      label1378:
      i = 1;
      break label299;
      label1383:
      i = 0;
      break label299;
      label1388:
      j = 0;
      break label460;
      label1394:
      j = 0;
    }
  }
  
  private void requestFileOffsets(int paramInt)
  {
    if (this.requestingCdnOffsets) {
      return;
    }
    this.requestingCdnOffsets = true;
    TLRPC.TL_upload_getCdnFileHashes localTL_upload_getCdnFileHashes = new TLRPC.TL_upload_getCdnFileHashes();
    localTL_upload_getCdnFileHashes.file_token = this.cdnToken;
    localTL_upload_getCdnFileHashes.offset = paramInt;
    ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_upload_getCdnFileHashes, new RequestDelegate()
    {
      public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
      {
        if (paramAnonymousTL_error != null) {
          FileStreamingLoadOperation.this.onFail(false, 0);
        }
        label257:
        for (;;)
        {
          return;
          FileStreamingLoadOperation.access$802(FileStreamingLoadOperation.this, false);
          paramAnonymousTLObject = (TLRPC.Vector)paramAnonymousTLObject;
          if (!paramAnonymousTLObject.objects.isEmpty())
          {
            if (FileStreamingLoadOperation.this.cdnHashes == null) {
              FileStreamingLoadOperation.access$902(FileStreamingLoadOperation.this, new HashMap());
            }
            i = 0;
            while (i < paramAnonymousTLObject.objects.size())
            {
              paramAnonymousTL_error = (TLRPC.TL_cdnFileHash)paramAnonymousTLObject.objects.get(i);
              FileStreamingLoadOperation.this.cdnHashes.put(Integer.valueOf(paramAnonymousTL_error.offset), paramAnonymousTL_error);
              i += 1;
            }
          }
          int i = 0;
          for (;;)
          {
            if (i >= FileStreamingLoadOperation.this.delayedRequestInfos.size()) {
              break label257;
            }
            paramAnonymousTLObject = (FileStreamingLoadOperation.RequestInfo)FileStreamingLoadOperation.this.delayedRequestInfos.get(i);
            if (FileStreamingLoadOperation.this.downloadedBytes == FileStreamingLoadOperation.RequestInfo.access$500(paramAnonymousTLObject))
            {
              FileStreamingLoadOperation.this.delayedRequestInfos.remove(i);
              if (FileStreamingLoadOperation.this.processRequestResult(paramAnonymousTLObject, null)) {
                break;
              }
              if (FileStreamingLoadOperation.RequestInfo.access$200(paramAnonymousTLObject) != null)
              {
                FileStreamingLoadOperation.RequestInfo.access$200(paramAnonymousTLObject).disableFree = false;
                FileStreamingLoadOperation.RequestInfo.access$200(paramAnonymousTLObject).freeResources();
                return;
              }
              if (FileStreamingLoadOperation.RequestInfo.access$300(paramAnonymousTLObject) != null)
              {
                FileStreamingLoadOperation.RequestInfo.access$300(paramAnonymousTLObject).disableFree = false;
                FileStreamingLoadOperation.RequestInfo.access$300(paramAnonymousTLObject).freeResources();
                return;
              }
              if (FileStreamingLoadOperation.RequestInfo.access$400(paramAnonymousTLObject) == null) {
                break;
              }
              FileStreamingLoadOperation.RequestInfo.access$400(paramAnonymousTLObject).disableFree = false;
              FileStreamingLoadOperation.RequestInfo.access$400(paramAnonymousTLObject).freeResources();
              return;
            }
            i += 1;
          }
        }
      }
    }, null, null, 0, this.datacenter_id, 1, true);
  }
  
  private void startDownloadRequest(int paramInt)
  {
    int j = 0;
    if ((j >= paramInt) || ((this.totalBytesCount > 0) && (this.nextDownloadOffset >= this.totalBytesCount))) {
      return;
    }
    boolean bool;
    label66:
    int k;
    label78:
    int m;
    final Object localObject;
    label133:
    final RequestInfo localRequestInfo;
    ConnectionsManager localConnectionsManager;
    RequestDelegate local5;
    if ((this.totalBytesCount <= 0) || (j == paramInt - 1) || ((this.totalBytesCount > 0) && (this.nextDownloadOffset + this.currentDownloadChunkSize >= this.totalBytesCount)))
    {
      bool = true;
      if (this.requestsCount % 2 != 0) {
        break label242;
      }
      k = 2;
      m = 34;
      if (!this.isCdn) {
        break label250;
      }
      localObject = new TLRPC.TL_upload_getCdnFile();
      ((TLRPC.TL_upload_getCdnFile)localObject).file_token = this.cdnToken;
      i = this.nextDownloadOffset;
      ((TLRPC.TL_upload_getCdnFile)localObject).offset = i;
      ((TLRPC.TL_upload_getCdnFile)localObject).limit = this.currentDownloadChunkSize;
      m = 0x22 | 0x1;
      localRequestInfo = new RequestInfo(null);
      this.requestInfos.add(localRequestInfo);
      RequestInfo.access$502(localRequestInfo, i);
      localConnectionsManager = ConnectionsManager.getInstance(this.currentAccount);
      local5 = new RequestDelegate()
      {
        public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
        {
          if (!FileStreamingLoadOperation.this.requestInfos.contains(localRequestInfo)) {}
          do
          {
            return;
            if ((paramAnonymousTL_error != null) && ((localObject instanceof TLRPC.TL_upload_getCdnFile)) && (paramAnonymousTL_error.text.equals("FILE_TOKEN_INVALID")))
            {
              FileStreamingLoadOperation.access$1902(FileStreamingLoadOperation.this, false);
              FileStreamingLoadOperation.this.clearOperaion(localRequestInfo);
              FileStreamingLoadOperation.this.startDownloadRequest(1);
              return;
            }
            if ((paramAnonymousTLObject instanceof TLRPC.TL_upload_fileCdnRedirect))
            {
              paramAnonymousTLObject = (TLRPC.TL_upload_fileCdnRedirect)paramAnonymousTLObject;
              if (!paramAnonymousTLObject.cdn_file_hashes.isEmpty())
              {
                if (FileStreamingLoadOperation.this.cdnHashes == null) {
                  FileStreamingLoadOperation.access$902(FileStreamingLoadOperation.this, new HashMap());
                }
                int i = 0;
                while (i < paramAnonymousTLObject.cdn_file_hashes.size())
                {
                  paramAnonymousTL_error = (TLRPC.TL_cdnFileHash)paramAnonymousTLObject.cdn_file_hashes.get(i);
                  FileStreamingLoadOperation.this.cdnHashes.put(Integer.valueOf(paramAnonymousTL_error.offset), paramAnonymousTL_error);
                  i += 1;
                }
              }
              if ((paramAnonymousTLObject.encryption_iv == null) || (paramAnonymousTLObject.encryption_key == null) || (paramAnonymousTLObject.encryption_iv.length != 16) || (paramAnonymousTLObject.encryption_key.length != 32))
              {
                paramAnonymousTLObject = new TLRPC.TL_error();
                paramAnonymousTLObject.text = "bad redirect response";
                paramAnonymousTLObject.code = 400;
                FileStreamingLoadOperation.this.processRequestResult(localRequestInfo, paramAnonymousTLObject);
                return;
              }
              FileStreamingLoadOperation.access$1902(FileStreamingLoadOperation.this, true);
              FileStreamingLoadOperation.access$2202(FileStreamingLoadOperation.this, paramAnonymousTLObject.dc_id);
              FileStreamingLoadOperation.access$2302(FileStreamingLoadOperation.this, paramAnonymousTLObject.encryption_iv);
              FileStreamingLoadOperation.access$2402(FileStreamingLoadOperation.this, paramAnonymousTLObject.encryption_key);
              FileStreamingLoadOperation.access$2502(FileStreamingLoadOperation.this, paramAnonymousTLObject.file_token);
              FileStreamingLoadOperation.this.clearOperaion(localRequestInfo);
              FileStreamingLoadOperation.this.startDownloadRequest(1);
              return;
            }
            if (!(paramAnonymousTLObject instanceof TLRPC.TL_upload_cdnFileReuploadNeeded)) {
              break;
            }
          } while (FileStreamingLoadOperation.this.reuploadingCdn);
          FileStreamingLoadOperation.this.clearOperaion(localRequestInfo);
          FileStreamingLoadOperation.access$2602(FileStreamingLoadOperation.this, true);
          paramAnonymousTLObject = (TLRPC.TL_upload_cdnFileReuploadNeeded)paramAnonymousTLObject;
          paramAnonymousTL_error = new TLRPC.TL_upload_reuploadCdnFile();
          paramAnonymousTL_error.file_token = FileStreamingLoadOperation.this.cdnToken;
          paramAnonymousTL_error.request_token = paramAnonymousTLObject.request_token;
          ConnectionsManager.getInstance(FileStreamingLoadOperation.this.currentAccount).sendRequest(paramAnonymousTL_error, new RequestDelegate()
          {
            public void run(TLObject paramAnonymous2TLObject, TLRPC.TL_error paramAnonymous2TL_error)
            {
              FileStreamingLoadOperation.access$2602(FileStreamingLoadOperation.this, false);
              if (paramAnonymous2TL_error == null)
              {
                paramAnonymous2TLObject = (TLRPC.Vector)paramAnonymous2TLObject;
                if (!paramAnonymous2TLObject.objects.isEmpty())
                {
                  if (FileStreamingLoadOperation.this.cdnHashes == null) {
                    FileStreamingLoadOperation.access$902(FileStreamingLoadOperation.this, new HashMap());
                  }
                  int i = 0;
                  while (i < paramAnonymous2TLObject.objects.size())
                  {
                    paramAnonymous2TL_error = (TLRPC.TL_cdnFileHash)paramAnonymous2TLObject.objects.get(i);
                    FileStreamingLoadOperation.this.cdnHashes.put(Integer.valueOf(paramAnonymous2TL_error.offset), paramAnonymous2TL_error);
                    i += 1;
                  }
                }
                FileStreamingLoadOperation.this.startDownloadRequest(1);
                return;
              }
              if ((paramAnonymous2TL_error.text.equals("FILE_TOKEN_INVALID")) || (paramAnonymous2TL_error.text.equals("REQUEST_TOKEN_INVALID")))
              {
                FileStreamingLoadOperation.access$1902(FileStreamingLoadOperation.this, false);
                FileStreamingLoadOperation.this.clearOperaion(FileStreamingLoadOperation.5.this.val$requestInfo);
                FileStreamingLoadOperation.this.startDownloadRequest(1);
                return;
              }
              FileStreamingLoadOperation.this.onFail(false, 0);
            }
          }, null, null, 0, FileStreamingLoadOperation.this.datacenter_id, 1, true);
          return;
          if ((paramAnonymousTLObject instanceof TLRPC.TL_upload_file))
          {
            FileStreamingLoadOperation.RequestInfo.access$202(localRequestInfo, (TLRPC.TL_upload_file)paramAnonymousTLObject);
            FileStreamingLoadOperation.RequestInfo.access$102(localRequestInfo, FileStreamingLoadOperation.RequestInfo.access$200(localRequestInfo).bytes);
            if (paramAnonymousTLObject != null)
            {
              if (FileStreamingLoadOperation.this.currentType != 50331648) {
                break label594;
              }
              StatsController.getInstance(FileStreamingLoadOperation.this.currentAccount).incrementReceivedBytesCount(paramAnonymousTLObject.networkType, 3, paramAnonymousTLObject.getObjectSize() + 4);
            }
          }
          for (;;)
          {
            FileStreamingLoadOperation.this.processRequestResult(localRequestInfo, paramAnonymousTL_error);
            return;
            if ((paramAnonymousTLObject instanceof TLRPC.TL_upload_webFile))
            {
              FileStreamingLoadOperation.RequestInfo.access$302(localRequestInfo, (TLRPC.TL_upload_webFile)paramAnonymousTLObject);
              FileStreamingLoadOperation.RequestInfo.access$102(localRequestInfo, FileStreamingLoadOperation.RequestInfo.access$300(localRequestInfo).bytes);
              break;
            }
            if (!(paramAnonymousTLObject instanceof TLRPC.TL_upload_cdnFile)) {
              break;
            }
            FileStreamingLoadOperation.RequestInfo.access$402(localRequestInfo, (TLRPC.TL_upload_cdnFile)paramAnonymousTLObject);
            FileStreamingLoadOperation.RequestInfo.access$102(localRequestInfo, FileStreamingLoadOperation.RequestInfo.access$400(localRequestInfo).bytes);
            break;
            label594:
            if (FileStreamingLoadOperation.this.currentType == 33554432) {
              StatsController.getInstance(FileStreamingLoadOperation.this.currentAccount).incrementReceivedBytesCount(paramAnonymousTLObject.networkType, 2, paramAnonymousTLObject.getObjectSize() + 4);
            } else if (FileStreamingLoadOperation.this.currentType == 16777216) {
              StatsController.getInstance(FileStreamingLoadOperation.this.currentAccount).incrementReceivedBytesCount(paramAnonymousTLObject.networkType, 4, paramAnonymousTLObject.getObjectSize() + 4);
            } else if (FileStreamingLoadOperation.this.currentType == 67108864) {
              StatsController.getInstance(FileStreamingLoadOperation.this.currentAccount).incrementReceivedBytesCount(paramAnonymousTLObject.networkType, 5, paramAnonymousTLObject.getObjectSize() + 4);
            }
          }
        }
      };
      if (!this.isCdn) {
        break label339;
      }
    }
    label242:
    label250:
    label339:
    for (int i = this.cdnDatacenterId;; i = this.datacenter_id)
    {
      RequestInfo.access$602(localRequestInfo, localConnectionsManager.sendRequest((TLObject)localObject, local5, null, null, m, i, k, bool));
      this.requestsCount += 1;
      j += 1;
      break;
      bool = false;
      break label66;
      k = 65538;
      break label78;
      if (this.webLocation != null)
      {
        localObject = new TLRPC.TL_upload_getWebFile();
        ((TLRPC.TL_upload_getWebFile)localObject).location = this.webLocation;
        i = this.nextDownloadOffset;
        ((TLRPC.TL_upload_getWebFile)localObject).offset = i;
        ((TLRPC.TL_upload_getWebFile)localObject).limit = this.currentDownloadChunkSize;
        break label133;
      }
      localObject = new TLRPC.TL_upload_getFile();
      ((TLRPC.TL_upload_getFile)localObject).location = this.location;
      i = this.nextDownloadOffset;
      ((TLRPC.TL_upload_getFile)localObject).offset = i;
      ((TLRPC.TL_upload_getFile)localObject).limit = this.currentDownloadChunkSize;
      break label133;
    }
  }
  
  public void cancel()
  {
    Utilities.stageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        if ((FileStreamingLoadOperation.this.state == 3) || (FileStreamingLoadOperation.this.state == 2)) {
          return;
        }
        if (FileStreamingLoadOperation.this.requestInfos != null)
        {
          int i = 0;
          while (i < FileStreamingLoadOperation.this.requestInfos.size())
          {
            FileStreamingLoadOperation.RequestInfo localRequestInfo = (FileStreamingLoadOperation.RequestInfo)FileStreamingLoadOperation.this.requestInfos.get(i);
            if (FileStreamingLoadOperation.RequestInfo.access$600(localRequestInfo) != 0) {
              ConnectionsManager.getInstance(FileStreamingLoadOperation.this.currentAccount).cancelRequest(FileStreamingLoadOperation.RequestInfo.access$600(localRequestInfo), true);
            }
            i += 1;
          }
        }
        FileStreamingLoadOperation.this.onFail(false, 1);
      }
    });
  }
  
  public void close()
  {
    cleanup();
    this.semaphore.release();
    this.uri = null;
    if (this.opened)
    {
      this.opened = false;
      if (this.listener != null) {
        this.listener.onTransferEnd(this);
      }
    }
  }
  
  public int getCurrentType()
  {
    return this.currentType;
  }
  
  public String getFileName()
  {
    if (this.location != null) {
      return this.location.volume_id + "_" + this.location.local_id + "." + this.ext;
    }
    return Utilities.MD5(this.webLocation.url) + "." + this.ext;
  }
  
  public Uri getUri()
  {
    return this.uri;
  }
  
  public long open(DataSpec paramDataSpec)
    throws IOException
  {
    int i = -1;
    this.uri = paramDataSpec.uri;
    this.location = new TLRPC.TL_inputDocumentFileLocation();
    this.location.id = Utilities.parseLong(this.uri.getQueryParameter("id")).longValue();
    this.location.access_hash = Utilities.parseLong(this.uri.getQueryParameter("hash")).longValue();
    this.datacenter_id = Utilities.parseInt(this.uri.getQueryParameter("dc")).intValue();
    this.totalBytesCount = Utilities.parseInt(this.uri.getQueryParameter("size")).intValue();
    this.currentAccount = Utilities.parseInt(this.uri.getQueryParameter("account")).intValue();
    this.ext = this.uri.getHost();
    String str = this.uri.getQueryParameter("mime");
    int j;
    if (this.ext != null)
    {
      j = this.ext.lastIndexOf('.');
      if (j != -1) {}
    }
    else
    {
      this.ext = "";
      if (!"audio/ogg".equals(str)) {
        break label336;
      }
      this.currentType = 50331648;
      label197:
      if (this.ext.length() <= 1) {
        if (str == null) {
          break label419;
        }
      }
      switch (str.hashCode())
      {
      default: 
        switch (i)
        {
        default: 
          label244:
          this.ext = "";
          label275:
          if (paramDataSpec.length != -1L) {}
          break;
        }
        break;
      }
    }
    for (long l = this.totalBytesCount - paramDataSpec.position;; l = paramDataSpec.length)
    {
      this.bytesRemaining = l;
      if (this.bytesRemaining >= 0L) {
        break label438;
      }
      throw new EOFException();
      this.ext = this.ext.substring(j);
      break;
      label336:
      if ("video/mp4".equals(str))
      {
        this.currentType = 33554432;
        break label197;
      }
      this.currentType = 67108864;
      break label197;
      if (!str.equals("video/mp4")) {
        break label244;
      }
      i = 0;
      break label244;
      if (!str.equals("audio/ogg")) {
        break label244;
      }
      i = 1;
      break label244;
      this.ext = ".mp4";
      break label275;
      this.ext = ".ogg";
      break label275;
      label419:
      this.ext = "";
      break label275;
    }
    label438:
    start();
    this.nextDownloadOffset = ((int)paramDataSpec.position / this.currentDownloadChunkSize * this.currentDownloadChunkSize);
    this.firstPacketOffset = ((int)(paramDataSpec.position - this.nextDownloadOffset));
    this.opened = true;
    if (this.listener != null) {
      this.listener.onTransferStart(this, paramDataSpec);
    }
    return this.bytesRemaining;
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    int i = 0;
    if (paramInt2 == 0)
    {
      paramInt1 = i;
      return paramInt1;
    }
    if (this.bytesRemaining == 0L) {
      return -1;
    }
    for (;;)
    {
      RequestInfo localRequestInfo;
      try
      {
        if (!this.downloadedInfos.isEmpty()) {
          localRequestInfo = (RequestInfo)this.downloadedInfos.get(0);
        }
        if (this.downloadedInfos.isEmpty())
        {
          FileLog.d("request offset " + paramInt1 + " and len " + paramInt2);
          startDownloadRequest(1);
          this.semaphore = new Semaphore(0);
          this.semaphore.acquire();
        }
        localRequestInfo = (RequestInfo)this.downloadedInfos.get(0);
        NativeByteBuffer localNativeByteBuffer = localRequestInfo.bytes;
        paramInt2 = Math.min(localRequestInfo.remainingBytes, paramInt2);
        localNativeByteBuffer.readBytes(paramArrayOfByte, paramInt1, paramInt2, false);
        RequestInfo.access$002(localRequestInfo, localRequestInfo.remainingBytes - paramInt2);
        if (localRequestInfo.remainingBytes == 0)
        {
          this.firstPacketOffset = 0;
          this.nextDownloadOffset += this.currentDownloadChunkSize;
          if (localRequestInfo.response != null)
          {
            localRequestInfo.response.disableFree = false;
            localRequestInfo.response.freeResources();
            this.downloadedInfos.clear();
          }
        }
        else
        {
          paramInt1 = paramInt2;
          if (paramInt2 <= 0) {
            break;
          }
          this.bytesRemaining -= paramInt2;
          paramInt1 = paramInt2;
          if (this.listener == null) {
            break;
          }
          this.listener.onBytesTransferred(this, paramInt2);
          return paramInt2;
        }
        if (localRequestInfo.responseWeb != null)
        {
          localRequestInfo.responseWeb.disableFree = false;
          localRequestInfo.responseWeb.freeResources();
          continue;
        }
        if (localRequestInfo.responseCdn == null) {
          continue;
        }
      }
      catch (Exception paramArrayOfByte)
      {
        throw new IOException(paramArrayOfByte);
      }
      localRequestInfo.responseCdn.disableFree = false;
      localRequestInfo.responseCdn.freeResources();
    }
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
    if ((this.location == null) && (this.webLocation == null))
    {
      onFail(true, 0);
      return false;
    }
    String str1 = null;
    String str4;
    String str5;
    String str2;
    String str3;
    if (this.webLocation != null)
    {
      String str6 = Utilities.MD5(this.webLocation.url);
      str4 = str6 + ".temp";
      str5 = str6 + "." + this.ext;
      str2 = str5;
      str3 = str4;
      if (this.key != null)
      {
        str1 = str6 + ".iv";
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
        label164:
        this.currentDownloadChunkSize = i;
        if (this.totalBytesCount < 1048576) {
          break label856;
        }
        i = 2;
        label180:
        this.currentMaxDownloadRequests = i;
        this.requestInfos = new ArrayList(this.currentMaxDownloadRequests);
        this.delayedRequestInfos = new ArrayList(this.currentMaxDownloadRequests - 1);
        this.downloadedInfos = new ArrayList();
        this.state = 1;
        this.cacheFileFinal = new File(this.storePath, str2);
        this.cacheFileTemp = new File(this.tempPath, str3);
        if (BuildVars.DEBUG_VERSION) {
          FileLog.d("start loading file to temp = " + this.cacheFileTemp + " final = " + this.cacheFileFinal);
        }
        if (str1 != null) {
          this.cacheIvTemp = new File(this.tempPath, str1);
        }
      }
      try
      {
        this.fiv = new RandomAccessFile(this.cacheIvTemp, "rws");
        if (0 == 0)
        {
          long l = this.cacheIvTemp.length();
          if ((l <= 0L) || (l % 32L != 0L)) {
            break label861;
          }
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
              break label894;
            }
            onFail(true, 0);
            return false;
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
              if (this.key == null) {
                break;
              }
              str1 = this.location.volume_id + "_" + this.location.local_id + ".iv";
              str2 = str5;
              str3 = str4;
              break;
            }
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
            break label164;
            label856:
            i = 4;
            break label180;
            label861:
            this.downloadedBytes = 0;
            continue;
            localException1 = localException1;
            FileLog.e(localException1);
            this.downloadedBytes = 0;
          }
        }
        catch (Exception localException2)
        {
          for (;;)
          {
            FileLog.e(localException2);
          }
        }
      }
    }
    label894:
    return true;
  }
  
  private static class RequestInfo
  {
    private NativeByteBuffer bytes;
    private int offset;
    private int remainingBytes;
    private int requestToken;
    private TLRPC.TL_upload_file response;
    private TLRPC.TL_upload_cdnFile responseCdn;
    private TLRPC.TL_upload_webFile responseWeb;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/FileStreamingLoadOperation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */