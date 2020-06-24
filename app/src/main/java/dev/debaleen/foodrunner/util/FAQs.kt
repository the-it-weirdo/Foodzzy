package dev.debaleen.foodrunner.util

import dev.debaleen.foodrunner.model.FAQUIModel

val faqs = hashMapOf<String, String>(
    Pair("question", "answer"),
    Pair("question1", "answer2")
)

fun formFaqs(faqs: HashMap<String, String>): ArrayList<FAQUIModel> {
    val returnList = arrayListOf<FAQUIModel>()

    faqs.map {
        returnList.add(
            FAQUIModel(
                question = it.key,
                answer = it.value
            )
        )
    }

    return returnList
}