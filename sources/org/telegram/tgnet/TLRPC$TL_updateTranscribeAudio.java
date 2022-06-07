package org.telegram.tgnet;

public class TLRPC$TL_updateTranscribeAudio extends TLRPC$Update {
    public static int constructor = -NUM;
    public int flags;
    public boolean isFinal;
    public String text;
    public long transcription_id;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        boolean z2 = true;
        if ((readInt32 & 1) == 0) {
            z2 = false;
        }
        this.isFinal = z2;
        this.transcription_id = abstractSerializedData.readInt64(z);
        this.text = abstractSerializedData.readString(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.isFinal ? this.flags | 1 : this.flags & -2;
        this.flags = i;
        abstractSerializedData.writeInt32(i);
        abstractSerializedData.writeInt64(this.transcription_id);
        abstractSerializedData.writeString(this.text);
    }
}
