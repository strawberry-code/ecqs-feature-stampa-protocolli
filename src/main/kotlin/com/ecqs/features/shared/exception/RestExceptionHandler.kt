package com.ecqs.features.shared.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

class InvalidProtocoloFormatException(message: String) : RuntimeException(message)
class PraticaNotFoundException(message: String) : RuntimeException(message)
class ProtocoloNotFoundException(message: String) : RuntimeException(message)
class TemplateNotFoundException(message: String) : RuntimeException(message)

@ControllerAdvice
class RestExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException, request: WebRequest): ResponseEntity<Any> {
        val body = mapOf("error" to ex.message)
        return ResponseEntity(body, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(InvalidProtocoloFormatException::class)
    fun handleInvalidProtocoloFormatException(ex: InvalidProtocoloFormatException, request: WebRequest): ResponseEntity<Any> {
        val body = mapOf("error" to ex.message)
        return ResponseEntity(body, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(PraticaNotFoundException::class)
    fun handlePraticaNotFoundException(ex: PraticaNotFoundException, request: WebRequest): ResponseEntity<Any> {
        val body = mapOf("error" to ex.message)
        return ResponseEntity(body, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(ProtocoloNotFoundException::class)
    fun handleProtocoloNotFoundException(ex: ProtocoloNotFoundException, request: WebRequest): ResponseEntity<Any> {
        val body = mapOf("error" to ex.message)
        return ResponseEntity(body, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(TemplateNotFoundException::class)
    fun handleTemplateNotFoundException(ex: TemplateNotFoundException, request: WebRequest): ResponseEntity<Any> {
        val body = mapOf("error" to ex.message)
        return ResponseEntity(body, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}