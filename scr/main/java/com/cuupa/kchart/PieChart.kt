package com.cuupa.kchart

import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.geom.Arc2D
import java.awt.image.BufferedImage
import java.util.stream.IntStream
import kotlin.math.roundToInt

class PieChart(dataPoints: List<DataPoint>) : Chart(dataPoints) {

    override fun getImage(): BufferedImage {
        val width = 250
        val height = 250
        return getImage(width, height, true)
    }

    override fun getImage(width: Int, height: Int): BufferedImage {
        return getImage(width, height, true)
    }

    override fun getImage(width: Int, height: Int, drawWithLegend: Boolean): BufferedImage {
        // what is this? a chart for ants?
        if (width < 2 || height < 2) {
            throw IllegalArgumentException("Invalid width or height")
        }

        val bufferedImage = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        val wholeImage = bufferedImage.createGraphics()

        wholeImage.color = Color.white
        wholeImage.fillRect(0, 0, width, height)
        val diameter = getDiameter(drawWithLegend, width)
        val radius = diameter / 2
        val positionX = getPositionX(drawWithLegend, width, diameter, radius)
        val positionY = (height.toFloat() / 2).roundToInt() - radius

        val diagramparts = computeDiagram(positionX, positionY, diameter)
        val colors = getColors(diagramparts.size)

        IntStream.range(0, diagramparts.size).forEach {
            wholeImage.color = getColor(it, colors)
            wholeImage.fill(diagramparts[it])
        }

        if (drawWithLegend) {
            createLegend(wholeImage, positionX, positionY, diameter, width)
        }
        // Disposes of this graphics context and releases any system resources that it is using.
        wholeImage.dispose()
        return bufferedImage
    }

    private fun getPositionX(
        drawWithLegend: Boolean,
        width: Int,
        diameter: Int,
        radius: Int
    ) = if (drawWithLegend) {
        (((width.toFloat() - diameter.toFloat()) / 100) * 20).toInt()
    } else {
        diameter - radius
    }

    private fun getDiameter(drawWithLegend: Boolean, width: Int) = if (drawWithLegend) {
        (width.toFloat() / 1.6f).roundToInt()
    } else {
        (width.toFloat() / 1.2f).roundToInt()
    }

    override fun createLegend(wholeImage: Graphics2D, positionX: Int, positionY: Int, diameter: Int, width: Int) {
        val positionXLegend = positionX + diameter + (((positionX.toFloat() / diameter.toFloat()) * 100) * 20).toInt()
        val fontSize = scaleFontSize(width)
        wholeImage.font = Font("TimesRoman", Font.BOLD, fontSize)
        var positionYLegend = positionY

        IntStream.range(0, dataPoints.size).forEach {
            val dataPointName = dataPoints[it].name
            wholeImage.color = colors[it]
            wholeImage.fillRect(
                positionXLegend - determineLegendColorPlacement(fontSize) - fontSize,
                positionYLegend - determineLegendColorPlacement(fontSize),
                fontSize,
                fontSize
            )
            wholeImage.color = textColor
            wholeImage.drawString(dataPointName, positionXLegend, positionYLegend)
            positionYLegend += 20 + wholeImage.font.size
        }
    }

    private fun determineLegendColorPlacement(fontSize: Int) = (fontSize.toFloat() / 1.2f).toInt()

    private fun computeDiagram(positionX: Int, positionY: Int, diameter: Int): List<Arc2D> {
        if (dataPoints.isEmpty()) {
            throw java.lang.IllegalArgumentException("No data")
        }

        var totalPoints = 0f
        dataPoints.forEach { totalPoints += it.value }
        if (totalPoints == 0f) {
            listOf<Arc2D>()
        }
        val degrees = dataPoints.map {
            it.value / totalPoints * 360
        }
        var startingPoint = 0f
        val arcs = mutableListOf<Arc2D>()
        degrees.forEach {
            arcs.add(
                Arc2D.Float(
                    positionX.toFloat(),
                    positionY.toFloat(),
                    diameter.toFloat(),
                    diameter.toFloat(),
                    startingPoint,
                    it,
                    Arc2D.PIE
                )
            )
            startingPoint += it
        }
        return arcs.toList()
    }
}