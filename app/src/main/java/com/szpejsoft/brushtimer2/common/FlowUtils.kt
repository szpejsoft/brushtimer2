package com.szpejsoft.brushtimer2.common

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

fun <T1, T2, T3> combine(
    f1: Flow<T1>,
    f2: Flow<T2>,
    f3: Flow<T3>
): Flow<Triple<T1, T2, T3>> =
    f1.combine(f2) { t1, t2 -> t1 to t2 }
        .combine(f3) { (t1, t2), t3 -> t1 to t2 tre t3 }

infix fun <T1, T2, T3> Pair<T1, T2>.tre(t3: T3): Triple<T1, T2, T3> = Triple(this.first, this.second, t3)