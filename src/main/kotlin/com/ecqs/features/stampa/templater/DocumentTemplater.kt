package com.ecqs.features.stampa.templater

import com.ecqs.features.stampa.model.Pratica
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

abstract class DocumentTemplater {

    fun getCurrentFormattedDate(): String {
        val sdf = SimpleDateFormat("HH:mm, dd MMMM yyyy", Locale.ITALIAN)
        return sdf.format(Date())
    }

    abstract fun getDocumentTitle(pratica: Pratica): String
    abstract fun getTemplateData(pratica: Pratica, protocollo: String): Map<String, Any>
}
