package com.rideaware.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.rideaware.domain.model.SignalStrength
import com.rideaware.ui.theme.SignalAmber
import com.rideaware.ui.theme.SignalGreen
import com.rideaware.ui.theme.SignalMuted

@Composable
fun SignalBadge(
    signalStrength: SignalStrength,
    rssi: Int,
    modifier: Modifier = Modifier
) {
    val (dotColor, backgroundColor) = when (signalStrength) {
        SignalStrength.GOOD -> SignalGreen to SignalGreen.copy(alpha = 0.12f)
        SignalStrength.FAIR -> SignalAmber to SignalAmber.copy(alpha = 0.12f)
        SignalStrength.WEAK -> SignalMuted to SignalMuted.copy(alpha = 0.12f)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "${signalStrength.label} ($rssi dBm)",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
