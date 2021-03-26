package com.liah.webradioapplication.adapters

import android.app.Activity
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.liah.webradioapplication.R
import com.liah.webradioapplication.api.Radio
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.list_radio.*

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
//            "#d1d8e0"
    )
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_radio, parent, false)

        return  CustomViewHolder(view).apply {


            itemView.setOnClickListener {
                val radioItem = radioList[adapterPosition]

                activity.tv_info_radioTitle.text = radioItem.radioTitle
                activity.tv_info_radioFreq.text = radioItem.radioFreq
                activity.tv_info_radioFreq.setTextColor(Color.parseColor(colorList[adapterPosition]))

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