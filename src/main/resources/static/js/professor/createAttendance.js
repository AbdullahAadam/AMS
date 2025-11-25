$(document).ready(function () {
    // Fetch CSRF token
    $.get("/csrf", function(data) {
        window.csrfToken = data.token;
        window.csrfHeader = data.headerName;
        console.log("CSRF Token retrieved:", window.csrfToken);
    });

    // Menu item click event
    $(".menu-item").click(function(){
        var url = $(this).attr('data-href');
        window.location.href = url;
    });

    // Submenu toggle functionality
    $(".submenu").on("click", function() {
        let submenuIcon = $(this).find(".images");
        $(".submenu").not(this).removeClass("active").find(".images").attr("src", "/images/admin/arrow2.png");
        $(this).toggleClass("active");
        submenuIcon.attr("src", $(this).hasClass("active") ? "/images/admin/arrow1.png" : "/images/admin/arrow2.png");
    });

	/*let today = new Date();
	let last30Days = new Date();
	last30Days.setDate(today.getDate() - 30);
	let todayStr = today.toISOString().split('T')[0];
	let last30DaysStr = last30Days.toISOString().split('T')[0];
	$("#attDate").val(todayStr);
	$("#attDate").attr("min", last30DaysStr);
	$("#attDate").attr("max", todayStr);
	
	$("#attDate").on("change",function(){
		let selectedDate=new Date(this.value);
		if(selectedDate.getDate()==0){
			$(this).val("");
		}
	});*/
	
	/*
	let today = new Date();
	let last30Days = new Date();
	last30Days.setDate(today.getDate() - 30);
	const formatDate = (date) => date.toISOString().split('T')[0];


	let validDates = [];
	let current = new Date(last30Days);
	while (current <= today) {
		if (current.getDay() !== 0) { 
			validDates.push(formatDate(current));
		}
		current.setDate(current.getDate() + 1);
	}

	// On input, remove invalid (Sunday) dates
	$("#attDate").attr("min", formatDate(last30Days));
	$("#attDate").attr("max", formatDate(today));

	$("#attDate").on("input", function () {
		if (!validDates.includes(this.value)) {
			$(this).val(""); // Clear invalid date
		}
	});*/
	let today = new Date();
	let last30Days = new Date();
	last30Days.setDate(today.getDate() - 30);

	const formatDate = (date) => date.toISOString().split('T')[0];

	// AJAX call to get holiday data
	$.ajax({
	    url: "/professor/holidays/date-name", 
	    method: "GET",
	    success: function (holidays) {
	        // Convert to lookup map for easy access
	        const holidayMap = {};
	        const holidayDates = holidays.map(h => {
	            holidayMap[h.date] = h.name;
	            return h.date;
	        });
			console.log("HOlidays:",holidays);

	        flatpickr("#attDate", {
	            dateFormat: "Y-m-d",
	            minDate: formatDate(last30Days),
	            maxDate: formatDate(today),
	            disable: [
	                function (date) {
	                    const d = formatDate(date);
	                    return date.getDay() === 0 || holidayDates.includes(d); 
	                }
	            ],
	            onDayCreate: function (dObj, dStr, fp, dayElem) {
	                const date = dayElem.dateObj;
	                const formatted = formatDate(date);

	                if (holidayMap[formatted]) {
	                    // Add tooltip
	                    dayElem.setAttribute("title", holidayMap[formatted]);

	                    // Add highlight style
	                    dayElem.style.backgroundColor = "#ffeaa7";
	                    dayElem.style.borderRadius = "50%";
	                    dayElem.style.color = "#2d3436";
	                    dayElem.style.fontWeight = "bold";
	                }
	            },
	            onChange: function(selectedDates, dateStr, instance) {
	                $("#attDateError").text(""); 
	            }
	        });
	    },
	    error: function () {
	        console.error("Failed to load holiday data.");
	    }
	});


	
    // Function to validate the form
    function validateForm() {
        let isValid = true;

        // Clear previous errors
        $(".error-message").text("").hide();
        $("select, input").removeClass("error-border");

        // Function to show error
        function showError(element, errorId, message) {
            $(element).addClass("error-border");
            $(errorId).text(message).show();
            isValid = false;
        }

        // Validate inputs
        if ($("#department").val().trim() === "") showError("#department", "#departmentError", "Please select a department.");
        if ($("#year").val().trim() === "") showError("#year", "#yearError", "Please select a year.");
        if ($("#batch").val().trim() === "") showError("#batch", "#batchError", "Please select a batch.");
        if ($("#subject").val().trim() === "") showError("#subject", "#subjectError", "Please select a subject.");
        if ($("#attDate").val().trim() === "") showError("#attDate", "#attDateError", "Please select a date.");
        if ($("#hour").val().trim() === "") showError("#hour", "#hourError", "Please select an hour.");

        return isValid;
    }

    // Remove error message and red border when clicking/selecting an input
    $("select, input").on("input change", function () {
        $(this).removeClass("error-border");
        $(this).siblings(".error-message").text("").hide();
    });

    // Handle form submission
    $("#createAttendance").submit(function (event) {
        event.preventDefault(); // Prevent form submission
		if(!validateForm()){
		     return;
		}
		
		let attDate = new Date($("#attDate").val()).toISOString().split("T")[0];

		
		let checkData = {                
		    semNo: $("#year").val(),
		    batch: $("#batch").val(),
		    attDate: attDate,
		    period: $("#hour").val(),
			subCode:$("#subject").val(),			
		};
		//console.log("Datas: ",creatingData);
		console.log("CheckDatas: ",checkData);
		$.ajax({
		        url: "/professor/validate",
		        type: "POST",
		        contentType: "application/json",
				xhrFields:{
					withCredentials: true
				},
				headers: { [window.csrfHeader]: window.csrfToken },
		        data: JSON.stringify(checkData),
		        success: function (response) {
					console.log("response",response);
					if (response.slotTaken) {
					       if (response.canProceed) {
					           // Same subject - show edit warning
					           const msg = response.originalMarker 
					               ? `Editing attendance originally marked by ${response.originalMarker}`
					               : "Editing existing attendance";
								   console.log("message",msg);
					           //toastr.warning(msg);
					           proceedWithAttendanceCreation(true);
					       } else {
					           // Different subject - show conflict
					           toastr.error(`Time slot conflict with: ${response.existingSubjectName}`);
					           highlightConflictingFields();
					       }
					   } else {
					       // New attendance
					       proceedWithAttendanceCreation(false);
					   }

		        },
				error: function (xhr) {
				    console.error("AJAX Error:", xhr.status, xhr.responseText);
				    if (xhr.status === 409) {
				        toastr.warning(xhr.responseText+"dk"); // Warning from backend
				    } else if (xhr.status === 401) {
				        toastr.error("🔒 Unauthorized access. Please log in again.");
				    } else {
				        toastr.error("❌ Unable to verify attendance slot. Please try again.");
				    }
				}
		    });
      
    });
	function highlightConflictingFields() {
	    $('#hour').addClass('is-invalid');
	    $('#hourError').text('This time slot is already taken').show();
	    // Add similar highlighting for other relevant fields
	}
	
	function proceedWithAttendanceCreation() {
		var semnum="Semester "+$("#year").val();
		let attDate = new Date($("#attDate").val()).toISOString().split("T")[0];
		creatingData={
					deptId:$("#department").val(),
					semNo:semnum,
					batch:$("#batch").val(),
					subjectId:$("#subject").val(),
					markingDate:attDate,
					period:$("#hour").val(),
				};
				$.ajax({
						url:`/professor/students/${creatingData.deptId}/${creatingData.semNo}/${creatingData.batch}`,
						type:"GET",
						success:function(students){
							console.log("Fetched Students:", students);
							if (typeof students === "string") {
							           toastr.warning(students); // Show the error message from the backend
							           return;
							}
							let form = $('<form>', {
							    action: '/professor/mark-attendance',
							    method: 'POST'
							});
							form.append($('<input>', { type: 'hidden', name: '_csrf', value: window.csrfToken }));
							form.append($('<input>', { type: 'hidden', name: 'markingDate', value: creatingData.markingDate }));
							form.append($('<input>', { type: 'hidden', name: 'period', value: creatingData.period }));
							form.append($('<input>', { type: 'hidden', name: 'subjectId', value: creatingData.subjectId }));
							form.append($('<input>', { type: 'hidden', name: 'department', value: $("#department option:selected").text() }));
							form.append($('<input>', { type: 'hidden', name: 'semester', value: $("#year option:selected").text() }));
							form.append($('<input>', { type: 'hidden', name: 'batch', value: creatingData.batch }));
							// Convert students list to JSON and add it as a hidden field
							console.log("Sending students data:", JSON.stringify(students));
							form.append($('<input>', { type: 'hidden', name: 'students', value: JSON.stringify(students) }));
							
							

							$("body").append(form);
							console.log("Students Hidden Input Value:", $('input[name="students"]').val());

							form.submit();
						},
						error: function(xhr, status, error) {
						        // Check if the error is a 404 status
						    if (xhr.status === 404) {
						         toastr.warning(xhr.responseText); // This will display the message from the backend
						    } else {
						         toastr.error("Failed to fetch students. Please try again.");
						    }
						    console.log("Error fetching students:", error);
					}
					});
				
			}
	
	
	$.ajax({
	    url: '/professor/departments',
	    type: 'GET',
	    success: function (departments) {
		    var departmentDropdown = $('#department');
		    departmentDropdown.empty().append('<option value="">Select the Department</option>');
			$.each(departments, function (index, department) {
		        departmentDropdown.append('<option value="' + department.id + '">' + department.name + '</option>');
		     });
	    },
	    error: function () {
	         console.log("Failed to fetch departments");
	    }
	});
	

	    
	    $("#department").on("change",function () {
	        var deptId = $(this).val();
	        var yearDropdown = $("#year");
			var subjectDropdown = $("#subject");
			var hourDropdown = $("#hour");
			
	        yearDropdown.empty().append('<option value="">Select the Semester</option>');
			subjectDropdown.empty().append('<option value="">Select the Subject</option>');
			hourDropdown.empty().append('<option value="">Select the Hour</option>');
			
	        if (deptId) {
	            $.ajax({
	                url: '/professor/department/'+deptId, 
	                type: 'GET',
	                success: function (data) {
	                    var totalYears = data.sem;
						var totalPeriods=data.period;
	                    for (var i = 1; i <= totalYears; i++) {
							// Value: "FINAL" for last year, else year number
							 // Display: Roman numeral
							yearDropdown.append('<option value="' + i+ '">' + 'Semester('+i+')' + '</option>');
	                    }
						for (var j = 1; j <= totalPeriods; j++) {
						     hourDropdown.append('<option value="' + j + '">Hour:' + j + '</option>');
						}
	                },
	                error: function () {
	                    console.log("Failed to fetch department details");
	                }
	            });
	        }
	    });
		$("#hour").prop("disabled",true);
		$("#year").on("change",function () {
		       var deptId = $("#department").val();
		       var semNo = $(this).val();
		       var subjectDropdown = $("#subject");
			   var hourDropdown = $("#hour");
		       subjectDropdown.empty().append('<option value="">Select the Subject</option>');
			   hourDropdown.empty().append('<option value="">Select the Hour</option>').prop("disabled", true);
		       if (deptId && semNo) {
		           /*$.ajax({
		               url: '/professor/subjects/byYear?deptId=' + deptId + '&year=' + year,
		               type: 'GET',
		               success: function (subjects) {
		                   $.each(subjects, function (index, subject) {
		                       subjectDropdown.append('<option value="' + subject.subId + '">' + subject.name + '</option>');
		                   });
		               },
		               error: function () {
		                   console.log("Failed to fetch subjects");
		               }
		           });*/
				   $.ajax({
				              url: '/professor/subjects/' + semNo + '/' + deptId,  
				              type: 'GET',
				              success: function (data) {
				                  console.log("Data: ", data);
				                  
				                  if (data.length === 0) {
				                      $("#subjectError").text("No subjects available for the selected semester and department.").show();
									  subjectDropdown.prop("disabled", true);
								  } else {
				                      $("#subjectError").hide();
									  subjectDropdown.prop("disabled", false);
				                      $.each(data, function (index, subject) {
				                          subjectDropdown.append('<option value="' + subject.subId + '">' + subject.name + '</option>');
				                      });
				                  }
				              },
				              error: function (xhr) {
								try {
								         var response = JSON.parse(xhr.responseText);
								          $("#subjectError").text(response.error || "⚠️ Unexpected error occurred.").show();
								} catch (e) {
								          $("#subjectError").text("❌ Unable to load subjects. Please contact support.").show();
								}
								subjectDropdown.prop("disabled", true);
				              }
				          });
				  
		       }
		   });
		   $("#subject").on("change", function () {
			var subjectSelected = $(this).val();
			        var deptId = $("#department").val();
			        var hourDropdown = $("#hour");

			        if (!subjectSelected) {
			            $("#hourError").text("Select a subject first!").show();
			            hourDropdown.prop("disabled", true);
			        } else {
			            $("#hourError").hide();
			            hourDropdown.prop("disabled", false);

			            // Fetch and update hours dynamically if department is selected
			            if (deptId) {
			                $.ajax({
			                    url: "/professor/department/" + deptId,
			                    type: "GET",
			                    success: function (data) {
			                        var totalPeriods = data.period;
			                        hourDropdown.empty().append('<option value="">Select the Hour</option>');

			                        for (var j = 1; j <= totalPeriods; j++) {
			                            hourDropdown.append('<option value="' + j + '">Hour: ' + j + '</option>');
			                        }

			                        hourDropdown.prop("disabled", false); // Enable after updating
			                    },
			                    error: function () {
			                        console.log("Failed to fetch department details");
			                        hourDropdown.prop("disabled", true);
			                    }
			                });
			            }
			        }			    
		    });
});
