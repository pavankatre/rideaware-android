package com.rideaware.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rideaware.ui.theme.TealSecondary
import com.rideaware.ui.theme.WarningAmber

@Composable
fun SafetyScreen(
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Road Safety & Digital Awareness",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Safe practices for two-wheeler operators and understanding wireless signals on the road.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )

        Spacer(modifier = Modifier.height(20.dp))

        SafetyPrincipleCard(
            icon = Icons.Default.LocalParking,
            iconTint = MaterialTheme.colorScheme.primary,
            title = "1. Park Safely Before Operating Your Phone",
            body = "Never glance at, touch, or configure your mobile device while operating a motorcycle, scooter, or bicycle. Pull completely off the moving lane into a designated, secure parking area first."
        )

        Spacer(modifier = Modifier.height(12.dp))

        SafetyPrincipleCard(
            icon = Icons.Default.Block,
            iconTint = WarningAmber,
            title = "2. Zero Road Confrontation",
            body = "Do not confront other road users, drivers, or riders based on observed Bluetooth signals. Radio packets travel freely across lanes and vehicles; a detected signal never proves who owns it or whether someone is distracted."
        )

        Spacer(modifier = Modifier.height(12.dp))

        SafetyPrincipleCard(
            icon = Icons.Default.Psychology,
            iconTint = TealSecondary,
            title = "3. Auditory Awareness Matters",
            body = "Hearing traffic cues (horns, approaching emergency sirens, screeching tires, engine shifts) is critical to two-wheeler survival. Using sound-isolating headphones or high-volume audio significantly degrades hazard response time."
        )

        Spacer(modifier = Modifier.height(12.dp))

        SafetyPrincipleCard(
            icon = Icons.Default.Gavel,
            iconTint = MaterialTheme.colorScheme.primary,
            title = "4. Legal & Regulatory Compliance",
            body = "Motor vehicle acts across jurisdictions strictly penalize the hand-held operation of mobile devices and certain earphone usage while riding. Always respect local traffic laws and prioritized safety guidelines."
        )

        Spacer(modifier = Modifier.height(12.dp))

        SafetyPrincipleCard(
            icon = Icons.Default.Shield,
            iconTint = TealSecondary,
            title = "5. BLE is Not an Enforcement Radar",
            body = "Bluetooth Low Energy signals reflect off concrete, absorb into human bodies, and broadcast intermittently. RideAware is an educational research tool, not an evidence collector or enforcement radar."
        )

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
private fun SafetyPrincipleCard(
    icon: ImageVector,
    iconTint: androidx.compose.ui.graphics.Color,
    title: String,
    body: String
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
