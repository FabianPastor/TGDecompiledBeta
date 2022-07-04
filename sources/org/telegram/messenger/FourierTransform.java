package org.telegram.messenger;

public abstract class FourierTransform {
    protected static final int LINAVG = 1;
    protected static final int LOGAVG = 2;
    protected static final int NOAVG = 3;
    protected static final float TWO_PI = 6.2831855f;
    protected float[] averages;
    protected int avgPerOctave;
    protected float bandWidth;
    protected float[] imag;
    protected int octaves;
    protected float[] real;
    protected int sampleRate;
    protected float[] spectrum;
    protected int timeSize;
    protected int whichAverage;

    /* access modifiers changed from: protected */
    public abstract void allocateArrays();

    public abstract void forward(float[] fArr);

    public abstract void inverse(float[] fArr);

    public abstract void scaleBand(int i, float f);

    public abstract void setBand(int i, float f);

    FourierTransform(int ts, float sr) {
        this.timeSize = ts;
        int i = (int) sr;
        this.sampleRate = i;
        this.bandWidth = (2.0f / ((float) ts)) * (((float) i) / 2.0f);
        noAverages();
        allocateArrays();
    }

    /* access modifiers changed from: protected */
    public void setComplex(float[] r, float[] i) {
        float[] fArr = this.real;
        if (fArr.length == r.length || this.imag.length == i.length) {
            System.arraycopy(r, 0, fArr, 0, r.length);
            System.arraycopy(i, 0, this.imag, 0, i.length);
        }
    }

    /* access modifiers changed from: protected */
    public void fillSpectrum() {
        float[] fArr;
        float lowFreq;
        int i = 0;
        while (true) {
            fArr = this.spectrum;
            if (i >= fArr.length) {
                break;
            }
            float[] fArr2 = this.real;
            float f = fArr2[i] * fArr2[i];
            float[] fArr3 = this.imag;
            fArr[i] = (float) Math.sqrt((double) (f + (fArr3[i] * fArr3[i])));
            i++;
        }
        int i2 = this.whichAverage;
        if (i2 == 1) {
            int avgWidth = fArr.length / this.averages.length;
            for (int i3 = 0; i3 < this.averages.length; i3++) {
                float avg = 0.0f;
                int j = 0;
                while (j < avgWidth) {
                    int offset = (i3 * avgWidth) + j;
                    float[] fArr4 = this.spectrum;
                    if (offset >= fArr4.length) {
                        break;
                    }
                    avg += fArr4[offset];
                    j++;
                }
                this.averages[i3] = avg / ((float) (j + 1));
            }
        } else if (i2 == 2) {
            int i4 = 0;
            while (true) {
                int i5 = this.octaves;
                if (i4 < i5) {
                    if (i4 == 0) {
                        lowFreq = 0.0f;
                    } else {
                        lowFreq = ((float) (this.sampleRate / 2)) / ((float) Math.pow(2.0d, (double) (i5 - i4)));
                    }
                    float freqStep = ((((float) (this.sampleRate / 2)) / ((float) Math.pow(2.0d, (double) ((this.octaves - i4) - 1)))) - lowFreq) / ((float) this.avgPerOctave);
                    float f2 = lowFreq;
                    int j2 = 0;
                    while (true) {
                        int i6 = this.avgPerOctave;
                        if (j2 >= i6) {
                            break;
                        }
                        this.averages[(i6 * i4) + j2] = calcAvg(f2, f2 + freqStep);
                        f2 += freqStep;
                        j2++;
                    }
                    i4++;
                } else {
                    return;
                }
            }
        }
    }

    public void noAverages() {
        this.averages = new float[0];
        this.whichAverage = 3;
    }

    public void linAverages(int numAvg) {
        if (numAvg <= this.spectrum.length / 2) {
            this.averages = new float[numAvg];
            this.whichAverage = 1;
        }
    }

    public void logAverages(int minBandwidth, int bandsPerOctave) {
        float nyq = ((float) this.sampleRate) / 2.0f;
        this.octaves = 1;
        while (true) {
            float f = nyq / 2.0f;
            nyq = f;
            if (f > ((float) minBandwidth)) {
                this.octaves++;
            } else {
                this.avgPerOctave = bandsPerOctave;
                this.averages = new float[(this.octaves * bandsPerOctave)];
                this.whichAverage = 2;
                return;
            }
        }
    }

    public int timeSize() {
        return this.timeSize;
    }

    public int specSize() {
        return this.spectrum.length;
    }

    public float getBand(int i) {
        if (i < 0) {
            i = 0;
        }
        float[] fArr = this.spectrum;
        if (i > fArr.length - 1) {
            i = fArr.length - 1;
        }
        return fArr[i];
    }

    public float getBandWidth() {
        return this.bandWidth;
    }

    public int freqToIndex(float freq) {
        if (freq < getBandWidth() / 2.0f) {
            return 0;
        }
        if (freq > ((float) (this.sampleRate / 2)) - (getBandWidth() / 2.0f)) {
            return this.spectrum.length - 1;
        }
        return Math.round(((float) this.timeSize) * (freq / ((float) this.sampleRate)));
    }

    public float indexToFreq(int i) {
        float bw = getBandWidth();
        if (i == 0) {
            return 0.25f * bw;
        }
        if (i == this.spectrum.length - 1) {
            return (((float) (this.sampleRate / 2)) - (bw / 2.0f)) + (0.25f * bw);
        }
        return ((float) i) * bw;
    }

    public float calcAvg(float lowFreq, float hiFreq) {
        int lowBound = freqToIndex(lowFreq);
        int hiBound = freqToIndex(hiFreq);
        float avg = 0.0f;
        for (int i = lowBound; i <= hiBound; i++) {
            avg += this.spectrum[i];
        }
        return avg / ((float) ((hiBound - lowBound) + 1));
    }

    public float[] getSpectrumReal() {
        return this.real;
    }

    public float[] getSpectrumImaginary() {
        return this.imag;
    }

    public void forward(float[] buffer, int startAt) {
        int length = buffer.length - startAt;
        int i = this.timeSize;
        if (length >= i) {
            float[] section = new float[i];
            System.arraycopy(buffer, startAt, section, 0, section.length);
            forward(section);
        }
    }

    public void inverse(float[] freqReal, float[] freqImag, float[] buffer) {
        setComplex(freqReal, freqImag);
        inverse(buffer);
    }

    public static class FFT extends FourierTransform {
        private float[] coslookup;
        private int[] reverse;
        private float[] sinlookup;

        public FFT(int timeSize, float sampleRate) {
            super(timeSize, sampleRate);
            if (((timeSize - 1) & timeSize) == 0) {
                buildReverseTable();
                buildTrigTables();
                return;
            }
            throw new IllegalArgumentException("FFT: timeSize must be a power of two.");
        }

        /* access modifiers changed from: protected */
        public void allocateArrays() {
            this.spectrum = new float[((this.timeSize / 2) + 1)];
            this.real = new float[this.timeSize];
            this.imag = new float[this.timeSize];
        }

        public void scaleBand(int i, float s) {
            if (s >= 0.0f) {
                float[] fArr = this.real;
                fArr[i] = fArr[i] * s;
                float[] fArr2 = this.imag;
                fArr2[i] = fArr2[i] * s;
                float[] fArr3 = this.spectrum;
                fArr3[i] = fArr3[i] * s;
                if (i != 0 && i != this.timeSize / 2) {
                    this.real[this.timeSize - i] = this.real[i];
                    this.imag[this.timeSize - i] = -this.imag[i];
                }
            }
        }

        public void setBand(int i, float a) {
            if (a >= 0.0f) {
                if (this.real[i] == 0.0f && this.imag[i] == 0.0f) {
                    this.real[i] = a;
                    this.spectrum[i] = a;
                } else {
                    float[] fArr = this.real;
                    fArr[i] = fArr[i] / this.spectrum[i];
                    float[] fArr2 = this.imag;
                    fArr2[i] = fArr2[i] / this.spectrum[i];
                    this.spectrum[i] = a;
                    float[] fArr3 = this.real;
                    fArr3[i] = fArr3[i] * this.spectrum[i];
                    float[] fArr4 = this.imag;
                    fArr4[i] = fArr4[i] * this.spectrum[i];
                }
                if (i != 0 && i != this.timeSize / 2) {
                    this.real[this.timeSize - i] = this.real[i];
                    this.imag[this.timeSize - i] = -this.imag[i];
                }
            }
        }

        private void fft() {
            for (int halfSize = 1; halfSize < this.real.length; halfSize *= 2) {
                float phaseShiftStepR = cos(halfSize);
                float phaseShiftStepI = sin(halfSize);
                float currentPhaseShiftR = 1.0f;
                float currentPhaseShiftI = 0.0f;
                for (int fftStep = 0; fftStep < halfSize; fftStep++) {
                    for (int i = fftStep; i < this.real.length; i += halfSize * 2) {
                        int off = i + halfSize;
                        float tr = (this.real[off] * currentPhaseShiftR) - (this.imag[off] * currentPhaseShiftI);
                        float ti = (this.imag[off] * currentPhaseShiftR) + (this.real[off] * currentPhaseShiftI);
                        this.real[off] = this.real[i] - tr;
                        this.imag[off] = this.imag[i] - ti;
                        float[] fArr = this.real;
                        fArr[i] = fArr[i] + tr;
                        float[] fArr2 = this.imag;
                        fArr2[i] = fArr2[i] + ti;
                    }
                    float tmpR = currentPhaseShiftR;
                    currentPhaseShiftR = (tmpR * phaseShiftStepR) - (currentPhaseShiftI * phaseShiftStepI);
                    currentPhaseShiftI = (tmpR * phaseShiftStepI) + (currentPhaseShiftI * phaseShiftStepR);
                }
            }
        }

        public void forward(float[] buffer) {
            if (buffer.length == this.timeSize) {
                bitReverseSamples(buffer, 0);
                fft();
                fillSpectrum();
            }
        }

        public void forward(float[] buffer, int startAt) {
            if (buffer.length - startAt >= this.timeSize) {
                bitReverseSamples(buffer, startAt);
                fft();
                fillSpectrum();
            }
        }

        public void forward(float[] buffReal, float[] buffImag) {
            if (buffReal.length == this.timeSize && buffImag.length == this.timeSize) {
                setComplex(buffReal, buffImag);
                bitReverseComplex();
                fft();
                fillSpectrum();
            }
        }

        public void inverse(float[] buffer) {
            if (buffer.length <= this.real.length) {
                for (int i = 0; i < this.timeSize; i++) {
                    float[] fArr = this.imag;
                    fArr[i] = fArr[i] * -1.0f;
                }
                bitReverseComplex();
                fft();
                for (int i2 = 0; i2 < buffer.length; i2++) {
                    buffer[i2] = this.real[i2] / ((float) this.real.length);
                }
            }
        }

        private void buildReverseTable() {
            int N = this.timeSize;
            int[] iArr = new int[N];
            this.reverse = iArr;
            iArr[0] = 0;
            int limit = 1;
            int bit = N / 2;
            while (limit < N) {
                for (int i = 0; i < limit; i++) {
                    int[] iArr2 = this.reverse;
                    iArr2[i + limit] = iArr2[i] + bit;
                }
                limit <<= 1;
                bit >>= 1;
            }
        }

        private void bitReverseSamples(float[] samples, int startAt) {
            for (int i = 0; i < this.timeSize; i++) {
                this.real[i] = samples[this.reverse[i] + startAt];
                this.imag[i] = 0.0f;
            }
        }

        private void bitReverseComplex() {
            float[] revReal = new float[this.real.length];
            float[] revImag = new float[this.imag.length];
            for (int i = 0; i < this.real.length; i++) {
                revReal[i] = this.real[this.reverse[i]];
                revImag[i] = this.imag[this.reverse[i]];
            }
            this.real = revReal;
            this.imag = revImag;
        }

        private float sin(int i) {
            return this.sinlookup[i];
        }

        private float cos(int i) {
            return this.coslookup[i];
        }

        private void buildTrigTables() {
            int N = this.timeSize;
            this.sinlookup = new float[N];
            this.coslookup = new float[N];
            for (int i = 0; i < N; i++) {
                this.sinlookup[i] = (float) Math.sin((double) (-3.1415927f / ((float) i)));
                this.coslookup[i] = (float) Math.cos((double) (-3.1415927f / ((float) i)));
            }
        }
    }
}
