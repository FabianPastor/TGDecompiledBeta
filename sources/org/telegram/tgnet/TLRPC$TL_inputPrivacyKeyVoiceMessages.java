package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_inputPrivacyKeyVoiceMessages extends TLRPC$InputPrivacyKey {
    public static int constructor = -NUM;

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
