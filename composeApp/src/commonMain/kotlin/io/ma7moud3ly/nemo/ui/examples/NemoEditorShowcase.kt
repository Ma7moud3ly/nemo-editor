import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.ma7moud3ly.nemo.NemoCodeEditor
import io.ma7moud3ly.nemo.model.EditorSettings
import io.ma7moud3ly.nemo.model.EditorThemes
import io.ma7moud3ly.nemo.model.Language
import io.ma7moud3ly.nemo.model.rememberCodeState
import nemoeditor.composeapp.generated.resources.Res
import nemoeditor.composeapp.generated.resources.logo
import org.jetbrains.compose.resources.painterResource
import androidx.compose.ui.tooling.preview.Preview

@Preview(widthDp = 1000, heightDp = 1030)
@Composable
private fun NemoEditorPreview() {
    NemoEditorShowcaseScreen()
}


@Composable
fun NemoEditorShowcaseScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1a1a2e))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(Res.drawable.logo),
                contentDescription = "Nemo Logo",
                modifier = Modifier.width(100.dp)
            )
            Text(
                text = "Nemo Code Editor",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 38.sp
                ),
                color = Color(EditorThemes.NEMO_DARK.syntax.keyword),
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Text(
            text = "A powerful, cross-platform code editor/viewer component built with Compose Multiplatform, featuring syntax highlighting, code formatting, and advanced editing capabilities",
            style = MaterialTheme.typography.titleLarge,
            color = Color(0xFFb8b8d1),
            textAlign = TextAlign.Justify,
            modifier = Modifier
                .padding(bottom = 32.dp)
                .fillMaxWidth(0.8f)
        )

        InstallationSection(modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        // Grid of examples using LazyVerticalGrid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item { ExampleKotlin() }
            item { ExampleJava() }
            item { ExamplePython() }
            item { ExampleJavaScript() }
        }
    }
}

@Composable
private fun InstallationSection(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Installation Card
        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF16213e)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF0d1117)
            ) {
                Column(
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(0xFFc9d1d9)
                                )
                            ) {
                                append("implementation(")
                            }
                            withStyle(
                                style = SpanStyle(
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(0xFF79c0ff)
                                )
                            ) {
                                append("\"io.github.ma7moud3ly:nemo-editor:1.0.2\"")
                            }
                            withStyle(
                                style = SpanStyle(
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(0xFFc9d1d9)
                                )
                            ) {
                                append(")")
                            }
                        },
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ExamplePython() {
    val codeState = rememberCodeState(
        code = """def fibonacci(n):
    if n <= 1:
        return n
    return fibonacci(n-1) + fibonacci(n-2)

# Generate sequence
for i in range(8):
    print(f"F({i}) = {fibonacci(i)}")""",
        language = Language.PYTHON
    )

    val editorSettings = remember {
        EditorSettings(
            theme = EditorThemes.DRACULA,
            showLineNumbers = false,
            readOnly = false,
            tabSize = 4
        )
    }

    EditorExampleCard(
        title = "3. Python",
        subtitle = "DRACULA Theme"
    ) {
        NemoCodeEditor(
            state = codeState,
            settings = editorSettings,
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
        )
    }
}

@Composable
private fun ExampleJavaScript() {
    val codeState = rememberCodeState(
        code = """const fetchData = async (id) => {
  try {
    const response = await fetch(
      `https://api.example.com/user/id`
    );
    const data = await response.json();
    return data;
  } catch (error) {
    console.error('Error:', error);
  }
};""",
        language = Language.JAVASCRIPT
    )

    val editorSettings = remember {
        EditorSettings(
            theme = EditorThemes.CANDY,
            showLineNumbers = false,
            readOnly = true,
            tabSize = 2
        )
    }

    EditorExampleCard(
        title = "4. JavaScript",
        subtitle = "CANDY theme"
    ) {
        NemoCodeEditor(
            state = codeState,
            settings = editorSettings,
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
        )
    }
}

@Composable
private fun ExampleKotlin() {
    val codeState = rememberCodeState(
        code = """data class User(
    val id: Int,
    val name: String,
    val email: String
)

fun processUsers(users: List<User>) {
    users.filter { it.email.contains("@") }
        .forEach { println(it.name) }
}""",
        language = Language.KOTLIN
    )

    val editorSettings = remember {
        EditorSettings(
            theme = EditorThemes.NEMO_DARK,
            showLineNumbers = true,
            readOnly = false,
            tabSize = 4
        )
    }

    EditorExampleCard(
        title = "1. Kotlin",
        subtitle = "NEMO DARK theme"
    ) {
        NemoCodeEditor(
            state = codeState,
            settings = editorSettings,
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
        )
    }
}

@Composable
private fun ExampleJava() {
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

    val editorSettings = remember {
        EditorSettings(
            theme = EditorThemes.VS_CODE_DARK,
            showLineNumbers = true,
            readOnly = false,
            tabSize = 4
        )
    }

    EditorExampleCard(
        title = "2. Java",
        subtitle = "VS CODE DARK Theme"
    ) {
        NemoCodeEditor(
            state = codeState,
            settings = editorSettings,
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
        )
    }
}

@Composable
private fun EditorExampleCard(
    title: String,
    subtitle: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF16213e)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Color.White,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 16.dp, start = 8.dp)
            )
            content()
        }
    }
}