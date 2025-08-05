package com.ecqs.features.stampa.controller

import com.ecqs.features.stampa.model.TipologiaDocumento
import com.ecqs.features.stampa.pdf.PdfService
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.Base64

@RestController
@RequestMapping("/stampa")
class StampaController(private val pdfService: PdfService) {

    @GetMapping("/{idPratica}/{tipologia}", produces = [MediaType.APPLICATION_PDF_VALUE])
    fun getDocument(
        @PathVariable idPratica: String,
        @PathVariable tipologia: String
    ): ResponseEntity<ByteArray> {
        validateIdPratica(idPratica)
        val tipologiaDocumento = TipologiaDocumento.fromString(tipologia)

        val context = mapOf("idPratica" to idPratica, "tipologia" to tipologia)
        val pdfBytes = pdfService.generaPdf(tipologiaDocumento.templateName, context)
        val filename = "${tipologiaDocumento.displayName.replace(" ", "_")}_${idPratica}.pdf"
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

        // Mocked base64 PDF
        val base64Pdf = "JVBERi0xLjQKJdPr6eEKMSAwIG9iago8PC9UeXBlIC9QYWdlCi9QYXJlbnQgMiAwIFIKL1Jlc291cmNlcyA8PC9Gb250IDw8L0YxIDQgMCBSID4+ID4+Ci9Db250ZW50cyAzIDAgUgo+PgplbmRvYmoKMiAwIG9iago8PC9UeXBlIC9QYWdlcwovS2lkcyBbMSAwIFIgXQovQ291bnQgMQo+PgplbmRvYmoKMyAwIG9iago8PC9MZW5ndGggNTg+PgpzdHJlYW0KQkQKRS9UeHQgL0YxIDEyIFRmCjAgMCAwIHJnCjAgMCAwIFJHCjAgMCAwIHNjbgpCVCg0MCA3NTUpIFRqCkc5IFR3CltIZWxsbywgV29ybGRdIFRKIgpFVAplbmRzdHJlYW0KZW5kb2JqCjQgMCBvYmoKPDwvVHlwZSAvRm9udAovU3VidHlwZSAvVHlwZTEKL0Jhc2VGb250IC9IZWx2ZXRpY2E+PgplbmRvYmoKeHJlZgowIDUKMDAwMDAwMDAwMCA2NTUzNSBmIAowMDAwMDAwMDE1IDAwMDAwIG4gCjAwMDAwMDAxMTIgMDAwMDAgbiAKMDAwMDAwMDE2MyAwMDAwMCBuIAowMDAwMDAwMzA0IDAwMDAwIG4gCnRyYWlsZXIKPDwvU2l6ZSA1Ci9Sb290IDIgMCBSID4+CnN0YXJ0eHJlZgozNzIKJSVFT0YK"
        val pdfBytes = Base64.getDecoder().decode(base64Pdf)

        val headers = HttpHeaders().apply {
            add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"documento.pdf\"")
        }
        return ResponseEntity.ok()
            .headers(headers)
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdfBytes)
    }

    private fun validateIdPratica(idPratica: String) {
        require(idPratica.matches(Regex("^[a-zA-Z0-9]{9}$"))) {
            "L'idPratica deve essere un valore alfanumerico di 9 caratteri."
        }
    }

    private fun validateProtocollo(protocollo: String) {
        require(protocollo.matches(Regex("^[a-zA-Z0-9]{3}-[a-zA-Z0-9]{3}$"))) {
            "Il protocollo deve essere un valore alfanumerico di 6 caratteri separati da trattino (es. q3w-45r)."
        }
    }
}