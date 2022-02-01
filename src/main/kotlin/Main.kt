class Parking(spaces: Int) {
    private val spots = MutableList(spaces) { true } // true - empty spot
    val cars = MutableList(spaces) { Car("", "") }

    // returns indexes (starting at 1), lowest first
    fun getOccupied(): MutableList<Int> {
        val occ = mutableListOf<Int>()
        for (i in 0..spots.lastIndex)
            if (!spots[i]) occ.add(i + 1)
        return occ
    }

    fun getFree(): MutableList<Int> {
        val free = mutableListOf<Int>()
        for (i in 0..spots.lastIndex)
            if (spots[i]) free.add(i + 1)
        return free
    }

    // false is spot occupied
    fun addCar(spot: Int, car: Car): Boolean {
        if (!spots[spot - 1]) return false
        car.spot = spot
        cars[spot - 1] = car
        spots[spot - 1] = false
        return true
    }

    // false if already empty
    fun removeCar(spot: Int): Boolean {
        if (spots[spot - 1]) return false
        cars[spot - 1] = Car("", "")
        spots[spot - 1] = true
        return true
    }

    // returns spot numbers or registrations
    fun filterByColor(color: String, spot: Boolean = false): String {
        val colorCars = cars.filter { it.color.lowercase() == color.lowercase() }
        val colorSpots = mutableListOf<Int>()
        val colorRegs = mutableListOf<String>()

        if (spot)
            for (car in colorCars) colorSpots += car.spot
        else
            for (car in colorCars) colorRegs += car.registration

        return if (spot) colorSpots.joinToString(", ") else colorRegs.joinToString(", ")
    }

    // returns spot given reg (-1 if not found)
    fun findCar(registration: String): Int {
        val filtered = cars.filter { it.registration == registration }
        if (filtered.isEmpty()) return -1
        return filtered[0].spot
    }
}

data class Car(val registration: String, val color: String, var spot: Int = 0)

fun main() {
    var parking = Parking(0)
    var parkingExists = false

    println("To begin type create followed by a number of spots in your parking lot.")
    println("Type help to take a look at other commands.")
    while (true) {
        print("> ")
        val inp = readLine()!!.split(" ")

        if (inp[0] !in "create exit help" && !parkingExists) {
            println("Sorry, a parking lot has not been created.")
            continue
        }

        when (inp[0]) {
            "create" -> {
                parking = Parking(inp[1].toInt())
                parkingExists = true
                println("Created a parking lot with ${inp[1]} spots.")
            }

            "park" -> {
                val car = Car(inp[1], inp[2])
                if (parking.getFree().isEmpty()) {
                    println("Sorry, the parking lot is full.")
                } else {
                    val spot = parking.getFree()[0]
                    parking.addCar(spot, car)
                    println("${car.color} car parked in spot $spot.")
                }
            }

            "leave" -> {
                if (parking.removeCar(inp[1].toInt()))
                    println("Spot ${inp[1]} is free.")
                else
                    println("There is no car in spot ${inp[1]}.")
            }

            "status" -> {
                val occ = parking.getOccupied()
                var res = ""
                if (occ.isEmpty()) {
                    println("Parking lot is empty.")
                } else {
                    for (i in 0..parking.cars.lastIndex) {
                        if (i + 1 in occ)
                            res += "${i + 1} ${parking.cars[i].registration} ${parking.cars[i].color}\n"
                    }
                    print(res)
                }
            }

            "reg_by_color" -> {
                val filtered = parking.filterByColor(inp[1])
                if (filtered.isEmpty()) println("No cars with color ${inp[1]} were found.")
                else println(filtered)
            }

            "spot_by_color" -> {
                val filtered = parking.filterByColor(inp[1], spot = true)
                if (filtered.isEmpty()) println("No cars with color ${inp[1]} were found.")
                else println(filtered)
            }

            "spot_by_reg" -> {
                val spotted = parking.findCar(inp[1])
                if (spotted == -1) println("No cars with registration number ${inp[1]} were found.")
                else println(spotted)
            }

            "help" -> {
                println("create [spots] -> creates a new parking lot with a specified number of spots")
                println("park [registration] [color] -> parks a car with specified properties on a free spot with the lowest number")
                println("leave [spot] -> removes a car from a spot")
                println("status -> displays all parked cars")
                println("reg_by_color [color] -> displays the registration numbers of all cars with the specified color")
                println("spot_by_color [color] -> displays the spot numbers of cars with given colors")
                println("spot_by_reg [registration] -> displays the spot where the car with given registration number is parked")
                println("help -> this list")
                println("exit -> exit... (yeah what did you expect?)")
            }

            "exit" -> break

            else -> println("Unknown command")
        }
    }
}
