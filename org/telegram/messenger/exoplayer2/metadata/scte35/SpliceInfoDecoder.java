package org.telegram.messenger.exoplayer2.metadata.scte35;

import android.text.TextUtils;
import org.telegram.messenger.exoplayer2.metadata.Metadata;
import org.telegram.messenger.exoplayer2.metadata.Metadata.Entry;
import org.telegram.messenger.exoplayer2.metadata.MetadataDecoder;
import org.telegram.messenger.exoplayer2.metadata.MetadataDecoderException;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.exoplayer2.util.ParsableBitArray;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

public final class SpliceInfoDecoder implements MetadataDecoder {
    private static final int TYPE_PRIVATE_COMMAND = 255;
    private static final int TYPE_SPLICE_INSERT = 5;
    private static final int TYPE_SPLICE_NULL = 0;
    private static final int TYPE_SPLICE_SCHEDULE = 4;
    private static final int TYPE_TIME_SIGNAL = 6;
    private final ParsableByteArray sectionData = new ParsableByteArray();
    private final ParsableBitArray sectionHeader = new ParsableBitArray();

    public boolean canDecode(String mimeType) {
        return TextUtils.equals(mimeType, MimeTypes.APPLICATION_SCTE35);
    }

    public Metadata decode(byte[] data, int size) throws MetadataDecoderException {
        this.sectionData.reset(data, size);
        this.sectionHeader.reset(data, size);
        this.sectionHeader.skipBits(39);
        long ptsAdjustment = (((long) this.sectionHeader.readBits(1)) << 32) | ((long) this.sectionHeader.readBits(32));
        this.sectionHeader.skipBits(20);
        int spliceCommandLength = this.sectionHeader.readBits(12);
        int spliceCommandType = this.sectionHeader.readBits(8);
        SpliceCommand command = null;
        this.sectionData.skipBytes(14);
        switch (spliceCommandType) {
            case 0:
                command = new SpliceNullCommand();
                break;
            case 4:
                command = SpliceScheduleCommand.parseFromSection(this.sectionData);
                break;
            case 5:
                command = SpliceInsertCommand.parseFromSection(this.sectionData, ptsAdjustment);
                break;
            case 6:
                command = TimeSignalCommand.parseFromSection(this.sectionData, ptsAdjustment);
                break;
            case 255:
                command = PrivateCommand.parseFromSection(this.sectionData, spliceCommandLength, ptsAdjustment);
                break;
        }
        if (command == null) {
            return new Metadata(new Entry[0]);
        }
        return new Metadata(command);
    }
}
