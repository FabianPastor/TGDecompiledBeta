package org.telegram.tgnet;

public class TLRPC$TL_notificationSoundRingtone extends TLRPC$NotificationSound {
    public static int constructor = -9666487;
    public long id;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.id = abstractSerializedData.readInt64(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt64(this.id);
    }
}
