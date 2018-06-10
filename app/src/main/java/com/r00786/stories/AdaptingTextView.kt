package com.r00786.stories

import android.content.Context
import android.util.AttributeSet

class AdaptingTextView : android.support.v7.widget.AppCompatTextView {

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context) : super(context) {}

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        // set fitting lines to prevent cut text
        val fittingLines = h / this.lineHeight
        if (fittingLines > 0) {
            this.setLines(fittingLines)
        }
    }

}