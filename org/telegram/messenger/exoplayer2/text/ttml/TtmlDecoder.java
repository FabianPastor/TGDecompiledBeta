package org.telegram.messenger.exoplayer2.text.ttml;

import android.text.Layout.Alignment;
import android.util.Log;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.messenger.exoplayer2.text.SimpleSubtitleDecoder;
import org.telegram.messenger.exoplayer2.text.SubtitleDecoderException;
import org.telegram.messenger.exoplayer2.util.ColorParser;
import org.telegram.messenger.exoplayer2.util.Util;
import org.telegram.messenger.exoplayer2.util.XmlPullParserUtil;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public final class TtmlDecoder
  extends SimpleSubtitleDecoder
{
  private static final String ATTR_BEGIN = "begin";
  private static final String ATTR_DURATION = "dur";
  private static final String ATTR_END = "end";
  private static final String ATTR_REGION = "region";
  private static final String ATTR_STYLE = "style";
  private static final Pattern CLOCK_TIME = Pattern.compile("^([0-9][0-9]+):([0-9][0-9]):([0-9][0-9])(?:(\\.[0-9]+)|:([0-9][0-9])(?:\\.([0-9]+))?)?$");
  private static final FrameAndTickRate DEFAULT_FRAME_AND_TICK_RATE = new FrameAndTickRate(30.0F, 1, 1);
  private static final int DEFAULT_FRAME_RATE = 30;
  private static final Pattern FONT_SIZE;
  private static final Pattern OFFSET_TIME = Pattern.compile("^([0-9]+(?:\\.[0-9]+)?)(h|m|s|ms|f|t)$");
  private static final Pattern PERCENTAGE_COORDINATES;
  private static final String TAG = "TtmlDecoder";
  private static final String TTP = "http://www.w3.org/ns/ttml#parameter";
  private final XmlPullParserFactory xmlParserFactory;
  
  static
  {
    FONT_SIZE = Pattern.compile("^(([0-9]*.)?[0-9]+)(px|em|%)$");
    PERCENTAGE_COORDINATES = Pattern.compile("^(\\d+\\.?\\d*?)% (\\d+\\.?\\d*?)%$");
  }
  
  public TtmlDecoder()
  {
    super("TtmlDecoder");
    try
    {
      this.xmlParserFactory = XmlPullParserFactory.newInstance();
      this.xmlParserFactory.setNamespaceAware(true);
      return;
    }
    catch (XmlPullParserException localXmlPullParserException)
    {
      throw new RuntimeException("Couldn't create XmlPullParserFactory instance", localXmlPullParserException);
    }
  }
  
  private TtmlStyle createIfNull(TtmlStyle paramTtmlStyle)
  {
    TtmlStyle localTtmlStyle = paramTtmlStyle;
    if (paramTtmlStyle == null) {
      localTtmlStyle = new TtmlStyle();
    }
    return localTtmlStyle;
  }
  
  private static boolean isSupportedTag(String paramString)
  {
    if ((paramString.equals("tt")) || (paramString.equals("head")) || (paramString.equals("body")) || (paramString.equals("div")) || (paramString.equals("p")) || (paramString.equals("span")) || (paramString.equals("br")) || (paramString.equals("style")) || (paramString.equals("styling")) || (paramString.equals("layout")) || (paramString.equals("region")) || (paramString.equals("metadata")) || (paramString.equals("smpte:image")) || (paramString.equals("smpte:data")) || (paramString.equals("smpte:information"))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  private static void parseFontSize(String paramString, TtmlStyle paramTtmlStyle)
    throws SubtitleDecoderException
  {
    Object localObject = paramString.split("\\s+");
    label21:
    int i;
    if (localObject.length == 1)
    {
      localObject = FONT_SIZE.matcher(paramString);
      if (!((Matcher)localObject).matches()) {
        break label279;
      }
      paramString = ((Matcher)localObject).group(3);
      i = -1;
      switch (paramString.hashCode())
      {
      }
    }
    for (;;)
    {
      switch (i)
      {
      default: 
        throw new SubtitleDecoderException("Invalid unit for fontSize: '" + paramString + "'.");
        if (localObject.length == 2)
        {
          localObject = FONT_SIZE.matcher(localObject[1]);
          Log.w("TtmlDecoder", "Multiple values in fontSize attribute. Picking the second value for vertical font size and ignoring the first.");
          break label21;
        }
        throw new SubtitleDecoderException("Invalid number of entries for fontSize: " + localObject.length + ".");
        if (paramString.equals("px"))
        {
          i = 0;
          continue;
          if (paramString.equals("em"))
          {
            i = 1;
            continue;
            if (paramString.equals("%")) {
              i = 2;
            }
          }
        }
        break;
      }
    }
    paramTtmlStyle.setFontSizeUnit(1);
    for (;;)
    {
      paramTtmlStyle.setFontSize(Float.valueOf(((Matcher)localObject).group(1)).floatValue());
      return;
      paramTtmlStyle.setFontSizeUnit(2);
      continue;
      paramTtmlStyle.setFontSizeUnit(3);
    }
    label279:
    throw new SubtitleDecoderException("Invalid expression for fontSize: '" + paramString + "'.");
  }
  
  private FrameAndTickRate parseFrameAndTickRates(XmlPullParser paramXmlPullParser)
    throws SubtitleDecoderException
  {
    int i = 30;
    Object localObject = paramXmlPullParser.getAttributeValue("http://www.w3.org/ns/ttml#parameter", "frameRate");
    if (localObject != null) {
      i = Integer.parseInt((String)localObject);
    }
    float f = 1.0F;
    localObject = paramXmlPullParser.getAttributeValue("http://www.w3.org/ns/ttml#parameter", "frameRateMultiplier");
    if (localObject != null)
    {
      localObject = ((String)localObject).split(" ");
      if (localObject.length != 2) {
        throw new SubtitleDecoderException("frameRateMultiplier doesn't have 2 parts");
      }
      f = Integer.parseInt(localObject[0]) / Integer.parseInt(localObject[1]);
    }
    int j = DEFAULT_FRAME_AND_TICK_RATE.subFrameRate;
    localObject = paramXmlPullParser.getAttributeValue("http://www.w3.org/ns/ttml#parameter", "subFrameRate");
    if (localObject != null) {
      j = Integer.parseInt((String)localObject);
    }
    int k = DEFAULT_FRAME_AND_TICK_RATE.tickRate;
    paramXmlPullParser = paramXmlPullParser.getAttributeValue("http://www.w3.org/ns/ttml#parameter", "tickRate");
    if (paramXmlPullParser != null) {
      k = Integer.parseInt(paramXmlPullParser);
    }
    return new FrameAndTickRate(i * f, j, k);
  }
  
  private Map<String, TtmlStyle> parseHeader(XmlPullParser paramXmlPullParser, Map<String, TtmlStyle> paramMap, Map<String, TtmlRegion> paramMap1)
    throws IOException, XmlPullParserException
  {
    paramXmlPullParser.next();
    Object localObject2;
    if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "style"))
    {
      Object localObject1 = XmlPullParserUtil.getAttributeValue(paramXmlPullParser, "style");
      localObject2 = parseStyleAttributes(paramXmlPullParser, new TtmlStyle());
      if (localObject1 != null)
      {
        localObject1 = parseStyleIds((String)localObject1);
        int i = localObject1.length;
        for (int j = 0; j < i; j++) {
          ((TtmlStyle)localObject2).chain((TtmlStyle)paramMap.get(localObject1[j]));
        }
      }
      if (((TtmlStyle)localObject2).getId() != null) {
        paramMap.put(((TtmlStyle)localObject2).getId(), localObject2);
      }
    }
    while (XmlPullParserUtil.isEndTag(paramXmlPullParser, "head"))
    {
      return paramMap;
      if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "region"))
      {
        localObject2 = parseRegionAttributes(paramXmlPullParser);
        if (localObject2 != null) {
          paramMap1.put(((TtmlRegion)localObject2).id, localObject2);
        }
      }
    }
  }
  
  private TtmlNode parseNode(XmlPullParser paramXmlPullParser, TtmlNode paramTtmlNode, Map<String, TtmlRegion> paramMap, FrameAndTickRate paramFrameAndTickRate)
    throws SubtitleDecoderException
  {
    long l1 = -9223372036854775807L;
    long l2 = -9223372036854775807L;
    long l3 = -9223372036854775807L;
    Object localObject1 = "";
    Object localObject2 = null;
    int i = paramXmlPullParser.getAttributeCount();
    TtmlStyle localTtmlStyle = parseStyleAttributes(paramXmlPullParser, null);
    int j = 0;
    label128:
    long l6;
    if (j < i)
    {
      Object localObject3 = paramXmlPullParser.getAttributeName(j);
      Object localObject4 = paramXmlPullParser.getAttributeValue(j);
      int k = -1;
      Object localObject5;
      switch (((String)localObject3).hashCode())
      {
      default: 
        switch (k)
        {
        default: 
          l4 = l1;
          localObject5 = localObject1;
          localObject3 = localObject2;
          l5 = l3;
          l6 = l2;
        }
        break;
      }
      for (;;)
      {
        j++;
        l2 = l6;
        l3 = l5;
        localObject2 = localObject3;
        localObject1 = localObject5;
        l1 = l4;
        break;
        if (!((String)localObject3).equals("begin")) {
          break label128;
        }
        k = 0;
        break label128;
        if (!((String)localObject3).equals("end")) {
          break label128;
        }
        k = 1;
        break label128;
        if (!((String)localObject3).equals("dur")) {
          break label128;
        }
        k = 2;
        break label128;
        if (!((String)localObject3).equals("style")) {
          break label128;
        }
        k = 3;
        break label128;
        if (!((String)localObject3).equals("region")) {
          break label128;
        }
        k = 4;
        break label128;
        l6 = parseTimeExpression((String)localObject4, paramFrameAndTickRate);
        l5 = l3;
        localObject3 = localObject2;
        localObject5 = localObject1;
        l4 = l1;
        continue;
        l5 = parseTimeExpression((String)localObject4, paramFrameAndTickRate);
        l6 = l2;
        localObject3 = localObject2;
        localObject5 = localObject1;
        l4 = l1;
        continue;
        l4 = parseTimeExpression((String)localObject4, paramFrameAndTickRate);
        l6 = l2;
        l5 = l3;
        localObject3 = localObject2;
        localObject5 = localObject1;
        continue;
        localObject4 = parseStyleIds((String)localObject4);
        l6 = l2;
        l5 = l3;
        localObject3 = localObject2;
        localObject5 = localObject1;
        l4 = l1;
        if (localObject4.length > 0)
        {
          localObject3 = localObject4;
          l6 = l2;
          l5 = l3;
          localObject5 = localObject1;
          l4 = l1;
          continue;
          l6 = l2;
          l5 = l3;
          localObject3 = localObject2;
          localObject5 = localObject1;
          l4 = l1;
          if (paramMap.containsKey(localObject4))
          {
            localObject5 = localObject4;
            l6 = l2;
            l5 = l3;
            localObject3 = localObject2;
            l4 = l1;
          }
        }
      }
    }
    long l4 = l2;
    long l5 = l3;
    if (paramTtmlNode != null)
    {
      l4 = l2;
      l5 = l3;
      if (paramTtmlNode.startTimeUs != -9223372036854775807L)
      {
        l6 = l2;
        if (l2 != -9223372036854775807L) {
          l6 = l2 + paramTtmlNode.startTimeUs;
        }
        l4 = l6;
        l5 = l3;
        if (l3 != -9223372036854775807L)
        {
          l5 = l3 + paramTtmlNode.startTimeUs;
          l4 = l6;
        }
      }
    }
    l2 = l5;
    if (l5 == -9223372036854775807L)
    {
      if (l1 == -9223372036854775807L) {
        break label617;
      }
      l2 = l4 + l1;
    }
    for (;;)
    {
      return TtmlNode.buildNode(paramXmlPullParser.getName(), l4, l2, localTtmlStyle, (String[])localObject2, (String)localObject1);
      label617:
      l2 = l5;
      if (paramTtmlNode != null)
      {
        l2 = l5;
        if (paramTtmlNode.endTimeUs != -9223372036854775807L) {
          l2 = paramTtmlNode.endTimeUs;
        }
      }
    }
  }
  
  private TtmlRegion parseRegionAttributes(XmlPullParser paramXmlPullParser)
  {
    String str1 = XmlPullParserUtil.getAttributeValue(paramXmlPullParser, "id");
    if (str1 == null)
    {
      paramXmlPullParser = null;
      return paramXmlPullParser;
    }
    String str2 = XmlPullParserUtil.getAttributeValue(paramXmlPullParser, "origin");
    Object localObject;
    if (str2 != null)
    {
      localObject = PERCENTAGE_COORDINATES.matcher(str2);
      if (!((Matcher)localObject).matches()) {}
    }
    for (;;)
    {
      float f2;
      float f3;
      float f5;
      int j;
      try
      {
        f1 = Float.parseFloat(((Matcher)localObject).group(1)) / 100.0F;
        f2 = Float.parseFloat(((Matcher)localObject).group(2));
        f3 = f2 / 100.0F;
        localObject = XmlPullParserUtil.getAttributeValue(paramXmlPullParser, "extent");
        if (localObject == null) {
          break label401;
        }
        localObject = PERCENTAGE_COORDINATES.matcher((CharSequence)localObject);
        if (!((Matcher)localObject).matches()) {
          break label370;
        }
      }
      catch (NumberFormatException paramXmlPullParser)
      {
        try
        {
          float f1;
          float f4 = Float.parseFloat(((Matcher)localObject).group(1)) / 100.0F;
          f2 = Float.parseFloat(((Matcher)localObject).group(2));
          f5 = f2 / 100.0F;
          int i = 0;
          paramXmlPullParser = XmlPullParserUtil.getAttributeValue(paramXmlPullParser, "displayAlign");
          f2 = f3;
          j = i;
          if (paramXmlPullParser != null)
          {
            paramXmlPullParser = Util.toLowerInvariant(paramXmlPullParser);
            j = -1;
          }
          switch (paramXmlPullParser.hashCode())
          {
          default: 
            switch (j)
            {
            default: 
              j = i;
              f2 = f3;
              paramXmlPullParser = new TtmlRegion(str1, f1, f2, 0, j, f4);
            }
            break;
          }
        }
        catch (NumberFormatException paramXmlPullParser)
        {
          Log.w("TtmlDecoder", "Ignoring region with malformed extent: " + str2);
          paramXmlPullParser = null;
        }
        paramXmlPullParser = paramXmlPullParser;
        Log.w("TtmlDecoder", "Ignoring region with malformed origin: " + str2);
        paramXmlPullParser = null;
      }
      break;
      Log.w("TtmlDecoder", "Ignoring region with unsupported origin: " + str2);
      paramXmlPullParser = null;
      break;
      Log.w("TtmlDecoder", "Ignoring region without an origin");
      paramXmlPullParser = null;
      break;
      break;
      label370:
      Log.w("TtmlDecoder", "Ignoring region with unsupported extent: " + str2);
      paramXmlPullParser = null;
      break;
      label401:
      Log.w("TtmlDecoder", "Ignoring region without an extent");
      paramXmlPullParser = null;
      break;
      if (paramXmlPullParser.equals("center"))
      {
        j = 0;
        continue;
        if (paramXmlPullParser.equals("after"))
        {
          j = 1;
          continue;
          j = 1;
          f2 = f3 + f5 / 2.0F;
          continue;
          j = 2;
          f2 = f3 + f5;
        }
      }
    }
  }
  
  private TtmlStyle parseStyleAttributes(XmlPullParser paramXmlPullParser, TtmlStyle paramTtmlStyle)
  {
    int i = paramXmlPullParser.getAttributeCount();
    int j = 0;
    TtmlStyle localTtmlStyle1 = paramTtmlStyle;
    if (j < i)
    {
      String str = paramXmlPullParser.getAttributeValue(j);
      paramTtmlStyle = paramXmlPullParser.getAttributeName(j);
      label124:
      int k;
      switch (paramTtmlStyle.hashCode())
      {
      default: 
        k = -1;
        switch (k)
        {
        default: 
          label127:
          paramTtmlStyle = localTtmlStyle1;
        }
        break;
      }
      for (;;)
      {
        j++;
        localTtmlStyle1 = paramTtmlStyle;
        break;
        if (!paramTtmlStyle.equals("id")) {
          break label124;
        }
        k = 0;
        break label127;
        if (!paramTtmlStyle.equals("backgroundColor")) {
          break label124;
        }
        k = 1;
        break label127;
        if (!paramTtmlStyle.equals("color")) {
          break label124;
        }
        k = 2;
        break label127;
        if (!paramTtmlStyle.equals("fontFamily")) {
          break label124;
        }
        k = 3;
        break label127;
        if (!paramTtmlStyle.equals("fontSize")) {
          break label124;
        }
        k = 4;
        break label127;
        if (!paramTtmlStyle.equals("fontWeight")) {
          break label124;
        }
        k = 5;
        break label127;
        if (!paramTtmlStyle.equals("fontStyle")) {
          break label124;
        }
        k = 6;
        break label127;
        if (!paramTtmlStyle.equals("textAlign")) {
          break label124;
        }
        k = 7;
        break label127;
        if (!paramTtmlStyle.equals("textDecoration")) {
          break label124;
        }
        k = 8;
        break label127;
        paramTtmlStyle = localTtmlStyle1;
        if ("style".equals(paramXmlPullParser.getName()))
        {
          paramTtmlStyle = createIfNull(localTtmlStyle1).setId(str);
          continue;
          paramTtmlStyle = createIfNull(localTtmlStyle1);
          try
          {
            paramTtmlStyle.setBackgroundColor(ColorParser.parseTtmlColor(str));
          }
          catch (IllegalArgumentException localIllegalArgumentException1)
          {
            Log.w("TtmlDecoder", "Failed parsing background value: " + str);
          }
          continue;
          paramTtmlStyle = createIfNull(localIllegalArgumentException1);
          try
          {
            paramTtmlStyle.setFontColor(ColorParser.parseTtmlColor(str));
          }
          catch (IllegalArgumentException localIllegalArgumentException2)
          {
            Log.w("TtmlDecoder", "Failed parsing color value: " + str);
          }
          continue;
          paramTtmlStyle = createIfNull(localIllegalArgumentException2).setFontFamily(str);
          continue;
          paramTtmlStyle = localIllegalArgumentException2;
          try
          {
            TtmlStyle localTtmlStyle2 = createIfNull(localIllegalArgumentException2);
            paramTtmlStyle = localTtmlStyle2;
            parseFontSize(str, localTtmlStyle2);
            paramTtmlStyle = localTtmlStyle2;
          }
          catch (SubtitleDecoderException localSubtitleDecoderException)
          {
            Log.w("TtmlDecoder", "Failed parsing fontSize value: " + str);
          }
          continue;
          paramTtmlStyle = createIfNull(localSubtitleDecoderException).setBold("bold".equalsIgnoreCase(str));
          continue;
          paramTtmlStyle = createIfNull(localSubtitleDecoderException).setItalic("italic".equalsIgnoreCase(str));
          continue;
          paramTtmlStyle = Util.toLowerInvariant(str);
          switch (paramTtmlStyle.hashCode())
          {
          default: 
            label652:
            k = -1;
          }
          for (;;)
          {
            switch (k)
            {
            default: 
              paramTtmlStyle = localSubtitleDecoderException;
              break;
            case 0: 
              paramTtmlStyle = createIfNull(localSubtitleDecoderException).setTextAlign(Layout.Alignment.ALIGN_NORMAL);
              break;
              if (!paramTtmlStyle.equals("left")) {
                break label652;
              }
              k = 0;
              continue;
              if (!paramTtmlStyle.equals("start")) {
                break label652;
              }
              k = 1;
              continue;
              if (!paramTtmlStyle.equals("right")) {
                break label652;
              }
              k = 2;
              continue;
              if (!paramTtmlStyle.equals("end")) {
                break label652;
              }
              k = 3;
              continue;
              if (!paramTtmlStyle.equals("center")) {
                break label652;
              }
              k = 4;
            }
          }
          paramTtmlStyle = createIfNull(localSubtitleDecoderException).setTextAlign(Layout.Alignment.ALIGN_NORMAL);
          continue;
          paramTtmlStyle = createIfNull(localSubtitleDecoderException).setTextAlign(Layout.Alignment.ALIGN_OPPOSITE);
          continue;
          paramTtmlStyle = createIfNull(localSubtitleDecoderException).setTextAlign(Layout.Alignment.ALIGN_OPPOSITE);
          continue;
          paramTtmlStyle = createIfNull(localSubtitleDecoderException).setTextAlign(Layout.Alignment.ALIGN_CENTER);
          continue;
          paramTtmlStyle = Util.toLowerInvariant(str);
          switch (paramTtmlStyle.hashCode())
          {
          default: 
            label908:
            k = -1;
          }
          for (;;)
          {
            switch (k)
            {
            default: 
              paramTtmlStyle = localSubtitleDecoderException;
              break;
            case 0: 
              paramTtmlStyle = createIfNull(localSubtitleDecoderException).setLinethrough(true);
              break;
              if (!paramTtmlStyle.equals("linethrough")) {
                break label908;
              }
              k = 0;
              continue;
              if (!paramTtmlStyle.equals("nolinethrough")) {
                break label908;
              }
              k = 1;
              continue;
              if (!paramTtmlStyle.equals("underline")) {
                break label908;
              }
              k = 2;
              continue;
              if (!paramTtmlStyle.equals("nounderline")) {
                break label908;
              }
              k = 3;
            }
          }
          paramTtmlStyle = createIfNull(localSubtitleDecoderException).setLinethrough(false);
          continue;
          paramTtmlStyle = createIfNull(localSubtitleDecoderException).setUnderline(true);
          continue;
          paramTtmlStyle = createIfNull(localSubtitleDecoderException).setUnderline(false);
        }
      }
    }
    return localSubtitleDecoderException;
  }
  
  private String[] parseStyleIds(String paramString)
  {
    return paramString.split("\\s+");
  }
  
  private static long parseTimeExpression(String paramString, FrameAndTickRate paramFrameAndTickRate)
    throws SubtitleDecoderException
  {
    Matcher localMatcher = CLOCK_TIME.matcher(paramString);
    double d4;
    double d5;
    label94:
    long l;
    if (localMatcher.matches())
    {
      double d1 = Long.parseLong(localMatcher.group(1)) * 3600L;
      double d2 = Long.parseLong(localMatcher.group(2)) * 60L;
      double d3 = Long.parseLong(localMatcher.group(3));
      paramString = localMatcher.group(4);
      if (paramString != null)
      {
        d4 = Double.parseDouble(paramString);
        paramString = localMatcher.group(5);
        if (paramString == null) {
          break label156;
        }
        d5 = (float)Long.parseLong(paramString) / paramFrameAndTickRate.effectiveFrameRate;
        paramString = localMatcher.group(6);
        if (paramString == null) {
          break label162;
        }
      }
      label156:
      label162:
      for (double d6 = Long.parseLong(paramString) / paramFrameAndTickRate.subFrameRate / paramFrameAndTickRate.effectiveFrameRate;; d6 = 0.0D)
      {
        l = (1000000.0D * (d1 + d2 + d3 + d4 + d5 + d6));
        return l;
        d4 = 0.0D;
        break;
        d5 = 0.0D;
        break label94;
      }
    }
    localMatcher = OFFSET_TIME.matcher(paramString);
    if (localMatcher.matches())
    {
      d5 = Double.parseDouble(localMatcher.group(1));
      paramString = localMatcher.group(2);
      int i = -1;
      switch (paramString.hashCode())
      {
      default: 
        label264:
        d4 = d5;
        switch (i)
        {
        default: 
          d4 = d5;
        }
        break;
      }
      for (;;)
      {
        l = (1000000.0D * d4);
        break;
        if (!paramString.equals("h")) {
          break label264;
        }
        i = 0;
        break label264;
        if (!paramString.equals("m")) {
          break label264;
        }
        i = 1;
        break label264;
        if (!paramString.equals("s")) {
          break label264;
        }
        i = 2;
        break label264;
        if (!paramString.equals("ms")) {
          break label264;
        }
        i = 3;
        break label264;
        if (!paramString.equals("f")) {
          break label264;
        }
        i = 4;
        break label264;
        if (!paramString.equals("t")) {
          break label264;
        }
        i = 5;
        break label264;
        d4 = d5 * 3600.0D;
        continue;
        d4 = d5 * 60.0D;
        continue;
        d4 = d5 / 1000.0D;
        continue;
        d4 = d5 / paramFrameAndTickRate.effectiveFrameRate;
        continue;
        d4 = d5 / paramFrameAndTickRate.tickRate;
      }
    }
    throw new SubtitleDecoderException("Malformed time expression: " + paramString);
  }
  
  protected TtmlSubtitle decode(byte[] paramArrayOfByte, int paramInt, boolean paramBoolean)
    throws SubtitleDecoderException
  {
    for (;;)
    {
      XmlPullParser localXmlPullParser;
      HashMap localHashMap1;
      HashMap localHashMap2;
      LinkedList localLinkedList;
      byte[] arrayOfByte;
      try
      {
        localXmlPullParser = this.xmlParserFactory.newPullParser();
        localHashMap1 = new java/util/HashMap;
        localHashMap1.<init>();
        localHashMap2 = new java/util/HashMap;
        localHashMap2.<init>();
        localObject1 = new org/telegram/messenger/exoplayer2/text/ttml/TtmlRegion;
        ((TtmlRegion)localObject1).<init>(null);
        localHashMap2.put("", localObject1);
        localObject1 = new java/io/ByteArrayInputStream;
        ((ByteArrayInputStream)localObject1).<init>(paramArrayOfByte, 0, paramInt);
        localXmlPullParser.setInput((InputStream)localObject1, null);
        paramArrayOfByte = null;
        localLinkedList = new java/util/LinkedList;
        localLinkedList.<init>();
        i = 0;
        j = localXmlPullParser.getEventType();
        localObject1 = DEFAULT_FRAME_AND_TICK_RATE;
        if (j == 1) {
          break;
        }
        localTtmlNode1 = (TtmlNode)localLinkedList.peekLast();
        if (i != 0) {
          break label494;
        }
        localObject2 = localXmlPullParser.getName();
        if (j != 2) {
          continue;
        }
        if ("tt".equals(localObject2)) {
          localObject1 = parseFrameAndTickRates(localXmlPullParser);
        }
        if (isSupportedTag((String)localObject2)) {
          continue;
        }
        localObject2 = new java/lang/StringBuilder;
        ((StringBuilder)localObject2).<init>();
        Log.i("TtmlDecoder", "Ignoring unsupported tag: " + localXmlPullParser.getName());
        paramInt = i + 1;
        arrayOfByte = paramArrayOfByte;
        localObject2 = localObject1;
      }
      catch (XmlPullParserException paramArrayOfByte)
      {
        TtmlNode localTtmlNode1;
        Object localObject2;
        throw new SubtitleDecoderException("Unable to decode source", paramArrayOfByte);
        try
        {
          TtmlNode localTtmlNode2 = parseNode(localXmlPullParser, localTtmlNode1, localHashMap2, (FrameAndTickRate)localObject1);
          localLinkedList.addLast(localTtmlNode2);
          localObject2 = localObject1;
          arrayOfByte = paramArrayOfByte;
          paramInt = i;
          if (localTtmlNode1 == null) {
            continue;
          }
          localTtmlNode1.addChild(localTtmlNode2);
          localObject2 = localObject1;
          arrayOfByte = paramArrayOfByte;
          paramInt = i;
        }
        catch (SubtitleDecoderException localSubtitleDecoderException)
        {
          Log.w("TtmlDecoder", "Suppressing parser error", localSubtitleDecoderException);
          paramInt = i + 1;
          localObject3 = localObject1;
          arrayOfByte = paramArrayOfByte;
        }
        continue;
        if (j != 4) {
          break label424;
        }
        localTtmlNode1.addChild(TtmlNode.buildTextNode(localXmlPullParser.getText()));
        localObject3 = localObject1;
        arrayOfByte = paramArrayOfByte;
        paramInt = i;
        continue;
      }
      catch (IOException paramArrayOfByte)
      {
        throw new IllegalStateException("Unexpected error when reading input.", paramArrayOfByte);
      }
      localXmlPullParser.next();
      int j = localXmlPullParser.getEventType();
      Object localObject1 = localObject2;
      paramArrayOfByte = arrayOfByte;
      int i = paramInt;
      continue;
      if ("head".equals(localObject2))
      {
        parseHeader(localXmlPullParser, localHashMap1, localHashMap2);
        localObject2 = localObject1;
        arrayOfByte = paramArrayOfByte;
        paramInt = i;
      }
      else
      {
        label424:
        Object localObject3 = localObject1;
        arrayOfByte = paramArrayOfByte;
        paramInt = i;
        if (j == 3)
        {
          if (localXmlPullParser.getName().equals("tt"))
          {
            paramArrayOfByte = new org/telegram/messenger/exoplayer2/text/ttml/TtmlSubtitle;
            paramArrayOfByte.<init>((TtmlNode)localLinkedList.getLast(), localHashMap1, localHashMap2);
          }
          localLinkedList.removeLast();
          localObject3 = localObject1;
          arrayOfByte = paramArrayOfByte;
          paramInt = i;
          continue;
          label494:
          if (j == 2)
          {
            paramInt = i + 1;
            localObject3 = localObject1;
            arrayOfByte = paramArrayOfByte;
          }
          else
          {
            localObject3 = localObject1;
            arrayOfByte = paramArrayOfByte;
            paramInt = i;
            if (j == 3)
            {
              paramInt = i - 1;
              localObject3 = localObject1;
              arrayOfByte = paramArrayOfByte;
            }
          }
        }
      }
    }
    return paramArrayOfByte;
  }
  
  private static final class FrameAndTickRate
  {
    final float effectiveFrameRate;
    final int subFrameRate;
    final int tickRate;
    
    FrameAndTickRate(float paramFloat, int paramInt1, int paramInt2)
    {
      this.effectiveFrameRate = paramFloat;
      this.subFrameRate = paramInt1;
      this.tickRate = paramInt2;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/text/ttml/TtmlDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */