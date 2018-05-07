package com.zaplab.zap

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Ramshad on 5/3/18.
 */
class HistoryListRecyclerAdapter(_context: Context, _list: MutableList<Transaction>, _vehicleList: HashMap<String, String>, _currentUser: String) :
        RecyclerView.Adapter<HistoryListRecyclerAdapter.MyViewHolder>() {

    val myContext = _context
    var list = _list
    var vehicleList = _vehicleList
    var currentUser = _currentUser
    val dateTimeFormat = SimpleDateFormat("dd/MM/yyyy HH:mm")
    lateinit var fromDate: Date
    lateinit var toDate: Date
    lateinit var currentDate: Date



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryListRecyclerAdapter.MyViewHolder {
        var v = LayoutInflater.from(myContext).inflate(R.layout.history_list_content, parent, false)
        var myViewHolder = MyViewHolder(v)
        return myViewHolder
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: HistoryListRecyclerAdapter.MyViewHolder, position: Int) {
        fromDate = dateTimeFormat.parse(list[position].fromDate)
        toDate= dateTimeFormat.parse(list[position].toDate)

        currentDate = dateTimeFormat.parse(dateTimeFormat.format(Date()))

        setMessage(position, holder)

    }

    /**
     * Set message in list according status of each transaction
     */
    private fun setMessage(position: Int, holder: MyViewHolder) {
        if (list[position].owner == currentUser && toDate.after(currentDate)) {
            holder.message.text = "Your car ${vehicleList.get(list[position].vehicleId)} is booked from ${dateTimeFormat.format(fromDate)}to ${dateTimeFormat.format(toDate)}"
        } else if (list[position].owner == currentUser && toDate.before(currentDate)) {
            holder.message.text = "Your car ${vehicleList.get(list[position].vehicleId)} was rented from ${dateTimeFormat.format(fromDate)} to ${dateTimeFormat.format(toDate)}"
        } else if (list[position].renter == currentUser && toDate.after(currentDate)) {
            holder.message.text = "You booked ${vehicleList.get(list[position].vehicleId)} from ${dateTimeFormat.format(fromDate)} to ${dateTimeFormat.format(toDate)}"
        } else {
            holder.message.text = "You rented ${vehicleList.get(list[position].vehicleId)} from ${dateTimeFormat.format(fromDate)} to ${dateTimeFormat.format(toDate)}"
        }
    }


    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val message: TextView = view.findViewById(R.id.tvHistoryMessage)
        val listItem: ConstraintLayout = view.findViewById(R.id.clHistoryContent)
    }
}