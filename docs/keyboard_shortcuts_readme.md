# Nemo Code Editor - Keyboard Shortcuts

Complete reference for all keyboard shortcuts available in the Nemo Code Editor.

---

## 📋 Quick Reference

### General Operations
| Windows/Linux | macOS | Action | Description |
|---------------|-------|--------|-------------|
| `Ctrl + N` | `⌘ + N` | New File | Create a new untitled file |
| `Ctrl + O` | `⌘ + O` | Open File | Open file picker |
| `Ctrl + Shift + O` | `⌘ + Shift + O` | Open Folder | Open folder picker |
| `Ctrl + S` | `⌘ + S` | Save | Save current file |
| `Ctrl + Shift + S` | `⌘ + Shift + S` | Save As | Save file with new name |
| `Ctrl + W` | `⌘ + W` | Close Tab | Close current tab |
| `Ctrl + Shift + W` | `⌘ + Shift + W` | Close All Tabs | Close all open tabs |

### Edit Operations
| Windows/Linux | macOS | Action | Description |
|---------------|-------|--------|-------------|
| `Ctrl + Z` | `⌘ + Z` | Undo | Undo last action |
| `Ctrl + Y` | `⌘ + Y` | Redo | Redo last undone action |
| `Ctrl + Shift + Z` | `⌘ + Shift + Z` | Redo (Alt) | Alternative redo shortcut |
| `Ctrl + D` | `⌘ + D` | Duplicate Line | Duplicate current line |
| `Ctrl + L` | `⌘ + L` | Delete Line | Delete current line |
| `Ctrl + /` | `⌘ + /` | Toggle Comment | Comment/uncomment line |

### Indentation
| Windows/Linux | macOS | Action | Description |
|---------------|-------|--------|-------------|
| `Ctrl + ]` | `⌘ + ]` | Indent | Indent line or selection |
| `Ctrl + [` | `⌘ + [` | Unindent | Unindent line or selection |

### Search & Replace
| Windows/Linux | macOS | Action | Description |
|---------------|-------|--------|-------------|
| `Ctrl + F` | `⌘ + F` | Toggle Find | Open/close find dialog |
| `Ctrl + H` | `⌘ + H` | Toggle Replace | Open/close find & replace dialog |

### Code Formatting
| Windows/Linux | macOS | Action | Description |
|---------------|-------|--------|-------------|
| `Ctrl + Shift + F` | `⌘ + Shift + F` | Format Code | Auto-format entire document |

### Tab Navigation
| Windows/Linux | macOS | Action | Description |
|---------------|-------|--------|-------------|
| `Ctrl + Tab` | `⌘ + Tab` | Next Tab | Switch to next tab |
| `Ctrl + Shift + Tab` | `⌘ + Shift + Tab` | Previous Tab | Switch to previous tab |

### View Operations
| Windows/Linux | macOS | Action | Description |
|---------------|-------|--------|-------------|
| `Ctrl + =` | `⌘ + =` | Zoom In | Increase font size |
| `Ctrl + NumPad +` | `⌘ + NumPad +` | Zoom In (Alt) | Alternative zoom in |
| `Ctrl + -` | `⌘ + -` | Zoom Out | Decrease font size |
| `Ctrl + NumPad -` | `⌘ + NumPad -` | Zoom Out (Alt) | Alternative zoom out |
| `Ctrl + B` | `⌘ + B` | Toggle Sidebar | Show/hide file explorer |

### Autocomplete
| Key | Action | Description |
|-----|--------|-------------|
| `Type letters` | Trigger | Start typing to show suggestions |
| `↓` or `Tab` | Next Item | Select next autocomplete item |
| `↑` | Previous Item | Select previous autocomplete item |
| `Enter` | Accept | Insert selected completion |
| `Esc` | Dismiss | Close autocomplete popup |

---

## 🔍 Detailed Descriptions

### General Operations

**New File** (`Ctrl + N` / `⌘ + N`)
- Creates a new untitled file
- Opens in a new tab
- Language detection when saved

**Open File** (`Ctrl + O` / `⌘ + O`)
- Opens file picker dialog
- Supports all file types
- Auto-detects language from extension

**Open Folder** (`Ctrl + Shift + O` / `⌘ + Shift + O`)
- Opens folder picker dialog
- Shows file explorer sidebar
- Navigate folder structure

**Save** (`Ctrl + S` / `⌘ + S`)
- Saves current file
- Creates file if untitled
- Shows save dialog for new files

**Save As** (`Ctrl + Shift + S` / `⌘ + Shift + S`)
- Saves with new name/location
- Opens save dialog
- Updates tab with new filename

**Close Tab** (`Ctrl + W` / `⌘ + W`)
- Closes current tab
- Prompts to save if modified
- Switches to adjacent tab

**Close All Tabs** (`Ctrl + Shift + W` / `⌘ + Shift + W`)
- Closes all open tabs
- Prompts for unsaved changes
- Returns to welcome screen

---

### Edit Operations

**Undo** (`Ctrl + Z` / `⌘ + Z`)
- Reverts last text change
- Up to 100 actions in history
- Works with all operations

**Redo** (`Ctrl + Y` / `⌘ + Y` or `Ctrl + Shift + Z` / `⌘ + Shift + Z`)
- Re-applies last undone action
- Two shortcuts available
- Cleared when new text typed

**Duplicate Line** (`Ctrl + D` / `⌘ + D`)
- Duplicates current line below
- Preserves indentation
- Works with multiple lines

**Delete Line** (`Ctrl + L` / `⌘ + L`)
- Deletes entire current line
- Includes newline character
- Recorded in undo history

**Toggle Comment** (`Ctrl + /` / `⌘ + /`)
- Comments/uncomments line
- Language-specific syntax:
  - **Kotlin/Java/JavaScript/C/C++**: `//`
  - **Python/MicroPython**: `#`
  - **HTML/XML**: `<!-- -->`
  - **CSS**: `/* */`
- Multi-line support

---

### Indentation

**Indent** (`Ctrl + ]` / `⌘ + ]`)
- Adds one indent level
- Respects tab settings
- Works on selection

**Unindent** (`Ctrl + [` / `⌘ + [`)
- Removes one indent level
- Smart removal
- Works on selection

---

### Search & Replace

**Toggle Find** (`Ctrl + F` / `⌘ + F`)
- Opens/closes find bar
- Features:
  - Case-sensitive
  - Whole word
  - Regular expressions
  - Next/Previous navigation

**Toggle Replace** (`Ctrl + H` / `⌘ + H`)
- Opens/closes find & replace bar
- All Find features plus:
  - Replace current
  - Replace all
  - Undo support

---

### Code Formatting

**Format Code** (`Ctrl + Shift + F` / `⌘ + Shift + F`)
- Formats entire document
- Language-aware rules
- Respects editor settings
- Recorded in undo history

---

### Tab Navigation

**Next Tab** (`Ctrl + Tab` / `⌘ + Tab`)
- Switches to next tab (right)
- Cycles to first after last

**Previous Tab** (`Ctrl + Shift + Tab` / `⌘ + Shift + Tab`)
- Switches to previous tab (left)
- Cycles to last before first

**Note:** Direct tab switching (`Ctrl + 1-9`) is **NOT** implemented

---

### View Operations

**Zoom In** (`Ctrl + =` or `Ctrl + NumPad +` / `⌘ + =`)
- Increases font size by 1
- Range: 8-32px
- Both shortcuts work

**Zoom Out** (`Ctrl + -` or `Ctrl + NumPad -` / `⌘ + -`)
- Decreases font size by 1
- Minimum: 8px
- Both shortcuts work

**Toggle Sidebar** (`Ctrl + B` / `⌘ + B`)
- Shows/hides file explorer
- State persists
- Desktop/tablet only

**Note:** Reset Zoom (`Ctrl + 0`) is **NOT** implemented

---

### Autocomplete

**Trigger** (Automatic)
- Shows when typing or after `.`
- Kotlin, Python and MicroPython only

**Navigate** (`↓` / `↑`)
- Down or Tab: Next item
- Up: Previous item

**Accept** (`Enter` or `Tab`)
- Inserts completion
- Closes popup

**Dismiss** (`Esc`)
- Closes without inserting

---

## 🔧 Technical Details

### Event Handling
- Uses `onPreviewKeyEvent`
- Intercepts before TextField
- Cross-platform key mapping

### Undo/Redo
- Maximum 100 actions
- Single undo per operation
- Format, line ops integrated

### Platform Support

| Platform | Support | Notes |
|----------|---------|-------|
| **Desktop** | ✅ Full | All shortcuts work |
| **Android** | ✅ Keyboard | Touch toolbar available |
| **iOS** | ⚠️ Limited | External keyboard needed |
| **Web** | ⚠️ Partial | Browser conflicts possible |

---

## 🐛 Troubleshooting

**Shortcut Not Working?**
1. Click editor to focus
2. Check OS conflicts
3. Verify platform support

**Autocomplete Issues?**
1. Check settings enabled
2. Kotlin, Python and MicroPython only
3. Not in read-only mode

---

## 📚 Related Documentation

- [NemoCodeEditor API Reference](./NemoCodeEditor.md)

---

## 🤝 Contributing

- [Open an issue](https://github.com/Ma7moud3ly/nemo-editor/issues)
- [Submit a pull request](https://github.com/Ma7moud3ly/nemo-editor/pulls)

---

<div align="center">
  <p><strong>Made with ❤️ using Kotlin Multiplatform</strong></p>
  <p>Nemo Editor - Just keep coding 🐠</p>
</div>