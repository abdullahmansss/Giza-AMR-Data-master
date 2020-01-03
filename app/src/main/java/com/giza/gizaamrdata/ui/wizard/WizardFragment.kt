package com.giza.gizaamrdata.ui.wizard

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItemsMultiChoice
import com.giza.gizaamrdata.R
import com.giza.gizaamrdata.models.Meter
import com.giza.gizaamrdata.ui.NavigationManager
import com.giza.gizaamrdata.ui.base.BaseFragment
import com.giza.gizaamrdata.ui.home.HomeFragment
import com.giza.gizaamrdata.ui.wizard.pages.*
import com.giza.gizaamrdata.utils.FileUtils
import com.giza.gizaamrdata.utils.Logger
import com.giza.gizaamrdata.utils.Rx2Bus
import com.giza.gizaamrdata.utils.RxEvents
import com.giza.gizaamrdata.utils.extensions.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.wizard_fragment.*
import java.io.File

/**
 * @author hossam.
 *
 *
 */
class WizardFragment : BaseFragment<WizardContract.Presenter>(), WizardContract.View {

    override val fragmentLayoutResourceId = R.layout.wizard_fragment
    lateinit var pagerAdapter: WizardPagerAdapter
    private lateinit var wizardDisposable: Disposable

    companion object {
        var openedForEdit: Boolean = false

        fun newInstance(meter: Meter = Meter()) = WizardFragment().putArgs {
            val args = Bundle()
            val fragment = WizardFragment()
            if (meter.number.isNotEmpty()) {
                // there is meter data have been passed to this fragment
                MeterModel.meter = meter
                openedForEdit = true
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun init(savedInstanceState: Bundle?) {
        presenter = WizardPresenter(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        retainInstance = true

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        if (savedInstanceState == null) {
            pagerAdapter = WizardPagerAdapter(this.childFragmentManager, this.lifecycle)
            pagerAdapter.addPage(Page1(), "Page1")
            pagerAdapter.addPage(Page2(), "Page2")
            pagerAdapter.addPage(Page3(), "Page3")
            pagerAdapter.addPage(Page4(), "Page4")
            pagerAdapter.addPage(Page5(), "Page5")
        }
        viewPagerWizard.adapter = pagerAdapter
        btnNext.deActivate()

        viewPagerWizard.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                handelButtonsVisibility(position, pagerAdapter.itemCount)
                step_view.go(position, true)
                Logger.d("onPageSelected $position")
                MeterModel.pageId = position
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                try {
                    invokePage(position)
                } catch (e: Exception) {
                    Logger.e(e.message.toString())
                }
            }
        })
        viewPagerWizard.isUserInputEnabled = false

        onNextClicked()
        onBackClicked()
    }

    class WizardPagerAdapter(manager: FragmentManager, lifecycle: Lifecycle) :
        FragmentStateAdapter(manager, lifecycle) {

        override fun createFragment(position: Int): Fragment {
            return pages[position]
        }

        override fun getItemCount(): Int {
            return pages.size
        }

        val pages: MutableList<Fragment> = ArrayList()

        private val titles: MutableList<String> = ArrayList()

        fun addPage(page: Fragment, title: String) {
            pages.add(page)
            titles.add(title)
        }

        fun getPageTitle(position: Int): CharSequence {
            return titles[position]
        }
    }

    private fun invokePage(pos: Int) {
        Logger.d("invokePage $pos")
        when (pos) {
            0 -> Rx2Bus.send(RxEvents.Wizard.PAGE1)
            1 -> Rx2Bus.send(RxEvents.Wizard.PAGE2)
            2 -> Rx2Bus.send(RxEvents.Wizard.PAGE3)
            3 -> Rx2Bus.send(RxEvents.Wizard.PAGE4)
            4 -> Rx2Bus.send(RxEvents.Wizard.PAGE5)
        }
    }

    override fun handelButtonsVisibility(pos: Int, max: Int) {
        Logger.d("handelButtonsVisibility $pos from total $max")
        when (pos) {
            0 -> cardBack.hide()
            1 -> cardBack .show()
            max - 2 -> {
                btnNext.text = getString(com.giza.gizaamrdata.R.string.next)
                btnNext.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    null,
                    null,
                    resources.getDrawable(com.giza.gizaamrdata.R.drawable.ic_arrow_right),
                    null
                )
                btnNext.invalidate()
            }
            max - 1 -> {
                btnNext.text = getString(com.giza.gizaamrdata.R.string.submit)
                btnNext.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    null,
                    null,
                    resources.getDrawable(R.drawable.ic_save),
                    null
                )
                btnNext.invalidate()
            }
        }

    }

    private fun onNextClicked() {
        btnNext.setOnClickListener {
            if (viewPagerWizard.currentItem < pagerAdapter.itemCount - 1) {
                viewPagerWizard.setCurrentItem(viewPagerWizard.currentItem + 1, true)
            } else {
                saveAndUpload()
            }
        }
    }

    private fun onBackClicked() {
        btnBack.setOnClickListener {
            if (viewPagerWizard.currentItem > 0)
                viewPagerWizard.setCurrentItem(viewPagerWizard.currentItem - 1, true)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("p", viewPagerWizard.currentItem)
        super.onSaveInstanceState(outState)
    }

    override fun onResume() {
        super.onResume()
        wizardDisposable = Rx2Bus.listen(RxEvents.Wizard::class.java).subscribe { wizardEvent ->
            if (wizardEvent.name == RxEvents.Wizard.ACTIVATE_NEXT.name) {
                btnNext.activate()
            } else if (wizardEvent.name == RxEvents.Wizard.DE_ACTIVATE_NEXT.name) {
                btnNext.deActivate()
            }
        }

        if (presenter.isMeterInfoCompleted()) {
            MaterialDialog(this.requireContext()).show {
                title(R.string.actions_title)
                listItemsMultiChoice(
                    R.array.actions_array, initialSelection = intArrayOf(0)
                ) { _, indices, text ->
                    for (s in text) {
                        MeterModel.meter.actions.add(s)
                    }
                    MeterModel.completed = false
                }
                positiveButton(R.string.choose)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        Rx2Bus.removeListener(wizardDisposable)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Logger.d("onActivityResult Wizard")
        for (fragment in pagerAdapter.pages) {
            if (fragment.isAdded) {
                fragment.onActivityResult(requestCode, resultCode, data)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        MeterModel.destroy()
        super.onDestroy()
    }

    @SuppressLint("CheckResult")
    private fun saveAndUpload() {

        Observable.fromCallable {
            try {
                if (!openedForEdit) {
                    FileUtils.renameNewAddedFiles(
                        MeterModel.meter.urls.filterNot {
                            it.startsWith("http://") || it.startsWith("https://")
                        }.map { File(it) })
                } else {
                    FileUtils.renameFiles(
                        MeterModel.meter.urls.filterNot {
                            it.startsWith("http://") || it.startsWith("https://")
                        })
                }
                Logger.e("files trhat are going to be uploaded ${MeterModel.meter.urls.joinToString()}")
            } catch (e: java.lang.Exception) {
                Logger.e("Can'nt copy file from these files ${MeterModel.meter.urls.joinToString()}")
            }

        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete {
                Logger.e("Coping is completed")
                btnNext?.hideLoading()
                try {
                    this.requireContext()?.showToast(getString(R.string.successful_insertion))
                } catch (e: Exception) {
                    Logger.e("couldn't show toast message")
                }
                presenter.addNewMeterToDb()
                goToHome()
            }
            .subscribe {
                btnNext?.showLoading()
                Logger.e("Images is getting copied! ${MeterModel.meter.urls.joinToString()}")
            }
    }

    private fun goToHome() {
        NavigationManager.attachAsRoot(
            this.requireActivity() as AppCompatActivity,
            HomeFragment.newInstance(),
            tag = NavigationManager.Tags.HOME
        )
    }
}