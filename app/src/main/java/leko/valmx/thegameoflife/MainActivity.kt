package leko.valmx.thegameoflife

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.*
import android.view.WindowManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.maxkeppeler.sheets.core.SheetStyle
import com.maxkeppeler.sheets.option.Option
import com.maxkeppeler.sheets.option.OptionSheet
import kotlinx.android.synthetic.main.activity_main.*
import leko.valmx.thegameoflife.game.GameView
import leko.valmx.thegameoflife.game.InteractionManager
import leko.valmx.thegameoflife.game.Cells
import leko.valmx.thegameoflife.game.GameColors
import leko.valmx.thegameoflife.game.tools.AutoPlayTool
import leko.valmx.thegameoflife.game.tools.EditTool
import leko.valmx.thegameoflife.game.tools.PasteTool
import leko.valmx.thegameoflife.recyclers.ThemeAdapter
import leko.valmx.thegameoflife.sheets.BlueprintPresetSelectCategorySheet
import leko.valmx.thegameoflife.sheets.MoreOptionsSheet
import leko.valmx.thegameoflife.utils.AssetUtils
import leko.valmx.thegameoflife.utils.AssetUtils.getPresetCategories
import leko.valmx.thegameoflife.utils.blueprints.Blueprint
import java.util.*

class MainActivity : AppCompatActivity(), OnThemeSelectedListener,
    GameColors.ThemeUpdateListener {

    private var themeSelectingMode = false

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Enable fullscreen TODO better approach?
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        game.mainActivity = this
        showStartPrompt(game)

        initViews()



        btn_nextGeration.post {
            initTheme()
        }


    }

    fun initViews() {

        /*      Buttons      */

        btn_showSchematics.setOnClickListener {
            BlueprintPresetSelectCategorySheet(this, game)
        }

        btn_moreGameOptions.post {
            loadStartSchematic()
        }

        btn_edit.setOnClickListener {
            game.interactionManager.registeredInteraction = EditTool(game)
        }

        btn_autoPlay.setOnClickListener {
            game.interactionManager.registeredInteraction = AutoPlayTool(game)
        }

        btn_endInteration.setOnClickListener {
            game.interactionManager.registeredInteraction = null
        }

        btn_moreGameOptions.setOnClickListener {
            MoreOptionsSheet(this, game)
        }

        btn_nextGeration.setOnClickListener {
            game.cells.calculateNextGenAsync()
        }

        /*      MISC        */

        themeView.setOnClickListener {
            themeSelectingMode = !themeSelectingMode
            if (theme_selector.visibility == GONE) {
                theme_selector.visibility = VISIBLE
            } else {
                theme_selector.visibility = GONE
            }
        }

        recycler_contextTools.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        recycler_themes.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        recycler_themes.adapter = ThemeAdapter(game, this)

    }


    fun initContextTool(interactable: InteractionManager.Interactable?) {
        if (context_tools.visibility == VISIBLE) {
            context_tools.visibility = INVISIBLE
            tool_bar.visibility = VISIBLE
            tool_name_bar.visibility = INVISIBLE
        } else {
            context_tools.visibility = VISIBLE
            tool_bar.visibility = INVISIBLE
            tool_name_bar.visibility = VISIBLE
            if (interactable != null)
                context_tool_info.text = interactable.getName()
        }
    }

    // Not used. TODO evaluate usefulness
    private fun loadStartSchematic() {
        val random = Random()

        val presetCategories = getPresetCategories()

        val randomCategory = presetCategories[random.nextInt(presetCategories.size)]
        try {
            val gridManager = game.gridManager

            val patternNamesOfCategory =
                AssetUtils.listAssetFiles("patterns/${randomCategory.path}/", this)

            val randomPattern =
                patternNamesOfCategory[random.nextInt(patternNamesOfCategory.size)]
            PasteTool(
                game,
                Blueprint(
                    AssetUtils.loadAssetString(
                        this,
                        "patterns/${randomCategory.path}/${randomPattern}"
                    )!!
                )
            ).apply {
                val shapeWidth = toolRect!!.width()
                val centerX = Cells.mapSizeX * gridManager.cellWidth / 2
                val centerY = Cells.mapSizeY * gridManager.cellWidth / 2
                toolRect!!.offset(centerX - shapeWidth / 2F, centerY - shapeWidth / 2F)
            }.applyBlueprint()
        } catch (e: Exception) {
            Log.e(Project.LOG_ID, "Could not load start schematic.")
        }
    }


    /*      Theming        */
    override fun onThemeSelected(theme: ThemeAdapter.ThemeBundle) {

        themeView.callOnClick()

        val prefs = getSharedPreferences(Project.PREF_ID, MODE_PRIVATE).edit()

        prefs.putInt("GRID", theme.grid)
        prefs.putInt("CELL", theme.cell)
        prefs.putInt("BACK", theme.back)
        prefs.putInt("UI", theme.ui)
        prefs.putInt("ICON", theme.icon)
        prefs.putInt("TOOL", theme.tool)
        prefs.putInt("TOOL_STROKE", theme.toolStroke)

        prefs.apply()

        onThemeUpdated()

        themeView.gridColor.color = theme.grid
        themeView.cellColor.color = theme.cell
        themeView.backColor.color = theme.back
        themeView.invalidate()
    }


    class ViewThemeBundle(val viewId: Int)

    private val viewsToBeThemed = LinkedList<ViewThemeBundle>()

    private fun initTheme() {

        // Views, that should be themed are added here: Valid Question: Why dont I use Themes? pls fix
        viewsToBeThemed.apply {
            add(ViewThemeBundle(btn_nextGeration.id))
            add(ViewThemeBundle(btn_autoPlay.id))
            add(ViewThemeBundle(btn_edit.id))
            add(ViewThemeBundle(theme_selector_wrapper.id))
            add(ViewThemeBundle(tool_bar.id))
            add(ViewThemeBundle(btn_moreGameOptions.id))
            add(ViewThemeBundle(tool_name_bar.id))
            add(ViewThemeBundle(tools.id))
            add(ViewThemeBundle(btn_showSchematics.id))
        }

        onThemeUpdated()
    }

    override fun onThemeUpdated() {

        viewsToBeThemed.forEach {
            try {

                findViewById<View>(it.viewId).apply {

                    if (this is ImageView)
                        drawable.setTint(game.gameColors.iconPaint.color)
                    else {
                        background.setTint(game.gameColors.ui.color)

                    }
                }
            } catch (e: java.lang.NullPointerException) {
                Log.e(Project.LOG_ID, "Could not apply theme to for ${it.viewId}")
            }

        }

    }

    private fun showStartPrompt(gameView: GameView) {
        OptionSheet().show(this) {
            title("How would you like to start?")
            cancelableOutside(false)
            with(Option(R.drawable.random_cube, "Random"))
            with(Option(R.drawable.upload, "With Blueprint"))
            with(Option("Empty"))

            style(SheetStyle.DIALOG)

            onPositive { index: Int, option: Option ->
                when (index) {
                    0 -> {
                        gameView.cells.randomize()
                    }
                    1 -> {
                        BlueprintPresetSelectCategorySheet(
                            requireContext(),
                            gameView
                        )
                    }
                }

            }

        }

    }


    /*   Methods stop any tasks, when App is exited to stop using resources   */
    override fun onPause() {
        super.onPause()
        game.animationManager.running = false
    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")

    override fun onBackPressed() {
        /*   unregisters interaction, so interactions can stop safely  */
        if (game.interactionManager.registeredInteraction != null) {
            game.interactionManager.registeredInteraction = null
            return
        }
        super.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        game.animationManager.running = true
        game.animationManager.run()
    }

}

interface OnThemeSelectedListener {
    fun onThemeSelected(theme: ThemeAdapter.ThemeBundle)
}
