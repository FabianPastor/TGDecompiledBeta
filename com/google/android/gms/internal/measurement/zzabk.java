package com.google.android.gms.internal.measurement;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class zzabk
{
  private static void zza(String paramString, Object paramObject, StringBuffer paramStringBuffer1, StringBuffer paramStringBuffer2)
    throws IllegalAccessException, InvocationTargetException
  {
    int m;
    if (paramObject != null)
    {
      if (!(paramObject instanceof zzabj)) {
        break label466;
      }
      int i = paramStringBuffer1.length();
      if (paramString != null)
      {
        paramStringBuffer2.append(paramStringBuffer1).append(zzfq(paramString)).append(" <\n");
        paramStringBuffer1.append("  ");
      }
      Class localClass = paramObject.getClass();
      Object localObject3;
      Object localObject4;
      for (Object localObject2 : localClass.getFields())
      {
        m = ((Field)localObject2).getModifiers();
        localObject3 = ((Field)localObject2).getName();
        if ((!"cachedSize".equals(localObject3)) && ((m & 0x1) == 1) && ((m & 0x8) != 8) && (!((String)localObject3).startsWith("_")) && (!((String)localObject3).endsWith("_")))
        {
          localObject4 = ((Field)localObject2).getType();
          localObject2 = ((Field)localObject2).get(paramObject);
          if ((((Class)localObject4).isArray()) && (((Class)localObject4).getComponentType() != Byte.TYPE))
          {
            if (localObject2 == null) {}
            for (m = 0;; m = Array.getLength(localObject2)) {
              for (int n = 0; n < m; n++) {
                zza((String)localObject3, Array.get(localObject2, n), paramStringBuffer1, paramStringBuffer2);
              }
            }
          }
          zza((String)localObject3, localObject2, paramStringBuffer1, paramStringBuffer2);
        }
      }
      ??? = localClass.getMethods();
      m = ???.length;
      ??? = 0;
      if (??? < m)
      {
        localObject3 = ???[???].getName();
        if (((String)localObject3).startsWith("set")) {
          localObject4 = ((String)localObject3).substring(3);
        }
        for (;;)
        {
          try
          {
            localObject3 = String.valueOf(localObject4);
            if (((String)localObject3).length() != 0)
            {
              localObject3 = "has".concat((String)localObject3);
              localObject3 = localClass.getMethod((String)localObject3, new Class[0]);
              if (!((Boolean)((Method)localObject3).invoke(paramObject, new Object[0])).booleanValue()) {}
            }
          }
          catch (NoSuchMethodException localNoSuchMethodException1)
          {
            continue;
            String str = new String("get");
            continue;
          }
          try
          {
            localObject3 = String.valueOf(localObject4);
            if (((String)localObject3).length() == 0) {
              continue;
            }
            localObject3 = "get".concat((String)localObject3);
            localObject3 = localClass.getMethod((String)localObject3, new Class[0]);
            zza((String)localObject4, ((Method)localObject3).invoke(paramObject, new Object[0]), paramStringBuffer1, paramStringBuffer2);
            ???++;
          }
          catch (NoSuchMethodException localNoSuchMethodException2)
          {
            continue;
          }
          break;
          localObject3 = new String("has");
        }
      }
      if (paramString != null)
      {
        paramStringBuffer1.setLength(i);
        paramStringBuffer2.append(paramStringBuffer1).append(">\n");
      }
    }
    return;
    label466:
    paramString = zzfq(paramString);
    paramStringBuffer2.append(paramStringBuffer1).append(paramString).append(": ");
    if ((paramObject instanceof String))
    {
      paramObject = (String)paramObject;
      paramString = (String)paramObject;
      if (!((String)paramObject).startsWith("http"))
      {
        paramString = (String)paramObject;
        if (((String)paramObject).length() > 200) {
          paramString = String.valueOf(((String)paramObject).substring(0, 200)).concat("[...]");
        }
      }
      m = paramString.length();
      paramObject = new StringBuilder(m);
      ??? = 0;
      if (??? < m)
      {
        char c = paramString.charAt(???);
        if ((c >= ' ') && (c <= '~') && (c != '"') && (c != '\'')) {
          ((StringBuilder)paramObject).append(c);
        }
        for (;;)
        {
          ???++;
          break;
          ((StringBuilder)paramObject).append(String.format("\\u%04x", new Object[] { Integer.valueOf(c) }));
        }
      }
      paramString = ((StringBuilder)paramObject).toString();
      paramStringBuffer2.append("\"").append(paramString).append("\"");
    }
    for (;;)
    {
      paramStringBuffer2.append("\n");
      break;
      if ((paramObject instanceof byte[]))
      {
        paramString = (byte[])paramObject;
        if (paramString == null)
        {
          paramStringBuffer2.append("\"\"");
        }
        else
        {
          paramStringBuffer2.append('"');
          ??? = 0;
          if (??? < paramString.length)
          {
            m = paramString[???] & 0xFF;
            if ((m == 92) || (m == 34)) {
              paramStringBuffer2.append('\\').append((char)m);
            }
            for (;;)
            {
              ???++;
              break;
              if ((m >= 32) && (m < 127)) {
                paramStringBuffer2.append((char)m);
              } else {
                paramStringBuffer2.append(String.format("\\%03o", new Object[] { Integer.valueOf(m) }));
              }
            }
          }
          paramStringBuffer2.append('"');
        }
      }
      else
      {
        paramStringBuffer2.append(paramObject);
      }
    }
  }
  
  public static <T extends zzabj> String zzc(T paramT)
  {
    if (paramT == null) {
      paramT = "";
    }
    for (;;)
    {
      return paramT;
      StringBuffer localStringBuffer1 = new StringBuffer();
      try
      {
        StringBuffer localStringBuffer2 = new java/lang/StringBuffer;
        localStringBuffer2.<init>();
        zza(null, paramT, localStringBuffer2, localStringBuffer1);
        paramT = localStringBuffer1.toString();
      }
      catch (IllegalAccessException paramT)
      {
        paramT = String.valueOf(paramT.getMessage());
        if (paramT.length() != 0) {
          paramT = "Error printing proto: ".concat(paramT);
        } else {
          paramT = new String("Error printing proto: ");
        }
      }
      catch (InvocationTargetException paramT)
      {
        paramT = String.valueOf(paramT.getMessage());
        if (paramT.length() != 0) {
          paramT = "Error printing proto: ".concat(paramT);
        } else {
          paramT = new String("Error printing proto: ");
        }
      }
    }
  }
  
  private static String zzfq(String paramString)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    int i = 0;
    if (i < paramString.length())
    {
      char c = paramString.charAt(i);
      if (i == 0) {
        localStringBuffer.append(Character.toLowerCase(c));
      }
      for (;;)
      {
        i++;
        break;
        if (Character.isUpperCase(c)) {
          localStringBuffer.append('_').append(Character.toLowerCase(c));
        } else {
          localStringBuffer.append(c);
        }
      }
    }
    return localStringBuffer.toString();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzabk.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */