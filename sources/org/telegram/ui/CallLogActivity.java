package org.telegram.ui;

import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.ImageSpan;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$EncryptedChat;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$MessageAction;
import org.telegram.tgnet.TLRPC$PhoneCallDiscardReason;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_inputMessagesFilterPhoneCalls;
import org.telegram.tgnet.TLRPC$TL_inputPeerEmpty;
import org.telegram.tgnet.TLRPC$TL_messageActionHistoryClear;
import org.telegram.tgnet.TLRPC$TL_messageActionPhoneCall;
import org.telegram.tgnet.TLRPC$TL_messages_search;
import org.telegram.tgnet.TLRPC$TL_phoneCallDiscardReasonBusy;
import org.telegram.tgnet.TLRPC$TL_phoneCallDiscardReasonMissed;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$UserFull;
import org.telegram.tgnet.TLRPC$messages_Messages;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.CallLogActivity;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.LocationCell;
import org.telegram.ui.Cells.ProfileSearchCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.voip.VoIPHelper;
import org.telegram.ui.ContactsActivity;

public class CallLogActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public View.OnClickListener callBtnClickListener = new View.OnClickListener() {
        public void onClick(View view) {
            CallLogActivity callLogActivity = CallLogActivity.this;
            TLRPC$User tLRPC$User = ((CallLogRow) view.getTag()).user;
            TLRPC$User unused = callLogActivity.lastCallUser = tLRPC$User;
            VoIPHelper.startCall(tLRPC$User, CallLogActivity.this.getParentActivity(), (TLRPC$UserFull) null);
        }
    };
    /* access modifiers changed from: private */
    public ArrayList<CallLogRow> calls = new ArrayList<>();
    private EmptyTextProgressView emptyView;
    /* access modifiers changed from: private */
    public boolean endReached;
    private boolean firstLoaded;
    /* access modifiers changed from: private */
    public ImageView floatingButton;
    private boolean floatingHidden;
    private final AccelerateDecelerateInterpolator floatingInterpolator = new AccelerateDecelerateInterpolator();
    private Drawable greenDrawable;
    private Drawable greenDrawable2;
    /* access modifiers changed from: private */
    public ImageSpan iconIn;
    /* access modifiers changed from: private */
    public ImageSpan iconMissed;
    /* access modifiers changed from: private */
    public ImageSpan iconOut;
    /* access modifiers changed from: private */
    public TLRPC$User lastCallUser;
    /* access modifiers changed from: private */
    public LinearLayoutManager layoutManager;
    private RecyclerListView listView;
    /* access modifiers changed from: private */
    public ListAdapter listViewAdapter;
    /* access modifiers changed from: private */
    public boolean loading;
    /* access modifiers changed from: private */
    public int prevPosition;
    /* access modifiers changed from: private */
    public int prevTop;
    private Drawable redDrawable;
    /* access modifiers changed from: private */
    public boolean scrollUpdated;

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        ListAdapter listAdapter;
        boolean z = false;
        if (i != NotificationCenter.didReceiveNewMessages || !this.firstLoaded) {
            if (i == NotificationCenter.messagesDeleted && this.firstLoaded && !objArr[2].booleanValue()) {
                ArrayList arrayList = objArr[0];
                Iterator<CallLogRow> it = this.calls.iterator();
                while (it.hasNext()) {
                    CallLogRow next = it.next();
                    Iterator<TLRPC$Message> it2 = next.calls.iterator();
                    while (it2.hasNext()) {
                        if (arrayList.contains(Integer.valueOf(it2.next().id))) {
                            it2.remove();
                            z = true;
                        }
                    }
                    if (next.calls.size() == 0) {
                        it.remove();
                    }
                }
                if (z && (listAdapter = this.listViewAdapter) != null) {
                    listAdapter.notifyDataSetChanged();
                }
            }
        } else if (!objArr[2].booleanValue()) {
            Iterator it3 = objArr[1].iterator();
            while (it3.hasNext()) {
                MessageObject messageObject = (MessageObject) it3.next();
                TLRPC$Message tLRPC$Message = messageObject.messageOwner;
                if (tLRPC$Message.action instanceof TLRPC$TL_messageActionPhoneCall) {
                    int i3 = tLRPC$Message.from_id == UserConfig.getInstance(this.currentAccount).getClientUserId() ? messageObject.messageOwner.to_id.user_id : messageObject.messageOwner.from_id;
                    int i4 = messageObject.messageOwner.from_id == UserConfig.getInstance(this.currentAccount).getClientUserId() ? 0 : 1;
                    TLRPC$PhoneCallDiscardReason tLRPC$PhoneCallDiscardReason = messageObject.messageOwner.action.reason;
                    if (i4 == 1 && ((tLRPC$PhoneCallDiscardReason instanceof TLRPC$TL_phoneCallDiscardReasonMissed) || (tLRPC$PhoneCallDiscardReason instanceof TLRPC$TL_phoneCallDiscardReasonBusy))) {
                        i4 = 2;
                    }
                    if (this.calls.size() > 0) {
                        CallLogRow callLogRow = this.calls.get(0);
                        if (callLogRow.user.id == i3 && callLogRow.type == i4) {
                            callLogRow.calls.add(0, messageObject.messageOwner);
                            this.listViewAdapter.notifyItemChanged(0);
                        }
                    }
                    CallLogRow callLogRow2 = new CallLogRow();
                    ArrayList arrayList2 = new ArrayList();
                    callLogRow2.calls = arrayList2;
                    arrayList2.add(messageObject.messageOwner);
                    callLogRow2.user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(i3));
                    callLogRow2.type = i4;
                    this.calls.add(0, callLogRow2);
                    this.listViewAdapter.notifyItemInserted(0);
                }
            }
        }
    }

    private class CustomCell extends FrameLayout {
        /* access modifiers changed from: private */
        public ImageView imageView;
        /* access modifiers changed from: private */
        public ProfileSearchCell profileSearchCell;

        public CustomCell(CallLogActivity callLogActivity, Context context) {
            super(context);
            setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            ProfileSearchCell profileSearchCell2 = new ProfileSearchCell(context);
            this.profileSearchCell = profileSearchCell2;
            profileSearchCell2.setPadding(LocaleController.isRTL ? AndroidUtilities.dp(32.0f) : 0, 0, LocaleController.isRTL ? 0 : AndroidUtilities.dp(32.0f), 0);
            this.profileSearchCell.setSublabelOffset(AndroidUtilities.dp(LocaleController.isRTL ? 2.0f : -2.0f), -AndroidUtilities.dp(4.0f));
            addView(this.profileSearchCell, LayoutHelper.createFrame(-1, -1.0f));
            ImageView imageView2 = new ImageView(context);
            this.imageView = imageView2;
            imageView2.setImageResource(NUM);
            this.imageView.setAlpha(214);
            this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("featuredStickers_addButton"), PorterDuff.Mode.MULTIPLY));
            this.imageView.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("listSelectorSDK21"), 1));
            this.imageView.setScaleType(ImageView.ScaleType.CENTER);
            this.imageView.setOnClickListener(callLogActivity.callBtnClickListener);
            this.imageView.setContentDescription(LocaleController.getString("Call", NUM));
            addView(this.imageView, LayoutHelper.createFrame(48, 48.0f, (LocaleController.isRTL ? 3 : 5) | 16, 8.0f, 0.0f, 8.0f, 0.0f));
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        getCalls(0, 50);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.didReceiveNewMessages);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagesDeleted);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didReceiveNewMessages);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagesDeleted);
    }

    public View createView(Context context) {
        Drawable mutate = getParentActivity().getResources().getDrawable(NUM).mutate();
        this.greenDrawable = mutate;
        mutate.setBounds(0, 0, mutate.getIntrinsicWidth(), this.greenDrawable.getIntrinsicHeight());
        this.greenDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("calls_callReceivedGreenIcon"), PorterDuff.Mode.MULTIPLY));
        this.iconOut = new ImageSpan(this.greenDrawable, 0);
        Drawable mutate2 = getParentActivity().getResources().getDrawable(NUM).mutate();
        this.greenDrawable2 = mutate2;
        mutate2.setBounds(0, 0, mutate2.getIntrinsicWidth(), this.greenDrawable2.getIntrinsicHeight());
        this.greenDrawable2.setColorFilter(new PorterDuffColorFilter(Theme.getColor("calls_callReceivedGreenIcon"), PorterDuff.Mode.MULTIPLY));
        this.iconIn = new ImageSpan(this.greenDrawable2, 0);
        Drawable mutate3 = getParentActivity().getResources().getDrawable(NUM).mutate();
        this.redDrawable = mutate3;
        mutate3.setBounds(0, 0, mutate3.getIntrinsicWidth(), this.redDrawable.getIntrinsicHeight());
        this.redDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("calls_callReceivedRedIcon"), PorterDuff.Mode.MULTIPLY));
        this.iconMissed = new ImageSpan(this.redDrawable, 0);
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("Calls", NUM));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    CallLogActivity.this.finishFragment();
                }
            }
        });
        FrameLayout frameLayout = new FrameLayout(context);
        this.fragmentView = frameLayout;
        frameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        FrameLayout frameLayout2 = (FrameLayout) this.fragmentView;
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context);
        this.emptyView = emptyTextProgressView;
        emptyTextProgressView.setText(LocaleController.getString("NoCallLog", NUM));
        frameLayout2.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setEmptyView(this.emptyView);
        RecyclerListView recyclerListView2 = this.listView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView2.setLayoutManager(linearLayoutManager);
        RecyclerListView recyclerListView3 = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.listViewAdapter = listAdapter;
        recyclerListView3.setAdapter(listAdapter);
        this.listView.setVerticalScrollbarPosition(LocaleController.isRTL ? 1 : 2);
        frameLayout2.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                CallLogActivity.this.lambda$createView$0$CallLogActivity(view, i);
            }
        });
        this.listView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new RecyclerListView.OnItemLongClickListener() {
            public final boolean onItemClick(View view, int i) {
                return CallLogActivity.this.lambda$createView$2$CallLogActivity(view, i);
            }
        });
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            /* JADX WARNING: Code restructure failed: missing block: B:27:0x00a5, code lost:
                if (java.lang.Math.abs(r1) > 1) goto L_0x00b2;
             */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void onScrolled(androidx.recyclerview.widget.RecyclerView r5, int r6, int r7) {
                /*
                    r4 = this;
                    org.telegram.ui.CallLogActivity r6 = org.telegram.ui.CallLogActivity.this
                    androidx.recyclerview.widget.LinearLayoutManager r6 = r6.layoutManager
                    int r6 = r6.findFirstVisibleItemPosition()
                    r7 = 0
                    r0 = 1
                    r1 = -1
                    if (r6 != r1) goto L_0x0011
                    r1 = 0
                    goto L_0x0021
                L_0x0011:
                    org.telegram.ui.CallLogActivity r1 = org.telegram.ui.CallLogActivity.this
                    androidx.recyclerview.widget.LinearLayoutManager r1 = r1.layoutManager
                    int r1 = r1.findLastVisibleItemPosition()
                    int r1 = r1 - r6
                    int r1 = java.lang.Math.abs(r1)
                    int r1 = r1 + r0
                L_0x0021:
                    if (r1 <= 0) goto L_0x006d
                    org.telegram.ui.CallLogActivity r2 = org.telegram.ui.CallLogActivity.this
                    org.telegram.ui.CallLogActivity$ListAdapter r2 = r2.listViewAdapter
                    int r2 = r2.getItemCount()
                    org.telegram.ui.CallLogActivity r3 = org.telegram.ui.CallLogActivity.this
                    boolean r3 = r3.endReached
                    if (r3 != 0) goto L_0x006d
                    org.telegram.ui.CallLogActivity r3 = org.telegram.ui.CallLogActivity.this
                    boolean r3 = r3.loading
                    if (r3 != 0) goto L_0x006d
                    org.telegram.ui.CallLogActivity r3 = org.telegram.ui.CallLogActivity.this
                    java.util.ArrayList r3 = r3.calls
                    boolean r3 = r3.isEmpty()
                    if (r3 != 0) goto L_0x006d
                    int r1 = r1 + r6
                    int r2 = r2 + -5
                    if (r1 < r2) goto L_0x006d
                    org.telegram.ui.CallLogActivity r1 = org.telegram.ui.CallLogActivity.this
                    java.util.ArrayList r1 = r1.calls
                    org.telegram.ui.CallLogActivity r2 = org.telegram.ui.CallLogActivity.this
                    java.util.ArrayList r2 = r2.calls
                    int r2 = r2.size()
                    int r2 = r2 - r0
                    java.lang.Object r1 = r1.get(r2)
                    org.telegram.ui.CallLogActivity$CallLogRow r1 = (org.telegram.ui.CallLogActivity.CallLogRow) r1
                    org.telegram.ui.-$$Lambda$CallLogActivity$3$1DV60DlgjFkI_3XLU_3k-ebYIpc r2 = new org.telegram.ui.-$$Lambda$CallLogActivity$3$1DV60DlgjFkI_3XLU_3k-ebYIpc
                    r2.<init>(r1)
                    org.telegram.messenger.AndroidUtilities.runOnUIThread(r2)
                L_0x006d:
                    org.telegram.ui.CallLogActivity r1 = org.telegram.ui.CallLogActivity.this
                    android.widget.ImageView r1 = r1.floatingButton
                    int r1 = r1.getVisibility()
                    r2 = 8
                    if (r1 == r2) goto L_0x00d1
                    android.view.View r5 = r5.getChildAt(r7)
                    if (r5 == 0) goto L_0x0086
                    int r5 = r5.getTop()
                    goto L_0x0087
                L_0x0086:
                    r5 = 0
                L_0x0087:
                    org.telegram.ui.CallLogActivity r1 = org.telegram.ui.CallLogActivity.this
                    int r1 = r1.prevPosition
                    if (r1 != r6) goto L_0x00a8
                    org.telegram.ui.CallLogActivity r1 = org.telegram.ui.CallLogActivity.this
                    int r1 = r1.prevTop
                    int r1 = r1 - r5
                    org.telegram.ui.CallLogActivity r2 = org.telegram.ui.CallLogActivity.this
                    int r2 = r2.prevTop
                    if (r5 >= r2) goto L_0x00a0
                    r2 = 1
                    goto L_0x00a1
                L_0x00a0:
                    r2 = 0
                L_0x00a1:
                    int r1 = java.lang.Math.abs(r1)
                    if (r1 <= r0) goto L_0x00b3
                    goto L_0x00b2
                L_0x00a8:
                    org.telegram.ui.CallLogActivity r1 = org.telegram.ui.CallLogActivity.this
                    int r1 = r1.prevPosition
                    if (r6 <= r1) goto L_0x00b1
                    r7 = 1
                L_0x00b1:
                    r2 = r7
                L_0x00b2:
                    r7 = 1
                L_0x00b3:
                    if (r7 == 0) goto L_0x00c2
                    org.telegram.ui.CallLogActivity r7 = org.telegram.ui.CallLogActivity.this
                    boolean r7 = r7.scrollUpdated
                    if (r7 == 0) goto L_0x00c2
                    org.telegram.ui.CallLogActivity r7 = org.telegram.ui.CallLogActivity.this
                    r7.hideFloatingButton(r2)
                L_0x00c2:
                    org.telegram.ui.CallLogActivity r7 = org.telegram.ui.CallLogActivity.this
                    int unused = r7.prevPosition = r6
                    org.telegram.ui.CallLogActivity r6 = org.telegram.ui.CallLogActivity.this
                    int unused = r6.prevTop = r5
                    org.telegram.ui.CallLogActivity r5 = org.telegram.ui.CallLogActivity.this
                    boolean unused = r5.scrollUpdated = r0
                L_0x00d1:
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.CallLogActivity.AnonymousClass3.onScrolled(androidx.recyclerview.widget.RecyclerView, int, int):void");
            }

            public /* synthetic */ void lambda$onScrolled$0$CallLogActivity$3(CallLogRow callLogRow) {
                CallLogActivity callLogActivity = CallLogActivity.this;
                List<TLRPC$Message> list = callLogRow.calls;
                callLogActivity.getCalls(list.get(list.size() - 1).id, 100);
            }
        });
        if (this.loading) {
            this.emptyView.showProgress();
        } else {
            this.emptyView.showTextView();
        }
        ImageView imageView = new ImageView(context);
        this.floatingButton = imageView;
        imageView.setVisibility(0);
        this.floatingButton.setScaleType(ImageView.ScaleType.CENTER);
        Drawable createSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor("chats_actionBackground"), Theme.getColor("chats_actionPressedBackground"));
        if (Build.VERSION.SDK_INT < 21) {
            Drawable mutate4 = context.getResources().getDrawable(NUM).mutate();
            mutate4.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable = new CombinedDrawable(mutate4, createSimpleSelectorCircleDrawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            createSimpleSelectorCircleDrawable = combinedDrawable;
        }
        this.floatingButton.setBackgroundDrawable(createSimpleSelectorCircleDrawable);
        this.floatingButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chats_actionIcon"), PorterDuff.Mode.MULTIPLY));
        this.floatingButton.setImageResource(NUM);
        this.floatingButton.setContentDescription(LocaleController.getString("Call", NUM));
        if (Build.VERSION.SDK_INT >= 21) {
            StateListAnimator stateListAnimator = new StateListAnimator();
            stateListAnimator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.floatingButton, "translationZ", new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            stateListAnimator.addState(new int[0], ObjectAnimator.ofFloat(this.floatingButton, "translationZ", new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
            this.floatingButton.setStateListAnimator(stateListAnimator);
            this.floatingButton.setOutlineProvider(new ViewOutlineProvider(this) {
                @SuppressLint({"NewApi"})
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                }
            });
        }
        frameLayout2.addView(this.floatingButton, LayoutHelper.createFrame(Build.VERSION.SDK_INT >= 21 ? 56 : 60, Build.VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, (LocaleController.isRTL ? 3 : 5) | 80, LocaleController.isRTL ? 14.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 14.0f, 14.0f));
        this.floatingButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                CallLogActivity.this.lambda$createView$4$CallLogActivity(view);
            }
        });
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$0$CallLogActivity(View view, int i) {
        if (i >= 0 && i < this.calls.size()) {
            CallLogRow callLogRow = this.calls.get(i);
            Bundle bundle = new Bundle();
            bundle.putInt("user_id", callLogRow.user.id);
            bundle.putInt("message_id", callLogRow.calls.get(0).id);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
            presentFragment(new ChatActivity(bundle), true);
        }
    }

    public /* synthetic */ boolean lambda$createView$2$CallLogActivity(View view, int i) {
        if (i < 0 || i >= this.calls.size()) {
            return false;
        }
        CallLogRow callLogRow = this.calls.get(i);
        ArrayList arrayList = new ArrayList();
        arrayList.add(LocaleController.getString("Delete", NUM));
        if (VoIPHelper.canRateCall((TLRPC$TL_messageActionPhoneCall) callLogRow.calls.get(0).action)) {
            arrayList.add(LocaleController.getString("CallMessageReportProblem", NUM));
        }
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setTitle(LocaleController.getString("Calls", NUM));
        builder.setItems((CharSequence[]) arrayList.toArray(new String[0]), new DialogInterface.OnClickListener(callLogRow) {
            private final /* synthetic */ CallLogActivity.CallLogRow f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                CallLogActivity.this.lambda$null$1$CallLogActivity(this.f$1, dialogInterface, i);
            }
        });
        builder.show();
        return true;
    }

    public /* synthetic */ void lambda$null$1$CallLogActivity(CallLogRow callLogRow, DialogInterface dialogInterface, int i) {
        if (i == 0) {
            confirmAndDelete(callLogRow);
        } else if (i == 1) {
            VoIPHelper.showRateAlert(getParentActivity(), (TLRPC$TL_messageActionPhoneCall) callLogRow.calls.get(0).action);
        }
    }

    public /* synthetic */ void lambda$createView$4$CallLogActivity(View view) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("destroyAfterSelect", true);
        bundle.putBoolean("returnAsResult", true);
        bundle.putBoolean("onlyUsers", true);
        bundle.putBoolean("allowSelf", false);
        ContactsActivity contactsActivity = new ContactsActivity(bundle);
        contactsActivity.setDelegate(new ContactsActivity.ContactsActivityDelegate() {
            public final void didSelectContact(TLRPC$User tLRPC$User, String str, ContactsActivity contactsActivity) {
                CallLogActivity.this.lambda$null$3$CallLogActivity(tLRPC$User, str, contactsActivity);
            }
        });
        presentFragment(contactsActivity);
    }

    public /* synthetic */ void lambda$null$3$CallLogActivity(TLRPC$User tLRPC$User, String str, ContactsActivity contactsActivity) {
        VoIPHelper.startCall(tLRPC$User, getParentActivity(), (TLRPC$UserFull) null);
    }

    /* access modifiers changed from: private */
    public void hideFloatingButton(boolean z) {
        if (this.floatingHidden != z) {
            this.floatingHidden = z;
            ImageView imageView = this.floatingButton;
            float[] fArr = new float[1];
            fArr[0] = z ? (float) AndroidUtilities.dp(100.0f) : 0.0f;
            ObjectAnimator duration = ObjectAnimator.ofFloat(imageView, "translationY", fArr).setDuration(300);
            duration.setInterpolator(this.floatingInterpolator);
            this.floatingButton.setClickable(!z);
            duration.start();
        }
    }

    /* access modifiers changed from: private */
    public void getCalls(int i, int i2) {
        if (!this.loading) {
            this.loading = true;
            EmptyTextProgressView emptyTextProgressView = this.emptyView;
            if (emptyTextProgressView != null && !this.firstLoaded) {
                emptyTextProgressView.showProgress();
            }
            ListAdapter listAdapter = this.listViewAdapter;
            if (listAdapter != null) {
                listAdapter.notifyDataSetChanged();
            }
            TLRPC$TL_messages_search tLRPC$TL_messages_search = new TLRPC$TL_messages_search();
            tLRPC$TL_messages_search.limit = i2;
            tLRPC$TL_messages_search.peer = new TLRPC$TL_inputPeerEmpty();
            tLRPC$TL_messages_search.filter = new TLRPC$TL_inputMessagesFilterPhoneCalls();
            tLRPC$TL_messages_search.q = "";
            tLRPC$TL_messages_search.offset_id = i;
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_search, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    CallLogActivity.this.lambda$getCalls$6$CallLogActivity(tLObject, tLRPC$TL_error);
                }
            }, 2), this.classGuid);
        }
    }

    public /* synthetic */ void lambda$getCalls$6$CallLogActivity(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLObject) {
            private final /* synthetic */ TLRPC$TL_error f$1;
            private final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                CallLogActivity.this.lambda$null$5$CallLogActivity(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$5$CallLogActivity(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        CallLogRow callLogRow;
        if (tLRPC$TL_error == null) {
            SparseArray sparseArray = new SparseArray();
            TLRPC$messages_Messages tLRPC$messages_Messages = (TLRPC$messages_Messages) tLObject;
            this.endReached = tLRPC$messages_Messages.messages.isEmpty();
            for (int i = 0; i < tLRPC$messages_Messages.users.size(); i++) {
                TLRPC$User tLRPC$User = tLRPC$messages_Messages.users.get(i);
                sparseArray.put(tLRPC$User.id, tLRPC$User);
            }
            if (this.calls.size() > 0) {
                ArrayList<CallLogRow> arrayList = this.calls;
                callLogRow = arrayList.get(arrayList.size() - 1);
            } else {
                callLogRow = null;
            }
            for (int i2 = 0; i2 < tLRPC$messages_Messages.messages.size(); i2++) {
                TLRPC$Message tLRPC$Message = tLRPC$messages_Messages.messages.get(i2);
                TLRPC$MessageAction tLRPC$MessageAction = tLRPC$Message.action;
                if (tLRPC$MessageAction != null && !(tLRPC$MessageAction instanceof TLRPC$TL_messageActionHistoryClear)) {
                    int i3 = tLRPC$Message.from_id == UserConfig.getInstance(this.currentAccount).getClientUserId() ? 0 : 1;
                    TLRPC$PhoneCallDiscardReason tLRPC$PhoneCallDiscardReason = tLRPC$Message.action.reason;
                    if (i3 == 1 && ((tLRPC$PhoneCallDiscardReason instanceof TLRPC$TL_phoneCallDiscardReasonMissed) || (tLRPC$PhoneCallDiscardReason instanceof TLRPC$TL_phoneCallDiscardReasonBusy))) {
                        i3 = 2;
                    }
                    int i4 = tLRPC$Message.from_id == UserConfig.getInstance(this.currentAccount).getClientUserId() ? tLRPC$Message.to_id.user_id : tLRPC$Message.from_id;
                    if (!(callLogRow != null && callLogRow.user.id == i4 && callLogRow.type == i3)) {
                        if (callLogRow != null && !this.calls.contains(callLogRow)) {
                            this.calls.add(callLogRow);
                        }
                        callLogRow = new CallLogRow();
                        callLogRow.calls = new ArrayList();
                        callLogRow.user = (TLRPC$User) sparseArray.get(i4);
                        callLogRow.type = i3;
                    }
                    callLogRow.calls.add(tLRPC$Message);
                }
            }
            if (callLogRow != null && callLogRow.calls.size() > 0 && !this.calls.contains(callLogRow)) {
                this.calls.add(callLogRow);
            }
        } else {
            this.endReached = true;
        }
        this.loading = false;
        this.firstLoaded = true;
        EmptyTextProgressView emptyTextProgressView = this.emptyView;
        if (emptyTextProgressView != null) {
            emptyTextProgressView.showTextView();
        }
        ListAdapter listAdapter = this.listViewAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    private void confirmAndDelete(CallLogRow callLogRow) {
        if (getParentActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", NUM));
            builder.setMessage(LocaleController.getString("ConfirmDeleteCallLog", NUM));
            builder.setPositiveButton(LocaleController.getString("Delete", NUM), new DialogInterface.OnClickListener(callLogRow) {
                private final /* synthetic */ CallLogActivity.CallLogRow f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    CallLogActivity.this.lambda$confirmAndDelete$7$CallLogActivity(this.f$1, dialogInterface, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            builder.show().setCanceledOnTouchOutside(true);
        }
    }

    public /* synthetic */ void lambda$confirmAndDelete$7$CallLogActivity(CallLogRow callLogRow, DialogInterface dialogInterface, int i) {
        ArrayList arrayList = new ArrayList();
        for (TLRPC$Message tLRPC$Message : callLogRow.calls) {
            arrayList.add(Integer.valueOf(tLRPC$Message.id));
        }
        MessagesController.getInstance(this.currentAccount).deleteMessages(arrayList, (ArrayList<Long>) null, (TLRPC$EncryptedChat) null, 0, 0, false, false);
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.listViewAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    public void onRequestPermissionsResultFragment(int i, String[] strArr, int[] iArr) {
        if (i != 101) {
            return;
        }
        if (iArr.length <= 0 || iArr[0] != 0) {
            VoIPHelper.permissionDenied(getParentActivity(), (Runnable) null);
        } else {
            VoIPHelper.startCall(this.lastCallUser, getParentActivity(), (TLRPC$UserFull) null);
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getAdapterPosition() != CallLogActivity.this.calls.size();
        }

        public int getItemCount() {
            int size = CallLogActivity.this.calls.size();
            return (CallLogActivity.this.calls.isEmpty() || CallLogActivity.this.endReached) ? size : size + 1;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            TextInfoPrivacyCell textInfoPrivacyCell;
            if (i == 0) {
                CustomCell customCell = new CustomCell(CallLogActivity.this, this.mContext);
                customCell.setTag(new ViewItem(CallLogActivity.this, customCell.imageView, customCell.profileSearchCell));
                textInfoPrivacyCell = customCell;
            } else if (i != 1) {
                TextInfoPrivacyCell textInfoPrivacyCell2 = new TextInfoPrivacyCell(this.mContext);
                textInfoPrivacyCell2.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                textInfoPrivacyCell = textInfoPrivacyCell2;
            } else {
                textInfoPrivacyCell = new LoadingCell(this.mContext);
            }
            return new RecyclerListView.Holder(textInfoPrivacyCell);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            SpannableString spannableString;
            if (viewHolder.getItemViewType() == 0) {
                ViewItem viewItem = (ViewItem) viewHolder.itemView.getTag();
                ProfileSearchCell profileSearchCell = viewItem.cell;
                CallLogRow callLogRow = (CallLogRow) CallLogActivity.this.calls.get(i);
                boolean z = false;
                TLRPC$Message tLRPC$Message = callLogRow.calls.get(0);
                String str = LocaleController.isRTL ? "‫" : "";
                if (callLogRow.calls.size() == 1) {
                    spannableString = new SpannableString(str + "  " + LocaleController.formatDateCallLog((long) tLRPC$Message.date));
                } else {
                    spannableString = new SpannableString(String.format(str + "  (%d) %s", new Object[]{Integer.valueOf(callLogRow.calls.size()), LocaleController.formatDateCallLog((long) tLRPC$Message.date)}));
                }
                SpannableString spannableString2 = spannableString;
                int i2 = callLogRow.type;
                if (i2 == 0) {
                    spannableString2.setSpan(CallLogActivity.this.iconOut, str.length(), str.length() + 1, 0);
                } else if (i2 == 1) {
                    spannableString2.setSpan(CallLogActivity.this.iconIn, str.length(), str.length() + 1, 0);
                } else if (i2 == 2) {
                    spannableString2.setSpan(CallLogActivity.this.iconMissed, str.length(), str.length() + 1, 0);
                }
                profileSearchCell.setData(callLogRow.user, (TLRPC$EncryptedChat) null, (CharSequence) null, spannableString2, false, false);
                if (i != CallLogActivity.this.calls.size() - 1 || !CallLogActivity.this.endReached) {
                    z = true;
                }
                profileSearchCell.useSeparator = z;
                viewItem.button.setTag(callLogRow);
            }
        }

        public int getItemViewType(int i) {
            if (i < CallLogActivity.this.calls.size()) {
                return 0;
            }
            return (CallLogActivity.this.endReached || i != CallLogActivity.this.calls.size()) ? 2 : 1;
        }
    }

    private class ViewItem {
        public ImageView button;
        public ProfileSearchCell cell;

        public ViewItem(CallLogActivity callLogActivity, ImageView imageView, ProfileSearchCell profileSearchCell) {
            this.button = imageView;
            this.cell = profileSearchCell;
        }
    }

    private class CallLogRow {
        public List<TLRPC$Message> calls;
        public int type;
        public TLRPC$User user;

        private CallLogRow(CallLogActivity callLogActivity) {
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        $$Lambda$CallLogActivity$jzz7fdD3HMf_Ya8AHg1H2NHtPCs r9 = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                CallLogActivity.this.lambda$getThemeDescriptions$8$CallLogActivity();
            }
        };
        TextPaint[] textPaintArr = Theme.dialogs_namePaint;
        TextPaint[] textPaintArr2 = Theme.dialogs_nameEncryptedPaint;
        $$Lambda$CallLogActivity$jzz7fdD3HMf_Ya8AHg1H2NHtPCs r7 = r9;
        return new ThemeDescription[]{new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{LocationCell.class, CustomCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"), new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"), new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"), new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "emptyListPlaceholder"), new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "progressCircle"), new ThemeDescription((View) this.listView, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "progressCircle"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"), new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"), new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_actionIcon"), new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_actionBackground"), new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_actionPressedBackground"), new ThemeDescription((View) this.listView, 0, new Class[]{CustomCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_addButton"), new ThemeDescription(this.listView, 0, new Class[]{CustomCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_verifiedCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_verifiedCheck"), new ThemeDescription(this.listView, 0, new Class[]{CustomCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_verifiedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_verifiedBackground"), new ThemeDescription(this.listView, 0, new Class[]{CustomCell.class}, Theme.dialogs_offlinePaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText3"), new ThemeDescription(this.listView, 0, new Class[]{CustomCell.class}, Theme.dialogs_onlinePaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText3"), new ThemeDescription((View) this.listView, 0, new Class[]{CustomCell.class}, (String[]) null, new Paint[]{textPaintArr[0], textPaintArr[1], Theme.dialogs_searchNamePaint}, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_name"), new ThemeDescription((View) this.listView, 0, new Class[]{CustomCell.class}, (String[]) null, new Paint[]{textPaintArr2[0], textPaintArr2[1], Theme.dialogs_searchNameEncryptedPaint}, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_secretName"), new ThemeDescription(this.listView, 0, new Class[]{CustomCell.class}, (Paint) null, Theme.avatarDrawables, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_text"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundRed"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundOrange"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundViolet"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundGreen"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundCyan"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundBlue"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundPink"), new ThemeDescription(this.listView, 0, new Class[]{View.class}, (Paint) null, new Drawable[]{this.greenDrawable, this.greenDrawable2, Theme.calllog_msgCallUpRedDrawable, Theme.calllog_msgCallDownRedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "calls_callReceivedGreenIcon"), new ThemeDescription(this.listView, 0, new Class[]{View.class}, (Paint) null, new Drawable[]{this.redDrawable, Theme.calllog_msgCallUpGreenDrawable, Theme.calllog_msgCallDownGreenDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "calls_callReceivedRedIcon")};
    }

    public /* synthetic */ void lambda$getThemeDescriptions$8$CallLogActivity() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int childCount = recyclerListView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.listView.getChildAt(i);
                if (childAt instanceof CustomCell) {
                    ((CustomCell) childAt).profileSearchCell.update(0);
                }
            }
        }
    }
}
