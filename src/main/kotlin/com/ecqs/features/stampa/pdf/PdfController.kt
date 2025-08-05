package com.ecqs.features.stampa.pdf

import com.ecqs.features.stampa.model.TipologiaDocumento
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/pdf")
class PdfController(private val pdfService: PdfService) {

    @GetMapping("/{idPratica}/{tipologia}", produces = [MediaType.APPLICATION_PDF_VALUE])
    fun getDocument(
        @PathVariable idPratica: String,
        @PathVariable tipologia: String
    ): ResponseEntity<ByteArray> {
        val context = mapOf("idPratica" to idPratica, "tipologia" to tipologia)
        val pdfBytes = pdfService.generaPdf("allegato-b", context)
        val filename = "${TipologiaDocumento.fromString(tipologia).displayName.replace(" ", "_")}_${idPratica}.pdf"
        val headers = HttpHeaders().apply {
            add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"$filename\"")
        }
        return ResponseEntity.ok()
            .headers(headers)
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdfBytes)
    }
}
