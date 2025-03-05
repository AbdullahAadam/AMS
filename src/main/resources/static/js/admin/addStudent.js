$(document).ready(function () {
    // Convert Student Registration Number to Uppercase
    $("#studentRegNo").on("input", function () {
        this.value = this.value.toUpperCase();
    });

    // Fetch Departments
    $.ajax({
        url: '/admin/listDepartments',
        type: 'GET',
        dataType: 'json',
        success: function (data) {
			console.log(data);
            if (data.length === 0) {
                $("#departmentError").text("No departments available.").show();
            } else {
                $.each(data, function (index, department) {
                    $('#department').append(
                        $('<option>', {
                            value: department.deptId,
                            text: department.deptName,
                            id: department.code
                        })
                    );
                });
            }
        },
        error: function (xhr, status, error) {
            console.log("Error fetching departments:", error);
        }
    });

    // Fetch Professors based on Department Selection
    $("#department").change(function () {
        var deptId = $(this).val();
        $("#professor").empty().append('<option value="">Select Professor</option>');

        if (deptId) {
            $.ajax({
                url: "/admin/professors/" + deptId,
                type: "GET",
                success: function (data) {
                    if (data.length === 0) {
                        $("#professorError").text("No professors available for this department.").show();
                    } else {
                        $.each(data, function (index, professor) {
                            $("#professor").append('<option value="' + professor.profId + '">' + professor.name + '</option>');
                        });

                        // Auto-select the only professor if only one is available
                        if (data.length === 1) {
                            $("#professor").val(data[0].profId);
                        }

                        $("#professorError").hide();
                    }
                },
                error: function (xhr, status, error) {
                    console.log("Error fetching professors:", error);
                }
            });
        }
    });

    
	function validateForm() {
	    let isValid = true;
	    let invalidField = null;
	    $(".error-message").hide();
	    $("input, select").removeClass("error-border");

	    
	    const studentRegNo = $("#studentRegNo").val().trim();
	    const regNoPattern = /^(\d{4})([A-Za-z]{2,5})(\d{3})$/;
	    const currentYear = new Date().getFullYear() + 1;

	    if (!studentRegNo || !regNoPattern.test(studentRegNo)) {
	        $("#studentRegNoError").text("Invalid RegNo").show();
	        $("#studentRegNo").addClass("error-border");
	        isValid = false;
	    } else {
	        const regnoYear = parseInt(studentRegNo.match(regNoPattern)[1], 10);
	        if (regnoYear < 2010 || regnoYear > currentYear) {
	            $("#studentRegNoError").text("RegNo year must be between 2010 and " + currentYear).show();
	            $("#studentRegNo").addClass("error-border");
	            isValid = false;
	        }
	    }

	    // Validate Department
	    const department = $("#department").val();
	    if (!department) {
	        $("#departmentError").text("Department is required.").show();
	        $("#department").addClass("error-border");
	        isValid = false;
	    }

	    const extractedDept = studentRegNo.match(regNoPattern)?.[2]?.toUpperCase();
	    let matchedDepartmentId = null;

	    $("#department option").each(function () {
	        const deptCode = $(this).attr('id');
	        if (deptCode && deptCode.toUpperCase() === extractedDept) {
	            matchedDepartmentId = $(this).val();
	            return false;
	        }
	    });

	    if (!matchedDepartmentId) {
	        $("#studentRegNoError").text("No matching department found.").show();
	        $("#studentRegNo").addClass("error-border");
	        isValid = false;
	    }
    
	    const professor = $("#professor").val();
	    if (!professor) {
	        if ($("#professor option").length > 1) {
	            $("#professorError").text("Professor is required.").show();
	            $("#professor").addClass("error-border");
	            isValid = false;
	        } else {
	            $("#professorError").text("No professors available for this department.").show();
	            $("#professor").addClass("error-border");
	            isValid = false;
	        }
	    }

	    return isValid;
	}

    // Auto Select 
	$("#studentRegNo").on('input', function () {
	    const studentRegNo = $(this).val().trim();
	    const regNoPattern = /^(\d{4})([A-Za-z]{2,5})(\d{3})$/;

	    if (regNoPattern.test(studentRegNo)) {
	        const extractedDept = studentRegNo.match(regNoPattern)[2].toUpperCase(); 
	        let matchedDepartmentId = null;

	        console.log("Extracted Dept:", extractedDept); 

	        $("#department option").each(function () {
	            const deptCode = $(this).attr('id'); 

	            if (deptCode) { 
	                console.log("Checking against:", deptCode.toUpperCase()); 

	                if (deptCode.toUpperCase() === extractedDept) {
	                    matchedDepartmentId = $(this).val();
	                    return false; 
	                }
	            }
	        });

	        if (matchedDepartmentId) {
	            $("#department").val(matchedDepartmentId).trigger("change");
	            $("#studentRegNoError").hide();
	            $("#studentRegNo").removeClass("error-border");
	        } else {
	            $("#studentRegNoError").text("No matching department found.").show();
	            $("#studentRegNo").addClass("error-border");
	        }
	    }
	});

    $("#studentRegNo, #studentName, #email").on("keydown", function () {
        $(".error-message").hide();
        $("input, select").removeClass("error-border");
    });

    $("#professor").change(function () {
        if ($(this).val()) {
            $("#professorError").hide();
            $(this).removeClass("error-border");
        }
    });

    $("#addStudentForm").submit(function (event) {
        event.preventDefault();
        if (!validateForm()) {
            return;
        }

        let studentData = {
            regNo: $("#studentRegNo").val().trim(),
            name: $("#studentName").val(),
            department: { deptId: $("#department").val() },
            mentor: {profId:$("#professor").val()},
            email: $("#email").val().trim(),
            LogStatus: "PENDING",
            StudentStatus: "ACTIVE",
			pwd:"",
			img:"",
			age:"",
			dob:"",
			phone:"",
			gender:"",
			address:"",
			joinDate:"",
			batch:"",
			currentYear:"",
			approvedBy:"",
        };
		console.log("datas: ",studentData);
		$.ajax({
			url:"/admin/addStudent",
			method:"POST",
			contentType:"application/json",
			xhrFields:{
				withCredentials:true
			},
			headers:{[window.csrfHeader]: window.csrfToken},
			data:JSON.stringify(studentData),
			success:function(response){
				console.log("Success: "+response);
				toastr.success(response);
				$("#addStudentForm").trigger("reset");
			},
			error:function(xhr,status,error){
				if(xhr.responseText.indexOf("already exists")!==-1){					
					var errorMessage=xhr.responseText;
					if(errorMessage.startsWith("Error:")){
						errorMessage=errorMessage.substring("Error:".length).trim();
					}
					toastr.warning(errorMessage);
					console.log(errorMessage);
					}else{
						toastr.error(xhr.responseText||"An unexpected error occur");
						console.log('Error: ' + status + ' - ' + error);
						console.log('Response: ' + xhr.responseText);
					}
			}
		});

       
    });
});




/*$(document).ready(function () {
    // Convert Student Registration Number to Uppercase
    $("#studentRegNo").on("input", function () {
        this.value = this.value.toUpperCase();
    });

    // Fetch Departments
    $.ajax({
        url: '/admin/listDepartments',
        type: 'GET',
        dataType: 'json',
        success: function (data) {
            $.each(data, function (index, department) {
                $('#department').append(
                    $('<option>', {
                        value: department.deptId,
                        text: department.deptName,
                        id: department.code
                    })
                );
            });
        },
        error: function (xhr, status, error) {
            console.log("Error fetching departments:", error);
        }
    });

    // Fetch Professors based on Department Selection
    $("#department").change(function () {
        var deptId = $(this).val();
        var selectedProfessor = $("#professor").val();
        $("#professor").empty().append('<option value="">Select Professor</option>');
        
        if (deptId) {
            $.ajax({
                url: "/admin/professors/" + deptId,
                type: "GET",
                success: function (data) {
                    if (data.length === 0) {
                        $("#professorError").text("No professors available for this department.").show();
                    } else {
                        $.each(data, function (index, professor) {
                            let selectedAttr = professor.profId === selectedProfessor ? "selected" : "";
                            $("#professor").append('<option value="' + professor.profId + '" ' + selectedAttr + '>' + professor.name + '</option>');
                        });
                        $("#professorError").hide();
                    }
                },
                error: function (xhr, status, error) {
                    console.log("Error fetching professors:", error);
                }
            });
        }
    });

    // Validate Form Before Submission
    function validateForm() {
        let isValid = true;
        let InvalidField = null;
        $(".error-message").hide();
        $("input, select").removeClass("error-border");

        const studentRegNo = $("#studentRegNo").val();
        const regNoPattern = /^(\d{4})([A-Za-z]{2,5})(\d{3})$/;
        const currentYear = new Date().getFullYear() + 1;

        if (!studentRegNo || !regNoPattern.test(studentRegNo)) {
            $("#studentRegNoError").text("Invalid RegNo").show();
            $("#studentRegNo").addClass("error-border");
            isValid = false;
        } else {
            const regnoYear = parseInt(studentRegNo.match(regNoPattern)[1], 10);
            if (regnoYear < 2010 || regnoYear > currentYear) {
                $("#studentRegNoError").text("RegNo year must be between 2010 and " + currentYear).show();
                $("#studentRegNo").addClass("error-border");
                isValid = false;
            }
        }

        // Validate Student Name
        const studentName = $("#studentName").val();
        if (!studentName || studentName.length < 3 || studentName.length > 50) {
            $("#studentNameError").text("Student Name must be between 3 to 50 characters.").show();
            $("#studentName").addClass("error-border");
            isValid = false;
        }

        // Validate Department Selection
        if (!$("#department").val()) {
            $("#departmentError").text("Department is required.").show();
            $("#department").addClass("error-border");
            isValid = false;
        }
		const department = $("#department").val();
		const departmentOptions = $("#department option").length;
		if (!department) {
			if (departmentOptions > 1) { 			 
				  $("#departmentError").text("Department is required.").show();
				  $("#department").addClass("error-border");
				  if (!InvalidField) InvalidField = $("#department");
				  isValid = false;
			} else {
				   // No professors available for the selected department
				   $("#departmentError").text("No department available .").show();
				   $("#department").addClass("error-border");
				   isValid = false;  // Prevent form submission
			}
		}

        // Validate Professor Selection
        if (!$("#professor").val() && $("#professor option").length > 1) {
            $("#professorError").text("Professor is required.").show();
            $("#professor").addClass("error-border");
            isValid = false;
        }
		const professor = $("#professor").val();
		const professorOptions = $("#professor option").length;

		if (!professor) {
		    if (professorOptions > 1) { 
		        // Professors are available but not selected
		        $("#professorError").text("Professor is required.").show();
		        $("#professor").addClass("error-border");
		        if (!InvalidField) InvalidField = $("#professor");
		        isValid = false;
		    } else {
		        // No professors available for the selected department
		        $("#professorError").text("No professors available for this department.").show();
		        $("#professor").addClass("error-border");
		        isValid = false;  // Prevent form submission
		    }
		}


        // Validate Email
        const email = $("#email").val().trim();
        const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!email || !emailPattern.test(email)) {
            $("#emailError").text("Invalid Email Format").show();
            $("#email").addClass("error-border");
            isValid = false;
        }

        return isValid;
    }

    // Auto Select Department Based on Student RegNo
    $("#studentRegNo").on('input', function () {
        const studentRegNo = $(this).val().trim();
        const regNoPattern = /^(\d{4})([A-Za-z]{3,5})(\d{3})$/;
        if (regNoPattern.test(studentRegNo)) {
            const extractedDept = studentRegNo.match(regNoPattern)[2];
            let matchedDepartmentId = null;
            $("#department option").each(function () {
                if ($(this).attr('id') === extractedDept) {
                    matchedDepartmentId = $(this).val();
                    return false;
                }
            });

            if (matchedDepartmentId) {
                $("#department").val(matchedDepartmentId).trigger("change");
                $("#studentRegNoError").hide();
                $("#studentRegNo").removeClass("error-border");
            } else {
                $("#studentRegNoError").text("No matching department found.").show();
                $("#studentRegNo").addClass("error-border");
            }
        }
    });

    // Clear Errors on Input
    $("#studentRegNo, #studentName, #email").on("keydown", function () {
        $(".error-message").hide();
        $("input, select").removeClass("error-border");
    });
    $("#professor").change(function () {
        if ($(this).val()) {
            $("#professorError").hide();
            $(this).removeClass("error-border");
        }
    });

    // Form Submission
    $("#addStudentForm").submit(function (event) {
        event.preventDefault();
        if (!validateForm()) {
            return;
        }

        let studentData = {
            regNo: $("#studentRegNo").val().trim(),
            name: $("#studentName").val(),
            department: { deptId: $("#department").val() },
            mentor: $("#professor").val(),
            email: $("#email").val().trim(),
            LogStatus: "PENDING",
            StudentStatus: "ACTIVE"
        };
        console.log("Submitted Data:", studentData);
    });
});*/
