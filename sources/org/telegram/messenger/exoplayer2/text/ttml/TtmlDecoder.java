package org.telegram.messenger.exoplayer2.text.ttml;

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

        FrameAndTickRate(float f, int i, int i2) {
            this.effectiveFrameRate = f;
            this.subFrameRate = i;
            this.tickRate = i2;
        }
    }

    public TtmlDecoder() {
        super(TAG);
        try {
            this.xmlParserFactory = XmlPullParserFactory.newInstance();
            this.xmlParserFactory.setNamespaceAware(true);
        } catch (Throwable e) {
            throw new RuntimeException("Couldn't create XmlPullParserFactory instance", e);
        }
    }

    protected TtmlSubtitle decode(byte[] bArr, int i, boolean z) throws SubtitleDecoderException {
        try {
            z = this.xmlParserFactory.newPullParser();
            Map hashMap = new HashMap();
            Map hashMap2 = new HashMap();
            TtmlSubtitle ttmlSubtitle = null;
            hashMap2.put(TtmlNode.ANONYMOUS_REGION_ID, new TtmlRegion(null));
            int i2 = 0;
            z.setInput(new ByteArrayInputStream(bArr, 0, i), null);
            bArr = new LinkedList();
            FrameAndTickRate frameAndTickRate = DEFAULT_FRAME_AND_TICK_RATE;
            for (i = z.getEventType(); i != 1; i = z.getEventType()) {
                TtmlNode ttmlNode = (TtmlNode) bArr.peekLast();
                if (i2 == 0) {
                    String name = z.getName();
                    if (i == 2) {
                        if (TtmlNode.TAG_TT.equals(name) != 0) {
                            frameAndTickRate = parseFrameAndTickRates(z);
                        }
                        if (isSupportedTag(name) == 0) {
                            i = TAG;
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("Ignoring unsupported tag: ");
                            stringBuilder.append(z.getName());
                            Log.i(i, stringBuilder.toString());
                            i2++;
                        } else if (TtmlNode.TAG_HEAD.equals(name) != 0) {
                            parseHeader(z, hashMap, hashMap2);
                        } else {
                            try {
                                i = parseNode(z, ttmlNode, hashMap2, frameAndTickRate);
                                bArr.addLast(i);
                                if (ttmlNode != null) {
                                    ttmlNode.addChild(i);
                                }
                            } catch (int i3) {
                                Log.w(TAG, "Suppressing parser error", i3);
                                i2++;
                            }
                        }
                    } else if (i3 == 4) {
                        ttmlNode.addChild(TtmlNode.buildTextNode(z.getText()));
                    } else if (i3 == 3) {
                        if (z.getName().equals(TtmlNode.TAG_TT) != 0) {
                            ttmlSubtitle = new TtmlSubtitle((TtmlNode) bArr.getLast(), hashMap, hashMap2);
                        }
                        bArr.removeLast();
                    }
                } else if (i3 == 2) {
                    i2++;
                } else if (i3 == 3) {
                    i2--;
                }
                z.next();
            }
            return ttmlSubtitle;
        } catch (byte[] bArr2) {
            throw new SubtitleDecoderException("Unable to decode source", bArr2);
        } catch (byte[] bArr22) {
            throw new IllegalStateException("Unexpected error when reading input.", bArr22);
        }
    }

    private FrameAndTickRate parseFrameAndTickRates(XmlPullParser xmlPullParser) throws SubtitleDecoderException {
        String attributeValue = xmlPullParser.getAttributeValue(TTP, "frameRate");
        int parseInt = attributeValue != null ? Integer.parseInt(attributeValue) : DEFAULT_FRAME_RATE;
        float f = 1.0f;
        String attributeValue2 = xmlPullParser.getAttributeValue(TTP, "frameRateMultiplier");
        if (attributeValue2 != null) {
            String[] split = attributeValue2.split(" ");
            if (split.length != 2) {
                throw new SubtitleDecoderException("frameRateMultiplier doesn't have 2 parts");
            }
            f = ((float) Integer.parseInt(split[0])) / ((float) Integer.parseInt(split[1]));
        }
        int i = DEFAULT_FRAME_AND_TICK_RATE.subFrameRate;
        String attributeValue3 = xmlPullParser.getAttributeValue(TTP, "subFrameRate");
        if (attributeValue3 != null) {
            i = Integer.parseInt(attributeValue3);
        }
        int i2 = DEFAULT_FRAME_AND_TICK_RATE.tickRate;
        xmlPullParser = xmlPullParser.getAttributeValue(TTP, "tickRate");
        if (xmlPullParser != null) {
            i2 = Integer.parseInt(xmlPullParser);
        }
        return new FrameAndTickRate(((float) parseInt) * f, i, i2);
    }

    private Map<String, TtmlStyle> parseHeader(XmlPullParser xmlPullParser, Map<String, TtmlStyle> map, Map<String, TtmlRegion> map2) throws IOException, XmlPullParserException {
        do {
            xmlPullParser.next();
            if (XmlPullParserUtil.isStartTag(xmlPullParser, "style")) {
                String attributeValue = XmlPullParserUtil.getAttributeValue(xmlPullParser, "style");
                TtmlStyle parseStyleAttributes = parseStyleAttributes(xmlPullParser, new TtmlStyle());
                if (attributeValue != null) {
                    for (Object obj : parseStyleIds(attributeValue)) {
                        parseStyleAttributes.chain((TtmlStyle) map.get(obj));
                    }
                }
                if (parseStyleAttributes.getId() != null) {
                    map.put(parseStyleAttributes.getId(), parseStyleAttributes);
                }
            } else if (XmlPullParserUtil.isStartTag(xmlPullParser, "region")) {
                TtmlRegion parseRegionAttributes = parseRegionAttributes(xmlPullParser);
                if (parseRegionAttributes != null) {
                    map2.put(parseRegionAttributes.id, parseRegionAttributes);
                }
            }
        } while (!XmlPullParserUtil.isEndTag(xmlPullParser, TtmlNode.TAG_HEAD));
        return map;
    }

    private org.telegram.messenger.exoplayer2.text.ttml.TtmlRegion parseRegionAttributes(org.xmlpull.v1.XmlPullParser r12) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r11 = this;
        r0 = "id";
        r2 = org.telegram.messenger.exoplayer2.util.XmlPullParserUtil.getAttributeValue(r12, r0);
        r0 = 0;
        if (r2 != 0) goto L_0x000a;
    L_0x0009:
        return r0;
    L_0x000a:
        r1 = "origin";
        r1 = org.telegram.messenger.exoplayer2.util.XmlPullParserUtil.getAttributeValue(r12, r1);
        if (r1 == 0) goto L_0x0109;
    L_0x0012:
        r3 = PERCENTAGE_COORDINATES;
        r3 = r3.matcher(r1);
        r4 = r3.matches();
        if (r4 == 0) goto L_0x00f2;
    L_0x001e:
        r4 = 1;
        r5 = r3.group(r4);	 Catch:{ NumberFormatException -> 0x00db }
        r5 = java.lang.Float.parseFloat(r5);	 Catch:{ NumberFormatException -> 0x00db }
        r6 = NUM; // 0x42c80000 float:100.0 double:5.53552857E-315;	 Catch:{ NumberFormatException -> 0x00db }
        r5 = r5 / r6;	 Catch:{ NumberFormatException -> 0x00db }
        r7 = 2;	 Catch:{ NumberFormatException -> 0x00db }
        r3 = r3.group(r7);	 Catch:{ NumberFormatException -> 0x00db }
        r3 = java.lang.Float.parseFloat(r3);	 Catch:{ NumberFormatException -> 0x00db }
        r3 = r3 / r6;
        r8 = "extent";
        r8 = org.telegram.messenger.exoplayer2.util.XmlPullParserUtil.getAttributeValue(r12, r8);
        if (r8 == 0) goto L_0x00d3;
    L_0x003c:
        r9 = PERCENTAGE_COORDINATES;
        r8 = r9.matcher(r8);
        r9 = r8.matches();
        if (r9 == 0) goto L_0x00bc;
    L_0x0048:
        r9 = r8.group(r4);	 Catch:{ NumberFormatException -> 0x00a5 }
        r9 = java.lang.Float.parseFloat(r9);	 Catch:{ NumberFormatException -> 0x00a5 }
        r9 = r9 / r6;	 Catch:{ NumberFormatException -> 0x00a5 }
        r8 = r8.group(r7);	 Catch:{ NumberFormatException -> 0x00a5 }
        r8 = java.lang.Float.parseFloat(r8);	 Catch:{ NumberFormatException -> 0x00a5 }
        r8 = r8 / r6;
        r0 = "displayAlign";
        r12 = org.telegram.messenger.exoplayer2.util.XmlPullParserUtil.getAttributeValue(r12, r0);
        r0 = 0;
        if (r12 == 0) goto L_0x0098;
    L_0x0063:
        r12 = org.telegram.messenger.exoplayer2.util.Util.toLowerInvariant(r12);
        r1 = -1;
        r6 = r12.hashCode();
        r10 = -NUM; // 0xffffffffaeb2cc55 float:-8.1307995E-11 double:NaN;
        if (r6 == r10) goto L_0x0081;
    L_0x0071:
        r10 = 92734940; // 0x58705dc float:1.2697491E-35 double:4.5817148E-316;
        if (r6 == r10) goto L_0x0077;
    L_0x0076:
        goto L_0x008a;
    L_0x0077:
        r6 = "after";
        r12 = r12.equals(r6);
        if (r12 == 0) goto L_0x008a;
    L_0x007f:
        r1 = r4;
        goto L_0x008a;
    L_0x0081:
        r6 = "center";
        r12 = r12.equals(r6);
        if (r12 == 0) goto L_0x008a;
    L_0x0089:
        r1 = r0;
    L_0x008a:
        switch(r1) {
            case 0: goto L_0x0092;
            case 1: goto L_0x008e;
            default: goto L_0x008d;
        };
    L_0x008d:
        goto L_0x0098;
    L_0x008e:
        r3 = r3 + r8;
        r4 = r3;
        r6 = r7;
        goto L_0x009a;
    L_0x0092:
        r12 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r8 = r8 / r12;
        r3 = r3 + r8;
        r6 = r4;
        goto L_0x0099;
    L_0x0098:
        r6 = r0;
    L_0x0099:
        r4 = r3;
    L_0x009a:
        r12 = new org.telegram.messenger.exoplayer2.text.ttml.TtmlRegion;
        r0 = 0;
        r1 = r12;
        r3 = r5;
        r5 = r0;
        r7 = r9;
        r1.<init>(r2, r3, r4, r5, r6, r7);
        return r12;
    L_0x00a5:
        r12 = "TtmlDecoder";
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "Ignoring region with malformed extent: ";
        r2.append(r3);
        r2.append(r1);
        r1 = r2.toString();
        android.util.Log.w(r12, r1);
        return r0;
    L_0x00bc:
        r12 = "TtmlDecoder";
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "Ignoring region with unsupported extent: ";
        r2.append(r3);
        r2.append(r1);
        r1 = r2.toString();
        android.util.Log.w(r12, r1);
        return r0;
    L_0x00d3:
        r12 = "TtmlDecoder";
        r1 = "Ignoring region without an extent";
        android.util.Log.w(r12, r1);
        return r0;
    L_0x00db:
        r12 = "TtmlDecoder";
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "Ignoring region with malformed origin: ";
        r2.append(r3);
        r2.append(r1);
        r1 = r2.toString();
        android.util.Log.w(r12, r1);
        return r0;
    L_0x00f2:
        r12 = "TtmlDecoder";
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "Ignoring region with unsupported origin: ";
        r2.append(r3);
        r2.append(r1);
        r1 = r2.toString();
        android.util.Log.w(r12, r1);
        return r0;
    L_0x0109:
        r12 = "TtmlDecoder";
        r1 = "Ignoring region without an origin";
        android.util.Log.w(r12, r1);
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer2.text.ttml.TtmlDecoder.parseRegionAttributes(org.xmlpull.v1.XmlPullParser):org.telegram.messenger.exoplayer2.text.ttml.TtmlRegion");
    }

    private String[] parseStyleIds(String str) {
        return str.split("\\s+");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private org.telegram.messenger.exoplayer2.text.ttml.TtmlStyle parseStyleAttributes(org.xmlpull.v1.XmlPullParser r12, org.telegram.messenger.exoplayer2.text.ttml.TtmlStyle r13) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r11 = this;
        r0 = r12.getAttributeCount();
        r1 = 0;
        r2 = r13;
        r13 = r1;
    L_0x0007:
        if (r13 >= r0) goto L_0x021a;
    L_0x0009:
        r3 = r12.getAttributeValue(r13);
        r4 = r12.getAttributeName(r13);
        r5 = r4.hashCode();
        r6 = 4;
        r7 = 3;
        r8 = 2;
        r9 = -1;
        r10 = 1;
        switch(r5) {
            case -1550943582: goto L_0x0070;
            case -1224696685: goto L_0x0066;
            case -1065511464: goto L_0x005c;
            case -879295043: goto L_0x0051;
            case -734428249: goto L_0x0047;
            case 3355: goto L_0x003d;
            case 94842723: goto L_0x0033;
            case 365601008: goto L_0x0029;
            case 1287124693: goto L_0x001f;
            default: goto L_0x001d;
        };
    L_0x001d:
        goto L_0x007a;
    L_0x001f:
        r5 = "backgroundColor";
        r4 = r4.equals(r5);
        if (r4 == 0) goto L_0x007a;
    L_0x0027:
        r4 = r10;
        goto L_0x007b;
    L_0x0029:
        r5 = "fontSize";
        r4 = r4.equals(r5);
        if (r4 == 0) goto L_0x007a;
    L_0x0031:
        r4 = r6;
        goto L_0x007b;
    L_0x0033:
        r5 = "color";
        r4 = r4.equals(r5);
        if (r4 == 0) goto L_0x007a;
    L_0x003b:
        r4 = r8;
        goto L_0x007b;
    L_0x003d:
        r5 = "id";
        r4 = r4.equals(r5);
        if (r4 == 0) goto L_0x007a;
    L_0x0045:
        r4 = r1;
        goto L_0x007b;
    L_0x0047:
        r5 = "fontWeight";
        r4 = r4.equals(r5);
        if (r4 == 0) goto L_0x007a;
    L_0x004f:
        r4 = 5;
        goto L_0x007b;
    L_0x0051:
        r5 = "textDecoration";
        r4 = r4.equals(r5);
        if (r4 == 0) goto L_0x007a;
    L_0x0059:
        r4 = 8;
        goto L_0x007b;
    L_0x005c:
        r5 = "textAlign";
        r4 = r4.equals(r5);
        if (r4 == 0) goto L_0x007a;
    L_0x0064:
        r4 = 7;
        goto L_0x007b;
    L_0x0066:
        r5 = "fontFamily";
        r4 = r4.equals(r5);
        if (r4 == 0) goto L_0x007a;
    L_0x006e:
        r4 = r7;
        goto L_0x007b;
    L_0x0070:
        r5 = "fontStyle";
        r4 = r4.equals(r5);
        if (r4 == 0) goto L_0x007a;
    L_0x0078:
        r4 = 6;
        goto L_0x007b;
    L_0x007a:
        r4 = r9;
    L_0x007b:
        switch(r4) {
            case 0: goto L_0x0202;
            case 1: goto L_0x01df;
            case 2: goto L_0x01bc;
            case 3: goto L_0x01b3;
            case 4: goto L_0x0191;
            case 5: goto L_0x0181;
            case 6: goto L_0x0171;
            case 7: goto L_0x00f2;
            case 8: goto L_0x0080;
            default: goto L_0x007e;
        };
    L_0x007e:
        goto L_0x0216;
    L_0x0080:
        r3 = org.telegram.messenger.exoplayer2.util.Util.toLowerInvariant(r3);
        r4 = r3.hashCode();
        r5 = -NUM; // 0xffffffffa8e6a22b float:-2.5605459E-14 double:NaN;
        if (r4 == r5) goto L_0x00bb;
    L_0x008d:
        r5 = -NUM; // 0xffffffffc2c9c6cc float:-100.888275 double:NaN;
        if (r4 == r5) goto L_0x00b1;
    L_0x0092:
        r5 = 913457136; // 0x36723ff0 float:3.6098027E-6 double:4.5130779E-315;
        if (r4 == r5) goto L_0x00a7;
    L_0x0097:
        r5 = NUM; // 0x641ec051 float:1.1713774E22 double:8.29900303E-315;
        if (r4 == r5) goto L_0x009d;
    L_0x009c:
        goto L_0x00c4;
    L_0x009d:
        r4 = "linethrough";
        r3 = r3.equals(r4);
        if (r3 == 0) goto L_0x00c4;
    L_0x00a5:
        r7 = r1;
        goto L_0x00c5;
    L_0x00a7:
        r4 = "nolinethrough";
        r3 = r3.equals(r4);
        if (r3 == 0) goto L_0x00c4;
    L_0x00af:
        r7 = r10;
        goto L_0x00c5;
    L_0x00b1:
        r4 = "underline";
        r3 = r3.equals(r4);
        if (r3 == 0) goto L_0x00c4;
    L_0x00b9:
        r7 = r8;
        goto L_0x00c5;
    L_0x00bb:
        r4 = "nounderline";
        r3 = r3.equals(r4);
        if (r3 == 0) goto L_0x00c4;
    L_0x00c3:
        goto L_0x00c5;
    L_0x00c4:
        r7 = r9;
    L_0x00c5:
        switch(r7) {
            case 0: goto L_0x00e8;
            case 1: goto L_0x00de;
            case 2: goto L_0x00d4;
            case 3: goto L_0x00ca;
            default: goto L_0x00c8;
        };
    L_0x00c8:
        goto L_0x0216;
    L_0x00ca:
        r2 = r11.createIfNull(r2);
        r2 = r2.setUnderline(r1);
        goto L_0x0216;
    L_0x00d4:
        r2 = r11.createIfNull(r2);
        r2 = r2.setUnderline(r10);
        goto L_0x0216;
    L_0x00de:
        r2 = r11.createIfNull(r2);
        r2 = r2.setLinethrough(r1);
        goto L_0x0216;
    L_0x00e8:
        r2 = r11.createIfNull(r2);
        r2 = r2.setLinethrough(r10);
        goto L_0x0216;
    L_0x00f2:
        r3 = org.telegram.messenger.exoplayer2.util.Util.toLowerInvariant(r3);
        r4 = r3.hashCode();
        switch(r4) {
            case -1364013995: goto L_0x0126;
            case 100571: goto L_0x011c;
            case 3317767: goto L_0x0112;
            case 108511772: goto L_0x0108;
            case 109757538: goto L_0x00fe;
            default: goto L_0x00fd;
        };
    L_0x00fd:
        goto L_0x012f;
    L_0x00fe:
        r4 = "start";
        r3 = r3.equals(r4);
        if (r3 == 0) goto L_0x012f;
    L_0x0106:
        r6 = r10;
        goto L_0x0130;
    L_0x0108:
        r4 = "right";
        r3 = r3.equals(r4);
        if (r3 == 0) goto L_0x012f;
    L_0x0110:
        r6 = r8;
        goto L_0x0130;
    L_0x0112:
        r4 = "left";
        r3 = r3.equals(r4);
        if (r3 == 0) goto L_0x012f;
    L_0x011a:
        r6 = r1;
        goto L_0x0130;
    L_0x011c:
        r4 = "end";
        r3 = r3.equals(r4);
        if (r3 == 0) goto L_0x012f;
    L_0x0124:
        r6 = r7;
        goto L_0x0130;
    L_0x0126:
        r4 = "center";
        r3 = r3.equals(r4);
        if (r3 == 0) goto L_0x012f;
    L_0x012e:
        goto L_0x0130;
    L_0x012f:
        r6 = r9;
    L_0x0130:
        switch(r6) {
            case 0: goto L_0x0165;
            case 1: goto L_0x0159;
            case 2: goto L_0x014d;
            case 3: goto L_0x0141;
            case 4: goto L_0x0135;
            default: goto L_0x0133;
        };
    L_0x0133:
        goto L_0x0216;
    L_0x0135:
        r2 = r11.createIfNull(r2);
        r3 = android.text.Layout.Alignment.ALIGN_CENTER;
        r2 = r2.setTextAlign(r3);
        goto L_0x0216;
    L_0x0141:
        r2 = r11.createIfNull(r2);
        r3 = android.text.Layout.Alignment.ALIGN_OPPOSITE;
        r2 = r2.setTextAlign(r3);
        goto L_0x0216;
    L_0x014d:
        r2 = r11.createIfNull(r2);
        r3 = android.text.Layout.Alignment.ALIGN_OPPOSITE;
        r2 = r2.setTextAlign(r3);
        goto L_0x0216;
    L_0x0159:
        r2 = r11.createIfNull(r2);
        r3 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r2 = r2.setTextAlign(r3);
        goto L_0x0216;
    L_0x0165:
        r2 = r11.createIfNull(r2);
        r3 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r2 = r2.setTextAlign(r3);
        goto L_0x0216;
    L_0x0171:
        r2 = r11.createIfNull(r2);
        r4 = "italic";
        r3 = r4.equalsIgnoreCase(r3);
        r2 = r2.setItalic(r3);
        goto L_0x0216;
    L_0x0181:
        r2 = r11.createIfNull(r2);
        r4 = "bold";
        r3 = r4.equalsIgnoreCase(r3);
        r2 = r2.setBold(r3);
        goto L_0x0216;
    L_0x0191:
        r4 = r11.createIfNull(r2);	 Catch:{ SubtitleDecoderException -> 0x019c }
        parseFontSize(r3, r4);	 Catch:{ SubtitleDecoderException -> 0x019b }
        r2 = r4;
        goto L_0x0216;
    L_0x019b:
        r2 = r4;
    L_0x019c:
        r4 = "TtmlDecoder";
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r6 = "Failed parsing fontSize value: ";
        r5.append(r6);
        r5.append(r3);
        r3 = r5.toString();
        android.util.Log.w(r4, r3);
        goto L_0x0216;
    L_0x01b3:
        r2 = r11.createIfNull(r2);
        r2 = r2.setFontFamily(r3);
        goto L_0x0216;
    L_0x01bc:
        r2 = r11.createIfNull(r2);
        r4 = org.telegram.messenger.exoplayer2.util.ColorParser.parseTtmlColor(r3);	 Catch:{ IllegalArgumentException -> 0x01c8 }
        r2.setFontColor(r4);	 Catch:{ IllegalArgumentException -> 0x01c8 }
        goto L_0x0216;
    L_0x01c8:
        r4 = "TtmlDecoder";
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r6 = "Failed parsing color value: ";
        r5.append(r6);
        r5.append(r3);
        r3 = r5.toString();
        android.util.Log.w(r4, r3);
        goto L_0x0216;
    L_0x01df:
        r2 = r11.createIfNull(r2);
        r4 = org.telegram.messenger.exoplayer2.util.ColorParser.parseTtmlColor(r3);	 Catch:{ IllegalArgumentException -> 0x01eb }
        r2.setBackgroundColor(r4);	 Catch:{ IllegalArgumentException -> 0x01eb }
        goto L_0x0216;
    L_0x01eb:
        r4 = "TtmlDecoder";
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r6 = "Failed parsing background value: ";
        r5.append(r6);
        r5.append(r3);
        r3 = r5.toString();
        android.util.Log.w(r4, r3);
        goto L_0x0216;
    L_0x0202:
        r4 = "style";
        r5 = r12.getName();
        r4 = r4.equals(r5);
        if (r4 == 0) goto L_0x0216;
    L_0x020e:
        r2 = r11.createIfNull(r2);
        r2 = r2.setId(r3);
    L_0x0216:
        r13 = r13 + 1;
        goto L_0x0007;
    L_0x021a:
        return r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer2.text.ttml.TtmlDecoder.parseStyleAttributes(org.xmlpull.v1.XmlPullParser, org.telegram.messenger.exoplayer2.text.ttml.TtmlStyle):org.telegram.messenger.exoplayer2.text.ttml.TtmlStyle");
    }

    private TtmlStyle createIfNull(TtmlStyle ttmlStyle) {
        return ttmlStyle == null ? new TtmlStyle() : ttmlStyle;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private TtmlNode parseNode(XmlPullParser xmlPullParser, TtmlNode ttmlNode, Map<String, TtmlRegion> map, FrameAndTickRate frameAndTickRate) throws SubtitleDecoderException {
        long j;
        long j2;
        XmlPullParser xmlPullParser2 = xmlPullParser;
        TtmlNode ttmlNode2 = ttmlNode;
        FrameAndTickRate frameAndTickRate2 = frameAndTickRate;
        String str = TtmlNode.ANONYMOUS_REGION_ID;
        int attributeCount = xmlPullParser.getAttributeCount();
        TtmlStyle parseStyleAttributes = parseStyleAttributes(xmlPullParser2, null);
        TtmlStyle ttmlStyle = null;
        long j3 = C0542C.TIME_UNSET;
        long j4 = C0542C.TIME_UNSET;
        long j5 = C0542C.TIME_UNSET;
        String str2 = str;
        for (int i = 0; i < attributeCount; i++) {
            Object obj;
            String attributeName = xmlPullParser2.getAttributeName(i);
            String attributeValue = xmlPullParser2.getAttributeValue(i);
            switch (attributeName.hashCode()) {
                case -934795532:
                    if (attributeName.equals("region")) {
                        obj = 4;
                        break;
                    }
                case 99841:
                    if (attributeName.equals(ATTR_DURATION)) {
                        obj = 2;
                        break;
                    }
                case 100571:
                    if (attributeName.equals("end")) {
                        obj = 1;
                        break;
                    }
                case 93616297:
                    if (attributeName.equals(ATTR_BEGIN)) {
                        obj = null;
                        break;
                    }
                case 109780401:
                    if (attributeName.equals("style")) {
                        obj = 3;
                        break;
                    }
                default:
            }
            obj = -1;
            Map<String, TtmlRegion> map2;
            switch (obj) {
                case null:
                    map2 = map;
                    j3 = parseTimeExpression(attributeValue, frameAndTickRate2);
                    break;
                case 1:
                    map2 = map;
                    j4 = parseTimeExpression(attributeValue, frameAndTickRate2);
                    break;
                case 2:
                    map2 = map;
                    j5 = parseTimeExpression(attributeValue, frameAndTickRate2);
                    break;
                case 3:
                    map2 = map;
                    String[] parseStyleIds = parseStyleIds(attributeValue);
                    if (parseStyleIds.length <= 0) {
                        break;
                    }
                    ttmlStyle = parseStyleIds;
                    break;
                case 4:
                    if (!map.containsKey(attributeValue)) {
                        break;
                    }
                    str2 = attributeValue;
                    break;
                default:
                    map2 = map;
                    break;
            }
        }
        if (ttmlNode2 != null) {
            long j6 = ttmlNode2.startTimeUs;
            j = C0542C.TIME_UNSET;
            if (j6 != C0542C.TIME_UNSET) {
                if (j3 != C0542C.TIME_UNSET) {
                    j3 += ttmlNode2.startTimeUs;
                }
                if (j4 != C0542C.TIME_UNSET) {
                    j4 += ttmlNode2.startTimeUs;
                }
            }
        } else {
            j = C0542C.TIME_UNSET;
        }
        if (j4 == j) {
            if (j5 != j) {
                j2 = j3 + j5;
            } else if (!(ttmlNode2 == null || ttmlNode2.endTimeUs == j)) {
                j2 = ttmlNode2.endTimeUs;
            }
            return TtmlNode.buildNode(xmlPullParser.getName(), j3, j2, parseStyleAttributes, ttmlStyle, str2);
        }
        j2 = j4;
        return TtmlNode.buildNode(xmlPullParser.getName(), j3, j2, parseStyleAttributes, ttmlStyle, str2);
    }

    private static boolean isSupportedTag(String str) {
        if (!(str.equals(TtmlNode.TAG_TT) || str.equals(TtmlNode.TAG_HEAD) || str.equals(TtmlNode.TAG_BODY) || str.equals(TtmlNode.TAG_DIV) || str.equals(TtmlNode.TAG_P) || str.equals(TtmlNode.TAG_SPAN) || str.equals(TtmlNode.TAG_BR) || str.equals("style") || str.equals(TtmlNode.TAG_STYLING) || str.equals(TtmlNode.TAG_LAYOUT) || str.equals("region") || str.equals(TtmlNode.TAG_METADATA) || str.equals(TtmlNode.TAG_SMPTE_IMAGE) || str.equals(TtmlNode.TAG_SMPTE_DATA))) {
            if (str.equals(TtmlNode.TAG_SMPTE_INFORMATION) == null) {
                return null;
            }
        }
        return true;
    }

    private static void parseFontSize(String str, TtmlStyle ttmlStyle) throws SubtitleDecoderException {
        Matcher matcher;
        String[] split = str.split("\\s+");
        if (split.length == 1) {
            matcher = FONT_SIZE.matcher(str);
        } else if (split.length == 2) {
            matcher = FONT_SIZE.matcher(split[1]);
            Log.w(TAG, "Multiple values in fontSize attribute. Picking the second value for vertical font size and ignoring the first.");
        } else {
            ttmlStyle = new StringBuilder();
            ttmlStyle.append("Invalid number of entries for fontSize: ");
            ttmlStyle.append(split.length);
            ttmlStyle.append(".");
            throw new SubtitleDecoderException(ttmlStyle.toString());
        }
        if (matcher.matches()) {
            String group = matcher.group(3);
            int i = -1;
            int hashCode = group.hashCode();
            if (hashCode != 37) {
                if (hashCode != 3240) {
                    if (hashCode == 3592) {
                        if (group.equals("px")) {
                            i = 0;
                        }
                    }
                } else if (group.equals("em")) {
                    i = 1;
                }
            } else if (group.equals("%")) {
                i = 2;
            }
            switch (i) {
                case 0:
                    ttmlStyle.setFontSizeUnit(1);
                    break;
                case 1:
                    ttmlStyle.setFontSizeUnit(2);
                    break;
                case 2:
                    ttmlStyle.setFontSizeUnit(3);
                    break;
                default:
                    ttmlStyle = new StringBuilder();
                    ttmlStyle.append("Invalid unit for fontSize: '");
                    ttmlStyle.append(group);
                    ttmlStyle.append("'.");
                    throw new SubtitleDecoderException(ttmlStyle.toString());
            }
            ttmlStyle.setFontSize(Float.valueOf(matcher.group(1)).floatValue());
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Invalid expression for fontSize: '");
        stringBuilder.append(str);
        stringBuilder.append("'.");
        throw new SubtitleDecoderException(stringBuilder.toString());
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static long parseTimeExpression(String str, FrameAndTickRate frameAndTickRate) throws SubtitleDecoderException {
        Matcher matcher = CLOCK_TIME.matcher(str);
        int i = 5;
        if (matcher.matches()) {
            double parseLong = (((double) (Long.parseLong(matcher.group(1)) * 3600)) + ((double) (Long.parseLong(matcher.group(2)) * 60))) + ((double) Long.parseLong(matcher.group(3)));
            str = matcher.group(4);
            double d = 0.0d;
            parseLong += str != null ? Double.parseDouble(str) : 0.0d;
            str = matcher.group(5);
            parseLong += str != null ? (double) (((float) Long.parseLong(str)) / frameAndTickRate.effectiveFrameRate) : 0.0d;
            str = matcher.group(6);
            if (str != null) {
                d = (((double) Long.parseLong(str)) / ((double) frameAndTickRate.subFrameRate)) / ((double) frameAndTickRate.effectiveFrameRate);
            }
            return (long) ((parseLong + d) * 1000000.0d);
        }
        matcher = OFFSET_TIME.matcher(str);
        if (matcher.matches()) {
            double parseDouble = Double.parseDouble(matcher.group(1));
            str = matcher.group(2);
            int hashCode = str.hashCode();
            if (hashCode != 102) {
                if (hashCode != 104) {
                    if (hashCode != 109) {
                        if (hashCode != 3494) {
                            switch (hashCode) {
                                case 115:
                                    if (str.equals("s") != null) {
                                        i = 2;
                                        break;
                                    }
                                case 116:
                                    if (str.equals("t") != null) {
                                        break;
                                    }
                                default:
                            }
                        } else if (str.equals("ms") != null) {
                            i = 3;
                            switch (i) {
                                case 0:
                                    parseDouble *= 0;
                                    break;
                                case 1:
                                    parseDouble *= 0;
                                    break;
                                case 2:
                                    break;
                                case 3:
                                    parseDouble /= 0;
                                    break;
                                case 4:
                                    parseDouble /= (double) frameAndTickRate.effectiveFrameRate;
                                    break;
                                case 5:
                                    parseDouble /= (double) frameAndTickRate.tickRate;
                                    break;
                                default:
                                    break;
                            }
                            return (long) (parseDouble * 1000000.0d);
                        }
                    } else if (str.equals("m") != null) {
                        i = 1;
                        switch (i) {
                            case 0:
                                parseDouble *= 0;
                                break;
                            case 1:
                                parseDouble *= 0;
                                break;
                            case 2:
                                break;
                            case 3:
                                parseDouble /= 0;
                                break;
                            case 4:
                                parseDouble /= (double) frameAndTickRate.effectiveFrameRate;
                                break;
                            case 5:
                                parseDouble /= (double) frameAndTickRate.tickRate;
                                break;
                            default:
                                break;
                        }
                        return (long) (parseDouble * 1000000.0d);
                    }
                } else if (str.equals("h") != null) {
                    i = 0;
                    switch (i) {
                        case 0:
                            parseDouble *= 0;
                            break;
                        case 1:
                            parseDouble *= 0;
                            break;
                        case 2:
                            break;
                        case 3:
                            parseDouble /= 0;
                            break;
                        case 4:
                            parseDouble /= (double) frameAndTickRate.effectiveFrameRate;
                            break;
                        case 5:
                            parseDouble /= (double) frameAndTickRate.tickRate;
                            break;
                        default:
                            break;
                    }
                    return (long) (parseDouble * 1000000.0d);
                }
            } else if (str.equals("f") != null) {
                i = 4;
                switch (i) {
                    case 0:
                        parseDouble *= 0;
                        break;
                    case 1:
                        parseDouble *= 0;
                        break;
                    case 2:
                        break;
                    case 3:
                        parseDouble /= 0;
                        break;
                    case 4:
                        parseDouble /= (double) frameAndTickRate.effectiveFrameRate;
                        break;
                    case 5:
                        parseDouble /= (double) frameAndTickRate.tickRate;
                        break;
                    default:
                        break;
                }
                return (long) (parseDouble * 1000000.0d);
            }
            i = -1;
            switch (i) {
                case 0:
                    parseDouble *= 0;
                    break;
                case 1:
                    parseDouble *= 0;
                    break;
                case 2:
                    break;
                case 3:
                    parseDouble /= 0;
                    break;
                case 4:
                    parseDouble /= (double) frameAndTickRate.effectiveFrameRate;
                    break;
                case 5:
                    parseDouble /= (double) frameAndTickRate.tickRate;
                    break;
                default:
                    break;
            }
            return (long) (parseDouble * 1000000.0d);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Malformed time expression: ");
        stringBuilder.append(str);
        throw new SubtitleDecoderException(stringBuilder.toString());
    }
}
