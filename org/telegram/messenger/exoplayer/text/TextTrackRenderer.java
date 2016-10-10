package org.telegram.messenger.exoplayer.text;

import android.annotation.TargetApi;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.telegram.messenger.exoplayer.ExoPlaybackException;
import org.telegram.messenger.exoplayer.MediaFormat;
import org.telegram.messenger.exoplayer.MediaFormatHolder;
import org.telegram.messenger.exoplayer.SampleHolder;
import org.telegram.messenger.exoplayer.SampleSource;
import org.telegram.messenger.exoplayer.SampleSourceTrackRenderer;
import org.telegram.messenger.exoplayer.util.Assertions;

@TargetApi(16)
public final class TextTrackRenderer
  extends SampleSourceTrackRenderer
  implements Handler.Callback
{
  private static final List<Class<? extends SubtitleParser>> DEFAULT_PARSER_CLASSES = new ArrayList();
  private static final int MSG_UPDATE_OVERLAY = 0;
  private final MediaFormatHolder formatHolder;
  private boolean inputStreamEnded;
  private PlayableSubtitle nextSubtitle;
  private int nextSubtitleEventIndex;
  private SubtitleParserHelper parserHelper;
  private int parserIndex;
  private HandlerThread parserThread;
  private PlayableSubtitle subtitle;
  private final SubtitleParser[] subtitleParsers;
  private final TextRenderer textRenderer;
  private final Handler textRendererHandler;
  
  static
  {
    try
    {
      DEFAULT_PARSER_CLASSES.add(Class.forName("org.telegram.messenger.exoplayer.text.webvtt.WebvttParser").asSubclass(SubtitleParser.class));
      try
      {
        DEFAULT_PARSER_CLASSES.add(Class.forName("org.telegram.messenger.exoplayer.text.ttml.TtmlParser").asSubclass(SubtitleParser.class));
        try
        {
          DEFAULT_PARSER_CLASSES.add(Class.forName("org.telegram.messenger.exoplayer.text.webvtt.Mp4WebvttParser").asSubclass(SubtitleParser.class));
          try
          {
            DEFAULT_PARSER_CLASSES.add(Class.forName("org.telegram.messenger.exoplayer.text.subrip.SubripParser").asSubclass(SubtitleParser.class));
            try
            {
              DEFAULT_PARSER_CLASSES.add(Class.forName("org.telegram.messenger.exoplayer.text.tx3g.Tx3gParser").asSubclass(SubtitleParser.class));
              return;
            }
            catch (ClassNotFoundException localClassNotFoundException1) {}
          }
          catch (ClassNotFoundException localClassNotFoundException2)
          {
            for (;;) {}
          }
        }
        catch (ClassNotFoundException localClassNotFoundException3)
        {
          for (;;) {}
        }
      }
      catch (ClassNotFoundException localClassNotFoundException4)
      {
        for (;;) {}
      }
    }
    catch (ClassNotFoundException localClassNotFoundException5)
    {
      for (;;) {}
    }
  }
  
  public TextTrackRenderer(SampleSource paramSampleSource, TextRenderer paramTextRenderer, Looper paramLooper, SubtitleParser... paramVarArgs)
  {
    this(new SampleSource[] { paramSampleSource }, paramTextRenderer, paramLooper, paramVarArgs);
  }
  
  public TextTrackRenderer(SampleSource[] paramArrayOfSampleSource, TextRenderer paramTextRenderer, Looper paramLooper, SubtitleParser... paramVarArgs)
  {
    super(paramArrayOfSampleSource);
    this.textRenderer = ((TextRenderer)Assertions.checkNotNull(paramTextRenderer));
    if (paramLooper == null) {}
    for (paramArrayOfSampleSource = null;; paramArrayOfSampleSource = new Handler(paramLooper, this))
    {
      this.textRendererHandler = paramArrayOfSampleSource;
      if (paramVarArgs != null)
      {
        paramArrayOfSampleSource = paramVarArgs;
        if (paramVarArgs.length != 0) {
          break;
        }
      }
      else
      {
        paramTextRenderer = new SubtitleParser[DEFAULT_PARSER_CLASSES.size()];
        int i = 0;
        for (;;)
        {
          paramArrayOfSampleSource = paramTextRenderer;
          if (i >= paramTextRenderer.length) {
            break;
          }
          try
          {
            paramTextRenderer[i] = ((SubtitleParser)((Class)DEFAULT_PARSER_CLASSES.get(i)).newInstance());
            i += 1;
          }
          catch (InstantiationException paramArrayOfSampleSource)
          {
            throw new IllegalStateException("Unexpected error creating default parser", paramArrayOfSampleSource);
          }
          catch (IllegalAccessException paramArrayOfSampleSource)
          {
            throw new IllegalStateException("Unexpected error creating default parser", paramArrayOfSampleSource);
          }
        }
      }
    }
    this.subtitleParsers = paramArrayOfSampleSource;
    this.formatHolder = new MediaFormatHolder();
  }
  
  private void clearTextRenderer()
  {
    updateTextRenderer(Collections.emptyList());
  }
  
  private long getNextEventTime()
  {
    if ((this.nextSubtitleEventIndex == -1) || (this.nextSubtitleEventIndex >= this.subtitle.getEventTimeCount())) {
      return Long.MAX_VALUE;
    }
    return this.subtitle.getEventTime(this.nextSubtitleEventIndex);
  }
  
  private int getParserIndex(MediaFormat paramMediaFormat)
  {
    int i = 0;
    while (i < this.subtitleParsers.length)
    {
      if (this.subtitleParsers[i].canParse(paramMediaFormat.mimeType)) {
        return i;
      }
      i += 1;
    }
    return -1;
  }
  
  private void invokeRendererInternalCues(List<Cue> paramList)
  {
    this.textRenderer.onCues(paramList);
  }
  
  private void updateTextRenderer(List<Cue> paramList)
  {
    if (this.textRendererHandler != null)
    {
      this.textRendererHandler.obtainMessage(0, paramList).sendToTarget();
      return;
    }
    invokeRendererInternalCues(paramList);
  }
  
  protected void doSomeWork(long paramLong1, long paramLong2, boolean paramBoolean)
    throws ExoPlaybackException
  {
    if (this.nextSubtitle == null) {}
    int i;
    do
    {
      do
      {
        try
        {
          this.nextSubtitle = this.parserHelper.getAndClearResult();
          if (getState() != 3) {
            return;
          }
        }
        catch (IOException localIOException)
        {
          throw new ExoPlaybackException(localIOException);
        }
        i = 0;
        int j = 0;
        if (this.subtitle != null)
        {
          paramLong2 = getNextEventTime();
          for (i = j; paramLong2 <= paramLong1; i = 1)
          {
            this.nextSubtitleEventIndex += 1;
            paramLong2 = getNextEventTime();
          }
        }
        j = i;
        if (this.nextSubtitle != null)
        {
          j = i;
          if (this.nextSubtitle.startTimeUs <= paramLong1)
          {
            this.subtitle = this.nextSubtitle;
            this.nextSubtitle = null;
            this.nextSubtitleEventIndex = this.subtitle.getNextEventTimeIndex(paramLong1);
            j = 1;
          }
        }
        if (j != 0) {
          updateTextRenderer(this.subtitle.getCues(paramLong1));
        }
      } while ((this.inputStreamEnded) || (this.nextSubtitle != null) || (this.parserHelper.isParsing()));
      SampleHolder localSampleHolder = this.parserHelper.getSampleHolder();
      localSampleHolder.clearData();
      i = readSource(paramLong1, this.formatHolder, localSampleHolder);
      if (i == -4)
      {
        this.parserHelper.setFormat(this.formatHolder.format);
        return;
      }
      if (i == -3)
      {
        this.parserHelper.startParseOperation();
        return;
      }
    } while (i != -1);
    this.inputStreamEnded = true;
  }
  
  protected long getBufferedPositionUs()
  {
    return -3L;
  }
  
  public boolean handleMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    default: 
      return false;
    }
    invokeRendererInternalCues((List)paramMessage.obj);
    return true;
  }
  
  protected boolean handlesTrack(MediaFormat paramMediaFormat)
  {
    return getParserIndex(paramMediaFormat) != -1;
  }
  
  protected boolean isEnded()
  {
    return (this.inputStreamEnded) && ((this.subtitle == null) || (getNextEventTime() == Long.MAX_VALUE));
  }
  
  protected boolean isReady()
  {
    return true;
  }
  
  protected void onDisabled()
    throws ExoPlaybackException
  {
    this.subtitle = null;
    this.nextSubtitle = null;
    this.parserThread.quit();
    this.parserThread = null;
    this.parserHelper = null;
    clearTextRenderer();
    super.onDisabled();
  }
  
  protected void onDiscontinuity(long paramLong)
  {
    this.inputStreamEnded = false;
    this.subtitle = null;
    this.nextSubtitle = null;
    clearTextRenderer();
    if (this.parserHelper != null) {
      this.parserHelper.flush();
    }
  }
  
  protected void onEnabled(int paramInt, long paramLong, boolean paramBoolean)
    throws ExoPlaybackException
  {
    super.onEnabled(paramInt, paramLong, paramBoolean);
    this.parserIndex = getParserIndex(getFormat(paramInt));
    this.parserThread = new HandlerThread("textParser");
    this.parserThread.start();
    this.parserHelper = new SubtitleParserHelper(this.parserThread.getLooper(), this.subtitleParsers[this.parserIndex]);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/text/TextTrackRenderer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */