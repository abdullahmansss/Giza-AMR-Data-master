package com.giza.gizaamrdata.ui.meterslist


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.giza.gizaamrdata.R
import com.giza.gizaamrdata.models.SearchMeterResultedObject
import com.giza.gizaamrdata.utils.extensions.setImage
import kotlinx.android.synthetic.main.item_meter_view.view.*

/**
 * @author hossam.
 */
class MetersAdapter(
    val context: Context?,
    val presenter: MetersListPresenter,
    private val itemClick: (SearchMeterResultedObject) -> Unit
) : RecyclerView.Adapter<MetersAdapter.MetersListHolder>() {

    var mSelectedPosition = RecyclerView.NO_POSITION
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MetersListHolder {

        return MetersListHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_meter_view, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MetersListHolder, position: Int) {
        holder.bind(presenter.getExistedMeters()[position], itemClick)
    }

    override fun getItemCount(): Int {
        return presenter.getExistedMeters().size
    }

    inner class MetersListHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(
            meter: SearchMeterResultedObject,
            itemClick: (SearchMeterResultedObject) -> Unit
        ) {

            itemView.imgDeviceImage1.setImage(meter.Images.getOrNull(0).toString(), R.drawable.ic_image_placeholder)

            itemView.txtMeterId.text = meter.Number
            itemView.txtMeterVendor.text = meter.Vendor
            itemView.txtOwnerId.text = meter.Owner_National_id
            itemView.txtOwnerNumber.text = meter.Owner_Phone
            itemView.txtOwnerName.text = meter.Owner_Name
            itemView.txtAddress.text = meter.Address
            itemView.txtMeterId.text = meter.Number
            itemView.setOnClickListener {
                itemClick(meter)
                updateSelectedItems(adapterPosition)
            }
        }


        private fun updateSelectedItems(selectedPosition: Int) {
            notifyItemChanged(mSelectedPosition)
            mSelectedPosition = selectedPosition
            notifyItemChanged(mSelectedPosition)
        }
    }
}
