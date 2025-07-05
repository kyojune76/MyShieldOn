## 🔍 점검 항목별 개발 현황

| 항목 번호 | 점검 항목 | 상태 | 설명 / 구현 방식 |
|-----------|------------------------------|--------|-------------------------|
| 1 | 루팅 탐지 (Rooting Status Detection) | ✅ 완료 | `RootCheckUtils.kt` 내 아래 5가지 방식으로 종합 진단 수행<br><br>• `checkForSuBinary()`<br>• `detectTestKeys()`<br>• `checkForBusyBox()`<br>• `checkSuExists()`<br>• `checkForRWPaths()` |
| 2 | 비공식 스토어 설치 앱 탐지 (Unofficial App Sources Check) | ✅ 완료 | `AppInfoUtils.kt` 내 `getInstallerPackageName()` 확인하여 Google Play, 삼성스토어, OneStore 외 출처 탐지 |
| 3 | 다운로드 경로 APK 설치 앱 탐지 (Installed APK from Downloads) | ✅ 완료 | `SecurityScanner.kt`에서 Downloads 폴더 내 APK 탐색 후, 해당 패키지가 설치되어 있는지 확인하여 `InstalledFromDownloadedApk` 이슈로 분류 |
| 4 | 위험 권한 과다 보유 앱 탐지 (Risky Permissions Application Check) | ✅ 완료 | 위험 권한을 3개 이상 보유한 앱 탐지<br>→ 예: `READ_CONTACTS`, `SEND_SMS` 포함 여부 |
| 5 | 운영체제 최신 업데이트/보안 패치 정보 (OS Update/Security Patch Status) | 🔴 미구현 | `Build.VERSION.RELEASE`, `SECURITY_PATCH` 등을 가져오는 로직 없음 |
| 6 | 백그라운드 과다 사용 앱 (Monitoring Background Usage) | 🔴 미구현 | `UsageStatsManager` 사용 로직 없음 |
| 7 | 금융/국민앱 APK 서명 무결성 검사 (App Signature Integrity Check) | 🟡 부분 구현 | 5개 주요 앱의 SHA-256 서명 비교는 구현됨. 전체 100개 대상 DB는 준비 중 |
| 8 | ADB 모드/알 수 없는 출처 허용 상태 점검 (Developer Mode Status Check) | 🔴 미구현 | `Settings.Global.ADB_ENABLED`, `Settings.Secure.INSTALL_NON_MARKET_APPS` 등 점검 로직 없음 |

---

### 🔧 구현 파일 참고

- `RootCheckUtils.kt` - 루팅 탐지 로직
- `AppInfoUtils.kt` - 비공식 설치 앱 탐지
- `SecurityScanner.kt` - 위험 권한 / APK 경로 탐지 / 서명 비교
- `ScanViewModel.kt` - 전체 점검 흐름 관리
- `MainActivity.kt` - UI 전환 및 ViewModel 연결
