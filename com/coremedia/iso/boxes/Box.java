package com.coremedia.iso.boxes;

import com.coremedia.iso.BoxParser;
import com.googlecode.mp4parser.DataSource;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

public abstract interface Box
{
  public abstract void getBox(WritableByteChannel paramWritableByteChannel)
    throws IOException;
  
  public abstract long getOffset();
  
  public abstract Container getParent();
  
  public abstract long getSize();
  
  public abstract String getType();
  
  public abstract void parse(DataSource paramDataSource, ByteBuffer paramByteBuffer, long paramLong, BoxParser paramBoxParser)
    throws IOException;
  
  public abstract void setParent(Container paramContainer);
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/boxes/Box.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */