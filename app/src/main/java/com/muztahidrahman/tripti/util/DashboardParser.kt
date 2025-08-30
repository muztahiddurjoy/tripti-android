package com.muztahidrahman.tripti.util
import android.os.Build
import androidx.annotation.RequiresApi
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class FoodItem(
    val name: String,
    val description: String,
    val menuType: String,
    val ids: List<String>,
    val dates: List<LocalDate>,
    val ingredients: List<String>
)

data class TodayOrder(
    val mealName: String,
    val mealType: String,
    val orderDate: LocalDate,
    val status: String,
    val qrCodeId: String?
)

data class DayView(
    val date: LocalDate,
    val dayName: String,
    val meals: Map<String, List<FoodItem>>
)

class FoodScheduleParser {

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    fun parseHtml(html: String): ParsedData {
        val doc = Jsoup.parse(html)

        return ParsedData(
            todayOrders = parseTodayOrders(doc),
            foodItems = parseFoodItems(doc),
            dayViews = parseDayViews(doc)
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun parseTodayOrders(doc: Document): List<TodayOrder> {
        val orders = mutableListOf<TodayOrder>()

        // Find all order cards
        val orderCards = doc.select("div.bg-gradient-to-br.from-purple-600.via-indigo-600.to-blue-600")

        orderCards.forEach { card ->
            val statusElement = card.selectFirst("div.absolute.top-2.right-2")
            val status = statusElement?.text()?.trim() ?: "Unknown"

            val mealName = card.selectFirst("h2")?.text()?.trim() ?: ""
            val mealType = card.select("p:contains(Meal:) span.font-semibold").first()?.text()?.trim() ?: ""
            val orderDateText = card.select("p:contains(Order Date:) span.font-semibold").first()?.text()?.trim() ?: ""
            val orderDate = try {
                LocalDate.parse(orderDateText, dateFormatter)
            } catch (e: Exception) {
                LocalDate.now()
            }

            // Check if QR code button exists and extract the order ID
            val qrButton = card.select("button:contains(View QR Code)")
            val qrCodeId = if (qrButton.isNotEmpty()) {
                qrButton.attr("onclick").substringAfter("openQRCodeModal('").substringBefore("')")
            } else {
                null
            }

            orders.add(TodayOrder(mealName, mealType, orderDate, status, qrCodeId))
        }

        return orders
    }

    private fun parseFoodItems(doc: Document): List<FoodItem> {
        val foodItems = mutableListOf<FoodItem>()

        // Find all food item elements
        val foodItemElements = doc.select("li.food-item")

        foodItemElements.forEach { item ->
            val name = item.attr("data-name")
            val description = item.attr("data-description")
            val menuType = item.attr("data-menutype")
            val ids = item.attr("data-ids").split(",").map { it.trim() }.filter { it.isNotEmpty() }
            val dateStrings = item.attr("data-dates").split(",").map { it.trim() }.filter { it.isNotEmpty() }
            val dates = dateStrings.mapNotNull {
                try {
                    LocalDate.parse(it, dateFormatter)
                } catch (e: Exception) {
                    null
                }
            }

            // Extract ingredients from the span elements
            val ingredients = item.select("span.bg-gray-100").map { it.text().trim() }

            foodItems.add(FoodItem(name, description, menuType, ids, dates, ingredients))
        }

        return foodItems
    }

    private fun parseDayViews(doc: Document): List<DayView> {
        val dayViews = mutableListOf<DayView>()

        // Find all day containers
        val dayContainers = doc.select("div.day-container")

        dayContainers.forEach { container ->
            val header = container.selectFirst("h2")
            val headerText = header?.text() ?: ""

            // Extract date from header text (e.g., "Sunday, August 31st")
            val date = parseDateFromHeader(headerText)

            val dayName = headerText.substringBefore(",").trim()

            val meals = mutableMapOf<String, MutableList<FoodItem>>()

            // Find all meal sections
            val mealSections = container.select("div.bg-white.rounded-2xl.shadow")

            mealSections.forEach { section ->
                val mealTypeElement = section.selectFirst("h3")
                val mealType = mealTypeElement?.text()?.trim() ?: "Unknown"

                val mealItems = section.select("li.food-item-day").map { item ->
                    val name = item.attr("data-name")
                    val description = item.attr("data-description")
                    val menuType = item.attr("data-menutype")
                    val ids = item.attr("data-ids").split(",").map { it.trim() }.filter { it.isNotEmpty() }
                    val dateStrings = item.attr("data-dates").split(",").map { it.trim() }.filter { it.isNotEmpty() }
                    val dates = dateStrings.mapNotNull {
                        try {
                            LocalDate.parse(it, dateFormatter)
                        } catch (e: Exception) {
                            null
                        }
                    }

                    // Extract ingredients from the span elements
                    val ingredients = item.select("span.bg-gray-100").map { it.text().trim() }

                    FoodItem(name, description, menuType, ids, dates, ingredients)
                }

                meals[mealType] = mealItems.toMutableList()
            }

            dayViews.add(DayView(date, dayName, meals))
        }

        return dayViews
    }

    private fun parseDateFromHeader(headerText: String): LocalDate {
        // Try to extract date from header text like "Sunday, August 31st"
        val regex = """([A-Za-z]+),\s+([A-Za-z]+)\s+(\d+)(?:st|nd|rd|th)""".toRegex()
        val match = regex.find(headerText)

        if (match != null) {
            val (_, monthName, day) = match.destructured
            val year = LocalDate.now().year // Assuming current year

            val month = when (monthName.toLowerCase()) {
                "january" -> 1
                "february" -> 2
                "march" -> 3
                "april" -> 4
                "may" -> 5
                "june" -> 6
                "july" -> 7
                "august" -> 8
                "september" -> 9
                "october" -> 10
                "november" -> 11
                "december" -> 12
                else -> 1
            }

            return LocalDate.of(year, month, day.toInt())
        }

        return LocalDate.now()
    }

    fun extractQrCodeData(html: String): Map<String, String> {
        val qrData = mutableMapOf<String, String>()
        val doc = Jsoup.parse(html)

        // Find all QR code buttons
        val qrButtons = doc.select("button:contains(View QR Code)")

        qrButtons.forEach { button ->
            val onclick = button.attr("onclick")
            if (onclick.contains("openQRCodeModal")) {
                val qrId = onclick.substringAfter("openQRCodeModal('").substringBefore("')")
                val mealCard = button.closest("div.bg-gradient-to-br")
                val mealName = mealCard?.selectFirst("h2")?.text()?.trim() ?: "Unknown"
                val mealType = mealCard?.select("p:contains(Meal:) span.font-semibold")?.first()?.text()?.trim() ?: "Unknown"

                qrData[qrId] = "$mealName ($mealType)"
            }
        }

        return qrData
    }
}

data class ParsedData(
    val todayOrders: List<TodayOrder>,
    val foodItems: List<FoodItem>,
    val dayViews: List<DayView>
)
//
//// Usage example
//fun main() {
//    val parser = FoodScheduleParser()
//
//    // Parse the HTML content
//    val parsedData = parser.parseHtml(htmlContent)
//
//    // Extract QR code data
//    val qrData = parser.extractQrCodeData(htmlContent)
//
//    println("Today's Orders: ${parsedData.todayOrders.size}")
//    println("Available Food Items: ${parsedData.foodItems.size}")
//    println("Day Views: ${parsedData.dayViews.size}")
//    println("QR Codes: ${qrData.size}")
//
//    // Print QR code data
//    qrData.forEach { (id, description) ->
//        println("QR ID: $id - $description")
//    }
//}