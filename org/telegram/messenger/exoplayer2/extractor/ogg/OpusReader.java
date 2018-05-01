package org.telegram.messenger.exoplayer2.extractor.ogg;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;
import org.telegram.messenger.exoplayer2.util.Util;

final class OpusReader
  extends StreamReader
{
  private static final int DEFAULT_SEEK_PRE_ROLL_SAMPLES = 3840;
  private static final int OPUS_CODE = Util.getIntegerCodeForString("Opus");
  private static final byte[] OPUS_SIGNATURE = { 79, 112, 117, 115, 72, 101, 97, 100 };
  private static final int SAMPLE_RATE = 48000;
  private boolean headerRead;
  
  private long getPacketDurationUs(byte[] paramArrayOfByte)
  {
    int i = paramArrayOfByte[0] & 0xFF;
    int j;
    int k;
    switch (i & 0x3)
    {
    default: 
      j = paramArrayOfByte[1] & 0x3F;
      i >>= 3;
      k = i & 0x3;
      if (i >= 16) {
        i = 2500 << k;
      }
      break;
    }
    for (;;)
    {
      return j * i;
      j = 1;
      break;
      j = 2;
      break;
      if (i >= 12) {
        i = 10000 << (k & 0x1);
      } else if (k == 3) {
        i = 60000;
      } else {
        i = 10000 << k;
      }
    }
  }
  
  private void putNativeOrderLong(List<byte[]> paramList, int paramInt)
  {
    long l = paramInt * 1000000000L / 48000L;
    paramList.add(ByteBuffer.allocate(8).order(ByteOrder.nativeOrder()).putLong(l).array());
  }
  
  public static boolean verifyBitstreamType(ParsableByteArray paramParsableByteArray)
  {
    boolean bool = false;
    if (paramParsableByteArray.bytesLeft() < OPUS_SIGNATURE.length) {}
    for (;;)
    {
      return bool;
      byte[] arrayOfByte = new byte[OPUS_SIGNATURE.length];
      paramParsableByteArray.readBytes(arrayOfByte, 0, OPUS_SIGNATURE.length);
      bool = Arrays.equals(arrayOfByte, OPUS_SIGNATURE);
    }
  }
  
  protected long preparePayload(ParsableByteArray paramParsableByteArray)
  {
    return convertTimeToGranule(getPacketDurationUs(paramParsableByteArray.data));
  }
  
  protected boolean readHeaders(ParsableByteArray paramParsableByteArray, long paramLong, StreamReader.SetupData paramSetupData)
    throws IOException, InterruptedException
  {
    if (!this.headerRead)
    {
      byte[] arrayOfByte = Arrays.copyOf(paramParsableByteArray.data, paramParsableByteArray.limit());
      int i = arrayOfByte[9];
      int j = arrayOfByte[11];
      int k = arrayOfByte[10];
      paramParsableByteArray = new ArrayList(3);
      paramParsableByteArray.add(arrayOfByte);
      putNativeOrderLong(paramParsableByteArray, (j & 0xFF) << 8 | k & 0xFF);
      putNativeOrderLong(paramParsableByteArray, 3840);
      paramSetupData.format = Format.createAudioSampleFormat(null, "audio/opus", null, -1, -1, i & 0xFF, 48000, paramParsableByteArray, null, 0, null);
      this.headerRead = true;
      bool = true;
      return bool;
    }
    if (paramParsableByteArray.readInt() == OPUS_CODE) {}
    for (boolean bool = true;; bool = false)
    {
      paramParsableByteArray.setPosition(0);
      break;
    }
  }
  
  protected void reset(boolean paramBoolean)
  {
    super.reset(paramBoolean);
    if (paramBoolean) {
      this.headerRead = false;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/extractor/ogg/OpusReader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */