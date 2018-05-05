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
import kotlinx.android.synthetic.main.fragment_add_car_pay.*

/**
 * Created by Ramshad on 4/11/18.
 */
class AddCarPayFragment: Fragment() {

    var dbRef = FirebaseDatabase.getInstance().reference
    var listOfCards = mutableListOf<CreditCard>()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_car_pay, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        loadCreditCards()

        btnAddCarPayFinish.setOnClickListener({
            btnAddCarPayFinish.startLoading()
            var allGood = true
            if (etAddCarPayRent.text.isBlank()) {
                btnAddCarPayFinish.loadingFailed()
                etAddCarPayRent.error = "Field cannot be left blank"
                allGood = false
            }

            if (allGood) {
                (activity as AddCarActivity).vehicle.rent = etAddCarPayRent.text.toString().toDouble()
                (activity as AddCarActivity).vehicle.card = listOfCards[viewPagerAddCarPay.currentItem]
                (activity as AddCarActivity).addVehicleToDB()
            }
        })

        btnAddCarPaySelectCredit.setOnClickListener({
            var selectedCard = listOfCards[viewPagerAddCarPay.currentItem]
            selectedCreditCard.visibility = View.VISIBLE
            selectedCreditCard.cardNumber = selectedCard.number
            selectedCreditCard.cardHolderName = selectedCard.name
            selectedCreditCard.setCardExpiry(selectedCard.exp)
            selectedCreditCard.setCVV(selectedCard.cvv)
            indicatorAddCarPay.visibility = View.GONE
            viewPagerAddCarPay.visibility = View.GONE
            btnAddCarPaySelectCredit.visibility = View.GONE
            fabAddCarPayAddCredit.visibility = View.GONE
            btnAddCarPayFinish.visibility = View.VISIBLE
        })


        fabAddCarPayAddCredit.setOnClickListener({
            addCreditCard()
        })
    }

    /**
     * Load all credit cards belonging to this user
     */
    private fun loadCreditCards() {
        dbRef.child("Cards").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(snap: DataSnapshot?) {
                if (snap != null) {
                    listOfCards.clear()
                    for (x in snap.children) {
                        var creditCard = x.getValue(CreditCard::class.java)
                        if (creditCard?.cardHolder?.equals(FirebaseAuth.getInstance().currentUser?.uid)!!)
                            creditCard?.let { listOfCards.add(it) }
                        var adapter = CreditCardListAdapter(activity?.applicationContext!!, listOfCards)
                        viewPagerAddCarPay.adapter = adapter
                        viewPagerAddCarPay.clipToPadding = false
                        viewPagerAddCarPay.setPadding(100, 0, 100, 0)
                        indicatorAddCarPay.setViewPager(viewPagerAddCarPay)
                    }
                }
            }

        })
    }

    /**
     * Add a new credit card
     */
    private fun addCreditCard() {
        var dialog = Dialog(activity)
        val view = activity?.layoutInflater?.inflate(R.layout.add_credit_card, null)
        dialog.setContentView(view)
        var etCardNum = dialog.findViewById<EditText>(R.id.etAddCardNum)
        var etCardName = dialog.findViewById<EditText>(R.id.etAddCardName)
        var etCardExp = dialog.findViewById<EditText>(R.id.etAddCardDate)
        var etCardCVV = dialog.findViewById<EditText>(R.id.etAddCardCVV)
        var creditCard = dialog.findViewById<CreditCardView>(R.id.creditCardAdd)
        var btnAddCard = dialog.findViewById<Button>(R.id.btnAddCard)
        etCardNum.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(t: CharSequence?, p1: Int, p2: Int, p3: Int) {
                creditCard.cardNumber = t.toString().trim()
            }

        })

        etCardName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(t: CharSequence?, p1: Int, p2: Int, p3: Int) {
                creditCard.cardHolderName = t.toString().trim()
            }

        })

        etCardExp.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(t: CharSequence?, p1: Int, p2: Int, p3: Int) {
                creditCard.setCardExpiry(t.toString().trim())
            }

        })

        etCardCVV.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(t: CharSequence?, p1: Int, p2: Int, p3: Int) {
                creditCard.cvv = t.toString().trim()
            }

        })

        etCardCVV.onFocusChangeListener = View.OnFocusChangeListener { p0, hasFocus ->
            if (hasFocus) {
                creditCard.showBack()
            } else
                creditCard.showFront()
        }

        btnAddCard.setOnClickListener({
            // Validation checks
            var creditCard = FirebaseAuth.getInstance().currentUser?.uid?.toString()?.let { it1 -> CreditCard(it1, etCardNum.text.toString(), etCardName.text.toString(), etCardExp.text.toString(), etCardCVV.text.toString()) }
            dbRef.child("Cards").push().setValue(creditCard).addOnCompleteListener { dialog.dismiss() }
        })

        dialog.show()
    }
}