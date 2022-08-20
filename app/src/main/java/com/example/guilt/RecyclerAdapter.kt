package com.example.guilt

import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import java.security.AccessController.getContext
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter


class RecyclerAdapter(private var appList: List<AppItem> = emptyList<AppItem>()) :
    RecyclerView.Adapter<RecyclerAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.app_item, parent, false)
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val curItem = appList[position]
        val t = Instant.ofEpochMilli(curItem.totalUsage!!).atZone(ZoneId.of("IST")).toLocalTime()
        //  val time = LocalDateTime.ofEpochSecond(curItem.totalUsage!!,0, ZoneOffset.UTC)
        val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH")
        dateTimeFormatter.format(t)
        Log.d("TIME", "${t.hour}")

        holder.appName.text = curItem.appName
        holder.appIcon.setImageDrawable(curItem.appIcon)
        holder.usageTime.text = "${t.hour}:${t.minute}"

    }

    override fun getItemCount(): Int {
        return appList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var appName: TextView
        var usageTime: TextView
        var appIcon: ImageView

        init {
            appName = itemView.findViewById(R.id.app_name)
            appIcon = itemView.findViewById(R.id.app_icon)
            usageTime = itemView.findViewById(R.id.usage_time)

            itemView.setOnClickListener {
                val curItem = appList[adapterPosition]
                val action = homeDirections.actionHome2ToMap(
                    curItem.longitude!!,
                    curItem.latitude!!,
                    curItem.appName!!,
                    curItem.packageName!!
                )
                Navigation.findNavController(it).navigate(action)
            }
        }
    }

}
