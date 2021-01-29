package org.telegram.tgnet;

public class TLRPC$TL_messageMediaAudio_layer45 extends TLRPC$MessageMedia {
    public static int constructor = -NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.audio_unused = TLRPC$Audio.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.audio_unused.serializeToStream(abstractSerializedData);
    }
}
