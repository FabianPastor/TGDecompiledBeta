package org.telegram.messenger.exoplayer.text.eia608;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import java.util.Collections;
import java.util.TreeSet;
import org.telegram.messenger.exoplayer.ExoPlaybackException;
import org.telegram.messenger.exoplayer.MediaFormat;
import org.telegram.messenger.exoplayer.MediaFormatHolder;
import org.telegram.messenger.exoplayer.SampleHolder;
import org.telegram.messenger.exoplayer.SampleSource;
import org.telegram.messenger.exoplayer.SampleSourceTrackRenderer;
import org.telegram.messenger.exoplayer.text.Cue;
import org.telegram.messenger.exoplayer.text.TextRenderer;
import org.telegram.messenger.exoplayer.util.Assertions;
import org.telegram.messenger.exoplayer.util.Util;

public final class Eia608TrackRenderer
  extends SampleSourceTrackRenderer
  implements Handler.Callback
{
  private static final int CC_MODE_PAINT_ON = 3;
  private static final int CC_MODE_POP_ON = 2;
  private static final int CC_MODE_ROLL_UP = 1;
  private static final int CC_MODE_UNKNOWN = 0;
  private static final int DEFAULT_CAPTIONS_ROW_COUNT = 4;
  private static final int MAX_SAMPLE_READAHEAD_US = 5000000;
  private static final int MSG_INVOKE_RENDERER = 0;
  private String caption;
  private int captionMode;
  private int captionRowCount;
  private final StringBuilder captionStringBuilder;
  private final Eia608Parser eia608Parser;
  private final MediaFormatHolder formatHolder;
  private boolean inputStreamEnded;
  private String lastRenderedCaption;
  private final TreeSet<ClosedCaptionList> pendingCaptionLists;
  private ClosedCaptionCtrl repeatableControl;
  private final SampleHolder sampleHolder;
  private final TextRenderer textRenderer;
  private final Handler textRendererHandler;
  
  public Eia608TrackRenderer(SampleSource paramSampleSource, TextRenderer paramTextRenderer, Looper paramLooper)
  {
    super(new SampleSource[] { paramSampleSource });
    this.textRenderer = ((TextRenderer)Assertions.checkNotNull(paramTextRenderer));
    if (paramLooper == null) {}
    for (paramSampleSource = null;; paramSampleSource = new Handler(paramLooper, this))
    {
      this.textRendererHandler = paramSampleSource;
      this.eia608Parser = new Eia608Parser();
      this.formatHolder = new MediaFormatHolder();
      this.sampleHolder = new SampleHolder(1);
      this.captionStringBuilder = new StringBuilder();
      this.pendingCaptionLists = new TreeSet();
      return;
    }
  }
  
  private void clearPendingSample()
  {
    this.sampleHolder.timeUs = -1L;
    this.sampleHolder.clearData();
  }
  
  private void consumeCaptionList(ClosedCaptionList paramClosedCaptionList)
  {
    int m = paramClosedCaptionList.captions.length;
    if (m == 0) {}
    label63:
    label124:
    do
    {
      return;
      int k = 0;
      int j = 0;
      if (j < m)
      {
        Object localObject = paramClosedCaptionList.captions[j];
        int i;
        if (((ClosedCaption)localObject).type == 0)
        {
          localObject = (ClosedCaptionCtrl)localObject;
          if ((m == 1) && (((ClosedCaptionCtrl)localObject).isRepeatable()))
          {
            i = 1;
            if ((i == 0) || (this.repeatableControl == null) || (this.repeatableControl.cc1 != ((ClosedCaptionCtrl)localObject).cc1) || (this.repeatableControl.cc2 != ((ClosedCaptionCtrl)localObject).cc2)) {
              break label124;
            }
            this.repeatableControl = null;
            k = i;
          }
        }
        for (;;)
        {
          j += 1;
          break;
          i = 0;
          break label63;
          if (i != 0) {
            this.repeatableControl = ((ClosedCaptionCtrl)localObject);
          }
          if (((ClosedCaptionCtrl)localObject).isMiscCode())
          {
            handleMiscCode((ClosedCaptionCtrl)localObject);
            k = i;
          }
          else
          {
            k = i;
            if (((ClosedCaptionCtrl)localObject).isPreambleAddressCode())
            {
              handlePreambleAddressCode();
              k = i;
              continue;
              handleText((ClosedCaptionText)localObject);
            }
          }
        }
      }
      if (k == 0) {
        this.repeatableControl = null;
      }
    } while ((this.captionMode != 1) && (this.captionMode != 3));
    this.caption = getDisplayCaption();
  }
  
  private String getDisplayCaption()
  {
    int j = this.captionStringBuilder.length();
    if (j == 0) {
      return null;
    }
    if (this.captionStringBuilder.charAt(j - 1) == '\n')
    {
      i = 1;
      label31:
      if ((j == 1) && (i != 0)) {
        break label69;
      }
      if (i == 0) {
        break label71;
      }
    }
    label69:
    label71:
    for (int i = j - 1;; i = j)
    {
      if (this.captionMode == 1) {
        break label76;
      }
      return this.captionStringBuilder.substring(0, i);
      i = 0;
      break label31;
      break;
    }
    label76:
    int m = 0;
    j = i;
    int k = 0;
    while ((k < this.captionRowCount) && (j != -1))
    {
      j = this.captionStringBuilder.lastIndexOf("\n", j - 1);
      k += 1;
    }
    k = m;
    if (j != -1) {
      k = j + 1;
    }
    this.captionStringBuilder.delete(0, k);
    return this.captionStringBuilder.substring(0, i - k);
  }
  
  private void handleMiscCode(ClosedCaptionCtrl paramClosedCaptionCtrl)
  {
    switch (paramClosedCaptionCtrl.cc2)
    {
    case 33: 
    case 34: 
    case 35: 
    case 36: 
    case 40: 
    default: 
      if (this.captionMode != 0) {
        break;
      }
    }
    do
    {
      do
      {
        return;
        this.captionRowCount = 2;
        setCaptionMode(1);
        return;
        this.captionRowCount = 3;
        setCaptionMode(1);
        return;
        this.captionRowCount = 4;
        setCaptionMode(1);
        return;
        setCaptionMode(2);
        return;
        setCaptionMode(3);
        return;
        switch (paramClosedCaptionCtrl.cc2)
        {
        default: 
          return;
        }
      } while (this.captionStringBuilder.length() <= 0);
      this.captionStringBuilder.setLength(this.captionStringBuilder.length() - 1);
      return;
      this.caption = null;
    } while ((this.captionMode != 1) && (this.captionMode != 3));
    this.captionStringBuilder.setLength(0);
    return;
    this.captionStringBuilder.setLength(0);
    return;
    this.caption = getDisplayCaption();
    this.captionStringBuilder.setLength(0);
    return;
    maybeAppendNewline();
  }
  
  private void handlePreambleAddressCode()
  {
    maybeAppendNewline();
  }
  
  private void handleText(ClosedCaptionText paramClosedCaptionText)
  {
    if (this.captionMode != 0) {
      this.captionStringBuilder.append(paramClosedCaptionText.text);
    }
  }
  
  private void invokeRenderer(String paramString)
  {
    if (Util.areEqual(this.lastRenderedCaption, paramString)) {
      return;
    }
    this.lastRenderedCaption = paramString;
    if (this.textRendererHandler != null)
    {
      this.textRendererHandler.obtainMessage(0, paramString).sendToTarget();
      return;
    }
    invokeRendererInternal(paramString);
  }
  
  private void invokeRendererInternal(String paramString)
  {
    if (paramString == null)
    {
      this.textRenderer.onCues(Collections.emptyList());
      return;
    }
    this.textRenderer.onCues(Collections.singletonList(new Cue(paramString)));
  }
  
  private boolean isSamplePending()
  {
    return this.sampleHolder.timeUs != -1L;
  }
  
  private void maybeAppendNewline()
  {
    int i = this.captionStringBuilder.length();
    if ((i > 0) && (this.captionStringBuilder.charAt(i - 1) != '\n')) {
      this.captionStringBuilder.append('\n');
    }
  }
  
  private void maybeParsePendingSample(long paramLong)
  {
    if (this.sampleHolder.timeUs > 5000000L + paramLong) {}
    ClosedCaptionList localClosedCaptionList;
    do
    {
      return;
      localClosedCaptionList = this.eia608Parser.parse(this.sampleHolder);
      clearPendingSample();
    } while (localClosedCaptionList == null);
    this.pendingCaptionLists.add(localClosedCaptionList);
  }
  
  private void setCaptionMode(int paramInt)
  {
    if (this.captionMode == paramInt) {}
    do
    {
      return;
      this.captionMode = paramInt;
      this.captionStringBuilder.setLength(0);
    } while ((paramInt != 1) && (paramInt != 0));
    this.caption = null;
  }
  
  protected void doSomeWork(long paramLong1, long paramLong2, boolean paramBoolean)
    throws ExoPlaybackException
  {
    if (isSamplePending()) {
      maybeParsePendingSample(paramLong1);
    }
    int i;
    if (this.inputStreamEnded) {
      i = -1;
    }
    while ((!isSamplePending()) && (i == -3))
    {
      j = readSource(paramLong1, this.formatHolder, this.sampleHolder);
      if (j == -3)
      {
        maybeParsePendingSample(paramLong1);
        i = j;
        continue;
        i = -3;
      }
      else
      {
        i = j;
        if (j == -1)
        {
          this.inputStreamEnded = true;
          i = j;
        }
      }
    }
    while ((!this.pendingCaptionLists.isEmpty()) && (((ClosedCaptionList)this.pendingCaptionLists.first()).timeUs <= paramLong1))
    {
      int j;
      ClosedCaptionList localClosedCaptionList = (ClosedCaptionList)this.pendingCaptionLists.pollFirst();
      consumeCaptionList(localClosedCaptionList);
      if (!localClosedCaptionList.decodeOnly) {
        invokeRenderer(this.caption);
      }
    }
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
    invokeRendererInternal((String)paramMessage.obj);
    return true;
  }
  
  protected boolean handlesTrack(MediaFormat paramMediaFormat)
  {
    return this.eia608Parser.canParse(paramMediaFormat.mimeType);
  }
  
  protected boolean isEnded()
  {
    return this.inputStreamEnded;
  }
  
  protected boolean isReady()
  {
    return true;
  }
  
  protected void onDiscontinuity(long paramLong)
  {
    this.inputStreamEnded = false;
    this.repeatableControl = null;
    this.pendingCaptionLists.clear();
    clearPendingSample();
    this.captionRowCount = 4;
    setCaptionMode(0);
    invokeRenderer(null);
  }
  
  protected void onEnabled(int paramInt, long paramLong, boolean paramBoolean)
    throws ExoPlaybackException
  {
    super.onEnabled(paramInt, paramLong, paramBoolean);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/text/eia608/Eia608TrackRenderer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */