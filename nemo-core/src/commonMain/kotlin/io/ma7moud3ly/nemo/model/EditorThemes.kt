package io.ma7moud3ly.nemo.model

object EditorThemes {

    /**
     * Nemo Light Theme
     * Inspired by the vibrant coral reef and Nemo's orange and white stripes
     * Warm, inviting colors with excellent readability
     */
    val NEMO_LIGHT = EditorTheme(
        name = "Nemo Light",
        dark = false,
        background = 0xFFFFF8F0,        // Soft coral white
        foreground = 0xFF2C3E50,        // Deep ocean blue-gray
        currentLineBackground = 0xFFFFEFDB, // Light peachy highlight
        selection = 0xFFFFD4A3,         // Warm sandy selection
        lineNumber = 0xFF95A5A6,        // Soft gray
        lineNumberActive = 0xFFE67E22,  // Nemo orange
        gutter = 0xFFFFF4E6,            // Very light coral
        syntax = SyntaxColors(
            keyword = 0xFFFF6B35,       // Nemo orange - vibrant and friendly
            string = 0xFF16A085,        // Sea green - calm and natural
            comment = 0xFF95A5A6,       // Soft gray - subtle but readable
            number = 0xFFD35400,        // Darker orange - stands out
            function = 0xFF2980B9,      // Ocean blue - trustworthy
            type = 0xFFC0392B,          // Coral red - distinctive
            variable = 0xFF34495E,      // Dark slate - clear contrast
            operator = 0xFF7F8C8D       // Medium gray - balanced
        )
    )

    /**
     * Nemo Dark Theme
     * Inspired by the deep ocean at night with bioluminescent accents
     * Eye-comfortable dark theme with pops of coral and ocean colors
     */
    val NEMO_DARK = EditorTheme(
        name = "Nemo Dark",
        dark = true,
        background = 0xFF1A2332,        // Deep ocean blue
        foreground = 0xFFE8F4F8,        // Light seafoam white
        currentLineBackground = 0xFF243447, // Slightly lighter ocean
        selection = 0xFF2C4A62,         // Muted blue selection
        lineNumber = 0xFF5A7A8F,        // Soft slate blue
        lineNumberActive = 0xFFFF9F5A,  // Bright Nemo orange
        gutter = 0xFF15202E,            // Darker ocean depth
        syntax = SyntaxColors(
            keyword = 0xFFFF6B35,       // Nemo orange - playful glow
            string = 0xFF4ECDC4,        // Turquoise cyan - refreshing
            comment = 0xFF6B8FA3,       // Muted blue-gray - easy on eyes
            number = 0xFFFFA07A,        // Light coral - warm accent
            function = 0xFF5DADE2,      // Bright ocean blue - clear
            type = 0xFFEC7063,          // Soft coral pink - gentle contrast
            variable = 0xFFA8D8EA,      // Pale blue - easy to read
            operator = 0xFF8FA3B0       // Soft blue-gray - subtle
        )
    )

    /**
     * Nemo Sunset Theme (Bonus)
     * Inspired by the golden hour over the ocean
     * Warm, dreamy colors perfect for evening coding
     */
    val NEMO_SUNSET = EditorTheme(
        name = "Nemo Sunset",
        dark = true,
        background = 0xFF2B3E50,        // Twilight blue
        foreground = 0xFFFFF5E1,        // Cream white
        currentLineBackground = 0xFF34495E, // Deeper twilight
        selection = 0xFF5D4E37,         // Warm brown
        lineNumber = 0xFF8B9A99,        // Dusty blue
        lineNumberActive = 0xFFFFAA5C,  // Sunset orange
        gutter = 0xFF243342,            // Dark twilight
        syntax = SyntaxColors(
            keyword = 0xFFFFAA5C,       // Sunset orange - warm glow
            string = 0xFFFFD89B,        // Golden yellow - radiant
            comment = 0xFF7C98AC,       // Soft blue - calm
            number = 0xFFFF8C6B,        // Peachy orange - soft
            function = 0xFF81C1E5,      // Sky blue - serene
            type = 0xFFFF9AA2,          // Pink coral - gentle
            variable = 0xFFDDE7F2,      // Pale blue-white - clear
            operator = 0xFF9FADB6       // Cool gray - balanced
        )
    )


    // VS Code Dark Theme
    val VS_CODE_DARK = EditorTheme(
        name = "VS Code Dark",
        dark = true,
        background = 0xFF1E1E1E,
        foreground = 0xFFD4D4D4,
        currentLineBackground = 0xFF2A2A2A,
        selection = 0xFF264F78,
        lineNumber = 0xFF858585,
        lineNumberActive = 0xFFC6C6C6,
        gutter = 0xFF252526,
        syntax = SyntaxColors(
            keyword = 0xFF569CD6,      // Blue
            string = 0xFFCE9178,       // Orange
            comment = 0xFF6A9955,      // Green
            number = 0xFFB5CEA8,       // Light green
            function = 0xFFDCDCAA,     // Yellow
            type = 0xFF4EC9B0,         // Cyan
            variable = 0xFF9CDCFE,     // Light blue
            operator = 0xFFD4D4D4      // White
        )
    )

    // VS Code Light Theme
    val VS_CODE_LIGHT = EditorTheme(
        name = "VS Code Light",
        dark = false,
        background = 0xFFFFFFFF,
        foreground = 0xFF000000,
        currentLineBackground = 0xFFF0F0F0,
        selection = 0xFFADD6FF,
        lineNumber = 0xFF237893,
        lineNumberActive = 0xFF0B216F,
        gutter = 0xFFF5F5F5,
        syntax = SyntaxColors(
            keyword = 0xFF0000FF,      // Blue
            string = 0xFFA31515,       // Red
            comment = 0xFF008000,      // Green
            number = 0xFF098658,       // Dark green
            function = 0xFF795E26,     // Brown
            type = 0xFF267F99,         // Teal
            variable = 0xFF001080,     // Dark blue
            operator = 0xFF000000      // Black
        )
    )

    // Monokai Theme
    val MONOKAI = EditorTheme(
        name = "Monokai",
        dark = true,
        background = 0xFF272822,
        foreground = 0xFFF8F8F2,
        currentLineBackground = 0xFF3E3D32,
        selection = 0xFF49483E,
        lineNumber = 0xFF90908A,
        lineNumberActive = 0xFFF8F8F2,
        gutter = 0xFF3E3D32,
        syntax = SyntaxColors(
            keyword = 0xFFF92672,      // Pink
            string = 0xFFE6DB74,       // Yellow
            comment = 0xFF75715E,      // Gray
            number = 0xFFAE81FF,       // Purple
            function = 0xFFA6E22E,     // Green
            type = 0xFF66D9EF,         // Cyan
            variable = 0xFFF8F8F2,     // White
            operator = 0xFFF92672      // Pink
        )
    )

    // Dracula Theme
    val DRACULA = EditorTheme(
        name = "Dracula",
        dark = true,
        background = 0xFF282A36,
        foreground = 0xFFF8F8F2,
        currentLineBackground = 0xFF44475A,
        selection = 0xFF44475A,
        lineNumber = 0xFF6272A4,
        lineNumberActive = 0xFFF8F8F2,
        gutter = 0xFF21222C,
        syntax = SyntaxColors(
            keyword = 0xFFFF79C6,      // Pink
            string = 0xFFF1FA8C,       // Yellow
            comment = 0xFF6272A4,      // Blue-gray
            number = 0xFFBD93F9,       // Purple
            function = 0xFF50FA7B,     // Green
            type = 0xFF8BE9FD,         // Cyan
            variable = 0xFFF8F8F2,     // White
            operator = 0xFFFF79C6      // Pink
        )
    )

    // GitHub Light Theme
    val GITHUB_LIGHT = EditorTheme(
        name = "GitHub Light",
        dark = false,
        background = 0xFFFFFFFF,
        foreground = 0xFF24292E,
        currentLineBackground = 0xFFFAFBFC,
        selection = 0xFFD0E9FF,
        lineNumber = 0xFF959DA5,
        lineNumberActive = 0xFF24292E,
        gutter = 0xFFFAFBFC,
        syntax = SyntaxColors(
            keyword = 0xFFD73A49,      // Red
            string = 0xFF032F62,       // Blue
            comment = 0xFF6A737D,      // Gray
            number = 0xFF005CC5,       // Blue
            function = 0xFF6F42C1,     // Purple
            type = 0xFF22863A,         // Green
            variable = 0xFF24292E,     // Black
            operator = 0xFFD73A49      // Red
        )
    )

    // Night Owl Theme
    val NIGHT_OWL = EditorTheme(
        name = "Night Owl",
        dark = true,
        background = 0xFF011627,
        foreground = 0xFFD6DEEB,
        currentLineBackground = 0xFF01111D,
        selection = 0xFF1D3B53,
        lineNumber = 0xFF4B6479,
        lineNumberActive = 0xFF80A4C2,
        gutter = 0xFF011627,
        syntax = SyntaxColors(
            keyword = 0xFFC792EA,      // Purple
            string = 0xFFECC48D,       // Yellow
            comment = 0xFF637777,      // Gray
            number = 0xFFF78C6C,       // Orange
            function = 0xFF82AAFF,     // Blue
            type = 0xFFFFCB8B,         // Light orange
            variable = 0xFFD6DEEB,     // White
            operator = 0xFF7FD1DE      // Cyan
        )
    )

    // Solarized Dark
    val SOLARIZED_DARK = EditorTheme(
        name = "Solarized Dark",
        dark = true,
        background = 0xFF002B36,
        foreground = 0xFF839496,
        currentLineBackground = 0xFF073642,
        selection = 0xFF073642,
        lineNumber = 0xFF586E75,
        lineNumberActive = 0xFF93A1A1,
        gutter = 0xFF073642,
        syntax = SyntaxColors(
            keyword = 0xFF859900,      // Green
            string = 0xFF2AA198,       // Cyan
            comment = 0xFF586E75,      // Gray
            number = 0xFFD33682,       // Magenta
            function = 0xFF268BD2,     // Blue
            type = 0xFFB58900,         // Yellow
            variable = 0xFF839496,     // Base0
            operator = 0xFF859900      // Green
        )
    )

    // Solarized Light
    val SOLARIZED_LIGHT = EditorTheme(
        name = "Solarized Light",
        dark = false,
        background = 0xFFFDF6E3,
        foreground = 0xFF657B83,
        currentLineBackground = 0xFFEEE8D5,
        selection = 0xFFEEE8D5,
        lineNumber = 0xFF93A1A1,
        lineNumberActive = 0xFF586E75,
        gutter = 0xFFEEE8D5,
        syntax = SyntaxColors(
            keyword = 0xFF859900,      // Green
            string = 0xFF2AA198,       // Cyan
            comment = 0xFF93A1A1,      // Gray
            number = 0xFFD33682,       // Magenta
            function = 0xFF268BD2,     // Blue
            type = 0xFFB58900,         // Yellow
            variable = 0xFF657B83,     // Base00
            operator = 0xFF859900      // Green
        )
    )

    /**
     * Breeze Theme (Ray.so)
     * Clean, airy theme with soft blues and purples
     */
    val BREEZE = EditorTheme(
        name = "Breeze",
        dark = true,
        background = 0xFF1F2937,        // Cool dark gray
        foreground = 0xFFE5E7EB,        // Light gray
        currentLineBackground = 0xFF374151,
        selection = 0xFF4B5563,
        lineNumber = 0xFF6B7280,
        lineNumberActive = 0xFF9CA3AF,
        gutter = 0xFF1F2937,
        syntax = SyntaxColors(
            keyword = 0xFF818CF8,      // Soft indigo
            string = 0xFF34D399,       // Emerald green
            comment = 0xFF6B7280,      // Medium gray
            number = 0xFFFBBF24,       // Amber
            function = 0xFF60A5FA,     // Sky blue
            type = 0xFFA78BFA,         // Purple
            variable = 0xFFE5E7EB,     // Light gray
            operator = 0xFFF472B6      // Pink
        )
    )

    /**
     * Candy Theme (Ray.so)
     * Sweet, vibrant colors with high contrast
     */
    val CANDY = EditorTheme(
        name = "Candy",
        dark = true,
        background = 0xFF1A1625,        // Deep purple-black
        foreground = 0xFFFFFFFF,        // Pure white
        currentLineBackground = 0xFF2A2139,
        selection = 0xFF3E2C53,
        lineNumber = 0xFF8B7BA8,
        lineNumberActive = 0xFFB8A4D4,
        gutter = 0xFF1A1625,
        syntax = SyntaxColors(
            keyword = 0xFFFF6AC1,      // Hot pink
            string = 0xFF5AF78E,       // Bright green
            comment = 0xFF6E6C7C,      // Muted purple-gray
            number = 0xFFFFD700,       // Gold
            function = 0xFF57C7FF,     // Cyan blue
            type = 0xFFFF6AC1,         // Hot pink
            variable = 0xFFF8F8F2,     // Off white
            operator = 0xFFFF6AC1      // Hot pink
        )
    )

    /**
     * Crimson Theme (Ray.so)
     * Bold reds and warm tones
     */
    val CRIMSON = EditorTheme(
        name = "Crimson",
        dark = true,
        background = 0xFF1C0B0E,        // Very dark red-brown
        foreground = 0xFFFAE6E6,        // Light pink-white
        currentLineBackground = 0xFF2D1217,
        selection = 0xFF4A1F29,
        lineNumber = 0xFF8B5366,
        lineNumberActive = 0xFFB87A8C,
        gutter = 0xFF1C0B0E,
        syntax = SyntaxColors(
            keyword = 0xFFFF6B9D,      // Pink
            string = 0xFFFFAD5C,       // Orange
            comment = 0xFF6B4C56,      // Muted rose
            number = 0xFFFFE66D,       // Yellow
            function = 0xFFFF6B9D,     // Pink
            type = 0xFFFF6B6B,         // Coral red
            variable = 0xFFFAE6E6,     // Light pink-white
            operator = 0xFFFF8FAB      // Light pink
        )
    )

    /**
     * Falcon Theme (Ray.so)
     * Military-inspired with greens and earth tones
     */
    val FALCON = EditorTheme(
        name = "Falcon",
        dark = true,
        background = 0xFF0C1419,        // Very dark blue-gray
        foreground = 0xFFB3BDC4,        // Cool gray
        currentLineBackground = 0xFF192530,
        selection = 0xFF293845,
        lineNumber = 0xFF5C6B77,
        lineNumberActive = 0xFF8A98A5,
        gutter = 0xFF0C1419,
        syntax = SyntaxColors(
            keyword = 0xFF80D4FF,      // Bright cyan
            string = 0xFF87D96B,       // Bright green
            comment = 0xFF5C6B77,      // Muted gray
            number = 0xFFFFCC66,       // Warm yellow
            function = 0xFF80D4FF,     // Bright cyan
            type = 0xFFFFAD66,         // Orange
            variable = 0xFFB3BDC4,     // Cool gray
            operator = 0xFF80D4FF      // Bright cyan
        )
    )

    /**
     * Meadow Theme (Ray.so)
     * Fresh greens and natural tones
     */
    val MEADOW = EditorTheme(
        name = "Meadow",
        dark = true,
        background = 0xFF0B1A0F,        // Very dark green
        foreground = 0xFFD4E8D9,        // Light mint
        currentLineBackground = 0xFF142B1A,
        selection = 0xFF1F4028,
        lineNumber = 0xFF4A6D52,
        lineNumberActive = 0xFF6B9975,
        gutter = 0xFF0B1A0F,
        syntax = SyntaxColors(
            keyword = 0xFF5FB584,      // Medium green
            string = 0xFF90EE90,       // Light green
            comment = 0xFF4A6D52,      // Muted green-gray
            number = 0xFFFFD966,       // Yellow
            function = 0xFF7EC699,     // Bright green
            type = 0xFF66D9A6,         // Cyan-green
            variable = 0xFFD4E8D9,     // Light mint
            operator = 0xFF5FB584      // Medium green
        )
    )

    /**
     * Midnight Theme (Ray.so)
     * Deep blues for late-night coding
     */
    val MIDNIGHT = EditorTheme(
        name = "Midnight",
        dark = true,
        background = 0xFF0A0E27,        // Deep navy
        foreground = 0xFFD6DEEB,        // Soft blue-white
        currentLineBackground = 0xFF111936,
        selection = 0xFF1D3B53,
        lineNumber = 0xFF4B6479,
        lineNumberActive = 0xFF80A4C2,
        gutter = 0xFF0A0E27,
        syntax = SyntaxColors(
            keyword = 0xFF9D7FEA,      // Soft purple
            string = 0xFF6BE4A3,       // Mint green
            comment = 0xFF4B6479,      // Blue-gray
            number = 0xFFFFCA85,       // Peach
            function = 0xFF7FDBFF,     // Sky blue
            type = 0xFFB4A4F4,         // Light purple
            variable = 0xFFD6DEEB,     // Soft blue-white
            operator = 0xFF7FDBFF      // Sky blue
        )
    )

    /**
     * Raindrop Theme (Ray.so)
     * Cool blues and teals like water
     */
    val RAINDROP = EditorTheme(
        name = "Raindrop",
        dark = true,
        background = 0xFF0D1B2A,        // Deep ocean blue
        foreground = 0xFFD8E9F0,        // Light cyan-white
        currentLineBackground = 0xFF1B2A41,
        selection = 0xFF2C4356,
        lineNumber = 0xFF4A6580,
        lineNumberActive = 0xFF7B99B8,
        gutter = 0xFF0D1B2A,
        syntax = SyntaxColors(
            keyword = 0xFF5EC4FF,      // Bright sky blue
            string = 0xFF7FE5CC,       // Turquoise
            comment = 0xFF4A6580,      // Slate blue
            number = 0xFFFFB86C,       // Warm orange
            function = 0xFF5EC4FF,     // Bright sky blue
            type = 0xFF9E86FF,         // Soft purple
            variable = 0xFFD8E9F0,     // Light cyan-white
            operator = 0xFF5EC4FF      // Bright sky blue
        )
    )

    /**
     * Sunset Theme (Ray.so)
     * Warm oranges and purples like dusk
     */
    val SUNSET = EditorTheme(
        name = "Sunset",
        dark = true,
        background = 0xFF1A0F1C,        // Deep purple-black
        foreground = 0xFFFFE4D6,        // Warm cream
        currentLineBackground = 0xFF2B1A2E,
        selection = 0xFF4A2F4D,
        lineNumber = 0xFF8B7089,
        lineNumberActive = 0xFFB89CB5,
        gutter = 0xFF1A0F1C,
        syntax = SyntaxColors(
            keyword = 0xFFFF6B9D,      // Pink
            string = 0xFFFFCC66,       // Golden yellow
            comment = 0xFF7A6B7D,      // Muted purple-gray
            number = 0xFFFF9D66,       // Coral orange
            function = 0xFFA78BFA,     // Bright purple
            type = 0xFFFF79C6,         // Hot pink
            variable = 0xFFFFE4D6,     // Warm cream
            operator = 0xFFFF6B9D      // Pink
        )
    )

    // All available themes
    val ALL_THEMES = listOf(
        NEMO_DARK,
        NEMO_LIGHT,
        NEMO_SUNSET,
        VS_CODE_DARK,
        VS_CODE_LIGHT,
        MONOKAI,
        DRACULA,
        GITHUB_LIGHT,
        NIGHT_OWL,
        SOLARIZED_DARK,
        SOLARIZED_LIGHT,

        BREEZE,
        CANDY,
        CRIMSON,
        FALCON,
        MEADOW,
        MIDNIGHT,
        RAINDROP,
        SUNSET
    )

    fun getThemeByName(name: String): EditorTheme {
        return ALL_THEMES.firstOrNull { it.name == name } ?: VS_CODE_DARK
    }
}