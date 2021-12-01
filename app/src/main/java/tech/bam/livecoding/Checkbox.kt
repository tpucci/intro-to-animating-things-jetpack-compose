package tech.bam.livecoding

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.scan

@Composable
@Preview(widthDp = 720, heightDp = 360)
fun PreviewCheckbox() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Checkbox(modifier = Modifier.scale(5f)) {
            Text(text = "KKoders 21")
        }
    }
}

@Composable
fun Checkbox(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    ClickHandler(modifier) { clicks ->
        val isChecked by
        remember {
            clicks.scan(initial = true) { isChecked, _ -> !isChecked }
        }
            .collectAsState(initial = true)


        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Stars(enabled = isChecked) {
                CheckboxFeedback(isChecked = isChecked)
            }
            content()
        }
    }
}

@Composable
fun ClickHandler(modifier: Modifier = Modifier, content: @Composable (clicks: Flow<Unit>) -> Unit) {
    val clicks = remember { MutableSharedFlow<Unit>() }
    val isPressed = remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed.value) .9f else 1f,
        animationSpec = tween(100, easing = LinearEasing)
    )

    Box(modifier = modifier
        .pointerInput(null) {
            detectTapGestures(
                onPress = {
                    isPressed.value = true
                    val released = this.tryAwaitRelease()
                    isPressed.value = false
                    if (released) clicks.emit(Unit)
                }
            )
        }
        .scale(scale)
    ) {
        content(clicks)
    }
}

@Composable
fun CheckboxFeedback(isChecked: Boolean) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.check))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        speed = if (isChecked) 2f else -2f
    )

    Box(
        modifier = Modifier.padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier.size(32.dp),
            color = Color.White,
            shape = RoundedCornerShape(8.dp),
            elevation = 4.dp
        ) {
            LottieAnimation(composition, progress)
        }
    }
}

@Composable
fun Stars(
    enabled: Boolean, content: @Composable () -> Unit
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.stars))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        speed = if (enabled) .8f else 2f,
        iterations = if (!enabled) 1 else LottieConstants.IterateForever
    )

    Box(contentAlignment = Alignment.Center) {
        Box(modifier = Modifier.padding(4.dp)) {
            content()
        }
        LottieAnimation(
            modifier = Modifier.matchParentSize(),
            composition = composition,
            progress = progress
        )
    }
}
