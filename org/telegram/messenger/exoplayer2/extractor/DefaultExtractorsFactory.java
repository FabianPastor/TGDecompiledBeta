package org.telegram.messenger.exoplayer2.extractor;

import java.lang.reflect.Constructor;

public final class DefaultExtractorsFactory
  implements ExtractorsFactory
{
  private static final Constructor<? extends Extractor> FLAC_EXTRACTOR_CONSTRUCTOR;
  private int fragmentedMp4Flags;
  private int matroskaFlags;
  private int mp3Flags;
  private int mp4Flags;
  private int tsFlags;
  private int tsMode = 1;
  
  static
  {
    Object localObject = null;
    try
    {
      Constructor localConstructor = Class.forName("org.telegram.messenger.exoplayer2.ext.flac.FlacExtractor").asSubclass(Extractor.class).getConstructor(new Class[0]);
      localObject = localConstructor;
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      for (;;) {}
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      for (;;) {}
    }
    FLAC_EXTRACTOR_CONSTRUCTOR = (Constructor)localObject;
  }
  
  /* Error */
  public Extractor[] createExtractors()
  {
    // Byte code:
    //   0: bipush 11
    //   2: istore_1
    //   3: aload_0
    //   4: monitorenter
    //   5: getstatic 42	org/telegram/messenger/exoplayer2/extractor/DefaultExtractorsFactory:FLAC_EXTRACTOR_CONSTRUCTOR	Ljava/lang/reflect/Constructor;
    //   8: ifnonnull +198 -> 206
    //   11: iload_1
    //   12: anewarray 32	org/telegram/messenger/exoplayer2/extractor/Extractor
    //   15: astore_2
    //   16: new 54	org/telegram/messenger/exoplayer2/extractor/mkv/MatroskaExtractor
    //   19: astore_3
    //   20: aload_3
    //   21: aload_0
    //   22: getfield 56	org/telegram/messenger/exoplayer2/extractor/DefaultExtractorsFactory:matroskaFlags	I
    //   25: invokespecial 59	org/telegram/messenger/exoplayer2/extractor/mkv/MatroskaExtractor:<init>	(I)V
    //   28: aload_2
    //   29: iconst_0
    //   30: aload_3
    //   31: aastore
    //   32: new 61	org/telegram/messenger/exoplayer2/extractor/mp4/FragmentedMp4Extractor
    //   35: astore_3
    //   36: aload_3
    //   37: aload_0
    //   38: getfield 63	org/telegram/messenger/exoplayer2/extractor/DefaultExtractorsFactory:fragmentedMp4Flags	I
    //   41: invokespecial 64	org/telegram/messenger/exoplayer2/extractor/mp4/FragmentedMp4Extractor:<init>	(I)V
    //   44: aload_2
    //   45: iconst_1
    //   46: aload_3
    //   47: aastore
    //   48: new 66	org/telegram/messenger/exoplayer2/extractor/mp4/Mp4Extractor
    //   51: astore_3
    //   52: aload_3
    //   53: aload_0
    //   54: getfield 68	org/telegram/messenger/exoplayer2/extractor/DefaultExtractorsFactory:mp4Flags	I
    //   57: invokespecial 69	org/telegram/messenger/exoplayer2/extractor/mp4/Mp4Extractor:<init>	(I)V
    //   60: aload_2
    //   61: iconst_2
    //   62: aload_3
    //   63: aastore
    //   64: new 71	org/telegram/messenger/exoplayer2/extractor/mp3/Mp3Extractor
    //   67: astore_3
    //   68: aload_3
    //   69: aload_0
    //   70: getfield 73	org/telegram/messenger/exoplayer2/extractor/DefaultExtractorsFactory:mp3Flags	I
    //   73: invokespecial 74	org/telegram/messenger/exoplayer2/extractor/mp3/Mp3Extractor:<init>	(I)V
    //   76: aload_2
    //   77: iconst_3
    //   78: aload_3
    //   79: aastore
    //   80: new 76	org/telegram/messenger/exoplayer2/extractor/ts/AdtsExtractor
    //   83: astore_3
    //   84: aload_3
    //   85: invokespecial 77	org/telegram/messenger/exoplayer2/extractor/ts/AdtsExtractor:<init>	()V
    //   88: aload_2
    //   89: iconst_4
    //   90: aload_3
    //   91: aastore
    //   92: new 79	org/telegram/messenger/exoplayer2/extractor/ts/Ac3Extractor
    //   95: astore_3
    //   96: aload_3
    //   97: invokespecial 80	org/telegram/messenger/exoplayer2/extractor/ts/Ac3Extractor:<init>	()V
    //   100: aload_2
    //   101: iconst_5
    //   102: aload_3
    //   103: aastore
    //   104: new 82	org/telegram/messenger/exoplayer2/extractor/ts/TsExtractor
    //   107: astore_3
    //   108: aload_3
    //   109: aload_0
    //   110: getfield 48	org/telegram/messenger/exoplayer2/extractor/DefaultExtractorsFactory:tsMode	I
    //   113: aload_0
    //   114: getfield 84	org/telegram/messenger/exoplayer2/extractor/DefaultExtractorsFactory:tsFlags	I
    //   117: invokespecial 87	org/telegram/messenger/exoplayer2/extractor/ts/TsExtractor:<init>	(II)V
    //   120: aload_2
    //   121: bipush 6
    //   123: aload_3
    //   124: aastore
    //   125: new 89	org/telegram/messenger/exoplayer2/extractor/flv/FlvExtractor
    //   128: astore_3
    //   129: aload_3
    //   130: invokespecial 90	org/telegram/messenger/exoplayer2/extractor/flv/FlvExtractor:<init>	()V
    //   133: aload_2
    //   134: bipush 7
    //   136: aload_3
    //   137: aastore
    //   138: new 92	org/telegram/messenger/exoplayer2/extractor/ogg/OggExtractor
    //   141: astore_3
    //   142: aload_3
    //   143: invokespecial 93	org/telegram/messenger/exoplayer2/extractor/ogg/OggExtractor:<init>	()V
    //   146: aload_2
    //   147: bipush 8
    //   149: aload_3
    //   150: aastore
    //   151: new 95	org/telegram/messenger/exoplayer2/extractor/ts/PsExtractor
    //   154: astore_3
    //   155: aload_3
    //   156: invokespecial 96	org/telegram/messenger/exoplayer2/extractor/ts/PsExtractor:<init>	()V
    //   159: aload_2
    //   160: bipush 9
    //   162: aload_3
    //   163: aastore
    //   164: new 98	org/telegram/messenger/exoplayer2/extractor/wav/WavExtractor
    //   167: astore_3
    //   168: aload_3
    //   169: invokespecial 99	org/telegram/messenger/exoplayer2/extractor/wav/WavExtractor:<init>	()V
    //   172: aload_2
    //   173: bipush 10
    //   175: aload_3
    //   176: aastore
    //   177: getstatic 42	org/telegram/messenger/exoplayer2/extractor/DefaultExtractorsFactory:FLAC_EXTRACTOR_CONSTRUCTOR	Ljava/lang/reflect/Constructor;
    //   180: astore_3
    //   181: aload_3
    //   182: ifnull +20 -> 202
    //   185: aload_2
    //   186: bipush 11
    //   188: getstatic 42	org/telegram/messenger/exoplayer2/extractor/DefaultExtractorsFactory:FLAC_EXTRACTOR_CONSTRUCTOR	Ljava/lang/reflect/Constructor;
    //   191: iconst_0
    //   192: anewarray 4	java/lang/Object
    //   195: invokevirtual 105	java/lang/reflect/Constructor:newInstance	([Ljava/lang/Object;)Ljava/lang/Object;
    //   198: checkcast 32	org/telegram/messenger/exoplayer2/extractor/Extractor
    //   201: aastore
    //   202: aload_0
    //   203: monitorexit
    //   204: aload_2
    //   205: areturn
    //   206: bipush 12
    //   208: istore_1
    //   209: goto -198 -> 11
    //   212: astore_2
    //   213: new 107	java/lang/IllegalStateException
    //   216: astore_3
    //   217: aload_3
    //   218: ldc 109
    //   220: aload_2
    //   221: invokespecial 112	java/lang/IllegalStateException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   224: aload_3
    //   225: athrow
    //   226: astore_2
    //   227: aload_0
    //   228: monitorexit
    //   229: aload_2
    //   230: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	231	0	this	DefaultExtractorsFactory
    //   2	207	1	i	int
    //   15	190	2	arrayOfExtractor	Extractor[]
    //   212	9	2	localException	Exception
    //   226	4	2	localObject1	Object
    //   19	206	3	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   185	202	212	java/lang/Exception
    //   5	11	226	finally
    //   11	28	226	finally
    //   32	44	226	finally
    //   48	60	226	finally
    //   64	76	226	finally
    //   80	88	226	finally
    //   92	100	226	finally
    //   104	120	226	finally
    //   125	133	226	finally
    //   138	146	226	finally
    //   151	159	226	finally
    //   164	172	226	finally
    //   177	181	226	finally
    //   185	202	226	finally
    //   213	226	226	finally
  }
  
  public DefaultExtractorsFactory setFragmentedMp4ExtractorFlags(int paramInt)
  {
    try
    {
      this.fragmentedMp4Flags = paramInt;
      return this;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public DefaultExtractorsFactory setMatroskaExtractorFlags(int paramInt)
  {
    try
    {
      this.matroskaFlags = paramInt;
      return this;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public DefaultExtractorsFactory setMp3ExtractorFlags(int paramInt)
  {
    try
    {
      this.mp3Flags = paramInt;
      return this;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public DefaultExtractorsFactory setMp4ExtractorFlags(int paramInt)
  {
    try
    {
      this.mp4Flags = paramInt;
      return this;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public DefaultExtractorsFactory setTsExtractorFlags(int paramInt)
  {
    try
    {
      this.tsFlags = paramInt;
      return this;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public DefaultExtractorsFactory setTsExtractorMode(int paramInt)
  {
    try
    {
      this.tsMode = paramInt;
      return this;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/extractor/DefaultExtractorsFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */