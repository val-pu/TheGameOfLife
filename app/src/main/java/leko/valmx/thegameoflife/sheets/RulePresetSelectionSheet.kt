package leko.valmx.thegameoflife.sheets

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.maxkeppeler.sheets.core.Sheet
import kotlinx.android.synthetic.main.sheet_predefined_rules_selector.*
import kotlinx.android.synthetic.main.sheet_rules.*
import kotlinx.android.synthetic.main.sheet_stencil.*
import leko.valmx.thegameoflife.R
import leko.valmx.thegameoflife.game.GameView
import leko.valmx.thegameoflife.game.tools.PasteTool
import leko.valmx.thegameoflife.game.tools.SelectionTool
import leko.valmx.thegameoflife.game.tools.copypasta.Sketch
import leko.valmx.thegameoflife.game.utils.GameRuleHelper
import leko.valmx.thegameoflife.recyclers.RulePresetPickerAdapter
import leko.valmx.thegameoflife.recyclers.RuleSheetAdapter
import leko.valmx.thegameoflife.recyclers.SketchAdapter

class RulePresetSelectionSheet(context: Context, val gameView: GameView, val ruleSelectedListener: RulePresetPickerAdapter.RuleSelectedListener) : Sheet(){
    fun show(ctx: Context) {
        this.windowContext = ctx
        this.width = null


        this.show()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rule_presets_recycler.layoutManager = LinearLayoutManager(context)
        rule_presets_recycler.adapter = RulePresetPickerAdapter(requireContext(), ruleSelectedListener) {
            dismiss()
        }

        source.setOnClickListener {
            SourceSheet(requireContext(),"https://conwaylife.com/wiki/List_of_Life-like_cellular_automata","27.11.2022","GNU Free Documentation License 1.2","https://www.gnu.org/licenses/fdl-1.3.html")
        }

    }

    override fun onCreateLayoutView(): View {
        return View.inflate(context, R.layout.sheet_predefined_rules_selector, null)
    }


}