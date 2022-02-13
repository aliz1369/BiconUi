package com.tivasoft.biconui.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CustomLinearLayoutManager extends LinearLayoutManager {

    public CustomLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public void updateChildrenAlpha() {
        int centerItem = getChildCount() / 2;
        for (int i = 0; i < centerItem; i++) {
            View child = getChildAt(i);
            float maxDist = (getWidth() / 2);
            float right = getDecoratedRight(child);
            float left = getDecoratedLeft(child);
            float childCenter = left + ((right - left) / 2); // Get item center position
            float center = getWidth() / 2; // Get RecyclerView's center position
            float alpha = (Math.abs((center - childCenter) - maxDist)) / maxDist;
            child.setAlpha(alpha);
            // Map between 0f and 1f the abs value of the distance
            // between the center of item and center of the RecyclerView
            // and set it as alpha
        }

        for (int i = centerItem; i < getChildCount(); i++) {
            View child = getChildAt(i);
            float maxDist = (getWidth() / 2);
            float right = getDecoratedRight(child);
            float left = getDecoratedLeft(child);
            float childCenter = left + ((right - left) / 2); // Get item center position
            float center = getWidth() / 2; // Get RecyclerView's center position
            float alpha = (Math.abs((childCenter - center) - maxDist)) / maxDist;
            child.setAlpha(alpha);
            // Map between 0f and 1f the abs value of the distance
            // between the center of item and center of the RecyclerView
            // and set it as alpha
        }
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        updateChildrenAlpha();
        return super.scrollHorizontallyBy(dx, recycler, state);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
        updateChildrenAlpha();
    }
}
