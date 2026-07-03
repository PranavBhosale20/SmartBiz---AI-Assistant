# 🩺 SmartBiz — AI-Assisted Clinic Backend

A Spring Boot + PostgreSQL backend that automates day-to-day clinic operations: appointment scheduling, doctor-wise slot availability, prescriptions with atomic inventory deduction, and rule-based billing. Built from the ground up with a layered architecture, DTOs at every boundary, and real business logic tested against real data — not a CRUD tutorial clone.

## 💡 Why this project

Clinics still run a lot of this on paper or fragmented spreadsheets: booking slots, tracking who's seen a doctor before, deducting stock when medicine is prescribed, calculating what to bill. SmartBiz models that workflow as a proper backend system, with the eventual goal of letting staff interact with it through natural language instead of multiple admin screens.

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language / Framework | Java 17, Spring Boot 4.0.7 |
| Database | PostgreSQL 18 |
| Persistence | Spring Data JPA / Hibernate |
| Build | Maven |
| API Testing | Postman |
| Version Control | Git / GitHub |

## 🏗️ Architecture

Standard layered design, with a strict rule: **controllers never return entities.**

```
Controller → Service → Repository → PostgreSQL
                ↑
         DTOs + Mappers
   (entities never cross the API boundary)
```

This was a deliberate fix carried over from an earlier version of the project, where returning entities directly caused lazy-loaded fields to serialize as `null`. Every response now goes through a dedicated DTO, mapped explicitly from the entity.

## 📦 Core Modules

**👤 Users & Staff** — Patient and staff records, with staff accounts (username/password/role) scaffolded for upcoming authentication.

**🩺 Doctors & Appointments** — Doctors define their own OPD hours and slot duration. Appointments link to a real `Doctor` entity (not a free-text name), and booking is validated against several real-world rules: no past-dated bookings, a 2-day advance booking window, and no double-booking for either the patient or the doctor. A dedicated endpoint computes live slot availability for a given doctor and date by generating all possible slots and filtering out the ones already booked.

**💊 Prescriptions & Inventory** — Prescriptions are linked to an appointment (and reach the relevant doctor/patient through it, rather than duplicating those references). Creating a prescription is a single atomic operation: it checks stock is sufficient, saves the prescription, deducts the stock, and automatically generates a corresponding billing line item — all in one transaction, so a failure partway through can't leave inventory or billing in an inconsistent state.

**🧾 Billing** — Bills are generated per appointment and combine a visit fee with the cost of any prescribed medicine. Visit pricing follows a real business rule: a patient's first-ever appointment (regardless of type) is billed a flat fee, while subsequent visits are billed according to that visit type's own rate. Non-prescribed extras (e.g. sanitizer) can be added to a bill manually, alongside the line items generated automatically from prescriptions.

## 🔍 Engineering Notes

- **DTOs and mappers throughout** — no entity is ever serialized directly in an API response.
- **Constructor injection** used consistently over field injection.
- **`@Transactional` where it matters** — prescription creation is the clearest example: stock check, stock deduction, and bill-item creation either all succeed or all roll back together.
- **Schema evolution handled deliberately** — adding required relationships to tables with existing data surfaced real Postgres NOT NULL migration conflicts; resolved by rebuilding the schema in dev, with the lesson that production systems would use Flyway/Liquibase instead of relying on Hibernate's auto-DDL.
- **Known limitation:** Bill responses currently serialize the entity directly rather than through a dedicated DTO — flagged for cleanup, not yet a blocker since nested fields serialize correctly.

## ✅ Current Status

All core domain logic — users, appointments, doctors, slot scheduling, prescriptions, inventory deduction, and billing — is built and verified end-to-end with real test data via Postman. A temporary, fully open `SecurityConfig` (permit-all, CSRF disabled) is in place purely to unblock API testing and is explicitly scaffolding, not a security implementation.

## 🗺️ Roadmap

- [ ] **Centralized exception handling** — custom `BusinessException` / `ResourceNotFoundException` types with a `@RestControllerAdvice` handler, replacing raw stack-trace responses with clean structured JSON errors.
- [ ] **Real authentication & authorization** — role-based login (`STAFF` / `PATIENT`) replacing the temporary open security config.
- [ ] **Rule-based NLP engine** — keyword/regex intent detection for natural language commands (e.g. "book a slot for tomorrow 5 PM").
- [ ] **AI fallback via Google Gemini** — handles inputs the rule-based engine can't confidently parse.
- [ ] **Unified `/chat` endpoint** — rule-based NLP first, Gemini fallback only when no clear intent is detected.
- [ ] **Frontend** — HTML/CSS/JS first; a React rewrite is planned as a separate later milestone.
- [ ] **Test coverage** — automated tests across the service layer.

## 🚀 Getting Started

```bash
git clone https://github.com/PranavBhosale20/SmartBiz---AI-Assistant.git
cd SmartBiz---AI-Assistant
```

1. Create a PostgreSQL database named `smartbiz_db`.
2. Update `application.properties` with your local PostgreSQL credentials.
3. Run the application — it starts on `localhost:8081` by default.
4. Import the Postman collection (if included in the repo) to explore the API.

---

*This project is under active development. Earlier phases focused on getting the domain model and business logic right with real verification at each step; current work is moving into security, NLP, and AI integration.*
