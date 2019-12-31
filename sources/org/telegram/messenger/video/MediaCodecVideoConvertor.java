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

    /* JADX WARNING: Unknown top exception splitter block from list: {B:156:0x02a3=Splitter:B:156:0x02a3, B:136:0x0266=Splitter:B:136:0x0266} */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x010f A:{Catch:{ Exception -> 0x0120, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x00e0 A:{Catch:{ Exception -> 0x0120, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x00e0 A:{Catch:{ Exception -> 0x0120, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x010f A:{Catch:{ Exception -> 0x0120, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:332:0x0590 A:{Catch:{ Exception -> 0x05ad, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:592:0x0a3f A:{Catch:{ Exception -> 0x0aeb, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:582:0x0a12 A:{Catch:{ Exception -> 0x0aeb, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:582:0x0a12 A:{Catch:{ Exception -> 0x0aeb, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:592:0x0a3f A:{Catch:{ Exception -> 0x0aeb, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:480:0x07f9 A:{Catch:{ Exception -> 0x0a66, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:478:0x07ef A:{Catch:{ Exception -> 0x0a66, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x040c  */
    /* JADX WARNING: Removed duplicated region for block: B:217:0x0354 A:{SYNTHETIC, Splitter:B:217:0x0354} */
    /* JADX WARNING: Removed duplicated region for block: B:261:0x0437 A:{Catch:{ Exception -> 0x0b10, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:345:0x05cc  */
    /* JADX WARNING: Removed duplicated region for block: B:278:0x0473 A:{SYNTHETIC, Splitter:B:278:0x0473} */
    /* JADX WARNING: Removed duplicated region for block: B:354:0x0603 A:{Catch:{ Exception -> 0x0af4, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:353:0x0600 A:{Catch:{ Exception -> 0x0af4, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:360:0x061b  */
    /* JADX WARNING: Removed duplicated region for block: B:358:0x060c  */
    /* JADX WARNING: Removed duplicated region for block: B:478:0x07ef A:{Catch:{ Exception -> 0x0a66, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:480:0x07f9 A:{Catch:{ Exception -> 0x0a66, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:649:0x0c1a A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:651:0x0c1f A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:653:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:655:0x0c2c A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x01c8 A:{SYNTHETIC, Splitter:B:97:0x01c8} */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x01bf A:{SYNTHETIC, Splitter:B:94:0x01bf} */
    /* JADX WARNING: Removed duplicated region for block: B:104:0x01d8  */
    /* JADX WARNING: Removed duplicated region for block: B:103:0x01d4  */
    /* JADX WARNING: Removed duplicated region for block: B:148:0x0291  */
    /* JADX WARNING: Removed duplicated region for block: B:109:0x01f8 A:{SYNTHETIC, Splitter:B:109:0x01f8} */
    /* JADX WARNING: Removed duplicated region for block: B:163:0x02af A:{SYNTHETIC, Splitter:B:163:0x02af} */
    /* JADX WARNING: Removed duplicated region for block: B:186:0x02f0  */
    /* JADX WARNING: Removed duplicated region for block: B:176:0x02ce A:{SYNTHETIC, Splitter:B:176:0x02ce} */
    /* JADX WARNING: Removed duplicated region for block: B:197:0x0311 A:{SYNTHETIC, Splitter:B:197:0x0311} */
    /* JADX WARNING: Removed duplicated region for block: B:193:0x0304 A:{SYNTHETIC, Splitter:B:193:0x0304} */
    /* JADX WARNING: Removed duplicated region for block: B:214:0x034f  */
    /* JADX WARNING: Removed duplicated region for block: B:206:0x032e A:{SYNTHETIC, Splitter:B:206:0x032e} */
    /* JADX WARNING: Removed duplicated region for block: B:217:0x0354 A:{SYNTHETIC, Splitter:B:217:0x0354} */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x040c  */
    /* JADX WARNING: Removed duplicated region for block: B:261:0x0437 A:{Catch:{ Exception -> 0x0b10, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:267:0x0453 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:278:0x0473 A:{SYNTHETIC, Splitter:B:278:0x0473} */
    /* JADX WARNING: Removed duplicated region for block: B:345:0x05cc  */
    /* JADX WARNING: Removed duplicated region for block: B:348:0x05e1 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:353:0x0600 A:{Catch:{ Exception -> 0x0af4, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:354:0x0603 A:{Catch:{ Exception -> 0x0af4, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:358:0x060c  */
    /* JADX WARNING: Removed duplicated region for block: B:360:0x061b  */
    /* JADX WARNING: Removed duplicated region for block: B:480:0x07f9 A:{Catch:{ Exception -> 0x0a66, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:478:0x07ef A:{Catch:{ Exception -> 0x0a66, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:649:0x0c1a A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:651:0x0c1f A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:653:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:655:0x0c2c A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x01bf A:{SYNTHETIC, Splitter:B:94:0x01bf} */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x01c8 A:{SYNTHETIC, Splitter:B:97:0x01c8} */
    /* JADX WARNING: Removed duplicated region for block: B:103:0x01d4  */
    /* JADX WARNING: Removed duplicated region for block: B:104:0x01d8  */
    /* JADX WARNING: Removed duplicated region for block: B:109:0x01f8 A:{SYNTHETIC, Splitter:B:109:0x01f8} */
    /* JADX WARNING: Removed duplicated region for block: B:148:0x0291  */
    /* JADX WARNING: Removed duplicated region for block: B:163:0x02af A:{SYNTHETIC, Splitter:B:163:0x02af} */
    /* JADX WARNING: Removed duplicated region for block: B:176:0x02ce A:{SYNTHETIC, Splitter:B:176:0x02ce} */
    /* JADX WARNING: Removed duplicated region for block: B:186:0x02f0  */
    /* JADX WARNING: Removed duplicated region for block: B:193:0x0304 A:{SYNTHETIC, Splitter:B:193:0x0304} */
    /* JADX WARNING: Removed duplicated region for block: B:197:0x0311 A:{SYNTHETIC, Splitter:B:197:0x0311} */
    /* JADX WARNING: Removed duplicated region for block: B:206:0x032e A:{SYNTHETIC, Splitter:B:206:0x032e} */
    /* JADX WARNING: Removed duplicated region for block: B:214:0x034f  */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x040c  */
    /* JADX WARNING: Removed duplicated region for block: B:217:0x0354 A:{SYNTHETIC, Splitter:B:217:0x0354} */
    /* JADX WARNING: Removed duplicated region for block: B:261:0x0437 A:{Catch:{ Exception -> 0x0b10, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:267:0x0453 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:345:0x05cc  */
    /* JADX WARNING: Removed duplicated region for block: B:278:0x0473 A:{SYNTHETIC, Splitter:B:278:0x0473} */
    /* JADX WARNING: Removed duplicated region for block: B:348:0x05e1 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:354:0x0603 A:{Catch:{ Exception -> 0x0af4, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:353:0x0600 A:{Catch:{ Exception -> 0x0af4, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:360:0x061b  */
    /* JADX WARNING: Removed duplicated region for block: B:358:0x060c  */
    /* JADX WARNING: Removed duplicated region for block: B:478:0x07ef A:{Catch:{ Exception -> 0x0a66, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:480:0x07f9 A:{Catch:{ Exception -> 0x0a66, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:649:0x0c1a A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:651:0x0c1f A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:653:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:655:0x0c2c A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x01c8 A:{SYNTHETIC, Splitter:B:97:0x01c8} */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x01bf A:{SYNTHETIC, Splitter:B:94:0x01bf} */
    /* JADX WARNING: Removed duplicated region for block: B:104:0x01d8  */
    /* JADX WARNING: Removed duplicated region for block: B:103:0x01d4  */
    /* JADX WARNING: Removed duplicated region for block: B:148:0x0291  */
    /* JADX WARNING: Removed duplicated region for block: B:109:0x01f8 A:{SYNTHETIC, Splitter:B:109:0x01f8} */
    /* JADX WARNING: Removed duplicated region for block: B:163:0x02af A:{SYNTHETIC, Splitter:B:163:0x02af} */
    /* JADX WARNING: Removed duplicated region for block: B:186:0x02f0  */
    /* JADX WARNING: Removed duplicated region for block: B:176:0x02ce A:{SYNTHETIC, Splitter:B:176:0x02ce} */
    /* JADX WARNING: Removed duplicated region for block: B:197:0x0311 A:{SYNTHETIC, Splitter:B:197:0x0311} */
    /* JADX WARNING: Removed duplicated region for block: B:193:0x0304 A:{SYNTHETIC, Splitter:B:193:0x0304} */
    /* JADX WARNING: Removed duplicated region for block: B:214:0x034f  */
    /* JADX WARNING: Removed duplicated region for block: B:206:0x032e A:{SYNTHETIC, Splitter:B:206:0x032e} */
    /* JADX WARNING: Removed duplicated region for block: B:217:0x0354 A:{SYNTHETIC, Splitter:B:217:0x0354} */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x040c  */
    /* JADX WARNING: Removed duplicated region for block: B:261:0x0437 A:{Catch:{ Exception -> 0x0b10, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:267:0x0453 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:278:0x0473 A:{SYNTHETIC, Splitter:B:278:0x0473} */
    /* JADX WARNING: Removed duplicated region for block: B:345:0x05cc  */
    /* JADX WARNING: Removed duplicated region for block: B:348:0x05e1 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:353:0x0600 A:{Catch:{ Exception -> 0x0af4, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:354:0x0603 A:{Catch:{ Exception -> 0x0af4, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:358:0x060c  */
    /* JADX WARNING: Removed duplicated region for block: B:360:0x061b  */
    /* JADX WARNING: Removed duplicated region for block: B:480:0x07f9 A:{Catch:{ Exception -> 0x0a66, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:478:0x07ef A:{Catch:{ Exception -> 0x0a66, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:649:0x0c1a A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:651:0x0c1f A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:653:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:655:0x0c2c A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:649:0x0c1a A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:651:0x0c1f A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:653:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:655:0x0c2c A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:686:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:686:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:715:0x0d22 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:649:0x0c1a A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:651:0x0c1f A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:653:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:655:0x0c2c A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:686:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:686:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:715:0x0d22 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0caa A:{Splitter:B:60:0x0139, ExcHandler: all (r0_57 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:720:0x0d2b  */
    /* JADX WARNING: Removed duplicated region for block: B:723:0x0d32 A:{SYNTHETIC, Splitter:B:723:0x0d32} */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0caa A:{Splitter:B:60:0x0139, ExcHandler: all (r0_57 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0caa A:{Splitter:B:60:0x0139, ExcHandler: all (r0_57 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0caa A:{Splitter:B:60:0x0139, ExcHandler: all (r0_57 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:715:0x0d22 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0caa A:{Splitter:B:60:0x0139, ExcHandler: all (r0_57 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:639:0x0bd8 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:649:0x0c1a A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:651:0x0c1f A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:653:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:655:0x0c2c A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:686:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:686:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:715:0x0d22 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0caa A:{Splitter:B:60:0x0139, ExcHandler: all (r0_57 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:704:0x0cf0  */
    /* JADX WARNING: Removed duplicated region for block: B:707:0x0cf7 A:{SYNTHETIC, Splitter:B:707:0x0cf7} */
    /* JADX WARNING: Removed duplicated region for block: B:715:0x0d22 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0caa A:{Splitter:B:60:0x0139, ExcHandler: all (r0_57 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:639:0x0bd8 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:649:0x0c1a A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:651:0x0c1f A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:653:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:655:0x0c2c A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:686:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:686:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:715:0x0d22 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0caa A:{Splitter:B:60:0x0139, ExcHandler: all (r0_57 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:639:0x0bd8 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:649:0x0c1a A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:651:0x0c1f A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:653:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:655:0x0c2c A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:686:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:686:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:715:0x0d22 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0caa A:{Splitter:B:60:0x0139, ExcHandler: all (r0_57 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:639:0x0bd8 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:649:0x0c1a A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:651:0x0c1f A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:653:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:655:0x0c2c A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:686:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:686:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:715:0x0d22 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0caa A:{Splitter:B:60:0x0139, ExcHandler: all (r0_57 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:639:0x0bd8 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:649:0x0c1a A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:651:0x0c1f A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:653:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:655:0x0c2c A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:686:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:686:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:715:0x0d22 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0caa A:{Splitter:B:60:0x0139, ExcHandler: all (r0_57 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:639:0x0bd8 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:649:0x0c1a A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:651:0x0c1f A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:653:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:655:0x0c2c A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:686:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:686:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:715:0x0d22 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0caa A:{Splitter:B:60:0x0139, ExcHandler: all (r0_57 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:639:0x0bd8 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:649:0x0c1a A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:651:0x0c1f A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:653:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:655:0x0c2c A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:686:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:686:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:715:0x0d22 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0caa A:{Splitter:B:60:0x0139, ExcHandler: all (r0_57 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0caa A:{Splitter:B:60:0x0139, ExcHandler: all (r0_57 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:704:0x0cf0  */
    /* JADX WARNING: Removed duplicated region for block: B:707:0x0cf7 A:{SYNTHETIC, Splitter:B:707:0x0cf7} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:715:0x0d22 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0caa A:{Splitter:B:60:0x0139, ExcHandler: all (r0_57 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:639:0x0bd8 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:649:0x0c1a A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:651:0x0c1f A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:653:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:655:0x0c2c A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:686:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:686:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:715:0x0d22 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0caa A:{Splitter:B:60:0x0139, ExcHandler: all (r0_57 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0caa A:{Splitter:B:60:0x0139, ExcHandler: all (r0_57 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:639:0x0bd8 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:649:0x0c1a A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:651:0x0c1f A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:653:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:655:0x0c2c A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:686:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:686:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:715:0x0d22 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0caa A:{Splitter:B:60:0x0139, ExcHandler: all (r0_57 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:639:0x0bd8 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:649:0x0c1a A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:651:0x0c1f A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:653:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:655:0x0c2c A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:686:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:686:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:715:0x0d22 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0caa A:{Splitter:B:60:0x0139, ExcHandler: all (r0_57 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0caa A:{Splitter:B:60:0x0139, ExcHandler: all (r0_57 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:639:0x0bd8 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:649:0x0c1a A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:651:0x0c1f A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:653:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:655:0x0c2c A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:686:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:686:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:715:0x0d22 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0caa A:{Splitter:B:60:0x0139, ExcHandler: all (r0_57 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0caa A:{Splitter:B:60:0x0139, ExcHandler: all (r0_57 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0caa A:{Splitter:B:60:0x0139, ExcHandler: all (r0_57 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:639:0x0bd8 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:649:0x0c1a A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:651:0x0c1f A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:653:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:655:0x0c2c A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:686:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:686:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:715:0x0d22 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0caa A:{Splitter:B:60:0x0139, ExcHandler: all (r0_57 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:639:0x0bd8 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:649:0x0c1a A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:651:0x0c1f A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:653:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:655:0x0c2c A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:686:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:686:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:715:0x0d22 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0caa A:{Splitter:B:60:0x0139, ExcHandler: all (r0_57 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:639:0x0bd8 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:649:0x0c1a A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:651:0x0c1f A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:653:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:655:0x0c2c A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:686:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:686:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:715:0x0d22 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0caa A:{Splitter:B:60:0x0139, ExcHandler: all (r0_57 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0caa A:{Splitter:B:60:0x0139, ExcHandler: all (r0_57 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:639:0x0bd8 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:649:0x0c1a A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:651:0x0c1f A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:653:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:655:0x0c2c A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:686:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:686:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:715:0x0d22 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0caa A:{Splitter:B:60:0x0139, ExcHandler: all (r0_57 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0caa A:{Splitter:B:60:0x0139, ExcHandler: all (r0_57 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0caa A:{Splitter:B:60:0x0139, ExcHandler: all (r0_57 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:704:0x0cf0  */
    /* JADX WARNING: Removed duplicated region for block: B:707:0x0cf7 A:{SYNTHETIC, Splitter:B:707:0x0cf7} */
    /* JADX WARNING: Removed duplicated region for block: B:715:0x0d22 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0caa A:{Splitter:B:60:0x0139, ExcHandler: all (r0_57 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:639:0x0bd8 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:649:0x0c1a A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:651:0x0c1f A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:653:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:655:0x0c2c A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:686:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:686:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:715:0x0d22 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0caa A:{Splitter:B:60:0x0139, ExcHandler: all (r0_57 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:639:0x0bd8 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:649:0x0c1a A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:651:0x0c1f A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:653:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:655:0x0c2c A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:686:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:686:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:715:0x0d22 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0caa A:{Splitter:B:60:0x0139, ExcHandler: all (r0_57 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0caa A:{Splitter:B:60:0x0139, ExcHandler: all (r0_57 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:704:0x0cf0  */
    /* JADX WARNING: Removed duplicated region for block: B:707:0x0cf7 A:{SYNTHETIC, Splitter:B:707:0x0cf7} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:715:0x0d22 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0caa A:{Splitter:B:60:0x0139, ExcHandler: all (r0_57 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0caa A:{Splitter:B:60:0x0139, ExcHandler: all (r0_57 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:704:0x0cf0  */
    /* JADX WARNING: Removed duplicated region for block: B:707:0x0cf7 A:{SYNTHETIC, Splitter:B:707:0x0cf7} */
    /* JADX WARNING: Removed duplicated region for block: B:715:0x0d22 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0caa A:{Splitter:B:60:0x0139, ExcHandler: all (r0_57 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:639:0x0bd8 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:649:0x0c1a A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:651:0x0c1f A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:653:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:655:0x0c2c A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:686:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:686:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:715:0x0d22 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0caa A:{Splitter:B:60:0x0139, ExcHandler: all (r0_57 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:704:0x0cf0  */
    /* JADX WARNING: Removed duplicated region for block: B:707:0x0cf7 A:{SYNTHETIC, Splitter:B:707:0x0cf7} */
    /* JADX WARNING: Removed duplicated region for block: B:715:0x0d22 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0caa A:{Splitter:B:60:0x0139, ExcHandler: all (r0_57 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:704:0x0cf0  */
    /* JADX WARNING: Removed duplicated region for block: B:707:0x0cf7 A:{SYNTHETIC, Splitter:B:707:0x0cf7} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:715:0x0d22 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0caa A:{Splitter:B:60:0x0139, ExcHandler: all (r0_57 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:639:0x0bd8 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:649:0x0c1a A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:651:0x0c1f A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:653:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:655:0x0c2c A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:686:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:686:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:715:0x0d22 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0caa A:{Splitter:B:60:0x0139, ExcHandler: all (r0_57 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:639:0x0bd8 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:649:0x0c1a A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:651:0x0c1f A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:653:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:655:0x0c2c A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:686:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:686:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:715:0x0d22 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0caa A:{Splitter:B:60:0x0139, ExcHandler: all (r0_57 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:639:0x0bd8 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:649:0x0c1a A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:651:0x0c1f A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:653:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:655:0x0c2c A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:686:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:686:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:715:0x0d22 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0caa A:{Splitter:B:60:0x0139, ExcHandler: all (r0_57 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:639:0x0bd8 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:649:0x0c1a A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:651:0x0c1f A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:653:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:655:0x0c2c A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:686:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:686:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:715:0x0d22 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0caa A:{Splitter:B:60:0x0139, ExcHandler: all (r0_57 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:639:0x0bd8 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:649:0x0c1a A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:651:0x0c1f A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:653:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:655:0x0c2c A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:686:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:686:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:715:0x0d22 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0caa A:{Splitter:B:60:0x0139, ExcHandler: all (r0_57 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0caa A:{Splitter:B:60:0x0139, ExcHandler: all (r0_57 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:639:0x0bd8 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:649:0x0c1a A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:651:0x0c1f A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:653:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:655:0x0c2c A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:686:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:686:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:715:0x0d22 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:639:0x0bd8 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:649:0x0c1a A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:651:0x0c1f A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:653:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:655:0x0c2c A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:686:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:686:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:715:0x0d22 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:639:0x0bd8 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:649:0x0c1a A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:651:0x0c1f A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:653:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:655:0x0c2c A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:686:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:686:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:715:0x0d22 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:639:0x0bd8 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:649:0x0c1a A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:651:0x0c1f A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:653:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:655:0x0c2c A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c3d, all -> 0x0caa }} */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:686:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:686:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:715:0x0d22 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0d05  */
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
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:60:0x0139, B:672:0x0c6d] */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:60:0x0139, B:678:0x0CLASSNAME] */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing block: B:134:0x0262, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:135:0x0263, code skipped:
            r1 = r0;
            r10 = r4;
     */
    /* JADX WARNING: Missing block: B:142:0x0281, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:143:0x0282, code skipped:
            r1 = r0;
            r10 = r2;
            r9 = r4;
     */
    /* JADX WARNING: Missing block: B:165:0x02bc, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:166:0x02bd, code skipped:
            r1 = r0;
     */
    /* JADX WARNING: Missing block: B:181:0x02dc, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:182:0x02dd, code skipped:
            r1 = r0;
            r6 = r2;
            r11 = r12;
            r64 = r28;
            r2 = null;
            r4 = null;
     */
    /* JADX WARNING: Missing block: B:183:0x02e5, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:184:0x02e6, code skipped:
            r1 = r0;
            r11 = r12;
            r64 = r28;
            r2 = null;
            r4 = null;
            r6 = null;
     */
    /* JADX WARNING: Missing block: B:195:0x030a, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:196:0x030b, code skipped:
            r1 = r0;
            r11 = r12;
            r64 = r28;
            r2 = null;
     */
    /* JADX WARNING: Missing block: B:212:0x0345, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:213:0x0346, code skipped:
            r9 = r73;
            r10 = r74;
            r1 = r0;
            r11 = r12;
            r64 = r28;
     */
    /* JADX WARNING: Missing block: B:229:0x03b3, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:248:0x03e9, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:249:0x03eb, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:250:0x03ec, code skipped:
            r10 = r77;
     */
    /* JADX WARNING: Missing block: B:251:0x03ee, code skipped:
            r9 = r73;
            r10 = r74;
            r1 = r0;
            r13 = r6;
     */
    /* JADX WARNING: Missing block: B:252:0x03f6, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:253:0x03f7, code skipped:
            r10 = r77;
     */
    /* JADX WARNING: Missing block: B:255:0x0405, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:256:0x0406, code skipped:
            r10 = r77;
            r45 = r6;
     */
    /* JADX WARNING: Missing block: B:272:0x0460, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:273:0x0461, code skipped:
            r9 = r73;
            r10 = r74;
            r1 = r0;
     */
    /* JADX WARNING: Missing block: B:274:0x0466, code skipped:
            r11 = r12;
            r64 = r28;
            r6 = r45;
     */
    /* JADX WARNING: Missing block: B:299:0x04d6, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:300:0x04d7, code skipped:
            r11 = r71;
            r9 = r73;
            r10 = r74;
            r1 = r0;
            r64 = r2;
            r13 = r28;
            r6 = r45;
            r2 = r58;
     */
    /* JADX WARNING: Missing block: B:339:0x05ad, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:340:0x05ae, code skipped:
            r11 = r71;
     */
    /* JADX WARNING: Missing block: B:342:0x05b8, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:343:0x05b9, code skipped:
            r61 = r28;
            r28 = r13;
            r11 = r71;
            r9 = r73;
            r10 = r74;
            r1 = r0;
     */
    /* JADX WARNING: Missing block: B:468:0x07c8, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:469:0x07c9, code skipped:
            r11 = r71;
            r6 = r72;
     */
    /* JADX WARNING: Missing block: B:494:0x0859, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:541:0x08de, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:544:?, code skipped:
            org.telegram.messenger.FileLog.e(r0);
            r1 = 1;
     */
    /* JADX WARNING: Missing block: B:559:0x0923, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:560:0x0925, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:561:0x0926, code skipped:
            r1 = r45;
     */
    /* JADX WARNING: Missing block: B:562:0x0928, code skipped:
            r9 = r73;
            r10 = r74;
            r6 = r1;
            r4 = r17;
     */
    /* JADX WARNING: Missing block: B:563:0x0930, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:564:0x0931, code skipped:
            r17 = r4;
            r9 = r73;
            r10 = r74;
            r6 = r45;
     */
    /* JADX WARNING: Missing block: B:565:0x093a, code skipped:
            r13 = r28;
            r64 = r61;
            r16 = null;
            r1 = r0;
     */
    /* JADX WARNING: Missing block: B:577:0x09d3, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:578:0x09d4, code skipped:
            r23 = r1;
            r49 = r2;
            r64 = r61;
            r16 = null;
            r9 = r73;
            r10 = r74;
            r1 = r0;
            r4 = r17;
            r6 = r23;
            r13 = r28;
     */
    /* JADX WARNING: Missing block: B:595:0x0a66, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:604:0x0aeb, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:605:0x0aec, code skipped:
            r9 = r73;
            r10 = r74;
            r1 = r0;
            r4 = r17;
     */
    /* JADX WARNING: Missing block: B:606:0x0af4, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:607:0x0af5, code skipped:
            r11 = r71;
     */
    /* JADX WARNING: Missing block: B:608:0x0af7, code skipped:
            r17 = r4;
            r23 = r45;
            r49 = r58;
            r64 = r61;
            r16 = null;
            r9 = r73;
            r10 = r74;
            r1 = r0;
     */
    /* JADX WARNING: Missing block: B:609:0x0b08, code skipped:
            r6 = r23;
            r13 = r28;
            r2 = r49;
     */
    /* JADX WARNING: Missing block: B:610:0x0b10, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:611:0x0b11, code skipped:
            r49 = r2;
            r17 = r4;
            r11 = r12;
            r64 = r28;
            r16 = null;
            r28 = r13;
            r9 = r73;
            r10 = r74;
            r1 = r0;
            r6 = r45;
     */
    /* JADX WARNING: Missing block: B:612:0x0b29, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:613:0x0b2a, code skipped:
            r49 = r2;
            r17 = r4;
            r23 = r6;
            r45 = null;
            r11 = r12;
            r64 = r28;
            r16 = null;
            r9 = r73;
            r10 = r74;
     */
    /* JADX WARNING: Missing block: B:614:0x0b3e, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:615:0x0b3f, code skipped:
            r49 = r2;
            r17 = r4;
            r23 = r6;
            r73 = r9;
            r74 = r10;
            r11 = r12;
            r64 = r28;
            r16 = null;
            r45 = null;
     */
    /* JADX WARNING: Missing block: B:616:0x0b52, code skipped:
            r1 = r0;
            r13 = r45;
     */
    /* JADX WARNING: Missing block: B:617:0x0b57, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:618:0x0b58, code skipped:
            r17 = r4;
            r23 = r6;
            r73 = r9;
            r74 = r10;
            r11 = r12;
            r64 = r28;
            r16 = null;
            r1 = r0;
            r2 = null;
            r13 = r2;
     */
    /* JADX WARNING: Missing block: B:619:0x0b6f, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:620:0x0b70, code skipped:
            r23 = r6;
            r73 = r9;
            r74 = r10;
            r11 = r12;
            r64 = r28;
            r16 = null;
            r1 = r0;
            r2 = null;
            r4 = r2;
            r13 = r4;
     */
    /* JADX WARNING: Missing block: B:621:0x0b86, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:622:0x0b87, code skipped:
            r73 = r9;
            r74 = r10;
            r11 = r12;
            r64 = r28;
            r16 = null;
            r1 = r0;
            r2 = null;
     */
    /* JADX WARNING: Missing block: B:623:0x0b98, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:624:0x0b99, code skipped:
            r45 = null;
            r73 = r9;
            r74 = r10;
            r11 = r12;
            r64 = r28;
            r16 = null;
            r1 = r0;
     */
    /* JADX WARNING: Missing block: B:625:0x0ba7, code skipped:
            r4 = r2;
            r6 = r4;
            r13 = r6;
     */
    /* JADX WARNING: Missing block: B:626:0x0bab, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:627:0x0bac, code skipped:
            r73 = r9;
            r74 = r10;
     */
    /* JADX WARNING: Missing block: B:628:0x0bb1, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:629:0x0bb2, code skipped:
            r11 = r12;
            r64 = r28;
            r16 = null;
            r45 = null;
            r1 = r0;
            r10 = r2;
     */
    /* JADX WARNING: Missing block: B:630:0x0bbe, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:631:0x0bbf, code skipped:
            r11 = r12;
            r64 = r28;
     */
    /* JADX WARNING: Missing block: B:632:0x0bc3, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:633:0x0bc4, code skipped:
            r64 = r4;
            r11 = r12;
     */
    /* JADX WARNING: Missing block: B:634:0x0bc7, code skipped:
            r16 = null;
            r45 = null;
            r1 = r0;
     */
    /* JADX WARNING: Missing block: B:635:0x0bce, code skipped:
            r2 = r45;
            r4 = r2;
            r6 = r4;
            r13 = r6;
            r15 = r13;
     */
    /* JADX WARNING: Missing block: B:640:0x0bda, code skipped:
            r3 = 1;
     */
    /* JADX WARNING: Missing block: B:660:0x0c3d, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:661:0x0c3e, code skipped:
            r1 = r0;
            r16 = r3;
     */
    /* JADX WARNING: Missing block: B:662:0x0CLASSNAME, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:663:0x0CLASSNAME, code skipped:
            r15 = r72;
            r1 = r0;
            r16 = r3;
     */
    /* JADX WARNING: Missing block: B:664:0x0c4a, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:665:0x0c4b, code skipped:
            r15 = r72;
     */
    /* JADX WARNING: Missing block: B:691:0x0ca5, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:692:0x0ca7, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:693:0x0ca8, code skipped:
            r15 = r7;
     */
    /* JADX WARNING: Missing block: B:694:0x0caa, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:695:0x0cab, code skipped:
            r2 = r0;
            r1 = r14;
     */
    /* JADX WARNING: Missing block: B:704:0x0cf0, code skipped:
            r1.release();
     */
    /* JADX WARNING: Missing block: B:708:?, code skipped:
            r1.finishMovie();
     */
    /* JADX WARNING: Missing block: B:709:0x0cfb, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:710:0x0cfc, code skipped:
            org.telegram.messenger.FileLog.e(r0);
     */
    /* JADX WARNING: Missing block: B:720:0x0d2b, code skipped:
            r3.release();
     */
    /* JADX WARNING: Missing block: B:724:?, code skipped:
            r3.finishMovie();
     */
    /* JADX WARNING: Missing block: B:725:0x0d36, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:726:0x0d37, code skipped:
            org.telegram.messenger.FileLog.e(r0);
     */
    private boolean convertVideoInternal(java.lang.String r67, java.io.File r68, int r69, boolean r70, int r71, int r72, int r73, int r74, long r75, long r77, long r79, boolean r81, boolean r82) {
        /*
        r66 = this;
        r14 = r66;
        r13 = r67;
        r15 = r69;
        r12 = r71;
        r11 = r72;
        r9 = r73;
        r10 = r74;
        r7 = r75;
        r5 = r77;
        r2 = new android.media.MediaCodec$BufferInfo;	 Catch:{ Exception -> 0x0caf, all -> 0x0caa }
        r2.<init>();	 Catch:{ Exception -> 0x0caf, all -> 0x0caa }
        r1 = new org.telegram.messenger.video.Mp4Movie;	 Catch:{ Exception -> 0x0caf, all -> 0x0caa }
        r1.<init>();	 Catch:{ Exception -> 0x0caf, all -> 0x0caa }
        r4 = r68;
        r1.setCacheFile(r4);	 Catch:{ Exception -> 0x0caf, all -> 0x0caa }
        r1.setRotation(r15);	 Catch:{ Exception -> 0x0caf, all -> 0x0caa }
        r1.setSize(r12, r11);	 Catch:{ Exception -> 0x0caf, all -> 0x0caa }
        r3 = new org.telegram.messenger.video.MP4Builder;	 Catch:{ Exception -> 0x0caf, all -> 0x0caa }
        r3.<init>();	 Catch:{ Exception -> 0x0caf, all -> 0x0caa }
        r4 = r70;
        r1 = r3.createMovie(r1, r4);	 Catch:{ Exception -> 0x0caf, all -> 0x0caa }
        r14.mediaMuxer = r1;	 Catch:{ Exception -> 0x0caf, all -> 0x0caa }
        r1 = new android.media.MediaExtractor;	 Catch:{ Exception -> 0x0caf, all -> 0x0caa }
        r1.<init>();	 Catch:{ Exception -> 0x0caf, all -> 0x0caa }
        r14.extractor = r1;	 Catch:{ Exception -> 0x0caf, all -> 0x0caa }
        r1 = r14.extractor;	 Catch:{ Exception -> 0x0caf, all -> 0x0caa }
        r1.setDataSource(r13);	 Catch:{ Exception -> 0x0caf, all -> 0x0caa }
        r3 = r79;
        r1 = (float) r3;	 Catch:{ Exception -> 0x0caf, all -> 0x0caa }
        r18 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r18 = r1 / r18;
        r66.checkConversionCanceled();	 Catch:{ Exception -> 0x0caf, all -> 0x0caa }
        if (r81 == 0) goto L_0x0CLASSNAME;
    L_0x004c:
        r1 = r14.extractor;	 Catch:{ Exception -> 0x0c5a, all -> 0x0caa }
        r3 = 0;
        r4 = org.telegram.messenger.MediaController.findTrack(r1, r3);	 Catch:{ Exception -> 0x0c5a, all -> 0x0caa }
        r1 = -1;
        if (r10 == r1) goto L_0x0068;
    L_0x0056:
        r1 = r14.extractor;	 Catch:{ Exception -> 0x005f, all -> 0x0caa }
        r3 = 1;
        r1 = org.telegram.messenger.MediaController.findTrack(r1, r3);	 Catch:{ Exception -> 0x005f, all -> 0x0caa }
        r3 = r1;
        goto L_0x0069;
    L_0x005f:
        r0 = move-exception;
        r1 = r0;
        r15 = r11;
        r16 = 0;
        r30 = 1;
        goto L_0x0cba;
    L_0x0068:
        r3 = -1;
    L_0x0069:
        if (r4 < 0) goto L_0x0c4f;
    L_0x006b:
        r20 = -1;
        r22 = android.os.Build.MANUFACTURER;	 Catch:{ Exception -> 0x0bc3, all -> 0x0caa }
        r1 = r22.toLowerCase();	 Catch:{ Exception -> 0x0bc3, all -> 0x0caa }
        r22 = r2;
        r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0bc3, all -> 0x0caa }
        r26 = 4;
        r5 = "video/avc";
        r6 = 18;
        if (r2 >= r6) goto L_0x0130;
    L_0x0080:
        r2 = org.telegram.messenger.MediaController.selectCodec(r5);	 Catch:{ Exception -> 0x0120, all -> 0x0caa }
        r29 = org.telegram.messenger.MediaController.selectColorFormat(r2, r5);	 Catch:{ Exception -> 0x0120, all -> 0x0caa }
        if (r29 == 0) goto L_0x0118;
    L_0x008a:
        r6 = r2.getName();	 Catch:{ Exception -> 0x0120, all -> 0x0caa }
        r13 = "OMX.qcom.";
        r13 = r6.contains(r13);	 Catch:{ Exception -> 0x0120, all -> 0x0caa }
        if (r13 == 0) goto L_0x00b2;
    L_0x0096:
        r6 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0120, all -> 0x0caa }
        r13 = 16;
        if (r6 != r13) goto L_0x00af;
    L_0x009c:
        r6 = "lge";
        r6 = r1.equals(r6);	 Catch:{ Exception -> 0x0120, all -> 0x0caa }
        if (r6 != 0) goto L_0x00ac;
    L_0x00a4:
        r6 = "nokia";
        r6 = r1.equals(r6);	 Catch:{ Exception -> 0x0120, all -> 0x0caa }
        if (r6 == 0) goto L_0x00af;
    L_0x00ac:
        r6 = 1;
    L_0x00ad:
        r13 = 1;
        goto L_0x00dc;
    L_0x00af:
        r6 = 1;
    L_0x00b0:
        r13 = 0;
        goto L_0x00dc;
    L_0x00b2:
        r13 = "OMX.Intel.";
        r13 = r6.contains(r13);	 Catch:{ Exception -> 0x0120, all -> 0x0caa }
        if (r13 == 0) goto L_0x00bc;
    L_0x00ba:
        r6 = 2;
        goto L_0x00b0;
    L_0x00bc:
        r13 = "OMX.MTK.VIDEO.ENCODER.AVC";
        r13 = r6.equals(r13);	 Catch:{ Exception -> 0x0120, all -> 0x0caa }
        if (r13 == 0) goto L_0x00c6;
    L_0x00c4:
        r6 = 3;
        goto L_0x00b0;
    L_0x00c6:
        r13 = "OMX.SEC.AVC.Encoder";
        r13 = r6.equals(r13);	 Catch:{ Exception -> 0x0120, all -> 0x0caa }
        if (r13 == 0) goto L_0x00d0;
    L_0x00ce:
        r6 = 4;
        goto L_0x00ad;
    L_0x00d0:
        r13 = "OMX.TI.DUCATI1.VIDEO.H264E";
        r6 = r6.equals(r13);	 Catch:{ Exception -> 0x0120, all -> 0x0caa }
        if (r6 == 0) goto L_0x00da;
    L_0x00d8:
        r6 = 5;
        goto L_0x00b0;
    L_0x00da:
        r6 = 0;
        goto L_0x00b0;
    L_0x00dc:
        r31 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x0120, all -> 0x0caa }
        if (r31 == 0) goto L_0x010f;
    L_0x00e0:
        r31 = r6;
        r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0120, all -> 0x0caa }
        r6.<init>();	 Catch:{ Exception -> 0x0120, all -> 0x0caa }
        r32 = r13;
        r13 = "codec = ";
        r6.append(r13);	 Catch:{ Exception -> 0x0120, all -> 0x0caa }
        r2 = r2.getName();	 Catch:{ Exception -> 0x0120, all -> 0x0caa }
        r6.append(r2);	 Catch:{ Exception -> 0x0120, all -> 0x0caa }
        r2 = " manufacturer = ";
        r6.append(r2);	 Catch:{ Exception -> 0x0120, all -> 0x0caa }
        r6.append(r1);	 Catch:{ Exception -> 0x0120, all -> 0x0caa }
        r2 = "device = ";
        r6.append(r2);	 Catch:{ Exception -> 0x0120, all -> 0x0caa }
        r2 = android.os.Build.MODEL;	 Catch:{ Exception -> 0x0120, all -> 0x0caa }
        r6.append(r2);	 Catch:{ Exception -> 0x0120, all -> 0x0caa }
        r2 = r6.toString();	 Catch:{ Exception -> 0x0120, all -> 0x0caa }
        org.telegram.messenger.FileLog.d(r2);	 Catch:{ Exception -> 0x0120, all -> 0x0caa }
        goto L_0x0113;
    L_0x010f:
        r31 = r6;
        r32 = r13;
    L_0x0113:
        r13 = r29;
        r2 = r31;
        goto L_0x0139;
    L_0x0118:
        r1 = new java.lang.RuntimeException;	 Catch:{ Exception -> 0x0120, all -> 0x0caa }
        r2 = "no supported color format";
        r1.<init>(r2);	 Catch:{ Exception -> 0x0120, all -> 0x0caa }
        throw r1;	 Catch:{ Exception -> 0x0120, all -> 0x0caa }
    L_0x0120:
        r0 = move-exception;
        r1 = r0;
        r64 = r4;
        r11 = r12;
    L_0x0125:
        r2 = 0;
        r4 = 0;
        r6 = 0;
        r13 = 0;
        r15 = 0;
    L_0x012a:
        r16 = 0;
        r30 = 1;
        goto L_0x0bd4;
    L_0x0130:
        r2 = NUM; // 0x7var_ float:1.701803E38 double:1.0527098025E-314;
        r2 = 0;
        r13 = NUM; // 0x7var_ float:1.701803E38 double:1.0527098025E-314;
        r32 = 0;
    L_0x0139:
        r6 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x0bc3, all -> 0x0caa }
        if (r6 == 0) goto L_0x0154;
    L_0x013d:
        r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0120, all -> 0x0caa }
        r6.<init>();	 Catch:{ Exception -> 0x0120, all -> 0x0caa }
        r29 = r3;
        r3 = "colorFormat = ";
        r6.append(r3);	 Catch:{ Exception -> 0x0120, all -> 0x0caa }
        r6.append(r13);	 Catch:{ Exception -> 0x0120, all -> 0x0caa }
        r3 = r6.toString();	 Catch:{ Exception -> 0x0120, all -> 0x0caa }
        org.telegram.messenger.FileLog.d(r3);	 Catch:{ Exception -> 0x0120, all -> 0x0caa }
        goto L_0x0156;
    L_0x0154:
        r29 = r3;
    L_0x0156:
        r3 = r12 * r11;
        r6 = r3 * 3;
        r25 = 2;
        r6 = r6 / 2;
        if (r2 != 0) goto L_0x0177;
    L_0x0160:
        r1 = r11 % 16;
        if (r1 == 0) goto L_0x01ab;
    L_0x0164:
        r1 = r11 % 16;
        r2 = 16;
        r1 = 16 - r1;
        r1 = r1 + r11;
        r1 = r1 - r11;
        r1 = r1 * r12;
        r2 = r1 * 5;
        r2 = r2 / 4;
    L_0x0172:
        r6 = r6 + r2;
    L_0x0173:
        r15 = r1;
        r24 = r6;
        goto L_0x01ae;
    L_0x0177:
        r15 = 1;
        if (r2 != r15) goto L_0x018d;
    L_0x017a:
        r1 = r1.toLowerCase();	 Catch:{ Exception -> 0x0120, all -> 0x0caa }
        r2 = "lge";
        r1 = r1.equals(r2);	 Catch:{ Exception -> 0x0120, all -> 0x0caa }
        if (r1 != 0) goto L_0x01ab;
    L_0x0186:
        r1 = r3 + 2047;
        r1 = r1 & -2048;
        r1 = r1 - r3;
        r6 = r6 + r1;
        goto L_0x0173;
    L_0x018d:
        r3 = 5;
        if (r2 != r3) goto L_0x0191;
    L_0x0190:
        goto L_0x01ab;
    L_0x0191:
        r3 = 3;
        if (r2 != r3) goto L_0x01ab;
    L_0x0194:
        r2 = "baidu";
        r1 = r1.equals(r2);	 Catch:{ Exception -> 0x0120, all -> 0x0caa }
        if (r1 == 0) goto L_0x01ab;
    L_0x019c:
        r1 = r11 % 16;
        r2 = 16;
        r1 = 16 - r1;
        r1 = r1 + r11;
        r1 = r1 - r11;
        r1 = r1 * r12;
        r2 = r1 * 5;
        r2 = r2 / 4;
        goto L_0x0172;
    L_0x01ab:
        r24 = r6;
        r15 = 0;
    L_0x01ae:
        r1 = r14.extractor;	 Catch:{ Exception -> 0x0bc3, all -> 0x0caa }
        r1.selectTrack(r4);	 Catch:{ Exception -> 0x0bc3, all -> 0x0caa }
        r1 = r14.extractor;	 Catch:{ Exception -> 0x0bc3, all -> 0x0caa }
        r1 = r1.getTrackFormat(r4);	 Catch:{ Exception -> 0x0bc3, all -> 0x0caa }
        r2 = 0;
        r6 = (r7 > r2 ? 1 : (r7 == r2 ? 0 : -1));
        if (r6 <= 0) goto L_0x01c8;
    L_0x01bf:
        r6 = r14.extractor;	 Catch:{ Exception -> 0x0120, all -> 0x0caa }
        r2 = 0;
        r6.seekTo(r7, r2);	 Catch:{ Exception -> 0x0120, all -> 0x0caa }
        r28 = r4;
        goto L_0x01d2;
    L_0x01c8:
        r2 = r14.extractor;	 Catch:{ Exception -> 0x0bc3, all -> 0x0caa }
        r28 = r4;
        r3 = 0;
        r6 = 0;
        r2.seekTo(r3, r6);	 Catch:{ Exception -> 0x0bbe, all -> 0x0caa }
    L_0x01d2:
        if (r10 > 0) goto L_0x01d8;
    L_0x01d4:
        r2 = 921600; // 0xe1000 float:1.291437E-39 double:4.55331E-318;
        goto L_0x01d9;
    L_0x01d8:
        r2 = r10;
    L_0x01d9:
        r3 = android.media.MediaFormat.createVideoFormat(r5, r12, r11);	 Catch:{ Exception -> 0x0bb1, all -> 0x0caa }
        r4 = "color-format";
        r3.setInteger(r4, r13);	 Catch:{ Exception -> 0x0bb1, all -> 0x0caa }
        r4 = "bitrate";
        r3.setInteger(r4, r2);	 Catch:{ Exception -> 0x0bb1, all -> 0x0caa }
        r4 = "frame-rate";
        r3.setInteger(r4, r9);	 Catch:{ Exception -> 0x0bb1, all -> 0x0caa }
        r4 = "i-frame-interval";
        r6 = 2;
        r3.setInteger(r4, r6);	 Catch:{ Exception -> 0x0bb1, all -> 0x0caa }
        r4 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0bb1, all -> 0x0caa }
        r6 = 23;
        if (r4 < r6) goto L_0x0291;
    L_0x01f8:
        r4 = java.lang.Math.min(r11, r12);	 Catch:{ Exception -> 0x0289, all -> 0x0caa }
        r6 = 1080; // 0x438 float:1.513E-42 double:5.336E-321;
        if (r4 < r6) goto L_0x0205;
    L_0x0200:
        r4 = 8;
        r6 = 4096; // 0x1000 float:5.74E-42 double:2.0237E-320;
        goto L_0x0220;
    L_0x0205:
        r4 = java.lang.Math.min(r11, r12);	 Catch:{ Exception -> 0x0289, all -> 0x0caa }
        r6 = 720; // 0x2d0 float:1.009E-42 double:3.557E-321;
        if (r4 < r6) goto L_0x0212;
    L_0x020d:
        r4 = 8;
        r6 = 2048; // 0x800 float:2.87E-42 double:1.0118E-320;
        goto L_0x0220;
    L_0x0212:
        r4 = java.lang.Math.min(r11, r12);	 Catch:{ Exception -> 0x0289, all -> 0x0caa }
        r6 = 480; // 0x1e0 float:6.73E-43 double:2.37E-321;
        if (r4 < r6) goto L_0x021d;
    L_0x021a:
        r6 = 512; // 0x200 float:7.175E-43 double:2.53E-321;
        goto L_0x021f;
    L_0x021d:
        r6 = 256; // 0x100 float:3.59E-43 double:1.265E-321;
    L_0x021f:
        r4 = 1;
    L_0x0220:
        r10 = android.media.MediaCodecInfo.CodecCapabilities.createFromProfileLevel(r5, r4, r6);	 Catch:{ Exception -> 0x0289, all -> 0x0caa }
        r74 = r10;
        if (r10 != 0) goto L_0x0233;
    L_0x0228:
        r10 = 8;
        if (r4 != r10) goto L_0x0233;
    L_0x022c:
        r4 = 1;
        r10 = android.media.MediaCodecInfo.CodecCapabilities.createFromProfileLevel(r5, r4, r6);	 Catch:{ Exception -> 0x0289, all -> 0x0caa }
        r4 = 1;
        goto L_0x0235;
    L_0x0233:
        r10 = r74;
    L_0x0235:
        r31 = r10.getEncoderCapabilities();	 Catch:{ Exception -> 0x0289, all -> 0x0caa }
        if (r31 == 0) goto L_0x0286;
    L_0x023b:
        r31 = r15;
        r15 = "profile";
        r3.setInteger(r15, r4);	 Catch:{ Exception -> 0x0289, all -> 0x0caa }
        r4 = "level";
        r3.setInteger(r4, r6);	 Catch:{ Exception -> 0x0289, all -> 0x0caa }
        r4 = r10.getVideoCapabilities();	 Catch:{ Exception -> 0x0289, all -> 0x0caa }
        r4 = r4.getBitrateRange();	 Catch:{ Exception -> 0x0289, all -> 0x0caa }
        r4 = r4.getUpper();	 Catch:{ Exception -> 0x0289, all -> 0x0caa }
        r4 = (java.lang.Integer) r4;	 Catch:{ Exception -> 0x0289, all -> 0x0caa }
        r4 = r4.intValue();	 Catch:{ Exception -> 0x0289, all -> 0x0caa }
        if (r2 <= r4) goto L_0x0266;
    L_0x025b:
        r2 = "bitrate";
        r3.setInteger(r2, r4);	 Catch:{ Exception -> 0x0262, all -> 0x0caa }
        r2 = r4;
        goto L_0x0266;
    L_0x0262:
        r0 = move-exception;
        r1 = r0;
        r10 = r4;
        goto L_0x028c;
    L_0x0266:
        r4 = r10.getVideoCapabilities();	 Catch:{ Exception -> 0x0289, all -> 0x0caa }
        r4 = r4.getSupportedFrameRates();	 Catch:{ Exception -> 0x0289, all -> 0x0caa }
        r4 = r4.getUpper();	 Catch:{ Exception -> 0x0289, all -> 0x0caa }
        r4 = (java.lang.Integer) r4;	 Catch:{ Exception -> 0x0289, all -> 0x0caa }
        r4 = r4.intValue();	 Catch:{ Exception -> 0x0289, all -> 0x0caa }
        if (r9 <= r4) goto L_0x02a8;
    L_0x027a:
        r6 = "frame-rate";
        r3.setInteger(r6, r4);	 Catch:{ Exception -> 0x0281, all -> 0x0caa }
        r9 = r4;
        goto L_0x02a8;
    L_0x0281:
        r0 = move-exception;
        r1 = r0;
        r10 = r2;
        r9 = r4;
        goto L_0x028c;
    L_0x0286:
        r31 = r15;
        goto L_0x02a8;
    L_0x0289:
        r0 = move-exception;
        r1 = r0;
        r10 = r2;
    L_0x028c:
        r11 = r12;
        r64 = r28;
        goto L_0x0125;
    L_0x0291:
        r31 = r15;
        r4 = java.lang.Math.min(r11, r12);	 Catch:{ Exception -> 0x0bb1, all -> 0x0caa }
        r6 = 480; // 0x1e0 float:6.73E-43 double:2.37E-321;
        if (r4 > r6) goto L_0x02a8;
    L_0x029b:
        r4 = 921600; // 0xe1000 float:1.291437E-39 double:4.55331E-318;
        if (r2 <= r4) goto L_0x02a3;
    L_0x02a0:
        r2 = 921600; // 0xe1000 float:1.291437E-39 double:4.55331E-318;
    L_0x02a3:
        r4 = "bitrate";
        r3.setInteger(r4, r2);	 Catch:{ Exception -> 0x0289, all -> 0x0caa }
    L_0x02a8:
        r10 = r2;
        r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0bab, all -> 0x0caa }
        r4 = 18;
        if (r2 >= r4) goto L_0x02bf;
    L_0x02af:
        r2 = "stride";
        r4 = r12 + 32;
        r3.setInteger(r2, r4);	 Catch:{ Exception -> 0x02bc, all -> 0x0caa }
        r2 = "slice-height";
        r3.setInteger(r2, r11);	 Catch:{ Exception -> 0x02bc, all -> 0x0caa }
        goto L_0x02bf;
    L_0x02bc:
        r0 = move-exception;
        r1 = r0;
        goto L_0x028c;
    L_0x02bf:
        r15 = android.media.MediaCodec.createEncoderByType(r5);	 Catch:{ Exception -> 0x0bab, all -> 0x0caa }
        r2 = 0;
        r4 = 1;
        r15.configure(r3, r2, r2, r4);	 Catch:{ Exception -> 0x0b98, all -> 0x0caa }
        r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0b86, all -> 0x0caa }
        r3 = 18;
        if (r2 < r3) goto L_0x02f0;
    L_0x02ce:
        r2 = new org.telegram.messenger.video.InputSurface;	 Catch:{ Exception -> 0x02e5, all -> 0x0caa }
        r3 = r15.createInputSurface();	 Catch:{ Exception -> 0x02e5, all -> 0x0caa }
        r2.<init>(r3);	 Catch:{ Exception -> 0x02e5, all -> 0x0caa }
        r2.makeCurrent();	 Catch:{ Exception -> 0x02dc, all -> 0x0caa }
        r6 = r2;
        goto L_0x02f1;
    L_0x02dc:
        r0 = move-exception;
        r1 = r0;
        r6 = r2;
        r11 = r12;
        r64 = r28;
        r2 = 0;
        r4 = 0;
        goto L_0x02ed;
    L_0x02e5:
        r0 = move-exception;
        r1 = r0;
        r11 = r12;
        r64 = r28;
        r2 = 0;
        r4 = 0;
        r6 = 0;
    L_0x02ed:
        r13 = 0;
        goto L_0x012a;
    L_0x02f0:
        r6 = 0;
    L_0x02f1:
        r15.start();	 Catch:{ Exception -> 0x0b6f, all -> 0x0caa }
        r2 = "mime";
        r2 = r1.getString(r2);	 Catch:{ Exception -> 0x0b6f, all -> 0x0caa }
        r4 = android.media.MediaCodec.createDecoderByType(r2);	 Catch:{ Exception -> 0x0b6f, all -> 0x0caa }
        r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0b57, all -> 0x0caa }
        r3 = 18;
        if (r2 < r3) goto L_0x0311;
    L_0x0304:
        r2 = new org.telegram.messenger.video.OutputSurface;	 Catch:{ Exception -> 0x030a, all -> 0x0caa }
        r2.<init>();	 Catch:{ Exception -> 0x030a, all -> 0x0caa }
        goto L_0x0318;
    L_0x030a:
        r0 = move-exception;
        r1 = r0;
        r11 = r12;
        r64 = r28;
        r2 = 0;
        goto L_0x02ed;
    L_0x0311:
        r2 = new org.telegram.messenger.video.OutputSurface;	 Catch:{ Exception -> 0x0b57, all -> 0x0caa }
        r3 = r69;
        r2.<init>(r12, r11, r3);	 Catch:{ Exception -> 0x0b57, all -> 0x0caa }
    L_0x0318:
        r3 = r2.getSurface();	 Catch:{ Exception -> 0x0b3e, all -> 0x0caa }
        r73 = r9;
        r74 = r10;
        r9 = 0;
        r10 = 0;
        r4.configure(r1, r3, r9, r10);	 Catch:{ Exception -> 0x0b29, all -> 0x0caa }
        r4.start();	 Catch:{ Exception -> 0x0b29, all -> 0x0caa }
        r1 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0b29, all -> 0x0caa }
        r10 = 21;
        if (r1 >= r10) goto L_0x034f;
    L_0x032e:
        r1 = r4.getInputBuffers();	 Catch:{ Exception -> 0x0345, all -> 0x0caa }
        r3 = r15.getOutputBuffers();	 Catch:{ Exception -> 0x0345, all -> 0x0caa }
        r9 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0345, all -> 0x0caa }
        r10 = 18;
        if (r9 >= r10) goto L_0x0343;
    L_0x033c:
        r9 = r15.getInputBuffers();	 Catch:{ Exception -> 0x0345, all -> 0x0caa }
        r10 = r9;
        r9 = r1;
        goto L_0x0352;
    L_0x0343:
        r9 = r1;
        goto L_0x0351;
    L_0x0345:
        r0 = move-exception;
    L_0x0346:
        r9 = r73;
        r10 = r74;
        r1 = r0;
        r11 = r12;
        r64 = r28;
        goto L_0x02ed;
    L_0x034f:
        r3 = 0;
        r9 = 0;
    L_0x0351:
        r10 = 0;
    L_0x0352:
        if (r29 < 0) goto L_0x040c;
    L_0x0354:
        r1 = r14.extractor;	 Catch:{ Exception -> 0x0405, all -> 0x0caa }
        r35 = r3;
        r3 = r29;
        r1 = r1.getTrackFormat(r3);	 Catch:{ Exception -> 0x0405, all -> 0x0caa }
        r29 = r13;
        r13 = "mime";
        r13 = r1.getString(r13);	 Catch:{ Exception -> 0x0405, all -> 0x0caa }
        r44 = r10;
        r10 = "audio/mp4a-latm";
        r10 = r13.equals(r10);	 Catch:{ Exception -> 0x0405, all -> 0x0caa }
        r13 = r14.mediaMuxer;	 Catch:{ Exception -> 0x0405, all -> 0x0caa }
        r45 = r6;
        r6 = 1;
        r13 = r13.addTrack(r1, r6);	 Catch:{ Exception -> 0x03f6, all -> 0x0caa }
        if (r10 == 0) goto L_0x03b5;
    L_0x0379:
        r6 = r14.extractor;	 Catch:{ Exception -> 0x03b3, all -> 0x0caa }
        r6.selectTrack(r3);	 Catch:{ Exception -> 0x03b3, all -> 0x0caa }
        r6 = "max-input-size";
        r1 = r1.getInteger(r6);	 Catch:{ Exception -> 0x03b3, all -> 0x0caa }
        r1 = java.nio.ByteBuffer.allocateDirect(r1);	 Catch:{ Exception -> 0x03b3, all -> 0x0caa }
        r33 = 0;
        r6 = (r7 > r33 ? 1 : (r7 == r33 ? 0 : -1));
        if (r6 <= 0) goto L_0x039b;
    L_0x038e:
        r6 = r14.extractor;	 Catch:{ Exception -> 0x03b3, all -> 0x0caa }
        r36 = r1;
        r1 = 0;
        r6.seekTo(r7, r1);	 Catch:{ Exception -> 0x03b3, all -> 0x0caa }
        r46 = r5;
        r37 = r10;
        goto L_0x03a9;
    L_0x039b:
        r36 = r1;
        r1 = r14.extractor;	 Catch:{ Exception -> 0x03b3, all -> 0x0caa }
        r46 = r5;
        r37 = r10;
        r5 = 0;
        r10 = 0;
        r1.seekTo(r5, r10);	 Catch:{ Exception -> 0x03b3, all -> 0x0caa }
    L_0x03a9:
        r10 = r77;
        r6 = r13;
        r5 = r36;
        r47 = r37;
        r13 = 0;
        goto L_0x041f;
    L_0x03b3:
        r0 = move-exception;
        goto L_0x03f9;
    L_0x03b5:
        r46 = r5;
        r37 = r10;
        r5 = new android.media.MediaExtractor;	 Catch:{ Exception -> 0x03f6, all -> 0x0caa }
        r5.<init>();	 Catch:{ Exception -> 0x03f6, all -> 0x0caa }
        r10 = r67;
        r5.setDataSource(r10);	 Catch:{ Exception -> 0x03f6, all -> 0x0caa }
        r5.selectTrack(r3);	 Catch:{ Exception -> 0x03f6, all -> 0x0caa }
        r10 = 0;
        r6 = (r7 > r10 ? 1 : (r7 == r10 ? 0 : -1));
        if (r6 <= 0) goto L_0x03d1;
    L_0x03cc:
        r6 = 0;
        r5.seekTo(r7, r6);	 Catch:{ Exception -> 0x03b3, all -> 0x0caa }
        goto L_0x03d5;
    L_0x03d1:
        r6 = 0;
        r5.seekTo(r10, r6);	 Catch:{ Exception -> 0x03f6, all -> 0x0caa }
    L_0x03d5:
        r6 = new org.telegram.messenger.video.AudioRecoder;	 Catch:{ Exception -> 0x03f6, all -> 0x0caa }
        r6.<init>(r1, r5, r3);	 Catch:{ Exception -> 0x03f6, all -> 0x0caa }
        r6.startTime = r7;	 Catch:{ Exception -> 0x03eb, all -> 0x0caa }
        r10 = r77;
        r6.endTime = r10;	 Catch:{ Exception -> 0x03e9, all -> 0x0caa }
        r47 = r37;
        r5 = 0;
        r65 = r13;
        r13 = r6;
        r6 = r65;
        goto L_0x041f;
    L_0x03e9:
        r0 = move-exception;
        goto L_0x03ee;
    L_0x03eb:
        r0 = move-exception;
        r10 = r77;
    L_0x03ee:
        r9 = r73;
        r10 = r74;
        r1 = r0;
        r13 = r6;
        goto L_0x0466;
    L_0x03f6:
        r0 = move-exception;
        r10 = r77;
    L_0x03f9:
        r9 = r73;
        r10 = r74;
        r1 = r0;
        r11 = r12;
        r64 = r28;
        r6 = r45;
        goto L_0x02ed;
    L_0x0405:
        r0 = move-exception;
        r10 = r77;
        r45 = r6;
        goto L_0x0346;
    L_0x040c:
        r35 = r3;
        r46 = r5;
        r45 = r6;
        r44 = r10;
        r3 = r29;
        r10 = r77;
        r29 = r13;
        r5 = 0;
        r6 = -5;
        r13 = 0;
        r47 = 1;
    L_0x041f:
        r66.checkConversionCanceled();	 Catch:{ Exception -> 0x0b10, all -> 0x0caa }
        r53 = r20;
        r50 = r35;
        r1 = 0;
        r20 = 0;
        r21 = 0;
        r48 = 0;
        r49 = -5;
        r51 = 0;
        r52 = 1;
        r55 = 0;
    L_0x0435:
        if (r1 == 0) goto L_0x044e;
    L_0x0437:
        if (r47 != 0) goto L_0x043c;
    L_0x0439:
        if (r20 != 0) goto L_0x043c;
    L_0x043b:
        goto L_0x044e;
    L_0x043c:
        r7 = r72;
        r9 = r73;
        r10 = r74;
        r11 = r12;
        r64 = r28;
        r6 = r45;
        r3 = 0;
        r17 = 0;
        r30 = 1;
        goto L_0x0CLASSNAME;
    L_0x044e:
        r66.checkConversionCanceled();	 Catch:{ Exception -> 0x0b10, all -> 0x0caa }
        if (r47 != 0) goto L_0x046d;
    L_0x0453:
        if (r13 == 0) goto L_0x046d;
    L_0x0455:
        r57 = r1;
        r1 = r14.mediaMuxer;	 Catch:{ Exception -> 0x0460, all -> 0x0caa }
        r1 = r13.step(r1, r6);	 Catch:{ Exception -> 0x0460, all -> 0x0caa }
        r20 = r1;
        goto L_0x046f;
    L_0x0460:
        r0 = move-exception;
        r9 = r73;
        r10 = r74;
        r1 = r0;
    L_0x0466:
        r11 = r12;
        r64 = r28;
        r6 = r45;
        goto L_0x012a;
    L_0x046d:
        r57 = r1;
    L_0x046f:
        r58 = r2;
        if (r21 != 0) goto L_0x05cc;
    L_0x0473:
        r1 = r14.extractor;	 Catch:{ Exception -> 0x05b8, all -> 0x0caa }
        r1 = r1.getSampleTrackIndex();	 Catch:{ Exception -> 0x05b8, all -> 0x0caa }
        r2 = r28;
        if (r1 != r2) goto L_0x04e8;
    L_0x047d:
        r28 = r13;
        r12 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r1 = r4.dequeueInputBuffer(r12);	 Catch:{ Exception -> 0x04d6, all -> 0x0caa }
        if (r1 < 0) goto L_0x04c8;
    L_0x0487:
        r12 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x04d6, all -> 0x0caa }
        r13 = 21;
        if (r12 >= r13) goto L_0x0490;
    L_0x048d:
        r12 = r9[r1];	 Catch:{ Exception -> 0x04d6, all -> 0x0caa }
        goto L_0x0494;
    L_0x0490:
        r12 = r4.getInputBuffer(r1);	 Catch:{ Exception -> 0x04d6, all -> 0x0caa }
    L_0x0494:
        r13 = r14.extractor;	 Catch:{ Exception -> 0x04d6, all -> 0x0caa }
        r61 = r2;
        r2 = 0;
        r38 = r13.readSampleData(r12, r2);	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        if (r38 >= 0) goto L_0x04b1;
    L_0x049f:
        r37 = 0;
        r38 = 0;
        r39 = 0;
        r41 = 4;
        r35 = r4;
        r36 = r1;
        r35.queueInputBuffer(r36, r37, r38, r39, r41);	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        r21 = 1;
        goto L_0x04ca;
    L_0x04b1:
        r37 = 0;
        r2 = r14.extractor;	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        r39 = r2.getSampleTime();	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        r41 = 0;
        r35 = r4;
        r36 = r1;
        r35.queueInputBuffer(r36, r37, r38, r39, r41);	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        r1 = r14.extractor;	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        r1.advance();	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        goto L_0x04ca;
    L_0x04c8:
        r61 = r2;
    L_0x04ca:
        r13 = r3;
        r62 = r5;
        r63 = r9;
        r12 = r22;
        r1 = 0;
        r22 = r6;
        goto L_0x0593;
    L_0x04d6:
        r0 = move-exception;
        r11 = r71;
        r9 = r73;
        r10 = r74;
        r1 = r0;
        r64 = r2;
        r13 = r28;
        r6 = r45;
        r2 = r58;
        goto L_0x012a;
    L_0x04e8:
        r61 = r2;
        r28 = r13;
        if (r47 == 0) goto L_0x0584;
    L_0x04ee:
        r2 = -1;
        if (r3 == r2) goto L_0x057a;
    L_0x04f1:
        if (r1 != r3) goto L_0x0584;
    L_0x04f3:
        r1 = r14.extractor;	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        r2 = 0;
        r1 = r1.readSampleData(r5, r2);	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        r12 = r22;
        r12.size = r1;	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        r1 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        r13 = 21;
        if (r1 >= r13) goto L_0x050c;
    L_0x0504:
        r5.position(r2);	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        r1 = r12.size;	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        r5.limit(r1);	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
    L_0x050c:
        r1 = r12.size;	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        if (r1 < 0) goto L_0x051e;
    L_0x0510:
        r1 = r14.extractor;	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        r1 = r1.getSampleTime();	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        r12.presentationTimeUs = r1;	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        r1 = r14.extractor;	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        r1.advance();	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        goto L_0x0523;
    L_0x051e:
        r1 = 0;
        r12.size = r1;	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        r21 = 1;
    L_0x0523:
        r1 = r12.size;	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        if (r1 <= 0) goto L_0x0578;
    L_0x0527:
        r1 = 0;
        r13 = (r10 > r1 ? 1 : (r10 == r1 ? 0 : -1));
        if (r13 < 0) goto L_0x0533;
    L_0x052d:
        r1 = r12.presentationTimeUs;	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        r13 = (r1 > r10 ? 1 : (r1 == r10 ? 0 : -1));
        if (r13 >= 0) goto L_0x0578;
    L_0x0533:
        r1 = 0;
        r12.offset = r1;	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        r2 = r14.extractor;	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        r2 = r2.getSampleFlags();	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        r12.flags = r2;	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        r2 = r14.mediaMuxer;	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        r13 = r3;
        r2 = r2.writeSampleData(r6, r5, r12, r1);	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        r33 = 0;
        r1 = (r2 > r33 ? 1 : (r2 == r33 ? 0 : -1));
        if (r1 == 0) goto L_0x0571;
    L_0x054b:
        r1 = r14.callback;	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        if (r1 == 0) goto L_0x0571;
    L_0x054f:
        r62 = r5;
        r22 = r6;
        r5 = r12.presentationTimeUs;	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        r5 = r5 - r7;
        r1 = (r5 > r55 ? 1 : (r5 == r55 ? 0 : -1));
        if (r1 <= 0) goto L_0x055e;
    L_0x055a:
        r5 = r12.presentationTimeUs;	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        r55 = r5 - r7;
    L_0x055e:
        r5 = r55;
        r1 = r14.callback;	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        r63 = r9;
        r9 = (float) r5;	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        r35 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r9 = r9 / r35;
        r9 = r9 / r18;
        r1.didWriteData(r2, r9);	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        r55 = r5;
        goto L_0x0592;
    L_0x0571:
        r62 = r5;
        r22 = r6;
        r63 = r9;
        goto L_0x0592;
    L_0x0578:
        r13 = r3;
        goto L_0x0571;
    L_0x057a:
        r13 = r3;
        r62 = r5;
        r63 = r9;
        r12 = r22;
        r22 = r6;
        goto L_0x058e;
    L_0x0584:
        r13 = r3;
        r62 = r5;
        r63 = r9;
        r12 = r22;
        r22 = r6;
        r2 = -1;
    L_0x058e:
        if (r1 != r2) goto L_0x0592;
    L_0x0590:
        r1 = 1;
        goto L_0x0593;
    L_0x0592:
        r1 = 0;
    L_0x0593:
        if (r1 == 0) goto L_0x05d9;
    L_0x0595:
        r1 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r36 = r4.dequeueInputBuffer(r1);	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        if (r36 < 0) goto L_0x05d9;
    L_0x059d:
        r37 = 0;
        r38 = 0;
        r39 = 0;
        r41 = 4;
        r35 = r4;
        r35.queueInputBuffer(r36, r37, r38, r39, r41);	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        r21 = 1;
        goto L_0x05d9;
    L_0x05ad:
        r0 = move-exception;
        r11 = r71;
    L_0x05b0:
        r9 = r73;
        r10 = r74;
        r1 = r0;
        r13 = r28;
        goto L_0x05c4;
    L_0x05b8:
        r0 = move-exception;
        r61 = r28;
        r28 = r13;
        r11 = r71;
        r9 = r73;
        r10 = r74;
        r1 = r0;
    L_0x05c4:
        r6 = r45;
        r2 = r58;
        r64 = r61;
        goto L_0x012a;
    L_0x05cc:
        r62 = r5;
        r63 = r9;
        r12 = r22;
        r61 = r28;
        r22 = r6;
        r28 = r13;
        r13 = r3;
    L_0x05d9:
        r1 = r48 ^ 1;
        r9 = r1;
        r2 = r49;
        r1 = 1;
    L_0x05df:
        if (r9 != 0) goto L_0x05fb;
    L_0x05e1:
        if (r1 == 0) goto L_0x05e4;
    L_0x05e3:
        goto L_0x05fb;
    L_0x05e4:
        r49 = r2;
        r3 = r13;
        r6 = r22;
        r13 = r28;
        r1 = r57;
        r2 = r58;
        r28 = r61;
        r5 = r62;
        r9 = r63;
        r22 = r12;
        r12 = r71;
        goto L_0x0435;
    L_0x05fb:
        r66.checkConversionCanceled();	 Catch:{ Exception -> 0x0af4, all -> 0x0caa }
        if (r82 == 0) goto L_0x0603;
    L_0x0600:
        r5 = 22000; // 0x55f0 float:3.0829E-41 double:1.08694E-319;
        goto L_0x0605;
    L_0x0603:
        r5 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
    L_0x0605:
        r3 = r15.dequeueOutputBuffer(r12, r5);	 Catch:{ Exception -> 0x0af4, all -> 0x0caa }
        r5 = -1;
        if (r3 != r5) goto L_0x061b;
    L_0x060c:
        r11 = r71;
        r6 = r72;
        r40 = r9;
        r5 = r46;
        r1 = -1;
        r10 = 0;
    L_0x0616:
        r25 = 2;
        r9 = r2;
        goto L_0x07ed;
    L_0x061b:
        r5 = -3;
        if (r3 != r5) goto L_0x0633;
    L_0x061e:
        r5 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        r6 = 21;
        if (r5 >= r6) goto L_0x0628;
    L_0x0624:
        r50 = r15.getOutputBuffers();	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
    L_0x0628:
        r11 = r71;
        r6 = r72;
        r10 = r1;
        r40 = r9;
    L_0x062f:
        r5 = r46;
        r1 = -1;
        goto L_0x0616;
    L_0x0633:
        r5 = -2;
        if (r3 != r5) goto L_0x067c;
    L_0x0636:
        r5 = r15.getOutputFormat();	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        r6 = -5;
        if (r2 != r6) goto L_0x0671;
    L_0x063d:
        if (r5 == 0) goto L_0x0671;
    L_0x063f:
        r2 = r14.mediaMuxer;	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        r6 = 0;
        r2 = r2.addTrack(r5, r6);	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        r6 = "prepend-sps-pps-to-idr-frames";
        r6 = r5.containsKey(r6);	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        if (r6 == 0) goto L_0x0671;
    L_0x064e:
        r6 = "prepend-sps-pps-to-idr-frames";
        r6 = r5.getInteger(r6);	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        r35 = r1;
        r1 = 1;
        if (r6 != r1) goto L_0x0673;
    L_0x0659:
        r1 = "csd-0";
        r1 = r5.getByteBuffer(r1);	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        r6 = "csd-1";
        r5 = r5.getByteBuffer(r6);	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        r1 = r1.limit();	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        r5 = r5.limit();	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        r1 = r1 + r5;
        r51 = r1;
        goto L_0x0673;
    L_0x0671:
        r35 = r1;
    L_0x0673:
        r11 = r71;
        r6 = r72;
        r40 = r9;
        r10 = r35;
        goto L_0x062f;
    L_0x067c:
        r35 = r1;
        if (r3 < 0) goto L_0x0ac5;
    L_0x0680:
        r1 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0af4, all -> 0x0caa }
        r6 = 21;
        if (r1 >= r6) goto L_0x0689;
    L_0x0686:
        r1 = r50[r3];	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        goto L_0x068d;
    L_0x0689:
        r1 = r15.getOutputBuffer(r3);	 Catch:{ Exception -> 0x0af4, all -> 0x0caa }
    L_0x068d:
        if (r1 == 0) goto L_0x0a9b;
    L_0x068f:
        r5 = r12.size;	 Catch:{ Exception -> 0x0af4, all -> 0x0caa }
        r6 = 1;
        if (r5 <= r6) goto L_0x07cf;
    L_0x0694:
        r5 = r12.flags;	 Catch:{ Exception -> 0x07c8, all -> 0x0caa }
        r25 = 2;
        r5 = r5 & 2;
        if (r5 != 0) goto L_0x0743;
    L_0x069c:
        if (r51 == 0) goto L_0x06af;
    L_0x069e:
        r5 = r12.flags;	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        r5 = r5 & r6;
        if (r5 == 0) goto L_0x06af;
    L_0x06a3:
        r5 = r12.offset;	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        r5 = r5 + r51;
        r12.offset = r5;	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        r5 = r12.size;	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        r5 = r5 - r51;
        r12.size = r5;	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
    L_0x06af:
        if (r52 == 0) goto L_0x070a;
    L_0x06b1:
        r5 = r12.flags;	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        r6 = 1;
        r5 = r5 & r6;
        if (r5 == 0) goto L_0x070a;
    L_0x06b7:
        r5 = r12.size;	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        r6 = 100;
        if (r5 <= r6) goto L_0x0705;
    L_0x06bd:
        r5 = r12.offset;	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        r1.position(r5);	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        r5 = 100;
        r5 = new byte[r5];	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        r1.get(r5);	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        r40 = r9;
        r6 = 0;
        r36 = 0;
    L_0x06ce:
        r9 = r5.length;	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        r9 = r9 + -4;
        if (r6 >= r9) goto L_0x0707;
    L_0x06d3:
        r9 = r5[r6];	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        if (r9 != 0) goto L_0x06fe;
    L_0x06d7:
        r9 = r6 + 1;
        r9 = r5[r9];	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        if (r9 != 0) goto L_0x06fe;
    L_0x06dd:
        r9 = r6 + 2;
        r9 = r5[r9];	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        if (r9 != 0) goto L_0x06fe;
    L_0x06e3:
        r9 = r6 + 3;
        r9 = r5[r9];	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        r37 = r5;
        r5 = 1;
        if (r9 != r5) goto L_0x0700;
    L_0x06ec:
        r9 = r36 + 1;
        if (r9 <= r5) goto L_0x06fb;
    L_0x06f0:
        r5 = r12.offset;	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        r5 = r5 + r6;
        r12.offset = r5;	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        r5 = r12.size;	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        r5 = r5 - r6;
        r12.size = r5;	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        goto L_0x0707;
    L_0x06fb:
        r36 = r9;
        goto L_0x0700;
    L_0x06fe:
        r37 = r5;
    L_0x0700:
        r6 = r6 + 1;
        r5 = r37;
        goto L_0x06ce;
    L_0x0705:
        r40 = r9;
    L_0x0707:
        r52 = 0;
        goto L_0x070c;
    L_0x070a:
        r40 = r9;
    L_0x070c:
        r5 = r14.mediaMuxer;	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        r6 = 1;
        r9 = r5.writeSampleData(r2, r1, r12, r6);	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        r5 = 0;
        r1 = (r9 > r5 ? 1 : (r9 == r5 ? 0 : -1));
        if (r1 == 0) goto L_0x0737;
    L_0x0719:
        r1 = r14.callback;	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        if (r1 == 0) goto L_0x0737;
    L_0x071d:
        r5 = r12.presentationTimeUs;	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        r5 = r5 - r7;
        r1 = (r5 > r55 ? 1 : (r5 == r55 ? 0 : -1));
        if (r1 <= 0) goto L_0x0728;
    L_0x0724:
        r5 = r12.presentationTimeUs;	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        r55 = r5 - r7;
    L_0x0728:
        r5 = r55;
        r1 = r14.callback;	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        r11 = (float) r5;	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        r36 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r11 = r11 / r36;
        r11 = r11 / r18;
        r1.didWriteData(r9, r11);	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        goto L_0x0739;
    L_0x0737:
        r5 = r55;
    L_0x0739:
        r11 = r71;
        r55 = r5;
        r5 = r46;
        r6 = r72;
        goto L_0x07d9;
    L_0x0743:
        r40 = r9;
        r5 = -5;
        if (r2 != r5) goto L_0x07c1;
    L_0x0748:
        r2 = r12.size;	 Catch:{ Exception -> 0x07c8, all -> 0x0caa }
        r2 = new byte[r2];	 Catch:{ Exception -> 0x07c8, all -> 0x0caa }
        r6 = r12.offset;	 Catch:{ Exception -> 0x07c8, all -> 0x0caa }
        r9 = r12.size;	 Catch:{ Exception -> 0x07c8, all -> 0x0caa }
        r6 = r6 + r9;
        r1.limit(r6);	 Catch:{ Exception -> 0x07c8, all -> 0x0caa }
        r6 = r12.offset;	 Catch:{ Exception -> 0x07c8, all -> 0x0caa }
        r1.position(r6);	 Catch:{ Exception -> 0x07c8, all -> 0x0caa }
        r1.get(r2);	 Catch:{ Exception -> 0x07c8, all -> 0x0caa }
        r1 = r12.size;	 Catch:{ Exception -> 0x07c8, all -> 0x0caa }
        r6 = 1;
        r1 = r1 - r6;
    L_0x0760:
        r9 = 3;
        if (r1 < 0) goto L_0x079e;
    L_0x0763:
        if (r1 <= r9) goto L_0x079e;
    L_0x0765:
        r10 = r2[r1];	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        if (r10 != r6) goto L_0x0799;
    L_0x0769:
        r6 = r1 + -1;
        r6 = r2[r6];	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        if (r6 != 0) goto L_0x0799;
    L_0x076f:
        r6 = r1 + -2;
        r6 = r2[r6];	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        if (r6 != 0) goto L_0x0799;
    L_0x0775:
        r6 = r1 + -3;
        r10 = r2[r6];	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        if (r10 != 0) goto L_0x0799;
    L_0x077b:
        r1 = java.nio.ByteBuffer.allocate(r6);	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        r10 = r12.size;	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        r10 = r10 - r6;
        r10 = java.nio.ByteBuffer.allocate(r10);	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        r11 = 0;
        r5 = r1.put(r2, r11, r6);	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        r5.position(r11);	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        r5 = r12.size;	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        r5 = r5 - r6;
        r2 = r10.put(r2, r6, r5);	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        r2.position(r11);	 Catch:{ Exception -> 0x05ad, all -> 0x0caa }
        goto L_0x07a0;
    L_0x0799:
        r1 = r1 + -1;
        r5 = -5;
        r6 = 1;
        goto L_0x0760;
    L_0x079e:
        r1 = 0;
        r10 = 0;
    L_0x07a0:
        r11 = r71;
        r6 = r72;
        r5 = r46;
        r2 = android.media.MediaFormat.createVideoFormat(r5, r11, r6);	 Catch:{ Exception -> 0x0859, all -> 0x0caa }
        if (r1 == 0) goto L_0x07b8;
    L_0x07ac:
        if (r10 == 0) goto L_0x07b8;
    L_0x07ae:
        r9 = "csd-0";
        r2.setByteBuffer(r9, r1);	 Catch:{ Exception -> 0x0859, all -> 0x0caa }
        r1 = "csd-1";
        r2.setByteBuffer(r1, r10);	 Catch:{ Exception -> 0x0859, all -> 0x0caa }
    L_0x07b8:
        r1 = r14.mediaMuxer;	 Catch:{ Exception -> 0x0859, all -> 0x0caa }
        r9 = 0;
        r1 = r1.addTrack(r2, r9);	 Catch:{ Exception -> 0x0859, all -> 0x0caa }
        r2 = r1;
        goto L_0x07d9;
    L_0x07c1:
        r11 = r71;
        r6 = r72;
        r5 = r46;
        goto L_0x07d9;
    L_0x07c8:
        r0 = move-exception;
        r11 = r71;
        r6 = r72;
        goto L_0x05b0;
    L_0x07cf:
        r11 = r71;
        r6 = r72;
        r40 = r9;
        r5 = r46;
        r25 = 2;
    L_0x07d9:
        r1 = r12.flags;	 Catch:{ Exception -> 0x0a66, all -> 0x0caa }
        r1 = r1 & 4;
        if (r1 == 0) goto L_0x07e3;
    L_0x07df:
        r1 = 0;
        r57 = 1;
        goto L_0x07e6;
    L_0x07e3:
        r1 = 0;
        r57 = 0;
    L_0x07e6:
        r15.releaseOutputBuffer(r3, r1);	 Catch:{ Exception -> 0x0a66, all -> 0x0caa }
        r9 = r2;
        r10 = r35;
        r1 = -1;
    L_0x07ed:
        if (r3 == r1) goto L_0x07f9;
    L_0x07ef:
        r46 = r5;
        r2 = r9;
        r1 = r10;
        r9 = r40;
    L_0x07f5:
        r10 = r77;
        goto L_0x05df;
    L_0x07f9:
        if (r48 != 0) goto L_0x0a69;
    L_0x07fb:
        r46 = r5;
        r2 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r5 = r4.dequeueOutputBuffer(r12, r2);	 Catch:{ Exception -> 0x0a66, all -> 0x0caa }
        if (r5 != r1) goto L_0x082c;
    L_0x0805:
        r17 = r4;
        r43 = r9;
        r23 = r45;
        r60 = r46;
        r49 = r58;
        r64 = r61;
        r25 = r62;
        r1 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r8 = 18;
        r9 = -1;
        r16 = 0;
        r19 = -5;
        r27 = 2;
        r30 = 1;
        r40 = 0;
    L_0x0822:
        r42 = 21;
        r45 = 0;
        r46 = 3;
        r58 = 0;
        goto L_0x0a86;
    L_0x082c:
        r2 = -3;
        if (r5 != r2) goto L_0x0839;
    L_0x082f:
        r17 = r4;
        r43 = r9;
        r23 = r45;
        r60 = r46;
        goto L_0x0a71;
    L_0x0839:
        r2 = -2;
        if (r5 != r2) goto L_0x085c;
    L_0x083c:
        r2 = r4.getOutputFormat();	 Catch:{ Exception -> 0x0859, all -> 0x0caa }
        r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x0859, all -> 0x0caa }
        if (r3 == 0) goto L_0x082f;
    L_0x0844:
        r3 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0859, all -> 0x0caa }
        r3.<init>();	 Catch:{ Exception -> 0x0859, all -> 0x0caa }
        r5 = "newFormat = ";
        r3.append(r5);	 Catch:{ Exception -> 0x0859, all -> 0x0caa }
        r3.append(r2);	 Catch:{ Exception -> 0x0859, all -> 0x0caa }
        r2 = r3.toString();	 Catch:{ Exception -> 0x0859, all -> 0x0caa }
        org.telegram.messenger.FileLog.d(r2);	 Catch:{ Exception -> 0x0859, all -> 0x0caa }
        goto L_0x082f;
    L_0x0859:
        r0 = move-exception;
        goto L_0x05b0;
    L_0x085c:
        if (r5 < 0) goto L_0x0a42;
    L_0x085e:
        r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0a66, all -> 0x0caa }
        r3 = 18;
        if (r2 < r3) goto L_0x086f;
    L_0x0864:
        r2 = r12.size;	 Catch:{ Exception -> 0x0859, all -> 0x0caa }
        if (r2 == 0) goto L_0x086a;
    L_0x0868:
        r2 = 1;
        goto L_0x086b;
    L_0x086a:
        r2 = 0;
    L_0x086b:
        r3 = r2;
        r33 = 0;
        goto L_0x0882;
    L_0x086f:
        r2 = r12.size;	 Catch:{ Exception -> 0x0a66, all -> 0x0caa }
        if (r2 != 0) goto L_0x087e;
    L_0x0873:
        r2 = r12.presentationTimeUs;	 Catch:{ Exception -> 0x0859, all -> 0x0caa }
        r33 = 0;
        r19 = (r2 > r33 ? 1 : (r2 == r33 ? 0 : -1));
        if (r19 == 0) goto L_0x087c;
    L_0x087b:
        goto L_0x0880;
    L_0x087c:
        r2 = 0;
        goto L_0x0881;
    L_0x087e:
        r33 = 0;
    L_0x0880:
        r2 = 1;
    L_0x0881:
        r3 = r2;
    L_0x0882:
        r2 = (r77 > r33 ? 1 : (r77 == r33 ? 0 : -1));
        if (r2 <= 0) goto L_0x089a;
    L_0x0886:
        r1 = r12.presentationTimeUs;	 Catch:{ Exception -> 0x0859, all -> 0x0caa }
        r35 = (r1 > r77 ? 1 : (r1 == r77 ? 0 : -1));
        if (r35 < 0) goto L_0x089a;
    L_0x088c:
        r1 = r12.flags;	 Catch:{ Exception -> 0x0859, all -> 0x0caa }
        r1 = r1 | 4;
        r12.flags = r1;	 Catch:{ Exception -> 0x0859, all -> 0x0caa }
        r3 = 0;
        r21 = 1;
        r33 = 0;
        r48 = 1;
        goto L_0x089c;
    L_0x089a:
        r33 = 0;
    L_0x089c:
        r1 = (r7 > r33 ? 1 : (r7 == r33 ? 0 : -1));
        if (r1 <= 0) goto L_0x08d4;
    L_0x08a0:
        r1 = -1;
        r35 = (r53 > r1 ? 1 : (r53 == r1 ? 0 : -1));
        if (r35 != 0) goto L_0x08d4;
    L_0x08a6:
        r1 = r12.presentationTimeUs;	 Catch:{ Exception -> 0x0859, all -> 0x0caa }
        r35 = (r1 > r7 ? 1 : (r1 == r7 ? 0 : -1));
        if (r35 >= 0) goto L_0x08d0;
    L_0x08ac:
        r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x0859, all -> 0x0caa }
        if (r1 == 0) goto L_0x08ce;
    L_0x08b0:
        r1 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0859, all -> 0x0caa }
        r1.<init>();	 Catch:{ Exception -> 0x0859, all -> 0x0caa }
        r2 = "drop frame startTime = ";
        r1.append(r2);	 Catch:{ Exception -> 0x0859, all -> 0x0caa }
        r1.append(r7);	 Catch:{ Exception -> 0x0859, all -> 0x0caa }
        r2 = " present time = ";
        r1.append(r2);	 Catch:{ Exception -> 0x0859, all -> 0x0caa }
        r2 = r12.presentationTimeUs;	 Catch:{ Exception -> 0x0859, all -> 0x0caa }
        r1.append(r2);	 Catch:{ Exception -> 0x0859, all -> 0x0caa }
        r1 = r1.toString();	 Catch:{ Exception -> 0x0859, all -> 0x0caa }
        org.telegram.messenger.FileLog.d(r1);	 Catch:{ Exception -> 0x0859, all -> 0x0caa }
    L_0x08ce:
        r3 = 0;
        goto L_0x08d4;
    L_0x08d0:
        r1 = r12.presentationTimeUs;	 Catch:{ Exception -> 0x0859, all -> 0x0caa }
        r53 = r1;
    L_0x08d4:
        r4.releaseOutputBuffer(r5, r3);	 Catch:{ Exception -> 0x0a66, all -> 0x0caa }
        if (r3 == 0) goto L_0x09eb;
    L_0x08d9:
        r58.awaitNewImage();	 Catch:{ Exception -> 0x08de, all -> 0x0caa }
        r1 = 0;
        goto L_0x08e4;
    L_0x08de:
        r0 = move-exception;
        r1 = r0;
        org.telegram.messenger.FileLog.e(r1);	 Catch:{ Exception -> 0x0a66, all -> 0x0caa }
        r1 = 1;
    L_0x08e4:
        if (r1 != 0) goto L_0x09eb;
    L_0x08e6:
        r1 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0a66, all -> 0x0caa }
        r5 = 18;
        if (r1 < r5) goto L_0x0945;
    L_0x08ec:
        r2 = r58;
        r3 = 0;
        r2.drawImage(r3);	 Catch:{ Exception -> 0x0930, all -> 0x0caa }
        r17 = r4;
        r3 = r12.presentationTimeUs;	 Catch:{ Exception -> 0x0925, all -> 0x0caa }
        r35 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r3 = r3 * r35;
        r1 = r45;
        r1.setPresentationTime(r3);	 Catch:{ Exception -> 0x0923, all -> 0x0caa }
        r1.swapBuffers();	 Catch:{ Exception -> 0x0923, all -> 0x0caa }
        r23 = r1;
        r49 = r2;
        r43 = r9;
        r58 = r33;
        r60 = r46;
        r64 = r61;
        r25 = r62;
        r8 = 18;
        r9 = -1;
        r16 = 0;
        r19 = -5;
        r27 = 2;
        r30 = 1;
        r42 = 21;
        r45 = 0;
        r46 = 3;
        goto L_0x0a0c;
    L_0x0923:
        r0 = move-exception;
        goto L_0x0928;
    L_0x0925:
        r0 = move-exception;
        r1 = r45;
    L_0x0928:
        r9 = r73;
        r10 = r74;
        r6 = r1;
        r4 = r17;
        goto L_0x093a;
    L_0x0930:
        r0 = move-exception;
        r17 = r4;
        r1 = r45;
        r9 = r73;
        r10 = r74;
        r6 = r1;
    L_0x093a:
        r13 = r28;
        r64 = r61;
        r16 = 0;
        r30 = 1;
        r1 = r0;
        goto L_0x0bd4;
    L_0x0945:
        r17 = r4;
        r1 = r45;
        r2 = r58;
        r3 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r35 = r15.dequeueInputBuffer(r3);	 Catch:{ Exception -> 0x09d3, all -> 0x0caa }
        if (r35 < 0) goto L_0x09aa;
    L_0x0953:
        r4 = 1;
        r2.drawImage(r4);	 Catch:{ Exception -> 0x09d3, all -> 0x0caa }
        r3 = r2.getFrame();	 Catch:{ Exception -> 0x09d3, all -> 0x0caa }
        r16 = r44[r35];	 Catch:{ Exception -> 0x09d3, all -> 0x0caa }
        r16.clear();	 Catch:{ Exception -> 0x09d3, all -> 0x0caa }
        r23 = r1;
        r43 = r9;
        r7 = -1;
        r8 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r19 = -5;
        r45 = 0;
        r1 = r3;
        r49 = r2;
        r58 = r33;
        r33 = r61;
        r2 = r16;
        r16 = 0;
        r3 = r29;
        r64 = r33;
        r30 = 1;
        r4 = r71;
        r60 = r46;
        r25 = r62;
        r27 = 2;
        r33 = 18;
        r46 = 3;
        r5 = r72;
        r8 = 18;
        r42 = 21;
        r6 = r31;
        r9 = -1;
        r7 = r32;
        org.telegram.messenger.Utilities.convertVideoFrame(r1, r2, r3, r4, r5, r6, r7);	 Catch:{ Exception -> 0x0aeb, all -> 0x0caa }
        r1 = 0;
        r2 = r12.presentationTimeUs;	 Catch:{ Exception -> 0x0aeb, all -> 0x0caa }
        r39 = 0;
        r33 = r15;
        r34 = r35;
        r35 = r1;
        r36 = r24;
        r37 = r2;
        r33.queueInputBuffer(r34, r35, r36, r37, r39);	 Catch:{ Exception -> 0x0aeb, all -> 0x0caa }
        goto L_0x0a0c;
    L_0x09aa:
        r23 = r1;
        r49 = r2;
        r43 = r9;
        r58 = r33;
        r60 = r46;
        r64 = r61;
        r25 = r62;
        r8 = 18;
        r9 = -1;
        r16 = 0;
        r19 = -5;
        r27 = 2;
        r30 = 1;
        r42 = 21;
        r45 = 0;
        r46 = 3;
        r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x0aeb, all -> 0x0caa }
        if (r1 == 0) goto L_0x0a0c;
    L_0x09cd:
        r1 = "input buffer not available";
        org.telegram.messenger.FileLog.d(r1);	 Catch:{ Exception -> 0x0aeb, all -> 0x0caa }
        goto L_0x0a0c;
    L_0x09d3:
        r0 = move-exception;
        r23 = r1;
        r49 = r2;
        r64 = r61;
        r16 = 0;
        r30 = 1;
        r9 = r73;
        r10 = r74;
        r1 = r0;
        r4 = r17;
        r6 = r23;
        r13 = r28;
        goto L_0x0bd4;
    L_0x09eb:
        r17 = r4;
        r43 = r9;
        r23 = r45;
        r60 = r46;
        r49 = r58;
        r64 = r61;
        r25 = r62;
        r8 = 18;
        r9 = -1;
        r16 = 0;
        r19 = -5;
        r27 = 2;
        r30 = 1;
        r42 = 21;
        r45 = 0;
        r46 = 3;
        r58 = r33;
    L_0x0a0c:
        r1 = r12.flags;	 Catch:{ Exception -> 0x0aeb, all -> 0x0caa }
        r1 = r1 & 4;
        if (r1 == 0) goto L_0x0a3f;
    L_0x0a12:
        r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x0aeb, all -> 0x0caa }
        if (r1 == 0) goto L_0x0a1b;
    L_0x0a16:
        r1 = "decoder stream end";
        org.telegram.messenger.FileLog.d(r1);	 Catch:{ Exception -> 0x0aeb, all -> 0x0caa }
    L_0x0a1b:
        r1 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0aeb, all -> 0x0caa }
        if (r1 < r8) goto L_0x0a25;
    L_0x0a1f:
        r15.signalEndOfInputStream();	 Catch:{ Exception -> 0x0aeb, all -> 0x0caa }
        r1 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        goto L_0x0a3c;
    L_0x0a25:
        r1 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r36 = r15.dequeueInputBuffer(r1);	 Catch:{ Exception -> 0x0aeb, all -> 0x0caa }
        if (r36 < 0) goto L_0x0a3c;
    L_0x0a2d:
        r37 = 0;
        r38 = 1;
        r3 = r12.presentationTimeUs;	 Catch:{ Exception -> 0x0aeb, all -> 0x0caa }
        r41 = 4;
        r35 = r15;
        r39 = r3;
        r35.queueInputBuffer(r36, r37, r38, r39, r41);	 Catch:{ Exception -> 0x0aeb, all -> 0x0caa }
    L_0x0a3c:
        r40 = 0;
        goto L_0x0a86;
    L_0x0a3f:
        r1 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        goto L_0x0a86;
    L_0x0a42:
        r17 = r4;
        r23 = r45;
        r49 = r58;
        r64 = r61;
        r16 = 0;
        r30 = 1;
        r1 = new java.lang.RuntimeException;	 Catch:{ Exception -> 0x0aeb, all -> 0x0caa }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0aeb, all -> 0x0caa }
        r2.<init>();	 Catch:{ Exception -> 0x0aeb, all -> 0x0caa }
        r3 = "unexpected result from decoder.dequeueOutputBuffer: ";
        r2.append(r3);	 Catch:{ Exception -> 0x0aeb, all -> 0x0caa }
        r2.append(r5);	 Catch:{ Exception -> 0x0aeb, all -> 0x0caa }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0aeb, all -> 0x0caa }
        r1.<init>(r2);	 Catch:{ Exception -> 0x0aeb, all -> 0x0caa }
        throw r1;	 Catch:{ Exception -> 0x0aeb, all -> 0x0caa }
    L_0x0a66:
        r0 = move-exception;
        goto L_0x0af7;
    L_0x0a69:
        r17 = r4;
        r60 = r5;
        r43 = r9;
        r23 = r45;
    L_0x0a71:
        r49 = r58;
        r64 = r61;
        r25 = r62;
        r1 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r8 = 18;
        r9 = -1;
        r16 = 0;
        r19 = -5;
        r27 = 2;
        r30 = 1;
        goto L_0x0822;
    L_0x0a86:
        r7 = r75;
        r1 = r10;
        r4 = r17;
        r45 = r23;
        r62 = r25;
        r9 = r40;
        r2 = r43;
        r58 = r49;
        r46 = r60;
        r61 = r64;
        goto L_0x07f5;
    L_0x0a9b:
        r11 = r71;
        r17 = r4;
        r23 = r45;
        r49 = r58;
        r64 = r61;
        r16 = 0;
        r30 = 1;
        r1 = new java.lang.RuntimeException;	 Catch:{ Exception -> 0x0aeb, all -> 0x0caa }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0aeb, all -> 0x0caa }
        r2.<init>();	 Catch:{ Exception -> 0x0aeb, all -> 0x0caa }
        r4 = "encoderOutputBuffer ";
        r2.append(r4);	 Catch:{ Exception -> 0x0aeb, all -> 0x0caa }
        r2.append(r3);	 Catch:{ Exception -> 0x0aeb, all -> 0x0caa }
        r3 = " was null";
        r2.append(r3);	 Catch:{ Exception -> 0x0aeb, all -> 0x0caa }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0aeb, all -> 0x0caa }
        r1.<init>(r2);	 Catch:{ Exception -> 0x0aeb, all -> 0x0caa }
        throw r1;	 Catch:{ Exception -> 0x0aeb, all -> 0x0caa }
    L_0x0ac5:
        r11 = r71;
        r17 = r4;
        r23 = r45;
        r49 = r58;
        r64 = r61;
        r16 = 0;
        r30 = 1;
        r1 = new java.lang.RuntimeException;	 Catch:{ Exception -> 0x0aeb, all -> 0x0caa }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0aeb, all -> 0x0caa }
        r2.<init>();	 Catch:{ Exception -> 0x0aeb, all -> 0x0caa }
        r4 = "unexpected result from encoder.dequeueOutputBuffer: ";
        r2.append(r4);	 Catch:{ Exception -> 0x0aeb, all -> 0x0caa }
        r2.append(r3);	 Catch:{ Exception -> 0x0aeb, all -> 0x0caa }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0aeb, all -> 0x0caa }
        r1.<init>(r2);	 Catch:{ Exception -> 0x0aeb, all -> 0x0caa }
        throw r1;	 Catch:{ Exception -> 0x0aeb, all -> 0x0caa }
    L_0x0aeb:
        r0 = move-exception;
        r9 = r73;
        r10 = r74;
        r1 = r0;
        r4 = r17;
        goto L_0x0b08;
    L_0x0af4:
        r0 = move-exception;
        r11 = r71;
    L_0x0af7:
        r17 = r4;
        r23 = r45;
        r49 = r58;
        r64 = r61;
        r16 = 0;
        r30 = 1;
        r9 = r73;
        r10 = r74;
        r1 = r0;
    L_0x0b08:
        r6 = r23;
        r13 = r28;
        r2 = r49;
        goto L_0x0bd4;
    L_0x0b10:
        r0 = move-exception;
        r49 = r2;
        r17 = r4;
        r11 = r12;
        r64 = r28;
        r23 = r45;
        r16 = 0;
        r30 = 1;
        r28 = r13;
        r9 = r73;
        r10 = r74;
        r1 = r0;
        r6 = r23;
        goto L_0x0bd4;
    L_0x0b29:
        r0 = move-exception;
        r49 = r2;
        r17 = r4;
        r23 = r6;
        r45 = r9;
        r11 = r12;
        r64 = r28;
        r16 = 0;
        r30 = 1;
        r9 = r73;
        r10 = r74;
        goto L_0x0b52;
    L_0x0b3e:
        r0 = move-exception;
        r49 = r2;
        r17 = r4;
        r23 = r6;
        r73 = r9;
        r74 = r10;
        r11 = r12;
        r64 = r28;
        r16 = 0;
        r30 = 1;
        r45 = 0;
    L_0x0b52:
        r1 = r0;
        r13 = r45;
        goto L_0x0bd4;
    L_0x0b57:
        r0 = move-exception;
        r17 = r4;
        r23 = r6;
        r73 = r9;
        r74 = r10;
        r11 = r12;
        r64 = r28;
        r16 = 0;
        r30 = 1;
        r45 = 0;
        r1 = r0;
        r2 = r45;
        r13 = r2;
        goto L_0x0bd4;
    L_0x0b6f:
        r0 = move-exception;
        r23 = r6;
        r73 = r9;
        r74 = r10;
        r11 = r12;
        r64 = r28;
        r16 = 0;
        r30 = 1;
        r45 = 0;
        r1 = r0;
        r2 = r45;
        r4 = r2;
        r13 = r4;
        goto L_0x0bd4;
    L_0x0b86:
        r0 = move-exception;
        r73 = r9;
        r74 = r10;
        r11 = r12;
        r64 = r28;
        r16 = 0;
        r30 = 1;
        r45 = 0;
        r1 = r0;
        r2 = r45;
        goto L_0x0ba7;
    L_0x0b98:
        r0 = move-exception;
        r45 = r2;
        r73 = r9;
        r74 = r10;
        r11 = r12;
        r64 = r28;
        r16 = 0;
        r30 = 1;
        r1 = r0;
    L_0x0ba7:
        r4 = r2;
        r6 = r4;
        r13 = r6;
        goto L_0x0bd4;
    L_0x0bab:
        r0 = move-exception;
        r73 = r9;
        r74 = r10;
        goto L_0x0bbf;
    L_0x0bb1:
        r0 = move-exception;
        r11 = r12;
        r64 = r28;
        r16 = 0;
        r30 = 1;
        r45 = 0;
        r1 = r0;
        r10 = r2;
        goto L_0x0bce;
    L_0x0bbe:
        r0 = move-exception;
    L_0x0bbf:
        r11 = r12;
        r64 = r28;
        goto L_0x0bc7;
    L_0x0bc3:
        r0 = move-exception;
        r64 = r4;
        r11 = r12;
    L_0x0bc7:
        r16 = 0;
        r30 = 1;
        r45 = 0;
        r1 = r0;
    L_0x0bce:
        r2 = r45;
        r4 = r2;
        r6 = r4;
        r13 = r6;
        r15 = r13;
    L_0x0bd4:
        r3 = r1 instanceof java.lang.IllegalStateException;	 Catch:{ Exception -> 0x0c4a, all -> 0x0caa }
        if (r3 == 0) goto L_0x0bdc;
    L_0x0bd8:
        if (r82 != 0) goto L_0x0bdc;
    L_0x0bda:
        r3 = 1;
        goto L_0x0bdd;
    L_0x0bdc:
        r3 = 0;
    L_0x0bdd:
        r5 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0caa }
        r5.<init>();	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0caa }
        r7 = "bitrate: ";
        r5.append(r7);	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0caa }
        r5.append(r10);	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0caa }
        r7 = " framerate: ";
        r5.append(r7);	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0caa }
        r5.append(r9);	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0caa }
        r7 = " size: ";
        r5.append(r7);	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0caa }
        r7 = r72;
        r5.append(r7);	 Catch:{ Exception -> 0x0c3d, all -> 0x0caa }
        r8 = "x";
        r5.append(r8);	 Catch:{ Exception -> 0x0c3d, all -> 0x0caa }
        r5.append(r11);	 Catch:{ Exception -> 0x0c3d, all -> 0x0caa }
        r5 = r5.toString();	 Catch:{ Exception -> 0x0c3d, all -> 0x0caa }
        org.telegram.messenger.FileLog.e(r5);	 Catch:{ Exception -> 0x0c3d, all -> 0x0caa }
        org.telegram.messenger.FileLog.e(r1);	 Catch:{ Exception -> 0x0c3d, all -> 0x0caa }
        r17 = 1;
    L_0x0CLASSNAME:
        r1 = r14.extractor;	 Catch:{ Exception -> 0x0c3d, all -> 0x0caa }
        r5 = r64;
        r1.unselectTrack(r5);	 Catch:{ Exception -> 0x0c3d, all -> 0x0caa }
        if (r2 == 0) goto L_0x0c1d;
    L_0x0c1a:
        r2.release();	 Catch:{ Exception -> 0x0c3d, all -> 0x0caa }
    L_0x0c1d:
        if (r6 == 0) goto L_0x0CLASSNAME;
    L_0x0c1f:
        r6.release();	 Catch:{ Exception -> 0x0c3d, all -> 0x0caa }
    L_0x0CLASSNAME:
        if (r4 == 0) goto L_0x0c2a;
    L_0x0CLASSNAME:
        r4.stop();	 Catch:{ Exception -> 0x0c3d, all -> 0x0caa }
        r4.release();	 Catch:{ Exception -> 0x0c3d, all -> 0x0caa }
    L_0x0c2a:
        if (r15 == 0) goto L_0x0CLASSNAME;
    L_0x0c2c:
        r15.stop();	 Catch:{ Exception -> 0x0c3d, all -> 0x0caa }
        r15.release();	 Catch:{ Exception -> 0x0c3d, all -> 0x0caa }
    L_0x0CLASSNAME:
        if (r13 == 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r13.release();	 Catch:{ Exception -> 0x0c3d, all -> 0x0caa }
    L_0x0CLASSNAME:
        r66.checkConversionCanceled();	 Catch:{ Exception -> 0x0c3d, all -> 0x0caa }
        r16 = r17;
        goto L_0x0CLASSNAME;
    L_0x0c3d:
        r0 = move-exception;
        r1 = r0;
        r16 = r3;
        goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r0 = move-exception;
        r15 = r72;
        r1 = r0;
        r16 = r3;
        goto L_0x0cba;
    L_0x0c4a:
        r0 = move-exception;
        r15 = r72;
        goto L_0x0cb9;
    L_0x0c4f:
        r7 = r11;
        r11 = r12;
        r16 = 0;
        r3 = 0;
    L_0x0CLASSNAME:
        r15 = r7;
        r4 = r16;
        r16 = r3;
        goto L_0x0c8c;
    L_0x0c5a:
        r0 = move-exception;
        r7 = r11;
        r11 = r12;
        r16 = 0;
        r30 = 1;
        r1 = r0;
    L_0x0CLASSNAME:
        r15 = r7;
        goto L_0x0cba;
    L_0x0CLASSNAME:
        r7 = r11;
        r11 = r12;
        r1 = -1;
        r16 = 0;
        r30 = 1;
        r12 = r2;
        r2 = r14.extractor;	 Catch:{ Exception -> 0x0ca7, all -> 0x0caa }
        r3 = r14.mediaMuxer;	 Catch:{ Exception -> 0x0ca7, all -> 0x0caa }
        if (r10 == r1) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r13 = 1;
        goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r13 = 0;
    L_0x0CLASSNAME:
        r1 = r66;
        r4 = r12;
        r5 = r75;
        r7 = r77;
        r9 = r79;
        r15 = r72;
        r11 = r68;
        r12 = r13;
        r1.readAndWriteTracks(r2, r3, r4, r5, r7, r9, r11, r12);	 Catch:{ Exception -> 0x0ca5, all -> 0x0caa }
        r9 = r73;
        r10 = r74;
        r4 = 0;
    L_0x0c8c:
        r1 = r14.extractor;
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r1.release();
    L_0x0CLASSNAME:
        r1 = r14.mediaMuxer;
        if (r1 == 0) goto L_0x0ca0;
    L_0x0CLASSNAME:
        r1.finishMovie();	 Catch:{ Exception -> 0x0c9b }
        goto L_0x0ca0;
    L_0x0c9b:
        r0 = move-exception;
        r1 = r0;
        org.telegram.messenger.FileLog.e(r1);
    L_0x0ca0:
        r6 = r71;
        r8 = r9;
        r9 = r10;
        goto L_0x0d03;
    L_0x0ca5:
        r0 = move-exception;
        goto L_0x0cb5;
    L_0x0ca7:
        r0 = move-exception;
        r15 = r7;
        goto L_0x0cb5;
    L_0x0caa:
        r0 = move-exception;
        r2 = r0;
        r1 = r14;
        goto L_0x0d27;
    L_0x0caf:
        r0 = move-exception;
        r15 = r11;
        r16 = 0;
        r30 = 1;
    L_0x0cb5:
        r9 = r73;
        r10 = r74;
    L_0x0cb9:
        r1 = r0;
    L_0x0cba:
        r2 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0d23 }
        r2.<init>();	 Catch:{ all -> 0x0d23 }
        r3 = "bitrate: ";
        r2.append(r3);	 Catch:{ all -> 0x0d23 }
        r2.append(r10);	 Catch:{ all -> 0x0d23 }
        r3 = " framerate: ";
        r2.append(r3);	 Catch:{ all -> 0x0d23 }
        r2.append(r9);	 Catch:{ all -> 0x0d23 }
        r3 = " size: ";
        r2.append(r3);	 Catch:{ all -> 0x0d23 }
        r2.append(r15);	 Catch:{ all -> 0x0d23 }
        r3 = "x";
        r2.append(r3);	 Catch:{ all -> 0x0d23 }
        r6 = r71;
        r2.append(r6);	 Catch:{ all -> 0x0d23 }
        r2 = r2.toString();	 Catch:{ all -> 0x0d23 }
        org.telegram.messenger.FileLog.e(r2);	 Catch:{ all -> 0x0d23 }
        org.telegram.messenger.FileLog.e(r1);	 Catch:{ all -> 0x0d23 }
        r1 = r14.extractor;
        if (r1 == 0) goto L_0x0cf3;
    L_0x0cf0:
        r1.release();
    L_0x0cf3:
        r1 = r14.mediaMuxer;
        if (r1 == 0) goto L_0x0d00;
    L_0x0cf7:
        r1.finishMovie();	 Catch:{ Exception -> 0x0cfb }
        goto L_0x0d00;
    L_0x0cfb:
        r0 = move-exception;
        r1 = r0;
        org.telegram.messenger.FileLog.e(r1);
    L_0x0d00:
        r8 = r9;
        r9 = r10;
        r4 = 1;
    L_0x0d03:
        if (r16 == 0) goto L_0x0d22;
    L_0x0d05:
        r17 = 1;
        r1 = r66;
        r2 = r67;
        r3 = r68;
        r4 = r69;
        r5 = r70;
        r6 = r71;
        r7 = r72;
        r10 = r75;
        r12 = r77;
        r14 = r79;
        r16 = r81;
        r1 = r1.convertVideoInternal(r2, r3, r4, r5, r6, r7, r8, r9, r10, r12, r14, r16, r17);
        return r1;
    L_0x0d22:
        return r4;
    L_0x0d23:
        r0 = move-exception;
        r1 = r66;
        r2 = r0;
    L_0x0d27:
        r3 = r1.extractor;
        if (r3 == 0) goto L_0x0d2e;
    L_0x0d2b:
        r3.release();
    L_0x0d2e:
        r3 = r1.mediaMuxer;
        if (r3 == 0) goto L_0x0d3b;
    L_0x0d32:
        r3.finishMovie();	 Catch:{ Exception -> 0x0d36 }
        goto L_0x0d3b;
    L_0x0d36:
        r0 = move-exception;
        r3 = r0;
        org.telegram.messenger.FileLog.e(r3);
    L_0x0d3b:
        goto L_0x0d3d;
    L_0x0d3c:
        throw r2;
    L_0x0d3d:
        goto L_0x0d3c;
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
