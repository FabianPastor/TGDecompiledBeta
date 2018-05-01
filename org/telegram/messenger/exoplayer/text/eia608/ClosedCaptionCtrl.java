package org.telegram.messenger.exoplayer.text.eia608;

final class ClosedCaptionCtrl
  extends ClosedCaption
{
  public static final byte BACKSPACE = 33;
  public static final byte CARRIAGE_RETURN = 45;
  public static final byte END_OF_CAPTION = 47;
  public static final byte ERASE_DISPLAYED_MEMORY = 44;
  public static final byte ERASE_NON_DISPLAYED_MEMORY = 46;
  public static final byte MID_ROW_CHAN_1 = 17;
  public static final byte MID_ROW_CHAN_2 = 25;
  public static final byte MISC_CHAN_1 = 20;
  public static final byte MISC_CHAN_2 = 28;
  public static final byte RESUME_CAPTION_LOADING = 32;
  public static final byte RESUME_DIRECT_CAPTIONING = 41;
  public static final byte ROLL_UP_CAPTIONS_2_ROWS = 37;
  public static final byte ROLL_UP_CAPTIONS_3_ROWS = 38;
  public static final byte ROLL_UP_CAPTIONS_4_ROWS = 39;
  public static final byte TAB_OFFSET_CHAN_1 = 23;
  public static final byte TAB_OFFSET_CHAN_2 = 31;
  public final byte cc1;
  public final byte cc2;
  
  protected ClosedCaptionCtrl(byte paramByte1, byte paramByte2)
  {
    super(0);
    this.cc1 = paramByte1;
    this.cc2 = paramByte2;
  }
  
  public boolean isMidRowCode()
  {
    return ((this.cc1 == 17) || (this.cc1 == 25)) && (this.cc2 >= 32) && (this.cc2 <= 47);
  }
  
  public boolean isMiscCode()
  {
    return ((this.cc1 == 20) || (this.cc1 == 28)) && (this.cc2 >= 32) && (this.cc2 <= 47);
  }
  
  public boolean isPreambleAddressCode()
  {
    return (this.cc1 >= 16) && (this.cc1 <= 31) && (this.cc2 >= 64) && (this.cc2 <= Byte.MAX_VALUE);
  }
  
  public boolean isRepeatable()
  {
    return (this.cc1 >= 16) && (this.cc1 <= 31);
  }
  
  public boolean isTabOffsetCode()
  {
    return ((this.cc1 == 23) || (this.cc1 == 31)) && (this.cc2 >= 33) && (this.cc2 <= 35);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/text/eia608/ClosedCaptionCtrl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */