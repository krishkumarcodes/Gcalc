# Contributing to Gcalc

Thank you for your interest in contributing to **Gcalc**! We appreciate bug reports, feature suggestions, and code contributions.

## Code of Conduct

By participating in this project, you agree to abide by our [Code of Conduct](CODE_OF_CONDUCT.md).

## Getting Started

1. **Fork the Repository**:
   Fork the repository on GitHub and clone your fork locally:
   ```bash
   git clone https://github.com/krishkumarcodes/Gcalc.git
   cd Gcalc
   ```

2. **Build and Test**:
   Verify your environment by building the project and running the automated test suite:
   ```bash
   gradle :app:assembleDebug
   gradle :app:testDebugUnitTest
   ```

## Development Guidelines

- **Branch Naming**:
  - `feat/feature-name` for new features
  - `fix/bug-description` for bug fixes
  - `docs/documentation-update` for documentation changes
- **Commit Messages**:
  Follow the [Conventional Commits](https://www.conventionalcommits.org/) specification:
  - `feat: add matrix determinant solver`
  - `fix: resolve asymptote rendering in graphing canvas`
  - `docs: update screenshot references in README`
  - `test: add unit test for BODMAS step breakdown`
- **UI Guidelines**:
  - Strictly maintain the Apple-style glassmorphic aesthetic and Material 3 theming.
  - Never use emoji in user-facing components; use vector drawables in rounded glass badges.
  - Ensure all interfaces render correctly on devices with a 360dp width.

## Pull Request Process

1. Ensure all unit tests pass before submitting your PR.
2. Update relevant documentation and add test cases for new functionality.
3. Submit a pull request targeting the `main` branch with a clear description of your changes.

## Release Process (Maintainers)

1. Update version numbers in `app/build.gradle.kts` and `package.json`.
2. Add changelog notes in `CHANGELOG.md` under the appropriate version header.
3. Commit with `chore(release): vX.Y.Z`.
4. Tag the release: `git tag vX.Y.Z && git push origin vX.Y.Z`.
5. The automated GitHub workflow will build the release APK and publish the release.
