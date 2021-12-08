package org.telegram.ui.Components.Paint;

import android.graphics.PointF;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class RenderState {
    private static final int DEFAULT_STATE_SIZE = 256;
    private int allocatedCount;
    public float alpha;
    public float angle;
    public float baseWeight;
    private ByteBuffer buffer;
    private int count;
    public double remainder;
    public float scale;
    public float spacing;
    public float viewportScale;

    public int getCount() {
        return this.count;
    }

    public void prepare() {
        this.count = 0;
        if (this.buffer == null) {
            this.allocatedCount = 256;
            ByteBuffer allocateDirect = ByteBuffer.allocateDirect(256 * 5 * 4);
            this.buffer = allocateDirect;
            allocateDirect.order(ByteOrder.nativeOrder());
            this.buffer.position(0);
        }
    }

    public float read() {
        return this.buffer.getFloat();
    }

    public void setPosition(int position) {
        ByteBuffer byteBuffer = this.buffer;
        if (byteBuffer != null && position >= 0 && position < this.allocatedCount) {
            byteBuffer.position(position * 5 * 4);
        }
    }

    public void appendValuesCount(int count2) {
        int newTotalCount = this.count + count2;
        if (newTotalCount > this.allocatedCount || this.buffer == null) {
            resizeBuffer();
        }
        this.count = newTotalCount;
    }

    public void resizeBuffer() {
        if (this.buffer != null) {
            this.buffer = null;
        }
        int max = Math.max(this.allocatedCount * 2, 256);
        this.allocatedCount = max;
        ByteBuffer allocateDirect = ByteBuffer.allocateDirect(max * 5 * 4);
        this.buffer = allocateDirect;
        allocateDirect.order(ByteOrder.nativeOrder());
        this.buffer.position(0);
    }

    public boolean addPoint(PointF point, float size, float angle2, float alpha2, int index) {
        if ((index == -1 || index < this.allocatedCount) && this.buffer.position() != this.buffer.limit()) {
            if (index != -1) {
                this.buffer.position(index * 5 * 4);
            }
            this.buffer.putFloat(point.x);
            this.buffer.putFloat(point.y);
            this.buffer.putFloat(size);
            this.buffer.putFloat(angle2);
            this.buffer.putFloat(alpha2);
            return true;
        }
        resizeBuffer();
        return false;
    }

    public void reset() {
        this.count = 0;
        this.remainder = 0.0d;
        ByteBuffer byteBuffer = this.buffer;
        if (byteBuffer != null) {
            byteBuffer.position(0);
        }
    }
}
