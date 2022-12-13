package leko.valmx.thegameoflife.recyclers

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import kotlinx.android.synthetic.main.item_saved_sketch.view.*
import leko.valmx.thegameoflife.R
import leko.valmx.thegameoflife.game.tools.copypasta.Sketch
import leko.valmx.thegameoflife.game.tools.copypasta.SketchLoadSaver

class SketchAdapter(context: Context, val onSketchSelectedListener: OnSketchSelectedListener) :
    Adapter<SketchAdapter.VH>() {

    val sketchManager = SketchLoadSaver(context)

    var names = sketchManager.getSketchNames()

    inner class VH(item: View) : RecyclerView.ViewHolder(item) {

        @SuppressLint("NotifyDataSetChanged")
        fun bind(name: String, sketchLoadSaver: SketchLoadSaver) {
            val sketch = sketchLoadSaver.getSketch(name)
            itemView.gameView.post {
                itemView.gameView.init() {
                    try {

//                        itemView.gameView.previewManager.init(sketch!!, true)
                    } catch (ignored: java.lang.Exception) {

                    }
                }
            }

            itemView.place_btn.setOnClickListener {
                onSketchSelectedListener.onSketchSelected(sketch!!)
            }

            itemView.sketch_name.text = name

            itemView.btn_delete_sketch.setOnClickListener {
                sketchLoadSaver.deleteSketch(name)
                names = sketchManager.getSketchNames()
                notifyDataSetChanged()
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            LayoutInflater.from(parent.context).inflate(R.layout.item_saved_sketch, parent, false)
        )
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(names!![position], sketchManager)
    }

    override fun getItemCount(): Int {

        Log.i("NAMES ARE", "${names?.size}")

        return names?.size ?: 0
    }

    interface OnSketchSelectedListener {
        fun onSketchSelected(sketch: Sketch)
    }

}