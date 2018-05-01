package org.telegram.messenger.exoplayer2.text.subrip;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.messenger.exoplayer2.text.SimpleSubtitleDecoder;

public final class SubripDecoder extends SimpleSubtitleDecoder {
    private static final String SUBRIP_TIMECODE = "(?:(\\d+):)?(\\d+):(\\d+),(\\d+)";
    private static final Pattern SUBRIP_TIMING_LINE = Pattern.compile("\\s*((?:(\\d+):)?(\\d+):(\\d+),(\\d+))\\s*-->\\s*((?:(\\d+):)?(\\d+):(\\d+),(\\d+))?\\s*");
    private static final String TAG = "SubripDecoder";
    private final StringBuilder textBuilder = new StringBuilder();

    public SubripDecoder() {
        super(TAG);
    }

    protected org.telegram.messenger.exoplayer2.text.subrip.SubripSubtitle decode(byte[] r6, int r7, boolean r8) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r5 = this;
        r8 = new java.util.ArrayList;
        r8.<init>();
        r0 = new org.telegram.messenger.exoplayer2.util.LongArray;
        r0.<init>();
        r1 = new org.telegram.messenger.exoplayer2.util.ParsableByteArray;
        r1.<init>(r6, r7);
    L_0x000f:
        r6 = r1.readLine();
        if (r6 == 0) goto L_0x00c9;
    L_0x0015:
        r7 = r6.length();
        if (r7 != 0) goto L_0x001c;
    L_0x001b:
        goto L_0x000f;
    L_0x001c:
        java.lang.Integer.parseInt(r6);	 Catch:{ NumberFormatException -> 0x00b1 }
        r6 = r1.readLine();
        if (r6 != 0) goto L_0x002e;
    L_0x0025:
        r6 = "SubripDecoder";
        r7 = "Unexpected end";
        android.util.Log.w(r6, r7);
        goto L_0x00c9;
    L_0x002e:
        r7 = SUBRIP_TIMING_LINE;
        r7 = r7.matcher(r6);
        r2 = r7.matches();
        if (r2 == 0) goto L_0x0099;
    L_0x003a:
        r6 = 1;
        r2 = parseTimecode(r7, r6);
        r0.add(r2);
        r2 = 6;
        r3 = r7.group(r2);
        r3 = android.text.TextUtils.isEmpty(r3);
        r4 = 0;
        if (r3 != 0) goto L_0x0056;
    L_0x004e:
        r2 = parseTimecode(r7, r2);
        r0.add(r2);
        goto L_0x0057;
    L_0x0056:
        r6 = r4;
    L_0x0057:
        r7 = r5.textBuilder;
        r7.setLength(r4);
    L_0x005c:
        r7 = r1.readLine();
        r2 = android.text.TextUtils.isEmpty(r7);
        if (r2 != 0) goto L_0x007f;
    L_0x0066:
        r2 = r5.textBuilder;
        r2 = r2.length();
        if (r2 <= 0) goto L_0x0075;
    L_0x006e:
        r2 = r5.textBuilder;
        r3 = "<br>";
        r2.append(r3);
    L_0x0075:
        r2 = r5.textBuilder;
        r7 = r7.trim();
        r2.append(r7);
        goto L_0x005c;
    L_0x007f:
        r7 = r5.textBuilder;
        r7 = r7.toString();
        r7 = android.text.Html.fromHtml(r7);
        r2 = new org.telegram.messenger.exoplayer2.text.Cue;
        r2.<init>(r7);
        r8.add(r2);
        if (r6 == 0) goto L_0x000f;
    L_0x0093:
        r6 = 0;
        r8.add(r6);
        goto L_0x000f;
    L_0x0099:
        r7 = "SubripDecoder";
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "Skipping invalid timing: ";
        r2.append(r3);
        r2.append(r6);
        r6 = r2.toString();
        android.util.Log.w(r7, r6);
        goto L_0x000f;
    L_0x00b1:
        r7 = "SubripDecoder";
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "Skipping invalid index: ";
        r2.append(r3);
        r2.append(r6);
        r6 = r2.toString();
        android.util.Log.w(r7, r6);
        goto L_0x000f;
    L_0x00c9:
        r6 = r8.size();
        r6 = new org.telegram.messenger.exoplayer2.text.Cue[r6];
        r8.toArray(r6);
        r7 = r0.toArray();
        r8 = new org.telegram.messenger.exoplayer2.text.subrip.SubripSubtitle;
        r8.<init>(r6, r7);
        return r8;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer2.text.subrip.SubripDecoder.decode(byte[], int, boolean):org.telegram.messenger.exoplayer2.text.subrip.SubripSubtitle");
    }

    private static long parseTimecode(Matcher matcher, int i) {
        return ((((((Long.parseLong(matcher.group(i + 1)) * 60) * 60) * 1000) + ((Long.parseLong(matcher.group(i + 2)) * 60) * 1000)) + (Long.parseLong(matcher.group(i + 3)) * 1000)) + Long.parseLong(matcher.group(i + 4))) * 1000;
    }
}
