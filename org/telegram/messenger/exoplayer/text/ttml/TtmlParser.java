package org.telegram.messenger.exoplayer.text.ttml;

import android.text.Layout.Alignment;
import android.util.Log;
import android.util.Pair;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.messenger.exoplayer.ParserException;
import org.telegram.messenger.exoplayer.text.SubtitleParser;
import org.telegram.messenger.exoplayer.util.ParserUtil;
import org.telegram.messenger.exoplayer.util.Util;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public final class TtmlParser
  implements SubtitleParser
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
  private static final String TAG = "TtmlParser";
  private static final String TTP = "http://www.w3.org/ns/ttml#parameter";
  private final XmlPullParserFactory xmlParserFactory;
  
  static
  {
    FONT_SIZE = Pattern.compile("^(([0-9]*.)?[0-9]+)(px|em|%)$");
    PERCENTAGE_COORDINATES = Pattern.compile("^(\\d+\\.?\\d*?)% (\\d+\\.?\\d*?)%$");
  }
  
  public TtmlParser()
  {
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
    return (paramString.equals("tt")) || (paramString.equals("head")) || (paramString.equals("body")) || (paramString.equals("div")) || (paramString.equals("p")) || (paramString.equals("span")) || (paramString.equals("br")) || (paramString.equals("style")) || (paramString.equals("styling")) || (paramString.equals("layout")) || (paramString.equals("region")) || (paramString.equals("metadata")) || (paramString.equals("smpte:image")) || (paramString.equals("smpte:data")) || (paramString.equals("smpte:information"));
  }
  
  private static void parseFontSize(String paramString, TtmlStyle paramTtmlStyle)
    throws ParserException
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
        throw new ParserException("Invalid unit for fontSize: '" + paramString + "'.");
        if (localObject.length == 2)
        {
          localObject = FONT_SIZE.matcher(localObject[1]);
          Log.w("TtmlParser", "Multiple values in fontSize attribute. Picking the second value for vertical font size and ignoring the first.");
          break label21;
        }
        throw new ParserException("Invalid number of entries for fontSize: " + localObject.length + ".");
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
    throw new ParserException("Invalid expression for fontSize: '" + paramString + "'.");
  }
  
  private FrameAndTickRate parseFrameAndTickRates(XmlPullParser paramXmlPullParser)
    throws ParserException
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
        throw new ParserException("frameRateMultiplier doesn't have 2 parts");
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
    Object localObject1;
    if (ParserUtil.isStartTag(paramXmlPullParser, "style"))
    {
      Object localObject2 = ParserUtil.getAttributeValue(paramXmlPullParser, "style");
      localObject1 = parseStyleAttributes(paramXmlPullParser, new TtmlStyle());
      if (localObject2 != null)
      {
        localObject2 = parseStyleIds((String)localObject2);
        int i = 0;
        while (i < localObject2.length)
        {
          ((TtmlStyle)localObject1).chain((TtmlStyle)paramMap.get(localObject2[i]));
          i += 1;
        }
      }
      if (((TtmlStyle)localObject1).getId() != null) {
        paramMap.put(((TtmlStyle)localObject1).getId(), localObject1);
      }
    }
    while (ParserUtil.isEndTag(paramXmlPullParser, "head"))
    {
      return paramMap;
      if (ParserUtil.isStartTag(paramXmlPullParser, "region"))
      {
        localObject1 = parseRegionAttributes(paramXmlPullParser);
        if (localObject1 != null) {
          paramMap1.put(((Pair)localObject1).first, ((Pair)localObject1).second);
        }
      }
    }
  }
  
  private TtmlNode parseNode(XmlPullParser paramXmlPullParser, TtmlNode paramTtmlNode, Map<String, TtmlRegion> paramMap, FrameAndTickRate paramFrameAndTickRate)
    throws ParserException
  {
    long l5 = 0L;
    long l1 = -1L;
    long l2 = -1L;
    Object localObject1 = "";
    Object localObject2 = null;
    int j = paramXmlPullParser.getAttributeCount();
    TtmlStyle localTtmlStyle = parseStyleAttributes(paramXmlPullParser, null);
    int i = 0;
    long l3;
    if (i < j)
    {
      String str = paramXmlPullParser.getAttributeName(i);
      Object localObject5 = paramXmlPullParser.getAttributeValue(i);
      Object localObject4;
      Object localObject3;
      if ("begin".equals(str))
      {
        l3 = parseTimeExpression((String)localObject5, paramFrameAndTickRate);
        l6 = l5;
        localObject4 = localObject1;
        localObject3 = localObject2;
        l4 = l2;
      }
      for (;;)
      {
        i += 1;
        l1 = l3;
        l2 = l4;
        localObject2 = localObject3;
        localObject1 = localObject4;
        l5 = l6;
        break;
        if ("end".equals(str))
        {
          l4 = parseTimeExpression((String)localObject5, paramFrameAndTickRate);
          l3 = l1;
          localObject3 = localObject2;
          localObject4 = localObject1;
          l6 = l5;
        }
        else if ("dur".equals(str))
        {
          l6 = parseTimeExpression((String)localObject5, paramFrameAndTickRate);
          l3 = l1;
          l4 = l2;
          localObject3 = localObject2;
          localObject4 = localObject1;
        }
        else if ("style".equals(str))
        {
          localObject5 = parseStyleIds((String)localObject5);
          l3 = l1;
          l4 = l2;
          localObject3 = localObject2;
          localObject4 = localObject1;
          l6 = l5;
          if (localObject5.length > 0)
          {
            localObject3 = localObject5;
            l3 = l1;
            l4 = l2;
            localObject4 = localObject1;
            l6 = l5;
          }
        }
        else
        {
          l3 = l1;
          l4 = l2;
          localObject3 = localObject2;
          localObject4 = localObject1;
          l6 = l5;
          if ("region".equals(str))
          {
            l3 = l1;
            l4 = l2;
            localObject3 = localObject2;
            localObject4 = localObject1;
            l6 = l5;
            if (paramMap.containsKey(localObject5))
            {
              localObject4 = localObject5;
              l3 = l1;
              l4 = l2;
              localObject3 = localObject2;
              l6 = l5;
            }
          }
        }
      }
    }
    long l6 = l1;
    long l4 = l2;
    if (paramTtmlNode != null)
    {
      l6 = l1;
      l4 = l2;
      if (paramTtmlNode.startTimeUs != -1L)
      {
        l3 = l1;
        if (l1 != -1L) {
          l3 = l1 + paramTtmlNode.startTimeUs;
        }
        l6 = l3;
        l4 = l2;
        if (l2 != -1L)
        {
          l4 = l2 + paramTtmlNode.startTimeUs;
          l6 = l3;
        }
      }
    }
    l1 = l4;
    if (l4 == -1L)
    {
      if (l5 <= 0L) {
        break label488;
      }
      l1 = l6 + l5;
    }
    for (;;)
    {
      return TtmlNode.buildNode(paramXmlPullParser.getName(), l6, l1, localTtmlStyle, (String[])localObject2, (String)localObject1);
      label488:
      l1 = l4;
      if (paramTtmlNode != null)
      {
        l1 = l4;
        if (paramTtmlNode.endTimeUs != -1L) {
          l1 = paramTtmlNode.endTimeUs;
        }
      }
    }
  }
  
  private Pair<String, TtmlRegion> parseRegionAttributes(XmlPullParser paramXmlPullParser)
  {
    String str = ParserUtil.getAttributeValue(paramXmlPullParser, "id");
    localObject = ParserUtil.getAttributeValue(paramXmlPullParser, "origin");
    paramXmlPullParser = ParserUtil.getAttributeValue(paramXmlPullParser, "extent");
    if ((localObject == null) || (str == null)) {}
    do
    {
      return null;
      float f1 = Float.MIN_VALUE;
      float f3 = Float.MIN_VALUE;
      Matcher localMatcher = PERCENTAGE_COORDINATES.matcher((CharSequence)localObject);
      float f2 = f3;
      if (localMatcher.matches()) {}
      try
      {
        f1 = Float.parseFloat(localMatcher.group(1)) / 100.0F;
        f2 = Float.parseFloat(localMatcher.group(2));
        f2 /= 100.0F;
      }
      catch (NumberFormatException localNumberFormatException2)
      {
        for (;;)
        {
          Log.w("TtmlParser", "Ignoring region with malformed origin: '" + (String)localObject + "'", localNumberFormatException2);
          f1 = Float.MIN_VALUE;
          f2 = f3;
        }
      }
      float f4 = Float.MIN_VALUE;
      f3 = f4;
      if (paramXmlPullParser != null)
      {
        localObject = PERCENTAGE_COORDINATES.matcher(paramXmlPullParser);
        f3 = f4;
        if (!((Matcher)localObject).matches()) {}
      }
      try
      {
        f3 = Float.parseFloat(((Matcher)localObject).group(1));
        f3 /= 100.0F;
      }
      catch (NumberFormatException localNumberFormatException1)
      {
        for (;;)
        {
          Log.w("TtmlParser", "Ignoring malformed region extent: '" + paramXmlPullParser + "'", localNumberFormatException1);
          f3 = f4;
        }
      }
    } while (f1 == Float.MIN_VALUE);
    return new Pair(str, new TtmlRegion(f1, f2, 0, f3));
  }
  
  private TtmlStyle parseStyleAttributes(XmlPullParser paramXmlPullParser, TtmlStyle paramTtmlStyle)
  {
    int k = paramXmlPullParser.getAttributeCount();
    int j = 0;
    TtmlStyle localTtmlStyle1 = paramTtmlStyle;
    if (j < k)
    {
      String str = paramXmlPullParser.getAttributeValue(j);
      paramTtmlStyle = paramXmlPullParser.getAttributeName(j);
      label128:
      int i;
      switch (paramTtmlStyle.hashCode())
      {
      default: 
        i = -1;
        switch (i)
        {
        default: 
          label130:
          paramTtmlStyle = localTtmlStyle1;
        }
        break;
      }
      for (;;)
      {
        j += 1;
        localTtmlStyle1 = paramTtmlStyle;
        break;
        if (!paramTtmlStyle.equals("id")) {
          break label128;
        }
        i = 0;
        break label130;
        if (!paramTtmlStyle.equals("backgroundColor")) {
          break label128;
        }
        i = 1;
        break label130;
        if (!paramTtmlStyle.equals("color")) {
          break label128;
        }
        i = 2;
        break label130;
        if (!paramTtmlStyle.equals("fontFamily")) {
          break label128;
        }
        i = 3;
        break label130;
        if (!paramTtmlStyle.equals("fontSize")) {
          break label128;
        }
        i = 4;
        break label130;
        if (!paramTtmlStyle.equals("fontWeight")) {
          break label128;
        }
        i = 5;
        break label130;
        if (!paramTtmlStyle.equals("fontStyle")) {
          break label128;
        }
        i = 6;
        break label130;
        if (!paramTtmlStyle.equals("textAlign")) {
          break label128;
        }
        i = 7;
        break label130;
        if (!paramTtmlStyle.equals("textDecoration")) {
          break label128;
        }
        i = 8;
        break label130;
        paramTtmlStyle = localTtmlStyle1;
        if ("style".equals(paramXmlPullParser.getName()))
        {
          paramTtmlStyle = createIfNull(localTtmlStyle1).setId(str);
          continue;
          paramTtmlStyle = createIfNull(localTtmlStyle1);
          try
          {
            paramTtmlStyle.setBackgroundColor(TtmlColorParser.parseColor(str));
          }
          catch (IllegalArgumentException localIllegalArgumentException1)
          {
            Log.w("TtmlParser", "failed parsing background value: '" + str + "'");
          }
          continue;
          paramTtmlStyle = createIfNull(localIllegalArgumentException1);
          try
          {
            paramTtmlStyle.setFontColor(TtmlColorParser.parseColor(str));
          }
          catch (IllegalArgumentException localIllegalArgumentException2)
          {
            Log.w("TtmlParser", "failed parsing color value: '" + str + "'");
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
          catch (ParserException localParserException)
          {
            Log.w("TtmlParser", "failed parsing fontSize value: '" + str + "'");
          }
          continue;
          paramTtmlStyle = createIfNull(localParserException).setBold("bold".equalsIgnoreCase(str));
          continue;
          paramTtmlStyle = createIfNull(localParserException).setItalic("italic".equalsIgnoreCase(str));
          continue;
          paramTtmlStyle = Util.toLowerInvariant(str);
          switch (paramTtmlStyle.hashCode())
          {
          default: 
            label664:
            i = -1;
          }
          for (;;)
          {
            switch (i)
            {
            default: 
              paramTtmlStyle = localParserException;
              break;
            case 0: 
              paramTtmlStyle = createIfNull(localParserException).setTextAlign(Layout.Alignment.ALIGN_NORMAL);
              break;
              if (!paramTtmlStyle.equals("left")) {
                break label664;
              }
              i = 0;
              continue;
              if (!paramTtmlStyle.equals("start")) {
                break label664;
              }
              i = 1;
              continue;
              if (!paramTtmlStyle.equals("right")) {
                break label664;
              }
              i = 2;
              continue;
              if (!paramTtmlStyle.equals("end")) {
                break label664;
              }
              i = 3;
              continue;
              if (!paramTtmlStyle.equals("center")) {
                break label664;
              }
              i = 4;
            }
          }
          paramTtmlStyle = createIfNull(localParserException).setTextAlign(Layout.Alignment.ALIGN_NORMAL);
          continue;
          paramTtmlStyle = createIfNull(localParserException).setTextAlign(Layout.Alignment.ALIGN_OPPOSITE);
          continue;
          paramTtmlStyle = createIfNull(localParserException).setTextAlign(Layout.Alignment.ALIGN_OPPOSITE);
          continue;
          paramTtmlStyle = createIfNull(localParserException).setTextAlign(Layout.Alignment.ALIGN_CENTER);
          continue;
          paramTtmlStyle = Util.toLowerInvariant(str);
          switch (paramTtmlStyle.hashCode())
          {
          default: 
            label912:
            i = -1;
          }
          for (;;)
          {
            switch (i)
            {
            default: 
              paramTtmlStyle = localParserException;
              break;
            case 0: 
              paramTtmlStyle = createIfNull(localParserException).setLinethrough(true);
              break;
              if (!paramTtmlStyle.equals("linethrough")) {
                break label912;
              }
              i = 0;
              continue;
              if (!paramTtmlStyle.equals("nolinethrough")) {
                break label912;
              }
              i = 1;
              continue;
              if (!paramTtmlStyle.equals("underline")) {
                break label912;
              }
              i = 2;
              continue;
              if (!paramTtmlStyle.equals("nounderline")) {
                break label912;
              }
              i = 3;
            }
          }
          paramTtmlStyle = createIfNull(localParserException).setLinethrough(false);
          continue;
          paramTtmlStyle = createIfNull(localParserException).setUnderline(true);
          continue;
          paramTtmlStyle = createIfNull(localParserException).setUnderline(false);
        }
      }
    }
    return localParserException;
  }
  
  private String[] parseStyleIds(String paramString)
  {
    return paramString.split("\\s+");
  }
  
  private static long parseTimeExpression(String paramString, FrameAndTickRate paramFrameAndTickRate)
    throws ParserException
  {
    Matcher localMatcher = CLOCK_TIME.matcher(paramString);
    double d1;
    double d2;
    if (localMatcher.matches())
    {
      double d4 = Long.parseLong(localMatcher.group(1)) * 3600L;
      double d5 = Long.parseLong(localMatcher.group(2)) * 60L;
      double d6 = Long.parseLong(localMatcher.group(3));
      paramString = localMatcher.group(4);
      if (paramString != null)
      {
        d1 = Double.parseDouble(paramString);
        paramString = localMatcher.group(5);
        if (paramString == null) {
          break label159;
        }
        d2 = (float)Long.parseLong(paramString) / paramFrameAndTickRate.effectiveFrameRate;
        label101:
        paramString = localMatcher.group(6);
        if (paramString == null) {
          break label165;
        }
      }
      label159:
      label165:
      for (double d3 = Long.parseLong(paramString) / paramFrameAndTickRate.subFrameRate / paramFrameAndTickRate.effectiveFrameRate;; d3 = 0.0D)
      {
        return (1000000.0D * (d4 + d5 + d6 + d1 + d2 + d3));
        d1 = 0.0D;
        break;
        d2 = 0.0D;
        break label101;
      }
    }
    localMatcher = OFFSET_TIME.matcher(paramString);
    if (localMatcher.matches())
    {
      d2 = Double.parseDouble(localMatcher.group(1));
      paramString = localMatcher.group(2);
      if (paramString.equals("h")) {
        d1 = d2 * 3600.0D;
      }
      for (;;)
      {
        return (1000000.0D * d1);
        if (paramString.equals("m"))
        {
          d1 = d2 * 60.0D;
        }
        else
        {
          d1 = d2;
          if (!paramString.equals("s")) {
            if (paramString.equals("ms"))
            {
              d1 = d2 / 1000.0D;
            }
            else if (paramString.equals("f"))
            {
              d1 = d2 / paramFrameAndTickRate.effectiveFrameRate;
            }
            else
            {
              d1 = d2;
              if (paramString.equals("t")) {
                d1 = d2 / paramFrameAndTickRate.tickRate;
              }
            }
          }
        }
      }
    }
    throw new ParserException("Malformed time expression: " + paramString);
  }
  
  public boolean canParse(String paramString)
  {
    return "application/ttml+xml".equals(paramString);
  }
  
  public TtmlSubtitle parse(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws ParserException
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
        localHashMap1 = new HashMap();
        localHashMap2 = new HashMap();
        localHashMap2.put("", new TtmlRegion());
        localXmlPullParser.setInput(new ByteArrayInputStream(paramArrayOfByte, paramInt1, paramInt2), null);
        paramArrayOfByte = null;
        localLinkedList = new LinkedList();
        paramInt2 = 0;
        i = localXmlPullParser.getEventType();
        localObject1 = DEFAULT_FRAME_AND_TICK_RATE;
        if (i == 1) {
          break;
        }
        localTtmlNode1 = (TtmlNode)localLinkedList.peekLast();
        if (paramInt2 != 0) {
          break label464;
        }
        localObject2 = localXmlPullParser.getName();
        if (i != 2) {
          continue;
        }
        if ("tt".equals(localObject2)) {
          localObject1 = parseFrameAndTickRates(localXmlPullParser);
        }
        if (isSupportedTag((String)localObject2)) {
          continue;
        }
        Log.i("TtmlParser", "Ignoring unsupported tag: " + localXmlPullParser.getName());
        paramInt1 = paramInt2 + 1;
        arrayOfByte = paramArrayOfByte;
        localObject2 = localObject1;
      }
      catch (XmlPullParserException paramArrayOfByte)
      {
        TtmlNode localTtmlNode1;
        Object localObject2;
        throw new ParserException("Unable to parse source", paramArrayOfByte);
        try
        {
          TtmlNode localTtmlNode2 = parseNode(localXmlPullParser, localTtmlNode1, localHashMap2, (FrameAndTickRate)localObject1);
          localLinkedList.addLast(localTtmlNode2);
          localObject2 = localObject1;
          arrayOfByte = paramArrayOfByte;
          paramInt1 = paramInt2;
          if (localTtmlNode1 == null) {
            continue;
          }
          localTtmlNode1.addChild(localTtmlNode2);
          localObject2 = localObject1;
          arrayOfByte = paramArrayOfByte;
          paramInt1 = paramInt2;
        }
        catch (ParserException localParserException)
        {
          Log.w("TtmlParser", "Suppressing parser error", localParserException);
          paramInt1 = paramInt2 + 1;
          localObject3 = localObject1;
          arrayOfByte = paramArrayOfByte;
        }
        continue;
        if (i != 4) {
          break label396;
        }
        localTtmlNode1.addChild(TtmlNode.buildTextNode(localXmlPullParser.getText()));
        localObject3 = localObject1;
        arrayOfByte = paramArrayOfByte;
        paramInt1 = paramInt2;
        continue;
      }
      catch (IOException paramArrayOfByte)
      {
        throw new IllegalStateException("Unexpected error when reading input.", paramArrayOfByte);
      }
      localXmlPullParser.next();
      int i = localXmlPullParser.getEventType();
      Object localObject1 = localObject2;
      paramArrayOfByte = arrayOfByte;
      paramInt2 = paramInt1;
      continue;
      if ("head".equals(localObject2))
      {
        parseHeader(localXmlPullParser, localHashMap1, localHashMap2);
        localObject2 = localObject1;
        arrayOfByte = paramArrayOfByte;
        paramInt1 = paramInt2;
      }
      else
      {
        label396:
        Object localObject3 = localObject1;
        arrayOfByte = paramArrayOfByte;
        paramInt1 = paramInt2;
        if (i == 3)
        {
          if (localXmlPullParser.getName().equals("tt")) {
            paramArrayOfByte = new TtmlSubtitle((TtmlNode)localLinkedList.getLast(), localHashMap1, localHashMap2);
          }
          localLinkedList.removeLast();
          localObject3 = localObject1;
          arrayOfByte = paramArrayOfByte;
          paramInt1 = paramInt2;
          continue;
          label464:
          if (i == 2)
          {
            paramInt1 = paramInt2 + 1;
            localObject3 = localObject1;
            arrayOfByte = paramArrayOfByte;
          }
          else
          {
            localObject3 = localObject1;
            arrayOfByte = paramArrayOfByte;
            paramInt1 = paramInt2;
            if (i == 3)
            {
              paramInt1 = paramInt2 - 1;
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


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/text/ttml/TtmlParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */