package com.coremedia.iso.boxes;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.List;

public abstract interface Container
{
  public abstract List<Box> getBoxes();
  
  public abstract <T extends Box> List<T> getBoxes(Class<T> paramClass);
  
  public abstract <T extends Box> List<T> getBoxes(Class<T> paramClass, boolean paramBoolean);
  
  public abstract ByteBuffer getByteBuffer(long paramLong1, long paramLong2)
    throws IOException;
  
  public abstract void setBoxes(List<Box> paramList);
  
  public abstract void writeContainer(WritableByteChannel paramWritableByteChannel)
    throws IOException;
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/boxes/Container.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */