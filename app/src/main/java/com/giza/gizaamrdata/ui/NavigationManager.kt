package com.giza.gizaamrdata.ui

/**
 * @author hossam.
 */


import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.giza.gizaamrdata.R
import com.giza.gizaamrdata.utils.Rx2Bus
import com.giza.gizaamrdata.utils.RxEvents

object NavigationManager {

    enum class Tags {
        AUTH,LOGIN, HOME, SEARCH, WIZARD, METER_DETAILS, PAGE1, PAGE2, PAGE3, PAGE4, PAGE5, SETTING
    }

    /**
     * Displays the next fragment
     *
     * @param fragment
     */
    fun attach(
        activity: AppCompatActivity,
        fragment: Fragment, @androidx.annotation.IdRes layoutId: Int,
        tag: Tags
    ) {

        for (i in 0 until activity.supportFragmentManager.backStackEntryCount)
            activity.supportFragmentManager.popBackStackImmediate()
        if (!isAtTheTopOnBackStack(activity.supportFragmentManager, tag)) {
            val ft = activity.supportFragmentManager.beginTransaction()
            ft.addToBackStack(tag.name)
            ft.replace(layoutId, fragment)
            ft.commitNow()
        }
    }

    /**
     * Displays the next fragment
     *
     * @param fragment
     */
    fun attachWithStack(
        activity: AppCompatActivity,
        fragment: Fragment,
        @androidx.annotation.IdRes layoutId: Int = 0,
        tag: Tags
    ) {
        if (!isAtTheTopOnBackStack(activity.supportFragmentManager, tag)) {
            val ft = activity.supportFragmentManager.beginTransaction()
            ft.addToBackStack(tag.name)
            ft.add(R.id.container_main_frame_layout, fragment)
            ft.commitAllowingStateLoss()
        }
    }

    fun popFragmentByTag(activity: AppCompatActivity, tag: Tags) {

        val ft = activity.supportFragmentManager.beginTransaction()
        val f = activity.supportFragmentManager.findFragmentByTag(tag.name)
        if (f != null) {
            ft.remove(f)
            activity.supportFragmentManager.popBackStackImmediate()
            ft.commitAllowingStateLoss()
        }
    }

    fun popByTag(activity: AppCompatActivity, tag: Tags) {
        for (i in 0 until activity.supportFragmentManager.backStackEntryCount)
            activity.supportFragmentManager.popBackStackImmediate()
        val ft = activity.supportFragmentManager.beginTransaction()
        val f = activity.supportFragmentManager.findFragmentByTag(tag.name)
        if (f != null) {
            ft.remove(f)
            ft.commitNow()
        }
    }

    fun getByTag(fragmentManager : FragmentManager, tag: Tags): Fragment? {
        fragmentManager.executePendingTransactions()
        return fragmentManager.findFragmentByTag(tag.name)
    }

    fun getById(fragmentManager : FragmentManager): Fragment? {
        fragmentManager.executePendingTransactions()
        return fragmentManager.findFragmentById(R.id.container_main_frame_layout)
    }

    fun isUp(activity: AppCompatActivity, tag: Tags): Boolean {
        return run {
            val f = activity.supportFragmentManager.findFragmentByTag(tag.name)
            f != null
        }
    }


    /**
     * Displays the next fragment
     *
     * @param fragment
     */
    fun attach(
        activity: AppCompatActivity,
        fragment: Fragment,
        isAnimated: Boolean,
        tag: Tags
    ) {

        for (i in 0 until activity.supportFragmentManager.backStackEntryCount)
            activity.supportFragmentManager.popBackStackImmediate()
        if (!isAtTheTopOnBackStack(activity.supportFragmentManager, tag)) {
            val ft = activity.supportFragmentManager.beginTransaction()

            if (isAnimated) {
                ft.setCustomAnimations(
                    R.anim.slide_in_left,
                    R.anim.slide_out_left
                )
            }
            ft.addToBackStack(tag.name)
            ft.replace(R.id.container_main_frame_layout, fragment)
            ft.commit()
        }
    }

    fun attachAsRoot(activity: AppCompatActivity, fragment: Fragment, tag: Tags) {
        for (i in 0 until activity.supportFragmentManager.backStackEntryCount)
            activity.supportFragmentManager.popBackStackImmediate()
        attach(activity, fragment, false, tag)
    }

    /**
     * @return true if stack has been popped succesfully, false if the stack has one element
     */
    fun popBackStackImmediate(mFragmentManager: FragmentManager): Boolean {
        return if (mFragmentManager.backStackEntryCount == 0) {
            false
        } else {
            if (mFragmentManager.backStackEntryCount == 2) {
                Rx2Bus.send(RxEvents.HomeUtils.BackHome)
            }
            mFragmentManager.popBackStackImmediate()
            true
        }
    }

    fun isAtTheTopOnBackStack(mFragmentManager: FragmentManager, tag: Tags): Boolean {
        return if (mFragmentManager.backStackEntryCount == 0) false
        else TextUtils.equals(
            mFragmentManager.getBackStackEntryAt(mFragmentManager.backStackEntryCount - 1).name, tag.name
        )
    }

    /**
     * Navigates back by popping teh back stack. If there is no more items left we finish the
     * current activity.
     *
     * @param baseActivity
     */
    fun navigateBack(baseActivity: AppCompatActivity) {

        if (baseActivity.supportFragmentManager.backStackEntryCount == 1) {
            // we can finish the base activity since we have no other fragments
            baseActivity.finish()
        } else {
            popBackStackImmediate(baseActivity.supportFragmentManager)
        }
    }

    fun clearBackStack(fragmentManager: FragmentManager) {
        for (entry in 0 until fragmentManager.backStackEntryCount) {
            fragmentManager.popBackStack()
        }
    }
}