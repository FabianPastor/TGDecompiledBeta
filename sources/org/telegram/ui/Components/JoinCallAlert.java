package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.Cells.GroupCreateUserCell;
import org.telegram.ui.Cells.ShareDialogCell;
import org.telegram.ui.Components.RecyclerListView;

public class JoinCallAlert extends BottomSheet {
    public static final int TYPE_CREATE = 0;
    public static final int TYPE_DISPLAY = 2;
    public static final int TYPE_JOIN = 1;
    private static ArrayList<TLRPC.Peer> cachedChats;
    private static long lastCacheDid;
    private static long lastCacheTime;
    private static int lastCachedAccount;
    /* access modifiers changed from: private */
    public boolean animationInProgress;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC.Peer> chats;
    private TLRPC.Peer currentPeer;
    /* access modifiers changed from: private */
    public int currentType;
    private JoinCallAlertDelegate delegate;
    private BottomSheetCell doneButton;
    /* access modifiers changed from: private */
    public boolean ignoreLayout;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    private int[] location = new int[2];
    /* access modifiers changed from: private */
    public TextView messageTextView;
    private boolean schedule;
    /* access modifiers changed from: private */
    public int scrollOffsetY;
    private TLRPC.InputPeer selectAfterDismiss;
    /* access modifiers changed from: private */
    public TLRPC.Peer selectedPeer;
    /* access modifiers changed from: private */
    public Drawable shadowDrawable;
    private TextView textView;

    public interface JoinCallAlertDelegate {
        void didSelectChat(TLRPC.InputPeer inputPeer, boolean z, boolean z2);
    }

    public static void resetCache() {
        cachedChats = null;
    }

    public static void processDeletedChat(int account, long did) {
        ArrayList<TLRPC.Peer> arrayList;
        if (lastCachedAccount == account && (arrayList = cachedChats) != null && did <= 0) {
            int a = 0;
            int N = arrayList.size();
            while (true) {
                if (a >= N) {
                    break;
                } else if (MessageObject.getPeerId(cachedChats.get(a)) == did) {
                    cachedChats.remove(a);
                    break;
                } else {
                    a++;
                }
            }
            if (cachedChats.isEmpty()) {
                cachedChats = null;
            }
        }
    }

    public class BottomSheetCell extends FrameLayout {
        /* access modifiers changed from: private */
        public View background;
        private boolean hasBackground;
        private CharSequence text;
        /* access modifiers changed from: private */
        public TextView[] textView = new TextView[2];
        final /* synthetic */ JoinCallAlert this$0;

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public BottomSheetCell(org.telegram.ui.Components.JoinCallAlert r18, android.content.Context r19, boolean r20) {
            /*
                r17 = this;
                r0 = r17
                r1 = r19
                r2 = r18
                r0.this$0 = r2
                r0.<init>(r1)
                r3 = 2
                android.widget.TextView[] r4 = new android.widget.TextView[r3]
                r0.textView = r4
                r4 = r20 ^ 1
                r0.hasBackground = r4
                r4 = 0
                r0.setBackground(r4)
                android.view.View r4 = new android.view.View
                r4.<init>(r1)
                r0.background = r4
                boolean r5 = r0.hasBackground
                java.lang.String r6 = "featuredStickers_addButton"
                r7 = 0
                r8 = 1
                if (r5 == 0) goto L_0x0034
                float[] r5 = new float[r8]
                r9 = 1082130432(0x40800000, float:4.0)
                r5[r7] = r9
                android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.AdaptiveRipple.filledRect((java.lang.String) r6, (float[]) r5)
                r4.setBackground(r5)
            L_0x0034:
                android.view.View r4 = r0.background
                r9 = -1
                r10 = -1082130432(0xffffffffbvar_, float:-1.0)
                r11 = 0
                r12 = 1098907648(0x41800000, float:16.0)
                r5 = 0
                if (r20 == 0) goto L_0x0041
                r13 = 0
                goto L_0x0043
            L_0x0041:
                r13 = 1098907648(0x41800000, float:16.0)
            L_0x0043:
                r14 = 1098907648(0x41800000, float:16.0)
                r15 = 1098907648(0x41800000, float:16.0)
                android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r10, r11, r12, r13, r14, r15)
                r0.addView(r4, r9)
                r4 = 0
            L_0x004f:
                if (r4 >= r3) goto L_0x00f9
                android.widget.TextView[] r9 = r0.textView
                android.widget.TextView r10 = new android.widget.TextView
                r10.<init>(r1)
                r9[r4] = r10
                android.widget.TextView[] r9 = r0.textView
                r9 = r9[r4]
                r9.setFocusable(r7)
                android.widget.TextView[] r9 = r0.textView
                r9 = r9[r4]
                r9.setLines(r8)
                android.widget.TextView[] r9 = r0.textView
                r9 = r9[r4]
                r9.setSingleLine(r8)
                android.widget.TextView[] r9 = r0.textView
                r9 = r9[r4]
                r9.setGravity(r8)
                android.widget.TextView[] r9 = r0.textView
                r9 = r9[r4]
                android.text.TextUtils$TruncateAt r10 = android.text.TextUtils.TruncateAt.END
                r9.setEllipsize(r10)
                android.widget.TextView[] r9 = r0.textView
                r9 = r9[r4]
                r10 = 17
                r9.setGravity(r10)
                boolean r9 = r0.hasBackground
                if (r9 == 0) goto L_0x00a7
                android.widget.TextView[] r9 = r0.textView
                r9 = r9[r4]
                java.lang.String r10 = "featuredStickers_buttonText"
                int r10 = org.telegram.ui.ActionBar.Theme.getColor(r10)
                r9.setTextColor(r10)
                android.widget.TextView[] r9 = r0.textView
                r9 = r9[r4]
                java.lang.String r10 = "fonts/rmedium.ttf"
                android.graphics.Typeface r10 = org.telegram.messenger.AndroidUtilities.getTypeface(r10)
                r9.setTypeface(r10)
                goto L_0x00b2
            L_0x00a7:
                android.widget.TextView[] r9 = r0.textView
                r9 = r9[r4]
                int r10 = org.telegram.ui.ActionBar.Theme.getColor(r6)
                r9.setTextColor(r10)
            L_0x00b2:
                android.widget.TextView[] r9 = r0.textView
                r9 = r9[r4]
                r9.setImportantForAccessibility(r3)
                android.widget.TextView[] r9 = r0.textView
                r9 = r9[r4]
                r10 = 1096810496(0x41600000, float:14.0)
                r9.setTextSize(r8, r10)
                android.widget.TextView[] r9 = r0.textView
                r9 = r9[r4]
                boolean r10 = r0.hasBackground
                if (r10 == 0) goto L_0x00cc
                r10 = 0
                goto L_0x00d2
            L_0x00cc:
                r10 = 1095761920(0x41500000, float:13.0)
                int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            L_0x00d2:
                r9.setPadding(r7, r7, r7, r10)
                android.widget.TextView[] r9 = r0.textView
                r9 = r9[r4]
                r10 = -2
                r11 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                r12 = 17
                r13 = 1103101952(0x41CLASSNAME, float:24.0)
                r14 = 0
                r15 = 1103101952(0x41CLASSNAME, float:24.0)
                r16 = 0
                android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r11, r12, r13, r14, r15, r16)
                r0.addView(r9, r10)
                if (r4 != r8) goto L_0x00f5
                android.widget.TextView[] r9 = r0.textView
                r9 = r9[r4]
                r9.setAlpha(r5)
            L_0x00f5:
                int r4 = r4 + 1
                goto L_0x004f
            L_0x00f9:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.JoinCallAlert.BottomSheetCell.<init>(org.telegram.ui.Components.JoinCallAlert, android.content.Context, boolean):void");
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(this.hasBackground ? 80.0f : 50.0f), NUM));
        }

        public void setText(CharSequence text2, boolean animated) {
            this.text = text2;
            if (!animated) {
                this.textView[0].setText(text2);
                return;
            }
            this.textView[1].setText(text2);
            boolean unused = this.this$0.animationInProgress = true;
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setDuration(180);
            animatorSet.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.textView[0], View.ALPHA, new float[]{1.0f, 0.0f}), ObjectAnimator.ofFloat(this.textView[0], View.TRANSLATION_Y, new float[]{0.0f, (float) (-AndroidUtilities.dp(10.0f))}), ObjectAnimator.ofFloat(this.textView[1], View.ALPHA, new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(this.textView[1], View.TRANSLATION_Y, new float[]{(float) AndroidUtilities.dp(10.0f), 0.0f})});
            animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    boolean unused = BottomSheetCell.this.this$0.animationInProgress = false;
                    TextView temp = BottomSheetCell.this.textView[0];
                    BottomSheetCell.this.textView[0] = BottomSheetCell.this.textView[1];
                    BottomSheetCell.this.textView[1] = temp;
                }
            });
            animatorSet.start();
        }

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            info.setClassName("android.widget.Button");
            info.setClickable(true);
        }
    }

    public static void checkFewUsers(Context context, long did, AccountInstance accountInstance, MessagesStorage.BooleanCallback callback) {
        if (lastCachedAccount != accountInstance.getCurrentAccount() || lastCacheDid != did || cachedChats == null || SystemClock.elapsedRealtime() - lastCacheTime >= 240000) {
            AlertDialog progressDialog = new AlertDialog(context, 3);
            TLRPC.TL_phone_getGroupCallJoinAs req = new TLRPC.TL_phone_getGroupCallJoinAs();
            req.peer = accountInstance.getMessagesController().getInputPeer(did);
            progressDialog.setOnCancelListener(new JoinCallAlert$$ExternalSyntheticLambda0(accountInstance, accountInstance.getConnectionsManager().sendRequest(req, new JoinCallAlert$$ExternalSyntheticLambda6(progressDialog, did, accountInstance, callback))));
            try {
                progressDialog.showDelayed(500);
            } catch (Exception e) {
            }
        } else {
            boolean z = true;
            if (cachedChats.size() != 1) {
                z = false;
            }
            callback.run(z);
        }
    }

    static /* synthetic */ void lambda$checkFewUsers$0(AlertDialog progressDialog, TLObject response, long did, AccountInstance accountInstance, MessagesStorage.BooleanCallback callback) {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if (response != null) {
            TLRPC.TL_phone_joinAsPeers res = (TLRPC.TL_phone_joinAsPeers) response;
            cachedChats = res.peers;
            lastCacheDid = did;
            lastCacheTime = SystemClock.elapsedRealtime();
            lastCachedAccount = accountInstance.getCurrentAccount();
            boolean z = false;
            accountInstance.getMessagesController().putChats(res.chats, false);
            accountInstance.getMessagesController().putUsers(res.users, false);
            if (res.peers.size() == 1) {
                z = true;
            }
            callback.run(z);
        }
    }

    public static void open(Context context, long did, AccountInstance accountInstance, BaseFragment fragment, int type, TLRPC.Peer scheduledPeer, JoinCallAlertDelegate delegate2) {
        Context context2 = context;
        long j = did;
        JoinCallAlertDelegate joinCallAlertDelegate = delegate2;
        if (context2 == null) {
            AccountInstance accountInstance2 = accountInstance;
        } else if (joinCallAlertDelegate == null) {
            AccountInstance accountInstance3 = accountInstance;
        } else if (lastCachedAccount != accountInstance.getCurrentAccount() || lastCacheDid != j || cachedChats == null || SystemClock.elapsedRealtime() - lastCacheTime >= 300000) {
            AlertDialog progressDialog = new AlertDialog(context2, 3);
            TLRPC.TL_phone_getGroupCallJoinAs req = new TLRPC.TL_phone_getGroupCallJoinAs();
            req.peer = accountInstance.getMessagesController().getInputPeer(j);
            JoinCallAlert$$ExternalSyntheticLambda7 joinCallAlert$$ExternalSyntheticLambda7 = r1;
            JoinCallAlert$$ExternalSyntheticLambda7 joinCallAlert$$ExternalSyntheticLambda72 = new JoinCallAlert$$ExternalSyntheticLambda7(progressDialog, accountInstance, delegate2, did, context, fragment, type, scheduledPeer);
            AccountInstance accountInstance4 = accountInstance;
            progressDialog.setOnCancelListener(new JoinCallAlert$$ExternalSyntheticLambda1(accountInstance4, accountInstance.getConnectionsManager().sendRequest(req, joinCallAlert$$ExternalSyntheticLambda7)));
            try {
                progressDialog.showDelayed(500);
            } catch (Exception e) {
            }
        } else if (cachedChats.size() != 1 || type == 0) {
            showAlert(context, did, cachedChats, fragment, type, scheduledPeer, delegate2);
            AccountInstance accountInstance5 = accountInstance;
        } else {
            joinCallAlertDelegate.didSelectChat(accountInstance.getMessagesController().getInputPeer(MessageObject.getPeerId(cachedChats.get(0))), false, false);
            AccountInstance accountInstance6 = accountInstance;
        }
    }

    static /* synthetic */ void lambda$open$3(AlertDialog progressDialog, TLObject response, AccountInstance accountInstance, JoinCallAlertDelegate delegate2, long did, Context context, BaseFragment fragment, int type, TLRPC.Peer scheduledPeer) {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if (response != null) {
            TLRPC.TL_phone_joinAsPeers res = (TLRPC.TL_phone_joinAsPeers) response;
            if (res.peers.size() == 1) {
                JoinCallAlertDelegate joinCallAlertDelegate = delegate2;
                delegate2.didSelectChat(accountInstance.getMessagesController().getInputPeer(MessageObject.getPeerId(res.peers.get(0))), false, false);
                return;
            }
            JoinCallAlertDelegate joinCallAlertDelegate2 = delegate2;
            cachedChats = res.peers;
            lastCacheDid = did;
            lastCacheTime = SystemClock.elapsedRealtime();
            lastCachedAccount = accountInstance.getCurrentAccount();
            accountInstance.getMessagesController().putChats(res.chats, false);
            accountInstance.getMessagesController().putUsers(res.users, false);
            showAlert(context, did, res.peers, fragment, type, scheduledPeer, delegate2);
            return;
        }
        JoinCallAlertDelegate joinCallAlertDelegate3 = delegate2;
    }

    private static void showAlert(Context context, long dialogId, ArrayList<TLRPC.Peer> peers, BaseFragment fragment, int type, TLRPC.Peer scheduledPeer, JoinCallAlertDelegate delegate2) {
        BaseFragment baseFragment = fragment;
        JoinCallAlert alert = new JoinCallAlert(context, dialogId, peers, type, scheduledPeer, delegate2);
        if (baseFragment == null) {
            alert.show();
        } else if (fragment.getParentActivity() != null) {
            fragment.showDialog(alert);
        }
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private JoinCallAlert(android.content.Context r26, long r27, java.util.ArrayList<org.telegram.tgnet.TLRPC.Peer> r29, int r30, org.telegram.tgnet.TLRPC.Peer r31, org.telegram.ui.Components.JoinCallAlert.JoinCallAlertDelegate r32) {
        /*
            r25 = this;
            r0 = r25
            r1 = r26
            r2 = r30
            r3 = r32
            r4 = 0
            r0.<init>(r1, r4)
            r5 = 2
            int[] r6 = new int[r5]
            r0.location = r6
            r0.setApplyBottomPadding(r4)
            java.util.ArrayList r6 = new java.util.ArrayList
            r7 = r29
            r6.<init>(r7)
            r0.chats = r6
            r0.delegate = r3
            r0.currentType = r2
            android.content.res.Resources r6 = r26.getResources()
            r8 = 2131166138(0x7var_ba, float:1.7946513E38)
            android.graphics.drawable.Drawable r6 = r6.getDrawable(r8)
            android.graphics.drawable.Drawable r6 = r6.mutate()
            r0.shadowDrawable = r6
            if (r2 != r5) goto L_0x00aa
            org.telegram.messenger.voip.VoIPService r6 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r6 == 0) goto L_0x0064
            org.telegram.messenger.voip.VoIPService r6 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            long r8 = r6.getSelfId()
            r6 = 0
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Peer> r10 = r0.chats
            int r10 = r10.size()
        L_0x0049:
            if (r6 >= r10) goto L_0x0063
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Peer> r11 = r0.chats
            java.lang.Object r11 = r11.get(r6)
            org.telegram.tgnet.TLRPC$Peer r11 = (org.telegram.tgnet.TLRPC.Peer) r11
            long r12 = org.telegram.messenger.MessageObject.getPeerId(r11)
            int r14 = (r12 > r8 ? 1 : (r12 == r8 ? 0 : -1))
            if (r14 != 0) goto L_0x0060
            r0.currentPeer = r11
            r0.selectedPeer = r11
            goto L_0x0063
        L_0x0060:
            int r6 = r6 + 1
            goto L_0x0049
        L_0x0063:
            goto L_0x0096
        L_0x0064:
            if (r31 == 0) goto L_0x008c
            long r8 = org.telegram.messenger.MessageObject.getPeerId(r31)
            r6 = 0
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Peer> r10 = r0.chats
            int r10 = r10.size()
        L_0x0071:
            if (r6 >= r10) goto L_0x008b
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Peer> r11 = r0.chats
            java.lang.Object r11 = r11.get(r6)
            org.telegram.tgnet.TLRPC$Peer r11 = (org.telegram.tgnet.TLRPC.Peer) r11
            long r12 = org.telegram.messenger.MessageObject.getPeerId(r11)
            int r14 = (r12 > r8 ? 1 : (r12 == r8 ? 0 : -1))
            if (r14 != 0) goto L_0x0088
            r0.currentPeer = r11
            r0.selectedPeer = r11
            goto L_0x008b
        L_0x0088:
            int r6 = r6 + 1
            goto L_0x0071
        L_0x008b:
            goto L_0x0096
        L_0x008c:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Peer> r6 = r0.chats
            java.lang.Object r6 = r6.get(r4)
            org.telegram.tgnet.TLRPC$Peer r6 = (org.telegram.tgnet.TLRPC.Peer) r6
            r0.selectedPeer = r6
        L_0x0096:
            android.graphics.drawable.Drawable r6 = r0.shadowDrawable
            android.graphics.PorterDuffColorFilter r8 = new android.graphics.PorterDuffColorFilter
            java.lang.String r9 = "voipgroup_inviteMembersBackground"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r10 = r9
            android.graphics.PorterDuff$Mode r11 = android.graphics.PorterDuff.Mode.MULTIPLY
            r8.<init>(r9, r11)
            r6.setColorFilter(r8)
            goto L_0x00c5
        L_0x00aa:
            android.graphics.PorterDuffColorFilter r8 = new android.graphics.PorterDuffColorFilter
            java.lang.String r9 = "dialogBackground"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r10 = r9
            android.graphics.PorterDuff$Mode r11 = android.graphics.PorterDuff.Mode.MULTIPLY
            r8.<init>(r9, r11)
            r6.setColorFilter(r8)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Peer> r6 = r0.chats
            java.lang.Object r6 = r6.get(r4)
            org.telegram.tgnet.TLRPC$Peer r6 = (org.telegram.tgnet.TLRPC.Peer) r6
            r0.selectedPeer = r6
        L_0x00c5:
            r0.fixNavigationBar(r10)
            int r6 = r0.currentType
            r8 = 1
            if (r6 != 0) goto L_0x00e2
            org.telegram.ui.Components.JoinCallAlert$1 r6 = new org.telegram.ui.Components.JoinCallAlert$1
            r6.<init>(r1)
            r6.setOrientation(r8)
            androidx.core.widget.NestedScrollView r9 = new androidx.core.widget.NestedScrollView
            r9.<init>(r1)
            r11 = r6
            r9.addView(r6)
            r0.setCustomView(r9)
            goto L_0x00f9
        L_0x00e2:
            org.telegram.ui.Components.JoinCallAlert$2 r6 = new org.telegram.ui.Components.JoinCallAlert$2
            r6.<init>(r1)
            r0.containerView = r6
            android.view.ViewGroup r11 = r0.containerView
            android.view.ViewGroup r6 = r0.containerView
            r6.setWillNotDraw(r4)
            android.view.ViewGroup r6 = r0.containerView
            int r9 = r0.backgroundPaddingLeft
            int r12 = r0.backgroundPaddingLeft
            r6.setPadding(r9, r4, r12, r4)
        L_0x00f9:
            int r6 = r0.currentAccount
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)
            r12 = r27
            long r14 = -r12
            java.lang.Long r9 = java.lang.Long.valueOf(r14)
            org.telegram.tgnet.TLRPC$Chat r6 = r6.getChat(r9)
            org.telegram.ui.Components.JoinCallAlert$3 r9 = new org.telegram.ui.Components.JoinCallAlert$3
            r9.<init>(r1)
            r0.listView = r9
            androidx.recyclerview.widget.LinearLayoutManager r14 = new androidx.recyclerview.widget.LinearLayoutManager
            android.content.Context r15 = r25.getContext()
            int r5 = r0.currentType
            if (r5 != 0) goto L_0x011d
            r5 = 0
            goto L_0x011e
        L_0x011d:
            r5 = 1
        L_0x011e:
            r14.<init>(r15, r5, r4)
            r9.setLayoutManager(r14)
            org.telegram.ui.Components.RecyclerListView r5 = r0.listView
            org.telegram.ui.Components.JoinCallAlert$ListAdapter r9 = new org.telegram.ui.Components.JoinCallAlert$ListAdapter
            r9.<init>(r1)
            r5.setAdapter(r9)
            org.telegram.ui.Components.RecyclerListView r5 = r0.listView
            r5.setVerticalScrollBarEnabled(r4)
            org.telegram.ui.Components.RecyclerListView r5 = r0.listView
            r5.setClipToPadding(r4)
            org.telegram.ui.Components.RecyclerListView r5 = r0.listView
            r5.setEnabled(r8)
            org.telegram.ui.Components.RecyclerListView r5 = r0.listView
            r5.setSelectorDrawableColor(r4)
            org.telegram.ui.Components.RecyclerListView r5 = r0.listView
            java.lang.String r9 = "dialogScrollGlow"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r5.setGlowColor(r9)
            org.telegram.ui.Components.RecyclerListView r5 = r0.listView
            org.telegram.ui.Components.JoinCallAlert$4 r9 = new org.telegram.ui.Components.JoinCallAlert$4
            r9.<init>()
            r5.setOnScrollListener(r9)
            org.telegram.ui.Components.RecyclerListView r5 = r0.listView
            org.telegram.ui.Components.JoinCallAlert$$ExternalSyntheticLambda8 r9 = new org.telegram.ui.Components.JoinCallAlert$$ExternalSyntheticLambda8
            r9.<init>(r0, r6)
            r5.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r9)
            if (r2 == 0) goto L_0x017b
            org.telegram.ui.Components.RecyclerListView r5 = r0.listView
            r16 = -1
            r17 = -1082130432(0xffffffffbvar_, float:-1.0)
            r18 = 51
            r19 = 0
            r20 = 1120403456(0x42CLASSNAME, float:100.0)
            r21 = 0
            r22 = 1117782016(0x42a00000, float:80.0)
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
            r11.addView(r5, r9)
            goto L_0x018f
        L_0x017b:
            org.telegram.ui.Components.RecyclerListView r5 = r0.listView
            r5.setSelectorDrawableColor(r4)
            org.telegram.ui.Components.RecyclerListView r5 = r0.listView
            r9 = 1092616192(0x41200000, float:10.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r5.setPadding(r14, r4, r9, r4)
        L_0x018f:
            if (r2 != 0) goto L_0x01b9
            org.telegram.ui.Components.RLottieImageView r5 = new org.telegram.ui.Components.RLottieImageView
            r5.<init>(r1)
            r5.setAutoRepeat(r8)
            r9 = 2131558575(0x7f0d00af, float:1.874247E38)
            r14 = 120(0x78, float:1.68E-43)
            r5.setAnimation(r9, r14, r14)
            r5.playAnimation()
            r16 = 160(0xa0, float:2.24E-43)
            r17 = 160(0xa0, float:2.24E-43)
            r18 = 49
            r19 = 17
            r20 = 8
            r21 = 17
            r22 = 0
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r16, (int) r17, (int) r18, (int) r19, (int) r20, (int) r21, (int) r22)
            r11.addView(r5, r9)
        L_0x01b9:
            android.widget.TextView r5 = new android.widget.TextView
            r5.<init>(r1)
            r0.textView = r5
            java.lang.String r9 = "fonts/rmedium.ttf"
            android.graphics.Typeface r9 = org.telegram.messenger.AndroidUtilities.getTypeface(r9)
            r5.setTypeface(r9)
            android.widget.TextView r5 = r0.textView
            r9 = 1101004800(0x41a00000, float:20.0)
            r5.setTextSize(r8, r9)
            r5 = 2
            if (r2 != r5) goto L_0x01df
            android.widget.TextView r5 = r0.textView
            java.lang.String r9 = "voipgroup_nameText"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r5.setTextColor(r9)
            goto L_0x01ea
        L_0x01df:
            android.widget.TextView r5 = r0.textView
            java.lang.String r9 = "dialogTextBlack"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r5.setTextColor(r9)
        L_0x01ea:
            android.widget.TextView r5 = r0.textView
            r5.setSingleLine(r8)
            android.widget.TextView r5 = r0.textView
            android.text.TextUtils$TruncateAt r9 = android.text.TextUtils.TruncateAt.END
            r5.setEllipsize(r9)
            if (r2 != 0) goto L_0x0233
            boolean r5 = org.telegram.messenger.ChatObject.isChannelOrGiga(r6)
            if (r5 == 0) goto L_0x020d
            android.widget.TextView r5 = r0.textView
            r9 = 2131628425(0x7f0e1189, float:1.8884142E38)
            java.lang.String r14 = "StartVoipChannelTitle"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r14, r9)
            r5.setText(r9)
            goto L_0x021b
        L_0x020d:
            android.widget.TextView r5 = r0.textView
            r9 = 2131628429(0x7f0e118d, float:1.888415E38)
            java.lang.String r14 = "StartVoipChatTitle"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r14, r9)
            r5.setText(r9)
        L_0x021b:
            android.widget.TextView r5 = r0.textView
            r16 = -2
            r17 = -2
            r18 = 49
            r19 = 23
            r20 = 16
            r21 = 23
            r22 = 0
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r16, (int) r17, (int) r18, (int) r19, (int) r20, (int) r21, (int) r22)
            r11.addView(r5, r9)
            goto L_0x027f
        L_0x0233:
            r5 = 2
            if (r2 != r5) goto L_0x0245
            android.widget.TextView r5 = r0.textView
            r9 = 2131629046(0x7f0e13f6, float:1.8885402E38)
            java.lang.String r14 = "VoipGroupDisplayAs"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r14, r9)
            r5.setText(r9)
            goto L_0x0268
        L_0x0245:
            boolean r5 = org.telegram.messenger.ChatObject.isChannelOrGiga(r6)
            if (r5 == 0) goto L_0x025a
            android.widget.TextView r5 = r0.textView
            r9 = 2131628975(0x7f0e13af, float:1.8885258E38)
            java.lang.String r14 = "VoipChannelJoinAs"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r14, r9)
            r5.setText(r9)
            goto L_0x0268
        L_0x025a:
            android.widget.TextView r5 = r0.textView
            r9 = 2131629064(0x7f0e1408, float:1.8885438E38)
            java.lang.String r14 = "VoipGroupJoinAs"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r14, r9)
            r5.setText(r9)
        L_0x0268:
            android.widget.TextView r5 = r0.textView
            r16 = -2
            r17 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r18 = 51
            r19 = 1102577664(0x41b80000, float:23.0)
            r20 = 1090519040(0x41000000, float:8.0)
            r21 = 1102577664(0x41b80000, float:23.0)
            r22 = 0
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
            r11.addView(r5, r9)
        L_0x027f:
            android.widget.TextView r5 = new android.widget.TextView
            android.content.Context r9 = r25.getContext()
            r5.<init>(r9)
            r0.messageTextView = r5
            r9 = 2
            if (r2 != r9) goto L_0x0297
            java.lang.String r9 = "voipgroup_lastSeenText"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r5.setTextColor(r9)
            goto L_0x02a0
        L_0x0297:
            java.lang.String r9 = "dialogTextGray3"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r5.setTextColor(r9)
        L_0x02a0:
            android.widget.TextView r5 = r0.messageTextView
            r9 = 1096810496(0x41600000, float:14.0)
            r5.setTextSize(r8, r9)
            r5 = 0
            r9 = 0
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Peer> r14 = r0.chats
            int r14 = r14.size()
        L_0x02af:
            if (r9 >= r14) goto L_0x02ec
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Peer> r15 = r0.chats
            java.lang.Object r15 = r15.get(r9)
            org.telegram.tgnet.TLRPC$Peer r15 = (org.telegram.tgnet.TLRPC.Peer) r15
            r16 = r5
            long r4 = org.telegram.messenger.MessageObject.getPeerId(r15)
            r17 = 0
            int r15 = (r4 > r17 ? 1 : (r4 == r17 ? 0 : -1))
            if (r15 >= 0) goto L_0x02e3
            int r15 = r0.currentAccount
            org.telegram.messenger.MessagesController r15 = org.telegram.messenger.MessagesController.getInstance(r15)
            r18 = r9
            long r8 = -r4
            java.lang.Long r8 = java.lang.Long.valueOf(r8)
            org.telegram.tgnet.TLRPC$Chat r8 = r15.getChat(r8)
            boolean r9 = org.telegram.messenger.ChatObject.isChannel(r8)
            if (r9 == 0) goto L_0x02e0
            boolean r9 = r8.megagroup
            if (r9 == 0) goto L_0x02e5
        L_0x02e0:
            r9 = 1
            r5 = r9
            goto L_0x02f0
        L_0x02e3:
            r18 = r9
        L_0x02e5:
            int r9 = r18 + 1
            r5 = r16
            r4 = 0
            r8 = 1
            goto L_0x02af
        L_0x02ec:
            r16 = r5
            r18 = r9
        L_0x02f0:
            android.widget.TextView r4 = r0.messageTextView
            org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy r8 = new org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy
            r8.<init>()
            r4.setMovementMethod(r8)
            android.widget.TextView r4 = r0.messageTextView
            java.lang.String r8 = "dialogTextLink"
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
            r4.setLinkTextColor(r8)
            r4 = 5
            if (r2 != 0) goto L_0x0376
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            boolean r9 = org.telegram.messenger.ChatObject.isChannel(r6)
            if (r9 == 0) goto L_0x0324
            boolean r9 = r6.megagroup
            if (r9 != 0) goto L_0x0324
            r9 = 2131628989(0x7f0e13bd, float:1.8885286E38)
            java.lang.String r14 = "VoipChannelStart2"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r14, r9)
            r8.append(r9)
            goto L_0x0330
        L_0x0324:
            r9 = 2131629103(0x7f0e142f, float:1.8885517E38)
            java.lang.String r14 = "VoipGroupStart2"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r14, r9)
            r8.append(r9)
        L_0x0330:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Peer> r9 = r0.chats
            int r9 = r9.size()
            r14 = 1
            if (r9 <= r14) goto L_0x034b
            java.lang.String r9 = "\n\n"
            r8.append(r9)
            r9 = 2131629003(0x7f0e13cb, float:1.8885315E38)
            java.lang.String r14 = "VoipChatDisplayedAs"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r14, r9)
            r8.append(r9)
            goto L_0x0352
        L_0x034b:
            org.telegram.ui.Components.RecyclerListView r9 = r0.listView
            r14 = 8
            r9.setVisibility(r14)
        L_0x0352:
            android.widget.TextView r9 = r0.messageTextView
            r9.setText(r8)
            android.widget.TextView r9 = r0.messageTextView
            r14 = 49
            r9.setGravity(r14)
            android.widget.TextView r9 = r0.messageTextView
            r18 = -2
            r19 = -2
            r20 = 49
            r21 = 23
            r22 = 0
            r23 = 23
            r24 = 5
            android.widget.LinearLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r18, (int) r19, (int) r20, (int) r21, (int) r22, (int) r23, (int) r24)
            r11.addView(r9, r14)
            goto L_0x03ba
        L_0x0376:
            if (r5 == 0) goto L_0x0387
            android.widget.TextView r8 = r0.messageTextView
            r9 = 2131629106(0x7f0e1432, float:1.8885524E38)
            java.lang.String r14 = "VoipGroupStartAsInfoGroup"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r14, r9)
            r8.setText(r9)
            goto L_0x0395
        L_0x0387:
            android.widget.TextView r8 = r0.messageTextView
            r9 = 2131629105(0x7f0e1431, float:1.8885522E38)
            java.lang.String r14 = "VoipGroupStartAsInfo"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r14, r9)
            r8.setText(r9)
        L_0x0395:
            android.widget.TextView r8 = r0.messageTextView
            boolean r9 = org.telegram.messenger.LocaleController.isRTL
            if (r9 == 0) goto L_0x039d
            r9 = 5
            goto L_0x039e
        L_0x039d:
            r9 = 3
        L_0x039e:
            r9 = r9 | 48
            r8.setGravity(r9)
            android.widget.TextView r8 = r0.messageTextView
            r18 = -2
            r19 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r20 = 51
            r21 = 1102577664(0x41b80000, float:23.0)
            r22 = 0
            r23 = 1102577664(0x41b80000, float:23.0)
            r24 = 1084227584(0x40a00000, float:5.0)
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r18, r19, r20, r21, r22, r23, r24)
            r11.addView(r8, r9)
        L_0x03ba:
            if (r2 != 0) goto L_0x03e0
            org.telegram.ui.Components.RecyclerListView r8 = r0.listView
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Peer> r9 = r0.chats
            int r9 = r9.size()
            if (r9 >= r4) goto L_0x03ca
            r4 = -2
            r18 = -2
            goto L_0x03cd
        L_0x03ca:
            r4 = -1
            r18 = -1
        L_0x03cd:
            r19 = 95
            r20 = 49
            r21 = 0
            r22 = 6
            r23 = 0
            r24 = 0
            android.widget.LinearLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r18, (int) r19, (int) r20, (int) r21, (int) r22, (int) r23, (int) r24)
            r11.addView(r8, r4)
        L_0x03e0:
            org.telegram.ui.Components.JoinCallAlert$BottomSheetCell r4 = new org.telegram.ui.Components.JoinCallAlert$BottomSheetCell
            r8 = 0
            r4.<init>(r0, r1, r8)
            r0.doneButton = r4
            android.view.View r4 = r4.background
            org.telegram.ui.Components.JoinCallAlert$$ExternalSyntheticLambda3 r8 = new org.telegram.ui.Components.JoinCallAlert$$ExternalSyntheticLambda3
            r8.<init>(r0, r3)
            r4.setOnClickListener(r8)
            int r4 = r0.currentType
            if (r4 != 0) goto L_0x0458
            org.telegram.ui.Components.JoinCallAlert$BottomSheetCell r4 = r0.doneButton
            r18 = -1
            r19 = 50
            r20 = 51
            r21 = 0
            r22 = 0
            r23 = 0
            r24 = 0
            android.widget.LinearLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r18, (int) r19, (int) r20, (int) r21, (int) r22, (int) r23, (int) r24)
            r11.addView(r4, r8)
            org.telegram.ui.Components.JoinCallAlert$BottomSheetCell r4 = new org.telegram.ui.Components.JoinCallAlert$BottomSheetCell
            r8 = 1
            r4.<init>(r0, r1, r8)
            boolean r8 = org.telegram.messenger.ChatObject.isChannelOrGiga(r6)
            if (r8 == 0) goto L_0x0429
            r8 = 2131628985(0x7f0e13b9, float:1.8885278E38)
            java.lang.String r9 = "VoipChannelScheduleVoiceChat"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r9 = 0
            r4.setText(r8, r9)
            goto L_0x0436
        L_0x0429:
            r9 = 0
            r8 = 2131629095(0x7f0e1427, float:1.8885501E38)
            java.lang.String r14 = "VoipGroupScheduleVoiceChat"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r14, r8)
            r4.setText(r8, r9)
        L_0x0436:
            android.view.View r8 = r4.background
            org.telegram.ui.Components.JoinCallAlert$$ExternalSyntheticLambda2 r9 = new org.telegram.ui.Components.JoinCallAlert$$ExternalSyntheticLambda2
            r9.<init>(r0)
            r8.setOnClickListener(r9)
            r16 = -1
            r17 = 50
            r18 = 51
            r19 = 0
            r20 = 0
            r21 = 0
            r22 = 0
            android.widget.LinearLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r16, (int) r17, (int) r18, (int) r19, (int) r20, (int) r21, (int) r22)
            r11.addView(r4, r8)
            goto L_0x046f
        L_0x0458:
            org.telegram.ui.Components.JoinCallAlert$BottomSheetCell r4 = r0.doneButton
            r16 = -1
            r17 = 1112014848(0x42480000, float:50.0)
            r18 = 83
            r19 = 0
            r20 = 0
            r21 = 0
            r22 = 0
            android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
            r11.addView(r4, r8)
        L_0x046f:
            r4 = 0
            r0.updateDoneButton(r4, r6)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.JoinCallAlert.<init>(android.content.Context, long, java.util.ArrayList, int, org.telegram.tgnet.TLRPC$Peer, org.telegram.ui.Components.JoinCallAlert$JoinCallAlertDelegate):void");
    }

    /* renamed from: lambda$new$6$org-telegram-ui-Components-JoinCallAlert  reason: not valid java name */
    public /* synthetic */ void m1072lambda$new$6$orgtelegramuiComponentsJoinCallAlert(TLRPC.Chat chat, View view, int position) {
        if (!this.animationInProgress && this.chats.get(position) != this.selectedPeer) {
            this.selectedPeer = this.chats.get(position);
            if (view instanceof GroupCreateUserCell) {
                ((GroupCreateUserCell) view).setChecked(true, true);
            } else if (view instanceof ShareDialogCell) {
                ((ShareDialogCell) view).setChecked(true, true);
                view.invalidate();
            }
            int N = this.listView.getChildCount();
            for (int a = 0; a < N; a++) {
                View child = this.listView.getChildAt(a);
                if (child != view) {
                    if (view instanceof GroupCreateUserCell) {
                        ((GroupCreateUserCell) child).setChecked(false, true);
                    } else if (view instanceof ShareDialogCell) {
                        ((ShareDialogCell) child).setChecked(false, true);
                    }
                }
            }
            if (this.currentType != 0) {
                updateDoneButton(true, chat);
            }
        }
    }

    /* renamed from: lambda$new$7$org-telegram-ui-Components-JoinCallAlert  reason: not valid java name */
    public /* synthetic */ void m1073lambda$new$7$orgtelegramuiComponentsJoinCallAlert(JoinCallAlertDelegate delegate2, View v) {
        TLRPC.InputPeer peer = MessagesController.getInstance(this.currentAccount).getInputPeer(MessageObject.getPeerId(this.selectedPeer));
        if (this.currentType != 2) {
            this.selectAfterDismiss = peer;
        } else if (this.selectedPeer != this.currentPeer) {
            boolean z = true;
            if (this.chats.size() <= 1) {
                z = false;
            }
            delegate2.didSelectChat(peer, z, false);
        }
        dismiss();
    }

    /* renamed from: lambda$new$8$org-telegram-ui-Components-JoinCallAlert  reason: not valid java name */
    public /* synthetic */ void m1074lambda$new$8$orgtelegramuiComponentsJoinCallAlert(View v) {
        this.selectAfterDismiss = MessagesController.getInstance(this.currentAccount).getInputPeer(MessageObject.getPeerId(this.selectedPeer));
        this.schedule = true;
        dismiss();
    }

    private void updateDoneButton(boolean animated, TLRPC.Chat chat) {
        if (this.currentType != 0) {
            long did = MessageObject.getPeerId(this.selectedPeer);
            if (DialogObject.isUserDialog(did)) {
                this.doneButton.setText(LocaleController.formatString("VoipGroupContinueAs", NUM, UserObject.getFirstName(MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(did)))), animated);
                return;
            }
            TLRPC.Chat peerChat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-did));
            BottomSheetCell bottomSheetCell = this.doneButton;
            Object[] objArr = new Object[1];
            objArr[0] = peerChat != null ? peerChat.title : "";
            bottomSheetCell.setText(LocaleController.formatString("VoipGroupContinueAs", NUM, objArr), animated);
        } else if (ChatObject.isChannelOrGiga(chat)) {
            this.doneButton.setText(LocaleController.formatString("VoipChannelStartVoiceChat", NUM, new Object[0]), animated);
        } else {
            this.doneButton.setText(LocaleController.formatString("VoipGroupStartVoiceChat", NUM, new Object[0]), animated);
        }
    }

    /* access modifiers changed from: private */
    public void updateLayout() {
        if (this.currentType != 0) {
            if (this.listView.getChildCount() <= 0) {
                RecyclerListView recyclerListView = this.listView;
                int paddingTop = recyclerListView.getPaddingTop();
                this.scrollOffsetY = paddingTop;
                recyclerListView.setTopGlowOffset(paddingTop);
                this.containerView.invalidate();
                return;
            }
            int newOffset = 0;
            View child = this.listView.getChildAt(0);
            RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(child);
            int top = child.getTop() - AndroidUtilities.dp(9.0f);
            if (top > 0 && holder != null && holder.getAdapterPosition() == 0) {
                newOffset = top;
            }
            if (this.scrollOffsetY != newOffset) {
                this.textView.setTranslationY((float) (AndroidUtilities.dp(19.0f) + top));
                this.messageTextView.setTranslationY((float) (AndroidUtilities.dp(56.0f) + top));
                RecyclerListView recyclerListView2 = this.listView;
                this.scrollOffsetY = newOffset;
                recyclerListView2.setTopGlowOffset(newOffset);
                this.containerView.invalidate();
            }
        }
    }

    public void dismissInternal() {
        super.dismissInternal();
        TLRPC.InputPeer inputPeer = this.selectAfterDismiss;
        if (inputPeer != null) {
            JoinCallAlertDelegate joinCallAlertDelegate = this.delegate;
            boolean z = true;
            if (this.chats.size() <= 1) {
                z = false;
            }
            joinCallAlertDelegate.didSelectChat(inputPeer, z, this.schedule);
        }
    }

    /* access modifiers changed from: protected */
    public boolean canDismissWithSwipe() {
        return false;
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context context;

        public ListAdapter(Context context2) {
            this.context = context2;
        }

        public int getItemCount() {
            return JoinCallAlert.this.chats.size();
        }

        public int getItemViewType(int position) {
            return 0;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return true;
        }

        /* JADX WARNING: type inference failed for: r0v4, types: [android.view.View, org.telegram.ui.Cells.ShareDialogCell] */
        /* JADX WARNING: Multi-variable type inference failed */
        /* JADX WARNING: Unknown variable types count: 1 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r11, int r12) {
            /*
                r10 = this;
                org.telegram.ui.Components.JoinCallAlert r0 = org.telegram.ui.Components.JoinCallAlert.this
                int r0 = r0.currentType
                r1 = 2
                if (r0 != 0) goto L_0x0026
                org.telegram.ui.Cells.ShareDialogCell r0 = new org.telegram.ui.Cells.ShareDialogCell
                android.content.Context r2 = r10.context
                r3 = 0
                r0.<init>(r2, r1, r3)
                androidx.recyclerview.widget.RecyclerView$LayoutParams r1 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r2 = 1117782016(0x42a00000, float:80.0)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                r3 = 1120403456(0x42CLASSNAME, float:100.0)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                r1.<init>((int) r2, (int) r3)
                r0.setLayoutParams(r1)
                goto L_0x003e
            L_0x0026:
                org.telegram.ui.Cells.GroupCreateUserCell r0 = new org.telegram.ui.Cells.GroupCreateUserCell
                android.content.Context r5 = r10.context
                r6 = 2
                r7 = 0
                r8 = 0
                org.telegram.ui.Components.JoinCallAlert r2 = org.telegram.ui.Components.JoinCallAlert.this
                int r2 = r2.currentType
                if (r2 != r1) goto L_0x0038
                r1 = 1
                r9 = 1
                goto L_0x003a
            L_0x0038:
                r1 = 0
                r9 = 0
            L_0x003a:
                r4 = r0
                r4.<init>(r5, r6, r7, r8, r9)
            L_0x003e:
                org.telegram.ui.Components.RecyclerListView$Holder r1 = new org.telegram.ui.Components.RecyclerListView$Holder
                r1.<init>(r0)
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.JoinCallAlert.ListAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            int adapterPosition = holder.getAdapterPosition();
            long did = MessageObject.getPeerId(JoinCallAlert.this.selectedPeer);
            boolean z = true;
            if (holder.itemView instanceof GroupCreateUserCell) {
                GroupCreateUserCell cell = (GroupCreateUserCell) holder.itemView;
                Object object = cell.getObject();
                long id = 0;
                if (object != null) {
                    if (object instanceof TLRPC.Chat) {
                        id = -((TLRPC.Chat) object).id;
                    } else {
                        id = ((TLRPC.User) object).id;
                    }
                }
                if (did != id) {
                    z = false;
                }
                cell.setChecked(z, false);
                return;
            }
            ShareDialogCell cell2 = (ShareDialogCell) holder.itemView;
            if (did != cell2.getCurrentDialog()) {
                z = false;
            }
            cell2.setChecked(z, false);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            String status;
            TLObject object;
            long did = MessageObject.getPeerId((TLRPC.Peer) JoinCallAlert.this.chats.get(position));
            if (did > 0) {
                object = MessagesController.getInstance(JoinCallAlert.this.currentAccount).getUser(Long.valueOf(did));
                status = LocaleController.getString("VoipGroupPersonalAccount", NUM);
            } else {
                object = MessagesController.getInstance(JoinCallAlert.this.currentAccount).getChat(Long.valueOf(-did));
                status = null;
            }
            boolean z = false;
            if (JoinCallAlert.this.currentType == 0) {
                ShareDialogCell cell = (ShareDialogCell) holder.itemView;
                if (did == MessageObject.getPeerId(JoinCallAlert.this.selectedPeer)) {
                    z = true;
                }
                cell.setDialog(did, z, (CharSequence) null);
                return;
            }
            GroupCreateUserCell cell2 = (GroupCreateUserCell) holder.itemView;
            if (position != getItemCount() - 1) {
                z = true;
            }
            cell2.setObject(object, (CharSequence) null, status, z);
        }
    }
}
