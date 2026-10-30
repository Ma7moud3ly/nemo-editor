package io.ma7moud3ly.nemo.lsp.completion

/**
 * Compose names suggested by [KotlinCompletionProvider], taken from the
 * fast-compose bindings. Each name maps to its parameter names, or to an empty
 * string when it takes none. Overloads share one entry with all their parameters.
 */
internal object ComposeBindings {

    /** Composable functions, such as `Column` and `Text`. */
    val composables: Map<String, String> = mapOf(
        "AlertDialog" to "onDismissRequest, confirmButton, modifier, dismissButton, icon, title, text, shape, containerColor, iconContentColor, titleContentColor, textContentColor, tonalElevation, properties",
        "AnimatedContent" to "targetState, modifier, transitionSpec, contentAlignment, label, contentKey, content",
        "AnimatedVisibility" to "visible, modifier, enter, exit, label, content",
        "AssistChip" to "onClick, label, modifier, enabled, leadingIcon, trailingIcon, shape, colors, elevation, border, horizontalArrangement, contentPadding",
        "AsyncImage" to "model, contentDescription, modifier, placeholder, error, fallback, onLoading, onSuccess, onError, alignment, contentScale, alpha, colorFilter, filterQuality, clipToBounds",
        "Badge" to "modifier, containerColor, contentColor, content",
        "BadgedBox" to "badge, modifier, content",
        "BasicText" to "text, modifier, style, onTextLayout, overflow, softWrap, maxLines, minLines",
        "BasicTextField" to "value, onValueChange, modifier, enabled, readOnly, textStyle, decorationBox, singleLine, maxLines, minLines, cursorBrush",
        "BottomAppBar" to "modifier, containerColor, contentColor, tonalElevation, contentPadding, content, actions, floatingActionButton",
        "BottomSheetScaffold" to "sheetContent, modifier, scaffoldState, sheetPeekHeight, sheetMaxWidth, sheetShape, sheetContainerColor, sheetContentColor, sheetTonalElevation, sheetShadowElevation, sheetDragHandle, sheetSwipeEnabled, topBar, snackbarHost, containerColor, contentColor, content",
        "Box" to "modifier, contentAlignment, propagateMinConstraints, content",
        "BoxWithConstraints" to "modifier, contentAlignment, content",
        "Button" to "onClick, modifier, enabled, shape, colors, elevation, border, contentPadding, interactionSource, content",
        "Canvas" to "modifier, onDraw",
        "Card" to "modifier, shape, colors, elevation, border, content, onClick, enabled",
        "CenterAlignedTopAppBar" to "title, modifier, navigationIcon, actions, expandedHeight, windowInsets, colors, scrollBehavior",
        "Checkbox" to "checked, onCheckedChange, modifier, enabled, colors",
        "CircularProgressIndicator" to "progress, modifier, color, strokeWidth",
        "Column" to "modifier, verticalArrangement, horizontalAlignment, content",
        "CompositionLocalProvider" to "values, content",
        "Crossfade" to "targetState, modifier, animationSpec, label, content",
        "DatePicker" to "state, modifier, colors, title, headline, showModeToggle",
        "DatePickerDialog" to "onDismissRequest, confirmButton, modifier, dismissButton, shape, tonalElevation, colors, properties, content",
        "Dialog" to "onDismissRequest, properties, content", "DisableSelection" to "content",
        "DismissibleDrawerSheet" to "modifier, drawerShape, drawerContainerColor, drawerContentColor, drawerTonalElevation, content",
        "DismissibleNavigationDrawer" to "drawerContent, modifier, drawerState, gesturesEnabled, content",
        "DisposableEffect" to "keys, effect, key1, key2, key3",
        "DropdownMenu" to "expanded, onDismissRequest, modifier, content",
        "DropdownMenuItem" to "text, onClick, modifier, leadingIcon, trailingIcon, enabled",
        "ElevatedAssistChip" to "onClick, label, modifier, enabled, leadingIcon, trailingIcon, shape, colors, elevation, border, horizontalArrangement, contentPadding",
        "ElevatedButton" to "onClick, modifier, enabled, shape, colors, elevation, border, contentPadding, content",
        "ElevatedCard" to "modifier, shape, colors, elevation, content, onClick, enabled",
        "ElevatedFilterChip" to "selected, onClick, label, modifier, enabled, leadingIcon, trailingIcon, shape, colors, elevation, border, horizontalArrangement, contentPadding",
        "ElevatedSuggestionChip" to "onClick, label, modifier, enabled, icon, shape, colors, elevation, border, horizontalArrangement, contentPadding",
        "ExposedDropdownMenu" to "expanded, onDismissRequest, modifier, content",
        "ExposedDropdownMenuBox" to "expanded, onExpandedChange, modifier, content",
        "ExtendedFloatingActionButton" to "onClick, modifier, shape, containerColor, contentColor, elevation, content, text, icon, expanded",
        "FilledIconButton" to "onClick, modifier, enabled, shape, colors, content",
        "FilledTonalButton" to "onClick, modifier, enabled, shape, colors, elevation, border, contentPadding, content",
        "FilledTonalIconButton" to "onClick, modifier, enabled, shape, colors, content",
        "FilterChip" to "selected, onClick, label, modifier, enabled, leadingIcon, trailingIcon, shape, colors, elevation, border, horizontalArrangement, contentPadding",
        "FloatingActionButton" to "onClick, modifier, shape, containerColor, contentColor, elevation, content",
        "FlowColumn" to "modifier, verticalArrangement, horizontalArrangement, itemHorizontalAlignment, maxItemsInEachColumn, maxLines, content",
        "FlowRow" to "modifier, horizontalArrangement, verticalArrangement, itemVerticalAlignment, maxItemsInEachRow, maxLines, content",
        "HorizontalDivider" to "modifier, thickness, color",
        "HorizontalPager" to "state, modifier, contentPadding, pageSize, beyondViewportPageCount, pageSpacing, verticalAlignment, userScrollEnabled, reverseLayout, pageContent",
        "Icon" to "imageVector, contentDescription, modifier, tint, bitmap, painter",
        "IconButton" to "onClick, modifier, enabled, colors, content",
        "IconToggleButton" to "checked, onCheckedChange, modifier, enabled, colors, content",
        "Image" to "imageVector, contentDescription, modifier, alignment, contentScale, alpha, colorFilter, bitmap, painter",
        "InputChip" to "selected, onClick, label, modifier, enabled, leadingIcon, avatar, trailingIcon, shape, colors, elevation, border",
        "LargeFloatingActionButton" to "onClick, modifier, shape, containerColor, contentColor, elevation, content",
        "LargeTopAppBar" to "title, modifier, navigationIcon, actions, collapsedHeight, expandedHeight, windowInsets, colors, scrollBehavior",
        "LaunchedEffect" to "keys, block, key1, key2, key3",
        "Layout" to "content, modifier, measurePolicy",
        "LazyColumn" to "modifier, state, contentPadding, reverseLayout, verticalArrangement, horizontalAlignment, userScrollEnabled, content",
        "LazyHorizontalGrid" to "rows, modifier, state, contentPadding, reverseLayout, horizontalArrangement, verticalArrangement, userScrollEnabled, content",
        "LazyHorizontalStaggeredGrid" to "rows, modifier, contentPadding, reverseLayout, verticalArrangement, horizontalItemSpacing, userScrollEnabled, content",
        "LazyRow" to "modifier, state, contentPadding, reverseLayout, horizontalArrangement, verticalAlignment, userScrollEnabled, content",
        "LazyVerticalGrid" to "columns, modifier, state, contentPadding, reverseLayout, verticalArrangement, horizontalArrangement, userScrollEnabled, content",
        "LazyVerticalStaggeredGrid" to "columns, modifier, contentPadding, reverseLayout, verticalItemSpacing, horizontalArrangement, userScrollEnabled, content",
        "LinearProgressIndicator" to "progress, modifier, color, trackColor, strokeCap",
        "ListItem" to "headlineContent, modifier, overlineContent, supportingContent, leadingContent, trailingContent, colors, tonalElevation, shadowElevation",
        "MaterialTheme" to "colorScheme, shapes, typography, content",
        "MediumTopAppBar" to "title, modifier, navigationIcon, actions, collapsedHeight, expandedHeight, windowInsets, colors, scrollBehavior",
        "ModalBottomSheet" to "onDismissRequest, modifier, sheetState, content",
        "ModalDrawerSheet" to "modifier, content",
        "ModalNavigationDrawer" to "drawerContent, modifier, drawerState, gesturesEnabled, content",
        "MultiChoiceSegmentedButtonRow" to "modifier, space, content",
        "NavigationBar" to "modifier, containerColor, contentColor, tonalElevation, content",
        "NavigationBarItem" to "selected, onClick, icon, modifier, enabled, label, alwaysShowLabel",
        "NavigationDrawerItem" to "label, selected, onClick, modifier, icon, badge",
        "NavigationRail" to "modifier, containerColor, contentColor, header, windowInsets, content",
        "NavigationRailItem" to "selected, onClick, icon, modifier, enabled, label, alwaysShowLabel",
        "OutlinedButton" to "onClick, modifier, enabled, shape, colors, elevation, border, contentPadding, content",
        "OutlinedCard" to "modifier, shape, colors, elevation, border, content, onClick, enabled",
        "OutlinedIconButton" to "onClick, modifier, enabled, shape, colors, border, content",
        "OutlinedTextField" to "value, onValueChange, modifier, enabled, readOnly, textStyle, label, placeholder, leadingIcon, trailingIcon, prefix, suffix, supportingText, isError, visualTransformation, keyboardOptions, keyboardActions, singleLine, maxLines, minLines, shape, colors",
        "PermanentDrawerSheet" to "modifier, drawerShape, drawerContainerColor, drawerContentColor, drawerTonalElevation, content",
        "PermanentNavigationDrawer" to "drawerContent, modifier, content",
        "PlainTooltip" to "modifier, caretShape, maxWidth, shape, contentColor, containerColor, tonalElevation, shadowElevation, content",
        "Popup" to "alignment, offset, onDismissRequest, properties, content",
        "PrimaryScrollableTabRow" to "selectedTabIndex, modifier, scrollState, containerColor, contentColor, edgePadding, tabs",
        "PrimaryTabRow" to "selectedTabIndex, modifier, tabs",
        "ProvideTextStyle" to "value, content",
        "RadioButton" to "selected, onClick, modifier, enabled, colors",
        "RangeSlider" to "value, onValueChange, modifier, enabled, valueRange, steps, onValueChangeFinished, colors",
        "Row" to "modifier, horizontalArrangement, verticalAlignment, content",
        "Scaffold" to "modifier, topBar, bottomBar, snackbarHost, floatingActionButton, floatingActionButtonPosition, containerColor, contentColor, contentWindowInsets, content",
        "ScrollableTabRow" to "selectedTabIndex, modifier, containerColor, contentColor, edgePadding, tabs",
        "SearchBar" to "inputField, expanded, onExpandedChange, modifier, shape, colors, tonalElevation, shadowElevation, content",
        "SecondaryTabRow" to "selectedTabIndex, modifier, containerColor, contentColor, tabs",
        "SegmentedButton" to "checked, onCheckedChange, shape, modifier, enabled, colors, icon, label, selected, onClick",
        "SelectionContainer" to "modifier, content", "SideEffect" to "effect",
        "SingleChoiceSegmentedButtonRow" to "modifier, space, content",
        "Slider" to "value, onValueChange, modifier, enabled, valueRange, steps, onValueChangeFinished, colors",
        "SmallFloatingActionButton" to "onClick, modifier, shape, containerColor, contentColor, elevation, content",
        "Snackbar" to "modifier, content", "SnackbarHost" to "hostState, modifier",
        "Spacer" to "modifier",
        "SuggestionChip" to "onClick, label, modifier, enabled, icon, shape, colors, elevation, border, horizontalArrangement, contentPadding",
        "Surface" to "modifier, shape, color, contentColor, tonalElevation, shadowElevation, border, content, onClick, enabled, checked, onCheckedChange, selected",
        "Switch" to "checked, onCheckedChange, modifier, thumbContent, enabled, colors",
        "Tab" to "selected, onClick, modifier, enabled, text, icon",
        "TabRow" to "selectedTabIndex, modifier, tabs",
        "Text" to "text, modifier, color, fontSize, fontStyle, fontWeight, fontFamily, letterSpacing, textDecoration, textAlign, lineHeight, overflow, softWrap, maxLines, minLines, onTextLayout, style",
        "TextButton" to "onClick, modifier, enabled, shape, colors, elevation, border, contentPadding, content",
        "TextField" to "value, onValueChange, modifier, enabled, readOnly, textStyle, label, placeholder, leadingIcon, trailingIcon, prefix, suffix, supportingText, isError, visualTransformation, keyboardOptions, keyboardActions, singleLine, maxLines, minLines, shape, colors",
        "TooltipBox" to "positionProvider, tooltip, state, modifier, onDismissRequest, focusable, enableUserInput, hasAction, content",
        "TopAppBar" to "title, modifier, navigationIcon, actions, expandedHeight, windowInsets, colors, scrollBehavior",
        "TriStateCheckbox" to "state, onClick, modifier, enabled, colors",
        "VerticalDivider" to "modifier, thickness, color",
        "VerticalPager" to "state, modifier, contentPadding, pageSize, beyondViewportPageCount, pageSpacing, horizontalAlignment, userScrollEnabled, reverseLayout, pageContent",
        "animateColorAsState" to "targetValue, animationSpec, label, finishedListener",
        "animateDpAsState" to "targetValue, animationSpec, label, finishedListener",
        "animateFloatAsState" to "targetValue, animationSpec, visibilityThreshold, label, finishedListener",
        "animateIntAsState" to "targetValue, animationSpec, label, finishedListener",
        "animateOffsetAsState" to "targetValue, animationSpec, label, finishedListener",
        "imageResource" to "id, resource", "isSystemInDarkTheme" to "", "key" to "keys, block",
        "painterResource" to "id, resource",
        "produceState" to "initialValue, keys, producer, key1, key2, key3",
        "remember" to "keys, calculation",
        "rememberAsyncImagePainter" to "model, placeholder, error, fallback, onLoading, onSuccess, onError, contentScale, filterQuality",
        "rememberBottomSheetScaffoldState" to "bottomSheetState, snackbarHostState",
        "rememberBottomSheetState" to "initialValue, enabledValues, confirmValueChange",
        "rememberCoroutineScope" to "",
        "rememberDatePickerState" to "initialSelectedDateMillis, initialDisplayedMonthMillis, yearRange, initialDisplayMode",
        "rememberDraggableState" to "onDelta", "rememberDrawerState" to "initialValue",
        "rememberInfiniteTransition" to "label",
        "rememberLazyGridState" to "initialFirstVisibleItemIndex, initialFirstVisibleItemScrollOffset",
        "rememberLazyListState" to "initialFirstVisibleItemIndex, initialFirstVisibleItemScrollOffset",
        "rememberModalBottomSheetState" to "skipPartiallyExpanded, confirmValueChange",
        "rememberPagerState" to "initialPage, initialPageOffsetFraction, pageCount",
        "rememberSaveable" to "inputs, init", "rememberScrollState" to "",
        "rememberScrollableState" to "consumeScrollDelta",
        "rememberStandardBottomSheetState" to "initialValue, confirmValueChange, skipHiddenState",
        "rememberTooltipState" to "initialIsVisible, isPersistent",
        "rememberUpdatedState" to "newValue", "stringResource" to "id, formatArgs, resource",
        "updateTransition" to "targetState, label", "vectorResource" to "id, resource"
    )

    /** Plain and suspend functions, such as `mutableStateOf` and `tween`. */
    val functions: Map<String, String> = mapOf(
        "Animatable" to "initialValue", "AnnotatedString" to "text",
        "Color" to "color, red, green, blue, alpha",
        "Constraints" to "minWidth, maxWidth, minHeight, maxHeight", "CornerRadius" to "x, y",
        "DialogProperties" to "dismissOnBackPress, dismissOnClickOutside, usePlatformDefaultWidth",
        "GridItemSpan" to "currentLineSpan", "IntOffset" to "x, y",
        "KeyboardActions" to "onDone, onGo, onNext, onPrevious, onSearch, onSend",
        "KeyboardOptions" to "capitalization, autoCorrectEnabled, keyboardType, imeAction",
        "MutableInteractionSource" to "", "MutableStateFlow" to "value", "Offset" to "x, y",
        "PaddingValues" to "all, horizontal, vertical, start, top, end, bottom",
        "ParagraphStyle" to "textAlign, textDirection, lineHeight",
        "PasswordVisualTransformation" to "mask", "Path" to "",
        "PopupProperties" to "focusable, dismissOnBackPress, dismissOnClickOutside, clippingEnabled, usePlatformDefaultWidth",
        "RoundedCornerShape" to "size, topStart, topEnd, bottomEnd, bottomStart",
        "Shadow" to "color, offset, blurRadius",
        "Shapes" to "extraSmall, small, medium, large, extraLarge", "Size" to "width, height",
        "SnackbarHostState" to "",
        "SpanStyle" to "color, fontSize, fontWeight, fontStyle, fontSynthesis, fontFamily, fontFeatureSettings, letterSpacing, baselineShift, textGeometricTransform, localeList, background, textDecoration",
        "Stroke" to "width, miter, cap, join",
        "TextStyle" to "color, fontSize, fontWeight, fontStyle, fontFamily, letterSpacing, background, textDecoration, shadow, textAlign, lineHeight",
        "ToggleableState" to "value", "TransformOrigin" to "pivotFractionX, pivotFractionY",
        "Typography" to "displayLarge, displayMedium, displaySmall, headlineLarge, headlineMedium, headlineSmall, titleLarge, titleMedium, titleSmall, bodyLarge, bodyMedium, bodySmall, labelLarge, labelMedium, labelSmall",
        "WindowInsets" to "left, top, right, bottom", "buildAnnotatedString" to "builder",
        "compositionLocalOf" to "defaultFactory",
        "darkColorScheme" to "primary, onPrimary, primaryContainer, onPrimaryContainer, inversePrimary, secondary, onSecondary, secondaryContainer, onSecondaryContainer, tertiary, onTertiary, tertiaryContainer, onTertiaryContainer, background, onBackground, surface, onSurface, surfaceVariant, onSurfaceVariant, surfaceTint, inverseSurface, inverseOnSurface, error, onError, errorContainer, onErrorContainer, outline, outlineVariant, scrim, surfaceBright, surfaceContainer, surfaceContainerHigh, surfaceContainerHighest, surfaceContainerLow, surfaceContainerLowest, surfaceDim",
        "delay" to "timeMillis", "derivedStateOf" to "calculation", "expandHorizontally" to "",
        "expandIn" to "", "expandVertically" to "", "fadeIn" to "animationSpec, initialAlpha",
        "fadeOut" to "animationSpec, targetAlpha", "infiniteRepeatable" to "animation, repeatMode",
        "keyframes" to "init",
        "lightColorScheme" to "primary, onPrimary, primaryContainer, onPrimaryContainer, inversePrimary, secondary, onSecondary, secondaryContainer, onSecondaryContainer, tertiary, onTertiary, tertiaryContainer, onTertiaryContainer, background, onBackground, surface, onSurface, surfaceVariant, onSurfaceVariant, surfaceTint, inverseSurface, inverseOnSurface, error, onError, errorContainer, onErrorContainer, outline, outlineVariant, scrim, surfaceBright, surfaceContainer, surfaceContainerHigh, surfaceContainerHighest, surfaceContainerLow, surfaceContainerLowest, surfaceDim",
        "mutableDoubleStateOf" to "value", "mutableFloatStateOf" to "value",
        "mutableIntStateOf" to "value", "mutableLongStateOf" to "value",
        "mutableStateListOf" to "elements", "mutableStateMapOf" to "pairs",
        "mutableStateOf" to "value", "repeatable" to "iterations, animation, repeatMode",
        "ripple" to "bounded, radius, color", "scaleIn" to "animationSpec, initialScale",
        "scaleOut" to "animationSpec, targetScale", "shrinkHorizontally" to "", "shrinkOut" to "",
        "shrinkVertically" to "", "slideInHorizontally" to "animationSpec, initialOffsetX",
        "slideInVertically" to "animationSpec, initialOffsetY",
        "slideOutHorizontally" to "animationSpec, targetOffsetX",
        "slideOutVertically" to "animationSpec, targetOffsetY", "snap" to "delayMillis",
        "snapshotFlow" to "block", "spring" to "dampingRatio, stiffness, visibilityThreshold",
        "staticCompositionLocalOf" to "defaultFactory",
        "tween" to "durationMillis, delayMillis, easing", "withFrameMillis" to "onFrame",
        "withFrameNanos" to "onFrame"
    )

    /** Types and objects, such as `Modifier`, `Color` and `Alignment`. */
    val types: List<String> = listOf(
        "Alignment", "Animatable", "AnnotatedString", "Arrangement", "BlendMode",
        "BottomAppBarDefaults", "BottomSheetScaffoldState", "BoxScope", "BoxWithConstraintsScope",
        "Brush", "ButtonDefaults", "CacheDrawScope", "CardDefaults", "CheckboxDefaults",
        "CircleShape", "Color", "ColorFilter", "ColorScheme", "ColumnScope", "CompositionLocal",
        "Constraints", "ContentDrawScope", "ContentScale", "ContentTransform", "CoroutineScope",
        "DatePickerDefaults", "DatePickerState", "DialogProperties", "DisplayMode",
        "DisposableEffectScope", "Dp", "DrawScope", "DrawStyle", "DrawerState", "DrawerValue",
        "EnterTransition", "ExitTransition", "ExposedDropdownMenuAnchorType",
        "ExposedDropdownMenuDefaults", "FabPosition", "Flow", "FontFamily", "FontStyle",
        "FontWeight", "GraphicsLayerScope", "GridCells", "GridItemSpan", "IconButtonDefaults",
        "Icons", "ImageBitmap", "ImageVector", "ImeAction", "InfiniteTransition", "IntOffset",
        "IntSize", "InteractionSource", "Job", "KeyboardActions", "KeyboardCapitalization",
        "KeyboardOptions", "KeyboardType", "KeyframeEntity", "KeyframesSpecConfig",
        "LayoutCoordinates", "LazyGridItemScope", "LazyGridItemSpanScope", "LazyGridScope",
        "LazyItemScope", "LazyListScope", "LazyListState", "LocalContentColor", "LocalDensity",
        "LocalFocusManager", "LocalLayoutDirection", "LocalSoftwareKeyboardController",
        "LocalTextStyle", "LocalUriHandler", "MaterialTheme", "Measurable", "Modifier",
        "MutableStateFlow", "Offset", "Orientation", "OutlinedTextFieldDefaults", "PaddingValues",
        "PageSize", "PagerState", "Painter", "ParagraphStyle", "Path", "PathEffect", "Placeable",
        "PointerInputChange", "PointerInputScope", "PopupProperties", "ProvidedValue",
        "RectangleShape", "RepeatMode", "Res", "ResourceRef", "Role", "RowScope", "ScrollState",
        "ScrollableState", "SearchBarDefaults", "Segment", "SegmentedButtonDefaults", "Shadow",
        "Shape", "Shapes", "SheetState", "SheetValue", "Size", "SnackbarDuration",
        "SnackbarHostState", "SnackbarResult", "SpanStyle", "Spring", "StaggeredGridCells",
        "StaggeredGridItemSpan", "StateFlow", "Stroke", "StrokeCap", "StrokeJoin", "TextAlign",
        "TextDecoration", "TextFieldDefaults", "TextOverflow", "TextStyle", "TextUnit", "TileMode",
        "ToggleableState", "TooltipAnchorPosition", "TooltipDefaults", "TooltipState",
        "TopAppBarDefaults", "TopAppBarScrollBehavior", "TransformOrigin", "Transition",
        "Typography", "VisualTransformation", "WindowInsets"
    )

    /** Top-level values, such as easing curves. */
    val values: List<String> = listOf(
        "FastOutLinearInEasing", "FastOutSlowInEasing", "Fill", "LinearEasing",
        "LinearOutSlowInEasing"
    )

    /** Members suggested after `Receiver.`, keyed by the receiver's name. */
    val members: Map<String, Map<String, String>> = mapOf(
        "Alignment" to mapOf(
            "Bottom" to "", "BottomCenter" to "", "BottomEnd" to "", "BottomStart" to "",
            "Center" to "", "CenterEnd" to "", "CenterHorizontally" to "", "CenterStart" to "",
            "CenterVertically" to "", "End" to "", "Start" to "", "Top" to "", "TopCenter" to "",
            "TopEnd" to "", "TopStart" to ""
        ),
        "Arrangement" to mapOf(
            "Bottom" to "", "Center" to "", "End" to "", "SpaceAround" to "", "SpaceBetween" to "",
            "SpaceEvenly" to "", "Start" to "", "Top" to "", "spacedBy" to "space"
        ),
        "AutoMirrored" to mapOf(
            "Default" to "", "Filled" to "", "Outlined" to ""
        ),
        "BlendMode" to mapOf(
            "Clear" to "", "Color" to "", "ColorBurn" to "", "ColorDodge" to "", "Darken" to "",
            "Difference" to "", "Dst" to "", "DstAtop" to "", "DstIn" to "", "DstOut" to "",
            "DstOver" to "", "Exclusion" to "", "Hardlight" to "", "Hue" to "", "Lighten" to "",
            "Luminosity" to "", "Modulate" to "", "Multiply" to "", "Overlay" to "", "Plus" to "",
            "Saturation" to "", "Screen" to "", "Softlight" to "", "Src" to "", "SrcAtop" to "",
            "SrcIn" to "", "SrcOut" to "", "SrcOver" to "", "Xor" to ""
        ),
        "BottomSheetScaffoldState" to mapOf(
            "bottomSheetState" to "", "snackbarHostState" to ""
        ),
        "Brush" to mapOf(
            "horizontalGradient" to "colors, startX, endX, tileMode, colorStops",
            "linearGradient" to "colors, start, end, tileMode, colorStops",
            "radialGradient" to "colors, center, radius, tileMode, colorStops",
            "sweepGradient" to "colors, center, colorStops",
            "verticalGradient" to "colors, startY, endY, tileMode, colorStops"
        ),
        "ButtonDefaults" to mapOf(
            "ContentPadding" to "", "buttonColors" to "containerColor, contentColor",
            "shape" to ""
        ),
        "CardDefaults" to mapOf(
            "cardColors" to "containerColor, contentColor", "cardElevation" to "defaultElevation",
            "shape" to ""
        ),
        "CheckboxDefaults" to mapOf(
            "colors" to "checkedColor, uncheckedColor, checkmarkColor"
        ),
        "Color" to mapOf(
            "copy" to "alpha", "Black" to "", "Blue" to "", "Cyan" to "", "DarkGray" to "",
            "Gray" to "", "Green" to "", "LightGray" to "", "Magenta" to "", "Red" to "",
            "Transparent" to "", "Unspecified" to "", "White" to "", "Yellow" to ""
        ),
        "ColorFilter" to mapOf(
            "tint" to "color, blendMode"
        ),
        "ColorScheme" to mapOf(
            "background" to "", "error" to "", "onBackground" to "", "onError" to "",
            "onPrimary" to "", "onPrimaryContainer" to "", "onSecondary" to "",
            "onSecondaryContainer" to "", "onSurface" to "", "onSurfaceVariant" to "",
            "onTertiary" to "", "outline" to "", "outlineVariant" to "", "primary" to "",
            "primaryContainer" to "", "secondary" to "", "secondaryContainer" to "",
            "surface" to "", "surfaceVariant" to "", "tertiary" to ""
        ),
        "ContentScale" to mapOf(
            "Crop" to "", "FillBounds" to "", "FillHeight" to "", "FillWidth" to "", "Fit" to "",
            "Inside" to "", "None" to ""
        ),
        "CoroutineScope" to mapOf(
            "launch" to "block"
        ),
        "DatePickerState" to mapOf(
            "displayMode" to "", "displayedMonthMillis" to "", "selectedDateMillis" to ""
        ),
        "Default" to mapOf(
            "AcUnit" to "", "AccessTime" to "", "AccountBalance" to "",
            "AccountBalanceWallet" to "", "AccountBox" to "", "AccountCircle" to "", "Add" to "",
            "AddCircle" to "", "AddShoppingCart" to "", "Alarm" to "", "AlternateEmail" to "",
            "Analytics" to "", "Apps" to "", "Archive" to "", "ArrowBack" to "",
            "ArrowDownward" to "", "ArrowDropDown" to "", "ArrowDropUp" to "",
            "ArrowForward" to "", "ArrowUpward" to "", "AttachFile" to "", "AttachMoney" to "",
            "Attachment" to "", "Autorenew" to "", "Badge" to "", "BarChart" to "",
            "BatteryFull" to "", "Block" to "", "Bluetooth" to "", "Bolt" to "", "Book" to "",
            "Bookmark" to "", "BookmarkBorder" to "", "Bookmarks" to "", "Brush" to "",
            "BugReport" to "", "Build" to "", "Cake" to "", "Calculate" to "",
            "CalendarMonth" to "", "CalendarToday" to "", "Call" to "", "CameraAlt" to "",
            "Cancel" to "", "CardGiftcard" to "", "Category" to "", "Celebration" to "",
            "Chat" to "", "Check" to "", "CheckCircle" to "", "CheckCircleOutline" to "",
            "ChevronLeft" to "", "ChevronRight" to "", "Circle" to "", "Clear" to "",
            "Close" to "", "Cloud" to "", "CloudDone" to "", "CloudDownload" to "",
            "CloudOff" to "", "CloudUpload" to "", "Code" to "", "ColorLens" to "",
            "Comment" to "", "Computer" to "", "Construction" to "", "Contacts" to "",
            "ContentCopy" to "", "ContentCut" to "", "ContentPaste" to "", "Create" to "",
            "CreateNewFolder" to "", "CreditCard" to "", "DarkMode" to "", "Dashboard" to "",
            "DateRange" to "", "Delete" to "", "Description" to "", "Directions" to "",
            "DirectionsCar" to "", "Done" to "", "DoneAll" to "", "Download" to "", "Drafts" to "",
            "DragHandle" to "", "DragIndicator" to "", "Eco" to "", "Edit" to "", "Email" to "",
            "EmojiEvents" to "", "Engineering" to "", "Error" to "", "ErrorOutline" to "",
            "Euro" to "", "Event" to "", "ExitToApp" to "", "ExpandLess" to "", "ExpandMore" to "",
            "Explore" to "", "Extension" to "", "Face" to "", "Favorite" to "",
            "FavoriteBorder" to "", "Feedback" to "", "FileDownload" to "", "FileUpload" to "",
            "Filter" to "", "FilterAlt" to "", "FilterList" to "", "Fingerprint" to "",
            "FitnessCenter" to "", "Flag" to "", "Flight" to "", "Folder" to "",
            "FolderOpen" to "", "FormatPaint" to "", "Forum" to "", "Fullscreen" to "",
            "Grade" to "", "GridView" to "", "Group" to "", "Groups" to "", "Handyman" to "",
            "Headphones" to "", "Help" to "", "History" to "", "Home" to "", "Hotel" to "",
            "HourglassEmpty" to "", "Image" to "", "Inbox" to "", "Info" to "",
            "InsertDriveFile" to "", "Insights" to "", "Inventory" to "", "Key" to "",
            "Keyboard" to "", "KeyboardArrowDown" to "", "KeyboardArrowLeft" to "",
            "KeyboardArrowRight" to "", "KeyboardArrowUp" to "", "Label" to "", "Language" to "",
            "Laptop" to "", "Launch" to "", "Layers" to "", "Leaderboard" to "", "LightMode" to "",
            "Lightbulb" to "", "Link" to "", "List" to "", "LocalCafe" to "",
            "LocalFireDepartment" to "", "LocalHospital" to "", "LocalMall" to "",
            "LocalOffer" to "", "LocalShipping" to "", "LocationOn" to "", "Lock" to "",
            "LockOpen" to "", "Login" to "", "Logout" to "", "Loyalty" to "", "Mail" to "",
            "Map" to "", "Memory" to "", "Menu" to "", "MenuOpen" to "", "Mic" to "",
            "MicOff" to "", "ModeEdit" to "", "MonetizationOn" to "", "Mood" to "",
            "MoreHoriz" to "", "MoreVert" to "", "Movie" to "", "MusicNote" to "",
            "MyLocation" to "", "Navigation" to "", "NearMe" to "", "Notifications" to "",
            "NotificationsActive" to "", "NotificationsNone" to "", "OpenInNew" to "",
            "Paid" to "", "Palette" to "", "Pause" to "", "PauseCircle" to "", "Payment" to "",
            "People" to "", "Person" to "", "PersonAdd" to "", "Pets" to "", "Phone" to "",
            "Photo" to "", "PhotoCamera" to "", "PhotoLibrary" to "", "PieChart" to "",
            "Place" to "", "PlayArrow" to "", "PlayCircle" to "", "PowerSettingsNew" to "",
            "Print" to "", "Public" to "", "QrCode" to "", "QrCodeScanner" to "", "Receipt" to "",
            "Redeem" to "", "Refresh" to "", "Remove" to "", "RemoveCircle" to "", "Repeat" to "",
            "Replay" to "", "Report" to "", "RequestQuote" to "", "Restaurant" to "", "Save" to "",
            "Savings" to "", "Schedule" to "", "School" to "", "Science" to "", "Search" to "",
            "Security" to "", "Sell" to "", "Send" to "", "SentimentSatisfied" to "",
            "Settings" to "", "Share" to "", "Shield" to "", "ShoppingBag" to "",
            "ShoppingCart" to "", "ShowChart" to "", "Shuffle" to "", "SkipNext" to "",
            "SkipPrevious" to "", "Smartphone" to "", "Sms" to "", "Spa" to "",
            "SportsEsports" to "", "Star" to "", "StarBorder" to "", "StarHalf" to "",
            "StarOutline" to "", "Stop" to "", "Storage" to "", "Store" to "", "Storefront" to "",
            "Sync" to "", "Tag" to "", "TextFields" to "", "Thermostat" to "", "ThumbDown" to "",
            "ThumbUp" to "", "Timeline" to "", "Timer" to "", "Title" to "", "Today" to "",
            "Translate" to "", "TrendingDown" to "", "TrendingUp" to "", "Tune" to "", "Tv" to "",
            "Umbrella" to "", "UnfoldLess" to "", "UnfoldMore" to "", "Upload" to "",
            "Verified" to "", "VerifiedUser" to "", "Videocam" to "", "ViewAgenda" to "",
            "ViewList" to "", "ViewModule" to "", "Visibility" to "", "VisibilityOff" to "",
            "VpnKey" to "", "Warning" to "", "Watch" to "", "WaterDrop" to "", "WbSunny" to "",
            "Whatshot" to "", "Widgets" to "", "Wifi" to "", "WifiOff" to "", "Work" to "",
            "ZoomIn" to "", "ZoomOut" to ""
        ),
        "DisplayMode" to mapOf(
            "Input" to "", "Picker" to ""
        ),
        "Dp" to mapOf(
            "div" to "other", "minus" to "other", "plus" to "other", "times" to "other",
            "toPx" to "", "unaryMinus" to "", "value" to ""
        ),
        "DrawerState" to mapOf(
            "close" to "", "currentValue" to "", "isClosed" to "", "isOpen" to "", "open" to ""
        ),
        "DrawerValue" to mapOf(
            "Closed" to "", "Open" to ""
        ),
        "ExposedDropdownMenuAnchorType" to mapOf(
            "PrimaryEditable" to "", "PrimaryNotEditable" to "", "SecondaryEditable" to ""
        ),
        "ExposedDropdownMenuDefaults" to mapOf(
            "TrailingIcon" to "expanded, modifier"
        ),
        "FabPosition" to mapOf(
            "Center" to "", "End" to "", "EndOverlay" to "", "Start" to ""
        ),
        "Filled" to mapOf(
            "AcUnit" to "", "AccessTime" to "", "AccountBalance" to "",
            "AccountBalanceWallet" to "", "AccountBox" to "", "AccountCircle" to "", "Add" to "",
            "AddCircle" to "", "AddShoppingCart" to "", "Alarm" to "", "AlternateEmail" to "",
            "Analytics" to "", "Apps" to "", "Archive" to "", "ArrowBack" to "",
            "ArrowDownward" to "", "ArrowDropDown" to "", "ArrowDropUp" to "",
            "ArrowForward" to "", "ArrowUpward" to "", "AttachFile" to "", "AttachMoney" to "",
            "Attachment" to "", "Autorenew" to "", "Badge" to "", "BarChart" to "",
            "BatteryFull" to "", "Block" to "", "Bluetooth" to "", "Bolt" to "", "Book" to "",
            "Bookmark" to "", "BookmarkBorder" to "", "Bookmarks" to "", "Brush" to "",
            "BugReport" to "", "Build" to "", "Cake" to "", "Calculate" to "",
            "CalendarMonth" to "", "CalendarToday" to "", "Call" to "", "CameraAlt" to "",
            "Cancel" to "", "CardGiftcard" to "", "Category" to "", "Celebration" to "",
            "Chat" to "", "Check" to "", "CheckCircle" to "", "CheckCircleOutline" to "",
            "ChevronLeft" to "", "ChevronRight" to "", "Circle" to "", "Clear" to "",
            "Close" to "", "Cloud" to "", "CloudDone" to "", "CloudDownload" to "",
            "CloudOff" to "", "CloudUpload" to "", "Code" to "", "ColorLens" to "",
            "Comment" to "", "Computer" to "", "Construction" to "", "Contacts" to "",
            "ContentCopy" to "", "ContentCut" to "", "ContentPaste" to "", "Create" to "",
            "CreateNewFolder" to "", "CreditCard" to "", "DarkMode" to "", "Dashboard" to "",
            "DateRange" to "", "Delete" to "", "Description" to "", "Directions" to "",
            "DirectionsCar" to "", "Done" to "", "DoneAll" to "", "Download" to "", "Drafts" to "",
            "DragHandle" to "", "DragIndicator" to "", "Eco" to "", "Edit" to "", "Email" to "",
            "EmojiEvents" to "", "Engineering" to "", "Error" to "", "ErrorOutline" to "",
            "Euro" to "", "Event" to "", "ExitToApp" to "", "ExpandLess" to "", "ExpandMore" to "",
            "Explore" to "", "Extension" to "", "Face" to "", "Favorite" to "",
            "FavoriteBorder" to "", "Feedback" to "", "FileDownload" to "", "FileUpload" to "",
            "Filter" to "", "FilterAlt" to "", "FilterList" to "", "Fingerprint" to "",
            "FitnessCenter" to "", "Flag" to "", "Flight" to "", "Folder" to "",
            "FolderOpen" to "", "FormatPaint" to "", "Forum" to "", "Fullscreen" to "",
            "Grade" to "", "GridView" to "", "Group" to "", "Groups" to "", "Handyman" to "",
            "Headphones" to "", "Help" to "", "History" to "", "Home" to "", "Hotel" to "",
            "HourglassEmpty" to "", "Image" to "", "Inbox" to "", "Info" to "",
            "InsertDriveFile" to "", "Insights" to "", "Inventory" to "", "Key" to "",
            "Keyboard" to "", "KeyboardArrowDown" to "", "KeyboardArrowLeft" to "",
            "KeyboardArrowRight" to "", "KeyboardArrowUp" to "", "Label" to "", "Language" to "",
            "Laptop" to "", "Launch" to "", "Layers" to "", "Leaderboard" to "", "LightMode" to "",
            "Lightbulb" to "", "Link" to "", "List" to "", "LocalCafe" to "",
            "LocalFireDepartment" to "", "LocalHospital" to "", "LocalMall" to "",
            "LocalOffer" to "", "LocalShipping" to "", "LocationOn" to "", "Lock" to "",
            "LockOpen" to "", "Login" to "", "Logout" to "", "Loyalty" to "", "Mail" to "",
            "Map" to "", "Memory" to "", "Menu" to "", "MenuOpen" to "", "Mic" to "",
            "MicOff" to "", "ModeEdit" to "", "MonetizationOn" to "", "Mood" to "",
            "MoreHoriz" to "", "MoreVert" to "", "Movie" to "", "MusicNote" to "",
            "MyLocation" to "", "Navigation" to "", "NearMe" to "", "Notifications" to "",
            "NotificationsActive" to "", "NotificationsNone" to "", "OpenInNew" to "",
            "Paid" to "", "Palette" to "", "Pause" to "", "PauseCircle" to "", "Payment" to "",
            "People" to "", "Person" to "", "PersonAdd" to "", "Pets" to "", "Phone" to "",
            "Photo" to "", "PhotoCamera" to "", "PhotoLibrary" to "", "PieChart" to "",
            "Place" to "", "PlayArrow" to "", "PlayCircle" to "", "PowerSettingsNew" to "",
            "Print" to "", "Public" to "", "QrCode" to "", "QrCodeScanner" to "", "Receipt" to "",
            "Redeem" to "", "Refresh" to "", "Remove" to "", "RemoveCircle" to "", "Repeat" to "",
            "Replay" to "", "Report" to "", "RequestQuote" to "", "Restaurant" to "", "Save" to "",
            "Savings" to "", "Schedule" to "", "School" to "", "Science" to "", "Search" to "",
            "Security" to "", "Sell" to "", "Send" to "", "SentimentSatisfied" to "",
            "Settings" to "", "Share" to "", "Shield" to "", "ShoppingBag" to "",
            "ShoppingCart" to "", "ShowChart" to "", "Shuffle" to "", "SkipNext" to "",
            "SkipPrevious" to "", "Smartphone" to "", "Sms" to "", "Spa" to "",
            "SportsEsports" to "", "Star" to "", "StarBorder" to "", "StarHalf" to "",
            "StarOutline" to "", "Stop" to "", "Storage" to "", "Store" to "", "Storefront" to "",
            "Sync" to "", "Tag" to "", "TextFields" to "", "Thermostat" to "", "ThumbDown" to "",
            "ThumbUp" to "", "Timeline" to "", "Timer" to "", "Title" to "", "Today" to "",
            "Translate" to "", "TrendingDown" to "", "TrendingUp" to "", "Tune" to "", "Tv" to "",
            "Umbrella" to "", "UnfoldLess" to "", "UnfoldMore" to "", "Upload" to "",
            "Verified" to "", "VerifiedUser" to "", "Videocam" to "", "ViewAgenda" to "",
            "ViewList" to "", "ViewModule" to "", "Visibility" to "", "VisibilityOff" to "",
            "VpnKey" to "", "Warning" to "", "Watch" to "", "WaterDrop" to "", "WbSunny" to "",
            "Whatshot" to "", "Widgets" to "", "Wifi" to "", "WifiOff" to "", "Work" to "",
            "ZoomIn" to "", "ZoomOut" to ""
        ),
        "FontFamily" to mapOf(
            "Cursive" to "", "Default" to "", "Monospace" to "", "SansSerif" to "", "Serif" to ""
        ),
        "FontStyle" to mapOf(
            "Italic" to "", "Normal" to ""
        ),
        "FontWeight" to mapOf(
            "Black" to "", "Bold" to "", "ExtraBold" to "", "Light" to "", "Medium" to "",
            "Normal" to "", "SemiBold" to "", "Thin" to ""
        ),
        "GridCells" to mapOf(
            "Adaptive" to "minSize", "Fixed" to "count"
        ),
        "Icons" to mapOf(
            "AutoMirrored" to "", "Default" to "", "Filled" to "", "Outlined" to ""
        ),
        "ImeAction" to mapOf(
            "Default" to "", "Done" to "", "Go" to "", "Next" to "", "None" to "",
            "Previous" to "", "Search" to "", "Send" to ""
        ),
        "Int" to mapOf(
            "MAX_VALUE" to "", "MIN_VALUE" to ""
        ),
        "IntOffset" to mapOf(
            "Zero" to "", "x" to "", "y" to ""
        ),
        "KeyboardCapitalization" to mapOf(
            "Characters" to "", "None" to "", "Sentences" to "", "Words" to ""
        ),
        "KeyboardOptions" to mapOf(
            "Default" to ""
        ),
        "KeyboardType" to mapOf(
            "Ascii" to "", "Decimal" to "", "Email" to "", "Number" to "", "NumberPassword" to "",
            "Password" to "", "Phone" to "", "Text" to "", "Uri" to ""
        ),
        "LazyListState" to mapOf(
            "animateScrollToItem" to "index, scrollOffset", "canScrollBackward" to "",
            "canScrollForward" to "", "firstVisibleItemIndex" to "",
            "firstVisibleItemScrollOffset" to "", "isScrollInProgress" to "",
            "scrollToItem" to "index, scrollOffset"
        ),
        "MaterialTheme" to mapOf(
            "colorScheme" to "", "shapes" to "", "typography" to ""
        ),
        "Modifier" to mapOf(
            "absoluteOffset" to "offset, x, y", "align" to "alignment", "alpha" to "alpha",
            "animateContentSize" to "", "animateItem" to "fadeInSpec, placementSpec, fadeOutSpec",
            "aspectRatio" to "ratio, matchHeightConstraintsFirst",
            "background" to "color, shape, brush, alpha", "blur" to "radius, radiusX, radiusY",
            "border" to "width, color, shape", "clearAndSetSemantics" to "properties",
            "clickable" to "enabled, onClickLabel, role, onClick", "clip" to "shape",
            "combinedClickable" to "enabled, onClickLabel, role, onLongClickLabel, onLongClick, onDoubleClick, onClick",
            "defaultMinSize" to "minWidth, minHeight",
            "draggable" to "state, orientation, enabled", "drawBehind" to "onDraw",
            "drawWithCache" to "onBuildDrawCache", "drawWithContent" to "onDraw",
            "fillMaxHeight" to "fraction", "fillMaxSize" to "fraction",
            "fillMaxWidth" to "fraction", "fillParentMaxHeight" to "fraction",
            "fillParentMaxWidth" to "fraction",
            "graphicsLayer" to "block, scaleX, scaleY, alpha, translationX, translationY, shadowElevation, rotationX, rotationY, rotationZ, cameraDistance, transformOrigin, shape, clip",
            "height" to "height", "heightIn" to "min, max",
            "horizontalScroll" to "state, enabled, reverseScrolling",
            "hoverable" to "interactionSource, enabled", "imePadding" to "",
            "indication" to "interactionSource, indication", "matchParentSize" to "",
            "menuAnchor" to "type, enabled", "navigationBarsPadding" to "",
            "nestedScroll" to "connection", "offset" to "offset, x, y",
            "onGloballyPositioned" to "onGloballyPositioned", "onSizeChanged" to "onSizeChanged",
            "padding" to "all, horizontal, vertical, paddingValues, start, top, end, bottom",
            "paint" to "painter, sizeToIntrinsics, alignment, contentScale, alpha, colorFilter",
            "pointerInput" to "keys, block", "requiredHeight" to "height",
            "requiredSize" to "size", "requiredWidth" to "width", "rotate" to "degrees",
            "safeContentPadding" to "", "safeDrawingPadding" to "", "scale" to "scale",
            "scrollable" to "state, orientation, enabled, reverseDirection",
            "selectable" to "selected, enabled, role, onClick", "selectableGroup" to "",
            "semantics" to "mergeDescendants, properties",
            "shadow" to "elevation, shape, clip, ambientColor, spotColor",
            "size" to "size, width, height",
            "sizeIn" to "minWidth, minHeight, maxWidth, maxHeight", "statusBarsPadding" to "",
            "systemBarsPadding" to "", "testTag" to "tag", "then" to "other",
            "toggleable" to "value, enabled, role, onValueChange",
            "verticalScroll" to "state, enabled, reverseScrolling", "weight" to "weight, fill",
            "width" to "width", "widthIn" to "min, max", "windowInsetsPadding" to "insets",
            "wrapContentHeight" to "", "wrapContentSize" to "", "wrapContentWidth" to "",
            "zIndex" to "zIndex"
        ),
        "Number" to mapOf(
            "dp" to "", "sp" to "", "times" to "other"
        ),
        "Offset" to mapOf(
            "copy" to "x, y", "div" to "operand", "minus" to "other", "plus" to "other",
            "times" to "operand", "unaryMinus" to "", "x" to "", "y" to "", "Infinite" to "",
            "Unspecified" to "", "Zero" to ""
        ),
        "Orientation" to mapOf(
            "Horizontal" to "", "Vertical" to ""
        ),
        "Outlined" to mapOf(
            "AcUnit" to "", "AccessTime" to "", "AccountBalance" to "",
            "AccountBalanceWallet" to "", "AccountBox" to "", "AccountCircle" to "", "Add" to "",
            "AddCircle" to "", "AddShoppingCart" to "", "Alarm" to "", "AlternateEmail" to "",
            "Analytics" to "", "Apps" to "", "Archive" to "", "ArrowBack" to "",
            "ArrowDownward" to "", "ArrowDropDown" to "", "ArrowDropUp" to "",
            "ArrowForward" to "", "ArrowUpward" to "", "AttachFile" to "", "AttachMoney" to "",
            "Attachment" to "", "Autorenew" to "", "Badge" to "", "BarChart" to "",
            "BatteryFull" to "", "Block" to "", "Bluetooth" to "", "Bolt" to "", "Book" to "",
            "Bookmark" to "", "BookmarkBorder" to "", "Bookmarks" to "", "Brush" to "",
            "BugReport" to "", "Build" to "", "Cake" to "", "Calculate" to "",
            "CalendarMonth" to "", "CalendarToday" to "", "Call" to "", "CameraAlt" to "",
            "Cancel" to "", "CardGiftcard" to "", "Category" to "", "Celebration" to "",
            "Chat" to "", "Check" to "", "CheckCircle" to "", "CheckCircleOutline" to "",
            "ChevronLeft" to "", "ChevronRight" to "", "Circle" to "", "Clear" to "",
            "Close" to "", "Cloud" to "", "CloudDone" to "", "CloudDownload" to "",
            "CloudOff" to "", "CloudUpload" to "", "Code" to "", "ColorLens" to "",
            "Comment" to "", "Computer" to "", "Construction" to "", "Contacts" to "",
            "ContentCopy" to "", "ContentCut" to "", "ContentPaste" to "", "Create" to "",
            "CreateNewFolder" to "", "CreditCard" to "", "DarkMode" to "", "Dashboard" to "",
            "DateRange" to "", "Delete" to "", "Description" to "", "Directions" to "",
            "DirectionsCar" to "", "Done" to "", "DoneAll" to "", "Download" to "", "Drafts" to "",
            "DragHandle" to "", "DragIndicator" to "", "Eco" to "", "Edit" to "", "Email" to "",
            "EmojiEvents" to "", "Engineering" to "", "Error" to "", "ErrorOutline" to "",
            "Euro" to "", "Event" to "", "ExitToApp" to "", "ExpandLess" to "", "ExpandMore" to "",
            "Explore" to "", "Extension" to "", "Face" to "", "Favorite" to "",
            "FavoriteBorder" to "", "Feedback" to "", "FileDownload" to "", "FileUpload" to "",
            "Filter" to "", "FilterAlt" to "", "FilterList" to "", "Fingerprint" to "",
            "FitnessCenter" to "", "Flag" to "", "Flight" to "", "Folder" to "",
            "FolderOpen" to "", "FormatPaint" to "", "Forum" to "", "Fullscreen" to "",
            "Grade" to "", "GridView" to "", "Group" to "", "Groups" to "", "Handyman" to "",
            "Headphones" to "", "Help" to "", "History" to "", "Home" to "", "Hotel" to "",
            "HourglassEmpty" to "", "Image" to "", "Inbox" to "", "Info" to "",
            "InsertDriveFile" to "", "Insights" to "", "Inventory" to "", "Key" to "",
            "Keyboard" to "", "KeyboardArrowDown" to "", "KeyboardArrowLeft" to "",
            "KeyboardArrowRight" to "", "KeyboardArrowUp" to "", "Label" to "", "Language" to "",
            "Laptop" to "", "Launch" to "", "Layers" to "", "Leaderboard" to "", "LightMode" to "",
            "Lightbulb" to "", "Link" to "", "List" to "", "LocalCafe" to "",
            "LocalFireDepartment" to "", "LocalHospital" to "", "LocalMall" to "",
            "LocalOffer" to "", "LocalShipping" to "", "LocationOn" to "", "Lock" to "",
            "LockOpen" to "", "Login" to "", "Logout" to "", "Loyalty" to "", "Mail" to "",
            "Map" to "", "Memory" to "", "Menu" to "", "MenuOpen" to "", "Mic" to "",
            "MicOff" to "", "ModeEdit" to "", "MonetizationOn" to "", "Mood" to "",
            "MoreHoriz" to "", "MoreVert" to "", "Movie" to "", "MusicNote" to "",
            "MyLocation" to "", "Navigation" to "", "NearMe" to "", "Notifications" to "",
            "NotificationsActive" to "", "NotificationsNone" to "", "OpenInNew" to "",
            "Paid" to "", "Palette" to "", "Pause" to "", "PauseCircle" to "", "Payment" to "",
            "People" to "", "Person" to "", "PersonAdd" to "", "Pets" to "", "Phone" to "",
            "Photo" to "", "PhotoCamera" to "", "PhotoLibrary" to "", "PieChart" to "",
            "Place" to "", "PlayArrow" to "", "PlayCircle" to "", "PowerSettingsNew" to "",
            "Print" to "", "Public" to "", "QrCode" to "", "QrCodeScanner" to "", "Receipt" to "",
            "Redeem" to "", "Refresh" to "", "Remove" to "", "RemoveCircle" to "", "Repeat" to "",
            "Replay" to "", "Report" to "", "RequestQuote" to "", "Restaurant" to "", "Save" to "",
            "Savings" to "", "Schedule" to "", "School" to "", "Science" to "", "Search" to "",
            "Security" to "", "Sell" to "", "Send" to "", "SentimentSatisfied" to "",
            "Settings" to "", "Share" to "", "Shield" to "", "ShoppingBag" to "",
            "ShoppingCart" to "", "ShowChart" to "", "Shuffle" to "", "SkipNext" to "",
            "SkipPrevious" to "", "Smartphone" to "", "Sms" to "", "Spa" to "",
            "SportsEsports" to "", "Star" to "", "StarBorder" to "", "StarHalf" to "",
            "StarOutline" to "", "Stop" to "", "Storage" to "", "Store" to "", "Storefront" to "",
            "Sync" to "", "Tag" to "", "TextFields" to "", "Thermostat" to "", "ThumbDown" to "",
            "ThumbUp" to "", "Timeline" to "", "Timer" to "", "Title" to "", "Today" to "",
            "Translate" to "", "TrendingDown" to "", "TrendingUp" to "", "Tune" to "", "Tv" to "",
            "Umbrella" to "", "UnfoldLess" to "", "UnfoldMore" to "", "Upload" to "",
            "Verified" to "", "VerifiedUser" to "", "Videocam" to "", "ViewAgenda" to "",
            "ViewList" to "", "ViewModule" to "", "Visibility" to "", "VisibilityOff" to "",
            "VpnKey" to "", "Warning" to "", "Watch" to "", "WaterDrop" to "", "WbSunny" to "",
            "Whatshot" to "", "Widgets" to "", "Wifi" to "", "WifiOff" to "", "Work" to "",
            "ZoomIn" to "", "ZoomOut" to ""
        ),
        "OutlinedTextFieldDefaults" to mapOf(
            "colors" to "focusedTextColor, unfocusedTextColor, focusedContainerColor, unfocusedContainerColor, cursorColor, focusedBorderColor, unfocusedBorderColor, focusedLabelColor, unfocusedLabelColor"
        ),
        "PageSize" to mapOf(
            "Fill" to "", "Fixed" to "pageSize"
        ),
        "PagerState" to mapOf(
            "animateScrollToPage" to "page, pageOffsetFraction", "currentPage" to "",
            "currentPageOffsetFraction" to "", "isScrollInProgress" to "", "pageCount" to "",
            "scrollToPage" to "page, pageOffsetFraction", "settledPage" to "", "targetPage" to ""
        ),
        "PathEffect" to mapOf(
            "cornerPathEffect" to "radius", "dashPathEffect" to "intervals, phase"
        ),
        "RepeatMode" to mapOf(
            "Restart" to "", "Reverse" to ""
        ),
        "Role" to mapOf(
            "Button" to "", "Checkbox" to "", "DropdownList" to "", "Image" to "",
            "RadioButton" to "", "Switch" to "", "Tab" to ""
        ),
        "ScrollState" to mapOf(
            "animateScrollTo" to "value", "isScrollInProgress" to "", "maxValue" to "",
            "scrollTo" to "value", "value" to ""
        ),
        "SearchBarDefaults" to mapOf(
            "InputField" to "query, onQueryChange, onSearch, expanded, onExpandedChange, modifier, enabled, placeholder, leadingIcon, trailingIcon"
        ),
        "SegmentedButtonDefaults" to mapOf(
            "itemShape" to "index, count, baseShape"
        ),
        "Shapes" to mapOf(
            "extraLarge" to "", "extraSmall" to "", "large" to "", "medium" to "", "small" to ""
        ),
        "SheetState" to mapOf(
            "currentValue" to "", "expand" to "", "hide" to "", "isVisible" to "",
            "partialExpand" to "", "show" to ""
        ),
        "SheetValue" to mapOf(
            "Expanded" to "", "Hidden" to "", "PartiallyExpanded" to ""
        ),
        "Size" to mapOf(
            "div" to "operand", "height" to "", "maxDimension" to "", "minDimension" to "",
            "times" to "operand", "width" to "", "Zero" to ""
        ),
        "SnackbarDuration" to mapOf(
            "Indefinite" to "", "Long" to "", "Short" to ""
        ),
        "SnackbarHostState" to mapOf(
            "showSnackbar" to "message, actionLabel, withDismissAction, duration"
        ),
        "SnackbarResult" to mapOf(
            "ActionPerformed" to "", "Dismissed" to ""
        ),
        "Spring" to mapOf(
            "DampingRatioHighBouncy" to "", "DampingRatioLowBouncy" to "",
            "DampingRatioMediumBouncy" to "", "DampingRatioNoBouncy" to "", "StiffnessHigh" to "",
            "StiffnessLow" to "", "StiffnessMedium" to "", "StiffnessMediumLow" to "",
            "StiffnessVeryLow" to ""
        ),
        "StaggeredGridCells" to mapOf(
            "Adaptive" to "minSize", "Fixed" to "count"
        ),
        "StaggeredGridItemSpan" to mapOf(
            "FullLine" to "", "SingleLane" to ""
        ),
        "StrokeCap" to mapOf(
            "Butt" to "", "Round" to "", "Square" to ""
        ),
        "StrokeJoin" to mapOf(
            "Bevel" to "", "Miter" to "", "Round" to ""
        ),
        "TextAlign" to mapOf(
            "Center" to "", "End" to "", "Justify" to "", "Left" to "", "Right" to "",
            "Start" to ""
        ),
        "TextDecoration" to mapOf(
            "LineThrough" to "", "None" to "", "Underline" to ""
        ),
        "TextFieldDefaults" to mapOf(
            "colors" to "focusedTextColor, unfocusedTextColor, focusedContainerColor, unfocusedContainerColor, cursorColor, focusedIndicatorColor, unfocusedIndicatorColor, focusedLabelColor, unfocusedLabelColor"
        ),
        "TextOverflow" to mapOf(
            "Clip" to "", "Ellipsis" to "", "Visible" to ""
        ),
        "TextStyle" to mapOf(
            "copy" to "color, fontSize, fontWeight", "merge" to "other", "Default" to ""
        ),
        "TileMode" to mapOf(
            "Clamp" to "", "Decal" to "", "Mirror" to "", "Repeated" to ""
        ),
        "ToggleableState" to mapOf(
            "Indeterminate" to "", "Off" to "", "On" to ""
        ),
        "TooltipAnchorPosition" to mapOf(
            "Above" to "", "Below" to "", "End" to "", "Left" to "", "Right" to "", "Start" to ""
        ),
        "TooltipDefaults" to mapOf(
            "rememberPlainTooltipPositionProvider" to "spacingBetweenTooltipAndAnchor",
            "rememberTooltipPositionProvider" to "positioning, spacingBetweenTooltipAndAnchor"
        ),
        "TooltipState" to mapOf(
            "dismiss" to "", "isVisible" to "", "show" to ""
        ),
        "TopAppBarDefaults" to mapOf(
            "enterAlwaysScrollBehavior" to "", "exitUntilCollapsedScrollBehavior" to "",
            "pinnedScrollBehavior" to "",
            "topAppBarColors" to "containerColor, scrolledContainerColor, navigationIconContentColor, titleContentColor, actionIconContentColor"
        ),
        "TransformOrigin" to mapOf(
            "Center" to ""
        ),
        "Typography" to mapOf(
            "bodyLarge" to "", "bodyMedium" to "", "bodySmall" to "", "displayLarge" to "",
            "displayMedium" to "", "displaySmall" to "", "headlineLarge" to "",
            "headlineMedium" to "", "headlineSmall" to "", "labelLarge" to "", "labelMedium" to "",
            "labelSmall" to "", "titleLarge" to "", "titleMedium" to "", "titleSmall" to ""
        ),
        "VisualTransformation" to mapOf(
            "None" to ""
        ),
        "WindowInsets" to mapOf(
            "ime" to "", "navigationBars" to "", "safeContent" to "", "safeDrawing" to "",
            "statusBars" to "", "systemBars" to ""
        )
    )

    /** Members of lambda scopes, such as `items` in `LazyColumn`, keyed by scope. */
    val scopeMembers: Map<String, Map<String, String>> = mapOf(
        "AnnotatedString.Builder" to mapOf(
            "append" to "text", "length" to "", "pop" to "", "pushStyle" to "style",
            "withStyle" to "style, block"
        ),
        "BoxWithConstraintsScope" to mapOf(
            "constraints" to "", "maxHeight" to "", "maxWidth" to "", "minHeight" to "",
            "minWidth" to ""
        ),
        "CacheDrawScope" to mapOf(
            "onDrawBehind" to "block", "onDrawWithContent" to "block", "size" to ""
        ),
        "ContentDrawScope" to mapOf(
            "drawContent" to ""
        ),
        "DisposableEffectScope" to mapOf(
            "onDispose" to "onDisposeEffect"
        ),
        "DrawScope" to mapOf(
            "center" to "",
            "drawArc" to "color, startAngle, sweepAngle, useCenter, topLeft, size, alpha, style, colorFilter, blendMode, brush",
            "drawCircle" to "color, radius, center, alpha, style, colorFilter, blendMode, brush",
            "drawLine" to "color, start, end, strokeWidth, cap, pathEffect, alpha, colorFilter, blendMode, brush",
            "drawOval" to "color, topLeft, size, alpha, style, colorFilter, blendMode, brush",
            "drawPath" to "path, color, alpha, style, colorFilter, blendMode, brush",
            "drawRect" to "color, topLeft, size, alpha, style, colorFilter, blendMode, brush",
            "drawRoundRect" to "color, topLeft, size, cornerRadius, style, alpha, colorFilter, blendMode, brush",
            "inset" to "horizontal, vertical, block", "rotate" to "degrees, pivot, block",
            "scale" to "scale, pivot, block", "size" to "", "translate" to "left, top, block"
        ),
        "GraphicsLayerScope" to mapOf(
            "alpha" to "", "cameraDistance" to "", "clip" to "", "rotationX" to "",
            "rotationY" to "", "rotationZ" to "", "scaleX" to "", "scaleY" to "",
            "shadowElevation" to "", "shape" to "", "size" to "", "transformOrigin" to "",
            "translationX" to "", "translationY" to ""
        ),
        "LazyGridItemSpanScope" to mapOf(
            "maxCurrentLineSpan" to "", "maxLineSpan" to ""
        ),
        "LazyGridScope" to mapOf(
            "item" to "key, span, contentType, content",
            "items" to "items, key, span, contentType, itemContent, count"
        ),
        "LazyListScope" to mapOf(
            "item" to "key, contentType, content",
            "items" to "items, key, contentType, itemContent, count",
            "itemsIndexed" to "items, key, contentType, itemContent",
            "stickyHeader" to "key, contentType, content"
        ),
        "LazyStaggeredGridScope" to mapOf(
            "item" to "key, contentType, span, content",
            "items" to "items, key, contentType, span, itemContent, count"
        ),
        "MeasureScope" to mapOf(
            "layout" to "width, height, alignmentLines, placementBlock"
        ),
        "PointerInputScope" to mapOf(
            "detectDragGestures" to "onDragStart, onDragEnd, onDragCancel, onDrag",
            "detectHorizontalDragGestures" to "onDragStart, onDragEnd, onDragCancel, onHorizontalDrag",
            "detectTapGestures" to "onDoubleTap, onLongPress, onPress, onTap",
            "detectVerticalDragGestures" to "onDragStart, onDragEnd, onDragCancel, onVerticalDrag"
        )
    )
}
