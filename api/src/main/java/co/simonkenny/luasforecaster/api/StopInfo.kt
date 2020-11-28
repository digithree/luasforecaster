package co.simonkenny.luasforecaster.api

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

const val DIRECTION_INBOUND = "Inbound"
const val DIRECTION_OUTBOUND = "Outbound"

@Xml(name = "stopInfo")
class StopInfo {

    // actually a standard date format but we don't need it
    @Attribute(name = "created")
    var created: String? = null

    @Attribute(name = "stop")
    var stop: String? = null

    @Attribute(name = "stopAbv")
    var stopAbv: String? = null

    @PropertyElement
    var message: String? = null

    @Element(name = "direction")
    var directions: List<Direction>? = null

    override fun toString(): String {
        return "StopInfo(created=$created, stop=$stop, stopAbv=$stopAbv, message=$message, directions=$directions)"
    }
}

@Xml(name = "direction")
class Direction {
    @Attribute(name = "name")
    var name: String? = null

    @Element
    var trams: List<Tram>? = null

    override fun toString(): String {
        return "Direction(name=$name, trams=$trams)"
    }
}

@Xml(name = "tram")
class Tram {
    @Attribute(name = "destination")
    var destination: String? = null

    // dueMins is an Int but can be blank, so we use String
    @Attribute(name = "dueMins")
    var dueMins: String? = null

    override fun toString(): String {
        return "Tram(destination=$destination, dueMins=$dueMins)"
    }
}
