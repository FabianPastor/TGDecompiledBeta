package org.telegram.messenger.exoplayer.metadata;

import org.telegram.messenger.exoplayer.ParserException;

public interface MetadataParser<T> {
    boolean canParse(String str);

    T parse(byte[] bArr, int i) throws ParserException;
}
