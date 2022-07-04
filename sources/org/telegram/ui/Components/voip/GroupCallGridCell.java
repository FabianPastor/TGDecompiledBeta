package org.telegram.ui.Components.voip;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.ui.GroupCallActivity;
import org.telegram.ui.GroupCallTabletGridAdapter;

public class GroupCallGridCell extends FrameLayout {
    public static final int CELL_HEIGHT = 165;
    public boolean attached;
    public GroupCallTabletGridAdapter gridAdapter;
    private final boolean isTabletGrid;
    ChatObject.VideoParticipant participant;
    public int position;
    GroupCallMiniTextureView renderer;
    public int spanCount;

    public GroupCallGridCell(Context context, boolean isTabletGrid2) {
        super(context);
        this.isTabletGrid = isTabletGrid2;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        float parentWidth;
        float h;
        if (this.isTabletGrid) {
            float measuredWidth = (((float) ((View) getParent()).getMeasuredWidth()) / 6.0f) * ((float) this.spanCount);
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(this.gridAdapter.getItemHeight(this.position), NUM));
            return;
        }
        float spanCount2 = GroupCallActivity.isLandscapeMode ? 3.0f : 2.0f;
        if (getParent() != null) {
            parentWidth = (float) ((View) getParent()).getMeasuredWidth();
        } else {
            parentWidth = (float) View.MeasureSpec.getSize(widthMeasureSpec);
        }
        if (GroupCallActivity.isTabletMode) {
            h = parentWidth / 2.0f;
        } else {
            h = parentWidth / spanCount2;
        }
        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec((int) (((float) AndroidUtilities.dp(4.0f)) + h), NUM));
    }

    public void setData(AccountInstance accountInstance, ChatObject.VideoParticipant participant2, ChatObject.Call call, long selfPeerId) {
        this.participant = participant2;
    }

    public ChatObject.VideoParticipant getParticipant() {
        return this.participant;
    }

    public void setRenderer(GroupCallMiniTextureView renderer2) {
        this.renderer = renderer2;
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

    public float getItemHeight() {
        GroupCallTabletGridAdapter groupCallTabletGridAdapter = this.gridAdapter;
        if (groupCallTabletGridAdapter != null) {
            return (float) groupCallTabletGridAdapter.getItemHeight(this.position);
        }
        return (float) getMeasuredHeight();
    }
}
