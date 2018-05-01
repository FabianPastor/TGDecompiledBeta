package net.hockeyapp.android.utils;

public class FeedbackParser
{
  public static FeedbackParser getInstance()
  {
    return FeedbackParserHolder.INSTANCE;
  }
  
  /* Error */
  public net.hockeyapp.android.objects.FeedbackResponse parseFeedbackResponse(String paramString)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_2
    //   2: aconst_null
    //   3: astore_3
    //   4: aload_1
    //   5: ifnull +668 -> 673
    //   8: new 28	org/json/JSONObject
    //   11: astore 4
    //   13: aload 4
    //   15: aload_1
    //   16: invokespecial 31	org/json/JSONObject:<init>	(Ljava/lang/String;)V
    //   19: aload 4
    //   21: ldc 33
    //   23: invokevirtual 37	org/json/JSONObject:getJSONObject	(Ljava/lang/String;)Lorg/json/JSONObject;
    //   26: astore 5
    //   28: new 39	net/hockeyapp/android/objects/Feedback
    //   31: astore 6
    //   33: aload 6
    //   35: invokespecial 40	net/hockeyapp/android/objects/Feedback:<init>	()V
    //   38: aload 5
    //   40: ldc 42
    //   42: invokevirtual 46	org/json/JSONObject:getJSONArray	(Ljava/lang/String;)Lorg/json/JSONArray;
    //   45: astore 7
    //   47: aconst_null
    //   48: astore_1
    //   49: aload 7
    //   51: invokevirtual 52	org/json/JSONArray:length	()I
    //   54: ifle +527 -> 581
    //   57: new 54	java/util/ArrayList
    //   60: astore_3
    //   61: aload_3
    //   62: invokespecial 55	java/util/ArrayList:<init>	()V
    //   65: iconst_0
    //   66: istore 8
    //   68: aload_3
    //   69: astore_1
    //   70: iload 8
    //   72: aload 7
    //   74: invokevirtual 52	org/json/JSONArray:length	()I
    //   77: if_icmpge +504 -> 581
    //   80: aload 7
    //   82: iload 8
    //   84: invokevirtual 58	org/json/JSONArray:getJSONObject	(I)Lorg/json/JSONObject;
    //   87: ldc 60
    //   89: invokevirtual 64	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   92: astore 9
    //   94: aload 7
    //   96: iload 8
    //   98: invokevirtual 58	org/json/JSONArray:getJSONObject	(I)Lorg/json/JSONObject;
    //   101: ldc 66
    //   103: invokevirtual 64	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   106: astore 10
    //   108: aload 7
    //   110: iload 8
    //   112: invokevirtual 58	org/json/JSONArray:getJSONObject	(I)Lorg/json/JSONObject;
    //   115: ldc 68
    //   117: invokevirtual 64	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   120: astore 11
    //   122: aload 7
    //   124: iload 8
    //   126: invokevirtual 58	org/json/JSONArray:getJSONObject	(I)Lorg/json/JSONObject;
    //   129: ldc 70
    //   131: invokevirtual 64	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   134: astore 12
    //   136: aload 7
    //   138: iload 8
    //   140: invokevirtual 58	org/json/JSONArray:getJSONObject	(I)Lorg/json/JSONObject;
    //   143: ldc 72
    //   145: invokevirtual 64	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   148: astore 13
    //   150: aload 7
    //   152: iload 8
    //   154: invokevirtual 58	org/json/JSONArray:getJSONObject	(I)Lorg/json/JSONObject;
    //   157: ldc 74
    //   159: invokevirtual 64	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   162: astore 14
    //   164: aload 7
    //   166: iload 8
    //   168: invokevirtual 58	org/json/JSONArray:getJSONObject	(I)Lorg/json/JSONObject;
    //   171: ldc 76
    //   173: invokevirtual 80	org/json/JSONObject:getInt	(Ljava/lang/String;)I
    //   176: istore 15
    //   178: aload 7
    //   180: iload 8
    //   182: invokevirtual 58	org/json/JSONArray:getJSONObject	(I)Lorg/json/JSONObject;
    //   185: ldc 82
    //   187: invokevirtual 64	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   190: astore 16
    //   192: aload 7
    //   194: iload 8
    //   196: invokevirtual 58	org/json/JSONArray:getJSONObject	(I)Lorg/json/JSONObject;
    //   199: ldc 84
    //   201: invokevirtual 80	org/json/JSONObject:getInt	(Ljava/lang/String;)I
    //   204: istore 17
    //   206: aload 7
    //   208: iload 8
    //   210: invokevirtual 58	org/json/JSONArray:getJSONObject	(I)Lorg/json/JSONObject;
    //   213: ldc 86
    //   215: invokevirtual 64	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   218: astore 18
    //   220: aload 7
    //   222: iload 8
    //   224: invokevirtual 58	org/json/JSONArray:getJSONObject	(I)Lorg/json/JSONObject;
    //   227: ldc 88
    //   229: invokevirtual 64	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   232: astore 19
    //   234: aload 7
    //   236: iload 8
    //   238: invokevirtual 58	org/json/JSONArray:getJSONObject	(I)Lorg/json/JSONObject;
    //   241: ldc 90
    //   243: invokevirtual 64	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   246: astore 20
    //   248: aload 7
    //   250: iload 8
    //   252: invokevirtual 58	org/json/JSONArray:getJSONObject	(I)Lorg/json/JSONObject;
    //   255: ldc 92
    //   257: invokevirtual 64	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   260: astore 21
    //   262: aload 7
    //   264: iload 8
    //   266: invokevirtual 58	org/json/JSONArray:getJSONObject	(I)Lorg/json/JSONObject;
    //   269: ldc 94
    //   271: invokevirtual 97	org/json/JSONObject:optJSONArray	(Ljava/lang/String;)Lorg/json/JSONArray;
    //   274: astore 22
    //   276: invokestatic 103	java/util/Collections:emptyList	()Ljava/util/List;
    //   279: astore_1
    //   280: aload 22
    //   282: ifnull +179 -> 461
    //   285: new 54	java/util/ArrayList
    //   288: astore 23
    //   290: aload 23
    //   292: invokespecial 55	java/util/ArrayList:<init>	()V
    //   295: iconst_0
    //   296: istore 24
    //   298: aload 23
    //   300: astore_1
    //   301: iload 24
    //   303: aload 22
    //   305: invokevirtual 52	org/json/JSONArray:length	()I
    //   308: if_icmpge +153 -> 461
    //   311: aload 22
    //   313: iload 24
    //   315: invokevirtual 58	org/json/JSONArray:getJSONObject	(I)Lorg/json/JSONObject;
    //   318: ldc 76
    //   320: invokevirtual 80	org/json/JSONObject:getInt	(Ljava/lang/String;)I
    //   323: istore 25
    //   325: aload 22
    //   327: iload 24
    //   329: invokevirtual 58	org/json/JSONArray:getJSONObject	(I)Lorg/json/JSONObject;
    //   332: ldc 105
    //   334: invokevirtual 80	org/json/JSONObject:getInt	(Ljava/lang/String;)I
    //   337: istore 26
    //   339: aload 22
    //   341: iload 24
    //   343: invokevirtual 58	org/json/JSONArray:getJSONObject	(I)Lorg/json/JSONObject;
    //   346: ldc 107
    //   348: invokevirtual 64	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   351: astore 27
    //   353: aload 22
    //   355: iload 24
    //   357: invokevirtual 58	org/json/JSONArray:getJSONObject	(I)Lorg/json/JSONObject;
    //   360: ldc 109
    //   362: invokevirtual 64	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   365: astore_1
    //   366: aload 22
    //   368: iload 24
    //   370: invokevirtual 58	org/json/JSONArray:getJSONObject	(I)Lorg/json/JSONObject;
    //   373: ldc 74
    //   375: invokevirtual 64	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   378: astore 28
    //   380: aload 22
    //   382: iload 24
    //   384: invokevirtual 58	org/json/JSONArray:getJSONObject	(I)Lorg/json/JSONObject;
    //   387: ldc 111
    //   389: invokevirtual 64	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   392: astore 29
    //   394: new 113	net/hockeyapp/android/objects/FeedbackAttachment
    //   397: astore 30
    //   399: aload 30
    //   401: invokespecial 114	net/hockeyapp/android/objects/FeedbackAttachment:<init>	()V
    //   404: aload 30
    //   406: iload 25
    //   408: invokevirtual 118	net/hockeyapp/android/objects/FeedbackAttachment:setId	(I)V
    //   411: aload 30
    //   413: iload 26
    //   415: invokevirtual 121	net/hockeyapp/android/objects/FeedbackAttachment:setMessageId	(I)V
    //   418: aload 30
    //   420: aload 27
    //   422: invokevirtual 124	net/hockeyapp/android/objects/FeedbackAttachment:setFilename	(Ljava/lang/String;)V
    //   425: aload 30
    //   427: aload_1
    //   428: invokevirtual 127	net/hockeyapp/android/objects/FeedbackAttachment:setUrl	(Ljava/lang/String;)V
    //   431: aload 30
    //   433: aload 28
    //   435: invokevirtual 130	net/hockeyapp/android/objects/FeedbackAttachment:setCreatedAt	(Ljava/lang/String;)V
    //   438: aload 30
    //   440: aload 29
    //   442: invokevirtual 133	net/hockeyapp/android/objects/FeedbackAttachment:setUpdatedAt	(Ljava/lang/String;)V
    //   445: aload 23
    //   447: aload 30
    //   449: invokeinterface 139 2 0
    //   454: pop
    //   455: iinc 24 1
    //   458: goto -160 -> 298
    //   461: new 141	net/hockeyapp/android/objects/FeedbackMessage
    //   464: astore 23
    //   466: aload 23
    //   468: invokespecial 142	net/hockeyapp/android/objects/FeedbackMessage:<init>	()V
    //   471: aload 23
    //   473: aload 21
    //   475: invokevirtual 145	net/hockeyapp/android/objects/FeedbackMessage:setAppId	(Ljava/lang/String;)V
    //   478: aload 23
    //   480: aload 19
    //   482: invokevirtual 148	net/hockeyapp/android/objects/FeedbackMessage:setCleanText	(Ljava/lang/String;)V
    //   485: aload 23
    //   487: aload 14
    //   489: invokevirtual 149	net/hockeyapp/android/objects/FeedbackMessage:setCreatedAt	(Ljava/lang/String;)V
    //   492: aload 23
    //   494: iload 15
    //   496: invokevirtual 150	net/hockeyapp/android/objects/FeedbackMessage:setId	(I)V
    //   499: aload 23
    //   501: aload 12
    //   503: invokevirtual 153	net/hockeyapp/android/objects/FeedbackMessage:setModel	(Ljava/lang/String;)V
    //   506: aload 23
    //   508: aload 20
    //   510: invokevirtual 156	net/hockeyapp/android/objects/FeedbackMessage:setName	(Ljava/lang/String;)V
    //   513: aload 23
    //   515: aload 11
    //   517: invokevirtual 159	net/hockeyapp/android/objects/FeedbackMessage:setOem	(Ljava/lang/String;)V
    //   520: aload 23
    //   522: aload 13
    //   524: invokevirtual 162	net/hockeyapp/android/objects/FeedbackMessage:setOsVersion	(Ljava/lang/String;)V
    //   527: aload 23
    //   529: aload 9
    //   531: invokevirtual 165	net/hockeyapp/android/objects/FeedbackMessage:setSubject	(Ljava/lang/String;)V
    //   534: aload 23
    //   536: aload 10
    //   538: invokevirtual 168	net/hockeyapp/android/objects/FeedbackMessage:setText	(Ljava/lang/String;)V
    //   541: aload 23
    //   543: aload 16
    //   545: invokevirtual 171	net/hockeyapp/android/objects/FeedbackMessage:setToken	(Ljava/lang/String;)V
    //   548: aload 23
    //   550: aload 18
    //   552: invokevirtual 174	net/hockeyapp/android/objects/FeedbackMessage:setUserString	(Ljava/lang/String;)V
    //   555: aload 23
    //   557: iload 17
    //   559: invokevirtual 177	net/hockeyapp/android/objects/FeedbackMessage:setVia	(I)V
    //   562: aload 23
    //   564: aload_1
    //   565: invokevirtual 181	net/hockeyapp/android/objects/FeedbackMessage:setFeedbackAttachments	(Ljava/util/List;)V
    //   568: aload_3
    //   569: aload 23
    //   571: invokevirtual 182	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   574: pop
    //   575: iinc 8 1
    //   578: goto -510 -> 68
    //   581: aload 6
    //   583: aload_1
    //   584: invokevirtual 186	net/hockeyapp/android/objects/Feedback:setMessages	(Ljava/util/ArrayList;)V
    //   587: aload 6
    //   589: aload 5
    //   591: ldc 90
    //   593: invokevirtual 64	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   596: invokevirtual 187	net/hockeyapp/android/objects/Feedback:setName	(Ljava/lang/String;)V
    //   599: aload 6
    //   601: aload 5
    //   603: ldc -67
    //   605: invokevirtual 64	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   608: invokevirtual 192	net/hockeyapp/android/objects/Feedback:setEmail	(Ljava/lang/String;)V
    //   611: aload 6
    //   613: aload 5
    //   615: ldc 76
    //   617: invokevirtual 80	org/json/JSONObject:getInt	(Ljava/lang/String;)I
    //   620: invokevirtual 193	net/hockeyapp/android/objects/Feedback:setId	(I)V
    //   623: aload 6
    //   625: aload 5
    //   627: ldc 74
    //   629: invokevirtual 64	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   632: invokevirtual 194	net/hockeyapp/android/objects/Feedback:setCreatedAt	(Ljava/lang/String;)V
    //   635: new 196	net/hockeyapp/android/objects/FeedbackResponse
    //   638: dup
    //   639: invokespecial 197	net/hockeyapp/android/objects/FeedbackResponse:<init>	()V
    //   642: astore_1
    //   643: aload_1
    //   644: aload 6
    //   646: invokevirtual 201	net/hockeyapp/android/objects/FeedbackResponse:setFeedback	(Lnet/hockeyapp/android/objects/Feedback;)V
    //   649: aload_1
    //   650: aload 4
    //   652: ldc -53
    //   654: invokevirtual 64	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   657: invokevirtual 206	net/hockeyapp/android/objects/FeedbackResponse:setStatus	(Ljava/lang/String;)V
    //   660: aload_1
    //   661: aload 4
    //   663: ldc 82
    //   665: invokevirtual 64	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   668: invokevirtual 207	net/hockeyapp/android/objects/FeedbackResponse:setToken	(Ljava/lang/String;)V
    //   671: aload_1
    //   672: astore_3
    //   673: aload_3
    //   674: areturn
    //   675: astore_1
    //   676: ldc -47
    //   678: aload_1
    //   679: invokestatic 215	net/hockeyapp/android/utils/HockeyLog:error	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   682: goto -83 -> 599
    //   685: astore_3
    //   686: aload_2
    //   687: astore_1
    //   688: ldc -39
    //   690: aload_3
    //   691: invokestatic 215	net/hockeyapp/android/utils/HockeyLog:error	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   694: aload_1
    //   695: astore_3
    //   696: goto -23 -> 673
    //   699: astore_1
    //   700: ldc -37
    //   702: aload_1
    //   703: invokestatic 215	net/hockeyapp/android/utils/HockeyLog:error	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   706: goto -95 -> 611
    //   709: astore_1
    //   710: ldc -35
    //   712: aload_1
    //   713: invokestatic 215	net/hockeyapp/android/utils/HockeyLog:error	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   716: goto -93 -> 623
    //   719: astore_1
    //   720: ldc -33
    //   722: aload_1
    //   723: invokestatic 215	net/hockeyapp/android/utils/HockeyLog:error	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   726: goto -91 -> 635
    //   729: astore_3
    //   730: ldc -31
    //   732: aload_3
    //   733: invokestatic 215	net/hockeyapp/android/utils/HockeyLog:error	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   736: goto -76 -> 660
    //   739: astore_3
    //   740: goto -52 -> 688
    //   743: astore_3
    //   744: ldc -29
    //   746: aload_3
    //   747: invokestatic 215	net/hockeyapp/android/utils/HockeyLog:error	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   750: goto -79 -> 671
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	753	0	this	FeedbackParser
    //   0	753	1	paramString	String
    //   1	686	2	localObject1	Object
    //   3	671	3	localObject2	Object
    //   685	6	3	localJSONException1	org.json.JSONException
    //   695	1	3	str1	String
    //   729	4	3	localJSONException2	org.json.JSONException
    //   739	1	3	localJSONException3	org.json.JSONException
    //   743	4	3	localJSONException4	org.json.JSONException
    //   11	651	4	localJSONObject1	org.json.JSONObject
    //   26	600	5	localJSONObject2	org.json.JSONObject
    //   31	614	6	localFeedback	net.hockeyapp.android.objects.Feedback
    //   45	218	7	localJSONArray1	org.json.JSONArray
    //   66	510	8	i	int
    //   92	438	9	str2	String
    //   106	431	10	str3	String
    //   120	396	11	str4	String
    //   134	368	12	str5	String
    //   148	375	13	str6	String
    //   162	326	14	str7	String
    //   176	319	15	j	int
    //   190	354	16	str8	String
    //   204	354	17	k	int
    //   218	333	18	str9	String
    //   232	249	19	str10	String
    //   246	263	20	str11	String
    //   260	214	21	str12	String
    //   274	107	22	localJSONArray2	org.json.JSONArray
    //   288	282	23	localObject3	Object
    //   296	160	24	m	int
    //   323	84	25	n	int
    //   337	77	26	i1	int
    //   351	70	27	str13	String
    //   378	56	28	str14	String
    //   392	49	29	str15	String
    //   397	51	30	localFeedbackAttachment	net.hockeyapp.android.objects.FeedbackAttachment
    // Exception table:
    //   from	to	target	type
    //   587	599	675	org/json/JSONException
    //   8	47	685	org/json/JSONException
    //   49	65	685	org/json/JSONException
    //   70	280	685	org/json/JSONException
    //   285	295	685	org/json/JSONException
    //   301	455	685	org/json/JSONException
    //   461	575	685	org/json/JSONException
    //   581	587	685	org/json/JSONException
    //   635	643	685	org/json/JSONException
    //   676	682	685	org/json/JSONException
    //   700	706	685	org/json/JSONException
    //   710	716	685	org/json/JSONException
    //   720	726	685	org/json/JSONException
    //   599	611	699	org/json/JSONException
    //   611	623	709	org/json/JSONException
    //   623	635	719	org/json/JSONException
    //   649	660	729	org/json/JSONException
    //   643	649	739	org/json/JSONException
    //   730	736	739	org/json/JSONException
    //   744	750	739	org/json/JSONException
    //   660	671	743	org/json/JSONException
  }
  
  private static class FeedbackParserHolder
  {
    static final FeedbackParser INSTANCE = new FeedbackParser(null);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/utils/FeedbackParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */