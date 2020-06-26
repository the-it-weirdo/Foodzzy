package dev.debaleen.foodrunner.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.debaleen.foodrunner.R
import dev.debaleen.foodrunner.model.RestaurantFoodItem

class RestaurantDetailAdapter(
    private var foodItemList: ArrayList<RestaurantFoodItem>,
    private val clickListener: CartButtonListener
) :
    RecyclerView.Adapter<RestaurantDetailAdapter.RestaurantDetailViewHolder>() {

    fun updateDataList(list: ArrayList<RestaurantFoodItem>) {
        foodItemList = list
        println(foodItemList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantDetailViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.food_item, parent, false)

        return RestaurantDetailViewHolder(view)
    }

    override fun getItemCount(): Int {
        return foodItemList.size
    }

    override fun onBindViewHolder(holder: RestaurantDetailViewHolder, position: Int) {
        val item = foodItemList[position]
        holder.bind(item, clickListener)
    }

    class RestaurantDetailViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val txtSerial: TextView = view.findViewById(R.id.txtSerial)
        private val txtFoodName: TextView = view.findViewById(R.id.txtFoodName)
        private val txtCostForOne: TextView = view.findViewById(R.id.txtCostForOne)
        private val btnAddToCart: Button = view.findViewById(R.id.btnAddToCart)
        private val btnRemoveFromCart: Button = view.findViewById(R.id.btnRemoveFromCart)

        fun bind(restaurantFoodItem: RestaurantFoodItem, clickListener: CartButtonListener) {
            txtSerial.text = (adapterPosition + 1).toString()
            txtFoodName.text = restaurantFoodItem.name
            txtCostForOne.text = restaurantFoodItem.costForOne

            btnAddToCart.setOnClickListener {
                if (adapterPosition != -1) {
                    btnAddToCart.visibility = View.GONE
                    btnRemoveFromCart.visibility = View.VISIBLE
                    clickListener.onAddToCartButtonClick(adapterPosition, restaurantFoodItem)
                }
            }

            btnRemoveFromCart.setOnClickListener {
                if (adapterPosition != -1) {
                    btnRemoveFromCart.visibility = View.GONE
                    btnAddToCart.visibility = View.VISIBLE
                    clickListener.onRemoveFromButtonClicked(adapterPosition, restaurantFoodItem)
                }
            }
        }
    }

    interface CartButtonListener {
        fun onAddToCartButtonClick(position: Int, foodItem: RestaurantFoodItem)
        fun onRemoveFromButtonClicked(position: Int, foodItem: RestaurantFoodItem)
    }
}