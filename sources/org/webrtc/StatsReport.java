package org.webrtc;

public class StatsReport {
    public final String id;
    public final double timestamp;
    public final String type;
    public final Value[] values;

    public static class Value {
        public final String name;
        public final String value;

        public Value(String name2, String value2) {
            this.name = name2;
            this.value = value2;
        }

        public String toString() {
            return "[" + this.name + ": " + this.value + "]";
        }
    }

    public StatsReport(String id2, String type2, double timestamp2, Value[] values2) {
        this.id = id2;
        this.type = type2;
        this.timestamp = timestamp2;
        this.values = values2;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("id: ");
        builder.append(this.id);
        builder.append(", type: ");
        builder.append(this.type);
        builder.append(", timestamp: ");
        builder.append(this.timestamp);
        builder.append(", values: ");
        int i = 0;
        while (true) {
            Value[] valueArr = this.values;
            if (i >= valueArr.length) {
                return builder.toString();
            }
            builder.append(valueArr[i].toString());
            builder.append(", ");
            i++;
        }
    }
}
