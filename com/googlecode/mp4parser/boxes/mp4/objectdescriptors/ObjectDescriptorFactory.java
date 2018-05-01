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
    int i = ((Descriptor)localObject).objectTypeIndication();
    Map localMap = (Map)descriptorRegistry.get(Integer.valueOf(i));
    localObject = localMap;
    if (localMap == null) {
      localObject = new HashMap();
    }
    int j = arrayOfInt.length;
    for (int k = 0;; k++)
    {
      if (k >= j)
      {
        descriptorRegistry.put(Integer.valueOf(i), localObject);
        break;
      }
      ((Map)localObject).put(Integer.valueOf(arrayOfInt[k]), localClass);
    }
  }
  
  public static BaseDescriptor createFrom(int paramInt, ByteBuffer paramByteBuffer)
    throws IOException
  {
    int i = IsoTypeReader.readUInt8(paramByteBuffer);
    Object localObject1 = (Map)descriptorRegistry.get(Integer.valueOf(paramInt));
    Object localObject2 = localObject1;
    if (localObject1 == null) {
      localObject2 = (Map)descriptorRegistry.get(Integer.valueOf(-1));
    }
    localObject1 = (Class)((Map)localObject2).get(Integer.valueOf(i));
    if ((localObject1 == null) || (((Class)localObject1).isInterface()) || (Modifier.isAbstract(((Class)localObject1).getModifiers())))
    {
      log.warning("No ObjectDescriptor found for objectTypeIndication " + Integer.toHexString(paramInt) + " and tag " + Integer.toHexString(i) + " found: " + localObject1);
      localObject2 = new UnknownDescriptor();
    }
    for (;;)
    {
      ((BaseDescriptor)localObject2).parse(i, paramByteBuffer);
      return (BaseDescriptor)localObject2;
      try
      {
        localObject2 = (BaseDescriptor)((Class)localObject1).newInstance();
      }
      catch (Exception paramByteBuffer)
      {
        log.log(Level.SEVERE, "Couldn't instantiate BaseDescriptor class " + localObject1 + " for objectTypeIndication " + paramInt + " and tag " + i, paramByteBuffer);
        throw new RuntimeException(paramByteBuffer);
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/boxes/mp4/objectdescriptors/ObjectDescriptorFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */