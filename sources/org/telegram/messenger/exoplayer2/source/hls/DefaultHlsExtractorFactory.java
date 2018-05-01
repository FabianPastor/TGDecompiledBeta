package org.telegram.messenger.exoplayer2.source.hls;

public final class DefaultHlsExtractorFactory implements HlsExtractorFactory {
    public static final String AAC_FILE_EXTENSION = ".aac";
    public static final String AC3_FILE_EXTENSION = ".ac3";
    public static final String EC3_FILE_EXTENSION = ".ec3";
    public static final String M4_FILE_EXTENSION_PREFIX = ".m4";
    public static final String MP3_FILE_EXTENSION = ".mp3";
    public static final String MP4_FILE_EXTENSION = ".mp4";
    public static final String MP4_FILE_EXTENSION_PREFIX = ".mp4";
    public static final String VTT_FILE_EXTENSION = ".vtt";
    public static final String WEBVTT_FILE_EXTENSION = ".webvtt";

    public android.util.Pair<org.telegram.messenger.exoplayer2.extractor.Extractor, java.lang.Boolean> createExtractor(org.telegram.messenger.exoplayer2.extractor.Extractor r9, android.net.Uri r10, org.telegram.messenger.exoplayer2.Format r11, java.util.List<org.telegram.messenger.exoplayer2.Format> r12, org.telegram.messenger.exoplayer2.drm.DrmInitData r13, org.telegram.messenger.exoplayer2.util.TimestampAdjuster r14) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Unknown predecessor block by arg (r9_19 org.telegram.messenger.exoplayer2.extractor.Extractor) in PHI: PHI: (r9_22 org.telegram.messenger.exoplayer2.extractor.Extractor) = (r9_2 org.telegram.messenger.exoplayer2.extractor.Extractor), (r9_0 org.telegram.messenger.exoplayer2.extractor.Extractor), (r9_18 org.telegram.messenger.exoplayer2.extractor.Extractor), (r9_19 org.telegram.messenger.exoplayer2.extractor.Extractor), (r9_21 org.telegram.messenger.exoplayer2.extractor.Extractor) binds: {(r9_2 org.telegram.messenger.exoplayer2.extractor.Extractor)=B:10:0x002f, (r9_0 org.telegram.messenger.exoplayer2.extractor.Extractor)=B:20:0x0056, (r9_18 org.telegram.messenger.exoplayer2.extractor.Extractor)=B:40:0x00ac, (r9_19 org.telegram.messenger.exoplayer2.extractor.Extractor)=B:45:0x00c6, (r9_21 org.telegram.messenger.exoplayer2.extractor.Extractor)=B:47:0x00d4}
	at jadx.core.dex.instructions.PhiInsn.replaceArg(PhiInsn.java:79)
	at jadx.core.dex.visitors.ModVisitor.processInvoke(ModVisitor.java:222)
	at jadx.core.dex.visitors.ModVisitor.replaceStep(ModVisitor.java:83)
	at jadx.core.dex.visitors.ModVisitor.visit(ModVisitor.java:68)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r8 = this;
        r10 = r10.getLastPathSegment();
        r0 = "text/vtt";
        r1 = r11.sampleMimeType;
        r0 = r0.equals(r1);
        r1 = 0;
        r2 = 1;
        if (r0 != 0) goto L_0x00d4;
    L_0x0010:
        r0 = ".webvtt";
        r0 = r10.endsWith(r0);
        if (r0 != 0) goto L_0x00d4;
    L_0x0018:
        r0 = ".vtt";
        r0 = r10.endsWith(r0);
        if (r0 == 0) goto L_0x0022;
    L_0x0020:
        goto L_0x00d4;
    L_0x0022:
        r0 = ".aac";
        r0 = r10.endsWith(r0);
        if (r0 == 0) goto L_0x0032;
    L_0x002a:
        r9 = new org.telegram.messenger.exoplayer2.extractor.ts.AdtsExtractor;
        r9.<init>();
    L_0x002f:
        r1 = r2;
        goto L_0x00db;
    L_0x0032:
        r0 = ".ac3";
        r0 = r10.endsWith(r0);
        if (r0 != 0) goto L_0x00cd;
    L_0x003a:
        r0 = ".ec3";
        r0 = r10.endsWith(r0);
        if (r0 == 0) goto L_0x0044;
    L_0x0042:
        goto L_0x00cd;
    L_0x0044:
        r0 = ".mp3";
        r0 = r10.endsWith(r0);
        if (r0 == 0) goto L_0x0054;
    L_0x004c:
        r9 = new org.telegram.messenger.exoplayer2.extractor.mp3.Mp3Extractor;
        r10 = 0;
        r9.<init>(r1, r10);
        goto L_0x002f;
    L_0x0054:
        if (r9 == 0) goto L_0x0058;
    L_0x0056:
        goto L_0x00db;
    L_0x0058:
        r9 = ".mp4";
        r9 = r10.endsWith(r9);
        if (r9 != 0) goto L_0x00b9;
    L_0x0060:
        r9 = ".m4";
        r0 = r10.length();
        r0 = r0 + -4;
        r9 = r10.startsWith(r9, r0);
        if (r9 != 0) goto L_0x00b9;
    L_0x006e:
        r9 = ".mp4";
        r0 = r10.length();
        r0 = r0 + -5;
        r9 = r10.startsWith(r9, r0);
        if (r9 == 0) goto L_0x007d;
    L_0x007c:
        goto L_0x00b9;
    L_0x007d:
        r9 = 16;
        if (r12 == 0) goto L_0x0084;
    L_0x0081:
        r9 = 48;
        goto L_0x0088;
    L_0x0084:
        r12 = java.util.Collections.emptyList();
    L_0x0088:
        r10 = r11.codecs;
        r11 = android.text.TextUtils.isEmpty(r10);
        if (r11 != 0) goto L_0x00ac;
    L_0x0090:
        r11 = "audio/mp4a-latm";
        r13 = org.telegram.messenger.exoplayer2.util.MimeTypes.getAudioMediaMimeType(r10);
        r11 = r11.equals(r13);
        if (r11 != 0) goto L_0x009e;
    L_0x009c:
        r9 = r9 | 2;
    L_0x009e:
        r11 = "video/avc";
        r10 = org.telegram.messenger.exoplayer2.util.MimeTypes.getVideoMediaMimeType(r10);
        r10 = r11.equals(r10);
        if (r10 != 0) goto L_0x00ac;
    L_0x00aa:
        r9 = r9 | 4;
    L_0x00ac:
        r10 = new org.telegram.messenger.exoplayer2.extractor.ts.TsExtractor;
        r11 = new org.telegram.messenger.exoplayer2.extractor.ts.DefaultTsPayloadReaderFactory;
        r11.<init>(r9, r12);
        r9 = 2;
        r10.<init>(r9, r14, r11);
        r9 = r10;
        goto L_0x00db;
    L_0x00b9:
        r9 = new org.telegram.messenger.exoplayer2.extractor.mp4.FragmentedMp4Extractor;
        r3 = 0;
        r5 = 0;
        if (r12 == 0) goto L_0x00c1;
    L_0x00bf:
        r7 = r12;
        goto L_0x00c6;
    L_0x00c1:
        r12 = java.util.Collections.emptyList();
        goto L_0x00bf;
    L_0x00c6:
        r2 = r9;
        r4 = r14;
        r6 = r13;
        r2.<init>(r3, r4, r5, r6, r7);
        goto L_0x00db;
    L_0x00cd:
        r9 = new org.telegram.messenger.exoplayer2.extractor.ts.Ac3Extractor;
        r9.<init>();
        goto L_0x002f;
    L_0x00d4:
        r9 = new org.telegram.messenger.exoplayer2.source.hls.WebvttExtractor;
        r10 = r11.language;
        r9.<init>(r10, r14);
    L_0x00db:
        r10 = java.lang.Boolean.valueOf(r1);
        r9 = android.util.Pair.create(r9, r10);
        return r9;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer2.source.hls.DefaultHlsExtractorFactory.createExtractor(org.telegram.messenger.exoplayer2.extractor.Extractor, android.net.Uri, org.telegram.messenger.exoplayer2.Format, java.util.List, org.telegram.messenger.exoplayer2.drm.DrmInitData, org.telegram.messenger.exoplayer2.util.TimestampAdjuster):android.util.Pair<org.telegram.messenger.exoplayer2.extractor.Extractor, java.lang.Boolean>");
    }
}
