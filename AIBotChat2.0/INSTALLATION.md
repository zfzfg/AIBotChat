# 🚀 Quick Installation Guide

## Step 1: Setup text-generation-webui

```bash
cd text-generation-webui
python server.py --api --listen
```

**Verify it's running:**
- Open browser: http://localhost:7860
- API should be on: http://localhost:5000

## Step 2: Create a Character

1. Go to http://localhost:7860
2. Click **"Character"** tab
3. Create a new character or load an existing one
4. **Remember the character name!** (e.g., "MinecraftHelper")

## Step 3: Build the Plugin

```bash
cd AIBotChat
mvn clean package
```

**Output:** `target/AIBotChat-2.0.jar`

## Step 4: Install on Server

1. Copy JAR to `plugins/` folder
2. Start server (creates config)
3. Stop server
4. Edit `plugins/AIBotChat/config.yml`:

```yaml
api:
  url: "http://localhost:5000/v1/chat/completions"
  character: "MinecraftHelper"  # ← Your character name!
  max-tokens: 250
  temperature: 0.7
```

5. Start server again

## Step 5: Test

In Minecraft:
```
/ai Hello, who are you?
```

## ✅ Success Checklist

- [ ] text-generation-webui running with `--api`
- [ ] Model loaded in webui
- [ ] Character created
- [ ] Plugin installed in `plugins/` folder
- [ ] Config edited with correct character name
- [ ] Server started without errors
- [ ] `/ai` command works

## ❌ Common Issues

### API Error 404
```bash
# Make sure you started with --api flag:
python server.py --api --listen
```

### Character not found
```yaml
# Character name must match exactly!
# Check: text-generation-webui/characters/YourCharacter.yaml
character: "YourCharacter"  # ← Exact name without .yaml
```

### Connection refused
```yaml
# Change from localhost to 127.0.0.1:
url: "http://127.0.0.1:5000/v1/chat/completions"
```

## 🌐 Remote Server Setup

If text-generation-webui runs on a different machine:

```yaml
# In config.yml:
url: "http://192.168.1.100:5000/v1/chat/completions"
```

Make sure port 5000 is open!

## 📊 Recommended Settings

### For short, quick answers:
```yaml
max-tokens: 150
temperature: 0.5
```

### For detailed explanations:
```yaml
max-tokens: 300
temperature: 0.7
```

### For creative responses:
```yaml
max-tokens: 400
temperature: 0.9
```

## 🔧 Testing the API Manually

Before installing the plugin, test if the API works:

**Windows PowerShell:**
```powershell
Invoke-RestMethod -Uri "http://localhost:5000/v1/models" -Method Get
```

**Linux/Mac:**
```bash
curl http://localhost:5000/v1/models
```

Should return a list of available models/characters.

## 📞 Need Help?

1. Check the full [README.md](README.md)
2. Look at server logs: `logs/latest.log`
3. Enable debug in text-generation-webui console
4. Visit [Sterra.Online](https://sterra.online)

---

**Happy chatting! 🤖**