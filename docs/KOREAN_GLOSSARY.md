# BitChord 한국어 용어집

BitChord-korean 번역에 쓰는 용어와 문체 규칙입니다. 문자열을 새로 번역하거나 고칠 때 이 문서를 따릅니다.

## 문체

- 해요체는 쓰지 않습니다.
- 제목, 설정 항목 이름, 상태 표시는 명사형으로 씁니다. 예: "업로드 완료", "다운로드 중지됨", "새 곡 없음"
- 버튼은 짧은 동사 어간이나 명사로 씁니다. 예: "저장", "삭제", "다시 시도", "로그인"
- 설명, 안내, 오류는 "~합니다"체로 씁니다. 오류는 무슨 일이 생겼는지와 어떻게 하면 되는지를 같이 씁니다.
- 확인 대화상자는 "~하시겠습니까?"로 씁니다.
- 번역투를 쓰지 않습니다. "~에 대한", "~를 통해", "~하는 것이 가능합니다", 불필요한 "당신의/귀하의"는 빼고 씁니다. "your"는 대부분 번역하지 않습니다.
- 영어 어순 그대로 옮기지 말고 한국어 앱에서 실제로 쓰는 말로 씁니다.
- 마침표는 설명문에만 붙입니다. 제목, 버튼, 항목 이름에는 붙이지 않습니다.
- 따옴표는 원문 따옴표(“ ”)를 그대로 씁니다.

## 서식 규칙

- `%1$s`, `%2$d`, `%%` 같은 자리표시자는 순서와 개수를 그대로 둡니다. 한국어 어순에 맞게 위치는 옮겨도 됩니다.
- Android 리소스에서는 `'`를 `\'`로, `"`를 `\"`로, `@`나 `?`로 시작하는 문장은 `\@`, `\?`로 이스케이프합니다. 원문에 있는 `\n`, HTML 태그, `&amp;` 같은 엔티티는 그대로 둡니다.
- 조사는 자리표시자 뒤에 올 때 "을(를)", "이(가)", "은(는)", "와(과)", "(으)로" 형태로 씁니다.
- 숫자 뒤 단위: 곡 → "%1$d곡", 개 → "%1$d개", 분/초 → "%1$d분".
- `plurals`는 한국어에 복수형이 없으므로 `quantity="other"` 하나만 둡니다.

## 용어

| 영어 | 한국어 | 메모 |
| --- | --- | --- |
| song, track | 곡 | "트랙"은 쓰지 않음 |
| playlist | 재생목록 | YouTube Music 한국어 표기 |
| album | 앨범 | |
| artist | 아티스트 | |
| library | 보관함 | |
| home | 홈 | |
| explore | 둘러보기 | |
| search | 검색 | |
| queue | 대기열 | |
| up next | 다음 곡 | |
| now playing | 재생 중 | |
| playing from | 재생 위치 | 예: "재생 위치: 좋아요 표시한 곡" |
| liked songs | 좋아요 표시한 곡 | |
| history | 재생 기록 | |
| lyrics | 가사 | |
| synced lyrics | 싱크 가사 | |
| romanization | 로마자 표기 | |
| translation | 번역 | |
| download | 다운로드 | 동사형 버튼은 "다운로드" |
| offline | 오프라인 | |
| cache | 캐시 | |
| lossless | 무손실 | |
| hi-res | 하이레스 | |
| quality | 음질 | 오디오 맥락일 때 |
| crossfade | 크로스페이드 | |
| gapless playback | 끊김 없는 재생 | |
| automix | 자동 믹스 | |
| equalizer | 이퀄라이저 | |
| normalization | 음량 평준화 | |
| playback speed | 재생 속도 | |
| shuffle | 셔플 | |
| repeat | 반복 | |
| sleep timer | 취침 타이머 | |
| listen together, party | 함께 듣기 | party는 "함께 듣기 방" |
| host | 방장 | 함께 듣기 맥락 |
| output device | 출력 기기 | |
| sign in / sign out | 로그인 / 로그아웃 | |
| account | 계정 | |
| settings | 설정 | |
| appearance | 화면 | 설정 섹션 이름 |
| theme | 테마 | |
| dark / light | 다크 / 라이트 | |
| glass | 글래스 | Liquid Glass 효과 이름 |
| module, source | 소스 | 음원을 가져오는 곳 |
| server | 서버 | |
| scrobble | 스크로블 | Last.fm 용어 |
| rich presence | 상태 표시 | Discord 맥락 |
| replay | 리플레이 | 연말 결산 같은 기능 이름 |
| share | 공유 | |
| copy link | 링크 복사 | |
| explicit | 19금 | 배지와 설정 이름 |
| widget | 위젯 | |
| notification | 알림 | |
| battery | 배터리 | |
| token | 토큰 | |
| import / export | 가져오기 / 내보내기 | |
| clear | 지우기 | 기록, 캐시 |
| remove | 삭제 / 빼기 | 목록에서 뺄 때는 "빼기", 파일을 지울 때는 "삭제" |
| retry, try again | 다시 시도 | |
| enable / disable | 사용 / 사용 안 함 | 토글 설명에서는 "켜기 / 끄기"도 가능 |
| couldn’t … | … 실패 / …할 수 없습니다 | 제목은 명사형, 설명은 "~할 수 없습니다" |
| YouTube Music, Spotify, Discord, Last.fm, ListenBrainz | 그대로 | 제품 이름 |
| Google 계정 | Google 계정 | |
