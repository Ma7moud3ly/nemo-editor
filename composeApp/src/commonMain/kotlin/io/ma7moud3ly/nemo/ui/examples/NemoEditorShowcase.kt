package io.ma7moud3ly.nemo.ui.examples

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.withStyle
import io.ma7moud3ly.nemo.NemoCodeEditor
import io.ma7moud3ly.nemo.model.CodeState
import io.ma7moud3ly.nemo.model.EditorSettings
import io.ma7moud3ly.nemo.model.EditorThemes
import io.ma7moud3ly.nemo.model.Language
import io.ma7moud3ly.nemo.model.rememberCodeState
import nemoeditor.composeapp.generated.resources.Res
import nemoeditor.composeapp.generated.resources.logo
import org.jetbrains.compose.resources.painterResource
import androidx.compose.ui.tooling.preview.Preview
import io.ma7moud3ly.nemo.ui.nemoApp.topBar.NemoAnimatedLogo

/* Light palette. Ink is the product's deep-ocean colour used as text on a warm-white page. */
private val PageTop = Color(0xFFFFFDFB)
private val PageBottom = Color(0xFFF1F4F9)
private val CardBg = Color(0xFFFFFFFF)
private val CardBorder = Color(0xFFE4E9F0)
private val BandBg = Color(0xFFFFF4ED)      // warm tint for the get-started band
private val ChipBg = Color(0xFFEEF1F6)
private val Ink = Color(0xFF1A2332)          // NEMO_DARK background, now the ink
private val NemoOrange = Color(0xFFFF6B35)
private val Teal = Color(0xFF0E9E92)         // legible turquoise on light

private fun ink(alpha: Float) = Ink.copy(alpha = alpha)

private val MiniEditorHeight = 80.dp
private val FullEditorHeight = 264.dp
private val UsageEditorHeight = 200.dp
private val OutputEditorHeight = 112.dp
private const val FeatureFontSize = 12
private const val UsageFontSize = 14
private const val OutputFontSize = 13

@Preview(widthDp = 1000, heightDp = 1180)
@Composable
private fun NemoEditorPreview() {
    NemoEditorShowcaseScreen()
}

@Composable
fun NemoEditorShowcaseScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(PageTop, PageBottom)))
            .padding(horizontal = 28.dp, vertical = 22.dp)
    ) {
        ShowcaseHeader()
        Spacer(modifier = Modifier.height(22.dp))

        UsageSection()
        Spacer(modifier = Modifier.height(22.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item { FeatureMultipleLanguages() }
            item { FeatureMultipleThemes() }
            item { FeatureGutter() }
            item { FeatureNestedCode() }
        }
    }
}

/* ----------------------------- Header ----------------------------- */

@Composable
private fun ShowcaseHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(Res.drawable.logo),
            contentDescription = "Nemo logo",
            modifier = Modifier.size(60.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = "Nemo Code Editor",
                fontSize = 30.sp,
                lineHeight = 34.sp,
                fontWeight = FontWeight.Bold,
                color = NemoOrange
            )
            Text(
                text = "A code editor for Kotlin Multiplatform.",
                style = MaterialTheme.typography.bodyMedium,
                color = ink(0.65f)
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        DependencySnippet()
    }
}

@Composable
private fun DependencySnippet() {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = ChipBg,
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(fontFamily = FontFamily.Monospace, color = ink(0.8f))) {
                    append("implementation(")
                }
                withStyle(SpanStyle(fontFamily = FontFamily.Monospace, color = NemoOrange)) {
                    append("\"io.github.ma7moud3ly:nemo-editor:<version>\"")
                }
                withStyle(SpanStyle(fontFamily = FontFamily.Monospace, color = ink(0.8f))) {
                    append(")")
                }
            },
            fontSize = 13.sp,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 11.dp)
        )
    }
}

/* --------------------------- Usage → Output ---------------------- */
/* Left: the call, at full size. Right: the result, in a small framed preview window. */

@Composable
private fun UsageSection() {
    val usageState = rememberCodeState(
        code = """@Composable
fun MyEditor() {
    val codeState = rememberCodeState(
        code = "fun main() {\nprintln(\"Hello\")\n}",
        language = Language.KOTLIN
    )
    NemoCodeEditor(state = codeState)
}""",
        language = Language.KOTLIN
    )
    val usageSettings = remember {
        EditorSettings(
            theme = EditorThemes.VS_CODE_LIGHT,
            fontSize = UsageFontSize,
            showLineNumbers = false,
            showIndentGuides = true,
            readOnly = true,
            tabSize = 4
        )
    }

    val outputState = rememberCodeState(
        code = """fun main() {
    println("Hello")
}""",
        language = Language.KOTLIN
    )
    val outputSettings = remember {
        EditorSettings(
            theme = EditorThemes.NEMO_LIGHT,
            fontSize = OutputFontSize,
            showLineNumbers = false,
            showIndentGuides = true,
            readOnly = false,
            tabSize = 4
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = BandBg),
        border = BorderStroke(1.dp, NemoOrange.copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(22.dp)) {
            Text(
                text = "Get started",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Ink
            )
            Text(
                text = "Two lines to a working editor",
                style = MaterialTheme.typography.bodySmall,
                color = ink(0.6f),
                modifier = Modifier.padding(top = 2.dp, bottom = 18.dp)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                // The call, full-size.
                Column(modifier = Modifier.weight(1.55f)) {
                    PaneLabel("Use it in Compose", ink(0.7f))
                    NemoCodeEditor(
                        state = usageState,
                        settings = usageSettings,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(UsageEditorHeight)
                    )
                }
                Spacer(modifier = Modifier.width(28.dp))
                // The result, small and framed to highlight it.
                Column(modifier = Modifier.weight(1f)) {
                    PaneLabel("Renders as", Teal)
                    OutputPreviewWindow {
                        NemoCodeEditor(
                            state = outputState,
                            settings = outputSettings,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(OutputEditorHeight)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PaneLabel(text: String, color: Color) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = color,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

/** A small window frame — title-bar dots + the rendered editor — so the output reads as a result. */
@Composable
private fun OutputPreviewWindow(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = CardBg,
        border = BorderStroke(1.dp, NemoOrange.copy(alpha = 0.45f)),
        shadowElevation = 10.dp
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 9.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Dot(NemoOrange.copy(alpha = 0.85f))
                Dot(Color(0xFFFFC15E))
                Dot(Teal.copy(alpha = 0.85f))
            }
            HorizontalDivider(color = CardBorder)
            content()
        }
    }
}

@Composable
private fun Dot(color: Color) {
    Box(
        modifier = Modifier
            .size(9.dp)
            .background(color = color, shape = CircleShape)
    )
}

/* --------------------------- Feature 1 ---------------------------- */
/* Multiple languages: one component, different grammars. */

@Composable
private fun FeatureMultipleLanguages() {
    val kotlin = rememberCodeState(
        code = """fun greet(name: String): String {
    return "Hello, ${'$'}name!"
}""",
        language = Language.KOTLIN
    )
    val python = rememberCodeState(
        code = """def greet(name):
    return f"Hello, {name}!"
""",
        language = Language.PYTHON
    )

    val settings = remember {
        EditorSettings(
            theme = EditorThemes.NEMO_LIGHT,
            fontSize = FeatureFontSize,
            showLineNumbers = false,
            showIndentGuides = true,
            readOnly = true,
            tabSize = 4
        )
    }

    FeatureCard(
        title = "Multiple languages",
        subtitle = "Kotlin, Java, Python, JavaScript, and more"
    ) {
        MiniEditor(kotlin, settings)
        Spacer(modifier = Modifier.height(10.dp))
        MiniEditor(python, settings)
    }
}

/* --------------------------- Feature 2 ---------------------------- */
/* Multiple themes: the same snippet, one light and one dark, to show the range. */

@Composable
private fun FeatureMultipleThemes() {
    val lightSnippet = rememberCodeState(
        code = """const sum = (a, b) => a + b;
console.log(sum(2, 3));""",
        language = Language.JAVASCRIPT
    )
    val darkSnippet = rememberCodeState(
        code = """const sum = (a, b) => a + b;
console.log(sum(2, 3));""",
        language = Language.JAVASCRIPT
    )

    val lightSettings = remember {
        EditorSettings(
            theme = EditorThemes.GITHUB_LIGHT,
            fontSize = FeatureFontSize,
            showLineNumbers = false,
            readOnly = true,
            tabSize = 2
        )
    }
    val lightSettings2 = remember {
        EditorSettings(
            theme = EditorThemes.SOLARIZED_LIGHT,
            fontSize = FeatureFontSize,
            showLineNumbers = false,
            readOnly = true,
            tabSize = 2
        )
    }

    FeatureCard(
        title = "Multiple themes",
        subtitle = "GitHub Light, Dracula, Nord, Solarized, and more"
    ) {
        MiniEditor(lightSnippet, lightSettings)
        Spacer(modifier = Modifier.height(10.dp))
        MiniEditor(darkSnippet, lightSettings2)
    }
}

/* --------------------------- Feature 3 ---------------------------- */
/* Configurable gutter + read-only viewing mode. */

@Composable
private fun FeatureGutter() {
    val codeState = rememberCodeState(
        code = """public class Calculator {
    private int result = 0;

    public void add(int value) {
        result += value;
    }

    public int getResult() {
        return result;
    }
}""",
        language = Language.JAVA
    )

    val settings = remember {
        EditorSettings(
            theme = EditorThemes.SOLARIZED_LIGHT,
            fontSize = FeatureFontSize,
            showLineNumbers = true,
            showIndentGuides = true,
            readOnly = true,
            tabSize = 4
        )
    }

    FeatureCard(
        title = "Line numbers & read-only",
        subtitle = "Toggle the gutter, lock editing for viewing"
    ) {
        NemoCodeEditor(
            state = codeState,
            settings = settings,
            modifier = Modifier
                .fillMaxWidth()
                .height(FullEditorHeight)
        )
    }
}

/* --------------------------- Feature 4 ---------------------------- */
/* Indent guides — the flagship: guide lines that track every open scope. */

@Composable
private fun FeatureNestedCode() {
    val codeState = rememberCodeState(
        code = """fun sum(node: Node): Int {
    return when (node) {
        is Node.Leaf -> node.value
        is Node.Group -> {
            node.children.sumOf { child ->
                when (child) {
                    is Node.Leaf -> {
                        if (child.value > 0) {
                            child.value
                        } else {
                            0
                        }
                    }

                    else -> sum(child)
                }
            }
        }
    }
}""",
        language = Language.KOTLIN
    )

    val settings = remember {
        EditorSettings(
            theme = EditorThemes.VS_CODE_LIGHT,
            fontSize = 10,
            showLineNumbers = true,
            showIndentGuides = true,
            readOnly = false,
            tabSize = 4
        )
    }

    FeatureCard(
        title = "Indent guides",
        subtitle = "Guide lines track every nested scope",
        accent = true
    ) {
        NemoCodeEditor(
            state = codeState,
            settings = settings,
            modifier = Modifier
                .fillMaxWidth()
                .height(FullEditorHeight)
        )
    }
}

/* ----------------------------- Shared ----------------------------- */

@Composable
private fun MiniEditor(
    state: CodeState,
    settings: EditorSettings
) {
    NemoCodeEditor(
        state = state,
        settings = settings,
        modifier = Modifier
            .fillMaxWidth()
            .height(MiniEditorHeight)
    )
}

@Composable
private fun FeatureCard(
    title: String,
    subtitle: String,
    accent: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = BorderStroke(1.dp, if (accent) NemoOrange.copy(alpha = 0.55f) else CardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Ink
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = ink(0.55f),
                modifier = Modifier.padding(top = 2.dp, bottom = 14.dp)
            )
            content()
        }
    }
}