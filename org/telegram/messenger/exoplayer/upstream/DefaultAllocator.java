package org.telegram.messenger.exoplayer.upstream;

import java.util.Arrays;
import org.telegram.messenger.exoplayer.util.Assertions;
import org.telegram.messenger.exoplayer.util.Util;

public final class DefaultAllocator implements Allocator {
    private static final int AVAILABLE_EXTRA_CAPACITY = 100;
    private int allocatedCount;
    private Allocation[] availableAllocations;
    private int availableCount;
    private final int individualAllocationSize;
    private final byte[] initialAllocationBlock;

    public DefaultAllocator(int individualAllocationSize) {
        this(individualAllocationSize, 0);
    }

    public DefaultAllocator(int individualAllocationSize, int initialAllocationCount) {
        boolean z;
        boolean z2 = true;
        if (individualAllocationSize > 0) {
            z = true;
        } else {
            z = false;
        }
        Assertions.checkArgument(z);
        if (initialAllocationCount < 0) {
            z2 = false;
        }
        Assertions.checkArgument(z2);
        this.individualAllocationSize = individualAllocationSize;
        this.availableCount = initialAllocationCount;
        this.availableAllocations = new Allocation[(initialAllocationCount + AVAILABLE_EXTRA_CAPACITY)];
        if (initialAllocationCount > 0) {
            this.initialAllocationBlock = new byte[(initialAllocationCount * individualAllocationSize)];
            for (int i = 0; i < initialAllocationCount; i++) {
                this.availableAllocations[i] = new Allocation(this.initialAllocationBlock, i * individualAllocationSize);
            }
            return;
        }
        this.initialAllocationBlock = null;
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
        boolean z = allocation.data == this.initialAllocationBlock || allocation.data.length == this.individualAllocationSize;
        Assertions.checkArgument(z);
        this.allocatedCount--;
        if (this.availableCount == this.availableAllocations.length) {
            this.availableAllocations = (Allocation[]) Arrays.copyOf(this.availableAllocations, this.availableAllocations.length * 2);
        }
        Allocation[] allocationArr = this.availableAllocations;
        int i = this.availableCount;
        this.availableCount = i + 1;
        allocationArr[i] = allocation;
        notifyAll();
    }

    public synchronized void release(Allocation[] allocations) {
        if (this.availableCount + allocations.length >= this.availableAllocations.length) {
            this.availableAllocations = (Allocation[]) Arrays.copyOf(this.availableAllocations, Math.max(this.availableAllocations.length * 2, this.availableCount + allocations.length));
        }
        for (Allocation allocation : allocations) {
            boolean z;
            if (allocation.data == this.initialAllocationBlock || allocation.data.length == this.individualAllocationSize) {
                z = true;
            } else {
                z = false;
            }
            Assertions.checkArgument(z);
            Allocation[] allocationArr = this.availableAllocations;
            int i = this.availableCount;
            this.availableCount = i + 1;
            allocationArr[i] = allocation;
        }
        this.allocatedCount -= allocations.length;
        notifyAll();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void trim(int targetSize) {
        int targetAvailableCount = Math.max(0, Util.ceilDivide(targetSize, this.individualAllocationSize) - this.allocatedCount);
        if (targetAvailableCount < this.availableCount) {
            if (this.initialAllocationBlock != null) {
                int highIndex = this.availableCount - 1;
                int lowIndex = 0;
                while (lowIndex <= highIndex) {
                    int lowIndex2;
                    int highIndex2;
                    Allocation lowAllocation = this.availableAllocations[lowIndex];
                    if (lowAllocation.data == this.initialAllocationBlock) {
                        lowIndex2 = lowIndex + 1;
                        highIndex2 = highIndex;
                    } else {
                        Allocation highAllocation = this.availableAllocations[lowIndex];
                        if (highAllocation.data != this.initialAllocationBlock) {
                            highIndex2 = highIndex - 1;
                            lowIndex2 = lowIndex;
                        } else {
                            lowIndex2 = lowIndex + 1;
                            this.availableAllocations[lowIndex] = highAllocation;
                            highIndex2 = highIndex - 1;
                            this.availableAllocations[highIndex] = lowAllocation;
                        }
                    }
                    highIndex = highIndex2;
                    lowIndex = lowIndex2;
                }
                targetAvailableCount = Math.max(targetAvailableCount, lowIndex);
            }
            Arrays.fill(this.availableAllocations, targetAvailableCount, this.availableCount, null);
            this.availableCount = targetAvailableCount;
        }
    }

    public synchronized int getTotalBytesAllocated() {
        return this.allocatedCount * this.individualAllocationSize;
    }

    public synchronized void blockWhileTotalBytesAllocatedExceeds(int limit) throws InterruptedException {
        while (getTotalBytesAllocated() > limit) {
            wait();
        }
    }

    public int getIndividualAllocationLength() {
        return this.individualAllocationSize;
    }
}
