package org.telegram.tgnet;

public class TLObject {
    private static final ThreadLocal<NativeByteBuffer> sizeCalculator = new ThreadLocal<NativeByteBuffer>() {
        /* access modifiers changed from: protected */
        public NativeByteBuffer initialValue() {
            return new NativeByteBuffer(true);
        }
    };
    public boolean disableFree = false;
    public int networkType;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return null;
    }

    public void freeResources() {
    }

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
    }

    public int getObjectSize() {
        NativeByteBuffer nativeByteBuffer = sizeCalculator.get();
        nativeByteBuffer.rewind();
        serializeToStream(sizeCalculator.get());
        return nativeByteBuffer.length();
    }
}
