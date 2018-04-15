package com.zaplab.zap

import android.content.Context
import android.support.constraint.Group
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

    private val ALL_DAY = 0
    private val NOT_AVAIL = 1
    private val CUSTOM = 2

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
            viewHolder.radioCustom = v.findViewById(R.id.rbAddCarAvailCustom)
            viewHolder.tvFrom = v.findViewById(R.id.tvAddCarAvailFrom)
            viewHolder.tvTo = v.findViewById(R.id.tvAddCarAvailTo)
            viewHolder.grpAvailSlider = v.findViewById(R.id.grpAvailSlider)
            v.tag = viewHolder
        } else {
            viewHolder = v.tag as ViewHolder
        }

        val startValue = 1000
        val endValue = 100000
        val factor = 500

        viewHolder.tvFrom?.text = "${viewHolder.slider?.leftPinValue.toString()}:00"
        viewHolder.tvTo?.text = "${viewHolder.slider?.rightPinValue.toString()}:00"

        viewHolder.slider?.setOnRangeBarChangeListener(object : RangeBar.OnRangeBarChangeListener {
            override fun onRangeChangeListener(rangeBar: RangeBar?, leftPinIndex: Int, rightPinIndex: Int, leftPinValue: String?, rightPinValue: String?) {
                viewHolder.tvFrom?.text = "${leftPinIndex.toString()}:00"
                viewHolder.tvTo?.text = "${rightPinIndex.toString()}:00"
            }
        })



        viewHolder.tvDay?.text = daysList[pos]

        viewHolder.radioAllDay?.setOnClickListener({
            viewHolder.grpAvailSlider?.visibility = View.GONE
            setTime(ALL_DAY, pos)

        })

        viewHolder.radioCustom?.setOnClickListener({
            viewHolder.grpAvailSlider?.visibility = View.VISIBLE
            setTime(CUSTOM, pos, "${viewHolder.slider?.leftPinValue.toString()}:00",
                    "${viewHolder.slider?.rightPinValue.toString()}:00" )

        })


        viewHolder.radioNotAvail?.setOnClickListener({
            viewHolder.grpAvailSlider?.visibility = View.GONE
            setTime(NOT_AVAIL, pos)
        })

        return v
    }


    fun setTime(type: Int, day: Int, from: String = "", to: String = "") {
        when (day) {
            0-> {
                when (type) {
                    0-> { availableDates.sunday = "AllDay" }
                    1-> { availableDates.sunday = "NotAvailable" }
                    2-> { availableDates.sunday = "$from - $to" }
                }
            }
            1-> {
                when (type) {
                    0-> { availableDates.monday = "AllDay" }
                    1-> { availableDates.monday = "NotAvailable" }
                    2-> { availableDates.monday = "$from - $to" }
                }
            }
            2-> {
                when (type) {
                    0-> { availableDates.tuesday = "AllDay" }
                    1-> { availableDates.tuesday = "NotAvailable" }
                    2-> { availableDates.tuesday = "$from - $to" }
                }
            }
            3-> {
                when (type) {
                    0-> { availableDates.wednesday = "AllDay" }
                    1-> { availableDates.wednesday = "NotAvailable" }
                    2-> { availableDates.wednesday = "$from - $to" }
                }
            }
            4-> {
                when (type) {
                    0-> { availableDates.thursday = "AllDay" }
                    1-> { availableDates.thursday = "NotAvailable" }
                    2-> { availableDates.thursday = "$from - $to" }
                }
            }
            5-> {
                when (type) {
                    0-> { availableDates.friday = "AllDay" }
                    1-> { availableDates.friday = "NotAvailable" }
                    2-> { availableDates.friday = "$from - $to" }
                }
            }
            6-> {
                when (type) {
                    0-> { availableDates.saturday = "AllDay" }
                    1-> { availableDates.saturday = "NotAvailable" }
                    2-> { availableDates.saturday = "$from - $to" }
                }
            }
        }
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
        var radioCustom: RadioButton? = null
        var grpAvailSlider: Group? = null
    }
}
