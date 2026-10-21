<div align="center">

<br/>

<img src="Logo.png" alt="BitChord KR icon" width="200" />

# BitChord-korean

### BitChord, in Korean

A fork of BitChord with a full Korean translation and a few tweaks for listening in Korea.

[한국어](README.md) | **English** | [日本語](README.ja.md)

</div>

> [!IMPORTANT]
> BitChord is not affiliated with, endorsed by, or connected to YouTube or Google in any way. Use it at your own discretion.

<div align="center">

<img src="Banner.png" alt="BitChord banner" width="100%" />

</div>

The original project is [kushagrasinghx/BitChord](https://github.com/kushagrasinghx/BitChord). Most of the app is the work of the upstream author and contributors; this repo adds a Korean translation and some features on top. The fork is maintained by marierie ([devuterian](https://github.com/devuterian)). The original README is kept as-is in [docs/UPSTREAM_README.md](docs/UPSTREAM_README.md).

## What's different

- **Korean translation:** The whole app and the player are translated, and Korean is in the in-app language picker. Terminology lives in [docs/KOREAN_GLOSSARY.md](docs/KOREAN_GLOSSARY.md).
- **Font:** Pretendard JP is bundled, with an option to use the system font instead. Korean text wraps between words, not mid-word. The font license (OFL) is in [docs/licenses/Pretendard-OFL.txt](docs/licenses/Pretendard-OFL.txt).
- **Share links:** Songs are shared as `youtu.be` / `youtube.com` links. It's a setting, on by default.
- **Player screen settings:** You can hide individual player elements one by one: handle, "Playing from", explicit badge, like button, current lyric line, times, quality badge, Automix indicator, volume bar, Listen Together button, and output device name. Status bar icons turn dark over bright full-bleed covers.
- **Lyrics romanization:** Korean lyrics are romanized on-device (Revised Romanization). You can show the original, the romanization and a translation together.
- **ALSong lyrics:** ALSong (알송) synced lyrics are available as a lyrics source.
- **Korean charts:** Turn on the "국내 차트 표시" (show Korean charts) setting to get Melon TOP 100 and Circle Chart weekly shelves on Home. These use unofficial endpoints and may break at any time.
- **Upstream PRs picked in:** A few upstream PRs that weren't merged yet.
  - [#844](https://github.com/kushagrasinghx/BitChord/pull/844): @itsmeadarsh2008
  - [#907](https://github.com/kushagrasinghx/BitChord/pull/907): @uveshm003
  - [#935](https://github.com/kushagrasinghx/BitChord/pull/935): @VictorZanelo
  - [#985](https://github.com/kushagrasinghx/BitChord/pull/985), [#987](https://github.com/kushagrasinghx/BitChord/pull/987), [#991](https://github.com/kushagrasinghx/BitChord/pull/991): @Galaxyyss
- **Installs alongside the original:** The package is `com.marierie.bitchordkorean` and the app is named "BitChord KR", so you don't have to remove the original BitChord.

Everything else (Hi-Res playback, Automix, downloads, scrobbling and so on) is the same as upstream. See the [original README](docs/UPSTREAM_README.md) for the full list.

## Install

1. Download `BitChordKR-*.apk` from the [Releases](https://github.com/devuterian/BitChord-korean/releases) page.
2. Open the APK. If Android blocks it, allow "Install unknown apps" for the app you downloaded it with (browser or file manager).
3. When the install finishes, launch "BitChord KR".

Requires Android 8.0 or later (minSdk 26). The in-app update check pulls from this fork's Releases, not the original repo's.

## Build

You need JDK 21.

```sh
./gradlew :app:assembleProdDebug
```

Release builds need a signing key. Copy `keystore.properties.example` to `keystore.properties` and fill it in. Never commit that file or the `.jks` keystore. If you lose the key, you can't update existing installs.

## Heads up

- The Korean charts use unofficial endpoints, so they can disappear from Home if the sites change.
- This fork is run independently of the upstream author. Please report fork-specific problems in [this repo's issues](https://github.com/devuterian/BitChord-korean/issues), not upstream.

## Disclaimer

BitChord is an independent third-party audio player, not associated with Google LLC, YouTube Music, Deezer, Telegram or their parent companies. It doesn't host or store music files; it reads local device storage or streams through public or user-authenticated APIs. It is made for personal research, educational and fair-use purposes, and you are responsible for complying with your local copyright law and the YouTube Terms of Service. The full original text is in the [original README](docs/UPSTREAM_README.md).

## License

Same as upstream: **GNU General Public License v3.0 (GPLv3)**. See [LICENSE](LICENSE). Distributing it means providing the source under the same license.
