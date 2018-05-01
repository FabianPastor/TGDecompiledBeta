package com.coremedia.iso;

import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.FileDataSourceImpl;
import com.googlecode.mp4parser.util.Path;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class BoxReplacer
{
  static
  {
    if (!BoxReplacer.class.desiredAssertionStatus()) {}
    for (boolean bool = true;; bool = false)
    {
      $assertionsDisabled = bool;
      return;
    }
  }
  
  public static void replace(Map<String, Box> paramMap, File paramFile)
    throws IOException
  {
    Object localObject = new IsoFile(new FileDataSourceImpl(new RandomAccessFile(paramFile, "r").getChannel()));
    HashMap localHashMap1 = new HashMap();
    HashMap localHashMap2 = new HashMap();
    Iterator localIterator = paramMap.entrySet().iterator();
    if (!localIterator.hasNext())
    {
      ((IsoFile)localObject).close();
      paramFile = new RandomAccessFile(paramFile, "rw").getChannel();
      paramMap = localHashMap1.keySet().iterator();
    }
    for (;;)
    {
      if (!paramMap.hasNext())
      {
        paramFile.close();
        return;
        paramMap = (Map.Entry)localIterator.next();
        localBox = Path.getPath((Container)localObject, (String)paramMap.getKey());
        localHashMap1.put(Path.createPath(localBox), (Box)paramMap.getValue());
        localHashMap2.put(Path.createPath(localBox), Long.valueOf(localBox.getOffset()));
        if (($assertionsDisabled) || (localBox.getSize() == ((Box)paramMap.getValue()).getSize())) {
          break;
        }
        throw new AssertionError();
      }
      localObject = (String)paramMap.next();
      Box localBox = (Box)localHashMap1.get(localObject);
      paramFile.position(((Long)localHashMap2.get(localObject)).longValue());
      localBox.getBox(paramFile);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/BoxReplacer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */