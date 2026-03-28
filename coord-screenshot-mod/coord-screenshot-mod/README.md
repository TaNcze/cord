# 📍 Coord Screenshot Mod — Minecraft 1.21 (Fabric)

Mod robi screenshota z nałożonymi współrzędnymi gracza **bez potrzeby włączania F3 lub cordów w ustawieniach**.

---

## ✨ Co robi?

Po naciśnięciu **F2** (domyślny klawisz):
- Robi screenshot aktualnego widoku
- Nakłada panel z cordami w lewym dolnym rogu:
  - `X`, `Y`, `Z` (dokładne)
  - Współrzędne bloku
  - Wymiar (Overworld / Nether / The End)
  - Datę i godzinę
- Zapisuje plik jako `coords_YYYY-MM-DD_HH.mm.ss.png` w folderze `screenshots/`
- Wyświetla potwierdzenie na czacie

> ✅ Cordy NIE muszą być włączone w HUD gry!

---

## 📦 Instalacja (gotowy .jar)

1. Zainstaluj **Fabric Loader** dla Minecraft 1.21 → https://fabricmc.net/use/installer/
2. Zainstaluj **Fabric API** → wrzuć do folderu `mods/`
3. Wrzuć plik `coord-screenshot-1.0.0.jar` do folderu `mods/`
4. Uruchom Minecraft

---

## 🔨 Kompilacja ze źródeł

### Wymagania
- **Java 21+** (np. Adoptium Temurin 21)
- **Git**

### Kroki

```bash
# 1. Wejdź do folderu moda
cd coord-screenshot-mod

# 2. (Windows) Uruchom:
gradlew.bat build

# (Linux/Mac) Uruchom:
./gradlew build

# 3. Gotowy plik .jar będzie w:
#    build/libs/coord-screenshot-1.0.0.jar
```

### Pierwsze uruchomienie (pobieranie Gradle)
```bash
# Windows
gradlew.bat wrapper

# Linux/Mac
gradle wrapper
# lub ściągnij Gradle 8.8 ze https://gradle.org/releases/
```

---

## ⌨️ Zmiana klawisza

Wejdź w: **Opcje → Sterowanie → Inne → "Screenshot z Cordami"**

Domyślny klawisz: **F2**

---

## 📁 Struktura plików

```
coord-screenshot-mod/
├── src/main/java/com/coordscreenshot/
│   ├── CoordScreenshotClient.java    ← główna klasa, rejestracja klawisza
│   ├── CoordScreenshotRenderer.java  ← rysuje panel z cordami + zapisuje PNG
│   └── ScreenshotCapture.java        ← odczyt framebuffera do NativeImage
├── src/main/resources/
│   ├── fabric.mod.json
│   └── assets/coordscreenshot/lang/en_us.json
├── build.gradle
├── gradle.properties
└── settings.gradle
```

---

## 🐛 Znane ograniczenia

- Mod jest **tylko po stronie klienta** (nie trzeba instalować na serwerze)
- Klawisz F2 może kolidować ze standardowym screenshotem Minecrafta — zmień jeden z nich w ustawieniach sterowania

---

## 📜 Licencja

MIT — możesz modyfikować i dystrybuować dowolnie.
