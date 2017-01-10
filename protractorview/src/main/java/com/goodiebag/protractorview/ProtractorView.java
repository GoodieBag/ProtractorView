package com.goodiebag.protractorview;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
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

    private int mAngleTextSize = 8;

    private int mTickOffset = 10;
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
    }

    public ProtractorView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ProtractorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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
    }
}
