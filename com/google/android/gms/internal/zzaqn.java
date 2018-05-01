package com.google.android.gms.internal;

import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.UUID;

public final class zzaqn
{
  public static final zzapl bqA = zza(Number.class, bqz);
  public static final zzapk<Character> bqB = new zzapk()
  {
    public void zza(zzaqr paramAnonymouszzaqr, Character paramAnonymousCharacter)
      throws IOException
    {
      if (paramAnonymousCharacter == null) {}
      for (paramAnonymousCharacter = null;; paramAnonymousCharacter = String.valueOf(paramAnonymousCharacter))
      {
        paramAnonymouszzaqr.zzut(paramAnonymousCharacter);
        return;
      }
    }
    
    public Character zzp(zzaqp paramAnonymouszzaqp)
      throws IOException
    {
      if (paramAnonymouszzaqp.bq() == zzaqq.brJ)
      {
        paramAnonymouszzaqp.nextNull();
        return null;
      }
      paramAnonymouszzaqp = paramAnonymouszzaqp.nextString();
      if (paramAnonymouszzaqp.length() != 1)
      {
        paramAnonymouszzaqp = String.valueOf(paramAnonymouszzaqp);
        if (paramAnonymouszzaqp.length() != 0) {}
        for (paramAnonymouszzaqp = "Expecting character, got: ".concat(paramAnonymouszzaqp);; paramAnonymouszzaqp = new String("Expecting character, got: ")) {
          throw new zzaph(paramAnonymouszzaqp);
        }
      }
      return Character.valueOf(paramAnonymouszzaqp.charAt(0));
    }
  };
  public static final zzapl bqC = zza(Character.TYPE, Character.class, bqB);
  public static final zzapk<String> bqD = new zzapk()
  {
    public void zza(zzaqr paramAnonymouszzaqr, String paramAnonymousString)
      throws IOException
    {
      paramAnonymouszzaqr.zzut(paramAnonymousString);
    }
    
    public String zzq(zzaqp paramAnonymouszzaqp)
      throws IOException
    {
      zzaqq localzzaqq = paramAnonymouszzaqp.bq();
      if (localzzaqq == zzaqq.brJ)
      {
        paramAnonymouszzaqp.nextNull();
        return null;
      }
      if (localzzaqq == zzaqq.brI) {
        return Boolean.toString(paramAnonymouszzaqp.nextBoolean());
      }
      return paramAnonymouszzaqp.nextString();
    }
  };
  public static final zzapk<BigDecimal> bqE = new zzapk()
  {
    public void zza(zzaqr paramAnonymouszzaqr, BigDecimal paramAnonymousBigDecimal)
      throws IOException
    {
      paramAnonymouszzaqr.zza(paramAnonymousBigDecimal);
    }
    
    public BigDecimal zzr(zzaqp paramAnonymouszzaqp)
      throws IOException
    {
      if (paramAnonymouszzaqp.bq() == zzaqq.brJ)
      {
        paramAnonymouszzaqp.nextNull();
        return null;
      }
      try
      {
        paramAnonymouszzaqp = new BigDecimal(paramAnonymouszzaqp.nextString());
        return paramAnonymouszzaqp;
      }
      catch (NumberFormatException paramAnonymouszzaqp)
      {
        throw new zzaph(paramAnonymouszzaqp);
      }
    }
  };
  public static final zzapk<BigInteger> bqF = new zzapk()
  {
    public void zza(zzaqr paramAnonymouszzaqr, BigInteger paramAnonymousBigInteger)
      throws IOException
    {
      paramAnonymouszzaqr.zza(paramAnonymousBigInteger);
    }
    
    public BigInteger zzs(zzaqp paramAnonymouszzaqp)
      throws IOException
    {
      if (paramAnonymouszzaqp.bq() == zzaqq.brJ)
      {
        paramAnonymouszzaqp.nextNull();
        return null;
      }
      try
      {
        paramAnonymouszzaqp = new BigInteger(paramAnonymouszzaqp.nextString());
        return paramAnonymouszzaqp;
      }
      catch (NumberFormatException paramAnonymouszzaqp)
      {
        throw new zzaph(paramAnonymouszzaqp);
      }
    }
  };
  public static final zzapl bqG = zza(String.class, bqD);
  public static final zzapk<StringBuilder> bqH = new zzapk()
  {
    public void zza(zzaqr paramAnonymouszzaqr, StringBuilder paramAnonymousStringBuilder)
      throws IOException
    {
      if (paramAnonymousStringBuilder == null) {}
      for (paramAnonymousStringBuilder = null;; paramAnonymousStringBuilder = paramAnonymousStringBuilder.toString())
      {
        paramAnonymouszzaqr.zzut(paramAnonymousStringBuilder);
        return;
      }
    }
    
    public StringBuilder zzt(zzaqp paramAnonymouszzaqp)
      throws IOException
    {
      if (paramAnonymouszzaqp.bq() == zzaqq.brJ)
      {
        paramAnonymouszzaqp.nextNull();
        return null;
      }
      return new StringBuilder(paramAnonymouszzaqp.nextString());
    }
  };
  public static final zzapl bqI = zza(StringBuilder.class, bqH);
  public static final zzapk<StringBuffer> bqJ = new zzapk()
  {
    public void zza(zzaqr paramAnonymouszzaqr, StringBuffer paramAnonymousStringBuffer)
      throws IOException
    {
      if (paramAnonymousStringBuffer == null) {}
      for (paramAnonymousStringBuffer = null;; paramAnonymousStringBuffer = paramAnonymousStringBuffer.toString())
      {
        paramAnonymouszzaqr.zzut(paramAnonymousStringBuffer);
        return;
      }
    }
    
    public StringBuffer zzu(zzaqp paramAnonymouszzaqp)
      throws IOException
    {
      if (paramAnonymouszzaqp.bq() == zzaqq.brJ)
      {
        paramAnonymouszzaqp.nextNull();
        return null;
      }
      return new StringBuffer(paramAnonymouszzaqp.nextString());
    }
  };
  public static final zzapl bqK = zza(StringBuffer.class, bqJ);
  public static final zzapk<URL> bqL = new zzapk()
  {
    public void zza(zzaqr paramAnonymouszzaqr, URL paramAnonymousURL)
      throws IOException
    {
      if (paramAnonymousURL == null) {}
      for (paramAnonymousURL = null;; paramAnonymousURL = paramAnonymousURL.toExternalForm())
      {
        paramAnonymouszzaqr.zzut(paramAnonymousURL);
        return;
      }
    }
    
    public URL zzv(zzaqp paramAnonymouszzaqp)
      throws IOException
    {
      if (paramAnonymouszzaqp.bq() == zzaqq.brJ) {
        paramAnonymouszzaqp.nextNull();
      }
      do
      {
        return null;
        paramAnonymouszzaqp = paramAnonymouszzaqp.nextString();
      } while ("null".equals(paramAnonymouszzaqp));
      return new URL(paramAnonymouszzaqp);
    }
  };
  public static final zzapl bqM = zza(URL.class, bqL);
  public static final zzapk<URI> bqN = new zzapk()
  {
    public void zza(zzaqr paramAnonymouszzaqr, URI paramAnonymousURI)
      throws IOException
    {
      if (paramAnonymousURI == null) {}
      for (paramAnonymousURI = null;; paramAnonymousURI = paramAnonymousURI.toASCIIString())
      {
        paramAnonymouszzaqr.zzut(paramAnonymousURI);
        return;
      }
    }
    
    public URI zzw(zzaqp paramAnonymouszzaqp)
      throws IOException
    {
      if (paramAnonymouszzaqp.bq() == zzaqq.brJ) {
        paramAnonymouszzaqp.nextNull();
      }
      for (;;)
      {
        return null;
        try
        {
          paramAnonymouszzaqp = paramAnonymouszzaqp.nextString();
          if ("null".equals(paramAnonymouszzaqp)) {
            continue;
          }
          paramAnonymouszzaqp = new URI(paramAnonymouszzaqp);
          return paramAnonymouszzaqp;
        }
        catch (URISyntaxException paramAnonymouszzaqp)
        {
          throw new zzaoz(paramAnonymouszzaqp);
        }
      }
    }
  };
  public static final zzapl bqO = zza(URI.class, bqN);
  public static final zzapk<InetAddress> bqP = new zzapk()
  {
    public void zza(zzaqr paramAnonymouszzaqr, InetAddress paramAnonymousInetAddress)
      throws IOException
    {
      if (paramAnonymousInetAddress == null) {}
      for (paramAnonymousInetAddress = null;; paramAnonymousInetAddress = paramAnonymousInetAddress.getHostAddress())
      {
        paramAnonymouszzaqr.zzut(paramAnonymousInetAddress);
        return;
      }
    }
    
    public InetAddress zzy(zzaqp paramAnonymouszzaqp)
      throws IOException
    {
      if (paramAnonymouszzaqp.bq() == zzaqq.brJ)
      {
        paramAnonymouszzaqp.nextNull();
        return null;
      }
      return InetAddress.getByName(paramAnonymouszzaqp.nextString());
    }
  };
  public static final zzapl bqQ = zzb(InetAddress.class, bqP);
  public static final zzapk<UUID> bqR = new zzapk()
  {
    public void zza(zzaqr paramAnonymouszzaqr, UUID paramAnonymousUUID)
      throws IOException
    {
      if (paramAnonymousUUID == null) {}
      for (paramAnonymousUUID = null;; paramAnonymousUUID = paramAnonymousUUID.toString())
      {
        paramAnonymouszzaqr.zzut(paramAnonymousUUID);
        return;
      }
    }
    
    public UUID zzz(zzaqp paramAnonymouszzaqp)
      throws IOException
    {
      if (paramAnonymouszzaqp.bq() == zzaqq.brJ)
      {
        paramAnonymouszzaqp.nextNull();
        return null;
      }
      return UUID.fromString(paramAnonymouszzaqp.nextString());
    }
  };
  public static final zzapl bqS = zza(UUID.class, bqR);
  public static final zzapl bqT = new zzapl()
  {
    public <T> zzapk<T> zza(zzaos paramAnonymouszzaos, zzaqo<T> paramAnonymouszzaqo)
    {
      if (paramAnonymouszzaqo.bB() != Timestamp.class) {
        return null;
      }
      new zzapk()
      {
        public void zza(zzaqr paramAnonymous2zzaqr, Timestamp paramAnonymous2Timestamp)
          throws IOException
        {
          this.brb.zza(paramAnonymous2zzaqr, paramAnonymous2Timestamp);
        }
        
        public Timestamp zzaa(zzaqp paramAnonymous2zzaqp)
          throws IOException
        {
          paramAnonymous2zzaqp = (Date)this.brb.zzb(paramAnonymous2zzaqp);
          if (paramAnonymous2zzaqp != null) {
            return new Timestamp(paramAnonymous2zzaqp.getTime());
          }
          return null;
        }
      };
    }
  };
  public static final zzapk<Calendar> bqU = new zzapk()
  {
    public void zza(zzaqr paramAnonymouszzaqr, Calendar paramAnonymousCalendar)
      throws IOException
    {
      if (paramAnonymousCalendar == null)
      {
        paramAnonymouszzaqr.bA();
        return;
      }
      paramAnonymouszzaqr.by();
      paramAnonymouszzaqr.zzus("year");
      paramAnonymouszzaqr.zzcs(paramAnonymousCalendar.get(1));
      paramAnonymouszzaqr.zzus("month");
      paramAnonymouszzaqr.zzcs(paramAnonymousCalendar.get(2));
      paramAnonymouszzaqr.zzus("dayOfMonth");
      paramAnonymouszzaqr.zzcs(paramAnonymousCalendar.get(5));
      paramAnonymouszzaqr.zzus("hourOfDay");
      paramAnonymouszzaqr.zzcs(paramAnonymousCalendar.get(11));
      paramAnonymouszzaqr.zzus("minute");
      paramAnonymouszzaqr.zzcs(paramAnonymousCalendar.get(12));
      paramAnonymouszzaqr.zzus("second");
      paramAnonymouszzaqr.zzcs(paramAnonymousCalendar.get(13));
      paramAnonymouszzaqr.bz();
    }
    
    public Calendar zzab(zzaqp paramAnonymouszzaqp)
      throws IOException
    {
      int j = 0;
      if (paramAnonymouszzaqp.bq() == zzaqq.brJ)
      {
        paramAnonymouszzaqp.nextNull();
        return null;
      }
      paramAnonymouszzaqp.beginObject();
      int k = 0;
      int m = 0;
      int n = 0;
      int i1 = 0;
      int i2 = 0;
      while (paramAnonymouszzaqp.bq() != zzaqq.brE)
      {
        String str = paramAnonymouszzaqp.nextName();
        int i = paramAnonymouszzaqp.nextInt();
        if ("year".equals(str)) {
          i2 = i;
        } else if ("month".equals(str)) {
          i1 = i;
        } else if ("dayOfMonth".equals(str)) {
          n = i;
        } else if ("hourOfDay".equals(str)) {
          m = i;
        } else if ("minute".equals(str)) {
          k = i;
        } else if ("second".equals(str)) {
          j = i;
        }
      }
      paramAnonymouszzaqp.endObject();
      return new GregorianCalendar(i2, i1, n, m, k, j);
    }
  };
  public static final zzapl bqV = zzb(Calendar.class, GregorianCalendar.class, bqU);
  public static final zzapk<Locale> bqW = new zzapk()
  {
    public void zza(zzaqr paramAnonymouszzaqr, Locale paramAnonymousLocale)
      throws IOException
    {
      if (paramAnonymousLocale == null) {}
      for (paramAnonymousLocale = null;; paramAnonymousLocale = paramAnonymousLocale.toString())
      {
        paramAnonymouszzaqr.zzut(paramAnonymousLocale);
        return;
      }
    }
    
    public Locale zzac(zzaqp paramAnonymouszzaqp)
      throws IOException
    {
      if (paramAnonymouszzaqp.bq() == zzaqq.brJ)
      {
        paramAnonymouszzaqp.nextNull();
        return null;
      }
      Object localObject = new StringTokenizer(paramAnonymouszzaqp.nextString(), "_");
      if (((StringTokenizer)localObject).hasMoreElements()) {}
      for (paramAnonymouszzaqp = ((StringTokenizer)localObject).nextToken();; paramAnonymouszzaqp = null)
      {
        if (((StringTokenizer)localObject).hasMoreElements()) {}
        for (String str = ((StringTokenizer)localObject).nextToken();; str = null)
        {
          if (((StringTokenizer)localObject).hasMoreElements()) {}
          for (localObject = ((StringTokenizer)localObject).nextToken();; localObject = null)
          {
            if ((str == null) && (localObject == null)) {
              return new Locale(paramAnonymouszzaqp);
            }
            if (localObject == null) {
              return new Locale(paramAnonymouszzaqp, str);
            }
            return new Locale(paramAnonymouszzaqp, str, (String)localObject);
          }
        }
      }
    }
  };
  public static final zzapl bqX = zza(Locale.class, bqW);
  public static final zzapk<zzaoy> bqY = new zzapk()
  {
    public void zza(zzaqr paramAnonymouszzaqr, zzaoy paramAnonymouszzaoy)
      throws IOException
    {
      if ((paramAnonymouszzaoy == null) || (paramAnonymouszzaoy.aY()))
      {
        paramAnonymouszzaqr.bA();
        return;
      }
      if (paramAnonymouszzaoy.aX())
      {
        paramAnonymouszzaoy = paramAnonymouszzaoy.bb();
        if (paramAnonymouszzaoy.be())
        {
          paramAnonymouszzaqr.zza(paramAnonymouszzaoy.aT());
          return;
        }
        if (paramAnonymouszzaoy.bd())
        {
          paramAnonymouszzaqr.zzdh(paramAnonymouszzaoy.getAsBoolean());
          return;
        }
        paramAnonymouszzaqr.zzut(paramAnonymouszzaoy.aU());
        return;
      }
      if (paramAnonymouszzaoy.aV())
      {
        paramAnonymouszzaqr.bw();
        paramAnonymouszzaoy = paramAnonymouszzaoy.ba().iterator();
        while (paramAnonymouszzaoy.hasNext()) {
          zza(paramAnonymouszzaqr, (zzaoy)paramAnonymouszzaoy.next());
        }
        paramAnonymouszzaqr.bx();
        return;
      }
      if (paramAnonymouszzaoy.aW())
      {
        paramAnonymouszzaqr.by();
        paramAnonymouszzaoy = paramAnonymouszzaoy.aZ().entrySet().iterator();
        while (paramAnonymouszzaoy.hasNext())
        {
          Map.Entry localEntry = (Map.Entry)paramAnonymouszzaoy.next();
          paramAnonymouszzaqr.zzus((String)localEntry.getKey());
          zza(paramAnonymouszzaqr, (zzaoy)localEntry.getValue());
        }
        paramAnonymouszzaqr.bz();
        return;
      }
      paramAnonymouszzaqr = String.valueOf(paramAnonymouszzaoy.getClass());
      throw new IllegalArgumentException(String.valueOf(paramAnonymouszzaqr).length() + 15 + "Couldn't write " + paramAnonymouszzaqr);
    }
    
    public zzaoy zzad(zzaqp paramAnonymouszzaqp)
      throws IOException
    {
      switch (zzaqn.26.bpW[paramAnonymouszzaqp.bq().ordinal()])
      {
      default: 
        throw new IllegalArgumentException();
      case 3: 
        return new zzape(paramAnonymouszzaqp.nextString());
      case 1: 
        return new zzape(new zzapv(paramAnonymouszzaqp.nextString()));
      case 2: 
        return new zzape(Boolean.valueOf(paramAnonymouszzaqp.nextBoolean()));
      case 4: 
        paramAnonymouszzaqp.nextNull();
        return zzapa.bou;
      case 5: 
        localObject = new zzaov();
        paramAnonymouszzaqp.beginArray();
        while (paramAnonymouszzaqp.hasNext()) {
          ((zzaov)localObject).zzc((zzaoy)zzb(paramAnonymouszzaqp));
        }
        paramAnonymouszzaqp.endArray();
        return (zzaoy)localObject;
      }
      Object localObject = new zzapb();
      paramAnonymouszzaqp.beginObject();
      while (paramAnonymouszzaqp.hasNext()) {
        ((zzapb)localObject).zza(paramAnonymouszzaqp.nextName(), (zzaoy)zzb(paramAnonymouszzaqp));
      }
      paramAnonymouszzaqp.endObject();
      return (zzaoy)localObject;
    }
  };
  public static final zzapl bqZ = zzb(zzaoy.class, bqY);
  public static final zzapk<Class> bqj = new zzapk()
  {
    public void zza(zzaqr paramAnonymouszzaqr, Class paramAnonymousClass)
      throws IOException
    {
      if (paramAnonymousClass == null)
      {
        paramAnonymouszzaqr.bA();
        return;
      }
      paramAnonymouszzaqr = String.valueOf(paramAnonymousClass.getName());
      throw new UnsupportedOperationException(String.valueOf(paramAnonymouszzaqr).length() + 76 + "Attempted to serialize java.lang.Class: " + paramAnonymouszzaqr + ". Forgot to register a type adapter?");
    }
    
    public Class zzo(zzaqp paramAnonymouszzaqp)
      throws IOException
    {
      if (paramAnonymouszzaqp.bq() == zzaqq.brJ)
      {
        paramAnonymouszzaqp.nextNull();
        return null;
      }
      throw new UnsupportedOperationException("Attempted to deserialize a java.lang.Class. Forgot to register a type adapter?");
    }
  };
  public static final zzapl bqk = zza(Class.class, bqj);
  public static final zzapk<BitSet> bql = new zzapk()
  {
    public void zza(zzaqr paramAnonymouszzaqr, BitSet paramAnonymousBitSet)
      throws IOException
    {
      if (paramAnonymousBitSet == null)
      {
        paramAnonymouszzaqr.bA();
        return;
      }
      paramAnonymouszzaqr.bw();
      int i = 0;
      if (i < paramAnonymousBitSet.length())
      {
        if (paramAnonymousBitSet.get(i)) {}
        for (int j = 1;; j = 0)
        {
          paramAnonymouszzaqr.zzcs(j);
          i += 1;
          break;
        }
      }
      paramAnonymouszzaqr.bx();
    }
    
    public BitSet zzx(zzaqp paramAnonymouszzaqp)
      throws IOException
    {
      if (paramAnonymouszzaqp.bq() == zzaqq.brJ)
      {
        paramAnonymouszzaqp.nextNull();
        return null;
      }
      BitSet localBitSet = new BitSet();
      paramAnonymouszzaqp.beginArray();
      Object localObject = paramAnonymouszzaqp.bq();
      int i = 0;
      if (localObject != zzaqq.brC)
      {
        boolean bool;
        switch (zzaqn.26.bpW[localObject.ordinal()])
        {
        default: 
          paramAnonymouszzaqp = String.valueOf(localObject);
          throw new zzaph(String.valueOf(paramAnonymouszzaqp).length() + 27 + "Invalid bitset value type: " + paramAnonymouszzaqp);
        case 1: 
          if (paramAnonymouszzaqp.nextInt() != 0) {
            bool = true;
          }
          break;
        }
        for (;;)
        {
          if (bool) {
            localBitSet.set(i);
          }
          i += 1;
          localObject = paramAnonymouszzaqp.bq();
          break;
          bool = false;
          continue;
          bool = paramAnonymouszzaqp.nextBoolean();
          continue;
          localObject = paramAnonymouszzaqp.nextString();
          try
          {
            int j = Integer.parseInt((String)localObject);
            if (j != 0) {
              bool = true;
            } else {
              bool = false;
            }
          }
          catch (NumberFormatException paramAnonymouszzaqp)
          {
            paramAnonymouszzaqp = String.valueOf(localObject);
            if (paramAnonymouszzaqp.length() == 0) {}
          }
        }
        for (paramAnonymouszzaqp = "Error: Expecting: bitset number value (1, 0), Found: ".concat(paramAnonymouszzaqp);; paramAnonymouszzaqp = new String("Error: Expecting: bitset number value (1, 0), Found: ")) {
          throw new zzaph(paramAnonymouszzaqp);
        }
      }
      paramAnonymouszzaqp.endArray();
      return localBitSet;
    }
  };
  public static final zzapl bqm = zza(BitSet.class, bql);
  public static final zzapk<Boolean> bqn = new zzapk()
  {
    public void zza(zzaqr paramAnonymouszzaqr, Boolean paramAnonymousBoolean)
      throws IOException
    {
      if (paramAnonymousBoolean == null)
      {
        paramAnonymouszzaqr.bA();
        return;
      }
      paramAnonymouszzaqr.zzdh(paramAnonymousBoolean.booleanValue());
    }
    
    public Boolean zzae(zzaqp paramAnonymouszzaqp)
      throws IOException
    {
      if (paramAnonymouszzaqp.bq() == zzaqq.brJ)
      {
        paramAnonymouszzaqp.nextNull();
        return null;
      }
      if (paramAnonymouszzaqp.bq() == zzaqq.brG) {
        return Boolean.valueOf(Boolean.parseBoolean(paramAnonymouszzaqp.nextString()));
      }
      return Boolean.valueOf(paramAnonymouszzaqp.nextBoolean());
    }
  };
  public static final zzapk<Boolean> bqo = new zzapk()
  {
    public void zza(zzaqr paramAnonymouszzaqr, Boolean paramAnonymousBoolean)
      throws IOException
    {
      if (paramAnonymousBoolean == null) {}
      for (paramAnonymousBoolean = "null";; paramAnonymousBoolean = paramAnonymousBoolean.toString())
      {
        paramAnonymouszzaqr.zzut(paramAnonymousBoolean);
        return;
      }
    }
    
    public Boolean zzae(zzaqp paramAnonymouszzaqp)
      throws IOException
    {
      if (paramAnonymouszzaqp.bq() == zzaqq.brJ)
      {
        paramAnonymouszzaqp.nextNull();
        return null;
      }
      return Boolean.valueOf(paramAnonymouszzaqp.nextString());
    }
  };
  public static final zzapl bqp = zza(Boolean.TYPE, Boolean.class, bqn);
  public static final zzapk<Number> bqq = new zzapk()
  {
    public void zza(zzaqr paramAnonymouszzaqr, Number paramAnonymousNumber)
      throws IOException
    {
      paramAnonymouszzaqr.zza(paramAnonymousNumber);
    }
    
    public Number zzg(zzaqp paramAnonymouszzaqp)
      throws IOException
    {
      if (paramAnonymouszzaqp.bq() == zzaqq.brJ)
      {
        paramAnonymouszzaqp.nextNull();
        return null;
      }
      try
      {
        byte b = (byte)paramAnonymouszzaqp.nextInt();
        return Byte.valueOf(b);
      }
      catch (NumberFormatException paramAnonymouszzaqp)
      {
        throw new zzaph(paramAnonymouszzaqp);
      }
    }
  };
  public static final zzapl bqr = zza(Byte.TYPE, Byte.class, bqq);
  public static final zzapk<Number> bqs = new zzapk()
  {
    public void zza(zzaqr paramAnonymouszzaqr, Number paramAnonymousNumber)
      throws IOException
    {
      paramAnonymouszzaqr.zza(paramAnonymousNumber);
    }
    
    public Number zzg(zzaqp paramAnonymouszzaqp)
      throws IOException
    {
      if (paramAnonymouszzaqp.bq() == zzaqq.brJ)
      {
        paramAnonymouszzaqp.nextNull();
        return null;
      }
      try
      {
        short s = (short)paramAnonymouszzaqp.nextInt();
        return Short.valueOf(s);
      }
      catch (NumberFormatException paramAnonymouszzaqp)
      {
        throw new zzaph(paramAnonymouszzaqp);
      }
    }
  };
  public static final zzapl bqt = zza(Short.TYPE, Short.class, bqs);
  public static final zzapk<Number> bqu = new zzapk()
  {
    public void zza(zzaqr paramAnonymouszzaqr, Number paramAnonymousNumber)
      throws IOException
    {
      paramAnonymouszzaqr.zza(paramAnonymousNumber);
    }
    
    public Number zzg(zzaqp paramAnonymouszzaqp)
      throws IOException
    {
      if (paramAnonymouszzaqp.bq() == zzaqq.brJ)
      {
        paramAnonymouszzaqp.nextNull();
        return null;
      }
      try
      {
        int i = paramAnonymouszzaqp.nextInt();
        return Integer.valueOf(i);
      }
      catch (NumberFormatException paramAnonymouszzaqp)
      {
        throw new zzaph(paramAnonymouszzaqp);
      }
    }
  };
  public static final zzapl bqv = zza(Integer.TYPE, Integer.class, bqu);
  public static final zzapk<Number> bqw = new zzapk()
  {
    public void zza(zzaqr paramAnonymouszzaqr, Number paramAnonymousNumber)
      throws IOException
    {
      paramAnonymouszzaqr.zza(paramAnonymousNumber);
    }
    
    public Number zzg(zzaqp paramAnonymouszzaqp)
      throws IOException
    {
      if (paramAnonymouszzaqp.bq() == zzaqq.brJ)
      {
        paramAnonymouszzaqp.nextNull();
        return null;
      }
      try
      {
        long l = paramAnonymouszzaqp.nextLong();
        return Long.valueOf(l);
      }
      catch (NumberFormatException paramAnonymouszzaqp)
      {
        throw new zzaph(paramAnonymouszzaqp);
      }
    }
  };
  public static final zzapk<Number> bqx = new zzapk()
  {
    public void zza(zzaqr paramAnonymouszzaqr, Number paramAnonymousNumber)
      throws IOException
    {
      paramAnonymouszzaqr.zza(paramAnonymousNumber);
    }
    
    public Number zzg(zzaqp paramAnonymouszzaqp)
      throws IOException
    {
      if (paramAnonymouszzaqp.bq() == zzaqq.brJ)
      {
        paramAnonymouszzaqp.nextNull();
        return null;
      }
      return Float.valueOf((float)paramAnonymouszzaqp.nextDouble());
    }
  };
  public static final zzapk<Number> bqy = new zzapk()
  {
    public void zza(zzaqr paramAnonymouszzaqr, Number paramAnonymousNumber)
      throws IOException
    {
      paramAnonymouszzaqr.zza(paramAnonymousNumber);
    }
    
    public Number zzg(zzaqp paramAnonymouszzaqp)
      throws IOException
    {
      if (paramAnonymouszzaqp.bq() == zzaqq.brJ)
      {
        paramAnonymouszzaqp.nextNull();
        return null;
      }
      return Double.valueOf(paramAnonymouszzaqp.nextDouble());
    }
  };
  public static final zzapk<Number> bqz = new zzapk()
  {
    public void zza(zzaqr paramAnonymouszzaqr, Number paramAnonymousNumber)
      throws IOException
    {
      paramAnonymouszzaqr.zza(paramAnonymousNumber);
    }
    
    public Number zzg(zzaqp paramAnonymouszzaqp)
      throws IOException
    {
      zzaqq localzzaqq = paramAnonymouszzaqp.bq();
      switch (zzaqn.26.bpW[localzzaqq.ordinal()])
      {
      case 2: 
      case 3: 
      default: 
        paramAnonymouszzaqp = String.valueOf(localzzaqq);
        throw new zzaph(String.valueOf(paramAnonymouszzaqp).length() + 23 + "Expecting number, got: " + paramAnonymouszzaqp);
      case 4: 
        paramAnonymouszzaqp.nextNull();
        return null;
      }
      return new zzapv(paramAnonymouszzaqp.nextString());
    }
  };
  public static final zzapl bra = new zzapl()
  {
    public <T> zzapk<T> zza(zzaos paramAnonymouszzaos, zzaqo<T> paramAnonymouszzaqo)
    {
      paramAnonymouszzaqo = paramAnonymouszzaqo.bB();
      if ((!Enum.class.isAssignableFrom(paramAnonymouszzaqo)) || (paramAnonymouszzaqo == Enum.class)) {
        return null;
      }
      paramAnonymouszzaos = paramAnonymouszzaqo;
      if (!paramAnonymouszzaqo.isEnum()) {
        paramAnonymouszzaos = paramAnonymouszzaqo.getSuperclass();
      }
      return new zzaqn.zza(paramAnonymouszzaos);
    }
  };
  
  public static <TT> zzapl zza(zzaqo<TT> paramzzaqo, final zzapk<TT> paramzzapk)
  {
    new zzapl()
    {
      public <T> zzapk<T> zza(zzaos paramAnonymouszzaos, zzaqo<T> paramAnonymouszzaqo)
      {
        if (paramAnonymouszzaqo.equals(this.bpf)) {
          return paramzzapk;
        }
        return null;
      }
    };
  }
  
  public static <TT> zzapl zza(Class<TT> paramClass, final zzapk<TT> paramzzapk)
  {
    new zzapl()
    {
      public String toString()
      {
        String str1 = String.valueOf(this.bre.getName());
        String str2 = String.valueOf(paramzzapk);
        return String.valueOf(str1).length() + 23 + String.valueOf(str2).length() + "Factory[type=" + str1 + ",adapter=" + str2 + "]";
      }
      
      public <T> zzapk<T> zza(zzaos paramAnonymouszzaos, zzaqo<T> paramAnonymouszzaqo)
      {
        if (paramAnonymouszzaqo.bB() == this.bre) {
          return paramzzapk;
        }
        return null;
      }
    };
  }
  
  public static <TT> zzapl zza(Class<TT> paramClass1, final Class<TT> paramClass2, final zzapk<? super TT> paramzzapk)
  {
    new zzapl()
    {
      public String toString()
      {
        String str1 = String.valueOf(paramClass2.getName());
        String str2 = String.valueOf(this.brf.getName());
        String str3 = String.valueOf(paramzzapk);
        return String.valueOf(str1).length() + 24 + String.valueOf(str2).length() + String.valueOf(str3).length() + "Factory[type=" + str1 + "+" + str2 + ",adapter=" + str3 + "]";
      }
      
      public <T> zzapk<T> zza(zzaos paramAnonymouszzaos, zzaqo<T> paramAnonymouszzaqo)
      {
        paramAnonymouszzaos = paramAnonymouszzaqo.bB();
        if ((paramAnonymouszzaos == this.brf) || (paramAnonymouszzaos == paramClass2)) {
          return paramzzapk;
        }
        return null;
      }
    };
  }
  
  public static <TT> zzapl zzb(Class<TT> paramClass, final zzapk<TT> paramzzapk)
  {
    new zzapl()
    {
      public String toString()
      {
        String str1 = String.valueOf(this.brj.getName());
        String str2 = String.valueOf(paramzzapk);
        return String.valueOf(str1).length() + 32 + String.valueOf(str2).length() + "Factory[typeHierarchy=" + str1 + ",adapter=" + str2 + "]";
      }
      
      public <T> zzapk<T> zza(zzaos paramAnonymouszzaos, zzaqo<T> paramAnonymouszzaqo)
      {
        if (this.brj.isAssignableFrom(paramAnonymouszzaqo.bB())) {
          return paramzzapk;
        }
        return null;
      }
    };
  }
  
  public static <TT> zzapl zzb(Class<TT> paramClass, final Class<? extends TT> paramClass1, final zzapk<? super TT> paramzzapk)
  {
    new zzapl()
    {
      public String toString()
      {
        String str1 = String.valueOf(this.brh.getName());
        String str2 = String.valueOf(paramClass1.getName());
        String str3 = String.valueOf(paramzzapk);
        return String.valueOf(str1).length() + 24 + String.valueOf(str2).length() + String.valueOf(str3).length() + "Factory[type=" + str1 + "+" + str2 + ",adapter=" + str3 + "]";
      }
      
      public <T> zzapk<T> zza(zzaos paramAnonymouszzaos, zzaqo<T> paramAnonymouszzaqo)
      {
        paramAnonymouszzaos = paramAnonymouszzaqo.bB();
        if ((paramAnonymouszzaos == this.brh) || (paramAnonymouszzaos == paramClass1)) {
          return paramzzapk;
        }
        return null;
      }
    };
  }
  
  private static final class zza<T extends Enum<T>>
    extends zzapk<T>
  {
    private final Map<String, T> brk = new HashMap();
    private final Map<T, String> brl = new HashMap();
    
    public zza(Class<T> paramClass)
    {
      try
      {
        Enum[] arrayOfEnum = (Enum[])paramClass.getEnumConstants();
        int k = arrayOfEnum.length;
        int i = 0;
        while (i < k)
        {
          Enum localEnum = arrayOfEnum[i];
          Object localObject1 = localEnum.name();
          Object localObject2 = (zzapn)paramClass.getField((String)localObject1).getAnnotation(zzapn.class);
          if (localObject2 != null)
          {
            String str = ((zzapn)localObject2).value();
            localObject2 = ((zzapn)localObject2).bh();
            int m = localObject2.length;
            int j = 0;
            for (;;)
            {
              localObject1 = str;
              if (j >= m) {
                break;
              }
              localObject1 = localObject2[j];
              this.brk.put(localObject1, localEnum);
              j += 1;
            }
          }
          this.brk.put(localObject1, localEnum);
          this.brl.put(localEnum, localObject1);
          i += 1;
        }
        return;
      }
      catch (NoSuchFieldException paramClass)
      {
        throw new AssertionError();
      }
    }
    
    public void zza(zzaqr paramzzaqr, T paramT)
      throws IOException
    {
      if (paramT == null) {}
      for (paramT = null;; paramT = (String)this.brl.get(paramT))
      {
        paramzzaqr.zzut(paramT);
        return;
      }
    }
    
    public T zzaf(zzaqp paramzzaqp)
      throws IOException
    {
      if (paramzzaqp.bq() == zzaqq.brJ)
      {
        paramzzaqp.nextNull();
        return null;
      }
      return (Enum)this.brk.get(paramzzaqp.nextString());
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzaqn.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */