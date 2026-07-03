package com.smartbiz.service;

import com.smartbiz.controller.ChatResponse;
import com.smartbiz.dto.AppointmentRequestDTO;
import com.smartbiz.exception.ResourceNotFoundException;
import com.smartbiz.model.Doctor;
import com.smartbiz.nlp.IntentResult;
import com.smartbiz.nlp.IntentType;
import com.smartbiz.nlp.NlpService;
import com.smartbiz.repository.DoctorRepository;
import com.smartbiz.repository.ProductRepository;
import com.smartbiz.security.AuthHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;

/**
 * Phase 9: orchestrates the full chat flow.
 *
 * 1. Pass message to NlpService (rule-based, falls back to Gemini)
 * 2. Check role-based intent permissions
 * 3. Execute the matched intent using extracted entities
 * 4. Return a ChatResponse with intent + message + data
 */
@Service
public class ChatService {

    private final NlpService nlpService;
    private final AppointmentService appointmentService;
    private final ProductService productService;
    private final BillService billService;
    private final DoctorRepository doctorRepository;
    private final ProductRepository productRepository;
    private final AuthHelper authHelper;

    public ChatService(NlpService nlpService,
                       AppointmentService appointmentService,
                       ProductService productService,
                       BillService billService,
                       DoctorRepository doctorRepository,
                       ProductRepository productRepository,
                       AuthHelper authHelper) {
        this.nlpService = nlpService;
        this.appointmentService = appointmentService;
        this.productService = productService;
        this.billService = billService;
        this.doctorRepository = doctorRepository;
        this.productRepository = productRepository;
        this.authHelper = authHelper;
    }

    public ChatResponse process(String message) {
        // Step 1: detect intent
        IntentResult result = nlpService.process(message);
        String intentName = result.getIntent().name();
        Map<String, String> entities = result.getEntities();

        // Step 2: UNKNOWN - couldn't determine intent
        if (result.getIntent() == IntentType.UNKNOWN) {
            return ChatResponse.error("UNKNOWN",
                    "Sorry, I didn't understand that. Try something like: " +
                    "'book appointment with Dr. Mehta tomorrow 10am' or " +
                    "'check stock of paracetamol'");
        }

        // Step 3: role-based intent restriction
        // PATIENT intents: BOOK_APPOINTMENT, CANCEL_APPOINTMENT, VIEW_SLOTS
        // STAFF intents: CHECK_STOCK, GENERATE_BILL, VIEW_BILL
        boolean isPatient = authHelper.isPatient();
        boolean isStaffIntent = result.getIntent() == IntentType.CHECK_STOCK
                || result.getIntent() == IntentType.GENERATE_BILL
                || result.getIntent() == IntentType.VIEW_BILL;

        if (isPatient && isStaffIntent) {
            return ChatResponse.error(intentName,
                    "You don't have permission to perform this action.");
        }

        // Step 4: execute the intent
        try {
            return switch (result.getIntent()) {
                case BOOK_APPOINTMENT -> handleBookAppointment(entities);
                case CANCEL_APPOINTMENT -> handleCancelAppointment(entities);
                case VIEW_SLOTS -> handleViewSlots(entities);
                case CHECK_STOCK -> handleCheckStock(entities);
                case GENERATE_BILL -> handleGenerateBill(entities);
                case VIEW_BILL -> handleViewBill(entities);
                default -> ChatResponse.error(intentName, "Intent not yet supported.");
            };
        } catch (Exception e) {
            // Catch missing entities or service-layer errors gracefully
            return ChatResponse.error(intentName, e.getMessage());
        }
    }

    // -------------------------------------------------------
    // PATIENT intent handlers
    // -------------------------------------------------------

    private ChatResponse handleBookAppointment(Map<String, String> entities) {
        // userId comes from JWT, not from the message
        Long userId = authHelper.getAuthenticatedUserId();

        String doctorName = entities.get("doctorName");
        String date = entities.get("date");
        String time = entities.get("time");

        if (doctorName == null) {
            return ChatResponse.error("BOOK_APPOINTMENT",
                    "Please specify a doctor name. e.g. 'book appointment with Dr. Mehta'");
        }
        if (date == null) {
            return ChatResponse.error("BOOK_APPOINTMENT",
                    "Please specify a date. e.g. 'tomorrow' or '2026-07-04'");
        }
        if (time == null) {
            return ChatResponse.error("BOOK_APPOINTMENT",
                    "Please specify a time. e.g. '10am' or '14:00'");
        }

        // Resolve doctor name -> doctorId
        Doctor doctor = doctorRepository.findByName("Dr. " + doctorName)
                .orElseGet(() -> doctorRepository.findByName(doctorName)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Doctor not found with name: " + doctorName)));

        // Build the appointmentDate string (yyyy-MM-ddTHH:mm:00)
        String appointmentDate = date + "T" + time + ":00";

        // We need a visitTypeId - default to 1 if not extracted
        // (NLP doesn't currently extract visit type from natural language)
        Long visitTypeId = entities.containsKey("visitTypeId")
                ? Long.parseLong(entities.get("visitTypeId")) : 1L;

        AppointmentRequestDTO dto = new AppointmentRequestDTO();
        dto.setUserId(userId);
        dto.setDoctorId(doctor.getId());
        dto.setVisitTypeId(visitTypeId);
        dto.setAppointmentDate(appointmentDate);

        Object booked = appointmentService.bookAppointment(dto);
        return new ChatResponse("BOOK_APPOINTMENT",
                "Appointment booked successfully!", booked);
    }

    private ChatResponse handleCancelAppointment(Map<String, String> entities) {
        String idStr = entities.get("appointmentId");
        if (idStr == null) {
            return ChatResponse.error("CANCEL_APPOINTMENT",
                    "Please specify an appointment id. e.g. 'cancel appointment 3'");
        }

        Long appointmentId = Long.parseLong(idStr);
        Object cancelled = appointmentService.cancelAppointment(appointmentId);
        return new ChatResponse("CANCEL_APPOINTMENT",
                "Appointment cancelled successfully.", cancelled);
    }

    private ChatResponse handleViewSlots(Map<String, String> entities) {
        String doctorName = entities.get("doctorName");
        String date = entities.get("date");

        if (doctorName == null) {
            return ChatResponse.error("VIEW_SLOTS",
                    "Please specify a doctor. e.g. 'slots for Dr. Mehta today'");
        }
        if (date == null) {
            return ChatResponse.error("VIEW_SLOTS",
                    "Please specify a date. e.g. 'today' or 'tomorrow'");
        }

        Doctor doctor = doctorRepository.findByName("Dr. " + doctorName)
                .orElseGet(() -> doctorRepository.findByName(doctorName)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Doctor not found with name: " + doctorName)));

        Object slots = appointmentService.getAvailableSlots(
                doctor.getId(), LocalDate.parse(date));
        return new ChatResponse("VIEW_SLOTS",
                "Available slots for Dr. " + doctorName + " on " + date, slots);
    }

    // -------------------------------------------------------
    // STAFF intent handlers
    // -------------------------------------------------------

    private ChatResponse handleCheckStock(Map<String, String> entities) {
        String productName = entities.get("productName");
        if (productName == null) {
            return ChatResponse.error("CHECK_STOCK",
                    "Please specify a product. e.g. 'check stock of paracetamol'");
        }

        // Search by name (partial match)
        Object products = productService.searchProducts(productName);
        return new ChatResponse("CHECK_STOCK",
                "Stock results for: " + productName, products);
    }

    private ChatResponse handleGenerateBill(Map<String, String> entities) {
        String idStr = entities.get("appointmentId");
        if (idStr == null) {
            return ChatResponse.error("GENERATE_BILL",
                    "Please specify an appointment id. e.g. 'generate bill for appointment 3'");
        }

        Long appointmentId = Long.parseLong(idStr);
        Object bill = billService.generateBill(appointmentId);
        return new ChatResponse("GENERATE_BILL",
                "Bill generated successfully.", bill);
    }

    private ChatResponse handleViewBill(Map<String, String> entities) {
        String idStr = entities.get("appointmentId");
        if (idStr == null) {
            return ChatResponse.error("VIEW_BILL",
                    "Please specify an appointment id. e.g. 'show bill for appointment 3'");
        }

        Long appointmentId = Long.parseLong(idStr);
        Object bill = billService.getBillByAppointmentId(appointmentId);
        return new ChatResponse("VIEW_BILL", "Bill for appointment " + appointmentId, bill);
    }
}