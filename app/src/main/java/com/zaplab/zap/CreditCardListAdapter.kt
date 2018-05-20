package com.zaplab.zap

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup
import com.cooltechworks.creditcarddesign.CreditCardView

/**
 * Created by Ramshad on 4/13/18.
 *
 * Adapter class to show list of credit cards
 */
class CreditCardListAdapter(_context: Context, _listOfCards: MutableList<CreditCard>): PagerAdapter() {

    val context = _context
    val listOfCards = _listOfCards

    override fun isViewFromObject(view: View, `object`: Any): Boolean { return view == `object` }

    override fun getCount(): Int = listOfCards.size

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        var creditCard = CreditCardView(context)
        creditCard.cardHolderName = listOfCards[position].name
        creditCard.cardNumber = listOfCards[position].number
        creditCard.setCardExpiry(listOfCards[position].exp)
        creditCard.cvv = listOfCards[position].cvv


        container.addView(creditCard, 0)
        return creditCard
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as CreditCardView)
    }
}