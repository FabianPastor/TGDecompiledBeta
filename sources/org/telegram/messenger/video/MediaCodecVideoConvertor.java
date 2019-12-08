package org.telegram.messenger.video;

import android.media.MediaExtractor;
import java.io.File;
import org.telegram.messenger.MediaController.VideoConvertorListener;

public class MediaCodecVideoConvertor {
    private static final int MEDIACODEC_TIMEOUT_DEFAULT = 2500;
    private static final int MEDIACODEC_TIMEOUT_INCREASED = 22000;
    private static final int PROCESSOR_TYPE_INTEL = 2;
    private static final int PROCESSOR_TYPE_MTK = 3;
    private static final int PROCESSOR_TYPE_OTHER = 0;
    private static final int PROCESSOR_TYPE_QCOM = 1;
    private static final int PROCESSOR_TYPE_SEC = 4;
    private static final int PROCESSOR_TYPE_TI = 5;
    VideoConvertorListener callback;
    MediaExtractor extractor;
    MP4Builder mediaMuxer;

    public boolean convertVideo(String str, File file, int i, boolean z, int i2, int i3, int i4, int i5, long j, long j2, boolean z2, long j3, VideoConvertorListener videoConvertorListener) {
        String str2 = str;
        File file2 = file;
        int i6 = i;
        boolean z3 = z;
        int i7 = i2;
        int i8 = i3;
        int i9 = i4;
        int i10 = i5;
        long j4 = j;
        long j5 = j2;
        boolean z4 = z2;
        long j6 = j3;
        this.callback = videoConvertorListener;
        return convertVideoInternal(str, file2, i6, z3, i7, i8, i9, i10, j4, j5, j6, z4, false);
    }

    /* JADX WARNING: Unknown top exception splitter block from list: {B:157:0x02a2=Splitter:B:157:0x02a2, B:137:0x0265=Splitter:B:137:0x0265} */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x010e A:{Catch:{ Exception -> 0x011f, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x00df A:{Catch:{ Exception -> 0x011f, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x00df A:{Catch:{ Exception -> 0x011f, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x010e A:{Catch:{ Exception -> 0x011f, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:350:0x058b A:{Catch:{ Exception -> 0x05a8, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:564:0x097c A:{Catch:{ Exception -> 0x0a2d, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:554:0x094f A:{Catch:{ Exception -> 0x0a2d, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:554:0x094f A:{Catch:{ Exception -> 0x0a2d, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:564:0x097c A:{Catch:{ Exception -> 0x0a2d, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:458:0x0753 A:{Catch:{ Exception -> 0x09a2, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:457:0x0746 A:{Catch:{ Exception -> 0x09a2, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:258:0x03f6  */
    /* JADX WARNING: Removed duplicated region for block: B:218:0x0355 A:{SYNTHETIC, Splitter:B:218:0x0355} */
    /* JADX WARNING: Removed duplicated region for block: B:262:0x041e A:{Catch:{ Exception -> 0x0a50, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:363:0x05c0  */
    /* JADX WARNING: Removed duplicated region for block: B:278:0x0462 A:{SYNTHETIC, Splitter:B:278:0x0462} */
    /* JADX WARNING: Removed duplicated region for block: B:372:0x05f6 A:{Catch:{ Exception -> 0x0a3a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:371:0x05f3 A:{Catch:{ Exception -> 0x0a3a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:378:0x0613  */
    /* JADX WARNING: Removed duplicated region for block: B:376:0x05ff  */
    /* JADX WARNING: Removed duplicated region for block: B:457:0x0746 A:{Catch:{ Exception -> 0x09a2, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:458:0x0753 A:{Catch:{ Exception -> 0x09a2, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:621:0x0b59 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:623:0x0b5e A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:625:0x0b63 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:627:0x0b6b A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:629:0x0b73 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:98:0x01c7 A:{SYNTHETIC, Splitter:B:98:0x01c7} */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x01be A:{SYNTHETIC, Splitter:B:95:0x01be} */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x01d7  */
    /* JADX WARNING: Removed duplicated region for block: B:104:0x01d3  */
    /* JADX WARNING: Removed duplicated region for block: B:149:0x0290  */
    /* JADX WARNING: Removed duplicated region for block: B:110:0x01f7 A:{SYNTHETIC, Splitter:B:110:0x01f7} */
    /* JADX WARNING: Removed duplicated region for block: B:164:0x02ae A:{SYNTHETIC, Splitter:B:164:0x02ae} */
    /* JADX WARNING: Removed duplicated region for block: B:186:0x02ef  */
    /* JADX WARNING: Removed duplicated region for block: B:177:0x02cd A:{SYNTHETIC, Splitter:B:177:0x02cd} */
    /* JADX WARNING: Removed duplicated region for block: B:197:0x0311 A:{SYNTHETIC, Splitter:B:197:0x0311} */
    /* JADX WARNING: Removed duplicated region for block: B:193:0x0303 A:{SYNTHETIC, Splitter:B:193:0x0303} */
    /* JADX WARNING: Removed duplicated region for block: B:215:0x0350  */
    /* JADX WARNING: Removed duplicated region for block: B:206:0x032e A:{SYNTHETIC, Splitter:B:206:0x032e} */
    /* JADX WARNING: Removed duplicated region for block: B:218:0x0355 A:{SYNTHETIC, Splitter:B:218:0x0355} */
    /* JADX WARNING: Removed duplicated region for block: B:258:0x03f6  */
    /* JADX WARNING: Removed duplicated region for block: B:262:0x041e A:{Catch:{ Exception -> 0x0a50, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:268:0x043d A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:278:0x0462 A:{SYNTHETIC, Splitter:B:278:0x0462} */
    /* JADX WARNING: Removed duplicated region for block: B:363:0x05c0  */
    /* JADX WARNING: Removed duplicated region for block: B:366:0x05d6 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:371:0x05f3 A:{Catch:{ Exception -> 0x0a3a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:372:0x05f6 A:{Catch:{ Exception -> 0x0a3a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:376:0x05ff  */
    /* JADX WARNING: Removed duplicated region for block: B:378:0x0613  */
    /* JADX WARNING: Removed duplicated region for block: B:458:0x0753 A:{Catch:{ Exception -> 0x09a2, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:457:0x0746 A:{Catch:{ Exception -> 0x09a2, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:621:0x0b59 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:623:0x0b5e A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:625:0x0b63 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:627:0x0b6b A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:629:0x0b73 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x01be A:{SYNTHETIC, Splitter:B:95:0x01be} */
    /* JADX WARNING: Removed duplicated region for block: B:98:0x01c7 A:{SYNTHETIC, Splitter:B:98:0x01c7} */
    /* JADX WARNING: Removed duplicated region for block: B:104:0x01d3  */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x01d7  */
    /* JADX WARNING: Removed duplicated region for block: B:110:0x01f7 A:{SYNTHETIC, Splitter:B:110:0x01f7} */
    /* JADX WARNING: Removed duplicated region for block: B:149:0x0290  */
    /* JADX WARNING: Removed duplicated region for block: B:164:0x02ae A:{SYNTHETIC, Splitter:B:164:0x02ae} */
    /* JADX WARNING: Removed duplicated region for block: B:177:0x02cd A:{SYNTHETIC, Splitter:B:177:0x02cd} */
    /* JADX WARNING: Removed duplicated region for block: B:186:0x02ef  */
    /* JADX WARNING: Removed duplicated region for block: B:193:0x0303 A:{SYNTHETIC, Splitter:B:193:0x0303} */
    /* JADX WARNING: Removed duplicated region for block: B:197:0x0311 A:{SYNTHETIC, Splitter:B:197:0x0311} */
    /* JADX WARNING: Removed duplicated region for block: B:206:0x032e A:{SYNTHETIC, Splitter:B:206:0x032e} */
    /* JADX WARNING: Removed duplicated region for block: B:215:0x0350  */
    /* JADX WARNING: Removed duplicated region for block: B:258:0x03f6  */
    /* JADX WARNING: Removed duplicated region for block: B:218:0x0355 A:{SYNTHETIC, Splitter:B:218:0x0355} */
    /* JADX WARNING: Removed duplicated region for block: B:262:0x041e A:{Catch:{ Exception -> 0x0a50, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:268:0x043d A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:363:0x05c0  */
    /* JADX WARNING: Removed duplicated region for block: B:278:0x0462 A:{SYNTHETIC, Splitter:B:278:0x0462} */
    /* JADX WARNING: Removed duplicated region for block: B:366:0x05d6 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:372:0x05f6 A:{Catch:{ Exception -> 0x0a3a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:371:0x05f3 A:{Catch:{ Exception -> 0x0a3a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:378:0x0613  */
    /* JADX WARNING: Removed duplicated region for block: B:376:0x05ff  */
    /* JADX WARNING: Removed duplicated region for block: B:457:0x0746 A:{Catch:{ Exception -> 0x09a2, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:458:0x0753 A:{Catch:{ Exception -> 0x09a2, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:621:0x0b59 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:623:0x0b5e A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:625:0x0b63 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:627:0x0b6b A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:629:0x0b73 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:98:0x01c7 A:{SYNTHETIC, Splitter:B:98:0x01c7} */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x01be A:{SYNTHETIC, Splitter:B:95:0x01be} */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x01d7  */
    /* JADX WARNING: Removed duplicated region for block: B:104:0x01d3  */
    /* JADX WARNING: Removed duplicated region for block: B:149:0x0290  */
    /* JADX WARNING: Removed duplicated region for block: B:110:0x01f7 A:{SYNTHETIC, Splitter:B:110:0x01f7} */
    /* JADX WARNING: Removed duplicated region for block: B:164:0x02ae A:{SYNTHETIC, Splitter:B:164:0x02ae} */
    /* JADX WARNING: Removed duplicated region for block: B:186:0x02ef  */
    /* JADX WARNING: Removed duplicated region for block: B:177:0x02cd A:{SYNTHETIC, Splitter:B:177:0x02cd} */
    /* JADX WARNING: Removed duplicated region for block: B:197:0x0311 A:{SYNTHETIC, Splitter:B:197:0x0311} */
    /* JADX WARNING: Removed duplicated region for block: B:193:0x0303 A:{SYNTHETIC, Splitter:B:193:0x0303} */
    /* JADX WARNING: Removed duplicated region for block: B:215:0x0350  */
    /* JADX WARNING: Removed duplicated region for block: B:206:0x032e A:{SYNTHETIC, Splitter:B:206:0x032e} */
    /* JADX WARNING: Removed duplicated region for block: B:218:0x0355 A:{SYNTHETIC, Splitter:B:218:0x0355} */
    /* JADX WARNING: Removed duplicated region for block: B:258:0x03f6  */
    /* JADX WARNING: Removed duplicated region for block: B:262:0x041e A:{Catch:{ Exception -> 0x0a50, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:268:0x043d A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:278:0x0462 A:{SYNTHETIC, Splitter:B:278:0x0462} */
    /* JADX WARNING: Removed duplicated region for block: B:363:0x05c0  */
    /* JADX WARNING: Removed duplicated region for block: B:366:0x05d6 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:371:0x05f3 A:{Catch:{ Exception -> 0x0a3a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:372:0x05f6 A:{Catch:{ Exception -> 0x0a3a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:376:0x05ff  */
    /* JADX WARNING: Removed duplicated region for block: B:378:0x0613  */
    /* JADX WARNING: Removed duplicated region for block: B:458:0x0753 A:{Catch:{ Exception -> 0x09a2, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:457:0x0746 A:{Catch:{ Exception -> 0x09a2, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:621:0x0b59 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:623:0x0b5e A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:625:0x0b63 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:627:0x0b6b A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:629:0x0b73 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:621:0x0b59 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:623:0x0b5e A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:625:0x0b63 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:627:0x0b6b A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:629:0x0b73 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0bca  */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0bd1 A:{SYNTHETIC, Splitter:B:657:0x0bd1} */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0c5d A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:665:0x0be4 A:{Splitter:B:61:0x0138, ExcHandler: all (r0_58 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:621:0x0b59 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:623:0x0b5e A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:625:0x0b63 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:627:0x0b6b A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:629:0x0b73 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0bca  */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0bd1 A:{SYNTHETIC, Splitter:B:657:0x0bd1} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0c5d A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:665:0x0be4 A:{Splitter:B:61:0x0138, ExcHandler: all (r0_58 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:611:0x0b19 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:621:0x0b59 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:623:0x0b5e A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:625:0x0b63 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:627:0x0b6b A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:629:0x0b73 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0bca  */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0bd1 A:{SYNTHETIC, Splitter:B:657:0x0bd1} */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0c5d A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:665:0x0be4 A:{Splitter:B:61:0x0138, ExcHandler: all (r0_58 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:611:0x0b19 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:621:0x0b59 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:623:0x0b5e A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:625:0x0b63 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:627:0x0b6b A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:629:0x0b73 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0bca  */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0bd1 A:{SYNTHETIC, Splitter:B:657:0x0bd1} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0c5d A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:665:0x0be4 A:{Splitter:B:61:0x0138, ExcHandler: all (r0_58 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0c5d A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:665:0x0be4 A:{Splitter:B:61:0x0138, ExcHandler: all (r0_58 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:611:0x0b19 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:621:0x0b59 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:623:0x0b5e A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:625:0x0b63 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:627:0x0b6b A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:629:0x0b73 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0bca  */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0bd1 A:{SYNTHETIC, Splitter:B:657:0x0bd1} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0c5d A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:665:0x0be4 A:{Splitter:B:61:0x0138, ExcHandler: all (r0_58 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:665:0x0be4 A:{Splitter:B:61:0x0138, ExcHandler: all (r0_58 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:611:0x0b19 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:621:0x0b59 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:623:0x0b5e A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:625:0x0b63 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:627:0x0b6b A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:629:0x0b73 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0bca  */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0bd1 A:{SYNTHETIC, Splitter:B:657:0x0bd1} */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0c5d A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:665:0x0be4 A:{Splitter:B:61:0x0138, ExcHandler: all (r0_58 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:676:0x0c2b  */
    /* JADX WARNING: Removed duplicated region for block: B:679:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:679:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0c5d A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:665:0x0be4 A:{Splitter:B:61:0x0138, ExcHandler: all (r0_58 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:665:0x0be4 A:{Splitter:B:61:0x0138, ExcHandler: all (r0_58 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:676:0x0c2b  */
    /* JADX WARNING: Removed duplicated region for block: B:679:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:679:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0c5d A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:665:0x0be4 A:{Splitter:B:61:0x0138, ExcHandler: all (r0_58 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:665:0x0be4 A:{Splitter:B:61:0x0138, ExcHandler: all (r0_58 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:611:0x0b19 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:621:0x0b59 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:623:0x0b5e A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:625:0x0b63 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:627:0x0b6b A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:629:0x0b73 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0bca  */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0bd1 A:{SYNTHETIC, Splitter:B:657:0x0bd1} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0c5d A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:665:0x0be4 A:{Splitter:B:61:0x0138, ExcHandler: all (r0_58 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:676:0x0c2b  */
    /* JADX WARNING: Removed duplicated region for block: B:679:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:679:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0c5d A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:665:0x0be4 A:{Splitter:B:61:0x0138, ExcHandler: all (r0_58 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:665:0x0be4 A:{Splitter:B:61:0x0138, ExcHandler: all (r0_58 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:611:0x0b19 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:621:0x0b59 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:623:0x0b5e A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:625:0x0b63 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:627:0x0b6b A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:629:0x0b73 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0bca  */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0bd1 A:{SYNTHETIC, Splitter:B:657:0x0bd1} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0c5d A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:665:0x0be4 A:{Splitter:B:61:0x0138, ExcHandler: all (r0_58 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:676:0x0c2b  */
    /* JADX WARNING: Removed duplicated region for block: B:679:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:679:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0c5d A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:665:0x0be4 A:{Splitter:B:61:0x0138, ExcHandler: all (r0_58 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:611:0x0b19 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:621:0x0b59 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:623:0x0b5e A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:625:0x0b63 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:627:0x0b6b A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:629:0x0b73 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0bca  */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0bd1 A:{SYNTHETIC, Splitter:B:657:0x0bd1} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0c5d A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:665:0x0be4 A:{Splitter:B:61:0x0138, ExcHandler: all (r0_58 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:611:0x0b19 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:621:0x0b59 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:623:0x0b5e A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:625:0x0b63 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:627:0x0b6b A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:629:0x0b73 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0bca  */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0bd1 A:{SYNTHETIC, Splitter:B:657:0x0bd1} */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0c5d A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:665:0x0be4 A:{Splitter:B:61:0x0138, ExcHandler: all (r0_58 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:611:0x0b19 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:621:0x0b59 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:623:0x0b5e A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:625:0x0b63 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:627:0x0b6b A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:629:0x0b73 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0bca  */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0bd1 A:{SYNTHETIC, Splitter:B:657:0x0bd1} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0c5d A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:665:0x0be4 A:{Splitter:B:61:0x0138, ExcHandler: all (r0_58 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:611:0x0b19 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:621:0x0b59 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:623:0x0b5e A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:625:0x0b63 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:627:0x0b6b A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:629:0x0b73 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0bca  */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0bd1 A:{SYNTHETIC, Splitter:B:657:0x0bd1} */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0c5d A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:665:0x0be4 A:{Splitter:B:61:0x0138, ExcHandler: all (r0_58 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:611:0x0b19 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:621:0x0b59 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:623:0x0b5e A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:625:0x0b63 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:627:0x0b6b A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:629:0x0b73 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0bca  */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0bd1 A:{SYNTHETIC, Splitter:B:657:0x0bd1} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0c5d A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:665:0x0be4 A:{Splitter:B:61:0x0138, ExcHandler: all (r0_58 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:665:0x0be4 A:{Splitter:B:61:0x0138, ExcHandler: all (r0_58 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:676:0x0c2b  */
    /* JADX WARNING: Removed duplicated region for block: B:679:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:679:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0c5d A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:665:0x0be4 A:{Splitter:B:61:0x0138, ExcHandler: all (r0_58 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:611:0x0b19 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:621:0x0b59 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:623:0x0b5e A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:625:0x0b63 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:627:0x0b6b A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:629:0x0b73 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0bca  */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0bd1 A:{SYNTHETIC, Splitter:B:657:0x0bd1} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0c5d A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:665:0x0be4 A:{Splitter:B:61:0x0138, ExcHandler: all (r0_58 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:692:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:695:0x0c6d A:{SYNTHETIC, Splitter:B:695:0x0c6d} */
    /* JADX WARNING: Removed duplicated region for block: B:665:0x0be4 A:{Splitter:B:61:0x0138, ExcHandler: all (r0_58 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:611:0x0b19 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:621:0x0b59 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:623:0x0b5e A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:625:0x0b63 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:627:0x0b6b A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:629:0x0b73 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0bca  */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0bd1 A:{SYNTHETIC, Splitter:B:657:0x0bd1} */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0c5d A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:665:0x0be4 A:{Splitter:B:61:0x0138, ExcHandler: all (r0_58 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:676:0x0c2b  */
    /* JADX WARNING: Removed duplicated region for block: B:679:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:679:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0c5d A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:665:0x0be4 A:{Splitter:B:61:0x0138, ExcHandler: all (r0_58 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:611:0x0b19 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:621:0x0b59 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:623:0x0b5e A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:625:0x0b63 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:627:0x0b6b A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:629:0x0b73 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0bca  */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0bd1 A:{SYNTHETIC, Splitter:B:657:0x0bd1} */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0c5d A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:665:0x0be4 A:{Splitter:B:61:0x0138, ExcHandler: all (r0_58 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:611:0x0b19 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:621:0x0b59 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:623:0x0b5e A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:625:0x0b63 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:627:0x0b6b A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:629:0x0b73 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0bca  */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0bd1 A:{SYNTHETIC, Splitter:B:657:0x0bd1} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0c5d A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:665:0x0be4 A:{Splitter:B:61:0x0138, ExcHandler: all (r0_58 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:611:0x0b19 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:621:0x0b59 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:623:0x0b5e A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:625:0x0b63 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:627:0x0b6b A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:629:0x0b73 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0bca  */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0bd1 A:{SYNTHETIC, Splitter:B:657:0x0bd1} */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0c5d A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:665:0x0be4 A:{Splitter:B:61:0x0138, ExcHandler: all (r0_58 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:611:0x0b19 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:621:0x0b59 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:623:0x0b5e A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:625:0x0b63 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:627:0x0b6b A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:629:0x0b73 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0bca  */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0bd1 A:{SYNTHETIC, Splitter:B:657:0x0bd1} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0c5d A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:665:0x0be4 A:{Splitter:B:61:0x0138, ExcHandler: all (r0_58 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:665:0x0be4 A:{Splitter:B:61:0x0138, ExcHandler: all (r0_58 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:611:0x0b19 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:621:0x0b59 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:623:0x0b5e A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:625:0x0b63 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:627:0x0b6b A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:629:0x0b73 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0bca  */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0bd1 A:{SYNTHETIC, Splitter:B:657:0x0bd1} */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0c5d A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:665:0x0be4 A:{Splitter:B:61:0x0138, ExcHandler: all (r0_58 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:611:0x0b19 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:621:0x0b59 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:623:0x0b5e A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:625:0x0b63 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:627:0x0b6b A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:629:0x0b73 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0bca  */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0bd1 A:{SYNTHETIC, Splitter:B:657:0x0bd1} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0c5d A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:665:0x0be4 A:{Splitter:B:61:0x0138, ExcHandler: all (r0_58 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:665:0x0be4 A:{Splitter:B:61:0x0138, ExcHandler: all (r0_58 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:611:0x0b19 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:621:0x0b59 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:623:0x0b5e A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:625:0x0b63 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:627:0x0b6b A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:629:0x0b73 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0bca  */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0bd1 A:{SYNTHETIC, Splitter:B:657:0x0bd1} */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0c5d A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:665:0x0be4 A:{Splitter:B:61:0x0138, ExcHandler: all (r0_58 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:676:0x0c2b  */
    /* JADX WARNING: Removed duplicated region for block: B:679:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:679:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0c5d A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:665:0x0be4 A:{Splitter:B:61:0x0138, ExcHandler: all (r0_58 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:611:0x0b19 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:621:0x0b59 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:623:0x0b5e A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:625:0x0b63 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:627:0x0b6b A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:629:0x0b73 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0bca  */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0bd1 A:{SYNTHETIC, Splitter:B:657:0x0bd1} */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0c5d A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:665:0x0be4 A:{Splitter:B:61:0x0138, ExcHandler: all (r0_58 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:665:0x0be4 A:{Splitter:B:61:0x0138, ExcHandler: all (r0_58 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:665:0x0be4 A:{Splitter:B:61:0x0138, ExcHandler: all (r0_58 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:665:0x0be4 A:{Splitter:B:61:0x0138, ExcHandler: all (r0_58 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:611:0x0b19 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:621:0x0b59 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:623:0x0b5e A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:625:0x0b63 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:627:0x0b6b A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:629:0x0b73 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0bca  */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0bd1 A:{SYNTHETIC, Splitter:B:657:0x0bd1} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0c5d A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:665:0x0be4 A:{Splitter:B:61:0x0138, ExcHandler: all (r0_58 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:665:0x0be4 A:{Splitter:B:61:0x0138, ExcHandler: all (r0_58 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:611:0x0b19 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:621:0x0b59 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:623:0x0b5e A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:625:0x0b63 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:627:0x0b6b A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:629:0x0b73 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0bca  */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0bd1 A:{SYNTHETIC, Splitter:B:657:0x0bd1} */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0c5d A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:665:0x0be4 A:{Splitter:B:61:0x0138, ExcHandler: all (r0_58 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:611:0x0b19 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:621:0x0b59 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:623:0x0b5e A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:625:0x0b63 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:627:0x0b6b A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:629:0x0b73 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0bca  */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0bd1 A:{SYNTHETIC, Splitter:B:657:0x0bd1} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0c5d A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:665:0x0be4 A:{Splitter:B:61:0x0138, ExcHandler: all (r0_58 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:611:0x0b19 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:621:0x0b59 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:623:0x0b5e A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:625:0x0b63 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:627:0x0b6b A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:629:0x0b73 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0bca  */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0bd1 A:{SYNTHETIC, Splitter:B:657:0x0bd1} */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0c5d A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:611:0x0b19 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:621:0x0b59 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:623:0x0b5e A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:625:0x0b63 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:627:0x0b6b A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:629:0x0b73 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0bca  */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0bd1 A:{SYNTHETIC, Splitter:B:657:0x0bd1} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0c5d A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:611:0x0b19 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:621:0x0b59 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:623:0x0b5e A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:625:0x0b63 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:627:0x0b6b A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:629:0x0b73 A:{Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0bca  */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0bd1 A:{SYNTHETIC, Splitter:B:657:0x0bd1} */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0c5d A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0CLASSNAME  */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:61:0x0138, B:642:0x0ba6] */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:61:0x0138, B:649:0x0bbd] */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing block: B:135:0x0261, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:136:0x0262, code skipped:
            r1 = r0;
            r10 = r4;
     */
    /* JADX WARNING: Missing block: B:143:0x0280, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:144:0x0281, code skipped:
            r1 = r0;
            r10 = r2;
            r9 = r4;
     */
    /* JADX WARNING: Missing block: B:146:0x0288, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:147:0x0289, code skipped:
            r1 = r0;
            r10 = r2;
     */
    /* JADX WARNING: Missing block: B:166:0x02bb, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:167:0x02bc, code skipped:
            r1 = r0;
     */
    /* JADX WARNING: Missing block: B:182:0x02db, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:183:0x02dc, code skipped:
            r1 = r0;
            r5 = r2;
            r13 = r12;
            r11 = r28;
            r2 = null;
            r4 = null;
     */
    /* JADX WARNING: Missing block: B:184:0x02e5, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:185:0x02e6, code skipped:
            r1 = r0;
            r13 = r12;
            r11 = r28;
            r2 = null;
            r4 = null;
            r5 = null;
     */
    /* JADX WARNING: Missing block: B:195:0x0309, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:196:0x030a, code skipped:
            r1 = r0;
            r13 = r12;
            r11 = r28;
            r2 = null;
     */
    /* JADX WARNING: Missing block: B:212:0x0345, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:213:0x0346, code skipped:
            r9 = r67;
            r10 = r68;
            r1 = r0;
            r13 = r12;
     */
    /* JADX WARNING: Missing block: B:214:0x034c, code skipped:
            r11 = r28;
     */
    /* JADX WARNING: Missing block: B:227:0x0393, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:228:0x0394, code skipped:
            r9 = r67;
            r10 = r68;
            r1 = r0;
            r13 = r12;
     */
    /* JADX WARNING: Missing block: B:249:0x03cd, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:250:0x03ce, code skipped:
            r11 = r71;
     */
    /* JADX WARNING: Missing block: B:251:0x03d2, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:252:0x03d4, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:253:0x03d5, code skipped:
            r13 = r61;
     */
    /* JADX WARNING: Missing block: B:254:0x03d7, code skipped:
            r11 = r71;
            r13 = r65;
            r9 = r67;
            r10 = r68;
            r1 = r0;
     */
    /* JADX WARNING: Missing block: B:255:0x03e0, code skipped:
            r11 = r28;
            r5 = r44;
     */
    /* JADX WARNING: Missing block: B:256:0x03e6, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:257:0x03e7, code skipped:
            r13 = r61;
            r11 = r71;
            r44 = r5;
            r13 = r65;
            r9 = r67;
            r10 = r68;
            r1 = r0;
     */
    /* JADX WARNING: Missing block: B:272:0x0449, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:298:0x04c6, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:299:0x04c7, code skipped:
            r13 = r65;
            r9 = r67;
            r1 = r0;
            r11 = r8;
            r55 = r10;
     */
    /* JADX WARNING: Missing block: B:326:0x0517, code skipped:
            if (r8.presentationTimeUs < r11) goto L_0x0519;
     */
    /* JADX WARNING: Missing block: B:357:0x05a8, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:358:0x05aa, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:359:0x05ab, code skipped:
            r56 = r2;
            r55 = r10;
     */
    /* JADX WARNING: Missing block: B:447:0x071d, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:448:0x071e, code skipped:
            r13 = r65;
            r5 = r66;
     */
    /* JADX WARNING: Missing block: B:472:0x07ae, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:519:0x083d, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:522:?, code skipped:
            org.telegram.messenger.FileLog.e(r0);
            r6 = 1;
     */
    /* JADX WARNING: Missing block: B:534:0x0882, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:535:0x0884, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:536:0x0885, code skipped:
            r7 = r44;
     */
    /* JADX WARNING: Missing block: B:537:0x0887, code skipped:
            r9 = r67;
            r10 = r68;
            r1 = r0;
            r2 = r6;
            r5 = r7;
            r11 = r28;
     */
    /* JADX WARNING: Missing block: B:549:0x091f, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:550:0x0920, code skipped:
            r12 = r6;
            r27 = r7;
            r11 = r28;
     */
    /* JADX WARNING: Missing block: B:567:0x09a2, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:575:0x0a2d, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:576:0x0a2e, code skipped:
            r9 = r67;
            r10 = r68;
            r1 = r0;
            r2 = r12;
            r5 = r27;
            r4 = r28;
     */
    /* JADX WARNING: Missing block: B:577:0x0a3a, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:578:0x0a3b, code skipped:
            r13 = r65;
     */
    /* JADX WARNING: Missing block: B:579:0x0a3d, code skipped:
            r11 = r28;
            r27 = r44;
            r12 = r56;
     */
    /* JADX WARNING: Missing block: B:580:0x0a43, code skipped:
            r28 = r4;
            r9 = r67;
            r10 = r68;
            r1 = r0;
            r2 = r12;
     */
    /* JADX WARNING: Missing block: B:581:0x0a50, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:582:0x0a51, code skipped:
            r13 = r65;
            r12 = r2;
            r55 = r10;
            r11 = r28;
            r27 = r44;
            r28 = r4;
            r9 = r67;
            r10 = r68;
            r1 = r0;
     */
    /* JADX WARNING: Missing block: B:583:0x0a65, code skipped:
            r5 = r27;
     */
    /* JADX WARNING: Missing block: B:584:0x0a69, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:585:0x0a6a, code skipped:
            r27 = r5;
            r44 = null;
            r13 = r12;
            r11 = r28;
            r12 = r2;
            r28 = r4;
            r9 = r67;
            r10 = r68;
     */
    /* JADX WARNING: Missing block: B:586:0x0a7d, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:587:0x0a7e, code skipped:
            r27 = r5;
            r67 = r9;
            r68 = r10;
            r13 = r12;
            r11 = r28;
            r44 = null;
            r12 = r2;
            r28 = r4;
     */
    /* JADX WARNING: Missing block: B:588:0x0a90, code skipped:
            r1 = r0;
            r55 = r44;
     */
    /* JADX WARNING: Missing block: B:589:0x0a95, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:590:0x0a96, code skipped:
            r27 = r5;
            r67 = r9;
            r68 = r10;
            r13 = r12;
            r11 = r28;
            r28 = r4;
            r1 = r0;
            r2 = null;
            r55 = r2;
     */
    /* JADX WARNING: Missing block: B:591:0x0aae, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:592:0x0aaf, code skipped:
            r27 = r5;
            r67 = r9;
            r68 = r10;
            r13 = r12;
            r11 = r28;
            r1 = r0;
            r2 = null;
            r4 = r2;
            r55 = r4;
     */
    /* JADX WARNING: Missing block: B:593:0x0ac6, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:594:0x0ac7, code skipped:
            r67 = r9;
            r68 = r10;
            r13 = r12;
            r11 = r28;
            r1 = r0;
            r2 = null;
     */
    /* JADX WARNING: Missing block: B:595:0x0ad8, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:596:0x0ad9, code skipped:
            r44 = null;
            r67 = r9;
            r68 = r10;
            r13 = r12;
            r11 = r28;
            r1 = r0;
     */
    /* JADX WARNING: Missing block: B:597:0x0ae7, code skipped:
            r4 = r2;
            r5 = r4;
            r55 = r5;
     */
    /* JADX WARNING: Missing block: B:598:0x0aec, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:599:0x0aed, code skipped:
            r67 = r9;
            r68 = r10;
     */
    /* JADX WARNING: Missing block: B:600:0x0af2, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:601:0x0af3, code skipped:
            r13 = r12;
            r11 = r28;
            r44 = null;
            r1 = r0;
            r10 = r2;
     */
    /* JADX WARNING: Missing block: B:602:0x0aff, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:603:0x0b00, code skipped:
            r13 = r12;
            r11 = r28;
     */
    /* JADX WARNING: Missing block: B:604:0x0b04, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:605:0x0b05, code skipped:
            r11 = r4;
            r13 = r12;
     */
    /* JADX WARNING: Missing block: B:606:0x0b07, code skipped:
            r44 = null;
            r1 = r0;
     */
    /* JADX WARNING: Missing block: B:607:0x0b0e, code skipped:
            r2 = r44;
            r4 = r2;
            r5 = r4;
            r15 = r5;
            r55 = r15;
     */
    /* JADX WARNING: Missing block: B:612:0x0b1b, code skipped:
            r3 = 1;
     */
    /* JADX WARNING: Missing block: B:631:0x0b7a, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:632:0x0b7b, code skipped:
            r1 = r0;
            r13 = r12;
     */
    /* JADX WARNING: Missing block: B:633:0x0b7f, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:634:0x0b80, code skipped:
            r13 = r66;
            r1 = r0;
     */
    /* JADX WARNING: Missing block: B:635:0x0b85, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:636:0x0b86, code skipped:
            r13 = r66;
     */
    /* JADX WARNING: Missing block: B:662:0x0bdf, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:663:0x0be1, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:664:0x0be2, code skipped:
            r13 = r12;
     */
    /* JADX WARNING: Missing block: B:665:0x0be4, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:666:0x0be5, code skipped:
            r2 = r0;
            r1 = r14;
     */
    /* JADX WARNING: Missing block: B:692:0x0CLASSNAME, code skipped:
            r3.release();
     */
    /* JADX WARNING: Missing block: B:696:?, code skipped:
            r3.finishMovie();
     */
    /* JADX WARNING: Missing block: B:697:0x0CLASSNAME, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:698:0x0CLASSNAME, code skipped:
            org.telegram.messenger.FileLog.e(r0);
     */
    private boolean convertVideoInternal(java.lang.String r61, java.io.File r62, int r63, boolean r64, int r65, int r66, int r67, int r68, long r69, long r71, long r73, boolean r75, boolean r76) {
        /*
        r60 = this;
        r14 = r60;
        r13 = r61;
        r15 = r63;
        r12 = r65;
        r11 = r66;
        r9 = r67;
        r10 = r68;
        r7 = r69;
        r5 = r71;
        r2 = new android.media.MediaCodec$BufferInfo;	 Catch:{ Exception -> 0x0be9, all -> 0x0be4 }
        r2.<init>();	 Catch:{ Exception -> 0x0be9, all -> 0x0be4 }
        r1 = new org.telegram.messenger.video.Mp4Movie;	 Catch:{ Exception -> 0x0be9, all -> 0x0be4 }
        r1.<init>();	 Catch:{ Exception -> 0x0be9, all -> 0x0be4 }
        r4 = r62;
        r1.setCacheFile(r4);	 Catch:{ Exception -> 0x0be9, all -> 0x0be4 }
        r1.setRotation(r15);	 Catch:{ Exception -> 0x0be9, all -> 0x0be4 }
        r1.setSize(r12, r11);	 Catch:{ Exception -> 0x0be9, all -> 0x0be4 }
        r3 = new org.telegram.messenger.video.MP4Builder;	 Catch:{ Exception -> 0x0be9, all -> 0x0be4 }
        r3.<init>();	 Catch:{ Exception -> 0x0be9, all -> 0x0be4 }
        r4 = r64;
        r1 = r3.createMovie(r1, r4);	 Catch:{ Exception -> 0x0be9, all -> 0x0be4 }
        r14.mediaMuxer = r1;	 Catch:{ Exception -> 0x0be9, all -> 0x0be4 }
        r1 = new android.media.MediaExtractor;	 Catch:{ Exception -> 0x0be9, all -> 0x0be4 }
        r1.<init>();	 Catch:{ Exception -> 0x0be9, all -> 0x0be4 }
        r14.extractor = r1;	 Catch:{ Exception -> 0x0be9, all -> 0x0be4 }
        r1 = r14.extractor;	 Catch:{ Exception -> 0x0be9, all -> 0x0be4 }
        r1.setDataSource(r13);	 Catch:{ Exception -> 0x0be9, all -> 0x0be4 }
        r3 = r73;
        r1 = (float) r3;	 Catch:{ Exception -> 0x0be9, all -> 0x0be4 }
        r18 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r18 = r1 / r18;
        r60.checkConversionCanceled();	 Catch:{ Exception -> 0x0be9, all -> 0x0be4 }
        if (r75 == 0) goto L_0x0b9f;
    L_0x004c:
        r1 = r14.extractor;	 Catch:{ Exception -> 0x0b95, all -> 0x0be4 }
        r3 = 0;
        r4 = org.telegram.messenger.MediaController.findTrack(r1, r3);	 Catch:{ Exception -> 0x0b95, all -> 0x0be4 }
        r1 = -1;
        if (r10 == r1) goto L_0x0067;
    L_0x0056:
        r1 = r14.extractor;	 Catch:{ Exception -> 0x005f, all -> 0x0be4 }
        r3 = 1;
        r1 = org.telegram.messenger.MediaController.findTrack(r1, r3);	 Catch:{ Exception -> 0x005f, all -> 0x0be4 }
        r3 = r1;
        goto L_0x0068;
    L_0x005f:
        r0 = move-exception;
        r1 = r0;
        r13 = r11;
        r3 = 0;
        r59 = 1;
        goto L_0x0bf5;
    L_0x0067:
        r3 = -1;
    L_0x0068:
        if (r4 < 0) goto L_0x0b8a;
    L_0x006a:
        r20 = -1;
        r22 = android.os.Build.MANUFACTURER;	 Catch:{ Exception -> 0x0b04, all -> 0x0be4 }
        r1 = r22.toLowerCase();	 Catch:{ Exception -> 0x0b04, all -> 0x0be4 }
        r22 = r2;
        r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0b04, all -> 0x0be4 }
        r25 = 4;
        r6 = "video/avc";
        r5 = 18;
        if (r2 >= r5) goto L_0x012f;
    L_0x007f:
        r2 = org.telegram.messenger.MediaController.selectCodec(r6);	 Catch:{ Exception -> 0x011f, all -> 0x0be4 }
        r29 = org.telegram.messenger.MediaController.selectColorFormat(r2, r6);	 Catch:{ Exception -> 0x011f, all -> 0x0be4 }
        if (r29 == 0) goto L_0x0117;
    L_0x0089:
        r5 = r2.getName();	 Catch:{ Exception -> 0x011f, all -> 0x0be4 }
        r13 = "OMX.qcom.";
        r13 = r5.contains(r13);	 Catch:{ Exception -> 0x011f, all -> 0x0be4 }
        if (r13 == 0) goto L_0x00b1;
    L_0x0095:
        r5 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x011f, all -> 0x0be4 }
        r13 = 16;
        if (r5 != r13) goto L_0x00ae;
    L_0x009b:
        r5 = "lge";
        r5 = r1.equals(r5);	 Catch:{ Exception -> 0x011f, all -> 0x0be4 }
        if (r5 != 0) goto L_0x00ab;
    L_0x00a3:
        r5 = "nokia";
        r5 = r1.equals(r5);	 Catch:{ Exception -> 0x011f, all -> 0x0be4 }
        if (r5 == 0) goto L_0x00ae;
    L_0x00ab:
        r5 = 1;
    L_0x00ac:
        r13 = 1;
        goto L_0x00db;
    L_0x00ae:
        r5 = 1;
    L_0x00af:
        r13 = 0;
        goto L_0x00db;
    L_0x00b1:
        r13 = "OMX.Intel.";
        r13 = r5.contains(r13);	 Catch:{ Exception -> 0x011f, all -> 0x0be4 }
        if (r13 == 0) goto L_0x00bb;
    L_0x00b9:
        r5 = 2;
        goto L_0x00af;
    L_0x00bb:
        r13 = "OMX.MTK.VIDEO.ENCODER.AVC";
        r13 = r5.equals(r13);	 Catch:{ Exception -> 0x011f, all -> 0x0be4 }
        if (r13 == 0) goto L_0x00c5;
    L_0x00c3:
        r5 = 3;
        goto L_0x00af;
    L_0x00c5:
        r13 = "OMX.SEC.AVC.Encoder";
        r13 = r5.equals(r13);	 Catch:{ Exception -> 0x011f, all -> 0x0be4 }
        if (r13 == 0) goto L_0x00cf;
    L_0x00cd:
        r5 = 4;
        goto L_0x00ac;
    L_0x00cf:
        r13 = "OMX.TI.DUCATI1.VIDEO.H264E";
        r5 = r5.equals(r13);	 Catch:{ Exception -> 0x011f, all -> 0x0be4 }
        if (r5 == 0) goto L_0x00d9;
    L_0x00d7:
        r5 = 5;
        goto L_0x00af;
    L_0x00d9:
        r5 = 0;
        goto L_0x00af;
    L_0x00db:
        r31 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x011f, all -> 0x0be4 }
        if (r31 == 0) goto L_0x010e;
    L_0x00df:
        r31 = r5;
        r5 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x011f, all -> 0x0be4 }
        r5.<init>();	 Catch:{ Exception -> 0x011f, all -> 0x0be4 }
        r32 = r13;
        r13 = "codec = ";
        r5.append(r13);	 Catch:{ Exception -> 0x011f, all -> 0x0be4 }
        r2 = r2.getName();	 Catch:{ Exception -> 0x011f, all -> 0x0be4 }
        r5.append(r2);	 Catch:{ Exception -> 0x011f, all -> 0x0be4 }
        r2 = " manufacturer = ";
        r5.append(r2);	 Catch:{ Exception -> 0x011f, all -> 0x0be4 }
        r5.append(r1);	 Catch:{ Exception -> 0x011f, all -> 0x0be4 }
        r2 = "device = ";
        r5.append(r2);	 Catch:{ Exception -> 0x011f, all -> 0x0be4 }
        r2 = android.os.Build.MODEL;	 Catch:{ Exception -> 0x011f, all -> 0x0be4 }
        r5.append(r2);	 Catch:{ Exception -> 0x011f, all -> 0x0be4 }
        r2 = r5.toString();	 Catch:{ Exception -> 0x011f, all -> 0x0be4 }
        org.telegram.messenger.FileLog.d(r2);	 Catch:{ Exception -> 0x011f, all -> 0x0be4 }
        goto L_0x0112;
    L_0x010e:
        r31 = r5;
        r32 = r13;
    L_0x0112:
        r13 = r29;
        r2 = r31;
        goto L_0x0138;
    L_0x0117:
        r1 = new java.lang.RuntimeException;	 Catch:{ Exception -> 0x011f, all -> 0x0be4 }
        r2 = "no supported color format";
        r1.<init>(r2);	 Catch:{ Exception -> 0x011f, all -> 0x0be4 }
        throw r1;	 Catch:{ Exception -> 0x011f, all -> 0x0be4 }
    L_0x011f:
        r0 = move-exception;
        r1 = r0;
        r11 = r4;
        r13 = r12;
    L_0x0123:
        r2 = 0;
        r4 = 0;
        r5 = 0;
        r15 = 0;
    L_0x0127:
        r55 = 0;
    L_0x0129:
        r58 = 0;
        r59 = 1;
        goto L_0x0b15;
    L_0x012f:
        r2 = NUM; // 0x7var_ float:1.701803E38 double:1.0527098025E-314;
        r2 = 0;
        r13 = NUM; // 0x7var_ float:1.701803E38 double:1.0527098025E-314;
        r32 = 0;
    L_0x0138:
        r5 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x0b04, all -> 0x0be4 }
        if (r5 == 0) goto L_0x0153;
    L_0x013c:
        r5 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x011f, all -> 0x0be4 }
        r5.<init>();	 Catch:{ Exception -> 0x011f, all -> 0x0be4 }
        r29 = r3;
        r3 = "colorFormat = ";
        r5.append(r3);	 Catch:{ Exception -> 0x011f, all -> 0x0be4 }
        r5.append(r13);	 Catch:{ Exception -> 0x011f, all -> 0x0be4 }
        r3 = r5.toString();	 Catch:{ Exception -> 0x011f, all -> 0x0be4 }
        org.telegram.messenger.FileLog.d(r3);	 Catch:{ Exception -> 0x011f, all -> 0x0be4 }
        goto L_0x0155;
    L_0x0153:
        r29 = r3;
    L_0x0155:
        r3 = r12 * r11;
        r5 = r3 * 3;
        r27 = 2;
        r5 = r5 / 2;
        if (r2 != 0) goto L_0x0176;
    L_0x015f:
        r1 = r11 % 16;
        if (r1 == 0) goto L_0x01aa;
    L_0x0163:
        r1 = r11 % 16;
        r2 = 16;
        r1 = 16 - r1;
        r1 = r1 + r11;
        r1 = r1 - r11;
        r1 = r1 * r12;
        r2 = r1 * 5;
        r2 = r2 / 4;
    L_0x0171:
        r5 = r5 + r2;
    L_0x0172:
        r15 = r1;
        r24 = r5;
        goto L_0x01ad;
    L_0x0176:
        r15 = 1;
        if (r2 != r15) goto L_0x018c;
    L_0x0179:
        r1 = r1.toLowerCase();	 Catch:{ Exception -> 0x011f, all -> 0x0be4 }
        r2 = "lge";
        r1 = r1.equals(r2);	 Catch:{ Exception -> 0x011f, all -> 0x0be4 }
        if (r1 != 0) goto L_0x01aa;
    L_0x0185:
        r1 = r3 + 2047;
        r1 = r1 & -2048;
        r1 = r1 - r3;
        r5 = r5 + r1;
        goto L_0x0172;
    L_0x018c:
        r3 = 5;
        if (r2 != r3) goto L_0x0190;
    L_0x018f:
        goto L_0x01aa;
    L_0x0190:
        r3 = 3;
        if (r2 != r3) goto L_0x01aa;
    L_0x0193:
        r2 = "baidu";
        r1 = r1.equals(r2);	 Catch:{ Exception -> 0x011f, all -> 0x0be4 }
        if (r1 == 0) goto L_0x01aa;
    L_0x019b:
        r1 = r11 % 16;
        r2 = 16;
        r1 = 16 - r1;
        r1 = r1 + r11;
        r1 = r1 - r11;
        r1 = r1 * r12;
        r2 = r1 * 5;
        r2 = r2 / 4;
        goto L_0x0171;
    L_0x01aa:
        r24 = r5;
        r15 = 0;
    L_0x01ad:
        r1 = r14.extractor;	 Catch:{ Exception -> 0x0b04, all -> 0x0be4 }
        r1.selectTrack(r4);	 Catch:{ Exception -> 0x0b04, all -> 0x0be4 }
        r1 = r14.extractor;	 Catch:{ Exception -> 0x0b04, all -> 0x0be4 }
        r1 = r1.getTrackFormat(r4);	 Catch:{ Exception -> 0x0b04, all -> 0x0be4 }
        r2 = 0;
        r5 = (r7 > r2 ? 1 : (r7 == r2 ? 0 : -1));
        if (r5 <= 0) goto L_0x01c7;
    L_0x01be:
        r5 = r14.extractor;	 Catch:{ Exception -> 0x011f, all -> 0x0be4 }
        r2 = 0;
        r5.seekTo(r7, r2);	 Catch:{ Exception -> 0x011f, all -> 0x0be4 }
        r28 = r4;
        goto L_0x01d1;
    L_0x01c7:
        r2 = r14.extractor;	 Catch:{ Exception -> 0x0b04, all -> 0x0be4 }
        r28 = r4;
        r3 = 0;
        r5 = 0;
        r2.seekTo(r3, r5);	 Catch:{ Exception -> 0x0aff, all -> 0x0be4 }
    L_0x01d1:
        if (r10 > 0) goto L_0x01d7;
    L_0x01d3:
        r2 = 921600; // 0xe1000 float:1.291437E-39 double:4.55331E-318;
        goto L_0x01d8;
    L_0x01d7:
        r2 = r10;
    L_0x01d8:
        r3 = android.media.MediaFormat.createVideoFormat(r6, r12, r11);	 Catch:{ Exception -> 0x0af2, all -> 0x0be4 }
        r4 = "color-format";
        r3.setInteger(r4, r13);	 Catch:{ Exception -> 0x0af2, all -> 0x0be4 }
        r4 = "bitrate";
        r3.setInteger(r4, r2);	 Catch:{ Exception -> 0x0af2, all -> 0x0be4 }
        r4 = "frame-rate";
        r3.setInteger(r4, r9);	 Catch:{ Exception -> 0x0af2, all -> 0x0be4 }
        r4 = "i-frame-interval";
        r5 = 2;
        r3.setInteger(r4, r5);	 Catch:{ Exception -> 0x0af2, all -> 0x0be4 }
        r4 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0af2, all -> 0x0be4 }
        r5 = 23;
        if (r4 < r5) goto L_0x0290;
    L_0x01f7:
        r4 = java.lang.Math.min(r11, r12);	 Catch:{ Exception -> 0x0288, all -> 0x0be4 }
        r5 = 1080; // 0x438 float:1.513E-42 double:5.336E-321;
        if (r4 < r5) goto L_0x0204;
    L_0x01ff:
        r4 = 8;
        r5 = 4096; // 0x1000 float:5.74E-42 double:2.0237E-320;
        goto L_0x021f;
    L_0x0204:
        r4 = java.lang.Math.min(r11, r12);	 Catch:{ Exception -> 0x0288, all -> 0x0be4 }
        r5 = 720; // 0x2d0 float:1.009E-42 double:3.557E-321;
        if (r4 < r5) goto L_0x0211;
    L_0x020c:
        r4 = 8;
        r5 = 2048; // 0x800 float:2.87E-42 double:1.0118E-320;
        goto L_0x021f;
    L_0x0211:
        r4 = java.lang.Math.min(r11, r12);	 Catch:{ Exception -> 0x0288, all -> 0x0be4 }
        r5 = 480; // 0x1e0 float:6.73E-43 double:2.37E-321;
        if (r4 < r5) goto L_0x021c;
    L_0x0219:
        r5 = 512; // 0x200 float:7.175E-43 double:2.53E-321;
        goto L_0x021e;
    L_0x021c:
        r5 = 256; // 0x100 float:3.59E-43 double:1.265E-321;
    L_0x021e:
        r4 = 1;
    L_0x021f:
        r10 = android.media.MediaCodecInfo.CodecCapabilities.createFromProfileLevel(r6, r4, r5);	 Catch:{ Exception -> 0x0288, all -> 0x0be4 }
        r68 = r10;
        if (r10 != 0) goto L_0x0232;
    L_0x0227:
        r10 = 8;
        if (r4 != r10) goto L_0x0232;
    L_0x022b:
        r4 = 1;
        r10 = android.media.MediaCodecInfo.CodecCapabilities.createFromProfileLevel(r6, r4, r5);	 Catch:{ Exception -> 0x0288, all -> 0x0be4 }
        r4 = 1;
        goto L_0x0234;
    L_0x0232:
        r10 = r68;
    L_0x0234:
        r31 = r10.getEncoderCapabilities();	 Catch:{ Exception -> 0x0288, all -> 0x0be4 }
        if (r31 == 0) goto L_0x0285;
    L_0x023a:
        r31 = r15;
        r15 = "profile";
        r3.setInteger(r15, r4);	 Catch:{ Exception -> 0x0288, all -> 0x0be4 }
        r4 = "level";
        r3.setInteger(r4, r5);	 Catch:{ Exception -> 0x0288, all -> 0x0be4 }
        r4 = r10.getVideoCapabilities();	 Catch:{ Exception -> 0x0288, all -> 0x0be4 }
        r4 = r4.getBitrateRange();	 Catch:{ Exception -> 0x0288, all -> 0x0be4 }
        r4 = r4.getUpper();	 Catch:{ Exception -> 0x0288, all -> 0x0be4 }
        r4 = (java.lang.Integer) r4;	 Catch:{ Exception -> 0x0288, all -> 0x0be4 }
        r4 = r4.intValue();	 Catch:{ Exception -> 0x0288, all -> 0x0be4 }
        if (r2 <= r4) goto L_0x0265;
    L_0x025a:
        r2 = "bitrate";
        r3.setInteger(r2, r4);	 Catch:{ Exception -> 0x0261, all -> 0x0be4 }
        r2 = r4;
        goto L_0x0265;
    L_0x0261:
        r0 = move-exception;
        r1 = r0;
        r10 = r4;
        goto L_0x028b;
    L_0x0265:
        r4 = r10.getVideoCapabilities();	 Catch:{ Exception -> 0x0288, all -> 0x0be4 }
        r4 = r4.getSupportedFrameRates();	 Catch:{ Exception -> 0x0288, all -> 0x0be4 }
        r4 = r4.getUpper();	 Catch:{ Exception -> 0x0288, all -> 0x0be4 }
        r4 = (java.lang.Integer) r4;	 Catch:{ Exception -> 0x0288, all -> 0x0be4 }
        r4 = r4.intValue();	 Catch:{ Exception -> 0x0288, all -> 0x0be4 }
        if (r9 <= r4) goto L_0x02a7;
    L_0x0279:
        r5 = "frame-rate";
        r3.setInteger(r5, r4);	 Catch:{ Exception -> 0x0280, all -> 0x0be4 }
        r9 = r4;
        goto L_0x02a7;
    L_0x0280:
        r0 = move-exception;
        r1 = r0;
        r10 = r2;
        r9 = r4;
        goto L_0x028b;
    L_0x0285:
        r31 = r15;
        goto L_0x02a7;
    L_0x0288:
        r0 = move-exception;
        r1 = r0;
        r10 = r2;
    L_0x028b:
        r13 = r12;
        r11 = r28;
        goto L_0x0123;
    L_0x0290:
        r31 = r15;
        r4 = java.lang.Math.min(r11, r12);	 Catch:{ Exception -> 0x0af2, all -> 0x0be4 }
        r5 = 480; // 0x1e0 float:6.73E-43 double:2.37E-321;
        if (r4 > r5) goto L_0x02a7;
    L_0x029a:
        r4 = 921600; // 0xe1000 float:1.291437E-39 double:4.55331E-318;
        if (r2 <= r4) goto L_0x02a2;
    L_0x029f:
        r2 = 921600; // 0xe1000 float:1.291437E-39 double:4.55331E-318;
    L_0x02a2:
        r4 = "bitrate";
        r3.setInteger(r4, r2);	 Catch:{ Exception -> 0x0288, all -> 0x0be4 }
    L_0x02a7:
        r10 = r2;
        r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0aec, all -> 0x0be4 }
        r4 = 18;
        if (r2 >= r4) goto L_0x02be;
    L_0x02ae:
        r2 = "stride";
        r4 = r12 + 32;
        r3.setInteger(r2, r4);	 Catch:{ Exception -> 0x02bb, all -> 0x0be4 }
        r2 = "slice-height";
        r3.setInteger(r2, r11);	 Catch:{ Exception -> 0x02bb, all -> 0x0be4 }
        goto L_0x02be;
    L_0x02bb:
        r0 = move-exception;
        r1 = r0;
        goto L_0x028b;
    L_0x02be:
        r15 = android.media.MediaCodec.createEncoderByType(r6);	 Catch:{ Exception -> 0x0aec, all -> 0x0be4 }
        r2 = 0;
        r4 = 1;
        r15.configure(r3, r2, r2, r4);	 Catch:{ Exception -> 0x0ad8, all -> 0x0be4 }
        r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0ac6, all -> 0x0be4 }
        r3 = 18;
        if (r2 < r3) goto L_0x02ef;
    L_0x02cd:
        r2 = new org.telegram.messenger.video.InputSurface;	 Catch:{ Exception -> 0x02e5, all -> 0x0be4 }
        r3 = r15.createInputSurface();	 Catch:{ Exception -> 0x02e5, all -> 0x0be4 }
        r2.<init>(r3);	 Catch:{ Exception -> 0x02e5, all -> 0x0be4 }
        r2.makeCurrent();	 Catch:{ Exception -> 0x02db, all -> 0x0be4 }
        r5 = r2;
        goto L_0x02f0;
    L_0x02db:
        r0 = move-exception;
        r1 = r0;
        r5 = r2;
        r13 = r12;
        r11 = r28;
        r2 = 0;
        r4 = 0;
        goto L_0x0127;
    L_0x02e5:
        r0 = move-exception;
        r1 = r0;
        r13 = r12;
        r11 = r28;
        r2 = 0;
        r4 = 0;
        r5 = 0;
        goto L_0x0127;
    L_0x02ef:
        r5 = 0;
    L_0x02f0:
        r15.start();	 Catch:{ Exception -> 0x0aae, all -> 0x0be4 }
        r2 = "mime";
        r2 = r1.getString(r2);	 Catch:{ Exception -> 0x0aae, all -> 0x0be4 }
        r4 = android.media.MediaCodec.createDecoderByType(r2);	 Catch:{ Exception -> 0x0aae, all -> 0x0be4 }
        r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0a95, all -> 0x0be4 }
        r3 = 18;
        if (r2 < r3) goto L_0x0311;
    L_0x0303:
        r2 = new org.telegram.messenger.video.OutputSurface;	 Catch:{ Exception -> 0x0309, all -> 0x0be4 }
        r2.<init>();	 Catch:{ Exception -> 0x0309, all -> 0x0be4 }
        goto L_0x0318;
    L_0x0309:
        r0 = move-exception;
        r1 = r0;
        r13 = r12;
        r11 = r28;
        r2 = 0;
        goto L_0x0127;
    L_0x0311:
        r2 = new org.telegram.messenger.video.OutputSurface;	 Catch:{ Exception -> 0x0a95, all -> 0x0be4 }
        r3 = r63;
        r2.<init>(r12, r11, r3);	 Catch:{ Exception -> 0x0a95, all -> 0x0be4 }
    L_0x0318:
        r3 = r2.getSurface();	 Catch:{ Exception -> 0x0a7d, all -> 0x0be4 }
        r67 = r9;
        r68 = r10;
        r9 = 0;
        r10 = 0;
        r4.configure(r1, r3, r9, r10);	 Catch:{ Exception -> 0x0a69, all -> 0x0be4 }
        r4.start();	 Catch:{ Exception -> 0x0a69, all -> 0x0be4 }
        r1 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0a69, all -> 0x0be4 }
        r10 = 21;
        if (r1 >= r10) goto L_0x0350;
    L_0x032e:
        r1 = r4.getInputBuffers();	 Catch:{ Exception -> 0x0345, all -> 0x0be4 }
        r3 = r15.getOutputBuffers();	 Catch:{ Exception -> 0x0345, all -> 0x0be4 }
        r9 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0345, all -> 0x0be4 }
        r10 = 18;
        if (r9 >= r10) goto L_0x0343;
    L_0x033c:
        r9 = r15.getInputBuffers();	 Catch:{ Exception -> 0x0345, all -> 0x0be4 }
        r10 = r9;
        r9 = r1;
        goto L_0x0353;
    L_0x0343:
        r9 = r1;
        goto L_0x0352;
    L_0x0345:
        r0 = move-exception;
        r9 = r67;
        r10 = r68;
        r1 = r0;
        r13 = r12;
    L_0x034c:
        r11 = r28;
        goto L_0x0127;
    L_0x0350:
        r3 = 0;
        r9 = 0;
    L_0x0352:
        r10 = 0;
    L_0x0353:
        if (r29 < 0) goto L_0x03f6;
    L_0x0355:
        r1 = r14.extractor;	 Catch:{ Exception -> 0x03e6, all -> 0x0be4 }
        r35 = r3;
        r3 = r29;
        r1 = r1.getTrackFormat(r3);	 Catch:{ Exception -> 0x03e6, all -> 0x0be4 }
        r29 = r13;
        r13 = "mime";
        r13 = r1.getString(r13);	 Catch:{ Exception -> 0x03e6, all -> 0x0be4 }
        r43 = r10;
        r10 = "audio/mp4a-latm";
        r10 = r13.equals(r10);	 Catch:{ Exception -> 0x03e6, all -> 0x0be4 }
        r13 = r14.mediaMuxer;	 Catch:{ Exception -> 0x03e6, all -> 0x0be4 }
        r44 = r5;
        r5 = 1;
        r13 = r13.addTrack(r1, r5);	 Catch:{ Exception -> 0x03d4, all -> 0x0be4 }
        if (r10 == 0) goto L_0x039b;
    L_0x037a:
        r5 = r14.extractor;	 Catch:{ Exception -> 0x0393, all -> 0x0be4 }
        r5.selectTrack(r3);	 Catch:{ Exception -> 0x0393, all -> 0x0be4 }
        r5 = "max-input-size";
        r1 = r1.getInteger(r5);	 Catch:{ Exception -> 0x0393, all -> 0x0be4 }
        r1 = java.nio.ByteBuffer.allocateDirect(r1);	 Catch:{ Exception -> 0x0393, all -> 0x0be4 }
        r11 = r71;
        r45 = r10;
        r5 = r13;
        r10 = 0;
        r13 = r61;
        goto L_0x0409;
    L_0x0393:
        r0 = move-exception;
        r9 = r67;
        r10 = r68;
        r1 = r0;
        r13 = r12;
        goto L_0x03e0;
    L_0x039b:
        r5 = new android.media.MediaExtractor;	 Catch:{ Exception -> 0x03d4, all -> 0x0be4 }
        r5.<init>();	 Catch:{ Exception -> 0x03d4, all -> 0x0be4 }
        r36 = r13;
        r13 = r61;
        r5.setDataSource(r13);	 Catch:{ Exception -> 0x03d2, all -> 0x0be4 }
        r5.selectTrack(r3);	 Catch:{ Exception -> 0x03d2, all -> 0x0be4 }
        r37 = r10;
        r10 = 0;
        r33 = (r7 > r10 ? 1 : (r7 == r10 ? 0 : -1));
        if (r33 <= 0) goto L_0x03b7;
    L_0x03b2:
        r10 = 0;
        r5.seekTo(r7, r10);	 Catch:{ Exception -> 0x0393, all -> 0x0be4 }
        goto L_0x03bc;
    L_0x03b7:
        r11 = r10;
        r10 = 0;
        r5.seekTo(r11, r10);	 Catch:{ Exception -> 0x03d2, all -> 0x0be4 }
    L_0x03bc:
        r10 = new org.telegram.messenger.video.AudioRecoder;	 Catch:{ Exception -> 0x03d2, all -> 0x0be4 }
        r10.<init>(r1, r5, r3);	 Catch:{ Exception -> 0x03d2, all -> 0x0be4 }
        r10.startTime = r7;	 Catch:{ Exception -> 0x03cd, all -> 0x0be4 }
        r11 = r71;
        r10.endTime = r11;	 Catch:{ Exception -> 0x0449, all -> 0x0be4 }
        r5 = r36;
        r45 = r37;
        r1 = 0;
        goto L_0x0409;
    L_0x03cd:
        r0 = move-exception;
        r11 = r71;
        goto L_0x044a;
    L_0x03d2:
        r0 = move-exception;
        goto L_0x03d7;
    L_0x03d4:
        r0 = move-exception;
        r13 = r61;
    L_0x03d7:
        r11 = r71;
        r13 = r65;
        r9 = r67;
        r10 = r68;
        r1 = r0;
    L_0x03e0:
        r11 = r28;
        r5 = r44;
        goto L_0x0127;
    L_0x03e6:
        r0 = move-exception;
        r13 = r61;
        r11 = r71;
        r44 = r5;
        r13 = r65;
        r9 = r67;
        r10 = r68;
        r1 = r0;
        goto L_0x034c;
    L_0x03f6:
        r11 = r71;
        r35 = r3;
        r44 = r5;
        r43 = r10;
        r3 = r29;
        r29 = r13;
        r13 = r61;
        r1 = 0;
        r5 = -5;
        r10 = 0;
        r45 = 1;
    L_0x0409:
        r60.checkConversionCanceled();	 Catch:{ Exception -> 0x0a50, all -> 0x0be4 }
        r52 = r20;
        r49 = r35;
        r20 = 0;
        r21 = 0;
        r46 = 0;
        r47 = 0;
        r48 = -5;
        r50 = 0;
    L_0x041c:
        if (r20 == 0) goto L_0x0438;
    L_0x041e:
        if (r45 != 0) goto L_0x0423;
    L_0x0420:
        if (r21 != 0) goto L_0x0423;
    L_0x0422:
        goto L_0x0438;
    L_0x0423:
        r13 = r65;
        r12 = r66;
        r9 = r67;
        r55 = r10;
        r11 = r28;
        r5 = r44;
        r3 = 0;
        r17 = 0;
        r59 = 1;
        r10 = r68;
        goto L_0x0b52;
    L_0x0438:
        r60.checkConversionCanceled();	 Catch:{ Exception -> 0x0a50, all -> 0x0be4 }
        if (r45 != 0) goto L_0x045d;
    L_0x043d:
        if (r10 == 0) goto L_0x045d;
    L_0x043f:
        r13 = r14.mediaMuxer;	 Catch:{ Exception -> 0x0449, all -> 0x0be4 }
        r13 = r10.step(r13, r5);	 Catch:{ Exception -> 0x0449, all -> 0x0be4 }
        r8 = r6;
        r21 = r13;
        goto L_0x045e;
    L_0x0449:
        r0 = move-exception;
    L_0x044a:
        r13 = r65;
        r9 = r67;
        r1 = r0;
        r55 = r10;
        r11 = r28;
    L_0x0453:
        r5 = r44;
        r58 = 0;
        r59 = 1;
        r10 = r68;
        goto L_0x0b15;
    L_0x045d:
        r8 = r6;
    L_0x045e:
        r6 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        if (r46 != 0) goto L_0x05c0;
    L_0x0462:
        r13 = r14.extractor;	 Catch:{ Exception -> 0x05aa, all -> 0x0be4 }
        r13 = r13.getSampleTrackIndex();	 Catch:{ Exception -> 0x05aa, all -> 0x0be4 }
        r54 = r8;
        r8 = r28;
        if (r13 != r8) goto L_0x04d0;
    L_0x046e:
        r13 = r4.dequeueInputBuffer(r6);	 Catch:{ Exception -> 0x04c6, all -> 0x0be4 }
        if (r13 < 0) goto L_0x04b5;
    L_0x0474:
        r6 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x04c6, all -> 0x0be4 }
        r7 = 21;
        if (r6 >= r7) goto L_0x047d;
    L_0x047a:
        r6 = r9[r13];	 Catch:{ Exception -> 0x04c6, all -> 0x0be4 }
        goto L_0x0481;
    L_0x047d:
        r6 = r4.getInputBuffer(r13);	 Catch:{ Exception -> 0x04c6, all -> 0x0be4 }
    L_0x0481:
        r7 = r14.extractor;	 Catch:{ Exception -> 0x04c6, all -> 0x0be4 }
        r28 = r8;
        r8 = 0;
        r38 = r7.readSampleData(r6, r8);	 Catch:{ Exception -> 0x0449, all -> 0x0be4 }
        if (r38 >= 0) goto L_0x049e;
    L_0x048c:
        r37 = 0;
        r38 = 0;
        r39 = 0;
        r41 = 4;
        r35 = r4;
        r36 = r13;
        r35.queueInputBuffer(r36, r37, r38, r39, r41);	 Catch:{ Exception -> 0x0449, all -> 0x0be4 }
        r46 = 1;
        goto L_0x04b7;
    L_0x049e:
        r37 = 0;
        r6 = r14.extractor;	 Catch:{ Exception -> 0x0449, all -> 0x0be4 }
        r39 = r6.getSampleTime();	 Catch:{ Exception -> 0x0449, all -> 0x0be4 }
        r41 = 0;
        r35 = r4;
        r36 = r13;
        r35.queueInputBuffer(r36, r37, r38, r39, r41);	 Catch:{ Exception -> 0x0449, all -> 0x0be4 }
        r6 = r14.extractor;	 Catch:{ Exception -> 0x0449, all -> 0x0be4 }
        r6.advance();	 Catch:{ Exception -> 0x0449, all -> 0x0be4 }
        goto L_0x04b7;
    L_0x04b5:
        r28 = r8;
    L_0x04b7:
        r57 = r1;
        r56 = r2;
        r55 = r10;
        r8 = r22;
        r6 = 0;
        r1 = r69;
        r22 = r9;
        goto L_0x058e;
    L_0x04c6:
        r0 = move-exception;
        r13 = r65;
        r9 = r67;
        r1 = r0;
        r11 = r8;
        r55 = r10;
        goto L_0x0453;
    L_0x04d0:
        r28 = r8;
        if (r45 == 0) goto L_0x057c;
    L_0x04d4:
        r6 = -1;
        if (r3 == r6) goto L_0x056f;
    L_0x04d7:
        if (r13 != r3) goto L_0x057c;
    L_0x04d9:
        r6 = r14.extractor;	 Catch:{ Exception -> 0x05aa, all -> 0x0be4 }
        r7 = 0;
        r6 = r6.readSampleData(r1, r7);	 Catch:{ Exception -> 0x05aa, all -> 0x0be4 }
        r8 = r22;
        r8.size = r6;	 Catch:{ Exception -> 0x05aa, all -> 0x0be4 }
        r6 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x05aa, all -> 0x0be4 }
        r13 = 21;
        if (r6 >= r13) goto L_0x04f2;
    L_0x04ea:
        r1.position(r7);	 Catch:{ Exception -> 0x0449, all -> 0x0be4 }
        r6 = r8.size;	 Catch:{ Exception -> 0x0449, all -> 0x0be4 }
        r1.limit(r6);	 Catch:{ Exception -> 0x0449, all -> 0x0be4 }
    L_0x04f2:
        r6 = r8.size;	 Catch:{ Exception -> 0x05aa, all -> 0x0be4 }
        if (r6 < 0) goto L_0x0504;
    L_0x04f6:
        r6 = r14.extractor;	 Catch:{ Exception -> 0x0449, all -> 0x0be4 }
        r6 = r6.getSampleTime();	 Catch:{ Exception -> 0x0449, all -> 0x0be4 }
        r8.presentationTimeUs = r6;	 Catch:{ Exception -> 0x0449, all -> 0x0be4 }
        r6 = r14.extractor;	 Catch:{ Exception -> 0x0449, all -> 0x0be4 }
        r6.advance();	 Catch:{ Exception -> 0x0449, all -> 0x0be4 }
        goto L_0x0509;
    L_0x0504:
        r6 = 0;
        r8.size = r6;	 Catch:{ Exception -> 0x05aa, all -> 0x0be4 }
        r46 = 1;
    L_0x0509:
        r6 = r8.size;	 Catch:{ Exception -> 0x05aa, all -> 0x0be4 }
        if (r6 <= 0) goto L_0x0566;
    L_0x050d:
        r6 = 0;
        r13 = (r11 > r6 ? 1 : (r11 == r6 ? 0 : -1));
        if (r13 < 0) goto L_0x0519;
    L_0x0513:
        r6 = r8.presentationTimeUs;	 Catch:{ Exception -> 0x0449, all -> 0x0be4 }
        r13 = (r6 > r11 ? 1 : (r6 == r11 ? 0 : -1));
        if (r13 >= 0) goto L_0x0566;
    L_0x0519:
        r6 = 0;
        r8.offset = r6;	 Catch:{ Exception -> 0x05aa, all -> 0x0be4 }
        r7 = r14.extractor;	 Catch:{ Exception -> 0x05aa, all -> 0x0be4 }
        r7 = r7.getSampleFlags();	 Catch:{ Exception -> 0x05aa, all -> 0x0be4 }
        r8.flags = r7;	 Catch:{ Exception -> 0x05aa, all -> 0x0be4 }
        r7 = r14.mediaMuxer;	 Catch:{ Exception -> 0x05aa, all -> 0x0be4 }
        r22 = r9;
        r55 = r10;
        r9 = r7.writeSampleData(r5, r1, r8, r6);	 Catch:{ Exception -> 0x0562, all -> 0x0be4 }
        r6 = 0;
        r13 = (r9 > r6 ? 1 : (r9 == r6 ? 0 : -1));
        if (r13 == 0) goto L_0x055b;
    L_0x0534:
        r6 = r14.callback;	 Catch:{ Exception -> 0x0562, all -> 0x0be4 }
        if (r6 == 0) goto L_0x055b;
    L_0x0538:
        r6 = r8.presentationTimeUs;	 Catch:{ Exception -> 0x0562, all -> 0x0be4 }
        r57 = r1;
        r56 = r2;
        r11 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r1 = r69;
        r6 = r6 - r1;
        r13 = (r6 > r50 ? 1 : (r6 == r50 ? 0 : -1));
        if (r13 <= 0) goto L_0x054b;
    L_0x0547:
        r6 = r8.presentationTimeUs;	 Catch:{ Exception -> 0x05a8, all -> 0x0be4 }
        r50 = r6 - r1;
    L_0x054b:
        r6 = r50;
        r13 = r14.callback;	 Catch:{ Exception -> 0x05a8, all -> 0x0be4 }
        r11 = (float) r6;	 Catch:{ Exception -> 0x05a8, all -> 0x0be4 }
        r12 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r11 = r11 / r12;
        r11 = r11 / r18;
        r13.didWriteData(r9, r11);	 Catch:{ Exception -> 0x05a8, all -> 0x0be4 }
        r50 = r6;
        goto L_0x058d;
    L_0x055b:
        r57 = r1;
        r56 = r2;
    L_0x055f:
        r1 = r69;
        goto L_0x058d;
    L_0x0562:
        r0 = move-exception;
        r56 = r2;
        goto L_0x05af;
    L_0x0566:
        r57 = r1;
        r56 = r2;
        r22 = r9;
        r55 = r10;
        goto L_0x055f;
    L_0x056f:
        r57 = r1;
        r56 = r2;
        r55 = r10;
        r8 = r22;
        r1 = r69;
        r22 = r9;
        goto L_0x0589;
    L_0x057c:
        r57 = r1;
        r56 = r2;
        r55 = r10;
        r8 = r22;
        r1 = r69;
        r22 = r9;
        r6 = -1;
    L_0x0589:
        if (r13 != r6) goto L_0x058d;
    L_0x058b:
        r6 = 1;
        goto L_0x058e;
    L_0x058d:
        r6 = 0;
    L_0x058e:
        if (r6 == 0) goto L_0x05ce;
    L_0x0590:
        r6 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r36 = r4.dequeueInputBuffer(r6);	 Catch:{ Exception -> 0x05a8, all -> 0x0be4 }
        if (r36 < 0) goto L_0x05ce;
    L_0x0598:
        r37 = 0;
        r38 = 0;
        r39 = 0;
        r41 = 4;
        r35 = r4;
        r35.queueInputBuffer(r36, r37, r38, r39, r41);	 Catch:{ Exception -> 0x05a8, all -> 0x0be4 }
        r46 = 1;
        goto L_0x05ce;
    L_0x05a8:
        r0 = move-exception;
        goto L_0x05b1;
    L_0x05aa:
        r0 = move-exception;
        r56 = r2;
        r55 = r10;
    L_0x05af:
        r1 = r69;
    L_0x05b1:
        r13 = r65;
    L_0x05b3:
        r9 = r67;
        r10 = r68;
        r1 = r0;
        r11 = r28;
        r5 = r44;
        r2 = r56;
        goto L_0x0129;
    L_0x05c0:
        r57 = r1;
        r56 = r2;
        r54 = r8;
        r55 = r10;
        r8 = r22;
        r1 = r69;
        r22 = r9;
    L_0x05ce:
        r6 = r47 ^ 1;
        r9 = r6;
        r7 = r48;
        r6 = 1;
    L_0x05d4:
        if (r9 != 0) goto L_0x05ee;
    L_0x05d6:
        if (r6 == 0) goto L_0x05d9;
    L_0x05d8:
        goto L_0x05ee;
    L_0x05d9:
        r13 = r61;
        r11 = r71;
        r48 = r7;
        r9 = r22;
        r6 = r54;
        r10 = r55;
        r22 = r8;
        r7 = r1;
        r2 = r56;
        r1 = r57;
        goto L_0x041c;
    L_0x05ee:
        r60.checkConversionCanceled();	 Catch:{ Exception -> 0x0a3a, all -> 0x0be4 }
        if (r76 == 0) goto L_0x05f6;
    L_0x05f3:
        r10 = 22000; // 0x55f0 float:3.0829E-41 double:1.08694E-319;
        goto L_0x05f8;
    L_0x05f6:
        r10 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
    L_0x05f8:
        r10 = r15.dequeueOutputBuffer(r8, r10);	 Catch:{ Exception -> 0x0a3a, all -> 0x0be4 }
        r11 = -1;
        if (r10 != r11) goto L_0x0613;
    L_0x05ff:
        r13 = r65;
        r36 = r3;
        r27 = r5;
        r12 = r7;
        r11 = r20;
        r3 = r54;
        r6 = -1;
        r19 = 0;
    L_0x060d:
        r20 = 2;
        r5 = r66;
        goto L_0x0744;
    L_0x0613:
        r11 = -3;
        if (r10 != r11) goto L_0x062f;
    L_0x0616:
        r11 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x05a8, all -> 0x0be4 }
        r12 = 21;
        if (r11 >= r12) goto L_0x0620;
    L_0x061c:
        r49 = r15.getOutputBuffers();	 Catch:{ Exception -> 0x05a8, all -> 0x0be4 }
    L_0x0620:
        r13 = r65;
        r36 = r3;
        r27 = r5;
        r19 = r6;
        r12 = r7;
        r11 = r20;
        r3 = r54;
        r6 = -1;
        goto L_0x060d;
    L_0x062f:
        r11 = -2;
        if (r10 != r11) goto L_0x0641;
    L_0x0632:
        r11 = r15.getOutputFormat();	 Catch:{ Exception -> 0x05a8, all -> 0x0be4 }
        r12 = -5;
        if (r7 != r12) goto L_0x0620;
    L_0x0639:
        r7 = r14.mediaMuxer;	 Catch:{ Exception -> 0x05a8, all -> 0x0be4 }
        r12 = 0;
        r7 = r7.addTrack(r11, r12);	 Catch:{ Exception -> 0x05a8, all -> 0x0be4 }
        goto L_0x0620;
    L_0x0641:
        if (r10 < 0) goto L_0x0a08;
    L_0x0643:
        r11 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0a3a, all -> 0x0be4 }
        r12 = 21;
        if (r11 >= r12) goto L_0x064c;
    L_0x0649:
        r11 = r49[r10];	 Catch:{ Exception -> 0x05a8, all -> 0x0be4 }
        goto L_0x0650;
    L_0x064c:
        r11 = r15.getOutputBuffer(r10);	 Catch:{ Exception -> 0x0a3a, all -> 0x0be4 }
    L_0x0650:
        if (r11 == 0) goto L_0x09de;
    L_0x0652:
        r13 = r8.size;	 Catch:{ Exception -> 0x0a3a, all -> 0x0be4 }
        r12 = 1;
        if (r13 <= r12) goto L_0x0724;
    L_0x0657:
        r13 = r8.flags;	 Catch:{ Exception -> 0x071d, all -> 0x0be4 }
        r20 = 2;
        r13 = r13 & 2;
        if (r13 != 0) goto L_0x069b;
    L_0x065f:
        r13 = r14.mediaMuxer;	 Catch:{ Exception -> 0x05a8, all -> 0x0be4 }
        r27 = r5;
        r35 = r6;
        r5 = r13.writeSampleData(r7, r11, r8, r12);	 Catch:{ Exception -> 0x05a8, all -> 0x0be4 }
        r11 = 0;
        r13 = (r5 > r11 ? 1 : (r5 == r11 ? 0 : -1));
        if (r13 == 0) goto L_0x0691;
    L_0x066f:
        r11 = r14.callback;	 Catch:{ Exception -> 0x05a8, all -> 0x0be4 }
        if (r11 == 0) goto L_0x0691;
    L_0x0673:
        r11 = r8.presentationTimeUs;	 Catch:{ Exception -> 0x05a8, all -> 0x0be4 }
        r11 = r11 - r1;
        r13 = (r11 > r50 ? 1 : (r11 == r50 ? 0 : -1));
        if (r13 <= 0) goto L_0x067e;
    L_0x067a:
        r11 = r8.presentationTimeUs;	 Catch:{ Exception -> 0x05a8, all -> 0x0be4 }
        r50 = r11 - r1;
    L_0x067e:
        r11 = r50;
        r13 = r14.callback;	 Catch:{ Exception -> 0x05a8, all -> 0x0be4 }
        r36 = r3;
        r3 = (float) r11;	 Catch:{ Exception -> 0x05a8, all -> 0x0be4 }
        r37 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r3 = r3 / r37;
        r3 = r3 / r18;
        r13.didWriteData(r5, r3);	 Catch:{ Exception -> 0x05a8, all -> 0x0be4 }
        r50 = r11;
        goto L_0x0693;
    L_0x0691:
        r36 = r3;
    L_0x0693:
        r13 = r65;
        r5 = r66;
        r3 = r54;
        goto L_0x0732;
    L_0x069b:
        r36 = r3;
        r27 = r5;
        r35 = r6;
        r3 = -5;
        if (r7 != r3) goto L_0x0693;
    L_0x06a4:
        r5 = r8.size;	 Catch:{ Exception -> 0x071d, all -> 0x0be4 }
        r5 = new byte[r5];	 Catch:{ Exception -> 0x071d, all -> 0x0be4 }
        r6 = r8.offset;	 Catch:{ Exception -> 0x071d, all -> 0x0be4 }
        r7 = r8.size;	 Catch:{ Exception -> 0x071d, all -> 0x0be4 }
        r6 = r6 + r7;
        r11.limit(r6);	 Catch:{ Exception -> 0x071d, all -> 0x0be4 }
        r6 = r8.offset;	 Catch:{ Exception -> 0x071d, all -> 0x0be4 }
        r11.position(r6);	 Catch:{ Exception -> 0x071d, all -> 0x0be4 }
        r11.get(r5);	 Catch:{ Exception -> 0x071d, all -> 0x0be4 }
        r6 = r8.size;	 Catch:{ Exception -> 0x071d, all -> 0x0be4 }
        r7 = 1;
        r6 = r6 - r7;
    L_0x06bc:
        r11 = 3;
        if (r6 < 0) goto L_0x06fa;
    L_0x06bf:
        if (r6 <= r11) goto L_0x06fa;
    L_0x06c1:
        r12 = r5[r6];	 Catch:{ Exception -> 0x05a8, all -> 0x0be4 }
        if (r12 != r7) goto L_0x06f5;
    L_0x06c5:
        r7 = r6 + -1;
        r7 = r5[r7];	 Catch:{ Exception -> 0x05a8, all -> 0x0be4 }
        if (r7 != 0) goto L_0x06f5;
    L_0x06cb:
        r7 = r6 + -2;
        r7 = r5[r7];	 Catch:{ Exception -> 0x05a8, all -> 0x0be4 }
        if (r7 != 0) goto L_0x06f5;
    L_0x06d1:
        r7 = r6 + -3;
        r12 = r5[r7];	 Catch:{ Exception -> 0x05a8, all -> 0x0be4 }
        if (r12 != 0) goto L_0x06f5;
    L_0x06d7:
        r6 = java.nio.ByteBuffer.allocate(r7);	 Catch:{ Exception -> 0x05a8, all -> 0x0be4 }
        r12 = r8.size;	 Catch:{ Exception -> 0x05a8, all -> 0x0be4 }
        r12 = r12 - r7;
        r12 = java.nio.ByteBuffer.allocate(r12);	 Catch:{ Exception -> 0x05a8, all -> 0x0be4 }
        r13 = 0;
        r3 = r6.put(r5, r13, r7);	 Catch:{ Exception -> 0x05a8, all -> 0x0be4 }
        r3.position(r13);	 Catch:{ Exception -> 0x05a8, all -> 0x0be4 }
        r3 = r8.size;	 Catch:{ Exception -> 0x05a8, all -> 0x0be4 }
        r3 = r3 - r7;
        r3 = r12.put(r5, r7, r3);	 Catch:{ Exception -> 0x05a8, all -> 0x0be4 }
        r3.position(r13);	 Catch:{ Exception -> 0x05a8, all -> 0x0be4 }
        goto L_0x06fc;
    L_0x06f5:
        r6 = r6 + -1;
        r3 = -5;
        r7 = 1;
        goto L_0x06bc;
    L_0x06fa:
        r6 = 0;
        r12 = 0;
    L_0x06fc:
        r13 = r65;
        r5 = r66;
        r3 = r54;
        r7 = android.media.MediaFormat.createVideoFormat(r3, r13, r5);	 Catch:{ Exception -> 0x07ae, all -> 0x0be4 }
        if (r6 == 0) goto L_0x0714;
    L_0x0708:
        if (r12 == 0) goto L_0x0714;
    L_0x070a:
        r11 = "csd-0";
        r7.setByteBuffer(r11, r6);	 Catch:{ Exception -> 0x07ae, all -> 0x0be4 }
        r6 = "csd-1";
        r7.setByteBuffer(r6, r12);	 Catch:{ Exception -> 0x07ae, all -> 0x0be4 }
    L_0x0714:
        r6 = r14.mediaMuxer;	 Catch:{ Exception -> 0x07ae, all -> 0x0be4 }
        r11 = 0;
        r6 = r6.addTrack(r7, r11);	 Catch:{ Exception -> 0x07ae, all -> 0x0be4 }
        r7 = r6;
        goto L_0x0732;
    L_0x071d:
        r0 = move-exception;
        r13 = r65;
        r5 = r66;
        goto L_0x05b3;
    L_0x0724:
        r13 = r65;
        r36 = r3;
        r27 = r5;
        r35 = r6;
        r3 = r54;
        r20 = 2;
        r5 = r66;
    L_0x0732:
        r6 = r8.flags;	 Catch:{ Exception -> 0x09a2, all -> 0x0be4 }
        r6 = r6 & 4;
        if (r6 == 0) goto L_0x073a;
    L_0x0738:
        r6 = 1;
        goto L_0x073b;
    L_0x073a:
        r6 = 0;
    L_0x073b:
        r11 = 0;
        r15.releaseOutputBuffer(r10, r11);	 Catch:{ Exception -> 0x09a2, all -> 0x0be4 }
        r11 = r6;
        r12 = r7;
        r19 = r35;
        r6 = -1;
    L_0x0744:
        if (r10 == r6) goto L_0x0753;
    L_0x0746:
        r54 = r3;
        r20 = r11;
        r7 = r12;
        r6 = r19;
        r5 = r27;
        r3 = r36;
        goto L_0x05d4;
    L_0x0753:
        if (r47 != 0) goto L_0x09a5;
    L_0x0755:
        r6 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r10 = r4.dequeueOutputBuffer(r8, r6);	 Catch:{ Exception -> 0x09a2, all -> 0x0be4 }
        r6 = -1;
        if (r10 != r6) goto L_0x0788;
    L_0x075e:
        r20 = r3;
        r48 = r11;
        r54 = r12;
        r26 = r27;
        r11 = r28;
        r10 = r36;
        r27 = r44;
        r12 = r56;
        r23 = r57;
        r1 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r9 = 18;
        r16 = 2;
        r30 = 3;
        r40 = 0;
    L_0x077a:
        r42 = -5;
        r44 = 0;
        r56 = 0;
        r58 = 0;
        r59 = 1;
        r28 = r4;
        goto L_0x09c3;
    L_0x0788:
        r7 = -3;
        if (r10 != r7) goto L_0x078d;
    L_0x078b:
        goto L_0x09a5;
    L_0x078d:
        r7 = -2;
        if (r10 != r7) goto L_0x07b1;
    L_0x0790:
        r7 = r4.getOutputFormat();	 Catch:{ Exception -> 0x07ae, all -> 0x0be4 }
        r10 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x07ae, all -> 0x0be4 }
        if (r10 == 0) goto L_0x09a5;
    L_0x0798:
        r10 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x07ae, all -> 0x0be4 }
        r10.<init>();	 Catch:{ Exception -> 0x07ae, all -> 0x0be4 }
        r6 = "newFormat = ";
        r10.append(r6);	 Catch:{ Exception -> 0x07ae, all -> 0x0be4 }
        r10.append(r7);	 Catch:{ Exception -> 0x07ae, all -> 0x0be4 }
        r6 = r10.toString();	 Catch:{ Exception -> 0x07ae, all -> 0x0be4 }
        org.telegram.messenger.FileLog.d(r6);	 Catch:{ Exception -> 0x07ae, all -> 0x0be4 }
        goto L_0x09a5;
    L_0x07ae:
        r0 = move-exception;
        goto L_0x05b3;
    L_0x07b1:
        if (r10 < 0) goto L_0x097f;
    L_0x07b3:
        r6 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x09a2, all -> 0x0be4 }
        r7 = 18;
        if (r6 < r7) goto L_0x07c9;
    L_0x07b9:
        r6 = r8.size;	 Catch:{ Exception -> 0x07ae, all -> 0x0be4 }
        if (r6 == 0) goto L_0x07bf;
    L_0x07bd:
        r6 = 1;
        goto L_0x07c0;
    L_0x07bf:
        r6 = 0;
    L_0x07c0:
        r48 = r11;
        r54 = r12;
        r11 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r33 = 0;
        goto L_0x07e1;
    L_0x07c9:
        r6 = r8.size;	 Catch:{ Exception -> 0x09a2, all -> 0x0be4 }
        if (r6 != 0) goto L_0x07d8;
    L_0x07cd:
        r6 = r8.presentationTimeUs;	 Catch:{ Exception -> 0x07ae, all -> 0x0be4 }
        r33 = 0;
        r37 = (r6 > r33 ? 1 : (r6 == r33 ? 0 : -1));
        if (r37 == 0) goto L_0x07d6;
    L_0x07d5:
        goto L_0x07da;
    L_0x07d6:
        r6 = 0;
        goto L_0x07db;
    L_0x07d8:
        r33 = 0;
    L_0x07da:
        r6 = 1;
    L_0x07db:
        r48 = r11;
        r54 = r12;
        r11 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
    L_0x07e1:
        r7 = (r71 > r33 ? 1 : (r71 == r33 ? 0 : -1));
        if (r7 <= 0) goto L_0x07f9;
    L_0x07e5:
        r11 = r8.presentationTimeUs;	 Catch:{ Exception -> 0x07ae, all -> 0x0be4 }
        r7 = (r11 > r71 ? 1 : (r11 == r71 ? 0 : -1));
        if (r7 < 0) goto L_0x07f9;
    L_0x07eb:
        r6 = r8.flags;	 Catch:{ Exception -> 0x07ae, all -> 0x0be4 }
        r6 = r6 | 4;
        r8.flags = r6;	 Catch:{ Exception -> 0x07ae, all -> 0x0be4 }
        r6 = 0;
        r11 = 0;
        r46 = 1;
        r47 = 1;
        goto L_0x07fb;
    L_0x07f9:
        r11 = 0;
    L_0x07fb:
        r7 = (r1 > r11 ? 1 : (r1 == r11 ? 0 : -1));
        if (r7 <= 0) goto L_0x0833;
    L_0x07ff:
        r33 = -1;
        r7 = (r52 > r33 ? 1 : (r52 == r33 ? 0 : -1));
        if (r7 != 0) goto L_0x0833;
    L_0x0805:
        r11 = r8.presentationTimeUs;	 Catch:{ Exception -> 0x07ae, all -> 0x0be4 }
        r7 = (r11 > r1 ? 1 : (r11 == r1 ? 0 : -1));
        if (r7 >= 0) goto L_0x082f;
    L_0x080b:
        r6 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x07ae, all -> 0x0be4 }
        if (r6 == 0) goto L_0x082d;
    L_0x080f:
        r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x07ae, all -> 0x0be4 }
        r6.<init>();	 Catch:{ Exception -> 0x07ae, all -> 0x0be4 }
        r7 = "drop frame startTime = ";
        r6.append(r7);	 Catch:{ Exception -> 0x07ae, all -> 0x0be4 }
        r6.append(r1);	 Catch:{ Exception -> 0x07ae, all -> 0x0be4 }
        r7 = " present time = ";
        r6.append(r7);	 Catch:{ Exception -> 0x07ae, all -> 0x0be4 }
        r11 = r8.presentationTimeUs;	 Catch:{ Exception -> 0x07ae, all -> 0x0be4 }
        r6.append(r11);	 Catch:{ Exception -> 0x07ae, all -> 0x0be4 }
        r6 = r6.toString();	 Catch:{ Exception -> 0x07ae, all -> 0x0be4 }
        org.telegram.messenger.FileLog.d(r6);	 Catch:{ Exception -> 0x07ae, all -> 0x0be4 }
    L_0x082d:
        r6 = 0;
        goto L_0x0833;
    L_0x082f:
        r11 = r8.presentationTimeUs;	 Catch:{ Exception -> 0x07ae, all -> 0x0be4 }
        r52 = r11;
    L_0x0833:
        r4.releaseOutputBuffer(r10, r6);	 Catch:{ Exception -> 0x09a2, all -> 0x0be4 }
        if (r6 == 0) goto L_0x0927;
    L_0x0838:
        r56.awaitNewImage();	 Catch:{ Exception -> 0x083d, all -> 0x0be4 }
        r6 = 0;
        goto L_0x0843;
    L_0x083d:
        r0 = move-exception;
        r6 = r0;
        org.telegram.messenger.FileLog.e(r6);	 Catch:{ Exception -> 0x09a2, all -> 0x0be4 }
        r6 = 1;
    L_0x0843:
        if (r6 != 0) goto L_0x0927;
    L_0x0845:
        r6 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x09a2, all -> 0x0be4 }
        r7 = 18;
        if (r6 < r7) goto L_0x0892;
    L_0x084b:
        r6 = r56;
        r10 = 0;
        r6.drawImage(r10);	 Catch:{ Exception -> 0x0884, all -> 0x0be4 }
        r11 = r8.presentationTimeUs;	 Catch:{ Exception -> 0x0884, all -> 0x0be4 }
        r37 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r11 = r11 * r37;
        r7 = r44;
        r7.setPresentationTime(r11);	 Catch:{ Exception -> 0x0882, all -> 0x0be4 }
        r7.swapBuffers();	 Catch:{ Exception -> 0x0882, all -> 0x0be4 }
        r20 = r3;
        r12 = r6;
        r40 = r9;
        r26 = r27;
        r11 = r28;
        r10 = r36;
        r23 = r57;
        r9 = 18;
        r16 = 2;
        r30 = 3;
        r42 = -5;
        r44 = 0;
        r56 = 0;
        r58 = 0;
        r59 = 1;
        r28 = r4;
        r27 = r7;
        goto L_0x0949;
    L_0x0882:
        r0 = move-exception;
        goto L_0x0887;
    L_0x0884:
        r0 = move-exception;
        r7 = r44;
    L_0x0887:
        r9 = r67;
        r10 = r68;
        r1 = r0;
        r2 = r6;
        r5 = r7;
        r11 = r28;
        goto L_0x0129;
    L_0x0892:
        r7 = r44;
        r6 = r56;
        r10 = 0;
        r11 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r17 = r15.dequeueInputBuffer(r11);	 Catch:{ Exception -> 0x091f, all -> 0x0be4 }
        if (r17 < 0) goto L_0x08f4;
    L_0x089f:
        r11 = 1;
        r6.drawImage(r11);	 Catch:{ Exception -> 0x091f, all -> 0x0be4 }
        r12 = r6.getFrame();	 Catch:{ Exception -> 0x091f, all -> 0x0be4 }
        r16 = r43[r17];	 Catch:{ Exception -> 0x091f, all -> 0x0be4 }
        r16.clear();	 Catch:{ Exception -> 0x091f, all -> 0x0be4 }
        r23 = r57;
        r2 = -1;
        r42 = -5;
        r44 = 0;
        r1 = r12;
        r12 = r6;
        r6 = -1;
        r56 = 0;
        r2 = r16;
        r16 = r3;
        r10 = r36;
        r58 = 0;
        r3 = r29;
        r11 = r28;
        r59 = 1;
        r28 = r4;
        r4 = r65;
        r40 = r9;
        r26 = r27;
        r9 = 18;
        r30 = 3;
        r27 = r7;
        r7 = r5;
        r5 = r66;
        r20 = r16;
        r16 = 2;
        r6 = r31;
        r7 = r32;
        org.telegram.messenger.Utilities.convertVideoFrame(r1, r2, r3, r4, r5, r6, r7);	 Catch:{ Exception -> 0x0a2d, all -> 0x0be4 }
        r35 = 0;
        r1 = r8.presentationTimeUs;	 Catch:{ Exception -> 0x0a2d, all -> 0x0be4 }
        r39 = 0;
        r33 = r15;
        r34 = r17;
        r36 = r24;
        r37 = r1;
        r33.queueInputBuffer(r34, r35, r36, r37, r39);	 Catch:{ Exception -> 0x0a2d, all -> 0x0be4 }
        goto L_0x0949;
    L_0x08f4:
        r20 = r3;
        r12 = r6;
        r40 = r9;
        r26 = r27;
        r11 = r28;
        r10 = r36;
        r23 = r57;
        r9 = 18;
        r16 = 2;
        r30 = 3;
        r42 = -5;
        r44 = 0;
        r56 = 0;
        r58 = 0;
        r59 = 1;
        r28 = r4;
        r27 = r7;
        r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x0a2d, all -> 0x0be4 }
        if (r1 == 0) goto L_0x0949;
    L_0x0919:
        r1 = "input buffer not available";
        org.telegram.messenger.FileLog.d(r1);	 Catch:{ Exception -> 0x0a2d, all -> 0x0be4 }
        goto L_0x0949;
    L_0x091f:
        r0 = move-exception;
        r12 = r6;
        r27 = r7;
        r11 = r28;
        goto L_0x0a43;
    L_0x0927:
        r20 = r3;
        r40 = r9;
        r26 = r27;
        r11 = r28;
        r10 = r36;
        r27 = r44;
        r12 = r56;
        r23 = r57;
        r9 = 18;
        r16 = 2;
        r30 = 3;
        r42 = -5;
        r44 = 0;
        r56 = 0;
        r58 = 0;
        r59 = 1;
        r28 = r4;
    L_0x0949:
        r1 = r8.flags;	 Catch:{ Exception -> 0x0a2d, all -> 0x0be4 }
        r1 = r1 & 4;
        if (r1 == 0) goto L_0x097c;
    L_0x094f:
        r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x0a2d, all -> 0x0be4 }
        if (r1 == 0) goto L_0x0958;
    L_0x0953:
        r1 = "decoder stream end";
        org.telegram.messenger.FileLog.d(r1);	 Catch:{ Exception -> 0x0a2d, all -> 0x0be4 }
    L_0x0958:
        r1 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0a2d, all -> 0x0be4 }
        if (r1 < r9) goto L_0x0962;
    L_0x095c:
        r15.signalEndOfInputStream();	 Catch:{ Exception -> 0x0a2d, all -> 0x0be4 }
        r1 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        goto L_0x0979;
    L_0x0962:
        r1 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r36 = r15.dequeueInputBuffer(r1);	 Catch:{ Exception -> 0x0a2d, all -> 0x0be4 }
        if (r36 < 0) goto L_0x0979;
    L_0x096a:
        r37 = 0;
        r38 = 1;
        r3 = r8.presentationTimeUs;	 Catch:{ Exception -> 0x0a2d, all -> 0x0be4 }
        r41 = 4;
        r35 = r15;
        r39 = r3;
        r35.queueInputBuffer(r36, r37, r38, r39, r41);	 Catch:{ Exception -> 0x0a2d, all -> 0x0be4 }
    L_0x0979:
        r40 = 0;
        goto L_0x09c3;
    L_0x097c:
        r1 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        goto L_0x09c3;
    L_0x097f:
        r11 = r28;
        r27 = r44;
        r12 = r56;
        r58 = 0;
        r59 = 1;
        r28 = r4;
        r1 = new java.lang.RuntimeException;	 Catch:{ Exception -> 0x0a2d, all -> 0x0be4 }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0a2d, all -> 0x0be4 }
        r2.<init>();	 Catch:{ Exception -> 0x0a2d, all -> 0x0be4 }
        r3 = "unexpected result from decoder.dequeueOutputBuffer: ";
        r2.append(r3);	 Catch:{ Exception -> 0x0a2d, all -> 0x0be4 }
        r2.append(r10);	 Catch:{ Exception -> 0x0a2d, all -> 0x0be4 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0a2d, all -> 0x0be4 }
        r1.<init>(r2);	 Catch:{ Exception -> 0x0a2d, all -> 0x0be4 }
        throw r1;	 Catch:{ Exception -> 0x0a2d, all -> 0x0be4 }
    L_0x09a2:
        r0 = move-exception;
        goto L_0x0a3d;
    L_0x09a5:
        r20 = r3;
        r40 = r9;
        r48 = r11;
        r54 = r12;
        r26 = r27;
        r11 = r28;
        r10 = r36;
        r27 = r44;
        r12 = r56;
        r23 = r57;
        r1 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r9 = 18;
        r16 = 2;
        r30 = 3;
        goto L_0x077a;
    L_0x09c3:
        r1 = r69;
        r3 = r10;
        r56 = r12;
        r6 = r19;
        r57 = r23;
        r5 = r26;
        r44 = r27;
        r4 = r28;
        r9 = r40;
        r7 = r54;
        r28 = r11;
        r54 = r20;
        r20 = r48;
        goto L_0x05d4;
    L_0x09de:
        r13 = r65;
        r11 = r28;
        r27 = r44;
        r12 = r56;
        r58 = 0;
        r59 = 1;
        r28 = r4;
        r1 = new java.lang.RuntimeException;	 Catch:{ Exception -> 0x0a2d, all -> 0x0be4 }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0a2d, all -> 0x0be4 }
        r2.<init>();	 Catch:{ Exception -> 0x0a2d, all -> 0x0be4 }
        r3 = "encoderOutputBuffer ";
        r2.append(r3);	 Catch:{ Exception -> 0x0a2d, all -> 0x0be4 }
        r2.append(r10);	 Catch:{ Exception -> 0x0a2d, all -> 0x0be4 }
        r3 = " was null";
        r2.append(r3);	 Catch:{ Exception -> 0x0a2d, all -> 0x0be4 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0a2d, all -> 0x0be4 }
        r1.<init>(r2);	 Catch:{ Exception -> 0x0a2d, all -> 0x0be4 }
        throw r1;	 Catch:{ Exception -> 0x0a2d, all -> 0x0be4 }
    L_0x0a08:
        r13 = r65;
        r11 = r28;
        r27 = r44;
        r12 = r56;
        r58 = 0;
        r59 = 1;
        r28 = r4;
        r1 = new java.lang.RuntimeException;	 Catch:{ Exception -> 0x0a2d, all -> 0x0be4 }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0a2d, all -> 0x0be4 }
        r2.<init>();	 Catch:{ Exception -> 0x0a2d, all -> 0x0be4 }
        r3 = "unexpected result from encoder.dequeueOutputBuffer: ";
        r2.append(r3);	 Catch:{ Exception -> 0x0a2d, all -> 0x0be4 }
        r2.append(r10);	 Catch:{ Exception -> 0x0a2d, all -> 0x0be4 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0a2d, all -> 0x0be4 }
        r1.<init>(r2);	 Catch:{ Exception -> 0x0a2d, all -> 0x0be4 }
        throw r1;	 Catch:{ Exception -> 0x0a2d, all -> 0x0be4 }
    L_0x0a2d:
        r0 = move-exception;
        r9 = r67;
        r10 = r68;
        r1 = r0;
        r2 = r12;
        r5 = r27;
        r4 = r28;
        goto L_0x0b15;
    L_0x0a3a:
        r0 = move-exception;
        r13 = r65;
    L_0x0a3d:
        r11 = r28;
        r27 = r44;
        r12 = r56;
    L_0x0a43:
        r58 = 0;
        r59 = 1;
        r28 = r4;
        r9 = r67;
        r10 = r68;
        r1 = r0;
        r2 = r12;
        goto L_0x0a65;
    L_0x0a50:
        r0 = move-exception;
        r13 = r65;
        r12 = r2;
        r55 = r10;
        r11 = r28;
        r27 = r44;
        r58 = 0;
        r59 = 1;
        r28 = r4;
        r9 = r67;
        r10 = r68;
        r1 = r0;
    L_0x0a65:
        r5 = r27;
        goto L_0x0b15;
    L_0x0a69:
        r0 = move-exception;
        r27 = r5;
        r44 = r9;
        r13 = r12;
        r11 = r28;
        r58 = 0;
        r59 = 1;
        r12 = r2;
        r28 = r4;
        r9 = r67;
        r10 = r68;
        goto L_0x0a90;
    L_0x0a7d:
        r0 = move-exception;
        r27 = r5;
        r67 = r9;
        r68 = r10;
        r13 = r12;
        r11 = r28;
        r44 = 0;
        r58 = 0;
        r59 = 1;
        r12 = r2;
        r28 = r4;
    L_0x0a90:
        r1 = r0;
        r55 = r44;
        goto L_0x0b15;
    L_0x0a95:
        r0 = move-exception;
        r27 = r5;
        r67 = r9;
        r68 = r10;
        r13 = r12;
        r11 = r28;
        r44 = 0;
        r58 = 0;
        r59 = 1;
        r28 = r4;
        r1 = r0;
        r2 = r44;
        r55 = r2;
        goto L_0x0b15;
    L_0x0aae:
        r0 = move-exception;
        r27 = r5;
        r67 = r9;
        r68 = r10;
        r13 = r12;
        r11 = r28;
        r44 = 0;
        r58 = 0;
        r59 = 1;
        r1 = r0;
        r2 = r44;
        r4 = r2;
        r55 = r4;
        goto L_0x0b15;
    L_0x0ac6:
        r0 = move-exception;
        r67 = r9;
        r68 = r10;
        r13 = r12;
        r11 = r28;
        r44 = 0;
        r58 = 0;
        r59 = 1;
        r1 = r0;
        r2 = r44;
        goto L_0x0ae7;
    L_0x0ad8:
        r0 = move-exception;
        r44 = r2;
        r67 = r9;
        r68 = r10;
        r13 = r12;
        r11 = r28;
        r58 = 0;
        r59 = 1;
        r1 = r0;
    L_0x0ae7:
        r4 = r2;
        r5 = r4;
        r55 = r5;
        goto L_0x0b15;
    L_0x0aec:
        r0 = move-exception;
        r67 = r9;
        r68 = r10;
        goto L_0x0b00;
    L_0x0af2:
        r0 = move-exception;
        r13 = r12;
        r11 = r28;
        r44 = 0;
        r58 = 0;
        r59 = 1;
        r1 = r0;
        r10 = r2;
        goto L_0x0b0e;
    L_0x0aff:
        r0 = move-exception;
    L_0x0b00:
        r13 = r12;
        r11 = r28;
        goto L_0x0b07;
    L_0x0b04:
        r0 = move-exception;
        r11 = r4;
        r13 = r12;
    L_0x0b07:
        r44 = 0;
        r58 = 0;
        r59 = 1;
        r1 = r0;
    L_0x0b0e:
        r2 = r44;
        r4 = r2;
        r5 = r4;
        r15 = r5;
        r55 = r15;
    L_0x0b15:
        r3 = r1 instanceof java.lang.IllegalStateException;	 Catch:{ Exception -> 0x0b85, all -> 0x0be4 }
        if (r3 == 0) goto L_0x0b1d;
    L_0x0b19:
        if (r76 != 0) goto L_0x0b1d;
    L_0x0b1b:
        r3 = 1;
        goto L_0x0b1e;
    L_0x0b1d:
        r3 = 0;
    L_0x0b1e:
        r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0b7f, all -> 0x0be4 }
        r6.<init>();	 Catch:{ Exception -> 0x0b7f, all -> 0x0be4 }
        r7 = "bitrate: ";
        r6.append(r7);	 Catch:{ Exception -> 0x0b7f, all -> 0x0be4 }
        r6.append(r10);	 Catch:{ Exception -> 0x0b7f, all -> 0x0be4 }
        r7 = " framerate: ";
        r6.append(r7);	 Catch:{ Exception -> 0x0b7f, all -> 0x0be4 }
        r6.append(r9);	 Catch:{ Exception -> 0x0b7f, all -> 0x0be4 }
        r7 = " size: ";
        r6.append(r7);	 Catch:{ Exception -> 0x0b7f, all -> 0x0be4 }
        r12 = r66;
        r6.append(r12);	 Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }
        r7 = "x";
        r6.append(r7);	 Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }
        r6.append(r13);	 Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }
        r6 = r6.toString();	 Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }
        org.telegram.messenger.FileLog.e(r6);	 Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }
        org.telegram.messenger.FileLog.e(r1);	 Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }
        r17 = 1;
    L_0x0b52:
        r1 = r14.extractor;	 Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }
        r1.unselectTrack(r11);	 Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }
        if (r2 == 0) goto L_0x0b5c;
    L_0x0b59:
        r2.release();	 Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }
    L_0x0b5c:
        if (r5 == 0) goto L_0x0b61;
    L_0x0b5e:
        r5.release();	 Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }
    L_0x0b61:
        if (r4 == 0) goto L_0x0b69;
    L_0x0b63:
        r4.stop();	 Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }
        r4.release();	 Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }
    L_0x0b69:
        if (r15 == 0) goto L_0x0b71;
    L_0x0b6b:
        r15.stop();	 Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }
        r15.release();	 Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }
    L_0x0b71:
        if (r55 == 0) goto L_0x0b76;
    L_0x0b73:
        r55.release();	 Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }
    L_0x0b76:
        r60.checkConversionCanceled();	 Catch:{ Exception -> 0x0b7a, all -> 0x0be4 }
        goto L_0x0b91;
    L_0x0b7a:
        r0 = move-exception;
        r1 = r0;
        r13 = r12;
        goto L_0x0bf5;
    L_0x0b7f:
        r0 = move-exception;
        r13 = r66;
        r1 = r0;
        goto L_0x0bf5;
    L_0x0b85:
        r0 = move-exception;
        r13 = r66;
        goto L_0x0bf3;
    L_0x0b8a:
        r13 = r12;
        r58 = 0;
        r12 = r11;
        r3 = 0;
        r17 = 0;
    L_0x0b91:
        r13 = r12;
        r4 = r17;
        goto L_0x0bc6;
    L_0x0b95:
        r0 = move-exception;
        r13 = r12;
        r58 = 0;
        r59 = 1;
        r1 = r0;
        r13 = r11;
        goto L_0x0bf4;
    L_0x0b9f:
        r8 = r2;
        r13 = r12;
        r58 = 0;
        r59 = 1;
        r12 = r11;
        r2 = r14.extractor;	 Catch:{ Exception -> 0x0be1, all -> 0x0be4 }
        r3 = r14.mediaMuxer;	 Catch:{ Exception -> 0x0be1, all -> 0x0be4 }
        r1 = -1;
        if (r10 == r1) goto L_0x0baf;
    L_0x0bad:
        r15 = 1;
        goto L_0x0bb0;
    L_0x0baf:
        r15 = 0;
    L_0x0bb0:
        r1 = r60;
        r4 = r8;
        r5 = r69;
        r7 = r71;
        r9 = r73;
        r11 = r62;
        r13 = r12;
        r12 = r15;
        r1.readAndWriteTracks(r2, r3, r4, r5, r7, r9, r11, r12);	 Catch:{ Exception -> 0x0bdf, all -> 0x0be4 }
        r9 = r67;
        r10 = r68;
        r3 = 0;
        r4 = 0;
    L_0x0bc6:
        r1 = r14.extractor;
        if (r1 == 0) goto L_0x0bcd;
    L_0x0bca:
        r1.release();
    L_0x0bcd:
        r1 = r14.mediaMuxer;
        if (r1 == 0) goto L_0x0bda;
    L_0x0bd1:
        r1.finishMovie();	 Catch:{ Exception -> 0x0bd5 }
        goto L_0x0bda;
    L_0x0bd5:
        r0 = move-exception;
        r1 = r0;
        org.telegram.messenger.FileLog.e(r1);
    L_0x0bda:
        r6 = r65;
        r8 = r9;
        r9 = r10;
        goto L_0x0c3e;
    L_0x0bdf:
        r0 = move-exception;
        goto L_0x0bef;
    L_0x0be1:
        r0 = move-exception;
        r13 = r12;
        goto L_0x0bef;
    L_0x0be4:
        r0 = move-exception;
        r2 = r0;
        r1 = r14;
        goto L_0x0CLASSNAME;
    L_0x0be9:
        r0 = move-exception;
        r13 = r11;
        r58 = 0;
        r59 = 1;
    L_0x0bef:
        r9 = r67;
        r10 = r68;
    L_0x0bf3:
        r1 = r0;
    L_0x0bf4:
        r3 = 0;
    L_0x0bf5:
        r2 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0c5e }
        r2.<init>();	 Catch:{ all -> 0x0c5e }
        r4 = "bitrate: ";
        r2.append(r4);	 Catch:{ all -> 0x0c5e }
        r2.append(r10);	 Catch:{ all -> 0x0c5e }
        r4 = " framerate: ";
        r2.append(r4);	 Catch:{ all -> 0x0c5e }
        r2.append(r9);	 Catch:{ all -> 0x0c5e }
        r4 = " size: ";
        r2.append(r4);	 Catch:{ all -> 0x0c5e }
        r2.append(r13);	 Catch:{ all -> 0x0c5e }
        r4 = "x";
        r2.append(r4);	 Catch:{ all -> 0x0c5e }
        r6 = r65;
        r2.append(r6);	 Catch:{ all -> 0x0c5e }
        r2 = r2.toString();	 Catch:{ all -> 0x0c5e }
        org.telegram.messenger.FileLog.e(r2);	 Catch:{ all -> 0x0c5e }
        org.telegram.messenger.FileLog.e(r1);	 Catch:{ all -> 0x0c5e }
        r1 = r14.extractor;
        if (r1 == 0) goto L_0x0c2e;
    L_0x0c2b:
        r1.release();
    L_0x0c2e:
        r1 = r14.mediaMuxer;
        if (r1 == 0) goto L_0x0c3b;
    L_0x0CLASSNAME:
        r1.finishMovie();	 Catch:{ Exception -> 0x0CLASSNAME }
        goto L_0x0c3b;
    L_0x0CLASSNAME:
        r0 = move-exception;
        r1 = r0;
        org.telegram.messenger.FileLog.e(r1);
    L_0x0c3b:
        r8 = r9;
        r9 = r10;
        r4 = 1;
    L_0x0c3e:
        if (r3 == 0) goto L_0x0c5d;
    L_0x0CLASSNAME:
        r17 = 1;
        r1 = r60;
        r2 = r61;
        r3 = r62;
        r4 = r63;
        r5 = r64;
        r6 = r65;
        r7 = r66;
        r10 = r69;
        r12 = r71;
        r14 = r73;
        r16 = r75;
        r1 = r1.convertVideoInternal(r2, r3, r4, r5, r6, r7, r8, r9, r10, r12, r14, r16, r17);
        return r1;
    L_0x0c5d:
        return r4;
    L_0x0c5e:
        r0 = move-exception;
        r1 = r60;
        r2 = r0;
    L_0x0CLASSNAME:
        r3 = r1.extractor;
        if (r3 == 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r3.release();
    L_0x0CLASSNAME:
        r3 = r1.mediaMuxer;
        if (r3 == 0) goto L_0x0CLASSNAME;
    L_0x0c6d:
        r3.finishMovie();	 Catch:{ Exception -> 0x0CLASSNAME }
        goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r0 = move-exception;
        r3 = r0;
        org.telegram.messenger.FileLog.e(r3);
    L_0x0CLASSNAME:
        goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        throw r2;
    L_0x0CLASSNAME:
        goto L_0x0CLASSNAME;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.video.MediaCodecVideoConvertor.convertVideoInternal(java.lang.String, java.io.File, int, boolean, int, int, int, int, long, long, long, boolean, boolean):boolean");
    }

    /* JADX WARNING: Removed duplicated region for block: B:91:0x0183  */
    /* JADX WARNING: Removed duplicated region for block: B:91:0x0183  */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x0125  */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x011d  */
    /* JADX WARNING: Removed duplicated region for block: B:86:0x016d  */
    /* JADX WARNING: Removed duplicated region for block: B:91:0x0183  */
    /* JADX WARNING: Removed duplicated region for block: B:93:0x0188  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x009a  */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x019d  */
    /* JADX WARNING: Removed duplicated region for block: B:98:0x019b  */
    /* JADX WARNING: Missing block: B:48:0x00dc, code skipped:
            if (r8[r9 + 3] != (byte) 1) goto L_0x00e2;
     */
    private long readAndWriteTracks(android.media.MediaExtractor r30, org.telegram.messenger.video.MP4Builder r31, android.media.MediaCodec.BufferInfo r32, long r33, long r35, long r37, java.io.File r39, boolean r40) throws java.lang.Exception {
        /*
        r29 = this;
        r0 = r29;
        r1 = r30;
        r2 = r31;
        r3 = r32;
        r4 = r33;
        r6 = 0;
        r7 = org.telegram.messenger.MediaController.findTrack(r1, r6);
        r9 = 1;
        if (r40 == 0) goto L_0x001a;
    L_0x0012:
        r10 = org.telegram.messenger.MediaController.findTrack(r1, r9);
        r12 = r10;
        r10 = r37;
        goto L_0x001d;
    L_0x001a:
        r10 = r37;
        r12 = -1;
    L_0x001d:
        r10 = (float) r10;
        r11 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r10 = r10 / r11;
        r13 = "max-input-size";
        r14 = 0;
        if (r7 < 0) goto L_0x0042;
    L_0x0027:
        r1.selectTrack(r7);
        r11 = r1.getTrackFormat(r7);
        r16 = r2.addTrack(r11, r6);
        r11 = r11.getInteger(r13);
        r17 = (r4 > r14 ? 1 : (r4 == r14 ? 0 : -1));
        if (r17 <= 0) goto L_0x003e;
    L_0x003a:
        r1.seekTo(r4, r6);
        goto L_0x0045;
    L_0x003e:
        r1.seekTo(r14, r6);
        goto L_0x0045;
    L_0x0042:
        r11 = 0;
        r16 = -1;
    L_0x0045:
        if (r12 < 0) goto L_0x0068;
    L_0x0047:
        r1.selectTrack(r12);
        r8 = r1.getTrackFormat(r12);
        r17 = r2.addTrack(r8, r9);
        r8 = r8.getInteger(r13);
        r11 = java.lang.Math.max(r8, r11);
        r8 = (r4 > r14 ? 1 : (r4 == r14 ? 0 : -1));
        if (r8 <= 0) goto L_0x0062;
    L_0x005e:
        r1.seekTo(r4, r6);
        goto L_0x0065;
    L_0x0062:
        r1.seekTo(r14, r6);
    L_0x0065:
        r8 = r17;
        goto L_0x0069;
    L_0x0068:
        r8 = -1;
    L_0x0069:
        r11 = java.nio.ByteBuffer.allocateDirect(r11);
        r17 = -1;
        if (r12 >= 0) goto L_0x0075;
    L_0x0071:
        if (r7 < 0) goto L_0x0074;
    L_0x0073:
        goto L_0x0075;
    L_0x0074:
        return r17;
    L_0x0075:
        r29.checkConversionCanceled();
        r21 = r14;
        r19 = r17;
        r13 = 0;
    L_0x007d:
        if (r13 != 0) goto L_0x01ac;
    L_0x007f:
        r29.checkConversionCanceled();
        r14 = r1.readSampleData(r11, r6);
        r3.size = r14;
        r14 = r30.getSampleTrackIndex();
        if (r14 != r7) goto L_0x0092;
    L_0x008e:
        r15 = r16;
    L_0x0090:
        r9 = -1;
        goto L_0x0098;
    L_0x0092:
        if (r14 != r12) goto L_0x0096;
    L_0x0094:
        r15 = r8;
        goto L_0x0090;
    L_0x0096:
        r9 = -1;
        r15 = -1;
    L_0x0098:
        if (r15 == r9) goto L_0x0188;
    L_0x009a:
        r9 = android.os.Build.VERSION.SDK_INT;
        r38 = r8;
        r8 = 21;
        if (r9 >= r8) goto L_0x00aa;
    L_0x00a2:
        r11.position(r6);
        r8 = r3.size;
        r11.limit(r8);
    L_0x00aa:
        if (r14 == r12) goto L_0x0115;
    L_0x00ac:
        r8 = r11.array();
        if (r8 == 0) goto L_0x0115;
    L_0x00b2:
        r9 = r11.arrayOffset();
        r25 = r11.limit();
        r25 = r9 + r25;
        r6 = -1;
    L_0x00bd:
        r26 = 4;
        r40 = r13;
        r13 = r25 + -4;
        if (r9 > r13) goto L_0x0112;
    L_0x00c5:
        r27 = r8[r9];
        if (r27 != 0) goto L_0x00df;
    L_0x00c9:
        r27 = r9 + 1;
        r27 = r8[r27];
        if (r27 != 0) goto L_0x00df;
    L_0x00cf:
        r27 = r9 + 2;
        r27 = r8[r27];
        if (r27 != 0) goto L_0x00df;
    L_0x00d5:
        r27 = r9 + 3;
        r28 = r12;
        r12 = r8[r27];
        r1 = 1;
        if (r12 == r1) goto L_0x00e4;
    L_0x00de:
        goto L_0x00e2;
    L_0x00df:
        r28 = r12;
        r1 = 1;
    L_0x00e2:
        if (r9 != r13) goto L_0x0109;
    L_0x00e4:
        r12 = -1;
        if (r6 == r12) goto L_0x0108;
    L_0x00e7:
        r12 = r9 - r6;
        if (r9 == r13) goto L_0x00ec;
    L_0x00eb:
        goto L_0x00ee;
    L_0x00ec:
        r26 = 0;
    L_0x00ee:
        r12 = r12 - r26;
        r13 = r12 >> 24;
        r13 = (byte) r13;
        r8[r6] = r13;
        r13 = r6 + 1;
        r1 = r12 >> 16;
        r1 = (byte) r1;
        r8[r13] = r1;
        r1 = r6 + 2;
        r13 = r12 >> 8;
        r13 = (byte) r13;
        r8[r1] = r13;
        r6 = r6 + 3;
        r1 = (byte) r12;
        r8[r6] = r1;
    L_0x0108:
        r6 = r9;
    L_0x0109:
        r9 = r9 + 1;
        r1 = r30;
        r13 = r40;
        r12 = r28;
        goto L_0x00bd;
    L_0x0112:
        r28 = r12;
        goto L_0x0119;
    L_0x0115:
        r28 = r12;
        r40 = r13;
    L_0x0119:
        r1 = r3.size;
        if (r1 < 0) goto L_0x0125;
    L_0x011d:
        r8 = r30.getSampleTime();
        r3.presentationTimeUs = r8;
        r1 = 0;
        goto L_0x0129;
    L_0x0125:
        r1 = 0;
        r3.size = r1;
        r1 = 1;
    L_0x0129:
        r6 = r3.size;
        if (r6 <= 0) goto L_0x017e;
    L_0x012d:
        if (r1 != 0) goto L_0x017e;
    L_0x012f:
        r8 = 0;
        if (r14 != r7) goto L_0x013f;
    L_0x0133:
        r6 = (r4 > r8 ? 1 : (r4 == r8 ? 0 : -1));
        if (r6 <= 0) goto L_0x013f;
    L_0x0137:
        r6 = (r19 > r17 ? 1 : (r19 == r17 ? 0 : -1));
        if (r6 != 0) goto L_0x013f;
    L_0x013b:
        r12 = r3.presentationTimeUs;
        r19 = r12;
    L_0x013f:
        r6 = (r35 > r8 ? 1 : (r35 == r8 ? 0 : -1));
        if (r6 < 0) goto L_0x014e;
    L_0x0143:
        r8 = r3.presentationTimeUs;
        r6 = (r8 > r35 ? 1 : (r8 == r35 ? 0 : -1));
        if (r6 >= 0) goto L_0x014a;
    L_0x0149:
        goto L_0x014e;
    L_0x014a:
        r6 = 1;
        r13 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        goto L_0x0181;
    L_0x014e:
        r6 = 0;
        r3.offset = r6;
        r8 = r30.getSampleFlags();
        r3.flags = r8;
        r8 = r2.writeSampleData(r15, r11, r3, r6);
        r12 = 0;
        r14 = (r8 > r12 ? 1 : (r8 == r12 ? 0 : -1));
        if (r14 == 0) goto L_0x017e;
    L_0x0161:
        r14 = r0.callback;
        if (r14 == 0) goto L_0x017e;
    L_0x0165:
        r14 = r3.presentationTimeUs;
        r23 = r14 - r19;
        r25 = (r23 > r21 ? 1 : (r23 == r21 ? 0 : -1));
        if (r25 <= 0) goto L_0x016f;
    L_0x016d:
        r21 = r14 - r19;
    L_0x016f:
        r14 = r21;
        r6 = r0.callback;
        r12 = (float) r14;
        r13 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r12 = r12 / r13;
        r12 = r12 / r10;
        r6.didWriteData(r8, r12);
        r21 = r14;
        goto L_0x0180;
    L_0x017e:
        r13 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
    L_0x0180:
        r6 = r1;
    L_0x0181:
        if (r6 != 0) goto L_0x0186;
    L_0x0183:
        r30.advance();
    L_0x0186:
        r1 = -1;
        goto L_0x0199;
    L_0x0188:
        r38 = r8;
        r28 = r12;
        r40 = r13;
        r1 = -1;
        r13 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        if (r14 != r1) goto L_0x0195;
    L_0x0193:
        r6 = 1;
        goto L_0x0199;
    L_0x0195:
        r30.advance();
        r6 = 0;
    L_0x0199:
        if (r6 == 0) goto L_0x019d;
    L_0x019b:
        r6 = 1;
        goto L_0x019f;
    L_0x019d:
        r6 = r40;
    L_0x019f:
        r1 = r30;
        r8 = r38;
        r13 = r6;
        r12 = r28;
        r6 = 0;
        r9 = 1;
        r14 = 0;
        goto L_0x007d;
    L_0x01ac:
        r28 = r12;
        r1 = r30;
        if (r7 < 0) goto L_0x01b5;
    L_0x01b2:
        r1.unselectTrack(r7);
    L_0x01b5:
        if (r28 < 0) goto L_0x01bc;
    L_0x01b7:
        r8 = r28;
        r1.unselectTrack(r8);
    L_0x01bc:
        return r19;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.video.MediaCodecVideoConvertor.readAndWriteTracks(android.media.MediaExtractor, org.telegram.messenger.video.MP4Builder, android.media.MediaCodec$BufferInfo, long, long, long, java.io.File, boolean):long");
    }

    private void checkConversionCanceled() {
        VideoConvertorListener videoConvertorListener = this.callback;
        if (videoConvertorListener != null && videoConvertorListener.checkConversionCanceled()) {
            throw new RuntimeException("canceled conversion");
        }
    }
}
