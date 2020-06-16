package org.telegram.messenger.video;

import android.media.MediaExtractor;
import java.io.File;
import java.util.ArrayList;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.VideoEditedInfo;

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

    public boolean convertVideo(String str, File file, int i, boolean z, int i2, int i3, int i4, int i5, long j, long j2, boolean z2, long j3, MediaController.SavedFilterState savedFilterState, String str2, ArrayList<VideoEditedInfo.MediaEntity> arrayList, boolean z3, MediaController.CropState cropState, MediaController.VideoConvertorListener videoConvertorListener) {
        String str3 = str;
        long j4 = j3;
        this.callback = videoConvertorListener;
        return convertVideoInternal(str, file, i, z, i2, i3, i4, i5, j, j2, j4, z2, false, savedFilterState, str2, arrayList, z3, cropState);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v0, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v1, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v2, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v28, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v3, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v4, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v5, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v6, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v7, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v8, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v9, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v10, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v11, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v12, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r67v13, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v49, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v52, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v8, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v173, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v9, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v174, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v10, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v177, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v124, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v11, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v99, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v12, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v100, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v185, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v105, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v106, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v13, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v88, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v131, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v189, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v132, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v91, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v95, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v97, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v100, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v101, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v117, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v120, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v222, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v223, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v175, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v224, resolved type: boolean} */
    /* JADX WARNING: type inference failed for: r3v46 */
    /* JADX WARNING: type inference failed for: r8v84 */
    /* JADX WARNING: type inference failed for: r11v102, types: [int] */
    /* JADX WARNING: type inference failed for: r8v90 */
    /* JADX WARNING: type inference failed for: r8v94 */
    /* JADX WARNING: Code restructure failed: missing block: B:123:0x02ab, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:176:0x035d, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:180:0x036f, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:181:0x0370, code lost:
        r21 = r6;
        r6 = r80;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:182:0x0374, code lost:
        r1 = r0;
        r9 = r21;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0092, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:211:0x0403, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:212:0x0405, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:213:0x0406, code lost:
        r2 = r51;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:215:0x040d, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:216:0x040e, code lost:
        r21 = r6;
        r2 = r51;
        r6 = r80;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:217:0x0414, code lost:
        r1 = r0;
        r11 = r2;
        r9 = r21;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0093, code lost:
        r6 = r80;
        r36 = "bitrate: ";
        r50 = r9;
        r9 = null;
        r33 = null;
        r47 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:238:0x0493, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:239:0x0494, code lost:
        r1 = r0;
        r47 = r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:240:0x0499, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:241:0x049a, code lost:
        r51 = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:242:0x049c, code lost:
        r13 = r47;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:246:0x04c3, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:247:0x04c4, code lost:
        r51 = r2;
        r9 = r21;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:248:0x04c9, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:249:0x04ca, code lost:
        r9 = r6;
        r13 = r47;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:254:0x04f0, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:258:0x050e, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:259:0x050f, code lost:
        r47 = r1;
        r6 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:264:0x052f, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:265:0x0530, code lost:
        r9 = r76;
        r6 = r80;
        r1 = r47;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:267:0x053a, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:268:0x053b, code lost:
        r9 = r76;
        r6 = r80;
        r1 = r47;
        r11 = r51;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:269:0x0544, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:270:0x0545, code lost:
        r50 = r9;
        r51 = r11;
        r36 = r29;
        r9 = r1;
        r1 = r6;
        r6 = r80;
        r47 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:271:0x0551, code lost:
        r33 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:272:0x0555, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:273:0x0556, code lost:
        r6 = r80;
        r50 = r9;
        r51 = r11;
        r36 = r29;
        r9 = r1;
        r1 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:274:0x0561, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:275:0x0562, code lost:
        r6 = r80;
        r50 = r9;
        r51 = r11;
        r36 = r29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:276:0x056b, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:277:0x056c, code lost:
        r6 = r80;
        r36 = "bitrate: ";
        r50 = r9;
        r51 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:278:0x0575, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:279:0x0576, code lost:
        r6 = r80;
        r36 = "bitrate: ";
        r50 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:280:0x057c, code lost:
        r1 = r0;
        r9 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:281:0x057e, code lost:
        r33 = null;
        r47 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:303:0x05cb, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:304:0x05cc, code lost:
        r1 = r0;
        r8 = r10;
        r5 = r11;
        r4 = r13;
        r69 = r6;
        r6 = r2;
        r2 = r3;
        r7 = r12;
        r11 = r69;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:311:0x05f4, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:312:0x05f5, code lost:
        r1 = r0;
        r2 = r3;
        r8 = r10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:313:0x05f9, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:314:0x05fb, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:315:0x05fc, code lost:
        r3 = r50;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:316:0x05fe, code lost:
        r8 = r78;
        r1 = r0;
        r2 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:317:0x0602, code lost:
        r5 = r11;
        r4 = r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:318:0x0605, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:319:0x0606, code lost:
        r8 = r78;
        r1 = r0;
        r2 = r50;
        r5 = r11;
        r4 = r36;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:320:0x060f, code lost:
        r7 = r12;
        r11 = r6;
        r6 = r42;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:321:0x0618, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:322:0x0619, code lost:
        r8 = r78;
        r1 = r0;
        r2 = r50;
        r5 = r11;
        r4 = r36;
        r7 = r12;
        r11 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:334:0x0657, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:335:0x0658, code lost:
        r1 = r0;
        r2 = r9;
        r8 = r10;
        r5 = r11;
        r7 = r12;
        r4 = r13;
        r6 = false;
        r11 = r80;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:356:0x06b0, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:357:0x06b2, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:358:0x06b3, code lost:
        r15 = r2;
        r36 = r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:359:0x06b7, code lost:
        r7 = r76;
        r5 = r77;
        r8 = r78;
        r2 = r79;
        r11 = r80;
        r1 = r0;
        r4 = r36;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:370:0x06ea, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:371:0x06eb, code lost:
        r2 = r79;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:372:0x06ed, code lost:
        r1 = r0;
        r65 = r6;
        r11 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:387:0x0724, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:403:0x076c, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:404:0x076d, code lost:
        r1 = r0;
        r11 = r9;
        r65 = r21;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:427:0x07f5, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:428:0x07f6, code lost:
        r6 = r79;
        r11 = r80;
        r1 = r0;
        r10 = r2;
        r43 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:455:0x0879, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:464:0x0899, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:475:0x08b6, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:476:0x08b7, code lost:
        r8 = r82;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:477:0x08bb, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:478:0x08bc, code lost:
        r8 = r82;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:481:0x08d1, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:482:0x08d2, code lost:
        r8 = r82;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:483:0x08d4, code lost:
        r6 = r79;
        r11 = r80;
        r1 = r0;
        r10 = r2;
        r2 = r23;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:524:0x099c, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:525:0x099d, code lost:
        r6 = r79;
        r11 = r80;
        r1 = r0;
        r10 = r2;
        r65 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:564:0x0a62, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:565:0x0a63, code lost:
        r6 = r79;
        r1 = r0;
        r10 = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:568:0x0a6c, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:569:0x0a6d, code lost:
        r43 = r7;
        r6 = r79;
        r1 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x017e, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:0x017f, code lost:
        r9 = r76;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:58:0x0181, code lost:
        r6 = r80;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:595:0x0afc, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:596:0x0afd, code lost:
        r1 = r0;
        r10 = r2;
        r6 = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:0x0183, code lost:
        r1 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:60:0x0184, code lost:
        r11 = r51;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:637:0x0ba6, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:638:0x0ba7, code lost:
        r1 = r0;
        r10 = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:697:0x0ca8, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:698:0x0caa, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:699:0x0cab, code lost:
        r60 = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:69:0x01a9, code lost:
        r6 = r8;
        r7 = r9;
        r8 = r10;
        r1 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:700:0x0cad, code lost:
        r1 = r0;
        r6 = r5;
        r11 = r12;
        r2 = r23;
        r10 = r60;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:728:0x0d2c, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:729:0x0d2d, code lost:
        r1 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:730:0x0d2e, code lost:
        r6 = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:744:0x0d54, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:745:0x0d55, code lost:
        r11 = r80;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:761:0x0d98, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:769:0x0da6, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:772:?, code lost:
        org.telegram.messenger.FileLog.e((java.lang.Throwable) r0);
        r1 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:785:0x0dd6, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:789:0x0df2, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:790:0x0df3, code lost:
        r1 = r0;
        r63 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:791:0x0df7, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:792:0x0df8, code lost:
        r11 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:793:0x0df9, code lost:
        r3 = r63;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:794:0x0dfb, code lost:
        r1 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:795:0x0dfc, code lost:
        r6 = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:799:0x0e1e, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:800:0x0e1f, code lost:
        r11 = r12;
        r10 = r60;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:801:0x0e23, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:802:0x0e24, code lost:
        r10 = r2;
        r65 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:809:0x0e67, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:810:0x0e68, code lost:
        r10 = r2;
        r63 = r63;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:811:0x0e6c, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:812:0x0e6e, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:813:0x0e6f, code lost:
        r5 = r79;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:814:0x0e71, code lost:
        r1 = r2;
        r65 = r7;
        r11 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:815:0x0e76, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:816:0x0e77, code lost:
        r5 = r79;
        r1 = r2;
        r43 = r7;
        r11 = r10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:817:0x0e7d, code lost:
        r3 = r63;
        r10 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:818:0x0e80, code lost:
        r6 = r5;
        r2 = r23;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:819:0x0e84, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:820:0x0e85, code lost:
        r5 = r79;
        r11 = r80;
        r1 = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:821:0x0e8b, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:822:0x0e8c, code lost:
        r5 = r79;
        r11 = r80;
        r1 = r60;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:823:0x0e92, code lost:
        r3 = r63;
        r10 = r1;
        r6 = r5;
        r2 = r23;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:824:0x0e99, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:825:0x0e9a, code lost:
        r5 = r79;
        r11 = r80;
        r3 = r63;
        r10 = r60;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:826:0x0ea4, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:827:0x0ea5, code lost:
        r5 = r8;
        r11 = r9;
        r65 = r21;
        r10 = r2;
        r63 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:828:0x0eae, code lost:
        r6 = r5;
        r2 = r23;
        r24 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:829:0x0eb3, code lost:
        r43 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:830:0x0eb5, code lost:
        r1 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:831:0x0eb7, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:832:0x0eb8, code lost:
        r11 = r9;
        r65 = r21;
        r1 = r0;
        r63 = r4;
        r6 = r8;
        r2 = r23;
        r10 = null;
        r24 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:833:0x0ec6, code lost:
        r43 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:834:0x0ec9, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:835:0x0eca, code lost:
        r11 = r9;
        r65 = r21;
        r1 = r0;
        r6 = r8;
        r2 = r23;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:836:0x0ed3, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:837:0x0ed4, code lost:
        r11 = r9;
        r65 = r21;
        r1 = r0;
        r2 = r23;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:838:0x0edb, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:839:0x0edc, code lost:
        r11 = r9;
        r65 = r21;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:840:0x0ee0, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:841:0x0ee1, code lost:
        r65 = r6;
        r11 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:842:0x0ee5, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:843:0x0ee6, code lost:
        r65 = r6;
        r11 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:844:0x0eea, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:845:0x0eeb, code lost:
        r11 = r80;
        r65 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:846:0x0eef, code lost:
        r2 = r79;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:847:0x0ef1, code lost:
        r1 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:848:0x0ef2, code lost:
        r6 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:849:0x0ef3, code lost:
        r10 = null;
        r24 = null;
        r43 = null;
        r63 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:854:0x0var_, code lost:
        r30 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:876:0x0var_, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:877:0x0var_, code lost:
        r1 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:878:0x0var_, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:879:0x0f5a, code lost:
        r7 = r76;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:880:0x0f5d, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:881:0x0f5e, code lost:
        r7 = r76;
        r5 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:882:0x0var_, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:883:0x0var_, code lost:
        r7 = r76;
        r5 = r77;
        r8 = r78;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:884:0x0f6b, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:885:0x0f6c, code lost:
        r7 = r76;
        r5 = r77;
        r8 = r78;
        r4 = r36;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:886:0x0var_, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:887:0x0var_, code lost:
        r7 = r76;
        r5 = r77;
        r8 = r78;
        r4 = r36;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:892:0x0f9c, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:893:0x0f9d, code lost:
        r1 = r0;
        r6 = r30;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:913:0x0fd8, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:914:0x0fd9, code lost:
        r8 = r10;
        r5 = r11;
        r7 = r12;
        r4 = r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:915:0x0fde, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:916:0x0fdf, code lost:
        r8 = r10;
        r5 = r11;
        r4 = r13;
        r69 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:917:0x0fe5, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:918:0x0fe6, code lost:
        r4 = "bitrate: ";
        r8 = r10;
        r5 = r11;
        r7 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:919:0x0fea, code lost:
        r11 = r80;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:920:0x0fed, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:921:0x0fee, code lost:
        r2 = r0;
        r1 = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:922:0x0ff2, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:923:0x0ff3, code lost:
        r4 = "bitrate: ";
        r5 = r11;
        r69 = r7;
        r8 = r10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:924:0x0ff8, code lost:
        r7 = r12;
        r11 = r69;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:925:0x0ffb, code lost:
        r2 = r79;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:926:0x0ffd, code lost:
        r1 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:927:0x0ffe, code lost:
        r6 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:929:?, code lost:
        org.telegram.messenger.FileLog.e(r4 + r2 + " framerate: " + r8 + " size: " + r5 + "x" + r7);
        org.telegram.messenger.FileLog.e((java.lang.Throwable) r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:930:0x102c, code lost:
        r1 = r14.extractor;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:931:0x102e, code lost:
        if (r1 != null) goto L_0x1030;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:932:0x1030, code lost:
        r1.release();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:933:0x1033, code lost:
        r1 = r14.mediaMuxer;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:934:0x1035, code lost:
        if (r1 != null) goto L_0x1037;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:936:?, code lost:
        r1.finishMovie();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:937:0x103b, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:938:0x103c, code lost:
        org.telegram.messenger.FileLog.e((java.lang.Throwable) r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:939:0x1040, code lost:
        r9 = r2;
        r13 = r6;
        r6 = r7;
        r7 = r5;
        r5 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:944:0x106d, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:945:0x106e, code lost:
        r1 = r71;
        r2 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:946:0x1071, code lost:
        r3 = r1.extractor;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:947:0x1073, code lost:
        if (r3 != null) goto L_0x1075;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:948:0x1075, code lost:
        r3.release();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:949:0x1078, code lost:
        r3 = r1.mediaMuxer;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:950:0x107a, code lost:
        if (r3 != null) goto L_0x107c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:952:?, code lost:
        r3.finishMovie();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:953:0x1080, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:954:0x1081, code lost:
        org.telegram.messenger.FileLog.e((java.lang.Throwable) r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:955:0x1086, code lost:
        throw r2;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:228:0x0444 A[Catch:{ Exception -> 0x0499, all -> 0x0fed }] */
    /* JADX WARNING: Removed duplicated region for block: B:229:0x045e A[Catch:{ Exception -> 0x0499, all -> 0x0fed }] */
    /* JADX WARNING: Removed duplicated region for block: B:285:0x0586 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:301:0x05c7 A[SYNTHETIC, Splitter:B:301:0x05c7] */
    /* JADX WARNING: Removed duplicated region for block: B:306:0x05db A[Catch:{ Exception -> 0x05cb, all -> 0x0fed }] */
    /* JADX WARNING: Removed duplicated region for block: B:308:0x05e0 A[Catch:{ Exception -> 0x05cb, all -> 0x0fed }] */
    /* JADX WARNING: Removed duplicated region for block: B:362:0x06cc  */
    /* JADX WARNING: Removed duplicated region for block: B:444:0x0838  */
    /* JADX WARNING: Removed duplicated region for block: B:446:0x083b  */
    /* JADX WARNING: Removed duplicated region for block: B:480:0x08c6  */
    /* JADX WARNING: Removed duplicated region for block: B:488:0x08ec  */
    /* JADX WARNING: Removed duplicated region for block: B:489:0x08ee  */
    /* JADX WARNING: Removed duplicated region for block: B:493:0x0904 A[Catch:{ Exception -> 0x0e76, all -> 0x0fed }] */
    /* JADX WARNING: Removed duplicated region for block: B:505:0x0936  */
    /* JADX WARNING: Removed duplicated region for block: B:572:0x0a76  */
    /* JADX WARNING: Removed duplicated region for block: B:575:0x0a91 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:580:0x0aae  */
    /* JADX WARNING: Removed duplicated region for block: B:581:0x0ab3  */
    /* JADX WARNING: Removed duplicated region for block: B:586:0x0abe  */
    /* JADX WARNING: Removed duplicated region for block: B:587:0x0ad6  */
    /* JADX WARNING: Removed duplicated region for block: B:705:0x0cc0 A[Catch:{ Exception -> 0x0e1e, all -> 0x0fed }] */
    /* JADX WARNING: Removed duplicated region for block: B:706:0x0cc4 A[Catch:{ Exception -> 0x0e1e, all -> 0x0fed }] */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0cd1  */
    /* JADX WARNING: Removed duplicated region for block: B:712:0x0ceb  */
    /* JADX WARNING: Removed duplicated region for block: B:751:0x0d67  */
    /* JADX WARNING: Removed duplicated region for block: B:762:0x0d9a  */
    /* JADX WARNING: Removed duplicated region for block: B:766:0x0da1 A[SYNTHETIC, Splitter:B:766:0x0da1] */
    /* JADX WARNING: Removed duplicated region for block: B:781:0x0dc8 A[Catch:{ Exception -> 0x0df2, all -> 0x0fed }] */
    /* JADX WARNING: Removed duplicated region for block: B:853:0x0efe A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:874:0x0var_ A[Catch:{ Exception -> 0x0var_, all -> 0x0fed }] */
    /* JADX WARNING: Removed duplicated region for block: B:888:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:890:0x0var_ A[SYNTHETIC, Splitter:B:890:0x0var_] */
    /* JADX WARNING: Removed duplicated region for block: B:895:0x0fa4 A[Catch:{ Exception -> 0x0f9c, all -> 0x0fed }] */
    /* JADX WARNING: Removed duplicated region for block: B:897:0x0fa9 A[Catch:{ Exception -> 0x0f9c, all -> 0x0fed }] */
    /* JADX WARNING: Removed duplicated region for block: B:899:0x0fb1 A[Catch:{ Exception -> 0x0f9c, all -> 0x0fed }] */
    /* JADX WARNING: Removed duplicated region for block: B:905:0x0fbf  */
    /* JADX WARNING: Removed duplicated region for block: B:908:0x0fc6 A[SYNTHETIC, Splitter:B:908:0x0fc6] */
    /* JADX WARNING: Removed duplicated region for block: B:920:0x0fed A[ExcHandler: all (r0v7 'th' java.lang.Throwable A[CUSTOM_DECLARE]), PHI: r14 
      PHI: (r14v4 org.telegram.messenger.video.MediaCodecVideoConvertor) = (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v7 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v8 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v8 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v8 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v8 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v8 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v8 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v8 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v8 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v8 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v8 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v15 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v18 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v18 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v18 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v18 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v18 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v18 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v18 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v18 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v18 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v18 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v18 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v18 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v18 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v18 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v18 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v18 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v18 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v18 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v18 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v18 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v22 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor) binds: [B:1:0x0018, B:2:?, B:4:0x0028, B:5:?, B:324:0x0638, B:325:?, B:327:0x0647, B:890:0x0var_, B:363:0x06ce, B:871:0x0f3f, B:850:0x0efa, B:851:?, B:856:0x0var_, B:857:?, B:859:0x0f0c, B:860:?, B:862:0x0var_, B:863:?, B:865:0x0var_, B:866:?, B:868:0x0f2d, B:869:?, B:389:0x072c, B:392:0x0739, B:393:?, B:406:0x0775, B:409:0x077b, B:410:?, B:411:0x0787, B:413:0x0795, B:414:?, B:416:0x07d5, B:417:?, B:418:0x07d8, B:421:0x07e0, B:422:?, B:490:0x08ef, B:577:0x0aa9, B:578:?, B:582:0x0ab7, B:714:0x0cf3, B:732:0x0d33, B:733:?, B:763:0x0d9c, B:764:?, B:766:0x0da1, B:776:0x0db9, B:771:0x0da8, B:767:?, B:752:0x0d69, B:739:0x0d40, B:724:0x0d0f, B:615:0x0b74, B:616:?, B:804:0x0e2e, B:624:0x0b85, B:625:?, B:702:0x0cba, B:628:0x0b8c, B:629:?, B:661:0x0bf9, B:662:?, B:664:0x0bfe, B:633:0x0b94, B:621:0x0b7f, B:619:0x0b7c, B:620:?, B:589:0x0ad9, B:506:0x0938, B:551:0x0a1b, B:511:0x0946, B:500:0x0925, B:433:0x0808, B:441:0x082c, B:457:0x0881, B:458:?, B:469:0x08a4, B:470:?, B:472:0x08a8, B:466:0x089c, B:462:0x0895, B:463:?, B:447:0x083d, B:451:0x085c, B:436:0x081c, B:425:0x07ec, B:396:0x0756, B:383:0x0714, B:373:0x06f3, B:374:?, B:367:0x06e1, B:368:?, B:346:0x0680, B:347:?, B:353:0x06a0, B:354:?, B:339:0x0668, B:331:0x064e, B:10:0x0058, B:301:0x05c7, B:282:0x0582, B:283:?, B:288:0x058d, B:289:?, B:291:0x0594, B:292:?, B:294:0x0599, B:295:?, B:297:0x05a3, B:298:?, B:11:?, B:23:0x00a1, B:24:?, B:32:0x00d8, B:33:?, B:38:0x0102, B:41:0x010d, B:44:0x012a, B:45:?, B:46:0x0136, B:47:?, B:49:0x0172, B:50:?, B:51:0x0175, B:52:?, B:62:0x0189, B:232:0x0477, B:117:0x02a0, B:118:?, B:251:0x04d4, B:127:0x02b4, B:128:?, B:219:0x0427, B:220:?, B:225:0x0435, B:131:0x02bb, B:132:?, B:185:0x0384, B:186:?, B:204:0x03e8, B:191:0x03a1, B:165:0x032f, B:173:0x034e, B:136:0x02c3, B:124:0x02ae, B:121:0x02a8, B:122:?, B:102:0x023b, B:54:0x0179, B:35:0x00dc, B:36:?, B:26:0x00a5, B:14:0x005e] A[DONT_GENERATE, DONT_INLINE], Splitter:B:1:0x0018] */
    /* JADX WARNING: Removed duplicated region for block: B:932:0x1030  */
    /* JADX WARNING: Removed duplicated region for block: B:935:0x1037 A[SYNTHETIC, Splitter:B:935:0x1037] */
    /* JADX WARNING: Removed duplicated region for block: B:941:0x1047  */
    /* JADX WARNING: Removed duplicated region for block: B:943:0x106c A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:948:0x1075  */
    /* JADX WARNING: Removed duplicated region for block: B:951:0x107c A[SYNTHETIC, Splitter:B:951:0x107c] */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:282:0x0582=Splitter:B:282:0x0582, B:856:0x0var_=Splitter:B:856:0x0var_, B:219:0x0427=Splitter:B:219:0x0427, B:62:0x0189=Splitter:B:62:0x0189, B:577:0x0aa9=Splitter:B:577:0x0aa9, B:389:0x072c=Splitter:B:389:0x072c, B:441:0x082c=Splitter:B:441:0x082c, B:288:0x058d=Splitter:B:288:0x058d, B:165:0x032f=Splitter:B:165:0x032f, B:490:0x08ef=Splitter:B:490:0x08ef, B:225:0x0435=Splitter:B:225:0x0435, B:582:0x0ab7=Splitter:B:582:0x0ab7, B:10:0x0058=Splitter:B:10:0x0058, B:32:0x00d8=Splitter:B:32:0x00d8, B:661:0x0bf9=Splitter:B:661:0x0bf9, B:702:0x0cba=Splitter:B:702:0x0cba, B:850:0x0efa=Splitter:B:850:0x0efa, B:763:0x0d9c=Splitter:B:763:0x0d9c, B:871:0x0f3f=Splitter:B:871:0x0f3f} */
    /* JADX WARNING: Unknown variable types count: 1 */
    @android.annotation.TargetApi(18)
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean convertVideoInternal(java.lang.String r72, java.io.File r73, int r74, boolean r75, int r76, int r77, int r78, int r79, long r80, long r82, long r84, boolean r86, boolean r87, org.telegram.messenger.MediaController.SavedFilterState r88, java.lang.String r89, java.util.ArrayList<org.telegram.messenger.VideoEditedInfo.MediaEntity> r90, boolean r91, org.telegram.messenger.MediaController.CropState r92) {
        /*
            r71 = this;
            r14 = r71
            r13 = r72
            r15 = r74
            r12 = r76
            r11 = r77
            r10 = r78
            r9 = r79
            r7 = r80
            r5 = r82
            r3 = r84
            r2 = r92
            java.lang.String r1 = "bitrate: "
            android.media.MediaCodec$BufferInfo r5 = new android.media.MediaCodec$BufferInfo     // Catch:{ Exception -> 0x0ff2, all -> 0x0fed }
            r5.<init>()     // Catch:{ Exception -> 0x0ff2, all -> 0x0fed }
            org.telegram.messenger.video.Mp4Movie r6 = new org.telegram.messenger.video.Mp4Movie     // Catch:{ Exception -> 0x0ff2, all -> 0x0fed }
            r6.<init>()     // Catch:{ Exception -> 0x0ff2, all -> 0x0fed }
            r15 = r73
            r6.setCacheFile(r15)     // Catch:{ Exception -> 0x0ff2, all -> 0x0fed }
            r7 = 0
            r6.setRotation(r7)     // Catch:{ Exception -> 0x0fe5, all -> 0x0fed }
            r6.setSize(r12, r11)     // Catch:{ Exception -> 0x0fe5, all -> 0x0fed }
            org.telegram.messenger.video.MP4Builder r8 = new org.telegram.messenger.video.MP4Builder     // Catch:{ Exception -> 0x0fe5, all -> 0x0fed }
            r8.<init>()     // Catch:{ Exception -> 0x0fe5, all -> 0x0fed }
            r15 = r75
            org.telegram.messenger.video.MP4Builder r6 = r8.createMovie(r6, r15)     // Catch:{ Exception -> 0x0fe5, all -> 0x0fed }
            r14.mediaMuxer = r6     // Catch:{ Exception -> 0x0fe5, all -> 0x0fed }
            float r6 = (float) r3     // Catch:{ Exception -> 0x0fe5, all -> 0x0fed }
            r17 = 1148846080(0x447a0000, float:1000.0)
            float r18 = r6 / r17
            r71.checkConversionCanceled()     // Catch:{ Exception -> 0x0fe5, all -> 0x0fed }
            java.lang.String r8 = "csd-1"
            java.lang.String r6 = "csd-0"
            r20 = r6
            java.lang.String r6 = "prepend-sps-pps-to-idr-frames"
            r22 = r6
            java.lang.String r7 = "video/avc"
            r29 = r7
            if (r91 == 0) goto L_0x0629
            if (r9 > 0) goto L_0x0058
            r9 = 921600(0xe1000, float:1.291437E-39)
        L_0x0058:
            int r7 = r12 % 16
            r23 = 1098907648(0x41800000, float:16.0)
            if (r7 == 0) goto L_0x00a1
            boolean r7 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0092, all -> 0x0fed }
            if (r7 == 0) goto L_0x0087
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0092, all -> 0x0fed }
            r7.<init>()     // Catch:{ Exception -> 0x0092, all -> 0x0fed }
            java.lang.String r6 = "changing width from "
            r7.append(r6)     // Catch:{ Exception -> 0x0092, all -> 0x0fed }
            r7.append(r12)     // Catch:{ Exception -> 0x0092, all -> 0x0fed }
            java.lang.String r6 = " to "
            r7.append(r6)     // Catch:{ Exception -> 0x0092, all -> 0x0fed }
            float r6 = (float) r12     // Catch:{ Exception -> 0x0092, all -> 0x0fed }
            float r6 = r6 / r23
            int r6 = java.lang.Math.round(r6)     // Catch:{ Exception -> 0x0092, all -> 0x0fed }
            int r6 = r6 * 16
            r7.append(r6)     // Catch:{ Exception -> 0x0092, all -> 0x0fed }
            java.lang.String r6 = r7.toString()     // Catch:{ Exception -> 0x0092, all -> 0x0fed }
            org.telegram.messenger.FileLog.d(r6)     // Catch:{ Exception -> 0x0092, all -> 0x0fed }
        L_0x0087:
            float r6 = (float) r12     // Catch:{ Exception -> 0x0092, all -> 0x0fed }
            float r6 = r6 / r23
            int r6 = java.lang.Math.round(r6)     // Catch:{ Exception -> 0x0092, all -> 0x0fed }
            int r6 = r6 * 16
            r12 = r6
            goto L_0x00a1
        L_0x0092:
            r0 = move-exception
            r6 = r80
            r36 = r1
            r50 = r9
            r9 = 0
            r33 = 0
            r47 = 0
        L_0x009e:
            r1 = r0
            goto L_0x0582
        L_0x00a1:
            int r6 = r11 % 16
            if (r6 == 0) goto L_0x00d8
            boolean r6 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0092, all -> 0x0fed }
            if (r6 == 0) goto L_0x00ce
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0092, all -> 0x0fed }
            r6.<init>()     // Catch:{ Exception -> 0x0092, all -> 0x0fed }
            java.lang.String r7 = "changing height from "
            r6.append(r7)     // Catch:{ Exception -> 0x0092, all -> 0x0fed }
            r6.append(r11)     // Catch:{ Exception -> 0x0092, all -> 0x0fed }
            java.lang.String r7 = " to "
            r6.append(r7)     // Catch:{ Exception -> 0x0092, all -> 0x0fed }
            float r7 = (float) r11     // Catch:{ Exception -> 0x0092, all -> 0x0fed }
            float r7 = r7 / r23
            int r7 = java.lang.Math.round(r7)     // Catch:{ Exception -> 0x0092, all -> 0x0fed }
            int r7 = r7 * 16
            r6.append(r7)     // Catch:{ Exception -> 0x0092, all -> 0x0fed }
            java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x0092, all -> 0x0fed }
            org.telegram.messenger.FileLog.d(r6)     // Catch:{ Exception -> 0x0092, all -> 0x0fed }
        L_0x00ce:
            float r6 = (float) r11     // Catch:{ Exception -> 0x0092, all -> 0x0fed }
            float r6 = r6 / r23
            int r6 = java.lang.Math.round(r6)     // Catch:{ Exception -> 0x0092, all -> 0x0fed }
            int r6 = r6 * 16
            r11 = r6
        L_0x00d8:
            boolean r6 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x056b, all -> 0x0fed }
            if (r6 == 0) goto L_0x0100
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0092, all -> 0x0fed }
            r6.<init>()     // Catch:{ Exception -> 0x0092, all -> 0x0fed }
            java.lang.String r7 = "create photo encoder "
            r6.append(r7)     // Catch:{ Exception -> 0x0092, all -> 0x0fed }
            r6.append(r12)     // Catch:{ Exception -> 0x0092, all -> 0x0fed }
            java.lang.String r7 = " "
            r6.append(r7)     // Catch:{ Exception -> 0x0092, all -> 0x0fed }
            r6.append(r11)     // Catch:{ Exception -> 0x0092, all -> 0x0fed }
            java.lang.String r7 = " duration = "
            r6.append(r7)     // Catch:{ Exception -> 0x0092, all -> 0x0fed }
            r6.append(r3)     // Catch:{ Exception -> 0x0092, all -> 0x0fed }
            java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x0092, all -> 0x0fed }
            org.telegram.messenger.FileLog.d(r6)     // Catch:{ Exception -> 0x0092, all -> 0x0fed }
        L_0x0100:
            r7 = r29
            android.media.MediaFormat r6 = android.media.MediaFormat.createVideoFormat(r7, r12, r11)     // Catch:{ Exception -> 0x056b, all -> 0x0fed }
            r29 = r1
            java.lang.String r1 = "color-format"
            r2 = 2130708361(0x7var_, float:1.701803E38)
            r6.setInteger(r1, r2)     // Catch:{ Exception -> 0x0561, all -> 0x0fed }
            java.lang.String r1 = "bitrate"
            r6.setInteger(r1, r9)     // Catch:{ Exception -> 0x0561, all -> 0x0fed }
            java.lang.String r1 = "frame-rate"
            r6.setInteger(r1, r10)     // Catch:{ Exception -> 0x0561, all -> 0x0fed }
            java.lang.String r1 = "i-frame-interval"
            r2 = 2
            r6.setInteger(r1, r2)     // Catch:{ Exception -> 0x0561, all -> 0x0fed }
            android.media.MediaCodec r1 = android.media.MediaCodec.createEncoderByType(r7)     // Catch:{ Exception -> 0x0561, all -> 0x0fed }
            r16 = r5
            r23 = r7
            r5 = 1
            r7 = 0
            r1.configure(r6, r7, r7, r5)     // Catch:{ Exception -> 0x0555, all -> 0x0fed }
            org.telegram.messenger.video.InputSurface r6 = new org.telegram.messenger.video.InputSurface     // Catch:{ Exception -> 0x0555, all -> 0x0fed }
            android.view.Surface r2 = r1.createInputSurface()     // Catch:{ Exception -> 0x0555, all -> 0x0fed }
            r6.<init>(r2)     // Catch:{ Exception -> 0x0555, all -> 0x0fed }
            r6.makeCurrent()     // Catch:{ Exception -> 0x0544, all -> 0x0fed }
            r1.start()     // Catch:{ Exception -> 0x0544, all -> 0x0fed }
            org.telegram.messenger.video.OutputSurface r33 = new org.telegram.messenger.video.OutputSurface     // Catch:{ Exception -> 0x0544, all -> 0x0fed }
            r34 = 0
            float r2 = (float) r10
            r35 = 1
            r76 = r1
            r36 = r29
            r1 = r33
            r28 = r2
            r29 = 2
            r2 = r88
            r3 = r72
            r4 = r89
            r15 = r16
            r5 = r90
            r7 = r6
            r44 = r20
            r45 = r22
            r13 = 21
            r6 = r34
            r47 = r7
            r48 = r23
            r7 = r12
            r49 = r8
            r8 = r11
            r50 = r9
            r9 = r74
            r10 = r28
            r51 = r11
            r11 = r35
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)     // Catch:{ Exception -> 0x053a, all -> 0x0fed }
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x052f, all -> 0x0fed }
            if (r1 >= r13) goto L_0x0188
            java.nio.ByteBuffer[] r6 = r76.getOutputBuffers()     // Catch:{ Exception -> 0x017e, all -> 0x0fed }
            goto L_0x0189
        L_0x017e:
            r0 = move-exception
            r9 = r76
        L_0x0181:
            r6 = r80
        L_0x0183:
            r1 = r0
        L_0x0184:
            r11 = r51
            goto L_0x0582
        L_0x0188:
            r6 = 0
        L_0x0189:
            r71.checkConversionCanceled()     // Catch:{ Exception -> 0x052f, all -> 0x0fed }
            r2 = r6
            r1 = 0
            r3 = -5
            r4 = 0
            r5 = 1
            r6 = 0
            r7 = 0
            r8 = 0
        L_0x0195:
            if (r6 != 0) goto L_0x051b
            r71.checkConversionCanceled()     // Catch:{ Exception -> 0x052f, all -> 0x0fed }
            r10 = r1 ^ 1
            r69 = r8
            r8 = r6
            r9 = r7
            r6 = r10
            r7 = 1
            r10 = r69
        L_0x01a4:
            if (r6 != 0) goto L_0x01ad
            if (r7 == 0) goto L_0x01a9
            goto L_0x01ad
        L_0x01a9:
            r6 = r8
            r7 = r9
            r8 = r10
            goto L_0x0195
        L_0x01ad:
            r71.checkConversionCanceled()     // Catch:{ Exception -> 0x052f, all -> 0x0fed }
            if (r87 == 0) goto L_0x01b7
            r19 = 22000(0x55f0, double:1.08694E-319)
            r13 = r19
            goto L_0x01b9
        L_0x01b7:
            r13 = 2500(0x9c4, double:1.235E-320)
        L_0x01b9:
            r69 = r6
            r6 = r76
            r76 = r69
            int r13 = r6.dequeueOutputBuffer(r15, r13)     // Catch:{ Exception -> 0x0512 }
            r14 = -1
            if (r13 != r14) goto L_0x01e0
            r14 = r71
            r79 = r1
            r20 = r2
            r23 = r5
            r19 = r9
            r21 = r10
            r5 = r48
            r2 = r51
            r1 = 0
            r11 = -1
        L_0x01d8:
            r9 = r6
            r10 = r8
            r8 = r44
        L_0x01dc:
            r6 = r80
            goto L_0x0442
        L_0x01e0:
            r14 = -3
            if (r13 != r14) goto L_0x020e
            int r14 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0203 }
            r77 = r7
            r7 = 21
            if (r14 >= r7) goto L_0x01ef
            java.nio.ByteBuffer[] r2 = r6.getOutputBuffers()     // Catch:{ Exception -> 0x0203 }
        L_0x01ef:
            r14 = r71
            r79 = r1
            r20 = r2
            r23 = r5
            r19 = r9
            r21 = r10
            r5 = r48
            r2 = r51
            r11 = -1
            r1 = r77
            goto L_0x01d8
        L_0x0203:
            r0 = move-exception
            r14 = r71
        L_0x0206:
            r1 = r0
            r9 = r6
            r11 = r51
            r6 = r80
            goto L_0x0582
        L_0x020e:
            r77 = r7
            r7 = -2
            if (r13 != r7) goto L_0x0296
            android.media.MediaFormat r7 = r6.getOutputFormat()     // Catch:{ Exception -> 0x0203 }
            boolean r14 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0203 }
            if (r14 == 0) goto L_0x0232
            java.lang.StringBuilder r14 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0203 }
            r14.<init>()     // Catch:{ Exception -> 0x0203 }
            r79 = r8
            java.lang.String r8 = "photo encoder new format "
            r14.append(r8)     // Catch:{ Exception -> 0x0203 }
            r14.append(r7)     // Catch:{ Exception -> 0x0203 }
            java.lang.String r8 = r14.toString()     // Catch:{ Exception -> 0x0203 }
            org.telegram.messenger.FileLog.d(r8)     // Catch:{ Exception -> 0x0203 }
            goto L_0x0234
        L_0x0232:
            r79 = r8
        L_0x0234:
            r8 = -5
            if (r3 != r8) goto L_0x0278
            if (r7 == 0) goto L_0x0278
            r14 = r71
            org.telegram.messenger.video.MP4Builder r3 = r14.mediaMuxer     // Catch:{ Exception -> 0x02ab, all -> 0x0fed }
            r8 = 0
            int r3 = r3.addTrack(r7, r8)     // Catch:{ Exception -> 0x02ab, all -> 0x0fed }
            r8 = r45
            boolean r19 = r7.containsKey(r8)     // Catch:{ Exception -> 0x02ab, all -> 0x0fed }
            if (r19 == 0) goto L_0x026d
            r19 = r3
            int r3 = r7.getInteger(r8)     // Catch:{ Exception -> 0x02ab, all -> 0x0fed }
            r45 = r8
            r8 = 1
            if (r3 != r8) goto L_0x0271
            r8 = r44
            java.nio.ByteBuffer r3 = r7.getByteBuffer(r8)     // Catch:{ Exception -> 0x02ab, all -> 0x0fed }
            r4 = r49
            java.nio.ByteBuffer r7 = r7.getByteBuffer(r4)     // Catch:{ Exception -> 0x02ab, all -> 0x0fed }
            int r3 = r3.limit()     // Catch:{ Exception -> 0x02ab, all -> 0x0fed }
            int r7 = r7.limit()     // Catch:{ Exception -> 0x02ab, all -> 0x0fed }
            int r3 = r3 + r7
            r7 = r4
            r4 = r3
            goto L_0x0275
        L_0x026d:
            r19 = r3
            r45 = r8
        L_0x0271:
            r8 = r44
            r7 = r49
        L_0x0275:
            r3 = r19
            goto L_0x027e
        L_0x0278:
            r14 = r71
            r8 = r44
            r7 = r49
        L_0x027e:
            r20 = r2
            r23 = r5
            r49 = r7
            r19 = r9
            r21 = r10
            r5 = r48
            r2 = r51
            r11 = -1
            r10 = r79
            r79 = r1
            r9 = r6
            r1 = r77
            goto L_0x01dc
        L_0x0296:
            r14 = r71
            r8 = r44
            r7 = r49
            if (r13 < 0) goto L_0x04f2
            r19 = r9
            int r9 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x04f0, all -> 0x0fed }
            r79 = r1
            r1 = 21
            if (r9 >= r1) goto L_0x02ae
            r1 = r2[r13]     // Catch:{ Exception -> 0x02ab, all -> 0x0fed }
            goto L_0x02b2
        L_0x02ab:
            r0 = move-exception
            goto L_0x0206
        L_0x02ae:
            java.nio.ByteBuffer r1 = r6.getOutputBuffer(r13)     // Catch:{ Exception -> 0x04f0, all -> 0x0fed }
        L_0x02b2:
            if (r1 == 0) goto L_0x04cf
            int r9 = r15.size     // Catch:{ Exception -> 0x04c9, all -> 0x0fed }
            r20 = r2
            r2 = 1
            if (r9 <= r2) goto L_0x041a
            int r9 = r15.flags     // Catch:{ Exception -> 0x040d, all -> 0x0fed }
            r2 = 2
            r9 = r9 & r2
            if (r9 != 0) goto L_0x0379
            if (r4 == 0) goto L_0x02d5
            int r9 = r15.flags     // Catch:{ Exception -> 0x02ab, all -> 0x0fed }
            r16 = 1
            r9 = r9 & 1
            if (r9 == 0) goto L_0x02d5
            int r9 = r15.offset     // Catch:{ Exception -> 0x02ab, all -> 0x0fed }
            int r9 = r9 + r4
            r15.offset = r9     // Catch:{ Exception -> 0x02ab, all -> 0x0fed }
            int r9 = r15.size     // Catch:{ Exception -> 0x02ab, all -> 0x0fed }
            int r9 = r9 - r4
            r15.size = r9     // Catch:{ Exception -> 0x02ab, all -> 0x0fed }
        L_0x02d5:
            if (r5 == 0) goto L_0x032d
            int r9 = r15.flags     // Catch:{ Exception -> 0x02ab, all -> 0x0fed }
            r16 = 1
            r9 = r9 & 1
            if (r9 == 0) goto L_0x032d
            int r5 = r15.size     // Catch:{ Exception -> 0x02ab, all -> 0x0fed }
            r9 = 100
            if (r5 <= r9) goto L_0x0329
            int r5 = r15.offset     // Catch:{ Exception -> 0x02ab, all -> 0x0fed }
            r1.position(r5)     // Catch:{ Exception -> 0x02ab, all -> 0x0fed }
            byte[] r5 = new byte[r9]     // Catch:{ Exception -> 0x02ab, all -> 0x0fed }
            r1.get(r5)     // Catch:{ Exception -> 0x02ab, all -> 0x0fed }
            r9 = 0
            r21 = 0
        L_0x02f2:
            r2 = 96
            if (r9 >= r2) goto L_0x0329
            byte r2 = r5[r9]     // Catch:{ Exception -> 0x02ab, all -> 0x0fed }
            if (r2 != 0) goto L_0x0321
            int r2 = r9 + 1
            byte r2 = r5[r2]     // Catch:{ Exception -> 0x02ab, all -> 0x0fed }
            if (r2 != 0) goto L_0x0321
            int r2 = r9 + 2
            byte r2 = r5[r2]     // Catch:{ Exception -> 0x02ab, all -> 0x0fed }
            if (r2 != 0) goto L_0x0321
            int r2 = r9 + 3
            byte r2 = r5[r2]     // Catch:{ Exception -> 0x02ab, all -> 0x0fed }
            r22 = r4
            r4 = 1
            if (r2 != r4) goto L_0x0323
            int r2 = r21 + 1
            if (r2 <= r4) goto L_0x031e
            int r2 = r15.offset     // Catch:{ Exception -> 0x02ab, all -> 0x0fed }
            int r2 = r2 + r9
            r15.offset = r2     // Catch:{ Exception -> 0x02ab, all -> 0x0fed }
            int r2 = r15.size     // Catch:{ Exception -> 0x02ab, all -> 0x0fed }
            int r2 = r2 - r9
            r15.size = r2     // Catch:{ Exception -> 0x02ab, all -> 0x0fed }
            goto L_0x032b
        L_0x031e:
            r21 = r2
            goto L_0x0323
        L_0x0321:
            r22 = r4
        L_0x0323:
            int r9 = r9 + 1
            r4 = r22
            r2 = 2
            goto L_0x02f2
        L_0x0329:
            r22 = r4
        L_0x032b:
            r5 = 0
            goto L_0x032f
        L_0x032d:
            r22 = r4
        L_0x032f:
            org.telegram.messenger.video.MP4Builder r2 = r14.mediaMuxer     // Catch:{ Exception -> 0x036f, all -> 0x0fed }
            r4 = 1
            long r1 = r2.writeSampleData(r3, r1, r15, r4)     // Catch:{ Exception -> 0x036f, all -> 0x0fed }
            r9 = r5
            r4 = 0
            int r21 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r21 == 0) goto L_0x035f
            org.telegram.messenger.MediaController$VideoConvertorListener r4 = r14.callback     // Catch:{ Exception -> 0x036f, all -> 0x0fed }
            if (r4 == 0) goto L_0x035f
            long r4 = r15.presentationTimeUs     // Catch:{ Exception -> 0x036f, all -> 0x0fed }
            r21 = r6
            r49 = r7
            r6 = r80
            long r4 = r4 - r6
            int r23 = (r4 > r10 ? 1 : (r4 == r10 ? 0 : -1))
            if (r23 <= 0) goto L_0x0352
            long r4 = r15.presentationTimeUs     // Catch:{ Exception -> 0x035d, all -> 0x0fed }
            long r10 = r4 - r6
        L_0x0352:
            org.telegram.messenger.MediaController$VideoConvertorListener r4 = r14.callback     // Catch:{ Exception -> 0x035d, all -> 0x0fed }
            float r5 = (float) r10     // Catch:{ Exception -> 0x035d, all -> 0x0fed }
            float r5 = r5 / r17
            float r5 = r5 / r18
            r4.didWriteData(r1, r5)     // Catch:{ Exception -> 0x035d, all -> 0x0fed }
            goto L_0x0365
        L_0x035d:
            r0 = move-exception
            goto L_0x0374
        L_0x035f:
            r21 = r6
            r49 = r7
            r6 = r80
        L_0x0365:
            r23 = r9
        L_0x0367:
            r5 = r48
            r1 = r49
            r2 = r51
            goto L_0x0427
        L_0x036f:
            r0 = move-exception
            r21 = r6
            r6 = r80
        L_0x0374:
            r1 = r0
            r9 = r21
            goto L_0x0184
        L_0x0379:
            r22 = r4
            r21 = r6
            r49 = r7
            r2 = -5
            r6 = r80
            if (r3 != r2) goto L_0x0409
            int r3 = r15.size     // Catch:{ Exception -> 0x0405, all -> 0x0fed }
            byte[] r3 = new byte[r3]     // Catch:{ Exception -> 0x0405, all -> 0x0fed }
            int r4 = r15.offset     // Catch:{ Exception -> 0x0405, all -> 0x0fed }
            int r9 = r15.size     // Catch:{ Exception -> 0x0405, all -> 0x0fed }
            int r4 = r4 + r9
            r1.limit(r4)     // Catch:{ Exception -> 0x0405, all -> 0x0fed }
            int r4 = r15.offset     // Catch:{ Exception -> 0x0405, all -> 0x0fed }
            r1.position(r4)     // Catch:{ Exception -> 0x0405, all -> 0x0fed }
            r1.get(r3)     // Catch:{ Exception -> 0x0405, all -> 0x0fed }
            int r1 = r15.size     // Catch:{ Exception -> 0x0405, all -> 0x0fed }
            r4 = 1
            int r1 = r1 - r4
        L_0x039c:
            if (r1 < 0) goto L_0x03e0
            r9 = 3
            if (r1 <= r9) goto L_0x03e0
            byte r2 = r3[r1]     // Catch:{ Exception -> 0x035d, all -> 0x0fed }
            if (r2 != r4) goto L_0x03d7
            int r2 = r1 + -1
            byte r2 = r3[r2]     // Catch:{ Exception -> 0x035d, all -> 0x0fed }
            if (r2 != 0) goto L_0x03d7
            int r2 = r1 + -2
            byte r2 = r3[r2]     // Catch:{ Exception -> 0x035d, all -> 0x0fed }
            if (r2 != 0) goto L_0x03d7
            int r2 = r1 + -3
            byte r4 = r3[r2]     // Catch:{ Exception -> 0x035d, all -> 0x0fed }
            if (r4 != 0) goto L_0x03d7
            java.nio.ByteBuffer r1 = java.nio.ByteBuffer.allocate(r2)     // Catch:{ Exception -> 0x035d, all -> 0x0fed }
            int r4 = r15.size     // Catch:{ Exception -> 0x035d, all -> 0x0fed }
            int r4 = r4 - r2
            java.nio.ByteBuffer r4 = java.nio.ByteBuffer.allocate(r4)     // Catch:{ Exception -> 0x035d, all -> 0x0fed }
            r23 = r5
            r9 = 0
            java.nio.ByteBuffer r5 = r1.put(r3, r9, r2)     // Catch:{ Exception -> 0x035d, all -> 0x0fed }
            r5.position(r9)     // Catch:{ Exception -> 0x035d, all -> 0x0fed }
            int r5 = r15.size     // Catch:{ Exception -> 0x035d, all -> 0x0fed }
            int r5 = r5 - r2
            java.nio.ByteBuffer r2 = r4.put(r3, r2, r5)     // Catch:{ Exception -> 0x035d, all -> 0x0fed }
            r2.position(r9)     // Catch:{ Exception -> 0x035d, all -> 0x0fed }
            goto L_0x03e4
        L_0x03d7:
            r23 = r5
            int r1 = r1 + -1
            r5 = r23
            r2 = -5
            r4 = 1
            goto L_0x039c
        L_0x03e0:
            r23 = r5
            r1 = 0
            r4 = 0
        L_0x03e4:
            r5 = r48
            r2 = r51
            android.media.MediaFormat r3 = android.media.MediaFormat.createVideoFormat(r5, r12, r2)     // Catch:{ Exception -> 0x0403, all -> 0x0fed }
            if (r1 == 0) goto L_0x03f9
            if (r4 == 0) goto L_0x03f9
            r3.setByteBuffer(r8, r1)     // Catch:{ Exception -> 0x0403, all -> 0x0fed }
            r1 = r49
            r3.setByteBuffer(r1, r4)     // Catch:{ Exception -> 0x0403, all -> 0x0fed }
            goto L_0x03fb
        L_0x03f9:
            r1 = r49
        L_0x03fb:
            org.telegram.messenger.video.MP4Builder r4 = r14.mediaMuxer     // Catch:{ Exception -> 0x0403, all -> 0x0fed }
            r9 = 0
            int r3 = r4.addTrack(r3, r9)     // Catch:{ Exception -> 0x0403, all -> 0x0fed }
            goto L_0x0427
        L_0x0403:
            r0 = move-exception
            goto L_0x0414
        L_0x0405:
            r0 = move-exception
            r2 = r51
            goto L_0x0414
        L_0x0409:
            r23 = r5
            goto L_0x0367
        L_0x040d:
            r0 = move-exception
            r21 = r6
            r2 = r51
            r6 = r80
        L_0x0414:
            r1 = r0
            r11 = r2
            r9 = r21
            goto L_0x0582
        L_0x041a:
            r22 = r4
            r23 = r5
            r21 = r6
            r1 = r7
            r5 = r48
            r2 = r51
            r6 = r80
        L_0x0427:
            int r4 = r15.flags     // Catch:{ Exception -> 0x04c3, all -> 0x0fed }
            r4 = r4 & 4
            r49 = r1
            r9 = r21
            r1 = 0
            if (r4 == 0) goto L_0x0434
            r4 = 1
            goto L_0x0435
        L_0x0434:
            r4 = 0
        L_0x0435:
            r9.releaseOutputBuffer(r13, r1)     // Catch:{ Exception -> 0x0499, all -> 0x0fed }
            r1 = r77
            r69 = r10
            r10 = r4
            r4 = r22
            r11 = -1
            r21 = r69
        L_0x0442:
            if (r13 == r11) goto L_0x045e
            r6 = r76
            r7 = r1
            r51 = r2
            r48 = r5
            r44 = r8
            r76 = r9
            r8 = r10
            r9 = r19
            r2 = r20
            r10 = r21
            r5 = r23
            r13 = 21
            r1 = r79
            goto L_0x01a4
        L_0x045e:
            if (r79 != 0) goto L_0x04a0
            r33.drawImage()     // Catch:{ Exception -> 0x0499, all -> 0x0fed }
            r11 = r19
            float r13 = (float) r11
            r19 = 1106247680(0x41var_, float:30.0)
            float r13 = r13 / r19
            float r13 = r13 * r17
            float r13 = r13 * r17
            float r13 = r13 * r17
            r77 = r1
            r51 = r2
            long r1 = (long) r13
            r13 = r47
            r13.setPresentationTime(r1)     // Catch:{ Exception -> 0x0493, all -> 0x0fed }
            r13.swapBuffers()     // Catch:{ Exception -> 0x0493, all -> 0x0fed }
            int r1 = r11 + 1
            float r2 = (float) r1     // Catch:{ Exception -> 0x0493, all -> 0x0fed }
            r11 = 1106247680(0x41var_, float:30.0)
            float r11 = r11 * r18
            int r2 = (r2 > r11 ? 1 : (r2 == r11 ? 0 : -1))
            if (r2 < 0) goto L_0x048f
            r9.signalEndOfInputStream()     // Catch:{ Exception -> 0x0493, all -> 0x0fed }
            r11 = r1
            r1 = 1
            r2 = 0
            goto L_0x04ac
        L_0x048f:
            r2 = r76
            r11 = r1
            goto L_0x04aa
        L_0x0493:
            r0 = move-exception
            r1 = r0
            r47 = r13
            goto L_0x0184
        L_0x0499:
            r0 = move-exception
            r51 = r2
        L_0x049c:
            r13 = r47
            goto L_0x0183
        L_0x04a0:
            r77 = r1
            r51 = r2
            r11 = r19
            r13 = r47
            r2 = r76
        L_0x04aa:
            r1 = r79
        L_0x04ac:
            r7 = r77
            r6 = r2
            r48 = r5
            r44 = r8
            r76 = r9
            r8 = r10
            r9 = r11
            r47 = r13
            r2 = r20
            r10 = r21
            r5 = r23
            r13 = 21
            goto L_0x01a4
        L_0x04c3:
            r0 = move-exception
            r51 = r2
            r9 = r21
            goto L_0x049c
        L_0x04c9:
            r0 = move-exception
            r9 = r6
            r13 = r47
            goto L_0x0181
        L_0x04cf:
            r9 = r6
            r1 = r47
            r6 = r80
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x050e, all -> 0x0fed }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x050e, all -> 0x0fed }
            r3.<init>()     // Catch:{ Exception -> 0x050e, all -> 0x0fed }
            java.lang.String r4 = "encoderOutputBuffer "
            r3.append(r4)     // Catch:{ Exception -> 0x050e, all -> 0x0fed }
            r3.append(r13)     // Catch:{ Exception -> 0x050e, all -> 0x0fed }
            java.lang.String r4 = " was null"
            r3.append(r4)     // Catch:{ Exception -> 0x050e, all -> 0x0fed }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x050e, all -> 0x0fed }
            r2.<init>(r3)     // Catch:{ Exception -> 0x050e, all -> 0x0fed }
            throw r2     // Catch:{ Exception -> 0x050e, all -> 0x0fed }
        L_0x04f0:
            r0 = move-exception
            goto L_0x0515
        L_0x04f2:
            r9 = r6
            r1 = r47
            r6 = r80
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x050e, all -> 0x0fed }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x050e, all -> 0x0fed }
            r3.<init>()     // Catch:{ Exception -> 0x050e, all -> 0x0fed }
            java.lang.String r4 = "unexpected result from encoder.dequeueOutputBuffer: "
            r3.append(r4)     // Catch:{ Exception -> 0x050e, all -> 0x0fed }
            r3.append(r13)     // Catch:{ Exception -> 0x050e, all -> 0x0fed }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x050e, all -> 0x0fed }
            r2.<init>(r3)     // Catch:{ Exception -> 0x050e, all -> 0x0fed }
            throw r2     // Catch:{ Exception -> 0x050e, all -> 0x0fed }
        L_0x050e:
            r0 = move-exception
            r47 = r1
            goto L_0x0536
        L_0x0512:
            r0 = move-exception
            r14 = r71
        L_0x0515:
            r9 = r6
            r1 = r47
            r6 = r80
            goto L_0x0536
        L_0x051b:
            r9 = r76
            r6 = r80
            r1 = r47
            r10 = r78
            r1 = r9
            r13 = r36
            r3 = r50
            r11 = r51
            r2 = 0
            r42 = 0
            goto L_0x05c5
        L_0x052f:
            r0 = move-exception
            r9 = r76
            r6 = r80
            r1 = r47
        L_0x0536:
            r11 = r51
            goto L_0x009e
        L_0x053a:
            r0 = move-exception
            r9 = r76
            r6 = r80
            r1 = r47
            r11 = r51
            goto L_0x0551
        L_0x0544:
            r0 = move-exception
            r50 = r9
            r51 = r11
            r36 = r29
            r9 = r1
            r1 = r6
            r6 = r80
            r47 = r1
        L_0x0551:
            r33 = 0
            goto L_0x009e
        L_0x0555:
            r0 = move-exception
            r6 = r80
            r50 = r9
            r51 = r11
            r36 = r29
            r9 = r1
            r1 = r0
            goto L_0x057e
        L_0x0561:
            r0 = move-exception
            r6 = r80
            r50 = r9
            r51 = r11
            r36 = r29
            goto L_0x057c
        L_0x056b:
            r0 = move-exception
            r6 = r80
            r36 = r1
            r50 = r9
            r51 = r11
            goto L_0x057c
        L_0x0575:
            r0 = move-exception
            r6 = r80
            r36 = r1
            r50 = r9
        L_0x057c:
            r1 = r0
            r9 = 0
        L_0x057e:
            r33 = 0
            r47 = 0
        L_0x0582:
            boolean r2 = r1 instanceof java.lang.IllegalStateException     // Catch:{ Exception -> 0x0618, all -> 0x0fed }
            if (r2 == 0) goto L_0x058b
            if (r87 != 0) goto L_0x058b
            r42 = 1
            goto L_0x058d
        L_0x058b:
            r42 = 0
        L_0x058d:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0605, all -> 0x0fed }
            r2.<init>()     // Catch:{ Exception -> 0x0605, all -> 0x0fed }
            r13 = r36
            r2.append(r13)     // Catch:{ Exception -> 0x05fb, all -> 0x0fed }
            r3 = r50
            r2.append(r3)     // Catch:{ Exception -> 0x05f9, all -> 0x0fed }
            java.lang.String r4 = " framerate: "
            r2.append(r4)     // Catch:{ Exception -> 0x05f9, all -> 0x0fed }
            r10 = r78
            r2.append(r10)     // Catch:{ Exception -> 0x05f4, all -> 0x0fed }
            java.lang.String r4 = " size: "
            r2.append(r4)     // Catch:{ Exception -> 0x05f4, all -> 0x0fed }
            r2.append(r11)     // Catch:{ Exception -> 0x05f4, all -> 0x0fed }
            java.lang.String r4 = "x"
            r2.append(r4)     // Catch:{ Exception -> 0x05f4, all -> 0x0fed }
            r2.append(r12)     // Catch:{ Exception -> 0x05f4, all -> 0x0fed }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x05f4, all -> 0x0fed }
            org.telegram.messenger.FileLog.e((java.lang.String) r2)     // Catch:{ Exception -> 0x05f4, all -> 0x0fed }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)     // Catch:{ Exception -> 0x05f4, all -> 0x0fed }
            r1 = r9
            r2 = r42
            r42 = 1
        L_0x05c5:
            if (r33 == 0) goto L_0x05d9
            r33.release()     // Catch:{ Exception -> 0x05cb, all -> 0x0fed }
            goto L_0x05d9
        L_0x05cb:
            r0 = move-exception
            r1 = r0
            r8 = r10
            r5 = r11
            r4 = r13
            r69 = r6
            r6 = r2
            r2 = r3
            r7 = r12
            r11 = r69
            goto L_0x0fff
        L_0x05d9:
            if (r47 == 0) goto L_0x05de
            r47.release()     // Catch:{ Exception -> 0x05cb, all -> 0x0fed }
        L_0x05de:
            if (r1 == 0) goto L_0x05e6
            r1.stop()     // Catch:{ Exception -> 0x05cb, all -> 0x0fed }
            r1.release()     // Catch:{ Exception -> 0x05cb, all -> 0x0fed }
        L_0x05e6:
            r71.checkConversionCanceled()     // Catch:{ Exception -> 0x05cb, all -> 0x0fed }
            r13 = r2
            r2 = r3
            r8 = r10
            r5 = r11
            r69 = r6
            r7 = r12
            r11 = r69
            goto L_0x0fb9
        L_0x05f4:
            r0 = move-exception
            r1 = r0
            r2 = r3
            r8 = r10
            goto L_0x0602
        L_0x05f9:
            r0 = move-exception
            goto L_0x05fe
        L_0x05fb:
            r0 = move-exception
            r3 = r50
        L_0x05fe:
            r8 = r78
            r1 = r0
            r2 = r3
        L_0x0602:
            r5 = r11
            r4 = r13
            goto L_0x060f
        L_0x0605:
            r0 = move-exception
            r3 = r50
            r8 = r78
            r1 = r0
            r2 = r3
            r5 = r11
            r4 = r36
        L_0x060f:
            r69 = r6
            r7 = r12
            r11 = r69
            r6 = r42
            goto L_0x0fff
        L_0x0618:
            r0 = move-exception
            r3 = r50
            r8 = r78
            r1 = r0
            r2 = r3
            r5 = r11
            r4 = r36
            r69 = r6
            r7 = r12
            r11 = r69
            goto L_0x0ffe
        L_0x0629:
            r6 = r80
            r13 = r1
            r15 = r5
            r49 = r8
            r8 = r20
            r45 = r22
            r5 = r29
            r3 = 3
            r4 = 100
            android.media.MediaExtractor r1 = new android.media.MediaExtractor     // Catch:{ Exception -> 0x0fde, all -> 0x0fed }
            r1.<init>()     // Catch:{ Exception -> 0x0fde, all -> 0x0fed }
            r14.extractor = r1     // Catch:{ Exception -> 0x0fde, all -> 0x0fed }
            r2 = r72
            r1.setDataSource(r2)     // Catch:{ Exception -> 0x0fde, all -> 0x0fed }
            android.media.MediaExtractor r1 = r14.extractor     // Catch:{ Exception -> 0x0fde, all -> 0x0fed }
            r7 = 0
            int r6 = org.telegram.messenger.MediaController.findTrack(r1, r7)     // Catch:{ Exception -> 0x0fd8, all -> 0x0fed }
            r1 = -1
            if (r9 == r1) goto L_0x0663
            android.media.MediaExtractor r1 = r14.extractor     // Catch:{ Exception -> 0x0657, all -> 0x0fed }
            r3 = 1
            int r1 = org.telegram.messenger.MediaController.findTrack(r1, r3)     // Catch:{ Exception -> 0x0657, all -> 0x0fed }
            r3 = r1
            goto L_0x0664
        L_0x0657:
            r0 = move-exception
            r1 = r0
            r2 = r9
            r8 = r10
            r5 = r11
            r7 = r12
            r4 = r13
            r6 = 0
            r11 = r80
            goto L_0x0fff
        L_0x0663:
            r3 = -1
        L_0x0664:
            java.lang.String r1 = "mime"
            if (r6 < 0) goto L_0x067a
            android.media.MediaExtractor r4 = r14.extractor     // Catch:{ Exception -> 0x0657, all -> 0x0fed }
            android.media.MediaFormat r4 = r4.getTrackFormat(r6)     // Catch:{ Exception -> 0x0657, all -> 0x0fed }
            java.lang.String r4 = r4.getString(r1)     // Catch:{ Exception -> 0x0657, all -> 0x0fed }
            boolean r4 = r4.equals(r5)     // Catch:{ Exception -> 0x0657, all -> 0x0fed }
            if (r4 != 0) goto L_0x067a
            r4 = 1
            goto L_0x067b
        L_0x067a:
            r4 = 0
        L_0x067b:
            if (r86 != 0) goto L_0x06c6
            if (r4 == 0) goto L_0x0680
            goto L_0x06c6
        L_0x0680:
            android.media.MediaExtractor r3 = r14.extractor     // Catch:{ Exception -> 0x06b2, all -> 0x0fed }
            org.telegram.messenger.video.MP4Builder r4 = r14.mediaMuxer     // Catch:{ Exception -> 0x06b2, all -> 0x0fed }
            r1 = -1
            if (r9 == r1) goto L_0x068a
            r17 = 1
            goto L_0x068c
        L_0x068a:
            r17 = 0
        L_0x068c:
            r1 = r71
            r8 = r2
            r2 = r3
            r3 = r4
            r4 = r15
            r5 = r80
            r15 = r8
            r36 = r13
            r13 = 0
            r7 = r82
            r9 = r84
            r11 = r73
            r12 = r17
            r1.readAndWriteTracks(r2, r3, r4, r5, r7, r9, r11, r12)     // Catch:{ Exception -> 0x06b0, all -> 0x0fed }
            r7 = r76
            r5 = r77
            r8 = r78
            r2 = r79
            r11 = r80
            r6 = 0
            goto L_0x0fbb
        L_0x06b0:
            r0 = move-exception
            goto L_0x06b7
        L_0x06b2:
            r0 = move-exception
            r15 = r2
            r36 = r13
            r13 = 0
        L_0x06b7:
            r7 = r76
            r5 = r77
            r8 = r78
            r2 = r79
            r11 = r80
            r1 = r0
            r4 = r36
            goto L_0x0ffe
        L_0x06c6:
            r12 = r2
            r36 = r13
            r13 = 0
            if (r6 < 0) goto L_0x0var_
            r19 = -1
            android.media.MediaExtractor r2 = r14.extractor     // Catch:{ Exception -> 0x0eea, all -> 0x0fed }
            r2.selectTrack(r6)     // Catch:{ Exception -> 0x0eea, all -> 0x0fed }
            android.media.MediaExtractor r2 = r14.extractor     // Catch:{ Exception -> 0x0eea, all -> 0x0fed }
            android.media.MediaFormat r11 = r2.getTrackFormat(r6)     // Catch:{ Exception -> 0x0eea, all -> 0x0fed }
            r9 = r80
            r21 = 0
            int r2 = (r9 > r21 ? 1 : (r9 == r21 ? 0 : -1))
            if (r2 <= 0) goto L_0x06f3
            android.media.MediaExtractor r2 = r14.extractor     // Catch:{ Exception -> 0x06ea, all -> 0x0fed }
            r2.seekTo(r9, r13)     // Catch:{ Exception -> 0x06ea, all -> 0x0fed }
            r7 = r3
            r3 = 0
            goto L_0x06fb
        L_0x06ea:
            r0 = move-exception
            r2 = r79
        L_0x06ed:
            r1 = r0
            r65 = r6
            r11 = r9
            goto L_0x0ef2
        L_0x06f3:
            android.media.MediaExtractor r2 = r14.extractor     // Catch:{ Exception -> 0x0ee5, all -> 0x0fed }
            r7 = r3
            r3 = 0
            r2.seekTo(r3, r13)     // Catch:{ Exception -> 0x0ee5, all -> 0x0fed }
        L_0x06fb:
            if (r79 > 0) goto L_0x0703
            r13 = r92
            r2 = 921600(0xe1000, float:1.291437E-39)
            goto L_0x0707
        L_0x0703:
            r2 = r79
            r13 = r92
        L_0x0707:
            if (r13 == 0) goto L_0x0726
            r3 = 90
            r4 = r74
            if (r4 == r3) goto L_0x071b
            r3 = 270(0x10e, float:3.78E-43)
            if (r4 != r3) goto L_0x0714
            goto L_0x071b
        L_0x0714:
            int r3 = r13.transformWidth     // Catch:{ Exception -> 0x0724, all -> 0x0fed }
            r21 = r3
            int r3 = r13.transformHeight     // Catch:{ Exception -> 0x0724, all -> 0x0fed }
            goto L_0x0721
        L_0x071b:
            int r3 = r13.transformHeight     // Catch:{ Exception -> 0x0724, all -> 0x0fed }
            r21 = r3
            int r3 = r13.transformWidth     // Catch:{ Exception -> 0x0724, all -> 0x0fed }
        L_0x0721:
            r13 = r21
            goto L_0x072c
        L_0x0724:
            r0 = move-exception
            goto L_0x06ed
        L_0x0726:
            r4 = r74
            r13 = r76
            r3 = r77
        L_0x072c:
            android.media.MediaFormat r4 = android.media.MediaFormat.createVideoFormat(r5, r13, r3)     // Catch:{ Exception -> 0x0ee0, all -> 0x0fed }
            r21 = r6
            java.lang.String r6 = "color-format"
            r22 = r7
            r7 = 2130708361(0x7var_, float:1.701803E38)
            r4.setInteger(r6, r7)     // Catch:{ Exception -> 0x0edb, all -> 0x0fed }
            java.lang.String r6 = "bitrate"
            r4.setInteger(r6, r2)     // Catch:{ Exception -> 0x0edb, all -> 0x0fed }
            java.lang.String r6 = "frame-rate"
            r7 = r78
            r4.setInteger(r6, r7)     // Catch:{ Exception -> 0x0edb, all -> 0x0fed }
            java.lang.String r6 = "i-frame-interval"
            r44 = r8
            r8 = 2
            r4.setInteger(r6, r8)     // Catch:{ Exception -> 0x0edb, all -> 0x0fed }
            int r6 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0edb, all -> 0x0fed }
            r8 = 23
            if (r6 >= r8) goto L_0x0773
            int r6 = java.lang.Math.min(r3, r13)     // Catch:{ Exception -> 0x076c, all -> 0x0fed }
            r8 = 480(0x1e0, float:6.73E-43)
            if (r6 > r8) goto L_0x0773
            r6 = 921600(0xe1000, float:1.291437E-39)
            if (r2 <= r6) goto L_0x0766
            r2 = 921600(0xe1000, float:1.291437E-39)
        L_0x0766:
            java.lang.String r6 = "bitrate"
            r4.setInteger(r6, r2)     // Catch:{ Exception -> 0x076c, all -> 0x0fed }
            goto L_0x0773
        L_0x076c:
            r0 = move-exception
            r1 = r0
            r11 = r9
            r65 = r21
            goto L_0x0ef2
        L_0x0773:
            r23 = r2
            android.media.MediaCodec r8 = android.media.MediaCodec.createEncoderByType(r5)     // Catch:{ Exception -> 0x0ed3, all -> 0x0fed }
            r2 = 1
            r6 = 0
            r8.configure(r4, r6, r6, r2)     // Catch:{ Exception -> 0x0ec9, all -> 0x0fed }
            org.telegram.messenger.video.InputSurface r4 = new org.telegram.messenger.video.InputSurface     // Catch:{ Exception -> 0x0ec9, all -> 0x0fed }
            android.view.Surface r2 = r8.createInputSurface()     // Catch:{ Exception -> 0x0ec9, all -> 0x0fed }
            r4.<init>(r2)     // Catch:{ Exception -> 0x0ec9, all -> 0x0fed }
            r4.makeCurrent()     // Catch:{ Exception -> 0x0eb7, all -> 0x0fed }
            r8.start()     // Catch:{ Exception -> 0x0eb7, all -> 0x0fed }
            java.lang.String r2 = r11.getString(r1)     // Catch:{ Exception -> 0x0eb7, all -> 0x0fed }
            android.media.MediaCodec r2 = android.media.MediaCodec.createDecoderByType(r2)     // Catch:{ Exception -> 0x0eb7, all -> 0x0fed }
            org.telegram.messenger.video.OutputSurface r24 = new org.telegram.messenger.video.OutputSurface     // Catch:{ Exception -> 0x0ea4, all -> 0x0fed }
            r25 = 0
            r26 = r3
            float r3 = (float) r7
            r27 = 0
            r59 = r1
            r28 = r49
            r1 = r24
            r60 = r2
            r29 = 2
            r2 = r88
            r61 = r22
            r62 = r26
            r31 = 0
            r22 = r3
            r3 = r25
            r63 = r4
            r4 = r89
            r64 = r5
            r5 = r90
            r65 = r21
            r6 = r92
            r66 = r28
            r7 = r76
            r79 = r8
            r67 = r44
            r68 = r45
            r8 = r77
            r9 = r74
            r10 = r22
            r21 = r13
            r13 = r11
            r11 = r27
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)     // Catch:{ Exception -> 0x0e99, all -> 0x0fed }
            android.view.Surface r1 = r24.getSurface()     // Catch:{ Exception -> 0x0e8b, all -> 0x0fed }
            r2 = r60
            r3 = 0
            r4 = 0
            r2.configure(r13, r1, r3, r4)     // Catch:{ Exception -> 0x0e84, all -> 0x0fed }
            r2.start()     // Catch:{ Exception -> 0x0e84, all -> 0x0fed }
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0e84, all -> 0x0fed }
            r4 = 21
            if (r1 >= r4) goto L_0x0802
            java.nio.ByteBuffer[] r6 = r2.getInputBuffers()     // Catch:{ Exception -> 0x07f5, all -> 0x0fed }
            java.nio.ByteBuffer[] r1 = r79.getOutputBuffers()     // Catch:{ Exception -> 0x07f5, all -> 0x0fed }
            goto L_0x0804
        L_0x07f5:
            r0 = move-exception
            r6 = r79
            r11 = r80
            r1 = r0
            r10 = r2
            r43 = r3
        L_0x07fe:
            r2 = r23
            goto L_0x0efa
        L_0x0802:
            r1 = r3
            r6 = r1
        L_0x0804:
            r4 = r61
            if (r4 < 0) goto L_0x08de
            android.media.MediaExtractor r5 = r14.extractor     // Catch:{ Exception -> 0x08d1, all -> 0x0fed }
            android.media.MediaFormat r5 = r5.getTrackFormat(r4)     // Catch:{ Exception -> 0x08d1, all -> 0x0fed }
            r7 = r59
            java.lang.String r8 = r5.getString(r7)     // Catch:{ Exception -> 0x08d1, all -> 0x0fed }
            java.lang.String r9 = "audio/mp4a-latm"
            boolean r8 = r8.equals(r9)     // Catch:{ Exception -> 0x08d1, all -> 0x0fed }
            if (r8 != 0) goto L_0x082b
            java.lang.String r8 = r5.getString(r7)     // Catch:{ Exception -> 0x07f5, all -> 0x0fed }
            java.lang.String r9 = "audio/mpeg"
            boolean r8 = r8.equals(r9)     // Catch:{ Exception -> 0x07f5, all -> 0x0fed }
            if (r8 == 0) goto L_0x0829
            goto L_0x082b
        L_0x0829:
            r8 = 0
            goto L_0x082c
        L_0x082b:
            r8 = 1
        L_0x082c:
            java.lang.String r7 = r5.getString(r7)     // Catch:{ Exception -> 0x08d1, all -> 0x0fed }
            java.lang.String r9 = "audio/unknown"
            boolean r7 = r7.equals(r9)     // Catch:{ Exception -> 0x08d1, all -> 0x0fed }
            if (r7 == 0) goto L_0x0839
            r4 = -1
        L_0x0839:
            if (r4 < 0) goto L_0x08c6
            if (r8 == 0) goto L_0x087c
            org.telegram.messenger.video.MP4Builder r7 = r14.mediaMuxer     // Catch:{ Exception -> 0x0879, all -> 0x0fed }
            r9 = 1
            int r7 = r7.addTrack(r5, r9)     // Catch:{ Exception -> 0x0879, all -> 0x0fed }
            android.media.MediaExtractor r9 = r14.extractor     // Catch:{ Exception -> 0x0879, all -> 0x0fed }
            r9.selectTrack(r4)     // Catch:{ Exception -> 0x0879, all -> 0x0fed }
            java.lang.String r9 = "max-input-size"
            int r5 = r5.getInteger(r9)     // Catch:{ Exception -> 0x0879, all -> 0x0fed }
            java.nio.ByteBuffer r5 = java.nio.ByteBuffer.allocateDirect(r5)     // Catch:{ Exception -> 0x0879, all -> 0x0fed }
            r10 = r80
            r9 = r4
            r3 = 0
            int r13 = (r10 > r3 ? 1 : (r10 == r3 ? 0 : -1))
            if (r13 <= 0) goto L_0x0866
            android.media.MediaExtractor r13 = r14.extractor     // Catch:{ Exception -> 0x0899, all -> 0x0fed }
            r3 = 0
            r13.seekTo(r10, r3)     // Catch:{ Exception -> 0x0899, all -> 0x0fed }
            r22 = r1
            r13 = r5
            goto L_0x0871
        L_0x0866:
            android.media.MediaExtractor r3 = r14.extractor     // Catch:{ Exception -> 0x0899, all -> 0x0fed }
            r22 = r1
            r13 = r5
            r1 = 0
            r4 = 0
            r3.seekTo(r4, r1)     // Catch:{ Exception -> 0x0899, all -> 0x0fed }
        L_0x0871:
            r1 = r7
            r5 = r8
            r3 = r9
            r7 = 0
            r8 = r82
            goto L_0x08ea
        L_0x0879:
            r0 = move-exception
            goto L_0x08d4
        L_0x087c:
            r10 = r80
            r22 = r1
            r9 = r4
            android.media.MediaExtractor r1 = new android.media.MediaExtractor     // Catch:{ Exception -> 0x08bb, all -> 0x0fed }
            r1.<init>()     // Catch:{ Exception -> 0x08bb, all -> 0x0fed }
            r1.setDataSource(r12)     // Catch:{ Exception -> 0x08bb, all -> 0x0fed }
            r4 = r9
            r1.selectTrack(r4)     // Catch:{ Exception -> 0x08bb, all -> 0x0fed }
            r3 = r8
            r7 = 0
            int r9 = (r10 > r7 ? 1 : (r10 == r7 ? 0 : -1))
            if (r9 <= 0) goto L_0x089b
            r9 = 0
            r1.seekTo(r10, r9)     // Catch:{ Exception -> 0x0899, all -> 0x0fed }
            goto L_0x089f
        L_0x0899:
            r0 = move-exception
            goto L_0x08be
        L_0x089b:
            r9 = 0
            r1.seekTo(r7, r9)     // Catch:{ Exception -> 0x08bb, all -> 0x0fed }
        L_0x089f:
            org.telegram.messenger.video.AudioRecoder r7 = new org.telegram.messenger.video.AudioRecoder     // Catch:{ Exception -> 0x08bb, all -> 0x0fed }
            r7.<init>(r5, r1, r4)     // Catch:{ Exception -> 0x08bb, all -> 0x0fed }
            r7.startTime = r10     // Catch:{ Exception -> 0x08b6, all -> 0x0fed }
            r8 = r82
            r7.endTime = r8     // Catch:{ Exception -> 0x092c, all -> 0x0fed }
            org.telegram.messenger.video.MP4Builder r1 = r14.mediaMuxer     // Catch:{ Exception -> 0x092c, all -> 0x0fed }
            android.media.MediaFormat r5 = r7.format     // Catch:{ Exception -> 0x092c, all -> 0x0fed }
            r13 = 1
            int r1 = r1.addTrack(r5, r13)     // Catch:{ Exception -> 0x092c, all -> 0x0fed }
            r5 = r3
            r3 = r4
            goto L_0x08e9
        L_0x08b6:
            r0 = move-exception
            r8 = r82
            goto L_0x092d
        L_0x08bb:
            r0 = move-exception
            r8 = r82
        L_0x08be:
            r6 = r79
            r1 = r0
            r11 = r10
            r43 = 0
            goto L_0x0a73
        L_0x08c6:
            r10 = r80
            r22 = r1
            r3 = r8
            r8 = r82
            r5 = r3
            r3 = r4
            r1 = -5
            goto L_0x08e8
        L_0x08d1:
            r0 = move-exception
            r8 = r82
        L_0x08d4:
            r6 = r79
            r11 = r80
            r1 = r0
            r10 = r2
            r2 = r23
            goto L_0x0ec6
        L_0x08de:
            r10 = r80
            r8 = r82
            r22 = r1
            r1 = r4
            r3 = r1
            r1 = -5
            r5 = 1
        L_0x08e8:
            r7 = 0
        L_0x08e9:
            r13 = 0
        L_0x08ea:
            if (r3 >= 0) goto L_0x08ee
            r4 = 1
            goto L_0x08ef
        L_0x08ee:
            r4 = 0
        L_0x08ef:
            r71.checkConversionCanceled()     // Catch:{ Exception -> 0x0e76, all -> 0x0fed }
            r33 = r19
            r19 = 1
            r20 = 0
            r25 = 0
            r26 = 0
            r27 = -5
            r28 = 0
            r31 = 0
        L_0x0902:
            if (r20 == 0) goto L_0x091e
            if (r5 != 0) goto L_0x0909
            if (r4 != 0) goto L_0x0909
            goto L_0x091e
        L_0x0909:
            r5 = r77
            r8 = r78
            r1 = r79
            r43 = r7
            r11 = r10
            r4 = r36
            r6 = 0
            r42 = 0
            r7 = r76
            r10 = r2
            r2 = r23
            goto L_0x0f3f
        L_0x091e:
            r71.checkConversionCanceled()     // Catch:{ Exception -> 0x0e76, all -> 0x0fed }
            if (r5 != 0) goto L_0x0934
            if (r7 == 0) goto L_0x0934
            org.telegram.messenger.video.MP4Builder r4 = r14.mediaMuxer     // Catch:{ Exception -> 0x092c, all -> 0x0fed }
            boolean r4 = r7.step(r4, r1)     // Catch:{ Exception -> 0x092c, all -> 0x0fed }
            goto L_0x0934
        L_0x092c:
            r0 = move-exception
        L_0x092d:
            r6 = r79
            r1 = r0
            r43 = r7
            goto L_0x0a72
        L_0x0934:
            if (r25 != 0) goto L_0x0a76
            r35 = r4
            android.media.MediaExtractor r4 = r14.extractor     // Catch:{ Exception -> 0x0a6c, all -> 0x0fed }
            int r4 = r4.getSampleTrackIndex()     // Catch:{ Exception -> 0x0a6c, all -> 0x0fed }
            r43 = r7
            r7 = r65
            if (r4 != r7) goto L_0x09a7
            r10 = 2500(0x9c4, double:1.235E-320)
            int r4 = r2.dequeueInputBuffer(r10)     // Catch:{ Exception -> 0x099c, all -> 0x0fed }
            if (r4 < 0) goto L_0x098d
            int r10 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x099c, all -> 0x0fed }
            r11 = 21
            if (r10 >= r11) goto L_0x0955
            r10 = r6[r4]     // Catch:{ Exception -> 0x099c, all -> 0x0fed }
            goto L_0x0959
        L_0x0955:
            java.nio.ByteBuffer r10 = r2.getInputBuffer(r4)     // Catch:{ Exception -> 0x099c, all -> 0x0fed }
        L_0x0959:
            android.media.MediaExtractor r11 = r14.extractor     // Catch:{ Exception -> 0x099c, all -> 0x0fed }
            r44 = r6
            r6 = 0
            int r55 = r11.readSampleData(r10, r6)     // Catch:{ Exception -> 0x099c, all -> 0x0fed }
            if (r55 >= 0) goto L_0x0976
            r54 = 0
            r55 = 0
            r56 = 0
            r58 = 4
            r52 = r2
            r53 = r4
            r52.queueInputBuffer(r53, r54, r55, r56, r58)     // Catch:{ Exception -> 0x099c, all -> 0x0fed }
            r25 = 1
            goto L_0x098f
        L_0x0976:
            r54 = 0
            android.media.MediaExtractor r6 = r14.extractor     // Catch:{ Exception -> 0x099c, all -> 0x0fed }
            long r56 = r6.getSampleTime()     // Catch:{ Exception -> 0x099c, all -> 0x0fed }
            r58 = 0
            r52 = r2
            r53 = r4
            r52.queueInputBuffer(r53, r54, r55, r56, r58)     // Catch:{ Exception -> 0x099c, all -> 0x0fed }
            android.media.MediaExtractor r4 = r14.extractor     // Catch:{ Exception -> 0x099c, all -> 0x0fed }
            r4.advance()     // Catch:{ Exception -> 0x099c, all -> 0x0fed }
            goto L_0x098f
        L_0x098d:
            r44 = r6
        L_0x098f:
            r38 = r3
            r37 = r5
            r45 = r13
            r5 = r25
            r6 = 0
            r12 = r80
            goto L_0x0a49
        L_0x099c:
            r0 = move-exception
            r6 = r79
            r11 = r80
            r1 = r0
            r10 = r2
            r65 = r7
            goto L_0x07fe
        L_0x09a7:
            r44 = r6
            if (r5 == 0) goto L_0x0a37
            r6 = -1
            if (r3 == r6) goto L_0x0a37
            if (r4 != r3) goto L_0x0a37
            android.media.MediaExtractor r4 = r14.extractor     // Catch:{ Exception -> 0x099c, all -> 0x0fed }
            r6 = 0
            int r4 = r4.readSampleData(r13, r6)     // Catch:{ Exception -> 0x099c, all -> 0x0fed }
            r15.size = r4     // Catch:{ Exception -> 0x099c, all -> 0x0fed }
            int r4 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x099c, all -> 0x0fed }
            r10 = 21
            if (r4 >= r10) goto L_0x09c7
            r13.position(r6)     // Catch:{ Exception -> 0x099c, all -> 0x0fed }
            int r4 = r15.size     // Catch:{ Exception -> 0x099c, all -> 0x0fed }
            r13.limit(r4)     // Catch:{ Exception -> 0x099c, all -> 0x0fed }
        L_0x09c7:
            int r4 = r15.size     // Catch:{ Exception -> 0x099c, all -> 0x0fed }
            if (r4 < 0) goto L_0x09d9
            android.media.MediaExtractor r4 = r14.extractor     // Catch:{ Exception -> 0x099c, all -> 0x0fed }
            long r10 = r4.getSampleTime()     // Catch:{ Exception -> 0x099c, all -> 0x0fed }
            r15.presentationTimeUs = r10     // Catch:{ Exception -> 0x099c, all -> 0x0fed }
            android.media.MediaExtractor r4 = r14.extractor     // Catch:{ Exception -> 0x099c, all -> 0x0fed }
            r4.advance()     // Catch:{ Exception -> 0x099c, all -> 0x0fed }
            goto L_0x09de
        L_0x09d9:
            r4 = 0
            r15.size = r4     // Catch:{ Exception -> 0x099c, all -> 0x0fed }
            r25 = 1
        L_0x09de:
            int r4 = r15.size     // Catch:{ Exception -> 0x099c, all -> 0x0fed }
            if (r4 <= 0) goto L_0x0a2e
            r10 = 0
            int r4 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1))
            if (r4 < 0) goto L_0x09ee
            long r10 = r15.presentationTimeUs     // Catch:{ Exception -> 0x099c, all -> 0x0fed }
            int r4 = (r10 > r8 ? 1 : (r10 == r8 ? 0 : -1))
            if (r4 >= 0) goto L_0x0a2e
        L_0x09ee:
            r4 = 0
            r15.offset = r4     // Catch:{ Exception -> 0x099c, all -> 0x0fed }
            android.media.MediaExtractor r6 = r14.extractor     // Catch:{ Exception -> 0x099c, all -> 0x0fed }
            int r6 = r6.getSampleFlags()     // Catch:{ Exception -> 0x099c, all -> 0x0fed }
            r15.flags = r6     // Catch:{ Exception -> 0x099c, all -> 0x0fed }
            org.telegram.messenger.video.MP4Builder r6 = r14.mediaMuxer     // Catch:{ Exception -> 0x099c, all -> 0x0fed }
            long r10 = r6.writeSampleData(r1, r13, r15, r4)     // Catch:{ Exception -> 0x099c, all -> 0x0fed }
            r39 = 0
            int r4 = (r10 > r39 ? 1 : (r10 == r39 ? 0 : -1))
            if (r4 == 0) goto L_0x0a2e
            org.telegram.messenger.MediaController$VideoConvertorListener r4 = r14.callback     // Catch:{ Exception -> 0x099c, all -> 0x0fed }
            if (r4 == 0) goto L_0x0a2e
            r6 = r3
            long r3 = r15.presentationTimeUs     // Catch:{ Exception -> 0x099c, all -> 0x0fed }
            r37 = r5
            r38 = r6
            r45 = r13
            r5 = 2500(0x9c4, double:1.235E-320)
            r12 = r80
            long r3 = r3 - r12
            int r46 = (r3 > r31 ? 1 : (r3 == r31 ? 0 : -1))
            if (r46 <= 0) goto L_0x0a1f
            long r3 = r15.presentationTimeUs     // Catch:{ Exception -> 0x0a62, all -> 0x0fed }
            long r31 = r3 - r12
        L_0x0a1f:
            r3 = r31
            org.telegram.messenger.MediaController$VideoConvertorListener r5 = r14.callback     // Catch:{ Exception -> 0x0a62, all -> 0x0fed }
            float r6 = (float) r3     // Catch:{ Exception -> 0x0a62, all -> 0x0fed }
            float r6 = r6 / r17
            float r6 = r6 / r18
            r5.didWriteData(r10, r6)     // Catch:{ Exception -> 0x0a62, all -> 0x0fed }
            r31 = r3
            goto L_0x0a46
        L_0x0a2e:
            r38 = r3
            r37 = r5
            r45 = r13
            r12 = r80
            goto L_0x0a46
        L_0x0a37:
            r38 = r3
            r37 = r5
            r45 = r13
            r12 = r80
            r3 = -1
            if (r4 != r3) goto L_0x0a46
            r5 = r25
            r6 = 1
            goto L_0x0a49
        L_0x0a46:
            r5 = r25
            r6 = 0
        L_0x0a49:
            if (r6 == 0) goto L_0x0a87
            r3 = 2500(0x9c4, double:1.235E-320)
            int r53 = r2.dequeueInputBuffer(r3)     // Catch:{ Exception -> 0x0a62, all -> 0x0fed }
            if (r53 < 0) goto L_0x0a87
            r54 = 0
            r55 = 0
            r56 = 0
            r58 = 4
            r52 = r2
            r52.queueInputBuffer(r53, r54, r55, r56, r58)     // Catch:{ Exception -> 0x0a62, all -> 0x0fed }
            r5 = 1
            goto L_0x0a87
        L_0x0a62:
            r0 = move-exception
            r6 = r79
            r1 = r0
            r10 = r2
        L_0x0a67:
            r65 = r7
        L_0x0a69:
            r11 = r12
            goto L_0x07fe
        L_0x0a6c:
            r0 = move-exception
            r43 = r7
            r6 = r79
            r1 = r0
        L_0x0a72:
            r11 = r10
        L_0x0a73:
            r10 = r2
            goto L_0x07fe
        L_0x0a76:
            r38 = r3
            r35 = r4
            r37 = r5
            r44 = r6
            r43 = r7
            r45 = r13
            r7 = r65
            r12 = r10
            r5 = r25
        L_0x0a87:
            r3 = r26 ^ 1
            r6 = r3
            r25 = r5
            r4 = r27
            r3 = 1
        L_0x0a8f:
            if (r6 != 0) goto L_0x0aa9
            if (r3 == 0) goto L_0x0a94
            goto L_0x0aa9
        L_0x0a94:
            r27 = r4
            r65 = r7
            r10 = r12
            r4 = r35
            r5 = r37
            r3 = r38
            r7 = r43
            r6 = r44
            r13 = r45
            r12 = r72
            goto L_0x0902
        L_0x0aa9:
            r71.checkConversionCanceled()     // Catch:{ Exception -> 0x0e6e, all -> 0x0fed }
            if (r87 == 0) goto L_0x0ab3
            r10 = 22000(0x55f0, double:1.08694E-319)
            r5 = r79
            goto L_0x0ab7
        L_0x0ab3:
            r5 = r79
            r10 = 2500(0x9c4, double:1.235E-320)
        L_0x0ab7:
            int r10 = r5.dequeueOutputBuffer(r15, r10)     // Catch:{ Exception -> 0x0e6c, all -> 0x0fed }
            r11 = -1
            if (r10 != r11) goto L_0x0ad6
            r27 = r1
            r60 = r2
            r48 = r6
            r65 = r7
            r2 = r21
            r7 = r62
            r9 = r64
            r6 = r66
            r3 = r67
            r1 = 0
            r8 = 3
            r42 = 0
            goto L_0x0ccf
        L_0x0ad6:
            r11 = -3
            if (r10 != r11) goto L_0x0b02
            int r11 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0afc, all -> 0x0fed }
            r27 = r1
            r1 = 21
            if (r11 >= r1) goto L_0x0ae5
            java.nio.ByteBuffer[] r22 = r5.getOutputBuffers()     // Catch:{ Exception -> 0x0afc, all -> 0x0fed }
        L_0x0ae5:
            r60 = r2
            r42 = r3
            r48 = r6
            r65 = r7
            r2 = r21
            r7 = r62
            r9 = r64
            r6 = r66
            r3 = r67
        L_0x0af7:
            r1 = 0
            r8 = 3
            r11 = -1
            goto L_0x0ccf
        L_0x0afc:
            r0 = move-exception
            r1 = r0
            r10 = r2
            r6 = r5
            goto L_0x0a67
        L_0x0b02:
            r27 = r1
            r1 = -2
            if (r10 != r1) goto L_0x0b68
            android.media.MediaFormat r1 = r5.getOutputFormat()     // Catch:{ Exception -> 0x0afc, all -> 0x0fed }
            r11 = -5
            if (r4 != r11) goto L_0x0b4f
            if (r1 == 0) goto L_0x0b4f
            org.telegram.messenger.video.MP4Builder r4 = r14.mediaMuxer     // Catch:{ Exception -> 0x0afc, all -> 0x0fed }
            r11 = 0
            int r4 = r4.addTrack(r1, r11)     // Catch:{ Exception -> 0x0afc, all -> 0x0fed }
            r11 = r68
            boolean r47 = r1.containsKey(r11)     // Catch:{ Exception -> 0x0afc, all -> 0x0fed }
            r79 = r3
            if (r47 == 0) goto L_0x0b44
            int r3 = r1.getInteger(r11)     // Catch:{ Exception -> 0x0afc, all -> 0x0fed }
            r47 = r4
            r4 = 1
            if (r3 != r4) goto L_0x0b46
            r3 = r67
            java.nio.ByteBuffer r4 = r1.getByteBuffer(r3)     // Catch:{ Exception -> 0x0afc, all -> 0x0fed }
            r48 = r6
            r6 = r66
            java.nio.ByteBuffer r1 = r1.getByteBuffer(r6)     // Catch:{ Exception -> 0x0afc, all -> 0x0fed }
            int r4 = r4.limit()     // Catch:{ Exception -> 0x0afc, all -> 0x0fed }
            int r1 = r1.limit()     // Catch:{ Exception -> 0x0afc, all -> 0x0fed }
            int r4 = r4 + r1
            r28 = r4
            goto L_0x0b4c
        L_0x0b44:
            r47 = r4
        L_0x0b46:
            r48 = r6
            r6 = r66
            r3 = r67
        L_0x0b4c:
            r4 = r47
            goto L_0x0b59
        L_0x0b4f:
            r79 = r3
            r48 = r6
            r6 = r66
            r3 = r67
            r11 = r68
        L_0x0b59:
            r42 = r79
            r60 = r2
            r65 = r7
            r68 = r11
            r2 = r21
            r7 = r62
            r9 = r64
            goto L_0x0af7
        L_0x0b68:
            r79 = r3
            r48 = r6
            r6 = r66
            r3 = r67
            r11 = r68
            if (r10 < 0) goto L_0x0e4a
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0e6c, all -> 0x0fed }
            r68 = r11
            r11 = 21
            if (r1 >= r11) goto L_0x0b7f
            r1 = r22[r10]     // Catch:{ Exception -> 0x0afc, all -> 0x0fed }
            goto L_0x0b83
        L_0x0b7f:
            java.nio.ByteBuffer r1 = r5.getOutputBuffer(r10)     // Catch:{ Exception -> 0x0e6c, all -> 0x0fed }
        L_0x0b83:
            if (r1 == 0) goto L_0x0e28
            int r11 = r15.size     // Catch:{ Exception -> 0x0e23, all -> 0x0fed }
            r65 = r7
            r7 = 1
            if (r11 <= r7) goto L_0x0cb6
            int r11 = r15.flags     // Catch:{ Exception -> 0x0caa, all -> 0x0fed }
            r11 = r11 & 2
            if (r11 != 0) goto L_0x0c2e
            if (r28 == 0) goto L_0x0bab
            int r11 = r15.flags     // Catch:{ Exception -> 0x0ba6, all -> 0x0fed }
            r11 = r11 & r7
            if (r11 == 0) goto L_0x0bab
            int r7 = r15.offset     // Catch:{ Exception -> 0x0ba6, all -> 0x0fed }
            int r7 = r7 + r28
            r15.offset = r7     // Catch:{ Exception -> 0x0ba6, all -> 0x0fed }
            int r7 = r15.size     // Catch:{ Exception -> 0x0ba6, all -> 0x0fed }
            int r7 = r7 - r28
            r15.size = r7     // Catch:{ Exception -> 0x0ba6, all -> 0x0fed }
            goto L_0x0bab
        L_0x0ba6:
            r0 = move-exception
            r1 = r0
            r10 = r2
            goto L_0x0d2e
        L_0x0bab:
            if (r19 == 0) goto L_0x0bf9
            int r7 = r15.flags     // Catch:{ Exception -> 0x0ba6, all -> 0x0fed }
            r11 = 1
            r7 = r7 & r11
            if (r7 == 0) goto L_0x0bf9
            int r7 = r15.size     // Catch:{ Exception -> 0x0ba6, all -> 0x0fed }
            r11 = 100
            if (r7 <= r11) goto L_0x0bf7
            int r7 = r15.offset     // Catch:{ Exception -> 0x0ba6, all -> 0x0fed }
            r1.position(r7)     // Catch:{ Exception -> 0x0ba6, all -> 0x0fed }
            byte[] r7 = new byte[r11]     // Catch:{ Exception -> 0x0ba6, all -> 0x0fed }
            r1.get(r7)     // Catch:{ Exception -> 0x0ba6, all -> 0x0fed }
            r11 = 0
            r19 = 0
        L_0x0bc6:
            r8 = 96
            if (r11 >= r8) goto L_0x0bf7
            byte r8 = r7[r11]     // Catch:{ Exception -> 0x0ba6, all -> 0x0fed }
            if (r8 != 0) goto L_0x0bf2
            int r8 = r11 + 1
            byte r8 = r7[r8]     // Catch:{ Exception -> 0x0ba6, all -> 0x0fed }
            if (r8 != 0) goto L_0x0bf2
            int r8 = r11 + 2
            byte r8 = r7[r8]     // Catch:{ Exception -> 0x0ba6, all -> 0x0fed }
            if (r8 != 0) goto L_0x0bf2
            int r8 = r11 + 3
            byte r8 = r7[r8]     // Catch:{ Exception -> 0x0ba6, all -> 0x0fed }
            r9 = 1
            if (r8 != r9) goto L_0x0bf2
            int r8 = r19 + 1
            if (r8 <= r9) goto L_0x0bf0
            int r7 = r15.offset     // Catch:{ Exception -> 0x0ba6, all -> 0x0fed }
            int r7 = r7 + r11
            r15.offset = r7     // Catch:{ Exception -> 0x0ba6, all -> 0x0fed }
            int r7 = r15.size     // Catch:{ Exception -> 0x0ba6, all -> 0x0fed }
            int r7 = r7 - r11
            r15.size = r7     // Catch:{ Exception -> 0x0ba6, all -> 0x0fed }
            goto L_0x0bf7
        L_0x0bf0:
            r19 = r8
        L_0x0bf2:
            int r11 = r11 + 1
            r8 = r82
            goto L_0x0bc6
        L_0x0bf7:
            r19 = 0
        L_0x0bf9:
            org.telegram.messenger.video.MP4Builder r7 = r14.mediaMuxer     // Catch:{ Exception -> 0x0caa, all -> 0x0fed }
            r60 = r2
            r8 = 1
            long r1 = r7.writeSampleData(r4, r1, r15, r8)     // Catch:{ Exception -> 0x0ca8, all -> 0x0fed }
            r7 = 0
            int r9 = (r1 > r7 ? 1 : (r1 == r7 ? 0 : -1))
            if (r9 == 0) goto L_0x0CLASSNAME
            org.telegram.messenger.MediaController$VideoConvertorListener r7 = r14.callback     // Catch:{ Exception -> 0x0ca8, all -> 0x0fed }
            if (r7 == 0) goto L_0x0CLASSNAME
            long r7 = r15.presentationTimeUs     // Catch:{ Exception -> 0x0ca8, all -> 0x0fed }
            long r7 = r7 - r12
            int r9 = (r7 > r31 ? 1 : (r7 == r31 ? 0 : -1))
            if (r9 <= 0) goto L_0x0CLASSNAME
            long r7 = r15.presentationTimeUs     // Catch:{ Exception -> 0x0ca8, all -> 0x0fed }
            long r31 = r7 - r12
        L_0x0CLASSNAME:
            r7 = r31
            org.telegram.messenger.MediaController$VideoConvertorListener r9 = r14.callback     // Catch:{ Exception -> 0x0ca8, all -> 0x0fed }
            float r11 = (float) r7     // Catch:{ Exception -> 0x0ca8, all -> 0x0fed }
            float r11 = r11 / r17
            float r11 = r11 / r18
            r9.didWriteData(r1, r11)     // Catch:{ Exception -> 0x0ca8, all -> 0x0fed }
            r31 = r7
        L_0x0CLASSNAME:
            r2 = r21
            r7 = r62
            r9 = r64
            r8 = 3
            goto L_0x0cba
        L_0x0c2e:
            r60 = r2
            r2 = -5
            if (r4 != r2) goto L_0x0CLASSNAME
            int r4 = r15.size     // Catch:{ Exception -> 0x0ca8, all -> 0x0fed }
            byte[] r4 = new byte[r4]     // Catch:{ Exception -> 0x0ca8, all -> 0x0fed }
            int r7 = r15.offset     // Catch:{ Exception -> 0x0ca8, all -> 0x0fed }
            int r8 = r15.size     // Catch:{ Exception -> 0x0ca8, all -> 0x0fed }
            int r7 = r7 + r8
            r1.limit(r7)     // Catch:{ Exception -> 0x0ca8, all -> 0x0fed }
            int r7 = r15.offset     // Catch:{ Exception -> 0x0ca8, all -> 0x0fed }
            r1.position(r7)     // Catch:{ Exception -> 0x0ca8, all -> 0x0fed }
            r1.get(r4)     // Catch:{ Exception -> 0x0ca8, all -> 0x0fed }
            int r1 = r15.size     // Catch:{ Exception -> 0x0ca8, all -> 0x0fed }
            r7 = 1
            int r1 = r1 - r7
        L_0x0c4b:
            r8 = 3
            if (r1 < 0) goto L_0x0CLASSNAME
            if (r1 <= r8) goto L_0x0CLASSNAME
            byte r9 = r4[r1]     // Catch:{ Exception -> 0x0ca8, all -> 0x0fed }
            if (r9 != r7) goto L_0x0CLASSNAME
            int r9 = r1 + -1
            byte r9 = r4[r9]     // Catch:{ Exception -> 0x0ca8, all -> 0x0fed }
            if (r9 != 0) goto L_0x0CLASSNAME
            int r9 = r1 + -2
            byte r9 = r4[r9]     // Catch:{ Exception -> 0x0ca8, all -> 0x0fed }
            if (r9 != 0) goto L_0x0CLASSNAME
            int r9 = r1 + -3
            byte r11 = r4[r9]     // Catch:{ Exception -> 0x0ca8, all -> 0x0fed }
            if (r11 != 0) goto L_0x0CLASSNAME
            java.nio.ByteBuffer r1 = java.nio.ByteBuffer.allocate(r9)     // Catch:{ Exception -> 0x0ca8, all -> 0x0fed }
            int r11 = r15.size     // Catch:{ Exception -> 0x0ca8, all -> 0x0fed }
            int r11 = r11 - r9
            java.nio.ByteBuffer r11 = java.nio.ByteBuffer.allocate(r11)     // Catch:{ Exception -> 0x0ca8, all -> 0x0fed }
            r2 = 0
            java.nio.ByteBuffer r7 = r1.put(r4, r2, r9)     // Catch:{ Exception -> 0x0ca8, all -> 0x0fed }
            r7.position(r2)     // Catch:{ Exception -> 0x0ca8, all -> 0x0fed }
            int r7 = r15.size     // Catch:{ Exception -> 0x0ca8, all -> 0x0fed }
            int r7 = r7 - r9
            java.nio.ByteBuffer r4 = r11.put(r4, r9, r7)     // Catch:{ Exception -> 0x0ca8, all -> 0x0fed }
            r4.position(r2)     // Catch:{ Exception -> 0x0ca8, all -> 0x0fed }
            goto L_0x0c8b
        L_0x0CLASSNAME:
            int r1 = r1 + -1
            r2 = -5
            r7 = 1
            goto L_0x0c4b
        L_0x0CLASSNAME:
            r1 = 0
            r11 = 0
        L_0x0c8b:
            r2 = r21
            r7 = r62
            r9 = r64
            android.media.MediaFormat r4 = android.media.MediaFormat.createVideoFormat(r9, r2, r7)     // Catch:{ Exception -> 0x0ca8, all -> 0x0fed }
            if (r1 == 0) goto L_0x0c9f
            if (r11 == 0) goto L_0x0c9f
            r4.setByteBuffer(r3, r1)     // Catch:{ Exception -> 0x0ca8, all -> 0x0fed }
            r4.setByteBuffer(r6, r11)     // Catch:{ Exception -> 0x0ca8, all -> 0x0fed }
        L_0x0c9f:
            org.telegram.messenger.video.MP4Builder r1 = r14.mediaMuxer     // Catch:{ Exception -> 0x0ca8, all -> 0x0fed }
            r11 = 0
            int r1 = r1.addTrack(r4, r11)     // Catch:{ Exception -> 0x0ca8, all -> 0x0fed }
            r4 = r1
            goto L_0x0cba
        L_0x0ca8:
            r0 = move-exception
            goto L_0x0cad
        L_0x0caa:
            r0 = move-exception
            r60 = r2
        L_0x0cad:
            r1 = r0
            r6 = r5
            r11 = r12
            r2 = r23
            r10 = r60
            goto L_0x0efa
        L_0x0cb6:
            r60 = r2
            goto L_0x0CLASSNAME
        L_0x0cba:
            int r1 = r15.flags     // Catch:{ Exception -> 0x0e1e, all -> 0x0fed }
            r1 = r1 & 4
            if (r1 == 0) goto L_0x0cc4
            r1 = 0
            r42 = 1
            goto L_0x0cc7
        L_0x0cc4:
            r1 = 0
            r42 = 0
        L_0x0cc7:
            r5.releaseOutputBuffer(r10, r1)     // Catch:{ Exception -> 0x0e1e, all -> 0x0fed }
            r20 = r42
            r11 = -1
            r42 = r79
        L_0x0ccf:
            if (r10 == r11) goto L_0x0ceb
            r21 = r2
            r67 = r3
            r79 = r5
            r66 = r6
            r62 = r7
            r64 = r9
            r1 = r27
            r3 = r42
            r6 = r48
            r2 = r60
        L_0x0ce5:
            r7 = r65
            r8 = r82
            goto L_0x0a8f
        L_0x0ceb:
            if (r26 != 0) goto L_0x0dff
            r21 = r2
            r10 = r60
            r1 = 2500(0x9c4, double:1.235E-320)
            int r8 = r10.dequeueOutputBuffer(r15, r1)     // Catch:{ Exception -> 0x0df7, all -> 0x0fed }
            if (r8 != r11) goto L_0x0d04
            r67 = r3
            r11 = r12
            r3 = r63
            r39 = 0
        L_0x0d00:
            r48 = 0
            goto L_0x0e0a
        L_0x0d04:
            r1 = -3
            if (r8 != r1) goto L_0x0d0c
        L_0x0d07:
            r67 = r3
            r11 = r12
            goto L_0x0e06
        L_0x0d0c:
            r1 = -2
            if (r8 != r1) goto L_0x0d31
            android.media.MediaFormat r1 = r10.getOutputFormat()     // Catch:{ Exception -> 0x0d2c, all -> 0x0fed }
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0d2c, all -> 0x0fed }
            if (r2 == 0) goto L_0x0d07
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0d2c, all -> 0x0fed }
            r2.<init>()     // Catch:{ Exception -> 0x0d2c, all -> 0x0fed }
            java.lang.String r8 = "newFormat = "
            r2.append(r8)     // Catch:{ Exception -> 0x0d2c, all -> 0x0fed }
            r2.append(r1)     // Catch:{ Exception -> 0x0d2c, all -> 0x0fed }
            java.lang.String r1 = r2.toString()     // Catch:{ Exception -> 0x0d2c, all -> 0x0fed }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x0d2c, all -> 0x0fed }
            goto L_0x0d07
        L_0x0d2c:
            r0 = move-exception
            r1 = r0
        L_0x0d2e:
            r6 = r5
            goto L_0x0a69
        L_0x0d31:
            if (r8 < 0) goto L_0x0dd8
            int r1 = r15.size     // Catch:{ Exception -> 0x0df7, all -> 0x0fed }
            if (r1 == 0) goto L_0x0d39
            r1 = 1
            goto L_0x0d3a
        L_0x0d39:
            r1 = 0
        L_0x0d3a:
            r39 = 0
            int r2 = (r82 > r39 ? 1 : (r82 == r39 ? 0 : -1))
            if (r2 <= 0) goto L_0x0d59
            long r11 = r15.presentationTimeUs     // Catch:{ Exception -> 0x0d54, all -> 0x0fed }
            int r13 = (r11 > r82 ? 1 : (r11 == r82 ? 0 : -1))
            if (r13 < 0) goto L_0x0d59
            int r1 = r15.flags     // Catch:{ Exception -> 0x0d54, all -> 0x0fed }
            r1 = r1 | 4
            r15.flags = r1     // Catch:{ Exception -> 0x0d54, all -> 0x0fed }
            r11 = r80
            r1 = 0
            r25 = 1
            r26 = 1
            goto L_0x0d5b
        L_0x0d54:
            r0 = move-exception
            r11 = r80
            goto L_0x0dfb
        L_0x0d59:
            r11 = r80
        L_0x0d5b:
            r39 = 0
            int r13 = (r11 > r39 ? 1 : (r11 == r39 ? 0 : -1))
            if (r13 <= 0) goto L_0x0d9a
            r49 = -1
            int r13 = (r33 > r49 ? 1 : (r33 == r49 ? 0 : -1))
            if (r13 != 0) goto L_0x0d9a
            r67 = r3
            long r2 = r15.presentationTimeUs     // Catch:{ Exception -> 0x0d98, all -> 0x0fed }
            int r13 = (r2 > r11 ? 1 : (r2 == r11 ? 0 : -1))
            if (r13 >= 0) goto L_0x0d93
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0d98, all -> 0x0fed }
            if (r1 == 0) goto L_0x0d91
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0d98, all -> 0x0fed }
            r1.<init>()     // Catch:{ Exception -> 0x0d98, all -> 0x0fed }
            java.lang.String r2 = "drop frame startTime = "
            r1.append(r2)     // Catch:{ Exception -> 0x0d98, all -> 0x0fed }
            r1.append(r11)     // Catch:{ Exception -> 0x0d98, all -> 0x0fed }
            java.lang.String r2 = " present time = "
            r1.append(r2)     // Catch:{ Exception -> 0x0d98, all -> 0x0fed }
            long r2 = r15.presentationTimeUs     // Catch:{ Exception -> 0x0d98, all -> 0x0fed }
            r1.append(r2)     // Catch:{ Exception -> 0x0d98, all -> 0x0fed }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x0d98, all -> 0x0fed }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x0d98, all -> 0x0fed }
        L_0x0d91:
            r1 = 0
            goto L_0x0d9c
        L_0x0d93:
            long r2 = r15.presentationTimeUs     // Catch:{ Exception -> 0x0d98, all -> 0x0fed }
            r33 = r2
            goto L_0x0d9c
        L_0x0d98:
            r0 = move-exception
            goto L_0x0dfb
        L_0x0d9a:
            r67 = r3
        L_0x0d9c:
            r10.releaseOutputBuffer(r8, r1)     // Catch:{ Exception -> 0x0dd6, all -> 0x0fed }
            if (r1 == 0) goto L_0x0dc0
            r24.awaitNewImage()     // Catch:{ Exception -> 0x0da6, all -> 0x0fed }
            r1 = 0
            goto L_0x0dac
        L_0x0da6:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)     // Catch:{ Exception -> 0x0dd6, all -> 0x0fed }
            r1 = 1
        L_0x0dac:
            if (r1 != 0) goto L_0x0dc0
            r24.drawImage()     // Catch:{ Exception -> 0x0dd6, all -> 0x0fed }
            long r1 = r15.presentationTimeUs     // Catch:{ Exception -> 0x0dd6, all -> 0x0fed }
            r49 = 1000(0x3e8, double:4.94E-321)
            long r1 = r1 * r49
            r3 = r63
            r3.setPresentationTime(r1)     // Catch:{ Exception -> 0x0df2, all -> 0x0fed }
            r3.swapBuffers()     // Catch:{ Exception -> 0x0df2, all -> 0x0fed }
            goto L_0x0dc2
        L_0x0dc0:
            r3 = r63
        L_0x0dc2:
            int r1 = r15.flags     // Catch:{ Exception -> 0x0df2, all -> 0x0fed }
            r1 = r1 & 4
            if (r1 == 0) goto L_0x0e0a
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0df2, all -> 0x0fed }
            if (r1 == 0) goto L_0x0dd1
            java.lang.String r1 = "decoder stream end"
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x0df2, all -> 0x0fed }
        L_0x0dd1:
            r5.signalEndOfInputStream()     // Catch:{ Exception -> 0x0df2, all -> 0x0fed }
            goto L_0x0d00
        L_0x0dd6:
            r0 = move-exception
            goto L_0x0df9
        L_0x0dd8:
            r11 = r12
            r3 = r63
            java.lang.RuntimeException r1 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x0df2, all -> 0x0fed }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0df2, all -> 0x0fed }
            r2.<init>()     // Catch:{ Exception -> 0x0df2, all -> 0x0fed }
            java.lang.String r4 = "unexpected result from decoder.dequeueOutputBuffer: "
            r2.append(r4)     // Catch:{ Exception -> 0x0df2, all -> 0x0fed }
            r2.append(r8)     // Catch:{ Exception -> 0x0df2, all -> 0x0fed }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0df2, all -> 0x0fed }
            r1.<init>(r2)     // Catch:{ Exception -> 0x0df2, all -> 0x0fed }
            throw r1     // Catch:{ Exception -> 0x0df2, all -> 0x0fed }
        L_0x0df2:
            r0 = move-exception
            r1 = r0
            r63 = r3
            goto L_0x0dfc
        L_0x0df7:
            r0 = move-exception
        L_0x0df8:
            r11 = r12
        L_0x0df9:
            r3 = r63
        L_0x0dfb:
            r1 = r0
        L_0x0dfc:
            r6 = r5
            goto L_0x07fe
        L_0x0dff:
            r21 = r2
            r67 = r3
            r11 = r12
            r10 = r60
        L_0x0e06:
            r3 = r63
            r39 = 0
        L_0x0e0a:
            r63 = r3
            r79 = r5
            r66 = r6
            r62 = r7
            r64 = r9
            r2 = r10
            r12 = r11
            r1 = r27
            r3 = r42
            r6 = r48
            goto L_0x0ce5
        L_0x0e1e:
            r0 = move-exception
            r11 = r12
            r10 = r60
            goto L_0x0df9
        L_0x0e23:
            r0 = move-exception
            r10 = r2
            r65 = r7
            goto L_0x0df8
        L_0x0e28:
            r1 = r2
            r65 = r7
            r11 = r12
            r3 = r63
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x0e67, all -> 0x0fed }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0e67, all -> 0x0fed }
            r4.<init>()     // Catch:{ Exception -> 0x0e67, all -> 0x0fed }
            java.lang.String r6 = "encoderOutputBuffer "
            r4.append(r6)     // Catch:{ Exception -> 0x0e67, all -> 0x0fed }
            r4.append(r10)     // Catch:{ Exception -> 0x0e67, all -> 0x0fed }
            java.lang.String r6 = " was null"
            r4.append(r6)     // Catch:{ Exception -> 0x0e67, all -> 0x0fed }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0e67, all -> 0x0fed }
            r2.<init>(r4)     // Catch:{ Exception -> 0x0e67, all -> 0x0fed }
            throw r2     // Catch:{ Exception -> 0x0e67, all -> 0x0fed }
        L_0x0e4a:
            r1 = r2
            r65 = r7
            r11 = r12
            r3 = r63
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x0e67, all -> 0x0fed }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0e67, all -> 0x0fed }
            r4.<init>()     // Catch:{ Exception -> 0x0e67, all -> 0x0fed }
            java.lang.String r6 = "unexpected result from encoder.dequeueOutputBuffer: "
            r4.append(r6)     // Catch:{ Exception -> 0x0e67, all -> 0x0fed }
            r4.append(r10)     // Catch:{ Exception -> 0x0e67, all -> 0x0fed }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0e67, all -> 0x0fed }
            r2.<init>(r4)     // Catch:{ Exception -> 0x0e67, all -> 0x0fed }
            throw r2     // Catch:{ Exception -> 0x0e67, all -> 0x0fed }
        L_0x0e67:
            r0 = move-exception
            r10 = r1
            r63 = r3
            goto L_0x0e80
        L_0x0e6c:
            r0 = move-exception
            goto L_0x0e71
        L_0x0e6e:
            r0 = move-exception
            r5 = r79
        L_0x0e71:
            r1 = r2
            r65 = r7
            r11 = r12
            goto L_0x0e7d
        L_0x0e76:
            r0 = move-exception
            r5 = r79
            r1 = r2
            r43 = r7
            r11 = r10
        L_0x0e7d:
            r3 = r63
            r10 = r1
        L_0x0e80:
            r6 = r5
            r2 = r23
            goto L_0x0eb5
        L_0x0e84:
            r0 = move-exception
            r5 = r79
            r11 = r80
            r1 = r2
            goto L_0x0e92
        L_0x0e8b:
            r0 = move-exception
            r5 = r79
            r11 = r80
            r1 = r60
        L_0x0e92:
            r3 = r63
            r10 = r1
            r6 = r5
            r2 = r23
            goto L_0x0eb3
        L_0x0e99:
            r0 = move-exception
            r5 = r79
            r11 = r80
            r1 = r60
            r3 = r63
            r10 = r1
            goto L_0x0eae
        L_0x0ea4:
            r0 = move-exception
            r1 = r2
            r3 = r4
            r5 = r8
            r11 = r9
            r65 = r21
            r10 = r1
            r63 = r3
        L_0x0eae:
            r6 = r5
            r2 = r23
            r24 = 0
        L_0x0eb3:
            r43 = 0
        L_0x0eb5:
            r1 = r0
            goto L_0x0efa
        L_0x0eb7:
            r0 = move-exception
            r3 = r4
            r5 = r8
            r11 = r9
            r65 = r21
            r1 = r0
            r63 = r3
            r6 = r5
            r2 = r23
            r10 = 0
            r24 = 0
        L_0x0ec6:
            r43 = 0
            goto L_0x0efa
        L_0x0ec9:
            r0 = move-exception
            r5 = r8
            r11 = r9
            r65 = r21
            r1 = r0
            r6 = r5
            r2 = r23
            goto L_0x0ef3
        L_0x0ed3:
            r0 = move-exception
            r11 = r9
            r65 = r21
            r1 = r0
            r2 = r23
            goto L_0x0ef2
        L_0x0edb:
            r0 = move-exception
            r11 = r9
            r65 = r21
            goto L_0x0ef1
        L_0x0ee0:
            r0 = move-exception
            r65 = r6
            r11 = r9
            goto L_0x0ef1
        L_0x0ee5:
            r0 = move-exception
            r65 = r6
            r11 = r9
            goto L_0x0eef
        L_0x0eea:
            r0 = move-exception
            r11 = r80
            r65 = r6
        L_0x0eef:
            r2 = r79
        L_0x0ef1:
            r1 = r0
        L_0x0ef2:
            r6 = 0
        L_0x0ef3:
            r10 = 0
            r24 = 0
            r43 = 0
            r63 = 0
        L_0x0efa:
            boolean r3 = r1 instanceof java.lang.IllegalStateException     // Catch:{ Exception -> 0x0var_, all -> 0x0fed }
            if (r3 == 0) goto L_0x0var_
            if (r87 != 0) goto L_0x0var_
            r30 = 1
            goto L_0x0var_
        L_0x0var_:
            r30 = 0
        L_0x0var_:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0f6b, all -> 0x0fed }
            r3.<init>()     // Catch:{ Exception -> 0x0f6b, all -> 0x0fed }
            r4 = r36
            r3.append(r4)     // Catch:{ Exception -> 0x0var_, all -> 0x0fed }
            r3.append(r2)     // Catch:{ Exception -> 0x0var_, all -> 0x0fed }
            java.lang.String r5 = " framerate: "
            r3.append(r5)     // Catch:{ Exception -> 0x0var_, all -> 0x0fed }
            r8 = r78
            r3.append(r8)     // Catch:{ Exception -> 0x0f5d, all -> 0x0fed }
            java.lang.String r5 = " size: "
            r3.append(r5)     // Catch:{ Exception -> 0x0f5d, all -> 0x0fed }
            r5 = r77
            r3.append(r5)     // Catch:{ Exception -> 0x0var_, all -> 0x0fed }
            java.lang.String r7 = "x"
            r3.append(r7)     // Catch:{ Exception -> 0x0var_, all -> 0x0fed }
            r7 = r76
            r3.append(r7)     // Catch:{ Exception -> 0x0f9c, all -> 0x0fed }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x0f9c, all -> 0x0fed }
            org.telegram.messenger.FileLog.e((java.lang.String) r3)     // Catch:{ Exception -> 0x0f9c, all -> 0x0fed }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)     // Catch:{ Exception -> 0x0f9c, all -> 0x0fed }
            r1 = r6
            r6 = r30
            r42 = 1
        L_0x0f3f:
            android.media.MediaExtractor r3 = r14.extractor     // Catch:{ Exception -> 0x0var_, all -> 0x0fed }
            r9 = r65
            r3.unselectTrack(r9)     // Catch:{ Exception -> 0x0var_, all -> 0x0fed }
            if (r10 == 0) goto L_0x0f4e
            r10.stop()     // Catch:{ Exception -> 0x0var_, all -> 0x0fed }
            r10.release()     // Catch:{ Exception -> 0x0var_, all -> 0x0fed }
        L_0x0f4e:
            r30 = r6
            r6 = r24
            r41 = r63
            goto L_0x0var_
        L_0x0var_:
            r0 = move-exception
            r1 = r0
            goto L_0x0fff
        L_0x0var_:
            r0 = move-exception
            r7 = r76
            goto L_0x0f9d
        L_0x0f5d:
            r0 = move-exception
            r7 = r76
            r5 = r77
            goto L_0x0f9d
        L_0x0var_:
            r0 = move-exception
            r7 = r76
            r5 = r77
            r8 = r78
            goto L_0x0f9d
        L_0x0f6b:
            r0 = move-exception
            r7 = r76
            r5 = r77
            r8 = r78
            r4 = r36
            goto L_0x0f9d
        L_0x0var_:
            r0 = move-exception
            r7 = r76
            r5 = r77
            r8 = r78
            r4 = r36
            goto L_0x0ffd
        L_0x0var_:
            r7 = r76
            r5 = r77
            r8 = r78
            r11 = r80
            r4 = r36
            r2 = r79
            r1 = 0
            r6 = 0
            r30 = 0
            r41 = 0
            r42 = 0
            r43 = 0
        L_0x0var_:
            if (r6 == 0) goto L_0x0fa2
            r6.release()     // Catch:{ Exception -> 0x0f9c, all -> 0x0fed }
            goto L_0x0fa2
        L_0x0f9c:
            r0 = move-exception
        L_0x0f9d:
            r1 = r0
            r6 = r30
            goto L_0x0fff
        L_0x0fa2:
            if (r41 == 0) goto L_0x0fa7
            r41.release()     // Catch:{ Exception -> 0x0f9c, all -> 0x0fed }
        L_0x0fa7:
            if (r1 == 0) goto L_0x0faf
            r1.stop()     // Catch:{ Exception -> 0x0f9c, all -> 0x0fed }
            r1.release()     // Catch:{ Exception -> 0x0f9c, all -> 0x0fed }
        L_0x0faf:
            if (r43 == 0) goto L_0x0fb4
            r43.release()     // Catch:{ Exception -> 0x0f9c, all -> 0x0fed }
        L_0x0fb4:
            r71.checkConversionCanceled()     // Catch:{ Exception -> 0x0f9c, all -> 0x0fed }
            r13 = r30
        L_0x0fb9:
            r6 = r42
        L_0x0fbb:
            android.media.MediaExtractor r1 = r14.extractor
            if (r1 == 0) goto L_0x0fc2
            r1.release()
        L_0x0fc2:
            org.telegram.messenger.video.MP4Builder r1 = r14.mediaMuxer
            if (r1 == 0) goto L_0x0fcf
            r1.finishMovie()     // Catch:{ Exception -> 0x0fca }
            goto L_0x0fcf
        L_0x0fca:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x0fcf:
            r9 = r2
            r69 = r7
            r7 = r5
            r5 = r6
            r6 = r69
            goto L_0x1045
        L_0x0fd8:
            r0 = move-exception
            r8 = r10
            r5 = r11
            r7 = r12
            r4 = r13
            goto L_0x0fea
        L_0x0fde:
            r0 = move-exception
            r8 = r10
            r5 = r11
            r4 = r13
            r69 = r6
            goto L_0x0ff8
        L_0x0fe5:
            r0 = move-exception
            r4 = r1
            r8 = r10
            r5 = r11
            r7 = r12
        L_0x0fea:
            r11 = r80
            goto L_0x0ffb
        L_0x0fed:
            r0 = move-exception
            r2 = r0
            r1 = r14
            goto L_0x1071
        L_0x0ff2:
            r0 = move-exception
            r4 = r1
            r5 = r11
            r69 = r7
            r8 = r10
        L_0x0ff8:
            r7 = r12
            r11 = r69
        L_0x0ffb:
            r2 = r79
        L_0x0ffd:
            r1 = r0
        L_0x0ffe:
            r6 = 0
        L_0x0fff:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x106d }
            r3.<init>()     // Catch:{ all -> 0x106d }
            r3.append(r4)     // Catch:{ all -> 0x106d }
            r3.append(r2)     // Catch:{ all -> 0x106d }
            java.lang.String r4 = " framerate: "
            r3.append(r4)     // Catch:{ all -> 0x106d }
            r3.append(r8)     // Catch:{ all -> 0x106d }
            java.lang.String r4 = " size: "
            r3.append(r4)     // Catch:{ all -> 0x106d }
            r3.append(r5)     // Catch:{ all -> 0x106d }
            java.lang.String r4 = "x"
            r3.append(r4)     // Catch:{ all -> 0x106d }
            r3.append(r7)     // Catch:{ all -> 0x106d }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x106d }
            org.telegram.messenger.FileLog.e((java.lang.String) r3)     // Catch:{ all -> 0x106d }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)     // Catch:{ all -> 0x106d }
            android.media.MediaExtractor r1 = r14.extractor
            if (r1 == 0) goto L_0x1033
            r1.release()
        L_0x1033:
            org.telegram.messenger.video.MP4Builder r1 = r14.mediaMuxer
            if (r1 == 0) goto L_0x1040
            r1.finishMovie()     // Catch:{ Exception -> 0x103b }
            goto L_0x1040
        L_0x103b:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x1040:
            r9 = r2
            r13 = r6
            r6 = r7
            r7 = r5
            r5 = 1
        L_0x1045:
            if (r13 == 0) goto L_0x106c
            r17 = 1
            r1 = r71
            r2 = r72
            r3 = r73
            r4 = r74
            r5 = r75
            r8 = r78
            r10 = r80
            r12 = r82
            r14 = r84
            r16 = r86
            r18 = r88
            r19 = r89
            r20 = r90
            r21 = r91
            r22 = r92
            boolean r1 = r1.convertVideoInternal(r2, r3, r4, r5, r6, r7, r8, r9, r10, r12, r14, r16, r17, r18, r19, r20, r21, r22)
            return r1
        L_0x106c:
            return r5
        L_0x106d:
            r0 = move-exception
            r1 = r71
            r2 = r0
        L_0x1071:
            android.media.MediaExtractor r3 = r1.extractor
            if (r3 == 0) goto L_0x1078
            r3.release()
        L_0x1078:
            org.telegram.messenger.video.MP4Builder r3 = r1.mediaMuxer
            if (r3 == 0) goto L_0x1085
            r3.finishMovie()     // Catch:{ Exception -> 0x1080 }
            goto L_0x1085
        L_0x1080:
            r0 = move-exception
            r3 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)
        L_0x1085:
            goto L_0x1087
        L_0x1086:
            throw r2
        L_0x1087:
            goto L_0x1086
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.video.MediaCodecVideoConvertor.convertVideoInternal(java.lang.String, java.io.File, int, boolean, int, int, int, int, long, long, long, boolean, boolean, org.telegram.messenger.MediaController$SavedFilterState, java.lang.String, java.util.ArrayList, boolean, org.telegram.messenger.MediaController$CropState):boolean");
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
