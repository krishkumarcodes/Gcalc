package com.example.formulas

import com.example.engine.MathEngine
import kotlin.math.*

object FormulaRepository {

    val constants: List<ScientificConstant> = listOf(
        ScientificConstant("c", "c", "Speed of Light in Vacuum", 299792458.0, "m/s", "299,792,458 m/s", "Speed at which light waves propagate through vacuum."),
        ScientificConstant("G", "G", "Newtonian Constant of Gravitation", 6.67430e-11, "m³/(kg·s²)", "6.67430 × 10⁻¹¹ m³/(kg·s²)", "Key constant in Newton's law of universal gravitation."),
        ScientificConstant("g", "g", "Standard Acceleration of Gravity", 9.80665, "m/s²", "9.80665 m/s²", "Nominal gravitational acceleration of an object in a vacuum near Earth's surface."),
        ScientificConstant("h", "h", "Planck Constant", 6.62607015e-34, "J·s", "6.62607 × 10⁻³⁴ J·s", "Relates photon energy to its frequency."),
        ScientificConstant("ℏ", "ℏ", "Reduced Planck Constant", 1.054571817e-34, "J·s", "1.05457 × 10⁻³⁴ J·s", "Dirac constant, h divided by 2π."),
        ScientificConstant("k_B", "k_B", "Boltzmann Constant", 1.380649e-23, "J/K", "1.380649 × 10⁻²³ J/K", "Relates the average relative thermal energy of particles in a gas with temperature."),
        ScientificConstant("N_A", "N_A", "Avogadro Constant", 6.02214076e23, "mol⁻¹", "6.02214 × 10²³ mol⁻¹", "Number of constituent particles per mole of substance."),
        ScientificConstant("R", "R", "Molar Gas Constant", 8.314462618, "J/(mol·K)", "8.31446 J/(mol·K)", "Constant in the ideal gas law PV = nRT."),
        ScientificConstant("e", "e", "Elementary Charge", 1.602176634e-19, "C", "1.60218 × 10⁻¹⁹ C", "Electric charge carried by a single proton."),
        ScientificConstant("m_e", "m_e", "Electron Rest Mass", 9.1093837015e-31, "kg", "9.10938 × 10⁻³¹ kg", "Mass of a stationary electron."),
        ScientificConstant("m_p", "m_p", "Proton Rest Mass", 1.67262192369e-27, "kg", "1.67262 × 10⁻²⁷ kg", "Mass of a stationary proton."),
        ScientificConstant("m_n", "m_n", "Neutron Rest Mass", 1.67492749804e-27, "kg", "1.67493 × 10⁻²⁷ kg", "Mass of a stationary neutron."),
        ScientificConstant("ε_0", "ε_0", "Vacuum Electric Permittivity", 8.8541878128e-12, "F/m", "8.85419 × 10⁻¹² F/m", "Capability of vacuum to permit electric fields."),
        ScientificConstant("μ_0", "μ_0", "Vacuum Magnetic Permeability", 1.25663706212e-6, "N/A²", "1.25664 × 10⁻⁶ N/A²", "Magnetic resistance in classical vacuum."),
        ScientificConstant("k_e", "k_e", "Coulomb Constant", 8.9875517923e9, "N·m²/C²", "8.98755 × 10⁹ N·m²/C²", "Proportionality factor in Coulomb's Law (1 / 4πε0)."),
        ScientificConstant("σ", "σ", "Stefan-Boltzmann Constant", 5.670374419e-8, "W/(m²·K⁴)", "5.670374419 × 10⁻⁸ W/(m²·K⁴)", "Blackbody radiative heat flux constant."),
        ScientificConstant("F", "F", "Faraday Constant", 96485.33212, "C/mol", "96,485.33 C/mol", "Magnitude of electric charge per mole of electrons."),
        ScientificConstant("R_∞", "R_∞", "Rydberg Constant", 10973731.568160, "m⁻¹", "1.09737 × 10⁷ m⁻¹", "Fundamental constant relating to atomic spectra."),
        ScientificConstant("u", "u", "Unified Atomic Mass Unit (Dalton)", 1.66053906660e-27, "kg", "1.66054 × 10⁻²⁷ kg", "Standard unit for mass on an atomic scale.")
    )

    val periodicTable: List<ChemicalElement> = listOf(
        ChemicalElement(1, "H", "Hydrogen", 1.008, "Nonmetal", 1, 1),
        ChemicalElement(2, "He", "Helium", 4.0026, "Noble Gas", 18, 1),
        ChemicalElement(3, "Li", "Lithium", 6.94, "Alkali Metal", 1, 2),
        ChemicalElement(4, "Be", "Beryllium", 9.0122, "Alkaline Earth", 2, 2),
        ChemicalElement(5, "B", "Boron", 10.81, "Metalloid", 13, 2),
        ChemicalElement(6, "C", "Carbon", 12.011, "Nonmetal", 14, 2),
        ChemicalElement(7, "N", "Nitrogen", 14.007, "Nonmetal", 15, 2),
        ChemicalElement(8, "O", "Oxygen", 15.999, "Nonmetal", 16, 2),
        ChemicalElement(9, "F", "Fluorine", 18.998, "Halogen", 17, 2),
        ChemicalElement(10, "Ne", "Neon", 20.180, "Noble Gas", 18, 2),
        ChemicalElement(11, "Na", "Sodium", 22.990, "Alkali Metal", 1, 3),
        ChemicalElement(12, "Mg", "Magnesium", 24.305, "Alkaline Earth", 2, 3),
        ChemicalElement(13, "Al", "Aluminium", 26.982, "Post-transition", 13, 3),
        ChemicalElement(14, "Si", "Silicon", 28.085, "Metalloid", 14, 3),
        ChemicalElement(15, "P", "Phosphorus", 30.974, "Nonmetal", 15, 3),
        ChemicalElement(16, "S", "Sulfur", 32.06, "Nonmetal", 16, 3),
        ChemicalElement(17, "Cl", "Chlorine", 35.45, "Halogen", 17, 3),
        ChemicalElement(18, "Ar", "Argon", 39.948, "Noble Gas", 18, 3),
        ChemicalElement(19, "K", "Potassium", 39.098, "Alkali Metal", 1, 4),
        ChemicalElement(20, "Ca", "Calcium", 40.078, "Alkaline Earth", 2, 4),
        ChemicalElement(21, "Sc", "Scandium", 44.956, "Transition Metal", 3, 4),
        ChemicalElement(22, "Ti", "Titanium", 47.867, "Transition Metal", 4, 4),
        ChemicalElement(23, "V", "Vanadium", 50.942, "Transition Metal", 5, 4),
        ChemicalElement(24, "Cr", "Chromium", 51.996, "Transition Metal", 6, 4),
        ChemicalElement(25, "Mn", "Manganese", 54.938, "Transition Metal", 7, 4),
        ChemicalElement(26, "Fe", "Iron", 55.845, "Transition Metal", 8, 4),
        ChemicalElement(27, "Co", "Cobalt", 58.933, "Transition Metal", 9, 4),
        ChemicalElement(28, "Ni", "Nickel", 58.693, "Transition Metal", 10, 4),
        ChemicalElement(29, "Cu", "Copper", 63.546, "Transition Metal", 11, 4),
        ChemicalElement(30, "Zn", "Zinc", 65.38, "Transition Metal", 12, 4),
        ChemicalElement(31, "Ga", "Gallium", 69.723, "Post-transition", 13, 4),
        ChemicalElement(32, "Ge", "Germanium", 72.630, "Metalloid", 14, 4),
        ChemicalElement(33, "As", "Arsenic", 74.922, "Metalloid", 15, 4),
        ChemicalElement(34, "Se", "Selenium", 78.971, "Nonmetal", 16, 4),
        ChemicalElement(35, "Br", "Bromine", 79.904, "Halogen", 17, 4),
        ChemicalElement(36, "Kr", "Krypton", 83.798, "Noble Gas", 18, 4),
        ChemicalElement(37, "Rb", "Rubidium", 85.468, "Alkali Metal", 1, 5),
        ChemicalElement(38, "Sr", "Strontium", 87.62, "Alkaline Earth", 2, 5),
        ChemicalElement(39, "Y", "Yttrium", 88.906, "Transition Metal", 3, 5),
        ChemicalElement(40, "Zr", "Zirconium", 91.224, "Transition Metal", 4, 5),
        ChemicalElement(41, "Nb", "Niobium", 92.906, "Transition Metal", 5, 5),
        ChemicalElement(42, "Mo", "Molybdenum", 95.95, "Transition Metal", 6, 5),
        ChemicalElement(43, "Tc", "Technetium", 98.0, "Transition Metal", 7, 5),
        ChemicalElement(44, "Ru", "Ruthenium", 101.07, "Transition Metal", 8, 5),
        ChemicalElement(45, "Rh", "Rhodium", 102.91, "Transition Metal", 9, 5),
        ChemicalElement(46, "Pd", "Palladium", 106.42, "Transition Metal", 10, 5),
        ChemicalElement(47, "Ag", "Silver", 107.87, "Transition Metal", 11, 5),
        ChemicalElement(48, "Cd", "Cadmium", 112.41, "Transition Metal", 12, 5),
        ChemicalElement(49, "In", "Indium", 114.82, "Post-transition", 13, 5),
        ChemicalElement(50, "Sn", "Tin", 118.71, "Post-transition", 14, 5),
        ChemicalElement(51, "Sb", "Antimony", 121.76, "Metalloid", 15, 5),
        ChemicalElement(52, "Te", "Tellurium", 127.60, "Metalloid", 16, 5),
        ChemicalElement(53, "I", "Iodine", 126.90, "Halogen", 17, 5),
        ChemicalElement(54, "Xe", "Xenon", 131.29, "Noble Gas", 18, 5),
        ChemicalElement(55, "Cs", "Cesium", 132.91, "Alkali Metal", 1, 6),
        ChemicalElement(56, "Ba", "Barium", 137.33, "Alkaline Earth", 2, 6),
        ChemicalElement(57, "La", "Lanthanum", 138.91, "Lanthanide", 3, 6),
        ChemicalElement(58, "Ce", "Cerium", 140.12, "Lanthanide", 4, 9), // 9 as pseudo period for bottom rows
        ChemicalElement(59, "Pr", "Praseodymium", 140.91, "Lanthanide", 5, 9),
        ChemicalElement(60, "Nd", "Neodymium", 144.24, "Lanthanide", 6, 9),
        ChemicalElement(61, "Pm", "Promethium", 145.0, "Lanthanide", 7, 9),
        ChemicalElement(62, "Sm", "Samarium", 150.36, "Lanthanide", 8, 9),
        ChemicalElement(63, "Eu", "Europium", 151.96, "Lanthanide", 9, 9),
        ChemicalElement(64, "Gd", "Gadolinium", 157.25, "Lanthanide", 10, 9),
        ChemicalElement(65, "Tb", "Terbium", 158.93, "Lanthanide", 11, 9),
        ChemicalElement(66, "Dy", "Dysprosium", 162.50, "Lanthanide", 12, 9),
        ChemicalElement(67, "Ho", "Holmium", 164.93, "Lanthanide", 13, 9),
        ChemicalElement(68, "Er", "Erbium", 167.26, "Lanthanide", 14, 9),
        ChemicalElement(69, "Tm", "Thulium", 168.93, "Lanthanide", 15, 9),
        ChemicalElement(70, "Yb", "Ytterbium", 173.05, "Lanthanide", 16, 9),
        ChemicalElement(71, "Lu", "Lutetium", 174.97, "Lanthanide", 17, 9),
        ChemicalElement(72, "Hf", "Hafnium", 178.49, "Transition Metal", 4, 6),
        ChemicalElement(73, "Ta", "Tantalum", 180.95, "Transition Metal", 5, 6),
        ChemicalElement(74, "W", "Tungsten", 183.84, "Transition Metal", 6, 6),
        ChemicalElement(75, "Re", "Rhenium", 186.21, "Transition Metal", 7, 6),
        ChemicalElement(76, "Os", "Osmium", 190.23, "Transition Metal", 8, 6),
        ChemicalElement(77, "Ir", "Iridium", 192.22, "Transition Metal", 9, 6),
        ChemicalElement(78, "Pt", "Platinum", 195.08, "Transition Metal", 10, 6),
        ChemicalElement(79, "Au", "Gold", 196.97, "Transition Metal", 11, 6),
        ChemicalElement(80, "Hg", "Mercury", 200.59, "Transition Metal", 12, 6),
        ChemicalElement(81, "Tl", "Thallium", 204.38, "Post-transition", 13, 6),
        ChemicalElement(82, "Pb", "Lead", 207.2, "Post-transition", 14, 6),
        ChemicalElement(83, "Bi", "Bismuth", 208.98, "Post-transition", 15, 6),
        ChemicalElement(84, "Po", "Polonium", 209.0, "Metalloid", 16, 6),
        ChemicalElement(85, "At", "Astatine", 210.0, "Halogen", 17, 6),
        ChemicalElement(86, "Rn", "Radon", 222.0, "Noble Gas", 18, 6),
        ChemicalElement(87, "Fr", "Francium", 223.0, "Alkali Metal", 1, 7),
        ChemicalElement(88, "Ra", "Radium", 226.0, "Alkaline Earth", 2, 7),
        ChemicalElement(89, "Ac", "Actinium", 227.0, "Actinide", 3, 7),
        ChemicalElement(90, "Th", "Thorium", 232.04, "Actinide", 4, 10),
        ChemicalElement(91, "Pa", "Protactinium", 231.04, "Actinide", 5, 10),
        ChemicalElement(92, "U", "Uranium", 238.03, "Actinide", 6, 10),
        ChemicalElement(93, "Np", "Neptunium", 237.0, "Actinide", 7, 10),
        ChemicalElement(94, "Pu", "Plutonium", 244.0, "Actinide", 8, 10),
        ChemicalElement(95, "Am", "Americium", 243.0, "Actinide", 9, 10),
        ChemicalElement(96, "Cm", "Curium", 247.0, "Actinide", 10, 10),
        ChemicalElement(97, "Bk", "Berkelium", 247.0, "Actinide", 11, 10),
        ChemicalElement(98, "Cf", "Californium", 251.0, "Actinide", 12, 10),
        ChemicalElement(99, "Es", "Einsteinium", 252.0, "Actinide", 13, 10),
        ChemicalElement(100, "Fm", "Fermium", 257.0, "Actinide", 14, 10),
        ChemicalElement(101, "Md", "Mendelevium", 258.0, "Actinide", 15, 10),
        ChemicalElement(102, "No", "Nobelium", 259.0, "Actinide", 16, 10),
        ChemicalElement(103, "Lr", "Lawrencium", 262.0, "Actinide", 17, 10),
        ChemicalElement(104, "Rf", "Rutherfordium", 267.0, "Transition Metal", 4, 7),
        ChemicalElement(105, "Db", "Dubnium", 270.0, "Transition Metal", 5, 7),
        ChemicalElement(106, "Sg", "Seaborgium", 271.0, "Transition Metal", 6, 7),
        ChemicalElement(107, "Bh", "Bohrium", 270.0, "Transition Metal", 7, 7),
        ChemicalElement(108, "Hs", "Hassium", 277.0, "Transition Metal", 8, 7),
        ChemicalElement(109, "Mt", "Meitnerium", 276.0, "Unknown", 9, 7),
        ChemicalElement(110, "Ds", "Darmstadtium", 281.0, "Unknown", 10, 7),
        ChemicalElement(111, "Rg", "Roentgenium", 280.0, "Unknown", 11, 7),
        ChemicalElement(112, "Cn", "Copernicium", 285.0, "Transition Metal", 12, 7),
        ChemicalElement(113, "Nh", "Nihonium", 284.0, "Unknown", 13, 7),
        ChemicalElement(114, "Fl", "Flerovium", 289.0, "Post-transition", 14, 7),
        ChemicalElement(115, "Mc", "Moscovium", 288.0, "Unknown", 15, 7),
        ChemicalElement(116, "Lv", "Livermorium", 293.0, "Unknown", 16, 7),
        ChemicalElement(117, "Ts", "Tennessine", 294.0, "Unknown", 17, 7),
        ChemicalElement(118, "Og", "Oganesson", 294.0, "Unknown", 18, 7)
    )

    private val elementMassMap: Map<String, Double> by lazy {
        periodicTable.associate { it.symbol to it.atomicMass }
    }

    /**
     * Calculates molar mass of a chemical formula.
     */
    fun calculateMolarMass(formulaStr: String): Pair<Double, Map<String, Int>> {
        val counts = mutableMapOf<String, Int>()
        try {
            parseFormulaRecursive(formulaStr, 1, counts)
        } catch (_: Exception) {}
        
        var totalMass = 0.0
        counts.forEach { (symbol, count) ->
            totalMass += (elementMassMap[symbol] ?: 0.0) * count
        }
        return totalMass to counts
    }

    private fun parseFormulaRecursive(formula: String, multiplier: Int, counts: MutableMap<String, Int>) {
        var i = 0
        while (i < formula.length) {
            when {
                formula[i] == '(' -> {
                    var bracketLevel = 1
                    val start = i + 1
                    i++
                    while (i < formula.length && bracketLevel > 0) {
                        if (formula[i] == '(') bracketLevel++
                        if (formula[i] == ')') bracketLevel--
                        i++
                    }
                    val subFormula = formula.substring(start, i - 1)
                    val subMultiplierMatch = Regex("^(\\d+)").find(formula.substring(i))
                    val subMult = subMultiplierMatch?.value?.toInt() ?: 1
                    parseFormulaRecursive(subFormula, multiplier * subMult, counts)
                    if (subMultiplierMatch != null) i += subMultiplierMatch.value.length
                }
                formula[i].isUpperCase() -> {
                    val start = i
                    i++
                    while (i < formula.length && formula[i].isLowerCase()) i++
                    val symbol = formula.substring(start, i)
                    val countMatch = Regex("^(\\d+)").find(formula.substring(i))
                    val count = countMatch?.value?.toInt() ?: 1
                    counts[symbol] = (counts[symbol] ?: 0) + count * multiplier
                    if (countMatch != null) i += countMatch.value.length
                }
                formula[i] == '.' -> {
                    // Hydrate dot, e.g. CuSO4.5H2O
                    i++
                    val subMultMatch = Regex("^(\\d+)").find(formula.substring(i))
                    val subMult = subMultMatch?.value?.toInt() ?: 1
                    if (subMultMatch != null) i += subMultMatch.value.length
                    parseFormulaRecursive(formula.substring(i), multiplier * subMult, counts)
                    break
                }
                else -> i++
            }
        }
    }

    val formulas: List<Formula> = listOf(
        Formula(
            id = "ohm_law",
            name = "Ohm's Law",
            category = FormulaCategory.PHYSICS,
            subcategory = "Electricity",
            mathExpression = "V = I * R",
            explanation = "Voltage equals current times resistance.",
            variables = listOf(
                FormulaVariable("V", "Voltage", "V", 0.0),
                FormulaVariable("I", "Current", "A", 0.0),
                FormulaVariable("R", "Resistance", "Ω", 0.0)
            ),
            tags = listOf("Physics", "Electricity"),
            solver = { values, target ->
                val v = values["V"] ?: 0.0
                val i = values["I"] ?: 0.0
                val r = values["R"] ?: 0.0
                val res = when(target) {
                    "V" -> i * r
                    "I" -> if(r != 0.0) v / r else 0.0
                    else -> if(i != 0.0) v / i else 0.0
                }
                FormulaResult(target, target, res, "", emptyList())
            }
        ),
        Formula(
            id = "quadratic",
            name = "Quadratic Formula",
            category = FormulaCategory.MATHS,
            subcategory = "Algebra",
            mathExpression = "ax² + bx + c = 0",
            explanation = "Solves for roots of a quadratic equation.",
            variables = listOf(
                FormulaVariable("a", "Coefficient a", "", 1.0),
                FormulaVariable("b", "Coefficient b", "", 0.0),
                FormulaVariable("c", "Coefficient c", "", 0.0)
            ),
            tags = listOf("Math", "Algebra"),
            solver = { values, target ->
                val a = values["a"] ?: 1.0
                val b = values["b"] ?: 0.0
                val c = values["c"] ?: 0.0
                val d = b*b - 4*a*c
                val res = if(d >= 0) (-b + sqrt(d)) / (2*a) else 0.0
                FormulaResult(target, target, res, "", emptyList())
            }
        ),
        Formula(
            id = "motion_displacement",
            name = "Displacement",
            category = FormulaCategory.PHYSICS,
            subcategory = "Kinematics",
            mathExpression = "s = ut + ½at²",
            explanation = "Displacement under constant acceleration.",
            variables = listOf(
                FormulaVariable("s", "Displacement", "m", 0.0),
                FormulaVariable("u", "Initial Velocity", "m/s", 0.0),
                FormulaVariable("t", "Time", "s", 0.0),
                FormulaVariable("a", "Acceleration", "m/s²", 0.0)
            ),
            tags = listOf("Physics", "Kinematics"),
            solver = { values, target ->
                val u = values["u"] ?: 0.0
                val t = values["t"] ?: 0.0
                val a = values["a"] ?: 0.0
                val res = u * t + 0.5 * a * t * t
                FormulaResult(target, target, res, "", emptyList())
            }
        ),
        Formula(
            id = "ph_calculation",
            name = "pH Calculation",
            category = FormulaCategory.CHEMISTRY,
            subcategory = "Acids & Bases",
            mathExpression = "pH = -log10([H+])",
            explanation = "Calculates pH from hydrogen ion concentration.",
            variables = listOf(
                FormulaVariable("pH", "pH Level", "", 0.0),
                FormulaVariable("H", "H+ Concentration", "M", 0.0)
            ),
            tags = listOf("Chemistry", "Acid"),
            solver = { values, target ->
                val h = values["H"] ?: 0.001
                val res = if (h > 0) -log10(h) else 0.0
                FormulaResult(target, target, res, "", emptyList())
            }
        )
    )

    val mathFormulas: List<MathFormula> = listOf(
        MathFormula("quadratic_short", "Quadratic", "x = (-b ± √(b²-4ac)) / 2a", "Algebra", "Solves ax² + bx + c = 0", listOf("a", "b", "c"), { inputs, target ->
            val a = inputs["a"] ?: 1.0; val b = inputs["b"] ?: 0.0; val c = inputs["c"] ?: 0.0
            val d = b*b - 4*a*c; com.example.engine.CalculationResult((-b + sqrt(max(0.0, d))) / (2*a), "")
        })
    )

    fun solveQuadraticRoots(a: Double, b: Double, c: Double): Pair<Double, Double>? {
        val d = b * b - 4 * a * c
        if (d < 0) return null
        val r1 = (-b - sqrt(d)) / (2 * a)
        val r2 = (-b + sqrt(d)) / (2 * a)
        return min(r1, r2) to max(r1, r2)
    }

    fun matrixDeterminant2x2(a: Double, b: Double, c: Double, d: Double): Double {
        return a * d - b * c
    }

    fun binaryToDecimalAndHex(bin: String): Pair<Long, String> {
        val dec = bin.toLong(2)
        val hex = dec.toString(16).uppercase()
        return dec to hex
    }

    fun calculateSubnet(ip: String, prefix: Int): Map<String, String> {
        val parts = ip.split(".").map { it.toInt() }
        var ipLong = 0L
        for (p in parts) {
            ipLong = (ipLong shl 8) or (p.toLong() and 0xFF)
        }
        val mask = if (prefix == 0) 0L else (-1L shl (32 - prefix)) and 0xFFFFFFFFL
        val netLong = ipLong and mask
        val bcastLong = netLong or (mask.inv() and 0xFFFFFFFFL)
        val hosts = if (prefix >= 31) 0 else (1L shl (32 - prefix)) - 2

        fun longToIp(v: Long): String {
            return "${(v shr 24) and 0xFF}.${(v shr 16) and 0xFF}.${(v shr 8) and 0xFF}.${v and 0xFF}"
        }

        return mapOf(
            "Network" to longToIp(netLong),
            "Broadcast" to longToIp(bcastLong),
            "HostCount" to hosts.toString()
        )
    }
}
