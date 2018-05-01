package org.telegram.messenger.exoplayer2.text;

import org.telegram.messenger.exoplayer2.decoder.DecoderInputBuffer;

public final class SubtitleInputBuffer
  extends DecoderInputBuffer
  implements Comparable<SubtitleInputBuffer>
{
  public long subsampleOffsetUs;
  
  public SubtitleInputBuffer()
  {
    super(1);
  }
  
  public int compareTo(SubtitleInputBuffer paramSubtitleInputBuffer)
  {
    int i = 1;
    if (isEndOfStream() != paramSubtitleInputBuffer.isEndOfStream()) {
      if (!isEndOfStream()) {}
    }
    for (;;)
    {
      return i;
      i = -1;
      continue;
      long l = this.timeUs - paramSubtitleInputBuffer.timeUs;
      if (l == 0L) {
        i = 0;
      } else if (l <= 0L) {
        i = -1;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/text/SubtitleInputBuffer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */