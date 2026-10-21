# Gcalc Architecture

## Overview
Gcalc is structured following modern Android Clean Architecture and MVVM design principles using Kotlin Coroutines, Jetpack Compose, and Material Design 3.

```
com.example/
├── components/     # UI building blocks (GlassCard, GlassNumPad, Logo, Dialogs)
├── engine/         # Pure math logic (MathEngine, UnitConverterEngine, StepWorkingEngine, LongDivisionEngine)
├── formulas/       # Formula repository, chemical element data (118 elements), scientific constants
├── screens/        # Primary composable screens (CalculatorScreen, ToolsScreen, HistorySettingsScreen)
│   └── tools/      # Dedicated screen composables for each tool (UnitConverter, Programmer, EquationSolver, Graphing, Statistics, PeriodicTable, DateTime, Finance, Constants)
├── store/          # Application state persistence (SettingsStore, ThemeEngine)
└── ui/theme/       # Theming definitions (GlassTheme, Color palettes, Typography)
```

## Core Modules

### 1. Mathematical Parsing & Calculation (`com.example.engine`)
- **`MathEngine.kt`**: High-precision math evaluator with recursive descent parsing, automatic BODMAS operator precedence, precision trigonometry with exact angle modes (DEG, RAD, GRAD), and exact fraction conversion via continued fraction approximation.
- **`StepWorkingEngine.kt`**: Formats mathematical expressions into structured notebook steps, ensuring divisions are rendered as genuine stacked fractions.
- **`LongDivisionEngine.kt`**: Computes bracket-based visual step-by-step long division for integer arithmetic.

### 2. Theming & Glassmorphism (`com.example.store` & `com.example.ui.theme`)
- **`ThemeEngine.kt`**: Dynamically generates surface, background, and highlight gradient color palettes using HSL space transformations based on the chosen Material Accent Color.
- **`GlassTheme.kt`**: CompositionLocal provider providing frosted glass borders, low opacity glass containers, and adaptive contrast foregrounds.

### 3. Engineering Tools Suite (`com.example.screens.tools`)
- Fully modular tools operating independently with local persistence, real vector iconography, and the custom on-screen number pad.
