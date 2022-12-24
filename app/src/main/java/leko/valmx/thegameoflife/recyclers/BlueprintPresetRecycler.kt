package leko.valmx.thegameoflife.recyclers

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.maxkeppeler.sheets.core.Sheet
import kotlinx.android.synthetic.main.item_blueprint_selection.view.*
import leko.valmx.thegameoflife.R
import leko.valmx.thegameoflife.sheets.BlueprintInfoSheet
import leko.valmx.thegameoflife.sheets.MoreOptionsSheet
import leko.valmx.thegameoflife.utils.AssetUtils
import leko.valmx.thegameoflife.utils.PresetCategory
import leko.valmx.thegameoflife.utils.blueprints.Blueprint

class BlueprintPresetRecycler(
    private val bluePrints: Array<String>,
    private val category: PresetCategory,
    private val onBluePrintSelected: (Blueprint) -> Unit,
    private val originSheet: Sheet? = null
) :
    Adapter<BlueprintPresetRecycler.VH>() {

    class VH(itemView: View) : ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_blueprint_selection, parent, false)
        )
    }

    override fun onBindViewHolder(holder: VH, position: Int) {


        val itemView = holder.itemView
        val blueprintName = bluePrints[position]
        Log.i("Loading Blueprint",blueprintName)
        val blueprint = Blueprint(
            AssetUtils.loadAssetString(
                itemView.context,
                "patterns/${category.path}/$blueprintName"
            )!!
        )

        itemView.blueprintName.text = blueprint.name
        itemView.blueprint_author.text = blueprint.author


        val preview = holder.itemView.preview
        preview.post {

            preview.init() {
                preview.previewManager.init(blueprint, true)
            }
        }

        itemView.setOnClickListener {
            BlueprintInfoSheet(blueprint).show(itemView.context)
        }

        itemView.btn_select_blueprint.setOnClickListener {
            onBluePrintSelected(blueprint)
            originSheet?.dismiss()
        }


    }

    override fun getItemCount(): Int = bluePrints.size

}