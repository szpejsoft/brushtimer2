package com.szpejsoft.brushtimer2.common

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine


data class Quadruple<T1, T2, T3, T4>(
    val first: T1,
    val second: T2,
    val third: T3,
    val fourth: T4
)

fun <T1, T2, T3, T4> combine(
    f1: Flow<T1>,
    f2: Flow<T2>,
    f3: Flow<T3>,
    f4: Flow<T4>
): Flow<Quadruple<T1, T2, T3, T4>> =
    f1.combine(f2) { t1, t2 -> t1 to t2 }
        .combine(f3) { (t1, t2), t3 -> t1 to t2 tre t3 }
        .combine(f4) { (t1, t2, t3), t4 -> t1 to t2 tre t3 qua t4 }

infix fun <T1, T2, T3> Pair<T1, T2>.tre(t3: T3): Triple<T1, T2, T3> = Triple(first, second, t3)
infix fun <T1, T2, T3, T4> Triple<T1, T2, T3>.qua(t4: T4): Quadruple<T1, T2, T3, T4> =
    Quadruple(first, second, third, t4)