package com.nowwt.svr.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.nowwt.svr.stories.StoriesActivity
import com.r00786.stories.CacheDataSourceCustom
import com.r00786.stories.Constants
import com.r00786.stories.Model.Result
import com.r00786.stories.R
import kotlinx.android.synthetic.main.layout_rv_child.view.*


/**
 * Created by Lucifer on 24-05-2018.
 */

class AdapterChildStories(context: Context) : RecyclerView.Adapter<AdapterChildStories.ViewHolder>() {
    var listItems = ArrayList<Result>()
    lateinit var viewHolderObj: ViewHolder
    var directReply = false

    var caching = CacheDataSourceCustom(context, 100 * 1024 * 1024, 5 * 1024 * 1024)

    val context = context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_rv_child, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        viewHolderObj = holder;
        holder.bindViews(listItems[position], position)
    }

    override fun getItemCount(): Int {
        if (listItems != null) {
            return listItems.size
        } else {
            return 0
        }
    }

    lateinit var callbacks: StoryLoaderCallbacks
    fun setCallBacksStoryCallBacks(str: StoryLoaderCallbacks) {
        callbacks = str
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {



        fun setCircleText(obj: Result) {


            itemView.et_text.text = obj.text
            listItems[position].isReplyKbVisibilty = false
            itemView.fl_text.visibility = View.VISIBLE
            itemView.et_text.textSize = getSizeAsPerLength(obj.text)

            var height = StoriesActivity.getViewHeight(itemView.fl_text)
            var rlParams = RelativeLayout.LayoutParams(height, height)
            rlParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            rlParams.marginStart = 40
            rlParams.bottomMargin = 40
            itemView.fl_text.layoutParams = rlParams
        }

        fun bindViews(obj: Result, position: Int) {
            setCircleText(obj)
            if (obj.type == Constants.VIDEO) {
                itemView.image.visibility = View.GONE
                itemView.player.visibility = View.VISIBLE
                var recordFilename = obj.photo
                callbacks.isStoryLoading()
                var bandwidthMeter = DefaultBandwidthMeter()
                itemView.player.requestFocus()
                val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory(bandwidthMeter)
                var trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)
                if (obj.simpleExoPlayer != null) {
                    obj.simpleExoPlayer.stop()
                }
                obj.simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector)
                itemView.player.player = obj.simpleExoPlayer!!
                itemView.player.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM)
//
                val extractorsFactory = DefaultExtractorsFactory()
//                val mediaSource = ExtractorMediaSource(Uri.parse(recordFilename),
//                        mediaDataSourceFactory, extractorsFactory, null, null)

                //With caching
                val mediaSource = ExtractorMediaSource(Uri.parse(recordFilename), caching, extractorsFactory, null, null)
                if (position == callbacks.getVideoPlayerIndex()) {
                    if (obj.isDurationSet) {
                        callbacks.setVideoDuration(obj.duration, position)
                        callbacks.isStoryLoaded()
                    }
                    if (obj.eventListener == null) {
                        obj.eventListener = object : Player.DefaultEventListener() {
                            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                                super.onPlayerStateChanged(playWhenReady, playbackState)

                                if (playWhenReady && playbackState == Player.STATE_READY && !obj.isDurationSet) {
                                    // media actually playing
                                    if (position == callbacks.getVideoPlayerIndex()) {
                                        var realDurationMillis = obj.simpleExoPlayer.duration
                                        callbacks.setVideoDuration(realDurationMillis, position)
                                        obj.duration = realDurationMillis;
                                        obj.isDurationSet = true
                                    }
                                    callbacks.isStoryLoaded()
                                } else if (playbackState != Player.STATE_BUFFERING && playWhenReady && obj.isDurationSet) {
                                    // might be idle (plays after prepare()),
                                    // buffering (plays when data available)
                                    // or ended (plays when seek away from end)
                                    if (position == callbacks.getVideoPlayerIndex())
                                        callbacks.isStoryLoaded()
                                } else {
                                    // player paused in any state
                                    if (position == callbacks.getVideoPlayerIndex())
                                        callbacks.isStoryLoading()
                                }
                            }

                        }
                    } else {
                        obj.simpleExoPlayer.removeListener(obj.eventListener)
                    }
                    obj.simpleExoPlayer.addListener(obj.eventListener)

                    if (position == callbacks.getVideoPlayerIndex()) {
                        obj.simpleExoPlayer.prepare(mediaSource)
                        obj.simpleExoPlayer.playWhenReady = true
                        callbacks.isStoryLoading()
                    }
                }
            }
            //////////////////////////
            else if (obj.type == Constants.PHOTO || obj.type == Constants.PHOTOPNG) {
                itemView.image.visibility = View.VISIBLE
                itemView.player.visibility = View.GONE

                if (position == callbacks.getVideoPlayerIndex()) {
                    callbacks.isStoryLoaded()
                    Glide.with(context).load(obj.photo).asBitmap().into(object : SimpleTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, glideAnimation: GlideAnimation<in Bitmap>) {
                            itemView.image.setImageBitmap(resource)
                            if (position == callbacks.getVideoPlayerIndex()) {
                                callbacks.isStoryLoaded()
                            }
                        }

                        override fun onLoadStarted(placeholder: Drawable?) {
                            super.onLoadStarted(placeholder)
                            if (position == callbacks.getVideoPlayerIndex()) {
                                callbacks.isStoryLoading()
                            }
                        }

                        override fun onLoadFailed(e: Exception?, errorDrawable: Drawable?) {
                            super.onLoadFailed(e, errorDrawable)
                            if (position == callbacks.getVideoPlayerIndex()) {
                                callbacks.isStoryLoading()
                            }
                        }


                    })
                }

            }


        }


    }

    interface StoryLoaderCallbacks {
        fun isStoryLoading()
        fun isStoryLoaded()
        fun getVisibleItem(): Int
        fun setVideoDuration(time: Long, position: Int)
        fun getVideoPlayerIndex(): Int

    }


    fun addData(listItems: ArrayList<Result>) {
        var size = this.listItems.size
        this.listItems = listItems
        var sizeNew = this.listItems.size
        notifyItemRangeChanged(size, sizeNew)

    }

    fun getSizeAsPerLength(string: String): Float {
        if (string.length >= 20) {
            return StoriesActivity.convertToSp(context, 20)
        } else if (string.length >= 40) {
            return StoriesActivity.convertToSp(context, 18)
        } else if (string.length >= 55) {
            return StoriesActivity.convertToSp(context, 16)
        }
        return StoriesActivity.convertToSp(context, 22)
    }

    fun pausePlayer(position: Int) {

        if (listItems[position] != null && listItems[position].simpleExoPlayer?.playWhenReady!!) {
            listItems[position]?.simpleExoPlayer?.playWhenReady = false
            listItems[position]?.simpleExoPlayer?.playbackState
            listItems[position]?.simpleExoPlayer.stop()
            listItems[position]?.simpleExoPlayer.release()

            if (listItems[position]?.eventListener != null)
                listItems[position]?.simpleExoPlayer.removeListener(listItems[position].eventListener)
        }
    }

    fun backPressed() {

    }

}
