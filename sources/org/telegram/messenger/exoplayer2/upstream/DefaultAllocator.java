package org.telegram.messenger.exoplayer2.upstream;

import java.util.Arrays;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.Util;

public final class DefaultAllocator implements Allocator {
    private static final int AVAILABLE_EXTRA_CAPACITY = 100;
    private int allocatedCount;
    private Allocation[] availableAllocations;
    private int availableCount;
    private final int individualAllocationSize;
    private final byte[] initialAllocationBlock;
    private final Allocation[] singleAllocationReleaseHolder;
    private int targetBufferSize;
    private final boolean trimOnReset;

    public DefaultAllocator(boolean z, int i) {
        this(z, i, 0);
    }

    public DefaultAllocator(boolean z, int i, int i2) {
        int i3 = 0;
        Assertions.checkArgument(i > 0);
        Assertions.checkArgument(i2 >= 0);
        this.trimOnReset = z;
        this.individualAllocationSize = i;
        this.availableCount = i2;
        this.availableAllocations = new Allocation[(i2 + AVAILABLE_EXTRA_CAPACITY)];
        if (i2 > 0) {
            this.initialAllocationBlock = new byte[(i2 * i)];
            while (i3 < i2) {
                this.availableAllocations[i3] = new Allocation(this.initialAllocationBlock, i3 * i);
                i3++;
            }
        } else {
            this.initialAllocationBlock = false;
        }
        this.singleAllocationReleaseHolder = new Allocation[1];
    }

    public synchronized void reset() {
        if (this.trimOnReset) {
            setTargetBufferSize(0);
        }
    }

    public synchronized void setTargetBufferSize(int i) {
        Object obj = i < this.targetBufferSize ? 1 : null;
        this.targetBufferSize = i;
        if (obj != null) {
            trim();
        }
    }

    public synchronized Allocation allocate() {
        Allocation allocation;
        this.allocatedCount++;
        if (this.availableCount > 0) {
            Allocation[] allocationArr = this.availableAllocations;
            int i = this.availableCount - 1;
            this.availableCount = i;
            allocation = allocationArr[i];
            this.availableAllocations[this.availableCount] = null;
        } else {
            allocation = new Allocation(new byte[this.individualAllocationSize], 0);
        }
        return allocation;
    }

    public synchronized void release(Allocation allocation) {
        this.singleAllocationReleaseHolder[0] = allocation;
        release(this.singleAllocationReleaseHolder);
    }

    public synchronized void release(Allocation[] allocationArr) {
        if (this.availableCount + allocationArr.length >= this.availableAllocations.length) {
            this.availableAllocations = (Allocation[]) Arrays.copyOf(this.availableAllocations, Math.max(this.availableAllocations.length * 2, this.availableCount + allocationArr.length));
        }
        for (Allocation allocation : allocationArr) {
            boolean z;
            Allocation[] allocationArr2;
            int i;
            if (allocation.data != this.initialAllocationBlock) {
                if (allocation.data.length != this.individualAllocationSize) {
                    z = false;
                    Assertions.checkArgument(z);
                    allocationArr2 = this.availableAllocations;
                    i = this.availableCount;
                    this.availableCount = i + 1;
                    allocationArr2[i] = allocation;
                }
            }
            z = true;
            Assertions.checkArgument(z);
            allocationArr2 = this.availableAllocations;
            i = this.availableCount;
            this.availableCount = i + 1;
            allocationArr2[i] = allocation;
        }
        this.allocatedCount -= allocationArr.length;
        notifyAll();
    }

    public synchronized void trim() {
        int i = 0;
        int max = Math.max(0, Util.ceilDivide(this.targetBufferSize, this.individualAllocationSize) - this.allocatedCount);
        if (max < this.availableCount) {
            if (this.initialAllocationBlock != null) {
                int i2 = this.availableCount - 1;
                while (i <= i2) {
                    Allocation allocation = this.availableAllocations[i];
                    if (allocation.data == this.initialAllocationBlock) {
                        i++;
                    } else {
                        Allocation allocation2 = this.availableAllocations[i2];
                        if (allocation2.data != this.initialAllocationBlock) {
                            i2--;
                        } else {
                            int i3 = i + 1;
                            this.availableAllocations[i] = allocation2;
                            int i4 = i2 - 1;
                            this.availableAllocations[i2] = allocation;
                            i2 = i4;
                            i = i3;
                        }
                    }
                }
                max = Math.max(max, i);
                if (max >= this.availableCount) {
                    return;
                }
            }
            Arrays.fill(this.availableAllocations, max, this.availableCount, null);
            this.availableCount = max;
        }
    }

    public synchronized int getTotalBytesAllocated() {
        return this.allocatedCount * this.individualAllocationSize;
    }

    public int getIndividualAllocationLength() {
        return this.individualAllocationSize;
    }
}
