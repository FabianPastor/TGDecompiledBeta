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

public final class zzapw
{
  public static final zzaot<Class> bmS = new zzaot()
  {
    public void zza(zzaqa paramAnonymouszzaqa, Class paramAnonymousClass)
      throws IOException
    {
      if (paramAnonymousClass == null)
      {
        paramAnonymouszzaqa.bx();
        return;
      }
      paramAnonymouszzaqa = String.valueOf(paramAnonymousClass.getName());
      throw new UnsupportedOperationException(String.valueOf(paramAnonymouszzaqa).length() + 76 + "Attempted to serialize java.lang.Class: " + paramAnonymouszzaqa + ". Forgot to register a type adapter?");
    }
    
    public Class zzo(zzapy paramAnonymouszzapy)
      throws IOException
    {
      if (paramAnonymouszzapy.bn() == zzapz.bos)
      {
        paramAnonymouszzapy.nextNull();
        return null;
      }
      throw new UnsupportedOperationException("Attempted to deserialize a java.lang.Class. Forgot to register a type adapter?");
    }
  };
  public static final zzaou bmT = zza(Class.class, bmS);
  public static final zzaot<BitSet> bmU = new zzaot()
  {
    public void zza(zzaqa paramAnonymouszzaqa, BitSet paramAnonymousBitSet)
      throws IOException
    {
      if (paramAnonymousBitSet == null)
      {
        paramAnonymouszzaqa.bx();
        return;
      }
      paramAnonymouszzaqa.bt();
      int i = 0;
      if (i < paramAnonymousBitSet.length())
      {
        if (paramAnonymousBitSet.get(i)) {}
        for (int j = 1;; j = 0)
        {
          paramAnonymouszzaqa.zzcu(j);
          i += 1;
          break;
        }
      }
      paramAnonymouszzaqa.bu();
    }
    
    public BitSet zzx(zzapy paramAnonymouszzapy)
      throws IOException
    {
      if (paramAnonymouszzapy.bn() == zzapz.bos)
      {
        paramAnonymouszzapy.nextNull();
        return null;
      }
      BitSet localBitSet = new BitSet();
      paramAnonymouszzapy.beginArray();
      Object localObject = paramAnonymouszzapy.bn();
      int i = 0;
      if (localObject != zzapz.bol)
      {
        boolean bool;
        switch (zzapw.26.bmF[localObject.ordinal()])
        {
        default: 
          paramAnonymouszzapy = String.valueOf(localObject);
          throw new zzaoq(String.valueOf(paramAnonymouszzapy).length() + 27 + "Invalid bitset value type: " + paramAnonymouszzapy);
        case 1: 
          if (paramAnonymouszzapy.nextInt() != 0) {
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
          localObject = paramAnonymouszzapy.bn();
          break;
          bool = false;
          continue;
          bool = paramAnonymouszzapy.nextBoolean();
          continue;
          localObject = paramAnonymouszzapy.nextString();
          try
          {
            int j = Integer.parseInt((String)localObject);
            if (j != 0) {
              bool = true;
            } else {
              bool = false;
            }
          }
          catch (NumberFormatException paramAnonymouszzapy)
          {
            paramAnonymouszzapy = String.valueOf(localObject);
            if (paramAnonymouszzapy.length() == 0) {}
          }
        }
        for (paramAnonymouszzapy = "Error: Expecting: bitset number value (1, 0), Found: ".concat(paramAnonymouszzapy);; paramAnonymouszzapy = new String("Error: Expecting: bitset number value (1, 0), Found: ")) {
          throw new zzaoq(paramAnonymouszzapy);
        }
      }
      paramAnonymouszzapy.endArray();
      return localBitSet;
    }
  };
  public static final zzaou bmV = zza(BitSet.class, bmU);
  public static final zzaot<Boolean> bmW = new zzaot()
  {
    public void zza(zzaqa paramAnonymouszzaqa, Boolean paramAnonymousBoolean)
      throws IOException
    {
      if (paramAnonymousBoolean == null)
      {
        paramAnonymouszzaqa.bx();
        return;
      }
      paramAnonymouszzaqa.zzdf(paramAnonymousBoolean.booleanValue());
    }
    
    public Boolean zzae(zzapy paramAnonymouszzapy)
      throws IOException
    {
      if (paramAnonymouszzapy.bn() == zzapz.bos)
      {
        paramAnonymouszzapy.nextNull();
        return null;
      }
      if (paramAnonymouszzapy.bn() == zzapz.bop) {
        return Boolean.valueOf(Boolean.parseBoolean(paramAnonymouszzapy.nextString()));
      }
      return Boolean.valueOf(paramAnonymouszzapy.nextBoolean());
    }
  };
  public static final zzaot<Boolean> bmX = new zzaot()
  {
    public void zza(zzaqa paramAnonymouszzaqa, Boolean paramAnonymousBoolean)
      throws IOException
    {
      if (paramAnonymousBoolean == null) {}
      for (paramAnonymousBoolean = "null";; paramAnonymousBoolean = paramAnonymousBoolean.toString())
      {
        paramAnonymouszzaqa.zzut(paramAnonymousBoolean);
        return;
      }
    }
    
    public Boolean zzae(zzapy paramAnonymouszzapy)
      throws IOException
    {
      if (paramAnonymouszzapy.bn() == zzapz.bos)
      {
        paramAnonymouszzapy.nextNull();
        return null;
      }
      return Boolean.valueOf(paramAnonymouszzapy.nextString());
    }
  };
  public static final zzaou bmY = zza(Boolean.TYPE, Boolean.class, bmW);
  public static final zzaot<Number> bmZ = new zzaot()
  {
    public void zza(zzaqa paramAnonymouszzaqa, Number paramAnonymousNumber)
      throws IOException
    {
      paramAnonymouszzaqa.zza(paramAnonymousNumber);
    }
    
    public Number zzg(zzapy paramAnonymouszzapy)
      throws IOException
    {
      if (paramAnonymouszzapy.bn() == zzapz.bos)
      {
        paramAnonymouszzapy.nextNull();
        return null;
      }
      try
      {
        byte b = (byte)paramAnonymouszzapy.nextInt();
        return Byte.valueOf(b);
      }
      catch (NumberFormatException paramAnonymouszzapy)
      {
        throw new zzaoq(paramAnonymouszzapy);
      }
    }
  };
  public static final zzaot<UUID> bnA = new zzaot()
  {
    public void zza(zzaqa paramAnonymouszzaqa, UUID paramAnonymousUUID)
      throws IOException
    {
      if (paramAnonymousUUID == null) {}
      for (paramAnonymousUUID = null;; paramAnonymousUUID = paramAnonymousUUID.toString())
      {
        paramAnonymouszzaqa.zzut(paramAnonymousUUID);
        return;
      }
    }
    
    public UUID zzz(zzapy paramAnonymouszzapy)
      throws IOException
    {
      if (paramAnonymouszzapy.bn() == zzapz.bos)
      {
        paramAnonymouszzapy.nextNull();
        return null;
      }
      return UUID.fromString(paramAnonymouszzapy.nextString());
    }
  };
  public static final zzaou bnB = zza(UUID.class, bnA);
  public static final zzaou bnC = new zzaou()
  {
    public <T> zzaot<T> zza(zzaob paramAnonymouszzaob, zzapx<T> paramAnonymouszzapx)
    {
      if (paramAnonymouszzapx.by() != Timestamp.class) {
        return null;
      }
      new zzaot()
      {
        public void zza(zzaqa paramAnonymous2zzaqa, Timestamp paramAnonymous2Timestamp)
          throws IOException
        {
          this.bnK.zza(paramAnonymous2zzaqa, paramAnonymous2Timestamp);
        }
        
        public Timestamp zzaa(zzapy paramAnonymous2zzapy)
          throws IOException
        {
          paramAnonymous2zzapy = (Date)this.bnK.zzb(paramAnonymous2zzapy);
          if (paramAnonymous2zzapy != null) {
            return new Timestamp(paramAnonymous2zzapy.getTime());
          }
          return null;
        }
      };
    }
  };
  public static final zzaot<Calendar> bnD = new zzaot()
  {
    public void zza(zzaqa paramAnonymouszzaqa, Calendar paramAnonymousCalendar)
      throws IOException
    {
      if (paramAnonymousCalendar == null)
      {
        paramAnonymouszzaqa.bx();
        return;
      }
      paramAnonymouszzaqa.bv();
      paramAnonymouszzaqa.zzus("year");
      paramAnonymouszzaqa.zzcu(paramAnonymousCalendar.get(1));
      paramAnonymouszzaqa.zzus("month");
      paramAnonymouszzaqa.zzcu(paramAnonymousCalendar.get(2));
      paramAnonymouszzaqa.zzus("dayOfMonth");
      paramAnonymouszzaqa.zzcu(paramAnonymousCalendar.get(5));
      paramAnonymouszzaqa.zzus("hourOfDay");
      paramAnonymouszzaqa.zzcu(paramAnonymousCalendar.get(11));
      paramAnonymouszzaqa.zzus("minute");
      paramAnonymouszzaqa.zzcu(paramAnonymousCalendar.get(12));
      paramAnonymouszzaqa.zzus("second");
      paramAnonymouszzaqa.zzcu(paramAnonymousCalendar.get(13));
      paramAnonymouszzaqa.bw();
    }
    
    public Calendar zzab(zzapy paramAnonymouszzapy)
      throws IOException
    {
      int j = 0;
      if (paramAnonymouszzapy.bn() == zzapz.bos)
      {
        paramAnonymouszzapy.nextNull();
        return null;
      }
      paramAnonymouszzapy.beginObject();
      int k = 0;
      int m = 0;
      int n = 0;
      int i1 = 0;
      int i2 = 0;
      while (paramAnonymouszzapy.bn() != zzapz.bon)
      {
        String str = paramAnonymouszzapy.nextName();
        int i = paramAnonymouszzapy.nextInt();
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
      paramAnonymouszzapy.endObject();
      return new GregorianCalendar(i2, i1, n, m, k, j);
    }
  };
  public static final zzaou bnE = zzb(Calendar.class, GregorianCalendar.class, bnD);
  public static final zzaot<Locale> bnF = new zzaot()
  {
    public void zza(zzaqa paramAnonymouszzaqa, Locale paramAnonymousLocale)
      throws IOException
    {
      if (paramAnonymousLocale == null) {}
      for (paramAnonymousLocale = null;; paramAnonymousLocale = paramAnonymousLocale.toString())
      {
        paramAnonymouszzaqa.zzut(paramAnonymousLocale);
        return;
      }
    }
    
    public Locale zzac(zzapy paramAnonymouszzapy)
      throws IOException
    {
      if (paramAnonymouszzapy.bn() == zzapz.bos)
      {
        paramAnonymouszzapy.nextNull();
        return null;
      }
      Object localObject = new StringTokenizer(paramAnonymouszzapy.nextString(), "_");
      if (((StringTokenizer)localObject).hasMoreElements()) {}
      for (paramAnonymouszzapy = ((StringTokenizer)localObject).nextToken();; paramAnonymouszzapy = null)
      {
        if (((StringTokenizer)localObject).hasMoreElements()) {}
        for (String str = ((StringTokenizer)localObject).nextToken();; str = null)
        {
          if (((StringTokenizer)localObject).hasMoreElements()) {}
          for (localObject = ((StringTokenizer)localObject).nextToken();; localObject = null)
          {
            if ((str == null) && (localObject == null)) {
              return new Locale(paramAnonymouszzapy);
            }
            if (localObject == null) {
              return new Locale(paramAnonymouszzapy, str);
            }
            return new Locale(paramAnonymouszzapy, str, (String)localObject);
          }
        }
      }
    }
  };
  public static final zzaou bnG = zza(Locale.class, bnF);
  public static final zzaot<zzaoh> bnH = new zzaot()
  {
    public void zza(zzaqa paramAnonymouszzaqa, zzaoh paramAnonymouszzaoh)
      throws IOException
    {
      if ((paramAnonymouszzaoh == null) || (paramAnonymouszzaoh.aV()))
      {
        paramAnonymouszzaqa.bx();
        return;
      }
      if (paramAnonymouszzaoh.aU())
      {
        paramAnonymouszzaoh = paramAnonymouszzaoh.aY();
        if (paramAnonymouszzaoh.bb())
        {
          paramAnonymouszzaqa.zza(paramAnonymouszzaoh.aQ());
          return;
        }
        if (paramAnonymouszzaoh.ba())
        {
          paramAnonymouszzaqa.zzdf(paramAnonymouszzaoh.getAsBoolean());
          return;
        }
        paramAnonymouszzaqa.zzut(paramAnonymouszzaoh.aR());
        return;
      }
      if (paramAnonymouszzaoh.aS())
      {
        paramAnonymouszzaqa.bt();
        paramAnonymouszzaoh = paramAnonymouszzaoh.aX().iterator();
        while (paramAnonymouszzaoh.hasNext()) {
          zza(paramAnonymouszzaqa, (zzaoh)paramAnonymouszzaoh.next());
        }
        paramAnonymouszzaqa.bu();
        return;
      }
      if (paramAnonymouszzaoh.aT())
      {
        paramAnonymouszzaqa.bv();
        paramAnonymouszzaoh = paramAnonymouszzaoh.aW().entrySet().iterator();
        while (paramAnonymouszzaoh.hasNext())
        {
          Map.Entry localEntry = (Map.Entry)paramAnonymouszzaoh.next();
          paramAnonymouszzaqa.zzus((String)localEntry.getKey());
          zza(paramAnonymouszzaqa, (zzaoh)localEntry.getValue());
        }
        paramAnonymouszzaqa.bw();
        return;
      }
      paramAnonymouszzaqa = String.valueOf(paramAnonymouszzaoh.getClass());
      throw new IllegalArgumentException(String.valueOf(paramAnonymouszzaqa).length() + 15 + "Couldn't write " + paramAnonymouszzaqa);
    }
    
    public zzaoh zzad(zzapy paramAnonymouszzapy)
      throws IOException
    {
      switch (zzapw.26.bmF[paramAnonymouszzapy.bn().ordinal()])
      {
      default: 
        throw new IllegalArgumentException();
      case 3: 
        return new zzaon(paramAnonymouszzapy.nextString());
      case 1: 
        return new zzaon(new zzape(paramAnonymouszzapy.nextString()));
      case 2: 
        return new zzaon(Boolean.valueOf(paramAnonymouszzapy.nextBoolean()));
      case 4: 
        paramAnonymouszzapy.nextNull();
        return zzaoj.bld;
      case 5: 
        localObject = new zzaoe();
        paramAnonymouszzapy.beginArray();
        while (paramAnonymouszzapy.hasNext()) {
          ((zzaoe)localObject).zzc((zzaoh)zzb(paramAnonymouszzapy));
        }
        paramAnonymouszzapy.endArray();
        return (zzaoh)localObject;
      }
      Object localObject = new zzaok();
      paramAnonymouszzapy.beginObject();
      while (paramAnonymouszzapy.hasNext()) {
        ((zzaok)localObject).zza(paramAnonymouszzapy.nextName(), (zzaoh)zzb(paramAnonymouszzapy));
      }
      paramAnonymouszzapy.endObject();
      return (zzaoh)localObject;
    }
  };
  public static final zzaou bnI = zzb(zzaoh.class, bnH);
  public static final zzaou bnJ = new zzaou()
  {
    public <T> zzaot<T> zza(zzaob paramAnonymouszzaob, zzapx<T> paramAnonymouszzapx)
    {
      paramAnonymouszzapx = paramAnonymouszzapx.by();
      if ((!Enum.class.isAssignableFrom(paramAnonymouszzapx)) || (paramAnonymouszzapx == Enum.class)) {
        return null;
      }
      paramAnonymouszzaob = paramAnonymouszzapx;
      if (!paramAnonymouszzapx.isEnum()) {
        paramAnonymouszzaob = paramAnonymouszzapx.getSuperclass();
      }
      return new zzapw.zza(paramAnonymouszzaob);
    }
  };
  public static final zzaou bna = zza(Byte.TYPE, Byte.class, bmZ);
  public static final zzaot<Number> bnb = new zzaot()
  {
    public void zza(zzaqa paramAnonymouszzaqa, Number paramAnonymousNumber)
      throws IOException
    {
      paramAnonymouszzaqa.zza(paramAnonymousNumber);
    }
    
    public Number zzg(zzapy paramAnonymouszzapy)
      throws IOException
    {
      if (paramAnonymouszzapy.bn() == zzapz.bos)
      {
        paramAnonymouszzapy.nextNull();
        return null;
      }
      try
      {
        short s = (short)paramAnonymouszzapy.nextInt();
        return Short.valueOf(s);
      }
      catch (NumberFormatException paramAnonymouszzapy)
      {
        throw new zzaoq(paramAnonymouszzapy);
      }
    }
  };
  public static final zzaou bnc = zza(Short.TYPE, Short.class, bnb);
  public static final zzaot<Number> bnd = new zzaot()
  {
    public void zza(zzaqa paramAnonymouszzaqa, Number paramAnonymousNumber)
      throws IOException
    {
      paramAnonymouszzaqa.zza(paramAnonymousNumber);
    }
    
    public Number zzg(zzapy paramAnonymouszzapy)
      throws IOException
    {
      if (paramAnonymouszzapy.bn() == zzapz.bos)
      {
        paramAnonymouszzapy.nextNull();
        return null;
      }
      try
      {
        int i = paramAnonymouszzapy.nextInt();
        return Integer.valueOf(i);
      }
      catch (NumberFormatException paramAnonymouszzapy)
      {
        throw new zzaoq(paramAnonymouszzapy);
      }
    }
  };
  public static final zzaou bne = zza(Integer.TYPE, Integer.class, bnd);
  public static final zzaot<Number> bnf = new zzaot()
  {
    public void zza(zzaqa paramAnonymouszzaqa, Number paramAnonymousNumber)
      throws IOException
    {
      paramAnonymouszzaqa.zza(paramAnonymousNumber);
    }
    
    public Number zzg(zzapy paramAnonymouszzapy)
      throws IOException
    {
      if (paramAnonymouszzapy.bn() == zzapz.bos)
      {
        paramAnonymouszzapy.nextNull();
        return null;
      }
      try
      {
        long l = paramAnonymouszzapy.nextLong();
        return Long.valueOf(l);
      }
      catch (NumberFormatException paramAnonymouszzapy)
      {
        throw new zzaoq(paramAnonymouszzapy);
      }
    }
  };
  public static final zzaot<Number> bng = new zzaot()
  {
    public void zza(zzaqa paramAnonymouszzaqa, Number paramAnonymousNumber)
      throws IOException
    {
      paramAnonymouszzaqa.zza(paramAnonymousNumber);
    }
    
    public Number zzg(zzapy paramAnonymouszzapy)
      throws IOException
    {
      if (paramAnonymouszzapy.bn() == zzapz.bos)
      {
        paramAnonymouszzapy.nextNull();
        return null;
      }
      return Float.valueOf((float)paramAnonymouszzapy.nextDouble());
    }
  };
  public static final zzaot<Number> bnh = new zzaot()
  {
    public void zza(zzaqa paramAnonymouszzaqa, Number paramAnonymousNumber)
      throws IOException
    {
      paramAnonymouszzaqa.zza(paramAnonymousNumber);
    }
    
    public Number zzg(zzapy paramAnonymouszzapy)
      throws IOException
    {
      if (paramAnonymouszzapy.bn() == zzapz.bos)
      {
        paramAnonymouszzapy.nextNull();
        return null;
      }
      return Double.valueOf(paramAnonymouszzapy.nextDouble());
    }
  };
  public static final zzaot<Number> bni = new zzaot()
  {
    public void zza(zzaqa paramAnonymouszzaqa, Number paramAnonymousNumber)
      throws IOException
    {
      paramAnonymouszzaqa.zza(paramAnonymousNumber);
    }
    
    public Number zzg(zzapy paramAnonymouszzapy)
      throws IOException
    {
      zzapz localzzapz = paramAnonymouszzapy.bn();
      switch (zzapw.26.bmF[localzzapz.ordinal()])
      {
      case 2: 
      case 3: 
      default: 
        paramAnonymouszzapy = String.valueOf(localzzapz);
        throw new zzaoq(String.valueOf(paramAnonymouszzapy).length() + 23 + "Expecting number, got: " + paramAnonymouszzapy);
      case 4: 
        paramAnonymouszzapy.nextNull();
        return null;
      }
      return new zzape(paramAnonymouszzapy.nextString());
    }
  };
  public static final zzaou bnj = zza(Number.class, bni);
  public static final zzaot<Character> bnk = new zzaot()
  {
    public void zza(zzaqa paramAnonymouszzaqa, Character paramAnonymousCharacter)
      throws IOException
    {
      if (paramAnonymousCharacter == null) {}
      for (paramAnonymousCharacter = null;; paramAnonymousCharacter = String.valueOf(paramAnonymousCharacter))
      {
        paramAnonymouszzaqa.zzut(paramAnonymousCharacter);
        return;
      }
    }
    
    public Character zzp(zzapy paramAnonymouszzapy)
      throws IOException
    {
      if (paramAnonymouszzapy.bn() == zzapz.bos)
      {
        paramAnonymouszzapy.nextNull();
        return null;
      }
      paramAnonymouszzapy = paramAnonymouszzapy.nextString();
      if (paramAnonymouszzapy.length() != 1)
      {
        paramAnonymouszzapy = String.valueOf(paramAnonymouszzapy);
        if (paramAnonymouszzapy.length() != 0) {}
        for (paramAnonymouszzapy = "Expecting character, got: ".concat(paramAnonymouszzapy);; paramAnonymouszzapy = new String("Expecting character, got: ")) {
          throw new zzaoq(paramAnonymouszzapy);
        }
      }
      return Character.valueOf(paramAnonymouszzapy.charAt(0));
    }
  };
  public static final zzaou bnl = zza(Character.TYPE, Character.class, bnk);
  public static final zzaot<String> bnm = new zzaot()
  {
    public void zza(zzaqa paramAnonymouszzaqa, String paramAnonymousString)
      throws IOException
    {
      paramAnonymouszzaqa.zzut(paramAnonymousString);
    }
    
    public String zzq(zzapy paramAnonymouszzapy)
      throws IOException
    {
      zzapz localzzapz = paramAnonymouszzapy.bn();
      if (localzzapz == zzapz.bos)
      {
        paramAnonymouszzapy.nextNull();
        return null;
      }
      if (localzzapz == zzapz.bor) {
        return Boolean.toString(paramAnonymouszzapy.nextBoolean());
      }
      return paramAnonymouszzapy.nextString();
    }
  };
  public static final zzaot<BigDecimal> bnn = new zzaot()
  {
    public void zza(zzaqa paramAnonymouszzaqa, BigDecimal paramAnonymousBigDecimal)
      throws IOException
    {
      paramAnonymouszzaqa.zza(paramAnonymousBigDecimal);
    }
    
    public BigDecimal zzr(zzapy paramAnonymouszzapy)
      throws IOException
    {
      if (paramAnonymouszzapy.bn() == zzapz.bos)
      {
        paramAnonymouszzapy.nextNull();
        return null;
      }
      try
      {
        paramAnonymouszzapy = new BigDecimal(paramAnonymouszzapy.nextString());
        return paramAnonymouszzapy;
      }
      catch (NumberFormatException paramAnonymouszzapy)
      {
        throw new zzaoq(paramAnonymouszzapy);
      }
    }
  };
  public static final zzaot<BigInteger> bno = new zzaot()
  {
    public void zza(zzaqa paramAnonymouszzaqa, BigInteger paramAnonymousBigInteger)
      throws IOException
    {
      paramAnonymouszzaqa.zza(paramAnonymousBigInteger);
    }
    
    public BigInteger zzs(zzapy paramAnonymouszzapy)
      throws IOException
    {
      if (paramAnonymouszzapy.bn() == zzapz.bos)
      {
        paramAnonymouszzapy.nextNull();
        return null;
      }
      try
      {
        paramAnonymouszzapy = new BigInteger(paramAnonymouszzapy.nextString());
        return paramAnonymouszzapy;
      }
      catch (NumberFormatException paramAnonymouszzapy)
      {
        throw new zzaoq(paramAnonymouszzapy);
      }
    }
  };
  public static final zzaou bnp = zza(String.class, bnm);
  public static final zzaot<StringBuilder> bnq = new zzaot()
  {
    public void zza(zzaqa paramAnonymouszzaqa, StringBuilder paramAnonymousStringBuilder)
      throws IOException
    {
      if (paramAnonymousStringBuilder == null) {}
      for (paramAnonymousStringBuilder = null;; paramAnonymousStringBuilder = paramAnonymousStringBuilder.toString())
      {
        paramAnonymouszzaqa.zzut(paramAnonymousStringBuilder);
        return;
      }
    }
    
    public StringBuilder zzt(zzapy paramAnonymouszzapy)
      throws IOException
    {
      if (paramAnonymouszzapy.bn() == zzapz.bos)
      {
        paramAnonymouszzapy.nextNull();
        return null;
      }
      return new StringBuilder(paramAnonymouszzapy.nextString());
    }
  };
  public static final zzaou bnr = zza(StringBuilder.class, bnq);
  public static final zzaot<StringBuffer> bns = new zzaot()
  {
    public void zza(zzaqa paramAnonymouszzaqa, StringBuffer paramAnonymousStringBuffer)
      throws IOException
    {
      if (paramAnonymousStringBuffer == null) {}
      for (paramAnonymousStringBuffer = null;; paramAnonymousStringBuffer = paramAnonymousStringBuffer.toString())
      {
        paramAnonymouszzaqa.zzut(paramAnonymousStringBuffer);
        return;
      }
    }
    
    public StringBuffer zzu(zzapy paramAnonymouszzapy)
      throws IOException
    {
      if (paramAnonymouszzapy.bn() == zzapz.bos)
      {
        paramAnonymouszzapy.nextNull();
        return null;
      }
      return new StringBuffer(paramAnonymouszzapy.nextString());
    }
  };
  public static final zzaou bnt = zza(StringBuffer.class, bns);
  public static final zzaot<URL> bnu = new zzaot()
  {
    public void zza(zzaqa paramAnonymouszzaqa, URL paramAnonymousURL)
      throws IOException
    {
      if (paramAnonymousURL == null) {}
      for (paramAnonymousURL = null;; paramAnonymousURL = paramAnonymousURL.toExternalForm())
      {
        paramAnonymouszzaqa.zzut(paramAnonymousURL);
        return;
      }
    }
    
    public URL zzv(zzapy paramAnonymouszzapy)
      throws IOException
    {
      if (paramAnonymouszzapy.bn() == zzapz.bos) {
        paramAnonymouszzapy.nextNull();
      }
      do
      {
        return null;
        paramAnonymouszzapy = paramAnonymouszzapy.nextString();
      } while ("null".equals(paramAnonymouszzapy));
      return new URL(paramAnonymouszzapy);
    }
  };
  public static final zzaou bnv = zza(URL.class, bnu);
  public static final zzaot<URI> bnw = new zzaot()
  {
    public void zza(zzaqa paramAnonymouszzaqa, URI paramAnonymousURI)
      throws IOException
    {
      if (paramAnonymousURI == null) {}
      for (paramAnonymousURI = null;; paramAnonymousURI = paramAnonymousURI.toASCIIString())
      {
        paramAnonymouszzaqa.zzut(paramAnonymousURI);
        return;
      }
    }
    
    public URI zzw(zzapy paramAnonymouszzapy)
      throws IOException
    {
      if (paramAnonymouszzapy.bn() == zzapz.bos) {
        paramAnonymouszzapy.nextNull();
      }
      for (;;)
      {
        return null;
        try
        {
          paramAnonymouszzapy = paramAnonymouszzapy.nextString();
          if ("null".equals(paramAnonymouszzapy)) {
            continue;
          }
          paramAnonymouszzapy = new URI(paramAnonymouszzapy);
          return paramAnonymouszzapy;
        }
        catch (URISyntaxException paramAnonymouszzapy)
        {
          throw new zzaoi(paramAnonymouszzapy);
        }
      }
    }
  };
  public static final zzaou bnx = zza(URI.class, bnw);
  public static final zzaot<InetAddress> bny = new zzaot()
  {
    public void zza(zzaqa paramAnonymouszzaqa, InetAddress paramAnonymousInetAddress)
      throws IOException
    {
      if (paramAnonymousInetAddress == null) {}
      for (paramAnonymousInetAddress = null;; paramAnonymousInetAddress = paramAnonymousInetAddress.getHostAddress())
      {
        paramAnonymouszzaqa.zzut(paramAnonymousInetAddress);
        return;
      }
    }
    
    public InetAddress zzy(zzapy paramAnonymouszzapy)
      throws IOException
    {
      if (paramAnonymouszzapy.bn() == zzapz.bos)
      {
        paramAnonymouszzapy.nextNull();
        return null;
      }
      return InetAddress.getByName(paramAnonymouszzapy.nextString());
    }
  };
  public static final zzaou bnz = zzb(InetAddress.class, bny);
  
  public static <TT> zzaou zza(zzapx<TT> paramzzapx, final zzaot<TT> paramzzaot)
  {
    new zzaou()
    {
      public <T> zzaot<T> zza(zzaob paramAnonymouszzaob, zzapx<T> paramAnonymouszzapx)
      {
        if (paramAnonymouszzapx.equals(this.blO)) {
          return paramzzaot;
        }
        return null;
      }
    };
  }
  
  public static <TT> zzaou zza(Class<TT> paramClass, final zzaot<TT> paramzzaot)
  {
    new zzaou()
    {
      public String toString()
      {
        String str1 = String.valueOf(this.bnN.getName());
        String str2 = String.valueOf(paramzzaot);
        return String.valueOf(str1).length() + 23 + String.valueOf(str2).length() + "Factory[type=" + str1 + ",adapter=" + str2 + "]";
      }
      
      public <T> zzaot<T> zza(zzaob paramAnonymouszzaob, zzapx<T> paramAnonymouszzapx)
      {
        if (paramAnonymouszzapx.by() == this.bnN) {
          return paramzzaot;
        }
        return null;
      }
    };
  }
  
  public static <TT> zzaou zza(Class<TT> paramClass1, final Class<TT> paramClass2, final zzaot<? super TT> paramzzaot)
  {
    new zzaou()
    {
      public String toString()
      {
        String str1 = String.valueOf(paramClass2.getName());
        String str2 = String.valueOf(this.bnO.getName());
        String str3 = String.valueOf(paramzzaot);
        return String.valueOf(str1).length() + 24 + String.valueOf(str2).length() + String.valueOf(str3).length() + "Factory[type=" + str1 + "+" + str2 + ",adapter=" + str3 + "]";
      }
      
      public <T> zzaot<T> zza(zzaob paramAnonymouszzaob, zzapx<T> paramAnonymouszzapx)
      {
        paramAnonymouszzaob = paramAnonymouszzapx.by();
        if ((paramAnonymouszzaob == this.bnO) || (paramAnonymouszzaob == paramClass2)) {
          return paramzzaot;
        }
        return null;
      }
    };
  }
  
  public static <TT> zzaou zzb(Class<TT> paramClass, final zzaot<TT> paramzzaot)
  {
    new zzaou()
    {
      public String toString()
      {
        String str1 = String.valueOf(this.bnS.getName());
        String str2 = String.valueOf(paramzzaot);
        return String.valueOf(str1).length() + 32 + String.valueOf(str2).length() + "Factory[typeHierarchy=" + str1 + ",adapter=" + str2 + "]";
      }
      
      public <T> zzaot<T> zza(zzaob paramAnonymouszzaob, zzapx<T> paramAnonymouszzapx)
      {
        if (this.bnS.isAssignableFrom(paramAnonymouszzapx.by())) {
          return paramzzaot;
        }
        return null;
      }
    };
  }
  
  public static <TT> zzaou zzb(Class<TT> paramClass, final Class<? extends TT> paramClass1, final zzaot<? super TT> paramzzaot)
  {
    new zzaou()
    {
      public String toString()
      {
        String str1 = String.valueOf(this.bnQ.getName());
        String str2 = String.valueOf(paramClass1.getName());
        String str3 = String.valueOf(paramzzaot);
        return String.valueOf(str1).length() + 24 + String.valueOf(str2).length() + String.valueOf(str3).length() + "Factory[type=" + str1 + "+" + str2 + ",adapter=" + str3 + "]";
      }
      
      public <T> zzaot<T> zza(zzaob paramAnonymouszzaob, zzapx<T> paramAnonymouszzapx)
      {
        paramAnonymouszzaob = paramAnonymouszzapx.by();
        if ((paramAnonymouszzaob == this.bnQ) || (paramAnonymouszzaob == paramClass1)) {
          return paramzzaot;
        }
        return null;
      }
    };
  }
  
  private static final class zza<T extends Enum<T>>
    extends zzaot<T>
  {
    private final Map<String, T> bnT = new HashMap();
    private final Map<T, String> bnU = new HashMap();
    
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
          Object localObject2 = (zzaow)paramClass.getField((String)localObject1).getAnnotation(zzaow.class);
          if (localObject2 != null)
          {
            String str = ((zzaow)localObject2).value();
            localObject2 = ((zzaow)localObject2).be();
            int m = localObject2.length;
            int j = 0;
            for (;;)
            {
              localObject1 = str;
              if (j >= m) {
                break;
              }
              localObject1 = localObject2[j];
              this.bnT.put(localObject1, localEnum);
              j += 1;
            }
          }
          this.bnT.put(localObject1, localEnum);
          this.bnU.put(localEnum, localObject1);
          i += 1;
        }
        return;
      }
      catch (NoSuchFieldException paramClass)
      {
        throw new AssertionError();
      }
    }
    
    public void zza(zzaqa paramzzaqa, T paramT)
      throws IOException
    {
      if (paramT == null) {}
      for (paramT = null;; paramT = (String)this.bnU.get(paramT))
      {
        paramzzaqa.zzut(paramT);
        return;
      }
    }
    
    public T zzaf(zzapy paramzzapy)
      throws IOException
    {
      if (paramzzapy.bn() == zzapz.bos)
      {
        paramzzapy.nextNull();
        return null;
      }
      return (Enum)this.bnT.get(paramzzapy.nextString());
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzapw.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */