package org.telegram.ui.Components.voip;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.ui.GroupCallActivity;

public class GroupCallGridCell extends FrameLayout {
    public boolean attached;
    ChatObject.VideoParticipant participant;
    GroupCallMiniTextureView renderer;
    public int spanCount;

    public GroupCallGridCell(Context context) {
        super(context);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int i3;
        float f = GroupCallActivity.isLandscapeMode ? 3.0f : 2.0f;
        if (getParent() != null) {
            i3 = ((View) getParent()).getMeasuredWidth();
        } else {
            i3 = View.MeasureSpec.getSize(i);
        }
        super.onMeasure(i, View.MeasureSpec.makeMeasureSpec((int) ((((float) i3) / f) + ((float) AndroidUtilities.dp(4.0f))), NUM));
    }

    public void setData(AccountInstance accountInstance, ChatObject.VideoParticipant videoParticipant, ChatObject.Call call, int i) {
        this.participant = videoParticipant;
    }

    public ChatObject.VideoParticipant getParticipant() {
        return this.participant;
    }

    public void setRenderer(GroupCallMiniTextureView groupCallMiniTextureView) {
        this.renderer = groupCallMiniTextureView;
    }

    public GroupCallMiniTextureView getRenderer() {
        return this.renderer;
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.attached = true;
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.attached = false;
    }
}
