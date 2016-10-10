package org.telegram.messenger.exoplayer.dash.mpd;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.messenger.exoplayer.ParserException;
import org.telegram.messenger.exoplayer.chunk.Format;
import org.telegram.messenger.exoplayer.drm.DrmInitData.SchemeInitData;
import org.telegram.messenger.exoplayer.extractor.mp4.PsshAtomUtil;
import org.telegram.messenger.exoplayer.upstream.UriLoadable.Parser;
import org.telegram.messenger.exoplayer.util.Assertions;
import org.telegram.messenger.exoplayer.util.MimeTypes;
import org.telegram.messenger.exoplayer.util.ParserUtil;
import org.telegram.messenger.exoplayer.util.UriUtil;
import org.telegram.messenger.exoplayer.util.Util;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class MediaPresentationDescriptionParser
  extends DefaultHandler
  implements UriLoadable.Parser<MediaPresentationDescription>
{
  private static final Pattern FRAME_RATE_PATTERN = Pattern.compile("(\\d+)(?:/(\\d+))?");
  private static final String TAG = "MediaPresentationDescriptionParser";
  private final String contentId;
  private final XmlPullParserFactory xmlParserFactory;
  
  public MediaPresentationDescriptionParser()
  {
    this(null);
  }
  
  public MediaPresentationDescriptionParser(String paramString)
  {
    this.contentId = paramString;
    try
    {
      this.xmlParserFactory = XmlPullParserFactory.newInstance();
      return;
    }
    catch (XmlPullParserException paramString)
    {
      throw new RuntimeException("Couldn't create XmlPullParserFactory instance", paramString);
    }
  }
  
  private static int checkContentTypeConsistency(int paramInt1, int paramInt2)
  {
    int i;
    if (paramInt1 == -1) {
      i = paramInt2;
    }
    do
    {
      return i;
      i = paramInt1;
    } while (paramInt2 == -1);
    if (paramInt1 == paramInt2) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      return paramInt1;
    }
  }
  
  private static String checkLanguageConsistency(String paramString1, String paramString2)
  {
    String str;
    if (paramString1 == null) {
      str = paramString2;
    }
    do
    {
      return str;
      str = paramString1;
    } while (paramString2 == null);
    Assertions.checkState(paramString1.equals(paramString2));
    return paramString1;
  }
  
  protected static String parseBaseUrl(XmlPullParser paramXmlPullParser, String paramString)
    throws XmlPullParserException, IOException
  {
    paramXmlPullParser.next();
    return UriUtil.resolve(paramString, paramXmlPullParser.getText());
  }
  
  protected static long parseDateTime(XmlPullParser paramXmlPullParser, String paramString, long paramLong)
    throws ParseException
  {
    paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, paramString);
    if (paramXmlPullParser == null) {
      return paramLong;
    }
    return Util.parseXsDateTime(paramXmlPullParser);
  }
  
  protected static long parseDuration(XmlPullParser paramXmlPullParser, String paramString, long paramLong)
  {
    paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, paramString);
    if (paramXmlPullParser == null) {
      return paramLong;
    }
    return Util.parseXsDuration(paramXmlPullParser);
  }
  
  protected static float parseFrameRate(XmlPullParser paramXmlPullParser, float paramFloat)
  {
    paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, "frameRate");
    float f = paramFloat;
    int i;
    if (paramXmlPullParser != null)
    {
      paramXmlPullParser = FRAME_RATE_PATTERN.matcher(paramXmlPullParser);
      f = paramFloat;
      if (paramXmlPullParser.matches())
      {
        i = Integer.parseInt(paramXmlPullParser.group(1));
        paramXmlPullParser = paramXmlPullParser.group(2);
        if (TextUtils.isEmpty(paramXmlPullParser)) {
          break label66;
        }
        f = i / Integer.parseInt(paramXmlPullParser);
      }
    }
    return f;
    label66:
    return i;
  }
  
  protected static int parseInt(XmlPullParser paramXmlPullParser, String paramString)
  {
    return parseInt(paramXmlPullParser, paramString, -1);
  }
  
  protected static int parseInt(XmlPullParser paramXmlPullParser, String paramString, int paramInt)
  {
    paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, paramString);
    if (paramXmlPullParser == null) {
      return paramInt;
    }
    return Integer.parseInt(paramXmlPullParser);
  }
  
  protected static long parseLong(XmlPullParser paramXmlPullParser, String paramString)
  {
    return parseLong(paramXmlPullParser, paramString, -1L);
  }
  
  protected static long parseLong(XmlPullParser paramXmlPullParser, String paramString, long paramLong)
  {
    paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, paramString);
    if (paramXmlPullParser == null) {
      return paramLong;
    }
    return Long.parseLong(paramXmlPullParser);
  }
  
  protected static String parseString(XmlPullParser paramXmlPullParser, String paramString1, String paramString2)
  {
    paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, paramString1);
    if (paramXmlPullParser == null) {
      return paramString2;
    }
    return paramXmlPullParser;
  }
  
  protected AdaptationSet buildAdaptationSet(int paramInt1, int paramInt2, List<Representation> paramList, List<ContentProtection> paramList1)
  {
    return new AdaptationSet(paramInt1, paramInt2, paramList, paramList1);
  }
  
  protected ContentProtection buildContentProtection(String paramString, UUID paramUUID, DrmInitData.SchemeInitData paramSchemeInitData)
  {
    return new ContentProtection(paramString, paramUUID, paramSchemeInitData);
  }
  
  protected Format buildFormat(String paramString1, String paramString2, int paramInt1, int paramInt2, float paramFloat, int paramInt3, int paramInt4, int paramInt5, String paramString3, String paramString4)
  {
    return new Format(paramString1, paramString2, paramInt1, paramInt2, paramFloat, paramInt3, paramInt4, paramInt5, paramString3, paramString4);
  }
  
  protected MediaPresentationDescription buildMediaPresentationDescription(long paramLong1, long paramLong2, long paramLong3, boolean paramBoolean, long paramLong4, long paramLong5, UtcTimingElement paramUtcTimingElement, String paramString, List<Period> paramList)
  {
    return new MediaPresentationDescription(paramLong1, paramLong2, paramLong3, paramBoolean, paramLong4, paramLong5, paramUtcTimingElement, paramString, paramList);
  }
  
  protected Period buildPeriod(String paramString, long paramLong, List<AdaptationSet> paramList)
  {
    return new Period(paramString, paramLong, paramList);
  }
  
  protected RangedUri buildRangedUri(String paramString1, String paramString2, long paramLong1, long paramLong2)
  {
    return new RangedUri(paramString1, paramString2, paramLong1, paramLong2);
  }
  
  protected Representation buildRepresentation(String paramString, int paramInt, Format paramFormat, SegmentBase paramSegmentBase)
  {
    return Representation.newInstance(paramString, paramInt, paramFormat, paramSegmentBase);
  }
  
  protected SegmentBase.SegmentList buildSegmentList(RangedUri paramRangedUri, long paramLong1, long paramLong2, int paramInt, long paramLong3, List<SegmentBase.SegmentTimelineElement> paramList, List<RangedUri> paramList1)
  {
    return new SegmentBase.SegmentList(paramRangedUri, paramLong1, paramLong2, paramInt, paramLong3, paramList, paramList1);
  }
  
  protected SegmentBase.SegmentTemplate buildSegmentTemplate(RangedUri paramRangedUri, long paramLong1, long paramLong2, int paramInt, long paramLong3, List<SegmentBase.SegmentTimelineElement> paramList, UrlTemplate paramUrlTemplate1, UrlTemplate paramUrlTemplate2, String paramString)
  {
    return new SegmentBase.SegmentTemplate(paramRangedUri, paramLong1, paramLong2, paramInt, paramLong3, paramList, paramUrlTemplate1, paramUrlTemplate2, paramString);
  }
  
  protected SegmentBase.SegmentTimelineElement buildSegmentTimelineElement(long paramLong1, long paramLong2)
  {
    return new SegmentBase.SegmentTimelineElement(paramLong1, paramLong2);
  }
  
  protected SegmentBase.SingleSegmentBase buildSingleSegmentBase(RangedUri paramRangedUri, long paramLong1, long paramLong2, String paramString, long paramLong3, long paramLong4)
  {
    return new SegmentBase.SingleSegmentBase(paramRangedUri, paramLong1, paramLong2, paramString, paramLong3, paramLong4);
  }
  
  protected UtcTimingElement buildUtcTimingElement(String paramString1, String paramString2)
  {
    return new UtcTimingElement(paramString1, paramString2);
  }
  
  protected int getContentType(Representation paramRepresentation)
  {
    String str = paramRepresentation.format.mimeType;
    if (TextUtils.isEmpty(str)) {}
    do
    {
      do
      {
        return -1;
        if (MimeTypes.isVideo(str)) {
          return 0;
        }
        if (MimeTypes.isAudio(str)) {
          return 1;
        }
        if ((MimeTypes.isText(str)) || ("application/ttml+xml".equals(str))) {
          return 2;
        }
      } while (!"application/mp4".equals(str));
      paramRepresentation = paramRepresentation.format.codecs;
    } while ((!"stpp".equals(paramRepresentation)) && (!"wvtt".equals(paramRepresentation)));
    return 2;
  }
  
  public MediaPresentationDescription parse(String paramString, InputStream paramInputStream)
    throws IOException, ParserException
  {
    try
    {
      localXmlPullParser = this.xmlParserFactory.newPullParser();
      localXmlPullParser.setInput(paramInputStream, null);
      if ((localXmlPullParser.next() != 2) || (!"MPD".equals(localXmlPullParser.getName()))) {
        throw new ParserException("inputStream does not contain a valid media presentation description");
      }
    }
    catch (XmlPullParserException paramString)
    {
      XmlPullParser localXmlPullParser;
      throw new ParserException(paramString);
      paramString = parseMediaPresentationDescription(localXmlPullParser, paramString);
      return paramString;
    }
    catch (ParseException paramString)
    {
      throw new ParserException(paramString);
    }
  }
  
  protected AdaptationSet parseAdaptationSet(XmlPullParser paramXmlPullParser, String paramString, SegmentBase paramSegmentBase)
    throws XmlPullParserException, IOException
  {
    int i2 = parseInt(paramXmlPullParser, "id", -1);
    int m = parseContentType(paramXmlPullParser);
    String str1 = paramXmlPullParser.getAttributeValue(null, "mimeType");
    String str2 = paramXmlPullParser.getAttributeValue(null, "codecs");
    int i3 = parseInt(paramXmlPullParser, "width", -1);
    int i4 = parseInt(paramXmlPullParser, "height", -1);
    float f = parseFrameRate(paramXmlPullParser, -1.0F);
    int k = -1;
    int i5 = parseInt(paramXmlPullParser, "audioSamplingRate", -1);
    Object localObject1 = paramXmlPullParser.getAttributeValue(null, "lang");
    ContentProtectionsBuilder localContentProtectionsBuilder = new ContentProtectionsBuilder();
    ArrayList localArrayList = new ArrayList();
    int j = 0;
    Object localObject2 = paramSegmentBase;
    paramSegmentBase = paramString;
    paramXmlPullParser.next();
    int n;
    Object localObject3;
    int i;
    int i1;
    Object localObject4;
    if (ParserUtil.isStartTag(paramXmlPullParser, "BaseURL"))
    {
      n = k;
      localObject3 = localObject1;
      i = m;
      i1 = j;
      localObject4 = paramSegmentBase;
      paramString = (String)localObject2;
      if (j == 0)
      {
        localObject4 = parseBaseUrl(paramXmlPullParser, paramSegmentBase);
        i1 = 1;
        paramString = (String)localObject2;
        i = m;
        localObject3 = localObject1;
        n = k;
      }
    }
    for (;;)
    {
      k = n;
      localObject1 = localObject3;
      m = i;
      j = i1;
      paramSegmentBase = (SegmentBase)localObject4;
      localObject2 = paramString;
      if (!ParserUtil.isEndTag(paramXmlPullParser, "AdaptationSet")) {
        break;
      }
      return buildAdaptationSet(i2, i, localArrayList, localContentProtectionsBuilder.build());
      if (ParserUtil.isStartTag(paramXmlPullParser, "ContentProtection"))
      {
        ContentProtection localContentProtection = parseContentProtection(paramXmlPullParser);
        n = k;
        localObject3 = localObject1;
        i = m;
        i1 = j;
        localObject4 = paramSegmentBase;
        paramString = (String)localObject2;
        if (localContentProtection != null)
        {
          localContentProtectionsBuilder.addAdaptationSetProtection(localContentProtection);
          n = k;
          localObject3 = localObject1;
          i = m;
          i1 = j;
          localObject4 = paramSegmentBase;
          paramString = (String)localObject2;
        }
      }
      else if (ParserUtil.isStartTag(paramXmlPullParser, "ContentComponent"))
      {
        localObject3 = checkLanguageConsistency((String)localObject1, paramXmlPullParser.getAttributeValue(null, "lang"));
        i = checkContentTypeConsistency(m, parseContentType(paramXmlPullParser));
        n = k;
        i1 = j;
        localObject4 = paramSegmentBase;
        paramString = (String)localObject2;
      }
      else if (ParserUtil.isStartTag(paramXmlPullParser, "Representation"))
      {
        paramString = parseRepresentation(paramXmlPullParser, paramSegmentBase, str1, str2, i3, i4, f, k, i5, (String)localObject1, (SegmentBase)localObject2, localContentProtectionsBuilder);
        localContentProtectionsBuilder.endRepresentation();
        i = checkContentTypeConsistency(m, getContentType(paramString));
        localArrayList.add(paramString);
        n = k;
        localObject3 = localObject1;
        i1 = j;
        localObject4 = paramSegmentBase;
        paramString = (String)localObject2;
      }
      else if (ParserUtil.isStartTag(paramXmlPullParser, "AudioChannelConfiguration"))
      {
        n = parseAudioChannelConfiguration(paramXmlPullParser);
        localObject3 = localObject1;
        i = m;
        i1 = j;
        localObject4 = paramSegmentBase;
        paramString = (String)localObject2;
      }
      else if (ParserUtil.isStartTag(paramXmlPullParser, "SegmentBase"))
      {
        paramString = parseSegmentBase(paramXmlPullParser, paramSegmentBase, (SegmentBase.SingleSegmentBase)localObject2);
        n = k;
        localObject3 = localObject1;
        i = m;
        i1 = j;
        localObject4 = paramSegmentBase;
      }
      else if (ParserUtil.isStartTag(paramXmlPullParser, "SegmentList"))
      {
        paramString = parseSegmentList(paramXmlPullParser, paramSegmentBase, (SegmentBase.SegmentList)localObject2);
        n = k;
        localObject3 = localObject1;
        i = m;
        i1 = j;
        localObject4 = paramSegmentBase;
      }
      else if (ParserUtil.isStartTag(paramXmlPullParser, "SegmentTemplate"))
      {
        paramString = parseSegmentTemplate(paramXmlPullParser, paramSegmentBase, (SegmentBase.SegmentTemplate)localObject2);
        n = k;
        localObject3 = localObject1;
        i = m;
        i1 = j;
        localObject4 = paramSegmentBase;
      }
      else
      {
        n = k;
        localObject3 = localObject1;
        i = m;
        i1 = j;
        localObject4 = paramSegmentBase;
        paramString = (String)localObject2;
        if (ParserUtil.isStartTag(paramXmlPullParser))
        {
          parseAdaptationSetChild(paramXmlPullParser);
          n = k;
          localObject3 = localObject1;
          i = m;
          i1 = j;
          localObject4 = paramSegmentBase;
          paramString = (String)localObject2;
        }
      }
    }
  }
  
  protected void parseAdaptationSetChild(XmlPullParser paramXmlPullParser)
    throws XmlPullParserException, IOException
  {}
  
  protected int parseAudioChannelConfiguration(XmlPullParser paramXmlPullParser)
    throws XmlPullParserException, IOException
  {
    int i;
    if ("urn:mpeg:dash:23003:3:audio_channel_configuration:2011".equals(parseString(paramXmlPullParser, "schemeIdUri", null))) {
      i = parseInt(paramXmlPullParser, "value");
    }
    for (;;)
    {
      paramXmlPullParser.next();
      if (ParserUtil.isEndTag(paramXmlPullParser, "AudioChannelConfiguration"))
      {
        return i;
        i = -1;
      }
    }
  }
  
  protected ContentProtection parseContentProtection(XmlPullParser paramXmlPullParser)
    throws XmlPullParserException, IOException
  {
    String str = paramXmlPullParser.getAttributeValue(null, "schemeIdUri");
    Object localObject1 = null;
    Object localObject3 = null;
    int i = 0;
    Object localObject4;
    int j;
    Object localObject2;
    do
    {
      paramXmlPullParser.next();
      localObject4 = localObject3;
      j = i;
      localObject2 = localObject1;
      if (ParserUtil.isStartTag(paramXmlPullParser, "cenc:pssh"))
      {
        localObject4 = localObject3;
        j = i;
        localObject2 = localObject1;
        if (paramXmlPullParser.next() == 4)
        {
          j = 1;
          localObject4 = new DrmInitData.SchemeInitData("video/mp4", Base64.decode(paramXmlPullParser.getText(), 0));
          localObject2 = PsshAtomUtil.parseUuid(((DrmInitData.SchemeInitData)localObject4).data);
        }
      }
      localObject3 = localObject4;
      i = j;
      localObject1 = localObject2;
    } while (!ParserUtil.isEndTag(paramXmlPullParser, "ContentProtection"));
    if ((j != 0) && (localObject2 == null))
    {
      Log.w("MediaPresentationDescriptionParser", "Skipped unsupported ContentProtection element");
      return null;
    }
    return buildContentProtection(str, (UUID)localObject2, (DrmInitData.SchemeInitData)localObject4);
  }
  
  protected int parseContentType(XmlPullParser paramXmlPullParser)
  {
    paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, "contentType");
    if (TextUtils.isEmpty(paramXmlPullParser)) {}
    do
    {
      return -1;
      if ("audio".equals(paramXmlPullParser)) {
        return 1;
      }
      if ("video".equals(paramXmlPullParser)) {
        return 0;
      }
    } while (!"text".equals(paramXmlPullParser));
    return 2;
  }
  
  protected RangedUri parseInitialization(XmlPullParser paramXmlPullParser, String paramString)
  {
    return parseRangedUrl(paramXmlPullParser, paramString, "sourceURL", "range");
  }
  
  protected MediaPresentationDescription parseMediaPresentationDescription(XmlPullParser paramXmlPullParser, String paramString)
    throws XmlPullParserException, IOException, ParseException
  {
    long l6 = parseDateTime(paramXmlPullParser, "availabilityStartTime", -1L);
    long l5 = parseDuration(paramXmlPullParser, "mediaPresentationDuration", -1L);
    long l7 = parseDuration(paramXmlPullParser, "minBufferTime", -1L);
    Object localObject1 = paramXmlPullParser.getAttributeValue(null, "type");
    boolean bool;
    long l2;
    label80:
    long l3;
    label97:
    Object localObject2;
    ArrayList localArrayList;
    long l1;
    label122:
    int j;
    int i;
    long l4;
    Object localObject3;
    Object localObject4;
    int k;
    int m;
    String str;
    if (localObject1 != null)
    {
      bool = ((String)localObject1).equals("dynamic");
      if (!bool) {
        break label285;
      }
      l2 = parseDuration(paramXmlPullParser, "minimumUpdatePeriod", -1L);
      if (!bool) {
        break label293;
      }
      l3 = parseDuration(paramXmlPullParser, "timeShiftBufferDepth", -1L);
      localObject2 = null;
      localObject1 = null;
      localArrayList = new ArrayList();
      if (!bool) {
        break label301;
      }
      l1 = -1L;
      j = 0;
      i = 0;
      l4 = l1;
      label200:
      do
      {
        paramXmlPullParser.next();
        if (!ParserUtil.isStartTag(paramXmlPullParser, "BaseURL")) {
          break;
        }
        localObject3 = localObject2;
        localObject4 = localObject1;
        l1 = l4;
        k = j;
        m = i;
        str = paramString;
        if (i == 0)
        {
          str = parseBaseUrl(paramXmlPullParser, paramString);
          m = 1;
          k = j;
          l1 = l4;
          localObject4 = localObject1;
          localObject3 = localObject2;
        }
        localObject2 = localObject3;
        localObject1 = localObject4;
        l4 = l1;
        j = k;
        i = m;
        paramString = str;
      } while (!ParserUtil.isEndTag(paramXmlPullParser, "MPD"));
      l4 = l5;
      if (l5 == -1L)
      {
        if (l1 == -1L) {
          break label609;
        }
        l4 = l1;
      }
    }
    for (;;)
    {
      if (localArrayList.isEmpty())
      {
        throw new ParserException("No periods found.");
        bool = false;
        break;
        label285:
        l2 = -1L;
        break label80;
        label293:
        l3 = -1L;
        break label97;
        label301:
        l1 = 0L;
        break label122;
        if (ParserUtil.isStartTag(paramXmlPullParser, "UTCTiming"))
        {
          localObject3 = parseUtcTiming(paramXmlPullParser);
          localObject4 = localObject1;
          l1 = l4;
          k = j;
          m = i;
          str = paramString;
          break label200;
        }
        if (ParserUtil.isStartTag(paramXmlPullParser, "Location"))
        {
          localObject4 = paramXmlPullParser.nextText();
          localObject3 = localObject2;
          l1 = l4;
          k = j;
          m = i;
          str = paramString;
          break label200;
        }
        localObject3 = localObject2;
        localObject4 = localObject1;
        l1 = l4;
        k = j;
        m = i;
        str = paramString;
        if (!ParserUtil.isStartTag(paramXmlPullParser, "Period")) {
          break label200;
        }
        localObject3 = localObject2;
        localObject4 = localObject1;
        l1 = l4;
        k = j;
        m = i;
        str = paramString;
        if (j != 0) {
          break label200;
        }
        localObject3 = parsePeriod(paramXmlPullParser, paramString, l4);
        localObject4 = (Period)((Pair)localObject3).first;
        if (((Period)localObject4).startMs == -1L)
        {
          if (bool)
          {
            k = 1;
            localObject3 = localObject2;
            localObject4 = localObject1;
            l1 = l4;
            m = i;
            str = paramString;
            break label200;
          }
          throw new ParserException("Unable to determine start of period " + localArrayList.size());
        }
        l1 = ((Long)((Pair)localObject3).second).longValue();
        if (l1 == -1L) {}
        for (l1 = -1L;; l1 = ((Period)localObject4).startMs + l1)
        {
          localArrayList.add(localObject4);
          localObject3 = localObject2;
          localObject4 = localObject1;
          k = j;
          m = i;
          str = paramString;
          break;
        }
        label609:
        l4 = l5;
        if (!bool) {
          throw new ParserException("Unable to determine duration of static manifest.");
        }
      }
    }
    return buildMediaPresentationDescription(l6, l4, l7, bool, l2, l3, (UtcTimingElement)localObject3, (String)localObject4, localArrayList);
  }
  
  protected Pair<Period, Long> parsePeriod(XmlPullParser paramXmlPullParser, String paramString, long paramLong)
    throws XmlPullParserException, IOException
  {
    String str2 = paramXmlPullParser.getAttributeValue(null, "id");
    paramLong = parseDuration(paramXmlPullParser, "start", paramLong);
    long l = parseDuration(paramXmlPullParser, "duration", -1L);
    String str1 = null;
    ArrayList localArrayList = new ArrayList();
    int i = 0;
    Object localObject1 = paramString;
    paramXmlPullParser.next();
    int j;
    Object localObject2;
    if (ParserUtil.isStartTag(paramXmlPullParser, "BaseURL"))
    {
      j = i;
      paramString = str1;
      localObject2 = localObject1;
      if (i == 0)
      {
        localObject2 = parseBaseUrl(paramXmlPullParser, (String)localObject1);
        j = 1;
        paramString = str1;
      }
    }
    for (;;)
    {
      i = j;
      str1 = paramString;
      localObject1 = localObject2;
      if (!ParserUtil.isEndTag(paramXmlPullParser, "Period")) {
        break;
      }
      return Pair.create(buildPeriod(str2, paramLong, localArrayList), Long.valueOf(l));
      if (ParserUtil.isStartTag(paramXmlPullParser, "AdaptationSet"))
      {
        localArrayList.add(parseAdaptationSet(paramXmlPullParser, (String)localObject1, str1));
        j = i;
        paramString = str1;
        localObject2 = localObject1;
      }
      else if (ParserUtil.isStartTag(paramXmlPullParser, "SegmentBase"))
      {
        paramString = parseSegmentBase(paramXmlPullParser, (String)localObject1, null);
        j = i;
        localObject2 = localObject1;
      }
      else if (ParserUtil.isStartTag(paramXmlPullParser, "SegmentList"))
      {
        paramString = parseSegmentList(paramXmlPullParser, (String)localObject1, null);
        j = i;
        localObject2 = localObject1;
      }
      else
      {
        j = i;
        paramString = str1;
        localObject2 = localObject1;
        if (ParserUtil.isStartTag(paramXmlPullParser, "SegmentTemplate"))
        {
          paramString = parseSegmentTemplate(paramXmlPullParser, (String)localObject1, null);
          j = i;
          localObject2 = localObject1;
        }
      }
    }
  }
  
  protected RangedUri parseRangedUrl(XmlPullParser paramXmlPullParser, String paramString1, String paramString2, String paramString3)
  {
    paramString2 = paramXmlPullParser.getAttributeValue(null, paramString2);
    long l1 = 0L;
    long l3 = -1L;
    paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, paramString3);
    long l2 = l3;
    if (paramXmlPullParser != null)
    {
      paramXmlPullParser = paramXmlPullParser.split("-");
      long l4 = Long.parseLong(paramXmlPullParser[0]);
      l1 = l4;
      l2 = l3;
      if (paramXmlPullParser.length == 2)
      {
        l2 = Long.parseLong(paramXmlPullParser[1]) - l4 + 1L;
        l1 = l4;
      }
    }
    return buildRangedUri(paramString1, paramString2, l1, l2);
  }
  
  protected Representation parseRepresentation(XmlPullParser paramXmlPullParser, String paramString1, String paramString2, String paramString3, int paramInt1, int paramInt2, float paramFloat, int paramInt3, int paramInt4, String paramString4, SegmentBase paramSegmentBase, ContentProtectionsBuilder paramContentProtectionsBuilder)
    throws XmlPullParserException, IOException
  {
    String str1 = paramXmlPullParser.getAttributeValue(null, "id");
    int i = parseInt(paramXmlPullParser, "bandwidth");
    String str2 = parseString(paramXmlPullParser, "mimeType", paramString2);
    String str3 = parseString(paramXmlPullParser, "codecs", paramString3);
    int j = parseInt(paramXmlPullParser, "width", paramInt1);
    int k = parseInt(paramXmlPullParser, "height", paramInt2);
    paramFloat = parseFrameRate(paramXmlPullParser, paramFloat);
    paramInt2 = paramInt3;
    int m = parseInt(paramXmlPullParser, "audioSamplingRate", paramInt4);
    paramInt1 = 0;
    paramString3 = paramString1;
    do
    {
      paramXmlPullParser.next();
      if (!ParserUtil.isStartTag(paramXmlPullParser, "BaseURL")) {
        break;
      }
      paramInt3 = paramInt2;
      paramInt4 = paramInt1;
      paramString2 = paramString3;
      paramString1 = paramSegmentBase;
      if (paramInt1 == 0)
      {
        paramString2 = parseBaseUrl(paramXmlPullParser, paramString3);
        paramInt4 = 1;
        paramString1 = paramSegmentBase;
        paramInt3 = paramInt2;
      }
      paramInt2 = paramInt3;
      paramInt1 = paramInt4;
      paramString3 = paramString2;
      paramSegmentBase = paramString1;
    } while (!ParserUtil.isEndTag(paramXmlPullParser, "Representation"));
    paramXmlPullParser = buildFormat(str1, str2, j, k, paramFloat, paramInt3, m, i, paramString4, str3);
    paramString3 = this.contentId;
    if (paramString1 != null) {}
    for (;;)
    {
      return buildRepresentation(paramString3, -1, paramXmlPullParser, paramString1);
      if (ParserUtil.isStartTag(paramXmlPullParser, "AudioChannelConfiguration"))
      {
        paramInt3 = parseAudioChannelConfiguration(paramXmlPullParser);
        paramInt4 = paramInt1;
        paramString2 = paramString3;
        paramString1 = paramSegmentBase;
        break;
      }
      if (ParserUtil.isStartTag(paramXmlPullParser, "SegmentBase"))
      {
        paramString1 = parseSegmentBase(paramXmlPullParser, paramString3, (SegmentBase.SingleSegmentBase)paramSegmentBase);
        paramInt3 = paramInt2;
        paramInt4 = paramInt1;
        paramString2 = paramString3;
        break;
      }
      if (ParserUtil.isStartTag(paramXmlPullParser, "SegmentList"))
      {
        paramString1 = parseSegmentList(paramXmlPullParser, paramString3, (SegmentBase.SegmentList)paramSegmentBase);
        paramInt3 = paramInt2;
        paramInt4 = paramInt1;
        paramString2 = paramString3;
        break;
      }
      if (ParserUtil.isStartTag(paramXmlPullParser, "SegmentTemplate"))
      {
        paramString1 = parseSegmentTemplate(paramXmlPullParser, paramString3, (SegmentBase.SegmentTemplate)paramSegmentBase);
        paramInt3 = paramInt2;
        paramInt4 = paramInt1;
        paramString2 = paramString3;
        break;
      }
      paramInt3 = paramInt2;
      paramInt4 = paramInt1;
      paramString2 = paramString3;
      paramString1 = paramSegmentBase;
      if (!ParserUtil.isStartTag(paramXmlPullParser, "ContentProtection")) {
        break;
      }
      ContentProtection localContentProtection = parseContentProtection(paramXmlPullParser);
      paramInt3 = paramInt2;
      paramInt4 = paramInt1;
      paramString2 = paramString3;
      paramString1 = paramSegmentBase;
      if (localContentProtection == null) {
        break;
      }
      paramContentProtectionsBuilder.addAdaptationSetProtection(localContentProtection);
      paramInt3 = paramInt2;
      paramInt4 = paramInt1;
      paramString2 = paramString3;
      paramString1 = paramSegmentBase;
      break;
      paramString1 = new SegmentBase.SingleSegmentBase(paramString2);
    }
  }
  
  protected SegmentBase.SingleSegmentBase parseSegmentBase(XmlPullParser paramXmlPullParser, String paramString, SegmentBase.SingleSegmentBase paramSingleSegmentBase)
    throws XmlPullParserException, IOException
  {
    long l1;
    long l3;
    label31:
    long l4;
    label52:
    long l2;
    label62:
    Object localObject;
    if (paramSingleSegmentBase != null)
    {
      l1 = paramSingleSegmentBase.timescale;
      l3 = parseLong(paramXmlPullParser, "timescale", l1);
      if (paramSingleSegmentBase == null) {
        break label184;
      }
      l1 = paramSingleSegmentBase.presentationTimeOffset;
      l4 = parseLong(paramXmlPullParser, "presentationTimeOffset", l1);
      if (paramSingleSegmentBase == null) {
        break label190;
      }
      l1 = paramSingleSegmentBase.indexStart;
      if (paramSingleSegmentBase == null) {
        break label196;
      }
      l2 = paramSingleSegmentBase.indexLength;
      localObject = paramXmlPullParser.getAttributeValue(null, "indexRange");
      if (localObject != null)
      {
        localObject = ((String)localObject).split("-");
        l1 = Long.parseLong(localObject[0]);
        l2 = Long.parseLong(localObject[1]) - l1 + 1L;
      }
      if (paramSingleSegmentBase == null) {
        break label204;
      }
      paramSingleSegmentBase = paramSingleSegmentBase.initialization;
    }
    for (;;)
    {
      paramXmlPullParser.next();
      localObject = paramSingleSegmentBase;
      if (ParserUtil.isStartTag(paramXmlPullParser, "Initialization")) {
        localObject = parseInitialization(paramXmlPullParser, paramString);
      }
      paramSingleSegmentBase = (SegmentBase.SingleSegmentBase)localObject;
      if (ParserUtil.isEndTag(paramXmlPullParser, "SegmentBase"))
      {
        return buildSingleSegmentBase((RangedUri)localObject, l3, l4, paramString, l1, l2);
        l1 = 1L;
        break;
        label184:
        l1 = 0L;
        break label31;
        label190:
        l1 = 0L;
        break label52;
        label196:
        l2 = -1L;
        break label62;
        label204:
        paramSingleSegmentBase = null;
      }
    }
  }
  
  protected SegmentBase.SegmentList parseSegmentList(XmlPullParser paramXmlPullParser, String paramString, SegmentBase.SegmentList paramSegmentList)
    throws XmlPullParserException, IOException
  {
    long l1;
    long l2;
    label31:
    long l3;
    label52:
    int i;
    label73:
    Object localObject6;
    Object localObject5;
    Object localObject4;
    Object localObject2;
    Object localObject3;
    Object localObject1;
    if (paramSegmentList != null)
    {
      l1 = paramSegmentList.timescale;
      l2 = parseLong(paramXmlPullParser, "timescale", l1);
      if (paramSegmentList == null) {
        break label210;
      }
      l1 = paramSegmentList.presentationTimeOffset;
      l3 = parseLong(paramXmlPullParser, "presentationTimeOffset", l1);
      if (paramSegmentList == null) {
        break label216;
      }
      l1 = paramSegmentList.duration;
      l1 = parseLong(paramXmlPullParser, "duration", l1);
      if (paramSegmentList == null) {
        break label224;
      }
      i = paramSegmentList.startNumber;
      i = parseInt(paramXmlPullParser, "startNumber", i);
      localObject6 = null;
      localObject5 = null;
      localObject4 = null;
      label126:
      do
      {
        paramXmlPullParser.next();
        if (!ParserUtil.isStartTag(paramXmlPullParser, "Initialization")) {
          break;
        }
        localObject2 = parseInitialization(paramXmlPullParser, paramString);
        localObject3 = localObject4;
        localObject1 = localObject5;
        localObject6 = localObject2;
        localObject5 = localObject1;
        localObject4 = localObject3;
      } while (!ParserUtil.isEndTag(paramXmlPullParser, "SegmentList"));
      paramXmlPullParser = (XmlPullParser)localObject2;
      paramString = (String)localObject1;
      localObject4 = localObject3;
      if (paramSegmentList != null)
      {
        if (localObject2 == null) {
          break label323;
        }
        label167:
        if (localObject1 == null) {
          break label332;
        }
        label172:
        if (localObject3 == null) {
          break label341;
        }
        localObject4 = localObject3;
        paramString = (String)localObject1;
        paramXmlPullParser = (XmlPullParser)localObject2;
      }
    }
    for (;;)
    {
      return buildSegmentList(paramXmlPullParser, l2, l3, i, l1, paramString, (List)localObject4);
      l1 = 1L;
      break;
      label210:
      l1 = 0L;
      break label31;
      label216:
      l1 = -1L;
      break label52;
      label224:
      i = 1;
      break label73;
      if (ParserUtil.isStartTag(paramXmlPullParser, "SegmentTimeline"))
      {
        localObject1 = parseSegmentTimeline(paramXmlPullParser);
        localObject2 = localObject6;
        localObject3 = localObject4;
        break label126;
      }
      localObject2 = localObject6;
      localObject1 = localObject5;
      localObject3 = localObject4;
      if (!ParserUtil.isStartTag(paramXmlPullParser, "SegmentURL")) {
        break label126;
      }
      localObject3 = localObject4;
      if (localObject4 == null) {
        localObject3 = new ArrayList();
      }
      ((List)localObject3).add(parseSegmentUrl(paramXmlPullParser, paramString));
      localObject2 = localObject6;
      localObject1 = localObject5;
      break label126;
      label323:
      localObject2 = paramSegmentList.initialization;
      break label167;
      label332:
      localObject1 = paramSegmentList.segmentTimeline;
      break label172;
      label341:
      localObject4 = paramSegmentList.mediaSegments;
      paramXmlPullParser = (XmlPullParser)localObject2;
      paramString = (String)localObject1;
    }
  }
  
  protected SegmentBase.SegmentTemplate parseSegmentTemplate(XmlPullParser paramXmlPullParser, String paramString, SegmentBase.SegmentTemplate paramSegmentTemplate)
    throws XmlPullParserException, IOException
  {
    long l1;
    long l2;
    label31:
    long l3;
    label52:
    int i;
    label73:
    Object localObject1;
    label94:
    UrlTemplate localUrlTemplate1;
    label116:
    UrlTemplate localUrlTemplate2;
    Object localObject4;
    Object localObject3;
    Object localObject2;
    if (paramSegmentTemplate != null)
    {
      l1 = paramSegmentTemplate.timescale;
      l2 = parseLong(paramXmlPullParser, "timescale", l1);
      if (paramSegmentTemplate == null) {
        break label236;
      }
      l1 = paramSegmentTemplate.presentationTimeOffset;
      l3 = parseLong(paramXmlPullParser, "presentationTimeOffset", l1);
      if (paramSegmentTemplate == null) {
        break label242;
      }
      l1 = paramSegmentTemplate.duration;
      l1 = parseLong(paramXmlPullParser, "duration", l1);
      if (paramSegmentTemplate == null) {
        break label250;
      }
      i = paramSegmentTemplate.startNumber;
      i = parseInt(paramXmlPullParser, "startNumber", i);
      if (paramSegmentTemplate == null) {
        break label256;
      }
      localObject1 = paramSegmentTemplate.mediaTemplate;
      localUrlTemplate1 = parseUrlTemplate(paramXmlPullParser, "media", (UrlTemplate)localObject1);
      if (paramSegmentTemplate == null) {
        break label262;
      }
      localObject1 = paramSegmentTemplate.initializationTemplate;
      localUrlTemplate2 = parseUrlTemplate(paramXmlPullParser, "initialization", (UrlTemplate)localObject1);
      localObject4 = null;
      localObject3 = null;
      label163:
      do
      {
        paramXmlPullParser.next();
        if (!ParserUtil.isStartTag(paramXmlPullParser, "Initialization")) {
          break;
        }
        localObject1 = parseInitialization(paramXmlPullParser, paramString);
        localObject2 = localObject3;
        localObject4 = localObject1;
        localObject3 = localObject2;
      } while (!ParserUtil.isEndTag(paramXmlPullParser, "SegmentTemplate"));
      paramXmlPullParser = (XmlPullParser)localObject1;
      localObject3 = localObject2;
      if (paramSegmentTemplate != null)
      {
        if (localObject1 == null) {
          break label300;
        }
        label197:
        if (localObject2 == null) {
          break label309;
        }
        localObject3 = localObject2;
      }
    }
    for (paramXmlPullParser = (XmlPullParser)localObject1;; paramXmlPullParser = (XmlPullParser)localObject1)
    {
      return buildSegmentTemplate(paramXmlPullParser, l2, l3, i, l1, (List)localObject3, localUrlTemplate2, localUrlTemplate1, paramString);
      l1 = 1L;
      break;
      label236:
      l1 = 0L;
      break label31;
      label242:
      l1 = -1L;
      break label52;
      label250:
      i = 1;
      break label73;
      label256:
      localObject1 = null;
      break label94;
      label262:
      localObject1 = null;
      break label116;
      localObject1 = localObject4;
      localObject2 = localObject3;
      if (!ParserUtil.isStartTag(paramXmlPullParser, "SegmentTimeline")) {
        break label163;
      }
      localObject2 = parseSegmentTimeline(paramXmlPullParser);
      localObject1 = localObject4;
      break label163;
      label300:
      localObject1 = paramSegmentTemplate.initialization;
      break label197;
      label309:
      localObject3 = paramSegmentTemplate.segmentTimeline;
    }
  }
  
  protected List<SegmentBase.SegmentTimelineElement> parseSegmentTimeline(XmlPullParser paramXmlPullParser)
    throws XmlPullParserException, IOException
  {
    ArrayList localArrayList = new ArrayList();
    long l1 = 0L;
    do
    {
      paramXmlPullParser.next();
      long l2 = l1;
      if (ParserUtil.isStartTag(paramXmlPullParser, "S"))
      {
        l1 = parseLong(paramXmlPullParser, "t", l1);
        long l3 = parseLong(paramXmlPullParser, "d");
        int j = parseInt(paramXmlPullParser, "r", 0);
        int i = 0;
        for (;;)
        {
          l2 = l1;
          if (i >= j + 1) {
            break;
          }
          localArrayList.add(buildSegmentTimelineElement(l1, l3));
          l1 += l3;
          i += 1;
        }
      }
      l1 = l2;
    } while (!ParserUtil.isEndTag(paramXmlPullParser, "SegmentTimeline"));
    return localArrayList;
  }
  
  protected RangedUri parseSegmentUrl(XmlPullParser paramXmlPullParser, String paramString)
  {
    return parseRangedUrl(paramXmlPullParser, paramString, "media", "mediaRange");
  }
  
  protected UrlTemplate parseUrlTemplate(XmlPullParser paramXmlPullParser, String paramString, UrlTemplate paramUrlTemplate)
  {
    paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, paramString);
    if (paramXmlPullParser != null) {
      paramUrlTemplate = UrlTemplate.compile(paramXmlPullParser);
    }
    return paramUrlTemplate;
  }
  
  protected UtcTimingElement parseUtcTiming(XmlPullParser paramXmlPullParser)
  {
    return buildUtcTimingElement(paramXmlPullParser.getAttributeValue(null, "schemeIdUri"), paramXmlPullParser.getAttributeValue(null, "value"));
  }
  
  protected static final class ContentProtectionsBuilder
    implements Comparator<ContentProtection>
  {
    private ArrayList<ContentProtection> adaptationSetProtections;
    private ArrayList<ContentProtection> currentRepresentationProtections;
    private ArrayList<ContentProtection> representationProtections;
    private boolean representationProtectionsSet;
    
    private void maybeAddContentProtection(List<ContentProtection> paramList, ContentProtection paramContentProtection)
    {
      if (!paramList.contains(paramContentProtection))
      {
        int i = 0;
        if (i < paramList.size())
        {
          if (!((ContentProtection)paramList.get(i)).schemeUriId.equals(paramContentProtection.schemeUriId)) {}
          for (boolean bool = true;; bool = false)
          {
            Assertions.checkState(bool);
            i += 1;
            break;
          }
        }
        paramList.add(paramContentProtection);
      }
    }
    
    public void addAdaptationSetProtection(ContentProtection paramContentProtection)
    {
      if (this.adaptationSetProtections == null) {
        this.adaptationSetProtections = new ArrayList();
      }
      maybeAddContentProtection(this.adaptationSetProtections, paramContentProtection);
    }
    
    public void addRepresentationProtection(ContentProtection paramContentProtection)
    {
      if (this.currentRepresentationProtections == null) {
        this.currentRepresentationProtections = new ArrayList();
      }
      maybeAddContentProtection(this.currentRepresentationProtections, paramContentProtection);
    }
    
    public ArrayList<ContentProtection> build()
    {
      if (this.adaptationSetProtections == null) {
        return this.representationProtections;
      }
      if (this.representationProtections == null) {
        return this.adaptationSetProtections;
      }
      int i = 0;
      while (i < this.representationProtections.size())
      {
        maybeAddContentProtection(this.adaptationSetProtections, (ContentProtection)this.representationProtections.get(i));
        i += 1;
      }
      return this.adaptationSetProtections;
    }
    
    public int compare(ContentProtection paramContentProtection1, ContentProtection paramContentProtection2)
    {
      return paramContentProtection1.schemeUriId.compareTo(paramContentProtection2.schemeUriId);
    }
    
    public void endRepresentation()
    {
      boolean bool = true;
      if (!this.representationProtectionsSet)
      {
        if (this.currentRepresentationProtections != null) {
          Collections.sort(this.currentRepresentationProtections, this);
        }
        this.representationProtections = this.currentRepresentationProtections;
        this.representationProtectionsSet = true;
      }
      for (;;)
      {
        this.currentRepresentationProtections = null;
        return;
        if (this.currentRepresentationProtections == null)
        {
          if (this.representationProtections == null) {}
          for (;;)
          {
            Assertions.checkState(bool);
            break;
            bool = false;
          }
        }
        Collections.sort(this.currentRepresentationProtections, this);
        Assertions.checkState(this.currentRepresentationProtections.equals(this.representationProtections));
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/dash/mpd/MediaPresentationDescriptionParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */