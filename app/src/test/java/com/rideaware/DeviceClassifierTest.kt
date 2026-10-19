package com.rideaware

import com.rideaware.domain.classifier.DeviceClassifier
import com.rideaware.domain.model.DeviceCategory
import org.junit.Assert.assertEquals
import org.junit.Test

class DeviceClassifierTest {

    @Test
    fun classify_audioServiceUuid_returnsPossibleAudioIndicator() {
        val a2dpSinkUuid = listOf("0000110b-0000-1000-8000-00805f9b34fb")
        val category = DeviceClassifier.classify(a2dpSinkUuid, null)
        assertEquals(DeviceCategory.POSSIBLE_AUDIO_INDICATOR, category)
    }

    @Test
    fun classify_leAudioUuid_returnsPossibleAudioIndicator() {
        val leAudioUuid = listOf("0000184e-0000-1000-8000-00805f9b34fb")
        val category = DeviceClassifier.classify(leAudioUuid, "Peripheral")
        assertEquals(DeviceCategory.POSSIBLE_AUDIO_INDICATOR, category)
    }

    @Test
    fun classify_audioKeywordInName_returnsPossibleAudioIndicator() {
        val category = DeviceClassifier.classify(emptyList(), "Sony WH-1000XM4")
        assertEquals(DeviceCategory.POSSIBLE_AUDIO_INDICATOR, category)
    }

    @Test
    fun classify_airpodsKeyword_returnsPossibleAudioIndicator() {
        val category = DeviceClassifier.classify(emptyList(), "Alex's AirPods Pro")
        assertEquals(DeviceCategory.POSSIBLE_AUDIO_INDICATOR, category)
    }

    @Test
    fun classify_heartRateUuid_returnsOtherKnownCategory() {
        val heartRateUuid = listOf("0000180d-0000-1000-8000-00805f9b34fb")
        val category = DeviceClassifier.classify(heartRateUuid, "Fitness Tracker")
        assertEquals(DeviceCategory.OTHER_KNOWN_CATEGORY, category)
    }

    @Test
    fun classify_unrecognizedUuidAndGenericName_returnsUnknown() {
        val genericUuid = listOf("12345678-1234-5678-1234-56789abcdef0")
        val category = DeviceClassifier.classify(genericUuid, "Smart Tag")
        assertEquals(DeviceCategory.UNKNOWN, category)
    }

    @Test
    fun classify_emptyInputs_returnsUnknown() {
        val category = DeviceClassifier.classify(emptyList(), null)
        assertEquals(DeviceCategory.UNKNOWN, category)
    }
}
