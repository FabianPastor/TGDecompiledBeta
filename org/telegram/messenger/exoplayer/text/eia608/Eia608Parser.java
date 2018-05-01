package org.telegram.messenger.exoplayer.text.eia608;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.telegram.messenger.exoplayer.SampleHolder;
import org.telegram.messenger.exoplayer.util.ParsableBitArray;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;

public final class Eia608Parser
{
  private static final int[] BASIC_CHARACTER_SET = { 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 225, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 233, 93, 237, 243, 250, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 231, 247, 209, 241, 9632 };
  private static final int COUNTRY_CODE = 181;
  private static final int PAYLOAD_TYPE_CC = 4;
  private static final int PROVIDER_CODE = 49;
  private static final int[] SPECIAL_CHARACTER_SET = { 174, 176, 189, 191, 8482, 162, 163, 9834, 224, 32, 232, 226, 234, 238, 244, 251 };
  private static final int[] SPECIAL_ES_FR_CHARACTER_SET = { 193, 201, 211, 218, 220, 252, 8216, 161, 42, 39, 8212, 169, 8480, 8226, 8220, 8221, 192, 194, 199, 200, 202, 203, 235, 206, 207, 239, 212, 217, 249, 219, 171, 187 };
  private static final int[] SPECIAL_PT_DE_CHARACTER_SET = { 195, 227, 205, 204, 236, 210, 242, 213, 245, 123, 125, 92, 94, 95, 124, 126, 196, 228, 214, 246, 223, 165, 164, 9474, 197, 229, 216, 248, 9484, 9488, 9492, 9496 };
  private static final int USER_DATA_TYPE_CODE = 3;
  private static final int USER_ID = NUM;
  private final ArrayList<ClosedCaption> captions = new ArrayList();
  private final ParsableBitArray seiBuffer = new ParsableBitArray();
  private final StringBuilder stringBuilder = new StringBuilder();
  
  private void addBufferedText()
  {
    if (this.stringBuilder.length() > 0)
    {
      this.captions.add(new ClosedCaptionText(this.stringBuilder.toString()));
      this.stringBuilder.setLength(0);
    }
  }
  
  private void addCtrl(byte paramByte1, byte paramByte2)
  {
    addBufferedText();
    this.captions.add(new ClosedCaptionCtrl(paramByte1, paramByte2));
  }
  
  private void backspace()
  {
    addCtrl((byte)20, (byte)33);
  }
  
  private static char getChar(byte paramByte)
  {
    return (char)BASIC_CHARACTER_SET[((paramByte & 0x7F) - 32)];
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
  
  public static boolean isSeiMessageEia608(int paramInt1, int paramInt2, ParsableByteArray paramParsableByteArray)
  {
    if ((paramInt1 != 4) || (paramInt2 < 8)) {}
    int i;
    int j;
    int k;
    do
    {
      return false;
      paramInt1 = paramParsableByteArray.getPosition();
      paramInt2 = paramParsableByteArray.readUnsignedByte();
      i = paramParsableByteArray.readUnsignedShort();
      j = paramParsableByteArray.readInt();
      k = paramParsableByteArray.readUnsignedByte();
      paramParsableByteArray.setPosition(paramInt1);
    } while ((paramInt2 != 181) || (i != 49) || (j != NUM) || (k != 3));
    return true;
  }
  
  boolean canParse(String paramString)
  {
    return paramString.equals("application/eia-608");
  }
  
  ClosedCaptionList parse(SampleHolder paramSampleHolder)
  {
    if (paramSampleHolder.size < 10) {
      return null;
    }
    this.captions.clear();
    this.stringBuilder.setLength(0);
    this.seiBuffer.reset(paramSampleHolder.data.array());
    this.seiBuffer.skipBits(67);
    int j = this.seiBuffer.readBits(5);
    this.seiBuffer.skipBits(8);
    int i = 0;
    if (i < j)
    {
      this.seiBuffer.skipBits(5);
      if (!this.seiBuffer.readBit()) {
        this.seiBuffer.skipBits(18);
      }
      for (;;)
      {
        i += 1;
        break;
        if (this.seiBuffer.readBits(2) != 0)
        {
          this.seiBuffer.skipBits(16);
        }
        else
        {
          this.seiBuffer.skipBits(1);
          byte b1 = (byte)this.seiBuffer.readBits(7);
          this.seiBuffer.skipBits(1);
          byte b2 = (byte)this.seiBuffer.readBits(7);
          if ((b1 != 0) || (b2 != 0)) {
            if (((b1 == 17) || (b1 == 25)) && ((b2 & 0x70) == 48))
            {
              this.stringBuilder.append(getSpecialChar(b2));
            }
            else if (((b1 == 18) || (b1 == 26)) && ((b2 & 0x60) == 32))
            {
              backspace();
              this.stringBuilder.append(getExtendedEsFrChar(b2));
            }
            else if (((b1 == 19) || (b1 == 27)) && ((b2 & 0x60) == 32))
            {
              backspace();
              this.stringBuilder.append(getExtendedPtDeChar(b2));
            }
            else if (b1 < 32)
            {
              addCtrl(b1, b2);
            }
            else
            {
              this.stringBuilder.append(getChar(b1));
              if (b2 >= 32) {
                this.stringBuilder.append(getChar(b2));
              }
            }
          }
        }
      }
    }
    addBufferedText();
    if (this.captions.isEmpty()) {
      return null;
    }
    ClosedCaption[] arrayOfClosedCaption = new ClosedCaption[this.captions.size()];
    this.captions.toArray(arrayOfClosedCaption);
    return new ClosedCaptionList(paramSampleHolder.timeUs, paramSampleHolder.isDecodeOnly(), arrayOfClosedCaption);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/text/eia608/Eia608Parser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */