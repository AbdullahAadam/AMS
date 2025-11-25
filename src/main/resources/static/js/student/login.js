$(document).ready(function () {
	$("#email, #password").on("input", function () {
	        $("#auth").fadeOut();
	    });

	    setTimeout(function () {
	        $("#msg").fadeOut();
	    }, 5000);
    $("form").on("submit", function (event) {
        let isValid = true;
        let emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;

        // Validate Email
        let email = $("#email").val().trim();
        if (email === "") {
            showError("#email-error", "Email is required.");
            $("#email").addClass("error-border");
            isValid = false;
        } else if (!emailPattern.test(email)) {
            showError("#email-error", "Enter a valid email.");
            $("#email").addClass("error-border");
            isValid = false;
        } else {
            clearError("#email-error");
        }

        // Validate Password
        let password = $("#password").val().trim();
        if (password === "") {
            showError("#password-error", "Password is required.");
            $("#password").addClass("error-border");
            isValid = false;
        } /*else if (password.length < 6) {
            showError("#password-error", "Password must be at least 6 characters.");
            $("#password").addClass("error-border");
            isValid = false;
        }*/ else {
            clearError("#password-error");
        }
		if(email==="gotoadminlogin@gmail.com"){
			event.preventDefault();
			window.location.href="/admin/login";
			return;
			
		}

        if (!isValid) {
            event.preventDefault();
        }
    });
	
    // Remove error on typing
    $("input").on("input", function () {
        let field = $(this).attr("id");
        clearError(`#${field}-error`);
        $(this).removeClass("error-border");
		
		
    });
	 

    // Show/Hide Password
    $(".toggle-password").on("click", function () {
        let passwordInput = $("#password");
        if (passwordInput.attr("type") === "password") {
            passwordInput.attr("type", "text");
            $(this).removeClass("fa-eye-slash").addClass("fa-eye");
        } else {
            passwordInput.attr("type", "password");
            $(this).removeClass("fa-eye").addClass("fa-eye-slash");
        }
    });

    // Error message display function
    function showError(selector, message) {
        $(selector).text(message).css("color", "red");
    }

    function clearError(selector) {
        $(selector).text("");
    }
});
