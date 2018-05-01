package com.google.protobuf;

import java.lang.reflect.Field;
import java.nio.Buffer;
import java.nio.ByteOrder;
import java.security.AccessController;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.misc.Unsafe;

final class zze
{
  private static final Logger logger;
  private static final boolean zzcrM;
  private static final long zzcrN;
  private static final Unsafe zzcrT;
  private static final Class<?> zzcrU;
  private static final boolean zzcrV;
  private static final boolean zzcrW;
  private static final boolean zzcrX;
  private static final zzd zzcrY;
  private static final boolean zzcrZ;
  private static final boolean zzcsa;
  private static final long zzcsb;
  private static final boolean zzcsc;
  
  static
  {
    boolean bool2 = true;
    logger = Logger.getLogger(zze.class.getName());
    zzcrT = zzLv();
    zzcrU = zzhP("libcore.io.Memory");
    Object localObject;
    label68:
    int i;
    label109:
    label134:
    long l;
    if (zzhP("org.robolectric.Robolectric") != null)
    {
      bool1 = true;
      zzcrV = bool1;
      zzcrW = zzg(Long.TYPE);
      zzcrX = zzg(Integer.TYPE);
      if (zzcrT != null) {
        break label174;
      }
      localObject = null;
      zzcrY = (zzd)localObject;
      zzcrZ = zzLy();
      zzcrM = zzLw();
      zzcsa = zzLx();
      if (!zzcrM) {
        break label243;
      }
      i = zzcrY.zzcsd.arrayBaseOffset(byte[].class);
      zzcrN = i;
      if (!zzLz()) {
        break label248;
      }
      localObject = zza(Buffer.class, "effectiveDirectAddress");
      if (localObject == null) {
        break label248;
      }
      if ((localObject != null) && (zzcrY != null)) {
        break label260;
      }
      l = -1L;
      label149:
      zzcsb = l;
      if (ByteOrder.nativeOrder() != ByteOrder.BIG_ENDIAN) {
        break label275;
      }
    }
    label174:
    label243:
    label248:
    label260:
    label275:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      zzcsc = bool1;
      return;
      bool1 = false;
      break;
      if (zzLz())
      {
        if (zzcrW)
        {
          localObject = new zzb(zzcrT);
          break label68;
        }
        if (zzcrX)
        {
          localObject = new zza(zzcrT);
          break label68;
        }
        localObject = null;
        break label68;
      }
      localObject = new zzc(zzcrT);
      break label68;
      i = -1;
      break label109;
      localObject = zza(Buffer.class, "address");
      break label134;
      l = zzcrY.zzcsd.objectFieldOffset((Field)localObject);
      break label149;
    }
  }
  
  static boolean zzLt()
  {
    return zzcrM;
  }
  
  static long zzLu()
  {
    return zzcrN;
  }
  
  private static Unsafe zzLv()
  {
    try
    {
      Unsafe localUnsafe = (Unsafe)AccessController.doPrivileged(new zzf());
      return localUnsafe;
    }
    catch (Throwable localThrowable) {}
    return null;
  }
  
  private static boolean zzLw()
  {
    if (zzcrT == null) {
      return false;
    }
    try
    {
      localObject = zzcrT.getClass();
      ((Class)localObject).getMethod("objectFieldOffset", new Class[] { Field.class });
      ((Class)localObject).getMethod("arrayBaseOffset", new Class[] { Class.class });
      ((Class)localObject).getMethod("getInt", new Class[] { Object.class, Long.TYPE });
      ((Class)localObject).getMethod("putInt", new Class[] { Object.class, Long.TYPE, Integer.TYPE });
      ((Class)localObject).getMethod("getLong", new Class[] { Object.class, Long.TYPE });
      ((Class)localObject).getMethod("putLong", new Class[] { Object.class, Long.TYPE, Long.TYPE });
      ((Class)localObject).getMethod("getObject", new Class[] { Object.class, Long.TYPE });
      ((Class)localObject).getMethod("putObject", new Class[] { Object.class, Long.TYPE, Object.class });
      if (zzLz()) {
        return true;
      }
      ((Class)localObject).getMethod("getByte", new Class[] { Object.class, Long.TYPE });
      ((Class)localObject).getMethod("putByte", new Class[] { Object.class, Long.TYPE, Byte.TYPE });
      ((Class)localObject).getMethod("getBoolean", new Class[] { Object.class, Long.TYPE });
      ((Class)localObject).getMethod("putBoolean", new Class[] { Object.class, Long.TYPE, Boolean.TYPE });
      ((Class)localObject).getMethod("getFloat", new Class[] { Object.class, Long.TYPE });
      ((Class)localObject).getMethod("putFloat", new Class[] { Object.class, Long.TYPE, Float.TYPE });
      ((Class)localObject).getMethod("getDouble", new Class[] { Object.class, Long.TYPE });
      ((Class)localObject).getMethod("putDouble", new Class[] { Object.class, Long.TYPE, Double.TYPE });
      return true;
    }
    catch (Throwable localThrowable)
    {
      Object localObject = logger;
      Level localLevel = Level.WARNING;
      String str = String.valueOf(localThrowable);
      ((Logger)localObject).logp(localLevel, "com.google.protobuf.UnsafeUtil", "supportsUnsafeArrayOperations", String.valueOf(str).length() + 71 + "platform method missing - proto runtime falling back to safer methods: " + str);
    }
    return false;
  }
  
  private static boolean zzLx()
  {
    if (zzcrT == null) {
      return false;
    }
    try
    {
      zzcrT.getClass().getMethod("copyMemory", new Class[] { Object.class, Long.TYPE, Object.class, Long.TYPE, Long.TYPE });
      return true;
    }
    catch (Throwable localThrowable)
    {
      logger.logp(Level.WARNING, "com.google.protobuf.UnsafeUtil", "supportsUnsafeCopyMemory", "copyMemory is missing from platform - proto runtime falling back to safer methods.");
    }
    return false;
  }
  
  private static boolean zzLy()
  {
    if (zzcrT == null) {
      return false;
    }
    try
    {
      localObject = zzcrT.getClass();
      ((Class)localObject).getMethod("objectFieldOffset", new Class[] { Field.class });
      ((Class)localObject).getMethod("getLong", new Class[] { Object.class, Long.TYPE });
      if (zzLz()) {
        return true;
      }
      ((Class)localObject).getMethod("getByte", new Class[] { Long.TYPE });
      ((Class)localObject).getMethod("putByte", new Class[] { Long.TYPE, Byte.TYPE });
      ((Class)localObject).getMethod("getInt", new Class[] { Long.TYPE });
      ((Class)localObject).getMethod("putInt", new Class[] { Long.TYPE, Integer.TYPE });
      ((Class)localObject).getMethod("getLong", new Class[] { Long.TYPE });
      ((Class)localObject).getMethod("putLong", new Class[] { Long.TYPE, Long.TYPE });
      ((Class)localObject).getMethod("copyMemory", new Class[] { Long.TYPE, Long.TYPE, Long.TYPE });
      return true;
    }
    catch (Throwable localThrowable)
    {
      Object localObject = logger;
      Level localLevel = Level.WARNING;
      String str = String.valueOf(localThrowable);
      ((Logger)localObject).logp(localLevel, "com.google.protobuf.UnsafeUtil", "supportsUnsafeByteBufferOperations", String.valueOf(str).length() + 71 + "platform method missing - proto runtime falling back to safer methods: " + str);
    }
    return false;
  }
  
  private static boolean zzLz()
  {
    return (zzcrU != null) && (!zzcrV);
  }
  
  private static Field zza(Class<?> paramClass, String paramString)
  {
    try
    {
      paramClass = paramClass.getDeclaredField(paramString);
      paramClass.setAccessible(true);
      return paramClass;
    }
    catch (Throwable paramClass) {}
    return null;
  }
  
  private static boolean zzg(Class<?> paramClass)
  {
    if (!zzLz()) {
      return false;
    }
    try
    {
      Class localClass = zzcrU;
      localClass.getMethod("peekLong", new Class[] { paramClass, Boolean.TYPE });
      localClass.getMethod("pokeLong", new Class[] { paramClass, Long.TYPE, Boolean.TYPE });
      localClass.getMethod("pokeInt", new Class[] { paramClass, Integer.TYPE, Boolean.TYPE });
      localClass.getMethod("peekInt", new Class[] { paramClass, Boolean.TYPE });
      localClass.getMethod("pokeByte", new Class[] { paramClass, Byte.TYPE });
      localClass.getMethod("peekByte", new Class[] { paramClass });
      localClass.getMethod("pokeByteArray", new Class[] { paramClass, byte[].class, Integer.TYPE, Integer.TYPE });
      localClass.getMethod("peekByteArray", new Class[] { paramClass, byte[].class, Integer.TYPE, Integer.TYPE });
      return true;
    }
    catch (Throwable paramClass) {}
    return false;
  }
  
  private static <T> Class<T> zzhP(String paramString)
  {
    try
    {
      paramString = Class.forName(paramString);
      return paramString;
    }
    catch (Throwable paramString) {}
    return null;
  }
  
  static final class zza
    extends zze.zzd
  {
    zza(Unsafe paramUnsafe)
    {
      super();
    }
  }
  
  static final class zzb
    extends zze.zzd
  {
    zzb(Unsafe paramUnsafe)
    {
      super();
    }
  }
  
  static final class zzc
    extends zze.zzd
  {
    zzc(Unsafe paramUnsafe)
    {
      super();
    }
  }
  
  static class zzd
  {
    Unsafe zzcsd;
    
    zzd(Unsafe paramUnsafe)
    {
      this.zzcsd = paramUnsafe;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/protobuf/zze.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */