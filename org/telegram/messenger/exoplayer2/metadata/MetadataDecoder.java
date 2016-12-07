package org.telegram.messenger.exoplayer2.metadata;

public interface MetadataDecoder {
    boolean canDecode(String str);

    Metadata decode(byte[] bArr, int i) throws MetadataDecoderException;
}
