package com.coremedia.iso.boxes;

import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.reflect.Factory;

public abstract class ChunkOffsetBox
  extends AbstractFullBox
{
  static {}
  
  public ChunkOffsetBox(String paramString)
  {
    super(paramString);
  }
  
  public abstract long[] getChunkOffsets();
  
  public abstract void setChunkOffsets(long[] paramArrayOfLong);
  
  public String toString()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return getClass().getSimpleName() + "[entryCount=" + getChunkOffsets().length + "]";
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/boxes/ChunkOffsetBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */