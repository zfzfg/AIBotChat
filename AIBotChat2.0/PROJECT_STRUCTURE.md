# 📁 Project Structure

## Complete File Tree

```
AIBotChat/
│
├── 📄 README.md                      # Main documentation
├── 📄 INSTALLATION.md                # Quick setup guide
├── 📄 CHANGELOG.md                   # Version history
├── 📄 LICENSE                        # License file
├── 📄 .gitignore                     # Git ignore rules
├── 📄 pom.xml                        # Maven build configuration
│
└── src/
    └── main/
        ├── java/
        │   └── de/
        │       └── zfzfg/
        │           └── aichat/
        │               ├── AIBotPlugin.java      # Main plugin class
        │               ├── AICommand.java        # Command handler
        │               └── TextGenAPI.java       # API communication
        │
        └── resources/
            ├── plugin.yml                        # Plugin metadata
            └── config.yml                        # Default configuration
```

## 📦 File Descriptions

### Root Files

| File | Purpose |
|------|---------|
| `README.md` | Complete documentation with features, setup, troubleshooting |
| `INSTALLATION.md` | Quick start guide for fast setup |
| `CHANGELOG.md` | Version history and planned features |
| `LICENSE` | Open source license |
| `.gitignore` | Files to exclude from git |
| `pom.xml` | Maven build configuration and dependencies |

### Java Source Files

| File | Class | Purpose |
|------|-------|---------|
| `AIBotPlugin.java` | Main plugin | Plugin initialization, lifecycle management |
| `AICommand.java` | Command executor | Handles `/ai` command, cooldowns, formatting |
| `TextGenAPI.java` | API client | HTTP communication with text-generation-webui |

### Resource Files

| File | Purpose |
|------|---------|
| `plugin.yml` | Spigot plugin metadata (name, version, commands) |
| `config.yml` | Default configuration template |

## 🏗️ Build Output

After running `mvn clean package`:

```
target/
├── classes/                          # Compiled .class files
├── generated-sources/                # Maven generated files
├── maven-archiver/                   # Build info
├── maven-status/                     # Compilation status
└── AIBotChat-2.0.jar                # 🎯 Final plugin JAR
```

## 📊 Code Statistics

| Metric | Count |
|--------|-------|
| Java Files | 3 |
| Total Lines | ~400 |
| Classes | 3 |
| Methods | ~15 |
| Config Options | 6 |

## 🔗 Dependencies

From `pom.xml`:

```xml
<dependencies>
    <!-- Spigot API (provided by server) -->
    <dependency>
        <groupId>org.spigotmc</groupId>
        <artifactId>spigot-api</artifactId>
        <version>1.20.1-R0.1-SNAPSHOT</version>
        <scope>provided</scope>
    </dependency>
    
    <!-- Gson (provided by Spigot) -->
    <dependency>
        <groupId>com.google.code.gson</groupId>
        <artifactId>gson</artifactId>
        <version>2.10.1</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

**Note**: Both dependencies are `provided` - they're included in Spigot, so the final JAR is lightweight (~20KB).

## 🚀 Usage Flow

```
Player → /ai question
    ↓
AICommand.java
    ↓ (validates & cooldown check)
TextGenAPI.java
    ↓ (async HTTP request)
text-generation-webui API
    ↓ (generates response)
TextGenAPI.java
    ↓ (cleanup & format)
AICommand.java
    ↓
Player sees response
```

## 📝 Configuration Flow

```
Server Start
    ↓
AIBotPlugin.onEnable()
    ↓
Load/Create config.yml
    ↓
Initialize TextGenAPI
    ↓
Register AICommand
    ↓
Ready for use!
```

## 🔄 Update Process

To update the plugin:

1. Replace JAR in `plugins/` folder
2. Restart server (or `/reload`)
3. Check `plugins/AIBotChat/config.yml` for new options
4. Update config if needed
5. Test with `/ai test`

---

**A Sterra.Online Project** - https://sterra.online