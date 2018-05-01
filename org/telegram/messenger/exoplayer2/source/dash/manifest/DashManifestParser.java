package org.telegram.messenger.exoplayer2.source.dash.manifest;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.util.Xml;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.ParserException;
import org.telegram.messenger.exoplayer2.drm.DrmInitData;
import org.telegram.messenger.exoplayer2.drm.DrmInitData.SchemeData;
import org.telegram.messenger.exoplayer2.extractor.mp4.PsshAtomUtil;
import org.telegram.messenger.exoplayer2.metadata.emsg.EventMessage;
import org.telegram.messenger.exoplayer2.upstream.ParsingLoadable.Parser;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.exoplayer2.util.UriUtil;
import org.telegram.messenger.exoplayer2.util.Util;
import org.telegram.messenger.exoplayer2.util.XmlPullParserUtil;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

public class DashManifestParser
  extends DefaultHandler
  implements ParsingLoadable.Parser<DashManifest>
{
  private static final Pattern CEA_608_ACCESSIBILITY_PATTERN = Pattern.compile("CC([1-4])=.*");
  private static final Pattern CEA_708_ACCESSIBILITY_PATTERN = Pattern.compile("([1-9]|[1-5][0-9]|6[0-3])=.*");
  private static final Pattern FRAME_RATE_PATTERN = Pattern.compile("(\\d+)(?:/(\\d+))?");
  private static final String TAG = "MpdParser";
  private final String contentId;
  private final XmlPullParserFactory xmlParserFactory;
  
  public DashManifestParser()
  {
    this(null);
  }
  
  public DashManifestParser(String paramString)
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
      i = paramInt1;
      break;
    }
  }
  
  private static String checkLanguageConsistency(String paramString1, String paramString2)
  {
    String str;
    if (paramString1 == null) {
      str = paramString2;
    }
    for (;;)
    {
      return str;
      str = paramString1;
      if (paramString2 != null)
      {
        Assertions.checkState(paramString1.equals(paramString2));
        str = paramString1;
      }
    }
  }
  
  private static void filterRedundantIncompleteSchemeDatas(ArrayList<DrmInitData.SchemeData> paramArrayList)
  {
    int i = paramArrayList.size() - 1;
    if (i >= 0)
    {
      DrmInitData.SchemeData localSchemeData = (DrmInitData.SchemeData)paramArrayList.get(i);
      if (!localSchemeData.hasData()) {}
      for (int j = 0;; j++) {
        if (j < paramArrayList.size())
        {
          if (((DrmInitData.SchemeData)paramArrayList.get(j)).canReplace(localSchemeData)) {
            paramArrayList.remove(i);
          }
        }
        else
        {
          i--;
          break;
        }
      }
    }
  }
  
  private static String getSampleMimeType(String paramString1, String paramString2)
  {
    String str;
    if (MimeTypes.isAudio(paramString1)) {
      str = MimeTypes.getAudioMediaMimeType(paramString2);
    }
    for (;;)
    {
      return str;
      if (MimeTypes.isVideo(paramString1))
      {
        str = MimeTypes.getVideoMediaMimeType(paramString2);
      }
      else
      {
        str = paramString1;
        if (!mimeTypeIsRawText(paramString1))
        {
          if ("application/mp4".equals(paramString1))
          {
            if ("stpp".equals(paramString2))
            {
              str = "application/ttml+xml";
              continue;
            }
            if ("wvtt".equals(paramString2)) {
              str = "application/x-mp4-vtt";
            }
          }
          else if ("application/x-rawcc".equals(paramString1))
          {
            if (paramString2 != null)
            {
              if (paramString2.contains("cea708"))
              {
                str = "application/cea-708";
                continue;
              }
              if ((paramString2.contains("eia608")) || (paramString2.contains("cea608")))
              {
                str = "application/cea-608";
                continue;
              }
            }
            str = null;
            continue;
          }
          str = null;
        }
      }
    }
  }
  
  private static boolean mimeTypeIsRawText(String paramString)
  {
    if ((MimeTypes.isText(paramString)) || ("application/ttml+xml".equals(paramString)) || ("application/x-mp4-vtt".equals(paramString)) || ("application/cea-708".equals(paramString)) || ("application/cea-608".equals(paramString))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  protected static String parseBaseUrl(XmlPullParser paramXmlPullParser, String paramString)
    throws XmlPullParserException, IOException
  {
    paramXmlPullParser.next();
    return UriUtil.resolve(paramString, paramXmlPullParser.getText());
  }
  
  protected static int parseCea608AccessibilityChannel(List<Descriptor> paramList)
  {
    int i = 0;
    Descriptor localDescriptor;
    Matcher localMatcher;
    if (i < paramList.size())
    {
      localDescriptor = (Descriptor)paramList.get(i);
      if (("urn:scte:dash:cc:cea-608:2015".equals(localDescriptor.schemeIdUri)) && (localDescriptor.value != null))
      {
        localMatcher = CEA_608_ACCESSIBILITY_PATTERN.matcher(localDescriptor.value);
        if (!localMatcher.matches()) {}
      }
    }
    for (i = Integer.parseInt(localMatcher.group(1));; i = -1)
    {
      return i;
      Log.w("MpdParser", "Unable to parse CEA-608 channel number from: " + localDescriptor.value);
      i++;
      break;
    }
  }
  
  protected static int parseCea708AccessibilityChannel(List<Descriptor> paramList)
  {
    int i = 0;
    Descriptor localDescriptor;
    Matcher localMatcher;
    if (i < paramList.size())
    {
      localDescriptor = (Descriptor)paramList.get(i);
      if (("urn:scte:dash:cc:cea-708:2015".equals(localDescriptor.schemeIdUri)) && (localDescriptor.value != null))
      {
        localMatcher = CEA_708_ACCESSIBILITY_PATTERN.matcher(localDescriptor.value);
        if (!localMatcher.matches()) {}
      }
    }
    for (i = Integer.parseInt(localMatcher.group(1));; i = -1)
    {
      return i;
      Log.w("MpdParser", "Unable to parse CEA-708 service block number from: " + localDescriptor.value);
      i++;
      break;
    }
  }
  
  protected static long parseDateTime(XmlPullParser paramXmlPullParser, String paramString, long paramLong)
    throws ParserException
  {
    paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, paramString);
    if (paramXmlPullParser == null) {}
    for (;;)
    {
      return paramLong;
      paramLong = Util.parseXsDateTime(paramXmlPullParser);
    }
  }
  
  protected static Descriptor parseDescriptor(XmlPullParser paramXmlPullParser, String paramString)
    throws XmlPullParserException, IOException
  {
    String str1 = parseString(paramXmlPullParser, "schemeIdUri", "");
    String str2 = parseString(paramXmlPullParser, "value", null);
    String str3 = parseString(paramXmlPullParser, "id", null);
    do
    {
      paramXmlPullParser.next();
    } while (!XmlPullParserUtil.isEndTag(paramXmlPullParser, paramString));
    return new Descriptor(str1, str2, str3);
  }
  
  protected static int parseDolbyChannelConfiguration(XmlPullParser paramXmlPullParser)
  {
    int i = -1;
    paramXmlPullParser = Util.toLowerInvariant(paramXmlPullParser.getAttributeValue(null, "value"));
    int j;
    if (paramXmlPullParser == null) {
      j = i;
    }
    for (;;)
    {
      return j;
      switch (paramXmlPullParser.hashCode())
      {
      default: 
        label72:
        j = -1;
      }
      for (;;)
      {
        switch (j)
        {
        default: 
          j = i;
          break;
        case 0: 
          j = 1;
          break;
          if (!paramXmlPullParser.equals("4000")) {
            break label72;
          }
          j = 0;
          continue;
          if (!paramXmlPullParser.equals("a000")) {
            break label72;
          }
          j = 1;
          continue;
          if (!paramXmlPullParser.equals("f801")) {
            break label72;
          }
          j = 2;
          continue;
          if (!paramXmlPullParser.equals("fa01")) {
            break label72;
          }
          j = 3;
        }
      }
      j = 2;
      continue;
      j = 6;
      continue;
      j = 8;
    }
  }
  
  protected static long parseDuration(XmlPullParser paramXmlPullParser, String paramString, long paramLong)
  {
    paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, paramString);
    if (paramXmlPullParser == null) {}
    for (;;)
    {
      return paramLong;
      paramLong = Util.parseXsDuration(paramXmlPullParser);
    }
  }
  
  protected static String parseEac3SupplementalProperties(List<Descriptor> paramList)
  {
    int i = 0;
    if (i < paramList.size())
    {
      Descriptor localDescriptor = (Descriptor)paramList.get(i);
      if ((!"tag:dolby.com,2014:dash:DolbyDigitalPlusExtensionType:2014".equals(localDescriptor.schemeIdUri)) || (!"ec+3".equals(localDescriptor.value))) {}
    }
    for (paramList = "audio/eac3-joc";; paramList = "audio/eac3")
    {
      return paramList;
      i++;
      break;
    }
  }
  
  protected static float parseFrameRate(XmlPullParser paramXmlPullParser, float paramFloat)
  {
    float f = paramFloat;
    paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, "frameRate");
    paramFloat = f;
    int i;
    if (paramXmlPullParser != null)
    {
      paramXmlPullParser = FRAME_RATE_PATTERN.matcher(paramXmlPullParser);
      paramFloat = f;
      if (paramXmlPullParser.matches())
      {
        i = Integer.parseInt(paramXmlPullParser.group(1));
        paramXmlPullParser = paramXmlPullParser.group(2);
        if (TextUtils.isEmpty(paramXmlPullParser)) {
          break label69;
        }
      }
    }
    label69:
    for (paramFloat = i / Integer.parseInt(paramXmlPullParser);; paramFloat = i) {
      return paramFloat;
    }
  }
  
  protected static int parseInt(XmlPullParser paramXmlPullParser, String paramString, int paramInt)
  {
    paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, paramString);
    if (paramXmlPullParser == null) {}
    for (;;)
    {
      return paramInt;
      paramInt = Integer.parseInt(paramXmlPullParser);
    }
  }
  
  protected static long parseLong(XmlPullParser paramXmlPullParser, String paramString, long paramLong)
  {
    paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, paramString);
    if (paramXmlPullParser == null) {}
    for (;;)
    {
      return paramLong;
      paramLong = Long.parseLong(paramXmlPullParser);
    }
  }
  
  protected static String parseString(XmlPullParser paramXmlPullParser, String paramString1, String paramString2)
  {
    paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, paramString1);
    if (paramXmlPullParser == null) {}
    for (;;)
    {
      return paramString2;
      paramString2 = paramXmlPullParser;
    }
  }
  
  protected AdaptationSet buildAdaptationSet(int paramInt1, int paramInt2, List<Representation> paramList, List<Descriptor> paramList1, List<Descriptor> paramList2)
  {
    return new AdaptationSet(paramInt1, paramInt2, paramList, paramList1, paramList2);
  }
  
  protected EventMessage buildEvent(String paramString1, String paramString2, long paramLong1, long paramLong2, byte[] paramArrayOfByte, long paramLong3)
  {
    return new EventMessage(paramString1, paramString2, paramLong2, paramLong1, paramArrayOfByte, paramLong3);
  }
  
  protected EventStream buildEventStream(String paramString1, String paramString2, long paramLong, long[] paramArrayOfLong, EventMessage[] paramArrayOfEventMessage)
  {
    return new EventStream(paramString1, paramString2, paramLong, paramArrayOfLong, paramArrayOfEventMessage);
  }
  
  protected Format buildFormat(String paramString1, String paramString2, int paramInt1, int paramInt2, float paramFloat, int paramInt3, int paramInt4, int paramInt5, String paramString3, int paramInt6, List<Descriptor> paramList1, String paramString4, List<Descriptor> paramList2)
  {
    String str1 = getSampleMimeType(paramString2, paramString4);
    Object localObject = str1;
    String str2;
    if (str1 != null)
    {
      str2 = str1;
      if ("audio/eac3".equals(str1)) {
        str2 = parseEac3SupplementalProperties(paramList2);
      }
      if (MimeTypes.isVideo(str2)) {
        paramString1 = Format.createVideoContainerFormat(paramString1, paramString2, str2, paramString4, paramInt5, paramInt1, paramInt2, paramFloat, null, paramInt6);
      }
    }
    for (;;)
    {
      return paramString1;
      if (MimeTypes.isAudio(str2))
      {
        paramString1 = Format.createAudioContainerFormat(paramString1, paramString2, str2, paramString4, paramInt5, paramInt3, paramInt4, null, paramInt6, paramString3);
      }
      else
      {
        localObject = str2;
        if (mimeTypeIsRawText(str2))
        {
          if ("application/cea-608".equals(str2)) {
            paramInt1 = parseCea608AccessibilityChannel(paramList1);
          }
          for (;;)
          {
            paramString1 = Format.createTextContainerFormat(paramString1, paramString2, str2, paramString4, paramInt5, paramInt6, paramString3, paramInt1);
            break;
            if ("application/cea-708".equals(str2)) {
              paramInt1 = parseCea708AccessibilityChannel(paramList1);
            } else {
              paramInt1 = -1;
            }
          }
        }
        paramString1 = Format.createContainerFormat(paramString1, paramString2, (String)localObject, paramString4, paramInt5, paramInt6, paramString3);
      }
    }
  }
  
  protected DashManifest buildMediaPresentationDescription(long paramLong1, long paramLong2, long paramLong3, boolean paramBoolean, long paramLong4, long paramLong5, long paramLong6, long paramLong7, UtcTimingElement paramUtcTimingElement, Uri paramUri, List<Period> paramList)
  {
    return new DashManifest(paramLong1, paramLong2, paramLong3, paramBoolean, paramLong4, paramLong5, paramLong6, paramLong7, paramUtcTimingElement, paramUri, paramList);
  }
  
  protected Period buildPeriod(String paramString, long paramLong, List<AdaptationSet> paramList, List<EventStream> paramList1)
  {
    return new Period(paramString, paramLong, paramList, paramList1);
  }
  
  protected RangedUri buildRangedUri(String paramString, long paramLong1, long paramLong2)
  {
    return new RangedUri(paramString, paramLong1, paramLong2);
  }
  
  protected Representation buildRepresentation(RepresentationInfo paramRepresentationInfo, String paramString1, String paramString2, ArrayList<DrmInitData.SchemeData> paramArrayList, ArrayList<Descriptor> paramArrayList1)
  {
    Format localFormat = paramRepresentationInfo.format;
    if (paramRepresentationInfo.drmSchemeType != null) {
      paramString2 = paramRepresentationInfo.drmSchemeType;
    }
    for (;;)
    {
      ArrayList localArrayList = paramRepresentationInfo.drmSchemeDatas;
      localArrayList.addAll(paramArrayList);
      paramArrayList = localFormat;
      if (!localArrayList.isEmpty())
      {
        filterRedundantIncompleteSchemeDatas(localArrayList);
        paramArrayList = localFormat.copyWithDrmInitData(new DrmInitData(paramString2, localArrayList));
      }
      paramString2 = paramRepresentationInfo.inbandEventStreams;
      paramString2.addAll(paramArrayList1);
      return Representation.newInstance(paramString1, paramRepresentationInfo.revisionId, paramArrayList, paramRepresentationInfo.baseUrl, paramRepresentationInfo.segmentBase, paramString2);
    }
  }
  
  protected SegmentBase.SegmentList buildSegmentList(RangedUri paramRangedUri, long paramLong1, long paramLong2, int paramInt, long paramLong3, List<SegmentBase.SegmentTimelineElement> paramList, List<RangedUri> paramList1)
  {
    return new SegmentBase.SegmentList(paramRangedUri, paramLong1, paramLong2, paramInt, paramLong3, paramList, paramList1);
  }
  
  protected SegmentBase.SegmentTemplate buildSegmentTemplate(RangedUri paramRangedUri, long paramLong1, long paramLong2, int paramInt, long paramLong3, List<SegmentBase.SegmentTimelineElement> paramList, UrlTemplate paramUrlTemplate1, UrlTemplate paramUrlTemplate2)
  {
    return new SegmentBase.SegmentTemplate(paramRangedUri, paramLong1, paramLong2, paramInt, paramLong3, paramList, paramUrlTemplate1, paramUrlTemplate2);
  }
  
  protected SegmentBase.SegmentTimelineElement buildSegmentTimelineElement(long paramLong1, long paramLong2)
  {
    return new SegmentBase.SegmentTimelineElement(paramLong1, paramLong2);
  }
  
  protected SegmentBase.SingleSegmentBase buildSingleSegmentBase(RangedUri paramRangedUri, long paramLong1, long paramLong2, long paramLong3, long paramLong4)
  {
    return new SegmentBase.SingleSegmentBase(paramRangedUri, paramLong1, paramLong2, paramLong3, paramLong4);
  }
  
  protected UtcTimingElement buildUtcTimingElement(String paramString1, String paramString2)
  {
    return new UtcTimingElement(paramString1, paramString2);
  }
  
  protected int getContentType(Format paramFormat)
  {
    int i = -1;
    paramFormat = paramFormat.sampleMimeType;
    if (TextUtils.isEmpty(paramFormat)) {}
    for (;;)
    {
      return i;
      if (MimeTypes.isVideo(paramFormat)) {
        i = 2;
      } else if (MimeTypes.isAudio(paramFormat)) {
        i = 1;
      } else if (mimeTypeIsRawText(paramFormat)) {
        i = 3;
      }
    }
  }
  
  public DashManifest parse(Uri paramUri, InputStream paramInputStream)
    throws IOException
  {
    XmlPullParser localXmlPullParser;
    try
    {
      localXmlPullParser = this.xmlParserFactory.newPullParser();
      localXmlPullParser.setInput(paramInputStream, null);
      if ((localXmlPullParser.next() != 2) || (!"MPD".equals(localXmlPullParser.getName())))
      {
        paramUri = new org/telegram/messenger/exoplayer2/ParserException;
        paramUri.<init>("inputStream does not contain a valid media presentation description");
        throw paramUri;
      }
    }
    catch (XmlPullParserException paramUri)
    {
      throw new ParserException(paramUri);
    }
    paramUri = parseMediaPresentationDescription(localXmlPullParser, paramUri.toString());
    return paramUri;
  }
  
  protected AdaptationSet parseAdaptationSet(XmlPullParser paramXmlPullParser, String paramString, SegmentBase paramSegmentBase)
    throws XmlPullParserException, IOException
  {
    int i = parseInt(paramXmlPullParser, "id", -1);
    int j = parseContentType(paramXmlPullParser);
    String str1 = paramXmlPullParser.getAttributeValue(null, "mimeType");
    String str2 = paramXmlPullParser.getAttributeValue(null, "codecs");
    int k = parseInt(paramXmlPullParser, "width", -1);
    int m = parseInt(paramXmlPullParser, "height", -1);
    float f = parseFrameRate(paramXmlPullParser, -1.0F);
    int n = -1;
    int i1 = parseInt(paramXmlPullParser, "audioSamplingRate", -1);
    Object localObject1 = paramXmlPullParser.getAttributeValue(null, "lang");
    SegmentBase localSegmentBase = null;
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    ArrayList localArrayList3 = new ArrayList();
    ArrayList localArrayList4 = new ArrayList();
    ArrayList localArrayList5 = new ArrayList();
    int i2 = 0;
    int i3 = 0;
    Object localObject2 = paramSegmentBase;
    Object localObject3 = paramString;
    paramSegmentBase = localSegmentBase;
    paramXmlPullParser.next();
    int i4;
    Object localObject4;
    int i5;
    int i6;
    int i7;
    Object localObject5;
    if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "BaseURL"))
    {
      i4 = n;
      localObject4 = localObject1;
      i5 = i2;
      localSegmentBase = paramSegmentBase;
      i6 = j;
      i7 = i3;
      localObject5 = localObject3;
      paramString = (String)localObject2;
      if (i3 == 0)
      {
        localObject5 = parseBaseUrl(paramXmlPullParser, (String)localObject3);
        i7 = 1;
        paramString = (String)localObject2;
        i6 = j;
        localSegmentBase = paramSegmentBase;
        i5 = i2;
        localObject4 = localObject1;
        i4 = n;
      }
    }
    for (;;)
    {
      n = i4;
      localObject1 = localObject4;
      i2 = i5;
      paramSegmentBase = localSegmentBase;
      j = i6;
      i3 = i7;
      localObject3 = localObject5;
      localObject2 = paramString;
      if (!XmlPullParserUtil.isEndTag(paramXmlPullParser, "AdaptationSet")) {
        break;
      }
      paramXmlPullParser = new ArrayList(localArrayList5.size());
      for (i3 = 0; i3 < localArrayList5.size(); i3++) {
        paramXmlPullParser.add(buildRepresentation((RepresentationInfo)localArrayList5.get(i3), this.contentId, localSegmentBase, localArrayList1, localArrayList2));
      }
      if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "ContentProtection"))
      {
        Pair localPair = parseContentProtection(paramXmlPullParser);
        if (localPair.first != null) {
          paramSegmentBase = (String)localPair.first;
        }
        i4 = n;
        localObject4 = localObject1;
        i5 = i2;
        localSegmentBase = paramSegmentBase;
        i6 = j;
        i7 = i3;
        localObject5 = localObject3;
        paramString = (String)localObject2;
        if (localPair.second != null)
        {
          localArrayList1.add(localPair.second);
          i4 = n;
          localObject4 = localObject1;
          i5 = i2;
          localSegmentBase = paramSegmentBase;
          i6 = j;
          i7 = i3;
          localObject5 = localObject3;
          paramString = (String)localObject2;
        }
      }
      else if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "ContentComponent"))
      {
        localObject4 = checkLanguageConsistency((String)localObject1, paramXmlPullParser.getAttributeValue(null, "lang"));
        i6 = checkContentTypeConsistency(j, parseContentType(paramXmlPullParser));
        i4 = n;
        i5 = i2;
        localSegmentBase = paramSegmentBase;
        i7 = i3;
        localObject5 = localObject3;
        paramString = (String)localObject2;
      }
      else if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "Role"))
      {
        i5 = i2 | parseRole(paramXmlPullParser);
        i4 = n;
        localObject4 = localObject1;
        localSegmentBase = paramSegmentBase;
        i6 = j;
        i7 = i3;
        localObject5 = localObject3;
        paramString = (String)localObject2;
      }
      else if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "AudioChannelConfiguration"))
      {
        i4 = parseAudioChannelConfiguration(paramXmlPullParser);
        localObject4 = localObject1;
        i5 = i2;
        localSegmentBase = paramSegmentBase;
        i6 = j;
        i7 = i3;
        localObject5 = localObject3;
        paramString = (String)localObject2;
      }
      else if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "Accessibility"))
      {
        localArrayList3.add(parseDescriptor(paramXmlPullParser, "Accessibility"));
        i4 = n;
        localObject4 = localObject1;
        i5 = i2;
        localSegmentBase = paramSegmentBase;
        i6 = j;
        i7 = i3;
        localObject5 = localObject3;
        paramString = (String)localObject2;
      }
      else if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "SupplementalProperty"))
      {
        localArrayList4.add(parseDescriptor(paramXmlPullParser, "SupplementalProperty"));
        i4 = n;
        localObject4 = localObject1;
        i5 = i2;
        localSegmentBase = paramSegmentBase;
        i6 = j;
        i7 = i3;
        localObject5 = localObject3;
        paramString = (String)localObject2;
      }
      else if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "Representation"))
      {
        paramString = parseRepresentation(paramXmlPullParser, (String)localObject3, str1, str2, k, m, f, n, i1, (String)localObject1, i2, localArrayList3, (SegmentBase)localObject2);
        i6 = checkContentTypeConsistency(j, getContentType(paramString.format));
        localArrayList5.add(paramString);
        i4 = n;
        localObject4 = localObject1;
        i5 = i2;
        localSegmentBase = paramSegmentBase;
        i7 = i3;
        localObject5 = localObject3;
        paramString = (String)localObject2;
      }
      else if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "SegmentBase"))
      {
        paramString = parseSegmentBase(paramXmlPullParser, (SegmentBase.SingleSegmentBase)localObject2);
        i4 = n;
        localObject4 = localObject1;
        i5 = i2;
        localSegmentBase = paramSegmentBase;
        i6 = j;
        i7 = i3;
        localObject5 = localObject3;
      }
      else if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "SegmentList"))
      {
        paramString = parseSegmentList(paramXmlPullParser, (SegmentBase.SegmentList)localObject2);
        i4 = n;
        localObject4 = localObject1;
        i5 = i2;
        localSegmentBase = paramSegmentBase;
        i6 = j;
        i7 = i3;
        localObject5 = localObject3;
      }
      else if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "SegmentTemplate"))
      {
        paramString = parseSegmentTemplate(paramXmlPullParser, (SegmentBase.SegmentTemplate)localObject2);
        i4 = n;
        localObject4 = localObject1;
        i5 = i2;
        localSegmentBase = paramSegmentBase;
        i6 = j;
        i7 = i3;
        localObject5 = localObject3;
      }
      else if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "InbandEventStream"))
      {
        localArrayList2.add(parseDescriptor(paramXmlPullParser, "InbandEventStream"));
        i4 = n;
        localObject4 = localObject1;
        i5 = i2;
        localSegmentBase = paramSegmentBase;
        i6 = j;
        i7 = i3;
        localObject5 = localObject3;
        paramString = (String)localObject2;
      }
      else
      {
        i4 = n;
        localObject4 = localObject1;
        i5 = i2;
        localSegmentBase = paramSegmentBase;
        i6 = j;
        i7 = i3;
        localObject5 = localObject3;
        paramString = (String)localObject2;
        if (XmlPullParserUtil.isStartTag(paramXmlPullParser))
        {
          parseAdaptationSetChild(paramXmlPullParser);
          i4 = n;
          localObject4 = localObject1;
          i5 = i2;
          localSegmentBase = paramSegmentBase;
          i6 = j;
          i7 = i3;
          localObject5 = localObject3;
          paramString = (String)localObject2;
        }
      }
    }
    return buildAdaptationSet(i, i6, paramXmlPullParser, localArrayList3, localArrayList4);
  }
  
  protected void parseAdaptationSetChild(XmlPullParser paramXmlPullParser)
    throws XmlPullParserException, IOException
  {}
  
  protected int parseAudioChannelConfiguration(XmlPullParser paramXmlPullParser)
    throws XmlPullParserException, IOException
  {
    int i = -1;
    String str = parseString(paramXmlPullParser, "schemeIdUri", null);
    if ("urn:mpeg:dash:23003:3:audio_channel_configuration:2011".equals(str)) {
      i = parseInt(paramXmlPullParser, "value", -1);
    }
    for (;;)
    {
      paramXmlPullParser.next();
      if (XmlPullParserUtil.isEndTag(paramXmlPullParser, "AudioChannelConfiguration"))
      {
        return i;
        if ("tag:dolby.com,2014:dash:audio_channel_configuration:2011".equals(str)) {
          i = parseDolbyChannelConfiguration(paramXmlPullParser);
        }
      }
    }
  }
  
  protected Pair<String, DrmInitData.SchemeData> parseContentProtection(XmlPullParser paramXmlPullParser)
    throws XmlPullParserException, IOException
  {
    Object localObject1 = null;
    Object localObject2 = null;
    Object localObject3 = null;
    boolean bool1 = false;
    String str = paramXmlPullParser.getAttributeValue(null, "schemeIdUri");
    Object localObject4 = localObject2;
    boolean bool2 = bool1;
    Object localObject5 = localObject1;
    Object localObject6 = localObject3;
    int i;
    if (str != null)
    {
      localObject6 = Util.toLowerInvariant(str);
      i = -1;
    }
    switch (((String)localObject6).hashCode())
    {
    default: 
      switch (i)
      {
      default: 
        localObject6 = localObject3;
        localObject5 = localObject1;
        bool2 = bool1;
        localObject4 = localObject2;
        label130:
        label181:
        do
        {
          paramXmlPullParser.next();
          if (!XmlPullParserUtil.isStartTag(paramXmlPullParser, "widevine:license")) {
            break label467;
          }
          localObject2 = paramXmlPullParser.getAttributeValue(null, "robustness_level");
          if ((localObject2 == null) || (!((String)localObject2).startsWith("HW"))) {
            break;
          }
          bool1 = true;
          localObject1 = localObject6;
          localObject2 = localObject4;
          localObject4 = localObject2;
          bool2 = bool1;
          localObject6 = localObject1;
        } while (!XmlPullParserUtil.isEndTag(paramXmlPullParser, "ContentProtection"));
        if (localObject1 == null) {}
        break;
      }
      break;
    }
    for (paramXmlPullParser = new DrmInitData.SchemeData((UUID)localObject1, "video/mp4", (byte[])localObject2, bool1);; paramXmlPullParser = null)
    {
      return Pair.create(localObject5, paramXmlPullParser);
      if (!((String)localObject6).equals("urn:mpeg:dash:mp4protection:2011")) {
        break;
      }
      i = 0;
      break;
      if (!((String)localObject6).equals("urn:uuid:9a04f079-9840-4286-ab92-e65be0885f95")) {
        break;
      }
      i = 1;
      break;
      if (!((String)localObject6).equals("urn:uuid:edef8ba9-79d6-4ace-a3c8-27dcd51d21ed")) {
        break;
      }
      i = 2;
      break;
      localObject1 = paramXmlPullParser.getAttributeValue(null, "value");
      str = paramXmlPullParser.getAttributeValue(null, "cenc:default_KID");
      localObject4 = localObject2;
      bool2 = bool1;
      localObject5 = localObject1;
      localObject6 = localObject3;
      if (TextUtils.isEmpty(str)) {
        break label130;
      }
      localObject4 = localObject2;
      bool2 = bool1;
      localObject5 = localObject1;
      localObject6 = localObject3;
      if ("00000000-0000-0000-0000-000000000000".equals(str)) {
        break label130;
      }
      localObject6 = str.split("\\s+");
      localObject2 = new UUID[localObject6.length];
      for (i = 0; i < localObject6.length; i++) {
        localObject2[i] = UUID.fromString(localObject6[i]);
      }
      localObject4 = PsshAtomUtil.buildPsshAtom(C.COMMON_PSSH_UUID, (UUID[])localObject2, null);
      localObject6 = C.COMMON_PSSH_UUID;
      bool2 = bool1;
      localObject5 = localObject1;
      break label130;
      localObject6 = C.PLAYREADY_UUID;
      localObject4 = localObject2;
      bool2 = bool1;
      localObject5 = localObject1;
      break label130;
      localObject6 = C.WIDEVINE_UUID;
      localObject4 = localObject2;
      bool2 = bool1;
      localObject5 = localObject1;
      break label130;
      bool1 = false;
      localObject2 = localObject4;
      localObject1 = localObject6;
      break label181;
      label467:
      localObject2 = localObject4;
      bool1 = bool2;
      localObject1 = localObject6;
      if (localObject4 != null) {
        break label181;
      }
      if ((XmlPullParserUtil.isStartTag(paramXmlPullParser, "cenc:pssh")) && (paramXmlPullParser.next() == 4))
      {
        localObject2 = Base64.decode(paramXmlPullParser.getText(), 0);
        localObject6 = PsshAtomUtil.parseUuid((byte[])localObject2);
        bool1 = bool2;
        localObject1 = localObject6;
        if (localObject6 != null) {
          break label181;
        }
        Log.w("MpdParser", "Skipping malformed cenc:pssh data");
        localObject2 = null;
        bool1 = bool2;
        localObject1 = localObject6;
        break label181;
      }
      localObject2 = localObject4;
      bool1 = bool2;
      localObject1 = localObject6;
      if (!C.PLAYREADY_UUID.equals(localObject6)) {
        break label181;
      }
      localObject2 = localObject4;
      bool1 = bool2;
      localObject1 = localObject6;
      if (!XmlPullParserUtil.isStartTag(paramXmlPullParser, "mspr:pro")) {
        break label181;
      }
      localObject2 = localObject4;
      bool1 = bool2;
      localObject1 = localObject6;
      if (paramXmlPullParser.next() != 4) {
        break label181;
      }
      localObject2 = PsshAtomUtil.buildPsshAtom(C.PLAYREADY_UUID, Base64.decode(paramXmlPullParser.getText(), 0));
      bool1 = bool2;
      localObject1 = localObject6;
      break label181;
    }
  }
  
  protected int parseContentType(XmlPullParser paramXmlPullParser)
  {
    int i = -1;
    paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, "contentType");
    if (TextUtils.isEmpty(paramXmlPullParser)) {}
    for (;;)
    {
      return i;
      if ("audio".equals(paramXmlPullParser)) {
        i = 1;
      } else if ("video".equals(paramXmlPullParser)) {
        i = 2;
      } else if ("text".equals(paramXmlPullParser)) {
        i = 3;
      }
    }
  }
  
  protected EventMessage parseEvent(XmlPullParser paramXmlPullParser, String paramString1, String paramString2, long paramLong, ByteArrayOutputStream paramByteArrayOutputStream)
    throws IOException, XmlPullParserException
  {
    long l1 = parseLong(paramXmlPullParser, "id", 0L);
    long l2 = parseLong(paramXmlPullParser, "duration", -9223372036854775807L);
    long l3 = parseLong(paramXmlPullParser, "presentationTime", 0L);
    l2 = Util.scaleLargeTimestamp(l2, 1000L, paramLong);
    paramLong = Util.scaleLargeTimestamp(l3, 1000000L, paramLong);
    return buildEvent(paramString1, paramString2, l1, l2, parseEventObject(paramXmlPullParser, paramByteArrayOutputStream), paramLong);
  }
  
  protected byte[] parseEventObject(XmlPullParser paramXmlPullParser, ByteArrayOutputStream paramByteArrayOutputStream)
    throws XmlPullParserException, IOException
  {
    paramByteArrayOutputStream.reset();
    XmlSerializer localXmlSerializer = Xml.newSerializer();
    localXmlSerializer.setOutput(paramByteArrayOutputStream, null);
    paramXmlPullParser.nextToken();
    if (!XmlPullParserUtil.isEndTag(paramXmlPullParser, "Event"))
    {
      switch (paramXmlPullParser.getEventType())
      {
      }
      for (;;)
      {
        paramXmlPullParser.nextToken();
        break;
        localXmlSerializer.startDocument(null, Boolean.valueOf(false));
        continue;
        localXmlSerializer.endDocument();
        continue;
        localXmlSerializer.startTag(paramXmlPullParser.getNamespace(), paramXmlPullParser.getName());
        for (int i = 0; i < paramXmlPullParser.getAttributeCount(); i++) {
          localXmlSerializer.attribute(paramXmlPullParser.getAttributeNamespace(i), paramXmlPullParser.getAttributeName(i), paramXmlPullParser.getAttributeValue(i));
        }
        localXmlSerializer.endTag(paramXmlPullParser.getNamespace(), paramXmlPullParser.getName());
        continue;
        localXmlSerializer.text(paramXmlPullParser.getText());
        continue;
        localXmlSerializer.cdsect(paramXmlPullParser.getText());
        continue;
        localXmlSerializer.entityRef(paramXmlPullParser.getText());
        continue;
        localXmlSerializer.ignorableWhitespace(paramXmlPullParser.getText());
        continue;
        localXmlSerializer.processingInstruction(paramXmlPullParser.getText());
        continue;
        localXmlSerializer.comment(paramXmlPullParser.getText());
        continue;
        localXmlSerializer.docdecl(paramXmlPullParser.getText());
      }
    }
    localXmlSerializer.flush();
    return paramByteArrayOutputStream.toByteArray();
  }
  
  protected EventStream parseEventStream(XmlPullParser paramXmlPullParser)
    throws XmlPullParserException, IOException
  {
    String str1 = parseString(paramXmlPullParser, "schemeIdUri", "");
    String str2 = parseString(paramXmlPullParser, "value", "");
    long l = parseLong(paramXmlPullParser, "timescale", 1L);
    ArrayList localArrayList = new ArrayList();
    Object localObject = new ByteArrayOutputStream(512);
    do
    {
      paramXmlPullParser.next();
      if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "Event")) {
        localArrayList.add(parseEvent(paramXmlPullParser, str1, str2, l, (ByteArrayOutputStream)localObject));
      }
    } while (!XmlPullParserUtil.isEndTag(paramXmlPullParser, "EventStream"));
    paramXmlPullParser = new long[localArrayList.size()];
    EventMessage[] arrayOfEventMessage = new EventMessage[localArrayList.size()];
    for (int i = 0; i < localArrayList.size(); i++)
    {
      localObject = (EventMessage)localArrayList.get(i);
      paramXmlPullParser[i] = ((EventMessage)localObject).presentationTimeUs;
      arrayOfEventMessage[i] = localObject;
    }
    return buildEventStream(str1, str2, l, paramXmlPullParser, arrayOfEventMessage);
  }
  
  protected RangedUri parseInitialization(XmlPullParser paramXmlPullParser)
  {
    return parseRangedUrl(paramXmlPullParser, "sourceURL", "range");
  }
  
  protected DashManifest parseMediaPresentationDescription(XmlPullParser paramXmlPullParser, String paramString)
    throws XmlPullParserException, IOException
  {
    long l1 = parseDateTime(paramXmlPullParser, "availabilityStartTime", -9223372036854775807L);
    long l2 = parseDuration(paramXmlPullParser, "mediaPresentationDuration", -9223372036854775807L);
    long l3 = parseDuration(paramXmlPullParser, "minBufferTime", -9223372036854775807L);
    Object localObject1 = paramXmlPullParser.getAttributeValue(null, "type");
    boolean bool;
    long l4;
    label83:
    long l5;
    label100:
    long l6;
    label117:
    long l7;
    Object localObject2;
    ArrayList localArrayList;
    long l8;
    label154:
    int i;
    int j;
    long l9;
    Object localObject3;
    Object localObject4;
    int k;
    int m;
    String str;
    if ((localObject1 != null) && (((String)localObject1).equals("dynamic")))
    {
      bool = true;
      if (!bool) {
        break label321;
      }
      l4 = parseDuration(paramXmlPullParser, "minimumUpdatePeriod", -9223372036854775807L);
      if (!bool) {
        break label329;
      }
      l5 = parseDuration(paramXmlPullParser, "timeShiftBufferDepth", -9223372036854775807L);
      if (!bool) {
        break label337;
      }
      l6 = parseDuration(paramXmlPullParser, "suggestedPresentationDelay", -9223372036854775807L);
      l7 = parseDateTime(paramXmlPullParser, "publishTime", -9223372036854775807L);
      localObject2 = null;
      localObject1 = null;
      localArrayList = new ArrayList();
      if (!bool) {
        break label345;
      }
      l8 = -9223372036854775807L;
      i = 0;
      j = 0;
      l9 = l8;
      label235:
      do
      {
        paramXmlPullParser.next();
        if (!XmlPullParserUtil.isStartTag(paramXmlPullParser, "BaseURL")) {
          break;
        }
        localObject3 = localObject2;
        localObject4 = localObject1;
        l8 = l9;
        k = i;
        m = j;
        str = paramString;
        if (j == 0)
        {
          str = parseBaseUrl(paramXmlPullParser, paramString);
          m = 1;
          k = i;
          l8 = l9;
          localObject4 = localObject1;
          localObject3 = localObject2;
        }
        localObject2 = localObject3;
        localObject1 = localObject4;
        l9 = l8;
        i = k;
        j = m;
        paramString = str;
      } while (!XmlPullParserUtil.isEndTag(paramXmlPullParser, "MPD"));
      l9 = l2;
      if (l2 == -9223372036854775807L)
      {
        if (l8 == -9223372036854775807L) {
          break label662;
        }
        l9 = l8;
      }
    }
    for (;;)
    {
      if (localArrayList.isEmpty())
      {
        throw new ParserException("No periods found.");
        bool = false;
        break;
        label321:
        l4 = -9223372036854775807L;
        break label83;
        label329:
        l5 = -9223372036854775807L;
        break label100;
        label337:
        l6 = -9223372036854775807L;
        break label117;
        label345:
        l8 = 0L;
        break label154;
        if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "UTCTiming"))
        {
          localObject3 = parseUtcTiming(paramXmlPullParser);
          localObject4 = localObject1;
          l8 = l9;
          k = i;
          m = j;
          str = paramString;
          break label235;
        }
        if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "Location"))
        {
          localObject4 = Uri.parse(paramXmlPullParser.nextText());
          localObject3 = localObject2;
          l8 = l9;
          k = i;
          m = j;
          str = paramString;
          break label235;
        }
        localObject3 = localObject2;
        localObject4 = localObject1;
        l8 = l9;
        k = i;
        m = j;
        str = paramString;
        if (!XmlPullParserUtil.isStartTag(paramXmlPullParser, "Period")) {
          break label235;
        }
        localObject3 = localObject2;
        localObject4 = localObject1;
        l8 = l9;
        k = i;
        m = j;
        str = paramString;
        if (i != 0) {
          break label235;
        }
        localObject3 = parsePeriod(paramXmlPullParser, paramString, l9);
        localObject4 = (Period)((Pair)localObject3).first;
        if (((Period)localObject4).startMs == -9223372036854775807L)
        {
          if (bool)
          {
            k = 1;
            localObject3 = localObject2;
            localObject4 = localObject1;
            l8 = l9;
            m = j;
            str = paramString;
            break label235;
          }
          throw new ParserException("Unable to determine start of period " + localArrayList.size());
        }
        l8 = ((Long)((Pair)localObject3).second).longValue();
        if (l8 == -9223372036854775807L) {}
        for (l8 = -9223372036854775807L;; l8 = ((Period)localObject4).startMs + l8)
        {
          localArrayList.add(localObject4);
          localObject3 = localObject2;
          localObject4 = localObject1;
          k = i;
          m = j;
          str = paramString;
          break;
        }
        label662:
        l9 = l2;
        if (!bool) {
          throw new ParserException("Unable to determine duration of static manifest.");
        }
      }
    }
    return buildMediaPresentationDescription(l1, l9, l3, bool, l4, l5, l6, l7, (UtcTimingElement)localObject3, (Uri)localObject4, localArrayList);
  }
  
  protected Pair<Period, Long> parsePeriod(XmlPullParser paramXmlPullParser, String paramString, long paramLong)
    throws XmlPullParserException, IOException
  {
    String str1 = paramXmlPullParser.getAttributeValue(null, "id");
    long l = parseDuration(paramXmlPullParser, "start", paramLong);
    paramLong = parseDuration(paramXmlPullParser, "duration", -9223372036854775807L);
    String str2 = null;
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    int i = 0;
    Object localObject1 = paramString;
    paramXmlPullParser.next();
    int j;
    Object localObject2;
    if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "BaseURL"))
    {
      j = i;
      paramString = str2;
      localObject2 = localObject1;
      if (i == 0)
      {
        localObject2 = parseBaseUrl(paramXmlPullParser, (String)localObject1);
        j = 1;
        paramString = str2;
      }
    }
    for (;;)
    {
      i = j;
      str2 = paramString;
      localObject1 = localObject2;
      if (!XmlPullParserUtil.isEndTag(paramXmlPullParser, "Period")) {
        break;
      }
      return Pair.create(buildPeriod(str1, l, localArrayList1, localArrayList2), Long.valueOf(paramLong));
      if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "AdaptationSet"))
      {
        localArrayList1.add(parseAdaptationSet(paramXmlPullParser, (String)localObject1, str2));
        j = i;
        paramString = str2;
        localObject2 = localObject1;
      }
      else if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "EventStream"))
      {
        localArrayList2.add(parseEventStream(paramXmlPullParser));
        j = i;
        paramString = str2;
        localObject2 = localObject1;
      }
      else if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "SegmentBase"))
      {
        paramString = parseSegmentBase(paramXmlPullParser, null);
        j = i;
        localObject2 = localObject1;
      }
      else if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "SegmentList"))
      {
        paramString = parseSegmentList(paramXmlPullParser, null);
        j = i;
        localObject2 = localObject1;
      }
      else
      {
        j = i;
        paramString = str2;
        localObject2 = localObject1;
        if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "SegmentTemplate"))
        {
          paramString = parseSegmentTemplate(paramXmlPullParser, null);
          j = i;
          localObject2 = localObject1;
        }
      }
    }
  }
  
  protected RangedUri parseRangedUrl(XmlPullParser paramXmlPullParser, String paramString1, String paramString2)
  {
    paramString1 = paramXmlPullParser.getAttributeValue(null, paramString1);
    long l1 = 0L;
    long l2 = -1L;
    paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, paramString2);
    long l3 = l2;
    if (paramXmlPullParser != null)
    {
      paramXmlPullParser = paramXmlPullParser.split("-");
      long l4 = Long.parseLong(paramXmlPullParser[0]);
      l1 = l4;
      l3 = l2;
      if (paramXmlPullParser.length == 2)
      {
        l3 = Long.parseLong(paramXmlPullParser[1]) - l4 + 1L;
        l1 = l4;
      }
    }
    return buildRangedUri(paramString1, l1, l3);
  }
  
  protected RepresentationInfo parseRepresentation(XmlPullParser paramXmlPullParser, String paramString1, String paramString2, String paramString3, int paramInt1, int paramInt2, float paramFloat, int paramInt3, int paramInt4, String paramString4, int paramInt5, List<Descriptor> paramList, SegmentBase paramSegmentBase)
    throws XmlPullParserException, IOException
  {
    String str1 = paramXmlPullParser.getAttributeValue(null, "id");
    int i = parseInt(paramXmlPullParser, "bandwidth", -1);
    String str2 = parseString(paramXmlPullParser, "mimeType", paramString2);
    String str3 = parseString(paramXmlPullParser, "codecs", paramString3);
    int j = parseInt(paramXmlPullParser, "width", paramInt1);
    int k = parseInt(paramXmlPullParser, "height", paramInt2);
    paramFloat = parseFrameRate(paramXmlPullParser, paramFloat);
    paramInt2 = paramInt3;
    int m = parseInt(paramXmlPullParser, "audioSamplingRate", paramInt4);
    paramString2 = null;
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    ArrayList localArrayList3 = new ArrayList();
    paramInt1 = 0;
    Object localObject = paramSegmentBase;
    String str4 = paramString1;
    do
    {
      paramXmlPullParser.next();
      if (!XmlPullParserUtil.isStartTag(paramXmlPullParser, "BaseURL")) {
        break;
      }
      paramInt4 = paramInt2;
      paramSegmentBase = paramString2;
      paramInt3 = paramInt1;
      paramString3 = str4;
      paramString1 = (String)localObject;
      if (paramInt1 == 0)
      {
        paramString3 = parseBaseUrl(paramXmlPullParser, str4);
        paramInt3 = 1;
        paramString1 = (String)localObject;
        paramSegmentBase = paramString2;
        paramInt4 = paramInt2;
      }
      paramInt2 = paramInt4;
      paramString2 = paramSegmentBase;
      paramInt1 = paramInt3;
      str4 = paramString3;
      localObject = paramString1;
    } while (!XmlPullParserUtil.isEndTag(paramXmlPullParser, "Representation"));
    paramXmlPullParser = buildFormat(str1, str2, j, k, paramFloat, paramInt4, m, i, paramString4, paramInt5, paramList, str3, localArrayList3);
    if (paramString1 != null) {}
    for (;;)
    {
      return new RepresentationInfo(paramXmlPullParser, paramString3, paramString1, paramSegmentBase, localArrayList1, localArrayList2, -1L);
      if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "AudioChannelConfiguration"))
      {
        paramInt4 = parseAudioChannelConfiguration(paramXmlPullParser);
        paramSegmentBase = paramString2;
        paramInt3 = paramInt1;
        paramString3 = str4;
        paramString1 = (String)localObject;
        break;
      }
      if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "SegmentBase"))
      {
        paramString1 = parseSegmentBase(paramXmlPullParser, (SegmentBase.SingleSegmentBase)localObject);
        paramInt4 = paramInt2;
        paramSegmentBase = paramString2;
        paramInt3 = paramInt1;
        paramString3 = str4;
        break;
      }
      if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "SegmentList"))
      {
        paramString1 = parseSegmentList(paramXmlPullParser, (SegmentBase.SegmentList)localObject);
        paramInt4 = paramInt2;
        paramSegmentBase = paramString2;
        paramInt3 = paramInt1;
        paramString3 = str4;
        break;
      }
      if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "SegmentTemplate"))
      {
        paramString1 = parseSegmentTemplate(paramXmlPullParser, (SegmentBase.SegmentTemplate)localObject);
        paramInt4 = paramInt2;
        paramSegmentBase = paramString2;
        paramInt3 = paramInt1;
        paramString3 = str4;
        break;
      }
      if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "ContentProtection"))
      {
        Pair localPair = parseContentProtection(paramXmlPullParser);
        if (localPair.first != null) {
          paramString2 = (String)localPair.first;
        }
        paramInt4 = paramInt2;
        paramSegmentBase = paramString2;
        paramInt3 = paramInt1;
        paramString3 = str4;
        paramString1 = (String)localObject;
        if (localPair.second == null) {
          break;
        }
        localArrayList1.add(localPair.second);
        paramInt4 = paramInt2;
        paramSegmentBase = paramString2;
        paramInt3 = paramInt1;
        paramString3 = str4;
        paramString1 = (String)localObject;
        break;
      }
      if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "InbandEventStream"))
      {
        localArrayList2.add(parseDescriptor(paramXmlPullParser, "InbandEventStream"));
        paramInt4 = paramInt2;
        paramSegmentBase = paramString2;
        paramInt3 = paramInt1;
        paramString3 = str4;
        paramString1 = (String)localObject;
        break;
      }
      paramInt4 = paramInt2;
      paramSegmentBase = paramString2;
      paramInt3 = paramInt1;
      paramString3 = str4;
      paramString1 = (String)localObject;
      if (!XmlPullParserUtil.isStartTag(paramXmlPullParser, "SupplementalProperty")) {
        break;
      }
      localArrayList3.add(parseDescriptor(paramXmlPullParser, "SupplementalProperty"));
      paramInt4 = paramInt2;
      paramSegmentBase = paramString2;
      paramInt3 = paramInt1;
      paramString3 = str4;
      paramString1 = (String)localObject;
      break;
      paramString1 = new SegmentBase.SingleSegmentBase();
    }
  }
  
  protected int parseRole(XmlPullParser paramXmlPullParser)
    throws XmlPullParserException, IOException
  {
    String str1 = parseString(paramXmlPullParser, "schemeIdUri", null);
    String str2 = parseString(paramXmlPullParser, "value", null);
    do
    {
      paramXmlPullParser.next();
    } while (!XmlPullParserUtil.isEndTag(paramXmlPullParser, "Role"));
    if (("urn:mpeg:dash:role:2011".equals(str1)) && ("main".equals(str2))) {}
    for (int i = 1;; i = 0) {
      return i;
    }
  }
  
  protected SegmentBase.SingleSegmentBase parseSegmentBase(XmlPullParser paramXmlPullParser, SegmentBase.SingleSegmentBase paramSingleSegmentBase)
    throws XmlPullParserException, IOException
  {
    long l1;
    long l2;
    label28:
    long l3;
    label47:
    long l4;
    label57:
    Object localObject;
    if (paramSingleSegmentBase != null)
    {
      l1 = paramSingleSegmentBase.timescale;
      l2 = parseLong(paramXmlPullParser, "timescale", l1);
      if (paramSingleSegmentBase == null) {
        break label173;
      }
      l1 = paramSingleSegmentBase.presentationTimeOffset;
      l3 = parseLong(paramXmlPullParser, "presentationTimeOffset", l1);
      if (paramSingleSegmentBase == null) {
        break label178;
      }
      l1 = paramSingleSegmentBase.indexStart;
      if (paramSingleSegmentBase == null) {
        break label183;
      }
      l4 = paramSingleSegmentBase.indexLength;
      localObject = paramXmlPullParser.getAttributeValue(null, "indexRange");
      if (localObject != null)
      {
        localObject = ((String)localObject).split("-");
        l1 = Long.parseLong(localObject[0]);
        l4 = Long.parseLong(localObject[1]) - l1 + 1L;
      }
      if (paramSingleSegmentBase == null) {
        break label189;
      }
      paramSingleSegmentBase = paramSingleSegmentBase.initialization;
    }
    for (;;)
    {
      paramXmlPullParser.next();
      localObject = paramSingleSegmentBase;
      if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "Initialization")) {
        localObject = parseInitialization(paramXmlPullParser);
      }
      paramSingleSegmentBase = (SegmentBase.SingleSegmentBase)localObject;
      if (XmlPullParserUtil.isEndTag(paramXmlPullParser, "SegmentBase"))
      {
        return buildSingleSegmentBase((RangedUri)localObject, l2, l3, l1, l4);
        l1 = 1L;
        break;
        label173:
        l1 = 0L;
        break label28;
        label178:
        l1 = 0L;
        break label47;
        label183:
        l4 = 0L;
        break label57;
        label189:
        paramSingleSegmentBase = null;
      }
    }
  }
  
  protected SegmentBase.SegmentList parseSegmentList(XmlPullParser paramXmlPullParser, SegmentBase.SegmentList paramSegmentList)
    throws XmlPullParserException, IOException
  {
    long l1;
    long l2;
    label28:
    long l3;
    label47:
    int i;
    label66:
    Object localObject1;
    Object localObject2;
    Object localObject3;
    Object localObject4;
    Object localObject5;
    Object localObject6;
    if (paramSegmentList != null)
    {
      l1 = paramSegmentList.timescale;
      l2 = parseLong(paramXmlPullParser, "timescale", l1);
      if (paramSegmentList == null) {
        break label203;
      }
      l1 = paramSegmentList.presentationTimeOffset;
      l3 = parseLong(paramXmlPullParser, "presentationTimeOffset", l1);
      if (paramSegmentList == null) {
        break label208;
      }
      l1 = paramSegmentList.duration;
      l1 = parseLong(paramXmlPullParser, "duration", l1);
      if (paramSegmentList == null) {
        break label215;
      }
      i = paramSegmentList.startNumber;
      i = parseInt(paramXmlPullParser, "startNumber", i);
      localObject1 = null;
      localObject2 = null;
      localObject3 = null;
      label118:
      do
      {
        paramXmlPullParser.next();
        if (!XmlPullParserUtil.isStartTag(paramXmlPullParser, "Initialization")) {
          break;
        }
        localObject4 = parseInitialization(paramXmlPullParser);
        localObject5 = localObject3;
        localObject6 = localObject2;
        localObject1 = localObject4;
        localObject2 = localObject6;
        localObject3 = localObject5;
      } while (!XmlPullParserUtil.isEndTag(paramXmlPullParser, "SegmentList"));
      paramXmlPullParser = (XmlPullParser)localObject4;
      localObject3 = localObject6;
      localObject2 = localObject5;
      if (paramSegmentList != null)
      {
        if (localObject4 == null) {
          break label313;
        }
        label160:
        if (localObject6 == null) {
          break label322;
        }
        label165:
        if (localObject5 == null) {
          break label331;
        }
        localObject2 = localObject5;
        localObject3 = localObject6;
        paramXmlPullParser = (XmlPullParser)localObject4;
      }
    }
    for (;;)
    {
      return buildSegmentList(paramXmlPullParser, l2, l3, i, l1, (List)localObject3, (List)localObject2);
      l1 = 1L;
      break;
      label203:
      l1 = 0L;
      break label28;
      label208:
      l1 = -9223372036854775807L;
      break label47;
      label215:
      i = 1;
      break label66;
      if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "SegmentTimeline"))
      {
        localObject6 = parseSegmentTimeline(paramXmlPullParser);
        localObject4 = localObject1;
        localObject5 = localObject3;
        break label118;
      }
      localObject4 = localObject1;
      localObject6 = localObject2;
      localObject5 = localObject3;
      if (!XmlPullParserUtil.isStartTag(paramXmlPullParser, "SegmentURL")) {
        break label118;
      }
      localObject5 = localObject3;
      if (localObject3 == null) {
        localObject5 = new ArrayList();
      }
      ((List)localObject5).add(parseSegmentUrl(paramXmlPullParser));
      localObject4 = localObject1;
      localObject6 = localObject2;
      break label118;
      label313:
      localObject4 = paramSegmentList.initialization;
      break label160;
      label322:
      localObject6 = paramSegmentList.segmentTimeline;
      break label165;
      label331:
      localObject2 = paramSegmentList.mediaSegments;
      paramXmlPullParser = (XmlPullParser)localObject4;
      localObject3 = localObject6;
    }
  }
  
  protected SegmentBase.SegmentTemplate parseSegmentTemplate(XmlPullParser paramXmlPullParser, SegmentBase.SegmentTemplate paramSegmentTemplate)
    throws XmlPullParserException, IOException
  {
    long l1;
    long l2;
    label28:
    long l3;
    label47:
    int i;
    label66:
    Object localObject1;
    label87:
    UrlTemplate localUrlTemplate1;
    label109:
    UrlTemplate localUrlTemplate2;
    Object localObject2;
    Object localObject3;
    Object localObject4;
    if (paramSegmentTemplate != null)
    {
      l1 = paramSegmentTemplate.timescale;
      l2 = parseLong(paramXmlPullParser, "timescale", l1);
      if (paramSegmentTemplate == null) {
        break label225;
      }
      l1 = paramSegmentTemplate.presentationTimeOffset;
      l3 = parseLong(paramXmlPullParser, "presentationTimeOffset", l1);
      if (paramSegmentTemplate == null) {
        break label230;
      }
      l1 = paramSegmentTemplate.duration;
      l1 = parseLong(paramXmlPullParser, "duration", l1);
      if (paramSegmentTemplate == null) {
        break label237;
      }
      i = paramSegmentTemplate.startNumber;
      i = parseInt(paramXmlPullParser, "startNumber", i);
      if (paramSegmentTemplate == null) {
        break label243;
      }
      localObject1 = paramSegmentTemplate.mediaTemplate;
      localUrlTemplate1 = parseUrlTemplate(paramXmlPullParser, "media", (UrlTemplate)localObject1);
      if (paramSegmentTemplate == null) {
        break label249;
      }
      localObject1 = paramSegmentTemplate.initializationTemplate;
      localUrlTemplate2 = parseUrlTemplate(paramXmlPullParser, "initialization", (UrlTemplate)localObject1);
      localObject2 = null;
      localObject3 = null;
      label155:
      do
      {
        paramXmlPullParser.next();
        if (!XmlPullParserUtil.isStartTag(paramXmlPullParser, "Initialization")) {
          break;
        }
        localObject1 = parseInitialization(paramXmlPullParser);
        localObject4 = localObject3;
        localObject2 = localObject1;
        localObject3 = localObject4;
      } while (!XmlPullParserUtil.isEndTag(paramXmlPullParser, "SegmentTemplate"));
      paramXmlPullParser = (XmlPullParser)localObject1;
      localObject3 = localObject4;
      if (paramSegmentTemplate != null)
      {
        if (localObject1 == null) {
          break label287;
        }
        label189:
        if (localObject4 == null) {
          break label296;
        }
        localObject3 = localObject4;
      }
    }
    for (paramXmlPullParser = (XmlPullParser)localObject1;; paramXmlPullParser = (XmlPullParser)localObject1)
    {
      return buildSegmentTemplate(paramXmlPullParser, l2, l3, i, l1, (List)localObject3, localUrlTemplate2, localUrlTemplate1);
      l1 = 1L;
      break;
      label225:
      l1 = 0L;
      break label28;
      label230:
      l1 = -9223372036854775807L;
      break label47;
      label237:
      i = 1;
      break label66;
      label243:
      localObject1 = null;
      break label87;
      label249:
      localObject1 = null;
      break label109;
      localObject1 = localObject2;
      localObject4 = localObject3;
      if (!XmlPullParserUtil.isStartTag(paramXmlPullParser, "SegmentTimeline")) {
        break label155;
      }
      localObject4 = parseSegmentTimeline(paramXmlPullParser);
      localObject1 = localObject2;
      break label155;
      label287:
      localObject1 = paramSegmentTemplate.initialization;
      break label189;
      label296:
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
      if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "S"))
      {
        l1 = parseLong(paramXmlPullParser, "t", l1);
        long l3 = parseLong(paramXmlPullParser, "d", -9223372036854775807L);
        int i = parseInt(paramXmlPullParser, "r", 0);
        for (int j = 0;; j++)
        {
          l2 = l1;
          if (j >= i + 1) {
            break;
          }
          localArrayList.add(buildSegmentTimelineElement(l1, l3));
          l1 += l3;
        }
      }
      l1 = l2;
    } while (!XmlPullParserUtil.isEndTag(paramXmlPullParser, "SegmentTimeline"));
    return localArrayList;
  }
  
  protected RangedUri parseSegmentUrl(XmlPullParser paramXmlPullParser)
  {
    return parseRangedUrl(paramXmlPullParser, "media", "mediaRange");
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
  
  protected static final class RepresentationInfo
  {
    public final String baseUrl;
    public final ArrayList<DrmInitData.SchemeData> drmSchemeDatas;
    public final String drmSchemeType;
    public final Format format;
    public final ArrayList<Descriptor> inbandEventStreams;
    public final long revisionId;
    public final SegmentBase segmentBase;
    
    public RepresentationInfo(Format paramFormat, String paramString1, SegmentBase paramSegmentBase, String paramString2, ArrayList<DrmInitData.SchemeData> paramArrayList, ArrayList<Descriptor> paramArrayList1, long paramLong)
    {
      this.format = paramFormat;
      this.baseUrl = paramString1;
      this.segmentBase = paramSegmentBase;
      this.drmSchemeType = paramString2;
      this.drmSchemeDatas = paramArrayList;
      this.inbandEventStreams = paramArrayList1;
      this.revisionId = paramLong;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/dash/manifest/DashManifestParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */