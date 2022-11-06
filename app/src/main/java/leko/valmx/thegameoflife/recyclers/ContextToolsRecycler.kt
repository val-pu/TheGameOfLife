package leko.valmx.thegameoflife.recyclers

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import kotlinx.android.synthetic.main.context_tool.view.*
import leko.valmx.thegameoflife.R
import java.util.LinkedList

class ContextToolsRecycler(val data: LinkedList<ContextTool>) :
    RecyclerView.Adapter<ContextToolsRecycler.VH>() {

    class VH(view: View) : ViewHolder(view) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(parent.context).inflate(R.layout.context_tool, parent, false))
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: VH, position: Int) {

        val itemView = holder.itemView

        val toolInfo = data[position]

        itemView.tool_img.setOnClickListener {
            toolInfo.onClick.onClick(it)

        }
        itemView.tool_img.setImageDrawable(holder.itemView.context.getDrawable(toolInfo.drawableId)!!)

    }

    override fun getItemCount(): Int = data.size

    class ContextTool(val drawableId: Int, val onClick: OnClickListener)

}