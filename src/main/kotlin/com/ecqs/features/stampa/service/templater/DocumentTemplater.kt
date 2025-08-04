package com.ecqs.features.stampa.service.templater

import org.springframework.core.io.ResourceLoader
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.deepoove.poi.XWPFTemplate
import com.ecqs.features.stampa.model.Pratica

/**
 * Abstract templater for DOCX documents based on poi-tl,
 * supporting simple placeholder substitutions.
 */
abstract class DocumentTemplater(private val resourceLoader: ResourceLoader) {

    companion object {
        private const val TEMPLATE_PATH = "templates/template.docx"
    }

    /**
     * Load the DOCX template from classpath.
     */
    protected fun loadTemplate(): XWPFTemplate {
        val resource = resourceLoader.getResource("classpath:" + TEMPLATE_PATH)
        if (!resource.exists()) {
            throw IllegalStateException("Template 'template.docx' non trovato o non leggibile.")
        }
        return XWPFTemplate.compile(resource.inputStream)
    }

    /**
     * Returns current date formatted as dd/MM/yyyy in Italian locale.
     */
    protected fun getCurrentFormattedDate(): String =
        SimpleDateFormat("dd/MM/yyyy", Locale.ITALIAN).format(Date())

    /**
     * Title to substitute in the document.
     */
    abstract fun getDocumentTitle(pratica: Pratica): String

    /**
     * List of template fields to render under {{CAMPI_TEMPLATE}}.
     */
    abstract fun getCampiTemplate(pratica: Pratica): List<String>

    /**
     * Applies all replacements and returns the generated document bytes.
     */
    open fun processTemplate(pratica: Pratica, protocollo: String): ByteArray {
        val template = loadTemplate()
        val data = mapOf(
            "DOCUMENT_TITLE" to getDocumentTitle(pratica),
            "DATA_EMISSIONE" to getCurrentFormattedDate(),
            "CAMPI_TEMPLATE" to getCampiTemplate(pratica).joinToString("\n"),
            "PROTOCOLLO" to protocollo
        )
        val out = ByteArrayOutputStream()
        template.render(data).writeAndClose(out)
        return out.toByteArray()
    }
}