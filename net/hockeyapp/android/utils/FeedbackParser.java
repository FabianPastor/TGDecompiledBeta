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
    //   1: astore 9
    //   3: aconst_null
    //   4: astore 8
    //   6: aload_1
    //   7: ifnull +693 -> 700
    //   10: new 28	org/json/JSONObject
    //   13: dup
    //   14: aload_1
    //   15: invokespecial 31	org/json/JSONObject:<init>	(Ljava/lang/String;)V
    //   18: astore 11
    //   20: aload 11
    //   22: ldc 33
    //   24: invokevirtual 37	org/json/JSONObject:getJSONObject	(Ljava/lang/String;)Lorg/json/JSONObject;
    //   27: astore 13
    //   29: new 39	net/hockeyapp/android/objects/Feedback
    //   32: dup
    //   33: invokespecial 40	net/hockeyapp/android/objects/Feedback:<init>	()V
    //   36: astore 12
    //   38: aload 13
    //   40: ldc 42
    //   42: invokevirtual 46	org/json/JSONObject:getJSONArray	(Ljava/lang/String;)Lorg/json/JSONArray;
    //   45: astore 14
    //   47: aconst_null
    //   48: astore_1
    //   49: aload 14
    //   51: invokevirtual 52	org/json/JSONArray:length	()I
    //   54: ifle +538 -> 592
    //   57: new 54	java/util/ArrayList
    //   60: dup
    //   61: invokespecial 55	java/util/ArrayList:<init>	()V
    //   64: astore 8
    //   66: iconst_0
    //   67: istore_2
    //   68: aload 8
    //   70: astore_1
    //   71: iload_2
    //   72: aload 14
    //   74: invokevirtual 52	org/json/JSONArray:length	()I
    //   77: if_icmpge +515 -> 592
    //   80: aload 14
    //   82: iload_2
    //   83: invokevirtual 58	org/json/JSONArray:getJSONObject	(I)Lorg/json/JSONObject;
    //   86: ldc 60
    //   88: invokevirtual 64	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   91: invokevirtual 70	java/lang/String:toString	()Ljava/lang/String;
    //   94: astore 15
    //   96: aload 14
    //   98: iload_2
    //   99: invokevirtual 58	org/json/JSONArray:getJSONObject	(I)Lorg/json/JSONObject;
    //   102: ldc 72
    //   104: invokevirtual 64	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   107: invokevirtual 70	java/lang/String:toString	()Ljava/lang/String;
    //   110: astore 16
    //   112: aload 14
    //   114: iload_2
    //   115: invokevirtual 58	org/json/JSONArray:getJSONObject	(I)Lorg/json/JSONObject;
    //   118: ldc 74
    //   120: invokevirtual 64	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   123: invokevirtual 70	java/lang/String:toString	()Ljava/lang/String;
    //   126: astore 17
    //   128: aload 14
    //   130: iload_2
    //   131: invokevirtual 58	org/json/JSONArray:getJSONObject	(I)Lorg/json/JSONObject;
    //   134: ldc 76
    //   136: invokevirtual 64	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   139: invokevirtual 70	java/lang/String:toString	()Ljava/lang/String;
    //   142: astore 18
    //   144: aload 14
    //   146: iload_2
    //   147: invokevirtual 58	org/json/JSONArray:getJSONObject	(I)Lorg/json/JSONObject;
    //   150: ldc 78
    //   152: invokevirtual 64	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   155: invokevirtual 70	java/lang/String:toString	()Ljava/lang/String;
    //   158: astore 19
    //   160: aload 14
    //   162: iload_2
    //   163: invokevirtual 58	org/json/JSONArray:getJSONObject	(I)Lorg/json/JSONObject;
    //   166: ldc 80
    //   168: invokevirtual 64	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   171: invokevirtual 70	java/lang/String:toString	()Ljava/lang/String;
    //   174: astore 20
    //   176: aload 14
    //   178: iload_2
    //   179: invokevirtual 58	org/json/JSONArray:getJSONObject	(I)Lorg/json/JSONObject;
    //   182: ldc 82
    //   184: invokevirtual 86	org/json/JSONObject:getInt	(Ljava/lang/String;)I
    //   187: istore 4
    //   189: aload 14
    //   191: iload_2
    //   192: invokevirtual 58	org/json/JSONArray:getJSONObject	(I)Lorg/json/JSONObject;
    //   195: ldc 88
    //   197: invokevirtual 64	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   200: invokevirtual 70	java/lang/String:toString	()Ljava/lang/String;
    //   203: astore 21
    //   205: aload 14
    //   207: iload_2
    //   208: invokevirtual 58	org/json/JSONArray:getJSONObject	(I)Lorg/json/JSONObject;
    //   211: ldc 90
    //   213: invokevirtual 86	org/json/JSONObject:getInt	(Ljava/lang/String;)I
    //   216: istore 5
    //   218: aload 14
    //   220: iload_2
    //   221: invokevirtual 58	org/json/JSONArray:getJSONObject	(I)Lorg/json/JSONObject;
    //   224: ldc 92
    //   226: invokevirtual 64	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   229: invokevirtual 70	java/lang/String:toString	()Ljava/lang/String;
    //   232: astore 22
    //   234: aload 14
    //   236: iload_2
    //   237: invokevirtual 58	org/json/JSONArray:getJSONObject	(I)Lorg/json/JSONObject;
    //   240: ldc 94
    //   242: invokevirtual 64	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   245: invokevirtual 70	java/lang/String:toString	()Ljava/lang/String;
    //   248: astore 23
    //   250: aload 14
    //   252: iload_2
    //   253: invokevirtual 58	org/json/JSONArray:getJSONObject	(I)Lorg/json/JSONObject;
    //   256: ldc 96
    //   258: invokevirtual 64	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   261: invokevirtual 70	java/lang/String:toString	()Ljava/lang/String;
    //   264: astore 24
    //   266: aload 14
    //   268: iload_2
    //   269: invokevirtual 58	org/json/JSONArray:getJSONObject	(I)Lorg/json/JSONObject;
    //   272: ldc 98
    //   274: invokevirtual 64	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   277: invokevirtual 70	java/lang/String:toString	()Ljava/lang/String;
    //   280: astore 25
    //   282: aload 14
    //   284: iload_2
    //   285: invokevirtual 58	org/json/JSONArray:getJSONObject	(I)Lorg/json/JSONObject;
    //   288: ldc 100
    //   290: invokevirtual 103	org/json/JSONObject:optJSONArray	(Ljava/lang/String;)Lorg/json/JSONArray;
    //   293: astore 26
    //   295: invokestatic 109	java/util/Collections:emptyList	()Ljava/util/List;
    //   298: astore_1
    //   299: aload 26
    //   301: ifnull +170 -> 471
    //   304: new 54	java/util/ArrayList
    //   307: dup
    //   308: invokespecial 55	java/util/ArrayList:<init>	()V
    //   311: astore 10
    //   313: iconst_0
    //   314: istore_3
    //   315: aload 10
    //   317: astore_1
    //   318: iload_3
    //   319: aload 26
    //   321: invokevirtual 52	org/json/JSONArray:length	()I
    //   324: if_icmpge +147 -> 471
    //   327: aload 26
    //   329: iload_3
    //   330: invokevirtual 58	org/json/JSONArray:getJSONObject	(I)Lorg/json/JSONObject;
    //   333: ldc 82
    //   335: invokevirtual 86	org/json/JSONObject:getInt	(Ljava/lang/String;)I
    //   338: istore 6
    //   340: aload 26
    //   342: iload_3
    //   343: invokevirtual 58	org/json/JSONArray:getJSONObject	(I)Lorg/json/JSONObject;
    //   346: ldc 111
    //   348: invokevirtual 86	org/json/JSONObject:getInt	(Ljava/lang/String;)I
    //   351: istore 7
    //   353: aload 26
    //   355: iload_3
    //   356: invokevirtual 58	org/json/JSONArray:getJSONObject	(I)Lorg/json/JSONObject;
    //   359: ldc 113
    //   361: invokevirtual 64	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   364: astore_1
    //   365: aload 26
    //   367: iload_3
    //   368: invokevirtual 58	org/json/JSONArray:getJSONObject	(I)Lorg/json/JSONObject;
    //   371: ldc 115
    //   373: invokevirtual 64	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   376: astore 27
    //   378: aload 26
    //   380: iload_3
    //   381: invokevirtual 58	org/json/JSONArray:getJSONObject	(I)Lorg/json/JSONObject;
    //   384: ldc 80
    //   386: invokevirtual 64	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   389: astore 28
    //   391: aload 26
    //   393: iload_3
    //   394: invokevirtual 58	org/json/JSONArray:getJSONObject	(I)Lorg/json/JSONObject;
    //   397: ldc 117
    //   399: invokevirtual 64	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   402: astore 29
    //   404: new 119	net/hockeyapp/android/objects/FeedbackAttachment
    //   407: dup
    //   408: invokespecial 120	net/hockeyapp/android/objects/FeedbackAttachment:<init>	()V
    //   411: astore 30
    //   413: aload 30
    //   415: iload 6
    //   417: invokevirtual 124	net/hockeyapp/android/objects/FeedbackAttachment:setId	(I)V
    //   420: aload 30
    //   422: iload 7
    //   424: invokevirtual 127	net/hockeyapp/android/objects/FeedbackAttachment:setMessageId	(I)V
    //   427: aload 30
    //   429: aload_1
    //   430: invokevirtual 130	net/hockeyapp/android/objects/FeedbackAttachment:setFilename	(Ljava/lang/String;)V
    //   433: aload 30
    //   435: aload 27
    //   437: invokevirtual 133	net/hockeyapp/android/objects/FeedbackAttachment:setUrl	(Ljava/lang/String;)V
    //   440: aload 30
    //   442: aload 28
    //   444: invokevirtual 136	net/hockeyapp/android/objects/FeedbackAttachment:setCreatedAt	(Ljava/lang/String;)V
    //   447: aload 30
    //   449: aload 29
    //   451: invokevirtual 139	net/hockeyapp/android/objects/FeedbackAttachment:setUpdatedAt	(Ljava/lang/String;)V
    //   454: aload 10
    //   456: aload 30
    //   458: invokeinterface 145 2 0
    //   463: pop
    //   464: iload_3
    //   465: iconst_1
    //   466: iadd
    //   467: istore_3
    //   468: goto -153 -> 315
    //   471: new 147	net/hockeyapp/android/objects/FeedbackMessage
    //   474: dup
    //   475: invokespecial 148	net/hockeyapp/android/objects/FeedbackMessage:<init>	()V
    //   478: astore 10
    //   480: aload 10
    //   482: aload 25
    //   484: invokevirtual 151	net/hockeyapp/android/objects/FeedbackMessage:setAppId	(Ljava/lang/String;)V
    //   487: aload 10
    //   489: aload 23
    //   491: invokevirtual 154	net/hockeyapp/android/objects/FeedbackMessage:setCleanText	(Ljava/lang/String;)V
    //   494: aload 10
    //   496: aload 20
    //   498: invokevirtual 155	net/hockeyapp/android/objects/FeedbackMessage:setCreatedAt	(Ljava/lang/String;)V
    //   501: aload 10
    //   503: iload 4
    //   505: invokevirtual 156	net/hockeyapp/android/objects/FeedbackMessage:setId	(I)V
    //   508: aload 10
    //   510: aload 18
    //   512: invokevirtual 159	net/hockeyapp/android/objects/FeedbackMessage:setModel	(Ljava/lang/String;)V
    //   515: aload 10
    //   517: aload 24
    //   519: invokevirtual 162	net/hockeyapp/android/objects/FeedbackMessage:setName	(Ljava/lang/String;)V
    //   522: aload 10
    //   524: aload 17
    //   526: invokevirtual 165	net/hockeyapp/android/objects/FeedbackMessage:setOem	(Ljava/lang/String;)V
    //   529: aload 10
    //   531: aload 19
    //   533: invokevirtual 168	net/hockeyapp/android/objects/FeedbackMessage:setOsVersion	(Ljava/lang/String;)V
    //   536: aload 10
    //   538: aload 15
    //   540: invokevirtual 171	net/hockeyapp/android/objects/FeedbackMessage:setSubjec	(Ljava/lang/String;)V
    //   543: aload 10
    //   545: aload 16
    //   547: invokevirtual 174	net/hockeyapp/android/objects/FeedbackMessage:setText	(Ljava/lang/String;)V
    //   550: aload 10
    //   552: aload 21
    //   554: invokevirtual 177	net/hockeyapp/android/objects/FeedbackMessage:setToken	(Ljava/lang/String;)V
    //   557: aload 10
    //   559: aload 22
    //   561: invokevirtual 180	net/hockeyapp/android/objects/FeedbackMessage:setUserString	(Ljava/lang/String;)V
    //   564: aload 10
    //   566: iload 5
    //   568: invokevirtual 183	net/hockeyapp/android/objects/FeedbackMessage:setVia	(I)V
    //   571: aload 10
    //   573: aload_1
    //   574: invokevirtual 187	net/hockeyapp/android/objects/FeedbackMessage:setFeedbackAttachments	(Ljava/util/List;)V
    //   577: aload 8
    //   579: aload 10
    //   581: invokevirtual 188	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   584: pop
    //   585: iload_2
    //   586: iconst_1
    //   587: iadd
    //   588: istore_2
    //   589: goto -521 -> 68
    //   592: aload 12
    //   594: aload_1
    //   595: invokevirtual 192	net/hockeyapp/android/objects/Feedback:setMessages	(Ljava/util/ArrayList;)V
    //   598: aload 12
    //   600: aload 13
    //   602: ldc 96
    //   604: invokevirtual 64	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   607: invokevirtual 70	java/lang/String:toString	()Ljava/lang/String;
    //   610: invokevirtual 193	net/hockeyapp/android/objects/Feedback:setName	(Ljava/lang/String;)V
    //   613: aload 12
    //   615: aload 13
    //   617: ldc -61
    //   619: invokevirtual 64	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   622: invokevirtual 70	java/lang/String:toString	()Ljava/lang/String;
    //   625: invokevirtual 198	net/hockeyapp/android/objects/Feedback:setEmail	(Ljava/lang/String;)V
    //   628: aload 12
    //   630: aload 13
    //   632: ldc 82
    //   634: invokevirtual 86	org/json/JSONObject:getInt	(Ljava/lang/String;)I
    //   637: invokevirtual 199	net/hockeyapp/android/objects/Feedback:setId	(I)V
    //   640: aload 12
    //   642: aload 13
    //   644: ldc 80
    //   646: invokevirtual 64	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   649: invokevirtual 70	java/lang/String:toString	()Ljava/lang/String;
    //   652: invokevirtual 200	net/hockeyapp/android/objects/Feedback:setCreatedAt	(Ljava/lang/String;)V
    //   655: new 202	net/hockeyapp/android/objects/FeedbackResponse
    //   658: dup
    //   659: invokespecial 203	net/hockeyapp/android/objects/FeedbackResponse:<init>	()V
    //   662: astore_1
    //   663: aload_1
    //   664: aload 12
    //   666: invokevirtual 207	net/hockeyapp/android/objects/FeedbackResponse:setFeedback	(Lnet/hockeyapp/android/objects/Feedback;)V
    //   669: aload_1
    //   670: aload 11
    //   672: ldc -47
    //   674: invokevirtual 64	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   677: invokevirtual 70	java/lang/String:toString	()Ljava/lang/String;
    //   680: invokevirtual 212	net/hockeyapp/android/objects/FeedbackResponse:setStatus	(Ljava/lang/String;)V
    //   683: aload_1
    //   684: aload 11
    //   686: ldc 88
    //   688: invokevirtual 64	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   691: invokevirtual 70	java/lang/String:toString	()Ljava/lang/String;
    //   694: invokevirtual 213	net/hockeyapp/android/objects/FeedbackResponse:setToken	(Ljava/lang/String;)V
    //   697: aload_1
    //   698: astore 8
    //   700: aload 8
    //   702: areturn
    //   703: astore_1
    //   704: aload_1
    //   705: invokevirtual 216	org/json/JSONException:printStackTrace	()V
    //   708: goto -95 -> 613
    //   711: astore_1
    //   712: aload 9
    //   714: astore 8
    //   716: aload_1
    //   717: invokevirtual 216	org/json/JSONException:printStackTrace	()V
    //   720: aload 8
    //   722: areturn
    //   723: astore_1
    //   724: aload_1
    //   725: invokevirtual 216	org/json/JSONException:printStackTrace	()V
    //   728: goto -100 -> 628
    //   731: astore_1
    //   732: aload_1
    //   733: invokevirtual 216	org/json/JSONException:printStackTrace	()V
    //   736: goto -96 -> 640
    //   739: astore_1
    //   740: aload_1
    //   741: invokevirtual 216	org/json/JSONException:printStackTrace	()V
    //   744: goto -89 -> 655
    //   747: astore 8
    //   749: aload 8
    //   751: invokevirtual 216	org/json/JSONException:printStackTrace	()V
    //   754: goto -71 -> 683
    //   757: astore 8
    //   759: aload 8
    //   761: invokevirtual 216	org/json/JSONException:printStackTrace	()V
    //   764: goto -67 -> 697
    //   767: astore_1
    //   768: aload 9
    //   770: astore 8
    //   772: goto -56 -> 716
    //   775: astore 9
    //   777: aload_1
    //   778: astore 8
    //   780: aload 9
    //   782: astore_1
    //   783: goto -67 -> 716
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	786	0	this	FeedbackParser
    //   0	786	1	paramString	String
    //   67	522	2	i	int
    //   314	154	3	j	int
    //   187	317	4	k	int
    //   216	351	5	m	int
    //   338	78	6	n	int
    //   351	72	7	i1	int
    //   4	717	8	localObject1	Object
    //   747	3	8	localJSONException1	org.json.JSONException
    //   757	3	8	localJSONException2	org.json.JSONException
    //   770	9	8	localObject2	Object
    //   1	768	9	localObject3	Object
    //   775	6	9	localJSONException3	org.json.JSONException
    //   311	269	10	localObject4	Object
    //   18	667	11	localJSONObject1	org.json.JSONObject
    //   36	629	12	localFeedback	net.hockeyapp.android.objects.Feedback
    //   27	616	13	localJSONObject2	org.json.JSONObject
    //   45	238	14	localJSONArray1	org.json.JSONArray
    //   94	445	15	str1	String
    //   110	436	16	str2	String
    //   126	399	17	str3	String
    //   142	369	18	str4	String
    //   158	374	19	str5	String
    //   174	323	20	str6	String
    //   203	350	21	str7	String
    //   232	328	22	str8	String
    //   248	242	23	str9	String
    //   264	254	24	str10	String
    //   280	203	25	str11	String
    //   293	99	26	localJSONArray2	org.json.JSONArray
    //   376	60	27	str12	String
    //   389	54	28	str13	String
    //   402	48	29	str14	String
    //   411	46	30	localFeedbackAttachment	net.hockeyapp.android.objects.FeedbackAttachment
    // Exception table:
    //   from	to	target	type
    //   598	613	703	org/json/JSONException
    //   38	47	711	org/json/JSONException
    //   49	66	711	org/json/JSONException
    //   71	299	711	org/json/JSONException
    //   304	313	711	org/json/JSONException
    //   318	464	711	org/json/JSONException
    //   471	585	711	org/json/JSONException
    //   592	598	711	org/json/JSONException
    //   655	663	711	org/json/JSONException
    //   704	708	711	org/json/JSONException
    //   724	728	711	org/json/JSONException
    //   732	736	711	org/json/JSONException
    //   740	744	711	org/json/JSONException
    //   613	628	723	org/json/JSONException
    //   628	640	731	org/json/JSONException
    //   640	655	739	org/json/JSONException
    //   669	683	747	org/json/JSONException
    //   683	697	757	org/json/JSONException
    //   10	38	767	org/json/JSONException
    //   663	669	775	org/json/JSONException
    //   749	754	775	org/json/JSONException
    //   759	764	775	org/json/JSONException
  }
  
  private static class FeedbackParserHolder
  {
    public static final FeedbackParser INSTANCE = new FeedbackParser(null);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/utils/FeedbackParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */