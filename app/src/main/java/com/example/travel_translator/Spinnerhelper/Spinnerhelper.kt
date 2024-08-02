package com.example.travel_translator.Spinnerhelper

import android.widget.ArrayAdapter
import android.widget.Spinner

class Spinnerhelper {
    fun <T> swapSelectedItems(spinner1: Spinner, spinner2: Spinner) {
        // Ensure both spinners have the same type of items and are ArrayAdapters
        val adapter1 = spinner1.adapter as? ArrayAdapter<T>
            ?: throw IllegalArgumentException("Spinner1 does not use an ArrayAdapter of type T")
        val adapter2 = spinner2.adapter as? ArrayAdapter<T>
            ?: throw IllegalArgumentException("Spinner2 does not use an ArrayAdapter of type T")
        val selectedItem1 = spinner1.selectedItem as? T
            ?: throw IllegalStateException("Selected item in spinner1 is not of type T")
        val selectedItem2 = spinner2.selectedItem as? T
            ?: throw IllegalStateException("Selected item in spinner2 is not of type T")
        val position1 = adapter2.getPosition(selectedItem2)
        val position2 = adapter1.getPosition(selectedItem1)
        if (position1 != -1 && position2 != -1) {
            spinner1.setSelection(position1)
            spinner2.setSelection(position2)
        } else {
            println("One or both items are not present in the spinners' adapters")
        }
    }
}