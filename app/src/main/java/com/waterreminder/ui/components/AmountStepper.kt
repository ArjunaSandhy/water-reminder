package com.waterreminder.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun AmountStepper(
    amount: Int,
    onAmountChange: (Int) -> Unit,
    minAmount: Int = 50,
    maxAmount: Int = 2000,
    step: Int = 50,
    modifier: Modifier = Modifier,
    displayUnit: String = "ml"
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedIconButton(
            onClick = {
                val newAmount = (amount - step).coerceAtLeast(minAmount)
                onAmountChange(newAmount)
            },
            modifier = Modifier.size(48.dp),
            enabled = amount > minAmount,
            shape = CircleShape,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = "Kurangi",
                tint = MaterialTheme.colorScheme.primary
            )
        }
        
        Text(
            text = "$amount $displayUnit",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 32.dp),
            color = MaterialTheme.colorScheme.onBackground
        )
        
        OutlinedIconButton(
            onClick = {
                val newAmount = (amount + step).coerceAtMost(maxAmount)
                onAmountChange(newAmount)
            },
            modifier = Modifier.size(48.dp),
            enabled = amount < maxAmount,
            shape = CircleShape,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Tambah",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}
