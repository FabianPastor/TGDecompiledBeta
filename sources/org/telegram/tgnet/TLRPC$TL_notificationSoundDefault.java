package org.telegram.tgnet;

public class TLRPC$TL_notificationSoundDefault extends TLRPC$NotificationSound {
    public static int constructor = -NUM;

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
