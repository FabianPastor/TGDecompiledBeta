package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import com.googlecode.mp4parser.util.CastUtils;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.reflect.Factory;

public class StaticChunkOffsetBox
  extends ChunkOffsetBox
{
  public static final String TYPE = "stco";
  private long[] chunkOffsets = new long[0];
  
  static {}
  
  public StaticChunkOffsetBox()
  {
    super("stco");
  }
  
  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    int i = CastUtils.l2i(IsoTypeReader.readUInt32(paramByteBuffer));
    this.chunkOffsets = new long[i];
    for (int j = 0;; j++)
    {
      if (j >= i) {
        return;
      }
      this.chunkOffsets[j] = IsoTypeReader.readUInt32(paramByteBuffer);
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
    int i = arrayOfLong.length;
    for (int j = 0;; j++)
    {
      if (j >= i) {
        return;
      }
      IsoTypeWriter.writeUInt32(paramByteBuffer, arrayOfLong[j]);
    }
  }
  
  protected long getContentSize()
  {
    return this.chunkOffsets.length * 4 + 8;
  }
  
  public void setChunkOffsets(long[] paramArrayOfLong)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this, paramArrayOfLong);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.chunkOffsets = paramArrayOfLong;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/boxes/StaticChunkOffsetBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */