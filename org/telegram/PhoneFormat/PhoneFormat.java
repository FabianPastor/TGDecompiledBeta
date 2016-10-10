package org.telegram.PhoneFormat;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.FileLog;

public class PhoneFormat
{
  private static volatile PhoneFormat Instance = null;
  public ByteBuffer buffer;
  public HashMap<String, ArrayList<String>> callingCodeCountries;
  public HashMap<String, CallingCodeInfo> callingCodeData;
  public HashMap<String, Integer> callingCodeOffsets;
  public HashMap<String, String> countryCallingCode;
  public byte[] data;
  public String defaultCallingCode;
  public String defaultCountry;
  private boolean initialzed = false;
  
  public PhoneFormat()
  {
    init(null);
  }
  
  public PhoneFormat(String paramString)
  {
    init(paramString);
  }
  
  public static PhoneFormat getInstance()
  {
    Object localObject1 = Instance;
    if (localObject1 == null)
    {
      for (;;)
      {
        try
        {
          PhoneFormat localPhoneFormat2 = Instance;
          localObject1 = localPhoneFormat2;
          if (localPhoneFormat2 == null) {
            localObject1 = new PhoneFormat();
          }
        }
        finally
        {
          continue;
        }
        try
        {
          Instance = (PhoneFormat)localObject1;
          return (PhoneFormat)localObject1;
        }
        finally {}
      }
      throw ((Throwable)localObject1);
    }
    return localPhoneFormat1;
  }
  
  public static String strip(String paramString)
  {
    paramString = new StringBuilder(paramString);
    int i = paramString.length() - 1;
    while (i >= 0)
    {
      if (!"0123456789+*#".contains(paramString.substring(i, i + 1))) {
        paramString.deleteCharAt(i);
      }
      i -= 1;
    }
    return paramString.toString();
  }
  
  public static String stripExceptNumbers(String paramString)
  {
    return stripExceptNumbers(paramString, false);
  }
  
  public static String stripExceptNumbers(String paramString, boolean paramBoolean)
  {
    StringBuilder localStringBuilder = new StringBuilder(paramString);
    paramString = "0123456789";
    if (paramBoolean) {
      paramString = "0123456789" + "+";
    }
    int i = localStringBuilder.length() - 1;
    while (i >= 0)
    {
      if (!paramString.contains(localStringBuilder.substring(i, i + 1))) {
        localStringBuilder.deleteCharAt(i);
      }
      i -= 1;
    }
    return localStringBuilder.toString();
  }
  
  public String callingCodeForCountryCode(String paramString)
  {
    return (String)this.countryCallingCode.get(paramString.toLowerCase());
  }
  
  public CallingCodeInfo callingCodeInfo(String paramString)
  {
    Object localObject2 = (CallingCodeInfo)this.callingCodeData.get(paramString);
    Object localObject1 = localObject2;
    if (localObject2 == null)
    {
      Object localObject3 = (Integer)this.callingCodeOffsets.get(paramString);
      localObject1 = localObject2;
      if (localObject3 != null)
      {
        localObject2 = this.data;
        int m = ((Integer)localObject3).intValue();
        localObject1 = new CallingCodeInfo();
        ((CallingCodeInfo)localObject1).callingCode = paramString;
        ((CallingCodeInfo)localObject1).countries = ((ArrayList)this.callingCodeCountries.get(paramString));
        this.callingCodeData.put(paramString, localObject1);
        int n = value16(m);
        int i = m + 2 + 2;
        int i1 = value16(i);
        i = i + 2 + 2;
        int i2 = value16(i);
        i = i + 2 + 2;
        paramString = new ArrayList(5);
        for (;;)
        {
          localObject3 = valueString(i);
          if (((String)localObject3).length() == 0) {
            break;
          }
          paramString.add(localObject3);
          i += ((String)localObject3).length() + 1;
        }
        ((CallingCodeInfo)localObject1).trunkPrefixes = paramString;
        i += 1;
        paramString = new ArrayList(5);
        for (;;)
        {
          localObject3 = valueString(i);
          if (((String)localObject3).length() == 0) {
            break;
          }
          paramString.add(localObject3);
          i += ((String)localObject3).length() + 1;
        }
        ((CallingCodeInfo)localObject1).intlPrefixes = paramString;
        paramString = new ArrayList(i2);
        i = m + n;
        int j = 0;
        while (j < i2)
        {
          localObject3 = new RuleSet();
          ((RuleSet)localObject3).matchLen = value16(i);
          i += 2;
          int i3 = value16(i);
          i += 2;
          ArrayList localArrayList = new ArrayList(i3);
          int k = 0;
          while (k < i3)
          {
            PhoneRule localPhoneRule = new PhoneRule();
            localPhoneRule.minVal = value32(i);
            i += 4;
            localPhoneRule.maxVal = value32(i);
            int i4 = i + 4;
            i = i4 + 1;
            localPhoneRule.byte8 = localObject2[i4];
            i4 = i + 1;
            localPhoneRule.maxLen = localObject2[i];
            i = i4 + 1;
            localPhoneRule.otherFlag = localObject2[i4];
            i4 = i + 1;
            localPhoneRule.prefixLen = localObject2[i];
            i = i4 + 1;
            localPhoneRule.flag12 = localObject2[i4];
            i4 = i + 1;
            localPhoneRule.flag13 = localObject2[i];
            int i5 = value16(i4);
            i = i4 + 2;
            localPhoneRule.format = valueString(m + n + i1 + i5);
            i4 = localPhoneRule.format.indexOf("[[");
            if (i4 != -1)
            {
              i5 = localPhoneRule.format.indexOf("]]");
              localPhoneRule.format = String.format("%s%s", new Object[] { localPhoneRule.format.substring(0, i4), localPhoneRule.format.substring(i5 + 2) });
            }
            localArrayList.add(localPhoneRule);
            if (localPhoneRule.hasIntlPrefix) {
              ((RuleSet)localObject3).hasRuleWithIntlPrefix = true;
            }
            if (localPhoneRule.hasTrunkPrefix) {
              ((RuleSet)localObject3).hasRuleWithTrunkPrefix = true;
            }
            k += 1;
          }
          ((RuleSet)localObject3).rules = localArrayList;
          paramString.add(localObject3);
          j += 1;
        }
        ((CallingCodeInfo)localObject1).ruleSets = paramString;
      }
    }
    return (CallingCodeInfo)localObject1;
  }
  
  public ArrayList countriesForCallingCode(String paramString)
  {
    String str = paramString;
    if (paramString.startsWith("+")) {
      str = paramString.substring(1);
    }
    return (ArrayList)this.callingCodeCountries.get(str);
  }
  
  public String defaultCallingCode()
  {
    return callingCodeForCountryCode(this.defaultCountry);
  }
  
  public CallingCodeInfo findCallingCodeInfo(String paramString)
  {
    CallingCodeInfo localCallingCodeInfo1 = null;
    int i = 0;
    for (;;)
    {
      CallingCodeInfo localCallingCodeInfo2 = localCallingCodeInfo1;
      if (i < 3)
      {
        localCallingCodeInfo2 = localCallingCodeInfo1;
        if (i < paramString.length())
        {
          localCallingCodeInfo1 = callingCodeInfo(paramString.substring(0, i + 1));
          if (localCallingCodeInfo1 == null) {
            break label46;
          }
          localCallingCodeInfo2 = localCallingCodeInfo1;
        }
      }
      return localCallingCodeInfo2;
      label46:
      i += 1;
    }
  }
  
  public String format(String paramString)
  {
    if (!this.initialzed) {}
    for (;;)
    {
      return paramString;
      try
      {
        Object localObject1 = strip(paramString);
        Object localObject2;
        if (((String)localObject1).startsWith("+"))
        {
          localObject1 = ((String)localObject1).substring(1);
          localObject2 = findCallingCodeInfo((String)localObject1);
          if (localObject2 != null)
          {
            localObject1 = ((CallingCodeInfo)localObject2).format((String)localObject1);
            return "+" + (String)localObject1;
          }
        }
        else
        {
          localObject2 = callingCodeInfo(this.defaultCallingCode);
          if (localObject2 != null)
          {
            String str = ((CallingCodeInfo)localObject2).matchingAccessCode((String)localObject1);
            if (str != null)
            {
              localObject2 = ((String)localObject1).substring(str.length());
              localObject1 = localObject2;
              CallingCodeInfo localCallingCodeInfo = findCallingCodeInfo((String)localObject2);
              if (localCallingCodeInfo != null) {
                localObject1 = localCallingCodeInfo.format((String)localObject2);
              }
              if (((String)localObject1).length() == 0) {
                return str;
              }
              return String.format("%s %s", new Object[] { str, localObject1 });
            }
            localObject1 = ((CallingCodeInfo)localObject2).format((String)localObject1);
            return (String)localObject1;
          }
        }
      }
      catch (Exception localException)
      {
        FileLog.e("tmessages", localException);
      }
    }
    return paramString;
  }
  
  /* Error */
  public void init(String paramString)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 6
    //   3: aconst_null
    //   4: astore 4
    //   6: aconst_null
    //   7: astore 7
    //   9: aconst_null
    //   10: astore 8
    //   12: aload 7
    //   14: astore 5
    //   16: getstatic 266	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   19: invokevirtual 272	android/content/Context:getAssets	()Landroid/content/res/AssetManager;
    //   22: ldc_w 274
    //   25: invokevirtual 280	android/content/res/AssetManager:open	(Ljava/lang/String;)Ljava/io/InputStream;
    //   28: astore_3
    //   29: aload 7
    //   31: astore 5
    //   33: aload_3
    //   34: astore 4
    //   36: aload_3
    //   37: astore 6
    //   39: new 282	java/io/ByteArrayOutputStream
    //   42: dup
    //   43: invokespecial 283	java/io/ByteArrayOutputStream:<init>	()V
    //   46: astore 7
    //   48: sipush 1024
    //   51: newarray <illegal type>
    //   53: astore 4
    //   55: aload_3
    //   56: aload 4
    //   58: iconst_0
    //   59: sipush 1024
    //   62: invokevirtual 289	java/io/InputStream:read	([BII)I
    //   65: istore_2
    //   66: iload_2
    //   67: iconst_m1
    //   68: if_icmpeq +52 -> 120
    //   71: aload 7
    //   73: aload 4
    //   75: iconst_0
    //   76: iload_2
    //   77: invokevirtual 293	java/io/ByteArrayOutputStream:write	([BII)V
    //   80: goto -25 -> 55
    //   83: astore 4
    //   85: aload 7
    //   87: astore_1
    //   88: aload 4
    //   90: astore 7
    //   92: aload_1
    //   93: astore 5
    //   95: aload_3
    //   96: astore 4
    //   98: aload 7
    //   100: invokevirtual 296	java/lang/Exception:printStackTrace	()V
    //   103: aload_1
    //   104: ifnull +7 -> 111
    //   107: aload_1
    //   108: invokevirtual 299	java/io/ByteArrayOutputStream:close	()V
    //   111: aload_3
    //   112: ifnull +7 -> 119
    //   115: aload_3
    //   116: invokevirtual 300	java/io/InputStream:close	()V
    //   119: return
    //   120: aload_0
    //   121: aload 7
    //   123: invokevirtual 304	java/io/ByteArrayOutputStream:toByteArray	()[B
    //   126: putfield 108	org/telegram/PhoneFormat/PhoneFormat:data	[B
    //   129: aload_0
    //   130: aload_0
    //   131: getfield 108	org/telegram/PhoneFormat/PhoneFormat:data	[B
    //   134: invokestatic 310	java/nio/ByteBuffer:wrap	([B)Ljava/nio/ByteBuffer;
    //   137: putfield 312	org/telegram/PhoneFormat/PhoneFormat:buffer	Ljava/nio/ByteBuffer;
    //   140: aload_0
    //   141: getfield 312	org/telegram/PhoneFormat/PhoneFormat:buffer	Ljava/nio/ByteBuffer;
    //   144: getstatic 318	java/nio/ByteOrder:LITTLE_ENDIAN	Ljava/nio/ByteOrder;
    //   147: invokevirtual 322	java/nio/ByteBuffer:order	(Ljava/nio/ByteOrder;)Ljava/nio/ByteBuffer;
    //   150: pop
    //   151: aload 7
    //   153: ifnull +8 -> 161
    //   156: aload 7
    //   158: invokevirtual 299	java/io/ByteArrayOutputStream:close	()V
    //   161: aload_3
    //   162: ifnull +7 -> 169
    //   165: aload_3
    //   166: invokevirtual 300	java/io/InputStream:close	()V
    //   169: aload_1
    //   170: ifnull +166 -> 336
    //   173: aload_1
    //   174: invokevirtual 139	java/lang/String:length	()I
    //   177: ifeq +159 -> 336
    //   180: aload_0
    //   181: aload_1
    //   182: putfield 232	org/telegram/PhoneFormat/PhoneFormat:defaultCountry	Ljava/lang/String;
    //   185: aload_0
    //   186: new 92	java/util/HashMap
    //   189: dup
    //   190: sipush 255
    //   193: invokespecial 323	java/util/HashMap:<init>	(I)V
    //   196: putfield 104	org/telegram/PhoneFormat/PhoneFormat:callingCodeOffsets	Ljava/util/HashMap;
    //   199: aload_0
    //   200: new 92	java/util/HashMap
    //   203: dup
    //   204: sipush 255
    //   207: invokespecial 323	java/util/HashMap:<init>	(I)V
    //   210: putfield 117	org/telegram/PhoneFormat/PhoneFormat:callingCodeCountries	Ljava/util/HashMap;
    //   213: aload_0
    //   214: new 92	java/util/HashMap
    //   217: dup
    //   218: bipush 10
    //   220: invokespecial 323	java/util/HashMap:<init>	(I)V
    //   223: putfield 100	org/telegram/PhoneFormat/PhoneFormat:callingCodeData	Ljava/util/HashMap;
    //   226: aload_0
    //   227: new 92	java/util/HashMap
    //   230: dup
    //   231: sipush 255
    //   234: invokespecial 323	java/util/HashMap:<init>	(I)V
    //   237: putfield 87	org/telegram/PhoneFormat/PhoneFormat:countryCallingCode	Ljava/util/HashMap;
    //   240: aload_0
    //   241: invokevirtual 326	org/telegram/PhoneFormat/PhoneFormat:parseDataHeader	()V
    //   244: aload_0
    //   245: iconst_1
    //   246: putfield 34	org/telegram/PhoneFormat/PhoneFormat:initialzed	Z
    //   249: return
    //   250: astore 4
    //   252: ldc -2
    //   254: aload 4
    //   256: invokestatic 260	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   259: goto -98 -> 161
    //   262: astore_3
    //   263: ldc -2
    //   265: aload_3
    //   266: invokestatic 260	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   269: goto -100 -> 169
    //   272: astore_1
    //   273: ldc -2
    //   275: aload_1
    //   276: invokestatic 260	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   279: goto -168 -> 111
    //   282: astore_1
    //   283: ldc -2
    //   285: aload_1
    //   286: invokestatic 260	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   289: return
    //   290: astore_1
    //   291: aload 4
    //   293: astore_3
    //   294: aload 5
    //   296: ifnull +8 -> 304
    //   299: aload 5
    //   301: invokevirtual 299	java/io/ByteArrayOutputStream:close	()V
    //   304: aload_3
    //   305: ifnull +7 -> 312
    //   308: aload_3
    //   309: invokevirtual 300	java/io/InputStream:close	()V
    //   312: aload_1
    //   313: athrow
    //   314: astore 4
    //   316: ldc -2
    //   318: aload 4
    //   320: invokestatic 260	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   323: goto -19 -> 304
    //   326: astore_3
    //   327: ldc -2
    //   329: aload_3
    //   330: invokestatic 260	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   333: goto -21 -> 312
    //   336: aload_0
    //   337: invokestatic 332	java/util/Locale:getDefault	()Ljava/util/Locale;
    //   340: invokevirtual 335	java/util/Locale:getCountry	()Ljava/lang/String;
    //   343: invokevirtual 90	java/lang/String:toLowerCase	()Ljava/lang/String;
    //   346: putfield 232	org/telegram/PhoneFormat/PhoneFormat:defaultCountry	Ljava/lang/String;
    //   349: goto -164 -> 185
    //   352: astore_1
    //   353: aload 7
    //   355: astore 5
    //   357: goto -63 -> 294
    //   360: astore 7
    //   362: aload 8
    //   364: astore_1
    //   365: aload 6
    //   367: astore_3
    //   368: goto -276 -> 92
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	371	0	this	PhoneFormat
    //   0	371	1	paramString	String
    //   65	12	2	i	int
    //   28	138	3	localInputStream	java.io.InputStream
    //   262	4	3	localException1	Exception
    //   293	16	3	localException2	Exception
    //   326	4	3	localException3	Exception
    //   367	1	3	localObject1	Object
    //   4	70	4	localObject2	Object
    //   83	6	4	localException4	Exception
    //   96	1	4	localObject3	Object
    //   250	42	4	localException5	Exception
    //   314	5	4	localException6	Exception
    //   14	342	5	localObject4	Object
    //   1	365	6	localObject5	Object
    //   7	347	7	localObject6	Object
    //   360	1	7	localException7	Exception
    //   10	353	8	localObject7	Object
    // Exception table:
    //   from	to	target	type
    //   48	55	83	java/lang/Exception
    //   55	66	83	java/lang/Exception
    //   71	80	83	java/lang/Exception
    //   120	151	83	java/lang/Exception
    //   156	161	250	java/lang/Exception
    //   165	169	262	java/lang/Exception
    //   107	111	272	java/lang/Exception
    //   115	119	282	java/lang/Exception
    //   16	29	290	finally
    //   39	48	290	finally
    //   98	103	290	finally
    //   299	304	314	java/lang/Exception
    //   308	312	326	java/lang/Exception
    //   48	55	352	finally
    //   55	66	352	finally
    //   71	80	352	finally
    //   120	151	352	finally
    //   16	29	360	java/lang/Exception
    //   39	48	360	java/lang/Exception
  }
  
  public boolean isPhoneNumberValid(String paramString)
  {
    if (!this.initialzed) {}
    CallingCodeInfo localCallingCodeInfo;
    do
    {
      do
      {
        return true;
        paramString = strip(paramString);
        if (!paramString.startsWith("+")) {
          break;
        }
        paramString = paramString.substring(1);
        localCallingCodeInfo = findCallingCodeInfo(paramString);
      } while ((localCallingCodeInfo != null) && (localCallingCodeInfo.isValidPhoneNumber(paramString)));
      return false;
      localCallingCodeInfo = callingCodeInfo(this.defaultCallingCode);
      if (localCallingCodeInfo == null) {
        return false;
      }
      String str = localCallingCodeInfo.matchingAccessCode(paramString);
      if (str == null) {
        break label112;
      }
      paramString = paramString.substring(str.length());
      if (paramString.length() == 0) {
        break;
      }
      localCallingCodeInfo = findCallingCodeInfo(paramString);
    } while ((localCallingCodeInfo != null) && (localCallingCodeInfo.isValidPhoneNumber(paramString)));
    return false;
    return false;
    label112:
    return localCallingCodeInfo.isValidPhoneNumber(paramString);
  }
  
  public void parseDataHeader()
  {
    int k = value32(0);
    int j = 4;
    int i = 0;
    while (i < k)
    {
      String str1 = valueString(j);
      j += 4;
      String str2 = valueString(j);
      j += 4;
      int m = value32(j);
      j += 4;
      if (str2.equals(this.defaultCountry)) {
        this.defaultCallingCode = str1;
      }
      this.countryCallingCode.put(str2, str1);
      this.callingCodeOffsets.put(str1, Integer.valueOf(m + (k * 12 + 4)));
      ArrayList localArrayList2 = (ArrayList)this.callingCodeCountries.get(str1);
      ArrayList localArrayList1 = localArrayList2;
      if (localArrayList2 == null)
      {
        localArrayList1 = new ArrayList();
        this.callingCodeCountries.put(str1, localArrayList1);
      }
      localArrayList1.add(str2);
      i += 1;
    }
    if (this.defaultCallingCode != null) {
      callingCodeInfo(this.defaultCallingCode);
    }
  }
  
  short value16(int paramInt)
  {
    if (paramInt + 2 <= this.data.length)
    {
      this.buffer.position(paramInt);
      return this.buffer.getShort();
    }
    return 0;
  }
  
  int value32(int paramInt)
  {
    if (paramInt + 4 <= this.data.length)
    {
      this.buffer.position(paramInt);
      return this.buffer.getInt();
    }
    return 0;
  }
  
  public String valueString(int paramInt)
  {
    int i = paramInt;
    for (;;)
    {
      try
      {
        if (i >= this.data.length) {
          break;
        }
        if (this.data[i] == 0)
        {
          if (paramInt == i - paramInt) {
            return "";
          }
          String str = new String(this.data, paramInt, i - paramInt);
          return str;
        }
      }
      catch (Exception localException)
      {
        localException.printStackTrace();
        return "";
      }
      i += 1;
    }
    return "";
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/PhoneFormat/PhoneFormat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */