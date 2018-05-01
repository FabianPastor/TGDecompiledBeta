package org.telegram.messenger.exoplayer.util;

import android.widget.TextView;
import org.telegram.messenger.exoplayer.CodecCounters;
import org.telegram.messenger.exoplayer.chunk.Format;
import org.telegram.messenger.exoplayer.upstream.BandwidthMeter;

public final class DebugTextViewHelper
  implements Runnable
{
  private static final int REFRESH_INTERVAL_MS = 1000;
  private final Provider debuggable;
  private final TextView textView;
  
  public DebugTextViewHelper(Provider paramProvider, TextView paramTextView)
  {
    this.debuggable = paramProvider;
    this.textView = paramTextView;
  }
  
  private String getBandwidthString()
  {
    BandwidthMeter localBandwidthMeter = this.debuggable.getBandwidthMeter();
    if ((localBandwidthMeter == null) || (localBandwidthMeter.getBitrateEstimate() == -1L)) {
      return "bw:?";
    }
    return "bw:" + localBandwidthMeter.getBitrateEstimate() / 1000L;
  }
  
  private String getQualityString()
  {
    Format localFormat = this.debuggable.getFormat();
    if (localFormat == null) {
      return "id:? br:? h:?";
    }
    return "id:" + localFormat.id + " br:" + localFormat.bitrate + " h:" + localFormat.height;
  }
  
  private String getRenderString()
  {
    return getTimeString() + " " + getQualityString() + " " + getBandwidthString() + " " + getVideoCodecCountersString();
  }
  
  private String getTimeString()
  {
    return "ms(" + this.debuggable.getCurrentPosition() + ")";
  }
  
  private String getVideoCodecCountersString()
  {
    CodecCounters localCodecCounters = this.debuggable.getCodecCounters();
    if (localCodecCounters == null) {
      return "";
    }
    return localCodecCounters.getDebugString();
  }
  
  public void run()
  {
    this.textView.setText(getRenderString());
    this.textView.postDelayed(this, 1000L);
  }
  
  public void start()
  {
    stop();
    run();
  }
  
  public void stop()
  {
    this.textView.removeCallbacks(this);
  }
  
  public static abstract interface Provider
  {
    public abstract BandwidthMeter getBandwidthMeter();
    
    public abstract CodecCounters getCodecCounters();
    
    public abstract long getCurrentPosition();
    
    public abstract Format getFormat();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/util/DebugTextViewHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */