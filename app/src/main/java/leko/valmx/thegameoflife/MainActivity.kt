package leko.valmx.thegameoflife

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.view.View.*
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import leko.valmx.thegameoflife.game.InteractionManager
import leko.valmx.thegameoflife.game.PaintManager
import leko.valmx.thegameoflife.game.animations.Animation
import leko.valmx.thegameoflife.game.tools.AutoPlayTool
import leko.valmx.thegameoflife.game.tools.EditTool
import leko.valmx.thegameoflife.game.tools.PasteTool
import leko.valmx.thegameoflife.game.tools.copypasta.Sketch
import leko.valmx.thegameoflife.recyclers.ThemeAdapter
import leko.valmx.thegameoflife.sheets.MoreOptionsSheet
import leko.valmx.thegameoflife.sheets.RulesSheet
import leko.valmx.thegameoflife.utils.AssetUtils
import leko.valmx.thegameoflife.utils.blueprints.Blueprint
import java.util.LinkedList
import kotlin.math.roundToLong

class MainActivity : AppCompatActivity(), OnThemeSelectedListener,
    PaintManager.ThemeUpdateListener {

    private var autoPlayRunning = false
    private var themeSelectingMode = false


    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Views, die custom eingefärbt werden sollen

        themedView.apply {
            add(ViewThemeBundle(nextStep.id))
            add(ViewThemeBundle(auto_play.id))
            add(ViewThemeBundle(edit_btn.id))
            add(ViewThemeBundle(theme_selector_wrapper.id))
            add(ViewThemeBundle(tool_bar.id))
            add(ViewThemeBundle(btn_more.id))
            add(ViewThemeBundle(tool_name_bar.id))
            add(ViewThemeBundle(tools.id))
        }

        game.mainActivity = this


        recycler_themes.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        recycler_themes.adapter = ThemeAdapter(game, this)

        nextStep.setOnClickListener {
            game.actorManager.doCycle()
        }

        nextStep.post {
            game.actorManager.doCycle()
            game.actorManager.doCycle()
            game.actorManager.doCycle()
            game.actorManager.doCycle()
        }

        auto_play.setOnClickListener {
            game.interactionManager.registeredInteraction = AutoPlayTool(game)
        }


        themeView.setOnClickListener {
            themeSelectingMode = !themeSelectingMode

            if (theme_selector.visibility == GONE) {
                theme_selector.visibility = VISIBLE

            } else
                theme_selector.visibility = GONE
        }

        edit_btn.setOnClickListener {
            game.interactionManager.registeredInteraction = EditTool(game)
        }


        btn_end_tool.setOnClickListener {
            game.interactionManager.registeredInteraction = null
        }

        btn_more.setOnClickListener {
            MoreOptionsSheet(this, game)
        }

        context_tools_recycler.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        btn_more.post {
            onThemeUpdated()
        }

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

    override fun onPostCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onPostCreate(savedInstanceState, persistentState)
    }

    override fun onPause() {
        super.onPause()
//        game.interactionManager.registeredInteraction = null
        game.animationManager.running = false
    }


    override fun onResume() {
        super.onResume()
        game.animationManager.running = true
        game.animationManager.run()
    }


    var generations = 0


    val PREF_ID = "CGOL_VALGAMES"

    override fun onThemeSelected(theme: ThemeAdapter.ThemeBundle) {

        themeView.callOnClick()

        val prefs = getSharedPreferences(PREF_ID, MODE_PRIVATE).edit()

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

    // System zum updaten der UI-Farben

    class ViewThemeBundle(val viewId: Int)

    val themedView = LinkedList<ViewThemeBundle>()


    override fun onThemeUpdated() {

        themedView.forEach {
            try {

                findViewById<View>(it.viewId).apply {

                    if (this is ImageView)
                        drawable.setTint(game.paintManager.iconPaint.color)
                    else {
                        background.setTint(game.paintManager.uiPaint.color)

                    }
                }
            } catch (e: java.lang.NullPointerException) {
                Log.e("theming@gol", "Could not apply theme to for ${it.viewId}")
            }

        }

    }


}

interface OnThemeSelectedListener {
    fun onThemeSelected(theme: ThemeAdapter.ThemeBundle)
}
