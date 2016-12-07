package org.telegram.messenger.exoplayer2.metadata.scte35;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

public final class PrivateCommand extends SpliceCommand {
    public static final Creator<PrivateCommand> CREATOR = new Creator<PrivateCommand>() {
        public PrivateCommand createFromParcel(Parcel in) {
            return new PrivateCommand(in);
        }

        public PrivateCommand[] newArray(int size) {
            return new PrivateCommand[size];
        }
    };
    public final byte[] commandBytes;
    public final long identifier;
    public final long ptsAdjustment;

    private PrivateCommand(long identifier, byte[] commandBytes, long ptsAdjustment) {
        this.ptsAdjustment = ptsAdjustment;
        this.identifier = identifier;
        this.commandBytes = commandBytes;
    }

    private PrivateCommand(Parcel in) {
        this.ptsAdjustment = in.readLong();
        this.identifier = in.readLong();
        this.commandBytes = new byte[in.readInt()];
        in.readByteArray(this.commandBytes);
    }

    static PrivateCommand parseFromSection(ParsableByteArray sectionData, int commandLength, long ptsAdjustment) {
        long identifier = sectionData.readUnsignedInt();
        byte[] privateBytes = new byte[(commandLength - 4)];
        sectionData.readBytes(privateBytes, 0, privateBytes.length);
        return new PrivateCommand(identifier, privateBytes, ptsAdjustment);
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.ptsAdjustment);
        dest.writeLong(this.identifier);
        dest.writeInt(this.commandBytes.length);
        dest.writeByteArray(this.commandBytes);
    }
}
