package org.telegram.messenger.exoplayer.chunk;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Display.Mode;
import android.view.WindowManager;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.exoplayer.MediaCodecUtil;
import org.telegram.messenger.exoplayer.MediaCodecUtil.DecoderQueryException;
import org.telegram.messenger.exoplayer.util.MimeTypes;
import org.telegram.messenger.exoplayer.util.Util;
import org.telegram.tgnet.ConnectionsManager;

public final class VideoFormatSelectorUtil {
    private static final float FRACTION_TO_CONSIDER_FULLSCREEN = 0.98f;
    private static final String TAG = "VideoFormatSelectorUtil";

    public static int[] selectVideoFormatsForDefaultDisplay(Context context, List<? extends FormatWrapper> formatWrappers, String[] allowedContainerMimeTypes, boolean filterHdFormats) throws DecoderQueryException {
        Point viewportSize = getDisplaySize(context);
        return selectVideoFormats(formatWrappers, allowedContainerMimeTypes, filterHdFormats, true, false, viewportSize.x, viewportSize.y);
    }

    public static int[] selectVideoFormats(List<? extends FormatWrapper> formatWrappers, String[] allowedContainerMimeTypes, boolean filterHdFormats, boolean orientationMayChange, boolean secureDecoder, int viewportWidth, int viewportHeight) throws DecoderQueryException {
        int i;
        int maxVideoPixelsToRetain = ConnectionsManager.DEFAULT_DATACENTER_ID;
        ArrayList<Integer> selectedIndexList = new ArrayList();
        int formatWrapperCount = formatWrappers.size();
        for (i = 0; i < formatWrapperCount; i++) {
            Format format = ((FormatWrapper) formatWrappers.get(i)).getFormat();
            if (isFormatPlayable(format, allowedContainerMimeTypes, filterHdFormats, secureDecoder)) {
                selectedIndexList.add(Integer.valueOf(i));
                if (format.width > 0 && format.height > 0 && viewportWidth > 0 && viewportHeight > 0) {
                    Point maxVideoSizeInViewport = getMaxVideoSizeInViewport(orientationMayChange, viewportWidth, viewportHeight, format.width, format.height);
                    int videoPixels = format.width * format.height;
                    if (format.width >= ((int) (((float) maxVideoSizeInViewport.x) * FRACTION_TO_CONSIDER_FULLSCREEN)) && format.height >= ((int) (((float) maxVideoSizeInViewport.y) * FRACTION_TO_CONSIDER_FULLSCREEN)) && videoPixels < maxVideoPixelsToRetain) {
                        maxVideoPixelsToRetain = videoPixels;
                    }
                }
            }
        }
        if (maxVideoPixelsToRetain != ConnectionsManager.DEFAULT_DATACENTER_ID) {
            for (i = selectedIndexList.size() - 1; i >= 0; i--) {
                format = ((FormatWrapper) formatWrappers.get(((Integer) selectedIndexList.get(i)).intValue())).getFormat();
                if (format.width > 0 && format.height > 0 && format.width * format.height > maxVideoPixelsToRetain) {
                    selectedIndexList.remove(i);
                }
            }
        }
        return Util.toArray(selectedIndexList);
    }

    private static boolean isFormatPlayable(Format format, String[] allowedContainerMimeTypes, boolean filterHdFormats, boolean secureDecoder) throws DecoderQueryException {
        if (allowedContainerMimeTypes != null && !Util.contains(allowedContainerMimeTypes, format.mimeType)) {
            return false;
        }
        if (filterHdFormats && (format.width >= 1280 || format.height >= 720)) {
            return false;
        }
        if (format.width > 0 && format.height > 0) {
            if (Util.SDK_INT >= 21) {
                String videoMediaMimeType = MimeTypes.getVideoMediaMimeType(format.codecs);
                if (MimeTypes.VIDEO_UNKNOWN.equals(videoMediaMimeType)) {
                    videoMediaMimeType = "video/avc";
                }
                if (format.frameRate <= 0.0f) {
                    return MediaCodecUtil.isSizeSupportedV21(videoMediaMimeType, secureDecoder, format.width, format.height);
                }
                return MediaCodecUtil.isSizeAndRateSupportedV21(videoMediaMimeType, secureDecoder, format.width, format.height, (double) format.frameRate);
            } else if (format.width * format.height > MediaCodecUtil.maxH264DecodableFrameSize()) {
                return false;
            }
        }
        return true;
    }

    private static Point getMaxVideoSizeInViewport(boolean orientationMayChange, int viewportWidth, int viewportHeight, int videoWidth, int videoHeight) {
        Object obj = 1;
        if (orientationMayChange) {
            Object obj2 = videoWidth > videoHeight ? 1 : null;
            if (viewportWidth <= viewportHeight) {
                obj = null;
            }
            if (obj2 != obj) {
                int tempViewportWidth = viewportWidth;
                viewportWidth = viewportHeight;
                viewportHeight = tempViewportWidth;
            }
        }
        if (videoWidth * viewportHeight >= videoHeight * viewportWidth) {
            return new Point(viewportWidth, Util.ceilDivide(viewportWidth * videoHeight, videoWidth));
        }
        return new Point(Util.ceilDivide(viewportHeight * videoWidth, videoHeight), viewportHeight);
    }

    private static Point getDisplaySize(Context context) {
        if (Util.SDK_INT < 25) {
            if ("Sony".equals(Util.MANUFACTURER) && Util.MODEL != null && Util.MODEL.startsWith("BRAVIA") && context.getPackageManager().hasSystemFeature("com.sony.dtv.hardware.panel.qfhd")) {
                return new Point(3840, 2160);
            }
            if ("NVIDIA".equals(Util.MANUFACTURER) && Util.MODEL != null && Util.MODEL.contains("SHIELD")) {
                String sysDisplaySize = null;
                try {
                    Class<?> systemProperties = Class.forName("android.os.SystemProperties");
                    sysDisplaySize = (String) systemProperties.getMethod("get", new Class[]{String.class}).invoke(systemProperties, new Object[]{"sys.display-size"});
                } catch (Exception e) {
                    Log.e(TAG, "Failed to read sys.display-size", e);
                }
                if (!TextUtils.isEmpty(sysDisplaySize)) {
                    try {
                        String[] sysDisplaySizeParts = sysDisplaySize.trim().split("x");
                        if (sysDisplaySizeParts.length == 2) {
                            int width = Integer.parseInt(sysDisplaySizeParts[0]);
                            int height = Integer.parseInt(sysDisplaySizeParts[1]);
                            if (width > 0 && height > 0) {
                                return new Point(width, height);
                            }
                        }
                    } catch (NumberFormatException e2) {
                    }
                    Log.e(TAG, "Invalid sys.display-size: " + sysDisplaySize);
                }
            }
        }
        Display display = ((WindowManager) context.getSystemService("window")).getDefaultDisplay();
        Point displaySize = new Point();
        if (Util.SDK_INT >= 23) {
            getDisplaySizeV23(display, displaySize);
            return displaySize;
        } else if (Util.SDK_INT >= 17) {
            getDisplaySizeV17(display, displaySize);
            return displaySize;
        } else if (Util.SDK_INT >= 16) {
            getDisplaySizeV16(display, displaySize);
            return displaySize;
        } else {
            getDisplaySizeV9(display, displaySize);
            return displaySize;
        }
    }

    @TargetApi(23)
    private static void getDisplaySizeV23(Display display, Point outSize) {
        Mode mode = display.getMode();
        outSize.x = mode.getPhysicalWidth();
        outSize.y = mode.getPhysicalHeight();
    }

    @TargetApi(17)
    private static void getDisplaySizeV17(Display display, Point outSize) {
        display.getRealSize(outSize);
    }

    @TargetApi(16)
    private static void getDisplaySizeV16(Display display, Point outSize) {
        display.getSize(outSize);
    }

    private static void getDisplaySizeV9(Display display, Point outSize) {
        outSize.x = display.getWidth();
        outSize.y = display.getHeight();
    }

    private VideoFormatSelectorUtil() {
    }
}
