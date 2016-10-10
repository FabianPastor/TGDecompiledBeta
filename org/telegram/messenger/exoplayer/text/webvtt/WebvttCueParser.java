package org.telegram.messenger.exoplayer.text.webvtt;

import android.text.Layout.Alignment;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;

public final class WebvttCueParser
{
  private static final char CHAR_AMPERSAND = '&';
  private static final char CHAR_GREATER_THAN = '>';
  private static final char CHAR_LESS_THAN = '<';
  private static final char CHAR_SEMI_COLON = ';';
  private static final char CHAR_SLASH = '/';
  private static final char CHAR_SPACE = ' ';
  private static final Pattern COMMENT = Pattern.compile("^NOTE(( |\t).*)?$");
  public static final Pattern CUE_HEADER_PATTERN = Pattern.compile("^(\\S+)\\s+-->\\s+(\\S+)(.*)?$");
  private static final Pattern CUE_SETTING_PATTERN = Pattern.compile("(\\S+?):(\\S+)");
  private static final String ENTITY_AMPERSAND = "amp";
  private static final String ENTITY_GREATER_THAN = "gt";
  private static final String ENTITY_LESS_THAN = "lt";
  private static final String ENTITY_NON_BREAK_SPACE = "nbsp";
  private static final String SPACE = " ";
  private static final int STYLE_BOLD = 1;
  private static final int STYLE_ITALIC = 2;
  private static final String TAG = "WebvttCueParser";
  private static final String TAG_BOLD = "b";
  private static final String TAG_CLASS = "c";
  private static final String TAG_ITALIC = "i";
  private static final String TAG_LANG = "lang";
  private static final String TAG_UNDERLINE = "u";
  private static final String TAG_VOICE = "v";
  private final StringBuilder textBuilder = new StringBuilder();
  
  private static void applyEntity(String paramString, SpannableStringBuilder paramSpannableStringBuilder)
  {
    int i = -1;
    switch (paramString.hashCode())
    {
    }
    for (;;)
    {
      switch (i)
      {
      default: 
        Log.w("WebvttCueParser", "ignoring unsupported entity: '&" + paramString + ";'");
        return;
        if (paramString.equals("lt"))
        {
          i = 0;
          continue;
          if (paramString.equals("gt"))
          {
            i = 1;
            continue;
            if (paramString.equals("nbsp"))
            {
              i = 2;
              continue;
              if (paramString.equals("amp")) {
                i = 3;
              }
            }
          }
        }
        break;
      }
    }
    paramSpannableStringBuilder.append('<');
    return;
    paramSpannableStringBuilder.append('>');
    return;
    paramSpannableStringBuilder.append(' ');
    return;
    paramSpannableStringBuilder.append('&');
  }
  
  private static void applySpansForTag(StartTag paramStartTag, SpannableStringBuilder paramSpannableStringBuilder)
  {
    String str = paramStartTag.name;
    int i = -1;
    switch (str.hashCode())
    {
    }
    for (;;)
    {
      switch (i)
      {
      default: 
        return;
        if (str.equals("b"))
        {
          i = 0;
          continue;
          if (str.equals("i"))
          {
            i = 1;
            continue;
            if (str.equals("u")) {
              i = 2;
            }
          }
        }
        break;
      }
    }
    paramSpannableStringBuilder.setSpan(new StyleSpan(1), paramStartTag.position, paramSpannableStringBuilder.length(), 33);
    return;
    paramSpannableStringBuilder.setSpan(new StyleSpan(2), paramStartTag.position, paramSpannableStringBuilder.length(), 33);
    return;
    paramSpannableStringBuilder.setSpan(new UnderlineSpan(), paramStartTag.position, paramSpannableStringBuilder.length(), 33);
  }
  
  private static int findEndOfTag(String paramString, int paramInt)
  {
    paramInt = paramString.indexOf('>', paramInt);
    if (paramInt == -1) {
      return paramString.length();
    }
    return paramInt + 1;
  }
  
  public static Matcher findNextCueHeader(ParsableByteArray paramParsableByteArray)
  {
    Object localObject;
    do
    {
      localObject = paramParsableByteArray.readLine();
      if (localObject == null) {
        break;
      }
      if (COMMENT.matcher((CharSequence)localObject).matches()) {
        for (;;)
        {
          localObject = paramParsableByteArray.readLine();
          if ((localObject == null) || (((String)localObject).isEmpty())) {
            break;
          }
        }
      }
      localObject = CUE_HEADER_PATTERN.matcher((CharSequence)localObject);
    } while (!((Matcher)localObject).matches());
    return (Matcher)localObject;
    return null;
  }
  
  private static boolean isSupportedTag(String paramString)
  {
    boolean bool = true;
    int i = -1;
    switch (paramString.hashCode())
    {
    }
    for (;;)
    {
      switch (i)
      {
      default: 
        bool = false;
      }
      return bool;
      if (paramString.equals("b"))
      {
        i = 0;
        continue;
        if (paramString.equals("c"))
        {
          i = 1;
          continue;
          if (paramString.equals("i"))
          {
            i = 2;
            continue;
            if (paramString.equals("lang"))
            {
              i = 3;
              continue;
              if (paramString.equals("u"))
              {
                i = 4;
                continue;
                if (paramString.equals("v")) {
                  i = 5;
                }
              }
            }
          }
        }
      }
    }
  }
  
  private static boolean parseCue(Matcher paramMatcher, ParsableByteArray paramParsableByteArray, WebvttCue.Builder paramBuilder, StringBuilder paramStringBuilder)
  {
    try
    {
      paramBuilder.setStartTime(WebvttParserUtil.parseTimestampUs(paramMatcher.group(1))).setEndTime(WebvttParserUtil.parseTimestampUs(paramMatcher.group(2)));
      parseCueSettingsList(paramMatcher.group(3), paramBuilder);
      paramStringBuilder.setLength(0);
      for (;;)
      {
        paramMatcher = paramParsableByteArray.readLine();
        if ((paramMatcher == null) || (paramMatcher.isEmpty())) {
          break;
        }
        if (paramStringBuilder.length() > 0) {
          paramStringBuilder.append("\n");
        }
        paramStringBuilder.append(paramMatcher.trim());
      }
      parseCueText(paramStringBuilder.toString(), paramBuilder);
    }
    catch (NumberFormatException paramParsableByteArray)
    {
      Log.w("WebvttCueParser", "Skipping cue with bad header: " + paramMatcher.group());
      return false;
    }
    return true;
  }
  
  static void parseCueSettingsList(String paramString, WebvttCue.Builder paramBuilder)
  {
    paramString = CUE_SETTING_PATTERN.matcher(paramString);
    while (paramString.find())
    {
      String str1 = paramString.group(1);
      String str2 = paramString.group(2);
      try
      {
        if (!"line".equals(str1)) {
          break label76;
        }
        parseLineAttribute(str2, paramBuilder);
      }
      catch (NumberFormatException localNumberFormatException)
      {
        Log.w("WebvttCueParser", "Skipping bad cue setting: " + paramString.group());
      }
      continue;
      label76:
      if ("align".equals(localNumberFormatException)) {
        paramBuilder.setTextAlignment(parseTextAlignment(str2));
      } else if ("position".equals(localNumberFormatException)) {
        parsePositionAttribute(str2, paramBuilder);
      } else if ("size".equals(localNumberFormatException)) {
        paramBuilder.setWidth(WebvttParserUtil.parsePercentage(str2));
      } else {
        Log.w("WebvttCueParser", "Unknown cue setting " + localNumberFormatException + ":" + str2);
      }
    }
  }
  
  static void parseCueText(String paramString, WebvttCue.Builder paramBuilder)
  {
    SpannableStringBuilder localSpannableStringBuilder = new SpannableStringBuilder();
    Stack localStack = new Stack();
    int i = 0;
    for (;;)
    {
      int n = i;
      if (n >= paramString.length()) {
        break;
      }
      char c = paramString.charAt(n);
      int j;
      switch (c)
      {
      default: 
        localSpannableStringBuilder.append(c);
        i = n + 1;
        break;
      case '<': 
        if (n + 1 >= paramString.length())
        {
          i = n + 1;
        }
        else
        {
          int i1;
          int k;
          if (paramString.charAt(n + 1) == '/')
          {
            j = 1;
            i1 = findEndOfTag(paramString, n + 1);
            if (paramString.charAt(i1 - 2) != '/') {
              break label258;
            }
            k = 1;
            if (j == 0) {
              break label264;
            }
            i = 2;
            if (k == 0) {
              break label269;
            }
          }
          String[] arrayOfString;
          for (int m = i1 - 2;; m = i1 - 1)
          {
            arrayOfString = tokenizeTag(paramString.substring(n + i, m));
            i = i1;
            if (arrayOfString == null) {
              break;
            }
            i = i1;
            if (!isSupportedTag(arrayOfString[0])) {
              break;
            }
            if (j == 0) {
              break label278;
            }
            StartTag localStartTag;
            do
            {
              i = i1;
              if (localStack.isEmpty()) {
                break;
              }
              localStartTag = (StartTag)localStack.pop();
              applySpansForTag(localStartTag, localSpannableStringBuilder);
            } while (!localStartTag.name.equals(arrayOfString[0]));
            i = i1;
            break;
            j = 0;
            break label118;
            k = 0;
            break label144;
            i = 1;
            break label151;
          }
          i = i1;
          if (k == 0)
          {
            localStack.push(new StartTag(arrayOfString[0], localSpannableStringBuilder.length()));
            i = i1;
          }
        }
        break;
      case '&': 
        label118:
        label144:
        label151:
        label258:
        label264:
        label269:
        label278:
        i = paramString.indexOf(';', n + 1);
        j = paramString.indexOf(' ', n + 1);
        if (i == -1) {
          i = j;
        }
        for (;;)
        {
          if (i == -1) {
            break label404;
          }
          applyEntity(paramString.substring(n + 1, i), localSpannableStringBuilder);
          if (i == j) {
            localSpannableStringBuilder.append(" ");
          }
          i += 1;
          break;
          if (j != -1) {
            i = Math.min(i, j);
          }
        }
        label404:
        localSpannableStringBuilder.append(c);
        i = n + 1;
      }
    }
    while (!localStack.isEmpty()) {
      applySpansForTag((StartTag)localStack.pop(), localSpannableStringBuilder);
    }
    paramBuilder.setText(localSpannableStringBuilder);
  }
  
  private static void parseLineAttribute(String paramString, WebvttCue.Builder paramBuilder)
    throws NumberFormatException
  {
    int i = paramString.indexOf(',');
    if (i != -1)
    {
      paramBuilder.setLineAnchor(parsePositionAnchor(paramString.substring(i + 1)));
      paramString = paramString.substring(0, i);
    }
    while (paramString.endsWith("%"))
    {
      paramBuilder.setLine(WebvttParserUtil.parsePercentage(paramString)).setLineType(0);
      return;
      paramBuilder.setLineAnchor(Integer.MIN_VALUE);
    }
    paramBuilder.setLine(Integer.parseInt(paramString)).setLineType(1);
  }
  
  private static int parsePositionAnchor(String paramString)
  {
    int j = 0;
    int i = -1;
    switch (paramString.hashCode())
    {
    }
    for (;;)
    {
      switch (i)
      {
      default: 
        Log.w("WebvttCueParser", "Invalid anchor value: " + paramString);
        j = Integer.MIN_VALUE;
      case 0: 
        return j;
        if (paramString.equals("start"))
        {
          i = 0;
          continue;
          if (paramString.equals("center"))
          {
            i = 1;
            continue;
            if (paramString.equals("middle"))
            {
              i = 2;
              continue;
              if (paramString.equals("end")) {
                i = 3;
              }
            }
          }
        }
        break;
      }
    }
    return 1;
    return 2;
  }
  
  private static void parsePositionAttribute(String paramString, WebvttCue.Builder paramBuilder)
    throws NumberFormatException
  {
    int i = paramString.indexOf(',');
    if (i != -1)
    {
      paramBuilder.setPositionAnchor(parsePositionAnchor(paramString.substring(i + 1)));
      paramString = paramString.substring(0, i);
    }
    for (;;)
    {
      paramBuilder.setPosition(WebvttParserUtil.parsePercentage(paramString));
      return;
      paramBuilder.setPositionAnchor(Integer.MIN_VALUE);
    }
  }
  
  private static Layout.Alignment parseTextAlignment(String paramString)
  {
    int i = -1;
    switch (paramString.hashCode())
    {
    }
    for (;;)
    {
      switch (i)
      {
      default: 
        Log.w("WebvttCueParser", "Invalid alignment value: " + paramString);
        return null;
        if (paramString.equals("start"))
        {
          i = 0;
          continue;
          if (paramString.equals("left"))
          {
            i = 1;
            continue;
            if (paramString.equals("center"))
            {
              i = 2;
              continue;
              if (paramString.equals("middle"))
              {
                i = 3;
                continue;
                if (paramString.equals("end"))
                {
                  i = 4;
                  continue;
                  if (paramString.equals("right")) {
                    i = 5;
                  }
                }
              }
            }
          }
        }
        break;
      }
    }
    return Layout.Alignment.ALIGN_NORMAL;
    return Layout.Alignment.ALIGN_CENTER;
    return Layout.Alignment.ALIGN_OPPOSITE;
  }
  
  private static String[] tokenizeTag(String paramString)
  {
    String str = paramString.replace("\\s+", " ").trim();
    if (str.length() == 0) {
      return null;
    }
    paramString = str;
    if (str.contains(" ")) {
      paramString = str.substring(0, str.indexOf(" "));
    }
    return paramString.split("\\.");
  }
  
  boolean parseNextValidCue(ParsableByteArray paramParsableByteArray, WebvttCue.Builder paramBuilder)
  {
    Matcher localMatcher;
    do
    {
      localMatcher = findNextCueHeader(paramParsableByteArray);
      if (localMatcher == null) {
        break;
      }
    } while (!parseCue(localMatcher, paramParsableByteArray, paramBuilder, this.textBuilder));
    return true;
    return false;
  }
  
  private static final class StartTag
  {
    public final String name;
    public final int position;
    
    public StartTag(String paramString, int paramInt)
    {
      this.position = paramInt;
      this.name = paramString;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/text/webvtt/WebvttCueParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */