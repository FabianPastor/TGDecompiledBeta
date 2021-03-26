package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$InputPeer;
import org.telegram.tgnet.TLRPC$Peer;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_phone_getGroupCallJoinAs;
import org.telegram.tgnet.TLRPC$TL_phone_joinAsPeers;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.GroupCreateUserCell;
import org.telegram.ui.Components.JoinCallAlert;
import org.telegram.ui.Components.RecyclerListView;

public class JoinCallAlert extends BottomSheet {
    private static ArrayList<TLRPC$Peer> cachedChats;
    private static long lastCacheDid;
    private static long lastCacheTime;
    private static int lastCachedAccount;
    /* access modifiers changed from: private */
    public boolean animationInProgress;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC$Peer> chats;
    private TLRPC$Peer currentPeer;
    /* access modifiers changed from: private */
    public int currentType;
    private JoinCallAlertDelegate delegate;
    private BottomSheetCell doneButton;
    /* access modifiers changed from: private */
    public boolean ignoreLayout;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    /* access modifiers changed from: private */
    public TextView messageTextView;
    /* access modifiers changed from: private */
    public int scrollOffsetY;
    private TLRPC$InputPeer selectAfterDismiss;
    /* access modifiers changed from: private */
    public TLRPC$Peer selectedPeer;
    /* access modifiers changed from: private */
    public Drawable shadowDrawable;
    private TextView textView;

    public interface JoinCallAlertDelegate {
        void didSelectChat(TLRPC$InputPeer tLRPC$InputPeer, boolean z);
    }

    /* access modifiers changed from: protected */
    public boolean canDismissWithSwipe() {
        return false;
    }

    public static void resetCache() {
        cachedChats = null;
    }

    public static void processDeletedChat(int i, long j) {
        ArrayList<TLRPC$Peer> arrayList;
        if (lastCachedAccount == i && (arrayList = cachedChats) != null && j <= 0) {
            int i2 = 0;
            int size = arrayList.size();
            while (true) {
                if (i2 >= size) {
                    break;
                } else if (((long) MessageObject.getPeerId(cachedChats.get(i2))) == j) {
                    cachedChats.remove(i2);
                    break;
                } else {
                    i2++;
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
        /* access modifiers changed from: private */
        public TextView[] textView = new TextView[2];

        public BottomSheetCell(Context context) {
            super(context);
            View view = new View(context);
            this.background = view;
            view.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed")));
            addView(this.background, LayoutHelper.createFrame(-1, -1.0f, 0, 16.0f, 16.0f, 16.0f, 16.0f));
            for (int i = 0; i < 2; i++) {
                this.textView[i] = new TextView(context);
                this.textView[i].setLines(1);
                this.textView[i].setSingleLine(true);
                this.textView[i].setGravity(1);
                this.textView[i].setEllipsize(TextUtils.TruncateAt.END);
                this.textView[i].setGravity(17);
                this.textView[i].setTextColor(Theme.getColor("featuredStickers_buttonText"));
                this.textView[i].setTextSize(1, 14.0f);
                this.textView[i].setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                addView(this.textView[i], LayoutHelper.createFrame(-2, -2, 17));
                if (i == 1) {
                    this.textView[i].setAlpha(0.0f);
                }
            }
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(80.0f), NUM));
        }

        public void setText(CharSequence charSequence, boolean z) {
            if (!z) {
                this.textView[0].setText(charSequence);
                return;
            }
            this.textView[1].setText(charSequence);
            boolean unused = JoinCallAlert.this.animationInProgress = true;
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setDuration(180);
            animatorSet.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.textView[0], View.ALPHA, new float[]{1.0f, 0.0f}), ObjectAnimator.ofFloat(this.textView[0], View.TRANSLATION_Y, new float[]{0.0f, (float) (-AndroidUtilities.dp(10.0f))}), ObjectAnimator.ofFloat(this.textView[1], View.ALPHA, new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(this.textView[1], View.TRANSLATION_Y, new float[]{(float) AndroidUtilities.dp(10.0f), 0.0f})});
            animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    boolean unused = JoinCallAlert.this.animationInProgress = false;
                    TextView textView = BottomSheetCell.this.textView[0];
                    BottomSheetCell.this.textView[0] = BottomSheetCell.this.textView[1];
                    BottomSheetCell.this.textView[1] = textView;
                }
            });
            animatorSet.start();
        }
    }

    public static void checkFewUsers(Context context, int i, AccountInstance accountInstance, MessagesStorage.BooleanCallback booleanCallback) {
        if (lastCachedAccount != accountInstance.getCurrentAccount() || lastCacheDid != ((long) i) || cachedChats == null || SystemClock.elapsedRealtime() - lastCacheTime >= 240000) {
            AlertDialog alertDialog = new AlertDialog(context, 3);
            TLRPC$TL_phone_getGroupCallJoinAs tLRPC$TL_phone_getGroupCallJoinAs = new TLRPC$TL_phone_getGroupCallJoinAs();
            tLRPC$TL_phone_getGroupCallJoinAs.peer = accountInstance.getMessagesController().getInputPeer(i);
            alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener(accountInstance.getConnectionsManager().sendRequest(tLRPC$TL_phone_getGroupCallJoinAs, new RequestDelegate(i, accountInstance, booleanCallback) {
                public final /* synthetic */ int f$1;
                public final /* synthetic */ AccountInstance f$2;
                public final /* synthetic */ MessagesStorage.BooleanCallback f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    AndroidUtilities.runOnUIThread(new Runnable(tLObject, this.f$1, this.f$2, this.f$3) {
                        public final /* synthetic */ TLObject f$1;
                        public final /* synthetic */ int f$2;
                        public final /* synthetic */ AccountInstance f$3;
                        public final /* synthetic */ MessagesStorage.BooleanCallback f$4;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                            this.f$3 = r4;
                            this.f$4 = r5;
                        }

                        public final void run() {
                            JoinCallAlert.lambda$null$0(AlertDialog.this, this.f$1, this.f$2, this.f$3, this.f$4);
                        }
                    });
                }
            })) {
                public final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void onCancel(DialogInterface dialogInterface) {
                    AccountInstance.this.getConnectionsManager().cancelRequest(this.f$1, true);
                }
            });
            try {
                alertDialog.showDelayed(500);
            } catch (Exception unused) {
            }
        } else {
            boolean z = true;
            if (cachedChats.size() != 1) {
                z = false;
            }
            booleanCallback.run(z);
        }
    }

    static /* synthetic */ void lambda$null$0(AlertDialog alertDialog, TLObject tLObject, int i, AccountInstance accountInstance, MessagesStorage.BooleanCallback booleanCallback) {
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if (tLObject != null) {
            TLRPC$TL_phone_joinAsPeers tLRPC$TL_phone_joinAsPeers = (TLRPC$TL_phone_joinAsPeers) tLObject;
            cachedChats = tLRPC$TL_phone_joinAsPeers.peers;
            lastCacheDid = (long) i;
            lastCacheTime = SystemClock.elapsedRealtime();
            lastCachedAccount = accountInstance.getCurrentAccount();
            boolean z = false;
            accountInstance.getMessagesController().putChats(tLRPC$TL_phone_joinAsPeers.chats, false);
            accountInstance.getMessagesController().putUsers(tLRPC$TL_phone_joinAsPeers.users, false);
            if (tLRPC$TL_phone_joinAsPeers.peers.size() == 1) {
                z = true;
            }
            booleanCallback.run(z);
        }
    }

    public static void open(Context context, int i, AccountInstance accountInstance, BaseFragment baseFragment, int i2, JoinCallAlertDelegate joinCallAlertDelegate) {
        int i3 = i;
        JoinCallAlertDelegate joinCallAlertDelegate2 = joinCallAlertDelegate;
        if (context != null && joinCallAlertDelegate2 != null) {
            if (lastCachedAccount != accountInstance.getCurrentAccount() || lastCacheDid != ((long) i3) || cachedChats == null || SystemClock.elapsedRealtime() - lastCacheTime >= 300000) {
                BaseFragment baseFragment2 = baseFragment;
                int i4 = i2;
                AlertDialog alertDialog = new AlertDialog(context, 3);
                TLRPC$TL_phone_getGroupCallJoinAs tLRPC$TL_phone_getGroupCallJoinAs = new TLRPC$TL_phone_getGroupCallJoinAs();
                tLRPC$TL_phone_getGroupCallJoinAs.peer = accountInstance.getMessagesController().getInputPeer(i);
                alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener(accountInstance.getConnectionsManager().sendRequest(tLRPC$TL_phone_getGroupCallJoinAs, new RequestDelegate(accountInstance, joinCallAlertDelegate, i, context, baseFragment, i2) {
                    public final /* synthetic */ AccountInstance f$1;
                    public final /* synthetic */ JoinCallAlert.JoinCallAlertDelegate f$2;
                    public final /* synthetic */ int f$3;
                    public final /* synthetic */ Context f$4;
                    public final /* synthetic */ BaseFragment f$5;
                    public final /* synthetic */ int f$6;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                        this.f$4 = r5;
                        this.f$5 = r6;
                        this.f$6 = r7;
                    }

                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        AndroidUtilities.runOnUIThread(new Runnable(tLObject, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6) {
                            public final /* synthetic */ TLObject f$1;
                            public final /* synthetic */ AccountInstance f$2;
                            public final /* synthetic */ JoinCallAlert.JoinCallAlertDelegate f$3;
                            public final /* synthetic */ int f$4;
                            public final /* synthetic */ Context f$5;
                            public final /* synthetic */ BaseFragment f$6;
                            public final /* synthetic */ int f$7;

                            {
                                this.f$1 = r2;
                                this.f$2 = r3;
                                this.f$3 = r4;
                                this.f$4 = r5;
                                this.f$5 = r6;
                                this.f$6 = r7;
                                this.f$7 = r8;
                            }

                            public final void run() {
                                JoinCallAlert.lambda$null$3(AlertDialog.this, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
                            }
                        });
                    }
                })) {
                    public final /* synthetic */ int f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onCancel(DialogInterface dialogInterface) {
                        AccountInstance.this.getConnectionsManager().cancelRequest(this.f$1, true);
                    }
                });
                try {
                    alertDialog.showDelayed(500);
                } catch (Exception unused) {
                }
            } else if (cachedChats.size() == 1) {
                joinCallAlertDelegate2.didSelectChat(accountInstance.getMessagesController().getInputPeer(MessageObject.getPeerId(cachedChats.get(0))), false);
            } else {
                BaseFragment baseFragment3 = baseFragment;
                showAlert(context, cachedChats, baseFragment, i2, joinCallAlertDelegate2);
            }
        }
    }

    static /* synthetic */ void lambda$null$3(AlertDialog alertDialog, TLObject tLObject, AccountInstance accountInstance, JoinCallAlertDelegate joinCallAlertDelegate, int i, Context context, BaseFragment baseFragment, int i2) {
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if (tLObject != null) {
            TLRPC$TL_phone_joinAsPeers tLRPC$TL_phone_joinAsPeers = (TLRPC$TL_phone_joinAsPeers) tLObject;
            if (tLRPC$TL_phone_joinAsPeers.peers.size() == 1) {
                joinCallAlertDelegate.didSelectChat(accountInstance.getMessagesController().getInputPeer(MessageObject.getPeerId(tLRPC$TL_phone_joinAsPeers.peers.get(0))), false);
                return;
            }
            cachedChats = tLRPC$TL_phone_joinAsPeers.peers;
            lastCacheDid = (long) i;
            lastCacheTime = SystemClock.elapsedRealtime();
            lastCachedAccount = accountInstance.getCurrentAccount();
            accountInstance.getMessagesController().putChats(tLRPC$TL_phone_joinAsPeers.chats, false);
            accountInstance.getMessagesController().putUsers(tLRPC$TL_phone_joinAsPeers.users, false);
            showAlert(context, tLRPC$TL_phone_joinAsPeers.peers, baseFragment, i2, joinCallAlertDelegate);
        }
    }

    private static void showAlert(Context context, ArrayList<TLRPC$Peer> arrayList, BaseFragment baseFragment, int i, JoinCallAlertDelegate joinCallAlertDelegate) {
        JoinCallAlert joinCallAlert = new JoinCallAlert(context, arrayList, i, joinCallAlertDelegate);
        if (baseFragment == null) {
            joinCallAlert.show();
        } else if (baseFragment.getParentActivity() != null) {
            baseFragment.showDialog(joinCallAlert);
        }
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private JoinCallAlert(android.content.Context r19, java.util.ArrayList<org.telegram.tgnet.TLRPC$Peer> r20, int r21, org.telegram.ui.Components.JoinCallAlert.JoinCallAlertDelegate r22) {
        /*
            r18 = this;
            r0 = r18
            r1 = r19
            r2 = r20
            r3 = r21
            r4 = r22
            r5 = 0
            r0.<init>(r1, r5)
            r0.setApplyBottomPadding(r5)
            r0.chats = r2
            r0.delegate = r4
            r0.currentType = r3
            android.content.res.Resources r6 = r19.getResources()
            r7 = 2131166006(0x7var_, float:1.7946245E38)
            android.graphics.drawable.Drawable r6 = r6.getDrawable(r7)
            android.graphics.drawable.Drawable r6 = r6.mutate()
            r0.shadowDrawable = r6
            r7 = 2
            if (r3 != r7) goto L_0x006f
            org.telegram.messenger.voip.VoIPService r6 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r6 == 0) goto L_0x0054
            org.telegram.messenger.voip.VoIPService r6 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            int r6 = r6.getSelfId()
            int r8 = r20.size()
            r9 = 0
        L_0x003e:
            if (r9 >= r8) goto L_0x005c
            java.lang.Object r10 = r2.get(r9)
            org.telegram.tgnet.TLRPC$Peer r10 = (org.telegram.tgnet.TLRPC$Peer) r10
            int r11 = org.telegram.messenger.MessageObject.getPeerId(r10)
            if (r11 != r6) goto L_0x0051
            r0.currentPeer = r10
            r0.selectedPeer = r10
            goto L_0x005c
        L_0x0051:
            int r9 = r9 + 1
            goto L_0x003e
        L_0x0054:
            java.lang.Object r6 = r2.get(r5)
            org.telegram.tgnet.TLRPC$Peer r6 = (org.telegram.tgnet.TLRPC$Peer) r6
            r0.selectedPeer = r6
        L_0x005c:
            android.graphics.drawable.Drawable r6 = r0.shadowDrawable
            android.graphics.PorterDuffColorFilter r8 = new android.graphics.PorterDuffColorFilter
            java.lang.String r9 = "voipgroup_inviteMembersBackground"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            android.graphics.PorterDuff$Mode r10 = android.graphics.PorterDuff.Mode.MULTIPLY
            r8.<init>(r9, r10)
            r6.setColorFilter(r8)
            goto L_0x0087
        L_0x006f:
            android.graphics.PorterDuffColorFilter r8 = new android.graphics.PorterDuffColorFilter
            java.lang.String r9 = "dialogBackground"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            android.graphics.PorterDuff$Mode r10 = android.graphics.PorterDuff.Mode.MULTIPLY
            r8.<init>(r9, r10)
            r6.setColorFilter(r8)
            java.lang.Object r6 = r2.get(r5)
            org.telegram.tgnet.TLRPC$Peer r6 = (org.telegram.tgnet.TLRPC$Peer) r6
            r0.selectedPeer = r6
        L_0x0087:
            org.telegram.ui.Components.JoinCallAlert$1 r6 = new org.telegram.ui.Components.JoinCallAlert$1
            r6.<init>(r1, r2)
            r0.containerView = r6
            r6.setWillNotDraw(r5)
            android.view.ViewGroup r6 = r0.containerView
            int r8 = r0.backgroundPaddingLeft
            r6.setPadding(r8, r5, r8, r5)
            org.telegram.ui.Components.JoinCallAlert$2 r6 = new org.telegram.ui.Components.JoinCallAlert$2
            r6.<init>(r1)
            r0.listView = r6
            androidx.recyclerview.widget.LinearLayoutManager r8 = new androidx.recyclerview.widget.LinearLayoutManager
            android.content.Context r9 = r18.getContext()
            r10 = 1
            r8.<init>(r9, r10, r5)
            r6.setLayoutManager(r8)
            org.telegram.ui.Components.RecyclerListView r6 = r0.listView
            org.telegram.ui.Components.JoinCallAlert$ListAdapter r8 = new org.telegram.ui.Components.JoinCallAlert$ListAdapter
            r8.<init>(r1)
            r6.setAdapter(r8)
            org.telegram.ui.Components.RecyclerListView r6 = r0.listView
            r6.setVerticalScrollBarEnabled(r5)
            org.telegram.ui.Components.RecyclerListView r6 = r0.listView
            r6.setClipToPadding(r5)
            org.telegram.ui.Components.RecyclerListView r6 = r0.listView
            r6.setEnabled(r10)
            org.telegram.ui.Components.RecyclerListView r6 = r0.listView
            java.lang.String r8 = "dialogScrollGlow"
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
            r6.setGlowColor(r8)
            org.telegram.ui.Components.RecyclerListView r6 = r0.listView
            org.telegram.ui.Components.JoinCallAlert$3 r8 = new org.telegram.ui.Components.JoinCallAlert$3
            r8.<init>()
            r6.setOnScrollListener(r8)
            org.telegram.ui.Components.RecyclerListView r6 = r0.listView
            org.telegram.ui.Components.-$$Lambda$JoinCallAlert$_jb27R-jfMgYN3OXfa9T7gq3Tjk r8 = new org.telegram.ui.Components.-$$Lambda$JoinCallAlert$_jb27R-jfMgYN3OXfa9T7gq3Tjk
            r8.<init>(r2)
            r6.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r8)
            android.view.ViewGroup r6 = r0.containerView
            org.telegram.ui.Components.RecyclerListView r8 = r0.listView
            r11 = -1
            r12 = -1082130432(0xffffffffbvar_, float:-1.0)
            r13 = 51
            r14 = 0
            r15 = 1120403456(0x42CLASSNAME, float:100.0)
            r16 = 0
            r17 = 1117782016(0x42a00000, float:80.0)
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r12, r13, r14, r15, r16, r17)
            r6.addView(r8, r9)
            android.widget.TextView r6 = new android.widget.TextView
            r6.<init>(r1)
            r0.textView = r6
            java.lang.String r8 = "fonts/rmedium.ttf"
            android.graphics.Typeface r8 = org.telegram.messenger.AndroidUtilities.getTypeface(r8)
            r6.setTypeface(r8)
            android.widget.TextView r6 = r0.textView
            r8 = 1101004800(0x41a00000, float:20.0)
            r6.setTextSize(r10, r8)
            if (r3 != r7) goto L_0x0120
            android.widget.TextView r6 = r0.textView
            java.lang.String r8 = "voipgroup_nameText"
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
            r6.setTextColor(r8)
            goto L_0x012b
        L_0x0120:
            android.widget.TextView r6 = r0.textView
            java.lang.String r8 = "dialogTextBlack"
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
            r6.setTextColor(r8)
        L_0x012b:
            if (r3 != r7) goto L_0x013c
            android.widget.TextView r6 = r0.textView
            r8 = 2131627973(0x7f0e0fc5, float:1.8883226E38)
            java.lang.String r9 = "VoipGroupDisplayAs"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r6.setText(r8)
            goto L_0x015b
        L_0x013c:
            if (r3 != 0) goto L_0x014d
            android.widget.TextView r6 = r0.textView
            r8 = 2131628024(0x7f0e0ff8, float:1.888333E38)
            java.lang.String r9 = "VoipGroupStartAs"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r6.setText(r8)
            goto L_0x015b
        L_0x014d:
            android.widget.TextView r6 = r0.textView
            r8 = 2131627990(0x7f0e0fd6, float:1.888326E38)
            java.lang.String r9 = "VoipGroupJoinAs"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r6.setText(r8)
        L_0x015b:
            android.widget.TextView r6 = r0.textView
            r6.setSingleLine(r10)
            android.widget.TextView r6 = r0.textView
            android.text.TextUtils$TruncateAt r8 = android.text.TextUtils.TruncateAt.END
            r6.setEllipsize(r8)
            android.view.ViewGroup r6 = r0.containerView
            android.widget.TextView r8 = r0.textView
            r11 = -2
            r12 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r13 = 51
            r14 = 1102577664(0x41b80000, float:23.0)
            r15 = 0
            r16 = 1102577664(0x41b80000, float:23.0)
            r17 = 0
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r12, r13, r14, r15, r16, r17)
            r6.addView(r8, r9)
            android.widget.TextView r6 = new android.widget.TextView
            android.content.Context r8 = r18.getContext()
            r6.<init>(r8)
            r0.messageTextView = r6
            if (r3 != r7) goto L_0x0195
            java.lang.String r3 = "voipgroup_lastSeenText"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r6.setTextColor(r3)
            goto L_0x019e
        L_0x0195:
            java.lang.String r3 = "dialogTextGray3"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r6.setTextColor(r3)
        L_0x019e:
            android.widget.TextView r3 = r0.messageTextView
            r6 = 1096810496(0x41600000, float:14.0)
            r3.setTextSize(r10, r6)
            int r3 = r20.size()
            r6 = 0
        L_0x01aa:
            if (r6 >= r3) goto L_0x01d5
            java.lang.Object r7 = r2.get(r6)
            org.telegram.tgnet.TLRPC$Peer r7 = (org.telegram.tgnet.TLRPC$Peer) r7
            int r7 = org.telegram.messenger.MessageObject.getPeerId(r7)
            if (r7 >= 0) goto L_0x01d2
            int r8 = r0.currentAccount
            org.telegram.messenger.MessagesController r8 = org.telegram.messenger.MessagesController.getInstance(r8)
            int r7 = -r7
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            org.telegram.tgnet.TLRPC$Chat r7 = r8.getChat(r7)
            boolean r8 = org.telegram.messenger.ChatObject.isChannel(r7)
            if (r8 == 0) goto L_0x01d6
            boolean r7 = r7.megagroup
            if (r7 == 0) goto L_0x01d2
            goto L_0x01d6
        L_0x01d2:
            int r6 = r6 + 1
            goto L_0x01aa
        L_0x01d5:
            r10 = 0
        L_0x01d6:
            if (r10 == 0) goto L_0x01e7
            android.widget.TextView r3 = r0.messageTextView
            r6 = 2131628026(0x7f0e0ffa, float:1.8883333E38)
            java.lang.String r7 = "VoipGroupStartAsInfoGroup"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r3.setText(r6)
            goto L_0x01f5
        L_0x01e7:
            android.widget.TextView r3 = r0.messageTextView
            r6 = 2131628025(0x7f0e0ff9, float:1.8883331E38)
            java.lang.String r7 = "VoipGroupStartAsInfo"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r3.setText(r6)
        L_0x01f5:
            android.widget.TextView r3 = r0.messageTextView
            org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy r6 = new org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy
            r6.<init>()
            r3.setMovementMethod(r6)
            android.widget.TextView r3 = r0.messageTextView
            java.lang.String r6 = "dialogTextLink"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r3.setLinkTextColor(r6)
            android.widget.TextView r3 = r0.messageTextView
            boolean r6 = org.telegram.messenger.LocaleController.isRTL
            if (r6 == 0) goto L_0x0212
            r6 = 5
            goto L_0x0213
        L_0x0212:
            r6 = 3
        L_0x0213:
            r6 = r6 | 48
            r3.setGravity(r6)
            android.view.ViewGroup r3 = r0.containerView
            android.widget.TextView r6 = r0.messageTextView
            r7 = -2
            r8 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r9 = 51
            r10 = 1102577664(0x41b80000, float:23.0)
            r11 = 0
            r12 = 1102577664(0x41b80000, float:23.0)
            r13 = 1084227584(0x40a00000, float:5.0)
            android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r8, r9, r10, r11, r12, r13)
            r3.addView(r6, r7)
            org.telegram.ui.Components.JoinCallAlert$BottomSheetCell r3 = new org.telegram.ui.Components.JoinCallAlert$BottomSheetCell
            r3.<init>(r1)
            r0.doneButton = r3
            r1 = 0
            r3.setBackground(r1)
            org.telegram.ui.Components.JoinCallAlert$BottomSheetCell r1 = r0.doneButton
            android.view.View r1 = r1.background
            org.telegram.ui.Components.-$$Lambda$JoinCallAlert$dw6RgDZ-lZPr1L3jdNttUFc_A1o r3 = new org.telegram.ui.Components.-$$Lambda$JoinCallAlert$dw6RgDZ-lZPr1L3jdNttUFc_A1o
            r3.<init>(r4, r2)
            r1.setOnClickListener(r3)
            android.view.ViewGroup r1 = r0.containerView
            org.telegram.ui.Components.JoinCallAlert$BottomSheetCell r2 = r0.doneButton
            r6 = -1
            r7 = 1112014848(0x42480000, float:50.0)
            r8 = 83
            r9 = 0
            r10 = 0
            r12 = 0
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r7, r8, r9, r10, r11, r12)
            r1.addView(r2, r3)
            r0.updateDoneButton(r5)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.JoinCallAlert.<init>(android.content.Context, java.util.ArrayList, int, org.telegram.ui.Components.JoinCallAlert$JoinCallAlertDelegate):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$6 */
    public /* synthetic */ void lambda$new$6$JoinCallAlert(ArrayList arrayList, View view, int i) {
        if (!this.animationInProgress && arrayList.get(i) != this.selectedPeer) {
            GroupCreateUserCell groupCreateUserCell = (GroupCreateUserCell) view;
            this.selectedPeer = (TLRPC$Peer) arrayList.get(i);
            groupCreateUserCell.setChecked(true, true);
            int childCount = this.listView.getChildCount();
            for (int i2 = 0; i2 < childCount; i2++) {
                GroupCreateUserCell groupCreateUserCell2 = (GroupCreateUserCell) this.listView.getChildAt(i2);
                if (groupCreateUserCell2 != groupCreateUserCell) {
                    groupCreateUserCell2.setChecked(false, true);
                }
            }
            updateDoneButton(true);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$7 */
    public /* synthetic */ void lambda$new$7$JoinCallAlert(JoinCallAlertDelegate joinCallAlertDelegate, ArrayList arrayList, View view) {
        TLRPC$InputPeer inputPeer = MessagesController.getInstance(this.currentAccount).getInputPeer(MessageObject.getPeerId(this.selectedPeer));
        if (this.currentType != 2) {
            this.selectAfterDismiss = inputPeer;
        } else if (this.selectedPeer != this.currentPeer) {
            boolean z = true;
            if (arrayList.size() <= 1) {
                z = false;
            }
            joinCallAlertDelegate.didSelectChat(inputPeer, z);
        }
        dismiss();
    }

    private void updateDoneButton(boolean z) {
        int peerId = MessageObject.getPeerId(this.selectedPeer);
        if (peerId > 0) {
            this.doneButton.setText(LocaleController.formatString("VoipGroupContinueAs", NUM, UserObject.getFirstName(MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(peerId)))), z);
            return;
        }
        TLRPC$Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-peerId));
        BottomSheetCell bottomSheetCell = this.doneButton;
        Object[] objArr = new Object[1];
        objArr[0] = chat != null ? chat.title : "";
        bottomSheetCell.setText(LocaleController.formatString("VoipGroupContinueAs", NUM, objArr), z);
    }

    /* access modifiers changed from: private */
    public void updateLayout() {
        if (this.listView.getChildCount() <= 0) {
            RecyclerListView recyclerListView = this.listView;
            int paddingTop = recyclerListView.getPaddingTop();
            this.scrollOffsetY = paddingTop;
            recyclerListView.setTopGlowOffset(paddingTop);
            this.containerView.invalidate();
            return;
        }
        int i = 0;
        View childAt = this.listView.getChildAt(0);
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(childAt);
        int top = childAt.getTop() - AndroidUtilities.dp(9.0f);
        if (top > 0 && holder != null && holder.getAdapterPosition() == 0) {
            i = top;
        }
        if (this.scrollOffsetY != i) {
            this.textView.setTranslationY((float) (AndroidUtilities.dp(19.0f) + top));
            this.messageTextView.setTranslationY((float) (top + AndroidUtilities.dp(56.0f)));
            RecyclerListView recyclerListView2 = this.listView;
            this.scrollOffsetY = i;
            recyclerListView2.setTopGlowOffset(i);
            this.containerView.invalidate();
        }
    }

    public void dismissInternal() {
        super.dismissInternal();
        TLRPC$InputPeer tLRPC$InputPeer = this.selectAfterDismiss;
        if (tLRPC$InputPeer != null) {
            JoinCallAlertDelegate joinCallAlertDelegate = this.delegate;
            boolean z = true;
            if (this.chats.size() <= 1) {
                z = false;
            }
            joinCallAlertDelegate.didSelectChat(tLRPC$InputPeer, z);
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context context;

        public int getItemViewType(int i) {
            return 0;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return true;
        }

        public ListAdapter(Context context2) {
            this.context = context2;
        }

        public int getItemCount() {
            return JoinCallAlert.this.chats.size();
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new RecyclerListView.Holder(new GroupCreateUserCell(this.context, 2, 0, false, JoinCallAlert.this.currentType == 2));
        }

        public void onViewAttachedToWindow(RecyclerView.ViewHolder viewHolder) {
            int i;
            viewHolder.getAdapterPosition();
            GroupCreateUserCell groupCreateUserCell = (GroupCreateUserCell) viewHolder.itemView;
            Object object = groupCreateUserCell.getObject();
            if (object != null) {
                int peerId = MessageObject.getPeerId(JoinCallAlert.this.selectedPeer);
                if (object instanceof TLRPC$Chat) {
                    i = -((TLRPC$Chat) object).id;
                } else {
                    i = ((TLRPC$User) object).id;
                }
                groupCreateUserCell.setChecked(peerId == i, false);
            }
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            String str;
            TLObject tLObject;
            GroupCreateUserCell groupCreateUserCell = (GroupCreateUserCell) viewHolder.itemView;
            int peerId = MessageObject.getPeerId((TLRPC$Peer) JoinCallAlert.this.chats.get(i));
            if (peerId > 0) {
                tLObject = MessagesController.getInstance(JoinCallAlert.this.currentAccount).getUser(Integer.valueOf(peerId));
                str = LocaleController.getString("VoipGroupPersonalAccount", NUM);
            } else {
                tLObject = MessagesController.getInstance(JoinCallAlert.this.currentAccount).getChat(Integer.valueOf(-peerId));
                str = null;
            }
            boolean z = true;
            if (i == getItemCount() - 1) {
                z = false;
            }
            groupCreateUserCell.setObject(tLObject, (CharSequence) null, str, z);
        }
    }
}
