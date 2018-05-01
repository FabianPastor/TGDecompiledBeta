package org.telegram.messenger.exoplayer2.text.webvtt;

import android.text.Layout.Alignment;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.AlignmentSpan.Standard;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

public final class WebvttCueParser
{
  private static final char CHAR_AMPERSAND = '&';
  private static final char CHAR_GREATER_THAN = '>';
  private static final char CHAR_LESS_THAN = '<';
  private static final char CHAR_SEMI_COLON = ';';
  private static final char CHAR_SLASH = '/';
  private static final char CHAR_SPACE = ' ';
  public static final Pattern CUE_HEADER_PATTERN = Pattern.compile("^(\\S+)\\s+-->\\s+(\\S+)(.*)?$");
  private static final Pattern CUE_SETTING_PATTERN = Pattern.compile("(\\S+?):(\\S+)");
  private static final String ENTITY_AMPERSAND = "amp";
  private static final String ENTITY_GREATER_THAN = "gt";
  private static final String ENTITY_LESS_THAN = "lt";
  private static final String ENTITY_NON_BREAK_SPACE = "nbsp";
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
    default: 
      switch (i)
      {
      default: 
        Log.w("WebvttCueParser", "ignoring unsupported entity: '&" + paramString + ";'");
      }
      break;
    }
    for (;;)
    {
      return;
      if (!paramString.equals("lt")) {
        break;
      }
      i = 0;
      break;
      if (!paramString.equals("gt")) {
        break;
      }
      i = 1;
      break;
      if (!paramString.equals("nbsp")) {
        break;
      }
      i = 2;
      break;
      if (!paramString.equals("amp")) {
        break;
      }
      i = 3;
      break;
      paramSpannableStringBuilder.append('<');
      continue;
      paramSpannableStringBuilder.append('>');
      continue;
      paramSpannableStringBuilder.append(' ');
      continue;
      paramSpannableStringBuilder.append('&');
    }
  }
  
  private static void applySpansForTag(String paramString, StartTag paramStartTag, SpannableStringBuilder paramSpannableStringBuilder, List<WebvttCssStyle> paramList, List<StyleMatch> paramList1)
  {
    int i = paramStartTag.position;
    int j = paramSpannableStringBuilder.length();
    String str = paramStartTag.name;
    int k = -1;
    switch (str.hashCode())
    {
    }
    for (;;)
    {
      switch (k)
      {
      default: 
        return;
        if (str.equals("b"))
        {
          k = 0;
          continue;
          if (str.equals("i"))
          {
            k = 1;
            continue;
            if (str.equals("u"))
            {
              k = 2;
              continue;
              if (str.equals("c"))
              {
                k = 3;
                continue;
                if (str.equals("lang"))
                {
                  k = 4;
                  continue;
                  if (str.equals("v"))
                  {
                    k = 5;
                    continue;
                    if (str.equals("")) {
                      k = 6;
                    }
                  }
                }
              }
            }
          }
        }
        break;
      }
    }
    paramSpannableStringBuilder.setSpan(new StyleSpan(1), i, j, 33);
    for (;;)
    {
      paramList1.clear();
      getApplicableStyles(paramList, paramString, paramStartTag, paramList1);
      int m = paramList1.size();
      for (k = 0; k < m; k++) {
        applyStyleToText(paramSpannableStringBuilder, ((StyleMatch)paramList1.get(k)).style, i, j);
      }
      paramSpannableStringBuilder.setSpan(new StyleSpan(2), i, j, 33);
      continue;
      paramSpannableStringBuilder.setSpan(new UnderlineSpan(), i, j, 33);
    }
  }
  
  private static void applyStyleToText(SpannableStringBuilder paramSpannableStringBuilder, WebvttCssStyle paramWebvttCssStyle, int paramInt1, int paramInt2)
  {
    if (paramWebvttCssStyle == null) {}
    for (;;)
    {
      return;
      if (paramWebvttCssStyle.getStyle() != -1) {
        paramSpannableStringBuilder.setSpan(new StyleSpan(paramWebvttCssStyle.getStyle()), paramInt1, paramInt2, 33);
      }
      if (paramWebvttCssStyle.isLinethrough()) {
        paramSpannableStringBuilder.setSpan(new StrikethroughSpan(), paramInt1, paramInt2, 33);
      }
      if (paramWebvttCssStyle.isUnderline()) {
        paramSpannableStringBuilder.setSpan(new UnderlineSpan(), paramInt1, paramInt2, 33);
      }
      if (paramWebvttCssStyle.hasFontColor()) {
        paramSpannableStringBuilder.setSpan(new ForegroundColorSpan(paramWebvttCssStyle.getFontColor()), paramInt1, paramInt2, 33);
      }
      if (paramWebvttCssStyle.hasBackgroundColor()) {
        paramSpannableStringBuilder.setSpan(new BackgroundColorSpan(paramWebvttCssStyle.getBackgroundColor()), paramInt1, paramInt2, 33);
      }
      if (paramWebvttCssStyle.getFontFamily() != null) {
        paramSpannableStringBuilder.setSpan(new TypefaceSpan(paramWebvttCssStyle.getFontFamily()), paramInt1, paramInt2, 33);
      }
      if (paramWebvttCssStyle.getTextAlign() != null) {
        paramSpannableStringBuilder.setSpan(new AlignmentSpan.Standard(paramWebvttCssStyle.getTextAlign()), paramInt1, paramInt2, 33);
      }
      switch (paramWebvttCssStyle.getFontSizeUnit())
      {
      default: 
        break;
      case 1: 
        paramSpannableStringBuilder.setSpan(new AbsoluteSizeSpan((int)paramWebvttCssStyle.getFontSize(), true), paramInt1, paramInt2, 33);
        break;
      case 2: 
        paramSpannableStringBuilder.setSpan(new RelativeSizeSpan(paramWebvttCssStyle.getFontSize()), paramInt1, paramInt2, 33);
        break;
      case 3: 
        paramSpannableStringBuilder.setSpan(new RelativeSizeSpan(paramWebvttCssStyle.getFontSize() / 100.0F), paramInt1, paramInt2, 33);
      }
    }
  }
  
  private static int findEndOfTag(String paramString, int paramInt)
  {
    paramInt = paramString.indexOf('>', paramInt);
    if (paramInt == -1) {}
    for (paramInt = paramString.length();; paramInt++) {
      return paramInt;
    }
  }
  
  private static void getApplicableStyles(List<WebvttCssStyle> paramList, String paramString, StartTag paramStartTag, List<StyleMatch> paramList1)
  {
    int i = paramList.size();
    for (int j = 0; j < i; j++)
    {
      WebvttCssStyle localWebvttCssStyle = (WebvttCssStyle)paramList.get(j);
      int k = localWebvttCssStyle.getSpecificityScore(paramString, paramStartTag.name, paramStartTag.classes, paramStartTag.voice);
      if (k > 0) {
        paramList1.add(new StyleMatch(k, localWebvttCssStyle));
      }
    }
    Collections.sort(paramList1);
  }
  
  private static String getTagName(String paramString)
  {
    paramString = paramString.trim();
    if (paramString.isEmpty()) {}
    for (paramString = null;; paramString = paramString.split("[ \\.]")[0]) {
      return paramString;
    }
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
  
  private static boolean parseCue(String paramString, Matcher paramMatcher, ParsableByteArray paramParsableByteArray, WebvttCue.Builder paramBuilder, StringBuilder paramStringBuilder, List<WebvttCssStyle> paramList)
  {
    boolean bool = true;
    try
    {
      paramBuilder.setStartTime(WebvttParserUtil.parseTimestampUs(paramMatcher.group(1))).setEndTime(WebvttParserUtil.parseTimestampUs(paramMatcher.group(2)));
      parseCueSettingsList(paramMatcher.group(3), paramBuilder);
      paramStringBuilder.setLength(0);
      for (;;)
      {
        paramMatcher = paramParsableByteArray.readLine();
        if (TextUtils.isEmpty(paramMatcher)) {
          break;
        }
        if (paramStringBuilder.length() > 0) {
          paramStringBuilder.append("\n");
        }
        paramStringBuilder.append(paramMatcher.trim());
      }
      return bool;
    }
    catch (NumberFormatException paramString)
    {
      Log.w("WebvttCueParser", "Skipping cue with bad header: " + paramMatcher.group());
      bool = false;
    }
    for (;;)
    {
      parseCueText(paramString, paramStringBuilder.toString(), paramBuilder, paramList);
    }
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
          break label79;
        }
        parseLineAttribute(str2, paramBuilder);
      }
      catch (NumberFormatException localNumberFormatException)
      {
        Log.w("WebvttCueParser", "Skipping bad cue setting: " + paramString.group());
      }
      continue;
      label79:
      if ("align".equals(str1))
      {
        paramBuilder.setTextAlignment(parseTextAlignment(str2));
      }
      else if ("position".equals(str1))
      {
        parsePositionAttribute(str2, paramBuilder);
      }
      else if ("size".equals(str1))
      {
        paramBuilder.setWidth(WebvttParserUtil.parsePercentage(str2));
      }
      else
      {
        StringBuilder localStringBuilder = new java/lang/StringBuilder;
        localStringBuilder.<init>();
        Log.w("WebvttCueParser", "Unknown cue setting " + str1 + ":" + str2);
      }
    }
  }
  
  static void parseCueText(String paramString1, String paramString2, WebvttCue.Builder paramBuilder, List<WebvttCssStyle> paramList)
  {
    SpannableStringBuilder localSpannableStringBuilder = new SpannableStringBuilder();
    Stack localStack = new Stack();
    ArrayList localArrayList = new ArrayList();
    int i = 0;
    for (;;)
    {
      int j = i;
      if (j >= paramString2.length()) {
        break;
      }
      char c = paramString2.charAt(j);
      int k;
      switch (c)
      {
      default: 
        localSpannableStringBuilder.append(c);
        i = j + 1;
        break;
      case '<': 
        if (j + 1 >= paramString2.length())
        {
          i = j + 1;
        }
        else
        {
          int m;
          int n;
          if (paramString2.charAt(j + 1) == '/')
          {
            k = 1;
            m = findEndOfTag(paramString2, j + 1);
            if (paramString2.charAt(m - 2) != '/') {
              break label283;
            }
            n = 1;
            if (k == 0) {
              break label289;
            }
            i = 2;
            if (n == 0) {
              break label295;
            }
          }
          Object localObject;
          for (int i1 = m - 2;; i1 = m - 1)
          {
            localObject = paramString2.substring(j + i, i1);
            String str = getTagName((String)localObject);
            i = m;
            if (str == null) {
              break;
            }
            i = m;
            if (!isSupportedTag(str)) {
              break;
            }
            if (k == 0) {
              break label304;
            }
            do
            {
              i = m;
              if (localStack.isEmpty()) {
                break;
              }
              localObject = (StartTag)localStack.pop();
              applySpansForTag(paramString1, (StartTag)localObject, localSpannableStringBuilder, paramList, localArrayList);
            } while (!((StartTag)localObject).name.equals(str));
            i = m;
            break;
            k = 0;
            break label133;
            n = 0;
            break label159;
            i = 1;
            break label167;
          }
          i = m;
          if (n == 0)
          {
            localStack.push(StartTag.buildStartTag((String)localObject, localSpannableStringBuilder.length()));
            i = m;
          }
        }
        break;
      case '&': 
        label133:
        label159:
        label167:
        label283:
        label289:
        label295:
        label304:
        i = paramString2.indexOf(';', j + 1);
        k = paramString2.indexOf(' ', j + 1);
        if (i == -1) {
          i = k;
        }
        for (;;)
        {
          if (i == -1) {
            break label434;
          }
          applyEntity(paramString2.substring(j + 1, i), localSpannableStringBuilder);
          if (i == k) {
            localSpannableStringBuilder.append(" ");
          }
          i++;
          break;
          if (k != -1) {
            i = Math.min(i, k);
          }
        }
        label434:
        localSpannableStringBuilder.append(c);
        i = j + 1;
      }
    }
    while (!localStack.isEmpty()) {
      applySpansForTag(paramString1, (StartTag)localStack.pop(), localSpannableStringBuilder, paramList, localArrayList);
    }
    applySpansForTag(paramString1, StartTag.buildWholeCueVirtualTag(), localSpannableStringBuilder, paramList, localArrayList);
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
      if (!paramString.endsWith("%")) {
        break label69;
      }
      paramBuilder.setLine(WebvttParserUtil.parsePercentage(paramString)).setLineType(0);
    }
    for (;;)
    {
      return;
      paramBuilder.setLineAnchor(Integer.MIN_VALUE);
      break;
      label69:
      int j = Integer.parseInt(paramString);
      i = j;
      if (j < 0) {
        i = j - 1;
      }
      paramBuilder.setLine(i).setLineType(1);
    }
  }
  
  private static int parsePositionAnchor(String paramString)
  {
    int i = 0;
    int j = -1;
    switch (paramString.hashCode())
    {
    default: 
      switch (j)
      {
      default: 
        Log.w("WebvttCueParser", "Invalid anchor value: " + paramString);
        i = Integer.MIN_VALUE;
      }
      break;
    }
    for (;;)
    {
      return i;
      if (!paramString.equals("start")) {
        break;
      }
      j = 0;
      break;
      if (!paramString.equals("center")) {
        break;
      }
      j = 1;
      break;
      if (!paramString.equals("middle")) {
        break;
      }
      j = 2;
      break;
      if (!paramString.equals("end")) {
        break;
      }
      j = 3;
      break;
      i = 1;
      continue;
      i = 2;
    }
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
    default: 
      switch (i)
      {
      default: 
        Log.w("WebvttCueParser", "Invalid alignment value: " + paramString);
        paramString = null;
      }
      break;
    }
    for (;;)
    {
      return paramString;
      if (!paramString.equals("start")) {
        break;
      }
      i = 0;
      break;
      if (!paramString.equals("left")) {
        break;
      }
      i = 1;
      break;
      if (!paramString.equals("center")) {
        break;
      }
      i = 2;
      break;
      if (!paramString.equals("middle")) {
        break;
      }
      i = 3;
      break;
      if (!paramString.equals("end")) {
        break;
      }
      i = 4;
      break;
      if (!paramString.equals("right")) {
        break;
      }
      i = 5;
      break;
      paramString = Layout.Alignment.ALIGN_NORMAL;
      continue;
      paramString = Layout.Alignment.ALIGN_CENTER;
      continue;
      paramString = Layout.Alignment.ALIGN_OPPOSITE;
    }
  }
  
  public boolean parseCue(ParsableByteArray paramParsableByteArray, WebvttCue.Builder paramBuilder, List<WebvttCssStyle> paramList)
  {
    boolean bool1 = false;
    String str = paramParsableByteArray.readLine();
    boolean bool2;
    if (str == null) {
      bool2 = bool1;
    }
    for (;;)
    {
      return bool2;
      Object localObject = CUE_HEADER_PATTERN.matcher(str);
      if (((Matcher)localObject).matches())
      {
        bool2 = parseCue(null, (Matcher)localObject, paramParsableByteArray, paramBuilder, this.textBuilder, paramList);
      }
      else
      {
        localObject = paramParsableByteArray.readLine();
        bool2 = bool1;
        if (localObject != null)
        {
          localObject = CUE_HEADER_PATTERN.matcher((CharSequence)localObject);
          bool2 = bool1;
          if (((Matcher)localObject).matches()) {
            bool2 = parseCue(str.trim(), (Matcher)localObject, paramParsableByteArray, paramBuilder, this.textBuilder, paramList);
          }
        }
      }
    }
  }
  
  private static final class StartTag
  {
    private static final String[] NO_CLASSES = new String[0];
    public final String[] classes;
    public final String name;
    public final int position;
    public final String voice;
    
    private StartTag(String paramString1, int paramInt, String paramString2, String[] paramArrayOfString)
    {
      this.position = paramInt;
      this.name = paramString1;
      this.voice = paramString2;
      this.classes = paramArrayOfString;
    }
    
    public static StartTag buildStartTag(String paramString, int paramInt)
    {
      Object localObject = paramString.trim();
      if (((String)localObject).isEmpty())
      {
        paramString = null;
        return paramString;
      }
      int i = ((String)localObject).indexOf(" ");
      label31:
      String str;
      if (i == -1)
      {
        paramString = "";
        localObject = ((String)localObject).split("\\.");
        str = localObject[0];
        if (localObject.length <= 1) {
          break label95;
        }
      }
      label95:
      for (localObject = (String[])Arrays.copyOfRange((Object[])localObject, 1, localObject.length);; localObject = NO_CLASSES)
      {
        paramString = new StartTag(str, paramInt, paramString, (String[])localObject);
        break;
        paramString = ((String)localObject).substring(i).trim();
        localObject = ((String)localObject).substring(0, i);
        break label31;
      }
    }
    
    public static StartTag buildWholeCueVirtualTag()
    {
      return new StartTag("", 0, "", new String[0]);
    }
  }
  
  private static final class StyleMatch
    implements Comparable<StyleMatch>
  {
    public final int score;
    public final WebvttCssStyle style;
    
    public StyleMatch(int paramInt, WebvttCssStyle paramWebvttCssStyle)
    {
      this.score = paramInt;
      this.style = paramWebvttCssStyle;
    }
    
    public int compareTo(StyleMatch paramStyleMatch)
    {
      return this.score - paramStyleMatch.score;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/text/webvtt/WebvttCueParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */