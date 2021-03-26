package com.liah.webradioapplication.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.liah.webradioapplication.R
import com.liah.webradioapplication.api.Radio
import kotlinx.android.synthetic.main.activity_main.*

class RadioListAdapter(private val radioList: Array<Radio>, private val activity: Activity) : RecyclerView.Adapter<RadioListAdapter.CustomViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_radio, parent, false)

        return  CustomViewHolder(view).apply {


            itemView.setOnClickListener {
//                activity.wv_backgrounWebview.loadUrl("about:blank")
//                activity.wv_backgrounWebview.clearView()
                val radioItem = radioList[adapterPosition]

                activity.tv_info_radioTitle.text = radioItem.radioTitle
                activity.tv_info_radioFreq.text = radioItem.radioFreq

                activity.wv_backgrounWebview.loadUrl(radioItem.radioWebSlug)
                activity.tv_info_radioSubject.text = "제목 로드중..."
                activity.tv_loading.text = "로딩중..."
//                activity.tv_loading.isVisible = true
                activity.tv_player_btn.tag = "pause"
                activity.tv_player_btn.setBackgroundResource(R.drawable.ic_baseline_play_circle_72)
                activity.tv_player_btn.isClickable = false
            }

        }
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val radioItem = radioList[position]
        holder.radioTitle.text = radioItem.radioTitle
        holder.radioFreq.text = radioItem.radioFreq

    }

    override fun getItemCount(): Int {
        return radioList.size
    }

    class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val radioTitle = itemView.findViewById<TextView>(R.id.tv_radioTitle)
        val radioFreq = itemView.findViewById<TextView>(R.id.tv_radioFreq)
    }
}
