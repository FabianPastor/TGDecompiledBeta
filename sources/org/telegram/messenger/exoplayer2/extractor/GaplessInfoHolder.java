package org.telegram.messenger.exoplayer2.extractor;

import java.util.regex.Pattern;
import org.telegram.messenger.exoplayer2.metadata.Metadata;
import org.telegram.messenger.exoplayer2.metadata.Metadata.Entry;
import org.telegram.messenger.exoplayer2.metadata.id3.CommentFrame;
import org.telegram.messenger.exoplayer2.metadata.id3.Id3Decoder.FramePredicate;

public final class GaplessInfoHolder {
    private static final String GAPLESS_COMMENT_ID = "iTunSMPB";
    private static final Pattern GAPLESS_COMMENT_PATTERN = Pattern.compile("^ [0-9a-fA-F]{8} ([0-9a-fA-F]{8}) ([0-9a-fA-F]{8})");
    public static final FramePredicate GAPLESS_INFO_ID3_FRAME_PREDICATE = new C18361();
    public int encoderDelay = -1;
    public int encoderPadding = -1;

    /* renamed from: org.telegram.messenger.exoplayer2.extractor.GaplessInfoHolder$1 */
    static class C18361 implements FramePredicate {
        public boolean evaluate(int i, int i2, int i3, int i4, int i5) {
            return i2 == 67 && i3 == 79 && i4 == 77 && (i5 == 77 || i == 2);
        }

        C18361() {
        }
    }

    public boolean setFromXingHeaderValue(int i) {
        int i2 = i >> 12;
        i &= 4095;
        if (i2 <= 0) {
            if (i <= 0) {
                return false;
            }
        }
        this.encoderDelay = i2;
        this.encoderPadding = i;
        return true;
    }

    public boolean setFromMetadata(Metadata metadata) {
        for (int i = 0; i < metadata.length(); i++) {
            Entry entry = metadata.get(i);
            if (entry instanceof CommentFrame) {
                CommentFrame commentFrame = (CommentFrame) entry;
                if (setFromComment(commentFrame.description, commentFrame.text)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean setFromComment(java.lang.String r5, java.lang.String r6) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r4 = this;
        r0 = "iTunSMPB";
        r5 = r0.equals(r5);
        r0 = 0;
        if (r5 != 0) goto L_0x000a;
    L_0x0009:
        return r0;
    L_0x000a:
        r5 = GAPLESS_COMMENT_PATTERN;
        r5 = r5.matcher(r6);
        r6 = r5.find();
        if (r6 == 0) goto L_0x0033;
    L_0x0016:
        r6 = 1;
        r1 = r5.group(r6);	 Catch:{ NumberFormatException -> 0x0033 }
        r2 = 16;	 Catch:{ NumberFormatException -> 0x0033 }
        r1 = java.lang.Integer.parseInt(r1, r2);	 Catch:{ NumberFormatException -> 0x0033 }
        r3 = 2;	 Catch:{ NumberFormatException -> 0x0033 }
        r5 = r5.group(r3);	 Catch:{ NumberFormatException -> 0x0033 }
        r5 = java.lang.Integer.parseInt(r5, r2);	 Catch:{ NumberFormatException -> 0x0033 }
        if (r1 > 0) goto L_0x002e;	 Catch:{ NumberFormatException -> 0x0033 }
    L_0x002c:
        if (r5 <= 0) goto L_0x0033;	 Catch:{ NumberFormatException -> 0x0033 }
    L_0x002e:
        r4.encoderDelay = r1;	 Catch:{ NumberFormatException -> 0x0033 }
        r4.encoderPadding = r5;	 Catch:{ NumberFormatException -> 0x0033 }
        return r6;
    L_0x0033:
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer2.extractor.GaplessInfoHolder.setFromComment(java.lang.String, java.lang.String):boolean");
    }

    public boolean hasGaplessInfo() {
        return (this.encoderDelay == -1 || this.encoderPadding == -1) ? false : true;
    }
}
