package dev.debaleen.foodrunner.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.debaleen.foodrunner.R
import dev.debaleen.foodrunner.model.OrderHistoryItem
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class OrderHistoryAdapter(private var orderHistoryList: ArrayList<OrderHistoryItem>) :
    RecyclerView.Adapter<OrderHistoryAdapter.OrderHistoryViewHolder>() {

    fun updateList(newList: ArrayList<OrderHistoryItem>) {
        orderHistoryList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.order_history_item, parent, false)

        return OrderHistoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return orderHistoryList.size
    }

    override fun onBindViewHolder(holder: OrderHistoryViewHolder, position: Int) {
        val orderItem = orderHistoryList[position]
        holder.bind(orderItem)
    }

    class OrderHistoryViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        private val txtRestaurantName: TextView = view.findViewById(R.id.txtRestaurantName)
        private val txtDate: TextView = view.findViewById(R.id.txtDate)
        private val container: RecyclerView = view.findViewById(R.id.orderItemsRecycler)
        private val txtTotalCost: TextView = view.findViewById(R.id.txtTotalCost)

        fun bind(orderHistoryItem: OrderHistoryItem) {
            txtRestaurantName.text = orderHistoryItem.restaurantName
            val placedAtDate = SimpleDateFormat(
                "dd-MM-yy HH:mm:ss",
                Locale.getDefault()
            ).parse(orderHistoryItem.orderPlacedAt)
            if (placedAtDate != null) {
                val dateStr =
                    SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault()).format(placedAtDate)
                txtDate.text = dateStr
            }
            txtTotalCost.text = orderHistoryItem.totalCost

            container.layoutManager = LinearLayoutManager(container.context)
            container.adapter = CartAdapter(ArrayList(orderHistoryItem.orderFoodItems))
        }
    }
}