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

    /* JADX WARNING: Removed duplicated region for block: B:51:0x010e A:{Catch:{ Exception -> 0x011f, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x00df A:{Catch:{ Exception -> 0x011f, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x00df A:{Catch:{ Exception -> 0x011f, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x010e A:{Catch:{ Exception -> 0x011f, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:300:0x0509 A:{Catch:{ Exception -> 0x0520, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:569:0x09d0 A:{Catch:{ Exception -> 0x0a87, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:550:0x0991 A:{Catch:{ Exception -> 0x09d6, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:550:0x0991 A:{Catch:{ Exception -> 0x09d6, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:569:0x09d0 A:{Catch:{ Exception -> 0x0a87, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:508:0x0862 A:{SYNTHETIC, Splitter:B:508:0x0862} */
    /* JADX WARNING: Removed duplicated region for block: B:569:0x09d0 A:{Catch:{ Exception -> 0x0a87, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:550:0x0991 A:{Catch:{ Exception -> 0x09d6, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:502:0x0857 A:{Catch:{ Exception -> 0x07dd, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:498:0x0833 A:{Catch:{ Exception -> 0x07dd, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:448:0x0788 A:{Catch:{ Exception -> 0x0a00, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:447:0x0777 A:{Catch:{ Exception -> 0x0a00, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:98:0x01c7 A:{SYNTHETIC, Splitter:B:98:0x01c7} */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x01be A:{SYNTHETIC, Splitter:B:95:0x01be} */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x01d7  */
    /* JADX WARNING: Removed duplicated region for block: B:104:0x01d3  */
    /* JADX WARNING: Removed duplicated region for block: B:110:0x01f7 A:{SYNTHETIC, Splitter:B:110:0x01f7} */
    /* JADX WARNING: Removed duplicated region for block: B:125:0x021c A:{SYNTHETIC, Splitter:B:125:0x021c} */
    /* JADX WARNING: Removed duplicated region for block: B:147:0x025d  */
    /* JADX WARNING: Removed duplicated region for block: B:138:0x023b A:{SYNTHETIC, Splitter:B:138:0x023b} */
    /* JADX WARNING: Removed duplicated region for block: B:158:0x0283 A:{SYNTHETIC, Splitter:B:158:0x0283} */
    /* JADX WARNING: Removed duplicated region for block: B:154:0x0273 A:{SYNTHETIC, Splitter:B:154:0x0273} */
    /* JADX WARNING: Removed duplicated region for block: B:176:0x02bf  */
    /* JADX WARNING: Removed duplicated region for block: B:167:0x029e A:{SYNTHETIC, Splitter:B:167:0x029e} */
    /* JADX WARNING: Removed duplicated region for block: B:219:0x037e  */
    /* JADX WARNING: Removed duplicated region for block: B:178:0x02c4 A:{SYNTHETIC, Splitter:B:178:0x02c4} */
    /* JADX WARNING: Removed duplicated region for block: B:223:0x03a7 A:{Catch:{ Exception -> 0x0aba, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:311:0x0543  */
    /* JADX WARNING: Removed duplicated region for block: B:240:0x03f1 A:{SYNTHETIC, Splitter:B:240:0x03f1} */
    /* JADX WARNING: Removed duplicated region for block: B:320:0x0581  */
    /* JADX WARNING: Removed duplicated region for block: B:319:0x0579  */
    /* JADX WARNING: Removed duplicated region for block: B:327:0x05a0  */
    /* JADX WARNING: Removed duplicated region for block: B:325:0x058e  */
    /* JADX WARNING: Removed duplicated region for block: B:447:0x0777 A:{Catch:{ Exception -> 0x0a00, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:448:0x0788 A:{Catch:{ Exception -> 0x0a00, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:638:0x0bc4 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:640:0x0bc9 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:642:0x0bce A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:644:0x0bd6 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:646:0x0bde A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x01be A:{SYNTHETIC, Splitter:B:95:0x01be} */
    /* JADX WARNING: Removed duplicated region for block: B:98:0x01c7 A:{SYNTHETIC, Splitter:B:98:0x01c7} */
    /* JADX WARNING: Removed duplicated region for block: B:104:0x01d3  */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x01d7  */
    /* JADX WARNING: Removed duplicated region for block: B:110:0x01f7 A:{SYNTHETIC, Splitter:B:110:0x01f7} */
    /* JADX WARNING: Removed duplicated region for block: B:125:0x021c A:{SYNTHETIC, Splitter:B:125:0x021c} */
    /* JADX WARNING: Removed duplicated region for block: B:138:0x023b A:{SYNTHETIC, Splitter:B:138:0x023b} */
    /* JADX WARNING: Removed duplicated region for block: B:147:0x025d  */
    /* JADX WARNING: Removed duplicated region for block: B:154:0x0273 A:{SYNTHETIC, Splitter:B:154:0x0273} */
    /* JADX WARNING: Removed duplicated region for block: B:158:0x0283 A:{SYNTHETIC, Splitter:B:158:0x0283} */
    /* JADX WARNING: Removed duplicated region for block: B:167:0x029e A:{SYNTHETIC, Splitter:B:167:0x029e} */
    /* JADX WARNING: Removed duplicated region for block: B:176:0x02bf  */
    /* JADX WARNING: Removed duplicated region for block: B:178:0x02c4 A:{SYNTHETIC, Splitter:B:178:0x02c4} */
    /* JADX WARNING: Removed duplicated region for block: B:219:0x037e  */
    /* JADX WARNING: Removed duplicated region for block: B:223:0x03a7 A:{Catch:{ Exception -> 0x0aba, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:229:0x03c6 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:240:0x03f1 A:{SYNTHETIC, Splitter:B:240:0x03f1} */
    /* JADX WARNING: Removed duplicated region for block: B:311:0x0543  */
    /* JADX WARNING: Removed duplicated region for block: B:314:0x055b A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:319:0x0579  */
    /* JADX WARNING: Removed duplicated region for block: B:320:0x0581  */
    /* JADX WARNING: Removed duplicated region for block: B:325:0x058e  */
    /* JADX WARNING: Removed duplicated region for block: B:327:0x05a0  */
    /* JADX WARNING: Removed duplicated region for block: B:448:0x0788 A:{Catch:{ Exception -> 0x0a00, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:447:0x0777 A:{Catch:{ Exception -> 0x0a00, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:638:0x0bc4 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:640:0x0bc9 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:642:0x0bce A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:644:0x0bd6 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:646:0x0bde A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:98:0x01c7 A:{SYNTHETIC, Splitter:B:98:0x01c7} */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x01be A:{SYNTHETIC, Splitter:B:95:0x01be} */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x01d7  */
    /* JADX WARNING: Removed duplicated region for block: B:104:0x01d3  */
    /* JADX WARNING: Removed duplicated region for block: B:110:0x01f7 A:{SYNTHETIC, Splitter:B:110:0x01f7} */
    /* JADX WARNING: Removed duplicated region for block: B:125:0x021c A:{SYNTHETIC, Splitter:B:125:0x021c} */
    /* JADX WARNING: Removed duplicated region for block: B:147:0x025d  */
    /* JADX WARNING: Removed duplicated region for block: B:138:0x023b A:{SYNTHETIC, Splitter:B:138:0x023b} */
    /* JADX WARNING: Removed duplicated region for block: B:158:0x0283 A:{SYNTHETIC, Splitter:B:158:0x0283} */
    /* JADX WARNING: Removed duplicated region for block: B:154:0x0273 A:{SYNTHETIC, Splitter:B:154:0x0273} */
    /* JADX WARNING: Removed duplicated region for block: B:176:0x02bf  */
    /* JADX WARNING: Removed duplicated region for block: B:167:0x029e A:{SYNTHETIC, Splitter:B:167:0x029e} */
    /* JADX WARNING: Removed duplicated region for block: B:219:0x037e  */
    /* JADX WARNING: Removed duplicated region for block: B:178:0x02c4 A:{SYNTHETIC, Splitter:B:178:0x02c4} */
    /* JADX WARNING: Removed duplicated region for block: B:223:0x03a7 A:{Catch:{ Exception -> 0x0aba, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:229:0x03c6 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:311:0x0543  */
    /* JADX WARNING: Removed duplicated region for block: B:240:0x03f1 A:{SYNTHETIC, Splitter:B:240:0x03f1} */
    /* JADX WARNING: Removed duplicated region for block: B:314:0x055b A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:320:0x0581  */
    /* JADX WARNING: Removed duplicated region for block: B:319:0x0579  */
    /* JADX WARNING: Removed duplicated region for block: B:327:0x05a0  */
    /* JADX WARNING: Removed duplicated region for block: B:325:0x058e  */
    /* JADX WARNING: Removed duplicated region for block: B:447:0x0777 A:{Catch:{ Exception -> 0x0a00, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:448:0x0788 A:{Catch:{ Exception -> 0x0a00, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:638:0x0bc4 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:640:0x0bc9 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:642:0x0bce A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:644:0x0bd6 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:646:0x0bde A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:638:0x0bc4 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:640:0x0bc9 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:642:0x0bce A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:644:0x0bd6 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:646:0x0bde A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:677:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:680:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:680:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0cde A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0cbf  */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0cbf  */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0cde A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:688:0x0CLASSNAME A:{Splitter:B:61:0x0138, ExcHandler: all (r0_67 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:688:0x0CLASSNAME A:{Splitter:B:61:0x0138, ExcHandler: all (r0_67 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:638:0x0bc4 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:640:0x0bc9 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:642:0x0bce A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:644:0x0bd6 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:646:0x0bde A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:677:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:680:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:680:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0cde A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0cbf  */
    /* JADX WARNING: Removed duplicated region for block: B:688:0x0CLASSNAME A:{Splitter:B:61:0x0138, ExcHandler: all (r0_67 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:638:0x0bc4 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:640:0x0bc9 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:642:0x0bce A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:644:0x0bd6 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:646:0x0bde A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:677:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:680:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:680:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0cbf  */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0cde A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:688:0x0CLASSNAME A:{Splitter:B:61:0x0138, ExcHandler: all (r0_67 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:638:0x0bc4 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:640:0x0bc9 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:642:0x0bce A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:644:0x0bd6 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:646:0x0bde A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:677:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:680:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:680:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0cde A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0cbf  */
    /* JADX WARNING: Removed duplicated region for block: B:688:0x0CLASSNAME A:{Splitter:B:61:0x0138, ExcHandler: all (r0_67 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:638:0x0bc4 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:640:0x0bc9 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:642:0x0bce A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:644:0x0bd6 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:646:0x0bde A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:677:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:680:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:680:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0cbf  */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0cde A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:688:0x0CLASSNAME A:{Splitter:B:61:0x0138, ExcHandler: all (r0_67 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:638:0x0bc4 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:640:0x0bc9 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:642:0x0bce A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:644:0x0bd6 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:646:0x0bde A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:677:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:680:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:680:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0cde A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0cbf  */
    /* JADX WARNING: Removed duplicated region for block: B:688:0x0CLASSNAME A:{Splitter:B:61:0x0138, ExcHandler: all (r0_67 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:638:0x0bc4 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:640:0x0bc9 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:642:0x0bce A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:644:0x0bd6 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:646:0x0bde A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:677:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:680:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:680:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0cbf  */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0cde A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:688:0x0CLASSNAME A:{Splitter:B:61:0x0138, ExcHandler: all (r0_67 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:699:0x0cac  */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0cb3 A:{SYNTHETIC, Splitter:B:702:0x0cb3} */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0cde A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0cbf  */
    /* JADX WARNING: Removed duplicated region for block: B:688:0x0CLASSNAME A:{Splitter:B:61:0x0138, ExcHandler: all (r0_67 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:638:0x0bc4 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:640:0x0bc9 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:642:0x0bce A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:644:0x0bd6 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:646:0x0bde A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:677:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:680:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:680:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0cbf  */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0cde A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:688:0x0CLASSNAME A:{Splitter:B:61:0x0138, ExcHandler: all (r0_67 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:699:0x0cac  */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0cb3 A:{SYNTHETIC, Splitter:B:702:0x0cb3} */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0cde A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0cbf  */
    /* JADX WARNING: Removed duplicated region for block: B:688:0x0CLASSNAME A:{Splitter:B:61:0x0138, ExcHandler: all (r0_67 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:638:0x0bc4 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:640:0x0bc9 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:642:0x0bce A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:644:0x0bd6 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:646:0x0bde A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:677:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:680:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:680:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0cbf  */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0cde A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:688:0x0CLASSNAME A:{Splitter:B:61:0x0138, ExcHandler: all (r0_67 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:638:0x0bc4 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:640:0x0bc9 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:642:0x0bce A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:644:0x0bd6 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:646:0x0bde A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:677:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:680:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:680:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0cde A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0cbf  */
    /* JADX WARNING: Removed duplicated region for block: B:688:0x0CLASSNAME A:{Splitter:B:61:0x0138, ExcHandler: all (r0_67 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:688:0x0CLASSNAME A:{Splitter:B:61:0x0138, ExcHandler: all (r0_67 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:638:0x0bc4 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:640:0x0bc9 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:642:0x0bce A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:644:0x0bd6 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:646:0x0bde A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:677:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:680:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:680:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0cbf  */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0cde A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:688:0x0CLASSNAME A:{Splitter:B:61:0x0138, ExcHandler: all (r0_67 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:638:0x0bc4 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:640:0x0bc9 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:642:0x0bce A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:644:0x0bd6 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:646:0x0bde A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:677:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:680:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:680:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0cde A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0cbf  */
    /* JADX WARNING: Removed duplicated region for block: B:688:0x0CLASSNAME A:{Splitter:B:61:0x0138, ExcHandler: all (r0_67 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:688:0x0CLASSNAME A:{Splitter:B:61:0x0138, ExcHandler: all (r0_67 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:638:0x0bc4 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:640:0x0bc9 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:642:0x0bce A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:644:0x0bd6 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:646:0x0bde A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:677:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:680:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:680:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0cbf  */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0cde A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:688:0x0CLASSNAME A:{Splitter:B:61:0x0138, ExcHandler: all (r0_67 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:699:0x0cac  */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0cb3 A:{SYNTHETIC, Splitter:B:702:0x0cb3} */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0cde A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0cbf  */
    /* JADX WARNING: Removed duplicated region for block: B:688:0x0CLASSNAME A:{Splitter:B:61:0x0138, ExcHandler: all (r0_67 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:699:0x0cac  */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0cb3 A:{SYNTHETIC, Splitter:B:702:0x0cb3} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0cbf  */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0cde A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:688:0x0CLASSNAME A:{Splitter:B:61:0x0138, ExcHandler: all (r0_67 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:688:0x0CLASSNAME A:{Splitter:B:61:0x0138, ExcHandler: all (r0_67 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:638:0x0bc4 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:640:0x0bc9 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:642:0x0bce A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:644:0x0bd6 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:646:0x0bde A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:677:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:680:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:680:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0cde A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0cbf  */
    /* JADX WARNING: Removed duplicated region for block: B:688:0x0CLASSNAME A:{Splitter:B:61:0x0138, ExcHandler: all (r0_67 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:638:0x0bc4 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:640:0x0bc9 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:642:0x0bce A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:644:0x0bd6 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:646:0x0bde A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:677:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:680:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:680:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0cbf  */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0cde A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:688:0x0CLASSNAME A:{Splitter:B:61:0x0138, ExcHandler: all (r0_67 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:638:0x0bc4 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:640:0x0bc9 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:642:0x0bce A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:644:0x0bd6 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:646:0x0bde A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:677:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:680:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:680:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0cde A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0cbf  */
    /* JADX WARNING: Removed duplicated region for block: B:688:0x0CLASSNAME A:{Splitter:B:61:0x0138, ExcHandler: all (r0_67 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:688:0x0CLASSNAME A:{Splitter:B:61:0x0138, ExcHandler: all (r0_67 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:688:0x0CLASSNAME A:{Splitter:B:61:0x0138, ExcHandler: all (r0_67 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:715:0x0ce7  */
    /* JADX WARNING: Removed duplicated region for block: B:718:0x0cee A:{SYNTHETIC, Splitter:B:718:0x0cee} */
    /* JADX WARNING: Removed duplicated region for block: B:638:0x0bc4 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:640:0x0bc9 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:642:0x0bce A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:644:0x0bd6 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:646:0x0bde A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:677:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:680:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:680:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0cbf  */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0cde A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:688:0x0CLASSNAME A:{Splitter:B:61:0x0138, ExcHandler: all (r0_67 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:638:0x0bc4 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:640:0x0bc9 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:642:0x0bce A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:644:0x0bd6 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:646:0x0bde A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:677:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:680:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:680:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0cde A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0cbf  */
    /* JADX WARNING: Removed duplicated region for block: B:688:0x0CLASSNAME A:{Splitter:B:61:0x0138, ExcHandler: all (r0_67 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:688:0x0CLASSNAME A:{Splitter:B:61:0x0138, ExcHandler: all (r0_67 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:638:0x0bc4 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:640:0x0bc9 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:642:0x0bce A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:644:0x0bd6 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:646:0x0bde A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:677:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:680:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:680:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0cbf  */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0cde A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:688:0x0CLASSNAME A:{Splitter:B:61:0x0138, ExcHandler: all (r0_67 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:638:0x0bc4 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:640:0x0bc9 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:642:0x0bce A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:644:0x0bd6 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:646:0x0bde A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:677:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:680:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:680:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0cde A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0cbf  */
    /* JADX WARNING: Removed duplicated region for block: B:688:0x0CLASSNAME A:{Splitter:B:61:0x0138, ExcHandler: all (r0_67 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:638:0x0bc4 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:640:0x0bc9 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:642:0x0bce A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:644:0x0bd6 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:646:0x0bde A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:677:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:680:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:680:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0cbf  */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0cde A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:688:0x0CLASSNAME A:{Splitter:B:61:0x0138, ExcHandler: all (r0_67 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:638:0x0bc4 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:640:0x0bc9 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:642:0x0bce A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:644:0x0bd6 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:646:0x0bde A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:677:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:680:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:680:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0cde A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0cbf  */
    /* JADX WARNING: Removed duplicated region for block: B:688:0x0CLASSNAME A:{Splitter:B:61:0x0138, ExcHandler: all (r0_67 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:688:0x0CLASSNAME A:{Splitter:B:61:0x0138, ExcHandler: all (r0_67 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:638:0x0bc4 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:640:0x0bc9 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:642:0x0bce A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:644:0x0bd6 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:646:0x0bde A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:677:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:680:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:680:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0cbf  */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0cde A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:688:0x0CLASSNAME A:{Splitter:B:61:0x0138, ExcHandler: all (r0_67 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:688:0x0CLASSNAME A:{Splitter:B:61:0x0138, ExcHandler: all (r0_67 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:699:0x0cac  */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0cb3 A:{SYNTHETIC, Splitter:B:702:0x0cb3} */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0cde A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0cbf  */
    /* JADX WARNING: Removed duplicated region for block: B:688:0x0CLASSNAME A:{Splitter:B:61:0x0138, ExcHandler: all (r0_67 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:688:0x0CLASSNAME A:{Splitter:B:61:0x0138, ExcHandler: all (r0_67 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:638:0x0bc4 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:640:0x0bc9 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:642:0x0bce A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:644:0x0bd6 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:646:0x0bde A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:677:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:680:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:680:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0cbf  */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0cde A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:688:0x0CLASSNAME A:{Splitter:B:61:0x0138, ExcHandler: all (r0_67 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:688:0x0CLASSNAME A:{Splitter:B:61:0x0138, ExcHandler: all (r0_67 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:688:0x0CLASSNAME A:{Splitter:B:61:0x0138, ExcHandler: all (r0_67 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:688:0x0CLASSNAME A:{Splitter:B:61:0x0138, ExcHandler: all (r0_67 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:688:0x0CLASSNAME A:{Splitter:B:61:0x0138, ExcHandler: all (r0_67 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:699:0x0cac  */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0cb3 A:{SYNTHETIC, Splitter:B:702:0x0cb3} */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0cde A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0cbf  */
    /* JADX WARNING: Removed duplicated region for block: B:688:0x0CLASSNAME A:{Splitter:B:61:0x0138, ExcHandler: all (r0_67 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:638:0x0bc4 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:640:0x0bc9 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:642:0x0bce A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:644:0x0bd6 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:646:0x0bde A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:677:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:680:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:680:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0cbf  */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0cde A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:688:0x0CLASSNAME A:{Splitter:B:61:0x0138, ExcHandler: all (r0_67 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:638:0x0bc4 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:640:0x0bc9 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:642:0x0bce A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:644:0x0bd6 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:646:0x0bde A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:677:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:680:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:680:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0cde A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0cbf  */
    /* JADX WARNING: Removed duplicated region for block: B:688:0x0CLASSNAME A:{Splitter:B:61:0x0138, ExcHandler: all (r0_67 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:699:0x0cac  */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0cb3 A:{SYNTHETIC, Splitter:B:702:0x0cb3} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0cbf  */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0cde A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:688:0x0CLASSNAME A:{Splitter:B:61:0x0138, ExcHandler: all (r0_67 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:699:0x0cac  */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0cb3 A:{SYNTHETIC, Splitter:B:702:0x0cb3} */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0cde A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0cbf  */
    /* JADX WARNING: Removed duplicated region for block: B:688:0x0CLASSNAME A:{Splitter:B:61:0x0138, ExcHandler: all (r0_67 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:638:0x0bc4 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:640:0x0bc9 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:642:0x0bce A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:644:0x0bd6 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:646:0x0bde A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:677:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:680:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:680:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0cbf  */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0cde A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:688:0x0CLASSNAME A:{Splitter:B:61:0x0138, ExcHandler: all (r0_67 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:688:0x0CLASSNAME A:{Splitter:B:61:0x0138, ExcHandler: all (r0_67 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:638:0x0bc4 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:640:0x0bc9 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:642:0x0bce A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:644:0x0bd6 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:646:0x0bde A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:677:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:680:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:680:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0cde A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0cbf  */
    /* JADX WARNING: Removed duplicated region for block: B:688:0x0CLASSNAME A:{Splitter:B:61:0x0138, ExcHandler: all (r0_67 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:699:0x0cac  */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0cb3 A:{SYNTHETIC, Splitter:B:702:0x0cb3} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0cbf  */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0cde A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:688:0x0CLASSNAME A:{Splitter:B:61:0x0138, ExcHandler: all (r0_67 'th' java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:638:0x0bc4 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:640:0x0bc9 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:642:0x0bce A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:644:0x0bd6 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:646:0x0bde A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:677:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:680:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:680:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0cde A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0cbf  */
    /* JADX WARNING: Removed duplicated region for block: B:638:0x0bc4 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:640:0x0bc9 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:642:0x0bce A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:644:0x0bd6 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:646:0x0bde A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:677:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:680:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:680:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0cbf  */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0cde A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:638:0x0bc4 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:640:0x0bc9 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:642:0x0bce A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:644:0x0bd6 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:646:0x0bde A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:677:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:680:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:680:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0cde A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0cbf  */
    /* JADX WARNING: Removed duplicated region for block: B:638:0x0bc4 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:640:0x0bc9 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:642:0x0bce A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:644:0x0bd6 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:646:0x0bde A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:677:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:680:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:680:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0cbf  */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0cde A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:638:0x0bc4 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:640:0x0bc9 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:642:0x0bce A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:644:0x0bd6 A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:646:0x0bde A:{Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:677:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:680:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:680:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0cde A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0cbf  */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:61:0x0138, B:672:0x0c3e] */
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
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:61:0x0138, B:665:0x0CLASSNAME] */
    /* JADX WARNING: Missing block: B:117:0x020d, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:118:0x020e, code skipped:
            r1 = r0;
            r10 = r2;
     */
    /* JADX WARNING: Missing block: B:127:0x0229, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:128:0x022a, code skipped:
            r1 = r0;
     */
    /* JADX WARNING: Missing block: B:143:0x0249, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:144:0x024a, code skipped:
            r1 = r0;
            r4 = r2;
            r13 = r12;
            r9 = r25;
            r2 = null;
            r3 = null;
     */
    /* JADX WARNING: Missing block: B:145:0x0253, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:146:0x0254, code skipped:
            r1 = r0;
            r13 = r12;
            r9 = r25;
            r2 = null;
            r3 = null;
            r4 = null;
     */
    /* JADX WARNING: Missing block: B:156:0x0279, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:157:0x027a, code skipped:
            r10 = r70;
            r1 = r0;
            r13 = r12;
            r9 = r25;
            r2 = null;
     */
    /* JADX WARNING: Missing block: B:173:0x02b6, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:174:0x02b7, code skipped:
            r10 = r70;
            r1 = r0;
            r13 = r12;
     */
    /* JADX WARNING: Missing block: B:175:0x02bb, code skipped:
            r9 = r25;
     */
    /* JADX WARNING: Missing block: B:190:0x031f, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:191:0x0320, code skipped:
            r10 = r70;
            r1 = r0;
            r13 = r12;
     */
    /* JADX WARNING: Missing block: B:210:0x0354, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:211:0x0356, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:212:0x0357, code skipped:
            r11 = r73;
     */
    /* JADX WARNING: Missing block: B:213:0x0359, code skipped:
            r13 = r67;
            r1 = r0;
            r57 = r10;
            r9 = r25;
            r4 = r43;
     */
    /* JADX WARNING: Missing block: B:214:0x0364, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:215:0x0365, code skipped:
            r11 = r73;
            r13 = r67;
            r10 = r70;
            r1 = r0;
     */
    /* JADX WARNING: Missing block: B:217:0x0372, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:218:0x0373, code skipped:
            r11 = r73;
            r43 = r4;
            r13 = r67;
            r10 = r70;
            r1 = r0;
     */
    /* JADX WARNING: Missing block: B:234:0x03d3, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:235:0x03d4, code skipped:
            r13 = r67;
            r1 = r0;
            r57 = r10;
            r9 = r25;
            r4 = r43;
            r2 = r54;
     */
    /* JADX WARNING: Missing block: B:236:0x03df, code skipped:
            r56 = null;
            r60 = true;
            r10 = r70;
     */
    /* JADX WARNING: Missing block: B:257:0x0452, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:258:0x0453, code skipped:
            r13 = r67;
            r1 = r0;
            r9 = r10;
            r4 = r43;
            r2 = r54;
            r5 = r56;
     */
    /* JADX WARNING: Missing block: B:292:0x04ed, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:293:0x04ee, code skipped:
            r13 = r67;
            r10 = r70;
            r1 = r0;
            r9 = r5;
     */
    /* JADX WARNING: Missing block: B:304:0x0520, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:305:0x0521, code skipped:
            r13 = r67;
            r10 = r70;
            r1 = r0;
            r4 = r43;
            r2 = r54;
            r5 = r56;
     */
    /* JADX WARNING: Missing block: B:307:0x052f, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:308:0x0530, code skipped:
            r57 = r10;
            r13 = r67;
            r10 = r70;
            r1 = r0;
            r9 = r25;
     */
    /* JADX WARNING: Missing block: B:309:0x0539, code skipped:
            r4 = r43;
            r2 = r54;
            r5 = r56;
     */
    /* JADX WARNING: Missing block: B:335:0x05ba, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:336:0x05bb, code skipped:
            r13 = r67;
     */
    /* JADX WARNING: Missing block: B:437:0x0752, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:438:0x0753, code skipped:
            r13 = r67;
            r9 = r68;
     */
    /* JADX WARNING: Missing block: B:463:0x07dd, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:511:0x0867, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:514:?, code skipped:
            org.telegram.messenger.FileLog.e(r0);
            r1 = true;
     */
    /* JADX WARNING: Missing block: B:527:0x08aa, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:528:0x08ab, code skipped:
            r1 = r43;
     */
    /* JADX WARNING: Missing block: B:541:0x0943, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:542:0x0944, code skipped:
            r59 = r1;
            r16 = r2;
            r43 = r3;
            r20 = r5;
            r9 = r60;
            r56 = null;
            r60 = true;
            r10 = r70;
            r1 = r0;
     */
    /* JADX WARNING: Missing block: B:543:0x0956, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:544:0x0957, code skipped:
            r20 = r5;
            r59 = r43;
            r9 = r60;
            r56 = null;
            r60 = true;
            r43 = r3;
            r10 = r70;
            r1 = r0;
            r2 = r54;
     */
    /* JADX WARNING: Missing block: B:554:0x099b, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:555:0x099c, code skipped:
            r10 = r70;
            r1 = r0;
            r2 = r16;
            r5 = r20;
            r3 = r43;
     */
    /* JADX WARNING: Missing block: B:570:0x09d6, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:571:0x09d7, code skipped:
            r1 = r20;
     */
    /* JADX WARNING: Missing block: B:574:0x0a00, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:582:0x0a87, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:583:0x0a88, code skipped:
            r10 = r70;
            r5 = r1;
            r2 = r16;
            r3 = r43;
     */
    /* JADX WARNING: Missing block: B:584:0x0a90, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:585:0x0a91, code skipped:
            r13 = r67;
     */
    /* JADX WARNING: Missing block: B:586:0x0a93, code skipped:
            r1 = r5;
            r59 = r43;
            r16 = r54;
            r9 = r60;
            r56 = null;
            r60 = true;
            r43 = r3;
            r10 = r70;
     */
    /* JADX WARNING: Missing block: B:587:0x0aa3, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:588:0x0aa4, code skipped:
            r13 = r67;
            r59 = r43;
            r16 = r54;
            r1 = r56;
            r9 = r60;
            r56 = null;
            r60 = true;
            r43 = r3;
            r10 = r70;
            r5 = r1;
     */
    /* JADX WARNING: Missing block: B:589:0x0ab7, code skipped:
            r2 = r16;
     */
    /* JADX WARNING: Missing block: B:590:0x0aba, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:591:0x0abb, code skipped:
            r13 = r67;
            r16 = r2;
            r1 = r5;
            r57 = r10;
            r9 = r25;
            r59 = r43;
            r56 = null;
            r60 = true;
            r43 = r3;
            r10 = r70;
     */
    /* JADX WARNING: Missing block: B:592:0x0ace, code skipped:
            r4 = r59;
     */
    /* JADX WARNING: Missing block: B:593:0x0ad2, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:594:0x0ad3, code skipped:
            r16 = r2;
            r43 = r3;
            r59 = r4;
            r1 = r5;
            r41 = null;
            r13 = r12;
            r9 = r25;
     */
    /* JADX WARNING: Missing block: B:595:0x0ae0, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:596:0x0ae1, code skipped:
            r16 = r2;
            r43 = r3;
            r59 = r4;
            r1 = r5;
            r13 = r12;
            r9 = r25;
            r41 = null;
     */
    /* JADX WARNING: Missing block: B:597:0x0aed, code skipped:
            r56 = null;
            r60 = true;
            r10 = r70;
            r57 = r41;
     */
    /* JADX WARNING: Missing block: B:598:0x0af7, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:599:0x0af8, code skipped:
            r43 = r3;
            r59 = r4;
            r1 = r5;
            r13 = r12;
            r9 = r25;
            r41 = null;
            r56 = null;
            r60 = true;
            r10 = r70;
     */
    /* JADX WARNING: Missing block: B:600:0x0b09, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:601:0x0b0a, code skipped:
            r43 = r3;
            r59 = r4;
            r1 = r5;
            r70 = r10;
            r13 = r12;
            r9 = r25;
            r41 = null;
            r56 = null;
            r60 = true;
     */
    /* JADX WARNING: Missing block: B:602:0x0b1a, code skipped:
            r2 = r41;
            r57 = r2;
     */
    /* JADX WARNING: Missing block: B:603:0x0b1f, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:604:0x0b20, code skipped:
            r59 = r4;
            r1 = r5;
            r70 = r10;
            r13 = r12;
            r9 = r25;
            r56 = null;
            r60 = true;
            r2 = null;
            r3 = r2;
            r57 = r3;
     */
    /* JADX WARNING: Missing block: B:605:0x0b34, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:606:0x0b35, code skipped:
            r1 = r5;
            r70 = r10;
            r13 = r12;
            r9 = r25;
            r56 = null;
            r60 = true;
            r2 = null;
     */
    /* JADX WARNING: Missing block: B:607:0x0b44, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:608:0x0b45, code skipped:
            r41 = null;
            r1 = r5;
            r70 = r10;
            r13 = r12;
            r9 = r25;
            r56 = null;
            r60 = true;
     */
    /* JADX WARNING: Missing block: B:609:0x0b51, code skipped:
            r3 = r2;
            r4 = r3;
            r57 = r4;
     */
    /* JADX WARNING: Missing block: B:611:0x0b57, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:612:0x0b58, code skipped:
            r70 = r10;
     */
    /* JADX WARNING: Missing block: B:613:0x0b5b, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:614:0x0b5c, code skipped:
            r13 = r12;
            r9 = r25;
            r41 = null;
            r56 = null;
            r60 = true;
            r1 = r0;
            r10 = r2;
     */
    /* JADX WARNING: Missing block: B:615:0x0b68, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:616:0x0b69, code skipped:
            r13 = r12;
            r9 = r25;
     */
    /* JADX WARNING: Missing block: B:617:0x0b6d, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:618:0x0b6e, code skipped:
            r9 = r4;
            r13 = r12;
     */
    /* JADX WARNING: Missing block: B:619:0x0b70, code skipped:
            r41 = null;
            r56 = null;
            r60 = true;
            r1 = r0;
     */
    /* JADX WARNING: Missing block: B:620:0x0b77, code skipped:
            r2 = r41;
            r3 = r2;
            r4 = r3;
            r5 = r4;
            r57 = r5;
     */
    /* JADX WARNING: Missing block: B:649:0x0be7, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:650:0x0be8, code skipped:
            r1 = r0;
            r13 = r11;
            r3 = r17;
     */
    /* JADX WARNING: Missing block: B:651:0x0bee, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:652:0x0bf0, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:653:0x0bf1, code skipped:
            r12 = r68;
     */
    /* JADX WARNING: Missing block: B:654:0x0bf3, code skipped:
            r1 = r0;
            r13 = r11;
     */
    /* JADX WARNING: Missing block: B:655:0x0bf6, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:656:0x0bf7, code skipped:
            r12 = r68;
            r13 = r69;
            r1 = r0;
     */
    /* JADX WARNING: Missing block: B:657:0x0bfc, code skipped:
            r3 = r56;
     */
    /* JADX WARNING: Missing block: B:658:0x0CLASSNAME, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:659:0x0CLASSNAME, code skipped:
            r12 = r68;
            r13 = r69;
     */
    /* JADX WARNING: Missing block: B:685:0x0CLASSNAME, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:686:0x0CLASSNAME, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:687:0x0CLASSNAME, code skipped:
            r13 = r11;
     */
    /* JADX WARNING: Missing block: B:688:0x0CLASSNAME, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:689:0x0CLASSNAME, code skipped:
            r2 = r0;
            r1 = r14;
     */
    /* JADX WARNING: Missing block: B:699:0x0cac, code skipped:
            r1.release();
     */
    /* JADX WARNING: Missing block: B:703:?, code skipped:
            r1.finishMovie();
     */
    /* JADX WARNING: Missing block: B:704:0x0cb7, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:705:0x0cb8, code skipped:
            org.telegram.messenger.FileLog.e(r0);
     */
    /* JADX WARNING: Missing block: B:715:0x0ce7, code skipped:
            r3.release();
     */
    /* JADX WARNING: Missing block: B:719:?, code skipped:
            r3.finishMovie();
     */
    /* JADX WARNING: Missing block: B:720:0x0cf2, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:721:0x0cf3, code skipped:
            org.telegram.messenger.FileLog.e(r0);
     */
    private boolean convertVideoInternal(java.lang.String r63, java.io.File r64, int r65, boolean r66, int r67, int r68, int r69, int r70, long r71, long r73, long r75, boolean r77, boolean r78) {
        /*
        r62 = this;
        r14 = r62;
        r13 = r63;
        r15 = r65;
        r12 = r67;
        r11 = r68;
        r9 = r69;
        r10 = r70;
        r7 = r71;
        r5 = r73;
        r2 = new android.media.MediaCodec$BufferInfo;	 Catch:{ Exception -> 0x0c6a, all -> 0x0CLASSNAME }
        r2.<init>();	 Catch:{ Exception -> 0x0c6a, all -> 0x0CLASSNAME }
        r1 = new org.telegram.messenger.video.Mp4Movie;	 Catch:{ Exception -> 0x0c6a, all -> 0x0CLASSNAME }
        r1.<init>();	 Catch:{ Exception -> 0x0c6a, all -> 0x0CLASSNAME }
        r4 = r64;
        r1.setCacheFile(r4);	 Catch:{ Exception -> 0x0c6a, all -> 0x0CLASSNAME }
        r1.setRotation(r15);	 Catch:{ Exception -> 0x0c6a, all -> 0x0CLASSNAME }
        r1.setSize(r12, r11);	 Catch:{ Exception -> 0x0c6a, all -> 0x0CLASSNAME }
        r3 = new org.telegram.messenger.video.MP4Builder;	 Catch:{ Exception -> 0x0c6a, all -> 0x0CLASSNAME }
        r3.<init>();	 Catch:{ Exception -> 0x0c6a, all -> 0x0CLASSNAME }
        r4 = r66;
        r1 = r3.createMovie(r1, r4);	 Catch:{ Exception -> 0x0c6a, all -> 0x0CLASSNAME }
        r14.mediaMuxer = r1;	 Catch:{ Exception -> 0x0c6a, all -> 0x0CLASSNAME }
        r1 = new android.media.MediaExtractor;	 Catch:{ Exception -> 0x0c6a, all -> 0x0CLASSNAME }
        r1.<init>();	 Catch:{ Exception -> 0x0c6a, all -> 0x0CLASSNAME }
        r14.extractor = r1;	 Catch:{ Exception -> 0x0c6a, all -> 0x0CLASSNAME }
        r1 = r14.extractor;	 Catch:{ Exception -> 0x0c6a, all -> 0x0CLASSNAME }
        r1.setDataSource(r13);	 Catch:{ Exception -> 0x0c6a, all -> 0x0CLASSNAME }
        r3 = r75;
        r1 = (float) r3;	 Catch:{ Exception -> 0x0c6a, all -> 0x0CLASSNAME }
        r18 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r18 = r1 / r18;
        r62.checkConversionCanceled();	 Catch:{ Exception -> 0x0c6a, all -> 0x0CLASSNAME }
        if (r77 == 0) goto L_0x0c1c;
    L_0x004c:
        r1 = r14.extractor;	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0CLASSNAME }
        r3 = 0;
        r4 = org.telegram.messenger.MediaController.findTrack(r1, r3);	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0CLASSNAME }
        r1 = -1;
        if (r10 == r1) goto L_0x0067;
    L_0x0056:
        r1 = r14.extractor;	 Catch:{ Exception -> 0x005f, all -> 0x0CLASSNAME }
        r3 = 1;
        r1 = org.telegram.messenger.MediaController.findTrack(r1, r3);	 Catch:{ Exception -> 0x005f, all -> 0x0CLASSNAME }
        r3 = r1;
        goto L_0x0068;
    L_0x005f:
        r0 = move-exception;
        r1 = r0;
        r13 = r9;
        r3 = 0;
        r60 = 1;
        goto L_0x0CLASSNAME;
    L_0x0067:
        r3 = -1;
    L_0x0068:
        if (r4 < 0) goto L_0x0CLASSNAME;
    L_0x006a:
        r20 = -1;
        r22 = android.os.Build.MANUFACTURER;	 Catch:{ Exception -> 0x0b6d, all -> 0x0CLASSNAME }
        r1 = r22.toLowerCase();	 Catch:{ Exception -> 0x0b6d, all -> 0x0CLASSNAME }
        r22 = r2;
        r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0b6d, all -> 0x0CLASSNAME }
        r6 = "video/avc";
        r27 = 4;
        r5 = 18;
        if (r2 >= r5) goto L_0x012f;
    L_0x007f:
        r2 = org.telegram.messenger.MediaController.selectCodec(r6);	 Catch:{ Exception -> 0x011f, all -> 0x0CLASSNAME }
        r28 = org.telegram.messenger.MediaController.selectColorFormat(r2, r6);	 Catch:{ Exception -> 0x011f, all -> 0x0CLASSNAME }
        if (r28 == 0) goto L_0x0117;
    L_0x0089:
        r5 = r2.getName();	 Catch:{ Exception -> 0x011f, all -> 0x0CLASSNAME }
        r13 = "OMX.qcom.";
        r13 = r5.contains(r13);	 Catch:{ Exception -> 0x011f, all -> 0x0CLASSNAME }
        if (r13 == 0) goto L_0x00b1;
    L_0x0095:
        r5 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x011f, all -> 0x0CLASSNAME }
        r13 = 16;
        if (r5 != r13) goto L_0x00ae;
    L_0x009b:
        r5 = "lge";
        r5 = r1.equals(r5);	 Catch:{ Exception -> 0x011f, all -> 0x0CLASSNAME }
        if (r5 != 0) goto L_0x00ab;
    L_0x00a3:
        r5 = "nokia";
        r5 = r1.equals(r5);	 Catch:{ Exception -> 0x011f, all -> 0x0CLASSNAME }
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
        r13 = r5.contains(r13);	 Catch:{ Exception -> 0x011f, all -> 0x0CLASSNAME }
        if (r13 == 0) goto L_0x00bb;
    L_0x00b9:
        r5 = 2;
        goto L_0x00af;
    L_0x00bb:
        r13 = "OMX.MTK.VIDEO.ENCODER.AVC";
        r13 = r5.equals(r13);	 Catch:{ Exception -> 0x011f, all -> 0x0CLASSNAME }
        if (r13 == 0) goto L_0x00c5;
    L_0x00c3:
        r5 = 3;
        goto L_0x00af;
    L_0x00c5:
        r13 = "OMX.SEC.AVC.Encoder";
        r13 = r5.equals(r13);	 Catch:{ Exception -> 0x011f, all -> 0x0CLASSNAME }
        if (r13 == 0) goto L_0x00cf;
    L_0x00cd:
        r5 = 4;
        goto L_0x00ac;
    L_0x00cf:
        r13 = "OMX.TI.DUCATI1.VIDEO.H264E";
        r5 = r5.equals(r13);	 Catch:{ Exception -> 0x011f, all -> 0x0CLASSNAME }
        if (r5 == 0) goto L_0x00d9;
    L_0x00d7:
        r5 = 5;
        goto L_0x00af;
    L_0x00d9:
        r5 = 0;
        goto L_0x00af;
    L_0x00db:
        r30 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x011f, all -> 0x0CLASSNAME }
        if (r30 == 0) goto L_0x010e;
    L_0x00df:
        r30 = r5;
        r5 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x011f, all -> 0x0CLASSNAME }
        r5.<init>();	 Catch:{ Exception -> 0x011f, all -> 0x0CLASSNAME }
        r31 = r13;
        r13 = "codec = ";
        r5.append(r13);	 Catch:{ Exception -> 0x011f, all -> 0x0CLASSNAME }
        r2 = r2.getName();	 Catch:{ Exception -> 0x011f, all -> 0x0CLASSNAME }
        r5.append(r2);	 Catch:{ Exception -> 0x011f, all -> 0x0CLASSNAME }
        r2 = " manufacturer = ";
        r5.append(r2);	 Catch:{ Exception -> 0x011f, all -> 0x0CLASSNAME }
        r5.append(r1);	 Catch:{ Exception -> 0x011f, all -> 0x0CLASSNAME }
        r2 = "device = ";
        r5.append(r2);	 Catch:{ Exception -> 0x011f, all -> 0x0CLASSNAME }
        r2 = android.os.Build.MODEL;	 Catch:{ Exception -> 0x011f, all -> 0x0CLASSNAME }
        r5.append(r2);	 Catch:{ Exception -> 0x011f, all -> 0x0CLASSNAME }
        r2 = r5.toString();	 Catch:{ Exception -> 0x011f, all -> 0x0CLASSNAME }
        org.telegram.messenger.FileLog.d(r2);	 Catch:{ Exception -> 0x011f, all -> 0x0CLASSNAME }
        goto L_0x0112;
    L_0x010e:
        r30 = r5;
        r31 = r13;
    L_0x0112:
        r13 = r28;
        r2 = r30;
        goto L_0x0138;
    L_0x0117:
        r1 = new java.lang.RuntimeException;	 Catch:{ Exception -> 0x011f, all -> 0x0CLASSNAME }
        r2 = "no supported color format";
        r1.<init>(r2);	 Catch:{ Exception -> 0x011f, all -> 0x0CLASSNAME }
        throw r1;	 Catch:{ Exception -> 0x011f, all -> 0x0CLASSNAME }
    L_0x011f:
        r0 = move-exception;
        r1 = r0;
        r9 = r4;
        r13 = r12;
    L_0x0123:
        r2 = 0;
        r3 = 0;
        r4 = 0;
        r5 = 0;
    L_0x0127:
        r56 = 0;
        r57 = 0;
    L_0x012b:
        r60 = 1;
        goto L_0x0b7e;
    L_0x012f:
        r2 = NUM; // 0x7var_ float:1.701803E38 double:1.0527098025E-314;
        r2 = 0;
        r13 = NUM; // 0x7var_ float:1.701803E38 double:1.0527098025E-314;
        r31 = 0;
    L_0x0138:
        r5 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x0b6d, all -> 0x0CLASSNAME }
        if (r5 == 0) goto L_0x0153;
    L_0x013c:
        r5 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x011f, all -> 0x0CLASSNAME }
        r5.<init>();	 Catch:{ Exception -> 0x011f, all -> 0x0CLASSNAME }
        r28 = r3;
        r3 = "colorFormat = ";
        r5.append(r3);	 Catch:{ Exception -> 0x011f, all -> 0x0CLASSNAME }
        r5.append(r13);	 Catch:{ Exception -> 0x011f, all -> 0x0CLASSNAME }
        r3 = r5.toString();	 Catch:{ Exception -> 0x011f, all -> 0x0CLASSNAME }
        org.telegram.messenger.FileLog.d(r3);	 Catch:{ Exception -> 0x011f, all -> 0x0CLASSNAME }
        goto L_0x0155;
    L_0x0153:
        r28 = r3;
    L_0x0155:
        r3 = r12 * r11;
        r5 = r3 * 3;
        r26 = 2;
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
        r1 = r1.toLowerCase();	 Catch:{ Exception -> 0x011f, all -> 0x0CLASSNAME }
        r2 = "lge";
        r1 = r1.equals(r2);	 Catch:{ Exception -> 0x011f, all -> 0x0CLASSNAME }
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
        r1 = r1.equals(r2);	 Catch:{ Exception -> 0x011f, all -> 0x0CLASSNAME }
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
        r1 = r14.extractor;	 Catch:{ Exception -> 0x0b6d, all -> 0x0CLASSNAME }
        r1.selectTrack(r4);	 Catch:{ Exception -> 0x0b6d, all -> 0x0CLASSNAME }
        r1 = r14.extractor;	 Catch:{ Exception -> 0x0b6d, all -> 0x0CLASSNAME }
        r1 = r1.getTrackFormat(r4);	 Catch:{ Exception -> 0x0b6d, all -> 0x0CLASSNAME }
        r2 = 0;
        r5 = (r7 > r2 ? 1 : (r7 == r2 ? 0 : -1));
        if (r5 <= 0) goto L_0x01c7;
    L_0x01be:
        r5 = r14.extractor;	 Catch:{ Exception -> 0x011f, all -> 0x0CLASSNAME }
        r2 = 0;
        r5.seekTo(r7, r2);	 Catch:{ Exception -> 0x011f, all -> 0x0CLASSNAME }
        r25 = r4;
        goto L_0x01d1;
    L_0x01c7:
        r2 = r14.extractor;	 Catch:{ Exception -> 0x0b6d, all -> 0x0CLASSNAME }
        r25 = r4;
        r3 = 0;
        r5 = 0;
        r2.seekTo(r3, r5);	 Catch:{ Exception -> 0x0b68, all -> 0x0CLASSNAME }
    L_0x01d1:
        if (r10 > 0) goto L_0x01d7;
    L_0x01d3:
        r2 = 921600; // 0xe1000 float:1.291437E-39 double:4.55331E-318;
        goto L_0x01d8;
    L_0x01d7:
        r2 = r10;
    L_0x01d8:
        r3 = android.media.MediaFormat.createVideoFormat(r6, r12, r11);	 Catch:{ Exception -> 0x0b5b, all -> 0x0CLASSNAME }
        r4 = "color-format";
        r3.setInteger(r4, r13);	 Catch:{ Exception -> 0x0b5b, all -> 0x0CLASSNAME }
        r4 = "bitrate";
        r3.setInteger(r4, r2);	 Catch:{ Exception -> 0x0b5b, all -> 0x0CLASSNAME }
        r4 = "frame-rate";
        r3.setInteger(r4, r9);	 Catch:{ Exception -> 0x0b5b, all -> 0x0CLASSNAME }
        r4 = "i-frame-interval";
        r5 = 2;
        r3.setInteger(r4, r5);	 Catch:{ Exception -> 0x0b5b, all -> 0x0CLASSNAME }
        r4 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0b5b, all -> 0x0CLASSNAME }
        r5 = 23;
        if (r4 >= r5) goto L_0x0215;
    L_0x01f7:
        r4 = java.lang.Math.min(r11, r12);	 Catch:{ Exception -> 0x020d, all -> 0x0CLASSNAME }
        r5 = 480; // 0x1e0 float:6.73E-43 double:2.37E-321;
        if (r4 > r5) goto L_0x0215;
    L_0x01ff:
        r4 = 921600; // 0xe1000 float:1.291437E-39 double:4.55331E-318;
        if (r2 <= r4) goto L_0x0207;
    L_0x0204:
        r2 = 921600; // 0xe1000 float:1.291437E-39 double:4.55331E-318;
    L_0x0207:
        r4 = "bitrate";
        r3.setInteger(r4, r2);	 Catch:{ Exception -> 0x020d, all -> 0x0CLASSNAME }
        goto L_0x0215;
    L_0x020d:
        r0 = move-exception;
        r1 = r0;
        r10 = r2;
    L_0x0210:
        r13 = r12;
        r9 = r25;
        goto L_0x0123;
    L_0x0215:
        r10 = r2;
        r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0b57, all -> 0x0CLASSNAME }
        r4 = 18;
        if (r2 >= r4) goto L_0x022c;
    L_0x021c:
        r2 = "stride";
        r4 = r12 + 32;
        r3.setInteger(r2, r4);	 Catch:{ Exception -> 0x0229, all -> 0x0CLASSNAME }
        r2 = "slice-height";
        r3.setInteger(r2, r11);	 Catch:{ Exception -> 0x0229, all -> 0x0CLASSNAME }
        goto L_0x022c;
    L_0x0229:
        r0 = move-exception;
        r1 = r0;
        goto L_0x0210;
    L_0x022c:
        r5 = android.media.MediaCodec.createEncoderByType(r6);	 Catch:{ Exception -> 0x0b57, all -> 0x0CLASSNAME }
        r2 = 0;
        r4 = 1;
        r5.configure(r3, r2, r2, r4);	 Catch:{ Exception -> 0x0b44, all -> 0x0CLASSNAME }
        r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0b34, all -> 0x0CLASSNAME }
        r3 = 18;
        if (r2 < r3) goto L_0x025d;
    L_0x023b:
        r2 = new org.telegram.messenger.video.InputSurface;	 Catch:{ Exception -> 0x0253, all -> 0x0CLASSNAME }
        r3 = r5.createInputSurface();	 Catch:{ Exception -> 0x0253, all -> 0x0CLASSNAME }
        r2.<init>(r3);	 Catch:{ Exception -> 0x0253, all -> 0x0CLASSNAME }
        r2.makeCurrent();	 Catch:{ Exception -> 0x0249, all -> 0x0CLASSNAME }
        r4 = r2;
        goto L_0x025e;
    L_0x0249:
        r0 = move-exception;
        r1 = r0;
        r4 = r2;
        r13 = r12;
        r9 = r25;
        r2 = 0;
        r3 = 0;
        goto L_0x0127;
    L_0x0253:
        r0 = move-exception;
        r1 = r0;
        r13 = r12;
        r9 = r25;
        r2 = 0;
        r3 = 0;
        r4 = 0;
        goto L_0x0127;
    L_0x025d:
        r4 = 0;
    L_0x025e:
        r5.start();	 Catch:{ Exception -> 0x0b1f, all -> 0x0CLASSNAME }
        r2 = "mime";
        r2 = r1.getString(r2);	 Catch:{ Exception -> 0x0b1f, all -> 0x0CLASSNAME }
        r3 = android.media.MediaCodec.createDecoderByType(r2);	 Catch:{ Exception -> 0x0b1f, all -> 0x0CLASSNAME }
        r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0b09, all -> 0x0CLASSNAME }
        r70 = r10;
        r10 = 18;
        if (r2 < r10) goto L_0x0283;
    L_0x0273:
        r2 = new org.telegram.messenger.video.OutputSurface;	 Catch:{ Exception -> 0x0279, all -> 0x0CLASSNAME }
        r2.<init>();	 Catch:{ Exception -> 0x0279, all -> 0x0CLASSNAME }
        goto L_0x028a;
    L_0x0279:
        r0 = move-exception;
        r10 = r70;
        r1 = r0;
        r13 = r12;
        r9 = r25;
        r2 = 0;
        goto L_0x0127;
    L_0x0283:
        r2 = new org.telegram.messenger.video.OutputSurface;	 Catch:{ Exception -> 0x0af7, all -> 0x0CLASSNAME }
        r10 = r65;
        r2.<init>(r12, r11, r10);	 Catch:{ Exception -> 0x0af7, all -> 0x0CLASSNAME }
    L_0x028a:
        r10 = r2.getSurface();	 Catch:{ Exception -> 0x0ae0, all -> 0x0CLASSNAME }
        r23 = r15;
        r9 = 0;
        r15 = 0;
        r3.configure(r1, r10, r9, r15);	 Catch:{ Exception -> 0x0ad2, all -> 0x0CLASSNAME }
        r3.start();	 Catch:{ Exception -> 0x0ad2, all -> 0x0CLASSNAME }
        r1 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0ad2, all -> 0x0CLASSNAME }
        r10 = 21;
        if (r1 >= r10) goto L_0x02bf;
    L_0x029e:
        r1 = r3.getInputBuffers();	 Catch:{ Exception -> 0x02b6, all -> 0x0CLASSNAME }
        r15 = r5.getOutputBuffers();	 Catch:{ Exception -> 0x02b6, all -> 0x0CLASSNAME }
        r9 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x02b6, all -> 0x0CLASSNAME }
        r10 = 18;
        if (r9 >= r10) goto L_0x02b3;
    L_0x02ac:
        r9 = r5.getInputBuffers();	 Catch:{ Exception -> 0x02b6, all -> 0x0CLASSNAME }
        r10 = r9;
        r9 = r1;
        goto L_0x02c2;
    L_0x02b3:
        r9 = r1;
        r10 = 0;
        goto L_0x02c2;
    L_0x02b6:
        r0 = move-exception;
        r10 = r70;
        r1 = r0;
        r13 = r12;
    L_0x02bb:
        r9 = r25;
        goto L_0x0127;
    L_0x02bf:
        r9 = 0;
        r10 = 0;
        r15 = 0;
    L_0x02c2:
        if (r28 < 0) goto L_0x037e;
    L_0x02c4:
        r1 = r14.extractor;	 Catch:{ Exception -> 0x0372, all -> 0x0CLASSNAME }
        r34 = r15;
        r15 = r28;
        r1 = r1.getTrackFormat(r15);	 Catch:{ Exception -> 0x0372, all -> 0x0CLASSNAME }
        r28 = r13;
        r13 = "mime";
        r13 = r1.getString(r13);	 Catch:{ Exception -> 0x0372, all -> 0x0CLASSNAME }
        r42 = r10;
        r10 = "audio/mp4a-latm";
        r10 = r13.equals(r10);	 Catch:{ Exception -> 0x0372, all -> 0x0CLASSNAME }
        r13 = r14.mediaMuxer;	 Catch:{ Exception -> 0x0372, all -> 0x0CLASSNAME }
        r43 = r4;
        r4 = 1;
        r13 = r13.addTrack(r1, r4);	 Catch:{ Exception -> 0x0364, all -> 0x0CLASSNAME }
        if (r10 == 0) goto L_0x0325;
    L_0x02e9:
        r4 = r14.extractor;	 Catch:{ Exception -> 0x031f, all -> 0x0CLASSNAME }
        r4.selectTrack(r15);	 Catch:{ Exception -> 0x031f, all -> 0x0CLASSNAME }
        r4 = "max-input-size";
        r1 = r1.getInteger(r4);	 Catch:{ Exception -> 0x031f, all -> 0x0CLASSNAME }
        r1 = java.nio.ByteBuffer.allocateDirect(r1);	 Catch:{ Exception -> 0x031f, all -> 0x0CLASSNAME }
        r32 = 0;
        r4 = (r7 > r32 ? 1 : (r7 == r32 ? 0 : -1));
        if (r4 <= 0) goto L_0x0309;
    L_0x02fe:
        r4 = r14.extractor;	 Catch:{ Exception -> 0x031f, all -> 0x0CLASSNAME }
        r35 = r1;
        r1 = 0;
        r4.seekTo(r7, r1);	 Catch:{ Exception -> 0x031f, all -> 0x0CLASSNAME }
        r36 = r10;
        goto L_0x0315;
    L_0x0309:
        r35 = r1;
        r1 = r14.extractor;	 Catch:{ Exception -> 0x031f, all -> 0x0CLASSNAME }
        r36 = r10;
        r4 = 0;
        r10 = 0;
        r1.seekTo(r10, r4);	 Catch:{ Exception -> 0x031f, all -> 0x0CLASSNAME }
    L_0x0315:
        r11 = r73;
        r4 = r13;
        r1 = r35;
        r13 = r36;
        r10 = 0;
        goto L_0x038e;
    L_0x031f:
        r0 = move-exception;
        r10 = r70;
        r1 = r0;
        r13 = r12;
        goto L_0x036c;
    L_0x0325:
        r36 = r10;
        r4 = new android.media.MediaExtractor;	 Catch:{ Exception -> 0x0364, all -> 0x0CLASSNAME }
        r4.<init>();	 Catch:{ Exception -> 0x0364, all -> 0x0CLASSNAME }
        r11 = r63;
        r4.setDataSource(r11);	 Catch:{ Exception -> 0x0364, all -> 0x0CLASSNAME }
        r4.selectTrack(r15);	 Catch:{ Exception -> 0x0364, all -> 0x0CLASSNAME }
        r10 = 0;
        r32 = (r7 > r10 ? 1 : (r7 == r10 ? 0 : -1));
        if (r32 <= 0) goto L_0x033f;
    L_0x033a:
        r10 = 0;
        r4.seekTo(r7, r10);	 Catch:{ Exception -> 0x031f, all -> 0x0CLASSNAME }
        goto L_0x0344;
    L_0x033f:
        r11 = r10;
        r10 = 0;
        r4.seekTo(r11, r10);	 Catch:{ Exception -> 0x0364, all -> 0x0CLASSNAME }
    L_0x0344:
        r10 = new org.telegram.messenger.video.AudioRecoder;	 Catch:{ Exception -> 0x0364, all -> 0x0CLASSNAME }
        r10.<init>(r1, r4, r15);	 Catch:{ Exception -> 0x0364, all -> 0x0CLASSNAME }
        r10.startTime = r7;	 Catch:{ Exception -> 0x0356, all -> 0x0CLASSNAME }
        r11 = r73;
        r10.endTime = r11;	 Catch:{ Exception -> 0x0354, all -> 0x0CLASSNAME }
        r4 = r13;
        r13 = r36;
        r1 = 0;
        goto L_0x038e;
    L_0x0354:
        r0 = move-exception;
        goto L_0x0359;
    L_0x0356:
        r0 = move-exception;
        r11 = r73;
    L_0x0359:
        r13 = r67;
        r1 = r0;
        r57 = r10;
        r9 = r25;
        r4 = r43;
        goto L_0x03df;
    L_0x0364:
        r0 = move-exception;
        r11 = r73;
        r13 = r67;
        r10 = r70;
        r1 = r0;
    L_0x036c:
        r9 = r25;
        r4 = r43;
        goto L_0x0127;
    L_0x0372:
        r0 = move-exception;
        r11 = r73;
        r43 = r4;
        r13 = r67;
        r10 = r70;
        r1 = r0;
        goto L_0x02bb;
    L_0x037e:
        r11 = r73;
        r43 = r4;
        r42 = r10;
        r34 = r15;
        r15 = r28;
        r28 = r13;
        r1 = 0;
        r4 = -5;
        r10 = 0;
        r13 = 1;
    L_0x038e:
        r62.checkConversionCanceled();	 Catch:{ Exception -> 0x0aba, all -> 0x0CLASSNAME }
        r50 = r20;
        r47 = r34;
        r20 = 0;
        r21 = 0;
        r44 = 0;
        r45 = 0;
        r46 = -5;
        r48 = 0;
        r49 = 1;
        r52 = 0;
    L_0x03a5:
        if (r20 == 0) goto L_0x03c1;
    L_0x03a7:
        if (r13 != 0) goto L_0x03ac;
    L_0x03a9:
        if (r21 != 0) goto L_0x03ac;
    L_0x03ab:
        goto L_0x03c1;
    L_0x03ac:
        r13 = r67;
        r12 = r68;
        r11 = r69;
        r57 = r10;
        r9 = r25;
        r4 = r43;
        r1 = 0;
        r17 = 0;
        r60 = 1;
        r10 = r70;
        goto L_0x0bbd;
    L_0x03c1:
        r62.checkConversionCanceled();	 Catch:{ Exception -> 0x0aba, all -> 0x0CLASSNAME }
        if (r13 != 0) goto L_0x03e7;
    L_0x03c6:
        if (r10 == 0) goto L_0x03e7;
    L_0x03c8:
        r54 = r2;
        r2 = r14.mediaMuxer;	 Catch:{ Exception -> 0x03d3, all -> 0x0CLASSNAME }
        r2 = r10.step(r2, r4);	 Catch:{ Exception -> 0x03d3, all -> 0x0CLASSNAME }
        r21 = r2;
        goto L_0x03e9;
    L_0x03d3:
        r0 = move-exception;
        r13 = r67;
        r1 = r0;
        r57 = r10;
        r9 = r25;
        r4 = r43;
        r2 = r54;
    L_0x03df:
        r56 = 0;
        r60 = 1;
        r10 = r70;
        goto L_0x0b7e;
    L_0x03e7:
        r54 = r2;
    L_0x03e9:
        r56 = r5;
        r55 = r6;
        r5 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        if (r44 != 0) goto L_0x0543;
    L_0x03f1:
        r2 = r14.extractor;	 Catch:{ Exception -> 0x052f, all -> 0x0CLASSNAME }
        r2 = r2.getSampleTrackIndex();	 Catch:{ Exception -> 0x052f, all -> 0x0CLASSNAME }
        r57 = r10;
        r10 = r25;
        if (r2 != r10) goto L_0x045e;
    L_0x03fd:
        r2 = r3.dequeueInputBuffer(r5);	 Catch:{ Exception -> 0x0452, all -> 0x0CLASSNAME }
        if (r2 < 0) goto L_0x0444;
    L_0x0403:
        r5 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0452, all -> 0x0CLASSNAME }
        r6 = 21;
        if (r5 >= r6) goto L_0x040c;
    L_0x0409:
        r5 = r9[r2];	 Catch:{ Exception -> 0x0452, all -> 0x0CLASSNAME }
        goto L_0x0410;
    L_0x040c:
        r5 = r3.getInputBuffer(r2);	 Catch:{ Exception -> 0x0452, all -> 0x0CLASSNAME }
    L_0x0410:
        r6 = r14.extractor;	 Catch:{ Exception -> 0x0452, all -> 0x0CLASSNAME }
        r25 = r9;
        r9 = 0;
        r37 = r6.readSampleData(r5, r9);	 Catch:{ Exception -> 0x0452, all -> 0x0CLASSNAME }
        if (r37 >= 0) goto L_0x042d;
    L_0x041b:
        r36 = 0;
        r37 = 0;
        r38 = 0;
        r40 = 4;
        r34 = r3;
        r35 = r2;
        r34.queueInputBuffer(r35, r36, r37, r38, r40);	 Catch:{ Exception -> 0x0452, all -> 0x0CLASSNAME }
        r44 = 1;
        goto L_0x0446;
    L_0x042d:
        r36 = 0;
        r5 = r14.extractor;	 Catch:{ Exception -> 0x0452, all -> 0x0CLASSNAME }
        r38 = r5.getSampleTime();	 Catch:{ Exception -> 0x0452, all -> 0x0CLASSNAME }
        r40 = 0;
        r34 = r3;
        r35 = r2;
        r34.queueInputBuffer(r35, r36, r37, r38, r40);	 Catch:{ Exception -> 0x0452, all -> 0x0CLASSNAME }
        r2 = r14.extractor;	 Catch:{ Exception -> 0x0452, all -> 0x0CLASSNAME }
        r2.advance();	 Catch:{ Exception -> 0x0452, all -> 0x0CLASSNAME }
        goto L_0x0446;
    L_0x0444:
        r25 = r9;
    L_0x0446:
        r59 = r4;
        r60 = r10;
        r6 = r22;
        r4 = r44;
        r22 = r1;
        goto L_0x0506;
    L_0x0452:
        r0 = move-exception;
        r13 = r67;
        r1 = r0;
        r9 = r10;
        r4 = r43;
        r2 = r54;
        r5 = r56;
        goto L_0x03df;
    L_0x045e:
        r25 = r9;
        if (r13 == 0) goto L_0x04f5;
    L_0x0462:
        r5 = -1;
        if (r15 == r5) goto L_0x04f5;
    L_0x0465:
        if (r2 != r15) goto L_0x04f5;
    L_0x0467:
        r2 = r14.extractor;	 Catch:{ Exception -> 0x0452, all -> 0x0CLASSNAME }
        r5 = 0;
        r2 = r2.readSampleData(r1, r5);	 Catch:{ Exception -> 0x0452, all -> 0x0CLASSNAME }
        r6 = r22;
        r6.size = r2;	 Catch:{ Exception -> 0x0452, all -> 0x0CLASSNAME }
        r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0452, all -> 0x0CLASSNAME }
        r9 = 21;
        if (r2 >= r9) goto L_0x0480;
    L_0x0478:
        r1.position(r5);	 Catch:{ Exception -> 0x0452, all -> 0x0CLASSNAME }
        r2 = r6.size;	 Catch:{ Exception -> 0x0452, all -> 0x0CLASSNAME }
        r1.limit(r2);	 Catch:{ Exception -> 0x0452, all -> 0x0CLASSNAME }
    L_0x0480:
        r2 = r6.size;	 Catch:{ Exception -> 0x0452, all -> 0x0CLASSNAME }
        if (r2 < 0) goto L_0x0493;
    L_0x0484:
        r2 = r14.extractor;	 Catch:{ Exception -> 0x0452, all -> 0x0CLASSNAME }
        r5 = r10;
        r9 = r2.getSampleTime();	 Catch:{ Exception -> 0x04ed, all -> 0x0CLASSNAME }
        r6.presentationTimeUs = r9;	 Catch:{ Exception -> 0x04ed, all -> 0x0CLASSNAME }
        r2 = r14.extractor;	 Catch:{ Exception -> 0x04ed, all -> 0x0CLASSNAME }
        r2.advance();	 Catch:{ Exception -> 0x04ed, all -> 0x0CLASSNAME }
        goto L_0x0499;
    L_0x0493:
        r5 = r10;
        r2 = 0;
        r6.size = r2;	 Catch:{ Exception -> 0x04ed, all -> 0x0CLASSNAME }
        r44 = 1;
    L_0x0499:
        r2 = r6.size;	 Catch:{ Exception -> 0x04ed, all -> 0x0CLASSNAME }
        if (r2 <= 0) goto L_0x04e6;
    L_0x049d:
        r9 = 0;
        r2 = (r11 > r9 ? 1 : (r11 == r9 ? 0 : -1));
        if (r2 < 0) goto L_0x04a9;
    L_0x04a3:
        r9 = r6.presentationTimeUs;	 Catch:{ Exception -> 0x04ed, all -> 0x0CLASSNAME }
        r2 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1));
        if (r2 >= 0) goto L_0x04e6;
    L_0x04a9:
        r2 = 0;
        r6.offset = r2;	 Catch:{ Exception -> 0x04ed, all -> 0x0CLASSNAME }
        r9 = r14.extractor;	 Catch:{ Exception -> 0x04ed, all -> 0x0CLASSNAME }
        r9 = r9.getSampleFlags();	 Catch:{ Exception -> 0x04ed, all -> 0x0CLASSNAME }
        r6.flags = r9;	 Catch:{ Exception -> 0x04ed, all -> 0x0CLASSNAME }
        r9 = r14.mediaMuxer;	 Catch:{ Exception -> 0x04ed, all -> 0x0CLASSNAME }
        r9 = r9.writeSampleData(r4, r1, r6, r2);	 Catch:{ Exception -> 0x04ed, all -> 0x0CLASSNAME }
        r32 = 0;
        r2 = (r9 > r32 ? 1 : (r9 == r32 ? 0 : -1));
        if (r2 == 0) goto L_0x04e6;
    L_0x04c0:
        r2 = r14.callback;	 Catch:{ Exception -> 0x04ed, all -> 0x0CLASSNAME }
        if (r2 == 0) goto L_0x04e6;
    L_0x04c4:
        r22 = r1;
        r1 = r6.presentationTimeUs;	 Catch:{ Exception -> 0x04ed, all -> 0x0CLASSNAME }
        r1 = r1 - r7;
        r34 = (r1 > r52 ? 1 : (r1 == r52 ? 0 : -1));
        if (r34 <= 0) goto L_0x04d1;
    L_0x04cd:
        r1 = r6.presentationTimeUs;	 Catch:{ Exception -> 0x04ed, all -> 0x0CLASSNAME }
        r52 = r1 - r7;
    L_0x04d1:
        r59 = r4;
        r1 = r52;
        r4 = r14.callback;	 Catch:{ Exception -> 0x04ed, all -> 0x0CLASSNAME }
        r60 = r5;
        r5 = (float) r1;
        r34 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r5 = r5 / r34;
        r5 = r5 / r18;
        r4.didWriteData(r9, r5);	 Catch:{ Exception -> 0x0520, all -> 0x0CLASSNAME }
        r52 = r1;
        goto L_0x0504;
    L_0x04e6:
        r22 = r1;
        r59 = r4;
        r60 = r5;
        goto L_0x0504;
    L_0x04ed:
        r0 = move-exception;
        r13 = r67;
        r10 = r70;
        r1 = r0;
        r9 = r5;
        goto L_0x0539;
    L_0x04f5:
        r59 = r4;
        r60 = r10;
        r6 = r22;
        r22 = r1;
        r1 = -1;
        if (r2 != r1) goto L_0x0504;
    L_0x0500:
        r4 = r44;
        r1 = 1;
        goto L_0x0507;
    L_0x0504:
        r4 = r44;
    L_0x0506:
        r1 = 0;
    L_0x0507:
        if (r1 == 0) goto L_0x0551;
    L_0x0509:
        r1 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r35 = r3.dequeueInputBuffer(r1);	 Catch:{ Exception -> 0x0520, all -> 0x0CLASSNAME }
        if (r35 < 0) goto L_0x0551;
    L_0x0511:
        r36 = 0;
        r37 = 0;
        r38 = 0;
        r40 = 4;
        r34 = r3;
        r34.queueInputBuffer(r35, r36, r37, r38, r40);	 Catch:{ Exception -> 0x0520, all -> 0x0CLASSNAME }
        r4 = 1;
        goto L_0x0551;
    L_0x0520:
        r0 = move-exception;
        r13 = r67;
        r10 = r70;
        r1 = r0;
        r4 = r43;
        r2 = r54;
        r5 = r56;
    L_0x052c:
        r9 = r60;
        goto L_0x053f;
    L_0x052f:
        r0 = move-exception;
        r57 = r10;
        r13 = r67;
        r10 = r70;
        r1 = r0;
        r9 = r25;
    L_0x0539:
        r4 = r43;
        r2 = r54;
        r5 = r56;
    L_0x053f:
        r56 = 0;
        goto L_0x012b;
    L_0x0543:
        r59 = r4;
        r57 = r10;
        r6 = r22;
        r60 = r25;
        r22 = r1;
        r25 = r9;
        r4 = r44;
    L_0x0551:
        r1 = r45 ^ 1;
        r9 = r1;
        r44 = r4;
        r2 = r46;
        r1 = 1;
    L_0x0559:
        if (r9 != 0) goto L_0x0574;
    L_0x055b:
        if (r1 == 0) goto L_0x055e;
    L_0x055d:
        goto L_0x0574;
    L_0x055e:
        r46 = r2;
        r1 = r22;
        r9 = r25;
        r2 = r54;
        r5 = r56;
        r10 = r57;
        r4 = r59;
        r25 = r60;
        r22 = r6;
        r6 = r55;
        goto L_0x03a5;
    L_0x0574:
        r62.checkConversionCanceled();	 Catch:{ Exception -> 0x0aa3, all -> 0x0CLASSNAME }
        if (r78 == 0) goto L_0x0581;
    L_0x0579:
        r4 = 22000; // 0x55f0 float:3.0829E-41 double:1.08694E-319;
        r39 = r9;
        r9 = r4;
        r5 = r56;
        goto L_0x0587;
    L_0x0581:
        r39 = r9;
        r5 = r56;
        r9 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
    L_0x0587:
        r4 = r5.dequeueOutputBuffer(r6, r9);	 Catch:{ Exception -> 0x0a90, all -> 0x0CLASSNAME }
        r9 = -1;
        if (r4 != r9) goto L_0x05a0;
    L_0x058e:
        r9 = r68;
        r12 = r2;
        r46 = r13;
        r11 = r20;
        r10 = r55;
        r1 = -1;
        r19 = 0;
    L_0x059a:
        r20 = 2;
        r13 = r67;
        goto L_0x0775;
    L_0x05a0:
        r9 = -3;
        if (r4 != r9) goto L_0x05c6;
    L_0x05a3:
        r9 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x05ba, all -> 0x0CLASSNAME }
        r10 = 21;
        if (r9 >= r10) goto L_0x05ad;
    L_0x05a9:
        r47 = r5.getOutputBuffers();	 Catch:{ Exception -> 0x05ba, all -> 0x0CLASSNAME }
    L_0x05ad:
        r9 = r68;
        r19 = r1;
        r12 = r2;
        r46 = r13;
        r11 = r20;
    L_0x05b6:
        r10 = r55;
        r1 = -1;
        goto L_0x059a;
    L_0x05ba:
        r0 = move-exception;
        r13 = r67;
    L_0x05bd:
        r10 = r70;
        r1 = r0;
        r4 = r43;
        r2 = r54;
        goto L_0x052c;
    L_0x05c6:
        r9 = -2;
        if (r4 != r9) goto L_0x0610;
    L_0x05c9:
        r9 = r5.getOutputFormat();	 Catch:{ Exception -> 0x05ba, all -> 0x0CLASSNAME }
        r10 = -5;
        if (r2 != r10) goto L_0x0604;
    L_0x05d0:
        if (r9 == 0) goto L_0x0604;
    L_0x05d2:
        r2 = r14.mediaMuxer;	 Catch:{ Exception -> 0x05ba, all -> 0x0CLASSNAME }
        r10 = 0;
        r2 = r2.addTrack(r9, r10);	 Catch:{ Exception -> 0x05ba, all -> 0x0CLASSNAME }
        r10 = "prepend-sps-pps-to-idr-frames";
        r10 = r9.containsKey(r10);	 Catch:{ Exception -> 0x05ba, all -> 0x0CLASSNAME }
        if (r10 == 0) goto L_0x0604;
    L_0x05e1:
        r10 = "prepend-sps-pps-to-idr-frames";
        r10 = r9.getInteger(r10);	 Catch:{ Exception -> 0x05ba, all -> 0x0CLASSNAME }
        r34 = r1;
        r1 = 1;
        if (r10 != r1) goto L_0x0606;
    L_0x05ec:
        r1 = "csd-0";
        r1 = r9.getByteBuffer(r1);	 Catch:{ Exception -> 0x05ba, all -> 0x0CLASSNAME }
        r10 = "csd-1";
        r9 = r9.getByteBuffer(r10);	 Catch:{ Exception -> 0x05ba, all -> 0x0CLASSNAME }
        r1 = r1.limit();	 Catch:{ Exception -> 0x05ba, all -> 0x0CLASSNAME }
        r9 = r9.limit();	 Catch:{ Exception -> 0x05ba, all -> 0x0CLASSNAME }
        r1 = r1 + r9;
        r48 = r1;
        goto L_0x0606;
    L_0x0604:
        r34 = r1;
    L_0x0606:
        r9 = r68;
        r12 = r2;
        r46 = r13;
        r11 = r20;
        r19 = r34;
        goto L_0x05b6;
    L_0x0610:
        r34 = r1;
        if (r4 < 0) goto L_0x0a60;
    L_0x0614:
        r1 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0a90, all -> 0x0CLASSNAME }
        r9 = 21;
        if (r1 >= r9) goto L_0x061d;
    L_0x061a:
        r1 = r47[r4];	 Catch:{ Exception -> 0x05ba, all -> 0x0CLASSNAME }
        goto L_0x0621;
    L_0x061d:
        r1 = r5.getOutputBuffer(r4);	 Catch:{ Exception -> 0x0a90, all -> 0x0CLASSNAME }
    L_0x0621:
        if (r1 == 0) goto L_0x0a35;
    L_0x0623:
        r10 = r6.size;	 Catch:{ Exception -> 0x0a90, all -> 0x0CLASSNAME }
        r9 = 1;
        if (r10 <= r9) goto L_0x0759;
    L_0x0628:
        r10 = r6.flags;	 Catch:{ Exception -> 0x0752, all -> 0x0CLASSNAME }
        r20 = 2;
        r10 = r10 & 2;
        if (r10 != 0) goto L_0x06d4;
    L_0x0630:
        if (r48 == 0) goto L_0x0643;
    L_0x0632:
        r10 = r6.flags;	 Catch:{ Exception -> 0x05ba, all -> 0x0CLASSNAME }
        r10 = r10 & r9;
        if (r10 == 0) goto L_0x0643;
    L_0x0637:
        r9 = r6.offset;	 Catch:{ Exception -> 0x05ba, all -> 0x0CLASSNAME }
        r9 = r9 + r48;
        r6.offset = r9;	 Catch:{ Exception -> 0x05ba, all -> 0x0CLASSNAME }
        r9 = r6.size;	 Catch:{ Exception -> 0x05ba, all -> 0x0CLASSNAME }
        r9 = r9 - r48;
        r6.size = r9;	 Catch:{ Exception -> 0x05ba, all -> 0x0CLASSNAME }
    L_0x0643:
        if (r49 == 0) goto L_0x069e;
    L_0x0645:
        r9 = r6.flags;	 Catch:{ Exception -> 0x05ba, all -> 0x0CLASSNAME }
        r10 = 1;
        r9 = r9 & r10;
        if (r9 == 0) goto L_0x069e;
    L_0x064b:
        r9 = r6.size;	 Catch:{ Exception -> 0x05ba, all -> 0x0CLASSNAME }
        r10 = 100;
        if (r9 <= r10) goto L_0x0699;
    L_0x0651:
        r9 = r6.offset;	 Catch:{ Exception -> 0x05ba, all -> 0x0CLASSNAME }
        r1.position(r9);	 Catch:{ Exception -> 0x05ba, all -> 0x0CLASSNAME }
        r9 = 100;
        r9 = new byte[r9];	 Catch:{ Exception -> 0x05ba, all -> 0x0CLASSNAME }
        r1.get(r9);	 Catch:{ Exception -> 0x05ba, all -> 0x0CLASSNAME }
        r46 = r13;
        r10 = 0;
        r26 = 0;
    L_0x0662:
        r13 = r9.length;	 Catch:{ Exception -> 0x05ba, all -> 0x0CLASSNAME }
        r13 = r13 + -4;
        if (r10 >= r13) goto L_0x069b;
    L_0x0667:
        r13 = r9[r10];	 Catch:{ Exception -> 0x05ba, all -> 0x0CLASSNAME }
        if (r13 != 0) goto L_0x0692;
    L_0x066b:
        r13 = r10 + 1;
        r13 = r9[r13];	 Catch:{ Exception -> 0x05ba, all -> 0x0CLASSNAME }
        if (r13 != 0) goto L_0x0692;
    L_0x0671:
        r13 = r10 + 2;
        r13 = r9[r13];	 Catch:{ Exception -> 0x05ba, all -> 0x0CLASSNAME }
        if (r13 != 0) goto L_0x0692;
    L_0x0677:
        r13 = r10 + 3;
        r13 = r9[r13];	 Catch:{ Exception -> 0x05ba, all -> 0x0CLASSNAME }
        r35 = r9;
        r9 = 1;
        if (r13 != r9) goto L_0x0694;
    L_0x0680:
        r13 = r26 + 1;
        if (r13 <= r9) goto L_0x068f;
    L_0x0684:
        r9 = r6.offset;	 Catch:{ Exception -> 0x05ba, all -> 0x0CLASSNAME }
        r9 = r9 + r10;
        r6.offset = r9;	 Catch:{ Exception -> 0x05ba, all -> 0x0CLASSNAME }
        r9 = r6.size;	 Catch:{ Exception -> 0x05ba, all -> 0x0CLASSNAME }
        r9 = r9 - r10;
        r6.size = r9;	 Catch:{ Exception -> 0x05ba, all -> 0x0CLASSNAME }
        goto L_0x069b;
    L_0x068f:
        r26 = r13;
        goto L_0x0694;
    L_0x0692:
        r35 = r9;
    L_0x0694:
        r10 = r10 + 1;
        r9 = r35;
        goto L_0x0662;
    L_0x0699:
        r46 = r13;
    L_0x069b:
        r49 = 0;
        goto L_0x06a0;
    L_0x069e:
        r46 = r13;
    L_0x06a0:
        r9 = r14.mediaMuxer;	 Catch:{ Exception -> 0x05ba, all -> 0x0CLASSNAME }
        r10 = 1;
        r11 = r9.writeSampleData(r2, r1, r6, r10);	 Catch:{ Exception -> 0x05ba, all -> 0x0CLASSNAME }
        r9 = 0;
        r1 = (r11 > r9 ? 1 : (r11 == r9 ? 0 : -1));
        if (r1 == 0) goto L_0x06cc;
    L_0x06ad:
        r1 = r14.callback;	 Catch:{ Exception -> 0x05ba, all -> 0x0CLASSNAME }
        if (r1 == 0) goto L_0x06cc;
    L_0x06b1:
        r9 = r6.presentationTimeUs;	 Catch:{ Exception -> 0x05ba, all -> 0x0CLASSNAME }
        r9 = r9 - r7;
        r1 = (r9 > r52 ? 1 : (r9 == r52 ? 0 : -1));
        if (r1 <= 0) goto L_0x06bc;
    L_0x06b8:
        r9 = r6.presentationTimeUs;	 Catch:{ Exception -> 0x05ba, all -> 0x0CLASSNAME }
        r52 = r9 - r7;
    L_0x06bc:
        r9 = r52;
        r1 = r14.callback;	 Catch:{ Exception -> 0x05ba, all -> 0x0CLASSNAME }
        r13 = (float) r9;	 Catch:{ Exception -> 0x05ba, all -> 0x0CLASSNAME }
        r26 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r13 = r13 / r26;
        r13 = r13 / r18;
        r1.didWriteData(r11, r13);	 Catch:{ Exception -> 0x05ba, all -> 0x0CLASSNAME }
        r52 = r9;
    L_0x06cc:
        r13 = r67;
        r9 = r68;
        r10 = r55;
        goto L_0x0763;
    L_0x06d4:
        r46 = r13;
        r9 = -5;
        if (r2 != r9) goto L_0x06cc;
    L_0x06d9:
        r2 = r6.size;	 Catch:{ Exception -> 0x0752, all -> 0x0CLASSNAME }
        r2 = new byte[r2];	 Catch:{ Exception -> 0x0752, all -> 0x0CLASSNAME }
        r10 = r6.offset;	 Catch:{ Exception -> 0x0752, all -> 0x0CLASSNAME }
        r11 = r6.size;	 Catch:{ Exception -> 0x0752, all -> 0x0CLASSNAME }
        r10 = r10 + r11;
        r1.limit(r10);	 Catch:{ Exception -> 0x0752, all -> 0x0CLASSNAME }
        r10 = r6.offset;	 Catch:{ Exception -> 0x0752, all -> 0x0CLASSNAME }
        r1.position(r10);	 Catch:{ Exception -> 0x0752, all -> 0x0CLASSNAME }
        r1.get(r2);	 Catch:{ Exception -> 0x0752, all -> 0x0CLASSNAME }
        r1 = r6.size;	 Catch:{ Exception -> 0x0752, all -> 0x0CLASSNAME }
        r10 = 1;
        r1 = r1 - r10;
    L_0x06f1:
        r11 = 3;
        if (r1 < 0) goto L_0x072f;
    L_0x06f4:
        if (r1 <= r11) goto L_0x072f;
    L_0x06f6:
        r12 = r2[r1];	 Catch:{ Exception -> 0x05ba, all -> 0x0CLASSNAME }
        if (r12 != r10) goto L_0x072a;
    L_0x06fa:
        r10 = r1 + -1;
        r10 = r2[r10];	 Catch:{ Exception -> 0x05ba, all -> 0x0CLASSNAME }
        if (r10 != 0) goto L_0x072a;
    L_0x0700:
        r10 = r1 + -2;
        r10 = r2[r10];	 Catch:{ Exception -> 0x05ba, all -> 0x0CLASSNAME }
        if (r10 != 0) goto L_0x072a;
    L_0x0706:
        r10 = r1 + -3;
        r12 = r2[r10];	 Catch:{ Exception -> 0x05ba, all -> 0x0CLASSNAME }
        if (r12 != 0) goto L_0x072a;
    L_0x070c:
        r1 = java.nio.ByteBuffer.allocate(r10);	 Catch:{ Exception -> 0x05ba, all -> 0x0CLASSNAME }
        r12 = r6.size;	 Catch:{ Exception -> 0x05ba, all -> 0x0CLASSNAME }
        r12 = r12 - r10;
        r12 = java.nio.ByteBuffer.allocate(r12);	 Catch:{ Exception -> 0x05ba, all -> 0x0CLASSNAME }
        r13 = 0;
        r9 = r1.put(r2, r13, r10);	 Catch:{ Exception -> 0x05ba, all -> 0x0CLASSNAME }
        r9.position(r13);	 Catch:{ Exception -> 0x05ba, all -> 0x0CLASSNAME }
        r9 = r6.size;	 Catch:{ Exception -> 0x05ba, all -> 0x0CLASSNAME }
        r9 = r9 - r10;
        r2 = r12.put(r2, r10, r9);	 Catch:{ Exception -> 0x05ba, all -> 0x0CLASSNAME }
        r2.position(r13);	 Catch:{ Exception -> 0x05ba, all -> 0x0CLASSNAME }
        goto L_0x0731;
    L_0x072a:
        r1 = r1 + -1;
        r9 = -5;
        r10 = 1;
        goto L_0x06f1;
    L_0x072f:
        r1 = 0;
        r12 = 0;
    L_0x0731:
        r13 = r67;
        r9 = r68;
        r10 = r55;
        r2 = android.media.MediaFormat.createVideoFormat(r10, r13, r9);	 Catch:{ Exception -> 0x07dd, all -> 0x0CLASSNAME }
        if (r1 == 0) goto L_0x0749;
    L_0x073d:
        if (r12 == 0) goto L_0x0749;
    L_0x073f:
        r11 = "csd-0";
        r2.setByteBuffer(r11, r1);	 Catch:{ Exception -> 0x07dd, all -> 0x0CLASSNAME }
        r1 = "csd-1";
        r2.setByteBuffer(r1, r12);	 Catch:{ Exception -> 0x07dd, all -> 0x0CLASSNAME }
    L_0x0749:
        r1 = r14.mediaMuxer;	 Catch:{ Exception -> 0x07dd, all -> 0x0CLASSNAME }
        r11 = 0;
        r1 = r1.addTrack(r2, r11);	 Catch:{ Exception -> 0x07dd, all -> 0x0CLASSNAME }
        r2 = r1;
        goto L_0x0763;
    L_0x0752:
        r0 = move-exception;
        r13 = r67;
        r9 = r68;
        goto L_0x05bd;
    L_0x0759:
        r9 = r68;
        r46 = r13;
        r10 = r55;
        r20 = 2;
        r13 = r67;
    L_0x0763:
        r1 = r6.flags;	 Catch:{ Exception -> 0x0a00, all -> 0x0CLASSNAME }
        r1 = r1 & 4;
        if (r1 == 0) goto L_0x076b;
    L_0x0769:
        r1 = 1;
        goto L_0x076c;
    L_0x076b:
        r1 = 0;
    L_0x076c:
        r11 = 0;
        r5.releaseOutputBuffer(r4, r11);	 Catch:{ Exception -> 0x0a00, all -> 0x0CLASSNAME }
        r11 = r1;
        r12 = r2;
        r19 = r34;
        r1 = -1;
    L_0x0775:
        if (r4 == r1) goto L_0x0788;
    L_0x0777:
        r56 = r5;
        r55 = r10;
        r20 = r11;
        r2 = r12;
        r1 = r19;
        r9 = r39;
        r13 = r46;
        r11 = r73;
        goto L_0x0559;
    L_0x0788:
        if (r45 != 0) goto L_0x0a03;
    L_0x078a:
        r1 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r4 = r3.dequeueOutputBuffer(r6, r1);	 Catch:{ Exception -> 0x0a00, all -> 0x0CLASSNAME }
        r1 = -1;
        if (r4 != r1) goto L_0x07b7;
    L_0x0793:
        r1 = r5;
        r8 = r6;
        r30 = r22;
        r16 = r54;
        r22 = r59;
        r9 = r60;
        r2 = 18;
        r26 = -5;
        r29 = 2;
        r39 = 0;
    L_0x07a5:
        r41 = 0;
        r54 = 0;
        r56 = 0;
        r58 = 3;
        r60 = 1;
        r59 = r43;
        r43 = r3;
    L_0x07b3:
        r3 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        goto L_0x0a15;
    L_0x07b7:
        r2 = -3;
        if (r4 != r2) goto L_0x07bc;
    L_0x07ba:
        goto L_0x0a03;
    L_0x07bc:
        r2 = -2;
        if (r4 != r2) goto L_0x07e0;
    L_0x07bf:
        r2 = r3.getOutputFormat();	 Catch:{ Exception -> 0x07dd, all -> 0x0CLASSNAME }
        r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x07dd, all -> 0x0CLASSNAME }
        if (r4 == 0) goto L_0x0a03;
    L_0x07c7:
        r4 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x07dd, all -> 0x0CLASSNAME }
        r4.<init>();	 Catch:{ Exception -> 0x07dd, all -> 0x0CLASSNAME }
        r1 = "newFormat = ";
        r4.append(r1);	 Catch:{ Exception -> 0x07dd, all -> 0x0CLASSNAME }
        r4.append(r2);	 Catch:{ Exception -> 0x07dd, all -> 0x0CLASSNAME }
        r1 = r4.toString();	 Catch:{ Exception -> 0x07dd, all -> 0x0CLASSNAME }
        org.telegram.messenger.FileLog.d(r1);	 Catch:{ Exception -> 0x07dd, all -> 0x0CLASSNAME }
        goto L_0x0a03;
    L_0x07dd:
        r0 = move-exception;
        goto L_0x05bd;
    L_0x07e0:
        if (r4 < 0) goto L_0x09db;
    L_0x07e2:
        r1 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0a00, all -> 0x0CLASSNAME }
        r2 = 18;
        if (r1 < r2) goto L_0x07f2;
    L_0x07e8:
        r1 = r6.size;	 Catch:{ Exception -> 0x07dd, all -> 0x0CLASSNAME }
        if (r1 == 0) goto L_0x07ee;
    L_0x07ec:
        r1 = 1;
        goto L_0x07ef;
    L_0x07ee:
        r1 = 0;
    L_0x07ef:
        r32 = 0;
        goto L_0x0804;
    L_0x07f2:
        r1 = r6.size;	 Catch:{ Exception -> 0x0a00, all -> 0x0CLASSNAME }
        if (r1 != 0) goto L_0x0801;
    L_0x07f6:
        r1 = r6.presentationTimeUs;	 Catch:{ Exception -> 0x07dd, all -> 0x0CLASSNAME }
        r32 = 0;
        r34 = (r1 > r32 ? 1 : (r1 == r32 ? 0 : -1));
        if (r34 == 0) goto L_0x07ff;
    L_0x07fe:
        goto L_0x0803;
    L_0x07ff:
        r1 = 0;
        goto L_0x0804;
    L_0x0801:
        r32 = 0;
    L_0x0803:
        r1 = 1;
    L_0x0804:
        r2 = (r73 > r32 ? 1 : (r73 == r32 ? 0 : -1));
        if (r2 <= 0) goto L_0x081f;
    L_0x0808:
        r34 = r1;
        r1 = r6.presentationTimeUs;	 Catch:{ Exception -> 0x07dd, all -> 0x0CLASSNAME }
        r35 = (r1 > r73 ? 1 : (r1 == r73 ? 0 : -1));
        if (r35 < 0) goto L_0x0821;
    L_0x0810:
        r1 = r6.flags;	 Catch:{ Exception -> 0x07dd, all -> 0x0CLASSNAME }
        r1 = r1 | 4;
        r6.flags = r1;	 Catch:{ Exception -> 0x07dd, all -> 0x0CLASSNAME }
        r32 = 0;
        r34 = 0;
        r44 = 1;
        r45 = 1;
        goto L_0x0823;
    L_0x081f:
        r34 = r1;
    L_0x0821:
        r32 = 0;
    L_0x0823:
        r1 = (r7 > r32 ? 1 : (r7 == r32 ? 0 : -1));
        if (r1 <= 0) goto L_0x085b;
    L_0x0827:
        r1 = -1;
        r35 = (r50 > r1 ? 1 : (r50 == r1 ? 0 : -1));
        if (r35 != 0) goto L_0x085b;
    L_0x082d:
        r1 = r6.presentationTimeUs;	 Catch:{ Exception -> 0x07dd, all -> 0x0CLASSNAME }
        r35 = (r1 > r7 ? 1 : (r1 == r7 ? 0 : -1));
        if (r35 >= 0) goto L_0x0857;
    L_0x0833:
        r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x07dd, all -> 0x0CLASSNAME }
        if (r1 == 0) goto L_0x0855;
    L_0x0837:
        r1 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x07dd, all -> 0x0CLASSNAME }
        r1.<init>();	 Catch:{ Exception -> 0x07dd, all -> 0x0CLASSNAME }
        r2 = "drop frame startTime = ";
        r1.append(r2);	 Catch:{ Exception -> 0x07dd, all -> 0x0CLASSNAME }
        r1.append(r7);	 Catch:{ Exception -> 0x07dd, all -> 0x0CLASSNAME }
        r2 = " present time = ";
        r1.append(r2);	 Catch:{ Exception -> 0x07dd, all -> 0x0CLASSNAME }
        r7 = r6.presentationTimeUs;	 Catch:{ Exception -> 0x07dd, all -> 0x0CLASSNAME }
        r1.append(r7);	 Catch:{ Exception -> 0x07dd, all -> 0x0CLASSNAME }
        r1 = r1.toString();	 Catch:{ Exception -> 0x07dd, all -> 0x0CLASSNAME }
        org.telegram.messenger.FileLog.d(r1);	 Catch:{ Exception -> 0x07dd, all -> 0x0CLASSNAME }
    L_0x0855:
        r1 = 0;
        goto L_0x085d;
    L_0x0857:
        r1 = r6.presentationTimeUs;	 Catch:{ Exception -> 0x07dd, all -> 0x0CLASSNAME }
        r50 = r1;
    L_0x085b:
        r1 = r34;
    L_0x085d:
        r3.releaseOutputBuffer(r4, r1);	 Catch:{ Exception -> 0x0a00, all -> 0x0CLASSNAME }
        if (r1 == 0) goto L_0x096e;
    L_0x0862:
        r54.awaitNewImage();	 Catch:{ Exception -> 0x0867, all -> 0x0CLASSNAME }
        r1 = 0;
        goto L_0x086d;
    L_0x0867:
        r0 = move-exception;
        r1 = r0;
        org.telegram.messenger.FileLog.e(r1);	 Catch:{ Exception -> 0x0956, all -> 0x0CLASSNAME }
        r1 = 1;
    L_0x086d:
        if (r1 != 0) goto L_0x096e;
    L_0x086f:
        r1 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0956, all -> 0x0CLASSNAME }
        r7 = 18;
        if (r1 < r7) goto L_0x08b8;
    L_0x0875:
        r2 = r54;
        r4 = 0;
        r2.drawImage(r4);	 Catch:{ Exception -> 0x08aa, all -> 0x0CLASSNAME }
        r7 = r6.presentationTimeUs;	 Catch:{ Exception -> 0x08aa, all -> 0x0CLASSNAME }
        r34 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r7 = r7 * r34;
        r1 = r43;
        r1.setPresentationTime(r7);	 Catch:{ Exception -> 0x08a8, all -> 0x0CLASSNAME }
        r1.swapBuffers();	 Catch:{ Exception -> 0x08a8, all -> 0x0CLASSNAME }
        r16 = r2;
        r43 = r3;
        r20 = r5;
        r8 = r6;
        r30 = r22;
        r54 = r32;
        r22 = r59;
        r9 = r60;
        r26 = -5;
        r29 = 2;
        r41 = 0;
        r56 = 0;
        r58 = 3;
        r60 = 1;
        r59 = r1;
        goto L_0x098b;
    L_0x08a8:
        r0 = move-exception;
        goto L_0x08ad;
    L_0x08aa:
        r0 = move-exception;
        r1 = r43;
    L_0x08ad:
        r10 = r70;
        r4 = r1;
        r9 = r60;
        r56 = 0;
        r60 = 1;
        goto L_0x0b55;
    L_0x08b8:
        r1 = r43;
        r2 = r54;
        r4 = 0;
        r7 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r17 = r5.dequeueInputBuffer(r7);	 Catch:{ Exception -> 0x0943, all -> 0x0CLASSNAME }
        if (r17 < 0) goto L_0x091c;
    L_0x08c5:
        r4 = 1;
        r2.drawImage(r4);	 Catch:{ Exception -> 0x0943, all -> 0x0CLASSNAME }
        r16 = r2.getFrame();	 Catch:{ Exception -> 0x0943, all -> 0x0CLASSNAME }
        r35 = r42[r17];	 Catch:{ Exception -> 0x0943, all -> 0x0CLASSNAME }
        r35.clear();	 Catch:{ Exception -> 0x0943, all -> 0x0CLASSNAME }
        r30 = r22;
        r8 = -1;
        r26 = -5;
        r41 = 0;
        r22 = r1;
        r1 = r16;
        r16 = r2;
        r7 = r6;
        r54 = r32;
        r2 = r35;
        r43 = r3;
        r56 = 0;
        r3 = r28;
        r6 = r60;
        r60 = 1;
        r61 = r59;
        r59 = r22;
        r22 = r61;
        r4 = r67;
        r20 = r5;
        r8 = 18;
        r29 = 2;
        r58 = 3;
        r5 = r68;
        r9 = r6;
        r6 = r23;
        r8 = r7;
        r7 = r31;
        org.telegram.messenger.Utilities.convertVideoFrame(r1, r2, r3, r4, r5, r6, r7);	 Catch:{ Exception -> 0x099b, all -> 0x0CLASSNAME }
        r34 = 0;
        r1 = r8.presentationTimeUs;	 Catch:{ Exception -> 0x099b, all -> 0x0CLASSNAME }
        r38 = 0;
        r32 = r20;
        r33 = r17;
        r35 = r24;
        r36 = r1;
        r32.queueInputBuffer(r33, r34, r35, r36, r38);	 Catch:{ Exception -> 0x099b, all -> 0x0CLASSNAME }
        goto L_0x098b;
    L_0x091c:
        r16 = r2;
        r43 = r3;
        r20 = r5;
        r8 = r6;
        r30 = r22;
        r54 = r32;
        r22 = r59;
        r9 = r60;
        r26 = -5;
        r29 = 2;
        r41 = 0;
        r56 = 0;
        r58 = 3;
        r60 = 1;
        r59 = r1;
        r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x099b, all -> 0x0CLASSNAME }
        if (r1 == 0) goto L_0x098b;
    L_0x093d:
        r1 = "input buffer not available";
        org.telegram.messenger.FileLog.d(r1);	 Catch:{ Exception -> 0x099b, all -> 0x0CLASSNAME }
        goto L_0x098b;
    L_0x0943:
        r0 = move-exception;
        r59 = r1;
        r16 = r2;
        r43 = r3;
        r20 = r5;
        r9 = r60;
        r56 = 0;
        r60 = 1;
        r10 = r70;
        r1 = r0;
        goto L_0x096a;
    L_0x0956:
        r0 = move-exception;
        r20 = r5;
        r59 = r43;
        r16 = r54;
        r9 = r60;
        r56 = 0;
        r60 = 1;
        r43 = r3;
        r10 = r70;
        r1 = r0;
        r2 = r16;
    L_0x096a:
        r4 = r59;
        goto L_0x0b7e;
    L_0x096e:
        r20 = r5;
        r8 = r6;
        r30 = r22;
        r16 = r54;
        r22 = r59;
        r9 = r60;
        r26 = -5;
        r29 = 2;
        r41 = 0;
        r56 = 0;
        r58 = 3;
        r60 = 1;
        r54 = r32;
        r59 = r43;
        r43 = r3;
    L_0x098b:
        r1 = r8.flags;	 Catch:{ Exception -> 0x09d6, all -> 0x0CLASSNAME }
        r1 = r1 & 4;
        if (r1 == 0) goto L_0x09d0;
    L_0x0991:
        r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x09d6, all -> 0x0CLASSNAME }
        if (r1 == 0) goto L_0x09a6;
    L_0x0995:
        r1 = "decoder stream end";
        org.telegram.messenger.FileLog.d(r1);	 Catch:{ Exception -> 0x099b, all -> 0x0CLASSNAME }
        goto L_0x09a6;
    L_0x099b:
        r0 = move-exception;
        r10 = r70;
        r1 = r0;
        r2 = r16;
        r5 = r20;
        r3 = r43;
        goto L_0x096a;
    L_0x09a6:
        r1 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x09d6, all -> 0x0CLASSNAME }
        r2 = 18;
        if (r1 < r2) goto L_0x09b4;
    L_0x09ac:
        r20.signalEndOfInputStream();	 Catch:{ Exception -> 0x099b, all -> 0x0CLASSNAME }
        r1 = r20;
        r3 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        goto L_0x09cd;
    L_0x09b4:
        r1 = r20;
        r3 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r35 = r1.dequeueInputBuffer(r3);	 Catch:{ Exception -> 0x0a87, all -> 0x0CLASSNAME }
        if (r35 < 0) goto L_0x09cd;
    L_0x09be:
        r36 = 0;
        r37 = 1;
        r5 = r8.presentationTimeUs;	 Catch:{ Exception -> 0x0a87, all -> 0x0CLASSNAME }
        r40 = 4;
        r34 = r1;
        r38 = r5;
        r34.queueInputBuffer(r35, r36, r37, r38, r40);	 Catch:{ Exception -> 0x0a87, all -> 0x0CLASSNAME }
    L_0x09cd:
        r39 = 0;
        goto L_0x0a15;
    L_0x09d0:
        r1 = r20;
        r2 = 18;
        goto L_0x07b3;
    L_0x09d6:
        r0 = move-exception;
        r1 = r20;
        goto L_0x0a88;
    L_0x09db:
        r1 = r5;
        r59 = r43;
        r16 = r54;
        r9 = r60;
        r56 = 0;
        r60 = 1;
        r43 = r3;
        r2 = new java.lang.RuntimeException;	 Catch:{ Exception -> 0x0a87, all -> 0x0CLASSNAME }
        r3 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0a87, all -> 0x0CLASSNAME }
        r3.<init>();	 Catch:{ Exception -> 0x0a87, all -> 0x0CLASSNAME }
        r5 = "unexpected result from decoder.dequeueOutputBuffer: ";
        r3.append(r5);	 Catch:{ Exception -> 0x0a87, all -> 0x0CLASSNAME }
        r3.append(r4);	 Catch:{ Exception -> 0x0a87, all -> 0x0CLASSNAME }
        r3 = r3.toString();	 Catch:{ Exception -> 0x0a87, all -> 0x0CLASSNAME }
        r2.<init>(r3);	 Catch:{ Exception -> 0x0a87, all -> 0x0CLASSNAME }
        throw r2;	 Catch:{ Exception -> 0x0a87, all -> 0x0CLASSNAME }
    L_0x0a00:
        r0 = move-exception;
        goto L_0x0a93;
    L_0x0a03:
        r1 = r5;
        r8 = r6;
        r30 = r22;
        r16 = r54;
        r22 = r59;
        r9 = r60;
        r2 = 18;
        r26 = -5;
        r29 = 2;
        goto L_0x07a5;
    L_0x0a15:
        r56 = r1;
        r6 = r8;
        r60 = r9;
        r55 = r10;
        r20 = r11;
        r2 = r12;
        r54 = r16;
        r1 = r19;
        r9 = r39;
        r3 = r43;
        r13 = r46;
        r43 = r59;
        r7 = r71;
        r11 = r73;
        r59 = r22;
        r22 = r30;
        goto L_0x0559;
    L_0x0a35:
        r13 = r67;
        r1 = r5;
        r59 = r43;
        r16 = r54;
        r9 = r60;
        r56 = 0;
        r60 = 1;
        r43 = r3;
        r2 = new java.lang.RuntimeException;	 Catch:{ Exception -> 0x0a87, all -> 0x0CLASSNAME }
        r3 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0a87, all -> 0x0CLASSNAME }
        r3.<init>();	 Catch:{ Exception -> 0x0a87, all -> 0x0CLASSNAME }
        r5 = "encoderOutputBuffer ";
        r3.append(r5);	 Catch:{ Exception -> 0x0a87, all -> 0x0CLASSNAME }
        r3.append(r4);	 Catch:{ Exception -> 0x0a87, all -> 0x0CLASSNAME }
        r4 = " was null";
        r3.append(r4);	 Catch:{ Exception -> 0x0a87, all -> 0x0CLASSNAME }
        r3 = r3.toString();	 Catch:{ Exception -> 0x0a87, all -> 0x0CLASSNAME }
        r2.<init>(r3);	 Catch:{ Exception -> 0x0a87, all -> 0x0CLASSNAME }
        throw r2;	 Catch:{ Exception -> 0x0a87, all -> 0x0CLASSNAME }
    L_0x0a60:
        r13 = r67;
        r1 = r5;
        r59 = r43;
        r16 = r54;
        r9 = r60;
        r56 = 0;
        r60 = 1;
        r43 = r3;
        r2 = new java.lang.RuntimeException;	 Catch:{ Exception -> 0x0a87, all -> 0x0CLASSNAME }
        r3 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0a87, all -> 0x0CLASSNAME }
        r3.<init>();	 Catch:{ Exception -> 0x0a87, all -> 0x0CLASSNAME }
        r5 = "unexpected result from encoder.dequeueOutputBuffer: ";
        r3.append(r5);	 Catch:{ Exception -> 0x0a87, all -> 0x0CLASSNAME }
        r3.append(r4);	 Catch:{ Exception -> 0x0a87, all -> 0x0CLASSNAME }
        r3 = r3.toString();	 Catch:{ Exception -> 0x0a87, all -> 0x0CLASSNAME }
        r2.<init>(r3);	 Catch:{ Exception -> 0x0a87, all -> 0x0CLASSNAME }
        throw r2;	 Catch:{ Exception -> 0x0a87, all -> 0x0CLASSNAME }
    L_0x0a87:
        r0 = move-exception;
    L_0x0a88:
        r10 = r70;
        r5 = r1;
        r2 = r16;
        r3 = r43;
        goto L_0x0ace;
    L_0x0a90:
        r0 = move-exception;
        r13 = r67;
    L_0x0a93:
        r1 = r5;
        r59 = r43;
        r16 = r54;
        r9 = r60;
        r56 = 0;
        r60 = 1;
        r43 = r3;
        r10 = r70;
        goto L_0x0ab7;
    L_0x0aa3:
        r0 = move-exception;
        r13 = r67;
        r59 = r43;
        r16 = r54;
        r1 = r56;
        r9 = r60;
        r56 = 0;
        r60 = 1;
        r43 = r3;
        r10 = r70;
        r5 = r1;
    L_0x0ab7:
        r2 = r16;
        goto L_0x0ace;
    L_0x0aba:
        r0 = move-exception;
        r13 = r67;
        r16 = r2;
        r1 = r5;
        r57 = r10;
        r9 = r25;
        r59 = r43;
        r56 = 0;
        r60 = 1;
        r43 = r3;
        r10 = r70;
    L_0x0ace:
        r4 = r59;
        goto L_0x0b55;
    L_0x0ad2:
        r0 = move-exception;
        r16 = r2;
        r43 = r3;
        r59 = r4;
        r1 = r5;
        r41 = r9;
        r13 = r12;
        r9 = r25;
        goto L_0x0aed;
    L_0x0ae0:
        r0 = move-exception;
        r16 = r2;
        r43 = r3;
        r59 = r4;
        r1 = r5;
        r13 = r12;
        r9 = r25;
        r41 = 0;
    L_0x0aed:
        r56 = 0;
        r60 = 1;
        r10 = r70;
        r57 = r41;
        goto L_0x0b55;
    L_0x0af7:
        r0 = move-exception;
        r43 = r3;
        r59 = r4;
        r1 = r5;
        r13 = r12;
        r9 = r25;
        r41 = 0;
        r56 = 0;
        r60 = 1;
        r10 = r70;
        goto L_0x0b1a;
    L_0x0b09:
        r0 = move-exception;
        r43 = r3;
        r59 = r4;
        r1 = r5;
        r70 = r10;
        r13 = r12;
        r9 = r25;
        r41 = 0;
        r56 = 0;
        r60 = 1;
    L_0x0b1a:
        r2 = r41;
        r57 = r2;
        goto L_0x0b55;
    L_0x0b1f:
        r0 = move-exception;
        r59 = r4;
        r1 = r5;
        r70 = r10;
        r13 = r12;
        r9 = r25;
        r41 = 0;
        r56 = 0;
        r60 = 1;
        r2 = r41;
        r3 = r2;
        r57 = r3;
        goto L_0x0b55;
    L_0x0b34:
        r0 = move-exception;
        r1 = r5;
        r70 = r10;
        r13 = r12;
        r9 = r25;
        r41 = 0;
        r56 = 0;
        r60 = 1;
        r2 = r41;
        goto L_0x0b51;
    L_0x0b44:
        r0 = move-exception;
        r41 = r2;
        r1 = r5;
        r70 = r10;
        r13 = r12;
        r9 = r25;
        r56 = 0;
        r60 = 1;
    L_0x0b51:
        r3 = r2;
        r4 = r3;
        r57 = r4;
    L_0x0b55:
        r1 = r0;
        goto L_0x0b7e;
    L_0x0b57:
        r0 = move-exception;
        r70 = r10;
        goto L_0x0b69;
    L_0x0b5b:
        r0 = move-exception;
        r13 = r12;
        r9 = r25;
        r41 = 0;
        r56 = 0;
        r60 = 1;
        r1 = r0;
        r10 = r2;
        goto L_0x0b77;
    L_0x0b68:
        r0 = move-exception;
    L_0x0b69:
        r13 = r12;
        r9 = r25;
        goto L_0x0b70;
    L_0x0b6d:
        r0 = move-exception;
        r9 = r4;
        r13 = r12;
    L_0x0b70:
        r41 = 0;
        r56 = 0;
        r60 = 1;
        r1 = r0;
    L_0x0b77:
        r2 = r41;
        r3 = r2;
        r4 = r3;
        r5 = r4;
        r57 = r5;
    L_0x0b7e:
        r6 = r1 instanceof java.lang.IllegalStateException;	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0CLASSNAME }
        if (r6 == 0) goto L_0x0b86;
    L_0x0b82:
        if (r78 != 0) goto L_0x0b86;
    L_0x0b84:
        r56 = 1;
    L_0x0b86:
        r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0bf6, all -> 0x0CLASSNAME }
        r6.<init>();	 Catch:{ Exception -> 0x0bf6, all -> 0x0CLASSNAME }
        r7 = "bitrate: ";
        r6.append(r7);	 Catch:{ Exception -> 0x0bf6, all -> 0x0CLASSNAME }
        r6.append(r10);	 Catch:{ Exception -> 0x0bf6, all -> 0x0CLASSNAME }
        r7 = " framerate: ";
        r6.append(r7);	 Catch:{ Exception -> 0x0bf6, all -> 0x0CLASSNAME }
        r11 = r69;
        r6.append(r11);	 Catch:{ Exception -> 0x0bf0, all -> 0x0CLASSNAME }
        r7 = " size: ";
        r6.append(r7);	 Catch:{ Exception -> 0x0bf0, all -> 0x0CLASSNAME }
        r12 = r68;
        r6.append(r12);	 Catch:{ Exception -> 0x0bee, all -> 0x0CLASSNAME }
        r7 = "x";
        r6.append(r7);	 Catch:{ Exception -> 0x0bee, all -> 0x0CLASSNAME }
        r6.append(r13);	 Catch:{ Exception -> 0x0bee, all -> 0x0CLASSNAME }
        r6 = r6.toString();	 Catch:{ Exception -> 0x0bee, all -> 0x0CLASSNAME }
        org.telegram.messenger.FileLog.e(r6);	 Catch:{ Exception -> 0x0bee, all -> 0x0CLASSNAME }
        org.telegram.messenger.FileLog.e(r1);	 Catch:{ Exception -> 0x0bee, all -> 0x0CLASSNAME }
        r17 = r56;
        r1 = 1;
    L_0x0bbd:
        r6 = r14.extractor;	 Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }
        r6.unselectTrack(r9);	 Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }
        if (r2 == 0) goto L_0x0bc7;
    L_0x0bc4:
        r2.release();	 Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }
    L_0x0bc7:
        if (r4 == 0) goto L_0x0bcc;
    L_0x0bc9:
        r4.release();	 Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }
    L_0x0bcc:
        if (r3 == 0) goto L_0x0bd4;
    L_0x0bce:
        r3.stop();	 Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }
        r3.release();	 Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }
    L_0x0bd4:
        if (r5 == 0) goto L_0x0bdc;
    L_0x0bd6:
        r5.stop();	 Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }
        r5.release();	 Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }
    L_0x0bdc:
        if (r57 == 0) goto L_0x0be1;
    L_0x0bde:
        r57.release();	 Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }
    L_0x0be1:
        r62.checkConversionCanceled();	 Catch:{ Exception -> 0x0be7, all -> 0x0CLASSNAME }
        r3 = r17;
        goto L_0x0c0e;
    L_0x0be7:
        r0 = move-exception;
        r1 = r0;
        r13 = r11;
        r3 = r17;
        goto L_0x0CLASSNAME;
    L_0x0bee:
        r0 = move-exception;
        goto L_0x0bf3;
    L_0x0bf0:
        r0 = move-exception;
        r12 = r68;
    L_0x0bf3:
        r1 = r0;
        r13 = r11;
        goto L_0x0bfc;
    L_0x0bf6:
        r0 = move-exception;
        r12 = r68;
        r13 = r69;
        r1 = r0;
    L_0x0bfc:
        r3 = r56;
        goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r0 = move-exception;
        r12 = r68;
        r13 = r69;
        goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r13 = r12;
        r56 = 0;
        r12 = r11;
        r11 = r9;
        r1 = 0;
        r3 = 0;
    L_0x0c0e:
        r4 = r1;
        r13 = r11;
        goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r0 = move-exception;
        r13 = r12;
        r56 = 0;
        r60 = 1;
        r12 = r11;
        r1 = r0;
        r13 = r9;
        goto L_0x0CLASSNAME;
    L_0x0c1c:
        r8 = r2;
        r13 = r12;
        r56 = 0;
        r60 = 1;
        r12 = r11;
        r11 = r9;
        r2 = r14.extractor;	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0CLASSNAME }
        r3 = r14.mediaMuxer;	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0CLASSNAME }
        r1 = -1;
        if (r10 == r1) goto L_0x0c2d;
    L_0x0c2b:
        r15 = 1;
        goto L_0x0c2e;
    L_0x0c2d:
        r15 = 0;
    L_0x0c2e:
        r1 = r62;
        r4 = r8;
        r5 = r71;
        r7 = r73;
        r12 = r11;
        r9 = r75;
        r13 = r68;
        r11 = r64;
        r13 = r12;
        r12 = r15;
        r1.readAndWriteTracks(r2, r3, r4, r5, r7, r9, r11, r12);	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0CLASSNAME }
        r10 = r70;
        r3 = 0;
        r4 = 0;
    L_0x0CLASSNAME:
        r1 = r14.extractor;
        if (r1 == 0) goto L_0x0c4c;
    L_0x0CLASSNAME:
        r1.release();
    L_0x0c4c:
        r1 = r14.mediaMuxer;
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r1.finishMovie();	 Catch:{ Exception -> 0x0CLASSNAME }
        goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r0 = move-exception;
        r1 = r0;
        org.telegram.messenger.FileLog.e(r1);
    L_0x0CLASSNAME:
        r6 = r67;
        r7 = r68;
        r60 = r4;
        goto L_0x0cbc;
    L_0x0CLASSNAME:
        r0 = move-exception;
        goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r0 = move-exception;
        r13 = r11;
        goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r0 = move-exception;
        r2 = r0;
        r1 = r14;
        goto L_0x0ce3;
    L_0x0c6a:
        r0 = move-exception;
        r13 = r9;
        r56 = 0;
        r60 = 1;
    L_0x0CLASSNAME:
        r10 = r70;
    L_0x0CLASSNAME:
        r1 = r0;
    L_0x0CLASSNAME:
        r3 = 0;
    L_0x0CLASSNAME:
        r2 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0cdf }
        r2.<init>();	 Catch:{ all -> 0x0cdf }
        r4 = "bitrate: ";
        r2.append(r4);	 Catch:{ all -> 0x0cdf }
        r2.append(r10);	 Catch:{ all -> 0x0cdf }
        r4 = " framerate: ";
        r2.append(r4);	 Catch:{ all -> 0x0cdf }
        r2.append(r13);	 Catch:{ all -> 0x0cdf }
        r4 = " size: ";
        r2.append(r4);	 Catch:{ all -> 0x0cdf }
        r7 = r68;
        r2.append(r7);	 Catch:{ all -> 0x0cdf }
        r4 = "x";
        r2.append(r4);	 Catch:{ all -> 0x0cdf }
        r6 = r67;
        r2.append(r6);	 Catch:{ all -> 0x0cdf }
        r2 = r2.toString();	 Catch:{ all -> 0x0cdf }
        org.telegram.messenger.FileLog.e(r2);	 Catch:{ all -> 0x0cdf }
        org.telegram.messenger.FileLog.e(r1);	 Catch:{ all -> 0x0cdf }
        r1 = r14.extractor;
        if (r1 == 0) goto L_0x0caf;
    L_0x0cac:
        r1.release();
    L_0x0caf:
        r1 = r14.mediaMuxer;
        if (r1 == 0) goto L_0x0cbc;
    L_0x0cb3:
        r1.finishMovie();	 Catch:{ Exception -> 0x0cb7 }
        goto L_0x0cbc;
    L_0x0cb7:
        r0 = move-exception;
        r1 = r0;
        org.telegram.messenger.FileLog.e(r1);
    L_0x0cbc:
        r9 = r10;
        if (r3 == 0) goto L_0x0cde;
    L_0x0cbf:
        r17 = 1;
        r1 = r62;
        r2 = r63;
        r3 = r64;
        r4 = r65;
        r5 = r66;
        r6 = r67;
        r7 = r68;
        r8 = r69;
        r10 = r71;
        r12 = r73;
        r14 = r75;
        r16 = r77;
        r1 = r1.convertVideoInternal(r2, r3, r4, r5, r6, r7, r8, r9, r10, r12, r14, r16, r17);
        return r1;
    L_0x0cde:
        return r60;
    L_0x0cdf:
        r0 = move-exception;
        r1 = r62;
        r2 = r0;
    L_0x0ce3:
        r3 = r1.extractor;
        if (r3 == 0) goto L_0x0cea;
    L_0x0ce7:
        r3.release();
    L_0x0cea:
        r3 = r1.mediaMuxer;
        if (r3 == 0) goto L_0x0cf7;
    L_0x0cee:
        r3.finishMovie();	 Catch:{ Exception -> 0x0cf2 }
        goto L_0x0cf7;
    L_0x0cf2:
        r0 = move-exception;
        r3 = r0;
        org.telegram.messenger.FileLog.e(r3);
    L_0x0cf7:
        goto L_0x0cf9;
    L_0x0cf8:
        throw r2;
    L_0x0cf9:
        goto L_0x0cf8;
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
