package org.webrtc;

import java.util.Map;

public class RTCStats {
    private final String id;
    private final Map<String, Object> members;
    private final long timestampUs;
    private final String type;

    public RTCStats(long timestampUs2, String type2, String id2, Map<String, Object> members2) {
        this.timestampUs = timestampUs2;
        this.type = type2;
        this.id = id2;
        this.members = members2;
    }

    public double getTimestampUs() {
        return (double) this.timestampUs;
    }

    public String getType() {
        return this.type;
    }

    public String getId() {
        return this.id;
    }

    public Map<String, Object> getMembers() {
        return this.members;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{ timestampUs: ");
        builder.append(this.timestampUs);
        builder.append(", type: ");
        builder.append(this.type);
        builder.append(", id: ");
        builder.append(this.id);
        for (Map.Entry<String, Object> entry : this.members.entrySet()) {
            builder.append(", ");
            builder.append(entry.getKey());
            builder.append(": ");
            appendValue(builder, entry.getValue());
        }
        builder.append(" }");
        return builder.toString();
    }

    private static void appendValue(StringBuilder builder, Object value) {
        if (value instanceof Object[]) {
            Object[] arrayValue = (Object[]) value;
            builder.append('[');
            for (int i = 0; i < arrayValue.length; i++) {
                if (i != 0) {
                    builder.append(", ");
                }
                appendValue(builder, arrayValue[i]);
            }
            builder.append(']');
        } else if (value instanceof String) {
            builder.append('\"');
            builder.append(value);
            builder.append('\"');
        } else {
            builder.append(value);
        }
    }

    static RTCStats create(long timestampUs2, String type2, String id2, Map members2) {
        return new RTCStats(timestampUs2, type2, id2, members2);
    }
}
