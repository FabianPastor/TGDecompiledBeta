package org.telegram.messenger.audioinfo.m4a;

import java.io.IOException;
import java.io.InputStream;
import org.telegram.messenger.audioinfo.util.PositionInputStream;
/* loaded from: classes.dex */
public final class MP4Input extends MP4Box<PositionInputStream> {
    public MP4Input(InputStream inputStream) {
        super(new PositionInputStream(inputStream), null, "");
    }

    public MP4Atom nextChildUpTo(String str) throws IOException {
        MP4Atom nextChild;
        do {
            nextChild = nextChild();
        } while (!nextChild.getType().matches(str));
        return nextChild;
    }

    public String toString() {
        return "mp4[pos=" + getPosition() + "]";
    }
}
