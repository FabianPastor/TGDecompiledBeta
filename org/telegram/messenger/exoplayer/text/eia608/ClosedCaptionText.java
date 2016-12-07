package org.telegram.messenger.exoplayer.text.eia608;

final class ClosedCaptionText extends ClosedCaption {
    public final String text;

    public ClosedCaptionText(String text) {
        super(1);
        this.text = text;
    }
}
