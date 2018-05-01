package org.telegram.messenger.exoplayer2.text;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collections;
import java.util.List;
import org.telegram.messenger.exoplayer2.BaseRenderer;
import org.telegram.messenger.exoplayer2.ExoPlaybackException;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.FormatHolder;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.MimeTypes;

public final class TextRenderer
  extends BaseRenderer
  implements Handler.Callback
{
  private static final int MSG_UPDATE_OUTPUT = 0;
  private static final int REPLACEMENT_STATE_NONE = 0;
  private static final int REPLACEMENT_STATE_SIGNAL_END_OF_STREAM = 1;
  private static final int REPLACEMENT_STATE_WAIT_END_OF_STREAM = 2;
  private SubtitleDecoder decoder;
  private final SubtitleDecoderFactory decoderFactory;
  private int decoderReplacementState;
  private final FormatHolder formatHolder;
  private boolean inputStreamEnded;
  private SubtitleInputBuffer nextInputBuffer;
  private SubtitleOutputBuffer nextSubtitle;
  private int nextSubtitleEventIndex;
  private final TextOutput output;
  private final Handler outputHandler;
  private boolean outputStreamEnded;
  private Format streamFormat;
  private SubtitleOutputBuffer subtitle;
  
  public TextRenderer(TextOutput paramTextOutput, Looper paramLooper)
  {
    this(paramTextOutput, paramLooper, SubtitleDecoderFactory.DEFAULT);
  }
  
  public TextRenderer(TextOutput paramTextOutput, Looper paramLooper, SubtitleDecoderFactory paramSubtitleDecoderFactory)
  {
    super(3);
    this.output = ((TextOutput)Assertions.checkNotNull(paramTextOutput));
    if (paramLooper == null) {}
    for (paramTextOutput = null;; paramTextOutput = new Handler(paramLooper, this))
    {
      this.outputHandler = paramTextOutput;
      this.decoderFactory = paramSubtitleDecoderFactory;
      this.formatHolder = new FormatHolder();
      return;
    }
  }
  
  private void clearOutput()
  {
    updateOutput(Collections.emptyList());
  }
  
  private long getNextEventTime()
  {
    if ((this.nextSubtitleEventIndex == -1) || (this.nextSubtitleEventIndex >= this.subtitle.getEventTimeCount())) {}
    for (long l = Long.MAX_VALUE;; l = this.subtitle.getEventTime(this.nextSubtitleEventIndex)) {
      return l;
    }
  }
  
  private void invokeUpdateOutputInternal(List<Cue> paramList)
  {
    this.output.onCues(paramList);
  }
  
  private void releaseBuffers()
  {
    this.nextInputBuffer = null;
    this.nextSubtitleEventIndex = -1;
    if (this.subtitle != null)
    {
      this.subtitle.release();
      this.subtitle = null;
    }
    if (this.nextSubtitle != null)
    {
      this.nextSubtitle.release();
      this.nextSubtitle = null;
    }
  }
  
  private void releaseDecoder()
  {
    releaseBuffers();
    this.decoder.release();
    this.decoder = null;
    this.decoderReplacementState = 0;
  }
  
  private void replaceDecoder()
  {
    releaseDecoder();
    this.decoder = this.decoderFactory.createDecoder(this.streamFormat);
  }
  
  private void updateOutput(List<Cue> paramList)
  {
    if (this.outputHandler != null) {
      this.outputHandler.obtainMessage(0, paramList).sendToTarget();
    }
    for (;;)
    {
      return;
      invokeUpdateOutputInternal(paramList);
    }
  }
  
  public boolean handleMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    default: 
      throw new IllegalStateException();
    }
    invokeUpdateOutputInternal((List)paramMessage.obj);
    return true;
  }
  
  public boolean isEnded()
  {
    return this.outputStreamEnded;
  }
  
  public boolean isReady()
  {
    return true;
  }
  
  protected void onDisabled()
  {
    this.streamFormat = null;
    clearOutput();
    releaseDecoder();
  }
  
  protected void onPositionReset(long paramLong, boolean paramBoolean)
  {
    clearOutput();
    this.inputStreamEnded = false;
    this.outputStreamEnded = false;
    if (this.decoderReplacementState != 0) {
      replaceDecoder();
    }
    for (;;)
    {
      return;
      releaseBuffers();
      this.decoder.flush();
    }
  }
  
  protected void onStreamChanged(Format[] paramArrayOfFormat, long paramLong)
    throws ExoPlaybackException
  {
    this.streamFormat = paramArrayOfFormat[0];
    if (this.decoder != null) {
      this.decoderReplacementState = 1;
    }
    for (;;)
    {
      return;
      this.decoder = this.decoderFactory.createDecoder(this.streamFormat);
    }
  }
  
  public void render(long paramLong1, long paramLong2)
    throws ExoPlaybackException
  {
    if (this.outputStreamEnded) {}
    for (;;)
    {
      return;
      if (this.nextSubtitle == null) {
        this.decoder.setPositionUs(paramLong1);
      }
      int i;
      int j;
      try
      {
        this.nextSubtitle = ((SubtitleOutputBuffer)this.decoder.dequeueOutputBuffer());
        if (getState() != 2) {
          continue;
        }
        i = 0;
        j = 0;
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
      }
      catch (SubtitleDecoderException localSubtitleDecoderException1)
      {
        throw ExoPlaybackException.createForRenderer(localSubtitleDecoderException1, getIndex());
      }
      if (this.nextSubtitle != null)
      {
        if (!this.nextSubtitle.isEndOfStream()) {
          break label303;
        }
        j = i;
        if (i == 0)
        {
          j = i;
          if (getNextEventTime() == Long.MAX_VALUE)
          {
            if (this.decoderReplacementState != 2) {
              break label287;
            }
            replaceDecoder();
            j = i;
          }
        }
      }
      label171:
      if (j != 0) {
        updateOutput(this.subtitle.getCues(paramLong1));
      }
      if (this.decoderReplacementState != 2) {
        label287:
        label303:
        label364:
        do
        {
          try
          {
            if (this.inputStreamEnded) {
              break;
            }
            if (this.nextInputBuffer == null)
            {
              this.nextInputBuffer = ((SubtitleInputBuffer)this.decoder.dequeueInputBuffer());
              if (this.nextInputBuffer == null) {
                break;
              }
            }
            if (this.decoderReplacementState != 1) {
              break label364;
            }
            this.nextInputBuffer.setFlags(4);
            this.decoder.queueInputBuffer(this.nextInputBuffer);
            this.nextInputBuffer = null;
            this.decoderReplacementState = 2;
          }
          catch (SubtitleDecoderException localSubtitleDecoderException2)
          {
            throw ExoPlaybackException.createForRenderer(localSubtitleDecoderException2, getIndex());
          }
          releaseBuffers();
          this.outputStreamEnded = true;
          j = i;
          break label171;
          j = i;
          if (this.nextSubtitle.timeUs > paramLong1) {
            break label171;
          }
          if (this.subtitle != null) {
            this.subtitle.release();
          }
          this.subtitle = this.nextSubtitle;
          this.nextSubtitle = null;
          this.nextSubtitleEventIndex = this.subtitle.getNextEventTimeIndex(paramLong1);
          j = 1;
          break label171;
          i = readSource(this.formatHolder, this.nextInputBuffer, false);
          if (i == -4)
          {
            if (this.nextInputBuffer.isEndOfStream()) {
              this.inputStreamEnded = true;
            }
            for (;;)
            {
              this.decoder.queueInputBuffer(this.nextInputBuffer);
              this.nextInputBuffer = null;
              break;
              this.nextInputBuffer.subsampleOffsetUs = this.formatHolder.format.subsampleOffsetUs;
              this.nextInputBuffer.flip();
            }
          }
        } while (i != -3);
      }
    }
  }
  
  public int supportsFormat(Format paramFormat)
  {
    int i;
    if (this.decoderFactory.supportsFormat(paramFormat)) {
      if (supportsFormatDrm(null, paramFormat.drmInitData)) {
        i = 4;
      }
    }
    for (;;)
    {
      return i;
      i = 2;
      continue;
      if (MimeTypes.isText(paramFormat.sampleMimeType)) {
        i = 1;
      } else {
        i = 0;
      }
    }
  }
  
  @Deprecated
  public static abstract interface Output
    extends TextOutput
  {}
  
  @Retention(RetentionPolicy.SOURCE)
  private static @interface ReplacementState {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/text/TextRenderer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */