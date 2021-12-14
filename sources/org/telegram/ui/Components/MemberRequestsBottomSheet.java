package org.telegram.ui.Components;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Delegates.MemberRequestsDelegate;
import org.telegram.ui.LaunchActivity;

public class MemberRequestsBottomSheet extends UsersAlertBase {
    private final FlickerLoadingView currentLoadingView;
    private final MemberRequestsDelegate delegate;
    private boolean enterEventSent;
    /* access modifiers changed from: private */
    public final StickerEmptyView membersEmptyView;
    private final StickerEmptyView membersSearchEmptyView;
    private final int touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    private float yOffset;

    public MemberRequestsBottomSheet(BaseFragment baseFragment, long j) {
        super(baseFragment.getParentActivity(), false, baseFragment.getCurrentAccount(), baseFragment.getResourceProvider());
        this.needSnapToTop = false;
        this.isEmptyViewVisible = false;
        AnonymousClass1 r4 = new MemberRequestsDelegate(baseFragment, this.container, j, false) {
            /* access modifiers changed from: protected */
            public void onImportersChanged(String str, boolean z, boolean z2) {
                if (!hasAllImporters()) {
                    if (MemberRequestsBottomSheet.this.membersEmptyView.getVisibility() != 4) {
                        MemberRequestsBottomSheet.this.membersEmptyView.setVisibility(4);
                    }
                    MemberRequestsBottomSheet.this.dismiss();
                } else if (z2) {
                    MemberRequestsBottomSheet.this.searchView.searchEditText.setText("");
                } else {
                    super.onImportersChanged(str, z, z2);
                }
            }
        };
        this.delegate = r4;
        r4.setShowLastItemDivider(false);
        setDimBehindAlpha(75);
        this.searchView.searchEditText.setHint(LocaleController.getString("SearchMemberRequests", NUM));
        MemberRequestsDelegate.Adapter adapter = r4.getAdapter();
        this.listViewAdapter = adapter;
        this.searchListViewAdapter = adapter;
        this.listView.setAdapter(adapter);
        r4.setRecyclerView(this.listView);
        int indexOfChild = ((ViewGroup) this.listView.getParent()).indexOfChild(this.listView);
        FlickerLoadingView loadingView = r4.getLoadingView();
        this.currentLoadingView = loadingView;
        this.containerView.addView(loadingView, indexOfChild, LayoutHelper.createFrame(-1, -1.0f));
        StickerEmptyView emptyView = r4.getEmptyView();
        this.membersEmptyView = emptyView;
        this.containerView.addView(emptyView, indexOfChild, LayoutHelper.createFrame(-1, -1.0f));
        StickerEmptyView searchEmptyView = r4.getSearchEmptyView();
        this.membersSearchEmptyView = searchEmptyView;
        this.containerView.addView(searchEmptyView, indexOfChild, LayoutHelper.createFrame(-1, -1.0f));
        r4.loadMembers();
    }

    public void show() {
        if (this.delegate.isNeedRestoreList && this.scrollOffsetY == 0) {
            this.scrollOffsetY = AndroidUtilities.dp(8.0f);
        }
        super.show();
        this.delegate.isNeedRestoreList = false;
    }

    public void onBackPressed() {
        if (this.delegate.onBackPressed()) {
            super.onBackPressed();
        }
    }

    public boolean isNeedRestoreDialog() {
        return this.delegate.isNeedRestoreList;
    }

    /* access modifiers changed from: protected */
    public boolean isAllowSelectChildAtPosition(float f, float f2) {
        return f2 >= ((float) (this.scrollOffsetY + this.frameLayout.getMeasuredHeight()));
    }

    /* access modifiers changed from: protected */
    public void setTranslationY(int i) {
        super.setTranslationY(i);
        this.currentLoadingView.setTranslationY((float) (this.frameLayout.getMeasuredHeight() + i));
        float f = (float) i;
        this.membersEmptyView.setTranslationY(f);
        this.membersSearchEmptyView.setTranslationY(f);
    }

    /* access modifiers changed from: protected */
    public void updateLayout() {
        if (this.listView.getChildCount() <= 0) {
            int paddingTop = this.listView.getVisibility() == 0 ? this.listView.getPaddingTop() - AndroidUtilities.dp(8.0f) : 0;
            if (this.scrollOffsetY != paddingTop) {
                this.scrollOffsetY = paddingTop;
                setTranslationY(paddingTop);
                return;
            }
            return;
        }
        super.updateLayout();
    }

    /* access modifiers changed from: protected */
    public void search(String str) {
        super.search(str);
        this.delegate.setQuery(str);
    }

    /* access modifiers changed from: protected */
    public void onSearchViewTouched(MotionEvent motionEvent, EditTextBoldCursor editTextBoldCursor) {
        if (motionEvent.getAction() == 0) {
            this.yOffset = (float) this.scrollOffsetY;
            this.delegate.setAdapterItemsEnabled(false);
        } else if (motionEvent.getAction() == 1 && Math.abs(((float) this.scrollOffsetY) - this.yOffset) < ((float) this.touchSlop) && !this.enterEventSent) {
            Activity findActivity = AndroidUtilities.findActivity(getContext());
            BaseFragment baseFragment = null;
            if (findActivity instanceof LaunchActivity) {
                LaunchActivity launchActivity = (LaunchActivity) findActivity;
                baseFragment = launchActivity.getActionBarLayout().fragmentsStack.get(launchActivity.getActionBarLayout().fragmentsStack.size() - 1);
            }
            if (baseFragment instanceof ChatActivity) {
                boolean needEnterText = ((ChatActivity) baseFragment).needEnterText();
                this.enterEventSent = true;
                AndroidUtilities.runOnUIThread(new MemberRequestsBottomSheet$$ExternalSyntheticLambda2(this, editTextBoldCursor), needEnterText ? 200 : 0);
            } else {
                this.enterEventSent = true;
                setFocusable(true);
                editTextBoldCursor.requestFocus();
                AndroidUtilities.runOnUIThread(new MemberRequestsBottomSheet$$ExternalSyntheticLambda0(editTextBoldCursor));
            }
        }
        if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
            this.delegate.setAdapterItemsEnabled(true);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onSearchViewTouched$1(EditTextBoldCursor editTextBoldCursor) {
        setFocusable(true);
        editTextBoldCursor.requestFocus();
        AndroidUtilities.runOnUIThread(new MemberRequestsBottomSheet$$ExternalSyntheticLambda1(editTextBoldCursor));
    }
}
