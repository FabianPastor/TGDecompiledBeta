package org.telegram.ui.Components;

import org.telegram.ui.Components.NumberPicker;

/* renamed from: org.telegram.ui.Components.-$$Lambda$AlertsCreator$ProximitySheetBuilder$Q9C7LuOopxN55xk1n8pVM7ZBncc  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$AlertsCreator$ProximitySheetBuilder$Q9C7LuOopxN55xk1n8pVM7ZBncc implements NumberPicker.Formatter {
    public static final /* synthetic */ $$Lambda$AlertsCreator$ProximitySheetBuilder$Q9C7LuOopxN55xk1n8pVM7ZBncc INSTANCE = new $$Lambda$AlertsCreator$ProximitySheetBuilder$Q9C7LuOopxN55xk1n8pVM7ZBncc();

    private /* synthetic */ $$Lambda$AlertsCreator$ProximitySheetBuilder$Q9C7LuOopxN55xk1n8pVM7ZBncc() {
    }

    public final String format(int i) {
        return String.format("0.%02d", new Object[]{Integer.valueOf(i)});
    }
}
