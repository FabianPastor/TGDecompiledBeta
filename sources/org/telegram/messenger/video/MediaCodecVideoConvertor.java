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
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v126, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v11, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v99, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v12, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v100, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v185, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v105, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v106, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v13, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v88, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v133, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v189, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v134, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v91, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v95, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v97, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v100, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v101, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v117, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v120, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v222, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v223, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v177, resolved type: long} */
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
    /* JADX WARNING: Code restructure failed: missing block: B:397:0x074f, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:398:0x0750, code lost:
        r1 = r0;
        r11 = r9;
        r65 = r21;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:433:0x0817, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:434:0x0818, code lost:
        r6 = r79;
        r11 = r80;
        r1 = r0;
        r10 = r2;
        r43 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:461:0x089b, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:470:0x08bb, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:481:0x08d8, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:482:0x08d9, code lost:
        r8 = r82;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:483:0x08dd, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:484:0x08de, code lost:
        r8 = r82;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:487:0x08f3, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:488:0x08f4, code lost:
        r8 = r82;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:489:0x08f6, code lost:
        r6 = r79;
        r11 = r80;
        r1 = r0;
        r10 = r2;
        r2 = r23;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:508:0x094e, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:509:0x094f, code lost:
        r6 = r79;
        r1 = r0;
        r43 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:530:0x09be, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:531:0x09bf, code lost:
        r6 = r79;
        r11 = r80;
        r1 = r0;
        r10 = r2;
        r65 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x017e, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:570:0x0a84, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:571:0x0a85, code lost:
        r6 = r79;
        r1 = r0;
        r10 = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:0x017f, code lost:
        r9 = r76;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:58:0x0181, code lost:
        r6 = r80;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:0x0183, code lost:
        r1 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:601:0x0b1e, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:602:0x0b1f, code lost:
        r1 = r0;
        r10 = r2;
        r6 = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:60:0x0184, code lost:
        r11 = r51;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:643:0x0bc8, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:644:0x0bc9, code lost:
        r1 = r0;
        r10 = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:69:0x01a9, code lost:
        r6 = r8;
        r7 = r9;
        r8 = r10;
        r1 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:703:0x0cca, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:704:0x0ccc, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:705:0x0ccd, code lost:
        r60 = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:706:0x0ccf, code lost:
        r1 = r0;
        r6 = r5;
        r11 = r12;
        r2 = r23;
        r10 = r60;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:734:0x0d4e, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:735:0x0d4f, code lost:
        r1 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:736:0x0d50, code lost:
        r6 = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:750:0x0d76, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:751:0x0d77, code lost:
        r11 = r80;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:767:0x0dba, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:775:0x0dc8, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:778:?, code lost:
        org.telegram.messenger.FileLog.e((java.lang.Throwable) r0);
        r1 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:791:0x0df8, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:795:0x0e14, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:796:0x0e15, code lost:
        r1 = r0;
        r63 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:797:0x0e19, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:798:0x0e1a, code lost:
        r11 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:799:0x0e1b, code lost:
        r3 = r63;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:800:0x0e1d, code lost:
        r1 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:801:0x0e1e, code lost:
        r6 = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:805:0x0e40, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:806:0x0e41, code lost:
        r11 = r12;
        r10 = r60;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:807:0x0e45, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:808:0x0e46, code lost:
        r10 = r2;
        r65 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:815:0x0e89, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:816:0x0e8a, code lost:
        r10 = r2;
        r63 = r63;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:817:0x0e8e, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:818:0x0e90, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:819:0x0e91, code lost:
        r5 = r79;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:820:0x0e93, code lost:
        r1 = r2;
        r65 = r7;
        r11 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:821:0x0e98, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:822:0x0e99, code lost:
        r5 = r79;
        r1 = r2;
        r43 = r7;
        r11 = r10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:823:0x0e9f, code lost:
        r3 = r63;
        r10 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:824:0x0ea2, code lost:
        r6 = r5;
        r2 = r23;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:825:0x0ea6, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:826:0x0ea7, code lost:
        r5 = r79;
        r11 = r80;
        r1 = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:827:0x0ead, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:828:0x0eae, code lost:
        r5 = r79;
        r11 = r80;
        r1 = r60;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:829:0x0eb4, code lost:
        r3 = r63;
        r10 = r1;
        r6 = r5;
        r2 = r23;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:830:0x0ebb, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:831:0x0ebc, code lost:
        r5 = r79;
        r11 = r80;
        r3 = r63;
        r10 = r60;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:832:0x0ec6, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:833:0x0ec7, code lost:
        r5 = r8;
        r11 = r9;
        r65 = r21;
        r10 = r2;
        r63 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:834:0x0ed0, code lost:
        r6 = r5;
        r2 = r23;
        r24 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:835:0x0ed5, code lost:
        r43 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:836:0x0ed7, code lost:
        r1 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:837:0x0ed9, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:838:0x0eda, code lost:
        r11 = r9;
        r65 = r21;
        r1 = r0;
        r63 = r4;
        r6 = r8;
        r2 = r23;
        r10 = null;
        r24 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:839:0x0ee8, code lost:
        r43 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:840:0x0eeb, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:841:0x0eec, code lost:
        r11 = r9;
        r65 = r21;
        r1 = r0;
        r6 = r8;
        r2 = r23;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:842:0x0ef5, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:843:0x0ef6, code lost:
        r11 = r9;
        r65 = r21;
        r1 = r0;
        r2 = r23;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:844:0x0efd, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:845:0x0efe, code lost:
        r11 = r9;
        r65 = r21;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:846:0x0var_, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:847:0x0var_, code lost:
        r65 = r6;
        r11 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:848:0x0var_, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:849:0x0var_, code lost:
        r65 = r6;
        r11 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:850:0x0f0c, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:851:0x0f0d, code lost:
        r11 = r80;
        r65 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:852:0x0var_, code lost:
        r2 = r79;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:853:0x0var_, code lost:
        r1 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:854:0x0var_, code lost:
        r6 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:855:0x0var_, code lost:
        r10 = null;
        r24 = null;
        r43 = null;
        r63 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:860:0x0var_, code lost:
        r30 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:882:0x0var_, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:883:0x0var_, code lost:
        r1 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:884:0x0f7b, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:885:0x0f7c, code lost:
        r7 = r76;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:886:0x0f7f, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:887:0x0var_, code lost:
        r7 = r76;
        r5 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:888:0x0var_, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:889:0x0var_, code lost:
        r7 = r76;
        r5 = r77;
        r8 = r78;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:890:0x0f8d, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:891:0x0f8e, code lost:
        r7 = r76;
        r5 = r77;
        r8 = r78;
        r4 = r36;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:892:0x0var_, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:893:0x0var_, code lost:
        r7 = r76;
        r5 = r77;
        r8 = r78;
        r4 = r36;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:898:0x0fbe, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:899:0x0fbf, code lost:
        r1 = r0;
        r6 = r30;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:919:0x0ffa, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:920:0x0ffb, code lost:
        r8 = r10;
        r5 = r11;
        r7 = r12;
        r4 = r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:921:0x1000, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:922:0x1001, code lost:
        r8 = r10;
        r5 = r11;
        r4 = r13;
        r69 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:923:0x1007, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:924:0x1008, code lost:
        r4 = "bitrate: ";
        r8 = r10;
        r5 = r11;
        r7 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:925:0x100c, code lost:
        r11 = r80;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:926:0x100f, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:927:0x1010, code lost:
        r2 = r0;
        r1 = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:928:0x1014, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:929:0x1015, code lost:
        r4 = "bitrate: ";
        r5 = r11;
        r69 = r7;
        r8 = r10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:930:0x101a, code lost:
        r7 = r12;
        r11 = r69;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:931:0x101d, code lost:
        r2 = r79;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:932:0x101f, code lost:
        r1 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:933:0x1020, code lost:
        r6 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:935:?, code lost:
        org.telegram.messenger.FileLog.e(r4 + r2 + " framerate: " + r8 + " size: " + r5 + "x" + r7);
        org.telegram.messenger.FileLog.e((java.lang.Throwable) r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:936:0x104e, code lost:
        r1 = r14.extractor;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:937:0x1050, code lost:
        if (r1 != null) goto L_0x1052;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:938:0x1052, code lost:
        r1.release();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:939:0x1055, code lost:
        r1 = r14.mediaMuxer;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:940:0x1057, code lost:
        if (r1 != null) goto L_0x1059;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:942:?, code lost:
        r1.finishMovie();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:943:0x105d, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:944:0x105e, code lost:
        org.telegram.messenger.FileLog.e((java.lang.Throwable) r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:945:0x1062, code lost:
        r9 = r2;
        r13 = r6;
        r6 = r7;
        r7 = r5;
        r5 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:950:0x108f, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:951:0x1090, code lost:
        r1 = r71;
        r2 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:952:0x1093, code lost:
        r3 = r1.extractor;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:953:0x1095, code lost:
        if (r3 != null) goto L_0x1097;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:954:0x1097, code lost:
        r3.release();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:955:0x109a, code lost:
        r3 = r1.mediaMuxer;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:956:0x109c, code lost:
        if (r3 != null) goto L_0x109e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:958:?, code lost:
        r3.finishMovie();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:959:0x10a2, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:960:0x10a3, code lost:
        org.telegram.messenger.FileLog.e((java.lang.Throwable) r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:961:0x10a8, code lost:
        throw r2;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:228:0x0444 A[Catch:{ Exception -> 0x0499, all -> 0x100f }] */
    /* JADX WARNING: Removed duplicated region for block: B:229:0x045e A[Catch:{ Exception -> 0x0499, all -> 0x100f }] */
    /* JADX WARNING: Removed duplicated region for block: B:285:0x0586 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:301:0x05c7 A[SYNTHETIC, Splitter:B:301:0x05c7] */
    /* JADX WARNING: Removed duplicated region for block: B:306:0x05db A[Catch:{ Exception -> 0x05cb, all -> 0x100f }] */
    /* JADX WARNING: Removed duplicated region for block: B:308:0x05e0 A[Catch:{ Exception -> 0x05cb, all -> 0x100f }] */
    /* JADX WARNING: Removed duplicated region for block: B:362:0x06cc  */
    /* JADX WARNING: Removed duplicated region for block: B:450:0x085a  */
    /* JADX WARNING: Removed duplicated region for block: B:452:0x085d  */
    /* JADX WARNING: Removed duplicated region for block: B:486:0x08e8  */
    /* JADX WARNING: Removed duplicated region for block: B:494:0x090e  */
    /* JADX WARNING: Removed duplicated region for block: B:495:0x0910  */
    /* JADX WARNING: Removed duplicated region for block: B:499:0x0926 A[Catch:{ Exception -> 0x0e98, all -> 0x100f }] */
    /* JADX WARNING: Removed duplicated region for block: B:511:0x0958  */
    /* JADX WARNING: Removed duplicated region for block: B:578:0x0a98  */
    /* JADX WARNING: Removed duplicated region for block: B:581:0x0ab3 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:586:0x0ad0  */
    /* JADX WARNING: Removed duplicated region for block: B:587:0x0ad5  */
    /* JADX WARNING: Removed duplicated region for block: B:592:0x0ae0  */
    /* JADX WARNING: Removed duplicated region for block: B:593:0x0af8  */
    /* JADX WARNING: Removed duplicated region for block: B:711:0x0ce2 A[Catch:{ Exception -> 0x0e40, all -> 0x100f }] */
    /* JADX WARNING: Removed duplicated region for block: B:712:0x0ce6 A[Catch:{ Exception -> 0x0e40, all -> 0x100f }] */
    /* JADX WARNING: Removed duplicated region for block: B:716:0x0cf3  */
    /* JADX WARNING: Removed duplicated region for block: B:718:0x0d0d  */
    /* JADX WARNING: Removed duplicated region for block: B:757:0x0d89  */
    /* JADX WARNING: Removed duplicated region for block: B:768:0x0dbc  */
    /* JADX WARNING: Removed duplicated region for block: B:772:0x0dc3 A[SYNTHETIC, Splitter:B:772:0x0dc3] */
    /* JADX WARNING: Removed duplicated region for block: B:787:0x0dea A[Catch:{ Exception -> 0x0e14, all -> 0x100f }] */
    /* JADX WARNING: Removed duplicated region for block: B:859:0x0var_ A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:880:0x0f6a A[Catch:{ Exception -> 0x0var_, all -> 0x100f }] */
    /* JADX WARNING: Removed duplicated region for block: B:894:0x0fa2  */
    /* JADX WARNING: Removed duplicated region for block: B:896:0x0fba A[SYNTHETIC, Splitter:B:896:0x0fba] */
    /* JADX WARNING: Removed duplicated region for block: B:901:0x0fc6 A[Catch:{ Exception -> 0x0fbe, all -> 0x100f }] */
    /* JADX WARNING: Removed duplicated region for block: B:903:0x0fcb A[Catch:{ Exception -> 0x0fbe, all -> 0x100f }] */
    /* JADX WARNING: Removed duplicated region for block: B:905:0x0fd3 A[Catch:{ Exception -> 0x0fbe, all -> 0x100f }] */
    /* JADX WARNING: Removed duplicated region for block: B:911:0x0fe1  */
    /* JADX WARNING: Removed duplicated region for block: B:914:0x0fe8 A[SYNTHETIC, Splitter:B:914:0x0fe8] */
    /* JADX WARNING: Removed duplicated region for block: B:926:0x100f A[ExcHandler: all (r0v7 'th' java.lang.Throwable A[CUSTOM_DECLARE]), PHI: r14 
      PHI: (r14v4 org.telegram.messenger.video.MediaCodecVideoConvertor) = (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v7 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v8 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v8 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v8 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v8 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v8 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v8 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v8 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v8 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v8 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v8 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v15 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v18 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v18 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v18 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v18 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v18 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v18 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v18 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v18 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v18 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v18 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v18 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v18 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v18 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v18 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v18 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v18 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v18 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v18 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v18 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v18 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v22 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v0 org.telegram.messenger.video.MediaCodecVideoConvertor) binds: [B:1:0x0018, B:2:?, B:4:0x0028, B:5:?, B:324:0x0638, B:325:?, B:327:0x0647, B:896:0x0fba, B:363:0x06ce, B:877:0x0var_, B:856:0x0f1c, B:857:?, B:862:0x0var_, B:863:?, B:865:0x0f2e, B:866:?, B:868:0x0f3b, B:869:?, B:871:0x0var_, B:872:?, B:874:0x0f4f, B:875:?, B:389:0x072c, B:390:?, B:400:0x0758, B:401:?, B:412:0x0797, B:415:0x079d, B:416:?, B:417:0x07a9, B:419:0x07b7, B:420:?, B:422:0x07f7, B:423:?, B:424:0x07fa, B:427:0x0802, B:428:?, B:496:0x0911, B:583:0x0acb, B:584:?, B:588:0x0ad9, B:720:0x0d15, B:738:0x0d55, B:739:?, B:769:0x0dbe, B:770:?, B:772:0x0dc3, B:782:0x0ddb, B:777:0x0dca, B:773:?, B:758:0x0d8b, B:745:0x0d62, B:730:0x0d31, B:621:0x0b96, B:622:?, B:810:0x0e50, B:630:0x0ba7, B:631:?, B:708:0x0cdc, B:634:0x0bae, B:635:?, B:667:0x0c1b, B:668:?, B:670:0x0CLASSNAME, B:639:0x0bb6, B:627:0x0ba1, B:625:0x0b9e, B:626:?, B:595:0x0afb, B:512:0x095a, B:557:0x0a3d, B:517:0x0968, B:506:0x0947, B:439:0x082a, B:447:0x084e, B:463:0x08a3, B:464:?, B:475:0x08c6, B:476:?, B:478:0x08ca, B:472:0x08be, B:468:0x08b7, B:469:?, B:453:0x085f, B:457:0x087e, B:442:0x083e, B:431:0x080e, B:404:0x0780, B:392:0x0730, B:393:?, B:395:0x0739, B:396:?, B:383:0x0714, B:373:0x06f3, B:374:?, B:367:0x06e1, B:368:?, B:346:0x0680, B:347:?, B:353:0x06a0, B:354:?, B:339:0x0668, B:331:0x064e, B:10:0x0058, B:301:0x05c7, B:282:0x0582, B:283:?, B:288:0x058d, B:289:?, B:291:0x0594, B:292:?, B:294:0x0599, B:295:?, B:297:0x05a3, B:298:?, B:11:?, B:23:0x00a1, B:24:?, B:32:0x00d8, B:33:?, B:38:0x0102, B:41:0x010d, B:44:0x012a, B:45:?, B:46:0x0136, B:47:?, B:49:0x0172, B:50:?, B:51:0x0175, B:52:?, B:62:0x0189, B:232:0x0477, B:117:0x02a0, B:118:?, B:251:0x04d4, B:127:0x02b4, B:128:?, B:219:0x0427, B:220:?, B:225:0x0435, B:131:0x02bb, B:132:?, B:185:0x0384, B:186:?, B:204:0x03e8, B:191:0x03a1, B:165:0x032f, B:173:0x034e, B:136:0x02c3, B:124:0x02ae, B:121:0x02a8, B:122:?, B:102:0x023b, B:54:0x0179, B:35:0x00dc, B:36:?, B:26:0x00a5, B:14:0x005e] A[DONT_GENERATE, DONT_INLINE], Splitter:B:1:0x0018] */
    /* JADX WARNING: Removed duplicated region for block: B:938:0x1052  */
    /* JADX WARNING: Removed duplicated region for block: B:941:0x1059 A[SYNTHETIC, Splitter:B:941:0x1059] */
    /* JADX WARNING: Removed duplicated region for block: B:947:0x1069  */
    /* JADX WARNING: Removed duplicated region for block: B:949:0x108e A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:954:0x1097  */
    /* JADX WARNING: Removed duplicated region for block: B:957:0x109e A[SYNTHETIC, Splitter:B:957:0x109e] */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:877:0x0var_=Splitter:B:877:0x0var_, B:282:0x0582=Splitter:B:282:0x0582, B:219:0x0427=Splitter:B:219:0x0427, B:862:0x0var_=Splitter:B:862:0x0var_, B:62:0x0189=Splitter:B:62:0x0189, B:583:0x0acb=Splitter:B:583:0x0acb, B:389:0x072c=Splitter:B:389:0x072c, B:288:0x058d=Splitter:B:288:0x058d, B:447:0x084e=Splitter:B:447:0x084e, B:165:0x032f=Splitter:B:165:0x032f, B:496:0x0911=Splitter:B:496:0x0911, B:225:0x0435=Splitter:B:225:0x0435, B:10:0x0058=Splitter:B:10:0x0058, B:32:0x00d8=Splitter:B:32:0x00d8, B:400:0x0758=Splitter:B:400:0x0758, B:588:0x0ad9=Splitter:B:588:0x0ad9, B:667:0x0c1b=Splitter:B:667:0x0c1b, B:708:0x0cdc=Splitter:B:708:0x0cdc, B:856:0x0f1c=Splitter:B:856:0x0f1c, B:769:0x0dbe=Splitter:B:769:0x0dbe} */
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
            android.media.MediaCodec$BufferInfo r5 = new android.media.MediaCodec$BufferInfo     // Catch:{ Exception -> 0x1014, all -> 0x100f }
            r5.<init>()     // Catch:{ Exception -> 0x1014, all -> 0x100f }
            org.telegram.messenger.video.Mp4Movie r6 = new org.telegram.messenger.video.Mp4Movie     // Catch:{ Exception -> 0x1014, all -> 0x100f }
            r6.<init>()     // Catch:{ Exception -> 0x1014, all -> 0x100f }
            r15 = r73
            r6.setCacheFile(r15)     // Catch:{ Exception -> 0x1014, all -> 0x100f }
            r7 = 0
            r6.setRotation(r7)     // Catch:{ Exception -> 0x1007, all -> 0x100f }
            r6.setSize(r12, r11)     // Catch:{ Exception -> 0x1007, all -> 0x100f }
            org.telegram.messenger.video.MP4Builder r8 = new org.telegram.messenger.video.MP4Builder     // Catch:{ Exception -> 0x1007, all -> 0x100f }
            r8.<init>()     // Catch:{ Exception -> 0x1007, all -> 0x100f }
            r15 = r75
            org.telegram.messenger.video.MP4Builder r6 = r8.createMovie(r6, r15)     // Catch:{ Exception -> 0x1007, all -> 0x100f }
            r14.mediaMuxer = r6     // Catch:{ Exception -> 0x1007, all -> 0x100f }
            float r6 = (float) r3     // Catch:{ Exception -> 0x1007, all -> 0x100f }
            r17 = 1148846080(0x447a0000, float:1000.0)
            float r18 = r6 / r17
            r71.checkConversionCanceled()     // Catch:{ Exception -> 0x1007, all -> 0x100f }
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
            boolean r7 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0092, all -> 0x100f }
            if (r7 == 0) goto L_0x0087
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0092, all -> 0x100f }
            r7.<init>()     // Catch:{ Exception -> 0x0092, all -> 0x100f }
            java.lang.String r6 = "changing width from "
            r7.append(r6)     // Catch:{ Exception -> 0x0092, all -> 0x100f }
            r7.append(r12)     // Catch:{ Exception -> 0x0092, all -> 0x100f }
            java.lang.String r6 = " to "
            r7.append(r6)     // Catch:{ Exception -> 0x0092, all -> 0x100f }
            float r6 = (float) r12     // Catch:{ Exception -> 0x0092, all -> 0x100f }
            float r6 = r6 / r23
            int r6 = java.lang.Math.round(r6)     // Catch:{ Exception -> 0x0092, all -> 0x100f }
            int r6 = r6 * 16
            r7.append(r6)     // Catch:{ Exception -> 0x0092, all -> 0x100f }
            java.lang.String r6 = r7.toString()     // Catch:{ Exception -> 0x0092, all -> 0x100f }
            org.telegram.messenger.FileLog.d(r6)     // Catch:{ Exception -> 0x0092, all -> 0x100f }
        L_0x0087:
            float r6 = (float) r12     // Catch:{ Exception -> 0x0092, all -> 0x100f }
            float r6 = r6 / r23
            int r6 = java.lang.Math.round(r6)     // Catch:{ Exception -> 0x0092, all -> 0x100f }
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
            boolean r6 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0092, all -> 0x100f }
            if (r6 == 0) goto L_0x00ce
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0092, all -> 0x100f }
            r6.<init>()     // Catch:{ Exception -> 0x0092, all -> 0x100f }
            java.lang.String r7 = "changing height from "
            r6.append(r7)     // Catch:{ Exception -> 0x0092, all -> 0x100f }
            r6.append(r11)     // Catch:{ Exception -> 0x0092, all -> 0x100f }
            java.lang.String r7 = " to "
            r6.append(r7)     // Catch:{ Exception -> 0x0092, all -> 0x100f }
            float r7 = (float) r11     // Catch:{ Exception -> 0x0092, all -> 0x100f }
            float r7 = r7 / r23
            int r7 = java.lang.Math.round(r7)     // Catch:{ Exception -> 0x0092, all -> 0x100f }
            int r7 = r7 * 16
            r6.append(r7)     // Catch:{ Exception -> 0x0092, all -> 0x100f }
            java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x0092, all -> 0x100f }
            org.telegram.messenger.FileLog.d(r6)     // Catch:{ Exception -> 0x0092, all -> 0x100f }
        L_0x00ce:
            float r6 = (float) r11     // Catch:{ Exception -> 0x0092, all -> 0x100f }
            float r6 = r6 / r23
            int r6 = java.lang.Math.round(r6)     // Catch:{ Exception -> 0x0092, all -> 0x100f }
            int r6 = r6 * 16
            r11 = r6
        L_0x00d8:
            boolean r6 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x056b, all -> 0x100f }
            if (r6 == 0) goto L_0x0100
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0092, all -> 0x100f }
            r6.<init>()     // Catch:{ Exception -> 0x0092, all -> 0x100f }
            java.lang.String r7 = "create photo encoder "
            r6.append(r7)     // Catch:{ Exception -> 0x0092, all -> 0x100f }
            r6.append(r12)     // Catch:{ Exception -> 0x0092, all -> 0x100f }
            java.lang.String r7 = " "
            r6.append(r7)     // Catch:{ Exception -> 0x0092, all -> 0x100f }
            r6.append(r11)     // Catch:{ Exception -> 0x0092, all -> 0x100f }
            java.lang.String r7 = " duration = "
            r6.append(r7)     // Catch:{ Exception -> 0x0092, all -> 0x100f }
            r6.append(r3)     // Catch:{ Exception -> 0x0092, all -> 0x100f }
            java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x0092, all -> 0x100f }
            org.telegram.messenger.FileLog.d(r6)     // Catch:{ Exception -> 0x0092, all -> 0x100f }
        L_0x0100:
            r7 = r29
            android.media.MediaFormat r6 = android.media.MediaFormat.createVideoFormat(r7, r12, r11)     // Catch:{ Exception -> 0x056b, all -> 0x100f }
            r29 = r1
            java.lang.String r1 = "color-format"
            r2 = 2130708361(0x7var_, float:1.701803E38)
            r6.setInteger(r1, r2)     // Catch:{ Exception -> 0x0561, all -> 0x100f }
            java.lang.String r1 = "bitrate"
            r6.setInteger(r1, r9)     // Catch:{ Exception -> 0x0561, all -> 0x100f }
            java.lang.String r1 = "frame-rate"
            r6.setInteger(r1, r10)     // Catch:{ Exception -> 0x0561, all -> 0x100f }
            java.lang.String r1 = "i-frame-interval"
            r2 = 2
            r6.setInteger(r1, r2)     // Catch:{ Exception -> 0x0561, all -> 0x100f }
            android.media.MediaCodec r1 = android.media.MediaCodec.createEncoderByType(r7)     // Catch:{ Exception -> 0x0561, all -> 0x100f }
            r16 = r5
            r23 = r7
            r5 = 1
            r7 = 0
            r1.configure(r6, r7, r7, r5)     // Catch:{ Exception -> 0x0555, all -> 0x100f }
            org.telegram.messenger.video.InputSurface r6 = new org.telegram.messenger.video.InputSurface     // Catch:{ Exception -> 0x0555, all -> 0x100f }
            android.view.Surface r2 = r1.createInputSurface()     // Catch:{ Exception -> 0x0555, all -> 0x100f }
            r6.<init>(r2)     // Catch:{ Exception -> 0x0555, all -> 0x100f }
            r6.makeCurrent()     // Catch:{ Exception -> 0x0544, all -> 0x100f }
            r1.start()     // Catch:{ Exception -> 0x0544, all -> 0x100f }
            org.telegram.messenger.video.OutputSurface r33 = new org.telegram.messenger.video.OutputSurface     // Catch:{ Exception -> 0x0544, all -> 0x100f }
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
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)     // Catch:{ Exception -> 0x053a, all -> 0x100f }
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x052f, all -> 0x100f }
            if (r1 >= r13) goto L_0x0188
            java.nio.ByteBuffer[] r6 = r76.getOutputBuffers()     // Catch:{ Exception -> 0x017e, all -> 0x100f }
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
            r71.checkConversionCanceled()     // Catch:{ Exception -> 0x052f, all -> 0x100f }
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
            r71.checkConversionCanceled()     // Catch:{ Exception -> 0x052f, all -> 0x100f }
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
            r71.checkConversionCanceled()     // Catch:{ Exception -> 0x052f, all -> 0x100f }
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
            org.telegram.messenger.video.MP4Builder r3 = r14.mediaMuxer     // Catch:{ Exception -> 0x02ab, all -> 0x100f }
            r8 = 0
            int r3 = r3.addTrack(r7, r8)     // Catch:{ Exception -> 0x02ab, all -> 0x100f }
            r8 = r45
            boolean r19 = r7.containsKey(r8)     // Catch:{ Exception -> 0x02ab, all -> 0x100f }
            if (r19 == 0) goto L_0x026d
            r19 = r3
            int r3 = r7.getInteger(r8)     // Catch:{ Exception -> 0x02ab, all -> 0x100f }
            r45 = r8
            r8 = 1
            if (r3 != r8) goto L_0x0271
            r8 = r44
            java.nio.ByteBuffer r3 = r7.getByteBuffer(r8)     // Catch:{ Exception -> 0x02ab, all -> 0x100f }
            r4 = r49
            java.nio.ByteBuffer r7 = r7.getByteBuffer(r4)     // Catch:{ Exception -> 0x02ab, all -> 0x100f }
            int r3 = r3.limit()     // Catch:{ Exception -> 0x02ab, all -> 0x100f }
            int r7 = r7.limit()     // Catch:{ Exception -> 0x02ab, all -> 0x100f }
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
            int r9 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x04f0, all -> 0x100f }
            r79 = r1
            r1 = 21
            if (r9 >= r1) goto L_0x02ae
            r1 = r2[r13]     // Catch:{ Exception -> 0x02ab, all -> 0x100f }
            goto L_0x02b2
        L_0x02ab:
            r0 = move-exception
            goto L_0x0206
        L_0x02ae:
            java.nio.ByteBuffer r1 = r6.getOutputBuffer(r13)     // Catch:{ Exception -> 0x04f0, all -> 0x100f }
        L_0x02b2:
            if (r1 == 0) goto L_0x04cf
            int r9 = r15.size     // Catch:{ Exception -> 0x04c9, all -> 0x100f }
            r20 = r2
            r2 = 1
            if (r9 <= r2) goto L_0x041a
            int r9 = r15.flags     // Catch:{ Exception -> 0x040d, all -> 0x100f }
            r2 = 2
            r9 = r9 & r2
            if (r9 != 0) goto L_0x0379
            if (r4 == 0) goto L_0x02d5
            int r9 = r15.flags     // Catch:{ Exception -> 0x02ab, all -> 0x100f }
            r16 = 1
            r9 = r9 & 1
            if (r9 == 0) goto L_0x02d5
            int r9 = r15.offset     // Catch:{ Exception -> 0x02ab, all -> 0x100f }
            int r9 = r9 + r4
            r15.offset = r9     // Catch:{ Exception -> 0x02ab, all -> 0x100f }
            int r9 = r15.size     // Catch:{ Exception -> 0x02ab, all -> 0x100f }
            int r9 = r9 - r4
            r15.size = r9     // Catch:{ Exception -> 0x02ab, all -> 0x100f }
        L_0x02d5:
            if (r5 == 0) goto L_0x032d
            int r9 = r15.flags     // Catch:{ Exception -> 0x02ab, all -> 0x100f }
            r16 = 1
            r9 = r9 & 1
            if (r9 == 0) goto L_0x032d
            int r5 = r15.size     // Catch:{ Exception -> 0x02ab, all -> 0x100f }
            r9 = 100
            if (r5 <= r9) goto L_0x0329
            int r5 = r15.offset     // Catch:{ Exception -> 0x02ab, all -> 0x100f }
            r1.position(r5)     // Catch:{ Exception -> 0x02ab, all -> 0x100f }
            byte[] r5 = new byte[r9]     // Catch:{ Exception -> 0x02ab, all -> 0x100f }
            r1.get(r5)     // Catch:{ Exception -> 0x02ab, all -> 0x100f }
            r9 = 0
            r21 = 0
        L_0x02f2:
            r2 = 96
            if (r9 >= r2) goto L_0x0329
            byte r2 = r5[r9]     // Catch:{ Exception -> 0x02ab, all -> 0x100f }
            if (r2 != 0) goto L_0x0321
            int r2 = r9 + 1
            byte r2 = r5[r2]     // Catch:{ Exception -> 0x02ab, all -> 0x100f }
            if (r2 != 0) goto L_0x0321
            int r2 = r9 + 2
            byte r2 = r5[r2]     // Catch:{ Exception -> 0x02ab, all -> 0x100f }
            if (r2 != 0) goto L_0x0321
            int r2 = r9 + 3
            byte r2 = r5[r2]     // Catch:{ Exception -> 0x02ab, all -> 0x100f }
            r22 = r4
            r4 = 1
            if (r2 != r4) goto L_0x0323
            int r2 = r21 + 1
            if (r2 <= r4) goto L_0x031e
            int r2 = r15.offset     // Catch:{ Exception -> 0x02ab, all -> 0x100f }
            int r2 = r2 + r9
            r15.offset = r2     // Catch:{ Exception -> 0x02ab, all -> 0x100f }
            int r2 = r15.size     // Catch:{ Exception -> 0x02ab, all -> 0x100f }
            int r2 = r2 - r9
            r15.size = r2     // Catch:{ Exception -> 0x02ab, all -> 0x100f }
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
            org.telegram.messenger.video.MP4Builder r2 = r14.mediaMuxer     // Catch:{ Exception -> 0x036f, all -> 0x100f }
            r4 = 1
            long r1 = r2.writeSampleData(r3, r1, r15, r4)     // Catch:{ Exception -> 0x036f, all -> 0x100f }
            r9 = r5
            r4 = 0
            int r21 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r21 == 0) goto L_0x035f
            org.telegram.messenger.MediaController$VideoConvertorListener r4 = r14.callback     // Catch:{ Exception -> 0x036f, all -> 0x100f }
            if (r4 == 0) goto L_0x035f
            long r4 = r15.presentationTimeUs     // Catch:{ Exception -> 0x036f, all -> 0x100f }
            r21 = r6
            r49 = r7
            r6 = r80
            long r4 = r4 - r6
            int r23 = (r4 > r10 ? 1 : (r4 == r10 ? 0 : -1))
            if (r23 <= 0) goto L_0x0352
            long r4 = r15.presentationTimeUs     // Catch:{ Exception -> 0x035d, all -> 0x100f }
            long r10 = r4 - r6
        L_0x0352:
            org.telegram.messenger.MediaController$VideoConvertorListener r4 = r14.callback     // Catch:{ Exception -> 0x035d, all -> 0x100f }
            float r5 = (float) r10     // Catch:{ Exception -> 0x035d, all -> 0x100f }
            float r5 = r5 / r17
            float r5 = r5 / r18
            r4.didWriteData(r1, r5)     // Catch:{ Exception -> 0x035d, all -> 0x100f }
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
            int r3 = r15.size     // Catch:{ Exception -> 0x0405, all -> 0x100f }
            byte[] r3 = new byte[r3]     // Catch:{ Exception -> 0x0405, all -> 0x100f }
            int r4 = r15.offset     // Catch:{ Exception -> 0x0405, all -> 0x100f }
            int r9 = r15.size     // Catch:{ Exception -> 0x0405, all -> 0x100f }
            int r4 = r4 + r9
            r1.limit(r4)     // Catch:{ Exception -> 0x0405, all -> 0x100f }
            int r4 = r15.offset     // Catch:{ Exception -> 0x0405, all -> 0x100f }
            r1.position(r4)     // Catch:{ Exception -> 0x0405, all -> 0x100f }
            r1.get(r3)     // Catch:{ Exception -> 0x0405, all -> 0x100f }
            int r1 = r15.size     // Catch:{ Exception -> 0x0405, all -> 0x100f }
            r4 = 1
            int r1 = r1 - r4
        L_0x039c:
            if (r1 < 0) goto L_0x03e0
            r9 = 3
            if (r1 <= r9) goto L_0x03e0
            byte r2 = r3[r1]     // Catch:{ Exception -> 0x035d, all -> 0x100f }
            if (r2 != r4) goto L_0x03d7
            int r2 = r1 + -1
            byte r2 = r3[r2]     // Catch:{ Exception -> 0x035d, all -> 0x100f }
            if (r2 != 0) goto L_0x03d7
            int r2 = r1 + -2
            byte r2 = r3[r2]     // Catch:{ Exception -> 0x035d, all -> 0x100f }
            if (r2 != 0) goto L_0x03d7
            int r2 = r1 + -3
            byte r4 = r3[r2]     // Catch:{ Exception -> 0x035d, all -> 0x100f }
            if (r4 != 0) goto L_0x03d7
            java.nio.ByteBuffer r1 = java.nio.ByteBuffer.allocate(r2)     // Catch:{ Exception -> 0x035d, all -> 0x100f }
            int r4 = r15.size     // Catch:{ Exception -> 0x035d, all -> 0x100f }
            int r4 = r4 - r2
            java.nio.ByteBuffer r4 = java.nio.ByteBuffer.allocate(r4)     // Catch:{ Exception -> 0x035d, all -> 0x100f }
            r23 = r5
            r9 = 0
            java.nio.ByteBuffer r5 = r1.put(r3, r9, r2)     // Catch:{ Exception -> 0x035d, all -> 0x100f }
            r5.position(r9)     // Catch:{ Exception -> 0x035d, all -> 0x100f }
            int r5 = r15.size     // Catch:{ Exception -> 0x035d, all -> 0x100f }
            int r5 = r5 - r2
            java.nio.ByteBuffer r2 = r4.put(r3, r2, r5)     // Catch:{ Exception -> 0x035d, all -> 0x100f }
            r2.position(r9)     // Catch:{ Exception -> 0x035d, all -> 0x100f }
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
            android.media.MediaFormat r3 = android.media.MediaFormat.createVideoFormat(r5, r12, r2)     // Catch:{ Exception -> 0x0403, all -> 0x100f }
            if (r1 == 0) goto L_0x03f9
            if (r4 == 0) goto L_0x03f9
            r3.setByteBuffer(r8, r1)     // Catch:{ Exception -> 0x0403, all -> 0x100f }
            r1 = r49
            r3.setByteBuffer(r1, r4)     // Catch:{ Exception -> 0x0403, all -> 0x100f }
            goto L_0x03fb
        L_0x03f9:
            r1 = r49
        L_0x03fb:
            org.telegram.messenger.video.MP4Builder r4 = r14.mediaMuxer     // Catch:{ Exception -> 0x0403, all -> 0x100f }
            r9 = 0
            int r3 = r4.addTrack(r3, r9)     // Catch:{ Exception -> 0x0403, all -> 0x100f }
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
            int r4 = r15.flags     // Catch:{ Exception -> 0x04c3, all -> 0x100f }
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
            r9.releaseOutputBuffer(r13, r1)     // Catch:{ Exception -> 0x0499, all -> 0x100f }
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
            r33.drawImage()     // Catch:{ Exception -> 0x0499, all -> 0x100f }
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
            r13.setPresentationTime(r1)     // Catch:{ Exception -> 0x0493, all -> 0x100f }
            r13.swapBuffers()     // Catch:{ Exception -> 0x0493, all -> 0x100f }
            int r1 = r11 + 1
            float r2 = (float) r1     // Catch:{ Exception -> 0x0493, all -> 0x100f }
            r11 = 1106247680(0x41var_, float:30.0)
            float r11 = r11 * r18
            int r2 = (r2 > r11 ? 1 : (r2 == r11 ? 0 : -1))
            if (r2 < 0) goto L_0x048f
            r9.signalEndOfInputStream()     // Catch:{ Exception -> 0x0493, all -> 0x100f }
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
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x050e, all -> 0x100f }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x050e, all -> 0x100f }
            r3.<init>()     // Catch:{ Exception -> 0x050e, all -> 0x100f }
            java.lang.String r4 = "encoderOutputBuffer "
            r3.append(r4)     // Catch:{ Exception -> 0x050e, all -> 0x100f }
            r3.append(r13)     // Catch:{ Exception -> 0x050e, all -> 0x100f }
            java.lang.String r4 = " was null"
            r3.append(r4)     // Catch:{ Exception -> 0x050e, all -> 0x100f }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x050e, all -> 0x100f }
            r2.<init>(r3)     // Catch:{ Exception -> 0x050e, all -> 0x100f }
            throw r2     // Catch:{ Exception -> 0x050e, all -> 0x100f }
        L_0x04f0:
            r0 = move-exception
            goto L_0x0515
        L_0x04f2:
            r9 = r6
            r1 = r47
            r6 = r80
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x050e, all -> 0x100f }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x050e, all -> 0x100f }
            r3.<init>()     // Catch:{ Exception -> 0x050e, all -> 0x100f }
            java.lang.String r4 = "unexpected result from encoder.dequeueOutputBuffer: "
            r3.append(r4)     // Catch:{ Exception -> 0x050e, all -> 0x100f }
            r3.append(r13)     // Catch:{ Exception -> 0x050e, all -> 0x100f }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x050e, all -> 0x100f }
            r2.<init>(r3)     // Catch:{ Exception -> 0x050e, all -> 0x100f }
            throw r2     // Catch:{ Exception -> 0x050e, all -> 0x100f }
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
            boolean r2 = r1 instanceof java.lang.IllegalStateException     // Catch:{ Exception -> 0x0618, all -> 0x100f }
            if (r2 == 0) goto L_0x058b
            if (r87 != 0) goto L_0x058b
            r42 = 1
            goto L_0x058d
        L_0x058b:
            r42 = 0
        L_0x058d:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0605, all -> 0x100f }
            r2.<init>()     // Catch:{ Exception -> 0x0605, all -> 0x100f }
            r13 = r36
            r2.append(r13)     // Catch:{ Exception -> 0x05fb, all -> 0x100f }
            r3 = r50
            r2.append(r3)     // Catch:{ Exception -> 0x05f9, all -> 0x100f }
            java.lang.String r4 = " framerate: "
            r2.append(r4)     // Catch:{ Exception -> 0x05f9, all -> 0x100f }
            r10 = r78
            r2.append(r10)     // Catch:{ Exception -> 0x05f4, all -> 0x100f }
            java.lang.String r4 = " size: "
            r2.append(r4)     // Catch:{ Exception -> 0x05f4, all -> 0x100f }
            r2.append(r11)     // Catch:{ Exception -> 0x05f4, all -> 0x100f }
            java.lang.String r4 = "x"
            r2.append(r4)     // Catch:{ Exception -> 0x05f4, all -> 0x100f }
            r2.append(r12)     // Catch:{ Exception -> 0x05f4, all -> 0x100f }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x05f4, all -> 0x100f }
            org.telegram.messenger.FileLog.e((java.lang.String) r2)     // Catch:{ Exception -> 0x05f4, all -> 0x100f }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)     // Catch:{ Exception -> 0x05f4, all -> 0x100f }
            r1 = r9
            r2 = r42
            r42 = 1
        L_0x05c5:
            if (r33 == 0) goto L_0x05d9
            r33.release()     // Catch:{ Exception -> 0x05cb, all -> 0x100f }
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
            goto L_0x1021
        L_0x05d9:
            if (r47 == 0) goto L_0x05de
            r47.release()     // Catch:{ Exception -> 0x05cb, all -> 0x100f }
        L_0x05de:
            if (r1 == 0) goto L_0x05e6
            r1.stop()     // Catch:{ Exception -> 0x05cb, all -> 0x100f }
            r1.release()     // Catch:{ Exception -> 0x05cb, all -> 0x100f }
        L_0x05e6:
            r71.checkConversionCanceled()     // Catch:{ Exception -> 0x05cb, all -> 0x100f }
            r13 = r2
            r2 = r3
            r8 = r10
            r5 = r11
            r69 = r6
            r7 = r12
            r11 = r69
            goto L_0x0fdb
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
            goto L_0x1021
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
            goto L_0x1020
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
            android.media.MediaExtractor r1 = new android.media.MediaExtractor     // Catch:{ Exception -> 0x1000, all -> 0x100f }
            r1.<init>()     // Catch:{ Exception -> 0x1000, all -> 0x100f }
            r14.extractor = r1     // Catch:{ Exception -> 0x1000, all -> 0x100f }
            r2 = r72
            r1.setDataSource(r2)     // Catch:{ Exception -> 0x1000, all -> 0x100f }
            android.media.MediaExtractor r1 = r14.extractor     // Catch:{ Exception -> 0x1000, all -> 0x100f }
            r7 = 0
            int r6 = org.telegram.messenger.MediaController.findTrack(r1, r7)     // Catch:{ Exception -> 0x0ffa, all -> 0x100f }
            r1 = -1
            if (r9 == r1) goto L_0x0663
            android.media.MediaExtractor r1 = r14.extractor     // Catch:{ Exception -> 0x0657, all -> 0x100f }
            r3 = 1
            int r1 = org.telegram.messenger.MediaController.findTrack(r1, r3)     // Catch:{ Exception -> 0x0657, all -> 0x100f }
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
            goto L_0x1021
        L_0x0663:
            r3 = -1
        L_0x0664:
            java.lang.String r1 = "mime"
            if (r6 < 0) goto L_0x067a
            android.media.MediaExtractor r4 = r14.extractor     // Catch:{ Exception -> 0x0657, all -> 0x100f }
            android.media.MediaFormat r4 = r4.getTrackFormat(r6)     // Catch:{ Exception -> 0x0657, all -> 0x100f }
            java.lang.String r4 = r4.getString(r1)     // Catch:{ Exception -> 0x0657, all -> 0x100f }
            boolean r4 = r4.equals(r5)     // Catch:{ Exception -> 0x0657, all -> 0x100f }
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
            android.media.MediaExtractor r3 = r14.extractor     // Catch:{ Exception -> 0x06b2, all -> 0x100f }
            org.telegram.messenger.video.MP4Builder r4 = r14.mediaMuxer     // Catch:{ Exception -> 0x06b2, all -> 0x100f }
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
            r1.readAndWriteTracks(r2, r3, r4, r5, r7, r9, r11, r12)     // Catch:{ Exception -> 0x06b0, all -> 0x100f }
            r7 = r76
            r5 = r77
            r8 = r78
            r2 = r79
            r11 = r80
            r6 = 0
            goto L_0x0fdd
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
            goto L_0x1020
        L_0x06c6:
            r12 = r2
            r36 = r13
            r13 = 0
            if (r6 < 0) goto L_0x0fa2
            r19 = -1
            android.media.MediaExtractor r2 = r14.extractor     // Catch:{ Exception -> 0x0f0c, all -> 0x100f }
            r2.selectTrack(r6)     // Catch:{ Exception -> 0x0f0c, all -> 0x100f }
            android.media.MediaExtractor r2 = r14.extractor     // Catch:{ Exception -> 0x0f0c, all -> 0x100f }
            android.media.MediaFormat r11 = r2.getTrackFormat(r6)     // Catch:{ Exception -> 0x0f0c, all -> 0x100f }
            r9 = r80
            r21 = 0
            int r2 = (r9 > r21 ? 1 : (r9 == r21 ? 0 : -1))
            if (r2 <= 0) goto L_0x06f3
            android.media.MediaExtractor r2 = r14.extractor     // Catch:{ Exception -> 0x06ea, all -> 0x100f }
            r2.seekTo(r9, r13)     // Catch:{ Exception -> 0x06ea, all -> 0x100f }
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
            goto L_0x0var_
        L_0x06f3:
            android.media.MediaExtractor r2 = r14.extractor     // Catch:{ Exception -> 0x0var_, all -> 0x100f }
            r7 = r3
            r3 = 0
            r2.seekTo(r3, r13)     // Catch:{ Exception -> 0x0var_, all -> 0x100f }
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
            int r3 = r13.transformWidth     // Catch:{ Exception -> 0x0724, all -> 0x100f }
            r21 = r3
            int r3 = r13.transformHeight     // Catch:{ Exception -> 0x0724, all -> 0x100f }
            goto L_0x0721
        L_0x071b:
            int r3 = r13.transformHeight     // Catch:{ Exception -> 0x0724, all -> 0x100f }
            r21 = r3
            int r3 = r13.transformWidth     // Catch:{ Exception -> 0x0724, all -> 0x100f }
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
            boolean r21 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0var_, all -> 0x100f }
            if (r21 == 0) goto L_0x0756
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0724, all -> 0x100f }
            r4.<init>()     // Catch:{ Exception -> 0x0724, all -> 0x100f }
            r21 = r6
            java.lang.String r6 = "create encoder with w = "
            r4.append(r6)     // Catch:{ Exception -> 0x074f, all -> 0x100f }
            r4.append(r13)     // Catch:{ Exception -> 0x074f, all -> 0x100f }
            java.lang.String r6 = " h = "
            r4.append(r6)     // Catch:{ Exception -> 0x074f, all -> 0x100f }
            r4.append(r3)     // Catch:{ Exception -> 0x074f, all -> 0x100f }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x074f, all -> 0x100f }
            org.telegram.messenger.FileLog.d(r4)     // Catch:{ Exception -> 0x074f, all -> 0x100f }
            goto L_0x0758
        L_0x074f:
            r0 = move-exception
            r1 = r0
            r11 = r9
            r65 = r21
            goto L_0x0var_
        L_0x0756:
            r21 = r6
        L_0x0758:
            android.media.MediaFormat r4 = android.media.MediaFormat.createVideoFormat(r5, r13, r3)     // Catch:{ Exception -> 0x0efd, all -> 0x100f }
            java.lang.String r6 = "color-format"
            r22 = r7
            r7 = 2130708361(0x7var_, float:1.701803E38)
            r4.setInteger(r6, r7)     // Catch:{ Exception -> 0x0efd, all -> 0x100f }
            java.lang.String r6 = "bitrate"
            r4.setInteger(r6, r2)     // Catch:{ Exception -> 0x0efd, all -> 0x100f }
            java.lang.String r6 = "frame-rate"
            r7 = r78
            r4.setInteger(r6, r7)     // Catch:{ Exception -> 0x0efd, all -> 0x100f }
            java.lang.String r6 = "i-frame-interval"
            r44 = r8
            r8 = 2
            r4.setInteger(r6, r8)     // Catch:{ Exception -> 0x0efd, all -> 0x100f }
            int r6 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0efd, all -> 0x100f }
            r8 = 23
            if (r6 >= r8) goto L_0x0795
            int r6 = java.lang.Math.min(r3, r13)     // Catch:{ Exception -> 0x074f, all -> 0x100f }
            r8 = 480(0x1e0, float:6.73E-43)
            if (r6 > r8) goto L_0x0795
            r6 = 921600(0xe1000, float:1.291437E-39)
            if (r2 <= r6) goto L_0x0790
            r2 = 921600(0xe1000, float:1.291437E-39)
        L_0x0790:
            java.lang.String r6 = "bitrate"
            r4.setInteger(r6, r2)     // Catch:{ Exception -> 0x074f, all -> 0x100f }
        L_0x0795:
            r23 = r2
            android.media.MediaCodec r8 = android.media.MediaCodec.createEncoderByType(r5)     // Catch:{ Exception -> 0x0ef5, all -> 0x100f }
            r2 = 1
            r6 = 0
            r8.configure(r4, r6, r6, r2)     // Catch:{ Exception -> 0x0eeb, all -> 0x100f }
            org.telegram.messenger.video.InputSurface r4 = new org.telegram.messenger.video.InputSurface     // Catch:{ Exception -> 0x0eeb, all -> 0x100f }
            android.view.Surface r2 = r8.createInputSurface()     // Catch:{ Exception -> 0x0eeb, all -> 0x100f }
            r4.<init>(r2)     // Catch:{ Exception -> 0x0eeb, all -> 0x100f }
            r4.makeCurrent()     // Catch:{ Exception -> 0x0ed9, all -> 0x100f }
            r8.start()     // Catch:{ Exception -> 0x0ed9, all -> 0x100f }
            java.lang.String r2 = r11.getString(r1)     // Catch:{ Exception -> 0x0ed9, all -> 0x100f }
            android.media.MediaCodec r2 = android.media.MediaCodec.createDecoderByType(r2)     // Catch:{ Exception -> 0x0ed9, all -> 0x100f }
            org.telegram.messenger.video.OutputSurface r24 = new org.telegram.messenger.video.OutputSurface     // Catch:{ Exception -> 0x0ec6, all -> 0x100f }
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
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)     // Catch:{ Exception -> 0x0ebb, all -> 0x100f }
            android.view.Surface r1 = r24.getSurface()     // Catch:{ Exception -> 0x0ead, all -> 0x100f }
            r2 = r60
            r3 = 0
            r4 = 0
            r2.configure(r13, r1, r3, r4)     // Catch:{ Exception -> 0x0ea6, all -> 0x100f }
            r2.start()     // Catch:{ Exception -> 0x0ea6, all -> 0x100f }
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0ea6, all -> 0x100f }
            r4 = 21
            if (r1 >= r4) goto L_0x0824
            java.nio.ByteBuffer[] r6 = r2.getInputBuffers()     // Catch:{ Exception -> 0x0817, all -> 0x100f }
            java.nio.ByteBuffer[] r1 = r79.getOutputBuffers()     // Catch:{ Exception -> 0x0817, all -> 0x100f }
            goto L_0x0826
        L_0x0817:
            r0 = move-exception
            r6 = r79
            r11 = r80
            r1 = r0
            r10 = r2
            r43 = r3
        L_0x0820:
            r2 = r23
            goto L_0x0f1c
        L_0x0824:
            r1 = r3
            r6 = r1
        L_0x0826:
            r4 = r61
            if (r4 < 0) goto L_0x0900
            android.media.MediaExtractor r5 = r14.extractor     // Catch:{ Exception -> 0x08f3, all -> 0x100f }
            android.media.MediaFormat r5 = r5.getTrackFormat(r4)     // Catch:{ Exception -> 0x08f3, all -> 0x100f }
            r7 = r59
            java.lang.String r8 = r5.getString(r7)     // Catch:{ Exception -> 0x08f3, all -> 0x100f }
            java.lang.String r9 = "audio/mp4a-latm"
            boolean r8 = r8.equals(r9)     // Catch:{ Exception -> 0x08f3, all -> 0x100f }
            if (r8 != 0) goto L_0x084d
            java.lang.String r8 = r5.getString(r7)     // Catch:{ Exception -> 0x0817, all -> 0x100f }
            java.lang.String r9 = "audio/mpeg"
            boolean r8 = r8.equals(r9)     // Catch:{ Exception -> 0x0817, all -> 0x100f }
            if (r8 == 0) goto L_0x084b
            goto L_0x084d
        L_0x084b:
            r8 = 0
            goto L_0x084e
        L_0x084d:
            r8 = 1
        L_0x084e:
            java.lang.String r7 = r5.getString(r7)     // Catch:{ Exception -> 0x08f3, all -> 0x100f }
            java.lang.String r9 = "audio/unknown"
            boolean r7 = r7.equals(r9)     // Catch:{ Exception -> 0x08f3, all -> 0x100f }
            if (r7 == 0) goto L_0x085b
            r4 = -1
        L_0x085b:
            if (r4 < 0) goto L_0x08e8
            if (r8 == 0) goto L_0x089e
            org.telegram.messenger.video.MP4Builder r7 = r14.mediaMuxer     // Catch:{ Exception -> 0x089b, all -> 0x100f }
            r9 = 1
            int r7 = r7.addTrack(r5, r9)     // Catch:{ Exception -> 0x089b, all -> 0x100f }
            android.media.MediaExtractor r9 = r14.extractor     // Catch:{ Exception -> 0x089b, all -> 0x100f }
            r9.selectTrack(r4)     // Catch:{ Exception -> 0x089b, all -> 0x100f }
            java.lang.String r9 = "max-input-size"
            int r5 = r5.getInteger(r9)     // Catch:{ Exception -> 0x089b, all -> 0x100f }
            java.nio.ByteBuffer r5 = java.nio.ByteBuffer.allocateDirect(r5)     // Catch:{ Exception -> 0x089b, all -> 0x100f }
            r10 = r80
            r9 = r4
            r3 = 0
            int r13 = (r10 > r3 ? 1 : (r10 == r3 ? 0 : -1))
            if (r13 <= 0) goto L_0x0888
            android.media.MediaExtractor r13 = r14.extractor     // Catch:{ Exception -> 0x08bb, all -> 0x100f }
            r3 = 0
            r13.seekTo(r10, r3)     // Catch:{ Exception -> 0x08bb, all -> 0x100f }
            r22 = r1
            r13 = r5
            goto L_0x0893
        L_0x0888:
            android.media.MediaExtractor r3 = r14.extractor     // Catch:{ Exception -> 0x08bb, all -> 0x100f }
            r22 = r1
            r13 = r5
            r1 = 0
            r4 = 0
            r3.seekTo(r4, r1)     // Catch:{ Exception -> 0x08bb, all -> 0x100f }
        L_0x0893:
            r1 = r7
            r5 = r8
            r3 = r9
            r7 = 0
            r8 = r82
            goto L_0x090c
        L_0x089b:
            r0 = move-exception
            goto L_0x08f6
        L_0x089e:
            r10 = r80
            r22 = r1
            r9 = r4
            android.media.MediaExtractor r1 = new android.media.MediaExtractor     // Catch:{ Exception -> 0x08dd, all -> 0x100f }
            r1.<init>()     // Catch:{ Exception -> 0x08dd, all -> 0x100f }
            r1.setDataSource(r12)     // Catch:{ Exception -> 0x08dd, all -> 0x100f }
            r4 = r9
            r1.selectTrack(r4)     // Catch:{ Exception -> 0x08dd, all -> 0x100f }
            r3 = r8
            r7 = 0
            int r9 = (r10 > r7 ? 1 : (r10 == r7 ? 0 : -1))
            if (r9 <= 0) goto L_0x08bd
            r9 = 0
            r1.seekTo(r10, r9)     // Catch:{ Exception -> 0x08bb, all -> 0x100f }
            goto L_0x08c1
        L_0x08bb:
            r0 = move-exception
            goto L_0x08e0
        L_0x08bd:
            r9 = 0
            r1.seekTo(r7, r9)     // Catch:{ Exception -> 0x08dd, all -> 0x100f }
        L_0x08c1:
            org.telegram.messenger.video.AudioRecoder r7 = new org.telegram.messenger.video.AudioRecoder     // Catch:{ Exception -> 0x08dd, all -> 0x100f }
            r7.<init>(r5, r1, r4)     // Catch:{ Exception -> 0x08dd, all -> 0x100f }
            r7.startTime = r10     // Catch:{ Exception -> 0x08d8, all -> 0x100f }
            r8 = r82
            r7.endTime = r8     // Catch:{ Exception -> 0x094e, all -> 0x100f }
            org.telegram.messenger.video.MP4Builder r1 = r14.mediaMuxer     // Catch:{ Exception -> 0x094e, all -> 0x100f }
            android.media.MediaFormat r5 = r7.format     // Catch:{ Exception -> 0x094e, all -> 0x100f }
            r13 = 1
            int r1 = r1.addTrack(r5, r13)     // Catch:{ Exception -> 0x094e, all -> 0x100f }
            r5 = r3
            r3 = r4
            goto L_0x090b
        L_0x08d8:
            r0 = move-exception
            r8 = r82
            goto L_0x094f
        L_0x08dd:
            r0 = move-exception
            r8 = r82
        L_0x08e0:
            r6 = r79
            r1 = r0
            r11 = r10
            r43 = 0
            goto L_0x0a95
        L_0x08e8:
            r10 = r80
            r22 = r1
            r3 = r8
            r8 = r82
            r5 = r3
            r3 = r4
            r1 = -5
            goto L_0x090a
        L_0x08f3:
            r0 = move-exception
            r8 = r82
        L_0x08f6:
            r6 = r79
            r11 = r80
            r1 = r0
            r10 = r2
            r2 = r23
            goto L_0x0ee8
        L_0x0900:
            r10 = r80
            r8 = r82
            r22 = r1
            r1 = r4
            r3 = r1
            r1 = -5
            r5 = 1
        L_0x090a:
            r7 = 0
        L_0x090b:
            r13 = 0
        L_0x090c:
            if (r3 >= 0) goto L_0x0910
            r4 = 1
            goto L_0x0911
        L_0x0910:
            r4 = 0
        L_0x0911:
            r71.checkConversionCanceled()     // Catch:{ Exception -> 0x0e98, all -> 0x100f }
            r33 = r19
            r19 = 1
            r20 = 0
            r25 = 0
            r26 = 0
            r27 = -5
            r28 = 0
            r31 = 0
        L_0x0924:
            if (r20 == 0) goto L_0x0940
            if (r5 != 0) goto L_0x092b
            if (r4 != 0) goto L_0x092b
            goto L_0x0940
        L_0x092b:
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
            goto L_0x0var_
        L_0x0940:
            r71.checkConversionCanceled()     // Catch:{ Exception -> 0x0e98, all -> 0x100f }
            if (r5 != 0) goto L_0x0956
            if (r7 == 0) goto L_0x0956
            org.telegram.messenger.video.MP4Builder r4 = r14.mediaMuxer     // Catch:{ Exception -> 0x094e, all -> 0x100f }
            boolean r4 = r7.step(r4, r1)     // Catch:{ Exception -> 0x094e, all -> 0x100f }
            goto L_0x0956
        L_0x094e:
            r0 = move-exception
        L_0x094f:
            r6 = r79
            r1 = r0
            r43 = r7
            goto L_0x0a94
        L_0x0956:
            if (r25 != 0) goto L_0x0a98
            r35 = r4
            android.media.MediaExtractor r4 = r14.extractor     // Catch:{ Exception -> 0x0a8e, all -> 0x100f }
            int r4 = r4.getSampleTrackIndex()     // Catch:{ Exception -> 0x0a8e, all -> 0x100f }
            r43 = r7
            r7 = r65
            if (r4 != r7) goto L_0x09c9
            r10 = 2500(0x9c4, double:1.235E-320)
            int r4 = r2.dequeueInputBuffer(r10)     // Catch:{ Exception -> 0x09be, all -> 0x100f }
            if (r4 < 0) goto L_0x09af
            int r10 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x09be, all -> 0x100f }
            r11 = 21
            if (r10 >= r11) goto L_0x0977
            r10 = r6[r4]     // Catch:{ Exception -> 0x09be, all -> 0x100f }
            goto L_0x097b
        L_0x0977:
            java.nio.ByteBuffer r10 = r2.getInputBuffer(r4)     // Catch:{ Exception -> 0x09be, all -> 0x100f }
        L_0x097b:
            android.media.MediaExtractor r11 = r14.extractor     // Catch:{ Exception -> 0x09be, all -> 0x100f }
            r44 = r6
            r6 = 0
            int r55 = r11.readSampleData(r10, r6)     // Catch:{ Exception -> 0x09be, all -> 0x100f }
            if (r55 >= 0) goto L_0x0998
            r54 = 0
            r55 = 0
            r56 = 0
            r58 = 4
            r52 = r2
            r53 = r4
            r52.queueInputBuffer(r53, r54, r55, r56, r58)     // Catch:{ Exception -> 0x09be, all -> 0x100f }
            r25 = 1
            goto L_0x09b1
        L_0x0998:
            r54 = 0
            android.media.MediaExtractor r6 = r14.extractor     // Catch:{ Exception -> 0x09be, all -> 0x100f }
            long r56 = r6.getSampleTime()     // Catch:{ Exception -> 0x09be, all -> 0x100f }
            r58 = 0
            r52 = r2
            r53 = r4
            r52.queueInputBuffer(r53, r54, r55, r56, r58)     // Catch:{ Exception -> 0x09be, all -> 0x100f }
            android.media.MediaExtractor r4 = r14.extractor     // Catch:{ Exception -> 0x09be, all -> 0x100f }
            r4.advance()     // Catch:{ Exception -> 0x09be, all -> 0x100f }
            goto L_0x09b1
        L_0x09af:
            r44 = r6
        L_0x09b1:
            r38 = r3
            r37 = r5
            r45 = r13
            r5 = r25
            r6 = 0
            r12 = r80
            goto L_0x0a6b
        L_0x09be:
            r0 = move-exception
            r6 = r79
            r11 = r80
            r1 = r0
            r10 = r2
            r65 = r7
            goto L_0x0820
        L_0x09c9:
            r44 = r6
            if (r5 == 0) goto L_0x0a59
            r6 = -1
            if (r3 == r6) goto L_0x0a59
            if (r4 != r3) goto L_0x0a59
            android.media.MediaExtractor r4 = r14.extractor     // Catch:{ Exception -> 0x09be, all -> 0x100f }
            r6 = 0
            int r4 = r4.readSampleData(r13, r6)     // Catch:{ Exception -> 0x09be, all -> 0x100f }
            r15.size = r4     // Catch:{ Exception -> 0x09be, all -> 0x100f }
            int r4 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x09be, all -> 0x100f }
            r10 = 21
            if (r4 >= r10) goto L_0x09e9
            r13.position(r6)     // Catch:{ Exception -> 0x09be, all -> 0x100f }
            int r4 = r15.size     // Catch:{ Exception -> 0x09be, all -> 0x100f }
            r13.limit(r4)     // Catch:{ Exception -> 0x09be, all -> 0x100f }
        L_0x09e9:
            int r4 = r15.size     // Catch:{ Exception -> 0x09be, all -> 0x100f }
            if (r4 < 0) goto L_0x09fb
            android.media.MediaExtractor r4 = r14.extractor     // Catch:{ Exception -> 0x09be, all -> 0x100f }
            long r10 = r4.getSampleTime()     // Catch:{ Exception -> 0x09be, all -> 0x100f }
            r15.presentationTimeUs = r10     // Catch:{ Exception -> 0x09be, all -> 0x100f }
            android.media.MediaExtractor r4 = r14.extractor     // Catch:{ Exception -> 0x09be, all -> 0x100f }
            r4.advance()     // Catch:{ Exception -> 0x09be, all -> 0x100f }
            goto L_0x0a00
        L_0x09fb:
            r4 = 0
            r15.size = r4     // Catch:{ Exception -> 0x09be, all -> 0x100f }
            r25 = 1
        L_0x0a00:
            int r4 = r15.size     // Catch:{ Exception -> 0x09be, all -> 0x100f }
            if (r4 <= 0) goto L_0x0a50
            r10 = 0
            int r4 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1))
            if (r4 < 0) goto L_0x0a10
            long r10 = r15.presentationTimeUs     // Catch:{ Exception -> 0x09be, all -> 0x100f }
            int r4 = (r10 > r8 ? 1 : (r10 == r8 ? 0 : -1))
            if (r4 >= 0) goto L_0x0a50
        L_0x0a10:
            r4 = 0
            r15.offset = r4     // Catch:{ Exception -> 0x09be, all -> 0x100f }
            android.media.MediaExtractor r6 = r14.extractor     // Catch:{ Exception -> 0x09be, all -> 0x100f }
            int r6 = r6.getSampleFlags()     // Catch:{ Exception -> 0x09be, all -> 0x100f }
            r15.flags = r6     // Catch:{ Exception -> 0x09be, all -> 0x100f }
            org.telegram.messenger.video.MP4Builder r6 = r14.mediaMuxer     // Catch:{ Exception -> 0x09be, all -> 0x100f }
            long r10 = r6.writeSampleData(r1, r13, r15, r4)     // Catch:{ Exception -> 0x09be, all -> 0x100f }
            r39 = 0
            int r4 = (r10 > r39 ? 1 : (r10 == r39 ? 0 : -1))
            if (r4 == 0) goto L_0x0a50
            org.telegram.messenger.MediaController$VideoConvertorListener r4 = r14.callback     // Catch:{ Exception -> 0x09be, all -> 0x100f }
            if (r4 == 0) goto L_0x0a50
            r6 = r3
            long r3 = r15.presentationTimeUs     // Catch:{ Exception -> 0x09be, all -> 0x100f }
            r37 = r5
            r38 = r6
            r45 = r13
            r5 = 2500(0x9c4, double:1.235E-320)
            r12 = r80
            long r3 = r3 - r12
            int r46 = (r3 > r31 ? 1 : (r3 == r31 ? 0 : -1))
            if (r46 <= 0) goto L_0x0a41
            long r3 = r15.presentationTimeUs     // Catch:{ Exception -> 0x0a84, all -> 0x100f }
            long r31 = r3 - r12
        L_0x0a41:
            r3 = r31
            org.telegram.messenger.MediaController$VideoConvertorListener r5 = r14.callback     // Catch:{ Exception -> 0x0a84, all -> 0x100f }
            float r6 = (float) r3     // Catch:{ Exception -> 0x0a84, all -> 0x100f }
            float r6 = r6 / r17
            float r6 = r6 / r18
            r5.didWriteData(r10, r6)     // Catch:{ Exception -> 0x0a84, all -> 0x100f }
            r31 = r3
            goto L_0x0a68
        L_0x0a50:
            r38 = r3
            r37 = r5
            r45 = r13
            r12 = r80
            goto L_0x0a68
        L_0x0a59:
            r38 = r3
            r37 = r5
            r45 = r13
            r12 = r80
            r3 = -1
            if (r4 != r3) goto L_0x0a68
            r5 = r25
            r6 = 1
            goto L_0x0a6b
        L_0x0a68:
            r5 = r25
            r6 = 0
        L_0x0a6b:
            if (r6 == 0) goto L_0x0aa9
            r3 = 2500(0x9c4, double:1.235E-320)
            int r53 = r2.dequeueInputBuffer(r3)     // Catch:{ Exception -> 0x0a84, all -> 0x100f }
            if (r53 < 0) goto L_0x0aa9
            r54 = 0
            r55 = 0
            r56 = 0
            r58 = 4
            r52 = r2
            r52.queueInputBuffer(r53, r54, r55, r56, r58)     // Catch:{ Exception -> 0x0a84, all -> 0x100f }
            r5 = 1
            goto L_0x0aa9
        L_0x0a84:
            r0 = move-exception
            r6 = r79
            r1 = r0
            r10 = r2
        L_0x0a89:
            r65 = r7
        L_0x0a8b:
            r11 = r12
            goto L_0x0820
        L_0x0a8e:
            r0 = move-exception
            r43 = r7
            r6 = r79
            r1 = r0
        L_0x0a94:
            r11 = r10
        L_0x0a95:
            r10 = r2
            goto L_0x0820
        L_0x0a98:
            r38 = r3
            r35 = r4
            r37 = r5
            r44 = r6
            r43 = r7
            r45 = r13
            r7 = r65
            r12 = r10
            r5 = r25
        L_0x0aa9:
            r3 = r26 ^ 1
            r6 = r3
            r25 = r5
            r4 = r27
            r3 = 1
        L_0x0ab1:
            if (r6 != 0) goto L_0x0acb
            if (r3 == 0) goto L_0x0ab6
            goto L_0x0acb
        L_0x0ab6:
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
            goto L_0x0924
        L_0x0acb:
            r71.checkConversionCanceled()     // Catch:{ Exception -> 0x0e90, all -> 0x100f }
            if (r87 == 0) goto L_0x0ad5
            r10 = 22000(0x55f0, double:1.08694E-319)
            r5 = r79
            goto L_0x0ad9
        L_0x0ad5:
            r5 = r79
            r10 = 2500(0x9c4, double:1.235E-320)
        L_0x0ad9:
            int r10 = r5.dequeueOutputBuffer(r15, r10)     // Catch:{ Exception -> 0x0e8e, all -> 0x100f }
            r11 = -1
            if (r10 != r11) goto L_0x0af8
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
            goto L_0x0cf1
        L_0x0af8:
            r11 = -3
            if (r10 != r11) goto L_0x0b24
            int r11 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b1e, all -> 0x100f }
            r27 = r1
            r1 = 21
            if (r11 >= r1) goto L_0x0b07
            java.nio.ByteBuffer[] r22 = r5.getOutputBuffers()     // Catch:{ Exception -> 0x0b1e, all -> 0x100f }
        L_0x0b07:
            r60 = r2
            r42 = r3
            r48 = r6
            r65 = r7
            r2 = r21
            r7 = r62
            r9 = r64
            r6 = r66
            r3 = r67
        L_0x0b19:
            r1 = 0
            r8 = 3
            r11 = -1
            goto L_0x0cf1
        L_0x0b1e:
            r0 = move-exception
            r1 = r0
            r10 = r2
            r6 = r5
            goto L_0x0a89
        L_0x0b24:
            r27 = r1
            r1 = -2
            if (r10 != r1) goto L_0x0b8a
            android.media.MediaFormat r1 = r5.getOutputFormat()     // Catch:{ Exception -> 0x0b1e, all -> 0x100f }
            r11 = -5
            if (r4 != r11) goto L_0x0b71
            if (r1 == 0) goto L_0x0b71
            org.telegram.messenger.video.MP4Builder r4 = r14.mediaMuxer     // Catch:{ Exception -> 0x0b1e, all -> 0x100f }
            r11 = 0
            int r4 = r4.addTrack(r1, r11)     // Catch:{ Exception -> 0x0b1e, all -> 0x100f }
            r11 = r68
            boolean r47 = r1.containsKey(r11)     // Catch:{ Exception -> 0x0b1e, all -> 0x100f }
            r79 = r3
            if (r47 == 0) goto L_0x0b66
            int r3 = r1.getInteger(r11)     // Catch:{ Exception -> 0x0b1e, all -> 0x100f }
            r47 = r4
            r4 = 1
            if (r3 != r4) goto L_0x0b68
            r3 = r67
            java.nio.ByteBuffer r4 = r1.getByteBuffer(r3)     // Catch:{ Exception -> 0x0b1e, all -> 0x100f }
            r48 = r6
            r6 = r66
            java.nio.ByteBuffer r1 = r1.getByteBuffer(r6)     // Catch:{ Exception -> 0x0b1e, all -> 0x100f }
            int r4 = r4.limit()     // Catch:{ Exception -> 0x0b1e, all -> 0x100f }
            int r1 = r1.limit()     // Catch:{ Exception -> 0x0b1e, all -> 0x100f }
            int r4 = r4 + r1
            r28 = r4
            goto L_0x0b6e
        L_0x0b66:
            r47 = r4
        L_0x0b68:
            r48 = r6
            r6 = r66
            r3 = r67
        L_0x0b6e:
            r4 = r47
            goto L_0x0b7b
        L_0x0b71:
            r79 = r3
            r48 = r6
            r6 = r66
            r3 = r67
            r11 = r68
        L_0x0b7b:
            r42 = r79
            r60 = r2
            r65 = r7
            r68 = r11
            r2 = r21
            r7 = r62
            r9 = r64
            goto L_0x0b19
        L_0x0b8a:
            r79 = r3
            r48 = r6
            r6 = r66
            r3 = r67
            r11 = r68
            if (r10 < 0) goto L_0x0e6c
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0e8e, all -> 0x100f }
            r68 = r11
            r11 = 21
            if (r1 >= r11) goto L_0x0ba1
            r1 = r22[r10]     // Catch:{ Exception -> 0x0b1e, all -> 0x100f }
            goto L_0x0ba5
        L_0x0ba1:
            java.nio.ByteBuffer r1 = r5.getOutputBuffer(r10)     // Catch:{ Exception -> 0x0e8e, all -> 0x100f }
        L_0x0ba5:
            if (r1 == 0) goto L_0x0e4a
            int r11 = r15.size     // Catch:{ Exception -> 0x0e45, all -> 0x100f }
            r65 = r7
            r7 = 1
            if (r11 <= r7) goto L_0x0cd8
            int r11 = r15.flags     // Catch:{ Exception -> 0x0ccc, all -> 0x100f }
            r11 = r11 & 2
            if (r11 != 0) goto L_0x0CLASSNAME
            if (r28 == 0) goto L_0x0bcd
            int r11 = r15.flags     // Catch:{ Exception -> 0x0bc8, all -> 0x100f }
            r11 = r11 & r7
            if (r11 == 0) goto L_0x0bcd
            int r7 = r15.offset     // Catch:{ Exception -> 0x0bc8, all -> 0x100f }
            int r7 = r7 + r28
            r15.offset = r7     // Catch:{ Exception -> 0x0bc8, all -> 0x100f }
            int r7 = r15.size     // Catch:{ Exception -> 0x0bc8, all -> 0x100f }
            int r7 = r7 - r28
            r15.size = r7     // Catch:{ Exception -> 0x0bc8, all -> 0x100f }
            goto L_0x0bcd
        L_0x0bc8:
            r0 = move-exception
            r1 = r0
            r10 = r2
            goto L_0x0d50
        L_0x0bcd:
            if (r19 == 0) goto L_0x0c1b
            int r7 = r15.flags     // Catch:{ Exception -> 0x0bc8, all -> 0x100f }
            r11 = 1
            r7 = r7 & r11
            if (r7 == 0) goto L_0x0c1b
            int r7 = r15.size     // Catch:{ Exception -> 0x0bc8, all -> 0x100f }
            r11 = 100
            if (r7 <= r11) goto L_0x0CLASSNAME
            int r7 = r15.offset     // Catch:{ Exception -> 0x0bc8, all -> 0x100f }
            r1.position(r7)     // Catch:{ Exception -> 0x0bc8, all -> 0x100f }
            byte[] r7 = new byte[r11]     // Catch:{ Exception -> 0x0bc8, all -> 0x100f }
            r1.get(r7)     // Catch:{ Exception -> 0x0bc8, all -> 0x100f }
            r11 = 0
            r19 = 0
        L_0x0be8:
            r8 = 96
            if (r11 >= r8) goto L_0x0CLASSNAME
            byte r8 = r7[r11]     // Catch:{ Exception -> 0x0bc8, all -> 0x100f }
            if (r8 != 0) goto L_0x0CLASSNAME
            int r8 = r11 + 1
            byte r8 = r7[r8]     // Catch:{ Exception -> 0x0bc8, all -> 0x100f }
            if (r8 != 0) goto L_0x0CLASSNAME
            int r8 = r11 + 2
            byte r8 = r7[r8]     // Catch:{ Exception -> 0x0bc8, all -> 0x100f }
            if (r8 != 0) goto L_0x0CLASSNAME
            int r8 = r11 + 3
            byte r8 = r7[r8]     // Catch:{ Exception -> 0x0bc8, all -> 0x100f }
            r9 = 1
            if (r8 != r9) goto L_0x0CLASSNAME
            int r8 = r19 + 1
            if (r8 <= r9) goto L_0x0CLASSNAME
            int r7 = r15.offset     // Catch:{ Exception -> 0x0bc8, all -> 0x100f }
            int r7 = r7 + r11
            r15.offset = r7     // Catch:{ Exception -> 0x0bc8, all -> 0x100f }
            int r7 = r15.size     // Catch:{ Exception -> 0x0bc8, all -> 0x100f }
            int r7 = r7 - r11
            r15.size = r7     // Catch:{ Exception -> 0x0bc8, all -> 0x100f }
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r19 = r8
        L_0x0CLASSNAME:
            int r11 = r11 + 1
            r8 = r82
            goto L_0x0be8
        L_0x0CLASSNAME:
            r19 = 0
        L_0x0c1b:
            org.telegram.messenger.video.MP4Builder r7 = r14.mediaMuxer     // Catch:{ Exception -> 0x0ccc, all -> 0x100f }
            r60 = r2
            r8 = 1
            long r1 = r7.writeSampleData(r4, r1, r15, r8)     // Catch:{ Exception -> 0x0cca, all -> 0x100f }
            r7 = 0
            int r9 = (r1 > r7 ? 1 : (r1 == r7 ? 0 : -1))
            if (r9 == 0) goto L_0x0CLASSNAME
            org.telegram.messenger.MediaController$VideoConvertorListener r7 = r14.callback     // Catch:{ Exception -> 0x0cca, all -> 0x100f }
            if (r7 == 0) goto L_0x0CLASSNAME
            long r7 = r15.presentationTimeUs     // Catch:{ Exception -> 0x0cca, all -> 0x100f }
            long r7 = r7 - r12
            int r9 = (r7 > r31 ? 1 : (r7 == r31 ? 0 : -1))
            if (r9 <= 0) goto L_0x0CLASSNAME
            long r7 = r15.presentationTimeUs     // Catch:{ Exception -> 0x0cca, all -> 0x100f }
            long r31 = r7 - r12
        L_0x0CLASSNAME:
            r7 = r31
            org.telegram.messenger.MediaController$VideoConvertorListener r9 = r14.callback     // Catch:{ Exception -> 0x0cca, all -> 0x100f }
            float r11 = (float) r7     // Catch:{ Exception -> 0x0cca, all -> 0x100f }
            float r11 = r11 / r17
            float r11 = r11 / r18
            r9.didWriteData(r1, r11)     // Catch:{ Exception -> 0x0cca, all -> 0x100f }
            r31 = r7
        L_0x0CLASSNAME:
            r2 = r21
            r7 = r62
            r9 = r64
            r8 = 3
            goto L_0x0cdc
        L_0x0CLASSNAME:
            r60 = r2
            r2 = -5
            if (r4 != r2) goto L_0x0CLASSNAME
            int r4 = r15.size     // Catch:{ Exception -> 0x0cca, all -> 0x100f }
            byte[] r4 = new byte[r4]     // Catch:{ Exception -> 0x0cca, all -> 0x100f }
            int r7 = r15.offset     // Catch:{ Exception -> 0x0cca, all -> 0x100f }
            int r8 = r15.size     // Catch:{ Exception -> 0x0cca, all -> 0x100f }
            int r7 = r7 + r8
            r1.limit(r7)     // Catch:{ Exception -> 0x0cca, all -> 0x100f }
            int r7 = r15.offset     // Catch:{ Exception -> 0x0cca, all -> 0x100f }
            r1.position(r7)     // Catch:{ Exception -> 0x0cca, all -> 0x100f }
            r1.get(r4)     // Catch:{ Exception -> 0x0cca, all -> 0x100f }
            int r1 = r15.size     // Catch:{ Exception -> 0x0cca, all -> 0x100f }
            r7 = 1
            int r1 = r1 - r7
        L_0x0c6d:
            r8 = 3
            if (r1 < 0) goto L_0x0cab
            if (r1 <= r8) goto L_0x0cab
            byte r9 = r4[r1]     // Catch:{ Exception -> 0x0cca, all -> 0x100f }
            if (r9 != r7) goto L_0x0ca6
            int r9 = r1 + -1
            byte r9 = r4[r9]     // Catch:{ Exception -> 0x0cca, all -> 0x100f }
            if (r9 != 0) goto L_0x0ca6
            int r9 = r1 + -2
            byte r9 = r4[r9]     // Catch:{ Exception -> 0x0cca, all -> 0x100f }
            if (r9 != 0) goto L_0x0ca6
            int r9 = r1 + -3
            byte r11 = r4[r9]     // Catch:{ Exception -> 0x0cca, all -> 0x100f }
            if (r11 != 0) goto L_0x0ca6
            java.nio.ByteBuffer r1 = java.nio.ByteBuffer.allocate(r9)     // Catch:{ Exception -> 0x0cca, all -> 0x100f }
            int r11 = r15.size     // Catch:{ Exception -> 0x0cca, all -> 0x100f }
            int r11 = r11 - r9
            java.nio.ByteBuffer r11 = java.nio.ByteBuffer.allocate(r11)     // Catch:{ Exception -> 0x0cca, all -> 0x100f }
            r2 = 0
            java.nio.ByteBuffer r7 = r1.put(r4, r2, r9)     // Catch:{ Exception -> 0x0cca, all -> 0x100f }
            r7.position(r2)     // Catch:{ Exception -> 0x0cca, all -> 0x100f }
            int r7 = r15.size     // Catch:{ Exception -> 0x0cca, all -> 0x100f }
            int r7 = r7 - r9
            java.nio.ByteBuffer r4 = r11.put(r4, r9, r7)     // Catch:{ Exception -> 0x0cca, all -> 0x100f }
            r4.position(r2)     // Catch:{ Exception -> 0x0cca, all -> 0x100f }
            goto L_0x0cad
        L_0x0ca6:
            int r1 = r1 + -1
            r2 = -5
            r7 = 1
            goto L_0x0c6d
        L_0x0cab:
            r1 = 0
            r11 = 0
        L_0x0cad:
            r2 = r21
            r7 = r62
            r9 = r64
            android.media.MediaFormat r4 = android.media.MediaFormat.createVideoFormat(r9, r2, r7)     // Catch:{ Exception -> 0x0cca, all -> 0x100f }
            if (r1 == 0) goto L_0x0cc1
            if (r11 == 0) goto L_0x0cc1
            r4.setByteBuffer(r3, r1)     // Catch:{ Exception -> 0x0cca, all -> 0x100f }
            r4.setByteBuffer(r6, r11)     // Catch:{ Exception -> 0x0cca, all -> 0x100f }
        L_0x0cc1:
            org.telegram.messenger.video.MP4Builder r1 = r14.mediaMuxer     // Catch:{ Exception -> 0x0cca, all -> 0x100f }
            r11 = 0
            int r1 = r1.addTrack(r4, r11)     // Catch:{ Exception -> 0x0cca, all -> 0x100f }
            r4 = r1
            goto L_0x0cdc
        L_0x0cca:
            r0 = move-exception
            goto L_0x0ccf
        L_0x0ccc:
            r0 = move-exception
            r60 = r2
        L_0x0ccf:
            r1 = r0
            r6 = r5
            r11 = r12
            r2 = r23
            r10 = r60
            goto L_0x0f1c
        L_0x0cd8:
            r60 = r2
            goto L_0x0CLASSNAME
        L_0x0cdc:
            int r1 = r15.flags     // Catch:{ Exception -> 0x0e40, all -> 0x100f }
            r1 = r1 & 4
            if (r1 == 0) goto L_0x0ce6
            r1 = 0
            r42 = 1
            goto L_0x0ce9
        L_0x0ce6:
            r1 = 0
            r42 = 0
        L_0x0ce9:
            r5.releaseOutputBuffer(r10, r1)     // Catch:{ Exception -> 0x0e40, all -> 0x100f }
            r20 = r42
            r11 = -1
            r42 = r79
        L_0x0cf1:
            if (r10 == r11) goto L_0x0d0d
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
        L_0x0d07:
            r7 = r65
            r8 = r82
            goto L_0x0ab1
        L_0x0d0d:
            if (r26 != 0) goto L_0x0e21
            r21 = r2
            r10 = r60
            r1 = 2500(0x9c4, double:1.235E-320)
            int r8 = r10.dequeueOutputBuffer(r15, r1)     // Catch:{ Exception -> 0x0e19, all -> 0x100f }
            if (r8 != r11) goto L_0x0d26
            r67 = r3
            r11 = r12
            r3 = r63
            r39 = 0
        L_0x0d22:
            r48 = 0
            goto L_0x0e2c
        L_0x0d26:
            r1 = -3
            if (r8 != r1) goto L_0x0d2e
        L_0x0d29:
            r67 = r3
            r11 = r12
            goto L_0x0e28
        L_0x0d2e:
            r1 = -2
            if (r8 != r1) goto L_0x0d53
            android.media.MediaFormat r1 = r10.getOutputFormat()     // Catch:{ Exception -> 0x0d4e, all -> 0x100f }
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0d4e, all -> 0x100f }
            if (r2 == 0) goto L_0x0d29
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0d4e, all -> 0x100f }
            r2.<init>()     // Catch:{ Exception -> 0x0d4e, all -> 0x100f }
            java.lang.String r8 = "newFormat = "
            r2.append(r8)     // Catch:{ Exception -> 0x0d4e, all -> 0x100f }
            r2.append(r1)     // Catch:{ Exception -> 0x0d4e, all -> 0x100f }
            java.lang.String r1 = r2.toString()     // Catch:{ Exception -> 0x0d4e, all -> 0x100f }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x0d4e, all -> 0x100f }
            goto L_0x0d29
        L_0x0d4e:
            r0 = move-exception
            r1 = r0
        L_0x0d50:
            r6 = r5
            goto L_0x0a8b
        L_0x0d53:
            if (r8 < 0) goto L_0x0dfa
            int r1 = r15.size     // Catch:{ Exception -> 0x0e19, all -> 0x100f }
            if (r1 == 0) goto L_0x0d5b
            r1 = 1
            goto L_0x0d5c
        L_0x0d5b:
            r1 = 0
        L_0x0d5c:
            r39 = 0
            int r2 = (r82 > r39 ? 1 : (r82 == r39 ? 0 : -1))
            if (r2 <= 0) goto L_0x0d7b
            long r11 = r15.presentationTimeUs     // Catch:{ Exception -> 0x0d76, all -> 0x100f }
            int r13 = (r11 > r82 ? 1 : (r11 == r82 ? 0 : -1))
            if (r13 < 0) goto L_0x0d7b
            int r1 = r15.flags     // Catch:{ Exception -> 0x0d76, all -> 0x100f }
            r1 = r1 | 4
            r15.flags = r1     // Catch:{ Exception -> 0x0d76, all -> 0x100f }
            r11 = r80
            r1 = 0
            r25 = 1
            r26 = 1
            goto L_0x0d7d
        L_0x0d76:
            r0 = move-exception
            r11 = r80
            goto L_0x0e1d
        L_0x0d7b:
            r11 = r80
        L_0x0d7d:
            r39 = 0
            int r13 = (r11 > r39 ? 1 : (r11 == r39 ? 0 : -1))
            if (r13 <= 0) goto L_0x0dbc
            r49 = -1
            int r13 = (r33 > r49 ? 1 : (r33 == r49 ? 0 : -1))
            if (r13 != 0) goto L_0x0dbc
            r67 = r3
            long r2 = r15.presentationTimeUs     // Catch:{ Exception -> 0x0dba, all -> 0x100f }
            int r13 = (r2 > r11 ? 1 : (r2 == r11 ? 0 : -1))
            if (r13 >= 0) goto L_0x0db5
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0dba, all -> 0x100f }
            if (r1 == 0) goto L_0x0db3
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0dba, all -> 0x100f }
            r1.<init>()     // Catch:{ Exception -> 0x0dba, all -> 0x100f }
            java.lang.String r2 = "drop frame startTime = "
            r1.append(r2)     // Catch:{ Exception -> 0x0dba, all -> 0x100f }
            r1.append(r11)     // Catch:{ Exception -> 0x0dba, all -> 0x100f }
            java.lang.String r2 = " present time = "
            r1.append(r2)     // Catch:{ Exception -> 0x0dba, all -> 0x100f }
            long r2 = r15.presentationTimeUs     // Catch:{ Exception -> 0x0dba, all -> 0x100f }
            r1.append(r2)     // Catch:{ Exception -> 0x0dba, all -> 0x100f }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x0dba, all -> 0x100f }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x0dba, all -> 0x100f }
        L_0x0db3:
            r1 = 0
            goto L_0x0dbe
        L_0x0db5:
            long r2 = r15.presentationTimeUs     // Catch:{ Exception -> 0x0dba, all -> 0x100f }
            r33 = r2
            goto L_0x0dbe
        L_0x0dba:
            r0 = move-exception
            goto L_0x0e1d
        L_0x0dbc:
            r67 = r3
        L_0x0dbe:
            r10.releaseOutputBuffer(r8, r1)     // Catch:{ Exception -> 0x0df8, all -> 0x100f }
            if (r1 == 0) goto L_0x0de2
            r24.awaitNewImage()     // Catch:{ Exception -> 0x0dc8, all -> 0x100f }
            r1 = 0
            goto L_0x0dce
        L_0x0dc8:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)     // Catch:{ Exception -> 0x0df8, all -> 0x100f }
            r1 = 1
        L_0x0dce:
            if (r1 != 0) goto L_0x0de2
            r24.drawImage()     // Catch:{ Exception -> 0x0df8, all -> 0x100f }
            long r1 = r15.presentationTimeUs     // Catch:{ Exception -> 0x0df8, all -> 0x100f }
            r49 = 1000(0x3e8, double:4.94E-321)
            long r1 = r1 * r49
            r3 = r63
            r3.setPresentationTime(r1)     // Catch:{ Exception -> 0x0e14, all -> 0x100f }
            r3.swapBuffers()     // Catch:{ Exception -> 0x0e14, all -> 0x100f }
            goto L_0x0de4
        L_0x0de2:
            r3 = r63
        L_0x0de4:
            int r1 = r15.flags     // Catch:{ Exception -> 0x0e14, all -> 0x100f }
            r1 = r1 & 4
            if (r1 == 0) goto L_0x0e2c
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0e14, all -> 0x100f }
            if (r1 == 0) goto L_0x0df3
            java.lang.String r1 = "decoder stream end"
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x0e14, all -> 0x100f }
        L_0x0df3:
            r5.signalEndOfInputStream()     // Catch:{ Exception -> 0x0e14, all -> 0x100f }
            goto L_0x0d22
        L_0x0df8:
            r0 = move-exception
            goto L_0x0e1b
        L_0x0dfa:
            r11 = r12
            r3 = r63
            java.lang.RuntimeException r1 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x0e14, all -> 0x100f }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0e14, all -> 0x100f }
            r2.<init>()     // Catch:{ Exception -> 0x0e14, all -> 0x100f }
            java.lang.String r4 = "unexpected result from decoder.dequeueOutputBuffer: "
            r2.append(r4)     // Catch:{ Exception -> 0x0e14, all -> 0x100f }
            r2.append(r8)     // Catch:{ Exception -> 0x0e14, all -> 0x100f }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0e14, all -> 0x100f }
            r1.<init>(r2)     // Catch:{ Exception -> 0x0e14, all -> 0x100f }
            throw r1     // Catch:{ Exception -> 0x0e14, all -> 0x100f }
        L_0x0e14:
            r0 = move-exception
            r1 = r0
            r63 = r3
            goto L_0x0e1e
        L_0x0e19:
            r0 = move-exception
        L_0x0e1a:
            r11 = r12
        L_0x0e1b:
            r3 = r63
        L_0x0e1d:
            r1 = r0
        L_0x0e1e:
            r6 = r5
            goto L_0x0820
        L_0x0e21:
            r21 = r2
            r67 = r3
            r11 = r12
            r10 = r60
        L_0x0e28:
            r3 = r63
            r39 = 0
        L_0x0e2c:
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
            goto L_0x0d07
        L_0x0e40:
            r0 = move-exception
            r11 = r12
            r10 = r60
            goto L_0x0e1b
        L_0x0e45:
            r0 = move-exception
            r10 = r2
            r65 = r7
            goto L_0x0e1a
        L_0x0e4a:
            r1 = r2
            r65 = r7
            r11 = r12
            r3 = r63
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x0e89, all -> 0x100f }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0e89, all -> 0x100f }
            r4.<init>()     // Catch:{ Exception -> 0x0e89, all -> 0x100f }
            java.lang.String r6 = "encoderOutputBuffer "
            r4.append(r6)     // Catch:{ Exception -> 0x0e89, all -> 0x100f }
            r4.append(r10)     // Catch:{ Exception -> 0x0e89, all -> 0x100f }
            java.lang.String r6 = " was null"
            r4.append(r6)     // Catch:{ Exception -> 0x0e89, all -> 0x100f }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0e89, all -> 0x100f }
            r2.<init>(r4)     // Catch:{ Exception -> 0x0e89, all -> 0x100f }
            throw r2     // Catch:{ Exception -> 0x0e89, all -> 0x100f }
        L_0x0e6c:
            r1 = r2
            r65 = r7
            r11 = r12
            r3 = r63
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x0e89, all -> 0x100f }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0e89, all -> 0x100f }
            r4.<init>()     // Catch:{ Exception -> 0x0e89, all -> 0x100f }
            java.lang.String r6 = "unexpected result from encoder.dequeueOutputBuffer: "
            r4.append(r6)     // Catch:{ Exception -> 0x0e89, all -> 0x100f }
            r4.append(r10)     // Catch:{ Exception -> 0x0e89, all -> 0x100f }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0e89, all -> 0x100f }
            r2.<init>(r4)     // Catch:{ Exception -> 0x0e89, all -> 0x100f }
            throw r2     // Catch:{ Exception -> 0x0e89, all -> 0x100f }
        L_0x0e89:
            r0 = move-exception
            r10 = r1
            r63 = r3
            goto L_0x0ea2
        L_0x0e8e:
            r0 = move-exception
            goto L_0x0e93
        L_0x0e90:
            r0 = move-exception
            r5 = r79
        L_0x0e93:
            r1 = r2
            r65 = r7
            r11 = r12
            goto L_0x0e9f
        L_0x0e98:
            r0 = move-exception
            r5 = r79
            r1 = r2
            r43 = r7
            r11 = r10
        L_0x0e9f:
            r3 = r63
            r10 = r1
        L_0x0ea2:
            r6 = r5
            r2 = r23
            goto L_0x0ed7
        L_0x0ea6:
            r0 = move-exception
            r5 = r79
            r11 = r80
            r1 = r2
            goto L_0x0eb4
        L_0x0ead:
            r0 = move-exception
            r5 = r79
            r11 = r80
            r1 = r60
        L_0x0eb4:
            r3 = r63
            r10 = r1
            r6 = r5
            r2 = r23
            goto L_0x0ed5
        L_0x0ebb:
            r0 = move-exception
            r5 = r79
            r11 = r80
            r1 = r60
            r3 = r63
            r10 = r1
            goto L_0x0ed0
        L_0x0ec6:
            r0 = move-exception
            r1 = r2
            r3 = r4
            r5 = r8
            r11 = r9
            r65 = r21
            r10 = r1
            r63 = r3
        L_0x0ed0:
            r6 = r5
            r2 = r23
            r24 = 0
        L_0x0ed5:
            r43 = 0
        L_0x0ed7:
            r1 = r0
            goto L_0x0f1c
        L_0x0ed9:
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
        L_0x0ee8:
            r43 = 0
            goto L_0x0f1c
        L_0x0eeb:
            r0 = move-exception
            r5 = r8
            r11 = r9
            r65 = r21
            r1 = r0
            r6 = r5
            r2 = r23
            goto L_0x0var_
        L_0x0ef5:
            r0 = move-exception
            r11 = r9
            r65 = r21
            r1 = r0
            r2 = r23
            goto L_0x0var_
        L_0x0efd:
            r0 = move-exception
            r11 = r9
            r65 = r21
            goto L_0x0var_
        L_0x0var_:
            r0 = move-exception
            r65 = r6
            r11 = r9
            goto L_0x0var_
        L_0x0var_:
            r0 = move-exception
            r65 = r6
            r11 = r9
            goto L_0x0var_
        L_0x0f0c:
            r0 = move-exception
            r11 = r80
            r65 = r6
        L_0x0var_:
            r2 = r79
        L_0x0var_:
            r1 = r0
        L_0x0var_:
            r6 = 0
        L_0x0var_:
            r10 = 0
            r24 = 0
            r43 = 0
            r63 = 0
        L_0x0f1c:
            boolean r3 = r1 instanceof java.lang.IllegalStateException     // Catch:{ Exception -> 0x0var_, all -> 0x100f }
            if (r3 == 0) goto L_0x0var_
            if (r87 != 0) goto L_0x0var_
            r30 = 1
            goto L_0x0var_
        L_0x0var_:
            r30 = 0
        L_0x0var_:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0f8d, all -> 0x100f }
            r3.<init>()     // Catch:{ Exception -> 0x0f8d, all -> 0x100f }
            r4 = r36
            r3.append(r4)     // Catch:{ Exception -> 0x0var_, all -> 0x100f }
            r3.append(r2)     // Catch:{ Exception -> 0x0var_, all -> 0x100f }
            java.lang.String r5 = " framerate: "
            r3.append(r5)     // Catch:{ Exception -> 0x0var_, all -> 0x100f }
            r8 = r78
            r3.append(r8)     // Catch:{ Exception -> 0x0f7f, all -> 0x100f }
            java.lang.String r5 = " size: "
            r3.append(r5)     // Catch:{ Exception -> 0x0f7f, all -> 0x100f }
            r5 = r77
            r3.append(r5)     // Catch:{ Exception -> 0x0f7b, all -> 0x100f }
            java.lang.String r7 = "x"
            r3.append(r7)     // Catch:{ Exception -> 0x0f7b, all -> 0x100f }
            r7 = r76
            r3.append(r7)     // Catch:{ Exception -> 0x0fbe, all -> 0x100f }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x0fbe, all -> 0x100f }
            org.telegram.messenger.FileLog.e((java.lang.String) r3)     // Catch:{ Exception -> 0x0fbe, all -> 0x100f }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)     // Catch:{ Exception -> 0x0fbe, all -> 0x100f }
            r1 = r6
            r6 = r30
            r42 = 1
        L_0x0var_:
            android.media.MediaExtractor r3 = r14.extractor     // Catch:{ Exception -> 0x0var_, all -> 0x100f }
            r9 = r65
            r3.unselectTrack(r9)     // Catch:{ Exception -> 0x0var_, all -> 0x100f }
            if (r10 == 0) goto L_0x0var_
            r10.stop()     // Catch:{ Exception -> 0x0var_, all -> 0x100f }
            r10.release()     // Catch:{ Exception -> 0x0var_, all -> 0x100f }
        L_0x0var_:
            r30 = r6
            r6 = r24
            r41 = r63
            goto L_0x0fb8
        L_0x0var_:
            r0 = move-exception
            r1 = r0
            goto L_0x1021
        L_0x0f7b:
            r0 = move-exception
            r7 = r76
            goto L_0x0fbf
        L_0x0f7f:
            r0 = move-exception
            r7 = r76
            r5 = r77
            goto L_0x0fbf
        L_0x0var_:
            r0 = move-exception
            r7 = r76
            r5 = r77
            r8 = r78
            goto L_0x0fbf
        L_0x0f8d:
            r0 = move-exception
            r7 = r76
            r5 = r77
            r8 = r78
            r4 = r36
            goto L_0x0fbf
        L_0x0var_:
            r0 = move-exception
            r7 = r76
            r5 = r77
            r8 = r78
            r4 = r36
            goto L_0x101f
        L_0x0fa2:
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
        L_0x0fb8:
            if (r6 == 0) goto L_0x0fc4
            r6.release()     // Catch:{ Exception -> 0x0fbe, all -> 0x100f }
            goto L_0x0fc4
        L_0x0fbe:
            r0 = move-exception
        L_0x0fbf:
            r1 = r0
            r6 = r30
            goto L_0x1021
        L_0x0fc4:
            if (r41 == 0) goto L_0x0fc9
            r41.release()     // Catch:{ Exception -> 0x0fbe, all -> 0x100f }
        L_0x0fc9:
            if (r1 == 0) goto L_0x0fd1
            r1.stop()     // Catch:{ Exception -> 0x0fbe, all -> 0x100f }
            r1.release()     // Catch:{ Exception -> 0x0fbe, all -> 0x100f }
        L_0x0fd1:
            if (r43 == 0) goto L_0x0fd6
            r43.release()     // Catch:{ Exception -> 0x0fbe, all -> 0x100f }
        L_0x0fd6:
            r71.checkConversionCanceled()     // Catch:{ Exception -> 0x0fbe, all -> 0x100f }
            r13 = r30
        L_0x0fdb:
            r6 = r42
        L_0x0fdd:
            android.media.MediaExtractor r1 = r14.extractor
            if (r1 == 0) goto L_0x0fe4
            r1.release()
        L_0x0fe4:
            org.telegram.messenger.video.MP4Builder r1 = r14.mediaMuxer
            if (r1 == 0) goto L_0x0ff1
            r1.finishMovie()     // Catch:{ Exception -> 0x0fec }
            goto L_0x0ff1
        L_0x0fec:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x0ff1:
            r9 = r2
            r69 = r7
            r7 = r5
            r5 = r6
            r6 = r69
            goto L_0x1067
        L_0x0ffa:
            r0 = move-exception
            r8 = r10
            r5 = r11
            r7 = r12
            r4 = r13
            goto L_0x100c
        L_0x1000:
            r0 = move-exception
            r8 = r10
            r5 = r11
            r4 = r13
            r69 = r6
            goto L_0x101a
        L_0x1007:
            r0 = move-exception
            r4 = r1
            r8 = r10
            r5 = r11
            r7 = r12
        L_0x100c:
            r11 = r80
            goto L_0x101d
        L_0x100f:
            r0 = move-exception
            r2 = r0
            r1 = r14
            goto L_0x1093
        L_0x1014:
            r0 = move-exception
            r4 = r1
            r5 = r11
            r69 = r7
            r8 = r10
        L_0x101a:
            r7 = r12
            r11 = r69
        L_0x101d:
            r2 = r79
        L_0x101f:
            r1 = r0
        L_0x1020:
            r6 = 0
        L_0x1021:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x108f }
            r3.<init>()     // Catch:{ all -> 0x108f }
            r3.append(r4)     // Catch:{ all -> 0x108f }
            r3.append(r2)     // Catch:{ all -> 0x108f }
            java.lang.String r4 = " framerate: "
            r3.append(r4)     // Catch:{ all -> 0x108f }
            r3.append(r8)     // Catch:{ all -> 0x108f }
            java.lang.String r4 = " size: "
            r3.append(r4)     // Catch:{ all -> 0x108f }
            r3.append(r5)     // Catch:{ all -> 0x108f }
            java.lang.String r4 = "x"
            r3.append(r4)     // Catch:{ all -> 0x108f }
            r3.append(r7)     // Catch:{ all -> 0x108f }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x108f }
            org.telegram.messenger.FileLog.e((java.lang.String) r3)     // Catch:{ all -> 0x108f }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)     // Catch:{ all -> 0x108f }
            android.media.MediaExtractor r1 = r14.extractor
            if (r1 == 0) goto L_0x1055
            r1.release()
        L_0x1055:
            org.telegram.messenger.video.MP4Builder r1 = r14.mediaMuxer
            if (r1 == 0) goto L_0x1062
            r1.finishMovie()     // Catch:{ Exception -> 0x105d }
            goto L_0x1062
        L_0x105d:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x1062:
            r9 = r2
            r13 = r6
            r6 = r7
            r7 = r5
            r5 = 1
        L_0x1067:
            if (r13 == 0) goto L_0x108e
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
        L_0x108e:
            return r5
        L_0x108f:
            r0 = move-exception
            r1 = r71
            r2 = r0
        L_0x1093:
            android.media.MediaExtractor r3 = r1.extractor
            if (r3 == 0) goto L_0x109a
            r3.release()
        L_0x109a:
            org.telegram.messenger.video.MP4Builder r3 = r1.mediaMuxer
            if (r3 == 0) goto L_0x10a7
            r3.finishMovie()     // Catch:{ Exception -> 0x10a2 }
            goto L_0x10a7
        L_0x10a2:
            r0 = move-exception
            r3 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)
        L_0x10a7:
            goto L_0x10a9
        L_0x10a8:
            throw r2
        L_0x10a9:
            goto L_0x10a8
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
