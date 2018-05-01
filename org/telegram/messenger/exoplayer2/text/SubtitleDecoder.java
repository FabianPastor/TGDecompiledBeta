package org.telegram.messenger.exoplayer2.text;

import org.telegram.messenger.exoplayer2.decoder.Decoder;

public abstract interface SubtitleDecoder
  extends Decoder<SubtitleInputBuffer, SubtitleOutputBuffer, SubtitleDecoderException>
{
  public abstract void setPositionUs(long paramLong);
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/text/SubtitleDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */