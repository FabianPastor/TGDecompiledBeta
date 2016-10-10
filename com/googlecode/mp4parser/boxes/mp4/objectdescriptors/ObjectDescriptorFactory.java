package com.googlecode.mp4parser.boxes.mp4.objectdescriptors;

import com.coremedia.iso.IsoTypeReader;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ObjectDescriptorFactory
{
  protected static Map<Integer, Map<Integer, Class<? extends BaseDescriptor>>> descriptorRegistry;
  protected static Logger log = Logger.getLogger(ObjectDescriptorFactory.class.getName());
  
  static
  {
    descriptorRegistry = new HashMap();
    Object localObject = new HashSet();
    ((Set)localObject).add(DecoderSpecificInfo.class);
    ((Set)localObject).add(SLConfigDescriptor.class);
    ((Set)localObject).add(BaseDescriptor.class);
    ((Set)localObject).add(ExtensionDescriptor.class);
    ((Set)localObject).add(ObjectDescriptorBase.class);
    ((Set)localObject).add(ProfileLevelIndicationDescriptor.class);
    ((Set)localObject).add(AudioSpecificConfig.class);
    ((Set)localObject).add(ExtensionProfileLevelDescriptor.class);
    ((Set)localObject).add(ESDescriptor.class);
    ((Set)localObject).add(DecoderConfigDescriptor.class);
    Iterator localIterator = ((Set)localObject).iterator();
    if (!localIterator.hasNext()) {
      return;
    }
    Class localClass = (Class)localIterator.next();
    localObject = (Descriptor)localClass.getAnnotation(Descriptor.class);
    int[] arrayOfInt = ((Descriptor)localObject).tags();
    int j = ((Descriptor)localObject).objectTypeIndication();
    Map localMap = (Map)descriptorRegistry.get(Integer.valueOf(j));
    localObject = localMap;
    if (localMap == null) {
      localObject = new HashMap();
    }
    int k = arrayOfInt.length;
    int i = 0;
    for (;;)
    {
      if (i >= k)
      {
        descriptorRegistry.put(Integer.valueOf(j), localObject);
        break;
      }
      ((Map)localObject).put(Integer.valueOf(arrayOfInt[i]), localClass);
      i += 1;
    }
  }
  
  public static BaseDescriptor createFrom(int paramInt, ByteBuffer paramByteBuffer)
    throws IOException
  {
    int i = IsoTypeReader.readUInt8(paramByteBuffer);
    Object localObject2 = (Map)descriptorRegistry.get(Integer.valueOf(paramInt));
    Object localObject1 = localObject2;
    if (localObject2 == null) {
      localObject1 = (Map)descriptorRegistry.get(Integer.valueOf(-1));
    }
    localObject2 = (Class)((Map)localObject1).get(Integer.valueOf(i));
    if ((localObject2 == null) || (((Class)localObject2).isInterface()) || (Modifier.isAbstract(((Class)localObject2).getModifiers())))
    {
      log.warning("No ObjectDescriptor found for objectTypeIndication " + Integer.toHexString(paramInt) + " and tag " + Integer.toHexString(i) + " found: " + localObject2);
      localObject1 = new UnknownDescriptor();
    }
    for (;;)
    {
      ((BaseDescriptor)localObject1).parse(i, paramByteBuffer);
      return (BaseDescriptor)localObject1;
      try
      {
        localObject1 = (BaseDescriptor)((Class)localObject2).newInstance();
      }
      catch (Exception paramByteBuffer)
      {
        log.log(Level.SEVERE, "Couldn't instantiate BaseDescriptor class " + localObject2 + " for objectTypeIndication " + paramInt + " and tag " + i, paramByteBuffer);
        throw new RuntimeException(paramByteBuffer);
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/boxes/mp4/objectdescriptors/ObjectDescriptorFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */