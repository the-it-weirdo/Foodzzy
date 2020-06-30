package dev.debaleen.foodrunner.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.debaleen.foodrunner.R
import dev.debaleen.foodrunner.model.RestaurantFoodItemUIModel
import dev.debaleen.foodrunner.util.hide
import dev.debaleen.foodrunner.util.show

class RestaurantDetailAdapter(
    private var foodItemList: ArrayList<RestaurantFoodItemUIModel>,
    private val clickListener: CartButtonListener
) :
    RecyclerView.Adapter<RestaurantDetailAdapter.RestaurantDetailViewHolder>() {

    fun updateDataList(list: ArrayList<RestaurantFoodItemUIModel>) {
        foodItemList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantDetailViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.food_item_ui, parent, false)

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

        fun bind(
            restaurantFoodItemUIItem: RestaurantFoodItemUIModel,
            clickListener: CartButtonListener
        ) {
            txtSerial.text = (adapterPosition + 1).toString()
            txtFoodName.text = restaurantFoodItemUIItem.name
            txtCostForOne.text = restaurantFoodItemUIItem.cost

            if (restaurantFoodItemUIItem.isInCart) {
                btnRemoveFromCart.show()
                btnAddToCart.hide()
            } else {
                btnAddToCart.show()
                btnRemoveFromCart.hide()
            }

            btnAddToCart.setOnClickListener {
                if (adapterPosition != -1) {
                    btnAddToCart.hide()
                    btnRemoveFromCart.show()
                    clickListener.onAddToCartButtonClick(adapterPosition, restaurantFoodItemUIItem)
                }
            }

            btnRemoveFromCart.setOnClickListener {
                if (adapterPosition != -1) {
                    btnRemoveFromCart.hide()
                    btnAddToCart.show()
                    clickListener.onRemoveFromButtonClicked(
                        adapterPosition,
                        restaurantFoodItemUIItem
                    )
                }
            }
        }
    }

    interface CartButtonListener {
        fun onAddToCartButtonClick(position: Int, foodItem: RestaurantFoodItemUIModel)
        fun onRemoveFromButtonClicked(position: Int, foodItem: RestaurantFoodItemUIModel)
    }
}