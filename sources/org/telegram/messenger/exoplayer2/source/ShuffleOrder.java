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
            int i;
            int[] insertionPoints = new int[insertionCount];
            int[] insertionValues = new int[insertionCount];
            for (i = 0; i < insertionCount; i++) {
                insertionPoints[i] = this.random.nextInt(this.shuffled.length + 1);
                int swapIndex = this.random.nextInt(i + 1);
                insertionValues[i] = insertionValues[swapIndex];
                insertionValues[swapIndex] = i + insertionIndex;
            }
            Arrays.sort(insertionPoints);
            int[] newShuffled = new int[(this.shuffled.length + insertionCount)];
            int indexInOldShuffled = 0;
            int indexInInsertionList = 0;
            for (i = 0; i < this.shuffled.length + insertionCount; i++) {
                if (indexInInsertionList >= insertionCount || indexInOldShuffled != insertionPoints[indexInInsertionList]) {
                    int indexInOldShuffled2 = indexInOldShuffled + 1;
                    newShuffled[i] = this.shuffled[indexInOldShuffled];
                    if (newShuffled[i] >= insertionIndex) {
                        newShuffled[i] = newShuffled[i] + insertionCount;
                    }
                    indexInOldShuffled = indexInOldShuffled2;
                } else {
                    int indexInInsertionList2 = indexInInsertionList + 1;
                    newShuffled[i] = insertionValues[indexInInsertionList];
                    indexInInsertionList = indexInInsertionList2;
                }
            }
            return new DefaultShuffleOrder(newShuffled, new Random(this.random.nextLong()));
        }

        public ShuffleOrder cloneAndRemove(int removalIndex) {
            int[] newShuffled = new int[(this.shuffled.length - 1)];
            boolean foundRemovedElement = false;
            int i = 0;
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

        public ShuffleOrder cloneAndClear() {
            return new DefaultShuffleOrder(0, new Random(this.random.nextLong()));
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

        public ShuffleOrder cloneAndClear() {
            return new UnshuffledShuffleOrder(0);
        }
    }

    ShuffleOrder cloneAndClear();

    ShuffleOrder cloneAndInsert(int i, int i2);

    ShuffleOrder cloneAndRemove(int i);

    int getFirstIndex();

    int getLastIndex();

    int getLength();

    int getNextIndex(int i);

    int getPreviousIndex(int i);
}
