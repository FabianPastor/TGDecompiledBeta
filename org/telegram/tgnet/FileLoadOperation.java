package org.telegram.tgnet;

import java.io.File;

public class FileLoadOperation
{
  private int address;
  private FileLoadOperationDelegate delegate;
  private boolean isForceRequest;
  private boolean started;
  
  public FileLoadOperation(int paramInt1, long paramLong1, long paramLong2, long paramLong3, int paramInt2, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, String paramString, int paramInt3, int paramInt4, File paramFile1, File paramFile2, FileLoadOperationDelegate paramFileLoadOperationDelegate)
  {
    this.address = native_createLoadOpetation(paramInt1, paramLong1, paramLong2, paramLong3, paramInt2, paramArrayOfByte1, paramArrayOfByte2, paramString, paramInt3, paramInt4, paramFile1.getAbsolutePath(), paramFile2.getAbsolutePath(), paramFileLoadOperationDelegate);
    this.delegate = paramFileLoadOperationDelegate;
  }
  
  public static native void native_cancelLoadOperation(int paramInt);
  
  public static native int native_createLoadOpetation(int paramInt1, long paramLong1, long paramLong2, long paramLong3, int paramInt2, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, String paramString1, int paramInt3, int paramInt4, String paramString2, String paramString3, Object paramObject);
  
  public static native void native_startLoadOperation(int paramInt);
  
  public void cancel()
  {
    if ((!this.started) || (this.address == 0)) {
      return;
    }
    native_cancelLoadOperation(this.address);
  }
  
  public boolean isForceRequest()
  {
    return this.isForceRequest;
  }
  
  public void setForceRequest(boolean paramBoolean)
  {
    this.isForceRequest = paramBoolean;
  }
  
  public void start()
  {
    if (this.started) {
      return;
    }
    if (this.address == 0)
    {
      this.delegate.onFailed(0);
      return;
    }
    this.started = true;
    native_startLoadOperation(this.address);
  }
  
  public boolean wasStarted()
  {
    return this.started;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/tgnet/FileLoadOperation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */