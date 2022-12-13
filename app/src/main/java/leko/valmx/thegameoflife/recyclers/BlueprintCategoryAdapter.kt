package leko.valmx.thegameoflife.recyclers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import kotlinx.android.synthetic.main.item_blueprint_selection.view.*
import leko.valmx.thegameoflife.R
import leko.valmx.thegameoflife.utils.AssetUtils
import leko.valmx.thegameoflife.utils.PresetCategory

class BlueprintCategoryAdapter(val onSelected: (PresetCategory) -> Unit) :
    Adapter<BlueprintCategoryAdapter.VH>() {

    private val data = AssetUtils.getPresetCategories()

    inner class VH(itemView: View) : ViewHolder(itemView) {
        fun bind(presetCategory: PresetCategory) {
            itemView.blueprintName.setText(presetCategory.name)
            itemView.setOnClickListener {
                onSelected(presetCategory)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_preset_category, parent, false)
        )
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val category = data[position]
        holder.bind(category)
    }

    override fun getItemCount(): Int = data.size


}