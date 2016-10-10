package com.google.android.gms.internal;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.Writer;

public class zzaqa
  implements Closeable, Flushable
{
  private static final String[] bov = new String[''];
  private static final String[] bow;
  private boolean bkN;
  private boolean bkO;
  private boolean bnY;
  private int[] bog = new int[32];
  private int boh = 0;
  private String box;
  private String boy;
  private final Writer out;
  private String separator;
  
  static
  {
    int i = 0;
    while (i <= 31)
    {
      bov[i] = String.format("\\u%04x", new Object[] { Integer.valueOf(i) });
      i += 1;
    }
    bov[34] = "\\\"";
    bov[92] = "\\\\";
    bov[9] = "\\t";
    bov[8] = "\\b";
    bov[10] = "\\n";
    bov[13] = "\\r";
    bov[12] = "\\f";
    bow = (String[])bov.clone();
    bow[60] = "\\u003c";
    bow[62] = "\\u003e";
    bow[38] = "\\u0026";
    bow[61] = "\\u003d";
    bow[39] = "\\u0027";
  }
  
  public zzaqa(Writer paramWriter)
  {
    zzagw(6);
    this.separator = ":";
    this.bkN = true;
    if (paramWriter == null) {
      throw new NullPointerException("out == null");
    }
    this.out = paramWriter;
  }
  
  private int bL()
  {
    if (this.boh == 0) {
      throw new IllegalStateException("JsonWriter is closed.");
    }
    return this.bog[(this.boh - 1)];
  }
  
  private void bM()
    throws IOException
  {
    if (this.boy != null)
    {
      bO();
      zzuw(this.boy);
      this.boy = null;
    }
  }
  
  private void bN()
    throws IOException
  {
    if (this.box == null) {}
    for (;;)
    {
      return;
      this.out.write("\n");
      int i = 1;
      int j = this.boh;
      while (i < j)
      {
        this.out.write(this.box);
        i += 1;
      }
    }
  }
  
  private void bO()
    throws IOException
  {
    int i = bL();
    if (i == 5) {
      this.out.write(44);
    }
    while (i == 3)
    {
      bN();
      zzagy(4);
      return;
    }
    throw new IllegalStateException("Nesting problem.");
  }
  
  private void zzagw(int paramInt)
  {
    if (this.boh == this.bog.length)
    {
      arrayOfInt = new int[this.boh * 2];
      System.arraycopy(this.bog, 0, arrayOfInt, 0, this.boh);
      this.bog = arrayOfInt;
    }
    int[] arrayOfInt = this.bog;
    int i = this.boh;
    this.boh = (i + 1);
    arrayOfInt[i] = paramInt;
  }
  
  private void zzagy(int paramInt)
  {
    this.bog[(this.boh - 1)] = paramInt;
  }
  
  private zzaqa zzc(int paramInt1, int paramInt2, String paramString)
    throws IOException
  {
    int i = bL();
    if ((i != paramInt2) && (i != paramInt1)) {
      throw new IllegalStateException("Nesting problem.");
    }
    if (this.boy != null)
    {
      paramString = String.valueOf(this.boy);
      if (paramString.length() != 0) {}
      for (paramString = "Dangling name: ".concat(paramString);; paramString = new String("Dangling name: ")) {
        throw new IllegalStateException(paramString);
      }
    }
    this.boh -= 1;
    if (i == paramInt2) {
      bN();
    }
    this.out.write(paramString);
    return this;
  }
  
  private void zzdj(boolean paramBoolean)
    throws IOException
  {
    switch (bL())
    {
    case 3: 
    case 5: 
    default: 
      throw new IllegalStateException("Nesting problem.");
    case 7: 
      if (!this.bnY) {
        throw new IllegalStateException("JSON must have only one top-level value.");
      }
    case 6: 
      if ((!this.bnY) && (!paramBoolean)) {
        throw new IllegalStateException("JSON must start with an array or an object.");
      }
      zzagy(7);
      return;
    case 1: 
      zzagy(2);
      bN();
      return;
    case 2: 
      this.out.append(',');
      bN();
      return;
    }
    this.out.append(this.separator);
    zzagy(5);
  }
  
  private zzaqa zzq(int paramInt, String paramString)
    throws IOException
  {
    zzdj(true);
    zzagw(paramInt);
    this.out.write(paramString);
    return this;
  }
  
  private void zzuw(String paramString)
    throws IOException
  {
    int j = 0;
    if (this.bkO) {}
    int m;
    int i;
    int n;
    int k;
    for (String[] arrayOfString = bow;; arrayOfString = bov)
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
  
  public final boolean bJ()
  {
    return this.bkO;
  }
  
  public final boolean bK()
  {
    return this.bkN;
  }
  
  public zzaqa bt()
    throws IOException
  {
    bM();
    return zzq(1, "[");
  }
  
  public zzaqa bu()
    throws IOException
  {
    return zzc(1, 2, "]");
  }
  
  public zzaqa bv()
    throws IOException
  {
    bM();
    return zzq(3, "{");
  }
  
  public zzaqa bw()
    throws IOException
  {
    return zzc(3, 5, "}");
  }
  
  public zzaqa bx()
    throws IOException
  {
    if (this.boy != null)
    {
      if (this.bkN) {
        bM();
      }
    }
    else
    {
      zzdj(false);
      this.out.write("null");
      return this;
    }
    this.boy = null;
    return this;
  }
  
  public void close()
    throws IOException
  {
    this.out.close();
    int i = this.boh;
    if ((i > 1) || ((i == 1) && (this.bog[(i - 1)] != 7))) {
      throw new IOException("Incomplete document");
    }
    this.boh = 0;
  }
  
  public void flush()
    throws IOException
  {
    if (this.boh == 0) {
      throw new IllegalStateException("JsonWriter is closed.");
    }
    this.out.flush();
  }
  
  public boolean isLenient()
  {
    return this.bnY;
  }
  
  public final void setIndent(String paramString)
  {
    if (paramString.length() == 0)
    {
      this.box = null;
      this.separator = ":";
      return;
    }
    this.box = paramString;
    this.separator = ": ";
  }
  
  public final void setLenient(boolean paramBoolean)
  {
    this.bnY = paramBoolean;
  }
  
  public zzaqa zza(Number paramNumber)
    throws IOException
  {
    if (paramNumber == null) {
      return bx();
    }
    bM();
    String str = paramNumber.toString();
    if ((!this.bnY) && ((str.equals("-Infinity")) || (str.equals("Infinity")) || (str.equals("NaN"))))
    {
      paramNumber = String.valueOf(paramNumber);
      throw new IllegalArgumentException(String.valueOf(paramNumber).length() + 39 + "Numeric values must be finite, but was " + paramNumber);
    }
    zzdj(false);
    this.out.append(str);
    return this;
  }
  
  public zzaqa zzcu(long paramLong)
    throws IOException
  {
    bM();
    zzdj(false);
    this.out.write(Long.toString(paramLong));
    return this;
  }
  
  public zzaqa zzdf(boolean paramBoolean)
    throws IOException
  {
    bM();
    zzdj(false);
    Writer localWriter = this.out;
    if (paramBoolean) {}
    for (String str = "true";; str = "false")
    {
      localWriter.write(str);
      return this;
    }
  }
  
  public final void zzdh(boolean paramBoolean)
  {
    this.bkO = paramBoolean;
  }
  
  public final void zzdi(boolean paramBoolean)
  {
    this.bkN = paramBoolean;
  }
  
  public zzaqa zzus(String paramString)
    throws IOException
  {
    if (paramString == null) {
      throw new NullPointerException("name == null");
    }
    if (this.boy != null) {
      throw new IllegalStateException();
    }
    if (this.boh == 0) {
      throw new IllegalStateException("JsonWriter is closed.");
    }
    this.boy = paramString;
    return this;
  }
  
  public zzaqa zzut(String paramString)
    throws IOException
  {
    if (paramString == null) {
      return bx();
    }
    bM();
    zzdj(false);
    zzuw(paramString);
    return this;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzaqa.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */