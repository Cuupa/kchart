package com.cuupa.kchart

import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.util.stream.IntStream

abstract class Chart(protected val dataPoints: List<DataPoint>) {

    abstract fun getImage(): BufferedImage

    abstract fun getImage(width: Int, height: Int): BufferedImage

    fun getColors(numberOfParts: Int): List<Color> {
        val list = mutableListOf<Color>()
        IntStream.range(0, numberOfParts).forEach {
            list.add(colors[it])
        }
        return list
    }

    abstract fun createLegend(wholeImage: Graphics2D, positionX: Int, positionY: Int, diameter: Int, width: Int)

    protected fun getColor(it: Int, colors: List<Color>) =
        if (dataPoints[it].customColor == null) {
            colors[it]
        } else {
            dataPoints[it].customColor
        }

    abstract fun getImage(width: Int, height: Int, drawWithLegend: Boolean): BufferedImage

    protected fun scaleFontSize(width: Int) = (width / 100) * 2 + 16

    protected var colors = listOf(
        Color(48, 124, 232),
        Color(151, 189, 243),
        Color(232, 109, 48),
        Color(230, 25, 110),
        Color(230, 178, 25),
        Color(240, 117, 168)
    )

    fun setCustomColors(colors: List<Color>) {
        this.colors = colors
    }

    protected val textColor = Color(69, 76, 82)

}