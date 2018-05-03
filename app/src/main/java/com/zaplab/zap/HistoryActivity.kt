package com.zaplab.zap

import android.app.Dialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.zaplab.zap.RecyclerItemClickListener.OnItemClickListener
import kotlinx.android.synthetic.main.activity_history_list.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Ramshad on 5/3/18.
 */
class HistoryActivity: AppCompatActivity() {


    var transactionList = mutableListOf<Transaction>()
    var vehicleList = hashMapOf<String, String>()
    var currentUser = ""
    val dateTimeFormat = SimpleDateFormat("dd/MM/yyyy HH:mm")

    lateinit var adapter: HistoryListRecyclerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_list)

        currentUser = (application as Global).currentUser.uid

        adapter = HistoryListRecyclerAdapter(this, transactionList, vehicleList, currentUser)
        rvHistoryList.layoutManager = LinearLayoutManager(this)


        getTransactions()

        rvHistoryList.addOnItemTouchListener(object : RecyclerItemClickListener(this,
                OnItemClickListener { view, position ->

                    var fromDate = dateTimeFormat.parse(transactionList[position].fromDate)
                    var toDate= dateTimeFormat.parse(transactionList[position].toDate)
                    var currentDate = dateTimeFormat.parse(dateTimeFormat.format(Date()))

                    var dialog = Dialog(this)
                    dialog.setContentView(R.layout.dialog_rate_report)

                    if (transactionList[position].owner == currentUser && toDate.before(currentDate)) {
                        dialog.show()
                    } else if (transactionList[position].renter == currentUser && toDate.before(currentDate)) {
                        dialog.show()
                    }

                })
        {})
    }

    private fun getTransactions() {
        FirebaseDatabase.getInstance().reference.child("Transactions").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
            }

            override fun onDataChange(snap: DataSnapshot?) {
                transactionList.clear()
                if ( snap != null) {
                    for ( x in snap.children) {
                        if ( x.child("owner").value == currentUser || x.child("renter").value == currentUser) {
                            transactionList.add(x.getValue(Transaction::class.java)!!)
                        }
                    }

                        getVehicleList()
                }
            }

        })
    }


    private fun getVehicleList() {

        FirebaseDatabase.getInstance().reference.child("Vehicles").addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {
            }

            override fun onDataChange(snap: DataSnapshot?) {
                if ( snap != null) {
                    for ( x in snap.children) {
                        transactionList.find { it.vehicleId == x.key }?.vehicleId?.let {
                            vehicleList.put(it, "${x.child("make").value}-${x.child("model").value}")
                        }
                    }
                    rvHistoryList.adapter = adapter
                    adapter.notifyDataSetChanged()
                }
            }

        })
    }


}