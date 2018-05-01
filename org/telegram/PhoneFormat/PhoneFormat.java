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
  
  /* Error */
  public static PhoneFormat getInstance()
  {
    // Byte code:
    //   0: getstatic 28	org/telegram/PhoneFormat/PhoneFormat:Instance	Lorg/telegram/PhoneFormat/PhoneFormat;
    //   3: astore_0
    //   4: aload_0
    //   5: astore_1
    //   6: aload_0
    //   7: ifnonnull +31 -> 38
    //   10: ldc 2
    //   12: monitorenter
    //   13: getstatic 28	org/telegram/PhoneFormat/PhoneFormat:Instance	Lorg/telegram/PhoneFormat/PhoneFormat;
    //   16: astore_0
    //   17: aload_0
    //   18: astore_1
    //   19: aload_0
    //   20: ifnonnull +15 -> 35
    //   23: new 2	org/telegram/PhoneFormat/PhoneFormat
    //   26: astore_1
    //   27: aload_1
    //   28: invokespecial 41	org/telegram/PhoneFormat/PhoneFormat:<init>	()V
    //   31: aload_1
    //   32: putstatic 28	org/telegram/PhoneFormat/PhoneFormat:Instance	Lorg/telegram/PhoneFormat/PhoneFormat;
    //   35: ldc 2
    //   37: monitorexit
    //   38: aload_1
    //   39: areturn
    //   40: astore_1
    //   41: ldc 2
    //   43: monitorexit
    //   44: aload_1
    //   45: athrow
    //   46: astore_1
    //   47: goto -6 -> 41
    // Local variable table:
    //   start	length	slot	name	signature
    //   3	17	0	localPhoneFormat1	PhoneFormat
    //   5	34	1	localPhoneFormat2	PhoneFormat
    //   40	5	1	localObject1	Object
    //   46	1	1	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   13	17	40	finally
    //   23	31	40	finally
    //   35	38	40	finally
    //   41	44	40	finally
    //   31	35	46	finally
  }
  
  public static String strip(String paramString)
  {
    paramString = new StringBuilder(paramString);
    for (int i = paramString.length() - 1; i >= 0; i--) {
      if (!"0123456789+*#".contains(paramString.substring(i, i + 1))) {
        paramString.deleteCharAt(i);
      }
    }
    return paramString.toString();
  }
  
  public static String stripExceptNumbers(String paramString)
  {
    return stripExceptNumbers(paramString, false);
  }
  
  public static String stripExceptNumbers(String paramString, boolean paramBoolean)
  {
    if (paramString == null) {}
    StringBuilder localStringBuilder;
    for (paramString = null;; paramString = localStringBuilder.toString())
    {
      return paramString;
      localStringBuilder = new StringBuilder(paramString);
      paramString = "0123456789";
      if (paramBoolean) {
        paramString = "0123456789" + "+";
      }
      for (int i = localStringBuilder.length() - 1; i >= 0; i--) {
        if (!paramString.contains(localStringBuilder.substring(i, i + 1))) {
          localStringBuilder.deleteCharAt(i);
        }
      }
    }
  }
  
  public String callingCodeForCountryCode(String paramString)
  {
    return (String)this.countryCallingCode.get(paramString.toLowerCase());
  }
  
  public CallingCodeInfo callingCodeInfo(String paramString)
  {
    Object localObject1 = (CallingCodeInfo)this.callingCodeData.get(paramString);
    Object localObject2 = localObject1;
    if (localObject1 == null)
    {
      Object localObject3 = (Integer)this.callingCodeOffsets.get(paramString);
      localObject2 = localObject1;
      if (localObject3 != null)
      {
        localObject1 = this.data;
        int i = ((Integer)localObject3).intValue();
        localObject2 = new CallingCodeInfo();
        ((CallingCodeInfo)localObject2).callingCode = paramString;
        ((CallingCodeInfo)localObject2).countries = ((ArrayList)this.callingCodeCountries.get(paramString));
        this.callingCodeData.put(paramString, localObject2);
        int j = value16(i);
        int k = i + 2 + 2;
        int m = value16(k);
        k = k + 2 + 2;
        int n = value16(k);
        k = k + 2 + 2;
        paramString = new ArrayList(5);
        for (;;)
        {
          localObject3 = valueString(k);
          if (((String)localObject3).length() == 0) {
            break;
          }
          paramString.add(localObject3);
          k += ((String)localObject3).length() + 1;
        }
        ((CallingCodeInfo)localObject2).trunkPrefixes = paramString;
        k++;
        paramString = new ArrayList(5);
        for (;;)
        {
          localObject3 = valueString(k);
          if (((String)localObject3).length() == 0) {
            break;
          }
          paramString.add(localObject3);
          k += ((String)localObject3).length() + 1;
        }
        ((CallingCodeInfo)localObject2).intlPrefixes = paramString;
        ArrayList localArrayList = new ArrayList(n);
        k = i + j;
        for (int i1 = 0; i1 < n; i1++)
        {
          RuleSet localRuleSet = new RuleSet();
          localRuleSet.matchLen = value16(k);
          k += 2;
          int i2 = value16(k);
          k += 2;
          localObject3 = new ArrayList(i2);
          for (int i3 = 0; i3 < i2; i3++)
          {
            paramString = new PhoneRule();
            paramString.minVal = value32(k);
            k += 4;
            paramString.maxVal = value32(k);
            k += 4;
            int i4 = k + 1;
            paramString.byte8 = localObject1[k];
            k = i4 + 1;
            paramString.maxLen = localObject1[i4];
            i4 = k + 1;
            paramString.otherFlag = localObject1[k];
            k = i4 + 1;
            paramString.prefixLen = localObject1[i4];
            i4 = k + 1;
            paramString.flag12 = localObject1[k];
            k = i4 + 1;
            paramString.flag13 = localObject1[i4];
            i4 = value16(k);
            k += 2;
            paramString.format = valueString(i + j + m + i4);
            int i5 = paramString.format.indexOf("[[");
            if (i5 != -1)
            {
              i4 = paramString.format.indexOf("]]");
              paramString.format = String.format("%s%s", new Object[] { paramString.format.substring(0, i5), paramString.format.substring(i4 + 2) });
            }
            ((ArrayList)localObject3).add(paramString);
            if (paramString.hasIntlPrefix) {
              localRuleSet.hasRuleWithIntlPrefix = true;
            }
            if (paramString.hasTrunkPrefix) {
              localRuleSet.hasRuleWithTrunkPrefix = true;
            }
          }
          localRuleSet.rules = ((ArrayList)localObject3);
          localArrayList.add(localRuleSet);
        }
        ((CallingCodeInfo)localObject2).ruleSets = localArrayList;
      }
    }
    return (CallingCodeInfo)localObject2;
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
    for (int i = 0;; i++)
    {
      CallingCodeInfo localCallingCodeInfo2 = localCallingCodeInfo1;
      if (i < 3)
      {
        localCallingCodeInfo2 = localCallingCodeInfo1;
        if (i < paramString.length())
        {
          localCallingCodeInfo1 = callingCodeInfo(paramString.substring(0, i + 1));
          if (localCallingCodeInfo1 == null) {
            continue;
          }
          localCallingCodeInfo2 = localCallingCodeInfo1;
        }
      }
      return localCallingCodeInfo2;
    }
  }
  
  public String format(String paramString)
  {
    Object localObject1;
    if (!this.initialzed) {
      localObject1 = paramString;
    }
    for (;;)
    {
      return (String)localObject1;
      try
      {
        Object localObject2 = strip(paramString);
        String str2;
        Object localObject3;
        if (((String)localObject2).startsWith("+"))
        {
          str2 = ((String)localObject2).substring(1);
          localObject3 = findCallingCodeInfo(str2);
          localObject1 = paramString;
          if (localObject3 != null)
          {
            str2 = ((CallingCodeInfo)localObject3).format(str2);
            localObject1 = new java/lang/StringBuilder;
            ((StringBuilder)localObject1).<init>();
            localObject1 = "+" + str2;
          }
        }
        else
        {
          localObject3 = callingCodeInfo(this.defaultCallingCode);
          localObject1 = paramString;
          if (localObject3 != null)
          {
            str2 = ((CallingCodeInfo)localObject3).matchingAccessCode((String)localObject2);
            if (str2 != null)
            {
              localObject3 = ((String)localObject2).substring(str2.length());
              localObject1 = localObject3;
              localObject2 = findCallingCodeInfo((String)localObject3);
              if (localObject2 != null) {
                localObject1 = ((CallingCodeInfo)localObject2).format((String)localObject3);
              }
              if (((String)localObject1).length() == 0) {
                localObject1 = str2;
              } else {
                localObject1 = String.format("%s %s", new Object[] { str2, localObject1 });
              }
            }
            else
            {
              localObject1 = ((CallingCodeInfo)localObject3).format((String)localObject2);
            }
          }
        }
      }
      catch (Exception localException)
      {
        FileLog.e(localException);
        String str1 = paramString;
      }
    }
  }
  
  /* Error */
  public void init(String paramString)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_2
    //   2: aconst_null
    //   3: astore_3
    //   4: aconst_null
    //   5: astore 4
    //   7: aconst_null
    //   8: astore 5
    //   10: aload 4
    //   12: astore 6
    //   14: getstatic 264	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   17: invokevirtual 270	android/content/Context:getAssets	()Landroid/content/res/AssetManager;
    //   20: ldc_w 272
    //   23: invokevirtual 278	android/content/res/AssetManager:open	(Ljava/lang/String;)Ljava/io/InputStream;
    //   26: astore 7
    //   28: aload 4
    //   30: astore 6
    //   32: aload 7
    //   34: astore_3
    //   35: aload 7
    //   37: astore_2
    //   38: new 280	java/io/ByteArrayOutputStream
    //   41: astore 8
    //   43: aload 4
    //   45: astore 6
    //   47: aload 7
    //   49: astore_3
    //   50: aload 7
    //   52: astore_2
    //   53: aload 8
    //   55: invokespecial 281	java/io/ByteArrayOutputStream:<init>	()V
    //   58: sipush 1024
    //   61: newarray <illegal type>
    //   63: astore_3
    //   64: aload 7
    //   66: aload_3
    //   67: iconst_0
    //   68: sipush 1024
    //   71: invokevirtual 287	java/io/InputStream:read	([BII)I
    //   74: istore 9
    //   76: iload 9
    //   78: iconst_m1
    //   79: if_icmpeq +54 -> 133
    //   82: aload 8
    //   84: aload_3
    //   85: iconst_0
    //   86: iload 9
    //   88: invokevirtual 291	java/io/ByteArrayOutputStream:write	([BII)V
    //   91: goto -27 -> 64
    //   94: astore_1
    //   95: aload 8
    //   97: astore_3
    //   98: aload_1
    //   99: astore 8
    //   101: aload_3
    //   102: astore_1
    //   103: aload_1
    //   104: astore 6
    //   106: aload 7
    //   108: astore_3
    //   109: aload 8
    //   111: invokevirtual 294	java/lang/Exception:printStackTrace	()V
    //   114: aload_1
    //   115: ifnull +7 -> 122
    //   118: aload_1
    //   119: invokevirtual 297	java/io/ByteArrayOutputStream:close	()V
    //   122: aload 7
    //   124: ifnull +8 -> 132
    //   127: aload 7
    //   129: invokevirtual 298	java/io/InputStream:close	()V
    //   132: return
    //   133: aload_0
    //   134: aload 8
    //   136: invokevirtual 302	java/io/ByteArrayOutputStream:toByteArray	()[B
    //   139: putfield 108	org/telegram/PhoneFormat/PhoneFormat:data	[B
    //   142: aload_0
    //   143: aload_0
    //   144: getfield 108	org/telegram/PhoneFormat/PhoneFormat:data	[B
    //   147: invokestatic 308	java/nio/ByteBuffer:wrap	([B)Ljava/nio/ByteBuffer;
    //   150: putfield 310	org/telegram/PhoneFormat/PhoneFormat:buffer	Ljava/nio/ByteBuffer;
    //   153: aload_0
    //   154: getfield 310	org/telegram/PhoneFormat/PhoneFormat:buffer	Ljava/nio/ByteBuffer;
    //   157: getstatic 316	java/nio/ByteOrder:LITTLE_ENDIAN	Ljava/nio/ByteOrder;
    //   160: invokevirtual 320	java/nio/ByteBuffer:order	(Ljava/nio/ByteOrder;)Ljava/nio/ByteBuffer;
    //   163: pop
    //   164: aload 8
    //   166: ifnull +8 -> 174
    //   169: aload 8
    //   171: invokevirtual 297	java/io/ByteArrayOutputStream:close	()V
    //   174: aload 7
    //   176: ifnull +8 -> 184
    //   179: aload 7
    //   181: invokevirtual 298	java/io/InputStream:close	()V
    //   184: aload_1
    //   185: ifnull +160 -> 345
    //   188: aload_1
    //   189: invokevirtual 139	java/lang/String:length	()I
    //   192: ifeq +153 -> 345
    //   195: aload_0
    //   196: aload_1
    //   197: putfield 232	org/telegram/PhoneFormat/PhoneFormat:defaultCountry	Ljava/lang/String;
    //   200: aload_0
    //   201: new 92	java/util/HashMap
    //   204: dup
    //   205: sipush 255
    //   208: invokespecial 321	java/util/HashMap:<init>	(I)V
    //   211: putfield 104	org/telegram/PhoneFormat/PhoneFormat:callingCodeOffsets	Ljava/util/HashMap;
    //   214: aload_0
    //   215: new 92	java/util/HashMap
    //   218: dup
    //   219: sipush 255
    //   222: invokespecial 321	java/util/HashMap:<init>	(I)V
    //   225: putfield 117	org/telegram/PhoneFormat/PhoneFormat:callingCodeCountries	Ljava/util/HashMap;
    //   228: aload_0
    //   229: new 92	java/util/HashMap
    //   232: dup
    //   233: bipush 10
    //   235: invokespecial 321	java/util/HashMap:<init>	(I)V
    //   238: putfield 100	org/telegram/PhoneFormat/PhoneFormat:callingCodeData	Ljava/util/HashMap;
    //   241: aload_0
    //   242: new 92	java/util/HashMap
    //   245: dup
    //   246: sipush 255
    //   249: invokespecial 321	java/util/HashMap:<init>	(I)V
    //   252: putfield 87	org/telegram/PhoneFormat/PhoneFormat:countryCallingCode	Ljava/util/HashMap;
    //   255: aload_0
    //   256: invokevirtual 324	org/telegram/PhoneFormat/PhoneFormat:parseDataHeader	()V
    //   259: aload_0
    //   260: iconst_1
    //   261: putfield 34	org/telegram/PhoneFormat/PhoneFormat:initialzed	Z
    //   264: goto -132 -> 132
    //   267: astore_3
    //   268: aload_3
    //   269: invokestatic 258	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   272: goto -98 -> 174
    //   275: astore 7
    //   277: aload 7
    //   279: invokestatic 258	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   282: goto -98 -> 184
    //   285: astore_1
    //   286: aload_1
    //   287: invokestatic 258	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   290: goto -168 -> 122
    //   293: astore_1
    //   294: aload_1
    //   295: invokestatic 258	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   298: goto -166 -> 132
    //   301: astore_1
    //   302: aload_3
    //   303: astore 7
    //   305: aload 6
    //   307: ifnull +8 -> 315
    //   310: aload 6
    //   312: invokevirtual 297	java/io/ByteArrayOutputStream:close	()V
    //   315: aload 7
    //   317: ifnull +8 -> 325
    //   320: aload 7
    //   322: invokevirtual 298	java/io/InputStream:close	()V
    //   325: aload_1
    //   326: athrow
    //   327: astore_3
    //   328: aload_3
    //   329: invokestatic 258	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   332: goto -17 -> 315
    //   335: astore 7
    //   337: aload 7
    //   339: invokestatic 258	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   342: goto -17 -> 325
    //   345: aload_0
    //   346: invokestatic 330	java/util/Locale:getDefault	()Ljava/util/Locale;
    //   349: invokevirtual 333	java/util/Locale:getCountry	()Ljava/lang/String;
    //   352: invokevirtual 90	java/lang/String:toLowerCase	()Ljava/lang/String;
    //   355: putfield 232	org/telegram/PhoneFormat/PhoneFormat:defaultCountry	Ljava/lang/String;
    //   358: goto -158 -> 200
    //   361: astore_1
    //   362: aload 8
    //   364: astore 6
    //   366: goto -61 -> 305
    //   369: astore 8
    //   371: aload 5
    //   373: astore_1
    //   374: aload_2
    //   375: astore 7
    //   377: goto -274 -> 103
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	380	0	this	PhoneFormat
    //   0	380	1	paramString	String
    //   1	374	2	localObject1	Object
    //   3	106	3	localObject2	Object
    //   267	36	3	localException1	Exception
    //   327	2	3	localException2	Exception
    //   5	39	4	localObject3	Object
    //   8	364	5	localObject4	Object
    //   12	353	6	localObject5	Object
    //   26	154	7	localInputStream	java.io.InputStream
    //   275	3	7	localException3	Exception
    //   303	18	7	localException4	Exception
    //   335	3	7	localException5	Exception
    //   375	1	7	localObject6	Object
    //   41	322	8	localObject7	Object
    //   369	1	8	localException6	Exception
    //   74	13	9	i	int
    // Exception table:
    //   from	to	target	type
    //   58	64	94	java/lang/Exception
    //   64	76	94	java/lang/Exception
    //   82	91	94	java/lang/Exception
    //   133	164	94	java/lang/Exception
    //   169	174	267	java/lang/Exception
    //   179	184	275	java/lang/Exception
    //   118	122	285	java/lang/Exception
    //   127	132	293	java/lang/Exception
    //   14	28	301	finally
    //   38	43	301	finally
    //   53	58	301	finally
    //   109	114	301	finally
    //   310	315	327	java/lang/Exception
    //   320	325	335	java/lang/Exception
    //   58	64	361	finally
    //   64	76	361	finally
    //   82	91	361	finally
    //   133	164	361	finally
    //   14	28	369	java/lang/Exception
    //   38	43	369	java/lang/Exception
    //   53	58	369	java/lang/Exception
  }
  
  public boolean isPhoneNumberValid(String paramString)
  {
    boolean bool = true;
    if (!this.initialzed) {}
    for (;;)
    {
      return bool;
      paramString = strip(paramString);
      CallingCodeInfo localCallingCodeInfo;
      if (paramString.startsWith("+"))
      {
        paramString = paramString.substring(1);
        localCallingCodeInfo = findCallingCodeInfo(paramString);
        if ((localCallingCodeInfo == null) || (!localCallingCodeInfo.isValidPhoneNumber(paramString))) {
          bool = false;
        }
      }
      else
      {
        localCallingCodeInfo = callingCodeInfo(this.defaultCallingCode);
        if (localCallingCodeInfo == null)
        {
          bool = false;
        }
        else
        {
          String str = localCallingCodeInfo.matchingAccessCode(paramString);
          if (str != null)
          {
            paramString = paramString.substring(str.length());
            if (paramString.length() != 0)
            {
              localCallingCodeInfo = findCallingCodeInfo(paramString);
              if ((localCallingCodeInfo == null) || (!localCallingCodeInfo.isValidPhoneNumber(paramString))) {
                bool = false;
              }
            }
            else
            {
              bool = false;
            }
          }
          else
          {
            bool = localCallingCodeInfo.isValidPhoneNumber(paramString);
          }
        }
      }
    }
  }
  
  public void parseDataHeader()
  {
    int i = value32(0);
    int j = 4;
    for (int k = 0; k < i; k++)
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
      this.callingCodeOffsets.put(str1, Integer.valueOf(m + (i * 12 + 4)));
      ArrayList localArrayList1 = (ArrayList)this.callingCodeCountries.get(str1);
      ArrayList localArrayList2 = localArrayList1;
      if (localArrayList1 == null)
      {
        localArrayList2 = new ArrayList();
        this.callingCodeCountries.put(str1, localArrayList2);
      }
      localArrayList2.add(str2);
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
      paramInt = this.buffer.getShort();
    }
    int j;
    for (int i = paramInt;; j = paramInt)
    {
      return i;
      paramInt = 0;
    }
  }
  
  int value32(int paramInt)
  {
    if (paramInt + 4 <= this.data.length) {
      this.buffer.position(paramInt);
    }
    for (paramInt = this.buffer.getInt();; paramInt = 0) {
      return paramInt;
    }
  }
  
  public String valueString(int paramInt)
  {
    int i = paramInt;
    for (;;)
    {
      try
      {
        if (i >= this.data.length) {
          break label70;
        }
        if (this.data[i] != 0) {
          continue;
        }
        if (paramInt != i - paramInt) {
          continue;
        }
        str1 = "";
      }
      catch (Exception localException)
      {
        String str1;
        localException.printStackTrace();
        str2 = "";
        continue;
        i++;
      }
      return str1;
      str1 = new java/lang/String;
      str1.<init>(this.data, paramInt, i - paramInt);
      continue;
      continue;
      label70:
      String str2 = "";
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/PhoneFormat/PhoneFormat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */