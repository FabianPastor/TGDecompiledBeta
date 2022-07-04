package org.webrtc;

public class Size {
    public int height;
    public int width;

    public Size(int width2, int height2) {
        this.width = width2;
        this.height = height2;
    }

    public String toString() {
        return this.width + "x" + this.height;
    }

    public boolean equals(Object other) {
        if (!(other instanceof Size)) {
            return false;
        }
        Size otherSize = (Size) other;
        if (this.width == otherSize.width && this.height == otherSize.height) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return (this.width * 65537) + 1 + this.height;
    }
}
