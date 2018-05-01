package org.telegram.messenger.exoplayer2.text.webvtt;

import android.text.Layout.Alignment;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.telegram.messenger.exoplayer2.util.Util;

public final class WebvttCssStyle
{
  public static final int FONT_SIZE_UNIT_EM = 2;
  public static final int FONT_SIZE_UNIT_PERCENT = 3;
  public static final int FONT_SIZE_UNIT_PIXEL = 1;
  private static final int OFF = 0;
  private static final int ON = 1;
  public static final int STYLE_BOLD = 1;
  public static final int STYLE_BOLD_ITALIC = 3;
  public static final int STYLE_ITALIC = 2;
  public static final int STYLE_NORMAL = 0;
  public static final int UNSPECIFIED = -1;
  private int backgroundColor;
  private int bold;
  private int fontColor;
  private String fontFamily;
  private float fontSize;
  private int fontSizeUnit;
  private boolean hasBackgroundColor;
  private boolean hasFontColor;
  private int italic;
  private int linethrough;
  private List<String> targetClasses;
  private String targetId;
  private String targetTag;
  private String targetVoice;
  private Layout.Alignment textAlign;
  private int underline;
  
  public WebvttCssStyle()
  {
    reset();
  }
  
  private static int updateScoreForMatch(int paramInt1, String paramString1, String paramString2, int paramInt2)
  {
    int i = -1;
    if ((paramString1.isEmpty()) || (paramInt1 == -1)) {
      i = paramInt1;
    }
    for (;;)
    {
      return i;
      if (paramString1.equals(paramString2)) {
        i = paramInt1 + paramInt2;
      }
    }
  }
  
  public void cascadeFrom(WebvttCssStyle paramWebvttCssStyle)
  {
    if (paramWebvttCssStyle.hasFontColor) {
      setFontColor(paramWebvttCssStyle.fontColor);
    }
    if (paramWebvttCssStyle.bold != -1) {
      this.bold = paramWebvttCssStyle.bold;
    }
    if (paramWebvttCssStyle.italic != -1) {
      this.italic = paramWebvttCssStyle.italic;
    }
    if (paramWebvttCssStyle.fontFamily != null) {
      this.fontFamily = paramWebvttCssStyle.fontFamily;
    }
    if (this.linethrough == -1) {
      this.linethrough = paramWebvttCssStyle.linethrough;
    }
    if (this.underline == -1) {
      this.underline = paramWebvttCssStyle.underline;
    }
    if (this.textAlign == null) {
      this.textAlign = paramWebvttCssStyle.textAlign;
    }
    if (this.fontSizeUnit == -1)
    {
      this.fontSizeUnit = paramWebvttCssStyle.fontSizeUnit;
      this.fontSize = paramWebvttCssStyle.fontSize;
    }
    if (paramWebvttCssStyle.hasBackgroundColor) {
      setBackgroundColor(paramWebvttCssStyle.backgroundColor);
    }
  }
  
  public int getBackgroundColor()
  {
    if (!this.hasBackgroundColor) {
      throw new IllegalStateException("Background color not defined.");
    }
    return this.backgroundColor;
  }
  
  public int getFontColor()
  {
    if (!this.hasFontColor) {
      throw new IllegalStateException("Font color not defined");
    }
    return this.fontColor;
  }
  
  public String getFontFamily()
  {
    return this.fontFamily;
  }
  
  public float getFontSize()
  {
    return this.fontSize;
  }
  
  public int getFontSizeUnit()
  {
    return this.fontSizeUnit;
  }
  
  public int getSpecificityScore(String paramString1, String paramString2, String[] paramArrayOfString, String paramString3)
  {
    int i = 0;
    int j;
    if ((this.targetId.isEmpty()) && (this.targetTag.isEmpty()) && (this.targetClasses.isEmpty()) && (this.targetVoice.isEmpty()))
    {
      j = i;
      if (paramString2.isEmpty()) {
        j = 1;
      }
    }
    for (;;)
    {
      return j;
      int k = updateScoreForMatch(updateScoreForMatch(updateScoreForMatch(0, this.targetId, paramString1, NUM), this.targetTag, paramString2, 2), this.targetVoice, paramString3, 4);
      j = i;
      if (k != -1)
      {
        j = i;
        if (Arrays.asList(paramArrayOfString).containsAll(this.targetClasses)) {
          j = k + this.targetClasses.size() * 4;
        }
      }
    }
  }
  
  public int getStyle()
  {
    int i = 0;
    int j = -1;
    if ((this.bold == -1) && (this.italic == -1)) {
      return j;
    }
    if (this.bold == 1) {}
    for (j = 1;; j = 0)
    {
      if (this.italic == 1) {
        i = 2;
      }
      j |= i;
      break;
    }
  }
  
  public Layout.Alignment getTextAlign()
  {
    return this.textAlign;
  }
  
  public boolean hasBackgroundColor()
  {
    return this.hasBackgroundColor;
  }
  
  public boolean hasFontColor()
  {
    return this.hasFontColor;
  }
  
  public boolean isLinethrough()
  {
    boolean bool = true;
    if (this.linethrough == 1) {}
    for (;;)
    {
      return bool;
      bool = false;
    }
  }
  
  public boolean isUnderline()
  {
    boolean bool = true;
    if (this.underline == 1) {}
    for (;;)
    {
      return bool;
      bool = false;
    }
  }
  
  public void reset()
  {
    this.targetId = "";
    this.targetTag = "";
    this.targetClasses = Collections.emptyList();
    this.targetVoice = "";
    this.fontFamily = null;
    this.hasFontColor = false;
    this.hasBackgroundColor = false;
    this.linethrough = -1;
    this.underline = -1;
    this.bold = -1;
    this.italic = -1;
    this.fontSizeUnit = -1;
    this.textAlign = null;
  }
  
  public WebvttCssStyle setBackgroundColor(int paramInt)
  {
    this.backgroundColor = paramInt;
    this.hasBackgroundColor = true;
    return this;
  }
  
  public WebvttCssStyle setBold(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      this.bold = i;
      return this;
    }
  }
  
  public WebvttCssStyle setFontColor(int paramInt)
  {
    this.fontColor = paramInt;
    this.hasFontColor = true;
    return this;
  }
  
  public WebvttCssStyle setFontFamily(String paramString)
  {
    this.fontFamily = Util.toLowerInvariant(paramString);
    return this;
  }
  
  public WebvttCssStyle setFontSize(float paramFloat)
  {
    this.fontSize = paramFloat;
    return this;
  }
  
  public WebvttCssStyle setFontSizeUnit(short paramShort)
  {
    this.fontSizeUnit = paramShort;
    return this;
  }
  
  public WebvttCssStyle setItalic(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      this.italic = i;
      return this;
    }
  }
  
  public WebvttCssStyle setLinethrough(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      this.linethrough = i;
      return this;
    }
  }
  
  public void setTargetClasses(String[] paramArrayOfString)
  {
    this.targetClasses = Arrays.asList(paramArrayOfString);
  }
  
  public void setTargetId(String paramString)
  {
    this.targetId = paramString;
  }
  
  public void setTargetTagName(String paramString)
  {
    this.targetTag = paramString;
  }
  
  public void setTargetVoice(String paramString)
  {
    this.targetVoice = paramString;
  }
  
  public WebvttCssStyle setTextAlign(Layout.Alignment paramAlignment)
  {
    this.textAlign = paramAlignment;
    return this;
  }
  
  public WebvttCssStyle setUnderline(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      this.underline = i;
      return this;
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface FontSizeUnit {}
  
  @Retention(RetentionPolicy.SOURCE)
  private static @interface OptionalBoolean {}
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface StyleFlags {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/text/webvtt/WebvttCssStyle.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */