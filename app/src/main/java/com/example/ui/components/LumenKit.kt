package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.LumenTheme

// =====================================================================
// Lumen Kit — the app's native component library (no Material3).
// Built only on Compose foundation + icon glyphs. Signatures mirror
// the exact parameter profiles used across the 22 screens so the
// Emerald Focus look stays pixel-identical while shedding MD3.
// =====================================================================

/** Local content-color provider so children Text/Icon inherit correctly. */
@Composable
fun ProvideContentColor(color: Color, content: @Composable () -> Unit) {
  androidx.compose.runtime.CompositionLocalProvider(
    LocalLumenContentColor provides color,
    content = content,
  )
}

val LocalLumenContentColor =
  androidx.compose.runtime.staticCompositionLocalOf<Color> { Color.Unspecified }

/** Tinted glyph renderer wrapping the icons artifact (assets, not MD3 UI). */
@Composable
fun Icon(
  imageVector: ImageVector,
  contentDescription: String?,
  modifier: Modifier = Modifier,
  tint: Color = Color.Unspecified,
) {
  val resolved = if (tint == Color.Unspecified) LocalLumenContentColor.current else tint
  androidx.compose.material.Icon(
    imageVector = imageVector,
    contentDescription = contentDescription,
    tint = resolved,
    modifier = modifier,
  )
}

/** Text that automatically picks up the surrounding Lumen content color. */
@Composable
fun Text(
  text: String,
  modifier: Modifier = Modifier,
  style: TextStyle = LumenTheme.typography.bodyMedium,
  color: Color = Color.Unspecified,
  textAlign: androidx.compose.ui.text.style.TextAlign? = null,
  overflow: androidx.compose.ui.text.style.TextOverflow = androidx.compose.ui.text.style.TextOverflow.Clip,
  maxLines: Int = Int.MAX_VALUE,
  lineHeight: androidx.compose.ui.unit.TextUnit = androidx.compose.ui.unit.TextUnit.Unspecified,
) {
  val resolved = if (color == Color.Unspecified) LocalLumenContentColor.current else color
  androidx.compose.foundation.text.BasicText(
    text = text,
    style =
      style.copy(
        color = resolved,
        textAlign = textAlign ?: style.textAlign,
        lineHeight =
          if (lineHeight != androidx.compose.ui.unit.TextUnit.Unspecified) lineHeight else style.lineHeight,
      ),
    modifier = modifier,
    overflow = overflow,
    maxLines = maxLines,
  )
}

/** Hairline separator (replaces MD3 Divider). */
@Composable
fun Divider(
  modifier: Modifier = Modifier,
  thickness: Dp = 1.dp,
  color: Color = LumenTheme.colors.outlineVariant,
) {
  Box(modifier = modifier.fillMaxWidth().height(thickness).background(color))
}

/** Generic tinted container with shape + optional elevation shadow. */
@Composable
fun Surface(
  modifier: Modifier = Modifier,
  shape: Shape = LumenTheme.shapes.medium,
  color: Color = LumenTheme.colors.surface,
  contentColor: Color = LumenTheme.colors.onSurface,
  tonalElevation: Dp = 0.dp,
  shadowElevation: Dp = 0.dp,
  border: BorderStroke? = null,
  onClick: (() -> Unit)? = null,
  enabled: Boolean = true,
  interactionSource: MutableInteractionSource? = null,
  content: @Composable BoxScope.() -> Unit,
) {
  val clickMod =
    if (onClick != null && enabled) {
      Modifier.clickable(
        enabled = enabled,
        interactionSource = interactionSource ?: remember { MutableInteractionSource() },
        indication = null,
        role = Role.Button,
        onClick = onClick,
      )
    } else {
      Modifier
    }
  Box(
    modifier =
      modifier
        .then(if (shadowElevation > 0.dp) Modifier.shadow(shadowElevation, shape) else Modifier)
        .clip(shape)
        .background(color)
        .then(if (border != null) Modifier.border(border, shape) else Modifier)
        .then(clickMod),
    propagateMinConstraints = false,
    content = { ProvideContentColor(contentColor, content) },
  )
}

class CardColors(val containerColor: Color, val contentColor: Color)

class CardElevation(val defaultElevation: Dp)

object CardDefaults {
  @Composable
  fun cardColors(
    containerColor: Color = LumenTheme.colors.surfaceContainerLowest,
    contentColor: Color = LumenTheme.colors.onSurface,
  ) = CardColors(containerColor, contentColor)

  @Composable
  fun cardElevation(defaultElevation: Dp = 0.dp) = CardElevation(defaultElevation)
}

/** Card: flat "paper" surface; optional outline + click. */
@Composable
fun Card(
  modifier: Modifier = Modifier,
  onClick: (() -> Unit)? = null,
  enabled: Boolean = true,
  shape: Shape = LumenTheme.shapes.large,
  colors: CardColors = CardDefaults.cardColors(),
  border: BorderStroke? = null,
  elevation: CardElevation = CardDefaults.cardElevation(),
  interactionSource: MutableInteractionSource? = null,
  content: @Composable ColumnScope.() -> Unit,
) {
  val clickableModifier =
    if (onClick != null && enabled) {
      Modifier.clickable(
        interactionSource = interactionSource ?: remember { MutableInteractionSource() },
        indication = null,
        role = Role.Button,
        onClick = onClick,
      )
    } else {
      Modifier
    }
  Surface(
    modifier = modifier.then(clickableModifier),
    shape = shape,
    color = colors.containerColor,
    contentColor = colors.contentColor,
    shadowElevation = elevation.defaultElevation,
    border = border,
  ) {
    Column(content = content)
  }
}

/** Outlined variant of Card. */
@Composable
fun OutlinedCard(
  modifier: Modifier = Modifier,
  onClick: (() -> Unit)? = null,
  enabled: Boolean = true,
  shape: Shape = LumenTheme.shapes.large,
  colors: CardColors = CardDefaults.cardColors(),
  border: BorderStroke = BorderStroke(1.dp, LumenTheme.colors.outline),
  elevation: CardElevation = CardDefaults.cardElevation(),
  content: @Composable ColumnScope.() -> Unit,
) {
  Card(
    modifier = modifier,
    onClick = onClick,
    enabled = enabled,
    shape = shape,
    colors = colors,
    border = border,
    elevation = elevation,
    content = content,
  )
}

class ButtonColors(
  val containerColor: Color,
  val contentColor: Color,
  val disabledContainerColor: Color,
  val disabledContentColor: Color,
)

object ButtonDefaults {
  @Composable
  fun buttonColors(
    containerColor: Color = LumenTheme.colors.primary,
    contentColor: Color = LumenTheme.colors.onPrimary,
    disabledContainerColor: Color = LumenTheme.colors.onSurface.copy(alpha = 0.12f),
    disabledContentColor: Color = LumenTheme.colors.onSurface.copy(alpha = 0.38f),
  ) = ButtonColors(containerColor, contentColor, disabledContainerColor, disabledContentColor)

  @Composable
  fun outlinedButtonColors(
    containerColor: Color = Color.Transparent,
    contentColor: Color = LumenTheme.colors.primary,
  ) =
    ButtonColors(
      containerColor,
      contentColor,
      LumenTheme.colors.onSurface.copy(alpha = 0.12f),
      LumenTheme.colors.onSurface.copy(alpha = 0.38f),
    )

  @Composable
  fun textButtonColors(contentColor: Color = LumenTheme.colors.primary) =
    ButtonColors(
      Color.Transparent,
      contentColor,
      Color.Transparent,
      LumenTheme.colors.onSurface.copy(alpha = 0.38f),
    )

  @Composable
  fun outlinedBorder(borderColor: Color = LumenTheme.colors.outline, borderWidth: Dp = 1.dp): BorderStroke =
    BorderStroke(borderWidth, borderColor)
}

/** Primary filled button. */
@Composable
fun Button(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  shape: Shape = LumenTheme.shapes.small,
  colors: ButtonColors = ButtonDefaults.buttonColors(),
  contentPadding: PaddingValues = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
  interactionSource: MutableInteractionSource? = null,
  content: @Composable RowScope.() -> Unit,
) {
  val bg = if (enabled) colors.containerColor else colors.disabledContainerColor
  val fg = if (enabled) colors.contentColor else colors.disabledContentColor
  Row(
    modifier =
      modifier
        .clip(shape)
        .background(bg)
        .clickable(
          enabled = enabled,
          interactionSource = interactionSource ?: remember { MutableInteractionSource() },
          indication = null,
          role = Role.Button,
          onClick = onClick,
        )
        .padding(contentPadding),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Center,
    content = { ProvideContentColor(fg) { content() } },
  )
}

/** Outlined button with transparent container. */
@Composable
fun OutlinedButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  shape: Shape = LumenTheme.shapes.small,
  colors: ButtonColors = ButtonDefaults.outlinedButtonColors(),
  border: BorderStroke = BorderStroke(1.dp, LumenTheme.colors.outline),
  contentPadding: PaddingValues = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
  interactionSource: MutableInteractionSource? = null,
  content: @Composable RowScope.() -> Unit,
) {
  val fg = if (enabled) colors.contentColor else colors.disabledContentColor
  Row(
    modifier =
      modifier
        .clip(shape)
        .background(colors.containerColor)
        .border(BorderStroke(border.width, if (enabled) border.color else fg), shape)
        .clickable(
          enabled = enabled,
          interactionSource = interactionSource ?: remember { MutableInteractionSource() },
          indication = null,
          role = Role.Button,
          onClick = onClick,
        )
        .padding(contentPadding),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Center,
    content = { ProvideContentColor(fg) { content() } },
  )
}

/** Text-only button. */
@Composable
fun TextButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  shape: Shape = LumenTheme.shapes.small,
  colors: ButtonColors = ButtonDefaults.textButtonColors(),
  contentPadding: PaddingValues = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
  interactionSource: MutableInteractionSource? = null,
  content: @Composable RowScope.() -> Unit,
) {
  Row(
    modifier =
      modifier
        .clip(shape)
        .clickable(
          enabled = enabled,
          interactionSource = interactionSource ?: remember { MutableInteractionSource() },
          indication = null,
          role = Role.Button,
          onClick = onClick,
        )
        .padding(contentPadding),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Center,
    content = {
      ProvideContentColor(if (enabled) colors.contentColor else colors.disabledContentColor) { content() }
    },
  )
}

/** Circular icon-only touch target (44dp — thumb-friendly per research). */
@Composable
fun IconButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  interactionSource: MutableInteractionSource? = null,
  content: @Composable BoxScope.() -> Unit,
) {
  Box(
    modifier =
      modifier
        .size(44.dp)
        .clip(CircleShape)
        .clickable(
          enabled = enabled,
          interactionSource = interactionSource ?: remember { MutableInteractionSource() },
          indication = null,
          role = Role.Button,
          onClick = onClick,
        ),
    contentAlignment = Alignment.Center,
    content = content,
  )
}

class TopBarColors(
  val containerColor: Color,
  val titleContentColor: Color,
  val navigationIconContentColor: Color,
  val actionIconContentColor: Color,
)

object TopAppBarDefaults {
  @Composable
  fun topAppBarColors(
    containerColor: Color = LumenTheme.colors.surface,
    titleContentColor: Color = LumenTheme.colors.onSurface,
    navigationIconContentColor: Color = LumenTheme.colors.onSurface,
    actionIconContentColor: Color = LumenTheme.colors.onSurface,
  ) = TopBarColors(containerColor, titleContentColor, navigationIconContentColor, actionIconContentColor)
}

/** Top bar: 64dp row with leading slot, title, trailing actions. */
@Composable
fun TopAppBar(
  title: @Composable () -> Unit,
  modifier: Modifier = Modifier,
  navigationIcon: @Composable () -> Unit = {},
  actions: @Composable RowScope.() -> Unit = {},
  colors: TopBarColors = TopAppBarDefaults.topAppBarColors(),
) {
  Row(
    modifier = modifier.fillMaxWidth().height(64.dp).background(colors.containerColor),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Box(Modifier.padding(start = 4.dp)) {
      ProvideContentColor(colors.navigationIconContentColor, navigationIcon)
    }
    Box(
      Modifier.weight(1f).padding(start = 4.dp, end = 8.dp),
      contentAlignment = Alignment.CenterStart,
    ) {
      ProvideContentColor(colors.titleContentColor) { title() }
    }
    ProvideContentColor(colors.actionIconContentColor) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End,
        content = actions,
      )
    }
  }
}

/** Scaffold: column layout with top/bottom bars and inset-aware content. */
@Composable
fun Scaffold(
  modifier: Modifier = Modifier,
  containerColor: Color = LumenTheme.colors.background,
  topBar: @Composable () -> Unit = {},
  bottomBar: @Composable () -> Unit = {},
  content: @Composable (PaddingValues) -> Unit,
) {
  Box(modifier = modifier.fillMaxSize().background(containerColor)) {
    Column(Modifier.fillMaxSize()) {
      topBar()
      Box(Modifier.weight(1f)) {
        // Bars stack in the column, so content needs no extra insets.
        content(PaddingValues())
      }
      bottomBar()
    }
  }
}

/** Small status pill (used inside BadgedBox). */
@Composable
fun Badge(
  modifier: Modifier = Modifier,
  containerColor: Color = LumenTheme.colors.error,
  contentColor: Color = LumenTheme.colors.onError,
  content: @Composable () -> Unit,
) {
  Box(
    modifier =
      modifier
        .clip(RoundedCornerShape(9.dp))
        .background(containerColor)
        .padding(horizontal = 6.dp, vertical = 2.dp),
    contentAlignment = Alignment.Center,
    content = { ProvideContentColor(contentColor, content) },
  )
}

/** Positions a badge over the top-end of its content. */
@Composable
fun BadgedBox(
  badge: @Composable () -> Unit,
  content: @Composable BoxScope.() -> Unit,
) {
  Box(contentAlignment = Alignment.TopEnd) {
    content()
    Box(Modifier.align(Alignment.TopEnd).offset(x = 8.dp, y = (-8).dp)) { badge() }
  }
}

class ChipColors(
  val containerColor: Color,
  val contentColor: Color,
  val selectedContainerColor: Color,
  val selectedContentColor: Color,
)

object FilterChipDefaults {
  @Composable
  fun filterChipColors(
    containerColor: Color = LumenTheme.colors.surfaceContainerLowest,
    labelColor: Color = LumenTheme.colors.onSurfaceVariant,
    selectedContainerColor: Color = LumenTheme.colors.primaryContainer,
    selectedLabelColor: Color = LumenTheme.colors.onPrimaryContainer,
  ) = ChipColors(containerColor, labelColor, selectedContainerColor, selectedLabelColor)
}

/** Filter chip with check affordance when selected. */
@Composable
fun FilterChip(
  selected: Boolean,
  onClick: () -> Unit,
  label: @Composable () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  shape: Shape = LumenTheme.shapes.chip,
  colors: ChipColors = FilterChipDefaults.filterChipColors(),
  leadingIcon: @Composable (() -> Unit)? = null,
) {
  val bg = if (selected) colors.selectedContainerColor else colors.containerColor
  val fg = if (selected) colors.selectedContentColor else colors.contentColor
  Row(
    modifier =
      modifier
        .height(36.dp)
        .clip(shape)
        .background(bg)
        .border(BorderStroke(1.dp, LumenTheme.colors.outlineVariant), shape)
        .clickable(enabled = enabled, role = Role.Checkbox, onClick = onClick)
        .padding(horizontal = 12.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    if (leadingIcon != null) {
      ProvideContentColor(fg, leadingIcon)
      Spacer(Modifier.width(6.dp))
    }
    ProvideContentColor(fg) { label() }
  }
}

class RadioColors(val selectedColor: Color, val unselectedColor: Color)

object RadioButtonDefaults {
  @Composable
  fun colors(
    selectedColor: Color = LumenTheme.colors.primary,
    unselectedColor: Color = LumenTheme.colors.onSurfaceVariant,
  ) = RadioColors(selectedColor, unselectedColor)
}

/** Radio option. */
@Composable
fun RadioButton(
  selected: Boolean,
  onClick: (() -> Unit)?,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  colors: RadioColors = RadioButtonDefaults.colors(),
) {
  val ring = if (selected) colors.selectedColor else colors.unselectedColor
  Box(
    modifier =
      modifier
        .size(22.dp)
        .clip(CircleShape)
        .border(BorderStroke(2.dp, ring), CircleShape)
        .then(
          if (onClick != null && enabled) {
            Modifier.clickable(role = Role.RadioButton, onClick = onClick)
          } else {
            Modifier
          }
        ),
    contentAlignment = Alignment.Center,
  ) {
    if (selected) {
      Box(Modifier.size(12.dp).clip(CircleShape).background(colors.selectedColor))
    }
  }
}

/** Segmented tab strip with an indicator line under the selection. */
@Composable
fun TabRow(
  selectedTabIndex: Int,
  modifier: Modifier = Modifier,
  containerColor: Color = LumenTheme.colors.surface,
  contentColor: Color = LumenTheme.colors.onSurface,
  edgePadding: Dp = 12.dp,
  tabs: @Composable () -> Unit,
) {
  Column(modifier = modifier.background(containerColor)) {
    Row(Modifier.fillMaxWidth().height(48.dp).padding(horizontal = edgePadding)) {
      ProvideContentColor(contentColor, tabs)
    }
    Row(Modifier.fillMaxWidth().height(2.dp)) {
      repeat(maxOf(selectedTabIndex, 0)) { Spacer(Modifier.weight(1f)) }
      Box(Modifier.weight(1f).fillMaxHeight().background(LumenTheme.colors.primary))
      Spacer(Modifier.weight(1))
    }
  }
}

/** One equal-width tab inside TabRow (weight supplied by caller modifier). */
@Composable
fun Tab(
  selected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  text: @Composable () -> Unit,
) {
  Box(
    modifier =
      modifier
        .fillMaxHeight()
        .clip(RoundedCornerShape(10.dp))
        .clickable(enabled = enabled, role = Role.Tab, onClick = onClick),
    contentAlignment = Alignment.Center,
    content = {
      ProvideContentColor(
        if (selected) LumenTheme.colors.primary else LumenTheme.colors.onSurfaceVariant
      ) {
        text()
      }
    },
  )
}

class SliderColors(val thumbColor: Color, val activeTrackColor: Color, val inactiveTrackColor: Color)

object SliderDefaults {
  @Composable
  fun colors(
    thumbColor: Color = LumenTheme.colors.primary,
    activeTrackColor: Color = LumenTheme.colors.primary,
    inactiveTrackColor: Color = LumenTheme.colors.surfaceContainerHighest,
  ) = SliderColors(thumbColor, activeTrackColor, inactiveTrackColor)
}

/** Slider with native track/thumb drawing (tap + drag). */
@Composable
fun Slider(
  value: Float,
  onValueChange: (Float) -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
  colors: SliderColors = SliderDefaults.colors(),
) {
  val span = (valueRange.endInclusive - valueRange.start).let { if (it <= 0f) 1f else it }
  val fraction = ((value - valueRange.start) / span).coerceIn(0f, 1f)
  Box(
    modifier =
      modifier
        .fillMaxWidth()
        .height(32.dp)
        .pointerInput(enabled, valueRange) {
          if (!enabled) return@pointerInput
          awaitEachGesture {
            val down = awaitFirstDown(requireUnconsumed = false)
            fun update(x: Float) {
              onValueChange(valueRange.start + (x / size.width.toFloat()).coerceIn(0f, 1f) * span)
            }
            update(down.position.x)
            drag(down.id) { change -> update(change.position.x) }
          }
        },
    contentAlignment = Alignment.CenterStart,
  ) {
    Box(Modifier.fillMaxWidth().height(6.dp).clip(CircleShape).background(colors.inactiveTrackColor))
    Box(Modifier.fillMaxWidth(fraction).height(6.dp).clip(CircleShape).background(colors.activeTrackColor))
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
      Spacer(Modifier.fillMaxWidth(fraction).width(0.dp))
      Box(Modifier.size(20.dp).offset(x = (-10).dp).clip(CircleShape).background(colors.thumbColor))
    }
  }
}

/** Modal bottom sheet rendered as a bottom-anchored dialog. */
@Composable
fun ModalBottomSheet(
  onDismissRequest: () -> Unit,
  modifier: Modifier = Modifier,
  sheetState: Any? = null,
  shape: Shape = RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp),
  containerColor: Color = LumenTheme.colors.surface,
  content: @Composable ColumnScope.() -> Unit,
) {
  Dialog(
    onDismissRequest = onDismissRequest,
    properties = DialogProperties(usePlatformDefaultWidth = false),
  ) {
    Box(
      Modifier
        .fillMaxSize()
        .background(LumenTheme.colors.scrim.copy(alpha = 0.5f))
        .clickable(onClick = onDismissRequest),
      contentAlignment = Alignment.BottomCenter,
    ) {
      Column(
        modifier =
          modifier
            .fillMaxWidth()
            .clip(shape)
            .background(containerColor)
            .padding(bottom = 24.dp),
      ) {
        Box(
          Modifier
            .align(Alignment.CenterHorizontally)
            .padding(top = 10.dp, bottom = 8.dp)
            .width(36.dp)
            .height(4.dp)
            .clip(CircleShape)
            .background(LumenTheme.colors.outline),
        )
        content()
      }
    }
  }
}

class FieldColors(
  val focusedBorderColor: Color,
  val unfocusedBorderColor: Color,
  val focusedContainerColor: Color,
  val unfocusedContainerColor: Color,
  val textColor: Color,
  val placeholderColor: Color,
  val labelColor: Color,
)

object OutlinedTextFieldDefaults {
  @Composable
  fun colors(
    focusedBorderColor: Color = LumenTheme.colors.primary,
    unfocusedBorderColor: Color = LumenTheme.colors.outline,
    focusedContainerColor: Color = LumenTheme.colors.surfaceContainerLowest,
    unfocusedContainerColor: Color = LumenTheme.colors.surfaceContainerLowest,
    textColor: Color = LumenTheme.colors.onSurface,
    placeholderColor: Color = LumenTheme.colors.onSurfaceVariant,
    labelColor: Color = LumenTheme.colors.onSurfaceVariant,
  ) = FieldColors(
    focusedBorderColor,
    unfocusedBorderColor,
    focusedContainerColor,
    unfocusedContainerColor,
    textColor,
    placeholderColor,
    labelColor,
  )
}

/** Outlined text field matching the audited call-site parameter set. */
@Composable
fun OutlinedTextField(
  value: String,
  onValueChange: (String) -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  placeholder: @Composable (() -> Unit)? = null,
  label: @Composable (() -> Unit)? = null,
  leadingIcon: @Composable (() -> Unit)? = null,
  trailingIcon: @Composable (() -> Unit)? = null,
  singleLine: Boolean = false,
  textStyle: TextStyle = LumenTheme.typography.bodyLarge,
  shape: Shape = LumenTheme.shapes.small,
  isError: Boolean = false,
  keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
  visualTransformation: VisualTransformation = VisualTransformation.None,
  colors: FieldColors = OutlinedTextFieldDefaults.colors(),
) {
  var focused by remember { androidx.compose.runtime.mutableStateOf(false) }
  val borderColor =
    when {
      isError -> LumenTheme.colors.error
      focused -> colors.focusedBorderColor
      else -> colors.unfocusedBorderColor
    }
  Column(modifier = modifier) {
    if (label != null) {
      Box(Modifier.padding(bottom = 4.dp)) { ProvideContentColor(colors.labelColor, label) }
    }
    Row(
      modifier =
        Modifier
          .fillMaxWidth()
          .clip(shape)
          .background(if (focused) colors.focusedContainerColor else colors.unfocusedContainerColor)
          .border(BorderStroke(if (focused) 2.dp else 1.dp, borderColor), shape)
          .padding(horizontal = 14.dp, vertical = 14.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      if (leadingIcon != null) {
        leadingIcon()
        Spacer(Modifier.width(10.dp))
      }
      BasicTextField(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        singleLine = singleLine,
        textStyle = textStyle.copy(color = colors.textColor),
        cursorBrush = SolidColor(LumenTheme.colors.primary),
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        modifier = Modifier.weight(1f).onFocusChanged { focused = it.isFocused },
        decorationBox = { inner ->
          Box(contentAlignment = Alignment.CenterStart) {
            if (placeholder != null && value.isEmpty()) {
              ProvideContentColor(colors.placeholderColor, placeholder)
            }
            inner()
          }
        },
      )
      if (trailingIcon != null) {
        Spacer(Modifier.width(10.dp))
        trailingIcon()
      }
    }
  }
}

/** Determinate progress bar (band scores, daily goals). */
@Composable
fun LinearProgressIndicator(
  progress: () -> Float,
  modifier: Modifier = Modifier,
  color: Color = LumenTheme.colors.primary,
  trackColor: Color = LumenTheme.colors.surfaceContainerHighest,
  strokeWidth: Dp = 8.dp,
) {
  val p = progress().coerceIn(0f, 1f)
  Box(
    modifier = modifier.fillMaxWidth().height(strokeWidth).clip(CircleShape).background(trackColor),
  ) {
    Box(Modifier.fillMaxWidth(p).height(strokeWidth).clip(CircleShape).background(color))
  }
}

/** Indeterminate spinner drawn natively. */
@Composable
fun CircularProgressIndicator(
  modifier: Modifier = Modifier,
  color: Color = LumenTheme.colors.primary,
  strokeWidth: Dp = 3.dp,
) {
  val transition = androidx.compose.animation.core.rememberInfiniteTransition(label = "spinner")
  val angle by
    transition.animateFloat(
      initialValue = 0f,
      targetValue = 360f,
      animationSpec =
        androidx.compose.animation.core.infiniteRepeatable(
          androidx.compose.animation.core.tween(900, easing = androidx.compose.animation.core.LinearEasing),
        ),
      label = "angle",
    )
  Box(
    modifier =
      modifier.size(28.dp).drawBehind {
        drawArc(
          color = color,
          startAngle = angle,
          sweepAngle = 280f,
          useCenter = false,
          style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round),
        )
      },
  ) {}
}
