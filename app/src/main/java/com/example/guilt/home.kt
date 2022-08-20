package com.example.guilt

import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.guilt.databinding.FragmentHomeBinding
import com.example.guilt.repository.Apps
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class home : Fragment() {


    private lateinit var binding: FragmentHomeBinding
    var finalRecylerList = ArrayList<AppItem>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //  val view = inflater.inflate(R.layout.fragment_home, container, false)
        val application = requireActivity().application
        val viewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(HomeViewModel::class.java)

        binding = FragmentHomeBinding.inflate(layoutInflater)
        var RecyclerAppList = ArrayList<AppItem>()




        CoroutineScope(Dispatchers.Default).launch {
            val check = viewModel.isDbEmpty()
            if (check) {
                Log.d("DATA-DB", "NO RECORDS")
                binding.noDataImage.visibility = View.VISIBLE
                binding.noDataText.visibility = View.VISIBLE
                //  Toast.makeText(context, "Empty DB", Toast.LENGTH_SHORT).show()
            } else {
                binding.noDataImage.visibility = View.GONE
                binding.noDataText.visibility = View.GONE

                binding.imageView.visibility = View.VISIBLE
                binding.imageView2.visibility = View.VISIBLE
                binding.avatar.visibility = View.VISIBLE
                binding.heading.visibility = View.VISIBLE
                binding.screenTimeData.visibility = View.VISIBLE
                binding.recentAppHeading.visibility = View.VISIBLE
                binding.timeHeading.visibility = View.VISIBLE

                val appList = viewModel.loadAllData()
                for (app: Apps in appList) {
                    var drawable: Drawable = app.packageName?.let { getIcon(it) }!!
                    Log.d("DATA-DB", "${app.appName} : ${app.longitude} : ${app.latitude}")

                    var appItem: AppItem = AppItem(
                        appName = app.appName,
                        packageName = app.packageName,
                        totalUsage = app.timeStamp,
                        latitude = app.latitude,
                        longitude = app.longitude,
                        appIcon = drawable
                    )
                    RecyclerAppList.add(appItem)
                }
            }
        }

        Thread.sleep(1000)

        binding.appRecycler.visibility = View.VISIBLE
        var layoutManager: RecyclerView.LayoutManager? = null
        var adapter: RecyclerView.Adapter<RecyclerAdapter.MyViewHolder>? = null
        layoutManager = LinearLayoutManager(requireActivity().applicationContext)
        binding.appRecycler.layoutManager = layoutManager
        adapter = RecyclerAdapter(RecyclerAppList)
        binding.appRecycler.adapter = adapter



        return binding.root
    }

    fun getIcon(packageName: String): Drawable? {

        try {
            val icon = activity?.packageManager?.getApplicationIcon(packageName)
            return icon
        } catch (e: PackageManager.NameNotFoundException) {
            return null
        }
    }


}