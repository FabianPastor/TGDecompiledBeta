package org.telegram.messenger.exoplayer.dash.mpd;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import com.google.firebase.analytics.FirebaseAnalytics.Param;
import com.mp4parser.iso14496.part30.WebVTTSampleEntry;
import com.mp4parser.iso14496.part30.XMLSubtitleSampleEntry;
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
import org.aspectj.lang.JoinPoint;
import org.telegram.messenger.exoplayer.ParserException;
import org.telegram.messenger.exoplayer.chunk.Format;
import org.telegram.messenger.exoplayer.dash.mpd.SegmentBase.SegmentList;
import org.telegram.messenger.exoplayer.dash.mpd.SegmentBase.SegmentTemplate;
import org.telegram.messenger.exoplayer.dash.mpd.SegmentBase.SegmentTimelineElement;
import org.telegram.messenger.exoplayer.dash.mpd.SegmentBase.SingleSegmentBase;
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

public class MediaPresentationDescriptionParser extends DefaultHandler implements Parser<MediaPresentationDescription> {
    private static final Pattern FRAME_RATE_PATTERN = Pattern.compile("(\\d+)(?:/(\\d+))?");
    private static final String TAG = "MediaPresentationDescriptionParser";
    private final String contentId;
    private final XmlPullParserFactory xmlParserFactory;

    protected static final class ContentProtectionsBuilder implements Comparator<ContentProtection> {
        private ArrayList<ContentProtection> adaptationSetProtections;
        private ArrayList<ContentProtection> currentRepresentationProtections;
        private ArrayList<ContentProtection> representationProtections;
        private boolean representationProtectionsSet;

        protected ContentProtectionsBuilder() {
        }

        public void addAdaptationSetProtection(ContentProtection contentProtection) {
            if (this.adaptationSetProtections == null) {
                this.adaptationSetProtections = new ArrayList();
            }
            maybeAddContentProtection(this.adaptationSetProtections, contentProtection);
        }

        public void addRepresentationProtection(ContentProtection contentProtection) {
            if (this.currentRepresentationProtections == null) {
                this.currentRepresentationProtections = new ArrayList();
            }
            maybeAddContentProtection(this.currentRepresentationProtections, contentProtection);
        }

        public void endRepresentation() {
            boolean z = true;
            if (!this.representationProtectionsSet) {
                if (this.currentRepresentationProtections != null) {
                    Collections.sort(this.currentRepresentationProtections, this);
                }
                this.representationProtections = this.currentRepresentationProtections;
                this.representationProtectionsSet = true;
            } else if (this.currentRepresentationProtections == null) {
                if (this.representationProtections != null) {
                    z = false;
                }
                Assertions.checkState(z);
            } else {
                Collections.sort(this.currentRepresentationProtections, this);
                Assertions.checkState(this.currentRepresentationProtections.equals(this.representationProtections));
            }
            this.currentRepresentationProtections = null;
        }

        public ArrayList<ContentProtection> build() {
            if (this.adaptationSetProtections == null) {
                return this.representationProtections;
            }
            if (this.representationProtections == null) {
                return this.adaptationSetProtections;
            }
            for (int i = 0; i < this.representationProtections.size(); i++) {
                maybeAddContentProtection(this.adaptationSetProtections, (ContentProtection) this.representationProtections.get(i));
            }
            return this.adaptationSetProtections;
        }

        private void maybeAddContentProtection(List<ContentProtection> contentProtections, ContentProtection contentProtection) {
            if (!contentProtections.contains(contentProtection)) {
                for (int i = 0; i < contentProtections.size(); i++) {
                    Assertions.checkState(!((ContentProtection) contentProtections.get(i)).schemeUriId.equals(contentProtection.schemeUriId));
                }
                contentProtections.add(contentProtection);
            }
        }

        public int compare(ContentProtection first, ContentProtection second) {
            return first.schemeUriId.compareTo(second.schemeUriId);
        }
    }

    public MediaPresentationDescriptionParser() {
        this(null);
    }

    public MediaPresentationDescriptionParser(String contentId) {
        this.contentId = contentId;
        try {
            this.xmlParserFactory = XmlPullParserFactory.newInstance();
        } catch (XmlPullParserException e) {
            throw new RuntimeException("Couldn't create XmlPullParserFactory instance", e);
        }
    }

    public MediaPresentationDescription parse(String connectionUrl, InputStream inputStream) throws IOException, ParserException {
        try {
            XmlPullParser xpp = this.xmlParserFactory.newPullParser();
            xpp.setInput(inputStream, null);
            if (xpp.next() == 2 && "MPD".equals(xpp.getName())) {
                return parseMediaPresentationDescription(xpp, connectionUrl);
            }
            throw new ParserException("inputStream does not contain a valid media presentation description");
        } catch (Throwable e) {
            throw new ParserException(e);
        } catch (Throwable e2) {
            throw new ParserException(e2);
        }
    }

    protected MediaPresentationDescription parseMediaPresentationDescription(XmlPullParser xpp, String baseUrl) throws XmlPullParserException, IOException, ParseException {
        long availabilityStartTime = parseDateTime(xpp, "availabilityStartTime", -1);
        long durationMs = parseDuration(xpp, "mediaPresentationDuration", -1);
        long minBufferTimeMs = parseDuration(xpp, "minBufferTime", -1);
        String typeString = xpp.getAttributeValue(null, "type");
        boolean dynamic = typeString != null ? typeString.equals("dynamic") : false;
        long minUpdateTimeMs = dynamic ? parseDuration(xpp, "minimumUpdatePeriod", -1) : -1;
        long timeShiftBufferDepthMs = dynamic ? parseDuration(xpp, "timeShiftBufferDepth", -1) : -1;
        UtcTimingElement utcTiming = null;
        String location = null;
        List<Period> periods = new ArrayList();
        long nextPeriodStartMs = dynamic ? -1 : 0;
        boolean seenEarlyAccessPeriod = false;
        boolean seenFirstBaseUrl = false;
        do {
            xpp.next();
            if (!ParserUtil.isStartTag(xpp, "BaseURL")) {
                if (ParserUtil.isStartTag(xpp, "UTCTiming")) {
                    utcTiming = parseUtcTiming(xpp);
                } else {
                    if (ParserUtil.isStartTag(xpp, "Location")) {
                        location = xpp.nextText();
                    } else {
                        if (ParserUtil.isStartTag(xpp, "Period") && !seenEarlyAccessPeriod) {
                            Pair<Period, Long> periodWithDurationMs = parsePeriod(xpp, baseUrl, nextPeriodStartMs);
                            Period period = periodWithDurationMs.first;
                            if (period.startMs != -1) {
                                long periodDurationMs = ((Long) periodWithDurationMs.second).longValue();
                                nextPeriodStartMs = periodDurationMs == -1 ? -1 : period.startMs + periodDurationMs;
                                periods.add(period);
                            } else if (dynamic) {
                                seenEarlyAccessPeriod = true;
                            } else {
                                throw new ParserException("Unable to determine start of period " + periods.size());
                            }
                        }
                    }
                }
            } else if (!seenFirstBaseUrl) {
                baseUrl = parseBaseUrl(xpp, baseUrl);
                seenFirstBaseUrl = true;
            }
        } while (!ParserUtil.isEndTag(xpp, "MPD"));
        if (durationMs == -1) {
            if (nextPeriodStartMs != -1) {
                durationMs = nextPeriodStartMs;
            } else if (!dynamic) {
                throw new ParserException("Unable to determine duration of static manifest.");
            }
        }
        if (!periods.isEmpty()) {
            return buildMediaPresentationDescription(availabilityStartTime, durationMs, minBufferTimeMs, dynamic, minUpdateTimeMs, timeShiftBufferDepthMs, utcTiming, location, periods);
        }
        throw new ParserException("No periods found.");
    }

    protected MediaPresentationDescription buildMediaPresentationDescription(long availabilityStartTime, long durationMs, long minBufferTimeMs, boolean dynamic, long minUpdateTimeMs, long timeShiftBufferDepthMs, UtcTimingElement utcTiming, String location, List<Period> periods) {
        return new MediaPresentationDescription(availabilityStartTime, durationMs, minBufferTimeMs, dynamic, minUpdateTimeMs, timeShiftBufferDepthMs, utcTiming, location, periods);
    }

    protected UtcTimingElement parseUtcTiming(XmlPullParser xpp) {
        return buildUtcTimingElement(xpp.getAttributeValue(null, "schemeIdUri"), xpp.getAttributeValue(null, Param.VALUE));
    }

    protected UtcTimingElement buildUtcTimingElement(String schemeIdUri, String value) {
        return new UtcTimingElement(schemeIdUri, value);
    }

    protected Pair<Period, Long> parsePeriod(XmlPullParser xpp, String baseUrl, long defaultStartMs) throws XmlPullParserException, IOException {
        String id = xpp.getAttributeValue(null, TtmlNode.ATTR_ID);
        long startMs = parseDuration(xpp, TtmlNode.START, defaultStartMs);
        long durationMs = parseDuration(xpp, "duration", -1);
        SegmentBase segmentBase = null;
        List<AdaptationSet> adaptationSets = new ArrayList();
        boolean seenFirstBaseUrl = false;
        do {
            xpp.next();
            if (!ParserUtil.isStartTag(xpp, "BaseURL")) {
                if (ParserUtil.isStartTag(xpp, "AdaptationSet")) {
                    adaptationSets.add(parseAdaptationSet(xpp, baseUrl, segmentBase));
                } else {
                    if (ParserUtil.isStartTag(xpp, "SegmentBase")) {
                        segmentBase = parseSegmentBase(xpp, baseUrl, null);
                    } else {
                        if (ParserUtil.isStartTag(xpp, "SegmentList")) {
                            segmentBase = parseSegmentList(xpp, baseUrl, null);
                        } else {
                            if (ParserUtil.isStartTag(xpp, "SegmentTemplate")) {
                                segmentBase = parseSegmentTemplate(xpp, baseUrl, null);
                            }
                        }
                    }
                }
            } else if (!seenFirstBaseUrl) {
                baseUrl = parseBaseUrl(xpp, baseUrl);
                seenFirstBaseUrl = true;
            }
        } while (!ParserUtil.isEndTag(xpp, "Period"));
        return Pair.create(buildPeriod(id, startMs, adaptationSets), Long.valueOf(durationMs));
    }

    protected Period buildPeriod(String id, long startMs, List<AdaptationSet> adaptationSets) {
        return new Period(id, startMs, adaptationSets);
    }

    protected AdaptationSet parseAdaptationSet(XmlPullParser xpp, String baseUrl, SegmentBase segmentBase) throws XmlPullParserException, IOException {
        int id = parseInt(xpp, TtmlNode.ATTR_ID, -1);
        int contentType = parseContentType(xpp);
        String mimeType = xpp.getAttributeValue(null, "mimeType");
        String codecs = xpp.getAttributeValue(null, "codecs");
        int width = parseInt(xpp, "width", -1);
        int height = parseInt(xpp, "height", -1);
        float frameRate = parseFrameRate(xpp, -1.0f);
        int audioChannels = -1;
        int audioSamplingRate = parseInt(xpp, "audioSamplingRate", -1);
        String language = xpp.getAttributeValue(null, "lang");
        ContentProtectionsBuilder contentProtectionsBuilder = new ContentProtectionsBuilder();
        List<Representation> representations = new ArrayList();
        boolean seenFirstBaseUrl = false;
        do {
            xpp.next();
            if (!ParserUtil.isStartTag(xpp, "BaseURL")) {
                if (ParserUtil.isStartTag(xpp, "ContentProtection")) {
                    ContentProtection contentProtection = parseContentProtection(xpp);
                    if (contentProtection != null) {
                        contentProtectionsBuilder.addAdaptationSetProtection(contentProtection);
                    }
                } else {
                    if (ParserUtil.isStartTag(xpp, "ContentComponent")) {
                        language = checkLanguageConsistency(language, xpp.getAttributeValue(null, "lang"));
                        contentType = checkContentTypeConsistency(contentType, parseContentType(xpp));
                    } else {
                        if (ParserUtil.isStartTag(xpp, "Representation")) {
                            Representation representation = parseRepresentation(xpp, baseUrl, mimeType, codecs, width, height, frameRate, audioChannels, audioSamplingRate, language, segmentBase, contentProtectionsBuilder);
                            contentProtectionsBuilder.endRepresentation();
                            contentType = checkContentTypeConsistency(contentType, getContentType(representation));
                            representations.add(representation);
                        } else {
                            if (ParserUtil.isStartTag(xpp, "AudioChannelConfiguration")) {
                                audioChannels = parseAudioChannelConfiguration(xpp);
                            } else {
                                if (ParserUtil.isStartTag(xpp, "SegmentBase")) {
                                    segmentBase = parseSegmentBase(xpp, baseUrl, (SingleSegmentBase) segmentBase);
                                } else {
                                    if (ParserUtil.isStartTag(xpp, "SegmentList")) {
                                        segmentBase = parseSegmentList(xpp, baseUrl, (SegmentList) segmentBase);
                                    } else {
                                        if (ParserUtil.isStartTag(xpp, "SegmentTemplate")) {
                                            segmentBase = parseSegmentTemplate(xpp, baseUrl, (SegmentTemplate) segmentBase);
                                        } else if (ParserUtil.isStartTag(xpp)) {
                                            parseAdaptationSetChild(xpp);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (!seenFirstBaseUrl) {
                baseUrl = parseBaseUrl(xpp, baseUrl);
                seenFirstBaseUrl = true;
            }
        } while (!ParserUtil.isEndTag(xpp, "AdaptationSet"));
        return buildAdaptationSet(id, contentType, representations, contentProtectionsBuilder.build());
    }

    protected AdaptationSet buildAdaptationSet(int id, int contentType, List<Representation> representations, List<ContentProtection> contentProtections) {
        return new AdaptationSet(id, contentType, representations, contentProtections);
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
            return 0;
        }
        if ("text".equals(contentType)) {
            return 2;
        }
        return -1;
    }

    protected int getContentType(Representation representation) {
        String mimeType = representation.format.mimeType;
        if (TextUtils.isEmpty(mimeType)) {
            return -1;
        }
        if (MimeTypes.isVideo(mimeType)) {
            return 0;
        }
        if (MimeTypes.isAudio(mimeType)) {
            return 1;
        }
        if (MimeTypes.isText(mimeType) || MimeTypes.APPLICATION_TTML.equals(mimeType)) {
            return 2;
        }
        if (!MimeTypes.APPLICATION_MP4.equals(mimeType)) {
            return -1;
        }
        String codecs = representation.format.codecs;
        if (XMLSubtitleSampleEntry.TYPE.equals(codecs) || WebVTTSampleEntry.TYPE.equals(codecs)) {
            return 2;
        }
        return -1;
    }

    protected ContentProtection parseContentProtection(XmlPullParser xpp) throws XmlPullParserException, IOException {
        String schemeIdUri = xpp.getAttributeValue(null, "schemeIdUri");
        UUID uuid = null;
        SchemeInitData data = null;
        boolean seenPsshElement = false;
        do {
            xpp.next();
            if (ParserUtil.isStartTag(xpp, "cenc:pssh") && xpp.next() == 4) {
                seenPsshElement = true;
                data = new SchemeInitData(MimeTypes.VIDEO_MP4, Base64.decode(xpp.getText(), 0));
                uuid = PsshAtomUtil.parseUuid(data.data);
            }
        } while (!ParserUtil.isEndTag(xpp, "ContentProtection"));
        if (!seenPsshElement || uuid != null) {
            return buildContentProtection(schemeIdUri, uuid, data);
        }
        Log.w(TAG, "Skipped unsupported ContentProtection element");
        return null;
    }

    protected ContentProtection buildContentProtection(String schemeIdUri, UUID uuid, SchemeInitData data) {
        return new ContentProtection(schemeIdUri, uuid, data);
    }

    protected void parseAdaptationSetChild(XmlPullParser xpp) throws XmlPullParserException, IOException {
    }

    protected Representation parseRepresentation(XmlPullParser xpp, String baseUrl, String adaptationSetMimeType, String adaptationSetCodecs, int adaptationSetWidth, int adaptationSetHeight, float adaptationSetFrameRate, int adaptationSetAudioChannels, int adaptationSetAudioSamplingRate, String adaptationSetLanguage, SegmentBase segmentBase, ContentProtectionsBuilder contentProtectionsBuilder) throws XmlPullParserException, IOException {
        String id = xpp.getAttributeValue(null, TtmlNode.ATTR_ID);
        int bandwidth = parseInt(xpp, "bandwidth");
        String mimeType = parseString(xpp, "mimeType", adaptationSetMimeType);
        String codecs = parseString(xpp, "codecs", adaptationSetCodecs);
        int width = parseInt(xpp, "width", adaptationSetWidth);
        int height = parseInt(xpp, "height", adaptationSetHeight);
        float frameRate = parseFrameRate(xpp, adaptationSetFrameRate);
        int audioChannels = adaptationSetAudioChannels;
        int audioSamplingRate = parseInt(xpp, "audioSamplingRate", adaptationSetAudioSamplingRate);
        String language = adaptationSetLanguage;
        boolean seenFirstBaseUrl = false;
        do {
            xpp.next();
            if (!ParserUtil.isStartTag(xpp, "BaseURL")) {
                if (ParserUtil.isStartTag(xpp, "AudioChannelConfiguration")) {
                    audioChannels = parseAudioChannelConfiguration(xpp);
                } else {
                    if (ParserUtil.isStartTag(xpp, "SegmentBase")) {
                        segmentBase = parseSegmentBase(xpp, baseUrl, (SingleSegmentBase) segmentBase);
                    } else {
                        if (ParserUtil.isStartTag(xpp, "SegmentList")) {
                            segmentBase = parseSegmentList(xpp, baseUrl, (SegmentList) segmentBase);
                        } else {
                            if (ParserUtil.isStartTag(xpp, "SegmentTemplate")) {
                                segmentBase = parseSegmentTemplate(xpp, baseUrl, (SegmentTemplate) segmentBase);
                            } else {
                                if (ParserUtil.isStartTag(xpp, "ContentProtection")) {
                                    ContentProtection contentProtection = parseContentProtection(xpp);
                                    if (contentProtection != null) {
                                        contentProtectionsBuilder.addAdaptationSetProtection(contentProtection);
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (!seenFirstBaseUrl) {
                baseUrl = parseBaseUrl(xpp, baseUrl);
                seenFirstBaseUrl = true;
            }
        } while (!ParserUtil.isEndTag(xpp, "Representation"));
        Format format = buildFormat(id, mimeType, width, height, frameRate, audioChannels, audioSamplingRate, bandwidth, language, codecs);
        String str = this.contentId;
        if (segmentBase == null) {
            SegmentBase singleSegmentBase = new SingleSegmentBase(baseUrl);
        }
        return buildRepresentation(str, -1, format, segmentBase);
    }

    protected Format buildFormat(String id, String mimeType, int width, int height, float frameRate, int audioChannels, int audioSamplingRate, int bandwidth, String language, String codecs) {
        return new Format(id, mimeType, width, height, frameRate, audioChannels, audioSamplingRate, bandwidth, language, codecs);
    }

    protected Representation buildRepresentation(String contentId, int revisionId, Format format, SegmentBase segmentBase) {
        return Representation.newInstance(contentId, (long) revisionId, format, segmentBase);
    }

    protected SingleSegmentBase parseSegmentBase(XmlPullParser xpp, String baseUrl, SingleSegmentBase parent) throws XmlPullParserException, IOException {
        long timescale = parseLong(xpp, "timescale", parent != null ? parent.timescale : 1);
        long presentationTimeOffset = parseLong(xpp, "presentationTimeOffset", parent != null ? parent.presentationTimeOffset : 0);
        long indexStart = parent != null ? parent.indexStart : 0;
        long indexLength = parent != null ? parent.indexLength : -1;
        String indexRangeText = xpp.getAttributeValue(null, "indexRange");
        if (indexRangeText != null) {
            String[] indexRange = indexRangeText.split("-");
            indexStart = Long.parseLong(indexRange[0]);
            indexLength = (Long.parseLong(indexRange[1]) - indexStart) + 1;
        }
        RangedUri initialization = parent != null ? parent.initialization : null;
        do {
            xpp.next();
            if (ParserUtil.isStartTag(xpp, "Initialization")) {
                initialization = parseInitialization(xpp, baseUrl);
            }
        } while (!ParserUtil.isEndTag(xpp, "SegmentBase"));
        return buildSingleSegmentBase(initialization, timescale, presentationTimeOffset, baseUrl, indexStart, indexLength);
    }

    protected SingleSegmentBase buildSingleSegmentBase(RangedUri initialization, long timescale, long presentationTimeOffset, String baseUrl, long indexStart, long indexLength) {
        return new SingleSegmentBase(initialization, timescale, presentationTimeOffset, baseUrl, indexStart, indexLength);
    }

    protected SegmentList parseSegmentList(XmlPullParser xpp, String baseUrl, SegmentList parent) throws XmlPullParserException, IOException {
        long timescale = parseLong(xpp, "timescale", parent != null ? parent.timescale : 1);
        long presentationTimeOffset = parseLong(xpp, "presentationTimeOffset", parent != null ? parent.presentationTimeOffset : 0);
        long duration = parseLong(xpp, "duration", parent != null ? parent.duration : -1);
        int startNumber = parseInt(xpp, "startNumber", parent != null ? parent.startNumber : 1);
        RangedUri initialization = null;
        List<SegmentTimelineElement> timeline = null;
        List<RangedUri> segments = null;
        do {
            xpp.next();
            if (ParserUtil.isStartTag(xpp, "Initialization")) {
                initialization = parseInitialization(xpp, baseUrl);
            } else {
                if (ParserUtil.isStartTag(xpp, "SegmentTimeline")) {
                    timeline = parseSegmentTimeline(xpp);
                } else {
                    if (ParserUtil.isStartTag(xpp, "SegmentURL")) {
                        if (segments == null) {
                            segments = new ArrayList();
                        }
                        segments.add(parseSegmentUrl(xpp, baseUrl));
                    }
                }
            }
        } while (!ParserUtil.isEndTag(xpp, "SegmentList"));
        if (parent != null) {
            if (initialization == null) {
                initialization = parent.initialization;
            }
            if (timeline == null) {
                timeline = parent.segmentTimeline;
            }
            if (segments == null) {
                segments = parent.mediaSegments;
            }
        }
        return buildSegmentList(initialization, timescale, presentationTimeOffset, startNumber, duration, timeline, segments);
    }

    protected SegmentList buildSegmentList(RangedUri initialization, long timescale, long presentationTimeOffset, int startNumber, long duration, List<SegmentTimelineElement> timeline, List<RangedUri> segments) {
        return new SegmentList(initialization, timescale, presentationTimeOffset, startNumber, duration, timeline, segments);
    }

    protected SegmentTemplate parseSegmentTemplate(XmlPullParser xpp, String baseUrl, SegmentTemplate parent) throws XmlPullParserException, IOException {
        long timescale = parseLong(xpp, "timescale", parent != null ? parent.timescale : 1);
        long presentationTimeOffset = parseLong(xpp, "presentationTimeOffset", parent != null ? parent.presentationTimeOffset : 0);
        long duration = parseLong(xpp, "duration", parent != null ? parent.duration : -1);
        int startNumber = parseInt(xpp, "startNumber", parent != null ? parent.startNumber : 1);
        UrlTemplate mediaTemplate = parseUrlTemplate(xpp, "media", parent != null ? parent.mediaTemplate : null);
        UrlTemplate initializationTemplate = parseUrlTemplate(xpp, JoinPoint.INITIALIZATION, parent != null ? parent.initializationTemplate : null);
        RangedUri initialization = null;
        List<SegmentTimelineElement> timeline = null;
        do {
            xpp.next();
            if (ParserUtil.isStartTag(xpp, "Initialization")) {
                initialization = parseInitialization(xpp, baseUrl);
            } else {
                if (ParserUtil.isStartTag(xpp, "SegmentTimeline")) {
                    timeline = parseSegmentTimeline(xpp);
                }
            }
        } while (!ParserUtil.isEndTag(xpp, "SegmentTemplate"));
        if (parent != null) {
            if (initialization == null) {
                initialization = parent.initialization;
            }
            if (timeline == null) {
                timeline = parent.segmentTimeline;
            }
        }
        return buildSegmentTemplate(initialization, timescale, presentationTimeOffset, startNumber, duration, timeline, initializationTemplate, mediaTemplate, baseUrl);
    }

    protected SegmentTemplate buildSegmentTemplate(RangedUri initialization, long timescale, long presentationTimeOffset, int startNumber, long duration, List<SegmentTimelineElement> timeline, UrlTemplate initializationTemplate, UrlTemplate mediaTemplate, String baseUrl) {
        return new SegmentTemplate(initialization, timescale, presentationTimeOffset, startNumber, duration, timeline, initializationTemplate, mediaTemplate, baseUrl);
    }

    protected List<SegmentTimelineElement> parseSegmentTimeline(XmlPullParser xpp) throws XmlPullParserException, IOException {
        List<SegmentTimelineElement> segmentTimeline = new ArrayList();
        long elapsedTime = 0;
        do {
            xpp.next();
            if (ParserUtil.isStartTag(xpp, "S")) {
                elapsedTime = parseLong(xpp, "t", elapsedTime);
                long duration = parseLong(xpp, "d");
                int count = parseInt(xpp, "r", 0) + 1;
                for (int i = 0; i < count; i++) {
                    segmentTimeline.add(buildSegmentTimelineElement(elapsedTime, duration));
                    elapsedTime += duration;
                }
            }
        } while (!ParserUtil.isEndTag(xpp, "SegmentTimeline"));
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

    protected RangedUri parseInitialization(XmlPullParser xpp, String baseUrl) {
        return parseRangedUrl(xpp, baseUrl, "sourceURL", "range");
    }

    protected RangedUri parseSegmentUrl(XmlPullParser xpp, String baseUrl) {
        return parseRangedUrl(xpp, baseUrl, "media", "mediaRange");
    }

    protected RangedUri parseRangedUrl(XmlPullParser xpp, String baseUrl, String urlAttribute, String rangeAttribute) {
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
        return buildRangedUri(baseUrl, urlText, rangeStart, rangeLength);
    }

    protected RangedUri buildRangedUri(String baseUrl, String urlText, long rangeStart, long rangeLength) {
        return new RangedUri(baseUrl, urlText, rangeStart, rangeLength);
    }

    protected int parseAudioChannelConfiguration(XmlPullParser xpp) throws XmlPullParserException, IOException {
        int audioChannels;
        if ("urn:mpeg:dash:23003:3:audio_channel_configuration:2011".equals(parseString(xpp, "schemeIdUri", null))) {
            audioChannels = parseInt(xpp, Param.VALUE);
        } else {
            audioChannels = -1;
        }
        do {
            xpp.next();
        } while (!ParserUtil.isEndTag(xpp, "AudioChannelConfiguration"));
        return audioChannels;
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
        return value == null ? defaultValue : Util.parseXsDuration(value);
    }

    protected static long parseDateTime(XmlPullParser xpp, String name, long defaultValue) throws ParseException {
        String value = xpp.getAttributeValue(null, name);
        return value == null ? defaultValue : Util.parseXsDateTime(value);
    }

    protected static String parseBaseUrl(XmlPullParser xpp, String parentBaseUrl) throws XmlPullParserException, IOException {
        xpp.next();
        return UriUtil.resolve(parentBaseUrl, xpp.getText());
    }

    protected static int parseInt(XmlPullParser xpp, String name) {
        return parseInt(xpp, name, -1);
    }

    protected static int parseInt(XmlPullParser xpp, String name, int defaultValue) {
        String value = xpp.getAttributeValue(null, name);
        return value == null ? defaultValue : Integer.parseInt(value);
    }

    protected static long parseLong(XmlPullParser xpp, String name) {
        return parseLong(xpp, name, -1);
    }

    protected static long parseLong(XmlPullParser xpp, String name, long defaultValue) {
        String value = xpp.getAttributeValue(null, name);
        return value == null ? defaultValue : Long.parseLong(value);
    }

    protected static String parseString(XmlPullParser xpp, String name, String defaultValue) {
        String value = xpp.getAttributeValue(null, name);
        return value == null ? defaultValue : value;
    }
}
