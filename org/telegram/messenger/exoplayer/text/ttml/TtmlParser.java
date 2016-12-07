package org.telegram.messenger.exoplayer.text.ttml;

import android.text.Layout.Alignment;
import android.util.Log;
import android.util.Pair;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.messenger.exoplayer.ParserException;
import org.telegram.messenger.exoplayer.text.Cue;
import org.telegram.messenger.exoplayer.text.SubtitleParser;
import org.telegram.messenger.exoplayer.util.MimeTypes;
import org.telegram.messenger.exoplayer.util.ParserUtil;
import org.telegram.messenger.exoplayer.util.Util;
import org.telegram.messenger.volley.DefaultRetryPolicy;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public final class TtmlParser implements SubtitleParser {
    private static final String ATTR_BEGIN = "begin";
    private static final String ATTR_DURATION = "dur";
    private static final String ATTR_END = "end";
    private static final String ATTR_REGION = "region";
    private static final String ATTR_STYLE = "style";
    private static final Pattern CLOCK_TIME = Pattern.compile("^([0-9][0-9]+):([0-9][0-9]):([0-9][0-9])(?:(\\.[0-9]+)|:([0-9][0-9])(?:\\.([0-9]+))?)?$");
    private static final FrameAndTickRate DEFAULT_FRAME_AND_TICK_RATE = new FrameAndTickRate(BitmapDescriptorFactory.HUE_ORANGE, 1, 1);
    private static final int DEFAULT_FRAME_RATE = 30;
    private static final Pattern FONT_SIZE = Pattern.compile("^(([0-9]*.)?[0-9]+)(px|em|%)$");
    private static final Pattern OFFSET_TIME = Pattern.compile("^([0-9]+(?:\\.[0-9]+)?)(h|m|s|ms|f|t)$");
    private static final Pattern PERCENTAGE_COORDINATES = Pattern.compile("^(\\d+\\.?\\d*?)% (\\d+\\.?\\d*?)%$");
    private static final String TAG = "TtmlParser";
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

    public TtmlParser() {
        try {
            this.xmlParserFactory = XmlPullParserFactory.newInstance();
            this.xmlParserFactory.setNamespaceAware(true);
        } catch (XmlPullParserException e) {
            throw new RuntimeException("Couldn't create XmlPullParserFactory instance", e);
        }
    }

    public boolean canParse(String mimeType) {
        return MimeTypes.APPLICATION_TTML.equals(mimeType);
    }

    public TtmlSubtitle parse(byte[] bytes, int offset, int length) throws ParserException {
        try {
            XmlPullParser xmlParser = this.xmlParserFactory.newPullParser();
            Map<String, TtmlStyle> globalStyles = new HashMap();
            Map<String, TtmlRegion> regionMap = new HashMap();
            regionMap.put("", new TtmlRegion());
            xmlParser.setInput(new ByteArrayInputStream(bytes, offset, length), null);
            TtmlSubtitle ttmlSubtitle = null;
            LinkedList<TtmlNode> nodeStack = new LinkedList();
            int unsupportedNodeDepth = 0;
            FrameAndTickRate frameAndTickRate = DEFAULT_FRAME_AND_TICK_RATE;
            for (int eventType = xmlParser.getEventType(); eventType != 1; eventType = xmlParser.getEventType()) {
                TtmlNode parent = (TtmlNode) nodeStack.peekLast();
                if (unsupportedNodeDepth == 0) {
                    String name = xmlParser.getName();
                    if (eventType == 2) {
                        if (TtmlNode.TAG_TT.equals(name)) {
                            frameAndTickRate = parseFrameAndTickRates(xmlParser);
                        }
                        if (!isSupportedTag(name)) {
                            Log.i(TAG, "Ignoring unsupported tag: " + xmlParser.getName());
                            unsupportedNodeDepth++;
                        } else if (TtmlNode.TAG_HEAD.equals(name)) {
                            parseHeader(xmlParser, globalStyles, regionMap);
                        } else {
                            try {
                                TtmlNode node = parseNode(xmlParser, parent, regionMap, frameAndTickRate);
                                nodeStack.addLast(node);
                                if (parent != null) {
                                    parent.addChild(node);
                                }
                            } catch (ParserException e) {
                                Log.w(TAG, "Suppressing parser error", e);
                                unsupportedNodeDepth++;
                            }
                        }
                    } else if (eventType == 4) {
                        parent.addChild(TtmlNode.buildTextNode(xmlParser.getText()));
                    } else if (eventType == 3) {
                        if (xmlParser.getName().equals(TtmlNode.TAG_TT)) {
                            ttmlSubtitle = new TtmlSubtitle((TtmlNode) nodeStack.getLast(), globalStyles, regionMap);
                        }
                        nodeStack.removeLast();
                    } else {
                        continue;
                    }
                } else if (eventType == 2) {
                    unsupportedNodeDepth++;
                } else if (eventType == 3) {
                    unsupportedNodeDepth--;
                }
                xmlParser.next();
            }
            return ttmlSubtitle;
        } catch (Throwable xppe) {
            throw new ParserException("Unable to parse source", xppe);
        } catch (IOException e2) {
            throw new IllegalStateException("Unexpected error when reading input.", e2);
        }
    }

    private FrameAndTickRate parseFrameAndTickRates(XmlPullParser xmlParser) throws ParserException {
        int frameRate = 30;
        String frameRateStr = xmlParser.getAttributeValue(TTP, "frameRate");
        if (frameRateStr != null) {
            frameRate = Integer.parseInt(frameRateStr);
        }
        float frameRateMultiplier = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        String frameRateMultiplierStr = xmlParser.getAttributeValue(TTP, "frameRateMultiplier");
        if (frameRateMultiplierStr != null) {
            String[] parts = frameRateMultiplierStr.split(" ");
            if (parts.length != 2) {
                throw new ParserException("frameRateMultiplier doesn't have 2 parts");
            }
            frameRateMultiplier = ((float) Integer.parseInt(parts[0])) / ((float) Integer.parseInt(parts[1]));
        }
        int subFrameRate = DEFAULT_FRAME_AND_TICK_RATE.subFrameRate;
        String subFrameRateStr = xmlParser.getAttributeValue(TTP, "subFrameRate");
        if (subFrameRateStr != null) {
            subFrameRate = Integer.parseInt(subFrameRateStr);
        }
        int tickRate = DEFAULT_FRAME_AND_TICK_RATE.tickRate;
        String tickRateStr = xmlParser.getAttributeValue(TTP, "tickRate");
        if (tickRateStr != null) {
            tickRate = Integer.parseInt(tickRateStr);
        }
        return new FrameAndTickRate(((float) frameRate) * frameRateMultiplier, subFrameRate, tickRate);
    }

    private Map<String, TtmlStyle> parseHeader(XmlPullParser xmlParser, Map<String, TtmlStyle> globalStyles, Map<String, TtmlRegion> globalRegions) throws IOException, XmlPullParserException {
        do {
            xmlParser.next();
            if (ParserUtil.isStartTag(xmlParser, "style")) {
                String parentStyleId = ParserUtil.getAttributeValue(xmlParser, "style");
                TtmlStyle style = parseStyleAttributes(xmlParser, new TtmlStyle());
                if (parentStyleId != null) {
                    String[] ids = parseStyleIds(parentStyleId);
                    for (Object obj : ids) {
                        style.chain((TtmlStyle) globalStyles.get(obj));
                    }
                }
                if (style.getId() != null) {
                    globalStyles.put(style.getId(), style);
                }
            } else if (ParserUtil.isStartTag(xmlParser, "region")) {
                Pair<String, TtmlRegion> ttmlRegionInfo = parseRegionAttributes(xmlParser);
                if (ttmlRegionInfo != null) {
                    globalRegions.put(ttmlRegionInfo.first, ttmlRegionInfo.second);
                }
            }
        } while (!ParserUtil.isEndTag(xmlParser, TtmlNode.TAG_HEAD));
        return globalStyles;
    }

    private Pair<String, TtmlRegion> parseRegionAttributes(XmlPullParser xmlParser) {
        String regionId = ParserUtil.getAttributeValue(xmlParser, TtmlNode.ATTR_ID);
        String regionOrigin = ParserUtil.getAttributeValue(xmlParser, "origin");
        String regionExtent = ParserUtil.getAttributeValue(xmlParser, TtmlNode.ATTR_TTS_EXTENT);
        if (regionOrigin == null || regionId == null) {
            return null;
        }
        float position = Cue.DIMEN_UNSET;
        float line = Cue.DIMEN_UNSET;
        Matcher originMatcher = PERCENTAGE_COORDINATES.matcher(regionOrigin);
        if (originMatcher.matches()) {
            try {
                position = Float.parseFloat(originMatcher.group(1)) / 100.0f;
                line = Float.parseFloat(originMatcher.group(2)) / 100.0f;
            } catch (NumberFormatException e) {
                Log.w(TAG, "Ignoring region with malformed origin: '" + regionOrigin + "'", e);
                position = Cue.DIMEN_UNSET;
            }
        }
        float width = Cue.DIMEN_UNSET;
        if (regionExtent != null) {
            Matcher extentMatcher = PERCENTAGE_COORDINATES.matcher(regionExtent);
            if (extentMatcher.matches()) {
                try {
                    width = Float.parseFloat(extentMatcher.group(1)) / 100.0f;
                } catch (NumberFormatException e2) {
                    Log.w(TAG, "Ignoring malformed region extent: '" + regionExtent + "'", e2);
                }
            }
        }
        if (position != Cue.DIMEN_UNSET) {
            return new Pair(regionId, new TtmlRegion(position, line, 0, width));
        }
        return null;
    }

    private String[] parseStyleIds(String parentStyleIds) {
        return parentStyleIds.split("\\s+");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private TtmlStyle parseStyleAttributes(XmlPullParser parser, TtmlStyle style) {
        int attributeCount = parser.getAttributeCount();
        for (int i = 0; i < attributeCount; i++) {
            boolean z;
            String attributeValue = parser.getAttributeValue(i);
            String attributeName = parser.getAttributeName(i);
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
                    z = true;
                    break;
            }
            switch (z) {
                case false:
                    if (!"style".equals(parser.getName())) {
                        break;
                    }
                    style = createIfNull(style).setId(attributeValue);
                    break;
                case true:
                    style = createIfNull(style);
                    try {
                        style.setBackgroundColor(TtmlColorParser.parseColor(attributeValue));
                        break;
                    } catch (IllegalArgumentException e) {
                        Log.w(TAG, "failed parsing background value: '" + attributeValue + "'");
                        break;
                    }
                case true:
                    style = createIfNull(style);
                    try {
                        style.setFontColor(TtmlColorParser.parseColor(attributeValue));
                        break;
                    } catch (IllegalArgumentException e2) {
                        Log.w(TAG, "failed parsing color value: '" + attributeValue + "'");
                        break;
                    }
                case true:
                    style = createIfNull(style).setFontFamily(attributeValue);
                    break;
                case true:
                    try {
                        style = createIfNull(style);
                        parseFontSize(attributeValue, style);
                        break;
                    } catch (ParserException e3) {
                        Log.w(TAG, "failed parsing fontSize value: '" + attributeValue + "'");
                        break;
                    }
                case true:
                    style = createIfNull(style).setBold(TtmlNode.BOLD.equalsIgnoreCase(attributeValue));
                    break;
                case true:
                    style = createIfNull(style).setItalic(TtmlNode.ITALIC.equalsIgnoreCase(attributeValue));
                    break;
                case true:
                    attributeName = Util.toLowerInvariant(attributeValue);
                    switch (attributeName.hashCode()) {
                        case -1364013995:
                            if (attributeName.equals(TtmlNode.CENTER)) {
                                z = true;
                                break;
                            }
                        case 100571:
                            if (attributeName.equals("end")) {
                                z = true;
                                break;
                            }
                        case 3317767:
                            if (attributeName.equals(TtmlNode.LEFT)) {
                                z = false;
                                break;
                            }
                        case 108511772:
                            if (attributeName.equals(TtmlNode.RIGHT)) {
                                z = true;
                                break;
                            }
                        case 109757538:
                            if (attributeName.equals(TtmlNode.START)) {
                                z = true;
                                break;
                            }
                        default:
                            z = true;
                            break;
                    }
                    switch (z) {
                        case false:
                            style = createIfNull(style).setTextAlign(Alignment.ALIGN_NORMAL);
                            break;
                        case true:
                            style = createIfNull(style).setTextAlign(Alignment.ALIGN_NORMAL);
                            break;
                        case true:
                            style = createIfNull(style).setTextAlign(Alignment.ALIGN_OPPOSITE);
                            break;
                        case true:
                            style = createIfNull(style).setTextAlign(Alignment.ALIGN_OPPOSITE);
                            break;
                        case true:
                            style = createIfNull(style).setTextAlign(Alignment.ALIGN_CENTER);
                            break;
                        default:
                            break;
                    }
                case true:
                    attributeName = Util.toLowerInvariant(attributeValue);
                    switch (attributeName.hashCode()) {
                        case -1461280213:
                            if (attributeName.equals(TtmlNode.NO_UNDERLINE)) {
                                z = true;
                                break;
                            }
                        case -1026963764:
                            if (attributeName.equals(TtmlNode.UNDERLINE)) {
                                z = true;
                                break;
                            }
                        case 913457136:
                            if (attributeName.equals(TtmlNode.NO_LINETHROUGH)) {
                                z = true;
                                break;
                            }
                        case 1679736913:
                            if (attributeName.equals(TtmlNode.LINETHROUGH)) {
                                z = false;
                                break;
                            }
                        default:
                            z = true;
                            break;
                    }
                    switch (z) {
                        case false:
                            style = createIfNull(style).setLinethrough(true);
                            break;
                        case true:
                            style = createIfNull(style).setLinethrough(false);
                            break;
                        case true:
                            style = createIfNull(style).setUnderline(true);
                            break;
                        case true:
                            style = createIfNull(style).setUnderline(false);
                            break;
                        default:
                            break;
                    }
                default:
                    break;
            }
        }
        return style;
    }

    private TtmlStyle createIfNull(TtmlStyle style) {
        return style == null ? new TtmlStyle() : style;
    }

    private TtmlNode parseNode(XmlPullParser parser, TtmlNode parent, Map<String, TtmlRegion> regionMap, FrameAndTickRate frameAndTickRate) throws ParserException {
        long duration = 0;
        long startTime = -1;
        long endTime = -1;
        String regionId = "";
        String[] styleIds = null;
        int attributeCount = parser.getAttributeCount();
        TtmlStyle style = parseStyleAttributes(parser, null);
        for (int i = 0; i < attributeCount; i++) {
            String attr = parser.getAttributeName(i);
            String value = parser.getAttributeValue(i);
            if (ATTR_BEGIN.equals(attr)) {
                startTime = parseTimeExpression(value, frameAndTickRate);
            } else if ("end".equals(attr)) {
                endTime = parseTimeExpression(value, frameAndTickRate);
            } else if (ATTR_DURATION.equals(attr)) {
                duration = parseTimeExpression(value, frameAndTickRate);
            } else if ("style".equals(attr)) {
                String[] ids = parseStyleIds(value);
                if (ids.length > 0) {
                    styleIds = ids;
                }
            } else if ("region".equals(attr) && regionMap.containsKey(value)) {
                regionId = value;
            }
        }
        if (!(parent == null || parent.startTimeUs == -1)) {
            if (startTime != -1) {
                startTime += parent.startTimeUs;
            }
            if (endTime != -1) {
                endTime += parent.startTimeUs;
            }
        }
        if (endTime == -1) {
            if (duration > 0) {
                endTime = startTime + duration;
            } else if (!(parent == null || parent.endTimeUs == -1)) {
                endTime = parent.endTimeUs;
            }
        }
        return TtmlNode.buildNode(parser.getName(), startTime, endTime, style, styleIds, regionId);
    }

    private static boolean isSupportedTag(String tag) {
        if (tag.equals(TtmlNode.TAG_TT) || tag.equals(TtmlNode.TAG_HEAD) || tag.equals(TtmlNode.TAG_BODY) || tag.equals(TtmlNode.TAG_DIV) || tag.equals(TtmlNode.TAG_P) || tag.equals(TtmlNode.TAG_SPAN) || tag.equals(TtmlNode.TAG_BR) || tag.equals("style") || tag.equals(TtmlNode.TAG_STYLING) || tag.equals(TtmlNode.TAG_LAYOUT) || tag.equals("region") || tag.equals(TtmlNode.TAG_METADATA) || tag.equals(TtmlNode.TAG_SMPTE_IMAGE) || tag.equals(TtmlNode.TAG_SMPTE_DATA) || tag.equals(TtmlNode.TAG_SMPTE_INFORMATION)) {
            return true;
        }
        return false;
    }

    private static void parseFontSize(String expression, TtmlStyle out) throws ParserException {
        Matcher matcher;
        String[] expressions = expression.split("\\s+");
        if (expressions.length == 1) {
            matcher = FONT_SIZE.matcher(expression);
        } else if (expressions.length == 2) {
            matcher = FONT_SIZE.matcher(expressions[1]);
            Log.w(TAG, "Multiple values in fontSize attribute. Picking the second value for vertical font size and ignoring the first.");
        } else {
            throw new ParserException("Invalid number of entries for fontSize: " + expressions.length + ".");
        }
        if (matcher.matches()) {
            String unit = matcher.group(3);
            int i = -1;
            switch (unit.hashCode()) {
                case 37:
                    if (unit.equals("%")) {
                        i = 2;
                        break;
                    }
                    break;
                case 3240:
                    if (unit.equals("em")) {
                        i = 1;
                        break;
                    }
                    break;
                case 3592:
                    if (unit.equals("px")) {
                        i = 0;
                        break;
                    }
                    break;
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
                    throw new ParserException("Invalid unit for fontSize: '" + unit + "'.");
            }
            out.setFontSize(Float.valueOf(matcher.group(1)).floatValue());
            return;
        }
        throw new ParserException("Invalid expression for fontSize: '" + expression + "'.");
    }

    private static long parseTimeExpression(String time, FrameAndTickRate frameAndTickRate) throws ParserException {
        Matcher matcher = CLOCK_TIME.matcher(time);
        if (matcher.matches()) {
            double durationSeconds = (((double) (Long.parseLong(matcher.group(1)) * 3600)) + ((double) (Long.parseLong(matcher.group(2)) * 60))) + ((double) Long.parseLong(matcher.group(3)));
            String fraction = matcher.group(4);
            durationSeconds += fraction != null ? Double.parseDouble(fraction) : 0.0d;
            String frames = matcher.group(5);
            durationSeconds += frames != null ? (double) (((float) Long.parseLong(frames)) / frameAndTickRate.effectiveFrameRate) : 0.0d;
            String subframes = matcher.group(6);
            return (long) (1000000.0d * (durationSeconds + (subframes != null ? (((double) Long.parseLong(subframes)) / ((double) frameAndTickRate.subFrameRate)) / ((double) frameAndTickRate.effectiveFrameRate) : 0.0d)));
        }
        matcher = OFFSET_TIME.matcher(time);
        if (matcher.matches()) {
            double offsetSeconds = Double.parseDouble(matcher.group(1));
            String unit = matcher.group(2);
            if (unit.equals("h")) {
                offsetSeconds *= 3600.0d;
            } else if (unit.equals("m")) {
                offsetSeconds *= 60.0d;
            } else if (!unit.equals("s")) {
                if (unit.equals("ms")) {
                    offsetSeconds /= 1000.0d;
                } else if (unit.equals("f")) {
                    offsetSeconds /= (double) frameAndTickRate.effectiveFrameRate;
                } else if (unit.equals("t")) {
                    offsetSeconds /= (double) frameAndTickRate.tickRate;
                }
            }
            return (long) (1000000.0d * offsetSeconds);
        }
        throw new ParserException("Malformed time expression: " + time);
    }
}
