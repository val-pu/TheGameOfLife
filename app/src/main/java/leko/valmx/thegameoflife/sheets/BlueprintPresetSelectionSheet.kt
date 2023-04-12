package leko.valmx.thegameoflife.sheets

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.maxkeppeler.sheets.core.IconButton
import com.maxkeppeler.sheets.core.Sheet
import com.maxkeppeler.sheets.core.SheetStyle
import com.maxkeppeler.sheets.info.InfoSheet
import kotlinx.android.synthetic.main.sheet_predefined_selector.*
import leko.valmx.thegameoflife.R
import leko.valmx.thegameoflife.game.GameView
import leko.valmx.thegameoflife.game.tools.PasteTool
import leko.valmx.thegameoflife.game.utils.GameRuleHelper
import leko.valmx.thegameoflife.recyclers.BlueprintPresetRecycler
import leko.valmx.thegameoflife.utils.AssetUtils
import leko.valmx.thegameoflife.utils.PresetCategory
import leko.valmx.thegameoflife.utils.blueprints.Blueprint
import java.util.*

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
        style(SheetStyle.DIALOG)


        this.show()
        onNegative("Back") {
            BlueprintPresetSelectCategorySheet(gameView.context, gameView)
        }
        positiveText = "Cancel"

        title("Select one item...")

        displayToolbar()
        withIconButton(IconButton(R.drawable.external_link)) {
            SourceSheet(
                requireContext(),
                category.url,
                "16.12.2022",
                "GNU Free Documentation License 1.2",
                "https://www.gnu.org/licenses/fdl-1.3.html"
            )
        }

    }

    override fun dismiss() {
        super.dismiss()
        System.gc()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        data = AssetUtils.listAssetFiles("patterns/${category.path}", requireContext())
        Arrays.sort(data)
        Log.d("Loaded assets", data.contentToString())

        presets_recycler.layoutManager = GridLayoutManager(context, 2)
        presets_recycler.adapter =
            BlueprintPresetRecycler(data, category, {

                val newRule = GameRuleHelper.RuleSet(it.rule)
                val gameRuleHelper = GameRuleHelper(requireContext()).apply { loadRules() }

                val initPasteTool = {
                    gameView.interactionManager.registeredInteraction =
                        PasteTool(gameView, it)

                }

                if (gameRuleHelper.ruleSet.getRuleInt() != newRule.getRuleInt()) {
                    InfoSheet().show(requireContext()) {
                        showsDialog = true
                        title("This pattern needs a different rule set to work")
                        content("Change the rule to ${it.rule}?")
                        onPositive("Yes") {
                            gameRuleHelper.saveRule(newRule)
                            gameView.javaActorManager.ruleSet = newRule
                            initPasteTool()
                        }

                        onNegative(initPasteTool)

                    }
                    return@BlueprintPresetRecycler
                }
                initPasteTool()

            }, this)

    }

    private fun loadAllBlueprints(): Array<String> {

        val result = LinkedList<Blueprint>()

        val assetFiles = AssetUtils.listAssetFiles("patterns/ship", requireContext())

        return assetFiles
    }

    override fun onCreateLayoutView(): View {
        return View.inflate(context, R.layout.sheet_predefined_selector, null)
    }


}