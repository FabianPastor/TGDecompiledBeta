package org.telegram.messenger.exoplayer.extractor.ts;

import org.telegram.messenger.exoplayer.MediaFormat;
import org.telegram.messenger.exoplayer.extractor.TrackOutput;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;

final class Id3Reader
  extends ElementaryStreamReader
{
  private static final int ID3_HEADER_SIZE = 10;
  private final ParsableByteArray id3Header;
  private int sampleBytesRead;
  private int sampleSize;
  private long sampleTimeUs;
  private boolean writingSample;
  
  public Id3Reader(TrackOutput paramTrackOutput)
  {
    super(paramTrackOutput);
    paramTrackOutput.format(MediaFormat.createId3Format());
    this.id3Header = new ParsableByteArray(10);
  }
  
  public void consume(ParsableByteArray paramParsableByteArray)
  {
    if (!this.writingSample) {
      return;
    }
    int i = paramParsableByteArray.bytesLeft();
    if (this.sampleBytesRead < 10)
    {
      int j = Math.min(i, 10 - this.sampleBytesRead);
      System.arraycopy(paramParsableByteArray.data, paramParsableByteArray.getPosition(), this.id3Header.data, this.sampleBytesRead, j);
      if (this.sampleBytesRead + j == 10)
      {
        this.id3Header.setPosition(6);
        this.sampleSize = (this.id3Header.readSynchSafeInt() + 10);
      }
    }
    i = Math.min(i, this.sampleSize - this.sampleBytesRead);
    this.output.sampleData(paramParsableByteArray, i);
    this.sampleBytesRead += i;
  }
  
  public void packetFinished()
  {
    if ((!this.writingSample) || (this.sampleSize == 0) || (this.sampleBytesRead != this.sampleSize)) {
      return;
    }
    this.output.sampleMetadata(this.sampleTimeUs, 1, this.sampleSize, 0, null);
    this.writingSample = false;
  }
  
  public void packetStarted(long paramLong, boolean paramBoolean)
  {
    if (!paramBoolean) {
      return;
    }
    this.writingSample = true;
    this.sampleTimeUs = paramLong;
    this.sampleSize = 0;
    this.sampleBytesRead = 0;
  }
  
  public void seek()
  {
    this.writingSample = false;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/extractor/ts/Id3Reader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */