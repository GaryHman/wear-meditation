# Wear Meditation (placeholder name)

Package / applicationId: `com.guidohuisman.wearmeditation`.

## Concept
A cheap (about $0.50 one-time, no ads, no tracking) **standalone Wear OS** meditation timer.
- Set a session length, start/stop/pause.
- Fully customizable **vibration patterns** and **sounds**: start cue, end cue, and optional interval bells (e.g. every N minutes). User can pick or edit patterns (lengths/pauses/intensity where the hardware allows) and choose sounds.
- Keeps running reliably with the screen off (use an ongoing activity / foreground service as Wear OS requires); ambient-friendly.
- Standalone: no phone app needed. No internet permission.
- Compose for Wear OS UI. No other features yet (no stats, no accounts, no billing code yet).

## First run
1. Scaffold + a minimal working timer with start/end vibration and one interval bell setting.
2. Debug build with the Gradle CLI.
3. README (what it is, build requirements, how to build, how to install on the watch).
4. `build-debug.cmd` in the project root: double-click builds the debug APK and tells where it is.
5. If JDK + Android SDK exist on this PC, prove it builds (`gradlew.bat assembleDebug`) and fix errors. If they don't, say so in README/STATUS.md and stop after writing the code.
## House rules (all projects)
- Meneer (Guido) is not a developer. Anything he must run = a double-clickable `.cmd` in the project root with plain-English steps in the README. Never ask him to type PowerShell or terminal commands.
- Kotlin, Gradle CLI (use the Gradle wrapper `gradlew.bat`), no emulators, no Android Studio requirement.
- No hardcoded absolute paths in code or scripts; resolve everything from the script's own folder (`%~dp0`). Use `local.properties` / env vars (`ANDROID_HOME`, `JAVA_HOME`) for the SDK and JDK, and keep `local.properties` out of git.
- Never put keystores, passwords, API keys or ad IDs in this folder, in git, or in chat. Debug builds only for now. Never publish, submit or roll out anything.
- Backups only in a hidden `.backups\` folder; never loose `.bak` files. Outdated files go to hidden `.archive\`; nothing gets deleted.
- Code, file names and in-app text in English.
- Be frugal: smallest change that works, no extra features, no heavy installs. If the JDK (17+) or Android SDK is missing on this PC, do NOT install them; write exactly what is missing in the README under "Build requirements" and in STATUS.md, and continue writing code that will build once they exist.
- This folder is its own git repo (branch `main`). Commit after each working step with clear messages. A private GitHub remote under GaryHman may be added later; do not create one yourself and do not push.
- Keep a short STATUS.md: what works, what is untested, what is blocked.