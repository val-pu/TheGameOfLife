package leko.valmx.thegameoflife.sheets

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.maxkeppeler.sheets.core.Sheet
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_saved_sketch.*
import kotlinx.android.synthetic.main.sheet_predefined_rules_selector.*
import leko.valmx.thegameoflife.R
import leko.valmx.thegameoflife.game.GameView
import leko.valmx.thegameoflife.game.tools.PasteTool
import leko.valmx.thegameoflife.game.tools.copypasta.Sketch
import leko.valmx.thegameoflife.recyclers.BlueprintPresetRecycler
import leko.valmx.thegameoflife.utils.AssetUtils
import leko.valmx.thegameoflife.utils.PresetCategory
import leko.valmx.thegameoflife.utils.blueprints.Blueprint
import java.util.Arrays
import java.util.LinkedList

class BlueprintPresetSelectionSheet(
    private val gameView: GameView,
    private val category: PresetCategory
) : Sheet() {

    private lateinit var data: Array<String>


    init {
        show(gameView.context)
    }

    fun show(ctx: Context) {
        this.windowContext = ctx
        this.width = null


        this.show()

    }

    override fun dismiss() {
        super.dismiss()
        System.gc()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        data = AssetUtils.listAssetFiles("patterns/ship", requireContext())
        Arrays.sort(data)
        Log.d("Loaded assets", data.contentToString())

        rule_presets_recycler.layoutManager = LinearLayoutManager(context)
        rule_presets_recycler.adapter =
            BlueprintPresetRecycler(data, category, {
                gameView.interactionManager.registeredInteraction =
                    PasteTool(gameView, Sketch(it.cells))
            }, this)

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

        val assetFiles = AssetUtils.listAssetFiles("patterns/ship", requireContext())

        return assetFiles
    }

    override fun onCreateLayoutView(): View {
        return View.inflate(context, R.layout.sheet_predefined_rules_selector, null)
    }


}