package org.telegram.tgnet;

public abstract class TLRPC$VideoSize extends TLObject {
    public int flags;
    public int h;
    public TLRPC$FileLocation location;
    public int size;
    public String type;
    public double video_start_ts;
    public int w;

    public static TLRPC$VideoSize TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$VideoSize tLRPC$VideoSize;
        if (i != -NUM) {
            tLRPC$VideoSize = i != NUM ? null : new TLRPC$TL_videoSize_layer115();
        } else {
            tLRPC$VideoSize = new TLRPC$TL_videoSize();
        }
        if (tLRPC$VideoSize != null || !z) {
            if (tLRPC$VideoSize != null) {
                tLRPC$VideoSize.readParams(abstractSerializedData, z);
            }
            return tLRPC$VideoSize;
        }
        throw new RuntimeException(String.format("can't parse magic %x in VideoSize", new Object[]{Integer.valueOf(i)}));
    }
}
