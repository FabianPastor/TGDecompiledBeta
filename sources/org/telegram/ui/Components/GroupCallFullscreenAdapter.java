package org.telegram.ui.Components;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$TL_groupCallParticipant;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.GroupCallUserCell;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.voip.GroupCallGridCell;
import org.telegram.ui.Components.voip.GroupCallMiniTextureView;
import org.telegram.ui.Components.voip.GroupCallRenderersContainer;
import org.telegram.ui.Components.voip.GroupCallStatusIcon;
import org.telegram.ui.GroupCallActivity;

public class GroupCallFullscreenAdapter extends RecyclerListView.SelectionAdapter {
    /* access modifiers changed from: private */
    public final GroupCallActivity activity;
    /* access modifiers changed from: private */
    public ArrayList<GroupCallMiniTextureView> attachedRenderers;
    /* access modifiers changed from: private */
    public final int currentAccount;
    /* access modifiers changed from: private */
    public ChatObject.Call groupCall;
    /* access modifiers changed from: private */
    public final ArrayList<TLRPC$TL_groupCallParticipant> participants = new ArrayList<>();
    /* access modifiers changed from: private */
    public GroupCallRenderersContainer renderersContainer;
    /* access modifiers changed from: private */
    public final ArrayList<ChatObject.VideoParticipant> videoParticipants = new ArrayList<>();
    /* access modifiers changed from: private */
    public boolean visible = false;

    public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
        return false;
    }

    public GroupCallFullscreenAdapter(ChatObject.Call call, int i, GroupCallActivity groupCallActivity) {
        this.groupCall = call;
        this.currentAccount = i;
        this.activity = groupCallActivity;
    }

    public void setRenderersPool(ArrayList<GroupCallMiniTextureView> arrayList, GroupCallRenderersContainer groupCallRenderersContainer) {
        this.attachedRenderers = arrayList;
        this.renderersContainer = groupCallRenderersContainer;
    }

    public void setGroupCall(ChatObject.Call call) {
        this.groupCall = call;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new RecyclerListView.Holder(new GroupCallUserCell(viewGroup.getContext()));
    }

    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant;
        ChatObject.VideoParticipant videoParticipant;
        GroupCallUserCell groupCallUserCell = (GroupCallUserCell) viewHolder.itemView;
        ChatObject.VideoParticipant videoParticipant2 = groupCallUserCell.videoParticipant;
        if (i < this.videoParticipants.size()) {
            videoParticipant = this.videoParticipants.get(i);
            tLRPC$TL_groupCallParticipant = this.videoParticipants.get(i).participant;
        } else {
            videoParticipant = null;
            tLRPC$TL_groupCallParticipant = this.participants.get(i - this.videoParticipants.size());
        }
        groupCallUserCell.setParticipant(videoParticipant, tLRPC$TL_groupCallParticipant);
        if (videoParticipant2 != null && !videoParticipant2.equals(videoParticipant) && groupCallUserCell.attached && groupCallUserCell.getRenderer() != null) {
            groupCallUserCell.attachRenderer(false);
            if (videoParticipant != null) {
                groupCallUserCell.attachRenderer(true);
            }
        } else if (!groupCallUserCell.attached) {
        } else {
            if (groupCallUserCell.getRenderer() == null && videoParticipant != null && this.visible) {
                groupCallUserCell.attachRenderer(true);
            } else if (groupCallUserCell.getRenderer() != null && videoParticipant == null) {
                groupCallUserCell.attachRenderer(false);
            }
        }
    }

    public int getItemCount() {
        return this.videoParticipants.size() + this.participants.size();
    }

    public void setVisibility(RecyclerListView recyclerListView, boolean z) {
        this.visible = z;
        for (int i = 0; i < recyclerListView.getChildCount(); i++) {
            View childAt = recyclerListView.getChildAt(i);
            if (childAt instanceof GroupCallUserCell) {
                GroupCallUserCell groupCallUserCell = (GroupCallUserCell) childAt;
                if (groupCallUserCell.getVideoParticipant() != null) {
                    groupCallUserCell.attachRenderer(z);
                }
            }
        }
    }

    public void scrollTo(ChatObject.VideoParticipant videoParticipant, RecyclerListView recyclerListView) {
        for (int i = 0; i < this.participants.size(); i++) {
            if (this.videoParticipants.get(i).equals(videoParticipant)) {
                ((LinearLayoutManager) recyclerListView.getLayoutManager()).scrollToPositionWithOffset(i, AndroidUtilities.dp(13.0f));
                return;
            }
        }
    }

    public class GroupCallUserCell extends FrameLayout implements GroupCallStatusIcon.Callback {
        boolean attached;
        AvatarDrawable avatarDrawable = new AvatarDrawable();
        private BackupImageView avatarImageView;
        GroupCallUserCell.AvatarWavesDrawable avatarWavesDrawable = new GroupCallUserCell.AvatarWavesDrawable(AndroidUtilities.dp(26.0f), AndroidUtilities.dp(29.0f));
        Paint backgroundPaint = new Paint(1);
        ValueAnimator colorAnimator;
        private TLRPC$Chat currentChat;
        private TLRPC$User currentUser;
        String drawingName;
        boolean hasAvatar;
        int lastColor;
        int lastWavesColor;
        RLottieImageView muteButton;
        String name;
        int nameWidth;
        TLRPC$TL_groupCallParticipant participant;
        int peerId;
        float progress = 1.0f;
        GroupCallMiniTextureView renderer;
        boolean selected;
        Paint selectionPaint = new Paint(1);
        float selectionProgress;
        boolean skipInvalidate;
        GroupCallStatusIcon statusIcon;
        TextPaint textPaint = new TextPaint(1);
        ChatObject.VideoParticipant videoParticipant;

        public GroupCallUserCell(Context context) {
            super(context);
            this.avatarDrawable.setTextSize((int) (((float) AndroidUtilities.dp(18.0f)) / 1.15f));
            BackupImageView backupImageView = new BackupImageView(context);
            this.avatarImageView = backupImageView;
            backupImageView.setRoundRadius(AndroidUtilities.dp(20.0f));
            addView(this.avatarImageView, LayoutHelper.createFrame(40, 40.0f, 1, 0.0f, 9.0f, 0.0f, 9.0f));
            setWillNotDraw(false);
            this.backgroundPaint.setColor(Theme.getColor("voipgroup_listViewBackground"));
            this.selectionPaint.setColor(Theme.getColor("voipgroup_speakingText"));
            this.selectionPaint.setStyle(Paint.Style.STROKE);
            this.selectionPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
            this.textPaint.setColor(-1);
            AnonymousClass1 r0 = new RLottieImageView(context, GroupCallFullscreenAdapter.this) {
                public void invalidate() {
                    super.invalidate();
                    GroupCallUserCell.this.invalidate();
                }
            };
            this.muteButton = r0;
            r0.setScaleType(ImageView.ScaleType.CENTER);
            addView(this.muteButton, LayoutHelper.createFrame(24, 24.0f));
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            this.textPaint.setTextSize((float) AndroidUtilities.dp(12.0f));
            if (this.name != null) {
                int min = (int) Math.min((float) AndroidUtilities.dp(46.0f), this.textPaint.measureText(this.name));
                this.nameWidth = min;
                this.drawingName = TextUtils.ellipsize(this.name, this.textPaint, (float) min, TextUtils.TruncateAt.END).toString();
            }
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(80.0f), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(80.0f), NUM));
        }

        public void setParticipant(ChatObject.VideoParticipant videoParticipant2, TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant) {
            this.videoParticipant = videoParticipant2;
            this.participant = tLRPC$TL_groupCallParticipant;
            int i = this.peerId;
            int peerId2 = MessageObject.getPeerId(tLRPC$TL_groupCallParticipant.peer);
            this.peerId = peerId2;
            boolean z = false;
            if (peerId2 > 0) {
                TLRPC$User user = AccountInstance.getInstance(GroupCallFullscreenAdapter.this.currentAccount).getMessagesController().getUser(Integer.valueOf(this.peerId));
                this.currentUser = user;
                this.currentChat = null;
                this.avatarDrawable.setInfo(user);
                this.name = UserObject.getFirstName(this.currentUser);
                this.avatarImageView.getImageReceiver().setCurrentAccount(GroupCallFullscreenAdapter.this.currentAccount);
                ImageLocation forUser = ImageLocation.getForUser(this.currentUser, 1);
                this.hasAvatar = forUser != null;
                this.avatarImageView.setImage(forUser, "50_50", (Drawable) this.avatarDrawable, (Object) this.currentUser);
            } else {
                TLRPC$Chat chat = AccountInstance.getInstance(GroupCallFullscreenAdapter.this.currentAccount).getMessagesController().getChat(Integer.valueOf(-this.peerId));
                this.currentChat = chat;
                this.currentUser = null;
                this.avatarDrawable.setInfo(chat);
                TLRPC$Chat tLRPC$Chat = this.currentChat;
                if (tLRPC$Chat != null) {
                    this.name = tLRPC$Chat.title;
                    this.avatarImageView.getImageReceiver().setCurrentAccount(GroupCallFullscreenAdapter.this.currentAccount);
                    ImageLocation forChat = ImageLocation.getForChat(this.currentChat, 1);
                    this.hasAvatar = forChat != null;
                    this.avatarImageView.setImage(forChat, "50_50", (Drawable) this.avatarDrawable, (Object) this.currentChat);
                }
            }
            boolean z2 = i == this.peerId;
            if (videoParticipant2 == null) {
                if (GroupCallFullscreenAdapter.this.renderersContainer.fullscreenPeerId == MessageObject.getPeerId(tLRPC$TL_groupCallParticipant.peer)) {
                    z = true;
                }
                this.selected = z;
            } else if (GroupCallFullscreenAdapter.this.renderersContainer.fullscreenParticipant != null) {
                this.selected = GroupCallFullscreenAdapter.this.renderersContainer.fullscreenParticipant.equals(videoParticipant2);
            } else {
                this.selected = false;
            }
            if (!z2) {
                setSelectedProgress(this.selected ? 1.0f : 0.0f);
            }
            GroupCallStatusIcon groupCallStatusIcon = this.statusIcon;
            if (groupCallStatusIcon != null) {
                groupCallStatusIcon.setParticipant(tLRPC$TL_groupCallParticipant, z2);
                updateState(z2);
            }
        }

        public void setAlpha(float f) {
            super.setAlpha(f);
        }

        public void setProgressToFullscreen(float f) {
            if (this.progress != f) {
                this.progress = f;
                if (f == 1.0f) {
                    this.avatarImageView.setTranslationY(0.0f);
                    this.avatarImageView.setScaleX(1.0f);
                    this.avatarImageView.setScaleY(1.0f);
                    this.backgroundPaint.setAlpha(255);
                    invalidate();
                    GroupCallMiniTextureView groupCallMiniTextureView = this.renderer;
                    if (groupCallMiniTextureView != null) {
                        groupCallMiniTextureView.invalidate();
                        return;
                    }
                    return;
                }
                float top = (((float) this.avatarImageView.getTop()) + (((float) this.avatarImageView.getMeasuredHeight()) / 2.0f)) - (((float) getMeasuredHeight()) / 2.0f);
                float f2 = 1.0f - f;
                float dp = ((((float) AndroidUtilities.dp(46.0f)) / ((float) AndroidUtilities.dp(40.0f))) * f2) + (1.0f * f);
                this.avatarImageView.setTranslationY((-top) * f2);
                this.avatarImageView.setScaleX(dp);
                this.avatarImageView.setScaleY(dp);
                this.backgroundPaint.setAlpha((int) (f * 255.0f));
                invalidate();
                GroupCallMiniTextureView groupCallMiniTextureView2 = this.renderer;
                if (groupCallMiniTextureView2 != null) {
                    groupCallMiniTextureView2.invalidate();
                }
            }
        }

        /* access modifiers changed from: protected */
        public void dispatchDraw(Canvas canvas) {
            GroupCallMiniTextureView groupCallMiniTextureView = this.renderer;
            if (groupCallMiniTextureView == null || !groupCallMiniTextureView.isFullyVisible() || GroupCallFullscreenAdapter.this.activity.drawingForBlur) {
                if (this.progress > 0.0f) {
                    float measuredWidth = (((float) getMeasuredWidth()) / 2.0f) * (1.0f - this.progress);
                    RectF rectF = AndroidUtilities.rectTmp;
                    rectF.set(measuredWidth, measuredWidth, ((float) getMeasuredWidth()) - measuredWidth, ((float) getMeasuredHeight()) - measuredWidth);
                    canvas.drawRoundRect(rectF, (float) AndroidUtilities.dp(13.0f), (float) AndroidUtilities.dp(13.0f), this.backgroundPaint);
                    drawSelection(canvas);
                }
                float x = this.avatarImageView.getX() + ((float) (this.avatarImageView.getMeasuredWidth() / 2));
                float y = this.avatarImageView.getY() + ((float) (this.avatarImageView.getMeasuredHeight() / 2));
                this.avatarWavesDrawable.update();
                this.avatarWavesDrawable.draw(canvas, x, y, this);
                float dp = ((float) AndroidUtilities.dp(46.0f)) / ((float) AndroidUtilities.dp(40.0f));
                float f = this.progress;
                float f2 = (dp * (1.0f - f)) + (f * 1.0f);
                this.avatarImageView.setScaleX(this.avatarWavesDrawable.getAvatarScale() * f2);
                this.avatarImageView.setScaleY(this.avatarWavesDrawable.getAvatarScale() * f2);
                super.dispatchDraw(canvas);
                return;
            }
            drawSelection(canvas);
        }

        /* JADX WARNING: Removed duplicated region for block: B:19:0x003a  */
        /* JADX WARNING: Removed duplicated region for block: B:21:? A[RETURN, SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private void drawSelection(android.graphics.Canvas r7) {
            /*
                r6 = this;
                boolean r0 = r6.selected
                r1 = 1037726734(0x3dda740e, float:0.10666667)
                r2 = 0
                r3 = 1065353216(0x3var_, float:1.0)
                if (r0 == 0) goto L_0x001f
                float r4 = r6.selectionProgress
                int r5 = (r4 > r3 ? 1 : (r4 == r3 ? 0 : -1))
                if (r5 == 0) goto L_0x001f
                float r4 = r4 + r1
                int r0 = (r4 > r3 ? 1 : (r4 == r3 ? 0 : -1))
                if (r0 <= 0) goto L_0x0018
                r4 = 1065353216(0x3var_, float:1.0)
                goto L_0x001b
            L_0x0018:
                r6.invalidate()
            L_0x001b:
                r6.setSelectedProgress(r4)
                goto L_0x0034
            L_0x001f:
                if (r0 != 0) goto L_0x0034
                float r0 = r6.selectionProgress
                int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
                if (r4 == 0) goto L_0x0034
                float r0 = r0 - r1
                int r1 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
                if (r1 >= 0) goto L_0x002e
                r0 = 0
                goto L_0x0031
            L_0x002e:
                r6.invalidate()
            L_0x0031:
                r6.setSelectedProgress(r0)
            L_0x0034:
                float r0 = r6.selectionProgress
                int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
                if (r0 <= 0) goto L_0x007a
                int r0 = r6.getMeasuredWidth()
                float r0 = (float) r0
                r1 = 1073741824(0x40000000, float:2.0)
                float r0 = r0 / r1
                float r2 = r6.progress
                float r3 = r3 - r2
                float r0 = r0 * r3
                android.graphics.RectF r2 = org.telegram.messenger.AndroidUtilities.rectTmp
                int r3 = r6.getMeasuredWidth()
                float r3 = (float) r3
                float r3 = r3 - r0
                int r4 = r6.getMeasuredHeight()
                float r4 = (float) r4
                float r4 = r4 - r0
                r2.set(r0, r0, r3, r4)
                android.graphics.Paint r0 = r6.selectionPaint
                float r0 = r0.getStrokeWidth()
                float r0 = r0 / r1
                android.graphics.Paint r3 = r6.selectionPaint
                float r3 = r3.getStrokeWidth()
                float r3 = r3 / r1
                r2.inset(r0, r3)
                r0 = 1094713344(0x41400000, float:12.0)
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r0)
                float r1 = (float) r1
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
                float r0 = (float) r0
                android.graphics.Paint r3 = r6.selectionPaint
                r7.drawRoundRect(r2, r1, r0, r3)
            L_0x007a:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.GroupCallFullscreenAdapter.GroupCallUserCell.drawSelection(android.graphics.Canvas):void");
        }

        private void setSelectedProgress(float f) {
            if (this.selectionProgress != f) {
                this.selectionProgress = f;
                this.selectionPaint.setAlpha((int) (f * 255.0f));
            }
        }

        public int getPeerId() {
            return this.peerId;
        }

        public BackupImageView getAvatarImageView() {
            return this.avatarImageView;
        }

        public TLRPC$TL_groupCallParticipant getParticipant() {
            return this.participant;
        }

        public ChatObject.VideoParticipant getVideoParticipant() {
            return this.videoParticipant;
        }

        /* access modifiers changed from: protected */
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            if (GroupCallFullscreenAdapter.this.visible && this.videoParticipant != null) {
                attachRenderer(true);
            }
            this.attached = true;
            if (GroupCallFullscreenAdapter.this.activity.statusIconPool.size() > 0) {
                this.statusIcon = GroupCallFullscreenAdapter.this.activity.statusIconPool.remove(GroupCallFullscreenAdapter.this.activity.statusIconPool.size() - 1);
            } else {
                this.statusIcon = new GroupCallStatusIcon();
            }
            this.statusIcon.setCallback(this);
            this.statusIcon.setImageView(this.muteButton);
            this.statusIcon.setParticipant(this.participant, false);
            updateState(false);
            this.avatarWavesDrawable.setShowWaves(this.statusIcon.isSpeaking(), this);
            if (!this.statusIcon.isSpeaking()) {
                this.avatarWavesDrawable.setAmplitude(0.0d);
            }
        }

        /* access modifiers changed from: protected */
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            attachRenderer(false);
            this.attached = false;
            if (this.statusIcon != null) {
                GroupCallFullscreenAdapter.this.activity.statusIconPool.add(this.statusIcon);
                this.statusIcon.setImageView((RLottieImageView) null);
                this.statusIcon.setCallback((GroupCallStatusIcon.Callback) null);
            }
            this.statusIcon = null;
        }

        public void attachRenderer(boolean z) {
            if (!GroupCallFullscreenAdapter.this.activity.isDismissed()) {
                if (z && this.renderer == null) {
                    this.renderer = GroupCallMiniTextureView.getOrCreate(GroupCallFullscreenAdapter.this.attachedRenderers, GroupCallFullscreenAdapter.this.renderersContainer, (GroupCallGridCell) null, this, (GroupCallGridCell) null, this.videoParticipant, GroupCallFullscreenAdapter.this.groupCall, GroupCallFullscreenAdapter.this.activity);
                } else if (!z) {
                    GroupCallMiniTextureView groupCallMiniTextureView = this.renderer;
                    if (groupCallMiniTextureView != null) {
                        groupCallMiniTextureView.setSecondaryView((GroupCallUserCell) null);
                    }
                    this.renderer = null;
                }
            }
        }

        public void setRenderer(GroupCallMiniTextureView groupCallMiniTextureView) {
            this.renderer = groupCallMiniTextureView;
        }

        public void drawOverlays(Canvas canvas) {
            if (this.drawingName != null) {
                canvas.save();
                int measuredWidth = ((getMeasuredWidth() - this.nameWidth) - AndroidUtilities.dp(24.0f)) / 2;
                this.textPaint.setAlpha((int) (this.progress * 255.0f * getAlpha()));
                canvas.drawText(this.drawingName, (float) (AndroidUtilities.dp(22.0f) + measuredWidth), (float) AndroidUtilities.dp(69.0f), this.textPaint);
                canvas.restore();
                canvas.save();
                canvas.translate((float) measuredWidth, (float) AndroidUtilities.dp(53.0f));
                if (this.muteButton.getDrawable() != null) {
                    this.muteButton.getDrawable().setAlpha((int) (this.progress * 255.0f * getAlpha()));
                    this.muteButton.draw(canvas);
                    this.muteButton.getDrawable().setAlpha(255);
                }
                canvas.restore();
            }
        }

        /* access modifiers changed from: protected */
        public boolean drawChild(Canvas canvas, View view, long j) {
            if (view == this.muteButton) {
                return true;
            }
            return super.drawChild(canvas, view, j);
        }

        public float getProgressToFullscreen() {
            return this.progress;
        }

        public GroupCallMiniTextureView getRenderer() {
            return this.renderer;
        }

        public void setAmplitude(double d) {
            GroupCallStatusIcon groupCallStatusIcon = this.statusIcon;
            if (groupCallStatusIcon != null) {
                groupCallStatusIcon.setAmplitude(d);
            }
            this.avatarWavesDrawable.setAmplitude(d);
        }

        /* JADX WARNING: Removed duplicated region for block: B:12:0x0035  */
        /* JADX WARNING: Removed duplicated region for block: B:16:0x006c  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void updateState(boolean r10) {
            /*
                r9 = this;
                org.telegram.ui.Components.voip.GroupCallStatusIcon r0 = r9.statusIcon
                if (r0 != 0) goto L_0x0005
                return
            L_0x0005:
                r0.updateIcon(r10)
                org.telegram.ui.Components.voip.GroupCallStatusIcon r0 = r9.statusIcon
                boolean r0 = r0.isMutedByMe()
                if (r0 == 0) goto L_0x0018
                java.lang.String r0 = "voipgroup_mutedByAdminIcon"
                int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)
            L_0x0016:
                r1 = r0
                goto L_0x0033
            L_0x0018:
                org.telegram.ui.Components.voip.GroupCallStatusIcon r0 = r9.statusIcon
                boolean r0 = r0.isSpeaking()
                if (r0 == 0) goto L_0x0027
                java.lang.String r0 = "voipgroup_speakingText"
                int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)
                goto L_0x0016
            L_0x0027:
                java.lang.String r0 = "voipgroup_nameText"
                int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)
                java.lang.String r1 = "voipgroup_listeningText"
                int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            L_0x0033:
                if (r10 != 0) goto L_0x006c
                android.animation.ValueAnimator r10 = r9.colorAnimator
                if (r10 == 0) goto L_0x0041
                r10.removeAllListeners()
                android.animation.ValueAnimator r10 = r9.colorAnimator
                r10.cancel()
            L_0x0041:
                r9.lastColor = r0
                r9.lastWavesColor = r1
                org.telegram.ui.Components.RLottieImageView r10 = r9.muteButton
                android.graphics.PorterDuffColorFilter r2 = new android.graphics.PorterDuffColorFilter
                android.graphics.PorterDuff$Mode r3 = android.graphics.PorterDuff.Mode.MULTIPLY
                r2.<init>(r0, r3)
                r10.setColorFilter(r2)
                android.text.TextPaint r10 = r9.textPaint
                int r0 = r9.lastColor
                r10.setColor(r0)
                android.graphics.Paint r10 = r9.selectionPaint
                r10.setColor(r1)
                org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r10 = r9.avatarWavesDrawable
                r0 = 38
                int r0 = androidx.core.graphics.ColorUtils.setAlphaComponent(r1, r0)
                r10.setColor(r0)
                r9.invalidate()
                goto L_0x0097
            L_0x006c:
                int r4 = r9.lastColor
                int r6 = r9.lastWavesColor
                r10 = 2
                float[] r10 = new float[r10]
                r10 = {0, NUM} // fill-array
                android.animation.ValueAnimator r10 = android.animation.ValueAnimator.ofFloat(r10)
                r9.colorAnimator = r10
                org.telegram.ui.Components.-$$Lambda$GroupCallFullscreenAdapter$GroupCallUserCell$gtKr_PRIQv6oil_uEp6rPgiruuU r8 = new org.telegram.ui.Components.-$$Lambda$GroupCallFullscreenAdapter$GroupCallUserCell$gtKr_PRIQv6oil_uEp6rPgiruuU
                r2 = r8
                r3 = r9
                r5 = r0
                r7 = r1
                r2.<init>(r4, r5, r6, r7)
                r10.addUpdateListener(r8)
                android.animation.ValueAnimator r10 = r9.colorAnimator
                org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell$2 r2 = new org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell$2
                r2.<init>(r0, r1)
                r10.addListener(r2)
                android.animation.ValueAnimator r10 = r9.colorAnimator
                r10.start()
            L_0x0097:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.GroupCallFullscreenAdapter.GroupCallUserCell.updateState(boolean):void");
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$updateState$0 */
        public /* synthetic */ void lambda$updateState$0$GroupCallFullscreenAdapter$GroupCallUserCell(int i, int i2, int i3, int i4, ValueAnimator valueAnimator) {
            this.lastColor = ColorUtils.blendARGB(i, i2, ((Float) valueAnimator.getAnimatedValue()).floatValue());
            this.lastWavesColor = ColorUtils.blendARGB(i3, i4, ((Float) valueAnimator.getAnimatedValue()).floatValue());
            this.muteButton.setColorFilter(new PorterDuffColorFilter(this.lastColor, PorterDuff.Mode.MULTIPLY));
            this.textPaint.setColor(this.lastColor);
            this.selectionPaint.setColor(this.lastWavesColor);
            this.avatarWavesDrawable.setColor(ColorUtils.setAlphaComponent(this.lastWavesColor, 38));
            invalidate();
        }

        public void invalidate() {
            if (!this.skipInvalidate) {
                this.skipInvalidate = true;
                super.invalidate();
                GroupCallMiniTextureView groupCallMiniTextureView = this.renderer;
                if (groupCallMiniTextureView != null) {
                    groupCallMiniTextureView.invalidate();
                } else {
                    GroupCallFullscreenAdapter.this.renderersContainer.invalidate();
                }
                this.skipInvalidate = false;
            }
        }

        public void onStatusChanged() {
            this.avatarWavesDrawable.setShowWaves(this.statusIcon.isSpeaking(), this);
            updateState(true);
        }

        public boolean isRemoving(RecyclerListView recyclerListView) {
            return recyclerListView.getChildAdapterPosition(this) == -1;
        }
    }

    public void update(boolean z, RecyclerListView recyclerListView) {
        if (z) {
            final ArrayList arrayList = new ArrayList(this.participants);
            final ArrayList arrayList2 = new ArrayList(this.videoParticipants);
            this.participants.clear();
            this.participants.addAll(this.groupCall.visibleParticipants);
            this.videoParticipants.clear();
            this.videoParticipants.addAll(this.groupCall.visibleVideoParticipants);
            DiffUtil.calculateDiff(new DiffUtil.Callback() {
                public boolean areContentsTheSame(int i, int i2) {
                    return true;
                }

                public int getOldListSize() {
                    return arrayList2.size() + arrayList.size();
                }

                public int getNewListSize() {
                    return GroupCallFullscreenAdapter.this.videoParticipants.size() + GroupCallFullscreenAdapter.this.participants.size();
                }

                public boolean areItemsTheSame(int i, int i2) {
                    TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant;
                    TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant2;
                    if (i < arrayList2.size() && i2 < GroupCallFullscreenAdapter.this.videoParticipants.size()) {
                        return ((ChatObject.VideoParticipant) arrayList2.get(i)).equals(GroupCallFullscreenAdapter.this.videoParticipants.get(i2));
                    }
                    int size = i - arrayList2.size();
                    int size2 = i2 - GroupCallFullscreenAdapter.this.videoParticipants.size();
                    if (size2 < 0 || size2 >= GroupCallFullscreenAdapter.this.participants.size() || size < 0 || size >= arrayList.size()) {
                        if (i < arrayList2.size()) {
                            tLRPC$TL_groupCallParticipant = ((ChatObject.VideoParticipant) arrayList2.get(i)).participant;
                        } else {
                            tLRPC$TL_groupCallParticipant = (TLRPC$TL_groupCallParticipant) arrayList.get(size);
                        }
                        if (i2 < GroupCallFullscreenAdapter.this.videoParticipants.size()) {
                            tLRPC$TL_groupCallParticipant2 = ((ChatObject.VideoParticipant) GroupCallFullscreenAdapter.this.videoParticipants.get(i2)).participant;
                        } else {
                            tLRPC$TL_groupCallParticipant2 = (TLRPC$TL_groupCallParticipant) GroupCallFullscreenAdapter.this.participants.get(size2);
                        }
                        return MessageObject.getPeerId(tLRPC$TL_groupCallParticipant.peer) == MessageObject.getPeerId(tLRPC$TL_groupCallParticipant2.peer);
                    } else if (MessageObject.getPeerId(((TLRPC$TL_groupCallParticipant) arrayList.get(size)).peer) == MessageObject.getPeerId(((TLRPC$TL_groupCallParticipant) GroupCallFullscreenAdapter.this.participants.get(size2)).peer)) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }).dispatchUpdatesTo((RecyclerView.Adapter) this);
            AndroidUtilities.updateVisibleRows(recyclerListView);
            return;
        }
        this.participants.clear();
        this.participants.addAll(this.groupCall.visibleParticipants);
        this.videoParticipants.clear();
        this.videoParticipants.addAll(this.groupCall.visibleVideoParticipants);
        notifyDataSetChanged();
    }
}
