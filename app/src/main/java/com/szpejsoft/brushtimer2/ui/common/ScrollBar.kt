package com.szpejsoft.brushtimer2.ui.common

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun BoxScope.GesturedScrollBar(
    state: ScrollState,
    direction: Direction,
    width: Dp = ScrollViewDefaults.scrollBarWidth,
    minLength: Dp = ScrollViewDefaults.scrollBarMinLength,
    color: Color = ScrollViewDefaults.scrollBarColor
) {
    val coroutineScope = rememberCoroutineScope()
    var isDraggingScrollbar by remember { mutableStateOf(false) }

    val alpha by animateFloatAsState(
        targetValue = if (state.isScrollInProgress || isDraggingScrollbar) 1f else 0f,
        animationSpec = tween(400, delayMillis = if (state.isScrollInProgress || isDraggingScrollbar) 0 else 700),
        label = "ScrollBarAlphaAnimation"
    )

    val viewSize = state.viewportSize.toFloat().takeIf { it > 0 } ?: return
    val contentSize = state.maxValue + viewSize
    if (contentSize <= viewSize) return

    val scrollbarSize = with(LocalDensity.current) {
        (viewSize * (viewSize / contentSize)).coerceIn(minLength.toPx()..viewSize)
    }
    val variableZone = viewSize - scrollbarSize

    val scrollOffset = (state.value.toFloat() / state.maxValue) * variableZone

    val isVertical = direction == Direction.VERTICAL
    val modifier = if (isVertical) {
        Modifier
            .fillMaxHeight()
            .width(width)
            .align(Alignment.CenterEnd)
    } else {
        Modifier
            .fillMaxWidth()
            .height(width)
            .align(Alignment.BottomCenter)
    }
    Box(
        modifier = modifier
            .pointerInput(state) {
                val onStart: (Offset) -> Unit = { isDraggingScrollbar = true }
                val onDrag: (PointerInputChange, Float) -> Unit = { _, dragAmount ->
                    val deltaScroll = (dragAmount / variableZone) * state.maxValue.toFloat()
                    coroutineScope.launch {
                        state.scrollTo((state.value + deltaScroll).coerceIn(0f, state.maxValue.toFloat()).toInt())
                    }
                }
                val onEnd = { isDraggingScrollbar = false }
                if (isVertical) {
                    detectVerticalDragGestures(
                        onDragStart = onStart,
                        onVerticalDrag = onDrag,
                        onDragEnd = onEnd,
                        onDragCancel = onEnd
                    )
                } else {
                    detectHorizontalDragGestures(
                        onDragStart = onStart,
                        onHorizontalDrag = onDrag,
                        onDragEnd = onEnd,
                        onDragCancel = onEnd
                    )
                }
            }
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            if (isVertical) {
                drawRoundRect(
                    topLeft = Offset(size.width - width.toPx(), scrollOffset),
                    size = Size(width.toPx(), scrollbarSize),
                    cornerRadius = CornerRadius(width.toPx() / 2),
                    color = color,
                    alpha = alpha
                )
            } else {
                drawRoundRect(
                    topLeft = Offset(scrollOffset, size.height - width.toPx()),
                    size = Size(scrollbarSize, width.toPx()),
                    cornerRadius = CornerRadius(width.toPx() / 2),
                    color = color,
                    alpha = alpha
                )
            }
        }
    }
}

enum class Direction {
    VERTICAL, HORIZONTAL
}

object ScrollViewDefaults {
    val scrollBarWidth = 6.dp
    val scrollBarMinLength = 24.dp
    val scrollBarColor = Color.LightGray
}