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

    public MemberRequestsBottomSheet(BaseFragment fragment, long chatId) {
        super(fragment.getParentActivity(), false, fragment.getCurrentAccount(), fragment.getResourceProvider());
        this.needSnapToTop = false;
        this.isEmptyViewVisible = false;
        AnonymousClass1 r4 = new MemberRequestsDelegate(fragment, this.container, chatId, false) {
            /* access modifiers changed from: protected */
            public void onImportersChanged(String query, boolean fromCache, boolean fromHide) {
                if (!hasAllImporters()) {
                    if (MemberRequestsBottomSheet.this.membersEmptyView.getVisibility() != 4) {
                        MemberRequestsBottomSheet.this.membersEmptyView.setVisibility(4);
                    }
                    MemberRequestsBottomSheet.this.dismiss();
                } else if (fromHide) {
                    MemberRequestsBottomSheet.this.searchView.searchEditText.setText("");
                } else {
                    super.onImportersChanged(query, fromCache, fromHide);
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
        this.listView.setAdapter(this.listViewAdapter);
        r4.setRecyclerView(this.listView);
        int position = ((ViewGroup) this.listView.getParent()).indexOfChild(this.listView);
        FlickerLoadingView loadingView = r4.getLoadingView();
        this.currentLoadingView = loadingView;
        this.containerView.addView(loadingView, position, LayoutHelper.createFrame(-1, -1.0f));
        StickerEmptyView emptyView = r4.getEmptyView();
        this.membersEmptyView = emptyView;
        this.containerView.addView(emptyView, position, LayoutHelper.createFrame(-1, -1.0f));
        StickerEmptyView searchEmptyView = r4.getSearchEmptyView();
        this.membersSearchEmptyView = searchEmptyView;
        this.containerView.addView(searchEmptyView, position, LayoutHelper.createFrame(-1, -1.0f));
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
    public boolean isAllowSelectChildAtPosition(float x, float y) {
        return y >= ((float) (this.scrollOffsetY + this.frameLayout.getMeasuredHeight()));
    }

    /* access modifiers changed from: protected */
    public void setTranslationY(int newOffset) {
        super.setTranslationY(newOffset);
        this.currentLoadingView.setTranslationY((float) (this.frameLayout.getMeasuredHeight() + newOffset));
        this.membersEmptyView.setTranslationY((float) newOffset);
        this.membersSearchEmptyView.setTranslationY((float) newOffset);
    }

    /* access modifiers changed from: protected */
    public void updateLayout() {
        int newOffset;
        if (this.listView.getChildCount() <= 0) {
            if (this.listView.getVisibility() == 0) {
                newOffset = this.listView.getPaddingTop() - AndroidUtilities.dp(8.0f);
            } else {
                newOffset = 0;
            }
            if (this.scrollOffsetY != newOffset) {
                this.scrollOffsetY = newOffset;
                setTranslationY(newOffset);
                return;
            }
            return;
        }
        super.updateLayout();
    }

    /* access modifiers changed from: protected */
    public void search(String text) {
        super.search(text);
        this.delegate.setQuery(text);
    }

    /* access modifiers changed from: protected */
    public void onSearchViewTouched(MotionEvent ev, EditTextBoldCursor searchEditText) {
        if (ev.getAction() == 0) {
            this.yOffset = (float) this.scrollOffsetY;
            this.delegate.setAdapterItemsEnabled(false);
        } else if (ev.getAction() == 1 && Math.abs(((float) this.scrollOffsetY) - this.yOffset) < ((float) this.touchSlop) && !this.enterEventSent) {
            Activity activity = AndroidUtilities.findActivity(getContext());
            BaseFragment fragment = null;
            if (activity instanceof LaunchActivity) {
                fragment = ((LaunchActivity) activity).getActionBarLayout().fragmentsStack.get(((LaunchActivity) activity).getActionBarLayout().fragmentsStack.size() - 1);
            }
            if (fragment instanceof ChatActivity) {
                boolean keyboardVisible = ((ChatActivity) fragment).needEnterText();
                this.enterEventSent = true;
                AndroidUtilities.runOnUIThread(new MemberRequestsBottomSheet$$ExternalSyntheticLambda2(this, searchEditText), keyboardVisible ? 200 : 0);
            } else {
                this.enterEventSent = true;
                setFocusable(true);
                searchEditText.requestFocus();
                AndroidUtilities.runOnUIThread(new MemberRequestsBottomSheet$$ExternalSyntheticLambda1(searchEditText));
            }
        }
        if (ev.getAction() == 1 || ev.getAction() == 3) {
            this.delegate.setAdapterItemsEnabled(true);
        }
    }

    /* renamed from: lambda$onSearchViewTouched$1$org-telegram-ui-Components-MemberRequestsBottomSheet  reason: not valid java name */
    public /* synthetic */ void m1112x7052ead7(EditTextBoldCursor searchEditText) {
        setFocusable(true);
        searchEditText.requestFocus();
        AndroidUtilities.runOnUIThread(new MemberRequestsBottomSheet$$ExternalSyntheticLambda0(searchEditText));
    }
}
