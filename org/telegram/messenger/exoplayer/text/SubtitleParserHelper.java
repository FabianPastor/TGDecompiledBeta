package org.telegram.messenger.exoplayer.text;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import java.io.IOException;
import org.telegram.messenger.exoplayer.MediaFormat;
import org.telegram.messenger.exoplayer.SampleHolder;
import org.telegram.messenger.exoplayer.util.Util;

final class SubtitleParserHelper
  implements Handler.Callback
{
  private static final int MSG_FORMAT = 0;
  private static final int MSG_SAMPLE = 1;
  private IOException error;
  private final Handler handler = new Handler(paramLooper, this);
  private final SubtitleParser parser;
  private boolean parsing;
  private PlayableSubtitle result;
  private RuntimeException runtimeError;
  private SampleHolder sampleHolder;
  private long subtitleOffsetUs;
  private boolean subtitlesAreRelative;
  
  public SubtitleParserHelper(Looper paramLooper, SubtitleParser paramSubtitleParser)
  {
    this.parser = paramSubtitleParser;
    flush();
  }
  
  private void handleFormat(MediaFormat paramMediaFormat)
  {
    boolean bool;
    if (paramMediaFormat.subsampleOffsetUs == Long.MAX_VALUE)
    {
      bool = true;
      this.subtitlesAreRelative = bool;
      if (!this.subtitlesAreRelative) {
        break label38;
      }
    }
    label38:
    for (long l = 0L;; l = paramMediaFormat.subsampleOffsetUs)
    {
      this.subtitleOffsetUs = l;
      return;
      bool = false;
      break;
    }
  }
  
  /* Error */
  private void handleSample(long paramLong, SampleHolder paramSampleHolder)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 4
    //   3: aconst_null
    //   4: astore 5
    //   6: aconst_null
    //   7: astore 6
    //   9: aload_0
    //   10: getfield 42	org/telegram/messenger/exoplayer/text/SubtitleParserHelper:parser	Lorg/telegram/messenger/exoplayer/text/SubtitleParser;
    //   13: aload_3
    //   14: getfield 71	org/telegram/messenger/exoplayer/SampleHolder:data	Ljava/nio/ByteBuffer;
    //   17: invokevirtual 77	java/nio/ByteBuffer:array	()[B
    //   20: iconst_0
    //   21: aload_3
    //   22: getfield 80	org/telegram/messenger/exoplayer/SampleHolder:size	I
    //   25: invokeinterface 86 4 0
    //   30: astore 7
    //   32: aload 7
    //   34: astore 4
    //   36: aload_0
    //   37: monitorenter
    //   38: aload_0
    //   39: getfield 88	org/telegram/messenger/exoplayer/text/SubtitleParserHelper:sampleHolder	Lorg/telegram/messenger/exoplayer/SampleHolder;
    //   42: aload_3
    //   43: if_acmpeq +6 -> 49
    //   46: aload_0
    //   47: monitorexit
    //   48: return
    //   49: aload_0
    //   50: new 90	org/telegram/messenger/exoplayer/text/PlayableSubtitle
    //   53: dup
    //   54: aload 4
    //   56: aload_0
    //   57: getfield 57	org/telegram/messenger/exoplayer/text/SubtitleParserHelper:subtitlesAreRelative	Z
    //   60: lload_1
    //   61: aload_0
    //   62: getfield 59	org/telegram/messenger/exoplayer/text/SubtitleParserHelper:subtitleOffsetUs	J
    //   65: invokespecial 93	org/telegram/messenger/exoplayer/text/PlayableSubtitle:<init>	(Lorg/telegram/messenger/exoplayer/text/Subtitle;ZJJ)V
    //   68: putfield 95	org/telegram/messenger/exoplayer/text/SubtitleParserHelper:result	Lorg/telegram/messenger/exoplayer/text/PlayableSubtitle;
    //   71: aload_0
    //   72: aload 5
    //   74: putfield 97	org/telegram/messenger/exoplayer/text/SubtitleParserHelper:error	Ljava/io/IOException;
    //   77: aload_0
    //   78: aload 6
    //   80: putfield 99	org/telegram/messenger/exoplayer/text/SubtitleParserHelper:runtimeError	Ljava/lang/RuntimeException;
    //   83: aload_0
    //   84: iconst_0
    //   85: putfield 101	org/telegram/messenger/exoplayer/text/SubtitleParserHelper:parsing	Z
    //   88: goto -42 -> 46
    //   91: astore_3
    //   92: aload_0
    //   93: monitorexit
    //   94: aload_3
    //   95: athrow
    //   96: astore 5
    //   98: goto -62 -> 36
    //   101: astore 6
    //   103: goto -67 -> 36
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	106	0	this	SubtitleParserHelper
    //   0	106	1	paramLong	long
    //   0	106	3	paramSampleHolder	SampleHolder
    //   1	54	4	localObject	Object
    //   4	69	5	localIOException	IOException
    //   96	1	5	localParserException	org.telegram.messenger.exoplayer.ParserException
    //   7	72	6	localRuntimeException1	RuntimeException
    //   101	1	6	localRuntimeException2	RuntimeException
    //   30	3	7	localSubtitle	Subtitle
    // Exception table:
    //   from	to	target	type
    //   38	46	91	finally
    //   46	48	91	finally
    //   49	88	91	finally
    //   92	94	91	finally
    //   9	32	96	org/telegram/messenger/exoplayer/ParserException
    //   9	32	101	java/lang/RuntimeException
  }
  
  public void flush()
  {
    try
    {
      this.sampleHolder = new SampleHolder(1);
      this.parsing = false;
      this.result = null;
      this.error = null;
      this.runtimeError = null;
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public PlayableSubtitle getAndClearResult()
    throws IOException
  {
    try
    {
      if (this.error != null) {
        throw this.error;
      }
    }
    finally
    {
      try
      {
        this.result = null;
        this.error = null;
        this.runtimeError = null;
        throw ((Throwable)localObject1);
      }
      finally {}
      if (this.runtimeError != null) {
        throw this.runtimeError;
      }
      PlayableSubtitle localPlayableSubtitle = this.result;
      this.result = null;
      this.error = null;
      this.runtimeError = null;
      return localPlayableSubtitle;
    }
  }
  
  public SampleHolder getSampleHolder()
  {
    try
    {
      SampleHolder localSampleHolder = this.sampleHolder;
      return localSampleHolder;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public boolean handleMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    }
    for (;;)
    {
      return true;
      handleFormat((MediaFormat)paramMessage.obj);
      continue;
      handleSample(Util.getLong(paramMessage.arg1, paramMessage.arg2), (SampleHolder)paramMessage.obj);
    }
  }
  
  public boolean isParsing()
  {
    try
    {
      boolean bool = this.parsing;
      return bool;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public void setFormat(MediaFormat paramMediaFormat)
  {
    this.handler.obtainMessage(0, paramMediaFormat).sendToTarget();
  }
  
  /* Error */
  public void startParseOperation()
  {
    // Byte code:
    //   0: iconst_1
    //   1: istore_1
    //   2: aload_0
    //   3: monitorenter
    //   4: aload_0
    //   5: getfield 101	org/telegram/messenger/exoplayer/text/SubtitleParserHelper:parsing	Z
    //   8: ifne +65 -> 73
    //   11: iload_1
    //   12: invokestatic 155	org/telegram/messenger/exoplayer/util/Assertions:checkState	(Z)V
    //   15: aload_0
    //   16: iconst_1
    //   17: putfield 101	org/telegram/messenger/exoplayer/text/SubtitleParserHelper:parsing	Z
    //   20: aload_0
    //   21: aconst_null
    //   22: putfield 95	org/telegram/messenger/exoplayer/text/SubtitleParserHelper:result	Lorg/telegram/messenger/exoplayer/text/PlayableSubtitle;
    //   25: aload_0
    //   26: aconst_null
    //   27: putfield 97	org/telegram/messenger/exoplayer/text/SubtitleParserHelper:error	Ljava/io/IOException;
    //   30: aload_0
    //   31: aconst_null
    //   32: putfield 99	org/telegram/messenger/exoplayer/text/SubtitleParserHelper:runtimeError	Ljava/lang/RuntimeException;
    //   35: aload_0
    //   36: getfield 40	org/telegram/messenger/exoplayer/text/SubtitleParserHelper:handler	Landroid/os/Handler;
    //   39: iconst_1
    //   40: aload_0
    //   41: getfield 88	org/telegram/messenger/exoplayer/text/SubtitleParserHelper:sampleHolder	Lorg/telegram/messenger/exoplayer/SampleHolder;
    //   44: getfield 158	org/telegram/messenger/exoplayer/SampleHolder:timeUs	J
    //   47: invokestatic 162	org/telegram/messenger/exoplayer/util/Util:getTopInt	(J)I
    //   50: aload_0
    //   51: getfield 88	org/telegram/messenger/exoplayer/text/SubtitleParserHelper:sampleHolder	Lorg/telegram/messenger/exoplayer/SampleHolder;
    //   54: getfield 158	org/telegram/messenger/exoplayer/SampleHolder:timeUs	J
    //   57: invokestatic 165	org/telegram/messenger/exoplayer/util/Util:getBottomInt	(J)I
    //   60: aload_0
    //   61: getfield 88	org/telegram/messenger/exoplayer/text/SubtitleParserHelper:sampleHolder	Lorg/telegram/messenger/exoplayer/SampleHolder;
    //   64: invokevirtual 168	android/os/Handler:obtainMessage	(IIILjava/lang/Object;)Landroid/os/Message;
    //   67: invokevirtual 148	android/os/Message:sendToTarget	()V
    //   70: aload_0
    //   71: monitorexit
    //   72: return
    //   73: iconst_0
    //   74: istore_1
    //   75: goto -64 -> 11
    //   78: astore_2
    //   79: aload_0
    //   80: monitorexit
    //   81: aload_2
    //   82: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	83	0	this	SubtitleParserHelper
    //   1	74	1	bool	boolean
    //   78	4	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   4	11	78	finally
    //   11	70	78	finally
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/text/SubtitleParserHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */