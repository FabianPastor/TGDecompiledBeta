package org.telegram.messenger.exoplayer2.text.webvtt;

import android.text.TextUtils;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.messenger.exoplayer2.util.ColorParser;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

final class CssParser
{
  private static final String BLOCK_END = "}";
  private static final String BLOCK_START = "{";
  private static final String PROPERTY_BGCOLOR = "background-color";
  private static final String PROPERTY_FONT_FAMILY = "font-family";
  private static final String PROPERTY_FONT_STYLE = "font-style";
  private static final String PROPERTY_FONT_WEIGHT = "font-weight";
  private static final String PROPERTY_TEXT_DECORATION = "text-decoration";
  private static final String VALUE_BOLD = "bold";
  private static final String VALUE_ITALIC = "italic";
  private static final String VALUE_UNDERLINE = "underline";
  private static final Pattern VOICE_NAME_PATTERN = Pattern.compile("\\[voice=\"([^\"]*)\"\\]");
  private final StringBuilder stringBuilder = new StringBuilder();
  private final ParsableByteArray styleInput = new ParsableByteArray();
  
  private void applySelectorToStyle(WebvttCssStyle paramWebvttCssStyle, String paramString)
  {
    if ("".equals(paramString)) {}
    label143:
    for (;;)
    {
      return;
      int i = paramString.indexOf('[');
      Object localObject = paramString;
      if (i != -1)
      {
        localObject = VOICE_NAME_PATTERN.matcher(paramString.substring(i));
        if (((Matcher)localObject).matches()) {
          paramWebvttCssStyle.setTargetVoice(((Matcher)localObject).group(1));
        }
        localObject = paramString.substring(0, i);
      }
      localObject = ((String)localObject).split("\\.");
      paramString = localObject[0];
      i = paramString.indexOf('#');
      if (i != -1)
      {
        paramWebvttCssStyle.setTargetTagName(paramString.substring(0, i));
        paramWebvttCssStyle.setTargetId(paramString.substring(i + 1));
      }
      for (;;)
      {
        if (localObject.length <= 1) {
          break label143;
        }
        paramWebvttCssStyle.setTargetClasses((String[])Arrays.copyOfRange((Object[])localObject, 1, localObject.length));
        break;
        paramWebvttCssStyle.setTargetTagName(paramString);
      }
    }
  }
  
  private static boolean maybeSkipComment(ParsableByteArray paramParsableByteArray)
  {
    int i = paramParsableByteArray.getPosition();
    int j = paramParsableByteArray.limit();
    byte[] arrayOfByte = paramParsableByteArray.data;
    if (i + 2 <= j)
    {
      int k = i + 1;
      if ((arrayOfByte[i] == 47) && (arrayOfByte[k] == 42))
      {
        k++;
        while (k + 1 < j)
        {
          int m = k + 1;
          int n = j;
          i = m;
          if ((char)arrayOfByte[k] == '*')
          {
            n = j;
            i = m;
            if ((char)arrayOfByte[m] == '/')
            {
              i = m + 1;
              n = i;
            }
          }
          j = n;
          k = i;
        }
        paramParsableByteArray.skipBytes(j - paramParsableByteArray.getPosition());
      }
    }
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  private static boolean maybeSkipWhitespace(ParsableByteArray paramParsableByteArray)
  {
    boolean bool = true;
    switch (peekCharAtPosition(paramParsableByteArray, paramParsableByteArray.getPosition()))
    {
    default: 
      bool = false;
    }
    for (;;)
    {
      return bool;
      paramParsableByteArray.skipBytes(1);
    }
  }
  
  private static String parseIdentifier(ParsableByteArray paramParsableByteArray, StringBuilder paramStringBuilder)
  {
    paramStringBuilder.setLength(0);
    int i = paramParsableByteArray.getPosition();
    int j = paramParsableByteArray.limit();
    int k = 0;
    while ((i < j) && (k == 0))
    {
      char c = (char)paramParsableByteArray.data[i];
      if (((c >= 'A') && (c <= 'Z')) || ((c >= 'a') && (c <= 'z')) || ((c >= '0') && (c <= '9')) || (c == '#') || (c == '-') || (c == '.') || (c == '_'))
      {
        i++;
        paramStringBuilder.append(c);
      }
      else
      {
        k = 1;
      }
    }
    paramParsableByteArray.skipBytes(i - paramParsableByteArray.getPosition());
    return paramStringBuilder.toString();
  }
  
  static String parseNextToken(ParsableByteArray paramParsableByteArray, StringBuilder paramStringBuilder)
  {
    skipWhitespaceAndComments(paramParsableByteArray);
    if (paramParsableByteArray.bytesLeft() == 0) {
      paramStringBuilder = null;
    }
    for (;;)
    {
      return paramStringBuilder;
      String str = parseIdentifier(paramParsableByteArray, paramStringBuilder);
      paramStringBuilder = str;
      if ("".equals(str)) {
        paramStringBuilder = "" + (char)paramParsableByteArray.readUnsignedByte();
      }
    }
  }
  
  private static String parsePropertyValue(ParsableByteArray paramParsableByteArray, StringBuilder paramStringBuilder)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    int i = 0;
    int j;
    String str;
    if (i == 0)
    {
      j = paramParsableByteArray.getPosition();
      str = parseNextToken(paramParsableByteArray, paramStringBuilder);
      if (str != null) {}
    }
    for (paramParsableByteArray = null;; paramParsableByteArray = localStringBuilder.toString())
    {
      return paramParsableByteArray;
      if (("}".equals(str)) || (";".equals(str)))
      {
        paramParsableByteArray.setPosition(j);
        i = 1;
        break;
      }
      localStringBuilder.append(str);
      break;
    }
  }
  
  private static String parseSelector(ParsableByteArray paramParsableByteArray, StringBuilder paramStringBuilder)
  {
    skipWhitespaceAndComments(paramParsableByteArray);
    if (paramParsableByteArray.bytesLeft() < 5) {
      paramParsableByteArray = null;
    }
    for (;;)
    {
      return paramParsableByteArray;
      if (!"::cue".equals(paramParsableByteArray.readString(5)))
      {
        paramParsableByteArray = null;
      }
      else
      {
        int i = paramParsableByteArray.getPosition();
        String str1 = parseNextToken(paramParsableByteArray, paramStringBuilder);
        if (str1 == null)
        {
          paramParsableByteArray = null;
        }
        else if ("{".equals(str1))
        {
          paramParsableByteArray.setPosition(i);
          paramParsableByteArray = "";
        }
        else
        {
          String str2 = null;
          if ("(".equals(str1)) {
            str2 = readCueTarget(paramParsableByteArray);
          }
          paramStringBuilder = parseNextToken(paramParsableByteArray, paramStringBuilder);
          if (")".equals(paramStringBuilder))
          {
            paramParsableByteArray = str2;
            if (paramStringBuilder != null) {}
          }
          else
          {
            paramParsableByteArray = null;
          }
        }
      }
    }
  }
  
  private static void parseStyleDeclaration(ParsableByteArray paramParsableByteArray, WebvttCssStyle paramWebvttCssStyle, StringBuilder paramStringBuilder)
  {
    skipWhitespaceAndComments(paramParsableByteArray);
    String str1 = parseIdentifier(paramParsableByteArray, paramStringBuilder);
    if ("".equals(str1)) {}
    for (;;)
    {
      return;
      if (":".equals(parseNextToken(paramParsableByteArray, paramStringBuilder)))
      {
        skipWhitespaceAndComments(paramParsableByteArray);
        String str2 = parsePropertyValue(paramParsableByteArray, paramStringBuilder);
        if ((str2 != null) && (!"".equals(str2)))
        {
          int i = paramParsableByteArray.getPosition();
          paramStringBuilder = parseNextToken(paramParsableByteArray, paramStringBuilder);
          if (";".equals(paramStringBuilder)) {}
          for (;;)
          {
            if (!"color".equals(str1)) {
              break label120;
            }
            paramWebvttCssStyle.setFontColor(ColorParser.parseCssColor(str2));
            break;
            if (!"}".equals(paramStringBuilder)) {
              break;
            }
            paramParsableByteArray.setPosition(i);
          }
          label120:
          if ("background-color".equals(str1)) {
            paramWebvttCssStyle.setBackgroundColor(ColorParser.parseCssColor(str2));
          } else if ("text-decoration".equals(str1))
          {
            if ("underline".equals(str2)) {
              paramWebvttCssStyle.setUnderline(true);
            }
          }
          else if ("font-family".equals(str1)) {
            paramWebvttCssStyle.setFontFamily(str2);
          } else if ("font-weight".equals(str1))
          {
            if ("bold".equals(str2)) {
              paramWebvttCssStyle.setBold(true);
            }
          }
          else if (("font-style".equals(str1)) && ("italic".equals(str2))) {
            paramWebvttCssStyle.setItalic(true);
          }
        }
      }
    }
  }
  
  private static char peekCharAtPosition(ParsableByteArray paramParsableByteArray, int paramInt)
  {
    return (char)paramParsableByteArray.data[paramInt];
  }
  
  private static String readCueTarget(ParsableByteArray paramParsableByteArray)
  {
    int i = paramParsableByteArray.getPosition();
    int j = paramParsableByteArray.limit();
    int k = 0;
    if ((i < j) && (k == 0))
    {
      if ((char)paramParsableByteArray.data[i] == ')') {}
      for (k = 1;; k = 0)
      {
        i++;
        break;
      }
    }
    return paramParsableByteArray.readString(i - 1 - paramParsableByteArray.getPosition()).trim();
  }
  
  static void skipStyleBlock(ParsableByteArray paramParsableByteArray)
  {
    while (!TextUtils.isEmpty(paramParsableByteArray.readLine())) {}
  }
  
  static void skipWhitespaceAndComments(ParsableByteArray paramParsableByteArray)
  {
    int i = 1;
    if ((paramParsableByteArray.bytesLeft() > 0) && (i != 0))
    {
      if ((maybeSkipWhitespace(paramParsableByteArray)) || (maybeSkipComment(paramParsableByteArray))) {}
      for (i = 1;; i = 0) {
        break;
      }
    }
  }
  
  public WebvttCssStyle parseBlock(ParsableByteArray paramParsableByteArray)
  {
    this.stringBuilder.setLength(0);
    int i = paramParsableByteArray.getPosition();
    skipStyleBlock(paramParsableByteArray);
    this.styleInput.reset(paramParsableByteArray.data, paramParsableByteArray.getPosition());
    this.styleInput.setPosition(i);
    Object localObject = parseSelector(this.styleInput, this.stringBuilder);
    if ((localObject == null) || (!"{".equals(parseNextToken(this.styleInput, this.stringBuilder)))) {
      paramParsableByteArray = null;
    }
    for (;;)
    {
      return paramParsableByteArray;
      paramParsableByteArray = new WebvttCssStyle();
      applySelectorToStyle(paramParsableByteArray, (String)localObject);
      localObject = null;
      int j = 0;
      if (j == 0)
      {
        int k = this.styleInput.getPosition();
        String str = parseNextToken(this.styleInput, this.stringBuilder);
        if ((str == null) || ("}".equals(str))) {}
        for (i = 1;; i = 0)
        {
          j = i;
          localObject = str;
          if (i != 0) {
            break;
          }
          this.styleInput.setPosition(k);
          parseStyleDeclaration(this.styleInput, paramParsableByteArray, this.stringBuilder);
          j = i;
          localObject = str;
          break;
        }
      }
      if (!"}".equals(localObject)) {
        paramParsableByteArray = null;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/text/webvtt/CssParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */