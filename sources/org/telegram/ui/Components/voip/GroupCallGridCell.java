package org.telegram.ui.Components.voip;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.ui.GroupCallActivity;
import org.telegram.ui.GroupCallTabletGridAdapter;
/* loaded from: classes3.dex */
public class GroupCallGridCell extends FrameLayout {
    public boolean attached;
    public GroupCallTabletGridAdapter gridAdapter;
    private final boolean isTabletGrid;
    ChatObject.VideoParticipant participant;
    public int position;
    GroupCallMiniTextureView renderer;
    public int spanCount;

    public GroupCallGridCell(Context context, boolean z) {
        super(context);
        this.isTabletGrid = z;
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        int size;
        if (this.isTabletGrid) {
            ((View) getParent()).getMeasuredWidth();
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(this.gridAdapter.getItemHeight(this.position), NUM));
            return;
        }
        float f = GroupCallActivity.isLandscapeMode ? 3.0f : 2.0f;
        if (getParent() != null) {
            size = ((View) getParent()).getMeasuredWidth();
        } else {
            size = View.MeasureSpec.getSize(i);
        }
        float f2 = size;
        super.onMeasure(i, View.MeasureSpec.makeMeasureSpec((int) ((GroupCallActivity.isTabletMode ? f2 / 2.0f : f2 / f) + AndroidUtilities.dp(4.0f)), NUM));
    }

    public void setData(AccountInstance accountInstance, ChatObject.VideoParticipant videoParticipant, ChatObject.Call call, long j) {
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

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.attached = true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.attached = false;
    }

    public float getItemHeight() {
        int measuredHeight;
        GroupCallTabletGridAdapter groupCallTabletGridAdapter = this.gridAdapter;
        if (groupCallTabletGridAdapter != null) {
            measuredHeight = groupCallTabletGridAdapter.getItemHeight(this.position);
        } else {
            measuredHeight = getMeasuredHeight();
        }
        return measuredHeight;
    }
}
