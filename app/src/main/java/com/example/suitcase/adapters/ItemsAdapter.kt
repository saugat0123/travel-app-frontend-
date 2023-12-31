package com.example.suitcase.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.suitcase.R

import com.example.rblibrary.entity.Item
import com.example.rblibrary.api.ServiceBuilder
import com.example.rblibrary.entity.Cart
import com.example.rblibrary.repository.CartRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ItemsAdapter(private val lstItems: ArrayList<Item>,
                   val context: Context)
    :RecyclerView.Adapter<ItemsAdapter.ItemViewHolder>()
{

    class ItemViewHolder(view: View): RecyclerView.ViewHolder(view){
        val itemName: TextView = view.findViewById(R.id.itemName)
        val itemPrice: TextView = view.findViewById(R.id.itemPrice)
        val itemImg: ImageView = view.findViewById(R.id.itemImg)
        val addToCart: ImageView = view.findViewById(R.id.addToCart)
        val like: ImageView = view.findViewById(R.id.like)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view =LayoutInflater.from(parent.context).inflate(R.layout.items,parent,false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = lstItems[position]
        holder.itemName.text = item.itemName
        holder.itemPrice.text = item.itemPrice.toString()


        val imagePath = ServiceBuilder.loadImagePath() + item.photo
        if (!item.photo.equals("no-photo.jpg")) {
            Glide.with(context)
                    .load(imagePath)
                    .fitCenter()
                    .into(holder.itemImg)
        }

        holder.like.setOnClickListener {
            val name = item.itemName
            val price = item.itemPrice.toString()
            val pic = item.photo
            val latitude = item.latitude
            val longitude = item.longitude
            val des = item.itemDes

            val items = Item(itemName = name, itemPrice = price.toInt(),photo = pic, latitude = latitude, longitude = longitude, itemDes = des)
            val intent = Intent(context, DetailsActivity::class.java)
            intent.putExtra("ITEM_DETAILS", items)
            context.startActivity(intent)
        }

        holder.addToCart.setOnClickListener {
            val name = item.itemName
            val price = item.itemPrice.toString()
            val pic = item.photo

            val carts = Cart(itemName = name,photo = pic,itemPrice = price)

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val cartRepo = CartRepo()
                    val response = cartRepo.addItemToCart(carts)
                    if(response.success == true){

                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                    context,
                                    "$name Added to Cart", Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } catch (ex: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                                context,
                                ex.toString(), Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }

    }

    override fun getItemCount(): Int {
        return lstItems.size
    }
}