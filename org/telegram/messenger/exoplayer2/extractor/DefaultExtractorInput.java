package org.telegram.messenger.exoplayer2.extractor;

import java.io.EOFException;
import java.io.IOException;
import java.util.Arrays;
import org.telegram.messenger.exoplayer2.upstream.DataSource;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.Util;

public final class DefaultExtractorInput
  implements ExtractorInput
{
  private static final int PEEK_MAX_FREE_SPACE = 524288;
  private static final int PEEK_MIN_FREE_SPACE_AFTER_RESIZE = 65536;
  private static final int SCRATCH_SPACE_SIZE = 4096;
  private final DataSource dataSource;
  private byte[] peekBuffer;
  private int peekBufferLength;
  private int peekBufferPosition;
  private long position;
  private final byte[] scratchSpace;
  private final long streamLength;
  
  public DefaultExtractorInput(DataSource paramDataSource, long paramLong1, long paramLong2)
  {
    this.dataSource = paramDataSource;
    this.position = paramLong1;
    this.streamLength = paramLong2;
    this.peekBuffer = new byte[65536];
    this.scratchSpace = new byte['က'];
  }
  
  private void commitBytesRead(int paramInt)
  {
    if (paramInt != -1) {
      this.position += paramInt;
    }
  }
  
  private void ensureSpaceForPeek(int paramInt)
  {
    paramInt = this.peekBufferPosition + paramInt;
    if (paramInt > this.peekBuffer.length)
    {
      paramInt = Util.constrainValue(this.peekBuffer.length * 2, 65536 + paramInt, 524288 + paramInt);
      this.peekBuffer = Arrays.copyOf(this.peekBuffer, paramInt);
    }
  }
  
  private int readFromDataSource(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
    throws InterruptedException, IOException
  {
    int i = -1;
    if (Thread.interrupted()) {
      throw new InterruptedException();
    }
    paramInt1 = this.dataSource.read(paramArrayOfByte, paramInt1 + paramInt3, paramInt2 - paramInt3);
    if (paramInt1 == -1) {
      if ((paramInt3 != 0) || (!paramBoolean)) {}
    }
    for (paramInt1 = i;; paramInt1 = paramInt3 + paramInt1)
    {
      return paramInt1;
      throw new EOFException();
    }
  }
  
  private int readFromPeekBuffer(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if (this.peekBufferLength == 0) {}
    for (paramInt1 = 0;; paramInt1 = paramInt2)
    {
      return paramInt1;
      paramInt2 = Math.min(this.peekBufferLength, paramInt2);
      System.arraycopy(this.peekBuffer, 0, paramArrayOfByte, paramInt1, paramInt2);
      updatePeekBuffer(paramInt2);
    }
  }
  
  private int skipFromPeekBuffer(int paramInt)
  {
    paramInt = Math.min(this.peekBufferLength, paramInt);
    updatePeekBuffer(paramInt);
    return paramInt;
  }
  
  private void updatePeekBuffer(int paramInt)
  {
    this.peekBufferLength -= paramInt;
    this.peekBufferPosition = 0;
    byte[] arrayOfByte = this.peekBuffer;
    if (this.peekBufferLength < this.peekBuffer.length - 524288) {
      arrayOfByte = new byte[this.peekBufferLength + 65536];
    }
    System.arraycopy(this.peekBuffer, paramInt, arrayOfByte, 0, this.peekBufferLength);
    this.peekBuffer = arrayOfByte;
  }
  
  public void advancePeekPosition(int paramInt)
    throws IOException, InterruptedException
  {
    advancePeekPosition(paramInt, false);
  }
  
  public boolean advancePeekPosition(int paramInt, boolean paramBoolean)
    throws IOException, InterruptedException
  {
    ensureSpaceForPeek(paramInt);
    int i = Math.min(this.peekBufferLength - this.peekBufferPosition, paramInt);
    int j;
    do
    {
      if (i >= paramInt) {
        break;
      }
      j = readFromDataSource(this.peekBuffer, this.peekBufferPosition, paramInt, i, paramBoolean);
      i = j;
    } while (j != -1);
    for (paramBoolean = false;; paramBoolean = true)
    {
      return paramBoolean;
      this.peekBufferPosition += paramInt;
      this.peekBufferLength = Math.max(this.peekBufferLength, this.peekBufferPosition);
    }
  }
  
  public long getLength()
  {
    return this.streamLength;
  }
  
  public long getPeekPosition()
  {
    return this.position + this.peekBufferPosition;
  }
  
  public long getPosition()
  {
    return this.position;
  }
  
  public void peekFully(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException, InterruptedException
  {
    peekFully(paramArrayOfByte, paramInt1, paramInt2, false);
  }
  
  public boolean peekFully(byte[] paramArrayOfByte, int paramInt1, int paramInt2, boolean paramBoolean)
    throws IOException, InterruptedException
  {
    if (!advancePeekPosition(paramInt2, paramBoolean)) {}
    for (paramBoolean = false;; paramBoolean = true)
    {
      return paramBoolean;
      System.arraycopy(this.peekBuffer, this.peekBufferPosition - paramInt2, paramArrayOfByte, paramInt1, paramInt2);
    }
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException, InterruptedException
  {
    int i = readFromPeekBuffer(paramArrayOfByte, paramInt1, paramInt2);
    int j = i;
    if (i == 0) {
      j = readFromDataSource(paramArrayOfByte, paramInt1, paramInt2, 0, true);
    }
    commitBytesRead(j);
    return j;
  }
  
  public void readFully(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException, InterruptedException
  {
    readFully(paramArrayOfByte, paramInt1, paramInt2, false);
  }
  
  public boolean readFully(byte[] paramArrayOfByte, int paramInt1, int paramInt2, boolean paramBoolean)
    throws IOException, InterruptedException
  {
    for (int i = readFromPeekBuffer(paramArrayOfByte, paramInt1, paramInt2); (i < paramInt2) && (i != -1); i = readFromDataSource(paramArrayOfByte, paramInt1, paramInt2, i, paramBoolean)) {}
    commitBytesRead(i);
    if (i != -1) {}
    for (paramBoolean = true;; paramBoolean = false) {
      return paramBoolean;
    }
  }
  
  public void resetPeekPosition()
  {
    this.peekBufferPosition = 0;
  }
  
  public <E extends Throwable> void setRetryPosition(long paramLong, E paramE)
    throws Throwable
  {
    if (paramLong >= 0L) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkArgument(bool);
      this.position = paramLong;
      throw paramE;
    }
  }
  
  public int skip(int paramInt)
    throws IOException, InterruptedException
  {
    int i = skipFromPeekBuffer(paramInt);
    int j = i;
    if (i == 0) {
      j = readFromDataSource(this.scratchSpace, 0, Math.min(paramInt, this.scratchSpace.length), 0, true);
    }
    commitBytesRead(j);
    return j;
  }
  
  public void skipFully(int paramInt)
    throws IOException, InterruptedException
  {
    skipFully(paramInt, false);
  }
  
  public boolean skipFully(int paramInt, boolean paramBoolean)
    throws IOException, InterruptedException
  {
    int j;
    for (int i = skipFromPeekBuffer(paramInt); (i < paramInt) && (i != -1); i = readFromDataSource(this.scratchSpace, -i, j, i, paramBoolean)) {
      j = Math.min(paramInt, this.scratchSpace.length + i);
    }
    commitBytesRead(i);
    if (i != -1) {}
    for (paramBoolean = true;; paramBoolean = false) {
      return paramBoolean;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/extractor/DefaultExtractorInput.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */