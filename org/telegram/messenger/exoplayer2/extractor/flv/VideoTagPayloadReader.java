package org.telegram.messenger.exoplayer2.extractor.flv;

import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.ParserException;
import org.telegram.messenger.exoplayer2.extractor.TrackOutput;
import org.telegram.messenger.exoplayer2.util.NalUnitUtil;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;
import org.telegram.messenger.exoplayer2.video.AvcConfig;

final class VideoTagPayloadReader
  extends TagPayloadReader
{
  private static final int AVC_PACKET_TYPE_AVC_NALU = 1;
  private static final int AVC_PACKET_TYPE_SEQUENCE_HEADER = 0;
  private static final int VIDEO_CODEC_AVC = 7;
  private static final int VIDEO_FRAME_KEYFRAME = 1;
  private static final int VIDEO_FRAME_VIDEO_INFO = 5;
  private int frameType;
  private boolean hasOutputFormat;
  private final ParsableByteArray nalLength = new ParsableByteArray(4);
  private final ParsableByteArray nalStartCode = new ParsableByteArray(NalUnitUtil.NAL_START_CODE);
  private int nalUnitLengthFieldLength;
  
  public VideoTagPayloadReader(TrackOutput paramTrackOutput)
  {
    super(paramTrackOutput);
  }
  
  protected boolean parseHeader(ParsableByteArray paramParsableByteArray)
    throws TagPayloadReader.UnsupportedFormatException
  {
    int i = paramParsableByteArray.readUnsignedByte();
    int j = i >> 4 & 0xF;
    i &= 0xF;
    if (i != 7) {
      throw new TagPayloadReader.UnsupportedFormatException("Video format not supported: " + i);
    }
    this.frameType = j;
    if (j != 5) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  protected void parsePayload(ParsableByteArray paramParsableByteArray, long paramLong)
    throws ParserException
  {
    int i = paramParsableByteArray.readUnsignedByte();
    long l = paramParsableByteArray.readInt24();
    Object localObject;
    if ((i == 0) && (!this.hasOutputFormat))
    {
      localObject = new ParsableByteArray(new byte[paramParsableByteArray.bytesLeft()]);
      paramParsableByteArray.readBytes(((ParsableByteArray)localObject).data, 0, paramParsableByteArray.bytesLeft());
      paramParsableByteArray = AvcConfig.parse((ParsableByteArray)localObject);
      this.nalUnitLengthFieldLength = paramParsableByteArray.nalUnitLengthFieldLength;
      paramParsableByteArray = Format.createVideoSampleFormat(null, "video/avc", null, -1, -1, paramParsableByteArray.width, paramParsableByteArray.height, -1.0F, paramParsableByteArray.initializationData, -1, paramParsableByteArray.pixelWidthAspectRatio, null);
      this.output.format(paramParsableByteArray);
      this.hasOutputFormat = true;
      return;
    }
    if ((i == 1) && (this.hasOutputFormat))
    {
      localObject = this.nalLength.data;
      localObject[0] = ((byte)0);
      localObject[1] = ((byte)0);
      localObject[2] = ((byte)0);
      j = this.nalUnitLengthFieldLength;
      int k;
      for (i = 0; paramParsableByteArray.bytesLeft() > 0; i = i + 4 + k)
      {
        paramParsableByteArray.readBytes(this.nalLength.data, 4 - j, this.nalUnitLengthFieldLength);
        this.nalLength.setPosition(0);
        k = this.nalLength.readUnsignedIntToInt();
        this.nalStartCode.setPosition(0);
        this.output.sampleData(this.nalStartCode, 4);
        this.output.sampleData(paramParsableByteArray, k);
      }
      paramParsableByteArray = this.output;
      if (this.frameType != 1) {
        break label291;
      }
    }
    label291:
    for (int j = 1;; j = 0)
    {
      paramParsableByteArray.sampleMetadata(paramLong + l * 1000L, j, i, 0, null);
      break;
      break;
    }
  }
  
  public void seek() {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/extractor/flv/VideoTagPayloadReader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */