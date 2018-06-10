package com.r00786.stories

/**
 * Created by Lucifer on 24-05-2018.
 */


import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.LinearLayout
import android.widget.ProgressBar
import java.util.*

/**
 * Created by rahuljanagouda on 29/09/17.
 */

class StoryStatusView : LinearLayout {
    private val progressBars = ArrayList<ProgressBar>()
    private val animators = ArrayList<ObjectAnimator>()
    private var storiesCount = -1
    private var current = 0
    private var userInteractionListener: UserInteractionListener? = null
    internal var isReverse: Boolean = false
    internal var isComplete: Boolean = false
    interface UserInteractionListener {
        fun onNext()

        fun onPrev()

        fun onComplete()
    }

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    private fun bindViews() {
        removeAllViews()

        for (i in 0 until storiesCount) {
            val p = createProgressBar()
            p.max = MAX_PROGRESS
            progressBars.add(p)
            addView(p)
            if (i + 1 < storiesCount) {
                addView(createSpace())
            }
        }
    }

    private fun createProgressBar(): ProgressBar {
        val p = ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal)
        p.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        p.progressDrawable = ContextCompat.getDrawable(context, R.drawable.progress_bg)
        return p
    }

    private fun createSpace(): View {
        val v = View(context)
        v.layoutParams = LinearLayout.LayoutParams(SPACE_BETWEEN_PROGRESS_BARS, LinearLayout.LayoutParams.WRAP_CONTENT)
        return v
    }


    fun setStoriesCount(storiesCount: Int) {
        this.storiesCount = storiesCount
        bindViews()
    }


    fun setUserInteractionListener(userInteractionListener: UserInteractionListener) {
        this.userInteractionListener = userInteractionListener
    }


    fun skip() {

        if (isComplete) return
        val p = progressBars[current]
        p.progress = p.max
        animators[current].cancel()
        moveToNext()
    }

    fun pause() {
        if (isComplete) return
        val p = progressBars[current]
        p.progress = p.progress
        animators[current].pause()
    }

    fun resume() {
        if (isComplete) return
        val p = progressBars[current]
        p.progress = p.progress
        animators[current].resume()

    }


    fun reverse() {
        if (isComplete) return
        var p = progressBars[current]
        p.progress = 0
        isReverse = true
        animators[current].cancel()
        if (0 <= current - 1) {
            p = progressBars[current - 1]
            p.progress = 0
            animators[--current].start()
        } else {
            animators[current].start()
        }
    }


    fun setStoryDuration(duration: Long) {
        animators.clear()
        for (i in progressBars.indices) {
            animators.add(createAnimator(i, duration))
        }
    }


    fun setStoriesCountWithDurations(durations: LongArray) {
        storiesCount = durations.size
        bindViews()
        animators.clear()
        for (i in progressBars.indices) {
            animators.add(createAnimator(i, durations[i]))
        }
    }


    fun playStories() {
        animators[0].start()
    }

    /**
     * Need to call when Activity or Fragment destroy
     */
    fun destroy() {
        for (a in animators) {
            a.removeAllListeners()
            a.cancel()
        }
    }

    private fun createAnimator(index: Int, duration: Long): ObjectAnimator {
        val animation = ObjectAnimator.ofInt(progressBars[index], "progress", MAX_PROGRESS)
        animation.interpolator = LinearInterpolator()
        animation.duration = duration
        animation.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                current = index
            }

            override fun onAnimationEnd(animation: Animator) {
                if (isReverse) {
                    isReverse = false
                    if (userInteractionListener != null) userInteractionListener!!.onPrev()
                    return
                }
                //                int next = current + 1;
                //                if (next <= (animators.size() - 1)) {
                //                    if (userInteractionListener != null) userInteractionListener.onNext();
                //                    animators.get(next).start();
                //                } else {
                //                    isComplete = true;
                //                    if (userInteractionListener != null) userInteractionListener.onComplete();
                //                }
            }

            override fun onAnimationCancel(animation: Animator) {}

            override fun onAnimationRepeat(animation: Animator) {

            }
        })
        return animation
    }

    fun moveToNext() {

        val next = current + 1
        if (next <= animators.size - 1) {
            //            if (userInteractionListener != null) userInteractionListener.onNext();
            animators[next].start()
        } else {
            isComplete = true
            if (userInteractionListener != null) userInteractionListener!!.onComplete()
        }
    }

    fun setCustomDuration(duration: Long, position: Int) {
        if (!isReverse) {
            animators[position].duration = duration * 2
        }


    }

    fun clear() {
        progressBars.clear()
        storiesCount = -1
        animators.clear()
    }

    companion object {

        private val MAX_PROGRESS = 100
        private val SPACE_BETWEEN_PROGRESS_BARS = 5
    }
}