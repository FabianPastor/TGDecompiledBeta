package org.telegram.messenger.time;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FastDateParser
  implements Serializable, DateParser
{
  private static final Strategy ABBREVIATED_YEAR_STRATEGY;
  private static final Strategy DAY_OF_MONTH_STRATEGY;
  private static final Strategy DAY_OF_WEEK_IN_MONTH_STRATEGY;
  private static final Strategy DAY_OF_YEAR_STRATEGY;
  private static final Strategy HOUR_OF_DAY_STRATEGY;
  private static final Strategy HOUR_STRATEGY;
  static final Locale JAPANESE_IMPERIAL = new Locale("ja", "JP", "JP");
  private static final Strategy LITERAL_YEAR_STRATEGY;
  private static final Strategy MILLISECOND_STRATEGY = new NumberStrategy(14);
  private static final Strategy MINUTE_STRATEGY;
  private static final Strategy MODULO_HOUR_OF_DAY_STRATEGY;
  private static final Strategy MODULO_HOUR_STRATEGY;
  private static final Strategy NUMBER_MONTH_STRATEGY;
  private static final Strategy SECOND_STRATEGY;
  private static final Strategy WEEK_OF_MONTH_STRATEGY;
  private static final Strategy WEEK_OF_YEAR_STRATEGY;
  private static final ConcurrentMap<Locale, Strategy>[] caches;
  private static final Pattern formatPattern = Pattern.compile("D+|E+|F+|G+|H+|K+|M+|S+|W+|Z+|a+|d+|h+|k+|m+|s+|w+|y+|z+|''|'[^']++(''[^']*+)*+'|[^'A-Za-z]++");
  private static final long serialVersionUID = 2L;
  private final int century;
  private transient String currentFormatField;
  private final Locale locale;
  private transient Strategy nextStrategy;
  private transient Pattern parsePattern;
  private final String pattern;
  private final int startYear;
  private transient Strategy[] strategies;
  private final TimeZone timeZone;
  
  static
  {
    caches = new ConcurrentMap[17];
    ABBREVIATED_YEAR_STRATEGY = new NumberStrategy(1)
    {
      void setCalendar(FastDateParser paramAnonymousFastDateParser, Calendar paramAnonymousCalendar, String paramAnonymousString)
      {
        int i = Integer.parseInt(paramAnonymousString);
        int j = i;
        if (i < 100) {
          j = paramAnonymousFastDateParser.adjustYear(i);
        }
        paramAnonymousCalendar.set(1, j);
      }
    };
    NUMBER_MONTH_STRATEGY = new NumberStrategy(2)
    {
      int modify(int paramAnonymousInt)
      {
        return paramAnonymousInt - 1;
      }
    };
    LITERAL_YEAR_STRATEGY = new NumberStrategy(1);
    WEEK_OF_YEAR_STRATEGY = new NumberStrategy(3);
    WEEK_OF_MONTH_STRATEGY = new NumberStrategy(4);
    DAY_OF_YEAR_STRATEGY = new NumberStrategy(6);
    DAY_OF_MONTH_STRATEGY = new NumberStrategy(5);
    DAY_OF_WEEK_IN_MONTH_STRATEGY = new NumberStrategy(8);
    HOUR_OF_DAY_STRATEGY = new NumberStrategy(11);
    MODULO_HOUR_OF_DAY_STRATEGY = new NumberStrategy(11)
    {
      int modify(int paramAnonymousInt)
      {
        return paramAnonymousInt % 24;
      }
    };
    MODULO_HOUR_STRATEGY = new NumberStrategy(10)
    {
      int modify(int paramAnonymousInt)
      {
        return paramAnonymousInt % 12;
      }
    };
    HOUR_STRATEGY = new NumberStrategy(10);
    MINUTE_STRATEGY = new NumberStrategy(12);
    SECOND_STRATEGY = new NumberStrategy(13);
  }
  
  protected FastDateParser(String paramString, TimeZone paramTimeZone, Locale paramLocale)
  {
    this(paramString, paramTimeZone, paramLocale, null);
  }
  
  protected FastDateParser(String paramString, TimeZone paramTimeZone, Locale paramLocale, Date paramDate)
  {
    this.pattern = paramString;
    this.timeZone = paramTimeZone;
    this.locale = paramLocale;
    paramString = Calendar.getInstance(paramTimeZone, paramLocale);
    int i;
    if (paramDate != null)
    {
      paramString.setTime(paramDate);
      i = paramString.get(1);
    }
    for (;;)
    {
      this.century = (i / 100 * 100);
      this.startYear = (i - this.century);
      init(paramString);
      return;
      if (paramLocale.equals(JAPANESE_IMPERIAL))
      {
        i = 0;
      }
      else
      {
        paramString.setTime(new Date());
        i = paramString.get(1) - 80;
      }
    }
  }
  
  private int adjustYear(int paramInt)
  {
    int i = this.century + paramInt;
    if (paramInt >= this.startYear) {}
    for (paramInt = i;; paramInt = i + 100) {
      return paramInt;
    }
  }
  
  private static StringBuilder escapeRegex(StringBuilder paramStringBuilder, String paramString, boolean paramBoolean)
  {
    paramStringBuilder.append("\\Q");
    int i = 0;
    char c1;
    int j;
    char c2;
    if (i < paramString.length())
    {
      c1 = paramString.charAt(i);
      switch (c1)
      {
      default: 
        j = i;
        c2 = c1;
      case '\'': 
        do
        {
          paramStringBuilder.append(c2);
          i = j + 1;
          break;
          c2 = c1;
          j = i;
        } while (!paramBoolean);
        j = i + 1;
        if (j != paramString.length()) {
          break;
        }
      }
    }
    for (;;)
    {
      return paramStringBuilder;
      i = paramString.charAt(j);
      c2 = i;
      break;
      i++;
      c2 = c1;
      j = i;
      if (i == paramString.length()) {
        break;
      }
      paramStringBuilder.append(c1);
      char c3 = paramString.charAt(i);
      c2 = c3;
      j = i;
      if (c3 != 'E') {
        break;
      }
      paramStringBuilder.append("E\\\\E\\");
      j = 81;
      c2 = j;
      j = i;
      break;
      paramStringBuilder.append("\\E");
    }
  }
  
  private static ConcurrentMap<Locale, Strategy> getCache(int paramInt)
  {
    synchronized (caches)
    {
      if (caches[paramInt] == null)
      {
        ConcurrentMap[] arrayOfConcurrentMap2 = caches;
        localObject1 = new java/util/concurrent/ConcurrentHashMap;
        ((ConcurrentHashMap)localObject1).<init>(3);
        arrayOfConcurrentMap2[paramInt] = localObject1;
      }
      Object localObject1 = caches[paramInt];
      return (ConcurrentMap<Locale, Strategy>)localObject1;
    }
  }
  
  private static String[] getDisplayNameArray(int paramInt, boolean paramBoolean, Locale paramLocale)
  {
    paramLocale = new DateFormatSymbols(paramLocale);
    switch (paramInt)
    {
    default: 
      paramLocale = null;
    }
    for (;;)
    {
      return paramLocale;
      paramLocale = paramLocale.getAmPmStrings();
      continue;
      if (paramBoolean)
      {
        paramLocale = paramLocale.getWeekdays();
      }
      else
      {
        paramLocale = paramLocale.getShortWeekdays();
        continue;
        paramLocale = paramLocale.getEras();
        continue;
        if (paramBoolean) {
          paramLocale = paramLocale.getMonths();
        } else {
          paramLocale = paramLocale.getShortMonths();
        }
      }
    }
  }
  
  private static Map<String, Integer> getDisplayNames(int paramInt, Calendar paramCalendar, Locale paramLocale)
  {
    return getDisplayNames(paramInt, paramLocale);
  }
  
  private static Map<String, Integer> getDisplayNames(int paramInt, Locale paramLocale)
  {
    HashMap localHashMap = new HashMap();
    insertValuesInMap(localHashMap, getDisplayNameArray(paramInt, false, paramLocale));
    insertValuesInMap(localHashMap, getDisplayNameArray(paramInt, true, paramLocale));
    paramLocale = localHashMap;
    if (localHashMap.isEmpty()) {
      paramLocale = null;
    }
    return paramLocale;
  }
  
  private Strategy getLocaleSpecificStrategy(int paramInt, Calendar paramCalendar)
  {
    ConcurrentMap localConcurrentMap = getCache(paramInt);
    Strategy localStrategy = (Strategy)localConcurrentMap.get(this.locale);
    Object localObject = localStrategy;
    if (localStrategy == null) {
      if (paramInt == 15)
      {
        localObject = new TimeZoneStrategy(this.locale);
        paramCalendar = (Strategy)localConcurrentMap.putIfAbsent(this.locale, localObject);
        if (paramCalendar == null) {
          break label92;
        }
        localObject = paramCalendar;
      }
    }
    label92:
    for (;;)
    {
      return (Strategy)localObject;
      localObject = new TextStrategy(paramInt, paramCalendar, this.locale);
      break;
    }
  }
  
  private Strategy getStrategy(String paramString, Calendar paramCalendar)
  {
    switch (paramString.charAt(0))
    {
    default: 
      paramString = new CopyQuotedStrategy(paramString);
    }
    for (;;)
    {
      return paramString;
      if (paramString.length() <= 2) {
        break;
      }
      paramString = new CopyQuotedStrategy(paramString.substring(1, paramString.length() - 1));
      continue;
      paramString = DAY_OF_YEAR_STRATEGY;
      continue;
      paramString = getLocaleSpecificStrategy(7, paramCalendar);
      continue;
      paramString = DAY_OF_WEEK_IN_MONTH_STRATEGY;
      continue;
      paramString = getLocaleSpecificStrategy(0, paramCalendar);
      continue;
      paramString = MODULO_HOUR_OF_DAY_STRATEGY;
      continue;
      paramString = HOUR_STRATEGY;
      continue;
      if (paramString.length() >= 3)
      {
        paramString = getLocaleSpecificStrategy(2, paramCalendar);
      }
      else
      {
        paramString = NUMBER_MONTH_STRATEGY;
        continue;
        paramString = MILLISECOND_STRATEGY;
        continue;
        paramString = WEEK_OF_MONTH_STRATEGY;
        continue;
        paramString = getLocaleSpecificStrategy(9, paramCalendar);
        continue;
        paramString = DAY_OF_MONTH_STRATEGY;
        continue;
        paramString = MODULO_HOUR_STRATEGY;
        continue;
        paramString = HOUR_OF_DAY_STRATEGY;
        continue;
        paramString = MINUTE_STRATEGY;
        continue;
        paramString = SECOND_STRATEGY;
        continue;
        paramString = WEEK_OF_YEAR_STRATEGY;
        continue;
        if (paramString.length() > 2)
        {
          paramString = LITERAL_YEAR_STRATEGY;
        }
        else
        {
          paramString = ABBREVIATED_YEAR_STRATEGY;
          continue;
          paramString = getLocaleSpecificStrategy(15, paramCalendar);
        }
      }
    }
  }
  
  private void init(Calendar paramCalendar)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    ArrayList localArrayList = new ArrayList();
    Matcher localMatcher = formatPattern.matcher(this.pattern);
    if (!localMatcher.lookingAt()) {
      throw new IllegalArgumentException("Illegal pattern character '" + this.pattern.charAt(localMatcher.regionStart()) + "'");
    }
    this.currentFormatField = localMatcher.group();
    for (Strategy localStrategy = getStrategy(this.currentFormatField, paramCalendar);; localStrategy = this.nextStrategy)
    {
      localMatcher.region(localMatcher.end(), localMatcher.regionEnd());
      if (!localMatcher.lookingAt())
      {
        this.nextStrategy = null;
        if (localMatcher.regionStart() == localMatcher.regionEnd()) {
          break;
        }
        throw new IllegalArgumentException("Failed to parse \"" + this.pattern + "\" ; gave up at index " + localMatcher.regionStart());
      }
      String str = localMatcher.group();
      this.nextStrategy = getStrategy(str, paramCalendar);
      if (localStrategy.addRegex(this, localStringBuilder)) {
        localArrayList.add(localStrategy);
      }
      this.currentFormatField = str;
    }
    if (localStrategy.addRegex(this, localStringBuilder)) {
      localArrayList.add(localStrategy);
    }
    this.currentFormatField = null;
    this.strategies = ((Strategy[])localArrayList.toArray(new Strategy[localArrayList.size()]));
    this.parsePattern = Pattern.compile(localStringBuilder.toString());
  }
  
  private static void insertValuesInMap(Map<String, Integer> paramMap, String[] paramArrayOfString)
  {
    if (paramArrayOfString == null) {}
    for (;;)
    {
      return;
      for (int i = 0; i < paramArrayOfString.length; i++) {
        if ((paramArrayOfString[i] != null) && (paramArrayOfString[i].length() > 0)) {
          paramMap.put(paramArrayOfString[i], Integer.valueOf(i));
        }
      }
    }
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    init(Calendar.getInstance(this.timeZone, this.locale));
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool1 = false;
    boolean bool2;
    if (!(paramObject instanceof FastDateParser)) {
      bool2 = bool1;
    }
    for (;;)
    {
      return bool2;
      paramObject = (FastDateParser)paramObject;
      bool2 = bool1;
      if (this.pattern.equals(((FastDateParser)paramObject).pattern))
      {
        bool2 = bool1;
        if (this.timeZone.equals(((FastDateParser)paramObject).timeZone))
        {
          bool2 = bool1;
          if (this.locale.equals(((FastDateParser)paramObject).locale)) {
            bool2 = true;
          }
        }
      }
    }
  }
  
  int getFieldWidth()
  {
    return this.currentFormatField.length();
  }
  
  public Locale getLocale()
  {
    return this.locale;
  }
  
  Pattern getParsePattern()
  {
    return this.parsePattern;
  }
  
  public String getPattern()
  {
    return this.pattern;
  }
  
  public TimeZone getTimeZone()
  {
    return this.timeZone;
  }
  
  public int hashCode()
  {
    return this.pattern.hashCode() + (this.timeZone.hashCode() + this.locale.hashCode() * 13) * 13;
  }
  
  boolean isNextNumber()
  {
    if ((this.nextStrategy != null) && (this.nextStrategy.isNumber())) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public Date parse(String paramString)
    throws ParseException
  {
    Date localDate = parse(paramString, new ParsePosition(0));
    if (localDate == null)
    {
      if (this.locale.equals(JAPANESE_IMPERIAL)) {
        throw new ParseException("(The " + this.locale + " locale does not support dates before 1868 AD)\nUnparseable date: \"" + paramString + "\" does not match " + this.parsePattern.pattern(), 0);
      }
      throw new ParseException("Unparseable date: \"" + paramString + "\" does not match " + this.parsePattern.pattern(), 0);
    }
    return localDate;
  }
  
  public Date parse(String paramString, ParsePosition paramParsePosition)
  {
    int i = paramParsePosition.getIndex();
    paramString = this.parsePattern.matcher(paramString.substring(i));
    if (!paramString.lookingAt()) {}
    Calendar localCalendar;
    for (paramString = null;; paramString = localCalendar.getTime())
    {
      return paramString;
      localCalendar = Calendar.getInstance(this.timeZone, this.locale);
      localCalendar.clear();
      int k;
      for (int j = 0; j < this.strategies.length; j = k)
      {
        Strategy[] arrayOfStrategy = this.strategies;
        k = j + 1;
        arrayOfStrategy[j].setCalendar(this, localCalendar, paramString.group(k));
      }
      paramParsePosition.setIndex(paramString.end() + i);
    }
  }
  
  public Object parseObject(String paramString)
    throws ParseException
  {
    return parse(paramString);
  }
  
  public Object parseObject(String paramString, ParsePosition paramParsePosition)
  {
    return parse(paramString, paramParsePosition);
  }
  
  public String toString()
  {
    return "FastDateParser[" + this.pattern + "," + this.locale + "," + this.timeZone.getID() + "]";
  }
  
  private static class CopyQuotedStrategy
    extends FastDateParser.Strategy
  {
    private final String formatField;
    
    CopyQuotedStrategy(String paramString)
    {
      super();
      this.formatField = paramString;
    }
    
    boolean addRegex(FastDateParser paramFastDateParser, StringBuilder paramStringBuilder)
    {
      FastDateParser.escapeRegex(paramStringBuilder, this.formatField, true);
      return false;
    }
    
    boolean isNumber()
    {
      char c1 = this.formatField.charAt(0);
      char c2 = c1;
      if (c1 == '\'')
      {
        c1 = this.formatField.charAt(1);
        c2 = c1;
      }
      return Character.isDigit(c2);
    }
  }
  
  private static class NumberStrategy
    extends FastDateParser.Strategy
  {
    private final int field;
    
    NumberStrategy(int paramInt)
    {
      super();
      this.field = paramInt;
    }
    
    boolean addRegex(FastDateParser paramFastDateParser, StringBuilder paramStringBuilder)
    {
      if (paramFastDateParser.isNextNumber()) {
        paramStringBuilder.append("(\\p{Nd}{").append(paramFastDateParser.getFieldWidth()).append("}+)");
      }
      for (;;)
      {
        return true;
        paramStringBuilder.append("(\\p{Nd}++)");
      }
    }
    
    boolean isNumber()
    {
      return true;
    }
    
    int modify(int paramInt)
    {
      return paramInt;
    }
    
    void setCalendar(FastDateParser paramFastDateParser, Calendar paramCalendar, String paramString)
    {
      paramCalendar.set(this.field, modify(Integer.parseInt(paramString)));
    }
  }
  
  private static abstract class Strategy
  {
    abstract boolean addRegex(FastDateParser paramFastDateParser, StringBuilder paramStringBuilder);
    
    boolean isNumber()
    {
      return false;
    }
    
    void setCalendar(FastDateParser paramFastDateParser, Calendar paramCalendar, String paramString) {}
  }
  
  private static class TextStrategy
    extends FastDateParser.Strategy
  {
    private final int field;
    private final Map<String, Integer> keyValues;
    
    TextStrategy(int paramInt, Calendar paramCalendar, Locale paramLocale)
    {
      super();
      this.field = paramInt;
      this.keyValues = FastDateParser.getDisplayNames(paramInt, paramCalendar, paramLocale);
    }
    
    boolean addRegex(FastDateParser paramFastDateParser, StringBuilder paramStringBuilder)
    {
      paramStringBuilder.append('(');
      paramFastDateParser = this.keyValues.keySet().iterator();
      while (paramFastDateParser.hasNext()) {
        FastDateParser.escapeRegex(paramStringBuilder, (String)paramFastDateParser.next(), false).append('|');
      }
      paramStringBuilder.setCharAt(paramStringBuilder.length() - 1, ')');
      return true;
    }
    
    void setCalendar(FastDateParser paramFastDateParser, Calendar paramCalendar, String paramString)
    {
      paramFastDateParser = (Integer)this.keyValues.get(paramString);
      if (paramFastDateParser == null)
      {
        paramFastDateParser = new StringBuilder(paramString);
        paramFastDateParser.append(" not in (");
        paramCalendar = this.keyValues.keySet().iterator();
        while (paramCalendar.hasNext()) {
          paramFastDateParser.append((String)paramCalendar.next()).append(' ');
        }
        paramFastDateParser.setCharAt(paramFastDateParser.length() - 1, ')');
        throw new IllegalArgumentException(paramFastDateParser.toString());
      }
      paramCalendar.set(this.field, paramFastDateParser.intValue());
    }
  }
  
  private static class TimeZoneStrategy
    extends FastDateParser.Strategy
  {
    private static final int ID = 0;
    private static final int LONG_DST = 3;
    private static final int LONG_STD = 1;
    private static final int SHORT_DST = 4;
    private static final int SHORT_STD = 2;
    private final SortedMap<String, TimeZone> tzNames = new TreeMap(String.CASE_INSENSITIVE_ORDER);
    private final String validTimeZoneChars;
    
    TimeZoneStrategy(Locale paramLocale)
    {
      super();
      paramLocale = DateFormatSymbols.getInstance(paramLocale).getZoneStrings();
      int i = paramLocale.length;
      int j = 0;
      if (j < i)
      {
        Object localObject1 = paramLocale[j];
        if (localObject1[0].startsWith("GMT")) {}
        for (;;)
        {
          j++;
          break;
          localObject2 = TimeZone.getTimeZone(localObject1[0]);
          if (!this.tzNames.containsKey(localObject1[1])) {
            this.tzNames.put(localObject1[1], localObject2);
          }
          if (!this.tzNames.containsKey(localObject1[2])) {
            this.tzNames.put(localObject1[2], localObject2);
          }
          if (((TimeZone)localObject2).useDaylightTime())
          {
            if (!this.tzNames.containsKey(localObject1[3])) {
              this.tzNames.put(localObject1[3], localObject2);
            }
            if (!this.tzNames.containsKey(localObject1[4])) {
              this.tzNames.put(localObject1[4], localObject2);
            }
          }
        }
      }
      Object localObject2 = new StringBuilder();
      ((StringBuilder)localObject2).append("(GMT[+\\-]\\d{0,1}\\d{2}|[+\\-]\\d{2}:?\\d{2}|");
      paramLocale = this.tzNames.keySet().iterator();
      while (paramLocale.hasNext()) {
        FastDateParser.escapeRegex((StringBuilder)localObject2, (String)paramLocale.next(), false).append('|');
      }
      ((StringBuilder)localObject2).setCharAt(((StringBuilder)localObject2).length() - 1, ')');
      this.validTimeZoneChars = ((StringBuilder)localObject2).toString();
    }
    
    boolean addRegex(FastDateParser paramFastDateParser, StringBuilder paramStringBuilder)
    {
      paramStringBuilder.append(this.validTimeZoneChars);
      return true;
    }
    
    void setCalendar(FastDateParser paramFastDateParser, Calendar paramCalendar, String paramString)
    {
      if ((paramString.charAt(0) == '+') || (paramString.charAt(0) == '-')) {
        paramFastDateParser = TimeZone.getTimeZone("GMT" + paramString);
      }
      TimeZone localTimeZone;
      do
      {
        for (;;)
        {
          paramCalendar.setTimeZone(paramFastDateParser);
          return;
          if (!paramString.startsWith("GMT")) {
            break;
          }
          paramFastDateParser = TimeZone.getTimeZone(paramString);
        }
        localTimeZone = (TimeZone)this.tzNames.get(paramString);
        paramFastDateParser = localTimeZone;
      } while (localTimeZone != null);
      throw new IllegalArgumentException(paramString + " is not a supported timezone name");
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/time/FastDateParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */