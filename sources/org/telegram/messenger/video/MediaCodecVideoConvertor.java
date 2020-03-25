package org.telegram.messenger.video;

import android.media.MediaExtractor;
import java.io.File;
import org.telegram.messenger.MediaController;

public class MediaCodecVideoConvertor {
    private static final int MEDIACODEC_TIMEOUT_DEFAULT = 2500;
    private static final int MEDIACODEC_TIMEOUT_INCREASED = 22000;
    private static final int PROCESSOR_TYPE_INTEL = 2;
    private static final int PROCESSOR_TYPE_MTK = 3;
    private static final int PROCESSOR_TYPE_OTHER = 0;
    private static final int PROCESSOR_TYPE_QCOM = 1;
    private static final int PROCESSOR_TYPE_SEC = 4;
    private static final int PROCESSOR_TYPE_TI = 5;
    MediaController.VideoConvertorListener callback;
    MediaExtractor extractor;
    MP4Builder mediaMuxer;

    public boolean convertVideo(String str, File file, int i, boolean z, int i2, int i3, int i4, int i5, long j, long j2, boolean z2, long j3, MediaController.VideoConvertorListener videoConvertorListener) {
        String str2 = str;
        long j4 = j3;
        this.callback = videoConvertorListener;
        return convertVideoInternal(str, file, i, z, i2, i3, i4, i5, j, j2, j4, z2, false);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v0, resolved type: org.telegram.messenger.video.AudioRecoder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v1, resolved type: org.telegram.messenger.video.AudioRecoder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v2, resolved type: org.telegram.messenger.video.AudioRecoder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v3, resolved type: org.telegram.messenger.video.AudioRecoder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v4, resolved type: org.telegram.messenger.video.AudioRecoder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v32, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v5, resolved type: org.telegram.messenger.video.AudioRecoder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v6, resolved type: org.telegram.messenger.video.AudioRecoder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v33, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r64v0, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v7, resolved type: org.telegram.messenger.video.AudioRecoder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r64v1, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r64v2, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v56, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v69, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v70, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v71, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v75, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v89, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v91, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v42, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r64v8, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v8, resolved type: org.telegram.messenger.video.AudioRecoder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v9, resolved type: org.telegram.messenger.video.AudioRecoder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v10, resolved type: org.telegram.messenger.video.AudioRecoder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v11, resolved type: org.telegram.messenger.video.AudioRecoder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v12, resolved type: org.telegram.messenger.video.AudioRecoder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r64v9, resolved type: org.telegram.messenger.video.AudioRecoder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v13, resolved type: org.telegram.messenger.video.AudioRecoder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v14, resolved type: org.telegram.messenger.video.AudioRecoder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r64v10, resolved type: org.telegram.messenger.video.AudioRecoder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v15, resolved type: org.telegram.messenger.video.AudioRecoder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r64v11, resolved type: org.telegram.messenger.video.AudioRecoder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v16, resolved type: org.telegram.messenger.video.AudioRecoder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r64v12, resolved type: org.telegram.messenger.video.AudioRecoder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v17, resolved type: org.telegram.messenger.video.AudioRecoder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r64v13, resolved type: org.telegram.messenger.video.AudioRecoder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v18, resolved type: org.telegram.messenger.video.AudioRecoder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v19, resolved type: org.telegram.messenger.video.AudioRecoder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r64v14, resolved type: org.telegram.messenger.video.AudioRecoder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r64v15, resolved type: org.telegram.messenger.video.AudioRecoder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v20, resolved type: org.telegram.messenger.video.AudioRecoder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r64v16, resolved type: org.telegram.messenger.video.AudioRecoder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v21, resolved type: org.telegram.messenger.video.AudioRecoder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r64v17, resolved type: org.telegram.messenger.video.AudioRecoder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v22, resolved type: org.telegram.messenger.video.AudioRecoder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v23, resolved type: org.telegram.messenger.video.AudioRecoder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r64v18, resolved type: org.telegram.messenger.video.AudioRecoder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v24, resolved type: org.telegram.messenger.video.AudioRecoder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v25, resolved type: org.telegram.messenger.video.AudioRecoder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v26, resolved type: org.telegram.messenger.video.AudioRecoder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v54, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v58, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v59, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v27, resolved type: org.telegram.messenger.video.AudioRecoder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v62, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v63, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v28, resolved type: org.telegram.messenger.video.AudioRecoder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v29, resolved type: org.telegram.messenger.video.AudioRecoder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v30, resolved type: org.telegram.messenger.video.AudioRecoder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v31, resolved type: org.telegram.messenger.video.AudioRecoder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v32, resolved type: org.telegram.messenger.video.AudioRecoder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v187, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v188, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v193, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v194, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v195, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v196, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v197, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v199, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v201, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v203, resolved type: boolean} */
    /* JADX WARNING: type inference failed for: r4v67, types: [int] */
    /* JADX WARNING: Code restructure failed: missing block: B:153:0x029d, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:154:0x029e, code lost:
        r10 = r81;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:155:0x02a2, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:156:0x02a3, code lost:
        r10 = r81;
        r2 = r0;
        r8 = r1;
        r6 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:176:0x02db, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:177:0x02dc, code lost:
        r10 = r81;
        r2 = r0;
        r8 = r1;
        r49 = r5;
        r1 = r6;
        r26 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:178:0x02e6, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:179:0x02e7, code lost:
        r10 = r81;
        r2 = r0;
        r8 = r1;
        r1 = r6;
        r26 = null;
        r49 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:180:0x02f0, code lost:
        r61 = null;
        r67 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:191:0x0316, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:192:0x0317, code lost:
        r2 = r0;
        r8 = r1;
        r1 = r6;
        r49 = r9;
        r67 = r10;
        r26 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:211:0x0361, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:256:0x0422, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:257:0x0424, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:258:0x0426, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:259:0x0427, code lost:
        r11 = r85;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:260:0x0429, code lost:
        r42 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:261:0x042b, code lost:
        r2 = r0;
        r8 = r1;
        r1 = r6;
        r67 = r10;
        r61 = r42;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:264:0x0441, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:265:0x0442, code lost:
        r11 = r85;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:282:0x049d, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:283:0x049e, code lost:
        r2 = r0;
        r61 = r8;
        r67 = r10;
        r10 = r81;
        r8 = r1;
        r1 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:304:0x0504, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:305:0x0505, code lost:
        r2 = r0;
        r1 = r6;
        r61 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:334:0x0565, code lost:
        if (r5.presentationTimeUs < r11) goto L_0x0567;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:349:0x05aa, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:350:0x05ab, code lost:
        r11 = r83;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:354:0x05c0, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:355:0x05c1, code lost:
        r11 = r83;
        r61 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:366:0x05ff, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:369:0x0608, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:370:0x0609, code lost:
        r11 = r83;
        r61 = r8;
        r2 = r0;
        r8 = r1;
        r1 = r6;
        r67 = r10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x00cf, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x00d0, code lost:
        r10 = r81;
        r6 = r82;
        r1 = r0;
        r12 = r13;
        r4 = r17;
        r5 = r18;
        r7 = r23;
        r11 = r24;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x00df, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x00e0, code lost:
        r15 = r80;
        r12 = r79;
        r10 = r81;
        r6 = r82;
        r1 = r0;
        r5 = " framerate: ";
        r7 = " size: ";
        r11 = "x";
        r4 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x00f0, code lost:
        r9 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:491:0x0824, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:492:0x0825, code lost:
        r9 = r79;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:561:0x094a, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:562:0x094b, code lost:
        r1 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:564:?, code lost:
        org.telegram.messenger.FileLog.e((java.lang.Throwable) r1);
        r9 = true;
        r1 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:576:0x098f, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:577:0x0990, code lost:
        r2 = r0;
        r1 = r6;
        r49 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:578:0x0995, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:579:0x0996, code lost:
        r8 = r49;
        r2 = r0;
        r1 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:580:0x099a, code lost:
        r26 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:592:0x0a30, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:593:0x0a31, code lost:
        r32 = r6;
        r49 = r8;
        r69 = r9;
        r67 = r10;
        r8 = r62;
        r10 = r81;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:594:0x0a42, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:595:0x0a43, code lost:
        r32 = r6;
        r67 = r10;
        r69 = r26;
        r8 = r62;
        r10 = r81;
        r6 = r82;
        r2 = r0;
        r1 = r32;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:606:0x0a8b, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:621:0x0ac0, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:622:0x0ac1, code lost:
        r1 = r32;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:631:0x0b72, code lost:
        r0 = e;
        r1 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:632:0x0b73, code lost:
        r6 = r82;
        r2 = r0;
        r1 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:634:0x0b7a, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:635:0x0b7b, code lost:
        r1 = r6;
        r67 = r10;
        r69 = r26;
        r8 = r62;
        r10 = r81;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:636:0x0b89, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:637:0x0b8a, code lost:
        r61 = r8;
        r67 = r10;
        r69 = r26;
        r10 = r81;
        r8 = r1;
        r1 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:638:0x0b98, code lost:
        r6 = r82;
        r2 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:639:0x0b9d, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:640:0x0b9e, code lost:
        r8 = r1;
        r1 = r6;
        r67 = r10;
        r69 = r26;
        r10 = r81;
        r6 = r82;
        r2 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:641:0x0bae, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:642:0x0baf, code lost:
        r8 = r1;
        r1 = r6;
        r49 = r9;
        r67 = r10;
        r10 = r81;
        r6 = r82;
        r2 = r0;
        r26 = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:643:0x0bc3, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:644:0x0bc4, code lost:
        r8 = r1;
        r1 = r6;
        r49 = r9;
        r67 = r10;
        r10 = r81;
        r6 = r82;
        r2 = r0;
        r26 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:645:0x0bd5, code lost:
        r61 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:646:0x0bd8, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:647:0x0bd9, code lost:
        r10 = r81;
        r8 = r1;
        r1 = r6;
        r49 = r9;
        r6 = r82;
        r2 = r0;
        r26 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:648:0x0be9, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:649:0x0bea, code lost:
        r10 = r81;
        r8 = r1;
        r1 = r6;
        r6 = r82;
        r2 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:650:0x0bf6, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:651:0x0bf7, code lost:
        r10 = r81;
        r8 = r1;
        r82 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:652:0x0bfd, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:653:0x0bfe, code lost:
        r8 = r1;
        r10 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:654:0x0CLASSNAME, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:655:0x0CLASSNAME, code lost:
        r10 = r81;
        r8 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:656:0x0CLASSNAME, code lost:
        r6 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:657:0x0c0b, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:658:0x0c0c, code lost:
        r10 = r81;
        r8 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:659:0x0c0f, code lost:
        r6 = r82;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:660:0x0CLASSNAME, code lost:
        r2 = r0;
        r1 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:661:0x0CLASSNAME, code lost:
        r26 = null;
        r49 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:662:0x0c1b, code lost:
        r61 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:668:0x0CLASSNAME, code lost:
        r9 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:702:0x0CLASSNAME, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:703:0x0CLASSNAME, code lost:
        r1 = r0;
        r9 = r35;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:704:0x0c8d, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:705:0x0c8f, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:706:0x0CLASSNAME, code lost:
        r12 = r79;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:707:0x0CLASSNAME, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:708:0x0CLASSNAME, code lost:
        r12 = r79;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:709:0x0CLASSNAME, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:710:0x0CLASSNAME, code lost:
        r12 = r79;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:713:0x0c9f, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:714:0x0ca0, code lost:
        r12 = r79;
        r4 = r17;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:719:0x0cad, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:720:0x0cae, code lost:
        r12 = r79;
        r4 = r17;
        r5 = r18;
        r7 = r23;
        r11 = r24;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:738:0x0cfd, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:739:0x0cfe, code lost:
        r2 = r0;
        r1 = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:748:0x0d3d, code lost:
        r1.release();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:752:?, code lost:
        r1.finishMovie();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:753:0x0d48, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:754:0x0d49, code lost:
        org.telegram.messenger.FileLog.e((java.lang.Throwable) r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:764:0x0d7c, code lost:
        r3.release();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:768:?, code lost:
        r3.finishMovie();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:769:0x0d87, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:770:0x0d88, code lost:
        org.telegram.messenger.FileLog.e((java.lang.Throwable) r0);
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:28:0x0098, B:89:0x01c6] */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:35:0x00c2, B:89:0x01c6] */
    /* JADX WARNING: Failed to insert additional move for type inference */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:122:0x0248 A[SYNTHETIC, Splitter:B:122:0x0248] */
    /* JADX WARNING: Removed duplicated region for block: B:125:0x0251  */
    /* JADX WARNING: Removed duplicated region for block: B:129:0x025d  */
    /* JADX WARNING: Removed duplicated region for block: B:130:0x0261  */
    /* JADX WARNING: Removed duplicated region for block: B:141:0x0284 A[SYNTHETIC, Splitter:B:141:0x0284] */
    /* JADX WARNING: Removed duplicated region for block: B:161:0x02b0  */
    /* JADX WARNING: Removed duplicated region for block: B:171:0x02cd A[SYNTHETIC, Splitter:B:171:0x02cd] */
    /* JADX WARNING: Removed duplicated region for block: B:182:0x02fc  */
    /* JADX WARNING: Removed duplicated region for block: B:189:0x0310 A[SYNTHETIC, Splitter:B:189:0x0310] */
    /* JADX WARNING: Removed duplicated region for block: B:195:0x0329 A[SYNTHETIC, Splitter:B:195:0x0329] */
    /* JADX WARNING: Removed duplicated region for block: B:204:0x0346 A[SYNTHETIC, Splitter:B:204:0x0346] */
    /* JADX WARNING: Removed duplicated region for block: B:213:0x0368  */
    /* JADX WARNING: Removed duplicated region for block: B:215:0x036e A[SYNTHETIC, Splitter:B:215:0x036e] */
    /* JADX WARNING: Removed duplicated region for block: B:226:0x039e  */
    /* JADX WARNING: Removed duplicated region for block: B:228:0x03a1  */
    /* JADX WARNING: Removed duplicated region for block: B:262:0x0434  */
    /* JADX WARNING: Removed duplicated region for block: B:266:0x0446  */
    /* JADX WARNING: Removed duplicated region for block: B:268:0x0454  */
    /* JADX WARNING: Removed duplicated region for block: B:269:0x0457  */
    /* JADX WARNING: Removed duplicated region for block: B:273:0x046e A[Catch:{ Exception -> 0x0b89, all -> 0x0cfd }] */
    /* JADX WARNING: Removed duplicated region for block: B:279:0x0494 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:286:0x04b1 A[SYNTHETIC, Splitter:B:286:0x04b1] */
    /* JADX WARNING: Removed duplicated region for block: B:359:0x05e2 A[Catch:{ Exception -> 0x05ff, all -> 0x0cfd }] */
    /* JADX WARNING: Removed duplicated region for block: B:371:0x0614  */
    /* JADX WARNING: Removed duplicated region for block: B:374:0x062a A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:379:0x0649 A[Catch:{ Exception -> 0x0b7a, all -> 0x0cfd }] */
    /* JADX WARNING: Removed duplicated region for block: B:380:0x064c A[Catch:{ Exception -> 0x0b7a, all -> 0x0cfd }] */
    /* JADX WARNING: Removed duplicated region for block: B:384:0x0655  */
    /* JADX WARNING: Removed duplicated region for block: B:386:0x0672  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x0103 A[SYNTHETIC, Splitter:B:46:0x0103] */
    /* JADX WARNING: Removed duplicated region for block: B:501:0x0858 A[Catch:{ Exception -> 0x0b7a, all -> 0x0cfd }] */
    /* JADX WARNING: Removed duplicated region for block: B:502:0x086e A[Catch:{ Exception -> 0x0b7a, all -> 0x0cfd }] */
    /* JADX WARNING: Removed duplicated region for block: B:601:0x0a81 A[Catch:{ Exception -> 0x0ac0, all -> 0x0cfd }] */
    /* JADX WARNING: Removed duplicated region for block: B:620:0x0abb A[Catch:{ Exception -> 0x0b72, all -> 0x0cfd }] */
    /* JADX WARNING: Removed duplicated region for block: B:667:0x0CLASSNAME A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0CLASSNAME A[Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0cfd }] */
    /* JADX WARNING: Removed duplicated region for block: B:693:0x0c6a A[Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0cfd }] */
    /* JADX WARNING: Removed duplicated region for block: B:695:0x0c6f A[Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0cfd }] */
    /* JADX WARNING: Removed duplicated region for block: B:697:0x0CLASSNAME A[Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0cfd }] */
    /* JADX WARNING: Removed duplicated region for block: B:699:0x0c7f A[Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0cfd }] */
    /* JADX WARNING: Removed duplicated region for block: B:721:0x0cba  */
    /* JADX WARNING: Removed duplicated region for block: B:724:0x0cc7  */
    /* JADX WARNING: Removed duplicated region for block: B:727:0x0cce A[SYNTHETIC, Splitter:B:727:0x0cce] */
    /* JADX WARNING: Removed duplicated region for block: B:738:0x0cfd A[ExcHandler: all (r0v6 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:89:0x01c6] */
    /* JADX WARNING: Removed duplicated region for block: B:748:0x0d3d  */
    /* JADX WARNING: Removed duplicated region for block: B:751:0x0d44 A[SYNTHETIC, Splitter:B:751:0x0d44] */
    /* JADX WARNING: Removed duplicated region for block: B:757:0x0d53  */
    /* JADX WARNING: Removed duplicated region for block: B:759:0x0d73 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:764:0x0d7c  */
    /* JADX WARNING: Removed duplicated region for block: B:767:0x0d83 A[SYNTHETIC, Splitter:B:767:0x0d83] */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x0172 A[Catch:{ Exception -> 0x01a9, all -> 0x0cfd }] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean convertVideoInternal(java.lang.String r75, java.io.File r76, int r77, boolean r78, int r79, int r80, int r81, int r82, long r83, long r85, long r87, boolean r89, boolean r90) {
        /*
            r74 = this;
            r14 = r74
            r13 = r75
            r15 = r77
            r12 = r79
            r11 = r80
            r9 = r81
            r10 = r82
            r7 = r83
            r5 = r85
            java.lang.String r4 = "x"
            java.lang.String r3 = " size: "
            java.lang.String r2 = " framerate: "
            java.lang.String r1 = "bitrate: "
            android.media.MediaCodec$BufferInfo r9 = new android.media.MediaCodec$BufferInfo     // Catch:{ Exception -> 0x0d02, all -> 0x0cfd }
            r9.<init>()     // Catch:{ Exception -> 0x0d02, all -> 0x0cfd }
            r18 = r1
            org.telegram.messenger.video.Mp4Movie r1 = new org.telegram.messenger.video.Mp4Movie     // Catch:{ Exception -> 0x0cef, all -> 0x0cfd }
            r1.<init>()     // Catch:{ Exception -> 0x0cef, all -> 0x0cfd }
            r7 = r76
            r1.setCacheFile(r7)     // Catch:{ Exception -> 0x0cef, all -> 0x0cfd }
            r1.setRotation(r15)     // Catch:{ Exception -> 0x0cef, all -> 0x0cfd }
            r1.setSize(r12, r11)     // Catch:{ Exception -> 0x0cef, all -> 0x0cfd }
            org.telegram.messenger.video.MP4Builder r8 = new org.telegram.messenger.video.MP4Builder     // Catch:{ Exception -> 0x0cef, all -> 0x0cfd }
            r8.<init>()     // Catch:{ Exception -> 0x0cef, all -> 0x0cfd }
            r15 = r78
            org.telegram.messenger.video.MP4Builder r1 = r8.createMovie(r1, r15)     // Catch:{ Exception -> 0x0cef, all -> 0x0cfd }
            r14.mediaMuxer = r1     // Catch:{ Exception -> 0x0cef, all -> 0x0cfd }
            android.media.MediaExtractor r1 = new android.media.MediaExtractor     // Catch:{ Exception -> 0x0cef, all -> 0x0cfd }
            r1.<init>()     // Catch:{ Exception -> 0x0cef, all -> 0x0cfd }
            r14.extractor = r1     // Catch:{ Exception -> 0x0cef, all -> 0x0cfd }
            r1.setDataSource(r13)     // Catch:{ Exception -> 0x0cef, all -> 0x0cfd }
            r12 = r87
            float r1 = (float) r12
            r8 = 1148846080(0x447a0000, float:1000.0)
            float r19 = r1 / r8
            r74.checkConversionCanceled()     // Catch:{ Exception -> 0x0ceb, all -> 0x0cfd }
            android.media.MediaExtractor r1 = r14.extractor     // Catch:{ Exception -> 0x0ceb, all -> 0x0cfd }
            r11 = 0
            int r1 = org.telegram.messenger.MediaController.findTrack(r1, r11)     // Catch:{ Exception -> 0x0cda, all -> 0x0cfd }
            r11 = -1
            if (r10 == r11) goto L_0x0078
            android.media.MediaExtractor r8 = r14.extractor     // Catch:{ Exception -> 0x0065, all -> 0x0cfd }
            r11 = 1
            int r8 = org.telegram.messenger.MediaController.findTrack(r8, r11)     // Catch:{ Exception -> 0x0065, all -> 0x0cfd }
            goto L_0x0079
        L_0x0065:
            r0 = move-exception
            r12 = r79
            r15 = r80
            r1 = r0
            r5 = r2
            r7 = r3
            r11 = r4
            r6 = r10
            r4 = r18
            r9 = 0
            r62 = 1
            r10 = r81
            goto L_0x0d12
        L_0x0078:
            r8 = -1
        L_0x0079:
            java.lang.String r11 = "mime"
            java.lang.String r12 = "video/avc"
            if (r1 < 0) goto L_0x0091
            android.media.MediaExtractor r13 = r14.extractor     // Catch:{ Exception -> 0x0065, all -> 0x0cfd }
            android.media.MediaFormat r13 = r13.getTrackFormat(r1)     // Catch:{ Exception -> 0x0065, all -> 0x0cfd }
            java.lang.String r13 = r13.getString(r11)     // Catch:{ Exception -> 0x0065, all -> 0x0cfd }
            boolean r13 = r13.equals(r12)     // Catch:{ Exception -> 0x0065, all -> 0x0cfd }
            if (r13 != 0) goto L_0x0091
            r13 = 1
            goto L_0x0092
        L_0x0091:
            r13 = 0
        L_0x0092:
            if (r89 != 0) goto L_0x00f5
            if (r13 == 0) goto L_0x0098
            goto L_0x00f5
        L_0x0098:
            android.media.MediaExtractor r8 = r14.extractor     // Catch:{ Exception -> 0x00df, all -> 0x0cfd }
            org.telegram.messenger.video.MP4Builder r11 = r14.mediaMuxer     // Catch:{ Exception -> 0x00df, all -> 0x0cfd }
            r1 = -1
            r13 = r18
            if (r10 == r1) goto L_0x00a3
            r12 = 1
            goto L_0x00a4
        L_0x00a3:
            r12 = 0
        L_0x00a4:
            r1 = r74
            r15 = r2
            r2 = r8
            r8 = r3
            r3 = r11
            r11 = r4
            r4 = r9
            r5 = r83
            r9 = r8
            r7 = r85
            r23 = r9
            r18 = r15
            r15 = 1
            r9 = r87
            r15 = r80
            r24 = r11
            r11 = r76
            r17 = r13
            r13 = r79
            r1.readAndWriteTracks(r2, r3, r4, r5, r7, r9, r11, r12)     // Catch:{ Exception -> 0x00cf, all -> 0x0cfd }
            r10 = r81
            r6 = r82
            r12 = r13
        L_0x00ca:
            r9 = 0
            r35 = 0
            goto L_0x0cc3
        L_0x00cf:
            r0 = move-exception
            r10 = r81
            r6 = r82
            r1 = r0
            r12 = r13
            r4 = r17
            r5 = r18
            r7 = r23
            r11 = r24
            goto L_0x00f0
        L_0x00df:
            r0 = move-exception
            r15 = r80
            r17 = r18
            r12 = r79
            r10 = r81
            r6 = r82
            r1 = r0
            r5 = r2
            r7 = r3
            r11 = r4
            r4 = r17
        L_0x00f0:
            r9 = 0
            r62 = 1
            goto L_0x0d12
        L_0x00f5:
            r13 = r79
            r15 = r80
            r23 = r3
            r24 = r4
            r17 = r18
            r18 = r2
            if (r1 < 0) goto L_0x0cba
            java.lang.String r4 = android.os.Build.MANUFACTURER     // Catch:{ Exception -> 0x0c0b, all -> 0x0cfd }
            java.lang.String r4 = r4.toLowerCase()     // Catch:{ Exception -> 0x0c0b, all -> 0x0cfd }
            int r5 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0c0b, all -> 0x0cfd }
            r25 = 4
            r7 = 16
            r10 = 2
            r6 = 18
            if (r5 >= r6) goto L_0x01bd
            android.media.MediaCodecInfo r5 = org.telegram.messenger.MediaController.selectCodec(r12)     // Catch:{ Exception -> 0x01a9, all -> 0x0cfd }
            int r29 = org.telegram.messenger.MediaController.selectColorFormat(r5, r12)     // Catch:{ Exception -> 0x01a9, all -> 0x0cfd }
            if (r29 == 0) goto L_0x01a1
            java.lang.String r2 = r5.getName()     // Catch:{ Exception -> 0x01a9, all -> 0x0cfd }
            java.lang.String r3 = "OMX.qcom."
            boolean r3 = r2.contains(r3)     // Catch:{ Exception -> 0x01a9, all -> 0x0cfd }
            if (r3 == 0) goto L_0x0144
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x01a9, all -> 0x0cfd }
            if (r2 != r7) goto L_0x0141
            java.lang.String r2 = "lge"
            boolean r2 = r4.equals(r2)     // Catch:{ Exception -> 0x01a9, all -> 0x0cfd }
            if (r2 != 0) goto L_0x013e
            java.lang.String r2 = "nokia"
            boolean r2 = r4.equals(r2)     // Catch:{ Exception -> 0x01a9, all -> 0x0cfd }
            if (r2 == 0) goto L_0x0141
        L_0x013e:
            r2 = 1
        L_0x013f:
            r3 = 1
            goto L_0x016e
        L_0x0141:
            r2 = 1
        L_0x0142:
            r3 = 0
            goto L_0x016e
        L_0x0144:
            java.lang.String r3 = "OMX.Intel."
            boolean r3 = r2.contains(r3)     // Catch:{ Exception -> 0x01a9, all -> 0x0cfd }
            if (r3 == 0) goto L_0x014e
            r2 = 2
            goto L_0x0142
        L_0x014e:
            java.lang.String r3 = "OMX.MTK.VIDEO.ENCODER.AVC"
            boolean r3 = r2.equals(r3)     // Catch:{ Exception -> 0x01a9, all -> 0x0cfd }
            if (r3 == 0) goto L_0x0158
            r2 = 3
            goto L_0x0142
        L_0x0158:
            java.lang.String r3 = "OMX.SEC.AVC.Encoder"
            boolean r3 = r2.equals(r3)     // Catch:{ Exception -> 0x01a9, all -> 0x0cfd }
            if (r3 == 0) goto L_0x0162
            r2 = 4
            goto L_0x013f
        L_0x0162:
            java.lang.String r3 = "OMX.TI.DUCATI1.VIDEO.H264E"
            boolean r2 = r2.equals(r3)     // Catch:{ Exception -> 0x01a9, all -> 0x0cfd }
            if (r2 == 0) goto L_0x016c
            r2 = 5
            goto L_0x0142
        L_0x016c:
            r2 = 0
            goto L_0x0142
        L_0x016e:
            boolean r32 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x01a9, all -> 0x0cfd }
            if (r32 == 0) goto L_0x019c
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01a9, all -> 0x0cfd }
            r6.<init>()     // Catch:{ Exception -> 0x01a9, all -> 0x0cfd }
            java.lang.String r7 = "codec = "
            r6.append(r7)     // Catch:{ Exception -> 0x01a9, all -> 0x0cfd }
            java.lang.String r5 = r5.getName()     // Catch:{ Exception -> 0x01a9, all -> 0x0cfd }
            r6.append(r5)     // Catch:{ Exception -> 0x01a9, all -> 0x0cfd }
            java.lang.String r5 = " manufacturer = "
            r6.append(r5)     // Catch:{ Exception -> 0x01a9, all -> 0x0cfd }
            r6.append(r4)     // Catch:{ Exception -> 0x01a9, all -> 0x0cfd }
            java.lang.String r5 = "device = "
            r6.append(r5)     // Catch:{ Exception -> 0x01a9, all -> 0x0cfd }
            java.lang.String r5 = android.os.Build.MODEL     // Catch:{ Exception -> 0x01a9, all -> 0x0cfd }
            r6.append(r5)     // Catch:{ Exception -> 0x01a9, all -> 0x0cfd }
            java.lang.String r5 = r6.toString()     // Catch:{ Exception -> 0x01a9, all -> 0x0cfd }
            org.telegram.messenger.FileLog.d(r5)     // Catch:{ Exception -> 0x01a9, all -> 0x0cfd }
        L_0x019c:
            r7 = r29
            r29 = r3
            goto L_0x01c6
        L_0x01a1:
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x01a9, all -> 0x0cfd }
            java.lang.String r3 = "no supported color format"
            r2.<init>(r3)     // Catch:{ Exception -> 0x01a9, all -> 0x0cfd }
            throw r2     // Catch:{ Exception -> 0x01a9, all -> 0x0cfd }
        L_0x01a9:
            r0 = move-exception
            r10 = r81
            r6 = r82
        L_0x01ae:
            r2 = r0
            r8 = r1
        L_0x01b0:
            r1 = 0
            r26 = 0
            r49 = 0
            r61 = 0
            r62 = 1
            r66 = 0
            goto L_0x0c1d
        L_0x01bd:
            r29 = 2130708361(0x7var_, float:1.701803E38)
            r2 = 0
            r7 = 2130708361(0x7var_, float:1.701803E38)
            r29 = 0
        L_0x01c6:
            boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0c0b, all -> 0x0cfd }
            if (r3 == 0) goto L_0x01de
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01a9, all -> 0x0cfd }
            r3.<init>()     // Catch:{ Exception -> 0x01a9, all -> 0x0cfd }
            java.lang.String r5 = "colorFormat = "
            r3.append(r5)     // Catch:{ Exception -> 0x01a9, all -> 0x0cfd }
            r3.append(r7)     // Catch:{ Exception -> 0x01a9, all -> 0x0cfd }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x01a9, all -> 0x0cfd }
            org.telegram.messenger.FileLog.d(r3)     // Catch:{ Exception -> 0x01a9, all -> 0x0cfd }
        L_0x01de:
            int r3 = r13 * r15
            int r5 = r3 * 3
            int r5 = r5 / r10
            if (r2 != 0) goto L_0x01fd
            int r2 = r15 % 16
            if (r2 == 0) goto L_0x0231
            int r2 = r15 % 16
            r3 = 16
            int r2 = 16 - r2
            int r2 = r2 + r15
            int r2 = r2 - r15
            int r2 = r2 * r13
            int r3 = r2 * 5
            int r3 = r3 / 4
        L_0x01f7:
            int r5 = r5 + r3
        L_0x01f8:
            r40 = r2
            r27 = r5
            goto L_0x0235
        L_0x01fd:
            r6 = 1
            if (r2 != r6) goto L_0x0213
            java.lang.String r2 = r4.toLowerCase()     // Catch:{ Exception -> 0x01a9, all -> 0x0cfd }
            java.lang.String r4 = "lge"
            boolean r2 = r2.equals(r4)     // Catch:{ Exception -> 0x01a9, all -> 0x0cfd }
            if (r2 != 0) goto L_0x0231
            int r2 = r3 + 2047
            r2 = r2 & -2048(0xffffffffffffvar_, float:NaN)
            int r2 = r2 - r3
            int r5 = r5 + r2
            goto L_0x01f8
        L_0x0213:
            r3 = 5
            if (r2 != r3) goto L_0x0217
            goto L_0x0231
        L_0x0217:
            r3 = 3
            if (r2 != r3) goto L_0x0231
            java.lang.String r2 = "baidu"
            boolean r2 = r4.equals(r2)     // Catch:{ Exception -> 0x01a9, all -> 0x0cfd }
            if (r2 == 0) goto L_0x0231
            int r2 = r15 % 16
            r3 = 16
            int r2 = 16 - r2
            int r2 = r2 + r15
            int r2 = r2 - r15
            int r2 = r2 * r13
            int r3 = r2 * 5
            int r3 = r3 / 4
            goto L_0x01f7
        L_0x0231:
            r27 = r5
            r40 = 0
        L_0x0235:
            android.media.MediaExtractor r2 = r14.extractor     // Catch:{ Exception -> 0x0c0b, all -> 0x0cfd }
            r2.selectTrack(r1)     // Catch:{ Exception -> 0x0c0b, all -> 0x0cfd }
            android.media.MediaExtractor r2 = r14.extractor     // Catch:{ Exception -> 0x0c0b, all -> 0x0cfd }
            android.media.MediaFormat r2 = r2.getTrackFormat(r1)     // Catch:{ Exception -> 0x0c0b, all -> 0x0cfd }
            r5 = 0
            r3 = r83
            int r33 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r33 <= 0) goto L_0x0251
            android.media.MediaExtractor r10 = r14.extractor     // Catch:{ Exception -> 0x01a9, all -> 0x0cfd }
            r5 = 0
            r10.seekTo(r3, r5)     // Catch:{ Exception -> 0x01a9, all -> 0x0cfd }
            r22 = r9
            goto L_0x025b
        L_0x0251:
            r5 = 0
            android.media.MediaExtractor r6 = r14.extractor     // Catch:{ Exception -> 0x0c0b, all -> 0x0cfd }
            r22 = r9
            r9 = 0
            r6.seekTo(r9, r5)     // Catch:{ Exception -> 0x0c0b, all -> 0x0cfd }
        L_0x025b:
            if (r82 > 0) goto L_0x0261
            r9 = 921600(0xe1000, float:1.291437E-39)
            goto L_0x0263
        L_0x0261:
            r9 = r82
        L_0x0263:
            android.media.MediaFormat r10 = android.media.MediaFormat.createVideoFormat(r12, r13, r15)     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0cfd }
            java.lang.String r5 = "color-format"
            r10.setInteger(r5, r7)     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0cfd }
            java.lang.String r5 = "bitrate"
            r10.setInteger(r5, r9)     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0cfd }
            java.lang.String r5 = "frame-rate"
            r6 = r81
            r10.setInteger(r5, r6)     // Catch:{ Exception -> 0x0bfd, all -> 0x0cfd }
            java.lang.String r5 = "i-frame-interval"
            r6 = 2
            r10.setInteger(r5, r6)     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0cfd }
            int r5 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0cfd }
            r6 = 23
            if (r5 >= r6) goto L_0x02aa
            int r5 = java.lang.Math.min(r15, r13)     // Catch:{ Exception -> 0x02a2, all -> 0x0cfd }
            r6 = 480(0x1e0, float:6.73E-43)
            if (r5 > r6) goto L_0x02aa
            r5 = 921600(0xe1000, float:1.291437E-39)
            if (r9 <= r5) goto L_0x0295
            r6 = 921600(0xe1000, float:1.291437E-39)
            goto L_0x0296
        L_0x0295:
            r6 = r9
        L_0x0296:
            java.lang.String r5 = "bitrate"
            r10.setInteger(r5, r6)     // Catch:{ Exception -> 0x029d, all -> 0x0cfd }
            r9 = r6
            goto L_0x02aa
        L_0x029d:
            r0 = move-exception
            r10 = r81
            goto L_0x01ae
        L_0x02a2:
            r0 = move-exception
            r10 = r81
            r2 = r0
            r8 = r1
            r6 = r9
            goto L_0x01b0
        L_0x02aa:
            int r5 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0bf6, all -> 0x0cfd }
            r6 = 18
            if (r5 >= r6) goto L_0x02bc
            java.lang.String r5 = "stride"
            int r6 = r13 + 32
            r10.setInteger(r5, r6)     // Catch:{ Exception -> 0x02a2, all -> 0x0cfd }
            java.lang.String r5 = "slice-height"
            r10.setInteger(r5, r15)     // Catch:{ Exception -> 0x02a2, all -> 0x0cfd }
        L_0x02bc:
            android.media.MediaCodec r6 = android.media.MediaCodec.createEncoderByType(r12)     // Catch:{ Exception -> 0x0bf6, all -> 0x0cfd }
            r82 = r9
            r5 = 0
            r9 = 1
            r6.configure(r10, r5, r5, r9)     // Catch:{ Exception -> 0x0be9, all -> 0x0cfd }
            int r5 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0be9, all -> 0x0cfd }
            r9 = 18
            if (r5 < r9) goto L_0x02fc
            org.telegram.messenger.video.InputSurface r5 = new org.telegram.messenger.video.InputSurface     // Catch:{ Exception -> 0x02e6, all -> 0x0cfd }
            android.view.Surface r9 = r6.createInputSurface()     // Catch:{ Exception -> 0x02e6, all -> 0x0cfd }
            r5.<init>(r9)     // Catch:{ Exception -> 0x02e6, all -> 0x0cfd }
            r5.makeCurrent()     // Catch:{ Exception -> 0x02db, all -> 0x0cfd }
            r9 = r5
            goto L_0x02fd
        L_0x02db:
            r0 = move-exception
            r10 = r81
            r2 = r0
            r8 = r1
            r49 = r5
            r1 = r6
            r26 = 0
            goto L_0x02f0
        L_0x02e6:
            r0 = move-exception
            r10 = r81
            r2 = r0
            r8 = r1
            r1 = r6
            r26 = 0
            r49 = 0
        L_0x02f0:
            r61 = 0
            r62 = 1
            r66 = 0
            r67 = 0
        L_0x02f8:
            r6 = r82
            goto L_0x0c1f
        L_0x02fc:
            r9 = 0
        L_0x02fd:
            r6.start()     // Catch:{ Exception -> 0x0bd8, all -> 0x0cfd }
            java.lang.String r5 = r2.getString(r11)     // Catch:{ Exception -> 0x0bd8, all -> 0x0cfd }
            android.media.MediaCodec r10 = android.media.MediaCodec.createDecoderByType(r5)     // Catch:{ Exception -> 0x0bd8, all -> 0x0cfd }
            int r5 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0bc3, all -> 0x0cfd }
            r36 = r7
            r7 = 18
            if (r5 < r7) goto L_0x0329
            org.telegram.messenger.video.OutputSurface r5 = new org.telegram.messenger.video.OutputSurface     // Catch:{ Exception -> 0x0316, all -> 0x0cfd }
            r5.<init>()     // Catch:{ Exception -> 0x0316, all -> 0x0cfd }
            goto L_0x0330
        L_0x0316:
            r0 = move-exception
            r2 = r0
            r8 = r1
            r1 = r6
            r49 = r9
            r67 = r10
            r26 = 0
        L_0x0320:
            r61 = 0
        L_0x0322:
            r62 = 1
            r66 = 0
            r10 = r81
            goto L_0x02f8
        L_0x0329:
            org.telegram.messenger.video.OutputSurface r5 = new org.telegram.messenger.video.OutputSurface     // Catch:{ Exception -> 0x0bc3, all -> 0x0cfd }
            r7 = r77
            r5.<init>(r13, r15, r7)     // Catch:{ Exception -> 0x0bc3, all -> 0x0cfd }
        L_0x0330:
            android.view.Surface r7 = r5.getSurface()     // Catch:{ Exception -> 0x0bae, all -> 0x0cfd }
            r26 = r5
            r49 = r9
            r5 = 0
            r9 = 0
            r10.configure(r2, r7, r9, r5)     // Catch:{ Exception -> 0x0b9d, all -> 0x0cfd }
            r10.start()     // Catch:{ Exception -> 0x0b9d, all -> 0x0cfd }
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b9d, all -> 0x0cfd }
            r7 = 21
            if (r2 >= r7) goto L_0x0368
            java.nio.ByteBuffer[] r5 = r10.getInputBuffers()     // Catch:{ Exception -> 0x0361, all -> 0x0cfd }
            java.nio.ByteBuffer[] r2 = r6.getOutputBuffers()     // Catch:{ Exception -> 0x0361, all -> 0x0cfd }
            int r9 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0361, all -> 0x0cfd }
            r7 = 18
            if (r9 >= r7) goto L_0x035c
            java.nio.ByteBuffer[] r7 = r6.getInputBuffers()     // Catch:{ Exception -> 0x0361, all -> 0x0cfd }
            r9 = r5
            r50 = r7
            goto L_0x035f
        L_0x035c:
            r9 = r5
            r50 = 0
        L_0x035f:
            r5 = r2
            goto L_0x036c
        L_0x0361:
            r0 = move-exception
        L_0x0362:
            r2 = r0
            r8 = r1
            r1 = r6
            r67 = r10
            goto L_0x0320
        L_0x0368:
            r5 = 0
            r9 = 0
            r50 = 0
        L_0x036c:
            if (r8 < 0) goto L_0x0446
            android.media.MediaExtractor r2 = r14.extractor     // Catch:{ Exception -> 0x0441, all -> 0x0cfd }
            android.media.MediaFormat r2 = r2.getTrackFormat(r8)     // Catch:{ Exception -> 0x0441, all -> 0x0cfd }
            java.lang.String r7 = r2.getString(r11)     // Catch:{ Exception -> 0x0441, all -> 0x0cfd }
            r39 = r5
            java.lang.String r5 = "audio/mp4a-latm"
            boolean r5 = r7.equals(r5)     // Catch:{ Exception -> 0x0441, all -> 0x0cfd }
            if (r5 != 0) goto L_0x0391
            java.lang.String r5 = r2.getString(r11)     // Catch:{ Exception -> 0x0361, all -> 0x0cfd }
            java.lang.String r7 = "audio/mpeg"
            boolean r5 = r5.equals(r7)     // Catch:{ Exception -> 0x0361, all -> 0x0cfd }
            if (r5 == 0) goto L_0x038f
            goto L_0x0391
        L_0x038f:
            r5 = 0
            goto L_0x0392
        L_0x0391:
            r5 = 1
        L_0x0392:
            java.lang.String r7 = r2.getString(r11)     // Catch:{ Exception -> 0x0441, all -> 0x0cfd }
            java.lang.String r11 = "audio/unknown"
            boolean r7 = r7.equals(r11)     // Catch:{ Exception -> 0x0441, all -> 0x0cfd }
            if (r7 == 0) goto L_0x039f
            r8 = -1
        L_0x039f:
            if (r8 < 0) goto L_0x0434
            if (r5 == 0) goto L_0x03e4
            org.telegram.messenger.video.MP4Builder r7 = r14.mediaMuxer     // Catch:{ Exception -> 0x0361, all -> 0x0cfd }
            r11 = 1
            int r7 = r7.addTrack(r2, r11)     // Catch:{ Exception -> 0x0361, all -> 0x0cfd }
            android.media.MediaExtractor r11 = r14.extractor     // Catch:{ Exception -> 0x0361, all -> 0x0cfd }
            r11.selectTrack(r8)     // Catch:{ Exception -> 0x0361, all -> 0x0cfd }
            java.lang.String r11 = "max-input-size"
            int r2 = r2.getInteger(r11)     // Catch:{ Exception -> 0x0361, all -> 0x0cfd }
            java.nio.ByteBuffer r2 = java.nio.ByteBuffer.allocateDirect(r2)     // Catch:{ Exception -> 0x0361, all -> 0x0cfd }
            r33 = 0
            int r11 = (r3 > r33 ? 1 : (r3 == r33 ? 0 : -1))
            if (r11 <= 0) goto L_0x03cc
            android.media.MediaExtractor r11 = r14.extractor     // Catch:{ Exception -> 0x0361, all -> 0x0cfd }
            r42 = r2
            r2 = 0
            r11.seekTo(r3, r2)     // Catch:{ Exception -> 0x0361, all -> 0x0cfd }
            r43 = r5
            r51 = r12
            goto L_0x03da
        L_0x03cc:
            r42 = r2
            android.media.MediaExtractor r2 = r14.extractor     // Catch:{ Exception -> 0x0361, all -> 0x0cfd }
            r43 = r5
            r51 = r12
            r5 = 0
            r11 = 0
            r2.seekTo(r11, r5)     // Catch:{ Exception -> 0x0361, all -> 0x0cfd }
        L_0x03da:
            r11 = r85
            r5 = r7
            r2 = r8
            r7 = r42
            r52 = r43
            goto L_0x043f
        L_0x03e4:
            r43 = r5
            r51 = r12
            android.media.MediaExtractor r5 = new android.media.MediaExtractor     // Catch:{ Exception -> 0x0441, all -> 0x0cfd }
            r5.<init>()     // Catch:{ Exception -> 0x0441, all -> 0x0cfd }
            r11 = r75
            r5.setDataSource(r11)     // Catch:{ Exception -> 0x0441, all -> 0x0cfd }
            r5.selectTrack(r8)     // Catch:{ Exception -> 0x0441, all -> 0x0cfd }
            r11 = 0
            int r7 = (r3 > r11 ? 1 : (r3 == r11 ? 0 : -1))
            if (r7 <= 0) goto L_0x0400
            r7 = 0
            r5.seekTo(r3, r7)     // Catch:{ Exception -> 0x0361, all -> 0x0cfd }
            goto L_0x0404
        L_0x0400:
            r7 = 0
            r5.seekTo(r11, r7)     // Catch:{ Exception -> 0x0441, all -> 0x0cfd }
        L_0x0404:
            org.telegram.messenger.video.AudioRecoder r7 = new org.telegram.messenger.video.AudioRecoder     // Catch:{ Exception -> 0x0441, all -> 0x0cfd }
            r7.<init>(r2, r5, r8)     // Catch:{ Exception -> 0x0441, all -> 0x0cfd }
            r7.startTime = r3     // Catch:{ Exception -> 0x0426, all -> 0x0cfd }
            r11 = r85
            r7.endTime = r11     // Catch:{ Exception -> 0x0424, all -> 0x0cfd }
            org.telegram.messenger.video.MP4Builder r2 = r14.mediaMuxer     // Catch:{ Exception -> 0x0424, all -> 0x0cfd }
            android.media.MediaFormat r5 = r7.format     // Catch:{ Exception -> 0x0424, all -> 0x0cfd }
            r42 = r7
            r7 = 1
            int r2 = r2.addTrack(r5, r7)     // Catch:{ Exception -> 0x0422, all -> 0x0cfd }
            r5 = r2
            r2 = r8
            r8 = r42
            r52 = r43
            r7 = 0
            goto L_0x0452
        L_0x0422:
            r0 = move-exception
            goto L_0x042b
        L_0x0424:
            r0 = move-exception
            goto L_0x0429
        L_0x0426:
            r0 = move-exception
            r11 = r85
        L_0x0429:
            r42 = r7
        L_0x042b:
            r2 = r0
            r8 = r1
            r1 = r6
            r67 = r10
            r61 = r42
            goto L_0x0322
        L_0x0434:
            r43 = r5
            r51 = r12
            r11 = r85
            r2 = r8
            r52 = r43
            r5 = -5
            r7 = 0
        L_0x043f:
            r8 = 0
            goto L_0x0452
        L_0x0441:
            r0 = move-exception
            r11 = r85
            goto L_0x0362
        L_0x0446:
            r39 = r5
            r51 = r12
            r11 = r85
            r2 = r8
            r5 = -5
            r7 = 0
            r8 = 0
            r52 = 1
        L_0x0452:
            if (r2 >= 0) goto L_0x0457
            r42 = 1
            goto L_0x0459
        L_0x0457:
            r42 = 0
        L_0x0459:
            r74.checkConversionCanceled()     // Catch:{ Exception -> 0x0b89, all -> 0x0cfd }
            r30 = -1
            r53 = 0
            r54 = 0
            r55 = 0
            r56 = 0
            r57 = 1
            r58 = 0
            r60 = -5
        L_0x046c:
            if (r53 == 0) goto L_0x048f
            if (r52 != 0) goto L_0x0473
            if (r42 != 0) goto L_0x0473
            goto L_0x048f
        L_0x0473:
            r61 = r8
            r67 = r10
            r12 = r13
            r4 = r17
            r5 = r18
            r7 = r23
            r11 = r24
            r9 = r49
            r2 = 0
            r35 = 0
            r62 = 1
            r10 = r81
            r8 = r1
            r1 = r6
            r6 = r82
            goto L_0x0c5e
        L_0x048f:
            r74.checkConversionCanceled()     // Catch:{ Exception -> 0x0b89, all -> 0x0cfd }
            if (r52 != 0) goto L_0x04ad
            if (r8 == 0) goto L_0x04ad
            org.telegram.messenger.video.MP4Builder r13 = r14.mediaMuxer     // Catch:{ Exception -> 0x049d, all -> 0x0cfd }
            boolean r13 = r8.step(r13, r5)     // Catch:{ Exception -> 0x049d, all -> 0x0cfd }
            goto L_0x04af
        L_0x049d:
            r0 = move-exception
            r2 = r0
            r61 = r8
            r67 = r10
            r62 = 1
            r66 = 0
            r10 = r81
            r8 = r1
            r1 = r6
            goto L_0x02f8
        L_0x04ad:
            r13 = r42
        L_0x04af:
            if (r54 != 0) goto L_0x0614
            android.media.MediaExtractor r3 = r14.extractor     // Catch:{ Exception -> 0x0608, all -> 0x0cfd }
            int r3 = r3.getSampleTrackIndex()     // Catch:{ Exception -> 0x0608, all -> 0x0cfd }
            if (r3 != r1) goto L_0x051c
            r61 = r5
            r4 = 2500(0x9c4, double:1.235E-320)
            int r3 = r10.dequeueInputBuffer(r4)     // Catch:{ Exception -> 0x049d, all -> 0x0cfd }
            if (r3 < 0) goto L_0x050b
            int r4 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x049d, all -> 0x0cfd }
            r5 = 21
            if (r4 >= r5) goto L_0x04cc
            r4 = r9[r3]     // Catch:{ Exception -> 0x049d, all -> 0x0cfd }
            goto L_0x04d0
        L_0x04cc:
            java.nio.ByteBuffer r4 = r10.getInputBuffer(r3)     // Catch:{ Exception -> 0x049d, all -> 0x0cfd }
        L_0x04d0:
            android.media.MediaExtractor r5 = r14.extractor     // Catch:{ Exception -> 0x049d, all -> 0x0cfd }
            r62 = r1
            r1 = 0
            int r45 = r5.readSampleData(r4, r1)     // Catch:{ Exception -> 0x0504, all -> 0x0cfd }
            if (r45 >= 0) goto L_0x04ed
            r44 = 0
            r45 = 0
            r46 = 0
            r48 = 4
            r42 = r10
            r43 = r3
            r42.queueInputBuffer(r43, r44, r45, r46, r48)     // Catch:{ Exception -> 0x0504, all -> 0x0cfd }
            r54 = 1
            goto L_0x050d
        L_0x04ed:
            r44 = 0
            android.media.MediaExtractor r1 = r14.extractor     // Catch:{ Exception -> 0x0504, all -> 0x0cfd }
            long r46 = r1.getSampleTime()     // Catch:{ Exception -> 0x0504, all -> 0x0cfd }
            r48 = 0
            r42 = r10
            r43 = r3
            r42.queueInputBuffer(r43, r44, r45, r46, r48)     // Catch:{ Exception -> 0x0504, all -> 0x0cfd }
            android.media.MediaExtractor r1 = r14.extractor     // Catch:{ Exception -> 0x0504, all -> 0x0cfd }
            r1.advance()     // Catch:{ Exception -> 0x0504, all -> 0x0cfd }
            goto L_0x050d
        L_0x0504:
            r0 = move-exception
            r2 = r0
            r1 = r6
            r61 = r8
            goto L_0x0602
        L_0x050b:
            r62 = r1
        L_0x050d:
            r11 = r83
            r63 = r7
            r5 = r22
            r64 = r61
            r1 = 0
            r22 = r2
            r61 = r8
            goto L_0x05e5
        L_0x051c:
            r62 = r1
            r61 = r5
            if (r52 == 0) goto L_0x05d3
            r1 = -1
            if (r2 == r1) goto L_0x05c6
            if (r3 != r2) goto L_0x05d3
            android.media.MediaExtractor r1 = r14.extractor     // Catch:{ Exception -> 0x05c0, all -> 0x0cfd }
            r3 = 0
            int r1 = r1.readSampleData(r7, r3)     // Catch:{ Exception -> 0x05c0, all -> 0x0cfd }
            r5 = r22
            r5.size = r1     // Catch:{ Exception -> 0x05c0, all -> 0x0cfd }
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x05c0, all -> 0x0cfd }
            r4 = 21
            if (r1 >= r4) goto L_0x0540
            r7.position(r3)     // Catch:{ Exception -> 0x0504, all -> 0x0cfd }
            int r1 = r5.size     // Catch:{ Exception -> 0x0504, all -> 0x0cfd }
            r7.limit(r1)     // Catch:{ Exception -> 0x0504, all -> 0x0cfd }
        L_0x0540:
            int r1 = r5.size     // Catch:{ Exception -> 0x05c0, all -> 0x0cfd }
            if (r1 < 0) goto L_0x0552
            android.media.MediaExtractor r1 = r14.extractor     // Catch:{ Exception -> 0x0504, all -> 0x0cfd }
            long r3 = r1.getSampleTime()     // Catch:{ Exception -> 0x0504, all -> 0x0cfd }
            r5.presentationTimeUs = r3     // Catch:{ Exception -> 0x0504, all -> 0x0cfd }
            android.media.MediaExtractor r1 = r14.extractor     // Catch:{ Exception -> 0x0504, all -> 0x0cfd }
            r1.advance()     // Catch:{ Exception -> 0x0504, all -> 0x0cfd }
            goto L_0x0557
        L_0x0552:
            r1 = 0
            r5.size = r1     // Catch:{ Exception -> 0x05c0, all -> 0x0cfd }
            r54 = 1
        L_0x0557:
            int r1 = r5.size     // Catch:{ Exception -> 0x05c0, all -> 0x0cfd }
            if (r1 <= 0) goto L_0x05b7
            r3 = 0
            int r1 = (r11 > r3 ? 1 : (r11 == r3 ? 0 : -1))
            if (r1 < 0) goto L_0x0567
            long r3 = r5.presentationTimeUs     // Catch:{ Exception -> 0x0504, all -> 0x0cfd }
            int r1 = (r3 > r11 ? 1 : (r3 == r11 ? 0 : -1))
            if (r1 >= 0) goto L_0x05b7
        L_0x0567:
            r1 = 0
            r5.offset = r1     // Catch:{ Exception -> 0x05c0, all -> 0x0cfd }
            android.media.MediaExtractor r3 = r14.extractor     // Catch:{ Exception -> 0x05c0, all -> 0x0cfd }
            int r3 = r3.getSampleFlags()     // Catch:{ Exception -> 0x05c0, all -> 0x0cfd }
            r5.flags = r3     // Catch:{ Exception -> 0x05c0, all -> 0x0cfd }
            org.telegram.messenger.video.MP4Builder r3 = r14.mediaMuxer     // Catch:{ Exception -> 0x05c0, all -> 0x0cfd }
            r22 = r2
            r4 = r61
            long r2 = r3.writeSampleData(r4, r7, r5, r1)     // Catch:{ Exception -> 0x05c0, all -> 0x0cfd }
            r33 = 0
            int r1 = (r2 > r33 ? 1 : (r2 == r33 ? 0 : -1))
            if (r1 == 0) goto L_0x05ae
            org.telegram.messenger.MediaController$VideoConvertorListener r1 = r14.callback     // Catch:{ Exception -> 0x05c0, all -> 0x0cfd }
            if (r1 == 0) goto L_0x05ae
            r63 = r7
            r61 = r8
            long r7 = r5.presentationTimeUs     // Catch:{ Exception -> 0x05aa, all -> 0x0cfd }
            r11 = r83
            long r7 = r7 - r11
            int r1 = (r7 > r58 ? 1 : (r7 == r58 ? 0 : -1))
            if (r1 <= 0) goto L_0x0597
            long r7 = r5.presentationTimeUs     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            long r58 = r7 - r11
        L_0x0597:
            r7 = r58
            org.telegram.messenger.MediaController$VideoConvertorListener r1 = r14.callback     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            r64 = r4
            float r4 = (float) r7     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            r20 = 1148846080(0x447a0000, float:1000.0)
            float r4 = r4 / r20
            float r4 = r4 / r19
            r1.didWriteData(r2, r4)     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            r58 = r7
            goto L_0x05e4
        L_0x05aa:
            r0 = move-exception
            r11 = r83
            goto L_0x0600
        L_0x05ae:
            r11 = r83
            r64 = r4
            r63 = r7
        L_0x05b4:
            r61 = r8
            goto L_0x05e4
        L_0x05b7:
            r11 = r83
            r22 = r2
            r63 = r7
            r64 = r61
            goto L_0x05b4
        L_0x05c0:
            r0 = move-exception
            r11 = r83
            r61 = r8
            goto L_0x0600
        L_0x05c6:
            r11 = r83
            r63 = r7
            r5 = r22
            r64 = r61
            r22 = r2
            r61 = r8
            goto L_0x05e0
        L_0x05d3:
            r11 = r83
            r63 = r7
            r5 = r22
            r64 = r61
            r22 = r2
            r61 = r8
            r1 = -1
        L_0x05e0:
            if (r3 != r1) goto L_0x05e4
            r1 = 1
            goto L_0x05e5
        L_0x05e4:
            r1 = 0
        L_0x05e5:
            if (r1 == 0) goto L_0x0622
            r1 = 2500(0x9c4, double:1.235E-320)
            int r43 = r10.dequeueInputBuffer(r1)     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            if (r43 < 0) goto L_0x0622
            r44 = 0
            r45 = 0
            r46 = 0
            r48 = 4
            r42 = r10
            r42.queueInputBuffer(r43, r44, r45, r46, r48)     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            r54 = 1
            goto L_0x0622
        L_0x05ff:
            r0 = move-exception
        L_0x0600:
            r2 = r0
            r1 = r6
        L_0x0602:
            r67 = r10
            r8 = r62
            goto L_0x0322
        L_0x0608:
            r0 = move-exception
            r11 = r83
            r61 = r8
            r2 = r0
            r8 = r1
            r1 = r6
            r67 = r10
            goto L_0x0322
        L_0x0614:
            r11 = r83
            r62 = r1
            r64 = r5
            r63 = r7
            r61 = r8
            r5 = r22
            r22 = r2
        L_0x0622:
            r1 = r55 ^ 1
            r8 = r1
            r2 = r60
            r1 = 1
        L_0x0628:
            if (r8 != 0) goto L_0x0644
            if (r1 == 0) goto L_0x062d
            goto L_0x0644
        L_0x062d:
            r60 = r2
            r3 = r11
            r42 = r13
            r2 = r22
            r8 = r61
            r1 = r62
            r7 = r63
            r13 = r79
            r11 = r85
            r22 = r5
            r5 = r64
            goto L_0x046c
        L_0x0644:
            r74.checkConversionCanceled()     // Catch:{ Exception -> 0x0b7a, all -> 0x0cfd }
            if (r90 == 0) goto L_0x064c
            r3 = 22000(0x55f0, double:1.08694E-319)
            goto L_0x064e
        L_0x064c:
            r3 = 2500(0x9c4, double:1.235E-320)
        L_0x064e:
            int r3 = r6.dequeueOutputBuffer(r5, r3)     // Catch:{ Exception -> 0x0b7a, all -> 0x0cfd }
            r4 = -1
            if (r3 != r4) goto L_0x0672
            r28 = r2
            r44 = r8
            r7 = r51
            r59 = r58
            r8 = 3
            r20 = 1148846080(0x447a0000, float:1000.0)
            r21 = 0
        L_0x0662:
            r41 = 2
            r51 = r39
            r58 = r57
            r57 = r56
            r56 = r53
            r53 = r9
            r9 = r79
            goto L_0x0856
        L_0x0672:
            r4 = -3
            if (r3 != r4) goto L_0x068e
            int r4 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            r7 = 21
            if (r4 >= r7) goto L_0x067f
            java.nio.ByteBuffer[] r39 = r6.getOutputBuffers()     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
        L_0x067f:
            r21 = r1
            r28 = r2
            r44 = r8
        L_0x0685:
            r7 = r51
            r59 = r58
            r4 = -1
            r8 = 3
            r20 = 1148846080(0x447a0000, float:1000.0)
            goto L_0x0662
        L_0x068e:
            r4 = -2
            if (r3 != r4) goto L_0x06d5
            android.media.MediaFormat r4 = r6.getOutputFormat()     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            r7 = -5
            if (r2 != r7) goto L_0x06cc
            if (r4 == 0) goto L_0x06cc
            org.telegram.messenger.video.MP4Builder r2 = r14.mediaMuxer     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            r7 = 0
            int r2 = r2.addTrack(r4, r7)     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            java.lang.String r7 = "prepend-sps-pps-to-idr-frames"
            boolean r7 = r4.containsKey(r7)     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            if (r7 == 0) goto L_0x06cc
            java.lang.String r7 = "prepend-sps-pps-to-idr-frames"
            int r7 = r4.getInteger(r7)     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            r42 = r1
            r1 = 1
            if (r7 != r1) goto L_0x06ce
            java.lang.String r1 = "csd-0"
            java.nio.ByteBuffer r1 = r4.getByteBuffer(r1)     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            java.lang.String r7 = "csd-1"
            java.nio.ByteBuffer r4 = r4.getByteBuffer(r7)     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            int r1 = r1.limit()     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            int r4 = r4.limit()     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            int r1 = r1 + r4
            r56 = r1
            goto L_0x06ce
        L_0x06cc:
            r42 = r1
        L_0x06ce:
            r28 = r2
            r44 = r8
            r21 = r42
            goto L_0x0685
        L_0x06d5:
            r42 = r1
            if (r3 < 0) goto L_0x0b4e
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b7a, all -> 0x0cfd }
            r7 = 21
            if (r1 >= r7) goto L_0x06e2
            r1 = r39[r3]     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            goto L_0x06e6
        L_0x06e2:
            java.nio.ByteBuffer r1 = r6.getOutputBuffer(r3)     // Catch:{ Exception -> 0x0b7a, all -> 0x0cfd }
        L_0x06e6:
            if (r1 == 0) goto L_0x0b25
            int r4 = r5.size     // Catch:{ Exception -> 0x0b7a, all -> 0x0cfd }
            r7 = 1
            if (r4 <= r7) goto L_0x0829
            int r4 = r5.flags     // Catch:{ Exception -> 0x0824, all -> 0x0cfd }
            r41 = 2
            r4 = r4 & 2
            if (r4 != 0) goto L_0x07a3
            if (r56 == 0) goto L_0x0708
            int r4 = r5.flags     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            r4 = r4 & r7
            if (r4 == 0) goto L_0x0708
            int r4 = r5.offset     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            int r4 = r4 + r56
            r5.offset = r4     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            int r4 = r5.size     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            int r4 = r4 - r56
            r5.size = r4     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
        L_0x0708:
            if (r57 == 0) goto L_0x0764
            int r4 = r5.flags     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            r7 = 1
            r4 = r4 & r7
            if (r4 == 0) goto L_0x0764
            int r4 = r5.size     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            r7 = 100
            if (r4 <= r7) goto L_0x075f
            int r4 = r5.offset     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            r1.position(r4)     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            r4 = 100
            byte[] r4 = new byte[r4]     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            r1.get(r4)     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            r7 = 0
            r43 = 0
        L_0x0725:
            r44 = r8
            r8 = 96
            if (r7 >= r8) goto L_0x0761
            byte r8 = r4[r7]     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            if (r8 != 0) goto L_0x0756
            int r8 = r7 + 1
            byte r8 = r4[r8]     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            if (r8 != 0) goto L_0x0756
            int r8 = r7 + 2
            byte r8 = r4[r8]     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            if (r8 != 0) goto L_0x0756
            int r8 = r7 + 3
            byte r8 = r4[r8]     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            r45 = r4
            r4 = 1
            if (r8 != r4) goto L_0x0758
            int r8 = r43 + 1
            if (r8 <= r4) goto L_0x0753
            int r4 = r5.offset     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            int r4 = r4 + r7
            r5.offset = r4     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            int r4 = r5.size     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            int r4 = r4 - r7
            r5.size = r4     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            goto L_0x0761
        L_0x0753:
            r43 = r8
            goto L_0x0758
        L_0x0756:
            r45 = r4
        L_0x0758:
            int r7 = r7 + 1
            r8 = r44
            r4 = r45
            goto L_0x0725
        L_0x075f:
            r44 = r8
        L_0x0761:
            r57 = 0
            goto L_0x0766
        L_0x0764:
            r44 = r8
        L_0x0766:
            org.telegram.messenger.video.MP4Builder r4 = r14.mediaMuxer     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            r53 = r9
            r7 = 1
            long r8 = r4.writeSampleData(r2, r1, r5, r7)     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            r33 = 0
            int r1 = (r8 > r33 ? 1 : (r8 == r33 ? 0 : -1))
            if (r1 == 0) goto L_0x0798
            org.telegram.messenger.MediaController$VideoConvertorListener r1 = r14.callback     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            if (r1 == 0) goto L_0x0798
            r7 = r3
            long r3 = r5.presentationTimeUs     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            long r3 = r3 - r11
            int r1 = (r3 > r58 ? 1 : (r3 == r58 ? 0 : -1))
            if (r1 <= 0) goto L_0x0785
            long r3 = r5.presentationTimeUs     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            long r58 = r3 - r11
        L_0x0785:
            r3 = r58
            org.telegram.messenger.MediaController$VideoConvertorListener r1 = r14.callback     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            r43 = r7
            float r7 = (float) r3     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            r20 = 1148846080(0x447a0000, float:1000.0)
            float r7 = r7 / r20
            float r7 = r7 / r19
            r1.didWriteData(r8, r7)     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            r58 = r3
            goto L_0x079c
        L_0x0798:
            r43 = r3
            r20 = 1148846080(0x447a0000, float:1000.0)
        L_0x079c:
            r9 = r79
            r7 = r51
            r8 = 3
            goto L_0x0838
        L_0x07a3:
            r43 = r3
            r44 = r8
            r53 = r9
            r7 = -5
            r20 = 1148846080(0x447a0000, float:1000.0)
            if (r2 != r7) goto L_0x079c
            int r2 = r5.size     // Catch:{ Exception -> 0x0824, all -> 0x0cfd }
            byte[] r2 = new byte[r2]     // Catch:{ Exception -> 0x0824, all -> 0x0cfd }
            int r3 = r5.offset     // Catch:{ Exception -> 0x0824, all -> 0x0cfd }
            int r4 = r5.size     // Catch:{ Exception -> 0x0824, all -> 0x0cfd }
            int r3 = r3 + r4
            r1.limit(r3)     // Catch:{ Exception -> 0x0824, all -> 0x0cfd }
            int r3 = r5.offset     // Catch:{ Exception -> 0x0824, all -> 0x0cfd }
            r1.position(r3)     // Catch:{ Exception -> 0x0824, all -> 0x0cfd }
            r1.get(r2)     // Catch:{ Exception -> 0x0824, all -> 0x0cfd }
            int r1 = r5.size     // Catch:{ Exception -> 0x0824, all -> 0x0cfd }
            r3 = 1
            int r1 = r1 - r3
        L_0x07c6:
            r8 = 3
            if (r1 < 0) goto L_0x0804
            if (r1 <= r8) goto L_0x0804
            byte r4 = r2[r1]     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            if (r4 != r3) goto L_0x07ff
            int r3 = r1 + -1
            byte r3 = r2[r3]     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            if (r3 != 0) goto L_0x07ff
            int r3 = r1 + -2
            byte r3 = r2[r3]     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            if (r3 != 0) goto L_0x07ff
            int r3 = r1 + -3
            byte r4 = r2[r3]     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            if (r4 != 0) goto L_0x07ff
            java.nio.ByteBuffer r1 = java.nio.ByteBuffer.allocate(r3)     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            int r4 = r5.size     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            int r4 = r4 - r3
            java.nio.ByteBuffer r4 = java.nio.ByteBuffer.allocate(r4)     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            r9 = 0
            java.nio.ByteBuffer r7 = r1.put(r2, r9, r3)     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            r7.position(r9)     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            int r7 = r5.size     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            int r7 = r7 - r3
            java.nio.ByteBuffer r2 = r4.put(r2, r3, r7)     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            r2.position(r9)     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            goto L_0x0806
        L_0x07ff:
            int r1 = r1 + -1
            r3 = 1
            r7 = -5
            goto L_0x07c6
        L_0x0804:
            r1 = 0
            r4 = 0
        L_0x0806:
            r9 = r79
            r7 = r51
            android.media.MediaFormat r2 = android.media.MediaFormat.createVideoFormat(r7, r9, r15)     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            if (r1 == 0) goto L_0x081c
            if (r4 == 0) goto L_0x081c
            java.lang.String r3 = "csd-0"
            r2.setByteBuffer(r3, r1)     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            java.lang.String r1 = "csd-1"
            r2.setByteBuffer(r1, r4)     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
        L_0x081c:
            org.telegram.messenger.video.MP4Builder r1 = r14.mediaMuxer     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            r3 = 0
            int r2 = r1.addTrack(r2, r3)     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            goto L_0x0838
        L_0x0824:
            r0 = move-exception
            r9 = r79
            goto L_0x0600
        L_0x0829:
            r43 = r3
            r44 = r8
            r53 = r9
            r7 = r51
            r8 = 3
            r20 = 1148846080(0x447a0000, float:1000.0)
            r41 = 2
            r9 = r79
        L_0x0838:
            int r1 = r5.flags     // Catch:{ Exception -> 0x0b7a, all -> 0x0cfd }
            r1 = r1 & 4
            r3 = r43
            if (r1 == 0) goto L_0x0842
            r1 = 1
            goto L_0x0843
        L_0x0842:
            r1 = 0
        L_0x0843:
            r4 = 0
            r6.releaseOutputBuffer(r3, r4)     // Catch:{ Exception -> 0x0b7a, all -> 0x0cfd }
            r28 = r2
            r51 = r39
            r21 = r42
            r59 = r58
            r4 = -1
            r58 = r57
            r57 = r56
            r56 = r1
        L_0x0856:
            if (r3 == r4) goto L_0x086e
            r1 = r21
            r2 = r28
            r8 = r44
            r39 = r51
            r9 = r53
            r53 = r56
            r56 = r57
            r57 = r58
            r58 = r59
            r51 = r7
            goto L_0x0628
        L_0x086e:
            if (r55 != 0) goto L_0x0ae9
            r1 = 2500(0x9c4, double:1.235E-320)
            int r3 = r10.dequeueOutputBuffer(r5, r1)     // Catch:{ Exception -> 0x0b7a, all -> 0x0cfd }
            if (r3 != r4) goto L_0x089f
            r2 = r1
            r9 = r5
            r1 = r6
            r73 = r7
            r67 = r10
            r69 = r26
            r8 = r62
            r70 = r63
            r12 = r64
            r11 = 18
            r16 = -1
            r44 = 0
        L_0x088d:
            r62 = 1
            r64 = 0
            r66 = 0
            r68 = 3
            r71 = -5
            r72 = 21
            r10 = r81
            r63 = r36
            goto L_0x0aff
        L_0x089f:
            r1 = -3
            if (r3 != r1) goto L_0x08a4
            goto L_0x0ae9
        L_0x08a4:
            r1 = -2
            if (r3 != r1) goto L_0x08c5
            android.media.MediaFormat r1 = r10.getOutputFormat()     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            if (r2 == 0) goto L_0x0ae9
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            r2.<init>()     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            java.lang.String r3 = "newFormat = "
            r2.append(r3)     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            r2.append(r1)     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            java.lang.String r1 = r2.toString()     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            goto L_0x0ae9
        L_0x08c5:
            if (r3 < 0) goto L_0x0ac5
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b7a, all -> 0x0cfd }
            r2 = 18
            if (r1 < r2) goto L_0x08d9
            int r1 = r5.size     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            if (r1 == 0) goto L_0x08d3
            r1 = 1
            goto L_0x08d4
        L_0x08d3:
            r1 = 0
        L_0x08d4:
            r8 = 2500(0x9c4, double:1.235E-320)
            r33 = 0
            goto L_0x08ed
        L_0x08d9:
            int r1 = r5.size     // Catch:{ Exception -> 0x0b7a, all -> 0x0cfd }
            if (r1 != 0) goto L_0x08e8
            long r1 = r5.presentationTimeUs     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            r33 = 0
            int r39 = (r1 > r33 ? 1 : (r1 == r33 ? 0 : -1))
            if (r39 == 0) goto L_0x08e6
            goto L_0x08ea
        L_0x08e6:
            r1 = 0
            goto L_0x08eb
        L_0x08e8:
            r33 = 0
        L_0x08ea:
            r1 = 1
        L_0x08eb:
            r8 = 2500(0x9c4, double:1.235E-320)
        L_0x08ed:
            int r2 = (r85 > r33 ? 1 : (r85 == r33 ? 0 : -1))
            if (r2 <= 0) goto L_0x0905
            long r8 = r5.presentationTimeUs     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            int r2 = (r8 > r85 ? 1 : (r8 == r85 ? 0 : -1))
            if (r2 < 0) goto L_0x0905
            int r1 = r5.flags     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            r1 = r1 | 4
            r5.flags = r1     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            r9 = 0
            r33 = 0
            r54 = 1
            r55 = 1
            goto L_0x0908
        L_0x0905:
            r9 = r1
            r33 = 0
        L_0x0908:
            int r1 = (r11 > r33 ? 1 : (r11 == r33 ? 0 : -1))
            if (r1 <= 0) goto L_0x0940
            r1 = -1
            int r8 = (r30 > r1 ? 1 : (r30 == r1 ? 0 : -1))
            if (r8 != 0) goto L_0x0940
            long r1 = r5.presentationTimeUs     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            int r8 = (r1 > r11 ? 1 : (r1 == r11 ? 0 : -1))
            if (r8 >= 0) goto L_0x093c
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            if (r1 == 0) goto L_0x093a
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            r1.<init>()     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            java.lang.String r2 = "drop frame startTime = "
            r1.append(r2)     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            r1.append(r11)     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            java.lang.String r2 = " present time = "
            r1.append(r2)     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            long r8 = r5.presentationTimeUs     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            r1.append(r8)     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
        L_0x093a:
            r9 = 0
            goto L_0x0940
        L_0x093c:
            long r1 = r5.presentationTimeUs     // Catch:{ Exception -> 0x05ff, all -> 0x0cfd }
            r30 = r1
        L_0x0940:
            r10.releaseOutputBuffer(r3, r9)     // Catch:{ Exception -> 0x0b7a, all -> 0x0cfd }
            if (r9 == 0) goto L_0x0a58
            r26.awaitNewImage()     // Catch:{ Exception -> 0x094a, all -> 0x0cfd }
            r9 = 0
            goto L_0x0950
        L_0x094a:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)     // Catch:{ Exception -> 0x0a42, all -> 0x0cfd }
            r9 = 1
        L_0x0950:
            if (r9 != 0) goto L_0x0a58
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0a42, all -> 0x0cfd }
            r8 = 18
            if (r1 < r8) goto L_0x099e
            r9 = r26
            r3 = 0
            r9.drawImage(r3)     // Catch:{ Exception -> 0x0995, all -> 0x0cfd }
            long r1 = r5.presentationTimeUs     // Catch:{ Exception -> 0x0995, all -> 0x0cfd }
            r42 = 1000(0x3e8, double:4.94E-321)
            long r1 = r1 * r42
            r8 = r49
            r8.setPresentationTime(r1)     // Catch:{ Exception -> 0x098f, all -> 0x0cfd }
            r8.swapBuffers()     // Catch:{ Exception -> 0x098f, all -> 0x0cfd }
            r32 = r6
            r73 = r7
            r49 = r8
            r69 = r9
            r67 = r10
            r8 = r62
            r70 = r63
            r12 = r64
            r11 = 18
            r16 = -1
            r62 = 1
            r66 = 0
            r68 = 3
            r71 = -5
            r72 = 21
            r10 = r81
            r9 = r5
            goto L_0x0a77
        L_0x098f:
            r0 = move-exception
            r2 = r0
            r1 = r6
            r49 = r8
            goto L_0x099a
        L_0x0995:
            r0 = move-exception
            r8 = r49
            r2 = r0
            r1 = r6
        L_0x099a:
            r26 = r9
            goto L_0x0602
        L_0x099e:
            r9 = r26
            r8 = r49
            r1 = 2500(0x9c4, double:1.235E-320)
            r3 = 0
            int r26 = r6.dequeueInputBuffer(r1)     // Catch:{ Exception -> 0x0a30, all -> 0x0cfd }
            if (r26 < 0) goto L_0x0a01
            r2 = 1
            r9.drawImage(r2)     // Catch:{ Exception -> 0x0a30, all -> 0x0cfd }
            java.nio.ByteBuffer r1 = r9.getFrame()     // Catch:{ Exception -> 0x0a30, all -> 0x0cfd }
            r16 = r50[r26]     // Catch:{ Exception -> 0x0a30, all -> 0x0cfd }
            r16.clear()     // Catch:{ Exception -> 0x0a30, all -> 0x0cfd }
            r49 = r8
            r8 = r62
            r62 = 1
            r2 = r16
            r11 = 0
            r3 = r36
            r12 = r64
            r16 = -1
            r4 = r79
            r11 = r9
            r64 = r33
            r66 = 0
            r9 = r5
            r5 = r80
            r32 = r6
            r67 = r10
            r69 = r11
            r11 = 18
            r68 = 3
            r10 = r81
            r6 = r40
            r73 = r7
            r70 = r63
            r71 = -5
            r72 = 21
            r63 = r36
            r7 = r29
            org.telegram.messenger.Utilities.convertVideoFrame(r1, r2, r3, r4, r5, r6, r7)     // Catch:{ Exception -> 0x0a8b, all -> 0x0cfd }
            r35 = 0
            long r1 = r9.presentationTimeUs     // Catch:{ Exception -> 0x0a8b, all -> 0x0cfd }
            r39 = 0
            r33 = r32
            r34 = r26
            r36 = r27
            r37 = r1
            r33.queueInputBuffer(r34, r35, r36, r37, r39)     // Catch:{ Exception -> 0x0a8b, all -> 0x0cfd }
            goto L_0x0a7b
        L_0x0a01:
            r32 = r6
            r73 = r7
            r49 = r8
            r69 = r9
            r67 = r10
            r8 = r62
            r70 = r63
            r12 = r64
            r11 = 18
            r16 = -1
            r62 = 1
            r66 = 0
            r68 = 3
            r71 = -5
            r72 = 21
            r10 = r81
            r9 = r5
            r64 = r33
            r63 = r36
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0a8b, all -> 0x0cfd }
            if (r1 == 0) goto L_0x0a7b
            java.lang.String r1 = "input buffer not available"
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x0a8b, all -> 0x0cfd }
            goto L_0x0a7b
        L_0x0a30:
            r0 = move-exception
            r32 = r6
            r49 = r8
            r69 = r9
            r67 = r10
            r8 = r62
            r62 = 1
            r66 = 0
            r10 = r81
            goto L_0x0a8c
        L_0x0a42:
            r0 = move-exception
            r32 = r6
            r67 = r10
            r69 = r26
            r8 = r62
            r62 = 1
            r66 = 0
            r10 = r81
            r6 = r82
            r2 = r0
            r1 = r32
            goto L_0x0c1f
        L_0x0a58:
            r9 = r5
            r32 = r6
            r73 = r7
            r67 = r10
            r69 = r26
            r8 = r62
            r70 = r63
            r12 = r64
            r11 = 18
            r16 = -1
            r62 = 1
            r66 = 0
            r68 = 3
            r71 = -5
            r72 = 21
            r10 = r81
        L_0x0a77:
            r64 = r33
            r63 = r36
        L_0x0a7b:
            int r1 = r9.flags     // Catch:{ Exception -> 0x0ac0, all -> 0x0cfd }
            r1 = r1 & 4
            if (r1 == 0) goto L_0x0abb
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0ac0, all -> 0x0cfd }
            if (r1 == 0) goto L_0x0a93
            java.lang.String r1 = "decoder stream end"
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x0a8b, all -> 0x0cfd }
            goto L_0x0a93
        L_0x0a8b:
            r0 = move-exception
        L_0x0a8c:
            r6 = r82
            r2 = r0
            r1 = r32
            goto L_0x0b76
        L_0x0a93:
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0ac0, all -> 0x0cfd }
            if (r1 < r11) goto L_0x0a9f
            r32.signalEndOfInputStream()     // Catch:{ Exception -> 0x0a8b, all -> 0x0cfd }
            r1 = r32
            r2 = 2500(0x9c4, double:1.235E-320)
            goto L_0x0ab8
        L_0x0a9f:
            r1 = r32
            r2 = 2500(0x9c4, double:1.235E-320)
            int r43 = r1.dequeueInputBuffer(r2)     // Catch:{ Exception -> 0x0b72, all -> 0x0cfd }
            if (r43 < 0) goto L_0x0ab8
            r44 = 0
            r45 = 1
            long r4 = r9.presentationTimeUs     // Catch:{ Exception -> 0x0b72, all -> 0x0cfd }
            r48 = 4
            r42 = r1
            r46 = r4
            r42.queueInputBuffer(r43, r44, r45, r46, r48)     // Catch:{ Exception -> 0x0b72, all -> 0x0cfd }
        L_0x0ab8:
            r44 = 0
            goto L_0x0aff
        L_0x0abb:
            r1 = r32
            r2 = 2500(0x9c4, double:1.235E-320)
            goto L_0x0aff
        L_0x0ac0:
            r0 = move-exception
            r1 = r32
            goto L_0x0b73
        L_0x0ac5:
            r1 = r6
            r67 = r10
            r69 = r26
            r8 = r62
            r62 = 1
            r66 = 0
            r10 = r81
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x0b72, all -> 0x0cfd }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b72, all -> 0x0cfd }
            r4.<init>()     // Catch:{ Exception -> 0x0b72, all -> 0x0cfd }
            java.lang.String r5 = "unexpected result from decoder.dequeueOutputBuffer: "
            r4.append(r5)     // Catch:{ Exception -> 0x0b72, all -> 0x0cfd }
            r4.append(r3)     // Catch:{ Exception -> 0x0b72, all -> 0x0cfd }
            java.lang.String r3 = r4.toString()     // Catch:{ Exception -> 0x0b72, all -> 0x0cfd }
            r2.<init>(r3)     // Catch:{ Exception -> 0x0b72, all -> 0x0cfd }
            throw r2     // Catch:{ Exception -> 0x0b72, all -> 0x0cfd }
        L_0x0ae9:
            r9 = r5
            r1 = r6
            r73 = r7
            r67 = r10
            r69 = r26
            r8 = r62
            r70 = r63
            r12 = r64
            r2 = 2500(0x9c4, double:1.235E-320)
            r11 = 18
            r16 = -1
            goto L_0x088d
        L_0x0aff:
            r6 = r1
            r62 = r8
            r5 = r9
            r64 = r12
            r1 = r21
            r2 = r28
            r8 = r44
            r39 = r51
            r9 = r53
            r53 = r56
            r56 = r57
            r57 = r58
            r58 = r59
            r36 = r63
            r10 = r67
            r26 = r69
            r63 = r70
            r51 = r73
            r11 = r83
            goto L_0x0628
        L_0x0b25:
            r1 = r6
            r67 = r10
            r69 = r26
            r8 = r62
            r62 = 1
            r66 = 0
            r10 = r81
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x0b72, all -> 0x0cfd }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b72, all -> 0x0cfd }
            r4.<init>()     // Catch:{ Exception -> 0x0b72, all -> 0x0cfd }
            java.lang.String r5 = "encoderOutputBuffer "
            r4.append(r5)     // Catch:{ Exception -> 0x0b72, all -> 0x0cfd }
            r4.append(r3)     // Catch:{ Exception -> 0x0b72, all -> 0x0cfd }
            java.lang.String r3 = " was null"
            r4.append(r3)     // Catch:{ Exception -> 0x0b72, all -> 0x0cfd }
            java.lang.String r3 = r4.toString()     // Catch:{ Exception -> 0x0b72, all -> 0x0cfd }
            r2.<init>(r3)     // Catch:{ Exception -> 0x0b72, all -> 0x0cfd }
            throw r2     // Catch:{ Exception -> 0x0b72, all -> 0x0cfd }
        L_0x0b4e:
            r1 = r6
            r67 = r10
            r69 = r26
            r8 = r62
            r62 = 1
            r66 = 0
            r10 = r81
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x0b72, all -> 0x0cfd }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b72, all -> 0x0cfd }
            r4.<init>()     // Catch:{ Exception -> 0x0b72, all -> 0x0cfd }
            java.lang.String r5 = "unexpected result from encoder.dequeueOutputBuffer: "
            r4.append(r5)     // Catch:{ Exception -> 0x0b72, all -> 0x0cfd }
            r4.append(r3)     // Catch:{ Exception -> 0x0b72, all -> 0x0cfd }
            java.lang.String r3 = r4.toString()     // Catch:{ Exception -> 0x0b72, all -> 0x0cfd }
            r2.<init>(r3)     // Catch:{ Exception -> 0x0b72, all -> 0x0cfd }
            throw r2     // Catch:{ Exception -> 0x0b72, all -> 0x0cfd }
        L_0x0b72:
            r0 = move-exception
        L_0x0b73:
            r6 = r82
            r2 = r0
        L_0x0b76:
            r26 = r69
            goto L_0x0c1f
        L_0x0b7a:
            r0 = move-exception
            r1 = r6
            r67 = r10
            r69 = r26
            r8 = r62
            r62 = 1
            r66 = 0
            r10 = r81
            goto L_0x0b98
        L_0x0b89:
            r0 = move-exception
            r61 = r8
            r67 = r10
            r69 = r26
            r62 = 1
            r66 = 0
            r10 = r81
            r8 = r1
            r1 = r6
        L_0x0b98:
            r6 = r82
            r2 = r0
            goto L_0x0c1f
        L_0x0b9d:
            r0 = move-exception
            r8 = r1
            r1 = r6
            r67 = r10
            r69 = r26
            r62 = 1
            r66 = 0
            r10 = r81
            r6 = r82
            r2 = r0
            goto L_0x0bd5
        L_0x0bae:
            r0 = move-exception
            r8 = r1
            r69 = r5
            r1 = r6
            r49 = r9
            r67 = r10
            r62 = 1
            r66 = 0
            r10 = r81
            r6 = r82
            r2 = r0
            r26 = r69
            goto L_0x0bd5
        L_0x0bc3:
            r0 = move-exception
            r8 = r1
            r1 = r6
            r49 = r9
            r67 = r10
            r62 = 1
            r66 = 0
            r10 = r81
            r6 = r82
            r2 = r0
            r26 = 0
        L_0x0bd5:
            r61 = 0
            goto L_0x0c1f
        L_0x0bd8:
            r0 = move-exception
            r10 = r81
            r8 = r1
            r1 = r6
            r49 = r9
            r62 = 1
            r66 = 0
            r6 = r82
            r2 = r0
            r26 = 0
            goto L_0x0c1b
        L_0x0be9:
            r0 = move-exception
            r10 = r81
            r8 = r1
            r1 = r6
            r62 = 1
            r66 = 0
            r6 = r82
            r2 = r0
            goto L_0x0CLASSNAME
        L_0x0bf6:
            r0 = move-exception
            r10 = r81
            r8 = r1
            r82 = r9
            goto L_0x0c0f
        L_0x0bfd:
            r0 = move-exception
            r8 = r1
            r10 = r6
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r0 = move-exception
            r10 = r81
            r8 = r1
        L_0x0CLASSNAME:
            r6 = r9
            r62 = 1
            r66 = 0
            goto L_0x0CLASSNAME
        L_0x0c0b:
            r0 = move-exception
            r10 = r81
            r8 = r1
        L_0x0c0f:
            r62 = 1
            r66 = 0
            r6 = r82
        L_0x0CLASSNAME:
            r2 = r0
            r1 = 0
        L_0x0CLASSNAME:
            r26 = 0
            r49 = 0
        L_0x0c1b:
            r61 = 0
        L_0x0c1d:
            r67 = 0
        L_0x0c1f:
            boolean r3 = r2 instanceof java.lang.IllegalStateException     // Catch:{ Exception -> 0x0cad, all -> 0x0cfd }
            if (r3 == 0) goto L_0x0CLASSNAME
            if (r90 != 0) goto L_0x0CLASSNAME
            r9 = 1
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r9 = 0
        L_0x0CLASSNAME:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0c9f, all -> 0x0cfd }
            r3.<init>()     // Catch:{ Exception -> 0x0c9f, all -> 0x0cfd }
            r4 = r17
            r3.append(r4)     // Catch:{ Exception -> 0x0c9b, all -> 0x0cfd }
            r3.append(r6)     // Catch:{ Exception -> 0x0c9b, all -> 0x0cfd }
            r5 = r18
            r3.append(r5)     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0cfd }
            r3.append(r10)     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0cfd }
            r7 = r23
            r3.append(r7)     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0cfd }
            r3.append(r15)     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0cfd }
            r11 = r24
            r3.append(r11)     // Catch:{ Exception -> 0x0c8f, all -> 0x0cfd }
            r12 = r79
            r3.append(r12)     // Catch:{ Exception -> 0x0c8d, all -> 0x0cfd }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x0c8d, all -> 0x0cfd }
            org.telegram.messenger.FileLog.e((java.lang.String) r3)     // Catch:{ Exception -> 0x0c8d, all -> 0x0cfd }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)     // Catch:{ Exception -> 0x0c8d, all -> 0x0cfd }
            r35 = r9
            r9 = r49
            r2 = 1
        L_0x0c5e:
            android.media.MediaExtractor r3 = r14.extractor     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0cfd }
            r3.unselectTrack(r8)     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0cfd }
            if (r26 == 0) goto L_0x0CLASSNAME
            r26.release()     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0cfd }
        L_0x0CLASSNAME:
            if (r9 == 0) goto L_0x0c6d
            r9.release()     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0cfd }
        L_0x0c6d:
            if (r67 == 0) goto L_0x0CLASSNAME
            r67.stop()     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0cfd }
            r67.release()     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0cfd }
        L_0x0CLASSNAME:
            if (r1 == 0) goto L_0x0c7d
            r1.stop()     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0cfd }
            r1.release()     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0cfd }
        L_0x0c7d:
            if (r61 == 0) goto L_0x0CLASSNAME
            r61.release()     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0cfd }
        L_0x0CLASSNAME:
            r74.checkConversionCanceled()     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0cfd }
            r9 = r2
            goto L_0x0cc3
        L_0x0CLASSNAME:
            r0 = move-exception
            r1 = r0
            r9 = r35
            goto L_0x0d12
        L_0x0c8d:
            r0 = move-exception
            goto L_0x0caa
        L_0x0c8f:
            r0 = move-exception
            r12 = r79
            goto L_0x0caa
        L_0x0CLASSNAME:
            r0 = move-exception
            r12 = r79
            goto L_0x0ca8
        L_0x0CLASSNAME:
            r0 = move-exception
            r12 = r79
            goto L_0x0ca6
        L_0x0c9b:
            r0 = move-exception
            r12 = r79
            goto L_0x0ca4
        L_0x0c9f:
            r0 = move-exception
            r12 = r79
            r4 = r17
        L_0x0ca4:
            r5 = r18
        L_0x0ca6:
            r7 = r23
        L_0x0ca8:
            r11 = r24
        L_0x0caa:
            r1 = r0
            goto L_0x0d12
        L_0x0cad:
            r0 = move-exception
            r12 = r79
            r4 = r17
            r5 = r18
            r7 = r23
            r11 = r24
            goto L_0x0d10
        L_0x0cba:
            r10 = r81
            r12 = r13
            r66 = 0
            r6 = r82
            goto L_0x00ca
        L_0x0cc3:
            android.media.MediaExtractor r1 = r14.extractor
            if (r1 == 0) goto L_0x0cca
            r1.release()
        L_0x0cca:
            org.telegram.messenger.video.MP4Builder r1 = r14.mediaMuxer
            if (r1 == 0) goto L_0x0cd7
            r1.finishMovie()     // Catch:{ Exception -> 0x0cd2 }
            goto L_0x0cd7
        L_0x0cd2:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x0cd7:
            r11 = r6
            goto L_0x0d51
        L_0x0cda:
            r0 = move-exception
            r12 = r79
            r15 = r80
            r10 = r81
            r5 = r2
            r7 = r3
            r11 = r4
            r4 = r18
            r62 = 1
            r66 = 0
            goto L_0x0d0e
        L_0x0ceb:
            r0 = move-exception
            r12 = r79
            goto L_0x0cf0
        L_0x0cef:
            r0 = move-exception
        L_0x0cf0:
            r10 = r81
            r5 = r2
            r7 = r3
            r15 = r11
            r62 = 1
            r66 = 0
            r11 = r4
            r4 = r18
            goto L_0x0d0e
        L_0x0cfd:
            r0 = move-exception
            r2 = r0
            r1 = r14
            goto L_0x0d78
        L_0x0d02:
            r0 = move-exception
            r10 = r81
            r5 = r2
            r7 = r3
            r15 = r11
            r62 = 1
            r66 = 0
            r11 = r4
            r4 = r1
        L_0x0d0e:
            r6 = r82
        L_0x0d10:
            r1 = r0
            r9 = 0
        L_0x0d12:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x0d74 }
            r2.<init>()     // Catch:{ all -> 0x0d74 }
            r2.append(r4)     // Catch:{ all -> 0x0d74 }
            r2.append(r6)     // Catch:{ all -> 0x0d74 }
            r2.append(r5)     // Catch:{ all -> 0x0d74 }
            r2.append(r10)     // Catch:{ all -> 0x0d74 }
            r2.append(r7)     // Catch:{ all -> 0x0d74 }
            r2.append(r15)     // Catch:{ all -> 0x0d74 }
            r2.append(r11)     // Catch:{ all -> 0x0d74 }
            r2.append(r12)     // Catch:{ all -> 0x0d74 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x0d74 }
            org.telegram.messenger.FileLog.e((java.lang.String) r2)     // Catch:{ all -> 0x0d74 }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)     // Catch:{ all -> 0x0d74 }
            android.media.MediaExtractor r1 = r14.extractor
            if (r1 == 0) goto L_0x0d40
            r1.release()
        L_0x0d40:
            org.telegram.messenger.video.MP4Builder r1 = r14.mediaMuxer
            if (r1 == 0) goto L_0x0d4d
            r1.finishMovie()     // Catch:{ Exception -> 0x0d48 }
            goto L_0x0d4d
        L_0x0d48:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x0d4d:
            r11 = r6
            r35 = r9
            r9 = 1
        L_0x0d51:
            if (r35 == 0) goto L_0x0d73
            r17 = 1
            r1 = r74
            r2 = r75
            r3 = r76
            r4 = r77
            r5 = r78
            r6 = r79
            r7 = r80
            r8 = r81
            r9 = r11
            r10 = r83
            r12 = r85
            r14 = r87
            r16 = r89
            boolean r1 = r1.convertVideoInternal(r2, r3, r4, r5, r6, r7, r8, r9, r10, r12, r14, r16, r17)
            return r1
        L_0x0d73:
            return r9
        L_0x0d74:
            r0 = move-exception
            r1 = r74
            r2 = r0
        L_0x0d78:
            android.media.MediaExtractor r3 = r1.extractor
            if (r3 == 0) goto L_0x0d7f
            r3.release()
        L_0x0d7f:
            org.telegram.messenger.video.MP4Builder r3 = r1.mediaMuxer
            if (r3 == 0) goto L_0x0d8c
            r3.finishMovie()     // Catch:{ Exception -> 0x0d87 }
            goto L_0x0d8c
        L_0x0d87:
            r0 = move-exception
            r3 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)
        L_0x0d8c:
            goto L_0x0d8e
        L_0x0d8d:
            throw r2
        L_0x0d8e:
            goto L_0x0d8d
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.video.MediaCodecVideoConvertor.convertVideoInternal(java.lang.String, java.io.File, int, boolean, int, int, int, int, long, long, long, boolean, boolean):boolean");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:50:0x00f0, code lost:
        if (r9[r15 + 3] != 1) goto L_0x00f6;
     */
    /* JADX WARNING: Removed duplicated region for block: B:93:0x0199  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private long readAndWriteTracks(android.media.MediaExtractor r27, org.telegram.messenger.video.MP4Builder r28, android.media.MediaCodec.BufferInfo r29, long r30, long r32, long r34, java.io.File r36, boolean r37) throws java.lang.Exception {
        /*
            r26 = this;
            r0 = r26
            r1 = r27
            r2 = r28
            r3 = r29
            r4 = r30
            r6 = 0
            int r7 = org.telegram.messenger.MediaController.findTrack(r1, r6)
            r9 = 1
            if (r37 == 0) goto L_0x0019
            int r10 = org.telegram.messenger.MediaController.findTrack(r1, r9)
            r11 = r34
            goto L_0x001c
        L_0x0019:
            r11 = r34
            r10 = -1
        L_0x001c:
            float r11 = (float) r11
            r12 = 1148846080(0x447a0000, float:1000.0)
            float r11 = r11 / r12
            java.lang.String r13 = "max-input-size"
            r14 = 0
            if (r7 < 0) goto L_0x0041
            r1.selectTrack(r7)
            android.media.MediaFormat r12 = r1.getTrackFormat(r7)
            int r16 = r2.addTrack(r12, r6)
            int r12 = r12.getInteger(r13)
            int r17 = (r4 > r14 ? 1 : (r4 == r14 ? 0 : -1))
            if (r17 <= 0) goto L_0x003d
            r1.seekTo(r4, r6)
            goto L_0x0044
        L_0x003d:
            r1.seekTo(r14, r6)
            goto L_0x0044
        L_0x0041:
            r12 = 0
            r16 = -1
        L_0x0044:
            if (r10 < 0) goto L_0x007a
            r1.selectTrack(r10)
            android.media.MediaFormat r8 = r1.getTrackFormat(r10)
            java.lang.String r6 = "mime"
            java.lang.String r6 = r8.getString(r6)
            java.lang.String r14 = "audio/unknown"
            boolean r6 = r6.equals(r14)
            if (r6 == 0) goto L_0x005e
            r6 = -1
            r10 = -1
            goto L_0x007b
        L_0x005e:
            int r6 = r2.addTrack(r8, r9)
            int r8 = r8.getInteger(r13)
            int r12 = java.lang.Math.max(r8, r12)
            r13 = 0
            int r8 = (r4 > r13 ? 1 : (r4 == r13 ? 0 : -1))
            if (r8 <= 0) goto L_0x0075
            r8 = 0
            r1.seekTo(r4, r8)
            goto L_0x007b
        L_0x0075:
            r8 = 0
            r1.seekTo(r13, r8)
            goto L_0x007b
        L_0x007a:
            r6 = -1
        L_0x007b:
            java.nio.ByteBuffer r8 = java.nio.ByteBuffer.allocateDirect(r12)
            r12 = -1
            if (r10 >= 0) goto L_0x0087
            if (r7 < 0) goto L_0x0086
            goto L_0x0087
        L_0x0086:
            return r12
        L_0x0087:
            r26.checkConversionCanceled()
            r20 = r12
            r14 = 0
            r18 = 0
        L_0x008f:
            if (r14 != 0) goto L_0x01c2
            r26.checkConversionCanceled()
            r15 = 0
            int r12 = r1.readSampleData(r8, r15)
            r3.size = r12
            int r12 = r27.getSampleTrackIndex()
            if (r12 != r7) goto L_0x00a5
            r13 = r16
        L_0x00a3:
            r15 = -1
            goto L_0x00ab
        L_0x00a5:
            if (r12 != r10) goto L_0x00a9
            r13 = r6
            goto L_0x00a3
        L_0x00a9:
            r13 = -1
            goto L_0x00a3
        L_0x00ab:
            if (r13 == r15) goto L_0x019f
            int r15 = android.os.Build.VERSION.SDK_INT
            r9 = 21
            if (r15 >= r9) goto L_0x00bc
            r9 = 0
            r8.position(r9)
            int r9 = r3.size
            r8.limit(r9)
        L_0x00bc:
            if (r12 == r10) goto L_0x0129
            byte[] r9 = r8.array()
            if (r9 == 0) goto L_0x0129
            int r15 = r8.arrayOffset()
            int r22 = r8.limit()
            int r22 = r15 + r22
            r35 = r6
            r6 = -1
        L_0x00d1:
            r23 = 4
            r37 = r14
            int r14 = r22 + -4
            if (r15 > r14) goto L_0x0126
            byte r24 = r9[r15]
            if (r24 != 0) goto L_0x00f3
            int r24 = r15 + 1
            byte r24 = r9[r24]
            if (r24 != 0) goto L_0x00f3
            int r24 = r15 + 2
            byte r24 = r9[r24]
            if (r24 != 0) goto L_0x00f3
            int r24 = r15 + 3
            r25 = r10
            byte r10 = r9[r24]
            r1 = 1
            if (r10 == r1) goto L_0x00f8
            goto L_0x00f6
        L_0x00f3:
            r25 = r10
            r1 = 1
        L_0x00f6:
            if (r15 != r14) goto L_0x011d
        L_0x00f8:
            r10 = -1
            if (r6 == r10) goto L_0x011c
            int r10 = r15 - r6
            if (r15 == r14) goto L_0x0100
            goto L_0x0102
        L_0x0100:
            r23 = 0
        L_0x0102:
            int r10 = r10 - r23
            int r14 = r10 >> 24
            byte r14 = (byte) r14
            r9[r6] = r14
            int r14 = r6 + 1
            int r1 = r10 >> 16
            byte r1 = (byte) r1
            r9[r14] = r1
            int r1 = r6 + 2
            int r14 = r10 >> 8
            byte r14 = (byte) r14
            r9[r1] = r14
            int r6 = r6 + 3
            byte r1 = (byte) r10
            r9[r6] = r1
        L_0x011c:
            r6 = r15
        L_0x011d:
            int r15 = r15 + 1
            r1 = r27
            r14 = r37
            r10 = r25
            goto L_0x00d1
        L_0x0126:
            r25 = r10
            goto L_0x012f
        L_0x0129:
            r35 = r6
            r25 = r10
            r37 = r14
        L_0x012f:
            int r1 = r3.size
            if (r1 < 0) goto L_0x013b
            long r9 = r27.getSampleTime()
            r3.presentationTimeUs = r9
            r1 = 0
            goto L_0x013f
        L_0x013b:
            r1 = 0
            r3.size = r1
            r1 = 1
        L_0x013f:
            int r6 = r3.size
            if (r6 <= 0) goto L_0x0163
            if (r1 != 0) goto L_0x0163
            r9 = 0
            if (r12 != r7) goto L_0x0157
            int r6 = (r4 > r9 ? 1 : (r4 == r9 ? 0 : -1))
            if (r6 <= 0) goto L_0x0157
            r14 = -1
            int r6 = (r20 > r14 ? 1 : (r20 == r14 ? 0 : -1))
            if (r6 != 0) goto L_0x0157
            long r14 = r3.presentationTimeUs
            r20 = r14
        L_0x0157:
            int r6 = (r32 > r9 ? 1 : (r32 == r9 ? 0 : -1))
            if (r6 < 0) goto L_0x0167
            long r9 = r3.presentationTimeUs
            int r6 = (r9 > r32 ? 1 : (r9 == r32 ? 0 : -1))
            if (r6 >= 0) goto L_0x0162
            goto L_0x0167
        L_0x0162:
            r1 = 1
        L_0x0163:
            r6 = 0
        L_0x0164:
            r17 = 1148846080(0x447a0000, float:1000.0)
            goto L_0x0197
        L_0x0167:
            r6 = 0
            r3.offset = r6
            int r9 = r27.getSampleFlags()
            r3.flags = r9
            long r9 = r2.writeSampleData(r13, r8, r3, r6)
            r13 = 0
            int r12 = (r9 > r13 ? 1 : (r9 == r13 ? 0 : -1))
            if (r12 == 0) goto L_0x0164
            org.telegram.messenger.MediaController$VideoConvertorListener r12 = r0.callback
            if (r12 == 0) goto L_0x0164
            long r13 = r3.presentationTimeUs
            long r22 = r13 - r20
            int r12 = (r22 > r18 ? 1 : (r22 == r18 ? 0 : -1))
            if (r12 <= 0) goto L_0x0188
            long r18 = r13 - r20
        L_0x0188:
            r12 = r18
            org.telegram.messenger.MediaController$VideoConvertorListener r14 = r0.callback
            float r15 = (float) r12
            r17 = 1148846080(0x447a0000, float:1000.0)
            float r15 = r15 / r17
            float r15 = r15 / r11
            r14.didWriteData(r9, r15)
            r18 = r12
        L_0x0197:
            if (r1 != 0) goto L_0x019c
            r27.advance()
        L_0x019c:
            r9 = r1
            r1 = -1
            goto L_0x01b1
        L_0x019f:
            r35 = r6
            r25 = r10
            r37 = r14
            r1 = -1
            r6 = 0
            r17 = 1148846080(0x447a0000, float:1000.0)
            if (r12 != r1) goto L_0x01ad
            r9 = 1
            goto L_0x01b1
        L_0x01ad:
            r27.advance()
            r9 = 0
        L_0x01b1:
            if (r9 == 0) goto L_0x01b5
            r14 = 1
            goto L_0x01b7
        L_0x01b5:
            r14 = r37
        L_0x01b7:
            r1 = r27
            r6 = r35
            r10 = r25
            r9 = 1
            r12 = -1
            goto L_0x008f
        L_0x01c2:
            r25 = r10
            r1 = r27
            if (r7 < 0) goto L_0x01cb
            r1.unselectTrack(r7)
        L_0x01cb:
            if (r25 < 0) goto L_0x01d2
            r10 = r25
            r1.unselectTrack(r10)
        L_0x01d2:
            return r20
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.video.MediaCodecVideoConvertor.readAndWriteTracks(android.media.MediaExtractor, org.telegram.messenger.video.MP4Builder, android.media.MediaCodec$BufferInfo, long, long, long, java.io.File, boolean):long");
    }

    private void checkConversionCanceled() {
        MediaController.VideoConvertorListener videoConvertorListener = this.callback;
        if (videoConvertorListener != null && videoConvertorListener.checkConversionCanceled()) {
            throw new RuntimeException("canceled conversion");
        }
    }
}
