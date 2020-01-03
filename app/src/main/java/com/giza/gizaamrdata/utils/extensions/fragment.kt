package com.giza.gizaamrdata.utils.extensions

import android.os.Bundle
import androidx.fragment.app.Fragment

/**
 * @author hossam.
 */
inline fun <FRAGMENT : Fragment> FRAGMENT.putArgs(argsBuilder: Bundle.() -> Unit)
        : FRAGMENT = this.apply { arguments = Bundle().apply(argsBuilder) }
