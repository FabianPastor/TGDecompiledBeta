package net.hockeyapp.android.metrics;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.hockeyapp.android.metrics.model.IJsonSerializable;

public final class JsonHelper
{
  private static final String[] CONTROL_CHARACTERS = new String[''];
  private static final int CONTROL_CHARACTER_RANGE = 128;
  
  static
  {
    int i = 0;
    while (i <= 31)
    {
      CONTROL_CHARACTERS[i] = String.format("\\u%04X", new Object[] { Integer.valueOf(i) });
      i += 1;
    }
    CONTROL_CHARACTERS[34] = "\\\"";
    CONTROL_CHARACTERS[92] = "\\\\";
    CONTROL_CHARACTERS[8] = "\\b";
    CONTROL_CHARACTERS[12] = "\\f";
    CONTROL_CHARACTERS[10] = "\\n";
    CONTROL_CHARACTERS[13] = "\\r";
    CONTROL_CHARACTERS[9] = "\\t";
  }
  
  public static String convert(char paramChar)
  {
    return Character.toString(paramChar);
  }
  
  public static String convert(Double paramDouble)
  {
    return Double.toString(paramDouble.doubleValue());
  }
  
  public static String convert(Float paramFloat)
  {
    return Float.toString(paramFloat.floatValue());
  }
  
  public static String convert(Integer paramInteger)
  {
    return Integer.toString(paramInteger.intValue());
  }
  
  public static String convert(Long paramLong)
  {
    return Long.toString(paramLong.longValue());
  }
  
  public static String convert(String paramString)
  {
    if (paramString == null) {
      return "null";
    }
    if (paramString.length() == 0) {
      return "\"\"";
    }
    return escapeJSON(paramString);
  }
  
  public static String convert(boolean paramBoolean)
  {
    return Boolean.toString(paramBoolean);
  }
  
  private static String escapeJSON(String paramString)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("\"");
    int i = 0;
    if (i < paramString.length())
    {
      char c = paramString.charAt(i);
      String str;
      if (c < '')
      {
        str = CONTROL_CHARACTERS[c];
        if (str == null) {
          localStringBuilder.append(c);
        }
      }
      for (;;)
      {
        i += 1;
        break;
        localStringBuilder.append(str);
        continue;
        if (c == ' ') {
          localStringBuilder.append("\\u2028");
        } else if (c == ' ') {
          localStringBuilder.append("\\u2029");
        } else {
          localStringBuilder.append(c);
        }
      }
    }
    localStringBuilder.append("\"");
    return localStringBuilder.toString();
  }
  
  public static <T> void writeDictionary(Writer paramWriter, Map<String, T> paramMap)
    throws IOException
  {
    if ((paramMap == null) || (paramMap.isEmpty())) {
      paramWriter.write("null");
    }
    Iterator localIterator;
    do
    {
      return;
      localIterator = paramMap.keySet().iterator();
    } while (!localIterator.hasNext());
    paramWriter.write("{");
    String str = (String)localIterator.next();
    Object localObject = paramMap.get(str);
    paramWriter.write("\"" + str + "\"");
    paramWriter.write(":");
    writeItem(paramWriter, localObject);
    while (localIterator.hasNext())
    {
      str = (String)localIterator.next();
      paramWriter.write(",");
      paramWriter.write("\"" + str + "\"");
      paramWriter.write(":");
      writeItem(paramWriter, paramMap.get(str));
    }
    paramWriter.write("}");
  }
  
  private static <T> void writeItem(Writer paramWriter, T paramT)
    throws IOException
  {
    if (paramT != null)
    {
      if ((paramT instanceof String))
      {
        paramWriter.write(convert((String)paramT));
        return;
      }
      if ((paramT instanceof Double))
      {
        paramWriter.write(convert((Double)paramT));
        return;
      }
      if ((paramT instanceof Integer))
      {
        paramWriter.write(convert((Integer)paramT));
        return;
      }
      if ((paramT instanceof Long))
      {
        paramWriter.write(convert((Long)paramT));
        return;
      }
      if ((paramT instanceof IJsonSerializable))
      {
        ((IJsonSerializable)paramT).serialize(paramWriter);
        return;
      }
      throw new IOException("Cannot serialize: " + paramT.toString());
    }
    paramWriter.write("null");
  }
  
  public static void writeJsonSerializable(Writer paramWriter, IJsonSerializable paramIJsonSerializable)
    throws IOException
  {
    if (paramIJsonSerializable != null) {
      paramIJsonSerializable.serialize(paramWriter);
    }
  }
  
  public static <T extends IJsonSerializable> void writeList(Writer paramWriter, List<T> paramList)
    throws IOException
  {
    if ((paramList == null) || (paramList.isEmpty())) {
      paramWriter.write("null");
    }
    do
    {
      return;
      paramList = paramList.iterator();
    } while (!paramList.hasNext());
    paramWriter.write("[");
    ((IJsonSerializable)paramList.next()).serialize(paramWriter);
    while (paramList.hasNext())
    {
      IJsonSerializable localIJsonSerializable = (IJsonSerializable)paramList.next();
      paramWriter.write(",");
      localIJsonSerializable.serialize(paramWriter);
    }
    paramWriter.write("]");
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/metrics/JsonHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */