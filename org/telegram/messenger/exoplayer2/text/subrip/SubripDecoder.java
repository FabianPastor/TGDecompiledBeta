package org.telegram.messenger.exoplayer2.text.subrip;

import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.messenger.exoplayer2.text.Cue;
import org.telegram.messenger.exoplayer2.text.SimpleSubtitleDecoder;
import org.telegram.messenger.exoplayer2.util.LongArray;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

public final class SubripDecoder
  extends SimpleSubtitleDecoder
{
  private static final String SUBRIP_TIMECODE = "(?:(\\d+):)?(\\d+):(\\d+),(\\d+)";
  private static final Pattern SUBRIP_TIMING_LINE = Pattern.compile("\\s*((?:(\\d+):)?(\\d+):(\\d+),(\\d+))\\s*-->\\s*((?:(\\d+):)?(\\d+):(\\d+),(\\d+))?\\s*");
  private static final String TAG = "SubripDecoder";
  private final StringBuilder textBuilder = new StringBuilder();
  
  public SubripDecoder()
  {
    super("SubripDecoder");
  }
  
  private static long parseTimecode(Matcher paramMatcher, int paramInt)
  {
    return (Long.parseLong(paramMatcher.group(paramInt + 1)) * 60L * 60L * 1000L + Long.parseLong(paramMatcher.group(paramInt + 2)) * 60L * 1000L + Long.parseLong(paramMatcher.group(paramInt + 3)) * 1000L + Long.parseLong(paramMatcher.group(paramInt + 4))) * 1000L;
  }
  
  protected SubripSubtitle decode(byte[] paramArrayOfByte, int paramInt, boolean paramBoolean)
  {
    ArrayList localArrayList = new ArrayList();
    LongArray localLongArray = new LongArray();
    paramArrayOfByte = new ParsableByteArray(paramArrayOfByte, paramInt);
    for (;;)
    {
      Object localObject = paramArrayOfByte.readLine();
      if ((localObject == null) || (((String)localObject).length() != 0))
      {
        try
        {
          Integer.parseInt((String)localObject);
          paramInt = 0;
          String str = paramArrayOfByte.readLine();
          if (str != null) {
            break label135;
          }
          Log.w("SubripDecoder", "Unexpected end");
          paramArrayOfByte = new Cue[localArrayList.size()];
          localArrayList.toArray(paramArrayOfByte);
          return new SubripSubtitle(paramArrayOfByte, localLongArray.toArray());
        }
        catch (NumberFormatException localNumberFormatException)
        {
          Log.w("SubripDecoder", "Skipping invalid index: " + (String)localObject);
        }
        continue;
        label135:
        localObject = SUBRIP_TIMING_LINE.matcher(localNumberFormatException);
        if (((Matcher)localObject).matches())
        {
          localLongArray.add(parseTimecode((Matcher)localObject, 1));
          if (!TextUtils.isEmpty(((Matcher)localObject).group(6)))
          {
            paramInt = 1;
            localLongArray.add(parseTimecode((Matcher)localObject, 6));
          }
          this.textBuilder.setLength(0);
          for (;;)
          {
            localObject = paramArrayOfByte.readLine();
            if (TextUtils.isEmpty((CharSequence)localObject)) {
              break;
            }
            if (this.textBuilder.length() > 0) {
              this.textBuilder.append("<br>");
            }
            this.textBuilder.append(((String)localObject).trim());
          }
        }
        Log.w("SubripDecoder", "Skipping invalid timing: " + localNumberFormatException);
        continue;
        localArrayList.add(new Cue(Html.fromHtml(this.textBuilder.toString())));
        if (paramInt != 0) {
          localArrayList.add(null);
        }
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/text/subrip/SubripDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */