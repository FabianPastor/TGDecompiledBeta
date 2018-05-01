package org.telegram.messenger.exoplayer.text.tx3g;

import org.telegram.messenger.exoplayer.text.Cue;
import org.telegram.messenger.exoplayer.text.Subtitle;
import org.telegram.messenger.exoplayer.text.SubtitleParser;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;

public final class Tx3gParser
  implements SubtitleParser
{
  private final ParsableByteArray parsableByteArray = new ParsableByteArray();
  
  public boolean canParse(String paramString)
  {
    return "application/x-quicktime-tx3g".equals(paramString);
  }
  
  public Subtitle parse(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    this.parsableByteArray.reset(paramArrayOfByte, paramInt2);
    paramInt1 = this.parsableByteArray.readUnsignedShort();
    if (paramInt1 == 0) {
      return Tx3gSubtitle.EMPTY;
    }
    return new Tx3gSubtitle(new Cue(this.parsableByteArray.readString(paramInt1)));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/text/tx3g/Tx3gParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */