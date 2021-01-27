package com.cuupa.kchart

import java.awt.Color

data class DataPoint(var name: String, var value: Float, var customColor: Color?) {
    constructor(name: String, value: Float) : this(name, value, null)
}