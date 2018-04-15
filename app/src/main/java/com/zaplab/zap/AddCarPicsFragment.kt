package com.zaplab.zap

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.nguyenhoanglam.imagepicker.model.Config
import com.nguyenhoanglam.imagepicker.model.Image
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker
import com.takusemba.multisnaprecyclerview.OnSnapListener
import kotlinx.android.synthetic.main.fragment_add_car_pics.*
import java.io.File
import java.util.*






/**
 * Created by Ramshad on 4/11/18.
 */
class AddCarPicsFragment: Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_car_pics, null)
    }


    var images = ArrayList<Image>()
    var imagePos = 0
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var adapter = RestRecyclerAdapter(activity?.applicationContext!!, images)
        val layoutManager = LinearLayoutManager(activity?.applicationContext, LinearLayoutManager.HORIZONTAL, false)
        rvAddCarPicsList.layoutManager = layoutManager
        rvAddCarPicsList.adapter = adapter
        rvAddCarPicsList.setOnSnapListener(OnSnapListener {
            imagePos = it

        })

        btnAddCarRemovePic.setOnClickListener {
            images.remove(images[imagePos])
            if (imagePos == images.size)
                imagePos--
            if (images.size < 1)
                btnAddCarRemovePic.visibility = View.GONE
            adapter.notifyDataSetChanged()
        }

        btnAddCarPicsNext.setOnClickListener({
            if (images.size < 4) {
                var dialog =  AlertDialog.Builder(activity)
                dialog.setMessage("Please add at least 4 pictures consisting of all sides of vehicle")
                        .setPositiveButton("Okay", null)
                dialog.create().show()
            } else {
                AddCarActivity.images.addAll(images)
                (activity as AddCarActivity).nextPager(2)
            }
        })


        btnAddCarPics.setOnClickListener({

            ImagePicker.with(this)                         //  Initialize ImagePicker with activity or fragment context
                    .setToolbarColor("#212121")         //  Toolbar color
                    .setStatusBarColor("#000000")       //  StatusBar color (works with SDK >= 21  )
                    .setToolbarTextColor("#FFFFFF")     //  Toolbar text color (Title and Done button)
                    .setToolbarIconColor("#FFFFFF")     //  Toolbar icon color (Back and Camera button)
                    .setProgressBarColor("#4CAF50")     //  ProgressBar color
                    .setBackgroundColor("#212121")      //  Background color
                    .setCameraOnly(false)               //  Camera mode
                    .setMultipleMode(true)              //  Select multiple images or single image
                    .setFolderMode(true)                //  Folder mode
                    .setShowCamera(true)                //  Show camera button
                    .setFolderTitle("Albums")           //  Folder title (works with FolderMode = true)
                    .setImageTitle("Galleries")         //  Image title (works with FolderMode = false)
                    .setDoneTitle("Done")               //  Done button title
                    .setLimitMessage("You have reached selection limit")    // Selection limit message
                    .setMaxSize(10)                     //  Max images can be selected
                    .setSavePath("ImagePicker")         //  Image capture folder name
                    .setSelectedImages(images)          //  Selected images
                    .setKeepScreenOn(true)              //  Keep screen on when selecting images
                    .start()                         //  Start ImagePicker
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Config.RC_PICK_IMAGES && resultCode == RESULT_OK && data != null) {
            images.clear()
            images.addAll(data.getParcelableArrayListExtra<Image>(Config.EXTRA_IMAGES))
            rvAddCarPicsList.adapter.notifyDataSetChanged()
            btnAddCarRemovePic.visibility = View.VISIBLE

        }
        super.onActivityResult(requestCode, resultCode, data)  // THIS METHOD SHOULD BE HERE so that ImagePicker works with fragment
    }


}



class RestRecyclerAdapter(_context: Context, _list: MutableList<Image> = mutableListOf()) :
        RecyclerView.Adapter<RestRecyclerAdapter.MyViewHolder>() {

    val myContext = _context
    var list = _list

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var v = LayoutInflater.from(myContext).inflate(R.layout.add_car_pics_recycler_content, parent, false)
        var myViewHolder : MyViewHolder= MyViewHolder(v)
        return myViewHolder
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val imgFile = File(list[position].path)

        if (imgFile.exists()) {
            val myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath())
            holder?.ivCarImage.setImageBitmap(myBitmap)

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }


    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var ivCarImage: ImageView = view.findViewById(R.id.ivAddCarPicsListImage)

    }
}