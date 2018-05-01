package org.telegram.messenger.exoplayer2.text.dvb;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Region.Op;
import android.util.Log;
import android.util.SparseArray;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.telegram.messenger.exoplayer2.text.Cue;
import org.telegram.messenger.exoplayer2.util.ParsableBitArray;
import org.telegram.messenger.exoplayer2.util.Util;
import org.telegram.ui.ActionBar.Theme;

final class DvbParser {
    private static final int DATA_TYPE_24_TABLE_DATA = 32;
    private static final int DATA_TYPE_28_TABLE_DATA = 33;
    private static final int DATA_TYPE_2BP_CODE_STRING = 16;
    private static final int DATA_TYPE_48_TABLE_DATA = 34;
    private static final int DATA_TYPE_4BP_CODE_STRING = 17;
    private static final int DATA_TYPE_8BP_CODE_STRING = 18;
    private static final int DATA_TYPE_END_LINE = 240;
    private static final int OBJECT_CODING_PIXELS = 0;
    private static final int OBJECT_CODING_STRING = 1;
    private static final int PAGE_STATE_NORMAL = 0;
    private static final int REGION_DEPTH_4_BIT = 2;
    private static final int REGION_DEPTH_8_BIT = 3;
    private static final int SEGMENT_TYPE_CLUT_DEFINITION = 18;
    private static final int SEGMENT_TYPE_DISPLAY_DEFINITION = 20;
    private static final int SEGMENT_TYPE_OBJECT_DATA = 19;
    private static final int SEGMENT_TYPE_PAGE_COMPOSITION = 16;
    private static final int SEGMENT_TYPE_REGION_COMPOSITION = 17;
    private static final String TAG = "DvbParser";
    private static final byte[] defaultMap2To4 = new byte[]{(byte) 0, (byte) 7, (byte) 8, (byte) 15};
    private static final byte[] defaultMap2To8 = new byte[]{(byte) 0, (byte) 119, (byte) -120, (byte) -1};
    private static final byte[] defaultMap4To8 = new byte[]{(byte) 0, (byte) 17, (byte) 34, (byte) 51, (byte) 68, (byte) 85, (byte) 102, (byte) 119, (byte) -120, (byte) -103, (byte) -86, (byte) -69, (byte) -52, (byte) -35, (byte) -18, (byte) -1};
    private Bitmap bitmap;
    private final Canvas canvas;
    private final ClutDefinition defaultClutDefinition;
    private final DisplayDefinition defaultDisplayDefinition;
    private final Paint defaultPaint = new Paint();
    private final Paint fillRegionPaint;
    private final SubtitleService subtitleService;

    private static final class ClutDefinition {
        public final int[] clutEntries2Bit;
        public final int[] clutEntries4Bit;
        public final int[] clutEntries8Bit;
        public final int id;

        public ClutDefinition(int i, int[] iArr, int[] iArr2, int[] iArr3) {
            this.id = i;
            this.clutEntries2Bit = iArr;
            this.clutEntries4Bit = iArr2;
            this.clutEntries8Bit = iArr3;
        }
    }

    private static final class DisplayDefinition {
        public final int height;
        public final int horizontalPositionMaximum;
        public final int horizontalPositionMinimum;
        public final int verticalPositionMaximum;
        public final int verticalPositionMinimum;
        public final int width;

        public DisplayDefinition(int i, int i2, int i3, int i4, int i5, int i6) {
            this.width = i;
            this.height = i2;
            this.horizontalPositionMinimum = i3;
            this.horizontalPositionMaximum = i4;
            this.verticalPositionMinimum = i5;
            this.verticalPositionMaximum = i6;
        }
    }

    private static final class ObjectData {
        public final byte[] bottomFieldData;
        public final int id;
        public final boolean nonModifyingColorFlag;
        public final byte[] topFieldData;

        public ObjectData(int i, boolean z, byte[] bArr, byte[] bArr2) {
            this.id = i;
            this.nonModifyingColorFlag = z;
            this.topFieldData = bArr;
            this.bottomFieldData = bArr2;
        }
    }

    private static final class PageComposition {
        public final SparseArray<PageRegion> regions;
        public final int state;
        public final int timeOutSecs;
        public final int version;

        public PageComposition(int i, int i2, int i3, SparseArray<PageRegion> sparseArray) {
            this.timeOutSecs = i;
            this.version = i2;
            this.state = i3;
            this.regions = sparseArray;
        }
    }

    private static final class PageRegion {
        public final int horizontalAddress;
        public final int verticalAddress;

        public PageRegion(int i, int i2) {
            this.horizontalAddress = i;
            this.verticalAddress = i2;
        }
    }

    private static final class RegionComposition {
        public final int clutId;
        public final int depth;
        public final boolean fillFlag;
        public final int height;
        public final int id;
        public final int levelOfCompatibility;
        public final int pixelCode2Bit;
        public final int pixelCode4Bit;
        public final int pixelCode8Bit;
        public final SparseArray<RegionObject> regionObjects;
        public final int width;

        public RegionComposition(int i, boolean z, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, SparseArray<RegionObject> sparseArray) {
            this.id = i;
            this.fillFlag = z;
            this.width = i2;
            this.height = i3;
            this.levelOfCompatibility = i4;
            this.depth = i5;
            this.clutId = i6;
            this.pixelCode8Bit = i7;
            this.pixelCode4Bit = i8;
            this.pixelCode2Bit = i9;
            this.regionObjects = sparseArray;
        }

        public void mergeFrom(RegionComposition regionComposition) {
            if (regionComposition != null) {
                regionComposition = regionComposition.regionObjects;
                for (int i = 0; i < regionComposition.size(); i++) {
                    this.regionObjects.put(regionComposition.keyAt(i), regionComposition.valueAt(i));
                }
            }
        }
    }

    private static final class RegionObject {
        public final int backgroundPixelCode;
        public final int foregroundPixelCode;
        public final int horizontalPosition;
        public final int provider;
        public final int type;
        public final int verticalPosition;

        public RegionObject(int i, int i2, int i3, int i4, int i5, int i6) {
            this.type = i;
            this.provider = i2;
            this.horizontalPosition = i3;
            this.verticalPosition = i4;
            this.foregroundPixelCode = i5;
            this.backgroundPixelCode = i6;
        }
    }

    private static final class SubtitleService {
        public final SparseArray<ClutDefinition> ancillaryCluts = new SparseArray();
        public final SparseArray<ObjectData> ancillaryObjects = new SparseArray();
        public final int ancillaryPageId;
        public final SparseArray<ClutDefinition> cluts = new SparseArray();
        public DisplayDefinition displayDefinition;
        public final SparseArray<ObjectData> objects = new SparseArray();
        public PageComposition pageComposition;
        public final SparseArray<RegionComposition> regions = new SparseArray();
        public final int subtitlePageId;

        public SubtitleService(int i, int i2) {
            this.subtitlePageId = i;
            this.ancillaryPageId = i2;
        }

        public void reset() {
            this.regions.clear();
            this.cluts.clear();
            this.objects.clear();
            this.ancillaryCluts.clear();
            this.ancillaryObjects.clear();
            this.displayDefinition = null;
            this.pageComposition = null;
        }
    }

    private static int getColor(int i, int i2, int i3, int i4) {
        return (((i << 24) | (i2 << 16)) | (i3 << 8)) | i4;
    }

    public DvbParser(int i, int i2) {
        this.defaultPaint.setStyle(Style.FILL_AND_STROKE);
        this.defaultPaint.setXfermode(new PorterDuffXfermode(Mode.SRC));
        this.defaultPaint.setPathEffect(null);
        this.fillRegionPaint = new Paint();
        this.fillRegionPaint.setStyle(Style.FILL);
        this.fillRegionPaint.setXfermode(new PorterDuffXfermode(Mode.DST_OVER));
        this.fillRegionPaint.setPathEffect(null);
        this.canvas = new Canvas();
        this.defaultDisplayDefinition = new DisplayDefinition(719, 575, 0, 719, 0, 575);
        this.defaultClutDefinition = new ClutDefinition(0, generateDefault2BitClutEntries(), generateDefault4BitClutEntries(), generateDefault8BitClutEntries());
        this.subtitleService = new SubtitleService(i, i2);
    }

    public void reset() {
        this.subtitleService.reset();
    }

    public List<Cue> decode(byte[] bArr, int i) {
        DvbParser dvbParser = this;
        ParsableBitArray parsableBitArray = new ParsableBitArray(bArr, i);
        while (parsableBitArray.bitsLeft() >= 48 && parsableBitArray.readBits(8) == 15) {
            parseSubtitlingSegment(parsableBitArray, dvbParser.subtitleService);
        }
        if (dvbParser.subtitleService.pageComposition == null) {
            return Collections.emptyList();
        }
        DisplayDefinition displayDefinition = dvbParser.subtitleService.displayDefinition != null ? dvbParser.subtitleService.displayDefinition : dvbParser.defaultDisplayDefinition;
        if (!(dvbParser.bitmap != null && displayDefinition.width + 1 == dvbParser.bitmap.getWidth() && displayDefinition.height + 1 == dvbParser.bitmap.getHeight())) {
            dvbParser.bitmap = Bitmap.createBitmap(displayDefinition.width + 1, displayDefinition.height + 1, Config.ARGB_8888);
            dvbParser.canvas.setBitmap(dvbParser.bitmap);
        }
        List<Cue> arrayList = new ArrayList();
        SparseArray sparseArray = dvbParser.subtitleService.pageComposition.regions;
        for (int i2 = 0; i2 < sparseArray.size(); i2++) {
            PageRegion pageRegion = (PageRegion) sparseArray.valueAt(i2);
            RegionComposition regionComposition = (RegionComposition) dvbParser.subtitleService.regions.get(sparseArray.keyAt(i2));
            int i3 = pageRegion.horizontalAddress + displayDefinition.horizontalPositionMinimum;
            int i4 = pageRegion.verticalAddress + displayDefinition.verticalPositionMinimum;
            float f = (float) i3;
            float f2 = (float) i4;
            float f3 = f2;
            float f4 = f;
            dvbParser.canvas.clipRect(f, f2, (float) Math.min(regionComposition.width + i3, displayDefinition.horizontalPositionMaximum), (float) Math.min(regionComposition.height + i4, displayDefinition.verticalPositionMaximum), Op.REPLACE);
            ClutDefinition clutDefinition = (ClutDefinition) dvbParser.subtitleService.cluts.get(regionComposition.clutId);
            if (clutDefinition == null) {
                clutDefinition = (ClutDefinition) dvbParser.subtitleService.ancillaryCluts.get(regionComposition.clutId);
                if (clutDefinition == null) {
                    clutDefinition = dvbParser.defaultClutDefinition;
                }
            }
            SparseArray sparseArray2 = regionComposition.regionObjects;
            int i5 = 0;
            while (i5 < sparseArray2.size()) {
                int i6;
                SparseArray sparseArray3;
                int keyAt = sparseArray2.keyAt(i5);
                RegionObject regionObject = (RegionObject) sparseArray2.valueAt(i5);
                ObjectData objectData = (ObjectData) dvbParser.subtitleService.objects.get(keyAt);
                ObjectData objectData2 = objectData == null ? (ObjectData) dvbParser.subtitleService.ancillaryObjects.get(keyAt) : objectData;
                if (objectData2 != null) {
                    i6 = i5;
                    sparseArray3 = sparseArray2;
                    paintPixelDataSubBlocks(objectData2, clutDefinition, regionComposition.depth, regionObject.horizontalPosition + i3, i4 + regionObject.verticalPosition, objectData2.nonModifyingColorFlag ? null : dvbParser.defaultPaint, dvbParser.canvas);
                } else {
                    i6 = i5;
                    sparseArray3 = sparseArray2;
                }
                i5 = i6 + 1;
                sparseArray2 = sparseArray3;
            }
            if (regionComposition.fillFlag) {
                int i7;
                if (regionComposition.depth == 3) {
                    i7 = clutDefinition.clutEntries8Bit[regionComposition.pixelCode8Bit];
                } else if (regionComposition.depth == 2) {
                    i7 = clutDefinition.clutEntries4Bit[regionComposition.pixelCode4Bit];
                } else {
                    i7 = clutDefinition.clutEntries2Bit[regionComposition.pixelCode2Bit];
                }
                dvbParser.fillRegionPaint.setColor(i7);
                dvbParser.canvas.drawRect(f4, f3, (float) (regionComposition.width + i3), (float) (regionComposition.height + i4), dvbParser.fillRegionPaint);
            }
            arrayList.add(new Cue(Bitmap.createBitmap(dvbParser.bitmap, i3, i4, regionComposition.width, regionComposition.height), f4 / ((float) displayDefinition.width), 0, f3 / ((float) displayDefinition.height), 0, ((float) regionComposition.width) / ((float) displayDefinition.width), ((float) regionComposition.height) / ((float) displayDefinition.height)));
            dvbParser.canvas.drawColor(0, Mode.CLEAR);
        }
        return arrayList;
    }

    private static void parseSubtitlingSegment(ParsableBitArray parsableBitArray, SubtitleService subtitleService) {
        int readBits = parsableBitArray.readBits(8);
        int readBits2 = parsableBitArray.readBits(16);
        int readBits3 = parsableBitArray.readBits(16);
        int bytePosition = parsableBitArray.getBytePosition() + readBits3;
        if (readBits3 * 8 > parsableBitArray.bitsLeft()) {
            Log.w(TAG, "Data field length exceeds limit");
            parsableBitArray.skipBits(parsableBitArray.bitsLeft());
            return;
        }
        PageComposition pageComposition;
        switch (readBits) {
            case 16:
                if (readBits2 == subtitleService.subtitlePageId) {
                    pageComposition = subtitleService.pageComposition;
                    PageComposition parsePageComposition = parsePageComposition(parsableBitArray, readBits3);
                    if (parsePageComposition.state == 0) {
                        if (!(pageComposition == null || pageComposition.version == parsePageComposition.version)) {
                            subtitleService.pageComposition = parsePageComposition;
                            break;
                        }
                    }
                    subtitleService.pageComposition = parsePageComposition;
                    subtitleService.regions.clear();
                    subtitleService.cluts.clear();
                    subtitleService.objects.clear();
                    break;
                }
                break;
            case 17:
                pageComposition = subtitleService.pageComposition;
                if (readBits2 == subtitleService.subtitlePageId && pageComposition != null) {
                    RegionComposition parseRegionComposition = parseRegionComposition(parsableBitArray, readBits3);
                    if (pageComposition.state == 0) {
                        parseRegionComposition.mergeFrom((RegionComposition) subtitleService.regions.get(parseRegionComposition.id));
                    }
                    subtitleService.regions.put(parseRegionComposition.id, parseRegionComposition);
                    break;
                }
            case 18:
                ClutDefinition parseClutDefinition;
                if (readBits2 != subtitleService.subtitlePageId) {
                    if (readBits2 == subtitleService.ancillaryPageId) {
                        parseClutDefinition = parseClutDefinition(parsableBitArray, readBits3);
                        subtitleService.ancillaryCluts.put(parseClutDefinition.id, parseClutDefinition);
                        break;
                    }
                }
                parseClutDefinition = parseClutDefinition(parsableBitArray, readBits3);
                subtitleService.cluts.put(parseClutDefinition.id, parseClutDefinition);
                break;
                break;
            case 19:
                ObjectData parseObjectData;
                if (readBits2 != subtitleService.subtitlePageId) {
                    if (readBits2 == subtitleService.ancillaryPageId) {
                        parseObjectData = parseObjectData(parsableBitArray);
                        subtitleService.ancillaryObjects.put(parseObjectData.id, parseObjectData);
                        break;
                    }
                }
                parseObjectData = parseObjectData(parsableBitArray);
                subtitleService.objects.put(parseObjectData.id, parseObjectData);
                break;
                break;
            case 20:
                if (readBits2 == subtitleService.subtitlePageId) {
                    subtitleService.displayDefinition = parseDisplayDefinition(parsableBitArray);
                    break;
                }
                break;
            default:
                break;
        }
        parsableBitArray.skipBytes(bytePosition - parsableBitArray.getBytePosition());
    }

    private static DisplayDefinition parseDisplayDefinition(ParsableBitArray parsableBitArray) {
        int readBits;
        int readBits2;
        int i;
        int i2;
        parsableBitArray.skipBits(4);
        boolean readBit = parsableBitArray.readBit();
        parsableBitArray.skipBits(3);
        int readBits3 = parsableBitArray.readBits(16);
        int readBits4 = parsableBitArray.readBits(16);
        if (readBit) {
            int readBits5 = parsableBitArray.readBits(16);
            int readBits6 = parsableBitArray.readBits(16);
            readBits = parsableBitArray.readBits(16);
            readBits2 = parsableBitArray.readBits(16);
            i = readBits6;
            i2 = readBits;
            readBits = readBits5;
        } else {
            readBits = 0;
            i2 = readBits;
            i = readBits3;
            readBits2 = readBits4;
        }
        return new DisplayDefinition(readBits3, readBits4, readBits, i, i2, readBits2);
    }

    private static PageComposition parsePageComposition(ParsableBitArray parsableBitArray, int i) {
        int readBits = parsableBitArray.readBits(8);
        int readBits2 = parsableBitArray.readBits(4);
        int readBits3 = parsableBitArray.readBits(2);
        parsableBitArray.skipBits(2);
        i -= 2;
        SparseArray sparseArray = new SparseArray();
        while (i > 0) {
            int readBits4 = parsableBitArray.readBits(8);
            parsableBitArray.skipBits(8);
            i -= 6;
            sparseArray.put(readBits4, new PageRegion(parsableBitArray.readBits(16), parsableBitArray.readBits(16)));
        }
        return new PageComposition(readBits, readBits2, readBits3, sparseArray);
    }

    private static RegionComposition parseRegionComposition(ParsableBitArray parsableBitArray, int i) {
        ParsableBitArray parsableBitArray2 = parsableBitArray;
        int readBits = parsableBitArray2.readBits(8);
        parsableBitArray2.skipBits(4);
        boolean readBit = parsableBitArray.readBit();
        parsableBitArray2.skipBits(3);
        int i2 = 16;
        int readBits2 = parsableBitArray2.readBits(16);
        int readBits3 = parsableBitArray2.readBits(16);
        int readBits4 = parsableBitArray2.readBits(3);
        int readBits5 = parsableBitArray2.readBits(3);
        int i3 = 2;
        parsableBitArray2.skipBits(2);
        int readBits6 = parsableBitArray2.readBits(8);
        int readBits7 = parsableBitArray2.readBits(8);
        int readBits8 = parsableBitArray2.readBits(4);
        int readBits9 = parsableBitArray2.readBits(2);
        parsableBitArray2.skipBits(2);
        int i4 = i - 10;
        SparseArray sparseArray = new SparseArray();
        while (i4 > 0) {
            int i5;
            int i6;
            int readBits10 = parsableBitArray2.readBits(i2);
            i2 = parsableBitArray2.readBits(i3);
            int readBits11 = parsableBitArray2.readBits(i3);
            int readBits12 = parsableBitArray2.readBits(12);
            int i7 = readBits9;
            parsableBitArray2.skipBits(4);
            int readBits13 = parsableBitArray2.readBits(12);
            i4 -= 6;
            if (i2 != 1) {
                if (i2 != 2) {
                    i5 = 0;
                    i6 = i5;
                    sparseArray.put(readBits10, new RegionObject(i2, readBits11, readBits12, readBits13, i5, i6));
                    readBits10 = 4;
                    readBits9 = i7;
                    i3 = 2;
                    i2 = 16;
                }
            }
            i4 -= 2;
            i5 = parsableBitArray2.readBits(8);
            i6 = parsableBitArray2.readBits(8);
            sparseArray.put(readBits10, new RegionObject(i2, readBits11, readBits12, readBits13, i5, i6));
            readBits10 = 4;
            readBits9 = i7;
            i3 = 2;
            i2 = 16;
        }
        return new RegionComposition(readBits, readBit, readBits2, readBits3, readBits4, readBits5, readBits6, readBits7, readBits8, readBits9, sparseArray);
    }

    private static ClutDefinition parseClutDefinition(ParsableBitArray parsableBitArray, int i) {
        ParsableBitArray parsableBitArray2 = parsableBitArray;
        int i2 = 8;
        int readBits = parsableBitArray2.readBits(8);
        parsableBitArray2.skipBits(8);
        int i3 = 2;
        int i4 = i - 2;
        int[] generateDefault2BitClutEntries = generateDefault2BitClutEntries();
        int[] generateDefault4BitClutEntries = generateDefault4BitClutEntries();
        int[] generateDefault8BitClutEntries = generateDefault8BitClutEntries();
        while (i4 > 0) {
            int readBits2;
            int readBits3;
            int readBits4;
            int readBits5 = parsableBitArray2.readBits(i2);
            int readBits6 = parsableBitArray2.readBits(i2);
            i4 -= 2;
            int[] iArr = (readBits6 & 128) != 0 ? generateDefault2BitClutEntries : (readBits6 & 64) != 0 ? generateDefault4BitClutEntries : generateDefault8BitClutEntries;
            if ((readBits6 & 1) != 0) {
                readBits6 = parsableBitArray2.readBits(i2);
                readBits2 = parsableBitArray2.readBits(i2);
                readBits3 = parsableBitArray2.readBits(i2);
                readBits4 = parsableBitArray2.readBits(i2);
                i4 -= 4;
            } else {
                readBits3 = parsableBitArray2.readBits(4) << 4;
                i4 -= 2;
                readBits4 = parsableBitArray2.readBits(i3) << 6;
                readBits6 = parsableBitArray2.readBits(6) << i3;
                readBits2 = parsableBitArray2.readBits(4) << 4;
            }
            if (readBits6 == 0) {
                readBits4 = 255;
                readBits2 = 0;
                readBits3 = 0;
            }
            int i5 = i4;
            double d = (double) readBits6;
            int i6 = readBits;
            double d2 = (double) (readBits2 - 128);
            double d3 = (double) (readBits3 - 128);
            iArr[readBits5] = getColor((byte) (255 - (readBits4 & 255)), Util.constrainValue((int) (d + (1.402d * d2)), 0, 255), Util.constrainValue((int) ((d - (0.34414d * d3)) - (0.71414d * d2)), 0, 255), Util.constrainValue((int) (d + (1.772d * d3)), 0, 255));
            i4 = i5;
            readBits = i6;
            i2 = 8;
            i3 = 2;
        }
        return new ClutDefinition(readBits, generateDefault2BitClutEntries, generateDefault4BitClutEntries, generateDefault8BitClutEntries);
    }

    private static ObjectData parseObjectData(ParsableBitArray parsableBitArray) {
        byte[] bArr;
        int readBits = parsableBitArray.readBits(16);
        parsableBitArray.skipBits(4);
        int readBits2 = parsableBitArray.readBits(2);
        boolean readBit = parsableBitArray.readBit();
        parsableBitArray.skipBits(1);
        byte[] bArr2 = null;
        if (readBits2 == 1) {
            parsableBitArray.skipBits(parsableBitArray.readBits(8) * 16);
        } else if (readBits2 == 0) {
            readBits2 = parsableBitArray.readBits(16);
            int readBits3 = parsableBitArray.readBits(16);
            if (readBits2 > 0) {
                bArr2 = new byte[readBits2];
                parsableBitArray.readBytes(bArr2, 0, readBits2);
            }
            if (readBits3 > 0) {
                bArr = new byte[readBits3];
                parsableBitArray.readBytes(bArr, 0, readBits3);
                return new ObjectData(readBits, readBit, bArr2, bArr);
            }
        }
        bArr = bArr2;
        return new ObjectData(readBits, readBit, bArr2, bArr);
    }

    private static int[] generateDefault2BitClutEntries() {
        return new int[]{0, -1, Theme.ACTION_BAR_VIDEO_EDIT_COLOR, -8421505};
    }

    private static int[] generateDefault4BitClutEntries() {
        int[] iArr = new int[16];
        iArr[0] = 0;
        for (int i = 1; i < iArr.length; i++) {
            if (i < 8) {
                iArr[i] = getColor(255, (i & 1) != 0 ? 255 : 0, (i & 2) != 0 ? 255 : 0, (i & 4) != 0 ? 255 : 0);
            } else {
                int i2 = 127;
                int i3 = (i & 1) != 0 ? 127 : 0;
                int i4 = (i & 2) != 0 ? 127 : 0;
                if ((i & 4) == 0) {
                    i2 = 0;
                }
                iArr[i] = getColor(255, i3, i4, i2);
            }
        }
        return iArr;
    }

    private static int[] generateDefault8BitClutEntries() {
        int[] iArr = new int[256];
        iArr[0] = 0;
        for (int i = 0; i < iArr.length; i++) {
            int i2 = 255;
            int i3;
            int i4;
            if (i < 8) {
                i3 = (i & 1) != 0 ? 255 : 0;
                i4 = (i & 2) != 0 ? 255 : 0;
                if ((i & 4) == 0) {
                    i2 = 0;
                }
                iArr[i] = getColor(63, i3, i4, i2);
            } else {
                i3 = i & 136;
                i4 = 170;
                int i5 = 85;
                int i6;
                if (i3 == 0) {
                    i6 = ((i & 1) != 0 ? 85 : 0) + ((i & 16) != 0 ? 170 : 0);
                    i3 = ((i & 2) != 0 ? 85 : 0) + ((i & 32) != 0 ? 170 : 0);
                    if ((i & 4) == 0) {
                        i5 = 0;
                    }
                    if ((i & 64) == 0) {
                        i4 = 0;
                    }
                    iArr[i] = getColor(255, i6, i3, i5 + i4);
                } else if (i3 != 8) {
                    i4 = 43;
                    if (i3 == 128) {
                        i6 = (((i & 1) != 0 ? 43 : 0) + 127) + ((i & 16) != 0 ? 85 : 0);
                        i3 = (((i & 2) != 0 ? 43 : 0) + 127) + ((i & 32) != 0 ? 85 : 0);
                        if ((i & 4) == 0) {
                            i4 = 0;
                        }
                        int i7 = 127 + i4;
                        if ((i & 64) == 0) {
                            i5 = 0;
                        }
                        iArr[i] = getColor(255, i6, i3, i7 + i5);
                    } else if (i3 == 136) {
                        i6 = ((i & 1) != 0 ? 43 : 0) + ((i & 16) != 0 ? 85 : 0);
                        i3 = ((i & 2) != 0 ? 43 : 0) + ((i & 32) != 0 ? 85 : 0);
                        if ((i & 4) == 0) {
                            i4 = 0;
                        }
                        if ((i & 64) == 0) {
                            i5 = 0;
                        }
                        iArr[i] = getColor(255, i6, i3, i4 + i5);
                    }
                } else {
                    i6 = ((i & 1) != 0 ? 85 : 0) + ((i & 16) != 0 ? 170 : 0);
                    i2 = ((i & 2) != 0 ? 85 : 0) + ((i & 32) != 0 ? 170 : 0);
                    if ((i & 4) == 0) {
                        i5 = 0;
                    }
                    if ((i & 64) == 0) {
                        i4 = 0;
                    }
                    iArr[i] = getColor(127, i6, i2, i5 + i4);
                }
            }
        }
        return iArr;
    }

    private static void paintPixelDataSubBlocks(ObjectData objectData, ClutDefinition clutDefinition, int i, int i2, int i3, Paint paint, Canvas canvas) {
        if (i == 3) {
            clutDefinition = clutDefinition.clutEntries8Bit;
        } else if (i == 2) {
            clutDefinition = clutDefinition.clutEntries4Bit;
        } else {
            clutDefinition = clutDefinition.clutEntries2Bit;
        }
        int[] iArr = clutDefinition;
        int i4 = i;
        int i5 = i2;
        Paint paint2 = paint;
        Canvas canvas2 = canvas;
        paintPixelDataSubBlock(objectData.topFieldData, iArr, i4, i5, i3, paint2, canvas2);
        paintPixelDataSubBlock(objectData.bottomFieldData, iArr, i4, i5, i3 + 1, paint2, canvas2);
    }

    private static void paintPixelDataSubBlock(byte[] bArr, int[] iArr, int i, int i2, int i3, Paint paint, Canvas canvas) {
        int i4 = i;
        ParsableBitArray parsableBitArray = new ParsableBitArray(bArr);
        int i5 = i2;
        int i6 = i3;
        byte[] bArr2 = null;
        byte[] bArr3 = bArr2;
        while (parsableBitArray.bitsLeft() != 0) {
            int readBits = parsableBitArray.readBits(8);
            if (readBits != 240) {
                int paint2BitPixelCodeString;
                byte[] bArr4;
                switch (readBits) {
                    case 16:
                        byte[] bArr5;
                        if (i4 != 3) {
                            if (i4 != 2) {
                                bArr5 = null;
                                paint2BitPixelCodeString = paint2BitPixelCodeString(parsableBitArray, iArr, bArr5, i5, i6, paint, canvas);
                                parsableBitArray.byteAlign();
                                break;
                            }
                            bArr4 = bArr3 == null ? defaultMap2To4 : bArr3;
                        } else {
                            bArr4 = bArr2 == null ? defaultMap2To8 : bArr2;
                        }
                        bArr5 = bArr4;
                        paint2BitPixelCodeString = paint2BitPixelCodeString(parsableBitArray, iArr, bArr5, i5, i6, paint, canvas);
                        parsableBitArray.byteAlign();
                    case 17:
                        paint2BitPixelCodeString = paint4BitPixelCodeString(parsableBitArray, iArr, i4 == 3 ? defaultMap4To8 : null, i5, i6, paint, canvas);
                        parsableBitArray.byteAlign();
                        break;
                    case 18:
                        paint2BitPixelCodeString = paint8BitPixelCodeString(parsableBitArray, iArr, null, i5, i6, paint, canvas);
                        break;
                    default:
                        switch (readBits) {
                            case 32:
                                bArr3 = buildClutMapTable(4, 4, parsableBitArray);
                                continue;
                            case DATA_TYPE_28_TABLE_DATA /*33*/:
                                bArr4 = buildClutMapTable(4, 8, parsableBitArray);
                                break;
                            case DATA_TYPE_48_TABLE_DATA /*34*/:
                                bArr4 = buildClutMapTable(16, 8, parsableBitArray);
                                break;
                            default:
                                continue;
                                continue;
                        }
                        bArr2 = bArr4;
                        break;
                }
                i5 = paint2BitPixelCodeString;
            } else {
                i6 += 2;
                i5 = i2;
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static int paint2BitPixelCodeString(ParsableBitArray parsableBitArray, int[] iArr, byte[] bArr, int i, int i2, Paint paint, Canvas canvas) {
        ParsableBitArray parsableBitArray2 = parsableBitArray;
        int i3 = i2;
        Paint paint2 = paint;
        int i4 = i;
        Object obj = null;
        while (true) {
            Object obj2;
            int i5;
            int i6;
            int readBits = parsableBitArray2.readBits(2);
            if (readBits != 0) {
                obj2 = obj;
                i5 = readBits;
                i6 = 1;
            } else {
                if (parsableBitArray2.readBit()) {
                    readBits = 3 + parsableBitArray2.readBits(3);
                    i5 = parsableBitArray2.readBits(2);
                } else {
                    if (!parsableBitArray2.readBit()) {
                        switch (parsableBitArray2.readBits(2)) {
                            case 0:
                                int i7 = 1;
                                break;
                            case 1:
                                obj2 = obj;
                                i6 = 2;
                                break;
                            case 2:
                                readBits = 12 + parsableBitArray2.readBits(4);
                                i5 = parsableBitArray2.readBits(2);
                                break;
                            case 3:
                                readBits = 29 + parsableBitArray2.readBits(8);
                                i5 = parsableBitArray2.readBits(2);
                                break;
                            default:
                                obj2 = obj;
                                break;
                        }
                    }
                    obj2 = obj;
                    i6 = 1;
                    i5 = 0;
                }
                obj2 = obj;
                i6 = readBits;
            }
            if (!(i6 == 0 || paint2 == null)) {
                if (bArr != null) {
                    i5 = bArr[i5];
                }
                paint2.setColor(iArr[i5]);
                Canvas canvas2 = canvas;
                canvas2.drawRect((float) i4, (float) i3, (float) (i4 + i6), (float) (i3 + 1), paint2);
            }
            i4 += i6;
            if (obj2 != null) {
                return i4;
            }
            obj = obj2;
        }
    }

    private static int paint4BitPixelCodeString(ParsableBitArray parsableBitArray, int[] iArr, byte[] bArr, int i, int i2, Paint paint, Canvas canvas) {
        ParsableBitArray parsableBitArray2 = parsableBitArray;
        int i3 = i2;
        Paint paint2 = paint;
        int i4 = i;
        Object obj = null;
        while (true) {
            Object obj2;
            int i5;
            int i6;
            int readBits = parsableBitArray2.readBits(4);
            if (readBits != 0) {
                obj2 = obj;
                i5 = readBits;
                i6 = 1;
            } else {
                if (parsableBitArray2.readBit()) {
                    if (parsableBitArray2.readBit()) {
                        switch (parsableBitArray2.readBits(2)) {
                            case 0:
                                obj2 = obj;
                                i6 = 1;
                                break;
                            case 1:
                                obj2 = obj;
                                i6 = 2;
                                break;
                            case 2:
                                readBits = 9 + parsableBitArray2.readBits(4);
                                i5 = parsableBitArray2.readBits(4);
                                break;
                            case 3:
                                readBits = 25 + parsableBitArray2.readBits(8);
                                i5 = parsableBitArray2.readBits(4);
                                break;
                            default:
                                obj2 = obj;
                                break;
                        }
                    }
                    readBits = parsableBitArray2.readBits(2) + 4;
                    i5 = parsableBitArray2.readBits(4);
                    obj2 = obj;
                    i6 = readBits;
                } else {
                    i5 = parsableBitArray2.readBits(3);
                    if (i5 != 0) {
                        obj2 = obj;
                        i6 = i5 + 2;
                    } else {
                        obj2 = 1;
                        i5 = 0;
                        i6 = i5;
                    }
                }
                i5 = 0;
            }
            if (!(i6 == 0 || paint2 == null)) {
                if (bArr != null) {
                    i5 = bArr[i5];
                }
                paint2.setColor(iArr[i5]);
                Canvas canvas2 = canvas;
                canvas2.drawRect((float) i4, (float) i3, (float) (i4 + i6), (float) (i3 + 1), paint2);
            }
            i4 += i6;
            if (obj2 != null) {
                return i4;
            }
            obj = obj2;
        }
    }

    private static int paint8BitPixelCodeString(ParsableBitArray parsableBitArray, int[] iArr, byte[] bArr, int i, int i2, Paint paint, Canvas canvas) {
        ParsableBitArray parsableBitArray2 = parsableBitArray;
        int i3 = i2;
        Paint paint2 = paint;
        int i4 = i;
        Object obj = null;
        while (true) {
            Object obj2;
            int i5;
            int i6;
            int readBits = parsableBitArray2.readBits(8);
            if (readBits != 0) {
                obj2 = obj;
                i5 = readBits;
                i6 = 1;
            } else if (parsableBitArray2.readBit()) {
                readBits = parsableBitArray2.readBits(7);
                i5 = parsableBitArray2.readBits(8);
                obj2 = obj;
                i6 = readBits;
            } else {
                i5 = parsableBitArray2.readBits(7);
                if (i5 != 0) {
                    obj2 = obj;
                    i6 = i5;
                    i5 = 0;
                } else {
                    obj2 = 1;
                    i5 = 0;
                    i6 = i5;
                }
            }
            if (!(i6 == 0 || paint2 == null)) {
                if (bArr != null) {
                    i5 = bArr[i5];
                }
                paint2.setColor(iArr[i5]);
                Canvas canvas2 = canvas;
                canvas2.drawRect((float) i4, (float) i3, (float) (i4 + i6), (float) (i3 + 1), paint2);
            }
            i4 += i6;
            if (obj2 != null) {
                return i4;
            }
            obj = obj2;
        }
    }

    private static byte[] buildClutMapTable(int i, int i2, ParsableBitArray parsableBitArray) {
        byte[] bArr = new byte[i];
        for (int i3 = 0; i3 < i; i3++) {
            bArr[i3] = (byte) parsableBitArray.readBits(i2);
        }
        return bArr;
    }
}
