package dev.debaleen.foodrunner.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import dev.debaleen.foodrunner.R
import dev.debaleen.foodrunner.model.RestaurantUIModel
import jp.wasabeef.picasso.transformations.CropSquareTransformation
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation


class RestaurantAdapter(
    private val itemList: ArrayList<RestaurantUIModel>,
    private val clickListener: RestaurantClickListener
) :
    RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.restaurant_item_layout, parent, false)

        return RestaurantViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        val restaurant = itemList[position]
        holder.bind(restaurant, clickListener)
    }

    class RestaurantViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val txtRestaurantName: TextView = view.findViewById(R.id.txtRestaurantName)
        private val txtCostForOne: TextView = view.findViewById(R.id.txtCostForOne)
        private val txtRating: TextView = view.findViewById(R.id.txtRating)
        private val imgRestaurantImage: ImageView = view.findViewById(R.id.imgRestaurantImage)
        private val btnFavouriteButton: ImageButton = view.findViewById(R.id.btnFavourite)
        private val llContent: RelativeLayout = view.findViewById(R.id.llContent)

        fun bind(restaurantUIModel: RestaurantUIModel, clickListener: RestaurantClickListener) {
            txtRestaurantName.text = restaurantUIModel.resName
            val costString = "${restaurantUIModel.resCostForOne}/person"
            txtCostForOne.text = costString
            txtRating.text = restaurantUIModel.resRating
            Picasso.get()
                .load(restaurantUIModel.resImageUrl)
                .error(R.mipmap.ic_launcher)
                .transform(CropSquareTransformation())
                .transform(RoundedCornersTransformation(15, 0))
                .into(imgRestaurantImage)

            if (restaurantUIModel.isFavourite) {
                btnFavouriteButton.setImageResource(R.drawable.ic_favourite_on)
            } else {
                btnFavouriteButton.setImageResource(R.drawable.ic_favourite_off)
            }

            llContent.setOnClickListener {
                if (adapterPosition != -1) {
                    clickListener.onRestaurantClick(adapterPosition, restaurantUIModel.resId)
                }
            }

            btnFavouriteButton.setOnClickListener {
                if (adapterPosition != -1) {
                    clickListener.onFavouriteClick(adapterPosition, restaurantUIModel.resId)
                }
            }
        }

    }

    interface RestaurantClickListener {
        fun onRestaurantClick(position: Int, resId: String)
        fun onFavouriteClick(position: Int, resId: String)
    }

}