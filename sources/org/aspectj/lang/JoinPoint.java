package org.aspectj.lang;
/* loaded from: classes.dex */
public interface JoinPoint {

    /* loaded from: classes.dex */
    public interface StaticPart {
        String toString();
    }

    Object getTarget();
}
