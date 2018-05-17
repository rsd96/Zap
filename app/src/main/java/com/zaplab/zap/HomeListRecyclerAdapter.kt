package com.zaplab.zap

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import java.io.Serializable

/**
 * Created by Ramshad on 4/22/18.
 */
class HomeListRecyclerAdapter(_context: Context, _list: MutableList<Vehicle>, idList: MutableList<String>) :
        RecyclerView.Adapter<HomeListRecyclerAdapter.MyViewHolder>() {

    val myContext = _context
    var list = _list
    var idList = idList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeListRecyclerAdapter.MyViewHolder {
        var v = LayoutInflater.from(myContext).inflate(R.layout.vehicle_listing_content, parent, false)
        var myViewHolder = MyViewHolder(v)
        return myViewHolder
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: HomeListRecyclerAdapter.MyViewHolder, position: Int) {


        var imageUri = list[position].imageList[0]

        //holder.vehicleImage.scaleType = ImageView.ScaleType.CENTER_CROP
//        photoView.setAdjustViewBounds(true);

        Picasso.with(myContext)
                .load(imageUri)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(holder?.vehicleImage, object : Callback {
                    override fun onSuccess() {

                    }

                    override fun onError() {
                        // Try again online if cache failed
                        Picasso.with(myContext)
                                .load(imageUri)
                                .into(holder?.vehicleImage, object : Callback {
                                    override fun onSuccess() {}

                                    override fun onError() {}
                                })
                    }
                })

        holder.vehicleImage.setOnClickListener({
            var intent = Intent(myContext, VehicleDetailActivity::class.java)
            intent.putExtra("vehicle", list[position] as Serializable)
            intent.putExtra("vehicleId", idList[position])
            myContext.startActivity(intent)
        })

        holder.title.text = "${list.get(position).make} - ${list.get(position).model}"
        holder.desc.text = list.get(position).desc
        holder.rent.text = "$ ${list.get(position).rent.toString()}p/hr"
        holder.location.text = "${list[position].city},${list[position].country}"
    }


    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val vehicleImage: ImageView = view.findViewById(R.id.ivVehicleCardImage)
        val title: TextView = view.findViewById(R.id.tvVehicleCardTitle)
        val desc: TextView = view.findViewById(R.id.tvVehicleCardDesc)
        val rent: TextView = view.findViewById(R.id.tvVehicleCardRent)
        val location: TextView = view.findViewById(R.id.tvVehicleCardLocation)


    }
}