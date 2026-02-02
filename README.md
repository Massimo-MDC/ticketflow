# TicketFlow - Sistema di Ticketing per Eventi Live

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-green.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

TicketFlow è una piattaforma RESTful per la vendita di biglietti per eventi live (concerti, eventi sportivi, spettacoli teatrali). Il sistema supporta tre tipologie di utenti con permessi differenziati e include un'API GraphQL completa.

## Indice

- [Caratteristiche](#caratteristiche)
- [Tecnologie Utilizzate](#tecnologie-utilizzate)
- [Requisiti di Sistema](#requisiti-di-sistema)
- [Installazione e Configurazione](#installazione-e-configurazione)
- [Variabili d'Ambiente](#variabili-dambiente)
- [Avvio dell'Applicazione](#avvio-dellapplicazione)
- [Struttura del Progetto](#struttura-del-progetto)
- [Modello di Dominio](#modello-di-dominio)
- [API REST](#api-rest)
- [API GraphQL](#api-graphql)
- [Autenticazione e Autorizzazione](#autenticazione-e-autorizzazione)
- [Integrazioni Esterne](#integrazioni-esterne)
- [Testing con Postman](#testing-con-postman)

## Caratteristiche

### Funzionalità Principali

- **Gestione Eventi**: Creazione e gestione di eventi con ereditarietà (Concerti, Sport, Teatro)
- **Sistema di Ticketing**: Acquisto biglietti con gestione settori e disponibilità
- **Autenticazione JWT**: Sistema sicuro con token Bearer
- **Tre Ruoli Utente**: USER, ORGANIZER, ADMIN con permessi differenziati
- **Paginazione**: Tutte le liste supportano paginazione e ordinamento
- **GraphQL**: API GraphQL completa con query e mutation
- **Upload Immagini**: Integrazione Cloudinary per gestione media
- **Notifiche Email**: Integrazione Mailgun per conferme ordini

### Requisiti Esame Soddisfatti

- ✅ 10+ tabelle con relazioni significative
- ✅ Ereditarietà JPA (JOINED strategy)
- ✅ 3 ruoli con @PreAuthorize
- ✅ Autenticazione JWT completa
- ✅ Password hashate con BCrypt
- ✅ Query avanzate (Derived, JPQL, Native, FETCH JOIN)
- ✅ Paginazione su tutti gli endpoint di lista
- ✅ Validazione con Bean Validation
- ✅ GlobalExceptionHandler centralizzato
- ✅ 2+ API esterne (Cloudinary + Mailgun)
- ✅ GraphQL (bonus)
- ✅ DTO per tutte le risposte

## Tecnologie Utilizzate

| Tecnologia | Versione | Descrizione |
|------------|----------|-------------|
| Java | 21 | Linguaggio di programmazione |
| Spring Boot | 3.5.x | Framework applicativo |
| Spring Security | 6.x | Sicurezza e autenticazione |
| Spring Data JPA | 3.x | Persistenza dati |
| Spring GraphQL | 1.3.x | API GraphQL |
| PostgreSQL | 15+ | Database relazionale |
| JWT (JJWT) | 0.12.6 | Token authentication |
| Cloudinary | 1.39.0 | Gestione immagini cloud |
| Unirest | 4.4.4 | Client HTTP per Mailgun |
| Lombok | - | Riduzione boilerplate |

## Requisiti di Sistema

- **Java**: JDK 21 o superiore
- **Maven**: 3.9.x o superiore
- **PostgreSQL**: 15 o superiore
- **Docker** (opzionale): Per avviare PostgreSQL via container

## Installazione e Configurazione

### 1. Clona il Repository

```bash
git clone https://github.com/tuousername/ticketflow.git
cd ticketflow
```

### 2. Configura il Database

**Opzione A - Docker Compose (consigliato):**

```bash
docker-compose up -d
```

**Opzione B - PostgreSQL locale:**

```sql
CREATE DATABASE ticketflow;
CREATE USER ticketflow_user WITH PASSWORD 'ticketflow_secret';
GRANT ALL PRIVILEGES ON DATABASE ticketflow TO ticketflow_user;
```

### 3. Configura le Variabili d'Ambiente

Copia il file di esempio e configura le variabili:

```bash
cp env.properties.example env.properties
```

## Variabili d'Ambiente

Modifica il file `env.properties` con i tuoi valori:

```properties
# Database
DB_USERNAME=ticketflow_user
DB_PASSWORD=ticketflow_secret

# JWT (minimo 256 bit per HS256)
JWT_SECRET=your_secret_key_at_least_256_bits_long_for_security

# Cloudinary (https://cloudinary.com)
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret

# Mailgun (https://mailgun.com)
MAILGUN_API_KEY=your_api_key
MAILGUN_DOMAIN=your_domain.mailgun.org
MAILGUN_FROM=noreply@yourdomain.com
```

## Avvio dell'Applicazione

```bash
# Compila il progetto
./mvnw clean install

# Avvia l'applicazione
./mvnw spring-boot:run
```

L'applicazione sarà disponibile su:
- **REST API**: http://localhost:8080/api
- **GraphQL Playground**: http://localhost:8080/graphiql

## Struttura del Progetto

```
src/main/java/it/ticketflow/ticketflow/
├── config/                 # Configurazioni Spring
│   ├── SecurityConfig.java
│   ├── CorsConfig.java
│   ├── CloudinaryConfig.java
│   └── JpaAuditingConfig.java
├── controller/             # REST Controllers
│   ├── AuthController.java
│   ├── UserController.java
│   ├── EventController.java
│   ├── OrderController.java
│   └── ...
├── dto/                    # Data Transfer Objects
│   ├── auth/
│   ├── event/
│   ├── order/
│   └── graphql/
├── entity/                 # Entità JPA
│   ├── User.java
│   ├── Event.java (abstract)
│   ├── ConcertEvent.java
│   ├── SportEvent.java
│   ├── TheaterEvent.java
│   └── ...
├── exception/              # Gestione errori
│   ├── GlobalExceptionHandler.java
│   ├── NotFoundException.java
│   └── BadRequestException.java
├── graphql/                # Resolver GraphQL
│   ├── QueryController.java
│   ├── MutationController.java
│   └── EventResolver.java
├── repository/             # Repository JPA
├── security/               # JWT e filtri
│   ├── JwtService.java
│   └── JwtAuthenticationFilter.java
└── service/                # Business logic
```

## Modello di Dominio

### Diagramma ER Semplificato

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│    User     │     │   Category  │     │    Venue    │
│  (UUID id)  │     │  (Long id)  │     │  (Long id)  │
└──────┬──────┘     └──────┬──────┘     └──────┬──────┘
       │                   │                   │
       │ organizer         │ category          │ venue
       ▼                   ▼                   ▼
┌──────────────────────────────────────────────────────┐
│                      Event (abstract)                 │
│  ConcertEvent | SportEvent | TheaterEvent            │
│  (JOINED Inheritance Strategy)                        │
└──────────────────────┬───────────────────────────────┘
                       │
       ┌───────────────┼───────────────┐
       ▼               ▼               ▼
┌─────────────┐ ┌─────────────┐ ┌─────────────┐
│ EventSector │ │ EventArtist │ │    Order    │
└──────┬──────┘ └──────┬──────┘ └──────┬──────┘
       │               │               │
       │               ▼               ▼
       │        ┌─────────────┐ ┌─────────────┐
       │        │   Artist    │ │   Ticket    │
       │        └─────────────┘ └─────────────┘
       │                               │
       └───────────────────────────────┘
```

### Entità Principali

| Entità | Descrizione | Relazioni Chiave |
|--------|-------------|------------------|
| **User** | Utenti della piattaforma | Orders (1:N), Events organizzati (1:N) |
| **Event** | Classe base astratta eventi | Venue (N:1), Category (N:1), Organizer (N:1) |
| **ConcertEvent** | Concerti (estende Event) | genre, tourName |
| **SportEvent** | Eventi sportivi | homeTeam, awayTeam, league |
| **TheaterEvent** | Spettacoli teatrali | director, durationMinutes |
| **Venue** | Location degli eventi | Sectors (1:N), Events (1:N) |
| **Sector** | Settori di una venue | Venue (N:1), basePrice |
| **EventSector** | Settore per evento specifico | availableTickets, currentPrice |
| **Order** | Ordini utenti | User (N:1), Tickets (1:N) |
| **Ticket** | Biglietti acquistati | Order (N:1), EventSector (N:1) |

## API REST

### Autenticazione

| Metodo | Endpoint | Descrizione | Accesso |
|--------|----------|-------------|---------|
| POST | `/api/auth/register` | Registrazione nuovo utente | Pubblico |
| POST | `/api/auth/login` | Login e generazione token | Pubblico |

### Users

| Metodo | Endpoint | Descrizione | Accesso |
|--------|----------|-------------|---------|
| GET | `/api/users/me` | Profilo utente corrente | Autenticato |
| PATCH | `/api/users/me` | Aggiorna profilo | Autenticato |
| PATCH | `/api/users/me/avatar` | Upload immagine profilo | Autenticato |
| GET | `/api/users` | Lista utenti (paginata) | ADMIN |
| PATCH | `/api/users/{id}/role` | Modifica ruolo utente | ADMIN |

### Events

| Metodo | Endpoint | Descrizione | Accesso |
|--------|----------|-------------|---------|
| GET | `/api/events/public` | Eventi pubblicati | Pubblico |
| GET | `/api/events/public/{id}` | Dettaglio evento | Pubblico |
| GET | `/api/events/public/search` | Ricerca eventi | Pubblico |
| POST | `/api/events/concerts` | Crea concerto | ORGANIZER |
| POST | `/api/events/sports` | Crea evento sportivo | ORGANIZER |
| POST | `/api/events/theaters` | Crea spettacolo | ORGANIZER |
| PATCH | `/api/events/{id}/status` | Modifica stato | ORGANIZER (owner) |
| POST | `/api/events/{id}/sectors` | Aggiungi settore | ORGANIZER (owner) |
| POST | `/api/events/{id}/artists` | Aggiungi artista | ORGANIZER (owner) |
| DELETE | `/api/events/{id}` | Elimina evento | ORGANIZER (owner) / ADMIN |

### Orders

| Metodo | Endpoint | Descrizione | Accesso |
|--------|----------|-------------|---------|
| POST | `/api/orders` | Crea ordine | Autenticato |
| GET | `/api/orders` | I miei ordini | Autenticato |
| GET | `/api/orders/{id}` | Dettaglio ordine | Autenticato (owner) |
| PATCH | `/api/orders/{id}/confirm` | Conferma ordine | Autenticato (owner) |
| PATCH | `/api/orders/{id}/cancel` | Annulla ordine | Autenticato (owner) |

### Tickets

| Metodo | Endpoint | Descrizione | Accesso |
|--------|----------|-------------|---------|
| GET | `/api/tickets` | I miei biglietti | Autenticato |
| GET | `/api/tickets/{id}` | Dettaglio biglietto | Autenticato (owner) |
| PATCH | `/api/tickets/{code}/validate` | Valida biglietto | ORGANIZER / ADMIN |

## API GraphQL

L'applicazione espone un'API GraphQL completa accessibile su `/graphql`.

### Playground GraphiQL

Accedi a http://localhost:8080/graphiql per esplorare lo schema interattivamente.

### Query Esempio

```graphql
# Eventi pubblicati con filtri
query {
  events(filter: { city: "Roma" }, page: 0, size: 10) {
    content {
      id
      title
      eventDate
      venue {
        name
        city
      }
      eventSectors {
        availableTickets
        currentPrice
      }
    }
    pageInfo {
      totalElements
      totalPages
    }
  }
}

# I miei ordini
query {
  myOrders(page: 0, size: 10) {
    content {
      orderNumber
      totalAmount
      status
      tickets {
        ticketCode
        purchasePrice
      }
    }
  }
}
```

### Mutation Esempio

```graphql
# Crea ordine
mutation {
  createOrder(input: {
    items: [
      { eventSectorId: 1, quantity: 2 }
    ]
  }) {
    id
    orderNumber
    totalAmount
    status
  }
}
```

## Autenticazione e Autorizzazione

### Flusso JWT

1. **Registrazione**: `POST /api/auth/register`
2. **Login**: `POST /api/auth/login` → Ricevi token JWT
3. **Richieste autenticate**: Header `Authorization: Bearer <token>`

### Ruoli e Permessi

| Ruolo | Permessi |
|-------|----------|
| **USER** | Visualizza eventi, acquista biglietti, gestisce propri ordini |
| **ORGANIZER** | Tutti i permessi USER + crea/gestisce propri eventi |
| **ADMIN** | Accesso completo a tutte le risorse |

### Esempio Header Autorizzazione

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

## Integrazioni Esterne

### Cloudinary (Gestione Immagini)

- Upload immagini per profili utente, eventi, venue, artisti
- Trasformazioni automatiche
- CDN globale

### Mailgun (Email Transazionali)

- Email di benvenuto alla registrazione
- Conferma ordini con dettagli biglietti
- Template personalizzabili

## Testing con Postman

### Import Collection

1. Apri Postman
2. File → Import
3. Seleziona il file `TicketFlow.postman_collection.json`

### Variabili di Ambiente

Configura le seguenti variabili in Postman:

| Variabile | Valore |
|-----------|--------|
| `baseUrl` | `http://localhost:8080` |
| `token` | (viene impostato automaticamente dopo login) |

### Ordine di Test Consigliato

1. **Auth**: Registra utente → Login
2. **Categories**: Lista categorie
3. **Venues**: Lista/crea venue
4. **Events**: Crea evento → Pubblica → Aggiungi settori
5. **Orders**: Crea ordine → Conferma
6. **Tickets**: Visualizza biglietti

## Contribuire

1. Fork del repository
2. Crea branch feature (`git checkout -b feature/AmazingFeature`)
3. Commit modifiche (`git commit -m 'Add AmazingFeature'`)
4. Push al branch (`git push origin feature/AmazingFeature`)
5. Apri Pull Request

## Licenza

Distribuito sotto licenza MIT. Vedi `LICENSE` per maggiori informazioni.

## Contatti

Progetto Esame EPICODE - Backend Programming

---

*Sviluppato con ❤️ per il corso Backend Programming di EPICODE*
