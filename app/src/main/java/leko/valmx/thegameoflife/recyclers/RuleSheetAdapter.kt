package leko.valmx.thegameoflife.recyclers

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import kotlinx.android.synthetic.main.item_rule_tweak.view.*
import leko.valmx.thegameoflife.R
import leko.valmx.thegameoflife.game.utils.GameRuleHelper

class RuleSheetAdapter(ctx: Context, val ruleSet: GameRuleHelper.RuleSet) : Adapter<RuleSheetAdapter.VH>() {


    class VH(item: View) : ViewHolder(item) {

        fun bind(rules: GameRuleHelper.RuleSet, position: Int) {
            itemView.tweak_title.text = "${position + 1} Neighbours"

            itemView.check_survive.isChecked = rules.willSurvive(position+1)
            itemView.check_born.isChecked = rules.isBorn(position+1)



            itemView.check_born.setOnCheckedChangeListener { buttonView, isChecked ->
                rules.setBorn(position, isChecked)
            }

            itemView.check_survive.setOnCheckedChangeListener { buttonView, isChecked ->
                rules.setSurvive(position, isChecked)
            }
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            LayoutInflater.from(parent.context).inflate(R.layout.item_rule_tweak, parent, false)
        )
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(ruleSet, position)
    }

    override fun getItemCount(): Int {
        return 8
    }
}