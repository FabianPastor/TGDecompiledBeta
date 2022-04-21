package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
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
import org.telegram.tgnet.TLRPC;
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
    public final ArrayList<TLRPC.TL_groupCallParticipant> participants = new ArrayList<>();
    /* access modifiers changed from: private */
    public GroupCallRenderersContainer renderersContainer;
    /* access modifiers changed from: private */
    public final ArrayList<ChatObject.VideoParticipant> videoParticipants = new ArrayList<>();
    /* access modifiers changed from: private */
    public boolean visible = false;

    public GroupCallFullscreenAdapter(ChatObject.Call groupCall2, int currentAccount2, GroupCallActivity activity2) {
        this.groupCall = groupCall2;
        this.currentAccount = currentAccount2;
        this.activity = activity2;
    }

    public void setRenderersPool(ArrayList<GroupCallMiniTextureView> attachedRenderers2, GroupCallRenderersContainer renderersContainer2) {
        this.attachedRenderers = attachedRenderers2;
        this.renderersContainer = renderersContainer2;
    }

    public void setGroupCall(ChatObject.Call groupCall2) {
        this.groupCall = groupCall2;
    }

    public boolean isEnabled(RecyclerView.ViewHolder holder) {
        return false;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecyclerListView.Holder(new GroupCallUserCell(parent.getContext()));
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        TLRPC.TL_groupCallParticipant participant;
        ChatObject.VideoParticipant videoParticipant;
        GroupCallUserCell view = (GroupCallUserCell) holder.itemView;
        ChatObject.VideoParticipant oldVideoParticipant = view.videoParticipant;
        if (position < this.videoParticipants.size()) {
            videoParticipant = this.videoParticipants.get(position);
            participant = this.videoParticipants.get(position).participant;
        } else if (position - this.videoParticipants.size() < this.participants.size()) {
            videoParticipant = null;
            participant = this.participants.get(position - this.videoParticipants.size());
        } else {
            return;
        }
        view.setParticipant(videoParticipant, participant);
        if (oldVideoParticipant != null && !oldVideoParticipant.equals(videoParticipant) && view.attached && view.getRenderer() != null) {
            view.attachRenderer(false);
            if (videoParticipant != null) {
                view.attachRenderer(true);
            }
        } else if (!view.attached) {
        } else {
            if (view.getRenderer() == null && videoParticipant != null && this.visible) {
                view.attachRenderer(true);
            } else if (view.getRenderer() != null && videoParticipant == null) {
                view.attachRenderer(false);
            }
        }
    }

    public int getItemCount() {
        return this.videoParticipants.size() + this.participants.size();
    }

    public void setVisibility(RecyclerListView listView, boolean visibility) {
        this.visible = visibility;
        for (int i = 0; i < listView.getChildCount(); i++) {
            View view = listView.getChildAt(i);
            if ((view instanceof GroupCallUserCell) && ((GroupCallUserCell) view).getVideoParticipant() != null) {
                ((GroupCallUserCell) view).attachRenderer(visibility);
            }
        }
    }

    public void scrollTo(ChatObject.VideoParticipant videoParticipant, RecyclerListView fullscreenUsersListView) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) fullscreenUsersListView.getLayoutManager();
        if (layoutManager != null) {
            for (int i = 0; i < this.videoParticipants.size(); i++) {
                if (this.videoParticipants.get(i).equals(videoParticipant)) {
                    layoutManager.scrollToPositionWithOffset(i, AndroidUtilities.dp(13.0f));
                    return;
                }
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
        private TLRPC.Chat currentChat;
        private TLRPC.User currentUser;
        String drawingName;
        boolean hasAvatar;
        int lastColor;
        private boolean lastMuted;
        private boolean lastRaisedHand;
        int lastWavesColor;
        RLottieImageView muteButton;
        String name;
        int nameWidth;
        TLRPC.TL_groupCallParticipant participant;
        long peerId;
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
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            this.textPaint.setTextSize((float) AndroidUtilities.dp(12.0f));
            if (this.name != null) {
                int min = (int) Math.min((float) AndroidUtilities.dp(46.0f), this.textPaint.measureText(this.name));
                this.nameWidth = min;
                this.drawingName = TextUtils.ellipsize(this.name, this.textPaint, (float) min, TextUtils.TruncateAt.END).toString();
            }
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(80.0f), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(80.0f), NUM));
        }

        public void setParticipant(ChatObject.VideoParticipant videoParticipant2, TLRPC.TL_groupCallParticipant participant2) {
            this.videoParticipant = videoParticipant2;
            this.participant = participant2;
            long lastPeerId = this.peerId;
            long peerId2 = MessageObject.getPeerId(participant2.peer);
            this.peerId = peerId2;
            boolean z = false;
            if (peerId2 > 0) {
                TLRPC.User user = AccountInstance.getInstance(GroupCallFullscreenAdapter.this.currentAccount).getMessagesController().getUser(Long.valueOf(this.peerId));
                this.currentUser = user;
                this.currentChat = null;
                this.avatarDrawable.setInfo(user);
                this.name = UserObject.getFirstName(this.currentUser);
                this.avatarImageView.getImageReceiver().setCurrentAccount(GroupCallFullscreenAdapter.this.currentAccount);
                ImageLocation imageLocation = ImageLocation.getForUser(this.currentUser, 1);
                this.hasAvatar = imageLocation != null;
                this.avatarImageView.setImage(imageLocation, "50_50", (Drawable) this.avatarDrawable, (Object) this.currentUser);
            } else {
                TLRPC.Chat chat = AccountInstance.getInstance(GroupCallFullscreenAdapter.this.currentAccount).getMessagesController().getChat(Long.valueOf(-this.peerId));
                this.currentChat = chat;
                this.currentUser = null;
                this.avatarDrawable.setInfo(chat);
                TLRPC.Chat chat2 = this.currentChat;
                if (chat2 != null) {
                    this.name = chat2.title;
                    this.avatarImageView.getImageReceiver().setCurrentAccount(GroupCallFullscreenAdapter.this.currentAccount);
                    ImageLocation imageLocation2 = ImageLocation.getForChat(this.currentChat, 1);
                    this.hasAvatar = imageLocation2 != null;
                    this.avatarImageView.setImage(imageLocation2, "50_50", (Drawable) this.avatarDrawable, (Object) this.currentChat);
                }
            }
            boolean animated = lastPeerId == this.peerId;
            if (videoParticipant2 == null) {
                if (GroupCallFullscreenAdapter.this.renderersContainer.fullscreenPeerId == MessageObject.getPeerId(participant2.peer)) {
                    z = true;
                }
                this.selected = z;
            } else if (GroupCallFullscreenAdapter.this.renderersContainer.fullscreenParticipant != null) {
                this.selected = GroupCallFullscreenAdapter.this.renderersContainer.fullscreenParticipant.equals(videoParticipant2);
            } else {
                this.selected = false;
            }
            if (!animated) {
                setSelectedProgress(this.selected ? 1.0f : 0.0f);
            }
            GroupCallStatusIcon groupCallStatusIcon = this.statusIcon;
            if (groupCallStatusIcon != null) {
                groupCallStatusIcon.setParticipant(participant2, animated);
                updateState(animated);
            }
        }

        public void setAlpha(float alpha) {
            super.setAlpha(alpha);
        }

        public void setProgressToFullscreen(float progress2) {
            if (this.progress != progress2) {
                this.progress = progress2;
                if (progress2 == 1.0f) {
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
                float moveToCenter = (((float) this.avatarImageView.getTop()) + (((float) this.avatarImageView.getMeasuredHeight()) / 2.0f)) - (((float) getMeasuredHeight()) / 2.0f);
                float s = ((1.0f - progress2) * (((float) AndroidUtilities.dp(46.0f)) / ((float) AndroidUtilities.dp(40.0f)))) + (progress2 * 1.0f);
                this.avatarImageView.setTranslationY((-moveToCenter) * (1.0f - progress2));
                this.avatarImageView.setScaleX(s);
                this.avatarImageView.setScaleY(s);
                this.backgroundPaint.setAlpha((int) (255.0f * progress2));
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
                    float p = (((float) getMeasuredWidth()) / 2.0f) * (1.0f - this.progress);
                    AndroidUtilities.rectTmp.set(p, p, ((float) getMeasuredWidth()) - p, ((float) getMeasuredHeight()) - p);
                    canvas.drawRoundRect(AndroidUtilities.rectTmp, (float) AndroidUtilities.dp(13.0f), (float) AndroidUtilities.dp(13.0f), this.backgroundPaint);
                    drawSelection(canvas);
                }
                float cx = this.avatarImageView.getX() + ((float) (this.avatarImageView.getMeasuredWidth() / 2));
                float cy = this.avatarImageView.getY() + ((float) (this.avatarImageView.getMeasuredHeight() / 2));
                this.avatarWavesDrawable.update();
                this.avatarWavesDrawable.draw(canvas, cx, cy, this);
                float scaleFrom = ((float) AndroidUtilities.dp(46.0f)) / ((float) AndroidUtilities.dp(40.0f));
                float f = this.progress;
                float s = ((1.0f - f) * scaleFrom) + (f * 1.0f);
                this.avatarImageView.setScaleX(this.avatarWavesDrawable.getAvatarScale() * s);
                this.avatarImageView.setScaleY(this.avatarWavesDrawable.getAvatarScale() * s);
                super.dispatchDraw(canvas);
                return;
            }
            drawSelection(canvas);
        }

        /* JADX WARNING: Removed duplicated region for block: B:19:0x003c  */
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
                goto L_0x0035
            L_0x001f:
                if (r0 != 0) goto L_0x0035
                float r0 = r6.selectionProgress
                int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
                if (r4 == 0) goto L_0x0035
                float r0 = r0 - r1
                int r1 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
                if (r1 >= 0) goto L_0x002e
                r0 = 0
                goto L_0x0031
            L_0x002e:
                r6.invalidate()
            L_0x0031:
                r6.setSelectedProgress(r0)
                goto L_0x0036
            L_0x0035:
            L_0x0036:
                float r0 = r6.selectionProgress
                int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
                if (r0 <= 0) goto L_0x0080
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
                android.graphics.RectF r2 = org.telegram.messenger.AndroidUtilities.rectTmp
                android.graphics.Paint r3 = r6.selectionPaint
                float r3 = r3.getStrokeWidth()
                float r3 = r3 / r1
                android.graphics.Paint r4 = r6.selectionPaint
                float r4 = r4.getStrokeWidth()
                float r4 = r4 / r1
                r2.inset(r3, r4)
                android.graphics.RectF r1 = org.telegram.messenger.AndroidUtilities.rectTmp
                r2 = 1094713344(0x41400000, float:12.0)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r2)
                float r3 = (float) r3
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                float r2 = (float) r2
                android.graphics.Paint r4 = r6.selectionPaint
                r7.drawRoundRect(r1, r3, r2, r4)
            L_0x0080:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.GroupCallFullscreenAdapter.GroupCallUserCell.drawSelection(android.graphics.Canvas):void");
        }

        private void setSelectedProgress(float p) {
            if (this.selectionProgress != p) {
                this.selectionProgress = p;
                this.selectionPaint.setAlpha((int) (255.0f * p));
            }
        }

        public long getPeerId() {
            return this.peerId;
        }

        public BackupImageView getAvatarImageView() {
            return this.avatarImageView;
        }

        public TLRPC.TL_groupCallParticipant getParticipant() {
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

        public void attachRenderer(boolean attach) {
            if (!GroupCallFullscreenAdapter.this.activity.isDismissed()) {
                if (attach && this.renderer == null) {
                    this.renderer = GroupCallMiniTextureView.getOrCreate(GroupCallFullscreenAdapter.this.attachedRenderers, GroupCallFullscreenAdapter.this.renderersContainer, (GroupCallGridCell) null, this, (GroupCallGridCell) null, this.videoParticipant, GroupCallFullscreenAdapter.this.groupCall, GroupCallFullscreenAdapter.this.activity);
                } else if (!attach) {
                    GroupCallMiniTextureView groupCallMiniTextureView = this.renderer;
                    if (groupCallMiniTextureView != null) {
                        groupCallMiniTextureView.setSecondaryView((GroupCallUserCell) null);
                    }
                    this.renderer = null;
                }
            }
        }

        public void setRenderer(GroupCallMiniTextureView renderer2) {
            this.renderer = renderer2;
        }

        public void drawOverlays(Canvas canvas) {
            if (this.drawingName != null) {
                canvas.save();
                int paddingStart = ((getMeasuredWidth() - this.nameWidth) - AndroidUtilities.dp(24.0f)) / 2;
                this.textPaint.setAlpha((int) (this.progress * 255.0f * getAlpha()));
                canvas.drawText(this.drawingName, (float) (AndroidUtilities.dp(22.0f) + paddingStart), (float) AndroidUtilities.dp(69.0f), this.textPaint);
                canvas.restore();
                canvas.save();
                canvas.translate((float) paddingStart, (float) AndroidUtilities.dp(53.0f));
                if (this.muteButton.getDrawable() != null) {
                    this.muteButton.getDrawable().setAlpha((int) (this.progress * 255.0f * getAlpha()));
                    this.muteButton.draw(canvas);
                    this.muteButton.getDrawable().setAlpha(255);
                }
                canvas.restore();
            }
        }

        /* access modifiers changed from: protected */
        public boolean drawChild(Canvas canvas, View child, long drawingTime) {
            if (child == this.muteButton) {
                return true;
            }
            return super.drawChild(canvas, child, drawingTime);
        }

        public float getProgressToFullscreen() {
            return this.progress;
        }

        public GroupCallMiniTextureView getRenderer() {
            return this.renderer;
        }

        public void setAmplitude(double value) {
            GroupCallStatusIcon groupCallStatusIcon = this.statusIcon;
            if (groupCallStatusIcon != null) {
                groupCallStatusIcon.setAmplitude(value);
            }
            this.avatarWavesDrawable.setAmplitude(value);
        }

        public void updateState(boolean animated) {
            final int newColor;
            final int newWavesColor;
            GroupCallStatusIcon groupCallStatusIcon = this.statusIcon;
            if (groupCallStatusIcon != null) {
                groupCallStatusIcon.updateIcon(animated);
                if (this.statusIcon.isMutedByMe()) {
                    newWavesColor = Theme.getColor("voipgroup_mutedByAdminIcon");
                    newColor = newWavesColor;
                } else if (this.statusIcon.isSpeaking()) {
                    newWavesColor = Theme.getColor("voipgroup_speakingText");
                    newColor = newWavesColor;
                } else {
                    newColor = Theme.getColor("voipgroup_nameText");
                    newWavesColor = Theme.getColor("voipgroup_listeningText");
                }
                if (!animated) {
                    ValueAnimator valueAnimator = this.colorAnimator;
                    if (valueAnimator != null) {
                        valueAnimator.removeAllListeners();
                        this.colorAnimator.cancel();
                    }
                    this.lastColor = newColor;
                    this.lastWavesColor = newWavesColor;
                    this.muteButton.setColorFilter(new PorterDuffColorFilter(newColor, PorterDuff.Mode.MULTIPLY));
                    this.textPaint.setColor(this.lastColor);
                    this.selectionPaint.setColor(newWavesColor);
                    this.avatarWavesDrawable.setColor(ColorUtils.setAlphaComponent(newWavesColor, 38));
                    invalidate();
                    return;
                }
                int colorFrom = this.lastColor;
                int colorWavesFrom = this.lastWavesColor;
                ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                this.colorAnimator = ofFloat;
                ofFloat.addUpdateListener(new GroupCallFullscreenAdapter$GroupCallUserCell$$ExternalSyntheticLambda0(this, colorFrom, newColor, colorWavesFrom, newWavesColor));
                this.colorAnimator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        GroupCallUserCell.this.lastColor = newColor;
                        GroupCallUserCell.this.lastWavesColor = newWavesColor;
                        GroupCallUserCell.this.muteButton.setColorFilter(new PorterDuffColorFilter(GroupCallUserCell.this.lastColor, PorterDuff.Mode.MULTIPLY));
                        GroupCallUserCell.this.textPaint.setColor(GroupCallUserCell.this.lastColor);
                        GroupCallUserCell.this.selectionPaint.setColor(GroupCallUserCell.this.lastWavesColor);
                        GroupCallUserCell.this.avatarWavesDrawable.setColor(ColorUtils.setAlphaComponent(GroupCallUserCell.this.lastWavesColor, 38));
                    }
                });
                this.colorAnimator.start();
            }
        }

        /* renamed from: lambda$updateState$0$org-telegram-ui-Components-GroupCallFullscreenAdapter$GroupCallUserCell  reason: not valid java name */
        public /* synthetic */ void m4037x4c7d1511(int colorFrom, int newColor, int colorWavesFrom, int newWavesColor, ValueAnimator valueAnimator) {
            this.lastColor = ColorUtils.blendARGB(colorFrom, newColor, ((Float) valueAnimator.getAnimatedValue()).floatValue());
            this.lastWavesColor = ColorUtils.blendARGB(colorWavesFrom, newWavesColor, ((Float) valueAnimator.getAnimatedValue()).floatValue());
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

        public boolean hasImage() {
            GroupCallMiniTextureView groupCallMiniTextureView = this.renderer;
            return groupCallMiniTextureView != null && groupCallMiniTextureView.hasImage();
        }

        public void onStatusChanged() {
            this.avatarWavesDrawable.setShowWaves(this.statusIcon.isSpeaking(), this);
            updateState(true);
        }

        public boolean isRemoving(RecyclerListView listView) {
            return listView.getChildAdapterPosition(this) == -1;
        }
    }

    public void update(boolean animated, RecyclerListView listView) {
        if (this.groupCall != null) {
            if (animated) {
                final ArrayList<TLRPC.TL_groupCallParticipant> oldParticipants = new ArrayList<>(this.participants);
                final ArrayList<ChatObject.VideoParticipant> oldVideoParticipants = new ArrayList<>(this.videoParticipants);
                this.participants.clear();
                if (!this.groupCall.call.rtmp_stream) {
                    this.participants.addAll(this.groupCall.visibleParticipants);
                }
                this.videoParticipants.clear();
                if (!this.groupCall.call.rtmp_stream) {
                    this.videoParticipants.addAll(this.groupCall.visibleVideoParticipants);
                }
                DiffUtil.calculateDiff(new DiffUtil.Callback() {
                    public int getOldListSize() {
                        return oldVideoParticipants.size() + oldParticipants.size();
                    }

                    public int getNewListSize() {
                        return GroupCallFullscreenAdapter.this.videoParticipants.size() + GroupCallFullscreenAdapter.this.participants.size();
                    }

                    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                        TLRPC.TL_groupCallParticipant oldParticipant;
                        TLRPC.TL_groupCallParticipant newParticipant;
                        if (oldItemPosition < oldVideoParticipants.size() && newItemPosition < GroupCallFullscreenAdapter.this.videoParticipants.size()) {
                            return ((ChatObject.VideoParticipant) oldVideoParticipants.get(oldItemPosition)).equals(GroupCallFullscreenAdapter.this.videoParticipants.get(newItemPosition));
                        }
                        int oldItemPosition2 = oldItemPosition - oldVideoParticipants.size();
                        int newItemPosition2 = newItemPosition - GroupCallFullscreenAdapter.this.videoParticipants.size();
                        if (newItemPosition2 < 0 || newItemPosition2 >= GroupCallFullscreenAdapter.this.participants.size() || oldItemPosition2 < 0 || oldItemPosition2 >= oldParticipants.size()) {
                            if (oldItemPosition < oldVideoParticipants.size()) {
                                oldParticipant = ((ChatObject.VideoParticipant) oldVideoParticipants.get(oldItemPosition)).participant;
                            } else {
                                oldParticipant = (TLRPC.TL_groupCallParticipant) oldParticipants.get(oldItemPosition2);
                            }
                            if (newItemPosition < GroupCallFullscreenAdapter.this.videoParticipants.size()) {
                                newParticipant = ((ChatObject.VideoParticipant) GroupCallFullscreenAdapter.this.videoParticipants.get(newItemPosition)).participant;
                            } else {
                                newParticipant = (TLRPC.TL_groupCallParticipant) GroupCallFullscreenAdapter.this.participants.get(newItemPosition2);
                            }
                            return MessageObject.getPeerId(oldParticipant.peer) == MessageObject.getPeerId(newParticipant.peer);
                        } else if (MessageObject.getPeerId(((TLRPC.TL_groupCallParticipant) oldParticipants.get(oldItemPosition2)).peer) == MessageObject.getPeerId(((TLRPC.TL_groupCallParticipant) GroupCallFullscreenAdapter.this.participants.get(newItemPosition2)).peer)) {
                            return true;
                        } else {
                            return false;
                        }
                    }

                    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                        return true;
                    }
                }).dispatchUpdatesTo((RecyclerView.Adapter) this);
                AndroidUtilities.updateVisibleRows(listView);
                return;
            }
            this.participants.clear();
            if (!this.groupCall.call.rtmp_stream) {
                this.participants.addAll(this.groupCall.visibleParticipants);
            }
            this.videoParticipants.clear();
            if (!this.groupCall.call.rtmp_stream) {
                this.videoParticipants.addAll(this.groupCall.visibleVideoParticipants);
            }
            notifyDataSetChanged();
        }
    }
}
