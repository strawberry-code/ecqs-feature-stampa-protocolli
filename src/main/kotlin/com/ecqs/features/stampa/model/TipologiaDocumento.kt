package com.ecqs.features.stampa.model

enum class TipologiaDocumento(val displayName: String) {
    ALLEGATO_B("Allegato B"),
    PRE_BENESTARE("Pre Benestare"),
    SINISTRI("Sinistri");

    companion object {
        fun fromString(value: String): TipologiaDocumento {
            return entries.firstOrNull { it.name.equals(value.replace("-", "_"), ignoreCase = true) }
                ?: throw IllegalArgumentException("Tipologia non valida. Valori ammessi: ${entries.joinToString { it.name.lowercase().replace("_", "-") }}")
        }
    }
}
