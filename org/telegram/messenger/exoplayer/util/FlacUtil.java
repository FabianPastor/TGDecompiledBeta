package org.telegram.messenger.exoplayer.util;

public final class FlacUtil
{
  private static final int FRAME_HEADER_SAMPLE_NUMBER_OFFSET = 4;
  
  public static long extractSampleTimestamp(FlacStreamInfo paramFlacStreamInfo, ParsableByteArray paramParsableByteArray)
  {
    paramParsableByteArray.skipBytes(4);
    long l2 = paramParsableByteArray.readUTF8EncodedLong();
    long l1 = l2;
    if (paramFlacStreamInfo.minBlockSize == paramFlacStreamInfo.maxBlockSize) {
      l1 = l2 * paramFlacStreamInfo.minBlockSize;
    }
    return 1000000L * l1 / paramFlacStreamInfo.sampleRate;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/util/FlacUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */