# AGENTS.md — AI Coding Harness Configuration

> **Gartner Reference Implementation:** This file serves as the repository-level **AI Coding Harness** for `weather-app`. Any AI coding agent working in this repository must follow these architectural rules, subagent model routing policies, and mandatory verification gates.

---

## 1. Project Context & Architecture
- **Application**: Real-Time Weather & 7-Day Forecast Web App (`weather-app`)
- **Tech Stack**: React 18, TypeScript 5, Vite 5, Tailwind CSS 3, Vitest, React Testing Library
- **External APIs**:
  - **Primary Weather Forecast**: Open-Meteo Forecast API (`https://api.open-meteo.com/v1/forecast`)
  - **Primary Geocoding**: Open-Meteo Geocoding API (`https://geocoding-api.open-meteo.com/v1/search`)
  - **Fallback / Borough & Regional Geocoding**: OpenStreetMap Photon API (`https://photon.komoot.io/api/`) for multi-word regional queries (e.g., `"North York Ontario"`) and municipal boroughs/districts.

### Directory Structure
```text
e:\Jev\weather-app\
├── AGENTS.md                        # Repository AI Coding Harness (this file)
├── .agents/
│   └── skills/
│       └── weather-feature-harness/ # Project-specific AI skill workflow
│           └── SKILL.md
├── src/
│   ├── types/weather.ts             # Shared TypeScript interfaces (Single Source of Truth)
│   ├── services/weatherApi.ts       # API client, geocoding fallback, and WMO code mappers
│   ├── components/                  # Accessible, glassmorphic React UI components
│   └── __tests__/                   # Vitest + React Testing Library test suites
```

---

## 2. Multi-Model Subagent Routing Policy
When executing multi-file features or refactors, use **Subagent-Driven Development** with tiered model selection:

| Role | Assigned Model Tier | Responsibilities |
|---|---|---|
| **Codebase Researcher** (`research`) | `flash_lite` | Fast, read-only file discovery and interface inspection |
| **Service & Config Engineer** (`self`) | `flash` | Updating `src/types/`, `src/services/`, and build/test configs |
| **UI/UX Component Engineer** (`self`) | `pro` | Designing complex React state, accessibility, keyboard navigation, and Tailwind styling |
| **Verification & Test Engineer** (`self`) | `flash` | Writing unit and component tests in `src/__tests__/` |

---

## 3. Coding, UI & Resilience Standards
1. **Contract-First Development**: Always define or update shared interfaces in `src/types/weather.ts` before spawning parallel UI and Test subagents.
2. **Geocoding & API Resilience**:
   - Never assume single-word city queries. Support multi-token regional queries (e.g., `"North York Ontario"`, `"Toronto, ON"`) via the Open-Meteo + Photon fallback pattern in `src/services/weatherApi.ts`.
   - Always format location labels with `admin1` (province/state) when available (`Name, Admin1, Country`).
3. **Accessibility (a11y)**:
   - Interactive inputs and dropdowns must include proper ARIA attributes (`role="combobox"`, `role="listbox"`, `role="option"`, `aria-expanded`, `aria-selected`) and full keyboard navigation (`ArrowUp`, `ArrowDown`, `Enter`, `Escape`).
4. **Visual Language**:
   - Maintain the dark radial-gradient glassmorphic theme (`glass-panel`, `glass-input`, subtle border highlights, and shimmer loading skeletons).

---

## 4. Mandatory Verification Gate (The Harness Check)
No code change, bugfix, or feature is considered complete until **both** automated verification commands pass with zero errors:

```powershell
# 1. Run the full unit & component test suite (must pass 100%)
npm test

# 2. Run strict TypeScript type-checking and production bundle verification
npm run build
```

---

## 5. Guardrails & Boundaries
- ✅ **Autonomous Execution Allowed**: Adding UI components, extending weather metrics, adding unit tests, improving geocoding accuracy, and refactoring styles.
- ⛔ **Human Approval Required**: Adding paid/keyed third-party APIs, changing core build toolchains, or removing existing test assertions to make a broken build pass.
