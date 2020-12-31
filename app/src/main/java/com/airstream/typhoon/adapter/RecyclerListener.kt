package com.airstream.typhoon.adapter

class RecyclerListener {
    interface OnBottomReachedListener {
        fun onBottomReached(position: Int)
    }

    interface OnCheckChangedListener {
        fun onCheckChanged(categoryId: Int, isChecked: Boolean)
    }
}