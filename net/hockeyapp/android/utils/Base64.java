package net.hockeyapp.android.utils;

import java.io.UnsupportedEncodingException;

public class Base64
{
  public static byte[] encode(byte[] paramArrayOfByte, int paramInt)
  {
    return encode(paramArrayOfByte, 0, paramArrayOfByte.length, paramInt);
  }
  
  public static byte[] encode(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
  {
    Encoder localEncoder = new Encoder(paramInt3, null);
    int i = paramInt2 / 3 * 4;
    int j;
    if (localEncoder.do_padding)
    {
      paramInt3 = i;
      if (paramInt2 % 3 > 0) {
        paramInt3 = i + 4;
      }
      i = paramInt3;
      if (localEncoder.do_newline)
      {
        i = paramInt3;
        if (paramInt2 > 0)
        {
          j = (paramInt2 - 1) / 57;
          if (!localEncoder.do_cr) {
            break label178;
          }
        }
      }
    }
    label178:
    for (i = 2;; i = 1)
    {
      i = paramInt3 + i * (j + 1);
      localEncoder.output = new byte[i];
      localEncoder.process(paramArrayOfByte, paramInt1, paramInt2, true);
      if (localEncoder.op == i) {
        break label184;
      }
      throw new AssertionError();
      paramInt3 = i;
      switch (paramInt2 % 3)
      {
      case 0: 
      default: 
        paramInt3 = i;
        break;
      case 1: 
        paramInt3 = i + 2;
        break;
      case 2: 
        paramInt3 = i + 3;
        break;
      }
    }
    label184:
    return localEncoder.output;
  }
  
  public static String encodeToString(byte[] paramArrayOfByte, int paramInt)
  {
    try
    {
      paramArrayOfByte = new String(encode(paramArrayOfByte, paramInt), "US-ASCII");
      return paramArrayOfByte;
    }
    catch (UnsupportedEncodingException paramArrayOfByte)
    {
      throw new AssertionError(paramArrayOfByte);
    }
  }
  
  static abstract class Coder
  {
    public int op;
    public byte[] output;
  }
  
  static class Encoder
    extends Base64.Coder
  {
    private static final byte[] ENCODE = { 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47 };
    private static final byte[] ENCODE_WEBSAFE = { 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 45, 95 };
    private final byte[] alphabet;
    private int count;
    public final boolean do_cr;
    public final boolean do_newline;
    public final boolean do_padding;
    private final byte[] tail;
    int tailLen;
    
    public Encoder(int paramInt, byte[] paramArrayOfByte)
    {
      this.output = paramArrayOfByte;
      boolean bool2;
      if ((paramInt & 0x1) == 0)
      {
        bool2 = true;
        this.do_padding = bool2;
        if ((paramInt & 0x2) != 0) {
          break label106;
        }
        bool2 = true;
        label35:
        this.do_newline = bool2;
        if ((paramInt & 0x4) == 0) {
          break label112;
        }
        bool2 = bool1;
        label50:
        this.do_cr = bool2;
        if ((paramInt & 0x8) != 0) {
          break label118;
        }
        paramArrayOfByte = ENCODE;
        label67:
        this.alphabet = paramArrayOfByte;
        this.tail = new byte[2];
        this.tailLen = 0;
        if (!this.do_newline) {
          break label125;
        }
      }
      label106:
      label112:
      label118:
      label125:
      for (paramInt = 19;; paramInt = -1)
      {
        this.count = paramInt;
        return;
        bool2 = false;
        break;
        bool2 = false;
        break label35;
        bool2 = false;
        break label50;
        paramArrayOfByte = ENCODE_WEBSAFE;
        break label67;
      }
    }
    
    public boolean process(byte[] paramArrayOfByte, int paramInt1, int paramInt2, boolean paramBoolean)
    {
      byte[] arrayOfByte1 = this.alphabet;
      byte[] arrayOfByte2 = this.output;
      int i = 0;
      int j = this.count;
      int k = paramInt1;
      int m = paramInt2 + paramInt1;
      paramInt2 = -1;
      paramInt1 = k;
      int n = paramInt2;
      int i1;
      switch (this.tailLen)
      {
      default: 
        n = paramInt2;
        paramInt1 = k;
      case 0: 
        paramInt2 = j;
        k = i;
        i = paramInt1;
        if (n != -1)
        {
          paramInt2 = 0 + 1;
          arrayOfByte2[0] = ((byte)arrayOfByte1[(n >> 18 & 0x3F)]);
          k = paramInt2 + 1;
          arrayOfByte2[paramInt2] = ((byte)arrayOfByte1[(n >> 12 & 0x3F)]);
          paramInt2 = k + 1;
          arrayOfByte2[k] = ((byte)arrayOfByte1[(n >> 6 & 0x3F)]);
          i1 = paramInt2 + 1;
          arrayOfByte2[paramInt2] = ((byte)arrayOfByte1[(n & 0x3F)]);
          n = j - 1;
          paramInt2 = n;
          k = i1;
          i = paramInt1;
          if (n == 0)
          {
            paramInt2 = i1;
            if (this.do_cr)
            {
              arrayOfByte2[i1] = ((byte)13);
              paramInt2 = i1 + 1;
            }
            k = paramInt2 + 1;
            arrayOfByte2[paramInt2] = ((byte)10);
            i = 19;
            paramInt2 = paramInt1;
            paramInt1 = k;
          }
        }
        break;
      }
      for (;;)
      {
        if (paramInt2 + 3 <= m)
        {
          k = (paramArrayOfByte[paramInt2] & 0xFF) << 16 | (paramArrayOfByte[(paramInt2 + 1)] & 0xFF) << 8 | paramArrayOfByte[(paramInt2 + 2)] & 0xFF;
          arrayOfByte2[paramInt1] = ((byte)arrayOfByte1[(k >> 18 & 0x3F)]);
          arrayOfByte2[(paramInt1 + 1)] = ((byte)arrayOfByte1[(k >> 12 & 0x3F)]);
          arrayOfByte2[(paramInt1 + 2)] = ((byte)arrayOfByte1[(k >> 6 & 0x3F)]);
          arrayOfByte2[(paramInt1 + 3)] = ((byte)arrayOfByte1[(k & 0x3F)]);
          n = paramInt2 + 3;
          paramInt1 += 4;
          i1 = i - 1;
          paramInt2 = i1;
          k = paramInt1;
          i = n;
          if (i1 != 0) {
            break label1252;
          }
          paramInt2 = paramInt1;
          if (this.do_cr)
          {
            arrayOfByte2[paramInt1] = ((byte)13);
            paramInt2 = paramInt1 + 1;
          }
          paramInt1 = paramInt2 + 1;
          arrayOfByte2[paramInt2] = ((byte)10);
          i = 19;
          paramInt2 = n;
          continue;
          paramInt1 = k;
          n = paramInt2;
          if (k + 2 > m) {
            break;
          }
          paramInt2 = this.tail[0];
          n = k + 1;
          k = paramArrayOfByte[k];
          paramInt1 = n + 1;
          n = (paramInt2 & 0xFF) << 16 | (k & 0xFF) << 8 | paramArrayOfByte[n] & 0xFF;
          this.tailLen = 0;
          break;
          paramInt1 = k;
          n = paramInt2;
          if (k + 1 > m) {
            break;
          }
          n = (this.tail[0] & 0xFF) << 16 | (this.tail[1] & 0xFF) << 8 | paramArrayOfByte[k] & 0xFF;
          this.tailLen = 0;
          paramInt1 = k + 1;
          break;
        }
        if (paramBoolean)
        {
          if (paramInt2 - this.tailLen == m - 1)
          {
            k = 0;
            if (this.tailLen > 0)
            {
              n = this.tail[0];
              k = 0 + 1;
            }
            for (;;)
            {
              n = (n & 0xFF) << 4;
              this.tailLen -= k;
              k = paramInt1 + 1;
              arrayOfByte2[paramInt1] = ((byte)arrayOfByte1[(n >> 6 & 0x3F)]);
              paramInt1 = k + 1;
              arrayOfByte2[k] = ((byte)arrayOfByte1[(n & 0x3F)]);
              n = paramInt1;
              if (this.do_padding)
              {
                k = paramInt1 + 1;
                arrayOfByte2[paramInt1] = ((byte)61);
                n = k + 1;
                arrayOfByte2[k] = ((byte)61);
              }
              paramInt1 = n;
              k = paramInt2;
              if (this.do_newline)
              {
                paramInt1 = n;
                if (this.do_cr)
                {
                  arrayOfByte2[n] = ((byte)13);
                  paramInt1 = n + 1;
                }
                k = paramInt1 + 1;
                arrayOfByte2[paramInt1] = ((byte)10);
                paramInt1 = k;
                k = paramInt2;
              }
              label750:
              if (this.tailLen != 0) {
                HockeyLog.error("BASE64", "Error during encoding");
              }
              paramInt2 = paramInt1;
              if (k != m)
              {
                HockeyLog.error("BASE64", "Error during encoding");
                paramInt2 = paramInt1;
              }
              label782:
              this.op = paramInt2;
              this.count = i;
              return true;
              i1 = paramInt2 + 1;
              n = paramArrayOfByte[paramInt2];
              paramInt2 = i1;
            }
          }
          if (paramInt2 - this.tailLen == m - 2)
          {
            k = 0;
            if (this.tailLen > 1)
            {
              n = this.tail[0];
              k = 0 + 1;
              label848:
              if (this.tailLen <= 0) {
                break label1053;
              }
              i1 = this.tail[k];
              k++;
            }
            for (;;)
            {
              n = (n & 0xFF) << 10 | (i1 & 0xFF) << 2;
              this.tailLen -= k;
              i1 = paramInt1 + 1;
              arrayOfByte2[paramInt1] = ((byte)arrayOfByte1[(n >> 12 & 0x3F)]);
              k = i1 + 1;
              arrayOfByte2[i1] = ((byte)arrayOfByte1[(n >> 6 & 0x3F)]);
              paramInt1 = k + 1;
              arrayOfByte2[k] = ((byte)arrayOfByte1[(n & 0x3F)]);
              n = paramInt1;
              if (this.do_padding)
              {
                arrayOfByte2[paramInt1] = ((byte)61);
                n = paramInt1 + 1;
              }
              paramInt1 = n;
              k = paramInt2;
              if (!this.do_newline) {
                break label750;
              }
              paramInt1 = n;
              if (this.do_cr)
              {
                arrayOfByte2[n] = ((byte)13);
                paramInt1 = n + 1;
              }
              k = paramInt1 + 1;
              arrayOfByte2[paramInt1] = ((byte)10);
              paramInt1 = k;
              break;
              i1 = paramInt2 + 1;
              n = paramArrayOfByte[paramInt2];
              paramInt2 = i1;
              break label848;
              label1053:
              i1 = paramArrayOfByte[paramInt2];
              paramInt2++;
            }
          }
          k = paramInt1;
          if (this.do_newline)
          {
            k = paramInt1;
            if (paramInt1 > 0)
            {
              k = paramInt1;
              if (i != 19)
              {
                if (!this.do_cr) {
                  break label1249;
                }
                k = paramInt1 + 1;
                arrayOfByte2[paramInt1] = ((byte)13);
                paramInt1 = k;
              }
            }
          }
        }
        label1249:
        for (;;)
        {
          k = paramInt1 + 1;
          arrayOfByte2[paramInt1] = ((byte)10);
          paramInt1 = k;
          k = paramInt2;
          break;
          if (paramInt2 == m - 1)
          {
            arrayOfByte1 = this.tail;
            k = this.tailLen;
            this.tailLen = (k + 1);
            arrayOfByte1[k] = ((byte)paramArrayOfByte[paramInt2]);
            paramInt2 = paramInt1;
            break label782;
          }
          if (paramInt2 == m - 2)
          {
            arrayOfByte1 = this.tail;
            k = this.tailLen;
            this.tailLen = (k + 1);
            arrayOfByte1[k] = ((byte)paramArrayOfByte[paramInt2]);
            arrayOfByte1 = this.tail;
            k = this.tailLen;
            this.tailLen = (k + 1);
            arrayOfByte1[k] = ((byte)paramArrayOfByte[(paramInt2 + 1)]);
          }
          paramInt2 = paramInt1;
          break label782;
        }
        label1252:
        n = i;
        paramInt1 = k;
        i = paramInt2;
        paramInt2 = n;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/utils/Base64.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */