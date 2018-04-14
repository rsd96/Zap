package com.zaplab.zap

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.cooltechworks.creditcarddesign.CreditCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_credit_card.*



/**
 * Created by Ramshad on 4/12/18.
 */
class CreditCardsFragment: Fragment() {

    var dbRef = FirebaseDatabase.getInstance().reference
    var listOfCards = mutableListOf<CreditCard>()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_credit_card, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        dbRef.child("Cards").addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(snap: DataSnapshot?) {
                if (snap != null) {
                    listOfCards.clear()
                    for (x in snap.children) {
                        var creditCard = x.getValue(CreditCard::class.java)
                        creditCard?.let { listOfCards.add(it) }
                        var adapter = CreditCardListAdapter(activity?.applicationContext!!, listOfCards)
                        viewPagerCreditCard.adapter = adapter
                        viewPagerCreditCard.clipToPadding = false
                        viewPagerCreditCard.setPadding(100, 0, 100, 0)
                        creditCardPagerIndicator.setViewPager(viewPagerCreditCard)
                    }
                }
            }

        })


        var isBack = false
        fabAddCreditCard.setOnClickListener({
            var dialog = Dialog(activity)
            val view = activity?.layoutInflater?.inflate(R.layout.add_credit_card, null)
            dialog.setContentView(view)
            var etCardNum = dialog.findViewById<EditText>(R.id.etAddCardNum)
            var etCardName = dialog.findViewById<EditText>(R.id.etAddCardName)
            var etCardExp = dialog.findViewById<EditText>(R.id.etAddCardDate)
            var etCardCVV = dialog.findViewById<EditText>(R.id.etAddCardCVV)
            var creditCard = dialog.findViewById<CreditCardView>(R.id.creditCardAdd)
            var btnAddCard = dialog.findViewById<Button>(R.id.btnAddCard)
            etCardNum.addTextChangedListener(object: TextWatcher{
                override fun afterTextChanged(p0: Editable?) {
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(t: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    creditCard.cardNumber = t.toString()
                }

            })

            etCardName.addTextChangedListener(object: TextWatcher{
                override fun afterTextChanged(p0: Editable?) {
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(t: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    creditCard.cardHolderName = t.toString()
                }

            })

            etCardExp.addTextChangedListener(object: TextWatcher{
                override fun afterTextChanged(p0: Editable?) {
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(t: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    creditCard.setCardExpiry(t.toString())
                }

            })

            etCardCVV.addTextChangedListener(object: TextWatcher{
                override fun afterTextChanged(p0: Editable?) {
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(t: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    creditCard.cvv = t.toString()
                }

            })

            etCardCVV.onFocusChangeListener = View.OnFocusChangeListener { p0, hasFocus ->
                if(hasFocus) {
                    creditCard.showBack()
                } else
                    creditCard.showFront()
            }

            btnAddCard.setOnClickListener({
                // Validation checks
                var creditCard = FirebaseAuth.getInstance().currentUser?.uid?.toString()?.let { it1 -> CreditCard(it1,etCardNum.text.toString(), etCardName.text.toString(), etCardExp.text.toString(), etCardCVV.text.toString()) }
                dbRef.child("Cards").push().setValue(creditCard).addOnCompleteListener { dialog.dismiss() }
            })

            dialog.show()
        })
    }
}