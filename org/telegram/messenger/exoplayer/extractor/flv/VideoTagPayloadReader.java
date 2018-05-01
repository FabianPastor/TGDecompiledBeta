package org.telegram.messenger.exoplayer.extractor.flv;

import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.exoplayer.MediaFormat;
import org.telegram.messenger.exoplayer.ParserException;
import org.telegram.messenger.exoplayer.extractor.TrackOutput;
import org.telegram.messenger.exoplayer.util.Assertions;
import org.telegram.messenger.exoplayer.util.NalUnitUtil;
import org.telegram.messenger.exoplayer.util.NalUnitUtil.SpsData;
import org.telegram.messenger.exoplayer.util.ParsableBitArray;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;

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
  
  private AvcSequenceHeaderData parseAvcCodecPrivate(ParsableByteArray paramParsableByteArray)
    throws ParserException
  {
    paramParsableByteArray.setPosition(4);
    int k = (paramParsableByteArray.readUnsignedByte() & 0x3) + 1;
    if (k != 3) {}
    ArrayList localArrayList;
    int m;
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      localArrayList = new ArrayList();
      m = paramParsableByteArray.readUnsignedByte() & 0x1F;
      i = 0;
      while (i < m)
      {
        localArrayList.add(NalUnitUtil.parseChildNalUnit(paramParsableByteArray));
        i += 1;
      }
    }
    int j = paramParsableByteArray.readUnsignedByte();
    int i = 0;
    while (i < j)
    {
      localArrayList.add(NalUnitUtil.parseChildNalUnit(paramParsableByteArray));
      i += 1;
    }
    float f = 1.0F;
    i = -1;
    j = -1;
    if (m > 0)
    {
      paramParsableByteArray = new ParsableBitArray((byte[])localArrayList.get(0));
      paramParsableByteArray.setPosition((k + 1) * 8);
      paramParsableByteArray = NalUnitUtil.parseSpsNalUnit(paramParsableByteArray);
      i = paramParsableByteArray.width;
      j = paramParsableByteArray.height;
      f = paramParsableByteArray.pixelWidthAspectRatio;
    }
    return new AvcSequenceHeaderData(localArrayList, k, i, j, f);
  }
  
  protected boolean parseHeader(ParsableByteArray paramParsableByteArray)
    throws TagPayloadReader.UnsupportedFormatException
  {
    int j = paramParsableByteArray.readUnsignedByte();
    int i = j >> 4 & 0xF;
    j &= 0xF;
    if (j != 7) {
      throw new TagPayloadReader.UnsupportedFormatException("Video format not supported: " + j);
    }
    this.frameType = i;
    return i != 5;
  }
  
  protected void parsePayload(ParsableByteArray paramParsableByteArray, long paramLong)
    throws ParserException
  {
    int i = paramParsableByteArray.readUnsignedByte();
    long l = paramParsableByteArray.readUnsignedInt24();
    if ((i == 0) && (!this.hasOutputFormat))
    {
      localObject = new ParsableByteArray(new byte[paramParsableByteArray.bytesLeft()]);
      paramParsableByteArray.readBytes(((ParsableByteArray)localObject).data, 0, paramParsableByteArray.bytesLeft());
      paramParsableByteArray = parseAvcCodecPrivate((ParsableByteArray)localObject);
      this.nalUnitLengthFieldLength = paramParsableByteArray.nalUnitLengthFieldLength;
      paramParsableByteArray = MediaFormat.createVideoFormat(null, "video/avc", -1, -1, getDurationUs(), paramParsableByteArray.width, paramParsableByteArray.height, paramParsableByteArray.initializationData, -1, paramParsableByteArray.pixelWidthAspectRatio);
      this.output.format(paramParsableByteArray);
      this.hasOutputFormat = true;
    }
    while (i != 1) {
      return;
    }
    Object localObject = this.nalLength.data;
    localObject[0] = 0;
    localObject[1] = 0;
    localObject[2] = 0;
    int j = this.nalUnitLengthFieldLength;
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
    if (this.frameType == 1) {}
    for (j = 1;; j = 0)
    {
      paramParsableByteArray.sampleMetadata(paramLong + l * 1000L, j, i, 0, null);
      return;
    }
  }
  
  public void seek() {}
  
  private static final class AvcSequenceHeaderData
  {
    public final int height;
    public final List<byte[]> initializationData;
    public final int nalUnitLengthFieldLength;
    public final float pixelWidthAspectRatio;
    public final int width;
    
    public AvcSequenceHeaderData(List<byte[]> paramList, int paramInt1, int paramInt2, int paramInt3, float paramFloat)
    {
      this.initializationData = paramList;
      this.nalUnitLengthFieldLength = paramInt1;
      this.pixelWidthAspectRatio = paramFloat;
      this.width = paramInt2;
      this.height = paramInt3;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/extractor/flv/VideoTagPayloadReader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */