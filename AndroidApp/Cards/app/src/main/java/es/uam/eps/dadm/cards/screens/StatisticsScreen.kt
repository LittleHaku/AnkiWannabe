package es.uam.eps.dadm.cards.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.barchart.BarChart
import co.yml.charts.ui.barchart.models.BarChartData
import co.yml.charts.ui.barchart.models.BarData
import co.yml.charts.ui.barchart.models.BarStyle
import es.uam.eps.dadm.cards.CardViewModel
import es.uam.eps.dadm.cards.R
import java.time.LocalDate

@Composable
fun StatisticsScreen(viewModel: CardViewModel) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Statistics(viewModel = viewModel)
    }

}

@Composable
fun Statistics(viewModel: CardViewModel) {
    //val reviews by viewModel.reviews.observeAsState(listOf())
    val reviews by viewModel.getUserReviews(viewModel.userId).observeAsState(listOf())
    val barData = mutableListOf<BarData>()


    // Print the content of the reviews list
    /*println("Content of reviews list:")
    reviews.forEach { review ->
        println(review) // Or display it as needed
    }*/

    reviews.let { reviewList ->
        val reviewsMap = viewModel.fromReviewsToMap(reviewList)

        val sortedReviewsMap = reviewsMap.toList()
            .sortedBy { (key, _) -> LocalDate.parse(key) }
            .toMap()

        sortedReviewsMap.entries.forEachIndexed { index, entry ->
            val date = entry.key
            val numberOfReviews = entry.value

            // Create a BarData point with x as the index and y as the number of reviews
            val point = BarData(
                point = Point(x = index.toFloat(), y = numberOfReviews.toFloat()),
//                point = Point(x = 0.0F, y = 3.2F),
                label = date,
//                label = "AAA",
                color = MaterialTheme.colorScheme.primary
            )

            barData.add(point)
        }
        if (barData.isNotEmpty()) {
            BarchartWithSolidBars(barData)
        } else {
            Text(
                text = stringResource(id = R.string.not_enough_data),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(50.dp)
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally)
            )


        }

    }

}

@Composable
private fun BarchartWithSolidBars(barData: List<BarData>) {

    // Get the highest value
    val maxValue = (barData.maxBy { p -> p.point.y }.point.y).toInt()
    val maxRange = when {
        maxValue <= 10 -> 10
        maxValue <= 25 -> 25
        maxValue <= 50 -> 50
        maxValue <= 100 -> 100
        maxValue <= 250 -> 250
        maxValue <= 500 -> 500
        else -> 1000
        // If someone does more than 1000 reviews a day create an issue on github and ill change it
    }


    val xAxisData =
        AxisData.Builder().axisStepSize(30.dp).steps(barData.size - 1).bottomPadding(40.dp)
            .axisLabelAngle(20f).axisLabelColor(MaterialTheme.colorScheme.onBackground)
            .axisLineColor(MaterialTheme.colorScheme.secondary).startDrawPadding(48.dp)
            .labelData { index -> barData[index].label }.build()
    val yAxisData = AxisData.Builder().steps(maxRange).labelAndAxisLinePadding(20.dp)
        .axisLabelColor(MaterialTheme.colorScheme.onBackground)
        .axisLineColor(MaterialTheme.colorScheme.secondary).axisOffset(20.dp)
        .labelData { index -> (index * (maxRange / maxRange)).toString() }.build()
    val barChartData = BarChartData(
        chartData = barData,
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        barStyle = BarStyle(
            paddingBetweenBars = 20.dp, barWidth = 25.dp
        ),
        showYAxis = true,
        showXAxis = true,
        horizontalExtraSpace = 10.dp,
        backgroundColor = Color.Transparent,
    )
    BarChart(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(), barChartData = barChartData
    )
}
