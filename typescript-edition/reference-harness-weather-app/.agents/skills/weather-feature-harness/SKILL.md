---
name: weather-feature-harness
description: Gartner-aligned AI Coding Harness skill for planning, parallel subagent implementation, and automated verification of features in weather-app.
---

# Weather App Feature Harness Skill

Use this skill whenever adding a new feature, weather metric, or UI enhancement to `e:\Jev\weather-app`. It enforces Gartner's **Agentic SDLC Workflow** (Contract -> Parallel Subagents -> Automated Verification Gate).

---

## Step 1: Contract Definition (Preventing Subagent Drift)
Before writing component code or tests, inspect and update `src/types/weather.ts`:
1. Define the exact TypeScript interfaces (`WeatherData`, `DailyForecast`, `GeocodingResult`, or new props).
2. Share these exact interfaces in the prompt of every subagent so the Service, UI, and Test agents work against an identical contract.

---

## Step 2: Parallel Multi-Model Subagent Dispatch
For multi-file features, spawn parallel subagents matched to the appropriate model tier:

1. **API / Data Agent (`Model: "flash"`)**
   - Updates `src/services/weatherApi.ts`.
   - Ensures Open-Meteo parameters and Photon geocoding fallbacks handle edge cases (null values, multi-word queries like `"North York Ontario"`, network errors).
2. **UI Component Agent (`Model: "pro"`)**
   - Updates or creates components in `src/components/` and `src/App.tsx`.
   - Applies glassmorphic Tailwind classes (`glass-panel`), responsive layout, and full keyboard/ARIA accessibility.
3. **Test Agent (`Model: "flash"`)**
   - Updates or creates test files in `src/__tests__/`.
   - Mocks `global.fetch` cleanly (handling multi-step fallback calls safely) and verifies positive, zero, negative, and error states.

---

## Step 3: Mandatory Verification Harness Gate
After subagents finish, the orchestrator agent MUST execute the verification harness in `e:\Jev\weather-app`:

```powershell
cd "e:\Jev\weather-app"
npm test
npm run build
```

### Gate Criteria:
- [ ] All Vitest test suites pass (`0` failures).
- [ ] TypeScript compiler (`tsc`) reports `0` type errors.
- [ ] Vite production build (`vite build`) succeeds cleanly.
- [ ] If any check fails, diagnose and fix before presenting results to the user.
