package leko.valmx.thegameoflife.sheets

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.maxkeppeler.sheets.core.Sheet
import kotlinx.android.synthetic.main.sheet_predefined_rules_selector.*
import leko.valmx.thegameoflife.R
import leko.valmx.thegameoflife.recyclers.BlueprintPresetRecycler
import leko.valmx.thegameoflife.utils.AssetUtils
import leko.valmx.thegameoflife.utils.blueprints.Blueprint
import java.util.LinkedList

class BlueprintPresetSelectionSheet(val onBluePrintSelected: (Blueprint) -> Unit) : Sheet() {
    fun show(ctx: Context) {
        this.windowContext = ctx
        this.width = null


        this.show()

    }

    override fun dismiss() {
        super.dismiss()
        System.gc()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rule_presets_recycler.layoutManager = LinearLayoutManager(context)
        rule_presets_recycler.adapter =
            BlueprintPresetRecycler(loadAllBlueprints(),onBluePrintSelected,this)

        source.setOnClickListener {
            SourceSheet(
                requireContext(),
                "https://conwaylife.com/wiki/List_of_Life-like_cellular_automata",
                "27.11.2022",
                "GNU Free Documentation License 1.2",
                "https://www.gnu.org/licenses/fdl-1.3.html"
            )
        }

    }

    private fun loadAllBlueprints(): Array<String> {

        val result = LinkedList<Blueprint>()

        val assetFiles = AssetUtils.listAssetFiles("patterns", requireContext())


        return assetFiles
    }

    override fun onCreateLayoutView(): View {
        return View.inflate(context, R.layout.sheet_predefined_rules_selector, null)
    }


}