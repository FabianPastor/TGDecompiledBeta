package org.telegram.messenger;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Locale;
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

public class FileUploadOperation
{
  private long currentFileId;
  private int currentPartNum = 0;
  private long currentUploaded = 0L;
  public FileUploadOperationDelegate delegate;
  private int estimatedSize = 0;
  private String fileKey;
  private int fingerprint = 0;
  private boolean isBigFile = false;
  private boolean isEncrypted = false;
  private boolean isLastPart = false;
  private byte[] iv;
  private byte[] ivChange;
  private byte[] key;
  private MessageDigest mdEnc = null;
  private byte[] readBuffer;
  private int requestToken = 0;
  private int saveInfoTimes = 0;
  private boolean started = false;
  public int state = 0;
  private FileInputStream stream;
  private long totalFileSize = 0L;
  private int totalPartsCount = 0;
  private int uploadChunkSize = 32768;
  private int uploadStartTime = 0;
  private String uploadingFilePath;
  
  public FileUploadOperation(String paramString, boolean paramBoolean, int paramInt)
  {
    this.uploadingFilePath = paramString;
    this.isEncrypted = paramBoolean;
    this.estimatedSize = paramInt;
  }
  
  private void cleanup()
  {
    ApplicationLoader.applicationContext.getSharedPreferences("uploadinfo", 0).edit().remove(this.fileKey + "_time").remove(this.fileKey + "_size").remove(this.fileKey + "_uploaded").remove(this.fileKey + "_id").remove(this.fileKey + "_iv").remove(this.fileKey + "_key").remove(this.fileKey + "_ivc").commit();
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
      FileLog.e("tmessages", localException);
    }
  }
  
  private void startUploadRequest()
  {
    if (this.state != 1) {
      return;
    }
    int i;
    for (;;)
    {
      try
      {
        this.started = true;
        if (this.stream != null) {
          break label1395;
        }
        File localFile = new File(this.uploadingFilePath);
        this.stream = new FileInputStream(localFile);
        if (this.estimatedSize != 0)
        {
          this.totalFileSize = this.estimatedSize;
          if (this.totalFileSize > 10485760L)
          {
            this.isBigFile = true;
            this.uploadChunkSize = ((int)Math.max(32L, (this.totalFileSize + 3072000L - 1L) / 3072000L));
            if (1024 % this.uploadChunkSize == 0) {
              break label198;
            }
            i = 64;
            if (this.uploadChunkSize <= i) {
              break;
            }
            i *= 2;
            continue;
          }
        }
        else
        {
          this.totalFileSize = localFile.length();
          continue;
        }
        try
        {
          this.mdEnc = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
        {
          FileLog.e("tmessages", localNoSuchAlgorithmException);
        }
      }
      catch (Exception localException1)
      {
        FileLog.e("tmessages", localException1);
        this.delegate.didFailedUploadingFile(this);
        cleanup();
        return;
      }
    }
    this.uploadChunkSize = i;
    label198:
    this.uploadChunkSize *= 1024;
    this.totalPartsCount = ((int)(this.totalFileSize + this.uploadChunkSize - 1L) / this.uploadChunkSize);
    this.readBuffer = new byte[this.uploadChunkSize];
    Object localObject3 = new StringBuilder().append(this.uploadingFilePath);
    Object localObject1;
    label269:
    long l;
    int k;
    int j;
    if (this.isEncrypted)
    {
      localObject1 = "enc";
      this.fileKey = Utilities.MD5((String)localObject1);
      localObject1 = ApplicationLoader.applicationContext.getSharedPreferences("uploadinfo", 0);
      l = ((SharedPreferences)localObject1).getLong(this.fileKey + "_size", 0L);
      this.uploadStartTime = ((int)(System.currentTimeMillis() / 1000L));
      k = 0;
      if ((this.estimatedSize != 0) || (l != this.totalFileSize)) {
        break label1775;
      }
      this.currentFileId = ((SharedPreferences)localObject1).getLong(this.fileKey + "_id", 0L);
      i = ((SharedPreferences)localObject1).getInt(this.fileKey + "_time", 0);
      l = ((SharedPreferences)localObject1).getLong(this.fileKey + "_uploaded", 0L);
      j = k;
      if (this.isEncrypted)
      {
        localObject3 = ((SharedPreferences)localObject1).getString(this.fileKey + "_iv", null);
        String str = ((SharedPreferences)localObject1).getString(this.fileKey + "_key", null);
        if ((localObject3 == null) || (str == null)) {
          break label1760;
        }
        this.key = Utilities.hexToBytes(str);
        this.iv = Utilities.hexToBytes((String)localObject3);
        if ((this.key == null) || (this.iv == null) || (this.key.length != 32) || (this.iv.length != 32)) {
          break label1755;
        }
        this.ivChange = new byte[32];
        System.arraycopy(this.iv, 0, this.ivChange, 0, 32);
        j = k;
      }
      label617:
      if ((j != 0) || (i == 0)) {
        break label1770;
      }
      if ((this.isBigFile) && (i < this.uploadStartTime - 86400))
      {
        k = 0;
        i = j;
        if (k == 0) {
          break label1015;
        }
        if (l <= 0L) {
          break label1765;
        }
        this.currentUploaded = l;
        this.currentPartNum = ((int)(l / this.uploadChunkSize));
        if (!this.isBigFile) {
          k = 0;
        }
      }
      else
      {
        for (;;)
        {
          i = j;
          if (k >= this.currentUploaded / this.uploadChunkSize) {
            break label1015;
          }
          int n = this.stream.read(this.readBuffer);
          int m = 0;
          i = m;
          if (this.isEncrypted)
          {
            i = m;
            if (n % 16 != 0) {
              i = 0 + (16 - n % 16);
            }
          }
          localObject3 = new NativeByteBuffer(n + i);
          if ((n != this.uploadChunkSize) || (this.totalPartsCount == this.currentPartNum + 1)) {
            this.isLastPart = true;
          }
          ((NativeByteBuffer)localObject3).writeBytes(this.readBuffer, 0, n);
          if (this.isEncrypted)
          {
            m = 0;
            for (;;)
            {
              if (m < i)
              {
                ((NativeByteBuffer)localObject3).writeByte(0);
                m += 1;
                continue;
                k = i;
                if (this.isBigFile) {
                  break;
                }
                k = i;
                if (i >= this.uploadStartTime - 5400.0F) {
                  break;
                }
                k = 0;
                break;
              }
            }
            Utilities.aesIgeEncryption(((NativeByteBuffer)localObject3).buffer, this.key, this.ivChange, true, true, 0, n + i);
          }
          ((NativeByteBuffer)localObject3).rewind();
          this.mdEnc.update(((NativeByteBuffer)localObject3).buffer);
          ((NativeByteBuffer)localObject3).reuse();
          k += 1;
        }
      }
      this.stream.skip(l);
      i = j;
      if (this.isEncrypted)
      {
        localObject3 = ((SharedPreferences)localObject1).getString(this.fileKey + "_ivc", null);
        if (localObject3 == null) {
          break label1211;
        }
        this.ivChange = Utilities.hexToBytes((String)localObject3);
        if (this.ivChange != null)
        {
          i = j;
          if (this.ivChange.length == 32) {}
        }
        else
        {
          i = 1;
          this.currentUploaded = 0L;
          this.currentPartNum = 0;
        }
      }
    }
    for (;;)
    {
      label1015:
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
        if (this.estimatedSize == 0) {
          storeFileUploadInfo((SharedPreferences)localObject1);
        }
      }
      boolean bool = this.isEncrypted;
      if (bool)
      {
        try
        {
          localObject1 = MessageDigest.getInstance("MD5");
          localObject3 = new byte[64];
          System.arraycopy(this.key, 0, localObject3, 0, 32);
          System.arraycopy(this.iv, 0, localObject3, 32, 32);
          localObject1 = ((MessageDigest)localObject1).digest((byte[])localObject3);
          i = 0;
          while (i < 4)
          {
            this.fingerprint |= ((localObject1[i] ^ localObject1[(i + 4)]) & 0xFF) << i * 8;
            i += 1;
            continue;
            label1211:
            i = 1;
            this.currentUploaded = 0L;
            this.currentPartNum = 0;
          }
        }
        catch (Exception localException2)
        {
          FileLog.e("tmessages", localException2);
        }
      }
      else
      {
        for (;;)
        {
          if (this.estimatedSize != 0)
          {
            l = this.stream.getChannel().size();
            if (this.currentUploaded + this.uploadChunkSize > l) {
              break;
            }
          }
          k = this.stream.read(this.readBuffer);
          j = 0;
          i = j;
          if (this.isEncrypted)
          {
            i = j;
            if (k % 16 != 0) {
              i = 0 + (16 - k % 16);
            }
          }
          localObject3 = new NativeByteBuffer(k + i);
          if ((k != this.uploadChunkSize) || ((this.estimatedSize == 0) && (this.totalPartsCount == this.currentPartNum + 1))) {
            this.isLastPart = true;
          }
          ((NativeByteBuffer)localObject3).writeBytes(this.readBuffer, 0, k);
          if (!this.isEncrypted) {
            break label1586;
          }
          j = 0;
          while (j < i)
          {
            ((NativeByteBuffer)localObject3).writeByte(0);
            j += 1;
          }
          label1395:
          if (this.estimatedSize == 0)
          {
            if (this.saveInfoTimes >= 4) {
              this.saveInfoTimes = 0;
            }
            if (((this.isBigFile) && (this.currentUploaded % 1048576L == 0L)) || ((!this.isBigFile) && (this.saveInfoTimes == 0)))
            {
              localObject2 = ApplicationLoader.applicationContext.getSharedPreferences("uploadinfo", 0).edit();
              ((SharedPreferences.Editor)localObject2).putLong(this.fileKey + "_uploaded", this.currentUploaded);
              if (this.isEncrypted) {
                ((SharedPreferences.Editor)localObject2).putString(this.fileKey + "_ivc", Utilities.bytesToHex(this.ivChange));
              }
              ((SharedPreferences.Editor)localObject2).commit();
            }
            this.saveInfoTimes += 1;
          }
        }
        Utilities.aesIgeEncryption(((NativeByteBuffer)localObject3).buffer, this.key, this.ivChange, true, true, 0, k + i);
        label1586:
        ((NativeByteBuffer)localObject3).rewind();
        if (!this.isBigFile) {
          this.mdEnc.update(((NativeByteBuffer)localObject3).buffer);
        }
        if (this.isBigFile)
        {
          localObject2 = new TLRPC.TL_upload_saveBigFilePart();
          ((TLRPC.TL_upload_saveBigFilePart)localObject2).file_part = this.currentPartNum;
          ((TLRPC.TL_upload_saveBigFilePart)localObject2).file_id = this.currentFileId;
          if (this.estimatedSize != 0)
          {
            ((TLRPC.TL_upload_saveBigFilePart)localObject2).file_total_parts = -1;
            ((TLRPC.TL_upload_saveBigFilePart)localObject2).bytes = ((NativeByteBuffer)localObject3);
          }
        }
        for (;;)
        {
          this.currentUploaded += k;
          this.requestToken = ConnectionsManager.getInstance().sendRequest((TLObject)localObject2, new RequestDelegate()
          {
            public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
            {
              FileUploadOperation.access$702(FileUploadOperation.this, 0);
              if (paramAnonymousTL_error == null)
              {
                if ((paramAnonymousTLObject instanceof TLRPC.TL_boolTrue))
                {
                  FileUploadOperation.access$808(FileUploadOperation.this);
                  FileUploadOperation.this.delegate.didChangedUploadProgress(FileUploadOperation.this, (float)FileUploadOperation.this.currentUploaded / (float)FileUploadOperation.this.totalFileSize);
                  if (FileUploadOperation.this.isLastPart)
                  {
                    FileUploadOperation.this.state = 3;
                    if (FileUploadOperation.this.key == null)
                    {
                      if (FileUploadOperation.this.isBigFile) {
                        paramAnonymousTLObject = new TLRPC.TL_inputFileBig();
                      }
                      for (;;)
                      {
                        paramAnonymousTLObject.parts = FileUploadOperation.this.currentPartNum;
                        paramAnonymousTLObject.id = FileUploadOperation.this.currentFileId;
                        paramAnonymousTLObject.name = FileUploadOperation.this.uploadingFilePath.substring(FileUploadOperation.this.uploadingFilePath.lastIndexOf("/") + 1);
                        FileUploadOperation.this.delegate.didFinishUploadingFile(FileUploadOperation.this, paramAnonymousTLObject, null, null, null);
                        FileUploadOperation.this.cleanup();
                        return;
                        paramAnonymousTLObject = new TLRPC.TL_inputFile();
                        paramAnonymousTLObject.md5_checksum = String.format(Locale.US, "%32s", new Object[] { new BigInteger(1, FileUploadOperation.this.mdEnc.digest()).toString(16) }).replace(' ', '0');
                      }
                    }
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
                      return;
                      paramAnonymousTLObject = new TLRPC.TL_inputEncryptedFileUploaded();
                      paramAnonymousTLObject.md5_checksum = String.format(Locale.US, "%32s", new Object[] { new BigInteger(1, FileUploadOperation.this.mdEnc.digest()).toString(16) }).replace(' ', '0');
                    }
                  }
                  FileUploadOperation.this.startUploadRequest();
                  return;
                }
                FileUploadOperation.this.delegate.didFailedUploadingFile(FileUploadOperation.this);
                FileUploadOperation.this.cleanup();
                return;
              }
              FileUploadOperation.this.delegate.didFailedUploadingFile(FileUploadOperation.this);
              FileUploadOperation.this.cleanup();
            }
          }, 0, 4);
          return;
          ((TLRPC.TL_upload_saveBigFilePart)localObject2).file_total_parts = this.totalPartsCount;
          break;
          localObject2 = new TLRPC.TL_upload_saveFilePart();
          ((TLRPC.TL_upload_saveFilePart)localObject2).file_part = this.currentPartNum;
          ((TLRPC.TL_upload_saveFilePart)localObject2).file_id = this.currentFileId;
          ((TLRPC.TL_upload_saveFilePart)localObject2).bytes = ((NativeByteBuffer)localObject3);
        }
        Object localObject2 = "";
        break label269;
        label1755:
        j = 1;
        break label617;
        label1760:
        j = 1;
        break label617;
        label1765:
        i = 1;
        continue;
        label1770:
        i = 1;
        continue;
        label1775:
        i = 1;
      }
    }
  }
  
  private void storeFileUploadInfo(SharedPreferences paramSharedPreferences)
  {
    paramSharedPreferences = paramSharedPreferences.edit();
    paramSharedPreferences.putInt(this.fileKey + "_time", this.uploadStartTime);
    paramSharedPreferences.putLong(this.fileKey + "_size", this.totalFileSize);
    paramSharedPreferences.putLong(this.fileKey + "_id", this.currentFileId);
    paramSharedPreferences.remove(this.fileKey + "_uploaded");
    if (this.isEncrypted)
    {
      paramSharedPreferences.putString(this.fileKey + "_iv", Utilities.bytesToHex(this.iv));
      paramSharedPreferences.putString(this.fileKey + "_ivc", Utilities.bytesToHex(this.ivChange));
      paramSharedPreferences.putString(this.fileKey + "_key", Utilities.bytesToHex(this.key));
    }
    paramSharedPreferences.commit();
  }
  
  public void cancel()
  {
    if (this.state == 3) {
      return;
    }
    this.state = 2;
    if (this.requestToken != 0) {
      ConnectionsManager.getInstance().cancelRequest(this.requestToken, true);
    }
    this.delegate.didFailedUploadingFile(this);
    cleanup();
  }
  
  protected void checkNewDataAvailable(final long paramLong)
  {
    Utilities.stageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        if ((FileUploadOperation.this.estimatedSize != 0) && (paramLong != 0L))
        {
          FileUploadOperation.access$102(FileUploadOperation.this, 0);
          FileUploadOperation.access$202(FileUploadOperation.this, paramLong);
          FileUploadOperation.access$302(FileUploadOperation.this, (int)(FileUploadOperation.this.totalFileSize + FileUploadOperation.this.uploadChunkSize - 1L) / FileUploadOperation.this.uploadChunkSize);
          if (FileUploadOperation.this.started)
          {
            SharedPreferences localSharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("uploadinfo", 0);
            FileUploadOperation.this.storeFileUploadInfo(localSharedPreferences);
          }
        }
        if (FileUploadOperation.this.requestToken == 0) {
          FileUploadOperation.this.startUploadRequest();
        }
      }
    });
  }
  
  public long getTotalFileSize()
  {
    return this.totalFileSize;
  }
  
  public void start()
  {
    if (this.state != 0) {
      return;
    }
    this.state = 1;
    Utilities.stageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        FileUploadOperation.this.startUploadRequest();
      }
    });
  }
  
  public static abstract interface FileUploadOperationDelegate
  {
    public abstract void didChangedUploadProgress(FileUploadOperation paramFileUploadOperation, float paramFloat);
    
    public abstract void didFailedUploadingFile(FileUploadOperation paramFileUploadOperation);
    
    public abstract void didFinishUploadingFile(FileUploadOperation paramFileUploadOperation, TLRPC.InputFile paramInputFile, TLRPC.InputEncryptedFile paramInputEncryptedFile, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/FileUploadOperation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */