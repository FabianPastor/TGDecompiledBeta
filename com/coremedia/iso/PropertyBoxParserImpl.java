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
    //   33: astore 6
    //   35: aload_0
    //   36: new 62	java/util/Properties
    //   39: dup
    //   40: invokespecial 63	java/util/Properties:<init>	()V
    //   43: putfield 43	com/coremedia/iso/PropertyBoxParserImpl:mapping	Ljava/util/Properties;
    //   46: aload_0
    //   47: getfield 43	com/coremedia/iso/PropertyBoxParserImpl:mapping	Ljava/util/Properties;
    //   50: aload 6
    //   52: invokevirtual 67	java/util/Properties:load	(Ljava/io/InputStream;)V
    //   55: invokestatic 73	java/lang/Thread:currentThread	()Ljava/lang/Thread;
    //   58: invokevirtual 77	java/lang/Thread:getContextClassLoader	()Ljava/lang/ClassLoader;
    //   61: astore 5
    //   63: aload 5
    //   65: astore 4
    //   67: aload 5
    //   69: ifnonnull +8 -> 77
    //   72: invokestatic 82	java/lang/ClassLoader:getSystemClassLoader	()Ljava/lang/ClassLoader;
    //   75: astore 4
    //   77: aload 4
    //   79: ldc 84
    //   81: invokevirtual 88	java/lang/ClassLoader:getResources	(Ljava/lang/String;)Ljava/util/Enumeration;
    //   84: astore 5
    //   86: aload 5
    //   88: invokeinterface 94 1 0
    //   93: ifne +19 -> 112
    //   96: aload_1
    //   97: arraylength
    //   98: istore_3
    //   99: iconst_0
    //   100: istore_2
    //   101: iload_2
    //   102: iload_3
    //   103: if_icmplt +67 -> 170
    //   106: aload 6
    //   108: invokevirtual 99	java/io/InputStream:close	()V
    //   111: return
    //   112: aload 5
    //   114: invokeinterface 103 1 0
    //   119: checkcast 105	java/net/URL
    //   122: invokevirtual 109	java/net/URL:openStream	()Ljava/io/InputStream;
    //   125: astore 4
    //   127: aload_0
    //   128: getfield 43	com/coremedia/iso/PropertyBoxParserImpl:mapping	Ljava/util/Properties;
    //   131: aload 4
    //   133: invokevirtual 67	java/util/Properties:load	(Ljava/io/InputStream;)V
    //   136: aload 4
    //   138: invokevirtual 99	java/io/InputStream:close	()V
    //   141: goto -55 -> 86
    //   144: astore_1
    //   145: new 111	java/lang/RuntimeException
    //   148: dup
    //   149: aload_1
    //   150: invokespecial 114	java/lang/RuntimeException:<init>	(Ljava/lang/Throwable;)V
    //   153: athrow
    //   154: astore_1
    //   155: aload 6
    //   157: invokevirtual 99	java/io/InputStream:close	()V
    //   160: aload_1
    //   161: athrow
    //   162: astore_1
    //   163: aload 4
    //   165: invokevirtual 99	java/io/InputStream:close	()V
    //   168: aload_1
    //   169: athrow
    //   170: aload_1
    //   171: iload_2
    //   172: aaload
    //   173: astore 4
    //   175: aload_0
    //   176: getfield 43	com/coremedia/iso/PropertyBoxParserImpl:mapping	Ljava/util/Properties;
    //   179: aload_0
    //   180: invokevirtual 52	java/lang/Object:getClass	()Ljava/lang/Class;
    //   183: aload 4
    //   185: invokevirtual 60	java/lang/Class:getResourceAsStream	(Ljava/lang/String;)Ljava/io/InputStream;
    //   188: invokevirtual 67	java/util/Properties:load	(Ljava/io/InputStream;)V
    //   191: iload_2
    //   192: iconst_1
    //   193: iadd
    //   194: istore_2
    //   195: goto -94 -> 101
    //   198: astore 4
    //   200: aload 4
    //   202: invokevirtual 117	java/io/IOException:printStackTrace	()V
    //   205: goto -45 -> 160
    //   208: astore_1
    //   209: aload_1
    //   210: invokevirtual 117	java/io/IOException:printStackTrace	()V
    //   213: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	214	0	this	PropertyBoxParserImpl
    //   0	214	1	paramVarArgs	String[]
    //   100	95	2	i	int
    //   98	6	3	j	int
    //   65	119	4	localObject1	Object
    //   198	3	4	localIOException	java.io.IOException
    //   61	52	5	localObject2	Object
    //   33	123	6	localInputStream	java.io.InputStream
    // Exception table:
    //   from	to	target	type
    //   46	63	144	java/io/IOException
    //   72	77	144	java/io/IOException
    //   77	86	144	java/io/IOException
    //   86	99	144	java/io/IOException
    //   112	127	144	java/io/IOException
    //   136	141	144	java/io/IOException
    //   163	170	144	java/io/IOException
    //   175	191	144	java/io/IOException
    //   35	46	154	finally
    //   46	63	154	finally
    //   72	77	154	finally
    //   77	86	154	finally
    //   86	99	154	finally
    //   112	127	154	finally
    //   136	141	154	finally
    //   145	154	154	finally
    //   163	170	154	finally
    //   175	191	154	finally
    //   127	136	162	finally
    //   155	160	198	java/io/IOException
    //   106	111	208	java/io/IOException
  }
  
  public Box createBox(String paramString1, byte[] paramArrayOfByte, String paramString2)
  {
    invoke(paramString1, paramArrayOfByte, paramString2);
    for (;;)
    {
      int i;
      try
      {
        localClass = Class.forName(this.clazzName);
        if (this.param.length > 0)
        {
          arrayOfClass = new Class[this.param.length];
          arrayOfObject = new Object[this.param.length];
          i = 0;
          if (i >= this.param.length) {
            return (Box)localClass.getConstructor(arrayOfClass).newInstance(arrayOfObject);
          }
          if ("userType".equals(this.param[i]))
          {
            arrayOfObject[i] = paramArrayOfByte;
            arrayOfClass[i] = byte[].class;
          }
          else if ("type".equals(this.param[i]))
          {
            arrayOfObject[i] = paramString1;
            arrayOfClass[i] = String.class;
          }
        }
      }
      catch (ClassNotFoundException paramString1)
      {
        Class[] arrayOfClass;
        Object[] arrayOfObject;
        throw new RuntimeException(paramString1);
        if ("parent".equals(this.param[i]))
        {
          arrayOfObject[i] = paramString2;
          arrayOfClass[i] = String.class;
        }
      }
      catch (InstantiationException paramString1)
      {
        throw new RuntimeException(paramString1);
        throw new InternalError("No such param: " + this.param[i]);
      }
      catch (IllegalAccessException paramString1)
      {
        Class localClass;
        throw new RuntimeException(paramString1);
        paramString1 = (Box)localClass.newInstance();
        return paramString1;
      }
      catch (InvocationTargetException paramString1)
      {
        throw new RuntimeException(paramString1);
      }
      catch (NoSuchMethodException paramString1)
      {
        throw new RuntimeException(paramString1);
      }
      i += 1;
    }
  }
  
  public void invoke(String paramString1, byte[] paramArrayOfByte, String paramString2)
  {
    String str1;
    if (paramArrayOfByte != null)
    {
      if (!"uuid".equals(paramString1)) {
        throw new RuntimeException("we have a userType but no uuid box type. Something's wrong");
      }
      String str2 = this.mapping.getProperty("uuid[" + Hex.encodeHex(paramArrayOfByte).toUpperCase() + "]");
      str1 = str2;
      if (str2 == null) {
        str1 = this.mapping.getProperty(paramString2 + "-uuid[" + Hex.encodeHex(paramArrayOfByte).toUpperCase() + "]");
      }
      paramArrayOfByte = str1;
      if (str1 == null) {
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
      str1 = this.mapping.getProperty(paramString1);
      paramArrayOfByte = str1;
      if (str1 == null)
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
      return;
    }
    paramString1 = this.constuctorPattern.matcher(paramString2);
    if (!paramString1.matches()) {
      throw new RuntimeException("Cannot work with that constructor: " + paramString2);
    }
    this.clazzName = paramString1.group(1);
    if (paramString1.group(2).length() == 0)
    {
      this.param = EMPTY_STRING_ARRAY;
      return;
    }
    if (paramString1.group(2).length() > 0) {}
    for (paramString1 = paramString1.group(2).split(",");; paramString1 = new String[0])
    {
      this.param = paramString1;
      return;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/PropertyBoxParserImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */