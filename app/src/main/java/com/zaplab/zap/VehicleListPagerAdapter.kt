package com.zaplab.zap

import android.content.Context
import android.content.Intent
import android.support.v4.view.PagerAdapter
import android.support.v7.widget.CardView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import com.varunest.sparkbutton.SparkButton
import java.io.Serializable

/**
 * Created by Ramshad on 4/6/18.
 */

class VehicleListPagerAdapter(_context: Context, _listOfVehicle: MutableList<Vehicle>): PagerAdapter() {
    val context = _context
    val listOfVehicle = _listOfVehicle
    override fun isViewFromObject(view: View, `object`: Any): Boolean { return view == `object` }

    override fun getCount(): Int = listOfVehicle.size

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = container.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val vehicleCard = inflater.inflate(R.layout.list_content_vehicle, null) as CardView


        val vehicleImage: ImageView = vehicleCard.findViewById(R.id.ivVehicleCardImage)
        val title: TextView = vehicleCard.findViewById(R.id.tvVehicleCardTitle)
        val desc: TextView = vehicleCard.findViewById(R.id.tvVehicleCardDesc)
        val star: SparkButton = vehicleCard.findViewById(R.id.btnVehicleCardStar)
        val rating: TextView = vehicleCard.findViewById(R.id.tvVehicleCardRating)
        val bond: TextView = vehicleCard.findViewById(R.id.tvVehicleCardBond)
        val rent: TextView = vehicleCard.findViewById(R.id.tvVehicleCardRent)
        var imageUri = listOfVehicle[position].imageList[0]

        vehicleImage.scaleType = ImageView.ScaleType.CENTER
//        photoView.setAdjustViewBounds(true);

        Picasso.with(context)
                .load(imageUri)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(vehicleImage, object : Callback {
                    override fun onSuccess() {

                    }

                    override fun onError() {
                        // Try again online if cache failed
                        Picasso.with(context)
                                .load(imageUri)
                                .into(vehicleImage, object : Callback {
                                    override fun onSuccess() {

                                    }

                                    override fun onError() {

                                    }
                                })
                    }
                })

        vehicleImage.setOnClickListener({
            var intent = Intent(context, VehicleDetailActivity::class.java)
            intent.putExtra("vehicle", listOfVehicle[position] as Serializable)
            context.startActivity(intent)
        })

        title.text = "${listOfVehicle.get(position).make} - ${listOfVehicle.get(position).model}"
        desc.text = listOfVehicle.get(position).desc
        rent.text = "$ ${listOfVehicle.get(position).rent.toString()}"
        container.addView(vehicleCard, 0)
        return vehicleCard
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as CardView)
    }



}