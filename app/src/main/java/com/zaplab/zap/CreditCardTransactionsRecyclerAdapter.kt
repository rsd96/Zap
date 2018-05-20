package com.zaplab.zap

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.github.vipulasri.timelineview.TimelineView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


/**
 * Created by Ramshad on 5/7/18.
 *
 * Adapter class to handle credit card transaction list
 */
class CreditCardTransactionsRecyclerAdapter  (_context: Context, _list: MutableList<Transaction>) :
        RecyclerView.Adapter<CreditCardTransactionsRecyclerAdapter.MyViewHolder>() {

    val myContext = _context
    var list = _list

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreditCardTransactionsRecyclerAdapter.MyViewHolder {
        var v = LayoutInflater.from(myContext).inflate(R.layout.credit_card_transaction_content, parent, false)
        var myViewHolder = MyViewHolder(v, viewType)
        return myViewHolder
    }

    override fun getItemCount(): Int = list.size

    override fun getItemViewType(position: Int): Int {
        return TimelineView.getTimeLineViewType(position,getItemCount());
    }

    override fun onBindViewHolder(holder: CreditCardTransactionsRecyclerAdapter.MyViewHolder, position: Int) {


        FirebaseDatabase.getInstance().reference.child("Vehicles").child(list[position].vehicleId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
            }

            override fun onDataChange(x: DataSnapshot?) {
                if ( x != null) {

                    holder.transaction?.text = "Booked ${x.child("make").value.toString()}-${x.child("model").value.toString()} " +
                            "from ${list[position].fromDate} to ${list[position].toDate}"
                }

            }

        })

        holder.time?.text = list[position].transactionDate
        holder.amount?.text = list[position].amount.toString()

    }


    class MyViewHolder(itemView: View?, viewType: Int) : RecyclerView.ViewHolder(itemView) {
        var mTimelineView: TimelineView = itemView?.findViewById<View>(R.id.time_marker) as TimelineView
        var time = itemView?.findViewById<TextView>(R.id.tvCreditCardTransactionTime)
        var amount = itemView?.findViewById<TextView>(R.id.tvCreditCardTransactionAmount)
        var transaction = itemView?.findViewById<TextView>(R.id.tvCreditCardTransaction)


    }
}