package com.zaplab.zap

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_add_car_info.*
import org.xdty.preference.colorpicker.ColorPickerDialog

/**
 * Created by Ramshad on 4/11/18.
 *
 * Fragment to get information about vehicle
 */
class AddCarInfoFragment: Fragment(), View.OnClickListener {


    private var selectedColor: Int = 0
    var tvList = mutableListOf<TextView>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_car_info, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        selectedColor = ContextCompat.getColor(activity?.applicationContext!!, R.color.white)

        tvList.add(etAddCarMake)
        tvList.add(etAddCarModel)
        tvList.add(etAddCarDesc)
        tvList.add(etAddMileage)
        tvList.add(etAddCarPlate)

        rbAuto.setOnClickListener(this)
        rbManual.setOnClickListener(this)

        loadingBtnInfo.setOnClickListener {
            loadingBtnInfo.startLoading()
            finishAddInfo()
        }

        viewAddColor.setOnClickListener {
            getVehicleColor()
        }

    }

    /**
     * Add the values to vehicle object in AddCarActivity and go to next add car process
     */
    private fun finishAddInfo() {
        if (isAllDataEntered()) {
            (activity as AddCarActivity).vehicle.make = etAddCarMake.text.toString()
            (activity as AddCarActivity).vehicle.model = etAddCarModel.text.toString()
            (activity as AddCarActivity).vehicle.desc = etAddCarDesc.text.toString()
            (activity as AddCarActivity).vehicle.odometer = Integer.parseInt(etAddMileage.text.toString())
            (activity as AddCarActivity).vehicle.plate = etAddCarPlate.text.toString()
            (activity as AddCarActivity).vehicle.color = selectedColor
            (activity as AddCarActivity).infoCheck = true
            (activity as AddCarActivity).vehicle.amenities = tagGrpAddCarAmenities.tags.toList()
            loadingBtnInfo.loadingSuccessful()
            (activity as AddCarActivity).nextPager(1)

        }
    }


    /**
     * Check if all field values are added
     */
    private fun isAllDataEntered(): Boolean {
        var hasAllData = true
        for (x in tvList) {
            if (x.text.isEmpty()){
                x.error = "Field cannot be left blank !"
                loadingBtnInfo.loadingFailed()
                hasAllData = false
                break
            }
        }
        return hasAllData
    }

    /**
     * Show color selection dialog and get color of vehicle
     */
    private fun getVehicleColor() {
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

        dialog.show(activity?.fragmentManager, "color_dialog_test")
    }


    /**
     * Handle radio auto or manual radio button clicks
     */
    override fun onClick(view: View?) {
        // Is the button now checked?
        val checked = (view as RadioButton).isChecked

        // Check which radio button was clicked
        when (view.getId()) {
            R.id.rbAuto -> {
                if (checked) {
                    (activity as AddCarActivity).vehicle.transmission = Transmission.Auto
                }
            }
            R.id.rbManual -> {
                if (checked) {
                    (activity as AddCarActivity).vehicle.transmission = Transmission.Manual
                }
            }
        }
    }

}