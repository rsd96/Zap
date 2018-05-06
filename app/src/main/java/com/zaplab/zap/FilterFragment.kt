package com.zaplab.zap

import android.app.Dialog
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.TabLayout
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import com.allattentionhere.fabulousfilter.AAH_FabulousFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import me.gujun.android.taggroup.TagGroup


/**
 * Created by Ramshad on 4/30/18.
 */
class FilterFragment: AAH_FabulousFragment() {

//    companion object {
//        fun newInstance(): FilterFragment {
//            return FilterFragment()
//        }
//    }

//    private lateinit var tabLayout: TabLayout
//    private lateinit var viewPagerFilter: ViewPager

    var countryFilter = ""
    var citiesFilterList = mutableListOf<String>()
    var dbRef = FirebaseDatabase.getInstance().reference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //setupTabs()
    }

    lateinit var tabLayout: TabLayout
    lateinit var viewPagerFilter: ViewPager

    override fun setupDialog(dialog: Dialog?, style: Int) {
        val contentView = View.inflate(context, R.layout.fragment_filter, null)
        val rl_content = contentView.findViewById(R.id.rl_content) as RelativeLayout
        val ll_buttons = contentView.findViewById(R.id.ll_buttons) as ConstraintLayout
        tabLayout = contentView.findViewById(R.id.tab_filter) as TabLayout
        viewPagerFilter= contentView.findViewById(R.id.viewPagerFilter) as ViewPager
        contentView.findViewById<Button>(R.id.btnFilterClose).setOnClickListener(View.OnClickListener {
            (activity as MainActivity).filter(countryFilter, citiesFilterList)
            closeFilter("closed")
        })
        contentView.findViewById<Button>(R.id.btnFilterReset).setOnClickListener( {
            countryFilter = ""
            citiesFilterList.clear()
            setupTabs()
        })

        setupTabs()
        //params to set
        setAnimationDuration(400) //optional; default 500ms
        setPeekHeight(300) // optional; default 400dp
        //setCallbacks(activity as AAH_FabulousFragment.Callbacks?) //optional; to get back result
        //setAnimationListener(activity as AAH_FabulousFragment.AnimationListener?) //optional; to get animation callbacks
        setViewgroupStatic(ll_buttons) // optional; layout to stick at bottom on slide
        setViewPager(viewPagerFilter) //optional; if you use viewpager that has scrollview
        setViewMain(rl_content) //necessary; main bottomsheet view
        setMainContentView(contentView) // necessary; call at end before super
        super.setupDialog(dialog, style)
    }

    private fun setupTabs() {
        var tabsAdapter = SectionsPagerAdapter()
//        tabsAdapter.addFragment(FilterLocationFragment(), "Location")
//        tabsAdapter.addFragment(FilterAvailFragment(), "Availability")
        viewPagerFilter.adapter = tabsAdapter
        viewPagerFilter.offscreenPageLimit = 4
        viewPagerFilter.currentItem = 0
        tabsAdapter.notifyDataSetChanged()
        tabLayout.setupWithViewPager(viewPagerFilter)
    }


    inner class SectionsPagerAdapter : PagerAdapter() {

        override fun instantiateItem(collection: ViewGroup, position: Int): Any {
            val inflater = LayoutInflater.from(context)
            var layout = inflater.inflate(R.layout.fragment_location_filter, collection, false) as ViewGroup
            when (position) {
                0 -> {}
                1 -> {
                    layout = inflater.inflate(R.layout.fragment_location_filter, collection, false) as ViewGroup
                    var countryTags = layout.findViewById<TagGroup>(R.id.tagGrpFilterCountries)
                    var cityTags = layout.findViewById<TagGroup>(R.id.tagGrpCities)
                    var countrySelection = layout.findViewById<TextView>(R.id.tvFilterLocationCountrySelected)
                    var citiesSelection = layout.findViewById<TextView>(R.id.tvFilterLocationCitiesSelected)

                    countryTags.setOnTagClickListener {
                        countryFilter = it
                        countrySelection.text = "Country : $it"
                        var cityList = mutableListOf<String>()
                        dbRef.child("Locations").child(countryFilter).addValueEventListener(object : ValueEventListener{
                            override fun onCancelled(p0: DatabaseError?) {
                            }

                            override fun onDataChange(snap: DataSnapshot?) {
                                if ( snap != null) {
                                    for ( x in snap.children) {
                                        cityList.add(x.value.toString())
                                    }
                                    cityTags.setTags(cityList)
                                    cityTags.visibility = View.VISIBLE
                                    countryTags.visibility = View.GONE
                                }

                            }

                        })

                    }

                    cityTags.setOnTagClickListener {
                        citiesFilterList.add(it)
                        citiesSelection.text = "Cities : ${citiesFilterList}"
                    }

                    var countryList = mutableListOf<String>()

                    dbRef.child("Locations").addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError?) {
                        }

                        override fun onDataChange(snap: DataSnapshot?) {
                            if ( snap != null) {
                                for ( x in snap.children) {
                                    countryList.add(x.key)
                                }
                                countryTags.setTags(countryList)
                            }

                        }

                    })
                }

                2 ->  layout = inflater.inflate(R.layout.fragment_avail_filter, collection, false) as ViewGroup
            }
            collection.addView(layout)
            return layout

        }

        override fun destroyItem(collection: ViewGroup, position: Int, view: Any) {
            collection.removeView(view as View)
        }

        override fun getCount(): Int {
            return 4
        }

        override fun getPageTitle(position: Int): CharSequence? {
            when (position) {
                0 -> return "MAKE"
                1 -> return "LOCATION"
                2 -> return "RENT"
            }
            return ""
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }
    }
}
