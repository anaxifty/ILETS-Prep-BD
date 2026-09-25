# IELTS Prep BD — UI Redesign: "Emerald Focus" Theme
_(Previous UI saved in `/workspace/backup/ui-redesign-2026-09-26/`)_

## 1. Research Summary — Human Psychology & Design Rationale

### Color psychology (why Emerald + Amber wins for an exam-prep app)
| Factor | Research finding | Application here |
|---|---|---|
| **Green = growth, competence, calm** | Environmental-color psychology (Andrews et al., Elliot & Maier's color-context theory) shows green primes approach motivation, learning openness and reduces anxiety vs. red-dominant environments. Blue conveys trust but feels "corporate/cold"; purple feels playful, not scholarly. | Primary brand = deep **Verdant Emerald `#00543C`** — also culturally resonant: green is the national color of Bangladesh 🇧🇩 (the red circle motif is echoed in the accent). |
| **Avoid threat-cues in a test app** | Red/orange tints used decoratively raise cortisol and test-anxiety (cue-reactivity research on exam settings). Students stare at these screens for 60-minute timed tests. | Old palette's coral `#FF7759` tertiary removed. Red is reserved strictly for semantic errors/countdown urgency. Success feedback uses green — which students subconsciously read as "correct". |
| **Warm accent = reward/motivation** | Amber/gold triggers achievement & optimism associations (trophy/star semantics). High salience against dark text. | Tertiary = **Amber Gold `#B26A00`** used only for streaks, target-band highlights, CTAs that need a "reward" feel. |
| **Near-white, slightly warm backgrounds** | Pure white causes halation & eye strain during long reading sessions; soft off-whites improve sustained focus (reading ergonomics research). Dark mode with pure-black OLED surfaces increases smearing complaints; a lifted charcoal is preferred. | Light bg `#F5FAF7` (mint-tinted paper); dark bg `#0E1512` (charcoal-green). Reading passages get a dedicated **"paper surface"** token (`#FCFEFD`) mimicking print — proven to aid comprehension comfort. |
| **Contrast / WCAG AA** | ≥4.5:1 body text contrast improves accessibility & perceived credibility (WebAIM). | On-surface text `#161D1A` ≈ 15:1; muted text ≈ 7:1; primary button white-on-emerald ≈ 8:1. |

### Shape & layout psychology
- **Consistent corner radius (Form follows feeling):** Mixed radii (8–28dp scattered) create visual noise and lower perceived polish (processing-fluency effect). New scale: **10 / 14 / 20 / 26 / 32 dp** tokens mapped by component size (smaller element → smaller radius), pill shapes only for chips/buttons.
- **Soft elevation over heavy shadows:** Anxiety-reducing interfaces favor flat, airy cards with 1px outlines + subtle tinted shadows ("calm technology"). Cards now use `outlineVariant` hairlines instead of drop shadows.
- **Chunking & reduced cognitive load:** Miller's Law — the Home screen was re-grouped into 3 scannable zones (Progress → Practice → Booking) with clear section headers instead of a wall of tiles.
- **Goal-gradient & loss aversion:** A visible band-score progress ring on Home exploits the goal-gradient effect (people accelerate when they see how close they are to the target).
- **Feedback & momentum:** Button press states, animated-ish progress bars, and success-colored results banners leverage immediate-feedback loops shown to increase study-session retention (Duolingo-style habit loops).
- **Type hierarchy:** Larger weight-jumps between headline/body improve skimming speed. Inter-style geometric sans default, tighter letter-spacing on headlines (typography research: display sizes need negative tracking).

### App icon psychology
- Icons with **a single high-contrast glyph** are remembered and found faster on a home screen (Ni Norman Group: recognition over recall).
- **Round/organic forms** are preferred over sharp ones (barlow & henle: curvature preference).
- Saturated mid-tone backgrounds survive both light AND dark home-screen wallpapers better than white or black.
- New icon: **emerald→deep-green gradient squircle, open-book glyph with a rising amber "progress bar" bookmark** — communicates *study* + *leveling up* at 48dp. The same vector is reused as the in-app splash background (brand consistency builds trust).

## 2. What changed technically
- `ui/theme/Color.kt` — full Emerald Focus palette (light + dark + semantic/band-score tokens).
- `ui/theme/Theme.kt` — complete M3 roles (error, inverse, scrim, surfaceContainer levels), updated shape scheme, tinted shadows.
- `ui/theme/Type.kt` — full typographic scale (display/headline/title/body/label).
- `res/values/colors.xml`, `drawable/ic_launcher_background.xml`, `drawable/ic_launcher_foreground.xml` — new brand colors + redesigned adaptive icon vector.
- `res/drawable/splash_brand.xml` + `res/values/themes.xml` — branded pre-launch splash.
- All 22 screen files recolored via legacy→new token mapping (old deep-blue `#004A77` identity retired; stray Material-green hardcodes unified).
- `HomeScreen.kt` hand-polished: new header greeting card, band-progress hero card, zone-grouped navigation.
