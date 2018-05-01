package com.coremedia.iso;

import com.coremedia.iso.boxes.Box;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PropertyBoxParserImpl
  extends AbstractBoxParser
{
  static String[] EMPTY_STRING_ARRAY = new String[0];
  StringBuilder buildLookupStrings = new StringBuilder();
  String clazzName;
  Pattern constuctorPattern = Pattern.compile("(.*)\\((.*?)\\)");
  Properties mapping;
  String[] param;
  
  public PropertyBoxParserImpl(Properties paramProperties)
  {
    this.mapping = paramProperties;
  }
  
  /* Error */
  public PropertyBoxParserImpl(String... paramVarArgs)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 26	com/coremedia/iso/AbstractBoxParser:<init>	()V
    //   4: aload_0
    //   5: ldc 28
    //   7: invokestatic 34	java/util/regex/Pattern:compile	(Ljava/lang/String;)Ljava/util/regex/Pattern;
    //   10: putfield 36	com/coremedia/iso/PropertyBoxParserImpl:constuctorPattern	Ljava/util/regex/Pattern;
    //   13: aload_0
    //   14: new 38	java/lang/StringBuilder
    //   17: dup
    //   18: invokespecial 39	java/lang/StringBuilder:<init>	()V
    //   21: putfield 41	com/coremedia/iso/PropertyBoxParserImpl:buildLookupStrings	Ljava/lang/StringBuilder;
    //   24: aload_0
    //   25: invokevirtual 52	java/lang/Object:getClass	()Ljava/lang/Class;
    //   28: ldc 54
    //   30: invokevirtual 60	java/lang/Class:getResourceAsStream	(Ljava/lang/String;)Ljava/io/InputStream;
    //   33: astore_2
    //   34: new 62	java/util/Properties
    //   37: astore_3
    //   38: aload_3
    //   39: invokespecial 63	java/util/Properties:<init>	()V
    //   42: aload_0
    //   43: aload_3
    //   44: putfield 43	com/coremedia/iso/PropertyBoxParserImpl:mapping	Ljava/util/Properties;
    //   47: aload_0
    //   48: getfield 43	com/coremedia/iso/PropertyBoxParserImpl:mapping	Ljava/util/Properties;
    //   51: aload_2
    //   52: invokevirtual 67	java/util/Properties:load	(Ljava/io/InputStream;)V
    //   55: invokestatic 73	java/lang/Thread:currentThread	()Ljava/lang/Thread;
    //   58: invokevirtual 77	java/lang/Thread:getContextClassLoader	()Ljava/lang/ClassLoader;
    //   61: astore 4
    //   63: aload 4
    //   65: astore_3
    //   66: aload 4
    //   68: ifnonnull +7 -> 75
    //   71: invokestatic 82	java/lang/ClassLoader:getSystemClassLoader	()Ljava/lang/ClassLoader;
    //   74: astore_3
    //   75: aload_3
    //   76: ldc 84
    //   78: invokevirtual 88	java/lang/ClassLoader:getResources	(Ljava/lang/String;)Ljava/util/Enumeration;
    //   81: astore 4
    //   83: aload 4
    //   85: invokeinterface 94 1 0
    //   90: ifne +22 -> 112
    //   93: aload_1
    //   94: arraylength
    //   95: istore 5
    //   97: iconst_0
    //   98: istore 6
    //   100: iload 6
    //   102: iload 5
    //   104: if_icmplt +63 -> 167
    //   107: aload_2
    //   108: invokevirtual 99	java/io/InputStream:close	()V
    //   111: return
    //   112: aload 4
    //   114: invokeinterface 103 1 0
    //   119: checkcast 105	java/net/URL
    //   122: invokevirtual 109	java/net/URL:openStream	()Ljava/io/InputStream;
    //   125: astore_3
    //   126: aload_0
    //   127: getfield 43	com/coremedia/iso/PropertyBoxParserImpl:mapping	Ljava/util/Properties;
    //   130: aload_3
    //   131: invokevirtual 67	java/util/Properties:load	(Ljava/io/InputStream;)V
    //   134: aload_3
    //   135: invokevirtual 99	java/io/InputStream:close	()V
    //   138: goto -55 -> 83
    //   141: astore_3
    //   142: new 111	java/lang/RuntimeException
    //   145: astore_1
    //   146: aload_1
    //   147: aload_3
    //   148: invokespecial 114	java/lang/RuntimeException:<init>	(Ljava/lang/Throwable;)V
    //   151: aload_1
    //   152: athrow
    //   153: astore_1
    //   154: aload_2
    //   155: invokevirtual 99	java/io/InputStream:close	()V
    //   158: aload_1
    //   159: athrow
    //   160: astore_1
    //   161: aload_3
    //   162: invokevirtual 99	java/io/InputStream:close	()V
    //   165: aload_1
    //   166: athrow
    //   167: aload_1
    //   168: iload 6
    //   170: aaload
    //   171: astore_3
    //   172: aload_0
    //   173: getfield 43	com/coremedia/iso/PropertyBoxParserImpl:mapping	Ljava/util/Properties;
    //   176: aload_0
    //   177: invokevirtual 52	java/lang/Object:getClass	()Ljava/lang/Class;
    //   180: aload_3
    //   181: invokevirtual 60	java/lang/Class:getResourceAsStream	(Ljava/lang/String;)Ljava/io/InputStream;
    //   184: invokevirtual 67	java/util/Properties:load	(Ljava/io/InputStream;)V
    //   187: iinc 6 1
    //   190: goto -90 -> 100
    //   193: astore_3
    //   194: aload_3
    //   195: invokevirtual 117	java/io/IOException:printStackTrace	()V
    //   198: goto -40 -> 158
    //   201: astore_1
    //   202: aload_1
    //   203: invokevirtual 117	java/io/IOException:printStackTrace	()V
    //   206: goto -95 -> 111
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	209	0	this	PropertyBoxParserImpl
    //   0	209	1	paramVarArgs	String[]
    //   33	122	2	localInputStream	java.io.InputStream
    //   37	98	3	localObject1	Object
    //   141	21	3	localIOException1	java.io.IOException
    //   171	10	3	str	String
    //   193	2	3	localIOException2	java.io.IOException
    //   61	52	4	localObject2	Object
    //   95	10	5	i	int
    //   98	90	6	j	int
    // Exception table:
    //   from	to	target	type
    //   47	63	141	java/io/IOException
    //   71	75	141	java/io/IOException
    //   75	83	141	java/io/IOException
    //   83	97	141	java/io/IOException
    //   112	126	141	java/io/IOException
    //   134	138	141	java/io/IOException
    //   161	167	141	java/io/IOException
    //   172	187	141	java/io/IOException
    //   34	47	153	finally
    //   47	63	153	finally
    //   71	75	153	finally
    //   75	83	153	finally
    //   83	97	153	finally
    //   112	126	153	finally
    //   134	138	153	finally
    //   142	153	153	finally
    //   161	167	153	finally
    //   172	187	153	finally
    //   126	134	160	finally
    //   154	158	193	java/io/IOException
    //   107	111	201	java/io/IOException
  }
  
  public Box createBox(String paramString1, byte[] paramArrayOfByte, String paramString2)
  {
    invoke(paramString1, paramArrayOfByte, paramString2);
    try
    {
      localClass = Class.forName(this.clazzName);
      if (this.param.length <= 0) {
        break label235;
      }
      arrayOfClass = new Class[this.param.length];
      arrayOfObject = new Object[this.param.length];
      i = 0;
      if (i < this.param.length) {
        break label75;
      }
      paramString1 = (Box)localClass.getConstructor(arrayOfClass).newInstance(arrayOfObject);
    }
    catch (ClassNotFoundException paramString1)
    {
      for (;;)
      {
        Class[] arrayOfClass;
        Object[] arrayOfObject;
        throw new RuntimeException(paramString1);
        if (!"parent".equals(this.param[i])) {
          break;
        }
        arrayOfObject[i] = paramString2;
        arrayOfClass[i] = String.class;
      }
    }
    catch (InstantiationException paramString1)
    {
      int i;
      throw new RuntimeException(paramString1);
      paramArrayOfByte = new java/lang/InternalError;
      paramString1 = new java/lang/StringBuilder;
      paramString1.<init>("No such param: ");
      paramArrayOfByte.<init>(this.param[i]);
      throw paramArrayOfByte;
    }
    catch (IllegalAccessException paramString1)
    {
      for (;;)
      {
        Class localClass;
        throw new RuntimeException(paramString1);
        paramString1 = (Box)localClass.newInstance();
      }
    }
    catch (InvocationTargetException paramString1)
    {
      throw new RuntimeException(paramString1);
    }
    catch (NoSuchMethodException paramString1)
    {
      throw new RuntimeException(paramString1);
    }
    return paramString1;
    label75:
    if ("userType".equals(this.param[i]))
    {
      arrayOfObject[i] = paramArrayOfByte;
      arrayOfClass[i] = byte[].class;
    }
    for (;;)
    {
      i++;
      break;
      if (!"type".equals(this.param[i])) {
        break label150;
      }
      arrayOfObject[i] = paramString1;
      arrayOfClass[i] = String.class;
    }
  }
  
  public void invoke(String paramString1, byte[] paramArrayOfByte, String paramString2)
  {
    String str2;
    if (paramArrayOfByte != null)
    {
      if (!"uuid".equals(paramString1)) {
        throw new RuntimeException("we have a userType but no uuid box type. Something's wrong");
      }
      String str1 = this.mapping.getProperty("uuid[" + Hex.encodeHex(paramArrayOfByte).toUpperCase() + "]");
      str2 = str1;
      if (str1 == null) {
        str2 = this.mapping.getProperty(paramString2 + "-uuid[" + Hex.encodeHex(paramArrayOfByte).toUpperCase() + "]");
      }
      paramArrayOfByte = str2;
      if (str2 == null) {
        paramArrayOfByte = this.mapping.getProperty("uuid");
      }
    }
    for (;;)
    {
      paramString2 = paramArrayOfByte;
      if (paramArrayOfByte == null) {
        paramString2 = this.mapping.getProperty("default");
      }
      if (paramString2 != null) {
        break;
      }
      throw new RuntimeException("No box object found for " + paramString1);
      str2 = this.mapping.getProperty(paramString1);
      paramArrayOfByte = str2;
      if (str2 == null)
      {
        paramArrayOfByte = paramString2 + '-' + paramString1;
        this.buildLookupStrings.setLength(0);
        paramArrayOfByte = this.mapping.getProperty(paramArrayOfByte);
      }
    }
    if (!paramString2.endsWith(")"))
    {
      this.param = EMPTY_STRING_ARRAY;
      this.clazzName = paramString2;
    }
    for (;;)
    {
      return;
      paramString1 = this.constuctorPattern.matcher(paramString2);
      if (!paramString1.matches()) {
        throw new RuntimeException("Cannot work with that constructor: " + paramString2);
      }
      this.clazzName = paramString1.group(1);
      if (paramString1.group(2).length() != 0) {
        break;
      }
      this.param = EMPTY_STRING_ARRAY;
    }
    if (paramString1.group(2).length() > 0) {}
    for (paramString1 = paramString1.group(2).split(",");; paramString1 = new String[0])
    {
      this.param = paramString1;
      break;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/PropertyBoxParserImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */