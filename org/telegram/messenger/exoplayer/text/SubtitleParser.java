package org.telegram.messenger.exoplayer.text;

import org.telegram.messenger.exoplayer.ParserException;

public interface SubtitleParser {
    boolean canParse(String str);

    Subtitle parse(byte[] bArr, int i, int i2) throws ParserException;
}
