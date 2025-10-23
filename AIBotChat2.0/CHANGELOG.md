# Changelog

All notable changes to AIBotChat will be documented in this file.

## [2.0] - 2025-10-23

### Added
- ✨ Character-based AI integration with text-generation-webui
- 🎯 Configurable stop sequences to prevent unwanted generation
- 🔄 Support for multiple API formats (Chat Completions, Completions, native)
- 🛡️ Cooldown system to prevent spam
- 🧹 Automatic response cleanup and truncation
- 📝 Comprehensive README and installation guide
- 🌐 Support for remote API servers
- ⚡ Async processing to prevent server lag

### Features
- Character selection from text-generation-webui
- Configurable max tokens and temperature
- Smart sentence truncation
- Multiple command aliases (/ai, /ask, /bot, /ki)
- Detailed error messages
- Debug logging for troubleshooting

### Configuration
- `api.url` - API endpoint URL
- `api.character` - Character name from text-generation-webui
- `api.max-tokens` - Maximum response length
- `api.temperature` - Response creativity level
- `api.stop-sequences` - Custom stop words
- `cooldown-seconds` - Request cooldown time

---

## Future Plans

### [2.1] - Planned
- [ ] Permission system
- [ ] Multi-language support
- [ ] Response history/context
- [ ] Admin commands
- [ ] Statistics tracking

### [2.2] - Planned
- [ ] Web dashboard
- [ ] Custom prompt templates
- [ ] Rate limiting per player
- [ ] Response caching
- [ ] Economy integration

---

**A Sterra.Online Project** - https://sterra.online