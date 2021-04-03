package com.liah.webradioapplication.adapters

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.media.AudioManager
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.liah.webradioapplication.Actions
import com.liah.webradioapplication.R
import com.liah.webradioapplication.WebViewForegroundService
import com.liah.webradioapplication.api.Radio
import kotlinx.android.synthetic.main.activity_main.*

class RadioListAdapter(private val radioList: Array<Radio>, private val activity: Activity) : RecyclerView.Adapter<RadioListAdapter.CustomViewHolder>() {
    private val colorList = arrayListOf<String>(
            "#eb3b5a",
            "#fa8231",
            "#f7b731",
            "#20bf6b",
            "#0fb9b1",
            "#2d98da",
            "#3867d6",
            "#8854d0",
            "#4b6584",
            "#394454",
    )
    var mediaPlayer : MediaPlayer? = MediaPlayer().apply {
        setAudioStreamType(AudioManager.STREAM_MUSIC)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_radio, parent, false)


        return  CustomViewHolder(view).apply {

            itemView.setOnClickListener {
                val radioItem = radioList[adapterPosition]

                activity.tv_info_radioTitle.text = radioItem.radioTitle
                activity.tv_info_radioFreq.text = radioItem.radioFreq
                activity.tv_info_radioFreq.setTextColor(Color.parseColor(colorList[adapterPosition]))

                activity.tv_player_btn.tag = "pause"
                activity.tv_player_btn.setBackgroundResource(R.drawable.ic_baseline_play_circle_96)
                activity.tv_player_btn.isClickable = false
                activity.tv_info_radioSubject.text = "제목 로드중..."
                activity.tv_loading.text = "로딩중..."

                mediaPlayer?.release()
                mediaPlayer = null
                mediaPlayer = MediaPlayer().apply {
                    setAudioStreamType(AudioManager.STREAM_MUSIC)
                    setDataSource(radioItem.radioHlsSlug)
                    prepare() // might take long! (for buffering, etc)
                    start()
                }
                val intent = Intent(activity, WebViewForegroundService::class.java)
                intent.action = Actions.START_FOREGROUND
                activity.startService(intent)

                activity.tv_player_btn.tag = "play"
                activity.tv_player_btn.setBackgroundResource(R.drawable.ic_baseline_pause_circle_96)
                activity.tv_player_btn.isClickable = true
                activity.tv_info_radioSubject.text = "임시제목"
                activity.tv_loading.text = ""

//                activity.wv_backgroundWebview.loadUrl(radioItem.radioWebSlug)

                activity.tv_player_btn.setOnClickListener {
                    if(it.tag.toString() == "play"){ // 현재 재생중
                        it.tag = "pause"
                        it.setBackgroundResource(R.drawable.ic_baseline_play_circle_96)
                        mediaPlayer?.pause()

                        val intent = Intent(activity, WebViewForegroundService::class.java)
                        intent.action = Actions.STOP_FOREGROUND
                        activity.startService(intent)


                    } else { // 현재 중지
                        it.tag = "play"
                        it.setBackgroundResource(R.drawable.ic_baseline_pause_circle_96)
                        mediaPlayer?.start()

                        val intent = Intent(activity, WebViewForegroundService::class.java)
                        intent.action = Actions.START_FOREGROUND
                        activity.startService(intent)
                    }
                }
            }
        }
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val radioItem = radioList[position]
        val radioBackgroundColor = Color.parseColor(colorList[position])
        holder.radioTitle.text = radioItem.radioTitle
        holder.radioFreq.text = radioItem.radioFreq
        holder.radioBackground.setBackgroundColor(radioBackgroundColor)
//        holder.radioBackground


    }

    override fun getItemCount(): Int {
        return radioList.size
    }

    class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val radioTitle = itemView.findViewById<TextView>(R.id.tv_radioTitle)
        val radioFreq = itemView.findViewById<TextView>(R.id.tv_radioFreq)
        val radioBackground = itemView.findViewById<ConstraintLayout>(R.id.CL_radioItem)
    }
}
//private val colorList = arrayListOf<String>(
//        "#eb3b5a",
//        "#fa8231",
//        "#f7b731",
//        "#20bf6b",
//        "#0fb9b1",
//        "#2d98da",
//        "#3867d6",
//        "#8854d0",
//        "#4b6584",
//        "#394454",
////            "#d1d8e0"
//)