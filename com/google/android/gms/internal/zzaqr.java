package com.google.android.gms.internal;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.Writer;

public class zzaqr
  implements Closeable, Flushable
{
  private static final String[] brM = new String[''];
  private static final String[] brN;
  private boolean boe;
  private boolean bof;
  private String brO;
  private String brP;
  private boolean brp;
  private int[] brx = new int[32];
  private int bry = 0;
  private final Writer out;
  private String separator;
  
  static
  {
    int i = 0;
    while (i <= 31)
    {
      brM[i] = String.format("\\u%04x", new Object[] { Integer.valueOf(i) });
      i += 1;
    }
    brM[34] = "\\\"";
    brM[92] = "\\\\";
    brM[9] = "\\t";
    brM[8] = "\\b";
    brM[10] = "\\n";
    brM[13] = "\\r";
    brM[12] = "\\f";
    brN = (String[])brM.clone();
    brN[60] = "\\u003c";
    brN[62] = "\\u003e";
    brN[38] = "\\u0026";
    brN[61] = "\\u003d";
    brN[39] = "\\u0027";
  }
  
  public zzaqr(Writer paramWriter)
  {
    zzagn(6);
    this.separator = ":";
    this.boe = true;
    if (paramWriter == null) {
      throw new NullPointerException("out == null");
    }
    this.out = paramWriter;
  }
  
  private int bO()
  {
    if (this.bry == 0) {
      throw new IllegalStateException("JsonWriter is closed.");
    }
    return this.brx[(this.bry - 1)];
  }
  
  private void bP()
    throws IOException
  {
    if (this.brP != null)
    {
      bR();
      zzuw(this.brP);
      this.brP = null;
    }
  }
  
  private void bQ()
    throws IOException
  {
    if (this.brO == null) {}
    for (;;)
    {
      return;
      this.out.write("\n");
      int i = 1;
      int j = this.bry;
      while (i < j)
      {
        this.out.write(this.brO);
        i += 1;
      }
    }
  }
  
  private void bR()
    throws IOException
  {
    int i = bO();
    if (i == 5) {
      this.out.write(44);
    }
    while (i == 3)
    {
      bQ();
      zzagp(4);
      return;
    }
    throw new IllegalStateException("Nesting problem.");
  }
  
  private void zzagn(int paramInt)
  {
    if (this.bry == this.brx.length)
    {
      arrayOfInt = new int[this.bry * 2];
      System.arraycopy(this.brx, 0, arrayOfInt, 0, this.bry);
      this.brx = arrayOfInt;
    }
    int[] arrayOfInt = this.brx;
    int i = this.bry;
    this.bry = (i + 1);
    arrayOfInt[i] = paramInt;
  }
  
  private void zzagp(int paramInt)
  {
    this.brx[(this.bry - 1)] = paramInt;
  }
  
  private zzaqr zzc(int paramInt1, int paramInt2, String paramString)
    throws IOException
  {
    int i = bO();
    if ((i != paramInt2) && (i != paramInt1)) {
      throw new IllegalStateException("Nesting problem.");
    }
    if (this.brP != null)
    {
      paramString = String.valueOf(this.brP);
      if (paramString.length() != 0) {}
      for (paramString = "Dangling name: ".concat(paramString);; paramString = new String("Dangling name: ")) {
        throw new IllegalStateException(paramString);
      }
    }
    this.bry -= 1;
    if (i == paramInt2) {
      bQ();
    }
    this.out.write(paramString);
    return this;
  }
  
  private void zzdl(boolean paramBoolean)
    throws IOException
  {
    switch (bO())
    {
    case 3: 
    case 5: 
    default: 
      throw new IllegalStateException("Nesting problem.");
    case 7: 
      if (!this.brp) {
        throw new IllegalStateException("JSON must have only one top-level value.");
      }
    case 6: 
      if ((!this.brp) && (!paramBoolean)) {
        throw new IllegalStateException("JSON must start with an array or an object.");
      }
      zzagp(7);
      return;
    case 1: 
      zzagp(2);
      bQ();
      return;
    case 2: 
      this.out.append(',');
      bQ();
      return;
    }
    this.out.append(this.separator);
    zzagp(5);
  }
  
  private zzaqr zzp(int paramInt, String paramString)
    throws IOException
  {
    zzdl(true);
    zzagn(paramInt);
    this.out.write(paramString);
    return this;
  }
  
  private void zzuw(String paramString)
    throws IOException
  {
    int j = 0;
    if (this.bof) {}
    int m;
    int i;
    int n;
    int k;
    for (String[] arrayOfString = brN;; arrayOfString = brM)
    {
      this.out.write("\"");
      m = paramString.length();
      i = 0;
      for (;;)
      {
        if (i >= m) {
          break label153;
        }
        n = paramString.charAt(i);
        if (n >= 128) {
          break;
        }
        String str2 = arrayOfString[n];
        str1 = str2;
        if (str2 != null) {
          break label101;
        }
        k = j;
        i += 1;
        j = k;
      }
    }
    if (n == 8232) {}
    for (String str1 = "\\u2028";; str1 = "\\u2029")
    {
      label101:
      if (j < i) {
        this.out.write(paramString, j, i - j);
      }
      this.out.write(str1);
      k = i + 1;
      break;
      k = j;
      if (n != 8233) {
        break;
      }
    }
    label153:
    if (j < m) {
      this.out.write(paramString, j, m - j);
    }
    this.out.write("\"");
  }
  
  public zzaqr bA()
    throws IOException
  {
    if (this.brP != null)
    {
      if (this.boe) {
        bP();
      }
    }
    else
    {
      zzdl(false);
      this.out.write("null");
      return this;
    }
    this.brP = null;
    return this;
  }
  
  public final boolean bM()
  {
    return this.bof;
  }
  
  public final boolean bN()
  {
    return this.boe;
  }
  
  public zzaqr bw()
    throws IOException
  {
    bP();
    return zzp(1, "[");
  }
  
  public zzaqr bx()
    throws IOException
  {
    return zzc(1, 2, "]");
  }
  
  public zzaqr by()
    throws IOException
  {
    bP();
    return zzp(3, "{");
  }
  
  public zzaqr bz()
    throws IOException
  {
    return zzc(3, 5, "}");
  }
  
  public void close()
    throws IOException
  {
    this.out.close();
    int i = this.bry;
    if ((i > 1) || ((i == 1) && (this.brx[(i - 1)] != 7))) {
      throw new IOException("Incomplete document");
    }
    this.bry = 0;
  }
  
  public void flush()
    throws IOException
  {
    if (this.bry == 0) {
      throw new IllegalStateException("JsonWriter is closed.");
    }
    this.out.flush();
  }
  
  public boolean isLenient()
  {
    return this.brp;
  }
  
  public final void setIndent(String paramString)
  {
    if (paramString.length() == 0)
    {
      this.brO = null;
      this.separator = ":";
      return;
    }
    this.brO = paramString;
    this.separator = ": ";
  }
  
  public final void setLenient(boolean paramBoolean)
  {
    this.brp = paramBoolean;
  }
  
  public zzaqr zza(Number paramNumber)
    throws IOException
  {
    if (paramNumber == null) {
      return bA();
    }
    bP();
    String str = paramNumber.toString();
    if ((!this.brp) && ((str.equals("-Infinity")) || (str.equals("Infinity")) || (str.equals("NaN"))))
    {
      paramNumber = String.valueOf(paramNumber);
      throw new IllegalArgumentException(String.valueOf(paramNumber).length() + 39 + "Numeric values must be finite, but was " + paramNumber);
    }
    zzdl(false);
    this.out.append(str);
    return this;
  }
  
  public zzaqr zzcs(long paramLong)
    throws IOException
  {
    bP();
    zzdl(false);
    this.out.write(Long.toString(paramLong));
    return this;
  }
  
  public zzaqr zzdh(boolean paramBoolean)
    throws IOException
  {
    bP();
    zzdl(false);
    Writer localWriter = this.out;
    if (paramBoolean) {}
    for (String str = "true";; str = "false")
    {
      localWriter.write(str);
      return this;
    }
  }
  
  public final void zzdj(boolean paramBoolean)
  {
    this.bof = paramBoolean;
  }
  
  public final void zzdk(boolean paramBoolean)
  {
    this.boe = paramBoolean;
  }
  
  public zzaqr zzus(String paramString)
    throws IOException
  {
    if (paramString == null) {
      throw new NullPointerException("name == null");
    }
    if (this.brP != null) {
      throw new IllegalStateException();
    }
    if (this.bry == 0) {
      throw new IllegalStateException("JsonWriter is closed.");
    }
    this.brP = paramString;
    return this;
  }
  
  public zzaqr zzut(String paramString)
    throws IOException
  {
    if (paramString == null) {
      return bA();
    }
    bP();
    zzdl(false);
    zzuw(paramString);
    return this;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzaqr.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */