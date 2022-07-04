package j$.time.temporal;

public interface TemporalQuery<R> {
    R queryFrom(TemporalAccessor temporalAccessor);
}
