package com.ecqs.features.stampa.controller

import com.ecqs.features.stampa.service.StampaService
import com.ecqs.features.stampa.model.TipologiaDocumento
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/stampa")
// TODO: Sviluppare la documentazione Swagger/OpenAPI per gli endpoint di stampa.
class StampaController(private val stampaService: StampaService) {

    @GetMapping("/{idPratica}/{tipologia}")
    fun getDocument(
        @PathVariable idPratica: String,
        @PathVariable tipologia: String
    ): ResponseEntity<Resource> {
        val protocollo = generateNewProtocollo() // Genera un nuovo protocollo
        val resource = stampaService.getDocument(idPratica, tipologia, protocollo)
        val filename = "${TipologiaDocumento.fromString(tipologia).displayName.replace(" ", "_")}_${idPratica}.docx"
        return buildResponse(resource, filename)
    }

    @GetMapping("/{protocollo}")
    fun getDocumentWithProtocol(
        @PathVariable protocollo: String
    ): ResponseEntity<Resource> {
        val (resource, filename) = stampaService.getDocumentWithProtocol(protocollo)
        return buildResponse(resource, filename)
    }

    private fun generateNewProtocollo(): String {
        // TODO: La generazione del protocollo deve evitare collisioni con i protocolli esistenti
        // nel DB di AUX (nuovo schema Protocolli).
        val randomString = UUID.randomUUID().toString().replace("-", "").substring(0, 6).uppercase()
        val part1 = randomString.substring(0, 3)
        val part2 = randomString.substring(3, 6)
        return "$part1-$part2"
    }

    private fun buildResponse(resource: Resource, filename: String): ResponseEntity<Resource> {
        val headers = HttpHeaders().apply {
            add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"$filename\"")
        }
        return ResponseEntity.ok()
            .headers(headers)
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(resource)
    }
}
