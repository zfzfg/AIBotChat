# AIBotChat - Minecraft AI Assistant Plugin

> A Spigot/Paper plugin that integrates text-generation-webui AI into your Minecraft server.  
> A [Sterra.Online](https://sterra.online) Project

## Features

- Character-based AI - Use custom characters from text-generation-webui
- Chat integration - Players can ask the AI questions directly in-game
- Async processing - No server lag during AI requests
- Cooldown system - Prevents spam and protects your API
- Smart response handling - Automatic cleanup of incomplete sentences
- Fully configurable - Customize everything via config.yml
- Permission support - Optional restriction for /ai command
- Reload command - Update configuration without restarting the server

## Requirements

- Minecraft Server: Spigot/Paper 1.20+ (Java 17+)
- text-generation-webui: Running with API enabled (absolutely essential for the plugin to function)
- Maven: For building the plugin

To get AIBotChat running, text-generation-webui is absolutely essential. This open-source tool must be installed and running locally with the API enabled. Additionally, a model must be loaded in text-generation-webui for the AI to generate responses—without a loaded model, the plugin won't function.

## Installation

### 1. Setup text-generation-webui

First, you need text-generation-webui running with the API enabled:

```bash
# Navigate to your text-generation-webui directory
cd text-generation-webui

# Start with API enabled
python server.py --api --listen
```

This will start:
- WebUI: http://localhost:7860
- API: http://localhost:5000

### 2. Create a Character

1. Open text-generation-webui in your browser (http://localhost:7860)
2. Go to the Character tab
3. Create or load a character (e.g., "MinecraftHelper", "ServerAssistant")
4. In the character's context, you can input specific information about your Minecraft server (e.g., rules, commands, or custom lore) to tailor the AI's responses.
5. Save the character and note its exact name—you'll need it for the config

### 3. Build the Plugin

```bash
# Clone or download this repository
cd AIBotChat

# Build with Maven
mvn clean package

# The JAR will be in target/AIBotChat-2.0.jar
```

### 4. Install on Server

1. Copy `AIBotChat-2.0.jar` to your server's `plugins/` folder
2. Start the server (this creates the config file)
3. Stop the server
4. Edit `plugins/AIBotChat/config.yml` (see Configuration below)
5. Start the server again

## Configuration

Edit `plugins/AIBotChat/config.yml`:

```yaml
api:
  # Your text-generation-webui API URL
  url: "http://localhost:5000/v1/chat/completions"
  
  # Character name (must exist in text-generation-webui!)
  character: "Assistant"
  
  # Maximum response length (in tokens)
  max-tokens: 250
  
  # Creativity level (0.0-1.0)
  temperature: 0.7
  
  # Words where AI should stop generating
  stop-sequences:
    - "USER:"
    - "User:"
    - "\nUSER:"
    # Add more if needed...

# Cooldown between requests (seconds)
cooldown-seconds: 3

# Require permission for /ai command?
# false: Everyone can use it (default)
# true: Only players with 'aibotchat.use' permission can use it
require-permission: false
```

### Important Settings

| Setting | Description | Recommended |
|---------|-------------|-------------|
| `url` | API endpoint URL | `http://localhost:5000/v1/chat/completions` |
| `character` | Character name from text-generation-webui | Your character name |
| `max-tokens` | Maximum response length | 200-300 |
| `temperature` | Creativity (0.0-1.0) | 0.7 |
| `stop-sequences` | Words where AI stops | Keep defaults |
| `cooldown-seconds` | Time between requests | 3-5 seconds |
| `require-permission` | Require 'aibotchat.use' permission | false |

### API Endpoints

Choose the correct URL for your setup:

| Endpoint | When to use |
|----------|-------------|
| `/v1/chat/completions` | Recommended - Best for conversations |
| `/v1/completions` | For simple text completion |
| `/api/v1/generate` | text-generation-webui native API (port 7860) |

## Usage

### Commands

| Command | Aliases | Description | Example |
|---------|---------|-------------|---------|
| `/ai <question>` | `/ask`, `/bot`, `/ki` | Ask the AI a question | `/ai How do I claim land?` |
| `/aireload` | - | Reload the plugin configuration | `/aireload` |

### Examples

```
/ai Hello, who are you?
/ai How do I set a home point?
/ai What is the spawn protection radius?
/ai Explain the rank system
```

### Permissions

- `aibotchat.use`: Allows using /ai command (default: true, but can be required via config)
- `aibotchat.admin`: Allows using /aireload (default: op)

To restrict access, set `require-permission: true` in config.yml and manage permissions with a plugin like LuckPerms.

## Troubleshooting

### "API Error 404"
- text-generation-webui is not running
- API is not enabled (missing `--api` flag)
- Wrong URL in config.yml

**Solution**: Start text-generation-webui with `python server.py --api --listen`

### "API Error 500"
- No model loaded in text-generation-webui
- Character doesn't exist

**Solution**: Load a model in the webui and verify the character name

### "Character not found"
The character name in config.yml must match exactly with the character file name in `text-generation-webui/characters/`.

**Example**: If you have `characters/MyBot.yaml`, use:
```yaml
character: "MyBot"
```

### AI generates "USER: ..." messages
This happens when stop-sequences don't work. Add more stop sequences to config.yml:
```yaml
stop-sequences:
  - "USER:"
  - "\nUSER:"
  - "Player:"
  - "\nPlayer:"
```

### Responses are cut off mid-sentence
Increase `max-tokens` in config.yml:
```yaml
max-tokens: 300  # Increase from 250
```

## Remote API Setup

To use a remote text-generation-webui server:

1. Start text-generation-webui with `--listen`:
   ```bash
   python server.py --api --listen --listen-port 5000
   ```

2. Use the server's IP in config.yml:
   ```yaml
   url: "http://your-server-ip:5000/v1/chat/completions"
   ```

3. Make sure port 5000 is open in your firewall

## Token Usage

Understanding tokens helps optimize your setup:

| Tokens | ≈ Words | Use case |
|--------|---------|----------|
| 100 | 75-80 | Short answers |
| 200 | 150-160 | Medium answers |
| 250 | 180-190 | Recommended |
| 500 | 375-400 | Long explanations |

**Note**: 1 token ≈ 0.75 words on average

## Building from Source

```bash
# Requirements
- Java 17+
- Maven 3.6+

# Clone repository
git clone https://github.com/zfzfg/AIBotChat.git
cd AIBotChat

# Build
mvn clean package

# Output
target/AIBotChat-2.0.jar
```

## Project Structure

```
AIBotChat/
├── pom.xml                          # Maven configuration
├── README.md                        # This file
└── src/main/
    ├── java/de/zfzfg/aichat/
    │   ├── AIBotPlugin.java         # Main plugin class
    │   ├── AICommand.java           # Command handler
    │   └── TextGenAPI.java          # API communication
    └── resources/
        ├── plugin.yml               # Plugin metadata
        └── config.yml               # Default configuration
```

## Contributing

This is a Sterra.Online project. Contributions are welcome!

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## License

This project is open source. Feel free to use and modify it for your server.

## Links

- Sterra.Online: [https://sterra.online](https://sterra.online)
- text-generation-webui: [https://github.com/oobabooga/text-generation-webui](https://github.com/oobabooga/text-generation-webui)
- Spigot: [https://www.spigotmc.org](https://www.spigotmc.org)

## Tips

- Use descriptive character names that match their purpose
- Start with low `max-tokens` (200) and increase if needed
- Lower `temperature` (0.5) for factual answers, higher (0.9) for creative responses
- Add custom stop-sequences for your specific use case
- Monitor server logs for API errors and debugging info

## Known Issues

- Stop sequences may not work with all text-generation-webui versions
- Some models ignore the character parameter
- Very long responses may be cut off mid-sentence

## Support

For issues and questions:
- Create an issue on GitHub
- Visit [Sterra.Online](https://sterra.online)
- Join the Sterra Community Discord server: [https://discord.gg/vKdN7SgjUX](https://discord.gg/vKdN7SgjUX)
- Check the [text-generation-webui documentation](https://github.com/oobabooga/text-generation-webui/wiki)

---

Developed by zfzfg. Visit my website at [sterra.online](https://sterra.online) for more projects and updates.
