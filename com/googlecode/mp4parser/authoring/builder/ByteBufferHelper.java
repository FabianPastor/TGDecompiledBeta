package com.googlecode.mp4parser.authoring.builder;

import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ByteBufferHelper
{
  public static List<ByteBuffer> mergeAdjacentBuffers(List<ByteBuffer> paramList)
  {
    ArrayList localArrayList = new ArrayList(paramList.size());
    paramList = paramList.iterator();
    for (;;)
    {
      if (!paramList.hasNext()) {
        return localArrayList;
      }
      ByteBuffer localByteBuffer1 = (ByteBuffer)paramList.next();
      int i = localArrayList.size() - 1;
      ByteBuffer localByteBuffer2;
      if ((i >= 0) && (localByteBuffer1.hasArray()) && (((ByteBuffer)localArrayList.get(i)).hasArray()) && (localByteBuffer1.array() == ((ByteBuffer)localArrayList.get(i)).array()))
      {
        int j = ((ByteBuffer)localArrayList.get(i)).arrayOffset();
        if (((ByteBuffer)localArrayList.get(i)).limit() + j == localByteBuffer1.arrayOffset())
        {
          localByteBuffer2 = (ByteBuffer)localArrayList.remove(i);
          localArrayList.add(ByteBuffer.wrap(localByteBuffer1.array(), localByteBuffer2.arrayOffset(), localByteBuffer2.limit() + localByteBuffer1.limit()).slice());
          continue;
        }
      }
      if ((i >= 0) && ((localByteBuffer1 instanceof MappedByteBuffer)) && ((localArrayList.get(i) instanceof MappedByteBuffer)) && (((ByteBuffer)localArrayList.get(i)).limit() == ((ByteBuffer)localArrayList.get(i)).capacity() - localByteBuffer1.capacity()))
      {
        localByteBuffer2 = (ByteBuffer)localArrayList.get(i);
        localByteBuffer2.limit(localByteBuffer1.limit() + localByteBuffer2.limit());
      }
      else
      {
        localByteBuffer1.reset();
        localArrayList.add(localByteBuffer1);
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/authoring/builder/ByteBufferHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */