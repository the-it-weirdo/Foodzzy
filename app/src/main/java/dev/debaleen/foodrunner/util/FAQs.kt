package dev.debaleen.foodrunner.util

import dev.debaleen.foodrunner.model.FAQModel

val faqs = hashMapOf<String, String>(
    Pair("Is it safe to order from Foodzzy?", "Yes, it is. Our employees strictly follow the guidelines of Social distancing and Personal hygiene."),
    Pair("How does Foodzzy ensures contact less delivery?", "Our employers wear single use gloves for each delivery. The food is wrapped in a paper covering which the " +
            "delivery person will keep at your doorstep and step away 6 feet from the door after ringing your doorbell. You can collect the food then."),
    Pair("Is Pay on Delivery available?", "To ensure contact less deliveries, we have temporarily suspended Pay on Delivery feature."),
    Pair("Are refunds made on cancellation of order?", "Our partner restaurants immediately starts working on a order on receiving. So, if a cancellation s requested from the " +
            "user, we will be unable to refund the order amount. However, if the order is cancelled from the restaurant, the amount will be refunded to the user."),
    Pair("Can I order from 2 or more restaurants at the same time?", "At present, our app doesn't support the feature. However, you can make separate orders from the app without waiting for an order to be delivered first.")
)

fun formFaqs(faqs: HashMap<String, String>): ArrayList<FAQModel> {
    val returnList = arrayListOf<FAQModel>()

    faqs.map {
        returnList.add(
            FAQModel(
                question = it.key,
                answer = it.value
            )
        )
    }

    return returnList
}