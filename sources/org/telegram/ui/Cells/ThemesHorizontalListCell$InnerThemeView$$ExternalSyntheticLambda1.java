package org.telegram.ui.Cells;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.Cells.ThemesHorizontalListCell;

public final /* synthetic */ class ThemesHorizontalListCell$InnerThemeView$$ExternalSyntheticLambda1 implements RequestDelegate {
    public final /* synthetic */ ThemesHorizontalListCell.InnerThemeView f$0;

    public /* synthetic */ ThemesHorizontalListCell$InnerThemeView$$ExternalSyntheticLambda1(ThemesHorizontalListCell.InnerThemeView innerThemeView) {
        this.f$0 = innerThemeView;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$parseTheme$1(tLObject, tLRPC$TL_error);
    }
}