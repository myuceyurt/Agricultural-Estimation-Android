package com.agrowise.app.utils

import LocationItem
import java.util.Locale

fun String.normalizeForSearch(): String =
    this.lowercase(Locale("tr", "TR"))
        .replace('ç', 'c')
        .replace('ğ', 'g')
        .replace('ı', 'i')
        .replace('ö', 'o')
        .replace('ş', 's')
        .replace('ü', 'u')

fun LocationItem.matches(query: String): Boolean {
    val q = query.normalizeForSearch()
    val d = district.normalizeForSearch()
    val c = city.normalizeForSearch()
    val full = "$district $city".normalizeForSearch()
    return d.contains(q) || c.contains(q) || full.contains(q)
}