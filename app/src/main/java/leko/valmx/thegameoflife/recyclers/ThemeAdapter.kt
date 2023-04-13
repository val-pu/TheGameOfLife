package leko.valmx.thegameoflife.recyclers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import kotlinx.android.synthetic.main.activity_main.view.*
import leko.valmx.thegameoflife.MainActivity
import leko.valmx.thegameoflife.R
import leko.valmx.thegameoflife.game.GameView
import java.util.*

class ThemeAdapter(val game: GameView, val mainActivity: MainActivity) :
    Adapter<ThemeAdapter.VH>() {

    class ThemeBundle(
        val back: Int,
        val cell: Int,
        val grid: Int,
        val ui: Int,
        val icon: Int,
        val tool: Int,
        val toolStroke: Int
    )

    val themes = LinkedList<ThemeBundle>()

    init {

        val res = game.resources

        themes.add(
            ThemeBundle(
                res.getColor(R.color.back_1),
                res.getColor(R.color.back_secondary_1),
                res.getColor(R.color.cell_1),
                res.getColor(R.color.grid_1),
                res.getColor(R.color.ui_1),
                res.getColor(R.color.icon_1),
                res.getColor(R.color.tool_1),
            )
        )
/*        themes.add(
            ThemeBundle(
                res.getColor(R.color.back_2),
                res.getColor(R.color.cell_2),
                res.getColor(R.color.grid_2),
                res.getColor(R.color.ui_2),
                res.getColor(R.color.icon_2),
                res.getColor(R.color.tool_1),
                res.getColor(R.color.tool_stroke_1),
            )
        )*/
        themes.add(
            ThemeBundle(
                res.getColor(R.color.back_3),
                res.getColor(R.color.back_secondary_3),
                res.getColor(R.color.cell_3),
                res.getColor(R.color.grid_3),
                res.getColor(R.color.ui_3),
                res.getColor(R.color.icon_3),
                res.getColor(R.color.tool_3),
            )
        )
/*        themes.add(
            ThemeBundle(
                res.getColor(R.color.back_4),
                res.getColor(R.color.cell_4),
                res.getColor(R.color.grid_4),
                res.getColor(R.color.ui_4),
                res.getColor(R.color.icon_4),
                res.getColor(R.color.tool_1),
                res.getColor(R.color.tool_stroke_1),
            )
        )*/
    }


    class VH(itemView: View) : ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = VH(
        LayoutInflater.from(parent.context).inflate(
            R.layout.item_theme_picker, parent, false
        )
    )

    override fun onBindViewHolder(holder: VH, position: Int) {
        val themeBundle = themes[position]
        val itemView = holder.itemView

        itemView.themeView.backColor.color = themeBundle.back
        itemView.themeView.cellColor.color = themeBundle.cell
        itemView.themeView.gridColor.color = themeBundle.grid

        itemView.setOnClickListener {
            game.paintManager.applyTheme(themeBundle, mainActivity)
            mainActivity.onThemeSelected(themeBundle)
        }

    }

    override fun getItemCount(): Int = themes.size

}