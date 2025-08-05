package com.ecqs.features.stampa.controller

import com.ecqs.features.stampa.model.TipologiaDocumento
import com.ecqs.features.stampa.service.StampaService
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/stampa")
class StampaController(private val stampaService: StampaService) {

    @GetMapping("/{idPratica}/{tipologia}", produces = [MediaType.APPLICATION_PDF_VALUE])
    fun getDocument(
        @PathVariable idPratica: String,
        @PathVariable tipologia: String
    ): ResponseEntity<ByteArray> {
        val sanitizedIdPratica = idPratica.uppercase()
        validateIdPratica(sanitizedIdPratica)
        val tipologiaDocumento = TipologiaDocumento.fromString(tipologia)

        val pdfBytes = stampaService.generateAndSavePdf(sanitizedIdPratica, tipologiaDocumento)
        val filename = "${tipologiaDocumento.displayName.replace(" ", "_")}_${sanitizedIdPratica}.pdf"
        val headers = HttpHeaders().apply {
            add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"$filename\"")
        }
        return ResponseEntity.ok()
            .headers(headers)
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdfBytes)
    }

    @GetMapping("/{protocollo}", produces = [MediaType.APPLICATION_PDF_VALUE])
    fun getDocumentWithProtocol(
        @PathVariable protocollo: String
    ): ResponseEntity<ByteArray> {
        validateProtocollo(protocollo)
        val pdfBytes = stampaService.getPdfByProtocollo(protocollo)

        val headers = HttpHeaders().apply {
            add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"documento.pdf\"")
        }
        return ResponseEntity.ok()
            .headers(headers)
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdfBytes)
    }

    private fun validateIdPratica(idPratica: String) {
        require(idPratica.matches(Regex("^[a-zA-Z0-9]{9}"))) {
            "L'idPratica deve essere un valore alfanumerico di 9 caratteri."
        }
    }

    private fun validateProtocollo(protocollo: String) {
        require(protocollo.matches(Regex("^[a-zA-Z0-9]{3}-[a-zA-Z0-9]{3}"))) {
            "Il protocollo deve essere un valore alfanumerico di 6 caratteri separati da trattino (es. q3w-45r)."
        }
    }
}