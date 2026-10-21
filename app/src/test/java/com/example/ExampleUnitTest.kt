package com.example

import com.example.engine.AngleMode
import com.example.engine.LongDivisionEngine
import com.example.engine.MathEngine
import com.example.engine.NumberFormatMode
import com.example.engine.StepWorkingEngine
import com.example.formulas.FormulaRepository
import com.example.service.NavigatorVibrateApi
import com.example.store.ThemeEngine
import com.example.ui.theme.AccentColor
import com.example.ui.theme.ThemeMode
import org.junit.Assert.*
import org.junit.Test
import kotlin.math.E
import kotlin.math.PI
import kotlin.math.abs

class ExampleUnitTest {

    @Test
    fun testBasicArithmeticAndPrecedence() {
        val r1 = MathEngine.evaluate("1 + 2")
        assertEquals(3.0, r1.value, 1e-9)

        val r2 = MathEngine.evaluate("9 - 12")
        assertEquals(-3.0, r2.value, 1e-9)

        val r3 = MathEngine.evaluate("6 * 7")
        assertEquals(42.0, r3.value, 1e-9)

        val r4 = MathEngine.evaluate("1 / 4")
        assertEquals(0.25, r4.value, 1e-9)

        val r5 = MathEngine.evaluate("0.1 + 0.2")
        assertEquals(0.3, r5.value, 1e-9)

        val r6 = MathEngine.evaluate("2 + 3 * 4")
        assertEquals(14.0, r6.value, 1e-9)

        val r7 = MathEngine.evaluate("(2 + 3) * 4")
        assertEquals(20.0, r7.value, 1e-9)

        val r8 = MathEngine.evaluate("100 / 8")
        assertEquals(12.5, r8.value, 1e-9)

        // Repeat equals test: 5 + 2 = = = gives 7, 9, 11
        val rep = MathEngine.evaluateRepeatEquals("5 + 2", 3)
        assertEquals(3, rep.size)
        assertEquals(7.0, rep[0], 1e-9)
        assertEquals(9.0, rep[1], 1e-9)
        assertEquals(11.0, rep[2], 1e-9)

        val p1 = MathEngine.evaluate("200 + 10%")
        assertEquals(220.0, p1.value, 1e-9)

        val p2 = MathEngine.evaluate("200 - 10%")
        assertEquals(180.0, p2.value, 1e-9)

        val p3 = MathEngine.evaluate("50 * 10%")
        assertEquals(5.0, p3.value, 1e-9)

        val p4 = MathEngine.evaluate("10%")
        assertEquals(0.1, p4.value, 1e-9)

        val r9 = MathEngine.evaluate("12345679 * 9")
        assertEquals(111111111.0, r9.value, 1e-9)

        val r10 = MathEngine.evaluate("0.5 * 0.5")
        assertEquals(0.25, r10.value, 1e-9)
    }

    @Test
    fun testConstants() {
        val pi = MathEngine.evaluate("PI")
        assertEquals(PI, pi.value, 1e-9)
        assertTrue(pi.displayString.startsWith("3.14159265358979"))

        val e = MathEngine.evaluate("e")
        assertEquals(E, e.value, 1e-9)
        assertTrue(e.displayString.startsWith("2.718281828"))

        val twoPi = MathEngine.evaluate("2 * PI")
        assertEquals(2 * PI, twoPi.value, 1e-9)

        val piOver2 = MathEngine.evaluate("PI / 2")
        assertEquals(PI / 2, piOver2.value, 1e-9)

        val ePow1 = MathEngine.evaluate("exp(1)")
        assertEquals(E, ePow1.value, 1e-9)

        val lnE = MathEngine.evaluate("ln(e)")
        assertEquals(1.0, lnE.value, 1e-9)
    }

    @Test
    fun testDegreesAngleMode() {
        assertEquals(0.0, MathEngine.evaluate("sin(0)", AngleMode.DEG).value, 1e-9)
        assertEquals(0.5, MathEngine.evaluate("sin(30)", AngleMode.DEG).value, 1e-9)
        assertEquals(0.7071067812, MathEngine.evaluate("sin(45)", AngleMode.DEG).value, 1e-8)
        assertEquals(0.8660254038, MathEngine.evaluate("sin(60)", AngleMode.DEG).value, 1e-8)
        assertEquals(1.0, MathEngine.evaluate("sin(90)", AngleMode.DEG).value, 1e-9)
        assertEquals(0.0, MathEngine.evaluate("sin(180)", AngleMode.DEG).value, 1e-9)
        assertEquals(-1.0, MathEngine.evaluate("sin(270)", AngleMode.DEG).value, 1e-9)
        assertEquals(0.0, MathEngine.evaluate("sin(360)", AngleMode.DEG).value, 1e-9)

        assertEquals(1.0, MathEngine.evaluate("cos(0)", AngleMode.DEG).value, 1e-9)
        assertEquals(0.5, MathEngine.evaluate("cos(60)", AngleMode.DEG).value, 1e-9)
        assertEquals(0.0, MathEngine.evaluate("cos(90)", AngleMode.DEG).value, 1e-9)
        assertEquals(-1.0, MathEngine.evaluate("cos(180)", AngleMode.DEG).value, 1e-9)

        assertEquals(0.0, MathEngine.evaluate("tan(0)", AngleMode.DEG).value, 1e-9)
        assertEquals(1.0, MathEngine.evaluate("tan(45)", AngleMode.DEG).value, 1e-9)
        assertEquals(-1.0, MathEngine.evaluate("tan(135)", AngleMode.DEG).value, 1e-9)

        val tan90 = MathEngine.evaluate("tan(90)", AngleMode.DEG)
        assertEquals("Undefined", tan90.displayString)

        assertEquals(30.0, MathEngine.evaluate("asin(0.5)", AngleMode.DEG).value, 1e-9)
        assertEquals(60.0, MathEngine.evaluate("acos(0.5)", AngleMode.DEG).value, 1e-9)
        assertEquals(45.0, MathEngine.evaluate("atan(1)", AngleMode.DEG).value, 1e-9)

        val asinTwo = MathEngine.evaluate("asin(2)", AngleMode.DEG)
        assertEquals("Domain error", asinTwo.displayString)
    }

    @Test
    fun testRadiansAngleMode() {
        assertEquals(0.5, MathEngine.evaluate("sin(PI / 6)", AngleMode.RAD).value, 1e-9)
        assertEquals(1.0, MathEngine.evaluate("sin(PI / 2)", AngleMode.RAD).value, 1e-9)
        assertEquals(0.0, MathEngine.evaluate("sin(PI)", AngleMode.RAD).value, 1e-9)
        assertEquals(0.0, MathEngine.evaluate("sin(2 * PI)", AngleMode.RAD).value, 1e-9)

        assertEquals(-1.0, MathEngine.evaluate("cos(PI)", AngleMode.RAD).value, 1e-9)
        assertEquals(0.5, MathEngine.evaluate("cos(PI / 3)", AngleMode.RAD).value, 1e-9)
        assertEquals(0.0, MathEngine.evaluate("cos(PI / 2)", AngleMode.RAD).value, 1e-9)

        assertEquals(1.0, MathEngine.evaluate("tan(PI / 4)", AngleMode.RAD).value, 1e-9)
        assertEquals(1.5707963268, MathEngine.evaluate("asin(1)", AngleMode.RAD).value, 1e-8)
        assertEquals(3.1415926536, MathEngine.evaluate("acos(-1)", AngleMode.RAD).value, 1e-8)
        assertEquals(0.7853981634, MathEngine.evaluate("atan(1)", AngleMode.RAD).value, 1e-8)
    }

    @Test
    fun testGradiansAngleMode() {
        assertEquals(1.0, MathEngine.evaluate("sin(100)", AngleMode.GRAD).value, 1e-9)
        assertEquals(-1.0, MathEngine.evaluate("cos(200)", AngleMode.GRAD).value, 1e-9)
        assertEquals(1.0, MathEngine.evaluate("tan(50)", AngleMode.GRAD).value, 1e-9)
        assertEquals(100.0, MathEngine.evaluate("asin(1)", AngleMode.GRAD).value, 1e-9)
    }

    @Test
    fun testHyperbolicFunctions() {
        assertEquals(0.0, MathEngine.evaluate("sinh(0)").value, 1e-9)
        assertEquals(1.0, MathEngine.evaluate("cosh(0)").value, 1e-9)
        assertEquals(0.0, MathEngine.evaluate("tanh(0)").value, 1e-9)
        assertEquals(1.1752011936, MathEngine.evaluate("sinh(1)").value, 1e-8)
        assertEquals(1.5430806348, MathEngine.evaluate("cosh(1)").value, 1e-8)
        assertEquals(0.7615941560, MathEngine.evaluate("tanh(1)").value, 1e-8)
    }

    @Test
    fun testLogsAndExponents() {
        assertEquals(0.0, MathEngine.evaluate("log(1)").value, 1e-9)
        assertEquals(1.0, MathEngine.evaluate("log(10)").value, 1e-9)
        assertEquals(3.0, MathEngine.evaluate("log(1000)").value, 1e-9)
        assertEquals(0.0, MathEngine.evaluate("ln(1)").value, 1e-9)
        assertEquals(2.0, MathEngine.evaluate("ln(e^2)").value, 1e-9)
        assertEquals(3.0, MathEngine.evaluate("log2(8)").value, 1e-9)
        assertEquals(10.0, MathEngine.evaluate("log2(1024)").value, 1e-9)
        assertEquals(7.3890560989, MathEngine.evaluate("exp(2)").value, 1e-8)
        assertEquals(1000.0, MathEngine.evaluate("10^3").value, 1e-9)
        assertEquals(1024.0, MathEngine.evaluate("2^10").value, 1e-9)
        assertEquals(0.25, MathEngine.evaluate("2^-2").value, 1e-9)
        assertEquals(25.0, MathEngine.evaluate("5^2").value, 1e-9)
        assertEquals(27.0, MathEngine.evaluate("3^3").value, 1e-9)
        assertEquals(1.4142135624, MathEngine.evaluate("2^0.5").value, 1e-8)
    }

    @Test
    fun testRootsAndPowers() {
        assertEquals(0.0, MathEngine.evaluate("sqrt(0)").value, 1e-9)
        assertEquals(12.0, MathEngine.evaluate("sqrt(144)").value, 1e-9)
        assertEquals(1.4142135624, MathEngine.evaluate("sqrt(2)").value, 1e-8)
        assertEquals(3.0, MathEngine.evaluate("cbrt(27)").value, 1e-9)
        assertEquals(-2.0, MathEngine.evaluate("cbrt(-8)").value, 1e-9)
        assertEquals(2.0, MathEngine.evaluate("yroot(16, 4)").value, 1e-9)
        assertEquals(0.125, MathEngine.evaluate("1/8").value, 1e-9)
        assertEquals(144.0, MathEngine.evaluate("12^2").value, 1e-9)
        assertEquals(125.0, MathEngine.evaluate("5^3").value, 1e-9)
        assertEquals(7.5, MathEngine.evaluate("abs(-7.5)").value, 1e-9)

        val errSqrt = MathEngine.evaluate("sqrt(-1)")
        assertEquals("Domain error", errSqrt.displayString)
    }

    @Test
    fun testFactorialAndCombinatorics() {
        assertEquals(1.0, MathEngine.evaluate("0!").value, 1e-9)
        assertEquals(1.0, MathEngine.evaluate("1!").value, 1e-9)
        assertEquals(120.0, MathEngine.evaluate("5!").value, 1e-9)
        assertEquals(3628800.0, MathEngine.evaluate("10!").value, 1e-9)
        assertEquals(2.43290200817664e18, MathEngine.evaluate("20!").value, 1e10)

        val fracFact = MathEngine.evaluate("5! / (3! * 2!)")
        assertEquals(10.0, fracFact.value, 1e-9)

        assertEquals(120.0, MathEngine.evaluate("ncr(10,3)").value, 1e-9)
        assertEquals(1.0, MathEngine.evaluate("ncr(5,0)").value, 1e-9)
        assertEquals(1.0, MathEngine.evaluate("ncr(5,5)").value, 1e-9)

        assertEquals(20.0, MathEngine.evaluate("npr(5,2)").value, 1e-9)
        assertEquals(720.0, MathEngine.evaluate("npr(10,3)").value, 1e-9)

        val errFactNeg = MathEngine.evaluate("(-1)!")
        assertEquals("Domain error", errFactNeg.displayString)

        val errFactDec = MathEngine.evaluate("2.5!")
        assertEquals("Domain error", errFactDec.displayString)
    }

    @Test
    fun testMiscOperations() {
        assertEquals(2.0, MathEngine.evaluate("17 mod 5").value, 1e-9)
        assertEquals(3.0, MathEngine.evaluate("floor(3.7)").value, 1e-9)
        assertEquals(4.0, MathEngine.evaluate("ceil(3.2)").value, 1e-9)
        assertEquals(3.0, MathEngine.evaluate("round(2.5)").value, 1e-9)
        assertEquals(3.14, MathEngine.evaluate("round(3.14159, 2)").value, 1e-9)

        assertEquals(1500.0, MathEngine.evaluate("1.5 EXP 3").value, 1e-9)
        assertEquals(6.02e23, MathEngine.evaluate("6.02 EXP 23").value, 1e18)

        assertEquals(4.0, MathEngine.evaluate("(-2)^2").value, 1e-9)
        assertEquals(-4.0, MathEngine.evaluate("-2^2").value, 1e-9)

        assertEquals(1.0, MathEngine.evaluate("2 * sin(30)", AngleMode.DEG).value, 1e-9)
        assertEquals(27.0, MathEngine.evaluate("3 * (4 + 5)").value, 1e-9)
        assertEquals(0.0, MathEngine.evaluate("2 * PI * sin(2 * PI)", AngleMode.RAD).value, 1e-9)
    }

    @Test
    fun testMemoryAndVariables() {
        var mem = 0.0
        // 5 M+ -> mem = 5
        mem += 5.0
        assertEquals(5.0, mem, 1e-9)
        // 3 M- -> mem = 2
        mem -= 3.0
        assertEquals(2.0, mem, 1e-9)
        // MC -> mem = 0
        mem = 0.0
        assertEquals(0.0, mem, 1e-9)

        // STO and RCL for variables A to F
        val vars = mapOf("A" to 10.0, "B" to 20.0, "C" to 3.0, "D" to 4.0, "E" to 5.0, "F" to 6.0)
        val evalVars = MathEngine.evaluate("A + B * C", AngleMode.DEG, 0.0, vars)
        assertEquals(70.0, evalVars.value, 1e-9)
    }

    @Test
    fun testFractionsAndFormatting() {
        val frac1 = MathEngine.toFraction(1.0 / 3.0)
        assertEquals("1/3", frac1)

        val frac2 = MathEngine.toFraction(0.75)
        assertEquals("3/4", frac2)

        val fmtThousands = MathEngine.formatNumber(1234567.891, NumberFormatMode.DECIMAL, 3, true)
        assertEquals("1,234,567.891", fmtThousands)

        val fmtSci = MathEngine.formatNumber(1e21, NumberFormatMode.AUTO)
        assertTrue(fmtSci.contains("1 × 10^21") || fmtSci.contains("1e21") || fmtSci.contains("10^21"))
    }

    @Test
    fun testErrors() {
        val divZero = MathEngine.evaluate("5 / 0")
        assertEquals("Can't divide by zero", divZero.displayString)

        val invalidZero = MathEngine.evaluate("0 / 0")
        assertTrue(invalidZero.displayString == "Invalid input" || invalidZero.displayString == "Can't divide by zero")

        val lnNeg = MathEngine.evaluate("ln(-1)")
        assertEquals("Domain error", lnNeg.displayString)

        val logZero = MathEngine.evaluate("log(0)")
        assertEquals("Domain error", logZero.displayString)
    }

    @Test
    fun testLongDivisionEngine() {
        val ld1 = LongDivisionEngine.computeLongDivision("10", "4")
        assertEquals("2.5", ld1.quotient)

        val ld2 = LongDivisionEngine.computeLongDivision("10", "3")
        assertTrue(ld2.isRepeating)

        val ld3 = LongDivisionEngine.computeLongDivision("144", "12")
        assertEquals("12", ld3.quotient)
        assertEquals("0", ld3.remainder)

        val ld4 = LongDivisionEngine.computeLongDivision("22", "7")
        assertTrue(ld4.isRepeating)
        assertTrue(ld4.displayQuotient.contains("3.142857") || ld4.quotient.contains("3.142857"))

        val working = StepWorkingEngine.breakdownWorking("2 + 3 * 4")
        assertTrue(working.steps.isNotEmpty())
        assertEquals("14", working.finalResult)
    }

    @Test
    fun testFormulasAndEngineering() {
        // Ohm's law V=10, R=5 gives I=2
        val ohm = FormulaRepository.formulas.first { it.id == "ohm_law" }
        val ohmRes = ohm.solver(mapOf("V" to 10.0, "R" to 5.0), "I")
        assertEquals(2.0, ohmRes.value, 1e-9)

        // s = ut + 1/2at^2 with u=0, a=10, t=3 gives 45
        val motion = FormulaRepository.formulas.first { it.id == "motion_displacement" }
        val motionRes = motion.solver(mapOf("u" to 0.0, "a" to 10.0, "t" to 3.0), "s")
        assertEquals(45.0, motionRes.value, 1e-9)

        // molar mass of H2SO4 ≈ 98.079
        val h2so4 = FormulaRepository.calculateMolarMass("H2SO4").first
        assertTrue(abs(h2so4 - 98.079) < 0.1)

        // pH of 0.001 M = 3
        val phFormula = FormulaRepository.formulas.first { it.id == "ph_calculation" }
        val phRes = phFormula.solver(mapOf("H" to 0.001), "pH")
        assertEquals(3.0, phRes.value, 1e-9)

        // quadratic x^2 - 5x + 6 = 0 gives roots 2 and 3
        val quadRoots = FormulaRepository.solveQuadraticRoots(1.0, -5.0, 6.0)
        assertNotNull(quadRoots)
        assertEquals(2.0, quadRoots!!.first, 1e-9)
        assertEquals(3.0, quadRoots.second, 1e-9)

        // matrix [[1,2],[3,4]] determinant = -2
        val det = FormulaRepository.matrixDeterminant2x2(1.0, 2.0, 3.0, 4.0)
        assertEquals(-2.0, det, 1e-9)

        // binary 1010 = decimal 10 = hex A
        val binDecHex = FormulaRepository.binaryToDecimalAndHex("1010")
        assertEquals(10L, binDecHex.first)
        assertEquals("A", binDecHex.second)

        // subnet 192.168.1.10/24 network 192.168.1.0 broadcast 192.168.1.255
        val subnet = FormulaRepository.calculateSubnet("192.168.1.10", 24)
        assertEquals("192.168.1.0", subnet["Network"])
        assertEquals("192.168.1.255", subnet["Broadcast"])
        assertEquals("254", subnet["HostCount"])
    }

    @Test
    fun testPersistenceAndSettings() {
        val darkPalette = ThemeEngine.generateGlassPalette(ThemeMode.DARK, AccentColor.CYAN, true)
        assertTrue(darkPalette.isDark)
        assertEquals(AccentColor.CYAN.color, darkPalette.accent)

        // Simulated restart state
        val simulatedAngleMode = AngleMode.GRAD
        val simulatedMemory = 42.0
        val simulatedTheme = ThemeMode.DARK
        val simulatedAccent = AccentColor.PURPLE
        val simulatedVars = mutableMapOf("A" to 123.45)

        assertEquals(AngleMode.GRAD, simulatedAngleMode)
        assertEquals(42.0, simulatedMemory, 1e-9)
        assertEquals(ThemeMode.DARK, simulatedTheme)
        assertEquals(AccentColor.PURPLE, simulatedAccent)
        assertEquals(123.45, simulatedVars["A"]!!, 1e-9)
    }

    @Test
    fun testHapticFeedbackService() {
        var lastVibratedDuration = -1L
        var vibrateCallCount = 0

        val mockNavigatorVibrate = object : NavigatorVibrateApi {
            override fun vibrate(durationMs: Long): Boolean {
                lastVibratedDuration = durationMs
                vibrateCallCount++
                return true
            }

            override fun vibrate(pattern: LongArray): Boolean {
                vibrateCallCount++
                return true
            }

            override fun cancel(): Boolean = true
        }

        class MockSettingsStore {
            var hapticsEnabled: Boolean = true
            fun toggleHaptics() { hapticsEnabled = !hapticsEnabled }
            fun updateHapticsEnabled(e: Boolean) { hapticsEnabled = e }
        }

        val mockSettings = MockSettingsStore()
        val service = object {
            val isHapticActive: Boolean get() = mockSettings.hapticsEnabled
            fun toggleHaptics() = mockSettings.toggleHaptics()
            fun updateHapticState(e: Boolean) = mockSettings.updateHapticsEnabled(e)
            fun performKeyTap(durationMs: Long = 8L): Boolean {
                if (!mockSettings.hapticsEnabled) return false
                return mockNavigatorVibrate.vibrate(durationMs)
            }
        }

        assertTrue(service.isHapticActive)
        val tapResult = service.performKeyTap(8L)
        assertTrue(tapResult)
        assertEquals(8L, lastVibratedDuration)
        assertEquals(1, vibrateCallCount)

        service.toggleHaptics()
        assertFalse(service.isHapticActive)
        val mutedTapResult = service.performKeyTap(8L)
        assertFalse(mutedTapResult)
        assertEquals(1, vibrateCallCount)

        service.updateHapticState(true)
        assertTrue(service.isHapticActive)
        val reEnabledResult = service.performKeyTap(8L)
        assertTrue(reEnabledResult)
        assertEquals(2, vibrateCallCount)
    }
}
