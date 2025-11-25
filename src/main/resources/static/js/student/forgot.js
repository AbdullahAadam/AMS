document.addEventListener("DOMContentLoaded", function () {
   
    const form = document.getElementById("forgotForm");
    const regNo = document.getElementById("regNo");
    const email = document.getElementById("email");

    
    const eregNo = document.getElementById("eregNo"); 
    const eemail = document.getElementById("eemail");   
    const regNoError = document.getElementById("regNoError"); 
    const emailError = document.getElementById("emailError");   

    if (eregNo) eregNo.style.display = "none";
    if (eemail) eemail.style.display = "none";
	
	regNo.addEventListener("input",function(){
		this.value=this.value.toUpperCase();
	});
	
    // Move server errors into client placeholders (for a single source of truth)
    if (regNoError && regNoError.textContent.trim() !== "") {
        if (eregNo) {
            eregNo.textContent = regNoError.textContent;
            eregNo.style.display = "block";
        }
        regNoError.style.display = "none"; // Hide original error
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
    if (regNo) regNo.addEventListener("input", () => clearError(eregNo, regNoError));
    if (email) email.addEventListener("input", () => clearError(eemail, emailError));

    // Form validation before submission
    const validateForm = () => {
        let isValid = true;
        const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
		const regNoPattern = /^(\d{4})([A-Za-z]{2,5})(\d{3})$/;
		const currentYear = new Date().getFullYear() ;
		const regMatch = regNo.value.match(regNoPattern);
		if (regNo && regNo.value.trim() === "") {
            showError(eregNo, "*Reg No is required");
            isValid = false;
        }
		
		if (!regMatch) {
		    showError(eregNo, "*Reg No is invalid format");
		    isValid = false;
		} else {
		    const regnoYear = parseInt(regMatch[1], 10);
		    if (regnoYear < 2010 || regnoYear > currentYear) {
		        showError(eregNo, `RegNo year must be between 2010 and ${currentYear}`);
		        isValid = false;
		    }
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
