package com.zaplab.zap

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.RadioButton
import android.widget.TextView
import com.appyvet.materialrangebar.RangeBar

/**
 * Created by Ramshad on 4/14/18.
 */
class CarAddAvailAdapter(internal var context: Context, internal var daysList : MutableList<String>, internal var availableDates: AvailableDates) : BaseAdapter() {


    override fun getView(pos: Int, convertView: View?, viewGroup: ViewGroup): View? {

        var TAG = "AlertFeedAdapter"
        var v: View? = convertView
        var viewHolder = ViewHolder()




        if (v == null) {
            val inflater = LayoutInflater.from(context)
            v = inflater.inflate(R.layout.avail_range_selector, null)

            viewHolder.tvDay = v.findViewById(R.id.tvAddCarAvailDay)
            viewHolder.slider = v.findViewById(R.id.rangeAddCarAvail)
            viewHolder.radioAllDay = v.findViewById(R.id.rbAddCarAvailAllDay)
            viewHolder.radioNotAvail = v.findViewById(R.id.rbAddCarAvailNot)
            viewHolder.tvFrom = v.findViewById(R.id.tvAddCarAvailFrom)
            viewHolder.tvTo = v.findViewById(R.id.tvAddCarAvailTo)
            v.tag = viewHolder
        } else {
            viewHolder = v.tag as ViewHolder
        }

        val startValue = 1000
        val endValue = 100000
        val factor = 500

        viewHolder.slider?.left = 0
        viewHolder.slider?.right = 48

        viewHolder.slider?.setOnRangeBarChangeListener(object : RangeBar.OnRangeBarChangeListener {
            override fun onRangeChangeListener(rangeBar: RangeBar?, leftPinIndex: Int, rightPinIndex: Int, leftPinValue: String?, rightPinValue: String?) {
                viewHolder.tvFrom?.text = leftPinIndex.toString()
                viewHolder.tvTo?.text = rightPinIndex.toString()
            }

        })


        viewHolder.tvDay?.text = daysList[pos]

        viewHolder.radioAllDay?.setOnClickListener({
            viewHolder.slider?.isEnabled = false

        })


        viewHolder.radioNotAvail?.setOnClickListener({
            viewHolder.slider?.isEnabled = false

        })



        return v
    }

    override fun getItem(pos: Int): Any {
        return daysList[pos]
    }

    override fun getItemId(pos: Int): Long {
        return pos.toLong()
    }

    override fun getCount(): Int = daysList.size


    internal class ViewHolder {
        var tvDay: TextView? = null
        var tvTo: TextView? = null
        var tvFrom: TextView? = null
        var slider: RangeBar? = null
        var radioAllDay: RadioButton? = null
        var radioNotAvail: RadioButton? = null
    }
}
