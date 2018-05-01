package org.telegram.messenger;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.util.SparseArray;
import android.util.SparseIntArray;
import java.io.File;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.InputEncryptedFile;
import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.TL_boolTrue;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputEncryptedFileBigUploaded;
import org.telegram.tgnet.TLRPC.TL_inputEncryptedFileUploaded;
import org.telegram.tgnet.TLRPC.TL_inputFile;
import org.telegram.tgnet.TLRPC.TL_inputFileBig;
import org.telegram.tgnet.TLRPC.TL_upload_saveBigFilePart;
import org.telegram.tgnet.TLRPC.TL_upload_saveFilePart;
import org.telegram.tgnet.WriteToSocketDelegate;

public class FileUploadOperation
{
  private static final int initialRequestsCount = 8;
  private static final int maxUploadingKBytes = 2048;
  private static final int minUploadChunkSize = 128;
  private long availableSize;
  private SparseArray<UploadCachedResult> cachedResults = new SparseArray();
  private int currentAccount;
  private long currentFileId;
  private int currentPartNum;
  private int currentType;
  private int currentUploadRequetsCount;
  private int currentUploadingBytes;
  private FileUploadOperationDelegate delegate;
  private int estimatedSize;
  private String fileKey;
  private int fingerprint;
  private ArrayList<byte[]> freeRequestIvs;
  private boolean isBigFile;
  private boolean isEncrypted;
  private boolean isLastPart;
  private byte[] iv;
  private byte[] ivChange;
  private byte[] key;
  private int lastSavedPartNum;
  private int maxRequestsCount;
  private boolean nextPartFirst;
  private SharedPreferences preferences;
  private byte[] readBuffer;
  private long readBytesCount;
  private int requestNum;
  private SparseIntArray requestTokens = new SparseIntArray();
  private int saveInfoTimes;
  private boolean started;
  private int state;
  private RandomAccessFile stream;
  private long totalFileSize;
  private int totalPartsCount;
  private int uploadChunkSize = 65536;
  private boolean uploadFirstPartLater;
  private int uploadStartTime;
  private long uploadedBytesCount;
  private String uploadingFilePath;
  
  public FileUploadOperation(int paramInt1, String paramString, boolean paramBoolean, int paramInt2, int paramInt3)
  {
    this.currentAccount = paramInt1;
    this.uploadingFilePath = paramString;
    this.isEncrypted = paramBoolean;
    this.estimatedSize = paramInt2;
    this.currentType = paramInt3;
    if ((paramInt2 != 0) && (!this.isEncrypted)) {}
    for (paramBoolean = true;; paramBoolean = false)
    {
      this.uploadFirstPartLater = paramBoolean;
      return;
    }
  }
  
  private void calcTotalPartsCount()
  {
    if (this.uploadFirstPartLater) {
      if (this.isBigFile) {
        this.totalPartsCount = ((int)(this.totalFileSize - this.uploadChunkSize + this.uploadChunkSize - 1L) / this.uploadChunkSize + 1);
      }
    }
    for (;;)
    {
      return;
      this.totalPartsCount = ((int)(this.totalFileSize - 1024L + this.uploadChunkSize - 1L) / this.uploadChunkSize + 1);
      continue;
      this.totalPartsCount = ((int)(this.totalFileSize + this.uploadChunkSize - 1L) / this.uploadChunkSize);
    }
  }
  
  private void cleanup()
  {
    if (this.preferences == null) {
      this.preferences = ApplicationLoader.applicationContext.getSharedPreferences("uploadinfo", 0);
    }
    this.preferences.edit().remove(this.fileKey + "_time").remove(this.fileKey + "_size").remove(this.fileKey + "_uploaded").remove(this.fileKey + "_id").remove(this.fileKey + "_iv").remove(this.fileKey + "_key").remove(this.fileKey + "_ivc").commit();
    try
    {
      if (this.stream != null)
      {
        this.stream.close();
        this.stream = null;
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
  
  private void startUploadRequest()
  {
    if (this.state != 1) {}
    Object localObject3;
    final int i;
    label202:
    final long l;
    final int j;
    final int k;
    Object localObject4;
    label712:
    label743:
    int n;
    label939:
    label945:
    label951:
    label1017:
    label1118:
    label1317:
    label1332:
    label1337:
    label1342:
    label1403:
    label1408:
    do
    {
      for (;;)
      {
        return;
        try
        {
          this.started = true;
          if (this.stream != null) {
            break label1408;
          }
          Object localObject1 = new java/io/File;
          ((File)localObject1).<init>(this.uploadingFilePath);
          if (!AndroidUtilities.isInternalUri(Uri.fromFile((File)localObject1))) {
            break;
          }
          localObject1 = new java/lang/Exception;
          ((Exception)localObject1).<init>("trying to upload internal file");
          throw ((Throwable)localObject1);
        }
        catch (Exception localException1)
        {
          FileLog.e(localException1);
          this.state = 4;
          this.delegate.didFailedUploadingFile(this);
          cleanup();
        }
      }
      localObject3 = new java/io/RandomAccessFile;
      ((RandomAccessFile)localObject3).<init>(localException1, "r");
      this.stream = ((RandomAccessFile)localObject3);
      if (this.estimatedSize != 0) {}
      for (this.totalFileSize = this.estimatedSize;; this.totalFileSize = localException1.length())
      {
        if (this.totalFileSize > 10485760L) {
          this.isBigFile = true;
        }
        this.uploadChunkSize = ((int)Math.max(128L, (this.totalFileSize + 3072000L - 1L) / 3072000L));
        if (1024 % this.uploadChunkSize == 0) {
          break label202;
        }
        i = 64;
        while (this.uploadChunkSize > i) {
          i *= 2;
        }
      }
      this.uploadChunkSize = i;
      this.maxRequestsCount = (2048 / this.uploadChunkSize);
      if (this.isEncrypted)
      {
        localObject2 = new java/util/ArrayList;
        ((ArrayList)localObject2).<init>(this.maxRequestsCount);
        this.freeRequestIvs = ((ArrayList)localObject2);
        for (i = 0; i < this.maxRequestsCount; i++) {
          this.freeRequestIvs.add(new byte[32]);
        }
      }
      this.uploadChunkSize *= 1024;
      calcTotalPartsCount();
      this.readBuffer = new byte[this.uploadChunkSize];
      Object localObject2 = new java/lang/StringBuilder;
      ((StringBuilder)localObject2).<init>();
      localObject3 = ((StringBuilder)localObject2).append(this.uploadingFilePath);
      if (this.isEncrypted)
      {
        localObject2 = "enc";
        this.fileKey = Utilities.MD5((String)localObject2);
        localObject2 = this.preferences;
        localObject3 = new java/lang/StringBuilder;
        ((StringBuilder)localObject3).<init>();
        l = ((SharedPreferences)localObject2).getLong(this.fileKey + "_size", 0L);
        this.uploadStartTime = ((int)(System.currentTimeMillis() / 1000L));
        j = 0;
        if ((this.uploadFirstPartLater) || (this.nextPartFirst) || (this.estimatedSize != 0) || (l != this.totalFileSize)) {
          break label1342;
        }
        localObject2 = this.preferences;
        localObject3 = new java/lang/StringBuilder;
        ((StringBuilder)localObject3).<init>();
        this.currentFileId = ((SharedPreferences)localObject2).getLong(this.fileKey + "_id", 0L);
        localObject3 = this.preferences;
        localObject2 = new java/lang/StringBuilder;
        ((StringBuilder)localObject2).<init>();
        i = ((SharedPreferences)localObject3).getInt(this.fileKey + "_time", 0);
        localObject3 = this.preferences;
        localObject2 = new java/lang/StringBuilder;
        ((StringBuilder)localObject2).<init>();
        l = ((SharedPreferences)localObject3).getLong(this.fileKey + "_uploaded", 0L);
        k = j;
        if (this.isEncrypted)
        {
          localObject3 = this.preferences;
          localObject2 = new java/lang/StringBuilder;
          ((StringBuilder)localObject2).<init>();
          localObject2 = ((SharedPreferences)localObject3).getString(this.fileKey + "_iv", null);
          localObject4 = this.preferences;
          localObject3 = new java/lang/StringBuilder;
          ((StringBuilder)localObject3).<init>();
          localObject3 = ((SharedPreferences)localObject4).getString(this.fileKey + "_key", null);
          if ((localObject2 == null) || (localObject3 == null)) {
            break label945;
          }
          this.key = Utilities.hexToBytes((String)localObject3);
          this.iv = Utilities.hexToBytes((String)localObject2);
          if ((this.key == null) || (this.iv == null) || (this.key.length != 32) || (this.iv.length != 32)) {
            break label939;
          }
          this.ivChange = new byte[32];
          System.arraycopy(this.iv, 0, this.ivChange, 0, 32);
          k = j;
        }
        if ((k != 0) || (i == 0)) {
          break label1337;
        }
        if ((!this.isBigFile) || (i >= this.uploadStartTime - 86400)) {
          break label951;
        }
        j = 0;
        i = k;
        if (j == 0) {
          break label1118;
        }
        if (l <= 0L) {
          break label1332;
        }
        this.readBytesCount = l;
        this.currentPartNum = ((int)(l / this.uploadChunkSize));
        if (this.isBigFile) {
          break label1017;
        }
      }
      for (j = 0;; j++)
      {
        i = k;
        if (j >= this.readBytesCount / this.uploadChunkSize) {
          break label1118;
        }
        int m = this.stream.read(this.readBuffer);
        n = 0;
        i = n;
        if (this.isEncrypted)
        {
          i = n;
          if (m % 16 != 0) {
            i = 0 + (16 - m % 16);
          }
        }
        localObject2 = new org/telegram/tgnet/NativeByteBuffer;
        ((NativeByteBuffer)localObject2).<init>(m + i);
        if ((m != this.uploadChunkSize) || (this.totalPartsCount == this.currentPartNum + 1)) {
          this.isLastPart = true;
        }
        ((NativeByteBuffer)localObject2).writeBytes(this.readBuffer, 0, m);
        if (this.isEncrypted)
        {
          n = 0;
          for (;;)
          {
            if (n < i)
            {
              ((NativeByteBuffer)localObject2).writeByte(0);
              n++;
              continue;
              localObject2 = "";
              break;
              k = 1;
              break label712;
              k = 1;
              break label712;
              j = i;
              if (this.isBigFile) {
                break label743;
              }
              j = i;
              if (i >= this.uploadStartTime - 5400.0F) {
                break label743;
              }
              j = 0;
              break label743;
            }
          }
          Utilities.aesIgeEncryption(((NativeByteBuffer)localObject2).buffer, this.key, this.ivChange, true, true, 0, m + i);
        }
        ((NativeByteBuffer)localObject2).reuse();
      }
      this.stream.seek(l);
      i = k;
      if (this.isEncrypted)
      {
        localObject3 = this.preferences;
        localObject2 = new java/lang/StringBuilder;
        ((StringBuilder)localObject2).<init>();
        localObject2 = ((SharedPreferences)localObject3).getString(this.fileKey + "_ivc", null);
        if (localObject2 == null) {
          break label1317;
        }
        this.ivChange = Utilities.hexToBytes((String)localObject2);
        if (this.ivChange != null)
        {
          i = k;
          if (this.ivChange.length == 32) {}
        }
        else
        {
          i = 1;
          this.readBytesCount = 0L;
          this.currentPartNum = 0;
        }
      }
      for (;;)
      {
        if (i != 0)
        {
          if (this.isEncrypted)
          {
            this.iv = new byte[32];
            this.key = new byte[32];
            this.ivChange = new byte[32];
            Utilities.random.nextBytes(this.iv);
            Utilities.random.nextBytes(this.key);
            System.arraycopy(this.iv, 0, this.ivChange, 0, 32);
          }
          this.currentFileId = Utilities.random.nextLong();
          if ((!this.nextPartFirst) && (!this.uploadFirstPartLater) && (this.estimatedSize == 0)) {
            storeFileUploadInfo();
          }
        }
        boolean bool = this.isEncrypted;
        if (!bool) {
          break;
        }
        try
        {
          localObject3 = MessageDigest.getInstance("MD5");
          localObject2 = new byte[64];
          System.arraycopy(this.key, 0, localObject2, 0, 32);
          System.arraycopy(this.iv, 0, localObject2, 32, 32);
          localObject2 = ((MessageDigest)localObject3).digest((byte[])localObject2);
          for (i = 0; i < 4; i++) {
            this.fingerprint |= ((localObject2[i] ^ localObject2[(i + 4)]) & 0xFF) << i * 8;
          }
          i = 1;
          this.readBytesCount = 0L;
          this.currentPartNum = 0;
        }
        catch (Exception localException2)
        {
          FileLog.e(localException2);
        }
        i = 1;
        continue;
        i = 1;
        continue;
        i = 1;
      }
      this.uploadedBytesCount = this.readBytesCount;
      this.lastSavedPartNum = this.currentPartNum;
      if (this.uploadFirstPartLater)
      {
        if (!this.isBigFile) {
          break;
        }
        this.stream.seek(this.uploadChunkSize);
        this.readBytesCount = this.uploadChunkSize;
        this.currentPartNum = 1;
      }
    } while ((this.estimatedSize != 0) && (this.readBytesCount + this.uploadChunkSize > this.availableSize));
    if (this.nextPartFirst)
    {
      this.stream.seek(0L);
      if (this.isBigFile)
      {
        i = this.stream.read(this.readBuffer);
        label1467:
        this.currentPartNum = 0;
        label1472:
        if (i == -1) {
          break label1671;
        }
        j = 0;
        k = j;
        if (this.isEncrypted)
        {
          k = j;
          if (i % 16 != 0) {
            k = 0 + (16 - i % 16);
          }
        }
        localObject4 = new org/telegram/tgnet/NativeByteBuffer;
        ((NativeByteBuffer)localObject4).<init>(i + k);
        if ((this.nextPartFirst) || (i != this.uploadChunkSize) || ((this.estimatedSize == 0) && (this.totalPartsCount == this.currentPartNum + 1)))
        {
          if (!this.uploadFirstPartLater) {
            break label1673;
          }
          this.nextPartFirst = true;
          this.uploadFirstPartLater = false;
        }
      }
    }
    for (;;)
    {
      ((NativeByteBuffer)localObject4).writeBytes(this.readBuffer, 0, i);
      if (!this.isEncrypted) {
        break label1957;
      }
      for (j = 0; j < k; j++) {
        ((NativeByteBuffer)localObject4).writeByte(0);
      }
      this.stream.seek(1024L);
      this.readBytesCount = 1024L;
      break label1403;
      i = this.stream.read(this.readBuffer, 0, 1024);
      break label1467;
      i = this.stream.read(this.readBuffer);
      break label1472;
      label1671:
      break;
      label1673:
      this.isLastPart = true;
    }
    Utilities.aesIgeEncryption(((NativeByteBuffer)localObject4).buffer, this.key, this.ivChange, true, true, 0, i + k);
    final byte[] arrayOfByte = (byte[])this.freeRequestIvs.get(0);
    System.arraycopy(this.ivChange, 0, arrayOfByte, 0, 32);
    this.freeRequestIvs.remove(0);
    label1737:
    if (this.isBigFile)
    {
      localObject3 = new org/telegram/tgnet/TLRPC$TL_upload_saveBigFilePart;
      ((TLRPC.TL_upload_saveBigFilePart)localObject3).<init>();
      k = this.currentPartNum;
      ((TLRPC.TL_upload_saveBigFilePart)localObject3).file_part = k;
      ((TLRPC.TL_upload_saveBigFilePart)localObject3).file_id = this.currentFileId;
      if (this.estimatedSize != 0)
      {
        ((TLRPC.TL_upload_saveBigFilePart)localObject3).file_total_parts = -1;
        label1784:
        ((TLRPC.TL_upload_saveBigFilePart)localObject3).bytes = ((NativeByteBuffer)localObject4);
      }
    }
    for (;;)
    {
      if ((this.isLastPart) && (this.nextPartFirst))
      {
        this.nextPartFirst = false;
        this.currentPartNum = (this.totalPartsCount - 1);
        this.stream.seek(this.totalFileSize);
      }
      this.readBytesCount += i;
      this.currentPartNum += 1;
      this.currentUploadRequetsCount += 1;
      j = this.requestNum;
      this.requestNum = (j + 1);
      l = k + i;
      n = ((TLObject)localObject3).getObjectSize();
      i = ConnectionsManager.getInstance(this.currentAccount).sendRequest((TLObject)localObject3, new RequestDelegate()
      {
        public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
        {
          int i;
          if (paramAnonymousTLObject != null)
          {
            i = paramAnonymousTLObject.networkType;
            if (FileUploadOperation.this.currentType != 50331648) {
              break label103;
            }
            StatsController.getInstance(FileUploadOperation.this.currentAccount).incrementSentBytesCount(i, 3, this.val$requestSize);
            label41:
            if (arrayOfByte != null) {
              FileUploadOperation.this.freeRequestIvs.add(arrayOfByte);
            }
            FileUploadOperation.this.requestTokens.delete(j);
            if (!(paramAnonymousTLObject instanceof TLRPC.TL_boolTrue)) {
              break label1107;
            }
            if (FileUploadOperation.this.state == 1) {
              break label208;
            }
          }
          for (;;)
          {
            return;
            i = ConnectionsManager.getCurrentNetworkType();
            break;
            label103:
            if (FileUploadOperation.this.currentType == 33554432)
            {
              StatsController.getInstance(FileUploadOperation.this.currentAccount).incrementSentBytesCount(i, 2, this.val$requestSize);
              break label41;
            }
            if (FileUploadOperation.this.currentType == 16777216)
            {
              StatsController.getInstance(FileUploadOperation.this.currentAccount).incrementSentBytesCount(i, 4, this.val$requestSize);
              break label41;
            }
            if (FileUploadOperation.this.currentType != 67108864) {
              break label41;
            }
            StatsController.getInstance(FileUploadOperation.this.currentAccount).incrementSentBytesCount(i, 5, this.val$requestSize);
            break label41;
            label208:
            FileUploadOperation.access$1602(FileUploadOperation.this, FileUploadOperation.this.uploadedBytesCount + i);
            long l;
            if (FileUploadOperation.this.estimatedSize != 0)
            {
              l = Math.max(FileUploadOperation.this.availableSize, FileUploadOperation.this.estimatedSize);
              label259:
              FileUploadOperation.this.delegate.didChangedUploadProgress(FileUploadOperation.this, (float)FileUploadOperation.this.uploadedBytesCount / (float)l);
              FileUploadOperation.access$1110(FileUploadOperation.this);
              if ((!FileUploadOperation.this.isLastPart) || (FileUploadOperation.this.currentUploadRequetsCount != 0) || (FileUploadOperation.this.state != 1)) {
                break label711;
              }
              FileUploadOperation.access$1502(FileUploadOperation.this, 3);
              if (FileUploadOperation.this.key != null) {
                break label502;
              }
              if (!FileUploadOperation.this.isBigFile) {
                break label485;
              }
              paramAnonymousTLObject = new TLRPC.TL_inputFileBig();
            }
            for (;;)
            {
              paramAnonymousTLObject.parts = FileUploadOperation.this.currentPartNum;
              paramAnonymousTLObject.id = FileUploadOperation.this.currentFileId;
              paramAnonymousTLObject.name = FileUploadOperation.this.uploadingFilePath.substring(FileUploadOperation.this.uploadingFilePath.lastIndexOf("/") + 1);
              FileUploadOperation.this.delegate.didFinishUploadingFile(FileUploadOperation.this, paramAnonymousTLObject, null, null, null);
              FileUploadOperation.this.cleanup();
              if (FileUploadOperation.this.currentType != 50331648) {
                break label612;
              }
              StatsController.getInstance(FileUploadOperation.this.currentAccount).incrementSentItemsCount(ConnectionsManager.getCurrentNetworkType(), 3, 1);
              break;
              l = FileUploadOperation.this.totalFileSize;
              break label259;
              label485:
              paramAnonymousTLObject = new TLRPC.TL_inputFile();
              paramAnonymousTLObject.md5_checksum = "";
            }
            label502:
            if (FileUploadOperation.this.isBigFile) {
              paramAnonymousTLObject = new TLRPC.TL_inputEncryptedFileBigUploaded();
            }
            for (;;)
            {
              paramAnonymousTLObject.parts = FileUploadOperation.this.currentPartNum;
              paramAnonymousTLObject.id = FileUploadOperation.this.currentFileId;
              paramAnonymousTLObject.key_fingerprint = FileUploadOperation.this.fingerprint;
              FileUploadOperation.this.delegate.didFinishUploadingFile(FileUploadOperation.this, null, paramAnonymousTLObject, FileUploadOperation.this.key, FileUploadOperation.this.iv);
              FileUploadOperation.this.cleanup();
              break;
              paramAnonymousTLObject = new TLRPC.TL_inputEncryptedFileUploaded();
              paramAnonymousTLObject.md5_checksum = "";
            }
            label612:
            if (FileUploadOperation.this.currentType == 33554432)
            {
              StatsController.getInstance(FileUploadOperation.this.currentAccount).incrementSentItemsCount(ConnectionsManager.getCurrentNetworkType(), 2, 1);
            }
            else if (FileUploadOperation.this.currentType == 16777216)
            {
              StatsController.getInstance(FileUploadOperation.this.currentAccount).incrementSentItemsCount(ConnectionsManager.getCurrentNetworkType(), 4, 1);
            }
            else if (FileUploadOperation.this.currentType == 67108864)
            {
              StatsController.getInstance(FileUploadOperation.this.currentAccount).incrementSentItemsCount(ConnectionsManager.getCurrentNetworkType(), 5, 1);
              continue;
              label711:
              if (FileUploadOperation.this.currentUploadRequetsCount < FileUploadOperation.this.maxRequestsCount)
              {
                if ((FileUploadOperation.this.estimatedSize == 0) && (!FileUploadOperation.this.uploadFirstPartLater) && (!FileUploadOperation.this.nextPartFirst))
                {
                  if (FileUploadOperation.this.saveInfoTimes >= 4) {
                    FileUploadOperation.access$2802(FileUploadOperation.this, 0);
                  }
                  if (k != FileUploadOperation.this.lastSavedPartNum) {
                    break label1036;
                  }
                  FileUploadOperation.access$2908(FileUploadOperation.this);
                  l = l;
                  paramAnonymousTLObject = arrayOfByte;
                  for (;;)
                  {
                    paramAnonymousTL_error = (FileUploadOperation.UploadCachedResult)FileUploadOperation.this.cachedResults.get(FileUploadOperation.this.lastSavedPartNum);
                    if (paramAnonymousTL_error == null) {
                      break;
                    }
                    l = FileUploadOperation.UploadCachedResult.access$3100(paramAnonymousTL_error);
                    paramAnonymousTLObject = FileUploadOperation.UploadCachedResult.access$3200(paramAnonymousTL_error);
                    FileUploadOperation.this.cachedResults.remove(FileUploadOperation.this.lastSavedPartNum);
                    FileUploadOperation.access$2908(FileUploadOperation.this);
                  }
                  if (((FileUploadOperation.this.isBigFile) && (l % 1048576L == 0L)) || ((!FileUploadOperation.this.isBigFile) && (FileUploadOperation.this.saveInfoTimes == 0)))
                  {
                    paramAnonymousTL_error = FileUploadOperation.this.preferences.edit();
                    paramAnonymousTL_error.putLong(FileUploadOperation.this.fileKey + "_uploaded", l);
                    if (FileUploadOperation.this.isEncrypted) {
                      paramAnonymousTL_error.putString(FileUploadOperation.this.fileKey + "_ivc", Utilities.bytesToHex(paramAnonymousTLObject));
                    }
                    paramAnonymousTL_error.commit();
                  }
                }
                for (;;)
                {
                  FileUploadOperation.access$2808(FileUploadOperation.this);
                  FileUploadOperation.this.startUploadRequest();
                  break;
                  label1036:
                  paramAnonymousTLObject = new FileUploadOperation.UploadCachedResult(FileUploadOperation.this, null);
                  FileUploadOperation.UploadCachedResult.access$3102(paramAnonymousTLObject, l);
                  if (arrayOfByte != null)
                  {
                    FileUploadOperation.UploadCachedResult.access$3202(paramAnonymousTLObject, new byte[32]);
                    System.arraycopy(arrayOfByte, 0, FileUploadOperation.UploadCachedResult.access$3200(paramAnonymousTLObject), 0, 32);
                  }
                  FileUploadOperation.this.cachedResults.put(k, paramAnonymousTLObject);
                }
                label1107:
                if (this.val$finalRequest != null) {
                  FileLog.e("23123");
                }
                FileUploadOperation.access$1502(FileUploadOperation.this, 4);
                FileUploadOperation.this.delegate.didFailedUploadingFile(FileUploadOperation.this);
                FileUploadOperation.this.cleanup();
              }
            }
          }
        }
      }, null, new WriteToSocketDelegate()
      {
        public void run()
        {
          Utilities.stageQueue.postRunnable(new Runnable()
          {
            public void run()
            {
              if (FileUploadOperation.this.currentUploadRequetsCount < FileUploadOperation.this.maxRequestsCount) {
                FileUploadOperation.this.startUploadRequest();
              }
            }
          });
        }
      }, 0, Integer.MAX_VALUE, j % 4 << 16 | 0x4, true);
      this.requestTokens.put(j, i);
      break;
      label1957:
      arrayOfByte = null;
      break label1737;
      ((TLRPC.TL_upload_saveBigFilePart)localObject3).file_total_parts = this.totalPartsCount;
      break label1784;
      localObject3 = new org/telegram/tgnet/TLRPC$TL_upload_saveFilePart;
      ((TLRPC.TL_upload_saveFilePart)localObject3).<init>();
      k = this.currentPartNum;
      ((TLRPC.TL_upload_saveFilePart)localObject3).file_part = k;
      ((TLRPC.TL_upload_saveFilePart)localObject3).file_id = this.currentFileId;
      ((TLRPC.TL_upload_saveFilePart)localObject3).bytes = ((NativeByteBuffer)localObject4);
    }
  }
  
  private void storeFileUploadInfo()
  {
    SharedPreferences.Editor localEditor = this.preferences.edit();
    localEditor.putInt(this.fileKey + "_time", this.uploadStartTime);
    localEditor.putLong(this.fileKey + "_size", this.totalFileSize);
    localEditor.putLong(this.fileKey + "_id", this.currentFileId);
    localEditor.remove(this.fileKey + "_uploaded");
    if (this.isEncrypted)
    {
      localEditor.putString(this.fileKey + "_iv", Utilities.bytesToHex(this.iv));
      localEditor.putString(this.fileKey + "_ivc", Utilities.bytesToHex(this.ivChange));
      localEditor.putString(this.fileKey + "_key", Utilities.bytesToHex(this.key));
    }
    localEditor.commit();
  }
  
  public void cancel()
  {
    if (this.state == 3) {}
    for (;;)
    {
      return;
      this.state = 2;
      Utilities.stageQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          for (int i = 0; i < FileUploadOperation.this.requestTokens.size(); i++) {
            ConnectionsManager.getInstance(FileUploadOperation.this.currentAccount).cancelRequest(FileUploadOperation.this.requestTokens.valueAt(i), true);
          }
        }
      });
      this.delegate.didFailedUploadingFile(this);
      cleanup();
    }
  }
  
  protected void checkNewDataAvailable(long paramLong1, final long paramLong2)
  {
    Utilities.stageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        if ((FileUploadOperation.this.estimatedSize != 0) && (paramLong2 != 0L))
        {
          FileUploadOperation.access$402(FileUploadOperation.this, 0);
          FileUploadOperation.access$502(FileUploadOperation.this, paramLong2);
          FileUploadOperation.this.calcTotalPartsCount();
          if ((!FileUploadOperation.this.uploadFirstPartLater) && (FileUploadOperation.this.started)) {
            FileUploadOperation.this.storeFileUploadInfo();
          }
        }
        FileUploadOperation.access$1002(FileUploadOperation.this, this.val$newAvailableSize);
        if (FileUploadOperation.this.currentUploadRequetsCount < FileUploadOperation.this.maxRequestsCount) {
          FileUploadOperation.this.startUploadRequest();
        }
      }
    });
  }
  
  public long getTotalFileSize()
  {
    return this.totalFileSize;
  }
  
  public void setDelegate(FileUploadOperationDelegate paramFileUploadOperationDelegate)
  {
    this.delegate = paramFileUploadOperationDelegate;
  }
  
  public void start()
  {
    if (this.state != 0) {}
    for (;;)
    {
      return;
      this.state = 1;
      Utilities.stageQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          FileUploadOperation.access$002(FileUploadOperation.this, ApplicationLoader.applicationContext.getSharedPreferences("uploadinfo", 0));
          for (int i = 0; i < 8; i++) {
            FileUploadOperation.this.startUploadRequest();
          }
        }
      });
    }
  }
  
  public static abstract interface FileUploadOperationDelegate
  {
    public abstract void didChangedUploadProgress(FileUploadOperation paramFileUploadOperation, float paramFloat);
    
    public abstract void didFailedUploadingFile(FileUploadOperation paramFileUploadOperation);
    
    public abstract void didFinishUploadingFile(FileUploadOperation paramFileUploadOperation, TLRPC.InputFile paramInputFile, TLRPC.InputEncryptedFile paramInputEncryptedFile, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2);
  }
  
  private class UploadCachedResult
  {
    private long bytesOffset;
    private byte[] iv;
    
    private UploadCachedResult() {}
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/FileUploadOperation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */