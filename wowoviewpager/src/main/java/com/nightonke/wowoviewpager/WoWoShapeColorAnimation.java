package com.nightonke.wowoviewpager;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;

import com.nightonke.wowoviewpager.Color.ColorChangeType;
import com.nightonke.wowoviewpager.Eases.EaseType;

/**
 * Created by Weiping on 2016/3/3.
 */

/**
 * animation to change the color of shape-drawable of view
 * notice that the background of the view must be:
 *
 * <?xml version="1.0" encoding="utf-8"?>
 * <shape xmlns:android="http://schemas.android.com/apk/res/android"
 *         android:shape="oval"  >
 *     <solid android:color="@color/red"/>
 * </shape>
 *
 */
public class WoWoShapeColorAnimation extends PageAnimation {

    private EaseType easeType;
    private boolean useSameEaseTypeBack = true;

    private ColorChangeType colorChangeType;

    private int targetColor;
    private int fromColor;
    
    private int targetA = -1;
    private int targetR = -1;
    private int targetG = -1;
    private int targetB = -1;
    private float[] targetHSV = new float[3];
    private int fromA = -1;
    private int fromR = -1;
    private int fromG = -1;
    private int fromB = -1;
    private float[] fromHSV = new float[3];

    public WoWoShapeColorAnimation(int page, float startOffset, float endOffset, int fromColor, int targetColor, ColorChangeType colorChangeType, EaseType easeType, boolean useSameEaseTypeBack) {
        setPage(page);
        setStartOffset(startOffset);
        setEndOffset(endOffset);

        this.easeType = easeType;
        this.useSameEaseTypeBack = useSameEaseTypeBack;
        this.fromColor = fromColor;
        this.targetColor = targetColor;
        setARGBandHSV();

        this.colorChangeType = colorChangeType;
    }

    private float lastPositionOffset = -1;

    private boolean lastTimeIsExceed = false;

    @Override
    public void play(View onView, float positionOffset) {

        if (positionOffset < getStartOffset()) {
            return;
        }

        if (positionOffset >= getEndOffset()) {
            // if the positionOffset exceeds the endOffset,
            // we should set onView to target color
            // otherwise there may be offsets between target color and actually color
            if (lastTimeIsExceed) return;
            // if the last time we do this, just return
            ((GradientDrawable)onView.getBackground()).setColor(targetColor);
            lastTimeIsExceed = true;
            return;
        }
        lastTimeIsExceed = false;

        // get the true offset
        positionOffset = (positionOffset - getStartOffset()) / (getEndOffset() - getStartOffset());
        float movementOffset;

        if (lastPositionOffset == -1) {
            // first movement
            movementOffset = easeType.getOffset(positionOffset);
        } else {
            if (positionOffset < lastPositionOffset) {
                // back
                if (useSameEaseTypeBack) {
                    movementOffset = 1 - easeType.getOffset(1 - positionOffset);
                } else {
                    movementOffset = easeType.getOffset(positionOffset);
                }
            } else {
                // forward
                movementOffset = easeType.getOffset(positionOffset);
            }
        }
        lastPositionOffset = positionOffset;

        if (colorChangeType == ColorChangeType.RGB) {
            ((GradientDrawable)onView.getBackground()).setColor(
                    Color.argb(
                            fromA + (int)((targetA - fromA) * movementOffset),
                            fromR + (int)((targetR - fromR) * movementOffset),
                            fromG + (int)((targetG - fromG) * movementOffset),
                            fromB + (int)((targetB - fromB) * movementOffset))
            );
        } else {
            ((GradientDrawable)onView.getBackground()).setColor(Color.HSVToColor(new float[]{
                fromHSV[0] + (targetHSV[0] - fromHSV[0]) * movementOffset,
                fromHSV[1] + (targetHSV[1] - fromHSV[1]) * movementOffset,
                fromHSV[2] + (targetHSV[2] - fromHSV[2]) * movementOffset
            }));
        }
    }

    private void setARGBandHSV() {
        targetA = Color.alpha(targetColor);
        targetR = Color.red(targetColor);
        targetG = Color.green(targetColor);
        targetB = Color.blue(targetColor);
        Color.colorToHSV(targetColor, targetHSV);
        
        fromA = Color.alpha(fromColor);
        fromR = Color.red(fromColor);
        fromG = Color.green(fromColor);
        fromB = Color.blue(fromColor);
        Color.colorToHSV(fromColor, fromHSV);
    }
}
