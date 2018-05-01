package org.telegram.ui.Components.Paint;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.opengl.GLES20;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Map;
import java.util.UUID;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.MessagesController;
import org.telegram.ui.Components.Size;

public class Painting {
    private Path activePath;
    private RectF activeStrokeBounds;
    private Slice backupSlice;
    private Texture bitmapTexture;
    private Brush brush;
    private Texture brushTexture;
    private int[] buffers = new int[1];
    private ByteBuffer dataBuffer;
    private PaintingDelegate delegate;
    private int paintTexture;
    private boolean paused;
    private float[] projection;
    private float[] renderProjection;
    private RenderState renderState = new RenderState();
    private RenderView renderView;
    private int reusableFramebuffer;
    private Map<String, Shader> shaders;
    private Size size;
    private int suppressChangesCounter;
    private ByteBuffer textureBuffer;
    private ByteBuffer vertexBuffer;

    public class PaintingData {
        public Bitmap bitmap;
        public ByteBuffer data;

        PaintingData(Bitmap bitmap, ByteBuffer byteBuffer) {
            this.bitmap = bitmap;
            this.data = byteBuffer;
        }
    }

    public interface PaintingDelegate {
        void contentChanged(RectF rectF);

        DispatchQueue requestDispatchQueue();

        UndoStore requestUndoStore();

        void strokeCommited();
    }

    public Painting(Size size) {
        this.size = size;
        this.dataBuffer = ByteBuffer.allocateDirect((((int) this.size.width) * ((int) this.size.height)) * 4);
        this.projection = GLMatrix.LoadOrtho(0.0f, this.size.width, 0.0f, this.size.height, -1.0f, 1.0f);
        if (this.vertexBuffer == null) {
            this.vertexBuffer = ByteBuffer.allocateDirect(32);
            this.vertexBuffer.order(ByteOrder.nativeOrder());
        }
        this.vertexBuffer.putFloat(0.0f);
        this.vertexBuffer.putFloat(0.0f);
        this.vertexBuffer.putFloat(this.size.width);
        this.vertexBuffer.putFloat(0.0f);
        this.vertexBuffer.putFloat(0.0f);
        this.vertexBuffer.putFloat(this.size.height);
        this.vertexBuffer.putFloat(this.size.width);
        this.vertexBuffer.putFloat(this.size.height);
        this.vertexBuffer.rewind();
        if (this.textureBuffer == null) {
            this.textureBuffer = ByteBuffer.allocateDirect(32);
            this.textureBuffer.order(ByteOrder.nativeOrder());
            this.textureBuffer.putFloat(0.0f);
            this.textureBuffer.putFloat(0.0f);
            this.textureBuffer.putFloat(1.0f);
            this.textureBuffer.putFloat(0.0f);
            this.textureBuffer.putFloat(0.0f);
            this.textureBuffer.putFloat(1.0f);
            this.textureBuffer.putFloat(1.0f);
            this.textureBuffer.putFloat(1.0f);
            this.textureBuffer.rewind();
        }
    }

    public void setDelegate(PaintingDelegate paintingDelegate) {
        this.delegate = paintingDelegate;
    }

    public void setRenderView(RenderView renderView) {
        this.renderView = renderView;
    }

    public Size getSize() {
        return this.size;
    }

    public RectF getBounds() {
        return new RectF(0.0f, 0.0f, this.size.width, this.size.height);
    }

    private boolean isSuppressingChanges() {
        return this.suppressChangesCounter > 0;
    }

    private void beginSuppressingChanges() {
        this.suppressChangesCounter++;
    }

    private void endSuppressingChanges() {
        this.suppressChangesCounter--;
    }

    public void setBitmap(Bitmap bitmap) {
        if (this.bitmapTexture == null) {
            this.bitmapTexture = new Texture(bitmap);
        }
    }

    private void update(RectF rectF, Runnable runnable) {
        GLES20.glBindFramebuffer(36160, getReusableFramebuffer());
        GLES20.glFramebufferTexture2D(36160, 36064, 3553, getTexture(), 0);
        if (GLES20.glCheckFramebufferStatus(36160) == 36053) {
            GLES20.glViewport(0, 0, (int) this.size.width, (int) this.size.height);
            runnable.run();
        }
        GLES20.glBindFramebuffer(36160, 0);
        if (isSuppressingChanges() == null && this.delegate != null) {
            this.delegate.contentChanged(rectF);
        }
    }

    public void paintStroke(final Path path, final boolean z, final Runnable runnable) {
        this.renderView.performInContext(new Runnable() {
            public void run() {
                Painting.this.activePath = path;
                GLES20.glBindFramebuffer(36160, Painting.this.getReusableFramebuffer());
                GLES20.glFramebufferTexture2D(36160, 36064, 3553, Painting.this.getPaintTexture(), 0);
                Utils.HasGLError();
                if (GLES20.glCheckFramebufferStatus(36160) == 36053) {
                    GLES20.glViewport(0, 0, (int) Painting.this.size.width, (int) Painting.this.size.height);
                    if (z) {
                        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
                        GLES20.glClear(MessagesController.UPDATE_MASK_CHAT_ADMINS);
                    }
                    if (Painting.this.shaders != null) {
                        Shader shader = (Shader) Painting.this.shaders.get(Painting.this.brush.isLightSaber() ? "brushLight" : "brush");
                        if (shader != null) {
                            GLES20.glUseProgram(shader.program);
                            if (Painting.this.brushTexture == null) {
                                Painting.this.brushTexture = new Texture(Painting.this.brush.getStamp());
                            }
                            GLES20.glActiveTexture(33984);
                            GLES20.glBindTexture(3553, Painting.this.brushTexture.texture());
                            GLES20.glUniformMatrix4fv(shader.getUniform("mvpMatrix"), 1, false, FloatBuffer.wrap(Painting.this.projection));
                            GLES20.glUniform1i(shader.getUniform("texture"), 0);
                            RectF RenderPath = Render.RenderPath(path, Painting.this.renderState);
                        } else {
                            return;
                        }
                    }
                    return;
                }
                RenderPath = null;
                GLES20.glBindFramebuffer(36160, 0);
                if (Painting.this.delegate != null) {
                    Painting.this.delegate.contentChanged(RenderPath);
                }
                if (Painting.this.activeStrokeBounds != null) {
                    Painting.this.activeStrokeBounds.union(RenderPath);
                } else {
                    Painting.this.activeStrokeBounds = RenderPath;
                }
                if (runnable != null) {
                    runnable.run();
                }
            }
        });
    }

    public void commitStroke(final int i) {
        this.renderView.performInContext(new Runnable() {

            /* renamed from: org.telegram.ui.Components.Paint.Painting$2$1 */
            class C12031 implements Runnable {
                C12031() {
                }

                public void run() {
                    if (Painting.this.shaders != null) {
                        Shader shader = (Shader) Painting.this.shaders.get(Painting.this.brush.isLightSaber() ? "compositeWithMaskLight" : "compositeWithMask");
                        if (shader != null) {
                            GLES20.glUseProgram(shader.program);
                            GLES20.glUniformMatrix4fv(shader.getUniform("mvpMatrix"), 1, false, FloatBuffer.wrap(Painting.this.projection));
                            GLES20.glUniform1i(shader.getUniform("mask"), 0);
                            Shader.SetColorUniform(shader.getUniform(TtmlNode.ATTR_TTS_COLOR), i);
                            GLES20.glActiveTexture(33984);
                            GLES20.glBindTexture(3553, Painting.this.getPaintTexture());
                            GLES20.glBlendFuncSeparate(770, 771, 770, 1);
                            GLES20.glVertexAttribPointer(0, 2, 5126, false, 8, Painting.this.vertexBuffer);
                            GLES20.glEnableVertexAttribArray(0);
                            GLES20.glVertexAttribPointer(1, 2, 5126, false, 8, Painting.this.textureBuffer);
                            GLES20.glEnableVertexAttribArray(1);
                            GLES20.glDrawArrays(5, 0, 4);
                        }
                    }
                }
            }

            public void run() {
                Painting.this.registerUndo(Painting.this.activeStrokeBounds);
                Painting.this.beginSuppressingChanges();
                Painting.this.update(null, new C12031());
                Painting.this.endSuppressingChanges();
                Painting.this.renderState.reset();
                Painting.this.activeStrokeBounds = null;
                Painting.this.activePath = null;
            }
        });
    }

    private void registerUndo(RectF rectF) {
        if (rectF != null && rectF.setIntersect(rectF, getBounds())) {
            final Slice slice = new Slice(getPaintingData(rectF, true).data, rectF, this.delegate.requestDispatchQueue());
            this.delegate.requestUndoStore().registerUndo(UUID.randomUUID(), new Runnable() {
                public void run() {
                    Painting.this.restoreSlice(slice);
                }
            });
        }
    }

    private void restoreSlice(final Slice slice) {
        this.renderView.performInContext(new Runnable() {
            public void run() {
                Buffer data = slice.getData();
                GLES20.glBindTexture(3553, Painting.this.getTexture());
                GLES20.glTexSubImage2D(3553, 0, slice.getX(), slice.getY(), slice.getWidth(), slice.getHeight(), 6408, 5121, data);
                if (!(Painting.this.isSuppressingChanges() || Painting.this.delegate == null)) {
                    Painting.this.delegate.contentChanged(slice.getBounds());
                }
                slice.cleanResources();
            }
        });
    }

    public void setRenderProjection(float[] fArr) {
        this.renderProjection = fArr;
    }

    public void render() {
        if (this.shaders != null) {
            if (this.activePath != null) {
                render(getPaintTexture(), this.activePath.getColor());
            } else {
                renderBlit();
            }
        }
    }

    private void render(int i, int i2) {
        Shader shader = (Shader) this.shaders.get(this.brush.isLightSaber() ? "blitWithMaskLight" : "blitWithMask");
        if (shader != null) {
            GLES20.glUseProgram(shader.program);
            GLES20.glUniformMatrix4fv(shader.getUniform("mvpMatrix"), 1, false, FloatBuffer.wrap(r0.renderProjection));
            GLES20.glUniform1i(shader.getUniform("texture"), 0);
            GLES20.glUniform1i(shader.getUniform("mask"), 1);
            Shader.SetColorUniform(shader.getUniform(TtmlNode.ATTR_TTS_COLOR), i2);
            GLES20.glActiveTexture(33984);
            GLES20.glBindTexture(3553, getTexture());
            GLES20.glActiveTexture(33985);
            GLES20.glBindTexture(3553, i);
            GLES20.glBlendFunc(1, 771);
            GLES20.glVertexAttribPointer(0, 2, 5126, false, 8, r0.vertexBuffer);
            GLES20.glEnableVertexAttribArray(0);
            GLES20.glVertexAttribPointer(1, 2, 5126, false, 8, r0.textureBuffer);
            GLES20.glEnableVertexAttribArray(1);
            GLES20.glDrawArrays(5, 0, 4);
            Utils.HasGLError();
        }
    }

    private void renderBlit() {
        Shader shader = (Shader) this.shaders.get("blit");
        if (shader != null) {
            GLES20.glUseProgram(shader.program);
            GLES20.glUniformMatrix4fv(shader.getUniform("mvpMatrix"), 1, false, FloatBuffer.wrap(r0.renderProjection));
            GLES20.glUniform1i(shader.getUniform("texture"), 0);
            GLES20.glActiveTexture(33984);
            GLES20.glBindTexture(3553, getTexture());
            GLES20.glBlendFunc(1, 771);
            GLES20.glVertexAttribPointer(0, 2, 5126, false, 8, r0.vertexBuffer);
            GLES20.glEnableVertexAttribArray(0);
            GLES20.glVertexAttribPointer(1, 2, 5126, false, 8, r0.textureBuffer);
            GLES20.glEnableVertexAttribArray(1);
            GLES20.glDrawArrays(5, 0, 4);
            Utils.HasGLError();
        }
    }

    public PaintingData getPaintingData(RectF rectF, boolean z) {
        RectF rectF2 = rectF;
        int i = (int) rectF2.left;
        int i2 = (int) rectF2.top;
        int width = (int) rectF.width();
        int height = (int) rectF.height();
        GLES20.glGenFramebuffers(1, this.buffers, 0);
        int i3 = this.buffers[0];
        GLES20.glBindFramebuffer(36160, i3);
        GLES20.glGenTextures(1, this.buffers, 0);
        int i4 = this.buffers[0];
        GLES20.glBindTexture(3553, i4);
        GLES20.glTexParameteri(3553, 10241, 9729);
        GLES20.glTexParameteri(3553, 10240, 9729);
        GLES20.glTexParameteri(3553, 10242, 33071);
        GLES20.glTexParameteri(3553, 10243, 33071);
        int i5 = 3553;
        i5 = i4;
        int i6 = i3;
        int i7 = height;
        boolean z2 = false;
        GLES20.glTexImage2D(3553, 0, 6408, width, height, 0, 6408, 5121, null);
        GLES20.glFramebufferTexture2D(36160, 36064, 3553, i5, z2);
        GLES20.glViewport(z2, z2, (int) this.size.width, (int) this.size.height);
        if (this.shaders == null) {
            return null;
        }
        Shader shader = (Shader) r0.shaders.get(z ? "nonPremultipliedBlit" : "blit");
        if (shader == null) {
            return null;
        }
        PaintingData paintingData;
        GLES20.glUseProgram(shader.program);
        Matrix matrix = new Matrix();
        matrix.preTranslate((float) (-i), (float) (-i2));
        GLES20.glUniformMatrix4fv(shader.getUniform("mvpMatrix"), 1, z2, FloatBuffer.wrap(GLMatrix.MultiplyMat4f(r0.projection, GLMatrix.LoadGraphicsMatrix(matrix))));
        if (z) {
            GLES20.glUniform1i(shader.getUniform("texture"), z2);
            GLES20.glActiveTexture(33984);
            GLES20.glBindTexture(3553, getTexture());
        } else {
            GLES20.glUniform1i(shader.getUniform("texture"), z2);
            GLES20.glActiveTexture(33984);
            GLES20.glBindTexture(3553, r0.bitmapTexture.texture());
            GLES20.glActiveTexture(33984);
            GLES20.glBindTexture(3553, getTexture());
        }
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glClear(MessagesController.UPDATE_MASK_CHAT_ADMINS);
        GLES20.glBlendFunc(1, 771);
        GLES20.glVertexAttribPointer(0, 2, 5126, false, 8, r0.vertexBuffer);
        GLES20.glEnableVertexAttribArray(z2);
        GLES20.glVertexAttribPointer(1, 2, 5126, false, 8, r0.textureBuffer);
        GLES20.glEnableVertexAttribArray(1);
        GLES20.glDrawArrays(5, z2, 4);
        r0.dataBuffer.limit((width * i7) * 4);
        GLES20.glReadPixels(0, 0, width, i7, 6408, 5121, r0.dataBuffer);
        if (z) {
            paintingData = new PaintingData(null, r0.dataBuffer);
        } else {
            Bitmap createBitmap = Bitmap.createBitmap(width, i7, Config.ARGB_8888);
            createBitmap.copyPixelsFromBuffer(r0.dataBuffer);
            paintingData = new PaintingData(createBitmap, null);
        }
        r0.buffers[z2] = i6;
        GLES20.glDeleteFramebuffers(1, r0.buffers, z2);
        r0.buffers[z2] = i5;
        GLES20.glDeleteTextures(1, r0.buffers, z2);
        return paintingData;
    }

    public void setBrush(Brush brush) {
        this.brush = brush;
        if (this.brushTexture != null) {
            this.brushTexture.cleanResources(true);
            this.brushTexture = null;
        }
    }

    public boolean isPaused() {
        return this.paused;
    }

    public void onPause(final Runnable runnable) {
        this.renderView.performInContext(new Runnable() {
            public void run() {
                Painting.this.paused = true;
                Painting.this.backupSlice = new Slice(Painting.this.getPaintingData(Painting.this.getBounds(), true).data, Painting.this.getBounds(), Painting.this.delegate.requestDispatchQueue());
                Painting.this.cleanResources(false);
                if (runnable != null) {
                    runnable.run();
                }
            }
        });
    }

    public void onResume() {
        restoreSlice(this.backupSlice);
        this.backupSlice = null;
        this.paused = false;
    }

    public void cleanResources(boolean z) {
        if (this.reusableFramebuffer != 0) {
            this.buffers[0] = this.reusableFramebuffer;
            GLES20.glDeleteFramebuffers(1, this.buffers, 0);
            this.reusableFramebuffer = 0;
        }
        this.bitmapTexture.cleanResources(z);
        if (this.paintTexture) {
            this.buffers[0] = this.paintTexture;
            GLES20.glDeleteTextures(1, this.buffers, 0);
            this.paintTexture = 0;
        }
        if (this.brushTexture) {
            this.brushTexture.cleanResources(true);
            this.brushTexture = null;
        }
        if (this.shaders) {
            for (Shader cleanResources : this.shaders.values()) {
                cleanResources.cleanResources();
            }
            this.shaders = null;
        }
    }

    private int getReusableFramebuffer() {
        if (this.reusableFramebuffer == 0) {
            int[] iArr = new int[1];
            GLES20.glGenFramebuffers(1, iArr, 0);
            this.reusableFramebuffer = iArr[0];
            Utils.HasGLError();
        }
        return this.reusableFramebuffer;
    }

    private int getTexture() {
        return this.bitmapTexture != null ? this.bitmapTexture.texture() : 0;
    }

    private int getPaintTexture() {
        if (this.paintTexture == 0) {
            this.paintTexture = Texture.generateTexture(this.size);
        }
        return this.paintTexture;
    }

    public void setupShaders() {
        this.shaders = ShaderSet.setup();
    }
}
