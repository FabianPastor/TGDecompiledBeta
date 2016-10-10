package org.telegram.messenger.exoplayer.text.subrip;

import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.messenger.exoplayer.text.Cue;
import org.telegram.messenger.exoplayer.text.SubtitleParser;
import org.telegram.messenger.exoplayer.util.LongArray;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;

public final class SubripParser
  implements SubtitleParser
{
  private static final Pattern SUBRIP_TIMESTAMP = Pattern.compile("(?:(\\d+):)?(\\d+):(\\d+),(\\d+)");
  private static final Pattern SUBRIP_TIMING_LINE = Pattern.compile("(\\S*)\\s*-->\\s*(\\S*)");
  private static final String TAG = "SubripParser";
  private final StringBuilder textBuilder = new StringBuilder();
  
  private static long parseTimecode(String paramString)
    throws NumberFormatException
  {
    paramString = SUBRIP_TIMESTAMP.matcher(paramString);
    if (!paramString.matches()) {
      throw new NumberFormatException("has invalid format");
    }
    return (Long.parseLong(paramString.group(1)) * 60L * 60L * 1000L + Long.parseLong(paramString.group(2)) * 60L * 1000L + Long.parseLong(paramString.group(3)) * 1000L + Long.parseLong(paramString.group(4))) * 1000L;
  }
  
  public boolean canParse(String paramString)
  {
    return "application/x-subrip".equals(paramString);
  }
  
  public SubripSubtitle parse(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    ArrayList localArrayList = new ArrayList();
    LongArray localLongArray = new LongArray();
    paramArrayOfByte = new ParsableByteArray(paramArrayOfByte, paramInt1 + paramInt2);
    paramArrayOfByte.setPosition(paramInt1);
    for (;;)
    {
      String str = paramArrayOfByte.readLine();
      if (str == null) {
        break;
      }
      if (str.length() != 0)
      {
        try
        {
          Integer.parseInt(str);
          paramInt1 = 0;
          str = paramArrayOfByte.readLine();
          Matcher localMatcher = SUBRIP_TIMING_LINE.matcher(str);
          if (localMatcher.find())
          {
            localLongArray.add(parseTimecode(localMatcher.group(1)));
            if (!TextUtils.isEmpty(localMatcher.group(2)))
            {
              paramInt1 = 1;
              localLongArray.add(parseTimecode(localMatcher.group(2)));
            }
            this.textBuilder.setLength(0);
            for (;;)
            {
              str = paramArrayOfByte.readLine();
              if (TextUtils.isEmpty(str)) {
                break;
              }
              if (this.textBuilder.length() > 0) {
                this.textBuilder.append("<br>");
              }
              this.textBuilder.append(str.trim());
            }
          }
        }
        catch (NumberFormatException localNumberFormatException)
        {
          Log.w("SubripParser", "Skipping invalid index: " + str);
        }
        Log.w("SubripParser", "Skipping invalid timing: " + str);
        continue;
        localArrayList.add(new Cue(Html.fromHtml(this.textBuilder.toString())));
        if (paramInt1 != 0) {
          localArrayList.add(null);
        }
      }
    }
    paramArrayOfByte = new Cue[localArrayList.size()];
    localArrayList.toArray(paramArrayOfByte);
    return new SubripSubtitle(paramArrayOfByte, localLongArray.toArray());
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/text/subrip/SubripParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */