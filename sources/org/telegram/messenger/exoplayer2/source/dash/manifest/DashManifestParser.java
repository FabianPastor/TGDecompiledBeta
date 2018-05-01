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
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.ParserException;
import org.telegram.messenger.exoplayer2.drm.DrmInitData;
import org.telegram.messenger.exoplayer2.drm.DrmInitData.SchemeData;
import org.telegram.messenger.exoplayer2.extractor.mp4.PsshAtomUtil;
import org.telegram.messenger.exoplayer2.metadata.emsg.EventMessage;
import org.telegram.messenger.exoplayer2.source.dash.manifest.SegmentBase.SegmentList;
import org.telegram.messenger.exoplayer2.source.dash.manifest.SegmentBase.SegmentTemplate;
import org.telegram.messenger.exoplayer2.source.dash.manifest.SegmentBase.SegmentTimelineElement;
import org.telegram.messenger.exoplayer2.source.dash.manifest.SegmentBase.SingleSegmentBase;
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

public class DashManifestParser extends DefaultHandler implements Parser<DashManifest> {
    private static final Pattern CEA_608_ACCESSIBILITY_PATTERN = Pattern.compile("CC([1-4])=.*");
    private static final Pattern CEA_708_ACCESSIBILITY_PATTERN = Pattern.compile("([1-9]|[1-5][0-9]|6[0-3])=.*");
    private static final Pattern FRAME_RATE_PATTERN = Pattern.compile("(\\d+)(?:/(\\d+))?");
    private static final String TAG = "MpdParser";
    private final String contentId;
    private final XmlPullParserFactory xmlParserFactory;

    protected static final class RepresentationInfo {
        public final String baseUrl;
        public final ArrayList<SchemeData> drmSchemeDatas;
        public final String drmSchemeType;
        public final Format format;
        public final ArrayList<Descriptor> inbandEventStreams;
        public final long revisionId;
        public final SegmentBase segmentBase;

        public RepresentationInfo(Format format, String str, SegmentBase segmentBase, String str2, ArrayList<SchemeData> arrayList, ArrayList<Descriptor> arrayList2, long j) {
            this.format = format;
            this.baseUrl = str;
            this.segmentBase = segmentBase;
            this.drmSchemeType = str2;
            this.drmSchemeDatas = arrayList;
            this.inbandEventStreams = arrayList2;
            this.revisionId = j;
        }
    }

    protected void parseAdaptationSetChild(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
    }

    public DashManifestParser() {
        this(null);
    }

    public DashManifestParser(String str) {
        this.contentId = str;
        try {
            this.xmlParserFactory = XmlPullParserFactory.newInstance();
        } catch (String str2) {
            throw new RuntimeException("Couldn't create XmlPullParserFactory instance", str2);
        }
    }

    public DashManifest parse(Uri uri, InputStream inputStream) throws IOException {
        try {
            XmlPullParser newPullParser = this.xmlParserFactory.newPullParser();
            newPullParser.setInput(inputStream, null);
            if (newPullParser.next() == 2) {
                if ("MPD".equals(newPullParser.getName()) != null) {
                    return parseMediaPresentationDescription(newPullParser, uri.toString());
                }
            }
            throw new ParserException("inputStream does not contain a valid media presentation description");
        } catch (Throwable e) {
            throw new ParserException(e);
        }
    }

    protected DashManifest parseMediaPresentationDescription(XmlPullParser xmlPullParser, String str) throws XmlPullParserException, IOException {
        DashManifestParser dashManifestParser;
        XmlPullParser xmlPullParser2 = xmlPullParser;
        long parseDateTime = parseDateTime(xmlPullParser2, "availabilityStartTime", C0542C.TIME_UNSET);
        long parseDuration = parseDuration(xmlPullParser2, "mediaPresentationDuration", C0542C.TIME_UNSET);
        long parseDuration2 = parseDuration(xmlPullParser2, "minBufferTime", C0542C.TIME_UNSET);
        String attributeValue = xmlPullParser2.getAttributeValue(null, "type");
        Object obj = null;
        boolean z = attributeValue != null && attributeValue.equals("dynamic");
        long parseDuration3 = z ? parseDuration(xmlPullParser2, "minimumUpdatePeriod", C0542C.TIME_UNSET) : C0542C.TIME_UNSET;
        long parseDuration4 = z ? parseDuration(xmlPullParser2, "timeShiftBufferDepth", C0542C.TIME_UNSET) : C0542C.TIME_UNSET;
        long parseDuration5 = z ? parseDuration(xmlPullParser2, "suggestedPresentationDelay", C0542C.TIME_UNSET) : C0542C.TIME_UNSET;
        long parseDateTime2 = parseDateTime(xmlPullParser2, "publishTime", C0542C.TIME_UNSET);
        List arrayList = new ArrayList();
        String str2 = str;
        long j = z ? C0542C.TIME_UNSET : 0;
        Uri uri = null;
        UtcTimingElement utcTimingElement = null;
        Object obj2 = null;
        while (true) {
            long j2;
            Object obj3;
            long j3;
            String str3;
            xmlPullParser.next();
            long j4 = parseDuration4;
            if (!XmlPullParserUtil.isStartTag(xmlPullParser2, "BaseURL")) {
                if (XmlPullParserUtil.isStartTag(xmlPullParser2, "UTCTiming")) {
                    j2 = parseDuration3;
                    utcTimingElement = parseUtcTiming(xmlPullParser);
                } else if (XmlPullParserUtil.isStartTag(xmlPullParser2, "Location")) {
                    j2 = parseDuration3;
                    uri = Uri.parse(xmlPullParser.nextText());
                } else if (XmlPullParserUtil.isStartTag(xmlPullParser2, "Period") && r21 == null) {
                    obj3 = obj;
                    Pair parsePeriod = parsePeriod(xmlPullParser2, str2, j);
                    j3 = j;
                    Period period = (Period) parsePeriod.first;
                    str3 = str2;
                    j2 = parseDuration3;
                    if (period.startMs != C0542C.TIME_UNSET) {
                        long longValue = ((Long) parsePeriod.second).longValue();
                        if (longValue == C0542C.TIME_UNSET) {
                            j3 = C0542C.TIME_UNSET;
                        } else {
                            j3 = period.startMs + longValue;
                        }
                        arrayList.add(period);
                        obj = obj3;
                        j = j3;
                        str2 = str3;
                        if (XmlPullParserUtil.isEndTag(xmlPullParser2, "MPD")) {
                            break;
                        }
                        parseDuration4 = j4;
                        parseDuration3 = j2;
                    } else if (z) {
                        obj = obj3;
                        j = j3;
                        str2 = str3;
                        obj2 = 1;
                        if (XmlPullParserUtil.isEndTag(xmlPullParser2, "MPD")) {
                            break;
                        }
                        parseDuration4 = j4;
                        parseDuration3 = j2;
                    } else {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("Unable to determine start of period ");
                        stringBuilder.append(arrayList.size());
                        throw new ParserException(stringBuilder.toString());
                    }
                }
                dashManifestParser = this;
                if (XmlPullParserUtil.isEndTag(xmlPullParser2, "MPD")) {
                    break;
                }
                parseDuration4 = j4;
                parseDuration3 = j2;
            } else if (obj == null) {
                dashManifestParser = this;
                str2 = parseBaseUrl(xmlPullParser2, str2);
                j2 = parseDuration3;
                obj = 1;
                if (XmlPullParserUtil.isEndTag(xmlPullParser2, "MPD")) {
                    break;
                }
                parseDuration4 = j4;
                parseDuration3 = j2;
            }
            dashManifestParser = this;
            j3 = j;
            obj3 = obj;
            str3 = str2;
            j2 = parseDuration3;
            obj = obj3;
            j = j3;
            str2 = str3;
            if (XmlPullParserUtil.isEndTag(xmlPullParser2, "MPD")) {
                break;
            }
            parseDuration4 = j4;
            parseDuration3 = j2;
        }
        if (parseDuration == C0542C.TIME_UNSET) {
            if (j != C0542C.TIME_UNSET) {
                parseDuration = j;
            } else if (!z) {
                throw new ParserException("Unable to determine duration of static manifest.");
            }
        }
        if (arrayList.isEmpty()) {
            throw new ParserException("No periods found.");
        }
        return dashManifestParser.buildMediaPresentationDescription(parseDateTime, parseDuration, parseDuration2, z, j2, j4, parseDuration5, parseDateTime2, utcTimingElement, uri, arrayList);
    }

    protected DashManifest buildMediaPresentationDescription(long j, long j2, long j3, boolean z, long j4, long j5, long j6, long j7, UtcTimingElement utcTimingElement, Uri uri, List<Period> list) {
        return new DashManifest(j, j2, j3, z, j4, j5, j6, j7, utcTimingElement, uri, list);
    }

    protected UtcTimingElement parseUtcTiming(XmlPullParser xmlPullParser) {
        return buildUtcTimingElement(xmlPullParser.getAttributeValue(null, "schemeIdUri"), xmlPullParser.getAttributeValue(null, "value"));
    }

    protected UtcTimingElement buildUtcTimingElement(String str, String str2) {
        return new UtcTimingElement(str, str2);
    }

    protected Pair<Period, Long> parsePeriod(XmlPullParser xmlPullParser, String str, long j) throws XmlPullParserException, IOException {
        String attributeValue = xmlPullParser.getAttributeValue(null, TtmlNode.ATTR_ID);
        long parseDuration = parseDuration(xmlPullParser, TtmlNode.START, j);
        j = parseDuration(xmlPullParser, "duration", C0542C.TIME_UNSET);
        List arrayList = new ArrayList();
        List arrayList2 = new ArrayList();
        Object obj = null;
        SegmentBase segmentBase = null;
        do {
            xmlPullParser.next();
            if (XmlPullParserUtil.isStartTag(xmlPullParser, "BaseURL")) {
                if (obj == null) {
                    str = parseBaseUrl(xmlPullParser, str);
                    obj = 1;
                }
            } else if (XmlPullParserUtil.isStartTag(xmlPullParser, "AdaptationSet")) {
                arrayList.add(parseAdaptationSet(xmlPullParser, str, segmentBase));
            } else if (XmlPullParserUtil.isStartTag(xmlPullParser, "EventStream")) {
                arrayList2.add(parseEventStream(xmlPullParser));
            } else if (XmlPullParserUtil.isStartTag(xmlPullParser, "SegmentBase")) {
                segmentBase = parseSegmentBase(xmlPullParser, null);
            } else if (XmlPullParserUtil.isStartTag(xmlPullParser, "SegmentList")) {
                segmentBase = parseSegmentList(xmlPullParser, null);
            } else if (XmlPullParserUtil.isStartTag(xmlPullParser, "SegmentTemplate")) {
                segmentBase = parseSegmentTemplate(xmlPullParser, null);
            }
        } while (!XmlPullParserUtil.isEndTag(xmlPullParser, "Period"));
        return Pair.create(buildPeriod(attributeValue, parseDuration, arrayList, arrayList2), Long.valueOf(j));
    }

    protected Period buildPeriod(String str, long j, List<AdaptationSet> list, List<EventStream> list2) {
        return new Period(str, j, list, list2);
    }

    protected AdaptationSet parseAdaptationSet(XmlPullParser xmlPullParser, String str, SegmentBase segmentBase) throws XmlPullParserException, IOException {
        List list;
        ArrayList arrayList;
        ArrayList arrayList2;
        int i;
        DashManifestParser dashManifestParser = this;
        XmlPullParser xmlPullParser2 = xmlPullParser;
        int parseInt = parseInt(xmlPullParser2, TtmlNode.ATTR_ID, -1);
        int parseContentType = parseContentType(xmlPullParser);
        String str2 = null;
        String attributeValue = xmlPullParser2.getAttributeValue(null, "mimeType");
        String attributeValue2 = xmlPullParser2.getAttributeValue(null, "codecs");
        int parseInt2 = parseInt(xmlPullParser2, "width", -1);
        int parseInt3 = parseInt(xmlPullParser2, "height", -1);
        float parseFrameRate = parseFrameRate(xmlPullParser2, -1.0f);
        int parseInt4 = parseInt(xmlPullParser2, "audioSamplingRate", -1);
        String attributeValue3 = xmlPullParser2.getAttributeValue(null, "lang");
        ArrayList arrayList3 = new ArrayList();
        ArrayList arrayList4 = new ArrayList();
        ArrayList arrayList5 = new ArrayList();
        ArrayList arrayList6 = new ArrayList();
        ArrayList arrayList7 = new ArrayList();
        String str3 = str;
        SegmentBase segmentBase2 = segmentBase;
        int i2 = parseContentType;
        int i3 = -1;
        String str4 = attributeValue3;
        String str5 = null;
        int i4 = 0;
        int i5 = i4;
        while (true) {
            String parseBaseUrl;
            String str6;
            ArrayList arrayList8;
            ArrayList arrayList9;
            String str7;
            XmlPullParser xmlPullParser3;
            List list2;
            xmlPullParser.next();
            int i6;
            if (XmlPullParserUtil.isStartTag(xmlPullParser2, "BaseURL")) {
                if (i4 == 0) {
                    parseBaseUrl = parseBaseUrl(xmlPullParser2, str3);
                    i4 = 1;
                    str6 = str4;
                }
                i6 = i2;
                str6 = str4;
                parseBaseUrl = str3;
                list = arrayList7;
                arrayList = arrayList6;
                arrayList2 = arrayList5;
                arrayList8 = arrayList4;
                arrayList9 = arrayList3;
                str7 = str2;
                xmlPullParser3 = xmlPullParser2;
                i = i6;
                if (!XmlPullParserUtil.isEndTag(xmlPullParser3, "AdaptationSet")) {
                    break;
                }
                xmlPullParser2 = xmlPullParser3;
                arrayList4 = arrayList8;
                i2 = i;
                str3 = parseBaseUrl;
                arrayList6 = arrayList;
                arrayList5 = arrayList2;
                arrayList3 = arrayList9;
                str2 = str7;
                list2 = list;
                str4 = str6;
            } else {
                if (!XmlPullParserUtil.isStartTag(xmlPullParser2, "ContentProtection")) {
                    if (XmlPullParserUtil.isStartTag(xmlPullParser2, "ContentComponent")) {
                        str6 = checkLanguageConsistency(str4, xmlPullParser2.getAttributeValue(str2, "lang"));
                        parseBaseUrl = str3;
                        list = arrayList7;
                        arrayList = arrayList6;
                        arrayList2 = arrayList5;
                        arrayList8 = arrayList4;
                        arrayList9 = arrayList3;
                        str7 = str2;
                        xmlPullParser3 = xmlPullParser2;
                        i = checkContentTypeConsistency(i2, parseContentType(xmlPullParser));
                    } else if (XmlPullParserUtil.isStartTag(xmlPullParser2, "Role")) {
                        i5 |= parseRole(xmlPullParser);
                    } else if (XmlPullParserUtil.isStartTag(xmlPullParser2, "AudioChannelConfiguration")) {
                        i3 = parseAudioChannelConfiguration(xmlPullParser);
                    } else {
                        if (XmlPullParserUtil.isStartTag(xmlPullParser2, "Accessibility")) {
                            arrayList5.add(parseDescriptor(xmlPullParser2, "Accessibility"));
                        } else if (XmlPullParserUtil.isStartTag(xmlPullParser2, "SupplementalProperty")) {
                            arrayList6.add(parseDescriptor(xmlPullParser2, "SupplementalProperty"));
                        } else if (XmlPullParserUtil.isStartTag(xmlPullParser2, "Representation")) {
                            int i7 = i2;
                            str6 = str4;
                            parseBaseUrl = str3;
                            ArrayList arrayList10 = arrayList7;
                            arrayList = arrayList6;
                            arrayList2 = arrayList5;
                            r34 = arrayList4;
                            arrayList9 = arrayList3;
                            str7 = str2;
                            RepresentationInfo parseRepresentation = parseRepresentation(xmlPullParser2, str3, attributeValue, attributeValue2, parseInt2, parseInt3, parseFrameRate, i3, parseInt4, str6, i5, arrayList2, segmentBase2);
                            int checkContentTypeConsistency = checkContentTypeConsistency(i7, getContentType(parseRepresentation.format));
                            list = arrayList10;
                            list.add(parseRepresentation);
                            i = checkContentTypeConsistency;
                            arrayList8 = r34;
                            xmlPullParser3 = xmlPullParser;
                        } else {
                            SegmentBase parseSegmentBase;
                            str6 = str4;
                            parseBaseUrl = str3;
                            list = arrayList7;
                            arrayList = arrayList6;
                            arrayList2 = arrayList5;
                            r34 = arrayList4;
                            arrayList9 = arrayList3;
                            str7 = str2;
                            i6 = i2;
                            xmlPullParser3 = xmlPullParser;
                            if (XmlPullParserUtil.isStartTag(xmlPullParser3, "SegmentBase")) {
                                parseSegmentBase = parseSegmentBase(xmlPullParser3, (SingleSegmentBase) segmentBase2);
                            } else if (XmlPullParserUtil.isStartTag(xmlPullParser3, "SegmentList")) {
                                parseSegmentBase = parseSegmentList(xmlPullParser3, (SegmentList) segmentBase2);
                            } else if (XmlPullParserUtil.isStartTag(xmlPullParser3, "SegmentTemplate")) {
                                parseSegmentBase = parseSegmentTemplate(xmlPullParser3, (SegmentTemplate) segmentBase2);
                            } else {
                                if (XmlPullParserUtil.isStartTag(xmlPullParser3, "InbandEventStream")) {
                                    arrayList8 = r34;
                                    arrayList8.add(parseDescriptor(xmlPullParser3, "InbandEventStream"));
                                } else {
                                    arrayList8 = r34;
                                    if (XmlPullParserUtil.isStartTag(xmlPullParser)) {
                                        parseAdaptationSetChild(xmlPullParser);
                                    }
                                }
                                i = i6;
                            }
                            segmentBase2 = parseSegmentBase;
                            i = i6;
                            arrayList8 = r34;
                        }
                        i6 = i2;
                        str6 = str4;
                        parseBaseUrl = str3;
                        list = arrayList7;
                        arrayList = arrayList6;
                        arrayList2 = arrayList5;
                        arrayList8 = arrayList4;
                        arrayList9 = arrayList3;
                        str7 = str2;
                        xmlPullParser3 = xmlPullParser2;
                        i = i6;
                    }
                    if (!XmlPullParserUtil.isEndTag(xmlPullParser3, "AdaptationSet")) {
                        break;
                    }
                    xmlPullParser2 = xmlPullParser3;
                    arrayList4 = arrayList8;
                    i2 = i;
                    str3 = parseBaseUrl;
                    arrayList6 = arrayList;
                    arrayList5 = arrayList2;
                    arrayList3 = arrayList9;
                    str2 = str7;
                    list2 = list;
                    str4 = str6;
                } else {
                    Pair parseContentProtection = parseContentProtection(xmlPullParser);
                    if (parseContentProtection.first != null) {
                        str5 = (String) parseContentProtection.first;
                    }
                    if (parseContentProtection.second != null) {
                        arrayList3.add(parseContentProtection.second);
                    }
                }
                str6 = str4;
                parseBaseUrl = str3;
            }
            list = arrayList7;
            arrayList = arrayList6;
            arrayList2 = arrayList5;
            arrayList8 = arrayList4;
            arrayList9 = arrayList3;
            str7 = str2;
            xmlPullParser3 = xmlPullParser2;
            i = i2;
            if (!XmlPullParserUtil.isEndTag(xmlPullParser3, "AdaptationSet")) {
                break;
            }
            xmlPullParser2 = xmlPullParser3;
            arrayList4 = arrayList8;
            i2 = i;
            str3 = parseBaseUrl;
            arrayList6 = arrayList;
            arrayList5 = arrayList2;
            arrayList3 = arrayList9;
            str2 = str7;
            list2 = list;
            str4 = str6;
        }
        List arrayList11 = new ArrayList(list.size());
        for (int i8 = 0; i8 < list.size(); i8++) {
            arrayList11.add(buildRepresentation((RepresentationInfo) list.get(i8), dashManifestParser.contentId, str5, arrayList9, arrayList8));
        }
        return buildAdaptationSet(parseInt, i, arrayList11, arrayList2, arrayList);
    }

    protected AdaptationSet buildAdaptationSet(int i, int i2, List<Representation> list, List<Descriptor> list2, List<Descriptor> list3) {
        return new AdaptationSet(i, i2, list, list2, list3);
    }

    protected int parseContentType(XmlPullParser xmlPullParser) {
        xmlPullParser = xmlPullParser.getAttributeValue(null, "contentType");
        if (TextUtils.isEmpty(xmlPullParser)) {
            return -1;
        }
        if (MimeTypes.BASE_TYPE_AUDIO.equals(xmlPullParser)) {
            return 1;
        }
        if (MimeTypes.BASE_TYPE_VIDEO.equals(xmlPullParser)) {
            return 2;
        }
        if (MimeTypes.BASE_TYPE_TEXT.equals(xmlPullParser) != null) {
            return 3;
        }
        return -1;
    }

    protected int getContentType(Format format) {
        format = format.sampleMimeType;
        if (TextUtils.isEmpty(format)) {
            return -1;
        }
        if (MimeTypes.isVideo(format)) {
            return 2;
        }
        if (MimeTypes.isAudio(format)) {
            return 1;
        }
        if (mimeTypeIsRawText(format) != null) {
            return 3;
        }
        return -1;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected Pair<String, SchemeData> parseContentProtection(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
        Object obj;
        Object obj2;
        byte[] bArr;
        Object obj3 = null;
        String attributeValue = xmlPullParser.getAttributeValue(null, "schemeIdUri");
        if (attributeValue != null) {
            int i;
            Object attributeValue2;
            attributeValue = Util.toLowerInvariant(attributeValue);
            int hashCode = attributeValue.hashCode();
            if (hashCode == 489446379) {
                if (attributeValue.equals("urn:uuid:9a04f079-9840-4286-ab92-e65be0885f95")) {
                    i = 1;
                    switch (i) {
                        case 0:
                            attributeValue = xmlPullParser.getAttributeValue(null, "value");
                            attributeValue2 = xmlPullParser.getAttributeValue(null, "cenc:default_KID");
                            if (!!TextUtils.isEmpty(attributeValue2)) {
                                break;
                            }
                            obj = attributeValue;
                            obj2 = null;
                            bArr = obj2;
                            break;
                        case 1:
                            obj2 = C0542C.PLAYREADY_UUID;
                            break;
                        case 2:
                            obj2 = C0542C.WIDEVINE_UUID;
                            break;
                        default:
                            break;
                    }
                }
            } else if (hashCode == 755418770) {
                if (attributeValue.equals("urn:uuid:edef8ba9-79d6-4ace-a3c8-27dcd51d21ed")) {
                    i = 2;
                    switch (i) {
                        case 0:
                            attributeValue = xmlPullParser.getAttributeValue(null, "value");
                            attributeValue2 = xmlPullParser.getAttributeValue(null, "cenc:default_KID");
                            if (!TextUtils.isEmpty(attributeValue2)) {
                                break;
                            }
                            obj = attributeValue;
                            obj2 = null;
                            bArr = obj2;
                            break;
                        case 1:
                            obj2 = C0542C.PLAYREADY_UUID;
                            break;
                        case 2:
                            obj2 = C0542C.WIDEVINE_UUID;
                            break;
                        default:
                            break;
                    }
                }
            } else if (hashCode == NUM) {
                if (attributeValue.equals("urn:mpeg:dash:mp4protection:2011")) {
                    i = 0;
                    switch (i) {
                        case 0:
                            attributeValue = xmlPullParser.getAttributeValue(null, "value");
                            attributeValue2 = xmlPullParser.getAttributeValue(null, "cenc:default_KID");
                            if (!TextUtils.isEmpty(attributeValue2) && !"00000000-0000-0000-0000-000000000000".equals(attributeValue2)) {
                                String[] split = attributeValue2.split("\\s+");
                                UUID[] uuidArr = new UUID[split.length];
                                for (int i2 = 0; i2 < split.length; i2++) {
                                    uuidArr[i2] = UUID.fromString(split[i2]);
                                }
                                bArr = PsshAtomUtil.buildPsshAtom(C0542C.COMMON_PSSH_UUID, uuidArr, null);
                                obj = attributeValue;
                                obj2 = C0542C.COMMON_PSSH_UUID;
                                break;
                            }
                            obj = attributeValue;
                            obj2 = null;
                            bArr = obj2;
                            break;
                        case 1:
                            obj2 = C0542C.PLAYREADY_UUID;
                            break;
                        case 2:
                            obj2 = C0542C.WIDEVINE_UUID;
                            break;
                        default:
                            break;
                    }
                }
            }
            i = -1;
            switch (i) {
                case 0:
                    attributeValue = xmlPullParser.getAttributeValue(null, "value");
                    attributeValue2 = xmlPullParser.getAttributeValue(null, "cenc:default_KID");
                    if (!TextUtils.isEmpty(attributeValue2)) {
                        break;
                    }
                    obj = attributeValue;
                    obj2 = null;
                    bArr = obj2;
                    break;
                case 1:
                    obj2 = C0542C.PLAYREADY_UUID;
                    break;
                case 2:
                    obj2 = C0542C.WIDEVINE_UUID;
                    break;
                default:
                    break;
            }
        }
        obj2 = null;
        bArr = obj2;
        obj = bArr;
        boolean z = false;
        do {
            xmlPullParser.next();
            if (XmlPullParserUtil.isStartTag(xmlPullParser, "widevine:license")) {
                String attributeValue3 = xmlPullParser.getAttributeValue(null, "robustness_level");
                z = attributeValue3 != null && attributeValue3.startsWith("HW");
            } else if (bArr == null) {
                if (XmlPullParserUtil.isStartTag(xmlPullParser, "cenc:pssh") && xmlPullParser.next() == 4) {
                    byte[] decode = Base64.decode(xmlPullParser.getText(), 0);
                    UUID parseUuid = PsshAtomUtil.parseUuid(decode);
                    if (parseUuid == null) {
                        Log.w(TAG, "Skipping malformed cenc:pssh data");
                        obj2 = parseUuid;
                        bArr = null;
                    } else {
                        UUID uuid = parseUuid;
                        bArr = decode;
                        obj2 = uuid;
                    }
                } else if (C0542C.PLAYREADY_UUID.equals(obj2) && XmlPullParserUtil.isStartTag(xmlPullParser, "mspr:pro") && xmlPullParser.next() == 4) {
                    bArr = PsshAtomUtil.buildPsshAtom(C0542C.PLAYREADY_UUID, Base64.decode(xmlPullParser.getText(), 0));
                }
            }
        } while (!XmlPullParserUtil.isEndTag(xmlPullParser, "ContentProtection"));
        if (obj2 != null) {
            obj3 = new SchemeData(obj2, MimeTypes.VIDEO_MP4, bArr, z);
        }
        return Pair.create(obj, obj3);
    }

    protected int parseRole(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
        String parseString = parseString(xmlPullParser, "schemeIdUri", null);
        String parseString2 = parseString(xmlPullParser, "value", null);
        do {
            xmlPullParser.next();
        } while (!XmlPullParserUtil.isEndTag(xmlPullParser, "Role"));
        return ("urn:mpeg:dash:role:2011".equals(parseString) == null || "main".equals(parseString2) == null) ? null : 1;
    }

    protected RepresentationInfo parseRepresentation(XmlPullParser xmlPullParser, String str, String str2, String str3, int i, int i2, float f, int i3, int i4, String str4, int i5, List<Descriptor> list, SegmentBase segmentBase) throws XmlPullParserException, IOException {
        String str5;
        String str6;
        String str7;
        SegmentBase segmentBase2;
        DashManifestParser dashManifestParser = this;
        XmlPullParser xmlPullParser2 = xmlPullParser;
        String attributeValue = xmlPullParser2.getAttributeValue(null, TtmlNode.ATTR_ID);
        int parseInt = parseInt(xmlPullParser2, "bandwidth", -1);
        String parseString = parseString(xmlPullParser2, "mimeType", str2);
        String parseString2 = parseString(xmlPullParser2, "codecs", str3);
        int parseInt2 = parseInt(xmlPullParser2, "width", i);
        int parseInt3 = parseInt(xmlPullParser2, "height", i2);
        float parseFrameRate = parseFrameRate(xmlPullParser2, f);
        int parseInt4 = parseInt(xmlPullParser2, "audioSamplingRate", i4);
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        Object obj = null;
        int i6 = i3;
        SegmentBase segmentBase3 = segmentBase;
        String str8 = null;
        String str9 = str;
        while (true) {
            SegmentBase segmentBase4;
            xmlPullParser.next();
            str5 = parseString2;
            if (!XmlPullParserUtil.isStartTag(xmlPullParser2, "BaseURL")) {
                if (XmlPullParserUtil.isStartTag(xmlPullParser2, "AudioChannelConfiguration")) {
                    str6 = str9;
                    i6 = parseAudioChannelConfiguration(xmlPullParser);
                } else if (XmlPullParserUtil.isStartTag(xmlPullParser2, "SegmentBase")) {
                    segmentBase3 = parseSegmentBase(xmlPullParser2, (SingleSegmentBase) segmentBase3);
                } else if (XmlPullParserUtil.isStartTag(xmlPullParser2, "SegmentList")) {
                    segmentBase3 = parseSegmentList(xmlPullParser2, (SegmentList) segmentBase3);
                } else if (XmlPullParserUtil.isStartTag(xmlPullParser2, "SegmentTemplate")) {
                    segmentBase3 = parseSegmentTemplate(xmlPullParser2, (SegmentTemplate) segmentBase3);
                } else if (XmlPullParserUtil.isStartTag(xmlPullParser2, "ContentProtection")) {
                    Pair parseContentProtection = parseContentProtection(xmlPullParser);
                    str6 = str9;
                    if (parseContentProtection.first != null) {
                        str8 = (String) parseContentProtection.first;
                    }
                    if (parseContentProtection.second != null) {
                        arrayList.add(parseContentProtection.second);
                    }
                } else {
                    str6 = str9;
                    if (XmlPullParserUtil.isStartTag(xmlPullParser2, "InbandEventStream")) {
                        arrayList2.add(parseDescriptor(xmlPullParser2, "InbandEventStream"));
                    } else if (XmlPullParserUtil.isStartTag(xmlPullParser2, "SupplementalProperty")) {
                        arrayList3.add(parseDescriptor(xmlPullParser2, "SupplementalProperty"));
                    }
                }
                str7 = str8;
                segmentBase4 = segmentBase3;
                if (XmlPullParserUtil.isEndTag(xmlPullParser2, "Representation")) {
                    break;
                }
                segmentBase3 = segmentBase4;
                str8 = str7;
                parseString2 = str5;
                str9 = str6;
            } else if (obj == null) {
                str9 = parseBaseUrl(xmlPullParser2, str9);
                obj = 1;
            }
            str6 = str9;
            str7 = str8;
            segmentBase4 = segmentBase3;
            if (XmlPullParserUtil.isEndTag(xmlPullParser2, "Representation")) {
                break;
            }
            segmentBase3 = segmentBase4;
            str8 = str7;
            parseString2 = str5;
            str9 = str6;
        }
        ArrayList arrayList4 = arrayList2;
        Format buildFormat = buildFormat(attributeValue, parseString, parseInt2, parseInt3, parseFrameRate, i6, parseInt4, parseInt, str4, i5, list, str5, arrayList3);
        if (segmentBase4 != null) {
            segmentBase2 = segmentBase4;
        } else {
            segmentBase2 = new SingleSegmentBase();
        }
        return new RepresentationInfo(buildFormat, str6, segmentBase2, str7, arrayList, arrayList4, -1);
    }

    protected Format buildFormat(String str, String str2, int i, int i2, float f, int i3, int i4, int i5, String str3, int i6, List<Descriptor> list, String str4, List<Descriptor> list2) {
        String str5;
        String str6 = str2;
        String str7 = str4;
        String sampleMimeType = getSampleMimeType(str6, str7);
        if (sampleMimeType != null) {
            if (MimeTypes.AUDIO_E_AC3.equals(sampleMimeType)) {
                sampleMimeType = parseEac3SupplementalProperties(list2);
            }
            str5 = sampleMimeType;
            if (MimeTypes.isVideo(str5)) {
                return Format.createVideoContainerFormat(str, str6, str5, str7, i5, i, i2, f, null, i6);
            }
            if (MimeTypes.isAudio(str5)) {
                return Format.createAudioContainerFormat(str, str6, str5, str7, i5, i3, i4, null, i6, str3);
            }
            if (mimeTypeIsRawText(str5)) {
                int parseCea608AccessibilityChannel = MimeTypes.APPLICATION_CEA608.equals(str5) ? parseCea608AccessibilityChannel(list) : MimeTypes.APPLICATION_CEA708.equals(str5) ? parseCea708AccessibilityChannel(list) : -1;
                return Format.createTextContainerFormat(str, str6, str5, str7, i5, i6, str3, parseCea608AccessibilityChannel);
            }
        }
        str5 = sampleMimeType;
        return Format.createContainerFormat(str, str6, str5, str7, i5, i6, str3);
    }

    protected Representation buildRepresentation(RepresentationInfo representationInfo, String str, String str2, ArrayList<SchemeData> arrayList, ArrayList<Descriptor> arrayList2) {
        Format format = representationInfo.format;
        if (representationInfo.drmSchemeType != null) {
            str2 = representationInfo.drmSchemeType;
        }
        List list = representationInfo.drmSchemeDatas;
        list.addAll(arrayList);
        if (list.isEmpty() == null) {
            filterRedundantIncompleteSchemeDatas(list);
            format = format.copyWithDrmInitData(new DrmInitData(str2, list));
        }
        Format format2 = format;
        List list2 = representationInfo.inbandEventStreams;
        list2.addAll(arrayList2);
        return Representation.newInstance(str, representationInfo.revisionId, format2, representationInfo.baseUrl, representationInfo.segmentBase, list2);
    }

    protected SingleSegmentBase parseSegmentBase(XmlPullParser xmlPullParser, SingleSegmentBase singleSegmentBase) throws XmlPullParserException, IOException {
        long parseLong;
        XmlPullParser xmlPullParser2 = xmlPullParser;
        SingleSegmentBase singleSegmentBase2 = singleSegmentBase;
        long parseLong2 = parseLong(xmlPullParser2, "timescale", singleSegmentBase2 != null ? singleSegmentBase2.timescale : 1);
        long j = 0;
        long parseLong3 = parseLong(xmlPullParser2, "presentationTimeOffset", singleSegmentBase2 != null ? singleSegmentBase2.presentationTimeOffset : 0);
        long j2 = singleSegmentBase2 != null ? singleSegmentBase2.indexStart : 0;
        if (singleSegmentBase2 != null) {
            j = singleSegmentBase2.indexLength;
        }
        String str = null;
        String attributeValue = xmlPullParser2.getAttributeValue(null, "indexRange");
        if (attributeValue != null) {
            String[] split = attributeValue.split("-");
            j = Long.parseLong(split[0]);
            parseLong = (Long.parseLong(split[1]) - j) + 1;
        } else {
            parseLong = j;
            j = j2;
        }
        if (singleSegmentBase2 != null) {
            str = singleSegmentBase2.initialization;
        }
        do {
            xmlPullParser.next();
            if (XmlPullParserUtil.isStartTag(xmlPullParser2, "Initialization")) {
                str = parseInitialization(xmlPullParser);
            }
        } while (!XmlPullParserUtil.isEndTag(xmlPullParser2, "SegmentBase"));
        return buildSingleSegmentBase(str, parseLong2, parseLong3, j, parseLong);
    }

    protected SingleSegmentBase buildSingleSegmentBase(RangedUri rangedUri, long j, long j2, long j3, long j4) {
        return new SingleSegmentBase(rangedUri, j, j2, j3, j4);
    }

    protected SegmentList parseSegmentList(XmlPullParser xmlPullParser, SegmentList segmentList) throws XmlPullParserException, IOException {
        XmlPullParser xmlPullParser2 = xmlPullParser;
        SegmentList segmentList2 = segmentList;
        long parseLong = parseLong(xmlPullParser2, "timescale", segmentList2 != null ? segmentList2.timescale : 1);
        long parseLong2 = parseLong(xmlPullParser2, "presentationTimeOffset", segmentList2 != null ? segmentList2.presentationTimeOffset : 0);
        long parseLong3 = parseLong(xmlPullParser2, "duration", segmentList2 != null ? segmentList2.duration : C0542C.TIME_UNSET);
        int parseInt = parseInt(xmlPullParser2, "startNumber", segmentList2 != null ? segmentList2.startNumber : 1);
        List list = null;
        RangedUri rangedUri = null;
        List list2 = rangedUri;
        do {
            xmlPullParser.next();
            if (XmlPullParserUtil.isStartTag(xmlPullParser2, "Initialization")) {
                rangedUri = parseInitialization(xmlPullParser);
            } else if (XmlPullParserUtil.isStartTag(xmlPullParser2, "SegmentTimeline")) {
                list2 = parseSegmentTimeline(xmlPullParser);
            } else if (XmlPullParserUtil.isStartTag(xmlPullParser2, "SegmentURL")) {
                if (list == null) {
                    list = new ArrayList();
                }
                list.add(parseSegmentUrl(xmlPullParser));
            }
        } while (!XmlPullParserUtil.isEndTag(xmlPullParser2, "SegmentList"));
        if (segmentList2 != null) {
            if (rangedUri == null) {
                rangedUri = segmentList2.initialization;
            }
            if (list2 == null) {
                list2 = segmentList2.segmentTimeline;
            }
            if (list == null) {
                list = segmentList2.mediaSegments;
            }
        }
        return buildSegmentList(rangedUri, parseLong, parseLong2, parseInt, parseLong3, list2, list);
    }

    protected SegmentList buildSegmentList(RangedUri rangedUri, long j, long j2, int i, long j3, List<SegmentTimelineElement> list, List<RangedUri> list2) {
        return new SegmentList(rangedUri, j, j2, i, j3, list, list2);
    }

    protected SegmentTemplate parseSegmentTemplate(XmlPullParser xmlPullParser, SegmentTemplate segmentTemplate) throws XmlPullParserException, IOException {
        DashManifestParser dashManifestParser = this;
        XmlPullParser xmlPullParser2 = xmlPullParser;
        SegmentTemplate segmentTemplate2 = segmentTemplate;
        long parseLong = parseLong(xmlPullParser2, "timescale", segmentTemplate2 != null ? segmentTemplate2.timescale : 1);
        long parseLong2 = parseLong(xmlPullParser2, "presentationTimeOffset", segmentTemplate2 != null ? segmentTemplate2.presentationTimeOffset : 0);
        long parseLong3 = parseLong(xmlPullParser2, "duration", segmentTemplate2 != null ? segmentTemplate2.duration : C0542C.TIME_UNSET);
        int parseInt = parseInt(xmlPullParser2, "startNumber", segmentTemplate2 != null ? segmentTemplate2.startNumber : 1);
        RangedUri rangedUri = null;
        UrlTemplate parseUrlTemplate = parseUrlTemplate(xmlPullParser2, "media", segmentTemplate2 != null ? segmentTemplate2.mediaTemplate : null);
        UrlTemplate parseUrlTemplate2 = parseUrlTemplate(xmlPullParser2, "initialization", segmentTemplate2 != null ? segmentTemplate2.initializationTemplate : null);
        List list = null;
        do {
            xmlPullParser.next();
            if (XmlPullParserUtil.isStartTag(xmlPullParser2, "Initialization")) {
                rangedUri = parseInitialization(xmlPullParser);
            } else if (XmlPullParserUtil.isStartTag(xmlPullParser2, "SegmentTimeline")) {
                list = parseSegmentTimeline(xmlPullParser);
            }
        } while (!XmlPullParserUtil.isEndTag(xmlPullParser2, "SegmentTemplate"));
        if (segmentTemplate2 != null) {
            if (rangedUri == null) {
                rangedUri = segmentTemplate2.initialization;
            }
            if (list == null) {
                list = segmentTemplate2.segmentTimeline;
            }
        }
        return buildSegmentTemplate(rangedUri, parseLong, parseLong2, parseInt, parseLong3, list, parseUrlTemplate2, parseUrlTemplate);
    }

    protected SegmentTemplate buildSegmentTemplate(RangedUri rangedUri, long j, long j2, int i, long j3, List<SegmentTimelineElement> list, UrlTemplate urlTemplate, UrlTemplate urlTemplate2) {
        return new SegmentTemplate(rangedUri, j, j2, i, j3, list, urlTemplate, urlTemplate2);
    }

    protected EventStream parseEventStream(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
        String parseString = parseString(xmlPullParser, "schemeIdUri", TtmlNode.ANONYMOUS_REGION_ID);
        String parseString2 = parseString(xmlPullParser, "value", TtmlNode.ANONYMOUS_REGION_ID);
        long parseLong = parseLong(xmlPullParser, "timescale", 1);
        List arrayList = new ArrayList();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(512);
        do {
            xmlPullParser.next();
            if (XmlPullParserUtil.isStartTag(xmlPullParser, "Event")) {
                arrayList.add(parseEvent(xmlPullParser, parseString, parseString2, parseLong, byteArrayOutputStream));
            }
        } while (!XmlPullParserUtil.isEndTag(xmlPullParser, "EventStream"));
        long[] jArr = new long[arrayList.size()];
        EventMessage[] eventMessageArr = new EventMessage[arrayList.size()];
        for (xmlPullParser = null; xmlPullParser < arrayList.size(); xmlPullParser++) {
            EventMessage eventMessage = (EventMessage) arrayList.get(xmlPullParser);
            jArr[xmlPullParser] = eventMessage.presentationTimeUs;
            eventMessageArr[xmlPullParser] = eventMessage;
        }
        return buildEventStream(parseString, parseString2, parseLong, jArr, eventMessageArr);
    }

    protected EventStream buildEventStream(String str, String str2, long j, long[] jArr, EventMessage[] eventMessageArr) {
        return new EventStream(str, str2, j, jArr, eventMessageArr);
    }

    protected EventMessage parseEvent(XmlPullParser xmlPullParser, String str, String str2, long j, ByteArrayOutputStream byteArrayOutputStream) throws IOException, XmlPullParserException {
        XmlPullParser xmlPullParser2 = xmlPullParser;
        long parseLong = parseLong(xmlPullParser2, TtmlNode.ATTR_ID, 0);
        long parseLong2 = parseLong(xmlPullParser2, "duration", C0542C.TIME_UNSET);
        long parseLong3 = parseLong(xmlPullParser2, "presentationTime", 0);
        return buildEvent(str, str2, parseLong, Util.scaleLargeTimestamp(parseLong2, 1000, j), parseEventObject(xmlPullParser2, byteArrayOutputStream), Util.scaleLargeTimestamp(parseLong3, C0542C.MICROS_PER_SECOND, j));
    }

    protected byte[] parseEventObject(XmlPullParser xmlPullParser, ByteArrayOutputStream byteArrayOutputStream) throws XmlPullParserException, IOException {
        byteArrayOutputStream.reset();
        XmlSerializer newSerializer = Xml.newSerializer();
        newSerializer.setOutput(byteArrayOutputStream, null);
        xmlPullParser.nextToken();
        while (!XmlPullParserUtil.isEndTag(xmlPullParser, "Event")) {
            int i = 0;
            switch (xmlPullParser.getEventType()) {
                case 0:
                    newSerializer.startDocument(null, Boolean.valueOf(false));
                    break;
                case 1:
                    newSerializer.endDocument();
                    break;
                case 2:
                    newSerializer.startTag(xmlPullParser.getNamespace(), xmlPullParser.getName());
                    while (i < xmlPullParser.getAttributeCount()) {
                        newSerializer.attribute(xmlPullParser.getAttributeNamespace(i), xmlPullParser.getAttributeName(i), xmlPullParser.getAttributeValue(i));
                        i++;
                    }
                    break;
                case 3:
                    newSerializer.endTag(xmlPullParser.getNamespace(), xmlPullParser.getName());
                    break;
                case 4:
                    newSerializer.text(xmlPullParser.getText());
                    break;
                case 5:
                    newSerializer.cdsect(xmlPullParser.getText());
                    break;
                case 6:
                    newSerializer.entityRef(xmlPullParser.getText());
                    break;
                case 7:
                    newSerializer.ignorableWhitespace(xmlPullParser.getText());
                    break;
                case 8:
                    newSerializer.processingInstruction(xmlPullParser.getText());
                    break;
                case 9:
                    newSerializer.comment(xmlPullParser.getText());
                    break;
                case 10:
                    newSerializer.docdecl(xmlPullParser.getText());
                    break;
                default:
                    break;
            }
            xmlPullParser.nextToken();
        }
        newSerializer.flush();
        return byteArrayOutputStream.toByteArray();
    }

    protected EventMessage buildEvent(String str, String str2, long j, long j2, byte[] bArr, long j3) {
        return new EventMessage(str, str2, j2, j, bArr, j3);
    }

    protected List<SegmentTimelineElement> parseSegmentTimeline(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
        List<SegmentTimelineElement> arrayList = new ArrayList();
        long j = 0;
        do {
            xmlPullParser.next();
            if (XmlPullParserUtil.isStartTag(xmlPullParser, "S")) {
                j = parseLong(xmlPullParser, "t", j);
                long parseLong = parseLong(xmlPullParser, "d", C0542C.TIME_UNSET);
                int i = 0;
                int parseInt = 1 + parseInt(xmlPullParser, "r", 0);
                while (i < parseInt) {
                    arrayList.add(buildSegmentTimelineElement(j, parseLong));
                    i++;
                    j += parseLong;
                }
            }
        } while (!XmlPullParserUtil.isEndTag(xmlPullParser, "SegmentTimeline"));
        return arrayList;
    }

    protected SegmentTimelineElement buildSegmentTimelineElement(long j, long j2) {
        return new SegmentTimelineElement(j, j2);
    }

    protected UrlTemplate parseUrlTemplate(XmlPullParser xmlPullParser, String str, UrlTemplate urlTemplate) {
        xmlPullParser = xmlPullParser.getAttributeValue(null, str);
        return xmlPullParser != null ? UrlTemplate.compile(xmlPullParser) : urlTemplate;
    }

    protected RangedUri parseInitialization(XmlPullParser xmlPullParser) {
        return parseRangedUrl(xmlPullParser, "sourceURL", "range");
    }

    protected RangedUri parseSegmentUrl(XmlPullParser xmlPullParser) {
        return parseRangedUrl(xmlPullParser, "media", "mediaRange");
    }

    protected RangedUri parseRangedUrl(XmlPullParser xmlPullParser, String str, String str2) {
        long parseLong;
        long parseLong2;
        String attributeValue = xmlPullParser.getAttributeValue(null, str);
        xmlPullParser = xmlPullParser.getAttributeValue(null, str2);
        if (xmlPullParser != null) {
            xmlPullParser = xmlPullParser.split("-");
            parseLong = Long.parseLong(xmlPullParser[0]);
            if (xmlPullParser.length == 2) {
                parseLong2 = (Long.parseLong(xmlPullParser[1]) - parseLong) + 1;
                return buildRangedUri(attributeValue, parseLong, parseLong2);
            }
        }
        parseLong = 0;
        parseLong2 = -1;
        return buildRangedUri(attributeValue, parseLong, parseLong2);
    }

    protected RangedUri buildRangedUri(String str, long j, long j2) {
        return new RangedUri(str, j, j2);
    }

    protected int parseAudioChannelConfiguration(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
        String parseString = parseString(xmlPullParser, "schemeIdUri", null);
        int i = -1;
        if ("urn:mpeg:dash:23003:3:audio_channel_configuration:2011".equals(parseString)) {
            i = parseInt(xmlPullParser, "value", -1);
        } else if ("tag:dolby.com,2014:dash:audio_channel_configuration:2011".equals(parseString)) {
            i = parseDolbyChannelConfiguration(xmlPullParser);
        }
        do {
            xmlPullParser.next();
        } while (!XmlPullParserUtil.isEndTag(xmlPullParser, "AudioChannelConfiguration"));
        return i;
    }

    private static void filterRedundantIncompleteSchemeDatas(ArrayList<SchemeData> arrayList) {
        for (int size = arrayList.size() - 1; size >= 0; size--) {
            SchemeData schemeData = (SchemeData) arrayList.get(size);
            if (!schemeData.hasData()) {
                for (int i = 0; i < arrayList.size(); i++) {
                    if (((SchemeData) arrayList.get(i)).canReplace(schemeData)) {
                        arrayList.remove(size);
                        break;
                    }
                }
            }
        }
    }

    private static String getSampleMimeType(String str, String str2) {
        if (MimeTypes.isAudio(str)) {
            return MimeTypes.getAudioMediaMimeType(str2);
        }
        if (MimeTypes.isVideo(str)) {
            return MimeTypes.getVideoMediaMimeType(str2);
        }
        if (mimeTypeIsRawText(str)) {
            return str;
        }
        if (!MimeTypes.APPLICATION_MP4.equals(str)) {
            if (!(MimeTypes.APPLICATION_RAWCC.equals(str) == null || str2 == null)) {
                if (str2.contains("cea708") != null) {
                    return MimeTypes.APPLICATION_CEA708;
                }
                if (!(str2.contains("eia608") == null && str2.contains("cea608") == null)) {
                    return MimeTypes.APPLICATION_CEA608;
                }
            }
            return null;
        } else if ("stpp".equals(str2) != null) {
            return MimeTypes.APPLICATION_TTML;
        } else {
            if ("wvtt".equals(str2) != null) {
                return MimeTypes.APPLICATION_MP4VTT;
            }
        }
        return null;
    }

    private static boolean mimeTypeIsRawText(String str) {
        if (!(MimeTypes.isText(str) || MimeTypes.APPLICATION_TTML.equals(str) || MimeTypes.APPLICATION_MP4VTT.equals(str) || MimeTypes.APPLICATION_CEA708.equals(str))) {
            if (MimeTypes.APPLICATION_CEA608.equals(str) == null) {
                return null;
            }
        }
        return true;
    }

    private static String checkLanguageConsistency(String str, String str2) {
        if (str == null) {
            return str2;
        }
        if (str2 == null) {
            return str;
        }
        Assertions.checkState(str.equals(str2));
        return str;
    }

    private static int checkContentTypeConsistency(int i, int i2) {
        if (i == -1) {
            return i2;
        }
        if (i2 == -1) {
            return i;
        }
        Assertions.checkState(i == i2 ? 1 : 0);
        return i;
    }

    protected static Descriptor parseDescriptor(XmlPullParser xmlPullParser, String str) throws XmlPullParserException, IOException {
        String parseString = parseString(xmlPullParser, "schemeIdUri", TtmlNode.ANONYMOUS_REGION_ID);
        String parseString2 = parseString(xmlPullParser, "value", null);
        String parseString3 = parseString(xmlPullParser, TtmlNode.ATTR_ID, null);
        do {
            xmlPullParser.next();
        } while (!XmlPullParserUtil.isEndTag(xmlPullParser, str));
        return new Descriptor(parseString, parseString2, parseString3);
    }

    protected static int parseCea608AccessibilityChannel(List<Descriptor> list) {
        for (int i = 0; i < list.size(); i++) {
            Descriptor descriptor = (Descriptor) list.get(i);
            if ("urn:scte:dash:cc:cea-608:2015".equals(descriptor.schemeIdUri) && descriptor.value != null) {
                Matcher matcher = CEA_608_ACCESSIBILITY_PATTERN.matcher(descriptor.value);
                if (matcher.matches()) {
                    return Integer.parseInt(matcher.group(1));
                }
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Unable to parse CEA-608 channel number from: ");
                stringBuilder.append(descriptor.value);
                Log.w(str, stringBuilder.toString());
            }
        }
        return -1;
    }

    protected static int parseCea708AccessibilityChannel(List<Descriptor> list) {
        for (int i = 0; i < list.size(); i++) {
            Descriptor descriptor = (Descriptor) list.get(i);
            if ("urn:scte:dash:cc:cea-708:2015".equals(descriptor.schemeIdUri) && descriptor.value != null) {
                Matcher matcher = CEA_708_ACCESSIBILITY_PATTERN.matcher(descriptor.value);
                if (matcher.matches()) {
                    return Integer.parseInt(matcher.group(1));
                }
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Unable to parse CEA-708 service block number from: ");
                stringBuilder.append(descriptor.value);
                Log.w(str, stringBuilder.toString());
            }
        }
        return -1;
    }

    protected static String parseEac3SupplementalProperties(List<Descriptor> list) {
        for (int i = 0; i < list.size(); i++) {
            Descriptor descriptor = (Descriptor) list.get(i);
            if ("tag:dolby.com,2014:dash:DolbyDigitalPlusExtensionType:2014".equals(descriptor.schemeIdUri) && "ec+3".equals(descriptor.value)) {
                return MimeTypes.AUDIO_E_AC3_JOC;
            }
        }
        return MimeTypes.AUDIO_E_AC3;
    }

    protected static float parseFrameRate(XmlPullParser xmlPullParser, float f) {
        xmlPullParser = xmlPullParser.getAttributeValue(null, "frameRate");
        if (xmlPullParser == null) {
            return f;
        }
        xmlPullParser = FRAME_RATE_PATTERN.matcher(xmlPullParser);
        if (!xmlPullParser.matches()) {
            return f;
        }
        f = Integer.parseInt(xmlPullParser.group(Float.MIN_VALUE));
        xmlPullParser = xmlPullParser.group(2);
        return !TextUtils.isEmpty(xmlPullParser) ? ((float) f) / ((float) Integer.parseInt(xmlPullParser)) : (float) f;
    }

    protected static long parseDuration(XmlPullParser xmlPullParser, String str, long j) {
        xmlPullParser = xmlPullParser.getAttributeValue(null, str);
        if (xmlPullParser == null) {
            return j;
        }
        return Util.parseXsDuration(xmlPullParser);
    }

    protected static long parseDateTime(XmlPullParser xmlPullParser, String str, long j) throws ParserException {
        xmlPullParser = xmlPullParser.getAttributeValue(null, str);
        if (xmlPullParser == null) {
            return j;
        }
        return Util.parseXsDateTime(xmlPullParser);
    }

    protected static String parseBaseUrl(XmlPullParser xmlPullParser, String str) throws XmlPullParserException, IOException {
        xmlPullParser.next();
        return UriUtil.resolve(str, xmlPullParser.getText());
    }

    protected static int parseInt(XmlPullParser xmlPullParser, String str, int i) {
        xmlPullParser = xmlPullParser.getAttributeValue(null, str);
        return xmlPullParser == null ? i : Integer.parseInt(xmlPullParser);
    }

    protected static long parseLong(XmlPullParser xmlPullParser, String str, long j) {
        xmlPullParser = xmlPullParser.getAttributeValue(null, str);
        return xmlPullParser == null ? j : Long.parseLong(xmlPullParser);
    }

    protected static String parseString(XmlPullParser xmlPullParser, String str, String str2) {
        xmlPullParser = xmlPullParser.getAttributeValue(null, str);
        return xmlPullParser == null ? str2 : xmlPullParser;
    }

    protected static int parseDolbyChannelConfiguration(XmlPullParser xmlPullParser) {
        xmlPullParser = Util.toLowerInvariant(xmlPullParser.getAttributeValue(null, "value"));
        if (xmlPullParser == null) {
            return -1;
        }
        int hashCode = xmlPullParser.hashCode();
        if (hashCode != 1596796) {
            if (hashCode != 2937391) {
                if (hashCode != 3094035) {
                    if (hashCode == 3133436) {
                        if (xmlPullParser.equals("fa01") != null) {
                            xmlPullParser = 3;
                            switch (xmlPullParser) {
                                case null:
                                    return 1;
                                case 1:
                                    return 2;
                                case 2:
                                    return 6;
                                case 3:
                                    return 8;
                                default:
                                    return -1;
                            }
                        }
                    }
                } else if (xmlPullParser.equals("f801") != null) {
                    xmlPullParser = 2;
                    switch (xmlPullParser) {
                        case null:
                            return 1;
                        case 1:
                            return 2;
                        case 2:
                            return 6;
                        case 3:
                            return 8;
                        default:
                            return -1;
                    }
                }
            } else if (xmlPullParser.equals("a000") != null) {
                xmlPullParser = 1;
                switch (xmlPullParser) {
                    case null:
                        return 1;
                    case 1:
                        return 2;
                    case 2:
                        return 6;
                    case 3:
                        return 8;
                    default:
                        return -1;
                }
            }
        } else if (xmlPullParser.equals("4000") != null) {
            xmlPullParser = null;
            switch (xmlPullParser) {
                case null:
                    return 1;
                case 1:
                    return 2;
                case 2:
                    return 6;
                case 3:
                    return 8;
                default:
                    return -1;
            }
        }
        xmlPullParser = -1;
        switch (xmlPullParser) {
            case null:
                return 1;
            case 1:
                return 2;
            case 2:
                return 6;
            case 3:
                return 8;
            default:
                return -1;
        }
    }
}
