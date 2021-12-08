package j$.util.stream;

import j$.util.Map;
import j$.util.Spliterator;
import java.util.EnumMap;
import java.util.Map;

enum StreamOpFlag {
    DISTINCT(0, set(Type.SPLITERATOR).set(Type.STREAM).setAndClear(Type.OP)),
    SORTED(1, set(Type.SPLITERATOR).set(Type.STREAM).setAndClear(Type.OP)),
    ORDERED(2, set(Type.SPLITERATOR).set(Type.STREAM).setAndClear(Type.OP).clear(Type.TERMINAL_OP).clear(Type.UPSTREAM_TERMINAL_OP)),
    SIZED(3, set(Type.SPLITERATOR).set(Type.STREAM).clear(Type.OP)),
    SHORT_CIRCUIT(12, set(Type.OP).set(Type.TERMINAL_OP));
    
    private static final int CLEAR_BITS = 2;
    private static final int FLAG_MASK = 0;
    private static final int FLAG_MASK_IS = 0;
    private static final int FLAG_MASK_NOT = 0;
    static final int INITIAL_OPS_VALUE = 0;
    static final int IS_DISTINCT = 0;
    static final int IS_ORDERED = 0;
    static final int IS_SHORT_CIRCUIT = 0;
    static final int IS_SIZED = 0;
    static final int IS_SORTED = 0;
    static final int NOT_DISTINCT = 0;
    static final int NOT_ORDERED = 0;
    static final int NOT_SIZED = 0;
    static final int NOT_SORTED = 0;
    static final int OP_MASK = 0;
    private static final int PRESERVE_BITS = 3;
    private static final int SET_BITS = 1;
    static final int SPLITERATOR_CHARACTERISTICS_MASK = 0;
    static final int STREAM_MASK = 0;
    static final int TERMINAL_OP_MASK = 0;
    static final int UPSTREAM_TERMINAL_OP_MASK = 0;
    private final int bitPosition;
    private final int clear;
    private final Map<Type, Integer> maskTable;
    private final int preserve;
    private final int set;

    enum Type {
        SPLITERATOR,
        STREAM,
        OP,
        TERMINAL_OP,
        UPSTREAM_TERMINAL_OP
    }

    static {
        StreamOpFlag streamOpFlag;
        StreamOpFlag streamOpFlag2;
        StreamOpFlag streamOpFlag3;
        StreamOpFlag streamOpFlag4;
        StreamOpFlag streamOpFlag5;
        SPLITERATOR_CHARACTERISTICS_MASK = createMask(Type.SPLITERATOR);
        int createMask = createMask(Type.STREAM);
        STREAM_MASK = createMask;
        OP_MASK = createMask(Type.OP);
        TERMINAL_OP_MASK = createMask(Type.TERMINAL_OP);
        UPSTREAM_TERMINAL_OP_MASK = createMask(Type.UPSTREAM_TERMINAL_OP);
        FLAG_MASK = createFlagMask();
        FLAG_MASK_IS = createMask;
        int i = createMask << 1;
        FLAG_MASK_NOT = i;
        INITIAL_OPS_VALUE = createMask | i;
        IS_DISTINCT = streamOpFlag.set;
        NOT_DISTINCT = streamOpFlag.clear;
        IS_SORTED = streamOpFlag2.set;
        NOT_SORTED = streamOpFlag2.clear;
        IS_ORDERED = streamOpFlag3.set;
        NOT_ORDERED = streamOpFlag3.clear;
        IS_SIZED = streamOpFlag4.set;
        NOT_SIZED = streamOpFlag4.clear;
        IS_SHORT_CIRCUIT = streamOpFlag5.set;
    }

    private static MaskBuilder set(Type t) {
        return new MaskBuilder(new EnumMap(Type.class)).set(t);
    }

    private static class MaskBuilder {
        final Map<Type, Integer> map;

        MaskBuilder(Map<Type, Integer> map2) {
            this.map = map2;
        }

        /* access modifiers changed from: package-private */
        public MaskBuilder mask(Type t, Integer i) {
            this.map.put(t, i);
            return this;
        }

        /* access modifiers changed from: package-private */
        public MaskBuilder set(Type t) {
            return mask(t, 1);
        }

        /* access modifiers changed from: package-private */
        public MaskBuilder clear(Type t) {
            return mask(t, 2);
        }

        /* access modifiers changed from: package-private */
        public MaskBuilder setAndClear(Type t) {
            return mask(t, 3);
        }

        /* access modifiers changed from: package-private */
        public Map<Type, Integer> build() {
            for (Type t : Type.values()) {
                Map.EL.putIfAbsent(this.map, t, 0);
            }
            return this.map;
        }
    }

    private StreamOpFlag(int position, MaskBuilder maskBuilder) {
        this.maskTable = maskBuilder.build();
        int position2 = position * 2;
        this.bitPosition = position2;
        this.set = 1 << position2;
        this.clear = 2 << position2;
        this.preserve = 3 << position2;
    }

    /* access modifiers changed from: package-private */
    public int set() {
        return this.set;
    }

    /* access modifiers changed from: package-private */
    public int clear() {
        return this.clear;
    }

    /* access modifiers changed from: package-private */
    public boolean isStreamFlag() {
        return this.maskTable.get(Type.STREAM).intValue() > 0;
    }

    /* access modifiers changed from: package-private */
    public boolean isKnown(int flags) {
        return (this.preserve & flags) == this.set;
    }

    /* access modifiers changed from: package-private */
    public boolean isCleared(int flags) {
        return (this.preserve & flags) == this.clear;
    }

    /* access modifiers changed from: package-private */
    public boolean isPreserved(int flags) {
        int i = this.preserve;
        return (flags & i) == i;
    }

    /* access modifiers changed from: package-private */
    public boolean canSet(Type t) {
        return (this.maskTable.get(t).intValue() & 1) > 0;
    }

    private static int createMask(Type t) {
        int mask = 0;
        for (StreamOpFlag flag : values()) {
            mask |= flag.maskTable.get(t).intValue() << flag.bitPosition;
        }
        return mask;
    }

    private static int createFlagMask() {
        int mask = 0;
        for (StreamOpFlag flag : values()) {
            mask |= flag.preserve;
        }
        return mask;
    }

    private static int getMask(int flags) {
        if (flags == 0) {
            return FLAG_MASK;
        }
        return ((((FLAG_MASK_IS & flags) << 1) | flags) | ((FLAG_MASK_NOT & flags) >> 1)) ^ -1;
    }

    static int combineOpFlags(int newStreamOrOpFlags, int prevCombOpFlags) {
        return (getMask(newStreamOrOpFlags) & prevCombOpFlags) | newStreamOrOpFlags;
    }

    static int toStreamFlags(int combOpFlags) {
        return ((combOpFlags ^ -1) >> 1) & FLAG_MASK_IS & combOpFlags;
    }

    static int toCharacteristics(int streamFlags) {
        return SPLITERATOR_CHARACTERISTICS_MASK & streamFlags;
    }

    static int fromCharacteristics(Spliterator<?> spliterator) {
        int characteristics = spliterator.characteristics();
        if ((characteristics & 4) == 0 || spliterator.getComparator() == null) {
            return SPLITERATOR_CHARACTERISTICS_MASK & characteristics;
        }
        return SPLITERATOR_CHARACTERISTICS_MASK & characteristics & -5;
    }

    static int fromCharacteristics(int characteristics) {
        return SPLITERATOR_CHARACTERISTICS_MASK & characteristics;
    }
}
