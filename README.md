# Subaru DL — Android

Versão Android do [Subaru DL](../subaru-dl). Por enquanto está na **fase 1 (viabilidade)**: uma tela de teste que
baixa um vídeo do YouTube no aparelho com yt-dlp + QuickJS + ffmpeg.

## Resultado da fase 1 (emulador x86_64, Android 17)

- `io.github.junkfood02.youtubedl-android` 0.18.1 (Maven Central) traz Python, yt-dlp, ffmpeg e **QuickJS** (`libqjs.so`).
- O yt-dlp se atualiza sozinho dentro do app (2026.08.19) e reconhece o QuickJS (`JS runtimes: quickjs-2025-04-26`).
- Download do YouTube com vídeo + áudio juntos (ffmpeg) funcionou, com a mesma escolha de formato do PC (395+251).
- Cliente `web` forçado falha igual no PC e no Android ("No video formats found"): é regra do YouTube, não do Android.
- Ainda **não testado**: celular arm64 de verdade, desafio de JavaScript resolvido pelo QuickJS (o caminho padrão atual
  do YouTube não precisa dele), gallery-dl, aria2.

## Como testar (mesmo jeito do PitStopHub)

Gradle pela linha de comando + emulador `Medium_Phone_API_37.0` + `adb`. O JDK é o do Android Studio, e o tmpdir
precisa ficar fora de `AppData` (ver README do PitStopHub Android).

```bash
export JAVA_HOME="/c/Program Files/Android/Android Studio/jbr"
export JAVA_TOOL_OPTIONS="-Djava.io.tmpdir=C:/Users/Administrator/jt -Djdk.net.unixdomain.tmpdir=C:/Users/Administrator/jt"
./gradlew.bat :app:assembleDebug
emulator -avd Medium_Phone_API_37.0 -no-snapshot-save
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -S -n com.subarudl.app/.MainActivity --es url "https://www.youtube.com/watch?v=jNQXAC9IVRw"
adb logcat -d -s SUBARU:I
```

Extras da tela de teste: `--es fmt "<seletor>"` e `--es client web` (força um cliente do YouTube).
Os arquivos baixados ficam em `/sdcard/Android/data/com.subarudl.app/files/teste`.

## Próximas fases

2. Interface (Compose, como o PitStopHub) e motor em segundo plano com notificação.
3. Colar link, prévia, qualidade, áudio, legenda, playlist, fila e histórico.
4. Galeria (gallery-dl), pasta por link, organizar por.
5. Compartilhar → Subaru DL, login por janela no app, atualização do yt-dlp.
