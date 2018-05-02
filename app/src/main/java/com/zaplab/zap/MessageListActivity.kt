package com.zaplab.zap

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.zaplab.zap.RecyclerItemClickListener.OnItemClickListener
import kotlinx.android.synthetic.main.activity_message_list.*

/**
 * Created by Ramshad on 4/24/18.
 */
class MessageListActivity: AppCompatActivity() {


    var channelList = mutableListOf<Channel>()
    var currentUser = ""
    lateinit var adapter : MessageListRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_list)


        currentUser = (application as Global).currentUser.uid

        adapter = MessageListRecyclerAdapter(applicationContext, channelList, currentUser)
        rvMessageList.layoutManager = LinearLayoutManager(this)

        getMessageHistory()

        rvMessageList.addOnItemTouchListener(object : RecyclerItemClickListener(this,
                OnItemClickListener { view, position ->
                    var intent = Intent(this, ChatActivity::class.java)
                    var toUser = if (channelList[position].user1 == currentUser) channelList[position].user2 else channelList[position].user1
                    intent.putExtra("TO_USER", toUser)
                    startActivity(intent)
                    finish()
                })
        {})
    }

    private fun getMessageHistory() {
        FirebaseDatabase.getInstance().reference.child("Channels").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
            }

            override fun onDataChange(snap: DataSnapshot?) {
                if ( snap != null) {
                    for ( x in snap.children) {
                        var user1 = x.child("user1").value.toString()
                        var user2 = x.child("user2").value.toString()

                        if ((user1 ==  currentUser || user2 == currentUser)) {
                            channelList.add(x.getValue(Channel::class.java)!!)
                        }
                    }

                    if (channelList.isNotEmpty()) {
                        rvMessageList.adapter = adapter
                        adapter.notifyDataSetChanged()
                    }
                }

            }

        })
    }
}
