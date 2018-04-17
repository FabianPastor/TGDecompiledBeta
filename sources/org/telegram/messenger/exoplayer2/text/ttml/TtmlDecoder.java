package org.telegram.messenger.exoplayer2.text.ttml;

import android.text.Layout.Alignment;
import android.util.Log;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.text.SimpleSubtitleDecoder;
import org.telegram.messenger.exoplayer2.text.SubtitleDecoderException;
import org.telegram.messenger.exoplayer2.util.ColorParser;
import org.telegram.messenger.exoplayer2.util.Util;
import org.telegram.messenger.exoplayer2.util.XmlPullParserUtil;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public final class TtmlDecoder extends SimpleSubtitleDecoder {
    private static final String ATTR_BEGIN = "begin";
    private static final String ATTR_DURATION = "dur";
    private static final String ATTR_END = "end";
    private static final String ATTR_REGION = "region";
    private static final String ATTR_STYLE = "style";
    private static final Pattern CLOCK_TIME = Pattern.compile("^([0-9][0-9]+):([0-9][0-9]):([0-9][0-9])(?:(\\.[0-9]+)|:([0-9][0-9])(?:\\.([0-9]+))?)?$");
    private static final FrameAndTickRate DEFAULT_FRAME_AND_TICK_RATE = new FrameAndTickRate(30.0f, 1, 1);
    private static final int DEFAULT_FRAME_RATE = 30;
    private static final Pattern FONT_SIZE = Pattern.compile("^(([0-9]*.)?[0-9]+)(px|em|%)$");
    private static final Pattern OFFSET_TIME = Pattern.compile("^([0-9]+(?:\\.[0-9]+)?)(h|m|s|ms|f|t)$");
    private static final Pattern PERCENTAGE_COORDINATES = Pattern.compile("^(\\d+\\.?\\d*?)% (\\d+\\.?\\d*?)%$");
    private static final String TAG = "TtmlDecoder";
    private static final String TTP = "http://www.w3.org/ns/ttml#parameter";
    private final XmlPullParserFactory xmlParserFactory;

    private static final class FrameAndTickRate {
        final float effectiveFrameRate;
        final int subFrameRate;
        final int tickRate;

        FrameAndTickRate(float effectiveFrameRate, int subFrameRate, int tickRate) {
            this.effectiveFrameRate = effectiveFrameRate;
            this.subFrameRate = subFrameRate;
            this.tickRate = tickRate;
        }
    }

    public TtmlDecoder() {
        super(TAG);
        try {
            this.xmlParserFactory = XmlPullParserFactory.newInstance();
            this.xmlParserFactory.setNamespaceAware(true);
        } catch (XmlPullParserException e) {
            throw new RuntimeException("Couldn't create XmlPullParserFactory instance", e);
        }
    }

    protected TtmlSubtitle decode(byte[] bytes, int length, boolean reset) throws SubtitleDecoderException {
        XmlPullParserException e;
        IOException e2;
        int i;
        try {
            XmlPullParser xmlParser = this.xmlParserFactory.newPullParser();
            Map<String, TtmlStyle> globalStyles = new HashMap();
            Map<String, TtmlRegion> regionMap = new HashMap();
            regionMap.put(TtmlNode.ANONYMOUS_REGION_ID, new TtmlRegion(null));
            try {
                ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes, 0, length);
                xmlParser.setInput(inputStream, null);
                TtmlSubtitle ttmlSubtitle = null;
                LinkedList<TtmlNode> nodeStack = new LinkedList();
                int unsupportedNodeDepth = 0;
                int eventType = xmlParser.getEventType();
                FrameAndTickRate frameAndTickRate = DEFAULT_FRAME_AND_TICK_RATE;
                while (eventType != 1) {
                    ByteArrayInputStream byteArrayInputStream;
                    TtmlNode parent = (TtmlNode) nodeStack.peekLast();
                    if (unsupportedNodeDepth == 0) {
                        String name = xmlParser.getName();
                        String name2;
                        if (eventType == 2) {
                            name2 = name;
                            if (TtmlNode.TAG_TT.equals(name2)) {
                                frameAndTickRate = parseFrameAndTickRates(xmlParser);
                            }
                            if (isSupportedTag(name2)) {
                                byteArrayInputStream = inputStream;
                                if (TtmlNode.TAG_HEAD.equals(name2) != null) {
                                    parseHeader(xmlParser, globalStyles, regionMap);
                                } else {
                                    try {
                                        inputStream = parseNode(xmlParser, parent, regionMap, frameAndTickRate);
                                        nodeStack.addLast(inputStream);
                                        if (parent != null) {
                                            parent.addChild(inputStream);
                                        }
                                    } catch (SubtitleDecoderException e3) {
                                        Log.w(TAG, "Suppressing parser error", e3);
                                        unsupportedNodeDepth++;
                                    }
                                }
                            } else {
                                String str = TAG;
                                byteArrayInputStream = inputStream;
                                inputStream = new StringBuilder();
                                inputStream.append("Ignoring unsupported tag: ");
                                inputStream.append(xmlParser.getName());
                                Log.i(str, inputStream.toString());
                                unsupportedNodeDepth++;
                            }
                        } else {
                            byteArrayInputStream = inputStream;
                            name2 = name;
                            if (eventType == 4) {
                                parent.addChild(TtmlNode.buildTextNode(xmlParser.getText()));
                            } else if (eventType == 3) {
                                if (xmlParser.getName().equals(TtmlNode.TAG_TT)) {
                                    ttmlSubtitle = new TtmlSubtitle((TtmlNode) nodeStack.getLast(), globalStyles, regionMap);
                                }
                                nodeStack.removeLast();
                            }
                        }
                    } else {
                        byteArrayInputStream = inputStream;
                        if (eventType == 2) {
                            unsupportedNodeDepth++;
                        } else if (eventType == 3) {
                            unsupportedNodeDepth--;
                        }
                    }
                    xmlParser.next();
                    eventType = xmlParser.getEventType();
                    inputStream = byteArrayInputStream;
                    byte[] bArr = bytes;
                }
                return ttmlSubtitle;
            } catch (XmlPullParserException e4) {
                e = e4;
            } catch (IOException e5) {
                e2 = e5;
            }
        } catch (XmlPullParserException e6) {
            e = e6;
            i = length;
            throw new SubtitleDecoderException("Unable to decode source", e);
        } catch (IOException e7) {
            e2 = e7;
            i = length;
            throw new IllegalStateException("Unexpected error when reading input.", e2);
        }
    }

    private FrameAndTickRate parseFrameAndTickRates(XmlPullParser xmlParser) throws SubtitleDecoderException {
        int frameRate = DEFAULT_FRAME_RATE;
        String frameRateString = xmlParser.getAttributeValue(TTP, "frameRate");
        if (frameRateString != null) {
            frameRate = Integer.parseInt(frameRateString);
        }
        float frameRateMultiplier = 1.0f;
        String frameRateMultiplierString = xmlParser.getAttributeValue(TTP, "frameRateMultiplier");
        if (frameRateMultiplierString != null) {
            String[] parts = frameRateMultiplierString.split(" ");
            if (parts.length != 2) {
                throw new SubtitleDecoderException("frameRateMultiplier doesn't have 2 parts");
            }
            frameRateMultiplier = ((float) Integer.parseInt(parts[0])) / ((float) Integer.parseInt(parts[1]));
        }
        int subFrameRate = DEFAULT_FRAME_AND_TICK_RATE.subFrameRate;
        String subFrameRateString = xmlParser.getAttributeValue(TTP, "subFrameRate");
        if (subFrameRateString != null) {
            subFrameRate = Integer.parseInt(subFrameRateString);
        }
        int tickRate = DEFAULT_FRAME_AND_TICK_RATE.tickRate;
        String tickRateString = xmlParser.getAttributeValue(TTP, "tickRate");
        if (tickRateString != null) {
            tickRate = Integer.parseInt(tickRateString);
        }
        return new FrameAndTickRate(((float) frameRate) * frameRateMultiplier, subFrameRate, tickRate);
    }

    private Map<String, TtmlStyle> parseHeader(XmlPullParser xmlParser, Map<String, TtmlStyle> globalStyles, Map<String, TtmlRegion> globalRegions) throws IOException, XmlPullParserException {
        do {
            xmlParser.next();
            if (XmlPullParserUtil.isStartTag(xmlParser, "style")) {
                String parentStyleId = XmlPullParserUtil.getAttributeValue(xmlParser, "style");
                TtmlStyle style = parseStyleAttributes(xmlParser, new TtmlStyle());
                if (parentStyleId != null) {
                    for (String id : parseStyleIds(parentStyleId)) {
                        style.chain((TtmlStyle) globalStyles.get(id));
                    }
                }
                if (style.getId() != null) {
                    globalStyles.put(style.getId(), style);
                }
            } else if (XmlPullParserUtil.isStartTag(xmlParser, "region")) {
                TtmlRegion ttmlRegion = parseRegionAttributes(xmlParser);
                if (ttmlRegion != null) {
                    globalRegions.put(ttmlRegion.id, ttmlRegion);
                }
            }
        } while (!XmlPullParserUtil.isEndTag(xmlParser, TtmlNode.TAG_HEAD));
        return globalStyles;
    }

    private TtmlRegion parseRegionAttributes(XmlPullParser xmlParser) {
        String str;
        StringBuilder stringBuilder;
        XmlPullParser xmlPullParser = xmlParser;
        String regionId = XmlPullParserUtil.getAttributeValue(xmlPullParser, TtmlNode.ATTR_ID);
        if (regionId == null) {
            return null;
        }
        String regionOrigin = XmlPullParserUtil.getAttributeValue(xmlPullParser, TtmlNode.ATTR_TTS_ORIGIN);
        if (regionOrigin != null) {
            Matcher originMatcher = PERCENTAGE_COORDINATES.matcher(regionOrigin);
            String str2;
            StringBuilder stringBuilder2;
            if (originMatcher.matches()) {
                int i = 1;
                try {
                    float position = Float.parseFloat(originMatcher.group(1)) / 100.0f;
                    float line = Float.parseFloat(originMatcher.group(2)) / 100.0f;
                    String regionExtent = XmlPullParserUtil.getAttributeValue(xmlPullParser, TtmlNode.ATTR_TTS_EXTENT);
                    if (regionExtent != null) {
                        originMatcher = PERCENTAGE_COORDINATES.matcher(regionExtent);
                        if (originMatcher.matches()) {
                            try {
                                float width = Float.parseFloat(originMatcher.group(1)) / 100.0f;
                                float height = Float.parseFloat(originMatcher.group(2)) / 100.0f;
                                int lineAnchor = 0;
                                String displayAlign = XmlPullParserUtil.getAttributeValue(xmlPullParser, TtmlNode.ATTR_TTS_DISPLAY_ALIGN);
                                if (displayAlign != null) {
                                    String toLowerInvariant = Util.toLowerInvariant(displayAlign);
                                    int hashCode = toLowerInvariant.hashCode();
                                    if (hashCode == -NUM) {
                                        if (toLowerInvariant.equals(TtmlNode.CENTER)) {
                                            i = 0;
                                            switch (i) {
                                                case 0:
                                                    lineAnchor = 1;
                                                    line += height / 2.0f;
                                                    break;
                                                case 1:
                                                    lineAnchor = 2;
                                                    line += height;
                                                    break;
                                                default:
                                                    break;
                                            }
                                        }
                                    } else if (hashCode == 92734940) {
                                        if (toLowerInvariant.equals("after")) {
                                            switch (i) {
                                                case 0:
                                                    lineAnchor = 1;
                                                    line += height / 2.0f;
                                                    break;
                                                case 1:
                                                    lineAnchor = 2;
                                                    line += height;
                                                    break;
                                                default:
                                                    break;
                                            }
                                        }
                                    }
                                    i = -1;
                                    switch (i) {
                                        case 0:
                                            lineAnchor = 1;
                                            line += height / 2.0f;
                                            break;
                                        case 1:
                                            lineAnchor = 2;
                                            line += height;
                                            break;
                                        default:
                                            break;
                                    }
                                }
                                return new TtmlRegion(regionId, position, line, 0, lineAnchor, width);
                            } catch (NumberFormatException e) {
                                str = TAG;
                                stringBuilder = new StringBuilder();
                                stringBuilder.append("Ignoring region with malformed extent: ");
                                stringBuilder.append(regionOrigin);
                                Log.w(str, stringBuilder.toString());
                                return null;
                            }
                        }
                        str2 = TAG;
                        stringBuilder2 = new StringBuilder();
                        stringBuilder2.append("Ignoring region with unsupported extent: ");
                        stringBuilder2.append(regionOrigin);
                        Log.w(str2, stringBuilder2.toString());
                        return null;
                    }
                    Log.w(TAG, "Ignoring region without an extent");
                    return null;
                } catch (NumberFormatException e2) {
                    NumberFormatException numberFormatException = e2;
                    str = TAG;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("Ignoring region with malformed origin: ");
                    stringBuilder.append(regionOrigin);
                    Log.w(str, stringBuilder.toString());
                    return null;
                }
            }
            str2 = TAG;
            stringBuilder2 = new StringBuilder();
            stringBuilder2.append("Ignoring region with unsupported origin: ");
            stringBuilder2.append(regionOrigin);
            Log.w(str2, stringBuilder2.toString());
            return null;
        }
        Log.w(TAG, "Ignoring region without an origin");
        return null;
    }

    private String[] parseStyleIds(String parentStyleIds) {
        return parentStyleIds.split("\\s+");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private TtmlStyle parseStyleAttributes(XmlPullParser parser, TtmlStyle style) {
        String str;
        StringBuilder stringBuilder;
        int attributeCount = parser.getAttributeCount();
        TtmlStyle style2 = style;
        for (int i = 0; i < attributeCount; i++) {
            boolean z;
            String attributeValue = parser.getAttributeValue(i);
            String attributeName = parser.getAttributeName(i);
            boolean z2 = true;
            boolean z3 = true;
            switch (attributeName.hashCode()) {
                case -1550943582:
                    if (attributeName.equals(TtmlNode.ATTR_TTS_FONT_STYLE)) {
                        z = true;
                        break;
                    }
                case -1224696685:
                    if (attributeName.equals(TtmlNode.ATTR_TTS_FONT_FAMILY)) {
                        z = true;
                        break;
                    }
                case -1065511464:
                    if (attributeName.equals(TtmlNode.ATTR_TTS_TEXT_ALIGN)) {
                        z = true;
                        break;
                    }
                case -879295043:
                    if (attributeName.equals(TtmlNode.ATTR_TTS_TEXT_DECORATION)) {
                        z = true;
                        break;
                    }
                case -734428249:
                    if (attributeName.equals(TtmlNode.ATTR_TTS_FONT_WEIGHT)) {
                        z = true;
                        break;
                    }
                case 3355:
                    if (attributeName.equals(TtmlNode.ATTR_ID)) {
                        z = false;
                        break;
                    }
                case 94842723:
                    if (attributeName.equals(TtmlNode.ATTR_TTS_COLOR)) {
                        z = true;
                        break;
                    }
                case 365601008:
                    if (attributeName.equals(TtmlNode.ATTR_TTS_FONT_SIZE)) {
                        z = true;
                        break;
                    }
                case 1287124693:
                    if (attributeName.equals(TtmlNode.ATTR_TTS_BACKGROUND_COLOR)) {
                        z = true;
                        break;
                    }
                default:
            }
            z = true;
            switch (z) {
                case false:
                    if (!"style".equals(parser.getName())) {
                        break;
                    }
                    style2 = createIfNull(style2).setId(attributeValue);
                    break;
                case true:
                    style2 = createIfNull(style2);
                    try {
                        style2.setBackgroundColor(ColorParser.parseTtmlColor(attributeValue));
                        break;
                    } catch (IllegalArgumentException e) {
                        str = TAG;
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("Failed parsing background value: ");
                        stringBuilder.append(attributeValue);
                        Log.w(str, stringBuilder.toString());
                        break;
                    }
                case true:
                    style2 = createIfNull(style2);
                    try {
                        style2.setFontColor(ColorParser.parseTtmlColor(attributeValue));
                        break;
                    } catch (IllegalArgumentException e2) {
                        str = TAG;
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("Failed parsing color value: ");
                        stringBuilder.append(attributeValue);
                        Log.w(str, stringBuilder.toString());
                        break;
                    }
                case true:
                    style2 = createIfNull(style2).setFontFamily(attributeValue);
                    break;
                case true:
                    try {
                        style2 = createIfNull(style2);
                        parseFontSize(attributeValue, style2);
                        break;
                    } catch (SubtitleDecoderException e3) {
                        str = TAG;
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("Failed parsing fontSize value: ");
                        stringBuilder.append(attributeValue);
                        Log.w(str, stringBuilder.toString());
                        break;
                    }
                case true:
                    style2 = createIfNull(style2).setBold(TtmlNode.BOLD.equalsIgnoreCase(attributeValue));
                    break;
                case true:
                    style2 = createIfNull(style2).setItalic(TtmlNode.ITALIC.equalsIgnoreCase(attributeValue));
                    break;
                case true:
                    attributeName = Util.toLowerInvariant(attributeValue);
                    switch (attributeName.hashCode()) {
                        case -1364013995:
                            if (attributeName.equals(TtmlNode.CENTER)) {
                                break;
                            }
                        case 100571:
                            if (attributeName.equals("end")) {
                                z2 = true;
                                break;
                            }
                        case 3317767:
                            if (attributeName.equals(TtmlNode.LEFT)) {
                                z2 = false;
                                break;
                            }
                        case 108511772:
                            if (attributeName.equals(TtmlNode.RIGHT)) {
                                z2 = true;
                                break;
                            }
                        case 109757538:
                            if (attributeName.equals(TtmlNode.START)) {
                                z2 = true;
                                break;
                            }
                        default:
                    }
                    z2 = true;
                    switch (z2) {
                        case false:
                            style2 = createIfNull(style2).setTextAlign(Alignment.ALIGN_NORMAL);
                            break;
                        case true:
                            style2 = createIfNull(style2).setTextAlign(Alignment.ALIGN_NORMAL);
                            break;
                        case true:
                            style2 = createIfNull(style2).setTextAlign(Alignment.ALIGN_OPPOSITE);
                            break;
                        case true:
                            style2 = createIfNull(style2).setTextAlign(Alignment.ALIGN_OPPOSITE);
                            break;
                        case true:
                            style2 = createIfNull(style2).setTextAlign(Alignment.ALIGN_CENTER);
                            break;
                        default:
                            break;
                    }
                    break;
                case true:
                    attributeName = Util.toLowerInvariant(attributeValue);
                    int hashCode = attributeName.hashCode();
                    if (hashCode == -NUM) {
                        if (attributeName.equals(TtmlNode.NO_UNDERLINE)) {
                            switch (z3) {
                                case false:
                                    style2 = createIfNull(style2).setLinethrough(true);
                                    break;
                                case true:
                                    style2 = createIfNull(style2).setLinethrough(false);
                                    break;
                                case true:
                                    style2 = createIfNull(style2).setUnderline(true);
                                    break;
                                case true:
                                    style2 = createIfNull(style2).setUnderline(false);
                                    break;
                                default:
                                    break;
                            }
                        }
                    } else if (hashCode == -NUM) {
                        if (attributeName.equals(TtmlNode.UNDERLINE)) {
                            z3 = true;
                            switch (z3) {
                                case false:
                                    style2 = createIfNull(style2).setLinethrough(true);
                                    break;
                                case true:
                                    style2 = createIfNull(style2).setLinethrough(false);
                                    break;
                                case true:
                                    style2 = createIfNull(style2).setUnderline(true);
                                    break;
                                case true:
                                    style2 = createIfNull(style2).setUnderline(false);
                                    break;
                                default:
                                    break;
                            }
                        }
                    } else if (hashCode == 913457136) {
                        if (attributeName.equals(TtmlNode.NO_LINETHROUGH)) {
                            z3 = true;
                            switch (z3) {
                                case false:
                                    style2 = createIfNull(style2).setLinethrough(true);
                                    break;
                                case true:
                                    style2 = createIfNull(style2).setLinethrough(false);
                                    break;
                                case true:
                                    style2 = createIfNull(style2).setUnderline(true);
                                    break;
                                case true:
                                    style2 = createIfNull(style2).setUnderline(false);
                                    break;
                                default:
                                    break;
                            }
                        }
                    } else if (hashCode == NUM) {
                        if (attributeName.equals(TtmlNode.LINETHROUGH)) {
                            z3 = false;
                            switch (z3) {
                                case false:
                                    style2 = createIfNull(style2).setLinethrough(true);
                                    break;
                                case true:
                                    style2 = createIfNull(style2).setLinethrough(false);
                                    break;
                                case true:
                                    style2 = createIfNull(style2).setUnderline(true);
                                    break;
                                case true:
                                    style2 = createIfNull(style2).setUnderline(false);
                                    break;
                                default:
                                    break;
                            }
                            break;
                        }
                    }
                    z3 = true;
                    switch (z3) {
                        case false:
                            style2 = createIfNull(style2).setLinethrough(true);
                            break;
                        case true:
                            style2 = createIfNull(style2).setLinethrough(false);
                            break;
                        case true:
                            style2 = createIfNull(style2).setUnderline(true);
                            break;
                        case true:
                            style2 = createIfNull(style2).setUnderline(false);
                            break;
                        default:
                            break;
                    }
                default:
                    break;
            }
        }
        return style2;
    }

    private TtmlStyle createIfNull(TtmlStyle style) {
        return style == null ? new TtmlStyle() : style;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private TtmlNode parseNode(XmlPullParser parser, TtmlNode parent, Map<String, TtmlRegion> regionMap, FrameAndTickRate frameAndTickRate) throws SubtitleDecoderException {
        long endTime;
        XmlPullParser xmlPullParser = parser;
        TtmlNode ttmlNode = parent;
        FrameAndTickRate frameAndTickRate2 = frameAndTickRate;
        long startTime = C0542C.TIME_UNSET;
        long endTime2 = C0542C.TIME_UNSET;
        String regionId = TtmlNode.ANONYMOUS_REGION_ID;
        int attributeCount = parser.getAttributeCount();
        TtmlStyle style = parseStyleAttributes(xmlPullParser, null);
        String regionId2 = regionId;
        String[] styleIds = null;
        long duration = C0542C.TIME_UNSET;
        int i = 0;
        while (i < attributeCount) {
            int attributeCount2;
            String attr = xmlPullParser.getAttributeName(i);
            String value = xmlPullParser.getAttributeValue(i);
            switch (attr.hashCode()) {
                case -934795532:
                    attributeCount2 = attributeCount;
                    if (attr.equals("region") != 0) {
                        attributeCount = 4;
                        break;
                    }
                case 99841:
                    attributeCount2 = attributeCount;
                    if (attr.equals(ATTR_DURATION) != 0) {
                        attributeCount = 2;
                        break;
                    }
                case 100571:
                    attributeCount2 = attributeCount;
                    if (attr.equals("end") != 0) {
                        attributeCount = 1;
                        break;
                    }
                case 93616297:
                    attributeCount2 = attributeCount;
                    if (attr.equals(ATTR_BEGIN) != 0) {
                        attributeCount = 0;
                        break;
                    }
                case 109780401:
                    attributeCount2 = attributeCount;
                    if (attr.equals("style") != 0) {
                        attributeCount = 3;
                        break;
                    }
                default:
                    attributeCount2 = attributeCount;
            }
            attributeCount = -1;
            switch (attributeCount) {
                case 0:
                    startTime = parseTimeExpression(value, frameAndTickRate2);
                    break;
                case 1:
                    endTime2 = parseTimeExpression(value, frameAndTickRate2);
                    break;
                case 2:
                    duration = parseTimeExpression(value, frameAndTickRate2);
                    break;
                case 3:
                    attributeCount = regionMap;
                    attributeCount = r0.parseStyleIds(value);
                    if (attributeCount.length <= 0) {
                        break;
                    }
                    styleIds = attributeCount;
                    break;
                case 4:
                    if (!regionMap.containsKey(value)) {
                        break;
                    }
                    regionId2 = value;
                    break;
                default:
                    break;
            }
            i++;
            attributeCount = attributeCount2;
            TtmlDecoder ttmlDecoder = this;
        }
        if (!(ttmlNode == null || ttmlNode.startTimeUs == C0542C.TIME_UNSET)) {
            long startTime2;
            if (startTime != C0542C.TIME_UNSET) {
                startTime2 = startTime + ttmlNode.startTimeUs;
            } else {
                startTime2 = startTime;
            }
            if (endTime2 != C0542C.TIME_UNSET) {
                endTime2 += ttmlNode.startTimeUs;
            }
            startTime = startTime2;
        }
        if (endTime2 == C0542C.TIME_UNSET) {
            if (duration != C0542C.TIME_UNSET) {
                endTime = startTime + duration;
            } else if (!(ttmlNode == null || ttmlNode.endTimeUs == C0542C.TIME_UNSET)) {
                endTime = ttmlNode.endTimeUs;
            }
            return TtmlNode.buildNode(parser.getName(), startTime, endTime, style, styleIds, regionId2);
        }
        endTime = endTime2;
        return TtmlNode.buildNode(parser.getName(), startTime, endTime, style, styleIds, regionId2);
    }

    private static boolean isSupportedTag(String tag) {
        if (!(tag.equals(TtmlNode.TAG_TT) || tag.equals(TtmlNode.TAG_HEAD) || tag.equals(TtmlNode.TAG_BODY) || tag.equals(TtmlNode.TAG_DIV) || tag.equals(TtmlNode.TAG_P) || tag.equals(TtmlNode.TAG_SPAN) || tag.equals(TtmlNode.TAG_BR) || tag.equals("style") || tag.equals(TtmlNode.TAG_STYLING) || tag.equals(TtmlNode.TAG_LAYOUT) || tag.equals("region") || tag.equals(TtmlNode.TAG_METADATA) || tag.equals(TtmlNode.TAG_SMPTE_IMAGE) || tag.equals(TtmlNode.TAG_SMPTE_DATA))) {
            if (!tag.equals(TtmlNode.TAG_SMPTE_INFORMATION)) {
                return false;
            }
        }
        return true;
    }

    private static void parseFontSize(String expression, TtmlStyle out) throws SubtitleDecoderException {
        Matcher matcher;
        String[] expressions = expression.split("\\s+");
        if (expressions.length == 1) {
            matcher = FONT_SIZE.matcher(expression);
        } else if (expressions.length == 2) {
            matcher = FONT_SIZE.matcher(expressions[1]);
            Log.w(TAG, "Multiple values in fontSize attribute. Picking the second value for vertical font size and ignoring the first.");
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Invalid number of entries for fontSize: ");
            stringBuilder.append(expressions.length);
            stringBuilder.append(".");
            throw new SubtitleDecoderException(stringBuilder.toString());
        }
        if (matcher.matches()) {
            String unit = matcher.group(3);
            int i = -1;
            int hashCode = unit.hashCode();
            if (hashCode != 37) {
                if (hashCode != 3240) {
                    if (hashCode == 3592) {
                        if (unit.equals("px")) {
                            i = 0;
                        }
                    }
                } else if (unit.equals("em")) {
                    i = 1;
                }
            } else if (unit.equals("%")) {
                i = 2;
            }
            switch (i) {
                case 0:
                    out.setFontSizeUnit(1);
                    break;
                case 1:
                    out.setFontSizeUnit(2);
                    break;
                case 2:
                    out.setFontSizeUnit(3);
                    break;
                default:
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("Invalid unit for fontSize: '");
                    stringBuilder.append(unit);
                    stringBuilder.append("'.");
                    throw new SubtitleDecoderException(stringBuilder.toString());
            }
            out.setFontSize(Float.valueOf(matcher.group(1)).floatValue());
            return;
        }
        stringBuilder = new StringBuilder();
        stringBuilder.append("Invalid expression for fontSize: '");
        stringBuilder.append(expression);
        stringBuilder.append("'.");
        throw new SubtitleDecoderException(stringBuilder.toString());
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static long parseTimeExpression(String time, FrameAndTickRate frameAndTickRate) throws SubtitleDecoderException {
        String str = time;
        FrameAndTickRate frameAndTickRate2 = frameAndTickRate;
        Matcher matcher = CLOCK_TIME.matcher(str);
        int i = 5;
        if (matcher.matches()) {
            double durationSeconds = (((double) (Long.parseLong(matcher.group(1)) * 3600)) + ((double) (Long.parseLong(matcher.group(2)) * 60))) + ((double) Long.parseLong(matcher.group(3)));
            String fraction = matcher.group(4);
            double d = 0.0d;
            durationSeconds += fraction != null ? Double.parseDouble(fraction) : 0.0d;
            String frames = matcher.group(5);
            durationSeconds += frames != null ? (double) (((float) Long.parseLong(frames)) / frameAndTickRate2.effectiveFrameRate) : 0.0d;
            String subframes = matcher.group(6);
            if (subframes != null) {
                d = (((double) Long.parseLong(subframes)) / ((double) frameAndTickRate2.subFrameRate)) / ((double) frameAndTickRate2.effectiveFrameRate);
            }
            return (long) (1000000.0d * (durationSeconds + d));
        }
        matcher = OFFSET_TIME.matcher(str);
        if (matcher.matches()) {
            double offsetSeconds = Double.parseDouble(matcher.group(1));
            String unit = matcher.group(2);
            int hashCode = unit.hashCode();
            if (hashCode != 102) {
                if (hashCode != 104) {
                    if (hashCode != 109) {
                        if (hashCode != 3494) {
                            switch (hashCode) {
                                case 115:
                                    if (unit.equals("s")) {
                                        i = 2;
                                        break;
                                    }
                                case 116:
                                    if (unit.equals("t")) {
                                        break;
                                    }
                                default:
                            }
                        } else if (unit.equals("ms")) {
                            i = 3;
                            switch (i) {
                                case 0:
                                    offsetSeconds *= 3600.0d;
                                    break;
                                case 1:
                                    offsetSeconds *= 60.0d;
                                    break;
                                case 2:
                                    break;
                                case 3:
                                    offsetSeconds /= 1000.0d;
                                    break;
                                case 4:
                                    offsetSeconds /= (double) frameAndTickRate2.effectiveFrameRate;
                                    break;
                                case 5:
                                    offsetSeconds /= (double) frameAndTickRate2.tickRate;
                                    break;
                                default:
                                    break;
                            }
                            return (long) (1000000.0d * offsetSeconds);
                        }
                    } else if (unit.equals("m")) {
                        i = 1;
                        switch (i) {
                            case 0:
                                offsetSeconds *= 3600.0d;
                                break;
                            case 1:
                                offsetSeconds *= 60.0d;
                                break;
                            case 2:
                                break;
                            case 3:
                                offsetSeconds /= 1000.0d;
                                break;
                            case 4:
                                offsetSeconds /= (double) frameAndTickRate2.effectiveFrameRate;
                                break;
                            case 5:
                                offsetSeconds /= (double) frameAndTickRate2.tickRate;
                                break;
                            default:
                                break;
                        }
                        return (long) (1000000.0d * offsetSeconds);
                    }
                } else if (unit.equals("h")) {
                    i = 0;
                    switch (i) {
                        case 0:
                            offsetSeconds *= 3600.0d;
                            break;
                        case 1:
                            offsetSeconds *= 60.0d;
                            break;
                        case 2:
                            break;
                        case 3:
                            offsetSeconds /= 1000.0d;
                            break;
                        case 4:
                            offsetSeconds /= (double) frameAndTickRate2.effectiveFrameRate;
                            break;
                        case 5:
                            offsetSeconds /= (double) frameAndTickRate2.tickRate;
                            break;
                        default:
                            break;
                    }
                    return (long) (1000000.0d * offsetSeconds);
                }
            } else if (unit.equals("f")) {
                i = 4;
                switch (i) {
                    case 0:
                        offsetSeconds *= 3600.0d;
                        break;
                    case 1:
                        offsetSeconds *= 60.0d;
                        break;
                    case 2:
                        break;
                    case 3:
                        offsetSeconds /= 1000.0d;
                        break;
                    case 4:
                        offsetSeconds /= (double) frameAndTickRate2.effectiveFrameRate;
                        break;
                    case 5:
                        offsetSeconds /= (double) frameAndTickRate2.tickRate;
                        break;
                    default:
                        break;
                }
                return (long) (1000000.0d * offsetSeconds);
            }
            i = -1;
            switch (i) {
                case 0:
                    offsetSeconds *= 3600.0d;
                    break;
                case 1:
                    offsetSeconds *= 60.0d;
                    break;
                case 2:
                    break;
                case 3:
                    offsetSeconds /= 1000.0d;
                    break;
                case 4:
                    offsetSeconds /= (double) frameAndTickRate2.effectiveFrameRate;
                    break;
                case 5:
                    offsetSeconds /= (double) frameAndTickRate2.tickRate;
                    break;
                default:
                    break;
            }
            return (long) (1000000.0d * offsetSeconds);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Malformed time expression: ");
        stringBuilder.append(str);
        throw new SubtitleDecoderException(stringBuilder.toString());
    }
}
