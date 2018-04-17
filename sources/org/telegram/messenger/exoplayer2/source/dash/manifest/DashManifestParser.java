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
import org.telegram.messenger.exoplayer2.C0539C;
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

        public RepresentationInfo(Format format, String baseUrl, SegmentBase segmentBase, String drmSchemeType, ArrayList<SchemeData> drmSchemeDatas, ArrayList<Descriptor> inbandEventStreams, long revisionId) {
            this.format = format;
            this.baseUrl = baseUrl;
            this.segmentBase = segmentBase;
            this.drmSchemeType = drmSchemeType;
            this.drmSchemeDatas = drmSchemeDatas;
            this.inbandEventStreams = inbandEventStreams;
            this.revisionId = revisionId;
        }
    }

    public DashManifestParser() {
        this(null);
    }

    public DashManifestParser(String contentId) {
        this.contentId = contentId;
        try {
            this.xmlParserFactory = XmlPullParserFactory.newInstance();
        } catch (XmlPullParserException e) {
            throw new RuntimeException("Couldn't create XmlPullParserFactory instance", e);
        }
    }

    public DashManifest parse(Uri uri, InputStream inputStream) throws IOException {
        try {
            XmlPullParser xpp = this.xmlParserFactory.newPullParser();
            xpp.setInput(inputStream, null);
            if (xpp.next() == 2) {
                if ("MPD".equals(xpp.getName())) {
                    return parseMediaPresentationDescription(xpp, uri.toString());
                }
            }
            throw new ParserException("inputStream does not contain a valid media presentation description");
        } catch (Throwable e) {
            throw new ParserException(e);
        }
    }

    protected DashManifest parseMediaPresentationDescription(XmlPullParser xpp, String baseUrl) throws XmlPullParserException, IOException {
        DashManifestParser dashManifestParser;
        XmlPullParser xmlPullParser = xpp;
        long availabilityStartTime = parseDateTime(xmlPullParser, "availabilityStartTime", C0539C.TIME_UNSET);
        long durationMs = parseDuration(xmlPullParser, "mediaPresentationDuration", C0539C.TIME_UNSET);
        long minBufferTimeMs = parseDuration(xmlPullParser, "minBufferTime", C0539C.TIME_UNSET);
        String typeString = xmlPullParser.getAttributeValue(null, "type");
        boolean seenFirstBaseUrl = false;
        boolean z = typeString != null && typeString.equals("dynamic");
        boolean dynamic = z;
        long minUpdateTimeMs = dynamic ? parseDuration(xmlPullParser, "minimumUpdatePeriod", C0539C.TIME_UNSET) : C0539C.TIME_UNSET;
        long timeShiftBufferDepthMs = dynamic ? parseDuration(xmlPullParser, "timeShiftBufferDepth", C0539C.TIME_UNSET) : C0539C.TIME_UNSET;
        long suggestedPresentationDelayMs = dynamic ? parseDuration(xmlPullParser, "suggestedPresentationDelay", C0539C.TIME_UNSET) : C0539C.TIME_UNSET;
        long publishTimeMs = parseDateTime(xmlPullParser, "publishTime", C0539C.TIME_UNSET);
        List<Period> periods = new ArrayList();
        boolean seenEarlyAccessPeriod = false;
        long nextPeriodStartMs = dynamic ? C0539C.TIME_UNSET : 0;
        Uri location = null;
        UtcTimingElement utcTiming = null;
        String baseUrl2 = baseUrl;
        while (true) {
            String typeString2;
            boolean seenFirstBaseUrl2;
            String baseUrl3;
            UtcTimingElement utcTimingElement;
            Uri uri;
            boolean seenEarlyAccessPeriod2;
            xpp.next();
            long nextPeriodStartMs2;
            if (!XmlPullParserUtil.isStartTag(xmlPullParser, "BaseURL")) {
                if (!XmlPullParserUtil.isStartTag(xmlPullParser, "UTCTiming")) {
                    if (!XmlPullParserUtil.isStartTag(xmlPullParser, "Location")) {
                        if (XmlPullParserUtil.isStartTag(xmlPullParser, "Period") && !seenEarlyAccessPeriod) {
                            typeString2 = typeString;
                            typeString = parsePeriod(xmlPullParser, baseUrl2, nextPeriodStartMs);
                            nextPeriodStartMs2 = nextPeriodStartMs;
                            Period period = typeString.first;
                            seenFirstBaseUrl2 = seenFirstBaseUrl;
                            baseUrl3 = baseUrl2;
                            if (period.startMs != C0539C.TIME_UNSET) {
                                long j;
                                seenFirstBaseUrl = ((Long) typeString.second).longValue();
                                if (seenFirstBaseUrl == C0539C.TIME_UNSET) {
                                    utcTimingElement = utcTiming;
                                    uri = location;
                                    j = C0539C.TIME_UNSET;
                                } else {
                                    utcTimingElement = utcTiming;
                                    uri = location;
                                    j = period.startMs + seenFirstBaseUrl;
                                }
                                utcTiming = j;
                                periods.add(period);
                                nextPeriodStartMs = utcTiming;
                                seenEarlyAccessPeriod2 = seenEarlyAccessPeriod;
                                if (XmlPullParserUtil.isEndTag(xmlPullParser, "MPD")) {
                                    break;
                                }
                                seenEarlyAccessPeriod = seenEarlyAccessPeriod2;
                                typeString = typeString2;
                                baseUrl2 = baseUrl3;
                                seenFirstBaseUrl = seenFirstBaseUrl2;
                                utcTiming = utcTimingElement;
                                location = uri;
                            } else if (dynamic) {
                                seenEarlyAccessPeriod2 = true;
                                utcTimingElement = utcTiming;
                                uri = location;
                                nextPeriodStartMs = nextPeriodStartMs2;
                                if (XmlPullParserUtil.isEndTag(xmlPullParser, "MPD")) {
                                    break;
                                }
                                seenEarlyAccessPeriod = seenEarlyAccessPeriod2;
                                typeString = typeString2;
                                baseUrl2 = baseUrl3;
                                seenFirstBaseUrl = seenFirstBaseUrl2;
                                utcTiming = utcTimingElement;
                                location = uri;
                            } else {
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append("Unable to determine start of period ");
                                stringBuilder.append(periods.size());
                                throw new ParserException(stringBuilder.toString());
                            }
                        }
                        dashManifestParser = this;
                        typeString2 = typeString;
                        nextPeriodStartMs2 = nextPeriodStartMs;
                        seenFirstBaseUrl2 = seenFirstBaseUrl;
                        baseUrl3 = baseUrl2;
                        utcTimingElement = utcTiming;
                        uri = location;
                        seenEarlyAccessPeriod2 = seenEarlyAccessPeriod;
                        nextPeriodStartMs = nextPeriodStartMs2;
                        if (XmlPullParserUtil.isEndTag(xmlPullParser, "MPD")) {
                            break;
                        }
                        seenEarlyAccessPeriod = seenEarlyAccessPeriod2;
                        typeString = typeString2;
                        baseUrl2 = baseUrl3;
                        seenFirstBaseUrl = seenFirstBaseUrl2;
                        utcTiming = utcTimingElement;
                        location = uri;
                    } else {
                        location = Uri.parse(xpp.nextText());
                    }
                } else {
                    utcTiming = parseUtcTiming(xpp);
                }
            } else if (seenFirstBaseUrl) {
                dashManifestParser = this;
                typeString2 = typeString;
                nextPeriodStartMs2 = nextPeriodStartMs;
                seenFirstBaseUrl2 = seenFirstBaseUrl;
                baseUrl3 = baseUrl2;
                utcTimingElement = utcTiming;
                uri = location;
                seenEarlyAccessPeriod2 = seenEarlyAccessPeriod;
                nextPeriodStartMs = nextPeriodStartMs2;
                if (XmlPullParserUtil.isEndTag(xmlPullParser, "MPD")) {
                    break;
                }
                seenEarlyAccessPeriod = seenEarlyAccessPeriod2;
                typeString = typeString2;
                baseUrl2 = baseUrl3;
                seenFirstBaseUrl = seenFirstBaseUrl2;
                utcTiming = utcTimingElement;
                location = uri;
            } else {
                baseUrl2 = parseBaseUrl(xmlPullParser, baseUrl2);
                seenFirstBaseUrl = true;
            }
            dashManifestParser = this;
            typeString2 = typeString;
            seenFirstBaseUrl2 = seenFirstBaseUrl;
            baseUrl3 = baseUrl2;
            utcTimingElement = utcTiming;
            uri = location;
            seenEarlyAccessPeriod2 = seenEarlyAccessPeriod;
            if (XmlPullParserUtil.isEndTag(xmlPullParser, "MPD")) {
                break;
            }
            seenEarlyAccessPeriod = seenEarlyAccessPeriod2;
            typeString = typeString2;
            baseUrl2 = baseUrl3;
            seenFirstBaseUrl = seenFirstBaseUrl2;
            utcTiming = utcTimingElement;
            location = uri;
        }
        if (durationMs == C0539C.TIME_UNSET) {
            if (nextPeriodStartMs != C0539C.TIME_UNSET) {
                durationMs = nextPeriodStartMs;
            } else if (!dynamic) {
                throw new ParserException("Unable to determine duration of static manifest.");
            }
        }
        long durationMs2 = durationMs;
        if (periods.isEmpty()) {
            throw new ParserException("No periods found.");
        }
        return dashManifestParser.buildMediaPresentationDescription(availabilityStartTime, durationMs2, minBufferTimeMs, dynamic, minUpdateTimeMs, timeShiftBufferDepthMs, suggestedPresentationDelayMs, publishTimeMs, utcTimingElement, uri, periods);
    }

    protected DashManifest buildMediaPresentationDescription(long availabilityStartTime, long durationMs, long minBufferTimeMs, boolean dynamic, long minUpdateTimeMs, long timeShiftBufferDepthMs, long suggestedPresentationDelayMs, long publishTimeMs, UtcTimingElement utcTiming, Uri location, List<Period> periods) {
        return new DashManifest(availabilityStartTime, durationMs, minBufferTimeMs, dynamic, minUpdateTimeMs, timeShiftBufferDepthMs, suggestedPresentationDelayMs, publishTimeMs, utcTiming, location, periods);
    }

    protected UtcTimingElement parseUtcTiming(XmlPullParser xpp) {
        return buildUtcTimingElement(xpp.getAttributeValue(null, "schemeIdUri"), xpp.getAttributeValue(null, "value"));
    }

    protected UtcTimingElement buildUtcTimingElement(String schemeIdUri, String value) {
        return new UtcTimingElement(schemeIdUri, value);
    }

    protected Pair<Period, Long> parsePeriod(XmlPullParser xpp, String baseUrl, long defaultStartMs) throws XmlPullParserException, IOException {
        DashManifestParser dashManifestParser = this;
        XmlPullParser xmlPullParser = xpp;
        String id = xmlPullParser.getAttributeValue(null, TtmlNode.ATTR_ID);
        long startMs = parseDuration(xmlPullParser, TtmlNode.START, defaultStartMs);
        long durationMs = parseDuration(xmlPullParser, "duration", C0539C.TIME_UNSET);
        List adaptationSets = new ArrayList();
        List<EventStream> eventStreams = new ArrayList();
        boolean seenFirstBaseUrl = false;
        SegmentBase segmentBase = null;
        String baseUrl2 = baseUrl;
        while (true) {
            xpp.next();
            if (XmlPullParserUtil.isStartTag(xmlPullParser, "BaseURL")) {
                if (!seenFirstBaseUrl) {
                    baseUrl2 = parseBaseUrl(xmlPullParser, baseUrl2);
                    seenFirstBaseUrl = true;
                }
            } else if (XmlPullParserUtil.isStartTag(xmlPullParser, "AdaptationSet")) {
                adaptationSets.add(parseAdaptationSet(xmlPullParser, baseUrl2, segmentBase));
            } else if (XmlPullParserUtil.isStartTag(xmlPullParser, "EventStream")) {
                eventStreams.add(parseEventStream(xpp));
            } else if (XmlPullParserUtil.isStartTag(xmlPullParser, "SegmentBase")) {
                segmentBase = parseSegmentBase(xmlPullParser, null);
            } else if (XmlPullParserUtil.isStartTag(xmlPullParser, "SegmentList")) {
                segmentBase = parseSegmentList(xmlPullParser, null);
            } else if (XmlPullParserUtil.isStartTag(xmlPullParser, "SegmentTemplate")) {
                segmentBase = parseSegmentTemplate(xmlPullParser, null);
            }
            String baseUrl3 = baseUrl2;
            boolean seenFirstBaseUrl2 = seenFirstBaseUrl;
            SegmentBase segmentBase2 = segmentBase;
            if (XmlPullParserUtil.isEndTag(xmlPullParser, "Period")) {
                return Pair.create(buildPeriod(id, startMs, adaptationSets, eventStreams), Long.valueOf(durationMs));
            }
            baseUrl2 = baseUrl3;
            seenFirstBaseUrl = seenFirstBaseUrl2;
            segmentBase = segmentBase2;
        }
    }

    protected Period buildPeriod(String id, long startMs, List<AdaptationSet> adaptationSets, List<EventStream> eventStreams) {
        return new Period(id, startMs, adaptationSets, eventStreams);
    }

    protected AdaptationSet parseAdaptationSet(XmlPullParser xpp, String baseUrl, SegmentBase segmentBase) throws XmlPullParserException, IOException {
        List<RepresentationInfo> representationInfos;
        DashManifestParser dashManifestParser = this;
        XmlPullParser xmlPullParser = xpp;
        int id = parseInt(xmlPullParser, TtmlNode.ATTR_ID, -1);
        int contentType = parseContentType(xpp);
        String str = null;
        String mimeType = xmlPullParser.getAttributeValue(null, "mimeType");
        String codecs = xmlPullParser.getAttributeValue(null, "codecs");
        int width = parseInt(xmlPullParser, "width", -1);
        int height = parseInt(xmlPullParser, "height", -1);
        float frameRate = parseFrameRate(xmlPullParser, -1.0f);
        int audioSamplingRate = parseInt(xmlPullParser, "audioSamplingRate", -1);
        String language = xmlPullParser.getAttributeValue(null, "lang");
        ArrayList<SchemeData> drmSchemeDatas = new ArrayList();
        ArrayList<Descriptor> inbandEventStreams = new ArrayList();
        ArrayList<Descriptor> accessibilityDescriptors = new ArrayList();
        int contentType2 = new ArrayList();
        ArrayList<Descriptor> inbandEventStreams2 = new ArrayList();
        int i = 0;
        String baseUrl2 = baseUrl;
        SegmentBase segmentBase2 = segmentBase;
        int contentType3 = contentType;
        String language2 = language;
        int audioChannels = -1;
        String drmSchemeType = null;
        int selectionFlags = 0;
        boolean seenFirstBaseUrl = false;
        while (true) {
            String baseUrl3;
            String language3;
            ArrayList<Descriptor> supplementalProperties;
            ArrayList<Descriptor> accessibilityDescriptors2;
            ArrayList<SchemeData> drmSchemeDatas2;
            String str2;
            XmlPullParser xmlPullParser2;
            List inbandEventStreams3;
            boolean seenFirstBaseUrl2 = seenFirstBaseUrl;
            xpp.next();
            int contentType4;
            if (XmlPullParserUtil.isStartTag(xmlPullParser, "BaseURL")) {
                if (!seenFirstBaseUrl2) {
                    baseUrl3 = parseBaseUrl(xmlPullParser, baseUrl2);
                    seenFirstBaseUrl2 = true;
                    language3 = language2;
                }
                contentType4 = contentType3;
                language3 = language2;
                baseUrl3 = baseUrl2;
                representationInfos = inbandEventStreams2;
                supplementalProperties = contentType2;
                accessibilityDescriptors2 = accessibilityDescriptors;
                inbandEventStreams2 = inbandEventStreams;
                drmSchemeDatas2 = drmSchemeDatas;
                str2 = str;
                xmlPullParser2 = xmlPullParser;
                contentType2 = contentType4;
                if (!XmlPullParserUtil.isEndTag(xmlPullParser2, "AdaptationSet")) {
                    break;
                }
                xmlPullParser = xmlPullParser2;
                inbandEventStreams = inbandEventStreams2;
                contentType3 = contentType2;
                seenFirstBaseUrl = seenFirstBaseUrl2;
                baseUrl2 = baseUrl3;
                contentType2 = supplementalProperties;
                accessibilityDescriptors = accessibilityDescriptors2;
                drmSchemeDatas = drmSchemeDatas2;
                str = str2;
                inbandEventStreams3 = representationInfos;
                language2 = language3;
            } else {
                if (!XmlPullParserUtil.isStartTag(xmlPullParser, "ContentProtection")) {
                    if (XmlPullParserUtil.isStartTag(xmlPullParser, "ContentComponent")) {
                        language3 = checkLanguageConsistency(language2, xmlPullParser.getAttributeValue(str, "lang"));
                        baseUrl3 = baseUrl2;
                        representationInfos = inbandEventStreams2;
                        supplementalProperties = contentType2;
                        accessibilityDescriptors2 = accessibilityDescriptors;
                        inbandEventStreams2 = inbandEventStreams;
                        drmSchemeDatas2 = drmSchemeDatas;
                        str2 = str;
                        xmlPullParser2 = xmlPullParser;
                        contentType2 = checkContentTypeConsistency(contentType3, parseContentType(xpp));
                    } else if (XmlPullParserUtil.isStartTag(xmlPullParser, "Role")) {
                        selectionFlags |= parseRole(xpp);
                    } else if (XmlPullParserUtil.isStartTag(xmlPullParser, "AudioChannelConfiguration")) {
                        audioChannels = parseAudioChannelConfiguration(xpp);
                    } else {
                        if (XmlPullParserUtil.isStartTag(xmlPullParser, "Accessibility")) {
                            accessibilityDescriptors.add(parseDescriptor(xmlPullParser, "Accessibility"));
                        } else if (XmlPullParserUtil.isStartTag(xmlPullParser, "SupplementalProperty")) {
                            contentType2.add(parseDescriptor(xmlPullParser, "SupplementalProperty"));
                        } else if (XmlPullParserUtil.isStartTag(xmlPullParser, "Representation")) {
                            int contentType5 = contentType3;
                            language3 = language2;
                            baseUrl3 = baseUrl2;
                            List<RepresentationInfo> representationInfos2 = inbandEventStreams2;
                            supplementalProperties = contentType2;
                            accessibilityDescriptors2 = accessibilityDescriptors;
                            inbandEventStreams = inbandEventStreams;
                            drmSchemeDatas2 = drmSchemeDatas;
                            str2 = str;
                            RepresentationInfo representationInfo = parseRepresentation(xmlPullParser, baseUrl2, mimeType, codecs, width, height, frameRate, audioChannels, audioSamplingRate, language3, selectionFlags, accessibilityDescriptors2, segmentBase2);
                            contentType4 = checkContentTypeConsistency(contentType5, getContentType(representationInfo.format));
                            representationInfos = representationInfos2;
                            representationInfos.add(representationInfo);
                            contentType2 = contentType4;
                            inbandEventStreams2 = inbandEventStreams;
                            xmlPullParser2 = xpp;
                        } else {
                            SegmentBase segmentBase3;
                            language3 = language2;
                            baseUrl3 = baseUrl2;
                            representationInfos = inbandEventStreams2;
                            supplementalProperties = contentType2;
                            accessibilityDescriptors2 = accessibilityDescriptors;
                            inbandEventStreams = inbandEventStreams;
                            drmSchemeDatas2 = drmSchemeDatas;
                            str2 = str;
                            contentType4 = contentType3;
                            xmlPullParser2 = xpp;
                            if (XmlPullParserUtil.isStartTag(xmlPullParser2, "SegmentBase")) {
                                segmentBase3 = parseSegmentBase(xmlPullParser2, (SingleSegmentBase) segmentBase2);
                            } else if (XmlPullParserUtil.isStartTag(xmlPullParser2, "SegmentList")) {
                                segmentBase3 = parseSegmentList(xmlPullParser2, (SegmentList) segmentBase2);
                            } else if (XmlPullParserUtil.isStartTag(xmlPullParser2, "SegmentTemplate")) {
                                segmentBase3 = parseSegmentTemplate(xmlPullParser2, (SegmentTemplate) segmentBase2);
                            } else {
                                if (XmlPullParserUtil.isStartTag(xmlPullParser2, "InbandEventStream")) {
                                    inbandEventStreams2 = inbandEventStreams;
                                    inbandEventStreams2.add(parseDescriptor(xmlPullParser2, "InbandEventStream"));
                                } else {
                                    inbandEventStreams2 = inbandEventStreams;
                                    if (XmlPullParserUtil.isStartTag(xpp)) {
                                        parseAdaptationSetChild(xpp);
                                    }
                                }
                                contentType2 = contentType4;
                            }
                            segmentBase2 = segmentBase3;
                            contentType2 = contentType4;
                            inbandEventStreams2 = inbandEventStreams;
                        }
                        contentType4 = contentType3;
                        language3 = language2;
                        baseUrl3 = baseUrl2;
                        representationInfos = inbandEventStreams2;
                        supplementalProperties = contentType2;
                        accessibilityDescriptors2 = accessibilityDescriptors;
                        inbandEventStreams2 = inbandEventStreams;
                        drmSchemeDatas2 = drmSchemeDatas;
                        str2 = str;
                        xmlPullParser2 = xmlPullParser;
                        contentType2 = contentType4;
                    }
                    if (!XmlPullParserUtil.isEndTag(xmlPullParser2, "AdaptationSet")) {
                        break;
                    }
                    xmlPullParser = xmlPullParser2;
                    inbandEventStreams = inbandEventStreams2;
                    contentType3 = contentType2;
                    seenFirstBaseUrl = seenFirstBaseUrl2;
                    baseUrl2 = baseUrl3;
                    contentType2 = supplementalProperties;
                    accessibilityDescriptors = accessibilityDescriptors2;
                    drmSchemeDatas = drmSchemeDatas2;
                    str = str2;
                    inbandEventStreams3 = representationInfos;
                    language2 = language3;
                } else {
                    Pair<String, SchemeData> contentProtection = parseContentProtection(xpp);
                    if (contentProtection.first != null) {
                        drmSchemeType = contentProtection.first;
                    }
                    if (contentProtection.second != null) {
                        drmSchemeDatas.add(contentProtection.second);
                    }
                }
                language3 = language2;
                baseUrl3 = baseUrl2;
            }
            representationInfos = inbandEventStreams2;
            supplementalProperties = contentType2;
            accessibilityDescriptors2 = accessibilityDescriptors;
            inbandEventStreams2 = inbandEventStreams;
            drmSchemeDatas2 = drmSchemeDatas;
            str2 = str;
            xmlPullParser2 = xmlPullParser;
            contentType2 = contentType3;
            if (!XmlPullParserUtil.isEndTag(xmlPullParser2, "AdaptationSet")) {
                break;
            }
            xmlPullParser = xmlPullParser2;
            inbandEventStreams = inbandEventStreams2;
            contentType3 = contentType2;
            seenFirstBaseUrl = seenFirstBaseUrl2;
            baseUrl2 = baseUrl3;
            contentType2 = supplementalProperties;
            accessibilityDescriptors = accessibilityDescriptors2;
            drmSchemeDatas = drmSchemeDatas2;
            str = str2;
            inbandEventStreams3 = representationInfos;
            language2 = language3;
        }
        List representations = new ArrayList(representationInfos.size());
        while (true) {
            int i2 = i;
            if (i2 >= representationInfos.size()) {
                return buildAdaptationSet(id, contentType2, representations, accessibilityDescriptors2, supplementalProperties);
            }
            representations.add(buildRepresentation((RepresentationInfo) representationInfos.get(i2), dashManifestParser.contentId, drmSchemeType, drmSchemeDatas2, inbandEventStreams2));
            i = i2 + 1;
        }
    }

    protected AdaptationSet buildAdaptationSet(int id, int contentType, List<Representation> representations, List<Descriptor> accessibilityDescriptors, List<Descriptor> supplementalProperties) {
        return new AdaptationSet(id, contentType, representations, accessibilityDescriptors, supplementalProperties);
    }

    protected int parseContentType(XmlPullParser xpp) {
        String contentType = xpp.getAttributeValue(null, "contentType");
        if (TextUtils.isEmpty(contentType)) {
            return -1;
        }
        if (MimeTypes.BASE_TYPE_AUDIO.equals(contentType)) {
            return 1;
        }
        if (MimeTypes.BASE_TYPE_VIDEO.equals(contentType)) {
            return 2;
        }
        if (MimeTypes.BASE_TYPE_TEXT.equals(contentType)) {
            return 3;
        }
        return -1;
    }

    protected int getContentType(Format format) {
        String sampleMimeType = format.sampleMimeType;
        if (TextUtils.isEmpty(sampleMimeType)) {
            return -1;
        }
        if (MimeTypes.isVideo(sampleMimeType)) {
            return 2;
        }
        if (MimeTypes.isAudio(sampleMimeType)) {
            return 1;
        }
        if (mimeTypeIsRawText(sampleMimeType)) {
            return 3;
        }
        return -1;
    }

    protected Pair<String, SchemeData> parseContentProtection(XmlPullParser xpp) throws XmlPullParserException, IOException {
        String toLowerInvariant;
        String schemeType = null;
        byte[] data = null;
        UUID uuid = null;
        boolean requiresSecureDecoder = false;
        SchemeData schemeData = null;
        String schemeIdUri = xpp.getAttributeValue(null, "schemeIdUri");
        if (schemeIdUri != null) {
            int i;
            String[] defaultKidStrings;
            UUID[] defaultKids;
            int i2;
            toLowerInvariant = Util.toLowerInvariant(schemeIdUri);
            int hashCode = toLowerInvariant.hashCode();
            if (hashCode == 489446379) {
                if (toLowerInvariant.equals("urn:uuid:9a04f079-9840-4286-ab92-e65be0885f95")) {
                    i = 1;
                    switch (i) {
                        case 0:
                            schemeType = xpp.getAttributeValue(null, "value");
                            toLowerInvariant = xpp.getAttributeValue(null, "cenc:default_KID");
                            defaultKidStrings = toLowerInvariant.split("\\s+");
                            defaultKids = new UUID[defaultKidStrings.length];
                            for (i2 = 0; i2 < defaultKidStrings.length; i2++) {
                                defaultKids[i2] = UUID.fromString(defaultKidStrings[i2]);
                            }
                            data = PsshAtomUtil.buildPsshAtom(C0539C.COMMON_PSSH_UUID, defaultKids, null);
                            uuid = C0539C.COMMON_PSSH_UUID;
                            break;
                        case 1:
                            uuid = C0539C.PLAYREADY_UUID;
                            break;
                        case 2:
                            uuid = C0539C.WIDEVINE_UUID;
                            break;
                        default:
                            break;
                    }
                }
            } else if (hashCode == 755418770) {
                if (toLowerInvariant.equals("urn:uuid:edef8ba9-79d6-4ace-a3c8-27dcd51d21ed")) {
                    i = 2;
                    switch (i) {
                        case 0:
                            schemeType = xpp.getAttributeValue(null, "value");
                            toLowerInvariant = xpp.getAttributeValue(null, "cenc:default_KID");
                            defaultKidStrings = toLowerInvariant.split("\\s+");
                            defaultKids = new UUID[defaultKidStrings.length];
                            for (i2 = 0; i2 < defaultKidStrings.length; i2++) {
                                defaultKids[i2] = UUID.fromString(defaultKidStrings[i2]);
                            }
                            data = PsshAtomUtil.buildPsshAtom(C0539C.COMMON_PSSH_UUID, defaultKids, null);
                            uuid = C0539C.COMMON_PSSH_UUID;
                            break;
                        case 1:
                            uuid = C0539C.PLAYREADY_UUID;
                            break;
                        case 2:
                            uuid = C0539C.WIDEVINE_UUID;
                            break;
                        default:
                            break;
                    }
                }
            } else if (hashCode == NUM) {
                if (toLowerInvariant.equals("urn:mpeg:dash:mp4protection:2011")) {
                    i = 0;
                    switch (i) {
                        case 0:
                            schemeType = xpp.getAttributeValue(null, "value");
                            toLowerInvariant = xpp.getAttributeValue(null, "cenc:default_KID");
                            if (!(TextUtils.isEmpty(toLowerInvariant) || "00000000-0000-0000-0000-000000000000".equals(toLowerInvariant))) {
                                defaultKidStrings = toLowerInvariant.split("\\s+");
                                defaultKids = new UUID[defaultKidStrings.length];
                                for (i2 = 0; i2 < defaultKidStrings.length; i2++) {
                                    defaultKids[i2] = UUID.fromString(defaultKidStrings[i2]);
                                }
                                data = PsshAtomUtil.buildPsshAtom(C0539C.COMMON_PSSH_UUID, defaultKids, null);
                                uuid = C0539C.COMMON_PSSH_UUID;
                                break;
                            }
                        case 1:
                            uuid = C0539C.PLAYREADY_UUID;
                            break;
                        case 2:
                            uuid = C0539C.WIDEVINE_UUID;
                            break;
                        default:
                            break;
                    }
                }
            }
            i = -1;
            switch (i) {
                case 0:
                    schemeType = xpp.getAttributeValue(null, "value");
                    toLowerInvariant = xpp.getAttributeValue(null, "cenc:default_KID");
                    defaultKidStrings = toLowerInvariant.split("\\s+");
                    defaultKids = new UUID[defaultKidStrings.length];
                    for (i2 = 0; i2 < defaultKidStrings.length; i2++) {
                        defaultKids[i2] = UUID.fromString(defaultKidStrings[i2]);
                    }
                    data = PsshAtomUtil.buildPsshAtom(C0539C.COMMON_PSSH_UUID, defaultKids, null);
                    uuid = C0539C.COMMON_PSSH_UUID;
                    break;
                case 1:
                    uuid = C0539C.PLAYREADY_UUID;
                    break;
                case 2:
                    uuid = C0539C.WIDEVINE_UUID;
                    break;
                default:
                    break;
            }
        }
        do {
            xpp.next();
            if (XmlPullParserUtil.isStartTag(xpp, "widevine:license")) {
                toLowerInvariant = xpp.getAttributeValue(null, "robustness_level");
                boolean z = toLowerInvariant != null && toLowerInvariant.startsWith("HW");
                requiresSecureDecoder = z;
            } else if (data == null) {
                if (XmlPullParserUtil.isStartTag(xpp, "cenc:pssh") && xpp.next() == 4) {
                    data = Base64.decode(xpp.getText(), 0);
                    uuid = PsshAtomUtil.parseUuid(data);
                    if (uuid == null) {
                        Log.w(TAG, "Skipping malformed cenc:pssh data");
                        data = null;
                    }
                } else if (C0539C.PLAYREADY_UUID.equals(uuid) && XmlPullParserUtil.isStartTag(xpp, "mspr:pro") && xpp.next() == 4) {
                    data = PsshAtomUtil.buildPsshAtom(C0539C.PLAYREADY_UUID, Base64.decode(xpp.getText(), 0));
                }
            }
        } while (!XmlPullParserUtil.isEndTag(xpp, "ContentProtection"));
        if (uuid != null) {
            schemeData = new SchemeData(uuid, MimeTypes.VIDEO_MP4, data, requiresSecureDecoder);
        }
        return Pair.create(schemeType, schemeData);
    }

    protected int parseRole(XmlPullParser xpp) throws XmlPullParserException, IOException {
        String schemeIdUri = parseString(xpp, "schemeIdUri", null);
        String value = parseString(xpp, "value", null);
        do {
            xpp.next();
        } while (!XmlPullParserUtil.isEndTag(xpp, "Role"));
        return ("urn:mpeg:dash:role:2011".equals(schemeIdUri) && "main".equals(value)) ? 1 : 0;
    }

    protected void parseAdaptationSetChild(XmlPullParser xpp) throws XmlPullParserException, IOException {
    }

    protected RepresentationInfo parseRepresentation(XmlPullParser xpp, String baseUrl, String adaptationSetMimeType, String adaptationSetCodecs, int adaptationSetWidth, int adaptationSetHeight, float adaptationSetFrameRate, int adaptationSetAudioChannels, int adaptationSetAudioSamplingRate, String adaptationSetLanguage, int adaptationSetSelectionFlags, List<Descriptor> adaptationSetAccessibilityDescriptors, SegmentBase segmentBase) throws XmlPullParserException, IOException {
        String baseUrl2;
        int audioChannels;
        SegmentBase segmentBase2;
        String drmSchemeType;
        DashManifestParser dashManifestParser = this;
        XmlPullParser xmlPullParser = xpp;
        String id = xmlPullParser.getAttributeValue(null, TtmlNode.ATTR_ID);
        int bandwidth = parseInt(xmlPullParser, "bandwidth", -1);
        String mimeType = parseString(xmlPullParser, "mimeType", adaptationSetMimeType);
        String codecs = parseString(xmlPullParser, "codecs", adaptationSetCodecs);
        int width = parseInt(xmlPullParser, "width", adaptationSetWidth);
        int height = parseInt(xmlPullParser, "height", adaptationSetHeight);
        float frameRate = parseFrameRate(xmlPullParser, adaptationSetFrameRate);
        int audioChannels2 = adaptationSetAudioChannels;
        int audioSamplingRate = parseInt(xmlPullParser, "audioSamplingRate", adaptationSetAudioSamplingRate);
        ArrayList<SchemeData> drmSchemeDatas = new ArrayList();
        ArrayList<Descriptor> inbandEventStreams = new ArrayList();
        ArrayList<Descriptor> supplementalProperties = new ArrayList();
        boolean seenFirstBaseUrl = false;
        SegmentBase segmentBase3 = segmentBase;
        String drmSchemeType2 = null;
        int audioChannels3 = audioChannels2;
        String baseUrl3 = baseUrl;
        while (true) {
            boolean seenFirstBaseUrl2;
            String str;
            String str2;
            int i;
            int i2;
            float f;
            int i3;
            xpp.next();
            int audioChannels4 = audioChannels3;
            if (!XmlPullParserUtil.isStartTag(xmlPullParser, "BaseURL")) {
                if (XmlPullParserUtil.isStartTag(xmlPullParser, "AudioChannelConfiguration")) {
                    baseUrl2 = baseUrl3;
                    audioChannels = parseAudioChannelConfiguration(xpp);
                    seenFirstBaseUrl2 = seenFirstBaseUrl;
                    segmentBase2 = segmentBase3;
                    drmSchemeType = drmSchemeType2;
                } else {
                    SegmentBase segmentBase4;
                    if (XmlPullParserUtil.isStartTag(xmlPullParser, "SegmentBase")) {
                        segmentBase4 = parseSegmentBase(xmlPullParser, (SingleSegmentBase) segmentBase3);
                    } else if (XmlPullParserUtil.isStartTag(xmlPullParser, "SegmentList")) {
                        segmentBase4 = parseSegmentList(xmlPullParser, (SegmentList) segmentBase3);
                    } else if (XmlPullParserUtil.isStartTag(xmlPullParser, "SegmentTemplate")) {
                        segmentBase4 = parseSegmentTemplate(xmlPullParser, (SegmentTemplate) segmentBase3);
                    } else if (XmlPullParserUtil.isStartTag(xmlPullParser, "ContentProtection")) {
                        Pair<String, SchemeData> contentProtection = parseContentProtection(xpp);
                        baseUrl2 = baseUrl3;
                        if (contentProtection.first != null) {
                            drmSchemeType2 = contentProtection.first;
                        }
                        if (contentProtection.second != null) {
                            drmSchemeDatas.add(contentProtection.second);
                        }
                        segmentBase2 = segmentBase3;
                        drmSchemeType = drmSchemeType2;
                        audioChannels = audioChannels4;
                        seenFirstBaseUrl2 = seenFirstBaseUrl;
                    } else {
                        baseUrl2 = baseUrl3;
                        if (XmlPullParserUtil.isStartTag(xmlPullParser, "InbandEventStream")) {
                            inbandEventStreams.add(parseDescriptor(xmlPullParser, "InbandEventStream"));
                        } else if (XmlPullParserUtil.isStartTag(xmlPullParser, "SupplementalProperty")) {
                            supplementalProperties.add(parseDescriptor(xmlPullParser, "SupplementalProperty"));
                        }
                    }
                    baseUrl2 = baseUrl3;
                    segmentBase2 = segmentBase4;
                    drmSchemeType = drmSchemeType2;
                    audioChannels = audioChannels4;
                    seenFirstBaseUrl2 = seenFirstBaseUrl;
                }
                if (!XmlPullParserUtil.isEndTag(xmlPullParser, "Representation")) {
                    break;
                }
                str = adaptationSetMimeType;
                str2 = adaptationSetCodecs;
                i = adaptationSetWidth;
                i2 = adaptationSetHeight;
                f = adaptationSetFrameRate;
                i3 = adaptationSetAudioSamplingRate;
                seenFirstBaseUrl = seenFirstBaseUrl2;
                baseUrl3 = baseUrl2;
                audioChannels3 = audioChannels;
                segmentBase3 = segmentBase2;
                drmSchemeType2 = drmSchemeType;
            } else if (seenFirstBaseUrl) {
                baseUrl2 = baseUrl3;
            } else {
                baseUrl2 = parseBaseUrl(xmlPullParser, baseUrl3);
                segmentBase2 = segmentBase3;
                drmSchemeType = drmSchemeType2;
                audioChannels = audioChannels4;
                seenFirstBaseUrl2 = true;
                if (!XmlPullParserUtil.isEndTag(xmlPullParser, "Representation")) {
                    break;
                }
                str = adaptationSetMimeType;
                str2 = adaptationSetCodecs;
                i = adaptationSetWidth;
                i2 = adaptationSetHeight;
                f = adaptationSetFrameRate;
                i3 = adaptationSetAudioSamplingRate;
                seenFirstBaseUrl = seenFirstBaseUrl2;
                baseUrl3 = baseUrl2;
                audioChannels3 = audioChannels;
                segmentBase3 = segmentBase2;
                drmSchemeType2 = drmSchemeType;
            }
            segmentBase2 = segmentBase3;
            drmSchemeType = drmSchemeType2;
            audioChannels = audioChannels4;
            seenFirstBaseUrl2 = seenFirstBaseUrl;
            if (!XmlPullParserUtil.isEndTag(xmlPullParser, "Representation")) {
                break;
            }
            str = adaptationSetMimeType;
            str2 = adaptationSetCodecs;
            i = adaptationSetWidth;
            i2 = adaptationSetHeight;
            f = adaptationSetFrameRate;
            i3 = adaptationSetAudioSamplingRate;
            seenFirstBaseUrl = seenFirstBaseUrl2;
            baseUrl3 = baseUrl2;
            audioChannels3 = audioChannels;
            segmentBase3 = segmentBase2;
            drmSchemeType2 = drmSchemeType;
        }
        return new RepresentationInfo(buildFormat(id, mimeType, width, height, frameRate, audioChannels, audioSamplingRate, bandwidth, adaptationSetLanguage, adaptationSetSelectionFlags, adaptationSetAccessibilityDescriptors, codecs, supplementalProperties), baseUrl2, segmentBase2 != null ? segmentBase2 : new SingleSegmentBase(), drmSchemeType, drmSchemeDatas, inbandEventStreams, -1);
    }

    protected Format buildFormat(String id, String containerMimeType, int width, int height, float frameRate, int audioChannels, int audioSamplingRate, int bitrate, String language, int selectionFlags, List<Descriptor> accessibilityDescriptors, String codecs, List<Descriptor> supplementalProperties) {
        String sampleMimeType;
        String str = containerMimeType;
        String str2 = codecs;
        String sampleMimeType2 = getSampleMimeType(str, str2);
        if (sampleMimeType2 != null) {
            if (MimeTypes.AUDIO_E_AC3.equals(sampleMimeType2)) {
                sampleMimeType2 = parseEac3SupplementalProperties(supplementalProperties);
            }
            sampleMimeType = sampleMimeType2;
            if (MimeTypes.isVideo(sampleMimeType)) {
                return Format.createVideoContainerFormat(id, str, sampleMimeType, str2, bitrate, width, height, frameRate, null, selectionFlags);
            }
            if (MimeTypes.isAudio(sampleMimeType)) {
                return Format.createAudioContainerFormat(id, str, sampleMimeType, str2, bitrate, audioChannels, audioSamplingRate, null, selectionFlags, language);
            }
            if (mimeTypeIsRawText(sampleMimeType)) {
                int parseCea608AccessibilityChannel;
                if (MimeTypes.APPLICATION_CEA608.equals(sampleMimeType)) {
                    parseCea608AccessibilityChannel = parseCea608AccessibilityChannel(accessibilityDescriptors);
                } else if (MimeTypes.APPLICATION_CEA708.equals(sampleMimeType)) {
                    parseCea608AccessibilityChannel = parseCea708AccessibilityChannel(accessibilityDescriptors);
                } else {
                    parseCea608AccessibilityChannel = -1;
                }
                return Format.createTextContainerFormat(id, str, sampleMimeType, str2, bitrate, selectionFlags, language, parseCea608AccessibilityChannel);
            }
        }
        sampleMimeType = sampleMimeType2;
        return Format.createContainerFormat(id, str, sampleMimeType, str2, bitrate, selectionFlags, language);
    }

    protected Representation buildRepresentation(RepresentationInfo representationInfo, String contentId, String extraDrmSchemeType, ArrayList<SchemeData> extraDrmSchemeDatas, ArrayList<Descriptor> extraInbandEventStreams) {
        RepresentationInfo representationInfo2 = representationInfo;
        Format format = representationInfo2.format;
        String drmSchemeType = representationInfo2.drmSchemeType != null ? representationInfo2.drmSchemeType : extraDrmSchemeType;
        List drmSchemeDatas = representationInfo2.drmSchemeDatas;
        drmSchemeDatas.addAll(extraDrmSchemeDatas);
        if (!drmSchemeDatas.isEmpty()) {
            filterRedundantIncompleteSchemeDatas(drmSchemeDatas);
            format = format.copyWithDrmInitData(new DrmInitData(drmSchemeType, drmSchemeDatas));
        }
        ArrayList<Descriptor> inbandEventStreams = representationInfo2.inbandEventStreams;
        inbandEventStreams.addAll(extraInbandEventStreams);
        return Representation.newInstance(contentId, representationInfo2.revisionId, format, representationInfo2.baseUrl, representationInfo2.segmentBase, inbandEventStreams);
    }

    protected SingleSegmentBase parseSegmentBase(XmlPullParser xpp, SingleSegmentBase parent) throws XmlPullParserException, IOException {
        XmlPullParser xmlPullParser = xpp;
        SingleSegmentBase singleSegmentBase = parent;
        long timescale = parseLong(xmlPullParser, "timescale", singleSegmentBase != null ? singleSegmentBase.timescale : 1);
        long j = 0;
        long presentationTimeOffset = parseLong(xmlPullParser, "presentationTimeOffset", singleSegmentBase != null ? singleSegmentBase.presentationTimeOffset : 0);
        long j2 = singleSegmentBase != null ? singleSegmentBase.indexStart : 0;
        if (singleSegmentBase != null) {
            j = singleSegmentBase.indexLength;
        }
        RangedUri initialization = null;
        String indexRangeText = xmlPullParser.getAttributeValue(null, "indexRange");
        if (indexRangeText != null) {
            String[] indexRange = indexRangeText.split("-");
            j2 = Long.parseLong(indexRange[0]);
            j = (Long.parseLong(indexRange[1]) - j2) + 1;
        }
        long indexLength = j;
        long indexStart = j2;
        if (singleSegmentBase != null) {
            initialization = singleSegmentBase.initialization;
        }
        while (true) {
            RangedUri initialization2 = initialization;
            xpp.next();
            if (XmlPullParserUtil.isStartTag(xmlPullParser, "Initialization")) {
                initialization2 = parseInitialization(xpp);
            }
            RangedUri initialization3 = initialization2;
            if (XmlPullParserUtil.isEndTag(xmlPullParser, "SegmentBase")) {
                return buildSingleSegmentBase(initialization3, timescale, presentationTimeOffset, indexStart, indexLength);
            }
            initialization = initialization3;
        }
    }

    protected SingleSegmentBase buildSingleSegmentBase(RangedUri initialization, long timescale, long presentationTimeOffset, long indexStart, long indexLength) {
        return new SingleSegmentBase(initialization, timescale, presentationTimeOffset, indexStart, indexLength);
    }

    protected SegmentList parseSegmentList(XmlPullParser xpp, SegmentList parent) throws XmlPullParserException, IOException {
        XmlPullParser xmlPullParser = xpp;
        SegmentList segmentList = parent;
        long timescale = parseLong(xmlPullParser, "timescale", segmentList != null ? segmentList.timescale : 1);
        long presentationTimeOffset = parseLong(xmlPullParser, "presentationTimeOffset", segmentList != null ? segmentList.presentationTimeOffset : 0);
        long duration = parseLong(xmlPullParser, "duration", segmentList != null ? segmentList.duration : C0539C.TIME_UNSET);
        int startNumber = parseInt(xmlPullParser, "startNumber", segmentList != null ? segmentList.startNumber : 1);
        RangedUri initialization = null;
        List<SegmentTimelineElement> timeline = null;
        List<RangedUri> segments = null;
        do {
            xpp.next();
            if (XmlPullParserUtil.isStartTag(xmlPullParser, "Initialization")) {
                initialization = parseInitialization(xpp);
            } else if (XmlPullParserUtil.isStartTag(xmlPullParser, "SegmentTimeline")) {
                timeline = parseSegmentTimeline(xpp);
            } else if (XmlPullParserUtil.isStartTag(xmlPullParser, "SegmentURL")) {
                if (segments == null) {
                    segments = new ArrayList();
                }
                segments.add(parseSegmentUrl(xpp));
            }
        } while (!XmlPullParserUtil.isEndTag(xmlPullParser, "SegmentList"));
        if (segmentList != null) {
            initialization = initialization != null ? initialization : segmentList.initialization;
            timeline = timeline != null ? timeline : segmentList.segmentTimeline;
            segments = segments != null ? segments : segmentList.mediaSegments;
        }
        return buildSegmentList(initialization, timescale, presentationTimeOffset, startNumber, duration, timeline, segments);
    }

    protected SegmentList buildSegmentList(RangedUri initialization, long timescale, long presentationTimeOffset, int startNumber, long duration, List<SegmentTimelineElement> timeline, List<RangedUri> segments) {
        return new SegmentList(initialization, timescale, presentationTimeOffset, startNumber, duration, timeline, segments);
    }

    protected SegmentTemplate parseSegmentTemplate(XmlPullParser xpp, SegmentTemplate parent) throws XmlPullParserException, IOException {
        DashManifestParser dashManifestParser = this;
        XmlPullParser xmlPullParser = xpp;
        SegmentTemplate segmentTemplate = parent;
        long timescale = parseLong(xmlPullParser, "timescale", segmentTemplate != null ? segmentTemplate.timescale : 1);
        long presentationTimeOffset = parseLong(xmlPullParser, "presentationTimeOffset", segmentTemplate != null ? segmentTemplate.presentationTimeOffset : 0);
        long duration = parseLong(xmlPullParser, "duration", segmentTemplate != null ? segmentTemplate.duration : C0539C.TIME_UNSET);
        int startNumber = parseInt(xmlPullParser, "startNumber", segmentTemplate != null ? segmentTemplate.startNumber : 1);
        List<SegmentTimelineElement> timeline = null;
        UrlTemplate mediaTemplate = parseUrlTemplate(xmlPullParser, "media", segmentTemplate != null ? segmentTemplate.mediaTemplate : null);
        UrlTemplate initializationTemplate = parseUrlTemplate(xmlPullParser, "initialization", segmentTemplate != null ? segmentTemplate.initializationTemplate : null);
        RangedUri initialization = null;
        do {
            xpp.next();
            if (XmlPullParserUtil.isStartTag(xmlPullParser, "Initialization")) {
                initialization = parseInitialization(xpp);
            } else if (XmlPullParserUtil.isStartTag(xmlPullParser, "SegmentTimeline")) {
                timeline = parseSegmentTimeline(xpp);
            }
        } while (!XmlPullParserUtil.isEndTag(xmlPullParser, "SegmentTemplate"));
        if (segmentTemplate != null) {
            initialization = initialization != null ? initialization : segmentTemplate.initialization;
            timeline = timeline != null ? timeline : segmentTemplate.segmentTimeline;
        }
        return buildSegmentTemplate(initialization, timescale, presentationTimeOffset, startNumber, duration, timeline, initializationTemplate, mediaTemplate);
    }

    protected SegmentTemplate buildSegmentTemplate(RangedUri initialization, long timescale, long presentationTimeOffset, int startNumber, long duration, List<SegmentTimelineElement> timeline, UrlTemplate initializationTemplate, UrlTemplate mediaTemplate) {
        return new SegmentTemplate(initialization, timescale, presentationTimeOffset, startNumber, duration, timeline, initializationTemplate, mediaTemplate);
    }

    protected EventStream parseEventStream(XmlPullParser xpp) throws XmlPullParserException, IOException {
        XmlPullParser xmlPullParser = xpp;
        String schemeIdUri = parseString(xmlPullParser, "schemeIdUri", TtmlNode.ANONYMOUS_REGION_ID);
        String value = parseString(xmlPullParser, "value", TtmlNode.ANONYMOUS_REGION_ID);
        long timescale = parseLong(xmlPullParser, "timescale", 1);
        List<EventMessage> eventMessages = new ArrayList();
        ByteArrayOutputStream scratchOutputStream = new ByteArrayOutputStream(512);
        do {
            xpp.next();
            if (XmlPullParserUtil.isStartTag(xmlPullParser, "Event")) {
                eventMessages.add(parseEvent(xmlPullParser, schemeIdUri, value, timescale, scratchOutputStream));
            }
        } while (!XmlPullParserUtil.isEndTag(xmlPullParser, "EventStream"));
        long[] presentationTimesUs = new long[eventMessages.size()];
        EventMessage[] events = new EventMessage[eventMessages.size()];
        for (int i = 0; i < eventMessages.size(); i++) {
            EventMessage event = (EventMessage) eventMessages.get(i);
            presentationTimesUs[i] = event.presentationTimeUs;
            events[i] = event;
        }
        return buildEventStream(schemeIdUri, value, timescale, presentationTimesUs, events);
    }

    protected EventStream buildEventStream(String schemeIdUri, String value, long timescale, long[] presentationTimesUs, EventMessage[] events) {
        return new EventStream(schemeIdUri, value, timescale, presentationTimesUs, events);
    }

    protected EventMessage parseEvent(XmlPullParser xpp, String schemeIdUri, String value, long timescale, ByteArrayOutputStream scratchOutputStream) throws IOException, XmlPullParserException {
        XmlPullParser xmlPullParser = xpp;
        long id = parseLong(xmlPullParser, TtmlNode.ATTR_ID, 0);
        long duration = parseLong(xmlPullParser, "duration", C0539C.TIME_UNSET);
        long presentationTime = parseLong(xmlPullParser, "presentationTime", 0);
        return buildEvent(schemeIdUri, value, id, Util.scaleLargeTimestamp(duration, 1000, timescale), parseEventObject(xmlPullParser, scratchOutputStream), Util.scaleLargeTimestamp(presentationTime, C0539C.MICROS_PER_SECOND, timescale));
    }

    protected byte[] parseEventObject(XmlPullParser xpp, ByteArrayOutputStream scratchOutputStream) throws XmlPullParserException, IOException {
        scratchOutputStream.reset();
        XmlSerializer xmlSerializer = Xml.newSerializer();
        xmlSerializer.setOutput(scratchOutputStream, null);
        xpp.nextToken();
        while (!XmlPullParserUtil.isEndTag(xpp, "Event")) {
            int i = 0;
            switch (xpp.getEventType()) {
                case 0:
                    xmlSerializer.startDocument(null, Boolean.valueOf(false));
                    break;
                case 1:
                    xmlSerializer.endDocument();
                    break;
                case 2:
                    xmlSerializer.startTag(xpp.getNamespace(), xpp.getName());
                    while (true) {
                        int i2 = i;
                        if (i2 >= xpp.getAttributeCount()) {
                            break;
                        }
                        xmlSerializer.attribute(xpp.getAttributeNamespace(i2), xpp.getAttributeName(i2), xpp.getAttributeValue(i2));
                        i = i2 + 1;
                    }
                case 3:
                    xmlSerializer.endTag(xpp.getNamespace(), xpp.getName());
                    break;
                case 4:
                    xmlSerializer.text(xpp.getText());
                    break;
                case 5:
                    xmlSerializer.cdsect(xpp.getText());
                    break;
                case 6:
                    xmlSerializer.entityRef(xpp.getText());
                    break;
                case 7:
                    xmlSerializer.ignorableWhitespace(xpp.getText());
                    break;
                case 8:
                    xmlSerializer.processingInstruction(xpp.getText());
                    break;
                case 9:
                    xmlSerializer.comment(xpp.getText());
                    break;
                case 10:
                    xmlSerializer.docdecl(xpp.getText());
                    break;
                default:
                    break;
            }
            xpp.nextToken();
        }
        xmlSerializer.flush();
        return scratchOutputStream.toByteArray();
    }

    protected EventMessage buildEvent(String schemeIdUri, String value, long id, long durationMs, byte[] messageData, long presentationTimeUs) {
        return new EventMessage(schemeIdUri, value, durationMs, id, messageData, presentationTimeUs);
    }

    protected List<SegmentTimelineElement> parseSegmentTimeline(XmlPullParser xpp) throws XmlPullParserException, IOException {
        List<SegmentTimelineElement> segmentTimeline = new ArrayList();
        long elapsedTime = 0;
        do {
            xpp.next();
            if (XmlPullParserUtil.isStartTag(xpp, "S")) {
                elapsedTime = parseLong(xpp, "t", elapsedTime);
                long duration = parseLong(xpp, "d", C0539C.TIME_UNSET);
                int i = 0;
                int count = 1 + parseInt(xpp, "r", 0);
                while (true) {
                    int i2 = i;
                    if (i2 >= count) {
                        break;
                    }
                    segmentTimeline.add(buildSegmentTimelineElement(elapsedTime, duration));
                    i = i2 + 1;
                    elapsedTime += duration;
                }
            }
        } while (!XmlPullParserUtil.isEndTag(xpp, "SegmentTimeline"));
        return segmentTimeline;
    }

    protected SegmentTimelineElement buildSegmentTimelineElement(long elapsedTime, long duration) {
        return new SegmentTimelineElement(elapsedTime, duration);
    }

    protected UrlTemplate parseUrlTemplate(XmlPullParser xpp, String name, UrlTemplate defaultValue) {
        String valueString = xpp.getAttributeValue(null, name);
        if (valueString != null) {
            return UrlTemplate.compile(valueString);
        }
        return defaultValue;
    }

    protected RangedUri parseInitialization(XmlPullParser xpp) {
        return parseRangedUrl(xpp, "sourceURL", "range");
    }

    protected RangedUri parseSegmentUrl(XmlPullParser xpp) {
        return parseRangedUrl(xpp, "media", "mediaRange");
    }

    protected RangedUri parseRangedUrl(XmlPullParser xpp, String urlAttribute, String rangeAttribute) {
        String urlText = xpp.getAttributeValue(null, urlAttribute);
        long rangeStart = 0;
        long rangeLength = -1;
        String rangeText = xpp.getAttributeValue(null, rangeAttribute);
        if (rangeText != null) {
            String[] rangeTextArray = rangeText.split("-");
            rangeStart = Long.parseLong(rangeTextArray[0]);
            if (rangeTextArray.length == 2) {
                rangeLength = (Long.parseLong(rangeTextArray[1]) - rangeStart) + 1;
            }
        }
        return buildRangedUri(urlText, rangeStart, rangeLength);
    }

    protected RangedUri buildRangedUri(String urlText, long rangeStart, long rangeLength) {
        return new RangedUri(urlText, rangeStart, rangeLength);
    }

    protected int parseAudioChannelConfiguration(XmlPullParser xpp) throws XmlPullParserException, IOException {
        String schemeIdUri = parseString(xpp, "schemeIdUri", null);
        int audioChannels = -1;
        if ("urn:mpeg:dash:23003:3:audio_channel_configuration:2011".equals(schemeIdUri)) {
            audioChannels = parseInt(xpp, "value", -1);
        } else if ("tag:dolby.com,2014:dash:audio_channel_configuration:2011".equals(schemeIdUri)) {
            audioChannels = parseDolbyChannelConfiguration(xpp);
        }
        do {
            xpp.next();
        } while (!XmlPullParserUtil.isEndTag(xpp, "AudioChannelConfiguration"));
        return audioChannels;
    }

    private static void filterRedundantIncompleteSchemeDatas(ArrayList<SchemeData> schemeDatas) {
        for (int i = schemeDatas.size() - 1; i >= 0; i--) {
            SchemeData schemeData = (SchemeData) schemeDatas.get(i);
            if (!schemeData.hasData()) {
                for (int j = 0; j < schemeDatas.size(); j++) {
                    if (((SchemeData) schemeDatas.get(j)).canReplace(schemeData)) {
                        schemeDatas.remove(i);
                        break;
                    }
                }
            }
        }
    }

    private static String getSampleMimeType(String containerMimeType, String codecs) {
        if (MimeTypes.isAudio(containerMimeType)) {
            return MimeTypes.getAudioMediaMimeType(codecs);
        }
        if (MimeTypes.isVideo(containerMimeType)) {
            return MimeTypes.getVideoMediaMimeType(codecs);
        }
        if (mimeTypeIsRawText(containerMimeType)) {
            return containerMimeType;
        }
        if (!MimeTypes.APPLICATION_MP4.equals(containerMimeType)) {
            if (MimeTypes.APPLICATION_RAWCC.equals(containerMimeType) && codecs != null) {
                if (codecs.contains("cea708")) {
                    return MimeTypes.APPLICATION_CEA708;
                }
                if (codecs.contains("eia608") || codecs.contains("cea608")) {
                    return MimeTypes.APPLICATION_CEA608;
                }
            }
            return null;
        } else if ("stpp".equals(codecs)) {
            return MimeTypes.APPLICATION_TTML;
        } else {
            if ("wvtt".equals(codecs)) {
                return MimeTypes.APPLICATION_MP4VTT;
            }
        }
        return null;
    }

    private static boolean mimeTypeIsRawText(String mimeType) {
        if (!(MimeTypes.isText(mimeType) || MimeTypes.APPLICATION_TTML.equals(mimeType) || MimeTypes.APPLICATION_MP4VTT.equals(mimeType) || MimeTypes.APPLICATION_CEA708.equals(mimeType))) {
            if (!MimeTypes.APPLICATION_CEA608.equals(mimeType)) {
                return false;
            }
        }
        return true;
    }

    private static String checkLanguageConsistency(String firstLanguage, String secondLanguage) {
        if (firstLanguage == null) {
            return secondLanguage;
        }
        if (secondLanguage == null) {
            return firstLanguage;
        }
        Assertions.checkState(firstLanguage.equals(secondLanguage));
        return firstLanguage;
    }

    private static int checkContentTypeConsistency(int firstType, int secondType) {
        if (firstType == -1) {
            return secondType;
        }
        if (secondType == -1) {
            return firstType;
        }
        Assertions.checkState(firstType == secondType);
        return firstType;
    }

    protected static Descriptor parseDescriptor(XmlPullParser xpp, String tag) throws XmlPullParserException, IOException {
        String schemeIdUri = parseString(xpp, "schemeIdUri", TtmlNode.ANONYMOUS_REGION_ID);
        String value = parseString(xpp, "value", null);
        String id = parseString(xpp, TtmlNode.ATTR_ID, null);
        do {
            xpp.next();
        } while (!XmlPullParserUtil.isEndTag(xpp, tag));
        return new Descriptor(schemeIdUri, value, id);
    }

    protected static int parseCea608AccessibilityChannel(List<Descriptor> accessibilityDescriptors) {
        for (int i = 0; i < accessibilityDescriptors.size(); i++) {
            Descriptor descriptor = (Descriptor) accessibilityDescriptors.get(i);
            if ("urn:scte:dash:cc:cea-608:2015".equals(descriptor.schemeIdUri) && descriptor.value != null) {
                Matcher accessibilityValueMatcher = CEA_608_ACCESSIBILITY_PATTERN.matcher(descriptor.value);
                if (accessibilityValueMatcher.matches()) {
                    return Integer.parseInt(accessibilityValueMatcher.group(1));
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

    protected static int parseCea708AccessibilityChannel(List<Descriptor> accessibilityDescriptors) {
        for (int i = 0; i < accessibilityDescriptors.size(); i++) {
            Descriptor descriptor = (Descriptor) accessibilityDescriptors.get(i);
            if ("urn:scte:dash:cc:cea-708:2015".equals(descriptor.schemeIdUri) && descriptor.value != null) {
                Matcher accessibilityValueMatcher = CEA_708_ACCESSIBILITY_PATTERN.matcher(descriptor.value);
                if (accessibilityValueMatcher.matches()) {
                    return Integer.parseInt(accessibilityValueMatcher.group(1));
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

    protected static String parseEac3SupplementalProperties(List<Descriptor> supplementalProperties) {
        for (int i = 0; i < supplementalProperties.size(); i++) {
            Descriptor descriptor = (Descriptor) supplementalProperties.get(i);
            if ("tag:dolby.com,2014:dash:DolbyDigitalPlusExtensionType:2014".equals(descriptor.schemeIdUri) && "ec+3".equals(descriptor.value)) {
                return MimeTypes.AUDIO_E_AC3_JOC;
            }
        }
        return MimeTypes.AUDIO_E_AC3;
    }

    protected static float parseFrameRate(XmlPullParser xpp, float defaultValue) {
        float frameRate = defaultValue;
        String frameRateAttribute = xpp.getAttributeValue(null, "frameRate");
        if (frameRateAttribute == null) {
            return frameRate;
        }
        Matcher frameRateMatcher = FRAME_RATE_PATTERN.matcher(frameRateAttribute);
        if (!frameRateMatcher.matches()) {
            return frameRate;
        }
        int numerator = Integer.parseInt(frameRateMatcher.group(1));
        String denominatorString = frameRateMatcher.group(2);
        if (TextUtils.isEmpty(denominatorString)) {
            return (float) numerator;
        }
        return ((float) numerator) / ((float) Integer.parseInt(denominatorString));
    }

    protected static long parseDuration(XmlPullParser xpp, String name, long defaultValue) {
        String value = xpp.getAttributeValue(null, name);
        if (value == null) {
            return defaultValue;
        }
        return Util.parseXsDuration(value);
    }

    protected static long parseDateTime(XmlPullParser xpp, String name, long defaultValue) throws ParserException {
        String value = xpp.getAttributeValue(null, name);
        if (value == null) {
            return defaultValue;
        }
        return Util.parseXsDateTime(value);
    }

    protected static String parseBaseUrl(XmlPullParser xpp, String parentBaseUrl) throws XmlPullParserException, IOException {
        xpp.next();
        return UriUtil.resolve(parentBaseUrl, xpp.getText());
    }

    protected static int parseInt(XmlPullParser xpp, String name, int defaultValue) {
        String value = xpp.getAttributeValue(null, name);
        return value == null ? defaultValue : Integer.parseInt(value);
    }

    protected static long parseLong(XmlPullParser xpp, String name, long defaultValue) {
        String value = xpp.getAttributeValue(null, name);
        return value == null ? defaultValue : Long.parseLong(value);
    }

    protected static String parseString(XmlPullParser xpp, String name, String defaultValue) {
        String value = xpp.getAttributeValue(null, name);
        return value == null ? defaultValue : value;
    }

    protected static int parseDolbyChannelConfiguration(XmlPullParser xpp) {
        String value = Util.toLowerInvariant(xpp.getAttributeValue(null, "value"));
        if (value == null) {
            return -1;
        }
        int hashCode = value.hashCode();
        if (hashCode != 1596796) {
            if (hashCode != 2937391) {
                if (hashCode != 3094035) {
                    if (hashCode == 3133436) {
                        if (value.equals("fa01")) {
                            hashCode = 3;
                            switch (hashCode) {
                                case 0:
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
                } else if (value.equals("f801")) {
                    hashCode = 2;
                    switch (hashCode) {
                        case 0:
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
            } else if (value.equals("a000")) {
                hashCode = 1;
                switch (hashCode) {
                    case 0:
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
        } else if (value.equals("4000")) {
            hashCode = 0;
            switch (hashCode) {
                case 0:
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
        hashCode = -1;
        switch (hashCode) {
            case 0:
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
