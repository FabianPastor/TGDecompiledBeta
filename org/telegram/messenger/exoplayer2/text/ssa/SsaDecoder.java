package org.telegram.messenger.exoplayer2.text.ssa;

import android.text.TextUtils;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.messenger.exoplayer2.text.Cue;
import org.telegram.messenger.exoplayer2.text.SimpleSubtitleDecoder;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.LongArray;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;
import org.telegram.messenger.exoplayer2.util.Util;

public final class SsaDecoder
  extends SimpleSubtitleDecoder
{
  private static final String DIALOGUE_LINE_PREFIX = "Dialogue: ";
  private static final String FORMAT_LINE_PREFIX = "Format: ";
  private static final Pattern SSA_TIMECODE_PATTERN = Pattern.compile("(?:(\\d+):)?(\\d+):(\\d+)(?::|\\.)(\\d+)");
  private static final String TAG = "SsaDecoder";
  private int formatEndIndex;
  private int formatKeyCount;
  private int formatStartIndex;
  private int formatTextIndex;
  private final boolean haveInitializationData;
  
  public SsaDecoder()
  {
    this(null);
  }
  
  public SsaDecoder(List<byte[]> paramList)
  {
    super("SsaDecoder");
    if ((paramList != null) && (!paramList.isEmpty()))
    {
      this.haveInitializationData = true;
      String str = new String((byte[])paramList.get(0));
      Assertions.checkArgument(str.startsWith("Format: "));
      parseFormatLine(str);
      parseHeader(new ParsableByteArray((byte[])paramList.get(1)));
    }
    for (;;)
    {
      return;
      this.haveInitializationData = false;
    }
  }
  
  private void parseDialogueLine(String paramString, List<Cue> paramList, LongArray paramLongArray)
  {
    if (this.formatKeyCount == 0) {
      Log.w("SsaDecoder", "Skipping dialogue line before complete format: " + paramString);
    }
    for (;;)
    {
      return;
      String[] arrayOfString = paramString.substring("Dialogue: ".length()).split(",", this.formatKeyCount);
      if (arrayOfString.length != this.formatKeyCount)
      {
        Log.w("SsaDecoder", "Skipping dialogue line with fewer columns than format: " + paramString);
      }
      else
      {
        long l1 = parseTimecodeUs(arrayOfString[this.formatStartIndex]);
        if (l1 == -9223372036854775807L)
        {
          Log.w("SsaDecoder", "Skipping invalid timing: " + paramString);
        }
        else
        {
          long l2 = -9223372036854775807L;
          String str = arrayOfString[this.formatEndIndex];
          if (!str.trim().isEmpty())
          {
            long l3 = parseTimecodeUs(str);
            l2 = l3;
            if (l3 == -9223372036854775807L)
            {
              Log.w("SsaDecoder", "Skipping invalid timing: " + paramString);
              continue;
            }
          }
          paramList.add(new Cue(arrayOfString[this.formatTextIndex].replaceAll("\\{.*?\\}", "").replaceAll("\\\\N", "\n").replaceAll("\\\\n", "\n")));
          paramLongArray.add(l1);
          if (l2 != -9223372036854775807L)
          {
            paramList.add(null);
            paramLongArray.add(l2);
          }
        }
      }
    }
  }
  
  private void parseEventBody(ParsableByteArray paramParsableByteArray, List<Cue> paramList, LongArray paramLongArray)
  {
    for (;;)
    {
      String str = paramParsableByteArray.readLine();
      if (str == null) {
        break;
      }
      if ((!this.haveInitializationData) && (str.startsWith("Format: "))) {
        parseFormatLine(str);
      } else if (str.startsWith("Dialogue: ")) {
        parseDialogueLine(str, paramList, paramLongArray);
      }
    }
  }
  
  private void parseFormatLine(String paramString)
  {
    paramString = TextUtils.split(paramString.substring("Format: ".length()), ",");
    this.formatKeyCount = paramString.length;
    this.formatStartIndex = -1;
    this.formatEndIndex = -1;
    this.formatTextIndex = -1;
    int i = 0;
    if (i < this.formatKeyCount)
    {
      String str = Util.toLowerInvariant(paramString[i].trim());
      label96:
      int j;
      switch (str.hashCode())
      {
      default: 
        j = -1;
        label99:
        switch (j)
        {
        }
        break;
      }
      for (;;)
      {
        i++;
        break;
        if (!str.equals("start")) {
          break label96;
        }
        j = 0;
        break label99;
        if (!str.equals("end")) {
          break label96;
        }
        j = 1;
        break label99;
        if (!str.equals("text")) {
          break label96;
        }
        j = 2;
        break label99;
        this.formatStartIndex = i;
        continue;
        this.formatEndIndex = i;
        continue;
        this.formatTextIndex = i;
      }
    }
    if ((this.formatStartIndex == -1) || (this.formatEndIndex == -1) || (this.formatTextIndex == -1)) {
      this.formatKeyCount = 0;
    }
  }
  
  private void parseHeader(ParsableByteArray paramParsableByteArray)
  {
    String str;
    do
    {
      str = paramParsableByteArray.readLine();
    } while ((str != null) && (!str.startsWith("[Events]")));
  }
  
  public static long parseTimecodeUs(String paramString)
  {
    paramString = SSA_TIMECODE_PATTERN.matcher(paramString);
    if (!paramString.matches()) {}
    for (long l = -9223372036854775807L;; l = Long.parseLong(paramString.group(1)) * 60L * 60L * 1000000L + Long.parseLong(paramString.group(2)) * 60L * 1000000L + Long.parseLong(paramString.group(3)) * 1000000L + Long.parseLong(paramString.group(4)) * 10000L) {
      return l;
    }
  }
  
  protected SsaSubtitle decode(byte[] paramArrayOfByte, int paramInt, boolean paramBoolean)
  {
    ArrayList localArrayList = new ArrayList();
    LongArray localLongArray = new LongArray();
    paramArrayOfByte = new ParsableByteArray(paramArrayOfByte, paramInt);
    if (!this.haveInitializationData) {
      parseHeader(paramArrayOfByte);
    }
    parseEventBody(paramArrayOfByte, localArrayList, localLongArray);
    paramArrayOfByte = new Cue[localArrayList.size()];
    localArrayList.toArray(paramArrayOfByte);
    return new SsaSubtitle(paramArrayOfByte, localLongArray.toArray());
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/text/ssa/SsaDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */