package com.zaplab.zap

import android.content.Intent
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.nguyenhoanglam.imagepicker.model.Image
import kotlinx.android.synthetic.main.activity_add_car.*




/**
 * Created by Ramshad on 4/6/18.
 */
class AddCarActivity: AppCompatActivity() {

    companion object {
        var infoCheck = false
        var vehicle = Vehicle()
        var images: ArrayList<Image> = ArrayList()
    }


    var fragmentAdapter = FragmentAdapter(supportFragmentManager)
    private var vehicle: Vehicle = Vehicle()
    var titleList = arrayListOf("INFO", "PICTURES", "AVAILABILITY", "LOCATION", "PAYMENT")

    var dbRef = FirebaseDatabase.getInstance().reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_car)


        fragmentAdapter.addFragment(AddCarInfoFragment())
        fragmentAdapter.addFragment(AddCarPicsFragment())
        fragmentAdapter.addFragment(AddCarAvailFragment())
        fragmentAdapter.addFragment(AddCarLocationFragment())
        fragmentAdapter.addFragment(AddCarPayFragment())

        pagerAddCar.adapter = fragmentAdapter
        indicatorAddCar.setViewPager(pagerAddCar)

        pagerAddCar.addOnPageChangeListener(object: ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                tvAddCarTitle.text = titleList[position]
            }

        })
    }

    fun nextPager(pos: Int) {
        pagerAddCar.currentItem = pos
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        //fragmentAdapter.getItem(1).onActivityResult(requestCode, resultCode, data)
    }


}









    /*




        barAvailSun.minimumProgress = 0
        barAvailSun.maximumProgress = 24



        loadingBtnAddCar.setOnClickListener {
            loadingBtnAddCar.startLoading()
            var hasAllData = true
            for (x in tvList) {
                if (x.text.isEmpty()){
                    x.error = "Field cannot be left blank !"
                    loadingBtnAddCar.loadingFailed()
                    hasAllData = false
                    break
                }
            }

            // check if all fields are not empty
            if (hasAllData) {
                vehicle.make = etAddCarMake.text.toString()
                vehicle.model = etAddCarModel.text.toString()
                vehicle.desc = etAddCarDesc.text.toString()
                vehicle.odometer = Integer.parseInt(etAddMileage.text.toString())
                vehicle.bond = Integer.parseInt(etAddBond.text.toString())
                vehicle.rent = Integer.parseInt(etAddRent.text.toString())
                vehicle.color = selectedColor
                dbRef.child("Vehicles").push().setValue(vehicle).addOnCompleteListener {
                    loadingBtnAddCar.loadingSuccessful()
                    finish()
                }
            }
        }

        viewAddColor.setOnClickListener {

            val mColors = resources.getIntArray(R.array.rainbow)

            val dialog = ColorPickerDialog.newInstance(R.string.color_picker_default_title,
                    mColors,
                    selectedColor,
                    5, // Number of columns
                    ColorPickerDialog.SIZE_SMALL,
                    true, // True or False to enable or disable the serpentine effect
                    1, // stroke width
                    Color.BLACK // stroke color
            )

            dialog.setOnColorSelectedListener { color ->
                selectedColor = color

                val bgShape = viewAddColor.background as GradientDrawable
                bgShape.setColor(selectedColor)
            }

            dialog.show(fragmentManager, "color_dialog_test");
        }

    }

    fun onRadioClick(view: View?) {
        // Is the button now checked?
        val checked = (view as RadioButton).isChecked

        // Check which radio button was clicked
        when (view.getId()) {
            R.id.rbAuto -> {
                if (checked) {
                    vehicle.transmission = Transmission.Auto
                }
            }
            R.id.rbManual -> {
                if (checked) {
                    vehicle.transmission = Transmission.Manual
                }
            }
        }
    }
     */