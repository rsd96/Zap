package com.zaplab.zap

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso

/**
 * Created by Ramshad on 4/6/18.
 */

class VehicleDetailPagerAdapter(_context: Context, _listOfImages: ArrayList<String>): PagerAdapter() {
    val context = _context
    val listOfImages = _listOfImages
    override fun isViewFromObject(view: View, `object`: Any): Boolean { return view == `object` }

    override fun getCount(): Int = listOfImages.size

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = container.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        var image = ImageView(context)
        var imageUri = listOfImages[position]

        image.scaleType = ImageView.ScaleType.FIT_XY;
        //image.setAdjustViewBounds(true);

        Picasso.with(context)
                .load(imageUri)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(image, object : Callback {
                    override fun onSuccess() {

                    }

                    override fun onError() {
                        // Try again online if cache failed
                        Picasso.with(context)
                                .load(imageUri)
                                .into(image, object : Callback {
                                    override fun onSuccess() {

                                    }

                                    override fun onError() {

                                    }
                                })
                    }
                })

        container.addView(image, 0)
        return image
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as ImageView)
    }

}