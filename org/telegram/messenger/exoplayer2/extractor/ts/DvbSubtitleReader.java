package org.telegram.messenger.exoplayer2.extractor.ts;

import java.util.Collections;
import java.util.List;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer2.extractor.TrackOutput;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

public final class DvbSubtitleReader
  implements ElementaryStreamReader
{
  private int bytesToCheck;
  private final TrackOutput[] outputs;
  private int sampleBytesWritten;
  private long sampleTimeUs;
  private final List<TsPayloadReader.DvbSubtitleInfo> subtitleInfos;
  private boolean writingSample;
  
  public DvbSubtitleReader(List<TsPayloadReader.DvbSubtitleInfo> paramList)
  {
    this.subtitleInfos = paramList;
    this.outputs = new TrackOutput[paramList.size()];
  }
  
  private boolean checkNextByte(ParsableByteArray paramParsableByteArray, int paramInt)
  {
    boolean bool = false;
    if (paramParsableByteArray.bytesLeft() == 0) {}
    for (;;)
    {
      return bool;
      if (paramParsableByteArray.readUnsignedByte() != paramInt) {
        this.writingSample = false;
      }
      this.bytesToCheck -= 1;
      bool = this.writingSample;
    }
  }
  
  public void consume(ParsableByteArray paramParsableByteArray)
  {
    int i = 0;
    if ((!this.writingSample) || ((this.bytesToCheck == 2) && (!checkNextByte(paramParsableByteArray, 32)))) {}
    for (;;)
    {
      return;
      if ((this.bytesToCheck != 1) || (checkNextByte(paramParsableByteArray, 0)))
      {
        int j = paramParsableByteArray.getPosition();
        int k = paramParsableByteArray.bytesLeft();
        TrackOutput[] arrayOfTrackOutput = this.outputs;
        int m = arrayOfTrackOutput.length;
        while (i < m)
        {
          TrackOutput localTrackOutput = arrayOfTrackOutput[i];
          paramParsableByteArray.setPosition(j);
          localTrackOutput.sampleData(paramParsableByteArray, k);
          i++;
        }
        this.sampleBytesWritten += k;
      }
    }
  }
  
  public void createTracks(ExtractorOutput paramExtractorOutput, TsPayloadReader.TrackIdGenerator paramTrackIdGenerator)
  {
    for (int i = 0; i < this.outputs.length; i++)
    {
      TsPayloadReader.DvbSubtitleInfo localDvbSubtitleInfo = (TsPayloadReader.DvbSubtitleInfo)this.subtitleInfos.get(i);
      paramTrackIdGenerator.generateNewId();
      TrackOutput localTrackOutput = paramExtractorOutput.track(paramTrackIdGenerator.getTrackId(), 3);
      localTrackOutput.format(Format.createImageSampleFormat(paramTrackIdGenerator.getFormatId(), "application/dvbsubs", null, -1, 0, Collections.singletonList(localDvbSubtitleInfo.initializationData), localDvbSubtitleInfo.language, null));
      this.outputs[i] = localTrackOutput;
    }
  }
  
  public void packetFinished()
  {
    if (this.writingSample)
    {
      TrackOutput[] arrayOfTrackOutput = this.outputs;
      int i = arrayOfTrackOutput.length;
      for (int j = 0; j < i; j++) {
        arrayOfTrackOutput[j].sampleMetadata(this.sampleTimeUs, 1, this.sampleBytesWritten, 0, null);
      }
      this.writingSample = false;
    }
  }
  
  public void packetStarted(long paramLong, boolean paramBoolean)
  {
    if (!paramBoolean) {}
    for (;;)
    {
      return;
      this.writingSample = true;
      this.sampleTimeUs = paramLong;
      this.sampleBytesWritten = 0;
      this.bytesToCheck = 2;
    }
  }
  
  public void seek()
  {
    this.writingSample = false;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/extractor/ts/DvbSubtitleReader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */