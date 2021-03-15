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
    public static ArrayList<TLRPC$Peer> cachedChats;
    public static long lastCacheDid;
    public static long lastCacheTime;
    /* access modifiers changed from: private */
    public boolean animationInProgress;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC$Peer> chats;
    private TLRPC$Peer currentPeer;
    /* access modifiers changed from: private */
    public int currentType;
    private BottomSheetCell doneButton;
    /* access modifiers changed from: private */
    public boolean ignoreLayout;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    private TextView messageTextView;
    /* access modifiers changed from: private */
    public int scrollOffsetY;
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

    public static void processDeletedChat(long j) {
        ArrayList<TLRPC$Peer> arrayList = cachedChats;
        if (arrayList != null && j <= 0) {
            int i = 0;
            int size = arrayList.size();
            while (true) {
                if (i >= size) {
                    break;
                } else if (((long) MessageObject.getPeerId(cachedChats.get(i))) == j) {
                    cachedChats.remove(i);
                    break;
                } else {
                    i++;
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
        if (lastCacheDid != ((long) i) || cachedChats == null || SystemClock.elapsedRealtime() - lastCacheTime >= 240000) {
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
            if (lastCacheDid != ((long) i3) || cachedChats == null || SystemClock.elapsedRealtime() - lastCacheTime >= 300000) {
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
    private JoinCallAlert(android.content.Context r18, java.util.ArrayList<org.telegram.tgnet.TLRPC$Peer> r19, int r20, org.telegram.ui.Components.JoinCallAlert.JoinCallAlertDelegate r21) {
        /*
            r17 = this;
            r0 = r17
            r1 = r18
            r2 = r19
            r3 = r20
            r4 = 0
            r0.<init>(r1, r4)
            r0.setApplyBottomPadding(r4)
            r0.chats = r2
            r0.currentType = r3
            android.content.res.Resources r5 = r18.getResources()
            r6 = 2131166005(0x7var_, float:1.7946243E38)
            android.graphics.drawable.Drawable r5 = r5.getDrawable(r6)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            r0.shadowDrawable = r5
            r6 = 2
            if (r3 != r6) goto L_0x006b
            org.telegram.messenger.voip.VoIPService r5 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r5 == 0) goto L_0x0050
            org.telegram.messenger.voip.VoIPService r5 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            int r5 = r5.getSelfId()
            int r7 = r19.size()
            r8 = 0
        L_0x003a:
            if (r8 >= r7) goto L_0x0058
            java.lang.Object r9 = r2.get(r8)
            org.telegram.tgnet.TLRPC$Peer r9 = (org.telegram.tgnet.TLRPC$Peer) r9
            int r10 = org.telegram.messenger.MessageObject.getPeerId(r9)
            if (r10 != r5) goto L_0x004d
            r0.currentPeer = r9
            r0.selectedPeer = r9
            goto L_0x0058
        L_0x004d:
            int r8 = r8 + 1
            goto L_0x003a
        L_0x0050:
            java.lang.Object r5 = r2.get(r4)
            org.telegram.tgnet.TLRPC$Peer r5 = (org.telegram.tgnet.TLRPC$Peer) r5
            r0.selectedPeer = r5
        L_0x0058:
            android.graphics.drawable.Drawable r5 = r0.shadowDrawable
            android.graphics.PorterDuffColorFilter r7 = new android.graphics.PorterDuffColorFilter
            java.lang.String r8 = "voipgroup_inviteMembersBackground"
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
            android.graphics.PorterDuff$Mode r9 = android.graphics.PorterDuff.Mode.MULTIPLY
            r7.<init>(r8, r9)
            r5.setColorFilter(r7)
            goto L_0x0083
        L_0x006b:
            android.graphics.PorterDuffColorFilter r7 = new android.graphics.PorterDuffColorFilter
            java.lang.String r8 = "dialogBackground"
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
            android.graphics.PorterDuff$Mode r9 = android.graphics.PorterDuff.Mode.MULTIPLY
            r7.<init>(r8, r9)
            r5.setColorFilter(r7)
            java.lang.Object r5 = r2.get(r4)
            org.telegram.tgnet.TLRPC$Peer r5 = (org.telegram.tgnet.TLRPC$Peer) r5
            r0.selectedPeer = r5
        L_0x0083:
            org.telegram.ui.Components.JoinCallAlert$1 r5 = new org.telegram.ui.Components.JoinCallAlert$1
            r5.<init>(r1, r2)
            r0.containerView = r5
            r5.setWillNotDraw(r4)
            android.view.ViewGroup r5 = r0.containerView
            int r7 = r0.backgroundPaddingLeft
            r5.setPadding(r7, r4, r7, r4)
            org.telegram.ui.Components.JoinCallAlert$2 r5 = new org.telegram.ui.Components.JoinCallAlert$2
            r5.<init>(r1)
            r0.listView = r5
            androidx.recyclerview.widget.LinearLayoutManager r7 = new androidx.recyclerview.widget.LinearLayoutManager
            android.content.Context r8 = r17.getContext()
            r9 = 1
            r7.<init>(r8, r9, r4)
            r5.setLayoutManager(r7)
            org.telegram.ui.Components.RecyclerListView r5 = r0.listView
            org.telegram.ui.Components.JoinCallAlert$ListAdapter r7 = new org.telegram.ui.Components.JoinCallAlert$ListAdapter
            r7.<init>(r1)
            r5.setAdapter(r7)
            org.telegram.ui.Components.RecyclerListView r5 = r0.listView
            r5.setVerticalScrollBarEnabled(r4)
            org.telegram.ui.Components.RecyclerListView r5 = r0.listView
            r5.setClipToPadding(r4)
            org.telegram.ui.Components.RecyclerListView r5 = r0.listView
            r5.setEnabled(r9)
            org.telegram.ui.Components.RecyclerListView r5 = r0.listView
            java.lang.String r7 = "dialogScrollGlow"
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            r5.setGlowColor(r7)
            org.telegram.ui.Components.RecyclerListView r5 = r0.listView
            org.telegram.ui.Components.JoinCallAlert$3 r7 = new org.telegram.ui.Components.JoinCallAlert$3
            r7.<init>()
            r5.setOnScrollListener(r7)
            org.telegram.ui.Components.RecyclerListView r5 = r0.listView
            org.telegram.ui.Components.-$$Lambda$JoinCallAlert$_jb27R-jfMgYN3OXfa9T7gq3Tjk r7 = new org.telegram.ui.Components.-$$Lambda$JoinCallAlert$_jb27R-jfMgYN3OXfa9T7gq3Tjk
            r7.<init>(r2)
            r5.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r7)
            android.view.ViewGroup r5 = r0.containerView
            org.telegram.ui.Components.RecyclerListView r7 = r0.listView
            r10 = -1
            r11 = -1082130432(0xffffffffbvar_, float:-1.0)
            r12 = 51
            r13 = 0
            r14 = 1120403456(0x42CLASSNAME, float:100.0)
            r15 = 0
            r16 = 1117782016(0x42a00000, float:80.0)
            android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r11, r12, r13, r14, r15, r16)
            r5.addView(r7, r8)
            android.widget.TextView r5 = new android.widget.TextView
            r5.<init>(r1)
            r0.textView = r5
            java.lang.String r7 = "fonts/rmedium.ttf"
            android.graphics.Typeface r7 = org.telegram.messenger.AndroidUtilities.getTypeface(r7)
            r5.setTypeface(r7)
            android.widget.TextView r5 = r0.textView
            r7 = 1101004800(0x41a00000, float:20.0)
            r5.setTextSize(r9, r7)
            if (r3 != r6) goto L_0x011b
            android.widget.TextView r5 = r0.textView
            java.lang.String r7 = "voipgroup_nameText"
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            r5.setTextColor(r7)
            goto L_0x0126
        L_0x011b:
            android.widget.TextView r5 = r0.textView
            java.lang.String r7 = "dialogTextBlack"
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            r5.setTextColor(r7)
        L_0x0126:
            if (r3 != r6) goto L_0x0137
            android.widget.TextView r5 = r0.textView
            r7 = 2131627971(0x7f0e0fc3, float:1.8883221E38)
            java.lang.String r8 = "VoipGroupDisplayAs"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
            r5.setText(r7)
            goto L_0x0156
        L_0x0137:
            if (r3 != 0) goto L_0x0148
            android.widget.TextView r5 = r0.textView
            r7 = 2131628016(0x7f0e0ff0, float:1.8883313E38)
            java.lang.String r8 = "VoipGroupStartAs"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
            r5.setText(r7)
            goto L_0x0156
        L_0x0148:
            android.widget.TextView r5 = r0.textView
            r7 = 2131627987(0x7f0e0fd3, float:1.8883254E38)
            java.lang.String r8 = "VoipGroupJoinAs"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
            r5.setText(r7)
        L_0x0156:
            android.widget.TextView r5 = r0.textView
            r5.setSingleLine(r9)
            android.widget.TextView r5 = r0.textView
            android.text.TextUtils$TruncateAt r7 = android.text.TextUtils.TruncateAt.END
            r5.setEllipsize(r7)
            android.view.ViewGroup r5 = r0.containerView
            android.widget.TextView r7 = r0.textView
            r10 = -2
            r11 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r12 = 51
            r13 = 1102577664(0x41b80000, float:23.0)
            r14 = 0
            r15 = 1102577664(0x41b80000, float:23.0)
            r16 = 0
            android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r11, r12, r13, r14, r15, r16)
            r5.addView(r7, r8)
            android.widget.TextView r5 = new android.widget.TextView
            android.content.Context r7 = r17.getContext()
            r5.<init>(r7)
            r0.messageTextView = r5
            if (r3 != r6) goto L_0x0190
            java.lang.String r3 = "voipgroup_lastSeenText"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r5.setTextColor(r3)
            goto L_0x0199
        L_0x0190:
            java.lang.String r3 = "dialogTextGray3"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r5.setTextColor(r3)
        L_0x0199:
            android.widget.TextView r3 = r0.messageTextView
            r5 = 1096810496(0x41600000, float:14.0)
            r3.setTextSize(r9, r5)
            android.widget.TextView r3 = r0.messageTextView
            r5 = 2131628017(0x7f0e0ff1, float:1.8883315E38)
            java.lang.String r6 = "VoipGroupStartAsInfo"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r3.setText(r5)
            android.widget.TextView r3 = r0.messageTextView
            org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy r5 = new org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy
            r5.<init>()
            r3.setMovementMethod(r5)
            android.widget.TextView r3 = r0.messageTextView
            java.lang.String r5 = "dialogTextLink"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r3.setLinkTextColor(r5)
            android.widget.TextView r3 = r0.messageTextView
            boolean r5 = org.telegram.messenger.LocaleController.isRTL
            if (r5 == 0) goto L_0x01cb
            r5 = 5
            goto L_0x01cc
        L_0x01cb:
            r5 = 3
        L_0x01cc:
            r5 = r5 | 48
            r3.setGravity(r5)
            android.view.ViewGroup r3 = r0.containerView
            android.widget.TextView r5 = r0.messageTextView
            r6 = -2
            r7 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r8 = 51
            r9 = 1102577664(0x41b80000, float:23.0)
            r10 = 0
            r11 = 1102577664(0x41b80000, float:23.0)
            r12 = 1084227584(0x40a00000, float:5.0)
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r7, r8, r9, r10, r11, r12)
            r3.addView(r5, r6)
            org.telegram.ui.Components.JoinCallAlert$BottomSheetCell r3 = new org.telegram.ui.Components.JoinCallAlert$BottomSheetCell
            r3.<init>(r1)
            r0.doneButton = r3
            r1 = 0
            r3.setBackground(r1)
            org.telegram.ui.Components.JoinCallAlert$BottomSheetCell r1 = r0.doneButton
            android.view.View r1 = r1.background
            org.telegram.ui.Components.-$$Lambda$JoinCallAlert$dw6RgDZ-lZPr1L3jdNttUFc_A1o r3 = new org.telegram.ui.Components.-$$Lambda$JoinCallAlert$dw6RgDZ-lZPr1L3jdNttUFc_A1o
            r5 = r21
            r3.<init>(r5, r2)
            r1.setOnClickListener(r3)
            android.view.ViewGroup r1 = r0.containerView
            org.telegram.ui.Components.JoinCallAlert$BottomSheetCell r2 = r0.doneButton
            r5 = -1
            r6 = 1112014848(0x42480000, float:50.0)
            r7 = 83
            r8 = 0
            r9 = 0
            r11 = 0
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r5, r6, r7, r8, r9, r10, r11)
            r1.addView(r2, r3)
            r0.updateDoneButton(r4)
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
        if (this.selectedPeer != this.currentPeer) {
            TLRPC$InputPeer inputPeer = MessagesController.getInstance(this.currentAccount).getInputPeer(MessageObject.getPeerId(this.selectedPeer));
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
            TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(peerId));
            this.doneButton.setText(LocaleController.formatString("VoipGroupContinueAs", NUM, UserObject.getFirstName(user)), z);
            return;
        }
        TLRPC$Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-peerId));
        this.doneButton.setText(LocaleController.formatString("VoipGroupContinueAs", NUM, chat.title), z);
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
            int peerId = MessageObject.getPeerId(JoinCallAlert.this.selectedPeer);
            if (object instanceof TLRPC$Chat) {
                i = -((TLRPC$Chat) object).id;
            } else {
                i = ((TLRPC$User) object).id;
            }
            groupCreateUserCell.setChecked(peerId == i, false);
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
