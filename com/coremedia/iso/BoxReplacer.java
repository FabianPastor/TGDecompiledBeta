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
    Object localObject1 = new IsoFile(new FileDataSourceImpl(new RandomAccessFile(paramFile, "r").getChannel()));
    HashMap localHashMap1 = new HashMap();
    HashMap localHashMap2 = new HashMap();
    paramMap = paramMap.entrySet().iterator();
    if (!paramMap.hasNext())
    {
      ((IsoFile)localObject1).close();
      paramMap = new RandomAccessFile(paramFile, "rw").getChannel();
      paramFile = localHashMap1.keySet().iterator();
    }
    for (;;)
    {
      if (!paramFile.hasNext())
      {
        paramMap.close();
        return;
        localObject2 = (Map.Entry)paramMap.next();
        Box localBox = Path.getPath((Container)localObject1, (String)((Map.Entry)localObject2).getKey());
        localHashMap1.put(Path.createPath(localBox), (Box)((Map.Entry)localObject2).getValue());
        localHashMap2.put(Path.createPath(localBox), Long.valueOf(localBox.getOffset()));
        if (($assertionsDisabled) || (localBox.getSize() == ((Box)((Map.Entry)localObject2).getValue()).getSize())) {
          break;
        }
        throw new AssertionError();
      }
      localObject1 = (String)paramFile.next();
      Object localObject2 = (Box)localHashMap1.get(localObject1);
      paramMap.position(((Long)localHashMap2.get(localObject1)).longValue());
      ((Box)localObject2).getBox(paramMap);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/BoxReplacer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */