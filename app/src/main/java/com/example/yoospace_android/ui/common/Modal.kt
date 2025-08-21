package com.example.yoospace_android.ui.common

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.yoospace_android.ui.theme.LocalExtraColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Modal(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit

) {
    // This is a placeholder for the actual modal implementation.
    // You can use a Dialog, BottomSheet, or any other modal component as needed.
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = LocalExtraColors.current.cardBackground,
        modifier = modifier

    ) { content()}

}