document.addEventListener("DOMContentLoaded", function () {
   
    const form = document.getElementById("forgotForm");
    const profId = document.getElementById("profId");
    const email = document.getElementById("email");

    
    const eprofId = document.getElementById("eprofId"); 
    const eemail = document.getElementById("eemail");   
    const profIdError = document.getElementById("profIdError"); 
    const emailError = document.getElementById("emailError");   

    if (eprofId) eprofId.style.display = "none";
    if (eemail) eemail.style.display = "none";

    // Move server errors into client placeholders (for a single source of truth)
    if (profIdError && profIdError.textContent.trim() !== "") {
        if (eprofId) {
            eprofId.textContent = profIdError.textContent;
            eprofId.style.display = "block";
        }
        profIdError.style.display = "none"; // Hide original error
    }

    if (emailError && emailError.textContent.trim() !== "") {
        if (eemail) {
            eemail.textContent = emailError.textContent;
            eemail.style.display = "block";
        }
        emailError.style.display = "none"; // Hide original error
    }

    // Function to show errors
    const showError = (element, message) => {
        if (!element) return;
        element.textContent = message;
        element.style.display = "block";
        element.previousElementSibling.style.borderColor = "red";
    };

    const clearError = (element, serverError) => {
        if (element) {
            element.textContent = "";
            element.style.display = "none";
        }
        if (serverError) {
            serverError.textContent = "";
            serverError.style.display = "none";
        }
        if (element && element.previousElementSibling) {
            element.previousElementSibling.style.borderColor = "#ddd";
        }
    };

    // Clear errors when user starts typing
    if (profId) profId.addEventListener("input", () => clearError(eprofId, profIdError));
    if (email) email.addEventListener("input", () => clearError(eemail, emailError));

    // Form validation before submission
    const validateForm = () => {
        let isValid = true;
        const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;

        if (profId && profId.value.trim() === "") {
            showError(eprofId, "*Professor ID is required");
            isValid = false;
        }

        if (email) {
            if (email.value.trim() === "") {
                showError(eemail, "*Email is required");
                isValid = false;
            } else if (!emailRegex.test(email.value)) {
                showError(eemail, "*Invalid email format");
                isValid = false;
            }
        }

        return isValid;
    };

    // Attach validation to form submission
    if (form) {
        form.addEventListener("submit", (event) => {
            if (!validateForm()) {
                event.preventDefault();
            }
        });
    }
});
