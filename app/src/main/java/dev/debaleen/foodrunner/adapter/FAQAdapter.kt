package dev.debaleen.foodrunner.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.debaleen.foodrunner.R
import dev.debaleen.foodrunner.model.FAQUIModel

class FAQAdapter(
    private val faqsList: ArrayList<FAQUIModel>
) : RecyclerView.Adapter<FAQAdapter.FAQViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FAQViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.faq_item, parent, false)

        return FAQViewHolder(view)
    }

    override fun getItemCount(): Int {
        return faqsList.size
    }

    override fun onBindViewHolder(holder: FAQViewHolder, position: Int) {
        val faq = faqsList[position]
        holder.bind(faq, position)
    }

    class FAQViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        private val txtQuestion: TextView = view.findViewById(R.id.txtQuestion)
        private val txtAnswer: TextView = view.findViewById(R.id.txtAnswer)

        fun bind(faqUiModel: FAQUIModel, position: Int) {
            txtQuestion.text = view.context.getString(
                R.string.question_template,
                position + 1,
                faqUiModel.question
            )
            txtAnswer.text = view.context.getString(R.string.answer_template, faqUiModel.answer)
        }
    }
}