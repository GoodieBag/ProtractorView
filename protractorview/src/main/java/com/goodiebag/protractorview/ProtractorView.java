package com.goodiebag.protractorview;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Krishanu on 10/01/17.
 */

public class ProtractorView extends View {

    private static final int MAX = 180;
    private final float DENSITY = getContext().getResources().getDisplayMetrics().density;

    /**
     * Private variables
     */
    //Rectangle for the arc
    private RectF mArcRect = new RectF();

    //Paints required for drawing
    private Paint mArcPaint;
    private Paint mProgressPaint;
    private Paint mTickPaint;
    private Paint mTickProgressPaint;
    private Paint mTickTextPaint;
    private Paint mTickTextColoredPaint;

    //Arc related dimens
    private int mArcRadius = 0;
    private int mArcWidth = 2;
    private int mProgressWidth = 2;
    private boolean mRoundedEdges = true;

    //Thumb Drawable
    private Drawable mThumb;

    //Thumb position related coordinates
    private int mTranslateX;
    private int mTranslateY;
    private int mThumbXPos;
    private int mThumbYPos;

    private int mAngleTextSize = 12;

    private int mTickOffset = 12;
    private int mTickLength = 10;
    private int mTickWidth = 2;
    private int mTickProgressWidth = 2;
    private int mAngle = 0;
    private boolean mTouchInside = true;
    private boolean mEnabled = true;
    private int mTicksBetweenLabel = 2;
    private int mTickIntervals = 15;
    private double mTouchAngle = 0;
    private float mTouchIgnoreRadius;

    //Event listener
    private OnProtractorViewChangeListener mOnProtractorViewChangeListener = null;


    //Interface for event listener
    public interface OnProtractorViewChangeListener {
        void onProgressChanged(ProtractorView protractorView, int progress, boolean fromUser);

        void onStartTrackingTouch(ProtractorView protractorView);

        void onStopTrackingTouch(ProtractorView protractorView);
    }

    public ProtractorView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public ProtractorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, R.attr.protractorViewStyle);
    }

    public ProtractorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }


    public int getmArcWidth() {
        return mArcWidth;
    }

    public void setmArcWidth(int mArcWidth) {
        this.mArcWidth = mArcWidth;
    }

    public int getmProgressWidth() {
        return mProgressWidth;
    }

    public void setmProgressWidth(int mProgressWidth) {
        this.mProgressWidth = mProgressWidth;
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {

        final Resources res = getResources();

        // Defaults, may need to link this into theme settings
        int arcColor = res.getColor(R.color.progress_gray);
        int arcProgressColor = res.getColor(R.color.default_blue_light);
        int textColor = res.getColor(R.color.progress_gray);
        int textProgressColor = res.getColor(R.color.default_blue_light);
        int tickColor = res.getColor(R.color.progress_gray);
        int tickProgressColor = res.getColor(R.color.default_blue_light);
        int thumbHalfheight = 0;
        int thumbHalfWidth = 0;

        mThumb = res.getDrawable(R.drawable.thumb_selector);


        // Convert all default dimens to pixels for current density
        mArcWidth = (int) (mArcWidth * DENSITY);
        mProgressWidth = (int) (mProgressWidth * DENSITY);
        mAngleTextSize = (int) (mAngleTextSize * DENSITY);
        mTickOffset = (int) (mTickOffset * DENSITY);
        mTickLength = (int) (mTickLength * DENSITY);
        mTickWidth = (int) (mTickWidth * DENSITY);
        mTickProgressWidth = (int) (mTickProgressWidth * DENSITY);

        if (attrs != null) {
            final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ProtractorView, defStyle, 0);
            Drawable thumb = array.getDrawable(R.styleable.ProtractorView_thumb);
            if (thumb != null) {
                mThumb = thumb;
            }
            thumbHalfheight = mThumb.getIntrinsicHeight() / 2;
            thumbHalfWidth = mThumb.getIntrinsicWidth() / 2;
            mThumb.setBounds(-thumbHalfWidth, -thumbHalfheight, thumbHalfWidth, thumbHalfheight);
            //Dimensions
            mAngleTextSize = (int) array.getDimension(R.styleable.ProtractorView_angleTextSize, mAngleTextSize);
            mProgressWidth = (int) array.getDimension(R.styleable.ProtractorView_progressWidth, mProgressWidth);
            mTickOffset = (int) array.getDimension(R.styleable.ProtractorView_tickOffset, mTickOffset);
            mTickLength = (int) array.getDimension(R.styleable.ProtractorView_tickLength, mTickLength);
            mArcWidth = (int) array.getDimension(R.styleable.ProtractorView_arcWidth, mArcWidth);
            //Integers
            mAngle = array.getInteger(R.styleable.ProtractorView_angle, mAngle);
            mTickIntervals = array.getInt(R.styleable.ProtractorView_tickIntervals, mTickIntervals);
            //Colors
            arcColor = array.getColor(R.styleable.ProtractorView_arcColor, arcColor);
            arcProgressColor = array.getColor(R.styleable.ProtractorView_arcProgressColor, arcProgressColor);
            textColor = array.getColor(R.styleable.ProtractorView_textColor, textColor);
            textProgressColor = array.getColor(R.styleable.ProtractorView_textProgressColor, textProgressColor);
            tickColor = array.getColor(R.styleable.ProtractorView_tickColor, tickColor);
            tickProgressColor = array.getColor(R.styleable.ProtractorView_tickProgressColor, tickProgressColor);
            //Boolean
            mRoundedEdges = array.getBoolean(R.styleable.ProtractorView_roundEdges, mRoundedEdges);
            mEnabled = array.getBoolean(R.styleable.ProtractorView_enabled, mEnabled);
            mTouchInside = array.getBoolean(R.styleable.ProtractorView_touchInside, mTouchInside);
            mTicksBetweenLabel = array.getInt(R.styleable.ProtractorView_ticksBetweenLabel, mTicksBetweenLabel);



        }

        mAngle = (mAngle > MAX) ? MAX : ((mAngle < 0) ? 0 : mAngle);

        mArcPaint = new Paint();
        mArcPaint.setColor(arcColor);
        mArcPaint.setAntiAlias(true);
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setStrokeWidth(mArcWidth);

        mProgressPaint = new Paint();
        mProgressPaint.setColor(arcProgressColor);
        mProgressPaint.setAntiAlias(true);
        mProgressPaint.setStyle(Paint.Style.STROKE);
        mProgressPaint.setStrokeWidth(mProgressWidth);

        if (mRoundedEdges) {
            mArcPaint.setStrokeCap(Paint.Cap.ROUND);
            mProgressPaint.setStrokeCap(Paint.Cap.ROUND);
        }

        mTickPaint = new Paint();
        mTickPaint.setColor(tickColor);
        mTickPaint.setAntiAlias(true);
        mTickPaint.setStyle(Paint.Style.STROKE);
        mTickPaint.setStrokeWidth(mTickWidth);


        mTickProgressPaint = new Paint();
        mTickProgressPaint.setColor(tickProgressColor);
        mTickProgressPaint.setAntiAlias(true);
        mTickProgressPaint.setStyle(Paint.Style.STROKE);
        mTickProgressPaint.setStrokeWidth(mTickProgressWidth);

        mTickTextPaint = new Paint();
        mTickTextPaint.setColor(textColor);
        mTickTextPaint.setAntiAlias(true);
        mTickTextPaint.setStyle(Paint.Style.FILL);
        mTickTextPaint.setTextSize(mAngleTextSize);
        mTickTextPaint.setTextAlign(Paint.Align.CENTER);

        mTickTextColoredPaint = new Paint();
        mTickTextColoredPaint.setColor(textProgressColor);
        mTickTextColoredPaint.setAntiAlias(true);
        mTickTextColoredPaint.setStyle(Paint.Style.FILL);
        mTickTextColoredPaint.setTextSize(mAngleTextSize);
        mTickTextColoredPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int height = getDefaultSize(getSuggestedMinimumHeight(),
                heightMeasureSpec);
        int width = getDefaultSize(getSuggestedMinimumWidth(),
                widthMeasureSpec);
        int min = Math.min(width, height);
        //width = min;
        height = min / 2;


        float top = 0;
        float left = 0;
        int arcDiameter = 0;

        int tickEndToArc = (mTickOffset + mTickLength);

        arcDiameter = min - 2 * tickEndToArc;
        arcDiameter = (int) (arcDiameter - 2 * 20 * DENSITY);
        mArcRadius = arcDiameter / 2;


        top = height - (mArcRadius);
        left = width / 2 - mArcRadius;

        mArcRect.set(left, top, left + arcDiameter, top + arcDiameter);

        mTranslateX = (int) mArcRect.centerX();
        mTranslateY = (int) mArcRect.centerY();


        int thumbAngle = mAngle;
        mThumbXPos = (int) (mArcRadius * Math.cos(Math.toRadians(thumbAngle)));
        mThumbYPos = (int) (mArcRadius * Math.sin(Math.toRadians(thumbAngle)));
        setTouchInSide(mTouchInside);
        setMeasuredDimension(width, height + tickEndToArc);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.scale(1, -1, mArcRect.centerX(), mArcRect.centerY());
        canvas.drawArc(mArcRect, 0, MAX, false, mArcPaint);
        canvas.drawArc(mArcRect, 0, mAngle, false, mProgressPaint);

        canvas.restore();
        double slope, startTickX, startTickY, endTickX, endTickY, midTickX, midTickY, thetaInRadians;
        double radiusOffset = mArcRadius + mTickOffset;

        int count = mTicksBetweenLabel;
        for (int i = 360; i >= 180; i -= mTickIntervals) {
            canvas.save();
            if (count == mTicksBetweenLabel) {
                //for text
                canvas.translate(mArcRect.centerX(), mArcRect.centerY());
                thetaInRadians = Math.toRadians(i);
                slope = Math.tan(thetaInRadians);
                startTickX = (radiusOffset * Math.cos(thetaInRadians));
                midTickX = startTickX + (((mTickLength / 2)) * Math.cos(thetaInRadians));
                midTickY = slope * midTickX;
                canvas.drawText("" + (360 - i), (float) midTickX, (float) midTickY, (mAngle <= 359 - i) ? mTickTextPaint : mTickTextColoredPaint);
                count = 0;
            } else {
                //for tick
                canvas.scale(-1, 1, mArcRect.centerX(), mArcRect.centerY());
                canvas.translate(mArcRect.centerX(), mArcRect.centerY());
                canvas.rotate(180);
                thetaInRadians = Math.toRadians(360 - i);
                slope = Math.tan(thetaInRadians);
                startTickX = (radiusOffset * Math.cos(thetaInRadians));
                startTickY = slope * startTickX;
                endTickX = startTickX + ((mTickLength) * Math.cos(thetaInRadians));
                endTickY = slope * endTickX;
                canvas.drawLine((float) startTickX, (float) startTickY, (float) endTickX, (float) endTickY, (mAngle <= 359 - i) ? mTickPaint : mTickProgressPaint);
                count++;
            }
            canvas.restore();
        }


        if (mEnabled) {
            // Draw the thumb nail
            canvas.save();
            canvas.scale(-1, 1, mArcRect.centerX(), mArcRect.centerY());
            canvas.translate(mTranslateX - mThumbXPos, mTranslateY - mThumbYPos);
            mThumb.draw(canvas);
            canvas.restore();
        }
    }


    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (mThumb != null && mThumb.isStateful()) {
            int[] state = getDrawableState();
            mThumb.setState(state);
        }
        invalidate();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mEnabled) {
            this.getParent().requestDisallowInterceptTouchEvent(true);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    onStartTrackingTouch();
                    updateOnTouch(event);
                    break;
                case MotionEvent.ACTION_MOVE:
                    updateOnTouch(event);
                    break;
                case MotionEvent.ACTION_UP:
                    onStopTrackingTouch();
                    setPressed(false);
                    this.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
                case MotionEvent.ACTION_CANCEL:
                    onStopTrackingTouch();
                    setPressed(false);
                    this.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
            }
            return true;
        }
        return false;
    }

    private void onStartTrackingTouch() {
        if (mOnProtractorViewChangeListener != null) {
            mOnProtractorViewChangeListener.onStartTrackingTouch(this);
        }
    }

    private void onStopTrackingTouch() {
        if (mOnProtractorViewChangeListener != null) {
            mOnProtractorViewChangeListener.onStopTrackingTouch(this);
        }
    }


    private boolean ignoreTouch(float xPos, float yPos) {
        boolean ignore = false;
        float x = xPos - mTranslateX;
        float y = yPos - mTranslateY;

        float touchRadius = (float) Math.sqrt(((x * x) + (y * y)));
        if (touchRadius < mTouchIgnoreRadius) {
            ignore = true;
        }
        return ignore;
    }

    private void updateOnTouch(MotionEvent event) {
        boolean ignoreTouch = ignoreTouch(event.getX(), event.getY());
        if (ignoreTouch) {
            return;
        }
        setPressed(true);
        mTouchAngle = getTouchDegrees(event.getX(), event.getY());
        onProgressRefresh((int) mTouchAngle, true);
    }



    private double getTouchDegrees(float xPos, float yPos) {
        float x = xPos - mTranslateX;
        float y = yPos - mTranslateY;
        x = -x;
        // convert to arc Angle
        double angle = Math.toDegrees(Math.atan2(y, x) + (Math.PI));
        if (angle > 270)
            angle = 0;
        else if (angle > 180)
            angle = 180;
        return angle;
    }

    private void onProgressRefresh(int angle, boolean fromUser) {
        updateAngle(angle, fromUser);
    }

    private void updateAngle(int angle, boolean fromUser) {
        mAngle = (angle > MAX) ? MAX : (angle < 0) ? 0 : angle;

        if (mOnProtractorViewChangeListener != null) {
            mOnProtractorViewChangeListener.onProgressChanged(this, mAngle, fromUser);
        }
        updateThumbPosition();
        invalidate();
    }


    private void updateThumbPosition() {
        int thumbAngle = mAngle; //(int) (mStartAngle + mProgressSweep + mRotation + 90);
        mThumbXPos = (int) (mArcRadius * Math.cos(Math.toRadians(thumbAngle)));
        mThumbYPos = (int) (mArcRadius * Math.sin(Math.toRadians(thumbAngle)));
    }

    public void setTouchInSide(boolean isEnabled) {
        int thumbHalfheight = (int) mThumb.getIntrinsicHeight() / 2;
        int thumbHalfWidth = (int) mThumb.getIntrinsicWidth() / 2;
        mTouchInside = isEnabled;
        if (mTouchInside) {
            mTouchIgnoreRadius = (float) (mArcRadius / 1.5);
        } else {
            mTouchIgnoreRadius = mArcRadius - Math.min(thumbHalfWidth, thumbHalfheight);
        }
    }

    public void setOnProtractorViewChangeListener(OnProtractorViewChangeListener l) {
        mOnProtractorViewChangeListener = l;
    }

    public void removeOnProtractorViewChangeListener(){
        mOnProtractorViewChangeListener=null;
    }

    public int getAngle() {
        return mAngle;
    }

    public void setAngle(int angle) {
        this.mAngle = angle;
        onProgressRefresh((int) mAngle, false);
    }

    public boolean getTouchInside(){
        return mTouchInside;
    }

    public boolean isEnabled() {
        return mEnabled;
    }

    public void setEnabled(boolean enabled) {
        this.mEnabled = enabled;
    }

    public int getProgressColor() {
        return mProgressPaint.getColor();
    }

    public void setProgressColor(int color) {
        mProgressPaint.setColor(color);
        invalidate();
    }

    public int getArcColor() {
        return mArcPaint.getColor();
    }

    public void setArcColor(int color) {
        mArcPaint.setColor(color);
        invalidate();
    }

    public int getProgressWidth() {
        return mProgressWidth;
    }

    public void setProgressWidth(int mProgressWidth) {
        this.mProgressWidth = mProgressWidth;
        mProgressPaint.setStrokeWidth(mProgressWidth);
    }

    public int getArcWidth() {
        return mArcWidth;
    }

    public void setArcWidth(int mArcWidth) {
        this.mArcWidth = mArcWidth;
        mArcPaint.setStrokeWidth(mArcWidth);
    }

    public boolean ismRoundedEdges() {
        return mRoundedEdges;
    }

    public void setmRoundedEdges(boolean mRoundedEdges) {
        this.mRoundedEdges = mRoundedEdges;
    }

    public Drawable getmThumb() {
        return mThumb;
    }

    public void setmThumb(Drawable mThumb) {
        this.mThumb = mThumb;
    }

    public int getmAngleTextSize() {
        return mAngleTextSize;
    }

    public void setmAngleTextSize(int mAngleTextSize) {
        this.mAngleTextSize = mAngleTextSize;
    }

    public int getmTickOffset() {
        return mTickOffset;
    }

    public void setmTickOffset(int mTickOffset) {
        this.mTickOffset = mTickOffset;
    }

    public int getmTickLength() {
        return mTickLength;
    }

    public void setmTickLength(int mTickLength) {
        this.mTickLength = mTickLength;
    }

    public int getmAngle() {
        return mAngle;
    }

    public void setmAngle(int mAngle) {
        this.mAngle = mAngle;
    }

    public boolean ismTouchInside() {
        return mTouchInside;
    }

    public void setmTouchInside(boolean mTouchInside) {
        this.mTouchInside = mTouchInside;
    }

    public boolean ismEnabled() {
        return mEnabled;
    }

    public void setmEnabled(boolean mEnabled) {
        this.mEnabled = mEnabled;
    }

    public int getmTicksBetweenLabel() {
        return mTicksBetweenLabel;
    }

    public void setmTicksBetweenLabel(int mTicksBetweenLabel) {
        this.mTicksBetweenLabel = mTicksBetweenLabel;
    }

    public int getmTickIntervals() {
        return mTickIntervals;
    }

    public void setmTickIntervals(int mTickIntervals) {
        this.mTickIntervals = mTickIntervals;
    }
}
