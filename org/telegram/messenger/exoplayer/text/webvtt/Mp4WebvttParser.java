package org.telegram.messenger.exoplayer.text.webvtt;

import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.exoplayer.ParserException;
import org.telegram.messenger.exoplayer.text.Cue;
import org.telegram.messenger.exoplayer.text.SubtitleParser;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;
import org.telegram.messenger.exoplayer.util.Util;

public final class Mp4WebvttParser
  implements SubtitleParser
{
  private static final int BOX_HEADER_SIZE = 8;
  private static final int TYPE_payl = Util.getIntegerCodeForString("payl");
  private static final int TYPE_sttg = Util.getIntegerCodeForString("sttg");
  private static final int TYPE_vttc = Util.getIntegerCodeForString("vttc");
  private final WebvttCue.Builder builder = new WebvttCue.Builder();
  private final ParsableByteArray sampleData = new ParsableByteArray();
  
  private static Cue parseVttCueBox(ParsableByteArray paramParsableByteArray, WebvttCue.Builder paramBuilder, int paramInt)
    throws ParserException
  {
    paramBuilder.reset();
    while (paramInt > 0)
    {
      if (paramInt < 8) {
        throw new ParserException("Incomplete vtt cue box header found.");
      }
      int i = paramParsableByteArray.readInt();
      int j = paramParsableByteArray.readInt();
      i -= 8;
      String str = new String(paramParsableByteArray.data, paramParsableByteArray.getPosition(), i);
      paramParsableByteArray.skipBytes(i);
      i = paramInt - 8 - i;
      if (j == TYPE_sttg)
      {
        WebvttCueParser.parseCueSettingsList(str, paramBuilder);
        paramInt = i;
      }
      else
      {
        paramInt = i;
        if (j == TYPE_payl)
        {
          WebvttCueParser.parseCueText(str.trim(), paramBuilder);
          paramInt = i;
        }
      }
    }
    return paramBuilder.build();
  }
  
  public boolean canParse(String paramString)
  {
    return "application/x-mp4vtt".equals(paramString);
  }
  
  public Mp4WebvttSubtitle parse(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws ParserException
  {
    this.sampleData.reset(paramArrayOfByte, paramInt1 + paramInt2);
    this.sampleData.setPosition(paramInt1);
    paramArrayOfByte = new ArrayList();
    while (this.sampleData.bytesLeft() > 0)
    {
      if (this.sampleData.bytesLeft() < 8) {
        throw new ParserException("Incomplete Mp4Webvtt Top Level box header found.");
      }
      paramInt1 = this.sampleData.readInt();
      if (this.sampleData.readInt() == TYPE_vttc) {
        paramArrayOfByte.add(parseVttCueBox(this.sampleData, this.builder, paramInt1 - 8));
      } else {
        this.sampleData.skipBytes(paramInt1 - 8);
      }
    }
    return new Mp4WebvttSubtitle(paramArrayOfByte);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/text/webvtt/Mp4WebvttParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */