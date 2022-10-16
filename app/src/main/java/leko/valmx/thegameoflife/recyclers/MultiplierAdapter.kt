package leko.valmx.thegameoflife.recyclers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import kotlinx.android.synthetic.main.item_multiplier.view.*
import leko.valmx.thegameoflife.R
import java.util.LinkedList

class MultiplierAdapter : Adapter<MultiplierAdapter.VH>() {

    val data = arrayOf(0.1, .5, 1.0, 1.5, 2.0, 3.0, 10.0, 20.0)

    class VH(itemView: View) : ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            LayoutInflater.from(parent.context).inflate(R.layout.item_multiplier, parent, false)
        )
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.itemView.multiplier.text = "${data[position]} x"

    }

    override fun getItemCount(): Int = data.size

}