package cl.salinas.ventasliveapp

import java.util.Calendar

object DateUtils {

    // 🔥 DÍA COMERCIAL: 04:00 → 03:59 (día siguiente)
    fun obtenerRangoTurno(year: Int, month: Int, day: Int): Pair<Long, Long> {

        val start = Calendar.getInstance().apply {
            set(year, month, day, 4, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val end = Calendar.getInstance().apply {
            set(year, month, day, 4, 0, 0)
            add(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 3)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis

        return start to end
    }

    // 🔥 TURNO ACTUAL (HOY)
    fun obtenerRangoHoyTurno(): Pair<Long, Long> {

        val now = Calendar.getInstance()
        val hour = now.get(Calendar.HOUR_OF_DAY)

        val baseDay = Calendar.getInstance()

        // 🌙 Si son antes de las 04:00 → pertenece al día anterior
        if (hour < 4) {
            baseDay.add(Calendar.DAY_OF_MONTH, -1)
        }

        return obtenerRangoTurno(
            baseDay.get(Calendar.YEAR),
            baseDay.get(Calendar.MONTH),
            baseDay.get(Calendar.DAY_OF_MONTH)
        )
    }
}