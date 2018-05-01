package org.telegram.messenger.exoplayer2.text.dvb;

import java.util.List;
import org.telegram.messenger.exoplayer2.text.SimpleSubtitleDecoder;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

public final class DvbDecoder
  extends SimpleSubtitleDecoder
{
  private final DvbParser parser;
  
  public DvbDecoder(List<byte[]> paramList)
  {
    super("DvbDecoder");
    paramList = new ParsableByteArray((byte[])paramList.get(0));
    this.parser = new DvbParser(paramList.readUnsignedShort(), paramList.readUnsignedShort());
  }
  
  protected DvbSubtitle decode(byte[] paramArrayOfByte, int paramInt, boolean paramBoolean)
  {
    if (paramBoolean) {
      this.parser.reset();
    }
    return new DvbSubtitle(this.parser.decode(paramArrayOfByte, paramInt));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/text/dvb/DvbDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */