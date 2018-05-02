package com.zaplab.zap

import android.app.Activity
import android.app.Dialog
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.cooltechworks.creditcarddesign.CreditCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

/**
 * Created by Ramshad on 5/2/18.
 */
class AddCreditCardDialog {

    companion object {
        fun showDialog(activity: Activity) {
            var dialog = Dialog(activity)
            val view = activity.layoutInflater?.inflate(R.layout.add_credit_card, null)
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
                FirebaseDatabase.getInstance().reference.child("Cards").push().setValue(creditCard).addOnCompleteListener { dialog.dismiss() }
            })

            dialog.show()
        }

    }


}