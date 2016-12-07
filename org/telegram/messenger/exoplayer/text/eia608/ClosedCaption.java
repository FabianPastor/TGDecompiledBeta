package org.telegram.messenger.exoplayer.text.eia608;

abstract class ClosedCaption {
    public static final int TYPE_CTRL = 0;
    public static final int TYPE_TEXT = 1;
    public final int type;

    protected ClosedCaption(int type) {
        this.type = type;
    }
}
