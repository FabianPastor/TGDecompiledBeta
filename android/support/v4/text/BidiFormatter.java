package android.support.v4.text;

import android.text.SpannableStringBuilder;
import java.util.Locale;

public final class BidiFormatter
{
  private static final BidiFormatter DEFAULT_LTR_INSTANCE = new BidiFormatter(false, 2, DEFAULT_TEXT_DIRECTION_HEURISTIC);
  private static final BidiFormatter DEFAULT_RTL_INSTANCE = new BidiFormatter(true, 2, DEFAULT_TEXT_DIRECTION_HEURISTIC);
  private static TextDirectionHeuristicCompat DEFAULT_TEXT_DIRECTION_HEURISTIC = TextDirectionHeuristicsCompat.FIRSTSTRONG_LTR;
  private static final String LRM_STRING = Character.toString('‎');
  private static final String RLM_STRING = Character.toString('‏');
  private final TextDirectionHeuristicCompat mDefaultTextDirectionHeuristicCompat;
  private final int mFlags;
  private final boolean mIsRtlContext;
  
  private BidiFormatter(boolean paramBoolean, int paramInt, TextDirectionHeuristicCompat paramTextDirectionHeuristicCompat)
  {
    this.mIsRtlContext = paramBoolean;
    this.mFlags = paramInt;
    this.mDefaultTextDirectionHeuristicCompat = paramTextDirectionHeuristicCompat;
  }
  
  private static int getEntryDir(CharSequence paramCharSequence)
  {
    return new DirectionalityEstimator(paramCharSequence, false).getEntryDir();
  }
  
  private static int getExitDir(CharSequence paramCharSequence)
  {
    return new DirectionalityEstimator(paramCharSequence, false).getExitDir();
  }
  
  public static BidiFormatter getInstance()
  {
    return new Builder().build();
  }
  
  private static boolean isRtlLocale(Locale paramLocale)
  {
    boolean bool = true;
    if (TextUtilsCompat.getLayoutDirectionFromLocale(paramLocale) == 1) {}
    for (;;)
    {
      return bool;
      bool = false;
    }
  }
  
  private String markAfter(CharSequence paramCharSequence, TextDirectionHeuristicCompat paramTextDirectionHeuristicCompat)
  {
    boolean bool = paramTextDirectionHeuristicCompat.isRtl(paramCharSequence, 0, paramCharSequence.length());
    if ((!this.mIsRtlContext) && ((bool) || (getExitDir(paramCharSequence) == 1))) {
      paramCharSequence = LRM_STRING;
    }
    for (;;)
    {
      return paramCharSequence;
      if ((this.mIsRtlContext) && ((!bool) || (getExitDir(paramCharSequence) == -1))) {
        paramCharSequence = RLM_STRING;
      } else {
        paramCharSequence = "";
      }
    }
  }
  
  private String markBefore(CharSequence paramCharSequence, TextDirectionHeuristicCompat paramTextDirectionHeuristicCompat)
  {
    boolean bool = paramTextDirectionHeuristicCompat.isRtl(paramCharSequence, 0, paramCharSequence.length());
    if ((!this.mIsRtlContext) && ((bool) || (getEntryDir(paramCharSequence) == 1))) {
      paramCharSequence = LRM_STRING;
    }
    for (;;)
    {
      return paramCharSequence;
      if ((this.mIsRtlContext) && ((!bool) || (getEntryDir(paramCharSequence) == -1))) {
        paramCharSequence = RLM_STRING;
      } else {
        paramCharSequence = "";
      }
    }
  }
  
  public boolean getStereoReset()
  {
    if ((this.mFlags & 0x2) != 0) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public CharSequence unicodeWrap(CharSequence paramCharSequence)
  {
    return unicodeWrap(paramCharSequence, this.mDefaultTextDirectionHeuristicCompat, true);
  }
  
  public CharSequence unicodeWrap(CharSequence paramCharSequence, TextDirectionHeuristicCompat paramTextDirectionHeuristicCompat, boolean paramBoolean)
  {
    if (paramCharSequence == null) {
      paramTextDirectionHeuristicCompat = null;
    }
    boolean bool;
    SpannableStringBuilder localSpannableStringBuilder;
    label53:
    char c1;
    char c2;
    label88:
    label112:
    do
    {
      return paramTextDirectionHeuristicCompat;
      bool = paramTextDirectionHeuristicCompat.isRtl(paramCharSequence, 0, paramCharSequence.length());
      localSpannableStringBuilder = new SpannableStringBuilder();
      if ((getStereoReset()) && (paramBoolean))
      {
        if (!bool) {
          break;
        }
        paramTextDirectionHeuristicCompat = TextDirectionHeuristicsCompat.RTL;
        localSpannableStringBuilder.append(markBefore(paramCharSequence, paramTextDirectionHeuristicCompat));
      }
      if (bool == this.mIsRtlContext) {
        break label165;
      }
      if (!bool) {
        break label153;
      }
      c1 = '‫';
      c2 = c1;
      localSpannableStringBuilder.append(c2);
      localSpannableStringBuilder.append(paramCharSequence);
      localSpannableStringBuilder.append('‬');
      paramTextDirectionHeuristicCompat = localSpannableStringBuilder;
    } while (!paramBoolean);
    if (bool) {}
    for (paramTextDirectionHeuristicCompat = TextDirectionHeuristicsCompat.RTL;; paramTextDirectionHeuristicCompat = TextDirectionHeuristicsCompat.LTR)
    {
      localSpannableStringBuilder.append(markAfter(paramCharSequence, paramTextDirectionHeuristicCompat));
      paramTextDirectionHeuristicCompat = localSpannableStringBuilder;
      break;
      paramTextDirectionHeuristicCompat = TextDirectionHeuristicsCompat.LTR;
      break label53;
      label153:
      c1 = '‪';
      c2 = c1;
      break label88;
      label165:
      localSpannableStringBuilder.append(paramCharSequence);
      break label112;
    }
  }
  
  public static final class Builder
  {
    private int mFlags;
    private boolean mIsRtlContext;
    private TextDirectionHeuristicCompat mTextDirectionHeuristicCompat;
    
    public Builder()
    {
      initialize(BidiFormatter.isRtlLocale(Locale.getDefault()));
    }
    
    private static BidiFormatter getDefaultInstanceFromContext(boolean paramBoolean)
    {
      if (paramBoolean) {}
      for (BidiFormatter localBidiFormatter = BidiFormatter.DEFAULT_RTL_INSTANCE;; localBidiFormatter = BidiFormatter.DEFAULT_LTR_INSTANCE) {
        return localBidiFormatter;
      }
    }
    
    private void initialize(boolean paramBoolean)
    {
      this.mIsRtlContext = paramBoolean;
      this.mTextDirectionHeuristicCompat = BidiFormatter.DEFAULT_TEXT_DIRECTION_HEURISTIC;
      this.mFlags = 2;
    }
    
    public BidiFormatter build()
    {
      if ((this.mFlags == 2) && (this.mTextDirectionHeuristicCompat == BidiFormatter.DEFAULT_TEXT_DIRECTION_HEURISTIC)) {}
      for (BidiFormatter localBidiFormatter = getDefaultInstanceFromContext(this.mIsRtlContext);; localBidiFormatter = new BidiFormatter(this.mIsRtlContext, this.mFlags, this.mTextDirectionHeuristicCompat, null)) {
        return localBidiFormatter;
      }
    }
  }
  
  private static class DirectionalityEstimator
  {
    private static final byte[] DIR_TYPE_CACHE = new byte['܀'];
    private int charIndex;
    private final boolean isHtml;
    private char lastChar;
    private final int length;
    private final CharSequence text;
    
    static
    {
      for (int i = 0; i < 1792; i++) {
        DIR_TYPE_CACHE[i] = Character.getDirectionality(i);
      }
    }
    
    DirectionalityEstimator(CharSequence paramCharSequence, boolean paramBoolean)
    {
      this.text = paramCharSequence;
      this.isHtml = paramBoolean;
      this.length = paramCharSequence.length();
    }
    
    private static byte getCachedDirectionality(char paramChar)
    {
      byte b1;
      if (paramChar < '܀') {
        b1 = DIR_TYPE_CACHE[paramChar];
      }
      for (byte b2 = b1;; b2 = b1)
      {
        return b2;
        b1 = Character.getDirectionality(paramChar);
      }
    }
    
    private byte skipEntityBackward()
    {
      int i = this.charIndex;
      if (this.charIndex > 0)
      {
        CharSequence localCharSequence = this.text;
        int j = this.charIndex - 1;
        this.charIndex = j;
        this.lastChar = localCharSequence.charAt(j);
        if (this.lastChar == '&') {
          i = 12;
        }
      }
      for (int k = i;; k = i)
      {
        return k;
        if (this.lastChar != ';') {
          break;
        }
        this.charIndex = i;
        this.lastChar = ((char)59);
        i = 13;
      }
    }
    
    private byte skipEntityForward()
    {
      int i;
      do
      {
        if (this.charIndex >= this.length) {
          break;
        }
        CharSequence localCharSequence = this.text;
        i = this.charIndex;
        this.charIndex = (i + 1);
        i = localCharSequence.charAt(i);
        this.lastChar = ((char)i);
      } while (i != 59);
      return 12;
    }
    
    private byte skipTagBackward()
    {
      int i = this.charIndex;
      label161:
      for (;;)
      {
        CharSequence localCharSequence;
        int j;
        if (this.charIndex > 0)
        {
          localCharSequence = this.text;
          j = this.charIndex - 1;
          this.charIndex = j;
          this.lastChar = localCharSequence.charAt(j);
          if (this.lastChar == '<') {
            i = 12;
          }
        }
        for (int k = i;; k = i)
        {
          return k;
          if (this.lastChar != '>') {
            break;
          }
          this.charIndex = i;
          this.lastChar = ((char)62);
          i = 13;
        }
        if ((this.lastChar == '"') || (this.lastChar == '\''))
        {
          j = this.lastChar;
          for (;;)
          {
            if (this.charIndex <= 0) {
              break label161;
            }
            localCharSequence = this.text;
            int m = this.charIndex - 1;
            this.charIndex = m;
            m = localCharSequence.charAt(m);
            this.lastChar = ((char)m);
            if (m == j) {
              break;
            }
          }
        }
      }
    }
    
    private byte skipTagForward()
    {
      int i = this.charIndex;
      CharSequence localCharSequence;
      int j;
      if (this.charIndex < this.length)
      {
        localCharSequence = this.text;
        j = this.charIndex;
        this.charIndex = (j + 1);
        this.lastChar = localCharSequence.charAt(j);
        if (this.lastChar == '>') {
          i = 12;
        }
      }
      for (int k = i;; k = i)
      {
        return k;
        if ((this.lastChar != '"') && (this.lastChar != '\'')) {
          break;
        }
        j = this.lastChar;
        for (;;)
        {
          if (this.charIndex >= this.length) {
            break label139;
          }
          localCharSequence = this.text;
          int m = this.charIndex;
          this.charIndex = (m + 1);
          m = localCharSequence.charAt(m);
          this.lastChar = ((char)m);
          if (m == j) {
            break;
          }
        }
        label139:
        break;
        this.charIndex = i;
        this.lastChar = ((char)60);
        i = 13;
      }
    }
    
    byte dirTypeBackward()
    {
      this.lastChar = this.text.charAt(this.charIndex - 1);
      int i;
      int j;
      if (Character.isLowSurrogate(this.lastChar))
      {
        i = Character.codePointBefore(this.text, this.charIndex);
        this.charIndex -= Character.charCount(i);
        i = Character.getDirectionality(i);
        j = i;
      }
      for (;;)
      {
        return j;
        this.charIndex -= 1;
        i = getCachedDirectionality(this.lastChar);
        j = i;
        if (this.isHtml) {
          if (this.lastChar == '>')
          {
            i = skipTagBackward();
            j = i;
          }
          else
          {
            j = i;
            if (this.lastChar == ';')
            {
              i = skipEntityBackward();
              j = i;
            }
          }
        }
      }
    }
    
    byte dirTypeForward()
    {
      this.lastChar = this.text.charAt(this.charIndex);
      int i;
      int j;
      if (Character.isHighSurrogate(this.lastChar))
      {
        i = Character.codePointAt(this.text, this.charIndex);
        this.charIndex += Character.charCount(i);
        i = Character.getDirectionality(i);
        j = i;
      }
      for (;;)
      {
        return j;
        this.charIndex += 1;
        i = getCachedDirectionality(this.lastChar);
        j = i;
        if (this.isHtml) {
          if (this.lastChar == '<')
          {
            i = skipTagForward();
            j = i;
          }
          else
          {
            j = i;
            if (this.lastChar == '&')
            {
              i = skipEntityForward();
              j = i;
            }
          }
        }
      }
    }
    
    int getEntryDir()
    {
      this.charIndex = 0;
      int i = 0;
      int j = 0;
      int k = 0;
      int m;
      for (;;)
      {
        if ((this.charIndex < this.length) && (k == 0)) {
          switch (dirTypeForward())
          {
          case 9: 
          case 3: 
          case 4: 
          case 5: 
          case 6: 
          case 7: 
          case 8: 
          case 10: 
          case 11: 
          case 12: 
          case 13: 
          default: 
            k = i;
            break;
          case 14: 
          case 15: 
            i++;
            j = -1;
            break;
          case 16: 
          case 17: 
            i++;
            j = 1;
            break;
          case 18: 
            i--;
            j = 0;
            break;
          case 0: 
            if (i == 0) {
              m = -1;
            }
            break;
          }
        }
      }
      for (;;)
      {
        return m;
        k = i;
        break;
        if (i == 0)
        {
          m = 1;
        }
        else
        {
          k = i;
          break;
          if (k == 0)
          {
            m = 0;
          }
          else
          {
            m = j;
            if (j == 0)
            {
              for (;;)
              {
                if (this.charIndex <= 0) {
                  break label283;
                }
                switch (dirTypeBackward())
                {
                default: 
                  break;
                case 14: 
                case 15: 
                  if (k == i)
                  {
                    m = -1;
                    break;
                  }
                  i--;
                  break;
                case 16: 
                case 17: 
                  if (k == i)
                  {
                    m = 1;
                    break;
                  }
                  i--;
                  break;
                case 18: 
                  i++;
                }
              }
              label283:
              m = 0;
            }
          }
        }
      }
    }
    
    int getExitDir()
    {
      int i = -1;
      this.charIndex = this.length;
      int j = 0;
      int k = 0;
      int m;
      for (;;)
      {
        if (this.charIndex > 0) {
          switch (dirTypeBackward())
          {
          case 9: 
          case 3: 
          case 4: 
          case 5: 
          case 6: 
          case 7: 
          case 8: 
          case 10: 
          case 11: 
          case 12: 
          case 13: 
          default: 
            if (k == 0) {
              k = j;
            }
            break;
          case 0: 
            if (j == 0) {
              m = i;
            }
            break;
          }
        }
      }
      for (;;)
      {
        return m;
        if (k != 0) {
          break;
        }
        k = j;
        break;
        m = i;
        if (k != j)
        {
          j--;
          break;
          if (j == 0)
          {
            m = 1;
          }
          else
          {
            if (k != 0) {
              break;
            }
            k = j;
            break;
            if (k == j)
            {
              m = 1;
            }
            else
            {
              j--;
              break;
              j++;
              break;
              m = 0;
            }
          }
        }
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v4/text/BidiFormatter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */