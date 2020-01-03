package com.giza.gizaamrdata.ui.meterdetails

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.request.RequestOptions
import com.giza.gizaamrdata.R
import com.giza.gizaamrdata.models.Meter
import com.giza.gizaamrdata.models.SearchMeterResultedObject
import com.giza.gizaamrdata.ui.NavigationManager
import com.giza.gizaamrdata.ui.base.BaseFragment
import com.giza.gizaamrdata.ui.wizard.WizardFragment
import com.giza.gizaamrdata.utils.extensions.putArgs
import com.glide.slider.library.Animations.DescriptionAnimation
import com.glide.slider.library.SliderLayout
import com.glide.slider.library.SliderTypes.BaseSliderView
import com.glide.slider.library.SliderTypes.TextSliderView
import com.glide.slider.library.Tricks.ViewPagerEx
import kotlinx.android.synthetic.main.meterdetails_fragment.*


/**
 * @author hossam.
 */
class MeterDetailsFragment : BaseFragment<MeterDetailsContract.Presenter>(), MeterDetailsContract.View,
    BaseSliderView.OnSliderClickListener,
    ViewPagerEx.OnPageChangeListener {


    override val fragmentLayoutResourceId = R.layout.meterdetails_fragment
    lateinit var meter: SearchMeterResultedObject

    companion object {
        fun newInstance(searchMeterResultedObject: SearchMeterResultedObject) = MeterDetailsFragment().putArgs {
            val args = Bundle()
            args.putSerializable("H", searchMeterResultedObject)
            val fragment = MeterDetailsFragment()
            fragment.arguments = args
            return fragment
        }

    }

    override fun init(savedInstanceState: Bundle?) {
        presenter = MeterDetailsPresenter(this)
        meter = this.arguments?.getSerializable("H") as SearchMeterResultedObject
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        retainInstance = true
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        onBackClicked()
        populateDate()

        val phone : String = meter.Owner_Phone

        if (phone.isNotEmpty())
        {
            call_card.visibility = View.VISIBLE
            message_card.visibility = View.VISIBLE

            mobile_btn.setOnClickListener{
                val intent =
                    Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:+20$phone")
                startActivity(intent)
            }

            message_btn.setOnClickListener {
                /*val packageManager: PackageManager? = context!!.packageManager
                val intent =
                    Intent(Intent.ACTION_SEND)
                intent.data = Uri.parse("smsto:$phone") // This ensures only SMS apps respond

                intent.putExtra("sms_body", "")
                if (intent.resolveActivity(packageManager!!) != null)
                {
                    startActivity(intent)
                }*/
                /*val intent =
                    Intent(Intent.ACTION_SEND)
                intent.data = Uri.parse("sms:+20$phone")
                startActivity(intent)*/

                val smsIntent =
                    Intent(Intent.ACTION_VIEW)
                smsIntent.type = "vnd.android-dir/mms-sms"
                smsIntent.putExtra("address", phone)
                //smsIntent.putExtra("sms_body", "Body of Message")
                startActivity(smsIntent)

                /*val packageManager: PackageManager? = context!!.packageManager
                val i =
                    Intent(Intent.ACTION_VIEW)

                try {
                    val url =
                        "https://api.whatsapp.com/send?phone=+20$phone&text=" + URLEncoder.encode(
                            "message",
                            "UTF-8"
                        )
                    i.setPackage("com.whatsapp")
                    i.data = Uri.parse(url)
                    if (i.resolveActivity(packageManager!!) != null) {
                        context!!.startActivity(i)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }*/
            }
        }

        maps_btn.setOnClickListener {
            val latitude: String = meter.Location_Latitude
            val longitude: String = meter.Location_Longitude
            val name: String = meter.Owner_Name

            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("geo:0,0?q=$latitude,$longitude($name)")
            )
            startActivity(intent)
        }
    }

    override fun onEditClicked()
    {
        presenter.navigateToWizard()
    }

    override fun goToWizard() {
        NavigationManager.attachWithStack(
            this.requireActivity() as AppCompatActivity,
            WizardFragment.newInstance(Meter.convertToMeter(meter)),
            tag = NavigationManager.Tags.HOME
        )
    }

    override fun onBackClicked() {

    }

    override fun populateDate() {
        prepareSlider()
        prepareMap()
        txtAddress.text = meter.Address
        txtBuildingType.text = meter.Building_Type
        txtBuildingUsage.text = meter.Building_Usage
        txtElectricityMeterNumber.text = meter.Electricity_Meter_Number
        txtMeterId.text = meter.Number
        txtOwnerName.text = meter.Owner_Name
        txtOwnerId.text = meter.Owner_National_id
        txtOwnerNumber.text = meter.Owner_Phone
        txtStreetType.text = meter.Street_Type
        txtMeterVendor.text = meter.Vendor
    }

    fun prepareMap()
    {
        val url = "https://maps.googleapis.com/maps/api/staticmap?center=\" + \"${meter.Location_Latitude},${meter.Location_Longitude}\" +\n" +
                "                    \"&markers=color:blue%7Clabel:S%7C${meter.Location_Latitude},${meter.Location_Longitude}\" +\n" +
                "                    \"&zoom=13&size=350x250&maptype=roadmap\\n\" +\n" +
                "                    \"&key=AIzaSyAPwS2app3RMke4vvHaLcg9tz11Ddr8qI4"

        webview.loadUrl(
            url
        )
        webview.setOnClickListener {
            val uris = Uri.parse(url)
            val intents = Intent(Intent.ACTION_VIEW, uris)
            val b = Bundle()
            b.putBoolean("new_window", true)
            intents.putExtras(b)
            startActivity(intents)
        }
    }

    fun prepareSlider() {
        val listUrl = mutableListOf<String>()
        val listName = mutableListOf<String>()



        meter.Images.forEachIndexed { index, item ->
            listUrl.add(item)
            listName.add("Image $index")
        }

        val requestOptions = RequestOptions()
        requestOptions.centerCrop()
        //.diskCacheStrategy(DiskCacheStrategy.NONE)
        //.placeholder(R.drawable.placeholder)
        //.error(R.drawable.placeholder);

        for (i in 0 until listUrl.size) {
            val sliderView = TextSliderView(requireContext())
            // if you want show image only / without description text use DefaultSliderView instead

            // initialize SliderLayout
            sliderView
                .image(listUrl[i])
                .description(listName[i])
                .setRequestOption(requestOptions)
                .setProgressBarVisible(true)
                .setOnSliderClickListener(this)

            //add your extra information
            sliderView.bundle(Bundle())
            sliderView.bundle.putString("extra", listName[i])
            slider.addSlider(sliderView)
        }

        // set Slider Transition Animation
        // mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Default);
        slider.setPresetTransformer(SliderLayout.Transformer.Accordion)

        slider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom)
        slider.setCustomAnimation(DescriptionAnimation())
        slider.setDuration(4000)
        slider.addOnPageChangeListener(this)
        if (meter.Images.isNullOrEmpty()) slider.visibility = View.GONE
    }

    override fun onStop() {
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        slider.stopAutoCycle()
        super.onStop()
    }

    override fun onSliderClick(slider: BaseSliderView) {
        showToast(slider.bundle.get("extra")!!.toString())
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {

    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_meter_detail, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_edit -> {
            onEditClicked()
            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }
}
