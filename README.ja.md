<div align="center">

<br/>

<img src="Logo.png" alt="BitChord KR アイコン" width="200" />

# BitChord-korean

### 韓国語で使える BitChord

BitChord を韓国語に翻訳し、韓国で使いやすいように手を加えたフォークです。

[한국어](README.md) | [English](README.en.md) | **日本語**

</div>

> [!IMPORTANT]
> BitChord は YouTube や Google とは無関係で、公認されたものでもありません。ご利用は自己責任でお願いします。

<div align="center">

<img src="Banner.png" alt="BitChord バナー" width="100%" />

</div>

オリジナルは [kushagrasinghx/BitChord](https://github.com/kushagrasinghx/BitChord) です。アプリの大部分は原作者とコントリビューターの成果で、このリポジトリはその上に韓国語訳といくつかの機能を加えたものです。フォークは marierie([devuterian](https://github.com/devuterian))が管理しています。元の README は [docs/UPSTREAM_README.md](docs/UPSTREAM_README.md) にそのまま残してあります。

## オリジナルとの違い

- **韓国語訳:** アプリ全体とプレーヤーを翻訳し、アプリ内の言語選択に韓国語を追加しました。用語の基準は [docs/KOREAN_GLOSSARY.md](docs/KOREAN_GLOSSARY.md) にあります。
- **フォント:** Pretendard JP を同梱し、システムフォントに切り替えるオプションもあります。韓国語は単語の途中ではなく、単語の区切りで折り返します。フォントのライセンス(OFL)は [docs/licenses/Pretendard-OFL.txt](docs/licenses/Pretendard-OFL.txt) です。
- **共有リンク:** 曲を `youtu.be` / `youtube.com` のリンクで共有します。設定で切り替えられ、初期状態はオンです。
- **プレーヤー画面の設定:** ハンドル、再生元、露骨な表現のバッジ、いいねボタン、現在の歌詞の行、時間、音質バッジ、Automix 表示、音量バー、一緒に聴くボタン、出力デバイス名を、プレーヤーから一つずつ非表示にできます。明るいフルブリードのカバーの上では、ステータスバーのアイコンが暗い色になります。
- **歌詞のローマ字表記:** 韓国語の歌詞を端末内でローマ字に変換します(国語のローマ字表記法)。原文、ローマ字、翻訳を一緒に表示することもできます。
- **ALSong の歌詞:** ALSong(알송)の同期歌詞を歌詞ソースとして使えます。
- **韓国チャート:** 設定の「국내 차트 표시」(韓国チャートを表示)をオンにすると、ホームに Melon TOP 100 と Circle Chart の週間チャートが並びます。非公式のエンドポイントを使っているので、いつ止まっても不思議ではありません。
- **オリジナルの PR を取り込み:** オリジナルでまだマージされていない PR をいくつか取り込みました。
  - [#844](https://github.com/kushagrasinghx/BitChord/pull/844): @itsmeadarsh2008
  - [#907](https://github.com/kushagrasinghx/BitChord/pull/907): @uveshm003
  - [#935](https://github.com/kushagrasinghx/BitChord/pull/935): @VictorZanelo
  - [#985](https://github.com/kushagrasinghx/BitChord/pull/985)、[#987](https://github.com/kushagrasinghx/BitChord/pull/987)、[#991](https://github.com/kushagrasinghx/BitChord/pull/991): @Galaxyyss
- **オリジナルと併用可能:** パッケージは `com.marierie.bitchordkorean`、アプリ名は「BitChord KR」なので、元の BitChord を消さずにインストールできます。

それ以外の機能(Hi-Res 再生、Automix、ダウンロード、スクロブルなど)はオリジナルと同じです。一覧は[元の README](docs/UPSTREAM_README.md)をご覧ください。

## インストール

1. [Releases](https://github.com/devuterian/BitChord-korean/releases) ページから `BitChordKR-*.apk` をダウンロードします。
2. ダウンロードした APK を開きます。ブロックされたら、ダウンロードに使ったアプリ(ブラウザやファイル管理アプリ)に「不明なアプリのインストール」を許可してください。
3. インストールが終わったら「BitChord KR」を起動します。

Android 8.0 以上(minSdk 26)が必要です。アプリ内のアップデート確認は、このフォークの Releases を参照します。オリジナルのリポジトリは見ません。

## ビルド

JDK 21 が必要です。

```sh
./gradlew :app:assembleProdDebug
```

リリースビルドには署名鍵が必要です。`keystore.properties.example` を `keystore.properties` にコピーして値を埋めてください。このファイルと `.jks` キーストアは、絶対にコミットしないでください。鍵をなくすと、インストール済みのアプリを更新できなくなります。

## 注意事項

- 韓国チャートは非公式のエンドポイントを使っているため、サイト側の変更でホームから消えることがあります。
- このフォークは原作者とは別に運営されています。フォーク固有の問題は、オリジナルではなく[このリポジトリの Issues](https://github.com/devuterian/BitChord-korean/issues) に報告してください。

## 免責

BitChord は、Google LLC、YouTube Music、Deezer、Telegram などとは無関係の、独立したサードパーティ製オーディオプレーヤーです。音楽ファイルのホスティングや保存は行わず、端末内のストレージの読み取り、または公開 API やユーザーが認証した API からのストリーミングのみを行います。個人の研究、教育、フェアユースを目的に作られており、居住地の著作権法と YouTube の利用規約を守る責任は利用者にあります。原文は[元の README](docs/UPSTREAM_README.md)をご覧ください。

## ライセンス

オリジナルと同じ **GNU General Public License v3.0 (GPLv3)** です。[LICENSE](LICENSE) を確認してください。配布する場合は、同じライセンスでソースコードも提供する必要があります。
