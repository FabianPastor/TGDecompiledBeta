package org.telegram.messenger.exoplayer2.audio;

import android.annotation.TargetApi;

public final class AudioAttributes
{
  public static final AudioAttributes DEFAULT = new Builder().build();
  private android.media.AudioAttributes audioAttributesV21;
  public final int contentType;
  public final int flags;
  public final int usage;
  
  private AudioAttributes(int paramInt1, int paramInt2, int paramInt3)
  {
    this.contentType = paramInt1;
    this.flags = paramInt2;
    this.usage = paramInt3;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool = true;
    if (this == paramObject) {}
    for (;;)
    {
      return bool;
      if ((paramObject == null) || (getClass() != paramObject.getClass()))
      {
        bool = false;
      }
      else
      {
        paramObject = (AudioAttributes)paramObject;
        if ((this.contentType != ((AudioAttributes)paramObject).contentType) || (this.flags != ((AudioAttributes)paramObject).flags) || (this.usage != ((AudioAttributes)paramObject).usage)) {
          bool = false;
        }
      }
    }
  }
  
  @TargetApi(21)
  android.media.AudioAttributes getAudioAttributesV21()
  {
    if (this.audioAttributesV21 == null) {
      this.audioAttributesV21 = new android.media.AudioAttributes.Builder().setContentType(this.contentType).setFlags(this.flags).setUsage(this.usage).build();
    }
    return this.audioAttributesV21;
  }
  
  public int hashCode()
  {
    return ((this.contentType + 527) * 31 + this.flags) * 31 + this.usage;
  }
  
  public static final class Builder
  {
    private int contentType = 0;
    private int flags = 0;
    private int usage = 1;
    
    public AudioAttributes build()
    {
      return new AudioAttributes(this.contentType, this.flags, this.usage, null);
    }
    
    public Builder setContentType(int paramInt)
    {
      this.contentType = paramInt;
      return this;
    }
    
    public Builder setFlags(int paramInt)
    {
      this.flags = paramInt;
      return this;
    }
    
    public Builder setUsage(int paramInt)
    {
      this.usage = paramInt;
      return this;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/audio/AudioAttributes.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */