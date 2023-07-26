package leko.valmx.thegameoflife.sheets

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.maxkeppeler.sheets.core.Sheet
import kotlinx.android.synthetic.main.sheet_rules.*
import kotlinx.android.synthetic.main.sheet_stencil.*
import leko.valmx.thegameoflife.R
import leko.valmx.thegameoflife.game.GameView
import leko.valmx.thegameoflife.game.tools.SelectionTool
import leko.valmx.thegameoflife.game.tools.copypasta.Sketch
import leko.valmx.thegameoflife.game.utils.GameRuleHelper
import leko.valmx.thegameoflife.recyclers.RuleSheetAdapter
import leko.valmx.thegameoflife.recyclers.SketchAdapter

class BlueprintSheet(context: Context, val gameView: GameView) : Sheet(),
    SketchAdapter.OnSketchSelectedListener {
    fun show(ctx: Context) {
        this.windowContext = ctx
        this.width = null

        positiveText = "Apply"
        positiveListener = {
            val ruleSet = (rulesRecycler.adapter as RuleSheetAdapter).ruleSet
            GameRuleHelper(ctx).saveRule((rulesRecycler.adapter as RuleSheetAdapter).ruleSet)
            gameView.cells.ruleSet = ruleSet
        }
        showsDialog = true
        displayPositiveButton(false)

        this.show()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler2.layoutManager = LinearLayoutManager(context)
        recycler2.adapter = SketchAdapter(requireContext(), this)

        btn_new_blueprint.setOnClickListener {
            gameView.interactionManager.registeredInteraction = SelectionTool(gameView)
            dismiss()
        }

        btn_browse_preset_blueprints.setOnClickListener {
            BlueprintPresetSelectCategorySheet(requireContext(), gameView)
        }

    }

    override fun onCreateLayoutView(): View {
        return View.inflate(context, R.layout.sheet_stencil, null)
    }

    override fun onSketchSelected(sketch: Sketch) {
//        gameView.interactionManager.registeredInteraction = PasteTool(gameView, sketch)
        dismiss()
    }
}