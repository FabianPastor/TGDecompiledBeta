package org.aspectj.runtime.internal;

import org.aspectj.runtime.CFlow;

public class CFlowPlusState extends CFlow {
    private Object[] state;

    public CFlowPlusState(Object[] state2) {
        this.state = state2;
    }

    public CFlowPlusState(Object[] state2, Object _aspect) {
        super(_aspect);
        this.state = state2;
    }

    public Object get(int index) {
        return this.state[index];
    }
}
