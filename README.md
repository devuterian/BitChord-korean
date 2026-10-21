<div align="center">

<br/>

<img src="Logo.png" alt="BitChord KR 아이콘" width="200" />

# BitChord-korean

### 한국어로 쓰는 BitChord

BitChord를 한국어로 옮기고, 한국에서 쓰기 좋게 손본 포크예요.

**한국어** | [English](README.en.md) | [日本語](README.ja.md)

</div>

> [!IMPORTANT]
> BitChord는 YouTube나 Google과 관련이 없고, 승인받은 프로젝트도 아닙니다. 사용은 본인 책임이에요.

<div align="center">

<img src="Banner.png" alt="BitChord 배너" width="100%" />

</div>

원본은 [kushagrasinghx/BitChord](https://github.com/kushagrasinghx/BitChord)예요. 앱 대부분은 원작자와 기여자들이 만든 거고, 이 저장소는 그 위에 한국어 번역과 몇 가지 기능을 얹은 겁니다. 포크는 marierie([devuterian](https://github.com/devuterian))가 관리해요. 원본 README는 [docs/UPSTREAM_README.md](docs/UPSTREAM_README.md)에 그대로 남겨 뒀어요.

## 원본이랑 다른 점

- **한국어 번역:** 앱과 플레이어 전체를 번역했고, 앱 언어 선택에 한국어를 넣었어요. 용어 기준은 [docs/KOREAN_GLOSSARY.md](docs/KOREAN_GLOSSARY.md)에 있어요.
- **글꼴:** Pretendard JP를 넣었고, 시스템 글꼴로 바꾸는 옵션도 있어요. 한국어는 글자 중간이 아니라 단어 사이에서 줄바꿈돼요. 글꼴 라이선스(OFL)는 [docs/licenses/Pretendard-OFL.txt](docs/licenses/Pretendard-OFL.txt)에 있어요.
- **공유 링크:** 곡을 `youtu.be` / `youtube.com` 링크로 공유해요. 설정에 있고 기본으로 켜져 있어요.
- **플레이어 화면 설정:** 플레이어에서 닫기 손잡이, 재생 위치, 19금 배지, 좋아요 버튼, 현재 가사 줄, 시간, 음질 배지, 자동 믹스 표시, 볼륨 바, 함께 듣기 버튼, 출력 기기 이름을 하나씩 숨길 수 있어요. 밝은 전체 화면 커버 위에서는 상태바 아이콘이 어둡게 바뀌어요.
- **가사 로마자 표기:** 한국어 가사를 기기 안에서 로마자로 바꿔 줘요(국어의 로마자 표기법). 원문, 로마자, 번역을 같이 띄울 수도 있어요.
- **알송 가사:** 알송(ALSong) 싱크 가사를 가사 소스로 쓸 수 있어요.
- **국내 차트:** 설정의 "국내 차트 표시"를 켜면 홈에 멜론 TOP 100과 써클차트 주간 차트가 나와요. 비공식 엔드포인트라서 언제든 막히거나 깨질 수 있어요.
- **원본 PR 가져오기:** 원본의 아직 머지되지 않은 PR 몇 개를 가져왔어요.
  - [#844](https://github.com/kushagrasinghx/BitChord/pull/844): @itsmeadarsh2008
  - [#907](https://github.com/kushagrasinghx/BitChord/pull/907): @uveshm003
  - [#935](https://github.com/kushagrasinghx/BitChord/pull/935): @VictorZanelo
  - [#985](https://github.com/kushagrasinghx/BitChord/pull/985), [#987](https://github.com/kushagrasinghx/BitChord/pull/987), [#991](https://github.com/kushagrasinghx/BitChord/pull/991): @Galaxyyss
- **원본과 같이 설치:** 패키지는 `com.marierie.bitchordkorean`, 앱 이름은 "BitChord KR"이라 원본 BitChord를 지우지 않아도 돼요.

그 밖의 기능(Hi-Res 재생, Automix, 다운로드, 스크로블링 등)은 원본과 같아요. 목록은 [원본 README](docs/UPSTREAM_README.md)를 보세요.

## 설치

1. [Releases](https://github.com/devuterian/BitChord-korean/releases) 페이지에서 `BitChordKR-*.apk`를 받아요.
2. 받은 APK를 열어요. 막히면 해당 앱(브라우저나 파일 관리자)에 "출처를 알 수 없는 앱 설치"를 허용해 주세요.
3. 설치가 끝나면 "BitChord KR"을 실행해요.

Android 8.0 이상(minSdk 26)이 필요해요. 앱 안의 업데이트 확인은 이 포크의 Releases에서 가져와요. 원본 저장소의 릴리스는 보지 않아요.

## 빌드

JDK 21이 필요해요.

```sh
./gradlew :app:assembleProdDebug
```

릴리스 빌드는 서명 키가 있어야 해요. `keystore.properties.example`을 `keystore.properties`로 복사해서 값을 채우면 됩니다. 이 파일과 `.jks` 키스토어는 절대 커밋하지 마세요. 키를 잃어버리면 기존 설치본에 업데이트를 못 해요.

## 주의 사항

- 국내 차트는 비공식 엔드포인트를 써서, 사이트가 바뀌면 홈에서 사라질 수 있어요.
- 이 포크는 원본 작자와 무관하게 운영돼요. 이 포크의 문제는 원본 저장소에 올리지 말고 [이 저장소 이슈](https://github.com/devuterian/BitChord-korean/issues)에 남겨 주세요.

## 면책

BitChord는 Google LLC, YouTube Music, Deezer, Telegram 등과 관련 없는 독립 서드파티 오디오 플레이어입니다. 음원 파일을 호스팅하거나 저장하지 않고, 기기 저장소를 읽거나 공개 API 또는 사용자가 인증한 API로 스트리밍하는 클라이언트로만 동작합니다. 개인 연구와 교육, 공정 이용 목적으로 만들어졌으며, 사용 지역의 저작권법과 YouTube 이용약관을 지키는 책임은 사용자에게 있습니다. 자세한 원문은 [원본 README](docs/UPSTREAM_README.md)를 보세요.

## 라이선스

원본과 같은 **GNU General Public License v3.0 (GPLv3)**입니다. [LICENSE](LICENSE)를 확인하세요. 배포할 때는 같은 라이선스로 소스를 함께 제공해야 해요.
