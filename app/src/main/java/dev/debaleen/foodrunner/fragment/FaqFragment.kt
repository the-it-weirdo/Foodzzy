package dev.debaleen.foodrunner.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.debaleen.foodrunner.R
import dev.debaleen.foodrunner.adapter.FAQAdapter
import dev.debaleen.foodrunner.util.faqs
import dev.debaleen.foodrunner.util.formFaqs


class FaqFragment : Fragment() {

    private lateinit var recyclerFaq: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var recyclerAdapter: FAQAdapter

    private val faqList = formFaqs(faqs)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_faq, container, false)
        recyclerFaq = view.findViewById(R.id.recyclerFAQ)
        recyclerFaq.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(activity as Context)
        recyclerFaq.layoutManager = layoutManager
        recyclerAdapter = FAQAdapter(faqList)
        recyclerFaq.adapter = recyclerAdapter

        recyclerFaq.addItemDecoration(
            DividerItemDecoration(
                recyclerFaq.context,
                (layoutManager as LinearLayoutManager).orientation
            )
        )

        return view
    }

}