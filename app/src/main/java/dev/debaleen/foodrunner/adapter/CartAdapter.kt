package dev.debaleen.foodrunner.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.debaleen.foodrunner.R
import dev.debaleen.foodrunner.model.RestaurantFoodItem

class CartAdapter(private var foodItemList: ArrayList<RestaurantFoodItem>) :
    RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    fun updateList(newList: ArrayList<RestaurantFoodItem>) {
        foodItemList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.order_items, parent, false)

        return CartViewHolder(view)
    }

    override fun getItemCount(): Int {
        return foodItemList.size
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = foodItemList[position]
        holder.bind(item)
    }

    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val txtItemName: TextView = view.findViewById(R.id.txtItemName)
        private val txtOrderItemPrice: TextView = view.findViewById(R.id.txtOrderItemPrice)

        fun bind(foodItem: RestaurantFoodItem) {
            txtItemName.text = foodItem.name
            txtOrderItemPrice.text = foodItem.cost
        }
    }
}