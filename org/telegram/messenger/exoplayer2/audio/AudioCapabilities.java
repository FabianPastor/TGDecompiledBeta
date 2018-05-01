package org.telegram.messenger.exoplayer2.audio;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import java.util.Arrays;

@TargetApi(21)
public final class AudioCapabilities
{
  public static final AudioCapabilities DEFAULT_AUDIO_CAPABILITIES = new AudioCapabilities(new int[] { 2 }, 2);
  private final int maxChannelCount;
  private final int[] supportedEncodings;
  
  AudioCapabilities(int[] paramArrayOfInt, int paramInt)
  {
    if (paramArrayOfInt != null)
    {
      this.supportedEncodings = Arrays.copyOf(paramArrayOfInt, paramArrayOfInt.length);
      Arrays.sort(this.supportedEncodings);
    }
    for (;;)
    {
      this.maxChannelCount = paramInt;
      return;
      this.supportedEncodings = new int[0];
    }
  }
  
  public static AudioCapabilities getCapabilities(Context paramContext)
  {
    return getCapabilities(paramContext.registerReceiver(null, new IntentFilter("android.media.action.HDMI_AUDIO_PLUG")));
  }
  
  @SuppressLint({"InlinedApi"})
  static AudioCapabilities getCapabilities(Intent paramIntent)
  {
    if ((paramIntent == null) || (paramIntent.getIntExtra("android.media.extra.AUDIO_PLUG_STATE", 0) == 0)) {}
    for (paramIntent = DEFAULT_AUDIO_CAPABILITIES;; paramIntent = new AudioCapabilities(paramIntent.getIntArrayExtra("android.media.extra.ENCODINGS"), paramIntent.getIntExtra("android.media.extra.MAX_CHANNEL_COUNT", 0))) {
      return paramIntent;
    }
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool = true;
    if (this == paramObject) {}
    for (;;)
    {
      return bool;
      if (!(paramObject instanceof AudioCapabilities))
      {
        bool = false;
      }
      else
      {
        paramObject = (AudioCapabilities)paramObject;
        if ((!Arrays.equals(this.supportedEncodings, ((AudioCapabilities)paramObject).supportedEncodings)) || (this.maxChannelCount != ((AudioCapabilities)paramObject).maxChannelCount)) {
          bool = false;
        }
      }
    }
  }
  
  public int getMaxChannelCount()
  {
    return this.maxChannelCount;
  }
  
  public int hashCode()
  {
    return this.maxChannelCount + Arrays.hashCode(this.supportedEncodings) * 31;
  }
  
  public boolean supportsEncoding(int paramInt)
  {
    if (Arrays.binarySearch(this.supportedEncodings, paramInt) >= 0) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public String toString()
  {
    return "AudioCapabilities[maxChannelCount=" + this.maxChannelCount + ", supportedEncodings=" + Arrays.toString(this.supportedEncodings) + "]";
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/audio/AudioCapabilities.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */