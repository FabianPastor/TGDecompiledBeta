package com.google.android.gms.dynamite;

import dalvik.system.PathClassLoader;

final class zzh
  extends PathClassLoader
{
  zzh(String paramString, ClassLoader paramClassLoader)
  {
    super(paramString, paramClassLoader);
  }
  
  protected final Class<?> loadClass(String paramString, boolean paramBoolean)
    throws ClassNotFoundException
  {
    if ((!paramString.startsWith("java.")) && (!paramString.startsWith("android."))) {}
    for (;;)
    {
      try
      {
        Class localClass = findClass(paramString);
        paramString = localClass;
        return paramString;
      }
      catch (ClassNotFoundException localClassNotFoundException) {}
      paramString = super.loadClass(paramString, paramBoolean);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/dynamite/zzh.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */