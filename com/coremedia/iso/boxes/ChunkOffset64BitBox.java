package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import com.googlecode.mp4parser.util.CastUtils;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.reflect.Factory;

public class ChunkOffset64BitBox
  extends ChunkOffsetBox
{
  public static final String TYPE = "co64";
  private long[] chunkOffsets;
  
  static {}
  
  public ChunkOffset64BitBox()
  {
    super("co64");
  }
  
  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    int j = CastUtils.l2i(IsoTypeReader.readUInt32(paramByteBuffer));
    this.chunkOffsets = new long[j];
    int i = 0;
    for (;;)
    {
      if (i >= j) {
        return;
      }
      this.chunkOffsets[i] = IsoTypeReader.readUInt64(paramByteBuffer);
      i += 1;
    }
  }
  
  public long[] getChunkOffsets()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.chunkOffsets;
  }
  
  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.chunkOffsets.length);
    long[] arrayOfLong = this.chunkOffsets;
    int j = arrayOfLong.length;
    int i = 0;
    for (;;)
    {
      if (i >= j) {
        return;
      }
      IsoTypeWriter.writeUInt64(paramByteBuffer, arrayOfLong[i]);
      i += 1;
    }
  }
  
  protected long getContentSize()
  {
    return this.chunkOffsets.length * 8 + 8;
  }
  
  public void setChunkOffsets(long[] paramArrayOfLong)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this, paramArrayOfLong);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.chunkOffsets = paramArrayOfLong;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/boxes/ChunkOffset64BitBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */