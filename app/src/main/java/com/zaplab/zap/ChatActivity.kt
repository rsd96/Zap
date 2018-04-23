package com.zaplab.zap

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_chat.*

/**
 * Created by Ramshad on 4/22/18.
 */
class ChatActivity: AppCompatActivity() {


    var dbRef = FirebaseDatabase.getInstance().reference
    var TAG = "ChatActivity"

    // list to hold messages
    var messageList = mutableListOf<Message>()
    private var mAdapter: ChatAdapter? = null
    private var channelId: String = ""
    private var toUserId: String = ""
    private var currentUserId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)



        // Get all data passed
        var data = intent.extras
        toUserId = data.getString("TO_USER")
        currentUserId = MainActivity.currentUser.uid

        // Get channel id
        dbRef.child("Channels").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
            }

            override fun onDataChange(snap: DataSnapshot?) {
                if (snap != null) {
                    for (x in snap.children) {
                        var user1 = x.child("user1").value.toString()
                        var user2 = x.child("user2").value.toString()
                        if ((user1 == toUserId || user1 == currentUserId) &&
                                (user2 == toUserId || user2 == currentUserId)) {
                            channelId = x.value.toString()
                            Log.d(TAG, channelId)
                            break
                        }
                    }

                    if (channelId.isNullOrBlank()) {

                        dbRef.child("Channels").push().setValue(Channel(user1 = currentUserId, user2 = toUserId, lastMessage = ""))

                    } else {
                        // Get all messages related to channel
                        dbRef.child("Messages").child(channelId).addValueEventListener(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError?) {
                            }

                            override fun onDataChange(snap: DataSnapshot?) {
                                if (snap != null) {
                                    for (x in snap.children) {
                                        messageList.add(x.getValue(Message::class.java)!!)
                                        rvChatMessageBoard.scrollToPosition(messageList.size - 1)
                                        mAdapter?.notifyItemInserted(messageList.size - 1)
                                        mAdapter?.refreshData(messageList)
                                        dbRef.child("Channels").child(channelId).child("lastMessage")
                                                .setValue(messageList[messageList.size - 1].message)
                                    }
                                }
                            }

                        })
                    }
                }
            }
        })



        // Get user data
        dbRef.child("Users").child(toUserId).addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {}

            override fun onDataChange(snap: DataSnapshot?) {
                var userName = snap?.child("userName")?.value.toString()
                var imageUri = snap?.child("photoUrl")?.value.toString()
                tvChatUserName.text = userName

                Picasso.with(applicationContext)
                        .load(imageUri)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .into(ivChatProfile, object : Callback {
                            override fun onSuccess() {}

                            override fun onError() {
                                // Try again online if cache failed
                                Picasso.with(applicationContext)
                                        .load(imageUri)
                                        .into(ivChatProfile, object : Callback {
                                            override fun onSuccess() {}

                                            override fun onError() {}
                                        })
                            }
                        })
            }
        })


        rvChatMessageBoard?.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        //mRecyclerView.setItemAnimator(new SlideInOutLeftItemAnimator(mRecyclerView));
        mAdapter = ChatAdapter(applicationContext, messageList)
        rvChatMessageBoard?.adapter = mAdapter


        btnChatSend.setOnClickListener( {
            var message = etChat.text.toString().trim()

            if (message.isNotEmpty()) {
                var m = Message()
                m.message = message
                m.from = currentUserId

                dbRef.child("Messages").child(channelId).push().setValue(m)
                etChat.text.clear()
            }
        })
    }
}