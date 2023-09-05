import com.github.nscala_time.time.Imports._
import org.joda.time.Months

import scala.util.Random

case class Product(creationDate: DateTime,
                   name: String = "",
                   category: String = "",
                   weight: Double = 0,
                   price: Double = 0)

case class Item(product: Product,
                cost: Double = 0,
                shippingFee: Double = 0,
                taxAmount: Double = 0)

case class Order(items: Seq[Item],
                 creationDate: DateTime,
                 customerName: String = "",
                 contact: String = "",
                 shippingAddress: String = "",
                 grandTotal: Double = 0)

object Main {
  val data: Seq[Order] = {
    val rand: Random = new scala.util.Random()
    //Generate 10000 products
    val products: Seq[Product] = {
      (1 to 10000)
        .map(_ => rand.nextInt(365 * 2))
        .map(daysAgo => Product(DateTime.now() - daysAgo.days)) // Generate products created in last two years
    }

    //Generate 100 items for each order
    def genItemsList(): Seq[Item] = {
      (1 to 100)
        .map(i => Item(products(rand.nextInt(10000))))
    }
    //Generate 10000 orders
    (1 to 10000)
      .map(_ => rand.nextInt(365 * 2))
      .map(daysAgo => Order(genItemsList(), DateTime.now() - daysAgo.days)) // Generate orders for last two years
  }

  //Example arguments "2021-01-01 00:00:00" "2024-01-01 00:00:00" "1-3" "1-6" "4-6" ">12" ">1" ">0" ">=0"
  def main(args: Array[String]): Unit = {
    val inputFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")
    val dateRange = args.take(2).map(date => DateTime.parse(date, inputFormatter))

    val ordersInRange = data.filter(order => order.creationDate >= dateRange(0) && order.creationDate <= dateRange(1))
    val productsPurchasedInRange = ordersInRange.flatMap(order => order.items.map(item => item.product))
    val now = DateTime.now

    val productsAgeWithCorrespondingOrdersNumber = productsPurchasedInRange
      .map(product => Months.monthsBetween(product.creationDate, now).getMonths)
      .groupBy(months => months)
      .toList
      .sortBy(_._1)

    val intervals = args.drop(2)

    intervals.foreach {
      case s if s.matches("""\d+-\d+""") =>
        val Array(min, max) = s.split("-").map(_.toInt)
        val number = productsAgeWithCorrespondingOrdersNumber.filter(o => o._1 >= min && o._1 <= max).map(_._2.size).sum
        println(s"$s months: $number orders")
      case s if s.matches(""">\d+""") =>
        val number = productsAgeWithCorrespondingOrdersNumber.filter(o => o._1 > s.substring(1).toInt).map(_._2.size).sum
        println(s"$s months: $number orders")
      case s if s.matches(""">=\d+""") =>
        val number = productsAgeWithCorrespondingOrdersNumber.filter(o => o._1 >= s.substring(2).toInt).map(_._2.size).sum
        println(s"$s months: $number orders")
      case s =>
        throw new IllegalArgumentException(s"Invalid string: $s")
    }
  }
}
