package com.google.android.gms.internal;

import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;

public class zzapy
  implements Closeable
{
  private static final char[] bnX = ")]}'\n".toCharArray();
  private boolean bnY = false;
  private final char[] bnZ = new char['Ѐ'];
  private int boa = 0;
  private int bob = 0;
  private int boc = 0;
  private long bod;
  private int boe;
  private String bof;
  private int[] bog = new int[32];
  private int boh = 0;
  private String[] boi;
  private int[] boj;
  private final Reader in;
  private int limit = 0;
  private int pos = 0;
  
  static
  {
    zzapd.blQ = new zzapd()
    {
      public void zzi(zzapy paramAnonymouszzapy)
        throws IOException
      {
        if ((paramAnonymouszzapy instanceof zzapo))
        {
          ((zzapo)paramAnonymouszzapy).bq();
          return;
        }
        int j = zzapy.zzag(paramAnonymouszzapy);
        int i = j;
        if (j == 0) {
          i = zzapy.zzah(paramAnonymouszzapy);
        }
        if (i == 13)
        {
          zzapy.zza(paramAnonymouszzapy, 9);
          return;
        }
        if (i == 12)
        {
          zzapy.zza(paramAnonymouszzapy, 8);
          return;
        }
        if (i == 14)
        {
          zzapy.zza(paramAnonymouszzapy, 10);
          return;
        }
        String str = String.valueOf(paramAnonymouszzapy.bn());
        i = zzapy.zzai(paramAnonymouszzapy);
        j = zzapy.zzaj(paramAnonymouszzapy);
        paramAnonymouszzapy = paramAnonymouszzapy.getPath();
        throw new IllegalStateException(String.valueOf(str).length() + 70 + String.valueOf(paramAnonymouszzapy).length() + "Expected a name but was " + str + " " + " at line " + i + " column " + j + " path " + paramAnonymouszzapy);
      }
    };
  }
  
  public zzapy(Reader paramReader)
  {
    int[] arrayOfInt = this.bog;
    int i = this.boh;
    this.boh = (i + 1);
    arrayOfInt[i] = 6;
    this.boi = new String[32];
    this.boj = new int[32];
    if (paramReader == null) {
      throw new NullPointerException("in == null");
    }
    this.in = paramReader;
  }
  
  private int bA()
    throws IOException
  {
    int i = this.bog[(this.boh - 1)];
    if (i == 1)
    {
      this.bog[(this.boh - 1)] = 2;
      switch (zzdg(true))
      {
      default: 
        this.pos -= 1;
        if (this.boh == 1) {
          bF();
        }
        i = bB();
        if (i == 0) {
          break;
        }
      }
    }
    int j;
    do
    {
      return i;
      if (i == 2)
      {
        switch (zzdg(true))
        {
        case 44: 
        default: 
          throw zzuv("Unterminated array");
        case 93: 
          this.boc = 4;
          return 4;
        }
        bF();
        break;
      }
      if ((i == 3) || (i == 5))
      {
        this.bog[(this.boh - 1)] = 4;
        if (i == 5) {
          switch (zzdg(true))
          {
          default: 
            throw zzuv("Unterminated object");
          case 125: 
            this.boc = 2;
            return 2;
          case 59: 
            bF();
          }
        }
        j = zzdg(true);
        switch (j)
        {
        default: 
          bF();
          this.pos -= 1;
          if (zze((char)j))
          {
            this.boc = 14;
            return 14;
          }
          break;
        case 34: 
          this.boc = 13;
          return 13;
        case 39: 
          bF();
          this.boc = 12;
          return 12;
        case 125: 
          if (i != 5)
          {
            this.boc = 2;
            return 2;
          }
          throw zzuv("Expected name");
        }
        throw zzuv("Expected name");
      }
      if (i == 4)
      {
        this.bog[(this.boh - 1)] = 5;
        switch (zzdg(true))
        {
        case 58: 
        case 59: 
        case 60: 
        default: 
          throw zzuv("Expected ':'");
        }
        bF();
        if (((this.pos >= this.limit) && (!zzagx(1))) || (this.bnZ[this.pos] != '>')) {
          break;
        }
        this.pos += 1;
        break;
      }
      if (i == 6)
      {
        if (this.bnY) {
          bI();
        }
        this.bog[(this.boh - 1)] = 7;
        break;
      }
      if (i == 7)
      {
        if (zzdg(false) == -1)
        {
          this.boc = 17;
          return 17;
        }
        bF();
        this.pos -= 1;
        break;
      }
      if (i != 8) {
        break;
      }
      throw new IllegalStateException("JsonReader is closed");
      if (i == 1)
      {
        this.boc = 4;
        return 4;
      }
      if ((i == 1) || (i == 2))
      {
        bF();
        this.pos -= 1;
        this.boc = 7;
        return 7;
      }
      throw zzuv("Unexpected value");
      bF();
      this.boc = 8;
      return 8;
      if (this.boh == 1) {
        bF();
      }
      this.boc = 9;
      return 9;
      this.boc = 3;
      return 3;
      this.boc = 1;
      return 1;
      j = bC();
      i = j;
    } while (j != 0);
    if (!zze(this.bnZ[this.pos])) {
      throw zzuv("Expected value");
    }
    bF();
    this.boc = 10;
    return 10;
  }
  
  private int bB()
    throws IOException
  {
    int i = this.bnZ[this.pos];
    String str2;
    String str1;
    int k;
    int j;
    if ((i == 116) || (i == 84))
    {
      str2 = "true";
      str1 = "TRUE";
      i = 5;
      k = str2.length();
      j = 1;
    }
    for (;;)
    {
      if (j >= k) {
        break label168;
      }
      if ((this.pos + j >= this.limit) && (!zzagx(j + 1)))
      {
        return 0;
        if ((i == 102) || (i == 70))
        {
          str2 = "false";
          str1 = "FALSE";
          i = 6;
          break;
        }
        if ((i == 110) || (i == 78))
        {
          str2 = "null";
          str1 = "NULL";
          i = 7;
          break;
        }
        return 0;
      }
      int m = this.bnZ[(this.pos + j)];
      if ((m != str2.charAt(j)) && (m != str1.charAt(j))) {
        return 0;
      }
      j += 1;
    }
    label168:
    if (((this.pos + k < this.limit) || (zzagx(k + 1))) && (zze(this.bnZ[(this.pos + k)]))) {
      return 0;
    }
    this.pos += k;
    this.boc = i;
    return i;
  }
  
  private int bC()
    throws IOException
  {
    char[] arrayOfChar = this.bnZ;
    int i2 = this.pos;
    int n = this.limit;
    long l1 = 0L;
    int i = 0;
    int j = 1;
    int k = 0;
    int m = 0;
    int i3 = n;
    int i1 = i3;
    n = i2;
    if (i2 + m == i3)
    {
      if (m == arrayOfChar.length) {
        return 0;
      }
      if (zzagx(m + 1)) {}
    }
    label101:
    char c;
    for (;;)
    {
      if ((k == 2) && (j != 0) && ((l1 != Long.MIN_VALUE) || (i != 0))) {
        if (i != 0)
        {
          this.bod = l1;
          this.pos += m;
          this.boc = 15;
          return 15;
          n = this.pos;
          i1 = this.limit;
          c = arrayOfChar[(n + m)];
          switch (c)
          {
          default: 
            if ((c < '0') || (c > '9'))
            {
              if (!zze(c)) {
                continue;
              }
              return 0;
            }
            break;
          case '-': 
            if (k == 0)
            {
              i = 1;
              k = 1;
            }
            break;
          }
        }
      }
    }
    for (;;)
    {
      int i4 = m + 1;
      m = k;
      i3 = i1;
      i2 = n;
      k = i;
      i = m;
      m = i4;
      break;
      if (k == 5)
      {
        i2 = 6;
        k = i;
        i = i2;
      }
      else
      {
        return 0;
        if (k == 5)
        {
          i2 = 6;
          k = i;
          i = i2;
        }
        else
        {
          return 0;
          if ((k == 2) || (k == 4))
          {
            i2 = 5;
            k = i;
            i = i2;
          }
          else
          {
            return 0;
            if (k == 2)
            {
              i2 = 3;
              k = i;
              i = i2;
            }
            else
            {
              return 0;
              if ((k == 1) || (k == 0))
              {
                l1 = -(c - '0');
                i2 = 2;
                k = i;
                i = i2;
              }
              else
              {
                if (k == 2)
                {
                  if (l1 == 0L) {
                    return 0;
                  }
                  long l2 = 10L * l1 - (c - '0');
                  if ((l1 > -922337203685477580L) || ((l1 == -922337203685477580L) && (l2 < l1))) {}
                  for (i3 = 1;; i3 = 0)
                  {
                    i2 = i;
                    l1 = l2;
                    j = i3 & j;
                    i = k;
                    k = i2;
                    break;
                  }
                }
                if (k == 3)
                {
                  i2 = 4;
                  k = i;
                  i = i2;
                }
                else
                {
                  if ((k == 5) || (k == 6))
                  {
                    i2 = 7;
                    k = i;
                    i = i2;
                    continue;
                    l1 = -l1;
                    break label101;
                    if ((k == 2) || (k == 4) || (k == 7))
                    {
                      this.boe = m;
                      this.boc = 16;
                      return 16;
                    }
                    return 0;
                  }
                  i2 = i;
                  i = k;
                  k = i2;
                }
              }
            }
          }
        }
      }
    }
  }
  
  private String bD()
    throws IOException
  {
    Object localObject1 = null;
    int i = 0;
    for (;;)
    {
      Object localObject2;
      int j;
      if (this.pos + i < this.limit)
      {
        localObject2 = localObject1;
        j = i;
        switch (this.bnZ[(this.pos + i)])
        {
        default: 
          i += 1;
          break;
        case '#': 
        case '/': 
        case ';': 
        case '=': 
        case '\\': 
          bF();
          j = i;
          localObject2 = localObject1;
        case '\t': 
        case '\n': 
        case '\f': 
        case '\r': 
        case ' ': 
        case ',': 
        case ':': 
        case '[': 
        case ']': 
        case '{': 
        case '}': 
          label188:
          if (localObject2 != null) {}
          break;
        }
      }
      else
      {
        for (localObject1 = new String(this.bnZ, this.pos, j);; localObject1 = ((StringBuilder)localObject2).toString())
        {
          this.pos = (j + this.pos);
          return (String)localObject1;
          if (i < this.bnZ.length)
          {
            localObject2 = localObject1;
            j = i;
            if (!zzagx(i + 1)) {
              break label188;
            }
            break;
          }
          localObject2 = localObject1;
          if (localObject1 == null) {
            localObject2 = new StringBuilder();
          }
          ((StringBuilder)localObject2).append(this.bnZ, this.pos, i);
          this.pos = (i + this.pos);
          if (zzagx(1)) {
            break label327;
          }
          j = 0;
          break label188;
          ((StringBuilder)localObject2).append(this.bnZ, this.pos, j);
        }
        label327:
        i = 0;
        localObject1 = localObject2;
      }
    }
  }
  
  private void bE()
    throws IOException
  {
    do
    {
      int i = 0;
      while (this.pos + i < this.limit) {
        switch (this.bnZ[(this.pos + i)])
        {
        default: 
          i += 1;
          break;
        case '#': 
        case '/': 
        case ';': 
        case '=': 
        case '\\': 
          bF();
        case '\t': 
        case '\n': 
        case '\f': 
        case '\r': 
        case ' ': 
        case ',': 
        case ':': 
        case '[': 
        case ']': 
        case '{': 
        case '}': 
          this.pos = (i + this.pos);
          return;
        }
      }
      this.pos = (i + this.pos);
    } while (zzagx(1));
  }
  
  private void bF()
    throws IOException
  {
    if (!this.bnY) {
      throw zzuv("Use JsonReader.setLenient(true) to accept malformed JSON");
    }
  }
  
  private void bG()
    throws IOException
  {
    int i;
    do
    {
      if ((this.pos < this.limit) || (zzagx(1)))
      {
        char[] arrayOfChar = this.bnZ;
        i = this.pos;
        this.pos = (i + 1);
        i = arrayOfChar[i];
        if (i == 10)
        {
          this.boa += 1;
          this.bob = this.pos;
        }
      }
      else
      {
        return;
      }
    } while (i != 13);
  }
  
  private char bH()
    throws IOException
  {
    if ((this.pos == this.limit) && (!zzagx(1))) {
      throw zzuv("Unterminated escape sequence");
    }
    Object localObject = this.bnZ;
    int i = this.pos;
    this.pos = (i + 1);
    char c = localObject[i];
    switch (c)
    {
    default: 
      return c;
    case 'u': 
      if ((this.pos + 4 > this.limit) && (!zzagx(4))) {
        throw zzuv("Unterminated escape sequence");
      }
      int j = this.pos;
      c = '\000';
      i = j;
      if (i < j + 4)
      {
        int k = this.bnZ[i];
        int m = (char)(c << '\004');
        if ((k >= 48) && (k <= 57)) {
          c = (char)(m + (k - 48));
        }
        for (;;)
        {
          i += 1;
          break;
          if ((k >= 97) && (k <= 102))
          {
            c = (char)(m + (k - 97 + 10));
          }
          else
          {
            if ((k < 65) || (k > 70)) {
              break label267;
            }
            c = (char)(m + (k - 65 + 10));
          }
        }
        localObject = String.valueOf(new String(this.bnZ, this.pos, 4));
        if (((String)localObject).length() != 0) {}
        for (localObject = "\\u".concat((String)localObject);; localObject = new String("\\u")) {
          throw new NumberFormatException((String)localObject);
        }
      }
      this.pos += 4;
      return c;
    case 't': 
      return '\t';
    case 'b': 
      return '\b';
    case 'n': 
      return '\n';
    case 'r': 
      return '\r';
    case 'f': 
      label267:
      return '\f';
    }
    this.boa += 1;
    this.bob = this.pos;
    return c;
  }
  
  private void bI()
    throws IOException
  {
    zzdg(true);
    this.pos -= 1;
    if ((this.pos + bnX.length > this.limit) && (!zzagx(bnX.length))) {
      return;
    }
    int i = 0;
    for (;;)
    {
      if (i >= bnX.length) {
        break label80;
      }
      if (this.bnZ[(this.pos + i)] != bnX[i]) {
        break;
      }
      i += 1;
    }
    label80:
    this.pos += bnX.length;
  }
  
  private int getColumnNumber()
  {
    return this.pos - this.bob + 1;
  }
  
  private int getLineNumber()
  {
    return this.boa + 1;
  }
  
  private void zzagw(int paramInt)
  {
    if (this.boh == this.bog.length)
    {
      arrayOfInt1 = new int[this.boh * 2];
      int[] arrayOfInt2 = new int[this.boh * 2];
      String[] arrayOfString = new String[this.boh * 2];
      System.arraycopy(this.bog, 0, arrayOfInt1, 0, this.boh);
      System.arraycopy(this.boj, 0, arrayOfInt2, 0, this.boh);
      System.arraycopy(this.boi, 0, arrayOfString, 0, this.boh);
      this.bog = arrayOfInt1;
      this.boj = arrayOfInt2;
      this.boi = arrayOfString;
    }
    int[] arrayOfInt1 = this.bog;
    int i = this.boh;
    this.boh = (i + 1);
    arrayOfInt1[i] = paramInt;
  }
  
  private boolean zzagx(int paramInt)
    throws IOException
  {
    boolean bool2 = false;
    char[] arrayOfChar = this.bnZ;
    this.bob -= this.pos;
    if (this.limit != this.pos)
    {
      this.limit -= this.pos;
      System.arraycopy(arrayOfChar, this.pos, arrayOfChar, 0, this.limit);
    }
    for (;;)
    {
      this.pos = 0;
      int i;
      do
      {
        i = this.in.read(arrayOfChar, this.limit, arrayOfChar.length - this.limit);
        bool1 = bool2;
        if (i == -1) {
          break;
        }
        this.limit = (i + this.limit);
        i = paramInt;
        if (this.boa == 0)
        {
          i = paramInt;
          if (this.bob == 0)
          {
            i = paramInt;
            if (this.limit > 0)
            {
              i = paramInt;
              if (arrayOfChar[0] == 65279)
              {
                this.pos += 1;
                this.bob += 1;
                i = paramInt + 1;
              }
            }
          }
        }
        paramInt = i;
      } while (this.limit < i);
      boolean bool1 = true;
      return bool1;
      this.limit = 0;
    }
  }
  
  private int zzdg(boolean paramBoolean)
    throws IOException
  {
    Object localObject = this.bnZ;
    int i = this.pos;
    int j = this.limit;
    for (;;)
    {
      int k = j;
      int m = i;
      if (i == j)
      {
        this.pos = i;
        if (!zzagx(1))
        {
          if (paramBoolean)
          {
            localObject = String.valueOf("End of input at line ");
            i = getLineNumber();
            j = getColumnNumber();
            throw new EOFException(String.valueOf(localObject).length() + 30 + (String)localObject + i + " column " + j);
          }
        }
        else
        {
          m = this.pos;
          k = this.limit;
        }
      }
      else
      {
        i = m + 1;
        j = localObject[m];
        if (j == 10)
        {
          this.boa += 1;
          this.bob = i;
          j = k;
          continue;
        }
        if ((j == 32) || (j == 13)) {
          break label384;
        }
        if (j == 9)
        {
          j = k;
          continue;
        }
        if (j == 47)
        {
          this.pos = i;
          if (i == k)
          {
            this.pos -= 1;
            boolean bool = zzagx(2);
            this.pos += 1;
            if (!bool) {
              return j;
            }
          }
          bF();
          switch (localObject[this.pos])
          {
          default: 
            return j;
          case '*': 
            this.pos += 1;
            if (!zzuu("*/")) {
              throw zzuv("Unterminated comment");
            }
            i = this.pos + 2;
            j = this.limit;
            break;
          case '/': 
            this.pos += 1;
            bG();
            i = this.pos;
            j = this.limit;
            break;
          }
        }
        if (j == 35)
        {
          this.pos = i;
          bF();
          bG();
          i = this.pos;
          j = this.limit;
          continue;
        }
        this.pos = i;
        return j;
      }
      return -1;
      label384:
      j = k;
    }
  }
  
  private boolean zze(char paramChar)
    throws IOException
  {
    switch (paramChar)
    {
    default: 
      return true;
    case '#': 
    case '/': 
    case ';': 
    case '=': 
    case '\\': 
      bF();
    }
    return false;
  }
  
  private String zzf(char paramChar)
    throws IOException
  {
    char[] arrayOfChar = this.bnZ;
    StringBuilder localStringBuilder = new StringBuilder();
    do
    {
      int k = this.pos;
      int j = this.limit;
      int i = k;
      if (i < j)
      {
        int i1 = i + 1;
        char c = arrayOfChar[i];
        if (c == paramChar)
        {
          this.pos = i1;
          localStringBuilder.append(arrayOfChar, k, i1 - k - 1);
          return localStringBuilder.toString();
        }
        int n;
        int m;
        if (c == '\\')
        {
          this.pos = i1;
          localStringBuilder.append(arrayOfChar, k, i1 - k - 1);
          localStringBuilder.append(bH());
          n = this.pos;
          m = this.limit;
          i = n;
        }
        for (;;)
        {
          k = n;
          j = m;
          break;
          n = k;
          m = j;
          i = i1;
          if (c == '\n')
          {
            this.boa += 1;
            this.bob = i1;
            n = k;
            m = j;
            i = i1;
          }
        }
      }
      localStringBuilder.append(arrayOfChar, k, i - k);
      this.pos = i;
    } while (zzagx(1));
    throw zzuv("Unterminated string");
  }
  
  private void zzg(char paramChar)
    throws IOException
  {
    char[] arrayOfChar = this.bnZ;
    do
    {
      int i = this.pos;
      int j = this.limit;
      if (i < j)
      {
        int m = i + 1;
        char c = arrayOfChar[i];
        if (c == paramChar)
        {
          this.pos = m;
          return;
        }
        int k;
        if (c == '\\')
        {
          this.pos = m;
          bH();
          i = this.pos;
          k = this.limit;
        }
        for (;;)
        {
          j = k;
          break;
          k = j;
          i = m;
          if (c == '\n')
          {
            this.boa += 1;
            this.bob = m;
            k = j;
            i = m;
          }
        }
      }
      this.pos = i;
    } while (zzagx(1));
    throw zzuv("Unterminated string");
  }
  
  private boolean zzuu(String paramString)
    throws IOException
  {
    boolean bool2 = false;
    for (;;)
    {
      if (this.pos + paramString.length() > this.limit)
      {
        bool1 = bool2;
        if (!zzagx(paramString.length())) {
          return bool1;
        }
      }
      if (this.bnZ[this.pos] != '\n') {
        break;
      }
      this.boa += 1;
      this.bob = (this.pos + 1);
      this.pos += 1;
    }
    int i = 0;
    for (;;)
    {
      if (i >= paramString.length()) {
        break label116;
      }
      if (this.bnZ[(this.pos + i)] != paramString.charAt(i)) {
        break;
      }
      i += 1;
    }
    label116:
    boolean bool1 = true;
    return bool1;
  }
  
  private IOException zzuv(String paramString)
    throws IOException
  {
    int i = getLineNumber();
    int j = getColumnNumber();
    String str = getPath();
    throw new zzaqb(String.valueOf(paramString).length() + 45 + String.valueOf(str).length() + paramString + " at line " + i + " column " + j + " path " + str);
  }
  
  public void beginArray()
    throws IOException
  {
    int j = this.boc;
    int i = j;
    if (j == 0) {
      i = bA();
    }
    if (i == 3)
    {
      zzagw(1);
      this.boj[(this.boh - 1)] = 0;
      this.boc = 0;
      return;
    }
    String str1 = String.valueOf(bn());
    i = getLineNumber();
    j = getColumnNumber();
    String str2 = getPath();
    throw new IllegalStateException(String.valueOf(str1).length() + 74 + String.valueOf(str2).length() + "Expected BEGIN_ARRAY but was " + str1 + " at line " + i + " column " + j + " path " + str2);
  }
  
  public void beginObject()
    throws IOException
  {
    int j = this.boc;
    int i = j;
    if (j == 0) {
      i = bA();
    }
    if (i == 1)
    {
      zzagw(3);
      this.boc = 0;
      return;
    }
    String str1 = String.valueOf(bn());
    i = getLineNumber();
    j = getColumnNumber();
    String str2 = getPath();
    throw new IllegalStateException(String.valueOf(str1).length() + 75 + String.valueOf(str2).length() + "Expected BEGIN_OBJECT but was " + str1 + " at line " + i + " column " + j + " path " + str2);
  }
  
  public zzapz bn()
    throws IOException
  {
    int j = this.boc;
    int i = j;
    if (j == 0) {
      i = bA();
    }
    switch (i)
    {
    default: 
      throw new AssertionError();
    case 1: 
      return zzapz.bom;
    case 2: 
      return zzapz.bon;
    case 3: 
      return zzapz.bok;
    case 4: 
      return zzapz.bol;
    case 12: 
    case 13: 
    case 14: 
      return zzapz.boo;
    case 5: 
    case 6: 
      return zzapz.bor;
    case 7: 
      return zzapz.bos;
    case 8: 
    case 9: 
    case 10: 
    case 11: 
      return zzapz.bop;
    case 15: 
    case 16: 
      return zzapz.boq;
    }
    return zzapz.bot;
  }
  
  public void close()
    throws IOException
  {
    this.boc = 0;
    this.bog[0] = 8;
    this.boh = 1;
    this.in.close();
  }
  
  public void endArray()
    throws IOException
  {
    int j = this.boc;
    int i = j;
    if (j == 0) {
      i = bA();
    }
    if (i == 4)
    {
      this.boh -= 1;
      localObject = this.boj;
      i = this.boh - 1;
      localObject[i] += 1;
      this.boc = 0;
      return;
    }
    Object localObject = String.valueOf(bn());
    i = getLineNumber();
    j = getColumnNumber();
    String str = getPath();
    throw new IllegalStateException(String.valueOf(localObject).length() + 72 + String.valueOf(str).length() + "Expected END_ARRAY but was " + (String)localObject + " at line " + i + " column " + j + " path " + str);
  }
  
  public void endObject()
    throws IOException
  {
    int j = this.boc;
    int i = j;
    if (j == 0) {
      i = bA();
    }
    if (i == 2)
    {
      this.boh -= 1;
      this.boi[this.boh] = null;
      localObject = this.boj;
      i = this.boh - 1;
      localObject[i] += 1;
      this.boc = 0;
      return;
    }
    Object localObject = String.valueOf(bn());
    i = getLineNumber();
    j = getColumnNumber();
    String str = getPath();
    throw new IllegalStateException(String.valueOf(localObject).length() + 73 + String.valueOf(str).length() + "Expected END_OBJECT but was " + (String)localObject + " at line " + i + " column " + j + " path " + str);
  }
  
  public String getPath()
  {
    StringBuilder localStringBuilder = new StringBuilder().append('$');
    int i = 0;
    int j = this.boh;
    if (i < j)
    {
      switch (this.bog[i])
      {
      }
      for (;;)
      {
        i += 1;
        break;
        localStringBuilder.append('[').append(this.boj[i]).append(']');
        continue;
        localStringBuilder.append('.');
        if (this.boi[i] != null) {
          localStringBuilder.append(this.boi[i]);
        }
      }
    }
    return localStringBuilder.toString();
  }
  
  public boolean hasNext()
    throws IOException
  {
    int j = this.boc;
    int i = j;
    if (j == 0) {
      i = bA();
    }
    return (i != 2) && (i != 4);
  }
  
  public final boolean isLenient()
  {
    return this.bnY;
  }
  
  public boolean nextBoolean()
    throws IOException
  {
    int j = this.boc;
    int i = j;
    if (j == 0) {
      i = bA();
    }
    if (i == 5)
    {
      this.boc = 0;
      localObject = this.boj;
      i = this.boh - 1;
      localObject[i] += 1;
      return true;
    }
    if (i == 6)
    {
      this.boc = 0;
      localObject = this.boj;
      i = this.boh - 1;
      localObject[i] += 1;
      return false;
    }
    Object localObject = String.valueOf(bn());
    i = getLineNumber();
    j = getColumnNumber();
    String str = getPath();
    throw new IllegalStateException(String.valueOf(localObject).length() + 72 + String.valueOf(str).length() + "Expected a boolean but was " + (String)localObject + " at line " + i + " column " + j + " path " + str);
  }
  
  public double nextDouble()
    throws IOException
  {
    int j = this.boc;
    int i = j;
    if (j == 0) {
      i = bA();
    }
    if (i == 15)
    {
      this.boc = 0;
      localObject = this.boj;
      i = this.boh - 1;
      localObject[i] += 1;
      return this.bod;
    }
    if (i == 16)
    {
      this.bof = new String(this.bnZ, this.pos, this.boe);
      this.pos += this.boe;
    }
    double d;
    do
    {
      for (;;)
      {
        this.boc = 11;
        d = Double.parseDouble(this.bof);
        if ((this.bnY) || ((!Double.isNaN(d)) && (!Double.isInfinite(d)))) {
          break label407;
        }
        i = getLineNumber();
        j = getColumnNumber();
        localObject = getPath();
        throw new zzaqb(String.valueOf(localObject).length() + 102 + "JSON forbids NaN and infinities: " + d + " at line " + i + " column " + j + " path " + (String)localObject);
        if ((i == 8) || (i == 9))
        {
          if (i == 8) {}
          for (char c = '\'';; c = '"')
          {
            this.bof = zzf(c);
            break;
          }
        }
        if (i != 10) {
          break;
        }
        this.bof = bD();
      }
    } while (i == 11);
    Object localObject = String.valueOf(bn());
    i = getLineNumber();
    j = getColumnNumber();
    String str = getPath();
    throw new IllegalStateException(String.valueOf(localObject).length() + 71 + String.valueOf(str).length() + "Expected a double but was " + (String)localObject + " at line " + i + " column " + j + " path " + str);
    label407:
    this.bof = null;
    this.boc = 0;
    localObject = this.boj;
    i = this.boh - 1;
    localObject[i] += 1;
    return d;
  }
  
  public int nextInt()
    throws IOException
  {
    int j = this.boc;
    int i = j;
    if (j == 0) {
      i = bA();
    }
    Object localObject1;
    if (i == 15)
    {
      i = (int)this.bod;
      if (this.bod != i)
      {
        long l = this.bod;
        i = getLineNumber();
        j = getColumnNumber();
        localObject1 = getPath();
        throw new NumberFormatException(String.valueOf(localObject1).length() + 89 + "Expected an int but was " + l + " at line " + i + " column " + j + " path " + (String)localObject1);
      }
      this.boc = 0;
      localObject1 = this.boj;
      j = this.boh - 1;
      localObject1[j] += 1;
      return i;
    }
    String str;
    if (i == 16)
    {
      this.bof = new String(this.bnZ, this.pos, this.boe);
      this.pos += this.boe;
      this.boc = 11;
      double d = Double.parseDouble(this.bof);
      i = (int)d;
      if (i != d)
      {
        localObject1 = this.bof;
        i = getLineNumber();
        j = getColumnNumber();
        str = getPath();
        throw new NumberFormatException(String.valueOf(localObject1).length() + 69 + String.valueOf(str).length() + "Expected an int but was " + (String)localObject1 + " at line " + i + " column " + j + " path " + str);
      }
    }
    else
    {
      if ((i == 8) || (i == 9))
      {
        if (i == 8) {}
        for (char c = '\'';; c = '"')
        {
          this.bof = zzf(c);
          try
          {
            i = Integer.parseInt(this.bof);
            this.boc = 0;
            localObject1 = this.boj;
            j = this.boh - 1;
            localObject1[j] += 1;
            return i;
          }
          catch (NumberFormatException localNumberFormatException) {}
          break;
        }
      }
      localObject2 = String.valueOf(bn());
      i = getLineNumber();
      j = getColumnNumber();
      str = getPath();
      throw new IllegalStateException(String.valueOf(localObject2).length() + 69 + String.valueOf(str).length() + "Expected an int but was " + (String)localObject2 + " at line " + i + " column " + j + " path " + str);
    }
    this.bof = null;
    this.boc = 0;
    Object localObject2 = this.boj;
    j = this.boh - 1;
    localObject2[j] += 1;
    return i;
  }
  
  public long nextLong()
    throws IOException
  {
    int j = this.boc;
    int i = j;
    if (j == 0) {
      i = bA();
    }
    Object localObject1;
    if (i == 15)
    {
      this.boc = 0;
      localObject1 = this.boj;
      i = this.boh - 1;
      localObject1[i] += 1;
      return this.bod;
    }
    long l;
    String str;
    if (i == 16)
    {
      this.bof = new String(this.bnZ, this.pos, this.boe);
      this.pos += this.boe;
      this.boc = 11;
      double d = Double.parseDouble(this.bof);
      l = d;
      if (l != d)
      {
        localObject1 = this.bof;
        i = getLineNumber();
        j = getColumnNumber();
        str = getPath();
        throw new NumberFormatException(String.valueOf(localObject1).length() + 69 + String.valueOf(str).length() + "Expected a long but was " + (String)localObject1 + " at line " + i + " column " + j + " path " + str);
      }
    }
    else
    {
      if ((i == 8) || (i == 9))
      {
        if (i == 8) {}
        for (char c = '\'';; c = '"')
        {
          this.bof = zzf(c);
          try
          {
            l = Long.parseLong(this.bof);
            this.boc = 0;
            localObject1 = this.boj;
            i = this.boh - 1;
            localObject1[i] += 1;
            return l;
          }
          catch (NumberFormatException localNumberFormatException) {}
          break;
        }
      }
      localObject2 = String.valueOf(bn());
      i = getLineNumber();
      j = getColumnNumber();
      str = getPath();
      throw new IllegalStateException(String.valueOf(localObject2).length() + 69 + String.valueOf(str).length() + "Expected a long but was " + (String)localObject2 + " at line " + i + " column " + j + " path " + str);
    }
    this.bof = null;
    this.boc = 0;
    Object localObject2 = this.boj;
    i = this.boh - 1;
    localObject2[i] += 1;
    return l;
  }
  
  public String nextName()
    throws IOException
  {
    int j = this.boc;
    int i = j;
    if (j == 0) {
      i = bA();
    }
    if (i == 14) {
      str1 = bD();
    }
    for (;;)
    {
      this.boc = 0;
      this.boi[(this.boh - 1)] = str1;
      return str1;
      if (i == 12)
      {
        str1 = zzf('\'');
      }
      else
      {
        if (i != 13) {
          break;
        }
        str1 = zzf('"');
      }
    }
    String str1 = String.valueOf(bn());
    i = getLineNumber();
    j = getColumnNumber();
    String str2 = getPath();
    throw new IllegalStateException(String.valueOf(str1).length() + 69 + String.valueOf(str2).length() + "Expected a name but was " + str1 + " at line " + i + " column " + j + " path " + str2);
  }
  
  public void nextNull()
    throws IOException
  {
    int j = this.boc;
    int i = j;
    if (j == 0) {
      i = bA();
    }
    if (i == 7)
    {
      this.boc = 0;
      localObject = this.boj;
      i = this.boh - 1;
      localObject[i] += 1;
      return;
    }
    Object localObject = String.valueOf(bn());
    i = getLineNumber();
    j = getColumnNumber();
    String str = getPath();
    throw new IllegalStateException(String.valueOf(localObject).length() + 67 + String.valueOf(str).length() + "Expected null but was " + (String)localObject + " at line " + i + " column " + j + " path " + str);
  }
  
  public String nextString()
    throws IOException
  {
    int j = this.boc;
    int i = j;
    if (j == 0) {
      i = bA();
    }
    if (i == 10) {
      str = bD();
    }
    for (;;)
    {
      this.boc = 0;
      localObject = this.boj;
      i = this.boh - 1;
      localObject[i] += 1;
      return str;
      if (i == 8)
      {
        str = zzf('\'');
      }
      else if (i == 9)
      {
        str = zzf('"');
      }
      else if (i == 11)
      {
        str = this.bof;
        this.bof = null;
      }
      else if (i == 15)
      {
        str = Long.toString(this.bod);
      }
      else
      {
        if (i != 16) {
          break;
        }
        str = new String(this.bnZ, this.pos, this.boe);
        this.pos += this.boe;
      }
    }
    String str = String.valueOf(bn());
    i = getLineNumber();
    j = getColumnNumber();
    Object localObject = getPath();
    throw new IllegalStateException(String.valueOf(str).length() + 71 + String.valueOf(localObject).length() + "Expected a string but was " + str + " at line " + i + " column " + j + " path " + (String)localObject);
  }
  
  public final void setLenient(boolean paramBoolean)
  {
    this.bnY = paramBoolean;
  }
  
  public void skipValue()
    throws IOException
  {
    int j = 0;
    int i = this.boc;
    int k = i;
    if (i == 0) {
      k = bA();
    }
    if (k == 3)
    {
      zzagw(1);
      i = j + 1;
    }
    for (;;)
    {
      this.boc = 0;
      j = i;
      if (i != 0) {
        break;
      }
      int[] arrayOfInt = this.boj;
      i = this.boh - 1;
      arrayOfInt[i] += 1;
      this.boi[(this.boh - 1)] = "null";
      return;
      if (k == 1)
      {
        zzagw(3);
        i = j + 1;
      }
      else if (k == 4)
      {
        this.boh -= 1;
        i = j - 1;
      }
      else if (k == 2)
      {
        this.boh -= 1;
        i = j - 1;
      }
      else if ((k == 14) || (k == 10))
      {
        bE();
        i = j;
      }
      else if ((k == 8) || (k == 12))
      {
        zzg('\'');
        i = j;
      }
      else if ((k == 9) || (k == 13))
      {
        zzg('"');
        i = j;
      }
      else
      {
        i = j;
        if (k == 16)
        {
          this.pos += this.boe;
          i = j;
        }
      }
    }
  }
  
  public String toString()
  {
    String str = String.valueOf(getClass().getSimpleName());
    int i = getLineNumber();
    int j = getColumnNumber();
    return String.valueOf(str).length() + 39 + str + " at line " + i + " column " + j;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzapy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */