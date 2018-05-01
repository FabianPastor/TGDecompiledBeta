package org.telegram.messenger.exoplayer2.text.cea;

import android.text.Layout.Alignment;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.exoplayer2.text.Cue;
import org.telegram.messenger.exoplayer2.text.Subtitle;
import org.telegram.messenger.exoplayer2.text.SubtitleInputBuffer;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

public final class Cea608Decoder
  extends CeaDecoder
{
  private static final int[] BASIC_CHARACTER_SET = { 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 225, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 233, 93, 237, 243, 250, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 231, 247, 209, 241, 9632 };
  private static final int CC_FIELD_FLAG = 1;
  private static final byte CC_IMPLICIT_DATA_HEADER = -4;
  private static final int CC_MODE_PAINT_ON = 3;
  private static final int CC_MODE_POP_ON = 2;
  private static final int CC_MODE_ROLL_UP = 1;
  private static final int CC_MODE_UNKNOWN = 0;
  private static final int CC_TYPE_FLAG = 2;
  private static final int CC_VALID_608_ID = 4;
  private static final int CC_VALID_FLAG = 4;
  private static final int[] COLORS;
  private static final int[] COLUMN_INDICES;
  private static final byte CTRL_BACKSPACE = 33;
  private static final byte CTRL_CARRIAGE_RETURN = 45;
  private static final byte CTRL_DELETE_TO_END_OF_ROW = 36;
  private static final byte CTRL_END_OF_CAPTION = 47;
  private static final byte CTRL_ERASE_DISPLAYED_MEMORY = 44;
  private static final byte CTRL_ERASE_NON_DISPLAYED_MEMORY = 46;
  private static final byte CTRL_RESUME_CAPTION_LOADING = 32;
  private static final byte CTRL_RESUME_DIRECT_CAPTIONING = 41;
  private static final byte CTRL_ROLL_UP_CAPTIONS_2_ROWS = 37;
  private static final byte CTRL_ROLL_UP_CAPTIONS_3_ROWS = 38;
  private static final byte CTRL_ROLL_UP_CAPTIONS_4_ROWS = 39;
  private static final int DEFAULT_CAPTIONS_ROW_COUNT = 4;
  private static final int NTSC_CC_FIELD_1 = 0;
  private static final int NTSC_CC_FIELD_2 = 1;
  private static final int[] ROW_INDICES = { 11, 1, 3, 12, 14, 5, 7, 9 };
  private static final int[] SPECIAL_CHARACTER_SET = { 174, 176, 189, 191, 8482, 162, 163, 9834, 224, 32, 232, 226, 234, 238, 244, 251 };
  private static final int[] SPECIAL_ES_FR_CHARACTER_SET = { 193, 201, 211, 218, 220, 252, 8216, 161, 42, 39, 8212, 169, 8480, 8226, 8220, 8221, 192, 194, 199, 200, 202, 203, 235, 206, 207, 239, 212, 217, 249, 219, 171, 187 };
  private static final int[] SPECIAL_PT_DE_CHARACTER_SET = { 195, 227, 205, 204, 236, 210, 242, 213, 245, 123, 125, 92, 94, 95, 124, 126, 196, 228, 214, 246, 223, 165, 164, 9474, 197, 229, 216, 248, 9484, 9488, 9492, 9496 };
  private int captionMode;
  private int captionRowCount;
  private final ParsableByteArray ccData = new ParsableByteArray();
  private final ArrayList<CueBuilder> cueBuilders = new ArrayList();
  private List<Cue> cues;
  private CueBuilder currentCueBuilder = new CueBuilder(0, 4);
  private List<Cue> lastCues;
  private final int packetLength;
  private byte repeatableControlCc1;
  private byte repeatableControlCc2;
  private boolean repeatableControlSet;
  private final int selectedField;
  
  static
  {
    COLUMN_INDICES = new int[] { 0, 4, 8, 12, 16, 20, 24, 28 };
    COLORS = new int[] { -1, -16711936, -16776961, -16711681, -65536, 65280, -65281 };
  }
  
  public Cea608Decoder(String paramString, int paramInt)
  {
    int i;
    if ("application/x-mp4-cea-608".equals(paramString))
    {
      i = 2;
      this.packetLength = i;
      switch (paramInt)
      {
      }
    }
    for (this.selectedField = 1;; this.selectedField = 2)
    {
      setCaptionMode(0);
      resetCueBuilders();
      return;
      i = 3;
      break;
    }
  }
  
  private static char getChar(byte paramByte)
  {
    return (char)BASIC_CHARACTER_SET[((paramByte & 0x7F) - 32)];
  }
  
  private List<Cue> getDisplayCues()
  {
    ArrayList localArrayList = new ArrayList();
    for (int i = 0; i < this.cueBuilders.size(); i++)
    {
      Cue localCue = ((CueBuilder)this.cueBuilders.get(i)).build();
      if (localCue != null) {
        localArrayList.add(localCue);
      }
    }
    return localArrayList;
  }
  
  private static char getExtendedEsFrChar(byte paramByte)
  {
    return (char)SPECIAL_ES_FR_CHARACTER_SET[(paramByte & 0x1F)];
  }
  
  private static char getExtendedPtDeChar(byte paramByte)
  {
    return (char)SPECIAL_PT_DE_CHARACTER_SET[(paramByte & 0x1F)];
  }
  
  private static char getSpecialChar(byte paramByte)
  {
    return (char)SPECIAL_CHARACTER_SET[(paramByte & 0xF)];
  }
  
  private boolean handleCtrl(byte paramByte1, byte paramByte2)
  {
    boolean bool1 = isRepeatable(paramByte1);
    boolean bool2;
    if (bool1) {
      if ((this.repeatableControlSet) && (this.repeatableControlCc1 == paramByte1) && (this.repeatableControlCc2 == paramByte2))
      {
        this.repeatableControlSet = false;
        bool2 = true;
      }
    }
    for (;;)
    {
      return bool2;
      this.repeatableControlSet = true;
      this.repeatableControlCc1 = paramByte1;
      this.repeatableControlCc2 = paramByte2;
      if (isMidrowCtrlCode(paramByte1, paramByte2))
      {
        handleMidrowCtrl(paramByte2);
        bool2 = bool1;
      }
      else if (isPreambleAddressCode(paramByte1, paramByte2))
      {
        handlePreambleAddressCode(paramByte1, paramByte2);
        bool2 = bool1;
      }
      else if (isTabCtrlCode(paramByte1, paramByte2))
      {
        this.currentCueBuilder.setTab(paramByte2 - 32);
        bool2 = bool1;
      }
      else
      {
        bool2 = bool1;
        if (isMiscCode(paramByte1, paramByte2))
        {
          handleMiscCode(paramByte2);
          bool2 = bool1;
        }
      }
    }
  }
  
  private void handleMidrowCtrl(byte paramByte)
  {
    boolean bool;
    if ((paramByte & 0x1) == 1)
    {
      bool = true;
      this.currentCueBuilder.setUnderline(bool);
      paramByte = paramByte >> 1 & 0xF;
      if (paramByte != 7) {
        break label68;
      }
      this.currentCueBuilder.setMidrowStyle(new StyleSpan(2), 2);
      this.currentCueBuilder.setMidrowStyle(new ForegroundColorSpan(-1), 1);
    }
    for (;;)
    {
      return;
      bool = false;
      break;
      label68:
      this.currentCueBuilder.setMidrowStyle(new ForegroundColorSpan(COLORS[paramByte]), 1);
    }
  }
  
  private void handleMiscCode(byte paramByte)
  {
    switch (paramByte)
    {
    case 33: 
    case 34: 
    case 35: 
    case 36: 
    case 40: 
    default: 
      if (this.captionMode != 0) {
        break;
      }
    }
    for (;;)
    {
      return;
      setCaptionMode(1);
      setCaptionRowCount(2);
      continue;
      setCaptionMode(1);
      setCaptionRowCount(3);
      continue;
      setCaptionMode(1);
      setCaptionRowCount(4);
      continue;
      setCaptionMode(2);
      continue;
      setCaptionMode(3);
      continue;
      switch (paramByte)
      {
      case 36: 
      default: 
        break;
      case 33: 
        this.currentCueBuilder.backspace();
        break;
      case 44: 
        this.cues = null;
        if ((this.captionMode == 1) || (this.captionMode == 3)) {
          resetCueBuilders();
        }
        break;
      case 46: 
        resetCueBuilders();
        break;
      case 47: 
        this.cues = getDisplayCues();
        resetCueBuilders();
        break;
      case 45: 
        if ((this.captionMode == 1) && (!this.currentCueBuilder.isEmpty())) {
          this.currentCueBuilder.rollUp();
        }
        break;
      }
    }
  }
  
  private void handlePreambleAddressCode(byte paramByte1, byte paramByte2)
  {
    byte b = ROW_INDICES[(paramByte1 & 0x7)];
    int i;
    if ((paramByte2 & 0x20) != 0)
    {
      i = 1;
      paramByte1 = b;
      if (i != 0) {
        paramByte1 = b + 1;
      }
      if (paramByte1 != this.currentCueBuilder.getRow())
      {
        if ((this.captionMode != 1) && (!this.currentCueBuilder.isEmpty()))
        {
          this.currentCueBuilder = new CueBuilder(this.captionMode, this.captionRowCount);
          this.cueBuilders.add(this.currentCueBuilder);
        }
        this.currentCueBuilder.setRow(paramByte1);
      }
      if ((paramByte2 & 0x1) == 1) {
        this.currentCueBuilder.setPreambleStyle(new UnderlineSpan());
      }
      paramByte1 = paramByte2 >> 1 & 0xF;
      if (paramByte1 > 7) {
        break label197;
      }
      if (paramByte1 != 7) {
        break label175;
      }
      this.currentCueBuilder.setPreambleStyle(new StyleSpan(2));
      this.currentCueBuilder.setPreambleStyle(new ForegroundColorSpan(-1));
    }
    for (;;)
    {
      return;
      i = 0;
      break;
      label175:
      this.currentCueBuilder.setPreambleStyle(new ForegroundColorSpan(COLORS[paramByte1]));
      continue;
      label197:
      this.currentCueBuilder.setIndent(COLUMN_INDICES[(paramByte1 & 0x7)]);
    }
  }
  
  private static boolean isMidrowCtrlCode(byte paramByte1, byte paramByte2)
  {
    if (((paramByte1 & 0xF7) == 17) && ((paramByte2 & 0xF0) == 32)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  private static boolean isMiscCode(byte paramByte1, byte paramByte2)
  {
    if (((paramByte1 & 0xF7) == 20) && ((paramByte2 & 0xF0) == 32)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  private static boolean isPreambleAddressCode(byte paramByte1, byte paramByte2)
  {
    if (((paramByte1 & 0xF0) == 16) && ((paramByte2 & 0xC0) == 64)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  private static boolean isRepeatable(byte paramByte)
  {
    if ((paramByte & 0xF0) == 16) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  private static boolean isTabCtrlCode(byte paramByte1, byte paramByte2)
  {
    if (((paramByte1 & 0xF7) == 23) && (paramByte2 >= 33) && (paramByte2 <= 35)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  private void resetCueBuilders()
  {
    this.currentCueBuilder.reset(this.captionMode);
    this.cueBuilders.clear();
    this.cueBuilders.add(this.currentCueBuilder);
  }
  
  private void setCaptionMode(int paramInt)
  {
    if (this.captionMode == paramInt) {}
    for (;;)
    {
      return;
      int i = this.captionMode;
      this.captionMode = paramInt;
      resetCueBuilders();
      if ((i == 3) || (paramInt == 1) || (paramInt == 0)) {
        this.cues = null;
      }
    }
  }
  
  private void setCaptionRowCount(int paramInt)
  {
    this.captionRowCount = paramInt;
    this.currentCueBuilder.setCaptionRowCount(paramInt);
  }
  
  protected Subtitle createSubtitle()
  {
    this.lastCues = this.cues;
    return new CeaSubtitle(this.cues);
  }
  
  protected void decode(SubtitleInputBuffer paramSubtitleInputBuffer)
  {
    this.ccData.reset(paramSubtitleInputBuffer.data.array(), paramSubtitleInputBuffer.data.limit());
    int i = 0;
    boolean bool = false;
    while (this.ccData.bytesLeft() >= this.packetLength)
    {
      if (this.packetLength == 2) {}
      int k;
      int m;
      for (int j = -4;; j = (byte)this.ccData.readUnsignedByte())
      {
        k = (byte)(this.ccData.readUnsignedByte() & 0x7F);
        m = (byte)(this.ccData.readUnsignedByte() & 0x7F);
        if (((j & 0x6) != 4) || ((this.selectedField == 1) && ((j & 0x1) != 0)) || ((this.selectedField == 2) && ((j & 0x1) != 1)) || ((k == 0) && (m == 0))) {
          break;
        }
        j = 1;
        if (((k & 0xF7) != 17) || ((m & 0xF0) != 48)) {
          break label183;
        }
        this.currentCueBuilder.append(getSpecialChar(m));
        i = j;
        break;
      }
      label183:
      if (((k & 0xF6) == 18) && ((m & 0xE0) == 32))
      {
        this.currentCueBuilder.backspace();
        if ((k & 0x1) == 0)
        {
          this.currentCueBuilder.append(getExtendedEsFrChar(m));
          i = j;
        }
        else
        {
          this.currentCueBuilder.append(getExtendedPtDeChar(m));
          i = j;
        }
      }
      else if ((k & 0xE0) == 0)
      {
        bool = handleCtrl(k, m);
        i = j;
      }
      else
      {
        this.currentCueBuilder.append(getChar(k));
        i = j;
        if ((m & 0xE0) != 0)
        {
          this.currentCueBuilder.append(getChar(m));
          i = j;
        }
      }
    }
    if (i != 0)
    {
      if (!bool) {
        this.repeatableControlSet = false;
      }
      if ((this.captionMode == 1) || (this.captionMode == 3)) {
        this.cues = getDisplayCues();
      }
    }
  }
  
  public void flush()
  {
    super.flush();
    this.cues = null;
    this.lastCues = null;
    setCaptionMode(0);
    setCaptionRowCount(4);
    resetCueBuilders();
    this.repeatableControlSet = false;
    this.repeatableControlCc1 = ((byte)0);
    this.repeatableControlCc2 = ((byte)0);
  }
  
  public String getName()
  {
    return "Cea608Decoder";
  }
  
  protected boolean isNewSubtitleDataAvailable()
  {
    if (this.cues != this.lastCues) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public void release() {}
  
  private static class CueBuilder
  {
    private static final int BASE_ROW = 15;
    private static final int POSITION_UNSET = -1;
    private static final int SCREEN_CHARWIDTH = 32;
    private int captionMode;
    private int captionRowCount;
    private final SpannableStringBuilder captionStringBuilder = new SpannableStringBuilder();
    private int indent;
    private final List<CueStyle> midrowStyles = new ArrayList();
    private final List<CharacterStyle> preambleStyles = new ArrayList();
    private final List<SpannableString> rolledUpCaptions = new ArrayList();
    private int row;
    private int tabOffset;
    private int underlineStartPosition;
    
    public CueBuilder(int paramInt1, int paramInt2)
    {
      reset(paramInt1);
      setCaptionRowCount(paramInt2);
    }
    
    public void append(char paramChar)
    {
      this.captionStringBuilder.append(paramChar);
    }
    
    public void backspace()
    {
      int i = this.captionStringBuilder.length();
      if (i > 0) {
        this.captionStringBuilder.delete(i - 1, i);
      }
    }
    
    public Cue build()
    {
      Object localObject = new SpannableStringBuilder();
      for (int i = 0; i < this.rolledUpCaptions.size(); i++)
      {
        ((SpannableStringBuilder)localObject).append((CharSequence)this.rolledUpCaptions.get(i));
        ((SpannableStringBuilder)localObject).append('\n');
      }
      ((SpannableStringBuilder)localObject).append(buildSpannableString());
      if (((SpannableStringBuilder)localObject).length() == 0)
      {
        localObject = null;
        return (Cue)localObject;
      }
      i = this.indent + this.tabOffset;
      int j = 32 - i - ((SpannableStringBuilder)localObject).length();
      int k = i - j;
      float f;
      if ((this.captionMode == 2) && ((Math.abs(k) < 3) || (j < 0)))
      {
        f = 0.5F;
        i = 1;
        label126:
        if ((this.captionMode != 1) && (this.row <= 7)) {
          break label233;
        }
        k = 2;
      }
      for (j = this.row - 15 - 2;; j = this.row)
      {
        localObject = new Cue((CharSequence)localObject, Layout.Alignment.ALIGN_NORMAL, j, 1, k, f, i, Float.MIN_VALUE);
        break;
        if ((this.captionMode == 2) && (k > 0))
        {
          f = 0.8F * ((32 - j) / 32.0F) + 0.1F;
          i = 2;
          break label126;
        }
        f = 0.8F * (i / 32.0F) + 0.1F;
        i = 0;
        break label126;
        label233:
        k = 0;
      }
    }
    
    public SpannableString buildSpannableString()
    {
      int i = this.captionStringBuilder.length();
      for (int j = 0; j < this.preambleStyles.size(); j++) {
        this.captionStringBuilder.setSpan(this.preambleStyles.get(j), 0, i, 33);
      }
      j = 0;
      if (j < this.midrowStyles.size())
      {
        CueStyle localCueStyle = (CueStyle)this.midrowStyles.get(j);
        if (j < this.midrowStyles.size() - localCueStyle.nextStyleIncrement) {}
        for (int k = ((CueStyle)this.midrowStyles.get(localCueStyle.nextStyleIncrement + j)).start;; k = i)
        {
          this.captionStringBuilder.setSpan(localCueStyle.style, localCueStyle.start, k, 33);
          j++;
          break;
        }
      }
      if (this.underlineStartPosition != -1) {
        this.captionStringBuilder.setSpan(new UnderlineSpan(), this.underlineStartPosition, i, 33);
      }
      return new SpannableString(this.captionStringBuilder);
    }
    
    public int getRow()
    {
      return this.row;
    }
    
    public boolean isEmpty()
    {
      if ((this.preambleStyles.isEmpty()) && (this.midrowStyles.isEmpty()) && (this.rolledUpCaptions.isEmpty()) && (this.captionStringBuilder.length() == 0)) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    public void reset(int paramInt)
    {
      this.captionMode = paramInt;
      this.preambleStyles.clear();
      this.midrowStyles.clear();
      this.rolledUpCaptions.clear();
      this.captionStringBuilder.clear();
      this.row = 15;
      this.indent = 0;
      this.tabOffset = 0;
      this.underlineStartPosition = -1;
    }
    
    public void rollUp()
    {
      this.rolledUpCaptions.add(buildSpannableString());
      this.captionStringBuilder.clear();
      this.preambleStyles.clear();
      this.midrowStyles.clear();
      this.underlineStartPosition = -1;
      int i = Math.min(this.captionRowCount, this.row);
      while (this.rolledUpCaptions.size() >= i) {
        this.rolledUpCaptions.remove(0);
      }
    }
    
    public void setCaptionRowCount(int paramInt)
    {
      this.captionRowCount = paramInt;
    }
    
    public void setIndent(int paramInt)
    {
      this.indent = paramInt;
    }
    
    public void setMidrowStyle(CharacterStyle paramCharacterStyle, int paramInt)
    {
      this.midrowStyles.add(new CueStyle(paramCharacterStyle, this.captionStringBuilder.length(), paramInt));
    }
    
    public void setPreambleStyle(CharacterStyle paramCharacterStyle)
    {
      this.preambleStyles.add(paramCharacterStyle);
    }
    
    public void setRow(int paramInt)
    {
      this.row = paramInt;
    }
    
    public void setTab(int paramInt)
    {
      this.tabOffset = paramInt;
    }
    
    public void setUnderline(boolean paramBoolean)
    {
      if (paramBoolean) {
        this.underlineStartPosition = this.captionStringBuilder.length();
      }
      for (;;)
      {
        return;
        if (this.underlineStartPosition != -1)
        {
          this.captionStringBuilder.setSpan(new UnderlineSpan(), this.underlineStartPosition, this.captionStringBuilder.length(), 33);
          this.underlineStartPosition = -1;
        }
      }
    }
    
    public String toString()
    {
      return this.captionStringBuilder.toString();
    }
    
    private static class CueStyle
    {
      public final int nextStyleIncrement;
      public final int start;
      public final CharacterStyle style;
      
      public CueStyle(CharacterStyle paramCharacterStyle, int paramInt1, int paramInt2)
      {
        this.style = paramCharacterStyle;
        this.start = paramInt1;
        this.nextStyleIncrement = paramInt2;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/text/cea/Cea608Decoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */