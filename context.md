# Riepilogo del Contesto del Progetto

## Panoramica del Progetto
Questo è un progetto Spring Boot costruito con Gradle e scritto in Kotlin. Fornisce un'API REST per il recupero di documenti relativi a pratiche specifiche.

## Funzionalità Principali
L'applicazione espone un `DocumentController` REST con due endpoint GET principali:
- `GET /document/{idPratica}/{tipologia}`
- `GET /document/{idPratica}/{tipologia}/{protocollo}`

L'obiettivo è validare i parametri di input e, in caso di successo, restituire un file `.docx` template per il download.

## Componenti Chiave

### 1. `controller/DocumentController.kt`
- Gestisce le richieste HTTP in arrivo.
- Delega tutta la logica di business al `DocumentService`.
- Costruisce la `ResponseEntity` per servire il file al client, impostando gli header corretti per il download (`Content-Disposition: attachment`).

### 2. `service/DocumentService.kt`
- Contiene la logica di business principale.
- Esegue la validazione e la sanitizzazione degli input.
- Simula il recupero dei dati da un database (mocking).
- Carica il file template dal classpath.
- Lancia eccezioni personalizzate in caso di errori di validazione o dati non trovati.

### 3. `model/TipologiaDocumento.kt`
- Un `enum` che definisce le tipologie di documento valide: `ALLEGATO_B`, `PRE_BENESTARE`, `SINISTRI`.
- Fornisce un metodo `fromString` per convertire in modo sicuro la stringa del path in un valore dell'enum, gestendo anche i trattini.

### 4. `model/Pratica.kt`
- Una `data class` che rappresenta la struttura di una pratica di prestito.
- Simula l'oggetto dati che verrebbe restituito da un sistema esterno (es. Corda).

### 5. `exception/RestExceptionHandler.kt`
- Un gestore di eccezioni globale (`@ControllerAdvice`).
- Intercetta le eccezioni personalizzate lanciate dal `DocumentService`.
- Traduce le eccezioni in risposte di errore HTTP appropriate e standardizzate (es. 400, 404, 500) con messaggi chiari per il client.

## Logica di Business e Regole di Validazione

- **`idPratica`**:
  - Deve essere una stringa alfanumerica di 9 caratteri.
  - Viene trattato come case-insensitive e normalizzato in uppercase internamente.
  - **Mock**: Accetta solo il valore `"N06FOOGHY"`. Qualsiasi altro valore risulta in un errore `404 Not Found`.

- **`tipologia`**:
  - Deve essere una delle seguenti stringhe (case-insensitive): `"allegato-b"`, `"pre-benestare"`, `"sinistri"`.
  - Un valore non valido risulta in un errore `400 Bad Request` che elenca le opzioni valide.

- **`protocollo`** (opzionale):
  - Deve essere una stringa alfanumerica di 6 caratteri separati da un trattino (es. `"q3w-45r"`).
  - Viene trattato come case-insensitive.
  - Un formato non valido risulta in un errore `400 Bad Request`.
  - **Mock**: Accetta solo il valore `"123-asd"`. Qualsiasi altro valore valido risulta in un errore `404 Not Found`.

## Gestione del Template

- Il sistema utilizza un unico file template chiamato `template.docx`.
- Questo file deve trovarsi in `src/main/resources/templates/`.
- Il `DocumentService` verifica l'esistenza del file a ogni richiesta.
- Se il file `template.docx` non viene trovato, il sistema restituisce un errore `500 Internal Server Error` per indicare un problema di configurazione interna.
- Il contenuto del file viene copiato e restituito così com'è, senza alcuna manipolazione.