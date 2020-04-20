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
    /* JADX WARNING: Code restructure failed: missing block: B:153:0x029c, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:154:0x029d, code lost:
        r10 = r81;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:155:0x02a1, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:156:0x02a2, code lost:
        r10 = r81;
        r2 = r0;
        r8 = r1;
        r6 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:176:0x02da, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:177:0x02db, code lost:
        r10 = r81;
        r2 = r0;
        r8 = r1;
        r49 = r5;
        r1 = r6;
        r26 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:178:0x02e5, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:179:0x02e6, code lost:
        r10 = r81;
        r2 = r0;
        r8 = r1;
        r1 = r6;
        r26 = null;
        r49 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:180:0x02ef, code lost:
        r61 = null;
        r67 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:191:0x0315, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:192:0x0316, code lost:
        r2 = r0;
        r8 = r1;
        r1 = r6;
        r49 = r9;
        r67 = r10;
        r26 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:211:0x0360, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:256:0x0421, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:257:0x0423, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:258:0x0425, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:259:0x0426, code lost:
        r11 = r85;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:260:0x0428, code lost:
        r42 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:261:0x042a, code lost:
        r2 = r0;
        r8 = r1;
        r1 = r6;
        r67 = r10;
        r61 = r42;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:282:0x049c, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:283:0x049d, code lost:
        r2 = r0;
        r61 = r8;
        r67 = r10;
        r10 = r81;
        r8 = r1;
        r1 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:304:0x0503, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:305:0x0504, code lost:
        r2 = r0;
        r1 = r6;
        r61 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:334:0x0564, code lost:
        if (r5.presentationTimeUs < r11) goto L_0x0566;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:349:0x05a9, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:350:0x05aa, code lost:
        r11 = r83;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:354:0x05bf, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:355:0x05c0, code lost:
        r11 = r83;
        r61 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:366:0x05fe, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:369:0x0607, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:370:0x0608, code lost:
        r11 = r83;
        r61 = r8;
        r2 = r0;
        r8 = r1;
        r1 = r6;
        r67 = r10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x00ce, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x00cf, code lost:
        r10 = r81;
        r6 = r82;
        r1 = r0;
        r12 = r13;
        r4 = r17;
        r5 = r18;
        r7 = r23;
        r11 = r24;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x00de, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x00df, code lost:
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
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x00ef, code lost:
        r9 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:491:0x0823, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:492:0x0824, code lost:
        r9 = r79;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:561:0x0949, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:562:0x094a, code lost:
        r1 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:564:?, code lost:
        org.telegram.messenger.FileLog.e((java.lang.Throwable) r1);
        r9 = true;
        r1 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:576:0x098e, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:577:0x098f, code lost:
        r2 = r0;
        r1 = r6;
        r49 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:578:0x0994, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:579:0x0995, code lost:
        r8 = r49;
        r2 = r0;
        r1 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:580:0x0999, code lost:
        r26 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:592:0x0a2f, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:593:0x0a30, code lost:
        r32 = r6;
        r49 = r8;
        r69 = r9;
        r67 = r10;
        r8 = r62;
        r10 = r81;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:594:0x0a41, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:595:0x0a42, code lost:
        r32 = r6;
        r67 = r10;
        r69 = r26;
        r8 = r62;
        r10 = r81;
        r6 = r82;
        r2 = r0;
        r1 = r32;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:606:0x0a8a, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:621:0x0abf, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:622:0x0ac0, code lost:
        r1 = r32;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:631:0x0b71, code lost:
        r0 = e;
        r1 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:632:0x0b72, code lost:
        r6 = r82;
        r2 = r0;
        r1 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:634:0x0b79, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:635:0x0b7a, code lost:
        r1 = r6;
        r67 = r10;
        r69 = r26;
        r8 = r62;
        r10 = r81;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:636:0x0b88, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:637:0x0b89, code lost:
        r61 = r8;
        r67 = r10;
        r69 = r26;
        r10 = r81;
        r8 = r1;
        r1 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:638:0x0b97, code lost:
        r6 = r82;
        r2 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:639:0x0b9c, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:640:0x0b9d, code lost:
        r8 = r1;
        r1 = r6;
        r67 = r10;
        r69 = r26;
        r10 = r81;
        r6 = r82;
        r2 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:641:0x0bad, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:642:0x0bae, code lost:
        r8 = r1;
        r1 = r6;
        r49 = r9;
        r67 = r10;
        r10 = r81;
        r6 = r82;
        r2 = r0;
        r26 = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:643:0x0bc2, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:644:0x0bc3, code lost:
        r8 = r1;
        r1 = r6;
        r49 = r9;
        r67 = r10;
        r10 = r81;
        r6 = r82;
        r2 = r0;
        r26 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:645:0x0bd4, code lost:
        r61 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:646:0x0bd7, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:647:0x0bd8, code lost:
        r10 = r81;
        r8 = r1;
        r1 = r6;
        r49 = r9;
        r6 = r82;
        r2 = r0;
        r26 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:648:0x0be8, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:649:0x0be9, code lost:
        r10 = r81;
        r8 = r1;
        r1 = r6;
        r6 = r82;
        r2 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:650:0x0bf5, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:651:0x0bf6, code lost:
        r10 = r81;
        r8 = r1;
        r82 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:652:0x0bfc, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:653:0x0bfd, code lost:
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
    /* JADX WARNING: Code restructure failed: missing block: B:657:0x0c0a, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:658:0x0c0b, code lost:
        r10 = r81;
        r8 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:659:0x0c0e, code lost:
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
    /* JADX WARNING: Code restructure failed: missing block: B:662:0x0c1a, code lost:
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
    /* JADX WARNING: Code restructure failed: missing block: B:704:0x0c8c, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:705:0x0c8e, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:706:0x0c8f, code lost:
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
    /* JADX WARNING: Code restructure failed: missing block: B:711:0x0c9a, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:712:0x0c9b, code lost:
        r12 = r79;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:713:0x0c9e, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:714:0x0c9f, code lost:
        r12 = r79;
        r4 = r17;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:715:0x0ca3, code lost:
        r5 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:716:0x0ca5, code lost:
        r7 = r23;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:717:0x0ca7, code lost:
        r11 = r24;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:718:0x0ca9, code lost:
        r1 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:719:0x0cac, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:720:0x0cad, code lost:
        r12 = r79;
        r4 = r17;
        r5 = r18;
        r7 = r23;
        r11 = r24;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:738:0x0cfc, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:739:0x0cfd, code lost:
        r2 = r0;
        r1 = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:764:0x0d7b, code lost:
        r3.release();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:768:?, code lost:
        r3.finishMovie();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:769:0x0d86, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:770:0x0d87, code lost:
        org.telegram.messenger.FileLog.e((java.lang.Throwable) r0);
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:28:0x0097, B:89:0x01c5] */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:35:0x00c1, B:89:0x01c5] */
    /* JADX WARNING: Failed to insert additional move for type inference */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:122:0x0247 A[SYNTHETIC, Splitter:B:122:0x0247] */
    /* JADX WARNING: Removed duplicated region for block: B:125:0x0250  */
    /* JADX WARNING: Removed duplicated region for block: B:129:0x025c  */
    /* JADX WARNING: Removed duplicated region for block: B:130:0x0260  */
    /* JADX WARNING: Removed duplicated region for block: B:141:0x0283 A[SYNTHETIC, Splitter:B:141:0x0283] */
    /* JADX WARNING: Removed duplicated region for block: B:161:0x02af  */
    /* JADX WARNING: Removed duplicated region for block: B:171:0x02cc A[SYNTHETIC, Splitter:B:171:0x02cc] */
    /* JADX WARNING: Removed duplicated region for block: B:182:0x02fb  */
    /* JADX WARNING: Removed duplicated region for block: B:189:0x030f A[SYNTHETIC, Splitter:B:189:0x030f] */
    /* JADX WARNING: Removed duplicated region for block: B:195:0x0328 A[SYNTHETIC, Splitter:B:195:0x0328] */
    /* JADX WARNING: Removed duplicated region for block: B:204:0x0345 A[SYNTHETIC, Splitter:B:204:0x0345] */
    /* JADX WARNING: Removed duplicated region for block: B:213:0x0367  */
    /* JADX WARNING: Removed duplicated region for block: B:215:0x036d A[SYNTHETIC, Splitter:B:215:0x036d] */
    /* JADX WARNING: Removed duplicated region for block: B:226:0x039d  */
    /* JADX WARNING: Removed duplicated region for block: B:228:0x03a0  */
    /* JADX WARNING: Removed duplicated region for block: B:262:0x0433  */
    /* JADX WARNING: Removed duplicated region for block: B:266:0x0445  */
    /* JADX WARNING: Removed duplicated region for block: B:268:0x0453  */
    /* JADX WARNING: Removed duplicated region for block: B:269:0x0456  */
    /* JADX WARNING: Removed duplicated region for block: B:273:0x046d A[Catch:{ Exception -> 0x0b88, all -> 0x0cfc }] */
    /* JADX WARNING: Removed duplicated region for block: B:279:0x0493 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:286:0x04b0 A[SYNTHETIC, Splitter:B:286:0x04b0] */
    /* JADX WARNING: Removed duplicated region for block: B:359:0x05e1 A[Catch:{ Exception -> 0x05fe, all -> 0x0cfc }] */
    /* JADX WARNING: Removed duplicated region for block: B:371:0x0613  */
    /* JADX WARNING: Removed duplicated region for block: B:374:0x0629 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:379:0x0648 A[Catch:{ Exception -> 0x0b79, all -> 0x0cfc }] */
    /* JADX WARNING: Removed duplicated region for block: B:380:0x064b A[Catch:{ Exception -> 0x0b79, all -> 0x0cfc }] */
    /* JADX WARNING: Removed duplicated region for block: B:384:0x0654  */
    /* JADX WARNING: Removed duplicated region for block: B:386:0x0671  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x0102 A[SYNTHETIC, Splitter:B:46:0x0102] */
    /* JADX WARNING: Removed duplicated region for block: B:501:0x0857 A[Catch:{ Exception -> 0x0b79, all -> 0x0cfc }] */
    /* JADX WARNING: Removed duplicated region for block: B:502:0x086d A[Catch:{ Exception -> 0x0b79, all -> 0x0cfc }] */
    /* JADX WARNING: Removed duplicated region for block: B:601:0x0a80 A[Catch:{ Exception -> 0x0abf, all -> 0x0cfc }] */
    /* JADX WARNING: Removed duplicated region for block: B:620:0x0aba A[Catch:{ Exception -> 0x0b71, all -> 0x0cfc }] */
    /* JADX WARNING: Removed duplicated region for block: B:667:0x0CLASSNAME A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0CLASSNAME A[Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0cfc }] */
    /* JADX WARNING: Removed duplicated region for block: B:693:0x0CLASSNAME A[Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0cfc }] */
    /* JADX WARNING: Removed duplicated region for block: B:695:0x0c6e A[Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0cfc }] */
    /* JADX WARNING: Removed duplicated region for block: B:697:0x0CLASSNAME A[Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0cfc }] */
    /* JADX WARNING: Removed duplicated region for block: B:699:0x0c7e A[Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0cfc }] */
    /* JADX WARNING: Removed duplicated region for block: B:721:0x0cb9  */
    /* JADX WARNING: Removed duplicated region for block: B:724:0x0cc6  */
    /* JADX WARNING: Removed duplicated region for block: B:727:0x0ccd A[SYNTHETIC, Splitter:B:727:0x0ccd] */
    /* JADX WARNING: Removed duplicated region for block: B:738:0x0cfc A[ExcHandler: all (r0v6 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:89:0x01c5] */
    /* JADX WARNING: Removed duplicated region for block: B:748:0x0d3c  */
    /* JADX WARNING: Removed duplicated region for block: B:751:0x0d43 A[SYNTHETIC, Splitter:B:751:0x0d43] */
    /* JADX WARNING: Removed duplicated region for block: B:757:0x0d52  */
    /* JADX WARNING: Removed duplicated region for block: B:759:0x0d72 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:764:0x0d7b  */
    /* JADX WARNING: Removed duplicated region for block: B:767:0x0d82 A[SYNTHETIC, Splitter:B:767:0x0d82] */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x0171 A[Catch:{ Exception -> 0x01a8, all -> 0x0cfc }] */
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
            android.media.MediaCodec$BufferInfo r9 = new android.media.MediaCodec$BufferInfo     // Catch:{ Exception -> 0x0d01, all -> 0x0cfc }
            r9.<init>()     // Catch:{ Exception -> 0x0d01, all -> 0x0cfc }
            r18 = r1
            org.telegram.messenger.video.Mp4Movie r1 = new org.telegram.messenger.video.Mp4Movie     // Catch:{ Exception -> 0x0cee, all -> 0x0cfc }
            r1.<init>()     // Catch:{ Exception -> 0x0cee, all -> 0x0cfc }
            r7 = r76
            r1.setCacheFile(r7)     // Catch:{ Exception -> 0x0cee, all -> 0x0cfc }
            r1.setRotation(r15)     // Catch:{ Exception -> 0x0cee, all -> 0x0cfc }
            r1.setSize(r12, r11)     // Catch:{ Exception -> 0x0cee, all -> 0x0cfc }
            org.telegram.messenger.video.MP4Builder r8 = new org.telegram.messenger.video.MP4Builder     // Catch:{ Exception -> 0x0cee, all -> 0x0cfc }
            r8.<init>()     // Catch:{ Exception -> 0x0cee, all -> 0x0cfc }
            r15 = r78
            org.telegram.messenger.video.MP4Builder r1 = r8.createMovie(r1, r15)     // Catch:{ Exception -> 0x0cee, all -> 0x0cfc }
            r14.mediaMuxer = r1     // Catch:{ Exception -> 0x0cee, all -> 0x0cfc }
            android.media.MediaExtractor r1 = new android.media.MediaExtractor     // Catch:{ Exception -> 0x0cee, all -> 0x0cfc }
            r1.<init>()     // Catch:{ Exception -> 0x0cee, all -> 0x0cfc }
            r14.extractor = r1     // Catch:{ Exception -> 0x0cee, all -> 0x0cfc }
            r1.setDataSource(r13)     // Catch:{ Exception -> 0x0cee, all -> 0x0cfc }
            r12 = r87
            float r1 = (float) r12
            r8 = 1148846080(0x447a0000, float:1000.0)
            float r19 = r1 / r8
            r74.checkConversionCanceled()     // Catch:{ Exception -> 0x0cea, all -> 0x0cfc }
            android.media.MediaExtractor r1 = r14.extractor     // Catch:{ Exception -> 0x0cea, all -> 0x0cfc }
            r11 = 0
            int r1 = org.telegram.messenger.MediaController.findTrack(r1, r11)     // Catch:{ Exception -> 0x0cd9, all -> 0x0cfc }
            r11 = -1
            if (r10 == r11) goto L_0x0077
            android.media.MediaExtractor r8 = r14.extractor     // Catch:{ Exception -> 0x0064, all -> 0x0cfc }
            r11 = 1
            int r8 = org.telegram.messenger.MediaController.findTrack(r8, r11)     // Catch:{ Exception -> 0x0064, all -> 0x0cfc }
            goto L_0x0078
        L_0x0064:
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
            goto L_0x0d11
        L_0x0077:
            r8 = -1
        L_0x0078:
            java.lang.String r11 = "mime"
            java.lang.String r12 = "video/avc"
            if (r1 < 0) goto L_0x0090
            android.media.MediaExtractor r13 = r14.extractor     // Catch:{ Exception -> 0x0064, all -> 0x0cfc }
            android.media.MediaFormat r13 = r13.getTrackFormat(r1)     // Catch:{ Exception -> 0x0064, all -> 0x0cfc }
            java.lang.String r13 = r13.getString(r11)     // Catch:{ Exception -> 0x0064, all -> 0x0cfc }
            boolean r13 = r13.equals(r12)     // Catch:{ Exception -> 0x0064, all -> 0x0cfc }
            if (r13 != 0) goto L_0x0090
            r13 = 1
            goto L_0x0091
        L_0x0090:
            r13 = 0
        L_0x0091:
            if (r89 != 0) goto L_0x00f4
            if (r13 == 0) goto L_0x0097
            goto L_0x00f4
        L_0x0097:
            android.media.MediaExtractor r8 = r14.extractor     // Catch:{ Exception -> 0x00de, all -> 0x0cfc }
            org.telegram.messenger.video.MP4Builder r11 = r14.mediaMuxer     // Catch:{ Exception -> 0x00de, all -> 0x0cfc }
            r1 = -1
            r13 = r18
            if (r10 == r1) goto L_0x00a2
            r12 = 1
            goto L_0x00a3
        L_0x00a2:
            r12 = 0
        L_0x00a3:
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
            r1.readAndWriteTracks(r2, r3, r4, r5, r7, r9, r11, r12)     // Catch:{ Exception -> 0x00ce, all -> 0x0cfc }
            r10 = r81
            r6 = r82
            r12 = r13
        L_0x00c9:
            r9 = 0
            r35 = 0
            goto L_0x0cc2
        L_0x00ce:
            r0 = move-exception
            r10 = r81
            r6 = r82
            r1 = r0
            r12 = r13
            r4 = r17
            r5 = r18
            r7 = r23
            r11 = r24
            goto L_0x00ef
        L_0x00de:
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
        L_0x00ef:
            r9 = 0
            r62 = 1
            goto L_0x0d11
        L_0x00f4:
            r13 = r79
            r15 = r80
            r23 = r3
            r24 = r4
            r17 = r18
            r18 = r2
            if (r1 < 0) goto L_0x0cb9
            java.lang.String r4 = android.os.Build.MANUFACTURER     // Catch:{ Exception -> 0x0c0a, all -> 0x0cfc }
            java.lang.String r4 = r4.toLowerCase()     // Catch:{ Exception -> 0x0c0a, all -> 0x0cfc }
            int r5 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0c0a, all -> 0x0cfc }
            r25 = 4
            r7 = 16
            r10 = 2
            r6 = 18
            if (r5 >= r6) goto L_0x01bc
            android.media.MediaCodecInfo r5 = org.telegram.messenger.MediaController.selectCodec(r12)     // Catch:{ Exception -> 0x01a8, all -> 0x0cfc }
            int r29 = org.telegram.messenger.MediaController.selectColorFormat(r5, r12)     // Catch:{ Exception -> 0x01a8, all -> 0x0cfc }
            if (r29 == 0) goto L_0x01a0
            java.lang.String r2 = r5.getName()     // Catch:{ Exception -> 0x01a8, all -> 0x0cfc }
            java.lang.String r3 = "OMX.qcom."
            boolean r3 = r2.contains(r3)     // Catch:{ Exception -> 0x01a8, all -> 0x0cfc }
            if (r3 == 0) goto L_0x0143
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x01a8, all -> 0x0cfc }
            if (r2 != r7) goto L_0x0140
            java.lang.String r2 = "lge"
            boolean r2 = r4.equals(r2)     // Catch:{ Exception -> 0x01a8, all -> 0x0cfc }
            if (r2 != 0) goto L_0x013d
            java.lang.String r2 = "nokia"
            boolean r2 = r4.equals(r2)     // Catch:{ Exception -> 0x01a8, all -> 0x0cfc }
            if (r2 == 0) goto L_0x0140
        L_0x013d:
            r2 = 1
        L_0x013e:
            r3 = 1
            goto L_0x016d
        L_0x0140:
            r2 = 1
        L_0x0141:
            r3 = 0
            goto L_0x016d
        L_0x0143:
            java.lang.String r3 = "OMX.Intel."
            boolean r3 = r2.contains(r3)     // Catch:{ Exception -> 0x01a8, all -> 0x0cfc }
            if (r3 == 0) goto L_0x014d
            r2 = 2
            goto L_0x0141
        L_0x014d:
            java.lang.String r3 = "OMX.MTK.VIDEO.ENCODER.AVC"
            boolean r3 = r2.equals(r3)     // Catch:{ Exception -> 0x01a8, all -> 0x0cfc }
            if (r3 == 0) goto L_0x0157
            r2 = 3
            goto L_0x0141
        L_0x0157:
            java.lang.String r3 = "OMX.SEC.AVC.Encoder"
            boolean r3 = r2.equals(r3)     // Catch:{ Exception -> 0x01a8, all -> 0x0cfc }
            if (r3 == 0) goto L_0x0161
            r2 = 4
            goto L_0x013e
        L_0x0161:
            java.lang.String r3 = "OMX.TI.DUCATI1.VIDEO.H264E"
            boolean r2 = r2.equals(r3)     // Catch:{ Exception -> 0x01a8, all -> 0x0cfc }
            if (r2 == 0) goto L_0x016b
            r2 = 5
            goto L_0x0141
        L_0x016b:
            r2 = 0
            goto L_0x0141
        L_0x016d:
            boolean r32 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x01a8, all -> 0x0cfc }
            if (r32 == 0) goto L_0x019b
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01a8, all -> 0x0cfc }
            r6.<init>()     // Catch:{ Exception -> 0x01a8, all -> 0x0cfc }
            java.lang.String r7 = "codec = "
            r6.append(r7)     // Catch:{ Exception -> 0x01a8, all -> 0x0cfc }
            java.lang.String r5 = r5.getName()     // Catch:{ Exception -> 0x01a8, all -> 0x0cfc }
            r6.append(r5)     // Catch:{ Exception -> 0x01a8, all -> 0x0cfc }
            java.lang.String r5 = " manufacturer = "
            r6.append(r5)     // Catch:{ Exception -> 0x01a8, all -> 0x0cfc }
            r6.append(r4)     // Catch:{ Exception -> 0x01a8, all -> 0x0cfc }
            java.lang.String r5 = "device = "
            r6.append(r5)     // Catch:{ Exception -> 0x01a8, all -> 0x0cfc }
            java.lang.String r5 = android.os.Build.MODEL     // Catch:{ Exception -> 0x01a8, all -> 0x0cfc }
            r6.append(r5)     // Catch:{ Exception -> 0x01a8, all -> 0x0cfc }
            java.lang.String r5 = r6.toString()     // Catch:{ Exception -> 0x01a8, all -> 0x0cfc }
            org.telegram.messenger.FileLog.d(r5)     // Catch:{ Exception -> 0x01a8, all -> 0x0cfc }
        L_0x019b:
            r7 = r29
            r29 = r3
            goto L_0x01c5
        L_0x01a0:
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x01a8, all -> 0x0cfc }
            java.lang.String r3 = "no supported color format"
            r2.<init>(r3)     // Catch:{ Exception -> 0x01a8, all -> 0x0cfc }
            throw r2     // Catch:{ Exception -> 0x01a8, all -> 0x0cfc }
        L_0x01a8:
            r0 = move-exception
            r10 = r81
            r6 = r82
        L_0x01ad:
            r2 = r0
            r8 = r1
        L_0x01af:
            r1 = 0
            r26 = 0
            r49 = 0
            r61 = 0
            r62 = 1
            r66 = 0
            goto L_0x0c1c
        L_0x01bc:
            r29 = 2130708361(0x7var_, float:1.701803E38)
            r2 = 0
            r7 = 2130708361(0x7var_, float:1.701803E38)
            r29 = 0
        L_0x01c5:
            boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0c0a, all -> 0x0cfc }
            if (r3 == 0) goto L_0x01dd
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01a8, all -> 0x0cfc }
            r3.<init>()     // Catch:{ Exception -> 0x01a8, all -> 0x0cfc }
            java.lang.String r5 = "colorFormat = "
            r3.append(r5)     // Catch:{ Exception -> 0x01a8, all -> 0x0cfc }
            r3.append(r7)     // Catch:{ Exception -> 0x01a8, all -> 0x0cfc }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x01a8, all -> 0x0cfc }
            org.telegram.messenger.FileLog.d(r3)     // Catch:{ Exception -> 0x01a8, all -> 0x0cfc }
        L_0x01dd:
            int r3 = r13 * r15
            int r5 = r3 * 3
            int r5 = r5 / r10
            if (r2 != 0) goto L_0x01fc
            int r2 = r15 % 16
            if (r2 == 0) goto L_0x0230
            int r2 = r15 % 16
            r3 = 16
            int r2 = 16 - r2
            int r2 = r2 + r15
            int r2 = r2 - r15
            int r2 = r2 * r13
            int r3 = r2 * 5
            int r3 = r3 / 4
        L_0x01f6:
            int r5 = r5 + r3
        L_0x01f7:
            r40 = r2
            r27 = r5
            goto L_0x0234
        L_0x01fc:
            r6 = 1
            if (r2 != r6) goto L_0x0212
            java.lang.String r2 = r4.toLowerCase()     // Catch:{ Exception -> 0x01a8, all -> 0x0cfc }
            java.lang.String r4 = "lge"
            boolean r2 = r2.equals(r4)     // Catch:{ Exception -> 0x01a8, all -> 0x0cfc }
            if (r2 != 0) goto L_0x0230
            int r2 = r3 + 2047
            r2 = r2 & -2048(0xffffffffffffvar_, float:NaN)
            int r2 = r2 - r3
            int r5 = r5 + r2
            goto L_0x01f7
        L_0x0212:
            r3 = 5
            if (r2 != r3) goto L_0x0216
            goto L_0x0230
        L_0x0216:
            r3 = 3
            if (r2 != r3) goto L_0x0230
            java.lang.String r2 = "baidu"
            boolean r2 = r4.equals(r2)     // Catch:{ Exception -> 0x01a8, all -> 0x0cfc }
            if (r2 == 0) goto L_0x0230
            int r2 = r15 % 16
            r3 = 16
            int r2 = 16 - r2
            int r2 = r2 + r15
            int r2 = r2 - r15
            int r2 = r2 * r13
            int r3 = r2 * 5
            int r3 = r3 / 4
            goto L_0x01f6
        L_0x0230:
            r27 = r5
            r40 = 0
        L_0x0234:
            android.media.MediaExtractor r2 = r14.extractor     // Catch:{ Exception -> 0x0c0a, all -> 0x0cfc }
            r2.selectTrack(r1)     // Catch:{ Exception -> 0x0c0a, all -> 0x0cfc }
            android.media.MediaExtractor r2 = r14.extractor     // Catch:{ Exception -> 0x0c0a, all -> 0x0cfc }
            android.media.MediaFormat r2 = r2.getTrackFormat(r1)     // Catch:{ Exception -> 0x0c0a, all -> 0x0cfc }
            r5 = 0
            r3 = r83
            int r33 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r33 <= 0) goto L_0x0250
            android.media.MediaExtractor r10 = r14.extractor     // Catch:{ Exception -> 0x01a8, all -> 0x0cfc }
            r5 = 0
            r10.seekTo(r3, r5)     // Catch:{ Exception -> 0x01a8, all -> 0x0cfc }
            r22 = r9
            goto L_0x025a
        L_0x0250:
            r5 = 0
            android.media.MediaExtractor r6 = r14.extractor     // Catch:{ Exception -> 0x0c0a, all -> 0x0cfc }
            r22 = r9
            r9 = 0
            r6.seekTo(r9, r5)     // Catch:{ Exception -> 0x0c0a, all -> 0x0cfc }
        L_0x025a:
            if (r82 > 0) goto L_0x0260
            r9 = 921600(0xe1000, float:1.291437E-39)
            goto L_0x0262
        L_0x0260:
            r9 = r82
        L_0x0262:
            android.media.MediaFormat r10 = android.media.MediaFormat.createVideoFormat(r12, r13, r15)     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0cfc }
            java.lang.String r5 = "color-format"
            r10.setInteger(r5, r7)     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0cfc }
            java.lang.String r5 = "bitrate"
            r10.setInteger(r5, r9)     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0cfc }
            java.lang.String r5 = "frame-rate"
            r6 = r81
            r10.setInteger(r5, r6)     // Catch:{ Exception -> 0x0bfc, all -> 0x0cfc }
            java.lang.String r5 = "i-frame-interval"
            r6 = 2
            r10.setInteger(r5, r6)     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0cfc }
            int r5 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0cfc }
            r6 = 23
            if (r5 >= r6) goto L_0x02a9
            int r5 = java.lang.Math.min(r15, r13)     // Catch:{ Exception -> 0x02a1, all -> 0x0cfc }
            r6 = 480(0x1e0, float:6.73E-43)
            if (r5 > r6) goto L_0x02a9
            r5 = 921600(0xe1000, float:1.291437E-39)
            if (r9 <= r5) goto L_0x0294
            r6 = 921600(0xe1000, float:1.291437E-39)
            goto L_0x0295
        L_0x0294:
            r6 = r9
        L_0x0295:
            java.lang.String r5 = "bitrate"
            r10.setInteger(r5, r6)     // Catch:{ Exception -> 0x029c, all -> 0x0cfc }
            r9 = r6
            goto L_0x02a9
        L_0x029c:
            r0 = move-exception
            r10 = r81
            goto L_0x01ad
        L_0x02a1:
            r0 = move-exception
            r10 = r81
            r2 = r0
            r8 = r1
            r6 = r9
            goto L_0x01af
        L_0x02a9:
            int r5 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0bf5, all -> 0x0cfc }
            r6 = 18
            if (r5 >= r6) goto L_0x02bb
            java.lang.String r5 = "stride"
            int r6 = r13 + 32
            r10.setInteger(r5, r6)     // Catch:{ Exception -> 0x02a1, all -> 0x0cfc }
            java.lang.String r5 = "slice-height"
            r10.setInteger(r5, r15)     // Catch:{ Exception -> 0x02a1, all -> 0x0cfc }
        L_0x02bb:
            android.media.MediaCodec r6 = android.media.MediaCodec.createEncoderByType(r12)     // Catch:{ Exception -> 0x0bf5, all -> 0x0cfc }
            r82 = r9
            r5 = 0
            r9 = 1
            r6.configure(r10, r5, r5, r9)     // Catch:{ Exception -> 0x0be8, all -> 0x0cfc }
            int r5 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0be8, all -> 0x0cfc }
            r9 = 18
            if (r5 < r9) goto L_0x02fb
            org.telegram.messenger.video.InputSurface r5 = new org.telegram.messenger.video.InputSurface     // Catch:{ Exception -> 0x02e5, all -> 0x0cfc }
            android.view.Surface r9 = r6.createInputSurface()     // Catch:{ Exception -> 0x02e5, all -> 0x0cfc }
            r5.<init>(r9)     // Catch:{ Exception -> 0x02e5, all -> 0x0cfc }
            r5.makeCurrent()     // Catch:{ Exception -> 0x02da, all -> 0x0cfc }
            r9 = r5
            goto L_0x02fc
        L_0x02da:
            r0 = move-exception
            r10 = r81
            r2 = r0
            r8 = r1
            r49 = r5
            r1 = r6
            r26 = 0
            goto L_0x02ef
        L_0x02e5:
            r0 = move-exception
            r10 = r81
            r2 = r0
            r8 = r1
            r1 = r6
            r26 = 0
            r49 = 0
        L_0x02ef:
            r61 = 0
            r62 = 1
            r66 = 0
            r67 = 0
        L_0x02f7:
            r6 = r82
            goto L_0x0c1e
        L_0x02fb:
            r9 = 0
        L_0x02fc:
            r6.start()     // Catch:{ Exception -> 0x0bd7, all -> 0x0cfc }
            java.lang.String r5 = r2.getString(r11)     // Catch:{ Exception -> 0x0bd7, all -> 0x0cfc }
            android.media.MediaCodec r10 = android.media.MediaCodec.createDecoderByType(r5)     // Catch:{ Exception -> 0x0bd7, all -> 0x0cfc }
            int r5 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0bc2, all -> 0x0cfc }
            r36 = r7
            r7 = 18
            if (r5 < r7) goto L_0x0328
            org.telegram.messenger.video.OutputSurface r5 = new org.telegram.messenger.video.OutputSurface     // Catch:{ Exception -> 0x0315, all -> 0x0cfc }
            r5.<init>()     // Catch:{ Exception -> 0x0315, all -> 0x0cfc }
            goto L_0x032f
        L_0x0315:
            r0 = move-exception
            r2 = r0
            r8 = r1
            r1 = r6
            r49 = r9
            r67 = r10
            r26 = 0
        L_0x031f:
            r61 = 0
        L_0x0321:
            r62 = 1
            r66 = 0
            r10 = r81
            goto L_0x02f7
        L_0x0328:
            org.telegram.messenger.video.OutputSurface r5 = new org.telegram.messenger.video.OutputSurface     // Catch:{ Exception -> 0x0bc2, all -> 0x0cfc }
            r7 = r77
            r5.<init>(r13, r15, r7)     // Catch:{ Exception -> 0x0bc2, all -> 0x0cfc }
        L_0x032f:
            android.view.Surface r7 = r5.getSurface()     // Catch:{ Exception -> 0x0bad, all -> 0x0cfc }
            r26 = r5
            r49 = r9
            r5 = 0
            r9 = 0
            r10.configure(r2, r7, r9, r5)     // Catch:{ Exception -> 0x0b9c, all -> 0x0cfc }
            r10.start()     // Catch:{ Exception -> 0x0b9c, all -> 0x0cfc }
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b9c, all -> 0x0cfc }
            r7 = 21
            if (r2 >= r7) goto L_0x0367
            java.nio.ByteBuffer[] r5 = r10.getInputBuffers()     // Catch:{ Exception -> 0x0360, all -> 0x0cfc }
            java.nio.ByteBuffer[] r2 = r6.getOutputBuffers()     // Catch:{ Exception -> 0x0360, all -> 0x0cfc }
            int r9 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0360, all -> 0x0cfc }
            r7 = 18
            if (r9 >= r7) goto L_0x035b
            java.nio.ByteBuffer[] r7 = r6.getInputBuffers()     // Catch:{ Exception -> 0x0360, all -> 0x0cfc }
            r9 = r5
            r50 = r7
            goto L_0x035e
        L_0x035b:
            r9 = r5
            r50 = 0
        L_0x035e:
            r5 = r2
            goto L_0x036b
        L_0x0360:
            r0 = move-exception
        L_0x0361:
            r2 = r0
            r8 = r1
            r1 = r6
            r67 = r10
            goto L_0x031f
        L_0x0367:
            r5 = 0
            r9 = 0
            r50 = 0
        L_0x036b:
            if (r8 < 0) goto L_0x0445
            android.media.MediaExtractor r2 = r14.extractor     // Catch:{ Exception -> 0x0440, all -> 0x0cfc }
            android.media.MediaFormat r2 = r2.getTrackFormat(r8)     // Catch:{ Exception -> 0x0440, all -> 0x0cfc }
            java.lang.String r7 = r2.getString(r11)     // Catch:{ Exception -> 0x0440, all -> 0x0cfc }
            r39 = r5
            java.lang.String r5 = "audio/mp4a-latm"
            boolean r5 = r7.equals(r5)     // Catch:{ Exception -> 0x0440, all -> 0x0cfc }
            if (r5 != 0) goto L_0x0390
            java.lang.String r5 = r2.getString(r11)     // Catch:{ Exception -> 0x0360, all -> 0x0cfc }
            java.lang.String r7 = "audio/mpeg"
            boolean r5 = r5.equals(r7)     // Catch:{ Exception -> 0x0360, all -> 0x0cfc }
            if (r5 == 0) goto L_0x038e
            goto L_0x0390
        L_0x038e:
            r5 = 0
            goto L_0x0391
        L_0x0390:
            r5 = 1
        L_0x0391:
            java.lang.String r7 = r2.getString(r11)     // Catch:{ Exception -> 0x0440, all -> 0x0cfc }
            java.lang.String r11 = "audio/unknown"
            boolean r7 = r7.equals(r11)     // Catch:{ Exception -> 0x0440, all -> 0x0cfc }
            if (r7 == 0) goto L_0x039e
            r8 = -1
        L_0x039e:
            if (r8 < 0) goto L_0x0433
            if (r5 == 0) goto L_0x03e3
            org.telegram.messenger.video.MP4Builder r7 = r14.mediaMuxer     // Catch:{ Exception -> 0x0360, all -> 0x0cfc }
            r11 = 1
            int r7 = r7.addTrack(r2, r11)     // Catch:{ Exception -> 0x0360, all -> 0x0cfc }
            android.media.MediaExtractor r11 = r14.extractor     // Catch:{ Exception -> 0x0360, all -> 0x0cfc }
            r11.selectTrack(r8)     // Catch:{ Exception -> 0x0360, all -> 0x0cfc }
            java.lang.String r11 = "max-input-size"
            int r2 = r2.getInteger(r11)     // Catch:{ Exception -> 0x0360, all -> 0x0cfc }
            java.nio.ByteBuffer r2 = java.nio.ByteBuffer.allocateDirect(r2)     // Catch:{ Exception -> 0x0360, all -> 0x0cfc }
            r33 = 0
            int r11 = (r3 > r33 ? 1 : (r3 == r33 ? 0 : -1))
            if (r11 <= 0) goto L_0x03cb
            android.media.MediaExtractor r11 = r14.extractor     // Catch:{ Exception -> 0x0360, all -> 0x0cfc }
            r42 = r2
            r2 = 0
            r11.seekTo(r3, r2)     // Catch:{ Exception -> 0x0360, all -> 0x0cfc }
            r43 = r5
            r51 = r12
            goto L_0x03d9
        L_0x03cb:
            r42 = r2
            android.media.MediaExtractor r2 = r14.extractor     // Catch:{ Exception -> 0x0360, all -> 0x0cfc }
            r43 = r5
            r51 = r12
            r5 = 0
            r11 = 0
            r2.seekTo(r11, r5)     // Catch:{ Exception -> 0x0360, all -> 0x0cfc }
        L_0x03d9:
            r11 = r85
            r5 = r7
            r2 = r8
            r7 = r42
            r52 = r43
            goto L_0x043e
        L_0x03e3:
            r43 = r5
            r51 = r12
            android.media.MediaExtractor r5 = new android.media.MediaExtractor     // Catch:{ Exception -> 0x0440, all -> 0x0cfc }
            r5.<init>()     // Catch:{ Exception -> 0x0440, all -> 0x0cfc }
            r11 = r75
            r5.setDataSource(r11)     // Catch:{ Exception -> 0x0440, all -> 0x0cfc }
            r5.selectTrack(r8)     // Catch:{ Exception -> 0x0440, all -> 0x0cfc }
            r11 = 0
            int r7 = (r3 > r11 ? 1 : (r3 == r11 ? 0 : -1))
            if (r7 <= 0) goto L_0x03ff
            r7 = 0
            r5.seekTo(r3, r7)     // Catch:{ Exception -> 0x0360, all -> 0x0cfc }
            goto L_0x0403
        L_0x03ff:
            r7 = 0
            r5.seekTo(r11, r7)     // Catch:{ Exception -> 0x0440, all -> 0x0cfc }
        L_0x0403:
            org.telegram.messenger.video.AudioRecoder r7 = new org.telegram.messenger.video.AudioRecoder     // Catch:{ Exception -> 0x0440, all -> 0x0cfc }
            r7.<init>(r2, r5, r8)     // Catch:{ Exception -> 0x0440, all -> 0x0cfc }
            r7.startTime = r3     // Catch:{ Exception -> 0x0425, all -> 0x0cfc }
            r11 = r85
            r7.endTime = r11     // Catch:{ Exception -> 0x0423, all -> 0x0cfc }
            org.telegram.messenger.video.MP4Builder r2 = r14.mediaMuxer     // Catch:{ Exception -> 0x0423, all -> 0x0cfc }
            android.media.MediaFormat r5 = r7.format     // Catch:{ Exception -> 0x0423, all -> 0x0cfc }
            r42 = r7
            r7 = 1
            int r2 = r2.addTrack(r5, r7)     // Catch:{ Exception -> 0x0421, all -> 0x0cfc }
            r5 = r2
            r2 = r8
            r8 = r42
            r52 = r43
            r7 = 0
            goto L_0x0451
        L_0x0421:
            r0 = move-exception
            goto L_0x042a
        L_0x0423:
            r0 = move-exception
            goto L_0x0428
        L_0x0425:
            r0 = move-exception
            r11 = r85
        L_0x0428:
            r42 = r7
        L_0x042a:
            r2 = r0
            r8 = r1
            r1 = r6
            r67 = r10
            r61 = r42
            goto L_0x0321
        L_0x0433:
            r43 = r5
            r51 = r12
            r11 = r85
            r2 = r8
            r52 = r43
            r5 = -5
            r7 = 0
        L_0x043e:
            r8 = 0
            goto L_0x0451
        L_0x0440:
            r0 = move-exception
            r11 = r85
            goto L_0x0361
        L_0x0445:
            r39 = r5
            r51 = r12
            r11 = r85
            r2 = r8
            r5 = -5
            r7 = 0
            r8 = 0
            r52 = 1
        L_0x0451:
            if (r2 >= 0) goto L_0x0456
            r42 = 1
            goto L_0x0458
        L_0x0456:
            r42 = 0
        L_0x0458:
            r74.checkConversionCanceled()     // Catch:{ Exception -> 0x0b88, all -> 0x0cfc }
            r30 = -1
            r53 = 0
            r54 = 0
            r55 = 0
            r56 = 0
            r57 = 1
            r58 = 0
            r60 = -5
        L_0x046b:
            if (r53 == 0) goto L_0x048e
            if (r52 != 0) goto L_0x0472
            if (r42 != 0) goto L_0x0472
            goto L_0x048e
        L_0x0472:
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
            goto L_0x0c5d
        L_0x048e:
            r74.checkConversionCanceled()     // Catch:{ Exception -> 0x0b88, all -> 0x0cfc }
            if (r52 != 0) goto L_0x04ac
            if (r8 == 0) goto L_0x04ac
            org.telegram.messenger.video.MP4Builder r13 = r14.mediaMuxer     // Catch:{ Exception -> 0x049c, all -> 0x0cfc }
            boolean r13 = r8.step(r13, r5)     // Catch:{ Exception -> 0x049c, all -> 0x0cfc }
            goto L_0x04ae
        L_0x049c:
            r0 = move-exception
            r2 = r0
            r61 = r8
            r67 = r10
            r62 = 1
            r66 = 0
            r10 = r81
            r8 = r1
            r1 = r6
            goto L_0x02f7
        L_0x04ac:
            r13 = r42
        L_0x04ae:
            if (r54 != 0) goto L_0x0613
            android.media.MediaExtractor r3 = r14.extractor     // Catch:{ Exception -> 0x0607, all -> 0x0cfc }
            int r3 = r3.getSampleTrackIndex()     // Catch:{ Exception -> 0x0607, all -> 0x0cfc }
            if (r3 != r1) goto L_0x051b
            r61 = r5
            r4 = 2500(0x9c4, double:1.235E-320)
            int r3 = r10.dequeueInputBuffer(r4)     // Catch:{ Exception -> 0x049c, all -> 0x0cfc }
            if (r3 < 0) goto L_0x050a
            int r4 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x049c, all -> 0x0cfc }
            r5 = 21
            if (r4 >= r5) goto L_0x04cb
            r4 = r9[r3]     // Catch:{ Exception -> 0x049c, all -> 0x0cfc }
            goto L_0x04cf
        L_0x04cb:
            java.nio.ByteBuffer r4 = r10.getInputBuffer(r3)     // Catch:{ Exception -> 0x049c, all -> 0x0cfc }
        L_0x04cf:
            android.media.MediaExtractor r5 = r14.extractor     // Catch:{ Exception -> 0x049c, all -> 0x0cfc }
            r62 = r1
            r1 = 0
            int r45 = r5.readSampleData(r4, r1)     // Catch:{ Exception -> 0x0503, all -> 0x0cfc }
            if (r45 >= 0) goto L_0x04ec
            r44 = 0
            r45 = 0
            r46 = 0
            r48 = 4
            r42 = r10
            r43 = r3
            r42.queueInputBuffer(r43, r44, r45, r46, r48)     // Catch:{ Exception -> 0x0503, all -> 0x0cfc }
            r54 = 1
            goto L_0x050c
        L_0x04ec:
            r44 = 0
            android.media.MediaExtractor r1 = r14.extractor     // Catch:{ Exception -> 0x0503, all -> 0x0cfc }
            long r46 = r1.getSampleTime()     // Catch:{ Exception -> 0x0503, all -> 0x0cfc }
            r48 = 0
            r42 = r10
            r43 = r3
            r42.queueInputBuffer(r43, r44, r45, r46, r48)     // Catch:{ Exception -> 0x0503, all -> 0x0cfc }
            android.media.MediaExtractor r1 = r14.extractor     // Catch:{ Exception -> 0x0503, all -> 0x0cfc }
            r1.advance()     // Catch:{ Exception -> 0x0503, all -> 0x0cfc }
            goto L_0x050c
        L_0x0503:
            r0 = move-exception
            r2 = r0
            r1 = r6
            r61 = r8
            goto L_0x0601
        L_0x050a:
            r62 = r1
        L_0x050c:
            r11 = r83
            r63 = r7
            r5 = r22
            r64 = r61
            r1 = 0
            r22 = r2
            r61 = r8
            goto L_0x05e4
        L_0x051b:
            r62 = r1
            r61 = r5
            if (r52 == 0) goto L_0x05d2
            r1 = -1
            if (r2 == r1) goto L_0x05c5
            if (r3 != r2) goto L_0x05d2
            android.media.MediaExtractor r1 = r14.extractor     // Catch:{ Exception -> 0x05bf, all -> 0x0cfc }
            r3 = 0
            int r1 = r1.readSampleData(r7, r3)     // Catch:{ Exception -> 0x05bf, all -> 0x0cfc }
            r5 = r22
            r5.size = r1     // Catch:{ Exception -> 0x05bf, all -> 0x0cfc }
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x05bf, all -> 0x0cfc }
            r4 = 21
            if (r1 >= r4) goto L_0x053f
            r7.position(r3)     // Catch:{ Exception -> 0x0503, all -> 0x0cfc }
            int r1 = r5.size     // Catch:{ Exception -> 0x0503, all -> 0x0cfc }
            r7.limit(r1)     // Catch:{ Exception -> 0x0503, all -> 0x0cfc }
        L_0x053f:
            int r1 = r5.size     // Catch:{ Exception -> 0x05bf, all -> 0x0cfc }
            if (r1 < 0) goto L_0x0551
            android.media.MediaExtractor r1 = r14.extractor     // Catch:{ Exception -> 0x0503, all -> 0x0cfc }
            long r3 = r1.getSampleTime()     // Catch:{ Exception -> 0x0503, all -> 0x0cfc }
            r5.presentationTimeUs = r3     // Catch:{ Exception -> 0x0503, all -> 0x0cfc }
            android.media.MediaExtractor r1 = r14.extractor     // Catch:{ Exception -> 0x0503, all -> 0x0cfc }
            r1.advance()     // Catch:{ Exception -> 0x0503, all -> 0x0cfc }
            goto L_0x0556
        L_0x0551:
            r1 = 0
            r5.size = r1     // Catch:{ Exception -> 0x05bf, all -> 0x0cfc }
            r54 = 1
        L_0x0556:
            int r1 = r5.size     // Catch:{ Exception -> 0x05bf, all -> 0x0cfc }
            if (r1 <= 0) goto L_0x05b6
            r3 = 0
            int r1 = (r11 > r3 ? 1 : (r11 == r3 ? 0 : -1))
            if (r1 < 0) goto L_0x0566
            long r3 = r5.presentationTimeUs     // Catch:{ Exception -> 0x0503, all -> 0x0cfc }
            int r1 = (r3 > r11 ? 1 : (r3 == r11 ? 0 : -1))
            if (r1 >= 0) goto L_0x05b6
        L_0x0566:
            r1 = 0
            r5.offset = r1     // Catch:{ Exception -> 0x05bf, all -> 0x0cfc }
            android.media.MediaExtractor r3 = r14.extractor     // Catch:{ Exception -> 0x05bf, all -> 0x0cfc }
            int r3 = r3.getSampleFlags()     // Catch:{ Exception -> 0x05bf, all -> 0x0cfc }
            r5.flags = r3     // Catch:{ Exception -> 0x05bf, all -> 0x0cfc }
            org.telegram.messenger.video.MP4Builder r3 = r14.mediaMuxer     // Catch:{ Exception -> 0x05bf, all -> 0x0cfc }
            r22 = r2
            r4 = r61
            long r2 = r3.writeSampleData(r4, r7, r5, r1)     // Catch:{ Exception -> 0x05bf, all -> 0x0cfc }
            r33 = 0
            int r1 = (r2 > r33 ? 1 : (r2 == r33 ? 0 : -1))
            if (r1 == 0) goto L_0x05ad
            org.telegram.messenger.MediaController$VideoConvertorListener r1 = r14.callback     // Catch:{ Exception -> 0x05bf, all -> 0x0cfc }
            if (r1 == 0) goto L_0x05ad
            r63 = r7
            r61 = r8
            long r7 = r5.presentationTimeUs     // Catch:{ Exception -> 0x05a9, all -> 0x0cfc }
            r11 = r83
            long r7 = r7 - r11
            int r1 = (r7 > r58 ? 1 : (r7 == r58 ? 0 : -1))
            if (r1 <= 0) goto L_0x0596
            long r7 = r5.presentationTimeUs     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            long r58 = r7 - r11
        L_0x0596:
            r7 = r58
            org.telegram.messenger.MediaController$VideoConvertorListener r1 = r14.callback     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            r64 = r4
            float r4 = (float) r7     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            r20 = 1148846080(0x447a0000, float:1000.0)
            float r4 = r4 / r20
            float r4 = r4 / r19
            r1.didWriteData(r2, r4)     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            r58 = r7
            goto L_0x05e3
        L_0x05a9:
            r0 = move-exception
            r11 = r83
            goto L_0x05ff
        L_0x05ad:
            r11 = r83
            r64 = r4
            r63 = r7
        L_0x05b3:
            r61 = r8
            goto L_0x05e3
        L_0x05b6:
            r11 = r83
            r22 = r2
            r63 = r7
            r64 = r61
            goto L_0x05b3
        L_0x05bf:
            r0 = move-exception
            r11 = r83
            r61 = r8
            goto L_0x05ff
        L_0x05c5:
            r11 = r83
            r63 = r7
            r5 = r22
            r64 = r61
            r22 = r2
            r61 = r8
            goto L_0x05df
        L_0x05d2:
            r11 = r83
            r63 = r7
            r5 = r22
            r64 = r61
            r22 = r2
            r61 = r8
            r1 = -1
        L_0x05df:
            if (r3 != r1) goto L_0x05e3
            r1 = 1
            goto L_0x05e4
        L_0x05e3:
            r1 = 0
        L_0x05e4:
            if (r1 == 0) goto L_0x0621
            r1 = 2500(0x9c4, double:1.235E-320)
            int r43 = r10.dequeueInputBuffer(r1)     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            if (r43 < 0) goto L_0x0621
            r44 = 0
            r45 = 0
            r46 = 0
            r48 = 4
            r42 = r10
            r42.queueInputBuffer(r43, r44, r45, r46, r48)     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            r54 = 1
            goto L_0x0621
        L_0x05fe:
            r0 = move-exception
        L_0x05ff:
            r2 = r0
            r1 = r6
        L_0x0601:
            r67 = r10
            r8 = r62
            goto L_0x0321
        L_0x0607:
            r0 = move-exception
            r11 = r83
            r61 = r8
            r2 = r0
            r8 = r1
            r1 = r6
            r67 = r10
            goto L_0x0321
        L_0x0613:
            r11 = r83
            r62 = r1
            r64 = r5
            r63 = r7
            r61 = r8
            r5 = r22
            r22 = r2
        L_0x0621:
            r1 = r55 ^ 1
            r8 = r1
            r2 = r60
            r1 = 1
        L_0x0627:
            if (r8 != 0) goto L_0x0643
            if (r1 == 0) goto L_0x062c
            goto L_0x0643
        L_0x062c:
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
            goto L_0x046b
        L_0x0643:
            r74.checkConversionCanceled()     // Catch:{ Exception -> 0x0b79, all -> 0x0cfc }
            if (r90 == 0) goto L_0x064b
            r3 = 22000(0x55f0, double:1.08694E-319)
            goto L_0x064d
        L_0x064b:
            r3 = 2500(0x9c4, double:1.235E-320)
        L_0x064d:
            int r3 = r6.dequeueOutputBuffer(r5, r3)     // Catch:{ Exception -> 0x0b79, all -> 0x0cfc }
            r4 = -1
            if (r3 != r4) goto L_0x0671
            r28 = r2
            r44 = r8
            r7 = r51
            r59 = r58
            r8 = 3
            r20 = 1148846080(0x447a0000, float:1000.0)
            r21 = 0
        L_0x0661:
            r41 = 2
            r51 = r39
            r58 = r57
            r57 = r56
            r56 = r53
            r53 = r9
            r9 = r79
            goto L_0x0855
        L_0x0671:
            r4 = -3
            if (r3 != r4) goto L_0x068d
            int r4 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            r7 = 21
            if (r4 >= r7) goto L_0x067e
            java.nio.ByteBuffer[] r39 = r6.getOutputBuffers()     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
        L_0x067e:
            r21 = r1
            r28 = r2
            r44 = r8
        L_0x0684:
            r7 = r51
            r59 = r58
            r4 = -1
            r8 = 3
            r20 = 1148846080(0x447a0000, float:1000.0)
            goto L_0x0661
        L_0x068d:
            r4 = -2
            if (r3 != r4) goto L_0x06d4
            android.media.MediaFormat r4 = r6.getOutputFormat()     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            r7 = -5
            if (r2 != r7) goto L_0x06cb
            if (r4 == 0) goto L_0x06cb
            org.telegram.messenger.video.MP4Builder r2 = r14.mediaMuxer     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            r7 = 0
            int r2 = r2.addTrack(r4, r7)     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            java.lang.String r7 = "prepend-sps-pps-to-idr-frames"
            boolean r7 = r4.containsKey(r7)     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            if (r7 == 0) goto L_0x06cb
            java.lang.String r7 = "prepend-sps-pps-to-idr-frames"
            int r7 = r4.getInteger(r7)     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            r42 = r1
            r1 = 1
            if (r7 != r1) goto L_0x06cd
            java.lang.String r1 = "csd-0"
            java.nio.ByteBuffer r1 = r4.getByteBuffer(r1)     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            java.lang.String r7 = "csd-1"
            java.nio.ByteBuffer r4 = r4.getByteBuffer(r7)     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            int r1 = r1.limit()     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            int r4 = r4.limit()     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            int r1 = r1 + r4
            r56 = r1
            goto L_0x06cd
        L_0x06cb:
            r42 = r1
        L_0x06cd:
            r28 = r2
            r44 = r8
            r21 = r42
            goto L_0x0684
        L_0x06d4:
            r42 = r1
            if (r3 < 0) goto L_0x0b4d
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b79, all -> 0x0cfc }
            r7 = 21
            if (r1 >= r7) goto L_0x06e1
            r1 = r39[r3]     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            goto L_0x06e5
        L_0x06e1:
            java.nio.ByteBuffer r1 = r6.getOutputBuffer(r3)     // Catch:{ Exception -> 0x0b79, all -> 0x0cfc }
        L_0x06e5:
            if (r1 == 0) goto L_0x0b24
            int r4 = r5.size     // Catch:{ Exception -> 0x0b79, all -> 0x0cfc }
            r7 = 1
            if (r4 <= r7) goto L_0x0828
            int r4 = r5.flags     // Catch:{ Exception -> 0x0823, all -> 0x0cfc }
            r41 = 2
            r4 = r4 & 2
            if (r4 != 0) goto L_0x07a2
            if (r56 == 0) goto L_0x0707
            int r4 = r5.flags     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            r4 = r4 & r7
            if (r4 == 0) goto L_0x0707
            int r4 = r5.offset     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            int r4 = r4 + r56
            r5.offset = r4     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            int r4 = r5.size     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            int r4 = r4 - r56
            r5.size = r4     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
        L_0x0707:
            if (r57 == 0) goto L_0x0763
            int r4 = r5.flags     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            r7 = 1
            r4 = r4 & r7
            if (r4 == 0) goto L_0x0763
            int r4 = r5.size     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            r7 = 100
            if (r4 <= r7) goto L_0x075e
            int r4 = r5.offset     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            r1.position(r4)     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            r4 = 100
            byte[] r4 = new byte[r4]     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            r1.get(r4)     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            r7 = 0
            r43 = 0
        L_0x0724:
            r44 = r8
            r8 = 96
            if (r7 >= r8) goto L_0x0760
            byte r8 = r4[r7]     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            if (r8 != 0) goto L_0x0755
            int r8 = r7 + 1
            byte r8 = r4[r8]     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            if (r8 != 0) goto L_0x0755
            int r8 = r7 + 2
            byte r8 = r4[r8]     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            if (r8 != 0) goto L_0x0755
            int r8 = r7 + 3
            byte r8 = r4[r8]     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            r45 = r4
            r4 = 1
            if (r8 != r4) goto L_0x0757
            int r8 = r43 + 1
            if (r8 <= r4) goto L_0x0752
            int r4 = r5.offset     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            int r4 = r4 + r7
            r5.offset = r4     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            int r4 = r5.size     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            int r4 = r4 - r7
            r5.size = r4     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            goto L_0x0760
        L_0x0752:
            r43 = r8
            goto L_0x0757
        L_0x0755:
            r45 = r4
        L_0x0757:
            int r7 = r7 + 1
            r8 = r44
            r4 = r45
            goto L_0x0724
        L_0x075e:
            r44 = r8
        L_0x0760:
            r57 = 0
            goto L_0x0765
        L_0x0763:
            r44 = r8
        L_0x0765:
            org.telegram.messenger.video.MP4Builder r4 = r14.mediaMuxer     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            r53 = r9
            r7 = 1
            long r8 = r4.writeSampleData(r2, r1, r5, r7)     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            r33 = 0
            int r1 = (r8 > r33 ? 1 : (r8 == r33 ? 0 : -1))
            if (r1 == 0) goto L_0x0797
            org.telegram.messenger.MediaController$VideoConvertorListener r1 = r14.callback     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            if (r1 == 0) goto L_0x0797
            r7 = r3
            long r3 = r5.presentationTimeUs     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            long r3 = r3 - r11
            int r1 = (r3 > r58 ? 1 : (r3 == r58 ? 0 : -1))
            if (r1 <= 0) goto L_0x0784
            long r3 = r5.presentationTimeUs     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            long r58 = r3 - r11
        L_0x0784:
            r3 = r58
            org.telegram.messenger.MediaController$VideoConvertorListener r1 = r14.callback     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            r43 = r7
            float r7 = (float) r3     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            r20 = 1148846080(0x447a0000, float:1000.0)
            float r7 = r7 / r20
            float r7 = r7 / r19
            r1.didWriteData(r8, r7)     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            r58 = r3
            goto L_0x079b
        L_0x0797:
            r43 = r3
            r20 = 1148846080(0x447a0000, float:1000.0)
        L_0x079b:
            r9 = r79
            r7 = r51
            r8 = 3
            goto L_0x0837
        L_0x07a2:
            r43 = r3
            r44 = r8
            r53 = r9
            r7 = -5
            r20 = 1148846080(0x447a0000, float:1000.0)
            if (r2 != r7) goto L_0x079b
            int r2 = r5.size     // Catch:{ Exception -> 0x0823, all -> 0x0cfc }
            byte[] r2 = new byte[r2]     // Catch:{ Exception -> 0x0823, all -> 0x0cfc }
            int r3 = r5.offset     // Catch:{ Exception -> 0x0823, all -> 0x0cfc }
            int r4 = r5.size     // Catch:{ Exception -> 0x0823, all -> 0x0cfc }
            int r3 = r3 + r4
            r1.limit(r3)     // Catch:{ Exception -> 0x0823, all -> 0x0cfc }
            int r3 = r5.offset     // Catch:{ Exception -> 0x0823, all -> 0x0cfc }
            r1.position(r3)     // Catch:{ Exception -> 0x0823, all -> 0x0cfc }
            r1.get(r2)     // Catch:{ Exception -> 0x0823, all -> 0x0cfc }
            int r1 = r5.size     // Catch:{ Exception -> 0x0823, all -> 0x0cfc }
            r3 = 1
            int r1 = r1 - r3
        L_0x07c5:
            r8 = 3
            if (r1 < 0) goto L_0x0803
            if (r1 <= r8) goto L_0x0803
            byte r4 = r2[r1]     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            if (r4 != r3) goto L_0x07fe
            int r3 = r1 + -1
            byte r3 = r2[r3]     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            if (r3 != 0) goto L_0x07fe
            int r3 = r1 + -2
            byte r3 = r2[r3]     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            if (r3 != 0) goto L_0x07fe
            int r3 = r1 + -3
            byte r4 = r2[r3]     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            if (r4 != 0) goto L_0x07fe
            java.nio.ByteBuffer r1 = java.nio.ByteBuffer.allocate(r3)     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            int r4 = r5.size     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            int r4 = r4 - r3
            java.nio.ByteBuffer r4 = java.nio.ByteBuffer.allocate(r4)     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            r9 = 0
            java.nio.ByteBuffer r7 = r1.put(r2, r9, r3)     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            r7.position(r9)     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            int r7 = r5.size     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            int r7 = r7 - r3
            java.nio.ByteBuffer r2 = r4.put(r2, r3, r7)     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            r2.position(r9)     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            goto L_0x0805
        L_0x07fe:
            int r1 = r1 + -1
            r3 = 1
            r7 = -5
            goto L_0x07c5
        L_0x0803:
            r1 = 0
            r4 = 0
        L_0x0805:
            r9 = r79
            r7 = r51
            android.media.MediaFormat r2 = android.media.MediaFormat.createVideoFormat(r7, r9, r15)     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            if (r1 == 0) goto L_0x081b
            if (r4 == 0) goto L_0x081b
            java.lang.String r3 = "csd-0"
            r2.setByteBuffer(r3, r1)     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            java.lang.String r1 = "csd-1"
            r2.setByteBuffer(r1, r4)     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
        L_0x081b:
            org.telegram.messenger.video.MP4Builder r1 = r14.mediaMuxer     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            r3 = 0
            int r2 = r1.addTrack(r2, r3)     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            goto L_0x0837
        L_0x0823:
            r0 = move-exception
            r9 = r79
            goto L_0x05ff
        L_0x0828:
            r43 = r3
            r44 = r8
            r53 = r9
            r7 = r51
            r8 = 3
            r20 = 1148846080(0x447a0000, float:1000.0)
            r41 = 2
            r9 = r79
        L_0x0837:
            int r1 = r5.flags     // Catch:{ Exception -> 0x0b79, all -> 0x0cfc }
            r1 = r1 & 4
            r3 = r43
            if (r1 == 0) goto L_0x0841
            r1 = 1
            goto L_0x0842
        L_0x0841:
            r1 = 0
        L_0x0842:
            r4 = 0
            r6.releaseOutputBuffer(r3, r4)     // Catch:{ Exception -> 0x0b79, all -> 0x0cfc }
            r28 = r2
            r51 = r39
            r21 = r42
            r59 = r58
            r4 = -1
            r58 = r57
            r57 = r56
            r56 = r1
        L_0x0855:
            if (r3 == r4) goto L_0x086d
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
            goto L_0x0627
        L_0x086d:
            if (r55 != 0) goto L_0x0ae8
            r1 = 2500(0x9c4, double:1.235E-320)
            int r3 = r10.dequeueOutputBuffer(r5, r1)     // Catch:{ Exception -> 0x0b79, all -> 0x0cfc }
            if (r3 != r4) goto L_0x089e
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
        L_0x088c:
            r62 = 1
            r64 = 0
            r66 = 0
            r68 = 3
            r71 = -5
            r72 = 21
            r10 = r81
            r63 = r36
            goto L_0x0afe
        L_0x089e:
            r1 = -3
            if (r3 != r1) goto L_0x08a3
            goto L_0x0ae8
        L_0x08a3:
            r1 = -2
            if (r3 != r1) goto L_0x08c4
            android.media.MediaFormat r1 = r10.getOutputFormat()     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            if (r2 == 0) goto L_0x0ae8
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            r2.<init>()     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            java.lang.String r3 = "newFormat = "
            r2.append(r3)     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            r2.append(r1)     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            java.lang.String r1 = r2.toString()     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            goto L_0x0ae8
        L_0x08c4:
            if (r3 < 0) goto L_0x0ac4
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b79, all -> 0x0cfc }
            r2 = 18
            if (r1 < r2) goto L_0x08d8
            int r1 = r5.size     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            if (r1 == 0) goto L_0x08d2
            r1 = 1
            goto L_0x08d3
        L_0x08d2:
            r1 = 0
        L_0x08d3:
            r8 = 2500(0x9c4, double:1.235E-320)
            r33 = 0
            goto L_0x08ec
        L_0x08d8:
            int r1 = r5.size     // Catch:{ Exception -> 0x0b79, all -> 0x0cfc }
            if (r1 != 0) goto L_0x08e7
            long r1 = r5.presentationTimeUs     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            r33 = 0
            int r39 = (r1 > r33 ? 1 : (r1 == r33 ? 0 : -1))
            if (r39 == 0) goto L_0x08e5
            goto L_0x08e9
        L_0x08e5:
            r1 = 0
            goto L_0x08ea
        L_0x08e7:
            r33 = 0
        L_0x08e9:
            r1 = 1
        L_0x08ea:
            r8 = 2500(0x9c4, double:1.235E-320)
        L_0x08ec:
            int r2 = (r85 > r33 ? 1 : (r85 == r33 ? 0 : -1))
            if (r2 <= 0) goto L_0x0904
            long r8 = r5.presentationTimeUs     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            int r2 = (r8 > r85 ? 1 : (r8 == r85 ? 0 : -1))
            if (r2 < 0) goto L_0x0904
            int r1 = r5.flags     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            r1 = r1 | 4
            r5.flags = r1     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            r9 = 0
            r33 = 0
            r54 = 1
            r55 = 1
            goto L_0x0907
        L_0x0904:
            r9 = r1
            r33 = 0
        L_0x0907:
            int r1 = (r11 > r33 ? 1 : (r11 == r33 ? 0 : -1))
            if (r1 <= 0) goto L_0x093f
            r1 = -1
            int r8 = (r30 > r1 ? 1 : (r30 == r1 ? 0 : -1))
            if (r8 != 0) goto L_0x093f
            long r1 = r5.presentationTimeUs     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            int r8 = (r1 > r11 ? 1 : (r1 == r11 ? 0 : -1))
            if (r8 >= 0) goto L_0x093b
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            if (r1 == 0) goto L_0x0939
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            r1.<init>()     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            java.lang.String r2 = "drop frame startTime = "
            r1.append(r2)     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            r1.append(r11)     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            java.lang.String r2 = " present time = "
            r1.append(r2)     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            long r8 = r5.presentationTimeUs     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            r1.append(r8)     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
        L_0x0939:
            r9 = 0
            goto L_0x093f
        L_0x093b:
            long r1 = r5.presentationTimeUs     // Catch:{ Exception -> 0x05fe, all -> 0x0cfc }
            r30 = r1
        L_0x093f:
            r10.releaseOutputBuffer(r3, r9)     // Catch:{ Exception -> 0x0b79, all -> 0x0cfc }
            if (r9 == 0) goto L_0x0a57
            r26.awaitNewImage()     // Catch:{ Exception -> 0x0949, all -> 0x0cfc }
            r9 = 0
            goto L_0x094f
        L_0x0949:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)     // Catch:{ Exception -> 0x0a41, all -> 0x0cfc }
            r9 = 1
        L_0x094f:
            if (r9 != 0) goto L_0x0a57
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0a41, all -> 0x0cfc }
            r8 = 18
            if (r1 < r8) goto L_0x099d
            r9 = r26
            r3 = 0
            r9.drawImage(r3)     // Catch:{ Exception -> 0x0994, all -> 0x0cfc }
            long r1 = r5.presentationTimeUs     // Catch:{ Exception -> 0x0994, all -> 0x0cfc }
            r42 = 1000(0x3e8, double:4.94E-321)
            long r1 = r1 * r42
            r8 = r49
            r8.setPresentationTime(r1)     // Catch:{ Exception -> 0x098e, all -> 0x0cfc }
            r8.swapBuffers()     // Catch:{ Exception -> 0x098e, all -> 0x0cfc }
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
            goto L_0x0a76
        L_0x098e:
            r0 = move-exception
            r2 = r0
            r1 = r6
            r49 = r8
            goto L_0x0999
        L_0x0994:
            r0 = move-exception
            r8 = r49
            r2 = r0
            r1 = r6
        L_0x0999:
            r26 = r9
            goto L_0x0601
        L_0x099d:
            r9 = r26
            r8 = r49
            r1 = 2500(0x9c4, double:1.235E-320)
            r3 = 0
            int r26 = r6.dequeueInputBuffer(r1)     // Catch:{ Exception -> 0x0a2f, all -> 0x0cfc }
            if (r26 < 0) goto L_0x0a00
            r2 = 1
            r9.drawImage(r2)     // Catch:{ Exception -> 0x0a2f, all -> 0x0cfc }
            java.nio.ByteBuffer r1 = r9.getFrame()     // Catch:{ Exception -> 0x0a2f, all -> 0x0cfc }
            r16 = r50[r26]     // Catch:{ Exception -> 0x0a2f, all -> 0x0cfc }
            r16.clear()     // Catch:{ Exception -> 0x0a2f, all -> 0x0cfc }
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
            org.telegram.messenger.Utilities.convertVideoFrame(r1, r2, r3, r4, r5, r6, r7)     // Catch:{ Exception -> 0x0a8a, all -> 0x0cfc }
            r35 = 0
            long r1 = r9.presentationTimeUs     // Catch:{ Exception -> 0x0a8a, all -> 0x0cfc }
            r39 = 0
            r33 = r32
            r34 = r26
            r36 = r27
            r37 = r1
            r33.queueInputBuffer(r34, r35, r36, r37, r39)     // Catch:{ Exception -> 0x0a8a, all -> 0x0cfc }
            goto L_0x0a7a
        L_0x0a00:
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
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0a8a, all -> 0x0cfc }
            if (r1 == 0) goto L_0x0a7a
            java.lang.String r1 = "input buffer not available"
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x0a8a, all -> 0x0cfc }
            goto L_0x0a7a
        L_0x0a2f:
            r0 = move-exception
            r32 = r6
            r49 = r8
            r69 = r9
            r67 = r10
            r8 = r62
            r62 = 1
            r66 = 0
            r10 = r81
            goto L_0x0a8b
        L_0x0a41:
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
            goto L_0x0c1e
        L_0x0a57:
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
        L_0x0a76:
            r64 = r33
            r63 = r36
        L_0x0a7a:
            int r1 = r9.flags     // Catch:{ Exception -> 0x0abf, all -> 0x0cfc }
            r1 = r1 & 4
            if (r1 == 0) goto L_0x0aba
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0abf, all -> 0x0cfc }
            if (r1 == 0) goto L_0x0a92
            java.lang.String r1 = "decoder stream end"
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x0a8a, all -> 0x0cfc }
            goto L_0x0a92
        L_0x0a8a:
            r0 = move-exception
        L_0x0a8b:
            r6 = r82
            r2 = r0
            r1 = r32
            goto L_0x0b75
        L_0x0a92:
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0abf, all -> 0x0cfc }
            if (r1 < r11) goto L_0x0a9e
            r32.signalEndOfInputStream()     // Catch:{ Exception -> 0x0a8a, all -> 0x0cfc }
            r1 = r32
            r2 = 2500(0x9c4, double:1.235E-320)
            goto L_0x0ab7
        L_0x0a9e:
            r1 = r32
            r2 = 2500(0x9c4, double:1.235E-320)
            int r43 = r1.dequeueInputBuffer(r2)     // Catch:{ Exception -> 0x0b71, all -> 0x0cfc }
            if (r43 < 0) goto L_0x0ab7
            r44 = 0
            r45 = 1
            long r4 = r9.presentationTimeUs     // Catch:{ Exception -> 0x0b71, all -> 0x0cfc }
            r48 = 4
            r42 = r1
            r46 = r4
            r42.queueInputBuffer(r43, r44, r45, r46, r48)     // Catch:{ Exception -> 0x0b71, all -> 0x0cfc }
        L_0x0ab7:
            r44 = 0
            goto L_0x0afe
        L_0x0aba:
            r1 = r32
            r2 = 2500(0x9c4, double:1.235E-320)
            goto L_0x0afe
        L_0x0abf:
            r0 = move-exception
            r1 = r32
            goto L_0x0b72
        L_0x0ac4:
            r1 = r6
            r67 = r10
            r69 = r26
            r8 = r62
            r62 = 1
            r66 = 0
            r10 = r81
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x0b71, all -> 0x0cfc }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b71, all -> 0x0cfc }
            r4.<init>()     // Catch:{ Exception -> 0x0b71, all -> 0x0cfc }
            java.lang.String r5 = "unexpected result from decoder.dequeueOutputBuffer: "
            r4.append(r5)     // Catch:{ Exception -> 0x0b71, all -> 0x0cfc }
            r4.append(r3)     // Catch:{ Exception -> 0x0b71, all -> 0x0cfc }
            java.lang.String r3 = r4.toString()     // Catch:{ Exception -> 0x0b71, all -> 0x0cfc }
            r2.<init>(r3)     // Catch:{ Exception -> 0x0b71, all -> 0x0cfc }
            throw r2     // Catch:{ Exception -> 0x0b71, all -> 0x0cfc }
        L_0x0ae8:
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
            goto L_0x088c
        L_0x0afe:
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
            goto L_0x0627
        L_0x0b24:
            r1 = r6
            r67 = r10
            r69 = r26
            r8 = r62
            r62 = 1
            r66 = 0
            r10 = r81
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x0b71, all -> 0x0cfc }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b71, all -> 0x0cfc }
            r4.<init>()     // Catch:{ Exception -> 0x0b71, all -> 0x0cfc }
            java.lang.String r5 = "encoderOutputBuffer "
            r4.append(r5)     // Catch:{ Exception -> 0x0b71, all -> 0x0cfc }
            r4.append(r3)     // Catch:{ Exception -> 0x0b71, all -> 0x0cfc }
            java.lang.String r3 = " was null"
            r4.append(r3)     // Catch:{ Exception -> 0x0b71, all -> 0x0cfc }
            java.lang.String r3 = r4.toString()     // Catch:{ Exception -> 0x0b71, all -> 0x0cfc }
            r2.<init>(r3)     // Catch:{ Exception -> 0x0b71, all -> 0x0cfc }
            throw r2     // Catch:{ Exception -> 0x0b71, all -> 0x0cfc }
        L_0x0b4d:
            r1 = r6
            r67 = r10
            r69 = r26
            r8 = r62
            r62 = 1
            r66 = 0
            r10 = r81
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x0b71, all -> 0x0cfc }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b71, all -> 0x0cfc }
            r4.<init>()     // Catch:{ Exception -> 0x0b71, all -> 0x0cfc }
            java.lang.String r5 = "unexpected result from encoder.dequeueOutputBuffer: "
            r4.append(r5)     // Catch:{ Exception -> 0x0b71, all -> 0x0cfc }
            r4.append(r3)     // Catch:{ Exception -> 0x0b71, all -> 0x0cfc }
            java.lang.String r3 = r4.toString()     // Catch:{ Exception -> 0x0b71, all -> 0x0cfc }
            r2.<init>(r3)     // Catch:{ Exception -> 0x0b71, all -> 0x0cfc }
            throw r2     // Catch:{ Exception -> 0x0b71, all -> 0x0cfc }
        L_0x0b71:
            r0 = move-exception
        L_0x0b72:
            r6 = r82
            r2 = r0
        L_0x0b75:
            r26 = r69
            goto L_0x0c1e
        L_0x0b79:
            r0 = move-exception
            r1 = r6
            r67 = r10
            r69 = r26
            r8 = r62
            r62 = 1
            r66 = 0
            r10 = r81
            goto L_0x0b97
        L_0x0b88:
            r0 = move-exception
            r61 = r8
            r67 = r10
            r69 = r26
            r62 = 1
            r66 = 0
            r10 = r81
            r8 = r1
            r1 = r6
        L_0x0b97:
            r6 = r82
            r2 = r0
            goto L_0x0c1e
        L_0x0b9c:
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
            goto L_0x0bd4
        L_0x0bad:
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
            goto L_0x0bd4
        L_0x0bc2:
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
        L_0x0bd4:
            r61 = 0
            goto L_0x0c1e
        L_0x0bd7:
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
            goto L_0x0c1a
        L_0x0be8:
            r0 = move-exception
            r10 = r81
            r8 = r1
            r1 = r6
            r62 = 1
            r66 = 0
            r6 = r82
            r2 = r0
            goto L_0x0CLASSNAME
        L_0x0bf5:
            r0 = move-exception
            r10 = r81
            r8 = r1
            r82 = r9
            goto L_0x0c0e
        L_0x0bfc:
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
        L_0x0c0a:
            r0 = move-exception
            r10 = r81
            r8 = r1
        L_0x0c0e:
            r62 = 1
            r66 = 0
            r6 = r82
        L_0x0CLASSNAME:
            r2 = r0
            r1 = 0
        L_0x0CLASSNAME:
            r26 = 0
            r49 = 0
        L_0x0c1a:
            r61 = 0
        L_0x0c1c:
            r67 = 0
        L_0x0c1e:
            boolean r3 = r2 instanceof java.lang.IllegalStateException     // Catch:{ Exception -> 0x0cac, all -> 0x0cfc }
            if (r3 == 0) goto L_0x0CLASSNAME
            if (r90 != 0) goto L_0x0CLASSNAME
            r9 = 1
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r9 = 0
        L_0x0CLASSNAME:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0c9e, all -> 0x0cfc }
            r3.<init>()     // Catch:{ Exception -> 0x0c9e, all -> 0x0cfc }
            r4 = r17
            r3.append(r4)     // Catch:{ Exception -> 0x0c9a, all -> 0x0cfc }
            r3.append(r6)     // Catch:{ Exception -> 0x0c9a, all -> 0x0cfc }
            r5 = r18
            r3.append(r5)     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0cfc }
            r3.append(r10)     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0cfc }
            r7 = r23
            r3.append(r7)     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0cfc }
            r3.append(r15)     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0cfc }
            r11 = r24
            r3.append(r11)     // Catch:{ Exception -> 0x0c8e, all -> 0x0cfc }
            r12 = r79
            r3.append(r12)     // Catch:{ Exception -> 0x0c8c, all -> 0x0cfc }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x0c8c, all -> 0x0cfc }
            org.telegram.messenger.FileLog.e((java.lang.String) r3)     // Catch:{ Exception -> 0x0c8c, all -> 0x0cfc }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)     // Catch:{ Exception -> 0x0c8c, all -> 0x0cfc }
            r35 = r9
            r9 = r49
            r2 = 1
        L_0x0c5d:
            android.media.MediaExtractor r3 = r14.extractor     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0cfc }
            r3.unselectTrack(r8)     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0cfc }
            if (r26 == 0) goto L_0x0CLASSNAME
            r26.release()     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0cfc }
        L_0x0CLASSNAME:
            if (r9 == 0) goto L_0x0c6c
            r9.release()     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0cfc }
        L_0x0c6c:
            if (r67 == 0) goto L_0x0CLASSNAME
            r67.stop()     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0cfc }
            r67.release()     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0cfc }
        L_0x0CLASSNAME:
            if (r1 == 0) goto L_0x0c7c
            r1.stop()     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0cfc }
            r1.release()     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0cfc }
        L_0x0c7c:
            if (r61 == 0) goto L_0x0CLASSNAME
            r61.release()     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0cfc }
        L_0x0CLASSNAME:
            r74.checkConversionCanceled()     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0cfc }
            r9 = r2
            goto L_0x0cc2
        L_0x0CLASSNAME:
            r0 = move-exception
            r1 = r0
            r9 = r35
            goto L_0x0d11
        L_0x0c8c:
            r0 = move-exception
            goto L_0x0ca9
        L_0x0c8e:
            r0 = move-exception
            r12 = r79
            goto L_0x0ca9
        L_0x0CLASSNAME:
            r0 = move-exception
            r12 = r79
            goto L_0x0ca7
        L_0x0CLASSNAME:
            r0 = move-exception
            r12 = r79
            goto L_0x0ca5
        L_0x0c9a:
            r0 = move-exception
            r12 = r79
            goto L_0x0ca3
        L_0x0c9e:
            r0 = move-exception
            r12 = r79
            r4 = r17
        L_0x0ca3:
            r5 = r18
        L_0x0ca5:
            r7 = r23
        L_0x0ca7:
            r11 = r24
        L_0x0ca9:
            r1 = r0
            goto L_0x0d11
        L_0x0cac:
            r0 = move-exception
            r12 = r79
            r4 = r17
            r5 = r18
            r7 = r23
            r11 = r24
            goto L_0x0d0f
        L_0x0cb9:
            r10 = r81
            r12 = r13
            r66 = 0
            r6 = r82
            goto L_0x00c9
        L_0x0cc2:
            android.media.MediaExtractor r1 = r14.extractor
            if (r1 == 0) goto L_0x0cc9
            r1.release()
        L_0x0cc9:
            org.telegram.messenger.video.MP4Builder r1 = r14.mediaMuxer
            if (r1 == 0) goto L_0x0cd6
            r1.finishMovie()     // Catch:{ Exception -> 0x0cd1 }
            goto L_0x0cd6
        L_0x0cd1:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x0cd6:
            r11 = r6
            goto L_0x0d50
        L_0x0cd9:
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
            goto L_0x0d0d
        L_0x0cea:
            r0 = move-exception
            r12 = r79
            goto L_0x0cef
        L_0x0cee:
            r0 = move-exception
        L_0x0cef:
            r10 = r81
            r5 = r2
            r7 = r3
            r15 = r11
            r62 = 1
            r66 = 0
            r11 = r4
            r4 = r18
            goto L_0x0d0d
        L_0x0cfc:
            r0 = move-exception
            r2 = r0
            r1 = r14
            goto L_0x0d77
        L_0x0d01:
            r0 = move-exception
            r10 = r81
            r5 = r2
            r7 = r3
            r15 = r11
            r62 = 1
            r66 = 0
            r11 = r4
            r4 = r1
        L_0x0d0d:
            r6 = r82
        L_0x0d0f:
            r1 = r0
            r9 = 0
        L_0x0d11:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x0d73 }
            r2.<init>()     // Catch:{ all -> 0x0d73 }
            r2.append(r4)     // Catch:{ all -> 0x0d73 }
            r2.append(r6)     // Catch:{ all -> 0x0d73 }
            r2.append(r5)     // Catch:{ all -> 0x0d73 }
            r2.append(r10)     // Catch:{ all -> 0x0d73 }
            r2.append(r7)     // Catch:{ all -> 0x0d73 }
            r2.append(r15)     // Catch:{ all -> 0x0d73 }
            r2.append(r11)     // Catch:{ all -> 0x0d73 }
            r2.append(r12)     // Catch:{ all -> 0x0d73 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x0d73 }
            org.telegram.messenger.FileLog.e((java.lang.String) r2)     // Catch:{ all -> 0x0d73 }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)     // Catch:{ all -> 0x0d73 }
            android.media.MediaExtractor r1 = r14.extractor
            if (r1 == 0) goto L_0x0d3f
            r1.release()
        L_0x0d3f:
            org.telegram.messenger.video.MP4Builder r1 = r14.mediaMuxer
            if (r1 == 0) goto L_0x0d4c
            r1.finishMovie()     // Catch:{ Exception -> 0x0d47 }
            goto L_0x0d4c
        L_0x0d47:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x0d4c:
            r11 = r6
            r35 = r9
            r9 = 1
        L_0x0d50:
            if (r35 == 0) goto L_0x0d72
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
        L_0x0d72:
            return r9
        L_0x0d73:
            r0 = move-exception
            r1 = r74
            r2 = r0
        L_0x0d77:
            android.media.MediaExtractor r3 = r1.extractor
            if (r3 == 0) goto L_0x0d7e
            r3.release()
        L_0x0d7e:
            org.telegram.messenger.video.MP4Builder r3 = r1.mediaMuxer
            if (r3 == 0) goto L_0x0d8b
            r3.finishMovie()     // Catch:{ Exception -> 0x0d86 }
            goto L_0x0d8b
        L_0x0d86:
            r0 = move-exception
            r3 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)
        L_0x0d8b:
            goto L_0x0d8d
        L_0x0d8c:
            throw r2
        L_0x0d8d:
            goto L_0x0d8c
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
