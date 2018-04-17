package org.telegram.messenger.exoplayer2.source;

import java.util.Arrays;
import java.util.Random;

public interface ShuffleOrder {

    public static class DefaultShuffleOrder implements ShuffleOrder {
        private final int[] indexInShuffled;
        private final Random random;
        private final int[] shuffled;

        public DefaultShuffleOrder(int length) {
            this(length, new Random());
        }

        public DefaultShuffleOrder(int length, long randomSeed) {
            this(length, new Random(randomSeed));
        }

        private DefaultShuffleOrder(int length, Random random) {
            this(createShuffledList(length, random), random);
        }

        private DefaultShuffleOrder(int[] shuffled, Random random) {
            this.shuffled = shuffled;
            this.random = random;
            this.indexInShuffled = new int[shuffled.length];
            for (int i = 0; i < shuffled.length; i++) {
                this.indexInShuffled[shuffled[i]] = i;
            }
        }

        public int getLength() {
            return this.shuffled.length;
        }

        public int getNextIndex(int index) {
            int shuffledIndex = this.indexInShuffled[index] + 1;
            return shuffledIndex < this.shuffled.length ? this.shuffled[shuffledIndex] : -1;
        }

        public int getPreviousIndex(int index) {
            int shuffledIndex = this.indexInShuffled[index] - 1;
            return shuffledIndex >= 0 ? this.shuffled[shuffledIndex] : -1;
        }

        public int getLastIndex() {
            return this.shuffled.length > 0 ? this.shuffled[this.shuffled.length - 1] : -1;
        }

        public int getFirstIndex() {
            return this.shuffled.length > 0 ? this.shuffled[0] : -1;
        }

        public ShuffleOrder cloneAndInsert(int insertionIndex, int insertionCount) {
            int swapIndex;
            int[] insertionPoints = new int[insertionCount];
            int[] insertionValues = new int[insertionCount];
            int i = 0;
            for (int i2 = 0; i2 < insertionCount; i2++) {
                insertionPoints[i2] = this.random.nextInt(this.shuffled.length + 1);
                swapIndex = this.random.nextInt(i2 + 1);
                insertionValues[i2] = insertionValues[swapIndex];
                insertionValues[swapIndex] = i2 + insertionIndex;
            }
            Arrays.sort(insertionPoints);
            int[] newShuffled = new int[(this.shuffled.length + insertionCount)];
            swapIndex = 0;
            int indexInInsertionList = 0;
            while (i < this.shuffled.length + insertionCount) {
                if (indexInInsertionList >= insertionCount || swapIndex != insertionPoints[indexInInsertionList]) {
                    int indexInOldShuffled = swapIndex + 1;
                    newShuffled[i] = this.shuffled[swapIndex];
                    if (newShuffled[i] >= insertionIndex) {
                        newShuffled[i] = newShuffled[i] + insertionCount;
                    }
                    swapIndex = indexInOldShuffled;
                } else {
                    int indexInInsertionList2 = indexInInsertionList + 1;
                    newShuffled[i] = insertionValues[indexInInsertionList];
                    indexInInsertionList = indexInInsertionList2;
                }
                i++;
            }
            return new DefaultShuffleOrder(newShuffled, new Random(this.random.nextLong()));
        }

        public ShuffleOrder cloneAndRemove(int removalIndex) {
            int i = 0;
            int[] newShuffled = new int[(this.shuffled.length - 1)];
            boolean foundRemovedElement = false;
            while (i < this.shuffled.length) {
                if (this.shuffled[i] == removalIndex) {
                    foundRemovedElement = true;
                } else {
                    newShuffled[foundRemovedElement ? i - 1 : i] = this.shuffled[i] > removalIndex ? this.shuffled[i] - 1 : this.shuffled[i];
                }
                i++;
            }
            return new DefaultShuffleOrder(newShuffled, new Random(this.random.nextLong()));
        }

        private static int[] createShuffledList(int length, Random random) {
            int[] shuffled = new int[length];
            for (int i = 0; i < length; i++) {
                int swapIndex = random.nextInt(i + 1);
                shuffled[i] = shuffled[swapIndex];
                shuffled[swapIndex] = i;
            }
            return shuffled;
        }
    }

    public static final class UnshuffledShuffleOrder implements ShuffleOrder {
        private final int length;

        public UnshuffledShuffleOrder(int length) {
            this.length = length;
        }

        public int getLength() {
            return this.length;
        }

        public int getNextIndex(int index) {
            index++;
            return index < this.length ? index : -1;
        }

        public int getPreviousIndex(int index) {
            index--;
            return index >= 0 ? index : -1;
        }

        public int getLastIndex() {
            return this.length > 0 ? this.length - 1 : -1;
        }

        public int getFirstIndex() {
            return this.length > 0 ? 0 : -1;
        }

        public ShuffleOrder cloneAndInsert(int insertionIndex, int insertionCount) {
            return new UnshuffledShuffleOrder(this.length + insertionCount);
        }

        public ShuffleOrder cloneAndRemove(int removalIndex) {
            return new UnshuffledShuffleOrder(this.length - 1);
        }
    }

    ShuffleOrder cloneAndInsert(int i, int i2);

    ShuffleOrder cloneAndRemove(int i);

    int getFirstIndex();

    int getLastIndex();

    int getLength();

    int getNextIndex(int i);

    int getPreviousIndex(int i);
}
