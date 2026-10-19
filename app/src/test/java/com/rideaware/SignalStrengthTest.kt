package com.rideaware

import com.rideaware.domain.model.SignalStrength
import org.junit.Assert.assertEquals
import org.junit.Test

class SignalStrengthTest {

    @Test
    fun fromRssi_strongSignal_returnsGood() {
        assertEquals(SignalStrength.GOOD, SignalStrength.fromRssi(-45))
        assertEquals(SignalStrength.GOOD, SignalStrength.fromRssi(-65))
    }

    @Test
    fun fromRssi_moderateSignal_returnsFair() {
        assertEquals(SignalStrength.FAIR, SignalStrength.fromRssi(-66))
        assertEquals(SignalStrength.FAIR, SignalStrength.fromRssi(-75))
        assertEquals(SignalStrength.FAIR, SignalStrength.fromRssi(-85))
    }

    @Test
    fun fromRssi_weakSignal_returnsWeak() {
        assertEquals(SignalStrength.WEAK, SignalStrength.fromRssi(-86))
        assertEquals(SignalStrength.WEAK, SignalStrength.fromRssi(-98))
        assertEquals(SignalStrength.WEAK, SignalStrength.fromRssi(-110))
    }
}
