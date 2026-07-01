package com.smartbiz.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Reads the currently authenticated user's details out of Spring
 * Security's SecurityContextHolder - the place JwtAuthFilter stored
 * them when it validated the incoming token.
 *
 * WHY a separate helper: AppointmentService and UserService both need
 * to know "who is calling this method right now" for ownership checks.
 * Rather than duplicating SecurityContextHolder.getContext()... calls
 * in both services, we centralize it here. Both services just inject
 * this helper and call getAuthenticatedUserId() / getAuthenticatedRole().
 *
 * Remember from JwtAuthFilter: we stored the userId as the
 * "principal" of the UsernamePasswordAuthenticationToken, and the
 * role as a SimpleGrantedAuthority prefixed with "ROLE_".
 */
@Component
public class AuthHelper {

    /**
     * Returns the userId embedded in the current request's JWT.
     * This is the database id of whoever is making this request
     * (StaffMember.id for STAFF, User.id for PATIENT).
     */
    public Long getAuthenticatedUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // We stored userId as the principal in JwtAuthFilter -
        // cast it back to Long here.
        return (Long) auth.getPrincipal();
    }

    /**
     * Returns "ROLE_STAFF" or "ROLE_PATIENT" for the current caller.
     * We read the first (and only) granted authority, which is exactly
     * what JwtAuthFilter set.
     */
    public String getAuthenticatedRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities().iterator().next().getAuthority();
    }

    public boolean isStaff() {
        return "ROLE_STAFF".equals(getAuthenticatedRole());
    }

    public boolean isPatient() {
        return "ROLE_PATIENT".equals(getAuthenticatedRole());
    }
}