package leko.valmx.thegameoflife.recyclers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import kotlinx.android.synthetic.main.item_blueprint_info_misc.view.*
import leko.valmx.thegameoflife.R
import leko.valmx.thegameoflife.utils.blueprints.Blueprint

class BlueprintInfoRecycler(val blueprint: Blueprint) : Adapter<BlueprintInfoRecycler.VH>() {

    private val comments = blueprint.comments
    private val websitePattern = Regex("https.*")

    private val viewTypeWebsite = 1

    open class VH(itemView: View) : ViewHolder(itemView) {
        open fun bind(comment: String) {
            itemView.content.text = comment
        }
    }

    class WebsiteInfoVH(itemView: View) : VH(itemView) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            viewTypeWebsite -> WebsiteInfoVH(
                inflater.inflate(
                    R.layout.item_blueprint_info_website,
                    parent,
                    false
                )
            )
            else -> VH(
                inflater.inflate(R.layout.item_blueprint_info_misc, parent, false)
            )
        }

    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(comments[position])
    }

    override fun getItemViewType(position: Int): Int {

        val s = comments[position]

        if (s.matches(websitePattern)) {
            return viewTypeWebsite;
        }

        return 0;
    }

    override fun getItemCount(): Int {
        return comments.size

    }

}