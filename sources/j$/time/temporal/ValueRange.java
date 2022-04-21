package j$.time.temporal;

import j$.time.DateTimeException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public final class ValueRange implements Serializable {
    private static final long serialVersionUID = -7317881728594519368L;
    private final long maxLargest;
    private final long maxSmallest;
    private final long minLargest;
    private final long minSmallest;

    public static ValueRange of(long min, long max) {
        if (min <= max) {
            return new ValueRange(min, min, max, max);
        }
        throw new IllegalArgumentException("Minimum value must be less than maximum value");
    }

    public static ValueRange of(long min, long maxSmallest2, long maxLargest2) {
        return of(min, min, maxSmallest2, maxLargest2);
    }

    public static ValueRange of(long minSmallest2, long minLargest2, long maxSmallest2, long maxLargest2) {
        if (minSmallest2 > minLargest2) {
            throw new IllegalArgumentException("Smallest minimum value must be less than largest minimum value");
        } else if (maxSmallest2 > maxLargest2) {
            throw new IllegalArgumentException("Smallest maximum value must be less than largest maximum value");
        } else if (minLargest2 <= maxLargest2) {
            return new ValueRange(minSmallest2, minLargest2, maxSmallest2, maxLargest2);
        } else {
            throw new IllegalArgumentException("Minimum value must be less than maximum value");
        }
    }

    private ValueRange(long minSmallest2, long minLargest2, long maxSmallest2, long maxLargest2) {
        this.minSmallest = minSmallest2;
        this.minLargest = minLargest2;
        this.maxSmallest = maxSmallest2;
        this.maxLargest = maxLargest2;
    }

    public boolean isFixed() {
        return this.minSmallest == this.minLargest && this.maxSmallest == this.maxLargest;
    }

    public long getMinimum() {
        return this.minSmallest;
    }

    public long getLargestMinimum() {
        return this.minLargest;
    }

    public long getSmallestMaximum() {
        return this.maxSmallest;
    }

    public long getMaximum() {
        return this.maxLargest;
    }

    public boolean isIntValue() {
        return getMinimum() >= -2147483648L && getMaximum() <= 2147483647L;
    }

    public boolean isValidValue(long value) {
        return value >= getMinimum() && value <= getMaximum();
    }

    public boolean isValidIntValue(long value) {
        return isIntValue() && isValidValue(value);
    }

    public long checkValidValue(long value, TemporalField field) {
        if (isValidValue(value)) {
            return value;
        }
        throw new DateTimeException(genInvalidFieldMessage(field, value));
    }

    public int checkValidIntValue(long value, TemporalField field) {
        if (isValidIntValue(value)) {
            return (int) value;
        }
        throw new DateTimeException(genInvalidFieldMessage(field, value));
    }

    private String genInvalidFieldMessage(TemporalField field, long value) {
        if (field != null) {
            return "Invalid value for " + field + " (valid values " + this + "): " + value;
        }
        return "Invalid value (valid values " + this + "): " + value;
    }

    private void readObject(ObjectInputStream s) {
        s.defaultReadObject();
        long j = this.minSmallest;
        long j2 = this.minLargest;
        if (j <= j2) {
            long j3 = this.maxSmallest;
            long j4 = this.maxLargest;
            if (j3 > j4) {
                throw new InvalidObjectException("Smallest maximum value must be less than largest maximum value");
            } else if (j2 > j4) {
                throw new InvalidObjectException("Minimum value must be less than maximum value");
            }
        } else {
            throw new InvalidObjectException("Smallest minimum value must be less than largest minimum value");
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ValueRange)) {
            return false;
        }
        ValueRange other = (ValueRange) obj;
        if (this.minSmallest == other.minSmallest && this.minLargest == other.minLargest && this.maxSmallest == other.maxSmallest && this.maxLargest == other.maxLargest) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        long j = this.minSmallest;
        long j2 = this.minLargest;
        long j3 = j + (j2 << 16) + (j2 >> 48);
        long j4 = this.maxSmallest;
        long j5 = j3 + (j4 << 32) + (j4 >> 32);
        long j6 = this.maxLargest;
        long hash = j5 + (j6 << 48) + (j6 >> 16);
        return (int) ((hash >>> 32) ^ hash);
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(this.minSmallest);
        if (this.minSmallest != this.minLargest) {
            buf.append('/');
            buf.append(this.minLargest);
        }
        buf.append(" - ");
        buf.append(this.maxSmallest);
        if (this.maxSmallest != this.maxLargest) {
            buf.append('/');
            buf.append(this.maxLargest);
        }
        return buf.toString();
    }
}
