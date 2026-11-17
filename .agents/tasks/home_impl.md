# JANA Student Home Screen – Implementation Notes

## What was built

### PART 1 – Shared JanaTopBar (`core:designsystem`)

**New file:** `core/designsystem/src/commonMain/kotlin/com/hussein/jana/core/designsystem/JanaTopBar.kt`

A reusable, stateless top app bar:
- Outer `Column` carries `statusBarsPadding()` so the 56 dp content row sits below the status bar in edge-to-edge mode.
- Left zone: optional ArrowBack `IconButton` → `JanaLogoCard(28 dp)` → screen title (`labelMedium`, ScholarlyTeal, SemiBold).
- Flexible `Spacer(weight=1f)` pushes right content to the end.
- Right zone: amber `CreditsBadge` (if `credits != null`) → `JanaInitialsAvatar` (if `showAvatar`) → `actions` slot.
- `HorizontalDivider` (1 dp, OutlineVariant) closes the bottom.
- Also exports `JanaInitialsAvatar` (parameterised size + background colour) for reuse in session cards.

### PART 2 – Home Feature overhaul (`feature:home`)

#### Contracts / ViewModels

| File | Key change |
|------|-----------|
| `HomeContract.kt` | Replaced `selectedTab: Int` with `enum HomeTab`; added `userName`, `credits` |
| `HomeViewModel.kt` | Handles `HomeIntent.TabSelected(tab)` |
| `tabs/home/HomeTabContract.kt` | Full `HomeTabState` with live session, AI message, subjects, upcoming sessions |
| `tabs/home/HomeTabViewModel.kt` | Mock static data via default state; each intent emits a `ShowSnackbar` effect |

#### Screen structure

```
HomeFlow  (koinViewModel → HomeViewModel)
└── HomeScreen  (Scaffold)
    ├── topBar: JanaTopBar (credits badge + avatar)
    ├── bottomBar: HomeBottomNav (5 items, ScholarlyTeal selected, PrimaryContainer indicator)
    └── content (Box + innerPadding):
        ├── HOME     → HomeTabScreen
        ├── TEACHERS → TeachersTabScreen  (placeholder)
        ├── JANA_AI  → JanaAiTabScreen    (placeholder)
        ├── SCHEDULE → ScheduleTabScreen  (placeholder)
        └── PROGRESS → ProgressTabScreen  (placeholder)

HomeTabScreen  (koinViewModel → HomeTabViewModel)
└── Box(fillMaxSize)
    ├── HomeTabContent (LazyColumn, 16 dp horizontal padding)
    │   ├── GreetingHeader
    │   ├── ActiveSessionBanner
    │   ├── LiveSessionCard (teal, full join button)
    │   ├── AiCompanionCard (AnnotatedString highlights, 2 outlined buttons)
    │   ├── ConceptMasterySection (CircularProgressArc per subject)
    │   ├── UpcomingSessionsSection (UpcomingSessionCard per session)
    │   └── Spacer(80 dp) – clears sticky banner
    ├── CreditsBanner (sticky, ScholarlyTeal, top-rounded)
    └── SnackbarHost (above banner, 90 dp bottom offset)
```

#### Components (all in `tabs/home/components/`)

- **GreetingHeader** – greeting + username + 👋 emoji, notifications icon
- **ActiveSessionBanner** – amber dot + "starting in N minutes"
- **LiveSessionCard** – teal surface, LiveZoomPill (red), group session, teacher row, join button
- **AiCompanionCard** – AstralIndigo chip, highlighted phrases, Quick Quiz + Formula Sheet buttons
- **SubjectProgressRow** + **CircularProgressArc** – Canvas arc (OutlineVariant track / ScholarlyTeal progress), level chip
- **ConceptMasterySection** – Serif header, subtitle, SubjectProgressRows separated by HorizontalDividers
- **UpcomingSessionsSection** + private **UpcomingSessionCard** – day/time row, reserved badge, teacher initials
- **CreditsBanner** – ScholarlyTeal, top-rounded corners, Refill Plan outlined button

#### DI

`di/HomeModule.kt` registers `HomeViewModel` and `HomeTabViewModel` via `koin viewModel { }`.

### PART 3 – String resources

New keys added to both `values/strings.xml` (English) and `values-ar/strings.xml` (Arabic):
`home_student_label`, `home_credits_label`, `nav_home`, `nav_teachers`, `nav_jana_ai`, `nav_schedule`, `nav_progress`, `home_greeting_morning/afternoon/evening`, `home_active_session_msg`, `home_minutes`, `home_live_badge`, `home_min_suffix`, `home_group_session`, `home_enrolled`, `home_prepare_join`, `home_ai_chip`, `home_quick_quiz`, `home_formula_sheet`, `home_concept_mastery`, `home_concept_subtitle`, `home_breakdown`, `home_upcoming_sessions`, `home_view_calendar`, `home_credits_banner`, `home_credits_subtitle`, `home_refill_plan`, `home_reserved`, `home_proficient`, `home_developing`, `home_foundations`, `home_today`, `home_teachers_coming_soon`, `home_ai_coming_soon`, `home_schedule_coming_soon`, `home_progress_coming_soon`.

## Build commands run and results

```
.\gradlew :core:designsystem:compileKotlinJvm
  → BUILD SUCCESSFUL (1 deprecation warning on existing AppLanguagePlatform.kt, unrelated)

.\gradlew :feature:home:compileKotlinJvm
  → First attempt: 1 error (bad `weight` import in GreetingHeader.kt → fixed)
  → Second attempt: BUILD SUCCESSFUL (2 deprecation warnings for TrendingUp/MenuBook → fixed to AutoMirrored variants)

.\gradlew :composeApp:compileKotlinJvm
  → BUILD SUCCESSFUL – 58 tasks, no errors, no warnings
```

## Files unchanged (kept as-is)
- `feature/home/src/commonMain/kotlin/com/hussein/jana/feature/home/HomeEntryProvider.kt`
- `feature/home/build.gradle.kts`
