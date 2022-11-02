package com.jey.kahauto

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.jey.kahauto.model.SellersList
import kotlinx.android.synthetic.main.card_item.view.*

class SellersListAdapter (
    context: Context,
    sellerListArrayList: List<SellersList>,
    val onSellerListClick: (sellerList:SellersList) -> Unit,
    ) :
    ArrayAdapter<SellersList>(context, 0, sellerListArrayList)
    {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var listItemView = convertView
            if (listItemView == null) {
                listItemView =
                    LayoutInflater.from(context).inflate(R.layout.card_item, parent, false)
            }

            val sellersList: SellersList? = getItem(position)
            listItemView!!.tv_card.text = sellersList?.listTitle
            listItemView.setOnClickListener {
                onSellerListClick(sellersList!!)
            }
            return listItemView
        }
    }